<%@page import="com.matech.audit.pub.db.DBConnect"%>
<%@page import="com.matech.framework.listener.UserSession"%>
<%@page import="com.matech.framework.pub.util.WebUtil"%>
<%@page import="com.matech.framework.pub.db.DbUtil"%>
<%@page import="java.sql.Connection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%
Connection conn=null;
DbUtil dbUtil=null;
WebUtil webUtil=new WebUtil(request, response);
UserSession userSession=webUtil.getUserSession();
int eff=0;
String re="";
response.setContentType(WebUtil.CONTENT_TYPE_UTF8);
try{
	conn=new DBConnect().getConnect();
	dbUtil=new DbUtil(conn);
	
}catch(Exception ex){
	re=ex.getLocalizedMessage();
}finally{
	DbUtil.close(conn);
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

</body>
</html>