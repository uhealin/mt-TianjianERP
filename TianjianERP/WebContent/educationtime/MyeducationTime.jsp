<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
    <%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>我的学时</title>
<script type="text/javascript">
	window.onload=function(){
		var year=document.getElementById("getYear").value;
		var selectYear=document.getElementById("selectYear");
		selectYear.value=year;
		selectYear.text=year;
	}
</script>
<style type="text/css">

.before{
	border: 0px;
	background-color: #FFFFFF !important;
}

.data_tb {
	background-color: #ffffff;
	text-align:center;
	margin:0 0px;
	width:40%;
	border:#8db2e3 1px solid; 
	BORDER-COLLAPSE: collapse; 
	margin-top: 20px;
	text-align:center;
}
.data_tb_alignright {	
	BACKGROUND: #e4f4fe; 
	white-space:nowrap;
	padding:5px;
	border-top: #8db2e3 1px solid;
	border-left: #8db2e3 1px solid;
	BORDER-RIGHT: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid; 
	height:30px;
	background-color: #d3e1f1;
	width:15%;
	font-size: 13px;
	text-align:left;
	font-family:"宋体";
}
.data_tb_content {
	PADDING-LEFT: 2px; 
	BORDER-TOP: #8db2e3 1px solid; 
	BORDER-LEFT: #8db2e3 1px solid;
	BORDER-RIGHT: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid;  
	width:50%;
	WORD-BREAK: break-all; 
	TEXT-ALIGN: left; 
	WORD-WRAP: break-word
}
</style>
</head>
<body>
<form id="thisFrm" name="thisFrm" method="post">
<table class="data_tb"  align="center">
<tr>
	<td class="data_tb_alignright">人员姓名：<td>
	<td class="data_tb_content">${username}</td>
	<td class="data_tb_alignright">  当前职级：</td>
	<td class="data_tb_content">${rank}<input type="hidden" id="getYear" name="getYear" value="${year}"/></td>
	<td class="data_tb_alignright">请选择要查询的年份:<td>
	<td class="data_tb_content" >
		<select onchange="goFind()" id="selectYear" name="selectYear">
		<%
		   ASFuntion ASF = new ASFuntion();
		   String strYear = ASF.getCurrentDate();
		   int year = Integer.valueOf(strYear.substring(0,4));
			for(int i = 1950; i<=year+10;i++){
		%>
			<option value="<%=i %>" <%if(i==year){ %>selected="selected"<%} %>><%=i %></option>
		<%
			}
		%>
		</select>
	</td>
</tr>
<tr>
	<td class="data_tb_alignright" colspan="8">一年(${yearOne }年)必修学时数：${lastEducationSetTime}小时，1年实修学时数: ${lastEducationTime}小时 ${isPassOne}</td>
</tr>
<tr>
	<td  class="data_tb_alignright" colspan="8">二年(${yearTwo }年)必修学时数：${educationSetTime}小时，2年实修学时数: ${educationTime}小时${isPassTwo}<c:if test="${isPassTwo=='(不通过)'|| isPassOne=='(不通过)'}"><a href="#" onclick="goReg()">报名</a></c:if></td>	
</tr>
</table>
</form>
  <div id="educationtimedetail" style="height:90%;width:100%">
      <mt:DataGridPrintByBean name="myEducationtime"  message="请选择单位编号" />
  </div>
</body>
<script type="text/javascript">
	function goFind(){
		var form=document.getElementById("thisFrm");
		form.action="${pageContext.request.contextPath}/educationTime.do?method=myEducationTime";
		form.submit();
	}
		
	function goReg(){
		var url="education.do?method=onReg";
		parent.openTab("registrationId","培训报名管理",url);
	}
</script>
</html>