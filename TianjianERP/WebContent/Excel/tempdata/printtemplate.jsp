<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="com.matech.framework.pub.util.StringUtil" %>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%
	String templateid=request.getParameter("templateid");
	String names=request.getParameter("names");
	String values=request.getParameter("values");
	if (names==null){
		//第一次提交，没参数，直接返回
		return;
	}
	if (names==null)names="";
	if (values==null)values="";
	//这里有一个很奇怪的现象就是如果末尾是`，切割后的数组会找1位；导致names和values的数目不匹配
	names+=" ";
	values+=" ";
	
	String aNames[]=names.split("`");
	String aValues[]=values.split("`");
	System.out.println("aNames.length="+aNames.length);
	System.out.println("aValues.length="+aValues.length);
	
	for (int i=0 ;i<aNames.length;i++){
		System.out.println("aNames["+i+"].value="+aNames[i]);		
	}
	
	for (int i=0 ;i<aValues.length;i++){
		System.out.println("aValues["+i+"].value="+aValues[i]);		
	}
	
	
	String random=StringUtil.getUUID();
%>
<html>
<head>
<title>模板套打</title>

<script type="text/javascript">

Ext.onReady(function(){
	var tbar_customer = new Ext.Toolbar({
		renderTo:'divBtn',
           items:[
           	{text:'打印', 
			 cls:'x-btn-text-icon', 
			 icon:'${pageContext.request.contextPath}/img/print.gif',
			 handler:function(){ print(); }
			 },
/*			 
			 '-',
			{text:'保存打印模板', 
			 cls:'x-btn-text-icon', 
			 icon:'/sdcszj/img/save.gif',
			 handler:function(){savetemplate();}
			 },
*/			 
			 '-',{text:'关闭', 
			 cls:'x-btn-text-icon', 
			 icon:'${pageContext.request.contextPath}/img/close.gif',handler:function(){ 
			 	MT_SYS_PAGECLOSE();
			 	closeTab(parent.parent.mainTab);
			 }},'->']
        });  
	
});

</script>


</head>
<body style=" padding: 0px; margin: 0px;">
	<div id="divBtn"></div>

<div id='exceldiv' style="display:none">
<object classid="clsid:C20572B8-6104-45B8-A3EE-303B42C26ABF" id="oframe" width="100%" height="96%">
	<param name="BorderStyle" value="1">
	<param name="SideBarVisible" value="0">
	<param name="Titlebar" value="0">
	<param name="Menubar" value="1">
</object>
</div>

<div id='wpsdiv' style="display:none">
<OBJECT id="wpsoframe" width="100%" height="96%" 
	classid="clsid:23739A7E-2000-4D1C-88D5-D50B18F7C347" codebase="iWebOffice2000.ocx#version=7,2,6,0">
</OBJECT>
</div>


<script language="javascript" >

var AuditReport =null;
var oframe=null;
var wpsoframe=null
try{
	AuditReport=new ActiveXObject("AuditReportPoject.AuditReport");
	oframe=document.getElementById('oframe');
	AuditReport.pDSOFramer=oframe;
	wpsoframe=document.getElementById('wpsoframe');
	
	if (!AuditReport || !oframe){
		//出错了，说明控件安装不成功，导航到专门的安装界面
		alert("验证控件安装失败或者没安装,请安装,详细错误原因:"+e);
		window.location="${pageContext.request.contextPath}/ocx/ocx.jsp";
	}
}catch(e){
	alert("验证控件安装失败或者没安装,请安装,详细错误原因:"+e);
	window.location="${pageContext.request.contextPath}/ocx/ocx.jsp";
}

//---------------
//获取主机地址
//---------------
function getlocationhost(){
	return "http:\/\/"+window.location.host;
}

function print(){
	AuditReport.funPrint();
}

 

