<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>	
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>公告管理</title>
<script type="text/javascript">


function ext_init(){ 
	var tbar = new Ext.Toolbar({
		renderTo: 'divBtn',
		items:[{
			text:'审批',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/edit.gif',
			handler:function () {
				goAudit();
			}
		},'-',{
			text:'查看流程图',
            icon:'${pageContext.request.contextPath}/img/query.png',
			handler:function checkFlowsheet() {
				var id = document.getElementById("chooseValue_proclamationAuditList").value;
				if(id == "") {
					alert("请从待审核 任务标签页中选择一条审核 记录！");
					return ;
				} else {
					openTab("lookFlowss","流程图","${pageContext.request.contextPath}/commonProcess.do?method=viewImageByTaskId&id="+id+"&key=",window.parent);
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
	   			goSearch_proclamationAuditList();
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
				title: '公告查询',
				resizable:false,   //禁止用户 四角 拖动
				contentEl:'divCheck_select',
		     	//renderTo : searchWin,
		     	width: 300,
		     	height:230,
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
	          			goSearch_proclamationAuditList();
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
	<mt:DataGridPrintByBean name="proclamationAuditList" />
</div>

<div id="divCheck_select" style="display: none;">
	<table border="0" align="center" width="100%" style="line-height: 25px;">
		<tr>
			<td colspan="2" height="10"></td>
		</tr>
		<tr>
			<td width="30%" align="right">申请人：</td>
			<td><input type="text" id="name" name="name"></td>
		</tr>
		<tr>
			<td width="30%" align="right">标题：</td>
			<td><input type="text" id="title" name="title"></td>
		</tr>
		<tr>
			<td width="30%" align="right">发布日期：</td>
			<td><input type="text" id="publishDate" name="publishDate"></td>
		</tr>
		<tr>
			<td align="right">内容：</td>
			<td>
				<textarea rows="5" cols="25" id="content" name="content"></textarea>
			 </td>
		</tr>
	</table>
</div>
<Script>

new Ext.form.DateField({			
	applyTo : 'publishDate',
	width: 133,
	format: 'Y-m-d'	
});

function goAdd()
{
	window.location="${pageContext.request.contextPath}/proclamationSy.do?method=addSkip";
}

function goAudit(){
	var taskId = document.getElementById("chooseValue_proclamationAuditList").value;
	
	if(taskId=="")
	{
		alert("请选择要审批的公告！");
		return ;
	}
	else
	{
		window.location="${pageContext.request.contextPath}/proclamationSy.do?method=auditSkip&taskId="+taskId;
	}
}

function goDelete()
{	
	
	var uuid = document.getElementById("chooseValue_proclamationAuditList").value;
	
	if(uuid=="")
	{
		alert("请选择要删除的公告！");
		return ;
	}
	else
	{
		if(confirm("确定删除此公告吗？","yes")){
			window.location="proclamationSy.do?method=delete&&uuid="+uuid;
		}
	}
}

function goUpdate(){
	 	var uuids =  document.getElementById("chooseValue_proclamationAuditList").value;	
	 
	 	if(uuids=="" || uuids==null){
			alert("请选择要修改的对象！");
			return;
	   	}else{
			 window.location = "${pageContext.request.contextPath}/proclamationSy.do?method=updateSkip&uuid="+uuids;
				 
		}
}


function goLook(){
 	var uuids =  document.getElementById("chooseValue_proclamationAuditList").value;	
 
 	if(uuids=="" || uuids==null){
		alert("请选择要查看的对象！");
		return;
   	}else{
		 window.location = "${pageContext.request.contextPath}/proclamationSy.do?method=look&uuid="+uuids;
			 
	}
}
function empty(){
	document.getElementById("title").value="";
	document.getElementById("publishDate").value="";
	document.getElementById("content").value="";
}
</Script>