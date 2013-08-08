<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link href="../AS_CSS/style.css" rel="stylesheet" type="text/css"  />
<style type="text/css">
  .formTable{
    padding: 0px 0px 0px 0px;
    width: 100%;
  }
</style>
<script type="text/javascript">


Ext.onReady(function (){
	new Ext.Toolbar({
			renderTo: "divBtn",
			height:30,
			defaults: {autoHeight: true,autoWidth:true},
	       items:[{ 
	           id:'saveBtn',
	           text:'保存',
	           icon:'${pageContext.request.contextPath}/img/save.gif' ,
	           handler:function(){
	        	   if (!formSubmitCheck('saveBtn')){
	        	   		return;
	        	   }else{
				   		mySubmit();
				   }
			   }
	     	 },'-',{ 
	        text:'返回',
	        icon:'${pageContext.request.contextPath}/img/back.gif', 
	        handler:function(){
				window.history.back();
			}
	  	},'->']
	});
	 
	new Ext.form.DateField({
		applyTo : 'birthday',
		width: 133,
		format: 'Y-m-d'
	});
	new Ext.form.DateField({
		applyTo : 'afterschooltime',
		width: 133,
		format: 'Y-m-d'
	});
	new Ext.form.DateField({
		applyTo : 'labour_sign_date',
		width: 133,
		format: 'Y-m-d'
	});
	new Ext.form.DateField({
		applyTo : 'jobtime',
		width: 133,
		format: 'Y-m'
	});
});
</script>

</head>


