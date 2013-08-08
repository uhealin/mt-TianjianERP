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
           items:[{ 
            text:'返回',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/back.gif',
            handler:function(){
				//window.location="${pageContext.request.contextPath}/customer.do?method=business&frameTree=${param.frameTree}";
            	window.history.back();
			}
      	},'-',new Ext.Toolbar.Fill()]
	});
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
      <td class="data_tb_alignright"  width="20%" align="right">客户编号：</td>
      <td  class="data_tb_content" >
		 ${business.customerid } 
      </td>
    </tr> 
  	<tr>
      <td class="data_tb_alignright"  width="20%" align="right">客户名称：</td>
      <td  class="data_tb_content" >
      	 ${business.customername }
	  </td>
    </tr>
  	<tr>
      <td class="data_tb_alignright"  width="20%" align="right">客户来源：</td>
      <td  class="data_tb_content" >
      	 ${business.source } 
	  </td>
    </tr>

  	<tr>
      <td class="data_tb_alignright"  width="20%" align="right">客户联系人：</td>
      <td  class="data_tb_content" >
      	 ${business.contact }
	  </td>
    </tr>
    <tr>
      <td class="data_tb_alignright"  width="20%" align="right">客户联系电话：</td>
      <td  class="data_tb_content" >
      	 ${business.contactway }
	  </td>
    </tr>
     
  	<tr>
      <td class="data_tb_alignright"  width="20%" align="right">标题：</td>
      <td  class="data_tb_content" >
      	 ${business.caption }
	  </td>
    </tr>

  	<tr>
      <td class="data_tb_alignright"  width="20%" align="right">详细情况：</td>
      <td  class="data_tb_content" >
      	 ${business.memo }
	  </td>
    </tr> 
    
    <tr>
      <td class="data_tb_alignright"  width="20%" align="right">商机责任人：</td>
      <td  class="data_tb_content" >
      	<input  <c:if test="${business.state != '待审核'}">style="background-color: #eeeeee;" readonly="readonly"</c:if> title="商机责任人"  type="text" id="owner" name="owner"  value="${business.owner }" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"   multilevel=true  valuemustexist=true autoid=867 >
	  </td>
    </tr>
     <tr>
      <td class="data_tb_alignright"  width="20%" align="right">商机截止日期：</td>
      <td  class="data_tb_content" >
      	  ${business.deadtime }
	  </td>
    </tr>
 </table>



</div>

</form>
</body>
<script>
new Validation("thisForm");

setObjDisabled("owner");
 
</script>
</html>