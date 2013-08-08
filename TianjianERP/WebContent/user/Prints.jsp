<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>批量打印</title>
</head>
<body leftmargin="0" topmargin="0">
<form name="thisForm" method="post" action="">
<script>

//获取文件的扩展名
function getfilenameext(filename){
  return filename.replace(/(.*\.)/g,'').toUpperCase();
}
//获取主机地址
function getlocationhost(){
  return "http:\/\/"+window.location.host;
}

//处理选择事件
function onPubSelect(){
	var selectBox = document.getElementsByTagName("INPUT");
	var vStr = "";
//	alert(selectBox.length);
	for(var i=0;i<selectBox.length;i++){
		if(selectBox[i].type=='checkbox' && selectBox[i].checked ==true && selectBox[i].name != 'box' && selectBox[i].parentElement.parentElement.rowIndex!=0){
			vStr += selectBox[i].value+",";
		}
	}
	if(vStr!=""){
			
			window.open("/AuditSystem/user.do?method=test&id="+vStr);
			//thisForm.vStr.value=vStr;
			//thisForm.action="user.do?method=test";
			//thisForm.submit();
	
	}else{
				alert("请选择要打印的人员资料");
			return
	}

}
function setChooseValue_CH(){
  var str = "|";
  for(var i=1;i<rowsLength-1;i++){
    if (document.thisForm.choose[i-1].checked == true)
    str += document.thisForm.choose[i-1].value +"|";
  }
  document.thisForm.strM.value=str;
}


function onCleargo(){
  document.thisForm.action="CopyPub.jsp";
  document.thisForm.submit();
  return true;
}
function onSelectgo(){
  document.thisForm.action="CopyPub.jsp?flag=1";
  document.thisForm.submit();
  return true;

}
</script>
${menuLocation }
<table width="30%" cellspacing="0" cellpadding="0"><tr><td height="5"></td></tr></table>

<input name="init" type="button" class="flyBT" value="返回上一页" onclick="goClose();">&nbsp;
<input name="pubselect" type="button" class="flyBT" value="打印选中" onclick="onPubSelect();">&nbsp;
<table width="30%" cellspacing="0" cellpadding="0"><tr><td height="5"></td></tr></table>

<mt:DataGridPrintByBean name="xlsPrint"  message="请选择单位编号" />

<br><br>
<input type="hidden" id=strM name=strM value="" />
</form>
<script language="javascript" >
	var AuditReport =  new ActiveXObject("AuditReportPoject.AuditReport");
</script>
<object classid="clsid:C20572B8-6104-45B8-A3EE-303B42C26ABF" id="oframe" width="100%" height="100%">
  <param name="BorderStyle" value="1">
    <param name="SideBarVisible" value="0">
      <param name="Titlebar" value="0">
        <param name="Menubar" value="1">
</object>

</body>
</html>
<script>

if (top.location == self.location) {
		document.all.init.value = "关  闭";
	}
	
	function goClose() {
		if (top.location == self.location) {
			window.close();
		} else {
			window.history.back();
		}
	}
</script>