<body>
<div id="divBtn" ></div>
   <form name="thisForm" action="" method="post">
      <table>
        <tbody>
         <tr>
            <td >
               <table class="formTable">
                 <tbody>
                  <tr>
                     <th>姓名</th>
                     <td><input name="name" value="${vo.name}"/></td>
                     <th>性别</th>
                     <td><input name="gender" /></td>
                     <th>民族</th>
                     <td><input name="nation"  value="${vo.nation}" /></td>
                     <th>出生日期</th>
                     <td><input name="birthday" id="birthday" value="${vo.birthday}"/></td>
                  </tr>
                  <tr>
                    <th>籍贯 </th>
                     <td><input name="nativeplace" value="${vo.nativeplace}"/></td>
                     <th>家庭所在地 </th>
                     <td colspan="3"><input name="address" value="${vo.address}"/></td>
                     <th>身份证号</th>
                     <td><input name="id" value="${vo.id}"/></td>
                     
                  </tr>
                  </tbody>
               </table>
            </td>
         </tr>
         
         <tr>
            <td  >
               <table class="formTable" style="width: 100%">
                  <thead>
                    <tr>
                      <th>毕业院校</th>
                      <th>专业</th>
                      <th>学制</th>
                      <th>毕业时间</th>
                      <th>学位</th>
                      <th>学历</th>
                    </tr>
                    <tr>
                    	<td><input name="graduation" type="text" value="${vo.graduation}"/></td>
                    	<td><input name="profession" type="text" value="${vo.profession}"/></td>
                    	<td><input name="educationyears" type="text" value="${vo.educationyears}"/></td>
                    	<td><input name="afterschooltime" type="text" value="${vo.afterschooltime}" id="afterschooltime"/></td>
                    	<td><input name="degree" type="text" value="${vo.degree}"/></td>
                    	<td><input name="degrees" type="text" value="${vo.degrees}"/></td>
                    </tr>
                  </thead>
               </table>
            </td>
         </tr>
         
         <tr>
            <td>
               <table class="formTable">
                  <tbody>
                    <tr>
                       <th> 参加工作时间</th>
                       <td> <input name="jobtime" value="${vo.jobtime}" id="jobtime" /></td>
                        <th> 技术职称</th>
                       <td> <input name="technology" value="${vo.technology}" /></td>
                        <th>取得时间 </th>
                       <td> <input name="gettechnologytime" value="${vo.gettechnologytime}" /></td>
                    </tr>
                    <tr>
                        <th>现在何单位何部门工作 </th>
                       <td> <input name="nowdepartname" value="${vo.nowdepartname}" /></td>
                        <th>现任职务 </th>
                       <td> <input name="nowposition" value="${vo.nowposition}" /></td>
                        <th>政治面貌 </th>
                       <td> <input name="political" value="${vo.political}" /></td>
                    </tr>
                  </tbody>
               </table>
            </td>
         </tr>
         <tr>
            <td>
              <table class="formTable">
                <tbody>
                  <tr>
                  <th rowspan="6">何时取得何种职业资格</th>
                  <th>职业资格</th>
                  <th>是否获取</th>
                  <th>何时取得</th>
                  </tr>
                  <tr>
                    <th>注册会计师</th>
                    <td><input name="CPA" value="${vo.CPA}" /></td>
                    <td><input name="CPATIME" value="${vo.CPATIME}" /></td>
                  </tr>
                   <tr>
                    <th>注册税务师 </th>
                    <td><input name="CTA" value="${vo.CTA }" /></td>
                    <td><input name="CTATIME" value="${vo.CTATIME}" /></td>
                  </tr>
                   <tr>
                    <th>注册资产评估师 </th>
                    <td><input name="CPV" value="${vo.CPV}" /></td>
                    <td><input name="CPVTIME" value="${vo.CPVTIME}" /></td>
                  </tr>
                   <tr>
                    <th> 注册造价师</th>
                    <td><input name="CCD" value="${vo.CCD}" /></td>
                    <td><input name=CCDTIME" value="${vo.CCDTIME}" /></td>
                  </tr>
                   <tr>
                    <th> 注册造价员</th>
                    <td><input name="CCM" value="${vo.CCM}" /></td>
                    <td><input name="CCMTIME" value="${vo.CCMTIME}" /></td>
                  </tr>
                   
                  
                </tbody>
              </table>
            </td>
         </tr>
         
         <tr>
           <td>
              <table class="formTable">
                <tbody>
                  <tr>
                     <th>掌握计算机程度 </th>
                     <td><input name="computerdegree" value="${vo.computerdegree}" /></td>
                     
                      <th>掌握外语 </th>
                     <td><input name="englishdegree" value="${vo.englishdegree}" /></td>
                     
                      <th>外语等级 </th>
                     <td><input name="englishlevel" value="${vo.englishlevel}" /></td>
                  </tr>
                  
                  <tr>
                     <th rowspan="2">健康状况 </th>
                     <th>身高</th>
                     <th>体重</th>
                     <th>视力</th>
                     <th colspan="2">薪资期望（年薪）</th>
                  </tr>
                  
                  <tr>
                     
                     <td><input name="heigth" value="${vo.heigth}" /></td>
                     <td><input name="weigth" value="${vo.weigth}" /></td>
                     <td><input name="eye" value="${vo.eye}" /></td>
                     <td colspan="2"><input name="salary" value="${vo.salary}" /></td>
                  </tr>
                  
                      <tr>
                     <th rowspan="2">婚姻状况 </th>
                     <th>配偶姓名</th>
                     <th colspan="2">在何单位何部门任何职</th>
                     <th>子女姓名</th>
                     <th colspan="2">年龄</th>
                  </tr>
                  
                  <tr> 
                     
                     <td><input name="othername" value="${vo.othername}" /></td>
                     <td colspan="2"><input name="otherdepart" value="${vo.otherdepart}" /></td>
                     <td><input name="childrenname" value="${vo.childrenname}" /></td>
                     <td><input name="childrenage" value="${vo.childrenage}" /></td>
                  </tr>
                  
                  <tr>
                    <th>家庭其他成员及主要社会关系</th>
                    <td colspan="5"><textarea rows="5" name="peoplerelation">${vo.peoplerelation}</textarea></td>
                  </tr>
                  
                    <tr>
                    <th>本人或配偶的房产情况</th>
                    <td colspan="5"><textarea rows="5" name="housestation">${vo.housestation}</textarea></td>
                  </tr>
                  
                  <tr>
                    <th rowspan="4">学习简历（从高中起，何年何月至何年何月在何地何校何专业学习）</th>
                    <td colspan="5"><input name="studyrelation_1" value="${vo.studyrelation_1}" /></td>
                  </tr>
                  
                  <tr>
                  <td colspan="5"><input name="studyrelation_2" value="${vo.studyrelation_2}" /></td>
                  </tr>
                   <tr>
                  <td colspan="5"><input name="studyrelation_3" value="${vo.studyrelation_3}" /></td>
                  </tr>
                   <tr>
                  <td colspan="5"><input name="studyrelation_4" value="${vo.studyrelation_4}" /></td>
                  </tr>
                  
                  <tr>
                    <th rowspan="3">工作简历</th>
                    <th>起止日期</th>
                    <th>工作单位</th>
                    <th>从事岗位</th>
                    <th>职务</th>
                  </tr>
                  
                  <tr>
                    <td><input name="worktime_1" value="${vo.worktime_1}" /></td>
                    <td><input name="department_1" value="${vo.department_1}" /></td>
                    <td><input name="role_1" value="${vo.role_1}" /></td>
                    <td><input name="position_1" value="${vo.position_1}" /></td>
                  </tr>
                  
                  <tr>
                    <td><input name="worktime_2" value="${vo.worktime_2}" /></td>
                    <td><input name="department_2" value="${vo.department_2}" /></td>
                    <td><input name="role_2" value="${vo.role_2}" /></td>
                    <td><input name="position_2" value="${vo.position_2}" /></td>
                  </tr>
                  
                  <tr>
                    <th>主要工作业绩或完成的代表性项目简介</th>
                    <td colspan="5"><textarea rows="5" name="standard">${vo.standard}</textarea></td>
                  </tr>
                  
                  
                  
                </tbody>
               
              </table>
           </td>
         </tr>
         
         <tr>
            <td>
            
               <table class="formTable">
                 <tbody>
                   <tr>
                     <th rowspan="2">通讯地址联系电话</th>
                     <th>详细通讯地址</th>
                     <td colspan="5"><input name="detaileaddress" value="${vo.detaileaddress}"/></td>
                   </tr>
                   <tr>
                     <th>Email</th>
                     <td><input name="email" value="${vo.email}" /></td>
                     <th>电话</th>
                     <td><input name="phone" value="${vo.phone}" /></td>
                     <th>手机</th>
                     <td><input name="tel" value="${vo.tel}" /></td>
                   </tr>
                   
                   <tr>
                     <th>求职岗位</th>
                     <td colspan="6"><textarea rows="3" name="wantjob">${vo.wantjob}</textarea></td>
                   </tr>
                   
                   <tr>
                     <th>备注</th>
                     <td colspan="6">
                      1.人事档案在<input name="personelfit" value="${vo.personelfit}"/>；2.养老保险<input name="endowment" value="${vo.endowment}"/>3.户口在<input name="account" value="${vo.account}"/>；4，与现单位已（未）签劳动合同，于<input name="labour_sign_date" id="labour_sign_date" value="${vo.labour_sign_date}"/>到期。
                     
                     </td>
                   </tr>
                    
                 </tbody>
               </table>
            </td>
         </tr>
         
         </tbody>
      </table>
   </form>
   
</body>
</html>