<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"  errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>个人项目简历</title>
</head>
<body leftmargin="0" topmargin="0">
<form name="thisForm" method="post" action="">
<div style="height:expression(document.body.clientHeight);" >
<mt:DataGridPrintByBean name="item"  message="请选择单位编号" />
</div>	
<input name="ret" type="hidden" class="flyBT" value="返  回" onclick="subClearSearch();">&nbsp;

</form>
</body>
</html>
<script>
/*返回*/
function subClearSearch(){
	window.location="user.do?method=List";
}
</script>