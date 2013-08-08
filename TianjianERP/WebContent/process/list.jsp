<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript">
//EXT初始化
function ext_init(){
	new Ext.Toolbar({
		renderTo:'GridDiv_processDefineList',
		items:[
			{ 
				text:'新增',
				icon:'${pageContext.request.contextPath}/img/add.gif' ,
				handler:function(){
					add();
				}
			},'-',{ 
				text:'修改',
				icon:'${pageContext.request.contextPath}/img/edit.gif' ,
				handler:function(){
					edit();
				}
			},'-',{ 
	               text:'流程设计器',
	               icon:'${pageContext.request.contextPath}/img/design.png' ,
	               handler:function(){
	            	   var value=document.getElementById('chooseValue_processDefineList').value;
          	       	    if(value==''){
          	       	    	alert('请先选择要修改的流程!');
          	       	    	return;
          	       	    }
          	       	    var trValueObj = document.getElementById("trValueId_"+value) ;
          	       	    var pkey = trValueObj.pkey ; 
	          	       	var tab = parent.tab ;
	          	       	var url = "${pageContext.request.contextPath}/process.do?method=designer&id="+pkey ;
	        	        if(tab){
	        				n = tab.add({    
	        					title:"流程设计器",    
	        					closable:true,  //通过html载入目标页    
	        					html:'<iframe name="designFrm" scrolling="no" frameborder="0" width="100%" height="100%" src="' + url + '"></iframe>'   
	        				}); 
	        		        tab.setActiveTab(n);
	        			}else {
	        				window.open(url);
	        			}		
							          	       	    
	   			   }
	           },'-',{ 
				text:'删除',
				icon:'${pageContext.request.contextPath}/img/delete.gif' ,
				handler:function(){
					remove();
				}
			},'-',{ 
                text:'启动流程',
                icon:'${pageContext.request.contextPath}/img/start.png' ,
                handler:function(){
                	startProcess();
    			}
          	},'-',{ 
                text:'发起申请',
                icon:'${pageContext.request.contextPath}/img/start.png' ,
                handler:function(){
                	apply();
    			}
          	},'-',{ 
                text:'查看流程图',
                icon:'${pageContext.request.contextPath}/img/query.gif' ,
                handler:function(){
                	viewImage();
    			}
          	},
          	'-',{ 
                text:'上传流程定义文件',
                icon:'${pageContext.request.contextPath}/img/start.png' ,
                handler:function(){
                	deploy();
    			}
          	},'-',{ 
				text:'查询',
				icon:'${pageContext.request.contextPath}/img/query.gif' ,
				handler:function(){
					queryWinFun();
				}
			},'-',{ 
				text:'全部重新发布',
				icon:'${pageContext.request.contextPath}/img/start.png' ,
				handler:function(){
					batchDeploy();
				}
			},'-',{ 
				text:'查看所有',
				icon:'${pageContext.request.contextPath}/img/refresh.gif' ,
				handler:function(){
			    	window.location = "${pageContext.request.contextPath}/process.do?method=processDefineList";
				}
			}
		]
	});
}
var queryWin = null;
function queryWinFun() {
	document.getElementById("search").style.display = "";
	if(queryWin == null) { 
	    queryWin = new Ext.Window({
			title: '查询',
			width: 430,
			height:180,
			contentEl:'search', 
	        closeAction:'hide',
	        modal:true,
	        listeners:{
				'hide':{fn: function () {
					 document.getElementById("search").style.display = "none";
				}}
			}, 
	        layout:'fit',
		    buttons:[{
	            text:'确定',
	            icon:'${pageContext.request.contextPath}/img/confirm.gif',
	          	handler:function() {
	        		 if (!formSubmitCheck('thisForm')) {
	          			return;
	          		}
	          		goSearch_processDefineList(2);
	          		queryWin.hide();
	          	}
	        },{
	            text:'重置',
	            icon:'${pageContext.request.contextPath}/img/refresh.gif',
	            handler:function(){
	            	reset("thisForm");
	            }
	        },{
	            text:'取消',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function(){
	            	queryWin.hide();
	            }
	        }]
	    });
	}

	queryWin.show();
}
window.attachEvent('onload',ext_init);
</script>
</head>

