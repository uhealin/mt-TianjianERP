package com.matech.audit.work.leaveCompany;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.api.ProcessInstance;
import org.jbpm.api.TaskQuery;
import org.jbpm.api.task.Task;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.leave.LeaveService;
import com.matech.audit.service.leave.model.Leave;
import com.matech.audit.service.leaveCompany.LeaveCompanyFlowService;
import com.matech.audit.service.leaveCompany.LeaveCompanyService;
import com.matech.audit.service.leaveCompany.model.LeaveCompany;
import com.matech.audit.service.leaveCompany.model.LeaveCompanyFlow;
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.audit.service.process.JbpmTemplate;
import com.matech.audit.service.process.ProcessFormService;
import com.matech.audit.service.process.model.ProcessForm;
import com.matech.audit.service.seal.SealService;
import com.matech.audit.service.user.UserService;
import com.matech.audit.service.user.model.User;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.WebUtil;

public class LeaveCompanyAction  extends MultiActionController{

	private static final String ADDANDEDIT = "/leaveCompany/AddandEdit.jsp";
	private static final String LIST = "/leaveCompany/list.jsp";
	private JbpmTemplate jbpmTemplate;
	
	public JbpmTemplate getJbpmTemplate() {
		return jbpmTemplate;
	}

	public void setJbpmTemplate(JbpmTemplate jbpmTemplate) {
		this.jbpmTemplate = jbpmTemplate;
	}
	
	
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		
		ModelAndView modelAndView = new ModelAndView(LIST);
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
		DataGridProperty pp = new DataGridProperty(); 
		try {	
			
			
			String ppSql="SELECT UUID, u.name,applyDate,predictLeaveDate,reason,STATUS FROM k_leaveofficeTJ l  LEFT JOIN k_user u ON l.`userId`=u.id WHERE l.STATUS = '未发起' ";
			pp.setTableID("leaveCompanyList");
			pp.setCustomerId("");
			pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			pp.setOrderBy_CH("applyDate");
			pp.setDirection("desc");
			
			pp.setColumnWidth("20,20,20,20,20");
			pp.setSQL(ppSql);
			
			pp.addColumn("用户", "name");
			pp.addColumn("申请日期", "applyDate");
			pp.addColumn("预计离职时间", "predictLeaveDate");
			pp.addColumn("原因", "reason");
			pp.addColumn("状态", "status");
			System.out.println("-----------leaveCompany-----list()---------");
			
//			
//			pp.addSqlWhere("title"," and a.title like '%${title}%' ");
//			pp.addSqlWhere("content"," and a.content like '%${content}%' ");
//			pp.addSqlWhere("publishDate"," and a.publishDate like '%${publishDate}%' ");
			
 			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		}
		
