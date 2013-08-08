<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>收款与发票登记</title>
<style>

.before{
	border: 0px;
	background-color: #FFFFFF !important;
}

.data_tb {
	background-color: #ffffff;
	text-align:center;
	margin:0 0px;
	width:50%;
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
	    var tbar = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		defaults: {autoHeight: true,autoWidth:true},
            items:[{
	            text:'返回',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/back.gif',
	            handler:function(){
					//closeTab(parent.tab);
					window.history.back();
				}
       		},'->'
			]
        });
		new ExtButtonPanel({
			desc:'',
			renderTo:'sbtBtn',
			columns:1,
			items:[{
                text: '保存',
                id:'appSubmit25', 
                icon:'${pageContext.request.contextPath}/img/receive.png' ,
                scale: 'large',
	               handler:function(){
	            	  goSave();
	   			   }
	           }
            ]  
		});
		
		new Ext.form.DateField({
			applyTo : 'bargaindate',
			width: 200,
			format: 'Y-m-d'
		});
    }
    window.attachEvent('onload',ext_init);



</script>
</head>
<body>
<div id="divBtn"></div>
<form name="thisForm" method="post" action="" >
<div style="height:expression(document.body.clientHeight-30);overflow: auto;padding:20px;" >

<input name="autoid" type="hidden" id="autoid" value="${autoid }">
<input name="loginid" type="hidden" id="loginid" value="${pbt.loginid }">

<span class="formTitle" >收款与发票登记<br/><br/> </span>
<br>
<table id="tb2" cellpadding="8" cellspacing="0" align="center" class="data_tb" >
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">项目名称：</td>
  <td  class="data_tb_content" colspan="3"><input value="${pbt.projectid }"  type="text" id="projectid" name="projectid"  refer="loginid" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"  class="required"  autoid=139  size=50 /></td>
</tr>
<tr>
  <td class="data_tb_alignright" align="center" colspan="4">收款情况</td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">收款日期：</td>
  <td  class="data_tb_content" colspan="3"><input value="${pbt.bargaindate }"  type="text" id="bargaindate" name="bargaindate"  class="required"   size=50 /></td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">收款金额：</td>
  <td  class="data_tb_content" colspan="3"><input onfocus="this.select();" value="${pbt.bargainmoney }"   type="text" id="bargainmoney" name="bargainmoney"  class="required"   size=50 /></td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">财务是否确认：</td>
  <td  class="data_tb_content" colspan="3"><input type="radio" value="1" id="property" name="property" <c:if test="${pbt.property == '1' || pbt.property == ''}">checked</c:if> />是 <input type="radio" value="0" id="property" name="property" <c:if test="${pbt.property == '0'}">checked</c:if> />否</td>
</tr>
<tr>
  <td class="data_tb_alignright" align="center" colspan="4">发票情况</td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">发票编号：</td>
  <td  class="data_tb_content" colspan="3"><input onfocus="this.select();" value="${pbt.invoicenumber }"   type="text" id="invoicenumber" name="invoicenumber"  size=50 /></td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">发票金额：</td>
  <td  class="data_tb_content" colspan="3"><input onfocus="this.select();" value="${pbt.billMoney }"   type="text" id="billMoney" name="billMoney"  size=50 /></td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">发票客户接收人：</td>
  <td  class="data_tb_content" colspan="3"><input onfocus="this.select();" value="${pbt.recipient }"   type="text" id="recipient" name="recipient"  size=50 /></td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">登记人：</td>
  <td  class="data_tb_content" ><input name="loginName" id="loginName"  value="${pbt.loginName }"   type="text"  class="before" readonly   ></td>
  <td class="data_tb_alignright" width="15%" align="right">登记时间：</td>
  <td  class="data_tb_content" ><input name="logindate" id="logindate"  value="${pbt.logindate }"   type="text"  class="before" readonly   ></td>  
</tr>
<tr>
  <td class="data_tb_content" align="center" colspan="4">&nbsp;</td>
</tr>
</table>

<center><div id="sbtBtn" ></div></center>

</div>
</form>

</body>
</html>
<script type="text/javascript">
<!--

function goSave(){
	if (!formSubmitCheck('thisForm')) return;
	
	var bargainmoney = document.getElementById("bargainmoney");
	if(bargainmoney.value == 0){
		alert("收款金额不能为零，请重新输入");
		bargainmoney.select();
		return;
	}
	
	thisForm.action = "${pageContext.request.contextPath}/practicalbalance.do?method=nsave";
	thisForm.target = "";
	thisForm.submit();
	
}
	
//-->
</script>
