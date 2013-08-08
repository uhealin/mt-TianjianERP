<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>业务报告</title>
<style type="text/css">
td div,th div{
float: left;

}
input{
width: 95%;
	}
</style>
<script type="text/javascript">
function ext_init(){
	
    var tbar = new Ext.Toolbar({
   		renderTo: "divBtn",
   		defaults: {autoHeight: true,autoWidth:true},
        items:[{
            text:'返回',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/back.gif',
            handler:function(){
				window.history.back();
			}
   		},'->'
		]
    });
    
}
window.attachEvent('onload',ext_init);
</script>
</head>
<body>
<div id="divBtn"></div>
<form action="${pageContext.request.contextPath}/businessreport.do?method=auditBusinesReport" method="post" name="thisForm">
<input type="hidden" value="" id="hiddenuuid" name="hiddenuuid"/>
<input type="hidden" value="no" id="hiddentype" name="hiddentype"/>
<input type="hidden" id="uuid" name="uuid" value="${businessReportVO.uuid }" />
<table class="formTable">
	<thead>
		<tr>
			<th colspan="5">
				项目质量控制复核预约表：</th>
		</tr>
	</thead>
	<tbody>

		<tr>
			<th style="text-align: left" width="17%">
				公司名称
			</th>
			<td width="17%">
				<input id="company_Name"  class="required" name="company_Name" type="text"  value="${businessReportVO.company_Name }" />
			</td>
			<th style="text-align: left"  width="17%">
				报告类型[注5]
			</th>
			<td colspan="2" width="30%">
				<input id="report_type" name="report_type"  class="required"  type="text"  value="${businessReportVO.report_type }" />
			</td>
			<th rowspan="12">
<div >
1.公司类型为：上市公司、<br>
国有企业、证券公司、期货<br>
公司、拟上市公司、一般企<br>
业、外商投资企业等；若公<br>
司上市公司/国有企业/拟上<br>
市公司的子公司，建议注明<br>
“上市公司/国有企业/拟上<br>
市公司的子公司”。 <br>
2.报告期间发生的重大或异<br>
常交易：在报告期间公司发<br>
生的企业合并或其他重大交<br>
易事项。 <br>
3.送审时间：业务报告完成<br>
一二级复核后，送至技术部<br>
的时间。 <br>
4.到期时间：希望技术部完<br>
成复核的时间。 <br>
5.报告类型分为：年度审计<br>
报告、申报报告。<br> 

