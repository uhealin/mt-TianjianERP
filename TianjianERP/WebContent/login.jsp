<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%@ page import="java.util.*"%>
<%@ page import="java.io.* "%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="com.matech.framework.pub.sys.UTILSysProperty"%>

<%!
	public List getFileList(String path) throws Exception {
		List movieFileList = new ArrayList();
		File tempFile = new File(path);

		fileList(tempFile,movieFileList);
		return movieFileList;
	}

	private void fileList(File file,List movieFileList) throws Exception {
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
		Map map = new HashMap();
		if(file.isDirectory()) {
			File tempFileList[] = file.listFiles();
			for(int i=0; i < tempFileList.length; i++) {
				fileList(tempFileList[i],movieFileList);
			}
		} else {
			String fileName = file.getName();
			if(fileName.indexOf(".ocx") >=0 || fileName.indexOf(".xla") >=0
					|| fileName.indexOf("MatechOfficeAddIn") >=0
					|| fileName.indexOf("MTOffice.dll") >=0
					|| fileName.indexOf("MTXLL.xll") >=0
					|| fileName.indexOf("FileStream32.dll") >=0
					) {
				map.put("filePath",file.getCanonicalPath());
				map.put("fileName",fileName);
				map.put("fileSize", String.valueOf(file.length()));
				map.put("fileLastModified", sdf.format( new Date(file.lastModified() ) ) );
				movieFileList.add(map);
			}
		}
	}

%>

<%
	List fileList = new ArrayList();
	String path = null;
    try {
        path = org.del.DelPublic.getWarPath();
    } catch (Exception e) {
        path = org.del.DelPublic.getClassRoot() + "../../";
    }

    if (path.substring(0, 1).equals("/")) {
        path = path.substring(1);
    }

    if (path.substring(path.length() - 1, path.length()).equals("/")) {
        path += "ocx";
    } else {
        path += "/ocx";
    }

	try {
		fileList = getFileList(path);
	} catch (Exception ex) {
		ex.printStackTrace();
	}


	String strFileNames="",strFileSizes="",strFileDates="";

	Iterator it=fileList.iterator();
	//hasNext是取值取的是当前值.他的运算过程是判断下个是否有值如果有继续.
	while(it.hasNext()){
	    //设it.next封装类,调用强制转换String类型赋值给i;
	    Map map=(HashMap)it.next();
	    strFileNames+=(String)map.get("fileName")+"`";
	    strFileDates+=(String)map.get("fileLastModified")+"`";
	    strFileSizes+=(String)map.get("fileSize")+"`";
	}

	String logo = UTILSysProperty.SysProperty.getProperty("logo");
	String title = UTILSysProperty.SysProperty.getProperty("title");
	
	// 得到存在相应的环境变量的值
	String opt = UTILSysProperty.SysProperty.getProperty("E审通标题");
	if(opt!=null && !"".equals(opt)){
		title = opt;
	}
	
	//登录时是否去掉用户名下拉;是：去掉下拉，否：有下拉
	String optAll = UTILSysProperty.SysProperty.getProperty("登录时是否去掉用户名下拉");
	System.out.print("optAll="+optAll);

%>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>天健会计师事务所综合管理系统</title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/images/donggua.ico" 
mce_href="${pageContext.request.contextPath}/images/donggua.ico" type="image/x-icon">

