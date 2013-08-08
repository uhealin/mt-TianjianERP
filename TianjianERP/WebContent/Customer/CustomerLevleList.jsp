<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>客户评级历史记录</title>

<script type="text/javascript">  

function ext_init(){
	
	var tbar_customer = new Ext.Toolbar({
   		renderTo: "divBtn",
   		height:30,
   		defaults: {autoHeight: true,autoWidth:true},
           items:[{ 
            text:'打印',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/print.gif',
            handler:function(){
				print_clevellist();
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
	
} 



window.attachEvent('onload',ext_init);
</script>










</head>
<body >
<div id="divBtn" ></div>


<input type="hidden" name="toall" id="toall" value="${toall}">


<table width="100%" cellspacing="0" cellpadding="0">
	<tr>
		<td width="7%" height="20" align=center>提交人：${recorder }</td>
		<td width="7%" align=center><span id="VoucherType"></span>&nbsp;审批人:&nbsp;<span
			id="VoucherID"></span>&nbsp;${examiner }</td>
		<td width="7%" align=right>提交考核时间：${starttime }</td>
		<td width="8%" align=left>&nbsp;</td>
	</tr>
</table>
<mt:DataGridPrintByBean name="clevellist" isOldGrid="true" />
<br>

<form name="thisForm" method="post">
审批说明：<textarea rows="5"
	cols="50" id="memo" name="memo">${memo }</textarea> <br />
<br />
客户级别： <input type="text"
				 		id="customerlevel"
						name="customerlevel"
						autoid="2015"
						onfocus="onPopDivClick(this);"
						onkeydown="onKeyDownEvent();"
						onkeyup="onKeyUpEvent();"
						onclick="onPopDivClick(this);"
						norestorehint=true
						noinput=true
						hideresult=true
						value="${customerlevel }">
<br/>
<br/>
<div id="pass" style="display: none" align="center">						
<input type="button" class="flyBT" value="审批通过" id="passbutton" onclick="passExam();">
<input type="button" class="flyBT" value="返  回" id="backbutton" onclick="getBack();">
</div>
</form>

<br>


</body>
<script>
	function passExam(){
		if(document.getElementById("customerlevel").value==""){
			alert("请先选择客户的级别！");
			return;
		}else{
		    var thisform = document.thisForm;
			
			var toall = document.getElementById("toall").value;
			var customerid = "${customerid}";
			if(toall=="true"){
				thisform.action = "${pageContext.request.contextPath}/customer.do?method=passCustomerLevel&recordtime=${recode}";
			}else{		
				thisform.action = "${pageContext.request.contextPath}/customer.do?method=passCustomerLevel&customerid="+customerid+"&recordtime=${recode}";
			}
					
			thisform.submit();
		}			
	}
	
	try{
		var onlylook = "${onlylook}";
		if(onlylook=="true"){
			document.getElementById("pass").style.display = "none";
		}else{
			document.getElementById("pass").style.display = "";
		}
	}catch(e){
	
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
	
</script>

</html>