<%@page import="com.matech.framework.listener.UserSession"%>
<%@page import="com.matech.framework.pub.util.WebUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>

<%
  String basePath="http://"+request.getLocalAddr()+":"+request.getLocalPort()+request.getContextPath();
  String uuid=request.getParameter("uuid");
  WebUtil webUtil=new WebUtil(request,response);
  UserSession userSession=webUtil.getUserSession();
  
%>

<script type="text/javascript">

var AuditReport,oframe;

     function init(){
    	 
    		AuditReport =  new ActiveXObject("AuditReportPoject.AuditReport");
    		AuditReport.pUTF8=true;
    		oframe = document.getElementById("oframe");
    		AuditReport.pDSOFramer=oframe;
    		fileOpen("<%=uuid%>.doc");
     }
     
    
 	function fileOpen(fileName) {
		AuditReport.pOpenMode=0;
		AuditReport.pFileName = fileName; 
		//AuditReport.pFileDir="E:\\ws-juno\\TianjianERP\\WebContent\\doc\\" ;
		AuditReport.pFileDir="D:\\" ;
		AuditReport.pOpenUrl="<%=basePath%>/docpost.do?method=openWord&uuid=<%=uuid%>";
		AuditReport.pTrackRev=false;
		AuditReport.pAppUser='<%=userSession.getUserName()%>';
		AuditReport.pSaveUrl="<%=basePath%>/docpost.do?method=saveWord&uuid=<%=uuid%>";
		var UrlParameter="&manuname="+fileName;
		AuditReport.pUrlParameter="http:\/\/"+window.location.host + "|" + UrlParameter;
		AuditReport.funOpenUrlFile();
	} 
   
 	
</script>

</head>
<body onload="init()">
   
    <object classid="clsid:C20572B8-6104-45B8-A3EE-303B42C26ABF" id="oframe" style="width:100%; height: 100%" >
	         	<param name="BorderStyle" value="1">
	        	<param name="SideBarVisible" value="0">
	       	 	<param name="Titlebar" value="0">
	      	   	<param name="Menubar" value="1">
	       </object>
    <div></div>
</body>
</html>