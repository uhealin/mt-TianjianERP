<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>客户收益评估</title>

<script type="text/javascript">  

function ext_init(){
	
	var tbar_levellist = new Ext.Toolbar({
		renderTo: 'gridDiv_ContractList',
		items:[{
			text:'查询', 
			id:'btn-query',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:function () {
            	queryWinFun();
            }
		},'-',{
			text:'打印',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/print.gif',
			handler:function () {
				print_ContractList();
			}
		},'-',{
			text:'关闭',
			icon:'${pageContext.request.contextPath}/img/close.gif',
           	handler:function(){
           		closeTab(parent.tab);
			}
		}]
	});
	
} 
 

var queryWin = null;
function queryWinFun(id){
	var searchDiv = document.getElementById("search") ;
	searchDiv.style.display = "" ; 	
	if(!queryWin) { 
	    queryWin = new Ext.Window({
			title: '查询',
			contentEl:'search',
		    renderTo :'searchWin',
	     	width: 350,
	     	height:150,
        	closeAction:'hide',
       	    listeners : {
	         	'hide':{
	         		fn: function () {
	         			new BlockDiv().hidden();
	         			searchDiv.style.display = "none" ;
						queryWin.hide();
					}
				}
	        },
        	layout:'fit',
	    	//html:searchDiv.innerHTML,
	    	buttons:[{
            	text:'确定',
          		handler:function(){
          			goSearch_ContractList();
    	           	queryWin.hide();
               		
            	}
          		
        	},{
            	text:'取消',
            	handler:function(){
            		document.getElementById("userid").value = "";
            		document.getElementById("projectYear").value = "";
            		goSearch_ContractList();
               		queryWin.hide();
            	}
        	}]
	    });
	    
    }
    new BlockDiv().show();
    queryWin.show();
}

function grid_dblclick(trObj,tableId) {

}
window.attachEvent('onload',ext_init);




</script>

</head>
<body >
<form name="thisForm" method="post"  action="">
<input type="hidden" name="customerid" id="customerid" value="${customerid}">

<div style="height:expression(document.body.clientHeight-23);" >
<mt:DataGridPrintByBean name="ContractList"  />
</div>


<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth)/2);top:expression((document.body.clientHeight)/2); z-index: 2"></div>

<div id="search" style="display:none">
<br/>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>

<table border="0" cellpadding="0" cellspacing="0" width="100%"  bgcolor="" >
	<tr align="center" height="10">
		
		<td align="right" >按责任人过滤：</td>
		<td align=left>
			<input name="userid" type="text" class='required'
				id="userid" maxlength="40" onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"
				valuemustexist="true" autoid="252" >
		</td>
	</tr>
	<tr align="center">
		
		<td align="right" height="10">按起始年度过滤：</td>
		<td align=left>
			<input name="projectYear" type="text" class='required'
				id="projectYear" maxlength="40" onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"
				valuemustexist="true" autoid="632" >
		</td>
	</tr>
</table>
</div>
</form>
</body>
</html>
