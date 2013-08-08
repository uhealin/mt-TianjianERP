<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>	
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp" %>
<%@ page import="java.util.*" %>
<%@page import="com.matech.framework.pub.util.ASFuntion"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>商机录入</title>

<script type="text/javascript">  

function ext_init(){
	

	    
	var tbar_customer = new Ext.Toolbar({
   		renderTo: "divBtn",
   		height:30,
   		defaults: {autoHeight: true,autoWidth:true},
           items:[{ 
            text:'发起跟踪',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/save.gif',
            handler:function(){
				setManager(1);
			}
      	},'-',{ 
            text:'放弃',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/save.gif',
            handler:function(){
				setManager(2);
			}
      	},'-',{ 
            text:'返回',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/back.gif',
            handler:function(){
				window.location="${pageContext.request.contextPath}/customer.do?method=business";
			}
      	},'-',new Ext.Toolbar.Fill()]
	});

} 

function setManager(opt){
	
	var oInputs = document.all.tags("INPUT");
	for ( i = 0; i < oInputs.length; i++ ) {
		if(oInputs[i].type == 'text' && oInputs[i].value == ""){
			alert("[" + oInputs[i].title + "]不能为空！");
			oInputs[i].focus();
			return false;
		}
	}
	
	var auditopinion = document.getElementById("auditopinion")
	if(auditopinion.value == ""){
		alert("[审核意见]不能为空！");
		auditopinion.focus();
		return false;
	}

	var state = document.getElementById("state");
	if(opt == 1){
		state.value = "待跟踪";
	}else if(opt == 2){
		state.value = "放弃";
	}
	
	thisForm.action = "${pageContext.request.contextPath}/customer.do?method=businessSave&opt=audit";
	thisForm.submit();
}


window.attachEvent('onload',ext_init);
</script>

<style>

input {
	border: 1px solid #AEC9D3;
}

legend {
	color: #006699;
}


</style> 

</head>
<body >
<div id="divBtn" ></div>

<form name="thisForm" method="post" action="" id="thisForm" style="background-color: #ecf2f2;border: 1px solid #AEC9D3;">

<div id="divTab"  style="width:100%; height:expression(document.body.clientHeight-27);overflow:auto">


<center style="color: #4A74BC;font-weight: bold;font-size: 14px;" >
商&nbsp;&nbsp;机&nbsp;&nbsp;信&nbsp;&nbsp;息<br/><br/> 

</center>


<fieldset>
	<legend>商机信息</legend>
	<input type="hidden" id="autoid" name="autoid"  value="${business.autoid }">
	<input type="hidden" id="state" name="state"  value="${business.state }">
	
 	<table width="98%" border="0" align="center" cellpadding="2" cellspacing="1" bgcolor="#6595d6">
	
  	<tr>
      <td align="right" width="20%" height="20" bgColor="#EEEEEE"><div align="right">客户编号<span class="mustSpan">[*]</span>：</div></td>
      <td align="left" bgColor="#ffffff">
      	${business.customerid }
      </td>
    </tr> 
  	<tr>
      <td align="right"width="20%" height="20" bgColor="#EEEEEE">客户名称<span class="mustSpan">[*]</span>：</td>
      <td align="left" bgColor="#ffffff">
      	${business.customername }
	  </td>
    </tr>
  	<tr>
      <td align="right"width="20%" height="20" bgColor="#EEEEEE">客户来源<span class="mustSpan">[*]</span>：</td>
      <td align="left" bgColor="#ffffff">
      	${business.source }
	  </td>
    </tr>

  	<tr>
      <td align="right" width="20%" height="20" bgColor="#EEEEEE">客户联系人<span class="mustSpan">[*]</span>：</td>
      <td align="left" bgColor="#ffffff">
      	${business.contact }
	  </td>
    </tr>
    <tr>
      <td align="right"width="20%" height="20" bgColor="#EEEEEE">客户联系方式<span class="mustSpan">[*]</span>：</td>
      <td align="left" bgColor="#ffffff">
      	${business.contactway }
	  </td>
    </tr>
     
  	<tr>
      <td align="right"width="20%" height="20" bgColor="#EEEEEE">标题<span class="mustSpan">[*]</span>：</td>
      <td align="left" bgColor="#ffffff">
      	${business.caption }
	  </td>
    </tr>

  	<tr>
      <td align="right"width="20%" height="20" bgColor="#EEEEEE">详细情况<span class="mustSpan">[*]</span>：</td>
      <td align="left" bgColor="#ffffff">
      	<textarea style="background-color: #eeeeee;" readonly="readonly" title="详细情况" class="required" rows="10" cols="100" id="memo" name="memo" >${business.memo }</textarea>
	  </td>
    </tr> 
    
    <tr>
      <td align="right"width="20%" height="20" bgColor="#EEEEEE">商机责任人<span class="mustSpan">[*]</span>：</td>
      <td align="left" bgColor="#ffffff">
      	<input title="商机责任人" class="required" type="text" id="owner" name="owner"  value="${business.owner }" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"   valuemustexist=true autoid=252 >
	  </td>
    </tr>
     <tr>
      <td align="right"width="20%" height="20" bgColor="#EEEEEE">商机截止日期<span class="mustSpan">[*]</span>：</td>
      <td align="left" bgColor="#ffffff">
      	${business.deadtime }
	  </td>
    </tr>
    <tr>
      <td align="right"width="20%" height="20" bgColor="#EEEEEE">录入人<span class="mustSpan">[*]</span>：</td>
      <td align="left" bgColor="#ffffff">
      	${business.iname }
	  </td>
    </tr>
    <tr>
      <td align="right"width="20%" height="20" bgColor="#EEEEEE">录入时间<span class="mustSpan">[*]</span>：</td>
      <td align="left" bgColor="#ffffff">
      	${business.idate }
	  </td>
    </tr>    
 </table>

</fieldset>

<fieldset>
	<legend>审核意见</legend>

	<input type="hidden" id="auser" name="auser"  value="${business.auser }">
	<input type="hidden" id="adate" name="adate"  value="${business.adate }">
		
 	<table width="98%" border="0" align="center" cellpadding="2" cellspacing="1" bgcolor="#6595d6">
 
  	<tr>
      <td align="right"width="20%" height="20" bgColor="#EEEEEE">审核意见<span class="mustSpan">[*]</span>：</td>
      <td align="left" bgColor="#ffffff">
      	<textarea  title="审核意见" class="required" rows="10" cols="100" id="auditopinion" name="auditopinion" >${business.auditopinion }</textarea>
	  </td>
    </tr> 
  
 </table>
	
</fieldset>
</div>


</form>



</body>
<script>
new Validation("thisForm");
 

function getCustomer(obj){
	if(obj.value != "000000"){
		var aJax = new ActiveXObject("Microsoft.XMLHTTP");
	   	aJax.open("POST","${pageContext.request.contextPath}/customer.do?method=customer&customerid="+obj.value+"&random="+ Math.random(),false);
	   	aJax.send();  
	   
	   	var result = aJax.responseText;
		
		var all = result.split("||");
		for(var i=0;i<all.length;i++){
			if(all[i] != null && all[i] != ""){
			 	var the = all[i].split(":");
			 	document.getElementById(the[0]).value = the[1];
			}
		}
	}
}
</script>
</html>