<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<style type="text/css">

	fieldset {margin: 10px;}
	.tTable {margin-top:10px;border:#d7e2f3 1px solid;border-collapse:collapse;}
	.tTable td,th {
		padding: 5 5 5 2px;text-align: left;white-space:nowrap;border-top:#d7e2f3 1px solid;border-left: #d7e2f3 1px solid;height:30px;
	}
	.tTable th{background-color: #f8f9f9;}
	.tTable input {border:1px solid #d7e2f3;}
</style>
 
<script type="text/javascript">


Ext.onReady(function (){
	new Ext.Toolbar({ 
			renderTo: "divBtn",
			height:30,
			defaults: {autoHeight: true,autoWidth:true},
	        items:[
	        	 { 
		           text:'保存',
		           icon:'${pageContext.request.contextPath}/img/save.gif' ,
		           handler:function(){
	        	   	  if (!formSubmitCheck('thisForm')) return;
					  save('save');
				   }
			     },'-',
			     /*{ 
		           text:'保存并开票',
		           icon:'${pageContext.request.contextPath}/img/confirm.gif' ,
		           handler:function(){
	        	   	  if (!formSubmitCheck('thisForm')) return;
					  save('saveAndInvoice');
				   }
			     },'-',*/
			     { 
			        text:'返回',
			        icon:'${pageContext.request.contextPath}/img/back.gif',  
			        handler:function(){
						window.history.back();
					}
	  			 },'->'
	  	 	]
	});

});
</script>
</head>
<body>
<div id="divBtn"></div>
<div style="height:expression(document.body.clientHeight-57);width:100%;overflow: auto">
<form name="thisForm" method="post" action="" id="thisForm">

	<fieldset>
		<legend>收款登记表</legend>
		<table cellpadding="1" align="center" cellspacing="1" width="100%" height="100%" class="tTable">
		
			<tr>
				<th width="150" style="text-align: right;">开票单位名称<span class="mustSpan">[*]</span>：</th>
				<td align="left" colspan="3">
					<input type="hidden" id="myinvoicenumber" name="myinvoicenumber">
					<input type="text"
					   name="pname"
					   id="pname"
					   maxlength="10"
					   
					   value="${getFunds.projectid}";
					   onclick="showmodeldialog();"
					   onchange="setProjectInfo(this)"
					   onpropertychange="getGetFundsHistoryInfo(this,'${getFunds.autoid}');getInvoiceHistoryInfo(this,'-1');"
					   title=""
					   class="required"
					   size="68"/><input type="hidden" id="projectid" name="projectid" title="${getFunds.projectid}" value="${getFunds.projectid}"> 
					   <span id="projectidspan" name="projectidspan" style="display: none">${getFunds.projectid}</span>
				</td>
			</tr>
			
				<tr>
				<th width="150" style="text-align: right">付&nbsp;款&nbsp;单&nbsp;位：</th>
				<td>
					<input type="text"
					   name="customerId"
					   id="customerId"
					   value="${getFunds.payCustomerId}"
					   onkeydown="onKeyDownEvent();"
					   onkeyup="onKeyUpEvent();"
					   onclick="onPopDivClick(this);"
					   autoid=2 
					   size="30"/> 
				</td>
				
				<th width="150" style="text-align: right">承&nbsp;接&nbsp;部&nbsp;门：</th>
				<td>
					<input type="text"
						   name="departname"
						   id="departname"
						   class="required"
						   onKeyDown="onKeyDownEvent();"
					       onKeyUp="onKeyUpEvent();" 
					       onClick="onPopDivClick(this);" 
					       valuemustexist=true 
					       autoid=123
					       value="${getFunds.continueDepartId}"
						   maxlength="50" size="30"  style="background-color: #f8f9f9"  />
				</td>
				
			</tr>
			
			<tr>
				<th width="150" style="text-align: right">委&nbsp;&nbsp;&nbsp;托&nbsp;&nbsp;号：</th>
				<td>
					<input type="text"
					   name="entrustNumber"
					   id="entrustNumber"
					   value=""
					   size="30" readonly="readonly" style="background-color: #f8f9f9" /> 
				</td>
				
				<th width="150" style="text-align: right">报&nbsp;告&nbsp;文&nbsp;号：</th>
				<td>
					<input type="text"
					   name="reportNumber"
					   id="reportNumber"
					   value=""
					   maxlength="20"
					   size="30"
					   readonly="readonly" style="background-color: #f8f9f9" />
				</td>
			</tr>
			
			
			<tr>
				<th width="150" style="text-align: right">业务约定书：</th>
				<td>
					<span id="reportfilename" ></span>
				</td>
				
				<th width="150" style="text-align: right">业务金额：</th>
				<td>
					<input type="text"
					   name="businessCost"
					   id="businessCost"
					   value=""
					   maxlength="20"
					   size="30"
					   readonly="readonly" style="background-color: #f8f9f9" />
				</td>
			</tr>
			
			<tr>
				<th width="150" style="text-align: right">业务状态：</th>
				<td>
					<input type="text"
					   name="property"
					   id="property"
					   value=""
					   size="30" readonly="readonly" style="background-color: #f8f9f9" /> 
				</td>
				
				<th width="150" style="text-align: right">企业是否具有证券：</th>
				<td>
					<input type="text"
					   name="isStock"
					   id="isStock"
					   value=""
					   maxlength="20"
					   size="30"
					   readonly="readonly" style="background-color: #f8f9f9" />
				</td>
			</tr>
			
			
			<tr>
				<th width="150" style="text-align: right;">已收款金额：</th>
				<td align="left">
					<input type="text" 
						   name="nmoney" 
						   id="nmoney"
						   maxlength="50" 
						   size="30" 
						   value="" readonly="readonly" style="background-color: #f8f9f9;" />&nbsp;元
				</td>
				
				<th width="150" style="text-align: right;">待收款金额：</th>
				<td>
					<input type="text" 
						   name="ymoney" 
						   id="ymoney"
						   maxlength="50" 
						   size="30"
						   value="" readonly="readonly" style="background-color: #f8f9f9;" />&nbsp;元
				</td>
			</tr>
			
		</table>
		
			
		<table cellpadding="1" align="center" cellspacing="1" width="100%" height="100%" class="tTable" id="getFunsdDetail">
			<tr>
				<th colspan="7">
					<center>本次收款明细&nbsp;&nbsp;<a href="#" onclick="f_addLine()">【点击增加】</a> </center>
				</th>
			</tr>
			<tr>
				<th style="display: none;">
					<center>开&nbsp;票&nbsp;客&nbsp;户&nbsp;代&nbsp;码</center>
				</th>
				<th >
					<center>凭&nbsp;证&nbsp;号</center>
				</th>
			 	<th style="display: none;">
					<center>发&nbsp;票&nbsp;编&nbsp;号</center>
				</th>
				<th >
					<center>收&nbsp;款&nbsp;日&nbsp;期</center>
				</th>
				<th >
					<center>收&nbsp;款&nbsp;形&nbsp;式</center>
				</th>
				<th >
					<center>收&nbsp;款&nbsp;金&nbsp;额</center>
				</th>
				<!-- 
				<th >
					<center>账&nbsp;面&nbsp;分&nbsp;类</center>
				</th>
				 -->
				<th >
					<center>操&nbsp;&nbsp;作</center>
				</th>
			</tr>
			<c:if test="${opt=='update'}">
				<tr>
				<th style="display: none;">
					<center><input id="customerCode" name="customerCode" size='20' value="${getFunds.customerCode}"></center>
				</th>
				<th >
					<center><input id="certificateNumber" name="certificateNumber" size='20' value="${getFunds.certificateNumber }"></center>
				</th>
				<th style="display: none;">
					<center><input id="invoicenumber" name="invoicenumber" size='20' value="${getFunds.invoicenumber }"></center>
				</th>
				<th >
					<center><input id="receicedate" name="receicedate" size='20' value="${getFunds.receicedate }" onclick="createDate(this)"></center>
				</th>
				<th >
					<center>
						<input id="ctype" 
							   name="ctype" 
							   value="${getFunds.ctype }" 
							   onkeydown="onKeyDownEvent();"
							   onkeyup="onKeyUpEvent();"
							   onclick="onPopDivClick(this);"
							   autoWidth="190"
							   size="25"
							   autoid="757">
					</center>
				</th>
				<th >
					<center><input id="receiceMoney" name="receiceMoney" size="10" onfocus="f_zero(this)" onkeyup="f_money(this);f_calc();" value="${getFunds.receiceMoney }" >元</center>
				</th>
				<th style="display: none;">
					<center>
						<input id="accounttype" 
							   name="accounttype" 
							   value="${getFunds.accounttype }"
							   onkeydown="onKeyDownEvent();"
							   onkeyup="onKeyUpEvent();"
							   onclick="onPopDivClick(this);"
							   autoWidth="190"
							   size="25"
							   autoid="4574">
					</center>
				</th>
				<th >
					<center><a href="#" onclick="f_delLine(this)">【删&nbsp;&nbsp;除】</a></center>
				</th>
			</tr>
			</c:if>
		</table>
		<br>
		
		<table cellpadding="1" align="center" cellspacing="1" width="100%" height="100%" class="tTable">
			<tr>
				<th width="150" style="text-align: right"></th>
				<td> </td>
				<th width="150" style="text-align: right">本次收款金额合计：</th>
				<td>
					<input type="text" 
						   name="total" 
						   id="total"
						   maxlength="50"
						   size="30"
						   value="" readonly="readonly" />元
				</td>
			</tr>
			
			<tr>
				<th width="150" style="text-align: right">操&nbsp;作&nbsp;人：</th>
				<td>
					<input type="text" 
						   name="optUser" 
						   id="optUser"
						   maxlength="50"
						   size="30"
						   value="${userSession.userName }" readonly="readonly" />
				</td>
				<th width="150" style="text-align: right">操&nbsp;作&nbsp;时&nbsp;间：</th>
				<td>
					<input type="text" 
						   name="cdate" 
						   id="cdate"
						   maxlength="50" 
						   size="30"
						   value="${now}"  readonly="readonly"/>
				</td>
			</tr>
			<tr>
				<th width="150" style="text-align: right" rowspan="2">备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注：</th>
				<td colspan="3">
					<textarea id="remark" name="remark" rows="3" cols="95%" >${getFunds.remark }</textarea>
				</td>
			</tr>
		</table>
		
		<table cellpadding="1" align="center" cellspacing="1" width="100%" height="100%" class="tTable" id="HistoryInfo">
			<tr>
				<td width="30%">
					<center>
						<table cellpadding="1" align="center" cellspacing="1" width="100%" height="100%" class="tTable" id="getFundsHistoryInfo">
							<tr>
								<th colspan="3">
									<center>历史收款明细</center>
								</th>
							</tr>
							<tr>
								<td width="20%"><center>收&nbsp;款&nbsp;日&nbsp;期</center></td>
								<td width="20%"><center>收&nbsp;款&nbsp;金&nbsp;额</center></td>
								<td width="20%"><center>操&nbsp;作&nbsp;人</center></td>
							</tr>
							<tr id="noGetFundsHistoryInfo">
								<td colspan="3">
									<center>暂无信息</center>
								</td>
							</tr>
						</table>
					</center>
				</td>
				<td width="70%">
					<center>
						<table cellpadding="1" align="center" cellspacing="1" width="100%" height="100%" class="tTable" id="invoiceHistoryInfo">
							<tr>
								<th colspan="5">
									<center>历史开票明细</center>
								</th>
							</tr>
							<tr>
								<td width="20%"><center>开&nbsp;票&nbsp;日&nbsp;期</center></td>
								<td width="20%"><center>发&nbsp;票&nbsp;号</center></td>
								<td width="20%"><center>开&nbsp;票&nbsp;金&nbsp;额</center></td>
								<td width="20%"><center>开&nbsp;票&nbsp;人</center></td>
								<td width="20%"><center>接&nbsp;收&nbsp;人</center></td>
							</tr>
							<tr id="noInvoiceHistoryInfo">
								<td colspan="5">
									<center>暂无信息</center>
								</td>
							</tr>
						</table>
					</center>
				</td>
			</tr>
		</table>
		 
	</fieldset>

<input type="hidden" id="skmoney" name="skmoney" value="${getFunds.receiceMoney}">
<input type="hidden" id="autoid" name="autoid" value="${getFunds.autoid}">
<input type="hidden" id="idKey" name="idKey" value="0" >
<input type="hidden" id="createDateKey" name="createDateKey" value="0"  >
</form>

<iframe name="groupFrame" id="groupFrame" scrolling="no" frameborder="0" width="100%" height="5%" src=""></iframe>
</div>
</body>

<script type="text/javascript">
	// var autoid = document.getElementById("autoid").value;
	var autoid = "${getFunds.autoid}";
	setObjDisabled("customerId");
	setObjDisabled("departname");
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
		//document.getElementById("receicemoney").value="0";
		//document.getElementById("receicedate").value=year+"-"+month+"-"+date;
	}else{
		// 设置项目信息
		var projectid = "${getFunds.projectid}";
		var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		var url = "${pageContext.request.contextPath}/getFunds.do?method=getProjectInfoJson&projectid="+projectid;
		oBao.open("post",url,false);
		oBao.send();
		var strResult = oBao.responseText;
		try{
			var obj = eval('('+strResult+')'); 
			var imoney = "${getFunds.receiceMoney}" ;
			if(document.getElementById("customerId").value ==""){
				document.getElementById("customerId").value=obj[0].customername;
			}
			document.getElementById("entrustNumber").value=obj[0].entrustNumber;
			document.getElementById("reportNumber").value=obj[0].reportNumber;
			if(document.getElementById("departname").value ==""){
				document.getElementById("departname").value=obj[0].departmentId;
			}
			document.getElementById("businessCost").value=obj[0].businessCost;
			document.getElementById("nmoney").value=accSub(imoney*1,obj[0].getFundsMoney*1);
			document.getElementById("ymoney").value= accSub(accSub(imoney*1,obj[0].getFundsMoney*1),obj[0].businessCost*1) ;
			document.getElementById("isStock").value=obj[0].isStock; 
			
			if(obj[0].reportfilename=="无"){
				document.getElementById("reportfilename").innerHTML="<a href='#'>无</a>";
			}else{
				document.getElementById("reportfilename").innerHTML="<a href='#' onclick=f_downReportFile('"+obj[0].reportfiletempname+"','"+obj[0].reportfilename+"')>"+obj[0].reportfilename+"</a>";
			}
			
			document.getElementById("property").value=obj[0].property; 
		}catch(e){
			alert(e);
		}
		
		var obj = document.getElementById("projectid");
		// 历史开票信息
		getInvoiceHistoryInfo(obj,"");
		
		// 历史收款信息
		getGetFundsHistoryInfo(obj,"${getFunds.autoid}");
		
		// 算总金额
		f_calc();
	}
	
		

	// 设置项目信息
	function setProjectInfo(t){
		var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		var url = "${pageContext.request.contextPath}/getFunds.do?method=getProjectInfoJson&projectid="+t.value;
		oBao.open("post",url,false);
		oBao.send();
		var strResult = oBao.responseText;
		
		var obj = eval('('+strResult+')'); 
        document.getElementById("myinvoicenumber").value=obj[0].invoicenumber;
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
	function save(p) {
		// 删除空的行
		f_delNullLine();
		
		var objs = document.getElementsByName("receiceMoney");
		if(objs.length>0){
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
				
				document.thisForm.action = "${pageContext.request.contextPath}/getFunds.do?method=save&opt="+p ;
				document.thisForm.submit();
			}
		}else{
			alert("请添加行！");
			return;
		}
	}


 
	

	var projectWin = null;
	function showmodeldialog(){
		isSave = false;
		//var url="${pageContext.request.contextPath}/getFunds.do?method=dhlist2" ;
		var url="${pageContext.request.contextPath}/getFunds.do?method=newdhlist2" ;
		
			
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
		       	html:'<iframe name="groupFrameTemp" id="groupFrameTemp" scrolling="no" frameborder="0" width="100%" height="100%" src="' + url + '"></iframe>',
	        	layout:'fit'
		    });
		    document.getElementById("groupFrame").style.display = "none";
	   
		
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
	function f_zero(t){
		if(t.value==0){
			t.value="";
		}
	}
	
	// 验证金额 
	function f_moneyBAK(){
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
	
	// 业务约定书下载
	function f_downReportFile(fileTempName,fileName){
		var url = "${pageContext.request.contextPath}/invoice.do?method=download";
		var requestString = "fileTempName=" + fileTempName + "&fileName="+fileName;
		var result = ajaxLoadPageSynch(url,requestString);
	}
	
	
	
	// 查询历史开票记录
	function getInvoiceHistoryInfo(t,autoid){
		var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		var url = "${pageContext.request.contextPath}/invoice.do?method=getHistoryInfo&projectid="+t.title+"&autoid="+autoid;
		oBao.open("post",url,false);
		oBao.send();
		var strResult = oBao.responseText;

		var obj = eval('('+strResult+')'); 
		
		var table = document.getElementById("invoiceHistoryInfo");
		
		var noHistoryInfo = document.getElementById("noInvoiceHistoryInfo");
	
		// 清楚上一笔记录
		if(noHistoryInfo.style.display=="none"){
			var rows = table.rows.length;
			for(var i=rows-1;i>2;i--){
				table.deleteRow(i);
			}
		}
		
		for(var i=0;i<obj.length;i++){
			//添加一行
			var newTr = table.insertRow();
			
			//添加两列
			var newTd1 = newTr.insertCell();
			var newTd2 = newTr.insertCell();
			var newTd3 = newTr.insertCell();
			var newTd4 = newTr.insertCell();
			var newTd5 = newTr.insertCell();
			
			// 设置列内容和属性
			newTd1.innerHTML = "<center>"+obj[i].cdate+"<center>"; 
			newTd2.innerHTML = "<center>"+obj[i].invoicenumber+"<center>";
			if(obj[i].money*1<0){
				newTd3.innerHTML = "<center ><span style='color:red'>"+obj[i].money+"&nbsp;&nbsp;&nbsp;</span>(元)<center>";
			}else{
				newTd3.innerHTML = "<center >"+obj[i].money+"&nbsp;&nbsp;&nbsp;(元)<center>";
			}
			newTd4.innerHTML = "<center>"+obj[i].createUser+"<center>";
			newTd5.innerHTML = "<center>"+obj[i].receiceUser+"<center>";
			
		}
		
		if(obj.length>0){	
			document.getElementById("noInvoiceHistoryInfo").style.display = "none";
		}
		
	}
	
	
	
	// 查询历史收款记录
	function getGetFundsHistoryInfo(t,autoid){
		var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		var url = "${pageContext.request.contextPath}/getFunds.do?method=getGetFundsHistoryInfo&projectid="+t.title+"&autoid="+autoid;
		oBao.open("post",url,false);
		oBao.send();
		var strResult = oBao.responseText;

		var obj = eval('('+strResult+')'); 
		
		var table = document.getElementById("getFundsHistoryInfo");
		
	    var noHistoryInfo = document.getElementById("noGetFundsHistoryInfo");
	
		// 清楚上一笔记录
		if(noHistoryInfo.style.display=="none"){
			var rows = table.rows.length;
			for(var i=rows-1;i>2;i--){
				table.deleteRow(i);
			}
		}
		
		for(var i=0;i<obj.length;i++){
			//添加一行
			var newTr = table.insertRow();
			
			//添加两列
			var newTd1 = newTr.insertCell();
			var newTd2 = newTr.insertCell();
			var newTd3 = newTr.insertCell();
			
			  
			// 设置列内容和属性
			newTd1.innerHTML = "<center>"+obj[i].receicedate+"<center>"; 
			if(obj[i].receiceMoney*1<0){
				newTd2.innerHTML = "<center ><span style='color:red'>"+obj[i].receiceMoney+"&nbsp;&nbsp;&nbsp;</span>(元)<center>";
			}else{
				newTd2.innerHTML = "<center >"+obj[i].receiceMoney+"&nbsp;&nbsp;&nbsp;(元)<center>";
			}
			newTd3.innerHTML = "<center>"+obj[i].createUser+"<center>";
			
		}
		
		if(obj.length>0){	
			document.getElementById("noGetFundsHistoryInfo").style.display = "none";
		}
		
	}
	
	
	// 添加行
	function f_addLine(){
	
		 var table = document.getElementById("getFunsdDetail");
	      //添加一行
	      var newTr = table.insertRow();
	
	      //添加 
	      //var newTd1 = newTr.insertCell();
	      var newTd2 = newTr.insertCell();
	      //var newTd3 = newTr.insertCell();
	      var newTd4 = newTr.insertCell();
	      var newTd5 = newTr.insertCell();
	      var newTd6 = newTr.insertCell();
	      //var newTd7 = newTr.insertCell();
	      var newTd8 = newTr.insertCell();
	      
	      var k = document.getElementById("idKey").value;
       	  k = k*1+1;
          var a = document.getElementById("myinvoicenumber").value;
	      // 设置列内容和属性
	     // newTd1.innerHTML = "<center><input type='text' class='required' name='customerCode' id='customerCode"+k+"'  size='20'  ></center>"; 
	      newTd2.innerHTML = "<center><input type='text' class='required' name='certificateNumber' id='certificateNumber"+k+"' size='20' value="+a+"  ></center>"; 	
	      //newTd3.innerHTML = "<center><input type='text' class='required' name='invoicenumber'  id='invoicenumber"+k+"' size='20' value="+a+"  ></center>"; 
	      newTd4.innerHTML = "<center><input type='text' class='required' name='receicedate' id='receicedate"+k+"' size='20' /></center>"; 
	      newTd5.innerHTML = "<center><input type='text' class='required' name='ctype' id='ctype"+k+"' size='20' onkeydown='onKeyDownEvent();' onkeyup='onKeyUpEvent();' onclick='onPopDivClick(this);' autoWidth='190' autoid='757' /></center>";
	      newTd6.innerHTML = "<center><input type='text' class='required' name='receiceMoney' id='receiceMoney"+k+"' size='10' onfocus='f_zero(this)' onkeyup='f_money(this);f_calc();' />元</center>";
	      //newTd7.innerHTML = "<center><input type='text' class='required' name='accounttype' id='accounttype"+k+"' size='20' onkeydown='onKeyDownEvent();' onkeyup='onKeyUpEvent();' onclick='onPopDivClick(this);' autoWidth='190' autoid='4574' ></center>";
	      newTd8.innerHTML = "<center><a href='#' onclick='f_delLine(this)'>【删&nbsp;&nbsp;除】</a><center>";
	      
	      //初始化日期控件
		  new Ext.form.DateField({
			  applyTo : 'receicedate'+k,
			  width: 133,
			  format: 'Y-m-d'
		  });
		
	      document.getElementById("idKey").value=k;
	}
	
	
	// 删除行
	function f_delLine(t){
		if(confirm("您确定要删除吗?")){
			t.parentNode.parentNode.parentNode.removeNode(true);
		}
	}
	 
	// 打开
	function fileOpen(tempName,fileName) {
		AuditReport.pOpenMode=1;
		AuditReport.pFileName = fileName; 
		AuditReport.pFileDir="c:\\manu\\workflow" ;
		AuditReport.pOpenUrl="http:\/\/"+window.location.host + "${pageContext.request.contextPath}/invoice.do?method=download&fileTempName=" + tempName;
		AuditReport.pSaveUrl="http:\/\/"+window.location.host + "${pageContext.request.contextPath}/invoice.do?method=upload&fileTempName=" + tempName;
		var UrlParameter="&manuname="+fileName;
		AuditReport.pUrlParameter="http:\/\/"+window.location.host + "|" + UrlParameter;
		AuditReport.funOpenUrlFile(1);
	}
	
	// 验证金额
	function f_money(t){
		t.value = t.value.replace(/[^\d\.\\-]/g,'');
		if(t.value*1<0){
			t.style.color = "red";
		}else{
			t.style.color = "black";
		}
	} 
	
	
	// 算出总额
	function f_calc(){
		var money = document.getElementsByName("receiceMoney");
		var totalMoney = "0"; 
		if(money){
			for(var i=0;i<money.length;i++){
				totalMoney = totalMoney*1+money[i].value*1;
			}
		}
		if(totalMoney*1<0){
			document.getElementById("total").style.color = "red";
		}else{
			document.getElementById("total").style.color = "black";
		}
		document.getElementById("total").value = totalMoney;
			
	} 
	
	
	// 删除发票号为空的行
	function f_delNullLine(){
		var receiceMoney = document.getElementsByName("receiceMoney");
	
		for(var i=receiceMoney.length-1;i>=0;i--){
			if(receiceMoney[i].value.trim()==""){
				receiceMoney[i].parentNode.parentNode.parentNode.removeNode(true);
			}
		}
	}
	
	// 初始化时间控件
	function createDate(t){
		var v = document.getElementById("createDateKey").value;
		if(v!="1"){
		 	//初始化日期控件
			new Ext.form.DateField({
				applyTo : t.id,
				width: 133,
			    format: 'Y-m-d'
			});
		}
		document.getElementById("createDateKey").value = "1";
	}
</script>
</html>