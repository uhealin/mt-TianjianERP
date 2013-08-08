package com.matech.audit.service.auditPlaform;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.matech.audit.service.auditPeople.AuditPeopleService;
import com.matech.audit.service.auditPlaform.model.AuditConfig;
import com.matech.audit.service.auditPlaform.model.AuditHurry;
import com.matech.audit.service.auditPlaform.model.AuditStep;
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.task.model.Task;
import com.matech.audit.service.user.UserService;
import com.matech.audit.service.userdef.UserdefService;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;

/**
 * <p>Title: 审计人员</p>
 * <p>Description: 提供项目审核流程等操作</p>
 * <p>Copyright: Copyright (c) 2006, 2008 MaTech Corporation.All rights reserved. </p>
 * <p>Company: Matech  广州铭太信息科技有限公司</p>
 *
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有，
 * 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 *
 * 贡献者团队:
 *     铭太科技 - 研发中心，审计开发组
 * 
 * @author BILL
 * 2007-6-30
 */
public class AuditPlaformService {
	
	private Connection conn = null;

	/**
	 * 构造方法
	 * @param conn 数据库连接
	 * @param projectId 项目编号
	 * @throws Exception
	 */
	public AuditPlaformService(Connection conn) throws Exception {
		DbUtil.checkConn(conn);
		this.conn = conn;
	}
	
