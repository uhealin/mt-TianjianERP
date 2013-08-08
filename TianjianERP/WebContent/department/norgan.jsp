<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8">
<title>单位信息</title>
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
function ext_init(){
	new Ext.form.DateField({
		applyTo : 'departdate',
		width: 133,
		format: 'Y-m-d'
	});	
	new Ext.form.DateField({
		applyTo : 'businessbegin',
		width: 133,
		format: 'Y-m-d'
	});
	new Ext.form.DateField({
		applyTo : 'businessend',
		width: 133,
		format: 'Y-m-d'
	});
		
}

//保存
function save(){
	if (!formSubmitCheck('thisForm')) return;
	thisForm.action = "${pageContext.request.contextPath}/department.do?method=save";
	thisForm.target = "";
	thisForm.submit();
}

</script>
</head>
<body>
<form name="thisForm" method="post" action="" >
<span class="formtitle" >
<c:if test="${organ.departid == ''}">新增单位信息</c:if>
<c:if test="${organ.departid != ''}">${organ.departname }</c:if>
<br/><br/></span>

<input type="hidden" id="tableid" name="tableid" value="k_organ" >
<input type="hidden" id="departid" name="departid" value="${organ.departid }">
<input type="hidden" id="issubject" name="issubject" value="${issubject }">

<table border="0" cellpadding="8" cellspacing="0" align="center" class="data_tb" >
<tr>
  <td class="data_tb_alignright" align="center" colspan="4">基本情况</td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">单位名称<span class="mustSpan">[*]</span>：</td>
  <td  class="data_tb_content" colspan="3"><input value="${organ.departname }" class="required" name="departname" type="text" id="departname" size=60 title="单位名称不能为空"></td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">单位备用名：</td>
  <td  class="data_tb_content" colspan="3"><input value="${organ.standbyname }" name="standbyname" type="text" id="standbyname" size="60"></td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">单位英文名：</td>
  <td  class="data_tb_content" colspan="3"><input value="${organ.departenname }" name="departenname" type="text" id="departenname" size="60"></td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">单位地址：</td>
  <td  class="data_tb_content" colspan="3"><input value="${organ.address }" name="address" type="text" id="address" maxlength="100"  size="60" maxlength="100"  title="单位地址"></td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">单位联系人：</td>
  <td  class="data_tb_content"  width="35%" ><input value="${organ.linkman }" name="linkman" type="text" id="linkman" maxlength="40" size="30"  title="单位联系人"></td>
  <td class="data_tb_alignright"  width="15%" align="right">联系电话：</td>
  <td  class="data_tb_content" ><input value="${organ.phone }" name="phone" type="text" id="phone" maxlength="20"  size="30" title="请正确输入联系电话"></td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">传真号码：</td>
  <td  class="data_tb_content" ><input value="${organ.fax }" name="fax" type="text" id="fax" maxlength="20"  size="30" title="请正确输入传真号码"></td>
  <td class="data_tb_alignright"  width="15%" align="right">电子邮件：</td>
  <td  class="data_tb_content" ><input value="${organ.email }" name="email" type="text" id="email" maxlength="50" size="30"  title="请正确输入电子邮件"></td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">邮政编码：</td>
  <td  class="data_tb_content" ><input value="${organ.postalcode }" name="postalcode" type="text" id="postalcode" size="30" maxlength="10"  title="请输入数字！"></td>
  <td class="data_tb_alignright"  width="15%" align="right">服务器地址：</td>
  <td  class="data_tb_content" ><input value="${organ.stockowner }" name="stockowner" type="text" id="stockowner" size="30" class="ip-wheninputed" title="对于分公司请填写本服务器的互联网访问ip或地址" ></td>
