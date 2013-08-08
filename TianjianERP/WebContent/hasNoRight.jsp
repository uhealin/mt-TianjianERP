<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page isErrorPage="true" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>
error page
</title>
<LINK href="<%=request.getContextPath()%>/AS_CSS/css.css" type="text/css" rel="stylesheet">
</head>

<asm:notOnline>
  <c:redirect url="/login.do"></c:redirect>
</asm:notOnline>
<%
	String errorId = request.getParameter("errorId");
	String errMsg="";
	int err = 0;
	if(errorId != null && !errorId.equals("")) {
		err = Integer.parseInt(errorId);
		switch(err) {
			case 1:
				errMsg = "对不起,你没有权限!";
				break;
			default:
				break;
		}
	} else {
		errMsg="系统错误!!";
	}
%>
<body>

<table cellpadding="0" cellspacing="0">
  <tr>
    <td height="50" width="10"></td>
  </tr>
</table>
<table width="600" cellpadding="0" cellspacing="0">
  <tr>
    <td width="97" rowspan="3"> </td>
    <td height="25" align="center"><table width="440" height="273" border="0" cellpadding="0" cellspacing="0" background="<%=request.getContextPath()%>/images/wrongtip.jpg">
        <tr>
          <td height="70" colspan="3"></td>
        </tr>
        <tr>
          <td height="30"></td>
          <td height="30" align="center" valign="bottom"><font color="red" size="3" face="华文彩云">错误提示</font></td>
          <td height="30"></td>
        </tr>
        <tr>
          <td width="50" height="140"></td>
          <td width="340" align="center" valign="middle">
          	<%=errMsg%>
          </td>
          <td width="50"></td>
        </tr>
        <tr>
          <td colspan="3"></td>
        </tr>
      </table></td>
  </tr>
  <tr>
    <td width="515" height="25" align="center"></td>
  </tr>
  <tr>

 <td align="center">
 <% if(err==0 && exception != null) { %>
  <input type="button" value="查看详细错误" onclick="showErr();"/>
<% } %>
    <input name="back" type="button" id="back" value="返回上一页" class="flyBT" onclick="window.history.back();"></td>
  </tr>

</table>
<% if(err==0 && exception != null) { %>
	<br /><br />
	<table width="95%" cellpadding="0" cellspacing="0">
		<tr><td align="center"></td></tr>
		<tr><td align="center">
		<div id ="errMessage" style=" display: none; text-align: left; padding: 10px;">
			<textarea id="tempText" name="tempText" cols="120" rows="30">
					<% exception.printStackTrace(new java.io.PrintWriter(out));%>
			</textarea>
		</div>
		</td></tr>
	</table>
<% } %>
<p></p>

</body>
</html>

<script>
	function showErr() {
		if(errMessage.style.display == 'none') {
			errMessage.style.display = '';
		} else {
			errMessage.style.display = 'none';
		}
	}
</script>
