<%@page import="com.matech.audit.work.cadet.CadetAction"%>
<%@page import="com.matech.audit.service.cadet.model.CadetVO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>实习生登记表</title>

<link rel="shortcut icon" href="${pageContext.request.contextPath}/images/donggua.ico" 
mce_href="${pageContext.request.contextPath}/images/donggua.ico" type="image/x-icon">

<link href="/AS_CSS/style.css" rel="stylesheet" type="text/css"  /> 
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
	  //initCombox("sex");
	  
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
		    $("input,textarea").attr("readonly","readonly");
		    $("#btnSave,#btnBack").attr("disabled","disabled");
	   }
	  
	  attachImageInit("photo","imgPhoto");
  });
  new Validation('thisForm');

  function mySubmit() {
  	
  	if (!formSubmitCheck('thisForm')) return ;
 }
  function  validate(){
	  //表单验证
	  return true;
  }
  
  function printCadet(){
		if(!confirm("打印前必须先按保存按钮,是否已保存? \n 如果未保存请保存后重新进入再打印；\n 如果已保存，请点确定进行下载或打印。")){
			return false;
		}
	  var uuid = document.getElementById("uuid").value;
	  if(uuid == ""){
		  alert("实习生登记表要先保存一次才能打印！");
		  return;
	  }
	  printform.qryWhere_em.value=" and uuid = '"+uuid+"' ";
	  printform.templateid.value="3b16d45d-3b65-11e2-84fe-6732c33eede8";
	  printform.submit();
	  stopWaiting();
  }
  
</script>

</head>
<body>
<form action="cadet.do?method=add" method="post" onsubmit="return validate()">
  <input name="uuid" value="${vo.uuid }" type="hidden" />
  <input name="type" value="1" type="hidden" />
  <input name="menuid" value="${param.menuid }" type="hidden">
  <input name="formid" value="${param.formid }" type="hidden">
  <input name="departmentid" value="${vo.departmentid }" type="hidden" />
  <h1 style="text-align: center;font-size: 2.0em" >实习生登记表</h1>
