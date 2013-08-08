<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@page import="com.matech.framework.pub.sys.UTILSysProperty"%>
<%@page import="com.matech.framework.listener.UserSession"%>
<%@page import="com.matech.framework.pub.util.ASFuntion"%>
<%@page import="org.del.JRockey2Opp"%>
<%@page import="java.util.Map"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.matech.audit.pub.db.DBConnect"%>
<%@page import="com.matech.framework.pub.db.DbUtil"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/chatSysPopLayer.js"></script>
　　<script type='text/javascript' src='${pageContext.request.contextPath}/dwr/engine.js'></script>
    <script type='text/javascript' src='${pageContext.request.contextPath}/dwr/util.js'></script>
    <script type='text/javascript' src='${pageContext.request.contextPath}/dwr/interface/PoplayerJS.js'></script>

  <script type="text/javascript">
  function _regPanchinaChatSS(){
	  try{
		PoplayerJS.setScriptSessionMark();
	  }catch(ex){}
	}
  try{
  _regPanchinaChatSS();
dwr.engine.setActiveReverseAjax(true);
  }catch(ex){}
  </script>
<%	
	ASFuntion CHF = new ASFuntion() ;
	String title = CHF.showNull(UTILSysProperty.SysProperty.getProperty("title"));
	String opt = CHF.showNull(UTILSysProperty.SysProperty.getProperty("E审通标题"));
	
	if(opt!=null && !"".equals(opt)){
		title = opt;
	}
	
	String logo = CHF.showNull(UTILSysProperty.SysProperty.getProperty("logo"));
	Map map = JRockey2Opp.readInfo();
	String sysCo = CHF.showNull((String)map.get("sysCo"));
	
	if("_pg".equals(session.getAttribute("pgVersion"))) {
		title = "天源评估系统";
		logo = "pg";
	}
	
	String pwdStrong = CHF.showNull(UTILSysProperty.SysProperty.getProperty("登录后必须修改密码的密码强度"));
	String bbsUrl = CHF.showNull(UTILSysProperty.SysProperty.getProperty("e审通论坛地址"));
	String ZH4Center = CHF.showNull(UTILSysProperty.SysProperty.getProperty("是否启用众华登陆首页")).trim();
	String menuType = CHF.showNull(UTILSysProperty.SysProperty.getProperty("菜单显示方式")).trim();
	String menuVersions = UTILSysProperty.SysProperty.getProperty("启用的中心");
	String fourCenter = "true" ;
	if(menuVersions != null && !"".equals(menuVersions)) {
		if(menuVersions.indexOf(",")==-1) {
			fourCenter = "" ;
		}
	}else {
		fourCenter = "" ;
	}
	UserSession userSession = (UserSession)request.getSession().getAttribute("userSession") ;
	boolean isZh4Center = "是".equals(ZH4Center) ;
%>
<title></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/images/ie.ico" 
mce_href="${pageContext.request.contextPath}/images/ie.ico" type="image/x-icon">

<style>

.hkfs{
  background-image: url(img/hkfs.png) !important;
}

.service {
    background-image: url(img/service.gif) !important;
}
.task {
    background-image: url(img/taskEdit.gif) !important;
}
.exit {
    background-image: url(img/exit.png) !important;
}

.email {
    background-image: url(img/email.png) !important;
}

.txl {
    background-image: url(img/txl.png) !important;
}

.switch {
    background-image: url(img/switch.png) !important;
}

.selectProject {
    background-image: url(img/selectProject.png) !important;
}

.bbs {
    background-image: url(img/bbs.png) !important;
}

.btn-panel td {
    padding-left:5px;
}

.unionReport {
    background-image: url(img/unionReport.png) !important;
}

h2 {
    color:#083772 !important;
    margin: 20px 0 0 !important;
    padding: 5px;
    background:#eee;
    width:400px;
}

h3 {
    font-weight:normal !important;
}

.btn-style .x-btn-center .x-btn-text {
	color: #FF0000;
}
</style>

<script type="text/javascript">
	
	
	function killSession(){
	  var oBao = new ActiveXObject("Microsoft.XMLHTTP");
	  oBao.open("POST","${pageContext.request.contextPath}/common.do?method=exitSystem&random=" + Math.random(),false);
	  oBao.send();
	}
	
	var centerId = "${param.centerId}" ;
	if(centerId == "") {
		centerId = "${centerId}"
	}
	var bbsUrl = "<%=bbsUrl%>" ;
	var isZh4Center = "<%=isZh4Center%>" ;
	var fourCenter = "<%=fourCenter%>" ;
	var menuType = "<%=menuType%>" ;
	var clientDogSysUi = "${userSession.clientDogSysUi}" ;
