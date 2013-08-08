<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>	
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp" %>
<%@page import="java.sql.Connection"%>
<%@page import="com.matech.audit.service.userdef.Userdef"%>
<%@page import="com.matech.framework.pub.util.ASFuntion"%>
<%@page import="com.matech.framework.pub.autocode.DELAutocode"%>
<%@page import="com.matech.framework.listener.UserSession"%>
<%@page import="com.matech.framework.pub.db.DbUtil"%>
<%@page import="com.matech.audit.pub.db.DBConnect"%>
<%
	ASFuntion CHF = new ASFuntion();	
 	String menuDetail=(String)request.getAttribute("menuDetail");
 	String act=(String)request.getAttribute("act");
 	String[] op = (String[])request.getAttribute("op");
 	String method = "";
 	if(act.equals("add")){
 		method = op[0];
 	}else{
 		if(act.equals("update")){
 			method = request.getAttribute("vocationid").toString();
 		}
 	}
	Userdef[] al=(Userdef[])request.getAttribute("userdef");
	
	UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
	String departmentid = userSession.getUserAuditDepartmentId();
	String userName = userSession.getUserName();//当前人
	
	DELAutocode t = new DELAutocode();
	
	String did = t.getAutoCode("KHDH", ""); //内部编号
	
	String area = "";
	Connection conn = null;
	try{
		String sql = "select a.enname from k_department a,k_department b where b.autoid = ? and a.level0 = 1 and b.fullpath like concat(a.fullpath,'%')";
		conn = new DBConnect().getConnect("");
		DbUtil db = new DbUtil(conn);
		area = db.queryForString(sql,new String[]{departmentid});
	} catch (Exception e) {
		area = "";
	} finally {
		DbUtil.close(conn);
	}
	
	String time = CHF.replaceStr(CHF.replaceStr(CHF.getCurrentDate()+CHF.getCurrentTime(), "-", ""), ":", "");
	String did2 = t.getAutoCode("ZDBH","",new String[]{area,time}); //单位编号(外部编号)
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>建立客户</title>
<style type="text/css">
.before{
	border: 0px;
	background-color: #FFFFFF !important;
}

.data_tb {
	background-color: #ffffff;
	text-align:center;
	margin:0 0px;
	width:80%;
	border:#8db2e3 1px solid; 
	BORDER-COLLAPSE: collapse; 
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



<script Language=JavaScript> 
var mytab;
	
	Ext.onReady(function(){
	
	   mytab = new Ext.TabPanel({
	        id: "tab",
	        renderTo: "divTab",
	        activeTab: 0, //选中第一个 tab
	        autoScroll:true,
	        frame: true,
	        height: document.body.clientHeight-Ext.get('divTab').getTop()-50, 
	        defaults: {autoHeight: true,autoWidth:true},
	        items:[
	            {contentEl: "tab1", title: "基本信息"},
	            {contentEl: "tab2", title: "详细信息"},
<c:if test="${sysUsr == '立信'}">  	            
	            {contentEl: "tab3", title: "报备与报告信息"},
</c:if> 	            
	            {contentEl: "tab4", title: "自定义信息"}
	        ]
	    });
	    
	    
	    var tbar_customer = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		height:30,
	   		defaults: {autoHeight: true,autoWidth:true},
            items:[{
	            text:'保存',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/save.gif',
	            handler:goAdEd
       		},
       		
		<c:if test="${frameTree != 1}">   		
		       		'-',{ 
			            text:'返回',
			            cls:'x-btn-text-icon',
			            icon:'${pageContext.request.contextPath}/img/back.gif',
			            handler:function(){
							history.back();
						}
		       		},
		</c:if> 
        		
       		'-',new Ext.Toolbar.Fill()]
        });
      
      	
      	new Ext.Viewport({
	        defaults:{border:false},
	        items:[
	        	tbar_customer
	        ]
	    })
	    
	    var date,udate,edate,stockStartDate,stockListingDate,aStateDate,fbusineDate,bstateDate;
	    
	    if(!date) {
		    date = new Ext.form.DateField({
				applyTo : 'departdate',
				width: 135,
				format: 'Y-m-d',
				emptyText: '' 
			});
	    }
	    
	    if(!udate) {
		    udate = new Ext.form.DateField({
				applyTo : 'businessbegin',
				width: 135,
				format: 'Y-m-d',
				emptyText: '' 
			});
	    }
		
		if(!edate) {
			edate = new Ext.form.DateField({
				applyTo : 'businessend',
				width: 135,
				format: 'Y-m-d',
				emptyText: '' 
			});
		}
		
		if(!stockStartDate){ 
			new Ext.form.DateField({
			    applyTo : 'stockStartDate',
			    width: 130,
				format: 'Y-m-d',
				emptyText: '' 
		    });
		}

		if(!stockListingDate){
	  		new Ext.form.DateField({ 
	  			applyTo : 'stockListingDate',
				width: 130,
				format: 'Y-m-d',
				emptyText: '' 
		   });
		}
	  
		if(!aStateDate){
	  		new Ext.form.DateField({ 
	  			applyTo : 'aStateDate',
				width: 130,
				format: 'Y-m-d',
				emptyText: '' 
		   });
		}
		if(!fbusineDate){
	  		new Ext.form.DateField({ 
	  			applyTo : 'fbusineDate',
				width: 130,
				format: 'Y-m-d',
				emptyText: '' 
		   });
		}
		if(!bstateDate){
	  		new Ext.form.DateField({ 
	  			applyTo : 'bstateDate',
				width: 130,
				format: 'Y-m-d',
				emptyText: '' 
		   });
		}
		
	});



function deleteLine() {
	var t=false;
	for (var i=UserDefTbody.children.length-1; i>=0 ; i-- )
	if (UserDefTbody.children[i].firstChild.firstChild.checked){
		UserDefTbody.deleteRow(i);
		t=true;
	}
	if(!t)
	{
		alert("请选定其中一列！！");
	}
}

function IsValidDate(obj){
	var str = obj.value;
	
	if(str == ""){
		return true;
	}
	
	var reg = /^(?:([0-9]{4}-(?:(?:0?[1,3-9]|1[0-2])-(?:29|30)|((?:0?[13578]|1[02])-31)))|([0-9]{4}-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|1\d|2[0-8]))|(((?:(\d\d(?:0[48]|[2468][048]|[13579][26]))|(?:0[48]00|[2468][048]00|[13579][26]00))-0?2-29)))$/;
	if(!str.match(reg)){
		alert("日期格式不正确，格式：yyyy-mm-dd");
		obj.select();
		return false;
	}
}

function isCurrency(obj){
	var reg = /^([1-9,-]{1}[0-9,-]{0,2}(\,[0-9,-]{3})*(\.[0-9,-]{0,2})?|[1-9,-]{1}\d*(\.[0-9,-]{0,2})?|0(\.[0-9,-]{0,2})?|(\.[0-9,-]{1,2})?)$/;
	if(!reg.test(obj)){
		return false;
	}
	return true;
}

function addLine()
{
	var obj=document.getElementById("UserDefTbodyCount");
	//增加计数
	obj.value=obj.value * 1 + 1;
	var objTr=attachstable.insertRow(-1);
	objTr.bgColor = "#ffffff";
	var objTd=objTr.insertCell(-1);
	objTd.innerHTML="<input type=checkbox id=checkLine name=\"checkbox\">";
	objTd.align="center";

	objTd=objTr.insertCell(-1);
	objTd.innerHTML="<input type=\"text\" name=\"UserDefName\"  id=\"UserDefName"+obj.value+"\" class='required' maxLength=20 title='请输入，不得为空' style=\"width: 100%; height: 18\">";
	objTd=objTr.insertCell(-1);
	objTd.innerHTML="<input type=\"text\" name=\"UserDefValue\" id=\"UserDefValue"+obj.value+"\"  class='required'  title='请输入，不得为空' style=\"width: 100%; height: 18\">";

}
</script>


</head>
<body>

<div id="panel"></div>


<div id="divBtn" ></div>

<form name="thisForm" method="post" action="" id="thisForm" style="padding: 1 10 10 10;background-color: #ecf2f2;margin: 1 10 10 10; border: 1px solid #AEC9D3;">
 <input name="isState" type="hidden" id="isState" /> <!-- 单选，状态 -->
<center style="color: #4A74BC;font-weight: bold;font-size: 14px;" >
客&nbsp;&nbsp;户&nbsp;&nbsp;信&nbsp;&nbsp;息<br/><br/> 

