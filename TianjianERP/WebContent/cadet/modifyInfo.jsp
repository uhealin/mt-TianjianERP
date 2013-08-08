<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

</head>
<body>

<form id="formCadet" action="${pageContext.request.contextPath}/cadet.do?method=modify" method="post" onsubmit=" return check()">
<input type="hidden" name="opt" id="opt" value="${opt}" />
<input type="hidden" name="vtype" value="${param.vtype }" >
<table  class="formTable">


  <tbody>
  <tr>
    <th style="text-align: right">姓　　名</th>
    <td><input type="text" style="width: 40%" name="name_cn" id="name_cn"></td>
  </tr>
  <tr>  
    <th style="text-align: right">身份证号</th>
    <td><input type="text" style="width: 40%" name="idcard" id="idcard"></td>
  </tr>

  
  <tr>
    <th></th>
    <td height="50">
    <button type="button" onclick="$('#formCadet').submit();">修改联系信息</button>&nbsp;
    <button type="button" onclick="$('#opt').val('2');$('#formCadet').submit();">修改考核申请信息</button>&nbsp;
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