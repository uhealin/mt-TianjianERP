<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>






<%
  
  String check=request.getParameter("check");
  
  
%>
	
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="../AS_CSS/css_main.css" rel="stylesheet" type="text/css">

</head>

<body leftmargin="0" topmargin="0">
<form name="thisForm" method="post" action="dataupload.jsp" id="thisForm" enctype="multipart/form-data">
  <font color="#FF0000" size="2" ><strong>您现在所在位置: </strong><font color="#0000CC">系统管理-&gt;插件安装</font></font><br>
  <table width="500"  border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td height="1" bgcolor="#0000FF"></td>
  </tr>
</table>

<br>
  <br>
  <br>
<div style="display:none">
<OBJECT ID="AuditReport"
CLASSID="CLSID:7CF3E9B9-2A03-4270-9017-B427FE717939"
CODEBASE="./AuditReportPoject.CAB#version=1,0,0,0">
</OBJECT>
</div>
<div class="qrblk" align="center">

        <fieldset  style="width:75%">
            <legend>插件安装向导</legend>
            <p align="center"><b>在线安装</b></p>
          <ul>
            <li>
              <p align="left">您的在线编辑器由于缺乏必要的插件而无法正常运行。您需要按以下步骤执行：</p>
			  </li>
			<li>
			   <p align="left">1.点击下载 [<a href="./Audit.exe">本地安装程序</a>]，您将会得到一个      
              可执行文件，请另存到本地并执行一次。</p>
			</li>
			<li>
			   <p align="left">2.执行后请点击<input type=button value="刷新并启用" onclick="window.location=window.location+'?check=1'">，
				当前页面将刷新，请选择启用控件。
			   </p>
			</li>
			<li>
			   <p align="left">3.启用成功后请点击<input type=button value="关闭并返回" onclick="opener.location=opener.location;window.close();">，
				即可开始编辑操作。
			   </p>
			</li>
            <li>
              <p align="left">如果您始终看到本界面，无法进入在线编辑OFFICE界面，请与系统管理员取得联系。</li>
          </ul>
      
          </fieldset>

</div>

<div style="display:none" >
<object classid="clsid:6BA21C22-53A5-463F-BBE8-5CF7FFA0132B" id="oframe" width="100%" height="98%">
         <param name="BorderStyle" value="1">
         <param name="SideBarVisible" value="0">
         <param name="Titlebar" value="0">
         <param name="Menubar" value="0">
       </object>
</div>


<script type="text/javascript">

	var AuditReport,oframe;

     function init(){
    	 try{
    		AuditReport =  new ActiveXObject("AuditReportPoject.AuditReport");
    		AuditReport.pUTF8=true;
    		oframe = document.getElementById("oframe");
			//aaa();
    		AuditReport.pDSOFramer=oframe;
			
			
			alert('初始化成功！');
		}catch(e){
			alert('安装失败，请检查您是否下载执行了Audit.exe，或者在页面刷新后选择了启用控件:'+e);
		}
     }
	 
	 if('<%=check %>'=='1'){
		init();
	 }
 </script>

 
</body>
</html>

