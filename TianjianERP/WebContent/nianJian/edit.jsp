<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ taglib uri="http://ckeditor.com" prefix="ckeditor" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/ckeditor/ckeditor.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/ckfinder/ckfinder.js"></script>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/images/editor.js" charset=GBK></script>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/images/DhtmlEdit.js" charset=GBK></script> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>注册会计师年检申报</title>
<script type="text/javascript">

function extInit(){
		
	mt_form_initDateSelect();
	
	var opt="${opt}";
	
	if(opt=="view"){
		new Ext.Toolbar({
			renderTo: "divBtn",
			items:[
				{
		            text:'关闭',
		            cls:'x-btn-text-icon',
		            icon:'${pageContext.request.contextPath}/img/close.gif',
		            handler:function(){
		            	closeTab(parent.tab);
					}
	       		}
	   		]
	    });
	}else{
		new Ext.Toolbar({
			renderTo: "divBtn",
			items:[
				{
		            text:'关闭',
		            cls:'x-btn-text-icon',
		            icon:'${pageContext.request.contextPath}/img/close.gif',
		            handler:function(){
		            	closeTab(parent.tab);
					}
	       		},'-',
		   		{ 
					text:'保存',
					cls:'x-btn-text-icon',
					icon:'${pageContext.request.contextPath}/img/save.gif',
					handler:function(){
						save();
					}
		   		}
	   		]
	    });
	}
	
      
	}

	
