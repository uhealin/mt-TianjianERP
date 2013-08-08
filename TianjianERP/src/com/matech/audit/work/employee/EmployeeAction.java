package com.matech.audit.work.employee;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.RepositoryService;
import org.jbpm.api.TaskQuery;
import org.jbpm.api.model.OpenExecution;
import org.jbpm.api.model.Transition;
import org.jbpm.api.task.Participation;
import org.jbpm.api.task.Task;
import org.jbpm.jpdl.internal.activity.DecisionHandlerActivity;
import org.jbpm.jpdl.internal.activity.TaskActivity;
import org.jbpm.pvm.internal.el.Expression;
import org.jbpm.pvm.internal.model.ActivityImpl;
import org.jbpm.pvm.internal.model.ExecutionImpl;
import org.jbpm.pvm.internal.model.ProcessDefinitionImpl;
import org.jbpm.pvm.internal.task.TaskImpl;
import org.jbpm.pvm.internal.wire.descriptor.ObjectDescriptor;
import org.jbpm.pvm.internal.wire.descriptor.StringDescriptor;
import org.jbpm.pvm.internal.wire.operation.FieldOperation;
import org.jbpm.pvm.internal.wire.usercode.UserCodeReference;
import org.jfree.data.gantt.TaskSeries;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.datagrid.ExtGrid;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.cadet.model.CadetVO;
import com.matech.audit.service.employe.model.EmployeeApplyVO;
import com.matech.audit.service.employe.model.EmployeeVO;
import com.matech.audit.service.form.FormDefineService;

import com.matech.audit.service.hr.model.ResumeVO;
import com.matech.audit.service.oa.personCapture.personCaptureService;
import com.matech.audit.service.process.JbpmServicce;
import com.matech.audit.service.process.JbpmTemplate;
import com.matech.audit.service.process.ProcessService;
import com.matech.audit.service.process.model.ProcessApply;
import com.matech.audit.service.process.model.ProcessDeploy;
import com.matech.audit.service.process.model.ProcessField;
import com.matech.audit.service.process.model.ProcessForm;
import com.matech.audit.service.user.UserService;
import com.matech.audit.service.user.model.User;
import com.matech.audit.service.user.model.UserVO;
import com.matech.audit.work.form.FormDefineAction;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;

public class EmployeeAction extends MultiActionController{

	private String List = "/employee/list.jsp";
	private String VIEW = "/employee/employeeview.jsp";
	private String EDIT="/employee/edit.jsp";
	private String LOGIN = "/login.jsp";
	private String EMPLOYEE = "/employee/employee.jsp";
	private String VIEW_EMPLOYEE = "/employee/viewEmployee.jsp";
	
	//public final static String PKEY_EMPLOY_APPLY="8709ad41-6392-442f-8e81-11a5282d2763";
	
//	public final static String PKEY_EMPLOY_APPLY="64d7b2ac-bb3d-40bc-ab06-b4e6b39873f3";
	
//	public final static String HR_EMPLOYEE_REGISTER="64d7b2ac-bb3d-40bc-ab06-b4e6b39873f3";
	public final static String HR_EMPLOYEE_REGISTER="8709ad41-6392-442f-8e81-11a5282d2763";
//	public final static String HR_EMPLOYEE_REGISTER="8709ad41-6392-442f-8e81-11a5282d2763";
	public final static String FORMID_HR_EMPLOYEE_REGISTER="34f344bc-ec2f-40f3-a5c7-59dd9193ab2d";
	//public final static String FORMID_HR_EMPLOYEE_REGISTER="38de243e-b868-4e1f-a6ae-cacdca0c6e7e";
	public final static String FORMID_EMPLOY_APPLY="233a7b57-e307-40cc-91f2-c470c05c8861";

	
	public final static String PKEY_EMPLOY_APPLY="8709ad41-6392-442f-8e81-11a5282d2763";
	
	
   private JbpmTemplate jbpmTemplate;
	
	public JbpmTemplate getJbpmTemplate() {
		return jbpmTemplate;
	}

