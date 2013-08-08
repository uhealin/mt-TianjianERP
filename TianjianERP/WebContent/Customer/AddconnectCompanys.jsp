<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/Validate_include.jsp"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.matech.audit.pub.db.DBConnect"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.ResultSet"%>
<%@ page import="com.matech.framework.pub.db.DbUtil" %>
<%
String customerid = request.getParameter("customerid");
Connection conn = new DBConnect().getConnect("");
String sql = "select departname from k_customer where departid='"+customerid+"'";
String bcostomername = "";
PreparedStatement ps = conn.prepareStatement(sql);
ResultSet rs = ps.executeQuery();
if(rs.next()) {
	bcostomername = rs.getString(1);
}
if(conn !=null){
	DbUtil.close(conn);
}
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>关联公司添加或修改</title>


<script type="text/javascript">  

function ext_init(){
	
	var tbar_customer = new Ext.Toolbar({
   		renderTo: "divBtn",
   		height:30,
   		defaults: {autoHeight: true,autoWidth:true},
           items:[{ 
            text:'保存',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/save.gif',
            handler:function(){
				addOrUpdateComName();
			}
      	},

      	'-',{ 
            text:'返回',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/back.gif',
            handler:function(){
            	window.location="${pageContext.request.contextPath}/connectcompanys.do?acts=update&&chooseCustomer=<%=customerid%>&frameTree=1";
				//window.history.back();
			}
      	},
       	
      	'-',new Ext.Toolbar.Fill()]
	});
	
} 


	window.attachEvent('onload',ext_init);
</script>










</head>

<body>
<div id="divBtn" ></div>
<div style="height:expression(document.body.clientHeight-30);overflow: auto" >

<fieldset style="width:100%"><legend>关联公司基本信息</legend> <br>
<br>

