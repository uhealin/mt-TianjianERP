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
		},'-',
		{
           text:'修改',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/edit.gif',
           handler:function(){
				goEdit();
		   }
        },'-',
		{
           text:'删除',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/delete.gif',
           handler:function(){
				goDelete();
		    }
        },'-',{
			text:'查询',
            icon:'${pageContext.request.contextPath}/img/query.gif',
			handler: queryWinFun
		},'-',
		/*
        {
           text:'打印',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/print.gif',
           handler:function(){
				goPrint();
		   }
        },'-',
        */
        {
			text:'刷新',
	   		icon:'${pageContext.request.contextPath}/img/refresh.gif',
	   		 handler:function(){
	   			empty();
	   			goSearch_meetingOrderList();
	   		 }
		},'-',
		/*
		{
            text:'关闭',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/close.gif',
            handler:function () {
           		closeTab(parent.tab);
            }
        },'-',
        */
        {
            text:'终止流程',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/close.gif',
            handler:function () {
           		stopFlow();
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
					title: '会议室预约查询',
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
		          			goSearch_meetingOrderList();
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
<mt:DataGridPrintByBean name="meetingOrderList"  />
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
	editable:false,
	format: 'Y-m-d'	
});
 
new Ext.form.DateField({			
	applyTo : 'endDate',
	width: 133,
	editable:false,
	format: 'Y-m-d'	
});

function goSearch(){
	queryWin.hide();
    goSearch_meetingOrderList(); 
} 


function empty(){
	 document.getElementById("moname").value="";
	 document.getElementById("meetingName").value="";
	 document.getElementById("beginDate").value="";
	 document.getElementById("endDate").value="";
}


// 清空
function goClear(){
	document.getElementById("title").value="";
	document.getElementById("organ").value="";
	document.getElementById("place").value="";
	document.getElementById("device").value="";
	document.getElementById("describes").value="";
	
	queryWin.hide();
    goSearch_meetingOrderList(); 
    
}

// 添加
function goAdd(){
	document.getElementById("thisForm").action = "${pageContext.request.contextPath}/meetingOrder.do?method=go&opt=add"; 
	document.getElementById("thisForm").submit();
}

//编辑
function goEdit(){
	
	 var id = document.getElementById("chooseValue_meetingOrderList").value; 
	
	 if(id == ""){
		 alert("请选择一项！");
		 return ;
	 }else{
		 
		 var url="${pageContext.request.contextPath}/meetingOrder.do?method=getStatus";
		 var requestString = "&uuid="+id;
		 var request= ajaxLoadPageSynch(url,requestString);
		 if(request !="未发起"){
			 alert("您的会议室预约状态是"+request+"，不能进行修改!");
			 return ;
		 }else{
				 document.getElementById("thisForm").action = "${pageContext.request.contextPath}/meetingOrder.do?method=go&opt=update&id="+id; 
				 document.getElementById("thisForm").submit();
		 }
	 }
	//var rs = getAuditStatus(id);
	//if(rs=="Y"){
	//	alert("已审核通过,不能删除！");
	//	return;
	//}else{
	//	if(id==""){
	//		alert("请选择一项！");
	//	}else{
	//		document.getElementById("thisForm").action = "${pageContext.request.contextPath}/meetingOrder.do?method=go&opt=update&id="+id; 
	//		document.getElementById("thisForm").submit();
	//	}
	//}
}
 
