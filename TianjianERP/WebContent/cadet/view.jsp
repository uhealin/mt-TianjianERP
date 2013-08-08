<%@ page import="java.util.Date" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>实习人员登记表</title>

<script type="text/javascript">
Ext.onReady(function (){
	
	mt_form_initDateSelect();
	
});
</script>
</head>
<body >
  <form action="cadet.do?method=add" method="post">
<table align="center" border="1px solid" align="center" class="formTable">
<thead>
<tr>
	<th  colspan="10" style="font-size: 36">实习人员登记表</th>
</tr>
</thead>
<tbody>
<input type="hidden" id="uuid" name="uuid" value="${cadetVO.uuid }">
  <tr >
    <th>姓&nbsp;名 </td>
    <td ><input type="text" name="name_cn" id="name_cn" value="${cadetVO.name_cn}"/></th>
    <th valign="center"  >性&nbsp;别 </th>
    <td style="width: 100px;" >
    <select  name="sex" id="sex" value="${cadetVO.sex }">
    <option value="男" >男</option>
    <option value="女">女</option>
    </select></td>
    <th valign="center"  >籍&nbsp;贯 </th>
    <td  valign="center" >
      <input type="text" name="natives" id="natives" value="${cadetVO.natives }"/>
    </td>
    <th valign="center"  >民&nbsp;族 </th>
    <td valign="center"  >
      <input type="text" name="nation" id="nation" value="${cadetVO.nation }"/>
  </td>
    <th valign="center"  >党/团员 </th>
    <td  valign="center" >
      <input type="text" name="politics" id="politics" value="${cadetVO.politics }"/>
    </td>
  </tr>
  <tr >
    <th valign="center" >出生年月日 </th>
    <td valign="center" >
    <input type="text" id="brithday" name="brithday"  ext_type=date value="${cadetVO.brithday }"/>
    </td>
    <th valign="center" style="width:150px;">家庭地址 </th>
    <td valign="center" colspan="3" ><input type="text" name="address" id="address"  value="${cadetVO.address }">省市（县） </td>
    <th valign="center" >身份证号 </th>
    <td valign="center" colspan="3" >
      <input type="text" name="idcard" id="idcard"   value="${cadetVO.idcard }"/>
   </td>
  </tr>
  <tr >
    <th width="93" valign="center" rowspan="2" >何院校何专业何时毕业 </th>
    <th valign="center"  >院校名称 </th>
    <td colspan="2"><input type="text" name="grad_school" id="grad_school" value="${cadetVO.grad_school }"></td>
    <th valign="center" >专业 </th>
    <td colspan="5"><input type="text" name="grad_major" id="grad_major" value="${cadetVO.grad_major }"></td>
    
    
    
  </tr>
  <tr >

    <th valign="center"  >学制</th>
    <td valign="center" colspan="2" ><input type="text" name="grad_sys" id="grad_sys" value="${cadetVO.grad_sys }">年 </td>
    <th valign="center"  >学历</th>
    <td valign="center" ><input type="text" name="grad_formal" id="grad_formal" value="${cadetVO.grad_formal }"></td>
    <th valign="center"  >毕业年月 </th>
    <td valign="center"  colspan="3">
      <input type="text" id="grad_date" name="grad_date" ext_type=date value="${cadetVO.grad_date }"/> </td>
  </tr>
  <tr >
    <th width="93" valign="center" >学位（&#8730;)</th>
    <td valign="center" colspan="5" >
    <input type="checkbox" value=" 博士" name="grad_degrees" id="grad_degrees" value="${cadetVO.grad_degrees }" >博士
    <input type="checkbox" value=" 硕士" name="grad_degrees" id="grad_degrees" value="${cadetVO.grad_degrees }" >硕士
    <input type="checkbox" value=" 双学士" name="grad_degrees" id="grad_degrees" value="${cadetVO.grad_degrees }">双学士
    <input type="checkbox" value=" 学士" name="grad_degrees" id="grad_degrees" value="${cadetVO.grad_degrees }">学士 </td>
    <th valign="center"  >辅修专业 </th>
    <td valign="center" colspan="3" ><input type="text" name="grad_minor" id="grad_minor" value="${cadetVO.grad_minor }"></td>
  </tr>
  <tr >
    <th width="93" valign="center" >在班级或年级成绩名次 </th>
    <td valign="center" colspan="2" ><input type="text"  name="class_position" id="class_position" value="${cadetVO.class_position }"> </br>
      <input type="text" name="class_total" id="class_total" value="${cadetVO.idcard }"> </td>
    <td valign="center" colspan="2" > <input type="text"  name="grade_positoin" id="grade_positoin" value="${cadetVO.grade_positoin }"> </br>
      <input type="text" name="grade_total" id="grade_total" value="${cadetVO.idcard }"></td>
    <td valign="center" colspan="2" >高考分/一本线分：<input type="text" name="cee_score" id="cee_score" value="${cadetVO.cee_score }">/</br><input type="text" name="cee_rcc_score" id="cee_rcc_score"value="${cadetVO.sex }"></td>
    <td valign="center" colspan="3" >高校录取批次&nbsp;(&#8730;)&nbsp;： </br>
      <input type="radio" name="cee_batch" id="cee_batch" value="${cadetVO.cee_batch }">一本/
      <input type="radio" name="cee_batch" id="cee_batch" value="${cadetVO.cee_batch }">二本/
      <input type="radio" name="cee_batch" id="cee_batch" value="${cadetVO.cee_batch }">三本/
      <input type="radio" name="cee_batch" id="cee_batch" value="${cadetVO.cee_batch }">其他 </td>
  </tr>
  <tr >
    <th width="93" valign="center" >英语等级 </th>
    <td valign="center" colspan="2"  ><input type="text" name="cet_grade" id="cet_grade" value="${cadetVO.cet_grade }"></td>
    <th valign="center" >计算机等级 </th>
    <td valign="center" colspan="2"  ><input type="text" name="cpt_grade" id="cpt_grade" value="${cadetVO.cpt_grade }"></td>
    <th valign="center"  >下列已报考的标注(&#8730;)：</th><td colspan="3">
    <input type="checkbox" name="ext_degrees" id="ext_degrees"  value="${cadetVO.ext_degrees }">研究生/
    	<input type="checkbox" name="ext_degrees" id="ext_degrees" value="${cadetVO.ext_degrees }">公务员/
    	<input type="checkbox" name="ext_degrees" id="ext_degrees" value="${cadetVO.ext_degrees }">已考未取 </td>
  </tr>
 
    	
  </tr>
  <tr >
    <th width="93" valign="center" rowspan="2" >健康状况 </th>
    <th valign="center"  >身高 </th><td><input type="text" name="body_height" id="body_height" value="${cadetVO.body_height }"></td>
    <th valign="center"  >体重 </th><td><input type="text" name="body_weight" id="body_weight" value="${cadetVO.body_weight }"></td>
    <th width="40" valign="center" >视力 </th><td><input type="text" name="body_vision" id="body_vision" value="${cadetVO.body_vision }"></td>
    
    <th valign="center"  >婚否 </th>
    <td colspan="2">
         <select name="marry_state" id="marry_state" value="${cadetVO.marry_state }">
    		<option value="已婚" >已婚</option>
    		<option value="未婚" >未婚</option>
    	</select></td>
  </tr>
  <tr >
   <th valign="center"  >健康情况 </th><td><input type="text" name="body_state" id="body_state" value="${cadetVO.body_state }"></td>
    
  </tr>
  <tr >
    <th width="93" valign="center" rowspan="2" >实习时间 </th>
    <th valign="center"  >计划:</th><td><input type="text" id="prac_plan_start_date" name="prac_plan_start_date"  ext_type=date value="${cadetVO.prac_plan_start_date }"/>
  	</td><td>至</td><td colspan="6"><input type="text" id="prac_plan_end_date" name="prac_plan_end_date"  ext_type=date value="${cadetVO.prac_plan_end_date }" /></td>
    </td>
  </tr>
  <tr >
    <th valign="center" >实际：</th><td><input type="text" id="prac_real_start_date" name="prac_real_start_date"  ext_type=date value="${cadetVO.prac_real_start_date }"/>
	 </td><td>至</td><td colspan="6"><input type="text" id="prac_real_end_date" name="prac_real_end_date"  ext_type=date value="${cadetVO.prac_real_end_date }"/>
    </td>
  </tr>
  <tr >
    <th width="93" valign="center" rowspan="5" >学习简历 
      （初中入学起）</th>
    <th valign="center" colspan="2" >何年何月-何年何月 </th>
    <th valign="center" colspan="7" >何地/何校/何专业/何学历</th>
  </tr>
  <tr >
    <td valign="center" ><input type="text" name="educ_start_date_1" id="educ_start_date_1" ext_type=date value="${cadetVO.educ_start_date_1 }">
    </td><td><input type="text" name="educ_end_date_1" id="educ_end_date_1" ext_type=date value="${cadetVO.educ_end_date_1 }"></td>
    <td valign="center" colspan="7" ><input type="text" name="educ_inof_1" id="educ_inof_1" style="width: 800px" value="${cadetVO.educ_inof_1 }" ></td>
  </tr>
  <tr >
    <td valign="center" ><input type="text" name="educ_start_date_2" id="educ_start_date_2" ext_type=date value="${cadetVO.educ_start_date_2 }">
    </td><td><input type="text" name="educ_end_date_2" id="educ_end_date_2" ext_type=date value="${cadetVO.educ_end_date_2 }" ></td>
    <td valign="center" colspan="7" ><input type="text" name="educ_inof_2" id="educ_inof_2" style="width: 800px" value="${cadetVO.educ_inof_2 }" ></td>
  </tr>
  <tr >
    <td valign="center"  ><input type="text" name="educ_start_date_3" id="educ_start_date_3" ext_type=date value="${cadetVO.educ_start_date_3 }">
    </td><td><input type="text"  name="educ_end_date_3" id="educ_end_date_3" ext_type=date value="${cadetVO.educ_end_date_3 }"></td>
    <td valign="center" colspan="7" ><input type="text" name="educ_inof_3" id="educ_inof_3" style="width: 800px" value="${cadetVO.educ_inof_3 }"></td>
  </tr>
  <tr >
    <td valign="center"  ><input type="text" name="educ_start_date_4" id="educ_start_date_4" ext_type=date value="${cadetVO.educ_start_date_4 }">
    </td><td><input type="text" name="educ_end_date_4" id="educ_end_date_4" ext_type=date value="${cadetVO.educ_end_date_4 }"></td>
    <td valign="center" colspan="7" ><input type="text" name="educ_inof_4" id="educ_inof_4" style="width: 800px" value="${cadetVO.educ_inof_4 }"></td>
  </tr>
  <tr >
    <th width="93" valign="center" rowspan="5" >社会实践 
      经历 </th>
    <th valign="center" colspan="2" >何年何月-何年何月 </th>
    <th valign="center" colspan="7" >何地/何处/何岗位 </th>
  </tr>
  <tr >
    <td valign="center" ><input type="text" name="soc_start_date_1" id="soc_start_date_1" ext_type=date value="${cadetVO.soc_start_date_1 }">
    </td><td><input type="text" name="soc_end_date_1" id="soc_end_date_1" ext_type=date value="${cadetVO.soc_end_date_1 }"></td>
    <td valign="center" colspan="7" ><input type="text" name="soc_inof_1" id="soc_inof_1" style="width: 800px" value="${cadetVO.soc_inof_1 }"></td>
  </tr>
  <tr >
    <td valign="center" ><input type="text" name="soc_start_date_2" id="soc_start_date_2" ext_type=date value="${cadetVO.soc_start_date_2 }">
    </td><td><input type="text" name="soc_end_date_2" id="soc_end_date_2" ext_type=date value="${cadetVO.soc_end_date_2 }" ></td>
    <td valign="center" colspan="7" ><input type="text" name="soc_inof_2" id="soc_inof_2" style="width: 800px" value="${cadetVO.soc_inof_2 }"></td>
  </tr>
  <tr >
    <td valign="center" ><input type="text" name="soc_start_date_3" id="soc_start_date_3" ext_type=date value="${cadetVO.soc_start_date_3 }">
    </td><td><input type="text" name="soc_end_date_3" id="soc_end_date_3" ext_type=date value="${cadetVO.soc_end_date_3 }"></td>
    <td valign="center" colspan="7" ><input type="text" name="soc_inof_3" id="soc_inof_3" style="width: 800px" value="${cadetVO.soc_inof_3 }"></td>
  </tr>
  <tr >
    <td valign="center"  ><input type="text" name="soc_start_date_4" id="soc_start_date_4" ext_type=date value="${cadetVO.soc_start_date_4 }">
    </td><td><input type="text" name="soc_end_date_4" id="soc_end_date_4" ext_type=date value="${cadetVO.soc_end_date_4 }"></td>
    <td valign="center" colspan="7" ><input type="text" name="soc_inof_4" id="soc_inof_4" style="width: 800px" value="${cadetVO.soc_inof_4 }"></td>
  </tr>
  <tr >
    <th width="93" valign="center" >执业资格</th>
    <td valign="center" colspan="9" ><p >注册会计师统考：<input type="text" name="cpa_pass_1" id="cpa_pass_1" value="${cadetVO.cpa_pass_1 }">、
    <input type="text" name="cpa_pass_2" id="cpa_pass_2" value="${cadetVO.cpa_pass_2 }">、
    <input type="text" name="cpa_pass_3" id="cpa_pass_3" value="${cadetVO.cpa_pass_3 }">、
    <input type="text" name="cpa_pass_4" id="cpa_pass_4" value="${cadetVO.cpa_pass_4 }">、
   <input type="text" name="cpa_pass_5" id="cpa_pass_5" value="${cadetVO.cpa_pass_5 }">等<input type="text" name="cpa_pass_count" id="cpa_pass_count" value="${cadetVO.sex }">门合格； </p>
      <p ><input type="text" name="cpa_attend_1" id="cpa_attend_1" value="${cadetVO.cpa_attend_1 }">、
      <input type="text" name="cpa_attend_2" id="cpa_attend_2" value="${cadetVO.cpa_attend_2 }">、
      <input type="text" name="cpa_attend_3" id="cpa_attend_3" value="${cadetVO.cpa_attend_3 }">、
      <input type="text" name="cpa_attend_4" id="cpa_attend_4" value="${cadetVO.cpa_attend_4 }">、
      <input type="text" name="cpa_attend_5" id="cpa_attend_5" value="${cadetVO.cpa_attend_5 }">等<input type="text" name="cpa_attend_count" id="cpa_attend_count" value="${cadetVO.cpa_attend_count }">门已考，成绩待公布。 </p>
      <p >其他执业资格：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;（附送成绩单或证书复印件） </p></td>
  </tr>
  <tr >
    <th width="93" valign="center" rowspan="2" >通讯地址 
      及电话 
     （请详细填写）</th>
    <th valign="center"  >学校通讯地址 </th>
    <td valign="center" colspan="8" ><input type="text" name="conn_scholl_address" id="conn_scholl_address" value="${cadetVO.conn_scholl_address }"></td>
    
    
  </tr>
  <tr >
    <th valign="center"  >E-mail </th>
    <td valign="center"  ><input type="text" name="conn_email" id="conn_email" value="${cadetVO.conn_email }"></td>
    <th valign="center" >邮&nbsp;&nbsp;编 </th>
    <td valign="center"  ><input type="text" name="conn_zip" id="conn_zip" value="${cadetVO.conn_zip }"></td>
    <th valign="center"  >电&nbsp;&nbsp;话</th>
    <td valign="center"  ><input type="text" name="conn_phone" id="conn_phone" value="${cadetVO.conn_phone }"></td>
    <th valign="center"  >手&nbsp;&nbsp;机 </th>
    <td valign="center" colspan="2"><input type="text" name="conn_mobile" id="conn_mobile" value="${cadetVO.conn_mobile }"></td>
  </tr>
  <tr>
  <td valign="center" colspan="5"><input type="submit" value="提交" name="submit" id="submit"></td>
  <td valign="center" colspan="5"><input type="reset" value="重置" name="reset" id="reset" ></td>
  </tr>
  </tbody>
</table>
   </form> 
</body>

</html>