<form name="connectcompanyForm" method="post">
<jodd:form	bean="connectCompanys" scope="request">
	<input type="hidden" name="act" id="act" value="${param.act}" />
	<input type="hidden" name="acts" id="acts" value="${param.acts}" />

	<table width="100%" height="30" border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td width="25%" nowrap="nowrap">
			<div align="right"><font color="red" size=3>*</font>客户编号：</div>
			</td>
			<td width="75%"><input name="customerid" type="text"
				id="customerid" valuemustexist=true autoid=2
				value="${param.customerid}"></td>
		</tr>

		<c:choose>
			<c:when test="${param.act=='add'}">
				<tr>
					<td width="25%" nowrap="nowrap">
					<div align="right"><font color="red" size=3>*</font>关联公司名称：</div>
					</td>
					<td width="75%"><input name="connectcompanysname" type="text"
						id="connectcompanysname" size="20" maxlength="20" class="required"
						value="${connectCompanys.connectcompanysname }"
						title="请输入自定义名称，不能为空！" onblur="goCheckName();">
					<div id="div1" style=" float:left;"></div>
					</td>
				</tr>

			</c:when>

			<c:when test="${act=='update'}">
				<input type="hidden" name="getcompanyName" id="getcompanyName"
					value="${connectCompanys.connectcompanysname}">
				<input type="hidden" name="autoid" id="autoid" value="${autoid}">
				<tr>
					<td width="25%" nowrap="nowrap">
					<div align="right"><font color="red" size=3>*</font>关联公司名称：</div>
					</td>
					<td width="75%"><input name="connectcompanysname" type="text"
						id="connectcompanysname" size="20" maxlength="20"
						values="${connectCompanys.connectcompanysname }"
						title="请输入自定义名称，不能为空！" onblur="changeName();">
					<div id="div1" style=" float:left;"></div>
					</td>
				</tr>

				
			</c:when>
		</c:choose>

		<tr>
			<td width="25%">
			<div align="right"><font color="red" size=3>*</font>是否关联方：</div>
			</td>
			<td width="75%"><select name="isunion" id="isunion" onchange="changeIsunion();">
				<option value="是"
					<c:if test="${connectCompanys.isunion=='是' }">selected="selected"</c:if>>是</option>
				<option value="否"
					<c:if test="${connectCompanys.isunion=='否' }">selected="selected"</c:if>>否</option>
			</select>
				&nbsp;&nbsp;关联关系：<input name="relations" type="text"
						id="relations" size="20" maxlength="20" onkeydown="onKeyDownEvent();"
						onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"
						values="${connectCompanys.relations }"  autoid="4414"
						title="请输入自定义名称，不能为空!" >

					</td>
				</tr>
		
		</table>
		</fieldset>
		<fieldset style="width:100%"><legend>关联公司详细信息</legend> <br>
		<table width="100%" height="30" border="0" cellpadding="0" cellspacing="0">
		<c:choose>
			<c:when test="${param.act=='add'}">
		<tr>
			<td width="25%" nowrap="nowrap">
			<div align="right"><font color="red" size=3></font>本公司名称：</div>
			</td>
			<td width="75%"><input type="hidden" name="bcompanyname1"
				id="bcompanyname1" value="<%=bcostomername %>" /> <input
				name="bcompanyname" type="text" id="bcompanyname" size="20"
				maxlength="20" value="<%=bcostomername %>"
				title="请输入自定义名称，不能为空！">
			<div id="div2" style=" float:left;"></div>
			</td>
		</tr>
		</c:when>
		<c:when test="${act=='update'}">
		<tr>
			<td width="25%" nowrap="nowrap">
			<div align="right"><font color="red" size=3></font>本公司名称：</div>
			</td>
			<td width="75%"><input type="hidden" name="bcompanyname1"
				id="bcompanyname1" value="${connectCompanys.bcompanyname }" /> <input
				name="bcompanyname" type="text" id="bcompanyname" size="20"
				maxlength="20" value="${connectCompanys.bcompanyname }"
				title="请输入自定义名称，不能为空！">
			<div id="div2" style=" float:left;"></div>
			</td>
		</tr>
		</c:when>
		</c:choose>
		<tr>
			<td width="25%" nowrap="nowrap">
			<div align="right"><font color="red" size=3></font>本公司帐套：</div>
			</td>
			<td width="75%"><input type="text" name="baccpackageid"
				id="baccpackageid" onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"
				value="${connectCompanys.baccpackageid }" autoid="9"
				refer="customerid" title="请输入本公司帐套"></td>
		</tr>

		<tr>
			<td width="25%" nowrap="nowrap">
			<div align="right"><font color="red" size=3></font>对方公司在本公司科目编号：</div>
			</td>
			<td width="75%"><input type="text" name="bsubjectid"
				id="bsubjectid" onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"
				value="${connectCompanys.bsubjectid }" autoid="373"
				class='checkexist-wheninputed' 
				multilevel="true" valuemustexist="true" refer="baccpackageid" title="请输入对方公司在本公司科目编号"></td>
		</tr>
		<tr>
			<td width="25%" nowrap="nowrap">
			<div align="right"><font color="red" size=3></font>对方公司在本公司辅助核算编号：</div>
			</td>
			<td width="75%"><input type="text" name="bassitemid"
				id="bassitemid" onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"
				value="${connectCompanys.bassitemid }" autoid="374"
				multilevel="true" valuemustexist="true" refer="baccpackageid" refer1="bsubjectid" title="请输入对方公司在本公司辅助核算编号">
			</td>
		</tr>

		<tr>
			<td width="25%" nowrap="nowrap">
			<div align="right"><font color="red" size=3></font>对方客户编号：</div>
			</td>
			<td width="75%"><input type="text" name="dcustomerid"
				id="dcustomerid" onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"
				class='checkexist-wheninputed'
				value="${connectCompanys.dcustomerid }" autoid="2" title="请输入对方客户编号"></td>
		</tr>

		<tr>
			<td width="25%" nowrap="nowrap">
			<div align="right"><font color="red" size=3></font>对方客户帐套：</div>
			</td>
			<td width="75%"><input type="text" name="daccpackageid"
				id="daccpackageid" onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"
				value="${connectCompanys.daccpackageid }" autoid="9"
				refer="dcustomerid" title="请输入对方客户帐套"></td>
		</tr>

		<tr>
			<td width="25%" nowrap="nowrap">
			<div align="right"><font color="red" size=3></font>本公司在对方公司科目编号：</div>
			</td>
			<td width="75%"><input type="text" name="dsubjectid"
				id="dsubjectid" onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"
				value="${connectCompanys.dsubjectid }" autoid="373"
				class='checkexist-wheninputed'
				multilevel="true" valuemustexist="true" refer="daccpackageid" title="请输入本公司在对方公司科目编号"></td>
		</tr>

		<tr>
			<td width="25%" nowrap="nowrap">
			<div align="right"><font color="red" size=3></font>本公司在对方公司辅助核算编号：</div>
			</td>
			<td width="75%"><input type="text" name="dassitemid"
				id="dassitemid" onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"
				value="${connectCompanys.dassitemid }" autoid="374"
				multilevel="true" valuemustexist="true" refer="daccpackageid" refer1="dsubjectid" title="请输入本公司在对方公司辅助核算编号">
			</td>
		</tr>

	</table>
	<input name="id" type="hidden" id="id">

	