</script>
</head>
<body onload="optModel()">
 <div id="divBtn"></div>
  <form action="${pageContext.request.contextPath}/nianJian.do?method=save" method="post" name="thisForm" id="thisForm">
	<table class="formTable" style="width: 90%">
	<thead>
		<tr>
			<th colspan="6">
				${year}注册会计师任职资格检查基本情况表</th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td height="40px">
				会计师事务所名称</td>
			<td colspan="3">
				<input id="accountantOfficeName" name="accountantOfficeName" size=50 type="text" value="天健会计师事务所"/></td>
			<td>
				填表日期</td>
			<td>
			   <input id="fillDate" name="fillDate" ext_type=date type="text" value="${accoutant.fillDate}" class="required"/></td>
		</tr>
		<tr>
			<th>
				注师编号</th>
			<td>
				<input id="cpaId" name="cpaId"  type="text" value="${accoutant.cpaId}" class="required"/></td>
			<th>
				姓名</th>
			<td>
				<input id="name" name="name"  type="text" value="${accoutant.name}" class="required"/></td>
			<th>
				是否专职执业</th>
			<td>
				<input id="isFull" name="isFull" autoid=10001 type="text" refer="是否" value="${accoutant.isFull}"/></td>
		</tr>
		<tr>
			<th>
				政治面貌</th>
			<td>
				<input id="politics_Status" name="politics_Status"  type="text" value="${accoutant.politics_Status}"/></td>
			<th>
				学历</th>
			<td>
				<input id="education" name="education"  type="text" value="${accoutant.education}"/></td>
			<th>
				职称</th>
			<td>
				<input id="rankId" name="rankId"  type="text" value="${accoutant.rankId}"/></td>
		</tr>
		<tr>
			<th>
				注册批准文号</th>
			<td colspan="3">
				<input id="approvalNum"  name="approvalNum"  type="text" value="${accoutant.approvalNum}" class="required"/></td>
			<th>
				注册时间</th>
			<td>
				<input id="registerDate" name="registerDate" ext_type=date type="text" value="${accoutant.registerDate}" class="required"/></td>
		</tr>
		<tr>
			<th>
				担任人大代表、政协委员会</th>
			<td colspan="3">
				<textarea id="npcOrCPPCC" name="npcOrCPPCC" cols="60" rows="5">${accoutant.npcOrCPPCC}</textarea></td>
			<th>
				担任民主党派、工商联职务</th>
			<td>
				<textarea id="gxlJob" name="gxlJob" cols="20" rows="5">${accoutant.gxlJob}</textarea></td>
		</tr>
		<tr>
			<th>
				所内职务</th>
			<td colspan="3">
				<input id="allJob" name="allJob"  type="text" size="40" value="${accoutant.allJob}"/></td>
			<th>
				联系电话</th>
			<td>
				<input id="phone" name="phone" type="text" value="${accoutant.phone}"/></td>
		</tr>
		<tr>
			<th>
				取得国内其他资格情况<br />
				&nbsp;</th>
			<td colspan="5">
				<textarea id="glzg" name="glzg"  cols="80" rows="10">${accoutant.glzg}</textarea></td>
		</tr>
		<tr>
			<th>
				取得港澳台以及境外资格情况<br />
				&nbsp;</th>
			<td colspan="5">
				<textarea id="gazg" name="gazg"   cols=""80 rows="10">${accoutant.gazg}</textarea></td>
		</tr>
		<tr>
			<th>
				奖励信息<br />
				&nbsp;</th>
			<td colspan="5">
			   <textarea id="reward" name="reward"   cols="80" rows="10">${accoutant.reward}</textarea>
				</td>
		</tr>
		<tr>
			<th>
				社会责任<br />
				&nbsp;</th>
			<td colspan="5">
			    <textarea id="responsibility" name="responsibility"   cols="80" rows="10">${accoutant.responsibility}</textarea>
				</td>
		</tr>
		<tr>
			<th>
				上年有无受过刑事处罚<br />
				&nbsp;</th>
			<td colspan="5">
				<input id="lastYear_criminal" name="lastYear_criminal" autoid=10001 refer="有无" type="text" value="${accoutant.lastYear_criminal}"/></td>
		</tr>
		<tr>
			<th>
				上年有无受过行政处罚、撤职以上处分<br />
				&nbsp;</th>
			<td colspan="5">
				<input id="lastYear_administrative" name="lastYear_administrative" autoid=10001 refer="有无" type="text" value="${accoutant.lastYear_administrative}"/></td>
		</tr>
		<tr>
			<th>
				上年有无受过行业惩戒<br />
				&nbsp;</th>
			<td colspan="5">
				<input id="lastYear_industry" name="lastYear_industry" autoid=10001 refer="有无" type="text" value="${accoutant.lastYear_industry}"/></td>
		</tr>
		<tr>
			<th>
				上年完成继续教育学时<br />
				&nbsp;</th>
			<td colspan="5">
				<input id="lastYear_eduHours" name="lastYear_eduHours"  type="text" value="${accoutant.lastYear_eduHours}"/></td>
		</tr>
		<tr>
			<th rowspan="2">
				注册会计师签名<br />
				&nbsp;</th>
			<td colspan="5">
				<br />
				<br />
				<strong><span style="font-size: 14px">本人承诺：保证以上所填列的内容真实、无遗漏。</span></strong><br />
				<br />
				<br />
				&nbsp;</td>
		</tr>
		<tr>
			<th colspan="2">
				签名：</th>
			<td>
			</td>
			
			<td colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;年&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;月&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日</td>
				
		</tr>
		<tr>
			<th rowspan="2">
				事务所审核意见</th>
			<td colspan="5">
				<textarea id="officeView" name="officeView" cols="80" rows="10" readonly="readonly"></textarea></td>
		</tr>
		<tr>
			<th colspan="2">
				负责人签字</th>
			<td></td>
				
			<td colspan="1">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;年&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;月&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日</td>

			<td>
				事务所盖章</td>
		</tr>
		<tr>
			<th rowspan="2">
				市注协检查意见</th>
			<td colspan="5">
				<textarea id="szxView" name="szxView" cols="80" rows="10" readonly="readonly"></textarea></td>
		</tr>
		<tr height="30px">
		   <td colspan="3"></td>
		 <td colspan="1">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;年&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;月&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日</td>

			<td colspan="1">
			市注协盖章
			 </td>
		</tr>
		<tr>
			<th>
				备注</th>
			<td colspan="5">
				<textarea id="remark" name="remark" rows="10" cols="80"></textarea></td>
		</tr>
		<tr>
			<td colspan="6" style="line-height: 25px">
				<p>
					注：1.注册会计师因病不能填写的，可委托其他注册会计师代为填写。<br />
					&nbsp; &nbsp; 2.事务所审核意见栏:应说明注册会计师本人或委托人代为填写的内容是否属实及其它需要说明的问题。<br />
					&nbsp; &nbsp; 3.市注协检查意见栏：若不同意，应注明原因。<br />
					&nbsp;&nbsp; &nbsp;4.协会代管人员应在会计师事务所名称栏内填写协会代管<br />
					&nbsp;</p>
			</td>
		</tr>
		<tr align="center">
		<thead>
		<th colspan="6">注册会计师${lastYear}年从事的主要项目清单</th></thead>
		</tr>
		<tr>
		<th>项目编号</th>
		<th colspan="3">项目名称</th>
		<th>报告文号</th>
		<th>报告出具日期</th>
		</tr>
		
		<c:if test="${flag eq 'add'}">
		
		<%
		for(int i=0;i<10;i++){
			pageContext.setAttribute("index",i);
			%>
		<tr>
		<td><input id="prjId_${index}" name="prjId" type="text" size="30" value="${prj.prjId}"/></td>
		<td colspan="3"><input id="prjName_${index}" name="prjName" type="text" size="50" value="${prj.prjName}"/></td>
		<td><input id="reportId_${index}" name="reportId" type="text" size="25" value="${prj.reportId}"/></td>
		<td><input id="reportName_${index}" name="reportName" ext_type=date type="text" size="25" value="${prj.reportName}"/></td></tr>
		<% 	
		}
		%>
		
		</c:if>
		<c:if test="${flag eq 'edit'}">
		<c:forEach items="${prjList}" var="prj" varStatus="rows">
		<tr>
		<td><input id="prjId_${rows.index}" name="prjId" type="text" size="30" value="${prj.prjId}" /></td>
		<td colspan="3"><input id="prjName_${rows.index}" name="prjName" type="text" size="50" value="${prj.prjName}" /></td>
		<td><input id="reportId" name="reportId_${rows.index}" type="text" size="25" value="${prj.reportId}" /></td>
		<td><input id="reportName_${rows.index}" name="reportName" ext_type=date type="text" size="25" value="${prj.reportName}"/>
		<input id="subId_${rows.index}" name="subId" type="hidden" value="${prj.uuid}"/>
		</td>
		</tr>
		</c:forEach>
		</c:if>
		<input id="flag" name="flag" type="hidden" value="${flag}"/>
		<input id="year" name="year" type="hidden" value="${year}"/>
		<input id="userId" name="userId" type="hidden" value="${accoutant.userId}"/>
		<input id="uuid" name="uuid" type="hidden" value="${accoutant.uuid}"/>
		<input id="opt" name="opt" type="hidden" value="${opt}"/>
	</tbody>
</table>
<div style="height: 30px">
	&nbsp;</div>
</body>

<script type="text/javascript">

Ext.onReady(extInit);
//保存
function save(){
	if (!formSubmitCheck('thisForm')){
  		return ;
  	}
	document.thisForm.submit();
}

function optModel(){
	var opt="${opt}";
	if(opt=="view"){
		view();
	}
}
function view(){
	
	var form_obj = document.all; 
	
	
	//form的值
	for (var i=0; i < form_obj.length; i++ ) {
		e=form_obj[i];
		if (e.tagName=='INPUT' || e.tagName=='TEXTAREA') {
			e.readOnly = true ;
			e.className = "readonly";
			e.disabled = true;
			e.backgroundImage = "none";
		}
		if(e.tagName=='SELECT'){
			e.disabled= true;
			e.className = "readonly";
		}
		if(e.tagName == 'A'){
			e.style.display = "none";
			e.disabled = true;
		}
		if(e.tagName == "IMG"){
			e.style.display = "none";
			e.disabled = true;
		}
	}
}
</script>
</html>