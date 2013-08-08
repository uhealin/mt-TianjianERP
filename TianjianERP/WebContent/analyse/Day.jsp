<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>区间设置</title>
</head>
<body style="padding: 5px;margin: 3px; ">
<form name="tForm" method="post" action="">
<br>
<div align="center"><font size=3 color="red"><b>区间设置</b></font></div>
<br>
<div style="height:expression(document.body.clientHeight-130);" >
<mt:DataGridPrintByBean name="day" />
</div>
<div align="center">
<input type="button" name="next" id="opSave" value="设　置" class="flyBT" onclick="goAdEd();">
&nbsp;
<input type="button" name="back" value="返  回" class="flyBT"  onClick="window.close();">
</div>


</form>
</body>
</html>
<script>
function goAdEd(){
	var ii=0;
	var result = "";
	document.getElementById("chooseValue_day").value = getChooseValue("day");
	
	var chooseValue_day = document.getElementById("chooseValue_day").value;
//	alert(chooseValue_day);
	selectBox = chooseValue_day.split(",");
	for(var i=0;i<selectBox.length;i++){
		result +=selectBox[i]+"|";	
	}
	
//	alert(result);
	if(result!="|"){
		var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		oBao.open("GET","analyse.do?method=day&sDate="+result + "&AccPackageID=${AccPackageID }",false);
		oBao.send();
		var strResult = unescape(oBao.responseText);
		alert(strResult);
		
		window.close(); 
		var dialog = window.dialogArguments;
		
		dialog.document.getElementById("sDate").value = result;
		
	}else{
		alert("请设置账龄区间!")
	}
}
</script>