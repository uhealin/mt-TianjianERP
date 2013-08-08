package com.matech.audit.work.system;

import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.framework.listener.UserSession;

/**
 * <p>Title: 安全类</p>
 * <p>Description: 检查用户是否已经登陆项目等</p>
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
 * 2007-7-14
 */
public class CommonSecurity {

	private HttpServletRequest request;

	private HttpServletResponse response;

	private static final String COMM_ERROR_VIEW = "/AuditSystem/AS_SYSTEM/CommError.jsp";

	/**
	 * 构造方法
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public CommonSecurity(HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(request == null) {
			throw new Exception("request不能为空...");
		}

		if(response == null) {
			throw new Exception("response不能为空...");
		}

		this.request = request;
		this.response = response;
	}

	/**
	 * 检查当前用户是否已经登陆项目
	 */
	public boolean checkProjectLogin() throws Exception {
		HttpSession session = request.getSession();
		UserSession userSession = (UserSession) session.getAttribute("userSession");

		if (userSession.getCurCustomerId() == null
				|| userSession.getCurAccPackageId() == null) {

			StringBuffer sUrl = new StringBuffer("");
	        if (request.getRequestURI() != null) {
	            sUrl.append(URLEncoder.encode(request.getRequestURI(),
	                            "UTF-8"));
	        }
	        if (request.getQueryString() != null) {
	            sUrl.append("?");
	            sUrl.append(URLEncoder.encode(request.getQueryString(), "UTF-8"));
	        }

	        String requestURL = (URLDecoder.decode(sUrl.toString(), "UTF-8"));

	        session.setAttribute("REQUEST_FULLPATH", requestURL);

			//如果当前客户编号或账套编号是否为空,则跳转到错误提示页面
			response.sendRedirect(COMM_ERROR_VIEW);
			return false;

		} 
		
//		else if (userSession.getCurChoiceAccPackageId() != null
//				&& !(userSession.getCurChoiceAccPackageId())
//						.equals(userSession.getCurAccPackageId())) {
//
//			StringBuffer sUrl = new StringBuffer("");
//	        if (request.getRequestURI() != null) {
//	            sUrl.append(URLEncoder.encode(request.getRequestURI(),
//	                            "UTF-8"));
//	        }
//	        if (request.getQueryString() != null) {
//	            sUrl.append("?");
//	            sUrl.append(URLEncoder.encode(request.getQueryString(), "UTF-8"));
//	        }
//
//	        String requestURL = (URLDecoder.decode(sUrl.toString(), "UTF-8"));
//
//	        session.setAttribute("REQUEST_FULLPATH", requestURL);
//
//			//如果当前用户选择账套编号不等于当前的账套编号,则跳转到错误提示页面
//			response.sendRedirect(COMM_ERROR_VIEW + "?flag=1");
//			return false;
//		}

		return true;
	}
}
