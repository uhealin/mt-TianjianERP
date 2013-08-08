<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>社会职务</title>
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
	        	   if (!formSubmitCheck('thisForm'))return;
				   save();
			   }
	     	 },'-',{ 
	        text:'返回',
	        icon:'${pageContext.request.contextPath}/img/back.gif', 
	        handler:function(){
				f_back();
			}
	  	},'->']
	});
	 
});
</script>

</head>
<body>
<div id="divBtn" ></div>
<span class="formTitle" >社会职务<br/><br/> </span><br>
<jodd:form bean="society" scope="request">
	<form name="thisForm" action="" method="post" >
	<input name="autoid" type="hidden" id="autoid" value="${autoid}" />
	<table  cellpadding="8" cellspacing="0" align="center" class="data_tb" >
		<tr <c:if test="${all!='all' || param.all!='all'}">style="display: none;" </c:if> >
			<td class="data_tb_alignright"  width="20%" align="right">姓名<span class="mustSpan">[*]</span>:</td>
			<td class="data_tb_content"><input  name="userid" type="text" class='required' id="userid" maxlength="40" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist="true" autoid="7"></td>
		</tr>
		
		<tr>
			<td class="data_tb_alignright"  width="20%" align="right">社会职务<span class="mustSpan">[*]</span>：</td>
			<td class="data_tb_content">
			<input onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" showhint=false onClick="onPopDivClick(this);" valuemustexist=true autoid=4587  noinput=true  title='客户来源' class="required" type="text" id="postname" name="postname"  value="">
			
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright"  width="20%" align="right">建议或提案年度<span class="mustSpan">[*]</span>：</td>
			<td class="data_tb_content"><input  name="pyear" type="text" class='required' id="pyear" maxlength="50" title="请正确输入关系">
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright"  width="20%" align="right">建议或提案数量<span class="mustSpan">[*]</span>：</td>
			<td class="data_tb_content"><input name="pcount" type="text" class="required" id="pcount" maxlength="50" title="请输入正确的联系电话"></td>
		</tr>
		<tr>
			<td class="data_tb_alignright"  width="20%" align="right">备注：</td>
			<td class="data_tb_content"><textarea rows="5" cols="50" id="memo" name="memo"></textarea></td>
		</tr>

	</table>
	</form>
</jodd:form>

</body>
<script>
new Validation("thisForm");
 
function save(){
   document.getElementById("postname").value=document.getElementById("advice-postname").value;
    document.thisForm.action="${pageContext.request.contextPath}/worknote.do?method=societySave";
	document.thisForm.submit();
}

// 返回
function f_back(){
	history.back()
}

</script>
</html>
