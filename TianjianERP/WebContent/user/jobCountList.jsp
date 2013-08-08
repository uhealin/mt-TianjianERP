<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>招聘计划统计</title>
<script type="text/javascript">

Ext.onReady(function(){
	
	var tbar_customer = new Ext.Toolbar({
		renderTo:'gridDiv_${tableid}',
           items:[
       {
           text:'打印',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/print.gif',
          	handler:function(){
				print_bmzpjhtj();
			}
        },'-',{
	            text:'关闭',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function () {
	            	closeTab(parent.tab);
	            }
	     }
        ]
        });  

});

</script>


</head>
<body>
<form name="thisForm" method="post" action="" >
<div style="height:expression(document.body.clientHeight-23);" >
<mt:DataGridPrintByBean name="bmzpjhtj"  />
</div>

<input type="hidden" id="flag" name="flag" value="${flag }">

<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>

<div id="search" style="display:none;">
<br>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
<table border="0" cellpadding="0" cellspacing="0" bgcolor="" width="100%" align="center">
	<tr>
      <td align="right">部门：</td>
      <td align=left>
        <input name="departmentid" type="text" id="departmentid"  onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true autoid=123 noinput=true autoHeight=150 >
      </td>
   	</tr>
	<tr>
      <td align="right">岗位名称：</td>
      <td align=left>
        <input value='' name="jobname" type="text" id="jobname" >
      </td>
   	</tr>
   	<tr>
      <td align="right">工作城市：</td>
      <td align=left>
      	<input value='' name="city" type="text" id="city" autoid=740 multilevel=true onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true noinput=true autoHeight=150>
      </td>
	</tr>
	<tr>
      <td align="right">到岗时间：</td>
      <td align=left>
      	<input value='' name="toworktime" type="text" id="toworktime"  >
      </td>
	</tr>
	<tr>
      <td align="right">工时要求：</td>
      <td align=left>
      	<select id='working' name="working" style="width: 130px">
      		<option value = ''  >--请选择--</option>
			<option value = '全职'  >全职</option>
			<option value = '实习'  >实习</option>
		</select>
      </td>
	</tr>
	<tr>
      <td align="right">状态：</td>
      <td align=left>
      		<select id='state' name="state" style="width: 130px">
      		<option value = ''  >--请选择--</option>
			<option value = '有效'  >有效</option>
			<option value = '失效'  >失效</option>
			</select>
      </td>
	</tr>
</table>
</div>
</form>
</body>
</html>
