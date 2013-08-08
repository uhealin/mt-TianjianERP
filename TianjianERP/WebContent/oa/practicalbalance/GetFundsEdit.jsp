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
				<th width="150" style="text-align: right;">项目名称<span class="mustSpan">[*]</span>：</th>
				<td align="left">
					<input type="text"
					   name="projectid"
					   id="projectid"
					   maxlength="10"
					   value="${getFunds.projectid }";
					   title="请输入有效的值"
					   onclick="showmodeldialog();"
					   onchange="setProjectInfo(this);"
					   class="required"
					   size="30" /> <span id="pname">${projectName}</span>
				</td>
				
				<th width="150" style="text-align: right;">业&nbsp;务&nbsp;费&nbsp;用：</th>
				<td align="left">
					<input type="text" 
						   name="businessCost" 
						   id="businessCost"
						   maxlength="50" 
						   size="30" 
						   value="" readonly="readonly" style="background-color: #f8f9f9;text-align:right" />&nbsp;元
				</td>
			</tr>
			
			<tr>
				<th width="150" style="text-align: right;">付&nbsp;款&nbsp;单&nbsp;位：</th>
				<td>
					<input type="text"
					   name="customerId"
					   id="customerId"
					   value=""
					   size="30" readonly="readonly" style="background-color: #f8f9f9" /> 
				</td>
				
				
				<th width="150" style="text-align: right;">已收款金额：</th>
				<td align="left">
					<input type="text" 
						   name="nmoney" 
						   id="nmoney"
						   maxlength="50" 
						   size="30" 
						   value="" readonly="readonly" style="background-color: #f8f9f9;text-align:right" />&nbsp;元
				</td>
			</tr>
			
			<tr>
				<th width="150" style="text-align: right;">委&nbsp;&nbsp;&nbsp;托&nbsp;&nbsp;号：</th>
				<td>
					<input type="text"
					   name="entrustNumber"
					   id="entrustNumber"
					   value=""
					   size="30" readonly="readonly" style="background-color: #f8f9f9" /> 
				</td>
				
				
				<th width="150" style="text-align: right;">待收款金额：</th>
				<td>
					<input type="text" 
						   name="ymoney" 
						   id="ymoney"
						   maxlength="50" 
						   size="30"
						   value="" readonly="readonly" style="background-color: #f8f9f9;text-align:right;" />&nbsp;元
				</td>
			</tr>
			
			<tr>
				<th width="150" style="text-align: right;">报&nbsp;告&nbsp;文&nbsp;号：</th>
				<td>
					<input type="text"
					   name="reportNumber"
					   id="reportNumber"
					   value=""
					   maxlength="20"
					   size="30"
					   readonly="readonly" style="background-color: #f8f9f9" />
				</td>
				
				
				<th width="150" style="text-align: right;">收&nbsp;款&nbsp;金&nbsp;额：</th>
				<td>
					<input type="text" 
						   name="receiceMoney" 
						   id="receiceMoney"
						   maxlength="50" 
						   size="30"
						   value="${getFunds.receiceMoney}" style="text-align:right;" onfocus="f_zero()" onblur="f_money()" class="number"/>&nbsp;&nbsp;<span class="mustSpan">！</span>元
				</td>
			</tr>
			
			<tr>
				<th width="150" style="text-align: right;">承&nbsp;接&nbsp;部&nbsp;门：</th>
				<td>
					<input type="text"
						   name="departname"
						   id="departname"
						   value=""
						   maxlength="50" size="30" readonly="readonly" style="background-color: #f8f9f9"  />
				</td>
				
				<th width="150" style="text-align: right;">收&nbsp;款&nbsp;日&nbsp;期：</th>
				<td>
					<input type="text" 
						   name="receicedate" 
						   id="receicedate"
						   maxlength="50" 
						   size="30"
						   value="${getFunds.receicedate}"  />
				</td>
			</tr>
			
			<tr>
				<th width="150" style="text-align: right;" rowspan="1">账&nbsp;面&nbsp;分&nbsp;类：</th>
				<td rowspan="1">
					<input type="text"
					   name="accounttype"
					   id="accounttype"
					   maxlength="20"
					   value="${getFunds.accounttype}";
					   title="请输入有效的值"
					   onkeydown="onKeyDownEvent();"
					   onkeyup="onKeyUpEvent();"
					   onclick="onPopDivClick(this);"
					   size="25"
					   autoid=4574 />  <span class="mustSpan">！</span> 
				</td>
				<th width="150" style="text-align: right;" rowspan="2">备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注：</th>
				<td rowspan="2">
					<textarea id="remark" name="remark" rows="4" cols="45" >${getFunds.remark }</textarea><span class="mustSpan">！</span>
				</td>
			</tr>
			<tr>
				<th width="150" style="text-align: right;" rowspan="1">收&nbsp;款&nbsp;形&nbsp;式：</th>
				<td rowspan="1">
					<input type="text"
					   name="ctype"
					   id="ctype"
					   maxlength="20"
					   value="${getFunds.ctype}";
					   title="请输入有效的值"
					   onkeydown="onKeyDownEvent();"
					   onkeyup="onKeyUpEvent();"
					   onclick="onPopDivClick(this);"
					   size="25"
					   autoid=4573 /> 
					 --<input type="text"
						   name="ctypenumber"
						   id="ctypenumber"
						   value="${getFunds.ctypenumber}"
						   maxlength="50" size="15" style="background-color: #f8f9f9"  /><span class="mustSpan">！</span> 
				</td>
			</tr>
			<tr>
				<th width="150" style="text-align: right">企业是否具有证劵业务：</th>
				<td colspan="3">
					<input type="text" 
						   name="isStock" 
						   id="isStock"
						   maxlength="50"
						   size="30"
						   value="" readonly="readonly" /> 
				</td>
			</tr>
			 
		</table>
	</fieldset>