</script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/AS_CSS/menu.css" />  
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/mainFrame.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/fisheye.js"></script>

</head>  
<body onbeforeunload="exitSystem();">

<div id="loading-mask" style=""></div>
  <div id="loading">
    <div class="loading-indicator">
  </div>
</div>
<div id="north">
<table align="center" cellpadding="0" cellspacing="0" width="100%">
  <tr>
    <td height="69" bgcolor="#FFFFFF">
      <table width="100%" height="69" cellpadding="0" cellspacing="0">
        <tr>

          <td colspan="2" height="2" background="${pageContext.request.contextPath}/images/topbg1.gif">

          </td>
        </tr>
        <tr >
          <td align="left" background="${pageContext.request.contextPath}/images/1_3.gif" id='qqq'>
            
          </td>
          <td align="right" background="${pageContext.request.contextPath}/images/1_3.gif">
			 <div id="top_menu" style="margin:0 10 0 0">
	           
        	</div>
          </td>
        </tr>
        <tr>
          <td colspan="2" height="4" background="/AuditSystem/images/bottombg1.gif" bgcolor="#4152A0">
          </td>
        </tr>

      </table>
    </td>
    </tr>
    </table>
</div>
<div id="west">
</div>

<div id="center" style="height:100%;">
	<div id="center-north">
		<div id=menu></div>
		<div id=button></div>
	</div>
	<div id=tabDiv></div>
</div>

<div id="south" style="height:30;">
   <iframe frameborder="no" name="bottomFrame" id="bottomFrame" src="AS_SYSTEM/bottomFrame.jsp" width="100%" height="23" scrolling="no"></iframe>
</div>



<script type="text/javascript">
try{
	var host=window.location.host;
	var imagesrc="1_2_eaudit.gif";
	var copyright='天健会计师事务所综合管理系统';
	if (host.indexOf('oazx')>-1){
		imagesrc="1_2_eaudit_zx.gif";
		copyright='浙江凯通企业管理咨询有限公司综合管理系统';
	}
	if (host.indexOf('oasw')>-1){
		imagesrc="1_2_eaudit_sw.gif";
		copyright='浙江天健税务师事务所综合管理系统';
	}
	if (host.indexOf('oagc')>-1){
		imagesrc="1_2_eaudit_gc.gif";
		copyright='浙江天健东方工程投资咨询有限公司综合管理系统';
	}
	if (host.indexOf('oapg')>-1){
		imagesrc="1_2_eaudit_pg.gif";
		copyright='坤元资产评估有限公司综合管理系统';
	}
	var qqq=document.getElementById('qqq');
	qqq.innerHTML="<img  src='${pageContext.request.contextPath}/images/"+imagesrc+"' />";
	
	document.title=copyright;
}catch(e){}

</script>

<!--消息框-->
<div id="msgPanel" style="position: absolute; right:0; bottom:23;width:300;height:150;z-index:1"></div>

<!--修改密码框-->
<div id=editPwdWin style="position:absolute;left:expression((document.body.clientWidth-150)/2);top:expression(this.offsetParent.scrollTop +280); z-index: 2"></div>

<div id="editPwd" style="display:none;">
<br><br>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="" style="line-height:3">
		<tr align="center">
			
			<td align="right">原密码：</td>
			<td align=left><input  type="password" name="password_old" id="password_old" class="required"/></td>
			</tr><tr>
			<td align="right">新密码：</td>
			<td align=left><input type="password" name="password" id="password" class="required" value="" onblur="getMsg()"/><span id="msg"></span> </td>
			</tr>
			<tr>	
			<td align="right">确认新密码：</td>
			<td align=left><input type="password" name="password_two" id="password_two" class="required" value="" /></td>
			</tr>
	</table>
	<br>
	 <span style="margin:0 0 0 10">您的密码强度过低,所内要求修改为:<font color=red>[<%=pwdStrong%>]</font>,请修改。</span>
	 
	 <div id="desc" style="margin:10 0 0 10"></div>
