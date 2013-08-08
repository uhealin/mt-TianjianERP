<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

</head>
<body>

<form action="${pageContext.request.contextPath}/cadet.do?method=cadet" method="post" target=_blank onsubmit=" return check()">
<input type="hidden" name="opt" id="opt" value="${opt}" />
<table  class="formTable">

<thead>
  <tr>
    <th colspan="2">实习生资料登记</th>
  </tr>
  </thead>
  <tbody>
  <tr>
    <th>姓名：</th>
    <td><input type="text" style="width: 40%" name="name_cn" id="name_cn"></td>
  </tr>
  <tr>  
    <th>身份证号：</th>
    <td><input type="text" style="width: 40%" name="idcard" id="idcard"></td>
  </tr>
  <tr>
  <th></th><td>
  
  <label><input type="radio" name="recruit"  value="社招" checked>社招</label>&nbsp;
  <label><input type="radio" name="recruit"  value="应届">应届</label>
  </td>
  </tr>
  
  <tr>
    <th></th>
    <td height="50">
    <button type="submit">填报/修改</button>&nbsp;
     <button type="button" onclick="parent.window.close()">退出</button>
    
    </td>
  </tr>

  </tbody>
</table>
</form>



</body>
<script type="text/javascript">



function check(){

	var id = document.getElementById("idcard").value;
	var name = document.getElementById("name_cn").value;
	
	if(name == "" || name == null){
		alert("姓名不能为空");
		return false;
	}
	
	 var b = validateIdCard(id);
	 if(b == 0){
		 return true;
	 }else{
		 alert("您输入的身份证号有误，请重新输入");
		 return false;
	 }
}


</script>
</html>