</jodd:form></form>

</fieldset>
</div>
</body>

<script type="text/javascript">
  new Validation("connectcompanyForm");

 function addOrUpdateComName(){

  	var act = document.getElementById("act").value;
  	var acts = document.getElementById("acts").value;

    if(act=="add"){
    	document.connectcompanyForm.action="${pageContext.request.contextPath}/connectcompanys.do?method=add&&acts="+acts;
    }else{
		document.connectcompanyForm.action="${pageContext.request.contextPath}/connectcompanys.do?method=update&autoid="+document.getElementById("autoid").value+"&&acts="+acts;
	}
	document.connectcompanyForm.submit();
}
 

// 检查该关联公司是否已经存在

function goCheckName() {
	if(connectcompanyForm.connectcompanysname.value != '') {
	  
		var aJax = new ActiveXObject("Microsoft.XMLHTTP");
		
		var customerid = document.getElementById("customerid").value
		var connectcompanyName = jsTrim(connectcompanyForm.connectcompanysname.value);
	
 		aJax.open("POST","${pageContext.request.contextPath}/connectcompanys.do?method=CheckName&connectcompanyName=" + connectcompanyName+"&customerid="+customerid,false);
	
		aJax.send();
		if(aJax.responseText != 'yes') {
			document.getElementById("div1").innerHTML = "<font color='red'>该关联公司已经存在！</font>";
			connectcompanyForm.connectcompanysname.value = '';
			document.all.connectcompanysname.focus();
		} else {
			document.getElementById("div1").innerHTML = "";
		}
	}else {
		document.getElementById("div1").innerHTML = "";
	}
}
// 检查该关联公司是否已经存在
function goCheckName1() {
	if(connectcompanyForm.bcompanyname.value != '') {
	  
		var aJax = new ActiveXObject("Microsoft.XMLHTTP");
		
		var customerid = document.getElementById("bcustomerid").value
		var connectcompanyName = jsTrim(connectcompanyForm.bcompanyname.value);
	
 		aJax.open("POST","${pageContext.request.contextPath}/connectcompanys.do?method=CheckName&connectcompanyName=" + connectcompanyName+"&customerid="+customerid,false);
	
		aJax.send();
		if(aJax.responseText != 'yes') {
			document.getElementById("div2").innerHTML = "<font color='red'>该关联公司已经存在！</font>";
			connectcompanyForm.bcompanyname.value = '';
			document.all.bcompanyname.focus();
		} else {
			document.getElementById("div2").innerHTML = "";
		}
	}else {
		document.getElementById("div2").innerHTML = "";
	}
}

function jsTrim(str) {
    return str.replace(/^\s* | \s*$/g,"");
}
//修改的时候先调用这个方法，如果没有改，则不调用检查方法
function changeName(){
	if(document.getElementById("getcompanyName").value==document.getElementById("connectcompanysname").value){
		
	}else{
		goCheckName();
	}

}

function changeName1(){
	if(document.getElementById("bcompanyname1").value==document.getElementById("bcompanyname").value){
		
	}else{
		goCheckName1();
	}

}

function setDisable(){
	setObjDisabled("customerid");
}

function changeIsunion() {
	var isunion = document.getElementById("isunion").value;
	
	if(isunion == "是") {
		document.getElementById("relations").disabled = false;
	} else {
		document.getElementById("relations").disabled = true;
		document.getElementById("relations").value = "";
	}
}
setTimeout("setDisable()","50");
</script>

</html>