<style> 
.wapper{font-size:12px; line-height:16px;}
.login{ width:1004px; height:317px; margin: auto; margin-top:100px; position:relative; }
.InputStyle{ width:92px; height:18px; border:#26a2d6 1px solid;}
.LoginInput{ position:absolute; right:150px; bottom:10px;}
.LoginInput a,.LoginInput a:hover,.LoginInput a:visited{ color:#1a57a8}
.footer{ text-align:center; margin-top:30px;}
.checkbox{vertical-align:middle; margin-top:0;} 
</style>
</head>
 
<body>

<SCRIPT LANGUAGE=JAVASCRIPT>
<!--
if (top.location != self.location)top.location=self.location;
// -->
</SCRIPT>



<center>
<div class="wapper">
	<div class="login" id="qqq">

		<div id=state2 name=state2 >



				<form action="${pageContext.request.contextPath}/login.do" method="post" name="thisForm" onsubmit="return next();">
				  <table width="220" border="0" class="LoginInput">
				    <tr>
				      <td width="61">&nbsp;&nbsp;&nbsp;用户名：</td>
				      <td width="92">
				      	<input  name="AS_usr"
																type="text" id="AS_usr"
																value="${cookiesValue}"
																size="20"
																class="InputStyle" 
																onkeydown="if(event.keyCode==13){event.keyCode=9;}"
																onclick="thisForm.AS_usr.focus();"
																tabindex="1">
				      </td>
				      <td width="61" rowspan="2"><input name="input2" type="image" src="images/login.gif" width="60" height="53" /></td>
				    </tr>
				    <tr>
				      <td>&nbsp;&nbsp;&nbsp;密&nbsp;&nbsp;&nbsp;&nbsp;码：</td>
				      <td>
				      				<input  name="AS_psw"  class="InputStyle" 
																type="password"
																id="AS_psw"
																value="${cookiesValuePass}"
																size="13"
																tabindex="2"
																>
				
									<input  name="AS_dog"
											type="hidden"
											id="AS_dog"
											value=""
											tabindex="3">
				
									<input name="userScreen"
											type="hidden"
											id="userScreen"
											value="" />
				      
				      </td>
				      </tr>
				    <tr>
				      <td colspan="3" height="30" style="padding:0px;">
						
						<table border=0>
						<tr><td>
				      		<input name="isRemember" type="checkbox" value="yes" id="isRemember" onpropertychange="addit(this,'_isRemember')">
						</td><td>
				        	<label for="isRemember" style="font-size: 12px;" for="isRemember">记住用户名</label> &nbsp;
						</td><td>
				      		<input name="isRememberPass" type="checkbox" value="yes" id="isRememberPass" onpropertychange="addit(this,'_RememberPass')">
					  	</td><td>
				        	<label for="isRememberPass" style="font-size: 12px;" for="isRememberPass">自动登录</label> &nbsp;
						</td></tr>
						
						<tr><td>
						</td><td align="left">
				        	<a href="#" onclick="window.open('cadet.do?method=beforeCadetSkip');" >入职申请</a>
						</td><td>
				      
					  	</td><td align="left">
				        	<a href="#" onclick="window.open('cadet.do');">入职登记</a>
						</td></tr>
						</table>
				        
				      </tr>
				  </table>
				</form>

	</div>
</div>
<div class="footer">Copyright @ <span id=qq2></span> All Rights Reserved.<br>Version 1.1 技术部维护</div>
</div>
</center>
</body>

<script type="text/javascript">

try{
	var host=window.location.host;
	var imagesrc="bg_eaudit.jpg";
	var copyright='天健会计师事务所';
	if (host.indexOf('oazx')>=0){
		imagesrc="bg_eaudit_zx.jpg";
		copyright='浙江凯通企业管理咨询有限公司';
	}
	if (host.indexOf('oasw')>=0){
		imagesrc="bg_eaudit_sw.jpg";
		copyright='浙江天健税务师事务所';
	}
	if (host.indexOf('oagc')>=0){
		imagesrc="bg_eaudit_gc.jpg";
		copyright='浙江天健东方工程投资咨询有限公司';
	}
	if (host.indexOf('oapg')>=0){
		imagesrc="bg_eaudit_pg.jpg";
		copyright='坤元资产评估有限公司';
	} 
        document.title=copyright+"综合管理系统";
	//alert(imagesrc);
	var qqq=document.getElementById('qqq');
	qqq.style.background="url(images/"+imagesrc+") no-repeat";
	var qq2=document.getElementById('qq2');
	qq2.innerHTML=copyright;
}catch(e){}



	var mtoffice;

	//获取主机地址
	function getlocationhost(){
		return "http:\/\/"+window.location.host;
	}

	function checkUserOnline() {
		var userLoginId = document.getElementById("AS_usr").value;
		var password = document.getElementById("AS_psw").value;
	
		var query_String = "&userLoginId=" + userLoginId
						 + "&password=" + password;
	
		var url = "${pageContext.request.contextPath}/onlineUser.do?method=checkUserOnline";
	
		return ajaxLoadPageSynch(url,query_String);
	}

	function loginChk(){
		
		/*
		if(!window.navigator.onLine) {
	  		alert("您的IE已被设成\"脱机工作\",请去掉IE菜单栏中\"文件(F)\"->\"脱机工作(W)\"前的钩后再点击登录! ");
	  		return false;
	  	}
	  	*/
	
		if(thisForm.AS_usr.value==""){
			alert("用户名不能为空！");
			thisForm.AS_usr.focus();
			return false;
		}
		if(thisForm.AS_psw.value==""){
			alert("密码不能为空！");
			thisForm.AS_psw.focus();
			return false;
		}
	
		
	  
		document.getElementById("userScreen").value = window.screen.width;
	
		try {
			var result = checkUserOnline();
	
			//alert(result);
			if(result != 'offLine' && result != 'noUser') {
				//如果用户在线
				if(confirm("该用户已经登录,如果您继续登录的话,将会导致已登录的该用户强行退出!")) {
					try {
						var query_String = "&sessionId=" + result;
						var url = "${pageContext.request.contextPath}/onlineUser.do?method=kickUser";
						ajaxLoadPageSynch(url,query_String);
					} catch(e) {
						//alert(e);
					}
	
					return true;
				} else {
					//用户离线或者用户名密码错误
					return false;
				}
			}
		} catch(e) {
			//alert(e);
		}
	}


	function next() {
		var src = event.srcElement;
		if (event.keyCode != 13)
			return;

		if (src.type == "text") {
			document.getElementById("AS_psw").focus();
			return false;
		}

		if (src.type == "password") {
			//thisForm.submit();
			return true;
		}
	}

	function addit(obj,token){
		if (obj.checked==false){
			//立刻去掉cookie
			delCookie(token);
			
			//如果是去掉记住用户名的勾选，那么也去掉cookie;
			if(token=="_isRemember"){
				delCookie("AuditLastLogin");
			}
			
		}else{
			//设置cookie
			addCookie(token,"1",30*24);
			
			//如果勾选了自动登录，就自动把记住用户名也勾选
			if(token=="_RememberPass"){
				document.getElementById("isRemember").checked=true;
			}
		}
	}
	
	function addCookie(objName,objValue,objHours){//添加cookie
		var str = objName + "=" + escape(objValue);
		if(objHours > 0){//为0时不设定过期时间，浏览器关闭时cookie自动消失
		var date = new Date();
		var ms = objHours*3600*1000;
		date.setTime(date.getTime() + ms);
		str += "; expires=" + date.toGMTString();
		}
		document.cookie = str;
	}

	function getCookie(objName){//获取指定名称的cookie的值
		var arrStr = document.cookie.split("; ");
		for(var i = 0;i < arrStr.length;i ++){
		var temp = arrStr[i].split("=");
		if(temp[0] == objName) return unescape(temp[1]);
		}
		return "";
	}

	function delCookie(name){//为了删除指定名称的cookie，可以将其过期时间设定为一个过去的时间
		var date = new Date();
		date.setTime(date.getTime() - 10000);
		document.cookie = name + "=a; expires=" + date.toGMTString();
	}
	
	function allCookie(){//读取所有保存的cookie字符串
		var str = document.cookie;
		if(str == ""){
		str = "没有保存任何cookie";
		}
		alert(str);
	}

	function init() {
	
		var fulls = "left=0,screenX=0,top=0,screenY=0,scrollbars=1,location=0,status=1,";    //定义弹出窗口的参数
	
		if (window.screen) {
			var ah = screen.availHeight - 30;
			var aw = screen.availWidth - 10;
			fulls += ",height=" + ah;
			fulls += ",innerHeight=" + ah;
			fulls += ",width=" + aw;
			fulls += ",innerWidth=" + aw;
			fulls += ",resizable"
		} else {
			fulls += ",resizable"; // 对于不支持screen属性的浏览器，可以手工进行最大化。
		}

		var win = window.open("login.do?init=true","",fulls);
		
		if(!win) {
			alert('对不起,E审通的弹出窗口给您的浏览器阻止了\n请【关闭弹窗口阻止程序】或【点击】浏览器上方黄色提示框,选择：总是允许来自此站点的弹出窗口'); 
		} else {
			//去掉关闭窗口的提示
			window.opener = null;
			window.open('','_self');
			window.close();
		}
	}
	
	if("${param.init}" != "true") {
		//init();
	}
	
</script>


<script language='javascript'>

	//弹出服务器端的提示
	var serverinfo='${serverinfo}';
	if (serverinfo>''){
		alert(serverinfo);
	}
	//通过读取cookie 设置2个勾选的值
	if (getCookie("_isRemember")=="1"){
		document.getElementById("isRemember").checked=true;
	}
	
	if (getCookie("_RememberPass")=="1"){
		document.getElementById("isRememberPass").checked=true;
	}
	
	var t=getCookie("AuditLastLogin");
	if (t>""){
		document.getElementById("AS_usr").value=t;
	}
</script>

</html>