</center>

<div id="_summary">
<div id="divTab" style="overflow:auto">
<div id="tab1" style="padding:10 10 10 10;">

<table  cellpadding="8" cellspacing="0" align="center" class="data_tb" >
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">单位编号<span class="mustSpan">[*]</span>：</td>
	<td  class="data_tb_content" colspan="3">
    	<input class="required" name="custdepartid" type="text" id="custdepartid" value="<%=did2 %>" title="例如北京的客户就是BJYYMMDDHHMM（例如：BJ201109071459），代表此客户为北京地区的客户，初次建立客户资料的时间为2011年09月07日14时59分。" >
    	<font color="red">生成规则按照“地区+提交的时间”格式统一编号</font>
	</td>
</tr>
 <tr style="display: none;">
	<td class="data_tb_alignright"  width="15%" align="right">内部编号<span class="mustSpan">[*]</span>：</td>
	<td  class="data_tb_content" colspan="3"><input name="departid" type="text" id="departid" style="background-color: #eeeeee;" value="${customer.departId }" readonly="readonly"></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">单位名称<span class="mustSpan">[*]</span>：</td>
	<td class="data_tb_content" colspan="3">
		<input onBlur="isExist();" name="departname" type="text" value="${customer.departName }" class="required" id="departname" title="客户名称不能为空且只允许输入200个字符" maxlength="200" size="50">
      	<span id="departname111" style="display: none;"></span>
    </td>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">单位简称：</td>
	<td class="data_tb_content" colspan="3"><input  name="customerShortName" type="text" id="customerShortName" title="单位简称" value="${customer.customerShortName }" maxlength="200" size="50"></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">单位曾用名：</td>
	<td class="data_tb_content" colspan="3"><input name="beforeName" type="text"  id="beforeName" title="单位曾用名" value="${customer.beforeName }" maxlength="200" size="50"></td> 
</tr> 
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">单位英文名称：</td>
    <td class="data_tb_content" colspan="3"><input name="departEnName" type="text"  id="departEnName" title="单位英文名称" value="${customer.departEnName }" maxlength="50" size="50"></td> 
</tr>   
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">会计制度类型<span class="mustSpan">[*]</span>：</td>
	<td class="data_tb_content" >
     	<input name="vocationid" type="text" id="vocationid" value="${customer.vocationId}"  class="required validate-digits"  title="请输入数字！" onkeydown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" onClick="onPopDivClick(this);" valuemustexist=true autoid=3 noinput=true >
		<input type="hidden" name="isdifferent" id="isdifferent" >
	</td>
	<td class="data_tb_alignright"  width="15%" align="right">客户归属部门：</td>
	<td class="data_tb_content" >
		<input name="departmentid" type="text" value="${customer.departmentid}" id="departmentid" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true
		<c:if test="${userSession.userId == '19'}">autoid=123</c:if>      	 
		<c:if test="${userSession.userId != '19'}">autoid=715</c:if>        	
		noinput=true autoHeight=150>
	</td>
</tr>
<tr>
	 <td class="data_tb_alignright"  width="15%" align="right">行业类型：</td>
	 <td class="data_tb_content" >
     	<input name="hylx" type="text" id="hylx" value="${customer.hylx}" onkeydown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" onClick="onPopDivClick(this);" valuemustexist=true autoid=261  >
	</td>
	<td class="data_tb_alignright"  width="15%" align="right">国别：</td>
    <td class="data_tb_content" ><input type="nation" id="nation"  title="国别" value="${customer.nation}" size="8"  ></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">资产总额：</td>
    <td class="data_tb_content" width="35%"><input name="totalassets" type="text" id="totalassets" title="资产总额必须为货币格式" maxlength="20" value="${customer.totalassets}" size="20" class="validate-currency"></td>
    <td class="data_tb_alignright"  width="15%" align="right">货币类型<span class="mustSpan">[*]</span>：</td>
    <td class="data_tb_content" width="35%"><input type="totalcurname" id="totalcurname" title="货币类型" value="${customer.totalcurname}" size="8" onkeydown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" onClick="onPopDivClick(this);" autoid="281" class="required" autoHeight=200></td>
</tr>    
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">注册资本：</td>
    <td class="data_tb_content" width="35%"><input name="register" type="text" id="register" title="注册资本必须为货币格式" maxlength="20" value="${customer.register}" size="20" class="validate-currency"></td>
	<td class="data_tb_alignright"  width="15%" align="right">货币类型：</td>
	<td class="data_tb_content" width="35%"><input type="curname" id="curname" title="货币类型" value="${customer.curname}" size="8"  onkeydown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" onClick="onPopDivClick(this);" autoid="281" class="required"></td>
</tr>

<tr>
	<td class="data_tb_alignright"  width="15%" align="right">所属集团：</td>
	<td class="data_tb_content" colspan="3"><input name="groupname" type="text" id="groupname" value="${customer.groupname}" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();"  onClick="onPopDivClick(this);"   valuemustexist="true" multilevel=true autoid="621"  maxlength="20"></td>
	<td class="data_tb_alignright"  width="15%" align="right" style="display:none">所属集团板块：</td>
	<td class="data_tb_content" ><input name="groupplate" type="hidden" id="groupplate" value="" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();"  onClick="onPopDivClick(this);"   valuemustexist="true" autoid="700" refer = '所属集团板块'  maxlength="20"></td>
</tr>    
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">组织机构性质：</td>
	<td class="data_tb_content" ><input onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" onClick="onPopDivClick(this);" valuemustexist=true autoid=700 refer='组织机构性质' noinput=true name="iframework" type="text" id="iframework" maxlength="200"  value="${customer.iframework}"  title="组织机构性质"></td>
	<td class="data_tb_alignright"  width="15%" align="right">公司性质：</td>
	<td class="data_tb_content" ><input onpropertychange="company(this);" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" onClick="onPopDivClick(this);" valuemustexist=true autoid=700 refer='公司性质' noinput=true name="companyProperty" type="text" id="companyProperty" maxlength="200"  value="${customer.companyProperty }"  title="组织机构性质"></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">控股股东/上级公司：</td>
	<td class="data_tb_content" colspan="3"><input onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" onClick="onPopDivClick(this);" valuemustexist=true autoid=710  name="parentName" type="text"  id="parentName" title="控股股东/上级公司" value="${customer.parentName }" maxlength="200" size="50"></td> 
</tr>
<tr>
    <td class="data_tb_alignright"  width="15%" align="right">控股方：</td> 
	<td class="data_tb_content" colspan="3"><input name="holding" type="text"  id="holding" title="控股方" value="${customer.holding }" maxlength="200" size="50"></td> 
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">股票板块<span id="id1" style="display: none;" class="mustSpan">[*]</span>：</td>
	<td class="data_tb_content" colspan="3"><input title="请输入股票板块" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" onClick="onPopDivClick(this);" valuemustexist=true autoid=700 refer='所属板块' noinput=true name="plate" type="text" id="plate" maxlength="200" size="60" value="${customer.plate }"  ></td>
</tr> 
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">证券市场1<span id="id2" style="display: none;" class="mustSpan">[*]</span>：</td>
    <td class="data_tb_content" width="35%"><input refer="证券市场" title="请输入证券市场" name="sMarket" value="${customer.sMarket }" type="text" id="sMarket"  onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();"  onClick="onPopDivClick(this);"   valuemustexist="true" autoid=700 maxlength="10"></td> 
    <td class="data_tb_alignright"  width="15%" align="right">股票代码1<span id="id3" style="display: none;" class="mustSpan">[*]</span>：</td>
	<td class="data_tb_content" width="35%"><input name="sockCode" type="text" id="sockCode" value="${customer.sockCode }" maxlength="20"  class="phonenumber-wheninputed" title="请正确输入股票代码"></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">证券市场2<span style="display: none;" class="mustSpan">[*]</span>：</td>
    <td class="data_tb_content" width="35%"><!-- <input title="请输入证券市场2" name="sMarket2" type="text" id="sMarket2"  value="H股" >-->H股</td> 
    <td class="data_tb_alignright"  width="15%" align="right">股票代码2<span id="id3" style="display: none;" class="mustSpan">[*]</span>：</td>
	<td class="data_tb_content" width="35%"><input name="sockCode2" type="text" id="sockCode2" value="${customer.sockCode2 }"   maxlength="20"  class="phonenumber-wheninputed" title="请正确输入股票代码2"></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">单位联系人：</td>
	<td class="data_tb_content" width="35%"><input name="linkman" type="text"  id="linkman" value="${customer.linkMan }" maxlength="40"  title="单位联系人"></td>
	<td class="data_tb_alignright"  width="15%" align="right">联系电话：</td>
	<td class="data_tb_content" width="35%"><input name="phone" type="text" id="phone" value="${customer.phone }" class="phonenumber-wheninputed" maxlength="20" title="请正确输入联系电话"></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">单位地址：</td>
	<td class="data_tb_content" colspan="3"><input name="address" type="text" value="${customer.address }" id="address" size="60" maxlength="60"  title="单位地址"></td>
