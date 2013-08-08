package com.matech.audit.work.process;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.jbpm.api.ProcessInstance;
import org.jbpm.api.TaskQuery;
import org.jbpm.api.task.Task;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.service.attachFileUploadService.AttachService;
import com.matech.audit.service.department.DepartmentService;
import com.matech.audit.service.department.model.DepartmentVO;
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.audit.service.process.JbpmTemplate;
import com.matech.audit.service.process.ProcessFormService;
import com.matech.audit.service.process.model.ProcessForm;
import com.matech.audit.service.proclamation.ProclamationService;
import com.matech.audit.service.proclamation.model.Proclamation;
import com.matech.audit.service.proclamation.model.ProclamationFlow;
import com.matech.audit.service.user.UserService;
import com.matech.audit.service.user.model.User;
import com.matech.audit.service.userpopedom.UserPopedomService;
import com.matech.audit.work.oa.interiorEmail.InteriorEmailAction;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
/**
 * proclamationSy.do?method=list&menuid=10000508
 * @author Administrator
 *
 */
public class ProclamationSyAction extends MultiActionController{
 
	private static final String LIST="process/proclamationSyProcess/List.jsp";
	private static final String ADDSKIP="process/proclamationSyProcess/AddandEdit.jsp";
	private static final String UPDATESKIP="process/proclamationSyProcess/AddandEdit.jsp";
	private static final String VIEW="proclamation/view.jsp";
	private static final String AUDITLIST="process/proclamationSyProcess/auditList.jsp"; //审批list
	private static final String AUDITTREEPAGE="process/proclamationSyProcess/treeList.jsp"; //审批页面
	private static final String AUDITLOOKPAGE="process/proclamationSyProcess/auditView.jsp"; //审批页面
	private static final String INDEXPAGE = "proclamation"; // 附件名
	
	private static final String MyLookProclamationList="proclamation/MyLookProclamationList.jsp";
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
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
		DataGridProperty pp = new DataGridProperty(); 
		try {	
			
			
			String ppSql=" SELECT a.`uuid`,a.`title`,c.departname AS departmentId,a.ctype,b.name AS `userId`, \n"
					     +" a.publishDate,CONCAT(SUBSTR(REPLACE(REPLACE(a.`content`,'\n',''),'\r',''),1,30),'...') as content, \n" 
					     +"a.`readUserId`,a.`property`,a.status \n"
					     +" FROM `k_proclamation` a  \n"
					     +" LEFT JOIN k_user b ON a.userId=b.Id \n"
					     +" LEFT JOIN k_department c ON a.departmentId = c.autoId \n"
					     +" WHERE 1=1 and a.userId = '"+userSession.getUserId()+"' and (a.endGoDate>CURDATE() or a.endGoDate is null or a.endGoDate='' ) and status <>'已作废'  ${title} ${content} ${publishDate} ${up} ${status} ${ctype} ";//and a.userId = '"+userSession.getUserId()+"'
			
			pp.setTableID("proclamationList");
			pp.setCustomerId("");
			pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			pp.setOrderBy_CH("publishDate");
			pp.setDirection("desc");
			
			pp.setColumnWidth("8,20,15,10,6,13");
			pp.setSQL(ppSql);
			
			pp.addColumn("发布人", "userId");
			pp.addColumn("标题", "title");
			pp.addColumn("类型", "ctype");
			pp.addColumn("发布部门", "departmentId");
			pp.addColumn("公告状态", "status");
			//pp.addColumn("内容", "content");
			pp.addColumn("发布时间", "publishDate");
			
			pp.addSqlWhere("ctype", " and a.ctype ='${ctype}' ");
			pp.addSqlWhere("status"," and a.status = '${status}'");
			pp.addSqlWhere("up"," and a.up='${up}' ");
			pp.addSqlWhere("title"," and a.title like '%${title}%' ");
			pp.addSqlWhere("content"," and a.content like '%${content}%' ");
			//pp.addSqlWhere("publishDate"," and a.publishDate like '%${publishDate}%' ");
			pp.addSqlWhere("publishDate"," and a.publishDate >= '${beginDate} 00:00:00' and a.publishDate<='${endDate} 24:00:00'");
			
 			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		}
		
