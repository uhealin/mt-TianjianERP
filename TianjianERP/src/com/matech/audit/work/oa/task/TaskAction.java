package com.matech.audit.work.oa.task;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.oa.procedure.ProcedureService;
import com.matech.audit.service.oa.target.TargetService;
import com.matech.audit.service.oa.task.TaskService;
import com.matech.audit.service.oa.task.TaskTreeService;
import com.matech.audit.service.oa.task.model.Task;
import com.matech.audit.service.user.UserService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.sys.UTILSysProperty;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.service.print.PrintSetup;

/**
 * <p>Title: 底稿任务处理 </p>
 * <p>Description: 底稿任务处理</p>
 * <p>Copyright: Copyright (c) 2006, 2008 MaTech Corporation.All rights reserved.</p>
 * <p>Company: Matech 广州铭太信息科技有限公司</p>
 *
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有，
 * 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 *
 * 贡献者团队: 铭太科技 - 研发中心，审计开发组
 *
 * @author void 2007-6-26
 */
public class TaskAction extends MultiActionController {


	private static final String TASK_VIEW = "task/manager.jsp"; // 任务管理主框架

	private static final String BAR_VIEW = "task/bar.jsp"; // bar页面

	private static final String SEARCH_FORM_VIEW = "task/searchForm.jsp"; // 搜索表单

	private static final String LEFT_VIEW = "task/left.jsp"; // 左框架页面

	private static final String LIST_VIEW = "task/list.jsp"; // 任务列表

	private static final String TREE_VIEW = "task/tree.jsp"; // 底稿树列表

	private static final String PRINT_VIEW = "Excel/tempdata/PrintandSave.jsp";

	private static final String TASK_PROCEDURE_TARGET_VIEW = "task/tarAndProList.jsp";

	public ModelAndView bar(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(BAR_VIEW);
		return modelAndView;
	}

	public ModelAndView left(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(LEFT_VIEW);
		return modelAndView;
	}

