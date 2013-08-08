<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>阅读邮件</title>

<style type="text/css">

.before{
	border: 0px;
	background-color: #FFFFFF !important;
}

.data_tb {
	background-color: #ffffff;
	text-align:center;
	margin:0 0px;
	width:60%;
	border:#8db2e3 1px solid; 
	BORDER-COLLAPSE: collapse; 
	margin-top: 20px;
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
	TEXT-ALIGN: center; 
	WORD-WRAP: break-word
}

</style>
<script type="text/javascript">
	Ext.onReady(function() {
		
		new ExtButtonPanel({
			desc:'',
			renderTo:'sbtBtn',
			items:[ {
	            text: '返回',
	            id:'appSubmit25', 
	            icon:'${pageContext.request.contextPath}/img/back_32.png' ,
	            scale: 'large',
	               handler:function(){
	            	  //closeTab(parent.tab);
						window.history.back();
	   			   }
	           }
	        ]  
		});    
	});
</script>
</head>
<body style="overflow-x: hidden; ">
	
	<input type="hidden" name="uuid" id="uuid" value="${email.uuid}">
	<table style="background-color: #cfe7f7; width: 900;border: 1px;margin-left: 10px;"  class="data_tb"   align="center">
		<tr>
			<td height="30" class="data_tb_alignright" >状态</td>
			<td class="data_tb_alignright" style="display: none;">最后阅读时间</td>
			<td class="data_tb_alignright" style="display: none;">阅读次数</td>
			<td class="data_tb_alignright" width="50%">收件人</td>
		</tr>
		<c:forEach items="${listEmailUser }" var="EmailUser">
		<tr>
			<td height="30" class="data_tb_content" >
					<img alt="${EmailUser.dustbin}" src="${pageContext.request.contextPath}/images/email/email_${EmailUser.isRead }.gif"> </td>
			<td class="data_tb_content" style="display: none;">${EmailUser.readDate ==null?'未阅读或旧数据影响':EmailUser.readDate }</td>
			<td class="data_tb_content" style="display: none;">${EmailUser.readTime}</td>
			<td class="data_tb_content" >${EmailUser.userId }</td>
		</tr>
		</c:forEach>
	</table>
	<span id="sbtBtn" style="text-align: center;width: 100%"> </span>
	<br><br><br>
</body>
<script type="text/javascript">
</script>
</html>