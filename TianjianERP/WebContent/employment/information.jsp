<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>简历</title>
<style type="text/css">

.before{
	border: 0px;
	background-color: #FFFFFF !important;
}

.data_tb {
	background-color: #ffffff;
	text-align:center;
	margin:0 0px;
	width:90%;
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
.data_tb_content1 {
	PADDING-LEFT: 2px; 
	BORDER-TOP: #8db2e3 1px solid; 
	BORDER-LEFT: #8db2e3 1px solid;
	BORDER-RIGHT: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid;  
	WORD-BREAK: break-all; 
	WORD-WRAP: break-word
}

</style>
<script type="text/javascript">
Ext.onReady(function(){
	var tbar_customer = new Ext.Toolbar({
   		renderTo: "divBtn",
   		height:30,
   		defaults: {autoHeight: true,autoWidth:true},
        items:[{
            text:'完成',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/save.gif',
            handler:function(){
            	finish();
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
</head>
<body>
<div id="divBtn" ></div>
<br>
<div style="height:expression(document.body.clientHeight-40);overflow:auto;">
<form method="post" id="thisForm" name="thisForm" target="hidden_frame">
	<table class="data_tb"  align="center">
		<tr>
			<td class="data_tb_alignright" colspan="7">
				<h3 align="center">应聘信息<br> EMPLOYMENT INFORMATION &nbsp;<a href="javascript:void(0)" onclick="goAdEd('info')">保存</a></h3>
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright" style="width: 20%">
				应聘职位首选<br>Position Applied for:
			</td>
			<td class="data_tb_content" style="width: 20%" colspan="2"><input type="text" id="position" name="position" value="${info.position }"  onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" noinput="true" onClick="onPopDivClick(this);" valuemustexist=true autoid=3057/></td>
			<td class="data_tb_alignright" style="width: 20%">
				次选<br>Second Choice:
			</td>
			<td class="data_tb_content" style="width: 20%" colspan="2"><input type="text" id="secondChoice" name="secondChoice" value="${info.secondChoice }"  onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" noinput="true" onClick="onPopDivClick(this);" valuemustexist=true autoid=3057/></td>
			<td rowspan="4">
				<img alt="" src="${pageContext.request.contextPath}${userPhotoSrc}" height="120" width="100" id="bill" onerror="this.src='${pageContext.request.contextPath}/images/noPhoto.gif'">
				<input type="text" id="myText" size="1" style="display:none"> <br>
				<a href="javascript:void(0);" onclick="queryWinFun();">&nbsp;点击上传相片</a>
				<a href="javascript:void(0);" onclick="deletePhto();"><img src="${pageContext.request.contextPath}/images/del.gif"></a>
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright">
				首选工作城市<br>Preferred Work Location:
			</td>
			<td class="data_tb_content" colspan="2"><input class="required" title="请输入" type="text" id="preferred" name="preferred" value="${info.preferred }" size="30" style="border-style:none;border:0;border-bottom:1 solid blue;"  onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" noinput="true" onClick="onPopDivClick(this);" valuemustexist=true autoid=4583/></td>
			<td class="data_tb_alignright">
				次选工作城市<br>Secondary Work Location:
			</td>
			<td class="data_tb_content" colspan="2">
				<input type="text" id="secondWork" name="secondWork" value="${info.secondWork }" size="30" style="border-style:none;border:0;border-bottom:1 solid blue;" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" noinput="true" onClick="onPopDivClick(this);" valuemustexist=true autoid=4583/>
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright" rowspan="2">
				应聘者来源<br>Candidate’s Source:
			</td>
			<td class="data_tb_content" colspan="5">&nbsp;&nbsp;<input type="radio" name="candidateSource" value="self" <c:if test="${info.candidateSource == 'self' }">checked="checked"</c:if>/>自荐(Self Recommendation)&nbsp;&nbsp;
			<input type="radio" name="candidateSource" value="internal" <c:if test="${info.candidateSource == 'internal' }">checked="checked"</c:if>/>内部推荐(Internal Referral)&nbsp;&nbsp;
			<input type="radio" name="candidateSource" value="hunter" <c:if test="${info.candidateSource == 'hunter' }">checked="checked"</c:if>/>猎头推荐(Headhunter)</td>
		</tr>
		<tr>
			<td colspan="5" class="data_tb_content1" align="center">推荐人&nbsp;&nbsp;<input type="text" id="recommend" name="recommend" value="${info.recommend }" style="border-style:none;border:0;border-bottom:1 solid blue;"/></td>
		</tr>
		<tr>
			<td class="data_tb_alignright">
				您所期望的待遇状况(税前)<br>Salary Expectation(pre-tax)
			</td>
			<td class="data_tb_alignright">月工资总额<br>Monthly Total Salary</td>
			<td class="data_tb_content"><input type="text" id="monthSalary" name="monthSalary" value="${info.monthSalary }" style="border-style:none;border:0;border-bottom:1 solid blue;"/></td>
			<td class="data_tb_alignright">其他<br>Others</td>
			<td class="data_tb_content"><input type="text" id="others" name="others" value="${info.others }" style="border-style:none;border:0;border-bottom:1 solid blue;"/></td>
			<td class="data_tb_alignright">年收入<br>Annual Package</td>
			<td class="data_tb_content"><input type="text" id="annualPackage" name="annualPackage" value="${info.annualPackage }" style="border-style:none;border:0;border-bottom:1 solid blue;"/></td>
		</tr>
		<tr>
			<td class="data_tb_alignright">
				可到职日期<br>Date Available for work
			</td>
			<td colspan="6" class="data_tb_content">
				<input type="text" disabled="disabled" readonly="readonly" id="availableWork1" name="availableWork1" value="${info.availableWork }" />
				<input type="hidden" id="availableWork" name="availableWork" value="${info.availableWork }"/>
			</td>
		</tr>
	</table >
	<br><br><br>
	<table id="educationTab" class="data_tb" align="center">
		<tr>
			<td colspan="7" class="data_tb_alignright">
				<h3 align="center">教育背景&nbsp;&nbsp;Education&nbsp;<a href="javascript:void(0)" onclick="addRow('education')">添加</a>&nbsp;&nbsp;<a href="javascript:void(0)" onclick="goAdEd('education')">保存</a></h3>
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright">学校种类<br>Type of school</td>
			<td class="data_tb_alignright">校名<br>Name of school/University</td>
			<td class="data_tb_alignright">开始时间<br>From</td>
			<td class="data_tb_alignright">结束时间<br>To</td>
			<td class="data_tb_alignright">专业<br>Major</td>
			<td class="data_tb_alignright">学位/证书<br>Degree/Diploma</td>
			<td class="data_tb_alignright">操作</td>
		</tr>
		<c:forEach var="education" items="${educationList}">
		<tr>
			<td class="data_tb_content"><input type="text" name="schoolType" value="${education.schoolType }" style="border-style:none;border:0;border-bottom:1 solid blue;"/></td>
			<td class="data_tb_content"><input type="text" name="schoolName" value="${education.schoolName }" style="border-style:none;border:0;border-bottom:1 solid blue;" size="40"/></td>
			<td class="data_tb_content"><input type="text" name="startTimeEducation" value="${education.startTime }" style="border-style:none;border:0;border-bottom:1 solid blue;"/></td>
			<td class="data_tb_content"><input type="text" name="endTimeEducation" value="${education.endTime }" style="border-style:none;border:0;border-bottom:1 solid blue;"/></td>
			<td class="data_tb_content"><input type="text" name="major" value="${education.major }" style="border-style:none;border:0;border-bottom:1 solid blue;"/></td>
			<td class="data_tb_content"><input type="text" name="degreeAndDiploma" value="${education.degreeAndDiploma }" style="border-style:none;border:0;border-bottom:1 solid blue;"/></td>
			<td class="data_tb_content">
				<a href="javascript:void(0)" onclick="educationTab.deleteRow(this.parentElement.parentElement.rowIndex);">删除</a>
			</td>
		</tr>
		</c:forEach>
		</table>
		<br><br><br>
		<table id="trainTab" class="data_tb"  align="center">
		<tr>
			<td colspan="7" class="data_tb_alignright">
				<h3 align="center">培训背景<br>TRAINING BACKGROUND&nbsp;<a href="javascript:void(0)" onclick="addRow('train')">添加</a>&nbsp;&nbsp;<a href="javascript:void(0)" onclick="goAdEd('train')">保存</a></h3>
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright">专业资格/培训课程<br>Professional<br> Qualification<br>/Training Courses
			</td>
			<td class="data_tb_alignright">培训机构<br>Training Vendor</td>
			<td class="data_tb_alignright">开始时间<br>From</td>
			<td class="data_tb_alignright">结束时间<br>To</td>
			<td class="data_tb_alignright">证书<br>Certificate</td>
			<td class="data_tb_alignright">备注<br>Remarks</td>
			<td class="data_tb_alignright">操作</td>
		</tr>
		<c:forEach var="train" items="${trainList}">
		<tr>
			<td class="data_tb_content"><input type="text" name="training" value="${train.training }" style="border-style:none;border:0;border-bottom:1 solid blue;"/></td>
			<td class="data_tb_content"><input type="text" name="trainingName" value="${train.trainingName }" style="border-style:none;border:0;border-bottom:1 solid blue;" size="40"/></td>
			<td class="data_tb_content"><input type="text" name="trainStartTime" value="${train.trainStartTime }" style="border-style:none;border:0;border-bottom:1 solid blue;"/></td>
			<td class="data_tb_content"><input type="text" name="trainEndTime" value="${train.trainEndTime }" style="border-style:none;border:0;border-bottom:1 solid blue;"/></td>
			<td class="data_tb_content"><input type="text" name="certificate" value="${train.certificate }" style="border-style:none;border:0;border-bottom:1 solid blue;"/></td>
			<td class="data_tb_content"><input type="text" name="remarks" value="${train.remarks }" style="border-style:none;border:0;border-bottom:1 solid blue;"/></td>
			<td class="data_tb_content">
				<a href="javascript:void(0)" onclick="trainTab.deleteRow(this.parentElement.parentElement.rowIndex);">删除</a>
			</td>
		</tr>
		</c:forEach>
	</table>
	<br><br><br>
	<table id="workHistoryTab" class="data_tb"  align="center">
		<tr>
			<td colspan="9" class="data_tb_alignright">
				<h3 align="center">工作经验 &nbsp;&nbsp; WORKING HISTORY&nbsp;&nbsp;<a href="javascript:void(0)" onclick="addRow('workHistory')">添加</a>&nbsp;&nbsp;<a href="javascript:void(0)" onclick="goAdEd('workHistory')">保存</a></h3>
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright">开始时间<br>From</td>
			<td class="data_tb_alignright">结束时间<br>To</td>
			<td class="data_tb_alignright">公司名称/任职部门<br>Employer/Department</td>
			<td class="data_tb_alignright">职位<br>Position	</td>
			<td class="data_tb_alignright">主要职责<br>Major Responsibilities</td>
			<td class="data_tb_alignright">直接主管职位<br>Direct supervisor</td>
			<td class="data_tb_alignright">薪资<br>Salary(Pre tax)
			</td>
			<td class="data_tb_alignright">离职原因<br>Leaving Reasons</td>
			<td class="data_tb_alignright">操作</td>
		</tr>
		<c:forEach var="workHistory" items="${workHistoryList}">
		<tr>
			<td class="data_tb_content"><input type="text" name="startTimeWork" value="${workHistory.startTime }" style="border-style:none;border:0;border-bottom:1 solid blue;"/></td>
			<td class="data_tb_content"><input type="text" name="endTimeWork" value="${workHistory.endTime }" style="border-style:none;border:0;border-bottom:1 solid blue;"/></td>
			<td class="data_tb_content"><input type="text" name="companyWork" value="${workHistory.company }" style="border-style:none;border:0;border-bottom:1 solid blue;" size="22"/></td>
			<td class="data_tb_content"><input type="text" name="positionWork" value="${workHistory.position }" style="border-style:none;border:0;border-bottom:1 solid blue;"/></td>
			<td class="data_tb_content"><input type="text" name="majorWork" value="${workHistory.major }" style="border-style:none;border:0;border-bottom:1 solid blue;" size="26"/></td>
			<td class="data_tb_content"><input type="text" name="supervisor" value="${workHistory.supervisor }" style="border-style:none;border:0;border-bottom:1 solid blue;"/></td>
			<td class="data_tb_content"><input type="text" name="salaryWork" value="${workHistory.salary }" style="border-style:none;border:0;border-bottom:1 solid blue;"/></td>
			<td class="data_tb_content"><input type="text" name="leaveReasons" value="${workHistory.leaveReasons }" style="border-style:none;border:0;border-bottom:1 solid blue;"/></td>
			<td class="data_tb_content">
				<a href="javascript:void(0)" onclick="workHistoryTab.deleteRow(this.parentElement.parentElement.rowIndex);">删除</a>
			</td>
		</tr>
		</c:forEach>
	</table>
	<br><br><br>
	<table id="referenceTab" class="data_tb"  align="center">
		<tr>
			<td colspan="6" class="data_tb_alignright">
				<h3 align="center">证明人 REFERENCE&nbsp;&nbsp;<a href="javascript:void(0)" onclick="addRow('reference')">添加</a>&nbsp;&nbsp;<a href="javascript:void(0)" onclick="goAdEd('reference')">保存</a></h3>
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright">姓名<br>Name</td>
			<td class="data_tb_alignright">工作单位<br>Name of Company</td>
			<td class="data_tb_alignright">关系<br>Relationship</td>
			<td class="data_tb_alignright">职位<br>Occupation</td>
			<td class="data_tb_alignright">联系电话<br>Contact Number</td>
			<td class="data_tb_alignright">操作</td>
		</tr>
		<c:forEach var="reference" items="${referenceList}">
		<tr>
			<td class="data_tb_content"><input type="text" id="nameReference" name="nameReference" value="${reference.name}" style="border-style:none;border:0;border-bottom:1 solid blue;"/></td>
			<td class="data_tb_content"><input type="text" id="companyReference" name="companyReference" value="${reference.company}" style="border-style:none;border:0;border-bottom:1 solid blue;" size="40"/></td>
			<td class="data_tb_content"><input type="text" id="relationshipReference" name="relationshipReference" value="${reference.relationship}" style="border-style:none;border:0;border-bottom:1 solid blue;"/></td>
			<td class="data_tb_content"><input type="text" id="occupationReference" name="occupationReference" value="${reference.occupation}" style="border-style:none;border:0;border-bottom:1 solid blue;"/></td>
			<td class="data_tb_content"><input type="text" id="telReference" name="telReference" value="${reference.tel}" style="border-style:none;border:0;border-bottom:1 solid blue;"/></td>
			<td class="data_tb_content">
				<a href="javascript:void(0)" onclick="referenceTab.deleteRow(this.parentElement.parentElement.rowIndex);">删除</a>
			</td>
		</tr>
		</c:forEach>
	</table>
	<br><br><br>
	<table class="data_tb"  align="center">
		<tr>
			<td colspan="5" class="data_tb_alignright">
				<h3 align="center">技能 SKILLS&nbsp;&nbsp;<a href="javascript:void(0)" onclick="goAdEd('skills')">保存</a></h3>
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright">语言能力<br>Language Skills</td>
			<td class="data_tb_alignright">母语<br>Mother Tongue</td>
			<td class="data_tb_content"><input id="motherTongue" name="motherTongue" value="${skills.motherTongue }" type="text" size="30" style="border-style:none;border:0;border-bottom:1 solid blue;"/></td>
			<td class="data_tb_alignright">外语种类及水平<br>Foreign language & Level</td>
			<td class="data_tb_content"><input id="foreignLanguage" name="foreignLanguage" value="${skills.foreignLanguage }" type="text" size="30" style="border-style:none;border:0;border-bottom:1 solid blue;"/></td>
		</tr>
		<tr>
			<td class="data_tb_alignright">电脑技能<br>Computer Skills</td>
			<td colspan="4" class="data_tb_content"><input id="computerSkills" name="computerSkills" value="${skills.computerSkills }" size="130" style="border-style:none;border:0;border-bottom:1 solid blue;"/></td>
		</tr>
		<tr>
			<td class="data_tb_alignright">其他特长和爱好<br>Special Skill and Hobby</td>
			<td colspan="4" class="data_tb_content"><input id="special" name="special" value="${skills.special }" size="130" style="border-style:none;border:0;border-bottom:1 solid blue;"/></td>
		</tr>
	</table>
	<br><br><br>
	<table id="familyInfoTab" class="data_tb"  align="center">
		<tr>
			<td colspan="8" class="data_tb_alignright">
				<h3 align="center">家庭成员/紧急情况联系人 <br>FAMILY INFORMATION/EMERGENCY CONTACT PERSON&nbsp;&nbsp;<a href="javascript:void(0)" onclick="addRow('family')">添加</a>&nbsp;&nbsp;<a href="javascript:void(0)" onclick="goAdEd('family')">保存</a></h3>
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright">姓名<br>Name</td>
			<td class="data_tb_alignright">年龄<br>Age</td>
			<td class="data_tb_alignright">与本人关系<br>Relationship</td>
			<td class="data_tb_alignright">职业<br>Occupation</td>
			<td class="data_tb_alignright">工作单位<br>Name of Company</td>
			<td class="data_tb_alignright">地址<br>Address</td>
			<td class="data_tb_alignright">电话<br>Telephone</td>
			<td class="data_tb_alignright">操作</td>
		</tr>
		<c:forEach var="family" items="${familyInfo}">
		<tr>
			<td class="data_tb_content"><input type="text" name="nameFamily" value="${family.name }" style="border-style:none;border:0;border-bottom:1 solid blue;" size="10"/></td>
			<td class="data_tb_content"><input type="text" name="ageFamily" value="${family.age }" style="border-style:none;border:0;border-bottom:1 solid blue;" size="5"/></td>
			<td class="data_tb_content"><input type="text" name="relationshipFamily" value="${family.relationship }" style="border-style:none;border:0;border-bottom:1 solid blue;width: 80px;"/></td>
			<td class="data_tb_content"><input type="text" name="occupationFamily" value="${family.occupation }" style="border-style:none;border:0;border-bottom:1 solid blue;width: 80px;"/></td>
			<td class="data_tb_content"><input type="text" name="companyFamily" value="${family.company }" style="border-style:none;border:0;border-bottom:1 solid blue;" size="40"/></td>
			<td class="data_tb_content"><input type="text" name="addressFamily" value="${family.address }" style="border-style:none;border:0;border-bottom:1 solid blue;" size="40"/></td>
			<td class="data_tb_content"><input type="text" name="telFamily" value="${family.tel }" style="border-style:none;border:0;border-bottom:1 solid blue;"/>
			</td>
			<td class="data_tb_content">
				<a href="javascript:void(0)" onclick="familyInfoTab.deleteRow(this.parentElement.parentElement.rowIndex);">删除</a>
			</td>
		</tr>
		</c:forEach>
	</table>
	<br><br><br>
	<table class="data_tb"  align="center">
		<tr>
			<td class="data_tb_alignright">
				<h3 align="center"> 问答&nbsp;&nbsp; QUESTIONS&nbsp;&nbsp;<a href="javascript:void(0)" onclick="goAdEd('questions')">保存</a></h3>
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright">主要工作业绩及获得荣誉<br>Major achievements and Honors obtained</td>
		</tr>
		<tr>
			<td class="data_tb_content"><textarea id="honorsObtained" value="${questions.honorsObtained }" name="honorsObtained" cols="100" rows="5" style="width: 100%"></textarea></td>
		</tr>
		<tr>
			<td class="data_tb_alignright">性格特征、特长爱好及自我评价<br>Personality，Hobbies and self-evaluation</td>
		</tr>
		<tr>
			<td class="data_tb_content"><textarea id="personality" value="${questions.personality }" name="personality" cols="100" rows="5" style="width: 100%"></textarea></td>
		</tr>
	</table>
	<br><br>
	<input type="hidden" id="linkUserId" name="linkUserId" value="${linkUserId }"/>
	<input type="hidden" id="uploadFileName" name="uploadFileName" value="${fn}">
	<input type="hidden" id="fileRondomName" name="fileRondomName" value="${fileTempName}">
	<input type="hidden" id="fn" name="fn" value="${fn}">
</form>

<iframe name="hidden_frame" id="hidden_frame" style='display:none'></iframe>
	
<iframe name="hidden_frame_img" id="hidden_frame_img" style='display:none'></iframe>
	
<form name="myForm" id="myForm" action="employment.do?method=uploadPhoto" method="post" enctype="multipart/form-data" target="hidden_frame_img">
	<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>
  	<input type="hidden" id="fileTempName" name="fileTempName" value="${fileTempName}">
</form>
</div>

<div id="search" style="display:none">
<br/>
<div style="margin:0 20 0 20">图片上传</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
		<tr align="center">
			<td align="right">上传附件：</td>
			<td align=left>
				<input type="file" id="Attachments" size="50" name="Attachments"/>
			</td>
		</tr>
	</table>
</div>
</body>
<script type="text/javascript">
new Validation('thisForm'); 

new Ext.form.DateField({			
	applyTo : 'availableWork1',
	width: 150,
	format: 'Y-m-d'	
});

function addRow(obj){
	if(obj=="education"){
		var educationTab=document.getElementById("educationTab");
		var objTr=educationTab.insertRow(-1);
		var objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='schoolType' style='border-style:none;border:0;border-bottom:1 solid blue;'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='schoolName' style='border-style:none;border:0;border-bottom:1 solid blue;' class='required' title='请输入'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='startTimeEducation' style='border-style:none;border:0;border-bottom:1 solid blue;'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='endTimeEducation' style='border-style:none;border:0;border-bottom:1 solid blue;'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='major' style='border-style:none;border:0;border-bottom:1 solid blue;'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='degreeAndDiploma' style='border-style:none;border:0;border-bottom:1 solid blue;'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<a href=\"javascript:void(0)\" onclick=\"educationTab.deleteRow(this.parentElement.parentElement.rowIndex);\">删除</a>";
	}
	if(obj=="train"){
		var trainTab=document.getElementById("trainTab");
		var objTr=trainTab.insertRow(-1);
		var objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='training' style='border-style:none;border:0;border-bottom:1 solid blue;'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='trainingName' style='border-style:none;border:0;border-bottom:1 solid blue;' class='required' title='请输入'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='trainStartTime' style='border-style:none;border:0;border-bottom:1 solid blue;'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='trainEndTime' style='border-style:none;border:0;border-bottom:1 solid blue;'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='certificate' style='border-style:none;border:0;border-bottom:1 solid blue;'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='remarks' style='border-style:none;border:0;border-bottom:1 solid blue;'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<a href=\"javascript:void(0)\" onclick=\"trainTab.deleteRow(this.parentElement.parentElement.rowIndex);\">删除</a>";
	}
	if(obj=="family"){
		var familyTab=document.getElementById("familyInfoTab");
		var objTr=familyTab.insertRow(-1);
		var objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='nameFamily' style='border-style:none;border:0;border-bottom:1 solid blue;' size='10' class='required' title='请输入'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='ageFamily' style='border-style:none;border:0;border-bottom:1 solid blue;' size='5'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='relationshipFamily' style='border-style:none;border:0;border-bottom:1 solid blue;' class='required' title='请输入'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='occupationFamily' style='border-style:none;border:0;border-bottom:1 solid blue;'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='companyFamily' style='border-style:none;border:0;border-bottom:1 solid blue;' size='40'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='addressFamily' style='border-style:none;border:0;border-bottom:1 solid blue;' size='40'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='telFamily' style='border-style:none;border:0;border-bottom:1 solid blue;'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<a href=\"javascript:void(0)\" onclick=\"familyInfoTab.deleteRow(this.parentElement.parentElement.rowIndex);\">删除</a>";
	}
	if(obj=="reference"){
		var referenceTab=document.getElementById("referenceTab");
		var objTr=referenceTab.insertRow(-1);
		var objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='nameReference' style='border-style:none;border:0;border-bottom:1 solid blue;' class='required' title='请输入'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='companyReference' style='border-style:none;border:0;border-bottom:1 solid blue;'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='relationshipReference' style='border-style:none;border:0;border-bottom:1 solid blue;'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='occupationReference' style='border-style:none;border:0;border-bottom:1 solid blue;'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='telReference' class='required' style='border-style:none;border:0;border-bottom:1 solid blue;'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<a href=\"javascript:void(0)\" onclick=\"referenceTab.deleteRow(this.parentElement.parentElement.rowIndex);\">删除</a>";
	}
	if(obj=="workHistory"){
		var workHistoryTab=document.getElementById("workHistoryTab");
		var objTr=workHistoryTab.insertRow(-1);
		var objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='startTimeWork' style='border-style:none;border:0;border-bottom:1 solid blue;'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='endTimeWork' style='border-style:none;border:0;border-bottom:1 solid blue;'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='companyWork' style='border-style:none;border:0;border-bottom:1 solid blue;' class='required' title='请输入'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='positionWork' style='border-style:none;border:0;border-bottom:1 solid blue;' class='required' title='请输入'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='majorWork' style='border-style:none;border:0;border-bottom:1 solid blue;'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='supervisor' style='border-style:none;border:0;border-bottom:1 solid blue;'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='salaryWork' style='border-style:none;border:0;border-bottom:1 solid blue;'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<input type='text' name='leaveReasons' style='border-style:none;border:0;border-bottom:1 solid blue;'/>";
		objTd=objTr.insertCell(-1);
		objTd.className="data_tb_content";
		objTd.innerHTML="<a href=\"javascript:void(0)\" onclick=\"workHistoryTab.deleteRow(this.parentElement.parentElement.rowIndex);\">删除</a>";
	}
}

function goAdEd(obj){
	if (!formSubmitCheck('thisForm')) return ;
	if(obj=="education"){
		document.getElementById("thisForm").action="employment.do?method=saveEducation";
	}
	if(obj=="info"){
		var first = document.getElementById("position").value;
		var second = document.getElementById("secondChoice").value;
		var rankLevel = ["高级经理1级","高级经理2级","高级项目经理1级","高级项目经理2级","经理1级","经理2级"];
		var certificate = document.getElementsByName("certificate").value;
		for(var i=0;i<rankLevel.length;i++){
			if(first == rankLevel[i] || second == rankLevel[i]){
				if(certificate == null || certificate == ""){
					alert("选择了高级经理1级,高级经理2级,高级项目经理1级,高级项目经理2级,经理1级,经理2级职位的,必要填写培训背景,填写注册会计师号");
					return;
				}
			}
		}
		var candidateSource = document.getElementsByName("candidateSource").value;
		if(candidateSource == "internal"){
			var recommend = document.getElementsByName("recommend").value;
			if(recommend == null || recommend == ''){
				alert("请填写推荐人");
				return;
			}
		}
		document.getElementById("thisForm").action="employment.do?method=saveInfo";
	}
	if(obj=="personal"){
		document.getElementById("thisForm").action="employment.do?method=savePersonal";
	}
	if(obj=="workHistory"){
		document.getElementById("thisForm").action="employment.do?method=saveWorkHistory";
	}
	if(obj=="reference"){
		document.getElementById("thisForm").action="employment.do?method=saveReference";
	}
	if(obj=="skills"){
		document.getElementById("thisForm").action="employment.do?method=saveSkills";
	}
	if(obj=="family"){
		document.getElementById("thisForm").action="employment.do?method=saveFamilyInfo";
	}
	if(obj=="questions"){
		document.getElementById("thisForm").action="employment.do?method=saveQuestions";
	}
	if(obj=="train"){
		document.getElementById("thisForm").action="employment.do?method=saveTrain";
	}
	document.getElementById("thisForm").submit();
}

var queryWin = null;
function queryWinFun(id){
	if(!queryWin) { 
		//new BlockDiv().show();
		var searchDiv = document.getElementById("search") ;
	    queryWin = new Ext.Window({
			title: '图片上传',
	     	renderTo : searchWin,
	     	width: 580,
	     	height:160,
        	closeAction:'hide',
       	    listeners : {
	         	'hide':{
	         		fn: function () {
	         			new BlockDiv().hidden();
						queryWin.hide();
					}
				}
	        },
        	layout:'fit',
	    	html:searchDiv.innerHTML,
	    	buttons:[{
            	text:'确定',
          		//handler:goSearch_key
          		handler:function(){
               		upLoadSumbit();
               		queryWin.hide();
            	}
        	},{
            	text:'取消',
            	handler:function(){
               		queryWin.hide();
            	}
        	}]
	    });
    }
    queryWin.show();
}

function upLoadSumbit() {

	var Attachments = document.getElementById("Attachments");

	document.getElementById("uploadFileName").value = Attachments.value ;
	var myForm = document.getElementById("myForm");
	myForm.action = "employment.do?method=uploadPhoto" ;
	//myForm.target="hidden_frame"
	myForm.submit();
	removeDiv("upLoadDiv") ;
}

function removeDiv(id) {
	queryWin.hide();
	//document.getElementById(id).style.display = "none" ;
}

function changePhoto(fileTempName,fileName) {
	var Attachments = document.getElementById("Attachments");
	//var uploadFileName = document.getElementById("uploadFileName");
	//alert(uploadFileName.value);
	if(fileTempName != "") {
		document.getElementById("fileRondomName").value = fileTempName; 
		document.getElementById("uploadFileName").value = fileName;
		document.getElementById("bill").src = "${pageContext.request.contextPath}/userPhoto/"+fileTempName;
	}
}

function deletePhto() {

	var Attachments = document.getElementById("Attachments");
	var showFileName = "${fileTempName}" ;
	var fileName = document.getElementById("uploadFileName").value ;
	var deleteName = "" ;

	if(fileName == "") {
		alert("您没有上传任何照片!");
		return ;
	}else {
		 deleteName = showFileName+getFileName(fileName).substr(getFileName(fileName).indexOf("."),fileName.length);
	}

		var opt = "${UserOpt}" ;
		var resText = ""

		if(opt == "2" || opt == "3") {

			var fn = document.getElementById("fn").value ;

			if(fn == "") {
				if(deleteName == ""){
					alert("您没有上传任何照片!");
					return ;
				}else{
					resText = "suc";
				}
				
			}else{
				var oBao = new ActiveXObject("Microsoft.XMLHTTP");
				var url="employment.do?method=deleteUpdatePhoto&deleteName="+deleteName+"&id=${info.id}" ;
				oBao.open("POST",url,false);
				oBao.send();
				resText = oBao.responseText ;
			}
			
		}else {
			var oBao = new ActiveXObject("Microsoft.XMLHTTP");
			var url="employment.do?method=deletePhoto&deleteName="+deleteName ;
			oBao.open("POST",url,false);
			oBao.send();
			resText = oBao.responseText ;
		}

		if(resText == "suc") {
			document.getElementById("Attachments").value = "";
			document.getElementById("uploadFileName").value = "" ;
			document.getElementById("bill").src = "${pageContext.request.contextPath}/images/noPhoto.gif" ;
			alert("文件删除成功!") ;
		}else if(resText == "fail"){
			alert("文件删除失败!") ;
		}else {
			alert("您要删除的文件不存在,请联管理员!");
		}
}

function getFileName(filepath) {
	var returnstr = filepath;
	var length = filepath.trim().length;

	if (length > 0) {
		var i = filepath.lastIndexOf("\\");
		if (i >= 0) {
			filepath = filepath.substring(i + 1);
			returnstr = filepath;
		}
	}
	return returnstr;
}

function finish(){
	//window.location="employment.do?method=list";
	var nameFamily = document.getElementsByName("nameFamily");
		if(nameFamily==null || nameFamily==""){
			alert("必须填写一条家庭成员信息");
			return;
		}
	if(confirm("点击完成按钮，不会保存您填写的信息，请填写完表里的信息后，点击对应表的保存按钮，才会保存您填写的信息，如果你确定保存了，请点击确认按钮")){
		window.close();
	}
}
</script>
</html>