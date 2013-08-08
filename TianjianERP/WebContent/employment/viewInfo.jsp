<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>应聘信息</title>
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
				<h3 align="center">应聘信息<br> EMPLOYMENT INFORMATION</h3>
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
				<img alt="" src="${pageContext.request.contextPath}${userPhotoSrc}" height="120" width="100" id="photo" onerror="this.src='${pageContext.request.contextPath}/images/noPhoto.gif'">
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
				<h3 align="center">教育背景&nbsp;&nbsp;Education</h3>
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
			</td>
		</tr>
		</c:forEach>
		</table>
		<br><br><br>
		<table id="trainTab" class="data_tb"  align="center">
		<tr>
			<td colspan="7" class="data_tb_alignright">
				<h3 align="center">培训背景<br>TRAINING BACKGROUND</h3>
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
			</td>
		</tr>
		</c:forEach>
	</table>
	<br><br><br>
	<table id="workHistoryTab" class="data_tb"  align="center">
		<tr>
			<td colspan="9" class="data_tb_alignright">
				<h3 align="center">工作经验 &nbsp;&nbsp; WORKING HISTORY</h3>
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
			</td>
		</tr>
		</c:forEach>
	</table>
	<br><br><br>
	<table id="referenceTab" class="data_tb"  align="center">
		<tr>
			<td colspan="6" class="data_tb_alignright">
				<h3 align="center">证明人 REFERENCE</h3>
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
			</td>
		</tr>
		</c:forEach>
	</table>
	<br><br><br>
	<table class="data_tb"  align="center">
		<tr>
			<td colspan="5" class="data_tb_alignright">
				<h3 align="center">技能 SKILLS</h3>
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
				<h3 align="center">家庭成员/紧急情况联系人 <br>FAMILY INFORMATION/EMERGENCY CONTACT PERSON</h3>
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
			</td>
		</tr>
		</c:forEach>
	</table>
	<br><br><br>
	<table class="data_tb"  align="center">
		<tr>
			<td class="data_tb_alignright">
				<h3 align="center"> 问答&nbsp;&nbsp; QUESTIONS</h3>
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
</form>
</div>
</body>
</html>