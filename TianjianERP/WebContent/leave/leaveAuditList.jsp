<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
 
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>会议信息</title>
 
<Script type="text/javascript">



Ext.onReady(function(){
	var tabs = new Ext.TabPanel({
	    renderTo: 'my-tab',
	    activeTab: 0,
	    layoutOnTabChange:true, 
	    forceLayout : true,
	    deferredRender:false,
	    height: document.body.clientHeight-Ext.get('my-tab').getTop()+14,
	    width : document.body.clientWidth, 
	    defaults: {autoWidth:true,autoHeight:true},
	    items:[
	        {contentEl:'tab1', title:'未审批', id:'cur1'},
	        {contentEl:'tab2', title:'已审批', id:'cur2'}
	    ]
	});

	tabs.on("tabchange",function(tabpanel,tab) {
		if(tab.id == "cur2") {
			goSearch_alreadyLeavList();			
		}
	}) ;
		//未审批
		
		goSearch_leaveAuditList();

		var tbar = new Ext.Toolbar({
		renderTo: 'divBtn',
		items:[
			{
				text:'审核',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/edit.gif',
				handler:function () {
					goAudit();
				}
			},'-',{
				text:'查看流程图',
	            icon:'${pageContext.request.contextPath}/img/query.png',
				handler:function () {
					var id =  document.getElementById("chooseValue_leaveAuditList").value;
					if(id == "") {
						alert("请从待审核 任务标签页中选择一条审核 记录！");
						return ;
					} else {
						parent.parent.openTab("lookFlow","流程图","commonProcess.do?method=viewImageByTaskId&id="+id);
					}
				}
			},'-',{
				text:'查询',
	            icon:'${pageContext.request.contextPath}/img/query.gif',
				handler: queryWinFun
			},'-',{
				text:'打印',
				cls:'x-btn-text-icon',
				icon:'${pageContext.request.contextPath}/img/print.gif',
				handler:function () {
					print_leaveAuditList();	}			
			},'->'
        ]
        }); 
        
        var tbar = new Ext.Toolbar({
		renderTo: 'divBtn2',
		items:[
			{
				text:'查询',
	            icon:'${pageContext.request.contextPath}/img/query.gif',
				handler: queryWinFun1
			},'-',{
				text:'打印',
				cls:'x-btn-text-icon',
				icon:'${pageContext.request.contextPath}/img/print.gif',
				handler:function () {
					print_alreadyLeavList();	
				}			
			},'->'
        ]
        });   
});


		//window 面板 进行查询
		var queryWin = null;
		function queryWinFun(id){
			resizable:false;
			var searchDiv = document.getElementById("search") ;
			searchDiv.style.display = "block" ;
			if(!queryWin) { 
			    queryWin = new Ext.Window({
					title: '请假审批查询',
					resizable:false,   //禁止用户 四角 拖动
					contentEl:'search',
			     	renderTo : searchWin,
			     	width: 320,
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
		          			goSearch_leaveAuditList();
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

window.attachEvent('onload',ext_init);

//已审批的查询
var queryWin = null;
		function queryWinFun1(id){
			resizable:false;
			var searchDiv = document.getElementById("search") ;
			searchDiv.style.display = "block" ;
			if(!queryWin) { 
			    queryWin = new Ext.Window({
					title: '请假审批查询',
					resizable:false,   //禁止用户 四角 拖动
					contentEl:'search',
			     	renderTo : searchWin,
			     	width: 320,
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
		          			goSearch_alreadyLeavList();
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

window.attachEvent('onload',ext_init);


</script>
 
 
</head>
<body>
<div id="my-tab">

	<div id="tab1">
		<div id="divBtn"></div> 
		<div style="height:expression(document.body.clientHeight-28);" >
			<mt:DataGridPrintByBean name="leaveAuditList" />
		</div>
	</div> 
	<div id="tab2">
		<div id="divBtn2"></div> 
		<div style="height:expression(document.body.clientHeight-28);" >
			<mt:DataGridPrintByBean name="alreadyLeavList" />
		</div>
	</div> 
</div>
<div id="divBtn"></div>

<form id="thisForm" name="thisForm" method="post" action="">
<div style="height:expression(document.body.clientHeight-28);" >
<mt:DataGridPrintByBean name="leaveAuditList"  />
</div>

<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>
  
<div id="search" style="display:none">
	<table border="0" cellpadding="0" cellspacing="0" bgcolor="" align="center" style="margin-top: 20px;">
		 <tr>
			<td class="data_tb_alignright" align="right" >申请人：</td>
			<td class="data_tb_content"  nowrap="nowrap"><input type="text" id="userName" 
				maxlength="30"  name="userName"   />
				</td>
	    </tr>
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

</body>
</html>


<script type="text/javascript">

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
 
 function empty(){
	 	document.getElementById("userName").value="";
	 	document.getElementById("leaveTypeId").value="";
	 	document.getElementById("leaveStartTime").value="";
		document.getElementById("leaveEndTime").value="";
		document.getElementById("memo").value="";
 }

//编辑
function goAudit(){
	var id = document.getElementById("chooseValue_leaveAuditList").value;
	if(id==""){
		alert("请选择一项！");
		return ;
	}else{
		window.location = "${pageContext.request.contextPath}/leave.do?method=auditSkip&taskId="+id; 
	}
}
 
function grid_dblclick(obj){
	
}
</Script>


