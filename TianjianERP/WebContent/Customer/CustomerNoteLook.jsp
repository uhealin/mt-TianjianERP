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
           items:[ { 
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

<style type="text/css">

.before{
	border: 0px;
	background-color: #FFFFFF !important;
}

.data_tb {
	background-color: #ffffff;
	text-align:center;
	margin:0 0px;
	width:60%;
	border:#8db2e3 1px solid; 
	BORDER-COLLAPSE: collapse; 
	margin-top: 20px;
}
.data_tb_alignright {	
	BACKGROUND: #e4f4fe; 
	white-space:nowrap;
	padding:5px;
	border-top: #8db2e3 1px solid;
	border-left: #8db2e3 1px solid;
	BORDER-RIGHT: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid; 
	height:30px;
	background-color: #d3e1f1;
	font-size: 13px;
	font-family:"宋体";
}
.data_tb_content {
	PADDING-LEFT: 2px; 
	BORDER-TOP: #8db2e3 1px solid; 
	BORDER-LEFT: #8db2e3 1px solid;
	BORDER-RIGHT: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid;  
	WORD-BREAK: break-all; 
	TEXT-ALIGN: left; 
	WORD-WRAP: break-word
}

</style>

</head>
<body >
<div id="divBtn" ></div>

<div style="height:expression(document.body.clientHeight-30);overflow: auto" >
<form name="noteForm" method="post" action="" id="noteForm" >

<span class="formTitle" ><br>客户服务记录信息</span>
<table width="100%" border="0" cellpadding="8" cellspacing="0" class="data_tb" align="center">
	<tr>
		<td nowrap="nowrap" align="right"  width="25%" class="data_tb_alignright">
			客户名称：
		</td>
		<td class="data_tb_content">
			<input type="text" name="customerId" id="customerId" class="required" title="请选择走访客户" value="${customerId}" 
					onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" refreshtarget="linkMan" onclick="onPopDivClick(this);" onchange="setCustomerLinkMan();" valuemustexist=true autoid=2 />
		</td>
	</tr>
	<tr>
		<td nowrap="nowrap" align="right" class="data_tb_alignright">
			客户联系人：
		</td> 
		<td class="data_tb_content">
			<input type="text" name=linkMan id="linkMan" class="required" refer="customerId" title="请输入走访客户联系人" value="${linkMan}" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"  autoid=851 >
		</td>
	</tr>
	<tr>
		<td align="right" class="data_tb_alignright">
			服务类型：
		</td>
		<td class="data_tb_content">
			 ${type0} 
		</td>
	</tr>	
	<tr>
		<td align="right" class="data_tb_alignright">
			服务时间：
		</td >
		<td class="data_tb_content">
			  ${noteTime}
		</td>
	</tr>

	<tr>
		<td align="right" class="data_tb_alignright">
			服务主题：
		</td>
		<td class="data_tb_content">
			  ${title}
		</td>
	</tr>
	<tr>
		<td align="right" class="data_tb_alignright">
			服务内容：
		</td>
		<td class="data_tb_content">
			 <pre>${content}</pre>
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
		<td align="right" class="data_tb_alignright">
			记录人：
		</td>
		<td class="data_tb_content">
			 ${userName} 
		</td>
	</tr>
</table>
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

setObjDisabled("customerId");
setObjDisabled("linkMan");
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