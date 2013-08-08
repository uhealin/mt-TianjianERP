package com.matech.audit.work.oa.task;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.procedure.ProcedureService;
import com.matech.audit.service.target.TargetService;
import com.matech.audit.service.task.TaskCommonService;
import com.matech.audit.service.task.TaskService;
import com.matech.audit.service.task.TaskTreeService;
import com.matech.audit.service.task.model.Task;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.UTILString;
import com.matech.framework.service.print.PrintSetup;

/**
 * <p>Title: 风险导向审计的主要ACTION类</p>
 * <p>Description: 装配各页面和负责审计环节的维护</p>
 * <p>Copyright: Copyright (c) 2006, 2008 MaTech Corporation.All rights reserved. </p>
 * <p>Company: Matech  广州铭太信息科技有限公司</p>
 *
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有，
 * 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 *
 * 贡献者团队:
 *     铭太科技 - 研发中心，审计开发组
 *
 * @author void
 * 2007-7-11
 */
public class TacheAction extends MultiActionController {
	private static final String BAR_VIEW = "taskCommon/bar.jsp"; // bar页面

	private static final String SEARCH_FORM_VIEW = "taskCommon/searchForm.jsp"; // 搜索表单

	private static final String LEFT_VIEW = "tache/left.jsp"; // 左框架页面

	private static final String LIST_VIEW = "tache/list.jsp"; // 任务列表

	private static final String TREE_VIEW = "tache/tree.jsp"; // 底稿树列表

	private static final String PROCEDURE_LIST_VIEW = "tache/procedureList.jsp";

	private static final String TACHE_LIST_VIEW = "tache/tacheList.jsp";

	private static final String TACHE_ADD_VIEW = "tache/tacheEdit.jsp";

	private static final String TACHE_EDIT_VIEW = "tache/tacheEdit.jsp";

	private static final String PRINT_VIEW = "Excel/tempdata/PrintandSave.jsp";

	public ModelAndView bar(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(BAR_VIEW);
		return modelAndView;
	}

