<%@page import="com.matech.framework.listener.UserSession"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%
	UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>收件箱</title>

<script type="text/javascript">

function ext_init(){ 
	var tbar = new Ext.Toolbar({
		renderTo: 'divBtn',
		items:[{
			text:'查询',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
	   		 handler:queryWinFun
		},'-',{
			text:'永久删除',
			id:'del',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/delete.gif',
			handler:function(){
				goDel(2);
			}
		},'-',{
			text:'刷新',
	   		icon:'${pageContext.request.contextPath}/img/refresh.gif',
	   		 handler:function(){
	   			empty();
	   			goSearch_draftList();
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
				title: '草稿查询',
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
	          			goSearch_draftList();
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
			<mt:DataGridPrintByBean name="draftList" />
		</div>
		<div id="divCheck_select" style="display:none;width: 100%;height: 100%">
			<table>
				<tr>
					<td colspan="2">&nbsp;</td>
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
//删除
function goDel(obj){
	 var uuids = getChooseValue("draftList");
	 if(uuids == ""){
		 alert("请选择要删除的邮件!");
		 return ;
	 }
	 if(confirm("确定删除此邮件吗？","yes")){
		 Ext.Ajax.request({
				method:'POST',
				params : { 
					uuids : uuids,
					ctype:obj
				},
				url:"${pageContext.request.contextPath}/interiorEmail.do?method=delDraft",
				success:function (response,options) {
					var request = response.responseText;
					 if(request =="true"){
						 goSearch_draftList();
						 goSerchList();
					}else{
						 alert("删除失败");
					 }
				},
				failure:function (response,options) {
					alert("后台出现异常,获取文件信息失败!");
				}
			});
		
		 //window.location="${pageContext.request.contextPath}/interiorEmail.do?method=delAddressee&uuids="+uuids+"&ctype="+obj;
	 }
}

new Ext.form.DateField({			
	applyTo : 'sendDate',
	width: 133,
	format: 'Y-m-d'	
});
function empty(){
	document.getElementById("title").value="";
	document.getElementById("importance").value="";
	document.getElementById("sendDate").value="";
	document.getElementById("content").value="";
}
 
function goSerchList(){
	
	var sql  = "SELECT COUNT(*) AS sumCount FROM `oa_emaildraft` WHERE addresser='<%=userSession.getUserId()%>'";
	var url="${pageContext.request.contextPath}/interiorEmail.do?method=getCount";
	var requestString = "&sql="+sql;
	var request= ajaxLoadPageSynch(url,requestString);
	
	window.parent.draftCount.innerText=request;  //更新草稿箱数量
	
}
//window.setInterval(goSerchList,10000); //定时器刷新邮箱
 
//编辑
function goEdit(obj){
	 if(obj == ""){
			 alert("编辑失败!邮件编号为空！");
			 return ;
	 }
	 window.location="${pageContext.request.contextPath}/interiorEmail.do?method=draftSkip&uuid="+obj;
}

//立即发送
function goSend(obj ){
	 if(obj == ""){
		 alert("发送失败！邮件编号为空！");
		 return ;
	 }
	 if(confirm("您确定要发送此邮件吗？","yes")){
		 Ext.Ajax.request({
				method:'POST',
				params : { 
					uuid : obj,
					Ifparam:'send'
				},
				url:"${pageContext.request.contextPath}/interiorEmail.do?method=draftSend&rand="+Math.random(),
				success:function (response,options) {
					var request = response.responseText;
					 if(request =="true"){
						 alert("发送成功");
						 empty();
						 goSearch_draftList();
					}else{
						 alert("发送失败");
					 }
				},
				failure:function (response,options) {
					alert("后台出现异常,获取文件信息失败!");
				}
			});
		 
	 }
	// window.location="${pageContext.request.contextPath}/interiorEmail.do?method=signAlreadyRead&uuids="+uuids;
}
function grid_dblclick(obj, tableId) {
	var uuid=obj.uuid;
	if(uuid==""){
		return ;
	}else{
		window.location="${pageContext.request.contextPath}/interiorEmail.do?method=draftSkip&uuid="+uuid+"&isReadOnly=true&back=draftList";
	}
	
}
</script>
</html>