</tr>
</table>

</div>

<div id="tab2" class="x-hide-display" style="padding:10 10 0 10;overflow: auto;">
<table  cellpadding="8" cellspacing="0" align="center" class="data_tb" > 
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">法人代表：</td>
	<td class="data_tb_content" width="35%"><input name="corporate" type="text" id="corporate" value="${customer.corporate }" maxlength="50" title="法人代表"></td>
	<td class="data_tb_alignright"  width="15%" align="right">纳税人识别号：</td>
	<td class="data_tb_content" width="35%"><input name="taxpayer" type="text" id="taxpayer" value="${customer.taxpayer }" maxlength="20" title="纳税人识别号"></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">国税号：</td>
	<td class="data_tb_content" colspan="3"><input name="countrycess" type="text"  id="countrycess" title="国税号" value="${customer.countryCess }" maxlength="20" size="60"></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">地税号：</td>
	<td class="data_tb_content" colspan="3"><input name="terracess" type="text"  id="terracess" title="地税号" value="${customer.terraCess }" maxlength="20" size="60"></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">营业执照注册号：</td>
    <td class="data_tb_content" colspan="3"><input name="BPR" type="text"  id="BPR" title="营业执照注册号" maxlength="20" value="${customer.bpr }" size="60"></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">企业代码：</td>
	<td class="data_tb_content" colspan="3"><input name="enterprisecode" type="text"  id="enterprisecode" title="企业代码" value="${customer.enterpriseCode }" maxlength="20" size="60"></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">注册地址：</td>
    <td class="data_tb_content" colspan="3"><input name="loginaddress" type="text" id="loginaddress" maxlength="200" size="60" value="${customer.loginAddress }"  title="注册地址"></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">成立日期：</td>
	<td class="data_tb_content" colspan="3"><input name="departdate" type="text" id="departdate" value="${customer.departDate }"  maxlength="10" class="validate-date-cn"  title="成立日期,格式yyyy-mm-dd"  onblur="return IsValidDate(this);" ></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">传真号码：</td>
    <td class="data_tb_content" width="35%"><input name="fax" type="text" id="fax" value="${customer.fax }" maxlength="20"  class="phonenumber-wheninputed" title="请正确输入传真号码"></td>
	<td class="data_tb_alignright"  width="15%" align="right">电子邮件：</td>
	<td class="data_tb_content" width="35%"><input name="email" type="text" id="email" value="${customer.email }"  class="validate-email" maxlength="50" title="请正确输入email！"></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">邮政编码：</td>
    <td class="data_tb_content" colspan="3"><input name="postalcode" type="text" id="postalcode" value="${customer.postalcode }" class="validate-digits"  maxlength="10" title="请输入数字！"></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">从业人数：</td>
	<td class="data_tb_content" colspan="3"><input  onkeyup="value=value.replace(/[^\d]/g,'')"  name="practitioner" type="text" id="practitioner" value="${customer.practitioner }"   maxlength="10"></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">经营方式：</td>
	<td class="data_tb_content" colspan="3"><input name="fashion" type="text" id="fashion" value="${customer.fashion }"  maxlength="10" ></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">客户级别：</td>
	<td class="data_tb_content" width="35%">
 		<!--<input name="customerIeve" type="text" id="customerIeve" value="${customer.customerIeve }" maxlength="20" >  -->  <!--  class="phonenumber-wheninputed" title="请正确输入客户级别" -->
		 <select id="customerIeve" name="customerIeve">
      	  <option value="A级" <c:if test="${customer.customerIeve=='A级' }">selected</c:if> >A级</option>
      	  <option value="B级" <c:if test="${customer.customerIeve=='B级' }">selected</c:if> >B级</option>
      	  <option value="C级" <c:if test="${customer.customerIeve=='C级' }">selected</c:if> >C级</option>
      	  <option value="D级" <c:if test="${customer.customerIeve=='D级' }">selected</c:if> >D级</option>
      	  <option value="E级" <c:if test="${customer.customerIeve=='E级' }">selected</c:if> >E级</option>
      	</select>
		
	
	</td>
	<td class="data_tb_alignright"  width="15%" align="right">网址：</td>
	<td class="data_tb_content" width="35%"><input name="webSite" type="text" id="webSite" value="${customer.webSite }" maxlength="40"  class="ip-wheninputed" title="请正确输入网址"></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">项目状态：</td>
	<td class="data_tb_content" width="35%"><input name="projectState" type="text" id="projectState" value="${customer.projectState }" maxlength="20"  ></td><!-- class="phonenumber-wheninputed" title="请正确输入项目状态" -->
	<td class="data_tb_alignright"  width="15%" align="right">状态：</td>
    <td class="data_tb_content">
    	<input name="state" type="text" id="state" maxlength="20" value="${customer.state }"  title="请正确输入状态">
    	<input name="calling" id="calling" type="hidden">  
    </td>
</tr>
<tr id="TRdescription" style="display:none">
	<td class="data_tb_alignright"  width="15%" align="right">行业信息：</td>
	<td class="data_tb_content" colspan="3"><textarea rows="10" cols="50" id="hyDescription" readonly="readonly"></textarea></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">客户状态：</td>
	<td class="data_tb_content" colspan="3"><input  title="客户状态" name="estate" type="text" id="estate" refer='客户状态' value="${customer.estate }" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();"  onClick="onPopDivClick(this);"   valuemustexist="true" autoid=700 maxlength="10"></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">客户资料来源途径：</td>
	<td class="data_tb_content" width="35%"><input onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" onClick="onPopDivClick(this);" valuemustexist=true autoid=700 refer='客户来源方式' noinput=true name="approach" type="text" id="approach" value="${customer.approach }"  maxlength="20" ></td>
	<td class="data_tb_alignright"  width="15%" align="right">介绍人姓名：</td>
	<td class="data_tb_content" width="35%"><input name="intro" type="text" id="intro" value="${customer.intro }"   maxlength="50" ></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">主负责人：</td>
	<td class="data_tb_content" colspan="3"><input name="mostly" type="text" id="mostly" value="<%=userName %>"  onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();"  onClick="onPopDivClick(this);"   valuemustexist="true" autoid=362 maxlength="10"></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">副负责人：</td>
	<td class="data_tb_content" colspan="3"><input name="subordination" type="text" id="subordination" value="${customer.subordination }" multiselect="true"  title="请输入，不得为空" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();"  onClick="onPopDivClick(this);" valuemustexist="true" autoid=362  maxlength="10" ></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">经营期限：</td>
	<td class="data_tb_content" colspan="3">
		<input type="radio" name="business_type" id="periodOfTimeBusiness" onClick="periodOfTime();" checked>经营时段
      	<input type="radio" name="business_type" id="LongTimeBusiness" onClick="longTime();">长期经营
	</td>
</tr>
<tr id="tttt">	      	
	<td class="data_tb_alignright"  width="15%" align="right">经营期限起：</td>      	
    <td class="data_tb_content" width="35%"><input name="businessbegin"  type="text" id="businessbegin" value="${customer.businessBegin }" maxlength="10" class="validate-date-cn"  title="请输入日期！格式yyyy-mm-dd"  onblur="return IsValidDate(this);"></td>
    <td class="data_tb_alignright"  width="15%" align="right">经营期限至：</td>
	<td class="data_tb_content" width="35%">
		<input name="businessend" type="text" id="businessend" value="${customer.businessEnd }" maxlength="10" class="validate-date-cn"  title="请输入日期！格式yyyy-mm-dd"  onblur="return IsValidDate(this);">
      	<input type="hidden" name="businessOfLongTime" id="businessOfLongTime" value="">
	</td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">经营范围：</td>
	<td class="data_tb_content" colspan="3"><textarea name="businessbound" cols="65" rows="3" onKeyUp="if(this.value.length>500)this.value=this.value.substring(0,500);" title="经营范围">${customer.businessBound }</textarea></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">备注：</td>
	<td class="data_tb_content" colspan="3"><textarea name="remark" cols="65" rows="3"  onkeyup="if(this.value.length>200)this.value=this.value.substring(0,200);" maxlength="10" id="remark" >${customer.remark }</textarea></td>
