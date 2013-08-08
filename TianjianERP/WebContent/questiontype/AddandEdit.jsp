<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ page import="java.util.*"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/Validate_include.jsp" %>

<%

	QuestionTypeTable pltt = new QuestionTypeTable();
	
	ASFuntion CHF=new ASFuntion();

	String id = CHF.showNull(request.getParameter("chooseValue"));
	String tid = CHF.showNull(request.getParameter("tid"));
	Connection conn =null;
	try{
	  	 conn = new DBConnect().getConnect();
	  	QuestionTypeMan pltm = new QuestionTypeMan(conn);
		if(id.equals(""))//增加页面
		{
			if(request.getParameter("typename")!=null)
			{
				pltt.setIsLeaf(0);
				pltt.setId(Integer.parseInt(CHF.showNull(request.getParameter("id"))));
				pltt.setParentID(Integer.parseInt(request.getParameter("parentid")));
				pltt.setQuestion_DB(1);
				pltt.setTypeName(request.getParameter("typename"));
				pltm.AddOrModifyAQuestionType(pltt,request.getParameter("adored"));
				response.sendRedirect(request.getContextPath()+"/questiontype/List.jsp?init=no&pid="+request.getParameter("parentid"));
			}
		}
		else//修改页面
		{
			pltt = pltm.getAQuestionTypeDetail(id);
		}
		
%>

<%@page import="com.matech.framework.pub.util.ASFuntion"%>
<%@page import="com.matech.framework.pub.db.DbUtil"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.matech.framework.pub.db.DBConnect"%>
<%@page import="com.matech.audit.service.questionType.model.QuestionTypeTable"%>
<%@page import="com.matech.audit.service.questionType.QuestionTypeMan"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title></title>


<script type="text/javascript">


Ext.onReady(function (){
	new Ext.Toolbar({
			renderTo: "divBtn",
			height:30,
			defaults: {autoHeight: true,autoWidth:true},
	        items:[
		        { 
			        text:'返回',
			        icon:'${pageContext.request.contextPath}/img/back.gif', 
			        handler:function(){
						window.history.back();
					}
	  			},'-',
		     	{ 
		           text:'保存',
		           icon:'${pageContext.request.contextPath}/img/save.gif' ,
		           handler:function(){
		        	   if (!formSubmitCheck('thisForm')) return;
						  document.getElementById("opt").value="save";
						  document.getElementById("thisForm").submit();
					   }
		     	},'->'
	  		]
	});
	
});
</script>

</head>

<div id="divBtn" ></div>
<body leftmargin="0" topmargin="0">
<form name="thisForm" method="post" action="AddandEdit.jsp">

  <table width="600" height="72" border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td width="2%" height="24">&nbsp;</td>
      <td width="16%">分类名称：</td>
      <td width="82%"><input name="typename" type="text" id="typename" value="" class="required"  title="请输入名字！" style="width:200px"></td>
    </tr>
    <tr>
      <td height="24">&nbsp;</td>
      <td>
      	<%if(!"".equals(id)){%>所属分类：<%}%>
      </td>
      <td>
	<%
		if(id.equals("")){//增加页面
	%>
		<input name="parentid" type="text" id="parentid" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();"   valuemustexist=true autoid=95 multilevel=true class="required"  title="请选择分类！" style="width:0px" refer="id">
	<%	
		}else{
	%>
	  <input name="parentid" type="text" id="parentid" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" onClick="onPopDivClick(this);" valuemustexist=true autoid=95 multilevel=true class="required"  title="请选择分类！" style="width:200px" refer="id">
	 <%}%> 
	  </td>
    </tr>
  </table>

  <input name="id" type="hidden" id="id" value="<%=pltt.getId()%>">
  <input name="adored" type="hidden" id="adored" value="ad">
  <input name="opt" type="hidden" id="opt" value="">
</form>
</body>
</html>
<script>

<%
//	if(!tid.equals("0")&&!tid.equals("")){
		if(tid.equals("null"))tid="1";
	%>
		document.thisForm.parentid.value="<%=tid%>";
		if(thisForm.id.value=="1"){
	//	document.thisForm.parentid.readOnly =true;
		setObjDisabled("parentid");
		hide("parentid");
		}else{
		document.thisForm.parentid.readOnly =false;
		}
	<%
//	}
	if(!id.equals(""))
	{
	%>
	//	document.thisForm.id.value="<%=pltt.getId()%>";
		document.thisForm.typename.value="<%=pltt.getTypeName()%>";
//		document.thisForm.isLeaf.value="<%=pltt.getIsLeaf()%>";
		document.thisForm.parentid.value="<%=pltt.getParentID()%>";
		document.thisForm.adored.value="ed";
	<%
		}
	}catch(Exception e)
	{
		out.print(e);
	}finally{
		 DbUtil.close(conn);
	}
	%>

 new Validation('thisForm');
</script>
