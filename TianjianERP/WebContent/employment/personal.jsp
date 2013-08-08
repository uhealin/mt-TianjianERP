<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>个人信息</title>
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
<script type="text/javascript">
Ext.onReady(function(){
	if("${personal.id}"!=null && "${personal.id}"!=""){
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
		    },'-',{
		    	text:'下一步',
		        cls:'x-btn-text-icon',
		        icon:'${pageContext.request.contextPath}/img/add.gif',
		        handler:function(){
					goNext();
				}
			}]
		})
	}else{
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
	}
})
</script>
</head>
<body>
<div id="divBtn"></div>
<div style="height:expression(document.body.clientHeight-30);overflow:auto;">
<form method="post" id="thisForm" name="thisForm">
	<h3 align="center">个人信息</h3>
	<table class="data_tb" align="center">
		<tr>
			<td class="data_tb_alignright">中文名<br>Chinese Name</td>
			<td class="data_tb_alignright">英文名<br>English Name</td>
			<td class="data_tb_alignright">性别<br>Gender</td>
			<td class="data_tb_alignright" colspan="2">出生年月<br>Date of Birth</td>
			<td class="data_tb_alignright">出生地<br>Place of Birth</td>
		</tr>
		<tr>
			<td class="data_tb_content"><input type="text" id="chineseName" name="chineseName" class="required" title="请输入" value="${personal.chineseName }" style="border-style:none;border:0;border-bottom:1 solid blue;margin-bottom: 3"/></td>
			<td class="data_tb_content"><input type="text" id="englishName" name="englishName" value="${personal.englishName }" style="border-style:none;border:0;border-bottom:1 solid blue;margin-bottom: 3"/></td>
			<td class="data_tb_content"><input type="radio" name="gender" value="男" <c:if test="${personal.gender=='男' }">checked</c:if> />男&nbsp;
										<input type="radio" name="gender" value="女" <c:if test="${personal.gender=='女' }">checked</c:if> />女
			</td>
			<td class="data_tb_content" colspan="2">
				<input class="required" disabled="disabled" id="born1" readonly="readonly" name="born1" type="text" value="${personal.born }"/>
				<input type="hidden" id="born" name="born" value="${personal.born }" style="border-style:none;border:0;border-bottom:1 solid blue;" size="40"/>
			</td>
			<td class="data_tb_content"><input type="text" id="place" name="place" value="${personal.place }" style="border-style:none;border:0;border-bottom:1 solid blue;margin-bottom: 3"/></td>
		</tr>
		<tr>
			<td class="data_tb_alignright" colspan="2">现工作地点<br>Current work location</td>
			<td class="data_tb_alignright">民族<br>Nationality</td>
			<td class="data_tb_alignright">最高学历<br>Last Degree</td>
			<td class="data_tb_alignright">户口所在地<br>Residence</td>
			<td class="data_tb_alignright">婚姻状况<br>Marital Status</td>
		</tr>
		<tr>
			<td class="data_tb_content" colspan="2"><input type="text" id="nowWorkPlace" name="nowWorkPlace" value="${personal.nowWorkPlace }" style="border-style:none;border:0;border-bottom:1 solid blue;margin-bottom: 3" size="40"/></td>
			<td class="data_tb_content"><input type="text" id="nation" name="nation" value="${personal.nation }" style="border-style:none;border:0;border-bottom:1 solid blue;margin-bottom: 3"/></td>
			<td class="data_tb_content"><input type="text" id="degree" name="degree" value="${personal.degree }" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true noinput=true autoid=3056 style="border-style:none;border:0;border-bottom:1 solid blue;margin-bottom: 3"/></td>
			<td class="data_tb_content"><input type="text" id="residence" name="residence" value="${personal.residence }" style="border-style:none;border:0;border-bottom:1 solid blue;margin-bottom: 3"/></td>
			<td class="data_tb_content">
				<input name="marital" type="radio" value="已婚" <c:if test="${personal.marital == '已婚' }">checked</c:if> />已婚&nbsp;
				<input name="marital" type="radio" value="未婚" <c:if test="${personal.marital == '未婚' }">checked</c:if> />未婚
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright">联系电话<br>Mobile Phone No</td>
			<td class="data_tb_alignright">护照号<br>Passport No</td>
			<td class="data_tb_alignright">身份证号码<br>ID No</td>
			<td class="data_tb_alignright" colspan="2">私人邮箱地址<br>E-Mail Address</td>
			<td class="data_tb_alignright">技术职称<br>Professional Qualifications</td>
		</tr>
		<tr>
			<td class="data_tb_content"><input type="text" id="mobile" name="mobile" value="${personal.mobile }" style="border-style:none;border:0;border-bottom:1 solid blue;margin-bottom: 3"/></td>
			<td class="data_tb_content"><input type="text" id="passport" name="passport" value="${personal.passport }" style="border-style:none;border:0;border-bottom:1 solid blue;margin-bottom: 3"/></td>
			<td class="data_tb_content"><input type="text" id="idNo" name="idNo" value="${personal.idNo }" class="required" title="请输入" style="border-style:none;border:0;border-bottom:1 solid blue;margin-bottom: 3"/></td>
			<td class="data_tb_content" colspan="2"><input type="text" id="email" name="email" value="${personal.email }" style="border-style:none;border:0;border-bottom:1 solid blue;margin-bottom: 3" size="40"/></td>
			<td class="data_tb_content"><input type="text" id="qualifications" name="qualifications" value="${personal.qualifications }" style="border-style:none;border:0;border-bottom:1 solid blue;margin-bottom: 3"/></td>
		</tr>
		<tr>
			<td colspan="6" class="data_tb_alignright">
				以下归国人员填写<br>For Expats and Returnees only
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright">国籍<br>Nationality</td>
			<td class="data_tb_content">
				<select onchange="checkNationality()" id="nationality1" name="nationality1">
					<option value="中国" selected="selected">中国</option>
					<option value="其他" <c:if test="${personal.nationality != '中国' && personal.nationality !='' && personal.nationality !=null}">selected</c:if>>其他</option>
				</select>
				<input type="text" id="nationality2" name="nationality" onblur="setNationality()" value="${personal.nationality }" style="border-style:none;border:0;border-bottom:1 solid blue;display: none;"/>
				<input type="hidden"  id="nationality" name="nationality" value="${personal.nationality }"/>
			</td>
			<td class="data_tb_alignright">
				是否具有国外永久居留权<br>Permanent Residence Status
			</td>
			<td class="data_tb_content">
				<input name="permanentResidence" type="radio" value="yes" <c:if test="${personal.permanentResidence == 'yes' }">checked</c:if> />是&nbsp;
				<input name="permanentResidence" type="radio" value="no" <c:if test="${personal.permanentResidence == 'no' }">checked</c:if> />否
			</td>
			<td class="data_tb_alignright">
				国内户口是否注销<br>Hu Kou Status
			</td>
			<td class="data_tb_content">
				<input name="huKou" type="radio" value="yes" <c:if test="${personal.huKou == 'yes' }">checked</c:if> />是&nbsp;
				<input name="huKou" type="radio" value="no" <c:if test="${personal.huKou == 'no' }">checked</c:if> />否
			</td>
		</tr>
		<tr>
			<td colspan="6" class="data_tb_alignright">有否有亲友及朋友受雇于本公司?如有请指明与该员工之关系及其姓名和所属部门?
				<br>Any relatives and friends in the Company? Please state the relationships, name and department?
			</td>
		</tr>
		<tr>
			<td colspan="6" class="data_tb_content">
				<input class="data_tb_content" id="relationships" name="relationships" value="${personal.relationships}" style="border-style:none;border:0;border-bottom:1 solid blue;margin-bottom: 3" size="150"/>
			</td>
		</tr>
		<tr>
			<td colspan="6">
				女性雇员适用:如您在孕产期需事先声明，公司将在录用对于您的工作环境及工作性质予以考虑
				<input type="radio" name="pregnant" value="yes" <c:if test="${personal.pregnant == 'yes' }">checked="checked"</c:if> />Yes&nbsp;
				<input type="radio" name="pregnant" value="no" <c:if test="${personal.pregnant == 'no' }"> checked="checked"</c:if>/>No
				<br>Applicable to female candidates: Please state if you are currently pregnant as the company may give you careful consideration to your working arrangement and conditions.
			</td>
		</tr>
	</table>
	<input id="id" name="id" value="${personal.id}" type="hidden"/>
