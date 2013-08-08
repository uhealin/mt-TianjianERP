<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>客户走访记录统计</title>

<script type="text/javascript">  

function ext_init(){
	
	var tbar_customer = new Ext.Toolbar({
   		renderTo: "gridDiv_customerVisitList",
   		height:30,
   		defaults: {autoHeight: true,autoWidth:true},
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
				print_customerVisitList();
			}
		},'-',{
			text:'关闭',
			cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/close.gif',
			handler:function () {
				closeTab(parent.tab);
			}
		},'-',new Ext.Toolbar.Fill()]
	});
	
} 

var queryWin = null;
function queryWinFun(id){
	var searchDiv = document.getElementById("search") ;
	searchDiv.style.display = "" ; 	
	if(!queryWin) { 
	    queryWin = new Ext.Window({
			title: '统计查询',
			contentEl:'search',
		    renderTo :'searchWin',
	     	width: 350,
	     	height:200,
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
          			consultCount();
    	           	queryWin.hide();
               		
            	}
          		
        	},{
            	text:'取消',
            	handler:function(){
               		queryWin.hide();
            	}
        	}]
	    });
	    
	    var udate,edate;
	    
	    if(!udate) {
		    udate = new Ext.form.DateField({
				applyTo : 'visitTime',
				width: 135,
				format: 'Y-m-d',
				emptyText: '' 
			});
	    }
		
		if(!edate) {
		    edate = new Ext.form.DateField({
				applyTo : 'visitTime2',
				width: 135,
				format: 'Y-m-d',
				emptyText: '' 
			});
	    }
    }
    new BlockDiv().show();
    queryWin.show();
}





window.attachEvent('onload',ext_init);
</script>

</head>

<body >
<div style="height:expression(document.body.clientHeight-30);" >
<mt:DataGridPrintByBean name="customerVisitList" outputData="true" outputType="invokeSearch" />
</div>

<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth)/2);top:expression((document.body.clientHeight)/2); z-index: 2"></div>

<div id="search" style="display:none">
<br/>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>

<table border="0" cellpadding="0" cellspacing="0" width="100%"  bgcolor="" >
	<tr align="center">
		
		<td align="right">走访时间起：</td>
		<td align=left>
			<input type="text" name="visitTime" id="visitTime"  class="validate-date-cn"/>
		</td>
	</tr>
	<tr align="center">
		
		<td align="right">走访时间至：</td>
		<td align=left>
			<input type="text" name="visitTime2" id="visitTime2"  class="validate-date-cn"/>
		</td>
	</tr>
</table>
</div>


<script>
	
	function clearAll(){
		window.location = "${pageContext.request.contextPath}/customerNote.do?method=noteCount";
	}
	
	function consultCount(){
		var startTime = document.getElementById("visitTime").value;
		var endTime = document.getElementById("visitTime2").value;
		
		if(startTime=="" && endTime==""){
			clearAll();
			return;
		}else if(startTime=="" && endTime!=""){
			alert("请选择您要统计的开始时间！");
			return;
		}else if(startTime!="" && endTime==""){
			alert("请选择您要统计的结束时间！");
			return;
		}else if(startTime > endTime){
			alert("开始时间不能大于结束时间！");
			return;
		}
		
		window.location = "${pageContext.request.contextPath}/customerNote.do?method=noteCount&startTime="+startTime+"&endTime="+endTime;
		
	}

</script>

</html>