<%@page import="java.util.UUID"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>职级管理</title>
<style type="text/css">

.before{
	border: 0px;
	background-color: #FFFFFF !important;
}

.data_tb {
	background-color: #ffffff;
	text-align:center;
	margin:0 0px;
	width:60%;
	border:#8db2e3 1px solid; 
	BORDER-COLLAPSE: collapse; 
	margin-top: 20px;
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
	text-align:center;
	width:20%;
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
	WORD-WRAP: break-word
	text-align:center;
}

</style>
<script type="text/javascript">

function ext_init(){
	new Ext.Toolbar({
		renderTo: "divBtn",
		height:30,
		defaults: {autoHeight: true,autoWidth:true},
       items:[{ 
           id:'saveBtn',
           text:'确认',
           icon:'${pageContext.request.contextPath}/img/save.gif' ,
           handler:function(){
		   		mySubmit();
		   }
     	 },'-',{ 
        text:'返回',
        icon:'${pageContext.request.contextPath}/img/back.gif', 
        handler:function(){
			window.history.back();
		}
  	},'->']
});
	
	new ExtButtonPanel({
		desc:'',
		renderTo:'sbtBtn',
		items:[
		{
            text: '确认',
            icon:'${pageContext.request.contextPath}/img/receive.png' ,
            scale: 'large',
            handler:function(){
            	mySubmit();
   			}
           },{
            text: '返回',
            icon:'${pageContext.request.contextPath}/img/back_32.png' ,
            scale: 'large',
               handler:function(){
            	  //closeTab(parent.tab);
					window.history.back();
   			   }
           }
        ]  
	});    
}
window.attachEvent('onload',ext_init);
</script>
</head>
<body style="overflow: auto;">
<div id="divBtn"></div>
	<form name="thisForm" action="${pageContext.request.contextPath}/salary.do?method=audit" class="autoHeightForm" method="post" >
		<input name="pch" type="hidden" id="pch" value="${pch}" />
		 <div id="newTableDiv" style="height: 70%;overflow: auto;text-align: center;">
		 	${newTable}
		  </div>
		<input type="hidden" name="ctype" id="ctype" value="${ctype }">
		<div id="personMag" style="color:gray;vertical-align: top;margin-left: 540px;" align="left">
		</div>
		<div id="sbtBtn" align="center"> </div>
	</form>
</body>


<script>
 if("${ctype}".indexOf("人事")>-1){
	 document.getElementById("personMag").innerText="您本月全部出勤，感谢您的辛勤工作!";
 }
 function ajaxEditSaray(pch,userId,n1,v1,sumSalary){
		
		var vValue = document.getElementById(v1).value;
		if(pch !="" && userId !="" && v1 !="" && vValue !=""){
			 Ext.Ajax.request({
					method:'POST',
					params : { 
						rand : Math.random(),
						pch:pch,
						userId:userId,
						nName:n1,
						vValue:vValue
					},
					url:"${pageContext.request.contextPath}/salary.do?method=ajaxEditSaray",
					success:function (response,options) {
						var request = response.responseText;
						if(request !=""){
							
							document.getElementById(sumSalary).innerText=request;
							
						}
						 
					},
					failure:function (response,options) {
						alert("后台出现异常,修改信息失败!");
					}
				});
			
		}else{
			return ;
		}
		
		
	}
 
function mySubmit(){
	var pch = document.getElementById("pch").value;
	var ctype = document.getElementById("ctype").value;
	if(pch !="" && ctype !=""){
		document.thisForm.action="${pageContext.request.contextPath}/salary.do?method=audit";
		document.thisForm.submit();
	}
}
</script>
</html>
