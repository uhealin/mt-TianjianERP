package com.matech.audit.service.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.auditPlaform.AuditPlaformService;
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.audit.service.userdef.UserdefService;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASDate;
import com.matech.framework.pub.util.ASFuntion;

public class AuditRemindTask implements Job {

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Connection conn = null ;
		PreparedStatement ps = null ;
		ResultSet rs = null ;
		try {
			ASFuntion CHF = new ASFuntion() ;
			conn = new DBConnect().getConnect("");
			String sql = "select projectid,projectname,state from z_project order by projectcreated desc" ;
			ps = conn.prepareStatement(sql) ;
			rs = ps.executeQuery() ;
			while(rs.next()) {
				String projectId = rs.getString(1) ;
				String projectName = rs.getString(2) ;
				int state = Math.abs(rs.getInt(3));
				//先得到当前日期
				String curDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) ;
				//得到项目各阶段的截止日期
				UserdefService us = new UserdefService(conn) ;
				String cdate = us.getValueByNameAndProperty("提交客户初稿时间",projectId) ;
				String fdate = us.getValueByNameAndProperty("提交一审时间",projectId) ;
				String sdate = us.getValueByNameAndProperty("提交二审时间",projectId) ;
				String tdate = us.getValueByNameAndProperty("提交三审时间",projectId) ;
				
				PlacardService placardService = new PlacardService(conn);
				PlacardTable placard = new PlacardTable();
				placard.setIsReversion(0);
				placard.setAddresser("19");
				placard.setAddresserTime(CHF.getCurrentDate()+" "+CHF.getCurrentTime());
				placard.setIsRead(0);
				placard.setCaption("项目逾期通知");
				AuditPlaformService aps = new AuditPlaformService(conn) ;
				String remindUser = aps.getAuditPeopleByState(projectId, state) ;
				String[] remindUserArr = remindUser.split(",") ;
				
				
				if(state <2) {
					//未提交一审
					if(!"".equals(fdate)) {
						//该项目设置了审计划中的最后提交一审时间
						int DifferDate =  ASDate.getDateNumber(fdate,curDate);  //与当前日期的差值
						if(DifferDate >= 3) {
							//相差了三天，提醒到负责人
							placard.setMatter("您负责的项目【"+projectName+"】已经相对计划提交一审时间逾期"+DifferDate+"天了!");
							for(int i=0;i<remindUserArr.length;i++) {
								placard.setAddressee(remindUserArr[i]);
								placardService.AddPlacard(placard);
							}
						}
					}
				}else if(state < 3) {
					//未提交二审
					if(!"".equals(fdate)) {
						//该项目设置了审计划中的最后提交二审时间
						int DifferDate =  ASDate.getDateNumber(sdate,curDate);  //与当前日期的差值
						if(DifferDate >= 3) {
							//相差了三天，提醒到负责人
							placard.setMatter("您负责的项目【"+projectName+"】已经相对计划提交二审时间逾期"+DifferDate+"天了!");
							for(int i=0;i<remindUserArr.length;i++) {
								//获得负责人的项目权限
								String projectIds = aps.getProjectIdsByUserId(remindUser) ;
								if(projectIds.indexOf(projectId)>0) {
									//拥有本项目的权限，发消息
									placard.setAddressee(remindUserArr[i]);
								//	placardService.AddPlacard(placard);
								}
							}
						}
					}
				}else if(state < 4) {
					//未提交三审
					if(!"".equals(fdate)) {
						//该项目设置了审计划中的最后提交三审时间
						int DifferDate =  ASDate.getDateNumber(tdate,curDate);  //与当前日期的差值
						if(DifferDate >= 3) {
							//相差了三天，提醒到负责人
							placard.setMatter("您负责的项目【"+projectName+"】已经相对计划提交三审时间逾期"+DifferDate+"天了!");
							for(int i=0;i<remindUserArr.length;i++) {
								//获得负责人的项目权限
								String projectIds = aps.getProjectIdsByUserId(remindUser) ;
								if(projectIds.indexOf(projectId)>0) {
									//拥有本项目的权限，发消息
									placard.setAddressee(remindUserArr[i]);
								//	placardService.AddPlacard(placard);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
	}

}
