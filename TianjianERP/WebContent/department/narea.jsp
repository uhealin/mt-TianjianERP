<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8">
<title>区域信息</title>
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

//修改区域名称
function getName(){
	var name1 = document.getElementById("name1");
	try{
		var name = document.getElementById("name");
		if(name1.value == "0"){
			//新增区域，显示新增的区域名
			name.style.display = "";
			name.value = "";
		}else{
			name.style.display = "none";
			name.value = obj.value;
		}
	}catch(e){}

}

</script>
</head>
<body>
<form name="thisForm" method="post" action="" >
<span class="formtitle" >
<c:if test="${area.autoid == ''}">新增区域</c:if>
<c:if test="${area.autoid != ''}">${area.name }</c:if>
<br/><br/></span>

<input type="hidden" id="tableid" name="tableid" value="k_area" >
<input type="hidden" id="issubject" name="issubject" value="${issubject }">

<input type="hidden" id="autoid" name="autoid" value="${area.autoid }" >
<input type="hidden" id="property" name="property" value="${area.property }" >

<table border="0" cellpadding="8" cellspacing="0" align="center" class="data_tb" >
<tr>
  <td class="data_tb_alignright" align="center" colspan="4">基本情况</td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="20%" align="right">所属单位<span class="mustSpan">[*]</span>：</td>
  <td  class="data_tb_content" ><input title="所属单位不能为空" value="${area.organid }" class="required" name="organid" type="text" id="organid" size=60 refreshtarget="departments" noinput=true  onkeydown="onKeyDownEvent();"  maxlength="10" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true autoid=732></td>
</tr>

<c:choose>
<c:when test="${issubject == '0'}" >

<tr>
  <td class="data_tb_alignright"  width="20%" align="right">区域名称<span class="mustSpan">[*]</span>：</td>
  <td  class="data_tb_content" >
  	<input onchange="getName();" title="区域名称不能为空"  value="${area.name }" useAdvice=true class="required" name="name1" type="text" id="name1" noinput=true refer=organid refer1=1 onkeydown="onKeyDownEvent();"  maxlength="10" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true autoid=731>&nbsp;&nbsp;
  	<input style="display: none;" title="区域名称不能为空"  value="${area.name }" class="required" name="name" type="text" id="name" size="30">
  </td>
</tr>
</tbody>
	
</c:when>
<c:otherwise>
<tr>
  <td class="data_tb_alignright"  width="20%" align="right">区域名称<span class="mustSpan">[*]</span>：</td>
  <td  class="data_tb_content" ><input title="区域名称不能为空"  value="${area.name }" class="required" name="name" type="text" id="name" size=60 ></td>
</tr>	
</c:otherwise>
</c:choose>

<tr>
  <td class="data_tb_alignright"   align="right">区域负责人：</td>
  <td  class="data_tb_content" ><input value="${area.managers }"  multiselect="true" size=60 name="managers" type="text" id="managers"  noinput=true  onkeydown="onKeyDownEvent();"  maxlength="10" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true autoid=616></td>
</tr>
<tr>
  <td class="data_tb_alignright" align="center" colspan="4">补充说明</td>
</tr>
<tr>
  <td class="data_tb_alignright"   align="right">还没有设置区域的部门：</td>
  <td  class="data_tb_content" ><input value="${departments }" size=60 name="departments" type="text" id="departments" multiselect="true" refer=organid noinput=true  onkeydown="onKeyDownEvent();"  maxlength="10" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true autoid=733></td>
</tr>
</table>

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
	document.getElementById("organid").className = "required";
	document.getElementById("advice-organid").className = "required";
	document.getElementById("name").className = "required";
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