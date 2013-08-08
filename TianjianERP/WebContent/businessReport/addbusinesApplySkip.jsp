<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>业务报告</title>
<script type="text/javascript">
	function openurl(uuid){
		alert("test");
		var uuids=uuid;
		alert(uuids);
		window.location.href="${pageContext.request.contextPath}/businessreport.do?method=addbusinesApplySkip&uuid="+uuids;
	}
</script>
</head>
<body>
<table class="formTable" style="width: 80%">
	<thead>
		<tr>
			<th colspan="5">
				指控维护组</th>
		</tr>
	</thead>
	<tbody>
		<tr>
					<th style="text-align: center"> 组名</th>
					<th style="text-align: center"> 组长</th>			
					<th style="text-align: center"> 组员</th>			
					<th style="text-align: center"> 部门</th>		
					<th style="text-align: center"></th>
		</tr>
	 <c:forEach items="${lgroup}" var="list" varStatus="var" >
		<tr>
			<td><input id="group_name${var.index }"  name ="group_name"  type="text" value="${list.group_name }"/></td>
			<td><input id="group_headman${var.index  }"   name ="group_headman"  onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"  autoid=30039  type="text" value="${list.group_headman }"/></td>
			<td><input id="group_member${var.index  }"  multiselect="true" name ="group_member"  onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"  autoid=7000  type="text" value="${list.group_member }" /></td>
			<td><input id="group_departmentid${var.index  }"  multiselect="true"  name ="group_departmentid"  onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"  autoid=5006 type="text" value="${list.group_departmentid }" /></td>
			<td><a id="group_apply${var.index }" href="#" onclick="window.location.href='${pageContext.request.contextPath}/businessreport.do?method=addbusinesApplySkip&uuid=${list.uuid }'">业务报告申请表</a></td>
		</tr>
		 </c:forEach>
	</tbody>
</table>

</body>
</html>