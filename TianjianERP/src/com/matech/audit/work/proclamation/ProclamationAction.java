package com.matech.audit.work.proclamation;

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
import com.matech.audit.service.attach.AttachService;
import com.matech.audit.service.department.DepartmentService;
import com.matech.audit.service.department.model.DepartmentVO;
import com.matech.audit.service.process.JbpmTemplate;
import com.matech.audit.service.process.ProcessFormService;
import com.matech.audit.service.process.model.ProcessForm;
import com.matech.audit.service.proclamation.ProclamationService;
import com.matech.audit.service.proclamation.model.Proclamation;
import com.matech.audit.service.proclamation.model.ProclamationFlow;
import com.matech.audit.service.user.UserService;
import com.matech.audit.service.user.model.User;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class ProclamationAction extends MultiActionController{

	private static final String LIST="proclamation/List.jsp";
	private static final String ADDSKIP="proclamation/AddandEdit.jsp";
	private static final String UPDATESKIP="proclamation/AddandEdit.jsp";
	private static final String VIEW="proclamation/view.jsp";
	private static final String AUDITLIST="proclamation/auditList.jsp"; //审批list
	private static final String AUDITTREEPAGE="proclamation/treeList.jsp"; //审批页面
	private static final String AUDITLOOKPAGE="proclamation/auditView.jsp"; //审批页面
	private static final String INDEXPAGE = "proclamation"; // 附件名
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
			
			
			String ppSql=" SELECT a.`uuid`,a.`title`,c.departname AS departmentId,b.name AS `userId`, \n"
					     +" a.publishDate,CONCAT(SUBSTR(REPLACE(REPLACE(a.`content`,'\n',''),'\r',''),1,30),'...') as content, \n" 
					     +"a.`readUserId`,a.`property`,a.status \n"
					     +" FROM `k_proclamation` a  \n"
					     +" LEFT JOIN k_user b ON a.userId=b.Id \n"
					     +" LEFT JOIN k_department c ON a.departmentId = c.autoId \n"
					     +" WHERE 1=1 and a.userId = '"+userSession.getUserId()+"' and a.status <>'已启动' and a.status !='已驳回'   ${title} ${content} ${publishDate}";
			
			pp.setTableID("proclamationList");  //proclamationDelList 
			pp.setCustomerId("");
			pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			pp.setOrderBy_CH("publishDate");
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
		
		Connection conn = null;
	
		try{
			conn = new DBConnect().getConnect();
			
			ProclamationService proclamationService = new ProclamationService(conn);
			
			boolean result = proclamationService.add(proclamation);
		
			//	//启动流程
			//this.startFlow(request, response, uuid);
			
			response.sendRedirect(request.getContextPath()+"/proclamation.do?method=list");
		}catch (Exception e) {
			System.out.println("收件公告错误啦："+e.getMessage());
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
	public ModelAndView updateSkip(HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelAndView = new ModelAndView(UPDATESKIP);
		
		String uuid = request.getParameter("uuid");
		Connection conn = null;
		try {
			
			conn=new DBConnect().getConnect();
			
			ProclamationService proclamationService = new ProclamationService(conn);
			
			Proclamation proclamation = proclamationService.getProclamation(uuid);
		
			modelAndView.addObject("proclamation",proclamation);
			
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
		 
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession"); 
		
		Proclamation proclamation = new Proclamation();
		proclamation.setUuid(uuid);
		proclamation.setTitle(title);
		proclamation.setContent(content.replace("“", "'").replace("”", "'").replace("\"", "'"));
		proclamation.setFileName(fileName);
		proclamation.setUserId(userSession.getUserId());
		proclamation.setDepartmentId(userSession.getUserAuditDepartmentId());
		
		Connection conn = null;
	
		try{
			conn = new DBConnect().getConnect();
			
			ProclamationService proclamationService = new ProclamationService(conn);
			
			proclamationService.update(proclamation);
			
			response.sendRedirect(request.getContextPath()+"/proclamation.do?method=list");
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
				
				String[] uuids = uuid.split(",");
				for (int i = 0; i < uuids.length; i++) {
					
					Proclamation proclamation = proclamationService.getProclamation(uuids[i]);
					
					
					boolean result = proclamationService.delete(uuids[i]);
					System.out.println("删除公告否成功："+result);
				}
				 
			} 
			
			response.sendRedirect(request.getContextPath()+"/proclamation.do?method=list");
			
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
						+"WHERE "+userSession.getUserId()+" LIKE CONCAT(c.userId_,'%') GROUP BY A.EXECUTION_ID_   \n"
						+") a  \n"
						+"INNER JOIN `j_proclamationprocss` b ON a.id_ = b.ProcessInstanceId "
						+"LEFT JOIN k_proclamation c ON b.proclamationId = c.uuid \n"
						+"LEFT JOIN k_department d ON c.departmentId = d.autoId \n"
						+"LEFT JOIN k_user e ON c.userid = e.id";
								
			pp.setTableID("proclamationAuditList");
			pp.setCustomerId("");
			pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			pp.setOrderBy_CH("publishDate");
			//pp.setDirection("desc");
			
			pp.setColumnWidth("10,10,10,15,10,10,15");
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
			
			
			//pp.addSqlWhere("title"," and a.title like '%${title}%' ");
			//pp.addSqlWhere("content"," and a.content like '%${content}%' ");
			//pp.addSqlWhere("publishDate"," and a.publishDate like '%${publishDate}%' ");
			
 			
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
		try {

			// 获取taskID
			String taskId = request.getParameter("taskId");
			System.out.println("taskId========：" + taskId);
			// 根据taskId 获取流程实例Id
			String pdid = jbpmTemplate.getProcessInstanceId(taskId);
			modelAndView = new ModelAndView(AUDITTREEPAGE);
			String departId = (String) jbpmTemplate.getVariable(taskId,"departId");
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
			
			
			list = ds.getDepartment(departid, "", checked);
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
				String sql = "SELECT b.departId,b.departName FROM k_department a \n" 
							+"LEFT JOIN k_organ b ON a.parentid = b.departid \n"
							+"WHERE a.autoId = '"+dVO.getAutoId()+"' AND level0 = '0'";
				String directDepartnames = new ASFuntion().showNull(proclamationService.getValueBySql(sql));
				
				String placeName = ""; //类型(总所/分所)
				String[] departIds = directDepartnames.split("@`@");
				String departId = "";
				if("".equals(departIds[0])){
					//查分所ID
						sql =  "SELECT a.autoId,a.departName FROM k_department a \n" 
								+"WHERE a.autoId = '"+dVO.getAutoId()+"' AND (level0 <> '0' OR level0 IS NULL OR level0 ='')";
					String partDepartname = new ASFuntion().showNull(proclamationService.getValueBySql(sql));
					departId = partDepartname.split("@`@")[0]; //分所编号
					placeName = "分所";
			
				}else{
					
					placeName = "总所";
					departId = departIds[0];  //总所ID
				}
				startMap.put("proposer", userId);
				startMap.put("placeName", placeName); //总所/分所
				startMap.put("departId", departId);   //（总/分）所编号
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
				proclamationFlow.setApplyDate(new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").format(new Date()));
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
			response.sendRedirect(request.getContextPath()+"/proclamation.do?method=list");
			DbUtil.close(conn);
		}
		return null;

	}
	
}