</tr>
</table>

</div>


<div id="tab3" class="x-hide-display" style="padding:10 10 10 10;">

<table  cellpadding="8" cellspacing="0" align="center" class="data_tb" > 
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">投资人员：</td>
	<td class="data_tb_content" colspan="3"><input name="iTmentName" type="text" id="iTmentName" value="" maxlength="40"  size="70" title="投资人员"></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">审批机构批准文号：</td>
	<td class="data_tb_content" width="35%"><input name="agency" type="text" id="agency" value="" maxlength="40"  title="审批机构批准文号"></td>
	<td class="data_tb_alignright"  width="15%" align="right">开始日期：</td>
	<td class="data_tb_content" width="35%"><input name="aStateDate" type="text" id="aStateDate" align="left" value="" maxlength="20"  title="请正确输入开始日期"></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">营业执照批准文号：</td>
	<td class="data_tb_content" width="35%"><input name="busineLicense" type="text" id="busineLicense" value="" maxlength="40"  title="营业执照批准文号"></td>
	<td class="data_tb_alignright"  width="15%" align="right">开始日期：</td>
	<td class="data_tb_content" width="35%"><input name="bstateDate" type="text" id="bstateDate" value="" maxlength="20"  class="phonenumber-wheninputed" title="请正确输入开始日期"></td>
</tr>   
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">董事长姓名：</td>
	<td class="data_tb_content" width="35%"><input name="directorName" type="text" id="directorName" value="" maxlength="40"  title="董事长姓名"></td>
	<td class="data_tb_alignright"  width="15%" align="right">董事长电话：</td>
	<td class="data_tb_content" width="35%"><input  onkeyup="value=value.replace(/[^\d]/g,'')" name="directorPhone" type="text" id="directorPhone" value="" maxlength="20"  class="phonenumber-wheninputed" title="请正确输入董事长电话"></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">懂秘：</td>
	<td class="data_tb_content" width="35%"><input name="dSecretary" type="text" id="dSecretary" value="" maxlength="40"  title="懂秘"></td>
	<td class="data_tb_alignright"  width="15%" align="right">董秘电话：</td>
	<td class="data_tb_content" width="35%"><input  onkeyup="value=value.replace(/[^\d]/g,'')" name="secretaryPhone" type="text" id="secretaryPhone" value="" maxlength="20"  class="phonenumber-wheninputed" title="请正确输入董秘电话"></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">总会计师：</td>
	<td class="data_tb_content" width="35%"><input name="sAccountant" type="text" id="sAccountant" value="" maxlength="40"  title="总会计师"></td>
	<td class="data_tb_alignright"  width="15%" align="right">总会记师电话：</td>
	<td class="data_tb_content" width="35%"><input   onkeyup="value=value.replace(/[^\d]/g,'')" name="accountanrPhone" type="text" id="accountanrPhone" value="" maxlength="20"  class="phonenumber-wheninputed" title="请正确输入总会记师电话">      </td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">职工总数：</td>
	<td class="data_tb_content" colspan="3"><input  onkeyup="value=value.replace(/[^\d]/g,'')" name="ctaffQuantity" type="text"  id="ctaffQuantity" title="职工总数" value="" maxlength="20" size="20"></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">财务总监：</td>
	<td class="data_tb_content" colspan="3"><input name="fDirector" type="text"  id="fDirector" title="财务总监" value="" maxlength="20" size="20"></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">财务经理：</td>
	<td class="data_tb_content" width="35%"><input name="fManager" type="text" id="fManager" value="" maxlength="40"  title="财务经理"></td>
	<td class="data_tb_alignright"  width="15%" align="right">财务经理电话：</td>
	<td class="data_tb_content" width="35%"><input   onkeyup="value=value.replace(/[^\d]/g,'')" name="fPhone" type="text" id="fPhone" value="" maxlength="20"  class="phonenumber-wheninputed" title="请正确输入财务经理电话"></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">办公地址(<font color="red">省</font>)：</td>
	<td class="data_tb_content" width="35%"><input name="pOfficeAddress" type="text" id="pOfficeAddress" value="" maxlength="40"  title="办公地址(省)"></td>
	<td class="data_tb_alignright"  width="15%" align="right">办公地址(<font color="#00B300">市</font>)：</td>
	<td class="data_tb_content" width="35%"><input name="cOfficeAddress" type="text" id="cOfficeAddress" value="" maxlength="20"  class="phonenumber-wheninputed" title="请正确输入办公地址(市)"></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">股票发行日期：</td>
	<td class="data_tb_content" colspan="3"><input size="20" name="stockStartDate" type="text"  id="stockStartDate" title="股票发行日期" value="" maxlength="20" size="20"></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">股票上市日期：</td>
	<td class="data_tb_content" colspan="3"><input  size="20"name="stockListingDate" type="text" id="stockListingDate" maxlength="200" size="20" value=""  title="股票上市日期"></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">首次承接业务日期：</td>
	<td class="data_tb_content" colspan="3"><input size="20" name="fbusineDate" type="text" id="fbusineDate" value=""  maxlength="10" class="validate-date-cn"  title="首次承接业务日期"    ></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">纳入合并报表范围子公司数量</td>
	<td class="data_tb_content" colspan="3"><input  onkeyup="value=value.replace(/[^\d]/g,'')"  name="mergerQuantity" type="text" id="mergerQuantity" value="" class="validate-digits"  maxlength="10" title="请输入纳入合并报表范围子公司数量"></td>
</tr> 
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">是否变更事务所：</td>
	<td class="data_tb_content" colspan="3">
		<input name="ischanged" id="ischangeYES" type="radio" value="是" checked="checked" onClick="document.getElementById('ischange').value = document.getElementById('ischangeYES').value;">是&nbsp;&nbsp;&nbsp;&nbsp; 
		<input type="radio" name="ischanged" id="ischangeNO" value="否" onClick="document.getElementById('ischange').value = document.getElementById('ischangeNO').value;">否
		<input type="hidden" id="ischange" value="是" name ="ischange">
	</td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">前任事务所：</td>
	<td class="data_tb_content" colspan="3"><input name="agoOffice" type="text" id="agoOffice" maxlength="200" size="67" value=""  title="前任事务所"></td>
</tr>
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">变更原因：</td>
	<td class="data_tb_content" colspan="3"><textarea name="cReason" id="cReason" cols="65" rows="5"  title="变更原因"></textarea></td>
</tr> 	
<tr>
	<td class="data_tb_alignright"  width="15%" align="right">独资或控股子公司说明：</td>
	<td class="data_tb_content" colspan="3"><textarea name="explain" id="explain" cols="65" rows="5" onKeyUp="if(this.value.length>500)this.value=this.value.substring(0,500);" title="经营范围"></textarea></td>
</tr> 		
</table>


</div>

