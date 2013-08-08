<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>社会招聘应聘报名表</title>

<!--  
<link href="/AS_CSS/style.css" rel="stylesheet" type="text/css"  />
-->
<style type="text/css">
  .formTable input{
     width: 98%;
     
  }
  
  th{
     text-align: left;
  }
  
  div.x-form-field-wrap{
     float: left;
  }
 
</style>
<script type="text/javascript">
  Ext.onReady(function(){
	  mt_form_initDateSelect();
	  //initCombox();
	  
	  if("view"=="${param.mode}"){
	    $("input").attr("disabled","disabled");
	  }
	  
	  attachImageInit("photo","imgPhoto");
  });
  
  
  
  
  function  validate(){
	 //alert(1);
	 var t=formSubmitCheck('thisForm');
	  //alert(t);
	 return  t;

  }
</script>
</head>
<body>
<form action="cadet.do?method=add" method="post" name="thisForm" onsubmit="return validate()">
  <input name="uuid" value="${vo.uuid }" type="hidden" />
  <input name="type" value="s" type="hidden" />
  <input name="menuid" value="${param.menuid }" type="hidden">
  <input name="formid" value="${param.formid }" type="hidden">
  <input name="departmentid" value="${vo.departmentid }" type="hidden" />
  <input name="hr_state" value="${vo.hr_state }" type="hidden" />
  <h1 style="text-align: center;font-size: 2.0em" >社会招聘应聘报名表</h1>
<table class="formTable" style="width: 93%" >
  <tbody style="text-align: left;" >
    <tr>
       <th style="width: 180px">姓名</th>
       <td style="width: 210px"><input name="name_cn" value="${vo.name_cn }" class="required"/></td>
       <th style="width: 120px">性别</th>
       <td style="width: 180px"><input id="sex" name="sex" value="${vo.sex }" autoid=700 refer="性别" class="required"/></td>
   
       <td style="width: 200px;text-align: center;" rowspan="4">
        <img id="imgPhoto" style="width: 108px;height: 150px"  /><br/>
       <input id="photo" type="hidden" name="photo_attach_id" value="${vo.photo_attach_id }"  />
       </td>
    </tr>
    
    <tr>
       <th>出生年月</th>
       <td><input ext_type="date" id="birthday" name="birthday" value="${vo.birthday }" class="required"/></td>
       <th>民族</th>
       <td><input name="nation" value="${vo.nation }" class="required" /></td>
    </tr>
    
 
    
    <tr>
      <th>籍贯</th>
      <td><input name="nativeplace" value="${vo.nativeplace }"  /></td>
      <th>家庭所在地</th>
      <td><input name="family_address" value="${vo.family_address }" /></td>
    </tr>
    
    <tr>
       <th>身份证号</th>
       <td colspan="3"><input name="idcard" readonly="readonly" value="${vo.idcard }"  class="required"/></td>
    </tr>
    
    <tr>
      <th rowspan="3">何院校何专业何<br />时毕业</th>
      <th>院校名称</th>
      <td><input name="grud_uni" value="${vo.grud_uni }" class="required"/></td>
      <th>专业</th>
      <td><input name="grud_pro_master" value="${vo.grud_pro_master }" class="required"/></td>
    </tr>
    
    <tr>
      <th>学制</th>
      <td><input name="grud_years" value="${vo.grud_years }" /></td>
      <th>毕业时间</th>
      <td><input ext_type="date" id="grud_date" name="grud_date" value="${vo.grud_date }" class="required"/></td>
      
    </tr>
    
    <tr>
      <th>学位</th>
      <td><input id="grud_degree" name="grud_degree" value="${vo.grud_degree }" autoid="700" refer="简历学位" /></td>
      <th>学历</th>
      <td><input id="grud_level" name="grud_level" value="${vo.grud_level }" autoid="700" refer="学历" class="required"/></td>
    </tr>
    
    <tr>
      <th>参加工作时间</th>
     <td><input ext_type="date" id="name="work_start_date"" name="work_start_date" value="${vo.work_start_date }" /></td>
     <th>政治面貌</th>
     <td colspan="2"><input id="political" name="political" value="${vo.political }" autoid="700" refer="政治面貌" /></td>
    </tr>
    
    <tr>
       <th>技术职称</th>
       <td><input name="work_tech_title" value="${vo.work_tech_title }" /></td>
       <th>取得时间</th>
       <td colspan="2"><input ext_type="date" id="work_tech_title_get_date" name="work_tech_title_get_date" value="${vo.work_tech_title_get_date }" /></td>
    </tr>
    
    <tr>
      <th>现在单位及部门</th>
      <td><input name="work_unit_dep" value="${vo.work_unit_dep }" /></td>
      <th>现任职务</th>
      <td colspan="2"><input name="work_post" value="${vo.work_post }" /></td>
    </tr>
    
    <tr>
      <th rowspan="3">何时取得何种执<br />业资格</th>
      <th>何年何月</th>
      <th colspan="3">取得执业资格名称</th>
    </tr>
    
    <tr>
      <td><input ext_type="date" name="pr_pass_1_get_date" id="pr_pass_1_get_date" value="${vo.pr_pass_1_get_date }" /></td>
      <td colspan="3"><input name="pr_pass_1_info" value="${vo.pr_pass_1_info }" /></td>
    </tr>
    <tr>
        <td><input ext_type="date" name="pr_pass_2_get_date" id="pr_pass_2_get_date" value="${vo.pr_pass_2_get_date }" /></td>
      <td colspan="3"><input name="pr_pass_2_info" value="${vo.pr_pass_2_info }"  /></td>
    </tr>