// 条件查询
var queryWin = null;
function queryWinFun(id){
	var searchDiv = document.getElementById("search") ;
	searchDiv.style.display = "" ;
	if(!queryWin) { 
	    queryWin = new Ext.Window({
			title: '菜单查询',
			contentEl:'search',
	     	renderTo : searchWin,
	     	width: 620,
	     	height:270,
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
          			goSearch();
            	}
        	},{
            	text:'清空',
          		handler:function(){  
          			goClear();
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



function grid_dblclick(obj){
	
}

function stopFlow(){
	 var id = document.getElementById("chooseValue_meetingOrderList").value; 
	 if(id == ""){
		 alert("请选择一项！");
		 return ;
	 }else{
		 
		 var url="${pageContext.request.contextPath}/meetingOrder.do?method=getStatus";
		 var requestString = "&uuid="+id;
		 var request= ajaxLoadPageSynch(url,requestString);
		 
		 if(request ="已发起"){
			alert("您的会议室预约状态是"+request+"，确定要终止？");
			window.location="meetingOrder.do?method=delete&&id="+id;
		 }
	 }
}
// 删除
function goDelete(){
	 var id = document.getElementById("chooseValue_meetingOrderList").value; 
	
	 if(id == ""){
		 alert("请选择一项！");
		 return ;
	 }else{
		 
		 var url="${pageContext.request.contextPath}/meetingOrder.do?method=getStatus";
		 var requestString = "&uuid="+id;
		 var request= ajaxLoadPageSynch(url,requestString);
		 
		 if(request !="未发起"){
			 alert("您的会议室预约状态是"+request+"，不能进行删除!");
			 return ;
		 }else{
			 if(confirm("是否确定删除该会议室吗？","提示")){
				window.location="meetingOrder.do?method=delete&&id="+id;
			}
		 }
		 
	 }
	 
	//var rs = getAuditStatus(id);
	//if(rs=="Y"){
	//	alert("已审核通过,不能删除！");
	//}else{
	//	if(id==""){
	//		alert("请选择一项！");
	//	}else{
	//		if(confirm("是否确定删除该会议室吗？","提示")){
	//			window.location="meetingOrder.do?method=delete&&id="+id;
	//		}
	//	}
	//}
}

// 得到状态
function getAuditStatus(id){
	var url = "${pageContext.request.contextPath}/meetingOrder.do?method=getStatus";
	var requestString = "id=" + id;
	var result = ajaxLoadPageSynch(url,requestString);
	return result;
}

//启动流程
function goFlow(){
	var uuid = document.getElementById("chooseValue_meetingOrderList").value;
	
	if(uuid=="")
	{
		alert("请选择要发起的会议室预约！");
		return ;
	}
	else
	{
			 var url="${pageContext.request.contextPath}/meetingOrder.do?method=getStatus";
			 var requestString = "&uuid="+uuid;
			 var request= ajaxLoadPageSynch(url,requestString);
			
			 if(request =="已发起"){
				 alert("您的会议室预约状态是"+request+"，不能在次发起!");
				
				 return ;
			 }
			 if(request == "已通过"){
				 alert("您的会议室预约状态是"+request+"，不能在次发起!");
				
				 return ;
			 }
			 if(request == "不通过"){
				 alert("您的会议室预约状态是"+request+"，不能在次发起!");
				
				 return ;
			 }
			 
			 alert(request);
		//if(confirm("确定要发起此会议室预约吗？发起后将无法进行修改","yes")){
			if(confirm("确定要发起此会议室预约吗？","yes")){
			 url="${pageContext.request.contextPath}/meetingOrder.do?method=updateStatu";
			 
			 requestString = "&uuid="+uuid;
			 request= ajaxLoadPageSynch(url,requestString);
			
			 if(request == "true"){
				goSearch_meetingOrderList(); 
				alert("发起成功！");
				return ;
			 }
				alert("发起失败！");
		}
	}
}

// 打印
function goPrint(){
	var id = document.getElementById("chooseValue_meetingOrderList").value;
	if(id==""){
		alert("请选择一项！");
	}else{
		//window.open("${pageContext.request.contextPath}/meetingOrder.do?method=goPrint&id="+id);  //旧的打印
		//myOpenUrlByWindowOpen("${pageContext.request.contextPath}/meetingOrder.do?method=masterplatePrint&id="+id,'',''); 
		//新的模版打印
		var url = "${pageContext.request.contextPath}/meetingOrder.do?method=masterplatePrint&id="+id;
		myTemplatePrint(url);

	}
}

</Script>