<div id="tab4" class="x-hide-display" style="padding:10 10 10 10;">
<fieldset>
	<legend>自定义信息</legend>
	<input type="hidden" name="setdef" id="setdef" value="0"> 
   <table width="98%" border="0" cellpadding="0" cellspacing="0" >
    <tr>
      <td colspan="4">
      <br>
	  <br>
	      <table border="0" name=attachstable id=attachstable cellSpacing="1" width="600" cellPadding="3" bgColor="#6595D6">
	          <thead id=thead1>
	          <tr bgColor="#B9C4D5">
	            <td width="5%">
	              <div align="center">选</div>
	            </td>
	            <td width="30%">
	              <div align="center">自定义名字</div>
	            </td>
	            <td width="65%">
	              <div align="center">自定义值</div>
	            </td>
	          </tr>
	    <c:choose>
			<c:when test="${act=='add'}">
			    <script>
			         document.getElementById("setdef").value="1";
			    </script>  
			       
		        <c:forEach items="${setValueList}" var="setvalue">
		          <tr bgcolor="#ffffff">
	          		<td>&nbsp;</td>
	          		<td><input type="text" name="defName"  value="${setvalue.defName}" id="defName" readonly="readonly" maxLength=20  style="width: 100%;  height: 18"></td>
	              	<td>
		              	<c:if test="${setvalue.dicType == null || setvalue.dicType == ''}">
		              		<input type="text" name="defValue" value="" id="defValue" style="width: 100%; height: 18">
		              	</c:if>
	              		<c:if test="${setvalue.dicType != null && setvalue.dicType != ''}">
		              		<input onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" onClick="onPopDivClick(this);" valuemustexist=true autoid=700 refer='${setvalue.dicType}' noinput=true type="text" name="defValue" value="" id="defValue" style="width: 100%; height: 18">
		              	</c:if>
	              	</td>	         
		          </tr>
		        </c:forEach>  
		    </c:when>  
		    
		    <c:when test="${act=='update'}"> 
		   	    <script>
			         document.getElementById("setdef").value="1";
			    </script>  
			          
		        <c:forEach items="${setValueList}" var="setvalue">
		          <tr bgcolor="#ffffff">
	          		<td>&nbsp;</td>
	          		<td><input type="text" name="defName"  value="${setvalue.defName}" id="defName" readonly="readonly" maxLength=20  style="width: 100%;  height: 18"></td>
	              	<td>
	              		<c:if test="${setvalue.dicType == null || setvalue.dicType == ''}">
	              			<input type="text" name="defValue" value="${setvalue.defValue}" id="defValue"  style="width: 100%;  height: 18">
	              		</c:if>
	              		<c:if test="${setvalue.dicType != null && setvalue.dicType != ''}">
	              			<input onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" onClick="onPopDivClick(this);" valuemustexist=true autoid=700 refer='${setvalue.dicType}' noinput=true type="text" name="defValue" value="${setvalue.defValue}" id="defValue"  style="width: 100%;  height: 18">
	              		</c:if>
	              	</td>	         
		          </tr>
		        </c:forEach>  
		    </c:when>   
	    </c:choose>      
	  			<tbody id=UserDefTbody>
	
	          	</tbody>
	        </table>
      
      </td>
    </tr>
    
  </table>

<input type="hidden" id=UserDefTbodyCount name=UserDefTbodyCount value="0" />

<input type="hidden" id="commondefNames" name="commondefNames" value="names" />
<input type="hidden" id="commondefValues" name="commondefValues" value="values" />

<input type="hidden" id="custcode" name="custcode"/>

<br/>
<input type=button onClick="addLine()" value=添加自定义行 name="button"/>
<input type=button onClick="deleteLine()" value=删除自定义行 name="button"/>

</fieldset>

</div>
</div>




 
 


  <p>&nbsp;</p>
	<input name="property" type="hidden" id="property" value="1"/>
  <input name="submitStr" type="hidden" id="submitStr"/>
  <input name="id" type="hidden" id="id"/>
   <input id="isShowTip" name="isShowTip" type="hidden" value=""/>
  <input name="adored" type="hidden" id="adored" value="ad"/><!--
  
  <center>
  		<input type="submit" name="next" id="opSave" value="保  存" class="flyBT" onclick="return goAdEd();">&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="button" name="back" id="back" value="返  回" class="flyBT"  onClick="goClose();"></td>
  </center>
-->

</form>


</body>

<%
if(al!=null){
	for(int i=0; i<al.length;i++){
		Userdef	udtab =al[i];
		%>
		<script Language=JavaScript>

		addLine();
		document.getElementById("UserDefName<%=i+1%>").value = "<%=udtab.getName()%>"
		document.getElementById("UserDefValue<%=i+1%>").value = "<%=udtab.getValue()%>"
		</script>
		<%
			}	
	}


%>

<script>

//验证是否修改
protect("thisForm");

	function periodOfTime(){
		document.all.businessend.value = "";
		document.getElementById("businessbegin").value = "";
		document.getElementById("tttt").style.display ="";
	}
	
	function longTime(){
		document.getElementById("tttt").style.display ="none";
		document.getElementById("businessbegin").value = "长期经营";
		
	}
</script>
<%
	String businessbegin = CHF.getXMLData(menuDetail,"businessbegin");
	
	if(businessbegin != null && businessbegin.equals("长期经营")){
%>
<script>
	document.getElementById("LongTimeBusiness").checked = true;
	longTime();
</script>
<%
	}
	String curname = CHF.showNull(CHF.getXMLData(menuDetail,"curname"));

	if(curname.equals("")){
%>
<script>
	document.thisForm.curname.value = "人民币";
	document.thisForm.totalcurname.value = "人民币";
</script>
<%
	}
	else {
%>
<script>
	document.thisForm.curname.value = "<%=curname%>";
</script>
<%
	}
%>

<script>
  new Validation("thisForm");
  setObjDisabled("sMarket2"); 

document.thisForm.departmentid.value="<%=departmentid%>"; 

 
<%
//客户归属部门
if(departmentid != null && !"".equals(departmentid)){
%>
	//setObjDisabled("departmentid");
<%	
}
%>