</form>
</div>
</body>
<script type="text/javascript">
if("${personal.nationality}" != "中国" && "${personal.nationality}" != '' && "${personal.nationality}" != null){
	document.getElementById("nationality2").style.display = "block"; 
}

new Ext.form.DateField({			
	applyTo : 'born1',
	width: 150,
	format: 'Y-m-d'	
});

if("${saveTrue}" == "true"){
	alert("保存成功!");
}
if("${updateTrue}" == "true"){
	alert("修改成功!");
}
function goAdEd(){
	if (!formSubmitCheck('thisForm')) return ;
	var born = document.getElementById("born1").value;
	document.getElementById("born").value = born;
	var idNo = document.getElementById("idNo").value;
	if(idNo.length != 15 && idNo.length !=18){
		alert("身份证长度不正确，长度应为15或者18位");
		return;
	}
	document.thisForm.action="employment.do?method=savePersonal";
	document.thisForm.submit();
}
function goNext(){
	var linkUserId = document.getElementById("id").value;
	window.location="employment.do?method=addResume&linkUserId="+linkUserId;
}

function checkNationality(){
	var nationality = document.getElementById("nationality1").value;
	if(nationality == "中国"){
		document.getElementById("nationality").value = nationality;
		document.getElementById("nationality2").style.display = "none";
	}else if(nationality == "其他"){
		document.getElementById("nationality2").style.display = "block";
	}
}

function setNationality(){
	document.getElementById("nationality").value = document.getElementById("nationality2").value;
}
</script>
</html>