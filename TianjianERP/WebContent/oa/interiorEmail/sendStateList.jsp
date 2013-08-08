<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>发件箱</title>

<script type="text/javascript">


function ext_init(){ 
	var tbar = new Ext.Toolbar({
		renderTo: 'divBtn',
		items:[{
			text:'查询',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
	   		 handler:queryWinFun
		},'-',{
			text:'返回',
	   		icon:'${pageContext.request.contextPath}/img/back.gif',
	   		 handler:function(){
 					window.location = "${pageContext.request.contextPath}/interiorEmail.do?method=sendList"+"&rand="+Math.random();
	   		 }
		}
        ]
     });  
	

	//window 面板 进行查询
	var queryWin = null;
	function queryWinFun(id){
		resizable:false;
		var searchDiv = document.getElementById("divCheck_select") ;
		searchDiv.style.display = "block" ;
		if(!queryWin) { 
		    queryWin = new Ext.Window({
				title: '邮件查询',
				resizable:false,   //禁止用户 四角 拖动
				contentEl:'divCheck_select',
		     	//renderTo : searchWin,
		     	width: 300,
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
	          			goSearch_sendEmailStateList();
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
<body >
<input type="hidden" id="uuid" name="uuid" value="${uuid}">
		<div id="divBtn"></div> 
		<div style="height:expression(document.body.clientHeight-28);width: 100%">
			<mt:DataGridPrintByBean name="sendEmailStateList" />
		</div>
		<div id="divCheck_select" style="display:none;width: 100%;height: 100%">
			<table>
				<tr>
					<td colspan="2" style="padding-top: 20px;padding-left: 20px;"><p>请输入查询条件：<hr>&nbsp;</td>
				</tr>
				<tr>
					<td width="40%" align="right">收件人：</td>
					<td><input type="text" name="userName" id="userName"></td>
				</tr>
				<tr>
					<td width="40%" align="right">所属部门：</td>
					<td><input type="text" name="departName" id="departName"></td>
				</tr>
			</table>
		</div>
		
</body>

<script type="text/javascript">

function empty(){
	document.getElementById("userName").value="";
	document.getElementById("departName").value="";
}
;
function goLookState(obj){
	if(obj !=""){
		var url = "${pageContext.request.contextPath}/interiorEmail.do?method=ajaxReadUser";
		var request= ajaxLoadPageSynch(url,"&uuid="+obj);
		if(request !=""){
			var listState = request.split("!");
			var msg = "";
			if(listState[0] !=""){
				msg +="未读人员：【"+listState[0]+"】。\n";
			}
			
			if(listState[1] !=""){
				msg +="已读人员：【"+listState[1]+"】。";
			}
			
			if(msg !=""){
				alert(msg);
			}
		}
	}
}
//立即发送
function goSend(userId,name,isRead){
	var uuid = document.getElementById("uuid").value;
	if(uuid == "" || userId == ""){
		 return ;
	 }
	var alMsg = "您确定要再次发送给【"+name+"】一份邮件吗？";
	if(isRead == "已读"){
		alMsg ="收件人已阅读此封邮件,"+alMsg;
	}
	if(confirm(alMsg,"yes")){
		
		var url = "${pageContext.request.contextPath}/interiorEmail.do?method=draftSend";
		var request= ajaxLoadPageSynch(url,"&uuid="+uuid+"&userId="+userId+"&rand="+Math.random());
		if(request == "true"){
			alert("发送成功!");
		}
		goSearch_sendEmailStateList();
	}
	
}
//撤销邮件
function goRepealSend(autoId,userId,isRead,name) {
	//alert(autoId+"|"+userId+"|"+isRead);
	if(autoId==""){
		return ;
	}else{
		if(isRead == "已读"){
			alert("收件人已阅读邮件，无法撤销!");
			return ;
		}
		if(confirm("您确定要撤销发送给【"+name+"】的邮件吗?","yes")){
			
			var url = "${pageContext.request.contextPath}/interiorEmail.do?method=ajaxRepealEmail";
			var request= ajaxLoadPageSynch(url,"&autoId="+autoId+"&rand="+Math.random());
			if(request == "true"){
				alert("撤销邮件成功!");
			}else{
				alert("撤销邮件失败!");
			}
			
			goSearch_sendEmailStateList();
		}
	}
}
//手机短信提醒
function goMbMsg(autoId,userId,isRead,name) {
	if(autoId==""){
		return ;
	}else{
		if(isRead == "已读"){
			alert("收件人已阅读邮件，无需发送短信!");
			return ;
		}
		var uuid = document.getElementById("uuid").value;
		if(uuid ==""){
			return ;
		}
		
		if(confirm("您确定要发送手机短信给【"+name+"】吗?","yes")){
			
			var url = "${pageContext.request.contextPath}/interiorEmail.do?method=ajaxMbMsg";
			var request= ajaxLoadPageSynch(url,"&uuid="+uuid+"&userId="+userId+"&rand="+Math.random());
		 	alert(request);
			goSearch_sendEmailStateList();
		}
	}
}
 
</script>
</html>