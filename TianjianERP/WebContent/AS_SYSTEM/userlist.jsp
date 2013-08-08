<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>在线人员列表</title>

<script>

	function ext_init(){
        
	    var tbar = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		defaults: {autoHeight: true,autoWidth:true},
            items:[{
	            text:'刷新',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/refresh.gif',
	            handler:function(){
	           		 window.location.reload();
				}  
       		},'-',{
	            text:'给所有在线用户发消息',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/mytask.gif',
	            handler:function(){
					sendMessage('${allUserId}');
				}
       		},'->'
			]
        });
        
    }
    window.attachEvent('onload',ext_init);
</script>
</head>
<body>
<div id="divBtn"></div>
<div style="height:expression(document.body.clientHeight-27);overflow:auto;width: 100%;padding: 5px;">
<fieldset  style="width:100%">
	<legend>基本信息</legend>
	<div style="OVERFLOW: auto">
	 <table id="DataGridTable" cellSpacing="1" cellPadding="3" width="100%" bgColor="#6595d6" border="0">
	     <tr class="DGtd" style="height: 24px;">
	       <td noWrap align="middle" bgColor="#DDE9F9" >序号</td>
	       <td noWrap align="middle" bgColor="#DDE9F9">在线用户</td>
	       <td noWrap align="middle" bgColor="#DDE9F9">登录名</td>
           <td noWrap align="middle" bgColor="#DDE9F9">所在部门</td>
	       <td noWrap align="middle" bgColor="#DDE9F9">终端IP地址</td>
	       <td noWrap align="middle" bgColor="#DDE9F9">登陆时间</td>
	       <td noWrap align="middle" bgColor="#DDE9F9">客户端加密狗编号</td>
	       <td noWrap align="middle" bgColor="#DDE9F9">操作</td>
	     </tr>
		<% int onlineCount = 0; //在线人数 %>
	    <c:forEach items="${userList}" var="us"  varStatus="var">
			<tr onmouseover="this.bgColor='#E4E8EF';" onmouseout="this.bgColor='#ffffff';" style="${userSession.userSessionId == us.userSessionId ? 'font-weight:bold;color:blue;' : ''}" bgColor="#ffffff" >
				<td align="center"><%  onlineCount ++; %> <%=onlineCount%></td>
				<td>${us.userName} </td>
				<td>${us.userLoginId}</td>
				<td>${us.userAuditDepartmentName}</td>
				<td>${us.userIp}</td>
				<td>${us.userLoginTime}</td>
				<td>${us.clientDogSysUi=='no'? '' : us.clientDogSysUi} </td>
				<td align="center">
					<input type="button" value="发送消息" ${userSession.userSessionId == us.userSessionId ? 'disabled=disabled' : ''} class="flyBT" onclick="sendMessage('${us.userId }');" />
					<input type="button" value="强制退出" ${userSession.userSessionId == us.userSessionId ? 'disabled=disabled' : ''} class="flyBT" onclick="kickUser('${us.userSessionId }','${us.userLoginId }');" />
				</td>
			</tr>
		</c:forEach>
	  </table>
	  <div style="margin-top: 10px;">在线人数为：<font color="red" style="font-weight:900;font-size: 15px;"><%= onlineCount %></font>人！</div>
	  
	</div>
</fieldset>

<fieldset  style="width:100%">
	<legend>项目和底稿信息</legend>
	<div style="OVERFLOW: auto">
		<table id="DataGridTable1" cellSpacing="1" cellPadding="3" width="100%" bgColor="#6595d6" border="0">
			<tr class="DGtd" style="height: 22px;">
				<td noWrap align="middle" bgColor="#DDE9F9" colspan="2" width="20%">用户姓名</td>
				<td noWrap align="middle" bgColor="#DDE9F9" width="10%">用户编号</td>
				<td noWrap align="middle" bgColor="#DDE9F9" width="30%">用户IP</td>
				<td noWrap align="middle" bgColor="#DDE9F9" width="30%">所在项目</td>
				<td noWrap align="middle" bgColor="#DDE9F9" width="10%">所在项目ID</td>
		 	</tr>

			<c:forEach items="${userList}" var="us"  varStatus="var">
				<c:set var="onclick" value="" ></c:set>
				<c:set var="cursor" value="" ></c:set>
				<c:set var="title" value="" ></c:set>
				<c:set var="img" value="${pageContext.request.contextPath}/images/nofollow.jpg"></c:set>
				<c:if test="${fn:length(us.userCurTasks) != 0}">
					<c:set var="onclick" value="showOrHide('${us.userSessionId }');" ></c:set>
					<c:set var="cursor" value="cursor:hand;" ></c:set>
					<c:set var="title" value="点击展开查看用户打开底稿" ></c:set>
					<c:set var="img" value="${pageContext.request.contextPath}/images/plus.jpg"></c:set>
				</c:if>

				<tr title="${title }" onclick="${onclick }" bgColor="#E4E8EF" style="height: 22px; ${cursor } ${userSession.userSessionId == us.userSessionId ? 'font-weight:bold;color:blue;' : ''}">
					<td colspan="2"><img id="img${us.userSessionId}" src="${img}" />&nbsp;&nbsp; ${us.userName} </td>
					<td>${us.userId} </td>
					<td>${us.userIp} </td>
					<td>${us.curProjectName} </td>
					<td>${us.curProjectId} </td>
	           	</tr>
	           	<tbody id="tbody${us.userSessionId }" style="display: none;">
		           	<tr bgcolor="#E4E8EF" style="height: 22px;">
		           		<td noWrap align="center">&nbsp;&nbsp;<img src="${pageContext.request.contextPath}/images/nofollow.jpg" /></td>
			            <td noWrap align="center">底稿名称</td>
						<td noWrap align="center">索引号</td>
						<td noWrap align="center">打开时间</td>
						<td noWrap align="center">打开方式</td>
						<td noWrap align="center">操作</td>
					</tr>
					<c:set var="readOnly" value="只读打开"></c:set>
					<c:set var="rendAndWrite" value="读写打开"></c:set>
		           	<c:forEach items="${us.userCurTasks}" var="curTask"  varStatus="var">
			           	<tr bgcolor="#ffffff" style="height: 22px;">
			           			<td noWrap align="center">&nbsp;&nbsp;<img src="${pageContext.request.contextPath}/images/nofollow.jpg" /></td>
				           		<td>${curTask.curTaskName}</td>
								<td>${curTask.curTaskCode}</td>
								<td>${curTask.curTaskEditTime}</td>

								<c:choose>
									<c:when test="${empty curTask.curTaskOpenType}">
										<td></td>
										<td align="center"><input type="button" disabled="disabled" value="关闭底稿" class="flyBT" /></td>
									</c:when>
									<c:otherwise>
										<td> ${curTask.curTaskOpenType == 1 ? rendAndWrite : readOnly } </td>
										<td align="center"><input type="button" ${(userSession.userSessionId == us.userSessionId || userSession.userId=='19') ? '': 'disabled=disabled'} value="关闭底稿" class="flyBT" onclick="closeTask('${us.userSessionId }','${curTask.curTaskId}','${curTask.curTaskProjectId}');" /></td>
									</c:otherwise>
									
								</c:choose>
			           	</tr>
		           	</c:forEach>
	           	</tbody>
			</c:forEach>
		</table>
	</div>
