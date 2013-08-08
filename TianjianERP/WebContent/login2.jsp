<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<%@ page import="java.util.*"%>
<%@ page import="java.io.* "%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="com.matech.framework.pub.sys.UTILSysProperty"%>
<%@ page import="java.sql.Connection"%>
<%@ page import="com.matech.audit.pub.db.DBConnect"%>
<%@ page import="com.matech.framework.pub.db.DbUtil"%>

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

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<%
response.addHeader("Cache-Control", "no-cache");
response.addHeader("Expires", "Thu, 01 Jan 1970 00:00:01 GMT");
%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<META HTTP-EQUIV="Pragma" CONTENT="no-cache"> 
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<META HTTP-EQUIV="expires" CONTENT="Wed, 26 Feb 1997 08:21:57 GMT"> 
<Meta name="Robots" Content="None">

<title><%=title %>_登录</title>


<SCRIPT LANGUAGE=JAVASCRIPT>
<!--
if (top.location != self.location)top.location=self.location;
// -->
</SCRIPT>
</head>
<body  leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" style="FILTER: progid:DXImageTransform.Microsoft.Gradient(gradientType=0,startColorStr=#438CCF,endColorStr=#ffffff) revealTrans(duration=3,transition=0) ;">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td>
		<table width="601" height="410" border="0" align="center">
			<tr>
				<td background="images/bg_<%=logo %>.gif">
				<p>&nbsp;</p>
				<p>&nbsp;</p>
				<table width="100%" border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td width="34%" height="90">&nbsp;</td>
						<td width="34%" height="90" align="center" valign="middle"><br>

							<div id=state1 name=state1 style="display:none">E审通正在帮您自动升级客户端程序，<br/>请稍候...
								<img src="AS_INCLUDE/indicator.gif" />
							</div>

							<div id=state3 name=state3 style="display:none">
										E审通正在自动升级客户端程序，请稍候...
								<OBJECT ID="AuditReport" CLASSID="CLSID:7CF3E9B9-2A03-4270-9017-B427FE717939"
										CODEBASE="AuditReportPoject.CAB#version=2,1,0,4">
								</OBJECT>
							</div>

							<div id=state2 name=state2 >
							<form action="${pageContext.request.contextPath}/login.do" method="post" name="thisForm" onsubmit="return next();">
							<table width="98%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<td height="16" colspan="2"><br>
									</td>
								</tr>

								<tr>
									<td width="35%" height="33" align="center">
										<img src="images/yonghu.gif" width="65" height="24">
									</td>
									<td width="65%" align="left">
										<input  name="AS_usr"
												type="text" id="AS_usr"
												value="${cookiesValue}"
												size="20"
												style="height:18px;width:125px"
												onkeydown="if(event.keyCode==13){event.keyCode=9;}"
												onclick="thisForm.AS_usr.focus();"
												tabindex="1">
									</td>
								</tr>

								<tr>
									<td align="center">
										<img src="images/mima.gif" width="65" height="24">
									</td>

									<td align="left">
										<input  name="AS_psw"
												type="password"
												id="AS_psw"
												value=""
												size="13"
												style="height:18px;width:125px"
												tabindex="2"
												>

										<input  name="AS_dog"
												type="hidden"
												id="AS_dog"
												value=""
												style="height:18px;width:125px"
												tabindex="3">

										<input name="userScreen"
												type="hidden"
												id="userScreen"
												value="" />
									</td>
								</tr>
								<tr>
									<td height="0" align="center" valign="middle" colspan="2" nowrap>
										<font size=2 color=red>&nbsp;${serverinfo}</font>
									</td>
								</tr>

								<tr>
									<td colspan="2" height="30" align="center" valign="top">
										<input name="isRemember" checked type="checkbox" value="yes" id="isRemember" style=" margin-left: 35px; margin-top: 0px;"><label for="isRemember" style="font-size: 12px;">记住用户名</label>
									</td>
								</tr>
								<tr>
									<td colspan="2" height="30" align="center" valign="top">
										<input
												type="submit"
												value="登 录"
												tabindex="3"

												onclick="return loginChk();"
												style=" FILTER: progid:DXImageTransform.Microsoft.Gradient(gradientType=0,startColorStr=#438CCF,endColorStr=#006699) revealTrans(duration=3,transition=0);
														background-color:#006699;border: 0px; color: #ffffff; font-size: 12px; width: 50px; height: 22px;" />
										<input
												type="button"
												value="入职申请"
												tabindex="3"
												onclick="window.open('cadet.do?method=getOpt&opt=1');"
													style=" FILTER: progid:DXImageTransform.Microsoft.Gradient(gradientType=0,startColorStr=#438CCF,endColorStr=#006699) revealTrans(duration=3,transition=0);
														background-color:#006699;border: 0px; color: #ffffff; font-size: 12px; width: 60px; height: 22px;"
											 />
												<!--  onclick="window.location.href='cadet.do?method=getOpt&opt=1';"-->
														
										<input
												type="button"
												value="实习生登记"
												tabindex="3"
												onclick="window.open('cadet.do');"
												style=" FILTER: progid:DXImageTransform.Microsoft.Gradient(gradientType=0,startColorStr=#438CCF,endColorStr=#006699) revealTrans(duration=3,transition=0);
														background-color:#006699;border: 0px; color: #ffffff; font-size: 12px; width: 70px; height: 22px;"
				  								/>
											<!--  	onclick="window.location.href='cadet.do';"-->
									</td>
					
									
								</tr>
							</table>
							</form>
							</div>

						</td>

						<td width="32%">&nbsp;</td>
					</tr>
				</table>

				<p>&nbsp;</p>
				</td>
			</tr>
		</table>
		</td>
	</tr>
</table>



<script type="text/javascript">

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

var mtoffice;



/**
 * 实习生登记 
 */
 
 function loginDJ(){
	alert("");
	
	window.open("cadet.jsp");
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

	try {
		if(!mtoffice) {
			mtoffice =  new ActiveXObject("MTOffice.WebOffice");
		}

		document.getElementById("AS_dog").value = mtoffice.funReadDog();
	} catch(e) {
		mtoffice = null;
	}


	try {
		AuditReport.subRegDll("c:\\Audit\\MatechOfficeAddIn.dll",true);
		
		//暂停注册
		//AuditReport.subRegDll("c:\\Audit\\MTScheduling.ocx",true);
		//AuditReport.subRegDll("c:\\Audit\\UniteReport.ocx",true);
		//alert('注册成功');
		
	} catch(e) {
		//alert(e);
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


<%
	String sign=(String)request.getParameter("move");
	sign = sign == null ? "" : sign;

	if(sign.equals("1")){
		out.write("fadein();");
 	}
 %>
	function fadein(){
	    var cur=23;
	    var bodytext=document.body.innerHTML;
		document.body.innerHTML='';
		document.body.filters.revealTrans.Transition=cur;
		document.body.filters.revealTrans.apply();
		document.body.innerHTML=bodytext;
		document.body.filters.revealTrans.play();
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

	function test(){
		try {
				AuditReport.funBeginSyncDownload("http://127.0.0.1:5199/AuditSystem/ocx/cd1.rmvb", "c:/cd1.rmvb","1.rm");
		} catch(e) {
				alert(e);
		}
	}


	var bcheckUpdateStatus =false;
	var iCheckCount=1;
	function checkUpdateStatus(){

		try{
			var result=AuditReport.pUpdateStatus;
			if (result == "成功"){
				bcheckUpdateStatus =false;

				document.getElementById("state2").style.display="";
				document.getElementById("state3").style.display="none";

				alert('E审通需要关闭网页更新控件，点击确定后本页面将关闭');
				window.close();
			}
			
			if (result == "成功无须关闭"){
				
				bcheckUpdateStatus =false;

				document.getElementById("state2").style.display="";
				document.getElementById("state3").style.display="none";
				
			}else if (result && result.indexOf("失败")>=0){
				//失败了
				bcheckUpdateStatus =false;
				alert(result);
				document.getElementById("state3").style.display="none";
				document.getElementById("state2").style.display="";
			}

			/*
			if (updatestatus){
				updatestatus.innerHTML="控件状态："+AuditReport.pUpdateStatus ;
			}
			*/

		} catch(e){
			iCheckCount ++;

			if (newmode==false && iCheckCount >200){
				alert('E审通需要关闭网页更新控件，点击确定后本页面将关闭');
				window.close();

				document.getElementById("state3").style.display="none";
				document.getElementById("state2").style.display="";

			}
		}

		if (bcheckUpdateStatus)
			setTimeout(checkUpdateStatus,100);
	}


/*
	function AuditReport::DownloadError(savefile,strerr){
		alert('更新'+savefile+'失败：'+strerr+",本次升级放弃");

		document.getElementById("state3").style.display="none";
		document.getElementById("state2").style.display="";

	}

	function AuditReport::DownloadComplete(savefile){
		alert('E审通需要关闭网页更新控件，点击确定后本页面将关闭');
		window.close();
	}
*/

  //验证有没有安装控件
  	try {
		if (!AuditReport){
			//出错了，说明控件安装不成功，导航到专门的安装界面
			alert("验证控件安装失败或者没安装,请安装。");
		    window.location="${pageContext.request.contextPath}/AS_SYSTEM/ocxsetup.htm";
		}
	}catch(e){
		alert("验证控件安装失败或者没安装,请安装,详细错误原因:"+e);
		//出错了，说明控件安装不成功，导航到专门的安装界面
	    window.location="${pageContext.request.contextPath}/AS_SYSTEM/ocxsetup.htm";
	}

	//新版升级
	var strFileNames='<%= strFileNames %>',strFileDates='<%= strFileDates %>',strFileSizes='<%= strFileSizes %>';
	AuditReport.pUrlParameter= getlocationhost()+"|";

	var newmode=true;
	try {

	  	if ( AuditReport.funNewCheckUpdateFile(strFileNames,strFileDates,strFileSizes)==1){

	  		document.getElementById("state2").style.display="none";
			document.getElementById("state3").style.display="";

			bcheckUpdateStatus=true;
			checkUpdateStatus ();

	  		AuditReport.subSyncDownloadOcx();
	  	}else{

	  		//创建控件
			try {
				mtoffice = new ActiveXObject("MTOffice.WebOffice");
			} catch(e) {
				mtoffice = null;
			}
	  	}

	}catch(e){
		newmode =false;
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
		
	${cookiesValue1}

</script>


</body>
</html>
