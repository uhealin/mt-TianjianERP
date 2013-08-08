package com.matech.audit.service.businessReport;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.matech.audit.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.service.schedule.ScheduleService;
import com.matech.framework.service.schedule.model.ScheduleVO;
import com.matech.sms.SmsOpt;
import com.sun.org.apache.bcel.internal.generic.NEW;

public class BusinessReportSchedule   implements Job{

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Date date=new Date();
		List<BusinessReportVO> businessReport=new ArrayList<BusinessReportVO>();
		Connection conn=null;
		try {
			conn=new DBConnect().getConnect("");
			
			DbUtil dbUtil=new DbUtil(conn);
			businessReport=dbUtil.select(BusinessReportVO.class, "select * from k_business_report order by except_complete_time");
		    SimpleDateFormat smdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    System.out.println("预约时间控制--------------");
		    
		 
		    ScheduleService scheduleService=new ScheduleService(conn);
		    
		    if(!scheduleService.isExcutionTime(this.getClass()))return;
		    

		    SimpleDateFormat smdf1 = new SimpleDateFormat("yyyy-MM-dd");
		    
			for (BusinessReportVO businessReportVO : businessReport) {
				if(businessReportVO.getState().equals("复核通过")||businessReportVO.getExcept_complete_time()==null||businessReportVO.getCancelstate().equals("作废")||businessReportVO.getState().equals("不复核")){
					//System.out.println("复核通过或作废");
					continue;
				} 
				String dtoday=smdf1.format(new Date()).toString();
				if(!dtoday.equals(businessReportVO.getExcept_complete_time())){
					//System.out.println("不为到期日");
					continue;
				}  
				String datestring=businessReportVO.getExcept_complete_time()+" 08:30:00";
				if(date.after(smdf.parse(datestring))){
					String mobilephone="";
						try{ 
						String appointuserid=businessReportVO.getAppoint_human().toString();
						mobilephone=(String) dbUtil.get("k_user", "id", appointuserid,"mobilePhone");
						String applyuserid=businessReportVO.getApply_userid().toString();
						String applayuser=dbUtil.queryForString(
								" SELECT CONCAT(ar.name,':',dep.departname,':',a.Name) AS allanem FROM k_user a " +
								" LEFT JOIN k_department dep ON dep.autoid =a.departmentid " +
								" LEFT JOIN k_area ar ON ar.autoid=dep.areaid " +
								" WHERE a.id = "+applyuserid);
						applayuser=applayuser.replaceAll(":", "");
						//mobilephone="18684544119";

						System.out.println(mobilephone+"发送短信:"+applayuser+"预约的"+businessReportVO.getCompany_Name()+"报告将于今天到期，请妥善处理！");
						//SmsOpt.sendSm(mobilephone, applayuser+"预约的"+businessReportVO.getCompany_Name()+"报告将于今天到期，请妥善处理！");
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args) {
	    SimpleDateFormat smdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    try {
			Date curdate=smdf.parse(smdf.format(new Date()));
			System.out.println(curdate);
			Date date=new Date();
			System.out.println(date.after(smdf.parse("2012-12-29 23:12:12")));
			System.out.println(date.before(smdf.parse("2012-12-29 23:12:12")));
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    System.out.println("iris:sldkfj:oweiru".replace(":", ""));
	    
	    System.out.println();
	    System.out.println("===============================");
	    SimpleDateFormat smdf1 = new SimpleDateFormat("yyyy-MM-dd");
	    String d1=smdf1.format(new Date()).toString();
	    String d2="2012-12-31";
	    System.out.println(d2.equals(d1));
//	    BusinessReportSchedule.test();
	    String upper="A123".toUpperCase();
	    String lower="A123".toLowerCase();
	    System.out.println(upper+" : "+lower);
	}

}
