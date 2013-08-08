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
<style>

.before{
	border: 0px;
	background-color: #FFFFFF !important;
}

.data_tb {
	background-color: #ffffff;
	text-align:center;
	margin:0 0px;
	width:80%;
	border:#8db2e3 1px solid; 
	BORDER-COLLAPSE: collapse; 
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

function ext_init(){
	

	    
	var tbar_customer = new Ext.Toolbar({
   		renderTo: "divBtn",
   		height:30,
   		defaults: {autoHeight: true,autoWidth:true},
           items:[
<c:if test="${business.state == '待审核'}">           
           { 
            text:'保存',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/save.gif',
            handler:function(){
				setManager();
			}
      	},'-',
</c:if>      	
      	{ 
            text:'返回',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/back.gif',
            handler:function(){
				window.location="${pageContext.request.contextPath}/customer.do?method=business&frameTree=${param.frameTree}";
            	//window.history.back();
			}
      	},'-',new Ext.Toolbar.Fill()]
	});

	    var date;
	    
	    if(!date) {
		    date = new Ext.form.DateField({
				applyTo : 'deadtime',
				width: 135,
				minValue:new Date(),
				format: 'Y-m-d',
				//editable : false,//不可修改
<c:if test="${business.state != '待审核'}">   				
				disabled : true,
</c:if>				
				emptyText: '' 
				
			});
	    }	
} 

function setManager(){
	
	if (!formSubmitCheck('thisForm')) return;

	thisForm.action = "${pageContext.request.contextPath}/customer.do?method=businessSave&frameTree=${param.frameTree}";
	showWaiting();//等待提示
	thisForm.submit();
}


window.attachEvent('onload',ext_init);
</script>

</head>
<body >
<div id="divBtn" ></div>

<form name="thisForm" method="post" action="" id="thisForm" >

<div id="divTab"  style="width:100%; height:expression(document.body.clientHeight-27);overflow:auto">

<span class="formTitle" >商机信息<br/><br/> </span>

	<input type="hidden" id="autoid" name="autoid"  value="${business.autoid }">
	<input type="hidden" id="state" name="state"  value="${business.state }">
	
	<input type="hidden" id="iuser" name="iuser"  value="${business.iuser }">
	<input type="hidden" id="idate" name="idate"  value="${business.idate }">

 	<table  cellpadding="8" cellspacing="0" align="center" class="data_tb" >
	
  	<tr style="display: none;">
      <td class="data_tb_alignright"  width="20%" align="right">客户编号<span class="mustSpan">[*]</span>：</td>
      <td  class="data_tb_content" >
		<input  <c:if test="${business.state != '待审核'}">style="background-color: #eeeeee;" readonly="readonly"</c:if> title='客户编号' value="${business.customerid }"    type="hidden" name="customerid" id="customerid"   />
      </td>
    </tr> 
  	<tr>
      <td class="data_tb_alignright"  width="20%" align="right">客户名称<span class="mustSpan">[*]</span>：</td>
      <td  class="data_tb_content" >
      	<input onchange="getCustomer(this);" <c:if test="${business.state != '待审核'}">style="background-color: #eeeeee;" readonly="readonly"</c:if> title='客户名称' class="required" type="text" id="customername" name="customername" value="${business.customername }" size="50">
	  </td>
    </tr>
  	<tr>
      <td class="data_tb_alignright"  width="20%" align="right">客户来源<span class="mustSpan">[*]</span>：</td>
      <td  class="data_tb_content" >
      	<input onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" showhint=false onClick="onPopDivClick(this);" valuemustexist=true autoid=860 refer='客户来源' noinput=true <c:if test="${business.state != '待审核'}">style="background-color: #eeeeee;" readonly="readonly"</c:if> title='客户来源' class="required" type="text" id="source" name="source"  value="${business.source }">
	  </td>
    </tr>

  	<tr>
      <td class="data_tb_alignright"  width="20%" align="right">客户联系人<span class="mustSpan">[*]</span>：</td>
      <td  class="data_tb_content" >
      	<input  <c:if test="${business.state != '待审核'}">style="background-color: #eeeeee;" readonly="readonly"</c:if> title='客户联系人' class="required" type="text" id="contact" name="contact"  value="${business.contact }">
	  </td>
    </tr>
    <tr>
      <td class="data_tb_alignright"  width="20%" align="right">客户联系电话<span class="mustSpan">[*]</span>：</td>
      <td  class="data_tb_content" >
      	<input  <c:if test="${business.state != '待审核'}">style="background-color: #eeeeee;" readonly="readonly"</c:if> title='客户联系方式' class="required validate-phonenumber" type="text" id="contactway" name="contactway"  value="${business.contactway }">
	  </td>
    </tr>
     
  	<tr>
      <td class="data_tb_alignright"  width="20%" align="right">标题<span class="mustSpan">[*]</span>：</td>
      <td  class="data_tb_content" >
      	<input  <c:if test="${business.state != '待审核'}">style="background-color: #eeeeee;" readonly="readonly"</c:if> title='标题'  class="required" type="text" id="caption" name="caption" size="80" value="${business.caption }">
	  </td>
    </tr>

  	<tr>
      <td class="data_tb_alignright"  width="20%" align="right">详细情况<span class="mustSpan">[*]</span>：</td>
      <td  class="data_tb_content" >
      	<textarea  <c:if test="${business.state != '待审核'}">style="background-color: #eeeeee;" readonly="readonly"</c:if> title="详细情况" class="required" rows="10" cols="80" id="memo" name="memo" >${business.memo }</textarea>
	  </td>
    </tr> 
    
    <tr>
      <td class="data_tb_alignright"  width="20%" align="right">商机责任人：</td>
      <td  class="data_tb_content" >
      	<input  <c:if test="${business.state != '待审核'}">style="background-color: #eeeeee;" readonly="readonly"</c:if> title="商机责任人"  type="text" id="owner" name="owner"  value="${business.owner }" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"   multilevel=true  valuemustexist=true autoid=867 >
	  </td>
    </tr>
     <tr>
      <td class="data_tb_alignright"  width="20%" align="right">商机截止日期<span class="mustSpan">[*]</span>：</td>
      <td  class="data_tb_content" >
      	<input   <c:if test="${business.state != '待审核'}">style="background-color: #eeeeee;" readonly="readonly"</c:if> title="商机截止日期" class="required" type="text" id="deadtime" name="deadtime"  value="${business.deadtime }">
	  </td>
    </tr>
<c:if test="${business.state != '待审核'}">   
    <tr>
      <td class="data_tb_alignright"  width="20%" align="right">录入人<span class="mustSpan">[*]</span>：</td>
      <td  class="data_tb_content" >
      	${business.iname }
	  </td>
    </tr>
    <tr>
      <td class="data_tb_alignright"  width="20%" align="right">录入时间<span class="mustSpan">[*]</span>：</td>
      <td  class="data_tb_content" >
      	${business.idate }
	  </td>
    </tr>  
</c:if>            
 </table>


<c:if test="${business.state != '待审核'}">  
<fieldset>
	<legend>审核意见</legend>

		
 	<table width="98%" border="0" align="center" cellpadding="2" cellspacing="1" bgcolor="#6595d6">

    <tr>
      <td align="right"width="20%" height="20" bgColor="#EEEEEE">审核人：</td>
      <td align="left" bgColor="#ffffff">
      	${business.aname }
	  </td>
    </tr>
    <tr>
      <td align="right"width="20%" height="20" bgColor="#EEEEEE">审核时间：</td>
      <td align="left" bgColor="#ffffff">
      	${business.adate }
	  </td>
    </tr>  
  	<tr>
      <td align="right"width="20%" height="20" bgColor="#EEEEEE">审核意见：</td>
      <td align="left" bgColor="#ffffff">
      	<textarea  <c:if test="${business.state != '待审核'}">style="background-color: #eeeeee;" readonly="readonly"</c:if>  title="审核意见" class="required" rows="10" cols="100" id="auditopinion" name="auditopinion" >${business.auditopinion }</textarea>
	  </td>
    </tr> 
  
 </table>
	
</fieldset>

<c:if test="${business.state != '待跟踪'}">

<fieldset>
	<legend>商机结论</legend>

		
 	<table width="98%" border="0" align="center" cellpadding="2" cellspacing="1" bgcolor="#6595d6">
     <tr>
      <td align="right"width="20%" height="20" bgColor="#EEEEEE">跟踪人：</td>
      <td align="left" bgColor="#ffffff">
      	${business.tname }
	  </td>
    </tr>
    <tr>
      <td align="right"width="20%" height="20" bgColor="#EEEEEE">跟踪时间：</td>
      <td align="left" bgColor="#ffffff">
      	${business.tdate }
	  </td>
    </tr> 
  	<tr>
      <td align="right"width="20%" height="20" bgColor="#EEEEEE">商机结论<span class="mustSpan">[*]</span>：</td>
      <td align="left" bgColor="#ffffff">
      	<textarea <c:if test="${business.state != '待跟踪'}">style="background-color: #eeeeee;" readonly="readonly"</c:if> title="商机结论" class="required" rows="10" cols="100" id="result" name="result" >${business.result }</textarea>
	  </td>
    </tr> 
  
 </table>
	
</fieldset>

</c:if>
</c:if>


</div>


</form>



</body>
<script>
new Validation("thisForm");
 

function getCustomer(obj){
	//alert(obj.value);
	
	if(obj.value != ""){
		var aJax = new ActiveXObject("Microsoft.XMLHTTP");
	   	aJax.open("POST","${pageContext.request.contextPath}/customer.do?method=customer&customerid="+obj.value+"&random="+ Math.random(),false);
	   	aJax.send();  
	   
	   	var result = aJax.responseText;
	   	//alert(result);
		var all = result.split("||");
		for(var i=0;i<all.length;i++){
			if(all[i] != null && all[i] != ""){
			 	var the = all[i].split(":");
			 	try{
			 		if(the[1] != ""){
			 			document.getElementById(the[0]).value = the[1];	
			 		}
			 	}catch(e){}
			}
		}
	}
}
</script>
</html>