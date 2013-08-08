<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>合同实际结算登记</title>

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
            	window.location="${pageContext.request.contextPath}/practicalbalance.do?frameTree=${frameTree}";
				//window.history.back();
			}
      	}]
	}); 
	
} 

window.attachEvent('onload',ext_init);

</script>

</head>
<body >
<div id="divBtn" ></div>

<input type="hidden" id="bargainid" name="bargainid" value="${bargainid }" />
<form name="thisForm1" method="post"
	action="/AuditSystem/practicalbalance.do?method=edit" id="thisForm1">

<br/>
<table width="100%" height="10" border="0" cellpadding="0" 
		cellspacing="0">
	
		<tr>
			<td width="25%" align="right"><font color="red" size=3>*</font>相关合同:</td>
			<td width="75%"><input name="cid" type="text" class='required'
				id="cid" maxlength="40" onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"
				valuemustexist="true" autoid="2014"  value="${bargainid }">&nbsp;<input type="submit" value="确　定" class="flyBT" onclick=""></td>
	</tr>
</table>
</form>
<div id="mypracticalbalance" name="mypracticalbalance">
<form name="thisForm" method="post"
	action="/AuditSystem/postchange.do?method=add" id="thisForm">
<jodd:form bean="pbt" scope="request">
	<table width="100%" height="46" border="0" cellpadding="0"
		cellspacing="0">
		<tr>
			<br />
		</tr>
		
		<tr>
			<td align="right" width="25%"><font color="red" size=3>*</font>甲方：</td>
			<td  width="75%"><input id="firstparty" type="text" class="required" size="40"
				 name="firstparty" title="请选择有效的合同！" readonly="readonly" style="background-color: #eeeeee;"/>
				<input name="cid" type="hidden" class='required'
				id="cid" maxlength="40" onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"
				valuemustexist="true" autoid="2014">
				</td>
		</tr>
		<tr>
			<td align="right" width="25%"><font color="red" size=3>*</font>乙方：</td>
			<td  width="75%"><input type="text" id="secondparty" class="required" size="40"
				name="secondparty" title="请选择有效的合同" readonly="readonly" style="background-color: #eeeeee;"/></td>
		</tr>
		<tr>
			<td align="right" width="25%"><font color="red" size=3>*</font>合同金额：</td>
			<td  width="75%"><input type="text" id="contractmoney" class="required" size="40"
				name="contractmoney" title="请选择有效的合同" value="${contractmoney }" readonly="readonly" style="background-color: #eeeeee;"/></td>
		</tr>
		<tr>
			<td align="right" width="25%"><font color="red" size=3>*</font>实际结算日期：</td>
			<td  width="75%"><input name="bargaindate" type="text" id="bargaindate"
				class="required validate-date-cn" title="请输入日期！" showcalendar="true">
			</td>
		</tr>
		<tr>
			<td align="right" width="25%"><font color="red" size=3>*</font>实际结算金额：</td>
			<td width="75%"><input id="bargainmoney" type="text" class="required validate-currency"
				maxlength="18" name="bargainmoney" onkeyup="value=value.replace(/[^\d\.]/g,'')" title="请输入有效的金额！"/></td>
		</tr>
		<tr>
			<td align="right" width="25%"><font color="red" size=3></font>实际结算方式：</td>
			<td width="75%"><input id="bargaintype" type="text" 
				maxlength="18" name="bargaintype"  /></td>
		</tr>
		
		<tr>
			<td align="right" width="25%"><font color="red" size=3></font>发票号：</td>
			<td  width="25%"><input id="invoicenumber" type="text" 
				maxlength="18" name="invoicenumber" class="validate-digits" title="请输入有效的发票号！"/></td>
		</tr>
		<tr>
			<td align="right" width="25%"><font color="red" size=3></font>对应结算计划：</td>
			<td width="25%"><textarea rows="6" cols="40" id="bargainplan" name="bargainplan"></textarea>
			
			</td>
		</tr>
	</table>



<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td height="22" >&nbsp;</td>
	</tr>
	<tr>
		<td  align="center"><input type="submit" name="next"
			value="确  定" class="flyBT" onclick="return mySubmit();";>
		<input type="submit" name="next"
			value="确定并继续追加" class="flyBT" onclick="return mySubmit1();";></td>
	</tr>
</table>

</jodd:form>
</form>
</div>
<script type="text/javascript">
new Validation('thisForm');
if(${bargainid!='null' &&  bargainid!=''}) {
	document.getElementById("mypracticalbalance").style.display = "";
} else {
	document.getElementById("mypracticalbalance").style.display = "none";
}
function mySubmit() {
	var autoid = '${autoid}';
	var bargainid = "${bargainid}";
	var bargainmoney = document.getElementById("bargainmoney").value;
	var url = "/AuditSystem/practicalbalance.do";
	query_String = "method=checkMoney&bargainid="+bargainid+"&bargainmoney="+bargainmoney+"&autoid="+autoid;
	var myreturnValue = ajaxLoadPageSynch(url,query_String);
	if(myreturnValue!='0.0') {
		alert("你输入的金额超过："+myreturnValue+"　请更正");
		return false;
	}

	if(autoid!='null' && autoid!='') {
		thisForm.action="/AuditSystem/practicalbalance.do?method=update&autoid=${autoid}";
	} else {
	
		thisForm.action="/AuditSystem/practicalbalance.do?method=add";
	}
}

function mySubmit1() {
	var autoid = '${autoid}';
	var bargainid = "${bargainid}";
	var bargainmoney = document.getElementById("bargainmoney").value;
	var url = "/AuditSystem/practicalbalance.do";
	query_String = "method=checkMoney&bargainid="+bargainid+"&bargainmoney="+bargainmoney+"&autoid="+autoid;
	var myreturnValue = ajaxLoadPageSynch(url,query_String);
	if(myreturnValue!='0.0') {
		alert("你输入的金额超过："+myreturnValue+"　请更正");
		return false;
	}

	if(autoid!='null' && autoid!='') {
		thisForm.action="/AuditSystem/practicalbalance.do?method=update&autoid=${autoid}&goon=1";
	} else {
	
		thisForm.action="/AuditSystem/practicalbalance.do?method=add&goon=1";
	}
}

</script>

</body>
</html>
