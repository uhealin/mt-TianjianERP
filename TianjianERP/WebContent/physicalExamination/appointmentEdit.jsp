<%@page import="com.matech.audit.service.physicalExamination.model.InformVO"%>
<%@ page language="java" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<script type="text/javascript">
   function batch_number_onchange(){
      var i;
      i=${inform.batch_number};
      var p;
      for(var j=1;j<9;j++){
          var id="time_"+j;
          if(j <= i){
             p=document.getElementById(id);
             p.style.display="block";
          }else{
             p=document.getElementById(id);
             p.style.display="none";
          }
      }
    }
</script>
</head>
<body onload="batch_number_onchange()" >
	
   
	<table class="formTable">
		<thead>
		   <tr>
				<th colspan="20">体检通知详细信息</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td><input type="hidden" id="uuid" name="uuid" value="${inform.uuid}"></td>
			</tr>
			<tr>
				<th>体检通知</th>
				<td><input type="text" value="${inform.notice_title }" /></td>
			</tr>
			<tr>
				<th>详细说明</th>
				<td><input type="text" value="${inform.detailed_description}" /></td>
			</tr>
			<tr id="batch_number">
				<th>批次数</th>
				<td><input type="text" value="${inform.batch_number}"/></td>
			</tr>
			<tr>
				<th>每批人数上限</th>
				<td><input type="text" value="${inform.number_limit}" /></td>
			</tr>
			<tr>
				<th>报名截止日期</th>
				<td><input type="text" value="${inform.closing_date}" ></td>
			</tr>
			<tr>
				<th>实际报名人数</th>
				<td><input type="text" value="${inform.reality_number}" /></td>
			</tr>
			<tr id="time_1">
				<th>批次1时间</th>
				<td><input type="text" value="${inform.batch_time_1}" /></td>
			</tr>
			<tr id="time_2">
				<th>批次2时间</th>
				<td><input type="text" value="${inform.batch_time_2}" /></td>
			</tr>
			<tr id="time_3">
				<th>批次3时间</th>
				<td><input type="text" value="${inform.batch_time_3}" /></td>
			</tr>
			<tr id="time_4">
				<th>批次4时间</th>
				<td><input type="text" value="${inform.batch_time_4}" /></td>
			</tr>
			<tr id="time_5">
				<th>批次5时间</th>
				<td><input type="text" value="${inform.batch_time_5}" /></td>
			</tr>
			<tr id="time_6">
				<th>批次6时间</th>
				<td><input type="text" value="${inform.batch_time_6}"/></td>
			</tr>
			<tr id="time_7">
				<th>批次7时间</th>
				<td><input type="text" value="${inform.batch_time_7}"/></td>
			</tr>
			<tr id="time_8">
				<th>批次8时间</th>
				<td><input type="text" value="${inform.batch_time_8}"/></td>
			</tr>
		</tbody>
	<form action="" id="f1" >
		<tbody>
			<tr>
				<th colspan="1">请选择要参加的批次：</th>
				<td>
					<select id="choose_batch" name="choose_batch">
						    <% 
						    InformVO vo=(InformVO)request.getAttribute("inform");
						    for(int i=1;i<=8;i++){ %>
						    <% if(vo.getBatch_number()>=i){ %>
							<option value="<%=i%>" >批次<%=i%></option>
							   <%}
						    }%>
					</select>
					
				</td>
			</tr>
			<tr>
				<th>是否确认提交？</th>
				<td><input type="button" value="确认" onclick="submit_onclick()"/></td>
			</tr>
		</tbody>
		<input type="hidden" id="inform_uuid" name="inform_uuid" value="${inform.uuid}">
	</form>
	</table>
   
</body>
<script type="text/javascript">
	function submit_onclick(){
		var f1= document.getElementById("f1");
		f1.action="appointment.do?method=list";
		//alert("${inform.uuid}");
		f1.submit();
	}
</script>
</html>