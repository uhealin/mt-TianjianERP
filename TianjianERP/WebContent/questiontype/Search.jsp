<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<fieldset>
    <legend>系统菜单查找</legend>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
		<tr>
			<td width=50>编号：</td>
			<td align=left><input type="text" name="menu_id"></td>
			<td width=150></td>
			<td width=50>名称：</td>
			<td align=left><input type="text" name="name"></td>
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
			<input type="button" name="csrh" value="清 除" onclick="subClearSearch();">
			</td>
		</tr>
	</table>
</fieldset>
<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
	<tr>
		<td height=10></td>
	</tr>
</table>
