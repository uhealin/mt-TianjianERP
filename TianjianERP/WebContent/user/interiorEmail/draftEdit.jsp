<%@page import="java.util.UUID"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>写信</title>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/images/editor.js" charset=GBK></script>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/images/DhtmlEdit.js" charset=GBK></script>

<style type="text/css">
.table 
{ 
	border-collapse: collapse; 
	border: none; 
	vertical-align:middle;
} 
.td{ 
	border: solid #cfe7f7 1px; 
} 
</style>

<script type="text/javascript">
var result = false;
Ext.onReady(function (){
	
	new Ext.Toolbar({
		renderTo: "historyBtn",
		height:30,
		defaults: {autoHeight: true,autoWidth:true},
       items:[{ 
        text:'返回',
        icon:'${pageContext.request.contextPath}/img/back.gif', 
        handler:function(){
			window.history.back();
		}
  	},'->']
	});
		var IframeID=document.getElementById("oblog_Composition").contentWindow;
	 	IframeID.document.body.innerHTML =document.getElementById("content").value;
	 	
	new ExtButtonPanel({
		desc:'',
		renderTo:'sbtBtn',
		items:[
		{
            text: '立即发送',
            id:'appSubmit23', 
            icon:'${pageContext.request.contextPath}/img/receive.png' ,
            scale: 'large',
            handler:function(){
            	mySubmit("立即发送");
   			}
           },{
            text: '保存到草稿箱',
            id:'appSubmit25', 
            icon:'${pageContext.request.contextPath}/img/receive.png' ,
            scale: 'large',
               handler:function(){
            	   mySubmit("草稿");
   			   }
           } 
        ]  
	});    
	
	  var Tree = Ext.tree;
	  var tree = new Tree.TreePanel({
      id:'userTree',
      animate:true, 
	  renderTo:'userTreeDiv',
	  //loader: new Tree.TreeLoader({dataUrl:"${pageContext.request.contextPath}/placard.do?method=getUserJsonTree"}),
	  loader: new Tree.TreeLoader({dataUrl:"${pageContext.request.contextPath}/interiorEmail.do?method=getUserJsonTree"}),
	     enableDD:true,
	     root:new Tree.AsyncTreeNode({
	  text: '选择人员',
	  draggable:false,
	  id:'source'
	     }),
	     containerScroll: true
	 });

	 
	
		tree.on('checkchange', function(node, checked) {   
			node.expand();   
			node.attributes.checked = checked; 
			
			node.eachChild(function(child) {  
				child.ui.toggleCheck(checked);   
				child.attributes.checked = checked;  
			});   
		}, tree);  
		
    
});
</script>
</head>
<body style="background-color: #e7f1f8;overflow-x:hidden; ">
	<div id="historyBtn"></div>
	<form action="" method="post" name="thisForm" id="thisForm">
	<input type="hidden" name="ctype" id="ctype">
	<input type="hidden" name="uuid" id="uuid" value="${email.uuid}">
	<input type="hidden" name="reply" id="reply" value="${reply}">
	<span style="margin-left: 30px;padding-top: 30px;"> 
	<img alt="" src="${pageContext.request.contextPath}/images/email/new_email.gif">写邮件</span>
	<table width="80%" align="center" style="background-color: white;" class="table">
		<tr>
			<td class="td" colspan="3" height="20px;"></td>
		</tr>
		<tr>
			<td class="td"  align="right" width="15%">收件人：</td>
			<td class="td" colspan="2">
				<input type="hidden" name="addresseeId"  id="addresseeId"  value="${userId}">
				<input type="text" name="addressee"  id="addressee" size="80" class='required' title="请选择人员" readonly="readonly" value="${userName}">
				<a href="#" onclick="queryWinFun('1')">添加联系人</a>
				<div style="margin-left: 30px;margin-top: 6px;display: none;"> 
					<a href="#" id="11" onclick="showDiv('1','11','抄送')">添加抄送 </a> -- 
					<a href="#" id="22" onclick="showDiv('2','22','密送')">添加密送</a> -- 
					<a href="#" id="33" onclick="showDiv('3','33','外部收件人')">添加外部收件人 </a>  -- 
					<a href="#" id="44" onclick="showDiv('4','44','最近联系人')">最近联系人 </a>
				</div>
				<br>
			</td>
		</tr>
		<tr style="display: none;" id="1">
			<td class="td"  align="right">抄送：</td>
			<td class="td" colspan="2">
				<input size="80" class='required' name="copyUserId" id="copyUserId" type="hidden">  
				<input size="80" class='required' name="copyUser" id="copyUser" readonly="readonly">  
				<a href="#" onclick="queryWinFun('2')">选人</a>
			</td>
		</tr>
		<tr style="display: none;" id="2">
			<td class="td"  align="right"> 密送：</td>
			<td class="td" colspan="2">
				<input size="80" class='required' name="secretUserId" id="secretUserId" type="hidden">  
				<input size="80" class='required' name="secretUser" id="secretUser" readonly="readonly">  
				<a href="#" onclick="queryWinFun('3')">选人</a>
			</td>
		</tr>
		<tr style="display: none;" id="3">
			<td class="td"  align="right">外部收信人：</td>
			<td class="td" colspan="2">
				<input size="80" class='required'> 
				<a href="#" >选人</a>
			</td>
		</tr>
		<tr style="display: none;" id="4">
			<td class="td"  align="right">最近联系人：</td>
			<td class="td" colspan="2">
				<input size="80" class='required' name="recentlyUser" id="recentlyUser"> 
				<a href="#" onclick="queryWinFun('4')">选人</a>
			</td>
		</tr>
		<tr>
			<td class="td"  align="right">邮件主题：</td>
			<td class="td" colspan="2"><input size="80" class='required' name="title" id="title" value="${email.title }"> 
			重要性：<select id="importance" name="importance">
			<option value="一般邮件">一般邮件</option>
			<option value="重要邮件" style="color: #FF8C00;">重要邮件</option>
			<option value="非常重要" style="color: red;font-size: 20px;">非常重要</option>
			</select>
			</td>
		</tr>
		<tr>
			<td class="td"  style="height: 360px;" align="right">内容：</td>
			<td class="td" colspan="2"> 
			<input type="hidden" name="content" id="content" value="${email.content}">
			<label style="height:95%;text-align: left;"> 
					    <jsp:include page="../../AS_INCLUDE/images/edit.html"/> 
			</label> &nbsp;</td>
		</tr>
		<tr>
			<td class="td"  align="right">附件：</td>
			<td class="td"><br>	<input type="hidden" name="fileId" id="fileId" value="${email.fileId}">
				<script type="text/javascript">
						if("${email.fileId}"==""){
							<%
									String fileName=UUID.randomUUID().toString();  //生成uuid
							%>
							document.getElementById("fileId").value="<%=fileName%>";
							attachInit('email','<%=fileName%>');		
						}else{
							attachInit('email','${email.fileId}');
						}
				</script>
			 &nbsp; </td>
		</tr>
		<tr>
		<td class="td"  align="right">提醒：</td>
				<td class="td" colspan="2">
				<input type="checkbox" value="是" id="instationRemind" name="instationRemind" > 使用内部短信提醒 <br>
				<input type="checkbox" value="是" id="mobilePhoneRemind" name="mobilePhoneRemind" > 使用手机短信提醒 </td>
			</tr>
		<tr>
			<td class="td"  align="right">收条：</td>
			<td class="td" colspan="2" style="vertical-align: middle;line-height: 25px;">
			<input type="checkbox" value="是" id="receiveRemind" name="receiveRemind" >
			请求阅读收条 (收件人第一次阅读邮件时，短信提醒发件人) </td>
		</tr>
	</table>
	<div id="divCheck_select" >
		<div id="userTreeDiv" style="width: 200px;display: none;height: 230px;overflow: auto;"></div>
	</div>
	<span id="sbtBtn" style="text-align: center;width: 100%"> </span>
	<br><br><br>
		
	</form>
