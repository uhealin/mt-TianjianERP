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
					var id =  document.getElementById("chooseValue_meetingOrderListAudit").value;
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
				text:'刷新',
		   		icon:'${pageContext.request.contextPath}/img/refresh.gif',
		   		 handler:function(){
		   			empty();
		   			goSearch_meetingOrderListAudit();
		   		 }
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
					title: '会议审批查询',
					resizable:false,   //禁止用户 四角 拖动
					contentEl:'search',
			     	//renderTo : searchWin,
			     	width: 320,
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
		          			goSearch_meetingOrderListAudit();
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
<body>

<div id="divBtn"></div>

<form id="thisForm" name="thisForm" method="post" action="">
<div style="height:expression(document.body.clientHeight-23);" >
<mt:DataGridPrintByBean name="meetingOrderListAudit"  />
</div>

<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>
  
<div id="search" style="display:none">
	<table border="0" cellpadding="0" cellspacing="5" width="100%" bgcolor="">
		<tr><td colspan="4" height="20"></td></tr>
		<tr align="center">
			<td align="right" width="40%" nowrap="nowrap">会议名称：</td>
			<td align="left" colspan="3">
				<input type="text"
					   name="moname"
					   id="moname"
					   title="请输入有效的值" 
					     /> 
			</td>
		</tr>
		
		<tr align="center">
			<td align="right" >会议室名称：</td>
			<td align="left" colspan="3">
				<input type="text"
					   name="meetingName"
					   id="meetingName" 
					     /> 
			</td>
		</tr>
		
		<tr align="center">
			<td align="right" >会议开始时间：</td>
			<td align="left" colspan="3">
				<input type="text"
					   name="beginDate"
					   id="beginDate" 
					     /> 
			</td>
		</tr>
		
		<tr align="center">
			<td align="right" >会议结束时间：</td>
			<td align="left" colspan="3">
				<input type="text"
					   name="endDate"
					   id="endDate" 
					     /> 
			</td>
		</tr>
		 
	</table>
</div>

</form>

</body>
</html>


<script type="text/javascript">

new Ext.form.DateField({			
	applyTo : 'beginDate',
	width: 133,
	format: 'Y-m-d'	
});
 
new Ext.form.DateField({			
	applyTo : 'endDate',
	width: 133,
	format: 'Y-m-d'	
});
 
 function empty(){
	 document.getElementById("moname").value="";
	 document.getElementById("meetingName").value="";
	 document.getElementById("beginDate").value="";
	 document.getElementById("endDate").value="";
 }
 

// 提示是否需要打印 会议时间表格。
var uuId = "${uuId}";
/*
if(uuId!="" && uuId!="null" && uuId!=null){
	var name = getMeetingOrderNameById(uuId);
	if(confirm("请问您现在是否需要打印会议名称为："+name+" 的时间表格?")){
		var url = "${pageContext.request.contextPath}/meetingOrder.do?method=masterplatePrint&id="+uuId;
		myTemplatePrint(url);
		//window.open("${pageContext.request.contextPath}/meetingOrder.do?method=masterplatePrint&id="+uuId);
	} 
}
*/


// 得到会议名称
function getMeetingOrderNameById(id){
	var url = "${pageContext.request.contextPath}/meetingOrder.do?method=getMeetingOrderNameById";
	var requestString = "id=" + id;
	var result = ajaxLoadPageSynch(url,requestString);
	return result;
}


//编辑
function goAudit(){
	var id = document.getElementById("chooseValue_meetingOrderListAudit").value;
	if(id==""){
		alert("请选择一项！");
	}else{
		document.getElementById("thisForm").action = "${pageContext.request.contextPath}/meetingOrder.do?method=toAuidt&taskId="+id; 
		document.getElementById("thisForm").submit();
	}
}
 
function grid_dblclick(obj){
	
}
</Script>


