<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>人员考核</title>
</head>
<body style=" margin: 5px; padding: 0px;">

<font color="#FF0000" size="2"><strong>您现在所在位置： </strong></font>
<font color="#0000CC">人员管理-&gt;人员考核管理-&gt;新增人员考核</font>
<br>
<table width="500" border="0" cellpadding="0" cellspacing="0" id="">
	<tr>
		<td height="1" bgcolor="#0000FF"></td>
	</tr>
</table>
<br>

<form name="thisForm" method="post">

<div id="choose" style="display: none">请选择考核人员：<input type="text"
	name="userid" id="userid" size="10" onkeydown="onKeyDownEvent();"
	onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"
	class="required" value="${userid}" autoid=2025 title="请选择,不能为空!" /> <input
	type="button" class="flyBT" value="确  定" onclick="setUserID();">
</div>
<br>

<div id="result" style="display: none">
<table cellpadding="3" cellspacing="1" bgcolor="#6595d6" width="100%"
	border="0" id="setlevel">
	<tr align="center" bgColor="#B9C4D5">
		<td>指标名称</td>
		<td>人员对应值</td>
		<td>参考分数</td>
		<td>最终分数</td>
		<td>说明</td>
	</tr>
	<c:forEach items="${userExamineList}" var="userExamine" varStatus="var">
		<tr onMouseOver="this.bgColor='#E4E8EF';"
			onMouseOut="this.bgColor='#F3F5F8';" bgColor="#F3F5F8">
			<td width="20%"><input type="hidden" name="cname"
				value="${userExamine.examineName}">${userExamine.examineName}</td>
			<td width="10%"><input type="hidden" name="objvalue"
				value="${userExamine.objectiveValue}">
			${userExamine.objectiveValue}</td>
			<td width="5%"><input type="hidden" name="sysscore"
				value="${userExamine.systemScore}">${userExamine.systemScore}</td>
			<td width="10%"><input type="text" size="5" name="userscore"
				value="${userExamine.systemScore}" onblur="setTotalScore();"
				onkeyup="value=value.replace(/[^\d\.]/g,'')"></td>
			<td name="cmemo" width="55%">${userExamine.examineMome}</td>
		</tr>
	</c:forEach>
	<c:forEach items="${userExamineDX}" var="userExamine" varStatus="var">
		<tr onMouseOver="this.bgColor='#E4E8EF';"
			onMouseOut="this.bgColor='#F3F5F8';" bgColor="#F3F5F8">
			<td width="20%"><input type="hidden" name="cname"
				value="${userExamine.examineName}">${userExamine.examineName}</td>
			<td width="10%">${userExamine.objectiveValue}</td>
			<td width="5%"><input type="hidden" name="sysscore"
				value="${userExamine.systemScore}">${userExamine.systemScore}</td>
			<td width="10%"><input type="text" size="5" name="userscore"
				value="${userExamine.systemScore}" onblur="setTotalScore();"
				onkeyup="value=value.replace(/[^\d\.]/g,'')"></td>
			<td name="cmemo" width="55%">${userExamine.examineMome}</td>
		</tr>
	</c:forEach>
	<tr bgColor="#ffffff">
		<td colspan="5" align="right" id="total">总分：</td>
	</tr>
</table>

<br>
<center><input type="button" class="flyBT" id="save"
	value="保  存" onclick="setLevel();">&nbsp;&nbsp;&nbsp; <input
	type="button" class="flyBT" value="返  回" onclick="getBack();">
</center>

</div>

<input type="hidden" name="toall" id="toall" value="${toall}"> <input
	type="hidden" name="cnames" id="cnames"> <input type="hidden"
	name="objvalues" id="objvalues"> <input type="hidden"
	name="sysscores" id="sysscores"> <input type="hidden"
	name="userscores" id="userscores"> <input type="hidden"
	name="userid" value="${userid}"> <input type="hidden"
	name="totalscores" id="totalscores"> <input type="hidden"
	name="deflevel" id="deflevel" value="未选"></form>

</body>

