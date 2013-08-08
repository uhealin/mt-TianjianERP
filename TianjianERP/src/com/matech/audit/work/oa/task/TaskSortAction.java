package com.matech.audit.work.oa.task;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.oa.task.TaskService;
import com.matech.audit.service.oa.task.model.Task;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.UTILString;

/**
 * <p>Title: 任务分类管理</p>
 * <p>Description: 任务分类管理</p>
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
 * 2007-7-3
 */
public class TaskSortAction extends MultiActionController {

	private static final String LIST_VIEW = "task/sortList.jsp";

	private static final String ADD_VIEW = "task/sortEdit.jsp";

	private static final String EDIT_VIEW = "task/sortEdit.jsp";

	/**
	 * 任务分类列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(LIST_VIEW);

		ASFuntion asfFunction = new ASFuntion();
		HttpSession session = request.getSession();

		String workflowId = request.getParameter("workflowId");
		if(workflowId == null || "".equals(workflowId)) {
			workflowId = "0";
		}
		String parentTaskId = asfFunction.showNull(request.getParameter("parentTaskId"));

		DataGridProperty dgProperty = new DataGridProperty();
		dgProperty.setTableID("taskSortList");
		dgProperty.setCurProjectDatabase(false);
		dgProperty.setInputType("radio");
		dgProperty.setWhichFieldIsValue(1);

		dgProperty.setPrintEnable(true);
		dgProperty.setPrintColumnWidth("20,40,40,20,20");
		dgProperty.setPrintTitle("底稿分类列表");

		dgProperty.addColumn("任务编号", "TaskCode");
		dgProperty.addColumn("任务名称", "TaskName");
		dgProperty.addColumn("任务概述", "taskcontent");
		dgProperty.addColumn("对应科目", "subjectName");
		dgProperty.addColumn("备注", "description");
		dgProperty.addColumn("是否必做", "ismust");

		if("".equals(parentTaskId)){
			parentTaskId = "0";
		}

		String sql = " select taskid,orderid,taskcode,taskname,taskcontent,description,subjectName,if(ismust like '%1%','必做','非必做') as ismust "
					+ " from jbpm_z_task "
					+ " where projectid='" + workflowId + "'"
					+ " and parenttaskid='" + parentTaskId + "'"
					+ " and isleaf=0";

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
		modelAndView.setViewName(ADD_VIEW);

		Connection conn = null;
		ASFuntion asf = new ASFuntion();

		try {

			//取得用户Session
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session.getAttribute("userSession");

			String parentTaskId = asf.showNull(request.getParameter("parentTaskId"));
			String curProjectId = userSession.getCurProjectId();

			conn = new DBConnect().getConnect("");
			TaskService taskService = new TaskService(conn,curProjectId);

			String taskCode = "";

			if ("".equals(parentTaskId)) {
				parentTaskId = "0";
			}

			taskCode = taskService.getMaxTaskCodeByParentTaskId(parentTaskId, 0);
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

		String taskId = "";

		try {
			String workflowId = request.getParameter("workflowId");

			if(workflowId == null || "".equals(workflowId)) {
				workflowId = "0";
			}

			conn = new DBConnect().getConnect("");

			TaskService taskService = new TaskService(conn,workflowId);

			if(("").equals(parentTaskId)){
				parentTaskId = "0";
			}

			//查询z_Task表得到层次
			taskId = taskService.getMaxTaskId();

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
			task.setProperty(request.getParameter("property"));
			task.setSubjectName(request.getParameter("subjectName"));
			task.setIsLeaf(0); //IsLeaf 的值，暂时为0, 0为不是叶子  1为叶子

			taskService.addTask(task);

			String ismust = request.getParameter("ismust");

			//如果是必做
			if("yes".equals(ismust)) {
				taskService.setTaskState(taskId, TaskService.TASK_STATE_CODE_MUST, true);
			} else {
				taskService.setTaskState(taskId, TaskService.TASK_STATE_CODE_MUST, false);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		String url = "/AuditSystem/oa/task.do?method=list&refresh=true&parentTaskId=" + taskId;
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
		modelAndView.setViewName(EDIT_VIEW);

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

			modelAndView.addObject("property",task.getProperty());
			modelAndView.addObject("taskCode",task.getTaskCode());
			modelAndView.addObject("taskId",task.getTaskId());
			modelAndView.addObject("taskName",task.getTaskName());
			modelAndView.addObject("subjectName",task.getSubjectName());
			modelAndView.addObject("taskContent",task.getTaskContent());
			modelAndView.addObject("description",task.getDescription());
			modelAndView.addObject("parentTaskId", task.getParentTaskId());

			String ismust = taskService.getTaskState(taskId, TaskService.TASK_STATE_CODE_MUST) ? "yes" : "no";

			//给JSP页面判断
			modelAndView.addObject("ismust", ismust);

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

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(EDIT_VIEW);

		Connection conn = null;
		ASFuntion asf = new ASFuntion();

		String taskId = asf.showNull(request.getParameter("taskId"));
		String taskCode = request.getParameter("taskCode");
		String taskName = request.getParameter("taskName");
		String taskContent = request.getParameter("taskContent");
		String description = request.getParameter("description");
		String property = request.getParameter("property");
		String subjectName = request.getParameter("subjectName");

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
			task.setProperty(property);
			task.setSubjectName(subjectName);

			taskService.updateTask(task);


			String ismust = request.getParameter("ismust");

			//如果是必做
			if("yes".equals(ismust)) {
				taskService.setTaskState(taskId, TaskService.TASK_STATE_CODE_MUST, true);
			} else {
				taskService.setTaskState(taskId, TaskService.TASK_STATE_CODE_MUST, false);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		String url = "/AuditSystem/oa/taskSort.do?method=list&refresh=true&parentTaskId=" + parentTaskId + "&taskId=" + taskId;
		response.sendRedirect(url);

		return null;
	}
}
