<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8">
<title>单位信息</title>
<style>

.before{
	border: 0px;
	background-color: #ffffff !important;
}

.data_tb {
	background-color: #ffffff;
	text-align:center;
	margin:0 0px;
	width:80%;
	border:#8db2e3 1px solid; 
	border-collapse: collapse; 
}
.data_tb_alignright {	
	background: #e4f4fe; 
	white-space:nowrap;
	padding:5px;
	border-top: #8db2e3 1px solid;
	border-left: #8db2e3 1px solid;
	border-right: #8db2e3 1px solid;
	border-bottom: #8db2e3 1px solid; 
	height:30px;
	background-color: #d3e1f1;
	font-size: 13px;
	font-family:"宋体";
}
.data_tb_content {
	padding-left: 2px; 
	border-top: #8db2e3 1px solid; 
	border-left: #8db2e3 1px solid;
	border-right: #8db2e3 1px solid;
	border-bottom: #8db2e3 1px solid;  
	word-break: break-all; 
	text-align: left; 
	word-wrap: break-word
}

</style>
<script type="text/javascript">
//保存
function save(){
	if (!formSubmitCheck('thisForm')) return;
	thisForm.action = "${pageContext.request.contextPath}/department.do?method=save";
	thisForm.target = "";
	thisForm.submit();
}
</script>
</head>
<body>
<form name="thisForm" method="post" action="" >
<div style="height: expression(document.body.clientHeight-27);">
<span class="formtitle" >
<c:if test="${depart.autoid == ''}">新增下级部门</c:if>
<c:if test="${depart.autoid != ''}">${depart.departname }</c:if>
<br/><br/></span>

<input type="hidden" id="tableid" name="tableid" value="k_department" >
<input type="hidden" id="issubject" name="issubject" value="${issubject }">

<input type="hidden" id="autoid1" name="autoid1" value="${autoid1 }">

<input type="hidden" id="popedom" name="popedom" value="${depart.popedom }">
<input type="hidden" id="autoid" name="autoid" value="${depart.autoid }">
<input type="hidden" id="property" name="property" value="${depart.property }">
<input type="hidden" id="level0" name="level0" value="${depart.level0 }">
<input type="hidden" id="fullpath" name="fullpath" value="${depart.fullpath }">
<input type="hidden" id="rand0" name="rand0" value="${depart.rand0 }">
<input type="hidden" id="isleaf" name="isleaf" value="${depart.isleaf }">

<table border="0" cellpadding="8" cellspacing="0" align="center" class="data_tb" >
<tr>
  <td class="data_tb_alignright" align="center" colspan="4">基本情况</td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="20%" align="right" >部门名称<span class="mustSpan">[*]</span>：</td>
  <td  class="data_tb_content" colspan="3"><input title="部门名称不能为空" value="${depart.departname }" class="required" name="departname" type="text" id="departname" size=60 ></td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="20%" align="right">部门缩写：</td>
  <td  class="data_tb_content" colspan="3"><input title="部门缩写不能为空" value="${depart.enname }"  name="enname" type="text" id="enname" size=60 ></td>
</tr>
<tr>
  <td class="data_tb_alignright"   align="right">上级单位|部门<span class="mustSpan">[*]</span>：</td>
  <td  class="data_tb_content" colspan="3"><input title="上级单位|部门不能为空" value="${depart.parentid }" class="required" size=60 name="parentid" type="text" id="parentid" refreshtarget="areaid"  multilevel=true  noinput=true  refer=autoid1 onkeydown="onKeyDownEvent();"  maxlength="10" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true autoid=730></td>
</tr>
<tr>
  <td class="data_tb_alignright"   align="right">类同部门：</td>
  <td  class="data_tb_content" colspan="3"><input value="${depart.projectpopedom }" multiselect="true"  size=60 name="projectpopedom" type="text" id="projectpopedom"  noinput=true onkeydown="onKeyDownEvent();"  maxlength="10" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true autoid=123></td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="20%" align="right">所属区域：</td>
  <td  class="data_tb_content" width="30%"><input value="${depart.areaid }" name="areaid" type="text" id="areaid"  noinput=true  refer=parentid onkeydown="onKeyDownEvent();"  maxlength="10" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true autoid=731></td>
  <td class="data_tb_alignright" width="20%"  align="right">部门类型：</td>
  <td  class="data_tb_content"width="30%"><input value="${depart.ltype }" name="ltype" type="text" id="ltype"  noinput=true  onkeydown="onKeyDownEvent();"  maxlength="10" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true autoid=700 refer="部门类型" ></td>
</tr>
<tr>
  <td class="data_tb_alignright" align="center" colspan="4">权限设定</td>
