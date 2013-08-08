<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>	
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>办公物品库存查询</title>
<script type="text/javascript">

Ext.onReady(function(){
	 
var tbar_customer=new Ext.Toolbar({
	renderTo:'divBtn',
       items:[{
           text:'审批',
           icon:'${pageContext.request.contextPath}/img/edit.gif',
           handler:function(){
        	   goApply();
			}
     	},'-',{
			text:'查看流程图',
           icon:'${pageContext.request.contextPath}/img/query.png',
			handler:function checkFlowsheet() {
				var id =  document.getElementById("chooseValue_cancelAuditList").value;	
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
		},'-',{
			text:'查询',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
	   		 handler:queryWinFun
		},'-',{
			text:'刷新',
	   		icon:'${pageContext.request.contextPath}/img/refresh.gif',
	   		 handler:function(){
	   			empty();
	   			goSearch_cancelAuditList();
	   		 }
		},'-',{
            text:'关闭',
            icon:'${pageContext.request.contextPath}/img/close.gif',
            handler:function(){
            	closeTab(parent.tab);
            }
        },'->'
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
			title: '物品报废查询',
			resizable:false,   //禁止用户 四角 拖动
			contentEl:'divCheck_select',
	     	//renderTo : searchWin,
	     	width: 300,
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
          			goSearch_cancelAuditList();
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
<body>
		<div id="divBtn"></div> 
		<div style="height:expression(document.body.clientHeight-28);">
			<mt:DataGridPrintByBean name="cancelAuditList" />
		</div>
	
<div id="divCheck_select" style="display: none;">
	<table border="0" align="center" width="100%">
		<tr>
			<td colspan="2" height="10"></td>
		</tr>
		<tr>
			<td width="30%" align="right">物品名称：</td>
			<td><input type="text" id="name" name="name"></td>
		</tr>
		<tr>
			<td width="30%" align="right">物品类别：</td>
			<td><input type="text" id="type" name="type"></td>
		</tr>
	 
		<tr>
			<td align="right">申请人：</td>
			<td><input type="text" id="userId" name="userId"></td>
		</tr>
		<tr>
			<td align="right">申请日期：</td>
			<td><input type="text" id="applyDate" name="applyDate"></td>
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
 
 //申领
 function goApply(){
	 var taskId =  document.getElementById("chooseValue_cancelAuditList").value;	
	 if(taskId ==""){
		 alert("请选择要审批的报废物品");
		 return ;
	 }else{
	 	window.location = "${pageContext.request.contextPath}/waresStock.do?method=cancelAuditSkip&taskId="+taskId;
	 }
}
 
 //查看
 function goView(){
	 var uuids = getChooseValue("cancelAuditList");
	 if(uuids ==""){
		 alert("请选择要查看的物品");
		 return ;
	 }else{
		 if(uuids.indexOf(",")>-1){
			 alert("不能同时查看多条物品详情，请选择单条进行查看！");
			return ;
		 }else{
		 	window.location = "${pageContext.request.contextPath}/waresStock.do?method=viewSkip&uuid="+uuids;
		 }
	 }
 }
 
</Script>