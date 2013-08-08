<%@ page language="java" contentType="text/html; charset=utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<link rel="shortcut icon" href="${pageContext.request.contextPath}/images/donggua.ico" 
mce_href="${pageContext.request.contextPath}/images/donggua.ico" type="image/x-icon">

<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>

<title>入职申请</title>

</head>
<body>

<form name="thisForm" action="cadet.do?method=employeeJspSkip&opt=1" method="post" onsubmit=" return check()">
<input type="hidden" name="opt" id="opt" value="${opt}" />
<table  class="formTable" >

<thead>
  <tr>
    <th colspan="2">个人资料</th>
  </tr>
  </thead>
  <tbody>
  <tr>
    <th align="right">姓名：</th>
    <td><input type="text" name="cardName" id="cardName"></td>
  </tr>
  <tr>  
    <th align="right">身份证号：</th>
    <td><input type="text" style="width: 90%" name="cardNum" id="cardNum"></td>
  </tr>
  <tr>
    <th></th>
    <td>
    <button type="submit">确认</button>
     <button type="button" onclick="closeMethod()">退出</button>
    <input type="hidden"  name="hiddentype" id="hiddentype" value="hiddentype"></td>
    </td>
  </tr>

  </tbody>
</table>

</form>



</body>
<script type="text/javascript">

Ext.onReady(function(){
	attachInit("report");
});

function closeMethod(){
	var b=confirm("确定退出此次申请机会？");
	if(b){
		window.close();
	}
}


function check(){

	var id = document.getElementById("cardNum").value;
	var name = document.getElementById("cardName").value;
	
	if(name == "" || name == null){
		alert("姓名不能为空");
		return false;
	}
	
	 var b = validateIdCard(id);
	 if(b == 0){
		   var url="cadet.do";

	      
			var url2="${pageContext.request.contextPath}/cadet.do?method=beforeCadet";
			var params="&cardNum="+id+"&cardName="+name;
			var result=ajaxLoadPageSynch(url2,params);
			if(result=="true"){
				//window.open('cadet.do?method=getOpt&opt=1&cardNum='+id);
				document.thisForm.submit();
				//window.location.href='cadet.do?method=employeeJspSkip&opt=1&cardNum='+id;
			}
			else{
			alert(result);
			}
		 return false;
	 }else{
		 alert("您输入的身份证号有误，请重新输入");
		 return false;
	 }
}



</script>
</html>