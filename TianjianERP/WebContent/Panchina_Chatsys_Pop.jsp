<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>




<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	
	<script type='text/javascript' src='dwr/engine.js'></script>
    <script type='text/javascript' src='dwr/util.js'></script>
    <script type='text/javascript' src='dwr/interface/PoplayerJS.js'></script>
	
	<script language="javascript">
		dwr.engine.setActiveReverseAjax(true); 
	</script>
	
  </head>
  
  <body>
  	<%
  		request.setCharacterEncoding("UTF-8");
		String senderName=java.net.URLDecoder.decode(request.getParameter("sendername"),"UTF-8");
		String content=java.net.URLDecoder.decode(request.getParameter("content"),"UTF-8");
		String userId=request.getParameter("userid");
  		if(senderName!=null&&content!=null&&userId!=null){
  			out.println("senderNasdmse="+senderName);
	%>
  	<script>
  		PoplayerJS.thisPopLayer('<%=senderName%>','<%=content%>','<%=userId%>');
  	</script>
  	<%
  		}
  	%>
  	
    <input type="button" value="测试" onclick="PoplayerJS.thisPopLayer('aa','bb','56509');">
  </body>
</html>
