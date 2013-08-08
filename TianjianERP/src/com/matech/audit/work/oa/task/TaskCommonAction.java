package com.matech.audit.work.oa.task;


import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;

import org.del.JRockey2Opp;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.advice.AdviceService;
import com.matech.audit.service.advice.model.Advice;
import com.matech.audit.service.auditPeople.AuditPeopleService;
import com.matech.audit.service.auditPlaform.AuditPlaformService;
import com.matech.audit.service.auditPlaform.model.AuditConfig;
import com.matech.audit.service.audittypetemplate.AuditTypeTemplateService;
import com.matech.audit.service.audittypetemplate.TreeView;
import com.matech.audit.service.customer.CustomerService;
import com.matech.audit.service.datamanage.BackupUtil;
import com.matech.audit.service.fileupload.MyFileUpload;
import com.matech.audit.service.log.LogService;
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.audit.service.procedure.ProcedureService;
import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.project.model.Project;
import com.matech.audit.service.target.TargetService;
import com.matech.audit.service.task.BatchUploadService;
import com.matech.audit.service.task.TaskCommonService;
import com.matech.audit.service.task.TaskRecycleService;
import com.matech.audit.service.task.TaskService;
import com.matech.audit.service.task.TaskTemplateService;
import com.matech.audit.service.task.model.Task;
import com.matech.audit.service.user.UserService;
import com.matech.audit.service.userState.UserStateService;
import com.matech.audit.service.userState.model.UserState;
import com.matech.audit.service.userdef.UserdefService;
import com.matech.audit.work.system.CommonSecurity;
import com.matech.framework.listener.OnlineListListener;
import com.matech.framework.listener.UserCurTask;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.single.Single;
import com.matech.framework.pub.sys.UTILSysProperty;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.pub.util.UTILString;
import com.matech.framework.service.excelupload.ExcelUploadService;
import com.matech.framework.service.userDisplay.UserDisplayService;

/**
 * <p>
 * Title: 底稿任务公共ACTION
 * </p>
 * <p>
 * Description: 负责底稿的添加,删除,打开等动作
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006, 2008 MaTech Corporation.All rights reserved.
 * </p>
 * <p>
 * Company: Matech 广州铭太信息科技有限公司
 * </p>
 *
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有， 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 *
 * 贡献者团队: 铭太科技 - 研发中心，审计开发组
 *
 * @author void 2007-7-2
 */
public class TaskCommonAction extends MultiActionController {
	//private static final String TACHE_VIEW = "tache/manager.jsp"; // 风险导向主框架

	private static final String TASK_VIEW = "taskCommon/searchList.jsp"; // 任务管理主框架

	private static final String ADD_VIEW = "taskCommon/taskEdit.jsp"; // 新增底稿

	private static final String EDIT_VIEW = "taskCommon/taskEdit.jsp"; // 修改底稿

	private static final String MANU_ERROR_VIEW = "AS_SYSTEM/ManuError.jsp"; // 打开同名的底稿的错误页面

	private static final String OPEN_OCX_VIEW = "taskCommon/fileopenocx.jsp"; // 打开底稿控件的页面

	private static final String SELECT_TASK_VIEW = "taskCommon/selectTask.jsp"; // 选择相关底稿的

	private static final String SELECT_PROCEDURE_VIEW = "taskCommon/selectProcedure.jsp"; // 选择相关底稿的

	private static final String BATCH_AUDITING_LIST_VIEW = "taskCommon/batchAuditingList.jsp";

	private static final String SEARCH_LIST_VIEW = "taskCommon/searchList.jsp";

	private static final String MOVE_VIEW = "taskCommon/move.jsp"; 

	private static final String USER0_TASK_LIST_VIEW = "taskCommon/getTaskByUser0.jsp";

	private static final String PROCEDURE_LIB_VIEW = "taskCommon/procedureLib.jsp";

	private static final String UPLOAD = "/taskCommon/procedureUpload.jsp";

	private static final String MUTIDELETE_LIST_VIEW = "/taskCommon/MutiList.jsp" ;

	private static final String ANNOTATIONS_TASK_LIST_VIEW = "/taskCommon/annotationsTaskList.jsp";

