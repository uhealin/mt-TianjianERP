<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="<%=request.getContextPath()%>/AS_CSS/css_main.css" rel="stylesheet" type="text/css">
<title></title>

</head>

<body leftmargin="0" topmargin="0">
<form name="thisForm" method="post" action="">
  <font color="#FF0000" size="2" ><strong>您现在所在位置: </strong><font color="#0000CC">问题库-&gt;全文搜索</font></font><br>
  <table width="500"  border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td height="1" bgcolor="#0000FF"></td>
  </tr>
  <tr>
      <td height="5"></td>
  </tr>
</table>
  <fieldset>
<legend>Search</legend>
<table width="97%" border="0" align="" cellpadding="0" cellspacing="0">
  <tr height="20">
    <td width="94" height="23" align="right"><strong>搜索条件</strong></td>
    <td width="450" height="23" align="left">&nbsp;
        <input name="searchArea" type="text" size="50" />
        <input name="searchB" type="button" class="flyBT" id="searchB" onclick="closeWindow();" value="搜  索" />
    </td>
    <td width="419" align="left" id="Policy_title">包括
      <input name="title" type="checkbox" value="checkbox" checked="checked" />
      标题&nbsp;&nbsp;
      <input type="checkbox" name="context" value="checkbox" />
      内容&nbsp;&nbsp;
      <input type="checkbox" name="wordNum" value="checkbox" />
      发放文号&nbsp;&nbsp;
      <input type="checkbox" name="sendDept" value="checkbox" />
      发放单位</td>
  </tr>
</table>
</fieldset>
<br>
</form>
</body>
</html>
<script>
function closeWindow()
{
	this.opener=null;
	window.close();
}
</script>