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

    if (tab == 4) {
        btnNext.disable();
    } else {
        btnNext.enable();
    }
}

function tabClose(flag) {
	if(flag == "basic") {
		if(parent.tab) {
			parent.tab.remove(parent.tab.getActiveTab());
		} else {
			window.close();
		}
	} else {
		window.history.back();
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
        		},{
        			contentEl: "tab5", 
        			title: "竞争对手",
        			listeners: {
        				activate: function(){
        					setStep(4);
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
			},'-',{
				id:'finish',
				text:'完成',
				handler: save
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
				tabClose(flag);
			}
   		}]
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
		<td width="5%" style="text-align: right;">投标创建人：</td>
		<td width="15%" style="text-align: left;">${bp.createName}</td>
		<td width="5%" style="text-align: right;">创建时间：</td>
		<td width="15%" style="text-align: left;">${bp.createDate}</td>
		<td width="5%" style="text-align: right;">审核人：</td>
		<td width="15%" style="text-align: left;">${bp.auditorName}</td>
		<td width="5%" style="text-align: right;">审核时间：</td>
		<td width="15%" style="text-align: left;">${bp.auditDate}</td>
		<td width="5%" style="text-align: right;">状态：</td>
		<td width="15%" style="text-align: left;">
			<input id="bidStatus" name="bidStatus" value="${bp.bidStatus}"  onkeydown="onKeyDownEvent();" 
   				   onkeyup="onKeyUpEvent();" 
   				   onclick="onPopDivClick(this);"
   				   refer="bidProjectStatus"; 
   				   autoWidth="170px";
   				   autoid=700 >
	    </td>
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
					   maxlength="40"
					   readonly="readonly" /> 
				</td>
			</tr>
			
			<!-- 委托机构 -->
			<tr>
				<td width="150" align="right">委托机构<span class="mustSpan">[*]</span>：</td>
				<td>
					<input name="trustOrgan" id="trustOrgan" value="${bp.trustOrgan }" class="required" readonly="readonly" >
				</td>
			</tr>
			
			<!-- 业务区间 -->
			<tr>
				<td width="150" align="right">业务区间开始<span class="mustSpan">[*]</span>：</td>
				<td>
					<input type="text" name="serviceStartTime" readonly="readonly" id="serviceStartTime" class="required" title="请输入有效的值" value="${bp.serviceStartTime }" />
				</td>
			</tr>
			<tr>
				<td width="150" align="right">业务区间结束<span class="mustSpan">[*]</span>：</td>
				<td>
					<input type="text" name="serviceEndTime" readonly="readonly" id="serviceEndTime" class="required" title="请输入有效的值" value="${bp.serviceEndTime }" />
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
					   class="required" readonly="readonly" />
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
					<input type="text" size="25" title="请输入有效的值" name=projectSimpleName id="projectSimpleName" value="${bp.projectSimpleName }" readonly="readonly" />
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
					<input type="text" size="50" maxlength="200" name="unitName" id="unitName" title="请输入有效的值" value="${bp.unitName }" readonly="readonly" />
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
					       readonly="readonly"
	      				   value="${bp.vocationType }" />
				</td>
			</tr>
			
			<!-- 单位英文名称 -->
			<tr>
				<td width="150" align="right">单位英文名称：</td>
				<td>
					<input name="unitEngName" type="text"  id="unitEngName" title="单位英文名称" value="${bp.unitEngName }" readonly="readonly" />
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
      				<input name="register" type="text" id="register" title="请输入有效的值"  maxlength="20" value="${bp.register }" size="20" readonly="readonly" class="validate-currency" />
      					
      				货币类型：
      				<input type="text"
      					   name="curName" 
      				       id="curName" 
      				       title="货币类型" 
      				       value="人民币"  
      				       value="${bp.curName }" readonly="readonly" />
				</td>
			</tr>
			
			<!-- 客户自定义信息 -->
			
			<tr>
      			<td width="150" align="right">单位简称：</td>
      			<td colspan="3">
      				<input type="text" name="unitSimpleName" id="unitSimpleName" title="单位简称" value="${bp.unitSimpleName }" readonly="readonly" />
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
					<input type="text" name="endDate" id="endDate" class="required" value="${bp.endDate }" readonly="readonly" />
				</td>
			</tr>
			<tr>
				<td colspan="2" ><hr style="color: #aaa;height: 1"></td>
			</tr>
			<tr>
				<td width="150" align="right">投标文件：</td>
				<td>
					<input type="hidden" id="bidAttachFileId" name="bidAttachFileId" value="${bp.bidAttachFileId }">
					<script type="text/javascript">
						attachInit('bidProject','${bp.bidAttachId}','showButton:false,remove:false');					
					</script>
				</td>
			</tr>
			
		</table>
	</div>
	
	<!-- 竞争对手 -->
	<div id="tab5" class="x-hide-display tabDiv">
		
		<table cellpadding="1" align="center" cellspacing="1" width="100%" height="100%" class="tTable">
			<tr>
				<td style="text-align: right;">
					中标方：
				</td>
				<td>
					<input type="text" id="getBidPerson" name="getBidPerson" value="${bp.getBidPerson }" size="40">
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<c:choose>
						<c:when test="${bp.getBidPerson=='' || bp.getBidPerson==null }">
							<input type="radio" name="getBid" onclick="f_fillName('Y');" >我方中标 
							&nbsp;&nbsp;<input type="radio" name="getBid" onclick="f_fillName('N');" checked >非我方中标
						</c:when>
						<c:otherwise>
							<input type="radio" name="getBid" onclick="f_fillName('Y');" <c:if test="${userAuditOfficeName==bp.getBidPerson }">checked</c:if> >我方中标 
							&nbsp;&nbsp;<input type="radio" name="getBid" onclick="f_fillName('N');" <c:if test="${userAuditOfficeName!=bp.getBidPerson }">checked</c:if> >非我方中标
						</c:otherwise>
					</c:choose> 
				</td>
				<td style="text-align: right;">
					中标价：
				</td>
				<td>
					<input type="text" id="getBidPrice" name="getBidPrice" value="${bp.getBidPrice }" class="validate-currency" onkeyup="value=value.replace(/[^\d\.\\-]/g,'')" >
				</td>
			</tr>
		</table>
		
		<table cellpadding="1" align="center" cellspacing="1" width="100%" height="100%" id="tTable" name="tTable" class="tTable">
			<tr>
				<th colspan="6" style="text-align: center;">
					竞争对手信息<a href="#" style="border: 0px;margin-left: 5%" class="a_href" onclick="f_add();">【新 增】</a>
				</th>
			</tr>
		
			<tr>
				<th width="150" style="text-align: center">
					<input type="checkbox" name="father" id="father" <c:if test="${listSize>0}">checked="checked"</c:if>  onclick="f_checkAll()" />
				</th>
				<th width="150" style="text-align: center">竞争对手名</th>
				<th width="150" style="text-align: center">竞争对手报价</th>
				<th width="260" style="text-align: center">竞争对手优势分析</th>
				<th width="260" style="text-align: center">劣势分析</th>
				<th width="150" style="text-align: center">操作</th>
			</tr>
			
			<c:forEach var="bc" items="${bidCompetorList}" >
				<tr>
					<td style="text-align: center">
						<input type="checkbox" name="son"  checked="checked" />
					</td>
					<td style="text-align: center">
						<input type="text" name="bidCompetitor" value="${bc.bidCompetitor }" size="19" 
							   onkeydown="onKeyDownEvent();" 
			   				   onkeyup="onKeyUpEvent();" 
			   				   onclick="onPopDivClick(this);"
			   				   refer="bidProjectStatus"; 
			   				   autoWidth="170px";
			   				   autoid="756" /> 
					</td>
					<td style="text-align: center">
						<input type="text" name="bidCompetitorPrice" value="${bc.bidCompetitorPrice }" size="19"  onkeyup="value=value.replace(/[^\d\.\\-]/g,'')" /> 
					</td>
					<td style="text-align: center">
						<textarea name="bidMemberSuperiority" cols="40%">${bc.bidMemberSuperiority } </textarea> 
					</td>
					<td style="text-align: center">
						<textarea name="bidMemberDisadvantaged" cols="40%">${bc.bidMemberDisadvantaged }</textarea>  
					</td>
					<td style="text-align: center">
						<a href="#" class="a_href" onclick="f_remove(this)">【删 除】</a>  
					</td>
				</tr>
			</c:forEach>
			
		</table>

	</div>
	
</div>

<input type="hidden" id="uuid" name="uuid" value="${bp.uuid}" >
<input type="hidden" id="opt" name="opt" value="${opt}" >
<input type="hidden" id="bidMember" name="bidMember" value="${bp.bidMember}">
<input type="hidden" id="bidMemberName" name="bidMemberName" value="${bp.bidMemberName}">
<input type="hidden" id="bidMemberDepartmentId" name="bidMemberDepartmentId" value="${bidMemberDepartmentId }" >
<input type="hidden" id="isGetBidProject" name="isGetBidProject" value="${bp.isGetBidProject}" >

</form>
</body>

<script type="text/javascript">

var bidMember = document.getElementById("bidMember");
var bidMemberName = document.getElementById("bidMemberName");

var isChangeName = true ; //是否改变项目名称




//ext初始化
Ext.onReady(extInit);


//////////////////

// 权限反选
function f_checkAll(){
	var ck = document.getElementById("father").checked;
	var s = document.getElementsByName("son");
	for(var i=0;i<s.length;i++){
		s[i].checked = ck;
	}
}

// 添加行
function f_add(){
   var table = document.getElementById("tTable");
      //添加一行
      var newTr = table.insertRow();

      //添加两列
      var newTd1 = newTr.insertCell();
      newTd1.className = "data_tb_content";
      var newTd2 = newTr.insertCell();
      newTd2.className = "data_tb_content";
      var newTd3 = newTr.insertCell();
      newTd3.className = "data_tb_content";
      var newTd4 = newTr.insertCell();
      newTd4.className = "data_tb_content";
      var newTd5 = newTr.insertCell();
      newTd5.className = "data_tb_content";
      var newTd6 = newTr.insertCell();
      newTd6.className = "data_tb_content";
      
      // 设置列内容和属性
      newTd1.innerHTML = "<input type='checkbox' name='son' >"; 
      newTd2.innerHTML = "<input type='text' name='bidCompetitor' onkeydown='onKeyDownEvent();' onkeyup='onKeyUpEvent();' onclick='onPopDivClick(this);' autoWidth='170px' autoid='756' >";
      newTd3.innerHTML = "<input type='text' name='bidCompetitorPrice' onkeyup='f_money(this);' > ";
      newTd4.innerHTML = "<textarea name='bidMemberSuperiority' cols='40%' ></textarea>";
      newTd5.innerHTML = "<textarea name='bidMemberDisadvantaged' cols='40%' ></textarea>";
      newTd6.innerHTML = "<a href='#' class='a_href' onclick='f_remove(this)'>【删 除】</a>";
      
}


// 删除
function f_remove(t){
	if(confirm("您确定要删除吗?")){
		t.parentNode.parentNode.removeNode(true);
	}
}

// 检验 选中的行 的竞争对手是否 为空
function f_checkNull(){
	var son = document.getElementsByName("son");	
	var sonlength = son.length;
	var bidCompetitor = document.getElementsByName("bidCompetitor");	
	// 动态 表格 注意 一下 循环
	for(var i=sonlength-1;i>=0;i--){
		if(son[i].checked){
			if(bidCompetitor[i].value=="" || bidCompetitor[i].value.trim()=="" ){
				alert("请填写竞争对手名称！");
				bidCompetitor[i].focus();
				return false;
			}
		}else{
			if(bidCompetitor[i].value=="" || bidCompetitor[i].value.trim()=="" ){
				bidCompetitor[i].parentNode.parentNode.removeNode(true);
			}else{
				son[i].checked = "checked" ;
			}
		}
		
	}
	return true;
}



//保存项目
function save() {
	
	if(!f_checkNull()){
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
	} else if (!formSubmitCheck('tab5')) {
		tab.setActiveTab(4);
		return;
	}
	
	var bidStatus = document.getElementById("bidStatus").value;
	var form = document.getElementById("thisForm");
	form.action = "${pageContext.request.contextPath}/bidProject.do?method=saveCompetitor";
	if(confirm("确定把该招投标状态设置为："+bidStatus)){
		showWaiting();
		
		form.submit();
	}
}


// 验证数字
function checkNum(obj){ 
	var re = /^-?[1-9]+(\.\d+)?$|^-?0(\.\d+)?$|^-?[1-9]+[0-9]*(\.\d+)?$/; 
    if (!re.test(obj.value)){ 
        alert("非法数字"); 
  		obj.value=""; 
        return false; 
    } 
}  


if("${userAuditOfficeName}"=="${bp.getBidPerson }"){
	document.getElementById("getBidPerson").readOnly = true;
}

function f_fillName(t){
	if(t=="Y"){
		document.getElementById("isGetBidProject").value = "是";
		document.getElementById("getBidPerson").value = "${userSession.userAuditOfficeName}";
		document.getElementById("getBidPerson").readOnly = true;
	}else{
		document.getElementById("isGetBidProject").value = "否";
		document.getElementById("getBidPerson").value = "${bp.getBidPerson}";
		document.getElementById("getBidPerson").readOnly = false;
	}
}

function f_money(t){
	t.value = t.value.replace(/[^\d\.\\-]/g,'');
} 
</script>
</html>