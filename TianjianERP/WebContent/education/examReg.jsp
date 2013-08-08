<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>考试报名</title>
<script type="text/javascript">
Ext.onReady(function(){
		var tbar_customer = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		height:30,
	   		defaults: {autoHeight: true,autoWidth:true},
            items:[{
	            text:'报名',
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
	<table class="data_tb"  align="center">
		<tr>
			<td class="data_tb_alignright">考试类型:</td>
			<td class="data_tb_content"><input class="required" readonly="readonly" id="examType" name="examType" type="text" value="${exam.examType }" title="请输入，不得为空"/></td>
		</tr>
		<tr>
			<td class="data_tb_alignright">考试科目:</td>
			<td class="data_tb_content">
				<input class="required" readonly="readonly" id="examSubject" name="examSubject" type="text" value="${exam.examSubject }" title="请输入，不得为空"/>
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright">报名开始时间:</td>
			<td class="data_tb_content">
				<input class="required" disabled="disabled" id="registrationStartTime1" readonly="readonly" name="registrationStartTime1" type="text" value="${exam.registrationStartTime }"/>
				<input readonly="readonly" id="registrationStartTime" name="registrationStartTime" type="hidden" value="${exam.registrationStartTime }" title="请输入，不得为空"/>
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright">报名结束时间:</td>
			<td class="data_tb_content">
				<input class="required" disabled="disabled" id="registrationEndTime1" readonly="readonly" name="registrationEndTime1" type="text" value="${exam.registrationEndTime }"/>
				<input readonly="readonly" id="registrationEndTime" name="registrationEndTime" type="hidden" value="${exam.registrationEndTime }" title="请输入，不得为空"/>
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright">资格要求:</td>
			<td class="data_tb_content"><input readonly="readonly" class="required" id="qualifications" name="qualifications" type="text" value="${exam.qualifications }" title="请输入，不得为空"/></td>
		</tr>
		<tr>
			<td class="data_tb_alignright">考试时间:</td>
			<td class="data_tb_content">
				<input class="required" disabled="disabled" id="examTime1" readonly="readonly" name="examTime1" type="text" value="${exam.examTime }"/>
				<input readonly="readonly" id="examTime" name="examTime" type="hidden" value="${exam.examTime }" title="请输入，不得为空"/>
			</td>
		</tr>
	</table>
	<input type="hidden" id="id" name="id" value="${exam.id }"/>
</form>
</body>
<script type="text/javascript">
function goAdEd(){
	//document.thisForm.action="education.do?method=educationReg&&educationId="+${education.id};
	//document.thisForm.submit();
 	    var url="${pageContext.request.contextPath}/education.do?method=isRegExam";
		var requestString = "&examRegListId="+${exam.id};
		var request= ajaxLoadPageSynch(url,requestString);
		if(request=="fail"){
			alert("你已经报名了");
		}
		else{
			window.location="education.do?method=examReg&&examRegListId="+${exam.id};
			//openTab("lookFlowss","我的报名","/AuditSystem/education.do?method=examReg",window.parent);
			//window.location="education.do?method=myList";
		}
}
</script>
</html>