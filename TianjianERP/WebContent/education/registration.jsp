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
<title>报名信息</title>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/images/editor.js" charset=GBK></script>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/images/DhtmlEdit.js" charset=GBK></script>
<script type="text/javascript">
Ext.onReady(function(){
	mt_form_initDateSelect();
	mt_form_initAttachFile();
	
		var tbar_customer = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		height:30,
	   		defaults: {autoHeight: true,autoWidth:true},
            items:[{
	            text:'报名',
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
})
</script>
<style type="text/css">
.tableStrong{
	font-weight: 900;
	text-align: right;
	padding-right: 8px;
	font-size: 13px;
}
table tbody th{
   width:150px;

}
</style>
</head>
<body scroll="auto">
<div id="divBtn"></div>
<form id="thisForm" name="thisForm" action="" method="post">
<div>
	<table  style="width:600px;line-height: 22px;"  class="formTable"> 
	   <thead>
		<tr>
			<th colspan="2">报名信息</th>
		</tr>
		</thead>
		<tbody>
		<tr>
			<th>培训班名称：</th>
			<td><input style="border: none" readonly="readonly" class="data_tb_content" id="name" name="name" type="text" value="${education.name }"/></td>
		</tr>
		<tr>
			<th>课程类型：</th>
			<td><input style="border: none" class="required" readonly="readonly" id="courseType" name="courseType" type="text" value="${education.courseType }" title="请输入，不得为空" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" noinput="true" onClick="onPopDivClick(this);" autoid=3053/></td>
		</tr>
		<tr>
			<th>培训日期：</th>
			<td>
				${education.trainStartTime }
				至
				${education.trainEndTime }
			</td>
		</tr>
		<tr>
			<th>讲师：</th>
			<td><input style="border: none" class="data_tb_content" readonly="readonly" id="teacherId" name="teacherId" type="text" value="${education.teacherId }" onkeyup="onKeyUpEvent();" 
					onclick="onPopDivClick(this);" 
					valuemustexist=true
					noinput=true 
					autoid=2057/></td>
		</tr>
		<tr>
			<th>报名起止日期：</th>
			<td>
				${education.registrationStartTime }
				至
				${education.registrationEndTime }
			</td>
		</tr>
		<tr>		
			<th>培训班类型：</th>
			<td><input style="border: none" class="data_tb_content" readonly="readonly" id="classType" name="classType" type="text" value="${education.classType }" onkeyup="onKeyUpEvent();" 
					onclick="onPopDivClick(this);" 
					valuemustexist=true
					noinput=true 
					autoid=2058/></td>
		</tr>
		<tr>
			<th>限定报名人数：</th>
			<td>${education.registrationNum}&nbsp;人</td>
		</tr>
		<tr>
			<th>培训对象：</th>
			<td>
				<input style="border: none" type="text" readonly="readonly" value="${education.trainObject }" id="trainObject" name="trainObject"/>
			</td>
		</tr>
		<tr>
			<th>评价平均得分：</th>
			<td>
				<input style="border: none" type="text" readonly="readonly" value="${voteValue }" id="voteValue" name="voteValue"/>
			</td>
		</tr>
		<tr>
			<th>培训地点：</th>
			<td><input style="border: none" readonly="readonly" class="data_tb_content" id="address" name="address" type="text" value="${education.address }"/></td>
		</tr>
		<tr>
			<th>网校链接：</th>
			<td><input style="border: none" class="data_tb_content" id="link" name="link" type="text" value="${education.link}"/></td>
		</tr>
		<tr>
			<th>培训内容：</th>
			<td><textarea style="border: none;overflow: hidden" readonly="readonly" class="data_tb_content" id="content" name="content" style="width:100%">${education.content }</textarea></td>
		</tr>
		<tr>
			<th>课程安排：</th>
			<td><textarea style="border: none;overflow: hidden" readonly="readonly" class="data_tb_content" id="arrangement" name="arrangement" style="width:100%">${education.arrangement }</textarea></td>
		</tr>
		<tr>
			<th>附件：</th>
			<td>
				<input style="border: none" ext_type=attachFile  type="hidden" name="attachment" id="attachment" value="${education.attachment}" readonly="readonly"/>
				
				<script type="text/javascript">
						//attachInit('attachment','${education.attachment}',"showButton:false,remove:false");
				</script>
			</td>			
		</tr>
		<tr>
			<th>发文时间：</th>
			<td><input style="border: none" readonly="readonly" class="data_tb_content" id="periodTime" name="periodTime" readonly="readonly" type="text" value="${education.periodTime}"/></td>
		</tr>
		<tr>
			<th>状态：</th>
			<td><input style="border: none" readonly="readonly" class="data_tb_content" id="state" name="state" type="text" value="${state}"/></td>
		</tr>
		<tr>
		</tr>
		</tbody>
	</table>
			<input type="hidden" value="${act }" id="act" name="act"/>
			<input type="hidden" value="${education.id }" id="id" name="id"/>
</div>
</form>
<div style="height: 30px"></div>
<script type="text/javascript">
function goAdEd(){
	var state="${state}";
	//document.thisForm.action="education.do?method=educationReg&&educationId="+${education.id};
	//document.thisForm.submit();
	    if(state=="等待报名"){
				alert("报名还没有开始,请等待报名!");
				return;
	    }else{
	    	
 	    var url="${pageContext.request.contextPath}/education.do?method=educationReg";
		var requestString = "&educationId="+${education.id};
		var request= ajaxLoadPageSynch(url,requestString);
		if(request=="fail"){
			alert("你已经报名了");
		}
		if(request=="full"){
			alert("你所报名的班已经满人");
		}else{
		
		
			var url="education.do?method=onReg";
			parent.openTab("registrationId","培训报名管理",url);
		
		
		}
	    }
}
</script>
</body>
</html>