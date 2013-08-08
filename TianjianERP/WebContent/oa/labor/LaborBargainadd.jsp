<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>

<title>增加劳动合同</title>
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
				   		setLaborBargainType();
				   }
			   }
	     	 },'-',{ 
	        text:'返回',
	        icon:'${pageContext.request.contextPath}/img/back.gif', 
	        handler:function(){
				window.history.back();
			}
	  	},'->']
	});
	 
	new Ext.form.DateField({
		applyTo : 'endorsedate',
		width: 133,
		format: 'Y-m-d'
	});
	
	new Ext.form.DateField({
		applyTo : 'trialtime',
		width: 133,
		format: 'Y-m-d'
	});
	
	new Ext.form.DateField({
		applyTo : 'ineffecttime',
		width: 133,
		format: 'Y-m-d'
	});
	
});
</script>
</head>
<body leftmargin="0" topmargin="0">
<div id="divBtn" ></div>


<input name="autoid" type="hidden" id="autoid" value="${autoid}" />
<input name="editall" type="hidden" id="editall" value="${all}" />
<input name="addall" type="hidden" id="addall" value="${param.all}" />
<span class="formTitle" >劳动合同<br/><br/> </span><br>
<jodd:form bean="lb" scope="request">
	<form name="thisForm" action="" method="post" enctype="multipart/form-data">

	<table  cellpadding="8" cellspacing="0" align="center" class="data_tb" >

		<%
				if (request.getParameter("autoid") == null ||"".equals(request.getParameter("autoid"))) {
		%>
		<tr>
			<td class="data_tb_alignright"  width="20%" align="right">合同编号<span class="mustSpan">[*]</span>:</td>
			<td class="data_tb_content"><input size="50" name="bargainID" type="text" class='required validate-number' id="bargainID" maxlength="40" title="请输入有效的合同编号"></td>
		</tr>
		<%
		} else {
		%>
		<tr>
			<td class="data_tb_alignright"  width="20%" align="right">合同编号<span class="mustSpan">[*]</span>:</td>
			<td class="data_tb_content"><input size="50" name="bargainID" type="text" readonly="readonly" class='required' id="bargainID" maxlength="40"></td>
		</tr>
		<%
		}
		%>
		<c:if test="${all=='all' || param.all=='all' }">
			<tr>
				<td class="data_tb_alignright"  width="20%" align="right">签署人<span class="mustSpan">[*]</span>:</td>
				<td class="data_tb_content"><input name="userid" type="text" class='required' id="userid" maxlength="40" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist="true" autoid="7"></td>
			</tr>
		</c:if>
		<tr>
			<td class="data_tb_alignright"  width="20%" align="right">合同类型<span class="mustSpan">[*]</span>:</td>
			<td class="data_tb_content">
			<select id="bargaintype" name="bargaintype" style="width: 130px">
			<option value="固定期限" >固定期限</option>
			<option value="无固定期限" >无固定期限</option>
			</select>
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright"  width="20%" align="right">基本工资<span class="mustSpan">[*]</span>:</td>
			<td class="data_tb_content"><input name="emolument" type="text" id="emolument" maxlength="40" class="required validate-number" title="请输入有效的金额">
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright"  width="20%" align="right">签订日期<span class="mustSpan">[*]</span>:</td>
			<td class="data_tb_content"><input name="endorsedate" type="text" id="endorsedate" maxlength="20" class="required validate-date-cn" title="有效时间" ></td>
		</tr>
		<tr>
			<td class="data_tb_alignright"  width="20%" align="right">试用到期日<span class="mustSpan">[*]</span>:</td>
			<td class="data_tb_content"><input name="trialtime" type="text" id="trialtime" maxlength="20" class="required validate-date-cn" title="有效时间" ></td>
		</tr>
		<tr>
			<td class="data_tb_alignright"  width="20%" align="right">合同到期日<span class="mustSpan">[*]</span>:</td>
			<td class="data_tb_content"><input name="ineffecttime" type="text" id="ineffecttime" maxlength="20" class="required validate-date-cn" title="有效时间" ></td>
		</tr>
		<tr>
			<td class="data_tb_alignright"  width="20%" align="right"><font color="red" size=3></font>其他:</td>
			<td class="data_tb_content"><textarea cols="50" rows="7" name="other" id="other"></textarea>
			</td>
		</tr>
		<tr>
			<td class="data_tb_alignright"  width="20%" align="right">附件：</td>
			<td class="data_tb_content">
			<input name="fileRondomNames" type="hidden" id="fileRondomNames" >
		  	<script>
				attachInit('oa/laoborBargainFoder','${lb.fileRondomNames}');					
			</script>
			</td>
		</tr>		
		
		<!-- 
      <tr>
    <td width="25%"><div align="right"><font color="red" size=3></font>登记人：</div></td>
    <td width="75%" ><input name="checkinperson" type="text" id="checkinperson"  maxlength="40">   
    </td>
  </tr>
      <tr>
    <td width="25%"><div align="right"><font color="red" size=3></font>登记日期：</div></td>
    <td width="75%" ><input name="checkindate" type="text" id="checkindate" maxlength="20"  class="validate-date-cn"  title="登记日期" showcalendar="true">
   
    </td>
  </tr>
  -->

	</table><!--


	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td height="22" colspan="3">&nbsp;</td>
		</tr>
		<tr>
			<td width="37%" align="right"><input type="submit" class="flyBT"
				value="确  定" onclick="return setLaborBargainType();" /></td>
			<td width="8%">&nbsp;</td>
			<td width="55%"><input type="button" name="back" value="返  回"
				class="flyBT" onClick="history.back()"></td>
		</tr>
	</table>

	--></form>
</jodd:form>


<form action="" method="POST" id="myForm" name="myForm">
	
	<input type="hidden" id="fileName_CH" name="fileName_CH">
	
</form>


</body>
<script>
new Validation("thisForm");

function setLaborBargainType(){

	
	var autoidvalue = document.getElementById("autoid").value;
	
    if(autoidvalue !=""){
    
			
       document.thisForm.action="${pageContext.request.contextPath}/laborbargain.do?method=updatelabor&autoid=" + autoidvalue+"&all="+document.getElementById("editall").value;
        
    	
    }else{

    	
		document.thisForm.action="${pageContext.request.contextPath}/laborbargain.do?method=addLabor&all="+document.getElementById("addall").value;
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
				var url="laborbargain.do?method=deleteUpload&autoId=${autoid}&fileName="+tbodyObj.fileName;
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
	myForm.action = "/AuditSystem/laborbargain.do?method=download&autoid=${autoid}";

	myForm.submit(); 
	
}


</script>




















</html>
