<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>新闻查看</title>

<script Language=JavaScript>

	function ext_init(){
	    var tbar = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		defaults: {autoHeight: true,autoWidth:true},
            items:[{
            	text:'关闭',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function () {
            		closeTab(parent.tab);
	            }
       		}
            //,{
            //	text:'返回',
	         //   cls:'x-btn-text-icon',
	          //  icon:'${pageContext.request.contextPath}/img/back.gif',
	          //  handler:function () {
            //		window.history.back();
	         //   }
       		//}
			]
        });
        
    }
    window.attachEvent('onload',ext_init);
</script>

</head>
<body leftmargin="0" topmargin="0">

<div id="divBtn"></div>
 	<div style="overflow: auto;height: 95%">
	<form name="thisForm" action="${pageContext.request.contextPath}/news.do" method="post" scorll="auto">
		<input name="autoId" type="hidden" id="autoId" value="${autoId}" />
		<input name="fileName" type="hidden" id="fileName" value="${fileName}" />
		<input name="filePath" type="hidden" id="filePath" value="${filePath}" />
		
		<table width="100%" border="0" align="center" cellpadding="2" cellspacing="1" bgcolor="#6595d6">
			<tr  class="DGtd">
		      <td height="30" bgColor="d3e1f1" id="title" align="center" style="size: 25px !important; ">${news.title}</td>
		     </tr>
		     <tr>
		     <td height="22" align="right">
			  <span id="Policy_company"></span>
			
			  日期： ${news.updateTime }&nbsp;&nbsp;</td>
			  </tr>
		</table>
		  	<div style="word-wrap:break-word;height: 99%;width:95%;margin-left: 100px;padding-right: 50px;margin-top: 15px;">
		  		<pre>${news.contents}</pre>
		  	</div>
			<div  style="width: 90%;height: 30px;margin-left: 80px;">
			  
			  	<c:forEach items="${list}" var="attach">
					<span id="fileSpan"><a href="#" onclick="down('${attach.fileName}','${attach.filePath}')">${attach.fileName}</a> </span><br><br>
				</c:forEach>
				<jsp:include page="/sysMenuManger/attachView.jsp">
				  <jsp:param value="${news.attachmentId}" name="indexid"/>
				</jsp:include>
		  </div>		   		 		 		
	</form>
	</div>
</body>


<script>
	// 下载 
	function down(fileName,filePath){
		document.getElementById("fileName").value = fileName;
		document.getElementById("filePath").value = filePath;
		document.thisForm.action = "${pageContext.request.contextPath}/news.do?method=download";
		document.thisForm.submit();
			
	}
</script>
</html>
