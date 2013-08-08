<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>等待我办理的工作</title>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/searchSession.js" charset=gbk></script>
 
</head>
<body>
<div style="height:expression(document.body.clientHeight);">
	<mt:DataGridPrintByBean name="myDealList" /> 
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


function goDeal(key,taskId,pName) {
    if(key=="sealApplyFlow"){
    		openUrl("${pageContext.request.contextPath}/seal.do?method=auditSkip&taskId="+taskId,pName) ;
    }else if(key=="leaveFlow"){
             openUrl("${pageContext.request.contextPath}/leave.do?method=auditSkip&taskId="+taskId,pName) ;
    }else if(key=="massControl"){
    	     openUrl("${pageContext.request.contextPath}/businessProject.do?method=auditSkip&taskId="+taskId,pName) ;
    }else if(key=="leaveOfficeFlow"){
             openUrl("${pageContext.request.contextPath}/leaveOffice.do?method=auditSkip&taskId="+taskId,pName) ;	    
    }else if(key=="userPrefermentFlow"){
    		   openUrl("${pageContext.request.contextPath}/userPreferment.do?method=auditSkip&taskId="+taskId,pName) ;
    }else if(key=="destroyLeaveFlow"){
    		   openUrl("${pageContext.request.contextPath}/leave.do?method=auditSkip&taskId="+taskId+"&ctype='销假'",pName) ;	    
    }else if(key=="meetingOrderFlow"){
               openUrl("${pageContext.request.contextPath}/meetingOrderSy.do?method=toAuidt&taskId="+taskId,pName) ;	    
    }else if(key=="docPostKey"){
               openUrl("${pageContext.request.contextPath}/document.do?method=audit&taskId="+taskId,pName) ;	    
    }else if(key=="waresApplyFlow"){	    
               openUrl("${pageContext.request.contextPath}/waresStockSy.do?method=auditSkip&ctype=bumen&uuids="+taskId,pName) ;	    
    }else if(key=="waresCancelFlow"){
               openUrl("${pageContext.request.contextPath}/waresStockSy.do?method=cancelAuditSkip&taskId="+taskId,pName) ;	    	       
    }else if(key=="proclamationFlow"){
               openUrl("${pageContext.request.contextPath}/proclamationSy.do?method=auditSkip&taskId="+taskId,pName) ;	    	       	    
    }else if(key=="reportDutyFlow"){
               openUrl("${pageContext.request.contextPath}/job.do?method=auditSkip&taskId="+taskId,pName) ;	    	       	    	    
    }
}


</script>
</html>


