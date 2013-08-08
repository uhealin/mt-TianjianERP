<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>实习生登记表</title>



<link href="/AS_CSS/style.css" rel="stylesheet" type="text/css"  /> 




</head>
<body>







<form action="cadet.do?method=correct" method="post" onsubmit="return validate()">

<input name="name_cn" value="${vo.name_cn }" type="hidden"/>
<input name="idcard" value="${vo.idcard }" type="hidden"/>
<input name="uuid" value="${vo.uuid }" type="hidden" />

<table class="formTable" style="table-layout: auto;" >


    <tr>
      <th rowspan="3">通讯方式<br/>(请详细填写)</th>
      <th>学校通讯地址</th>
      <td colspan="5"><input name="address" value="${vo.address }" size="70"/></td>
    </tr>
    
    <tr>
      <th>E-mail</th>
      <td colspan="3"><input name="email" value="${vo.email }" size="50"/></td>
       <th>手机</th>
      <td><input name="mobile" value="${vo.mobile }" /></td>
    </tr>
    
     <tr>
      <th>紧急联系人</th>
      <td ><input name="urgent_conn_name" value="${vo.urgent_conn_name }" /></td>
      <th>与本人关系</th>
      <td><input name="urgent_conn_relation" value="${vo.urgent_conn_relation }" /></td>
      <th>手机</th>
      <td><input name="urgent_conn_phone" value="${vo.urgent_conn_phone }" /></td>
    </tr>
    <tr style="height: 50px">
       <th></th>
       <td colspan="6">
		&nbsp;
       <button type="submit">保存</button>

       </td>
    </tr>	

</table>
</form>

	
</body>
</html>