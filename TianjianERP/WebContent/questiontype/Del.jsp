<%@page import="com.matech.audit.service.questionType.QuestionTypeMan"%>

<%@page import="com.matech.framework.pub.db.DBConnect"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.matech.framework.pub.db.DbUtil"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>

<%
	response.setHeader("Pragma","No-cache");
	response.setHeader("Cache-Control","no-cache");
	response.setDateHeader("Expires", 0);
	Connection conn =null;
	try{
	  	 conn = new DBConnect().getConnect();
		String str = new QuestionTypeMan(conn).delAQuestionType(request.getParameter("chooseValue"));
		out.println(str);
//		out.println("window.location.reload();");
	}catch(Exception e)
	{
		out.print(e);
	}finally{
		 DbUtil.close(conn);
	}
%>