<input type="hidden" id="skmoney" name="skmoney" value="${getFunds.receiceMoney}">
<input type="hidden" id="autoid" name="autoid" value="${getFunds.autoid}">
</form>

</div>
<iframe name="groupFrame" id="groupFrame" scrolling="no" frameborder="0" width="100%" height="100%" src=""></iframe>
</body>

<script type="text/javascript">

	
	// var autoid = document.getElementById("autoid").value;
	var autoid = "${getFunds.autoid}";
	
	// 新增
	if(autoid=="" || autoid==null){
		// 默认收款时间为当前时间
		var now = new Date();
		var year = now.getYear();
		var month = now.getMonth()+1;
		if(month*1<10){
			month = "0"+month;
		}
		var date = now.getDate();
		if(date*1<10){
			date = "0"+date;
		}
		
		// 金额 默认显示 0
		document.getElementById("ymoney").value="0";
		document.getElementById("nmoney").value="0";
		document.getElementById("receicemoney").value="0";
		document.getElementById("receicedate").value=year+"-"+month+"-"+date;
	}else{
		// 设置项目信息
		var projectid = "${getFunds.projectid}";
		var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		var url = "${pageContext.request.contextPath}/getFunds.do?method=getProjectInfoJson&projectid="+projectid;
		oBao.open("post",url,false);
		oBao.send();
		var strResult = oBao.responseText;
	
		var obj = eval('('+strResult+')'); 
		
		var imoney = "${getFunds.receiceMoney}" ;
		document.getElementById("customerId").value=obj[0].customername;
		document.getElementById("entrustNumber").value=obj[0].entrustNumber;
		document.getElementById("reportNumber").value=obj[0].reportNumber;
		document.getElementById("departname").value=obj[0].departname;
		document.getElementById("businessCost").value=obj[0].businessCost;
		document.getElementById("nmoney").value=accSub(imoney*1,obj[0].getFundsMoney*1);
		document.getElementById("ymoney").value= accSub(accSub(imoney*1,obj[0].getFundsMoney*1),obj[0].businessCost*1) ;
		document.getElementById("isStock").value=obj[0].isStock; 
	}
	
		

	new Validation("thisForm");
	
	// 设置项目信息
	function setProjectInfo(t){
		var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		var url = "${pageContext.request.contextPath}/getFunds.do?method=getProjectInfoJson&projectid="+t.value;
		oBao.open("post",url,false);
		oBao.send();
		var strResult = oBao.responseText;

		var obj = eval('('+strResult+')'); 

		document.getElementById("customerId").value=obj[0].customername;
		document.getElementById("entrustNumber").value=obj[0].entrustNumber;
		document.getElementById("reportNumber").value=obj[0].reportNumber;
		document.getElementById("departname").value=obj[0].departname;
		document.getElementById("businessCost").value=obj[0].businessCost;
		document.getElementById("nmoney").value=obj[0].getFundsMoney*1;
		document.getElementById("ymoney").value=accSub(obj[0].getFundsMoney*1,obj[0].businessCost*1);
		document.getElementById("isStock").value=obj[0].isStock; 
		
	}
	
	//减法函数
	function accSub(arg1,arg2){
	     var r1,r2,m,n;
	     try{r1=arg1.toString().split(".")[1].length}catch(e){r1=0}
	     try{r2=arg2.toString().split(".")[1].length}catch(e){r2=0}
	     m=Math.pow(10,Math.max(r1,r2));
	     //last modify by deeka
	     //动态控制精度长度
	     n=(r1>=r2)?r1:r2;
	     return ((arg2*m-arg1*m)/m).toFixed(n);
	}
	

	// 保存
	function save() {
		// 待收款金额
		var ymoney = document.getElementById("ymoney").value;
		// 收款金额
		var receiceMoney = document.getElementById("receiceMoney").value;
		if(receiceMoney*1<=0){
			alert("收款金额不能小于等于0"); 
			return;
		}
		if(receiceMoney*1>ymoney*1){
			alert("收款金额大于待收款金额,最多只能收款:"+ymoney);
			return;
		}else{	
			// 防止网络慢 用户点击 多次保存
			showWaiting();
			
			if(autoid=="" || autoid==null){
				document.thisForm.action = "${pageContext.request.contextPath}/getFunds.do?method=add" ;
				document.thisForm.submit();
				new BlockDiv().show();
			}else{
				document.thisForm.action = "${pageContext.request.contextPath}/getFunds.do?method=update" ;
				document.thisForm.submit();
				new BlockDiv().show();
			}
		}
	}


 
	

	var projectWin = null;
	function showmodeldialog(){
		isSave = false;
		var url="${pageContext.request.contextPath}/getFunds.do?method=list2" ;
			
		if(!projectWin) { 
			projectWin = new Ext.Window({
		     	renderTo : Ext.getBody(),
		     	width: 800,
		     	width: 800,
		     	id:'projectWin',
		     	height:420,
		     	title:'请选择项目',
		     	closable:'flase',
	        	closeAction:'hide',
	       	    listeners : {
		         	'hide':{
	
	         		fn: function () {
		         			new BlockDiv().hidden();
		         			projectWin.hide();
						}
					}
		        },
		       	html:'<iframe name="groupFrame" id="groupFrame" scrolling="no" frameborder="0" width="100%" height="100%" src="' + url + '"></iframe>',
	        	layout:'fit'
		    });
	    } else {
	    	document.getElementById("groupFrame").src = url;
	    }
		
		new BlockDiv().show();
		projectWin.show();
 
	}


	function showmodeldialog2(){
		var str = window.showModalDialog("${pageContext.request.contextPath}/getFunds.do?method=list2"," ","dialogWidth:800px;dialogHeight:500px;help:no;status:no;" );
		if(str != null && str != ""){
			var strs = str.split("$");
			document.getElementById("projectid").value =  strs[0];
			document.getElementById("pname").innerHTML =  strs[1];
	
			// 设置项目信息
			var projectid = strs[0];
			var oBao = new ActiveXObject("Microsoft.XMLHTTP");
			var url = "${pageContext.request.contextPath}/getFunds.do?method=getProjectInfoJson&projectid="+projectid;
			oBao.open("post",url,false);
			oBao.send();
			var strResult = oBao.responseText;
	
			var obj = eval('('+strResult+')'); 
	
			document.getElementById("customerId").value=obj[0].customername;
			document.getElementById("entrustNumber").value=obj[0].entrustNumber;
			document.getElementById("reportNumber").value=obj[0].reportNumber;
			document.getElementById("departname").value=obj[0].departname;
			document.getElementById("businessCost").value=obj[0].businessCost;
			document.getElementById("nmoney").value=obj[0].getFundsMoney*1;
			document.getElementById("ymoney").value=obj[0].businessCost*1 - obj[0].getFundsMoney*1;
			document.getElementById("receiceMoney").value=0;
		}
	}



	// 处理金额 
	function f_zero(){
		if(document.getElementById("receiceMoney").value==0){
			document.getElementById("receiceMoney").value="";
		}
	}
	
	// 验证金额 
	function f_money(){
		var receiceMoney = document.getElementById("receiceMoney").value;
		if(receiceMoney==""){
			document.getElementById("receiceMoney").value=0;
			return;
		}
		
		if(/^(?!(0[0-9]{0,}$))[0-9]{1,}[.]{0,}[0-9]{0,}$/.test(receiceMoney)==false)
		{
			alert('非法金额，请重新录入金额 !');
			document.getElementById("receiceMoney").select();
		}
	}
	
</script>
</html>