/**
* 去除字符串str头尾的空格
* @param str 字符串
* @return str去除头尾空格后的字符串。
*/
function trim(str){
	if(str == null) return "" ;
	
	// 去除前面所有的空格
	while( str.charAt(0) == ' ' ){
		str = str.substring(1,str.length);
	}
	
	// 去除后面的空格
	while( str.charAt(str.length-1) == ' ' ){
		str = str.substring(0,str.length-1);
	}

	return str ;
}


//用EXCEL
function openfile(){
	
	document.getElementById('exceldiv').style.display="";

	AuditReport.pFileName = '<%= random %>.xls';
    AuditReport.pOpenUrl = getlocationhost()+'${pageContext.request.contextPath}/Excel/template/Download1.jsp?filename=<%= templateid %>';
	AuditReport.funOpenUrlFile();
<%
	for (int i=0 ;i<aNames.length;i++){
%>
	AuditReport.subSetObjValueByToken(trim('<%=aNames[i] %>'),trim('<%=aValues[i] %>'))
<%
	}
%>
	
	//加保护，限制修改
	AuditReport.subProtectDocument();
}


//用WPS
function openwpsfile(){
	
	document.getElementById('wpsdiv').style.display="";

	//下载文件到临时目录
	AuditReport.pFileName = AuditReport.funGetTempDir() + '<%= random %>.et';
    AuditReport.pOpenUrl = getlocationhost()+'${pageContext.request.contextPath}/template/<%= templateid %>.et';

    AuditReport.funDownloadFile(AuditReport.pOpenUrl, AuditReport.pFileName);
	
	//不显示菜单
	wpsoframe.ShowMenu="1"	
	
	wpsoframe.WebOpenLocalFile(AuditReport.pFileName);
	
	if (!wpsoframe.WebObject){
    	alert('wps文件未正常打开');
    	return false;
    }
	
	//设置值
<%
	for (int i=0 ;i<aNames.length;i++){
%>
	subWpsSetObjValueByToken(trim('<%=aNames[i] %>'),trim('<%=aValues[i] %>'))
<%
	}
%>
	
	//加保护，限制修改
	wpsoframe.WebObject.ActiveSheet.Protect("123456");
}

function MT_SYS_PAGECLOSE(){
	try{
		if (AuditReport){
			AuditReport.funCloseFile();
		}
		if (wpsoframe && wpsoframe.WebObject){
			wpsoframe.WebClose();
		}
	}catch(e){}
}


function subWpsSetObjValueByToken(strToken,strValue){

    try{
    
	    var a , i,myWb;
	    
	    myWb=wpsoframe.WebObject;
	    
	    if (!myWb){
	    	return false;
	    }
	    
	    //先做输入框赋值
	    for( i = 1;i<= myWb.ActiveSheet.Shapes.Count;i++){
	        a = myWb.ActiveSheet.Shapes.Item(i)
	        try{
	        	if (a.AlternativeText == strToken || a.Name == strToken){
	            	a.Text = strValue;
	            }
			}catch(e){}    
			a=null;    
	    }
	    
	    //再做名称定位赋值
	    var t,s,sheetname,rangeaddress
	    try{
	    	t=myWb.Names(strToken);
	    	if (t){
	    		s = t.RefersTo;
	    		sheetname=s.substr(1,s.indexOf("!")-1);
	    		rangeaddress=s.substr(s.indexOf("!")+1);
	    		myWb.Worksheets(sheetname).Range(rangeaddress).Value = strValue;
	    	}
		}catch(e){}    
	    t=null;
	    
	    myWb=null;
	}catch(e){
		a=null;
		myWb=null;
	}
}

try{
	if (AuditReport.funCheckWpsInstalled()){
		openwpsfile();
	}else{
		openfile();
	}
}catch(e){
	alert(e);
	alert("控件需要升级，点击切换到升级界面");
	window.location="${pageContext.request.contextPath}/ocx/ocx.jsp";
}

</script>

</body>
</html>
