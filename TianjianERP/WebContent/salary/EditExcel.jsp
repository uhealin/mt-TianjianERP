<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%

	String refresh=request.getParameter("refresh");
	if (refresh!=null && !refresh.equals("")){
		request.setAttribute("refresh",refresh);
	}

	String filenames=(String)request.getAttribute("filenames");
	if (filenames==null)filenames="";
	String[] filename=filenames.split(",");
	
	String mode=(String)request.getAttribute("mode");
	
%>
<html>
<head>
<title>工资编辑</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>


<script type="text/javascript">

Ext.onReady(function(){
	 
    //修改工资的工具条
	var tbar_customer_upload = new Ext.Toolbar({
		renderTo:'divBtnUpdate',
           items:[
           
<%
	//是不是查看
	if (!"view".equals(mode)){
%>           
        {
            text:'保存',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/save.gif',
            handler:function(){
            	save();
			}
      	},'-',
<%
	//合并
	}
%>         	
      	{
			text:'关闭',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/close.gif',
			handler:function(){
				beforeWindowClosed();window.close();
			}
		},'-','<a href="${pageContext.request.contextPath}/AS_SYSTEM/Audit.exe" target="blank">点击下载在线编辑控件','-','<font color=red>如果无法滚动或直接编辑，请在Excel上双击后再继续</font>','->'
        ]
   });  
	

});

</script>

</head>
<body onunload="beforeWindowClosed();">
<div id="divBtnUpdate"></div>
       <object classid="clsid:C20572B8-6104-45B8-A3EE-303B42C26ABF" id="oframe" width="100%" height="95%">
         <param name="BorderStyle" value="1">
         <param name="SideBarVisible" value="0">
         <param name="Titlebar" value="0">
         <param name="Menubar" value="1">
       </object>

<script language="javascript" type="text/javascript">

var AuditReport =  new ActiveXObject("AuditReportPoject.AuditReport");

function save(){
  try{
    if (AuditReport.pFileOpened){
    
		//保存数据内容到后台数据库表
		var url=getlocationhost()+"${pageContext.request.contextPath}/AS_SYSTEM/saveSalaryExcelByTxt.jsp?pch=${pch}&userid=${userSession.userId}&areaid=${userSession.areaid}";
		AuditReport.funSaveExcelToUrl1(url);
	
		//保存文件到服务器
		AuditReport.pSaveUrl = getlocationhost()+"${pageContext.request.contextPath}/AS_SYSTEM/saveSalaryExcel.jsp?pch=${pch}";
		AuditReport.pZipByClient=true;
		var t=AuditReport.funSaveUrlFile();
		
		//prompt("",t);
		alert(t);
      
    }
  }catch(e){}
}


//获取文件的扩展名
function getfilenameext(filename){
  return filename.replace(/(.*\.)/g,'').toUpperCase();
}

//获取主机地址
function getlocationhost(){
  return "http:\/\/"+window.location.host;
}

function beforeWindowClosed(){
  try{
    if (AuditReport.pFileOpened){
      AuditReport.funCloseFile();
	   AuditReport.pDSOFramer=null;
    }
  }catch(e){}
}



var orifilename="<%= filename[0] %>";

//打开文件
try{

	AuditReport.pDSOFramer=oframe;
	AuditReport.pUTF8=true;
	
	AuditReport.pFileName = "${pch}.xls";

	var url="";
	
	if ('true'=='${bExists}'){
		//文件已经存在
		url=getlocationhost()+"${pageContext.request.contextPath}/salary/Download.jsp?filename=<%= filename[0] %>";
		AuditReport.pZipByClient=true;
		
		AuditReport.pOpenUrl = url;
		AuditReport.pUrlParameter= getlocationhost()+"|&userid=${userSession.userId}";
		
		AuditReport.funDownloadFileZipByClient(url, 'c:\\'+AuditReport.pFileName);
		//alert('c:\\'+AuditReport.pFileName);
		
		AuditReport.subOpenLocalFile('c:\\'+AuditReport.pFileName );
	}else{
		//初次创建
		url=getlocationhost()+"${pageContext.request.contextPath}/Excel/tempdata/Download.jsp?filename=<%= filename[0] %>";
		
		AuditReport.pOpenUrl = url;
		AuditReport.pUrlParameter= getlocationhost()+"|&userid=${userSession.userId}";
		
		AuditReport.funDownloadFile(url, 'c:\\' + orifilename);
		AuditReport.subOpenLocalFile('c:\\' + orifilename);	
	}
	

	
	
	
	/*
	
	*/

	var fileName ,tempfile,url;
	
<%
	//合并
	for(int i=1; i<filename.length ; i++){
%>
	
			fileName = "<%= filename[i] %>";
			tempfile = AuditReport.funGetTempDir() + fileName;
			url=getlocationhost()+"${pageContext.request.contextPath}/Excel/tempdata/Download.jsp?filename=<%= filename[i] %>";
			result=AuditReport.funDownloadFile(url,tempfile);
			if (result==""){
				AuditReport.funCombineFile(tempfile);
			}else{
				alert("下载指定底稿失败:"+result);
			}
<%
	}
%>		
	
	//转换公式
	AuditReport.subSetTextToFomula();
		
}catch(e){
  alert(e);
	   //出错了，说明控件安装不成功，导航到专门的安装界面
  window.location="${pageContext.request.contextPath}/AS_SYSTEM/ocxsetup.htm";
}






try {
	window.opener=null;

	var width = window.screen.width;
	var height = window.screen.height-30;
	window.moveTo(0,0);	//移动窗口
	window.resizeTo(width,height); //改变窗口大小
}catch(e){}

</script>

</body>
</html>