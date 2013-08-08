<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>考试信息</title>
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
<style type="text/css">

.before{
	border: 0px;
	background-color: #FFFFFF !important;
}

.data_tb {
	background-color: #ffffff;
	text-align:center;
	margin:0 0px;
	width:60%;
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
			<td class="data_tb_content"><input class="required" id="examType" size="80" name="examType" type="text" value="${exam.examType }" title="请输入，不得为空" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" noinput="true" onClick="onPopDivClick(this);" autoid=3052/></td>
		</tr>
		<tr>
			<td class="data_tb_alignright">考试科目:</td>
			<td class="data_tb_content">
				<input class="required" id="examSubject" name="examSubject" size="80" type="text" value="${exam.examSubject }" title="请输入，不得为空"/>
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright">报名开始时间:</td>
			<td class="data_tb_content">
				<input class="required" disabled="disabled" size="80" id="registrationStartTime1" readonly="readonly" name="registrationStartTime1" type="text" value="${exam.registrationStartTime }"/>
				<input readonly="readonly" id="registrationStartTime" size="80" name="registrationStartTime" type="hidden" value="${exam.registrationStartTime }" title="请输入，不得为空"/>
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright">报名结束时间:</td>
			<td class="data_tb_content">
				<input class="required" disabled="disabled" size="80" id="registrationEndTime1" readonly="readonly" name="registrationEndTime1" type="text" value="${exam.registrationEndTime }"/>
				<input readonly="readonly" id="registrationEndTime" size="80" name="registrationEndTime" type="hidden" value="${exam.registrationEndTime }" title="请输入，不得为空"/>
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright">资格要求:</td>
			<td class="data_tb_content"><input class="required" size="80" id="qualifications" name="qualifications" type="text" value="${exam.qualifications }" title="请输入，不得为空"/></td>
		</tr>
		<tr>
			<td class="data_tb_alignright">考试时间:</td>
			<td class="data_tb_content">
				<input class="required" disabled="disabled" id="examTime1" size="80" readonly="readonly" name="examTime1" type="text" value="${exam.examTime }"/>
				<input readonly="readonly" id="examTime" name="examTime" size="80" type="hidden" value="${exam.examTime }" title="请输入，不得为空"/>
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright">备注:</td>
			<td class="data_tb_content">
				<input id="remark" name="remark" size="80" type="text" value="${exam.remark}"/>
			</td>
		</tr>
	</table>
	<input type="hidden" id="act" name="act" value="${act }"/>
	<input type="hidden" id="id" name="id" value="${exam.id }"/>
</form>
</body>
<script type="text/javascript">
new Ext.form.DateField({			
	applyTo : 'registrationStartTime1',
	width: 150,
	minValue : new Date(),
	format: 'Y-m-d'	
});
new Ext.form.DateField({			
	applyTo : 'registrationEndTime1',
	width: 150,
	minValue : new Date(),
	format: 'Y-m-d'	
});
new Ext.form.DateField({			
	applyTo : 'examTime1',
	width: 150,
	minValue : new Date(),
	format: 'Y-m-d'	
});

new Validation('thisForm');
function goAdEd(){
	if (!formSubmitCheck('thisForm')) return ;
	var act=document.getElementById("act").value;
	var registrationStartTime=document.getElementById("registrationStartTime1").value;
	document.getElementById("registrationStartTime").value=registrationStartTime;
	var registrationEndTime=document.getElementById("registrationEndTime1").value;
	document.getElementById("registrationEndTime").value=registrationEndTime;
	var examTime=document.getElementById("examTime1").value;
	document.getElementById("examTime").value=examTime;
	document.thisForm.action="education.do?method=saveOrUpdateExam&&act="+act;
	document.thisForm.submit();
}
</script>
</html>