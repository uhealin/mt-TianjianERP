<%@ page language="java" contentType="text/html; charset=gbk"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>	
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>离职人员列表</title>
<script type="text/javascript">


function ext_init(){ 
	var tbar = new Ext.Toolbar({
		renderTo: 'divBtn',
		items:[{
            text:'新增',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/add.gif',
            handler:function(){
            	window.location="${pageContext.request.contextPath}/leaveCompany.do?method=goAdd";
 			}
      	},'-',{
            text:'修改',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/edit.gif',
           	handler:function(){
 				goEdit();
 			}
         },'-',{
            text:'删除',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/delete.gif',
            handler:function () {
				goDelete();
            }
        },'-',{
            text:'发起离职申请',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/delete.gif',
            handler:function () {
				goStartLeaveCompany();
            }
        },'-',{
			text:'刷新',
	   		icon:'${pageContext.request.contextPath}/img/refresh.gif',
	   		 handler:function(){
	   			goSearch_pConfigGeneralReferenceList();
	   		 }
		}
        ]
     });
}
var ConfigGeneralReferenceAddDiv = null;
function queryWinFun(id){
	var searchDiv = document.getElementById("ConfigGeneralReferenceAdd");
	searchDiv.style.display = "block" ;
	if(!ConfigGeneralReferenceAddDiv) { 
		ConfigGeneralReferenceAddDiv = new Ext.Window({
			title: '事务所工时总基准',
	     	renderTo:configGeneralReferenceAddWin,
			contentEl:'ConfigGeneralReferenceAdd',
	     	width:300,
	     	height:150,
        	closeAction:'hide',
       	    listeners : {
	         	'hide':{
	         		fn: function () {
	         			new BlockDiv().hidden();
	         			ConfigGeneralReferenceAddDiv.hide();
					}
				}
	        },
        	layout:'fit',
	    	buttons:[{
            	text:'增加',
          		handler:function(){
            		var addtime =document.getElementById("ConfigGeneralReferenceAddTime").value;
            		var addmoney=document.getElementById("ConfigGeneralReferenceAddMoney").value;
            		if(addtime!=""){
            			if(addmoney!=""){
	            			if(isNaN(addtime)){
	            				alert("工时基础请输入数字");
	            			}else{
	            				if(isNaN(addmoney)){
		            				alert("工时工资请输入数字");
		            			}
	            				else{
	            					window.location="${pageContext.request.contextPath}/projectManagerConfig.do?method=addConfigGeneralReference&addtime="+addtime+"&addmoney="+addmoney;
	            				}
	            			}
            			}else{
            				alert("工时工资基础（元）不能为空");
            			}
            		}else{
            			alert("工时基础（小时）不能为空");
            		}
            	}
        	},{
            	text:'清空',
          		handler:function(){
            		document.getElementById("ConfigGeneralReferenceAddTime").value="";
            		document.getElementById("ConfigGeneralReferenceAddMoney").value="";
            	}
        	},{
            	text:'取消',
            	handler:function(){
            		document.getElementById("ConfigGeneralReferenceAddTime").value="";
            		document.getElementById("ConfigGeneralReferenceAddMoney").value="";
         			new BlockDiv().hidden();
	        		ConfigGeneralReferenceAddDiv.hide();
            	}
        	}]
	    });
    }
    new BlockDiv().show();
    ConfigGeneralReferenceAddDiv.show();
}
window.attachEvent('onload',ext_init);

</script>

</head>

<body leftmargin="0" topmargin="0">
<div id="configGeneralReferenceAddWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>
<div id="divBtn"></div> 

<div style="height:expression(document.body.clientHeight-28);" >
	<mt:DataGridPrintByBean name="leaveCompanyList" />
</div>

<div id="ConfigGeneralReferenceAdd" style="display: none;">
	<table border="0" align="center" width="100%" style="line-height: 25px;">
		<tr>
			<td width="50%" align="right">工时基础（小时）：</td>
			<td><input type="text" id="ConfigGeneralReferenceAddTime" name="ConfigGeneralReferenceAddTime" /></td>
		</tr>
		<tr>
			<td width="50%" align="right">工时工资基础（元）：</td>
			<td><input type="text" id="ConfigGeneralReferenceAddMoney" name="ConfigGeneralReferenceAddMoney" /></td>
		</tr>
	</table>
</div>
<Script>

function goDelete()
{	
	
	var uuid = getChooseValue("leaveCompanyList");
	
	if(uuid=="")
	{
		alert("请选择要删除的记录！");
		return ;
	}
	else
	{
		if(confirm("确定删除此记录吗？","yes")){
			window.location="${pageContext.request.contextPath}/leaveCompany.do?method=deleteLeaveCompany&&uuid="+uuid;
		}
	}
}

function goDelete()
{	
	
	var uuid = getChooseValue("leaveCompanyList");
	
	if(uuid=="")
	{
		alert("请选择要发起的记录！");
		return ;
	}
	else
	{
		if(confirm("确定发起离职吗？","yes")){
			window.location="${pageContext.request.contextPath}/leaveCompany.do?method=deleteLeaveCompany&&uuid="+uuid;
		}
	}
}


function goEdit(){
		var pid = getChooseValue("leaveCompanyList");
	 
	 	if(pid=="" || pid==null){
			alert("请选择要修改的记录！");
			return;
	   	}else{
	   		window.location="${pageContext.request.contextPath}/leaveCompany.do?method=updateSkipLeaveCompany&uuid="+pid;
				 
		}
}


</Script>