<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ page import="java.util.*"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%

	QuestionTable plt = new QuestionTable();
	
	ASFuntion CHF=new ASFuntion();
	String id = CHF.showNull(request.getParameter("chooseValue"));
	
	String menuDetail = "";
	Connection conn =null;
	try{
	  	 conn = new DBConnect().getConnect();
	  	QuestionMan plm = new QuestionMan(conn);
	  	plm.updateViewCount(id);
		if(id.equals(""))
		{

			response.sendRedirect("List.jsp");
		}
		else
		{
			plt = plm.getAQuestionDetail(id);
		}
%>  

<%@page import="com.matech.framework.pub.util.ASFuntion"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.matech.framework.pub.db.DBConnect"%>
<%@page import="com.matech.framework.pub.db.DbUtil"%>
<%@page import="com.matech.audit.service.cases.model.CasesTable"%>
<%@page import="com.matech.audit.service.cases.CasesMan"%>
<%@page import="com.matech.audit.service.question.model.QuestionTable"%>
<%@page import="com.matech.audit.service.question.QuestionMan"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>查看问题</title>
<style>
td {
	font-size: 12px;
	text-decoration: none;
}
</style>

</head>

<body bgcolor="#9EB6D8" leftmargin="10" rightmargin="10" topmargin="10">
<form name="thisForm" method="post" action="">
<table width="100%" height="121" border="0" cellpadding="0" cellspacing="1" bgcolor="#6595d6">
    <tr>
      <td align="center" valign="top" bordercolor="#CCCCCC" bgcolor="#FFFFFF">

  <table width="98%"  border="0" cellpadding="0" cellspacing="0">
  <tr>
      <td height="5" align="left"></td>
  </tr>
    <tr>
      <td height="18" align="left">
	  <font color="#FF0000" size="2" ><strong>您现在所在位置: </strong><font color="#0000CC">问题库-&gt;查看问题</font></font>
	  </td>
  </tr>
  <tr>
  <td>
  <table width="60%"  border="0" cellpadding="0" cellspacing="0">
   <tr>
      <td height="1" bgcolor="#0000FF"></td>
  </tr>
</table>
</td>
  </tr>
</table>
  <br>
  <table width="98%" border="0" align="center" cellpadding="2" cellspacing="1" bgcolor="#6595d6">
	<tr height="20" class="DGtd">
		<td height="23" align="center" bgColor="#EEEEEE"><strong>文件标题</strong></td>
      <td height="23" align="center" bgColor="#EEEEEE" id="Title" onDblClick="alert(2);"><%=id.equals("")?"":plt.getTitle()%></td>
	</tr>
	<tbody id="SWlist">
	<tr height=18>
		<td width="98" height="20" align="center" bgColor="#EEEEEE"><strong>分类</strong></td>
		<td width="845" align="left" bgColor="#ffffff" id="CasesType"><%=id.equals("")?"":plm.findQuestionTpyeNameById(plt.getQuestionType())%></td>
	  </tr>
	<tr height=18>
	  <td height="20" align="center" bgColor="#EEEEEE"><strong>作者</strong></td>
	  <td align="left" bgColor="#ffffff" id="Policy_code"><%=id.equals("")?"":plt.getAuthor()%></td>
	  </tr>
	<tr height=18>
	  <td height="20" align="center" bgColor="#EEEEEE"><strong>关键字</strong></td>
	  <td align="left" bgColor="#ffffff" id="Policy_company"><%=id.equals("")?"":plt.getKeyValue()%></td>
	  </tr>
	<tr height=18>
	  <td height="22" align="center" bgColor="#EEEEEE"><strong>文件内容</strong></td>
	  <td align="left" bgColor="#ffffff" id="Policy_context"><%=id.equals("")?"":plt.getContext().replaceAll("\r","<br>").replaceAll("\n","<br>")%></td>
	  </tr>
	<tbody>
</table>
<br>
<br>
<table width="90%" align="center">
<tr>
  <td width="" align="center">
        <input type="button" name="next" value="返  回" class="flyBT"  onClick="getback();">
  </td>
    </tr>
</table>
  </td>
    </tr>
  </table>
  <p>&nbsp;</p>
</form>
</body>
</html>
<script>
<%

	}catch(Exception e)
	{
		out.print(e);
	}finally{
		 DbUtil.close(conn);
	}
	%>
function closeWindow()
{
	this.opener=null;
	window.close();
}


function getback(){
   window.location="${pageContext.request.contextPath}/questionsearch/Search.jsp";
}
</script>
