<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/multiSelect.js" charset="GBK"></script>

<title>用户排序设置</title>

<style type="text/css">
.multiImg {
	margin-top: 5px;
	margin-left: 10px;
	cursor: hand;
}

select {
	font-size: 12px;
	color: blue;
}
</style>
</head>
<body>
<div id="divBtn"></div>

<form name="thisForm" method="post" action="" id="thisForm" class="autoHeightForm" style="height:expression(document.body.clientHeight-57);" >
<table align="center">
	<tr>
		<td align="right">
			<select multiple name="multiSelect" id="multiSelect" size="20" style='width:200;' >
				<c:forEach items="${userList }" var="user">
					<option value="${user.id }">${user.name }</option>
				</c:forEach>
			</select>
		</td>
		<td align="left">
			<img alt="置顶" class="multiImg" src="${pageContext.request.contextPath}/img/top.gif" onclick="moveTop(document.getElementById('multiSelect'));"><br/>
			<img alt="上移" class="multiImg" src="${pageContext.request.contextPath}/img/up.gif" onclick="moveUp(document.getElementById('multiSelect'));"><br/>
			<img alt="下移" class="multiImg" src="${pageContext.request.contextPath}/img/down.gif" onclick="moveDown(document.getElementById('multiSelect'));"><br/>
			<img alt="置底" class="multiImg" src="${pageContext.request.contextPath}/img/bottom.gif" onclick="moveBottom(document.getElementById('multiSelect'));"><br/>
		</td>
	</tr>
</table>
</form>
</body>

<script type="text/javascript">
function extInit() {
	var tbar = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		defaults: {autoHeight: true,autoWidth:true},
            items:[{
	            text:'保存',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/save.gif',
	            handler:function(){
					save();
				}  
       		},'-',{
	            text:'关闭',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function(){
					close();
				}
       		}]
     });
}

var flag = "${param.flag}";

//关闭
function close() {
	if(flag == "window") {
		closeWindow();
	}
}

//关闭窗口
function closeWindow() {
	parent.window.setOrderByWin.hide();
}

//保存排序后结果
function save() {
	var multiSelect = document.getElementById("multiSelect");
	var values = "-1";
	//var text = "-1";
	
	for(var i=0; i < multiSelect.length; i++) {
		values += "," + multiSelect.options[i].value;
		//text += "," + multiSelect.options[i].text;
	}

	saveOrderBy(values);

	close();
}

//保存排序后结果
function saveOrderBy(values) {
	var url = "${pageContext.request.contextPath}/user.do?method=saveOrderBy";
	var requestString = "values=" + values + "&rand=" + Math.random();
	var result = ajaxLoadPageSynch(url, requestString);	
}

window.attachEvent('onload',extInit);
</script>
</html>