<%
	if(act.equals("update"))
	{
	%>

		//setObjDisabled("departid");
	//	setObjDisabled("vocationid");
	    document.getElementById("custcode").value = "<%=CHF.getXMLData(menuDetail,"custdepartid")%>";
	    document.thisForm.custdepartid.value="<%=CHF.getXMLData(menuDetail,"custdepartid")%>";
      	document.thisForm.departid.value="<%=CHF.getXMLData(menuDetail,"departid")%>";
      	document.thisForm.departEnName.value="<%=CHF.getXMLData(menuDetail,"departEnName")%>";
		document.thisForm.departname.value="<%=CHF.getXMLData(menuDetail,"departname")%>";
	//	document.thisForm.vocationid.value="<%=CHF.getXMLData(menuDetail,"vocationid")%>";
		document.thisForm.hylx.value="<%=CHF.getXMLData(menuDetail,"hylx")%>";
		document.thisForm.linkman.value="<%=CHF.getXMLData(menuDetail,"linkman")%>";
		document.thisForm.phone.value="<%=CHF.getXMLData(menuDetail,"phone")%>";
		document.thisForm.email.value="<%=CHF.getXMLData(menuDetail,"email")%>";
		document.thisForm.address.value="<%=CHF.getXMLData(menuDetail,"address")%>";
		document.thisForm.enterprisecode.value="<%=CHF.getXMLData(menuDetail,"enterprisecode")%>";
        document.thisForm.corporate.value="<%=CHF.getXMLData(menuDetail,"corporate")%>";
		document.thisForm.countrycess.value="<%=CHF.getXMLData(menuDetail,"countrycess")%>";
		document.thisForm.terracess.value="<%=CHF.getXMLData(menuDetail,"terracess")%>";
		document.thisForm.departdate.value="<%=CHF.getXMLData(menuDetail,"departdate")%>";
		document.thisForm.loginaddress.value="<%=CHF.getXMLData(menuDetail,"loginaddress")%>";
		document.thisForm.businessbegin.value="<%=CHF.getXMLData(menuDetail,"businessbegin")%>";
		document.thisForm.businessend.value="<%=CHF.getXMLData(menuDetail,"businessend")%>";        
		document.thisForm.businessbound.value="<%=CHF.getXMLData(menuDetail,"businessbound").replaceAll("\r\n","\\\\n").replaceAll("\n\r","\\\\n").replaceAll("\r","\\\\r").replaceAll("\n","\\\\n")%>";
		document.thisForm.remark.value="<%=CHF.getXMLData(menuDetail,"remark").replaceAll("\r\n","\\\\n").replaceAll("\n\r","\\\\n").replaceAll("\r","\\\\r").replaceAll("\n","\\\\n")%>";

		document.thisForm.register.value="<%=CHF.getXMLData(menuDetail,"register")%>";
		document.thisForm.BPR.value="<%=CHF.getXMLData(menuDetail,"BPR")%>";
//		document.thisForm.stockowner.value="<%=CHF.getXMLData(menuDetail,"stockowner").replaceAll("\r\n","\\\\n").replaceAll("\n\r","\\\\n").replaceAll("\r","\\\\r").replaceAll("\n","\\\\n")%>";

//		document.thisForm.stock.value = document.thisForm.stockowner.value;
		document.thisForm.postalcode.value="<%=CHF.getXMLData(menuDetail,"postalcode")%>";
		document.thisForm.fax.value="<%=CHF.getXMLData(menuDetail,"fax")%>";
		
		document.thisForm.taxpayer.value="<%=CHF.getXMLData(menuDetail,"taxpayer")%>";
		
		document.thisForm.practitioner.value="<%=CHF.getXMLData(menuDetail,"practitioner")%>";
		document.thisForm.fashion.value="<%=CHF.getXMLData(menuDetail,"fashion")%>";
		document.thisForm.calling.value="<%=CHF.getXMLData(menuDetail,"calling")%>";
		
		document.thisForm.estate.value="<%=CHF.getXMLData(menuDetail,"estate")%>";
		
		document.thisForm.approach.value="<%=CHF.getXMLData(menuDetail,"approach")%>";
		document.thisForm.mostly.value="<%=CHF.getXMLData(menuDetail,"mostly")%>";
		document.thisForm.subordination.value="<%=CHF.getXMLData(menuDetail,"subordination")%>";
		document.thisForm.groupname.value="<%=CHF.getXMLData(menuDetail,"groupname")%>";
		
		document.thisForm.departmentid.value="<%=CHF.getXMLData(menuDetail,"departmentid")%>";
		
		//单位曾用名
		document.thisForm.beforeName.value="<%=CHF.getXMLData(menuDetail,"beforeName")%>";
		//简称
		document.thisForm.customerShortName.value="<%=CHF.getXMLData(menuDetail,"customerShortName")%>";
		//组织机构性质 下拉
		document.thisForm.iframework.value="<%=CHF.getXMLData(menuDetail,"iframework")%>";
		//所属板块 下拉
		document.thisForm.plate.value="<%=CHF.getXMLData(menuDetail,"plate")%>";
		//介绍人姓名
		document.thisForm.intro.value="<%=CHF.getXMLData(menuDetail,"intro")%>";	
		//控股股东/上级公司
		document.thisForm.parentName.value="<%=CHF.getXMLData(menuDetail,"parentName")%>";	
		//控股方 holding
		document.thisForm.holding.value="<%=CHF.getXMLData(menuDetail,"holding")%>";	
		//公司性质 companyProperty 下拉
		document.thisForm.companyProperty.value="<%=CHF.getXMLData(menuDetail,"companyProperty")%>";
		document.thisForm.groupplate.value="<%=CHF.getXMLData(menuDetail,"groupplate")%>";
		document.thisForm.adored.value="ed";
		//后期所加字段
		document.thisForm.sMarket.value="<%=CHF.getXMLData(menuDetail,"sMarket")%>";
		document.thisForm.sockCode.value="<%=CHF.getXMLData(menuDetail,"sockCode")%>";
		document.thisForm.customerIeve.value="<%=CHF.getXMLData(menuDetail,"customerIeve")%>";
		document.thisForm.webSite.value="<%=CHF.getXMLData(menuDetail,"webSite")%>";
		document.thisForm.projectState.value="<%=CHF.getXMLData(menuDetail,"projectState")%>";
		document.thisForm.state.value="<%=CHF.getXMLData(menuDetail,"state")%>";
		
		//报备报告信息所加字段
		document.thisForm.iTmentName.value="<%=CHF.getXMLData(menuDetail,"iTmentName")%>";
		document.thisForm.agency.value="<%=CHF.getXMLData(menuDetail,"agency")%>";
		document.thisForm.aStateDate.value="<%=CHF.getXMLData(menuDetail,"aStateDate")%>";
		document.thisForm.busineLicense.value="<%=CHF.getXMLData(menuDetail,"busineLicense")%>";
		document.thisForm.bstateDate.value="<%=CHF.getXMLData(menuDetail,"bstateDate")%>";
		document.thisForm.directorName.value="<%=CHF.getXMLData(menuDetail,"directorName")%>";
		
		document.thisForm.directorPhone.value="<%=CHF.getXMLData(menuDetail,"directorPhone")%>";
		document.thisForm.dSecretary.value="<%=CHF.getXMLData(menuDetail,"dSecretary")%>";
		document.thisForm.secretaryPhone.value="<%=CHF.getXMLData(menuDetail,"secretaryPhone")%>";
		document.thisForm.ctaffQuantity.value="<%=CHF.getXMLData(menuDetail,"ctaffQuantity")%>";
		document.thisForm.sAccountant.value="<%=CHF.getXMLData(menuDetail,"sAccountant")%>";
		document.thisForm.fDirector.value="<%=CHF.getXMLData(menuDetail,"fDirector")%>";
		
		document.thisForm.accountanrPhone.value="<%=CHF.getXMLData(menuDetail,"accountanrPhone")%>";
		document.thisForm.fManager.value="<%=CHF.getXMLData(menuDetail,"fManager")%>";
		document.thisForm.fPhone.value="<%=CHF.getXMLData(menuDetail,"fPhone")%>";
		document.thisForm.stockStartDate.value="<%=CHF.getXMLData(menuDetail,"stockStartDate")%>";
		document.thisForm.stockListingDate.value="<%=CHF.getXMLData(menuDetail,"stockListingDate")%>";
		document.thisForm.pOfficeAddress.value="<%=CHF.getXMLData(menuDetail,"pOfficeAddress")%>";
		
		document.thisForm.cOfficeAddress.value="<%=CHF.getXMLData(menuDetail,"cOfficeAddress")%>";
		document.thisForm.fbusineDate.value="<%=CHF.getXMLData(menuDetail,"fbusineDate")%>";
		document.thisForm.isState.value="<%=CHF.getXMLData(menuDetail,"ischange")%>";
		
	
		
		document.thisForm.mergerQuantity.value="<%=CHF.getXMLData(menuDetail,"mergerQuantity")%>";
		document.thisForm.agoOffice.value="<%=CHF.getXMLData(menuDetail,"agoOffice")%>";
	
		document.thisForm.cReason.value="<%=CHF.getXMLData(menuDetail,"cReason").replaceAll("\r\n","\\\\n").replaceAll("\n\r","\\\\n").replaceAll("\r","\\\\r").replaceAll("\n","\\\\n")%>";
		document.thisForm.explain.value="<%=CHF.getXMLData(menuDetail,"explain").replaceAll("\r\n","\\\\n").replaceAll("\n\r","\\\\n").replaceAll("\r","\\\\r").replaceAll("\n","\\\\n")%>";
		 
		//2011-11-1
		document.thisForm.nation.value="<%=CHF.getXMLData(menuDetail,"nation")%>";
		document.thisForm.totalassets.value="<%=CHF.getXMLData(menuDetail,"totalassets")%>";
		document.thisForm.totalcurname.value="<%=CHF.getXMLData(menuDetail,"totalcurname")%>";
		
	<%
	}
	%>
	
//

