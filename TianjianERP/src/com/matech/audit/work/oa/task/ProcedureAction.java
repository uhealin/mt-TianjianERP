package com.matech.audit.work.oa.task;

import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.audittypetemplate.AuditTypeTemplateService;
import com.matech.audit.service.oa.procedure.ProcedureService;
import com.matech.audit.service.oa.procedure.model.Procedure;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;

public class ProcedureAction extends MultiActionController {

	/**
	 * 增加一个审计程序
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView add(HttpServletRequest request,
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

			String curProjectId = request.getParameter("projectId");

			if (curProjectId == null || "".equals(curProjectId)) {
				curProjectId = userSession.getCurProjectId();
			}

			String parentTaskId = request.getParameter("parentTaskId");
			String parentId = request.getParameter("parentId");

			conn = new DBConnect().getConnect("");
			ProcedureService procedureService = new ProcedureService(conn,
					curProjectId);

			Procedure procedure = new Procedure();

			procedure.setState("未完成");
			procedure.setProjectId(curProjectId);
			procedure.setParentId(parentId);

			if(!"0".equals(parentId)) {
				Procedure parentProcedure = procedureService.getProcedureById(parentId);
				procedure.setFullpath(parentProcedure.getFullpath() + "|" + parentProcedure.getAutoId());
				procedure.setLevel0(parentProcedure.getLevel0() + 1);
			} else {
				procedure.setFullpath("0");
				procedure.setLevel0(0);
			}

			String curauditTypeProperty = new AuditTypeTemplateService(conn)
					.getPropertyByProjectId(curProjectId);

			if ("1".equals(curauditTypeProperty)) {
				// 风险导向
				procedure.setTaskId("~" + parentTaskId + "~");
			} else {
				// 普通审计程序
				procedure.setTaskId(parentTaskId);
			}

			if (procedureService.addProcedure(procedure) > 0) {
				out.write("ok");
			} else {
				out.write("error");
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.write("error");
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}

	/**
	 * 更新一个审计程序
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView update(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		Connection conn = null;
		PrintWriter out = response.getWriter();
		try {
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session.getAttribute("userSession");

			String curProjectId = request.getParameter("projectId");

			if (curProjectId == null || "".equals(curProjectId)) {
				curProjectId = userSession.getCurProjectId();
			}

			String att = request.getParameter("att");
			String val = request.getParameter("val");
			String autoId = request.getParameter("autoId");

			conn = new DBConnect().getConnect("");
			ProcedureService procedureService = new ProcedureService(conn,
					curProjectId);

			//
			if (procedureService.updateProcedureByAutoId(autoId, att, val) > 0) {
				out.write("ok");
			} else {
				out.write("error");
			}

		} catch (Exception e) {
			e.printStackTrace();
			out.write("error");
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}

	/**
	 * 删除一个审计程序
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView remove(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);

		Connection conn = null;
		PrintWriter out = response.getWriter();
		try {
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession) session.getAttribute("userSession");

			String curProjectId = request.getParameter("projectId");

			if (curProjectId == null || "".equals(curProjectId)) {
				curProjectId = userSession.getCurProjectId();
			}

			String autoId = request.getParameter("autoId");
			String taskId = request.getParameter("taskId");

			conn = new DBConnect().getConnect("");
			ProcedureService procedureService = new ProcedureService(conn,
					curProjectId);

			String curauditTypeProperty = new AuditTypeTemplateService(conn)
					.getPropertyByProjectId(curProjectId);

			int result = 0;
			if ("1".equals(curauditTypeProperty)) {
				// 风险导向
				result = procedureService.removeProcedureByAutoIdAndTaskId(autoId, taskId);
			} else {
				// 普通审计程序
				result = procedureService.removeProcedureByAutoId(autoId);
			}

			// 删除审计程序,删除成功返回>0的数值
			if (result > 0) {
				out.write("ok");
			} else {
				out.write("error");
			}

		} catch (Exception e) {
			e.printStackTrace();
			out.write("error");
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}
}