</div>
</body>
<script type="text/javascript">

	document.getElementById("msgPanel").style.display = "none" ;
	var messageWin = null;
	function messageWinFun() {
		document.getElementById("msgPanel").style.display = "" ;
		if(!messageWin) {
		    messageWin = new Ext.Window({
		     title: '系统消息',
		     renderTo :'msgPanel',
		     width: 300,
		     height:150,
		     closeAction:'hide',
		        listeners   : {
		        	'hide':{fn: function () {
		        		document.getElementById("msgPanel").style.display = "none" ;
						messageWin.hide();	         	
		        	}}
		        },
		      layout:'fit',
		      buttons:[{
	            	text:'查看详情',
	          		handler:function (){
	          			goSetup();
	          			messageWin.hide();
	          		}
	        	},{
	        		text:'已阅',
	          		handler:function(){
	          			goUpdateIsRead();
	          		}
	        	}]
		    });
	   }
	  	messageWin.show();
	    doStart();
	    
	}
	
	function goAddPlacard(){
		var newTab = tab.add({    
	        'id':"sysPlacard",    
	        'title':"系统公告",    
	         closable:true,  //通过html载入目标页    
	         html:'<iframe scrolling="no" frameborder="0" width="100%" height="100%" src="${pageContext.request.contextPath}/placard.do?method=findOnePlacard"></iframe>'   
	     }); 
     	tab.setActiveTab(newTab);
     	messageWin.hide();
	}
	
	function goUpdateIsRead(){
		var url="${pageContext.request.contextPath}/placard.do?method=updateIsRead";
		var request= ajaxLoadPageSynch(url,"");
		messageWin.hide();
	}
	
	var editPwdWin = null;
	function editPwdWinWinFun() {
		
		var editPwdDiv = document.getElementById("editPwd") ;
		editPwdDiv.style.display = "" ;
		
		if(!editPwdWin) {
		    editPwdWin = new Ext.Window({
		     title: '修改密码',
		     renderTo :'editPwdWin',
		     contentEl:'editPwd',
		     width: 400,
		     height:260,
		     closable:false,
		     closeAction:'hide',
		        listeners   : {
		        	'hide':{fn: function () {
						editPwdWin.hide();
						new BlockDiv().hidden();
						editPwdDiv.style.display = "none" ;	         	
		        	}}
		        },
		      layout:'fit',
		       buttons:[{
		           text:'确定',
		         	handler:function(){
		         		if(!chkPwd()) {
		         			return ;
		         		}
		               	if(updatePwd()) {
		               		editPwdWin.hide();
		               	}
		           }
		       }]
		 
		    });
	   }
	   new BlockDiv().show();
	  	editPwdWin.show();
	}
	
	function getPwdCompare() {
		var pwdStr = "<%=pwdStrong%>" ;
		
		if(pwdStr == "无") {
			pwdCompare = -1 ;
		}else if(pwdStr == "低级密码强度") {
			pwdCompare = 1 ;
		}else if(pwdStr == "中级密码强度") {
			pwdCompare = 2 ;
		}else if(pwdStr == "高级密码强度") {
			pwdCompare = 3 ;
		}else {
			pwdCompare = -1 ;
		}
		return pwdCompare ;
	}
	
	Ext.onReady(function () {
		
		var pwdStrong = getResult("<%=userSession.getUserPwd()%>") ;
		var pwdCompare = getPwdCompare();
		if(pwdStrong < pwdCompare) {
			//密码不符合要求，弹出修改密码窗口
			editPwdWinWinFun() ;
			
			var desc = document.getElementById("desc") ;
			desc.innerHTML = "说明："
			if(pwdCompare == 1) {
				desc.innerHTML += "低级密码强度要求密码不为1" ;
			}else if(pwdCompare == 2) {
				desc.innerHTML += "中级密码强度要求密码位数在6位以上。" ;
			}else if(pwdCompare == 3) {
				desc.innerHTML += "高级密码强度要求密码位数在6位以上，并且含有数字和字母" ;
			}
		}
	}) ;
	
	
	
function createXmlHttp() {
	try {
		xmlHttp = new ActiveXObject("Msxml2.XMLHTTP");
	} catch (e) {
		try {
			xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
		} catch (e2) {
			xmlHttp = false;
		}
	}
}

//-----------------
//启动定时刷新的方法
//-----------------
function doStart() {
	createXmlHttp();
	var url = "${pageContext.request.contextPath}/info.do?method=user&random=" + Math.random();
	xmlHttp.open("GET", url , true);
	xmlHttp.onreadystatechange = updatePage;
	xmlHttp.send(null);
}

//----------------
//检查状态并返回结果
//----------------
function updatePage() {
	if(xmlHttp.readyState == 4) {
		if(xmlHttp.status == 200) {
			setTimeout("doStart()", 10000);
			result = unescape(xmlHttp.responseText);		
			if(result.indexOf("|||") > -1) {
				var strResult = result.split("|||");
												
				if(strResult[0].indexOf('无新消息<br /><br />无新消息<br /><br />') >= 0) {
					messageWin.body.update("<img src=\"${pageContext.request.contextPath}/images/showMessage.gif\"/><font color='blue' size='2'>无新消息</font>");
				} else {
					var strResultSub = strResult[0].split("<br />");
					messageWin.body.update("<img src=\"${pageContext.request.contextPath}/images/showMessage.gif\"/>"+strResultSub[0]);
				}
				
			} 	
		}
	}
}

