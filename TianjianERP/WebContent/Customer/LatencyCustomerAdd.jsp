<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>增加客户潜在项目</title>

<script type="text/javascript">  

function ext_init(){
	
	var tbar_customer = new Ext.Toolbar({
   		renderTo: "divBtn",
   		height:30,
   		defaults: {autoHeight: true,autoWidth:true},
           items:[{ 
            text:'保存',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/save.gif',
            handler:function(){
				addOrupdate();
			}
      	},'-',{ 
            text:'返回',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/back.gif',
            handler:function(){
				window.location='${pageContext.request.contextPath}/latencyCustomer.do?customerid=${param.customerid}&frameTree=1';
			}
      	},'-',new Ext.Toolbar.Fill()]
	});
	
	var udate,edate,edate1;
    if(!udate) {
	    udate = new Ext.form.DateField({
			applyTo : 'recodeTime',
			width: 135,
			format: 'Y-m-d',
			emptyText: '' 
		});
    }
	
	if(!edate) {
		edate = new Ext.form.DateField({
			applyTo : 'planTime',
			width: 135,
			format: 'Y-m-d',
			emptyText: '' 
		});
	} 
	
	if(!edate1) {
		edate1 = new Ext.form.DateField({
			applyTo : 'denoteTime',
			width: 135,
			format: 'Y-m-d',
			emptyText: '' 
		});
	} 	
} 



window.attachEvent('onload',ext_init);
</script>

</head>

<body >
<div id="divBtn" ></div>
<div style="height:expression(document.body.clientHeight-30);overflow: auto" >

<input name="autoid" type="hidden" id="autoid" value="${autoid}"/> 

<fieldset  style="width:100%">
<legend>客户潜在项目信息</legend>
<br>
<jodd:form bean="LatencyCustomer" scope="request">
<form name="thisForm" action="" method="post">

<input name="customerid" type="hidden" id="customerid" value="${param.customerid}"/> 

<table width="100%" height="150" border="0" cellpadding="0" cellspacing="0">  
  <c:if test="${param.isAll=='all'}">
	  <tr>
	    <td width="25%"><div align="right">客户编号：</div></td>
	    <td width="75%" ><input type="text" name="CustomerID" id="CustomerID" size="10" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"  class="required"  autoid=2  title="请选择,不能为空!"/>
	    </td>
	  </tr>
  </c:if>
  
  <tr>
    <td width="25%"><div align="right">记录人：</div></td>
    <td width="75%" ><input type="text" name="recoder" id="recoder" class="required" title="请输入,不能为空!">
    </td>
  </tr>
  
  <tr>
    <td width="25%"><div align="right">记录时间：</div></td>
    <td width="75%" ><input type="text" name="recodeTime" id="recodeTime"  class="validate-date-cn"> 
    </td>
  </tr>
  
  <tr>
    <td width="25%"><div align="right">可行性评估：</div></td>
    <td width="75%" ><input type="text" name="viable" id="viable" class="required" title="请输入,不能为空!">
    </td>
  </tr>
 
  <tr>
    <td width="25%"><div align="right">项目编号：</div></td>
    <td width="75%" >
    	<input type="hidden" name="userLoginId" id="userLoginId" value="${userSession.userId }" >	
    	<input type="text" name="projectId" id="projectId" class="required" title="请输入,不能为空!"  refer="userLoginId"  onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"  class="validate-alpha"  autoid=139  value="" autoWidth="450">
    </td>
  </tr>
  
  <tr>
    <td width="25%" valign="top"><div align="right">项目信息：</div></td>
    <td width="75%" ><textarea cols="50" rows="8" name="projectInformation" id="projectInformation" class="required" title="请输入,不能为空!"></textarea>
    </td>
  </tr>

  <tr>
    <td width="25%"><div align="right">预计时间：</div></td>
    <td width="75%" ><input type="text" name="planTime" id="planTime"  class="validate-date-cn">
    </td>
  </tr>
   
  <tr>
    <td width="25%" valign="top"><div align="right">后继跟踪指示：</div></td>
    <td width="75%" ><textarea cols="50" rows="8" name="nextDenote" id="nextDenote" class="required" title="请输入,不能为空!"></textarea>
    </td>
  </tr>
  
  <tr>
    <td width="25%"><div align="right">后继跟踪责任人：</div></td>
    <td width="75%" ><input type="text" name="nextPrincipal" id="nextPrincipal" > 
    </td>
  </tr>
  
  <tr>
    <td width="25%"><div align="right">指示人：</div></td>
    <td width="75%" ><input type="text" name="denotePerson" id="denotePerson"> 
    </td>
  </tr>
  
  <tr>
    <td width="25%"><div align="right">指示时间：</div></td>
    <td width="75%" ><input type="text" name="denoteTime" id="denoteTime"  class="validate-date-cn"> 
    </td>
  </tr>
</table>

  

</form>
</jodd:form>
</fieldset>

</div>
</body>

<script>

	new Validation("thisForm");
	 
	function addOrupdate(){
			
		var autoid = document.getElementById("autoid").value;
	
	    if(autoid!=""){
	    	document.thisForm.action="${pageContext.request.contextPath}/latencyCustomer.do?method=updateLatencyCustomer&autoid=" + autoid;
	    }else{
			document.thisForm.action="${pageContext.request.contextPath}/latencyCustomer.do?method=addLatencyCustomer";
		}
		document.thisForm.submit();
		
	}
	
//	try{
//		var customerIdvalue = document.getElementById("customerId").value;
//	
//	    if(customerIdvalue!=""){
//	    	document.getElementById("update").style.display = "";
//	    	document.getElementById("add").style.display = "none";
//	    }else{
//	    	document.getElementById("update").style.display = "none";
//	    	document.getElementById("add").style.display = "";
//	    }
//	}catch(e){
//	
//	}

</script>
</html>