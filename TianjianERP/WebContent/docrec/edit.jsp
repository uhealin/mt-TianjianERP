<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<title>Insert title here</title>
</head>
<body>
   <table class="formTable" cellpading="1">
	<tbody>
		<tr>
			<td colspan="6">
				收文处理单</td>
		</tr>
		<tr>
			<td style="width:150px">
				文件名</td>
			<td>
				<input property=""  ext_id=file_name  id="file_name"  ext_name=file_name  name="file_name"  ext_field=file_name  field=file_name  /></td>
			<td style="width:150px">
 
				文件来源</td>
			<td>
				<input property=""  ext_id=file_source  id="file_source"  ext_name=file_source  name="file_source"  ext_field=file_source  field=file_source  /></td>
			<td>
				发文文号</td>
			<td>
				<input property=""  ext_id=post_doc_no  id="post_doc_no"  ext_name=post_doc_no  name="post_doc_no"  ext_type=grid  type="text" grid=true  ext_field=post_doc_no  field=post_doc_no  ext_select=5001  autoid="5001"  /></td>
		</tr>
		<tr>
			<td>
				收文文号</td>
			<td>
				<input property=""  ext_id=rec_doc_no  id="rec_doc_no"  ext_name=rec_doc_no  name="rec_doc_no"  ext_field=rec_doc_no  field=rec_doc_no  /></td>
			<td>
				收文日期</td>
			<td>
				<input property=""  ext_id=rec_date  id="rec_date"  ext_name=rec_date  name="rec_date"  ext_type=date  type="text"  ext_field=rec_date  field=rec_date  /></td>
			<td>
				附件上传</td>
			<td>
				</td>
		</tr>
		<tr>
			<td>
				流转范围</td>
			<td>
				<input property=""  ext_id=roam_range  id="roam_range"  ext_name=roam_range  name="roam_range"  ext_field=roam_range  field=roam_range  /></td>
			<td>
				截止日期</td>
			<td colspan="3">
				<input property=""  ext_id=timeout_date  id="timeout_date"  ext_name=timeout_date  name="timeout_date"  ext_type=date  type="text"  ext_field=timeout_date  field=timeout_date  /></td>
		</tr>
		<tr>
			<td>
				处理方式</td>
			<td colspan="2">
				<input property=""  ext_id=handle_type  id="handle_type"  ext_name=handle_type  name="handle_type"  ext_type=singleSelect  type="text" valuemustexist=true  ext_field=handle_type  field=handle_type  ext_select=5000  autoid="5000"  /></td>
			<td>
				传阅人办理时限</td>
			<td>
				<input property=""  ext_id=handle_hour  id="handle_hour"  ext_name=handle_hour  name="handle_hour"  ext_size=5  size=5  ext_type=text  type="text"  ext_field=handle_hour  field=handle_hour  ext_validate=validate-positiveInt  class="validate-positiveInt"  />小时</td>
			<td>
				</td>
			<td>
				</td>
		</tr>
	</tbody>
</table>
   
</body>
</html>