function goAdEd() {	
	
	
	//if (!formSubmitCheck('thisForm')) return;
	if (!formSubmitCheck('tab1')) {
		mytab.setActiveTab(0);
		return;
	} else if (!formSubmitCheck('tab2')) {
		mytab.setActiveTab(1);
		return;
	} else if (!formSubmitCheck('tab3')) {
		mytab.setActiveTab(2);
		return;
	} else if (!formSubmitCheck('tab4')) {
		mytab.setActiveTab(3);
		return;
	}
	
	document.body.onbeforeunload = function () {};
    var custcode = document.getElementById("custcode").value;
    var custdepartid = document.getElementById("custdepartid").value;
    
    document.getElementById("isShowTip").value = true ;
    //change4();
    //change9();
    if(custcode!=custdepartid){
    	if(isNumberExistent()){
    		document.getElementById("custdepartid").value = "";
    		document.getElementById("custdepartid").select();
    	}    
    }
    
	var adored = document.getElementById("adored").value;
	//var departId = document.getElementById("departid").value;
	var checkCustomerId = "";
	if("${customer.departName}" != document.getElementById("departname").value){
		checkCustomerId = isExistent();
	}else{
		//alert(1);
		checkCustomerId = "ok";
	}
	//alert("|"+checkCustomerId+"|");
	//alert(departId);
	var flag = "no";
	
	//如果客户名不存在或者客户ID等于当前的客户ID
	//if(checkCustomerId.indexOf("ok") > -1 || checkCustomerId.indexOf(departId) > -1) {
	//	flag = "yes";
	//}
	if(checkCustomerId.indexOf("ok") > -1) {
		flag = "yes";
	}
	
	if(flag == "no") {
		alert("该客户名已经存在,请重新输入");
		//alert("该客户名已经存在,请重新输入");
		//document.getElementById("departname").value = "";
		document.getElementById("departname").select();
		document.getElementById("departname").focus();
		return false;
	}
	if(document.all.businessbegin.value != "长期经营"){
		if(document.all.businessbegin.value > document.all.businessend.value) {
			alert("[经营期限]开始日期不能大于结束日期");
			return false;
		}
	}
	
	if(document.all.businessbegin.value == "长期经营"){
		document.all.businessend.value = "";
	}
  
    
//    alert(names);
//    alert(values);
//    return;
	
	thisForm.submitStr.value =
		"<departname>"+thisForm.departname.value+"</departname>"+
		"<departEnName>"+thisForm.departEnName.value+"</departEnName>"+
		"<vocationid>" +thisForm.vocationid.value+"</vocationid>"+
		"<hylx>"+thisForm.hylx.value+"</hylx>"+
		"<linkman>"+thisForm.linkman.value+"</linkman>"+
		"<phone>"+thisForm.phone.value+"</phone>"+
		"<email>"+thisForm.email.value+"</email>"+
		"<address>"+thisForm.address.value+"</address>"+
		"<corporate>"+thisForm.corporate.value+"</corporate>"+
		"<countrycess>"+thisForm.countrycess.value+"</countrycess>"+
		"<terracess>"+thisForm.terracess.value+"</terracess>"+
		"<enterprisecode>"+thisForm.enterprisecode.value+"</enterprisecode>"+
		"<departdate>"+thisForm.departdate.value+"</departdate>"+
		"<loginaddress>"+thisForm.loginaddress.value+"</loginaddress>"+
		"<businessbegin>"+thisForm.businessbegin.value+"</businessbegin>"+
		"<businessend>"+thisForm.businessend.value+"</businessend>"+
		"<businessbound>"+thisForm.businessbound.value+"</businessbound>"+
		"<remark>"+thisForm.remark.value+"</remark>"+
		"<property>"+thisForm.property.value+"</property>"+
		"<register>"+thisForm.register.value+"</register>"+
		"<BPR>"+thisForm.BPR.value+"</BPR>"+
		"<postalcode>"+thisForm.postalcode.value+"</postalcode>"+
		"<fax>"+thisForm.fax.value+"</fax>"+
		"<taxpayer>"+thisForm.taxpayer.value+"</taxpayer>"+
		"<curname>"+thisForm.curname.value+"</curname>"+
		"<custdepartid>"+thisForm.custdepartid.value+"</custdepartid>"+
		"<practitioner>"+thisForm.practitioner.value+"</practitioner>"+
		"<fashion>"+thisForm.fashion.value+"</fashion>"+
		"<calling>"+thisForm.calling.value+"</calling>"+
		"<estate>"+thisForm.estate.value+"</estate>"+
		"<approach>"+thisForm.approach.value+"</approach>"+
		"<mostly>"+thisForm.mostly.value+"</mostly>"+
		"<subordination>"+thisForm.subordination.value+"</subordination>"+
		"<groupname>"+thisForm.groupname.value+"</groupname>"+
		"<departmentid>"+thisForm.departmentid.value+"</departmentid>"+
		
		"<companyProperty>"+thisForm.companyProperty.value+"</companyProperty>"+
		"<holding>"+thisForm.holding.value+"</holding>"+
		"<parentName>"+thisForm.parentName.value+"</parentName>"+
		"<intro>"+thisForm.intro.value+"</intro>"+	
		"<plate>"+thisForm.plate.value+"</plate>"+
		"<customerShortName>"+thisForm.customerShortName.value+"</customerShortName>"+
		"<iframework>"+thisForm.iframework.value+"</iframework>"+
		"<beforeName>"+thisForm.beforeName.value+"</beforeName>"+
		    //后期添加字段
       "<sMarket>"+thisForm.sMarket.value+"</sMarket>"+
       "<sockCode>"+thisForm.sockCode.value+"</sockCode>"+
       "<sockCode2>"+thisForm.sockCode2.value+"</sockCode2>"+
       "<customerIeve>"+thisForm.customerIeve.value+"</customerIeve>"+
       "<webSite>"+thisForm.webSite.value+"</webSite>"+
       "<projectState>"+thisForm.projectState.value+"</projectState>"+
       "<state>"+thisForm.state.value+"</state>"+
       
	    //后期添加的报备报告信息字段
       "<iTmentName>"+thisForm.iTmentName.value+"</iTmentName>"+
       "<agency>"+thisForm.agency.value+"</agency>"+
       "<aStateDate>"+thisForm.aStateDate.value+"</aStateDate>"+
       "<busineLicense>"+thisForm.busineLicense.value+"</busineLicense>"+
       "<bstateDate>"+thisForm.bstateDate.value+"</bstateDate>"+
       "<directorName>"+thisForm.directorName.value+"</directorName>"+
       
       "<directorPhone>"+thisForm.directorPhone.value+"</directorPhone>"+
       "<dSecretary>"+thisForm.dSecretary.value+"</dSecretary>"+
       "<secretaryPhone>"+thisForm.secretaryPhone.value+"</secretaryPhone>"+
       "<ctaffQuantity>"+thisForm.ctaffQuantity.value+"</ctaffQuantity>"+
       "<sAccountant>"+thisForm.sAccountant.value+"</sAccountant>"+
       "<fDirector>"+thisForm.fDirector.value+"</fDirector>"+
       
       "<accountanrPhone>"+thisForm.accountanrPhone.value+"</accountanrPhone>"+
       "<fManager>"+thisForm.fManager.value+"</fManager>"+
       "<fPhone>"+thisForm.fPhone.value+"</fPhone>"+
       "<stockStartDate>"+thisForm.stockStartDate.value+"</stockStartDate>"+
       "<stockListingDate>"+thisForm.stockListingDate.value+"</stockListingDate>"+
       "<pOfficeAddress>"+thisForm.pOfficeAddress.value+"</pOfficeAddress>"+
       
       "<cOfficeAddress>"+thisForm.cOfficeAddress.value+"</cOfficeAddress>"+
       "<fbusineDate>"+thisForm.fbusineDate.value+"</fbusineDate>"+
       "<ischange>"+thisForm.ischange.value+"</ischange>"+
       "<explain>"+thisForm.explain.value+"</explain>"+
       "<mergerQuantity>"+thisForm.mergerQuantity.value+"</mergerQuantity>"+
       "<agoOffice>"+thisForm.agoOffice.value+"</agoOffice>"+
       "<groupplate>"+thisForm.groupplate.value+"</groupplate>"+
       
       "<nation>"+thisForm.nation.value+"</nation>"+
       "<totalassets>"+thisForm.totalassets.value+"</totalassets>"+
       "<totalcurname>"+thisForm.totalcurname.value+"</totalcurname>"+
       
      "<cReason>"+thisForm.cReason.value+"</cReason>";
    	
    //alert(thisForm.submitStr.value);
    	
    if(document.getElementById("setdef").value=="1"){

	    var defname = new Array();
	    var defvalue = new Array();
	    defname = document.getElementsByName("defName");
	    defvalue = document.getElementsByName("defValue");
	    var names="";
	    for(i=0;i<defname.length;i++){
	        if(defname[i].value!=""){
	        	names+=defname[i].value+"-";
	        }else{
	        	names+=+"?"+"-";
	        }
	    	
	    }
	    var values="";
	    for(i=0;i<defvalue.length;i++){
	    	if(defvalue[i].value!=""){
	    		values+=defvalue[i].value+"-";
	    	}else{
	    		values+=+"?"+"-";
	    	}
	    	
	    }
	    
	    document.getElementById("commondefNames").value=names;
	    document.getElementById("commondefValues").value=values;  
          
    } 
//  var xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
//	xmlHttp.open("POST", "/AuditSystem/customer.do?method=addAndEdit&names="+document.getElementById("commondefNames").value+"&values="+document.getElementById("commondefValues").value+"&UserDefTbodyCount="+document.getElementById("UserDefTbodyCount").value, false);
//	xmlHttp.send();
	
//	alert(document.getElementById("UserDefTbodyCount").value);
//	return;
	 
	 if('<%=method %>'!=document.getElementById("vocationid").value){
	 	document.getElementById("isdifferent").value="different";
	 }
	 
	 if('<%=act%>'=='update'){
	 	if(document.getElementById("isdifferent").value=="different"){
	 		if(confirm("警告！您确定要修改会计制度类型吗？这将会影响科目对照！\n\n如果确定，请修改科目对照！")){
	 			document.thisForm.action="customer.do?method=addAndEdit&&act=<%=act%>";
	 		}
		 	else{
		 		return false;
		 	}
	 	}
	 } 
	if(window.parent.name=="rightFrame"){
		document.thisForm.action="customer.do?method=addAndEdit&flag=${param.flag}&act=<%=act%>&towhere=2&frameTree=${frameTree}";
	}else{
		document.thisForm.action="customer.do?method=addAndEdit&flag=${param.flag}&act=<%=act%>&towhere=1&frameTree=${frameTree}";
	}
	document.thisForm.submit() ;
	return true;
}

