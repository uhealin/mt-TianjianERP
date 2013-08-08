<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/Validate_include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
 

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>会议室信息</title>
 
<Script type="text/javascript">

function ext_init(){ 
		var tbar = new Ext.Toolbar({
		renderTo: 'divBtn',
		items:[
			{
				text:'新增',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/add.gif',
				handler:function () {
					goAdd();
				}
			},'-',
			{
	           text:'修改',
	           cls:'x-btn-text-icon',
	           icon:'${pageContext.request.contextPath}/img/edit.gif',
	          	handler:function(){
					goEdit();
				}
	        },'-',
			{
	           text:'删除',
	           cls:'x-btn-text-icon',
	           icon:'${pageContext.request.contextPath}/img/delete.gif',
	          	handler:function(){
					goDelete();
				}
	        },'-',
	        {
				text:'查询',
				cls:'x-btn-text-icon',
				icon:'${pageContext.request.contextPath}/img/query.gif',
				handler:queryWinFun 
			},'-',
	        {
	            text:'关闭',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function () {
	           		closeTab(parent.tab);
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
<mt:DataGridPrintByBean name="MeetingRoomList"  />
</div>

<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>

<div id="search" style="display:none">
<fieldset>
    <legend style="font-size:12px;">会议室</legend>
	<table border="0" cellpadding="0" cellspacing="5" width="100%" bgcolor="">
		<tr align="center">
			<td align="right" >会议室名称：</td>
			<td align="left" colspan="3">
				<input type="text"
					   name="name"
					   id="name"
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
					   size="40" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" onClick="onPopDivClick(this);" autoid=4583  /> 
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
	   				   autoid=10022
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


function goSearch(){
	queryWin.hide();
    goSearch_MeetingRoomList(); 
} 

// 清空
function goClear(){
	document.getElementById("name").value="";
	document.getElementById("organ").value="";
	document.getElementById("place").value="";
	document.getElementById("device").value="";
	document.getElementById("describes").value="";
	
	queryWin.hide();
    goSearch_MeetingRoomList(); 
    
}

// 添加
function goAdd(){
	document.getElementById("thisForm").action = "${pageContext.request.contextPath}/meetingRoom.do?method=go&opt=add"; 
	document.getElementById("thisForm").submit();
}

//编辑
function goEdit(){
	var id = document.getElementById("chooseValue_MeetingRoomList").value;
	if(id==""){
		alert("请选择一项！");
	}else{
		document.getElementById("thisForm").action = "${pageContext.request.contextPath}/meetingRoom.do?method=go&opt=update&id="+id; 
		document.getElementById("thisForm").submit();
	}
}
 
// 条件查询
var queryWin = null;
function queryWinFun(id){
	var searchDiv = document.getElementById("search") ;
	searchDiv.style.display = "" ;
	if(!queryWin) { 
	    queryWin = new Ext.Window({
			title: '菜单查询',
			contentEl:'search',
	     	renderTo : searchWin,
	     	width: 620,
	     	height:270,
        	closeAction:'hide',
       	    listeners : {
	         	'hide':{
	         		fn: function () {
	         			new BlockDiv().hidden();
						queryWin.hide();
					}
				}
	        },
        	layout:'fit',
	    	buttons:[{
            	text:'搜索',
          		handler:function(){
          			goSearch();
            	}
        	},{
            	text:'清空',
          		handler:function(){  
          			goClear();
            	}
        	},{
            	text:'取消',
            	handler:function(){
               		queryWin.hide();
            	}
        	}]
	    });
	    
    }
    new BlockDiv().show();
    queryWin.show();
}



function grid_dblclick(obj){
	
}


// 删除
function goDelete(){
	if(document.getElementById("chooseValue_MeetingRoomList").value==""){
		alert("请选择一项！");
	}else{
		if(confirm("是否确定删除该会议室吗？","提示")){
			window.location="meetingRoom.do?method=delete&&id="+document.getElementById("chooseValue_MeetingRoomList").value;
		}
	}
}

</Script>


