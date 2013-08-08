<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>	
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>员工奖惩记录管理</title>
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
	onclick="print_encouragement();">

--><form name="thisForm" method="post" action="/AuditSystem/encouragement.do?all=${all }">
<!--<c:if test="${all=='all' }">
<fieldset style="width:100%">
    <legend>查询条件</legend>
	<table width="100%" height="46" border="0" cellpadding="0" cellspacing="0" bgcolor="">
	<tr>
		<td>
		奖征人：&nbsp;&nbsp;<input type="text" name="searchPerson" id="searchPerson" value="" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" autoid=7>
		&nbsp;&nbsp;
		奖征类型：&nbsp;&nbsp;<input type="text" name="searchType" id="searchType" value="" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" autoid=2010>
		<input type="submit" name="srh" class="flyBT" value="搜 索"  />
             &nbsp;
             <input type="reset" name="csrh" value="显示全部" class="flyBT" onclick="thisForm.submit();" />
             </td>
	  </tr>
  </table>
</fieldset>
</c:if>

<table width="100%" cellspacing="0" cellpadding="0">
	<tr>
		<td height="20" align=center><font size=3 color=red><b>员工奖惩记录管理</b></font></td>
	</tr>
</table>

<table width="30%" cellspacing="0" cellpadding="0">
	<tr>
		<td height="5"></td>
	</tr>
</table>
-->

<div style="height:expression(document.body.clientHeight-28);" >
<mt:DataGridPrintByBean name="encouragement" />
</div>
</form>

<Script>

function goAdd()
{
	window.location="${pageContext.request.contextPath}/encouragement.do?method=edit&all=${all}";
//	window.location="/AuditSystem/encouragement.do?method=edit&all=${all}";
}
function goDelete()
{	
	if(document.getElementById("chooseValue_encouragement").value=="")
	{
		alert("请选择要删除的员工奖惩记录！");
	}
	else
	{
		//alert(document.thisForm.chooseValue.value);
		if(confirm("确定删除此员工奖惩记录？")){
			window.location="${pageContext.request.contextPath}/encouragement.do?method=del&&autoid="+document.getElementById("chooseValue_encouragement").value+"&all=${all}";
		}
	}
}
function goEdit()
{
	if(document.getElementById("chooseValue_encouragement").value=="")
	{
		alert("请选择要修改的员工奖惩记录！");
	}
	else
	{
		//alert(document.thisForm.chooseValue.value);
		window.location="${pageContext.request.contextPath}/encouragement.do?method=edit&autoid="+document.getElementById("chooseValue_encouragement").value+"&all=${all}";
	}
}
</Script>