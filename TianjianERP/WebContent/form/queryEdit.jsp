<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>列表维护</title>

<script type="text/javascript">
function ext_init(){
	new Ext.Toolbar({
		renderTo: "divBtn",
		height:27,
       items:[{ 
           text:'保存',
           icon:'${pageContext.request.contextPath}/img/save.gif' ,
           handler:function(){
		   		save();
		   }
     	 },'-',{ 
           text:'返回',
           icon:'${pageContext.request.contextPath}/img/back.gif' ,
           handler:function(){
		   		window.location="${pageContext.request.contextPath}/formDefine.do?method=formList";
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
	<form name="thisForm" method="post">
		<input type="hidden" name="formId" id="formId" value="${formId}" />
		<table border="0" cellspacing="0" class="editTable" >
			<tr>
				<td class="editTitle" >自定义HTML</td>
			</tr>
			<tr>
				<td>
					<textarea name="listHtml" rows="8" cols="100">${listHtml }</textarea>
				</td>
			</tr>
		</table>
		<table border="0" cellspacing="0" class="editTable" >
			<tr>
				<td class="editTitle" >自定义SQL</td>
			</tr>
			<tr>
				<td>
					<textarea name="listSql" rows="8" cols="100">${listSql }</textarea>
				</td>
			</tr>
			
			<tr>
				<td><input type="button" class="flyBT" onclick="executeSql()" value="执行SQL更新字段名">&nbsp;
				<input type="button" class="flyBT" onclick="genTable()" value="生成简易表单">
				</td>
			</tr>
		</table>
		
		<table class="listTable" cellspacing="1" cellpadding="0">
		
		   <tr>
		      <td colspan="9">
		        <textarea name="thead" rows="5" cols="50" style="width:100%">${thead}</textarea>
		      </td>
		   </tr>
		
			<tr>
				<th>数据库字段</th>
				<th>字段名称</th>
				<th>是否在列表显示</th>
				<th>是否在列表行隐藏</th>
				<th>字段排序方式</th>
				<th>字段宽度</th>
				<th>字段顺序</th>
				<th>字段类型</th>
				<th>特殊行标识值</th>
			</tr>
			
			<c:forEach items="${formQueryFeildList}" var="formQueryFeild">
				<tr>
					
					<td>
						<input type="hidden" name="uuid" value="${formQueryFeild.uuid}">
						<input type="hidden" name="enname" value="${formQueryFeild.enname}">
						${formQueryFeild.enname }
					</td>
					<td>
						<input type="text" name="name" value="${formQueryFeild.name }">
					</td>
					<td align="center">
						<select name="bshow" >
							<option value="1" >显示</option>
							<option value="0" <c:if test="${formQueryFeild.bshow == 0}">selected="selected"</c:if> >不显示</option>
						</select>
					</td>
					<td align="center">
						<select name="bhiddenrow" >
							<option value="1" >隐藏</option>
							<option value="0" <c:if test="${formQueryFeild.bhiddenrow == 0}">selected="selected"</c:if>  >不隐藏</option>
						</select>
					</td>
					
					<td align="center">
						<select name="border">
							<option value="0" >不排序</option>
							<option value="1" <c:if test="${formQueryFeild.border == 1}">selected="selected"</c:if> >升序</option>
							<option value="-1" <c:if test="${formQueryFeild.border == -1}">selected="selected"</c:if> >降序</option>
						</select>
					</td>
					
					<td align="center">
						<input type="text" size="5" name="width" value="${formQueryFeild.width}" >
					</td>
					
					<td align="center">
						<input type="text" size="3" name="orderid" value="${formQueryFeild.orderid}" >
					</td>
					<td align="center">
						<select name="btype">
							<option value="showLeft" <c:if test="${formQueryFeild.btype == 'showLeft'}">selected="selected"</c:if> >左对齐</option>
							<option value="showCenter" <c:if test="${formQueryFeild.btype == 'showCenter'}">selected="selected"</c:if> >居中</option>
							<option value="showRight" <c:if test="${formQueryFeild.btype == 'showRight'}">selected="selected"</c:if> >右对齐</option>
							<option value="showMoney" <c:if test="${formQueryFeild.btype == 'showMoney'}">selected="selected"</c:if> >货币</option>
							<option value="showAttach" <c:if test="${formQueryFeild.btype == 'showAttach'}">selected="selected"</c:if> >附件下载</option>
							<option value="showProcess" <c:if test="${formQueryFeild.btype == 'showProcess'}">selected="selected"</c:if> >流程跟踪</option>
						</select>
					</td>
					
					<td align="center">
						<input type="text" size="10" name=rowFlag value="${formQueryFeild.rowFlag}" >
					</td>
					</tr>
			</c:forEach>	
		</table>
	</form>
	<br/><br/>
</div>
</body>
<script type="text/javascript">
//保存表单字段
function save(){
	document.thisForm.action="${pageContext.request.contextPath}/formQueryConfig.do?method=saveField";
	document.thisForm.submit();
}

//执行sql语句，更新页面的表单字段名
function executeSql(){
	document.thisForm.action="${pageContext.request.contextPath}/formQueryConfig.do?method=queryConfigEdit";
	document.thisForm.submit();
}

function genTable(){
	Ext.MessageBox.confirm("操作提示","此操作会覆盖整个表单内容，建议先做好备份，确认继续吗?",function(e){
		if(e){
			Ext.MessageBox.prompt("输入提示","请输入生成表单的行数，必须为大于0的数字",function(e,text){
				if(e=="ok"){
					Ext.MessageBox.confirm("操作提示","是否排除空标题的列?",function(e){
						var noempty=e?"y":"n";
						var formid=$("#formId").val();
						var url="${pageContext.request.contextPath}/formQueryConfig.do?method=doGenForm";
					    $.post(url,{formid:formid,cols:text,noempty:noempty},function(str){alert(str)});
					});
					
				}
			});  //Ext.MessageBox.promp
		}else {
			return false;
		}
		
	}); //Ext.MessageBox.confirm
}
</script>
</html>