	public void saveStep(AuditStep auditStep) {
		PreparedStatement ps = null;
		
		try {
			String sql = "insert into `z_auditstep`(`projectId`,`curDealUser`,`preDealUser`,`submitAuditTime`,`taskArriveTime`,`preState`,`advice`,`property`,`backAuditTime`,`curState`) values "
					   + "(?,?,?,?, ?,?,?,?, ?,?)" ;
			ps = conn.prepareStatement(sql);
			ps.setString(1,auditStep.getProjectId());
			ps.setString(2,auditStep.getCurDealUser());
			ps.setString(3,auditStep.getPreDealUser());
			ps.setString(4,auditStep.getSubmitAuditTime());
			ps.setString(5,auditStep.getTaskArriveTime());
			ps.setString(6,auditStep.getPreState());
			ps.setString(7,auditStep.getAdvice());
			ps.setString(8,auditStep.getProperty());
			ps.setString(9,auditStep.getBackAuditTime());
			ps.setString(10,auditStep.getCurState()) ;
			ps.execute() ;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	public AuditStep getLastStep(String projectId) {
		PreparedStatement ps = null;
		ResultSet rs = null ;
		try {
			String sql = "select `projectId`,`curDealUser`,`preDealUser`,`submitAuditTime`,`taskArriveTime`,`preState`,`advice`,`property`,`backAuditTime`,`curState` from z_auditstep" 
					   + " where submitAuditTime=(select max(submitAuditTime) from z_auditstep where projectId=?) and projectId=?" ;
			ps = conn.prepareStatement(sql);
			ps.setString(1,projectId);
			ps.setString(2,projectId);
			
			rs = ps.executeQuery() ;
			AuditStep auditStep = new AuditStep() ;
			
			if(rs.next()) {
				auditStep.setProjectId(rs.getString(1)) ;
				auditStep.setCurDealUser(rs.getString(2)) ;
				auditStep.setPreDealUser(rs.getString(3)) ;
				auditStep.setSubmitAuditTime(rs.getString(4)) ;
				auditStep.setTaskArriveTime(rs.getString(5)) ;
				auditStep.setPreState(rs.getString(6));
				auditStep.setAdvice(rs.getString(7)) ;
				auditStep.setProperty(rs.getString(8)) ;
				auditStep.setBackAuditTime(rs.getString(9)) ;
				auditStep.setCurState(rs.getString(10)) ;
			}
			return auditStep ;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		return null ;
	}
	
	public void sendMessageToAuditPeople(String projectId, String userId,
			String sendUserId, int state) {

		try {

			ASFuntion CHF = new ASFuntion();
			PlacardTable pt = new PlacardTable();
			PlacardService pls = new PlacardService(conn);

			pt.setAddresser(sendUserId);
			String userName = new UserService(conn).getUser(sendUserId, "id")
					.getName();
			pt.setAddresserTime(CHF.getCurrentDate() + " "
					+ CHF.getCurrentTime());
			pt.setCaption("您有最新的"+state+"审分工!");
			
			if("4".equals(state)) {
				pt.setCaption("您有最新的签发分工!");
			}
			
			String projectName = new ProjectService(conn).getProjectById(
					projectId).getProjectName();

			String Matter = "["
					+ userName
					+ "]已经将项目["
					+ projectName
					+ "]交由您复核<br> <a href=\"../flex/ctrl.jsp\">登陆该项目</a>";

			pt.setMatter(Matter);
			pt.setIsRead(0);
			pt.setIsReversion(0);
			pt.setIsNotReversion(0);
			pt.setAddressee(userId);
			pls.AddPlacard(pt);

		} catch (Exception e) {
			Debug.print(Debug.iError, "发送消息给相关人员出错", e);
		}
	}
	
	public void sendMessageToBackAuditPeople(String projectId, String userId,
			String sendUserId, String state) {

		try {

			ASFuntion CHF = new ASFuntion();
			PlacardTable pt = new PlacardTable();
			PlacardService pls = new PlacardService(conn);
			ProjectService ps = new ProjectService(conn) ;
			String projectName =ps.getProjectById(projectId).getProjectName() ;
			
			pt.setAddresser(sendUserId);
			String userName = new UserService(conn).getUser(sendUserId, "id").getName();
			pt.setAddresserTime(CHF.getCurrentDate() + " "+ CHF.getCurrentTime());
			pt.setCaption("项目审核退回消息");

			String Matter = "["
					+ userName
					+ "]已经将项目["
					+ projectName
					+ "]退回给您复核<br> <a href=\"../flex/ctrl.jsp\">登陆该项目</a>";

			pt.setMatter(Matter);
			pt.setIsRead(0);
			pt.setIsReversion(0);
			pt.setIsNotReversion(0);
			pt.setAddressee(userId);
			pls.AddPlacard(pt);

		} catch (Exception e) {
			Debug.print(Debug.iError, "发送消息给相关人员出错", e);
		}
	}
	
	//所有底稿最后修改时间
	public Task getLastUpdateTaskTime(String projectId) {
		PreparedStatement ps = null;
		ResultSet rs = null ;
		try {
			String sql = "select TaskCode,TaskName,udate,userName from z_task where projectId = ? " 
					   + " and udate=(select max(udate) from z_task where projectId=?)" ;
			ps = conn.prepareStatement(sql);
			ps.setString(1,projectId);
			ps.setString(2,projectId);
			
			rs = ps.executeQuery() ;
			Task task = new Task();
			if(rs.next()) {
				task.setTaskCode(rs.getString("TaskCode")) ;
				task.setTaskName(rs.getString("taskName")) ;
				task.setUdate(rs.getString("udate")) ;
				task.setUserName(rs.getString("userName")) ;
			}
			return task ;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		return null ;
	}
	
	//实质性底稿最后修改时间
	public Task getLastUpdateRealTaskTime(String projectId) {
		PreparedStatement ps = null;
		ResultSet rs = null ;
		try {
			String sql = "select TaskCode,TaskName,udate,userName from z_task where subjectName <> '' and subjectName is not null and projectId = ?" 
					   + " and udate=(select max(udate) from z_task where subjectName <> '' and subjectName is not null and projectId = ?)" ;
			ps = conn.prepareStatement(sql);
			ps.setString(1,projectId);
			ps.setString(2,projectId);
			
			rs = ps.executeQuery() ;
			Task task = new Task();
			if(rs.next()) {
				task.setTaskCode(rs.getString("TaskCode")) ;
				task.setTaskName(rs.getString("taskName")) ;
				task.setUdate(rs.getString("udate")) ;
				task.setUserName(rs.getString("userName")) ;
			}
			return task ;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		return null ;
	}
	
	public void saveHurry(AuditHurry auditHurry) {
		PreparedStatement ps = null;
		
		try {
			String sql = "insert into `z_audithurry`(`projectId`,`reserveTime`,`auditTime`,`reason`,`updateDate`,`userName`,`property`) values "
					   + "(?,?,?,?, ?,?,?)" ;
			ps = conn.prepareStatement(sql);
			ps.setString(1,auditHurry.getProjectId());
			ps.setString(2,auditHurry.getReserveTime());
			ps.setString(3,auditHurry.getAuditTime());
			ps.setString(4,auditHurry.getReason());
			ps.setString(5,auditHurry.getUpdateDate());
			ps.setString(6,auditHurry.getUserName());
			ps.setString(7,auditHurry.getProperty());
			
			ps.execute() ;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	
	public AuditHurry getHurry(String projectId) {
		PreparedStatement ps = null;
		
		try {
			String sql = "select `projectId`,`reserveTime`,`auditTime`,`reason`,`updateDate`,`userName`,`property` from z_audithurry "
					   + " where projectId = ?" ;
			ps = conn.prepareStatement(sql);
			
			ps.setString(1,projectId) ;
			AuditHurry auditHurry = new AuditHurry() ;
			ResultSet rs = ps.executeQuery() ;
			ASFuntion CHF = new ASFuntion();
			if(rs.next()) {
				auditHurry.setProjectId(CHF.showNull(rs.getString(1))) ;
				auditHurry.setReserveTime(CHF.showNull(rs.getString(2))) ;
				auditHurry.setAuditTime(CHF.showNull(rs.getString(3)));
				auditHurry.setReason(CHF.showNull(rs.getString(4)));
				auditHurry.setUpdateDate(CHF.showNull(rs.getString(5)));
				auditHurry.setUserName(CHF.showNull(rs.getString(6)));
				auditHurry.setProperty(CHF.showNull(rs.getString(7)));
				
				return auditHurry ;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
			return null ;
	}
	
	
	//更新底稿送达时间
	public Task updateTaskArriveTime(String projectId) {
		PreparedStatement ps = null;
		ResultSet rs = null ;
		try {
			String sql = "select max(submitAuditTime) from z_auditstep where projectId = ?" ;
			
			ps = conn.prepareStatement(sql) ;
			ps.setString(1,projectId) ;
			rs = ps.executeQuery() ;
			String maxSubmitTime = "" ;
			if(rs.next()) {
				maxSubmitTime = rs.getString(1) ;
			}
			
			sql = "update z_auditstep set taskArriveTime = now() "
					   +" where projectId=? and submitAuditTime =?" ;
			
			ps = conn.prepareStatement(sql);
			ps.setString(1,projectId);
			ps.setString(2,maxSubmitTime);
			
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		return null ;
	}
	
	public boolean hasAuditPopem(String userid, String projectid,String state)
	throws MatechException {
		DbUtil.checkConn(conn);
		
		String sql = "";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSet rs2 = null ;
		try {
			
			sql = "select 1 from z_auditPeople where Role=? and projectId=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, state+"审人");
			ps.setString(2, projectid);
			rs = ps.executeQuery();
			if (rs.next()) {
				//如果项目已经指定了一个复核人，就看当前人是不是这个人
				sql = "select 1 from z_auditPeople where Role=? and projectId=? and userId=?";
				ps = conn.prepareStatement(sql);
				ps.setString(1, state+"审人");
				ps.setString(2, projectid);
				ps.setString(3,userid) ;
				
				rs2 = ps.executeQuery() ;
				
				if(rs2.next()) {
					return true ;
				}else {
					return false ;
				}
			}else {
				//没有复核人，就看当前人有无复核权限
				UserdefService uds = new UserdefService(conn);
				AuditPeopleService aps = new AuditPeopleService(conn, projectid);
				if ("1".equals(state) || "-1".equals(state)) {
					if (aps.hasAudit(userid)) {
						return true;
					} else { 
						return false;
					}
				} else if ("2".equals(state) || "-2".equals(state)) {
					if (uds.hasAuthority2(userid, projectid)) {
						return true;
					} else {
						return false;
					}
				} else if ("3".equals(state) || "-3".equals(state)) {
					if (uds.hasAuthority3(userid, projectid)) {
						return true;
					} else {
						return false;
					}
				}else if("4".equals(state)){
					if (uds.hasAuthority4(userid, projectid)) {
						return true;
					} else { 
						return false;
					}
				}
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return false ;
	}

	//判断流程结束没有
	public boolean hasNextState(String state) {
		PreparedStatement ps = null;
		ResultSet rs = null ;
		try {
			
			String sql = "select 1 from k_auditconfig where id=?" ;
			
			ps = conn.prepareStatement(sql);
			ps.setInt(1,Integer.parseInt(state)+1);
			
			rs = ps.executeQuery() ;
			if(rs.next()) {
				return true ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		return false ;
	}
	
	public String getConfigStepName(String state) {
		PreparedStatement ps = null;
		ResultSet rs = null ;
		try {
			
			String sql = "select stepname from k_auditconfig where id=?" ;
			
			ps = conn.prepareStatement(sql);
			ps.setString(1,state);
			
			rs = ps.executeQuery() ;
			if(rs.next()) {
				return rs.getString(1) ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		return "" ;
	}
	
	public List<AuditConfig> getConfigList() {
		PreparedStatement ps = null;
		ResultSet rs = null ;
		List<AuditConfig> list = new ArrayList<AuditConfig>() ;
		try {
			
			String sql = "select id,stepname,cname,readonly,property from k_auditconfig where id>0 and id<7" ;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery() ;
			while(rs.next()) {
				AuditConfig ac = new AuditConfig() ;
				ac.setId(rs.getInt(1)) ;
				ac.setStepname(rs.getString(2)) ;
				ac.setCname(rs.getString(3)) ;
				ac.setReadonly(rs.getString(4)) ;
				ac.setProperty(rs.getString(5)) ;
				list.add(ac) ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		return list ;
	}
	
	public String getProjectIdsByUserId(String userId) {
		String projectIds = "-1";
		String departmentIds = "-1";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// 找出用户的所有项目
			String sql = " select ifnull(group_concat(projectid),-1) "
					+ " from z_auditpeople " + " where userid=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			rs = ps.executeQuery();
	
			if (rs.next()) {
				projectIds = rs.getString(1);
			}
	
			// 找出项目权限
			sql = " select ifnull(group_concat(a.autoid),-1) from "
					+ " k_department a,k_user b "
					+ " where b.ProjectPopedom like concat('%.',a.autoid,'.%') "
					+ "	and b.id=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			rs = ps.executeQuery();
	
			if (rs.next()) {
				departmentIds = rs.getString(1);
			}
	
			// 找出部门项目编号
			sql = " select ifnull(group_concat(b.ProjectId),-1) from k_user a,z_auditpeople b "
					+ " where a.id = b.userid "
					+ " and a.departmentid in ("
					+ departmentIds + ") ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
	
			if (rs.next()) {
				projectIds += "," + rs.getString(1);
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return projectIds ;
	}
	
	public String getAuditUserId(int state) {
		PreparedStatement ps = null;
		ResultSet rs = null ;
		try {
			
			String sql = "select group_concat(distinct userid) from k_userrole where rid in ( "
				   + "select svalue from s_config "
				   + "where sname = ?)";
			ps = conn.prepareStatement(sql);
			if (state == 2) {
				ps.setString(1,"二审人角色") ;
			} else if (state == 3) {
				ps.setString(1,"三审人角色") ;
			}
			rs = ps.executeQuery() ;
			if(rs.next()) {
				return rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		return "" ;
	}
	
	public String getCopartner() {
		PreparedStatement ps = null;
		ResultSet rs = null ;
		try {
			String sql = "select group_concat(distinct userid) from k_userrole "
				   + " where rid in (select id from k_role where rolename='合伙人')" ;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery() ;
			if(rs.next()) {
				return rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		return "" ;
	}
	
	public String getAuditPeopleByAuditPlan(String projectId,int state) {
		PreparedStatement ps = null;
		ResultSet rs = null ;
		try {
			String user = "user5" ;
			if(state == 1) {
				user = "user5" ;
			}else if(state == 2) {
				user = "user2" ;
			}else if(state == 3) {
				user = "user3" ;
			}else if(state == 4) {
				user = "user4" ;
			}
			
			String sql = "select group_concat(distinct "+user+") from z_auditplan where projectid=? and "+user+"<>''" ;
			ps = conn.prepareStatement(sql);
			ps.setString(1,projectId) ;
			rs = ps.executeQuery();
			if(rs.next()) {
				return rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		return "" ;
	}
	
	public String getAuditPeopleByState(String projectId,int state){
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			sql = "select userId from z_auditPeople where Role=? and projectId=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, state+"审人");
			ps.setString(2, projectId);
			rs = ps.executeQuery();
			if (rs.next()) {
				//项目已经指定了一个复核人
				return rs.getString(1);
			}else {
				//没有指定复核人，就找有权复核的人
				if (state == 1) {
					String strSql = "select group_concat(DISTINCT(userid))"
						+ " from asdb.z_auditpeople "
						+ " where isAudit = '1' "
						+ " and projectId = ? ";
					ps = conn.prepareStatement(strSql) ;
					ps.setString(1,projectId);
					rs = ps.executeQuery() ;
					if(rs.next()) {
						return rs.getString(1) ;
					}
				} else if (state == 2) {
					return getAuditUserId(2) ;
				} else if (state == 3) {
					return getAuditUserId(3) ;
				}
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return "" ;
	}
	
	//获取当前时间的前3天 当天和后3天待排班2审项目
	//返回格式为 MM-dd <br><br> 项目数
	public List getDate(String dateString,String userPopem){
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Map> dayList = new ArrayList<Map>() ;
		try {
			  DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd E");
			  /*
			  Date inputDate = dateFormat.parse(dateString);
			  
			  Calendar cal = Calendar.getInstance();
			  cal.setTime(inputDate);
			  int inputDayOfYear = cal.get(Calendar.DAY_OF_YEAR);
			  for(int i=3;i>=-3;i--){
				  Map<String,String> map = new HashMap<String,String>() ;
				  cal.set(Calendar.DAY_OF_YEAR ,inputDayOfYear-i);
				  String date = dateFormat.format(cal.getTime()) ;
				  String projectCount =  getAppProjectCount(date.substring(0,10));
				  map.put("date", date) ;
				  map.put("projectCount", projectCount) ;
				  
				  dayList.add(map);
			  }
			 */ 
			  
			  for(int i=-3;i<=3;i++){
				  Map<String,String> map = new HashMap<String,String>() ;
				  long date =  dateFormat.parse(dateString).getTime()+i*3600*1000*24 ;
				  String tempDate = dateFormat.format(date) ;
				  String projectCount =  getAppProjectCount(tempDate.substring(0,10),userPopem);
				  map.put("date", tempDate) ;
				  map.put("projectCount", projectCount) ;
				  
				  dayList.add(map);
			  }
		} catch (Exception e) {
			e.printStackTrace();
			Debug.print(Debug.iError, "访问失败", e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return dayList ;
	}
	
	public String getAppProjectCount(String date,String userPopem){
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String curDate = formatter.format(new Date());
			
			String sbSql = getDealingSql() ; // 这是已经送底稿进来的,但还在处理的
			
			String sqlTemp = "" ;
			String todayFinishCount = "" ;  // 
			
			if(curDate.equals(date) || "".equals(date)) {
				//是今天
				String sbSql2 = getFinishSql(curDate) ; // 这是已经完成的
				sqlTemp = sbSql + " union \n " + sbSql2 
					    + " and b.secondAuditDate='"+ curDate +"'" ;
				
				String todaySql = "select count(*) from ("+ sbSql2 + " ) a " ;
				
				if(userPopem!= null && !"".equals(userPopem)) {
					todaySql += " LEFT JOIN( \n "
							 + " 	SELECT a.userid,b.name,a.ProjectID FROM z_auditpeople a,k_user b WHERE a.userid = b.id AND a.role = '二审负责人' \n"
							 + " ) c ON a.ProjectID = c.projectid \n" 
							 + " left join z_projectext e on a.projectid = e.projectid "
							 + " where 1=1 " + userPopem + " \n"  ;
				}
				
				DbUtil dbUtil = new DbUtil(conn) ;
				int todayCount = dbUtil.queryForInt(todaySql) ;
				todayFinishCount = todayCount+"/";
			}else {
				//计算差值
				long day = formatter.parse(date).getTime() - (new Date().getTime()) ;
				if(day < 0) {
					//今天之前
					sqlTemp = getFinishSql(date) ;  ;
				}else {
					//今天之后的
					sqlTemp = getAppSql(date) ; ;
				}
				//sqlWhere += " and a.secondAuditDate ='"+date+"'" ;
			}
			
			sql = "select count(*) from (" + sqlTemp + " ) a ";
			if(userPopem!= null && !"".equals(userPopem)) {
				  
				sql += " LEFT JOIN( \n "
				 	+ " 		SELECT a.userid,b.name,a.ProjectID FROM z_auditpeople a,k_user b WHERE a.userid = b.id AND a.role = '二审负责人' \n"
				 	+ " ) c ON a.ProjectID = c.projectid \n" 
				 	 + " left join z_projectext e on a.projectid = e.projectid "
					 + " where 1=1 " + userPopem + " \n"  ;
			}
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				return todayFinishCount+rs.getString(1);
			}
		} catch (Exception e) { 
			Debug.print(Debug.iError, "访问失败", e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return "" ;
	}
	
	public void updateAppointment(String processInstanseId,String secondAuditUser) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try{
			String sql = "update j_auditappointment set secondAuditUser=? where processInstanseId=?";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1,secondAuditUser) ;
			ps.setString(2,processInstanseId) ;
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps) ;
		}
	}
	
	public void updateAppOperate(String processInstanseId) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try{
			String sql = "update z_projectext set s6=? where projectId=?";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1,"是") ;
			ps.setString(2,processInstanseId) ;
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps) ;
		}
	}
	
	public void updateProjectext(String projectid,String column,String value) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try{
			String sql = "update z_projectext set "+column+"=? where projectId=?";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1,value) ;
			ps.setString(2,projectid) ;
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps) ;
		}
	}
	
	public String getProjectext(String projectid,String column) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null ;
		try{
			String sql = "select  "+column+" from z_projectext where projectId=?";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1,projectid) ;
			rs = ps.executeQuery();
			if(rs.next()) {
				return rs.getString(1) ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps) ;
		}
		return "" ;
	}
	
	public boolean receiveNumAppCheck(String projectIds) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null ;
		boolean isAppCheck = true ;
		ASFuntion CHF = new ASFuntion() ;
		try{
			/*
			String sql = " SELECT a.projectid,c.state FROM z_project a "
					   + " LEFT JOIN z_projectext b ON a.projectid = b.projectid "
					   + " LEFT JOIN j_auditappointment c ON CONCAT(',',c.projectid,',') LIKE CONCAT('%,',a.projectid,',%') "
					   + " WHERE b.s1 = '重大' and a.projectid in("+projectIds+") "  ;
			*/
			String sql = "select group_concat(projectid) from z_projectext where projectid in("+projectIds+") and s1 = '重大'" ;
			ps = conn.prepareStatement(sql) ;
			rs = ps.executeQuery() ;
			if(rs.next()) {
				projectIds = CHF.showNull(rs.getString(1)) ;
			}
			
			if(!"".equals(projectIds)) {
				String[] pIdArr = projectIds.split(",") ;
				for(int i=0;i<pIdArr.length;i++) {
					boolean isPass = false ;
					sql = "SELECT state FROM j_auditappointment WHERE CONCAT(',',projectid,',') LIKE CONCAT('%,"+pIdArr[i]+",%')" ;
					ps = conn.prepareStatement(sql) ;
					rs = ps.executeQuery() ;
					while(rs.next()) {
						if("审核通过".equals(rs.getString(1))) {
							isPass = true ;
						}
					}
					
					isAppCheck = isPass ;
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps) ;
		}
		return isAppCheck ;
	}
	
	public boolean receiveNumDateCheck(String projectIds) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null ;
		boolean isDateCheck = true ;
		try{
			
			String sql = " SELECT a.projectId,b.s4 as secondAuditDate "
					   + " FROM z_project a "
					   + " LEFT JOIN z_projectext b ON a.projectid = b.projectid "
					   + " WHERE b.s1 = '重大'  "
					   + " AND (TO_DAYS(b.s4) <> TO_DAYS(NOW()) and TO_DAYS(b.s4)-1 <> TO_DAYS(NOW())) "
					   + " and a.projectid in("+projectIds+")" ;
			ps = conn.prepareStatement(sql) ;
			rs = ps.executeQuery() ;
			if(rs.next()) {
				isDateCheck = false ;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps) ;
		}
		return isDateCheck ;
	}
	
	//一审操作，z_auditpeople role = '部门一审'，appointdate > '' 有就是完成
	public boolean receiveNumAppointCheck(String projectIds) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null ;
		boolean isAppointCheck = true ;
		try{
			
			String sql = "select a.* from z_project a " +
			"	left join ( " +
			"		select * from z_auditpeople where role = '部门一审' and projectid in("+projectIds+") " +
			"	) b on a.projectid = b.projectid " +
			"	where a.projectid in("+projectIds+") " +
			"	and ifnull(appointdate ,'') = ''" ;
			ps = conn.prepareStatement(sql) ;
			rs = ps.executeQuery() ;
			if(rs.next()) {
				isAppointCheck = false ;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps) ;
		}
		return isAppointCheck ;
		
	}
	
	//工时申报数必须大于0。oa_timesreport projectid = xx 完成
	public boolean receiveNumReportCheck(String projectIds) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null ;
		boolean isReportCheck = true ;
		try{
			
			String sql = "select a.* from z_project a " +
			"	left join ( " +
			"		select * from oa_timesreport where projectid in("+projectIds+") " +
			"	) b on a.projectid = b.projectid " +
			"	where a.projectid in("+projectIds+") " +
			"	and b.projectid is null " ;
			ps = conn.prepareStatement(sql) ;
			rs = ps.executeQuery() ;
			if(rs.next()) {
				isReportCheck = false ;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps) ;
		}
		return isReportCheck ;
	}
	
	//客户资料中的公司性质必须指定。
	public boolean receiveNumCompanyCheck(String projectIds) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null ;
		boolean isCompanyCheck = true ;
		try{
			
			String sql = "select a.* from k_customer a,z_project b " +
			"	where projectid in("+projectIds+")  " +
			"	and a.DepartID = b.customerid " +
			"	and ifnull(a.companyProperty ,'')=''" ;
			ps = conn.prepareStatement(sql) ;
			rs = ps.executeQuery() ;
			if(rs.next()) {
				isCompanyCheck = false ;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps) ;
		}
		return isCompanyCheck ;
	}
	
	
	public static String getDealingSql() {
		// 这是已经送底稿进来的,但还在处理的
		
		StringBuffer sbSql = new StringBuffer() ;
		
		sbSql.append(" SELECT a.projectid,a.auditpara,a.projectname,a.state,'' as secondAuditDate,b.property,a.CustomerId,a.departmentid FROM z_project a \n") ;
		sbSql.append(" inner join ( \n") ;
		sbSql.append(" 	SELECT taskArriveTime,property,a.projectid \n") ;
		sbSql.append(" 	FROM z_auditstep a \n") ;
		sbSql.append(" 	INNER JOIN ( \n") ;
		sbSql.append(" 		SELECT MAX(autoid) AS autoid,projectid FROM z_auditstep a \n") ;
		sbSql.append(" 		WHERE taskArriveTime > '' GROUP BY projectid \n") ;
		sbSql.append(" 	) b  ON a.autoid = b.autoid \n") ;
		sbSql.append(" ) b ON a.ProjectID = b.projectid \n") ;
		sbSql.append(" WHERE ((b.property > '' AND state<4) or state=4) \n") ;
		
		return sbSql.toString() ;
	}
	
	public static String getFinishSql(String date) {
		// 这是已经完成的
		
		StringBuffer sbSql2 = new StringBuffer() ;
		
		sbSql2.append(" SELECT a.projectid,a.auditpara,a.projectname,a.state,secondAuditDate, \n") ;
		sbSql2.append(" c.property,a.CustomerId,a.departmentid  FROM z_project a \n") ;
		sbSql2.append(" inner JOIN ( \n") ;
		sbSql2.append(" 	SELECT substr(submitAuditTime,1,10) as secondAuditDate,a.projectid,a.curstate \n") ;
		sbSql2.append(" 	FROM z_auditstep a \n") ;
		sbSql2.append(" 	INNER JOIN ( \n") ;
		sbSql2.append(" 		SELECT MAX(autoid) AS autoid,projectid  FROM z_auditstep a \n") ;
		sbSql2.append(" 		WHERE submitAuditTime > '' GROUP BY projectid \n") ;
		sbSql2.append(" 	) b  ON a.autoid = b.autoid \n") ;
		sbSql2.append(" ) b ON a.ProjectID = b.projectid  \n") ;
		if(!"".equals(date)) {
			sbSql2.append("AND secondAuditDate ='"+ date +"' ") ;
		}
		sbSql2.append(" LEFT JOIN ( \n") ;
		sbSql2.append(" 	SELECT taskArriveTime,property,a.projectid \n") ;
		sbSql2.append(" 	FROM z_auditstep a \n") ;
		sbSql2.append(" 	INNER JOIN ( \n") ;
		sbSql2.append(" 		SELECT MAX(autoid) AS autoid,projectid  FROM z_auditstep a\n") ;
		sbSql2.append(" 		WHERE (a.property IS NOT NULL AND a.property <> '') GROUP BY projectid \n") ;
		sbSql2.append(" 	) b  ON a.autoid = b.autoid \n") ;
		sbSql2.append(" ) c ON a.ProjectID = c.projectid\n") ;
		sbSql2.append(" WHERE b.curstate>4 \n") ;
		
		return sbSql2.toString() ;
	}
	
	public static String getAppSql(String date) {
		//这些是预约了的 还没送进来的
		
		StringBuffer sbSql3 = new StringBuffer() ;
		
		sbSql3.append(" 	SELECT a.projectid,a.auditpara,a.projectname,a.state,c.property,a.CustomerId,a.departmentid, \n") ;
		sbSql3.append(" 	b.s4 AS secondAuditDate  \n") ;
		sbSql3.append(" 	FROM z_project a  \n") ;
		sbSql3.append(" 	INNER JOIN z_projectext b ON a.projectid = b.projectid AND b.s1 = '重大' \n") ;
		if(!"".equals(date)) {
			sbSql3.append(" AND b.s4 ='"+date+"' \n") ;
		}
		sbSql3.append(" 	LEFT JOIN ( \n") ;
		sbSql3.append(" 		SELECT taskArriveTime,property,a.projectid \n") ;
		sbSql3.append(" 		FROM z_auditstep a \n") ;
		sbSql3.append(" 		INNER JOIN ( \n") ;
		sbSql3.append(" 			SELECT MAX(autoid) AS autoid FROM z_auditstep a \n") ;
		sbSql3.append(" 			where taskArriveTime>'' GROUP BY projectid \n") ;
		sbSql3.append(" 		) b  ON a.autoid = b.autoid \n") ;
		sbSql3.append(" 	) c ON a.ProjectID = c.projectid \n") ;
		sbSql3.append(" 	WHERE (c.property IS NULL OR c.property = '') \n") ;
		sbSql3.append("  	AND state<5  \n");
		
		return sbSql3.toString() ;
	}
	
	/**
	 * 根据,号分隔的项目编号获得所有项目
	 * @param groupProjectId
	 * @return
	 * @throws Exception
	 */
	public List getProjectsByProjectIds(String projectIds) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		List<Map> projectList = new ArrayList<Map>() ;
		ASFuntion CHF = new ASFuntion() ;
		try {
			
			String sql = " SELECT a.projectname,c.s1 as special,c.s5 as reportCount,a.projectid,c.s4 as appDate  "
				       + " 	FROM z_project a "
			           + " 	LEFT JOIN z_projectext c ON a.projectid = c.projectid  "
				       + " 	where a.projectid in ("+projectIds+")  " ;
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				Map<String, String> map = new HashMap<String, String>() ;
				map.put("projectname",rs.getString(1)) ;
				map.put("special",rs.getString(2)) ;
				map.put("reportCount",CHF.showNull(rs.getString(3))) ;
				map.put("projectid",rs.getString(4)) ;
				map.put("appDate",CHF.showNull(rs.getString(5))) ;
				projectList.add(map) ;
			}		
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return projectList;
	}
	
	
	
	/**
	 * 根据部门编号获得部门所属分所
	 * @param departmentId
	 * @return
	 * @throws Exception
	 */
	public String getDepartByDepartmentId(String departmentId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String departid = "" ;
		try {
			
			String sql = " SELECT b.autoid \n"
					   + " FROM k_department a \n"
					   + " LEFT JOIN k_department b \n"
					   + " ON a.fullpath LIKE CONCAT(b.fullpath,'%') \n"
					   + " AND b.level0 = 1\n"
					   + " where a.autoid=? \n" ;
			
			ps = conn.prepareStatement(sql);
			ps.setString(1,departmentId) ;
			rs = ps.executeQuery();
			
			if(rs.next()) {
				departid = rs.getString(1) ;
			}		
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return departid;
	}
	
	
	public void updateAuditRight(String column,String auditpara,String departmentid,String value) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null ;
		try{
			String sql = "select 1 from k_auditRight where departmentid=? and auditpara=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1,departmentid) ;
			ps.setString(2,auditpara) ;
			rs = ps.executeQuery();
			
			if(rs.next()) {
				//update
				sql = "update k_auditRight set "+column+"=? where  departmentid=? and auditpara=? " ;
				ps = conn.prepareStatement(sql) ;
				ps.setString(1,value) ;
				ps.setString(2,departmentid) ;
				ps.setString(3,auditpara) ;
				ps.execute() ;
			}else {
				//insert 
				sql = "insert into k_auditRight(departmentid,auditpara,"+column+") values(?,?,?) " ;
				ps = conn.prepareStatement(sql) ;
				ps.setString(1,departmentid) ;
				ps.setString(2,auditpara) ;
				ps.setString(3,value) ;
				ps.execute() ;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps) ;
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		  DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd E");
	//	  Date inputDate = dateFormat.parse(new SimpleDateFormat("yyyy-MM-dd E").format(new Date()));
		  for(int i=3;i>=-3;i--){
			  long date =  dateFormat.parse("2011-01-01 星期四").getTime()+i*3600*1000*24 ;
			  System.out.println("#########"+date);
			  System.out.println(dateFormat.format(date));
		  }
		  
	}
	
}
