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
<title>Insert title here</title>
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
<body>
<body onload="optModel()">
 <div id="divBtn"></div>
 <form action="${pageContext.request.contextPath}/nianJian.do?method=saveTax" method="post" name="thisForm" id="thisForm">
<table class="formTable" style="width: 90%">
	<thead>
		<tr>
			<th colspan="6">
				执业注册税务师年检登记表</th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<th>
				姓名</th>
			<td>
				<input id="name" name="name" type="text" value="${tax.name}" class="required"/></td>
			<th>
				性别</th>
			<td>
				<input id="sex" name="sex" type="text" autoid=10001 refer="用户性别" value="${tax.sex}" class="required"/></td>
			<th>
				出生年月</th>
			<td>
				<input id="birthday" name="birthday" ext_type=date type="text" value="${tax.birthday}" class="required"/></td>
		</tr>
		<tr>
			<th>
				文化程度</th>
			<td>
				<input id="education" name="education" type="text" value="${tax.education}"/></td>
			<th>
				身份证号</th>
			<td colspan="3">
				<input id="cardNum" name="cardNum" type="text" value="${tax.cardNum}" class="required"/></td>
		</tr>
		<tr>
			<th>
				所在单位</th>
			<td colspan="3">
				<input id="unit" name="unit" type="text" value="${tax.unit}" size="60"/></td>
			<th>
				联系电话</th>
			<td>
				<input id="phone" name="phone" type="text" value="${tax.phone}"/></td>
		</tr>
		<tr>
			<th colspan="2">
				执行注册备案编号</th>
			<td colspan="2">
				<input id="registId" name="registId" type="text" value="${tax.registId}" size="40"/></td>
			<th>
				出资比率</th>
			<td>
				<input id="rate" name="rate" type="text" value="${tax.rate}"/></td>
		</tr>
		<tr>
			<th colspan="2">
				现工作岗位及职位或职称</th>
			<td colspan="2">
				<input id="rank" name="rank" type="text" value="${tax.rank}" size="40"/></td>
			<th>
				省执行证书</th>
			<td>
				<input id="referenceNo" name="referenceNo" type="text" value="${tax.referenceNo}"/></td>
		</tr>
		<tr>
			<th rowspan="2" colspan="1">
				自查情况</th>
			<td colspan="5">
				<textarea id="selfCondition" name="selfCondition" cols="80" rows="10">${tax.selfCondition}</textarea></td>
			
		</tr>
		<tr>
			<th colspan="1" style="text-align: right;">
				自检人签章</th>
			<td colspan="2">
				&nbsp;</td>
			<td colspan="2">
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;年&nbsp;&nbsp;&nbsp;&nbsp;月&nbsp;&nbsp;&nbsp;&nbsp;日</td>
			
		</tr>
		<tr>
			<th rowspan="3">
				事务所意见</th>
			<td colspan="5">
				<textarea id="siSuggestion" name="siSuggestion" cols="80" rows="10" readonly="readonly"></textarea></td>
			
		</tr>
		<tr>
			<th>
				负责人(签章)</th>
			<td>
				&nbsp;</td>
			<th>
				单位(公章)</th>
			<td colspan="2">
				&nbsp;</td>
			
		</tr>
		<tr>
			<td colspan="5" style="text-align: center;height: 30px">
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;年&nbsp;&nbsp;&nbsp;&nbsp;月&nbsp;&nbsp;&nbsp;&nbsp;日</td>
		</tr>
		<tr>
			<th rowspan="4">
				县级税务<br>
				机关纳税服务<br>
				(征管)科审核意见</th>
			<td colspan="5">
				<textarea id="xianSuggestion" name="xianSuggestion" cols="80" rows="10" readonly="readonly"></textarea></td>
			
		</tr>
		<tr>
			<th>
				负责人(签章)</th>
			<td>
				&nbsp;</td>
			<th>
				负责人(签章)</th>
			<td colspan="2">
				&nbsp;</td>
			
		</tr>
		<tr>
			<th>
				县国税纳税服务科（公章）</th>
			<td>
				&nbsp;</td>
			<th>
				 县地税征管科（公章）</th>
			<td colspan="2">
				&nbsp;</td>
			
		</tr>
		<tr>
			<td colspan="2" style="text-align: center;height: 30px">
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;年&nbsp;&nbsp;&nbsp;&nbsp;月&nbsp;&nbsp;&nbsp;&nbsp;日</td>
			<td colspan="3" style="text-align: center;height: 30px">
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;年&nbsp;&nbsp;&nbsp;&nbsp;月&nbsp;&nbsp;&nbsp;&nbsp;日</td>
		</tr>
		<tr>
			<th rowspan="4">
				市级税务<br>
				机关纳税服务<br>
				(征管)处<br>
				 审核意见</th>
			<td colspan="5">
				<textarea id="citySuggestion" name="citySuggestion" cols="80" rows="10" readonly="readonly"></textarea></td>
			
		</tr>
		<tr>
			<th>
				负责人(签章)</th>
			<td>
				&nbsp;</td>
			<th>
				负责人(签章)</th>
			<td colspan="2">
				&nbsp;</td>
			
		</tr>
		<tr>
			<th>
				市国税纳税服务处（公章）</th>
			<td>
				&nbsp;</td>
			<th>
				市地税征管处（公章）</th>
			<td colspan="2">
				&nbsp;</td>
			
		</tr>
		<tr>
			<td colspan="2" style="text-align: center;height: 30px">
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;年&nbsp;&nbsp;&nbsp;&nbsp;月&nbsp;&nbsp;&nbsp;&nbsp;日</td>
			<td colspan="3" style="text-align: center;height: 30px">
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;年&nbsp;&nbsp;&nbsp;&nbsp;月&nbsp;&nbsp;&nbsp;&nbsp;日</td>
		</tr>
		<tr>
			<th>
			   省注税</th>
			<td colspan="5">
				&nbsp;</td>
		</tr>
		<tr>
			<th>
				管理中心</th>
			<td colspan="5">
				&nbsp;</td>
		</tr>
		<tr>
			<th rowspan="2">
				年检结论</th>
			<th>
				省注册税务师管理中心（公章）</th>
			<td colspan="4">
				&nbsp;</td>
		</tr>
		<tr>
			<td colspan="5" style="text-align: center;height: 30px">
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;年&nbsp;&nbsp;&nbsp;&nbsp;月&nbsp;&nbsp;&nbsp;&nbsp;日</td>
	
		</tr>
		<tr style="height: 50px"><td colspan="6"></td></tr>
		<thead>
	<tr><th colspan="6">中国注册税务师协会执业会员年度检查登记表</th></tr>
	<tr><th colspan="6">(${year}年度)</th></tr>
	</thead>
	<tr>
	<th>姓  名</th>
	<td><input id="sName" name="sName" type="text" value="${society.name}" class="required"/></td>
	<th>性  别</th>
	<td><input id="sSex" name="sSex" type="text" value="${society.sex}" autoid=10001 refer="用户性别" class="required"/></td>
	<th>出生年月</th>
	<td><input id="sBirthday" name="sBirthday" type="text" value="${society.birthday}" class="required"/></td>
	</tr>
	<tr>
	<th>现居住地</th>
	<td><input id="currentAddress" name="currentAddress" type="text" value="${society.currentAddress}"/></td>
	<th>民  族</th>
	<td><input id="nation" name="nation" type="text" value="${society.nation}"/></td>
	<th>学  历</th>
	<td><input id="sEducation" name="sEducation" type="text" value="${society.education}"/></td>
	</tr>
	<tr>
	<th>身份证号</th>
	<td colspan="3"><input id="sCardNum" name="sCardNum" type="text" value="${society.cardNum}" class="required"/></td>
	<th>所学专业</th>
	<td><input id=""specialty name="specialty" type="text" value="${society.specialty}"/></td></tr>
	<tr>
	<th>政治面貌</th>
	<td colspan="2"><input id="political" name="political" type="text" value="${society.political}"/></td>
	<th colspan="2">从事注税工作累计时间</th>
	<td><input id="workYear" name="workYear" type="text" value="${society.workYear}"/></td></tr>
	<tr>
	<th colspan="1">执业资格证书号</th>
	<td colspan="2"><input id="zhiyezgNum" name="zhiyezgNum" type="text" value="${society.zhiyezgNum}" size="40"/></td>
	<th colspan="2">执业备案（注册）号</th>
	<td><input id="zhiyebaNum" name="zhiyebaNum" type="text" value="${society.zhiyebaNum}"/></td></tr>
	<tr>
	<th colspan="1">执业会员证书号</th>
	<td colspan="2"><input id="zhiyehyNum" name="zhiyehyNum" type="text" value="${society.zhiyehyNum}" size="40"/></td>
	<th colspan="1">联系电话</th>
	<td colspan="2"><input id="sPhone" name="sPhone" type="text" value="${society.phone}" size="50"/></td></tr>
	<tr>
	<th colspan="1">本人通讯地址</th>
	<td colspan="3"><input id="address" name="address" type="text" value="${society.address}" size="60"/></td>
	<th>邮  编</th>
	<td><input id="postcode" name="postcode" type="text" value="${society.postcode}"/></td></td></tr>
	<tr>
	<th colspan="1">执 业 单 位</th>
	<td colspan="3"><input id="zhiyeUnit" name="zhiyeUnit" type="text" value="${society.zhiyeUnit}" size="60"/></td>
	<th>职  务</th>
	<td><input id="sRank" name="sRank" value="${society.rank}" type="text" /></td></tr>
	<tr>
	<th colspan="1">上一年度年检结论</th>
	<td colspan="3"><input id="lastResult" name="lastResult" type="text" value="${society.lastResult}" size="60"/></td>
	<th>会费交纳情况</th>
	<td><input id="feePay" name="feePay" type="text" value="${society.feePay}"/></td></td></tr>
	<tr>
	<th colspan="1">继续教育完成情况</th>
	<td colspan="5"><input id="continueEducate" name="continueEducate" type="text" value="${society.continueEducate}" size="60"/></td></tr>
	<tr>
	<th>受过何种处分</th>
	<td colspan="3"><textarea id="punishment" name="punishment" cols="35" rows="10">${society.punishment}</textarea></td>
	<th>有无不良执业记录</th>
	<td><textarea id="badRecord" name="badRecord" cols="20" rows="10">${society.badRecord}</textarea></td>
	</tr>
	<tr height="40px">
	<th>事务所负责人</th>
	<td colspan="2"></td>
	<th>省税协年检结论</th>
	<td colspan="2"></td></tr>
	<tr>
	<th colspan="3" style="text-align: right;height: 30px">（公  章）</th>
	<th colspan="3" style="text-align: right;height: 30px">（公  章）</th></tr>
	<tr>
	<td colspan="3" style="text-align: center;height: 30px">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;年&nbsp;&nbsp;&nbsp;&nbsp;月&nbsp;&nbsp;&nbsp;&nbsp;日</td>
	<td colspan="3" style="text-align: center;height: 30px">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;年&nbsp;&nbsp;&nbsp;&nbsp;月&nbsp;&nbsp;&nbsp;&nbsp;日</td></tr>
	<tr>
	<td colspan="6" style="height: 60px">说明：随本表应报送执业会员证。</td>
	</tr>
	<tr style="height: 50px"><td colspan="6"></td></tr>
	<tr align="center">
		<thead>
		<th colspan="6">注册税务师${lastYear}年从事的主要项目清单</th></thead>
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
		<td><input id="prjId_${index}" name="prjId" type="text" size="30" value="${prj.prjId}" /></td>
		<td colspan="3"><input id="prjName_${index}" name="prjName" type="text" size="50" value="${prj.prjName}" /></td>
		<td><input id="reportId_${index}" name="reportId" type="text" size="25" value="${prj.reportId}" /></td>
		<td><input id="reportName_${index}" name="reportName" ext_type=date type="text" size="25" value="${prj.reportName}" /></td></tr>
		<% 	
		}
		%>
		
		</c:if>
		<c:if test="${flag eq 'edit'}">
		<c:forEach items="${prjList}" var="prj" varStatus="rows">
		<tr>
		<td><input id="prjId_${rows.index}" name="prjId" type="text" size="30" value="${prj.prjId}"  /></td>
		<td colspan="3"><input id="prjName_${rows.index}" name="prjName" type="text" size="50" value="${prj.prjName}" /></td>
		<td><input id="reportId" name="reportId_${rows.index}" type="text" size="25" value="${prj.reportId}" /></td>
		<td><input id="reportName_${rows.index}" name="reportName" ext_type=date type="text" size="25" value="${prj.reportName}" />
		<input id="subId_${rows.index}" name="subId" type="hidden" value="${prj.uuid}"/>
		</td>
		</tr>
		</c:forEach>
		</c:if>
	</tbody>
	<input id="flag" name="flag" type="hidden" value="${flag}"/>
		<input id="year" name="year" type="hidden" value="${year}"/>
		<input id="userId" name="userId" type="hidden" value="${tax.userId}"/>
		<input id="uuid" name="uuid" type="hidden" value="${tax.uuid}"/>
		<input id="sUuid" name="sUuid" type="hidden" value="${society.uuid}"/>
		<input id="opt" name="opt" type="hidden" value="${opt}"/>
</table>
</form>
<div style="height: 30px"></div>
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