<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>阅读邮件</title>
<!-- matech -->
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/common.js" charset="GBK"></script>
<!-- base -->
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/ext-base.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/ext-all.js"></script>
<style type="text/css">

.before{
	border: 0px;
	background-color: #FFFFFF !important;
}

.data_tb {
	text-align:left;
	margin:0px;
	border:#8db2e3 1px solid; 
	BORDER-COLLAPSE: collapse; 
}
.data_tb_alignright {	
	white-space:nowrap;
	padding:5px;
	border-top: #8db2e3 1px solid;
	border-left: #8db2e3 1px solid;
	BORDER-RIGHT: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid; 
	background-color:#F0F0F0;
	height:16px;
	text-align:left;
	width:100px;
	font-size: 13px;
	font-family:"宋体";
	color:#003366;
}
.data_tb_content {
	PADDING-LEFT: 2px; 
	BORDER-TOP: #8db2e3 1px solid; 
	BORDER-LEFT: #8db2e3 1px solid;
	BORDER-RIGHT: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid;  
	font-size: 13px;
	WORD-BREAK: break-all; 
	TEXT-ALIGN: left; 
	WORD-WRAP: break-word
}
.btn3_mouseout {
BORDER-RIGHT: #2C59AA 1px solid;width:65px;height:23px; PADDING-RIGHT: 2px; BORDER-TOP: #2C59AA 1px solid; PADDING-LEFT: 2px; FONT-SIZE: 12px; FILTER: progid:DXImageTransform.Microsoft.Gradient(GradientType=0, StartColorStr=#ffffff, EndColorStr=#C3DAF5); BORDER-LEFT: #2C59AA 1px solid; CURSOR: hand; COLOR: black; PADDING-TOP: 2px; BORDER-BOTTOM: #2C59AA 1px solid
}
.btn3_mouseover {
BORDER-RIGHT: #2C59AA 1px solid;width:65px;height:23px; PADDING-RIGHT: 2px; BORDER-TOP: #2C59AA 1px solid; PADDING-LEFT: 2px; FONT-SIZE: 12px; FILTER: progid:DXImageTransform.Microsoft.Gradient(GradientType=0, StartColorStr=#ffffff, EndColorStr=#D7E7FA); BORDER-LEFT: #2C59AA 1px solid; CURSOR: hand; COLOR: black; PADDING-TOP: 2px; BORDER-BOTTOM: #2C59AA 1px solid
}
.btn3_mousedown
{
BORDER-RIGHT: #FFE400 1px solid;width:65px;height:23px; PADDING-RIGHT: 2px; BORDER-TOP: #FFE400 1px solid; PADDING-LEFT: 2px; FONT-SIZE: 12px; FILTER: progid:DXImageTransform.Microsoft.Gradient(GradientType=0, StartColorStr=#ffffff, EndColorStr=#C3DAF5); BORDER-LEFT: #FFE400 1px solid; CURSOR: hand; COLOR: black; PADDING-TOP: 2px; BORDER-BOTTOM: #FFE400 1px solid
}
.btn3_mouseup {
BORDER-RIGHT: #2C59AA 1px solid;width:65px;height:23px; PADDING-RIGHT: 2px; BORDER-TOP: #2C59AA 1px solid; PADDING-LEFT: 2px; FONT-SIZE: 12px; FILTER: progid:DXImageTransform.Microsoft.Gradient(GradientType=0, StartColorStr=#ffffff, EndColorStr=#C3DAF5); BORDER-LEFT: #2C59AA 1px solid; CURSOR: hand; COLOR: black; PADDING-TOP: 2px; BORDER-BOTTOM: #2C59AA 1px solid
}
</style>
<script type="text/javascript">
try{
	if("${email }" ==''){
		alert("邮件已被删除或不存在，将返回收件箱!");
		parent.window.location = "${pageContext.request.contextPath}/interiorEmail.do?method=emailMain&rand="+Math.random();
	}
}catch(e){
	
}
</script>
</head>
<body  style="overflow: hidden;margin: 0px;padding: 0px;">
	<div style="z-index: 100;overflow:hidden; height:460px;width:100%;">
	
	<div id="contentDiv" style="overflow:auto;width:100%;height:100%;overflow-x:hidden;margin:0px;padding: 0px; ">
		<!-- <input type="hidden" name="uuid" id="uuid" value="${email.uuid}"> -->
		<table style="width:100%;border: 1px;margin-left: 2px;display: block;"  class="data_tb"   align="center" id="tableContent">
			<tr >
				<td  class="data_tb_alignright" width="100px;">
			   	    主题：</td>
			      <td class="data_tb_content" height="20px;" >${email.title}</td>
		     </tr>
		     <tr>
			     <td  class="data_tb_alignright">
			   	   发送人：</td>
			     <td class="data_tb_content">
			     <div style="text-align: left;">
					  ${userName }&nbsp;&nbsp;
					</div>
				 </td>
			 </tr>
			  <tr>
			     <td   class="data_tb_alignright">
			   	    发件时间：</td>
			     <td class="data_tb_content">
			     <div style="text-align: left;">
					  ${email.sendDate }</div>
				 </td>
			 </tr>
			<tr>
				<td width="10%" class="data_tb_alignright" >收件人：</td>
				<td class="data_tb_content" align="left"><span id="readUserSpan">${readUserNames }</span>
				<c:if test="${readUser !='' }">
					<a href="${pageContext.request.contextPath}/interiorEmail.do?method=readUser&uuid=${email.uuid}">查看阅读状态</a>
				</c:if>&nbsp;
				 </td>
			</tr>
			<tr>
				<td width="10%" class="data_tb_alignright" >抄送人：</td>
				<td class="data_tb_content" align="left"><span id="copyUserSpan">${copyUserName }</span>
				 </td>
			</tr>
			
			<tr>
				<td width="10%" class="data_tb_alignright" >附件信息：</td>
				<td class="data_tb_content"  align="left">
				<script type="text/javascript">
							attachInit('email','${email.fileId}',"showButton:false,remove:false");
					</script> &nbsp;</td>
			</tr>
		</table>
		
	  	<div style="word-wrap:break-word;width:95%;padding-left:25px;" id="emailContentDiv">
	 			<pre>${email.content}</pre>
		</div>
		
		<textarea style="display: none;" id="readUserNameArea"; >${readUserNames }</textarea>
		<textarea style="display: none;" id="copyUserNameArea"; >${copyUserName }</textarea>
		<input type="hidden" id="uuid" name="uuid" value="${uuid }">
		<div style="display: none;" id="contentDiv">${email.content}</div>
	</div>
	
	<span style="position: absolute;z-index:120;bottom: 5px; overflow: auto;width: 99%;text-align: center;"> 
	<c:if test="${isReadOnly =='' or isReadOnly ==null }">
		<button  class=btn3_mouseout onclick="goEmail('${upPageUuid}',0)"> 上一封</button>
		<button  class=btn3_mouseout onclick="goEmail('${nextPageUuid}',1)"> 下一封</button>
		<button  class=btn3_mouseout onclick="goReply()"> 回复</button>
		<button  class=btn3_mouseout onclick="replyAll()"> 回复全部</button>
		<button  class=btn3_mouseout onclick="transmit()">转发</button>
		<button  class=btn3_mouseout onclick="del()">删除</button>
		<button  class=btn3_mouseout onclick="goSend('${uuid}')">再次发送</button>
		<!--
		<input type="button" value="上一封" class="button" onclick="goEmail(0);">
		<input type="button" value="下一封" class="button" onclick="goEmail(1);">
		<input type="button" value="回复" class="button" onclick="goReply();">
		<input type="button" value="回复全部" class="button" onclick="replyAll();">
		<input type="button" value="转发" class="button" onclick="transmit();">
		<input type="button" value="删除" class="button" onclick="del();">-->
	</c:if>
	 	<button  class=btn3_mouseout onclick="copyText()">复制全文</button>
		<button  class=btn3_mouseout onclick="printText()">打印全文</button>
		<!--<button  onclick="AllAreaWord()">导出word</button>-->
		<button  class=btn3_mouseout onclick="goBack()">返回</button>
	  <!--  
		<input type="button" value="复制全文" class="button" onclick="copyText();">
		<input type="button" value="打印全文" class="button" onclick="printText();">
		<input type="button" value="导出word" class="button" onclick="AllAreaWord();" style="display: none;" >
		<input type="button" value="返回" class="button"  onclick="goBack()">-->
	</span>
	</div>
