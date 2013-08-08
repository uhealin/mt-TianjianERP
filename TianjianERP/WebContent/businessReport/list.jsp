<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>业务报告</title>


<script type="text/javascript">  

function ext_init(){
	new Ext.form.DateField({
		applyTo : 'property',
		width: 133,
		format: 'Y-m-d'
	});
	new Ext.form.DateField({
		applyTo : 'reaudit_time',
		width: 133,
		format: 'Y-m-d'
	});
	new Ext.form.DateField({
		applyTo : 'except_complete_time',
		width: 133,
		format: 'Y-m-d'
	});
	var tbar = new Ext.Toolbar({
		renderTo: 'btn',
		items:[{
			text:'查询', 
			id:'btn-query',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:function () {
            	queryWinFun();
            }
		},'-',{
            text:'查看',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/query.gif',
            handler:function(){
            	  var uuid=Ext.get("chooseValue_BusinessReportAction_tableid").dom.value;
            	  if(uuid!=null&&uuid!=''){
            	  var url="businessreport.do?method=viewBusinesReport&uuid="+uuid;
            	  window.location.href=url; // parent.parent.openTab("","预约查看",url);
            	  }else{
            		  alert("请选择要查看的预约记录");
            	  }
			}
   		},'->'
		]
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
	     	width: 300,
	     	height:300,
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
          			goSearch_BusinessReportAction_tableid();
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
<body>
<div id="btn"></div>
<form action="" method="post" id="thisForm" name="thisForm">

<div style="height:expression(document.body.clientHeight-23);" >
<mt:DataGridPrintByBean name="BusinessReportAction_tableid" />
</div>
<input type="hidden" name="customerid" id="customerid" value="${customerid}"> 
<input name="customerName" type="hidden"  value="${customerName}" >&nbsp;

<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth)/2);top:expression((document.body.clientHeight)/2); z-index: 2"></div>

<div id="search" style="display:none">
<br/>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>

<table border="0" cellpadding="0" cellspacing="0" width="100%"  bgcolor="" >
	<tr align="center">
		
		<td align="right">组名：</td>
		<td align=left>
			<input  type="text" name="group_name" id="group_name" size="20"  autoid=7002/>
		</td>
	</tr>
	<tr align="center">
		
		<td align="right">送审人：</td>
		<td align=left>
			<input  type="text" name="sname" id="sname" size="20"  autoid=10016/>
		</td>
	</tr>
	<tr align="center">
		<td align="right">送审部门：</td>
		<td align=left>
			<input type="text" autoid=30026 name="sqdepartment" id="sqdepartment"  class="validate-date-cn"/>
		</td>
	</tr>
	<tr align="center">
		
		<td align="right">申请时间：</td>
		<td align=left>
			<input type="text" name="property" id="property" class="validate-date-cn"/>
		</td>
	</tr>	
		<tr align="center">
		<td align="right">送审时间：</td>
		<td align=left>
			<input type="text" name="reaudit_time" id="reaudit_time"  class="validate-date-cn"/>
		</td>
	</tr>	
		<tr align="center">
		<td align="right">到期时间：</td>
		<td align=left>
			<input type="text" name="except_complete_time" id="except_complete_time"  class="validate-date-cn"/>
		</td>
	</tr>	
</table>
</div>

</form>
</div>
</body>
</html>