<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>	
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>工作岗位变动记录</title>
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
<div id="divBtn"></div><!--

<table width="30%" cellspacing="0" cellpadding="0">
	<tr>
		<td height="5"></td>
	</tr>
</table>
<input name="add" type="button" class="flyBT" value=" 增 加 "
	onclick="goAdd();">
<input name="mod" type="button" class="flyBT" value=" 修 改 "
	onclick="goEdit();">
<input name="del" type="button" class="flyBT" value=" 删 除 "
	onclick="goDelete();">
<input name="view" type="button" class="flyBT" value=" 打 印 "
	onclick="print_postchange();">

--><form name="thisForm" method="post" action=""><!--

<c:if test="${all=='all' }">
<fieldset style="width:100%">
    <legend>查询条件</legend>
	<table width="100%" height="46" border="0" cellpadding="0" cellspacing="0" bgcolor="">
	<tr>
		<td>
		姓名：&nbsp;&nbsp;<input type="text" name="worker" id="worker" value="" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" autoid=7>
		&nbsp;&nbsp;
		岗位名：&nbsp;&nbsp;<input type="text" name="workDepart" id="workDepart" value="" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" autoid=2006>
		<input type="submit" name="srh" class="flyBT" value="搜 索"" />
             &nbsp;
             <input type="reset" name="csrh" value="显示全部" class="flyBT" onclick="thisForm.submit();" />
             </td>
	  </tr>
  </table>
</fieldset>
</c:if>

<table width="100%" cellspacing="0" cellpadding="0">
	<tr>
		<td height="20" align=center><font size=3 color=red><b>工作岗位变动记录</b></font></td>
	</tr>
</table>

<table width="30%" cellspacing="0" cellpadding="0">
	<tr>
		<td height="5"></td>
	</tr>
</table>
-->

<div style="height:expression(document.body.clientHeight-28);" >
<mt:DataGridPrintByBean name="postchange" />
</div>
</form>

<Script>

function goAdd()
{
//	window.location="/AuditSystem/oa/postchange/AddandEdit.jsp?all=${all}";
	window.location="${pageContext.request.contextPath}/postchange.do?method=addDefault&all=${all}";
}
function goDelete()
{	
	if(document.getElementById("chooseValue_postchange").value=="")
	{
		alert("请选择要删除的工作岗位变动记录！");
	}
	else
	{
		//alert(document.thisForm.chooseValue.value);
		if(confirm("确定删除此工作岗位变动记录？")){
			window.location="${pageContext.request.contextPath}/postchange.do?method=del&&autoid="+document.getElementById("chooseValue_postchange").value+"&all=${all}";
		}
	}
}
function goEdit()
{
	if(document.getElementById("chooseValue_postchange").value=="")
	{
		alert("请选择要修改的工作岗位变动记录！");
	}
	else
	{
		//alert(document.thisForm.chooseValue.value);
		window.location="${pageContext.request.contextPath}/postchange.do?method=edit&autoid="+document.getElementById("chooseValue_postchange").value+"&all=${all}";
	}
}
</Script>