</tr>
<tr>
  <td class="data_tb_alignright" align="center" colspan="4">详细资料</td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">法人代表：</td>
  <td  class="data_tb_content" colspan="3"><input value="${organ.corporate }" name="corporate" type="text" id="corporate" maxlength="50"  title="法人代表"></td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">国税号：</td>
  <td  class="data_tb_content" ><input value="${organ.countrycess }" name="countrycess" type="text" maxlength="20"  id="countrycess" title="国税号" size="30"></td>
  <td class="data_tb_alignright"  width="15%" align="right">地税号：</td>
  <td  class="data_tb_content" ><input value="${organ.terracess }" name="terracess" type="text" maxlength="20"  id="terracess" title="地税号" size="30"></td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">企业代码：</td>
  <td  class="data_tb_content" ><input value="${organ.enterprisecode }" name="enterprisecode" type="text" maxlength="20"  id="enterprisecode" title="企业代码" size="30"></td>
  <td class="data_tb_alignright"  width="15%" align="right">成立日期：</td>
  <td class="data_tb_content" ><input value="${organ.departdate }" name="departdate" type="text" id="departdate" maxlength="20"  class="validate-date-cn"  title="成立日期" showcalendar="true"></td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">注册地址：</td>
  <td  class="data_tb_content" colspan="3"><input value="${organ.loginaddress }" name="loginaddress" type="text" id="loginaddress" maxlength="100"  size="60"  title="注册地址"></td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">经营期限起：</td>
  <td  class="data_tb_content" ><input value="${organ.businessbegin }" name="businessbegin" type="text" id="businessbegin" maxlength="10"  class="validate-date-cn"  title="经营期限" ></td>
  <td class="data_tb_alignright"  width="15%" align="right">经营期限至：</td>
  <td  class="data_tb_content" ><input value="${organ.businessend }" name="businessend" type="text" id="businessend" maxlength="10"  class="validate-date-cn"  title="请输入日期！" ></td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">经营范围：</td>
  <td  class="data_tb_content" colspan="3"><textarea name="businessbound" cols="85" rows="5"  id="businessbound"  title="经营范围">${organ.businessbound }</textarea></td>
</tr>
<tr>
  <td class="data_tb_alignright"  width="15%" align="right">备注：</td>
  <td  class="data_tb_content" colspan="3"><textarea name="remark" cols="85" rows="5" id="remark" >${organ.remark }</textarea></td>
</tr>
</table>

</form>
</body>
</html>
<script type="text/javascript">
var ifAdd = "${param.ifAdd}";
function update(){
	var form_obj = document.all; 
	//form的值
	for (i=0;i<form_obj.length ;i++ ) {
		e=form_obj[i];
		
		if (e.tagName=='INPUT' || e.tagName=='TEXTAREA') {
			e.readOnly = false ;
			e.className = "";
			if(e.type == 'checkbox'){
				e.disabled = false ;
			}
		}
		if(e.tagName=='SELECT'){
			e.className = "";
			e.disabled= false;
		}
		//alert(e.tagName);
		if(e.tagName == 'A'){
			e.style.display = "";
		}
		if(e.tagName == "IMG"){
			e.style.display = "";
		}
		
	}
	
	//恢复className
	document.getElementById("departname").className = "required";
}	


function view(){
	var form_obj = document.all; 
	//form的值
	for (i=0,j=0;i<form_obj.length ;i++ ) {
		e=form_obj[i];
		
		if (e.tagName=='INPUT' || e.tagName=='TEXTAREA') {
			e.readOnly = true ;
			e.className = "before";
			if(e.type == 'checkbox'){
				e.disabled = true ;
			}
		}
		if(e.tagName=='SELECT'){
			e.disabled= true;
			e.className = "before";
		}
		if(e.tagName == 'A'){
			e.style.display = "none";
		}
		if(e.tagName == "IMG"){
			e.style.display = "none";
		}
		
	}
}

Ext.onReady(function(){
	ext_init();
	if(ifAdd != "1"){
		view();
		var btn = parent.Ext.getCmp("btn-divTab");
		btn.setText("修改");
		btn.setIcon("${pageContext.request.contextPath}/img/edit.gif");
	}else{
		var btn = parent.Ext.getCmp("btn-divTab");
		btn.setText("保存");
		btn.setIcon("${pageContext.request.contextPath}/img/save.gif");
	}
});
</script>