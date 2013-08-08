<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>	
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>站内邮件管理</title>
<style type="text/css">
</style>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/oa/interiorEmail/email.css" />
</head>

<body style="overflow: hidden;">
<table style="width: 100%;height: 99%;" border =0 scrooling="no" topmargin="0" leftmargin="0">
	<tr>
	<td style="width: 13%; height: 100%;display: bolck;" id="menuShow" >
		<div id="body">
		<div class="toolBar">
		   <a class="nmBtn" href="javascript:void(0);"  title="点击查看新邮件" id="nmBtnShow"
		    		onmouseout="bgOnmouseout('nmBtnShow')" onmouseover="bgOnmouseover('nmBtnShow')"
		    		onclick="openPage('${pageContext.request.contextPath}/interiorEmail.do?method=addresseeList&ifRead=1')">
		   			<img src="${pageContext.request.contextPath}/images/email/nm.png"  >未 读(<span id="notGet">${notGet}</span>)
		   </a>
		   <a class="wmBtn" href="javascript:void(0);"  id="wmBtnShow" 
		   		onmouseout="bgOnmouseout('wmBtnShow')" onmouseover="bgOnmouseover('wmBtnShow')" 
		   		onclick="openPage('${pageContext.request.contextPath}/interiorEmail.do?method=addSkip')">
		   		<img src="${pageContext.request.contextPath}/images/email/wm.png" align="absMiddle">写 信
		   	</a>
		</div>
		
		<div class="header" style="margin-left: 5">邮件箱管理</div>
		<ul id="folder">
		  <li><a href="javascript:void(0);"  onclick="openPage('${pageContext.request.contextPath}/interiorEmail.do?method=addresseeList')">
		  		<img src="${pageContext.request.contextPath}/images/email/inbox.gif" /> 收件箱 
		  		(<span id="addresseeCount">${addresseeCount }</span>)</a>
		  </li>
		  <li>
		  		<a href="javascript:void(0);" onclick="openPage('${pageContext.request.contextPath}/interiorEmail.do?method=draftList')">
		  		<img src="${pageContext.request.contextPath}/images/email/outbox.gif" /> 草稿箱
		  		 (<span id="draftCount">${draftCount}</span>)</a>
		  </li>
		  <li>
		  		<a href="javascript:void(0);"  onclick="openPage('${pageContext.request.contextPath}/interiorEmail.do?method=sendList')">
		  		<img src="${pageContext.request.contextPath}/images/email/sentbox.gif" /> 已发送
		  		 (<span id="alreadyCount">${alreadyCount}</span>)</a>
		  </li>
		  <li>
		  		<a href="javascript:void(0);"  onclick="openPage('${pageContext.request.contextPath}/interiorEmail.do?method=deletedList')">
		  		<img src="${pageContext.request.contextPath}/images/email/trash.gif"  /> 已删除 
		  		(<span id=delCount>${delCount }</span>)</a>
		  </li>
		</ul>
		</div>
		<td align="left">
		<td style="width: 1.8%;height: 10px;background-color:#E7F1F8;border-collapse:collapse;border-RIGHT: #b8d1e2 1px solid; vertical-align: middle;" onclick="bodyShow()">
			<img src="${pageContext.request.contextPath}/images/default/layout/mini-left.gif"  id="showImage" alt="隐藏菜单"/>
		</td>
		<td width="81%" height="100%" align="left">
			<iframe src="${pageContext.request.contextPath}/interiorEmail.do?method=addresseeList" style="height: 100%;width: 100%;margin: 0px;padding: 0px;" frameborder="0" id="mainIframe" name="mainIframe"> </iframe>
		</td>
	</tr>
</table>
</body>
<script type="text/javascript">
		
function openPage(url){
	if(url == "")
		return;
	else
		document.getElementById("mainIframe").src=url+"&rand="+Math.random();
		//showWaiting();//等待提示
}

function bgOnmouseout(obj){
	document.getElementById(obj).style.backgroundImage="";
}
function bgOnmouseover(obj){
	document.getElementById(obj).style.backgroundImage="url(${pageContext.request.contextPath}/images/email/bgColor.png)";
}
 function bodyShow(){
	 var menuShow = document.getElementById("menuShow");
	 var showImage = document.getElementById("showImage");
	 
	 if(menuShow.style.display == "none"){
		 document.getElementById("menuShow").style.display ="block";
		 showImage.src = "${pageContext.request.contextPath}/images/default/layout/mini-left.gif";
		 showImage.alt = "隐藏菜单";
	 }else{
		 document.getElementById("menuShow").style.display ="none";
		 showImage.src = "${pageContext.request.contextPath}/images/default/layout/mini-right.gif";
		 showImage.alt = "显示菜单";
	 }
 }
 
var isReadOnly="${isReadOnly}";
var back="${back}";
var uuid = "${uuidName}";
if(isReadOnly=="true" && back=="true" && uuid !=""){
	document.getElementById("mainIframe").src="${pageContext.request.contextPath}/interiorEmail.do?method=read&uuid="+uuid+"&isReadOnly=true&back=addresseeList&rand="+Math.random();
}
if("${userIdAll}" !=""){
	document.getElementById("mainIframe").src="${pageContext.request.contextPath}/interiorEmail.do?method=goSendEmailByIds&userIdAll=${userIdAll}&back=addresseeList&rand="+Math.random()
}
</script>
</html> 