<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
    <%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
<link href="../AS_CSS/style.css" rel="stylesheet" type="text/css"  />
<style type="text/css">
  .formTable{
    padding: 0px 0px 0px 0px;
    width: 100%;
  }
  
  .cen{
    text-align: center;
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
				   		save();
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
		applyTo :'afterschooltime',
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
<div style="overflow: scroll;height: 100%">
<div id="divBtn" ></div>
	<form name="thisForm" action="" method="post">
		<table class="formTable" style="width: 100%">
			<thead>
				<tr>
					<td><input name="type" id="type" type="hidden" value="1"/></td>
					<td><input name="uuid" id="uuid" type="hidden" value="${vo.uuid}"/></td>
					<th>姓名</th>
						<td><input name="name" type="text" value="${vo.name}"/></td>
					<th>性别</th>
					<td>
						<select id="gender" name="gender">
      	  					<option value="男" <c:if test="${vo.gender=='男' }">selected</c:if> >男</option>
      	  					<option value="女" <c:if test="${vo.gender=='女' }">selected</c:if> >女</option>
      					</select>
					</td>
					<th>籍贯</th>
					<td><input name="nativeplace" value="${vo.nativeplace}"/></td>
					<th>民族</th>
					<td><input name="nation" value="${vo.nation}"/></td>
					<th>党/团员</th>
					<td><input name="political" value="${vo.political}"/>
				</tr>
				<tr>
					<th>出生日期</th>
					<td colspan="3"><input name="birthday" id="birthday" value="${vo.birthday}"/></td>
					<th>家庭住址</th>
					<td colspan="2"><input name="address" value="${vo.address}"/></td>
					<th>身份证号</th>
					<td><input name="id" value="${vo.id}"></td>
				</tr>
				<tr>
					<th rowspan="2">何院校何专业何时毕业</th>
					<th colspan="3">院校名称</th>
					<th colspan="2" style="text-align: center">专业</th>
					<th>学制</th>
					<th>学历</th>
					<th colspan="2">毕业年月</th>
				</tr>
				<tr>
					<td colspan="3"><input name="graduation" value="${vo.graduation}" /></td>
					<td colspan="2"><input name="profession" value="${vo.profession}" /></td>
					<td ><input name="educationyears" value="${vo.educationyears}" /></td>
					<td ><input name="degrees" value="${vo.degrees}" /></td>
					<td colspan="2"><input name="afterschooltime" id="afterschooltime" value="${vo.afterschooltime}" /></td>
				</tr>
				<tr>
					<th>辅修专业</th>
					<td colspan="7"><textarea name="otherprofession" >${vo.otherprofession}</textarea></td><!--  -->
					<th>学位</th>
					<td><input name="degree" value="${vo.gegree}" /></td>
				</tr>
				<tr>
					<th >英语等级</th>
					<td><input name="englishlevel" value="${vo.englishlevel}" /></td>
					<th>计算机等级</th>
					<td ><input name="computerdegree" value="${vo.computerdegree}" /></td>
					<th>友列已报考的标注（√）</th>
					<td>
					 	<input id="Checkbox5" name="examremark" value="研究生" type="checkbox" />研究生&nbsp;&nbsp;
					</td>
					<td><input id="Checkbox6" name="examremark" value="公务员" type="checkbox" />公务员&nbsp;&nbsp;</td>
					<td><input id="Checkbox7" name="examremark" value="已考未取" type="checkbox" />已考未取&nbsp;&nbsp;</td>
				</tr>
							<tr>
					<th rowspan="2">健康状况</th>
					<th colspan="2">身高</th>
					<th colspan="2">体重</th>
					<th>视力</th>
					<th colspan="2">期望薪资（年薪）</th>
					<th rowspan="2">婚否</th>
				</tr>
				<tr>
					<td colspan="2"><input name="heigth" value="${vo.heigth}"/></td>
					<td colspan="2"><input name="weigth" value="${vo.weigth}"/></td>
					<td><input name="eye" value="${vo.eye}"/></td>
					<td colspan="2"><input name="salary" value="${vo.salary}"/>
					<td >
						<select id="married" name="married">
      	  					<option value="已婚" <c:if test="${vo.married=='已婚' }">selected</c:if> >已婚</option>
      	  					<option value="未婚" <c:if test="${vo.married=='未婚' }">selected</c:if> >未婚</option>
      					</select>
					</td>					
				</tr>
				<tr>
					<th rowspan="5">学习简历<br>(初中入学起)</th>
					<th colspan="3">何年何月-到何年何月</th>
					
					<th colspan="6">何地/何校/何专业/何学历</th>
				</tr>
				<tr>
					<td colspan="3" ><input name="studytime1" value="${vo.studytime1}"/></td>
					<td colspan="6"><input name="departdet1" value="${vo.departdet1}"/></td>
				</tr>
				<tr>
					<td colspan="3" ><input name="studytime2" value="${vo.studytime2}"/></td>
					<td colspan="6"><input name="departdet2" value="${vo.departdet2}"/></td>
				</tr>
				<tr>
					<td colspan="3" ><input name="studytime3" value="${vo.studytime3}"/></td>
					<td colspan="6"><input name="departdet3" value="${vo.departdet3}"/></td>
				</tr>
				<tr>
					<td colspan="3" ><input name="studytime4" value="${vo.studytime4}"/></td>
					<td colspan="6"><input name="departdet4" value="${vo.departdet4}"/></td>
				</tr>
				<tr>
					<th rowspan="5">社会实践经历</th>
					<th colspan="3">何年何月-到何年何月</th>
					
					<th colspan="6">何地/何岗位</th>
				</tr>
				<tr>
					<td colspan="3" ><input name="socialtime1" value="${vo.socialtime1}"/></td>
					<td colspan="6"><input name="socialdet1" value="${vo.socialdet1}"/></td>
				</tr>
				<tr>
					<td colspan="3" ><input name="socialtime2" value="${vo.socialtime2}"/></td>
					<td colspan="6"><input name="socialdet2" value="${vo.socialdet2}"/></td>
				</tr>
				<tr>
					<td colspan="3" ><input name="socialtime3" value="${vo.socialtime3}"/></td>
					<td colspan="6"><input name="socialdet3" value="${vo.socialdet3}"/></td>
				</tr>
				<tr>
					<td colspan="3" ><input name="socialtime4" value="${vo.socialtime4}"/></td>
					<td colspan="6"><input name="socialdet4" value="${vo.socialdet4}"/></td>
				</tr>
				 <tr> 
             		<th>职业资格</th>
             		<td colspan="7">注册会计师统考有：<input name="qualification" value="${vo.qualification}" title="填写合格的会计师统考门数"/> 合格/>
                </tr>
                <tr>
                	<th rowspan="2">通信地址及电话</th>
                	<th colspan="3">学校地址</th>
                	<th>邮编</th>
                	<th colspan="2">Email</th>
                	<th>固定电话</th>
                	<th colspan="2">手机</th>
                </tr>
                <tr>
                	<td colspan="3"><input name="schooleaddress" value="${vo.schooleaddress" /></td>
                	<td><input name="post" value="${vo.post}" /></td>
                	<td colspan="2"><input name="email" value="${vo.email}" /></td>
                	<td><input name="phone" value="${vo.phone}" /></td>
                	<td colspan="2"><input name="tel" value="${vo.tel}" /></td>
                </tr>
                <tr>
                	<th >性格特长描述</th>
                	<td colspan="8"><textarea rows="4" name="specialty">${vo.specialty}</textarea>
                </tr>
                <tr>
                	<th>个人自愿排序</th>
                	<td colspan="3"><input name="volunteer"/> </td>
                	<td colspan="4">1，考研  2，考公务员  3，出国深造  4，天健  5，其他</td>
                </tr>
                <tr>
                	<th>求职岗位</th>
                	<td colspan="3"> <input name="wantjob" value="${vo.wantjob}"/></td>
                </tr>
                <tr>
                	<th>本人申明</th>
                	<td colspan="7">是否与本所在职员工有近亲属关系，如有请申明：<textarea name="statement">${vo.statement}</textarea></td>
                </tr>
                <tr>
                	<th>实习安排</th>
                	<td colspan="8">时间：<input name="plantimebegin" value="${vo.plantimebegin}" id="plantimebegin" style="width:150px"/>至:<input name="plantimeendtime" id="plantimeendtime" value="${vo.plantimeendtime }" style="width:150px"/>
                		(尽早安排；自行解决住宿问题)
                	</td>
                </tr>
			</thesd>
		</table>
		
	</form>
</div>
<script type="text/javascript">
new Validation('thisForm');

function save() {
		thisForm.action="${pageContext.request.contextPath}/resume.do?method=add";
		document.thisForm.submit();
}

</script>

</body>
</html>