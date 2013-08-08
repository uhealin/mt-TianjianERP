<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="java.util.UUID"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>执业会员培训信息</title>
<script type="text/javascript">
Ext.onReady(function(){
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
	            	// goAdEd();
	            	doAndLocaleEdu();
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
<style type="text/css">

.before{
	border: 0px;
	background-color: #FFFFFF !important;
}

.data_tb {
	background-color: #ffffff;
	text-align:center;
	margin:0 0px;
	width:40%;
	border:#8db2e3 1px solid; 
	BORDER-COLLAPSE: collapse; 
	margin-top: 20px;
	text-align:center;
}
.data_tb_alignright {	
	BACKGROUND: #e4f4fe; 
	white-space:nowrap;
	padding:5px;
	border-top: #8db2e3 1px solid;
	border-left: #8db2e3 1px solid;
	BORDER-RIGHT: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid; 
	height:30px;
	background-color: #d3e1f1;
	width:15%;
	font-size: 13px;
	font-family:"宋体";
}
.data_tb_content {
	PADDING-LEFT: 2px; 
	BORDER-TOP: #8db2e3 1px solid; 
	BORDER-LEFT: #8db2e3 1px solid;
	BORDER-RIGHT: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid;  
	WORD-BREAK: break-all; 
	TEXT-ALIGN: left; 
	WORD-WRAP: break-word
}

</style>
</head>
<body>
<div id="divBtn" ></div>
<div style="height:expression(document.body.clientHeight-30);overflow:auto;"">
<form id="thisForm" name="thisForm" action="" method="post">

    <input type="hidden" id="uuid" name="uuid" value="${UUID }">
	<table class="data_tb"  align="center">
		<tr>
			<td class="data_tb_alignright" colspan="4">现场培训班资料</td>
		</tr>
		<tr>
			<td class="data_tb_alignright">培训班名称<span class="mustSpan">[*]</span>:</td>
			<td class="data_tb_content"><input class="required" id="name" name="name" type="text" value="${education.name }" title="请输入，不得为空"  
			   maxlength="20"
			   class="required"
			   noinput=true
			   title="请输入有效的值"/></td>
			<td class="data_tb_alignright">课程类型</td>
			<td class="data_tb_content"><input class="required" id="courseType" name="courseType" type="text" value="${education.courseType }" title="请输入，不得为空" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" noinput="true" onClick="onPopDivClick(this);"
			autoid=700
			refer="课程类型"/></td>
		</tr>
		<tr>
			<td class="data_tb_alignright">培训日期<span class="mustSpan">[*]</span>:</td>
			<td class="data_tb_content">
				<input class="required" disabled="disabled" id="trainStartTime1" readonly="readonly" name="trainStartTime1" type="text" value="${education.trainStartTime }"/>
				<input id="trainStartTime" readonly="readonly" name="trainStartTime" type="hidden" value="${education.trainStartTime }"/>
				至
				<input class="required" disabled="disabled" id="trainEndTime1" readonly="readonly" name="trainEndTime1" type="text" value="${education.trainEndTime }"/>
				<input id="trainEndTime" readonly="readonly" name="trainEndTime" type="hidden" value="${education.trainEndTime }"/>
			</td>
			<td class="data_tb_alignright">讲师:</td>
			<td class="data_tb_content"><input class="data_tb_content" id="teacherId" name="teacherId" type="text" value="${education.teacherId }" onkeyup="onKeyUpEvent();" 
					onclick="onPopDivClick(this);" 
					onkeydown="onKeyDownEvent();"
					valuemustexist=true
					multiselect="true"
					noinput=true 
					autoid=2057
					/></td>
		</tr>
		<tr>
			<td class="data_tb_alignright">报名起止日期:<span class="mustSpan">[*]</span>:</td>
			<td class="data_tb_content">
				<input class="required" disabled="disabled" id="registrationStartTime1" readonly="readonly" name="registrationStartTime1" type="text" value="${education.registrationStartTime }"/>
				<input id="registrationStartTime" readonly="readonly" name="registrationStartTime" type="hidden" value="${education.registrationStartTime }"/>
				至
				<input class="required" disabled="disabled" id="registrationEndTime1" readonly="readonly" name="registrationEndTime1" type="text" value="${education.registrationEndTime }"/>
				<input id="registrationEndTime" readonly="readonly" name="registrationEndTime" type="hidden" value="${education.registrationEndTime }"/>
			</td>
			<td class="data_tb_alignright">培训班类型:</td>
			<td class="data_tb_content"><input class="data_tb_content" id="classType" name="classType" type="text" value="${education.classType }" onkeyup="onKeyUpEvent();" 
					onclick="onPopDivClick(this);" 
					onkeydown="onKeyDownEvent();"
					valuemustexist=true
					noinput=true 
					autoid=700 refer="培训班类型"/></td>
		</tr>		
		<tr>
			<td class="data_tb_alignright">限定报名人数<span class="mustSpan">[*]</span>:</td>
			<td class="data_tb_content"><input class="required validate-positiveInt" id="registrationNum" name="registrationNum" type="text" value="${education.registrationNum }"/></td>
			<td class="data_tb_alignright">每人费用:</td>
			<td class="data_tb_content"><input class="validate-number" id="cost" name="cost" type="text" value="${education.cost }"/>元</td>
		</tr>
		<tr style="display: none;">
			<td class="data_tb_alignright">培训对象:</td>
			<td class="data_tb_content">
				<input type="text" value="${education.trainObject }" id="trainObject" name="trainObject"/>
			</td>
			<td class="data_tb_alignright">评价平均得分:</td>
			<td class="data_tb_content">
				<input type="text" value="${voteValue }" id="voteValue" name="voteValue"/>
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright">培训地点:</td>
			<td class="data_tb_content" colspan="3"><input class="data_tb_content" size="60" id="address" name="address" type="text" value="${education.address }"/></td>
		</tr>
		<!-- 
		<tr>
			<td class="data_tb_alignright">外部网校链接:</td>
			<td class="data_tb_content" colspan="3">
				<input class="data_tb_content" id="link" size="60" name="link" type="text" value="${education.link }"/>
				<br>或者&nbsp;<input type="button" value="上传网校课件" style="width: 100" onclick="uploadcourse()">&nbsp;</td>
		</tr>
		 -->
		<tr>
			<td class="data_tb_alignright">培训内容:</td>
			<td class="data_tb_content" colspan="3"><textarea class="data_tb_content" id="content"  name="content" style="width:100%;height: 50;overflow: visible;">${education.content }</textarea></td>
		</tr>
		<tr>
			<td class="data_tb_alignright">课程安排:</td>
			<td class="data_tb_content" colspan="3"><textarea class="data_tb_content" id="arrangement"  name="arrangement" style="width:100%;height:50;overflow:visible;">${education.arrangement }</textarea></td>
		</tr>
		<tr>
			<td class="data_tb_alignright">附件:</td>
			<td class="data_tb_content" colspan="3">
				<input type="hidden" name="attachment" id="attachment" value="${education.attachment}"  ext_type="attachFile" />
				<script type="text/javascript">
				/*
					if("${education.attachment}"==""){
						<%String fileName=UUID.randomUUID().toString();%>
						document.getElementById("attachment").value="<%=fileName%>";
						attachInit('education','<%=fileName%>');
						alert(2);
					}else{
						attachInit('education','${education.attachment}');
					}
				*/
			
				mt_form_initAttachFile();
				</script>
			</td>			
		</tr>
		<c:if test="${act=='edit'}">
		<tr>
			<td class="data_tb_alignright">发布时间:</td>
			<td class="data_tb_content"><input class="data_tb_content" id="periodTime" name="periodTime" readonly="readonly" type="text" value="${education.periodTime }"/></td>
			<td class="data_tb_alignright">状态:</td>
			<td class="data_tb_content"><input class="data_tb_content" readonly="readonly" id="state" name="state" type="text" value="${state }"/></td>
		</tr>
		</c:if>
		<tr>
			<input type="hidden" value="${act }" id="act" name="act"/>
			<input type="hidden" value="${education.id }" id="id" name="id"/>
		</tr>
	</table>

</form>
</div>
<script type="text/javascript">
new Ext.form.DateField({			
	applyTo : 'trainStartTime1',
	width: 150,
	minValue : new Date(),
	format: 'Y-m-d'	
});
new Ext.form.DateField({			
	applyTo : 'trainEndTime1',
	width: 150,
	minValue : new Date(),
	format: 'Y-m-d'	
});
new Ext.form.DateField({			
	applyTo : 'registrationStartTime1',
	width: 150,
	minValue : new Date(),
	format: 'Y-m-d'	
});
new Ext.form.DateField({			
	applyTo : 'registrationEndTime1',
	width: 150,
	minValue : new Date(),
	format: 'Y-m-d'	
});
</script>

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
/*
 * 保存现场培训班
 */
function doAndLocaleEdu(){
	alert("localeEdu");
	//if (!formSubmitCheck('thisForm')) return ;
	var act=document.getElementById("act").value;
	var trainStartTime=document.getElementById("trainStartTime1").value;
	document.getElementById("trainStartTime").value=trainStartTime;
	var trainEndTime=document.getElementById("trainEndTime1").value;
	document.getElementById("trainEndTime").value=trainEndTime;
	var registrationStartTime=document.getElementById("registrationStartTime1").value;
	document.getElementById("registrationStartTime").value=registrationStartTime;
	var registrationEndTime=document.getElementById("registrationEndTime1").value;
	document.getElementById("registrationEndTime").value=registrationEndTime;
	
	//alert(act);
	document.thisForm.action="education.do?method=saveOrUpdateLocaleEdu&&act="+act;
	
	document.thisForm.submit();
	
}
</script>
</body>
</html>