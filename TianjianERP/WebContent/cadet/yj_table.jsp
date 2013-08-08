<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>应届毕业生应聘报名表</title>

<link href="/AS_CSS/style.cssd" rel="stylesheet" type="text/css"  /> 
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
	  

	  $(".cb").each(function(index){
		  var hid=$(this);
		 // alert(hid.val());
		  var checkbox=$("<input type='checkbox' "+(hid.val()=="是"?"checked='checked'":"")+" />");
		  checkbox.css("width","30px");
		  hid.before(checkbox);
		  checkbox.click(function(){
			  hid.val($(this).attr("checked")==true?"是":"否");
			  //alert($(this).attr("checked")+hid.val());
		  });
	  });	  
	  
	  
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
<form action="cadet.do?method=add" method="post"  name="thisForm" onsubmit="return validate()">
  <input name="uuid" value="${vo.uuid }" type="hidden" />
  <input name="type" value="y" type="hidden" />
  <input name="menuid" value="${param.menuid }" type="hidden">
  <input name="formid" value="${param.formid }" type="hidden">
  <input name="departmentid" value="${vo.departmentid }" type="hidden" />
  <input name="hr_state" value="${vo.hr_state }" type="hidden" />
  <h1 style="text-align: center;font-size: 2.0em" >应届毕业生应聘报名表</h1>
