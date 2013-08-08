<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>底稿列表</title>
</head>
<body style=" margin: 5px; padding: 0px;">
<font color="#FF0000" size="2" ><strong>您现在所在位置: </strong><font color="#0000CC">节点管理</font></font><br>
  <table width="500"  border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td height="1" bgcolor="#0000FF"></td>
  </tr>
</table>
<br/>

<input name="" type="button" class="flyBT" value="增  加" onclick="goAdd();">&nbsp;
<input name="" type="button" class="flyBT" value="修  改" onclick="goEdit();">&nbsp;
<input name="" type="button" class="flyBT" value="删  除" onclick="goDelete();">&nbsp;
<input name="" type="button" class="flyBT" value="移  动" onclick="goMove();">&nbsp;
<input name="" type="button" class="flyBT" value="打  印" onclick="print_taskSortList();">&nbsp;
<input name="" type="button" class="flyBT" value="返  回" onclick="history.back();">&nbsp;
<input name="parentTaskId" type="hidden" id ="parentTaskId" value ="${parentTaskId}">

<br/>
<br/>
<mt:DataGridPrintByBean name="taskSortList"/>

</body>
<script language="javascript" type="text/javascript">
var parentTaskId = document.getElementById("parentTaskId").value;

function goEdit() {
	var chooseValue_taskSortList = document.getElementById("chooseValue_taskSortList").value;

	if(chooseValue_taskSortList == "") {
		alert("请选择要修改的分类！");
	} else {
		window.location="${pageContext.request.contextPath}/oa/taskSort.do?method=edit&taskId=" + chooseValue_taskSortList;
	}
}

function goAdd(){
    window.location="${pageContext.request.contextPath}/oa/taskSort.do?method=add&parentTaskId=" + parentTaskId;
}

function goDelete(){
	var chooseValue_taskSortList = document.getElementById("chooseValue_taskSortList").value;

	if(chooseValue_taskSortList == ""){
		alert("请选择要删除的分类！");
	} else {
		if(confirm("您的操作可能会造成数据丢失，您确定要删除该记录吗？","提示")){
			//window.location="${pageContext.request.contextPath}/taskCommon.do?method=remove&taskType=task&taskId=" + chooseValue_taskSortList + "&parentTaskId=" + parentTaskId;
		}
	}
}


function refreshLeftFrame() {
	parent.TaskLeftFrame.location = parent.TaskLeftFrame.location;
}

var refresh = "${param.refresh}";

if(refresh == "true") {
	refreshLeftFrame();
}
</Script>