</body>

<script type="text/javascript">
try{
	var ListBt = document.getElementsByTagName("button");
	for(var i = 0;i<ListBt.length;i++){
		ListBt[i].onmouseover = function(){
			this.className = "btn3_mouseover";
		};
		ListBt[i].onmouseout = function(){
			this.className = "btn3_mouseout";
		};
		ListBt[i].onmousedown = function(){
			this.className = "btn3_mousedown";
		};
		ListBt[i].onmouseup = function(){
			this.className = "btn3_mouseup";
		};
	}
}catch(e){
	
}
function goSend(obj ){
	 if(obj == ""){
		 alert("发送失败！邮件编号为空！");
		 return ;
	 }
	 window.location="${pageContext.request.contextPath}/interiorEmail.do?method=replySkip&uuid="+obj+"&opt=againSend&rand="+Math.random();
}
function goBack(){
	if("${isReadOnly}" == ""){
		window.location='${pageContext.request.contextPath}/interiorEmail.do?method=addresseeList&rand='+Math.random(); //收件箱
	}else{
		if("${back}" == "sendList"){
			window.location='${pageContext.request.contextPath}/interiorEmail.do?method=sendList&rand='+Math.random();  //已发送
		}else if("${back}" == "deleteList"){
			window.location='${pageContext.request.contextPath}/interiorEmail.do?method=deletedList&rand='+Math.random();  //已删除
		}else if("${back}" == "draftList"){
			window.location='${pageContext.request.contextPath}/interiorEmail.do?method=draftList&rand='+Math.random();  //草稿箱
		}else{
			parent.window.location = "${pageContext.request.contextPath}/interiorEmail.do?method=emailMain&rand="+Math.random();
			//parent.document.getElementById("mainIframe").src ='${pageContext.request.contextPath}/interiorEmail.do?method=addresseeList';    //收件箱
		}
	}
}
function AllAreaWord(){
		//var cont = document.getElementById("contentDiv").innerHTML;
			var oWD = new ActiveXObject("Word.Application"); 
			var oDC = oWD.Documents.Add("",0,1); 
			var oRange =oDC.Range(0,1); 
			var sel = document.body.createTextRange(); 
			sel.moveToElementText(contentDiv); 
			sel.select(); 
			sel.execCommand("Copy"); 
			oRange.Paste(); 
			oWD.Application.Visible = true; 
		 
}
 
