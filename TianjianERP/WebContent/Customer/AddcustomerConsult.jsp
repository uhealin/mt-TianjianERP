<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.*"%>
<%@page import="com.matech.framework.listener.UserSession"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>增加或修改客户接洽记录</title>

<script type="text/javascript">  

function ext_init(){
	
	var tbar_customer = new Ext.Toolbar({
   		renderTo: "divBtn",
   		height:30,
   		defaults: {autoHeight: true,autoWidth:true},
           items:[{ 
            text:'返回',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/back.gif',
            handler:function(){
				getBack();
			}
      	},'-',new Ext.Toolbar.Fill()]
	});
	
} 



window.attachEvent('onload',ext_init);
</script>

</head>

<%
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	String today = sdf.format(new Date());

	UserSession userSession = (UserSession) request.getSession()
			.getAttribute("userSession");
	String username = userSession.getUserName();
%>


<body >
<div id="divBtn" ></div>

<div style="height:expression(document.body.clientHeight-30);overflow: auto" >

<input name="autoid" type="hidden" id="autoid" value="${autoid}" />

<fieldset style="width:100%"><legend>业务咨询记录</legend> <br>

<form name="thisForm" action="" method="post">
<input name="customerid" id="customerid" type="hidden" value="${param.customerid}" />

<table width="70%" height="150" border="0" cellpadding="0"
	cellspacing="0"  style="margin-left: 50px;line-height: 27px;">

	<tr>
		<td width="25%">
		<div align="right">通讯类型：</div>
		</td>
		<td width="75%"><input type="text" name="ctype"
			id="ctype"   onfocus="onPopDivClick(this);"
			onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();"
			onclick="onPopDivClick(this);" norestorehint=true noinput=true
			  autoid=372 class="required"
			></td>
	</tr>
	
	 <tr>
		<td width="25%">
		<div align="right">通讯号码：</div>
		</td>
		<td width="75%"><input type="text" name="number"
			id="number" onblur="ajaxCustomer(this.value)" class="required"></td>
	</tr>
		<tr>
		<td width="25%">
		<div align="right">联系人：</div>
		</td>
		<td width="75%"><input type="text" name="linkMan" id="linkMan"
			class="required" title="请输入,不能为空!" value="${CustomerConsult.linkMan}">
		</td>
	</tr>
	 <tr >
		<td  colspan="2" align="center">
		<div style="width:50%;text-align: left;"><hr></div>
		</td>
		 
	</tr>
	<tr id="phone" style="display: none;">
		<td width="25%">
		<div align="right">联系人电话：</div>
		</td>
		<td width="75%"><input type="text" name="PHONE"
			id="PHONE" class="required validate-phonenumber"
			value="${CustomerConsult.PHONE}" onblur="autoFill(this.value);"></td>
	</tr>
	<tr id="qqmsn" style="display: none;">
		<td width="25%">
		<div align="right">联系人QQ/MSN：</div>
		</td>
		<td width="75%"><input type="text" name="QQ"
			id="QQ" class="validate-number" value="${CustomerConsult.QQ}">
		</td>
	</tr>

	<tr id="email1" style="display: none;">
		<td width="25%">
		<div align="right">联系人Email：</div>
		</td>
		<td width="75%"><input type="text" name="EMAIL"
			id="EMAIL" class="validate-email"
			value="${CustomerConsult.EMAIL}"></td>
	</tr>
	
	<c:if test="${param.isAll=='all'}">
		<tr style="display: none;">
			<td width="25%">
			<div align="right">客户编号：</div>
			</td>
			<td width="75%"><input type="hidden" name="CustomerNumber"
			  id="CustomerNumber" /></td>
		</tr>
	</c:if>

	<tr>
		<td width="25%">
		<div align="right">客户名称：</div>
		</td>
		<td width="75%"><input type="text" size="50" name="customerName"
			id="customerName" class="required" title="请输入,不能为空!"
			value="${CustomerConsult.customerName}"
			onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();"
			onclick="onPopDivClick(this);" norestorehint=true  autoid=601
			></td>
	</tr>
		<tr id="customerManagerView" style="display: none;">
		<td width="25%">
		<div align="right">客户承做人：</div>
		</td>
		<td width="75%" ><input type="hidden" name="customerMainPerson" id="customerMainPerson"><span id="customerManager"></span> </td>
	</tr>
		<tr id="customerDepartNameView" style="display: none;">
		<td width="25%">
		<div align="right">客户所属部门：</div>
		</td>
		<td width="75%" id="customerDepartName"></td>
	</tr>
	 <tr >
		<td  colspan="2" align="center">
		<div style="width:50%;text-align: left;"><hr></div>
		</td>
		 
	</tr>
	<tr>
		<td width="25%">
		<div align="right">来访时间：</div>
		</td>
		<td width="75%">
		<c:choose>
		<c:when test="${CustomerConsult.visitTime == '' || CustomerConsult.visitTime ==null}">
			<input type="text" name="visitTime" id="visitTime" value="<%=today%>" class="required validate-date-cn">
		</c:when>
		<c:otherwise>
				<input type="text" name="visitTime" id="visitTime" value="${CustomerConsult.visitTime}" class="required validate-date-cn">
		</c:otherwise>
		</c:choose>
		</td>
	</tr>

	<tr>
		<td width="25%" valign="top">
		<div align="right">来访事由：</div>
		</td>
		<td width="75%"><textarea cols="50" rows="1" name="visitProblem"  style="overflow:visible; height: 60px;"
			id="visitProblem" class="required" title="请输入,不能为空!" >${CustomerConsult.problem}</textarea>
		</td>
	</tr>

	<tr id="historyRec" style="display: none;">
		<td width="25%" valign="top">
		<div align="right">解决历史记录：</div>
		</td>
		<td width="75%">
		<table width="95%" cellpadding="3" cellspacing="1" bgcolor="#6595d6"
			style="font-size:14px;">
			<tr bgColor="#E4E8EF">
				<td align="center" style="height: 20px;">问题状态</td>
				<td align="center">解决记录</td>
				<td align="center">责任部门</td>
				<td align="center">责任人</td>
				<td align="center">解决期限</td>
				<td align="center">分工人</td>
			</tr>
			<c:forEach items="${ConsultTxtList}" var="consultTxt">
				<tr bgColor="ffffff">
					<td align="center" style="height: 15px;">${consultTxt.state}</td>
					<td align="center">${consultTxt.recordContent}</td>
					<td align="center">${consultTxt.department}</td>
					<td align="center">${consultTxt.person}</td>
					<td align="center">${consultTxt.untillTime}</td>
					<td align="center">${consultTxt.manager}</td>
				</tr>
			</c:forEach>
		</table>
		</td>
	</tr>

	<tr id="problemState">
		<td width="25%">
		<div align="right">问题状态：</div>
		</td>
		<td width="75%"><input name="pstate" type="text" id="pstate"
			size="20" title="请输入，不得为空" onKeyDown="onKeyDownEvent();"
			onKeyUp="onKeyUpEvent();" noinput="true"
			onClick="onPopDivClick(this);" valuemustexist=true autoid=370
			onpropertychange="setState();" class="required"  value= '待处理' ></td>
	</tr>

	<tr id="finishedMan" style="display:none;">
		<td width="25%">
		<div align="right">解决人：</div>
		</td>
		<td width="75%"><input name="fPerson" type="text" id="fPerson"
			size="20" style="background: #EEEEEE;" onfocus="onPopDivClick(this);"
			onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();"
			onclick="onPopDivClick(this);" norestorehint=true class="required"
			noinput=true value="<%=username%>" onchange="setFinishPerson();"
			autoid=252></td>
	</tr>

	<tr id="finishedTimeF" style="display:none;">
		<td width="25%">
		<div align="right">解决时间：</div>
		</td>
		<td width="75%">
		<c:choose>
		<c:when test="${CustomerConsult.dealTime == '' || CustomerConsult.dealTime ==null}">
			<input type="text" name="finishedTime" id="finishedTime" class="required validate-date-cn" value="<%=today %>">
		</c:when>
		<c:otherwise>
				<input type="text" name="finishedTime" id="finishedTime" value="${CustomerConsult.dealTime}" class="required validate-date-cn">
		</c:otherwise>
		</c:choose>
		</td>
	</tr>
	<tr id="finishedRec" style="display: none">
		<td width="25%" valign="top">
		<div align="right">解决记录：</div>
		</td>
		<td width="75%"><textarea cols="50" rows="1" name="finishedRec" style="overflow:visible;height: 60px;"
			id="finishedRec" class="required" title="请输入,不能为空!"></textarea></td>
	</tr>

	<tr id="unfinishedP" style="display: none;">
		<td width="25%" valign="top">
		<div align="right">未尽事宜：</div>
		</td>
		<td width="75%"><textarea cols="50" rows="1" style="overflow:visible;height: 60px;"
			name="unfixQuestion" id="unfixQuestion" class="required"
			title="请输入,不能为空!">${CustomerConsult.unfinishProblem }</textarea></td>
	</tr>
	<tr id="unfinishedD" style="display: none;">
		<td width="25%">
		<div align="right">未尽事宜责任部门：</div>
		</td>
		<td width="75%"><input name="Department" type="text"
			id="Department" size="20" title="请输入，不得为空"
			onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();"
			noinput="true" onClick="onPopDivClick(this);" valuemustexist=true
			onchange="setDepartment();" autoid=123  value="${CustomerConsult.unfinishDepart}" refreshtarget="Person"></td>
	</tr>
	<tr id="unfinishedM" style="display: none;">
		<td width="25%">
		<div align="right">未尽事宜责任人：</div>
		</td>
		<td width="75%"><input name="Person" type="text" id="Person"
			size="20" style="background: #EEEEEE;" onfocus="onPopDivClick(this);"
			onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();"
			onclick="onPopDivClick(this);"  valuemustexist=true
			onchange="setPerson();" autoid=4588 refer="Department"  value="${CustomerConsult.unfinishMan}" ></td>
	</tr>
	<tr id="unfinishedT" style="display: none;">
		<td width="25%">
		<div align="right">解决期限：</div>
		</td>
		<td width="75%"><input type="text" name="untillTime"
			id="untillTime"  class="required validate-date-cn" 
			value="${CustomerConsult.untillTime}"></td>
	</tr>

	<tr id="problemRecorder">
		<td width="25%">
		<div align="right">记录人：</div>
		</td>
		<td width="75%">
		<span><%=username%></span>
		<input type="hidden" name="recoder"
			readonly="readonly" id="recoder" value="<%=userSession.getUserId()%>"
			class="required" title="请输入,不能为空!"></td>
	</tr>

	<tr id="problemRTime">
		<td width="25%">
		<div align="right">记录时间：</div>
		</td>
		<td width="75%">
		<span><%=today%></span>
		<input type="hidden" name="recodeTime"
			readonly="readonly" id="recodeTime" value="<%=today%>"
			class="required validate-date-cn"></td>
	</tr>
