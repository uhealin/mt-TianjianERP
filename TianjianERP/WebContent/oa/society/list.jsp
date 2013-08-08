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
           text:'修改',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/edit.gif',
           handler:function(){
				goEdit();
		   }
        },'-',{
			text:'删除',
			id:'del',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/delete.gif',
			handler:function(){
				goDelete();
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
<div style="height:expression(document.body.clientHeight-28);" >
<mt:DataGridPrintByBean name="societyList" />
</div>
</form>

<Script>

function goAdd()
{
	window.location="${pageContext.request.contextPath}/worknote.do?method=societyEdit&userid=${param.userid}";
}
function goDelete()
{	
	if(document.getElementById("chooseValue_societyList").value=="")
	{
		alert("请选择要删除的工作记录！");
	}
	else
	{
		//alert(document.thisForm.chooseValue.value);
		if(confirm("确定删除此工作记录？")){
			window.location="${pageContext.request.contextPath}/worknote.do?method=societySave&flag=del&userid=${param.userid}&autoid="+document.getElementById("chooseValue_societyList").value;
		}
	}
}
function goEdit()
{
	if(document.getElementById("chooseValue_societyList").value=="")
	{
		alert("请选择要修改的工作记录！");
	}
	else
	{
		//alert(document.thisForm.chooseValue.value);
		window.location="${pageContext.request.contextPath}/worknote.do?method=societyEdit&autoid="+document.getElementById("chooseValue_societyList").value+"";
	}
}
</Script>