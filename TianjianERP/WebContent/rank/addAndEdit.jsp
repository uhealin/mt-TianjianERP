<%@page import="java.util.UUID"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>职级管理</title>
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
	width:20%;
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
<script type="text/javascript">

function ext_init(){
	new Ext.Toolbar({
		renderTo: "divBtn",
		height:30,
		defaults: {autoHeight: true,autoWidth:true},
       items:[{ 
           id:'saveBtn',
           text:'保存',
           icon:'${pageContext.request.contextPath}/img/save.gif' ,
           handler:function(){
		   		mySubmit();
		   }
     	 },'-',{ 
        text:'返回',
        icon:'${pageContext.request.contextPath}/img/back.gif', 
        handler:function(){
			window.history.back();
		}
  	},'->']
});
	
	new ExtButtonPanel({
		desc:'',
		renderTo:'sbtBtn',
		items:[
		{
            text: '保存',
            icon:'${pageContext.request.contextPath}/img/receive.png' ,
            scale: 'large',
            handler:function(){
            	mySubmit();
   			}
           },{
            text: '返回',
            icon:'${pageContext.request.contextPath}/img/back_32.png' ,
            scale: 'large',
               handler:function(){
            	  //closeTab(parent.tab);
					window.history.back();
   			   }
           }
        ]  
	});    
}
window.attachEvent('onload',ext_init);
</script>
</head>
<body>

<div id="divBtn"></div>

	<form name="thisForm" action="${pageContext.request.contextPath}/rank.do?method=add" class="autoHeightForm" method="post" >
		<input name="autoId" type="hidden" id="autoId" value="${rank.autoId}" />
		<table class="data_tb" align="center"  border="0">
			<tr >
				  <td class="data_tb_alignright" align="right">职级名称：</td>
			      <td class="data_tb_content" align="left">
			      	<input name="name" id="name"  class='required' value="${rank.name }" size="50"/>
				  </td>
			</tr>
			<tr >
				  <td class="data_tb_alignright" align="right" width="10%" >职级分类：</td>
			      <td  class="data_tb_content"  align="left">
			      	<select name="ctype" id="ctype" class='required' style="width: 140">
			      	<option value="试用职级">试用职级</option>
			      	<option value="正式职级">正式职级</option>
			      	</select>
				  </td>
			</tr>
			<tr >
				  <td class="data_tb_alignright" align="right">所属组：</td>
			      <td class="data_tb_content" align="left">
			      	<input name="group" id="group"  class='required' value="${rank.group }"
			      	onkeydown="onKeyDownEvent();"
					onkeyup="onKeyUpEvent();"
					onclick="onPopDivClick(this);"
					autoid=862
			      	 />
				  </td>
			</tr>
			<tr>
				  <td class="data_tb_alignright" align="right">工资格式类型：</td>
			      <td class="data_tb_content" align="left">
			      	<input name="formatSalary" id="formatSalary"  class='required' value="" />
				  </td>
			</tr>
			<tr style="display:none;">
				  <td class="data_tb_alignright" align="right">基本工资：</td>
			      <td class="data_tb_content" align="left">
			      	<input name="baseSalary" id="baseSalary"  class='required validate-currency' value="${rank.baseSalary }" />
				  </td>
			</tr>
			<tr style="display:none;" >
				  <td class="data_tb_alignright" align="right">工时工资：</td>
			      <td class="data_tb_content" align="left">
			      	<input name="timeSalary" id="timeSalary"  class='required validate-currency' value="${rank.timeSalary }" />
			      	如果没有，请填“0”。
				  </td>
			</tr>
			
			<tr >
				  <td class="data_tb_alignright" align="right">排序号：</td>
			      <td class="data_tb_content" align="left">
			      	<input name="sequenceNumber" id="sequenceNumber"  class='required' value="${rank.sequenceNumber }" />
				  </td>
			</tr>
			<tr>
				<td class="data_tb_alignright" align="right" >备注：</td>
				<td class="data_tb_content" align="left">
					<textarea style="width: 400;height: 100px;overflow: visible;" name="explain" id="explain">${rank.explain }</textarea>
			    </td>
			</tr>
		</table>
		<div id="sbtBtn" align="center"> </div>
	</form>
</body>


<script>

if("${rank.ctype}" !="")
{
	document.getElementById("ctype").value="${rank.ctype}";
}
new Validation("thisForm");
 
function mySubmit(){
	if (!formSubmitCheck('thisForm')) return ;
	var autoId = document.getElementById("autoId").value;
    if(autoId ==""){
       document.thisForm.action="${pageContext.request.contextPath}/rank.do?method=add";
    }else{
		document.thisForm.action="${pageContext.request.contextPath}/rank.do?method=update";
	}
		document.thisForm.submit();
}
</script>
</html>
