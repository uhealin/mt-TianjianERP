<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="../hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/Validate_include.jsp"%>
<html>
<head>
<title>新增客户服务记录</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta HTTP-EQUIV="imagetoolbar" content="no">
<script type="text/javascript">  

function ext_init(){
	
	var tbar_customer = new Ext.Toolbar({
   		renderTo: "divBtn",
   		height:30,
   		defaults: {autoHeight: true,autoWidth:true},
           items:[{ 
            text:'保存',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/save.gif',
            handler:function(){
				check(1);
			}
      	},'-',{ 
            text:'保存并发起商机登记',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/save.gif',
            handler:function(){
				check(2);
			}
      	},'-',{ 
            text:'返回',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/back.gif',
            handler:function(){
				goClose();
			}
      	},'-',new Ext.Toolbar.Fill()]
	});
	
} 



window.attachEvent('onload',ext_init);
</script>

</head>
<body >
<div id="divBtn" ></div>

<div style="height:expression(document.body.clientHeight-30);overflow: auto" >
<form name="noteForm" method="post" action="" id="noteForm" >

<fieldset style="width:100%"><legend>客户服务记录信息</legend> <br>

<table width="100%" border="0" cellpadding="8" cellspacing="0">
	<tr>
		<td nowrap="nowrap" align="right"  width="25%">
			客户名称：
		</td>
		<td>
			<input type="text" name="customerId" id="customerId" class="required" title="请选择走访客户" value="${customerId}"
					onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" refreshtarget="linkMan" onclick="onPopDivClick(this);" onchange="setCustomerLinkMan();" valuemustexist=true autoid=601 />
		</td>
	</tr>
	<tr>
		<td nowrap="nowrap" align="right">
			客户联系人：
		</td> 
		<td>
			<input type="text" name=linkMan id="linkMan" class="required" refer="customerId" title="请输入走访客户联系人" value="${linkMan}" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"  autoid=851 >
			<a href="#" onClick="addLinkman()">【新增联系人】</a>
		</td>
	</tr>
	<tr>
		<td align="right">
			服务类型：
		</td>
		<td>
			<input type="text" name="type0" id="type0" value="${type0}" noinput=true onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true autoid=700 refer="服务类型" >
		</td>
	</tr>	
	<tr>
		<td align="right">
			服务时间：
		</td>
		<td>
			<input type="text" name="noteTime" id="noteTime" title="请输入走访时间" class="required validate-date-cn" showcalendar="true" readonly="readonly" value="${noteTime}">
		</td>
	</tr>

	<tr>
		<td align="right">
			服务主题：
		</td>
		<td>
			<input type="text" name="title" id="title" size="78" title="请输入走访主题" maxlength="200" class="required" value="${title}">
		</td>
	</tr>
	<tr>
		<td align="right">
			服务内容：
		</td>
		<td>
			<textarea rows="5" cols="50" class="required" title="请输入走访记录" name="content" id="content" >${content}</textarea>
		</td>
	</tr>
<!-- 
	<tr id="loadfile" style="display: ">
		<td align="right">
			附件：
		</td>
		<td>
			<input type="file" name="file" size="78" id="file" onchange="setFileName();">
		</td>
	</tr>

	<tr>
		<td align="right">
			附件名：
		</td>
		<td>
			<input type="text" name="fileName" id="fileName" value="${fileName}" size="30">
		</td>
	</tr>
 -->
	<tr>
		<td align="right">
			记录人：
		</td>
		<td>
			<input type="text" name="userName" id="userName" value="${userName}" readonly="readonly" style="border: 0px;">
		</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td>
			<input type="hidden" name="autoId" id="autoId" value="${autoId}" class="flyBT" />
		</td>
	</tr>
</table>
</fieldset>
</form>
</div>
</body>
</html>

<script language="javascript">
Validation.prototype.check = function(){
		var t;
		if(this.options.stopOnFirst) {
			t=Form.getElements(this.form).all(Validation.validate);
		} else {
			t=Form.getElements(this.form).collect(Validation.validate).all();
		}
		return t;
}

var vd = new Validation('noteForm');

//------------------
// 获取文件的扩展名
//------------------
function getfilenameext(filename){
	return filename.replace(/(.*\.)/g,'').toLowerCase();
}

//新增联系人
function addLinkman(){
	var customerId = document.getElementById("customerId").value;	
	if(customerId ==""){
		alert("请先选择客户名称！");
		return ;
	}else{
		parent.openTab("customerLinkmanAdd","新增联系人","Customer/manageradd.jsp?departid="+customerId);
	}
}

//--------------------
// 去空格函数
//--------------------
function jsTrim(str) {
    return str.replace(/^\s* | \s*$/g,"");
}

//--------------------
// 设置文件名
//--------------------
function setFileName() {
	try {
		var filePath = document.getElementById("file").value;
		var fileName = filePath.substring(filePath.lastIndexOf("\\") + 1);
		//var fileNameExt = "." + getfilenameext(fileName);
		//fileName = fileName.substring(0,fileName.lastIndexOf("."));
		//alert(fileName);
		//alert(fileNameExt);

		document.getElementById("fileName").value = jsTrim(fileName);
	} catch(e) {

	}
}

//------------------
// 响应提交前的检查
//------------------
function check(opt){
	var autoId = document.getElementById("autoId").value;

	if(autoId != "") {
		document.noteForm.action = "${pageContext.request.contextPath}/customerNote.do?method=update&autoId=" + autoId + "&opt="+opt;
	} else {
		document.noteForm.action = "${pageContext.request.contextPath}/customerNote.do?method=save&opt="+opt;
		document.noteForm.encoding = "multipart/form-data";
	}

	if(!vd.check()) {
		return false;
	}

	document.noteForm.submit();
}

//------------------------------------------
// 设置客户联系人
//------------------------------------------
function setCustomerLinkMan() {
	try {
		var customerId = document.getElementById("customerId").value;
		var url = "${pageContext.request.contextPath}/customer.do?method=getCustomerInfo&customerId=" + customerId;

		var result = ajaxLoadPageSynch(url,null);

		if(result.indexOf("error") < 0) {
			document.getElementById("linkMan").value = result;
		}
	} catch(e) {

	}
}

//如果不是框架页
if (top.location == self.location) {
	document.all.back.value = "关  闭";
}

//------------------------------------------
// 如果该页面在框架中，执行"后退"动作,否则关闭页面
//------------------------------------------
function goClose() {
	if (top.location == self.location) {
		window.close();
	} else {
		window.history.back();
	}
}

try{
	var autoId = document.getElementById("autoId").value;
	
	if(autoId!=""){
		//document.getElementById("loadfile").style.display = "none";
	}
	
}catch(e){

}
</script>