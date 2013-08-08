<%@page import="java.util.UUID"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>印章申请</title>
<script type="text/javascript">
	Ext.onReady(function() {
		
		new Ext.Toolbar({
			renderTo : "divBtn",
			height : 30,
			defaults : {
				autoHeight : true,
				autoWidth : true
			},
			items : [ {
				id : 'saveBtn',
				text : '保存',
				icon : '${pageContext.request.contextPath}/img/save.gif',
				handler : function() {
					mySubmit();
				}
			}, '-', {
				text : '返回',
				icon : '${pageContext.request.contextPath}/img/back.gif',
				handler : function() {
					window.history.back();
				}
			}, '->' ]
		});
		
		new ExtButtonPanel({
			desc:'',
			renderTo:'sbtBtn',
			items:[
			{
	            text: '保存',
	            id:'appSubmit23', 
	            icon:'${pageContext.request.contextPath}/img/receive.png' ,
	            scale: 'large',
	            handler:function(){
	            	mySubmit();
	   			}
	           },{
	            text: '返回',
	            id:'appSubmit25', 
	            icon:'${pageContext.request.contextPath}/img/back_32.png' ,
	            scale: 'large',
	               handler:function(){
	            	  //closeTab(parent.tab);
						window.history.back();
	   			   }
	           }
	        ]  
		});    

	});
</script>

<style type="text/css">

.before{
	border: 0px;
	background-color: #FFFFFF !important;
}

.data_tb {
	background-color: #ffffff;
	text-align:center;
	margin:0 0px;
	width:65%;
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
</head>
<body leftmargin="0" topmargin="0">
<div id="divBtn"></div>
<form name="thisForm" method="post" action="" id="thisForm" >
<input type="hidden" id="uuid" name="uuid" value="${seal.uuid }" />

<span class="formTitle" ><br>印章申请信息</span>
<table   border="0" style="line-height: 30px;"
	class="data_tb" align="center" >
	<tr>
		<td colspan="2" class="data_tb_alignright">
		印章申请信息<br />
		</td>
	</tr>
	<tr>
		<td class="data_tb_alignright" align="right" width="20%"><font color="red" size=3>*</font>申请事项：</td>
		<td class="data_tb_content" style="paddingpadding-left: 50px;">
			<input  type="text" name="matter" id="matter" class="required" onfocus="onPopDivClick(this);"
				onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();"
				onclick="onPopDivClick(this);"
				autoid=4580
				value="${seal.matter }" />
			 
		</td>
	</tr>
	<tr>
		<td class="data_tb_alignright" align="right" width="20%">
				<div><font color="red" size=3>*</font>公章类型：</div>
		</td>
		 <td class="data_tb_content" style="paddingpadding-left: 50px;">
			<input  type="text" name="ctype" id="ctype" class="required" onfocus="onPopDivClick(this);"
				onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();"
				onclick="onPopDivClick(this);"
				autoid=4581
				value="${seal.ctype }" />
			 
		</td>
	</tr>
	<tr>
		<td class="data_tb_alignright" align="right" width="20%">
				<div>备注：</div>
		</td>
		 <td class="data_tb_content" style="paddingpadding-left: 50px;">
			<textarea name="remark" id="remark"  style="overflow: visible;height: 130px;width: 400px;">${seal.remark}</textarea>
			 
		</td>
	</tr>
	<tr>
			<td class="data_tb_alignright" align="right"  style="vertical-align: top;margin-top: 30px;">附件：</td>
			<td class="data_tb_content">
				<input type="hidden" id="fileName" name="fileName" value="${seal.fileName}">
				<div style="vertical-align: middle;width: 100%;margin-top: 10px;">
				<script type="text/javascript">
				if("${seal.fileName}"==""){
					<%
							String fileName=UUID.randomUUID().toString();  //生成uuid
					%>
					document.getElementById("fileName").value="<%=fileName%>";
					attachInit('seal','<%=fileName%>');					
				}else{
					attachInit('seal','${seal.fileName}');
				}
				</script>
				</div>
			</td>
		</tr>

</table>
	<center><div id="sbtBtn" ></div></center>
 </form>

<script type="text/javascript">
	new Validation('thisForm');
	
	function mySubmit() {
		
		var filename= document.getElementById("fileName").value; //附件编号
		var ctype= document.getElementById("ctype").value;   //公章类型
		
		//getAttachCount(); 验证附件是否上传函数，返回数值，如果返回1就代表有一个附件，以此类推。0就是没有上传附件
		
		  if (!formSubmitCheck('thisForm')) return;
		   
		  var url = "${pageContext.request.contextPath}/seal.do?method=getAccessory";
		  var requestString = "&table=seal&uuid="+filename;
		  var result ="";
		  
		if (document.getElementById("uuid").value != "") {
			
			if(ctype.indexOf("电子")>-1){
				  result = ajaxLoadPageSynch(url,requestString);
				if(result == ""){
					alert("请上传附件！");
					return ;
				}
				
				var resultTwo = result.split(",");
				alert(resultTwo);
				if(resultTwo[1] !=""){
					alert("一次只能申请一次电子章!");
					return ;
				}
				
			} 
			document.thisForm.action = "${pageContext.request.contextPath}/seal.do?method=update";
		} else {
			
			if(ctype.indexOf("电子")>-1){
				  result = ajaxLoadPageSynch(url,requestString);
				if(result == ""){
					alert("请上传附件！");
					return ;
				}
				
				var resultTwo = result.split(",");
				alert(resultTwo);
				if(resultTwo[1] !=""){
					alert("一次只能申请一次电子章!");
					return ;
				}
				
			} 
			
			document.thisForm.action = "${pageContext.request.contextPath}/seal.do?method=add";
		}
		document.thisForm.submit();
	}
 
</script>

</body>
</html>
