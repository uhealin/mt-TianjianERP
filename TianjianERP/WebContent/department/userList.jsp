<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>部门人员列表</title>
<script type="text/javascript">

Ext.onReady(function(){
	 new Ext.Toolbar({
		renderTo:'divBtn',
           items:[{
	            text:'查询',
	            icon:'${pageContext.request.contextPath}/img/query.gif',
	            handler:function(){
	            	queryWinFun();
				}
      		},'-',{
	            text:'返回',
	            icon:'${pageContext.request.contextPath}/img/back.gif',
	            handler:function () {
	            	window.history.back();
	            }
	        }]
     });  
	 

});

var queryWin = null;
function queryWinFun() {
	document.getElementById("search").style.display = "";
	if(queryWin == null) { 
	    queryWin = new Ext.Window({
			title: '部门人员查询',
			width: 400,
			height:200,
			contentEl:'search', 
			modal:true,
	        closeAction:'hide',
	        listeners:{
				'hide':{fn: function () {
					 document.getElementById("search").style.display = "none";
				}}
			},
	        layout:'fit',
	        contentEl:'search',
		    buttons:[{
	            text:'确定',
	          	handler:function() {
	          		goSearch_staticUserList();
	          		queryWin.hide();
	          	}
	        },{
	            text:'取消',
	            handler:function(){
	            	queryWin.hide();
	            }
	        }]
	    });
	}
	 queryWin.show();
}
</script>
</head>
<body>
<div id="divBtn" ></div>
<div style="height: expression(document.body.clientHeight-27);">
	<mt:DataGridPrintByBean name="staticUserList"/>
</div>

<div id="search" style="display: none;">
<br/>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
		
		<tr>
			<td align="right">性名：</td>
			<td align=left>
				<input type="text"
						id="userName"
						name="userName">
			</td>
		</tr>
		
        
         <tr>
			<td align="right">登陆名：</td>
			<td align=left>
				<input type="text"
						id="loginId"
						name="loginId">
			</td>
		 </tr>
		 
			 <tr>
				<td align="right">薪酬级别：</td>
				<td align=left>
					<input type="text"
							id="rank"
							name="rank">
				</td>
			 </tr>
	</table>
</div>

<form name="thisForm" method="post" action="">
	<input type="hidden" id="otherSqlWhere" name="otherSqlWhere">
	<input type="hidden" id="menu_id" name="menu_id">
	<input type="hidden" id="udate" name="udate" value="${beginDate}">
	<input type="hidden" id="edate" name="edate" value="${endDate}">
	<input type="hidden" id="isBack" name="isBack" value="true">
</form>

</body>

<script type="text/javascript">

	function grid_dblclick(trObj,tableId) { 
		
		var grid = Ext.getCmp("gridId_staticUserList");
	  	var select = grid.getSelectionModel();
	  	var active = select.selection.get(select._activeItem);
	    var col = active.col;
	    
	    var userid = trObj.userid ;
	    
	    var url = "" ;
	    
		if(col == 8) {
			//期间登陆次数
			document.getElementById("otherSqlWhere").value = " and cmdname IN ('用户登录','排班系统')" ;
			document.getElementById("menu_id").value = userid;
		 	url = "${pageContext.request.contextPath}/log.do";
		 	document.thisForm.action = url ;
		 	document.thisForm.submit() ;
		}
	}
</script>

</html>
