<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>商机录入</title>

<script type="text/javascript">  

function goEdit() {
	if(document.getElementById("chooseValue_CustomerBusiness").value=="") {
		alert("请选择要修改的客户！");
	} else {
		window.location="customer.do?method=businessAdd&autoid="+document.getElementById("chooseValue_CustomerBusiness").value;
	}
}

function goDel() {
	if(document.getElementById("chooseValue_CustomerBusiness").value=="") {
		alert("请选择要删除的客户！");
	} else {
		if(confirm("您的操作可能会造成数据丢失，您确定要删除该记录吗？","提示")){
			window.location="customer.do?method=businessDel&autoid="+document.getElementById("chooseValue_CustomerBusiness").value;
		}
	}
}

function ext_init(){
	
	var tbar_levellist = new Ext.Toolbar({
		renderTo: 'gridDiv_CustomerBusiness',
		items:[{
	            text:'新增',
	            cls:'x-btn-text-icon',
	            icon:'/AuditSystem/img/add.gif',
	            handler:function(){
	            	window.location = "${pageContext.request.contextPath}/customer.do?method=businessAdd";
				}
       		},'-',{
	            text:'修改',
	            cls:'x-btn-text-icon',
	            icon:'/AuditSystem/img/edit.gif',
	            handler:function(){
	            	goEdit();
				}
       		},'-',{
	            text:'删除',
	            cls:'x-btn-text-icon',
	            icon:'/AuditSystem/img/delete.gif',
	            handler:function(){
	            	goDel();
				}
       		},'-',{
			text:'打印',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/print.gif',
			handler:function () {
				print_CustomerBusiness();
			}
		},'-',{
			text:'批量导入',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/excel.gif',
			handler:function () {
				 window.location = "${pageContext.request.contextPath}/customer.do?method=businessUploadSkip";
			}
		},'-',{
			text:'查看',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:function () {
					var autoid = document.getElementById("chooseValue_CustomerBusiness").value;
					if(autoid ==""){
						alert("请选择一项");
						return ;
					}
					window.location="${pageContext.request.contextPath}/customer.do?method=businessLook&autoid="+autoid;
			}
		},'-',{
			text:'发起招投标登记',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/start.png',
			handler:function () {
				if(getState()!="待审核"){
					alert("不能再次发起!");
					return;
				}
				start("0","招投标登记");
				
			}
		},'-',{
			text:'发起客户承接登记',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/start.png',
			handler:function () {
				if(getState()!="待审核"){
					alert("不能再次发起!");
					return;
				}
				start("1","客户承接登记");
			}
		},'-',{
			text:'失败',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/taskState_1.gif',
			handler:queryWinFun
		},'-',{
			text:'关闭',
			icon:'${pageContext.request.contextPath}/img/close.gif',
           	handler:function(){
           		closeTab(parent.tab);
			}
		}]
	});
	
	
	//window 面板 进行查询
	var queryWin = null;
	function queryWinFun(id){
		var autoid = document.getElementById("chooseValue_CustomerBusiness").value;
		if(autoid ==""){
			alert("请选择一项");
			return ;
		}
		resizable:false;
		var searchDiv = document.getElementById("divCheck_select") ;
		searchDiv.style.display = "block" ;
		if(!queryWin) { 
		    queryWin = new Ext.Window({
				title: '失败原因',
				resizable:false,   //禁止用户 四角 拖动
				contentEl:'divCheck_select',
		     	//renderTo : searchWin,
		     	width: 400,
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
	            	text:'确定',
	            	handler:function(){
	            		
	            		var result = document.getElementById("result").value;
	            		if(result ==""){
	            			alert("请填写失败原因!");
	            			return;
	            		}
						if(confirm("您确定要把这个商机标记为失败吗？","yes")){
							
		            		var url="${pageContext.request.contextPath}/customer.do?method=businessFail";
		            		var requestString = "&result="+result+"&autoId="+autoid;
		            		var request= ajaxLoadPageSynch(url,requestString);
		            		if(request !=""){
		            			queryWin.hide();
								alert("已标记物失败！");
								goSearch_CustomerBusiness();
								return;
		            		}
						}	            		
	            		
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


function grid_dblclick(trObj,tableId) {

}
window.attachEvent('onload',ext_init);


function getState(){
	var autoid = document.getElementById("chooseValue_CustomerBusiness").value;
	if(autoid ==""){
		alert("请选择一项");
		return ;
	}
	 var url="${pageContext.request.contextPath}/customer.do?method=businessState";
	 var requestString = "&autoId="+autoid;
	 var request= ajaxLoadPageSynch(url,requestString);
	 
	 return request;
}

function start(obj,name){
	var autoid = document.getElementById("chooseValue_CustomerBusiness").value;
	if(autoid ==""){
		alert("请选择一项");
		return ;
	}
	if(confirm("您确定要发起"+name,"yes")){
		
		parent.openTab("startContinueApply",name,"customer.do?method=startBidOrContinue&autoId="+autoid+"&opt="+obj);
	}
}

</script>

</head>
<body >

<input type="hidden" name="customerid" id="customerid" value="${customerid}">

<div style="height:expression(document.body.clientHeight-23);" >
<mt:DataGridPrintByBean name="CustomerBusiness"  />
</div>
	<div id="divCheck_select">
		<table style="width: 100%;height: 100%">
			<tr>
				<td style="width: 20%;" align="right">失败原因：</td>
				<td>
					<textarea style="width: 300px;height:130px;overflow: visible;" id="result" name="result"></textarea>
				</td>
			</tr>
		</table>
		
	</div>

</body>
</html>
