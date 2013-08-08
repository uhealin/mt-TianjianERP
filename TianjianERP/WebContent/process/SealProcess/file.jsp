<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
    <%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
    <%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>查看附件</title>
<script type="text/javascript">
Ext.onReady(function() {
 	new Ext.Toolbar({
			renderTo : "divBtn",
			height : 30,
			defaults : {
				autoHeight : true,
				autoWidth : true
			},
			items : [ {
				text : '查看终稿',
				icon : '${pageContext.request.contextPath}/img/past.gif',
				handler : function() {
					lookCase(false); 
				}
			},'-',{
				text : '查看修改痕迹',
				icon : '${pageContext.request.contextPath}/img/mytask.gif',
				handler : function() {
					lookCase(true);
				}
			},'-',{
				text : '关闭',
				icon : '${pageContext.request.contextPath}/img/close.gif',
				handler : function() {
				  closeFile();
				  closeTab(parent.tab);
				//	lookCase(true);
				}
			}]
		});
	})
</script>
</head>
<body onunload="closeFile();" style=" padding: 0px; margin: 0px;">
   <div id="divBtn"></div>
    <div id="sealFile"  style="height:expression(document.body.clientHeight);width:100%">
			
			<object classid="clsid:C20572B8-6104-45B8-A3EE-303B42C26ABF" id="oframe" width="100%" height="100%">
	         	<param name="BorderStyle" value="1">
	        	<param name="SideBarVisible" value="0">
	       	 	<param name="Titlebar" value="0">
	      	   	<param name="Menubar" value="1">
	       </object>
			
	</div>
 
</body>


<script FOR=AuditReport EVENT=HaveNavigate>
	//window.open(AuditReport.pNavegateUrl);
</script>

<script type="text/javascript">
		var AuditReport =  new ActiveXObject("AuditReportPoject.AuditReport");
		AuditReport.pUTF8=true;
		AuditReport.pDSOFramer=oframe;
		fileOpen('${attachFileName}','${attachId}');
		
		//获取主机地址
		function getlocationhost(){
			return "http:\/\/"+window.location.host;
		}
		
		function fileOpen(fileName,textFileName) {
			AuditReport.pOpenMode=0;
			AuditReport.pFileName = fileName; 
			AuditReport.pAppUser="${userSession.userName}";
			AuditReport.pTrackRev=true;
			//查看终稿、
			//AuditReport.pShowRev=false;
			//查看修改痕迹，用
			//AuditReport.pShowRev=true;
			
			var openUrl = getlocationhost() + "${pageContext.request.contextPath}/seal.do?method=downloadFile&textFileName="+textFileName+"&indexTable=seal";
			var saveUrl = getlocationhost() + "${pageContext.request.contextPath}/seal.do?method=uploadFile&indexTable=seal&fileTempName="+textFileName+"&attachId=${attachId}";

			AuditReport.pFileDir="c:\\manu\\workflow" ;
			//AuditReport.pZipByClient=true; //
			AuditReport.pOpenUrl = openUrl;
			AuditReport.pSaveUrl = saveUrl;
			AuditReport.pPrintCountUrl=getlocationhost() + "${pageContext.request.contextPath}/AS_SYSTEM/getPrintCount.jsp?uuid=${uuid}";
			//var UrlParameter="&manuname=1";
			//AuditReport.pUrlParameter="http:\/\/"+window.location.host;
			var UrlParameter="&curProjectid="
							+ "&curPackageid="
							+ "&taskid="
							+ "&userId=${userSession.userId}"
							+ "&curTaskCode="
							+ "&sessionId=<%=session.getId()%>"
							+ "&projectId=123123"
							+ "&readonly=false"
							+ "&userName=${userSession.userName}"
							+ "&manuname=" + fileName;
			AuditReport.pUrlParameter=getlocationhost() + "|" + UrlParameter;
			AuditReport.funOpenUrlFile();
	} 
		function lookCase(p){
				AuditReport.pShowRev=p;
		}

		function closeFile(){
				try{
					AuditReport.funCloseFile();
					//AuditReport.FunCloseFile();
			}catch(e){}
		}
</script>
</html>