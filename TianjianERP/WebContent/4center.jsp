<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="com.matech.framework.pub.sys.UTILSysProperty"%>
<%@page import="com.matech.framework.listener.UserSession" %>
<%@page import="com.matech.framework.pub.util.ASFuntion"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.matech.audit.pub.db.DBConnect"%>
<%@page import="com.matech.framework.pub.db.DbUtil"%>

<%
	session.removeAttribute("REQUEST_FULLPATH");
	ASFuntion CHF = new ASFuntion() ;
	
	String title = CHF.showNull(UTILSysProperty.SysProperty.getProperty("title"));
	
	// 得到存在相应的环境变量的值
	String opt = UTILSysProperty.SysProperty.getProperty("E审通标题");
	if(opt!=null && !"".equals(opt)){
		title = opt;
	}
	
	String center = UTILSysProperty.SysProperty.getProperty("启用的中心");
	String ZH4Center = CHF.showNull(UTILSysProperty.SysProperty.getProperty("是否启用众华登陆首页")).trim();
	String centerIds = "" ;
	if(center != null && !"".equals(center)) {
		if(center.indexOf("审计作业中心") > -1) {
			centerIds +=",1" ;
		}
		if(center.indexOf("项目管理中心") > -1){
			centerIds +=",2" ;
		}
		if(center.indexOf("质量管理中心") > -1){
			centerIds +=",3" ;
		}
		if(center.indexOf("客户管理中心") > -1){
			centerIds +=",4" ;
		}
	}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title><%=title %></title>
</head>

<body onbeforeunload="killSession();" bgcolor="#DEEFF6";>

<object classid="clsid:C20572B8-6104-45B8-A3EE-303B42C26ABF" id="oframe" width="100%" height="96%" style="display:none;"> 
	<param name="BorderStyle" value="1">
	<param name="SideBarVisible" value="0">
	<param name="Titlebar" value="0">
	<param name="Menubar" value="1">
</object>
		
<center>
<img src="${pageContext.request.contextPath}/images/4center.jpg" border="0" usemap="#Map" />
<map name="Map" id="Map">
<!-- 审计作业中心  -->
<area shape="poly" onclick="openCenter(1);" coords="345,84,344,188,324,197,304,204,294,212,281,224,273,237,266,250,263,262,261,272,155,272,155,256,158,246,160,236,163,225,167,215,170,206,175,196,179,189,184,180,192,168,201,155,212,143,223,133,235,123,249,114,263,106,275,100,291,95,304,90,319,87" href="#" />

<!-- 项目管理中心  -->
<area shape="poly" onclick="openCenter(2);" coords="370,191,370,85,383,86,390,88,402,90,414,93,426,97,437,102,449,107,460,112,470,119,486,131,497,142,506,151,516,163,524,174,534,189,542,203,547,216,550,226,554,237,557,246,557,257,558,267,559,274,453,274,447,256,444,246,438,235,432,227,423,218,415,210,403,202,391,197" href="#" />

<!-- 质量控制中心  -->
<area shape="poly" onclick="openCenter(3);" coords="155,297,261,298,265,313,271,328,277,340,285,350,297,361,309,370,323,376,333,379,342,380,344,383,343,486,329,485,313,482,303,481,287,476,271,468,259,463,248,456,238,450,227,441,216,430,208,422,199,412,192,403,185,395,177,380,171,366,165,354,161,334" href="#" />

<!-- 客户管理中心  -->
<area shape="poly" onclick="openCenter(4);" coords="451,298,557,299,557,316,555,326,551,338,547,351,541,364,535,377,529,389,521,398,514,410,506,422,497,429,491,436,481,444,471,450,461,457,452,463,439,469,425,475,409,480,399,482,387,484,368,487,369,382,393,375,404,371,411,367,424,354,434,344,438,338,443,326,448,314" href="#" />

<!-- 新闻公告  -->
<area shape="circle" coords="358,288,73" href="#" onclick="goIndex();"/>

<!-- 退出系统  -->
<area shape="rect" onclick="exitSystem();" coords="591,514,703,541" href="#" />
</map>
</center>
</body>

<script type="text/javascript">
var is_close=true;

var clientSysVn = "${clientSysVn}";
var clientCenterIds = "";
var clientSysUi = "${userSession.clientDogSysUi}";
	
function isEntry(centerId) {
	var entry = true;
	
	if(clientSysUi != "no" && clientSysVn.indexOf("企业版") == -1 && clientSysVn != "u盾") {

		//非企业版副狗,只可以访问特定的中心
		if(clientSysVn.indexOf("审计作业") > -1) {
			clientCenterIds +=",1";
		}
		if(clientSysVn.indexOf("项目管理") > -1){
			clientCenterIds +=",2";
		}
		if(clientSysVn.indexOf("质量管理") > -1){
			clientCenterIds +=",3";
		}
		if(clientSysVn.indexOf("客户管理") > -1){
			clientCenterIds +=",4";
		}
		if(clientSysVn.indexOf("公共信息") > -1){
			clientCenterIds +=",5";
		}
		
		if(clientCenterIds.indexOf(centerId) >-1) {
			entry = true;
		} else {
			entry = false;
		}
	}
	
	return entry;
}

function openCenter(centerId){
	var centerIds = "<%=centerIds%>" ;
	
	
	if(centerIds.indexOf(centerId) >-1 && isEntry(centerId)) {	
		is_close=false;
		window.location="${pageContext.request.contextPath}/extIndex.jsp?centerId="+centerId;
	} else {
		alert("您没有进入该中心的权限！");
	}
}


function exitSystem() { 
	var bexit=false;
	var curTaskXmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	curTaskXmlHttp.open("POST","${pageContext.request.contextPath}/onlineUser.do?method=userCurTask",false);
	curTaskXmlHttp.send();
	var strResult = unescape(curTaskXmlHttp.responseText);

	if(strResult.indexOf("对不起") >= 0) {
		bexit=window.confirm(strResult.replace("对不起,你打开了以下底稿,请关闭后再进入项目! ","你打开了以下的底稿,请保存后再退出系统:\n点击[确定]将放弃保存强制退出，点击[取消]将留在本页面\n"));
	}else{
		bexit=window.confirm("是否退出系统？");
	}

	if (bexit){
		//不提示退出
		exitSystemAsk=0;
		window.location="${pageContext.request.contextPath}/common.do?method=exitSystem";
	}
}



function killSession(){

	  if(is_close){
		  var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		  oBao.open("POST","${pageContext.request.contextPath}/common.do?method=exitSystem&random=" + Math.random(),false);
		  try {
		    oBao.send();
		   
		  }catch(ex) {
		  }
	}
}

function goIndex() {
	var ZH4Center = "<%=ZH4Center%>" ;
	if(ZH4Center == "否" && isEntry("5")) {
		window.open("${pageContext.request.contextPath}/info.do?method=index");
	}else {
		return ;
	}
}

var AuditReport =  new ActiveXObject("AuditReportPoject.AuditReport");
</script>

</html>