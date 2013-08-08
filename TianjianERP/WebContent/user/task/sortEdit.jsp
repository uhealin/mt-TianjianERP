<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="../hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<html>
<head>
<title>新增节点</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
</head>
<body style=" margin: 5px; padding: 0px;">
<font color="#FF0000" size="2" ><strong>您现在所在位置: </strong><font color="#0000CC">编辑节点</font></font><br>
  <table width="500"  border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td height="1" bgcolor="#0000FF"></td>
  </tr>
</table>
<br/>

<form name="taskSortForm" id="taskSortForm" action="" method="post">
<table width="600" border="0" cellpadding="8" cellspacing="0">
	<tr>
		<td width="100" align="right">任务编号：</td>
		<td>
			<input type="text" name="taskCode" id="taskCode"
				value="${taskCode }" size="40" maxlength="10"
				class="required alphanum-wheninputed"
				title="请输入数字，字母，下划线，或 - ">
		</td>
	</tr>
	<tr>
		<td align="right">任务名称：</td>
		<td>
			<input type="text" name="taskName" id="taskName"
				value="${taskName }" size="40" maxlength="50" class="required filename-wheninputed"
				title='任务名不能包含\/:*?"<>|'>
		</td>
	</tr>
	<tr>
		<td align="right" valign="top">任务概述：</td>
		<td>
			<textarea name="taskContent" cols="60" rows="5" id="taskContent" >${taskContent }</textarea>
		</td>
	</tr>
	<tr>
		<td align="right" valign="top">备注：</td>
		<td>
			<textarea name="description" cols="60" rows="5" id="description" ></textarea>
		</td>
	</tr>

	<!-- 对应科目 -->
	<tr style="display: none;">
		<td align="right">对应科目：</td>
		<td>
		  	<input type="text" name="subjectName" id="subjectName" size = 50 maxlength="20" value = "${subjectName }"
		  	onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" autoid=122 noinput=false>
		</td>
	</tr>

	<tr style="display: none;">
		<td align="right">特殊任务：</td>
		<td>
			<input type="text" name="property" id="property" size="50" value="${property }"
				maxlength="20" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();"
				onclick="onPopDivClick(this);"  noinput=true autoid="10">
		</td>
	</tr>

	<tr>
		<td align="right" valign="top">是否必做：</td>
		<td><input type="checkbox" name="taskAttribute" id="taskismust" value="1" onclick="return onchecked(1);">必做
			<input type="checkbox" name="taskAttribute" id="taskisnotmust" value="0" onclick="return onchecked(2);">非必做
		</td>
	</tr>

	<tr>
		<td>
			<input name="taskId" type="hidden" id="taskId" value="${taskId }">
			<input name="parentTaskId" type="hidden" id="parentTaskId" value="${parentTaskId }">
			<input type="hidden" name="ismust" id="ismust" value="${ismust}">
		</td>
  		<td>
  			<input type="submit" value="确  定" class="flyBT" onClick="return goSave();">&nbsp;&nbsp;&nbsp;&nbsp;
  			<input type="button" name="back" value="返  回" class="flyBT"  onClick="window.history.back();">
  		</td>
	</tr>
 </table>

</form>
</body>
</html>

<script type="text/javascript">
new Validation('taskSortForm');

var parentTaskId = "${parentTaskId}";
var oldTaskCode = "${taskCode}";

//---------------------------
// 保存任务
//---------------------------
function goSave(){
	//检查新的任务编号是否唯一
	var taskCode = document.getElementById("taskCode").value;
	taskCode = jsTrim(taskCode);
	var taskName = document.getElementById("taskName").value;

	if(taskCode==""){
		alert("底稿编号不能为空！");
		return false;
	}

	if(document.getElementById("taskId").value == ""){
		//检查新的任务编号是否唯一
		if(!isOnly(taskCode)){
			return false;
		}

		document.taskSortForm.action = "${pageContext.request.contextPath}/oa/taskSort.do?method=save";
	}else {
	 	if(jsTrim(oldTaskCode) != taskCode){
			if(!isOnly(taskCode)){
	    		return false;
			}
		}
		document.taskSortForm.action = "${pageContext.request.contextPath}/oa/taskSort.do?method=update";
	}

}

//------------------------
// 检查新的任务编号是否唯一
//------------------------
function isOnly(taskcode){
	var oBao = new ActiveXObject("Microsoft.XMLHTTP");
	oBao.open("POST", "${pageContext.request.contextPath}/oa/task.do?method=checkIsOnly2&taskCode=" + taskcode + "&random=" + Math.random(),false);
	oBao.send();

	var strResult = unescape(oBao.responseText) + "";
	if(strResult.indexOf("only")>=0){
		return true;
	}else{
		alert("相同任务编号已存在，名称为：" + strResult + "，本任务无法保存。");
		return false;
	}
}

//--------------------
// 去空格函数
//--------------------
function jsTrim(str) {
    return str.replace(/^\s* | \s*$/g,"");
}

//--------------------
// 响应必做/非必做单击事件
//--------------------
function onchecked(flag){
	if(flag == "1"){
		document.getElementById("taskismust").checked = true;
		document.getElementById("taskisnotmust").checked = false;
		document.getElementById("ismust").value = "yes";
	}
	else{
		document.getElementById("taskismust").checked = false;
		document.getElementById("taskisnotmust").checked = true;
		document.getElementById("ismust").value = "no";
	}
}

if(document.getElementById("ismust").value == "yes" || document.getElementById("taskId").value == "" ){
	//document.write(document.getElementById());
	document.getElementById("taskismust").checked = true;
	document.getElementById("taskisnotmust").checked = false;
}
else{
	document.getElementById("taskismust").checked = false;
	document.getElementById("taskisnotmust").checked = true;
}
</script>
