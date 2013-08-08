<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
 
<html>
<head>


<%
response.addHeader("Cache-Control", "no-cache");
response.addHeader("Expires", "Thu, 01 Jan 1970 00:00:01 GMT");
%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<META HTTP-EQUIV="Pragma" CONTENT="no-cache"> 
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<META HTTP-EQUIV="expires" CONTENT="Wed, 26 Feb 1997 08:21:57 GMT"> 

<title>会议时间</title>
 <style type="text/css">

	.tTable {margin-top:10px;border:white 1px solid;border-collapse:collapse;}
	.tTable td,th {
		padding: 5 5 5 1px;text-align: left;white-space:nowrap;border-top:white 1px solid;border-left: white 1px solid;height:30px;
	}
	.tTable input {border:1px solid #d7e2f3;}
	
</style>
 
</head>
<body >

<form id="thisForm" name="thisForm" method="post" action="">
<br/>
&nbsp;&nbsp;&nbsp;<input type="button" id="print" value="  打 印" onclick="window.print();" style="width: 60px;background-image: url('img/print.gif');background-repeat: no-repeat;background-color: #d7e2f3">

	<div>
		<table class="tTable" width="50%" style="margin-left: 5%;">
			<tr style="border: 1px solid red">
				<td ><img src="images/LXDH_logo.png" style="width: 200px;"></td>
				<td></td>
				<td align="left"><font style="font-size: 35px;font-weight: bold">大 华 会 计 师 事 务 所</font></td>
				<td></td>
			</tr>
		</table>
	</div>
		<br><br><br><br><br><br> 
	
	<div style="text-align: center;">
		<table class="tTable" width="60%">
			<tr>
				<td colspan="4" style="text-align: center;"><font style="font-size: 45px">${mo.meetingRoomId}</font></td>
			</tr>
		</table>
		<br> 
		
		<table class="tTable" width="750" cellspacing="35">
			<tr>
				<td style="text-align: right;font-size: 26px;">会议名称：</td>
				<td style="font-size: 26px;width: 650px;">${mo.name}</td>
			</tr>	
			<tr>
				<td style="text-align: right;font-size: 26px;">部门：</td>
				<td style="font-size: 26px;width: 650px;">${departname}</td>
			</tr>	
			<tr>
				<td style="text-align: right;font-size: 26px;">参会人员：</td>
				<td style="font-size: 26px;width: 650px;">${joinUser}</td>
			</tr>		
			<tr>
				<td style="text-align: right;font-size: 26px;">会议时间：</td>
				<td style="font-size: 26px;width: 650px;">${meetingTime}</td>
			</tr>		
		</table>
	</div>

</form>

</body>

<script type="text/javascript">
	var HKEY_Root,HKEY_Path,HKEY_Key;    
	HKEY_Root="HKEY_CURRENT_USER";    
	HKEY_Path="\\Software\\Microsoft\\Internet Explorer\\PageSetup\\";   
	
	//设置网页打印的页眉页脚和页边距   
	function PageSetup_Null()    
	{    
		 try    
		 {    
			  var Wsh=new ActiveXObject("WScript.Shell");    
			  HKEY_Key="header";    
			//设置页眉（为空）   
			  Wsh.RegWrite(HKEY_Root+HKEY_Path+HKEY_Key,"");    
			  HKEY_Key="footer";    
			//设置页脚（为空）   
			  Wsh.RegWrite(HKEY_Root+HKEY_Path+HKEY_Key,"");   
		}catch(e){
			//alert(111);
		 }    
	}       

	window.onbeforeprint = function (){
		PageSetup_Null();
		document.getElementById("print").style.display = "none" ;
	}
	
	window.onafterprint = function (){
		document.getElementById("print").style.display = "" ;
	}


	  
     // 打印设置  
     function doPrintSet(){   
         document.all.WebBrowser.ExecWB(8,1);
     }   


	//加入页面保护   禁止复制(copy),禁用鼠标右键!
    function rf()
    {return false; }
    document.oncontextmenu = rf
    function keydown()
    {if(event.ctrlKey ==true || event.keyCode ==93 || event.shiftKey ==true){return false;} }
    document.onkeydown =keydown
    function drag()
    {return false;}
    document.ondragstart=drag
    function stopmouse(e) {
    if (navigator.appName == 'Netscape' && (e.which == 3 || e.which == 2))
    return false;
    else if
    (navigator.appName == 'Microsoft Internet Explorer' && (event.button == 2 || event.button == 3)) {
    return false;
    }
    return true;
    }
    document.onmousedown=stopmouse;
    if (document.layers)
    window.captureEvents(Event.MOUSEDOWN);
    window.onmousedown=stopmouse;


</script>

</html>
