<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript">
//EXT初始化
function ext_init(){
	new Ext.Toolbar({
		renderTo:'tbar',
		items:[
			{ 
				text:'保存',
				icon:'${pageContext.request.contextPath}/img/save.gif' ,
				handler:function(){
					save();
				}
			},'-',{
				text:'返回',
				icon:'${pageContext.request.contextPath}/img/back.gif' ,
				handler:function(){
					var parentId=document.getElementById("parentId").value;
		            window.location = "${pageContext.request.contextPath}/formDefine.do?method=formTypeList&parentId="+parentId;
				}
			}
		]
	});
}

window.attachEvent('onload',ext_init);

</script>

</head>
<body>
<div id="tbar"></div>
<div class="autoHeightDiv" >
	<form name="thisForm" name="thisForm" method="post" action="${pageContext.request.contextPath}/formDefine.do?method=formTypeSave">
		
		<input type="hidden" name="formTypeId" id="formTypeId" value="${formType.formTypeId}">
		<input type="hidden" name="parentId" id="parentId" value="${formType.parentId}">
				
		<table class="editTable" cellspacing="0" cellpadding="0">
			<tr>
				<td class="editTitle" colspan="2">分类管理</td>
			</tr>
			<tr>
				<th width="150">分类名称：<span class="mustSpan">[*]</span></th>
				<td>
				    <input type="text" name="formTypeName" id="formTypeName" class="required" value="${formType.formTypeName}" />
				</td>
			</tr>
			
			<tr>
				<th width="150">说明：</th>
				<td>
				    <textarea name="property" id="property" rows="5" cols="60">${formType.property}</textarea>
				</td>
			</tr>
		</table>
	</form>
</div>	
</body>

<script type="text/javascript">

//保存
function save() {
	if (!formSubmitCheck('thisForm')) {
		return;
	}

	document.thisForm.submit();
}
</script>
</html>