<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ page import="java.util.*" %>

<%
boolean bContinue = (Boolean)session.getAttribute("bContinue");
boolean bijiao = (Boolean)session.getAttribute("bijiao");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>kdic管理</title>

<script Language=JavaScript>

	function ext_init(){
	    var tbar = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		
<%
if(bContinue || bijiao) {
%>
			items:[{
			    text:'查看投票结果',
			    icon:'${pageContext.request.contextPath}/img/query.gif',
			    handler:function(){
					document.resultForm.submit();
				}
				},'-',{
			    text:'返回',
			    icon:'${pageContext.request.contextPath}/img/back.gif',
			    handler:function(){
					window.history.back();
				}
				}
			]	  
<%
} else {
%>



            items:[{
	            text:'提交投票',
	            icon:'${pageContext.request.contextPath}/img/save.gif',
	            handler:function(){
	            	return setBargainType();
				}  
       		},'-',{
	            text:'查看投票结果',
	            icon:'${pageContext.request.contextPath}/img/query.gif',
	            handler:function(){
					document.resultForm.submit();
				}
       		},'-',{
	            text:'返回',
	            icon:'${pageContext.request.contextPath}/img/back.gif',
	            handler:function(){
					window.history.back();
				}
       		},'->'
			]



<%
}
%>



        });
       
	    
<%
if(bContinue) {
%>
tbar.add(
	'<div style="margin-left:200px;color:red;">你已经投过票了！</div>'
);
tbar.doLayout();
<%
}
%>  
<%
if(bijiao) {
%>
tbar.add(
	'<div style="margin-left:200px;color:red;">投票截止时间已过！</div>'
);
tbar.doLayout();
<%
}
%>  	    
	    
	    
    }
    window.attachEvent('onload',ext_init);
</script>

</head>
<body leftmargin="0" topmargin="0">

<%
List list = (List)session.getAttribute("list");
List list1 = (List)session.getAttribute("list1");
%>

<div id="divBtn"></div>

<%--

if(bContinue) {
	out.println("<font color='red'><b>你已经投过票了!!</b></font>");
}
--%>



<form name="resultForm" action="${pageContext.request.contextPath}/option.do?method=result" method="post">
	<input name="uuid" type="hidden" id="uuid" value="${op.uuid}" />
	<input name="anonymous" type="hidden" id="anonymous" value="${anonymous}" />
	<!-- <input type="submit" value="查看投票结果">  -->
</form>

<!-- <input name="uuid" type="hidden" id="uuid" value="${uuid}" />  -->

	<form name="thisForm" action="${pageContext.request.contextPath}/option.do?method=save" method="post">
	
		<input name="uuid" type="hidden" id="uuid" value="${op.uuid}" />
		<input name="onlyonece" type="hidden" id="onlyonece" value="${op.onlyonece}" />
		<input name="inputtype" type="hidden" id="inputtype" value="${inputtype}" />
		<input name="anonymous" type="hidden" id="anonymous" value="${op.anonymous}" />
		<input name="endtime" type="hidden" id="endtime" value="${endtime}" />
		<input name="msg" type="hidden" id="msg" value="${msg}" />

		
		<span class="formTitle" >
		在线调查<br/> 
		</span>

	<table width="100%" height="150" border="0" cellpadding="0"	cellspacing="0"   class="formTable">
		<tr>
			<th width="25%">投票标题</th><td width="75%">${op.title}</td>
		</tr>

<%
for(int i=1; i<=10; i++) {
%>
		<tr>
			<th width="25%"  <%=list1.get(i-1) %>>选项<%=i %>
			</th>
			<td width="75%"  <%=list1.get(i-1) %>><input type="${inputtype}" name="opt" value="<%=i %>" <%if(bContinue||bijiao) out.println("disabled"); %>><%=list.get(i-1) %>
			</td>
		</tr>
<%
}
%>
	</table><br>
	
<table width="100%" border="0" cellpadding="0"	cellspacing="0"   class="formTable">
	<tr height="20">
		<th width="25%">属性</th>
		<td width="18%">是否匿名：${anonymous }</td>
		<td width="19%">投票类型：${op.opttype }</td>
		<td width="19%">是否只投一次：${onlyonece }</td>
		<td width="19%">截止日期：${op.endtime }</td>
	</tr>
</table>


	</form>
	
	
	
</body>


<script>

function setBargainType(){
	document.thisForm.submit();
	/*
	if (!formSubmitCheck('thisForm'))return ;
	var uuid = document.getElementById("uuid").value;
	alert(uuid);
    if(uuid !=""){
       document.thisForm.action="${pageContext.request.contextPath}/option.do?method=update&uuid=" + uuid;
       document.thisForm.submit();
    }else{
		document.thisForm.action="${pageContext.request.contextPath}/option.do?method=addDic";
		document.thisForm.submit();
	}
	*/
}

</script>
</html>
