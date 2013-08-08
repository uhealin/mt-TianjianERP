<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">   

<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/multiSelect.js" charset="GBK"></script>

<title>部门顺序设置</title>

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
				<c:forEach items="${departmentList }" var="department">
					<option value="${department.autoId }">${department.departmentName }</option>
				</c:forEach>
			</select>
		</td>
		<td align="left">
			<img alt="置顶" class="multiImg" src="${pageContext.request.contextPath}/img/top.gif" onclick="moveTop(document.getElementById('multiSelect'));"><br/>
			<img alt="上移" class="multiImg" src="${pageContext.request.contextPath}/img/up.gif" onclick="moveUp(document.getElementById('multiSelect'));"><br/>
			<img alt="下移" class="multiImg" src="${pageContext.request.contextPath}/img/down.gif" onclick="moveDown(document.getElementById('multiSelect'));"><br/>
			<img alt="置底" class="multiImg" src="${pageContext.request.contextPath}/img/bottom.gif" onclick="moveBottom(document.getElementById('multiSelect'));"><br/>
		</td>
		<td align="left" valign="top">
		放到【<input name="departmentid" type="text" id="departmentid"  title="请输入，不得为空" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" noinput="true" onClick="onPopDivClick(this);" valuemustexist=true autoid=123 autoHeight=150 />】之后<br>
		<input type="button" value="移动" onclick="move();" class="flyBT">
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
	try{
		parent.window.goSearch_department();
	}catch(e){}
	try{
		parent.window.refreshTree();
	}catch(e){}
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
	var url = "${pageContext.request.contextPath}/department.do?method=saveOrderBy";
	var requestString = "values=" + values + "&rand=" + Math.random();
	var result = ajaxLoadPageSynch(url, requestString);	
}

//移动 
function move(){
	var obj = document.getElementById('multiSelect');
	var id = document.getElementById('departmentid').value;
	var name = document.getElementById('advice-departmentid');
	if(name == null) return;
	
	var index = 0 ;
	for(var i =obj.options.length -1 ; i >= 0; i--) {
		if(obj.options[i].value == id){
			index = i;
			break;
		}
	}
	//alert(index);
	
	var  opts = []; 
	for(var i =obj.options.length -1 ; i >= 0; i--) {
		if(obj.options[i].selected) {
			opts.push(obj.options[i]);
			obj.remove(i);
		}
	}
	
	for(var t = opts.length-1 ; t>=0 ; t--) {
		var opt = new Option(opts[t].text,opts[t].value);
		opt.selected = true;
		obj.options.add(opt, index);
	}
}

window.attachEvent('onload',extInit);
</script>
</html>