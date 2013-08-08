<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="java.util.UUID"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ taglib uri="http://ckeditor.com" prefix="ckeditor" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/ckeditor/ckeditor.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/ckfinder/ckfinder.js"></script>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>执业会员培训信息</title>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/images/editor.js" charset=GBK></script>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/images/DhtmlEdit.js" charset=GBK></script>
<script type="text/javascript">
Ext.onReady(function(){
	
	mt_form_initDateSelect();
	mt_form_initAttachFile();
	
	if("${state}" !="结束"){
		var tbar_customer = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		height:30,
	   		defaults: {autoHeight: true,autoWidth:true},
            items:[{
	            text:'保存',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/save.gif',
	            handler:function(){
	            	goAdEd();
	            }
       		},'-',{ 
			        text:'返回',
			        cls:'x-btn-text-icon',
			        icon:'${pageContext.request.contextPath}/img/back.gif',
			        handler:function(){
						history.back();
					}
		       	}]
		})
	}else{
		var tbar_customer = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		height:30,
	   		defaults: {autoHeight: true,autoWidth:true},
            items:[{ 
			        text:'返回',
			        cls:'x-btn-text-icon',
			        icon:'${pageContext.request.contextPath}/img/back.gif',
			        handler:function(){
						history.back();
					}
		       	}]
		})
	}
})
</script>

</head>
<body>
<div id="divBtn" ></div>
<div style="height:expression(document.body.clientHeight-30);overflow:auto;"">
<form id="thisForm" name="thisForm" action="" method="post">

    <input type="hidden" id="uuid" name="uuid" value="${UUID }">
	<table class="formTable" style="width: 80%">
	<thead>
		<tr>
			<th colspan="4">
				培训班资料</th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<th>
				培训班名称<span class="mustSpan">[*]</span></th>
			<td>
				<input class="required" id="name" name="name" type="text" value="${education.name}"/></td>
			<th>
				课程类型<span class="mustSpan">[*]</span></th>
			<td>
				<input class="required" id="courseType" name="courseType" type="text" 
				value="${education.courseType}"
				autoid=3053 onKeyUp="onKeyUpEvent();" valuemustexist=true noinput="true" onClick="onPopDivClick(this);"/></td>
		</tr>
		<tr>
			<th>
				培训开始日期<span class="mustSpan">[*]</span></th>
			<td>
						<input class="required" id="trainStartTime" name="trainStartTime" 
						ext_id=trainStartTime ext_name =trainStartTime ext_type=date type="text" value="${education.registrationStartTime}"/></td>
						
		    <th>培训结束日期<span class="mustSpan">[*]</span></th>
			<td>	
						<input id="trainEndTime" name="trainEndTime" class="required"
						ext_id=trainEndTime ext_name =trainEndTime ext_type=date type="text" value="${education.registrationStartTime}"/>	
			</td>
		</tr>
		<tr>
			<th>
				报名开始日期<span class="mustSpan">[*]</span></th>
			<td>
				<input class="required" id="registrationStartTime" name="registrationStartTime" 
				ext_id=trainStartTime1 ext_name =trainStartTime ext_type=date  type="text" value="${education.registrationStartTime}" />
				</td>
		   <th>报名结束日期<span class="mustSpan">[*]</span></th>
			<td>
				<input class="required" id="registrationEndTime" name="registrationEndTime" 
				ext_id=trainStartTime ext_name =trainStartTime ext_type=date type="text"  value="${education.registrationEndTime}"/></td>
		</tr>
		<tr>
			<th>
				培训班类型<span class="mustSpan">[*]</span></th>
			<td>
				<input class="required" id="classType" name="classType" type="text" value="${education.classType}" onclick="onPopDivClick(this);" 
					onkeydown="onKeyDownEvent();"
					valuemustexist=true
					noinput=true 
					autoid=2058/></td>
			<th>
				讲师<span class="mustSpan">[*]</span></th>
			<td>
				<input class="required" id="teacherId" name="teacherId" type="text" 
					onclick="onPopDivClick(this);" 
					onkeydown="onKeyDownEvent();"
					valuemustexist=true
					multiselect="true"
					noinput=true 
					autoid=2057 value="${education.teacherId}"/></td>
		</tr>
		<tr>
			<th>
				限定报名人数<span class="mustSpan">[*]</span></th>
			<td>
				<input class="required" id="registrationNum" name="registrationNum" type="text" value="${education.registrationNum}"/></td>
		   <th>培训对象<span class="mustSpan">[*]</span></th>
		   <td><input class="required" id="trainObject" name="trainObject" type="text" value="${education.trainObject}"></td>
		</tr>
		<tr>
			<th>
				培训地点</th>
			<td colspan="3">
				<input id="address" name="address" type="text" value="${education.address}" size="80"/></td>
		</tr>
		<tr>
			<th>
				外部网连接<br />
				&nbsp;</th>
			<td colspan="3">
				<input id="link" name="link" type="text" value="${education.link}" size="80"/></td>
		</tr>
		 <!--  
		<tr>
			
			<td colspan="3">
				<input type="button" value="上传网校课件" style="width: 100" onclick="uploadcourse()">
				</td>
		</tr>
	   -->
		<tr>
			<th>
				培训内容</th>
			<td colspan="3">
				<input id="content" name="content" type="text" value="${education.content}" size="80"/></td>
		</tr>
		<tr>
			<th>
				课程安排</th>
			<td colspan="3">
				<input id="arrangement" name="arrangement"  type="text" value="${education.arrangement}" size="80"/></td>
		</tr>
		<tr>
			<th>
				附件</th>
			<td colspan="3">
				<input id="attachment" name="attachment" type="hidden" value="${education.attachment}"/>
				<script type="text/javascript">
					if("${education.attachment}"==""){
						<%String fileName=UUID.randomUUID().toString();%>
						document.getElementById("attachment").value="<%=fileName%>";
						attachInit('attachment','<%=fileName%>');
					}else{
						attachInit('attachment','${education.attachment}');
					}
				</script>
				</td>
		</tr>
		<c:if test="${act=='edit'}">
		<tr>
			<th>发布时间:</th>
			<td><input  id="periodTime" name="periodTime" readonly="readonly" type="text" value="${education.periodTime}"/></td>
			<th>状态:</th>
			<td><input  readonly="readonly" id="state" name="state" type="text" value="${state}"/></td>
		</tr>
		</c:if>
		<tr>
			<input type="hidden" value="${act}" id="act" name="act"/>
			<input type="hidden" value="${education.id}" id="id" name="id"/>
			
		</tr>
	</tbody>
</table>


</form>
</div>

<script type="text/javascript">
function uploadcourse(){
	try{
			var uuid = "${education.uuid}"; 
			if(uuid == ""){
				uuid = "${UUID}";
			}
			
			var url="course.do?method=addSkip&uuid="+uuid;
			parent.openTab(new Date().getTime(),"在线教学平台",url);
	}catch(e){
		alert(e);
	}
}
new Validation('thisForm'); 
function goAdEd(){
	if (!formSubmitCheck('thisForm')) return ;
	var act=document.getElementById("act").value;
	document.thisForm.action="education.do?method=saveOrUpdate&&act="+act;
	document.thisForm.submit();
}
</script>
</body>
</html>