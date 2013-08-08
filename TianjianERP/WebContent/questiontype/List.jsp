<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@  page  import="java.sql.*"%>
<%@  page  import="java.io.*"%>
<%@ taglib prefix="mt" uri="http://www.matech.cn/tag" %>
<%@page import="com.matech.audit.pub.datagrid.DataGrid"%>
<%@page import="com.matech.framework.pub.util.ASFuntion"%>
<%@page import="com.matech.audit.pub.datagrid.DataGridProperty"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>问题类型管理</title>

<Script type="text/javascript">

function ext_init(){ 
		var tbar = new Ext.Toolbar({
		renderTo: 'divBtn',
		items:[
			{
				text:'新增',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/add.gif',
				handler:function () {
					goAdd();
				}
			},'-',
			{
	           text:'修改',
	           cls:'x-btn-text-icon',
	           icon:'${pageContext.request.contextPath}/img/edit.gif',
	          	handler:function(){
					goEdit();
				}
	        },'-',
			{
	           text:'删除',
	           cls:'x-btn-text-icon',
	           icon:'${pageContext.request.contextPath}/img/delete.gif',
	          	handler:function(){
					goDelete();
				}
	        },'-',
	        {
				text:'返回',
				cls:'x-btn-text-icon',
				icon:'${pageContext.request.contextPath}/img/query.gif',
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

<form name="thisForm" method="post" action="">
<%

ASFuntion CHF = new ASFuntion();
String pid = CHF.showNull(request.getParameter("pid"));
if("null".equals(pid)||"".equals(pid))pid="1";
DataGridProperty pp = new DataGridProperty();


//必要设置
pp.setTableID("questiontype");
//基本设置
//pp.setDatabaseDepartID("100900");
pp.setPageSize_CH(50);
//	pp.setWhichFieldIsValue(0);

//sql设置

String subSearchID = CHF.showNull(request.getParameter("menu_id"));
String subSearchName = CHF.showNull(request.getParameter("name"));
String inType = CHF.showNull(request.getParameter("inType"));
String sql = "select a.ID,a.TypeName atn,a.ParentID,b.TypeName from p_Questiontype a left join  p_Questiontype b on a.parentID=b.ID where 1=1 and a.ParentID='"+pid+"' and a.id <>'1'";

//sql设置

if (!subSearchID.equals(""))
	sql = sql + "and DepartID like '%" + subSearchID + "%'";
if (!subSearchName.equals(""))
	sql = sql + "and DepartName like '%" + subSearchName + "%'";
if (!inType.equals(""))
	sql = sql + "and industryname like '%" + inType + "%'";

System.out.println(sql);

pp.setSQL(sql);
pp.setDirection_CH("ID,atn,ParentID,TypeName");
//pp.setDirection("desc");
//pp.addSqlWhere("summary", "where keyvalue like '%${name}%'");

pp.setInputType("radio");
pp.addColumn("编号", "ID");
pp.addColumn("问题类型名称", "atn");
pp.addColumn("所属问题ID号", "ParentID");
pp.addColumn("所属问题", "TypeName");



//pp.setTableHead("部门号,部门名称,上级部门,部门服务期地址");
pp.setWhichFieldIsValue(1);

request.getSession()
		.setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);




/*
	ASFuntion CHF=new ASFuntion();
	String pid = CHF.showNull(request.getParameter("pid"));
	if("null".equals(pid)||"".equals(pid))pid="1";
	String DGXML = "";
	DataGrid dg=new DataGrid();
	//dg.setInputType("checkbox");
	dg.setInputType("radio");
	dg.setPageSize(50);
	dg.setConfigXML("DGXML_CASESTYPE");
	dg.setOut(out);
	dg.setSession(session);
	dg.setDefaultCol(5);
	dg.setColName("编号,问题类型名称,所属问题ID号,所属问题,其它,");
	dg.setWhichFieldIsValue(0);
	if(request.getParameter("init")!=null)
	{
		DGXML = (String)session.getAttribute("DGXML_CASESTYPE");
	}
	dg.setParameter(DGXML);
	dg.setSQL("select a.ID,a.TypeName,a.ParentID,b.TypeName,0 from p_Questiontype a left join  p_Questiontype b on a.parentID=b.ID where 1=1 and a.ParentID='"+pid+"' and a.id <>'1'");

	*/
	
%>

<div style="height:expression(document.body.clientHeight-23);" >
<mt:DataGridPrintByBean name="questiontype"  />
</div>

</form>
<Script>
function goReturn(){
	var pid="<%=pid%>";
	if(pid=="null"){
		window.location="../question/List.jsp";
	}else{
		window.location="../question/List.jsp?pid="+pid;
	}
}
function goInitPage()
{

	window.location="List.jsp";
}
function goSearch1()
{

	window.open('../AS_SYSTEM/search4Grid.jsp?allColName='+ document.thisForm.allColName_CH.value,'_blank','menubar=0,status=0,toolbar=0,width=464,height=171,top=210,left=170');
}
function goAdd()
{

	window.location="AddandEdit.jsp?tid=<%=pid%>";
}
function goDelete()
{
	if(document.getElementById("chooseValue_questiontype").value=="")
	{
		alert("请选择要删除的分类！");
	}
	else
	{
		if(confirm("是否确定要删除分类？")){
			var oBao = new ActiveXObject("Microsoft.XMLHTTP");
			oBao.open("GET","Del.jsp?chooseValue="+document.getElementById("chooseValue_questiontype").value,false);
			oBao.send();
			var strResult = unescape(oBao.responseText);
			window.location.reload();
		}else{
			return false;
		}

	}
}
function goEdit()
{
	if(document.getElementById("chooseValue_questiontype").value=="")
	{
		alert("请选择要修改的问题！");
	}
	else
	{
		window.location="AddandEdit.jsp?chooseValue="+document.getElementById("chooseValue_questiontype").value+"&idtxt=";
	}
}
function goRead()
{
	if(document.getElementById("chooseValue_questiontype").value=="")
	{
		alert("请选择要查看的问题！");
	}
	else
	{
		window.open("View.jsp?chooseValue="+document.getElementById("chooseValue_questiontype").value);

	}
}
function subClearSearch()
{
	window.location="List.jsp";
}

try{
	parent.refreshTree();
}catch(e){}

</script>
