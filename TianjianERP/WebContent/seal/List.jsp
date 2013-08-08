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
			text:'查看审批状态',
            icon:'${pageContext.request.contextPath}/img/query.png',
			handler:function checkFlowsheet() {
				var id =  document.getElementById("chooseValue_sealList").value;
				
				if(id == "") {
					alert("请从待审核 任务标签页中选择一条审核 记录！");
					return ;
				} else {
					var uuid  =id.split(",");
					 
					 var url="${pageContext.request.contextPath}/seal.do?method=lookImages";
					 var requestString = "&uuid="+uuid;
					 var request= ajaxLoadPageSynch(url,requestString);
					 var taskId = request;
					if(taskId =="" || taskId ==null ){
						 alert("公章未发起或发起失败，不能查看流程图");
						return ;
					 }else{
						openTab("lookFlowss","流程图","${pageContext.request.contextPath}/commonProcess.do?method=viewImageByTaskId&id="+taskId,window.parent);
					 }
				}
			}
		},'-',{
			text:'查看流程图',
            icon:'${pageContext.request.contextPath}/img/query.png',
			handler:function checkFlowsheet() {
				viewImage("sealApplyFlow","");
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
            text:'打 印',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/print.gif',
            handler:function () {
            	print_sealList();
            }
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
		     	height:240,
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

function viewImage(key,url) {
	
	if(url ==""){
		var request = ajaxLoadPageSynch("/AuditSystem/leave.do?method=queryJbpmPdIdByKey","&key="+key+"&rendom="+Math.random());
		if(request == ""){
			return ;
		}
		url = "/AuditSystem/commonProcess.do?method=viewImageByDefinitionId&pdId="+request+"&rendom="+Math.random();
	}
	var tab = parent.parent.tab ;
    if(tab){
		n = tab.add({    
			title:"流程图",    
			closable:true,  //通过html载入目标页    
			html:'<iframe name="fdfd" scrolling="auto" frameborder="0" width="100%" height="100%" src="'+url+'"></iframe>'   
		}); 
        tab.setActiveTab(n);
	}
}
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
		<td   align="right" width="20%">申请事项：</td>
		<td   style="paddingpadding-left: 50px;">
			<input  type="text" name="matter" id="matter" class="required" onfocus="onPopDivClick(this);"
				onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();"
				onclick="onPopDivClick(this);"
				autoid=4580
				  />
			 
		</td>
	</tr>
	<tr>
		<td   align="right" width="20%">
				<div>公章类型：</div>
		</td>
		 <td   style="paddingpadding-left: 50px;">
			<input  type="text" name="ctype" id="ctype" class="required" onfocus="onPopDivClick(this);"
				onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();"
				onclick="onPopDivClick(this);"
				autoid=4581 noinput=true
				value="${seal.ctype }" />
			 
		</td>
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
<Script>

new Ext.form.DateField({			
	applyTo : 'applyDate',
	width: 133,
	format: 'Y-m-d'	
});

function goAdd()
{
	window.location="${pageContext.request.contextPath}/sealNotFlow.do?method=addSkip";
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
			
			 var url="${pageContext.request.contextPath}/sealNotFlow.do?method=getStatus";
			 var requestString = "&uuid="+uuid;
			 var request= ajaxLoadPageSynch(url,requestString);
			 if(request !="未发起"){
				 alert("您的印章状态是"+request+"，不能进行删除操作");
				 return ;
			 }
			
			window.location="sealNotFlow.do?method=delete&&uuid="+uuid;
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
		   	 var url="${pageContext.request.contextPath}/seal.do?method=getStatus";
			 var requestString = "&uuid="+uuids;
			 var request= ajaxLoadPageSynch(url,requestString);
			 if(request !="未发起"){
				 alert("您的印章状态是"+request+"，不能进行发起操作");
				 return ;
			 }
	   		
		    url = "${pageContext.request.contextPath}/seal.do?method=updateStatus";
		    requestString = "&uuid="+uuids;
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
		   	 var url="${pageContext.request.contextPath}/sealNotFlow.do?method=getStatus";
			 var requestString = "&uuid="+uuids;
			 var request= ajaxLoadPageSynch(url,requestString);
			 if(request !="未发起"){
				 alert("您的印章状态是"+request+"，不能进行修改操作");
				 return ;
			 }
			 window.location = "${pageContext.request.contextPath}/sealNotFlow.do?method=updateSkip&uuid="+uuids;
				 
		}
}

function goFlow(){
	var uuids =  document.getElementById("chooseValue_sealList").value;	
	 
 	if(uuids=="" || uuids==null){
		alert("请选择对象！");
		return;
   	}else{
	   	 var url="${pageContext.request.contextPath}/sealNotFlow.do?method=getStatus";
		 var requestString = "&uuid="+uuids;
		 var request= ajaxLoadPageSynch(url,requestString);
		 if(request !="未发起"){
			 alert("您的印章状态是"+request+"，不能进行发起申领操作");
			 return ;
		 }
			if(confirm("确定发起此公章吗？发起后将无法删除和修改","yes")){
				
				 url="${pageContext.request.contextPath}/seal.do?method=startFlow";
				 requestString = "&uuid="+uuids;
				 request= ajaxLoadPageSynch(url,requestString);
				  goSearch_sealList();
			}
			 
	}
}


//dategrid双击事件
function grid_dblclick(obj, tableId) {
	var uuid=obj.uuid;
	if(uuid==""){
		return ;
	}else{
		 window.location ="${pageContext.request.contextPath}/seal.do?method=lookAudit&uuid="+uuid;	
	}
}

function empty(){
	document.getElementById("matter").value="";
	document.getElementById("remark").value="";
	document.getElementById("ctype").value="";
	document.getElementById("applyDate").value="";
}
</Script>