<body>

<div class="autoHeightDiv">
	<mt:DataGridPrintByBean name="processDefineList" />
</div>

<div id="search" style="display: none;">
	<form name="thisForm" id="thisForm" method="post">
	<input type="hidden" id="dic_id" name="dic_id">
	<input type="hidden" id="addId" name="addId" value="${addId}">
	
		<br/>
		<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
		<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
		
		<table border="0" cellpadding="5" cellspacing="10" width="100%" align="center">
		     <tr>
				<td align="right">流程名称： </td>
				<td><input type="text" name="processName" id="processName" width="15"/>
				  </td>
			</tr>
		</table>
	</form>		
</div>
</body>

<script type="text/javascript">
//新增
function add(){
	window.location = "${pageContext.request.contextPath}/process.do?method=addAndEdit";	
}

//修改
function edit(){
	var chooseValue = document.getElementById("chooseValue_processDefineList").value;
	if(chooseValue=="") {
		alert("请选择需要修改的流程");
		return;
	} else {
		window.location = "${pageContext.request.contextPath}/process.do?method=addAndEdit&id=" + chooseValue;
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

function deploy() {
	var id = document.getElementById("chooseValue_processDefineList").value;
	var processKey = document.getElementById("trValueId_"+id).pkey;
	if(processKey == "") { 
		alert("流程定义key为空,不能上传流程文件!");
	} else {
		window.location = "${pageContext.request.contextPath}/process/description.jsp?processKey="+processKey ;
	}
}

 //双击修改
  function grid_dblclick(obj, tableId) {
  
    var id=obj.pkey;
    if(id==""){
 		 return ;
 	}else{
	  window.location = "${pageContext.request.contextPath}/process.do?method=edit&id=" + id;
	}
   }
 
 
  function startProcess() {
		
	var id = document.getElementById("chooseValue_processDefineList").value;
	var processKey = document.getElementById("trValueId_"+id).pkey;
	
	if(processKey == "") {     
		alert("流程定义key为空,不能上传流程文件!");
	} else {
		Ext.Ajax.request({
			method:'POST',
			url:'${pageContext.request.contextPath}/process.do?method=processExist&key='+processKey,
			success:function (response,options) {
				 var pId = response.responseText ;
				 if(pId != "") {
					window.location = "${pageContext.request.contextPath}/process.do?method=startProcess&pdId="+pId ;
				 }else {
					alert("流程定义尚未发布，不能发起新的流程！");
				 }
			},
			failure:function (response,options) {
				alert("后台出现异常,暂不能发起申请!");
			}
		});
	}
 }
  
  function apply() {
	  var id = document.getElementById("chooseValue_processDefineList").value;
	  var processKey = document.getElementById("trValueId_"+id).pkey;
	  window.location = "${pageContext.request.contextPath}/process.do?method=processTransfer&pKey="+processKey ;
  }
  
  
  function viewImage() {
		
	var id = document.getElementById("chooseValue_processDefineList").value;
	
	if(id == "") {
		alert("请选择一条流程记录!") ;
		return ;
	}
	
	var trValue = document.getElementById("trValueId_"+id);
	
	var pdId = trValue.pdid ;
	
	if(pdId == "") {
		alert("流程尚未发布,不能显示流程图!") ;
		return ;
	}
	var tab = parent.tab ;
     if(tab){
		n = tab.add({    
			'title':"流程图",    
			closable:true,  //通过html载入目标页    
			html:'<iframe name="fdfd" scrolling="auto" frameborder="0" width="100%" height="100%" src="${pageContext.request.contextPath}/process.do?method=viewImageByPIdOrKey&key='+id+'"></iframe>'   
		}); 
        tab.setActiveTab(n);
	}
}
  
  
  function batchDeploy(){
	  showWaiting("流程正在发布...") ;
	  var url = '${pageContext.request.contextPath}/process.do?method=batchDeploy' ;
	  var text = ajaxLoadPageSynch(url) ;
	  
	  if(text == "ok") {
		 alert("流程批量发布成功！");
	  }else {
		alert(text);
	  }
	  stopWaiting();
  }
</script>
</html>