	/**
	 * 左框架
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView left(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(LEFT_VIEW);
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
		modelAndView.addObject("tache", "tache");
		modelAndView.addObject("curProjectProperty", curProjectProperty);
		modelAndView.addObject("searchTitle", "底稿和目标查找");

		return modelAndView;
	}

	/**
	 * 底稿树
	 *
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
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session.getAttribute("userSession");

			String curProjectId = userSession.getCurProjectId();

			conn = new DBConnect().getConnect("");
			TaskTreeService taskTreeService = new TaskTreeService(conn,curProjectId);

			if (taskId == null) {
				taskId = "0";
			}
			modelAndView.addObject("taskId", taskId);
			modelAndView.addObject("treeScript", taskTreeService.getScript(taskId));
			modelAndView.addObject("tree", taskTreeService.getTacheSubTree(taskId));

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
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getSubTree(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String taskId = request.getParameter("parentTaskId");
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();

		Connection conn = null;

		try {
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session.getAttribute("userSession");

			String curProjectId = userSession.getCurProjectId();

			conn = new DBConnect().getConnect("");
			TaskTreeService taskTreeService = new TaskTreeService(conn,
					curProjectId);

			if (taskId == null) {
				taskId = "0";
			}

			out.write(taskTreeService.getTacheSubTree(taskId));

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}

	public ModelAndView procedureList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);

		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session.getAttribute("userSession");

		request.setCharacterEncoding("utf-8");

		ModelAndView modelAndView = new ModelAndView();
		ASFuntion asf = new ASFuntion();

		Connection conn = null;

		try {
			conn = new DBConnect().getConnect("");

			String targetTaskId = asf.showNull(request.getParameter("taskId"));

			String curProjectId = userSession.getCurProjectId();

			String tacheTaskId = new TaskService(conn, curProjectId).getParentTaskId(targetTaskId);
			if(!"null".equals(targetTaskId)
					&& !"".equals(targetTaskId)
					&& !"mytask".equals(targetTaskId)){

				ProcedureService procedureService = new ProcedureService(conn, curProjectId);
				Set procedureSet = procedureService.getProceduresByTaskId(targetTaskId);

				modelAndView.addObject("procedureList",procedureSet);
				modelAndView.addObject("tacheTaskId",tacheTaskId);
				modelAndView.addObject("targetTaskId",targetTaskId);

				DataGridProperty dgProperty = new DataGridProperty();
				dgProperty.setTableID("correlationTaskList");
				dgProperty.setCurProjectDatabase(true);

				dgProperty.addColumn("任务编号", "TaskCode");
				dgProperty.addColumn("任务名称", "TaskName");
				dgProperty.addColumn("编制人", "User1");
				dgProperty.addColumn("编制时间", "date1","showDate:yyyy-MM-dd");
				dgProperty.addColumn("退回时间", "date4");
				dgProperty.addColumn("一级复核人", "User5");
				dgProperty.addColumn("一级复核时间", "date5","showDate:yyyy-MM-dd");
				dgProperty.addColumn("二级复核人", "User2");
				dgProperty.addColumn("二级复核时间", "date2","showDate:yyyy-MM-dd");
				dgProperty.addColumn("三级复核人", "User3");
				dgProperty.addColumn("三级复核时间", "date3","showDate:yyyy-MM-dd");
				dgProperty.addColumn("最后保存人", "UserName");
				dgProperty.addColumn("最后保存时间", "Udate");
				dgProperty.setTrActionProperty(true);
				dgProperty.setTrAction("fullPath='${fullpath}' taskId='${taskId}' manuId='${manuid}' style='cursor:hand;' onDBLclick=\"openTaskFile(this);\"");

				StringBuffer sql = new StringBuffer();
				sql.append(" select DISTINCT t.TaskCode,t.manuid,t.fullpath,TaskName,");
				sql.append(" u1.name as User1,date1, ");
				sql.append(" date4, ");
				sql.append(" u5.name as User5,date5, ");
				sql.append(" u2.name as User2,date2, ");
				sql.append(" u3.name as User3,date3, ");
				sql.append(" t.UserName,t.Udate,t.TaskID,t.orderid " );
				sql.append(" from z_procedure p, z_Task t " );
				sql.append(" left join k_user u1 on t.user1 = u1.id ");
				sql.append(" left join k_user u2 on t.user2 = u2.id ");
				sql.append(" left join k_user u3 on t.user3 = u3.id ");
				sql.append(" left join k_user u5 on t.user5 = u5.id ");
				sql.append(" where t.isleaf=1" );
				sql.append(" and p.TaskID like '%~" + targetTaskId + "~%'" );
				sql.append(" and t.ProjectID = '" + curProjectId + "'" );
				sql.append(" and p.Manuscript like concat('%,',t.taskcode,',%')");
				sql.append(" and t.projectid= p.projectid" );

				dgProperty.setTitle("相关底稿列表：");

				dgProperty.setSQL(sql.toString());
				dgProperty.setOrderBy_CH("orderid");
				dgProperty.setDirection("asc");
				modelAndView.addObject("correlationTaskList","yes");
				session.setAttribute(DataGrid.sessionPre + dgProperty.getTableID(),dgProperty);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		modelAndView.setViewName(PROCEDURE_LIST_VIEW);
		return modelAndView;
	}

	/**
	 * 审计目标列表和环节下的底稿列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		request.setCharacterEncoding("utf-8");

		ASFuntion asf = new ASFuntion();
		ModelAndView modelAndView = new ModelAndView();

		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session.getAttribute("userSession");

		String curProjectId = userSession.getCurProjectId();
		String parentTaskId = asf.showNull(request.getParameter("parentTaskId"));
		parentTaskId = parentTaskId.equals("") ? "0" : parentTaskId;

		Connection conn = null;

		try {
			conn = new DBConnect().getConnect("");

			TargetService targetService = new TargetService(conn, curProjectId);

			List targetList = null;

			targetList = targetService.getTargetListByParentTaskId(parentTaskId);

			int hasTargetChildren = targetService.hasChildren(parentTaskId,"target") ? 1 : 0;
			int hasTacheChildren = targetService.hasChildren(parentTaskId,"tache") ? 1 : 0;
			int hasChildren = targetService.hasChildren(parentTaskId) ? 1 : 0;

			modelAndView.addObject("hasTargetChildren",new Integer(hasTargetChildren));
			modelAndView.addObject("hasTacheChildren",new Integer(hasTacheChildren));
			modelAndView.addObject("hasChildren",new Integer(hasChildren));
			modelAndView.addObject("targetList",targetList);
			modelAndView.addObject("parentTaskId",parentTaskId);

			String fullPath = new TaskService(conn,curProjectId).getFullPath(parentTaskId);
			modelAndView.addObject("fullPath", fullPath);

			if(hasTacheChildren == 0) {
				DataGridProperty dgProperty = new DataGridProperty();
				dgProperty.setTableID("tacheManukList");
				dgProperty.setCurProjectDatabase(true);
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
				sql.append(" From z_Task a ");
				sql.append(" left join k_user u1 on a.user1 = u1.id ");
				sql.append(" left join k_user u2 on a.user2 = u2.id ");
				sql.append(" left join k_user u3 on a.user3 = u3.id ");
				sql.append(" left join k_user u5 on a.user5 = u5.id ");
				sql.append(" Where a.ProjectId='" + curProjectId + "'");
				sql.append(" and IsLeaf=1 ");
				sql.append(" and (a.property not like 'A%' or a.property is null) ");

				if("0".equals(parentTaskId)){

					dgProperty.addColumn("必作项状态",  "ismustdo");

				}else if(parentTaskId != null && !"".equals(parentTaskId) && !"null".equals(parentTaskId)){

					sql.append(" and a.ParentTaskID ='" + parentTaskId + "' ");
				}

				dgProperty.setSQL(sql.toString());
				dgProperty.setOrderBy_CH("orderid");
				dgProperty.setDirection("asc");
				session.setAttribute(DataGrid.sessionPre + dgProperty.getTableID(),dgProperty);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}


		modelAndView.setViewName(LIST_VIEW);
		return modelAndView;
	}

	/**
	 * 审计环节列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView tacheList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(TACHE_LIST_VIEW);

		ASFuntion asfFunction = new ASFuntion();
		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session.getAttribute("userSession");

		String curProjectId = userSession.getCurProjectId();
		String parentTaskId = asfFunction.showNull(request.getParameter("parentTaskId"));

		DataGridProperty dgProperty = new DataGridProperty();
		dgProperty.setTableID("tacheList");
		dgProperty.setCurProjectDatabase(true);
		dgProperty.setInputType("radio");
		dgProperty.setWhichFieldIsValue(1);

		dgProperty.setPrintEnable(true);
		dgProperty.setPrintColumnWidth("20,40,40,20");
		dgProperty.setPrintTitle("审计环节列表");

		dgProperty.addColumn("任务编号", "TaskCode");
		dgProperty.addColumn("任务名称", "TaskName");
		dgProperty.addColumn("任务概述", "taskcontent");
		dgProperty.addColumn("备注", "description");

		if("".equals(parentTaskId)){
			parentTaskId = "0";
		}

		String sql = " select taskid,orderid,taskcode,taskname,taskcontent,description "
					+ " from z_task "
					+ " where projectid='" + curProjectId + "'"
					+ " and parenttaskid='" + parentTaskId + "'"
					+ " and isleaf=0 "
					+ " and property = 'tache'";

		dgProperty.setSQL(sql.toString());
		dgProperty.setOrderBy_CH("orderid");
		dgProperty.setDirection("asc");
		session.setAttribute(DataGrid.sessionPre + dgProperty.getTableID(),dgProperty);

		modelAndView.addObject("parentTaskId",parentTaskId);

		return modelAndView;
	}

	/**
	 * 添加分类
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(TACHE_ADD_VIEW);

		Connection conn = null;
		ASFuntion asf = new ASFuntion();

		try {

			//取得用户Session
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session.getAttribute("userSession");

			String parentTaskId = asf.showNull(request.getParameter("parentTaskId"));
			String curProjectId = userSession.getCurProjectId();

			conn = new DBConnect().getConnect("");
			TaskCommonService taskCommonService = new TaskCommonService(conn,curProjectId);

			String taskCode = "";

			if ("".equals(parentTaskId)) {
				parentTaskId = "0";
			}

			taskCode = taskCommonService.getMaxTaskCodeByParentTaskId(parentTaskId, 0);
			taskCode = UTILString.getNewTaskCode(taskCode);

			modelAndView.addObject("taskCode", taskCode);
			modelAndView.addObject("parentTaskId", parentTaskId);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		return modelAndView;
	}

	/**
	 * 保存任务
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView save(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;
		ASFuntion asf = new ASFuntion();

		String parentTaskId = asf.showNull(request.getParameter("parentTaskId"));

		try {
			//取得用户Session
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session.getAttribute("userSession");

			String curProjectId = userSession.getCurProjectId();

			conn = new DBConnect().getConnect("");

			TaskService taskService = new TaskService(conn,curProjectId);

			if(("").equals(parentTaskId)){
				parentTaskId = "0";
			}

			//查询z_Task表得到层次
			String taskId = taskService.getMaxTaskId();

			String taskCode = request.getParameter("taskCode");
			String taskName = request.getParameter("taskName");
			String taskContent = request.getParameter("taskContent");
			String description = request.getParameter("description");

			Task task = new Task();

			task.setTaskId(taskId);
			task.setTaskCode(taskCode);
			task.setTaskName(taskName);
			task.setTaskContent(taskContent);
			task.setDescription(description);
			task.setParentTaskId(parentTaskId);
			task.setProperty("tache");
			task.setIsLeaf(0); //IsLeaf 的值，暂时为0, 0为不是叶子  1为叶子

			taskService.addTask(task);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		String url = "tache.do?method=tacheList&parentTaskId=" + parentTaskId;
		response.sendRedirect(url);

		return null;
	}

	/**
	 * 编辑任务信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(TACHE_EDIT_VIEW);

		Connection conn = null;
		ASFuntion asf = new ASFuntion();

		String taskId = asf.showNull(request.getParameter("taskId"));

		try {
			//取得用户Session
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session.getAttribute("userSession");

			String curProjectId = userSession.getCurProjectId();

			conn = new DBConnect().getConnect("");

			TaskService taskService = new TaskService(conn,curProjectId);

			Task task = taskService.getTaskByTaskId(taskId);

			modelAndView.addObject("taskCode",task.getTaskCode());
			modelAndView.addObject("taskId",task.getTaskId());
			modelAndView.addObject("taskName",task.getTaskName());
			modelAndView.addObject("taskContent",task.getTaskContent());
			modelAndView.addObject("description",task.getDescription());
			modelAndView.addObject("parentTaskId", task.getParentTaskId());

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		return modelAndView;
	}

	/**
	 * 更新任务信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView update(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;
		ASFuntion asf = new ASFuntion();

		String taskId = asf.showNull(request.getParameter("taskId"));
		String taskCode = request.getParameter("taskCode");
		String taskName = request.getParameter("taskName");
		String taskContent = request.getParameter("taskContent");
		String description = request.getParameter("description");

		String parentTaskId = request.getParameter("parentTaskId");
		try {
			//取得用户Session
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session.getAttribute("userSession");

			String curProjectId = userSession.getCurProjectId();

			conn = new DBConnect().getConnect("");

			TaskService taskService = new TaskService(conn,curProjectId);

			Task task = taskService.getTaskByTaskId(taskId);

			task.setTaskCode(taskCode);
			task.setTaskName(taskName);
			task.setTaskContent(taskContent);
			task.setDescription(description);

			taskService.updateTask(task);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		String url = "tache.do?method=tacheList&parentTaskId=" + parentTaskId;
		response.sendRedirect(url);

		return null;
	}

	/**
	 * 打印审计目标
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView printTarget(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ASFuntion asf = new ASFuntion();
		String parentTaskId = asf.showNull(request.getParameter("parentTaskId"));

		//取得用户Session
		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session.getAttribute("userSession");
		String projectId = userSession.getCurProjectId();


		Connection conn = null;

		ModelAndView modelAndView = new ModelAndView(PRINT_VIEW);

		try {
			conn = new DBConnect().getConnect("");
			new DBConnect().changeDataBaseByProjectid(conn, projectId);

			if ("".equals(parentTaskId) || "".equals(projectId)) {
				PrintWriter out = response.getWriter();
				out.print("打印参数错误,parentTaskId或projectId不能为空");
				return null;
			}

			String sql1 = "select  a.state,a.defineID,a.auditTarget,CorrelationExeProcedure,if(executeit=0,'未完成','已完成') "
					  + " from z_target a,z_task b "
					  + " where b.property = 'target' "
					  + " and b.FullPath like '%" + parentTaskId + "%'"
					  + " and a.taskid = b.taskid "
					  + " and a.projectid = '" + projectId + "'"
					  + " and b.projectid = '" + projectId + "'"
					  + " order by b.orderid";

			PrintSetup printSetup = new PrintSetup(conn);
			printSetup.setStrTitles(new String[]{"审计目标列表"});
			printSetup.setStrChineseTitles(new String[]{"目标状态`目标编号`目标名称`相关审计程序`是否完成"});
			printSetup.setStrQuerySqls(new String[]{sql1});
			printSetup.setCharColumn(new String[]{"1`2`3`4`5" });
			printSetup.setIColumnWidths(new int[]{12,12,70,20,12});
			printSetup.setVertical(true);

			String filename = printSetup.getExcelFile();

			modelAndView.addObject("refresh","");

			modelAndView.addObject("saveasfilename", "审计目标列表");
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

		ASFuntion asf = new ASFuntion();
		String taskId = asf.showNull(request.getParameter("taskId"));

		//取得用户Session
		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session.getAttribute("userSession");
		String projectId = userSession.getCurProjectId();

		//被审计单位名
		String curDepartName = userSession.getCurCustomerName();

		//当前事务所名
		String curAuditDeptName = userSession.getUserAuditOfficeName();

		Connection conn = null;

		try {
			conn = new DBConnect().getConnect("");


			String taskName = new TaskService(conn, projectId).getTaskNameByTaskId(taskId);

			//审计目标
			String sql1 = "select '" + taskName +"', a.audittarget,a.correlationexeprocedure,if(executeit=1,'是','否') as executeit,a.state "
						+ " from z_target a,z_task b where "
						+ " a.projectid = '" + projectId + "'"
						+ " and a.audittarget > '' "
						+ " and a.projectId=b.projectId "
						+ " and a.taskid=b.taskid "
						+ " and b.parentTaskid = '" + taskId + "'"
						+ " order by a.defineid ";


			//审计程序
			String sql2 = "select '" + taskName +"', a.auditprocedure,a.manuscript,a.executor,a.state "
						+ " from z_procedure a,(select a.taskId,a.projectId "
						+ " 						from z_target a,z_task b where "
						+ " 						a.projectid = '" + projectId + "'"
						+ " 						and a.audittarget > '' "
						+ " 						and a.projectId=b.projectId "
						+ " 						and a.taskid=b.taskid "
						+ " 						and b.parentTaskid = '" + taskId + "') b "
						+ " where a.projectid = '" + projectId + "'"
						+ " and a.projectId=b.projectId "
						+ " and a.auditprocedure > '' "
						+ " and a.taskid like concat('%~',b.taskid,'~%') "
						+ " order by a.defineid ";

			new DBConnect().changeDataBaseByProjectid(conn, projectId);

			Map varMap = new HashMap();
			varMap.put("curAuditDeptName", curAuditDeptName);
			varMap.put("curDepartName", curDepartName);

			PrintSetup printSetup = new PrintSetup(conn);
			printSetup.setStrQuerySqls(new String[]{sql1,sql2});
			printSetup.setVarMap(varMap);
			printSetup.setCharColumn(new String[]{"1`2`3`4`5","1`2`3`4`5}"});
			printSetup.setStrExcelTemplateFileName("targetAndPro.xls");

			String filename = printSetup.getExcelFile();

			modelAndView.addObject("refresh","");

			modelAndView.addObject("saveasfilename", "审计方案");
			modelAndView.addObject("bVpage","false");
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
	 * 打印实质性测试审计程序表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView printProcedure(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(PRINT_VIEW);

		ASFuntion asf = new ASFuntion();
		String targetTaskId = asf.showNull(request.getParameter("targetTaskId"));

		//取得用户Session
		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session.getAttribute("userSession");
		String projectId = userSession.getCurProjectId();

		//被审计单位名
		String curDepartName = userSession.getCurCustomerName();

		//当前事务所名
		String curAuditDeptName = userSession.getUserAuditOfficeName();

		String printAll = asf.showNull(request.getParameter("printAll"));

		Connection conn = null;

		try {
			conn = new DBConnect().getConnect("");


			if ("".equals(targetTaskId) || "".equals(projectId)) {
				PrintWriter out = response.getWriter();
				out.print("打印参数错误,taskId或projectId不能为空");
				return null;
			}

			String sql1 = "select cognizance,AuditProcedure,Executor,Manuscript "
						+ " from z_procedure "
						+ " where taskid like '%~" + targetTaskId + "~%'"
						+ " and projectid = '" + projectId + "'";


			if(printAll.equals("false")) {
				sql1 += " and state='已完成' ";
			}

			sql1 += " order by DefineID";

			String target = new TaskService(conn, projectId).getAuditTarget(targetTaskId);

			new DBConnect().changeDataBaseByProjectid(conn, projectId);
			Map varMap = new HashMap();
			varMap.put("target", target);
			varMap.put("curAuditDeptName", curAuditDeptName);
			varMap.put("curDepartName", curDepartName);

			PrintSetup printSetup = new PrintSetup(conn);
			printSetup.setStrQuerySqls(new String[]{sql1});
			printSetup.setVarMap(varMap);
			printSetup.setCharColumn(new String[] {"1`2`3`4"});
			printSetup.setStrExcelTemplateFileName("tacheProcedure.xls");

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
}
