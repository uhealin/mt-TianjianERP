<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<%@page import="com.matech.framework.listener.UserSession"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<style type="text/css">

	fieldset {margin: 10px;}
	#tTable {margin-top:10px;border:#d7e2f3 1px solid;border-collapse:collapse;}
	#tTable td,th {
		padding: 5 5 5 10px;text-align: left;white-space:nowrap;border-top:#d7e2f3 1px solid;border-left: #d7e2f3 1px solid;height:30px;
	}
	#tTable th{background-color: #f8f9f9;}
	#tTable input {border:0px;border-bottom:1px solid #aaa;}
</style>
<%
	UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
	String departmentid = userSession.getUserAuditDepartmentId();
%>
<script type="text/javascript">


Ext.onReady(function (){
	new Ext.Toolbar({
			renderTo: "divBtn",
			height:30,
			defaults: {autoHeight: true,autoWidth:true},
	       items:[{ 
	        text:'返回',
	        icon:'${pageContext.request.contextPath}/img/back.gif', 
	        handler:function(){
				window.history.back();
			}
	  	},'->']
	});
	
	 
});
</script>
</head>
<body>
<div id="divBtn" ></div>
<div style="height:expression(document.body.clientHeight-27);width:100%;overflow: auto;">
<form name="thisForm" method="post" action="" id="thisForm">

	<fieldset>
		<legend>项目信息</legend>
		<table cellpadding="1" align="center" cellspacing="1" width="100%" height="100%" id="tTable">
		
			<tr>
				<th width="25%" align="left">委托客户 ：</th>
				<td width="25%" align="left">
					<input type="text"
					   name="entrustCustomerId"
					   id="entrustCustomerId"
					   value="${bp.entrustCustomerId}"
					   disabled="disabled"
					   class="required"
					   maxlength="10"
					   title="请输入有效的值"
					   onkeydown="onKeyDownEvent();"
					   onkeyup="onKeyUpEvent();"
					   onclick="onPopDivClick(this);"
					   valuemustexist=true
					   onchange="setProjectName();"
					   size="30"
					   autoid=2 /> 
				</td>
				
				<th width="25%" align="left">客户类型  ：</th>
				<td width="25%" align="left">
					<input type="text" 
						   name="customerType" 
						   id="customerType"
						   disabled="disabled"
						   maxlength="50"
						   onkeydown="onKeyDownEvent();"
						   onkeyup="onKeyUpEvent();"
						   onclick="onPopDivClick(this);"
						   valuemustexist=true
						   autoid="633"
						   class = "required"
						   value="${bp.customerType}"  />
				</td>
			</tr>
			
			<tr>
				<th width="150" align="left">被审客户 ：</th>
				<td>
					<input type="text"
					   name="customerId"
					   id="customerId"
					   value="${bp.customerId}"
					   disabled="disabled"
					   class="required"
					   maxlength="10"
					   title="请输入有效的值"
					   onkeydown="onKeyDownEvent();"
					   onkeyup="onKeyUpEvent();"
					   onclick="onPopDivClick(this);"
					   valuemustexist=true
					   size="30"
					   autoid=2 onchange="setCustomerInfo(this);changeCustomerId(this);setProjectName();" /> 
				</td>
				
				
				<th width="150" align="right">项目负责人  ：</th>
				<td>
					<input type="text" 
						   name="managerUserId" 
						   id="managerUserId"
						   disabled="disabled"
						   maxlength="50"
						   onkeydown="onKeyDownEvent();"
						   onkeyup="onKeyUpEvent();"
						   onclick="onPopDivClick(this);"
						   valuemustexist=true
						   autoid="617"
						   size="30"
						   value="${bp.managerUserId}"  />
				</td>
			</tr>
			
			<tr>
				<th width="150" align="right">付款客户 ：</th>
				<td>
					<input type="text"
					   name="payCustomerId"
					   id="payCustomerId"
					   value="${bp.payCustomerId}"
					   disabled="disabled"
					   class="required"
					   maxlength="10"
					   title="请输入有效的值"
					   onkeydown="onKeyDownEvent();"
					   onkeyup="onKeyUpEvent();"
					   onclick="onPopDivClick(this);"
					   valuemustexist=true
					   size="30"
					   autoid=2 onchange="changeCustomerId(this);setProjectName();" /> 
				</td>
				
				
				<th width="150" align="right">高级经理/部门经理复核：</th>
				<td>
					<input type="text" 
						   name="departManagerUserId" 
						   id="departManagerUserId"
						   disabled="disabled"
						   maxlength="50"
						   onkeydown="onKeyDownEvent();"
						   onkeyup="onKeyUpEvent();"
						   onclick="onPopDivClick(this);"
						   valuemustexist=true
						   autoid="639"
						   size="30"
						   value="${bp.departManagerUserId}"  />
				</td>
			</tr>
			
			<tr>
				<th width="150" align="right">委托内容 ：</th>
				<td>
					<input type="text"
					   name="auditpara"
					   id="auditpara"
					   value="${bp.auditpara}"
					   disabled="disabled"
					   maxlength="20"
					   class="required"
					   noinput=true
					   title="请输入有效的值"
					   onkeydown="onKeyDownEvent();"
					   onkeyup="onKeyUpEvent();"
					   onclick="onPopDivClick(this);"
					   onchange="setProjectName();"
					   autoid=58"/>
				</td>
				
				
				<th width="150" align="right">业务合伙人复核：</th>
				<td>
					<input type="text" 
						   name="partnerUserId" 
						   id="partnerUserId"
						   disabled="disabled"
						   maxlength="50"
						   onkeydown="onKeyDownEvent();"
						   onkeyup="onKeyUpEvent();"
						   onclick="onPopDivClick(this);"
						   valuemustexist=true
						   autoid="637"
						   size="30"
						   value="${bp.partnerUserId}"  />
				</td>
			</tr>
			
			<!-- 模板编号 -->
			<tr>
				<th width="150" align="right">底稿模板 ：</th>
				<td>
					<input type="text"
						   name="typeId"
						   id="typeId"
						   value="${bp.typeId}"
						   disabled="disabled"
						   maxlength="50"
						   class="required validate-digits"
						   noinput=true
						   title="请输入有效的值"
						   onkeydown="onKeyDownEvent();"
						   onkeyup="onKeyUpEvent();"
						   onclick="onPopDivClick(this);"
						   valuemustexist=true
						   autoid=5
						   refer=auditPara />
				</td>
				
				<th width="150" align="right">风险控制合伙人复核：</th>
				<td>
					<input type="text" 
						   name="ristPartnerUserId" 
						   id="ristPartnerUserId"
						   disabled="disabled"
						   maxlength="50"
						   onkeydown="onKeyDownEvent();"
						   onkeyup="onKeyUpEvent();"
						   onclick="onPopDivClick(this);"
						   valuemustexist=true
						   autoid="637"
						   size="30"
						   value="${bp.ristPartnerUserId}"  />
				</td>
			</tr>
			
			<!-- 业务区间 -->
			<tr>
				<th width="150" align="right">业务区间开始 ：</th>
				<td>
					<input type="text" name="auditTimeBegin" readonly="readonly" id="auditTimeBegin" disabled="disabled" class="required" 
					 title="请输入有效的值" value="${project.auditTimeBegin }" onpropertychange="setProjectName();" />
				</td>
				
				
				<th width="150" align="right">主任会计师：</th>
				<td>
					<input type="text" 
						   name="seniorCpaUserId" 
						   id="seniorCpaUserId"
						   disabled="disabled"
						   maxlength="50"
						   onkeydown="onKeyDownEvent();"
						   onkeyup="onKeyUpEvent();"
						   onclick="onPopDivClick(this);"
						   valuemustexist=true
						   autoid="640"
						   size="30"
						   value="${bp.seniorCpaUserId}"  />
				</td>
			</tr>
			<tr>
				<th width="150" align="right">业务区间结束 ：</th>
				<td>
					<input type="text" name="auditTimeEnd" readonly="readonly" id="auditTimeEnd" disabled="disabled"
					 	class="required" title="请输入有效的值" value="${project.auditTimeEnd }" onpropertychange="setProjectName();" />
				</td>
				
				
				<th width="150" align="right">项目风险级别 ：</th>
				<td>
					<input type="text" 
						   name="ristLevel" 
						   id="ristLevel"
						   disabled="disabled"
						   maxlength="50"
						   onkeydown="onKeyDownEvent();"
						   onkeyup="onKeyUpEvent();"
						   onclick="onPopDivClick(this);"
						   autoid="634"
						   size="10"
						   class="required" 
						   value="${bp.ristLevel}"  />
				</td>
			</tr>
			
			<tr>
				<th width="150" align="right">项目名称 ：</th>
				<td>
					<input type="text" size="50" maxlength="500" title="请输入有效的值" name="projectName" id="projectName" disabled="disabled" class="required" value="${bp.projectName }" />
				</td>
				
				
				<th width="150" align="right">是否需要归档 ：</th>
				<td>
					<input type="text" 
						   name="isStore" 
						   id="isStore"
						   disabled="disabled"
						   maxlength="50"
						   onkeydown="onKeyDownEvent();"
						   onkeyup="onKeyUpEvent();"
						   onclick="onPopDivClick(this);"
						   autoid="399"
						   size="10"
						   class="required" 
						   value="${bp.isStore}"  />
				</td>
			</tr>
			
			<tr>
				<th width="150" align="right">是否特定项目 ：</th>
				<td>
					<input type="text" 
						   name="isSpecialProject" 
						   id="isSpecialProject"
						   disabled="disabled"
						   maxlength="50"
						   onkeydown="onKeyDownEvent();"
						   onkeyup="onKeyUpEvent();"
						   onclick="onPopDivClick(this);"
						   autoid="399"
						   size="10"
						   class = "required"
						   value="${bp.isSpecialProject}"  />
				</td>
				
				<th width="150" align="right">签约日期：</th>
				<td>
					<input type="text" 
						   name="signedDate" 
						   id="signedDate"
						   disabled="disabled"
						   readonly="readonly"
						   autoid="399"
						   value="${bp.signedDate}"  />
				</td>
				
			</tr>
			
			<tr>
				<th width="150" align="right">是否新承接项目 ：</th>
				<td>
					<input type="text" 
						   name="isNewTakeProject" 
						   id="isNewTakeProject"
						   disabled="disabled"
						   maxlength="50"
						   onkeydown="onKeyDownEvent();"
						   onkeyup="onKeyUpEvent();"
						   onclick="onPopDivClick(this);"
						   autoid="399"
						   size="10"
						   class = "required"
						   value="${bp.isNewTakeProject}" />
				</td>
				
				<th width="150" align="right">是否出具报告：</th>
				<td>
					<input type="text" 
						   name="isReport" 
						   id="isReport"
						   disabled="disabled"
						   maxlength="50"
						   onkeydown="onKeyDownEvent();"
						   onkeyup="onKeyUpEvent();"
						   onclick="onPopDivClick(this);"
						   autoid="399"
						   size="10"
						   value="${bp.isReport}"  />
				</td>
			</tr>
			
			<tr>
				
				<th width="150" align="right">业务费用 ：</th>
				<td>
					<input type="text" 
						   name="businessCost" 
						   id="businessCost"
						   disabled="disabled"
						   class = "required"
						   size="30"
						   value="${bp.businessCost}" />
				</td>
				
				<th width="150" align="right">企业性质：</th>
				<td>
					<input name="companyType" 
					       id="companyType" 
					       disabled="disabled"
					       type="text" 
					       value="${bp.companyType}"  />
				</td>
				
			</tr>
			
			<tr>
				
				<th width="150" align="right">所属部门 ：</th>
				<td>
					<input name="departmentId" 
					       id="departmentId" 
					       disabled="disabled"
					       type="text" 
					       onKeyDown="onKeyDownEvent();" 
					       onKeyUp="onKeyUpEvent();" 
					       noinput="true" 
					       onClick="onPopDivClick(this);" 
					       valuemustexist=true 
					       autoid=123
					       class = "required"
					       value="${bp.departmentId}"  />
				</td>
				
				<th width="150" align="right">主营业务：</th>
				<td>
					<input name="business" 
					       id="business" 
					       disabled="disabled"
					       type="text" 
					       value="${bp.business}"  />
				</td>
				
			</tr>
			
			<tr>
				
				<th width="150" align="right">客户联系人：</th>
				<td>
					<input name="contactUser" 
					       id="contactUser" 
					       disabled="disabled"
					       type="text" 
					       title="此数据是根据被审客户从客户资料中直接过渡"
					       value="${bp.contactUser}"/>
				</td>
				
				<th width="150" align="right">联系电话：</th>
				<td>
					<input name="contactPhone" 
					       id="contactPhone" 
					       disabled="disabled"
					       title="此数据是根据被审客户从客户资料中直接过渡"
					       type="text" 
					       value="${bp.contactPhone}"/>
				</td>
				
			</tr>
			
			<tr>
				
				<th width="150" align="right">出具报告的特殊要求：</th>
				<td>
					<input name="reportRequire" 
					       id="reportRequire" 
					       disabled="disabled"
					       type="text" 
					       size="50"
					       value="${bp.reportRequire}"  />
				</td>
				
				<th width="150" align="right">出具报告日期：</th>
				<td>
					<input name="reportDate" 
					       id="reportDate" 
					       disabled="disabled"
					       type="text" 
					       value="${bp.reportDate}"  />
				</td>
				
			</tr>
			
			<tr>
				
				<th width="150" align="right">业务来源渠道：</th>
				<td>
					<input name="businesChannel" 
					       id="businesChannel" 
					       disabled="disabled"
					       type="text" 
					       value="${bp.businesChannel}"  />
				</td>
				
				<th width="150" align="right">&nbsp;</th>
				<td>
					&nbsp;
				</td>
				
			</tr>
			
		</table>
	</fieldset>

<input type="hidden" id="projectID" name="projectID" value="${bp.projectID}">
</form>

</div>
</body> 
</html>