<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>批量删除人员</title>
</head>
<body leftmargin="0" topmargin="0">

<form name="thisForm" method="post" action=""id="thisForm" >

${menuLocation }

<table width="30%" cellspacing="0" cellpadding="0"><tr><td height="5"></td></tr></table>


<input name="pubselect" type="button" class="flyBT" value="禁用选中" onclick="goDelete();">&nbsp;
<input name="init" type="button" class="flyBT" value="返回上一页" onclick="goClose();">&nbsp;
<table width="30%" cellspacing="0" cellpadding="0"><tr><td height="5"></td></tr></table>

<mt:DataGridPrintByBean name="serMutiDelete" message="请选择单位编号" />

<br><br>
</form>

</body>
</html>
<script>


	
	function goClose() {
		if (top.location == self.location) {
			window.close();
		} else {
			window.history.back();
		}
	}
	

function goDelete(){

   	
	if(document.thisForm.chooseValue_serMutiDelete.value==""){
		alert("请选择要禁用的非禁用人员！");
		return;
	}else{
	
		var aJax = new ActiveXObject("Microsoft.XMLHTTP");
		var url="user.do?method=CheckName&id="+thisForm.chooseValue_serMutiDelete.value;

	    aJax.open("POST", url, false);
	    aJax.send();

	    if(aJax.responseText == 'yes'){
	    	alert("系统管理员不能被禁用！");
	    	return;
	    }else{
    		var str = "禁用后该人员将无法登录系统，取消禁用可以在还原功能中进行，确定要禁用吗？";
			
			if(confirm(str,"提示")){
				var oBao = new ActiveXObject("Microsoft.XMLHTTP");
				var url="user.do?method=Remove3&id="+thisForm.chooseValue_serMutiDelete.value+"&random="+Math.random();
				oBao.open("POST", url, false);
				oBao.send();
				
				var strResult = unescape(oBao.responseText);
				alert(strResult);
			}
	  	}
	}

	goClean_CH_user();
}

function goClean_CH_user() {
	window.location="user.do?method=mutiDeleteList";
}
</script>
