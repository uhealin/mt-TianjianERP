<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>发送消息</title>
</head>

<body style="margin: 0px; padding: 0px;">
	<form name="msgForm" action="${pageContext.request.contextPath}/onlineUser.do?method=sendMessage"  onsubmit="return goCheck();" method="post" target="_self">
	  <table class="adviceTable" width="100%">
	  
	  	<tr><th colspan="2" height="30" valign="middle">填写消息</th></tr>
	  	<tr><td height="1" bgcolor="#6595d6" colspan="2"></td></tr>
		<tr height=18>
			<td align="left" width="70">
				消息标题:<font color="red" size="2">*</font>
			</td>
			<td>
				<input type="text" name="txtTitle" id="txtTitle">
			</td>
		</tr>
		
		<tr height=18>
			<td align="left" valign="top">
				消息内容:<font color="red" size="2">*</font>
			</td>
			<td>
				<textarea name="txtMsg" id="txtMsg" cols="45" rows="6" title="请填写消息,不能为空！"></textarea>	
			</td>
		</tr>
		
		<tr>
			<td align="center" colspan="2">
				<input type="hidden" name="userId" id="userId" value='${param["userId"]}'>
				
				<input type="submit" value="确  定" class="flyBT">&nbsp;&nbsp;&nbsp;&nbsp;
				<input type="reset" value="清  空" class="flyBT">&nbsp;&nbsp;&nbsp;&nbsp;
				<input type="button" value="取  消" onclick="self.close();" class="flyBT">&nbsp;&nbsp;&nbsp;&nbsp;
			</td>
		</tr>
	</table>		
	</form>
<script type="text/javascript">

function goCheck() {
	if(document.getElementById("txtTitle").value == '') {
		alert('消息标题不能为空');
		return false;
	} else if (document.getElementById("txtMsg").value == '') {
		alert("消息内容不能为空,请正确填写!");
		return false;
	} else {
		return true;
	}
}
</script>
</body>
</html>