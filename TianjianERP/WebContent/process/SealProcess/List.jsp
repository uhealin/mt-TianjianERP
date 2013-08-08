<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>	
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>印章管理</title>
<script type="text/javascript">


function ext_init(){ 
	var tbar = new Ext.Toolbar({
		renderTo: 'divBtn',
		items:[{
			text:'新增',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/add.gif',
			handler:function () {
				goAdd();
			}
		},'-',{
			text:'发起申请',
            icon:'${pageContext.request.contextPath}/img/start.png',
			handler:function () {
				  goFlow();
			}
		},'-',{
			text:'查看流程图',
            icon:'${pageContext.request.contextPath}/img/query.png',
			handler:function checkFlowsheet() {
				var id =  document.getElementById("chooseValue_sealList").value;
				
				if(id == "") {
					alert("请从待审核 任务标签页中选择一条审核 记录！");
					return ;
				} else {
					var uuid  =id.split(",");
					if(uuid[1] =="-1"){
						 alert("公章未发起或发起失败，不能查看流程图");
						return ;
					 }else{
						openTab("lookFlowss","流程图","${pageContext.request.contextPath}/commonProcess.do?method=viewImageByTaskId&id="+uuid[1],window.parent);
					 }
				}
			}
		},'-',{
           text:'修改',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/edit.gif',
           handler:function(){
        	   goUpdate();
		   }
        },'-',{
			text:'删除',
			id:'del',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/delete.gif',
			handler:function(){
				goDelete();
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
	   			goSearch_sealList();
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
				title: '印章申请查询',
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
	          			goSearch_sealList();
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
	<mt:DataGridPrintByBean name="sealList" />
</div>

<div id="divCheck_select" style="display: none;">
	<table border="0" align="center" width="100%" style="line-height: 25px;">
		<tr>
			<td colspan="2" height="10"></td>
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
	window.location="${pageContext.request.contextPath}/seal.do?method=addSkip";
}
function goDelete()
{	
	
	var uuid = document.getElementById("chooseValue_sealList").value;
	if(uuid=="")
	{
		alert("请选择要删除的印章请用！");
		return ;
	}
	else
	{
		if(confirm("确定删除此印章吗请用？","yes")){
			
			var uuids = uuid.split(",");
			
			 var url="${pageContext.request.contextPath}/seal.do?method=getStatus";
			 var requestString = "&uuid="+uuids[0];
			 var request= ajaxLoadPageSynch(url,requestString);
			 if(request !="未发起"){
				 alert("您的印章状态是"+request+"，不能进行删除操作");
				 return ;
			 }
			
			window.location="seal.do?method=delete&&uuid="+uuids[0];
		}
	}
}

	//发起公章申请
	function updateStatus(){
		var uuids =  document.getElementById("chooseValue_sealList").value;	
	 	if(uuids=="" || uuids==null){
			alert("请选择要发起的对象！");
			return;
	   	}else{
	   		
	   		var uuid = uuids.split(",");
	   		
		   	 var url="${pageContext.request.contextPath}/seal.do?method=getStatus";
			 var requestString = "&uuid="+uuid[0];
			 var request= ajaxLoadPageSynch(url,requestString);
			 if(request !="未发起"){
				 alert("您的印章状态是"+request+"，不能进行发起操作");
				 return ;
			 }
	   		
		    url = "${pageContext.request.contextPath}/seal.do?method=updateStatus";
		    requestString = "&uuid="+uuid[0];
		    result =ajaxLoadPageSynch(url,requestString);
			  alert(result);
			  goSearch_sealList();
		}
	}

function goUpdate(){
	 	var uuids =  document.getElementById("chooseValue_sealList").value;	
	 	if(uuids=="" || uuids==null){
			alert("请选择要修改的对象！");
			return;
	   	}else{
		 	var uuid = uuids.split(",");
		 	
		   	 var url="${pageContext.request.contextPath}/seal.do?method=getStatus";
			 var requestString = "&uuid="+uuid[0];
			 var request= ajaxLoadPageSynch(url,requestString);
			 if(request !="未发起"){
				 alert("您的印章状态是"+request+"，不能进行修改操作");
				 return ;
			 }
			 window.location = "${pageContext.request.contextPath}/seal.do?method=updateSkip&uuid="+uuid[0];
				 
		}
}

function goFlow(){
	var uuids =  document.getElementById("chooseValue_sealList").value;	
	 
 	if(uuids=="" || uuids==null){
		alert("请选择对象！");
		return;
   	}else{
   		var uuid = uuids.split(",");
   		
	   	 var url="${pageContext.request.contextPath}/seal.do?method=getStatus";
		 var requestString = "&uuid="+uuid[0];
		 var request= ajaxLoadPageSynch(url,requestString);
		 if(request !="未发起"){
			 alert("您的印章状态是"+request+"，不能进行发起申领操作");
			 return ;
		 }
			if(confirm("确定发起此公章吗？发起后将无法删除和修改","yes")){
				
				 url="${pageContext.request.contextPath}/seal.do?method=startFlow";
				 requestString = "&uuid="+uuid[0];
				 request= ajaxLoadPageSynch(url,requestString);
				goSearch_sealList();
			}
			 
	}
}


//dategrid双击事件
function grid_dblclick(obj, tableId) {
	var taskId=obj.taskId;
	if(taskId==""){
		return ;
	}else{
		 window.location ="${pageContext.request.contextPath}/seal.do?method=lookAudit&taskId="+taskId;	
	}
}

function empty(){
	document.getElementById("title").value="";
	document.getElementById("publishDate").value="";
	document.getElementById("content").value="";
}
</Script>