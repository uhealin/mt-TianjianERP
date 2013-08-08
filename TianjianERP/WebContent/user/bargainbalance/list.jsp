<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>

<title>合同结算计划</title>

<script type="text/javascript">  

function ext_init(){
	var tbar_bargainbalance = new Ext.Toolbar({
		renderTo: 'gridDiv_bargainbalance',
		items:[{
			text:'新增', 
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/add.gif',
			handler:function () {
				goAdd();
			}
		},'-',{
            text:'修改',
            cls:'x-btn-text-icon',
            icon:'/AuditSystem/img/edit.gif',
           	handler:function(){
				goUpdate();
			}
        },'-',{
            text:'删除',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/delete.gif',
            handler:function () {
            	goDelete();
            }
        },'-',{
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
				print_bargainbalance();
			}
		},'-',{
			text:'关闭',
			cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/close.gif',
			handler:function () {
				closeTab(parent.tab);
				//parent.tab.remove(parent.tab.getActiveTab()); 
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
          			thisForm.submit();
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
    new BlockDiv().show();
    queryWin.show();
}

window.attachEvent('onload',ext_init);

</script>











</head>
<body  leftmargin="0" topmargin="0">

<form name="thisForm" method="post" action="/AuditSystem/bargainbalance.do">

<div style="height:expression(document.body.clientHeight-23);" >
<mt:DataGridPrintByBean name="bargainbalance" outputData="true" outputType="invokeSearch" />
</div>

<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth)/2);top:expression((document.body.clientHeight)/2); z-index: 2"></div>

<div id="search" style="display:none">
<br/>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>

<table border="0" cellpadding="0" cellspacing="0" width="100%"  bgcolor="" >
	<tr align="center">
		
		<td align="right">项目编号：</td>
		<td align=left>
			<input name="bargainid" type="text" class='required'
				id="bargainid" maxlength="40" onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"
				valuemustexist="true" autoid="134" >
		</td>
	</tr>
</table>
</div>

</form>
</body>

<script>
function goAdd(){
	
    window.location="${pageContext.request.contextPath}/oa/bargainbalance/balanceadd.jsp" ;
	       
}  

function goUpdate(){
	var choose_bargainbalance = document.getElementById("chooseValue_bargainbalance").value;
	
	if(choose_bargainbalance == ""){
		alert("请选择要修改记录！");
	} else {		
		window.location="${pageContext.request.contextPath}/bargainbalance.do?method=exitblan&autoid=" + choose_bargainbalance;
	}
}

function goDelete(){
	var choose_bargainbalance = document.getElementById("chooseValue_bargainbalance").value;

	if(choose_bargainbalance == ""){
		alert("请选择要删除的记录！");
	} else {
		if(confirm("您的操作可能会造成数据丢失，您确定要删除该记录吗？","提示")){
			window.location="${pageContext.request.contextPath}/bargainbalance.do?method=removeblan&autoid=" + choose_bargainbalance;
		}
	}
} 




</script>
</html>