<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ taglib uri="http://ckeditor.com" prefix="ckeditor" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/ckeditor/ckeditor.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/ckfinder/ckfinder.js"></script>
 <script src="<%=request.getContextPath()%>/AS_INCLUDE/images/editor.js" charset=GBK></script>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/images/DhtmlEdit.js" charset=GBK></script>
 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<body>
<table class="formTable" width="80%">

<thead>
	<tr>
	<th colspan="6">培训报名汇总</th>
	</tr>
</thead>
<tbody>
	<tr>
	<th>姓名</th>
	<td><input id="name" name="name" value="${education.name}" type="text"/></td>
	<th>所属部门</th>
	<td><input id="departmentName" name="departmentName" type="text" value="${education.departmentName}"/></td>
	<th>培训班级名称</th>
	<td><input id="className" name="className" type="text" value="${education.className}"/></td>
	</tr>
   <tr>
   <th>课程类型</th>
	<td><input id="courseType" name="courseType" value="${education.courseType}" type="text" /></td>
	<th>报名开始时间</th>
	<td><input id="registerStartTime" name="registerStartTime" type="text" value="${education.registerStartTime}"/></td>
	<th>报名结束时间</th>
	<td><input id="registerEndTime" name="registerEndTime" type="text" value="${education.registerEndTime}"/></td>
   
   </tr>
    <tr>
   <th>培训班类型</th>
   <td><input id="peiType" name="peiType" type="text" value="${education.peiType}"/></td>
   <th>培训对象</th>
   <td><input id="trainObject" name="trainObject" type="text" value="${education.trainObject}"/></td>
   <th>报名人数</th>
   <td><input id="registNum" name="registNum" type="text" value="${education.registNum}"/></td>
   </tr>
   <tr>
   <th>限制报名人数</th>
   <td><input id="limitNum" name="limitNum" type="text" value="${education.limitNum}"></td>
   <th>报名状态</th>
   <td colspan="3"><input id="state" name="state" type="text" value="${education.state}"/></td>
   </tr>
</tbody>
</table>
</body>
</html>