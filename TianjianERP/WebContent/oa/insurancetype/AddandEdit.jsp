<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>险种类型管理</title>
</head>
<body leftmargin="0" topmargin="0">
<input name="autoid" type="hidden" id="autoid" value="${autoid }">
<form name="thisForm" method="post"
	action="/AuditSystem/insurancetype.do?method=add" id="thisForm" enctype="multipart/form-data">
<jodd:form bean="itt" scope="request">
${menuLocation}

<fieldset style="width:100%"><legend>险种类型管理</legend>

	<table width="100%" height="80" border="0" cellpadding="0"
		cellspacing="0">
		<!-- 
  <tr>
    <td width="13%"><div align="right"><font color="red" size=3>*</font>缴费人：</div></td>
    <td width="87%"><input name="captureperson" type="text" id="captureperson"  maxlength="20"  class="required" title="请输入，不得为空">   
    <input name="autoid" type="hidden" id="autoid">   
    </td>
  </tr>
   -->
		<%
				System.out
				.println("myautoid:" + request.getParameter("autoid"));
				if (!"".equals(request.getParameter("autoid"))
				&& request.getParameter("autoid") != null) {
		%>
		<tr>
			<td width="13%">
			<div align="right"><font color="red" size=3>*</font>保险类型：</div>
			</td>
			<td width="87%"><input name="ctype" type="text" id="ctype"
				maxlength="20" readonly="readonly" class="required" title="请输入，不得为空">

			</td>
		</tr>
		<%
		} else {
		%>
		<tr>
			<td width="13%">
			<div align="right"><font color="red" size=3>*</font>保险类型：</div>
			</td>
			<td width="87%"><input name="ctype" type="text" id="ctype"
				maxlength="20" class="required" title="请输入，不得为空"></td>
		</tr>
		<%
		}
		%>

		
		<tr>
			<td width="13%">
			<div align="right"><font color="red" size=3>*</font>保险期限：</div>
			</td>
			<td width="87%"><input name="ctime" type="text" id="ctime"
				maxlength="50" class="required" title="请输入，不得为空"></td>
		</tr>
		<tr>
			<td>
			<div align="right">保险金额：</div>
			</td>
			<td width="87%"><input name="cmoney" type="text" id="cmoney"
				maxlength="20" title="请输入，不得为空">
		</tr>
		<tr>
			<td>
			<div align="right">保险费：</div>
			</td>
			<td width="87%"><input name="insurance" type="text"
				id="insurance" maxlength="20" title="请输入，不得为空">
		</tr>
		
		<tr>
			<td nowrap="nowrap">
			<div align="right">保险责任范围：</div>
			</td>
			<td nowrap><textarea rows="5" cols="40" id="carea" name="carea"></textarea>
			
		</tr>
		
		<tr>
			<td>
			<div align="right">附件：</div>
			</td>
			<td>
			<table width="80%" border="0" name=attachstable id=attachstable
				cellSpacing="1" cellPadding="3" bgColor="#eeeeee">
				<thead id=thead1>
					<tr bgColor="#B9C4D5">
						<td style="width:5%">
						<div align="center">选</div>
						</td>
						<td style="width:90%">
						<div align="center">附 件</div>
						</td>
					</tr>
					<tbody id="uploadTbody">
					
				<c:choose>
					<c:when test="${autoid != null}">
						<c:forEach items="${fileArr}" var="fileName">
		        			<tr>
								<td><input type=radio id=checkLine isExist="true" name="checkbox" fileName="${fileName}"></td>
								<td align="center" bgColor="#FFFFFF"><a href="#" onclick="goDownload('${fileName}');">${fileName}</a>
								<input type="hidden" name="existFile">
								</td>
							</tr>
		        		</c:forEach>  
					</c:when>
					<c:otherwise>
						
						<tr>
							<td><input type=checkbox id=checkLine isExist="false" name="checkbox" fileName=""></td>
							<td><input type="file" name="uploadFile" style="width:100%"></td>
						</tr>
					
					</c:otherwise>
				
				</c:choose>
				
				</tbody>

			</table>

			<input type=button onClick="addLine()" value=添加 name="button">
			<input type=button onClick="deleteLine()" value=删除 name="button">
			</td>
		</tr>


	</table>
	</fieldset>
</jodd:form>


<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td height="22" colspan="3">&nbsp;</td>
	</tr>
	<tr>
		<td width="37%" align="right"><input type="submit" name="next"
			value="确  定" class="flyBT" onclick="mySubmit();";></td>
		<td width="8%">&nbsp;</td>
		<td width="55%"><input type="button" name="back" value="返  回"
			class="flyBT" onClick="window.history.back();"></td>
	</tr>
</table>

<input name="AS_dog" type="hidden" id="AS_dog" value=""></form>


<form action="" method="POST" id="myForm" name="myForm">
	
	<input type="hidden" id="fileName_CH" name="fileName_CH">
	
</form>

<script type="text/javascript">
new Validation('thisForm');

function deleteLine()
{	
	var t=false;
	
	
	for (var i=uploadTbody.children.length-1; i>=0 ; i-- ) {
	var tbodyObj = uploadTbody.children[i].firstChild.firstChild ;
	if (tbodyObj.checked){
		
		if(tbodyObj.isExist) {
			
			if(confirm("您的操作可能会导致文件丢失,您确定删除吗?"))  {
			
				var oBao = new ActiveXObject("Microsoft.XMLHTTP");
				var url="insurancetype.do?method=deleteUpload&autoId=${autoid}&fileName="+tbodyObj.fileName;
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
	myForm.action = "/AuditSystem/insurancetype.do?method=download&autoid=${autoid}";

	myForm.submit(); 
	
}

function mySubmit() {
	var autoid = document.getElementById("autoid").value;
	if(document.getElementById("autoid").value!="") {
		thisForm.action="/AuditSystem/insurancetype.do?method=update&autoid="+autoid+"&all=${all}";
	} else {
		thisForm.action="/AuditSystem/insurancetype.do?method=add&all=${param.all}";
	}
//	thisForm.action="/AuditSystem/insurancetype.do?method=add";
}

</script>

</body>
</html>