</body>
<script type="text/javascript">
//alert(window.parent.location.reload());


new Validation('thisForm');

if("${email.importance}" !=""){
	document.getElementById("importance").value="${email.importance}";
} 
if("${receiveRemind}" == "是"){
	document.getElementById("receiveRemind").checked=true;
}

if("${mobilePhoneRemind}" == "是"){
	document.getElementById("mobilePhoneRemind").checked=true;
}
	

function mySubmit(obj){
	if (!formSubmitCheck('thisForm')) return ;
	var uuid = document.getElementById("uuid").value;
	var IframeID=document.getElementById("oblog_Composition").contentWindow;
	var content = IframeID.document.body.innerHTML;
	if(content=="" || content==null){
		alert("内容不能为空!");
		return;
	}
	if(content.indexOf("<P>&nbsp;</P>")>-1){
		content = content.replace("<P>&nbsp;</P>","");
	}
	document.getElementById("content").value=content;
	document.getElementById("ctype").value=obj;
	
	if("${reply}" !=""){ //回复
		document.thisForm.action="${pageContext.request.contextPath}/interiorEmail.do?method=reply";
	}else if(uuid !=""){ //草稿
		
	}else{
		document.thisForm.action="${pageContext.request.contextPath}/interiorEmail.do?method=saveAddressee";
	}
	window.parent.location.reload();  //刷新父窗口
	document.thisForm.submit();
}
 
