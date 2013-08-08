<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>kdic管理</title>

<script Language=JavaScript>

	function ext_init(){
	    var tbar = new Ext.Toolbar({
	   		renderTo: "divBtn",
            items:[{
	            text:'保存',
	            icon:'${pageContext.request.contextPath}/img/save.gif',
	            handler:function(){
	            	return setBargainType();
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
<body leftmargin="0" topmargin="0">

<div id="divBtn"></div>


<input name="autoId" type="hidden" id="autoId" value="${autoId}" />
 
	<form name="thisForm" action="${pageContext.request.contextPath}/dic.do" method="post">
		<span class="formTitle" >
		kdic维护<br/> 
		</span>

	<table width="100%" height="150" border="0" cellpadding="0"	cellspacing="0">
	
		<tr>
			<td width="25%">
			<div align="right"><font color="red" size=3>*</font>名 称：</div>
			</td>
			<td width="75%"><input name="name" type="text" value="${dic.name}"
				class='required' id="name" maxlength="20" title="请输入名称" >
			</td>
		</tr>


		<tr>
			<td width="25%">
			<div align="right"><font color="red" size=3>*</font>值：</div>
			</td>
			<td width="75%"><input name="value" type="text" value="${dic.value}"
				class='required' id="value" maxlength="50" title="请输入值">
			</td>
		</tr>
		
		<c:choose>
			<c:when test="${ctype == null }">
				<tr>
					<td width="25%">
					<div align="right"><font color="red" size=3>*</font>类 型：</div>
					</td>
					<td width="75%">
						<input name="ctype" type="text" id="ctype" value="${dic.ctype}"
								class="required" title="请选择类型" onkeydown="onKeyDownEvent();" 
								onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"
								valuemustexist=true autoid=2022>
					</td>
				</tr>
			</c:when>
			<c:otherwise>
				<tr>
					<td width="25%" colspan="2">
						<input name="ctype" type="hidden" id="ctype" value="${ctype }">
					</td>
				</tr>
			</c:otherwise>
		</c:choose>
		<tr>
			<td width="25%">
			<div align="right">属性或排序值：</div>
			</td>
			<td width="75%">
				<input name="property" type="text" id="property"  value="${dic.property}" >
			</td>
		</tr>

	</table>

	</form>
</body>


<script>

function setBargainType(){
	 if (!formSubmitCheck('thisForm'))return ;
	var dicId = document.getElementById("autoId").value;
    if(dicId !=""){
       document.thisForm.action="${pageContext.request.contextPath}/kdic.do?method=update&autoId=" + dicId;
       document.thisForm.submit();
    }else{
		document.thisForm.action="${pageContext.request.contextPath}/kdic.do?method=addDic";
		document.thisForm.submit();
	}
}

</script>
</html>