function goSetup(){
	var newTab = tab.add({    
	        'id':"sysMessage",    
	        'title':"系统公告",    
	         closable:true,  //通过html载入目标页    
	         html:'<iframe scrolling="no" frameborder="0" width="100%" height="100%" src="${pageContext.request.contextPath}/placard/View.jsp"></iframe>'   
	     }); 
     tab.setActiveTab(newTab);   
}


function updatePwd(){

	var password_old = "";

		password_old = document.getElementById("password_old").value;
		
		if(password_old=="" ||password_old==null){
			alert("旧密码不能为空，密码修改失败，请重新输入!");
			return false;
		}
		var password = document.getElementById("password").value;
		if(password == "" ||password==null) {
				alert("新密码不能为空，密码修改失败，请重新输入!");
			return false;

		}
		var password_two = document.getElementById("password_two").value;
		if(password_two == ""||password_two==null) {
				alert("确认密码不能为空，密码修改失败，请重新输入!");
			return false;
		}
	

	if(password.indexOf("\\") > -1
			|| password.indexOf("/") > -1
			|| password.indexOf(":") > -1
			|| password.indexOf("*") > -1
			|| password.indexOf("?") > -1
			|| password.indexOf("\"") > -1
			|| password.indexOf("<") > -1
			|| password.indexOf(">") > -1
			|| password.indexOf("|") > -1
			|| password.indexOf("+") > -1
			|| password.indexOf("&") > -1
			|| password.indexOf("=") > -1) {

			alert("密码不能含有\/:*?\"<>|+&=等符号");
			return false;
		}
		
	var oBao = new ActiveXObject("Microsoft.XMLHTTP");
	var url="user.do?method=UpdatePWD&UserOpt=3&id=<%=userSession.getUserId()%>&password="+password+"&password_old="+password_old+"&random="+Math.random();

	oBao.open("POST", url, false);
	oBao.send();
	if(oBao.responseText == "error"){
		alert("旧密码不正确！");
		return false;
	}

	if(oBao.responseText == 'yes'){
		alert('密码修改成功!');
		return true ;
	}else{
		alert('密码修改失败，请重新输入!');
	}

	document.getElementById("password").value = "";
	document.getElementById("password_two").value = "";
	document.getElementById("password_old").value = "";
	return false ;
}

//密码一致的验证
function chkPwd(){

	if(document.getElementById("password_two").value!=document.getElementById("password").value){
		alert("两次输入的密码不一致！");
		document.getElementById("password_two").value="";
		document.getElementById("password").value="";
		document.getElementById("password").focus();
		return false;
	}
	
	var pwdStrong = getResult(document.getElementById("password").value) ;
	var pwdCompare = getPwdCompare();
	if(pwdStrong < pwdCompare) {
		alert("新密码强度不符合所内的要求，请重新修改！") ;
		return false ;
	}
	
	return true;
}

function chkpwd(obj)
{
	var t=obj.value;
	var id=getResult(t);

	//定义对应的消息提示
	var msg=new Array(4);
	msg[0]="初始密码";
	msg[1]="低级密码强度";
	msg[2]="中级密码强度";
	msg[3]="高级密码强度";

	return msg[id];
}

function getMsg() {
	var obj = document.getElementById("password");
	
	var msg = "" ;
	if(obj.value == "") {
		msg = "新密码不能为空"
	}else {
		msg = chkpwd(obj) ;
	}
	document.getElementById("msg").innerHTML = "<font color='red'>"+msg+"</font>" ;
}

//定义检测函数,返回0/1/2/3分别代表无效/差/一般/强
function getResult(s)
{
	if(s == 1) {
		return 0 ;
	}
	
	if (s.match(/[0-9]/ig) && s.match(/[a-z]/ig) && s.length >=6)
	{
		//密码大于6位，且包含数字和字母，判断为高级强度密码
		return 3
	}
	
	if(s.length >=6) {
		//密码大于6位，判断为中级强度密码
		return 2
	}

	return 1 ;     //其它则为低级强度密码
}


//这里刷新狗以后，应该也同步修改这个变量才对
var serverDogSysCo='${userSession.userAuditOfficeName}'



</script>
</html>
