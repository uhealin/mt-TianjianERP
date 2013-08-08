<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>家庭成员记录</title>
<style>

.before{
	border: 0px;
	background-color: #ffffff !important;
}

.data_tb {
	background-color: #ffffff;
	text-align:center;
	margin:0 0px;
	width:80%;
	border:#8db2e3 1px solid; 
	border-collapse: collapse; 
}
.data_tb_alignright {	
	background: #e4f4fe; 
	white-space:nowrap;
	padding:5px;
	border-top: #8db2e3 1px solid;
	border-left: #8db2e3 1px solid;
	border-right: #8db2e3 1px solid;
	border-bottom: #8db2e3 1px solid; 
	height:30px;
	background-color: #d3e1f1;
	font-size: 13px;
	font-family:"宋体";
}
.data_tb_content {
	padding-left: 2px; 
	border-top: #8db2e3 1px solid; 
	border-left: #8db2e3 1px solid;
	border-right: #8db2e3 1px solid;
	border-bottom: #8db2e3 1px solid;  
	word-break: break-all; 
	text-align: left; 
	word-wrap: break-word
}

</style>
<script type="text/javascript">


Ext.onReady(function (){
	new Ext.Toolbar({
			renderTo: "divBtn",
			height:30,
			defaults: {autoHeight: true,autoWidth:true},
	       items:[{ 
	           id:'saveBtn',
	           text:'保存',
	           icon:'${pageContext.request.contextPath}/img/save.gif' ,
	           handler:function(){
	        	   if (!formSubmitCheck('thisForm')){
	        	   		return;
	        	   }else{
				   		setFamilyBargainType();
				   }
			   }
	     	 },'-',{ 
	        text:'返回',
	        icon:'${pageContext.request.contextPath}/img/back.gif', 
	        handler:function(){
				f_back();
			}
	  	},'->']
	});
	 
});
</script>

</head>
<body>
<div id="divBtn" ></div>

<input name="autoid" type="hidden" id="autoid" value="${autoid}" />
<span class="formTitle" >家庭成员记录<br/><br/> </span><br>
<jodd:form bean="family" scope="request">
	<form name="thisForm" action="" method="post" enctype="multipart/form-data">

	<table  cellpadding="8" cellspacing="0" align="center" class="data_tb" >
		<tr>
			<td class="data_tb_alignright"  width="20%" align="right">姓名<span class="mustSpan">[*]</span>：</td>
			<td class="data_tb_content"><input name="compellation" type="text" class='required' id="compellation" maxlength="50" title="请输入姓名">
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright"  width="20%" align="right">关系<span class="mustSpan">[*]</span>：</td>
			<td class="data_tb_content"><input name="footing" type="text" class='required' id="footing" maxlength="50" title="请正确输入关系">
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright"  width="20%" align="right">联系电话：</td>
			<td class="data_tb_content"><input name="phone" type="text" class="phonenumber-wheninputed" id="phone" maxlength="50" title="请输入正确的联系电话"></td>
		</tr>
		<tr>
			<td class="data_tb_alignright"  width="20%" align="right">工作单位：</td>
			<td class="data_tb_content"><input name="workunit" type="text" id="workunit" maxlength="50" size="50"></td>
		</tr>
		<tr>
			<td class="data_tb_alignright"  width="20%" align="right">政治面貌：</td>
			<td class="data_tb_content"><input name="government" type="text" id="government" maxlength="50"></td>
		</tr>
		<tr>
			<td class="data_tb_alignright"  width="20%" align="right">附件：</td>
			<td class="data_tb_content">
			<input name="fileRondomNames" type="hidden" id="fileRondomNames" >
		  	<script>
				attachInit('oa/familyFoder','${family.fileRondomNames}');					
			</script>
			</td>
		</tr>
		

	</table>

	<!-- 
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td height="22" colspan="3">&nbsp;</td>
		</tr>
		<tr>
			<td width="37%" align="right"><input type="submit" class="flyBT"
				value="确  定" onclick="return setFamilyBargainType();" /></td>
			<td width="8%">&nbsp;</td>
			<td width="55%"><input type="button" name="back" value="返  回"
				class="flyBT" onClick="history.back()"></td>
		</tr>
	</table>
 	-->
	</form>
</jodd:form>

<form action="" method="POST" id="myForm" name="myForm">
	
	<input type="hidden" id="fileName_CH" name="fileName_CH">
	
</form>

</body>
<script>
new Validation("thisForm");
 
function setFamilyBargainType(){
	var autoidvalue = document.getElementById("autoid").value;
    if(autoidvalue !=""){
       document.thisForm.action="${pageContext.request.contextPath}/family.do?method=updateFamily&autoid=" + autoidvalue;
    }else{
		document.thisForm.action="${pageContext.request.contextPath}/family.do?method=addFamily";
	}
	document.thisForm.submit();
}


function deleteLine()
{	
	var t=false;
	
	
	for (var i=uploadTbody.children.length-1; i>=0 ; i-- ) {
	var tbodyObj = uploadTbody.children[i].firstChild.firstChild ;
	if (tbodyObj.checked){
		
		if(tbodyObj.isExist) {
			
			if(confirm("您的操作可能会导致文件丢失,您确定删除吗?"))  {
			
				var oBao = new ActiveXObject("Microsoft.XMLHTTP");
				var url="family.do?method=deleteUpload&autoId=${autoid}&fileName="+tbodyObj.fileName;
				oBao.open("POST", url, false);		
				oBao.send();
				if(oBao.responseText == "notFound"){
					alert("找不到文件，请联系管理员!");
					return false;
				}else if(oBao.responseText == "fail") {
					alert("附件删除失败!");
					return false;
				}else if(oBao.responseText == "suc"){
					alert("附件删除成功!");
					uploadTbody.deleteRow(i);
				}
				
				}
				t=true;
			}
		else {
			uploadTbody.deleteRow(i);
			t=true;
			}
		}
	}
	if(!t)
	{
		alert("请选定其中一列！！");
	}
	
}

function addLine()
{

	var objTr=attachstable.insertRow(-1);

	var objTd=objTr.insertCell(-1);
	objTd.innerHTML="<input type=checkbox id=checkLine name=\"checkbox\">";

	objTd=objTr.insertCell(-1);
	objTd.innerHTML="<input type=\"file\" name=\"uploadFile\" style=\"width:100%\">";
	
	
}

function goDownload(fileName) {

	document.getElementById("fileName_CH").value = fileName ;	
	myForm.action = "/AuditSystem/family.do?method=download&autoid=${autoid}";

	myForm.submit(); 
	
}


// 返回
function f_back(){
	history.back()
}

</script>
</html>