		return modelAndView;
	}
	
	/**
	 * 已审批的列表
	 * @param request
	 * @param response
	 * @return
	 */
	private static String finishList ="process/proclamationSyProcess/finishProclamationList.jsp";
	public ModelAndView finishProclamationList(HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(finishList);
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
		DataGridProperty pp = new DataGridProperty(); 
		try {	
			
			
			String ppSql=" SELECT DISTINCT a.`uuid`,a.`title`,c.departname AS departmentId,a.ctype,b.name AS `userId`, \n"
					     +" a.publishDate,CONCAT(SUBSTR(REPLACE(REPLACE(a.`content`,'\n',''),'\r',''),1,30),'...') as content, \n" 
					     +"a.`readUserId`,a.`property`,a.status \n"
					     +" FROM `k_proclamation` a  \n"
					     +" LEFT JOIN k_user b ON a.userId=b.Id \n"
					     +" LEFT JOIN k_department c ON a.departmentId = c.autoId \n"
					     +" left join j_proclamationprocss d on a.uuid = d.proclamationId "
					     +" left join j_processform e on d.ProcessInstanceId = e.processInstanseId "
					     +" WHERE 1=1 and e.dealUserId = '"+userSession.getUserId()+"' and status ='已审批'  ${title} ${content} ${publishDate}";
			
			ppSql=" SELECT DISTINCT a.`uuid`,a.`title`,c.departname AS departmentId,a.ctype,b.name AS `userId`, \n"
				     +" a.publishDate,CONCAT(SUBSTR(REPLACE(REPLACE(a.`content`,'\n',''),'\r',''),1,30),'...') as content, \n" 
				     +"a.`readUserId`,a.`property`,a.status \n"
				     +" FROM `k_proclamation` a  \n"
				     +" LEFT JOIN k_user b ON a.userId=b.Id \n"
				     +" LEFT JOIN k_department c ON a.departmentId = c.autoId \n"
				     +" left join j_proclamationprocss d on a.uuid = d.proclamationId "
				     +" left join mt_jbpm_processdeploy e on d.ProcessInstanceId = e.pdid "
				     +" WHERE 1=1 and e.updateuser = '"+userSession.getUserId()+"' and status ='已审批'  ${title} ${content} ${publishDate}";
			
			pp.setTableID("finishProclamationList");
			pp.setCustomerId("");
			pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			pp.setOrderBy_CH("publishDate");
			pp.setDirection("desc");
			
			pp.setColumnWidth("10,20,15,10,13");
			pp.setSQL(ppSql);
			
			pp.addColumn("发布人", "userId");
			pp.addColumn("标题", "title");
			pp.addColumn("类型", "ctype");
			pp.addColumn("发布部门", "departmentId");
			//pp.addColumn("公告状态", "status");
			//pp.addColumn("内容", "content");
			pp.addColumn("发布时间", "publishDate");
			
			
			pp.addSqlWhere("title"," and a.title like '%${title}%' ");
			pp.addSqlWhere("content"," and a.content like '%${content}%' ");
			pp.addSqlWhere("publishDate"," and a.publishDate like '%${publishDate}%' ");
			
 			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		}
		
		return modelAndView;
	}
 
	/**
	 * 新增跳转
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView addSkip(HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(ADDSKIP);
		
		return modelAndView;
	}
	
	/**
	 * 新增
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView add(HttpServletRequest request, HttpServletResponse response){
		
		ASFuntion asf = new ASFuntion();
		
		String title = asf.showNull(request.getParameter("title"));
		String content = asf.showNull(request.getParameter("content"));
		String fileName = asf.showNull(request.getParameter("fileName"));
		String ctype = asf.showNull(request.getParameter("ctype"));
		String up=request.getParameter("up");
		String upDatesStr=request.getParameter("upDates");
		Integer upDates=Integer.valueOf(upDatesStr); 
		String goDate=request.getParameter("goDate");
		String endGoDate=request.getParameter("endGoDate");
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
		
		String	publishDate= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format( new Date());
		
		String uuid = UUID.randomUUID().toString();
		Proclamation proclamation = new Proclamation();
		proclamation.setUuid(uuid);
		proclamation.setTitle(title);
		proclamation.setContent(content.replace("“", "'").replace("”", "'").replace("\"", "'"));
		proclamation.setFileName(fileName);
		proclamation.setPublishDate(publishDate);
		proclamation.setUserId(userSession.getUserId());
		proclamation.setDepartmentId(userSession.getUserAuditDepartmentId());
		proclamation.setCtype(ctype);
		proclamation.setUp(up);
		proclamation.setUpDates(upDates);
		proclamation.setGoDate(goDate);
		proclamation.setEndGoDate(endGoDate);
		Connection conn = null;
	
		try{
			conn = new DBConnect().getConnect();
			
			ProclamationService proclamationService = new ProclamationService(conn);
			
			boolean result = proclamationService.add(proclamation);
			
			response.sendRedirect(request.getContextPath()+"/proclamationSy.do?method=list");
		}catch (Exception e) {
			System.out.println("收件公告错误啦："+e.getMessage());
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	
	/**
	 * 启动流程
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	public ModelAndView startFlow(HttpServletRequest request, HttpServletResponse response) throws IOException{
		// 获取登录的用户
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		String userId = userSession.getUserId();
		Connection conn = null;
		ASFuntion asf = new ASFuntion();
		ModelAndView modelAndView = new ModelAndView();
		try {
				conn = new DBConnect().getConnect();
				String uuid = request.getParameter("uuid");
				ProclamationService proclamationService = new ProclamationService(conn);
				Map<String, String> startMap = new HashMap<String, String>();
				UserService userService = new UserService(conn);
				DepartmentService departmentService = new DepartmentService(conn);
				User user = userService.getUser(userId,"id");
				DepartmentVO dVO = departmentService.getVo(Integer.parseInt(user.getDepartmentid()));
				
				//总所
				/*String sql = "SELECT b.departId,b.departName FROM k_department a \n" 
							+"LEFT JOIN k_organ b ON a.parentid = b.departid \n"
							+"WHERE a.autoId = '"+dVO.getAutoId()+"' AND level0 = '0'";
				String directDepartnames = new ASFuntion().showNull(proclamationService.getValueBySql(sql));*/
				
				String sql = "SELECT b.name FROM k_department a LEFT JOIN k_area b ON a.areaid = b.autoid WHERE a.autoid='"+userSession.getUserAuditDepartmentId()+"' ";
				String departName = new DbUtil(conn).queryForString(sql);
				String placeName = ""; //类型(总所/分所)
				//String[] departIds = directDepartnames.split("@`@");
				//String departId = "";
				if(departName.indexOf("北京")>-1){
					placeName = "总所";
					//departId = departIds[0];  //总所ID
			
				}else{
					//查分所ID
					/*sql =  "SELECT a.autoId,a.departName FROM k_department a \n" 
							+"WHERE a.autoId = '"+dVO.getAutoId()+"' AND (level0 <> '0' OR level0 IS NULL OR level0 ='')";
					String partDepartname = new ASFuntion().showNull(proclamationService.getValueBySql(sql));
					//departId = partDepartname.split("@`@")[0]; //分所编号*/
					placeName = "分所";
					
				}
				startMap.put("proposer", userId);
				startMap.put("placeName", placeName); //总所/分所
				//startMap.put("departId", departId);   //（总/分）所编号
				startMap.put("transactUser", "19");
		
				// 启动流程
				String pdId = proclamationService.getPdidByKey("proclamationFlow");
				modelAndView.addObject("pdId",pdId);
		
				// 启动流程
				ProcessInstance pi =jbpmTemplate.startProcessById(pdId, startMap);
		
				// 获取节点任务
				TaskQuery tq = jbpmTemplate.getTaskService().createTaskQuery();
				List<Task> taskList2 = tq.processInstanceId(pi.getId()).list();
				Task myTask = taskList2.get(0);
		
				ProclamationFlow proclamationFlow = new ProclamationFlow();
				proclamationFlow.setProcessInstanceId(pi.getId());
				proclamationFlow.setProclamationId(uuid);
				proclamationFlow.setApplyuser(startMap.get("proposer"));
				proclamationFlow.setApplyDate(asf.getCurrentDate()+" "+asf.getCurrentTime());
				proclamationFlow.setState(myTask.getName());
				proclamationFlow.setProperty("");
		
				// 根据节点 ID 把下个节点的执行人 传到下一个节点
				jbpmTemplate.setTaskVariables(myTask.getId(), startMap);
				modelAndView.addObject("taskId",myTask.getId());
				
				ProcessFormService processFormService = new ProcessFormService(conn);

				String activeName = jbpmTemplate.getActivityName(myTask.getId()); // 获取当前节点
				ProcessForm processForm = new ProcessForm();
				processForm.setProcessInstanseId(pdId);
				processForm.setKey("公告流程发起");
				processForm.setValue("成功发起流程");
				processForm.setNodeName(activeName);
				processForm.setDealUserId(userId);
				processForm.setDealTime(new ASFuntion().getCurrentDate()+" "+new ASFuntion().getCurrentTime());
				processForm.setDealUserId(userId);
				processForm.setProperty("");
				processFormService.add(processForm);// 添加记录(流程表)
				
				// 完成节点
				jbpmTemplate.completeTask(myTask.getId());
		
				proclamationService.addProclamationProcss(proclamationFlow);// 添加合同流程 (自己的表)
				
				sql = "UPDATE k_proclamation SET `status`  = '已启动' WHERE `uuid` = '"+uuid+"'";
				proclamationService.UpdateValueBySql(sql); //修改状态
				
				
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			response.sendRedirect(request.getContextPath()+"/proclamationSy.do?method=list");
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
	public ModelAndView updateSkip(HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(UPDATESKIP);
		
		String uuid = request.getParameter("uuid");
		String opt = request.getParameter("opt"); //用来判断跳转的
		Connection conn = null;
		try {
			
			conn=new DBConnect().getConnect();
			
			ProclamationService proclamationService = new ProclamationService(conn);
			
			Proclamation proclamation = proclamationService.getProclamation(uuid);
		
			modelAndView.addObject("proclamation",proclamation);
			
			modelAndView.addObject("opt",opt);

		} catch (Exception e) {
			
			System.out.println("得到公告信息出错："+e.getMessage());
		
		}finally{
			DbUtil.close(conn);
		}
		
		
		return modelAndView;
	}
	
	/**
	 * 查看公告
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView look(HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(VIEW);
		
		String uuid = request.getParameter("uuid");
		String opt = request.getParameter("opt");
		String myLook = request.getParameter("myLook");
		modelAndView.addObject("opt", opt);
		modelAndView.addObject("myLook", myLook);
		Connection conn = null;
		try {
			
			conn=new DBConnect().getConnect();
			
			ProclamationService proclamationService = new ProclamationService(conn);
			
			Proclamation proclamation = proclamationService.getCNProclamation(uuid);
		
			modelAndView.addObject("proclamation",proclamation);
			
		} catch (Exception e) {
			
			System.out.println("得到公告信息出错："+e.getMessage());
		
		}finally{
			DbUtil.close(conn);
		}
		
		
		return modelAndView;
	}

	/**
	 * 修改
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView update(HttpServletRequest request, HttpServletResponse response){
		
		ASFuntion asf = new ASFuntion();
		
		String uuid = asf.showNull(request.getParameter("uuid"));
		String title = asf.showNull(request.getParameter("title"));
		String content = asf.showNull(request.getParameter("content"));
		String fileName = asf.showNull(request.getParameter("fileName"));
		String ctype = asf.showNull(request.getParameter("ctype"));
		String opt = asf.showNull(request.getParameter("opt"));
		String up=request.getParameter("up");
		String goDate=request.getParameter("goDate");
		String endGoDate=request.getParameter("endGoDate");
		String taskId = asf.showNull(request.getParameter("taskId"));
		if("".equals(up) || up==null){
			up="不置顶";
		}
		String upDatesStr=request.getParameter("upDates");
		
		Integer upDates=Integer.valueOf(upDatesStr); 
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
		
		Proclamation proclamation = new Proclamation();
		proclamation.setUuid(uuid);
		proclamation.setTitle(title);
		proclamation.setContent(content.replace("“", "'").replace("”", "'").replace("\"", "'"));
		proclamation.setFileName(fileName);
		proclamation.setUserId(userSession.getUserId());
		proclamation.setCtype(ctype);
		proclamation.setDepartmentId(userSession.getUserAuditDepartmentId());
		proclamation.setUp(up);
		proclamation.setUpDates(upDates);
		proclamation.setGoDate(goDate);
		proclamation.setEndGoDate(endGoDate);
		Connection conn = null;
	
		try{
			conn = new DBConnect().getConnect();
			
			ProclamationService proclamationService = new ProclamationService(conn);
			
			proclamationService.update(proclamation);
			
			if(!"".equals(opt)){
				response.sendRedirect(request.getContextPath()+"/proclamationSy.do?method=finishProclamationList");
			}else{
				if(!"".equals(taskId)){
					
					String activeName = jbpmTemplate.getActivityName(taskId); // 获取当前节点
					
					String sql = "UPDATE k_proclamation SET `status`  = '驳回已修改' WHERE `uuid` = '"+uuid+"'";
					proclamationService.UpdateValueBySql(sql); //修改状态
					
					sql = "UPDATE j_proclamationprocss SET `proclamationId`  = '"+activeName+"' WHERE `uuid` = '"+uuid+"'";
					proclamationService.UpdateValueBySql(sql); //修改状态
					
					String pdId = jbpmTemplate.getProcessInstanceId(taskId);

					// 完成节点
					jbpmTemplate.completeTask(taskId);
					ProcessFormService processFormService = new ProcessFormService(conn);
					
					ProcessForm processForm = new ProcessForm();
					processForm.setProcessInstanseId(pdId);
					processForm.setKey("备注");
					processForm.setValue("发起人修改");
					processForm.setNodeName(activeName);
					processForm.setDealUserId(userSession.getUserId());
					processForm.setDealTime(new ASFuntion().getCurrentDate()+" "+new ASFuntion().getCurrentTime());
					processForm.setProperty("");

					processFormService.add(processForm);// 添加记录
				}
					response.sendRedirect(request.getContextPath()+"/proclamationSy.do?method=list");
			}
		}catch (Exception e) {
			System.out.println("公告修改错误啦："+e.getMessage());
		}finally{
			DbUtil.close(conn);
		}
		return null;
	}
	
	/**
	 * 删除
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws Exception{
		Connection conn = null;
		
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		ASFuntion asf = new ASFuntion();
		String uuid = asf.showNull(request.getParameter("uuid"));
		
		try {
			conn = new DBConnect().getConnect();
			
			
			if(!"".equals(uuid)){
				ProclamationService proclamationService = new ProclamationService(conn);
				AttachService attachService = new AttachService(conn);
				Proclamation proclamation = proclamationService.getProclamation(uuid);
				

				attachService.remove(INDEXPAGE, proclamation.getFileName()); // 删除文件
				
				boolean result = proclamationService.delete(uuid);
				System.out.println("删除公告否成功："+result);
				 
			} 
			
			response.sendRedirect(request.getContextPath()+"/proclamationSy.do?method=list");
			
		} catch (IOException e) {
			
			System.out.println("删除公告错误："+e.getMessage());
		}finally{
			DbUtil.close(conn);
		}
		
		return null;
	}
	
	/**
	 * 审批list
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView auditList(HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(AUDITLIST);
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
		DataGridProperty pp = new DataGridProperty(); 
		try {	
			
			
			String ppSql="	 SELECT a.*,c.title,d.departname,e.name,c.publishDate FROM ( \n"
						+"SELECT DISTINCT a.DBID_,b.ID_,b.ACTIVITYNAME_,GROUP_CONCAT(c.userID_) AS auditUserId,GROUP_CONCAT(d.name) AS auditName  \n"
						+"FROM jbpm4_task a  \n"
						+"INNER JOIN jbpm4_execution b ON a.EXECUTION_ID_ = b.ID_   \n"
						+"LEFT JOIN  jbpm4_participation c ON a.DBID_ = c.TASK_  AND c.type_ = 'candidate'  \n"
						+"LEFT JOIN k_user d ON c.userId_ = d.id \n"
						+"WHERE "+userSession.getUserId()+" LIKE CONCAT(c.userId_,'%')  \n"
						+" GROUP BY A.EXECUTION_ID_   ) a  \n"
						+"INNER JOIN `j_proclamationprocss` b ON a.id_ = b.ProcessInstanceId "
						+"inner JOIN k_proclamation c ON b.proclamationId = c.uuid \n"
						+"LEFT JOIN k_department d ON c.departmentId = d.autoId \n"
						+"LEFT JOIN k_user e ON c.userid = e.id \n"
						+" where 1=1 and (c.status = '已启动' or c.status='驳回已修改') ${title} ${name} ${content} ${content}";		
			pp.setTableID("proclamationAuditList");
			pp.setCustomerId("");
			pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			pp.setOrderBy_CH("publishDate");
			//pp.setDirection("desc");
			
			pp.setColumnWidth("10,10,10,15,13,10,15");
			pp.setSQL(ppSql);
			
			//pp.addColumn("发布人", "userId");
			//pp.addColumn("发布部门", "departmentId");
		//	pp.addColumn("流程实例ID", "DBID_");
			pp.addColumn("审批人", "auditName");
			pp.addColumn("标题", "title");
			pp.addColumn("申请人", "name");
			pp.addColumn("申请部门", "departname");
			pp.addColumn("申请时间", "publishDate");
			pp.addColumn("流程状态", "ACTIVITYNAME_");
			
			
			pp.addSqlWhere("title"," and c.title like '%${title}%' ");
			pp.addSqlWhere("name"," and e.name like '%${name}%' ");
			pp.addSqlWhere("content"," and c.content like '%${content}%' ");
			pp.addSqlWhere("publishDate"," and c.publishDate like '%${publishDate}%' ");
			
 			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		}
		
		return modelAndView;
	}
	
	/**
	 * 审批时跳转
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView auditSkip(HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = null;
		Connection conn = null;
		try {
			ASFuntion asf = new ASFuntion();
			// 获取taskID
			String taskId = request.getParameter("taskId");
			System.out.println("taskId========：" + taskId);
			// 根据taskId 获取流程实例Id
			String pdid = jbpmTemplate.getProcessInstanceId(taskId);
			
			String activeName = jbpmTemplate.getActivityName(taskId); // 获取当前节点
			if("撰稿人拟稿".equals(activeName) || activeName.indexOf("拟稿")>-1){
				modelAndView = new ModelAndView(UPDATESKIP);
			}else{
				modelAndView = new ModelAndView(AUDITTREEPAGE);
			}
			
			String departId = (String) jbpmTemplate.getVariable(taskId,"departId");
			
			conn = new DBConnect().getConnect();
			
			String uuid  = asf.showNull(new DbUtil(conn).queryForString("SELECT proclamationId FROM j_proclamationprocss WHERE ProcessInstanceId = '"+pdid+"'"));
			if(!"".equals(uuid)){
				ProclamationService pms = new ProclamationService(conn);
				Proclamation proclamation = pms.getProclamation(uuid);
				modelAndView.addObject("proclamation", proclamation);
			}
			//************************************************有配置form才会有用
			// 根据taskid获取 jbpm的form
			//String form = jbpmTemplate.getFormResourceName(taskId);

			// 当form 为空时， 调到修改页面，
		//	if (("").equals(form) || form == null) {
				//String ordersId = orderService.getOrdersIdByTaskId(taskId);

				//Order order = orderService.getOrderById(ordersId);
				//List<OrderDog> listOrderDog = orderService.getListODog(order.getOrdersId());
				//modelAndView.addObject("taskId", taskId);
				//modelAndView.addObject("order", order);
				//modelAndView.addObject("listOrderDog", listOrderDog);
				//modelAndView.addObject("pdid", pdid);


				//	} else {
				//		modelAndView = new ModelAndView();
				//		modelAndView.setViewName("proclamation/" + form);
				//	}
			modelAndView.addObject("taskId", taskId);
			modelAndView.addObject("pdid", pdid);
			modelAndView.addObject("departId", departId);
			return modelAndView;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("公告出错  action:" + e.getMessage());
		} finally {
		}
		return modelAndView;
		
	}
	
	/**
	 * 已驳回修改跳转
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView rejectUpdateSkip(HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(UPDATESKIP);;
		Connection conn = null;
		try {
			ASFuntion asf = new ASFuntion();
			// 获取
			String uuid = asf.showNull(request.getParameter("uuid"));
			
			if(!"".equals(uuid)){
				
				conn = new DBConnect().getConnect();
				
				UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
				
				String sql = "SELECT a.DBID_ FROM (  \n"
							+"SELECT DISTINCT a.DBID_,b.ID_,b.ACTIVITYNAME_,GROUP_CONCAT(c.userID_) AS auditUserId,GROUP_CONCAT(d.name) AS auditName \n"  
							+"FROM jbpm4_task a   \n"
							+"INNER JOIN jbpm4_execution b ON a.EXECUTION_ID_ = b.ID_   \n" 
							+"LEFT JOIN  jbpm4_participation c ON a.DBID_ = c.TASK_  AND c.type_ = 'candidate'  \n" 
							+"LEFT JOIN k_user d ON c.userId_ = d.id  \n"
							+"WHERE  "+userSession.getUserId()+" LIKE CONCAT(c.userId_,'%')   \n"
							+" GROUP BY A.EXECUTION_ID_   ) a   \n"
							+"INNER JOIN `j_proclamationprocss` b ON a.id_ = b.ProcessInstanceId  \n"
							+" WHERE proclamationId = '"+uuid+"'";
				
				String taskId = new DbUtil(conn).queryForString(sql);
				
				// 根据taskId 获取流程实例Id
				String pdid = jbpmTemplate.getProcessInstanceId(taskId);
				
				String activeName = jbpmTemplate.getActivityName(taskId); // 获取当前节点
				
				String departId = (String) jbpmTemplate.getVariable(taskId,"departId");
				
				ProcessFormService pfs = new ProcessFormService(conn);
				List nodeList = pfs.getNodeList(pdid);
				modelAndView.addObject("nodeList", nodeList);
				
				
				ProclamationService pms = new ProclamationService(conn);
				Proclamation proclamation = pms.getProclamation(uuid);

				modelAndView.addObject("proclamation", proclamation);
				modelAndView.addObject("taskId", taskId);
				modelAndView.addObject("pdid", pdid);
				modelAndView.addObject("departId", departId);
				modelAndView.addObject("activeName", activeName);
			}
			return modelAndView;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("公告出错  action:" + e.getMessage());
		} finally {
		}
		return modelAndView;
		
	}
	
	/**
	 * 审批<iframe>详情
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView auditDetails(HttpServletRequest request, HttpServletResponse response){
		
		Connection con = null;
		ModelAndView modelAndView = new ModelAndView(AUDITLOOKPAGE);
		try {
			con = new DBConnect().getConnect();

			ProclamationService proclamationService = new ProclamationService(con);
			ProcessFormService pfs = new ProcessFormService(con);
			// 获取taskID
			String pdid = request.getParameter("pdid");

			List nodeList = pfs.getNodeList(pdid);
			
			String sql = "select proclamationId from j_proclamationprocss where ProcessInstanceId='"+pdid+"'";
			String proclamationIds = proclamationService.getValueBySql(sql); //得到公告ID
			String [] proclamationId = proclamationIds.split("@`@");
			Proclamation proclamation =proclamationService.getCNProclamation(proclamationId[0]);
			 
			modelAndView.addObject("pdid", pdid);
			modelAndView.addObject("nodeList", nodeList);
			modelAndView.addObject("proclamation", proclamation);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("公告详情出错  action:" + e.getMessage());
		} finally {
			DbUtil.close(con);
		}
		return modelAndView;
		
	}

	/**
	 * 审批完成
	 * @param req
	 * @param res
	 * @return
	 */
	public ModelAndView auditTransit(HttpServletRequest req,
			HttpServletResponse res) {
		Connection con = null;

		try {
			con = new DBConnect().getConnect();

			ProcessFormService processFormService = new ProcessFormService(con);
			ProclamationService proclamationService = new ProclamationService(con);
			PlacardService placardService = new PlacardService(con);
			//InteriorEmailAction inEmailAction = new InteriorEmailAction();
			ASFuntion  asf = new ASFuntion();
			
			UserSession userSession = (UserSession) req.getSession().getAttribute("userSession");
			String mobilePhoneMsg = asf.showNull(req.getParameter("mpMsg"));
			String psContent = asf.showNull(req.getParameter("content")); //公告内容
			String taskId = asf.showNull(req.getParameter("taskId"));
			if(!"".equals(taskId)){
				
				try{
					String readUserId = req.getParameter("readUserId").replace("user_","");
					//String pdid = req.getParameter("pdid"); 这个是页面传过来的
					String pdId = jbpmTemplate.getProcessInstanceId(taskId);
					String titleValue = req.getParameter("titleValue");
					if (titleValue == null || titleValue == "") {
						titleValue = "审批通过";
					}
					
					String activeName = jbpmTemplate.getActivityName(taskId); // 获取当前节点
					// 活跃的名称
					String userId = userSession.getUserId();
					ProcessForm processForm = new ProcessForm();
					processForm.setProcessInstanseId(pdId);
					processForm.setKey("备注");
					processForm.setValue(titleValue);
					processForm.setNodeName(activeName);
					processForm.setDealUserId(userId);
					processForm.setDealTime(new ASFuntion().getCurrentDate()+" "+new ASFuntion().getCurrentTime());
					processForm.setProperty("");
					
					processFormService.add(processForm);// 添加记录
					
					String sql = "select proclamationId from j_proclamationprocss where ProcessInstanceId='"+pdId+"'";
					String proclamationIds = proclamationService.getValueBySql(sql); //得到公告ID
					
					String [] proclamationId = proclamationIds.split("@`@");
					sql = "UPDATE k_proclamation SET `status`  = '已审批',readUserId='"+readUserId+"' WHERE `uuid` = '"+proclamationId[0]+"'";
					proclamationService.UpdateValueBySql(sql); //修改状态
					
					sql = "UPDATE j_proclamationprocss SET `proclamationId`  = '"+activeName+"' WHERE `uuid` = '"+proclamationId[0]+"'";
					proclamationService.UpdateValueBySql(sql); //修改状态
					if(!"".equals(psContent)){
						sql = "UPDATE k_proclamation SET `content`  = '"+psContent+"' WHERE `uuid` = '"+proclamationId[0]+"'";
						proclamationService.UpdateValueBySql(sql); //修改状态
					}
					Proclamation proclamation = proclamationService.getProclamation(proclamationId[0]);
					String todayTime = asf.getCurrentDate()+" "+asf.getCurrentTime();
					
					String content = "您有一条公告通知，标题为：【"+proclamation.getTitle()+"】，" +
					"发布时间为：【"+todayTime+"】。请您及时阅读！";
					//给老大发信息
					PlacardTable placardTable=new PlacardTable(); 
					placardTable.setAddresser(userSession.getUserId());//发起
					placardTable.setAddresserTime(todayTime);
					placardTable.setCaption("公告通知提醒");
					placardTable.setMatter(content);
					
					// 查看详情
					placardTable.setUrl("proclamationSy.do?method=look&opt=true");
					placardTable.setUuidName("uuid");
					placardTable.setUuid(proclamation.getUuid());
					placardTable.setModel("公告通知");
					String [] userIds = null;
					
					if("allUser".equals(readUserId)){
						String id = new DbUtil(con).queryForString("SELECT group_concat(id) from k_user where state=0 ");
						userIds = id.split(",");
					}else{
						userIds = readUserId.split(",");
					}
					for (int i = 0; i < userIds.length; i++) {
						
						placardTable.setAddressee(userIds[i]); //接收的老大UserId
						if("是".equals(mobilePhoneMsg)){
							placardTable.setMpShortMessage("是");
							placardTable.setMpContent(content);
						}
						
							placardService.AddPlacard(placardTable); //记录人发消息
						
					}
				}catch(Exception e){
					
				}finally{
					
					// 完成节点
					jbpmTemplate.completeTask(taskId);
					
					res.sendRedirect(req.getContextPath()+ "/proclamationSy.do?method=auditList");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("审批通过出错：" + e.getMessage());
		} finally {
			DbUtil.close(con);
		}

		return null;
	}

	/**
	 * 驳回
	 * @param req
	 * @param res
	 * @return
	 */
	public ModelAndView auditReject(HttpServletRequest req,
			HttpServletResponse res) {
		Connection con = null;

		try {
			con = new DBConnect().getConnect();

			ProcessFormService processFormService = new ProcessFormService(con);
			ProclamationService proclamationService = new ProclamationService(con);
			
			UserSession userSession = (UserSession) req.getSession().getAttribute("userSession");
			
			String taskId = req.getParameter("taskId");
			if(!"".equals(taskId)){
				
				String pdId = jbpmTemplate.getProcessInstanceId(taskId);
				String titleValue = req.getParameter("titleValue");
				if (titleValue == null || titleValue == "") {
					titleValue = "审批不通过";
				}
				
				String activeName = jbpmTemplate.getActivityName(taskId); // 获取当前节点
				// 活跃的名称
				String userId = userSession.getUserId();
				ProcessForm processForm = new ProcessForm();
				processForm.setProcessInstanseId(pdId);
				processForm.setKey("审批备注");
				processForm.setValue(titleValue);
				processForm.setNodeName(activeName);
				processForm.setDealUserId(userId);
				processForm.setDealTime(new ASFuntion().getCurrentDate()+" "+new ASFuntion().getCurrentTime());
				processForm.setProperty("");
				
				processFormService.add(processForm);// 添加记录
				
				String sql = "select proclamationId from j_proclamationprocss where ProcessInstanceId='"+pdId+"' limit 1 ";
				String proclamationId = new DbUtil(con).queryForString(sql); //得到公告ID
				
				sql = "UPDATE k_proclamation SET `status`  = '已驳回' WHERE `uuid` = '"+proclamationId+"'";
				proclamationService.UpdateValueBySql(sql); //修改状态
				
				sql = "UPDATE j_proclamationprocss SET `proclamationId`  = '"+activeName+"' WHERE `uuid` = '"+proclamationId+"'";
				proclamationService.UpdateValueBySql(sql); //修改状态
				
				// 完成节点
				jbpmTemplate.completeTask(taskId,"驳回");
			}
			 
			res.sendRedirect(req.getContextPath()+ "/proclamationSy.do?method=auditList");

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("审批通过出错：" + e.getMessage());
		} finally {
			DbUtil.close(con);
		}

		return null;
	}
	
	/**
	 * 公告审批tree
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void getTree(HttpServletRequest request, HttpServletResponse response)throws Exception {
		response.setContentType("text/html;charset=utf-8");
		Connection conn = null;
		try {
			ASFuntion CHF=new ASFuntion();
			
			String checked = CHF.showNull(request.getParameter("checked"));
			String addUser = CHF.showNull(request.getParameter("addUser"));//部门
			String departid = CHF.showNull(request.getParameter("departid"));//部门
			System.out.println("部门编号："+departid);
			conn = new DBConnect().getConnect();
			DepartmentService ds = new DepartmentService(conn);
			ProclamationService prs = new ProclamationService(conn);
			ds.setAddUser(addUser);
			List list = list = new ArrayList();
			
/*			Map map = new HashMap();
			map.put("isSubject","3");//用于标志：当前节目的类型
			map.put("cls","folder");
			map.put("leaf",false);
			map.put("id","depart_0") ;
			map.put("departid","");
			map.put("areaid","");
			map.put("departname","无部门");
			map.put("bparentid","");
			map.put("checked","true".equals(checked));
			map.put("text","无部门人员");*/
			
			String sql = "SELECT a.parentid,a.areaid FROM k_department a \n"
						+"INNER JOIN k_department b ON b.fullpath LIKE CONCAT(a.fullpath,'%') \n"
						+"WHERE a.parentid='"+departid+"' AND a.level0='1'"; 
			
			String parentidAndAreaid = CHF.showNull(prs.getValueBySql(sql));
			if(!"".equals(parentidAndAreaid)){
				parentidAndAreaid = parentidAndAreaid.replace("@`@", ",");
				parentidAndAreaid = parentidAndAreaid.substring(0, parentidAndAreaid.length()-1);
				String[] pad = parentidAndAreaid.split(",");
				
				list = ds.getDepartment(pad[0], "", checked);
			}
			List list1 = ds.getUser(departid, checked);
			System.out.println("sss"+list1);
			if(list1 != null){
				if(list == null) list = new ArrayList();
				for(int i = 0;i<list1.size(); i++){
					list.add(list1.get(i));
				}
			}
			
			String json = "{}";
			if(list != null){
				json = JSONArray.fromObject(list).toString();
			}
			System.out.println("json="+json);
			response.getWriter().write(json);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
	}
	
	/**
	 * 得到公告状态
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getStatus(HttpServletRequest request, HttpServletResponse response){
		
		Connection con = null;
		try {
			response.setCharacterEncoding("utf-8");
			con = new DBConnect().getConnect();
			PrintWriter out = response.getWriter();
			ProclamationService proclamationService = new ProclamationService(con);
			// 获取taskID
			String uuid = request.getParameter("uuid");
			
			String sql = "select status from k_proclamation where uuid='"+uuid+"'";
			String proclamationIds = proclamationService.getValueBySql(sql); //得到公告ID
			String [] proclamationId = proclamationIds.split("@`@");
			out.write(proclamationId[0]);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("得到公告状态出错  action:" + e.getMessage());
		} finally {
			DbUtil.close(con);
		}
		return null;
		
	}
	
	/**
	 * 作废 公告
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView updateStatus(HttpServletRequest request, HttpServletResponse response){
		
		Connection con = null;
		try {
			response.setCharacterEncoding("utf-8");
			con = new DBConnect().getConnect();
			PrintWriter out = response.getWriter();
			ProclamationService proclamationService = new ProclamationService(con);
			// 获取taskID
			String uuid = request.getParameter("uuid");
			
			String 	sql = "UPDATE k_proclamation SET `status`  = '已作废' WHERE `uuid` = '"+uuid+"'";
			boolean result = proclamationService.UpdateValueBySql(sql); //得到公告ID
			
			String folg = "false";
			if(result){
				folg = "true";
			}
			
			out.write(folg);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("作废公告状态出错  action:" + e.getMessage());
		} finally {
			DbUtil.close(con);
		}
		return null;
		
	}
	
	/**
	 * 我可以查看的公告通知
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView MyLookProclamationlist(HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(MyLookProclamationList);
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
		DataGridProperty pp = new DataGridProperty(); 
		try {	
			
			
			String ppSql=" SELECT a.`uuid`,a.`title`,c.departname AS departmentId,b.name AS `userId`, \n"
					     +" a.publishDate,CONCAT(SUBSTR(REPLACE(REPLACE(a.`content`,'\n',''),'\r',''),1,30),'...') as content, \n" 
					     +"a.`readUserId`,a.`property`,a.status \n"
					     +" FROM `k_proclamation` a  \n"
					     +" LEFT JOIN k_user b ON a.userId=b.Id \n"
					     +" LEFT JOIN k_department c ON a.departmentId = c.autoId \n"
					     +" WHERE 1=1 and (a.endGoDate>CURDATE() or a.endGoDate is null or a.endGoDate='' ) and (concat(a.readUserId,',') like concat('%"+userSession.getUserId()+",','%') or 19 = "+userSession.getUserId()+" or a.readUserId='allUser') and status ='已审批'  ${title} ${content} ${publishDate}";
			
			pp.setTableID("MyLookProclamationList");
			pp.setCustomerId("");
			pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			pp.setOrderBy_CH("up");
			pp.setDirection("desc");
			
			pp.setColumnWidth("10,20,15,10,10");
			pp.setSQL(ppSql);
			
			pp.addColumn("发布人", "userId");
			pp.addColumn("标题", "title");
			pp.addColumn("发布部门", "departmentId");
			pp.addColumn("公告状态", "status");
			//pp.addColumn("内容", "content");
			pp.addColumn("发布时间", "publishDate");
			
			
			pp.addSqlWhere("title"," and a.title like '%${title}%' ");
			pp.addSqlWhere("content"," and a.content like '%${content}%' ");
			pp.addSqlWhere("publishDate"," and a.publishDate like '%${publishDate}%' ");
			
 			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		}
		
		return modelAndView;
	}
	
}