	public void setJbpmTemplate(JbpmTemplate jbpmTemplate) {
		this.jbpmTemplate = jbpmTemplate;
	}
	
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelandview = new ModelAndView(List);
		
		
		return modelandview;
	}
	
	public ModelAndView listId(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelandview = new ModelAndView(VIEW);
		
		String uuid = request.getParameter("uuid");
		WebUtil webUtil = null;
		EmployeeVO employeeVO = null;
		Connection conn = null;
		DbUtil dbutil = null;
		try {
			webUtil = new WebUtil(request, response);
			employeeVO = webUtil.evalObject(EmployeeVO.class);
			
			conn = new DBConnect().getConnect();
			dbutil = new DbUtil(conn);
			employeeVO = dbutil.load(EmployeeVO.class, uuid);
			request.setAttribute("employeeVO", employeeVO);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			dbutil.close(conn);
		}
		
		return modelandview;
	}

	
	//添加
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/html;charset=utf-8");
		
		ModelAndView modelandview = new ModelAndView(LOGIN);
		WebUtil webUtil = null;
		EmployeeVO employeeVO = null;
		Connection conn = null;
		DbUtil dbutil = null;
		int i = 0;
		
		String entityId=UUID.randomUUID().toString();
		try{
			
			webUtil =new WebUtil(request, response);
			  conn = new DBConnect().getConnect();
			     dbutil = new DbUtil(conn);
			employeeVO = webUtil.evalObject(EmployeeVO.class);
		   
			employeeVO.setUuid(entityId);
			i+=dbutil.insert(employeeVO);
		   
		     String uuid=UUID.randomUUID().toString();
		     employeeVO.setUuid(uuid);
		     i = dbutil.insert(employeeVO);
		     
		     
		     if(i < 1){
		    	 throw new Exception("保存失败");
		     }
		     
		   
		    	 
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
	    //response.sendRedirect(LOGIN);
		return null;
	}
	
	//修改
	public ModelAndView updateSave(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelandview = new ModelAndView(EDIT);
		
		
		return modelandview;
	}
	
	
	public ModelAndView doBatchCheck(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		String uuids=request.getParameter("uuids"),strUuids="";
		String[] arrUuids=uuids.split(",");
		
		for(String uuid:arrUuids){
			strUuids+="''"+uuid+"'',";
		}
		strUuids=StringUtil.trim(strUuids, ",");
		String remark_check=request.getParameter("remark_check");
		String hr_state=request.getParameter("hr_state");
		int eff=0;
		String re="";
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			eff+=dbUtil.executeUpdate(ResumeVO.class,
					"update {0} set hr_state=?,remark_check=?,hr_check_time=?,hr_check_id=? where uuid in ("+strUuids+")", 
					hr_state
					,remark_check
					,StringUtil.getCurDateTime()
					,userSession.getUserId()
					);
			if(eff>0){
				re="审核完成";
			}else{
				re="没有记录被审核";
			}
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(re);
		return null;
	}
	
	public ModelAndView doBatchInterview(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		UserSession userSession=webUtil.getUserSession();
		String uuids=request.getParameter("uuids"),strUuids="";
		String[] arrUuids=uuids.split(",");
		
		for(String uuid:arrUuids){
			strUuids+="''"+uuid+"'',";
		}
		strUuids=StringUtil.trim(strUuids, ",");
		String remark_interview=request.getParameter("remark_interview");
		//String result=request.getParameter("result");
		String hr_interview_state=request.getParameter("hr_interview_state");
		int eff=0;
		String re="";
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
			eff+=dbUtil.executeUpdate(ResumeVO.class,
					"update {0} set hr_interview_state=?,remark_interview=? where uuid in ("+strUuids+")", 
					hr_interview_state
					,remark_interview
					);
			if(eff>0){
				re="审核完成";
			}else{
				re="没有记录被审核";
			}
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(re);
		return null;
	}
	

	public ModelAndView doApply(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn=null;
		DbUtil dbUtil=null;
		WebUtil webUtil=new WebUtil(request, response);
		//UserSession userSession=webUtil.getUserSession();
		String uuid=request.getParameter("uuid");
		String checker_id=request.getParameter("checker_id");
		if(checker_id==null||"".equals(checker_id)){
			checker_id="19";
		}
        String departmentId=request.getParameter("departmentId");
		UserSession userSession=new UserSession();
		int eff=0;
		String re="";
		response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
		EmployeeVO employeeVO=webUtil.evalObject(EmployeeVO.class);
		userSession.setUserId(""+checker_id);
		request.getSession().setAttribute("userSession", userSession);
//		EmployeeApplyVO employeeApplyVO=new EmployeeApplyVO();
		try{
			conn=new DBConnect().getConnect();
			dbUtil=new DbUtil(conn);
//			employeeVO.setUuid(UUID.randomUUID().toString());
//			eff+=dbUtil.insert(employeeVO);
//			if(eff<1){
//				throw new Exception("登记保存失败");
//			}
//		    employeeApplyVO.setUuid(UUID.randomUUID().toString());
//		    employeeApplyVO.setDepartmentid(departmentid);
//		    employeeApplyVO.setEmployee_id(employeeVO.getUuid());
//		    employeeApplyVO.setState("0");
//		    employeeApplyVO.setUserid(checker_id);
//		    employeeApplyVO.setTemp_userid(UUID.randomUUID().toString());
//			eff+=dbUtil.insert(employeeApplyVO);
//			if(eff<2){
//				throw new Exception("你已发起过申请,不能再发起");
//			}
			JbpmTemplate jbpmTemplate = JbpmServicce.getJbpmTemplate();
			
			
			
			//新发起流程
			FormDefineService fdfs = new FormDefineService(conn) ;
			ProcessService prs = new ProcessService(conn) ;
			
//			String pId = StringUtil.showNull(prs.getPIdByPKeyAndForeignId(HR_EMPLOYEE_REGISTER, employeeApplyVO.getUuid())) ;
			
			//保存表单
			String formEntityId = StringUtil.showNull(request.getParameter("formEntityId")) ;//直接申请或修改时会把这个参数传进来
			uuid = fdfs.saveFormData(request,response,FORMID_HR_EMPLOYEE_REGISTER,formEntityId);
			EmployeeVO em=dbUtil.load(EmployeeVO.class, uuid);
			em.setDepartmentId(departmentId);
			dbUtil.update(em);
			//if(!"".equals(pId)) {
				//已经发起申请了，直接跳转
				//taskId = jbpmTemplate.getActivityTask(pId).getId() ;
				//response.sendRedirect(request.getContextPath()+"/process.do?method=processTransfer&pKey="
				//					+pKey+"&uuid="+formEntityId+"&taskId="+taskId+"&apply="+apply) ;
				//return null ;
			//}
			
			//首次发起申请 
			ProcessDeploy pd = prs.getProcessDeploy(HR_EMPLOYEE_REGISTER) ;
			
			Map<String,String> startMap = new HashMap<String, String>() ;
			startMap.put("applyUser", checker_id) ;
			startMap.put("uuid",uuid) ;
			startMap.put("mt_formid", FORMID_HR_EMPLOYEE_REGISTER ) ;
			startMap.put("processName", StringUtil.showNull(pd.getPname()) ) ;
			startMap.put("pKey", HR_EMPLOYEE_REGISTER ) ;
			
			ProcessInstance pi = jbpmTemplate.startProcessById(pd.getPdId(),startMap);
			
			//完成第一个结点的任务  lmb修改成开始后不完成第一个节点
			/*
			TaskQuery tq = jbpmTemplate.getTaskService().createTaskQuery() ;   
			List<Task> taskList2 = tq.processInstanceId(pi.getId()).list();  				
			Task myTask = taskList2.get(0) ;
			jbpmTemplate.completeTask(myTask.getId(),nextNodeName) ;
			*/
			//保存流程轨迹
			ProcessForm pf = new ProcessForm() ;
			pf.setpId(pi.getId()) ;
			pf.setKey("意见") ;
			pf.setValue("新发起申请") ;
			pf.setDealTime(StringUtil.getCurDateTime()) ;
			pf.setDealUserId(checker_id) ;
			pf.setNodeName("保存") ;
			pf.setFormId(FORMID_HR_EMPLOYEE_REGISTER) ;
			pf.setFormEntityId(uuid) ;
			prs.addProcessForm(pf) ;
			System.out.println("------------------+++++++++++++");
			//保存申请记录
			ProcessApply pa = new ProcessApply() ;
			pa.setId(StringUtil.getUUID()) ;
			pa.setPkey(HR_EMPLOYEE_REGISTER) ;
			pa.setPid(pi.getId()) ; 
			pa.setForeignId(uuid) ;
			pa.setPname(pd.getPname()) ;
			pa.setApplyTime(StringUtil.getCurDateTime()) ;
			UserVO userVO=dbUtil.load(UserVO.class, "identityCard", employeeVO.getIdcard());
			pa.setApplyUserId(userVO.getId()+"") ;
			prs.addProcessApply(pa) ;
		    re="已成功发起考核申请！<script type=\"text/javascript\">var b=confirm(\"关闭窗口？\");if(b){parent.parent.close();}</script>";
		}catch(Exception ex){
			re=ex.getLocalizedMessage();
		}finally{
			DbUtil.close(conn);
		}
		response.getWriter().write(re);
		return null;
	}
	
	//查看
	public ModelAndView view(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView modelandview = new ModelAndView(VIEW_EMPLOYEE);
		//ModelAndView modelandview = null;
		
		String uuid = request.getParameter("uuid");
		WebUtil webUtil = null;
		EmployeeVO employeeVO  = null;
		Connection conn = null;
		DbUtil dbutil = null;
		try {
			webUtil = new WebUtil(request, response);
			//cadetVO = webUtil.evalObject(CadetVO.class);
			
			conn = new DBConnect().getConnect();
			dbutil = new DbUtil(conn);
			employeeVO = dbutil.load(EmployeeVO.class, uuid);
			request.setAttribute("employee", employeeVO);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			dbutil.close(conn);
		}
		
		return modelandview;
	}	

	
}
