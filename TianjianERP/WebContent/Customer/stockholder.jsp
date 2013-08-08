<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ page import="java.util.*" %>
<%@page import="com.matech.framework.pub.util.ASFuntion"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<title>设置股东信息</title>

<script>
//var register = window.opener.document.getElementById("register").value;
//var curname = window.opener.document.getElementById("curname").value;

var register = "${register}";
var curname = "${curname}";

function addLine(){

	if(document.getElementById("register").value==""||document.getElementById("curname").value==""){
		alert("请填写客户的注册金额,并选择金额单位！");
		return;
	}else{
		var obj=document.getElementById("stockholderCount");
		//增加计数
		obj.value=obj.value * 1 + 1;
		var objTr=document.getElementById("mytable").insertRow(-1);
		objTr.align="center";
		var objTd;
		objTd=objTr.insertCell(-1);
		objTd.innerHTML="<input type=checkbox id=checkLine name=\"checkbox\">";
		objTd=objTr.insertCell(-1);
		objTd.innerHTML="<input type=\"text\" name=\"name\" class='required' style=\"width: 80px; height:22px\">";
		objTd=objTr.insertCell(-1);
		objTd.innerHTML="<input type=\"text\" name=\"percentage\" class='required' title='占注册百分比' onblur='onLeave(this);' style=\"width: 100px; height:22px\">";
		objTd=objTr.insertCell(-1);
		objTd.innerHTML="<input type=\"text\" name=\"registerFund\" readonly='readonly' style=\"width: 80px; height:22px\">";
		objTd=objTr.insertCell(-1);
		objTd.innerHTML="<input type=\"text\" name=\"totalFund\" title=\"投资总额\" onblur=\"onLeave(this);\" class=\"required\" style=\"width: 80px; height:22px\">";
		objTd=objTr.insertCell(-1);
		objTd.innerHTML="<input type=\"text\" name=\"factFund\" class='required' title='实收金额' onblur='onLeave(this);' style=\"width: 80px; height:22px\">";
		objTd=objTr.insertCell(-1);
		objTd.innerHTML="<input type=\"text\" name=\"percentOfFund\" readonly='readonly' style=\"width: 80px; height:22px\">";

		var registerFund = document.getElementsByName("registerFund");
		for(i=0; i<registerFund.length; i++){
		//	registerFund[i].value = register;
			registerFund[i].value = document.getElementById("register").value;
		}
	}
}
function deleteLine() {
	var flag=false;
	for (var i=addBody.children.length-1; i>=0 ; i-- )
	if (addBody.children[i].firstChild.firstChild.checked){
		addBody.deleteRow(i);
		flag=true;
	}
	if(!flag)
	{
		alert("请选定其中一列！！");
	}
}

function saveCheck(){

	var names = new Array();
	var percentages = new Array();

	names = document.getElementsByName("name");
	percentages = document.getElementsByName("percentage");
	factFund = document.getElementsByName("factFund");

	var sum = 0;
	for(var i=0; i<names.length; i++){

		if(names[i].value == ""){
			alert("股东名称不能为空！");
			names[i].focus();
			return false;
		}
		else if(percentages[i].value == ""){
			alert("占注册百分比不能为空！");
			percentages[i].focus();
			return false;
		}else if(factFund[i].value == ""){
			alert("实收金额不能为空！");
			factFund[i].focus();
			return false;
		}
		sum += parseInt(percentages[i].value);
	}

	if(sum > 100){
		alert("错误！占注册百分比之和不能超过100.");
		return false;
	}

	var total = "";
	for(var i=0; i<names.length; i++){
		var sname = names[i].value;
		var spercent = parseFloat(percentages[i].value);
		total += sname+" "+spercent+"%;";
	}

//	window.opener.document.getElementById("stockowner").value = total;
//	window.opener.document.getElementById("stock").value = total;

	document.thisForm.action = "/AuditSystem/stockholder.do?method=save";
	document.thisForm.submit();
}

function isCurrency(obj){
	var reg = /^([1-9,-]{1}[0-9,-]{0,2}(\,[0-9,-]{3})*(\.[0-9,-]{0,2})?|[1-9,-]{1}\d*(\.[0-9,-]{0,2})?|0(\.[0-9,-]{0,2})?|(\.[0-9,-]{1,2})?)$/;
	if(!reg.test(obj)){
		return false;
	}
	return true;
}

