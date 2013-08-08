<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/TLD_include.jsp" %>

<%

	
	ASFuntion asf = new ASFuntion();
	String searhArea = asf.showNull(request.getParameter("searchArea"));
	String srAbout = asf.showNull(request.getParameter("srAbout"));
	
	//问题
	//StringBuffer sql =new StringBuffer("select ID,TITLE,CONTEXT from p_Question where");
	
	//法律法规
	StringBuffer sql =new StringBuffer("select ID,TITLE,CONTENT from p_policy where");
	
	String cPage = request.getParameter("cPage");
	String sqlWhere = "init";
	//问题库
	//String ext="questiontype IN (SELECT id from p_questiontype where TypeName LIKE ";
	
	//法律法规
	String ext="POLICYTYPE IN (SELECT id from p_policytype where TypeName LIKE ";
	if((!srAbout.equals("")) && (!searhArea.equals("")))
	{
		sqlWhere = searhArea.trim();
	}

%>
<%@page import="com.matech.framework.pub.util.ASFuntion"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="<%=request.getContextPath()%>/AS_CSS/css_main.css" rel="stylesheet" type="text/css"/>
<title></title>

</head>

<body leftmargin="0" topmargin="0" onload="return isInited();">
<form name="thisForm" method="post" action="Search.jsp" onsubmit="return search();" id="thisForm">
 ${menuLocation }
  <tr>
      <td height="5"></td>
  </tr>

  <fieldset>
<legend>Search</legend>
<table width="97%" border="0" align="" cellpadding="0" cellspacing="0">
  <tr height="20">
    <td width="94" height="23" align="right"><strong>搜索条件</strong></td>
    <td width="399" height="23" align="left">&nbsp;
        <input name="searchArea" type="text" size="40"  maxlength="50" value="<%=searhArea%>"/>
        <input name="searchB" type="submit" class="flyBT" id="searchB" value="搜  索" />
    </td>
    <td width="490" align="left" id="Policy_title" style="display: none;">包括
      <input type="checkbox" name="Title" value="checkbox"  checked="checked"/>
      标题&nbsp;
	  <input type="checkbox" name="Author" value="checkbox"  checked="checked"/>
      作者&nbsp;
	  <input type="checkbox" name="QuestionType" value="checkbox" checked="checked" />
      分类&nbsp;
	  <input type="checkbox" name="KeyValue" value="checkbox" checked="checked" />
      关键字&nbsp;
      <input type="checkbox" name="Context" value="checkbox"  checked="checked"/>
      内容
</td>
  </tr>
</table>
</fieldset>
<br>
<asm:Search sqlWhere="<%=sqlWhere%>" cPage="1" srAbout="<%=srAbout%>" sql="<%=sql%>" ext="<%=ext%>"/>
<input type="hidden" name="srAbout" value="" />
</form>
</body>
</html>
<script>
if ("<%=srAbout%>".indexOf("Title") >= 0) {
	thisForm.Title.checked = true
}
//当前是法律法规，是问题库的时候需要
//if ("<%=srAbout%>".indexOf("Author") >= 0) {
//	thisForm.Author.checked = true
//}
if ("<%=srAbout%>".indexOf("QuestionType") >= 0) {
	thisForm.QuestionType.checked = true
}
if ("<%=srAbout%>".indexOf("KeyValue") >= 0) {
	thisForm.KeyValue.checked = true
}
if ("<%=srAbout%>".indexOf("Context") >= 0) {
	thisForm.Context.checked = true
}
function search()
{
	if(thisForm.searchArea.value=='')
	{
		return false;
	}
	var srAbout = "";
	if(thisForm.Title.checked == true)
	{
		srAbout = srAbout + "Title,";
	}
	//当前是法律法规，是问题库的时候需要
	//if(thisForm.Author.checked == true)
	//{
	//	srAbout = srAbout + "Author,";
	//}
	
	
	if(thisForm.QuestionType.checked == true)
	{
		
		//srAbout = srAbout + "QuestionType,"; //问题库
		srAbout = srAbout + "POLICYTYPE,"; //法律法规
	}
	if(thisForm.KeyValue.checked == true)
	{
		//问题库
		//srAbout = srAbout + "KeyValue,";
	}
	if(thisForm.Context.checked == true)
	{
		srAbout = srAbout + "Content,";
	}
//	alert(srAbout);
	if(srAbout.length<6)
	{
		alert("请至少选择一个条件！");
		return false;
	}
	thisForm.srAbout.value = srAbout;
	//thisForm.action = "Search.jsp";
	//thisForm.submit();
}

 
function isInited() {
	try{
		window.parent.initChecked();
	} catch(e) {
	}
}
</script>
