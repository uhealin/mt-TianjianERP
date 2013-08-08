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
		},'-',{
			text:'发送手机短信',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/tel.gif',
			handler:function(){
				sendSms();
			}
		},'-',{
			text:'发送邮件',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/notes.gif',
			handler:function(){
				sendEmail()();
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
<mt:DataGridPrintByBean name="yingjieList" />
</div>
</form>

<Script>

function goAdd()
{
	window.location="${pageContext.request.contextPath}/resume.do?method=yjEdit";
}
function goDelete()
{	
	if(document.getElementById("chooseValue_societyList").value=="")
	{
		alert("请选择要删除的简历！");
	}
	else
	{
		//alert(document.thisForm.chooseValue.value);
		if(confirm("确定删除此简历？")){
			window.location="${pageContext.request.contextPath}/resume.do?method=del&uuid="+document.getElementById("chooseValue_yingjieListt").value;
		}
	}
}
function goEdit()
{
	if(document.getElementById("chooseValue_yingjieList").value=="")
	{
		alert("请选择要查看的简历！");
	}
	else
	{
		//alert(document.thisForm.chooseValue.value);
		window.location="${pageContext.request.contextPath}/resume.do?method=look&uuid="+document.getElementById("chooseValue_yingjieList").value+"";
	}
}
function sendSms(){
  
   window.location.href="job.do?method=sendSms";
}
function sendEmail(){

   window.location.href="interiorEmail.do?method=sendEmail";
}
</Script>