		return modelAndView;
	}
	
	
	public ModelAndView addAndEdit(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		
		ModelAndView model = new ModelAndView(ADDANDEDIT) ;

		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			WebUtil webUtil=new WebUtil(request, response);
			LeaveCompany lc=webUtil.evalObject(LeaveCompany.class);
			conn = new DBConnect().getConnect();
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			LeaveCompanyService leaveService = new LeaveCompanyService(conn);
			String  uuid = asf.showNull(request.getParameter("uuid"));
			if(!"".equals(uuid)){
					this.startFlow(request, response, uuid); //启动流程
					
					String sql = "UPDATE `k_leaveofficeTJ` SET `status` = '已发起' WHERE `uuid` = '"+uuid+"'"; 
					
					leaveService.UpdateValueBySql(sql); 
					PrintWriter out = response.getWriter();
					out.write("发起成功");
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		model.addObject("iris","iris");
		return model;
	}
	


	public ModelAndView startLeaveCompany(HttpServletRequest request, HttpServletResponse response){
		
		
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect();
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			LeaveCompanyService leaveService = new LeaveCompanyService(conn);
			String  uuid = asf.showNull(request.getParameter("uuid"));
			
			if(!"".equals(uuid)){
				
				
					this.startFlow(request, response, uuid); //启动流程
					
					String sql = "UPDATE `k_leaveofficeTJ` SET `status` = '已发起' WHERE `uuid` = '"+uuid+"'"; 
					
					leaveService.UpdateValueBySql(sql); 
					PrintWriter out = response.getWriter();
					out.write("发起成功");
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return null;
	}
	
	
	

	public ModelAndView startFlow(HttpServletRequest request, HttpServletResponse response,String uuid) throws IOException{
		// 获取登录的用户
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		String userId = userSession.getUserId();
		Connection conn = null;
		
		ASFuntion asf = new ASFuntion();
		try {
				conn = new DBConnect().getConnect();

				LeaveCompanyService leaveService = new LeaveCompanyService(conn);
				SealService sealService = new SealService(conn);
				Map<String, String> startMap = new HashMap<String, String>();
				startMap.put("applyUserId", userId);
				
		
				String sql = "select PDID from mt_jbpm_processdeploy where PKEY='de5e21e5-d1d9-4372-af22-01ef63169278' " ;
				String processDefinitionId = leaveService.getValueBySql(sql);
				
				sql ="SELECT a.id FROM k_user a,k_role b,k_userrole c \n"
					+"WHERE a.departmentid = '"+userSession.getUserAuditDepartmentId()+"'  \n"
					+"AND b.rolename = '部门主管'  \n"
					+"AND a.id = c.userid \n"
					+"AND b.id = c.rid";
				String auditUserIds = sealService.getValueBySql(sql);
				
				if(!"".equals(auditUserIds)){
					auditUserIds = auditUserIds.replaceAll("@`@",",");
					auditUserIds = auditUserIds.substring(0, auditUserIds.length()-1);
				}else{
					auditUserIds ="19";
				}
				startMap.put("auditUser", auditUserIds);
				// 启动流程
				ProcessInstance pi =jbpmTemplate.startProcessById(processDefinitionId, startMap);
		
				// 获取节点任务
				TaskQuery tq = jbpmTemplate.getTaskService().createTaskQuery();
				List<Task> taskList2 = tq.processInstanceId(pi.getId()).list();
				Task myTask = taskList2.get(0);
		  
				LeaveCompanyFlow leaveFlow = new LeaveCompanyFlow();
				leaveFlow.setProcessInstanceId(pi.getId());
				leaveFlow.setUuid(uuid);
				leaveFlow.setApplyuser(userSession.getUserId());
				leaveFlow.setApplyDate(asf.getCurrentDate()+" "+asf.getCurrentTime());
				leaveFlow.setState(myTask.getName());
				leaveFlow.setCtype("");
				leaveFlow.setProperty("");
		
				// 根据节点 ID 把下个节点的执行人 传到下一个节点
				jbpmTemplate.setTaskVariables(myTask.getId(), startMap);
				
				LeaveCompanyFlowService leaveCompanyFlowService = new LeaveCompanyFlowService(conn);
				
				ProcessFormService processFormService = new ProcessFormService(conn);
				
				String activeName = jbpmTemplate.getActivityName(myTask.getId()); // 获取当前节点
				ProcessForm processForm = new ProcessForm();
				processForm.setProcessInstanseId(processDefinitionId);
				processForm.setKey("发起状态");
				processForm.setValue("发起成功");
				processForm.setNodeName(activeName);
				processForm.setDealUserId(userId);
				processForm.setDealTime(new ASFuntion().getCurrentDate()+" "+new ASFuntion().getCurrentTime());
				processForm.setDealUserId(userId);
				processForm.setProperty("");
				processFormService.add(processForm);// 添加记录(流程表)
				
				// 完成节点
				jbpmTemplate.completeTask(myTask.getId());
		
				leaveCompanyFlowService.addLeaveCompanyFlow(leaveFlow);// 添加流程 (自己的表)
				
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return null;

	}
	
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

	
	
	public ModelAndView goAdd(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		
		ModelAndView modelAndView = new ModelAndView(ADDANDEDIT);
		
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 

		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect();
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			String  uuid = asf.showNull(request.getParameter("uuid"));
			DbUtil dbutil=new DbUtil(conn);
//			List<User> l=dbutil.select(User.class, "select * from {0} where id={1}", userSession.getUserId());
			
//			UserVO user=dbutil.load(UserVO.class,Integer.parseInt(userSession.getUserId()));
//			user=l.get(0);
//			WebUtil webUtil=new WebUtil(request, response);
//			LeaveCompany lc=webUtil.evalObject(LeaveCompany.class);
//			LeaveCompanyService leaveService = new LeaveCompanyService(conn);
//			System.out.println(user.getName()+"-------------"+user.getLoginid()+"+++++++loginid===="+user.getId()+"------listuser--"+l.get(0).getId());
//			System.out.println(user.getName()+"-------------"+user.getLoginid()+"+++++++loginid===="+user.getId());
			User user=new User();
			UserService userService=new UserService(conn);
			user=userService.getUser(userSession.getUserId(), "id");
			
			modelAndView.addObject("lc",user);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	public ModelAndView addLeaveCompany(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		
		ModelAndView modelAndView = new ModelAndView(ADDANDEDIT);
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
		String uuid=UUID.randomUUID().toString();
		ASFuntion asf = new ASFuntion();

		WebUtil webUtil=new WebUtil(request, response);
		LeaveCompany lc=webUtil.evalObject(LeaveCompany.class);
//		String userId=asf.showNull(request.getParameter("userId"));
//		String sex=asf.showNull(request.getParameter("sex"));
//		String departmentId=asf.showNull(request.getParameter("departmentId"));
//		String birthday=asf.showNull(request.getParameter("birthday"));
//		String inworktime=asf.showNull(request.getParameter("inworktime"));
//		String incompanytime=asf.showNull(request.getParameter("incompanytime"));
//		
//
//		String education=asf.showNull(request.getParameter("education"));
//		String degree=asf.showNull(request.getParameter("degree"));
//		String major=asf.showNull(request.getParameter("major"));
//		String graduation=asf.showNull(request.getParameter("graduation"));
//		String qualification=asf.showNull(request.getParameter("qualification"));
//		String rank=asf.showNull(request.getParameter("rank"));
//
//		String techQualification=asf.showNull(request.getParameter("techQualification"));
//		String mobilePhone=asf.showNull(request.getParameter("mobilePhone"));
		String applyDate=new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString();
//		String predictLeaveDate=asf.showNull(request.getParameter("predictLeaveDate"));
//		String reason=asf.showNull(request.getParameter("contents"));
////		String status=asf.showNull(request.getParameter("status"));
//		
//		
		lc.setUUID(uuid);
//		lc.setUserId(userId);
//		lc.setSex(sex);
//		lc.setDepartmentid(departmentId);
//		lc.setBirthday(birthday);
//		lc.setInworktime(inworktime);
//		lc.setIncompanytime(incompanytime);
//		lc.setEducation(education);
//		lc.setDegree(degree);
//		lc.setMajor(major);
//		lc.setGraduation(graduation);
//		lc.setQualification(qualification);
//		lc.setRank(rank);
//		lc.setTechQualification(techQualification);
//		lc.setMobilePhone(mobilePhone);
		lc.setApplyDate(applyDate);
//		lc.setPredictLeaveDate(predictLeaveDate);
//		lc.setReason(reason);
//		lc.setStatus(status);

		
		PreparedStatement ps = null ;
		Connection con=null;

		try {
			con=new DBConnect().getConnect("");
			LeaveCompanyService lcs=new LeaveCompanyService(con);
			lc.setUserId(userSession.getUserId());
			lcs.addLeaveCompany(lc);
			response.sendRedirect(request.getContextPath()+"/leaveCompany.do");
		} catch (Exception e) {
			e.printStackTrace();
		} 
		finally{
			DbUtil.close(con);
		}
		
		
		return null;
	}
	
	
	public ModelAndView deleteLeaveCompany(HttpServletRequest request, HttpServletResponse response){
		ASFuntion CHF = new ASFuntion() ;
		Connection conn = null ;
		try {
			conn = new DBConnect().getConnect("") ;
			String uuid = CHF.showNull(request.getParameter("uuid")) ;
			LeaveCompanyService lcs=new LeaveCompanyService(conn);
			lcs.deleteLeaveCompany(uuid);
			response.sendRedirect(request.getContextPath()+"/leaveCompany.do") ;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {  
			DbUtil.close(conn) ;
		} 
		return null; 
	}
	

	public ModelAndView updateLeaveCompany(HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(ADDANDEDIT);
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		try {

			conn = new DBConnect().getConnect("");
			LeaveCompany lc=new LeaveCompany();
			String uuid=asf.showNull(request.getParameter("hiddenPid"));
			String userId=asf.showNull(request.getParameter("userId"));
			String sex=asf.showNull(request.getParameter("sex"));
			String departmentId=asf.showNull(request.getParameter("departmentId"));
			String birthday=asf.showNull(request.getParameter("birthday"));
			String inworktime=asf.showNull(request.getParameter("inworktime"));
			String incompanytime=asf.showNull(request.getParameter("incompanytime"));
			

			String education=asf.showNull(request.getParameter("education"));
			String degree=asf.showNull(request.getParameter("degree"));
			String major=asf.showNull(request.getParameter("major"));
			String graduation=asf.showNull(request.getParameter("graduation"));
			String qualification=asf.showNull(request.getParameter("qualification"));
			String rank=asf.showNull(request.getParameter("rank"));

			String techQualification=asf.showNull(request.getParameter("techQualification"));
			String mobilePhone=asf.showNull(request.getParameter("mobilePhone"));
			String applyDate=new Date().toString();
			String predictLeaveDate=asf.showNull(request.getParameter("predictLeaveDate"));
			String reason=asf.showNull(request.getParameter("contents"));
//			String status=asf.showNull(request.getParameter("status"));
			
			
			lc.setUUID(uuid);
			lc.setUserId(userId);
			lc.setSex(sex);
			lc.setDepartmentid(departmentId);
			lc.setBirthday(birthday);
			lc.setInworktime(inworktime);
			lc.setIncompanytime(incompanytime);
			lc.setEducation(education);
			lc.setDegree(degree);
			lc.setMajor(major);
			lc.setGraduation(graduation);
			lc.setQualification(qualification);
			lc.setRank(rank);
			lc.setTechQualification(techQualification);
			lc.setMobilePhone(mobilePhone);
			lc.setApplyDate(applyDate);
			lc.setPredictLeaveDate(predictLeaveDate);
			lc.setReason(reason);
//			lc.setStatus(status);
			
			
			LeaveCompanyService lcs=new LeaveCompanyService(conn);
			lcs.updateLeaveCompany(lc);
			response.sendRedirect(request.getContextPath()+"/leaveCompany.do") ;
			
		} catch (Exception e) { 
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		
		return null;
	}

	
	public ModelAndView updateSkipLeaveCompany(HttpServletRequest request, HttpServletResponse response){
		ASFuntion CHF = new ASFuntion() ;
		Connection conn = null ;
		ModelAndView m=new ModelAndView(ADDANDEDIT);
		ASFuntion asf = new ASFuntion();
		String uuid=asf.showNull(request.getParameter("uuid"));
		System.out.println(uuid+"-----------------------");

		LeaveCompany lc = new LeaveCompany();
		try {

			conn = new DBConnect().getConnect("");
			LeaveCompanyService lcs=new LeaveCompanyService(conn);
			lc=lcs.getLeaveCompany(uuid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		m.addObject("lc",lc);
		return m;
	}
}
