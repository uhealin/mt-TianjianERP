<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>	
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>印章统计</title>
<script type="text/javascript">


function ext_init(){ 
	var tbar = new Ext.Toolbar({
		renderTo: 'divBtn',
		items:[{
			text:'查询',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
	   		 handler:queryWinFun
		},'-',{
			text:'刷新',
	   		icon:'${pageContext.request.contextPath}/img/refresh.gif',
	   		 handler:function(){
	   			empty();
	   			goSearch_sealStatisticsList();
	   		 }
		}
        ]
     });  
	

	//window 面板 进行查询
	var queryWin = null;
	function queryWinFun(id){
		resizable:false;
		var searchDiv = document.getElementById("divCheck_select") ;
		searchDiv.style.display = "block" ;
		if(!queryWin) { 
		    queryWin = new Ext.Window({
				title: '印章统计查询',
				resizable:false,   //禁止用户 四角 拖动
				contentEl:'divCheck_select',
		     	//renderTo : searchWin,
		     	width: 330,
		     	height:260,
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
	          			goSearch_sealStatisticsList();
						queryWin.hide();
	            	}
	        	},{
	            	text:'清空',
	            	handler:function(){
	            		empty();
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
}
window.attachEvent('onload',ext_init);

</script>

</head>

<body leftmargin="0" topmargin="0">
<div id="divBtn"></div> 

<div style="height:expression(document.body.clientHeight-28);" >
	<mt:DataGridPrintByBean name="sealStatisticsList" />
</div>

<div id="divCheck_select" style="display: none;">
	<table border="0" align="center" width="100%">
		<tr>
			<td colspan="2" height="10"></td>
		</tr>
		<tr>
			<td align="right">申请人：</td>
			<td><input type="text" id="userId" name="userId"></td>
		</tr>
		<tr>
			<td align="right">申请日期：</td>
			<td><input type="text" id="applyDate" name="applyDate"></td>
		</tr>
		<tr>
			<td align="right">申请人所属部门：</td>
			<td><input type="text" id="departname" name="departname"></td>
		</tr>
		<tr>
			<td width="30%" align="right">印章事项：</td>
			<td><input type="text" id="matter" name="matter"></td>
		</tr>
		<tr>
			<td align="right">印章类型：</td>
			<td><input type="text" id="ctype" name="ctype"></td>
		</tr>
		<tr>
			<td align="right">状态：</td>
			<td><input type="text" id="status" name="status"></td>
		</tr>
	</table>
</div>
<Script>

new Ext.form.DateField({			
	applyTo : 'applyDate',
	width: 133,
	format: 'Y-m-d'	
});

function empty(){
	document.getElementById("applyDate").value="";
	document.getElementById("departname").value="";
	document.getElementById("matter").value="";
	document.getElementById("userId").value="";
	document.getElementById("ctype").value="";
	document.getElementById("status").value="";
} 

</Script>