<script>
	try{		
		setTotalScore();
		var all = "${all}";
		if("${userid}"!="") {
			if(!checkState()){
				alert("该人员尚有未评审的申请！");
				document.getElementById("save").disabled = true;
			}
		}
	}catch(e){
	
	}
	
	function setTotalScore(){
		var scores = document.getElementsByName("userscore");
		var total = 0;
		
		for(i=0;i<scores.length;i++){			
			total+=(scores[i].value)*1;
		}
		
		document.getElementById("totalscores").value = "" + total; 
		document.getElementById("total").innerHTML = "总分：" + "<font color='red'>"+total+"</font>"; 
	}
	
	function setLevel(){
		var cnames = document.getElementsByName("cname");
		var objvalues = document.getElementsByName("objvalue");
		var sysscores = document.getElementsByName("sysscore");
		var userscores = document.getElementsByName("userscore");
	
		//指标名称	
		var thecnames = "";
		for(i=0;i<cnames.length;i++){
			thecnames+=cnames[i].value+",";
		}
		document.getElementById("cnames").value = thecnames.substring(0,thecnames.length-1);
			
		//人员考核值	
		var theobjvalues = "";
		for(i=0;i<objvalues.length;i++){
			theobjvalues+=objvalues[i].value+",";
		}
		theobjvalues += document.getElementById("deflevel").value;
		document.getElementById("objvalues").value = theobjvalues;
	
		//系统分数
		var thesysscores = "";
		for(i=0;i<sysscores.length;i++){
			thesysscores+=sysscores[i].value+",";
		}
			
		document.getElementById("sysscores").value = thesysscores.substring(0,thesysscores.length-1);
    
		//人员考核分数
		var theuserscores = "";
		for(i=0;i<userscores.length;i++){
			theuserscores+=userscores[i].value+",";
		}
		document.getElementById("userscores").value = theuserscores.substring(0,theuserscores.length-1);	

		var form = document.thisForm; 
		var all = "${all}";
		form.action = "${pageContext.request.contextPath}/userLevel.do?method=saveUserLevel&all="+all;
		form.submit();						
	}
	
	function checkState(){
		var aJax = new ActiveXObject("Microsoft.XMLHTTP");
		var url = "${pageContext.request.contextPath}/userLevel.do?method=checkUserLevel&userid="+"${userid}"+"&random="+ Math.random();
    	aJax.open("POST",url,false);
    	aJax.send();  
   
    	var result = aJax.responseText;
		if(result=="yes"){
			return true;
		}else{
			return false;
		}		
	}
	
	function FF1(the,count){
		var theselect;// = document.getElementById("D1");
		var selectlevel;// = document.getElementById("D1").value;
		var tablename = document.getElementById("setlevel");	
		var tablelength = tablename.rows.length;
		selectlevel = the.value;
		theselect = the;
		tablename.rows(tablelength-count).childNodes(2).innerHTML = "<input type=\"hidden\" name=\"sysscore\" value=\"" +selectlevel+ "\" >"+selectlevel;
		tablename.rows(tablelength-count).childNodes(3).innerHTML = "<input type=\"text\" size=\"5\" name=\"userscore\" value=\"" +selectlevel+ "\" onblur=\"setTotalScore();\" onkeyup=\"value=value.replace(/[^\\d\\.]/g,'')\">";
		for(var i=0 ; i < theselect.options.length; i++) {
			if(theselect.options[i].selected) {
				document.getElementById("deflevel").value = theselect.options[i].innerText; 
				break;
			}
		}
		setTotalScore();
	}
	
	function getBack(){
		var toall = document.getElementById("toall").value;
		var userid = "${userid}";
		var all = "${all}";

		if(toall=="true"){
			window.location="${pageContext.request.contextPath}/userLevel.do?method=levelHistory&all="+all;
		}else{		
			window.location="${pageContext.request.contextPath}/userLevel.do?method=levelHistory&userid="+userid+"&all="+all;
		}
	}
	
	function setUserID(){
		var userid = document.getElementById("userid").value;
		if(userid==""){
			alert("请先选择人员编号！");
			return false;
		}else{
			window.location = "${pageContext.request.contextPath}/userLevel.do?method=setLevel&toall=true&all=${all}&userid="+userid;				
		}
	}
	
	function setResult(a_Num , a_Bit){
		return (( Math.round((a_Num + Math.pow(10, -a_Bit - 6)) * Math.pow (10 , a_Bit)) / Math.pow(10 , a_Bit)).toFixed(a_Bit));
	}
	
	try{//"${userid}"=="" && 
		if("all"=='${all}'){
			document.getElementById("choose").style.display = "";
		}
		if("${userid}"!=""){
			document.getElementById("result").style.display = "";
			if("${toall}"=="true"){
				document.getElementById("choose").style.display = "";
			}
		}
	}catch(e){
	
	}
					
</script>

</html>
