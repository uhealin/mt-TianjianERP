<%@page import="com.matech.framework.listener.UserSession"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>收件箱</title>

<script type="text/javascript">


function ext_init(){ 
	var tbar = new Ext.Toolbar({
		renderTo: 'divBtn',
		items:[{
			text:'<b>写信</b>',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/add.gif',
			handler:function () {
				goAdd();
			}
		},'-',{
			text:'<b>回复</b>',
            icon:'${pageContext.request.contextPath}/img/edit.gif',
			handler:function () {
				  goReply();
			}
		},'-',{
			text:'<b>转发</b>',
            icon:'${pageContext.request.contextPath}/img/switch2.gif',
			handler:function () {
				transmit();
			}
		},'-',{
			text:'<b>阅读</b>',
            icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:function () {
				  goRead("");
			}
		},'-',{
	           text:'<b>标记已阅读</b>',
	           cls:'x-btn-text-icon',
	           icon:'${pageContext.request.contextPath}/img/past.gif',
	           handler:function(){
	        	   goSignRead();
			   }
	    },'-',{
           text:'<b>删除</b>',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/recycle.png',
           handler:function(){
        	   goDel(1);
		   }
        },'-',{
			text:'<b>永久删除</b>',
			id:'del',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/delete.gif',
			handler:function(){
				goDel(2);
			}
		},'-',{
	           text:'<b>删除所有已读邮件</b>',
	           cls:'x-btn-text-icon',
	           icon:'${pageContext.request.contextPath}/img/recycle.png',
	           handler:function(){
	        	   goDel(3);
	           }
	     },'-',{
			text:'<b>查询</b>',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
	   		 handler:queryWinFun
		},'->',{
			text:'<b>刷新</b>',
	   		icon:'${pageContext.request.contextPath}/img/refresh.gif',
	   		 handler:function(){
	   			empty();
	   			goSearch_addresseeList();
	   		 }
		}
		/*,'-',{
			text:'<b>打印</b>',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/print.gif',
			handler:function () {
				print_addresseeList();
			}
		}*/
		,'->'
		
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
				title: '收件箱查询',
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
	          			goSearch_addresseeList();
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
<body style="overflow: auto;" scroll=no>
		<input type="hidden" name="notGet" id="notGet" value="${notGet }">
		<input type="hidden" name="addresseeCount" id="addresseeCount" value="${addresseeCount }">
		<div id="divBtn"></div> 
		<div style="height:expression(document.body.clientHeight-28);width: 100%">
			<mt:DataGridPrintByBean name="addresseeList" />
		</div>
		<div id="divCheck_select" style="display:none;width: 100%;height: 100%">
			<table>
				<tr>
					<td colspan="2">&nbsp;</td>
				</tr>
				<tr>
					<td width="40%" align="right">发件人：</td>
					<td><input type="text" name="addressee" id="addressee"></td>
				</tr>
				<tr>
					<td width="40%" align="right">标题：</td>
					<td><input type="text" name="title" id="title"></td>
				</tr>
				<tr>
					<td align="right">重要性：</td>
					<td> 
						<select name="importance" id="importance" style="width: 130px;">
							<option value="">请选择</option>
							<option value="一般邮件">一般邮件</option>
							<option value="重要邮件">重要邮件</option>
							<option value="非常重要">非常重要</option>
						</select>
					</td>
				</tr>
				<tr>
					<td align="right">发件时间：</td>
					<td><input type="text" name="sendDate" id="sendDate"></td>
				</tr>
				<tr>
					<td align="right">内容：</td>
					<td><input type="text" name="content" id="content"></td>
				</tr>
			</table>
		</div>
		
</body>

<script type="text/javascript">
	
function goSerchList(){
		
		var sql="SELECT COUNT(DISTINCT a.uuid) AS sumCount FROM `oa_emailuser` a " +
						"INNER JOIN  oa_email  b ON a.uuid=b.`uuid` "+	
						"WHERE userId='<%=userSession.getUserId()%>' AND ctype='收件人' AND a.dustbin = '否' ";
		var url="${pageContext.request.contextPath}/interiorEmail.do?method=getCount";
		var requestString = "&sql="+sql;
		var request= ajaxLoadPageSynch(url,requestString);
		if(request ==""){
			request = "0";
		}
		window.parent.addresseeCount.innerText=request;  //修改收件箱数量
		
	}
	//window.setInterval(goSerchList,10000); //定时器刷新邮箱
	
new Ext.form.DateField({			
	applyTo : 'sendDate',
	width: 133,
	format: 'Y-m-d'	
});

	//未读
function goReadCount(){
	var sql = "SELECT COUNT(DISTINCT a.`uuid`) AS sumCount " +
				 "FROM `oa_emailuser` a " +
				 " INNER JOIN oa_email b ON a.`uuid` = b.`uuid` "+
				 " WHERE userId='<%=userSession.getUserId()%>' and a.isRead='否'  "  +
				 "  and (a.ctype='收件人' or a.ctype='抄送人' or a.ctype='密送人') ";
	var url="${pageContext.request.contextPath}/interiorEmail.do?method=getCount ";
	var requestString = "&sql="+sql;
	var request= ajaxLoadPageSynch(url,requestString);
	if(request ==""){
		request = "0";
	}
	window.parent.notGet.innerText=request;  //更新草稿箱数量
}

//收件箱数量
function goSendCount(){
	var sql = "SELECT COUNT(DISTINCT a.uuid) AS sumCount FROM `oa_emailuser` a " +
				" left join oa_email b on a.uuid = b.uuid "+
				"WHERE a.userId='<%=userSession.getUserId()%>' " +
				" AND (a.ctype='收件人' or a.ctype='抄送人' or a.ctype='密送人')" +
				" AND a.dustbin = '否' AND b.`status` <> '已删除' AND b.`status`<>'已撤销' ";
	var url="${pageContext.request.contextPath}/interiorEmail.do?method=getCount";
	var requestString = "&sql="+sql;
	var request= ajaxLoadPageSynch(url,requestString);
	if(request ==""){
		request = "0";
	}
	window.parent.addresseeCount.innerText=request;  //更新草稿箱数量
}

//已删除 数量
function goDelCount(){
	var sql="SELECT COUNT(DISTINCT a.uuid) AS sumCount" +
			" FROM `oa_emailuser` a " +
			" WHERE userId='<%=userSession.getUserId()%>' AND dustbin='是' and (a.ctype='收件人' or a.ctype='抄送人' or a.ctype='密送人') ";
	var url="${pageContext.request.contextPath}/interiorEmail.do?method=getCount";
	var requestString = "&sql="+sql;
	var request= ajaxLoadPageSynch(url,requestString);
	if(request ==""){
		request = "0";
	}
	window.parent.delCount.innerText=request;  //更新草稿箱数量
}
	
function goRead(objUuid){
	var uuids = "";
	if(objUuid == ""){
		uuids = getChooseValue("addresseeList");
	}else{
		uuids = objUuid;
	}
	 
	 if(uuids == ""){
		 alert("请选择要阅读的邮件!");
		 return ;
	 }else{
		 if(uuids.indexOf(",")>-1){
			 alert("请选择单条进行阅读!");
			 return ;
		 }else{
			 window.location="${pageContext.request.contextPath}/interiorEmail.do?method=read&uuid="+uuids;
		 }
	 }
}

//回复
function goReply(){
	 var uuids = getChooseValue("addresseeList");
	 
	 if(uuids == ""){
		 alert("请选择要回复的邮件!");
		 return ;
	 }else{
		 if(uuids.indexOf(",")>-1){
			 alert("请选择单条进行回复!");
			 return ;
		 }else{
			 window.location="${pageContext.request.contextPath}/interiorEmail.do?method=replySkip&uuid="+uuids;
		 }
	 }
}

function empty(){
	document.getElementById("addressee").value="";
	document.getElementById("title").value="";
	document.getElementById("importance").value="";
	document.getElementById("sendDate").value="";
	document.getElementById("content").value="";
}

function goAdd(){
	window.location="${pageContext.request.contextPath}/interiorEmail.do?method=addSkip";
}
//删除
function goDel(obj){
	 var uuids = getChooseValue("addresseeList");
	 if(obj ==1 || obj ==2){
		 if(uuids == ""){
			 alert("请选择要删除的邮件!");
			 return ;
		 }
	 }
	 var msg = "";
	 if(obj ==1){
		 msg = "确定要将选择的邮件放到回收站吗？";
	 }else if(obj ==2){
		 msg = "确定要永久删除勾选的邮件？删除后就无法恢复！";
	 }else{
		 msg = "确定删除所有已读邮件吗？";
	 }
	 if(confirm(msg,"yes")){
		 
		 Ext.Ajax.request({
				method:'POST',
				params : { 
					uuids : uuids,
					ctype:obj
				},
				url:"${pageContext.request.contextPath}/interiorEmail.do?method=delAddressee",
				success:function (response,options) {
					var request = response.responseText;
					 if(request =="true"){
						 goSearch_addresseeList();
						 goDelCount(); //更新已删除数量
						 goSendCount(); //更新 收件箱数量
						 goReadCount(); //未读
					}else{
						 alert("删除失败");
					 }
				},
				failure:function (response,options) {
					alert("后台出现异常,获取文件信息失败!");
				}
			});
		 //window.parent.location.reload();  //刷新父窗口
		// window.location="${pageContext.request.contextPath}/interiorEmail.do?method=delAddressee&uuids="+uuids+"&ctype="+obj;
	 }
}
//标记已阅读
function goSignRead( ){
	 var uuids = getChooseValue("addresseeList");
	 var newCount = uuids.split(",");
	 if(uuids == ""){
		 alert("请选择要标记的邮件!");
		 return ;
	 }
	 if(confirm("确定要标记为已读邮件吗？","yes")){
		 Ext.Ajax.request({
				method:'POST',
				params : { 
					uuids : uuids 
				},
				url:"${pageContext.request.contextPath}/interiorEmail.do?method=signAlreadyRead",
				success:function (response,options) {
					var request = response.responseText;
					 if(request =="true"){
						 goSearch_addresseeList();
						 goReadCount();
					}else{
						 alert("标记失败");
					 }
				},
				failure:function (response,options) {
					alert("后台出现异常,获取文件信息失败!");
				}
			});
		// window.location="&uuids="+uuids;
	 }
}
function transmit(){
 var uuids = getChooseValue("addresseeList");
	 
	 if(uuids == ""){
		 alert("请选择要转发的邮件!");
		 return ;
	 }else{
		 if(uuids.indexOf(",")>-1){
			 alert("请选择单条!");
			 return ;
		 }else{
			 window.location="${pageContext.request.contextPath}/interiorEmail.do?method=transmit&uuid="+uuids;
		 }
	 }
	 
}
//双击事件
function grid_dblclick(obj, tableId) {
	var uuid=obj.uuid;
	if(uuid==""){
		return ;
	}else if(uuid){
		window.location="${pageContext.request.contextPath}/interiorEmail.do?method=read&uuid="+uuid;
	}
	
}
</script>
</html>