<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
<script Language=JavaScript>


	function ext_init(){
	    var tbar = new Ext.Toolbar({
	   		renderTo: "divBtn",
            items:[{
	            text:'返回',
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



</style>
<body>
<div id="divBtn"></div>

<%
String msg = (String)session.getAttribute("msg");
out.println("<font color='red'><b>" + msg + "</b></font>");
%>


</body>
</html>