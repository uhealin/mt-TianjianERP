<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>会议信息</title>
 
</head>
<body>

<div id="divBtn"></div>

<form id="thisForm" name="thisForm" method="post" action="">
<div style="height:expression(document.body.clientHeight-23);" >
<mt:DataGridPrintByBean name="meetingOrderListNotPass"  />
</div>

<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>

<div id="search" style="display:none">
<fieldset>
    <legend style="font-size:12px;">会议</legend>
	<table border="0" cellpadding="0" cellspacing="5" width="100%" bgcolor="">
		<tr align="center">
			<td align="right" >会议名称：</td>
			<td align="left" colspan="3">
				<input type="text"
					   name="moname"
					   id="moname"
					   title="请输入有效的值" 
					   size="40"  /> 
			</td>
		</tr>
		<tr align="center">
			<td align="right" >所属机构：</td>
			<td align="left" colspan="3">
				<input type="text"
					   name="organ"
					   id="organ"
					   title="请输入有效的值" 
					   size="40"  /> 
			</td>
		</tr>
		
		<tr align="center">
			<td align="right" >所在地点：</td>
			<td align="left" colspan="3">
				<input type="text"
					   name="place"
					   id="place" 
					   size="40"  /> 
			</td>
		</tr>
		
		<tr align="center">
			<td align="right" >设备情况：</td>
			<td align="left" colspan="3">
				<input type="text"
					   name="device"
					   id="device" 
					   autoWidth="210"
	   				   onkeydown="onKeyDownEvent();" 
	   				   onkeyup="onKeyUpEvent();" 
	   				   onclick="onPopDivClick(this);" 
	   				   autoid=700
	   				   refer="deviceDescribe"
	   				   multiselect="true" 
					   size="40"  /> 
			</td>
		</tr>
		
		<tr align="center">
			<td align="right" >会议室描述：</td>
			<td align="left" colspan="3">
				<input type="text"
					   name="describes"
					   id="describes" 
					   size="50"  /> 
			</td>
		</tr>
		 
	</table>
</fieldset>
</div>

</form>

</body>
</html>


<script type="text/javascript">

function grid_dblclick(obj){
	document.getElementById("thisForm").action = "${pageContext.request.contextPath}/meetingOrder.do?method=view&isSee=Y&id="+obj.uuid; 
	document.getElementById("thisForm").submit();
}

</Script>


