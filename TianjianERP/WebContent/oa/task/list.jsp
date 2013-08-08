<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>底稿列表</title>
</head>
<body style=" margin: 5px; padding: 0px;">
<font color="#FF0000" size="2" ><strong>您现在所在位置: </strong><font color="#0000CC">程序和目标</font></font><br>
  <table width="500"  border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td height="1" bgcolor="#0000FF"></td>
  </tr>
</table>
<br/>

<input name="parentTaskId" type="hidden" id ="parentTaskId" value ="${parentTaskId }">
<c:choose>

	<c:when test="${parentTaskId != '0' && parentTaskId != 'mytask' && parentTaskId != ''}">

		<input name="" type="button" class="flyBT" value="增  加" onclick="goTaskAdd();" >&nbsp;
		<input name="" type="button" class="flyBT" value="修改底稿属性" onclick="goEdit();"  >&nbsp;
		<input name="" type="button" class="flyBT" value="删  除" onclick="goDelete();">&nbsp;

	<!--
		<input name="" type="button" class="flyBT" value="移  动" onclick="goMove();">&nbsp;
		<input name="" type="button" class="flyBT" value="批量导入底稿" onclick="goBatchUpload();">&nbsp;
		<input name="" type="button" class="flyBT" value="分类管理" onclick="goSort();">&nbsp;
		<input name="" type="button" class="flyBT" value="重用模版" onclick="goRecover();">&nbsp;
		<c:if test="${userSession.userAuditOfficeName == '铭太科技内部专用'}">
			<input name="" type="button" class="flyBT" value="保存到模板" onclick="saveToTemplate();">&nbsp;
		</c:if>
		<input name="" type="button" class="flyBT" value="审计目标及程序" onclick="goTarPro();">&nbsp;
		<input type="button" value="返回工作台" name="getback" id="getback" class="flyBT" onclick="return goBack();">
	-->

	<br/><br/>
		<mt:DataGridPrintByBean name="jbpm_taskList"/>

		<br/>
		<iframe id="iframeTarAndpro" name="iframeTarAndpro" width="100%" height="100%" frameborder="0"  src="${pageContext.request.contextPath}/oa/task.do?method=tarAndPro&parentTaskId=${parentTaskId }"  >
		</iframe>
	</c:when>

	<c:otherwise>
		请选择节点
	</c:otherwise>
</c:choose>

<br/>
</body>

<Script type="text/javascript" language="javascript">
var parentTaskId = document.getElementById("parentTaskId").value;

var curAuditTypeProperty = "${userSession.curAuditTypeProperty}";
var projectId = "${projectId}";

//---------------------------------
// 重置底稿
//---------------------------------
function goRecover() {
	var taskId = document.getElementById("chooseValue_taskList").value;
	if(taskId == ""){
		alert("请选择要重置的底稿！");
	}else{
		if(isOpen(taskId)) {
			alert("已经有人打开了该张底稿,不允许重置该底稿!!!");
			return;
		}
		if(confirm("您的操作可能会造成数据丢失，您确定要重置该底稿吗？","提示")){
			var result = recover(taskId);
			alert(result);

			try {
				if(result.indexOf('失败') == -1) {
					var tr = document.getElementById("tr" + taskId);
					openTaskFile(tr);
				}
			} catch(e) {
				//alert(e);
			}

		}
	}
}

//----------------------------
// 重置底稿
//----------------------------
function recover(taskId) {

	var xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
    xmlHttp.open("POST","${pageContext.request.contextPath}/taskCommon.do?method=recover&taskId=" + taskId + "&random=" + Math.random(),false);
    xmlHttp.send();
    var strResult = unescape(xmlHttp.responseText) + "";
    return strResult;
}

//-----------------------------
// 分类管理
//-----------------------------
function goSort(){
    if(parentTaskId=="mytask"){
		alert("不能在我的任务下进行分类管理！");
    }else{
    	window.location="${pageContext.request.contextPath}/taskSort.do?method=list&parentTaskId=" + parentTaskId;
    }
}

//-------------------------
// 添加一张新底稿
//-------------------------
function goTaskAdd(){
    if(parentTaskId=="null"||parentTaskId==""||parentTaskId=="0"||parentTaskId=="mytask"){
		alert("请在相应的分类下增加底稿！");
		return false;
	}else{
		var url = "${pageContext.request.contextPath}/taskCommon.do?method=add&parentTaskId=" + parentTaskId;
	//	myOpenUrl(url);
	    myOpenUrlByWindowOpen(url,"","");
    }
}

//------------------------
// 删除一张底稿
//------------------------
function goDelete(){
	var chooseValue_taskList = document.getElementById("chooseValue_taskList").value;

	if(chooseValue_taskList == ""){
		alert("请选择要删除的底稿！");
	} else {

		if(isOpen(chooseValue_taskList)) {
			alert("已经有人打开了该张底稿,不允许删除该底稿!!!");
			return;
		}

		var url = "${pageContext.request.contextPath}/taskCommon.do?method=remove&taskType=taskManu&taskId=" + chooseValue_taskList + "&parentTaskId=" + parentTaskId;
		if(confirm("您的操作可能会造成数据丢失，您确定要删除该记录吗？","提示")){
			if(!isComplete(chooseValue_taskList)) {
				if(confirm("该底稿已经编制或审核完成,你确定要删除该底稿吗?","提示")) {
					window.location = url;
				}
			} else {
				window.location = url;
			}
		}
	}
}

//------------------------
// 移动底稿
//------------------------
function goMove() {

	var chooseValue_taskList = document.getElementById("chooseValue_taskList").value;

	if(chooseValue_taskList == ""){
		alert("请选择要移动的底稿！");
	} else {
		if(isOpen(chooseValue_taskList)) {
			alert("已经有人打开了该张底稿,不允许移动该底稿!!!");
			return;
		}
		window.location="${pageContext.request.contextPath}/taskCommon.do?method=moveView&taskType=taskManu&taskId=" + chooseValue_taskList;
	}
}

