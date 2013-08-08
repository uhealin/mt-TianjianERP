<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>表单按钮维护</title>
<script type="text/javascript">
function ext_init(){
	new Ext.Toolbar({
		renderTo: "divBtn",
		height:30,
		defaults: {autoHeight: true,autoWidth:true},
       items:[{ 
           text:'保存',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/save.gif' ,
           handler:function(){
		   		save();
		   }
     	 },'-',{ 
           text:'返回',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/back.gif' ,
           handler:function(){
           		var formId = document.getElementById("formId").value;
		   		window.location="${pageContext.request.contextPath}/formQueryConfig.do?method=updateButtonsList&formId="+formId;
		   }
     	 },'->']
});
}
window.attachEvent('onload',ext_init);

</script>
</head>
<body>
	<div id="divBtn"></div>
	<div class="autoHeightDiv" style="overflow: auto;">
		<form name="thisForm" action="${pageContext.request.contextPath}/formQueryConfig.do?method=saveButtons" method="post">
			
			<input type="hidden" name="uuid" value="${formButton.uuid }">
			<input type="hidden" name="formId" value="${formButton.formid }">  
			
			<table border="0" cellspacing="0" class="editTable" >
				<tr>
					<td class="editTitle" colspan="2">表单按钮管理</td>
				</tr>
				<tr>
					<th>按钮名：</th>
					<td>
						<input type="text" name="name" value="${formButton.name }" class="required" >
					</td>
				</tr>
				<tr>
					<th>英文名：</th>
					<td>
						<input type="text" name="enname" value="${formButton.enname }" > （用于作为参数hiddenBtn使用）
					</td>
				</tr>
				<tr>
					<th>图标：</th>
					<td>
						
						<input type="text" name="icon" value="${formButton.icon }">
					</td>
				</tr>
				
				<tr>
					<th nowrap="nowrap">是否追加|分隔：</th>
					<td>
						<select name="afterGroup" >
							<option value="0" <c:if test="${formButton.aftergroup==0 }">selected="selected"</c:if> >不追加</option>
							<option value="1" <c:if test="${formButton.aftergroup==1 }">selected="selected"</c:if> >追加</option>
						</select>
					</td>
				</tr>
				
				<tr>
					<th>排序：</th>
					<td>
						<input type="text" style="width: 100px" name="orderId" value="${formButton.orderid }">
					</td>
				</tr>
				
				<tr>
					<th nowrap="nowrap">按钮类型：</th>
					<td>
						<select name="buttonType" >
							<option value="0" <c:if test="${formButton.buttonType==0 }">selected="selected"</c:if> >列表按钮</option>
							<option value="1" <c:if test="${formButton.buttonType==1 }">selected="selected"</c:if> >表单按钮</option>
						</select>
					</td>
				</tr>
				
				<tr>
					<th>执行前调用JS函数名：</th>
					<td>
						<input type="text" size="50" name="beforeClick" value="${formButton.beforeClick }">
					</td>
				</tr>
				
				<tr>
					<th>执行前调用的JS：</th>
					<td>
						
						<textarea name="beforeClickJs" rows="10" cols="80" >${formButton.beforeClickJs }</textarea>
					</td>
				</tr>
				
				<tr>
					<th nowrap="nowrap">扩展处理方式：</th>
					<td>
						<select name="handleType" id="handleType" onchange="selectHandleType(this);" >
							<option value="0" <c:if test="${formButton.handleType=='0' }">selected="selected"</c:if> >JS处理</option>
							<option value="1" <c:if test="${formButton.handleType=='1' }">selected="selected"</c:if> >JAVA类或SQL处理</option>
						</select>
					</td>
				</tr>
				
				<tbody id="jsTbody" >
					<tr>
						<th>调用的JS函数：</th>
						<td>
							<input type="text" size="50" name="onClick" value="${formButton.onclick }">
						</td>
					
					</tr>
					
					<tr>
						<th>扩展的JS函数：</th>
						<td>
							<textarea name="extJs" rows="10" cols="80" >${formButton.extjs }</textarea>
						</td>
					</tr>
				</tbody>
				
				<tbody id="javaTbody" style="display: none;">
					<tr>
						<th>JAVA接口：</th>
						<td>
							<select name="className" id="className">
							<option id="noJava" value="" selected="selected">无JAVA处理类</option>
									<c:forEach items="${classList}" var="class">
										<option value="${class.name}">${class.name}</option>
									</c:forEach>
							</select>
						</td>
					</tr>
					
					<tr>
						<th>参数：</th>
						<td>
							<textarea name="sql" id="sql" rows="10" cols="80" >${formButton.sql}</textarea>
						</td>
					</tr>
				</tbody>
				
				
				<tr>
					<th>执行后调用JS函数名：</th>
					<td>
						<input type="text" size="50" name="afterClick" value="${formButton.afterClick }">
					</td>
				</tr>
				
				<tr>
					<th>执行后调用的JS：</th>
					<td>
						
						<textarea name="afterClickJs" rows="10" cols="80" >${formButton.afterClickJs }</textarea>
					</td>
				</tr>
			</table>
		</form>
	</div>
</body>
<script type="text/javascript">
var uuid = document.getElementById("uuid").value;

//当修改时
if (uuid != "") {
	document.getElementById("className").value = "${formButton.className}";
}

function save(){
	document.thisForm.submit();
}
	
function selectHandleType() {
	var selectValue = document.getElementById("handleType").value;
	if(selectValue == "1") {
		document.getElementById("jsTbody").style.display = "none";
		document.getElementById("javaTbody").style.display = "";
	} else {
		document.getElementById("jsTbody").style.display = "";
		document.getElementById("javaTbody").style.display = "none";
	}
}

selectHandleType();
</script>
</html>