</div>
			
			 </th>
		</tr>
		<tr>
			<th style="text-align: left">
				公司类型[注1]</th>
			<td>
				<input id="company_type" class="required"   name="company_type" type="text"  value="${businessReportVO.company_type }" /></td>
			<th style="text-align: left">
				报告期间</th>
			<td colspan="2">
				 <input id="report_BeginTime"  class="required"  name="report_BeginTime" type="text"  value="${businessReportVO.report_BeginTime }"  />
				 <div style="display: none;"><div>至</div>
				  <input id="report_EndTime"  class="required"  name="report_EndTime" type="text"  value="${businessReportVO.report_EndTime }" />
				  </div>
				  </td>
				  
		</tr>
		<tr>
			<th style="text-align: left">
				主营业务</th>
			<td>
				<input id="major_Business" class="required"   name="major_Business" type="text"  value="${businessReportVO.major_Business }" /></td>
			<th style="text-align: left">
				报告意见类型</th>
			<td colspan="2">
				<input id="report_Suggestion_Type" class="required"   name="report_Suggestion_Type" type="text"  value="${businessReportVO.report_Suggestion_Type }" /></td>
		</tr>
		<tr>
			<th style="text-align: left">
				控股股东或实际控制人</th>
			<td>
				<input id="stockholder" name="stockholder" type="text"  value="${businessReportVO.stockholder }" /></td>
			<th style="text-align: left">
				报告用途</th>
			<td colspan="2">
				<input id="report_use" name="report_use" type="text"  value="${businessReportVO.report_use }" /></td>
		</tr>
		<tr>
			<th style="text-align: left">
				是否为集团内重要组成部分</th>
			<td>
				<input id="group_major_part_ind" name="group_major_part_ind" type="text"  value="${businessReportVO.group_major_part_ind }" /></td>
			<th style="text-align: left">
				本所连续/首次出具报告</th>
			<td colspan="2">
				<input id="company_first_report" name="company_first_report" type="text"  value="${businessReportVO.company_first_report }"  autoid=7003   refer='本所连续首次出具报告' />
				</td>
		</tr>
		<tr>
			<th style="text-align: left">
				报告期发生的重大或异常<br>交易[注2]</th>
			<td>
				<input id="unusual_deal" name="unusual_deal" type="text"  value="${businessReportVO.unusual_deal }" /></td>
			<th style="text-align: left">
				合并/单体报告</th>
			<td colspan="2">
				<input id="merge_report_ind" name="merge_report_ind" type="text"  value="${businessReportVO.merge_report_ind }" autoid=7003   refer='合并或单体报告'/></td>
		</tr>
		<tr>
			<th style="text-align: left">
				送审人</th>
			<td>
				
				<input type="text"  id="apply_userid"  name="apply_userid"   value="${userSession.userId }" type="text"  onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"  autoid=10016 readonly="readonly"/>
				</td>
			<th style="text-align: left">
				合并单体数量</th>
			<td colspan="2">
				<input id="merge_count" name="merge_count" type="text"  value="${businessReportVO.merge_count }" /></td>
		</tr>
		<tr>
			<th style="text-align: left">
				二级复核人</th>
			<td>
				<input id="reaudit_person" name="reaudit_person" type="text"  value="${businessReportVO.reaudit_person }"  onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"  autoid=10016 /></td>
				
			<th rowspan="3" style="text-align: left">
				重要财务信息（单位万元，列<br /> 示报告期最近期间，有合并填<br />合并信息）:</th>
			<th style="text-align: left;">
				净资产总额</th>
			<td style="width:222px;">
				<input id="assets_totalamount"   onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')"   onblur="jiSuan(this)"  name="assets_totalamount" type="text"  value="${businessReportVO.assets_totalamount }" /></td>
		</tr>
		
		<tr>
			<th style="text-align: left">
				送审时间[注3]</th>
			<td>
				<input id="reaudit_time" name="reaudit_time"  type="text"  value="${businessReportVO.reaudit_time }" /></td>
			<th style="text-align: left">
				收入</th>
			<td>
				<input id="income" name="income"   type="text"  onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')"   onblur="jiSuan(this)" value="${businessReportVO.income }" /></td>
		</tr>
		<tr>
			<th style="text-align: left">
				到期时间[注4]</th>
			<td>
				<input id="except_complete_time" name="except_complete_time" type="text"  value="${businessReportVO.except_complete_time }" /></td>
			<th style="text-align: left">
				净利润</th>
			<td>
				<input id="net_profit" name="net_profit"  onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')"   onblur="jiSuan(this)"  class="validate-currency" type="text"  value="${businessReportVO.net_profit }" /></td>
		</tr>
		<tr>
			<th style="text-align: left">
				备注：</th>
			<td colspan="4">
				<textarea  cols="100" id="remarks"  name="remarks"  rows="7" value="${businessReportVO.remarks }"  ></textarea>
				</td>
		</tr>
	</tbody>
</table>
<br><br><br>
<table class="formTable">
		<thead>
		<tr>
			<th colspan="5">
				审核相关意见</th>
		</tr>
	</thead>
		<tr>
			<th colspan="1">
				时间</th>
			<th colspan="2">
				审核人</th>
			<th colspan="2">
				意见</th>
		</tr>
	 
			<c:forEach items="${suggestions }" var="l">
			<tr>
			<th  colspan="1">${l.suggestion_time }</th>
			<th colspan="2">${l.suggestion_user }</th>
			<th colspan="2">${l.suggestion_context }</th>
			</tr>
		</c:forEach>
		 </tr>
</table>

</form>
<br><br><br><br><br>
<script type="text/javascript">
new Ext.form.DateField({
	applyTo : 'report_BeginTime',
	width: 133,
	format: 'Y-m-d'
});
new Ext.form.DateField({
	applyTo : 'except_complete_time',
	width: 133,
	format: 'Y-m-d'
});
new Ext.form.DateField({
	applyTo : 'reaudit_time',
	width: 133,
	format: 'Y-m-d'
});
new Ext.form.DateField({
	applyTo : 'report_EndTime',
	width: 133,
	format: 'Y-m-d'
});

function audit(type){
	document.getElementById("hiddentype").value=type;
	var o=document.getElementById("other").value;
	if(type=='other'){
		if(o==""||o==null){
			alert("请选择一个指派人");
			return false;
		}
	}
	if(type=='back'){
		window.history.back();
	}
	document.thisForm.submit();
}
</script>

</body>
</html>