<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>人员考核历史记录</title>

<script type="text/javascript">


Ext.onReady(function (){
	new Ext.Toolbar({
			renderTo: "divBtn",
			height:30,
			defaults: {autoHeight: true,autoWidth:true},
	       items:[{ 
	           id:'saveBtn',
	           text:'打印',
	           icon:'${pageContext.request.contextPath}/img/print.gif' ,
	           handler:function(){
	        	  print_clevellist();
			   }
	     	 },'-',{ 
	        text:'返回',
	        icon:'${pageContext.request.contextPath}/img/back.gif', 
	        handler:function(){
				getBack();
			}
	  	},'->']
	});
	 
});
</script>

</head>
<body >
<div id="divBtn"></div>

<input type="hidden" name="toall" id="toall" value="${toall}">

<div id="look"><input type="button" class="flyBT" value="打  印"
	onclick="print_clevellist();"> <input type="button"
	class="flyBT" value="返  回" onclick="getBack();"></div>

<br />
<table width="100%" cellspacing="0" cellpadding="0">
	<tr>
		<td width="7%" height="20" align=center>提交人：${recorder }</td>
		<td width="7%" align=center><span id="VoucherType"></span>&nbsp;审批人:&nbsp;<span
			id="VoucherID"></span>&nbsp;${examiner }</td>
		<td width="7%" align=right>提交考核时间：${starttime }</td>
		<td width="8%" align=left>&nbsp;</td>
	</tr>
</table>

<mt:DataGridPrintByBean name="clevellist" />
<br>

<form name="thisForm" method="post">审批说明：<textarea rows="5"
	cols="50" id="memo" name="memo">${memo }</textarea> <br />
<br />
人员级别： <input type="text" id="userlevel" name="userlevel" autoid="2026"
	onfocus="onPopDivClick(this);" onkeydown="onKeyDownEvent();"
	onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"
	norestorehint=true noinput=true hideresult=true value="${userlevel }">
<div id="pass" style="display: none" align="center">
<input type="button" class="flyBT" value="审批通过" onclick="passExam();">
<input type="button" class="flyBT" value="返  回" onclick="getBack();">
</div>
</form>

</body>
<script>
	function passExam(){
		if(document.getElementById("userlevel").value==""){
			alert("请先选择人员的级别！");
			return;
		}else{
		    var thisform = document.thisForm;
			
			var toall = document.getElementById("toall").value;
			var userid = "${userid}";
			if(toall=="true"){
				thisform.action = "${pageContext.request.contextPath}/userLevel.do?method=passUserLevel&all=all&recordtime=${recode}";
			}else{		
				thisform.action = "${pageContext.request.contextPath}/userLevel.do?method=passUserLevel&userid="+userid+"&recordtime=${recode}";
			}
					
			thisform.submit();
		}			
	}
	
	try{
		var onlylook = "${onlylook}";
		if(onlylook=="true"){
			document.getElementById("look").style.display = "";
			document.getElementById("pass").style.display = "none";
		}else{
			document.getElementById("look").style.display = "none";
			document.getElementById("pass").style.display = "";
		}
	}catch(e){
	
	}
	
	function getBack(){
		var toall = document.getElementById("toall").value;
		var userid = "${userid}";
		var all = "${all}";
	
		if(toall=="true"){
			window.location="${pageContext.request.contextPath}/userLevel.do?method=levelHistory&all="+all;
		}else{		
			window.location="${pageContext.request.contextPath}/userLevel.do?method=levelHistory&userid="+userid+"&all="+all;
		}
	}
	
</script>

</html>
