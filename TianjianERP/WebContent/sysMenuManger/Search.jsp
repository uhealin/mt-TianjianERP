<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<fieldset>
    <legend>系统菜单查找</legend>
	<table border="0" cellpadding="0" cellspacing="0" bgcolor="">
		<tr>
                  <td>菜单ID：</td>
                  <td align=left>
                    <input type="text" name="menu_id"
                    	 value="<%=session.getAttribute("menu_id")!=null?session.getAttribute("menu_id").toString():""%>">
                  </td>
                  <td width="20">&nbsp;</td>
                  <td>名称：</td>
                  <td align=left>
                    <input type="text" name="name"
                     value="<%=session.getAttribute("name")!=null?session.getAttribute("name").toString():""%>">
                  </td>

		 <td height="5" colspan=10></td>

			<td height=10 colspan = 10 align="right">
			<input type="button" name="srh" class="flyBT" value="搜 索" onclick="subSearch();">&nbsp;
			<input type="button" name="csrh" class="flyBT" value="清 空" onclick="subClearSearch();">
			</td>
		</tr>
	</table>
</fieldset>
<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
	<tr>
		<td height=10></td>
	</tr>
</table>
