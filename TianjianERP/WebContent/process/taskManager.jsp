<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<script type="text/javascript" src="${ctx}/js/process.js"></script>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript">
//EXT初始化
function ext_init(){
	new Ext.Toolbar({
		renderTo:'tbarDiv',
		items:[
			{ 
				text:'代办任务',
				icon:'${pageContext.request.contextPath}/img/check.png' ,
				handler:function(){
					audit();
				}
			},'-',{ 
				text:'更改处理人',
				icon:'${pageContext.request.contextPath}/img/edit.gif' ,
				handler:function(){
					
					var id = document.getElementById("chooseValue_taskManagerList").value;
					if(id=="") {
						alert("请选择需要更改的流程");
						return;
					} 
					winFun();
				}
			},'-',{ 
				text:'删除流程',
				icon:'${pageContext.request.contextPath}/img/delete.gif' ,
				handler:function(){
					
				}
			},'-',{ 
				text:'流程跟踪',
				icon:'${pageContext.request.contextPath}/img/switch2.gif' ,
				handler:function(){
					view();
				}
			},'-',{ 
                text:'查看流程图',
                icon:'${pageContext.request.contextPath}/img/query.gif' ,
                handler:function(){
                	mt_process_viewImage("taskManagerList");
    			}
          	},'-',{ 
				text:'查看所有',
				icon:'${pageContext.request.contextPath}/img/refresh.gif' ,
				handler:function(){
			    	window.location = "${pageContext.request.contextPath}/process.do?method=taskManagerList";
				}
			},'-',{ 
				text:'关闭',
				icon:'${pageContext.request.contextPath}/img/close.gif' ,
				handler:function(){
			    	closeTab(parent.mainTab) ;
				}
			}
		]
	});
}
var win = null;
function winFun() {
	document.getElementById("assign").style.display = "";
	if(win == null) { 
		win = new Ext.Window({
			title: '更改处理人',
			width: 430,
			height:180,
			contentEl:'assign', 
	        closeAction:'hide',
	        modal:true,
	        listeners:{
				'hide':{fn: function () {
					 document.getElementById("assign").style.display = "none";
				}}
			}, 
	        layout:'fit',
		    buttons:[{
	            text:'确定',
	            icon:'${pageContext.request.contextPath}/img/confirm.gif',
	          	handler:function() {
	          		assign() ;
	          	}
	        },{
	            text:'取消',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function(){
	            	win.hide();
	            }
	        }]
	    });
	}

	win.show();
}
window.attachEvent('onload',ext_init);
</script>
</head>

<body>
<div id=tbarDiv></div>
<div class="autoHeightDiv">
	<mt:DataGridPrintByBean name="taskManagerList" />
</div>

<div id="assign" style="display: none;">
	<br/>
	<div style="margin:0 20 0 20">请在下面选择一个人员：</div>
	<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
	
	<table border="0" cellpadding="5" cellspacing="10" width="100%" align="center">
	     <tr>
			<td align="right">人员名称： </td>
			<td>
				<input autoid=1 id="assignUser" name="assignUser" multilevel=true size="20" />
			</td>
		</tr>
	</table>
</div>

</body>

<script type="text/javascript">


function audit() {
	var id = document.getElementById("chooseValue_taskManagerList").value;
	if(id == "") {
		alert("请选择一条代办任务！");
		return ;
	}
	var trObj = document.getElementById("trValueId_"+id) ;
	var pkey = trObj.pkey;
	var taskId = trObj.taskId;
	
	if(!taskId) {
		alert("该流程已经审结，不能再办理!") ;
		return ;
	}
	
	window.location = "${pageContext.request.contextPath}/process.do?method=processTransfer&taskId=" + taskId + "&pKey="+pkey;
}

//修改
function edit(){
	var chooseValue = document.getElementById("chooseValue_processDefineList").value;
	if(chooseValue=="") {
		alert("请选择需要修改的流程");
		return;
	} else {
		window.location = "${pageContext.request.contextPath}/process.do?method=edit&id=" + chooseValue;
	}
}

//删除
function remove(){
	var chooseValue = document.getElementById("chooseValue_processDefineList").value;
	
	if(chooseValue=="") {
		alert("请选择需要删除的流程");
		return;
	} else {
		
		if(!confirm("确定要删除该流程？")) {
			return;
		}
		
		window.location = "${pageContext.request.contextPath}/process.do?method=remove&id=" + chooseValue;
	}
}

function view(){
	var id = document.getElementById("chooseValue_taskManagerList").value;
	
	if(id=="") {
		alert("请选择需要查看的流程");
		return;
	} 
	
	var trValue = document.getElementById("trValueId_"+id);
	
	var pId = trValue.pid ;
	var pName = trValue.pName ;
	var uuid = trValue.uuid ;
	
	mt_process_view(pId,pName,uuid) ;
}

function assign(){
	var id = document.getElementById("chooseValue_taskManagerList").value;
	
	var trValue = document.getElementById("trValueId_"+id);
	
	var taskId = trValue.taskId ;
	var pName = trValue.pName ;
	var uuid = trValue.uuid ;
	
	if(!taskId) {
		alert("该流程已经审结，不能更改办理人!") ;
		return ;
	}
	
	var assignUser = document.getElementById("assignUser").value ;
	if(!assignUser) {
		alert("请选择一个办理人!") ;
		return ;
	}
	
	Ext.Ajax.request({
		method:'POST',
		url:'${pageContext.request.contextPath}/process.do?method=assignUser',
		success:function (response,options) {
			var result = response.responseText;
			
			if(result == "ok") {
				win.hide();
				goSearch_taskManagerList() ;
			}else {
				alert("后台发生异常,更改失败!") ;
			}
			
		},
		failure:function (response,options) {
			alert("网络超时,处理失败,请联系管理员!");
			return false ;
		},
		params:{
			taskId:taskId,
			assignUser:assignUser
		}
	});
	
}

 //双击修改
  function grid_dblclick(obj, tableId) {
  
  }
 
</script>
</html>