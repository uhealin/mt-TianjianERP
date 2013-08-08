<%@page import="com.matech.framework.pub.db.DbUtil"%>
<%@page import="com.matech.audit.service.placard.PlacardService"%>
<%@page import="com.matech.framework.pub.db.DBConnect"%>
<%@page import="java.sql.Connection"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>

<%
	response.setHeader("Pragma","No-cache");
	response.setHeader("Cache-Control","no-cache");
	response.setDateHeader("Expires", 0);
	Connection conn =null;
    try{
   	 conn = new DBConnect().getConnect();
   	    String delteAction = request.getParameter("chooseValue");
   	    String userId = request.getParameter("userId");
   	    
   	    if("all".equals(delteAction)){
   	    	new PlacardService(conn).delAllAPlacardByUser(userId);
   	    }else{
   	    	new PlacardService(conn).delAPlacard(delteAction);	
   	    }
		
		out.println("<script>window.location='View.jsp';</script>");
	}catch(Exception e)
	{
		out.print(e);
	}finally{
		DbUtil.close(conn);
	}
%>