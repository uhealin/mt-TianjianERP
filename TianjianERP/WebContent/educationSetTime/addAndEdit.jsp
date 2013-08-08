<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gbk">
<title>Insert title here</title>
<script type="text/javascript">
Ext.onReady(function(){
		var tbar_customer = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		height:30,
	   		defaults: {autoHeight: true,autoWidth:true},
            items:[{
	            text:'保存',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/save.gif',
	            handler:function(){
	            	goAdEd();
	            }
       		},'-',{ 
			        text:'返回',
			        cls:'x-btn-text-icon',
			        icon:'${pageContext.request.contextPath}/img/back.gif',
			        handler:function(){
						history.back();
					}
		       	}]
		})
})
</script>
<script type="text/javascript">
window.onload=function(){
	var yearOne=document.getElementById("getYearOne").value;
	var yearTwo=document.getElementById("getYearTwo").value;
	var selectYearOne=document.getElementById("selectYearOne");
	var selectYearTwo=document.getElementById("selectYearTwo");
	if(yearOne!=null && yearOne!=""){
		selectYearOne.value = yearOne;
		selectYearOne.text = yearOne;
	}
	if(yearTwo!=null && yearTwo!=""){
		selectYearTwo.value = yearTwo;
		selectYearTwo.text = yearTwo;
	}
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
	font-family:"宋体";
}
.data_tb_content {
	PADDING-LEFT: 2px; 
	BORDER-TOP: #8db2e3 1px solid; 
	BORDER-LEFT: #8db2e3 1px solid;
	BORDER-RIGHT: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid;  
	WORD-BREAK: break-all; 
	TEXT-ALIGN: left; 
	WORD-WRAP: break-word
}
</style>
</head>
<body>
<div id="divBtn" ></div>
<form id="thisForm" name="thisForm" action="" method="post">
<table  class="data_tb"  align="center">
	<tr>
		<td class="data_tb_alignright">职级:</td>
		<td class="data_tb_content"><input type="text" id="rankId" class="required"
				maxlength="10"  name="rankId" title="请输入，不能为空！" 
				onfocus="onPopDivClick(this);"
			onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();"
			onclick="onPopDivClick(this);" 
			  autoid=959  noinput=true 
				 value="${educationSetTime.rankId}" ></td>
		<td class="data_tb_alignright">学时检查年度:<input type="hidden" id="getYearOne" name="getYearOne" value="${educationSetTime.yearOne}"/><input type="hidden" id="getYearTwo" name="getYearTwo" value="${educationSetTime.yearTwo}"/></td>
		<td class="data_tb_content"><select id="selectYearOne" name="selectYearOne">
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
		</select></td>
		<input type="hidden" id="selectYearTwo" name="selectYearTwo"/>
	</tr>
	<!-- <tr>
		
		<td class="data_tb_alignright">二年学时年份:</td>
		<td class="data_tb_content"><select id="selectYearTwo" name="selectYearTwo">
		<%
			for(int i = 1950; i<=year+10;i++){
		%>
			<option value="<%=i %>" <%if(i==year){ %>selected="selected"<%} %>><%=i %></option>
		<%
			}
		%>
		</select></td>
	</tr> -->
	<tr>
		<td class="data_tb_alignright">本年必修学时:</td>
		<td class="data_tb_content"><input class="required validate-positiveInt" id="timeOne" name="timeOne" maxlength="3" type="text" value="${educationSetTime.timeOne}"/><input id="id" name="id" value="${id}" type="hidden"/></td>
		<td class="data_tb_alignright">上年必修学时:</td>
		<td class="data_tb_content"><input class="required validate-positiveInt" id="timeTwo" name="timeTwo" maxlength="3" type="text" value="${educationSetTime.timeTwo}"/><input id="act" name="act" value="${act}" type="hidden"/></td>
	</tr>
</table>
</form>	
</body>
<script type="text/javascript">
	function goAdEd(){
		var one=document.getElementById("selectYearOne").value;
		document.getElementById("selectYearTwo").value=one-1;
		var act=document.getElementById("act").value;
		var form =document.getElementById("thisForm");
		form.action="educationSetTime.do?method=saveOrUpdate&act="+act;
		form.submit();
	}
</script>
</html>