//如果不是框架页
try{
if (top.location == self.location) {
	document.all.back.value = "关  闭";
}
}catch(e){}
//如果是框架页则返回上一页,否则就关闭窗口
function goClose() {
	if (top.location == self.location) {
		window.close();
	} else {
		window.history.back();
	}
}

function openManager(){
	var departid = document.thisForm.departid.value;
	var url = "${pageContext.request.contextPath}/manager.do?method=edit&departid="+departid;
	window.open(url,'',"width=860,height="+window.screen.height*2/3+",top="+window.screen.height/7+",left="+(window.screen.width-860)/2+",scrollbars=no");
}

function openStockholder(){
	var register = document.getElementById("register");
	var curname = document.getElementById("curname");
	if(register.value == ""){
		alert("请先完成注册资本！");
		register.focus();
		return false;
	}
	if(curname.value == ""){
		alert("请先完成货币类型！");
		curname.focus();
		return false;
	}
	
	if(!isCurrency(register.value)){
		alert("注册资本必须为货币格式!");
		register.select();
		return false;
	}
	var departid = document.thisForm.departid.value;
	var url = "${pageContext.request.contextPath}/stockholder.do?method=edit&departid="+departid;
	window.open(url, '', "width=660,height=500,top=100,left="+(window.screen.width-660)/2); 
}

//检查客户名是否已经存在
//存在返回客户ID，不存在返回OK
function isExistent() {
	var xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	xmlHttp.open("POST", "${pageContext.request.contextPath}/customer.do?method=checkCustomer&&customerName=" + thisForm.departname.value + "&random=" + Math.random(), false);
	xmlHttp.send();
	var strResult = unescape(xmlHttp.responseText);
	return strResult;
}

//检查单位编号是否已经存在
//存在返回NO，不存在返回OK
function isNumberExistent() {
	var xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	xmlHttp.open("POST", "${pageContext.request.contextPath}/customer.do?method=checkCustomerNumber&customerNumber=" + thisForm.custdepartid.value + "&random=" + Math.random(), false);
	xmlHttp.send();
	var strResult = unescape(xmlHttp.responseText);
	
	if(strResult=="no"){
		alert("该单位编号已存在，请重新输入");
		return true;
	}
}

function isExist(){
	var departId = document.getElementById("departid").value;
	var departname111 = document.getElementById("departname111");
	var departname = document.getElementById("departname");
	if(departname.value == ""){
		departname111.style.display = "none";
	}else{
		if("${customer.departName}" ==departname.value){
			return ;
		}
		var checkCustomerId = isExistent();
		departname111.style.display = "";
		if(checkCustomerId.indexOf("ok") > -1) {
			departname111.innerHTML = "<font color='blue'>此单位名称可用</font>";
		}else{
			departname111.innerHTML = "<font color='red'>此单位名称已存在</font>";
			document.getElementById("departname").select();
		}
	}
}
//==================
//添加关联公司
//==================
function setconnectcompanys(){

	var url = "${pageContext.request.contextPath}/connectcompanys.do?acts=${act}&&chooseCustomer=" + document.getElementById("departid").value;
	window.open(url);
	
//	window.open(url,'',"width="+window.screen.width+",height="+window.screen.height+",scrollbars=yes");
	
//	window.showModalDialog("${pageContext.request.contextPath}/connectcompanys.do?chooseCustomer=" + document.getElementById("chooseCustomer").value+"&math="+Math.random(),window,"dialogWidth:640px;dialogHeight:480px;status=0;help=0;scroll=1;")  
}

//if(${act!='update'}){
//		document.getElementById("addconnectcompanys").disabled="disabled";
//		document.getElementById("manager").disabled="disabled";
//		document.getElementById("stockholder").disabled="disabled";
//		tdID1.innerHTML=tdID1.innerHTML+"(选择修改才能设置)";		
//		tdID2.innerHTML=tdID2.innerHTML+"(选择修改才能设置)";	
//		tdID3.innerHTML=tdID3.innerHTML+"(选择修改才能设置)";	
//	}

//try{
//	if(window.parent.name=="rightFrame"){
//		document.getElementById("opSave").style.display = "none";
//		document.getElementById("back").style.display = "none";	
//	}else{
//		
//	}
//}catc


/* 
if(document.thisForm.estate.value=='正式'){

	document.thisForm.change3[0].checked = true
	
}else if(document.thisForm.estate.value=='潜在'){

	document.thisForm.change3[1].checked = true
	
}
else if(document.thisForm.estate.value=='意向'){

	document.thisForm.change3[2].checked = true
	
}
else if(document.thisForm.estate.value=='曾代理'){

	document.thisForm.change3[3].checked = true
	
}else{

	document.thisForm.change3[4].checked = true
} */
function change4(){
	var a1 = document.thisForm.change3[0].checked 
	var a2 = document.thisForm.change3[1].checked 
	var a3 = document.thisForm.change3[2].checked 
	var a4 = document.thisForm.change3[3].checked 
	
	if(a1){
		document.getElementById("estate").value = '正式';
	}else if(a2){
		document.getElementById("estate").value = '潜在';
	
	}else if(a3){
		document.getElementById("estate").value = '意向';
	}else if(a4){
		document.getElementById("estate").value = '曾代理';
	}else{
		document.getElementById("estate").value = '其他';
	}
}







/*
if(document.thisForm.approach.value=='项目提供'){

	document.thisForm.change4[0].checked = true
	
}else if(document.thisForm.approach.value=='培训名单'){

	document.thisForm.change4[1].checked = true
	
}else if(document.thisForm.approach.value=='登记注册资料'){

	document.thisForm.change4[2].checked = true
	
}else if(document.thisForm.approach.value=='网上查询'){

	document.thisForm.change4[3].checked = true
	
}else if(document.thisForm.approach.value=='受理电话'){

	document.thisForm.change4[4].checked = true
	
}else if(document.thisForm.approach.value=='客户网站'){

	document.thisForm.change4[5].checked = true
	
}else {
	document.thisForm.change4[6].checked = true
}
*/
function change9(){
	var a1 = document.thisForm.change4[0].checked 
	var a2 = document.thisForm.change4[1].checked 
	var a3 = document.thisForm.change4[2].checked 
	var a4 = document.thisForm.change4[3].checked 
	var a5 = document.thisForm.change4[4].checked 
	var a6 = document.thisForm.change4[5].checked 
	var a7 = document.thisForm.change4[6].checked 

	
	if(a1){
		document.getElementById("approach").value = '项目提供';
	}else if(a2){
		document.getElementById("approach").value = '培训名单';
	
	}else if(a3){
		document.getElementById("approach").value = '登记注册资料';
	}else if(a4){
		document.getElementById("approach").value = '网上查询';
		
	}else if(a5){
		document.getElementById("approach").value = '受理电话';
	
	}else if(a6){
		document.getElementById("approach").value = '客户网站';
		
	}else{
		document.getElementById("approach").value = '其他';
	}
}

function showdescription(hyId) {
	var xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	xmlHttp.open("POST", "${pageContext.request.contextPath}/customer.do?method=getDescription&autoId=" + hyId, false);
	xmlHttp.send();
	var strResult = unescape(xmlHttp.responseText);
	
	document.getElementById("TRDescription").style.display = "";
	document.getElementById("hyDescription").innerText =  strResult;
}


isValue=document.getElementById("isState").value; 
if(isValue!=""){ //非等于“” 就是修改页面
	 isState=document.getElementById("isState").value;      
	 var stateYes=document.getElementById("ischangeYES");           //是
	 var stateNO=document.getElementById("ischangeNO");             //否
	 if(isState=="是"){    		
		 stateYes.checked=true;
	 }else{    	
		 stateNO.checked=true;
		 
	 }
}

function company(obj){
	//alert(obj.value);
	if(obj.value.indexOf("上市公司")>-1){
		//股票板块 证券市场 股票代码
		document.getElementById("plate").className = "required";
		document.getElementById("sMarket").className = "required";
		document.getElementById("sockCode").className = "required";
		document.getElementById("id1").style.display = "";
		document.getElementById("id2").style.display = "";
		document.getElementById("id3").style.display = "";
	}else{
		document.getElementById("plate").className = "";
		document.getElementById("sMarket").className = "";
		document.getElementById("sockCode").className = "";
		document.getElementById("id1").style.display = "none";
		document.getElementById("id2").style.display = "none";
		document.getElementById("id3").style.display = "none";
	}
}

</script> 

</html>