//打印文本
function printText() {
	var contentDiv =  document.getElementById("contentDiv").innerHTML;
    var winname = window.open('', "_blank",'');
    winname.document.title = "打印";
    winname.document.open('text/html', 'replace');
    winname.document.writeln(contentDiv);
    winname.document.close();
    winname.print();
    winname.close();
}
/*
try{
var buttons = document.getElementsByTagName("input");
for(var j= 0;j<buttons.length;j++){
	if(buttons[j].type == "button"){
		buttons[j].style.background="white";
		buttons[j].onmouseover=function(){
			this.style.background="#D97911";
		}
		buttons[j].onmouseout=function(){
			//alert(buttons[j].value);
			this.style.background="white";
		}
	}
}
}catch(e){
	alert(e);
}
*/
var LookUser = ""; //全部名单
var readHtml  = ""; //部分名单(只显示20条)
var r = /^[0-9]*[1-9][0-9]*$/; //判断是否是正整数

function loadUser(objType,objReadUserName,objSpan){
	if(objReadUserName != ""){
		LookUser = "";
		readHtml = "";
		var readUs = objReadUserName.split(",");
		var ishr = 0; //用于判断是否要加<hr>
		for(var i = 0 ;i<readUs.length;i++){
			if(i<10){
				if(readUs[i].length>2){
					readHtml +=readUs[i]+";&nbsp;";
				}else{
					readHtml +=readUs[i]+"&nbsp;;&nbsp;&nbsp;";
				}
			}
			
		}
		if(readUs.length>10){
			readHtml += "<a href='javascript:void(0);' onclick='alert(\""+objReadUserName+"\")'>查看全部名单</a>";
		}
		document.getElementById(objSpan).innerHTML = readHtml;
	}
}
function lookAllUser(obj,objUserName,objSpan){
	if(obj == "1"){
		loadUser(obj,objUserName ,objSpan); //加载收件人
		document.getElementById(objSpan).innerHTML = LookUser + "<a href='javascript:void(0);' onclick='lookAllUser(0,\""+objUserName+"\",\""+objSpan+"\")'>查看部分名单</a>";;
	}else{
		loadUser(obj,objUserName ,objSpan); //加载收件人
		document.getElementById(objSpan).innerHTML = readHtml;
	}
}
try{
	
	var rdUser = document.getElementById("readUserNameArea").innerText;
	var cpUser = document.getElementById("copyUserNameArea").innerText;
	loadUser("0",rdUser ,"readUserSpan"); //加载收件人
	loadUser("0",cpUser ,"copyUserSpan"); //加载抄送人
}catch(e){
	
}
function replyAll(){
	var uuid = document.getElementById("uuid").value;
	if(uuid !=""){
		window.location="${pageContext.request.contextPath}/interiorEmail.do?method=replyAll&uuid="+uuid;
	}
}
function goEmail(uuid,obtType){
	//var uuid = document.getElementById("uuid").value;
	if(uuid !=""){
		window.location="${pageContext.request.contextPath}/interiorEmail.do?method=read&uuid="+uuid+"&rand="+Math.random();
	}else{
		if(obtType ==0){
			alert("当前已经是第一封邮件了!");
		}else{
			alert("当前已经是最后一封邮件了!");
		}
	}
}
function del(){
	var uuid = document.getElementById("uuid").value;
	if(uuid !=""){
		if(confirm("您确定要删除此份邮件吗?","yes")){
			 /*Ext.Ajax.request({
					method:'POST',
					params : { 
						uuids : uuid,
						ctype:"1"
					},
					url:"${pageContext.request.contextPath}/interiorEmail.do?method=delAddressee",
					success:function (response,options) {
						var request = response.responseText;
						 if(request =="true"){
							 goDelCount(); //修改删除数
							 window.location="${pageContext.request.contextPath}/ interiorEmail.do?method=addresseeList";
						}else{
							 alert("删除失败");
						 }
					},
					failure:function (response,options) {
						alert("后台出现异常,获取文件信息失败!");
					}
				});*/
			 
			 var result = ajaxLoadPageSynch("${pageContext.request.contextPath}/interiorEmail.do?method=delAddressee", "&uuids="+uuid+"&ctype=1");
			 if(result =="true"){
				 goDelCount(); //修改删除数
				 //window.location="${pageContext.request.contextPath}/ interiorEmail.do?method=addresseeList";
				 parent.window.location = "${pageContext.request.contextPath}/interiorEmail.do?method=emailMain&rand="+Math.random();
			}else{
				 alert("删除失败");
			 }
		}
	}
}
function goDelCount(){
	var sql  = "SELECT COUNT(DISTINCT `uuid`) AS sumCount" +
			 " FROM `oa_emailuser` a " +
			 " WHERE userId='${userSession.userId}' AND dustbin='是' and (a.ctype='收件人' or a.ctype='抄送人' or a.ctype='密送人')";
	var url="${pageContext.request.contextPath}/interiorEmail.do?method=getCount";
	var requestString = "&sql="+sql;
	var request= ajaxLoadPageSynch(url,requestString);
	if(request ==""){
		request = "0";
	}
	window.parent.delCount.innerText=request;  //更新草稿箱数量
}
function transmit(){
	var uuid = document.getElementById("uuid").value;
	if(uuid !=""){
		window.location="${pageContext.request.contextPath}/interiorEmail.do?method=transmit&uuid="+uuid;
	}
}
function copyText(){
	var s = document.getElementById("contentDiv").innerText;
	if (window.clipboardData) {
		window.clipboardData.setData("Text", s);
		alert("已经复制到剪切板，按Ctrl+V粘贴或鼠标右键选中粘贴使用！");
	} else if (navigator.userAgent.indexOf("Opera") != -1) {
		window.location = s;
	} else if (window.netscape) {
		try {
			netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
		} catch (e) {
			alert("被浏览器拒绝！\n请在浏览器地址栏输入'about:config'并回车\n然后将'signed.applets.codebase_principal_support'设置为'true'");
		}
		var clip = Components.classes['@mozilla.org/widget/clipboard;1']
				.createInstance(Components.interfaces.nsIClipboard);
		if (!clip)
			return;
		var trans = Components.classes['@mozilla.org/widget/transferable;1']
				.createInstance(Components.interfaces.nsITransferable);
		if (!trans)
			return;
		trans.addDataFlavor('text/unicode');
		var str = new Object();
		var len = new Object();
		var str = Components.classes["@mozilla.org/supports-string;1"]
				.createInstance(Components.interfaces.nsISupportsString);
		var copytext = s;
		str.data = copytext;
		trans.setTransferData("text/unicode", str, copytext.length * 2);
		var clipid = Components.interfaces.nsIClipboard;
		if (!clip)
			return false;
		clip.setData(trans, null, clipid.kGlobalClipboard);
		alert("已经复制到剪切板，按Ctrl+V粘贴或鼠标右键选中粘贴使用！");
	}
}
try{
	//修改未读的数量
	window.parent.notGet.innerText="${notGet}";
	
}catch(e){
}
function goReply(){
	 window.location="${pageContext.request.contextPath}/interiorEmail.do?method=replySkip&uuid=${email.uuid}";
}

</script>
</html>