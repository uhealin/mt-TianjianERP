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
			text:'查看',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
	   		 handler:function(){
	   		 	var uuid = getUuid(1);
	   			//var uuid = document.getElementById("chooseValue_sendList").value;
	   			if(uuid==""){
	   				return ;
	   			}else{
	   				window.location="${pageContext.request.contextPath}/interiorEmail.do?method=read&uuid="+uuid+"&isReadOnly=sendList&readUser=look&back=sendList";
	   			}
	   		 }
		},'-',{
			text:'查询',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
	   		 handler:queryWinFun
		},'-',{
			text:'查看阅读状态',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
	   		 handler:function(){
	   			var uuid = getUuid(1);
	   			if(uuid==""){
	   				return ;
	   			}else{
   					window.location="${pageContext.request.contextPath}/interiorEmail.do?method=sendEmailStateList&uuid="+uuid+"&randomId="+Math.random();
	   			}
	   		 }
		},'-',{
			text:'撤销未读邮件',
	   		icon:'${pageContext.request.contextPath}/img/reset.gif',
	   		 handler:function(){
	   			goRepealEmail();
	   		 }
		},'-',{
			text:'删除',
	   		icon:'${pageContext.request.contextPath}/img/delete.gif',
	   		 handler:function(){
	   			goDel();
	   		 }
		},'-',{
			text:'刷新',
	   		icon:'${pageContext.request.contextPath}/img/refresh.gif',
	   		 handler:function(){
	   			empty();
	   			goSearch_sendList();
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
				title: '已发件邮件查询',
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
	          			goSearch_sendList();
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
		<div id="divBtn"></div> 
		<div style="height:expression(document.body.clientHeight-28);width: 100%">
			<mt:DataGridPrintByBean name="sendList" />
		</div>
		<div id="divCheck_select" style="display:none;width: 100%;height: 100%">
			<table>
				<tr>
					<td colspan="2">&nbsp;</td>
				</tr>
				<tr>
					<td width="40%" align="right">收件人</td>
					<td><input type="text" name="addressee" id="addressee"></td>
				</tr>
				<tr>
					<td width="40%" align="right">标题</td>
					<td><input type="text" name="title" id="title"></td>
				</tr>
				<tr>
					<td align="right">重要性</td>
					<td><select name="importance" id="importance" style="width: 130px;">
							<option value="">请选择</option>
							<option value="一般邮件">一般邮件</option>
							<option value="重要邮件">重要邮件</option>
							<option value="非常重要">非常重要</option>
						</select></td>
				</tr>
				<tr>
					<td align="right">发件时间</td>
					<td><input type="text" name="sendDate" id="sendDate"></td>
				</tr>
				<tr>
					<td align="right">内容</td>
					<td><input type="text" name="content" id="content"></td>
				</tr>
			</table>
		</div>
		
</body>

<script type="text/javascript">
function getUuid(obj){
	var uuids = getChooseValue("sendList");
	if(uuids == ""){
		alert("请选择一项");
		return "";
	}
	if(obj ==1){
		if(uuids.indexOf(",")>-1){
			alert("只能选择一项");
			return "";
		}
	}
	return uuids;
}
new Ext.form.DateField({			
	applyTo : 'sendDate',
	width: 133,
	format: 'Y-m-d'	
});
function empty(){
	document.getElementById("addressee").value="";
	document.getElementById("title").value="";
	document.getElementById("importance").value="";
	document.getElementById("sendDate").value="";
	document.getElementById("content").value="";
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
function goSend(obj ){
	 if(obj == ""){
		 alert("发送失败！邮件编号为空！");
		 return ;
	 }
	 /*
	 if(confirm("您确定要发送此邮件吗？","yes")){
		 
		 Ext.Ajax.request({
				method:'POST',
				params : { 
					uuid : obj 
				},
				url:"${pageContext.request.contextPath}/interiorEmail.do?method=draftSend&rand="+Math.random(),
				success:function (response,options) {
					var request = response.responseText;
					 if(request =="true"){
						 alert("发送成功");
						 empty();
						 goSearch_sendList();
					}else{
						 alert("发送失败");
					 }
				},
				failure:function (response,options) {
					alert("后台出现异常,获取文件信息失败!");
				}
			});
		 
	 }*/
	 
	 window.location="${pageContext.request.contextPath}/interiorEmail.do?method=replySkip&uuid="+obj+"&opt=againSend&rand="+Math.random();
}

function grid_dblclick(obj, tableId) {
	var uuid=obj.uuid;
	if(uuid==""){
		return ;
	}else{
		window.location="${pageContext.request.contextPath}/interiorEmail.do?method=read&uuid="+uuid+"&isReadOnly=sendList&readUser=look&back=sendList";
	}
	
}

function goLook(uuid) {
	if(uuid==""){
		return ;
	}else{
		window.location="${pageContext.request.contextPath}/interiorEmail.do?method=read&uuid="+uuid+"&isReadOnly=true&readUser=look&back=sendList";
	}
	
}

function goRepealEmail() {
	var uuid = getUuid(1);
	if(uuid==""){
		return ;
	}else{
		if(confirm("您确定要撤销邮件吗？撤销将会把此封所有收件人未读的邮件删除!")){
			window.location="${pageContext.request.contextPath}/interiorEmail.do?method=repealEmail&uuid="+uuid+"&randomId="+Math.random();
		}
	}
	
}
function goDel() {
	var uuid = getUuid(0);
	if(uuid == ""){
		return ;
	}else{
		if(confirm("您确定要删除邮件吗？")){
			window.location="${pageContext.request.contextPath}/interiorEmail.do?method=sendDel&uuid="+uuid+"&randomId="+Math.random();
			goSendCount();
		}
	}
	
}
function goActionLookState(uuid){
	if(uuid==""){
		return ;
	}else{
		window.location="${pageContext.request.contextPath}/interiorEmail.do?method=sendEmailStateList&uuid="+uuid+"&randomId="+Math.random();
	}
}
//已发送 数量
function goSendCount(){
	var sql="SELECT COUNT(*) AS sumCount FROM `oa_email` WHERE addresser='${userSession.userId}'and status !='已撤销' and status !='已删除'";
	var url="${pageContext.request.contextPath}/interiorEmail.do?method=getCount";
	var requestString = "&sql="+sql;
	var request= ajaxLoadPageSynch(url,requestString);
	if(request ==""){
		request = "0";
	}
	window.parent.alreadyCount.innerText=request;  //更新已删除数量
}
</script>
</html>