</fieldset>

<fieldset  style="width:100%">
	<legend>并发处理</legend>
	<div style="OVERFLOW: auto">
	 <table id="DataGridTable" cellSpacing="1" cellPadding="3" width="100%" bgColor="#6595d6" border="0">
	     <tr class="DGtd" style="height: 22px;">
	       <td noWrap align="middle" bgColor="#DDE9F9">用户</td>
	       <td noWrap align="middle" bgColor="#DDE9F9">处理事项</td>
	       <td noWrap align="middle" bgColor="#DDE9F9">处理时间</td>
	     </tr>

	    <c:forEach items="${lockList}" var="lockinfo"  varStatus="var">
			<tr onmouseover="this.bgColor='#E4E8EF';" onmouseout="this.bgColor='#ffffff';" style="height: 22px; ${userSession.userSessionId == us.userSessionId ? 'font-weight:bold;color:blue;' : ''}" bgColor="#ffffff" >
				<td>${lockinfo.strUserid}</td>
				<td>${lockinfo.strKey}</td>
				<td>${lockinfo.strWorktime}</td>
			</tr>
		</c:forEach>
	  </table>
	</div>
</fieldset>
</div>
</body>
<script language="javaScript">
//----------------------------------
// 踢出在线用户
//----------------------------------
function kickUser(sessionId,user) {

	if(user == 'admin') {
		alert('对不起,你不能强制退出管理员!');
		return;
	}
	if(sessionId == '')
		alert('对不起,系统错误,失败!');
	else {
		if(confirm("确定要强制退出该用户？")) {
			var onlineXmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
			onlineXmlHttp.open("POST","${pageContext.request.contextPath}/onlineUser.do?method=kickUser&sessionId=" + sessionId,false);
			onlineXmlHttp.send();
			window.location.reload();
		}
	}
}

function sendMessage(userId) {

	if(userId == '') {
		alert("对不起,系统错误,发送失败");
	} else {
		var left = ( screen.availWidth - 500 ) / 2;
		var top = ( screen.availHeight - 400 ) / 2;
		window.open('${pageContext.request.contextPath}/AS_SYSTEM/sendMessage.jsp?userId=' + userId,'','left=' + left +',top=' + top + ',width=500,height=400,toolbar=no,menubar=no,scrollbars=yes,resizable=no, location=no,status=no');
	}

}


function closeTask(sessionId, taskId, projectId) {
	if(sessionId=="") {
		alert("对不起,系统错误,关闭失败,sessionId为空:" + sessionId);

	} else if(taskId=="") {
		alert("对不起,系统错误,关闭失败,taskId为空:" + taskId);

	} else if(projectId=="") {
		alert("对不起,系统错误,关闭失败,projectId为空:" + projectId);

	} else if(confirm("本功能仅用于不启动服务器情况下强制性关闭,选中用户正在编辑的数据有可能无法保存,是否继续?")) {
		var onlineXmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
		var url = "${pageContext.request.contextPath}/onlineUser.do?method=closeTask&sessionId=" + sessionId + "&taskId=" + taskId + "&projectId=" + projectId;
		onlineXmlHttp.open("POST", url, false);
		onlineXmlHttp.send();
		window.location.reload();
	}
}

function showOrHide(sessionId) {
	var tbodyObj = document.getElementById("tbody" + sessionId);
	var imgObj = document.getElementById("img" + sessionId);
	if(tbodyObj.style.display == "") {
		tbodyObj.style.display = "none";
		imgObj.src = "${pageContext.request.contextPath}/images/plus.jpg";
	} else {
		tbodyObj.style.display = "";
		imgObj.src = "${pageContext.request.contextPath}/images/nofollow.jpg";
	}
}
</script>
</html>