<!-- 

6->3
    
3-5

 -->    

    
    <tr>
      <th rowspan="3">未取得执业资格<br/>者，填写已通过<br/>的考试科目</th>
      <th>执业资格考试名称</th>
      <th colspan="3">已通过的科目</th>
    </tr>
    
     <tr>
         <td><input ext_type="date" name="pr_attend_1_get_date" id="pr_attend_1_get_date" value="${vo.pr_attend_1_get_date }" /></td>
      <td colspan="3"><input name="pr_attend_1_info" value="${vo.pr_attend_1_info }" /></td>
    </tr>
    <tr>
         <td><input ext_type="date" name="pr_attend_2_get_date" id="pr_attend_2_get_date" value="${vo.pr_attend_2_get_date }" /></td>
      <td colspan="3"><input name="pr_attend_2_info" value="${vo.pr_attend_2_info }" /></td>
    </tr>
    
<!--     
    
6->3
    
3-5

 -->    
    
    <tr>
      <th>计算机考试等级</th>
      <td><input id="cge_info" name="cge_info" value="${vo.cge_info }" autoid="700" refer="计算机等级" /></td>
      <th>掌握外语/等级</th>
      <td colspan="2"><input style="width: 45%"  name="lang_info" value="${vo.lang_info }" />
      /<input style="width: 45%"  name="lang_level" value="${vo.lang_level }" />
      </td>
    </tr>
    
    <tr>
      <th rowspan="2">健康状况</th>
      <th>身高(公分)</th>
      <td><input name="pe_height" value="${vo.pe_height }" /></td>
      <th>体重(公斤)</th>
      <td><input name="pe_weight" value="${vo.pe_weight }" /></td>
    </tr>
    
    <tr>
    <th>薪资期望（年薪）</th>
      <td colspan="3"><input name="expect_payment" value="${vo.expect_payment }" /></td>
    </tr>
    
    <tr>
      <th rowspan="2">婚姻情况</th>
      <th>婚否</th>
      <td><input id="pe_marry" name="pe_marry" value="${vo.pe_marry }" autoid="700" refer="婚姻状况" /></td>
      <th>配偶姓名</th>
      <td><input name="other_namecn" value="${vo.other_namecn }" /></td>
    </tr>
    
    <tr>
       <th>在何单位任何职</th>
       <td><input name="other_work_info" value="${vo.other_work_info }" /></td>
       <th>子女年龄</th>
       <td><input name="child_age" value="${vo.child_age }" /></td>
    </tr>
    
    <tr>
       <th>本人或配偶的房<br/>产情况</th>
       <td colspan="4"><textarea name="house_info">${vo.house_info }</textarea></td>
    </tr>
    
    <tr>
      <th rowspan="4">学习简历(从高中入<br/>学起)</th>
      <th>何年何月-何年何月</th>
      <th colspan="3">何地/何校/何专业/何学历</th>
    </tr>

   <tr>
      <td>
      <input ext_type="date" id="edc_1_end_date" name="edc_1_end_date" value="${vo.edc_1_end_date }" style="width: 42%;" class="required"/>
      <input ext_type="date" id="edc_1_start_date" name="edc_1_start_date" value="${vo.edc_1_start_date }" style="width: 42%;" class="required"/>
      </td>
      <td colspan="3"><input name="edc_1_info" value="${vo.edc_1_info }" class="required"/></td>
   </tr>
   
   <tr>
         <td>
         <input ext_type="date" id="edc_2_start_date" name="edc_2_start_date" value="${vo.edc_2_start_date }" style="width: 46%" />
      	 <input ext_type="date" id="edc_2_end_date" name="edc_2_end_date" value="${vo.edc_2_end_date }" style="width: 46%" /></td>
      	<td colspan="3"><input name="edc_2_info" value="${vo.edc_2_info }" /></td>
   </tr>
   
   <tr>
         <td><input ext_type="date" id="edc_3_start_date" name="edc_3_start_date" value="${vo.edc_3_start_date }" style="width: 46%" />
      		<input ext_type="date" id="edc_3_end_date" name="edc_3_end_date" value="${vo.edc_3_end_date }" style="width: 46%" /></td>
      	<td colspan="3"><input name="edc_3_info" value="${vo.edc_3_info }" /></td>
   </tr>
   
   <tr>
     <th rowspan="4">工作简历</th>
     <th>起止日期</th>
     <th>工作单位</th>
     <th>从事单位</th>
     <th>职务</th>
   </tr>
   
   <tr>
     <td><input ext_type="date" id="work_1_start_date" name="work_1_start_date" value="${vo.work_1_start_date }" style="width: 46%" class="required"/>
      <input ext_type="date" id="work_1_end_date" name="work_1_end_date" value="${vo.work_1_end_date }" style="width: 46%" class="required"/></td>
     <td><input name="work_1_unit" value="${vo.work_1_unit }" class="required"/></td>
     <td><input name="work_1_dept" value="${vo.work_1_dept }" class="required"/></td>
     <td><input name="work_1_post" value="${vo.work_1_post }" class="required"/></td>
   </tr>
   
     <tr>
     <td><input ext_type="date" id="work_2_start_date" name="work_2_start_date" value="${vo.work_2_start_date }" style="width: 46%" />
      <input ext_type="date" id="work_2_end_date" name="work_2_end_date" value="${vo.work_2_end_date }" style="width: 46%" /></td>
     <td><input name="work_2_unit" value="${vo.work_2_unit }" /></td>
     <td><input name="work_2_dept" value="${vo.work_2_dept }" /></td>
     <td><input name="work_2_post" value="${vo.work_2_post }" /></td>
   </tr>
   
     <tr>
     <td><input ext_type="date" id="work_3_start_date" name="work_3_start_date" value="${vo.work_3_start_date }" style="width: 46%" />
      <input ext_type="date" id="work_3_end_date" name="work_3_end_date" value="${vo.work_3_end_date }" style="width: 46%" /></td>
     <td><input name="work_3_unit" value="${vo.work_3_unit }" /></td>
     <td><input name="work_3_dept" value="${vo.work_3_dept }" /></td>
     <td><input name="work_3_post" value="${vo.work_3_post }" /></td>
   </tr>
    
    <tr>
      <th>主要工作业绩或<br/>完成的代表性项<br/>目简介</th>
      <td colspan="4"><textarea name="work_achi_info" >${vo.work_achi_info }</textarea></td>
    </tr>
    
    <tr>
      <th rowspan="2">通讯方式</th>
      <th>详细通讯地址</th>
      <td colspan="3"><input name="address" value="${vo.address }" /></td>
    </tr>
    
    <tr>
      <th>邮编</th>
      <td><input name="zipcode" value="${vo.zipcode }" /></td>
      <th>手机</th>
      <td><input name="mobile" value="${vo.mobile }" class="required"/></td>
    </tr>
    
    <tr>
      <th>求职岗位</th>
      <td colspan="2">
      <input name="apply_job_id" id="apply_job_id" value="${vo.apply_job_id}" type="hidden" />
      <input name="apply_job_name" id="apply_job_name" value="${vo.apply_job_name}" onclick="show_selectJob('apply_job_name','apply_job_id','0')" readonly="readonly" class="required"/></td>
      <th>预计到岗时间</th>
      <td><input ext_type="date" id="except_start_work_date" name="except_start_work_date" value="${vo.except_start_work_date }" class="required"/></td>
    </tr>
    
    <tr>
      <th>备注</th>
      <td colspan="4"><textarea name='remark'>${vo.remark }</textarea> </td>
    </tr>
    
    <tr>
       <th></th>
       <td colspan="4"> 
	       <button type="submit" onclick="mySubmit();">保存</button>&nbsp;&nbsp;
	       <button type="button" onclick="window.print();">打印</button>&nbsp;&nbsp;
	       <button type="button" onclick="window.history.back(); ">返回</button>
       </td>
    </tr>
   </tbody>
    
    
</table>

		</form>
		
<script >
 new Validation("thisForm");

</script>
	
</body>
</html>