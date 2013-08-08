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

function setStep(tab) {	
	var btnNext = Ext.getCmp("move-next");
	var btnBack = Ext.getCmp("move-prev");
	    
    if (tab == 0) {
        btnBack.disable();
    } else {
        btnBack.enable();
    }

    if (tab == 3) {
        btnNext.disable();
    } else {
        btnNext.enable();
    }
}


function extInit(){
	
	function navHandler(dir) {
		var i = 0;
		var cur = 0;
		var curTab = tab.getActiveTab();
	
		tab.items.each(function(item) {   
			if(item == curTab) {
				cur = i;
			} 
			
			i++;
		});  
		
		cur += dir;

	    setStep(cur);	
	    tab.setActiveTab(cur);
	}

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
        					setStep(0);
        				}
        			}
        		},{
        			contentEl: "tab2", 
        			title: "客户信息",
        			listeners: {
						activate: function(){
        					setStep(1);
        				}
        			}
        		},{
        			contentEl: "tab3", 
        			title: "参与标书成员",
        			listeners: {
        				activate: function(){
        					setStep(2);
        				}
        			}
        		},{
        			contentEl: "tab4", 
        			title: "标书信息",
        			listeners: {
        				activate: function(){
        					setStep(3);
        				}
        			}
        		}
        ],
        bbar:[ '->',{
				id:'move-prev',
				text:'上一步',
				disabled: true,
				handler: function(){
					navHandler(-1);
				}  
			},'-',{
				id:'move-next',
				text:'下一步',
				handler: function(){
					navHandler(1);
				}
			}
          ]
	});
	
	tbar_project = new Ext.Toolbar({
		renderTo: "divBtn",
		items:[
			{ 
				text:'返回',
				cls:'x-btn-text-icon',
				icon:'${pageContext.request.contextPath}/img/back.gif',
				handler:function(){
					window.history.back();
				}
   			},'-',
   			{ 
				text:"通过",
				cls:'x-btn-text-icon',
				icon:'${pageContext.request.contextPath}/img/confirm.gif',
				handler:function(){
					save_yes();
				}
			},'-',
			{ 
				text:"不通过",
				cls:'x-btn-text-icon',
				icon:'${pageContext.request.contextPath}/img/close.gif',
				handler:function(){
					displayReason();
				}
			}
   		]
    });
      	
 	new Ext.Viewport({
		defaults:{border:false},
		items:[
			tbar_project
		]
	})
	
	var Tree = Ext.tree;
	
	var data = new Tree.TreeLoader({
		 dataUrl:'${pageContext.request.contextPath}/bidProject.do?method=getDepartmentList',
		 baseParams:{bidMemeber:'${bp.bidMember}',bidMemberDepartmentId:'${bidMemberDepartmentId}'}
	});
	
	var tree = new Tree.TreePanel({
        el:'departmentTreeDiv',
        id:'departmentTree',
        autoScroll:true,
        animate:true,
        height:320, 
        rootVisible:false,
        containerScroll: true, 
        loader: data
    });
    
    data.on('beforeload',function(treeLoader,node){
		this.baseParams.type = node.attributes.type,
		this.baseParams.departmentId = node.attributes.departmentId
	},data);
		
	tree.on('checkchange', function(node, checked) {   
		node.expand();   
		
		node.attributes.checked = checked; 
		
		node.eachChild(function(child) {  
			child.ui.toggleCheck(checked);   
			child.attributes.checked = checked;   
			child.fireEvent('checkchange', child, checked);   
		});   
	}, tree);  
	
    var root = new Tree.AsyncTreeNode({
        text: '机构人员列表',
        draggable:false,
        id:'root'
    });
    tree.setRootNode(root);

    tree.render();
	
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
		<td width="10%" style="text-align: right;">投标创建人：</td>
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

<table class="tTable" id="tab_reason" style="display: none">
	<tr>
		<td width="15%" style="text-align: right;">不通过原因：</td>
		<td width="65%" style="text-align: left;"><textarea rows="5" cols="90%" id="reason" name="reason" >${bp.reason }</textarea></td>
		<td width="20%" style="text-align: center"><input type="button" value="确定" onclick="save_no()">&nbsp;&nbsp;&nbsp;<input type="button" value="取消" onclick="f_clear()"></td>
	</tr>