//------------------------
// 编辑底稿信息
//------------------------
function goEdit() {
	var chooseValue_taskList = document.getElementById("chooseValue_taskList").value;
	if(chooseValue_taskList==""){
		alert("请选择要修改的底稿！");
	}else{
		if(isOpen(chooseValue_taskList)) {
			alert("已经有人打开了该张底稿,不允许修改该底稿!!!");
			return;
		}

		window.location="${pageContext.request.contextPath}/taskCommon.do?method=edit&taskType=taskManu&taskId=" + chooseValue_taskList + "&parentTaskId=" + parentTaskId;
	}
}

function openTaskFile(objTR){
	var taskId = objTR.taskId;
	var fullPath = objTR.fullPath;
	var manuId = objTR.manuId;
	var fullPath = objTR.fullPath;

	var url = "/AuditSystem/taskCommon.do?method=fileOpen"
			+ "&taskId=" + taskId
			+ "&projectId=" + projectId
			+ "&random=" + Math.random();

	myOpenUrl(url);
	//window.open(url, target="_blank", "height=" + screen.availheight + ",width=" + screen.availwidth + ",top=0,left=0,toolbar=no,locationbar=no,status=no");
}

function goTarPro(){
	if( parentTaskId == ""
			||parentTaskId == "0"
			||parentTaskId == "mytask" ){

		alert("请先选择相应的分类，再编辑审计目标及程序信息！");
		return;
	}

	try {
		if(document.all.iframeTarAndpro.style.display == "") {
			document.all.iframeTarAndpro.style.display = "none";
		} else {
			document.all.iframeTarAndpro.style.display = "";
		}
	} catch(e) {}
}

function getTarPro(){
    var oBao = new ActiveXObject("Microsoft.XMLHTTP");
    oBao.open("POST","${pageContext.request.contextPath}/task.do?method=getTargetAndProcedure&parentTaskId=" + parentTaskId + "&random=" + Math.random(),false);
    oBao.send();
    var strResult = unescape(oBao.responseText) + "";
    return strResult;
}

//----------------------------
// 批量导入底稿
//----------------------------
function goBatchUpload() {
	if(parentTaskId=="mytask") {
		alert("不能在我的任务下面导入底稿");
		return;
	}
  	window.location="${pageContext.request.contextPath}/taskCommon/batchUpload.jsp?parentTaskId=" + parentTaskId;
}

//----------------------------
// 保存用户当前项目最后一次打开的任务
//----------------------------
function saveUserState(taskId) {
	var url = "${pageContext.request.contextPath}/taskCommon.do?method=savaUserState"
			+ "&taskId=" + taskId
			+ "&random=" + Math.random();

	var userStateXmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	userStateXmlHttp.open("post",url,false);
	userStateXmlHttp.send();
}

//---------------------------------//
// 判断底稿是否已经被编制或审核
//---------------------------------//
function isComplete(taskId) {
    var oBao = new ActiveXObject("Microsoft.XMLHTTP");
    oBao.open("POST","${pageContext.request.contextPath}/taskCommon.do?method=isComplete&taskId=" + taskId + "&random=" + Math.random(),false);
    oBao.send();
    var strResult = unescape(oBao.responseText) + "";

    if(strResult.indexOf("true") < 0) {
    	return true;
    } else {
    	return false;
    }
}

//---------------------------------//
// 判断底稿是否已经被打开
//---------------------------------//
function isOpen(taskId) {
	var oBao = new ActiveXObject("Microsoft.XMLHTTP");
    oBao.open("POST","${pageContext.request.contextPath}/taskCommon.do?method=isOpen&taskId=" + taskId + "&random=" + Math.random(),false);
    oBao.send();
    var strResult = unescape(oBao.responseText) + "";

    if(strResult.indexOf("true") > -1) {
    	return true;
    } else {
    	return false;
    }

}
//------------------------
// 返回工作台
//------------------------

function goBack(){
	var targetUrl = "${pageContext.request.contextPath}/AuditProject.do?method=login&pid=${userSession.curProjectId}";
	var url = window.parent.location + "";

	if(url.indexOf('mainFrame.jsp') > -1){
		window.location = targetUrl;
	}else {
		window.parent.location = targetUrl;
	}
}

//------------------------
// 保存到模板
//------------------------
function saveToTemplate() {
	var chooseValue_taskList = document.getElementById("chooseValue_taskList").value;
	if(chooseValue_taskList==""){
		alert("请选择要保存的底稿！");
	}else{

		if(confirm("该操作会把模板中的数据覆盖,确定要继续操作吗?","提示")) {
			var query_String = "taskId=" + chooseValue_taskList + "&projectId=" + projectId;

			var url = "${pageContext.request.contextPath}/taskCommon.do?method=saveToTemplate";

			var result = ajaxLoadPageSynch(url,query_String);

			if(result.indexOf("ok") < 0) {
				alert(result);
			} else {
				alert("保存到模板成功!!请到模板管理清理底稿！！");
			}
		}
	}
}

function refreshLeftFrame() {
	parent.TaskLeftFrame.location = parent.TaskLeftFrame.location;
}

//---------------------------------//
// 展开左边的树
//---------------------------------//
function openLeftTree() {

	try {
		parent.TaskLeftFrame.doItForRight(parentTaskId);
	} catch(e) {
		//alert(e);
	}
}

var refresh = "${param.refresh}";

if(refresh == "true") {
	refreshLeftFrame();
	setTimeout("openLeftTree()",500);
}

</script>
</html>