function isPercent(value){
	if(isNaN(value)){
		return false;
	}
	return true;
}

var isAlert = false;
function onLeave(obj){

	var register = document.getElementById("register").value;

	if(obj.value == ""){
		alert(obj.title + "不能为空！");
		obj.focus();
		isAlert = true;
		return false;
	}

	var parent = obj.parentElement.parentElement;

	var percentObj = parent.children(2).firstChild;		//占比注册百分比
	var totalObj = parent.children(4).firstChild;		//投资总额
	var factObj = parent.children(5).firstChild;		//实收金额
	var outObj = parent.children(6).firstChild;			//出资比例

	if(obj.name == "percentage"){

		//判断占注册百分比
		if(!isPercent(obj.value)){

			alert("请正确完成" + obj.title + "的格式！");

			obj.value = "";
			obj.focus();

			isAlert = true;
			return false;

		}

		//如果注册百分比大于100或者小于等于0
		if(parseFloat(obj.value) > 100 || parseFloat(obj.value) <= 0){

			//如果注册百分比大于100
			if(parseFloat(obj.value) > 100){
				alert(obj.title + "不能超过100%!");

			} else if (parseFloat(obj.value) <= 0){
				//少于等于0
				alert(obj.title + "不能少于等于0！");
			}

			//投资总额
			totalObj.value = "";
			outObj.value = "";

			obj.value = "";
			obj.focus();

			isAlert = true;
			return false;
		}

		//获得占注册比例
		var percent = percentObj.value;

		//赋值给投资总额
		totalObj.value = parseFloat(percent) * parseFloat(register) / 100;

		var fact = factObj.value;

		if(fact != ""){
			var total = totalObj.value;

			//计算出资比例
			outObj.value = Math.round(10000 * parseFloat(fact) / parseFloat(total) ) / 100 + "%";
		}

	} else if (obj.name == "factFund"){

		//判断实收金额
		if(!isCurrency(obj.value)){
			alert("请正确完成" + obj.title + "的格式！");
			obj.value = "";
			obj.focus();

			isAlert = true;
			return false;
		}

		var fact = factObj.value;
		var total = totalObj.value;
		var percent = percentObj.value;

		//如果占注册百分比为空
		if(percent == ""){
			alert("请先完成占注册百分比！");
			percentObj.focus();

			isAlert = true;
			return false;
		}

		//计算出资比例
		outObj.value = Math.round(10000 * parseFloat(fact) / parseFloat(total) ) / 100 + "%";

	} else if(obj.name == "totalFund") {
		//判断投资总额格式是否正确
		if(!isPercent(obj.value)){
			alert("请正确完成" + obj.title + "的格式！");

			//清空投资总额
			obj.value = "";
			obj.focus();

			isAlert = true;
			return false;

		}

		//如果投资总额大于注册金额或小于等于0
		if(parseFloat(obj.value) > register || parseFloat(obj.value) <= 0){

			//如果投资总额大于注册金额
			if(parseFloat(obj.value) > register){
				alert(obj.title + "不能大于注册金额");

			} else if (parseFloat(obj.value) <= 0){
				alert(obj.title + "不能少于等于0！");
			}

			//清空值
			totalObj.value = "";
			outObj.value = "";
			obj.value = "";

			//获得焦点
			obj.focus();

			isAlert = true;
			return false;
		}

		var total = totalObj.value;

		percentObj.value = parseFloat(total) * 100 / parseFloat(register);
		var fact = factObj.value;

		if(fact != ""){
			outObj.value = Math.round(10000 * parseFloat(fact) / parseFloat(total) ) / 100 + "%";
		}

	} else {

		return false;
	}

	isAlert = false;
}

function filling() {
	var register = document.getElementById("register").value;
	var percentages = document.getElementsByName("percentage");
	var totalFunds = document.getElementsByName("totalFund");
	var percentOfFunds = document.getElementsByName("percentOfFund");
	var registerFunds = document.getElementsByName("registerFund");
	for(var i=0; i<percentages.length; i++) {
		onLeave(percentages[i]);
		registerFunds[i].value=register;
	}
}
</script>
</head>
<body>
<br>

<span class="formTitle" >
	股&nbsp;&nbsp;东&nbsp;&nbsp;简&nbsp;&nbsp;历<br/><br/> 
