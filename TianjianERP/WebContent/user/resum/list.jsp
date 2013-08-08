<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>	
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>社会职务</title>
<script type="text/javascript">


function ext_init(){ 
	var tbar = new Ext.Toolbar({
		renderTo: 'divBtn',
		items:[{
			text:'新增',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/add.gif',
			handler:function () {
				goAdd();
			}
		},'-',{
           text:'查看',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/edit.gif',
           handler:function(){
				goLook();
		   }
        },'-',{
			text:'删除',
			id:'del',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/delete.gif',
			handler:function(){
				goDelete();
			}
		},'-',{
			text:'修改',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/edit.gif',
			handler:function(){
				edit();
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
<div style="height:expression(document.body.clientHeight-23);" >
<mt:DataGridPrintByBean name="resumList" />
</div>
</form>

<Script>

function goAdd()
{
	window.location="${pageContext.request.contextPath}/resum.do?method=view";
}
function goDelete()
{	
	if(document.getElementById("chooseValue_resumList").value=="")
	{
		alert("请选择要删除的列！");
	}
	else
	{
		
		var autoid = document.getElementById("chooseValue_resumList").value;
		if(confirm("确定删除此项？")){
			window.location="${pageContext.request.contextPath}/resum.do?method=del&autoid="+autoid;
		}
	}
}
function edit()
{
	if(document.getElementById("chooseValue_resumList").value=="")
	{
		alert("请选择要修改的项！");
	}
	else
	{
		var autoid = document.getElementById("chooseValue_resumList").value;
		window.location="${pageContext.request.contextPath}/resum.do?method=edit&autoid="+autoid;
	}
}
function goLook(){
if(document.getElementById("chooseValue_resumList").value=="")
	{
		alert("请选择要查看的项！");
	}else{
		window.location="${pageContext.request.contextPath}/resum.do?method=goLook&autoid="+document.getElementById("chooseValue_resumList").value;
	}
		
}

</Script>