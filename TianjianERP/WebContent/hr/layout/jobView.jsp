<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<link href="${pageContext.request.contextPath}/AS_CSS/style.css" rel="stylesheet" type="text/css"  />


<div style="height:expression(document.body.clientHeight-30);overflow: auto;" >

<input name="flag" type="hidden" id="flag" value="${flag}">
<input name="table" type="hidden" id="table" value="${table}">
<input name="unid" type="hidden" id="unid" value="${edit.unid }">
<input type="hidden" name="who" id="who" value="${who}">

	<table  cellpadding="8" cellspacing="0" align="center" class="formTable" >
	
	<tr>
	  <th class="data_tb_alignright"  width="15%" align="right">总部/分所<span class="mustSpan">[*]</span>：</th>
	  <td  class="data_tb_content" width="35%">
	  		<input type="hidden" value='${edit.areaid }' name='areaid'>
	  		<input type="hidden" value='${edit.areaname }' name=areaname>${edit.areaname }
	  <th class="data_tb_alignright"  width="35%" align="right">部门<span class="mustSpan">[*]</span>：</th>
	  <td class="data_tb_content" width="15%">
	  		<input type="hidden" value='${edit.departmentid }' name='departmentid'>
	  		<input type="hidden" value='${edit.departmentname }' name='departmentname'>${edit.departmentname }
	  		
	  		<input type="hidden" value='已审批' name='status'>
	  </td>
	</tr>	
	<tr>
	 
	</tr>	
	<tr>
	  <th class="data_tb_alignright"  width="15%" align="right">岗位名称<span class="mustSpan">[*]</span>：</th>
	  <td  class="data_tb_content"><input class="required" value='${edit.jobname }' name="jobname" type="text" id="jobname" size=40 class="required"></td>
	   <th class="data_tb_alignright"  width="15%" align="right">状态<span class="mustSpan">[*]</span>：</th>
	  <td  class="data_tb_content" width="25%">
			${edit.state}
	  </td>
	</tr>
	<tr>
	  <th class="data_tb_alignright"  width="15%" align="right">招聘人数<span class="mustSpan">[*]</span>：</th>
	  <td  class="data_tb_content" >
	  	<input class="validate-positiveInt"  validchar="0123456789"
	  		 onkeypress="return blockChar(this)" onpaste="return false" value='${edit.peoplecount }' name="peoplecount" type="text" id="peoplecount"  
	  		 class="required">
	  </td>
	  <th class="data_tb_alignright"  width="15%" align="right">招聘类型<span class="mustSpan">[*]</span>：</th>
	  <td class="data_tb_content" width="25%">
	  	${edit.type}
	  </td>
	</tr>		
	<tr>
	  <th class="data_tb_alignright"  width="15%" align="right">资历要求：</th>
	  <td  class="data_tb_content" width="35%">
	  	<input value='${edit.qualifications }' name="qualifications" type="text" id="qualifications" 
	  	multiselect="true"  autoid=700 refer='资历' onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" 
	  	noinput=true autoHeight=150 ></td>
	 <th class="data_tb_alignright"  width="15%" align="right">学历要求：</th>
	  <td  class="data_tb_content" width="35%">
	  	<input value='${edit.education }' name="education" type="text" id="education" autoid=700 refer='学历' 
	  	onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true noinput=true autoHeight=150 
	  	></td>	  
	</tr>	
	<tr>
	  <th class="data_tb_alignright"  width="15%" align="right">到岗时间要求：</th>
	  <td  class="data_tb_content"  ><input value='${edit.toworktime }' name="toworktime" type="text" id="toworktime" ></td>
	   <th class="data_tb_alignright"  width="15%" align="right">证书要求：</th>
	  <td  class="data_tb_content" width="35%"><input value='${edit.certificate }' name="certificate" type="text" id="certificate" multiselect="true" 
	  autoid=700 refer='证书' onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" 
	  noinput=true autoHeight=150 ></td>
	</tr>
	<tr>
	  <th class="data_tb_alignright"  width="15%" align="right">工作城市：</th>
	  <td  class="data_tb_content" width="35%"><input value='${edit.city }' name="city" type="text" id="city" autoid=740 multilevel=true 
	  	onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true noinput=true 
	  	autoHeight=150 </td>  
	  <th class="data_tb_alignright"  width="15%" align="right">工时要求：</th>
	  <td  class="data_tb_content">
			${edit.working }
	  </td>
	</tr>	
	<tr> 
	  <th class="data_tb_alignright"  width="15%" align="right">招聘要求<span class="mustSpan">[*]</span>：</th>
	  <td  class="data_tb_content" colspan="3"><textarea name="remark" id="remark" cols="100" rows="10" maxlength="500"  onkeyup="if(this.value.length>500)this.value=this.value.substring(0,500);">${edit.remark}</textarea></td>
	</tr>
	<!--  
	<tr> 
	  <th class="data_tb_alignright"  width="15%" align="right">招聘原因：</th>
	  <td  class="data_tb_content" colspan="3"><textarea name="reason" id="reason" cols="100" rows="10" maxlength="500"  >${edit.reason}</textarea></td>
	</tr>
	<tr>
	  <th class="data_tb_alignright"  width="15%" align="right">最后修改人：</th>
	  <td  class="data_tb_content" width="35%"><input class="before" readonly value='${edit.lastuser }' name="lastuser" type="text" id="lastuser" ></td>
	  <th class="data_tb_alignright"  width="15%" align="right">最后修改时间</th>
	  <td  class="data_tb_content" width="35%"><input class="before" readonly value='${edit.lasttime }' name="lasttime" type="text" id="lasttime" ></td>
	</tr>
	-->		
	</table>


</div>