<table class="formTable" style="table-layout: auto;" >
  <tbody style="text-align: left;" >
    <tr>
       <th style="width: 100px" >姓名</th>
       <td style="width: 210px"><input name="name_cn" value="${vo.name_cn }" /></td>
       <th style="width: 80px">性别</th>
       <td style="width: 100px"><input id="sex" name="sex" value="${vo.sex }" autoid=700 refer="性别" /></td>
       <th style="width: 80px">民族</th>
       <td style="width: 120px"><input name="nation" value="${vo.nation }" /></td>
       <td style="width: 200px;text-align: center;" rowspan="6">
       <img id="imgPhoto" style="width: 108px;height: 150px"  /><br/>
       <input id="photo" type="hidden" name="photo_attach_id" value="${vo.photo_attach_id }"  />
       </td>
    </tr>
    
    <tr>
        <th >出生年月</th>
       <td ><input ext_type="date" id="birthday" name="birthday" value="${vo.birthday }" /></td>
       <th >政治面貌</th>
       <td ><input id="political" name="political" value="${vo.political }" autoid="700" refer="政治面貌" /></td>
       <th >籍贯</th>
       <td ><input name="nativeplace" value="${vo.nativeplace }"/></td>
    </tr>
    
     <tr>
       <th >身份证号</th>
       <td colspan="5"><input name="idcard" value="${vo.idcard }" readonly="readonly"  /></td>
       
      
    </tr>
    
    <tr>
       <th >家庭地址</th>
       <td colspan="5"><input name="family_address" value="${vo.family_address }"  /></td>
       
    </tr>
    
    <tr>
        <th rowspan="4">何院校何专<br/>业何时毕业</th>
        <th >院校名称</th>
        <td colspan="4"><input name="grud_uni" value="${vo.grud_uni }" /></td>
    </tr>
    
    <tr>
        <th>专业</th>
        <td colspan="4"><input name="grud_pro_master" value="${vo.grud_pro_master }" /></td>
    </tr>
    
    
    
    <tr>
        <th>学历</th>
        <td colspan="2"><input id="grud_level" name="grud_level" value="${vo.grud_level }" autoid="700" refer="学历" /></td>
        <th colspan="2">毕业年月</th>
        <td><input name="grud_date" value="${vo.grud_date }" /></td>
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
       <td colspan="2" ><input name="cee_score" value="${vo.cee_score }" style="width: 40%"/>/
        <input name="cee_line" value="${vo.cee_line }" style="width: 40%"/></td>
       <th colspan="2">高校录取批次</th>
       <td  ><input id="cee_batch" name="cee_batch" value="${vo.cee_batch }" autoid="700" refer="简历院校" /></td>
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
       <td ><input name="expect_payment" value="${vo.expect_payment }"></td>
       
    </tr>
    
    <tr>
      <th rowspan="5">学习简历<br/>(初中入学起)</th>
      <th>何年何月-何年何月</th>
      <th colspan="5">何地/何校/何专业/何学历</th>
    </tr>
    
    <tr>
      <td><input ext_type="date" id="edc_1_start_date" name="edc_1_start_date" value="${vo.edc_1_start_date }" style="width: 46%" />
      <input ext_type="date" id="edc_1_end_date" name="edc_1_end_date" value="${vo.edc_1_end_date }" style="width: 46%" /></td>
      <td colspan="5"><input name="edc_1_info" value="${vo.edc_1_info }" /></td>
    </tr>
    
    
    
    <tr>
      <td><input ext_type="date" id="edc_2_start_date" name="edc_2_start_date" value="${vo.edc_2_start_date }" style="width: 46%;display: inline;" />
      <input ext_type="date" id="edc_2_end_date" name="edc_2_end_date" value="${vo.edc_2_end_date }" style="width: 46%;display: inline;" /></td>
      <td colspan="5"><input name="edc_2_info" value="${vo.edc_2_info }" /></td>
    </tr><tr>
            <td><input ext_type="date" id="edc_3_start_date" name="edc_3_start_date" value="${vo.edc_3_start_date }" style="width: 46%" />
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
      <td><input ext_type="date" id="prac_1_start_date" name="prac_1_start_date" value="${vo.prac_1_start_date }" style="width: 46%" />
      <input ext_type="date" id="prac_1_end_date" name="prac_1_end_date" value="${vo.prac_1_end_date }" style="width: 46%" /></td>
      <td colspan="5"><input name="prac_1_info" value="${vo.prac_1_info }" /></td>
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
         <table >
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
         <table >
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
        <th colspan="4">3.ACCA、CGA等国际注册会计考试通过情况：</th>
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
      <td colspan="3"><input name="email" value="${vo.email }" /></td>
       <th>手机</th>
      <td><input name="mobile" value="${vo.mobile }" /></td>
    </tr>
    
     <tr>
      <th>紧急联系人</th>
      <td ><input name="urgent_conn_name" value="${vo.urgent_conn_name }" /></td>
      <th>与本人关系</th>
      <td><input name="urgent_conn_relation" value="${vo.urgent_conn_relation }" /></td>
      <th>手机</th>
      <td><input name="urgent_conn_phone" value="${vo.urgent_conn_phone }" /></td>
    </tr>
    
    <tr>
      <th>实习计划时间</th>
      <td colspan="2"><input ext_type="date" id="prac_start_date" name="prac_start_date" value="${vo.prac_start_date }" style="width: 100px" />
      <input ext_type="date" id="prac_end_date" name="prac_end_date" value="${vo.prac_end_date }" style="width: 100px" /></td>
      <th>计划实习报到时间</th>
      <td colspan="3"><input ext_type="date" id="prac_real_start_date" name="prac_real_start_date" value="${vo.prac_real_start_date }"  /></td>
    </tr>
    
    <tr>
      <th>部门意见</th>
      <td colspan="2">
          <br /><br/><br /><br/>
                                 负责人(签名):<br/>
         <div style="text-align: right;">年&nbsp;&nbsp;&nbsp;&nbsp;月&nbsp;&nbsp;&nbsp;&nbsp;日</div>
      </td>
     
      <th>所领导意见</th>
      <td colspan="3">
              <br /><br/><br /><br/>
                                 负责人(签名):<br/>
         <div style="text-align: right;">年&nbsp;&nbsp;&nbsp;&nbsp;月&nbsp;&nbsp;&nbsp;&nbsp;日</div>
      </td>
    </tr>
    
    <tr>
       <th></th>
       <td colspan="6">
       <c:if test="${!hasAssigned }">
       <button type="submit" id="btnSave">保存</button>&nbsp;&nbsp;
       </c:if>
       <button type="button" id="btnPrint" onclick="printCadet();">下载打印</button>&nbsp;&nbsp;
       <button type="button" id="btnBack" onclick="window.history.back(); ">返回</button>
       </td>
    </tr>
    
   
    
    </tbody>
    
    
</table>
     
     <div style="padding-left: 4%;font-size: 1.1em;padding-top: 5px">
     注意事项：此表由实习生本人填写，经部门主管签署意见后复印2份，其中1份交部门主管留底，其余2份交回人力资源部。
     </div>

</form>
		
<form name=printform action="${pageContext.request.contextPath}/print.do?method=download" method="post" target="blank">
<input type=hidden name=templateid id=templateid value="">
<input type=hidden name=qryWhere_em id=qryWhere_em value="">
</form>
	
</body>
</html>