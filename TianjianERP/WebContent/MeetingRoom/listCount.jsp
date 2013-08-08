<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/Validate_include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
 
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>会议使用查询</title>
 
<Script type="text/javascript">

function ext_init(){ 
		var tbar = new Ext.Toolbar({
			renderTo: 'divBtn',
			items:[
		        {
					text:'查询',
					cls:'x-btn-text-icon',
					icon:'${pageContext.request.contextPath}/img/query.gif',
					handler:queryWinFun 
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
<mt:DataGridPrintByBean name="meetingRoomListCount"  />
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
				   size="40"  onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" autoid=751 /> 
			</td>
		</tr>
		<tr align="center">
			<td align="right" >所属机构：</td>
			<td align="left" colspan="3">
				<input type="text"
				   name="organ"
				   id="organ"
				   title="请输入有效的值" 
				   size="40" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" autoid=845  /> 
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
    goSearch_meetingRoomListCount(); 
} 

// 清空
function goClear(){
	document.getElementById("name").value="";
	document.getElementById("organ").value="";
	
	queryWin.hide();
    goSearch_meetingRoomListCount(); 
    
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
	     	height:220,
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


</Script>


