<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/AS_CSS/jquery.qtip.min.css"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/jquery.qtip.min.js" charset="GBK"></script>

<script type="text/javascript">

  $(document).onReady(function(){
	  
	  $("#j").qtip({
		  content:"你老味"
	  });
	  
  });

</script>

</head>
<body>
  <div>
  <span class="user">xxxx</span>
  </div>
</body>
</html>