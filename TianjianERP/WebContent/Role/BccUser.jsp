<%@page import="com.matech.framework.pub.db.DBConnect"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.matech.audit.service.role.RoleService"%>
<%@page import="com.matech.framework.pub.util.ASFuntion"%>
<%@page import="org.util.Debug"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
 <%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ page import="java.util.*"%>
<%
	//request.setCharacterEncoding("utf-8");
	ASFuntion CHF=new ASFuntion();
	String rid = CHF.showNull(request.getParameter("rid"));
	String usr = CHF.showNull(request.getParameter("usr"));
	Connection conn=null;
	try{
		conn=new DBConnect().getConnect();
	RoleService rm = new RoleService(conn);
	out.println(rm.getUserTable(rid,usr));		
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		if(conn!=null)
			conn.close();
	}
%>