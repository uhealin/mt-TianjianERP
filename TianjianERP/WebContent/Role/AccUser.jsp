<%@page import="com.matech.framework.pub.db.DBConnect"%>
<%@page import="com.matech.audit.service.accright.AccRightService"%>
<%@page import="com.matech.framework.pub.util.ASFuntion"%>
<%@page import="org.util.Debug"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
 <%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
>
<%@  page  import="java.sql.*"%>
<%@ page import="java.util.*"%>
<%
	//request.setCharacterEncoding("utf-8");
	ASFuntion CHF=new ASFuntion();
	Connection conn = null;
	AccRightService arm =new AccRightService();
//	String UserID = CHF.showNull(request.getParameter("UserID"));

//	Debug.prtOut("UserID = |"+UserID);
	try{
		conn = new DBConnect().getConnect();
		out.print(arm.getAUserTable(conn));		
	//	out.print(arm.getAUserTable(UserID,conn));		
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		if(conn!=null)conn.close();
	}
%>