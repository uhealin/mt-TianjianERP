<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
 
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>我的会议查询</title>
 
<Script type="text/javascript">

function ext_init(){ 
		var tbar = new Ext.Toolbar({
		renderTo: 'divBtn',
		items:[
			{
				text:'会议答复',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/add.gif',
				handler:function () {
					goAnswer();
				}
			},'->'
        ]
        });  

}
window.attachEvent('onload',ext_init);

</script>
 
 
</head>
<body>

<div id="divBtn"></div>

<form id="thisForm" name="thisForm" method="post" action="">
<div style="height:expression(document.body.clientHeight-23);" >
<mt:DataGridPrintByBean name="myMeetingOrderList"  />
</div>

</form>

</body>
</html>

<script type="text/javascript">

function grid_dblclick(obj){
	
}


// 答复
function goAnswer(){
	var id = document.getElementById("chooseValue_myMeetingOrderList").value; 
	if(id==""){
		alert("请选择一项！");
	}else{
		document.getElementById("thisForm").action = "${pageContext.request.contextPath}/meetingOrder.do?method=goOpt&id="+id;
		document.getElementById("thisForm").submit();
	}
}

</Script>
