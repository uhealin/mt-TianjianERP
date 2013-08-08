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
		
	    var tbar = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		defaults: {autoHeight: true,autoWidth:true},
            items:[{
	            text:'申请',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/start.png',
	            handler:function(){
					return save();
	            	//document.fthisform
				}
       		},'->']
        });
        
    }
	
	
    window.attachEvent('onload',ext_init);
</script>


</head>
<body leftmargin="0" topmargin="0" >
<div id="divBtn"></div>


<form  name="thisform" action="${pageContext.request.contextPath}/employee.do?method=doApply" method="post">
<table border="1" class="formTable">
	<thead>
		<tr>
			<th colspan="6" rowspan="2">
				<p class="p0" style="text-align: center; margin-top: 0pt; margin-bottom: 0pt">
					<span style="font-size: 26px">天健会计师事务所</span><br />
					&nbsp;</p>
				<!--EndFragment--></th>
			<td colspan="2" rowspan="5">
				</td>
			
		</tr>
		<tr>
			
			
		</tr>
	</thead>
	<tbody>
		<tr>
			<th colspan="6" style="text-align: center">
				员工登记表</th>
		</tr>
		<tr>
			<th>
				姓名</th>
			<td>
          
				<input id="name" name="name" type="text"  value="${cadet.name_cn }"/></td>
			<th>
				性别</th>
			<td>
			<select id="sex" name="sex" >
				<c:choose>
						<c:when test="${cadet.sex=='女'}">				
							<option value="女" >女</option>
							<option value="男">男</option>
						</c:when>
						<c:otherwise>			
							<option value="男">男</option>
							<option value="女" >女</option>
						</c:otherwise>
				</c:choose>
			</select>
				
			<th>
				民族</th>
			<td>
				<input id="nation" name="nation" type="text"  value="${cadet.nation }" /></td>
			
		</tr>
		<tr>
			<th>
				婚姻状况（已婚/未婚）</th>
			<td>
			<select id="marriage" name="marriage">
				<c:choose>
						<c:when test="${cadet.pe_marry=='已婚'}">	
							<option value="已婚">已婚</option>
							<option value="未婚">未婚</option>
						</c:when>
						<c:otherwise>
							<option value="未婚">未婚</option>
							<option value="已婚">已婚</option>
						</c:otherwise>
				</c:choose>
			</select>
				</td>
			<th>
				身份证号</th>
			<td >
				<input id="idcard" name="idcard" type="text"  value="${cadet.idcard }" /></td>
				<th>
				出生日期</th>
			<td>
				<input id="brithday" name="brithday"  ext_id=brithday ext_name=brithday  value="${cadet.birthday }" 
				 type="text" ext_type=date style="width: 500px;" /></td>
		</tr>
		<tr>
			<th>
				出生地</th>
			<td>
				<input id="brith_address" name="brith_address"  type="text"  value="${cadet.nativeplace }" /></td>
			<th>
				籍贯</th>
			<td>
				<input id="natives" name="natives"  type="text"  value="${cadet.nativeplace }" /></td>
			<th>
				父母居住地</th>
			<td >
				<input id="parents_address" name="parents_address" type="text"  value="${cadet.nativeplace }" /></td>
		</tr>
		
		<tr>
			<th>
				学历</th>
			<td>
				<input id="grad_formal" name="grad_formal"  type="text"  value="${cadet.grud_level }" /></td>
			<th>
				学位</th>
			<td>
				<input id="grad_degrees" name="grad_degrees"  type="text"  value="${cadet.grud_degree }" /></td>
			<th>
				是否双学位</th>
			<td>
				<select id="t_grad_degrees" name="t_grad_degrees">
					<option value="否">否</option>
					<option value="是">是</option>
				</select>
				</td>
			<th>
				第二学位</th>
			<td>
			
				<input id="two_grad_degrees" name="two_grad_degrees" type="text"  /></td>
		</tr>
		<tr>
			<th>
				英语等级</th>
			<td>
				<input id="cet_grade" name="cet_grade" type="text"  value="${cadet.cet_level }" /></td>
			<th>
				第二外语及等级</th>
			<td>
				<input id="two_foreign_language" name="two_foreign_language" type="text" /></td>
			<th>
				技术职称</th>
			<td>
				<input id="academic _title" name="academic _title" type="text"  value="${cadet.work_tech_title }" /></td>
			<th>
				取得职称时间</th>
			<td>
				<input id="academic_title_time" name="academic_title_time" ext_id=academic_title_time ext_name =academic_title_time ext_type=date type="text"  value="${cadet.work_tech_title_get_date }" /></td>
		</tr>
		<tr>
			<th>
				党/团员</th>
			<td>
				<select id="politics" name="politics">
				
					<option value="团员">团员</option>
					<option value="党员">党员</option>
				</select>
				
			<th>
				入党/团时间</th>
			<td>
				<input id="politics_time" name="politics_time" ext_id=politics_time ext_name =politics_time ext_type=date type="text"/></td>
			<th>
				户口所属地</th>
			<td>
				<input id="h_k_address" name="h_k_address" type="text" ${cadet.nativeplace }/></td>
			<th>
				户口是否迁移本所</th>
			<td>
				<select id="h_k_move" name="h_k_move">
					<option value="否">否</option>
					<option value="是">是</option>
				</select>
		</td>
		</tr>
		<tr>
			<th>
				现在地址</th>
			<td colspan="3">
				<input id="now_address" name="now_address" type="text"  value="${cadet.address }"  /></td>
			<th>
				手机</th>
			<td>
				<input id="conn_mobile" name="conn_mobile" type="text"  value="${cadet.urgent_conn_phone }" /></td>
			<th>
				应急联系人电话</th>
			<td>
				<input id="conn_phone" name="conn_phone" type="text"  value="${cadet.urgent_conn_phone }" /></td>
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
				<input id="exam_course1" name="exam_course1" type="text"  value="${cadet.pr_attend_1_info }" /></td>
			<td>
				<input id="exam_time1" name="exam_time1" ext_id=exam_time1 ext_name=exam_time1 ext_type=date type="text"  value="${cadet.pr_attend_1_get_date }" /></td>
			<td colspan="2">
				<input id="acquire_academic_title1" name="acquire_academic_title1" type="text"  value="${cadet.pr_pass_1_info }" /></td>
			<td colspan="2">
				<input id="register_time1" name="register_time1"  ext_id=register_time1 ext_name=register_time1 ext_type= date type="text"  value="${cadet.pr_pass_1_get_date }" /></td>
		</tr>
		<tr>
			<td colspan="2">
				<input id="exam_course2" name="exam_course2" type="text"  value="${cadet.pr_attend_2_info }" /></td>
			<td>
				<input id="exam_time2" name="exam_time2" ext_id=exam_time2 ext_name=exam_time2 ext_type=date type="text"  value="${cadet.pr_attend_2_get_date }" /></td>
			<td colspan="2">
				<input id="acquire_academic_title2" name="acquire_academic_title2" type="text"  value="${cadet.pr_pass_2_info }" /></td>
			<td colspan="2">
				<input id="register_time2" name="register_time2" ext_id=register_time2 ext_name=register_time2 ext_type= date type="text"  value="${cadet.pr_pass_2_get_date }" /></td>
		</tr>
		<tr>
			<td colspan="2">
				<input id="exam_course3" name="exam_course3" type="text"  value="" /></td>
			<td>
				<input id="exam_time3" name="exam_time3" ext_id=exam_time3 ext_name=exam_time3 ext_type=date type="text"  value="" /></td>
			<td colspan="2">
				<input id="acquire_academic_title3" name="acquire_academic_title3" type="text"  value="" /></td>
			<td colspan="2">
				<input id="register_time3" name="register_time3" ext_id=register_time3 ext_name=register_time3 ext_type= date type="text"  value="" /></td>
		</tr>
		<tr>
			<td colspan="2">
				<input id="exam_course4" name="exam_course4" type="text"  value="" /></td>
			<td>
				<input id="exam_time4" name="exam_time4" ext_id=exam_time4 ext_name=exam_time4 ext_type=date type="text"  value="" /></td>
			<td colspan="2">
				<input id="acquire_academic_title4" name="acquire_academic_title4" type="text"  value="" /></td>
			<td colspan="2">
				<input id="register_time4" name="register_time4" ext_id=register_time4 ext_name=register_time4 ext_type= date type="text"  value="" /></td>
		</tr>
		<tr>
			<td colspan="2">
				<input id="exam_course5" name="exam_course5;ext_type=text" type="text"  value="" /></td>
			<td>
				<input id="exam_time5" name="exam_time5" ext_id=exam_time5 ext_name=exam_time5 ext_type=date type="text"  value="" /></td>
			<td colspan="2">
				<input id="acquire_academic_title5" name="acquire_academic_title5" type="text"  value="" /></td>
			<td colspan="2">
				<input id="register_time5" name="register_time5" ext_id=register_time5 ext_name=register_time5 ext_type= date type="text"  value="" /></td>
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
				<input id="study_start_time1" name="study_start_time1" ext_id=study_start_time1 ext_name=study_start_time1 ext_type=date type="text"  value="${cadet.edc_1_start_date }" /></td>
			<td>
				<input id="study_end_time1" name="study_end_time1" ext_id=study_end_time1 ext_name=study_end_time1 ext_type=date type="text"  value="${cadet.edc_1_end_date }"  /></td>
			<td colspan="3">
				<input id="grad_school1" name="grad_school1" type="text" value="${cadet.edc_1_info }" /></td>
			<td colspan="2">
				<input id="grad_major1" name="grad_major1" type="text"  value="" /></td>
		</tr>
		<tr>
			<td>
				<input id="study_start_time2" name="study_start_time2"  ext_id=study_start_time2 ext_name=study_start_time2 ext_type=date type="text"  value="${cadet.edc_2_start_date }" /></td>
			<td>
				<input id="study_end_time2" name="study_end_time2" ext_id=study_end_time2 ext_name=study_end_time2 ext_type=date type="text"  value="${cadet.edc_2_end_date }" /></td>
			<td colspan="3">
				<input id="grad_school1" name="grad_school1" type="text"  value="${cadet.edc_2_info }" /></td>
			<td colspan="2">
				<input id="grad_major2" name="grad_major2" type="text"  value="" /></td>
		</tr>
		<tr>
			<td>
				<input id="study_start_time3" name="study_start_time3"  ext_id=study_start_time3 ext_name=study_start_time3 ext_type=date type="text"  value="${cadet.edc_3_start_date }" /></td>
			<td>
				<input id="study_end_time3" name="study_end_time3" ext_id=study_end_time3 ext_name=study_end_time3 ext_type=date type="text"  value="${cadet.edc_3_end_date }" /></td>
			<td colspan="3">
				<input id="grad_school3" name="grad_school3" type="text"  value="${cadet.edc_3_info }" /></td>
			<td colspan="2">
				<input id="grad_major3" name="grad_major3" type="text"  value="" /></td>
		</tr>
		<tr>
			<td>
				<input id="study_start_time4" name="study_start_time4"  ext_id=study_start_time4 ext_name=study_start_time4 ext_type=date type="text"  value="${cadet.edc_4_start_date }" /></td>
			<td>
				<input id="study_end_time4" name="study_end_time4" ext_id=study_end_time4 ext_name=study_end_time4 ext_type=date type="text"  value="${cadet.edc_4_end_date }" /></td>
			<td colspan="3">
				<input id="grad_school4" name="grad_school4" type="text"  value="${cadet.edc_4_info }" /></td>
			<td colspan="2">
				<input id="grad_major4" name="grad_major4" type="text"  value="" /></td>
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
				职&nbsp;务</th>
		</tr>
		<tr>
			<td>
				<input id="work_strart_time1" name="work_strart_time1" ext_id=work_strart_time1 ext_name=work_strart_time1 ext_type=date type="text"  value="${cadet.work_1_start_date }" /></td>
			<td>
				<input id="work_end_time1" name="work_end_time1" ext_id=work_end_time1 ext_name=work_end_time1 ext_type=date type="text"  value="${cadet.work_1_end_date }" /></td>
			<td colspan="3">
				<input id="work_unit1" name="work_unit1;ext_type=text" type="text"  value="${cadet.work_1_unit } ${work_1_dept}" /></td>
			<td colspan="2">
				<input id="work_job1" name="work_job1;ext_type=text" type="text"  value="${cadet.work_1_post }" /></td>
		</tr>
		<tr>
			<td>
				<input id="work_strart_time2" name="work_strart_time2" ext_id=work_strart_time2 ext_name=work_strart_time2 ext_type=date type="text"  value="${cadet.work_2_start_date }" /></td>
			<td>
				<input id="work_end_time2" name="work_end_time2" ext_id=work_end_time2 ext_name=work_end_time2 ext_type=date type="text"  value="${cadet.work_2_end_date }" /></td>
			<td colspan="3">
				<input id="work_unit2" name="work_unit2" type="text"  value="${cadet.work_2_unit } ${work_2_dept}" /></td>
			<td colspan="2">
				<input id="work_job2" name="work_job2" type="text"  value="${cadet.work_2_post }" /></td>
		</tr>
		<tr>
			<td>
				<input id="work_strart_time3" name="work_strart_time3" ext_id=work_strart_time3 ext_name=work_strart_time3 ext_type=date type="text"  value="${cadet.work_3_start_date }" /></td>
			<td>
				<input id="work_end_time3" name="work_end_time3" ext_id=work_end_time3 ext_name=work_end_time3 ext_type=date type="text"  value="${cadet.work_3_end_date }" /></td>
			<td colspan="3">
				<input id="work_unit3" name="work_unit3" type="text"  value="${cadet.work_3_unit } ${work_3_dept}" /></td>
			<td colspan="2">
				<input id="work_job3" name="work_job3" type="text"  value="${cadet.work_3_post }" /></td>
		</tr>
		<tr>
			<td>
				<input id="work_strart_time4" name="work_strart_time4;ext_type=date" ext_id=work_strart_time4 ext_name=work_strart_time4 ext_type=date type="text"  value="" /></td>
			<td>
				<input id="work_end_time4" name="work_end_time4" ext_id=work_end_time4 ext_name=work_end_time4 ext_type=date type="text"  value="" /></td>
			<td colspan="3">
				<input id="work_unit4" name="work_unit4;ext_type=text" type="text"  value="" /></td>
			<td colspan="2">
				<input id="work_job4" name="work_job4;ext_type=text" type="text"  value="" /></td>
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
				<input id="family_name1" name="family_name1" type="text"  value="" /></td>
			<td>
				<input id="relation1" name="relation1" type="text"  value="" /></td>
			<td colspan="3">
				<input id="f_work_until1" name="f_work_until1" type="text"  value="" /></td>
			<td>
				<input id="f_work_job1" name="f_work_job1" type="text"  value="" /></td>
			<td>
				<input id="f_phone1" name="f_phone1" type="text"  value="" /></td>
		</tr>
		<tr>
			<td>
				<input id="family_name2" name="family_name2" type="text"  value="" /></td>
			<td>
				<input id="relation2" name="relation2" type="text"  value="" /></td>
			<td colspan="3">
				<input id="f_work_until2" name="f_work_until2" type="text"  value="" /></td>
			<td>
				<input id="f_work_job2" name="f_work_job2" type="text"  value="" /></td>
			<td>
				<input id="f_phone2" name="f_phone2" type="text"  value="" /></td>
		</tr>
		<tr>
			<td>
				<input id="family_name3" name="family_name3" type="text"  value="" /></td>
			<td>
				<input id="relation3" name="relation3" type="text"  value="" /></td>
			<td colspan="3">
				<input id="f_work_until3" name="f_work_until3" type="text"  value="" /></td>
			<td>
				<input id="f_work_job3" name="f_work_job3" type="text"  value="" /></td>
			<td>
				<input id="f_phone3" name="f_phone3" type="text"  value="" /></td>
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
				<input id="society_name1" name="society_name1" type="text"  value="" /></td>
			<td>
				<input id="relation4" name="relation4" type="text"  value="" /></td>
			<td colspan="3">
				<input id="f_work_until4" name="f_work_until4" type="text"  value="" /></td>
			<td>
				<input id="f_work_job4" name="f_work_job4" type="text"  value="" /></td>
			<td>
				<input id="f_phone4" name="f_phone4" type="text"  value="" /></td>
		</tr>
		<tr>
			<td>
				<input id="society_name2" name="society_name2" type="text"  value="" /></td>
			<td>
				<input id="relation5" name="relation5" type="text"  value="" /></td>
			<td colspan="3">
				<input id="f_work_until5" name="f_work_until5" type="text"  value="" /></td>
			<td>
				<input id="f_work_job5" name="f_work_job5" type="text"  value="" /></td>
			<td>
				<input id="f_phone5" name="f_phone5" type="text"  value="" /></td>
		</tr>
		<tr>
			<th>
				备注</th>
			<td colspan="7">
				<textarea id="remarks" name="remarks"></textarea></td>
		</tr>
		<tr>
			<th colspan="6">
				天健会计师事务所制（2011年12月版）&nbsp;</th>
			<th>
				填表日期：</th>
			<td>
				<input id="write_date" name="write_date" ext_id=write_date ext_name=write_date ext_type=date type="text"  value="${request.d }" /></td>
		</tr>
				<tr>
	    <th>
		  审核人</th>
			<td colspan="7">
				<input name="checker_id" id="checker_id" autoid="10016"  width="50"  size="50"  class="required"/></td>
		</tr>
		
		<tr>
		  <th>
		  申请部门</th>
			<td colspan="7">
				<input name="departmentid" id="departmentid"  width="50"  size="50" autoid="30026" /></td>
		</tr>
		
	</tbody>
</table>


</form>
</body>

<script type="text/javascript">
   function save(){
	   var checker_id=document.getElementById("checker_id").value;
	   var departmentid=document.getElementById("departmentid").value;
	   if(checker_id!=""&&checker_id!=null){
		   if(departmentid!=""&&departmentid!=null)
	   			document.forms["thisform"].submit();
	   			else{
	   				alert("请填写申请部门！");
	   			}
	   }
	   else{
		   alert("请填写审核人！");
	   }
   }

   
  

</script>

</html>