<table class="formTable" style="width: 93%" > 
  <tbody style="text-align: left;" >
    <tr>
       <th style="width: 100px" >姓名</th>
       <td style="width: 210px"><input name="name_cn" value="${vo.name_cn }" class="required"/></td>
       <th style="width: 80px">性别</th>
       <td style="width: 100px"><input id="sex" name="sex" value="${vo.sex }" autoid=700 refer="性别" class="required"/></td>
       <th style="width: 80px">民族</th>
       <td style="width: 120px"><input name="nation" value="${vo.nation }" class="required"/></td>
       <td style="width: 200px;text-align: center;" rowspan="6">
       <img id="imgPhoto" style="width: 108px;height: 150px"  /><br/>
       <input id="photo" type="hidden" name="photo_attach_id" value="${vo.photo_attach_id }"  />
       </td>
    </tr>
    
    <tr>
        <th >出生年月</th>
       <td ><input ext_type="date" id="birthday" name="birthday" value="${vo.birthday }" class="required"/></td>
       <th >政治面貌</th>
       <td ><input id="political" name="political" value="${vo.political }" autoid="700" refer="政治面貌" class="required"/></td>
       <th >籍贯</th>
       <td ><input name="nativeplace" value="${vo.nativeplace }" class="required"/></td>
    </tr>
    
     <tr>
       <th >身份证号</th>
       <td colspan="5"><input name="idcard" value="${vo.idcard }"  class="required"/></td>
     </tr>
    
    <tr>
       <th >家庭地址</th>
       <td colspan="5"><input name="family_address" value="${vo.family_address }" /></td>
    </tr>
    
    <tr>
        <th rowspan="4">何院校何专<br/>业何时毕业</th>
        <th >院校名称</th>
        <td colspan="4"><input name="grud_uni" value="${vo.grud_uni }" class="required"/></td>
    </tr>
    
    <tr>
        <th>专业</th>
        <td colspan="4"><input name="grud_pro_master" value="${vo.grud_pro_master }" class="required"/></td>
    </tr>
    
    
    
    <tr>
        <th>学历</th>
        <td colspan="2"><input id="grud_level" name="grud_level" value="${vo.grud_level }" autoid="700" refer="学历" class="required"/></td>
        <th colspan="2">毕业年月</th>
        <td><input ext_type="date" id="grud_date" name="grud_date" value="${vo.grud_date }" class="required"/></td>
    </tr>
    
     <tr>
        <th>学位</th>
        <td colspan="2"><input id="grud_degree" name="grud_degree" value="${vo.grud_degree }" autoid="700" refer="简历学位" /></td>
        <th colspan="2">辅修专业</th>
        <td><input name="grud_pro_slave" value="${vo.grud_pro_slave }" /></td>
    </tr>
      
    <tr>
       <th rowspan="2">在班级或年</br>级成绩名次</th>
       <th>班级名次/总人数</th>
       <td colspan="2" ><input name="class_postion" value="${vo.class_postion }" style="width: 40%" />/
        <input name="class_total" value="${vo.class_total }" style="width: 40%"/> </td>
       <th colspan="2">年级名次/总人数</th> 
       <td  ><input name="grade_postion" value="${vo.grade_postion }" style="width: 40%" />/
        <input name="grade_total" value="${vo.grade_total }" style="width: 40%"/></td>
    </tr>
     
    <tr>
       <th>高考分/一本线分</th>
       <td colspan="2" ><input name="cee_score" value="${vo.cee_score }" style="width: 40%" />/
        <input name="cee_line" value="${vo.cee_line }" style="width: 40%"/></td>
       <th colspan="2">高校录取批次</th>
       <td><input id="cee_batch" name="cee_batch" value="${vo.cee_batch }" autoid="700" refer="简历院校" /></td>
    </tr>
    
    <tr>
       <th>英语等级</th>
       <td><input id="cet_level" name="cet_level" value="${vo.cet_level }" autoid="700" refer="英语等级" /></td>
       <th  colspan="2">考研、考公出国计划</th>
       <td colspan="3"><input id="plan_ext_study" name="plan_ext_study" value="${vo.plan_ext_study }" autoid="700" refer="考研考公出国计划" /></td>
    </tr>
    
    <tr>
       <th>第二外语</th>
       <td><input  name="lang_info" value="${vo.lang_info }" /></td>
       <th  colspan="2">计算机等级</th>
       <td colspan="3"><input id="cge_info" name="cge_info" value="${vo.cge_info }" autoid="700" refer="计算机等级" /></td>
    </tr>
    
    <tr>
       <th rowspan="2"> 个人状况</th>
       <th>身高(公分)</th>
       <td colspan="2"><input name="pe_height" value="${vo.pe_height }" /></td>
       <th colspan="2">体重(公斤)</th>
       <td ><input name="pe_weight" value="${vo.pe_weight }" /></td>
    </tr>
    
    <tr>
      
    <th>婚否</th>
       <td colspan="2"><input id="pe_marry" name="pe_marry" value="${vo.pe_marry }" autoid="700" refer="婚姻状况" /></td>
       <th colspan="2">期望薪资(年薪)</th>
       <td ><input name="expect_payment" value="${vo.expect_payment }" /></td>
       
    </tr>
    
    <tr>
      <th rowspan="5">学习简历<br/>(初中入学起)</th>
      <th>何年何月-何年何月</th>
      <th colspan="5">何地/何校/何专业/何学历</th>
    </tr>
    
    <tr>
      <td><input ext_type="date" id="edc_1_start_date" name="edc_1_start_date" value="${vo.edc_1_start_date }" style="width: 46%" class="required"/>
      <input ext_type="date" id="edc_1_end_date" name="edc_1_end_date" value="${vo.edc_1_end_date }" style="width: 46%" class="required"/></td>
      <td colspan="5"><input name="edc_1_info" value="${vo.edc_1_info }" class="required"/></td>
    </tr>
    
    
    
    <tr>
      <td><input ext_type="date" id="edc_2_start_date" name="edc_2_start_date" value="${vo.edc_2_start_date }" style="width: 46%;display: inline;" />
      <input ext_type="date" id="edc_2_end_date" name="edc_2_end_date" value="${vo.edc_2_end_date }" style="width: 46%;display: inline;" /></td>
      <td colspan="5"><input name="edc_2_info" value="${vo.edc_2_info }" /></td>
    </tr><tr>
            <td>
            <input ext_type="date" id="edc_3_start_date" name="edc_3_start_date" value="${vo.edc_3_start_date }" style="width: 46%" />
	        <input ext_type="date" id="edc_3_end_date" name="edc_3_end_date" value="${vo.edc_3_end_date }" style="width: 46%" /></td>
	        <td colspan="5"><input name="edc_3_info" value="${vo.edc_3_info }" /></td>
    </tr><tr>
            <td><input ext_type="date" id="edc_4_start_date" name="edc_4_start_date" value="${vo.edc_4_start_date }" style="width: 46%" />
      <input ext_type="date" id="edc_4_end_date" name="edc_4_end_date" value="${vo.edc_4_end_date }" style="width: 46%" /></td>
      <td colspan="5"><input name="edc_4_info" value="${vo.edc_4_info }" /></td>
    </tr>
 
 
     <tr>
      <th rowspan="5">社会实践经<br/>历（填写与求</br>职岗位相关</br>的实践经历，</br>天健的实习</br>经历必填)</th>
      <th>何年何月-何年何月</th>
      <th colspan="5">单位/部门/岗位</th>
    </tr>
    
    <tr>
      <td><input ext_type="date" id="prac_1_start_date" name="prac_1_start_date" value="${vo.prac_1_start_date }" style="width: 46%" class="required"/>
      <input ext_type="date" id="prac_1_end_date" name="prac_1_end_date" value="${vo.prac_1_end_date }" style="width: 46%" class="required"/></td>
      <td colspan="5"><input name="prac_1_info" value="${vo.prac_1_info }" class="required"/></td>
    </tr>
    
    
    
    <tr>
     <td><input ext_type="date" id="prac_2_start_date" name="prac_2_start_date" value="${vo.prac_2_start_date }" style="width: 46%" />
      <input ext_type="date" id="prac_2_end_date" name="prac_2_end_date" value="${vo.prac_2_end_date }" style="width: 46%" /></td>
      <td colspan="5"><input name="prac_2_info" value="${vo.prac_2_info }" /></td>
    </tr><tr>
     <td><input ext_type="date" id="prac_3_start_date" name="prac_3_start_date" value="${vo.prac_3_start_date }" style="width: 46%" />
      <input ext_type="date" id="prac_3_end_date" name="prac_3_end_date" value="${vo.prac_3_end_date }" style="width: 46%" /></td>
      <td colspan="5"><input name="prac_3_info" value="${vo.prac_3_info }" /></td>
    </tr><tr>
     <td><input ext_type="date" id="prac_4_start_date" name="prac_4_start_date" value="${vo.prac_4_start_date }" style="width: 46%" />
      <input ext_type="date" id="prac_4_end_date" name="prac_4_end_date" value="${vo.prac_4_end_date }" style="width: 46%" /></td>
      <td colspan="5"><input name="prac_4_info" value="${vo.prac_4_info }" /></td>
    </tr>
    
    <tr>
       <th rowspan="4">执业资格</th>
       <th>1.注册会计师统考<br/>已合格的标注:</th>
       <td colspan="5">
         <table style="width: 100%">
           <tr>
             <td style="width: 23%;border-width: 0px">
             <input class="cb" type="hidden" name="cpa_account_pass_ind" value="${vo.cpa_account_pass_ind }">会计
             </td>
             <td style="width: 23%;border-width: 0px"><input class="cb" type="hidden" name="cpa_audit_pass_ind" value="${vo.cpa_audit_pass_ind }">审计</td>
            <td style="width: 23%;border-width: 0px"><input class="cb" type="hidden" name="cpa_fcm_pass_ind" value="${vo.cpa_fcm_pass_ind }">财务成本管理</td>
            <td style="width: 23%;border-width: 0px"><input class="cb" type="hidden" name="cpa_eclaw_pass_ind" value="${vo.cpa_eclaw_pass_ind }">经济法</td>
           </tr>
           
         <tr>
              <td style="border-width: 0px"  ><input class="cb" type="hidden" name="cpa_taxlaw_pass_ind" value="${vo.cpa_taxlaw_pass_ind }">税法</td>
             <td style="border-width: 0px"  colspan="2" ><input class="cb" type="hidden" name="cpa_srm_pass_ind" value="${vo.cpa_srm_pass_ind }">战略与风险管理</td>
            <td style="border-width: 0px"  ><input class="cb" type="hidden" name="cpa_sp_pass_ind" value="${vo.cpa_sp_pass_ind }">综合阶段</td>
  
            
           </tr></table>
       </td>
    </tr>
    
    <tr>
       
       <th>2.注册会计师统考<br/>已报考，成绩待公布<br/>的标注:</th>
       <td colspan="5">
         <table style="width: 100%">
           <tr>
             <td style="width: 23%;border-width: 0px"><input class="cb" type="hidden" name="cpa_account_attend_ind" value="${vo.cpa_account_attend_ind }">会计</td>
             <td style="width: 23%;border-width: 0px"><input class="cb" type="hidden" name="cpa_audit_attend_ind" value="${vo.cpa_audit_attend_ind }">审计</td>
            <td style="width: 23%;border-width: 0px"><input class="cb" type="hidden" name="cpa_fcm_attend_ind" value="${vo.cpa_fcm_attend_ind }">财务成本管理</td>
            <td style="width: 23%;border-width: 0px"><input class="cb" type="hidden" name="cpa_eclaw_attend_ind" value="${vo.cpa_eclaw_attend_ind }">经济法</td>
           </tr>
           
         <tr>
             <td style="border-width: 0px" ><input class="cb" type="hidden" name="cpa_taxlaw_attend_ind" value="${vo.cpa_taxlaw_attend_ind }">税法</td>
             <td style="border-width: 0px"  colspan="2" ><input class="cb" type="hidden" name="cpa_srm_attend_ind" value="${vo.cpa_srm_attend_ind }">战略与风险管理</td>
            <td style="border-width: 0px"  ><input class="cb" type="hidden" name="cpa_sp_attend_ind" value="${vo.cpa_sp_attend_ind }">综合阶段</td>
            
           </tr></table>
       </td> 
    </tr>
    <tr>
        <th colspan="4">3.ACCA、CGA等国际注册会计考试通过情况</th>
        <td colspan="2"><input name="ext_pr_info" value="${vo.ext_pr_info }"   /></td>
    </tr>
    
    <tr>
       <th>4.其他职业资格</th>
       <td colspan="5"><input name="other_pr_info" value="${vo.other_pr_info }" /></td>
    </tr>
    
    <tr>
      <th rowspan="3">通讯方式<br/>(请详细填写)</th>
      <th>学校通讯地址</th>
      <td colspan="5"><input name="address" value="${vo.address }" /></td>
    </tr>
    
    <tr>
      <th>E-mail</th>
      <td colspan="3"><input name="email" value="${vo.email }" class="required"/></td>
      <th>邮编</th>
      <td><input name="zipcode" value="${vo.zipcode }" /></td>
    </tr>
    
     <tr>
      <th>电话</th>
      <td colspan="3"><input name="phone" value="${vo.phone }" /></td>
      <th>手机</th>
      <td><input name="mobile" value="${vo.mobile }" class="required"/></td>
    </tr>
    
    <tr>
      <th>性格特长简述</th>
      <td colspan="6"><textarea name="special_info" >${vo.special_info}</textarea></td>
    </tr>
    
    <tr>
      <th>求职岗位</th>
      <td colspan="4">
      <input name="apply_job_id" id="apply_job_id" value="${vo.apply_job_id}" type="hidden" class="validate"/>
      <input readonly="readonly" name="apply_job_name" id="apply_job_name" value="${vo.apply_job_name}" onclick="show_selectJob('apply_job_name','apply_job_id','1')" class="required"/></td>
  
      
      <th>希望工作地点</th>
      <td><input  name="work_address" value="${vo.work_address }" /></td>
    </tr>
    
    <tr>
      <th>实习安排</th>
      <td colspan="6">时间:<input ext_type="date" id="prac_start_date" name="prac_start_date" value="${vo.prac_start_date }" style="width: 100px" />至
      <input ext_type="date" id="prac_end_date" name="prac_end_date" value="${vo.prac_end_date }" style="width: 100px" />(应尽早安排，并请自行解决住宿。)</td>
    </tr>
    
    <tr>
       <th></th>
       <td colspan="6"><button type="submit">保存</button>&nbsp;&nbsp;
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