<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ taglib uri="http://ckeditor.com" prefix="ckeditor" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/ckeditor/ckeditor.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/ckfinder/ckfinder.js"></script>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title></title>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/images/editor.js" charset=GBK></script>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/images/DhtmlEdit.js" charset=GBK></script>

<style>
	#t {
		{border-collapse:collapse;border:none;};
	}
	
	#t td{
		{border:solid #6595d6 1px;};
	}
	
	
</style>
<script Language=JavaScript>

	function ext_init(){
	
	    var tbar = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		defaults: {autoHeight: true,autoWidth:true},
            items:[{
	            text:'修改',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/edit.gif',
	            handler:function(){
	            	return setBargainType();
				}  
       		},'-',{
	            text:'返回',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/back.gif',
	            handler:function(){
					window.history.back();
				}
       		},'->'
			]
        });
        
    }
    window.attachEvent('onload',ext_init);
</script>

</head>
<body leftmargin="0" topmargin="0">

<div id="divBtn"></div>


 
<jodd:form bean="news" scope="request">
	<form name="thisForm" action="${pageContext.request.contextPath}/employeeedit.do" class="autoHeightForm" method="post" enctype="multipart/form-data">
		 <input type="hidden" name="uuid" name="uuid" value="${employeeedit.uuid}">
		
	
	<div style="overflow: auto;height: 430px;">
		
					 <!--   <textarea rows="2" cols="1"  name="contents" id="contents"  style="width:1000px;height: 430px ">${employeeedit}</textarea>-->  
					 ${employeeedit.content} 
					
	</div>
	</form>
</jodd:form>
</body>


<script>
new Validation("thisForm");


function setBargainType(){


	var uuid = document.getElementById("uuid").value;

    
       document.thisForm.action="${pageContext.request.contextPath}/employeeedit.do?method=update&uuid="+uuid;
       document.thisForm.submit();
}


</script>
</html>
