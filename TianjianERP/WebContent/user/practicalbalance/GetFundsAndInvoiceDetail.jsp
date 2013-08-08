<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
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
<div style="overflow: auto;width:100%;height:470px">
<div style="width:99%;">
	<c:forEach items="${requestScope['getFunds']}" var="getFunds" varStatus="gfs">
	<fieldset>
		<legend>收款登记表</legend>
		<table cellpadding="1" align="center" cellspacing="1" width="100%" height="100%" class="tTable">
		
			<tr>
				<th width="150" style="text-align: right;">项目名称<span class="mustSpan">[*]</span>：</th>
				<td align="left" colspan="3">
					<input type="hidden" id="myinvoicenumber" name="myinvoicenumber">
					<input type="text"
					   name="pname${gfs.index}"
					   id="pname${gfs.index}"
					   maxlength="10"
					   value="${getFunds.projectid}";
					   onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();"
						noinput="true" onClick="onPopDivClick(this);"
					   title=""
					   class="required"
					   size="68" 
					   autoid="963" readonly="readonly"/> <input type="hidden" id="projectid${gfs.index}" name="projectid${gfs.index}" title="${getFunds.projectid}" value="${getFunds.projectid}"> 
					   <span id="projectidspan${gfs.index}" name="projectidspan${gfs.index}" style="display: none">${getFunds.projectid}</span>
				</td>
			</tr>
			
				<tr>
				<th width="150" style="text-align: right">付&nbsp;款&nbsp;单&nbsp;位：</th>
				<td>
					<input type="text"
					   name="customerId"
					   id="customerId"
					   value="${projectInfo.customername }"
					   size="30" readonly="readonly" style="background-color: #f8f9f9" /> 
				</td>
				
				<th width="150" style="text-align: right">承&nbsp;接&nbsp;部&nbsp;门：</th>
				<td>
					<input type="text"
						   name="departname"
						   id="departname"
						   value="${projectInfo.departname}"
						   maxlength="50" size="30" readonly="readonly" style="background-color: #f8f9f9"  />
				</td>
				
			</tr>
			
			<tr>
				<th width="150" style="text-align: right">委&nbsp;&nbsp;&nbsp;托&nbsp;&nbsp;号：</th>
				<td>
					<input type="text"
					   name="entrustNumber"
					   id="entrustNumber"
					   value="${projectInfo.entrustNumber }"
					   size="30" readonly="readonly" style="background-color: #f8f9f9" /> 
				</td>
				
				<th width="150" style="text-align: right">报&nbsp;告&nbsp;文&nbsp;号：</th>
				<td>
					<input type="text"
					   name="reportNumber"
					   id="reportNumber"
					   value="${projectInfo.reportNumber }"
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
					   value="${projectInfo.businessCost }"
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
					   value="${projectInfo.property }"
					   size="30" readonly="readonly" style="background-color: #f8f9f9" /> 
				</td>
				
				<th width="150" style="text-align: right">企业是否具有证券：</th>
				<td>
					<input type="text"
					   name="isStock"
					   id="isStock"
					   value="${projectInfo.isStock }"
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
						   value="${projectInfo.getFundsMoney }" readonly="readonly" style="background-color: #f8f9f9;" />&nbsp;元
				</td>
				
				<th width="150" style="text-align: right;">待收款金额：</th>
				<td>
					<input type="text" 
						   name="ymoney" 
						   id="ymoney"
						   maxlength="50" 
						   size="30"
						   value="${ymoney }" readonly="readonly" style="background-color: #f8f9f9;" />&nbsp;元
				</td>
			</tr>
			
		</table>
		
			
		<table cellpadding="1" align="center" cellspacing="1" width="100%" height="100%" class="tTable" id="getFunsdDetail">
			<tr>
				<th colspan="6">
					<center>本次收款票明细&nbsp;&nbsp;</center>
				</th>
			</tr>
			<tr>
			 	<th width="20%">
					<center>发&nbsp;票&nbsp;编&nbsp;号</center>
				</th>
				<th width="20%">
					<center>收&nbsp;款&nbsp;日&nbsp;期</center>
				</th>
				<th width="20%">
					<center>收&nbsp;款&nbsp;形&nbsp;式</center>
				</th>
				<th width="20%">
					<center>收&nbsp;款&nbsp;金&nbsp;额</center>
				</th>
				<th width="20%">
					<center>账&nbsp;面&nbsp;分&nbsp;类</center>
				</th>
			</tr>

				<tr>
				<th width="20%">
					<center><input id="invoicenumber${gfs.index}" name="invoicenumber${gfs.index}" size='20' value="${getFunds.receicedate }" onclick="createDate(this)"></center>
				</th>
				<th width="20%">
					<center><input id="receicedate${gfs.index}" name="receicedate${gfs.index}" size='20' value="${getFunds.receicedate }" onclick="createDate(this)"></center>
				</th>
				<th width="20%">
					<center>
						<input id="ctype${gfs.index}" 
							   name="ctype${gfs.index}" 
							   value="${getFunds.ctype }" 
							   onkeydown="onKeyDownEvent();"
							   onkeyup="onKeyUpEvent();"
							   onclick="onPopDivClick(this);"
							   autoWidth="190"
							   size="25"
							   autoid="757" readonly="readonly">
					</center>
				</th>
				<th width="20%">
					<center><input id="receiceMoney" name="receiceMoney" size="25" onfocus="f_zero(this)" onkeyup="f_money(this);f_calc();" value="${getFunds.receiceMoney }" >元</center>
				</th>
				<th width="20%">
					<center>
						<input id="accounttype${gfs.index}" 
							   name="accounttype${gfs.index}" 
							   value="${getFunds.accounttype }"
							   onkeydown="onKeyDownEvent();"
							   onkeyup="onKeyUpEvent();"
							   onclick="onPopDivClick(this);"
							   autoWidth="190"
							   size="25"
							   autoid="4574" readonly="readonly">
					</center>
				</th>
			</tr>

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
						   value="${getFunds.receiceMoney }" readonly="readonly" />元
				</td>
			</tr>
			
			<tr>
				<th width="150" style="text-align: right">操&nbsp;作&nbsp;人：</th>
				<td>
					<input type="text" 
						   name="optUser${gfs.index}" 
						   id="optUser${gfs.index}"
						   maxlength="50"
						   onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();"
							noinput="true" onClick="onPopDivClick(this);"
						   size="30"
						   value="${getFunds.createUser }" readonly="readonly" autoid="867"/>
				</td>
				<th width="150" style="text-align: right">操&nbsp;作&nbsp;时&nbsp;间：</th>
				<td>
					<input type="text" 
						   name="cdate${gfs.index}" 
						   id="cdate${gfs.index}"
						   maxlength="50" 
						   size="30"
						   value="${getFunds.receicedate}"  readonly="readonly"/>
				</td>
			</tr>
			<tr>
				<th width="150" style="text-align: right" rowspan="2">备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注：</th>
				<td colspan="3">
					<textarea id="remark${gfs.index}" name="remark${gfs.index}" rows="3" cols="95%" >${getFunds.remark }</textarea>
				</td>
			</tr>
		</table>
		
		<table cellpadding="1" align="center" cellspacing="1" width="100%" height="100%" class="tTable" id="HistoryInfo">
			<tr>
				<td >
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
							<%int i =0; %>
							<c:forEach items="${historyGetFunsMap}" var="mapHgf" >
								<c:if test="${mapHgf.autoid == getFunds.autoid}">
									<tr>
										<td>${mapHgf.receicedate}</td>
										<td>${mapHgf.receiceMoney}</td>
										<td>${mapHgf.createUser}</td>
										<%i = i+1; %>
									</tr>
								</c:if>
							</c:forEach>
								<%if(i==0){ %>
									<tr id="noGetFundsHistoryInfo${gfs.index}">
										<td colspan="3">
											<center>暂无信息</center>
										</td>
									</tr>
								<%} %>
						</table>
					</center>
				</td>
				
			</tr>
		</table>
		 
	</fieldset>
	</c:forEach>
