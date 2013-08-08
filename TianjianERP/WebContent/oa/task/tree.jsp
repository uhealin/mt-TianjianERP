<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title></title>

<style>
.stateSpan {
	height:12px;
	overflow:hidden;
	width:12px;
	border: 1px solid #000000;
	padding:0px;
	margin: 0px;
}
</style>
</head>
<body style="color: #0000CC; padding: 0px; margin-left: 10px;">
<!--
<span title="相关底稿必须进行处理" style="cursor: hand;" onclick="search('1');">
	<span class="stateSpan" style="background-color:#B33232; ">
	</span>&nbsp;<span style="color:#B33232 ">必做</span>&nbsp;
</span>

<span title="相关底稿在重大事件汇报中被提及" style="cursor: hand;" onclick="search('2');">
	<span class="stateSpan" style="background-color:#D97125; ">
	</span>&nbsp;<span style="color:#D97125 ">关注</span>&nbsp;
</span>

<span title="用户账上有相关科目数据" style="cursor: hand;" onclick="search('3');">
	<span class="stateSpan" style="background-color:#1A76B7; ">
	</span>&nbsp;<span style="color:#1A76B7 ">有数据</span>&nbsp;
</span>

<span title="已有项目成员编制底稿并进行了保存" style="cursor: hand;" onclick="search('4');">
	<span class="stateSpan" style="background-color:#66A72D; ">
	</span>&nbsp;<span style="color:#66A72D ">已保存</span>&nbsp;
</span>
<br/>
 -->
<span id="title">
<a href="${pageContext.request.contextPath}/oa/task.do?method=list&parentTaskId=0"
	target='TaskMainFrame'
	style=" font-size:12px;"
	onclick="doIt(this);closeFrame();"><b>所有节点</b>
</a>&nbsp;
<!--
	<a href="${pageContext.request.contextPath}/task.do?method=list&parentTaskId=mytask"
		target='TaskMainFrame'
		style=" font-size:12px;font-weight:bold;"
		onclick="doIt(this)"><b>我的任务</b>
	</a>&nbsp;
 -->
<a href="${pageContext.request.contextPath}/oa/taskSort.do?method=add&parentTaskId=0"
	target='TaskMainFrame'
	style=" font-size:12px;font-weight:bold;"><b>新增节点</b>
</a>&nbsp;

<a href="${pageContext.request.contextPath}/oa/taskSort.do?method=list&parentTaskId=0"
	target='TaskMainFrame'
	style=" font-size:12px;font-weight:bold;"
	onclick="doIt(this)"><b>节点管理</b>
</a>
</span>

${tree}
<input type="hidden" name="taskId" id="taskId" value="${taskId}">
<input type="hidden" name="projectId" id="projectId" value="${param.projectId}">
<input type="hidden" name="isLeaf" id="isLeaf" value="${param.isLeaf}">

</body>

<script type="text/javascript">

var projectId = "${projectId}";

if(document.getElementById("isLeaf").value == "false") {
	document.getElementById("title").style.display = "none";
}
//-----------------------------
// 获得子树
//-----------------------------
function getSubTree(id) {
	var objId = "subTree"+id;
	var objImg = "ActImg"+id;
	var objBank = "subImg"+id;
	var objM=document.getElementById(objImg);
	var objB=document.getElementById(objBank);
	var obj=document.getElementById(objId);

	if(obj != null) {
		//obj.innerHTML = '正在加载，请稍候';
		if(obj.style.display==""){
			obj.style.display = "none";
			objB.style.display = "none";
			objM.src="${pageContext.request.contextPath}/images/plus.jpg";
		} else {
			showObj(id);
		}
	}
}

//-----------------------------
// 显示子树
//-----------------------------
function showObj(id) {
	var objId = "subTree"+id;
	var objImg = "ActImg"+id;
	var objBank = "subImg"+id;
	var objM=document.getElementById(objImg);
	var objB=document.getElementById(objBank);
	var obj=document.getElementById(objId);

	if(obj != null) {
		obj.style.display = "";
		objB.style.display = "";
		objM.src="${pageContext.request.contextPath}/images/nofollow.jpg";

		//每次展开时无条件刷新一次
		var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		var url = "${pageContext.request.contextPath}/oa/task.do?method=getSubTree&parentTaskId="+ id
			 	+ "&projectId=" + document.getElementById("projectId").value
			 	+ "&isLeaf=" + document.getElementById("isLeaf").value
			 	+ "&random=" + Math.random();
		oBao.open("GET", url ,false);
		oBao.send();
		var strResult = unescape(oBao.responseText)+"";
		obj.innerHTML = strResult;
	}
}

//-----------------------------
// 关闭窗口
//-----------------------------
function closeFrame(){
	parent.parent.TaskLeftFrame.cols="50,*";
}

//-----------------------
// 选中的对象突出显示
//-----------------------
function doItForRight(id) {

	var temp = document.getElementById("a" + id);
	doIt(temp);
}

//-----------------------
// 选中的对象突出显示
//-----------------------
function doIt(obj){
	if(obj != null) {
		var allLinkTags = document.getElementsByTagName("a");
		for(var i=0;i < allLinkTags.length;i++){
			if(allLinkTags[i].style.fontWeight != "bold") {
				allLinkTags[i].style.color='#0000CC';
			}
		}
		obj.style.color='red';
	}

}

//-----------------------------------------
//每次点击都生成一个随机数加在连接后面,防止网页缓存
//-----------------------------------------
function goUrl(UNID,taskId) {
	var url = "/AuditSystem/taskCommon.do?method=fileOpen"
			+ "&taskId=" + taskId
			+ "&projectId=" + projectId
			+ "&isBack=no"
			+ "&random=" + Math.random();

	myOpenUrl(url);
	//alert(url);
	//screen.availwidth,screen.availheight
	//window.open(url, target="_blank", "height=" + screen.availheight + ",width=" + screen.availwidth + ",top=0,left=0,toolbar=no,locationbar=no,status=no");
}

//-----------------------------------------
//每次点击都生成一个随机数加在连接后面,防止网页缓存
//-----------------------------------------
function goUrl2(taskId,sheetTaskCode) {
	var url = "/AuditSystem/taskCommon.do?method=fileOpen"
			+ "&taskId=" + taskId
			+ "&projectId=" + projectId
			+ "&sheetTaskCode=" + sheetTaskCode
			+ "&isBack=no"
			+ "&random=" + Math.random();

	myOpenUrl(url);
	//alert(url);
	//screen.availwidth,screen.availheight
	//window.open(url, target="_blank", "height=" + screen.availheight + ",width=" + screen.availwidth + ",top=0,left=0,toolbar=no,locationbar=no,status=no");
}


//---------------------------
//搜索
//---------------------------
function search(state) {
	var url = "${pageContext.request.contextPath}/taskCommon.do?method=search&selpro=10&state=" + state + "&random=" + Math.random();
	window.open(url, target="TaskMainFrame");
}
</script>
</html>