	/**
	 * 跳转到任务管理主框架
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView manager(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(TASK_VIEW);
		return modelAndView;
	}

	/**
	 * 底稿列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView();


		ASFuntion asfFunction = new ASFuntion();
		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session.getAttribute("userSession");

		String curProjectId = request.getParameter("projectId");

		if (curProjectId == null || "".equals(curProjectId)) {
			curProjectId = "0";
		}

		String parentTaskId = asfFunction.showNull(request.getParameter("parentTaskId"));
		String userId = userSession.getUserId();

		Connection conn = null;

		try {
			conn = new DBConnect().getConnect("");
			String fullPath = new TaskService(conn,curProjectId).getFullPath(parentTaskId);
			modelAndView.addObject("fullPath", fullPath);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		DataGridProperty dgProperty = new DataGridProperty();
		dgProperty.setTableID("jbpm_taskList");
		dgProperty.setCurProjectDatabase(false);
		dgProperty.setInputType("radio");
		dgProperty.setWhichFieldIsValue(2);

		dgProperty.addColumn("任务编号", "TaskCode");
		dgProperty.addColumn("任务名称", "TaskName");
		dgProperty.addColumn("编制人", "User1");
		dgProperty.addColumn("编制时间", "date1","showDate:yyyy-MM-dd");
		dgProperty.addColumn("退回时间", "date4","showDate:yyyy-MM-dd");
		dgProperty.addColumn("一级复核人", "User5");
		dgProperty.addColumn("一级复核时间", "date5","showDate:yyyy-MM-dd");
		dgProperty.addColumn("二级复核人", "User2");
		dgProperty.addColumn("二级复核时间", "date2","showDate:yyyy-MM-dd");
		dgProperty.addColumn("三级复核人", "User3");
		dgProperty.addColumn("三级复核时间", "date3","showDate:yyyy-MM-dd");
		dgProperty.addColumn("最后保存人", "UserName");
		dgProperty.addColumn("最后保存时间", "Udate");
		dgProperty.setTrActionProperty(true);
		dgProperty.setTrAction("id='tr${taskId}' fullPath='${fullpath}' taskId='${taskId}' manuId='${manuid}' style='cursor:hand;' onDBLclick=\"openTaskFile(this);\"");

		StringBuffer sql = new StringBuffer();
		sql.append(" select a.manuid,TaskID,orderid,fullpath,TaskCode,TaskName,");
		sql.append(" u1.name as user1,date1, ");
		sql.append(" date4, ");
		sql.append(" u5.name as user5,date5, ");
		sql.append(" u2.name as User2,date2, ");
		sql.append(" u3.name as user3,date3, ");
		sql.append(" case when ismust like '%1%' and user1 is not null and user1 <>'' then '必作已做' ");
		sql.append(" when ismust like '%1%' and (user1 is null ||user1 ='') then '必作未做' end as ismustdo,a.UserName,a.Udate ");
		sql.append(" From jbpm_z_Task a ");
		sql.append(" left join k_user u1 on a.user1 = u1.id ");
		sql.append(" left join k_user u2 on a.user2 = u2.id ");
		sql.append(" left join k_user u3 on a.user3 = u3.id ");
		sql.append(" left join k_user u5 on a.user5 = u5.id ");
		sql.append(" Where a.ProjectId='" + curProjectId + "'");
		sql.append(" and IsLeaf=1 ");
		sql.append(" and (a.property not like 'A%' or a.property is null) ");

		if("mytask".equals(parentTaskId)) {

			dgProperty.addColumn("必作项状态",  "ismustdo");
			sql.append(" and a.User0=" + userId);

		}else if("0".equals(parentTaskId)){

			dgProperty.addColumn("必作项状态",  "ismustdo");

		}else if(parentTaskId != null && !"".equals(parentTaskId) && !"null".equals(parentTaskId)){

			sql.append(" and a.ParentTaskID ='" + parentTaskId + "' ");
		}

		dgProperty.setSQL(sql.toString());
		dgProperty.setOrderBy_CH("orderid");
		dgProperty.setDirection("asc");
		session.setAttribute(DataGrid.sessionPre + dgProperty.getTableID(),dgProperty);

		modelAndView.addObject("parentTaskId", parentTaskId);
		modelAndView.addObject("projectId", curProjectId);
		modelAndView.setViewName(LIST_VIEW);
		return modelAndView;
	}

	/**
	 * 底稿树
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView tree(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(TREE_VIEW);

		String taskId = request.getParameter("taskId");
		Connection conn = null;

		try {

			String workflowId = request.getParameter("workflowId");
			String isLeaf = request.getParameter("isLeaf");

			conn = new DBConnect().getConnect("");
			TaskTreeService taskTreeService = new TaskTreeService(conn,"0");

			if("false".equals(isLeaf)) {
				taskTreeService.setIsleaf(false);
			}
			if (taskId == null) {
				taskId = "0";
			}
			modelAndView.addObject("taskId", taskId);
			modelAndView.addObject("workflowId", workflowId);
			modelAndView.addObject("treeScript", taskTreeService.getScript(taskId));
			modelAndView.addObject("tree", taskTreeService.getSubTree(taskId));

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		return modelAndView;
	}

	/**
	 * 获取子树
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getSubTree(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String taskId = request.getParameter("parentTaskId");
		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		PrintWriter out = response.getWriter();

		Connection conn = null;

		try {
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession)session.getAttribute("userSession");

			String curProjectId = request.getParameter("projectId");
			String isLeaf = request.getParameter("isLeaf");

			if (curProjectId == null || "".equals(curProjectId)) {
				curProjectId = userSession.getCurProjectId();
			}

			conn = new DBConnect().getConnect("");
			TaskTreeService taskTreeService = new TaskTreeService(conn,curProjectId);

			if("false".equals(isLeaf)) {
				taskTreeService.setIsleaf(false);
			}

			if (taskId == null) {
				taskId = "0";
			}

			out.write(taskTreeService.getSubTree(taskId));

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 获得普通审计的审计程序和目标
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView tarAndPro(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(TASK_PROCEDURE_TARGET_VIEW);
		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		Connection conn = null;

		try {
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession)session.getAttribute("userSession");

			String curProjectId = request.getParameter("projectId");

			if (curProjectId == null || "".equals(curProjectId)) {
				curProjectId = userSession.getCurProjectId();
				curProjectId = "0";
			}

			String userId = userSession.getUserId();
			String parentTaskId = request.getParameter("parentTaskId");

			//如果没有parentTaskId
			if(parentTaskId == null || "".equals(parentTaskId)) {
				//PrintWriter out = response.getWriter();
				//out.print("请选择结点");
				//return null;
				parentTaskId = "0";
			}

			conn = new DBConnect().getConnect("");
			TargetService targetService = new TargetService(conn,curProjectId);
			ProcedureService procedureService = new ProcedureService(conn,curProjectId);
			TaskService taskService = new TaskService(conn,curProjectId);

			List targetList = targetService.getTargetListByTaskId(parentTaskId);
			List procedureList = new ArrayList();

			procedureService.getProcedureListByTaskId(procedureList,parentTaskId,"0");
			Task task = taskService.getTaskByTaskId(parentTaskId);

			UserService userService = new UserService(conn);

			String role = UTILSysProperty.SysProperty.getProperty("允许修改程序和目标的角色");

			boolean isManager = true;

			if(role != null && !"".equals(role) ) {
				isManager = userService.validateUserRole(userId, role);
			}

			modelAndView.addObject("targetList", targetList);
			modelAndView.addObject("procedureList", procedureList);
			modelAndView.addObject("taskName", task.getTaskName());
			modelAndView.addObject("taskCode", task.getTaskCode());
			modelAndView.addObject("parentTaskId", parentTaskId);
			modelAndView.addObject("isManager", String.valueOf(isManager));

			if(parentTaskId != null && !parentTaskId.equals("")) {
				DataGridProperty dgProperty2 = new DataGridProperty();
				dgProperty2.setTableID("jpbm_correlationTaskList");
				dgProperty2.setCurProjectDatabase(false);

				dgProperty2.addColumn("任务编号", "TaskCode");
				dgProperty2.addColumn("任务名称", "TaskName");
				dgProperty2.addColumn("编制人", "User1");
				dgProperty2.addColumn("编制时间", "date1","showDate:yyyy-MM-dd");
				dgProperty2.addColumn("退回时间", "date4");
				dgProperty2.addColumn("一级复核人", "User5");
				dgProperty2.addColumn("一级复核时间", "date5","showDate:yyyy-MM-dd");
				dgProperty2.addColumn("二级复核人", "User2");
				dgProperty2.addColumn("二级复核时间", "date2","showDate:yyyy-MM-dd");
				dgProperty2.addColumn("三级复核人", "User3");
				dgProperty2.addColumn("三级复核时间", "date3","showDate:yyyy-MM-dd");
				dgProperty2.addColumn("最后保存人", "UserName");
				dgProperty2.addColumn("最后保存时间", "Udate");
				dgProperty2.setTrActionProperty(true);
				dgProperty2.setTrAction("fullPath='${fullpath}' taskId='${taskId}' manuId='${manuid}' style='cursor:hand;' onDBLclick=\"openTaskFile(this);\"");

				StringBuffer sql2 = new StringBuffer();
				sql2.append(" select DISTINCT t.TaskCode,t.manuid,t.fullpath,TaskName,");
				sql2.append(" u1.name as User1,date1, ");
				sql2.append(" date4, ");
				sql2.append(" u5.name as User5,date5, ");
				sql2.append(" u2.name as User2,date2, ");
				sql2.append(" u3.name as User3,date3, ");
				sql2.append(" t.UserName,t.Udate,t.TaskID,t.orderid " );
				sql2.append(" from jbpm_z_procedure p, jbpm_z_Task t " );
				sql2.append(" left join k_user u1 on t.user1 = u1.id ");
				sql2.append(" left join k_user u2 on t.user2 = u2.id ");
				sql2.append(" left join k_user u3 on t.user3 = u3.id ");
				sql2.append(" left join k_user u5 on t.user5 = u5.id ");
				sql2.append(" where t.isleaf=1" );
				sql2.append(" and p.TaskID = '" + parentTaskId + "'" );
				sql2.append(" and t.ProjectID = '" + curProjectId + "'" );
				sql2.append(" and p.Manuscript like concat('%,',t.taskcode,',%')");
				sql2.append(" and t.projectid= p.projectid" );

				dgProperty2.setTitle("相关底稿列表：");

				dgProperty2.setSQL(sql2.toString());
				dgProperty2.setOrderBy_CH("orderid");
				dgProperty2.setDirection("asc");
				modelAndView.addObject("correlationTaskList","yes");
				session.setAttribute(DataGrid.sessionPre + dgProperty2.getTableID(),dgProperty2);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		return modelAndView;
	}


	/**
	 * 搜索表单
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView searchForm(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(SEARCH_FORM_VIEW);
		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session.getAttribute("userSession");

		String curProjectProperty = userSession.getCurProjectProperty();
		modelAndView.addObject("curProjectProperty", curProjectProperty);
		modelAndView.addObject("searchTitle", "底稿查找");
		return modelAndView;
	}

	/**
	 * 打印程序和目标
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView printTarAndPro(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(PRINT_VIEW);
		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		ASFuntion asf = new ASFuntion();
		String taskId = asf.showNull(request.getParameter("taskId"));

		//取得用户Session
		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session.getAttribute("userSession");

		String curProjectId = request.getParameter("projectId");

		if (curProjectId == null || "".equals(curProjectId)) {
			curProjectId = userSession.getCurProjectId();
		}

		//被审计单位名
		String curDepartName = userSession.getCurCustomerName();

		//当前事务所名
		String curAuditDeptName = userSession.getUserAuditOfficeName();

		Connection conn = null;

		try {
			conn = new DBConnect().getConnect("");


			String taskName = new TaskService(conn, curProjectId).getTaskNameByTaskId(taskId);

			//审计目标
			String sql1 = "select '" + taskName +"', audittarget,correlationexeprocedure,if(executeit=1,'是','否') as executeit,state from jbpm_z_target where "
							+ " projectid = '" + curProjectId + "'"
							+ " and audittarget > '' "
							+ " and taskid = '" + taskId + "'" ;

			//审计程序
			String sql2 = "select '" + taskName +"', auditprocedure,manuscript,executor,state from jbpm_z_procedure where "
							+ " projectid = '" + curProjectId + "'"
							+ " and auditprocedure > '' "
							+ " and taskid = '" + taskId + "'" ;

			new DBConnect().changeDataBaseByProjectid(conn, curProjectId);

			Map varMap = new HashMap();
			varMap.put("curAuditDeptName", curAuditDeptName);
			varMap.put("curDepartName", curDepartName);

			PrintSetup printSetup = new PrintSetup(conn);
			printSetup.setStrQuerySqls(new String[]{sql1,sql2});
			printSetup.setVarMap(varMap);
			printSetup.setCharColumn(new String[]{"1`2`3`4`5","1`2`3`4`5}"});
			printSetup.setStrExcelTemplateFileName("TaskAndPro.xls");

			String filename = printSetup.getExcelFile();

			modelAndView.addObject("refresh","");

			modelAndView.addObject("saveasfilename", "实质性测试审计程序表");
			modelAndView.addObject("bVpage","true");
			modelAndView.addObject("filename", filename);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		return modelAndView;
	}

	/**
	 * 返回比当前程序优先需要完成的程序autoId
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getNoFinishProcedure(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		PrintWriter out = response.getWriter();

		Connection conn = null;

		try {
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession)session.getAttribute("userSession");

			String curProjectId = request.getParameter("projectId");

			if (curProjectId == null || "".equals(curProjectId)) {
				curProjectId = userSession.getCurProjectId();
			}

			String autoId = request.getParameter("autoId");

			conn = new DBConnect().getConnect("");
			String result = new ProcedureService(conn,curProjectId).getNoFinishProcedure(autoId);

			if (result == null) {
				result = "ok";
			}

			out.write(result);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 检查TASKCODE是否唯一
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView checkIsOnly(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Connection conn = null;
		ASFuntion asf = new ASFuntion();

		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);

		PrintWriter out = response.getWriter();

		try {
			//取得用户Session
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session.getAttribute("userSession");

			String taskCode = asf.showNull(request.getParameter("taskCode"));

			String curProjectId = request.getParameter("projectId");

			if(curProjectId == null || "".equals(curProjectId)) {
				curProjectId = userSession.getCurProjectId();	//当前项目编号
			}

			conn = new DBConnect().getConnect("");
			TaskService taskService = new TaskService(conn,curProjectId);

			String taskName = taskService.checkIsOnlyByTaskCode(taskCode);

		    if(taskName == null){
		    	out.print("only");
		    }else{
		    	out.print(taskName);
		    }

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 检查TASKCODE是否唯一
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView checkIsOnly2(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Connection conn = null;
		ASFuntion asf = new ASFuntion();

		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);

		PrintWriter out = response.getWriter();

		try {
			//取得用户Session
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session.getAttribute("userSession");

			String taskCode = asf.showNull(request.getParameter("taskCode"));

			String curProjectId = request.getParameter("projectId");

			if(curProjectId == null || "".equals(curProjectId)) {
				curProjectId = userSession.getCurProjectId();	//当前项目编号
			}

			conn = new DBConnect().getConnect("");
			TaskService taskService = new TaskService(conn,curProjectId);

			String taskName = taskService.checkIsOnlyByTaskCode2(taskCode);

		    if(taskName == null){
		    	out.print("only");
		    }else{
		    	out.print(taskName);
		    }

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}
}