</div>

<div style="width:99%;"><span class="mustSpan">-----------------------------------------------------</span>以下是开票登记表:<span class="mustSpan">-----------------------------------------------------</span></div>
<div style="width:99%;">
	<c:forEach items="${requestScope['invoice']}" var="invoice" varStatus="inv">
	<fieldset>
		<legend>开票登记表</legend>
		<table cellpadding="1" align="center" cellspacing="1" width="100%" height="100%" class="tTable">
		
			<tr>
				<th width="150" style="text-align: right">项目名称<span class="mustSpan">[*]</span>：</th>
				<td align="left" colspan="3">
					<input type="text"
						   id="iname${inv.index}"
						   name="iname${inv.index}"
						   maxlength="10"
						   value="${invoice.projectid}"
						   onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();"
						   noinput="true" onClick="onPopDivClick(this);"
						   title="${invoice.projectid}"
						   class="required"
						   size="68" 
						   autoid="963" readonly="readonly"/><input type="hidden" id="projectid" name="projectid" title="${invoice.projectid}" value="${invoice.projectid}"> 
						   <span id="projectidspan" name="projectidspan" style="display: none">${invoice.projectid}</span>
				</td>
			</tr>
			
			<tr>
				<th width="150" style="text-align: right">付&nbsp;款&nbsp;单&nbsp;位：</th>
				<td>
					<input type="text"
					   name="customerId"
					   id="customerId"
					   value="${projectInfoInv.customername }"
					   size="30" readonly="readonly" style="background-color: #f8f9f9" /> 
				</td>
				
				<th width="150" style="text-align: right">承&nbsp;接&nbsp;部&nbsp;门：</th>
				<td>
					<input type="text"
						   name="departname"
						   id="departname"
						   value="${projectInfoInv.departname}"
						   maxlength="50" size="30" readonly="readonly" style="background-color: #f8f9f9"  />
				</td>
				
			</tr>
			
			<tr>
				<th width="150" style="text-align: right">委&nbsp;&nbsp;&nbsp;托&nbsp;&nbsp;号：</th>
				<td>
					<input type="text"
					   name="entrustNumber"
					   id="entrustNumber"
					   value="${projectInfoInv.entrustNumber }"
					   size="30" readonly="readonly" style="background-color: #f8f9f9" /> 
				</td>
				
				<th width="150" style="text-align: right">报&nbsp;告&nbsp;文&nbsp;号：</th>
				<td>
					<input type="text"
					   name="reportNumber"
					   id="reportNumber"
					   value="${projectInfoInv.reportNumber}"
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
					   value="${projectInfoInv.businessCost }"
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
					   value="${projectInfoInv.property }"
					   size="30" readonly="readonly" style="background-color: #f8f9f9" /> 
				</td>
				
				<th width="150" style="text-align: right">企业是否具有证券：</th>
				<td>
					<input type="text"
					   name="isStock"
					   id="isStock"
					   value="${projectInfoInv.isStock }"
					   maxlength="20"
					   size="30"
					   readonly="readonly" style="background-color: #f8f9f9" />
				</td>
			</tr>
			
			
			<tr>
				<th width="150" style="text-align: right">已开票金额：</th>
				<td align="left">
					<input type="text" 
						   name="nmoney" 
						   id="nmoney"
						   maxlength="50" 
						   size="30" 
						   value="${projectInfoInv.invoicemoney }" readonly="readonly" style="background-color: #f8f9f9;" />&nbsp;元
				</td>
				
				
				<th width="150" style="text-align: right">待开票金额：</th>
				<td>
					<input type="text" 
						   name="ymoney" 
						   id="ymoney"
						   maxlength="50" 
						   size="30"
						   value="${ymoneyInv }" readonly="readonly" style="background-color: #f8f9f9;" />&nbsp;元
				</td>
			</tr>
		</table>
		
		<table cellpadding="1" align="center" cellspacing="1" width="100%" height="100%" class="tTable" id="invoiceDetail">
			<tr>
				<th colspan="3">
					<center>本次开票明细&nbsp;&nbsp;</center>
				</th>
			</tr>
			<tr>
				<th width="40%">
					<center>发&nbsp;票&nbsp;编&nbsp;号</center>
				</th>
				<th width="40%">
					<center>开&nbsp;票&nbsp;金&nbsp;额</center>
				</th>

			</tr>
				<tr>
				<th width="40%">
					<center><input id="invoicenumber" name="invoicenumber" size='30' value="${invoice.invoicenumber }"></center>
				</th>
				<th width="40%">
					<center><input id="money" name="money" size="30" onfocus="f_zero()" onkeyup="f_money(this);f_calc();" value="${invoice.money }" >元</center>
				</th>

			</tr>
		</table>
		<br>
		<table cellpadding="1" align="center" cellspacing="1" width="100%" height="100%" class="tTable">
			<tr>
				<th width="150" style="text-align: right">发票接收人<span class="mustSpan">[*]</span>：</th>
				<td>
					<input type="text" 
						   name="receiceUser" 
						   id="receiceUser"
						   maxlength="50"
						   size="30"
						   class="required"
						   value="${invoice.receiceUser }"  />
				</td>
				<th width="150" style="text-align: right">本次开票金额合计：</th>
				<td>
					<input type="text" 
						   name="total" 
						   id="total"
						   maxlength="50"
						   size="30"
						   value="${invoice.money }" readonly="readonly" />元
				</td>
			</tr>
			
			<tr>
				<th width="150" style="text-align: right">操&nbsp;作&nbsp;人：</th>
				<td>
					<input type="text" 
						   name="optUserIn" 
						   id="optUserIn"
						   maxlength="50"
						   onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();"
							noinput="true" onClick="onPopDivClick(this);"
						   size="30"
						   value="${invoice.createUser }" readonly="readonly" autoid="867"/>
				</td>
				<th width="150" style="text-align: right">开&nbsp;票&nbsp;日&nbsp;期<span class="mustSpan">[*]</span>：</th>
				<td>
					<input type="text" 
						   name="cdate" 
						   id="cdate"
						   maxlength="50" 
						   size="30"
						   value="${invoice.cdate}"  class="required"/>
				</td>
			</tr>
			<tr>
				<th width="150" style="text-align: right" rowspan="2">备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注：</th>
				<td colspan="3">
					<textarea id="remark" name="remark" rows="3" cols="95%" >${invoice.remark }</textarea>
				</td>
			</tr>
		</table>
		
		<table cellpadding="1" align="center" cellspacing="1" width="100%" height="100%" class="tTable" id="historyInfo">
			<tr>
				<th colspan="5">
					<center>历史开票信息</center>
				</th>
			</tr>
			<tr>
				<td width="20%"><center>开&nbsp;票&nbsp;日&nbsp;期</center></td>
				<td width="20%"><center>发&nbsp;票&nbsp;号</center></td>
				<td width="20%"><center>开&nbsp;票&nbsp;金&nbsp;额</center></td>
				<td width="20%"><center>开&nbsp;票&nbsp;人</center></td>
				<td width="20%"><center>接&nbsp;收&nbsp;人</center></td>
			</tr>
			<%int j=0; %>
			<c:forEach items="${historyInv}" var="hin">
				<c:if test="${hin.autoid==invoice.autoid}">
					<tr>
						<td>${hin.cdate }</td>
						<td>${hin.invoicenumber }</td>
						<td>${hin.money }</td>
						<td>${hin.createUser }</td>
						<td>${hin.receiceUser }</td>
						<%j++; %>
					</tr>
				</c:if>
			</c:forEach>
			<%if(j==0){ %>
			<tr id="noHistoryInfo">
				<td colspan="5">
					<center>暂无信息</center>
				</td>
			</tr>
			<%} %>
		</table>
		
	</fieldset>
	</c:forEach>

</div>
</div>	
</body>
<script type="text/javascript">
 
function getxxx(autoid,trid){
	//1 :访问后台 得到值
	//2:给 TR添加子元素(td)
}

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
</script>
</html>