</table>


<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td height="22" colspan="3">&nbsp;</td>
	</tr>
	<tr>
		<td width="37%" align="right">
		<div id="living" style="display: none;">
			<input type="submit" class="flyBT" value="激  活" onclick="return setAlive();" />
		</div>
		<div id="saveResult">
			<input type="submit" class="flyBT" value="保  存" onclick="return addOrupdate(1);" />
			<input type="submit" class="flyBT" value="保存并发起商机登记" onclick="return addOrupdate(2);" />
		</div>
		</td>
		<td width="8%">&nbsp;</td>
		<td width="55%">&nbsp;</td>
	</tr>
</table>

<input type="hidden" id="unfixDepartment" name="unfixDepartment" /> 
<input type="hidden" id="unfixPerson" name="unfixPerson"  /> 
<input	type="hidden" id="unfixPerson" name="fixPerson" value="<%=username%>" />
<input name="setcustomerid" type="hidden" id="setcustomerid"	value="${CustomerConsult.customerid}" /></form>

</fieldset>

<c:if test="${autoid==null}">
<iframe id="questionsearch" src="${pageContext.request.contextPath}/questionsearch/Search.jsp" width="100%" height="300">

</iframe>
</c:if>

</div>
</body>  

<script>
new Ext.form.DateField({			
	applyTo : 'visitTime',
	width: 133,
	format: 'Y-m-d'	
});
new Ext.form.DateField({			
	applyTo : 'untillTime',
	width: 133,
	format: 'Y-m-d'	
});
new Ext.form.DateField({			
	applyTo : 'finishedTime',
	width: 133,
	format: 'Y-m-d'	
});

	new Validation("thisForm");
	 
	function addOrupdate(opt){
		if (!formSubmitCheck('thisForm')) return ;
		var autoid = document.getElementById("autoid").value;
	    if(autoid!=""){
	    	document.thisForm.action="${pageContext.request.contextPath}/customerConsult.do?method=updateCustomerTrack&flag=${flag}&autoid=" + autoid + "&opt=" + opt;
	    }else{
	    	//if(document.getElementById("customerDepartNameView").style.display == "none"){
			//	var customerName = document.getElementById("customerName").value;
			//	document.getElementById("CustomerNumber").value =customerName;
			//}
	    	document.thisForm.action="${pageContext.request.contextPath}/customerConsult.do?method=addCustomerTrack&flag=${flag}&opt=" + opt;
		}
	}
	
	function getBack(){
		var customerid = "${param.customerid}";
		if(customerid==""){
			window.location="${pageContext.request.contextPath}/customerConsult.do?isSolve=${flag}";
		}else{
			window.location="${pageContext.request.contextPath}/customerConsult.do?isSolve=${flag}&customerid="+customerid;
		}		
	}
	
	
	
	function setState(){
		var state = document.getElementById("pstate").value;
	    
	    
	    if(state=="已解决"){
	    	document.getElementById("finishedMan").style.display = "";
	    	document.getElementById("finishedTime").style.disabled="disabled";
	    	document.getElementById("finishedRec").style.display = "";
	    	document.getElementById("unfinishedP").style.display = "none";
	    	document.getElementById("unfinishedD").style.display = "none";
	    	document.getElementById("unfinishedM").style.display = "none";
	    	document.getElementById("unfinishedT").style.display = "none";	
	    	document.getElementById("finishedTimeF").style.display = "";	
	    	
	    }else{
	    	document.getElementById("finishedMan").style.display = "none";
	    	document.getElementById("finishedTimeF").style.display = "none";
	    	document.getElementById("finishedRec").style.display = "none";
	    	document.getElementById("unfinishedP").style.display = "";
	    	document.getElementById("unfinishedD").style.display = "";
	    	document.getElementById("unfinishedM").style.display = "";
	    	document.getElementById("unfinishedT").style.display = "";	
	    }	
	}

	function setCustomerName(){
		document.getElementById("customerName").value = document.getElementById("advice-CustomerNumber").innerHTML;	
		setLinks();
	}
	
	function setLinks(){
		var customerId = document.getElementById("CustomerNumber").value;
	
		if(customerId == "") {
			return;
		}
		
		var url = "${pageContext.request.contextPath}/customerConsult.do?method=getLink&customerid=" + customerId;
		
		var result = ajaxLoadPageSynch(url,null);
	    
	    var links = new Array();  	
    	links = result.split(",");
    	
    	document.getElementById("linkMan").value = links[0];
    	document.getElementById("PHONE").value = links[1];
    	document.getElementById("EMAIL").value = links[2];
	}

	function setDepartment(){
		document.getElementById("unfixDepartment").value = document.getElementById("advice-Department").innerHTML;
	}
	
	function setPerson(){
		document.getElementById("unfixPerson").value = document.getElementById("advice-Person").innerHTML;
	}
	
	function setFinishPerson(){
		document.getElementById("fixPerson").value = document.getElementById("advice-fPerson").innerHTML;
	}
	
	function setDisplay(){
		document.getElementById("finishedMan").style.display = "none";
    	document.getElementById("finishedTimeF").style.display="none";
    	document.getElementById("finishedRec").style.display = "none";
    	document.getElementById("unfinishedP").style.display = "none";
    	document.getElementById("unfinishedD").style.display = "none";
    	document.getElementById("unfinishedM").style.display = "none";
    	document.getElementById("unfinishedT").style.display = "none";	
	}
	
	function setAlive(){
		document.getElementById("living").style.display = "none";
		document.getElementById("saveResult").style.display = "";
		
		document.getElementById("problemState").style.display = "";
		document.getElementById("problemRecorder").style.display = "";
		document.getElementById("problemRTime").style.display = "";
	}

	try{
		if(document.getElementById("customerName").value==""){
			document.getElementById("customerName").value = "${param.customerName}";
		}  
	    var autoid = document.getElementById("autoid").value;
		if(autoid!=""){
			document.getElementById("visitTime").value = "${CustomerConsult.visitTime}";
			document.getElementById("recoder").value = "${CustomerConsult.recoder}";
			document.getElementById("recodeTime").value = "${CustomerConsult.recodeTime}";
			if("${CustomerConsult.QQ}" !=""){
				document.getElementById("ctype").value="QQ/MSN";
				document.getElementById("number").value="${CustomerConsult.QQ}";
			}else if("${CustomerConsult.PHONE}" !=""){
				document.getElementById("ctype").value="电话";
				document.getElementById("number").value="${CustomerConsult.PHONE}";
			}else if("${CustomerConsult.EMAIL}" !=""){
				document.getElementById("ctype").value="邮箱";
				document.getElementById("number").value="${CustomerConsult.EMAIL}";
			}
				
			setState();
			
			document.getElementById("historyRec").style.display = "";
			setDisplay();
			
			var thePsate = "${CustomerConsult.state}";
		
			if(thePsate=="已解决"){
				document.getElementById("living").style.display = "";
				document.getElementById("saveResult").style.display = "none";
				
				document.getElementById("problemState").style.display = "none";
				document.getElementById("problemRecorder").style.display = "none";
				document.getElementById("problemRTime").style.display = "none";
		
			}
			
		}
		
	}catch(e){
	
	}


	function autoFill(a) {
		if(a == "") {
			return;
		} else {
			var url = "${pageContext.request.contextPath}/customerConsult.do?method=autoFill&linkManTel=" + a;
		
			var oBao = new ActiveXObject("Microsoft.XMLHTTP");
			oBao.open("POST",url,false);
			oBao.send();
			strResult = oBao.responseText;
			
			if(strResult=="false") {
				return;
			} else {
				var customerInfo = strResult.split("@`@");
				document.getElementById("CustomerNumber").value = customerInfo[0];
				document.getElementById("customerName").value = customerInfo[1];
				document.getElementById("linkMan").value = customerInfo[2];
				document.getElementById("QQ").value = customerInfo[3];
				document.getElementById("EMAIL").value = customerInfo[4];
				
				document.getElementById("customerManager").innerHTML=""; //客户承做人
				document.getElementById("customerDepartName").innerHTML=""; //客户所属部门
			}
		}
		
		
	}
	
	//用异步查询客户信息
	function ajaxCustomer(obj) {
		var ctype = document.getElementById("ctype").value;
		if(obj == "") {
			return;
		} else {
			if(ctype == ""){
				alert("请先选择通讯类型");
				return ;
			}
			
			//var url = "${pageContext.request.contextPath}/customerConsult.do?method=autoFill&linkManTel=" + obj;
			//var oBao = new ActiveXObject("Microsoft.XMLHTTP");
			//oBao.open("POST",url,false);
			//oBao.send();
			
			var url="${pageContext.request.contextPath}/customerConsult.do?method=customerFill";
			var requestString = "&ctype="+ctype+"&number="+obj;
			var request= ajaxLoadPageSynch(url,requestString);
			if(request=="false") {
				document.getElementById("CustomerNumber").value = "";
				document.getElementById("customerName").value =  "";
				document.getElementById("customerManager").innerHTML =  "";
				document.getElementById("customerDepartName").innerHTML =  "";
				document.getElementById("Person").value =  "";
				document.getElementById("Department").value =  "";
				document.getElementById("linkMan").value="";
				document.getElementById("customerManagerView").style.display = "none";
				document.getElementById("customerDepartNameView").style.display = "none";
				
				return;
			} else {
				var customerInfo = request.split("@`@");
				document.getElementById("CustomerNumber").value = customerInfo[0];
				document.getElementById("customerName").value = customerInfo[1];
				
				document.getElementById("customerManager").innerHTML = customerInfo[2];
				document.getElementById("customerDepartName").innerHTML = customerInfo[3];
				document.getElementById("Person").value = customerInfo[4];
				document.getElementById("Department").value = customerInfo[5];
				document.getElementById("linkMan").value=customerInfo[2];
				document.getElementById("customerMainPerson").value=customerInfo[4];
				document.getElementById("customerManagerView").style.display = "block";
				document.getElementById("customerDepartNameView").style.display = "block";
				
			}
		}
		
		
	}
	
	function initChecked() {
		var iframe = document.getElementById('questionsearch');
		var Doc = iframe.document;
		if(iframe.contentDocument){ // For NS6
		    Doc = iframe.contentDocument;
		}else if(iframe.contentWindow){ // For IE5.5 and IE6
		    Doc = iframe.contentWindow.document;
		}
		
		Doc.getElementsByName("Title")[0].checked = "checked";
		Doc.getElementsByName("Author")[0].checked = "checked";
		Doc.getElementsByName("Context")[0].checked = "checked";
	}
</script>

</html>
