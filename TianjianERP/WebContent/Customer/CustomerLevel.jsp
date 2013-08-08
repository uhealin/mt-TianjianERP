<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>客户等级</title>

<script type="text/javascript">  

function ext_init(){
	
	var tbar_customer = new Ext.Toolbar({
   		renderTo: "divBtn",
   		height:30,
   		defaults: {autoHeight: true,autoWidth:true},
           items:[{ 
            id:'saveid',
            text:'保存',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/save.gif',
            handler:function(){
				setLevel();
			}
      	},'-',{ 
            text:'返回',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/back.gif',
            handler:function(){
				getBack();
			}
      	},'-',new Ext.Toolbar.Fill()]
	});
	
	try{		
		setTotalScore();
		if(!checkState()){
			alert("该客户尚有未评审的申请！");
			
			Ext.getCmp('saveid').disable();
			
		}
	}catch(e){
	
	}
} 



window.attachEvent('onload',ext_init);
</script>



</head>
<body >
<div id="divBtn" ></div>


<form name="thisForm" method="post">

<div id="choose" style="display: none">
请选择客户编号：<input type="text" name="CustomerID" id="CustomerID" size="10" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"  class="required" value="${customerid}" autoid=2  title="请选择,不能为空!"/>
<input type="button" class="flyBT" value="确  定" onclick="setCustomerID();">
</div>
<br>

<div id="result" style="display: none">
<table cellpadding="3" cellspacing="1" bgcolor="#6595d6" width="100%" border="0" id="setlevel">
<tr align="center" bgColor="#B9C4D5">
	<td>指标名称</td>
	<td>客户值</td>
	<td>系统分数</td>
	<td>客户分数</td>
	<td>说明</td>
</tr>
<c:forEach items="${customerExamineList}" var="customerExamine" varStatus="var">
	<tr onMouseOver="this.bgColor='#E4E8EF';" onMouseOut="this.bgColor='#F3F5F8';"  bgColor="#F3F5F8">
		<td width="20%"><input type="hidden" name="cname" value="${customerExamine.examineName}">${customerExamine.examineName}</td>	
		<td width="10%"><input type="hidden" name="objvalue" value="${customerExamine.objectiveValue}"> ${customerExamine.objectiveValue}</td>	
		<td width="5%"><input type="hidden" name="sysscore" value="${customerExamine.systemScore}">${customerExamine.systemScore}</td>
		<td width="10%"><input type="text" size="5" name="userscore" value="${customerExamine.systemScore}" onblur="setTotalScore();" onkeyup="value=value.replace(/[^\d\.]/g,'')"></td>
		<td name="cmemo" width="55%">${customerExamine.examineMome}</td>
	</tr>
</c:forEach>
<c:forEach items="${customerExamineDX}" var="customerExamine" varStatus="var">
	<tr onMouseOver="this.bgColor='#E4E8EF';" onMouseOut="this.bgColor='#F3F5F8';"  bgColor="#F3F5F8">
		<td width="20%"><input type="hidden" name="cname" value="${customerExamine.examineName}">${customerExamine.examineName}</td>	
		<td width="10%">${customerExamine.objectiveValue}</td>	
		<td width="5%"><input type="hidden" name="sysscore" value="${customerExamine.systemScore}">${customerExamine.systemScore}</td>
		<td width="10%"><input type="text" size="5" name="userscore" value="${customerExamine.systemScore}" onblur="setTotalScore();" onkeyup="value=value.replace(/[^\d\.]/g,'')"></td>
		<td name="cmemo" width="55%">${customerExamine.examineMome}</td>
	</tr>
</c:forEach>
<tr bgColor="#ffffff">
  <td colspan="5" align="right" id="total">总分：</td>
</tr>
</table>


</div>

<input type="hidden" name="toall" id="toall" value="${toall}">
<input type="hidden" name="cnames" id="cnames">
<input type="hidden" name="objvalues" id="objvalues">
<input type="hidden" name="sysscores" id="sysscores">
<input type="hidden" name="userscores" id="userscores">
<input type="hidden" name="customerid" value="${customerid}">
<input type="hidden" name="totalscores" id="totalscores">
<input type="hidden" name="deflevel" id="deflevel" value="未选">
</form>

</body>

<script>
	
	
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
			
		//客户值	
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
    
		//客户分数
		var theuserscores = "";
		for(i=0;i<userscores.length;i++){
			theuserscores+=userscores[i].value+",";
		}
		document.getElementById("userscores").value = theuserscores.substring(0,theuserscores.length-1);	

		var form = document.thisForm; 
		form.action = "${pageContext.request.contextPath}/customer.do?method=saveCustomerLevel";
		form.submit();						
	}
	
	function checkState(){
		var aJax = new ActiveXObject("Microsoft.XMLHTTP");
    	aJax.open("POST","${pageContext.request.contextPath}/customer.do?method=checkCustomerLevel&customerid="+"${customerid}"+"&random="+ Math.random(),false);
    	aJax.send();  
   
    	var result = aJax.responseText;
		if(result=="yes"){
			return true;
		}else{
			return false;
		}		
	}
	//定性的下拉
	function FF1(the,count){
		var theselect;// = document.getElementById("D1");
		var selectlevel;// = document.getElementById("D1").value;
		var tablename = document.getElementById("setlevel");	
		var tablelength = tablename.rows.length;
		selectlevel = the.value;
		var num = "${num}";
		theselect = the;
		tablename.rows(tablelength-num+tablelength-count-3).childNodes(2).innerHTML = "<input type=\"hidden\" name=\"sysscore\" value=\"" +selectlevel+ "\" >"+selectlevel;
		tablename.rows(tablelength-num+tablelength-count-3).childNodes(3).innerHTML = "<input type=\"text\" size=\"5\" name=\"userscore\" value=\"" +selectlevel+ "\" onblur=\"setTotalScore();\" onkeyup=\"value=value.replace(/[^\\d\\.]/g,'')\">";
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
		var customerid = "${customerid}";

		if(toall=="true"){
			window.location="${pageContext.request.contextPath}/customer.do?method=levelHistory";
		}else{		
			window.location="${pageContext.request.contextPath}/customer.do?method=levelHistory&customerid="+customerid;
		}
	}
	
	function setCustomerID(){
		var customerid = document.getElementById("CustomerID").value;
		if(customerid==""){
			alert("请先选择客户编号！");
			return false;
		}else{
			window.location = "${pageContext.request.contextPath}/customer.do?method=setLevel&toall=true&customerid="+customerid;				
		}
	}
	
	function setResult(a_Num , a_Bit){
		return (( Math.round((a_Num + Math.pow(10, -a_Bit - 6)) * Math.pow (10 , a_Bit)) / Math.pow(10 , a_Bit)).toFixed(a_Bit));
	}
	
	try{
		if("${customerid}"==""){
			document.getElementById("choose").style.display = "";
		}else{
			document.getElementById("result").style.display = "";
			if("${toall}"=="true"){
				document.getElementById("choose").style.display = "";
			}
		}
	}catch(e){
	
	}
					
</script>

</html>