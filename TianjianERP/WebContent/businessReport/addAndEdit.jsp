<%@page import="javax.servlet.jsp.tagext.TryCatchFinally"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>质量控制</title>
<style type="text/css">
td div,th div{
float: left;

}
.mytable{
	border-collapse: collapse; 
	border:#8db2e3 1px solid; 
	width: 900px;;
	margin-left: 4%;
	border: 0px;
}
.mytable th td{
 border-left: 0px;
}
.mytable td table{
   margin-top:0px;
	width:99.8%;
}
.mytable  th {
	background: #e4f4fe; 
	white-space:nowrap;
	padding:5px;
	height:30px;
	background-color: #d3e1f1;
	font-size: 13px;
	font-family:"宋体";
	text-align: center;
}
input{
width: 83%;
	}
	
	
.sontable {
	background-color: #ffffff;
	text-align:center;
	width:80%;
	border:#8db2e3 1px solid; 
	border-collapse: collapse; 
   table-layout: auto;
    vertical-align: top;
}
.sontable tbody td {
	padding-left:2px; 
	border-top: #8db2e3 1px solid; 
	border-left: #8db2e3 1px solid;
	border-bottom: #8db2e3 1px solid;  
	word-break: break-all; 
	text-align: left; 
	word-wrap: break-word

}