</tr>
<tr>
  <td class="data_tb_alignright"   align="right">项目信息缺省审批人：</td>
  <td  class="data_tb_content" ><input value="${depart.projectapprove }" name="projectapprove" type="text" id="projectapprove"  noinput=true  refer=parentid onkeydown="onKeyDownEvent();"  maxlength="10" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true autoid=670></td>
  <td class="data_tb_alignright"   align="right">预算工时缺省审批人：</td>
  <td  class="data_tb_content" ><input value="${depart.schedulingapprove }" name="schedulingapprove" type="text" id="schedulingapprove"  noinput=true  onkeydown="onKeyDownEvent();"  maxlength="10" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true autoid=670  ></td>
</tr>
<tr>
  <td class="data_tb_alignright"   align="right">预算费用缺省审批人：</td>
  <td  class="data_tb_content" ><input value="${depart.costapprove }" name="costapprove" type="text" id="costapprove"  noinput=true  refer=parentid onkeydown="onKeyDownEvent();"  maxlength="10" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true autoid=670></td>
  <td class="data_tb_alignright"   align="right">实际费用缺省审批人：</td>
  <td  class="data_tb_content" ><input value="${depart.realityapprove }" name="realityapprove" type="text" id="realityapprove"  noinput=true  onkeydown="onKeyDownEvent();"  maxlength="10" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true autoid=670 ></td>
</tr>
<!--
<tr>
  <td class="data_tb_alignright" align="center" colspan="4">功能设置</td>
</tr>
<tr>
  <td class="data_tb_alignright"   align="right">部门默认模板：</td>
  <td  class="data_tb_content" colspan="3" ><input value="${depart.typeid }" size=60 name="typeid" type="text" id="typeid"  noinput=true onkeydown="onKeyDownEvent();"  maxlength="10" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true autoid=65></td>
</tr>
<tr>
  <td class="data_tb_alignright"   align="right">部门服务器地址：</td>
  <td  class="data_tb_content" colspan="3">
  	<input value="${depart.url }" size=60 name="url" type="text" id="url" value=""  class="ip-wheninputed"  >
  	<br>如果为空则是本地部门，如果有ip则为外网部（用于集团监控）。
	<br>[例子]内容可以是ip:"202.96.128.68"，或者域名"www.google.com"。
  </td>
</tr>
-->
<tr>
  <td class="data_tb_alignright" align="center" colspan="4">补充说明</td>
</tr>
<tr>
  <td class="data_tb_alignright"   align="right">通信地址：</td>
  <td  class="data_tb_content" colspan="3"><input value="${depart.address }" size=60 name="address" type="text" id="address" ></td>
</tr>
<tr>
  <td class="data_tb_alignright"   align="right">邮编：</td>
  <td  class="data_tb_content" colspan="3"><input value="${depart.postalcode }" size=30 name="postalcode" type="text" id="postalcode" ></td>
</tr>
</table>
</div>
</form>
</body>
</html>
<script type="text/javascript">
var ifAdd = "${param.ifAdd}";
function update(){
	var form_obj = document.all; 
	//form的值
	for (i=0;i<form_obj.length ;i++ ) {
		e=form_obj[i];
		
		if (e.tagName=='INPUT' || e.tagName=='TEXTAREA') {
			e.readOnly = false ;
			e.className = "";
			if(e.type == 'checkbox'){
				e.disabled = false ;
			}
		}
		if(e.tagName=='SELECT'){
			e.className = "";
			e.disabled= false;
		}
		//alert(e.tagName);
		if(e.tagName == 'A'){
			e.style.display = "";
		}
		if(e.tagName == "IMG"){
			e.style.display = "";
		}
		
	}
	
	//恢复className
	//document.getElementById("enname").className = "required";
	document.getElementById("departname").className = "required";
	document.getElementById("parentid").className = "required";
	document.getElementById("advice-parentid").className = "required";
}	


function view(){
	var form_obj = document.all; 
	//form的值
	for (i=0,j=0;i<form_obj.length ;i++ ) {
		e=form_obj[i];
		
		if (e.tagName=='INPUT' || e.tagName=='TEXTAREA') {
			e.readOnly = true ;
			e.className = "before";
			if(e.type == 'checkbox'){
				e.disabled = true ;
			}
		}
		if(e.tagName=='SELECT'){
			e.disabled= true;
			e.className = "before";
		}
		if(e.tagName == 'A'){
			e.style.display = "none";
		}
		if(e.tagName == "IMG"){
			e.style.display = "none";
		}
		
	}
}


Ext.onReady(function(){
	if(ifAdd != "1"){
		view();
		var btn = parent.Ext.getCmp("btn-divTab");
		btn.setText("修改");
		btn.setIcon("${pageContext.request.contextPath}/img/edit.gif");
	}else{
		var btn = parent.Ext.getCmp("btn-divTab");
		btn.setText("保存");
		btn.setIcon("${pageContext.request.contextPath}/img/save.gif");
	}
});

</script>