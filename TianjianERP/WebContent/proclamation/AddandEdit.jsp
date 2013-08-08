<%@page import="java.util.UUID"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>公告信息</title>
<script type="text/javascript">
	Ext.onReady(function() {
		
		 var IframeID=document.getElementById("oblog_Composition").contentWindow;
		 IframeID.document.body.innerHTML = document.getElementById("content").value;
		
		new Ext.Toolbar({
			renderTo : "divBtn",
			height : 30,
			defaults : {
				autoHeight : true,
				autoWidth : true
			},
			items : [ {
				id : 'saveBtn',
				text : '保存',
				icon : '${pageContext.request.contextPath}/img/save.gif',
				handler : function() {
					mySubmit();
				}
			}, '-', {
				text : '返回',
				icon : '${pageContext.request.contextPath}/img/back.gif',
				handler : function() {
					window.history.back();
				}
			}, '->' ]
		});

	});
</script>
</head>
<body >
<div id="divBtn"></div>
<div style="overflow: auto;height: 95%" >
<form name="thisForm" method="post" action="" id="thisForm" >
<input type="hidden" id="uuid" name="uuid" value="${proclamation.uuid }" />

<fieldset style="width: 100% "><legend>公告信息</legend>
<table width="100%"  border="0" cellpadding="0" style="line-height: 40px;"
	cellspacing="0">
	<tr>
		<td align="right" width="15%"><font color="red" size=3>*</font>标题：</td>
		<td style="paddingpadding-left: 50px;"><input id="title" type="text" class="required" maxlength="18"
			name="title" title="请输入，不能为空！" size="85"  value="${proclamation.title}" /></td>
	</tr>
	<tr>
		<td align="right" width="15%">
				<div><font color="red" size=3>*</font>内容：</div>
		</td>
		<td height="400" width="800px">
			<input type="hidden" id="content" name="content" value="${proclamation.content }">
			<jsp:include page="../AS_INCLUDE/images/edit.html" />
		</td>
	</tr>
	<tr>
			<td align="right"  style="vertical-align: top;margin-top: 30px;">附件：</td>
			<td >
				<input type="hidden" id="fileName" name="fileName" value="${proclamation.fileName}">
				<div style="vertical-align: middle;width: 100%;margin-top: 10px;">
				<script type="text/javascript">
				if("${proclamation.fileName}"==""){
					<%
							String fileName=UUID.randomUUID().toString();  //生成uuid
					%>
					document.getElementById("fileName").value="<%=fileName%>";
					attachInit('proclamation','<%=fileName%>');					
				}else{
					attachInit('proclamation','${proclamation.fileName}');
				}
				</script>
				</div>
			</td>
		</tr>
</table>
</fieldset>
 </form>
</div>
<br>
<br>
<script type="text/javascript">
	new Validation('thisForm');
	function mySubmit() {
		  if (!formSubmitCheck('thisForm'))return ;
		    
	    	var IframeID=document.getElementById("oblog_Composition").contentWindow;
			document.getElementById("content").value = IframeID.document.body.innerHTML;
			var questionDescribe = IframeID.document.body.innerHTML;
		
			if(questionDescribe.indexOf("<P>&nbsp;</P>")>-1){
				questionDescribe = questionDescribe.replace("<P>&nbsp;</P>","");
			}
			
			if(questionDescribe=="" || questionDescribe==null){
				alert("内容不能为空!");
				return;
			}
			//document.getElementById("content").value=questionDescribe;
		if (document.getElementById("uuid").value != "") {

			document.thisForm.action = "${pageContext.request.contextPath}/proclamation.do?method=update";
		} else {

			document.thisForm.action = "${pageContext.request.contextPath}/proclamation.do?method=add";
		}
		document.thisForm.submit();
	}
 
</script>

</body>
</html>
