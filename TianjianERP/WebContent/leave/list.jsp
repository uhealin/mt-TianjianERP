<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>请假类型设置</title>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/searchSession.js" charset=gbk></script>

<script type="text/javascript">

Ext.onReady(function(){
	var tabs = new Ext.TabPanel({
	    renderTo: 'my-tabs',
	    activeTab: 0,
	    layoutOnTabChange:true, 
	    forceLayout : true,
	    deferredRender:false,
	    height: document.body.clientHeight-Ext.get('my-tabs').getTop()+14,
	    width : document.body.clientWidth, 
	    defaults: {autoWidth:true,autoHeight:true},
	    items:[
	        {contentEl:'tab1', title:'未发起', id:'cur1'},
	        {contentEl:'tab2', title:'已发起', id:'cur2'},
	        {contentEl:'tab3', title:'未销假', id:'cur3'},
	        {contentEl:'tab4', title:'已销假', id:'cur4'} 
	    ]
	});

	tabs.on("tabchange",function(tabpanel,tab) {
		if(tab.id == "cur1") {
			goSearch_leavList();			
		}else if(tab.id == "cur2") {
			goSearch_yiLeavList();			
		}else if(tab.id == "cur3") {
			goSearch_daiLeavList();			
		}else if(tab.id == "cur4") {
			goSearch_jieshuLeavList();			
		} 
	}) ;

	goSearch_leavList();
	
	var tbar_customer = new Ext.Toolbar({
		renderTo:'divBtn',
           items:[{
            text:'新增',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/add.gif',
            handler:function(){
            	goAdd();
			}
      	},'-',{
            text:'发起请假申请',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/start.png',
           	handler:function(){
 				goAudit();
 			}
         },'-',{
             text:'查看审核流程图',
             cls:'x-btn-text-icon',
             icon:'${pageContext.request.contextPath}/img/mytask.gif',
            	handler:function(){
            		viewImage("leaveFlow","");
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
			text:'查询',
			id:'btn-query',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:queryWinFun
		},'-',{
			text:'打印',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/print.gif',
			handler:function () {
				print_leavList();
			}
		},'->'
        ]
        });
	
	
	var tbar_customer2 = new Ext.Toolbar({
		renderTo:'divBtn2',
           items:[{
            text:'查看审核进度',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/mytask.gif',
           	handler:function(){
           		 lookImages("yiLeavList");
           	}
         },'-',{
             text:'请假作废',
             cls:'x-btn-text-icon',
             icon:'${pageContext.request.contextPath}/img/recycle.png',
            	handler:function(){
            		updateStatus("yiLeavList","发起人已作废","您确定要作废此条请假申请吗？","已发起");
  			}
          },'-',{
			text:'打印',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/print.gif',
			handler:function () {
				print_yiLeavList();
			}
		},'->'
        ]
        });
	
	
	var tbar_customer3 = new Ext.Toolbar({
		renderTo:'divBtn3',
           items:[{
            text:'查看审核流程图',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/mytask.gif',
           	handler:function(){
           		viewImage("destroyLeaveFlow","");
 			}
         },'-',{
             text:'发起销假',
             cls:'x-btn-text-icon',
             icon:'${pageContext.request.contextPath}/img/recycle.png',
            	handler:function(){
  				goDestroy();
  			}
          },'-',{
			text:'打印',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/print.gif',
			handler:function () {
				print_daiLeavList();
			}
		},'->'
        ]
        });

	var tbar_customer4 = new Ext.Toolbar({
		renderTo:'divBtn4',
           items:[{
            text:'查看审核进度',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/mytask.gif',
           	handler:function(){
           	 lookImages("jieshuLeavList");
 			}
         },'-',{
             text:'销假终止',
             cls:'x-btn-text-icon',
             icon:'${pageContext.request.contextPath}/img/recycle.png',
            	handler:function(){
            	updateStatus("jieshuLeavList","销假已终止","您确定要终止此条销假申请吗？","销假已发起");
  			}
          },'-',{
			text:'打印',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/print.gif',
			handler:function () {
				print_jieshuLeavList();
			}
		},'->'
        ]
        });

});

	
var queryWin = null;
function queryWinFun(id){
	// 设置时间 只读 
	var searchDiv = document.getElementById("search") ;
	searchDiv.style.display = "" ;
	if(!queryWin) { 
	    queryWin = new Ext.Window({
			title: '请假查询',
			contentEl:'search',
	     	renderTo : searchWin,
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
          			goSearch_leavList();
          			queryWin.hide();
            	}
        	},{
            	text:'清空',
          		handler:function(){
          			subClearSearch();
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

function lookImages (tableId){
	if(tableId ==""){
		return ;
	}
	var id = document.getElementById("chooseValue_"+tableId).value;
	if(id ==""){
		alert("请选择一项!");
		return;
	}

	var taskId = id.split("~");
	viewImage("","${pageContext.request.contextPath}/commonProcess.do?method=viewImageByTaskId&id="+taskId[1]);
	
}

function viewImage(key,url) {
	
	if(url ==""){
		var request = ajaxLoadPageSynch("${pageContext.request.contextPath}/leave.do?method=queryJbpmPdIdByKey","&key="+key+"&rendom="+Math.random());
		if(request == ""){
			return ;
		}
		url = "${pageContext.request.contextPath}/commonProcess.do?method=viewImageByDefinitionId&pdId="+request+"&rendom="+Math.random();
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

function updateStatus(tableId,status,msg,updateWhere) {
	
	if(tableId =="" || status ==""){
		return ;
	}
	var id = document.getElementById("chooseValue_"+tableId).value;
	if(id ==""){
		alert("请选择一项!"+"chooseValue_"+tableId);
		return;
	}
	var keyId = id.split("~");
	
	var url="${pageContext.request.contextPath}/leave.do?method=getStatus";
	 var requestString = "&uuid="+keyId[0];
	 var request = ajaxLoadPageSynch(url,requestString);
	 if(request != updateWhere){
		 alert("请假状态必须是"+updateWhere+"才能进行操作!");
		 return ;
	 }
	
	if(confirm(msg,"yes")){
		var ajaxUrl="${pageContext.request.contextPath}/leave.do?method=updateStatus";
		var ajaxString = "&uuid="+keyId[0]+"&status="+status+"&rendom="+Math.random();
		ajaxLoadPageSynch(ajaxUrl,ajaxString);
		eval("goSearch_"+tableId+"();"); //刷新dataGrid
	}
}

</script>

</head>
<body>
<div id="divprint">
<form name="thisForm" method="post" action="">

<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-210)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>

<div id="search" style="display:none;">
<br>
<br>
		<table border="0" cellpadding="0" cellspacing="0" bgcolor="" align="center">
		<tr>
			<td class="data_tb_alignright" align="right" >请假类型：</td>
			<td class="data_tb_content"  nowrap="nowrap">
				<input type="text" id="leaveTypeId"  maxlength="30"  name="leaveTypeId"
						onkeydown="onKeyDownEvent();"
						onkeyup="onKeyUpEvent();"
						onclick="onPopDivClick(this);"
						noinput=true
						autoid=865
					   />
				</td>
	    </tr>
	    <tr>
			<td class="data_tb_alignright" align="right" >开始时间：</td>
			<td class="data_tb_content"  nowrap="nowrap"><input type="text" id="leaveStartTime" 
				maxlength="30"  name="leaveStartTime"   />
				</td>
	    </tr>
	    <tr>
			<td class="data_tb_alignright" align="right" >结束时间：</td>
			<td class="data_tb_content"  nowrap="nowrap">
				<input type="text" id="leaveEndTime" maxlength="30"  name="leaveEndTime"   />
				</td>
	    </tr>
	    <tr>
			<td class="data_tb_alignright" align="right">请假原因：</td>
			<td class="data_tb_content"><input type="text" id="memo"
				maxlength="50" name="memo" /></td>
		</tr>
		</table>
</div>	
</form>
</div>
<div id="my-tabs">
	<div id="tab1">
		<div id="divBtn"></div>
		<div style="height:expression(document.body.clientHeight-58);">
			<mt:DataGridPrintByBean name="leavList"/>
		</div>
	</div>
	<div id="tab2">
		<div id="divBtn2"></div>
		<div style="height:expression(document.body.clientHeight-58);">
			<mt:DataGridPrintByBean name="yiLeavList"/>
		</div>
	</div>
	<div id="tab3">
		<div id="divBtn3"></div>
		<div style="height:expression(document.body.clientHeight-58);">
			<mt:DataGridPrintByBean name="daiLeavList"/>
		</div>
	</div>
	<div id="tab4">
		<div id="divBtn4"></div>
		<div style="height:expression(document.body.clientHeight-58);">
			<mt:DataGridPrintByBean name="jieshuLeavList"/>
		</div>
	</div>
</div>
<Script>
new Ext.form.DateField({			
	applyTo : 'leaveStartTime',
	width: 133,
	format: 'Y-m-d'	
});

new Ext.form.DateField({			
	applyTo : 'leaveEndTime',
	width: 133,
	format: 'Y-m-d'	
});


//清空 
function subClearSearch()
{
	document.thisForm.leaveTypeId.value="";
	document.thisForm.leaveStartTime.value="";
	document.thisForm.leaveEndTime.value="";
	document.thisForm.memo.value="";
}

// 添加 
function goAdd()
{
	window.location="${pageContext.request.contextPath}/leave.do?method=addSkip";
}
 
 
 //报废
 function goDestroy(){
	 var id = document.getElementById("chooseValue_daiLeavList").value;
	 var ids = id.split("~");
	 if(ids[0]==""){
			alert("请选择一项！");
			return ;
		}
		else{
				 var url="${pageContext.request.contextPath}/leave.do?method=getStatus";
				 var requestString = "&uuid="+ids[0];
				 var request = ajaxLoadPageSynch(url,requestString);
				 if(request != "已通过待销假"){
					 alert("请假状态必须是已通过待销假才能进行销假。");
					 return ;
				 }
				 window.location="${pageContext.request.contextPath}/leave.do?method=destroySkip&uuid="+ids[0];
		}
 }
 
 function goAudit(){
	 if(document.getElementById("chooseValue_leavList").value==""){
			alert("请选择一项！");
			return ;
		}
		else{
			if(confirm("您确定要发起此请假吗？发起后将无法再修改！")){
				 var url="${pageContext.request.contextPath}/leave.do?method=getStatus";
				 var requestString = "&uuid="+document.getElementById("chooseValue_leavList").value;
				 var request = ajaxLoadPageSynch(url,requestString);
				 if(request != "未发起"){
					 alert("请假状态必须是未发起才能进行发起。");
					 return ;
				 }
				 url="${pageContext.request.contextPath}/leave.do?method=startAudit";
				 requestString = "&uuid="+document.getElementById("chooseValue_leavList").value;
				 var result = ajaxLoadPageSynch(url,requestString);
				 alert(result);
				 goSearch_leavList();
			}	
		}
 }

// 删除 
function goDelete(){
	if(document.getElementById("chooseValue_leavList").value==""){
		alert("请选择一项！");
		return ;
	}
	else{
		if(confirm("您确定要删除此资料吗？删除后将无法恢复！")){
			 var url="${pageContext.request.contextPath}/leave.do?method=getStatus";
			 var requestString = "&uuid="+document.getElementById("chooseValue_leavList").value;
			 var request = ajaxLoadPageSynch(url,requestString);
			 if(request != "未发起"){
				 alert("请假状态必须是未发起才能删除操作。");
				 return ;
			 }
			window.location="${pageContext.request.contextPath}/leave.do?method=del&uuid="+document.getElementById("chooseValue_leavList").value;
		}	
	}
}


// 修改 
function goEdit()
{
	if(document.getElementById("chooseValue_leavList").value=="")
	{
		alert("请选择一项！");
		return ;
	}
	else
	{
		 var url="${pageContext.request.contextPath}/leave.do?method=getStatus";
		 var requestString = "&uuid="+document.getElementById("chooseValue_leavList").value;
		 var request = ajaxLoadPageSynch(url,requestString);
		 if(request != "未发起"){
			 alert("请假状态必须是未发起才能修改操作。");
			 return ;
		 }
		window.location="${pageContext.request.contextPath}/leave.do?method=updateSkip&uuid="+document.getElementById("chooseValue_leavList").value;
	}
}

//双击
function grid_dblclick(obj) {
	window.location="leave.do?method=viewNews&&autoId="+obj.autoId;
}

 
</script>
</body>
</html>


