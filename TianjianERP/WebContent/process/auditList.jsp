<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="../hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<title>流程任务列表</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<script type="text/javascript">  

var mytab ;
function ext_init(){
	
	 new Ext.Toolbar({
   		renderTo: "divBtn",
   		height:30,
           items:[{ 
               text:'审批',
               icon:'${pageContext.request.contextPath}/img/start.png' ,
               handler:function(){
   				  audit();
   			   }
         	},'-',{ 
                text:'查询',
                icon:'${pageContext.request.contextPath}/img/query.gif' ,
                handler:function(){
                	queryWinFun(1);
    			}
          	},'-',{ 
                text:'查看流程图',
                icon:'${pageContext.request.contextPath}/img/query.gif' ,
                handler:function(){
                	viewImage2();
    			}
          	},'-',{ 
            text:'关闭',
            icon:'${pageContext.request.contextPath}/img/close.gif',
            handler:function(){
            	closeTab(parent.mainTab);
			}
      	},'->']
	});
	 
	 new Ext.Toolbar({
	   		renderTo: "divBtn2",
	   		height:30,
	        items:[{ 
	               text:'查看',
	               icon:'${pageContext.request.contextPath}/img/query.gif' ,
	               handler:function(){
	   				  view();
	   			   }
	         	},'-',{ 
	                text:'查询',
	                icon:'${pageContext.request.contextPath}/img/query.gif' ,
	                handler:function(){
	                	queryWinFun(1);
	    			}
          		},'-',{ 
	            text:'关闭',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function(){
	            	closeTab(parent.mainTab);
				}
	      	},'->']
		});
	 
	 mytab = new Ext.TabPanel({  
        id: "tab",
        renderTo: "divTab",
        activeTab:0, //选中第一个 tab
        layoutOnTabChange:true, 
        forceLayout : true,
        deferredRender:false,
        height: document.body.clientHeight-Ext.get('divTab').getTop(),
        width : document.body.clientWidth, 
        items:[
            {contentEl: "auditList",title: "待办理任务",id:"audit"},
            {contentEl: "auditedList",title: "已办理任务",id:"audited"}
        ]
    });
	
	
	 var isRefresh = false ;
     mytab.on("tabchange",function(tabpanel,tab) {
    	if(tab.id == "audited") {
			if(!isRefresh) {
				clearSearch();
			}
			isRefresh = true ; 
		}
    }) ;
	
}

var queryWin = null;
function queryWinFun() {
	
	document.getElementById("search").style.display = "";
	if(queryWin == null) { 
	    queryWin = new Ext.Window({
			title: '流程查询',
			width: 400,
			height:250,
			contentEl:'search', 
	        closeAction:'hide',
	        listeners:{
				'hide':{fn: function () {
					 document.getElementById("search").style.display = "none";
				}}
			},
	        layout:'fit',
	        modal:true,
		    buttons:[{
	            text:'确定',
	          	handler:function() {
	          		goSearch() ;
	          		queryWin.hide();
	          	}
	        },{
	            text:'清空',
	            handler:function(){
	            	clearSearch();
	            }
	        },{
	            text:'取消',
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
<body >


<div id="divTab" style="overflow:auto">

	<div id="auditList" style="height:expression(document.body.clientHeight-62);width:100%">
		<div id="divBtn" ></div>
		<mt:DataGridPrintByBean name="auditList_${pKey}"  />
	</div>
	
	<div id="auditedList" class="x-hide-display" style="height:expression(document.body.clientHeight-62);width:100%">
		<div id="divBtn2" ></div> 
		<mt:DataGridPrintByBean name="auditedList_${pKey}" outputData="false" outputType="invoikSearch"/>
	</div>
	
</div>



<div id="search" style="display: none;">
<br/>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
	<table border="0" cellpadding="0" cellspacing="10" width="100%">
	
		<tr>
			<td align="right">流程名称：</td>
			<td align=left>
				<input type="text"
						id="pName"
						name="pName">
			</td>
		</tr>
		<tr>
			<td align="right">节点名称：</td>
			<td align=left>
				<input type="text"
						id="nodeName"
						name="nodeName">
			</td>
		</tr>
		
        
         <tr>
			<td align="right">申请人：</td>
			<td align=left>
				<input type="text"
						id="applyUserName"
						name="applyUserName">
			</td>
		 </tr>
	</table>
</div>

</body>
</html>

<script language="javascript">

	
	function audit() {
		var id = document.getElementById("chooseValue_auditList_${pKey}").value;
		if(id == "") {
			alert("请从待审核 任务标签页中选择一条审核 记录！");
		} else {
			var processKey = document.getElementById("trValueId_"+id).pkey;
			window.location = "${pageContext.request.contextPath}/process.do?method=processTransfer&taskId=" + id + "&pKey="+processKey;
		}
	}
	
	
	function viewImage2() {
		var id = document.getElementById("chooseValue_auditList_${pKey}").value;
		if(id == "") {
			alert("请从待审核 任务标签页中选择一条审核 记录！");
		} else {
			var tab = parent.mainTab ;
	        if(tab){
				n = tab.add({    
					'title':"流程图",    
					closable:true,  //通过html载入目标页    
					html:'<iframe name="imageFrm" scrolling="auto" frameborder="0" width="100%" height="100%" src="${pageContext.request.contextPath}/process.do?method=viewImageByTaskId&id='+id+'"></iframe>'   
				}); 
		        tab.setActiveTab(n);
			}else {
				window.open('${pageContext.request.contextPath}/process.do?method=viewImageByTaskId&id='+id);
			}		
		}
	}
	
	
	function clearSearch() {
		document.getElementById("pName").value = "";
		document.getElementById("nodeName").value = "";
		document.getElementById("applyUserName").value = "";
		goSearch() ;
	}
	
	function goSearch() {
		var tab = mytab.getActiveTab();
		if(tab.id == "audit") {
			goSearch_auditList_${pKey}();
		}else if(tab.id == "audited") {
			goSearch_auditedList_${pKey}();
		}
	}
	
	function view(){
		
		var id = document.getElementById("chooseValue_auditedList_${pKey}").value;
		if(id == "") {
			alert("请选择一条处理记录！");
			return ;
		} 
		
		var processKey = document.getElementById("trValueId_"+id).pkey;
		window.location = "${pageContext.request.contextPath}/process.do?method=processTransfer&view=true&pId=" + id ;
		
	}
	
	function grid_dblclick(obj, tableId) {
		
		if(tableId == "auditList_${pKey}") {
			var pKey = obj.pkey ;
			var taskId = obj.taskId ;
			
			window.location = "${pageContext.request.contextPath}/process.do?method=processTransfer&taskId=" + taskId + "&pKey="+pKey;
		}else if(tableId == "auditedList_${pKey}") {
			
			var pId = obj.pId ;
			
			window.location = "${pageContext.request.contextPath}/process.do?method=processTransfer&view=true&pId=" + pId ;
		}
		 
	}
		

</script>