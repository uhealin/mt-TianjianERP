package com.matech.audit.work.leave;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.api.ProcessInstance;
import org.jbpm.api.TaskQuery;
import org.jbpm.api.task.Task;
import org.jbpm.pvm.internal.xml.Parse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.service.leave.LeaveService;
import com.matech.audit.service.leave.model.Leave;
import com.matech.audit.service.leave.model.LeaveFlow;
import com.matech.audit.service.leaveOffice.LeaveOfficeService;
import com.matech.audit.service.leaveType.LeaveTypeService;
import com.matech.audit.service.leaveType.model.LeaveType;
import com.matech.audit.service.oa.interiorEmail.InteriorEmailService;
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.audit.service.process.JbpmTemplate;
import com.matech.audit.service.process.ProcessFormService;
import com.matech.audit.service.process.model.ProcessForm;
import com.matech.audit.service.seal.SealService;
import com.matech.audit.service.seal.model.SealFlow;
import com.matech.audit.service.user.UserService;
import com.matech.audit.service.user.model.User;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class LeaveAction extends MultiActionController{
	
	private static final String LIST = "leave/list.jsp";
	private static final String ADDSKIP = "leave/AddandEdit.jsp";
	private static final String UPDATESKIP = "leave/AddandEdit.jsp";
	private static final String LEAVEAUDITLIST = "leave/leaveAuditList.jsp";//请假审批list
	private static final String AUDIT = "leave/audit.jsp";//审批 详情页面
	private static final String DESTROYAUDITLIST = "leave/destroyAuditList.jsp";//销假

	private JbpmTemplate jbpmTemplate;
	
	public JbpmTemplate getJbpmTemplate() {
		return jbpmTemplate;
	}

	public void setJbpmTemplate(JbpmTemplate jbpmTemplate) {
		this.jbpmTemplate = jbpmTemplate;
	}

   
	/**
	 * list
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(LIST);
		UserSession userSession =(UserSession) request.getSession().getAttribute("userSession");
		DataGridProperty pp = new DataGridProperty(); 
		try {	
			
			
			String sql ="SELECT a.`uuid`,b.name as userName,a.`applyDate`,c.name leaveTypeName,a.`leaveStartTime`,a.`leaveEndTime`, \n"
				       +"a.`leaveHourCount`,a.`destroyStartTime`,a.`destroyEndTime`,a.`destroyHourCount`, \n"
				       +"a.`RealStartTime`,a.`RealEndTime`,a.`realHourCount`,a.`memo`,ifnull(concat(e.auditStatus,'审批'),a.status) as status,a.`property` \n"
				       +" FROM k_leave a "
				       +" left join k_user b on a.userId = b.id"
				       +" left join k_leavetypesetup c on a.leaveTypeId = c.autoId"
				       +" left join j_leaveprocss d on a.uuid = d.uuid "
				       +" LEFT JOIN  \n"
						+"( \n"
						+"	SELECT DISTINCT a.DBID_ AS taskId,b.ID_, a.EXECUTION_ID_,b.ACTIVITYNAME_ AS auditStatus,  \n"
						+"	GROUP_CONCAT(c.userID_ ) AS auditUserId   \n"
						+"	FROM jbpm4_task a    \n"
						+"	LEFT JOIN jbpm4_execution b ON a.EXECUTION_ID_ = b.ID_    \n"
						+"	LEFT JOIN  jbpm4_participation c ON a.DBID_ = c.TASK_  AND c.type_ = 'candidate'  \n" 
						+"	GROUP BY a.EXECUTION_ID_   \n"
						+") e on  d.`ProcessInstanceId`= e.ID_    \n"
				       +" where 1=1 and a.status='未发起' and a.userId = '"+userSession.getUserId()+"' ${leaveTypeId} ${leaveStartTime} ${leaveEndTime} ${memo}";
				
			
			pp.setTableID("leavList");
			pp.setCustomerId(""); //
			pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			pp.setOrderBy_CH("applyDate");
			pp.setDirection("desc");
			pp.setPrintEnable(true);
			pp.setPrintTitle("未发起列表");
			
			pp.setColumnWidth("10,10,15,15,8,20,10");
			pp.setSQL(sql);
			
			pp.addColumn("用户", "userName");
			pp.addColumn("请假类型", "leaveTypeName");
			pp.addColumn("请假开始时间", "leaveStartTime");
			pp.addColumn("请假结束时间", "leaveEndTime");
			pp.addColumn("请假时数", "leaveHourCount");
			pp.addColumn("请假原因", "memo");
			pp.addColumn("状态", "status");
			
			pp.addSqlWhere("leaveTypeId"," and a.leaveTypeId like '%${leaveTypeId}%' ");
			pp.addSqlWhere("leaveStartTime"," and a.leaveStartTime like '%${leaveStartTime}%' ");
			pp.addSqlWhere("leaveEndTime"," and a.leaveEndTime like '%${leaveEndTime}%' ");
			pp.addSqlWhere("memo"," and a.memo like '%${memo}%' ");
			
			
			
			DataGridProperty pp2 = new DataGridProperty(); 
			String sql2 ="SELECT concat(a.`uuid`,'~',e.taskId) as uuid ,b.name as userName,a.`applyDate`,c.name leaveTypeName,a.`leaveStartTime`,a.`leaveEndTime`, \n"
				       +"a.`leaveHourCount`,a.`destroyStartTime`,a.`destroyEndTime`,a.`destroyHourCount`, \n"
				       +"a.`RealStartTime`,a.`RealEndTime`,a.`realHourCount`,a.`memo`,ifnull(concat(e.auditStatus,'审批'),a.status) as status,a.`property` \n"
				       +" FROM k_leave a "
				       +" left join k_user b on a.userId = b.id"
				       +" left join k_leavetypesetup c on a.leaveTypeId = c.autoId"
				       +" left join j_leaveprocss d on a.uuid = d.uuid "
				       +" LEFT JOIN  \n"
						+"( \n"
						+"	SELECT DISTINCT a.DBID_ AS taskId,b.ID_, a.EXECUTION_ID_,b.ACTIVITYNAME_ AS auditStatus,  \n"
						+"	GROUP_CONCAT(c.userID_ ) AS auditUserId   \n"
						+"	FROM jbpm4_task a    \n"
						+"	LEFT JOIN jbpm4_execution b ON a.EXECUTION_ID_ = b.ID_    \n"
						+"	LEFT JOIN  jbpm4_participation c ON a.DBID_ = c.TASK_  AND c.type_ = 'candidate'  \n" 
						+"	GROUP BY a.EXECUTION_ID_   \n"
						+") e on  d.`ProcessInstanceId`= e.ID_    \n"
				       +" where 1=1 and a.status='已发起' and a.userId = '"+userSession.getUserId()+"'";
			
		
				pp2.setTableID("yiLeavList");
				pp2.setCustomerId(""); //
				pp2.setWhichFieldIsValue(1);
				pp2.setInputType("radio");
				pp2.setOrderBy_CH("applyDate");
				pp2.setDirection("desc");
				pp2.setPrintEnable(true);
				pp2.setPrintTitle("已发起列表");
				pp2.setColumnWidth("10,10,15,15,8,10");
				pp2.setSQL(sql2);
				
				pp2.addColumn("用户", "userName");
				pp2.addColumn("请假类型", "leaveTypeName");
				pp2.addColumn("请假开始时间", "leaveStartTime");
				pp2.addColumn("请假结束时间", "leaveEndTime");
				pp2.addColumn("请假时数", "leaveHourCount");
				//pp2.addColumn("请假原因", "memo");
				pp2.addColumn("状态", "status");

				DataGridProperty pp3 = new DataGridProperty(); 
				String sql3 ="SELECT a.`uuid`,b.name as userName,a.`applyDate`,c.name leaveTypeName,a.`leaveStartTime`,a.`leaveEndTime`, \n"
					       +"a.`leaveHourCount`,a.`destroyStartTime`,a.`destroyEndTime`,a.`destroyHourCount`, \n"
					       +"a.`RealStartTime`,a.`RealEndTime`,a.`realHourCount`,a.`memo`,ifnull(concat(e.auditStatus,'审批'),a.status) as status,a.`property` \n"
					       +" FROM k_leave a "
					       +" left join k_user b on a.userId = b.id"
					       +" left join k_leavetypesetup c on a.leaveTypeId = c.autoId"
					       +" left join j_leaveprocss d on a.uuid = d.uuid "
					       +" LEFT JOIN  \n"
							+"( \n"
							+"	SELECT DISTINCT a.DBID_ AS taskId,b.ID_, a.EXECUTION_ID_,b.ACTIVITYNAME_ AS auditStatus,  \n"
							+"	GROUP_CONCAT(c.userID_ ) AS auditUserId   \n"
							+"	FROM jbpm4_task a    \n"
							+"	LEFT JOIN jbpm4_execution b ON a.EXECUTION_ID_ = b.ID_    \n"
							+"	LEFT JOIN  jbpm4_participation c ON a.DBID_ = c.TASK_  AND c.type_ = 'candidate'  \n" 
							+"	GROUP BY a.EXECUTION_ID_   \n"
							+") e on  d.`ProcessInstanceId`= e.ID_    \n"
					       +" where 1=1 and a.status='已通过待销假' and a.userId = '"+userSession.getUserId()+"'";
				
			
				pp3.setTableID("daiLeavList");
				pp3.setCustomerId(""); //
				pp3.setWhichFieldIsValue(1);
				pp3.setInputType("radio");
				pp3.setOrderBy_CH("applyDate");
				pp3.setDirection("desc");
				pp3.setPrintEnable(true);
				pp3.setPrintTitle("未销假列表");
				
				pp3.setColumnWidth("10,10,15,15,8,10");
				pp3.setSQL(sql3);
				
				pp3.addColumn("用户", "userName");
				pp3.addColumn("请假类型", "leaveTypeName");
				pp3.addColumn("请假开始时间", "leaveStartTime");
				pp3.addColumn("请假结束时间", "leaveEndTime");
				pp3.addColumn("请假时数", "leaveHourCount");
				//pp3.addColumn("请假原因", "memo");
				pp3.addColumn("状态", "status");

				DataGridProperty pp4 = new DataGridProperty(); 
				String sql4 ="SELECT DISTINCT concat(a.`uuid`,'~',e.taskId) as uuid,b.name as userName,a.`applyDate`,c.name leaveTypeName,a.`leaveStartTime`,a.`leaveEndTime`, \n"
					       +"a.`leaveHourCount`,a.`destroyStartTime`,a.`destroyEndTime`,a.`destroyHourCount`, \n"
					       +"a.`RealStartTime`,a.`RealEndTime`,a.`realHourCount`,a.`memo`,ifnull(e.auditStatus,a.status) as status,a.`property` \n"
					       +" FROM k_leave a "
					       +" left join k_user b on a.userId = b.id"
					       +" left join k_leavetypesetup c on a.leaveTypeId = c.autoId"
					       +" left join j_leaveprocss d on a.uuid = d.uuid "
					       +" inner JOIN  \n"
							+"( \n"
							+"	SELECT DISTINCT a.DBID_ AS taskId,b.ID_, a.EXECUTION_ID_,b.ACTIVITYNAME_ AS auditStatus,  \n"
							+"	GROUP_CONCAT(c.userID_ ) AS auditUserId   \n"
							+"	FROM jbpm4_task a    \n"
							+"	LEFT JOIN jbpm4_execution b ON a.EXECUTION_ID_ = b.ID_    \n"
							+"	LEFT JOIN  jbpm4_participation c ON a.DBID_ = c.TASK_  AND c.type_ = 'candidate'  \n" 
							+"	GROUP BY a.EXECUTION_ID_   \n"
							+") e on  d.`ProcessInstanceId`= e.ID_    \n"
					       +" where 1=1 and (a.status='销假已发起' or a.status='结束') and a.userId = '"+userSession.getUserId()+"'";
				
			
				pp4.setTableID("jieshuLeavList");
				pp4.setCustomerId(""); //
				pp4.setWhichFieldIsValue(1);
				pp4.setInputType("radio");
				pp4.setOrderBy_CH("applyDate");
				pp4.setDirection("desc");
				pp4.setPrintEnable(true);
				pp4.setPrintTitle("已销假列表");
				
				pp4.setColumnWidth("6,8,12,12,6,13,13,8,10");
				pp4.setSQL(sql4);
				
				pp4.addColumn("用户", "userName");
				pp4.addColumn("请假类型", "leaveTypeName");
				pp4.addColumn("请假开始时间", "leaveStartTime");
				pp4.addColumn("请假结束时间", "leaveEndTime");
				pp4.addColumn("请假时数", "leaveHourCount");
				pp4.addColumn("实际请假开始时间", "destroyStartTime");
				pp4.addColumn("实际请假结束时间", "destroyEndTime");
				pp4.addColumn("实际请假时数", "destroyHourCount");
				//pp4.addColumn("请假原因", "memo");
				pp4.addColumn("状态", "status");

					
				request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
				request.getSession().setAttribute(DataGrid.sessionPre + pp2.getTableID(), pp2);
				request.getSession().setAttribute(DataGrid.sessionPre + pp3.getTableID(), pp3);
				request.getSession().setAttribute(DataGrid.sessionPre + pp4.getTableID(), pp4);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{			
		}
		
		return modelAndView;
	}
	
	/**
	 * 新增跳转
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView addSkip (HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(ADDSKIP) ;
		UserSession usersession = (UserSession)request.getSession().getAttribute("userSession");
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect();
			
			LeaveService leaveService = new LeaveService(conn);
			String sex = leaveService.userDetail(usersession.getUserId());
			
			this.getWorkDate(modelAndView, leaveService);
			modelAndView.addObject("ctype","请假");
			modelAndView.addObject("sex",sex );
			
			//当前时间
			modelAndView.addObject("todateTime", asf.getCurrentDate()+" "+asf.getCurrentTime());
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
	
		return modelAndView;
	}
	
	/**
	 * 获取上班时间方法
	 * @param modelAndView
	 * @param leaveService
	 */
	public void getWorkDate(ModelAndView modelAndView,LeaveService leaveService){
			 
			String sql = "SELECT `value` FROM k_dic WHERE ctype = '上班时间' AND `name`='上午上班时间'"; 
			String fWorkTime= leaveService.getValueBySql(sql); //上午上班时间
			
			sql = "SELECT `value` FROM k_dic WHERE ctype = '上班时间' AND `name`='上午下班时间'"; 
			String fOffDutyTime= leaveService.getValueBySql(sql); //上午下班时间
			
			sql = "SELECT `value` FROM k_dic WHERE ctype = '上班时间' AND `name`='下午上班时间'"; 
			String arvoWorkTime= leaveService.getValueBySql(sql); //下午上班时间
			
			sql = "SELECT `value` FROM k_dic WHERE ctype = '上班时间' AND `name`='下午下班时间'"; 
			String arvoOffDutyTime= leaveService.getValueBySql(sql); //下午下班时间
			
			modelAndView.addObject("fWorkTime",fWorkTime);
			modelAndView.addObject("fOffDutyTime",fOffDutyTime);
			modelAndView.addObject("arvoWorkTime",arvoWorkTime);
			modelAndView.addObject("arvoOffDutyTime",arvoOffDutyTime);
	 
	}
	
	/**
	 * 新增与修改
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView add (HttpServletRequest request, HttpServletResponse response){
		
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect();
			
			UserSession userSession =(UserSession) request.getSession().getAttribute("userSession");
			
		    LeaveService leaveService = new LeaveService(conn);
			
			String  uuid= asf.showNull(request.getParameter("uuid"));
			String  leaveStartTime= asf.showNull(request.getParameter("leaveStartTime"));
			String  leaveEndTime= asf.showNull(request.getParameter("leaveEndTime"));
			String  leaveHourCount= asf.showNull(request.getParameter("leaveHourCount"));
			String  leaveTypeId= asf.showNull(request.getParameter("leaveTypeId"));
			String  memo = asf.showNull(request.getParameter("memo"));
			String applyDate = asf.showNull(request.getParameter("applyDate"));
			
			Leave leave = new Leave();
			
			leave.setLeaveStartTime(leaveStartTime);
			leave.setLeaveEndTime(leaveEndTime);
			leave.setLeaveHourCount(leaveHourCount);
			leave.setLeaveTypeId(leaveTypeId);
			leave.setMemo(memo);
			leave.setStatus("未发起");
			leave.setApplyDate(applyDate);
			leave.setUserId(userSession.getUserId());
			
			leave.setUuid(uuid);
			if("".equals(uuid)){
				uuid = UUID.randomUUID().toString();
				leave.setUuid(uuid);
				leaveService.add(leave);
			}else{
				leaveService.update(leave);

			}
			
			response.sendRedirect(request.getContextPath()+"/leave.do?method=list");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	
	/**
	 * 修改跳转
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView updateSkip (HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(UPDATESKIP);
		UserSession usersession = (UserSession)request.getSession().getAttribute("userSession");
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect();
			
			LeaveService leaveService = new LeaveService(conn);
			String  uuid = asf.showNull(request.getParameter("uuid"));
			 
			Leave leave = leaveService.getLeave(uuid);
			String sex = leaveService.userDetail(usersession.getUserId());
			
			this.getWorkDate(modelAndView, leaveService);
			
			modelAndView.addObject("ctype","请假");
			modelAndView.addObject("leave",leave);
			modelAndView.addObject("sex",sex);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return modelAndView;
	}
	
	/**
	 * 删除
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView del (HttpServletRequest request, HttpServletResponse response){
		
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect();
			
			LeaveService leaveService = new LeaveService(conn);
			String  uuid = asf.showNull(request.getParameter("uuid"));
			 
			leaveService.delete(uuid);
			
			response.sendRedirect(request.getContextPath()+"/leave.do?method=list");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return null;
	}

	/**
	 * 得到状态
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getStatus(HttpServletRequest request, HttpServletResponse response){
		
		
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect();
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("utf-8");
			PrintWriter out = response.getWriter();
			LeaveService leaveService = new LeaveService(conn);
			String  uuid = asf.showNull(request.getParameter("uuid"));
			
			if(!"".equals(uuid)){
				
				String sql = "select `status` from `k_leave` WHERE `uuid` = '"+uuid+"'"; 
				
				String status = asf.showNull(leaveService.getValueBySql(sql)); 
				
				out.write(status);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return null;
	}
	
	/**
	 * 检查使用情况
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView checkUse(HttpServletRequest request, HttpServletResponse response){
	
		UserSession userSession =(UserSession) request.getSession().getAttribute("userSession");
		
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect();
			String remind = "";
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			//response.setHeader("charset","utf-8"); 
			PrintWriter out = response.getWriter();
			LeaveService leaveService = new LeaveService(conn);
			LeaveTypeService leaveTypeService = new LeaveTypeService(conn);
			String  leaveTypeId = asf.showNull(request.getParameter("leaveTypeId"));
			
			if(!"".equals(leaveTypeId)){
				LeaveType  leaveType= leaveTypeService.getLeaveType(leaveTypeId);
				String sql = "SELECT COUNT(*) FROM k_leave a  \n"
							+"INNER JOIN k_leavetypesetup b ON a.leaveTypeId = b.autoId \n"
							+"WHERE a.userId = '"+userSession.getUserId()+"' AND a.leaveTypeId='"+leaveTypeId+"'"
							+" and a.status <> '发起人已作废' and  ApplyDate BETWEEN CONCAT(SUBSTR(NOW(),1,7),'-01') AND CONCAT(SUBSTR(NOW(),1,7),'-31')";
				
				int countTime = Integer.parseInt(leaveService.getValueBySql(sql)); //请假次数
				
				if(countTime>0){
					
						sql = "SELECT SUM(realHourCount) as realHourCount  FROM k_leave a  \n"
							+"INNER JOIN k_leavetypesetup b ON a.leaveTypeId = b.autoId \n"
							+"WHERE a.userId = '"+userSession.getUserId()+"' AND a.leaveTypeId='"+leaveTypeId+"'"
							+" and a.status <> '发起人已作废' and  ApplyDate BETWEEN CONCAT(SUBSTR(NOW(),1,7),'-01') AND CONCAT(SUBSTR(NOW(),1,7),'-31')"; 
						String realHourCount = asf.showNull(leaveService.getValueBySql(sql));
			
						double sumDayTime = 0;
						if(!"".equals(realHourCount)){
							if(realHourCount.indexOf(".")>-1){
								String tmp1 = realHourCount.substring(0,realHourCount.indexOf("."));
								int tmp2 = Integer.parseInt(realHourCount.substring(realHourCount.indexOf(".")+1).substring(0,1));
							    if(tmp2<=5){
							    	sumDayTime = Integer.parseInt(tmp1)+0.5;   //请假小于0.5小时，按半小时算
							    }else{
							    	sumDayTime = Integer.parseInt(tmp1)+1;    //大于0.5小时，按1小时算
							    }
							}else{
								sumDayTime=Integer.parseInt(realHourCount);
							}
						
						}
					   
						double dayTimes = getDayTimes(leaveService);
								
						/**
						 * 用总用时除以一条上班小时，得到 请假天数
						 * 
						 * */
						int dayTime =(int)(sumDayTime/dayTimes); 
						float dayHour = (float)(sumDayTime%dayTimes); //得到余数的小时
						int onlyDays = dayTime;
						if(dayHour>0){
							onlyDays = dayTime+1;
						}
						
						remind = "我已休假<font color=red>"+countTime+"</font>次" +
								"<font color=red>"+dayTime+"</font>天";
						if(dayHour != 0){
							remind +="<font color=red>"+dayHour+"</font>小时";
						}		
						
						
						int monthCountLimit = Integer.parseInt(leaveType.getMonthCountLimit());
						
						if(monthCountLimit==0){
							remind +=",不限定次数";
						}else{
							int surplusCount = monthCountLimit - countTime; 
							remind +=",可休假剩余："+surplusCount+"次";
						}
						
						//剩余多少天数
						int monthDayLimit = Integer.parseInt(leaveType.getMonthDayLimit());
						
						
						if(monthDayLimit == 0){
							remind +="、不限定天数。";
						}else{
							
							int surplusDayCount = monthDayLimit - onlyDays; 
							remind +=""+surplusDayCount+"天";

							if(dayHour != 0){
								remind +=dayTimes-dayHour+"个小时。";
							}
							
						}
						//remind +=",可休假剩余"+surplusCount+"次,"+surplusDayCount+"天"+"";
						
						
				}else{
					remind ="您可请假";
					if(Integer.parseInt(leaveType.getMonthCountLimit())==0){
						remind +="无限制次,";
					}else{
						remind +=leaveType.getMonthCountLimit()+"次,";
					}
					if(Integer.parseInt(leaveType.getMonthDayLimit())==0){
						remind +="无限制天!";
					}else{
						remind +=leaveType.getMonthDayLimit()+"天！";
					}
				}
				out.write(remind);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return null;
	}

	/**
	 * 发起请假申请
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView startAudit(HttpServletRequest request, HttpServletResponse response){
		
		
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect();
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			LeaveService leaveService = new LeaveService(conn);
			String  uuid = asf.showNull(request.getParameter("uuid"));
			
			if(!"".equals(uuid)){
				
				String sql = "UPDATE `k_leave` SET `status` = '已发起' WHERE `uuid` = '"+uuid+"'"; 
				
				boolean result = leaveService.UpdateValueBySql(sql); 
				
				if(result){
					
					startFlow(request, response, uuid,"请假"); //启动流程
				}
				
			}
			PrintWriter out = response.getWriter();
			out.write("发起成功");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return null;
	}

	/**
	 * 请假 审批
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView leaveAuditList(HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(LEAVEAUDITLIST);
		UserSession userSession =(UserSession) request.getSession().getAttribute("userSession");
		DataGridProperty pp = new DataGridProperty(); 
		
		try {	
			
			
			String sql ="SELECT DISTINCT e.*,a.`uuid`,b.name as userName,a.`applyDate`,c.name leaveTypeName,a.`leaveStartTime`,a.`leaveEndTime`, \n"
				       +"a.`leaveHourCount`,a.`destroyStartTime`,a.`destroyEndTime`,a.`destroyHourCount`, \n"
				       +"a.`RealStartTime`,a.`RealEndTime`,a.`realHourCount`,a.`memo`,ifnull(concat(e.auditStatus,'审批'),a.status) as status,a.`property` \n"
				       +" FROM k_leave a "
				       +" left join k_user b on a.userId = b.id"
				       +" left join k_leavetypesetup c on a.leaveTypeId = c.autoId"
				       +" INNER JOIN `j_leaveprocss` d ON a.uuid  = d.`uuid` \n"
				       +" inner JOIN (" 
				       +"SELECT DISTINCT a.DBID_ AS taskId,b.ID_, a.EXECUTION_ID_,b.ACTIVITYNAME_ AS auditStatus, \n" 
					   +"GROUP_CONCAT(c.userID_ ) AS auditUserId  \n"
					   +"FROM jbpm4_task a   \n"
					   +"INNER JOIN jbpm4_execution b ON a.EXECUTION_ID_ = b.ID_   \n"
					   +"LEFT JOIN  jbpm4_participation c ON a.DBID_ = c.TASK_  AND c.type_ = 'candidate'  \n"
					   +" where 1=1 " 
					   +" AND "+userSession.getUserId()+" like concat(c.userID_,'%') "
					   +"GROUP BY a.EXECUTION_ID_  \n"
				       +") e on e.ID_  = d.`ProcessInstanceId` "
				       +" where 1=1 and a.status ='已发起' and d.ctype='请假' ${userName} ${leaveTypeId} ${leaveStartTime} ${leaveEndTime} ${memo}";
				
			
			pp.setTableID("leaveAuditList");
			pp.setCustomerId(""); //
			pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			pp.setOrderBy_CH("applyDate");
			pp.setDirection("desc");
			
			pp.setColumnWidth("10,10,15,15,8,20,10");
			pp.setSQL(sql);
			
			pp.addColumn("用户", "userName");
			pp.addColumn("请假类型", "leaveTypeName");
			pp.addColumn("请假开始时间", "leaveStartTime");
			pp.addColumn("请假结束时间", "leaveEndTime");
			pp.addColumn("请假小时数", "leaveHourCount");
			pp.addColumn("请假原因", "memo");
			pp.addColumn("状态", "status");
			
			pp.addSqlWhere("userName"," and b.name like '%${userName}%' ");
			pp.addSqlWhere("leaveTypeId"," and a.leaveTypeId like '%${leaveTypeId}%' ");
			pp.addSqlWhere("leaveStartTime"," and a.leaveStartTime like '%${leaveStartTime}%' ");
			pp.addSqlWhere("leaveEndTime"," and a.leaveEndTime like '%${leaveEndTime}%' ");
			pp.addSqlWhere("memo"," and a.memo like '%${memo}%' ");
			
			
			
			//已审批的流程
			
			DataGridProperty pp3 = new DataGridProperty(); 
			String sql3 ="SELECT a.`uuid`,b.name as userName,a.`applyDate`,c.name leaveTypeName,a.`leaveStartTime`,a.`leaveEndTime`, \n"
				       +"a.`leaveHourCount`,a.`destroyStartTime`,a.`destroyEndTime`,a.`destroyHourCount`, \n"
				       +"a.`RealStartTime`,a.`RealEndTime`,a.`realHourCount`,a.`memo`,ifnull(concat(e.auditStatus,'审批'),a.status) as status,a.`property` \n"
				       +" FROM k_leave a "
				       +" left join k_user b on a.userId = b.id"
				       +" left join k_leavetypesetup c on a.leaveTypeId = c.autoId"
				       +" left join j_leaveprocss d on a.uuid = d.uuid "
				       +" LEFT JOIN  \n"
						+"( \n"
						+"	SELECT DISTINCT a.DBID_ AS taskId,b.ID_, a.EXECUTION_ID_,b.ACTIVITYNAME_ AS auditStatus,  \n"
						+"	GROUP_CONCAT(c.userID_ ) AS auditUserId   \n"
						+"	FROM jbpm4_task a    \n"
						+"	LEFT JOIN jbpm4_execution b ON a.EXECUTION_ID_ = b.ID_    \n"
						+"	LEFT JOIN  jbpm4_participation c ON a.DBID_ = c.TASK_  AND c.type_ = 'candidate'  \n" 
						+"	GROUP BY a.EXECUTION_ID_   \n"
						+") e on  d.`ProcessInstanceId`= e.ID_    \n"
				       +" where 1=1 and (a.status='已通过待销假' or a.status = '已发起') and a.userId = '"+userSession.getUserId()+"' ${userName} ${leaveTypeId} ${leaveStartTime} ${leaveEndTime} ${memo}";
			
			pp3.setTableID("alreadyLeavList");
			pp3.setCustomerId(""); //
			pp3.setWhichFieldIsValue(1);
			pp3.setInputType("radio");
			pp3.setOrderBy_CH("applyDate");
			pp3.setDirection("desc");
			pp3.setPrintEnable(true);
			pp3.setPrintTitle("未销假列表");
			
			pp3.setColumnWidth("10,10,15,15,8,10");
			pp3.setSQL(sql3);
			
			pp3.addColumn("用户", "userName");
			pp3.addColumn("请假类型", "leaveTypeName");
			pp3.addColumn("请假开始时间", "leaveStartTime");
			pp3.addColumn("请假结束时间", "leaveEndTime");
			pp3.addColumn("请假时数", "leaveHourCount");
			//pp3.addColumn("请假原因", "memo");
			pp3.addColumn("状态", "status");
			
			pp3.addSqlWhere("userName"," and b.name like '%${userName}%' ");
			pp3.addSqlWhere("leaveTypeId"," and a.leaveTypeId like '%${leaveTypeId}%' ");
			pp3.addSqlWhere("leaveStartTime"," and a.leaveStartTime like '%${leaveStartTime}%' ");
			pp3.addSqlWhere("leaveEndTime"," and a.leaveEndTime like '%${leaveEndTime}%' ");
			pp3.addSqlWhere("memo"," and a.memo like '%${memo}%' ");
			
			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			request.getSession().setAttribute(DataGrid.sessionPre + pp3.getTableID(), pp3);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{			
		}
		
		return modelAndView;
	}
	
	/**
	 * 审批跳转
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView auditSkip (HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(AUDIT);
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect();
			
			LeaveService leaveService = new LeaveService(conn);
			UserService userService = new UserService(conn);
			
			String  taskId = asf.showNull(request.getParameter("taskId"));
			String  ctype = asf.showNull(request.getParameter("ctype")); //获取 请假审批 还是 销假审批
			
			if(!"".equals(taskId)){
				
				String pdid = jbpmTemplate.getProcessInstanceId(taskId);
				
				String sql = "select uuid from j_leaveprocss where ProcessInstanceId='"+pdid+"'"; 
				String uuid = leaveService.getValueBySql(sql);
				
				if(!"".equals(uuid)){
					
					Leave leave = leaveService.getLeave(uuid);

					User user = userService.getUser(leave.getUserId(), "id");
					leave.setUserId(asf.showNull(user.getName()));
					
					if("".equals(ctype)){
						ctype = "请假审批";
					}else{
						ctype = "销假审批";
					}
					modelAndView.addObject("leave",leave);
					modelAndView.addObject("ctype",ctype);
					modelAndView.addObject("leave",leave);
					modelAndView.addObject("taskId",taskId);
				}
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return modelAndView;
	}
	
	/**
	 * 请假审批 完成
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView auditAgree(HttpServletRequest request, HttpServletResponse response){
		
		
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect();
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			Map<String, String> startMap = new HashMap<String, String>();
			
			LeaveService leaveService = new LeaveService(conn);
			String  uuid = asf.showNull(request.getParameter("uuid"));
			String taskId = asf.showNull(request.getParameter("taskId"));
			
			if(!"".equals(uuid)){
				
				ProcessFormService processFormService = new ProcessFormService(conn);
				
				String pdid = jbpmTemplate.getProcessInstanceId(taskId);
				String activeName = jbpmTemplate.getActiveName(pdid);
				
				ProcessForm processForm = new ProcessForm();
				processForm.setProcessInstanseId(pdid);
				processForm.setKey("审批意见");
				processForm.setValue("");
				processForm.setNodeName(activeName);
				processForm.setDealUserId(userSession.getUserId());
				processForm.setDealTime(new ASFuntion().getCurrentDate()+" "+new ASFuntion().getCurrentTime());
				processForm.setProperty("");
				processFormService.add(processForm);// 添加记录(流程表)
				
				
				String sql = " SELECT TO_DAYS(`leaveEndTime`)-TO_DAYS(`leaveStartTime`) as day FROM `k_leave` WHERE `uuid` = '"+uuid+"' ";
				int day = Integer.parseInt(leaveService.getValueBySql(sql));
				startMap.put("day", day+"");
				
				// 根据节点 ID 把下个节点的执行人 传到下一个节点
				jbpmTemplate.setTaskVariables(taskId, startMap);
				
				jbpmTemplate.completeTask(taskId); //完成节点
				
				if("执行合伙人批准".equals(activeName) || "主管合伙人批准".equals(activeName) || (activeName.equals("人力资源部备案") && day<3) ||(activeName.equals("业务经理")&&day<3)){
					
					
					
					sql = "UPDATE `k_leave` SET `status` = '已通过待销假' WHERE `uuid` = '"+uuid+"'"; 
					leaveService.UpdateValueBySql(sql); 
					Leave leave = leaveService.getLeave(uuid);
					//发消息提醒
					PlacardTable pt = new PlacardTable();
					PlacardService placardService = new PlacardService(conn);
					pt.setAddresser(userSession.getUserId());
					pt.setAddresserTime(asf.getCurrentDate()+" "+asf.getCurrentTime());
					pt.setMpShortMessage("是");  //设置发送手机短信
					pt.setCaption("请假审批通过");
					String content  = "您的请假申请已通过审核。请假日期为：【"+leave.getLeaveStartTime()+"】 至 【"+leave.getLeaveEndTime()+" 】结束!";
					pt.setMatter(content);
					pt.setAddressee(leave.getUserId());
					pt.setMpContent(content);
					
					placardService.AddPlacard(pt);
				}
				
			}
			response.sendRedirect(request.getContextPath()+"/leave.do?method=leaveAuditList");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}

	/**
	 * 销假 审批List
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView destroyAuditList(HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(DESTROYAUDITLIST);
		UserSession userSession =(UserSession) request.getSession().getAttribute("userSession");
		DataGridProperty pp = new DataGridProperty(); 
		try {	
			
			
			String sql ="SELECT e.*,a.`uuid`,b.name as userName,a.`applyDate`,c.name leaveTypeName,a.`leaveStartTime`,a.`leaveEndTime`, \n"
				       +"a.`leaveHourCount`,a.`destroyStartTime`,a.`destroyEndTime`,a.`destroyHourCount`, \n"
				       +"a.`RealStartTime`,a.`RealEndTime`,a.`realHourCount`,a.`memo`,a.status,a.`property` \n"
				       +" FROM k_leave a "
				       +" left join k_user b on a.userId = b.id "
				       +" left join k_leavetypesetup c on a.leaveTypeId = c.autoId "
				       +" INNER JOIN `j_leaveprocss` d ON a.uuid  = d.`uuid` \n"
				       +" inner JOIN (" 
				       +"SELECT DISTINCT a.DBID_ AS taskId,b.ID_, a.EXECUTION_ID_,b.ACTIVITYNAME_ AS auditStatus, \n" 
					   +"GROUP_CONCAT(c.userID_ ) AS auditUserId  \n"
					   +"FROM jbpm4_task a   \n"
					   +"INNER JOIN jbpm4_execution b ON a.EXECUTION_ID_ = b.ID_   \n"
					   +"LEFT JOIN  jbpm4_participation c ON a.DBID_ = c.TASK_  AND c.type_ = 'candidate'  \n"
					   +" where 1=1 " 
					   +" AND "+userSession.getUserId()+" like concat(c.userID_,'%') "
					   +"GROUP BY a.EXECUTION_ID_  \n"
				       +") e on e.ID_  = d.`ProcessInstanceId` "
				       +" where 1=1 and a.status ='销假已发起' and d.ctype='销假' ${userName} ${leaveTypeId} ${leaveStartTime} ${leaveEndTime} ${memo}";
				
			
			pp.setTableID("destroyAuditList");
			pp.setCustomerId(""); //
			pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			pp.setOrderBy_CH("applyDate");
			pp.setDirection("desc");
			pp.setPrintEnable(true);
			
			
			pp.setColumnWidth("10,10,10,10,8,10,10,9,15,10");
			pp.setSQL(sql);
			
			pp.addColumn("用户", "userName");
			pp.addColumn("请假类型", "leaveTypeName");
			pp.addColumn("请假开始时间", "leaveStartTime");
			pp.addColumn("请假结束时间", "leaveEndTime");
			pp.addColumn("请假总用时时间", "leaveHourCount");
			pp.addColumn("实际请假开始时间", "destroyStartTime");
			pp.addColumn("实际请假结束时间", "destroyEndTime");
			pp.addColumn("实际请假总用时时间", "destroyHourCount");
			pp.addColumn("请假原因", "memo");
			pp.addColumn("状态", "status");
			
			pp.addSqlWhere("userName"," and b.name like '%${userName}%' ");
			pp.addSqlWhere("leaveTypeId"," and a.leaveTypeId like '%${leaveTypeId}%' ");
			pp.addSqlWhere("leaveStartTime"," and a.leaveStartTime like '%${leaveStartTime}%' ");
			pp.addSqlWhere("leaveEndTime"," and a.leaveEndTime like '%${leaveEndTime}%' ");
			pp.addSqlWhere("memo"," and a.memo like '%${memo}%' ");
			
			//已审批的销假状态
			DataGridProperty pp1 = new DataGridProperty(); 
			String alSql ="SELECT e.*,a.`uuid`,b.name as userName,a.`applyDate`,c.name leaveTypeName,a.`leaveStartTime`,a.`leaveEndTime`, \n"
			       +"a.`leaveHourCount`,a.`destroyStartTime`,a.`destroyEndTime`,a.`destroyHourCount`, \n"
			       +"a.`RealStartTime`,a.`RealEndTime`,a.`realHourCount`,a.`memo`,a.status,a.`property` \n"
			       +" FROM k_leave a "
			       +" left join k_user b on a.userId = b.id "
			       +" left join k_leavetypesetup c on a.leaveTypeId = c.autoId "
			       +" INNER JOIN `j_leaveprocss` d ON a.uuid  = d.`uuid` \n"
			       +" inner JOIN (" 
			       +"SELECT DISTINCT a.DBID_ AS taskId,b.ID_, a.EXECUTION_ID_,b.ACTIVITYNAME_ AS auditStatus, \n" 
				   +"GROUP_CONCAT(c.userID_ ) AS auditUserId  \n"
				   +"FROM jbpm4_task a   \n"
				   +"INNER JOIN jbpm4_execution b ON a.EXECUTION_ID_ = b.ID_   \n"
				   +"LEFT JOIN  jbpm4_participation c ON a.DBID_ = c.TASK_  AND c.type_ = 'candidate'  \n"
				   +" where 1=1 " 
				   +" AND "+userSession.getUserId()+" like concat(c.userID_,'%') "
				   +"GROUP BY a.EXECUTION_ID_  \n"
			       +") e on e.ID_  = d.`ProcessInstanceId` "
			       +" where 1=1 and (a.status ='已通过待销假' and d.ctype='销假' or a.status = '已发起') ${userName} ${leaveTypeId} ${leaveStartTime} ${leaveEndTime} ${memo}";
			
		
		pp1.setTableID("alreadyDestroyAuditList");
		pp1.setCustomerId(""); //
		pp1.setWhichFieldIsValue(1);
		pp1.setInputType("radio");
		pp1.setOrderBy_CH("applyDate");
		pp1.setDirection("desc");
		pp1.setPrintEnable(true);
		pp1.setColumnWidth("10,10,10,10,8,10,10,9,15,10");
		pp1.setSQL(alSql);
		
		pp1.addColumn("用户", "userName");
		pp1.addColumn("请假类型", "leaveTypeName");
		pp1.addColumn("请假开始时间", "leaveStartTime");
		pp1.addColumn("请假结束时间", "leaveEndTime");
		pp1.addColumn("请假总用时时间", "leaveHourCount");
		pp1.addColumn("实际请假开始时间", "destroyStartTime");
		pp1.addColumn("实际请假结束时间", "destroyEndTime");
		pp1.addColumn("实际请假总用时时间", "destroyHourCount");
		pp1.addColumn("请假原因", "memo");
		pp1.addColumn("状态", "status");
		
		pp1.addSqlWhere("userName"," and b.name like '%${userName}%' ");
		pp1.addSqlWhere("leaveTypeId"," and a.leaveTypeId like '%${leaveTypeId}%' ");
		pp1.addSqlWhere("leaveStartTime"," and a.leaveStartTime like '%${leaveStartTime}%' ");
		pp1.addSqlWhere("leaveEndTime"," and a.leaveEndTime like '%${leaveEndTime}%' ");
		pp1.addSqlWhere("memo"," and a.memo like '%${memo}%' ");
			
			
		request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		request.getSession().setAttribute(DataGrid.sessionPre + pp1.getTableID(), pp1);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{			
		}
		
		return modelAndView;
	}
	
	/**
	 * 销假跳转
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView destroySkip (HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(UPDATESKIP);
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect();
			
			LeaveService leaveService = new LeaveService(conn);
			String  uuid = asf.showNull(request.getParameter("uuid"));
			
			if(!"".equals(uuid)){
					
				Leave leave = leaveService.getLeave(uuid);
				modelAndView.addObject("leave",leave);
				
				this.getWorkDate(modelAndView, leaveService);
			}
			
			modelAndView.addObject("ctype","销假");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return modelAndView;
	}
	
	/**
	 * 发起人销假 保存
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView destroySave (HttpServletRequest request, HttpServletResponse response){
		
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect();
			
			LeaveService leaveService = new LeaveService(conn);
			
			String  uuid= asf.showNull(request.getParameter("uuid"));
			String  destroyStartTime= asf.showNull(request.getParameter("destroyStartTime"));
			String  destroyEndTime= asf.showNull(request.getParameter("destroyEndTime"));
			String  destroyHourCount= asf.showNull(request.getParameter("destroyHourCount"));
			
			Leave leave = new Leave();
			leave.setStatus("销假已发起");
			leave.setDestroyStartTime(destroyStartTime);
			leave.setDestroyEndTime(destroyEndTime);
			leave.setDestroyHourCount(destroyHourCount);
			
			leave.setUuid(uuid);
			if(!"".equals(uuid)){
				boolean result = leaveService.destroySave(leave);
				if(result){
					
					this.startFlow(request, response, uuid,"销假"); //启动流程
				}
			}			
			response.sendRedirect(request.getContextPath()+"/leave.do?method=list");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	
	/**
	 * 启动流程
	 * @param request
	 * @param response
	 * @param uuid
	 * @return
	 * @throws IOException
	 */
	public ModelAndView startFlow(HttpServletRequest request, HttpServletResponse response,String uuid,String ctype) throws IOException{
		// 获取登录的用户
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		String userId = userSession.getUserId();
		Connection conn = null;
		
		ASFuntion asf = new ASFuntion();
		try {
				conn = new DBConnect().getConnect();

				LeaveService leaveService = new LeaveService(conn);
				InteriorEmailService emailService =  new InteriorEmailService(conn);
				Map<String, String> startMap = new HashMap<String, String>();
				startMap.put("applyUserId", userId);
		
				String sql = ""; 
				if("销假".equals(ctype)){
					
					sql = "select processDefinitionId from j_processdeploy where processKey='destroyLeaveFlow' " ;
				}else if("请假".equals(ctype)){
					
					sql = "select processDefinitionId from j_processdeploy where processKey='leaveFlow' " ;
				}
				String processDefinitionId = leaveService.getValueBySql(sql);
				
				/*sql = "SELECT b.departname FROM k_user a " +
						"LEFT JOIN k_department b ON a.departmentid = b.autoId " +
						"where a.id = '"+userSession.getUserId()+"'" ;
				String departname = leaveService.getValueBySql(sql);
				
				String auditUser = "";
				if(departname.indexOf("行政")>-1 || departname.indexOf("公共")>-1){
					auditUser = "行政部门";
				}else{
					auditUser = "业务部门";
				}	*/
				
				String auditUser = "";
				sql = "SELECT GROUP_CONCAT(autoid) FROM k_department WHERE fullpath LIKE '614%' AND autoid !=614";
				String departId = leaveService.getValueBySql(sql)+",";
				if(departId.indexOf(userSession.getUserAuditDepartmentId()) >-1){
					auditUser = "行政部门";
				}else{
					auditUser = "业务部门";
				}	
				
				if("销假".equals(ctype)){
					startMap.put("auditUser", auditUser);
				}else{
					startMap.put("departName", auditUser);
				
				}
				
				sql = "SELECT  GROUP_CONCAT(DISTINCT c.id) AS userid FROM k_user a \n"
						+"LEFT JOIN k_department b ON a.`departmentid` = b.autoid \n"
						+"INNER JOIN k_user c ON b.`autoid` = c.`departmentid`  \n"
						+"LEFT JOIN k_userrole d ON c.`id` = d.userid \n"
						+"LEFT JOIN k_role e ON d.`rid` = e.`id`  \n"
						+"WHERE a.id='"+userSession.getUserId()+"' AND (e.rolename LIKE '%经理%' OR e.rolename LIKE '%负责人%')";
				String adUserId = "";
				adUserId = emailService.getValueBySql(sql);
				if("".equals(adUserId)){
					adUserId = "19"; //找不到人审批，就用管理员进行审批
				}else{
					adUserId = adUserId.replaceAll("@`@", ",");
					adUserId = adUserId.substring(0, adUserId.length()-1);
					adUserId+=",19";
					}
				
				System.out.println("请假人员审批人："+adUserId);
				startMap.put("departManger", adUserId);
				
				// 启动流程
				ProcessInstance pi =jbpmTemplate.startProcessById(processDefinitionId, startMap);
		
				// 获取节点任务
				TaskQuery tq = jbpmTemplate.getTaskService().createTaskQuery();
				List<Task> taskList2 = tq.processInstanceId(pi.getId()).list();
				Task myTask = taskList2.get(0);
		  
				LeaveFlow leaveFlow = new LeaveFlow();
				leaveFlow.setProcessInstanceId(pi.getId());
				leaveFlow.setUuid(uuid);
				leaveFlow.setApplyuser(userSession.getUserId());
				leaveFlow.setApplyDate(asf.getCurrentDate()+" "+asf.getCurrentTime());
				leaveFlow.setState(myTask.getName());
				leaveFlow.setCtype(ctype);
		
				// 根据节点 ID 把下个节点的执行人 传到下一个节点
				jbpmTemplate.setTaskVariables(myTask.getId(), startMap);
				
				ProcessFormService processFormService = new ProcessFormService(conn);
				
				String activeName = jbpmTemplate.getActivityName(myTask.getId()); // 获取当前节点
				ProcessForm processForm = new ProcessForm();
				processForm.setProcessInstanseId(processDefinitionId);
				processForm.setKey(ctype+"发起状态");
				processForm.setValue("发起成功");
				processForm.setNodeName(activeName);
				processForm.setDealUserId(userId);
				processForm.setDealTime(new ASFuntion().getCurrentDate()+" "+new ASFuntion().getCurrentTime());
				processForm.setDealUserId(userId);
				processForm.setProperty("");
				processFormService.add(processForm);// 添加记录(流程表)
				
				// 完成节点
				jbpmTemplate.completeTask(myTask.getId());
		
				leaveService.addProcss(leaveFlow);// 添加流程 (自己的表)
				
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;

	}
	
	/**
	 * 销假 审批 通过
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView destroy (HttpServletRequest request, HttpServletResponse response){
		
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect();
			
			LeaveService leaveService = new LeaveService(conn);
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			
			String  uuid= asf.showNull(request.getParameter("uuid"));
			String taskId = asf.showNull(request.getParameter("taskId"));
			
			if(!"".equals(taskId)){
				leaveService.destroy(uuid);
				
				ProcessFormService processFormService = new ProcessFormService(conn);
				
				String pdid = jbpmTemplate.getProcessInstanceId(taskId);
				String activeName = jbpmTemplate.getActiveName(pdid);
				
				ProcessForm processForm = new ProcessForm();
				processForm.setProcessInstanseId(pdid);
				processForm.setKey("审批意见");
				processForm.setValue("");
				processForm.setNodeName(activeName);
				processForm.setDealUserId(userSession.getUserId());
				processForm.setDealTime(new ASFuntion().getCurrentDate()+" "+new ASFuntion().getCurrentTime());
				processForm.setProperty("");
				processFormService.add(processForm);// 添加记录(流程表)
				
				String sql = "update set state='结束' from j_leaveprocss where ProcessInstanceId='"+pdid+"'";
				leaveService.UpdateValueBySql(sql); //
				
				sql = "update set state='销假成功' from k_leave where uuid='"+uuid+"'";
				leaveService.UpdateValueBySql(sql); //
				
				jbpmTemplate.completeTask(taskId);
				Leave leave = leaveService.getLeave(uuid);
				PlacardTable pt = new PlacardTable();
				PlacardService placardService = new PlacardService(conn);
				pt.setAddresser(userSession.getUserId());
				pt.setAddresserTime(asf.getCurrentDate()+" "+asf.getCurrentTime());
				pt.setMpShortMessage("是");  //设置发送手机短信
				pt.setCaption("销假审批通过");
				String content  = "您的销假申请已通过审核。销假日期为：【"+leave.getDestroyStartTime()+"】 至 【"+leave.getDestroyEndTime()+" 】结束!";
				pt.setMatter(content);
				pt.setAddressee(leave.getUserId());
				pt.setMpContent(content);
 
				placardService.AddPlacard(pt);


			}			
			response.sendRedirect(request.getContextPath()+"/leave.do?method=destroyAuditList");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	
	/**
	 * 一天上多少个小时
	 */
	private double getDayTimes(LeaveService leaveService){
	
		
      		    String sql = "SELECT `value` FROM k_dic WHERE ctype = '上班时间' AND `name`='上午上班时间'"; 
				String fWorkTime= leaveService.getValueBySql(sql); //上午上班时间
				
				sql = "SELECT `value` FROM k_dic WHERE ctype = '上班时间' AND `name`='上午下班时间'"; 
				String fOffDutyTime= leaveService.getValueBySql(sql); //上午下班时间
				
				sql = "SELECT `value` FROM k_dic WHERE ctype = '上班时间' AND `name`='下午上班时间'"; 
				String arvoWorkTime= leaveService.getValueBySql(sql); //下午上班时间
				
				sql = "SELECT `value` FROM k_dic WHERE ctype = '上班时间' AND `name`='下午下班时间'"; 
				String arvoOffDutyTime= leaveService.getValueBySql(sql); //下午下班时间
				
				String[] fworkTimes       = fWorkTime.split(":");
				String[] fOffDutyTimes    = fOffDutyTime.split(":");
				String[] arvoWorkTimes    = arvoWorkTime.split(":");
				String[] arvoOffDutyTimes = arvoOffDutyTime.split(":");
				double fworkDay = Double.parseDouble(fworkTimes[0])+(Double.parseDouble(fworkTimes[1])/60);
				double fOffDutyDay = Double.parseDouble(fOffDutyTimes[0])+(Double.parseDouble(fOffDutyTimes[1])/60);
				double arvoWorkDay = Double.parseDouble(arvoWorkTimes[0])+(Double.parseDouble(arvoWorkTimes[1])/60);
				double arvoOffDutyDay = Double.parseDouble(arvoOffDutyTimes[0])+(Double.parseDouble(arvoOffDutyTimes[1])/60);
				
	            return ((fOffDutyDay-fworkDay)+(arvoOffDutyDay-arvoWorkDay));			
	
	}
	/**
	 * 请假的详细情况
	 */
	public ModelAndView checkUse2(HttpServletRequest request, HttpServletResponse response){
		UserSession userSession =(UserSession) request.getSession().getAttribute("userSession");
		boolean flag = true;
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect();
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			//response.setHeader("charset","utf-8"); 
			PrintWriter out = response.getWriter();
			LeaveService leaveService = new LeaveService(conn);
			LeaveTypeService leaveTypeService = new LeaveTypeService(conn);
			String  leaveTypeId = asf.showNull(request.getParameter("leaveTypeId"));
			String  uuid= asf.showNull(request.getParameter("uuid"));
			String  leaveHourCount= asf.showNull(request.getParameter("leaveHourCount"));
			
			LeaveType  leaveType= leaveTypeService.getLeaveType(leaveTypeId);   //请假限制
			int monthCountLimit = Integer.parseInt(leaveType.getMonthCountLimit());   
			int YearCountLimit = Integer.parseInt(leaveType.getYearCountLimit());
			double dayTimes = getDayTimes(leaveService); 
			
			int days =(int)( Double.parseDouble(leaveHourCount)/dayTimes);
			double moredays = Double.parseDouble(leaveHourCount)%dayTimes;
			
			//验证要请假的天数是否超过月限制
			if(moredays>0){
				days= days+1; 
			}
			if((days>Integer.parseInt(leaveType.getMonthDayLimit())) && Integer.parseInt(leaveType.getMonthDayLimit())!=0){
				flag = false;
			}
			
			if(!"".equals(leaveTypeId)){
				
				String sql = "SELECT COUNT(*) FROM k_leave a  \n"
							+"INNER JOIN k_leavetypesetup b ON a.leaveTypeId = b.autoId \n"
							+"WHERE a.userId = '"+userSession.getUserId()+"' AND a.leaveTypeId='"+leaveTypeId+"'  "
							+" and a.status <> '发起人已作废' AND applyDate BETWEEN CONCAT(SUBSTR(NOW(),1,7),'-01') AND CONCAT(SUBSTR(NOW(),1,7),'-31')"; 
				
				int countTime = Integer.parseInt(leaveService.getValueBySql(sql)); //请假次数    
				sql ="SELECT COUNT(*) FROM k_leave a  \n"
					+"INNER JOIN k_leavetypesetup b ON a.leaveTypeId = b.autoId \n"
					+"WHERE a.userId = '"+userSession.getUserId()+"' AND a.leaveTypeId='"+leaveTypeId+"'  "
					+"and a.status <> '发起人已作废' AND Year(applyDate) = Year(now())";
				int yearCountTime = Integer.parseInt(leaveService.getValueBySql(sql));  //年请假次数
				
				
				if(countTime>0){
					   //计算月请假的时间是否超过
						sql = "SELECT SUM(realHourCount) as realHourCount  FROM k_leave a  \n"
							+"INNER JOIN k_leavetypesetup b ON a.leaveTypeId = b.autoId \n"
							+"WHERE a.userId = '"+userSession.getUserId()+"' AND a.leaveTypeId='"+leaveTypeId+"'"
							+" and a.status <> '发起人已作废' AND applyDate BETWEEN CONCAT(SUBSTR(NOW(),1,7),'-01') AND CONCAT(SUBSTR(NOW(),1,7),'-31')"; 
						String realHourCount = asf.showNull(leaveService.getValueBySql(sql));
						
		
						double sumDayTime = 0;
						if(!"".equals(realHourCount)){
							if(realHourCount.indexOf(".")>-1){
								String tmp1 =realHourCount.substring(0,realHourCount.indexOf("."));
								int tmp2 =Integer.parseInt(realHourCount.substring(realHourCount.indexOf(".")+1).substring(0,1));
							 
							    if(tmp2<=5){
							    	sumDayTime = Integer.parseInt(tmp1)+0.5 ; //请假小于0.5小时，按半小时算
							    }else{
							    	sumDayTime = Integer.parseInt(tmp1)+1;   //大于0.5小时，按1小时算
							    }
							}else{
								sumDayTime=Integer.parseInt(realHourCount);
							}
						
						}
			
						/**
						 * 用总用时除以一条上班小时，得到 请假天数
						 * 
						 * */
						int dayTime =(int)(sumDayTime/dayTimes); //请假天数

						if((dayTime+days>Integer.parseInt(leaveType.getMonthDayLimit())) && Integer.parseInt(leaveType.getMonthDayLimit())!=0){
							flag = false;
						}
						//计算年请教的时间是否超过
						sql = "SELECT SUM(realHourCount) as realHourCount  FROM k_leave a  \n"
							+"INNER JOIN k_leavetypesetup b ON a.leaveTypeId = b.autoId \n"
							+"WHERE a.userId = '"+userSession.getUserId()+"' AND a.leaveTypeId='"+leaveTypeId+"'"
							+" and a.status <> '发起人已作废' AND Year(applyDate) = Year(now())"; 
						String yearHourCount = asf.showNull(leaveService.getValueBySql(sql));
						double yearSumDayTime =0;
						if(!"".equals(yearHourCount)){
							if(yearHourCount.indexOf(".")>-1){
								String tmp1 =yearHourCount.substring(0,yearHourCount.indexOf("."));
								int tmp2 =Integer.parseInt(yearHourCount.substring(yearHourCount.indexOf(".")+1).substring(0,1));
							 
							    if(tmp2<=5){
							    	yearSumDayTime = Integer.parseInt(tmp1)+0.5 ; //请假小于0.5小时，按半小时算
							    }else{
							    	yearSumDayTime = Integer.parseInt(tmp1)+1;   //大于0.5小时，按1小时算
							    }
							}else{
								yearSumDayTime=Integer.parseInt(yearHourCount);
							}
						
						}
						int yearDayTime = (int)(yearSumDayTime/dayTimes);
						if((yearDayTime+days>Integer.parseInt(leaveType.getYearDayLimit())) && Integer.parseInt(leaveType.getYearDayLimit())!=0){
							flag = false;
						}
		
				}
				
				  String remaind ="";
				  if(uuid!="" && flag){
					  remaind="可以修改";
				  }else if(!flag || (countTime>=monthCountLimit && monthCountLimit!=0) || (yearCountTime>=YearCountLimit && YearCountLimit!=0)){
					  remaind="不能请假了";
				  }
				  out.write(remaind);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return null;
	}

	/**
	 * 根据KEY查出它的 PDID
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView queryJbpmPdIdByKey (HttpServletRequest request, HttpServletResponse response){

		Connection conn = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			
			ASFuntion asf = new ASFuntion();
			String key = asf.showNull(request.getParameter("key"));
			conn = new DBConnect().getConnect();
			
			String pdId = new DbUtil(conn).queryForString("SELECT processDefinitionId FROM `j_processdeploy` WHERE processKey='"+key+"' LIMIT 1 ");
			
			response.getWriter().write(pdId); 
			
			//当前时间
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
	
		return null;
	}
	
	/**
	 * 修改状态
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView updateStatus(HttpServletRequest request, HttpServletResponse response){
		
		
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect();
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("utf-8");
			
			PrintWriter out = response.getWriter();

			String  uuid = asf.showNull(request.getParameter("uuid"));
			String  status = asf.showNull(request.getParameter("status"));
			
			if(!"".equals(uuid)){
				
				String sql = "update `k_leave` set `status`='"+status+"' WHERE `uuid` = '"+uuid+"'"; 
				
				int result = new DbUtil(conn).executeUpdate(sql); 
				
				out.write(result);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return null;
	}
	
	/**
	 * 检测请假开始时间，是否符合规定
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView checkDay(HttpServletRequest request, HttpServletResponse response){
		
		
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect();
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("utf-8");
			PrintWriter out = response.getWriter();
			String  leaveTypeId = asf.showNull(request.getParameter("leaveTypeId"));
			String  startDate = asf.showNull(request.getParameter("startDate"));
			
			if(!"".equals(leaveTypeId)){
				
				String sql = "SELECT CONCAT('"+startDate+"'<DATE_ADD(NOW(),INTERVAL applyLimit DAY),'~',applyLimit)FROM k_leavetypesetup WHERE autoid =  '"+leaveTypeId+"' and applyLimit>0 "; 
				
				String resutl = asf.showNull(new DbUtil(conn).queryForString(sql)); //1大于请假时间，0小于请假规定时间,""也是小于规定时间
				
				out.write(resutl);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return null;
	}
}
