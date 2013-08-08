<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>我发起的工作</title>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/searchSession.js" charset=gbk></script>
 
</head>
<body>
<div style="height:expression(document.body.clientHeight);">
	<mt:DataGridPrintByBean name="myApplyList" /> 
</div>
</body>
<script type="text/javascript">

function openUrl(url,name) {
	var tab = parent.tab ;
	if(tab && tab.id == "mainFrameTab"){
		try{
			n = parent.parent.tab.add({    
				'title':name,  
				 closable:true,  //通过html载入目标页    
				 html:'<iframe scrolling="no" frameborder="0" width="100%" height="100%" src="' + url + '"></iframe>'   
			});    
			parent.parent.tab.setActiveTab(n);
		}catch(e){
			window.location = url ;
		}
	}else {
		window.location = url ;
	}
	
}


function goView(key,pId,pName) {
	//if(key == 'BusinessTakeKey') {
	//	openUrl('${pageContext.request.contextPath}/businessTake.do?method=processInfo&showTool=true&processInstanceId='+pId,pName) ;
	//}else if(key == 'TaskAuditKey'){
		openUrl('${pageContext.request.contextPath}/commonProcess.do?method=viewImage&id='+pId,pName) ;
	//}
}

</script>
</html>


