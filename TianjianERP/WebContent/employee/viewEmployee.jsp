<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ taglib uri="http://ckeditor.com" prefix="ckeditor" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/ckeditor/ckeditor.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/ckfinder/ckfinder.js"></script>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>员工登记</title>

<style type="text/css">

  .formTable input{
    width: 90%;
  }

</style>

<script src="<%=request.getContextPath()%>/AS_INCLUDE/images/editor.js" charset=GBK></script>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/images/DhtmlEdit.js" charset=GBK></script>

<style>
	#t {
		{border-collapse:collapse;border:none;};
	}
	
	#t td{
		{border:solid #6595d6 1px;};
	}
	
	
</style>
<script Language=JavaScript>

	function ext_init(){
	
		mt_form_initDateSelect();
		mt_form_initAttachFile();
        attachImageInit("photo","imgPhoto");		
    }

    window.attachEvent('onload',ext_init);
</script>


</head>
<body leftmargin="0" topmargin="0">
<div id="divBtn"></div>


<table border="1" class="formTable" style="width: 93%">
	<thead>
		<tr>
			<th colspan="8" style="text-align: center">
				员工登记表</th>
		</tr>
	</thead>
	<tbody>

		
		<tr>
			<th>姓名</th>
			<td>
          		<input id="name" name="name" type="text"  value="${employee.name}"/></td>
			<th>性别</th>
			<td>
			<select id="sex" name="sex" >
			
							<option value="男"
							 <c:if test="${employee.sex eq 男 }">selected</c:if>>男</option>
							<option value="女"
							<c:if test="${employee.sex eq 女 }">selected</c:if>>女</option>
			</select>
				
			<th>民族</th>
			<td>
				<input id="nation" name="nation" type="text"  value="${employee.nation}" />
			</td>
			<td colspan="2" rowspan="7" style="text-align: center;">
		       <img id="imgPhoto" style="width: 108px;height: 150px"/><br/>
		      <input id="photo" type="hidden" name="employeeId" value=""  />			
		       
			</td>
		</tr>
		
		<tr>
			<th>
				婚姻状况（已婚/未婚）</th>
			<td>
			<select id="marriage" name="marriage">

							<option value="已婚" 
							<c:if test="${employee.marriage eq  已婚}">selected</c:if>>已婚</option>
							<option value="未婚" 
							<c:if test="${employee.marriage eq  未婚}">selected</c:if>>未婚</option>
			</select>
			</td>
			<th>身份证号</th>
			<td >
				<input id="idcard" name="idcard" type="text"  value="${employee.idcard }" />
			</td>
				<th>出生日期</th>
			<td>
				<input ext_type=date id="brithday" name="brithday"  value="${employee.brithday}" style="width: 500px;" />
			</td>
		</tr>
		
		<tr>
			<th>出生地</th>
			<td>
				<input id="brith_address" name="brith_address"  type="text"  value="${employee.brith_address}" />
			</td>
			<th>籍贯</th>
			<td>
				<input id="natives" name="natives"  type="text"  value="${employee.natives }" />
			</td>
			<th>父母居住地</th>
			<td >
				<input id="parents_address" name="parents_address" type="text"  value="${employee.parents_address}"/>
			</td>
		</tr>
		
		<tr>
			<th>
				学历</th>
			<td>
				<input id="grad_formal" name="grad_formal"  type="text"  value="${employee.grad_formal}" /></td>
			<th>
				学位</th>
			<td>
				<input id="grad_degrees" name="grad_degrees"  type="text"  value="${employee.grad_degrees}" /></td>
			<th>
				是否双学位</th>
			<td>
				<select id="t_grad_degrees" name="t_grad_degrees">
				   <option value="否"
					 <c:if test="${employee.t_grad_degrees eq 否 }">selected</c:if>>否</option>
				     
					<option value="是" 
						<c:if test="${employee.t_grad_degrees eq 是 }">selected</c:if>>是</option>
				</select>
				</td>

		</tr>
		<tr>
			<th>
				英语等级</th>
			<td>
				<input id="cet_grade" name="cet_grade" type="text"  value="${employee.cet_grade}" /></td>
			<th>
				第二外语及等级</th>
			<td>
				<input id="two_foreign_language" name="two_foreign_language" type="text" value="${employee.two_foreign_language}/${employee.cet_grade}"/></td>

			<th>
				第二学位</th>
			<td>
			
				<input id="two_grad_degrees" name="two_grad_degrees" type="text"  value="${employee.two_grad_degrees }"/></td>				

		</tr>
		<tr>
			<th>
				党/团员</th>
			<td>
				<select id="politics" name="politics">
				
					<option value="团员"
					<c:if test="${employee.politics eq 团员}">selected</c:if>>团员</option>
					<option value="党员"
					<c:if test="${employee.politics eq 党员}">selected</c:if>>党员</option>
				</select>
				
			<th>
				入党/团时间</th>
			<td>
				<input ext_type=date id="politics_time" name="politics_time"  type="text" value="${employee.politics_time}"/></td>
			<th>
				户口所属地</th>
			<td>
				<input id="h_k_address" name="h_k_address" type="text" value="${employee.h_k_address}" /></td>

		</tr>
		
		<tr>

			<th>
				户口是否迁移本所</th>
			<td>
				<select id="h_k_move" name="h_k_move">
					<option value="否"
					<c:if test="${employee.h_k_address eq 否}">selected</c:if>>否</option>
					<option value="是"
					<c:if test="${employee.h_k_address eq 是}">selected</c:if>>是</option>
				</select>
		</td>
			<th>
				技术职称</th>
			<td>
				<input id="academic_title" name="academic_title" type="text"  value="${employee.academic_title}" /></td>		
			<th>
				取得职称时间</th>
			<td>
				<input ext_type=date id="academic_title_time" name="academic_title_time" type="text"  value="${employee.academic_title_time}" /></td>		
				
		</tr>
		
		<tr>
			<th>
				现在地址</th>
			<td colspan="3">
				<input id="now_address" name="now_address" type="text"  value="${employee.now_address}"  /></td>
			<th>
				手机</th>
			<td>
				<input id="conn_mobile" name="conn_mobile" type="text"  value="${employee.conn_mobile}" /></td>
			<th>
				应急联系人电话</th>
			<td>
				<input id="conn_phone" name="conn_phone" type="text"  value="${employee.conn_phone}" /></td>
		</tr>
		<tr>
			<th rowspan="6">
				参加执业资格考试及<br />
				取得执业资格情况<br />
				&nbsp;</th>
			<th colspan="2" style="text-align: left">
				参加执业资格考试科目</th>
			<th style="text-align: left">
				考试时间</th>
			<th colspan="2" style="text-align: left">
				取得执业资格名称</th>
			<th colspan="2" style="text-align: left">
				注册时间</th>
		</tr>
		<tr>
			<td colspan="2">
				<input id="exam_course1" name="exam_course1" type="text"  value="${employee.exam_course1}" /></td>
			<td>
				<input id="exam_time1" name="exam_time1"  ext_type=date type="text"  value="${employee.exam_time1}" /></td>
			<td colspan="2">
				<input id="acquire_academic_title1" name="acquire_academic_title1" type="text"  value="${employee.acquire_academic_title1}" /></td>
			<td colspan="2">
				<input id="register_time1" name="register_time1"   ext_type= date type="text"  value="${employee.register_time1}" /></td>
		</tr>
		<tr>
			<td colspan="2">
				<input id="exam_course2" name="exam_course2" type="text"  value="${employee.exam_course2}" /></td>
			<td>
				<input id="exam_time2" name="exam_time2"  ext_type=date type="text"  value="${employee.exam_time2}" /></td>
			<td colspan="2">
				<input id="acquire_academic_title2" name="acquire_academic_title2" type="text"  value="${employee.acquire_academic_title2}" /></td>
			<td colspan="2">
				<input id="register_time2" name="register_time2"  ext_type= date type="text"  value="${employee.register_time2}" /></td>
		</tr>
		<tr>
			<td colspan="2">
				<input id="exam_course3" name="exam_course3" type="text"  value="${employee.exam_course3}" /></td>
			<td>
				<input id="exam_time3" name="exam_time3"  ext_type=date type="text"  value="${employee.exam_time3}" /></td>
			<td colspan="2">
				<input id="acquire_academic_title3" name="acquire_academic_title3" type="text"  value="${employee.acquire_academic_title3}" /></td>
			<td colspan="2">
				<input id="register_time3" name="register_time3"  ext_type= date type="text"  value="${employee.register_time3}" /></td>
		</tr>
		<tr>
			<td colspan="2">
				<input id="exam_course4" name="exam_course4" type="text"  value="${employee.exam_course4}" /></td>
			<td>
				<input id="exam_time4" name="exam_time4"  ext_type=date type="text"  value="${employee.exam_time4}" /></td>
			<td colspan="2">
				<input id="acquire_academic_title4" name="acquire_academic_title4" type="text"  value="${employee.acquire_academic_title4}" /></td>
			<td colspan="2">
				<input id="register_time4" name="register_time4"  ext_type= date type="text"  value="${employee.register_time4}" /></td>
		</tr>
		<tr>
			<td colspan="2">
				<input id="exam_course5" name="exam_course5" type="text"  value="${employee.exam_course5}" /></td>
			<td>
				<input id="exam_time5" name="exam_time5"  ext_type=date type="text"  value="${employee.exam_time5}" /></td>
			<td colspan="2">
				<input id="acquire_academic_title5" name="acquire_academic_title5" type="text"  value="${employee.acquire_academic_title5}" /></td>
			<td colspan="2">
				<input id="register_time5" name="register_time5"  ext_type= date type="text"  value="${employee.register_time5}" /></td>
		</tr>
		<tr>
			<th rowspan="5">
				学习经历（从初中入学起）<br />
				&nbsp;</th>
			<th style="text-align: left">
				开始时间</th>
			<th style="text-align: left">
				结束时间</th>
			<th colspan="3" style="text-align: left">
				毕业学校</th>
			<th colspan="2" style="text-align: left">
				专业</th>
		</tr>
		<tr>
			<td>
				<input id="study_start_time1" name="study_start_time1"  ext_type=date type="text"  value="${employee.study_start_time1}" /></td>
			<td>
				<input id="study_end_time1" name="study_end_time1"  ext_type=date type="text"  value="${employee.study_end_time1}"  /></td>
			<td colspan="3">
				<input id="grad_school1" name="grad_school1" type="text" value="${employee.grad_school1}" /></td>
			<td colspan="2">
				<input id="grad_major1" name="grad_major1" type="text"  value="${employee.grad_major1}" /></td>
		</tr>
		<tr>
			<td>
				<input id="study_start_time2" name="study_start_time2"   ext_type=date type="text"  value="${employee.study_start_time2}" /></td>
			<td>
				<input id="study_end_time2" name="study_end_time2"  ext_type=date type="text"  value="${employee.study_end_time2}" /></td>
			<td colspan="3">
				<input id="grad_school2" name="grad_school2" type="text"  value="${employee.grad_school2}" /></td>
			<td colspan="2">
				<input id="grad_major2" name="grad_major2" type="text"  value="${employee.grad_major2}" /></td>
		</tr>
		<tr>
			<td>
				<input id="study_start_time3" name="study_start_time3"   ext_type=date type="text"  value="${employee.study_start_time3}" /></td>
			<td>
				<input id="study_end_time3" name="study_end_time3"  ext_type=date type="text"  value="${employee.study_end_time3}" /></td>
			<td colspan="3">
				<input id="grad_school3" name="grad_school3" type="text"  value="${employee.grad_school3}" /></td>
			<td colspan="2">
				<input id="grad_major3" name="grad_major3" type="text"  value="${employee.grad_major3}" /></td>
		</tr>
		<tr>
			<td>
				<input id="study_start_time4" name="study_start_time4"   ext_type=date type="text"  value="${employee.study_start_time4}" /></td>
			<td>
				<input id="study_end_time4" name="study_end_time4"  ext_type=date type="text"  value="${employee.study_end_time4}" /></td>
			<td colspan="3">
				<input id="grad_school4" name="grad_school4" type="text"  value="${employee.grad_school4}" /></td>
			<td colspan="2">
				<input id="grad_major4" name="grad_major4" type="text"  value="${employee.grad_major4}" /></td>
		</tr>
		<tr>
			<th rowspan="5">
				工作经历(本单位跟<br />
				填在最后，终止日期不填)<br />
			</th>
			<th style="text-align: left">
				开始时间</th>
			<th style="text-align: left">
				结束时间</th>
			<th colspan="3" style="text-align: left">
				所在单位及部门</th>
			<th colspan="2" style="text-align: left">
				职务</th>
		</tr>
		<tr>
			<td>
				<input id="work_strart_time1" name="work_strart_time1"  ext_type=date type="text"  value="${employee.work_strart_time1}" /></td>
			<td>
				<input id="work_end_time1" name="work_end_time1"  ext_type=date type="text"  value="${employee.work_end_time1}" /></td>
			<td colspan="3">
				<input id="work_unit1" name="work_unit1" type="text"  value="${employee.work_unit1}" /></td>
			<td colspan="2">
				<input id="work_job1" name="work_job1" type="text"  value="${employee.work_job1}"  /></td>
		</tr>
		<tr>
			<td>
				<input id="work_strart_time2" name="work_strart_time2"  ext_type=date type="text"  value="${employee.work_strart_time2 }" /></td>
			<td>
				<input id="work_end_time2" name="work_end_time2"  ext_type=date type="text" value="${employee.work_end_time2}" /></td>
			<td colspan="3">
				<input id="work_unit2" name="work_unit2" type="text"  value="${employee.work_unit2}" /></td>
			<td colspan="2">
				<input id="work_job2" name="work_job2" type="text"  value="${employee.work_job2}" /></td>
		</tr>
		<tr>
			<td>
				<input id="work_strart_time3" name="work_strart_time3"  ext_type=date type="text"  value="${employee.work_strart_time3}" /></td>
			<td>
				<input id="work_end_time3" name="work_end_time3"  ext_type=date type="text"  value="${employee.work_end_time3}" /></td>
			<td colspan="3">
				<input id="work_unit3" name="work_unit3" type="text"  value="${employee.work_unit3}" /></td>
			<td colspan="2">
				<input id="work_job3" name="work_job3" type="text"  value="${employee.work_job3}" /></td>
		</tr>
		<tr>
			<td>
				<input id="work_strart_time4" name="work_strart_time4"  ext_type=date type="text"  value="${employee.work_strart_time4}" /></td>
			<td>
				<input id="work_end_time4" name="work_end_time4"  ext_type=date type="text"  value="${employee.work_end_time4}" /></td>
			<td colspan="3">
				<input id="work_unit4" name="work_unit4" type="text"  value="${employee.work_unit4}" /></td>
			<td colspan="2">
				<input id="work_job4" name="work_job4" type="text"  value="${employee.work_job4}" /></td>
		</tr>
		<tr>
			<th rowspan="4">
				家庭成员<br />
				&nbsp;</th>
			<th style="text-align: left">
				姓名</th>
			<th style="text-align: left">
				与本人关系</th>
			<th colspan="3" style="text-align: left">
				现工作单位、部门</th>
			<th style="text-align: left">
				现任职务</th>
			<th style="text-align: left">
				联系电话</th>
		</tr>
		<tr>
			<td>
				<input id="family_name1" name="family_name1" type="text"  value="${employee.family_name1}" /></td>
			<td>
				<input id="relation1" name="relation1" type="text"  value="${employee.relation1}" /></td>
			<td colspan="3">
				<input id="f_work_until1" name="f_work_until1" type="text"  value="${employee.f_work_until1}" /></td>
			<td>
				<input id="f_work_job1" name="f_work_job1" type="text"  value="${employee.f_work_job1}" /></td>
			<td>
				<input id="f_phone1" name="f_phone1" type="text"  value="${employee.f_phone1}" /></td>
		</tr>
		<tr>
			<td>
				<input id="family_name2" name="family_name2" type="text"  value="${employee.family_name2}" /></td>
			<td>
				<input id="relation2" name="relation2" type="text"  value="${employee.relation2}" /></td>
			<td colspan="3">
				<input id="f_work_until2" name="f_work_until2" type="text"  value="${employee.f_work_until2}" /></td>
			<td>
				<input id="f_work_job2" name="f_work_job2" type="text"  value="${employee.f_work_job2}" /></td>
			<td>
				<input id="f_phone2" name="f_phone2" type="text"  value="${employee.f_phone2}" /></td>
		</tr>
		<tr>
			<td>
				<input id="family_name3" name="family_name3" type="text"  value="${employee.family_name3}" /></td>
			<td>
				<input id="relation3" name="relation3" type="text"  value="${employee.relation3}" /></td>
			<td colspan="3">
				<input id="f_work_until3" name="f_work_until3" type="text"  value="${employee.f_work_until3}" /></td>
			<td>
				<input id="f_work_job3" name="f_work_job3" type="text"  value="${employee.f_work_job3}" /></td>
			<td>
				<input id="f_phone3" name="f_phone3" type="text"  value="${employee.f_phone3}" /></td>
		</tr>
		<tr>
			<th rowspan="3">
				主要社会关系<br />
				&nbsp;</th>
			<th style="text-align: left">
				姓名</th>
			<th style="text-align: left">
				与本人关系</th>
			<th colspan="3" style="text-align: left">
				现工作单位、部门</th>
			<th style="text-align: left">
				现任职务</th>
			<th style="text-align: left">
				联系电话</th>
		</tr>
		<tr>
			<td>
				<input id="society_name1" name="society_name1" type="text"  value="${employee.society_name1}" /></td>
			<td>
				<input id="relation4" name="relation4" type="text"  value="${employee.relation4}" /></td>
			<td colspan="3">
				<input id="f_work_until4" name="f_work_until4" type="text"  value="${employee.f_work_until4}" /></td>
			<td>
				<input id="f_work_job4" name="f_work_job4" type="text"  value="${employee.f_work_job4}" /></td>
			<td>
				<input id="f_phone4" name="f_phone4" type="text"  value="${employee.f_phone4}" /></td>
		</tr>
		<tr>
			<td>
				<input id="society_name2" name="society_name2" type="text"  value="${employee.society_name2}" /></td>
			<td>
				<input id="relation5" name="relation5" type="text"  value="${employee.relation5}" /></td>
			<td colspan="3">
				<input id="f_work_until5" name="f_work_until5" type="text"  value="${employee.f_work_until5}" /></td>
			<td>
				<input id="f_work_job5" name="f_work_job5" type="text"  value="${employee.f_work_job5}" /></td>
			<td>
				<input id="f_phone5" name="f_phone5" type="text"  value="${employee.f_phone5}" /></td>
		</tr>
		<tr>
			<th>
				备注</th>
			<td colspan="7">
				<textarea id="remarks" name="remarks" >${employee.remarks}</textarea></td>
		</tr>
		
	   
	</tbody>
</table>


</body>
<script type="text/javascript">

function view(){
	var form_obj = document.all; 
	
	//form的值
	for (var i=0; i < form_obj.length; i++ ) {
		e=form_obj[i];
		if (e.tagName=='INPUT' || e.tagName=='TEXTAREA') {
			e.readOnly = true ;
			e.className = "readonly";
			e.disabled = true;
			e.backgroundImage = "none";
		}
		if(e.tagName=='SELECT'){
			e.disabled= true;
			e.className = "readonly";
		}
		if(e.tagName == 'A'){
			e.style.display = "none";
			e.disabled = true;
		}
		if(e.tagName == "IMG"){
			e.style.display = "none";
			e.disabled = true;
		}
	}
}




</script>

</html>
