<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@page import="com.matech.framework.listener.UserSession"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style>
	.workClass {
		background-color: "#ffffff";
		height: 22px;
	}

	.tTable {margin-top:10px;border:#d7e2f3 1px solid;border-collapse:collapse;width: 80%}
	.tTable td,th {
		padding: 5 5 5 1px;white-space:nowrap;border-top:#d7e2f3 1px solid;border-left: #d7e2f3 1px solid;height:30px;
	}
	.tTable th{background-color: #f8f9f9;}
	.tTable input {border:1px solid #d7e2f3;}
	
	.data_tb_content {
		text-align: center;
	}

	.a_href {
		blr:expression(this.onFocus=this.blur());
	}


</style>
<script type="text/javascript">
var tbar_project;
var tab;
 
function extInit(){
	

	tab = new Ext.TabPanel({
        id: "tab",
        renderTo: "divTab",
        activeTab: 0, //选中第一个 tab
        autoScroll:true,
        frame: true,
        height: document.body.clientHeight-Ext.get('divTab').getTop()-50, 
        defaults: {autoHeight: true,autoWidth:true},
        items:[{
        			contentEl: "tab1", 
        			title: "项目信息", 
        			listeners: {
        				activate: function(){
        				}
        			}
        		},{
        			contentEl: "tab2", 
        			title: "客户信息",
        			listeners: {
						activate: function(){
        				}
        			}
        		},{
        			contentEl: "tab3", 
        			title: "参与标书成员",
        			listeners: {
        				activate: function(){
        				}
        			}
        		},{
        			contentEl: "tab4", 
        			title: "标书信息",
        			listeners: {
        				activate: function(){
        				}
        			}
        		}
        ] 
	});
	
	var flag = "${param.flag}";
	var text = "返回";
	var icon = "back.gif";
	
	if(flag == "basic") {
		text = "关闭";
		icon = "close.gif";
	}
	
	tbar_project = new Ext.Toolbar({
		renderTo: "divBtn",
		items:[{ 
			text:text,
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/' + icon,
			handler:function(){
				window.history.back();
			}
   		}]
    });
      	
 	new Ext.Viewport({
		defaults:{border:false},
		items:[
			tbar_project
		]
	})
	
	 
}


//检查同名客户
function checkCustomer() {
	var unitName = document.getElementById("unitName").value;
	
	var url = "${pageContext.request.contextPath}/customer.do?method=checkCustomerName";
	var request = "&customerName=" + unitName + "&rand=" + new Date().getTime();
	var result = ajaxLoadPageSynch(url,request);
	
	return result;
}

</script>
</head>
<body>
<div id="panel"></div>
<div id="divBtn" ></div>
<form name="thisForm" method="post" action="" id="thisForm" class="autoHeightForm">
<span class="formTitle">招投标信息</span><br/>

<table class="tTable">
	<tr>
		<td width="5%" style="text-align: right;">投标创建人：</td>
		<td width="15%" style="text-align: left;">${bp.createName}</td>
		<td width="5%" style="text-align: right;">创建时间：</td>
		<td width="15%" style="text-align: left;">${bp.createDate}</td>
		<td width="5%" style="text-align: right;">审核人：</td>
		<td width="15%" style="text-align: left;">${bp.auditorName}</td>
		<td width="5%" style="text-align: right;">审核时间：</td>
		<td width="15%" style="text-align: left;">${bp.auditDate}</td>
		<td width="5%" style="text-align: right;">状态：</td>
		<td width="15%" style="text-align: left;">${bp.bidStatus}</td>
	</tr>
</table>

<br/>
<c:if test="${bp.reason!='' && bp.reason!=null}">
<table class="tTable" id="tab_reason">
	<tr>
		<td width="15%" style="text-align: right;">不通过原因：</td>
		<td style="text-align: left;"><textarea rows="5" cols="90%" id="reason" name="reason" disabled="disabled">${bp.reason }</textarea></td>
	</tr>
</table>
</c:if>

<br/>	
<div id="divTab" style="overflow:auto">
	<!-- 项目信息 -->
	<div id="tab1" class="tabDiv">
		<table cellpadding="5" align="left" border="0" cellspacing="10">
			<tr>
				<td width="150" align="right">被审计单位<span class="mustSpan">[*]</span>：</td>
				<td>
					<input type="text"
					   name="auditUnit"
					   id="auditUnit"
					   value="${bp.auditUnit }"
					   class="required"
					   maxlength="60"
					   title="请输入有效的值"
					   onkeydown="onKeyDownEvent();"
					   onkeyup="onKeyUpEvent();"
					   onclick="onPopDivClick(this);"
					   useAdvice=true
					   autoid=110 onchange="changeaAuditUnit(this);setProjectName();" /> 
				</td>
			</tr>
			
			<!-- 委托机构 -->
			<tr>
				<td width="150" align="right">委托机构<span class="mustSpan">[*]</span>：</td>
				<td>
					<input name="trustOrgan" id="trustOrgan" value="${bp.trustOrgan }" class="required" class="required"
					   maxlength="60"
					   title="请输入有效的值"
					   onkeydown="onKeyDownEvent();"
					   onkeyup="onKeyUpEvent();"
					   onclick="onPopDivClick(this);"
					   useAdvice=true
					   autoid=110 >
				</td>
			</tr>
			
			<!-- 业务区间 -->
			<tr>
				<td width="150" align="right">业务区间开始<span class="mustSpan">[*]</span>：</td>
				<td>
					<input type="text" name="serviceStartTime" readonly="readonly" id="serviceStartTime" class="required validate-date-cn" 
						title="请输入有效的值" value="${bp.serviceStartTime }" onpropertychange="setProjectName();" />
				</td>
			</tr>
			<tr>
				<td width="150" align="right">业务区间结束<span class="mustSpan">[*]</span>：</td>
				<td>
					<input type="text" name="serviceEndTime" readonly="readonly" id="serviceEndTime" class="required validate-date-cn" 
						title="请输入有效的值" value="${bp.serviceEndTime }" onpropertychange="setProjectName();" />
				</td>
			</tr>
			
			
			<tr>
				<td width="150" align="right">业务类型<span class="mustSpan">[*]</span>：</td>
				<td>
					<input type="text"
					   name="serviceType"
					   id="serviceType"
					   value="${bp.serviceType }"
					   useAdvice = true
					   maxlength="20"
					   class="required"
					   noinput=true
					   title="请输入有效的值"
					   onkeydown="onKeyDownEvent();"
					   onkeyup="onKeyUpEvent();"
					   onclick="onPopDivClick(this);"
					   onchange="setProjectName();"
					   autoid=58"/>
				</td>
			</tr>

			<!-- 项目名称，手工输入 -->
			<tr>
				<td width="150" align="right">项目名称<span class="mustSpan">[*]</span>：</td>
				<td>
					<input type="text" size="50" maxlength="500" title="请输入有效的值" name="projectName" id="projectName" class="required" value="${bp.projectName }" />
				</td>
			</tr>
			
			
			<!-- 项目简称，手工输入 -->
			<tr>
				<td width="150" align="right">项目简称：</td>
				<td>
					<input type="text" size="25" title="请输入有效的值" name=projectSimpleName id="projectSimpleName" value="${bp.projectSimpleName }" />
				</td>
			</tr>
			
		</table>
	</div>
	
	<!-- 客户信息 -->
	<div id="tab2" class="x-hide-display tabDiv">
		<table cellpadding="5" width="800" align="left" border="0" cellspacing="10">
			<!-- 单位名称 -->
			<tr>
				<td width="150" align="right">单位名称<span class="mustSpan">[*]</span>：</td>
				<td>
					<input type="text" size="50" maxlength="200" name="unitName" id="unitName" title="请输入有效的值" value="${bp.unitName }" />
				</td>
			</tr>
			
			<!-- 会计制度 -->
			<tr>
				<td width="150" align="right">会计制度类型<span class="mustSpan">[*]</span>：</td>
				<td>
					<input name="vocationType" 
						   id="vocationType" 
					       type="text" 
					       class="required validate-digits" 
					       title="请输入有效的值"
	      				   onkeydown="onKeyDownEvent();" 
	      				   onkeyup="onKeyUpEvent();" 
	      				   onclick="onPopDivClick(this);" 
	      				   valuemustexist=true
	      				   useAdvice = true
	      				   autoid=3 
	      				   value="${bp.vocationType }"
	      				   noinput=true />
				</td>
			</tr>
			
			<!-- 单位英文名称 -->
			<tr>
				<td width="150" align="right">单位英文名称：</td>
				<td>
					<input name="unitEngName" type="text"  id="unitEngName" title="单位英文名称" value="${bp.unitEngName }" />
				</td>
			</tr>
			
			<!-- 行业类型 -->
			<tr>
				<td width="150" align="right">行业类型：</td>
				<td>
					<input name="hylx" 
					       id="hylx" 
					       type="text" 
					       onkeydown="onKeyDownEvent();" 
					       onkeyup="onKeyUpEvent();" 
					       onclick="onPopDivClick(this);" 
					       valuemustexist=true 
					       autoid=261 
					       noinput=true 
					       value="${bp.hylx }" />
				</td>
			</tr>
    
    		<!-- 注册资本 -->
			<tr>
				<td width="150" align="right">注册资本：</td>
      			<td colspan="3">
      				<input name="register" type="text" id="register" title="请输入有效的值"  maxlength="20" value="${bp.register }" size="20" class="validate-currency" />
      					
      				货币类型：
      				<input type="text"
      					   name="curName" 
      				       id="curName" 
      				       title="货币类型" 
      				       value="人民币" 
      				       valuemustexist=true
      				       size="8" 
      				       onkeydown="onKeyDownEvent();" 
      				       onkeyup="onKeyUpEvent();" 
      				       onclick="onPopDivClick(this);" 
      				       autoid="281" 
      				       value="${bp.curName }" />
				</td>
			</tr>
			
			<!-- 客户自定义信息 -->
			
			<tr>
      			<td width="150" align="right">单位简称：</td>
      			<td colspan="3">
      				<input type="text" name="unitSimpleName" id="unitSimpleName" title="单位简称" value="${bp.unitSimpleName }" />
				</td>
			</tr>
		</table>		
	</div>
	
	<!-- 项目成员 -->
	<div id="tab3" class="x-hide-display tabDiv">
		<table width="98%" height="320" cellspacing="10">
			<tr>
				<!-- 项目分工左边的部门人员树 -->
		 
				<!-- 项目分工情况 -->
				<td style="width: 100%;border: 0px solid #99BBE8;" valign="top">
					<div style="height: 320px;overflow-y:auto;">
						<table width="90%" bgcolor="#99BBE8" cellspacing="1" cellpadding="3">
							<tr bgcolor="#99BBE8" align="center" height="22" style="position:relative; top:expression(this.offsetParent.scrollTop);">
								<td bgcolor="#DDE9F9" nowrap="nowrap" align="center" width="30%">姓&nbsp;名</td>
								<td bgcolor="#DDE9F9" nowrap="nowrap" align="center" width="60%">职&nbsp;责</td>
							</tr>
							
							<tbody id="workTBody">
								<c:forEach items="${listBidMemeber}" var="lMemeber">
									<tr id="userTr_${lMemeber.bidMemberId}" class="workClass">
										<td align="center">${lMemeber.bidMemberName}</td>
										<td align="center">
											<input type="text" 
													size="35" 
													id="${lMemeber.bidMemberId}"
													name="duty" 
													class="required" 
													title="请输入有效的值"
													value="${lMemeber.duty}" 
													onkeydown="onKeyDownEvent();" 
													onkeyup="onKeyUpEvent();" 
													onclick="onPopDivClick(this);" 
													autoid=700
													refer="bidProjectDuty"
													useAdvice=true
													style="background-color:#E8FFDF;text-align: center" />
										</td>
										
									</tr>
								</c:forEach>
							</tbody>
							
							<tr class="workClass">
								<td colspan="3">&nbsp;</td>
							</tr>
							
						</table>
					</div>
				</td>
				
			</tr>
		
		</table>
	</div>
	
	<!-- 附件信息 -->
	<div id="tab4" class="x-hide-display tabDiv">
		<table cellpadding="5" width="600" align="left" border="0" cellspacing="10">
			
			<tr>
				<td width="150" align="right">招标文件：</td>
				<td>
					<input type="hidden" id="bidAttachFileId" name="bidAttachFileId" value="${bp.bidAttachFileId }">
					<script type="text/javascript">
						attachInit('bidProject','${bp.bidAttachFileId}',"showButton:false,remove:false");					
					</script>
				</td>
			</tr>
			
			<tr>
				<td colspan="2"></td>
			</tr>
			<tr>
				<td colspan="2"></td>
			</tr>
			<tr>
				<td colspan="2"></td>
			</tr>
			
			<tr>
				<td width="150" align="right">投标截止日期：</td>
				<td>
					<input type="text" name="endDate" id="endDate" class="required" value="${bp.endDate }" />
				</td>
			</tr>
			
		</table>
	</div>
	
	
</div>

<input type="hidden" id="uuid" name="uuid" value="${bp.uuid}" >
<input type="hidden" id="opt" name="opt" value="${opt}" >
<input type="hidden" id="bidMember" name="bidMember" value="${bp.bidMember}">
<input type="hidden" id="bidMemberName" name="bidMemberName" value="${bp.bidMemberName}">
<input type="hidden" id="dutys" name="dutys" value="${bp.duty}">
<input type="hidden" id="bidMemberDepartmentId" name="bidMemberDepartmentId" value="${bidMemberDepartmentId }" >

</form>
</body>

<script type="text/javascript">

var bidMember = document.getElementById("bidMember");
var bidMemberName = document.getElementById("bidMemberName");

var isChangeName = true ; //是否改变项目名称

//初始化
function init() {	
	var today = new Date();
	var year = today.getYear();
	var serviceStartTime = document.getElementById("serviceStartTime");
	var serviceEndTime = document.getElementById("serviceEndTime");

	//如果6月30日前，还是默认前一年度的项目区间
    if(today.getMonth() + 1 < 7 ) {
    	year--;
    }

	//初始化业务区间
	serviceStartTime.value = year + "-01-01";
	serviceEndTime.value = year + "-12-31";
	
	//初始化项目分工
	var user = {
		userId:'${userSession.userId}',
		roleName:'项目经理',
		userName:'${userSession.userName}',
		departmentId:'${userSession.userAuditDepartmentId}',
		isAudit:"1",
		isTarAndPro:"1"
	}
	
	//addUser(user);
	
}

//ext初始化
Ext.onReady(extInit);
Ext.onReady(init);


//////////////////
 
 


//保存项目
function save() {
	 
	//tab1,tab2等分别是div的ID
	//tab.setActiveTab(1); //这个就是定位显示对应的TAB
	isChangeName = false ; 
	if (!formSubmitCheck('tab1')) {
		tab.setActiveTab(0);
		return;
	} else if (!formSubmitCheck('tab2')) {
		tab.setActiveTab(1);
		return;
	} else if (!formSubmitCheck('tab3')) {
		tab.setActiveTab(2);
		return;
	} else if (!formSubmitCheck('tab4')) {
		tab.setActiveTab(3);
		return;
	}  
	
	// 检查职责
	// checkDuty();
	var dutys = document.getElementsByName("duty");
	var temp = "";
	for(var i=0;i<dutys.length;i++){
		temp = temp + dutys[i].value + ",";
	}
	document.getElementById("dutys").value = temp;
	
	var serviceStartTime = document.getElementById("serviceStartTime").value;
	var serviceEndTime = document.getElementById("serviceEndTime").value;
	
	//判断日期是否正确
	if(serviceStartTime > serviceEndTime){
		alert("[开始业务区间]不能大于[结束业务区间]!");
		return;
	}
	
	var form = document.getElementById("thisForm");
	form.action = "${pageContext.request.contextPath}/bidProject.do?method=save";
	
	showWaiting();
	
	form.submit();
}




//更改 基础信息 的客户编号时,如果是选择已有客户，则从后台读取客户信息
function changeaAuditUnit(obj) {
	var auditUnit = obj.value;
	var unitName = document.getElementById("unitName");
	var vocationType = document.getElementById("vocationType");
	var unitEngName = document.getElementById("unitEngName");
	var hylx = document.getElementById("hylx");
	var curName = document.getElementById("curName");
	var register = document.getElementById("register");
	var unitSimpleName = document.getElementById("unitSimpleName");
	
	var auditType = document.getElementById("auditType");
	
	if(auditUnit != "000000" && !isNaN(auditUnit)) {
		var url = "${pageContext.request.contextPath}/customer.do?method=getInfo";
		var requestString = "customerId=" + auditUnit;
		var result = ajaxLoadPageSynch(url,requestString);

		var customer = Ext.util.JSON.decode(result);
		
		if(customer) {
			unitName.value = customer.departName;
			vocationType.value = customer.vocationId;
			unitEngName.value = customer.departEnName;
			hylx.value = customer.hylx;
			curName.value = customer.curname;
			unitSimpleName.value = customer.customerShortName;
			//auditType.value = "";
		}
	} else if(auditUnit == "000000"){
		unitName.value = "";
		vocationType.value = "59";
		unitEngName.value = "";
		hylx.value = "";
		curName.value = "人民币";
		register.value = "";
		unitSimpleName.value = "";
	} else if(isNaN(auditUnit)) {

		setCustomerName();
		
		vocationType.value = "59";
		unitEngName.value = "";
		hylx.value = "";
		curName.value = "人民币";
		register.value = "";
		unitSimpleName.value = "";
	}
}


function setCustomerName() {
	var auditUnit = document.getElementById("auditUnit");
	var unitName = document.getElementById("unitName");
	
	if(isNaN(auditUnit.value)) {

		unitName.value = auditUnit.value;
		auditUnit.value = "000000";
	}
}


function setProjectName() {
	if("${project.projectId}"=="" && isChangeName){
		var customerName = document.getElementById("advice-auditUnit") ;
		if(!customerName || customerName.innerText == "新建客户") {
			return ;
		}
		var va = document.getElementById("serviceStartTime").value;
		var vm = document.getElementById("serviceEndTime").value;
		var vae;
		va = va.substr(0,4);
		vm = vm.substr(0,4);
		if(va!=vm){
			vae = va + "年至"+vm+"年-";
		}else{
			vae = va + "年-";
		}
		var vap = document.getElementById("serviceType").value;
		document.getElementById("projectName").value = customerName.innerText+vae+vap;
	}
}





//增加人员分工
function addUser(obj) {		

	var userId = obj.userId;
	var departmentId = obj.departmentId;
	var trObj = document.getElementById("userTr_" + userId);
	var isAudit = obj.isAudit ? obj.isAudit : "0";
	var isTarAndPro = obj.isTarAndPro ? obj.isTarAndPro : "0";
	
	if(!trObj) {

		//展开树结点
		try {
			if(departmentId == "") {
				departmentId = "0";
			}
	
			var tree = Ext.getCmp("departmentTree");
			var path = "/root/department_" + departmentId + "/user_" + userId;
		
			if(tree) {
				tree.expandPath(path,"id");
			}
		} catch(e) {
			//alert(e);
		}
			
		var tbody = document.getElementById("workTBody");
		trObj = tbody.insertRow();
		trObj.className = "workClass"; 
		trObj.id = "userTr_" + userId;
		
		var tdObj = trObj.insertCell();
		tdObj.align = "center";
		tdObj.innerHTML = obj.userName;
		
		tdObj = trObj.insertCell();
		tdObj.align = "center";
		tdObj.innerHTML = "<input type=\"text\" size=\"35\" style=\"text-align:center\" id=\"bMember"+ userId +"\" value=\"写标书\" name=\"duty\" class=\"required\" onkeydown=\"onKeyDownEvent();\" onkeyup=\"onKeyUpEvent();\" onclick=\"onPopDivClick(this);\" useAdvice=true refer=\"bidProjectDuty\" autoid=700 style=\"background-color:#E8FFDF;\" >";
		
	}
}


//删除用户
function removeUser(obj) {
	
	var userId = obj.userId;

	var trObj = document.getElementById("userTr_" + userId);
	
	if(trObj) {
		var tbody = document.getElementById("workTBody");
		tbody.removeChild(trObj);
	}
}

// 检查职责
function checkDuty(){
	var dutys = document.getElementsByName("duty");
	for(var i=0;i<dutys.length;i++){
		alert(dutys[i].value);
		if(dutys[i].value.trim()==""){
			alert("请填写职责！");
			dutys[i].focus();
			return;
		}
	}
}



function View(){
	var form_obj = document.all; 
	//form的值
	for (i=0;i<form_obj.length ;i++ ) {
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
		//alert(e.tagName);
		if(e.tagName == 'A'){
			//e.style.display = "none";
		}
		if(e.tagName == "IMG"){
			//e.style.display = "none";
		}
		
	}
		
//	document.getElementById("memoTr").style.display = "none";
}

View();
	 
</script>
</html>