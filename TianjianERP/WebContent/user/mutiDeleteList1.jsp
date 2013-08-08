<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>批量删除客户</title>


<script type="text/javascript">

function ext_init(){
	
	var tbar_${DataGrid } = new Ext.Toolbar({
		renderTo: 'gridDiv_serMutiDelete1',
		items:[{
			text:'批量删除',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/delete.gif',
			handler:function () {
				goDelete2();
			}
		},'-',{
			text:'返回',
			cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/back.gif',
			handler:function () {
				goClose();
			}
		},'-',{
			text:'查询',
			id:'btn-query',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:queryWinFun
		},'->',{
			text:'刷新',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/refresh.gif',
			handler:function () {
				window.location.reload();
			}
			
		}${menuLocation}]
	});
	
} 

window.attachEvent('onload',ext_init);

</script>


</head>
<body leftmargin="0" topmargin="0">
<form name="thisForm" method="post" action="">
<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>
<div style="height:expression(document.body.clientHeight-30);" >
<mt:DataGridPrintByBean name="serMutiDelete1" message="请选择单位编号" />
</div>
</form>
<div id="search" style="display:none">
<br/>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
		<tr align="center">
			
			<td align="right">登录名：</td>
			<td align=left>
				<input type="text" name="loginid" id="loginid"> 
			</td>
		</tr>
		<tr align="center">
			
			<td align="right">姓名：</td>
			<td align=left>
				<input type="text" name="name" id="name" >
			</td>
		</tr>
		<tr align="center">
			
			<td align="right">所属部门：</td>
			<td align=left>
				<input name="department" id="department" type="text" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();"  onClick="onPopDivClick(this);" valuemustexist=true autoid=4402>
			</td>
		</tr>
	</table>
</div>
</body>
</html>
<script>
   //人员查询
var queryWin = null;
function queryWinFun(id){
	
		if(!queryWin) { 
			new BlockDiv().show();
			var searchDiv = document.getElementById("search") ;
		    queryWin = new Ext.Window({
				title: '人员查询',
		     	renderTo : searchWin,
		     	width: 300,
		     	height:195,
	        	closeAction:'hide',
	       	    listeners : {
		         	'hide':{
		         		fn: function () {
		         			new BlockDiv().hidden();
							queryWin.hide();
						}
					}
		        },
	        	layout:'fit',
		    	html:searchDiv.innerHTML,
		    	buttons:[{
	            	text:'确定',
	          		handler:goSearch1
	          		//handler:goView
	        	},{
	            	text:'取消',
	            	handler:function(){
		            	goClean_CH_user();
	               		queryWin.hide();
	            	}
	        	}]
		    });
	    }
	    queryWin.show();
}
   

function goSearch1() {
		document.thisForm.action="${pageContext.request.contextPath}/user.do?method=mutiDeleteList1&flag=1"
		document.thisForm.submit();
}
function goClean_CH_user() {
		window.location="${pageContext.request.contextPath}/user.do?method=List";
}

	function goClose() {
		if (top.location == self.location) {
			window.close();
		} else {
			window.history.back();
		}
}
	
function goDelete2() {

	document.getElementById("chooseValue_serMutiDelete1").value = getChooseValue("serMutiDelete1");
	
	if(document.thisForm.chooseValue_serMutiDelete1.value==""){
		alert("请选择要永远删除的禁用人员！");
		return; 
	}else{
		var aJax = new ActiveXObject("Microsoft.XMLHTTP");
		var url="user.do?method=CheckName&id="+thisForm.chooseValue_serMutiDelete1.value;

	    aJax.open("POST", url, false);
	    aJax.send();

	   
   		var str = "人员删除后无法登录系统，彻底删除后将不可还原，确定要删除吗？";
		
		if(confirm(str,"提示")){
		var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		var url="user.do?method=Remove4&id="+thisForm.chooseValue_serMutiDelete1.value+"&random="+Math.random();

		oBao.open("POST", url, false);
		oBao.send();
		var strResult = unescape(oBao.responseText);
		goSearch_serMutiDelete1();
		alert(strResult);
			
	  	}
	}

	goClean_CH_user();
}

function goClean_CH_user() {
	window.location="user.do?method=mutiDeleteList1";
}
</script>
