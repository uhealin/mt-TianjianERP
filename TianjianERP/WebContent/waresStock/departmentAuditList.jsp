<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>	
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>物品部门审批</title>
<script type="text/javascript">

Ext.onReady(function(){
	 
var tbar_customer=new Ext.Toolbar({
	renderTo:'divBtn',
       items:[ {
            text:'审批',
            icon:'${pageContext.request.contextPath}/img/edit.gif',
            handler:function(){
            	goAudit();
 			}
      	},'-',{
			text:'查看流程图',
            icon:'${pageContext.request.contextPath}/img/query.png',
			handler:function checkFlowsheet() {
				var id = getChooseValue("deparementAuditList");
				if(id == "") {
					alert("请从待审核 任务标签页中选择一条审核 记录！");
					return ;
				} else {
					if(id.indexOf(",")>-1){
						 alert("不能同时查看多条物品流程图，请选择单条进行查看！");
						return ;
					 }else{
						openTab("lookFlowss","流程图","${pageContext.request.contextPath}/commonProcess.do?method=viewImageByTaskId&id="+id+"&key=",window.parent);
					 }
				}
			}
		} ,'-',{
			text:'查询',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
	   		 handler:queryWinFun
		},'-',{
			text:'刷新',
	   		icon:'${pageContext.request.contextPath}/img/refresh.gif',
	   		 handler:function(){
	   			empty();
	   			goSearch_deparementAuditList();
	   		 }
		}  ]
    });  
        
       
        

//window 面板 进行查询
var queryWin = null;
function queryWinFun(id){
	resizable:false;
	var searchDiv = document.getElementById("divCheck_select") ;
	searchDiv.style.display = "block" ;
	if(!queryWin) { 
	    queryWin = new Ext.Window({
			title: '物品申领查询',
			resizable:false,   //禁止用户 四角 拖动
			contentEl:'divCheck_select',
	     	//renderTo : searchWin,
	     	width: 320,
	     	height:200,
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
          			goSearch_deparementAuditList();
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
});

</script>
</head>
<body >
		<div id="divBtn"></div> 
		<div style="height:expression(document.body.clientHeight-28);" >
			<mt:DataGridPrintByBean name="deparementAuditList" />
		</div>
	
<div id="divCheck_select" style="display: none;">
	<table border="0" align="center" width="100%">
		<tr>
			<td colspan="2" height="10"></td>
		</tr>
		<tr>
			<td width="30%" align="right">申请人：</td>
			<td><input type="text" id="userId" name="userId"></td>
		</tr>
		<tr>
			<td width="30%" align="right">申请时间：</td>
			<td><input type="text" id="applyDate" name="applyDate"></td>
		</tr>
		<tr>
			<td width="30%" align="right">物品名称：</td>
			<td><input type="text" id="name" name="name"></td>
		</tr>
		<tr>
			<td width="30%" align="right">物品类别：</td>
			<td><input type="text" id="type" name="type"></td>
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
		document.getElementById("name").value="";
	 	document.getElementById("type").value="";
		document.getElementById("applyDate").value="";
		document.getElementById("userId").value="";
 }
 
 function goAudit(){
	 	 
	 	var uuids = getChooseValue("deparementAuditList");
	 	 if(uuids=="" || uuids==null){
			alert("请选择要修改的对象！");
			return;
	   	}else{
	   		window.location = "${pageContext.request.contextPath}/waresStockSy.do?method=auditSkip&ctype=bumen&uuids="+uuids;
				 
		}
 }
 
</Script>