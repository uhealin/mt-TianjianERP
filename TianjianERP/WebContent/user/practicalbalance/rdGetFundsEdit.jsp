<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<%@page import="com.matech.framework.listener.UserSession"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<style type="text/css">

	fieldset {margin: 10px;}
	#tTable {margin-top:10px;border:#d7e2f3 1px solid;border-collapse:collapse;}
	#tTable td,th {
		padding: 5 5 5 10px;text-align: left;white-space:nowrap;border-top:#d7e2f3 1px solid;border-left: #d7e2f3 1px solid;height:30px;
	}
	#tTable th{background-color: #f8f9f9;}
	#tTable input {border:0px;border-bottom:1px solid #aaa;}
</style>
 
<script type="text/javascript">


Ext.onReady(function (){
	new Ext.Toolbar({
			renderTo: "divBtn",
			height:30,
			defaults: {autoHeight: true,autoWidth:true},
	       items:[{ 
	           text:'保存',
	           icon:'${pageContext.request.contextPath}/img/save.gif' ,
	           handler:function(){
	        	   if (!formSubmitCheck('thisForm')) return;
					  save();
				   }
	     	 },'-',{ 
	        text:'返回',
	        icon:'${pageContext.request.contextPath}/img/back.gif', 
	        handler:function(){
				window.history.back();
			}
	  	},'->']
	});
	
	
	
	//初始化日期控件
	new Ext.form.DateField({
		applyTo : 'receicedate',
		width: 133,
		format: 'Y-m-d'
	});
	
	 
});
</script>
</head>
<body>
<div id="divBtn" ></div>
<div style="height:expression(document.body.clientHeight-27);width:100%;overflow: auto;">
<form name="thisForm" method="post" action="" id="thisForm">

	<fieldset>
		<legend>收款登记表</legend>
		<table cellpadding="1" align="center" cellspacing="1" width="100%" height="100%" id="tTable">
		
			<tr>
				
				<td align="right">项目名称<span class="mustSpan">[*]</span>：</td>
				<td align=left>
					<input type="hidden" name="userLoginId" id="userLoginId" value="${userSession.userId }" >
					<input  type="text" id="projectid" name="projectid"  
							refer="userLoginId" size="25" onkeydown="onKeyDownEvent();" 
							onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"  
							class="required"  autoid=139 autoWidth="450"
							onChange = "getMoney(this.value);"  value= "${getFunds.projectid}"
							/>
				</td>
				
				<th width="150" align="left">业务费用 ：</th>
				<td align="left">
					<input type="text" 
						   name="businessCost" 
						   id="businessCost"
						   maxlength="50" 
						   value = "${price}"
						   readonly = "readonly" 
						   size="30" 
						   value="" style="background-color: #f8f9f9; text-align:right" />&nbsp;元
				</td>
			</tr>
			
			<tr>
				<th width="150" align="right">收款日期：</th>
				<td>
					<input type="text" 
						   name="receicedate" 
						   id="receicedate"
						   maxlength="50" 
						   size="30"
						   value="${getFunds.receicedate}"  />
				</td>
				
				
				<th width="150" align="left">已收款金额 ：</th>
				<td align="left">
					<input type="text" 
						   name="nmoney" 
						   id="nmoney"
						   maxlength="50" 
						   size="30" 
						   value = "${receiceMoney}"
						   readonly = "readonly"
						   value="" style="background-color: #f8f9f9;text-align:right" />&nbsp;元
				</td>
			</tr>
			
			<tr>
				
				<th width="150" align="right">待收款金额 ：</th>
				<td>
					<input type="text" 
						   name="ymoney" 
						   id="ymoney"
						   maxlength="50" 
						   size="30"
						   readonly = "readonly" 
						   value="" style="background-color: #f8f9f9;text-align:right;" />&nbsp;元
				</td>
				
				<th width="150" align="right" rowspan="1">收款形式<span class="mustSpan">[*]</span>：</th>
				<td rowspan="1">
					<input type="text"
					   name="ctype"
					   id="ctype"
					   maxlength="10"
					   value="${getFunds.ctype}";
					   title="请输入有效的值"
					   onkeydown="onKeyDownEvent();"
					   onkeyup="onKeyUpEvent();"
					   onclick="onPopDivClick(this);"
					   valuemustexist=true
					   size="25"
					   autoid=4573 /> 
					 	--<input type="text"
						   name="ctypenumber"
						   id="ctypenumber"
						   value="${getFunds.ctypenumber}"
						   maxlength="50" size="15" style="background-color: #f8f9f9"  /> 
				</td>
			</tr>
			
			<tr>
				
				<th width="150" align="right">收款金额<span class="mustSpan">[*]</span>：</th>
				<td>
					<input type="text" 
						   name="receiceMoney" 
						   id="receiceMoney"
						   maxlength="50" 
						   size="30"
						   value="${getFunds.receiceMoney}"
						   value="" style="text-align:right;" />元
				</td>
				
				<th width="150" align="right" rowspan="1">备注：</th>
				<td rowspan="1">
					<textarea id="remark" name="remark" rows="3" cols="45" >${getFunds.remark }</textarea>
				</td>
			</tr>
			
			<tr>
				
				<th width="150" align="right" rowspan="1">&nbsp;</th>
				<td rowspan="1">
					&nbsp;
				</td>
				
				<th width="150" align="right">&nbsp;</th>
				<td>
					&nbsp;		
				</td>
			</tr>
			
			 
		</table>
	</fieldset>

