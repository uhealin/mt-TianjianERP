<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/images/donggua.ico" 
mce_href="${pageContext.request.contextPath}/images/donggua.ico" type="image/x-icon">

<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>


<title>转正申请</title>
</head>
<body>
<body>
<div style="height: 10px;"></div>
<form name="thisForm" action="${pageContext.request.contextPath}/official.do?method=edit" method="post" onsubmit="return check()">

<table  class="formTable" >

<thead>
  <tr>
    <th colspan="2">员工转正</th>
  </tr>
  </thead>
  <tbody>
  <tr>
    <th align="right">姓名：</th>
    <td><input type="text" name="name" id="name"></td>
  </tr>
  <tr>  
    <th align="right">身份证号：</th>
    <td><input type="text"  name="cardNum" id="cardNum"></td>
  </tr>
  <tr>
    <th></th>
    <td>
    <button type="submit">确认</button>
     <button type="button" onclick="closeMethod()">退出</button>
    </td>
  </tr>

  </tbody>
</table>

</form>
</body>
<script type="text/javascript">



//验证
function check(){
	
	var name =$("#name").val();
	var cardNum=$("#cardNum").val();
	
	if(name==""||name==null){
		alert("姓名不能为空");
		return false;
	}
	
	var checkCard= validateIdCard(cardNum);
	
	if(checkCard==0){
		var url="${pageContext.request.contextPath}/official.do?method=checkApply";
		var request="&cardNum="+cardNum+"&name="+name;
		var result=ajaxLoadPageSynch(url,request);
		
		if(result=="yes"){
			document.thisForm.submit();
			return true;
		}else{
			alert(result);
			return false;
		}
		
	}else{
		alert("您输入的身份证号有误，请重新输入!")
		return false;
	}
	
}


//关闭
function closeMethod(){
	
	
	var b=confirm("确定退出此次申请机会？");
	if(b){
		window.close();
	}
}


</script>

</html>