</span>
	
<%
	ASFuntion CHF = new ASFuntion();
	List list = new ArrayList();
	if(request.getAttribute("stockholders") != null){
		list = (List)request.getAttribute("stockholders");
	}
%>

<form name="thisForm" id="thisForm" method="post">

<table>

<tr>
	<td>
		<input type="hidden" name="managerCount" id="stockholderCount" value="<%=list.size() %>">
		<input type="hidden" name="departid" id="departid" value="<%=CHF.showNull(request.getParameter("departid")) %>">
		注册金额：<input type="text" name="register" id="register" class="required" onkeyup="value=value.replace(/[^\d\.]/g,'')" title="请输入合法金额！" onblur="return filling();"/>
		金额单位：<input type="text" name="curname" id="curname"  title="货币类型"  size="8"
      		onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" autoid="281" class="required">
	</td>
</tr>

<tr>
	<td>
		<table width="" align="center" border="0" id="mytable" name="mytable" cellSpacing="1" cellPadding="3" bgColor="#eeeeee" >
	<tr align="center" bgColor="#B9C4D5" >
		<td width="60">选</td>
		<td width="80">股东名称</td>
		<td width="100">占注册百分比(%)</td>
    	<td width="80">注册金额</td>
		<td width="80">投资总额</td>
		<td width="80">实收金额</td>
		<td width="80">出资比例</td>
	</tr>
	<tbody id="addBody" name="addBody">
		<c:forEach items="${stockholders}" var="stockholder">
			<tr bgColor="#eeeeee" >
				<td width="60" align="center"><input type=checkbox id=checkLine name="checkbox"></td>
				<td width="80"><input type="text"
									name="name"
									value="${stockholder.name}"
									class="required"
									style="width: 80px; height:22px">
				</td>
				<td width="80"><input type="text"
									name="percentage"
									value="${stockholder.percentage}"
									title="占注册百分比"
									onblur="return onLeave(this);"
									class="required"
									style="width: 100px; height:22px">
				</td>
				<td width="80"><input type="text"
									name="registerFund"
									value=""
									style="width: 80px; height:22px"
									readonly="readonly">
				</td>
				<td width="80"><input type="text"
									name="totalFund"
									value=""
									title="投资总额"
									onblur="return onLeave(this);"
									class="required"
									style="width: 80px; height:22px"
									/>
				</td>
				<td width="80"><input type="text"
									name="factFund"
									title="实收金额"
									onblur="return onLeave(this);"
									value="${stockholder.factFund}"
									class="required"
									style="width: 80px; height:22px">
				</td>
				<td width="80"><input type="text"
									name="percentOfFund"
									value=""
									style="width: 80px; height:22px"
									readonly="readonly">
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>
	</td>
</tr>

</table>




<br>

<center>
<input type="button" value="增  加" class="flyBT" onclick="addLine();">
&nbsp;&nbsp;&nbsp;
<input type="button" value="删  除" class="flyBT" onclick="deleteLine();">
&nbsp;&nbsp;&nbsp;
<input type="button" value="保  存" class="flyBT" onclick="saveCheck();">
&nbsp;&nbsp;&nbsp;
<!-- <input type="button" value="关  闭" class="flyBT" onclick="window.close();"> -->
</center>
</form>
</body>
</html>
<script>
if(<%=list.size() %> > 0){

	var percentage = document.getElementsByName("percentage");
	var registerFund = document.getElementsByName("registerFund");
	var totalFund = document.getElementsByName("totalFund");
	var factFund = document.getElementsByName("factFund");
	var percentOfFund = document.getElementsByName("percentOfFund");

	for(i=0; i<registerFund.length; i++){
		registerFund[i].value = register;

		totalFund[i].value = Math.round(1000*parseFloat(registerFund[i].value)*parseFloat(percentage[i].value/1000)/100);
		percentOfFund[i].value = Math.round(10000*parseFloat(factFund[i].value)/parseFloat(totalFund[i].value))/100 + "%";
	}
}

if(register=="null"){
	document.getElementById("register").value = "";
}else{
	document.getElementById("register").value = register;
}

if(curname=="null"){
	document.getElementById("curname").value = "";
}else{
	document.getElementById("curname").value = curname;
}


</script>