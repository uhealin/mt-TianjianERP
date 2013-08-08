<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title></title>
<script type="text/javascript">
function switchBar() {
	if (parent.rightKingFrame.cols=="250,10,*"){
		Off();
	} else {
		On();
  	}
}

function Off(){
	parent.rightKingFrame.cols="1,10,*";
	thisBar.style.backgroundImage = "url('${pageContext.request.contextPath}/images/button_2right.gif')";
}

function On(){
	parent.rightKingFrame.cols="250,10,*";
	thisBar.style.backgroundImage = "url('${pageContext.request.contextPath}/images/button_2left.gif')";
}
</script>
</head>
<body style="margin: 0px;" background="${pageContext.request.contextPath}/images/menu_bg.gif">
<table height="100%" width="100%" border="0" cellspacing="0" cellpadding="0" align="left" valign="top" onClick="switchBar();" >
<tr><td align="center" valign="center" background="${pageContext.request.contextPath}/images/menu_bg.gif" style="cursor:hand">
	<div style="position: absolute;top: 50%;left: 0;">
	<div id="thisBar" style=" cursor:hand; height: 72px; width: 8px; background-image:url('${pageContext.request.contextPath}/images/button_2left.gif');"></div>
	</div>
</td></tr>
</table>
</body>
</html>