<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/Validate_include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
 
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>会议室统计</title>
 
<Script type="text/javascript">

function ext_init(){ 
		var tbar = new Ext.Toolbar({
			renderTo: 'title',
			html:[
		        '<div style="text-align: center;margin-top:10px;margin-bottom:10px;"><font style="font-size: 15">会议室使用情况统计表</font></div>'
	        ]
        });  
        
        var tbar = new Ext.Toolbar({
			renderTo: 'divBtn',
			html:[
		          '<div style="margin-top:5px;margin-bottom:2px;">'
		        + '<table style="width:600px;">'
		        + '<tr>'
		        + '<td width="7%" style="align: right;">会议室：</td><td width="25%"><input id="roomName" name="roomName" style="vertical-align: middle;white-space:nowrap;" size="30" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" noinput="true" onClick="onPopDivClick(this);" valuemustexist="true" autoid="751" autoWidth="200" autoHeight="150"></td>'
		        + '<td width="25%"><input type="button" value="查询" style="vertical-align: middle;white-space:nowrap;width:70px;" onclick="f_search()"></td>'
		        + '<td width="25%"><span id="useTime"></td>'
		        + '</tr>'
		        + '</table>'
				+ '</div>'
	        ]
        });  
        

}
window.attachEvent('onload',ext_init);

</script>
 
 
</head>
<body>
<div id="title" ></div>
<div id="divBtn" ></div>

<form id="thisForm" name="thisForm" method="post" action="">
<div style="height:expression(document.body.clientHeight-75);" >
<mt:DataGridPrintByBean name="listRoomCount"  />
</div>

<input type="hidden" id="meetingRoomName" name="meetingRoomName" >
</form>

</body>
</html>


<script type="text/javascript">

function f_search(){
	var roomName = document.getElementById("roomName").value;
	document.getElementById("meetingRoomName").value = roomName;
	goSearch_listRoomCount(); 
	var url = "${pageContext.request.contextPath}/meetingRoom.do?method=getTime";
	var requestString = "meetingRoomName=" + roomName;
	var result = ajaxLoadPageSynch(url,requestString);
	document.getElementById("useTime").innerHTML = result;
	
}

</Script>