	private static final String ATTACH_ADD_VIEW = "/task/taskAttach.jsp";
	
	
	//申报表页面
	private static final String DECLARE = "taskCommon/declare.jsp";
	
	
	/**
	 * 显示0号模板，属性为template的模板树
	 */
	public void tree(HttpServletRequest request,HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			
			ASFuntion CHF=new ASFuntion();
			
			String checked = CHF.showNull(request.getParameter("checked"));
			String parentTaskId = CHF.showNull(request.getParameter("parentTaskId"));
			String typeId = CHF.showNull(request.getParameter("typeId"));
			String property = CHF.showNull(request.getParameter("property"));
			
			System.out.println("parentTaskId="+parentTaskId+"|typeId="+typeId+"|property="+property);
			
			List list = null;
			TreeView tv = new TreeView(conn);
			list = tv.getExtTree(typeId, parentTaskId, property, checked);
			System.out.println(list);
			response.getWriter().write(JSONArray.fromObject(list).toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		
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

		// 检查是否登陆项目
		if (!new CommonSecurity(request, response).checkProjectLogin()) {
			return null;
		}

		ModelAndView modelAndView = new ModelAndView();
		Connection conn = null;

		try {
			conn = new DBConnect().getConnect("");

			modelAndView.setViewName(TASK_VIEW);

			// 取得用户Session
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session
					.getAttribute("userSession");

			// 获得当前系统的版本,0或者空表示普通版,1表示大事务版,增加审计目标和程序
			String curProjectId = request.getParameter("projectId");

			if (curProjectId == null || "".equals(curProjectId)) {
				curProjectId = userSession.getCurProjectId(); // 当前项目编号
			}

			String userId = userSession.getUserId();
		/*	String curauditTypeProperty = new AuditTypeTemplateService(conn)
					.getPropertyByProjectId(curProjectId);*/

			// 支持从FILEOPENOCX2.JSP打开
			String taskId = request.getParameter("taskId");

			if (taskId == null || taskId.equals("")) {
				// 从用户上次最后访问的底稿目录打开
				UserStateService userStateService = new UserStateService(conn);
				taskId = userStateService.getLastTaskId(userId, curProjectId);
			}
			if (taskId != null) {
				modelAndView.addObject("taskId", taskId);
			}

		/*	if ("1".equals(curauditTypeProperty)) {
				modelAndView.setViewName(TACHE_VIEW);
			}*/

			modelAndView.addObject("curProjectId", curProjectId);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		return modelAndView;
	}

	/**
	 * 添加新的底稿
	 *
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

			// 取得用户Session
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session
					.getAttribute("userSession");

			String parentTaskId = asf.showNull(request
					.getParameter("parentTaskId"));

			String curProjectId = request.getParameter("projectId");

			if (curProjectId == null || "".equals(curProjectId)) {
				curProjectId = userSession.getCurProjectId(); // 当前项目编号
			}

			conn = new DBConnect().getConnect("");
			TaskCommonService taskCommonService = new TaskCommonService(conn,
					curProjectId);

			String taskCode = "";

			if ("".equals(parentTaskId)) {
				taskCode = taskCommonService.getMaxTaskCodeByParentTaskId("0");
				taskCode = UTILString.getNewTaskCode(taskCode);
			} else {
				taskCode = taskCommonService.getMaxTaskCodeByParentTaskId(parentTaskId, 1);
				System.out.println("taskCode：" + taskCode);
				taskCode = UTILString.getNewTaskCode(taskCode);
			}
			
			//改为动态取关注底稿
			AuditPlaformService aps = new AuditPlaformService(conn) ;
			List<AuditConfig> configList = aps.getConfigList() ;
			modelAndView.addObject("list", configList) ;
			
			
			DataGridProperty pp = new DataGridProperty() ;

			// sql设置
			String sql = "select taskid,taskName,Description from k_tasktemplate where typeid='0' and property='template' "
				   + "union select taskid,taskname,Description from z_task where property='A2' and projectid = '"+ curProjectId +"'" ;

			pp.setSQL(sql); 
			//基本设置
			pp.setCancelPage(true) ;
			pp.setCancelBBar(true) ;
			pp.setTableID("templateList") ;
			pp.setPageSize_CH(50);
			pp.setOrderBy_CH("taskid") ;
			pp.setDirection("asc");
			pp.setWhichFieldIsValue(1);
			pp.setInputType("radio");
			pp.addColumn("底稿名称","taskName");
			pp.addColumn("适用情况","Description");
			
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);


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
	 * 编辑底稿信息
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String taskId = request.getParameter("taskId");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(EDIT_VIEW);

		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session
				.getAttribute("userSession");

		String parentTaskId = request.getParameter("parentTaskId");
		String taskType = request.getParameter("taskType");

		String curProjectId = request.getParameter("projectId");

		if (curProjectId == null || "".equals(curProjectId)) {
			curProjectId = userSession.getCurProjectId(); // 当前项目编号
		}

		Connection conn = null;
		ASFuntion CHF = new ASFuntion() ;
		try {
			conn = new DBConnect().getConnect("");
			TaskService taskService = new TaskService(conn, curProjectId);
			Task task = taskService.getTaskByTaskId(taskId);

			modelAndView.addObject("taskCode", task.getTaskCode());
			modelAndView.addObject("newfilename", task.getTaskName());
			modelAndView.addObject("taskproperty", task.getProperty());
			modelAndView.addObject("taskId", task.getTaskId());
			modelAndView.addObject("taskType", taskType);
			modelAndView.addObject("parentTaskId", parentTaskId);
			modelAndView.addObject("subjectName", task.getSubjectName());
			modelAndView.addObject("description", task.getDescription());
			modelAndView.addObject("taskContent", task.getTaskContent());

			String auditproperty = CHF.showNull(task.getAuditproperty());
			
			//改为动态取关注底稿
			AuditPlaformService aps = new AuditPlaformService(conn) ;
			List<AuditConfig> configList = aps.getConfigList() ;
			List<Map> list = new ArrayList<Map>() ;
			for(AuditConfig ac:configList) {
				int id = ac.getId() ;
				Map map = new HashMap() ;
				map.put("stepname",ac.getStepname()) ;
				map.put("id",id) ;
				if (auditproperty.indexOf(id+"") > -1) {
					map.put("checked","checked") ;
				}
				list.add(map) ;
			}
			modelAndView.addObject("list", list) ;
			/*
			if (!"".equals(auditproperty) && auditproperty != null) {
				if (auditproperty.indexOf("1") > -1) {
					modelAndView.addObject("firstChecked", "yes");
				}
				if (auditproperty.indexOf("2") > -1) {
					modelAndView.addObject("secondChecked", "yes");
				}
				if (auditproperty.indexOf("3") > -1) {
					modelAndView.addObject("thirdChecked", "yes");
				}
			}*/
			TaskCommonService taskCommonService = new TaskCommonService(conn,
					curProjectId);
			boolean ismust = taskCommonService.getTaskState(taskId,
					TaskCommonService.TASK_STATE_CODE_MUST);
			if (ismust) {
				modelAndView.addObject("ismust", "yes");
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
	 * 更新底稿信息
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView update(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");

		// 取得用户Session
		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session
				.getAttribute("userSession");

		String curProjectId = request.getParameter("projectId");

		if (curProjectId == null || "".equals(curProjectId)) {
			curProjectId = userSession.getCurProjectId(); // 当前项目编号
		}

		// 任务类型,可以是任务结点，也可以是任务结点，根据不同的值操作完毕后返回不同的页面
		String taskType = request.getParameter("taskType");
		String taskId = request.getParameter("taskId");

		// 来自页面的parentTaskId,是为了操作完毕后返回到原来的页面上
		String parentTaskId = request.getParameter("parentTaskId");

		String fileName = request.getParameter("newfilename");
		String subjectName = request.getParameter("subjectName");
		String taskproperty = request.getParameter("taskproperty");

		String taskCode = request.getParameter("taskcode");
		String taskContent = request.getParameter("taskContent");
		String description = request.getParameter("description");
		String taskAttribute = request.getParameter("taskAttribute");
		String auditproperty = request.getParameter("auditproperty");

		PrintWriter out = response.getWriter();

		Connection conn = null;
		String url = "";

		try {
			conn = new DBConnect().getConnect("");

			String curauditTypeProperty = new AuditTypeTemplateService(conn)
					.getPropertyByProjectId(curProjectId);

			if ("task".equals(taskType)) {
				if ("1".equals(curauditTypeProperty)) {
					// 风险导向审计环节
					url = "tache.do?method=tacheList&refresh=true";
				} else {
					// 普通任务结点
					url = "taskSort.do?method=list&refresh=true";
				}

			} else if ("taskManu".equals(taskType)) {
				if ("1".equals(curauditTypeProperty)) {
					// 风险导向审计底稿
					url = "tache.do?method=list&refresh=true";
				} else {
					// 普通任务底稿
					url = "task.do?method=list&refresh=true";
				}

			}

			// 更新任务表中的底稿信息
			TaskService taskService = new TaskService(conn, curProjectId);
			Task task = taskService.getTaskByTaskId(taskId);

			task.setTaskName(fileName);
			task.setSubjectName(subjectName);
			task.setProperty(taskproperty);
			task.setTaskCode(taskCode);
			task.setOrderId(UTILString.getOrderId(taskCode));
			task.setTaskContent(taskContent);
			task.setDescription(description);
			task.setAuditproperty(auditproperty);
			taskService.updateTask(task);

			TaskCommonService taskCommonService = new TaskCommonService(conn,
					curProjectId);
			if (taskAttribute != null && taskAttribute.equals("1")) {
				taskCommonService.setTaskState(task.getTaskId(),
						TaskCommonService.TASK_STATE_CODE_MUST, true);
			} else {
				taskCommonService.setTaskState(task.getTaskId(),
						TaskCommonService.TASK_STATE_CODE_MUST, false);
			}

			System.out.println("hzh: projectid=" + curProjectId);
			System.out.println("hzh: taskID=" + task.getTaskId());

			// taskService.removeTask(taskId); //执行数据库删除操作
			out.println("<Script>alert(\"更新成功。\");</Script>");

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		url += "&parentTaskId=" + parentTaskId;
		out.println("<Script>window.location='" + url + "';</Script>");

		return null;
	}

	/**
	 * 检查TASKCODE是否唯一
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView checkIsOnly(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;
		ASFuntion asf = new ASFuntion();

		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		PrintWriter out = response.getWriter();

		try {
			// 取得用户Session
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session
					.getAttribute("userSession");

			String taskCode = asf.showNull(request.getParameter("taskCode"));
			String isleaf = asf.showNull(request.getParameter("isleaf"));
			String curProjectId = request.getParameter("projectId");

			if (curProjectId == null || "".equals(curProjectId)) {
				curProjectId = userSession.getCurProjectId(); // 当前项目编号
			}

			conn = new DBConnect().getConnect("");
			TaskCommonService taskCommonService = new TaskCommonService(conn,
					curProjectId);

			if("".equals(isleaf)) {
				isleaf = "1";
			}
			String taskName = taskCommonService.checkIsOnlyByTaskCode(taskCode, isleaf);

			if (taskName == null) {
				out.print("only");
			} else {
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
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView checkIsOnly2(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;
		ASFuntion asf = new ASFuntion();

		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		PrintWriter out = response.getWriter();

		try {
			// 取得用户Session
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session
					.getAttribute("userSession");

			String taskCode = asf.showNull(request.getParameter("taskCode"));

			String curProjectId = request.getParameter("projectId"); 

			if (curProjectId == null || "".equals(curProjectId)) {
				curProjectId = userSession.getCurProjectId(); // 当前项目编号
			}

			conn = new DBConnect().getConnect("");
			TaskCommonService taskCommonService = new TaskCommonService(conn,
					curProjectId);

			String taskName = taskCommonService
					.checkIsOnlyByTaskCode2(taskCode);

			if (taskName == null) {
				out.print("only");
			} else {
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
	 * 上传底稿文件
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView fileUpload(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		PrintWriter out = response.getWriter();

		// 文件的唯一ID

		Connection conn = null;

		try {
			conn = new DBConnect().getConnect("");

			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session
					.getAttribute("userSession");
			if (userSession == null) {
				userSession = new UserSession();

				String userId = request.getParameter("userId");
				String userName = new UserService(conn).getUser(userId, "id")
						.getName();

				userSession.setUserId(userId);
				userSession.setUserName(userName);
				userSession.setCurProjectId(request.getParameter("projectId"));
			}

			// 项目编号
			String projectId = request.getParameter("projectId");
			String taskId = request.getParameter("taskId");
			String typeId = request.getParameter("typeId");

			// 如果是更改项目，则projectId是当前项目ID；
			if (projectId == null || "".equals(projectId)
					|| "null".equals(projectId)) {

				projectId = request.getParameter("curProjectid");

				if (projectId == null || "".equals(projectId)
						|| "null".equals(projectId)) {

					projectId = userSession.getCurProjectId();
				}
			}

			String taskCode = request.getParameter("taskCode");
			
			TaskService taskService = null;			
			if (taskId == null || taskId.equals("")) {
				taskService=new TaskService(conn, projectId);
				if (taskCode != null && !"".equals(taskCode)) {
					taskId = taskService.getTaskIdByTaskCode(taskCode);
					System.out.println("按照底稿编号保存,taskId=" + taskId);
				}
			}

			MyFileUpload fileUpload = new MyFileUpload(request, conn);

			if (typeId != null && !"".equals(typeId)) {
				projectId = typeId;
			}

			if (taskId == null || "".equals(taskId) || "null".equals(taskId)) {
				// 新增
				String customerId = new CustomerService(conn)
						.getCustomerIdByProjectId(projectId);
				String uploadResult = fileUpload.Upload(projectId, customerId);
				if (uploadResult != null) {
					//保存成功后如果是word或者excel就自动打开文件
					Map map = fileUpload.getMap() ;
					Task task = taskService.getTaskByTaskId(uploadResult) ;
					String parentTaskId =task.getParentTaskId() ;
					String fullPath = task.getFullPath() ;
					fullPath = fullPath.replaceAll("\\|","/") ;
					String url = "task.do?parentTaskId="+parentTaskId + "&fullPath="+fullPath ;
					if(((String)map.get("filename")).indexOf(".xls") == -1 && ((String)map.get("filename")).indexOf(".doc") == -1) {
						/*
						out.write("<script type=\"text/javascript\">");
						out.write("alert(\"上传成功！\");");
						out.write("window.close();");
						out.write("</script>");
						out.flush() ;*/
						url += "&type=noOpen&refreshTree=true" ;
					}else {
						/*
						out.write("<script type=\"text/javascript\" src=\"${pageContext.request.contextPath}/AS_INCLUDE/common.js\" charset=\"GBK\"></script>");
						out.write("<script type=\"text/javascript\">");
						out.write("var taskAction = new TaskAction();");
						out.write("taskAction.taskId = " + uploadResult + ";");
						out.write("taskAction.open();");
				//		out.write("window.location=\"taskCommon.do?method=fileOpen&taskId="
				//					+ uploadResult + "&flag=tab&projectId=" + projectId+"\";");
						out.write("</script>");
						//response.sendRedirect("taskCommon.do?method=fileOpen&taskId="
					//				+ uploadResult + "&projectId=" + projectId); */
						url += "&type=open&taskId=" + uploadResult + "&refreshTree=true";
					}
					System.out.println("url:"+url);
					response.sendRedirect(url);
				} else {
					out.write("Error\n文件传输失败");
				}
			} else {

				// 修改
				boolean uploadResult = fileUpload.UploadUpdate(taskId,
						projectId, userSession.getUserName());

				if (uploadResult) {
					out.write("Success\n文件修改成功!");
				} else {
					out.write("Error\n文件传输失败");
				}
			}
		

		} catch (Exception e) {
			out.write("Error\n文件传输失败：" + e.getMessage());
			e.printStackTrace();
			// throw e;
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 底稿批量上传
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView batchUpload(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		PrintWriter out = response.getWriter();

		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session
				.getAttribute("userSession");

		String projectId = request.getParameter("projectId");
		String taskCodeType = request.getParameter("taskCodeType");
		String zero = request.getParameter("zero");
		String followParent = request.getParameter("followParent");
		
		if (projectId == null || "".equals(projectId)) {
			projectId = userSession.getCurProjectId(); // 当前项目编号
		}

		String parentTaskId;
		String fileName;

		Connection conn = null;
		Map parameterMap = null;
		ASFuntion CHF = new ASFuntion() ;
		try {

			conn = new DBConnect().getConnect("");
			String curAuditTypeProperty = new AuditTypeTemplateService(conn)
					.getPropertyByProjectId(projectId);
			String filePath = BackupUtil.getPath(7);

			MyFileUpload myfileUpload = new MyFileUpload(request);
			// 正式上传文件
			myfileUpload.UploadFile(null, filePath);

			parameterMap = myfileUpload.getMap();
			parentTaskId = (String) parameterMap.get("parentTaskId");
			fileName = (String) parameterMap.get("filename");
			String pdId = CHF.showNull((String) parameterMap.get("pdId"));  //当type=syAudit时，是苏亚金诚底稿复核导入底稿用到此功能
			String taskType = (String) parameterMap.get("taskType");  //当type=syAudit时，是苏亚金诚底稿复核导入底稿用到此功能
			String processTaskId = CHF.showNull((String) parameterMap.get("processTaskId"));  
		
			if ("".equals(parentTaskId) || parentTaskId == null) {
				parentTaskId = "0";
			}
			
			TaskService taskService = new TaskService(conn, projectId) ;
			String fullPath = taskService.getTaskByTaskId(parentTaskId).getFullPath() ;
			if(fullPath != null) {
				fullPath = fullPath.replaceAll("\\|","/") ;
			}
			
			BatchUploadService batchUpload = new BatchUploadService(conn,
					projectId);
			
			batchUpload.setTaskCodeType(taskCodeType);
			batchUpload.setZero(zero);
			batchUpload.setFollowParent("yes".equals(followParent));
			
			batchUpload.upload(filePath + fileName, parentTaskId);
			

			out.write("<body style='font-size:12px;'>");
			out.write("导入完毕,正在重算底稿全路径,请稍候...<br/><br/>");

			// 重算底稿全路径
			taskService.repairTaskFullPath();

			String url;
			if ("1".equals(curAuditTypeProperty)) {
				url = "tache.do?method=tacheList&parentTaskId=" + parentTaskId;
			} else {
				url = "task.do?method=list&parentTaskId=" + parentTaskId + "&fullPath="+fullPath+"&refreshTree=true";
			}
			
			if(!"".equals(pdId) || !"".equals(processTaskId)) {
				//苏亚导入时跳回到苏亚那边的界面
				url = request.getContextPath()+"/taskAuditSy.do?method=start&projectId="+projectId+"&pdId="+pdId+"&taskType="+taskType+"&taskId="+processTaskId ;
			}

			out.write("</body>");
			out.write("<script>");
			out.write("		alert('批量导入完成');");
			out.write("		window.location='" + url + "';");
			out.write("</script>");

		} catch (Exception ex) {
			ex.printStackTrace();
			out.write("<script>");
			out.write("		alert('批量导入底稿出错：" + ex.getMessage() + "');");
			out.write("		window.history.back();");
			out.write("</script>");
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}

	/**
	 * 打开底稿文件
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView fileOpen(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		ModelAndView modelAndView = new ModelAndView(OPEN_OCX_VIEW);

		ASFuntion asf = new ASFuntion();

		String taskId = asf.showNull(request.getParameter("taskId"));
		String taskCode = asf.showNull(request.getParameter("taskCode"));
		String isBack = request.getParameter("isBack");
		String flag = request.getParameter("flag") ;

		// 取得用户Session
		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session
				.getAttribute("userSession");

		Connection conn = null;

		String userSessionId = asf.showNull(session.getId());

		try {
			conn = new DBConnect().getConnect("");

			String curProjectId = request.getParameter("projectId");

			if (curProjectId == null || "".equals(curProjectId)) {
				curProjectId = userSession.getCurProjectId(); // 当前项目编号
			}

			String userId = "",userName="";

			if (userSession == null) {
				userSession = new UserSession();

				userId = request.getParameter("userId");
				userName = new UserService(conn).getUser(userId, "id")
						.getName();

				userSession.setUserId(userId);
				userSession.setUserName(userName);
				userSession.setCurProjectId(request.getParameter("projectId"));
			} else {
				userId = userSession.getUserId(); // 用户编号,ID
				userName= userSession.getUserName();
			}
			System.out.println("qwh:userName="+userName);
			
			TaskService taskService = new TaskService(conn, curProjectId);
			Task task = null;
			TaskCommonService taskCommonService = new TaskCommonService(conn,
					curProjectId);

			if (!"".equals(taskId)) {
				task = taskService.getTaskByTaskId(taskId); // 获得当前底稿详细信息
			} else if (!"".equals(taskCode)) {
				task = taskService.getTaskByTaskCodeIsleaf(taskCode); // 获得当前底稿详细信息
			}

			// 判断是否有该底稿存在
			if (task == null) {
				throw new Exception("指定的底稿不存在！taskId=" + taskId + ",taskcode="
						+ taskCode);
			}

			String fileName = task.getTaskName(); // 底稿名称
			if (fileName == null || "".equals(fileName)) {
				throw new Exception("底稿名字不得为空！");
			}

			// 检查当前用户是否有权复核
			boolean hasAudit = new AuditPeopleService(conn, curProjectId)
					.hasAudit(userId); // 当前用户是否有权审核

			ProjectService projectService = new ProjectService(conn);
			Project project = projectService.getProjectById(curProjectId);

			String typeId = request.getParameter("typeId");
			String sheetTaskCode = request.getParameter("sheetTaskCode");
			String curProjectProperty = project.getProperty();
			String curProjectState = "" + project.getState();

		/*	String curAuditTypeProperty = new AuditTypeTemplateService(conn)
					.getPropertyByProjectId(curProjectId);*/
			String curAuditTypeProperty = new ProjectService(conn).getProjectById(curProjectId).getTemplateType() ;

			String user0 = asf.showNull(task.getUser0()); // 分工人
			String user1 = asf.showNull(task.getUser1()); // 编制人
			String user2 = asf.showNull(task.getUser2()); // 二级审核
			String user3 = asf.showNull(task.getUser3()); // 三级审核
			String user4 = asf.showNull(task.getUser4()); // 退回人
			String user5 = asf.showNull(task.getUser5()); // 一级审核
			taskCode = asf.showNull(task.getTaskCode()); // 索引号
			taskId = asf.showNull(task.getTaskId()); // 索引号
			String orderid = asf.showNull(task.getOrderId());// 任务序号

			String taskcheck1 = asf.showNull(task.getTaskContent());
			System.out.println("taskcheck1=" + taskcheck1);
			
			boolean isEditPeopleCanEdit = false ;   //编制人是否始终可以修改底稿
			
			if(curProjectProperty.indexOf("4") >-1) {
				//编制人始终可修改底稿
				isEditPeopleCanEdit = true ;
			}

			// 检查是否有人打开了这个文件，有的话只读打开！
			String userhasopened = "";

			// list存放着所有在线用户的各种信息
			List list = OnlineListListener.getList();

			// 检查是否该文件已经被人打开，并检查本机是否打开了同名文件
			if (!list.isEmpty()) {
				for (int i = 0; i < list.size(); i++) {

					UserSession us = (UserSession) list.get(i);
					Set userCurTasks = us.getUserCurTasks();
					Iterator it = userCurTasks.iterator();

					while (it.hasNext()) {
						UserCurTask userCurTask = (UserCurTask) it.next();
						if (userSessionId.equals(us.getUserSessionId())
								&& (taskCode + fileName).equals(userCurTask
										.getCurTaskCode()
										+ userCurTask.getCurTaskName())) {
							// 如果本机打开了同名文件，就直接导航到错误页面，不允许继续打开。
							response.sendRedirect(MANU_ERROR_VIEW);
							return null;
						}
						if (taskId.equals(userCurTask.getCurTaskId())
								&& curProjectId.equals(userCurTask
										.getCurTaskProjectId())) {
							// 如果已经有人打开了这个文件
							userhasopened = us.getUserName();
							break;
						}
					}
				}
			}

			// 文件的打开方式
			String fileState = "";
			boolean isReadOnly = true;
			boolean canSaveFile = true;

			// 如果没人打开这个底稿
			if (userhasopened.equals("") || userhasopened == null) {
				// 底稿未编制
				if ("".equals(user1)) {
					// -----------------------------------------------
					// 读写方式打开:
					// 首先,底稿未编制完成,以及满足下面3个条件中1个
					//
					// 1.当前用户是分工人
					// 2.该底稿未分工
					// 3.当前用户是退回人
					//
					// 否则都是只读打开
					// -----------------------------------------------
					if (user4.equals(userId) || user0.equals(userId)
							|| user0.equals("")) {
						// 满足读写条件
						isReadOnly = false;
						fileState = "读写方式打开";
					} else {
						// 不满足读写条件
						isReadOnly = true;
						fileState = "你不是底稿分工人,按照只读模式打开";
						canSaveFile = false;
					}
				} else {
					// ------------------------------------
					// 只读方式打开
					// 1.编制完成
					// 2.二级审核通过
					// 3.三级审核通过
					// 4.不是底稿分工人
					// 5.一级审核通过
					// ------------------------------------
					if (!user3.equals("")) {
						// 3.三级审核通过
						isReadOnly = true;
						fileState = "底稿三级审核已通过，按照只读模式打开";
						canSaveFile = false;
						
						if(user1.equals(userId) && isEditPeopleCanEdit) {
							isReadOnly = false;
							fileState = "底稿三级审核已通过，项目允许编制人修改, 读写打开";
						}
					} else if (!user2.equals("")) {
						// 2.二级审核通过
						isReadOnly = true;
						fileState = "底稿二级审核已通过，按照只读模式打开";
						canSaveFile = false;

						if (hasAudit && curProjectProperty.indexOf("3") >= 0) {
							isReadOnly = false;
							fileState = "底稿二级审核已通过，项目允许三级审核修改, 读写打开";
						}
						
						if(user1.equals(userId) && isEditPeopleCanEdit) {
							isReadOnly = false;
							fileState = "底稿二级审核已通过，项目允许编制人修改, 读写打开";
						}

					} else if (!user5.equals("")) {

						// 5.一级审核通过
						isReadOnly = true;
						fileState = "底稿一级审核已通过，按照只读模式打开";
						canSaveFile = false;

						if (hasAudit && curProjectProperty.indexOf("2") >= 0) {
							isReadOnly = false;
							fileState = "底稿一级审核已通过，项目允许二级审核修改, 读写打开";
						}
						
						if(user1.equals(userId) && isEditPeopleCanEdit) {
							isReadOnly = false;
							fileState = "底稿一级审核已通过，项目允许编制人修改, 读写打开";
						}

					} else if (!user1.equals("")) {
						if (user1.equals(userId)) {
							// 1.编制完成,但用户是编制人本人的话读写打开
							isReadOnly = false;
							fileState = "底稿已编制完成，编制人读写打开";
						} else {
							isReadOnly = true;
							fileState = "底稿已编制完成，非编制人只读打开";
							canSaveFile = false;

							if (curProjectProperty.indexOf("1") >= 0) {
								if (hasAudit) {
									isReadOnly = false;
									fileState = "底稿已编制完成，项目允许一级审核修改, 读写打开";
								} else {
									isReadOnly = true;
									fileState = "底稿已编制完成，你无权复核，只读打开";
									canSaveFile = false;
								} 
							}

						}

					} else {

						// 4.不是底稿分工人
						isReadOnly = true;
						fileState = "你不是底稿分工人,按照只读模式打开";
						canSaveFile = false;

					}
				}

			} else {

				// 有人打开了底稿
				isReadOnly = true;
				fileState = userhasopened + "正在编辑该底稿,你以只读方式打开";
				canSaveFile = false;

			}

			// 如果项目已经归档,只读打开
			if (curProjectState.equals("2")) {
				isReadOnly = true;
				fileState = "该项目已经归档,所有底稿以只读方式打开";
				canSaveFile = false;
			}

			// 如果没有加密狗,加上试用版信息
			if (JRockey2Opp.getUserLic() <= 0) {
				fileState += "(试用版)";
			}

			// ReportService reportService = new ReportService(conn);
			// int isReportProject =
			// reportService.isReportProject(curProjectId);

			String referStr = "";

			/**
			 * 未来自动取数这里要添加代码
			 */
			/*
			 * if(isReportProject == 1){ //如果该项目是合并报表项目,则带上引用项目的信息 Map referMap =
			 * reportService.getReferDetail(curProjectId,taskCode);
			 *
			 * if(referMap.get("referProjectid") != null) { referStr +=
			 * "&referProjectid=" + (String)referMap.get("referProjectid"); }
			 *
			 * if(referMap.get("referAccpackageid")!=null) { referStr +=
			 * "&referPackageid=" + (String)referMap.get("referAccpackageid"); }
			 *
			 * if(referMap.get("referTaskCode")!=null) { referStr +=
			 * "&referTaskCode=" + (String)referMap.get("referTaskCode"); } }
			 */

			// 登记用户打开的底稿信息
			UserCurTask userCurTask = new UserCurTask();
			userCurTask.setCurTaskProjectId(curProjectId); // 底稿项目编号
			userCurTask.setCurTaskName(fileName); // 文件名
			userCurTask.setCurTaskEditTime(asf.getCurrentDate() + " "
					+ asf.getCurrentTime()); // 开始编辑时间
			userCurTask.setCurTaskCode(taskCode); // 索引号
			userCurTask.setCurTaskId(taskId); // 任务编号

			// 如果其他用户打开，就登记为只读打开
			if (isReadOnly) {
				// 只读
				userCurTask.setCurTaskOpenType("0");
			} else {
				userCurTask.setCurTaskOpenType("1");
			}
			userSession.getUserCurTasks().add(userCurTask);

			session.setAttribute("userSession", userSession);

			// 如果项目已经归档,则不再允许编制,复核,退回等
			if (!"2".equals(curProjectState)) {
				// 编制完成按钮
				if ("".equals(user1)) {
					modelAndView.addObject("btn1", "ok");
				}

				// 一级复核按钮
				if (!"".equals(user1) && "".equals(user5)
						&& curProjectProperty.length() == 3 && "".equals(user2)
						&& "".equals(user3)) {
					modelAndView.addObject("btn5", "ok");
				}

				// 二级复核按钮
				if (((!"".equals(user5) && "".equals(user2)) || (!""
						.equals(user1)
						&& "".equals(user2) && curProjectProperty.length() == 2)
						&& "".equals(user3))) {
					modelAndView.addObject("btn2", "ok");
				}

				// 三级复核按钮
				if (!"".equals(user2) && "".equals(user3)) {
					modelAndView.addObject("btn3", "ok");
				}

				// 退回底稿按钮
				if (!"".equals(user1)) {
					modelAndView.addObject("btn4", "ok");
				}
			}

			List taskScriptList = new ArrayList();

			String strIsHbbb = "false";
			String strHbbbValue = UTILSysProperty.SysProperty
					.getProperty("合并报表对应单体模板");
			if (strHbbbValue == null) {
				strHbbbValue = "";
			}
			if (("," + strHbbbValue + ",").indexOf(","
					+ userSession.getCurAuditType() + ",") >= 0) {
				strIsHbbb = "true";
			}

			modelAndView.addObject("IsHbbb", strIsHbbb);

			modelAndView.addObject("curCustomerName", new CustomerService(conn)
					.getCustomer(project.getCustomerId()).getCurname());
			modelAndView
					.addObject("curAccPackageId", project.getAccPackageId());
			modelAndView.addObject("userId", userId);
			modelAndView.addObject("userName", userName);
			modelAndView.addObject("flag",flag) ;
			modelAndView.addObject("sessionId", session.getId());
			modelAndView.addObject("fileName", fileName);
			modelAndView.addObject("referStr", referStr);
			modelAndView.addObject("taskCode", taskCode);
			modelAndView.addObject("projectId", curProjectId);
			modelAndView.addObject("curProjectId", curProjectId);
			modelAndView.addObject("taskScriptList", taskScriptList);
			modelAndView.addObject("isReadOnly", new Boolean(isReadOnly));
			modelAndView.addObject("fileState", fileState);
			modelAndView.addObject("taskId", taskId);
			modelAndView.addObject("parentTaskId", task.getParentTaskId());
			modelAndView.addObject("fullPath", task.getFullPath());
			modelAndView.addObject("parentFullPath", taskService.getTaskByTaskId(task.getParentTaskId()).getFullPath());
			modelAndView
					.addObject("curAuditTypeProperty", curAuditTypeProperty);
			modelAndView.addObject("isBack", isBack);
			modelAndView.addObject("canSaveFile", new Boolean(canSaveFile));
			modelAndView.addObject("userName", userSession.getUserName());
			modelAndView.addObject("prevTaskInfo", taskCommonService
					.getPrevTaskInfo(orderid));
			modelAndView.addObject("nextTaskInfo", taskCommonService
					.getNextTaskInfo(orderid));

			modelAndView.addObject("taskcheck1", taskcheck1);

			modelAndView
					.addObject("selected", request.getParameter("selected"));

			if ("铭太科技内部专用".equals(userSession.getUserAuditOfficeName())) {
				modelAndView.addObject("DebugMode", "true");
			} else {
				modelAndView.addObject("DebugMode", "false");
			}

			if ("全表保存".equals(task.getDescription())) {
				modelAndView.addObject("savemode", "true");
				org.util.Debug.prtOut("qwh:savemoe:true");

			} else {
				modelAndView.addObject("savemode", "false");
				org.util.Debug.prtOut("qwh:savemoe:false");
			}

			modelAndView.addObject("CombineFile", asf.showNull(request
					.getParameter("CombineFile")));

			// 保存用户最后一次打开的底稿信息
			String taskInfo = curProjectId + "`" + taskId + "`" + typeId;
			new UserDisplayService(conn).setLastTask(userId, taskInfo);

			String temp = new ASFuntion().showNull(UTILSysProperty.SysProperty
					.getProperty("允许使用底稿库的模板属性"));

			if (temp.indexOf(curAuditTypeProperty) > -1) {
				String showTaskNames = taskService.getShowSheetTaskNames(taskId);
				String hiddenTaskNames = taskService.getHiddenSheetTaskNames(taskId);
				
				modelAndView.addObject("showTaskNames", showTaskNames.replaceAll("\"", "\\\\\""));
				modelAndView.addObject("hiddenTaskNames", hiddenTaskNames.replaceAll("\"", "\\\\\""));
				
			}
			
			//当前显示表页
			if(sheetTaskCode != null && !"".equals(sheetTaskCode)) {
				String sheetTaskName = taskService.getSheetNameBySheetTaskCode(taskId, sheetTaskCode);
				modelAndView.addObject("sheetTaskName", sheetTaskName.replaceAll("\"", "\\\\\""));
			}

			String saveLog = new ASFuntion().showNull(UTILSysProperty.SysProperty.getProperty("底稿保存时是否在底稿意见中保留轨迹"));

			modelAndView.addObject("saveLog", saveLog);
			
			String openmode =new ASFuntion().showNull(UTILSysProperty.SysProperty.getProperty("底稿打开模式"));
			if ("".equals(openmode)){
				openmode="0";
			}
			modelAndView.addObject("openmode", openmode);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	/**
	 * 下载底稿文件
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView fileDownload(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		ASFuntion funtion = new ASFuntion();

		String projectId = funtion.showNull(request.getParameter("projectId"));
		String typeId = funtion.showNull(request.getParameter("typeId"));
		String taskId = funtion.showNull(request.getParameter("taskId"));
		String taskCode = funtion.showNull(request.getParameter("taskCode"));

		//重置表页的时候，前台传回来的是projectid和taskcode,后台翻译成对应的typeId和taskId
		String bGetTemplate=funtion.showNull(request.getParameter("bGetTemplate"));
		
		
		String zipByClient=funtion.showNull(request.getParameter("zipByClient"));

		byte[] bytes = null;
		String fileName = null;

		Connection conn = null;
		PrintWriter out=null;
		try {
			conn = new DBConnect().getConnect("");
			DbUtil dbUtil = new DbUtil(conn);
			
			ManuFileService manuFileService = new ManuFileService(conn);
			
			
			if ("1".equals(bGetTemplate)){
				//重置表页的时候，前台传回来的是projectid和taskcode,后台翻译成对应的typeId和taskId
				
				new DBConnect().changeDataBaseByProjectid(conn, projectId);
				
				String sql = "select b.typeid "
							+"from z_project a,k_tasktemplate b,z_task c "
							+"where a.projectid=?  "
							+"and c.projectid=?  "
							+"and c.taskid=? "
							+"and a.audittype=b.typeid "
							+"and b.taskcode=c.taskcode "
							+"and b.isleaf=1 ";

				Object[] object = new Object[] {
						projectId,projectId,taskId
				};
				
				typeId= dbUtil.queryForString(sql, object);
				
				sql = "select b.taskid "
					+"from z_project a,k_tasktemplate b,z_task c "
					+"where a.projectid=?  "
					+"and c.projectid=?  "
					+"and c.taskid=? "
					+"and a.audittype=b.typeid "
					+"and b.taskcode=c.taskcode "
					+"and b.isleaf=1 ";

				taskId= dbUtil.queryForString(sql, object);
				
				if (typeId==null || taskId==null ){
					throw new Exception("无法定位该项目的模板或底稿！");
				}
				
				projectId="";
				taskCode="";
				
				System.out.println("taskId="+taskId+"|typeId="+typeId);
			}
			
			// 根据项目编号projectId或模板编号typeId加底稿编号taskid下载底稿
			if (!"".equals(projectId) || !"".equals(typeId)) {
				
				String fileUpdateDate = "";

				if (!"".equals(projectId)) {

					// 如果taskcode不为空
					if (!"".equals(taskCode)) {
						taskId = new TaskService(conn, projectId).getTaskIdByTaskCodeIsLeaf(taskCode);
					}

					// 根据项目编号和底稿编号获得文件，项目底稿
					if ("1".equals(zipByClient)){
						//前台自己解压
						bytes = manuFileService.getFileByProjectIdAndTaskId(
								projectId, taskId);
					}else{
						//后台解压
						bytes = manuFileService.getUnZipFileByProjectIdAndTaskId(
								projectId, taskId);
					}


					fileName = new TaskService(conn, projectId)
							.getTaskNameByTaskId(taskId);
					
					fileUpdateDate = new TaskService(conn, projectId).getTaskByTaskId(taskId).getUdate();

				} else if (!"".equals(typeId)) {

					// 如果taskcode不为空
					if (!"".equals(taskCode)) {
						taskId = new TaskTemplateService(conn, typeId)
								.getTaskTemplateIdByTaskCode(taskCode);

					}
					// 根据模板编号和底稿编号获得文件，模板底稿
					if ("1".equals(zipByClient)){
						//前台自己解压
						bytes = manuFileService.getFileByTypeIdAndTaskId(
								typeId, taskId);
					}else{
						//后台解压
						bytes = manuFileService.getUnZipFileByTypeIdAndTaskId(
								typeId, taskId);
					}

					fileName = new TaskCommonService(conn)
							.getFileNameByTypeIdAndTaskId(typeId, taskId);
					
					fileUpdateDate = new TaskTemplateService(conn, projectId).getTaskTemplateByTaskId(taskId).getUdate();
				}

				// 判断文件是word还是excel
				if (fileName != null) {
					String mime = fileName.toLowerCase().indexOf(".doc") > -1 ? "application/doc"
							: "application/vnd.ms-excel";
					response.setContentType(mime);
					fileName = URLEncoder.encode(fileName, "GBK");
					
					response.setHeader("Content-disposition","attachment; filename=" + fileName + ";updateDate=" + fileUpdateDate);
				}

				System.out.println("打开底稿：" + fileName + ", projectId="
						+ projectId + ", typeId=" + typeId + ", taskId="
						+ taskId + ", taskCode=" + taskCode + "|打开方式："+ zipByClient);

			}

			if (bytes != null && bytes.length > 0) {
				OutputStream outs = response.getOutputStream();
				outs.write(bytes);
				outs.flush();
				outs.close();
			} else {
				out = response.getWriter();
				out.print("错误:文件不存在！");
			}
		} catch (Exception e) {
			out = response.getWriter();
			out.print("错误:"+e.getMessage());
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
			if (out!=null)out.close();
		}
		return null;
	}
	
	
	/**
	 * 下载底稿文件
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView attachFileDownload(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		ASFuntion funtion = new ASFuntion();

		String projectId = funtion.showNull(request.getParameter("projectId"));
		String taskId = funtion.showNull(request.getParameter("taskId"));


		byte[] bytes = null;
		String fileName = null;

		Connection conn = null;

		try {
			conn = new DBConnect().getConnect("");

			ManuFileService manuFileService = new ManuFileService(conn);
			
			// 根据项目编号projectId或模板编号typeId加底稿编号taskid下载底稿
			if (!"".equals(projectId) ) {
				//后台解压
				bytes = manuFileService.getUnZipFileByProjectIdAndTaskId(
						projectId, taskId);
				fileName = new TaskService(conn, projectId).getTaskNameByTaskId(taskId);
			}
			fileName = URLEncoder.encode(fileName,"UTF-8") ;
			response.setContentType("application/x-msdownload");
			response.setHeader("Content-disposition",
					"attachment; filename=" + fileName);
			response.setHeader("Content-Length", String.valueOf(bytes.length));
			if (bytes != null && bytes.length > 0) {
				OutputStream outs = response.getOutputStream();
				outs.write(bytes);
				outs.flush();
				outs.close();
			} else {
				PrintWriter out = response.getWriter();
				out.print("错误!!文件不存在！");
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
	 * 关闭底稿,清空用户打开的底稿信息
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView fileClose(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// 取得用户Session
		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session
				.getAttribute("userSession");

		Set userCurTasks = userSession.getUserCurTasks();
		Iterator it = userCurTasks.iterator();
		ASFuntion asf = new ASFuntion();
		String taskId = asf.showNull(request.getParameter("taskId"));

		while (it.hasNext()) {
			UserCurTask userCurTask = (UserCurTask) it.next();

			if (taskId.equals(userCurTask.getCurTaskId())) {
				userCurTasks.remove(userCurTask);
				break;
			}
		}

		System.out.println("关闭底稿了,该用户当前打开底稿数：" + userCurTasks.size());

		return null;
	}

	/**
	 * 删除任务
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView remove(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/html;charset=utf-8");

		// 取得用户Session
		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session
				.getAttribute("userSession");

		String curProjectId = request.getParameter("projectId");

		if (curProjectId == null || "".equals(curProjectId)) {
			curProjectId = userSession.getCurProjectId(); // 当前项目编号
		}

		String taskId = request.getParameter("taskId"); // 任务编号

		// 任务类型,可以是任务结点，也可以是任务结点，根据不同的值操作完毕后返回不同的页面
		String taskType = request.getParameter("taskType");

		// 来自页面的parentTaskId,是为了操作完毕后返回到原来的页面上
		String parentTaskId = request.getParameter("parentTaskId");

		PrintWriter out = response.getWriter();

		Connection conn = null;
		String url = "";
		boolean isAllowRemove = true; // 是否能够删除的标志
		String fullPath = "" ;

		try {
			conn = new DBConnect().getConnect("");
			TaskService taskService = new TaskService(conn, curProjectId);
			String curauditTypeProperty = new AuditTypeTemplateService(conn)
					.getPropertyByProjectId(curProjectId);

			if ("task".equals(taskType)) {
				if ("1".equals(curauditTypeProperty)) {
					// 风险导向审计环节
					url = "tache.do?method=tacheList&refresh=true";
				} else {
					// 普通任务结点
					url = "taskSort.do?method=list&refresh=true";
				}

				// 如果该任务结点还有下级子任务或者底稿的话，则不允许删除
				if (taskService.getChildrenCountByParentTaskId(taskId) > 0) {
					out
							.println("<Script>alert(\"该任务还有下级子任务，请先删除下级后再删除本任务。\");</Script>");
					isAllowRemove = false;
				} else {
					isAllowRemove = true;
				}
			} else if ("taskManu".equals(taskType)) {
				if ("1".equals(curauditTypeProperty)) {
					// 风险导向审计底稿
					url = "tache.do?method=list&refresh=true";
				} else {
					// 普通任务底稿
					url = "task.do?method=list&refresh=true";
				}

			}
			url += "&parentTaskId=" + parentTaskId;
			// 如果标志为允许删除,就执行数据库删除操作
			if (isAllowRemove) {

				Task task = taskService.getTaskByTaskId(taskId) ;
				String userId = userSession.getUserId() ;
				
				if(parentTaskId != null && !"".equals(parentTaskId) && !"0".equals(parentTaskId)) {
					Task parentTask = taskService.getTaskByTaskId(parentTaskId);
					fullPath = parentTask.getFullPath() ;
					fullPath = fullPath.replaceAll("\\|","/") ;
				}

				String user0 = new ASFuntion().showNull(task.getUser0());
				if(!"".equals(user0) && !userId.equals(user0)) {
					out.println("<Script>alert(\"您不是这张底稿的分工人,不能删除底稿。\");</Script>");
					out.println("<Script>window.location='" + url + "';</Script>");
					return null ;
				}


				String taskName = task.getTaskName();

				taskService.removeTask(taskId); // 执行数据库删除操作

				LogService.updateToLog(userSession, "删除底稿[" + taskName + "]",
						conn);

				out.println("<Script>");
				out.println("alert(\"该任务已被删除。\");");
				out.println("</Script>");
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}


		out.println("<Script>window.location='" + url + "&projectId=" + curProjectId + "&refreshTree=true&fullPath="+fullPath+"';</Script>");

		return null;
	}

	/**
	 * 处理编制,审核,退回流程
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView auditing(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");

		// 取得用户Session
		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session
				.getAttribute("userSession");

		String tempType = request.getParameter("type"); // 临时复核类型
		String taskId = request.getParameter("taskId"); // 底稿任务编号
		String userId = request.getParameter("userId"); // 用户编号
		String curProjectId = request.getParameter("projectId"); // 当前项目编号
		String curDate = request.getParameter("curDate") ;  //底稿编制完成日期

		// 如果request中没有传userId参数过来,那么就取session中的当前用户编号
		if (userId == null || "".equals(userId)) {
			userId = userSession.getUserId();
		}

		// 如果request中没有传projectId参数过来,那么就取session中的当前项目编号
		if (curProjectId == null || "".equals(curProjectId)) {
			curProjectId = userSession.getCurProjectId(); // 当前项目编号
		}

		int type = 0; // 复核类型
		try {
			type = Integer.parseInt(tempType);
		} catch (Exception e) {
			throw new Exception("参数错误:type=" + tempType);
		}

		String levelBack = null;
		if (type == 4) {
			levelBack = request.getParameter("levelBack");
		}

		PrintWriter out = response.getWriter();
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			ProjectService ps = new ProjectService(conn);
			int state = ps.getState(curProjectId);
			boolean hasAuthority3 = false;
			boolean hasAuthority2 = false;
			UserdefService udf = new UserdefService(conn);
			hasAuthority2 = udf.hasAuthority2(userId, curProjectId);
			hasAuthority3 = udf.hasAuthority3(userId, curProjectId);

			TaskService taskService = new TaskService(conn,
					curProjectId);
			Task task = taskService.getTaskByTaskId(taskId);

			String auditProperty = task.getAuditproperty();

			if (type == 1) {

				if (auditProperty!=null&&auditProperty.indexOf("2")==0) {
					if(state != 5&&state!=6) {
						out.write("notyet2");
						return null;
					}
				} else if(auditProperty!=null&&auditProperty.indexOf("3")==0){
					if (state != 6) {
						out.write("notyet3");
						return null;
					}
				}
			} else if (type == 2) {
				if (state != 5&&state!=6) {
					out.write("notyet2");
					return null;
				} else {

					if (!hasAuthority2) {
						out.write("nopopedom2");
						return null;
					}
				}
			} else if (type == 3) {
				if (state != 6) {
					out.write("notyet3");
					return null;
				} else {
					if (!hasAuthority3) {
						out.write("nopopedom3");
						return null;
					}

				}
			}

			TaskCommonService taskCommonService = new TaskCommonService(conn,
						curProjectId);
			// 返回编制或审核结果
			out.write(taskCommonService.auditing(userId, taskId, type,
					levelBack,curDate));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 选择相关底稿
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView selectTask(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(SELECT_TASK_VIEW);

		// 取得用户Session
		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session
				.getAttribute("userSession");

		String parentTaskId = request.getParameter("parentTaskId"); // 底稿任务编号
		String curProjectId = request.getParameter("projectId");
		String pflag = request.getParameter("pflag");

		if (curProjectId == null || "".equals(curProjectId)) {
			curProjectId = userSession.getCurProjectId();
		}
		String manuScriptValue = request.getParameter("manuScriptValue");
		String manuScriptTagId = request.getParameter("manuScriptTagId");

		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			TaskService taskService = new TaskService(conn, curProjectId);

			// 返回编制或审核结果
			List selectTaskList = taskService
					.getTaskListByParentTaskId(parentTaskId);

			if ("isMust".equals(manuScriptTagId)) {
				manuScriptValue = taskService.getMustTaskCodes(parentTaskId);
			}

			List selectedTaskList = taskService
					.getTaskListByTaskCodes(manuScriptValue);
			List selectedSheetTaskList = taskService
					.getSheetTaskListByTaskCodes(manuScriptValue);

			String customerId = new CustomerService(conn)
					.getCustomerIdByProjectId(curProjectId);

			modelAndView.addObject("manuScriptValue", manuScriptValue);
			modelAndView.addObject("selectTaskList", selectTaskList);
			modelAndView.addObject("selectedTaskList", selectedTaskList);
			modelAndView.addObject("selectedSheetTaskList",
					selectedSheetTaskList);
			modelAndView.addObject("manuScriptTagId", manuScriptTagId);
			modelAndView.addObject("parentTaskId", parentTaskId);
			modelAndView.addObject("projectId", curProjectId);
			modelAndView.addObject("customerId", customerId);
			modelAndView.addObject("pflag",pflag);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		return modelAndView;
	}

	/**
	 * 选择同环节下的审计程序
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView selectProcedure(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(SELECT_PROCEDURE_VIEW);

		// 取得用户Session
		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session
				.getAttribute("userSession");

		String curProjectId = request.getParameter("projectId");

		if (curProjectId == null || "".equals(curProjectId)) {
			curProjectId = userSession.getCurProjectId(); // 当前项目编号
		}

		ASFuntion asf = new ASFuntion();
		String taskId = asf.showNull(request.getParameter("taskId"));
		String flag = asf.showNull(request.getParameter("flag"));

		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			TaskService taskService = new TaskService(conn, curProjectId);

			String parentTaskId = taskService.getParentTaskId(taskId);
			TargetService targetService = new TargetService(conn, curProjectId);
			List targetList = targetService
					.getTargetAndProListByTaskId(parentTaskId);

			modelAndView.addObject("targetList", targetList);
			modelAndView.addObject("flag", flag);
			modelAndView.addObject("taskId", taskId);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		return modelAndView;
	}

	/**
	 * 确定选择审计程序
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView enterSelectProcedure(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		response.setContentType("text/html;charset=utf-8");

		ASFuntion asf = new ASFuntion();

		String autoId = asf.showNull(request.getParameter("autoId"));
		String taskId = asf.showNull(request.getParameter("taskId"));
		String flag = asf.showNull(request.getParameter("flag"));

		// 取得用户Session
		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session
				.getAttribute("userSession");

		String curProjectId = request.getParameter("projectId");

		if (curProjectId == null || "".equals(curProjectId)) {
			curProjectId = userSession.getCurProjectId(); // 当前项目编号
		}
		String[] autoIds = autoId.split(",");

		PrintWriter out = response.getWriter();

		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			ProcedureService proceService = new ProcedureService(conn,
					curProjectId);
			if ("clone".equals(flag)) {
				proceService.cloneProcedure(autoIds, taskId);
				out.write("ok");
			} else if ("refer".equals(flag)) {
				proceService.referProcedure(autoIds, taskId);
				out.write("ok");
			} else {
				out.write("error");
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
	 * 批量审核底稿列表
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView batchAuditingList1(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();

		// 取得用户Session
		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session
				.getAttribute("userSession");
		String userId = userSession.getUserId();

		// 检查是否登陆项目
		if (!new CommonSecurity(request, response).checkProjectLogin()) {
			return null;
		}

		String typeTemp = request.getParameter("type");
		int type = 0;
		try {
			type = Integer.parseInt(typeTemp);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		String mytip = "" + UTILSysProperty.SysProperty.get("一级复核人可以复核自己底稿");

		String curProjectId = request.getParameter("projectId");

		if (curProjectId == null || "".equals(curProjectId)) {
			curProjectId = userSession.getCurProjectId(); // 当前项目编号
		}

		int state = 0;
		boolean hasAuthority3 = false;
		boolean hasAuthority2 = false;
		boolean isAudit = false;


		Connection conn = null;
		try {
			String acc = userSession.getCurAccPackageId();
			conn = new DBConnect().getConnect(acc.substring(0, 6));
			ProjectService ps = new ProjectService(conn);
			state = ps.getState(curProjectId);
			UserdefService udf = new UserdefService(conn);
			hasAuthority2 = udf.hasAuthority2(userId,curProjectId);
			hasAuthority3 = udf.hasAuthority3(userId,curProjectId);

			if(type==2) {
				isAudit = hasAuthority2;
			} else if(type==3) {
				isAudit = hasAuthority3;
			} else {
				isAudit = new AuditPeopleService(conn, curProjectId)
					.hasAudit(userId);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		if(state!=2) {
			if (type == 2) {
				if (state != 5&&state!=6) {
					out.println("<script type=\"text/javascript\">");
					out.println("	alert('项目尚未进入二审状态');");
					out.println("	window.self.location='/AuditSystem/taskCommon.do?method=manager';");
					out.println("</script>");
					return null;
				} else {

					if (!hasAuthority2) {
						out.println("<script type=\"text/javascript\">");
						out.println("	alert('用户无权复核');");
						out.println("	window.self.location='/AuditSystem/taskCommon.do?method=manager';");
						out.println("</script>");
						return null;
					}
				}
			} else if (type == 3) {
				if (state != 6) {
					out.println("<script type=\"text/javascript\">");
					out.println("	alert('项目尚未进入三审状态');");
					out.println("	window.self.location='/AuditSystem/taskCommon.do?method=manager';");
					out.println("</script>");
					return null;
				} else {

					if (!hasAuthority3) {
						out.println("<script type=\"text/javascript\">");
						out.println("	alert('用户无权复核');");
						out.println("	window.self.location='/AuditSystem/taskCommon.do?method=manager';");
						out.println("</script>");
						return null;
					}
				}
			}

		}

		String curProjectProperty = userSession.getCurProjectProperty();
		String menuId = request.getParameter("menuId");
		String[] labelValues = { "分工", "编制", "二级", "三级", "退回", "一级" };


		ModelAndView modelAndView = new ModelAndView(BATCH_AUDITING_LIST_VIEW);
		if (curProjectProperty != null) {
			if (type == 5 && curProjectProperty.length() == 2) {
				out.write("<font size=2 color='red'>对不起,当前项目不支持一级复核!!</font>");
				out
						.write("<input type='button' value='返  回' onclick='history.go(-1);'>&nbsp;");
				return null;
			}
		}

		// 等待复核
		DataGridProperty dgProperty = new DataGridProperty();
		dgProperty.setTableID("waitAuditTaskList");
		dgProperty.setTitle("等待" + labelValues[type] + "复核的底稿列表");
		dgProperty.setCurProjectDatabase(true);
		dgProperty.setInputType("checkbox");
		dgProperty.setWhichFieldIsValue(1);

		dgProperty.setPrintEnable(true);
		dgProperty.setPrintTitle("等待" + labelValues[type] + "复核底稿列表");

		dgProperty.addColumn("底稿编号", "TaskCode");
		dgProperty.addColumn("底稿编号", "TaskName");
		dgProperty.addColumn("编制人", "User1");
		dgProperty.addColumn("编制时间", "date1", "showDate:yyyy-MM-dd");
		dgProperty.setTrActionProperty(true);
		dgProperty
				.setTrAction("id='tr${taskId}' fullPath='${fullpath}' taskId='${taskId}' manuId='${manuid}' style='cursor:hand;' onDBLclick=\"openTaskFile(this);\"");

		StringBuffer sql = new StringBuffer();
		sql.append(" select taskId,TaskCode,TaskName,manuid,fullpath, ");
		sql.append(" u1.name as User1,date1, ");
		sql.append(" u5.name as User5,date5, ");
		sql.append(" u2.name as User2,date2, ");
		sql.append(" u3.name as User3,date3 ");
		sql.append(" from z_Task t");
		sql.append(" left join k_user u1 on t.user1 = u1.id ");
		sql.append(" left join k_user u2 on t.user2 = u2.id ");
		sql.append(" left join k_user u3 on t.user3 = u3.id ");
		sql.append(" left join k_user u5 on t.user5 = u5.id ");
		sql.append(" where ProjectID = '" + curProjectId + "'");
		sql.append(" and IsLeaf = 1 ");
		// 判断一级复核人是否可以复核自己底稿
		if ("是".equals(mytip) && "一级".equals(labelValues[type])) {

		} else {
			sql.append(" and user1 != '" + userId + "'");
		}

		// 已经复核
		DataGridProperty dgProperty2 = new DataGridProperty();
		dgProperty2.setTableID("auditedTaskList");
		dgProperty2.setTitle("已完成复核的底稿列表");
		dgProperty2.setCurProjectDatabase(true);

		dgProperty2.setPrintEnable(true);
		dgProperty2.setPrintTitle("已完成复核的底稿列表");

		dgProperty2.addColumn("底稿编号", "TaskCode");
		dgProperty2.addColumn("底稿编号", "TaskName");
		dgProperty2.addColumn("编制人", "User1");
		dgProperty2.addColumn("编制时间", "date1", "showDate:yyyy-MM-dd");
		dgProperty2.setInputType("checkbox");
		dgProperty2.setWhichFieldIsValue(1);
		dgProperty2.setTrActionProperty(true);
		dgProperty2
				.setTrAction("id='tr${taskId}' fullPath='${fullpath}' taskId='${taskId}' manuId='${manuid}' style='cursor:hand;' onDBLclick=\"openTaskFile(this);\"");

		StringBuffer sql2 = new StringBuffer();
		sql2.append(" select taskId,TaskCode,TaskName,manuid,fullpath, ");
		sql2.append(" u1.name as User1,date1, ");
		sql2.append(" u5.name as User5,date5, ");
		sql2.append(" u2.name as User2,date2, ");
		sql2.append(" u3.name as User3,date3 ");
		sql2.append(" from z_Task t");
		sql2.append(" left join k_user u1 on t.user1 = u1.id ");
		sql2.append(" left join k_user u2 on t.user2 = u2.id ");
		sql2.append(" left join k_user u3 on t.user3 = u3.id ");
		sql2.append(" left join k_user u5 on t.user5 = u5.id ");
		sql2.append(" where ProjectID = '" + curProjectId + "'");
		sql2.append(" and IsLeaf = 1 ");

		DataGridProperty dgProperty3 = new DataGridProperty();
		String sql3 = "";
		if (type == 2 || type == 3) {
			dgProperty3.setTableID("myTaskList");
			dgProperty3.setTitle((type == 2 ? "二" : "三") + "审需要编制的底稿");
			dgProperty3.setCurProjectDatabase(true);
			dgProperty3.setPrintEnable(true);
			dgProperty3.setPrintTitle((type == 2 ? "二" : "三") + "审需要编制的底稿");
			dgProperty3.addColumn("底稿编号", "TaskCode");
			dgProperty3.addColumn("底稿名称", "TaskName");
			dgProperty3.setTrActionProperty(true);
			dgProperty3
					.setTrAction("id='tr${taskId}' fullPath='${fullpath}' taskId='${taskId}' manuId='${manuid}' style='cursor:hand;' onDBLclick=\"openTaskFile(this);\"");
		}

		switch (type) {
		case 2:
			// 等待复核
			dgProperty.addColumn("一级复核人", "User5");
			dgProperty.addColumn("复核时间", "date5", "showDate:yyyy-MM-dd");
			dgProperty.addColumn("二级复核人", "User2");
			dgProperty.addColumn("复核时间", "date2", "showDate:yyyy-MM-dd");

			// 已经复核
			dgProperty2.addColumn("一级复核人", "User5");
			dgProperty2.addColumn("复核时间", "date5", "showDate:yyyy-MM-dd");
			dgProperty2.addColumn("二级复核人", "User2");
			dgProperty2.addColumn("复核时间", "date2", "showDate:yyyy-MM-dd");

			if (curProjectProperty.length() == 2) {
				/*
				 * //等待复核 sql.append(" And (User1 IS NOT NULL And User1 != '')
				 * "); sql.append(" And (auditproperty like '%2%') ");
				 * sql.append(" And (User2 IS NULL || User2 = '')");
				 *
				 * //已经复核 sql2.append(" And User1 IS NOT NULL And User1 != ''" );
				 * sql2.append(" And (auditproperty like '%2%') " );
				 * sql2.append(" And User2 IS NOT NULL And User2 != ''" );
				 */
				System.out.println("如果看到此行代码请找小丘！！！！！！！！！");
			} else {
				// 等待复核
				sql.append(" And (auditproperty like '%2%') ");
				sql.append(" And (User2 IS NULL || User2 = '')");

				// 已经复核
				// sql2.append(" And (auditproperty like '%2%') " );

				sql2.append(" And ((User5 IS NOT NULL And User5 != '')");
				sql2.append(" or (User1 IS NOT NULL And User1 != '')");
				sql2.append(" or (User2 is not null and user2 !=''))");
				sql2.append(" and user1 != '" + userId + "'");
			}

			sql3 = "select taskId,TaskCode,TaskName,manuid,fullpath from z_Task where ProjectID='"
					+ curProjectId
					+ "' and IsLeaf = 1 and auditproperty like '%2%' and auditproperty not like '%1%' And (User2 IS NULL || User2 = '')";

			break;

		case 3:
			// 等待复核
			dgProperty.addColumn("一级复核人", "User5");
			dgProperty.addColumn("复核时间", "date5", "showDate:yyyy-MM-dd");
			dgProperty.addColumn("二级复核人", "User2");
			dgProperty.addColumn("复核时间", "date2", "showDate:yyyy-MM-dd");
			dgProperty.addColumn("三级复核人", "User3");
			dgProperty.addColumn("复核时间", "date3", "showDate:yyyy-MM-dd");
			sql.append(" And (auditproperty like '%3%') ");
			sql.append(" And (User3 IS NULL || User3 = '')");

			// 已经复核
			dgProperty2.addColumn("一级复核人", "User5");
			dgProperty2.addColumn("复核时间", "date5", "showDate:yyyy-MM-dd");
			dgProperty2.addColumn("二级复核人", "User2");
			dgProperty2.addColumn("复核时间", "date2", "showDate:yyyy-MM-dd");
			dgProperty2.addColumn("三级复核人", "User3");
			dgProperty2.addColumn("复核时间", "date3", "showDate:yyyy-MM-dd");
			// sql.append(" And (auditproperty like '%3%') ");
			sql2.append(" And ((User2 IS NOT NULL And User2 != '')");
			sql2.append(" or (User1 IS NOT NULL And User1 != '')");
			sql2.append(" or (User5 IS NOT NULL And User5 != '')");
			sql2.append(" or (user3 is not null and user3 != ''))");
			sql2.append(" and user1 != '" + userId + "'");

			sql3 = "select taskId,TaskCode,TaskName,manuid,fullpath from z_Task where ProjectID='"
					+ curProjectId
					+ "' and IsLeaf = 1 and auditproperty like '%3%' and auditproperty not like '%1%' and auditproperty not like '%2%' And (User3 IS NULL || User3 = '')";

			break;

		case 5:
			// 等待复核
			dgProperty.addColumn("一级复核人", "User5");
			dgProperty.addColumn("复核时间", "date5", "showDate:yyyy-MM-dd");
			sql.append(" And (User1 IS NOT NULL And User1 != '') ");
			sql.append(" And (User5 IS NULL || User5 = '')");

			// 已经复核
			dgProperty2.addColumn("一级复核人", "User5");
			dgProperty2.addColumn("复核时间", "date5", "showDate:yyyy-MM-dd");
			sql2.append(" And User1 IS NOT NULL And User1 != ''");
			sql2.append(" And (User5 IS NOT NULL and User5 != '')");
			sql2.append(" And user1!='" + userId + "'");

			break;

		default:
			break;
		}

