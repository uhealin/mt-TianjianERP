<%@page import="java.util.UUID"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/getPy.js"  charset="GBK"></script>

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
			window.history.go(-1);
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
					window.history.go(-1);
   			   }
           }
        ]  
	});    
}
window.attachEvent('onload',ext_init);
</script>
</head>
<body style="overflow: hidden;">

<div id="divBtn"></div>

	<form name="thisForm" action="${pageContext.request.contextPath}/rank.do?method=add" class="autoHeightForm" method="post" >
		<input name="uuid" type="hidden" id="uuid" value="${rankWages.uuid}" />
		<table class="data_tb" align="center"  border="0">
			<tr >
				  <td class="data_tb_alignright" align="right">工资项名称：</td>
			      <td class="data_tb_content" align="left">
			      	<input name="wagesName" id="wagesName"  class='required' size="20" value="${rankWages.wagesName}"	 onblur="query()"/>
				  </td>
			</tr>
			<tr >
				  <td class="data_tb_alignright" align="right">工资项拼音简写：</td>
			      <td class="data_tb_content" align="left">
			      	<input name="propenty" id="propenty"  class='required' size="20" value="${rankWages.propenty}"	 />
				  </td>
			</tr>
			<tr style="display: none;">
				  <td class="data_tb_alignright" align="right" width="10%" >对应职级类型：</td>
			      <td  class="data_tb_content"  align="left">
			      <%String rankId = request.getParameter("rankId");
			      if((null!=rankId) && (!"".equals(rankId)) ){ %>
			     	<input name="rankId" id="rankId"  class='required'  readonly="readonly"  style="background-color:#FFFFB3" value="${rankWages.rankId}${rankId}"
			      	onkeydown="onKeyDownEvent();"
					onkeyup="onKeyUpEvent();"
					onclick="onPopDivClick(this);"
					noinput=true
					autoid=863
			      	 />
			      	 <%}else{ %>
			      	 <input name="rankId" id="rankId"  class='required'  style="background-color:#FFFFB3" value="${rankWages.rankId}${rankId}"
			      	onkeydown="onKeyDownEvent();"
					onkeyup="onKeyUpEvent();"
					onclick="onPopDivClick(this);"
					noinput=true
					autoid=863
			      	 />
			      	 <%} %>
				  </td>
			</tr>
			<tr  >
				  <td class="data_tb_alignright" align="right">分组：</td>
			      <td class="data_tb_content" align="left">
			      	<input name="groupFlag" id="groupFlag"  class='required' autoid=700 refer="工资分组" value="${rankWages.groupFlag}" />
				  </td>
			</tr>			
			<tr  style="display: none;">
				  <td class="data_tb_alignright" align="right">内部编号：</td>
			      <td class="data_tb_content" align="left">
			      	<input name="interiorId" id="interiorId"  class='required' value="${rankWages.interiorId }" />
				  </td>
			</tr>
			<tr >
				  <td class="data_tb_alignright" align="right">取值类型：</td>
			      <td class="data_tb_content" align="left">
			      	<select name="valueType" id="valueType"  class='required' style="width: 100px;" >
			      		<option value="缺省值">缺省值</option>
			      		<option value="数据库计算">数据库计算</option>
			      		<option value="字段计算">字段计算</option>
			      		<option value="记忆字段">记忆字段</option>
			      	</select>
				  </td>
			</tr>
			<tr >
				  <td class="data_tb_alignright" align="right">值：</td>
				  <td class="data_tb_content" align="left">
			      	<input name="getValue" id="getValue"  class='required' size="65" value="${rankWages.getValue }" />
				  </td>
			</tr>
			<tr >
				  <td class="data_tb_alignright" align="right">有权修改的环节：</td>
			      <td class="data_tb_content" align="left">
			      	<select name="updateTache" id="updateTache"  class='required' style="width: 100px;" >
			      		<option value="人事部">人事部</option>
			      		<option value="业务部">业务部</option>
			      		<option value="只读">只读</option>
			      		<option value="均可">均可</option>
			      	</select>
				  </td>
			</tr>
			<tr >
				  <td class="data_tb_alignright" align="right">排序编号 ：</td>
			      <td class="data_tb_content" align="left">
			      	<input name="orderId" id="orderId"  class='required' onkeyup="value=value.replace(/[^\d]/g,'') "  onbeforepaste="clipboardData.setData('text',clipboardData.getData('text').replace(/[^\d]/g,''))" value="${rankWages.orderId}" />
				  </td>
			</tr>
			<tr>
				<td class="data_tb_alignright" align="right" >备注：</td>
				<td class="data_tb_content" align="left">
					<textarea style="width: 400;height: 100px;overflow: visible;" name="remark" id="remark">${rankWages.remark}</textarea>
			    </td>
			</tr>
		</table>
		<div id="sbtBtn" align="center"> </div>
	</form>
</body>


<script>
 
new Validation("thisForm");
 
 if("${rankWages.updateTache }" !=""){
	 document.getElementById("updateTache").value = "${rankWages.updateTache }";
 }
 
 if("${rankWages.updateTache }" !=""){
	 document.getElementById("valueType").value = "${rankWages.valueType }";
 }
function mySubmit(){
	if (!formSubmitCheck('thisForm')) return ;
	var uuid = document.getElementById("uuid").value;
    if(uuid ==""){
       document.thisForm.action="${pageContext.request.contextPath}/rankWages.do?method=add";
    }else{
		document.thisForm.action="${pageContext.request.contextPath}/rankWages.do?method=update";
	}
		document.thisForm.submit();
} 
//自动生成拼音简写
function query(){
	var str = document.getElementById("wagesName").value.trim();
	if(str == "") return;
	var arrRslt = makePy(str);
	var loginidInput = document.getElementById("propenty");
	if(arrRslt.length >0 ) {
		loginidInput.value = arrRslt[0] ;
	}
}	 

</script>
</html>
