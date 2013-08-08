package com.matech.audit.work.oa.task;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.audittypetemplate.AuditTypeTemplateService;
import com.matech.audit.service.oa.procedure.ProcedureService;
import com.matech.audit.service.oa.procedure.model.Procedure;
import com.matech.audit.service.oa.target.TargetService;
import com.matech.audit.service.oa.target.model.Target;
import com.matech.audit.service.oa.task.TaskService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

/**
 * <p>Title: 审计目标ACTION类</p>
 * <p>Description: 负责普通审计和风险导向审计中的审计目标维护</p>
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
public class TargetAction extends MultiActionController {
	/**
	 * 增加一个普通审计的审计目标
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);

		Connection conn = null;
		PrintWriter out = response.getWriter();
		try {
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession)session.getAttribute("userSession");

			String curProjectId = request.getParameter("projectId");

			if (curProjectId == null || "".equals(curProjectId)) {
				curProjectId = userSession.getCurProjectId();
			}

			String parentTaskId = request.getParameter("parentTaskId");

			conn = new DBConnect().getConnect("");
			TargetService targetService = new TargetService(conn,curProjectId);

			Target target = new Target();
			target.setTaskId(parentTaskId);
			target.setState("未完成");
			target.setProjectID(curProjectId);
			targetService.addTarget(target);

			out.write("ok");
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
	 * 删除一个审计目标
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView remove(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);

		Connection conn = null;
		PrintWriter out = response.getWriter();
		try {
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession)session.getAttribute("userSession");

			String curProjectId = request.getParameter("projectId");

			if (curProjectId == null || "".equals(curProjectId)) {
				curProjectId = userSession.getCurProjectId();
			}

			String autoId = request.getParameter("autoId");

			conn = new DBConnect().getConnect("");
			TargetService targetService = new TargetService(conn,curProjectId);

			//普通审计程序
			int result = targetService.removeTargetByAutoId(autoId);

			if(result > 0 ) {
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
	 * 更新一个普通审计的审计目标
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView update(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);

		Connection conn = null;
		PrintWriter out = response.getWriter();
		try {
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession)session.getAttribute("userSession");

			String curProjectId = request.getParameter("projectId");

			if (curProjectId == null || "".equals(curProjectId)) {
				curProjectId = userSession.getCurProjectId();
			}

			String att = request.getParameter("att");
			String val = request.getParameter("val");
			String autoId =  request.getParameter("autoId");

			conn = new DBConnect().getConnect("");
			TargetService targetService = new TargetService(conn,curProjectId);

			if(targetService.updateTargetByAutoId(autoId, att, val) > 0) {
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
	 * 获得审计程序,供AJAX调用
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getProcedureSet(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);

		Connection conn = null;
		PrintWriter out = response.getWriter();

		try {
			conn = new DBConnect().getConnect("");

			HttpSession session = request.getSession();
			UserSession userSession = (UserSession)session.getAttribute("userSession");

			String curProjectId = request.getParameter("projectId");

			if (curProjectId == null || "".equals(curProjectId)) {
				curProjectId = userSession.getCurProjectId();
			}

			String taskId = request.getParameter("taskId");

			ProcedureService procedureService = new ProcedureService(conn,curProjectId);
			Set procedureSet = procedureService.getProceduresByTaskId(taskId);

			Iterator iterator = procedureSet.iterator();
			ASFuntion asf = new ASFuntion();
			StringBuffer sb = new StringBuffer();

			//遍历审计程序,每个审计程序用"||"分割,程序中的每个属性用"``"分割
			while(iterator.hasNext()) {
				Procedure procedure = (Procedure)iterator.next();

				// "``"前加个空格是为了防止2个属性都是空字符串的并在一起,
				// 例如出现："````"的情况,所以要变成"`` ``"
				sb.append(asf.showNull(procedure.getState()) + " ``");
				sb.append(asf.showNull(procedure.getDefineId()) + " ``");
				sb.append(asf.showNull(procedure.getAuditProcedure()) + " ``");
				sb.append(asf.showNull(procedure.getManuLinks()) + " ``");
				sb.append(asf.showNull(procedure.getExecutor()) + " ``");

				sb.append("||");
			}

			out.write(sb.toString());

			return null;
		} catch (Exception e) {
			e.printStackTrace();
			out.write("error");
		} finally {
			DbUtil.close(conn);
		}

		return null;

	}

}