</table>

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
					   readonly="readonly"/> 
				</td>
			</tr>
			
			<!-- 委托机构 -->
			<tr>
				<td width="150" align="right">委托机构<span class="mustSpan">[*]</span>：</td>
				<td>
					<input name="trustOrgan" id="trustOrgan" value="${bp.trustOrgan }" readonly="readonly" >
				</td>
			</tr>
			
			<!-- 业务区间 -->
			<tr>
				<td width="150" align="right">业务区间开始<span class="mustSpan">[*]</span>：</td>
				<td>
					<input type="text" name="serviceStartTime" readonly="readonly" id="serviceStartTime" title="请输入有效的值" value="${bp.serviceStartTime }" />
				</td>
			</tr>
			<tr>
				<td width="150" align="right">业务区间结束<span class="mustSpan">[*]</span>：</td>
				<td>
					<input type="text" name="serviceEndTime" readonly="readonly" id="serviceEndTime" title="请输入有效的值" value="${bp.serviceEndTime }" />
				</td>
			</tr>
			
			
			<tr>
				<td width="150" align="right">业务类型<span class="mustSpan">[*]</span>：</td>
				<td>
					<input type="text"
					   name="serviceType"
					   id="serviceType"
					   value="${bp.serviceType }"
					   readonly="readonly" />
				</td>
			</tr>

			<!-- 项目名称，手工输入 -->
			<tr>
				<td width="150" align="right">项目名称<span class="mustSpan">[*]</span>：</td>
				<td>
					<input type="text" size="50" maxlength="500" title="请输入有效的值" name="projectName" id="projectName" readonly="readonly" value="${bp.projectName }" />
				</td>
			</tr>
			
			
			<!-- 项目简称，手工输入 -->
			<tr>
				<td width="150" align="right">项目简称：</td>
				<td>
					<input type="text" size="25" title="请输入有效的值" name=projectSimpleName id="projectSimpleName" readonly="readonly" value="${bp.projectSimpleName }" />
				</td>
			</tr>
			 
		</table>
	</div>
	
	<!-- 客户信息 -->
	<div id="tab2" class="x-hide-display tabDiv">
		<table cellpadding="5" width="600" align="left" border="0" cellspacing="10">
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
					       readonly="readonly"
	      				   value="${bp.vocationType }" />
				</td>
			</tr>
			
			<!-- 单位英文名称 -->
			<tr>
				<td width="150" align="right">单位英文名称：</td>
				<td>
					<input name="unitEngName" type="text"  id="unitEngName" title="单位英文名称" readonly="readonly" value="${bp.unitEngName }" />
				</td>
			</tr>
			
			<!-- 行业类型 -->
			<tr>
				<td width="150" align="right">行业类型：</td>
				<td>
					<input name="hylx" 
					       id="hylx" 
					       type="text" 
					       readonly="readonly"
					       value="${bp.hylx }" />
				</td>
			</tr>
    
    		<!-- 注册资本 -->
			<tr>
				<td width="150" align="right">注册资本：</td>
      			<td colspan="3">
      				<input name="register" type="text" id="register" title="请输入有效的值"  maxlength="20" value="${bp.register }" size="20" readonly="readonly" />
      					
      				货币类型：
      				<input type="text"
      					   name="curName" 
      				       id="curName" 
      				       title="货币类型" 
      				       readonly="readonly"
      				       value="${bp.curName }" />
				</td>
			</tr>
			
			<!-- 客户自定义信息 -->
			
			<tr>
      			<td width="150" align="right">单位简称：</td>
      			<td colspan="3">
      				<input type="text" name="unitSimpleName" id="unitSimpleName" title="单位简称" readonly="readonly" value="${bp.unitSimpleName }" />
				</td>
			</tr>
		</table>		
	</div>
	
	<!-- 项目成员 -->
	<div id="tab3" class="x-hide-display tabDiv">
		<table width="98%" height="320" cellspacing="10">
			<tr>
				<!-- 项目分工左边的部门人员树 -->
				<td id="departmentTreeDiv" style="overflow-y:auto;" width="30%" valign="top">
				</td>
				
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
													readonly="readonly"
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
					<c:forEach var="aList" items="${attachList}">
						<span>
							 <a id="${aList.attachid}_filename" href="${pageContext.request.contextPath}/common.do?method=attachDownload&attachId=${aList.attachid }" title="下载${aList.attachname}"> 
							 </a>
				   			 &nbsp;<font style="color:#CCCCCC;" id="${aList.attachid}_filesize"></font>
				  		</span>
				  		<script type="text/javascript">
				  			document.getElementById("${aList.attachid}_filesize").innerHTML = formatDecimal('${aList.filesize/1024}',2)+"&nbsp;KB";
				  			document.getElementById("${aList.attachid}_filename").innerHTML = maxString('${aList.attachname}');
				  		</script>
			            &nbsp;<a href="${pageContext.request.contextPath}/common.do?method=attachDownload&attachId=${aList.attachid}" title="下载：${aList.attachname}"><img src="${pageContext.request.contextPath}/img/download.gif"></a>
						<br>
					</c:forEach>
				</td>
			</tr>
			
			<tr>
				<td colspan="2"></td>
			</tr>
			
			<tr>
				<td width="150" align="right">投标截止日期：</td>
				<td>
					<input type="text" name="endDate" id="endDate" readonly="readonly" value="${bp.endDate }" />
				</td>
			</tr>
			
			<tr>
				<td colspan="2" ><hr style="color: #aaa;height: 1"></td>
			</tr>
			<tr>
				<td width="150" align="right">投标文件：</td>
				<td>
					<input type="hidden" id="bidAttachId" name="bidAttachId" value="${bp.bidAttachId }">
					<script type="text/javascript">
						attachInit('bidProject','${bp.bidAttachId}','showButton:false,remove:false');					
					</script>
				</td>
			</tr>
			
		</table>
	</div>
	
	
