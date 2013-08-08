<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>	
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>印章管理</title>
<script type="text/javascript">
	function openUrl(url,name) {
		var tab = parent.tab ;
		if(tab && tab.id == "mainFrameTab"){
			try{
				n = parent.parent.tab.add({    
					'title':name,  
					 closable:true,  //通过html载入目标页    
					 html:'<iframe scrolling="no" frameborder="0" width="100%" height="100%" src="' + url + '"></iframe>'   
				});    
				parent.parent.tab.setActiveTab(n);
			}catch(e){
				window.location = url ;
			}
		}else {
			window.location = url ;
		}
		
	}

function ext_init(){ 
	
	 tabs = new Ext.TabPanel({
		    renderTo: 'my-tabs',
		    activeTab: 0,
		    layoutOnTabChange:true, 
		    forceLayout : true,
		    deferredRender:false,
		    height: document.body.clientHeight-Ext.get('my-tabs').getTop(),
		    width : document.body.clientWidth, 
		    defaults: {autoWidth:true,autoHeight:true},
		    items:[
		        {contentEl:'tab1', title:'未审批', id:'cur1'},
		        {contentEl:'tab2', title:'已审批', id:'cur2'} 
		    ]
		});
		
		tabs.on("tabchange",function(tabpanel,tab) {
	    	if(tab.id == "cur2") {
					goSearch_sealYetAuditList();			
			} 
	    }) ;

		goSearch_sealAuditList();
	
	var tbar = new Ext.Toolbar({
		renderTo: 'divBtn',
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
				var id =  document.getElementById("chooseValue_sealAuditList").value;
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
            text:'打 印',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/print.gif',
            handler:function () {
            	print_sealAuditList();
            }
        },'-',{
			text:'刷新',
	   		icon:'${pageContext.request.contextPath}/img/refresh.gif',
	   		 handler:function(){
	   			empty();
	   			goSearch_sealAuditList();
	   		 }
		}
        ]
     });  
	
	var tbar2 = new Ext.Toolbar({
		renderTo: 'divBtn2',
		items:[{
			text:'查询',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
	   		 handler:queryWinFun2
		},'-',{
            text:'打 印',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/print.gif',
            handler:function () {
            	print_sealYetAuditList();
            }
        },'-',{
			text:'刷新',
	   		icon:'${pageContext.request.contextPath}/img/refresh.gif',
	   		 handler:function(){
	   			empty();
	   			goSearch_sealYetAuditList();
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
				title: '印章审批查询',
				resizable:false,   //禁止用户 四角 拖动
				contentEl:'divCheck_select',
		     	//renderTo : searchWin,
		     	width: 300,
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
	          			goSearch_sealAuditList();
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
	
	var queryWin2 = null;
	function queryWinFun2(id){
		resizable:false;
		var searchDiv2 = document.getElementById("divCheck_select2") ;
		searchDiv2.style.display = "block" ;
		if(!queryWin2) { 
		    queryWin2 = new Ext.Window({
				title: '印章已审查询',
				resizable:false,   //禁止用户 四角 拖动
				contentEl:'divCheck_select2',
		     	//renderTo : searchWin,
		     	width: 300,
		     	height:260,
	        	closeAction:'hide',
	       	    listeners : {
		         	'hide':{
		         		fn: function () {
		         			new BlockDiv().hidden();
							queryWin2.hide();
						}
					}
		        },
	        	layout:'fit',
		    	buttons:[{
	            	text:'搜索',
	          		handler:function(){
	          			goSearch_sealYetAuditList();
						queryWin2.hide();
	            	}
	        	},{
	            	text:'清空',
	            	handler:function(){
	            		empty();
	            	}
	        	},{
	            	text:'取消',
	            	handler:function(){
	               		queryWin2.hide();
	            	}
	        	}]
		    });
	    }
	    new BlockDiv().show();
	    queryWin2.show();
	}
}
window.attachEvent('onload',ext_init);

</script>

</head>

<body leftmargin="0" topmargin="0">


<div id="my-tabs">
	<div style="height:expression(document.body.clientHeight-58);" id="tab1">
	<div id="divBtn"></div> 
		<mt:DataGridPrintByBean name="sealAuditList" /> 
	</div>
	
	<div style="height:expression(document.body.clientHeight-58);" id="tab2">
		<div id="divBtn2"></div> 
		<mt:DataGridPrintByBean name="sealYetAuditList" /> 
	</div>
</div>
<div id="divCheck_select" style="display: none;">
	<table border="0" align="center" width="100%" style="line-height: 25px;">
		<tr>
			<td colspan="2" height="10"></td>
		</tr>
		<tr>
			<td width="30%" align="right">申请事项：</td>
			<td><input type="text" id="matter" name="matter"></td>
		</tr>
		<tr>
			<td width="30%" align="right">公章类型：</td>
			<td><input type="text" id="ctype" name="ctype"></td>
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
			<td align="right">备注：</td>
			<td>
				<textarea rows="3" cols="20" id="remark" name="remark"></textarea>
			 </td>
		</tr>
	</table>
</div>

<div id="divCheck_select2" style="display: none;">
	<table border="0" align="center" width="100%" style="line-height: 25px;">
		<tr>
			<td colspan="2" height="10"></td>
		</tr>
		<tr>
			<td width="30%" align="right">申请事项：</td>
			<td><input type="text" id="matter2" name="matter2"></td>
		</tr>
		<tr>
			<td width="30%" align="right">公章类型：</td>
			<td><input type="text" id="ctype2" name="ctype2"></td>
		</tr>
		<tr>
			<td width="30%" align="right">申请人：</td>
			<td><input type="text" id="userId2" name="userId2"></td>
		</tr>
		<tr>
			<td width="30%" align="right">申请时间：</td>
			<td><input type="text" id="applyDate2" name="applyDate2"></td>
		</tr>
		<tr>
			<td align="right">备注：</td>
			<td>
				<textarea rows="3" cols="20" id="remark2" name="remark2"></textarea>
			 </td>
		</tr>
	</table>
</div>
<Script>

new Ext.form.DateField({			
	applyTo : 'applyDate',
	width: 133,
	format: 'Y-m-d'	
});

new Ext.form.DateField({			
	applyTo : 'applyDate2',
	width: 133,
	format: 'Y-m-d'	
});

function goAudit()
{
	var id =  document.getElementById("chooseValue_sealAuditList").value;
	if(id == "") {
		alert("请从要审批的记录！");
		return ;
	} else {
		window.location="${pageContext.request.contextPath}/seal.do?method=auditSkip&taskId="+id;
	}
} 
function empty(){
	document.getElementById("matter").value="";
	document.getElementById("remark").value="";
	document.getElementById("ctype").value="";
	document.getElementById("userId").value="";
	document.getElementById("applyDate").value="";
}
	function goView(pId,pName) {
			if(pId !=""){
				if(pId.indexOf(".")>-1){
					openUrl('${pageContext.request.contextPath}/commonProcess.do?method=viewImage&id='+pId,pName) ;
				}
			}else{
				alert("流程编号不存在，无法查看流程图");
				return ;
			}
	}
</Script>