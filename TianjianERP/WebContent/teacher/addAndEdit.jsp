<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ taglib uri="http://ckeditor.com" prefix="ckeditor" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/ckeditor/ckeditor.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/ckfinder/ckfinder.js"></script>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>培训教师</title>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/images/editor.js" charset=GBK></script>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/images/DhtmlEdit.js" charset=GBK></script>
<script type="text/javascript">
Ext.onReady(function(){
		var tbar_customer = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		height:30,
	   		defaults: {autoHeight: true,autoWidth:true},
            items:[{
	            text:'保存',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/save.gif',
	            handler:function(){
	            	goAdEd();
	            }
       		},'-',{ 
			        text:'返回',
			        cls:'x-btn-text-icon',
			        icon:'${pageContext.request.contextPath}/img/back.gif',
			        handler:function(){
						history.back();
					}
		       	}]
		})
})
</script>
<style type="text/css">
table tbody th{
	text-align: left;
	width: 20%;
}

</style>
</head>
<body>
<div id="divBtn" ></div>
<form id="thisForm" name="thisForm" action="" method="post">
<div style="overflow: auto">
	<table  style="width: 60%" class="formTable">
	  <thead>
		<tr>
			<th  colspan="3">教师资料</th>
		</tr>
	 </thead>
		 <tbody>
		<tr>
			<th>姓名<span class="mustSpan">[*]</span></th>
			<td colspan="2"><input class="required" id="name" name="name" type="text" value="${teacher.name }" title="请输入，不得为空"/></td>
		</tr>
		<c:if test="${act=='edit'}">
		<tr>
			<th>教师编号<span class="mustSpan">[*]</span></th>
			<td  colspan="2"><input  id="teacherNum" readonly="readonly" name="teacherNum" type="text" value="${teacher.teacherNum }"/></td>
		</tr>
		</c:if>
		<tr>
			<th>职称</th>
			<td  colspan="2"><input  id="title" name="title" type="text" value="${teacher.title}"/></td>
		</tr>
		<tr>
			<th>职位</th>
			<td  colspan="2"><input  id="position" name="position" type="text" value="${teacher.position }"/></td>
		</tr>
		<tr>
			<th>授课专业</th>
			<td  colspan="2"><input  id="professional" name="professional" type="text" value="${teacher.professional }"/></td>
		</tr>
		<tr>
			<th>性别</th>
			<td  colspan="2">
				<select id="sex" name="sex">
					<option value="男" <c:if test="${teacher.sex=='男' }">selected="selected"</c:if>>男</option>
					<option value="女" <c:if test="${teacher.sex=='女' }">selected="selected"</c:if>>女</option>
				</select>
			</td>
		</tr>
		<tr>
			<th>工作单位</th>
			<td  colspan="2"><input id="company" name="company" type="text" value="${teacher.company}" size="40"/></td>
		</tr>
		<tr>
			<th>讲师归属</th>
			<td  colspan="2"><input  id="state" name="state" type="text" value="${teacher.state}" size="40"/></td>
		</tr>
		<tr>
			<td colspan="3">
			<input type="hidden" value="${act}" id="act" name="act"/>
			<input type="hidden" value="${teacher.id }" id="id" name="id"/>
			</td>
		</tr>
		</tbody>
	</table>
</div>
</form>
</body>
<script type="text/javascript">
new Validation('thisForm');
function goAdEd(){
	if (!formSubmitCheck('thisForm')) return ;
	var act=document.getElementById("act").value;
	document.thisForm.action="teacher.do?method=saveOrUpdate&&act="+act;
	document.thisForm.submit();
}
</script>
</html>