		if (isAudit) {
			// 等待复核
			dgProperty.setSQL(sql.toString());
			dgProperty.setOrderBy_CH("orderid");
			dgProperty.setDirection("asc");
			session.setAttribute(DataGrid.sessionPre + dgProperty.getTableID(),
					dgProperty);
			modelAndView.addObject("isAudit", "true");
		}

		// 已经复核
		dgProperty2.setSQL(sql2.toString());
		dgProperty2.setOrderBy_CH("orderid");
		dgProperty2.setDirection("asc");
		session.setAttribute(DataGrid.sessionPre + dgProperty2.getTableID(),
				dgProperty2);

		if (type == 2 || type == 3) {
			// 二审三审要编制的底稿
			dgProperty3.setSQL(sql3);
			dgProperty3.setOrderBy_CH("taskId");
			dgProperty3.setDirection("asc");
			session
					.setAttribute(DataGrid.sessionPre
							+ dgProperty3.getTableID(), dgProperty3);
		}

		modelAndView.addObject("labelValue", labelValues[type]);
		modelAndView.addObject("type", new Integer(type));
		modelAndView.addObject("menuId", menuId);
		modelAndView.addObject("projectId", curProjectId);
		modelAndView.addObject("state", String.valueOf(state));

		return modelAndView;
	}

	/**
	 * 审计工作台
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView batchAuditingList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		response.setContentType("text/html;charset=utf-8");

		// 取得用户Session
		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session
				.getAttribute("userSession");
		String userId = userSession.getUserId();

		// 检查是否登陆项目
		if (!new CommonSecurity(request, response).checkProjectLogin()) {
			return null;
		}

		String typeTemp = request.getParameter("type");
		int type = 0;
		try {
			type = Integer.parseInt(typeTemp);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		String curProjectId = request.getParameter("projectId");

		if (curProjectId == null || "".equals(curProjectId)) {
			curProjectId = userSession.getCurProjectId(); // 当前项目编号
		}

		int state = 0;
		boolean hasAuthority3 = false;
		boolean hasAuthority2 = false;
		boolean isAudit = false;

		String customerId = null;
		Connection conn = null;

		String warnStr = null;

		try {
			String acc = userSession.getCurAccPackageId();
			conn = new DBConnect().getConnect(acc.substring(0, 6));
			ProjectService ps = new ProjectService(conn);
			state = ps.getState(curProjectId);

			UserdefService udf = new UserdefService(conn);
			hasAuthority2 = udf.hasAuthority2(userId,curProjectId);
			hasAuthority3 = udf.hasAuthority3(userId,curProjectId);

			customerId = new CustomerService(conn).getCustomerIdByProjectId(curProjectId);

			if(type==2) {
				isAudit = hasAuthority2;
			} else if(type==3) {
				isAudit = hasAuthority3;
			} else {
				isAudit = new AuditPeopleService(conn, curProjectId)
					.hasAudit(userId);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		if(state!=2) {
			if (type == 2) {
				if (state != 5&&state!=6) {
					warnStr = "项目尚未进入二审";
				} else {

					if (!hasAuthority2) {
						warnStr = "你无权进入二审工作台";
					}
				}
			} else if (type == 3) {
				if (state != 6) {
					warnStr = "项目尚未进入三审";
				} else {

					if (!hasAuthority3) {
						warnStr = "你无权进入三审工作台";
					}
				}
			} else if (type == 5) {
				if(!isAudit) {
					warnStr = "你无权进入一审工作台";
				}
			}

		}

		String[] labelValues = { "分工", "编制", "二审", "三审", "退回", "一审" };


		ModelAndView modelAndView = new ModelAndView(BATCH_AUDITING_LIST_VIEW);

		//关注底稿
		DataGridProperty dgProperty = new DataGridProperty();
		dgProperty.setTableID("concernedTaskList");
		dgProperty.setCurProjectDatabase(true);

		dgProperty.setPrintEnable(true);
		dgProperty.setPrintTitle(labelValues[type] + "关注底稿");

		dgProperty.addColumn("底稿编号", "TaskCode");
		dgProperty.addColumn("底稿编号", "TaskName");
		dgProperty.addColumn("编制人", "User1");
		dgProperty.addColumn("编制时间", "date1", "showDate:yyyy-MM-dd");
		dgProperty.setTrActionProperty(true);
		dgProperty
				.setTrAction("id='tr${taskId}' fullPath='${fullpath}' taskId='${taskId}' manuId='${manuid}' style='cursor:hand;' onDBLclick=\"openTaskFile(this);\"");

		StringBuffer sql = new StringBuffer();
		sql.append(" select taskId,TaskCode,TaskName,manuid,fullpath, ");
		sql.append(" u1.name as User1,date1, ");
		sql.append(" u5.name as User5,date5, ");
		sql.append(" u2.name as User2,date2, ");
		sql.append(" u3.name as User3,date3 ");
		sql.append(" from z_Task t");
		sql.append(" left join k_user u1 on t.user1 = u1.id ");
		sql.append(" left join k_user u2 on t.user2 = u2.id ");
		sql.append(" left join k_user u3 on t.user3 = u3.id ");
		sql.append(" left join k_user u5 on t.user5 = u5.id ");
		sql.append(" where ProjectID = '" + curProjectId + "'");
		sql.append(" and IsLeaf = 1 ");

		//我退回的底稿
		DataGridProperty dgProperty2 = new DataGridProperty();
		dgProperty2.setTableID("backedTaskList");
		dgProperty2.setCurProjectDatabase(true);

		dgProperty2.setPrintEnable(true);
		dgProperty2.setPrintTitle("本阶段退回的工作底稿");

		dgProperty2.addColumn("底稿编号", "TaskCode");
		dgProperty2.addColumn("底稿编号", "TaskName");
		dgProperty2.addColumn("编制人", "User1");
		dgProperty2.addColumn("编制时间", "date1", "showDate:yyyy-MM-dd");
		dgProperty2.addColumn("一级复核人", "User5");
		dgProperty2.addColumn("复核时间", "date5", "showDate:yyyy-MM-dd");
		dgProperty2.setInputType("checkbox");
		dgProperty2.setWhichFieldIsValue(1);
		dgProperty2.setTrActionProperty(true);
		dgProperty2
				.setTrAction("id='tr${taskId}' fullPath='${fullpath}' taskId='${taskId}' manuId='${manuid}' style='cursor:hand;' onDBLclick=\"openTaskFile(this);\"");

		StringBuffer sql2 = new StringBuffer();
		sql2.append(" select taskId,TaskCode,TaskName,manuid,fullpath, ");
		sql2.append(" u1.name as User1,date1, ");
		sql2.append(" u5.name as User5,date5, ");
		sql2.append(" u2.name as User2,date2, ");
		sql2.append(" u3.name as User3,date3 ");
		sql2.append(" from z_Task t");
		sql2.append(" left join k_user u1 on t.user1 = u1.id ");
		sql2.append(" left join k_user u2 on t.user2 = u2.id ");
		sql2.append(" left join k_user u3 on t.user3 = u3.id ");
		sql2.append(" left join k_user u5 on t.user5 = u5.id ");
		sql2.append(" where ProjectID = '" + curProjectId + "'");
		sql2.append(" and IsLeaf = 1 ");

		String levelBack = "";

		if(type==5) {
			levelBack = "1";
		} else if(type==2) {
			levelBack = "2";
		} else if(type==3) {
			levelBack = "3";
		}

		sql2.append(" and levelBack = '" + levelBack + "'");

		//所有底稿
		DataGridProperty dgProperty3 = new DataGridProperty();
		dgProperty3.setTableID("AllTaskList");
		dgProperty3.setCurProjectDatabase(true);

		dgProperty3.setPrintEnable(true);
		dgProperty3.setPrintTitle("所有底稿");

		dgProperty3.addColumn("底稿编号", "TaskCode");
		dgProperty3.addColumn("底稿编号", "TaskName");
		dgProperty3.addColumn("编制人", "User1");
		dgProperty3.addColumn("编制时间", "date1", "showDate:yyyy-MM-dd");
		dgProperty3.addColumn("一级复核人", "User5");
		dgProperty3.addColumn("复核时间", "date5", "showDate:yyyy-MM-dd");
		dgProperty3.setInputType("checkbox");
		dgProperty3.setWhichFieldIsValue(1);
		dgProperty3.setTrActionProperty(true);
		dgProperty3
				.setTrAction("id='tr${taskId}' fullPath='${fullpath}' taskId='${taskId}' manuId='${manuid}' style='cursor:hand;' onDBLclick=\"openTaskFile(this);\"");

		StringBuffer sql3 = new StringBuffer();
		sql3.append(" select taskId,TaskCode,TaskName,manuid,fullpath, ");
		sql3.append(" u1.name as User1,date1, ");
		sql3.append(" u5.name as User5,date5, ");
		sql3.append(" u2.name as User2,date2, ");
		sql3.append(" u3.name as User3,date3 ");
		sql3.append(" from z_Task t");
		sql3.append(" left join k_user u1 on t.user1 = u1.id ");
		sql3.append(" left join k_user u2 on t.user2 = u2.id ");
		sql3.append(" left join k_user u3 on t.user3 = u3.id ");
		sql3.append(" left join k_user u5 on t.user5 = u5.id ");
		sql3.append(" where ProjectID = '" + curProjectId + "'");
		sql3.append(" and IsLeaf = 1 ");
		sql3.append(" and ((user5 is not null and user5 !='') or (user1 is not null and user1 !=''))");
		sql3.append(" and user1 != '" + userId + "'");

		switch (type) {
		case 5:
			//关注底稿
			sql.append(" And auditproperty like '%1%'");
			dgProperty.addColumn("一级复核人", "User5");
			dgProperty.addColumn("复核时间", "date5", "showDate:yyyy-MM-dd");

			break;

		case 2:
			//关注底稿
			sql.append(" And (auditproperty like '%2%') ");
			dgProperty.addColumn("一级复核人", "User5");
			dgProperty.addColumn("复核时间", "date5", "showDate:yyyy-MM-dd");
			dgProperty.addColumn("二级复核人", "User2");
			dgProperty.addColumn("复核时间", "date2", "showDate:yyyy-MM-dd");

			break;

		case 3:
			//关注底稿
			sql.append(" And (auditproperty like '%3%') ");
			dgProperty.addColumn("一级复核人", "User5");
			dgProperty.addColumn("复核时间", "date5", "showDate:yyyy-MM-dd");
			dgProperty.addColumn("二级复核人", "User2");
			dgProperty.addColumn("复核时间", "date2", "showDate:yyyy-MM-dd");
			dgProperty.addColumn("三级复核人", "User3");
			dgProperty.addColumn("复核时间", "date3", "showDate:yyyy-MM-dd");
			break;

		default:
			break;
		}

		//关注底稿
		dgProperty.setSQL(sql.toString());
		dgProperty.setOrderBy_CH("orderid");
		dgProperty.setDirection("asc");
		session.setAttribute(DataGrid.sessionPre + dgProperty.getTableID(),
				dgProperty);

		//退回底稿
		dgProperty2.setSQL(sql2.toString());
		dgProperty2.setOrderBy_CH("orderid");
		dgProperty2.setDirection("asc");
		session.setAttribute(DataGrid.sessionPre + dgProperty2.getTableID(),
				dgProperty2);

		//所有底稿
		dgProperty3.setSQL(sql3.toString());
		dgProperty3.setOrderBy_CH("orderid");
		dgProperty3.setDirection("asc");
		session.setAttribute(DataGrid.sessionPre + dgProperty3.getTableID(),
				dgProperty3);

		modelAndView.addObject("labelValue", labelValues[type]);
		modelAndView.addObject("type", new Integer(type));
		modelAndView.addObject("projectId", curProjectId);
		modelAndView.addObject("state", String.valueOf(state));


		DataGridProperty dgProperty4 = new DataGridProperty();

		dgProperty4.addColumn("测试环节", "taskname");
		dgProperty4.addColumn("实质性程序", "AuditProcedure");
		dgProperty4.addColumn("备注", "remark");
		dgProperty4.addColumn("被谁改为不适用", "notApplicableMan");
		dgProperty4.addColumn("改为不适用时间", "notApplicableDate");

		String sql4 = "select distinct a.AutoID as AutoID,a.AuditProcedure as AuditProcedure,a.remark as remark,b.taskname as taskname,k1.name as notApplicableMan,"
					+ "a.notApplicableDate as notApplicableDate "
					+ " from z_procedure a "
					+ " inner join z_task b on a.TaskID = b.TaskId and a.ProjectId=b.ProjectId "
					+ " left join k_user k1 on k1.id=a.notApplicableMan "
					+ " where a.State='不适用' "
					+ " and a.ProjectID='" + curProjectId + "' "
					+ " and a.approvalMan is null "
					+ " and a.notApplicableMan is not null";

		dgProperty4.setCustomerId(customerId);
		dgProperty4.setTitle("等待审批的不适用程序");
		dgProperty4.setTableID("notApplicableProcedureList");
		dgProperty4.setInputType("checkbox");
		dgProperty4.setWhichFieldIsValue(1);
		dgProperty4.setSQL(sql4);
		dgProperty4.setOrderBy_CH("AutoID");
		dgProperty4.setDirection("asc");
		dgProperty4.setPrintTitle("等待审批的不适用程序");
		dgProperty4.setPrintEnable(true);
		dgProperty4.setPrintVerTical(false);

		DataGridProperty dgProperty5 = new DataGridProperty();

		dgProperty5.addColumn("测试环节", "taskname");
		dgProperty5.addColumn("实质性程序", "AuditProcedure");
		dgProperty5.addColumn("备注", "remark");
		dgProperty5.addColumn("被谁改为不适用", "notApplicableMan");
		dgProperty5.addColumn("改为不适用时间", "notApplicableDate");
		dgProperty5.addColumn("审批人", "approvalMan");
		dgProperty5.addColumn("审批时间", "approvalDate");

		String sql1 = "select distinct a.AutoID as AutoID,a.AuditProcedure as AuditProcedure,a.remark as remark,b.taskname as taskname,k1.name as notApplicableMan,"
					+ "a.notApplicableDate as notApplicableDate,k2.name as approvalMan,a.approvalDate as approvalDate"
					+ " from z_procedure a "
					+ " inner join z_task b on a.TaskID = b.TaskId and a.ProjectId=b.ProjectId "
					+ " left join k_user k1 on k1.id=a.notApplicableMan left join k_user k2 on k2.id=a.approvalMan "
					+ " where a.ProjectID='" + curProjectId + "' "
					+ " and a.notApplicableMan is not null "
					+ " and a.approvalMan is not null";

		dgProperty5.setCustomerId(customerId);
		dgProperty5.setTitle("已经审批的不适用程序");
		dgProperty5.setTableID("applicabledProcedureList");
		dgProperty5.setSQL(sql1);
		dgProperty5.setOrderBy_CH("AutoID");
		dgProperty5.setDirection("asc");
		dgProperty5.setPrintTitle("已经审批的不适用程序");
		dgProperty5.setPrintEnable(true);
		dgProperty5.setPrintVerTical(false);

		session.setAttribute(DataGrid.sessionPre + dgProperty4.getTableID(),dgProperty4);
		session.setAttribute(DataGrid.sessionPre + dgProperty5.getTableID(),dgProperty5);

		modelAndView.addObject("state", String.valueOf(state));
		modelAndView.addObject("warnStr",warnStr);
		return modelAndView;
	}

	/**
	 * 执行批量审核
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView batchAuditing(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();

		String[] taskIds = request
				.getParameterValues("choose_waitAuditTaskList");
		String typeTemp = request.getParameter("type");
		String menuId = request.getParameter("menuId");
		String url = "taskCommon.do?method=batchAuditingList&type=" + typeTemp
				+ "&menuId=" + menuId;
		int type = 0;

		try {
			type = Integer.parseInt(typeTemp);
		} catch (Exception e) {
			e.printStackTrace();
			response.sendRedirect(url);
		}

		if (taskIds == null) {
			response.sendRedirect(url);
			return null;
		}

		Connection conn = null;
		try {
//			 取得用户Session
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session
					.getAttribute("userSession");
			String acc = userSession.getCurAccPackageId();
			conn = new DBConnect().getConnect(acc.substring(0, 6));


			String curProjectId = request.getParameter("projectId");

			if (curProjectId == null || "".equals(curProjectId)) {
				curProjectId = userSession.getCurProjectId(); // 当前项目编号
			}

			ProjectService ps = new ProjectService(conn);
			int state = ps.getState(curProjectId);
			UserdefService udf = new UserdefService(conn);

			boolean hasAuthority2 = false;
			boolean hasAuthority3 = false;
			// 尚未进入二审
			if (type == 2) {
				if (state != 5) {
					out
							.println("<script type=\"text/javascript\">alert('项目尚未进入二审状态')</script>");
					return null;
				} else {
					hasAuthority2 = udf.hasAuthority2(userSession
							.getUserId(), curProjectId);
					if (!hasAuthority2) {
						out
								.println("<script type=\"text/javascript\">alert('用户无权复核')</script>");
						return null;
					}
				}
			} else if (type == 3) {
				if (state != 6) {
					out
							.println("<script type=\"text/javascript\">alert('项目尚未进入三审状态')</script>");
					return null;
				} else {
					hasAuthority3 = udf.hasAuthority3(userSession
							.getUserId(), curProjectId);
					if (!hasAuthority3) {
						out
								.println("<script type=\"text/javascript\">alert('用户无权复核')</script>");
						return null;
					}
				}
			}

			String curProjectName = userSession.getCurProjectName();
			String userId = userSession.getUserId();
			TaskCommonService taskCommonService = new TaskCommonService(conn,
					curProjectId);
			PlacardService placardMan = new PlacardService(conn);
			TaskService taskService = new TaskService(conn, curProjectId);
			Task task = null;
			String caption = "";
			String msgContent = "";

			ASFuntion asf = new ASFuntion();

			switch (type) {
			case 5:
				// 如果是一级复核
				caption = "一级复核完成信息";
				msgContent = "一级复核已经完成";
				break;

			case 2:
				caption = "二级复核完成信息";
				msgContent = "二级复核已经完成";
				break;

			case 3:
				caption = "三级复核完成信息";
				msgContent = "三级复核已经完成";
				break;

			default:
				return null;
			}

			AuditPeopleService auditPeopleService = new AuditPeopleService(
					conn, curProjectId);
			List auditPeopleList = auditPeopleService.getAuditPeopleId();

			// 发信息给编制人
			auditPeopleList.remove(userId); // 首先把自己从复核人员中去掉

			// 返回编制或审核结果
			for (int i = 0; i < taskIds.length; i++) {
				try {
					// 编制底稿
					taskCommonService.auditing(userId, taskIds[i], type, null,null);

					// 下面的代码是发送公告给编制人
					task = taskService.getTaskByTaskId(taskIds[i]);
					StringBuffer msg = new StringBuffer();
					msg.append("<br />项目名称:" + curProjectName);
					msg.append("<br />底稿名称:" + task.getTaskName());
					msg.append("<br />索引号:" + task.getTaskCode());
					msg.append("<br /><br />");
					msg.append(msgContent);
					msg
							.append("&nbsp;&nbsp;<a href='/AuditSystem/taskCommon.do?method=fileOpen");
					msg.append("&projectId=" + curProjectId);
					msg.append("&taskId=" + taskIds[i]);
					msg.append("' target='_self'>点击查看底稿</a>");

					auditPeopleList.remove(task.getUser1()); // 把编制人从有权复核人中删除
					auditPeopleList.add(task.getUser1()); // 添加编制人

					PlacardTable placard = new PlacardTable();
					placard.setCaption(caption);
					placard.setMatter(msg.toString());
					placard.setIsReversion(0);
					placard.setAddresser(userId);
					placard.setAddresserTime(asf.getCurrentDate() + " "
							+ asf.getCurrentTime());
					placard.setIsRead(0);

					for (int j = 0; j < auditPeopleList.size(); j++) {
						placard.setAddressee(auditPeopleList.get(j).toString());
						placardMan.AddPlacard(placard);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		response.sendRedirect(url);
		return null;
	}

	/**
	 * 重置底稿
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView recover(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		Connection conn = null;
		PrintWriter out = response.getWriter();

		try {
			conn = new DBConnect().getConnect("");

			// 取得用户Session
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session
					.getAttribute("userSession");

			String curProjectId = request.getParameter("projectId");

			if (curProjectId == null || "".equals(curProjectId)) {
				curProjectId = userSession.getCurProjectId(); // 当前项目编号
			}

			// 底稿ID
			String taskId = request.getParameter("taskId");

			TaskService taskService = new TaskService(conn, curProjectId);

			// 如果taskid为空,就取taskCode
			if ("".equals(taskId) || taskId == null) {
				String taskCode = request.getParameter("taskCode");
				taskId = taskService.getTaskIdByTaskCode(taskCode);
			}

            // 移动到回收站
			new TaskRecycleService(conn,curProjectId).moveToRecycle(taskId,"0");

			taskService.recoverTask(taskId);

			out.write("重置底稿成功!!");

		} catch (Exception e) {
			out.write("重置底稿失败：" + e.getMessage());
			//throw e;
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 搜索底稿
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView search(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView();

		String selproTemp = request.getParameter("selpro");
		String selvalue = request.getParameter("selvalue");

		int selpro = 0;
		try {
			selpro = Integer.parseInt(selproTemp);
		} catch (Exception e) {
			e.printStackTrace();
		}

		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session
				.getAttribute("userSession");

		String curProjectId = request.getParameter("projectId");

		if (curProjectId == null || "".equals(curProjectId)) {
			curProjectId = userSession.getCurProjectId(); // 当前项目编号
		}
		
		if (curProjectId == null || "".equals(curProjectId)) {
			if (!new CommonSecurity(request, response).checkProjectLogin()) {
				return null;
			}
		}

		String curProjectProperty = userSession.getCurProjectProperty();

		DataGridProperty dgProperty = new DataGridProperty();
		dgProperty.setTableID("searchTaskList");
		dgProperty.setCurProjectDatabase(true);
		dgProperty.setInputType("radio");
		dgProperty.setWhichFieldIsValue(2);

		dgProperty.addColumn("任务编号", "TaskCode");
		dgProperty.addColumn("任务名称", "TaskName");
		dgProperty.addColumn("编制人", "User1");
		dgProperty.addColumn("编制时间", "date1", "showDate:yyyy-MM-dd");
		dgProperty.addColumn("退回时间", "date4", "showDate:yyyy-MM-dd");
		dgProperty.addColumn("一级复核人", "User5");
		dgProperty.addColumn("一级复核时间", "date5", "showDate:yyyy-MM-dd");
		dgProperty.addColumn("二级复核人", "User2");
		dgProperty.addColumn("二级复核时间", "date2", "showDate:yyyy-MM-dd");
		dgProperty.addColumn("三级复核人", "User3");
		dgProperty.addColumn("三级复核时间", "date3", "showDate:yyyy-MM-dd");
		dgProperty.addColumn("最后保存人", "UserName");
		dgProperty.addColumn("最后保存时间", "Udate");
		dgProperty.setTrActionProperty(true);
		dgProperty
				.setTrAction("id='tr${taskId}' fullPath='${fullpath}' taskId='${taskId}' manuId='${manuid}' style='cursor:hand;' onDBLclick=\"openTaskFile(this);\"");

		dgProperty.setPrintEnable(true);
		dgProperty.setPrintVerTical(true);
		dgProperty.setPrintTitle("底稿列表");

		StringBuffer sql = new StringBuffer();
		sql
				.append(" select a.manuid,TaskID,orderid,fullpath,TaskCode,TaskName,");
		sql.append(" u1.name as user1,date1, ");
		sql.append(" date4, ");
		sql.append(" u5.name as user5,date5, ");
		sql.append(" u2.name as User2,date2, ");
		sql.append(" u3.name as user3,date3, ");
		sql
				.append(" case when ismust like '%1%' and user1 is not null and user1 <>'' then '必作已做' ");
		sql
				.append(" when ismust like '%1%' and (user1 is null ||user1 ='') then '必作未做' end as ismustdo,a.UserName,a.Udate ");
		sql.append(" From z_Task a ");
		sql.append(" left join k_user u1 on a.user1 = u1.id ");
		sql.append(" left join k_user u2 on a.user2 = u2.id ");
		sql.append(" left join k_user u3 on a.user3 = u3.id ");
		sql.append(" left join k_user u5 on a.user5 = u5.id ");
		sql.append(" Where a.ProjectId='" + curProjectId + "'");
		sql.append(" and IsLeaf=1 ");
		// sql.append(" and (a.property<>'A1' or a.property is null) ");

		switch (selpro) {
		case 0:
			//按底稿属性查找
			sql.append(" and a.property = '" + selvalue + "'");
			break;
			
		case 1:
			// 按底稿编号查找
			sql.append(" and (TaskCode Like concat(replace('" + selvalue
					+ "','－','-'),'%')) ");
			break;

		case 2:
			// 按底稿名称查找
			sql.append(" and TaskName Like '%" + selvalue + "%' ");
			break;

		case 3:
			// 按编制人查找
			sql.append(" and u1.name Like '%" + selvalue + "%'");
			break;

		case 4:
			// 按编二级复核人查找
			sql.append(" and u2.name Like '%" + selvalue + "%'");
			break;

		case 5:
			// 按编三级复核人查找
			sql.append(" and u3.name Like '%" + selvalue + "%'");
			break;

		case 6:
			// 查找空白底稿
			sql
					.append(" And (User1 Is NULL || User1 = '') And (User2 Is NULL || User2 = '') And (User3 Is NULL || User3 = '') ");
			break;

		case 7:
			// 查找已编制完成底稿
			sql.append(" And (User1 Is Not NULL And User1 != '')");
			break;

		case 8:
			// 查找已二级复核底稿
			if (curProjectProperty.length() == 3) {
				sql
						.append(" And (User5 Is Not NULL And User5 != '') And (User2 Is Not NULL And User2 != '') ");
			} else {
				sql
						.append(" And (User1 Is Not NULL And User1 != '') And (User2 Is Not NULL And User2 != '') ");
			}
			break;

		case 9:
			// 查找已三级复核底稿
			sql
					.append(" And (User1 Is Not NULL And User1 != '') And (User3 Is Not NULL And User3 != '') ");
			break;

		case 10:
			String state = new ASFuntion().showNull(request
					.getParameter("state"));
			
			String showAll = request.getParameter("showAll");
			if(showAll!=null && !"".equals(showAll)) {
				modelAndView.addObject("showAll",showAll);
			}
			//必做有数据
			if("13".equals(state)) {
				sql.append(" and (a.SubjectName = '' or a.SubjectName is  null or a.ismust like '%1%' or a.ismust like '%3%') ");
				//sql.append(" and (ismust like '%1%' or ismust like '%3%') ");
			} else {
				if ("1".equals(state)) {
					// 查找必作项
					dgProperty.addColumn("必作项状态", "ismustdo");
				}
				modelAndView.addObject("state",state);
				sql.append(" and ismust like '%" + state + "%'");
			}
			break;

		case 11:
			// 查找已退回的底稿
			sql.append(" And (User4 Is Not NULL And User4 != '') ");
			break;

		case 12:
			// 查找已一级复核底稿
			sql
					.append(" And (User1 Is Not NULL And User1 != '') And (User5 Is Not NULL And User5 != '')");
			break;

		case 13:
			// 按一级复核人查找
			sql.append(" And u5.name Like '%" + selvalue + "%'");
			break;

		case 14:
			// 目标编号
			request.getRequestDispatcher("taskCommon.do?method=searchTarget")
					.forward(request, response);
			return null;

		case 15:
			// 目标名称
			request.getRequestDispatcher("taskCommon.do?method=searchTarget")
					.forward(request, response);
			return null;
		case 16:
			//根据parentId获得所有底稿
			Connection conn = null;
			String taskIds = "-1";
			
			try{
				conn = new DBConnect().getConnect("");
				List taskList = new TaskService(conn, curProjectId).getTaskListByParentTaskId(selvalue);
				
				
				Task task = null;
				for(int i=0; i < taskList.size(); i++) {
					task = (Task)taskList.get(i);
					taskIds += "," + task.getTaskId();
				}
				
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				DbUtil.close(conn);
			}
			
			System.out.println("taskIds:" + taskIds);
			
			
			sql.append(" and a.taskId in (").append(taskIds).append(") and a.isleaf=1 ");
			break;
		case 17:
			//获得92模板里的1、2两类底稿
			
			Connection conn2 = null;
			String taskIds2 = "-1";
			
			try{
				conn2 = new DBConnect().getConnect("");
				String sqlWhere = " and (fullpath like '1|%' or fullpath like '2|%') " ;
				taskIds2 += ","+new TaskService(conn2, curProjectId).getTasksBytemplateIdAndType(sqlWhere,"92");
				
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				DbUtil.close(conn2);
			}
			
			sql.append(" and a.taskId in (").append(taskIds2).append(") and a.isleaf=1 ");
			break;
		case 18:
			//获得92模板里的1、2两类底稿
			
			Connection conn3 = null;
			String taskIds3 = "-1";
			
			try{
				conn3 = new DBConnect().getConnect("");
				String sqlWhere = " and fullpath like '4|%' " ;
				taskIds3 += ","+new TaskService(conn3, curProjectId).getTasksBytemplateIdAndType(sqlWhere,"92");
				
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				DbUtil.close(conn3);
			}
			
			sql.append(" and a.taskId in (").append(taskIds3).append(") and a.isleaf=1 ");
			break;
		
		case 19:
			
			String inSql = " select taskId from( "
						+ " select b.taskId from z_task a,z_task b "
						+ " where a.taskcode='" + selvalue + "' "  
						+ " and a.projectid='" + curProjectId + "'"
						+ " and b.projectid='" + curProjectId + "'"
						+ " and b.fullpath like concat(a.fullpath,'|%') "
						+ " and b.isleaf=1 ) tt ";
			
			
			//sql.append(" and a.taskId in (").append(inSql).append(") and a.isleaf=1 ");
			sql.append(" and (a.taskId in (").append(inSql).append(") or a.taskcode='" + selvalue + "') and a.isleaf=1 ");
			
			break;
		default:
			break;
		}

		dgProperty.setSQL(sql.toString());
		dgProperty.setOrderBy_CH("orderid");
		dgProperty.setDirection("asc");
		session.setAttribute(DataGrid.sessionPre + dgProperty.getTableID(),
				dgProperty);

		modelAndView.setViewName(SEARCH_LIST_VIEW);
		return modelAndView;
	}

	/**
	 * 搜索审计目标
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView searchTarget(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView();

		String selproTemp = request.getParameter("selpro");
		String selvalue = request.getParameter("selvalue");

		int selpro = 0;
		try {
			selpro = Integer.parseInt(selproTemp);
		} catch (Exception e) {
			e.printStackTrace();
		}

		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session
				.getAttribute("userSession");

		String curProjectId = request.getParameter("projectId");

		if (curProjectId == null || "".equals(curProjectId)) {
			curProjectId = userSession.getCurProjectId(); // 当前项目编号
		}

		DataGridProperty dgProperty = new DataGridProperty();
		dgProperty.setTableID("searchTaskList");
		dgProperty.setCurProjectDatabase(true);
		dgProperty.setInputType("radio");
		dgProperty.setWhichFieldIsValue(1);

		// 目标状态,目标编号,目标名称,相关执行程序
		dgProperty.addColumn("目标状态", "state");
		dgProperty.addColumn("目标编号", "defineid");
		dgProperty.addColumn("目标名称", "audittarget");
		dgProperty.addColumn("相关执行程序", "CorrelationExeProcedure");

		dgProperty.setPrintEnable(true);
		dgProperty.setPrintColumnWidth("12,12,72,30");
		dgProperty.setPrintTitle("审计目标列表");

		dgProperty.setTrActionProperty(true);
		dgProperty
				.setTrAction(" taskId=${TaskID} style='cursor:hand;' onDBLclick=\"openTarget(this);\"");

		StringBuffer sql = new StringBuffer();
		sql
				.append(" select TaskID,state,defineid,audittarget,CorrelationExeProcedure ");
		sql.append(" From z_target Where ProjectId = '" + curProjectId + "'");

		switch (selpro) {
		case 14:
			// 目标编号
			sql.append(" And DefineID Like '" + selvalue + "%'");
			break;

		case 15:
			// 目标名称
			sql.append(" And audittarget Like '%" + selvalue + "%'");
			break;

		default:
			break;
		}

		dgProperty.setSQL(sql.toString());
		dgProperty.setOrderBy_CH("DefineID");
		dgProperty.setDirection("asc");
		session.setAttribute(DataGrid.sessionPre + dgProperty.getTableID(),
				dgProperty);

		modelAndView.setViewName(SEARCH_LIST_VIEW);
		return modelAndView;
	}

	/**
	 * 保存打印的底稿
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView fileUploadByPrint(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		Connection conn = null;

		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session
				.getAttribute("userSession");

		try {
			conn = new DBConnect().getConnect("");

			String projectId = request.getParameter("curProjectid");
			;
			org.util.Debug.prtOut("qwh:projectid=" + projectId);

			if (projectId == null || projectId.equals("")) {
				projectId = userSession.getCurProjectId();
			}

			MyFileUpload fileUpload = new MyFileUpload(request, conn);

			String customerId = new CustomerService(conn)
					.getCustomerIdByProjectId(projectId);

			// 新增
			String uploadResult = fileUpload.Upload(projectId, customerId);
			PrintWriter out = response.getWriter();
			if (uploadResult != null) {
				out.print("文件保存成功！");
			} else {
				out.print("文件保存失败！");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}

	/**
	 * 保存用户在当前项目最后一次打开的底稿
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView savaUserState(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		Connection conn = null;

		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session
				.getAttribute("userSession");

		try {
			conn = new DBConnect().getConnect("");

			String projectId = request.getParameter("projectId");

			if (projectId == null || "".equals(projectId)) {
				projectId = userSession.getCurProjectId(); // 当前项目编号
			}

			String userId = userSession.getUserId();
			String taskId = request.getParameter("taskId");

			UserStateService userStateService = new UserStateService(conn);

			UserState userState = userStateService.getUserState(userId,
					projectId);

			if (userState != null) {
				userState.setLastTaskId(taskId);
				userStateService.updateUserState(userState);
			} else {
				userState = new UserState();
				userState.setUserId(userId);
				userState.setProjectId(projectId);
				userState.setLastTaskId(taskId);

				userStateService.saveUserState(userState);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}

	/**
	 * 移动底稿界面
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView moveView(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(MOVE_VIEW);

		ASFuntion asfuntion = new ASFuntion();

		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session
				.getAttribute("userSession");

		String taskId = asfuntion.showNull(request.getParameter("taskId"));
		String taskType = asfuntion.showNull(request.getParameter("taskType"));
		String curProjectId = request.getParameter("projectId");

		if (curProjectId == null || "".equals(curProjectId)) {
			curProjectId = userSession.getCurProjectId(); // 当前项目编号
		}

		Connection conn = null;

		try {
			conn = new DBConnect().getConnect("");
			Task task = null;
			TaskService taskService = new TaskService(conn, curProjectId);
			task = taskService.getTaskByTaskId(taskId);

			modelAndView.addObject("task", task);
			modelAndView.addObject("taskType", taskType);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		return modelAndView;
	}

	/**
	 * 移动底稿
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView move(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ASFuntion asfuntion = new ASFuntion();

		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session
				.getAttribute("userSession");

		String curProjectId = request.getParameter("projectId");

		if (curProjectId == null || "".equals(curProjectId)) {
			curProjectId = userSession.getCurProjectId(); // 当前项目编号
		}

		String oldParentId = asfuntion.showNull(request
				.getParameter("oldParentId"));
		String newParentId = asfuntion.showNull(request
				.getParameter("newParentId"));
		String taskId = request.getParameter("taskId");
		String taskType = request.getParameter("taskType");
		String url = "";

		Connection conn = null;

		try {
			conn = new DBConnect().getConnect("");

			TaskService taskService = new TaskService(conn, curProjectId);
			taskService.moveTask(taskId, oldParentId, newParentId);

			String curauditTypeProperty = new AuditTypeTemplateService(conn)
					.getPropertyByProjectId(curProjectId);

			if ("task".equals(taskType)) {
				if ("1".equals(curauditTypeProperty)) {
					// 风险导向审计环节
					url = "tache.do?method=tacheList&refresh=true";
				} else {
					// 普通任务结点
					url = "taskSort.do?method=list&refresh=true";
				}
			} else if ("taskManu".equals(taskType)) {
				if ("1".equals(curauditTypeProperty)) {
					// 风险导向审计底稿
					url = "tache.do?method=list&refresh=true";
				} else {
					// 普通任务底稿
					url = "task.do?method=list&refresh=true";
				}
			}

			response.sendRedirect(url);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 判断底稿是否已经编制或已经复核
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView isComplete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");

		// 取得用户Session
		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session
				.getAttribute("userSession");

		String curProjectId = request.getParameter("projectId");

		if (curProjectId == null || "".equals(curProjectId)) {
			curProjectId = userSession.getCurProjectId(); // 当前项目编号
		}

		String taskId = request.getParameter("taskId"); // 任务编号

		PrintWriter out = response.getWriter();

		Connection conn = null;

		try {
			conn = new DBConnect().getConnect("");
			TaskService taskService = new TaskService(conn, curProjectId);
			out.println(taskService.isCompleteByTaskId(taskId));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 判断用户已经打开该底稿
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView isOpen(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");

		// 取得用户Session
		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session
				.getAttribute("userSession");

		String curProjectId = request.getParameter("projectId");

		if (curProjectId == null || "".equals(curProjectId)) {
			curProjectId = userSession.getCurProjectId(); // 当前项目编号
		}

		String taskId = request.getParameter("taskId"); // 任务编号

		Connection conn = null;

		boolean isOpen = false;

		PrintWriter out = response.getWriter();

		try {
			conn = new DBConnect().getConnect("");

			// list存放着所有在线用户的各种信息
			List list = OnlineListListener.getList();

			// 检查是否该文件已经被人打开
			for (int i = 0; i < list.size() && !isOpen; i++) {

				UserSession us = (UserSession) list.get(i);
				Set userCurTasks = us.getUserCurTasks();
				Iterator it = userCurTasks.iterator();

				while (it.hasNext()) {
					UserCurTask userCurTask = (UserCurTask) it.next();

					if (taskId.equals(userCurTask.getCurTaskId())
							&& curProjectId.equals(userCurTask
									.getCurTaskProjectId())) {
						// 如果已经有人打开了这个文件
						isOpen = true;
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		out.print(isOpen);
		return null;
	}

	/**
	 * 获得底稿以及意见信息,给控件调用
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getTaskInfo(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");

		String curProjectId = request.getParameter("curProjectid"); // 项目编号
		String taskId = request.getParameter("curTaskId"); // 任务编号

		Connection conn = null;

		PrintWriter out = response.getWriter();

		try {
			conn = new DBConnect().getConnect("");
			TaskCommonService taskCommonService = new TaskCommonService(conn,
					curProjectId);
			Set set = taskCommonService.getTaskInfo(taskId);
			Iterator it = set.iterator();

			while (it.hasNext()) {
				out.print((String) it.next() + "`");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 设置底稿为关注
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView setAtitonTo(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");

		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session
				.getAttribute("userSession");

		String curProjectId = request.getParameter("projectId");

		if (curProjectId == null || "".equals(curProjectId)) {
			curProjectId = userSession.getCurProjectId(); // 当前项目编号
		}

		String taskId = request.getParameter("taskId"); // 任务编号
		String isAtionTo = request.getParameter("isAtionTo");// 是否关注

		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			TaskCommonService taskCommonService = new TaskCommonService(conn,
					curProjectId);

			if (isAtionTo.equals("1")) {
				taskCommonService.setTaskState(taskId,
						TaskCommonService.TASK_STATE_CODE_ATTENTION, true);
			} else {
				taskCommonService.setTaskState(taskId,
						TaskCommonService.TASK_STATE_CODE_ATTENTION, false);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 获取分工人的底稿列表
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getTaskListByUser0(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String user0 = request.getParameter("user0"); // 人员编号
		String projectId = request.getParameter("projectId"); // 项目编号

		HttpSession session = request.getSession();

		Connection conn = null;

		DataGridProperty dgProperty = new DataGridProperty() {
			public void onSearch(javax.servlet.http.HttpSession session,
					javax.servlet.http.HttpServletRequest request,
					javax.servlet.http.HttpServletResponse response)
					throws Exception {
			}
		};

		String customerId = null;
		try {
			conn = new DBConnect().getConnect("");
			customerId = new CustomerService(conn)
					.getCustomerIdByProjectId(projectId);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		dgProperty.setTableID("user0TaskList");
		dgProperty.setInputType("checkbox");
		dgProperty.setCustomerId(customerId);
		dgProperty.setWhichFieldIsValue(1);
		dgProperty.setTrActionProperty(true);
		dgProperty.setTrAction("style='cursor:hand;' taskId=\"${taskId}\" ");
		dgProperty.setPrintTitle("底稿分类列表");

		dgProperty.addColumn("任务编号", "TaskCode");
		dgProperty.addColumn("任务名称", "TaskName");

		String sql = " select taskId,taskcode,taskname,orderid "
				+ " from z_task a left join k_user " + " where user0='"
				+ user0.split("-")[0] + "'" + " and projectId='" + projectId
				+ "' " + " and isleaf=1 ";

		dgProperty.setSQL(sql);
		dgProperty.setOrderBy_CH("orderid");
		dgProperty.setDirection("asc");
		session.setAttribute(DataGrid.sessionPre + dgProperty.getTableID(),
				dgProperty);

		System.out.println("ssss");

		return new ModelAndView(USER0_TASK_LIST_VIEW);
	}

	/**
	 * 返回某张底稿的parentTaskId,参数为:projectid,taskid
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getParentTaskId(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		String parenttaskid = "";
		try {
			String projectid = request.getParameter("projectid");
			String taskid = request.getParameter("taskid");
			conn = new DBConnect().getConnect("");

			new DBConnect().changeDataBaseByProjectid(projectid, conn);
			sql = " select parenttaskid from z_task where taskid=? and projectid=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, taskid);
			ps.setString(2, projectid);
			rs = ps.executeQuery();

			if (rs.next()) {
				parenttaskid = rs.getString(1);
			}
			if (parenttaskid == null) {
				parenttaskid = "";
			}
			response.setContentType("text/html;charset=utf-8"); // 设置编码
			PrintWriter out = response.getWriter();
			out.print(parenttaskid);
			out.close();
		} catch (Exception e) {
			Debug.print(Debug.iError, "读取底稿信息失败！", e);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 验证底稿编号是否存在
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView validateTaskCode(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Connection conn = null;
		ASFuntion asf = new ASFuntion();

		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		PrintWriter out = response.getWriter();

		try {
			// 取得用户Session
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session
					.getAttribute("userSession");

			String taskCode = asf.showNull(request.getParameter("taskCode"));

			String curProjectId = request.getParameter("projectId");

			if (curProjectId == null || "".equals(curProjectId)) {
				curProjectId = userSession.getCurProjectId(); // 当前项目编号
			}

			conn = new DBConnect().getConnect("");
			TaskService taskService = new TaskService(conn, curProjectId);

			String noTaskCodes = taskService.validateTaskCode(taskCode);

			out.print(noTaskCodes);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 根据上级taskCode获得新的taskCode
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getNewTaskCode(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ASFuntion asf = new ASFuntion();
		Connection conn = null;
		String taskCode = "";
		try {
			conn = new DBConnect().getConnect("");
			String parentTaskCode = asf.showNull(request
					.getParameter("parentTaskCode"));
			UserSession userSession = (UserSession) request.getSession()
					.getAttribute("userSession");
			String projectId = asf.showNull(request.getParameter("projectId"));
			String taskname = asf.showNull(request.getParameter("taskname"));
			String parentTaskId = asf.showNull(request.getParameter("parentTaskId"));
			
			String token=asf.showNull(request.getParameter("token"));
			
			String opt = asf.showNull(request.getParameter("opt"));
			
			if ("".equals(projectId)) {
				projectId = userSession.getCurProjectId();
			}

			TaskCommonService taskCommonService = new TaskCommonService(conn,
					projectId);
			TaskService taskService = new TaskService(conn, projectId);

			String taskId = null;
			
			if (!"".equals(parentTaskId)) {
				taskId = parentTaskId;
			} else if ("".equals(taskname)) {
				taskId = taskService.getTaskIdByTaskCode(parentTaskCode);
			} else {
				taskId = taskService.getTaskIdByTaskCode(parentTaskCode,
						taskname);
			}

			if(!"".equals(opt)) {	//为自由函证生成taskCode
				taskId = "";
			}
			
			if ("".equals(taskId)) {
				taskCode = taskCommonService.getMaxTaskCodeByParentTaskId("0");
				taskCode = UTILString.getNewTaskCode(taskCode);
			} else {
				taskCode = taskCommonService.getMaxTaskCodeByParentTaskId(taskId, 1);
				
				if ("".equals(token)){
					taskCode = UTILString.getNewTaskCode(taskCode);
				}else{
					taskCode = UTILString.getNewTaskCode(taskCode+token);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}

		response.setContentType("text/html;charset=utf-8"); // 设置编码
		PrintWriter out = response.getWriter();
		org.util.Debug.prtOut("qwh:taskCode=" + taskCode);
		out.print(taskCode);
		out.close();

		return null;
	}

	/**
	 * 设置底稿状态
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView setTaskState(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		Connection conn = null;

		try {
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session
					.getAttribute("userSession");

			String projectId = request.getParameter("projectId");
			String showTaskCodes = request.getParameter("showTaskCodes");
			String hiddenTaskCodes = request.getParameter("hiddenTaskCodes");

			if (projectId == null || "".equals(projectId)) {
				projectId = userSession.getCurProjectId();
			}

			conn = new DBConnect().getConnect("");
			TaskService taskService = new TaskService(conn, projectId);
			taskService.setHiddenTask(hiddenTaskCodes);
			taskService.setShowTask(showTaskCodes);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}
	
	/**
	 * 设置底稿状态
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView setTaskShowAndHidden(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		Connection conn = null;

		try {
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session
					.getAttribute("userSession");

			String projectId = request.getParameter("projectId");
			String taskCodes = request.getParameter("taskCodes");
			String hiddenTaskCodes = request.getParameter("hiddenTaskCodes");

			if (projectId == null || "".equals(projectId)) {
				projectId = userSession.getCurProjectId();
			}

			conn = new DBConnect().getConnect("");
			TaskService taskService = new TaskService(conn, projectId);
			
			if(!"".equals(taskCodes)){
				taskService.setTaskShowAndHidden(taskCodes);	
				System.out.println("taskCodes:" + taskCodes);
			} else if(!"".equals(hiddenTaskCodes)) {
				taskService.setTaskHidden(hiddenTaskCodes);
				System.out.println("hiddenTaskCodes:" + hiddenTaskCodes);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 保存项目底稿到模板
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView saveToTemplate(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		Connection conn = null;

		PrintWriter out = response.getWriter();
		try {
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session
					.getAttribute("userSession");

			String projectId = request.getParameter("projectId");
			String taskId = request.getParameter("taskId");

			if (projectId == null || "".equals(projectId)) {
				projectId = userSession.getCurProjectId();
			}

			conn = new DBConnect().getConnect("");
			TaskCommonService taskCommonService = new TaskCommonService(conn,
					projectId);
			taskCommonService.saveToTemplate(taskId);

			out.write("ok");
		} catch (Exception e) {
			out.write("出错了:" + e.getMessage());
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 选择底稿库
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView procedureLib(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(PROCEDURE_LIB_VIEW);

		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		Connection conn = null;

		try {
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session
					.getAttribute("userSession");

			String curProjectId = request.getParameter("projectId");

			if (curProjectId == null || "".equals(curProjectId)) {
				curProjectId = userSession.getCurProjectId();
			}

			String parentTaskId = request.getParameter("parentTaskId");

			// 如果没有parentTaskId
			if (parentTaskId == null || "".equals(parentTaskId)) {
				parentTaskId = "0";
			}

			String toTaskId = request.getParameter("toTaskId");

			if (toTaskId == null) {
				toTaskId = parentTaskId;
			}

			conn = new DBConnect().getConnect("");

			String customerId = new ProjectService(conn).getProjectById(
					curProjectId).getCustomerId();
			ProcedureService procedureService = new ProcedureService(conn,
					curProjectId);
			TaskService taskService = new TaskService(conn, curProjectId);

			procedureService.setLib(true);
			List procedureList = new ArrayList();

			procedureService.getProcedureListByTaskId(procedureList,
					parentTaskId, "0", true);
			Task task = taskService.getTaskByTaskId(parentTaskId);

			modelAndView.addObject("procedureList", procedureList);
			modelAndView.addObject("taskName", task.getTaskName());
			modelAndView.addObject("taskCode", task.getTaskCode());
			modelAndView.addObject("parentTaskId", parentTaskId);
			modelAndView.addObject("toTaskId", toTaskId);
			modelAndView.addObject("projectId", curProjectId);
			modelAndView.addObject("customerId", customerId);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		return modelAndView;
	}

	/**
	 * 设置底稿状态
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView setProcedureState(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		Connection conn = null;

		try {
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session
					.getAttribute("userSession");

			String projectId = request.getParameter("projectId");
			String hidden = request.getParameter("hidden");
			String show = request.getParameter("show");
			String fromTaskId = request.getParameter("fromTaskId");
			String toTaskId = request.getParameter("toTaskId");

			if (projectId == null || "".equals(projectId)) {
				projectId = userSession.getCurProjectId();
			}

			conn = new DBConnect().getConnect("");
			ProcedureService procedureService = new ProcedureService(conn,
					projectId);
			procedureService.setProcedureState(hidden, show, fromTaskId,
					toTaskId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 批量导入
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView upload(HttpServletRequest request,
			HttpServletResponse response) {

		String projectId = request.getParameter("projectId");
		String parentTaskId = request.getParameter("parentTaskId");

		ModelAndView modelAndView = new ModelAndView(UPLOAD);

		modelAndView.addObject("projectId", projectId);
		modelAndView.addObject("parentTaskId", parentTaskId);

		return modelAndView;
	}

	/**
	 * 保存批量导入
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView saveUpload(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		PrintWriter out = null;
		Connection conn = null;
		UserSession us = (UserSession) request.getSession().getAttribute(
				"userSession");
		String lockmsg = "批量导入审计程序和目标";
		try {

			response.setContentType("text/html;charset=utf-8"); // 设置编码
			// response.setHeader("title", "EXCEL导入");
			out = response.getWriter();

			Map parameters = null;

			String uploadtemppath = "";

			String strFullFileName = "";

			MyFileUpload myfileUpload = new MyFileUpload(request);
			uploadtemppath = myfileUpload.UploadFile(null, null);
			parameters = myfileUpload.getMap();
			String projectId = (String) parameters.get("projectId");
			String parentTaskId = (String) parameters.get("parentTaskId");

			// 如果不存在,说明是第一次提交,这个时候从MAP中取值
			uploadtemppath = (String) parameters.get("tempdir");

			strFullFileName = uploadtemppath
					+ (String) parameters.get("filename");
			System.out.println("strFullFileName=" + strFullFileName);
			uploadtemppath = (String) parameters.get("tempdir");

			if (uploadtemppath.equals("")) {
				out.print("Error\n帐套数据上传及预处理失败");
			} else {
				out.println("帐套数据上传并分析成功!<br>正在加载，请等待<br><br><br>");
			}

			int error = 0; // 用于标记程序是否出错,出错了后面就不会再继续执行了

			conn = new DBConnect().getDirectConnect("");

			// 初始化业务对象
			ExcelUploadService upload = null;
			try {
				upload = new ExcelUploadService(conn, strFullFileName);
			} catch (Exception e) {
				e.printStackTrace();
				out.println("临时路径有误,请与系统管理员联系<br>");
				error = 1;
			}

			upload.setExcelNum("");
			upload.setExcelString("");

			// 检查用户指定年份的帐套是否存在;
			// 定义单一，避免其他用户干扰；

			try {
				Single.locked(lockmsg, us.getUserLoginId());
			} catch (Exception e) {
				out.println(e.getMessage() + "<br/>");
				error = 1;
			}

			if (error > 0) {
				out.println("装载活动遇到错误,已经中止!<br>请解决错误后重新装载");
			} else {
				org.util.Debug.prtOut("装载的临时目录为:" + uploadtemppath);
				out.println("继续处理装载<br>");
				out.flush();

				out.println("正在分析EXCEL文件......");
				out.flush();
				upload.init();
				out.println("分析EXCEL文件完毕!<BR>");

				// 开始装载科目余额表信息
				// 首先清空指定表的指定帐套的数据;
				out.println("正在装载审计目标......");
				out.flush();

				TargetService targetService = new TargetService(conn, projectId);

				// 临时表名称
				String tempTableName = "tt_z_target";
				targetService.setTempTableName(tempTableName);
				targetService.createTable();

				// 设置审计目标
				String[] exexlKmye = { "编号", "审计目标", "认定" };
				String[] tableKmye = { "DefineID", "AuditTarget", "cognizance" };
				String[] exexlPzmxOpt = { "状态", "相关执行程序" };
				String[] tablePzmxOpt = { "state", "CorrelationExeProcedure" };

				String[] exexlKmyeFixFields = { "ProjectID", "taskid" };
				String[] excelKmyeFixFieldValues = { projectId, parentTaskId };

				String result = "";

				result = upload.LoadFromExcel("审计目标", tempTableName, exexlKmye,
						tableKmye, exexlPzmxOpt, tablePzmxOpt,
						exexlKmyeFixFields, excelKmyeFixFieldValues);

				out.println("装载审计目标完毕!<BR>");
				out.flush();

				out.println("开始更新审计目标!......");
				out.flush();
				targetService.insertData();
				out.println("更新审计目标完毕!<BR>");

				if (result != null && result.length() > 0) {
					out.println("<br><br>审计目标装载非正常结果报告：<BR><font color='red'>");
					out.println(result);
					out.println("</font><br>");
				}

				out.println("正在装载审计程序......");
				out.flush();

				ProcedureService procedureService = new ProcedureService(conn,
						projectId);
				tempTableName = "tt_z_procedure";
				procedureService.setTempTableName(tempTableName);
				procedureService.createTable();

				// 设置审计目标
				String[] exexlKmye2 = { "编号", "实质性程序", "认定" };
				String[] tableKmye2 = { "DefineID", "AuditProcedure",
						"cognizance" };
				String[] exexlPzmxOpt2 = { "相关底稿", "状态", "执行人", "备注" };
				String[] tablePzmxOpt2 = { "Manuscript", "state", "Executor",
						"remark" };

				result = upload.LoadFromExcel("审计程序", tempTableName,
						exexlKmye2, tableKmye2, exexlPzmxOpt2, tablePzmxOpt2,
						exexlKmyeFixFields, excelKmyeFixFieldValues);

				out.println("装载审计程序完毕!<BR>");
				out.flush();

				out.println("开始更新审计程序!......");
				out.flush();
				procedureService.insertData();
				out.println("更新审计程序完毕!<BR>");

				if (result != null && result.length() > 0) {
					out.println("<br><br>审计程序装载非正常结果报告：<BR><font color='red'>");
					out.println(result);
					out.println("</font><br>");
				}

				out
						.println("<hr>数据装载成功 <a href=\"/AuditSystem/task.do?method=tarAndPro&projectId="
								+ projectId
								+ "&parentTaskId="
								+ parentTaskId
								+ "\">返回查询页面</a>\"</font>");

			}
		} catch (Exception e) {
			e.printStackTrace();
			out.println("<font style=\"color:red\">装载处理出现错误:<br/>"
					+ e.getMessage());
			out
					.println("<a href=\"user.do?method=Upload\">返回装载页面</a>\"</font>");

			e.printStackTrace();
			throw e;
		} finally {
			try {
				Single.unlocked(lockmsg, us.getUserLoginId());
			} catch (Exception e) {
				out.println("撤销并发锁失败：" + e.getMessage() + "<br/>");
			}
			DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 底稿退回
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView rejectTasks(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String projectId = request.getParameter("projectId");
		UserSession userSession = (UserSession) request.getSession()
				.getAttribute("userSession");
		String userId = userSession.getUserId();
		String curProjectName = userSession.getCurProjectName();
		String levelBack = request.getParameter("levelBack");
		PrintWriter out = response.getWriter();

		ASFuntion asf = new ASFuntion();

		Task task = null;
		PlacardTable placard = null;
		Connection conn = null;
		boolean isAudit = false;
		try {
			String acc = userSession.getCurAccPackageId();
			conn = new DBConnect().getConnect(acc.substring(0, 6));
			ProjectService ps = new ProjectService(conn);
			int state = ps.getState(projectId);

			int type = 0; // 复核类型
			try {
				type = Integer.parseInt(request.getParameter("type"));
			} catch (Exception e) {
				throw new Exception("参数错误:type=" + type);
			}

			boolean hasAuthority3 = false;
			boolean hasAuthority2 = false;
			UserdefService udf = new UserdefService(conn);

			if (type == 2) {
				if (state != 5&&state!=6) {
					out.println("<script type=\"text/javascript\">");
					out.println("	alert('项目尚未进入二审状态');");
					out.println("	window.self.location='/AuditSystem/taskCommon.do?method=manager';");
					out.println("</script>");
					return null;
				} else {
					hasAuthority2 = udf.hasAuthority2(userId, projectId);
					if (!hasAuthority2) {
						out.println("<script type=\"text/javascript\">");
						out.println("	alert('用户无权退回底稿');");
						out.println("	window.self.location='/AuditSystem/taskCommon.do?method=manager';");
						out.println("</script>");
						return null;
					}
				}
			} else if (type == 3) {
				if (state != 6) {
					out.println("<script type=\"text/javascript\">");
					out.println("	alert('项目尚未进入三审状态');");
					out.println("	window.self.location='/AuditSystem/taskCommon.do?method=manager';");
					out.println("</script>");
					return null;
				} else {
					hasAuthority3 = udf.hasAuthority3(userId, projectId);
					if (!hasAuthority3) {
						out.println("<script type=\"text/javascript\">");
						out.println("	alert('用户无权退回底稿');");
						out.println("	window.self.location='/AuditSystem/taskCommon.do?method=manager';");
						out.println("</script>");
						return null;
					}

				}
			}

			if (type == 2) {
				isAudit = hasAuthority2;
			} else if (type == 3) {
				isAudit = hasAuthority3;
			} else if (type == 5) {
				isAudit = new AuditPeopleService(conn, projectId)
						.hasAudit(userId);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		String str = asf.showNull(request.getParameter("str"));
		if (!"".equals(str)) {
			String[] taskIds = str.split(",");
			String msgContent = asf.showNull(request.getParameter("advice"));
			if (isAudit) {// 有权复核
				try {
					PlacardService placardMan = new PlacardService(conn);
					TaskService taskService = new TaskService(conn, projectId);
					AdviceService as = new AdviceService(conn, projectId);
					TaskCommonService taskCommonService = new TaskCommonService(
							conn, projectId);
					for (int i = 0; i < taskIds.length; i++) {

						taskCommonService.auditing(userId, taskIds[i], 4,
								levelBack,null);
						task = taskService.getTaskByTaskId(taskIds[i]);
						placard = new PlacardTable();
						placard.setCaption("底稿退回");
						StringBuffer msg = new StringBuffer();
						msg.append("<br />项目名称:" + curProjectName);
						msg.append("<br />底稿名称:" + task.getTaskName());
						msg.append("<br />索引号:" + task.getTaskCode());
						msg.append("<br /><br />");
						msg.append(msgContent);
						msg
								.append("&nbsp;&nbsp;<a href='/AuditSystem/taskCommon.do?method=fileOpen");
						msg.append("&projectId=" + projectId);
						msg.append("&taskId=" + taskIds[i]);
						msg.append("' target='_self'>点击查看底稿</a>");
						placard.setMatter(msg.toString());
						placard.setIsReversion(0);
						placard.setAddresser(userId);
						placard.setAddresserTime(asf.getCurrentDate() + " "
								+ asf.getCurrentTime());
						placard.setIsRead(0);
						placard.setAddressee(task.getUser1());
						placardMan.AddPlacard(placard);
						Advice a = new Advice();
						a.setProjectId(projectId);
						a.setTaskId(taskIds[i]);
						a.setAdvice(msgContent);
						a.setAdviceType("退回");
						a.setUserId(userId);
						as.addAdvice(a);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		response
				.sendRedirect("/AuditSystem/taskCommon.do?method=batchAuditingList&type="
						+ asf.showNull(request.getParameter("type")));
		return null;
	}

	/**
	 * 通过退回程序
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView auditTasks(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String projectId = request.getParameter("projectId");
		UserSession userSession = (UserSession) request.getSession()
				.getAttribute("userSession");
		String userId = userSession.getUserId();
		String curProjectName = userSession.getCurProjectName();
		PrintWriter out = response.getWriter();

		ASFuntion asf = new ASFuntion();

		Task task = null;
		PlacardTable placard = null;
		Connection conn = null;
		boolean isAudit = false;
		int type = 0; // 复核类型

		try {
			String acc = userSession.getCurAccPackageId();
			conn = new DBConnect().getConnect(acc.substring(0, 6));
			ProjectService ps = new ProjectService(conn);
			int state = ps.getState(projectId);


			try {
				type = Integer.parseInt(request.getParameter("type"));
			} catch (Exception e) {
				throw new Exception("参数错误:type=" + type);
			}

			boolean hasAuthority3 = false;
			boolean hasAuthority2 = false;
			UserdefService udf = new UserdefService(conn);

			if (type == 2) {
				if (state != 5&&state!=6) {
					out.println("<script type=\"text/javascript\">");
					out.println("	alert('项目尚未进入二审状态');");
					out.println("	window.self.location='/AuditSystem/taskCommon.do?method=manager';");
					out.println("</script>");
					return null;
				} else {
					hasAuthority2 = udf.hasAuthority2(userId, projectId);
					if (!hasAuthority2) {
						out.println("<script type=\"text/javascript\">");
						out.println("	alert('用户无权退回底稿');");
						out.println("	window.self.location='/AuditSystem/taskCommon.do?method=manager';");
						out.println("</script>");
						return null;
					}
				}
			} else if (type == 3) {
				if (state != 6) {
					out.println("<script type=\"text/javascript\">");
					out.println("	alert('项目尚未进入三审状态');");
					out.println("	window.self.location='/AuditSystem/taskCommon.do?method=manager';");
					out.println("</script>");
					return null;
				} else {
					hasAuthority3 = udf.hasAuthority3(userId, projectId);
					if (!hasAuthority3) {
						out.println("<script type=\"text/javascript\">");
						out.println("	alert('用户无权退回底稿');");
						out.println("	window.self.location='/AuditSystem/taskCommon.do?method=manager';");
						out.println("</script>");
						return null;
					}

				}
			}

			if (type == 2) {
				isAudit = hasAuthority2;
			} else if (type == 3) {
				isAudit = hasAuthority3;
			} else if (type == 5) {
				isAudit = new AuditPeopleService(conn, projectId)
						.hasAudit(userId);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		String str = asf.showNull(request.getParameter("str"));
		if (!"".equals(str)) {
			String[] taskIds = str.split(",");
			if (isAudit) {// 有权复核
				try {
					PlacardService placardMan = new PlacardService(conn);
					TaskService taskService = new TaskService(conn, projectId);
					TaskCommonService taskCommonService = new TaskCommonService(
							conn, projectId);
					for (int i = 0; i < taskIds.length; i++) {

						taskCommonService.auditing(userId, taskIds[i],type,null,null);
						task = taskService.getTaskByTaskId(taskIds[i]);
						placard = new PlacardTable();
						placard.setCaption("底稿审核完成");
						StringBuffer msg = new StringBuffer();
						msg.append("<br />项目名称:" + curProjectName);
						msg.append("<br />底稿名称:" + task.getTaskName());
						msg.append("<br />索引号:" + task.getTaskCode());
						msg.append("<br /><br />");
						if(type==2) {
							msg.append("<br />一审完成");
						} else if(type==3) {
							msg.append("<br />二审完成");
						} else if(type==5) {
							msg.append("<br />三审完成");
						}
						msg
								.append("&nbsp;&nbsp;<a href='/AuditSystem/taskCommon.do?method=fileOpen");
						msg.append("&projectId=" + projectId);
						msg.append("&taskId=" + taskIds[i]);
						msg.append("' target='_self'>点击查看底稿</a>");
						placard.setMatter(msg.toString());
						placard.setIsReversion(0);
						placard.setAddresser(userId);
						placard.setAddresserTime(asf.getCurrentDate() + " "
								+ asf.getCurrentTime());
						placard.setIsRead(0);
						placard.setAddressee(task.getUser1());
						placardMan.AddPlacard(placard);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		response
				.sendRedirect("/AuditSystem/taskCommon.do?method=batchAuditingList&type="
						+ asf.showNull(request.getParameter("type")));
		return null;
	}


	/**
	 * 底稿列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView multiDeleteTaskList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(MUTIDELETE_LIST_VIEW);

		response.setContentType("text/html;charset=utf-8");
		ASFuntion asfFunction = new ASFuntion();
		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session.getAttribute("userSession");

		String curProjectId = request.getParameter("projectId");

		if (curProjectId == null || "".equals(curProjectId)) {
			curProjectId = userSession.getCurProjectId();
		}

		String parentTaskId = asfFunction.showNull(request.getParameter("parentTaskId"));
		String taskId = asfFunction.showNull(request.getParameter("taskId"));

		String customerId = null;
		Connection conn = null;
		PrintWriter out = response.getWriter();
		String url = "";

		try {

			conn = new DBConnect().getConnect(customerId);

			customerId = userSession.getCurCustomerId() ;
			String fullPath = new TaskService(conn,curProjectId).getFullPath(taskId);
			modelAndView.addObject("fullPath", fullPath);
			TaskService taskService = new TaskService(conn, curProjectId);

			String curauditTypeProperty = new AuditTypeTemplateService(conn)
			.getPropertyByProjectId(curProjectId);


				if ("1".equals(curauditTypeProperty)) {
					// 风险导向审计环节
					url = "tache.do?method=tacheList&refresh=true";
				} else {
					// 普通任务结点
					url = "taskSort.do?method=list&refresh=true";
				}

				url += "&parentTaskId=" + parentTaskId;



			if (taskService.getChildrenCountByParentTaskId(taskId) > 0) {
	//			out.println("<Script>alert(\"该任务还有下级子任务，请先删除下级后再删除本任务。\");</Script>");
				DataGridProperty dgProperty = new DataGridProperty();
				dgProperty.setTableID("multiDeleteTaskList");
				dgProperty.setCustomerId(customerId);
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

				if(taskId != null && !"".equals(taskId) && !"null".equals(taskId)){
					sql.append(" and concat('|',fullpath,'|') like '%|" + taskId + "|%' ");

				}

				dgProperty.setSQL(sql.toString());
				dgProperty.setOrderBy_CH("orderid");
				dgProperty.setDirection("asc");
				session.setAttribute(DataGrid.sessionPre + dgProperty.getTableID(),dgProperty);

				modelAndView.addObject("parentTaskId", parentTaskId);
				modelAndView.addObject("taskId",taskId) ;
				modelAndView.addObject("projectId", curProjectId);
				return modelAndView;
			} else {


				String taskName = taskService.getTaskByTaskId(taskId)
				.getTaskName();

				taskService.removeTask(taskId); // 执行数据库删除操作

				LogService.updateToLog(userSession, "删除底稿[" + taskName + "]",
				conn);
				out.println("<Script>window.location='" + url + "&projectId=" + curProjectId + "';</Script>");
				return null ;
			}


			}catch (Exception e) {
				e.printStackTrace();
			} finally {
				DbUtil.close(conn);
			}

			return null ;
	}


	/**
	 * 删除任务
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView MultiRemove(HttpServletRequest request,
			HttpServletResponse response) throws Exception {



		// 取得用户Session
		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session
				.getAttribute("userSession");

		String curProjectId = request.getParameter("projectId");

		if (curProjectId == null || "".equals(curProjectId)) {
			curProjectId = userSession.getCurProjectId(); // 当前项目编号
		}

		String taskId = request.getParameter("taskId"); // 任务编号

		// 任务类型,可以是任务结点，也可以是任务结点，根据不同的值操作完毕后返回不同的页面
		String taskType = request.getParameter("taskType");

		// 来自页面的parentTaskId,是为了操作完毕后返回到原来的页面上
		String parentTaskId = request.getParameter("parentTaskId");

		PrintWriter out = response.getWriter();

		Connection conn = null;
		String url = "";


		try {
			conn = new DBConnect().getConnect("");
			TaskService taskService = new TaskService(conn, curProjectId);
			String curauditTypeProperty = new AuditTypeTemplateService(conn)
					.getPropertyByProjectId(curProjectId);

			if ("task".equals(taskType)) {
				if ("1".equals(curauditTypeProperty)) {
					// 风险导向审计环节
					url = "tache.do?method=tacheList&refresh=true";
				} else {
					// 普通任务结点
					url = "taskSort.do?method=list&refresh=true";
				}
			} else if ("taskManu".equals(taskType)) {
				if ("1".equals(curauditTypeProperty)) {
					// 风险导向审计底稿
					url = "tache.do?method=list&refresh=true";
				} else {
					// 普通任务底稿
					url = "task.do?method=list&refresh=true";
				}

			}


				List taskList = taskService.getTaskListByParentTaskId(taskId) ;
				for(int i=0;i<taskList.size();i++) {
					Task task = (Task)taskList.get(i) ;
					taskService.removeTask(task.getTaskId()); // 执行数据库删除操作
					String childTaskName = task.getTaskName() ;
					LogService.updateToLog(userSession, "删除底稿[" + childTaskName + "]",
					conn);
				}


		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		url += "&refreshTree=true&parentTaskId=" + parentTaskId;
		out.println("<Script>window.location='" + url + "';</Script>");

		return null;
	}

	public void hasRightToDeteleTask(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null ;

		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session.getAttribute("userSession") ;
		ASFuntion CHF = new ASFuntion() ;
		PrintWriter out = response.getWriter() ;

		String userId = userSession.getUserId() ;
		String taskId = CHF.showNull(request.getParameter("taskId")) ;

		String curProjectId = request.getParameter("projectId");

		if (curProjectId == null || "".equals(curProjectId)) {
			curProjectId = userSession.getCurProjectId(); // 当前项目编号
		}

		try {

			conn = new DBConnect().getConnect("");
			TaskService taskService = new TaskService(conn,curProjectId) ;

			List taskList = taskService.getTaskListByParentTaskId(taskId) ;
			for(int i=0;i<taskList.size();i++) {

				Task task = (Task)taskList.get(i) ;
				String user0 = CHF.showNull(task.getUser0());
				if(!"".equals(user0) && !userId.equals(user0)) {
					out.write("no") ;
					return ;
				}

			}

/*			String user0 = new ASFuntion().showNull(task.getUser0());
			if(!"".equals(user0) && !userId.equals(user0)) {
//				out.println("<Script>alert(\"您不是这张底稿的分工人,不能删除底稿。\");</Script>");

			}*/

			out.write("yes") ;

		}catch(Exception e){
			e.printStackTrace() ;
		}finally {
			DbUtil.close(conn);
		}


	}

	/**
	 * 获得有附注表的底稿列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView annotationsTaskList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView(ANNOTATIONS_TASK_LIST_VIEW);

		response.setContentType("text/html;charset=utf-8");

		// 取得用户Session
		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session.getAttribute("userSession");

		String curProjectId = request.getParameter("projectId"); // 当前项目编号

		// 如果request中没有传projectId参数过来,那么就取session中的当前项目编号
		if (curProjectId == null || "".equals(curProjectId)) {
			curProjectId = userSession.getCurProjectId(); // 当前项目编号
		}

		String customerId = "";

		Connection conn = null;

		try {
			conn = new DBConnect().getConnect("");
			customerId = new CustomerService(conn).getCustomerIdByProjectId(curProjectId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		//有附注表的底稿列表
		DataGridProperty dgProperty = new DataGridProperty();
		dgProperty.setTableID("annotationsTaskList");
		dgProperty.setCustomerId(customerId);

		dgProperty.setPrintEnable(true);

		dgProperty.addColumn("底稿编号", "taskcode");
		dgProperty.addColumn("底稿名称", "taskname");

		dgProperty.setInputType("checkbox");
		dgProperty.setWhichFieldIsValue(1);

		dgProperty.setCancelOrderby(true);
		dgProperty.setCancelPage(true);

		StringBuffer sql = new StringBuffer();
		sql.append(" select a.taskid,a.taskcode,a.taskname from z_task a,  ");
		sql.append(" 	(select distinct taskid ");
		sql.append(" 		from z_sheettask ");
		sql.append(" 		where projectid='" + curProjectId + "' ");
		sql.append(" 		and sheetname like '%附注%' ) b ");
		sql.append(" where a.projectid='" + curProjectId + "' ");
		sql.append(" and a.taskid=b.taskid ");
		sql.append(" order by a.orderid ");

		dgProperty.setSQL(sql.toString());

		session.setAttribute(DataGrid.sessionPre + dgProperty.getTableID(),dgProperty);

		return modelAndView;
	}
	
	/**
	 * 检查底稿名称
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void checkNameOnly(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null ;
		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session.getAttribute("userSession") ;
		
		PrintWriter out = response.getWriter() ;
		
		String curProjectId = request.getParameter("projectId");
		if (curProjectId == null || "".equals(curProjectId)) {
			curProjectId = userSession.getCurProjectId(); // 当前项目编号
		}
		String fileName = request.getParameter("fileName");
		try {
			conn = new DBConnect().getConnect("");
			TaskService taskService = new TaskService(conn,curProjectId) ;
			out.write(taskService.checkNameOnly(fileName));
		}catch(Exception e){
			e.printStackTrace() ;
		}finally {
			out.close();
			DbUtil.close(conn);
		}
	}
	
	/**
	 * 附件上传之前设置
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView addAttach(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		
		HttpSession session = request.getSession();
		UserSession userSession = (UserSession)session.getAttribute("userSession");
		String projectId = request.getParameter("projectId");
		if(projectId==null||"".equals(projectId)) {
			projectId = userSession.getCurProjectId();
		}
		String taskId = request.getParameter("taskId");
		ModelAndView modelAndView = new ModelAndView(ATTACH_ADD_VIEW);
		modelAndView.addObject("projectId", projectId);
		modelAndView.addObject("taskId", taskId);
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			TaskService taskService = new TaskService(conn,projectId);
			TaskCommonService commonService = new TaskCommonService(conn,projectId);
			Task task = taskService.getTaskByTaskId(taskId);
			modelAndView.addObject("taskName",task.getTaskName());
			modelAndView.addObject("taskCode",task.getTaskCode());
			modelAndView.addObject("attachCode",commonService.getAttachCodeByTaskId(taskId,projectId));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	
//	判断底稿是否编程完成
	public void isTaskFinish(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null ;

		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session.getAttribute("userSession") ;
		ASFuntion CHF = new ASFuntion() ;
		PrintWriter out = response.getWriter() ;

		String userId = userSession.getUserId() ;
		String taskId = CHF.showNull(request.getParameter("taskId")) ;

		String curProjectId = request.getParameter("projectId");

		if (curProjectId == null || "".equals(curProjectId)) {
			curProjectId = userSession.getCurProjectId(); // 当前项目编号
		}

		try {

			conn = new DBConnect().getConnect("");
			TaskService taskService = new TaskService(conn,curProjectId) ;
			String user1 = taskService.getTaskByTaskId(taskId).getUser1() ;
			
			if("".equals(user1) || user1 == null) {
				//没有编制完成
				out.write("noFinish") ;
			}else if(user1.equals(userId)) {
				//编制人是自己
				out.write("isSelf") ;
			}else {
				out.write("yes") ;
			}
		}catch(Exception e){
			e.printStackTrace() ;
		}finally {
			DbUtil.close(conn);
		}


	}
	
	/**
	 * 保存附件
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView saveAttach(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		
		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		PrintWriter out = null;
		Connection conn = null;
		
		String projectId = "";
		String taskId = "";

		try {
			conn = new DBConnect().getConnect("");
			out =  response.getWriter();
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session
					.getAttribute("userSession");

			// 项目编号
			projectId = request.getParameter("projectId");
			taskId = request.getParameter("taskId");

			// 如果是更改项目，则projectId是当前项目ID；
			if (projectId == null || "".equals(projectId)
					|| "null".equals(projectId)) {

				projectId = request.getParameter("curProjectid");

				if (projectId == null || "".equals(projectId)
						|| "null".equals(projectId)) {

					projectId = userSession.getCurProjectId();
				}
			}
			
			System.out.println("项目编号" + projectId);

			taskId = new TaskCommonService(conn).saveAttach(request, conn);

		} catch (Exception e) {
			out.write("Error\n文件传输失败：" + e.getMessage());
			e.printStackTrace();
			// throw e;
		} finally {
			DbUtil.close(conn);
		}

		response.sendRedirect("taskAttach.do?projectId="+projectId+"&taskId="+taskId+"");
		return null;
	}
	
	 
	//作业指导上传
	public ModelAndView helpUpload(HttpServletRequest request,	HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		PrintWriter out = response.getWriter();

		// 文件的唯一ID

		Connection conn = null;

		try {
			conn = new DBConnect().getConnect("");

			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session.getAttribute("userSession");
			if (userSession == null) {
				userSession = new UserSession();

				String userId = request.getParameter("userId");
				String userName = new UserService(conn).getUser(userId, "id").getName();

				userSession.setUserId(userId);
				userSession.setUserName(userName);
			}

			// 项目编号
			String taskId = request.getParameter("taskId");
			String typeId = request.getParameter("typeId");

			// 如果是更改项目，则projectId是当前项目ID；

			MyFileUpload fileUpload = new MyFileUpload(request, conn);

			boolean uploadResult = fileUpload.UploadUpdateHelp(taskId,typeId, userSession.getUserName());

			if (uploadResult) {
				out.write("Success\n文件修改成功!");
			} else {
				out.write("Error\n文件传输失败");
			}
		

		} catch (Exception e) {
			out.write("Error\n文件传输失败：" + e.getMessage());
			e.printStackTrace();
			// throw e;
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}
	
	//作业指导下载
	public ModelAndView helpDownload(HttpServletRequest request,HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		ASFuntion funtion = new ASFuntion();

		String typeId = funtion.showNull(request.getParameter("typeId"));
		String taskId = funtion.showNull(request.getParameter("taskId"));	//ID

		//重置表页的时候，前台传回来的是projectid和taskcode,后台翻译成对应的typeId和taskId
		
		
		String zipByClient=funtion.showNull(request.getParameter("zipByClient"));

		byte[] bytes = null;
		String fileName = null;

		Connection conn = null;
		PrintWriter out=null;
		try {
			conn = new DBConnect().getConnect("");
			DbUtil dbUtil = new DbUtil(conn);
			
			ManuFileService manuFileService = new ManuFileService(conn);
			
			
			// 根据项目编号projectId或模板编号typeId加底稿编号taskid下载底稿

				if (!"".equals(typeId)) {

					// 根据模板编号和底稿编号获得文件，模板底稿
					if ("1".equals(zipByClient)){
						//前台自己解压
						bytes = manuFileService.getFileByHelpId(typeId, taskId);
					}else{
						//后台解压
						bytes = manuFileService.getUnZipFileByHelpId(typeId, taskId);
					}

					String sql = "select a.helpname "
						+"from k_tasktemplatehelp a "
						+"where a.taskId =?  "
						+"and a.typeid=?  ";

					Object[] object = new Object[] {
							taskId,typeId
					};
					
					fileName = dbUtil.queryForString(sql, object);
			
				}

				// 判断文件是word还是excel
				if (fileName != null) {
					String mime = fileName.toLowerCase().indexOf(".doc") > -1 ? "application/doc"
							: "application/vnd.ms-excel";
					response.setContentType(mime);
				}

				System.out.println("打开底稿：" + fileName + ", typeId=" + typeId + ", taskId="+ taskId + ", |打开方式："+ zipByClient);


			if (bytes != null && bytes.length > 0) {
				OutputStream outs = response.getOutputStream();
				outs.write(bytes);
				outs.flush();
				outs.close();
			} else {
				out = response.getWriter();
				out.print("错误:文件不存在！");
			}
		} catch (Exception e) {
			out = response.getWriter();
			out.print("错误:"+e.getMessage());
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
			if (out!=null)out.close();
		}
		return null;
	}
	
	/**
	 * 1、通过ProjectID得到【客户申报表】的taskID和taskCode,打开导入申报表的页面
	 * 2、导入EXCEL表，替换【客户申报表】；只要替换底稿文件，原底稿信息不变，底稿文件名不变
	 * 	注：如果找不到【客户申报表】，页面出现提示信息，并不能导入【客户申报表】
	 */
	//打开【申报表导入】的页面
	public ModelAndView addDeclare(HttpServletRequest request,HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(DECLARE);
		Connection conn = null;
		try {
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");

			String curProjectId = request.getParameter("projectId");
			if (curProjectId == null || "".equals(curProjectId)) {
				// 检查是否登陆项目
				if (!new CommonSecurity(request, response).checkProjectLogin()) {
					return null;
				}
				curProjectId = userSession.getCurProjectId();
			}
			conn = new DBConnect().getConnect("");
			TaskService taskService = new TaskService(conn,curProjectId);
			
			String taskName = "客户申报表";
			Task task = taskService.getTaskByTaskName(taskName);
			
			modelAndView.addObject("projectId", curProjectId);
			modelAndView.addObject("task", task);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	//替换【客户申报表】底稿
	public ModelAndView saveDeclare(HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		Connection conn = null;

		try {
			conn = new DBConnect().getConnect("");
			response.setContentType("text/html;charset=utf-8");
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);

			PrintWriter out = response.getWriter();
			
			String projectId = request.getParameter("projectId"); //项目编号
			String taskId = request.getParameter("taskId");	//底稿taskId
			
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			
			MyFileUpload fileUpload = new MyFileUpload(request,conn);  

			boolean uploadResult = fileUpload.UploadUpdate(taskId,projectId, userSession.getUserName());
			
			Map map = fileUpload.getMap();
			String istask = (String)map.get("istask");
			
			if("1".equals(istask)){
				new DBConnect().changeDataBaseByProjectid(conn, projectId);
				
				String sql = "update z_task set udate='',username='' where taskId= ? and projectId=? ";
				DbUtil dbUtil = new DbUtil(conn);

				Object[] object = new Object[] {
						taskId,
						projectId
				};
				dbUtil.execute(sql, object);
				
				String url  = "taskCommon.do?method=addDeclare";
				
				out.print("<script>alert('取数方式修改成功！');\n");
				out.print("window.location = '"+url+"';\n");
				out.print("</script>");
				
			}else{
			
				String url = "";
				if (uploadResult) {
					url = "taskCommon.do?method=fileOpen&taskId=" + taskId;
					
					out.print("<script>alert('文件修改成功！');\n");
					out.print("window.location = '"+url+"';\n");
					out.print("</script>");
					
				} else {
					out.write("Error\n文件传输失败");
				}
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