<input type="hidden" id="skmoney" name="skmoney" value="${getFunds.receiceMoney}">
<input type="hidden" id="autoid" name="autoid" value="${getFunds.autoid}">
</form>

</div>
</body>

<script type="text/javascript">
	new Validation("thisForm");
	var autoid = document.getElementById("autoid").value;
	// 新增
	if(autoid=="" || autoid==null){
		// 默认收款时间为当前时间
		var now = new Date();
		var year = now.getYear();
		var month = now.getMonth()+1;
		var date = now.getDate();
		document.getElementById("receicedate").value=year+"-"+month+"-"+date;
		// 金额 默认显示 0
		document.getElementById("ymoney").value="0";
		document.getElementById("nmoney").value="0";
		document.getElementById("receicemoney").value="0";
	}else{
		document.getElementById("ymoney").value=document.getElementById("businessCost").value*1 -document.getElementById("nmoney").value*1;
		document.getElementById("projectid").readOnly = "readOnly" ;
		
	}
	
	// 保存
	function save() {
		// 待收款金额
		var ymoney = document.getElementById("ymoney").value;
		// 收款金额
		var receiceMoney = document.getElementById("receiceMoney").value;
		if(receiceMoney*1<0){
			alert("收款金额不能小于0");
			return;
		}
		if(receiceMoney*1>ymoney*1){
			alert("收款金额大于待收款金额,最多只能收款:"+ymoney);
			return;
		}else{	
			if(autoid=="" || autoid==null){
				document.thisForm.action = "${pageContext.request.contextPath}/getFunds.do?method=add" ;
				document.thisForm.submit();
			}else{
				document.thisForm.action = "${pageContext.request.contextPath}/getFunds.do?method=update" ;
				document.thisForm.submit();
			}
		}
	}
	
	function getMoney(projectid) {
		var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		var url = "${pageContext.request.contextPath}/getFunds.do?method=getMoney&projectid="+projectid;
		oBao.open("post",url,false);
		oBao.send();
		
		var strResult = oBao.responseText;
		var obj = eval('('+strResult+')'); 
		
		document.getElementById("nmoney").value = obj.receiceMoney ;
		document.getElementById("businessCost").value = obj.price ;
		
		document.getElementById("ymoney").value= obj.price*1 -obj.receiceMoney*1;
	}
	

</script>
</html>