//window 面板 进行查询
function queryWinFun(obj){
	var queryWin = null;
	resizable:false;
	var searchDiv = document.getElementById("divCheck_select") ;
	searchDiv.style.display = "block" ;
	document.getElementById("userTreeDiv").style.display = "block"; //人员树
	document.getElementById("sbtBtn").style.display = "none";  //立即发送按钮
	if(!queryWin) { 
	    queryWin = new Ext.Window({
			title: '添加联系人',
			resizable:false,   //禁止用户 四角 拖动
			contentEl:'divCheck_select',
	     	//renderTo : searchWin,
	     	width: 220,
	     	height:300,
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
            	text:'确认',
          		handler:function(){
					var userId = getUrsValue();//Id
					var userName = getUserName(); //name
					setUser(obj,userId,userName);
					document.getElementById("userTreeDiv").style.display = "none" ;
					document.getElementById("sbtBtn").style.display = "block";  //立即发送按钮
					queryWin.hide();
            	}
        	},{
            	text:'取消',
            	handler:function(){
               		queryWin.hide();
               		document.getElementById("userTreeDiv").style.display = "none" ;
               		document.getElementById("sbtBtn").style.display = "block";  //立即发送按钮
            	}
        	}]
	    });
    }
    new BlockDiv().show();
    queryWin.show();
}

function setUser(obj,objUser,objUserName){
	if(obj == 1){//收件人
		document.getElementById("addresseeId").value =objUser;
		document.getElementById("addressee").value = objUserName;
	}else if(obj == 2){ //抄送
		document.getElementById("copyUserId").value = objUser;
		document.getElementById("copyUser").value = objUserName;
	}else if(obj == 3){ //密送
		document.getElementById("secretUserId").value = objUser;
		document.getElementById("secretUser").value = objUserName;
	}else if(obj == 4){ //最近联系
		document.getElementById("recentlyUser").value = objUser;
	} 
}

//得到选中的人员Id
function getUrsValue() {
	var tree = Ext.getCmp("userTree");
	var selects = tree.getChecked();
	var usrs = "" ;
	for(var i=0;i<selects.length;i++) {
		if(selects[i].isLeaf()) {
			usrs += selects[i].id + ",";
		}
	}
	if(usrs != "") {
		usrs = usrs.substr(0,usrs.length-1);
	}
	return usrs;
}
//得到选中的人员名称
function getUserName() {
	var tree = Ext.getCmp("userTree");
	var selects = tree.getChecked();
	var userName = "";
	for(var i=0;i<selects.length;i++) {
		if(selects[i].isLeaf()) {
			userName += selects[i].attributes.userName + ",";
		}
	}
	if(userName != "") {
		userName = userName.substr(0,userName.length-1);
	}
	return userName;
}

//显示隐藏
function showDiv(objDiv,obj,msg){
	var thisDiv = document.getElementById(objDiv);
	if(thisDiv.style.display =="none" ){
		thisDiv.style.display = "block";
		document.getElementById(obj).innerText = "隐藏"+msg;
	}else{
		document.getElementById(obj).innerText = "添加"+msg;
		thisDiv.style.display = "none";
	}
}
</script>
</html>