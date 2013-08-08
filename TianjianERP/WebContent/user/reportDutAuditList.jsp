<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
 
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>会议信息</title>
 
<Script type="text/javascript">
function ext_init(){ 
  
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
					var id =  document.getElementById("chooseValue_reportDutAuditList").value;
					if(id == "") {
						alert("请从待审核 任务标签页中选择一条审核 记录！");
						return ;
					} else {
						parent.parent.openTab("lookFlow","流程图","commonProcess.do?method=viewImageByTaskId&id="+id);
					}
				}
			},'-',{
				text:'打印',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/print.gif',
				handler:function () {
					print_reportDutAuditList();
				}
			},'-',{
				text:'查询',
	            icon:'${pageContext.request.contextPath}/img/query.gif',
				handler: queryWinFun
			},'->'
        ]
        });  
		


		//window 面板 进行查询
		var queryWin = null;
		function queryWinFun(id){
			resizable:false;
			var searchDiv = document.getElementById("search") ;
			searchDiv.style.display = "block" ;
			if(!queryWin) { 
			    queryWin = new Ext.Window({
					title: '入职审批查询',
					resizable:false,   //禁止用户 四角 拖动
					contentEl:'search',
			     	//renderTo : searchWin,
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
		          			goSearch_reportDutAuditList();
							queryWin.hide();
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
<body>

<div id="divBtn"></div>

<form id="thisForm" name="thisForm" method="post" action="">
<div style="height:expression(document.body.clientHeight-25);" >
<mt:DataGridPrintByBean name="reportDutAuditList"  />
</div>

<div id="searchWin"></div>
  
<div id="search" style="display:none">
	<table border="0" cellpadding="0" cellspacing="0" bgcolor="" align="center" style="margin-top: 20px;">
		 <tr>
			<td class="data_tb_alignright" align="right" >申请人：</td>
			<td class="data_tb_content"  nowrap="nowrap"><input type="text" id="userName" 
				maxlength="30"  name="userName"   />
				</td>
	    </tr>
		<tr>
			<td class="data_tb_alignright" align="right" >应聘职级：</td>
			<td class="data_tb_content"  nowrap="nowrap">
				<input type="text" id="leaveTypeId"  maxlength="30"  name="leaveTypeId"
						onkeydown="onKeyDownEvent();"
						onkeyup="onKeyUpEvent();"
						onclick="onPopDivClick(this);"
						noinput=true
						autoid=864
					   />
				</td>
	    </tr>
	    <tr>
			<td class="data_tb_alignright" align="right" >应聘部门：</td>
			<td class="data_tb_content"  nowrap="nowrap">
			<input name="department" id="department"  type="text" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();"  onClick="onPopDivClick(this);" valuemustexist=true autoid=4402>
				</td>
	    </tr>
	    <tr>
			<td class="data_tb_alignright" align="right" >学历：</td>
			<td class="data_tb_content"  nowrap="nowrap">
				<input type="text" id="educationl" maxlength="30"  name="educationl"   />
				</td>
	    </tr>
		</table>
</div>

</form>

</body>
</html>


<script type="text/javascript">

 function empty(){
	 	document.getElementById("userName").value="";
	 	document.getElementById("leaveTypeId").value="";
	 	document.getElementById("department").value="";
		document.getElementById("educationl").value="";
 }

//编辑
function goAudit(){
	var id = document.getElementById("chooseValue_reportDutAuditList").value;
	if(id==""){
		alert("请选择一项！");
		return ;
	}else{
		window.location = "${pageContext.request.contextPath}/job.do?method=auditSkip&taskId="+id; 
	}
}
</Script>


