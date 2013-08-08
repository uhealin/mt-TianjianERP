<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>合同结算计划</title>

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
            	window.location="${pageContext.request.contextPath}/bargainbalance.do?frameTree=${frameTree}";
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


<input name="autoid" type="hidden" id="autoid" value="${autoid}" />
<input name="bargainid" type="hidden" id="bargainid" value="${bargainid}" />
<br>

<form name="thisForm1" action="" method="post">
<table width="100%" height="10" border="0" cellpadding="0"
	cellspacing="0">
	<!--  中天粤
	<tr>
		<td width="25%">
		<div align="right"><font color="red" size=3>*</font>业务约定书编号：</div>
		</td>
		<td width="75%">
		<c:if test="2=1">v
			<input type="text" name="bargainid"
				class="required" id="bargainid" size="20"
				onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();"
				onclick="onPopDivClick(this);" value="${bargainid }"
				valuemustexist=true autoid=2014 />
		</c:if>
				<input type="text" name="bargainid"
						class="required" id="bargainid" size="20"
						onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();"
						onclick="onPopDivClick(this);" value="${bargainid }"
						valuemustexist=true autoid=870 /> &nbsp;&nbsp; 
			<input type="submit" name="srh" class="flyBT" value="确 定" onclick="return checkit();" />
		</td>
	</tr>
	-->
	<tr>
		<td width="25%">
		<div align="right"><font color="red" size=3>*</font>项目名称：</div>
		</td>
		<td width="75%">
				<input type="text" name="projectId"
						class="required" id="projectId" size="20"
						onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();"
						onclick="onPopDivClick(this);" value="${bargainid }"
						 autoid=4605 /> &nbsp;&nbsp; 
			<input type="submit" name="srh" class="flyBT" value="确 定" onclick="return checkit();" />
		</td>
	</tr>
</table>
</form>

<div style="display: none" id="contract" name="contract">
<form name="thisForm" action="" method="post">
<jodd:form bean="bbt" scope="request">
	<table width="100%" height="10" border="0" cellpadding="0"
		cellspacing="0">
		 <tr>
			<td><input type="hidden" name="bargainid"  value="${bargainid }"
				id="bargainid" size="50" onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"
				valuemustexist=true autoid="4605"  />&nbsp;&nbsp;</td>
		</tr>
	<!--
		<tr>
			<td width="25%">
			<div align="right"><font color="red" size=3>*</font>甲方：</div>
			</td>
			<td width="75%"><input name="firstparty" type="text"
				id="firstparty" size="50" class="required" 
				  title="请选择有效的合同"></td>
		</tr>
		<tr>
			<td width="25%">
			<div align="right"><font color="red" size=3>*</font>乙方：</div>
			</td>
			<td width="75%"><input name="secondparty" type="text"
				id="secondparty" size="50" class="required" 
				 title="请选择有效的项目"></td>
		</tr>
		 -->
		<tr>
			<td width="25%">
			<div align="right"><font color="red" size=3>*</font>收费金额：</div>
			</td>
			<td width="75%"><input name="projectMoney" type="text"
				id="projectMoney" size="50" class="required"  readonly="readonly"
				value="${planmoney}"  title="请选择有效的合同"></td>
		</tr>
		<tr>
			<td width="25%">
			<div align="right"><font color="red" size=3>*</font>计划结算日期：</div>
			</td>
			<td width="75%"><input name="plandate" type="text" id="plandate"
				maxlength="40" class="required validate-date-cn"></td>
		</tr>


		<tr>
			<td width="25%" valign="top">
			<div align="right"><font color="red" size=3>*</font>计划结算金额：</div>
			</td>
			<td width="75%"><input name="planmoney" type="text" 
				class="required validate-currency" id="planmoney" onkeyup="value=value.replace(/[^\d\.]/g,'')" maxlength="20"></td>
		</tr>
		<tr>
			<td width="25%" valign="top">
			<div align="right"><font color="red" size=3></font>计划结算方式：</div>
			</td>
			<td width="75%"><input name="planfashion" type="text"
				id="planfashion" maxlength="20"></td>
		</tr>
		<tr>
			<td width="25%" valign="top">
			<div align="right"><font color="red" size=3></font>结算条件：</div>
			</td>
			<td width="75%"><textarea rows="8" cols="50"
				name="plancondition" id="plancondition">
</textarea>
		</tr>
	</table>


	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td height="22" >&nbsp;</td>
		</tr>
		<tr>
			<td  align="center"><input type="submit" class="flyBT"
				value="确  定" onclick="return setBlanType()" />
			<input type="submit" class="flyBT"
				value="确定并继续追加" onclick="return setBlanType1()" /></td>
			
		</tr>
	</table>
</div>
</jodd:form>
</form>
</body>

<script>
new Validation("thisForm");
new Ext.form.DateField({			
	applyTo : 'plandate',
	width: 133,
	format: 'Y-m-d'	
});
function setBlanType(){
	var autoid = document.getElementById("autoid").value;
	var bargainid = "${bargainid}";
	//alert(bargainid);
	var bargainmoney = document.getElementById("planmoney").value;
	var url = "/AuditSystem/bargainbalance.do";
	query_String = "method=checkMoney&bargainid="+bargainid+"&bargainmoney="+bargainmoney+"&autoid="+autoid;
	var myreturnValue = ajaxLoadPageSynch(url,query_String);
	/*
	if(myreturnValue!='0.0') {
		alert("你输入的金额超过："+myreturnValue+"　请更正");
		return false;
	}*/
    if(autoid !=""){
       document.thisForm.action="${pageContext.request.contextPath}/bargainbalance.do?method=updateblan&autoid=" + autoid;
        
    }else{
		document.thisForm.action="${pageContext.request.contextPath}/bargainbalance.do?method=addblan";
	}
}

function setBlanType1(){
	var autoid = document.getElementById("autoid").value;
	var bargainid = "${bargainid}";
	
	var bargainmoney = document.getElementById("planmoney").value;
	
	var url = "/AuditSystem/bargainbalance.do";
	query_String = "method=checkMoney&bargainid="+bargainid+"&bargainmoney="+bargainmoney+"&autoid="+autoid;
	var myreturnValue = ajaxLoadPageSynch(url,query_String);
	if(myreturnValue!='0.0') {
		alert("你输入的金额超过："+myreturnValue+"　请更正");
		return false;
	}
    if(autoid !=""){
       document.thisForm.action="${pageContext.request.contextPath}/bargainbalance.do?method=updateblan&autoid="+autoid+"&goon=1";
        
    }else{
		document.thisForm.action="${pageContext.request.contextPath}/bargainbalance.do?method=addblan&goon=1";
	}
}

function checkit(){
	var tname = document.getElementById("projectId").value;
	//thisForm1.action = "${pageContext.request.contextPath}/bargainbalance.do?method=edit";
	thisForm1.action = "${pageContext.request.contextPath}/bargainbalance.do?method=loadMoney";
	if(tname != ""){
		document.getElementById("contract").style.display= "";
    }
    
}

//判断有没选择对应的合同
try{
	var bargainid = "${bargainid}";
	
	if(bargainid!=""&& bargainid!=null){
		document.getElementById("contract").style.display= "";
	}else{
	document.getElementById("contract").style.display= "none";
	}
	
}catch(e){

}

</script>
</html>