.sontable th {
	background: #e4f4fe; 
	white-space:nowrap;
	padding:5px 5px;
	border-top: #8db2e3 1px solid;
	border-left: #8db2e3 1px solid;
	border-right: #8db2e3 1px solid;
	border-bottom: #8db2e3 1px solid; 
	height:30px;
	background-color: #d3e1f1;
	font-size: 13px;
	font-family:"宋体";
	text-align: left;
}
</style>
<script type="text/javascript">
Ext.onReady(function (){
new Validation('thisForm');
	var hasgroup='${hasgroup}';
	if(hasgroup=='false'){
		alert("您所在部门没有对应复核小组！");
	}
});
function ext_init(){
	
    var tbar = new Ext.Toolbar({
   		renderTo: "divBtn",
   		defaults: {autoHeight: true,autoWidth:true},
        items:[

			<c:if test="${isaudit!='isaudit' and view!='true'}">
               { 
			text:'保存',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/save.gif',
			handler:function(){
				var hidvalue=document.getElementById("hiddenuuid").value;
            	if (!formSubmitCheck('thisForm')) return ;
            	var b=false;
            	var doctype='${doctype}';
            	if(doctype=='tjy'){
            		b=$("#company_type").val()!="";
            	}
            	if(doctype=='tjs'){
            		b=$("#company_type").val()!=''&&$("#major_Business").val()!=''&&$("#stockholder").val()!=''&&$("#group_major_part_ind").val()!='';
            	}
            	if(doctype=='tj'||doctype=='wwh'){
            		b=$("#company_type").val()!=''&&$("#major_Business").val()!=''&&$("#stockholder").val()!=''&&$("#group_major_part_ind").val()!=''&&$("#report_BeginTime").val()!=''&&$("#report_EndTime").val()!=''&&$("#assets_totalamount").val()!=''&&$("#income").val()!=''&&$("#net_profit").val()!='';
            		b=b&&$("#company_first_report").val()!=''&&$("#merge_count").val()!='';
            		if(doctype=='wwh'){
            			b=b&&$("#report_moreinfo").val()!='';
            		}
            	}
            	if(b==false){
            		if(confirm("资料尚未完整，请按‘取消’继续补充，按‘确认’则被保存并提交。")){
            		}else{
                		return;
            		}
            	}
            	
            	if(hidvalue==""||hidvalue==null){
				document.thisForm.submit();
            	}
            	else{
    				document.thisForm.action="${pageContext.request.contextPath}/businessreport.do?method=updateBusinesReport";
    				document.thisForm.submit();
            	}
			}
   		},'-',
   		</c:if>
   		{
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
<form action="${pageContext.request.contextPath}/businessreport.do?method=addbusinesApply" method="post" name="thisForm" id="thisForm" >
<input type="hidden"  id="hiddenuuid" name="hiddenuuid" value="${businessReportVO.uuid }"/>
<input type="hidden"  id="uuid" name="uuid" value="${businessReportVO.uuid }"/>
<input type="hidden"  id="property" name="property" value="${businessReportVO.property }"/>
<input type="hidden"  id="appoint_human" name="appoint_human" value="${businessReportVO.appoint_human }" />
<input type="hidden"  id="audit_groupname"  name="audit_groupname"   value="${businessReportVO.audit_groupname }"/>
<input type="hidden"  id="audit_person" name="audit_person"  value="${businessReportVO.reaudit_person }"    />
<input type="hidden"  id="audit_departmentid"  name="audit_departmentid"    value="${businessReportVO.audit_departmentid==null ? userSession.userAuditDepartmentId : businessReportVO.audit_departmentid }" />
<input type="hidden"  id="state" name="state" value="${businessReportVO.state }" /> 
<input type="hidden"  id="cancelstate" name="cancelstate" value="${businessReportVO.cancelstate }" /> 
<input type="hidden"  id="tj_report_type" name="tj_report_type" value="${doctype==null ?  businessReportVO.tj_report_type : doctype }" /> 
<input type="hidden"  id="report_data_receive_ind" name="report_data_receive_ind" value="${businessReportVO.report_data_receive_ind }" /> 
<input type="hidden"  id="report_data_receive_time" name="report_data_receive_time" value="${businessReportVO.report_data_receive_time }" />
<input type="hidden"  id="appoint_ind" name="appoint_ind" value="${businessReportVO.appoint_ind }" />
<input type="hidden"  id="report_data_receive_ind" name="report_data_receive_ind" value="${businessReportVO.report_data_receive_ind }" />


<br>
<br>
<br>
<table class="mytable" > 
		<tr>
			<th colspan="2"  style="border:#8db2e3 1px solid;  border-bottom: 0px" >
				<b style="font-size: 15">项目质量控制复核预约表</b><br><c:if test="${doctype!='wwh' }">(一个文号一个预约)</c:if><c:if test="${doctype=='wwh' }">(一事一预约)</c:if></th>
		</tr> 

<td width="50%">
<table  class="sontable">
	<thead>
		<tr>
			<th colspan="3" style="text-align: center;">
				<b style="font-size: 15">被审计单位基本信息</b></th>
		</tr>
	</thead> 
<tbody>
<tr <c:if test="${doctype!='wwh' }"> style="display: none;" </c:if> ><th width="25%">报送对象</th><td colspan="2"  width="75%" ><input id="report_target"  class="required" name="report_target" type="text"  value="${businessReportVO.report_target }" /></td></tr>
<tr <c:if test="${doctype!='wwh' }"> style="display: none;" </c:if>><th>具体事项</th><td colspan="2"><input id="report_moreinfo"    name="report_moreinfo" type="text"  value="${businessReportVO.report_moreinfo }" /></td></td></tr>


<tr><th width="25%">单位名称</th><td colspan="2"  width="75%" ><input id="company_Name"  class="required" name="company_Name" type="text"  value="${businessReportVO.company_Name }" /></td></tr>
<tr><th>企业类型</th><td colspan="2"><input id="company_type"    name="company_type" type="text"  value="${businessReportVO.company_type }" /></td></td></tr>
<c:if test="${doctype=='tjy' }">
<tr><th>验证类型</th><td colspan="2"   ><input id="report_validate_type"  class="required" name="report_validate_type" type="text"  value="${businessReportVO.report_validate_type }" /></td></tr>

<tr><th >出资方式</th><td colspan="2"  ><textarea id="report_pay_type"  name="report_pay_type"   class="required"  cols="43"  rows="12" >${businessReportVO.report_pay_type }</textarea></td></tr>

<tr><th>审验金额</th><td colspan="2"  ><input id="report_audit_amount"  class="required" name="report_audit_amount" type="text"  value="${businessReportVO.report_audit_amount }"   onkeyup="this.value=this.value.replace(/[^\d-\.]/g,'')"   onfocus="cleardo(this)" onafterpaste="this.value=this.value.replace(/[^\d-\.]/g,'')"   onblur="jiSuan(this)"  /></td></tr>
<tr><th>验资截止日</th><td colspan="2"><input id="report_validate_amount_date"   class="required"   name="report_validate_amount_date" type="text"  value="${businessReportVO.report_validate_amount_date }" /></td></tr>
</c:if>
<c:if test="${doctype!='tjy' }">
<tr><th>主营业务</th><td colspan="2"><input id="major_Business"    name="major_Business" type="text"  value="${businessReportVO.major_Business }" /></td></tr>
<tr><th>控股股东或实际控制人</th><td colspan="2"><input id="stockholder" name="stockholder" type="text"  value="${businessReportVO.stockholder }" /></td></tr>
<tr><th>是否为集团内重要组成部分</th><td colspan="2"><input id="group_major_part_ind" name="group_major_part_ind" type="text"  value="${businessReportVO.group_major_part_ind }" /></td></tr>
<tr ><th>报告期间起始</th><td colspan="2"><input  <c:if test="${doctype=='tjs' }">class="required"</c:if>  id="report_BeginTime"  name="report_BeginTime" type="text"  value="${businessReportVO.report_BeginTime }"  /></td></tr>
<tr><th>报告期间结束</th><td colspan="2"><input <c:if test="${doctype=='tjs' }">class="required"</c:if>  id="report_EndTime"   name="report_EndTime" type="text"  value="${businessReportVO.report_EndTime }" /></td></tr>
<tr><th rowspan="3">重要财务信息</th><th width="20%">资产总额</th><td><input  <c:if test="${doctype=='tjs' }">class="required"</c:if> style="width: 77%;" id="assets_totalamount"  onfocus="cleardo(this)"  onkeyup="this.value=this.value.replace(/[^\d-\.]/g,'')" onafterpaste="this.value=this.value.replace(/[^\d-\.]/g,'')"   onblur="jiSuan(this)"  name="assets_totalamount" type="text"  value="${businessReportVO.assets_totalamount }" /></td></tr>
<tr><th>收入总额</th><td><input style="width: 77%;"  <c:if test="${doctype=='tjs' }">class="required"</c:if>  id="income" name="income"   type="text"  onkeyup="this.value=this.value.replace(/[^\d-\.]/g,'')"   onfocus="cleardo(this)" onafterpaste="this.value=this.value.replace(/[^\d-\.]/g,'')"   onblur="jiSuan(this)" value="${businessReportVO.income }" /></td></tr>
<tr><th>净利润</th><td><input style="width: 77%;"  <c:if test="${doctype=='tjs' }">class="required"</c:if>   onfocus="cleardo(this)"  id="net_profit" name="net_profit"  onkeyup="this.value=this.value.replace(/[^\d-\.]/g,'')" onafterpaste="this.value=this.value.replace(/[^\d-\.]/g,'')"   onblur="jiSuan(this)"  class="validate-currency" type="text"  value="${businessReportVO.net_profit }" /></td></tr>
</c:if>
<tr><th>业务承接</th><td colspan="2"><input <c:if test="${doctype=='tjs' or doctype=='tjy' }">class="required"</c:if>  id="company_first_report" width="100" name="company_first_report" type="text"  value="${businessReportVO.company_first_report }"  autoid=7003   refer='本所连续首次出具报告'  <c:if test="${isaudit=='isaudit' or view =='true'  }">readOnly</c:if>  /></td></tr>
<c:if test="${doctype!='tjy' }">
<tr><th>主体数量</th><td colspan="2"><input <c:if test="${doctype=='tjs' }">class="required"</c:if>  id="merge_count"  onblur="maxnumber(this)" onkeyup="this.value=this.value.replace(/\D/g,'')" onafterpaste="this.value=this.value.replace(/\D/g,'')"   name="merge_count" type="text"  value="${businessReportVO.merge_count }" /></td></tr>
</c:if>
</tbody>
</table>
</td>



<td width="50%">
<table class="sontable" >
	<thead>
		<tr>
			<th colspan="3" style="text-align: center;">
				<b style="font-size: 15">质控基本信息</b></th>
		</tr>
	</thead>
<tbody>
<tr><th width="50%">报告标题</th><td width="50%" colspan="2"><input id="report_type" name="report_type"  class="required" type="text"  value="${businessReportVO.report_type }" /></td></tr>
<tr><th>意见类型</th><td colspan="2"><input id="report_Suggestion_Type"  class="required"   name="report_Suggestion_Type" type="text"  value="${businessReportVO.report_Suggestion_Type }" /></td></tr>
<tr><th>报告用途</th><td colspan="2"><input id="report_use" name="report_use" class="required"  type="text"  value="${businessReportVO.report_use }" /></td></tr>
<tr><th>预约联系人</th><td colspan="2"><input type="text"  id="apply_userid"  class="required"  name="apply_userid"   value="${businessReportVO.apply_userid==null ? userSession.userId : businessReportVO.apply_userid}" type="text"  onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"  autoid=10016 readonly="readonly"/></td></tr>
<tr><th>项目组复核人</th><td colspan="2"><input id="project_reaudit_person"  class="required" name="project_reaudit_person" type="text"  value="${businessReportVO.project_reaudit_person }"  onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"  autoid=10016 <c:if test="${isaudit=='isaudit' or view =='true'  }">readOnly</c:if>  /></td></tr>
<tr><th>部门复核人</th><td colspan="2"><input id="department_reaudit_person" class="required"  name="department_reaudit_person" type="text"  value="${businessReportVO.department_reaudit_person }"  onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"  <c:if test="${isaudit=='isaudit' or view =='true'  }">readOnly</c:if>  autoid=10016 /></td></tr>
<tr><th>签发合伙人</th><td colspan="2"><input id="issue_partner" name="issue_partner"  class="required" type="text"  value="${businessReportVO.issue_partner }"  onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"  autoid=10016 <c:if test="${isaudit=='isaudit' or view =='true'  }">readOnly</c:if>  /></td></tr>
<tr><th>送核日期</th><td><input id="reaudit_time" name="reaudit_time"  class="required"  type="text"  value="${businessReportVO.reaudit_time }" /></td><td width="25%"><input id="reaudit_time_ampm" name="reaudit_time_ampm" type="text"  <c:if test="${isaudit=='isaudit' or view =='true'  }">readOnly</c:if>  onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"  autoid="700" refer="上午下午" value="${businessReportVO.reaudit_time_ampm==null ? 'am' :  businessReportVO.reaudit_time_ampm}"></td></tr>
<tr><th>到期日期</th><td ><input id="except_complete_time"  class="required" name="except_complete_time" type="text"  value="${businessReportVO.except_complete_time }" /></td><td width="25%"><input  <c:if test="${isaudit=='isaudit' or view =='true'  }">readOnly</c:if> id="except_complete_time_ampm" name="except_complete_time_ampm"  onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"  autoid="700"  refer="上午下午"  type="text"value="${businessReportVO.except_complete_time_ampm==null ? 'am' :  businessReportVO.except_complete_time_ampm}"></td></tr>
<tr><th>紧急程度</th><td><input id="report_level"  name="report_level"  <c:if test="${isaudit=='isaudit' or view =='true'  }">readOnly</c:if>   type="text" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"  autoid="700"  refer="紧急程度"  value="${businessReportVO.report_level==null ? '普通' : businessReportVO.report_level}"></td></tr>
<tr><th rowspan="5">备注</th><td  rowspan="5" colspan="2"><textarea <c:if test="${isaudit=='isaudit' or view =='true'  }">readOnly</c:if>  <c:if test="${doctype!='tjy' }"> style="margin: 3 0 3 0;" </c:if>  cols="50" id="remarks"  name="remarks" <c:if test="${doctype=='tjs' }">rows="4" </c:if>  <c:if test="${doctype=='tj' }">rows="4"</c:if>  <c:if test="${doctype=='tjy' }">rows="2"</c:if>  <c:if test="${doctype=='wwh' }">rows="9"</c:if> value="${businessReportVO.remarks }"  >${businessReportVO.remarks }</textarea></td></tr>

</tbody>
</table>

</td>
</tr>
<tr>
<c:if test="${doctype=='tjs' or doctype=='tj' }">
<th colspan="2" align="left" style=" text-align: left;">
说明：	<br>										
1.单位名称应为被审计单位法定全称；<br>
2.企业类型主要成分为上市公司、拟上市公司、国有授权经营企业、银行、证劵公司、期货公司、外商投资和一般企业。对集团客户的组成部分而言，可列<br>&nbsp;&nbsp;为上述企业类型的子公司；<br>
3.主体数量是指报表包含的法律主题数量，单体报表的主题数量为1；<br>
4.报告标题指报告名称；<br>										
5.送核时间为报告预计送核时间，因排班需要，请在送核时间准时报送资料，否则将重新排队；<br>
6.到期时间为希望完成项目质量控制复核时间，到期时间至少为送核时间后2个工作日。<br>
7.如需要紧急处理，请选择加急处理并在备注中说明原由。紧急处理将在年终统计后，提交风险管理与质量控制委员会。<br>
</th>
</c:if>
<c:if test="${doctype=='tjy' }">
<th colspan="2" align="left" style=" text-align: left;">
说明：	<br>										
1.单位名称应为被审验单位法定全称；<br>
2.企业类型主要成分为上市公司、拟上市公司、国有授权经营企业、银行、证劵公司、期货公司、外商投资和一般企业。对集团客户的组成部分而言，可列<br>&nbsp;&nbsp;为上述企业类型的子公司；<br>
3.验资类型主要区分为新设验资、增资验资、减资验资、净资产折股验资、和其他验资；<br>
4.出资方式主要区分为货币出资、实物出资、无形资产出资、净资产出资和其他出资；<br>
5.审验金额为本期验资金额；<br>
6.报告标题指报告名称；<br>
7.送核时间为报告预计送核时间，因排班需要，请在送核时间准时报送资料，否则将重新排队；<br>
8.到期时间为希望完成项目质量控制复核时间。<br>	
9.如需要紧急处理，请选择加急处理并在备注中说明原由。紧急处理将在年终统计后，提交风险管理与质量控制委员会。<br>					
</th>
</c:if>
<c:if test="${doctype=='wwh' }">
<th colspan="2" align="left" style=" text-align: left;">
说明：	<br>										
1.报送对象请详细说明报送对象全名；<br>
2.具体事项请说明出具该文件的原因，出具文件的核心内容；<br>
3.单位名称应为被审计单位法定全称；<br>
4.企业类型主要成分为上市公司、拟上市公司、国有授权经营企业、银行、证劵公司、期货公司、外商投资和一般企业。对集团客户的组成部分而言，可列<br>&nbsp;&nbsp;为上述企业类型的子公司；<br>
5.主体数量是指报表包含的法律主题数量，单体报表的主题数量为1；<br>
6.报告标题指报告名称；<br>
7.送核时间为报告预计送核时间，因排班需要，请在送核时间准时报送资料，否则将重新排队；<br>
8.到期时间为希望完成项目质量控制复核时间。<br>
9.如需要紧急处理，请选择加急处理并在备注中说明原由。紧急处理将在年终统计后，提交风险管理与质量控制委员会。<br>
</th>
</c:if>
</tr>
</table> 

</form>


<c:if test="${isaudit=='isaudit' and businessReportVO.appoint_ind=='0' }">
<br><br>
<form action="${pageContext.request.contextPath}/businessreport.do?method=auditBusinesReport" method="post" name="formaudit" id="formaudit">
<input type="hidden" value="no" id="hiddentype" name="hiddentype"/>
<input type="hidden" value="" id="hiddenuuid" name="hiddenuuid"/>
<input type="hidden" id="audituuid" name="audituuid" value="${businessReportVO.uuid }" />
<table  class="formTable" style="width: 900px;" >
		<thead><tr><th colspan="6">任务分配</th></tr></thead>
		<tr><th width="15%" rowspan="2">指派他人：</th><th  width="15%" >组名：</th><td  width="25%" ><input id="组名" name="组名"   type="text"    autoid=7004  /></td>
			<td width="15%" rowspan="2">
			<input type="button" value="保存"  onclick="audit('other')"> 
			</td>
			<td width="15%" rowspan="2"> 
			<input type="button" value="自己复核" onclick="auditmyself()">
			</td>
			<td width="15%" rowspan="2"> 
				<input type="button" value="不复核" onclick="audit('no')"> 
			</td>
		</tr>
			<tr>
				<th>成员：</th>
				<td  width="25%" ><input   id="other" name="other" type="text"  onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"  autoid=7005  refer=组名  /></td>
			 </tr>
		<tr>
			<th style="text-align: left">
				资料接收</th>
			<td colspan="5">
				<input id="databtn"   name="databtn"  type="button" value="确认收到复核资料" onclick="confirmDataReceive(this)" style="width: 150px;"<c:if test="${businessReportVO.report_data_receive_ind=='1' }">readOnly   disabled="disabled" </c:if> >
				</td>
		</tr>
		<tr>
			<th style="text-align: left">
				提示说明</th>
			<td colspan="5">
				<textarea  cols="100" id="suggestion_help"  name="suggestion_help"  rows="7" ></textarea>
				<input type="hidden" id="suggestion_help_change_ind" name="suggestion_help_change_ind" value="0">
				</td>
		</tr>
		 <tr>
				<th colspan="6" >
				注：<br>
				1.送审资料如果由组长接收了，请点击确认“收到复核资料”按钮；<br>
				2.“提示说明”用来记录组长提示复核人员应关注的内容。
				</th>	
		</tr>
		</table>
</form>
</c:if>


<c:if test="${isaudit=='isaudit' and businessReportVO.appoint_ind=='1' }">
<br><br>
<form action="${pageContext.request.contextPath}/businessreport.do?method=auditBusinesReport" method="post" name="formaudit" id="formaudit">
<input type="hidden" value="no" id="hiddentype" name="hiddentype"/>
<input type="hidden" value="" id="hiddenuuid" name="hiddenuuid"/>
<input type="hidden" id="audituuid" name="audituuid" value="${businessReportVO.uuid }" />
<table  class="formTable" style="width: 900px;" >
		<thead><tr><th colspan="7">审核</th></tr></thead>
		<tr>
		<th  width="15%" rowspan="2">复核结果：</th>
		<td width="15%" rowspan="2"> 
			<input type="button" value="复核通过" onclick="audit('yes')">
			</td>
			<td width="15%" rowspan="2"> 
				<input type="button" value="暂缓通过" onclick="audit('wait')"> 
			</td>
		<th width="15%" rowspan="2">转派他人：</th><th  width="15%" >组名：</th>
		<td  width="25%" ><input id="组名" name="组名"   type="text"    autoid=7004  /></td>
			<td width="15%" rowspan="2">
			<input type="button" value="保存"  onclick="audit('other')"> 
			</td>
			
		</tr>
			<tr>
				<th>成员：</th>
				<td  width="25%" ><input   id="other" name="other" type="text"  onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"  autoid=7005  refer=组名  /></td>
			 </tr>
		<tr>
			<th style="text-align: left">
				资料接收</th>
			<td colspan="6">
				<input id="databtn"   name="databtn" type="button" value="确认收到复核资料" onclick="confirmDataReceive(this)" style="width: 150px;" <c:if test="${businessReportVO.report_data_receive_ind=='1' }">readOnly   disabled="disabled" </c:if> >
				</td>
		</tr>
		<tr>
			<th style="text-align: left">
				复核说明</th>
			<td colspan="6">
				<textarea   cols="100" id="suggestion_help"  name="suggestion_help"  rows="7" onfocus="clearhelp(this)" onblur="suggestionHelpChangeInd(this)"><c:forEach items="${suggestions }" var="l">${l.suggestion_help }   </c:forEach>
				</textarea>
				<input type="hidden" id="suggestion_help_change_ind" name="suggestion_help_change_ind" value="1">
				</td>
		</tr>
		<tr>
			<th style="text-align: left">
				复核意见</th>
			<td colspan="6">
				<textarea  cols="100" id="suggestion_context"  name="suggestion_context"  rows="3" ></textarea>
				</td>
		</tr>
		 <tr>
				<th colspan="7" >
				注：<br>
				1.暂缓通过系因报告不符合出具条件，而退回业务部修改；
				2.“复核说明”用来记录复核人员提示项目组或记录暂缓通过的原因；
				</th>	
		</tr>
		</table>
</form>
</c:if>


<br><br><br>
<c:if test="${isaudit=='isaudit' or view =='true'  }">
<table  class="formTable" style="width: 900px;" >
		<thead>
		<tr>
			<th colspan="5">
				审核相关意见</th>
		</tr>
	</thead>
		<tr>
			<th colspan="1" width="20%">
				时间</th>
			<th colspan="2" width="20%">
				审核人</th>
			<th colspan="2" width="60%" >
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
</c:if>


<br><br><br>
</body>
<script type="text/javascript">


if('${doctype}'!='tjy'&&'${businessReportVO.uuid}'==''){
document.getElementById("report_EndTime").value=(new Date().getFullYear()-1)+"-12-31";
document.getElementById("report_BeginTime").value=(new Date().getFullYear()-1)+"-01-01";
}

new Ext.form.DateField({
	applyTo : 'except_complete_time',
	emptyText : '请选择..',
	width: 133,
	minValue : new Date(),
	format: 'Y-m-d'
	<c:if test="${isadd=='true'}">
	,value:new Date().add(Date.DAY,3)
	</c:if>
});
new Ext.form.DateField({
	applyTo : 'reaudit_time',
	emptyText : '请选择..',
	width: 133,
	minValue : new Date(),
	format: 'Y-m-d'
	<c:if test="${isadd=='true'}">
	,value:new Date().add(Date.DAY,1)
	</c:if>
});
new Ext.form.DateField({
	applyTo : 'report_EndTime',
	emptyText : '请选择..',
	width: 133,
	format: 'Y-m-d'
});
new Ext.form.DateField({
	applyTo : 'report_BeginTime',
	emptyText : '请选择..',
	width: 133,
	format: 'Y-m-d'
});



Ext.onReady(function (){
	//value:new Date().getFullYear()+"01-01"
   // var inputs=document.getElementsByTagName("input");
   // for(var i=0;i<inputs.length-5;i++){
   // inputs[i].setAttribute("readOnly",true);
   // }
   // document.getElementById("other").setAttribute("readOnly",false);
      
<c:if test="${isaudit=='isaudit' or view =='true'  }">
	$("#thisForm input").attr("readonly","readonly");
	</c:if>
	});


function audit(type){
	var perent='${percentind}';
	document.getElementById("hiddentype").value=type;
	var o=document.getElementById("other").value;
	if(type=='other'){
		if(o==""||o==null){
			alert("请选择一个指派人");
			return false;
		}
	}
	if(type=='no'){
		if(confirm("当前复核小组的复核比例为${grouppercent}%,请慎重考虑")){
			if(perent=='false') {
				alert("当前复核小组复核通过比例已小于基数!");
				return false;
			}
		}else{
			return false;
		}
	}
	if(type=='back'){
		window.history.back();
	}
	document.formaudit.submit();
}

function confirmDataReceive(obj){
	var uuid=document.getElementById("audituuid").value;
	var vid=obj.id;
	var url="${pageContext.request.contextPath}/businessreport.do";
	var param={method:"updateBusinesReportDataReceiveCancelState",uuid:uuid};
	$.post(url,param,function(str){
		if(str=='ok'){alert("接收资料成功!");document.getElementById(vid).disabled="disabled";}
	});
	
}
function auditmyself(){
	var uuid=document.getElementById("audituuid").value;
	var url="${pageContext.request.contextPath}/businessreport.do";
	var param={method:"updateBusinesReportAppointInd",uuid:uuid};
	$.post(url,param,function(str){
		window.location.reload();
	});
	
}


function test(){
	var doctype='${doctype}';
	var isaudit='${isaudit}';
	var view='${view}';
	
}
</script>



<script>
var suggestion_help="";
new Ext.form.DateField({
	applyTo : 'report_validate_amount_date',
	emptyText : '请选择..',
	width: 133,
	format: 'Y-m-d',
	value:new Date()
});

function clearhelp(obj){
	var v=obj.value;
	suggestion_help=v;
	obj.select();
	
}

function suggestionHelpChangeInd(obj){
	var v=obj.value;
	if(v!=suggestion_help){
		$("#suggestion_help_change_ind").val("0");
	}
}

function cleardo(obj){
	var v=obj.value.replaceAll(",","");
	document.getElementById(obj.id).value=v;
	
}

function maxnumber(obj){
	var o=obj.value;
	if(isNaN(o)){
		alert("请填入数字!");
		obj.select();
		return ;
	}else{
		if(parseInt(o)>200){
			alert("请填入小于200的数字!");
			obj.select();
			return ;
		}
	}
	
}

function jiSuan(obj)	{
	var newStr = "";		var count = 0;		
	if(obj.value.indexOf(".")==-1) {		
		for(var i=obj.value.length-1;i>=0;i--) {	
			if(count % 3 == 0 && count != 0&&obj.value.charAt(i)!='-') {	
				newStr = obj.value.charAt(i) + "," + newStr;
			} else	 {					
				newStr = obj.value.charAt(i) + newStr;
			}
			count++;
			}			
		obj.value = newStr + ".00";		
	 }
	else {		    
			for(var i=obj.value.indexOf(".")-1;i>=0;i--) {		
				if(count % 3 == 0 && count != 0&&obj.value.charAt(i)!='-') {		
				newStr = obj.value.charAt(i) + "," + newStr;	
				} else	 {		
						newStr = obj.value.charAt(i) + newStr;	
						}	
					count++;	
					}	
		 obj.value = newStr + (obj.value + "00").substr((obj.value + "00").indexOf("."),3);	
	 	}	
	} 
</script>

</html>