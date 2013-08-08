
<%@ page language="java" contentType="text/html; charset=utf-8"pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>表单管理 </title>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/getPy.js" charset="GBK"></script>
<script type="text/javascript">

var formTablePre = "${formTablePre}";
function ext_init(){
    new Ext.Toolbar({
   		renderTo: "divBtn",
           items:[{
            text:'保存',
            icon:'${pageContext.request.contextPath}/img/save.gif',
            handler:function(){
            	if (!formSubmitCheck('thisForm')) {
            		return;
            	}
            	
            	var tableType = document.getElementById("tableType").value;
            	var tableNameObj = document.getElementById("tableName");  
            	
            	if(tableType==1){
           			//如果是手工建表
           			if(tableNameObj.value == ""){
           				alert("请输入表名！");
           				tableNameObj.focus();
           				return;
           			} 
            	}
            	
            	document.all.thisForm.submit();
			}  
      		},'-',{
            text:'返回',
            icon:'${pageContext.request.contextPath}/img/back.gif',
            handler:function(){
				window.history.back();
			}
      		},'->'
		]
	});    
}

window.attachEvent('onload',ext_init);
</script>
</head>
<body>
<div id="divBtn"></div>
<div class="autoHeightDiv" style="overflow: auto;">
	<form name="thisForm" id="thisForm" method="post" action="${pageContext.request.contextPath}/formDefine.do?method=formSave">
		<input type="hidden" name="uuid"  id="uuid" value="${formDefine.uuid}" />

		<table border="0" cellspacing="0" class="editTable" >
			<tr>
				<td class="editTitle" colspan="2">表单管理</td>
			</tr>
			
			<tr>
				<th nowrap="nowrap">建表方式：<span class="mustSpan">[*]</span></th>
				<td>
					<select name="tableType" id="tableType" onchange="setTableType();">
						<option value="0" <c:if test="${formDefine.tableType==0}">selected="selected"</c:if> >自动建表</option>
						<option value="1" <c:if test="${formDefine.tableType==1}">selected="selected"</c:if> >手工建表</option>
					</select>
				</td>
			</tr>
			
			<tr>
				<th nowrap="nowrap">中文名：<span class="mustSpan">[*]</span></th>
				<td>
					<input name="name" id="name" type="text" value="${formDefine.name}" class="required" title="请输入中文名,不能为空!" onblur="setAutoEnName();">
				</td>
			</tr>
			
			<tr>
				<th nowrap="nowrap">英文名：<span class="mustSpan">[*]</span></th>
				<td>
					<input type="text" name="enName" id="enName" value="${formDefine.enname}" title="请输入英文名,英文名不能为空"/>
					<input type="hidden" name="oldEnName" id="oldEnName" value="${formDefine.tableName}" >
				</td>
			</tr>
			<tr>
				<th nowrap="nowrap">数据库表名：<span class="mustSpan">[*]</span></th>
				<td colspan="3">
					<input type="text" name="tableName" id="tableName" title="请输入表名,表名不能为空"  value="${formDefine.tableName}"/>
				</td>
			</tr>
			
			<tr>
				<th nowrap="nowrap">所属分类：</th>
				<td>
					<input name="formType" id="formType" value="${formDefine.formType}" autoid="10000" multilevel=true>
				</td>
			</tr>
			
			<tr>
				<th nowrap="nowrap">表单定义HTML：</th>
				<td>
					<textarea name="defineStr" cols="120"  rows="10" id="defineStr">${formDefine.definestr}</textarea>
				</td>
			</tr>
			<tr>
				<th nowrap="nowrap">扩展类：</th>
				<td>
					
					<select name="extClass" id="extClass">
					<option id="noJava" value="" selected="selected">无JAVA处理类</option>
							<c:forEach items="${classList}" var="class">
								<option value="${class.name}">${class.name}</option>
							</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<th nowrap="nowrap">选择类型：</th>
				<td>
					<select name="selectType" >
						<option value="单选"" <c:if test="${formDefine.selecttype=='单选' }">selected="selected"</c:if> >单选</option>
						<option value="多选" <c:if test="${formDefine.selecttype=='多选' }">selected="selected"</c:if> >多选</option>
					</select>
				</td>
			</tr>
			<tr>
				<th nowrap="nowrap">说明：</th>
				<td valign="top">
					 <textarea name="property" id="property" cols="80" rows="5" >${formDefine.property}</textarea>
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
	document.getElementById("extClass").value = "${formDefine.extclass}";
}

function setTableType(){
	var tableType = document.getElementById("tableType").value;
	
	if(tableType == 0) {
		setObjDisabled("tableName");   //禁用输入框
		setObjEnabled("enName");
	} else {
		setObjDisabled("enName");
		setObjEnabled("tableName");
	}
}

//自动创建英文名
function setAutoEnName() {
	var tableType = document.getElementById("tableType").value;
	if(tableType == 0) {
		//自动建表
		var name = document.getElementById("name").value;
		var enName = makePy(name);
		
		if(enName.length >0){
			document.getElementById("enName").value = enName[0];
			document.getElementById("tableName").value = formTablePre + enName[0];
		}
	} 
}
setTableType();

function changeCapital(){
	var amount = document.getElementById("金额合计").value ;
	var capitalAmount = renminbiToCapital(amount);
	alert(capitalAmount);
}



</script>
</html>