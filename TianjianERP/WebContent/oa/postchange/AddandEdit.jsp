<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>工作记录登记</title>
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


Ext.onReady(function (){
	new Ext.Toolbar({
			renderTo: "divBtn",
			height:30,
			defaults: {autoHeight: true,autoWidth:true},
	       items:[{ 
	           id:'saveBtn',
	           text:'保存',
	           icon:'${pageContext.request.contextPath}/img/save.gif' ,
	           handler:function(){
	        	   if (!formSubmitCheck('thisForm')){
	        	   		return;
	        	   }else{
				   		mySubmit();
				   }
			   }
	     	 },'-',{ 
	        text:'返回',
	        icon:'${pageContext.request.contextPath}/img/back.gif', 
	        handler:function(){
				window.history.back();
			}
	  	},'->']
	});
	 
	new Ext.form.DateField({
		applyTo : 'starttime',
		width: 133,
		format: 'Y-m-d'
	});
});
</script>

</head>
<body leftmargin="0" topmargin="0">
<div id="divBtn" ></div>
<input type="hidden" id="autoid" name="autoid" value="${autoid }" />
<form name="thisForm" method="post" action="" id="thisForm">
<jodd:form	bean="pt" scope="request">
<span class="formTitle" >工作岗位变动<br/><br/> </span><br>

	<table  cellpadding="8" cellspacing="0" align="center" class="data_tb" >
		<c:if test="${all=='all' || param.all=='all'}">
			<tr>
				<td class="data_tb_alignright"  width="20%" align="right">姓名<span class="mustSpan">[*]</span>:</td>
				<td class="data_tb_content"><input name="userid" type="text" class='required' id="userid" maxlength="40" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist="true" autoid="7"></td>
			</tr>
		</c:if>
		<tr>
			<td class="data_tb_alignright"  width="20%" align="right">原部门<span class="mustSpan">[*]</span>：</td>
			<td class="data_tb_content"><input id="fdepartmentid" type="text" class="required" maxlength="18" name="fdepartmentid" title="请输入，不能为空！" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true autoid=123 noinput=true autoHeight=150 /></td>
		</tr>		
		<tr>
			<td class="data_tb_alignright"  width="20%" align="right">原岗位<span class="mustSpan">[*]</span>：</td>
			<td class="data_tb_content"><input id="formerlypost" type="text" class="required" maxlength="18" name="formerlypost" title="请输入，不能为空！" /></td>
		</tr>
		<tr>
			<td class="data_tb_alignright"  width="20%" align="right">调整后部门<span class="mustSpan">[*]</span>：</td>
			<td class="data_tb_content"><input type="text" id="adepartmentid" class="required" name="adepartmentid" title="请输入，不能为空！" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true autoid=123 noinput=true autoHeight=150 /></td>
		</tr>		
		<tr>
			<td class="data_tb_alignright"  width="20%" align="right">调整后岗位<span class="mustSpan">[*]</span>：</td>
			<td class="data_tb_content"><input type="text" id="adjustpost" class="required" name="adjustpost" title="请输入，不能为空！" /></td>
		</tr>
		<tr>
			<td class="data_tb_alignright"  width="20%" align="right">调整时间：</td>
			<td class="data_tb_content"><input name="starttime" type="text" id="starttime" class="validate-date-cn" title="请输入日期！" >
			</td>
		</tr>
		<!-- 
  <tr>
   <td align="right" width="120">结束时间：</td>
    <td><input name="endtime" type="text" id="endtime"   class="validate-date-cn"  title="请输入日期！" showcalendar="true">
        </td>
  </tr>
   -->
	</table>

</jodd:form>


<!--<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td height="22" colspan="3">&nbsp;</td>
	</tr>
	<tr>
		<td width="37%" align="right"><input type="submit" name="next"
			value="确  定" class="flyBT" onclick="mySubmit();";></td>
		<td width="8%">&nbsp;</td>
		<td width="55%"><input type="button" name="back" value="返  回"
			class="flyBT" onClick="window.history.back();"></td>
	</tr>
</table>

--><input name="AS_dog" type="hidden" id="AS_dog" value=""></form>

<script type="text/javascript">
new Validation('thisForm');

function mySubmit() {
	if(document.getElementById("autoid").value!="") {
	
		thisForm.action="/AuditSystem/postchange.do?method=update&autoid="+document.getElementById("autoid").value+"&all=${all}";
	} else {
	
		thisForm.action="/AuditSystem/postchange.do?method=add&all=${param.all}";
	}
	document.thisForm.submit();
}

</script>

</body>
</html>
