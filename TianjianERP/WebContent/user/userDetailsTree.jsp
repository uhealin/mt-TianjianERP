<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>考试报名</title>
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

.before{
	border: 0px;
	background-color: #FFFFFF !important;
}

.data_tb {
	background-color: #ffffff;
	text-align:center;
	margin:0 0px;
	width:80%;
	border:#8db2e3 1px solid; 
	BORDER-COLLAPSE: collapse; 
	margin-top: 20px;
	text-align:center;
}
.data_tb_alignright {	
	BACKGROUND: #e4f4fe; 
	white-space:nowrap;
	padding:5px;
	border-top: #8db2e3 1px solid;
	border-left: #8db2e3 1px solid;
	BORDER-RIGHT: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid; 
	height:30px;
	background-color: #d3e1f1;
	text-align:right;
	width:15%;
	font-size: 13px;
	font-family:"宋体";
}
.data_tb_content {
	PADDING-LEFT: 2px; 
	BORDER-TOP: #8db2e3 1px solid; 
	BORDER-LEFT: #8db2e3 1px solid;
	BORDER-RIGHT: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid;  
	WORD-BREAK: break-all; 
	TEXT-ALIGN: left; 
	WORD-WRAP: break-word
}
</style>
</head>
<body>
<div id="divBtn" ></div>
<form id="thisForm" name="thisForm" action="" method="post">
	<table class="data_tb"  align="center">
		<tr>
			<td class="data_tb_alignright">英文名称:</td>
			<td class="data_tb_content">
				<input class="validate-alpha required" id="id" name="id" type="text" value="${userDetailsTree.id }" title="请输入英文名称!" maxLength="20"/>
		   </td>
		</tr>
		<tr>
			<td class="data_tb_alignright">标签名称:</td>
			<td class="data_tb_content">
				<input class="required" id="text" name="text" type="text" value="${userDetailsTree.text }" title="请输入，不得为空" maxLength="15" size="25" />
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright">url:</td>
			<td class="data_tb_content">
				<input class="required" id="url"  name="url" type="text" value="${userDetailsTree.url}" size="100" />
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright">排序:</td>
			<td class="data_tb_content">
				<input class="validate-digits"  id="orderby" name="orderby" type="text" value="${userDetailsTree.orderby }" title="必须填写数字!" size="10" maxLength="3" />
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright">是否隐藏:</td>
			<td class="data_tb_content">
				<select id="isShow" name="isShow">
					<option value="1" <c:if test="${userDetailsTree.isShow =='1'}">selected</c:if> >显示</option>
					<option value="0" <c:if test="${userDetailsTree.isShow =='0'}">selected</c:if> >隐藏</option>
				</select>
			</td>
		</tr>
		 
	</table>
	<input type="hidden" id="autoId" name="autoId" value="${userDetailsTree.autoId }"/>
</form>
</body>
<script type="text/javascript">
new Validation('thisForm'); 
function goAdEd(){
	if (formSubmitCheck('thisForm')){
		document.thisForm.action="${pageContext.request.contextPath}/user.do?method=saveDetailsTree";
		document.thisForm.submit();
	}
}
</script>
</html>