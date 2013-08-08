<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>实习生登记表</title>



<link href="/AS_CSS/style.css" rel="stylesheet" type="text/css"  /> 


<script type="text/javascript">

Ext.onReady(function(){
	attachInit("report");
});

</script>

</head>
<body>







<form action="cadet.do?method=updateCadetCheck" method="post" onsubmit="return validate()">


<input name="uuid" value="${checkVO.uuid }" type="hidden" />

<table class="formTable" style="table-layout: auto;" >


  <tr>
     <th style="width: 200px">实习报告：</th>
     <td><input type="hidden" id="report" name="report" value="${checkVO.report }" /></td>
   </tr>
   <tr>
     <th>参与项目情况：</th>
     <td><textarea id="project_case" name="project_case" cols="30" rows="5">${checkVO.project_case}</textarea></td>
   </tr>
    <tr style="height: 50px">
       <th></th>
       <td colspan="6">
		&nbsp;
       <button type="submit">保存</button>
       <button type="button" onclick="window.parent.close()">退出</button>

       </td>
    </tr>	

</table>
</form>

	
</body>
</html>