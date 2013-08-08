<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="../hasNoRight.jsp"%>
<fieldset>
    <legend>部门查找</legend>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
		<tr>
			<td width=80>部门编号：</td>
			<td align=left><input type="text" name="menu_id"
							value="<%=session.getAttribute("menu_id")!=null?session.getAttribute("menu_id").toString():""%>"></td>
			<td width=150></td>
			<td width=80>部门名称：</td>
			<td align=left><input type="text" name="name"
							value="<%=session.getAttribute("name")!=null?session.getAttribute("name").toString():""%>"></td>
		</tr>
		<tr>
		 <td height="5" colspan=10></td>
		</tr>
		<tr>
		 <td height="1" bgcolor="" colspan=10></td>
		</tr>
		<tr>
		 <td height="5" colspan=10></td>
		</tr>
		<tr>
			<td height=10 colspan = 10 align=center>
			<input type="button" name="srh" value="搜 索" onclick="subSearch();">&nbsp;&nbsp;&nbsp;&nbsp;
			<input type="button" name="csrh" value="清 除" onclick="goInitPage();">
			</td>
		</tr>
	</table>
</fieldset>
<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
	<tr>
		<td height=10></td>
	</tr>
</table>
