<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>	
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp" %>
<%@ page import="java.util.*" %>
<%@page import="com.matech.framework.pub.util.ASFuntion"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>分配${iText}</title>

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
				setManager();
			}
      	},'-',{ 
            text:'返回',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/back.gif',
            handler:function(){
				window.location="${pageContext.request.contextPath}/customer.do?method=manager";
			}
      	},'-',new Ext.Toolbar.Fill()]
	});
	
} 

function setManager(){
	thisForm.action = "${pageContext.request.contextPath}/customer.do?method=managerSave";
	thisForm.submit();
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
	width:70%;
	border:#8db2e3 1px solid; 
	BORDER-COLLAPSE: collapse; 
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
	width:15%;
	font-size: 13px;
	TEXT-ALIGN: center; 
	font-family:"宋体";
}
.data_tb_content {
	PADDING-LEFT: 2px; 
	BORDER-TOP: #8db2e3 1px solid; 
	BORDER-LEFT: #8db2e3 1px solid;
	BORDER-RIGHT: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid;  
	WORD-BREAK: break-all; 
	TEXT-ALIGN: center; 
	height:25px;
	WORD-WRAP: break-word
}

</style>
</head>
<body >
<div id="divBtn" ></div>



<fieldset  style="width:100%">
<legend>分配${iText}</legend>
<br>
<form name="thisForm" action="" method="post">

<table width="100%" height="150" border="0" cellpadding="0" cellspacing="0">

    <tr>
    <td width="25%"><div align="right">客户名称<span class="mustSpan">[*]</span>：</div></td>
    <td width="75%" >
	<input value='${manager.customerid }' type="text" name="customerid" id="customerid" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"   valuemustexist=true autoid=2/>
    </td>
  </tr>
      <tr>
    <td width="25%"><div align="right">第一${iText}<span class="mustSpan">[*]</span>：</div></td>
    <td width="75%" >
    <input value='${manager.user1 }' type="text" name="user1" id="user1" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"  multilevel=true  valuemustexist=true autoid=867/>
       
    </td>
  </tr>
      <tr>
    <td width="25%"><div align="right"><font color="red" size=3></font>第二${iText}：</div></td>
    <td width="75%" >
    <input value='${manager.user2 }' type="text" name="user2" id="user2" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"   multilevel=true valuemustexist=true autoid=867/>
    
    </td>
  </tr>
 
</table>
		<div id="mangerLog">
		<c:if test="${mapList !='[]'}">
			<div style="width: 80%;background-color: #d3e1e5;border:#8db2e3 1px solid;font-size: 20px; " align="center"><br>更新记录<br>&nbsp;</div>
			<table id="mangerLogTable" style="width: 80%;text-align: center;display: block;" class="data_tb">
				<tr>
					<td class="data_tb_alignright">第一承做人</td>
					<td class="data_tb_alignright">第二承做人</td>
					<td class="data_tb_alignright">更新时间</td>
				</tr>
				<c:forEach items="${mapList}" var="map" >
				<tr>
					<td class="data_tb_content">${map.user1name}&nbsp;</td>
					<td class="data_tb_content">${map.user2name}&nbsp;</td>
					<td class="data_tb_content">${map.createdate}&nbsp;</td>
				</tr>
				</c:forEach>
			</table>
			</c:if>
		</div>
 </form>
</body>
<script>
new Validation("thisForm");

function showLog(){
	var logTable = document.getElementById("mangerLogTable");
	if(logTable.style.display = "block"){
		logTable.style.display = "none"
	}else{
		logTable.style.display = "block"
	}
}
</script>
</html>