<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>系统用户使用情况统计表</title>
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
	            text:'打印',
	            icon:'${pageContext.request.contextPath}/img/print.gif',
	            handler:function () {
	            	print_usageStaticList();
	        	}
	        },'-',{
	            text:'关闭',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function () {
	            	closeTab(parent.tab);
	            }
	        },'-','<span id=beginDateSpan style="margin-left:150px;color:#ff6600;">起始日期：${beginDate}</span> '
	       + '<span id=endDateSpan style="margin-left:20px;color:#ff6600;">结束日期：${endDate}</span>'
        ]
     });  
	 
	 new Ext.form.DateField({
		applyTo : 'beginDate',
		width: 133,
		format: 'Y-m-d'
	 });
	 
	 new Ext.form.DateField({
		applyTo : 'endDate',
		width: 133,
		format: 'Y-m-d'
	 });

});

var queryWin = null;
function queryWinFun() {
	document.getElementById("search").style.display = "";
	if(queryWin == null) { 
	    queryWin = new Ext.Window({
			title: '系统使用情况统计查询',
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
	          		document.getElementById("beginDateSpan").innerText = "起始日期："+document.getElementById("beginDate").value ;
	          		document.getElementById("endDateSpan").innerText = "结束日期："+document.getElementById("endDate").value ;
	          		goSearch_usageStaticList();
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
	<mt:DataGridPrintByBean name="usageStaticList"/>
</div>

<div id="search" style="display: none;">
<br/>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
		
		<tr>
			<td align="right">起始日期：</td>
			<td align=left>
				<input type="text"
						id="beginDate"
						name="beginDate"
						value="${beginDate}">
			</td>
		</tr>
		
        
         <tr>
			<td align="right">结束日期：</td>
			<td align=left>
				<input type="text"
						id="endDate"
						name="endDate"
						value="${endDate}">
			</td>
		 </tr>
		 
		 <c:if test="${limit != 'true'}">
			 <tr>
				<td align="right">所属部门：</td>
				<td align=left>
					<input type="text"
							id="departname"
							name="departname">
				</td>
			 </tr>
		 </c:if>
	</table>
</div>
</body>

<script type="text/javascript">

	function grid_dblclick(trObj,tableId) { 
		
		var grid = Ext.getCmp("gridId_usageStaticList");
	  	var select = grid.getSelectionModel();
	  	var active = select.selection.get(select._activeItem);
	    var row = active.row, col = active.col;
	    
	    var departmentid = trObj.departmentid ;
	    var beginDate = document.getElementById("beginDate").value ;
	    var endDate = document.getElementById("endDate").value ;
	    
	    var url = "" ;
		if((col>2 && col<7) || col == 9) {
			//人员列 
			 url = "${pageContext.request.contextPath}/department.do?method=userList&departmentid="+departmentid 
			 	 + "&beginDate="+beginDate+"&endDate="+endDate + "&col="+col ;
		}else if(col == 7) {
			url = "${pageContext.request.contextPath}/department.do?method=customerList&departmentid="+ departmentid;
		}else if(col == 8) {
			url = "${pageContext.request.contextPath}/department.do?method=projectList&departmentid="+ departmentid;
		}else if(col == 10 || col==11) {
			url = "MT://部门人员一览/"+beginDate+"/"+endDate+"/"+ departmentid;
		}
		if(url != "") 
	   		window.location = url ;
	}
</script>

</html>