</div>

<input type="hidden" id="uuid" name="uuid" value="${bp.uuid}" >
<input type="hidden" id="opt" name="opt" value="${opt}" >
<input type="hidden" id="param_opt" name="param_opt" >
<input type="hidden" id="auditStatus" name="auditStatus" value="${bp.auditStatus}">
<input type="hidden" id="bidMember" name="bidMember" value="${bp.bidMember}">
<input type="hidden" id="bidMemberName" name="bidMemberName" value="${bp.bidMemberName}">
<input type="hidden" id="bidMemberDepartmentId" name="bidMemberDepartmentId" value="${bidMemberDepartmentId }" >

</form>
</body>

<script type="text/javascript">

var bidMember = document.getElementById("bidMember");
var bidMemberName = document.getElementById("bidMemberName");

var isChangeName = true ; //是否改变项目名称




//ext初始化
Ext.onReady(extInit);


//////////////////
 
 


//保存项目
function save_yes(){
	 
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
	
	document.getElementById("auditStatus").value = "通过";
	document.getElementById("param_opt").value = "yes";
	
	var form = document.getElementById("thisForm");
	form.action = "${pageContext.request.contextPath}/bidProject.do?method=audit";
	
	showWaiting();
	
	form.submit();
}

function displayReason(){
	var reason = document.getElementById("tab_reason");
	if(reason.style.display=="none"){
	 	document.getElementById("tab_reason").style.display="";
	 	document.getElementById("reason").focus();
	 }else{
	 	document.getElementById("tab_reason").style.display="none";
	 }
}

//保存项目
function save_no(){
	
	var reason = document.getElementById("reason").value + "";
	if(reason.trim() == ""){
		alert("请填写不通过原因！");
		document.getElementById("reason").focus();
		return;
	}
	 
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
	
	document.getElementById("auditStatus").value = "不通过";
	document.getElementById("param_opt").value = "no";
	
	var form = document.getElementById("thisForm");
	form.action = "${pageContext.request.contextPath}/bidProject.do?method=audit";
	
	showWaiting();
	
	form.submit();
}


function f_clear(){
	document.getElementById("reason").value = "";
	document.getElementById("tab_reason").style.display = "none";
}

	 
</script>
</html>