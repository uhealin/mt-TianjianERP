<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ page import="java.text.*,java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
<script Language=JavaScript>


	function ext_init(){
	    var tbar = new Ext.Toolbar({
	   		renderTo: "divBtn",
            items:[{
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
<style type="text/css">
td {
height:20;
}



</style>
<body>
<div id="divBtn"></div>

<%
//out.println(request.getParameter("uuid") + "<br>");
//out.println(((((Integer)session.getAttribute("op"+9+"Count"))*100)/totalCount) + "%");
int totalCount = (Integer)session.getAttribute("totalCount");
List list = (List)session.getAttribute("list");
String msg = (String)session.getAttribute("msg");
String anonymous = (String )session.getAttribute("anonymous");
//out.println(anonymous);
%>


<font color="red"><b>${msg}</b></font>

<table width="100%" border="0" cellpadding="0"	cellspacing="0"   class="formTable">

<tr>
	<th width="8%">标题</th><td colspan="5">${op.title }</td>
</tr>

<%
if("false".equals(anonymous)) {
%>


<%
for(int i=0; i<list.size(); i++) {
%>

	<tr>
		<th width="8%">选项<%=(i+1) %></th>
		<td width="32%"><%=list.get(i) %></td>
		<td width="30%"><%=session.getAttribute("name"+(i+1)) %></td>
		<td width="5%"><%=session.getAttribute("op"+(i+1)+"Count") %>票</td>
		<td width="5%"><%=(((((Integer)session.getAttribute("op"+(i+1)+"Count"))*100)/totalCount) + "%") %></td>
		<td width="20%">
			<table border="0" cellpadding="0" cellspacing=1" bgcolor="#767676">
				<tr>
					<td bgcolor="#FFFFFF" height="16" valign="middle">
						<img src="${pageContext.request.contextPath}/option/images/vote_co.gif" width="<%=(((((Integer)session.getAttribute("op"+(i+1)+"Count"))*100)/totalCount) + "%") %>" height="10">
					</td>
				</tr>
			</table>
		</td>
	</tr>

<%
}
} else {
	for(int i=0; i<list.size(); i++) {
%>


	<tr>
		<th width="8%">选项<%=(i+1) %></th>
		<td width="32%"><%=list.get(i) %></td>
		<td width="5%"><%=session.getAttribute("op"+(i+1)+"Count") %>票</td>
		<td width="5%"><%=(((((Integer)session.getAttribute("op"+(i+1)+"Count"))*100)/totalCount) + "%") %></td>
		<td width="50%">
			<table border="0" cellpadding="0" cellspacing=1" bgcolor="#767676">
				<tr>
					<td bgcolor="#FFFFFF" height="16" valign="middle">
						<img src="${pageContext.request.contextPath}/option/images/vote_co.gif" width="<%=(((((Integer)session.getAttribute("op"+(i+1)+"Count"))*100)/totalCount) + "%") %>" height="10">
					</td>
				</tr>
			</table>
		</td>
	</tr>



<%	
	}
}
%>

</table>

<!-- 
${msg}
 -->


</body>
</html>