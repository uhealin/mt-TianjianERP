<%@ page language="java" contentType="text/html;charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
    <meta http-equiv="Cache-Control" content="no-store"/>
    <meta http-equiv="Pragma" content="no-cache"/>
    <meta http-equiv="Expires" content="0"/>
    
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/AS_CSS/jpdl.css" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/jpdl.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/jpdlPanel.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/jpdlMain.js"></script>

    <style>        
    
		
		body {
			color: #333;
			font-size: 12px !important;
			font-family: "宋体";
			scrollbar-face-color: #DEE3E7;
			scrollbar-highlight-color: #FFFFFF;
			scrollbar-shadow-color: #DEE3E7;
			scrollbar-3dlight-color: #D1D7DC;
			scrollbar-arrow-color: #006699; 
			scrollbar-track-color: #EFEFEF;
			scrollbar-darkshadow-color: #98AAB1;
		}
		
		.radio {
			height:14px;
			border:!important;
		}
		
		.checkbox {
			height:14px;
			border:!important;
		}
		
		input {
			height: 22px;
			border: 1px solid #CCCCCC;
			padding-top: 3px !important;
			padding-left: 3px !important;
		}
		
		a {
			text-decoration: none;
		}
    </style>
   
    <title>流程配置</title>
    
  </head>
  <body>
  
  
  <div id="attrDiv" style="display: none;font-size: 12px !important;" >
		<br/>
		<table border="0" cellpadding="5" cellspacing="10" width="100%" align="center" style="font-size: 12px !important;">
			<tr>
				<td align="right">名称：</td>
				<td align=left>
					<input type="text" name="nodeName" id="nodeName">
				</td>
			</tr>
			
			<tr id="fromTr">
				<td align="right">对应表单：</td>
				<td align=left>
					<input type="text" name="form" id="form" autoid=10003 noinput=true size=18
						onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" >
					<a href="javascript:;" onclick="showFormAttr()">属性配置</a>
				</td>
				
			</tr>
			
			<tr id="handlerTr">
				<td align="right">处理类：</td>
				<td align=left>
					<select name="handlerClass" id="handlerClass" style="width:180px;">
					<option id="noJava" value="" selected="selected">无JAVA处理类</option>
							<c:forEach items="${classList}" var="class">
								<option value="${class.name}">${class.simpleName}</option>
							</c:forEach>
					</select>
				</td>
				
			</tr>
			
			<tr id="decisionTr" style="display:none;">
				<td align="right">分支处理：</td>
				<td align=left>
					<select id=decisionType name=decisionType onchange="changeDecisionType(this);">
						<option value="表达式" selected="selected">表达式</option>
						<option value="调用类">调用类</option>
					</select>
				</td>
			</tr>
			
			<tr id="decisionTr1" style="display:none">
				<td align="right">表达式：</td>
				<td align=left>
					<input type="text" name="decisionExp" id="decisionExp">
				</td>
			</tr>
			
			<tr id="decisionTr2" style="display:none">
				<td align="right">类路径：</td>
				<td align=left>
					<select name="decisionClass" id="decisionClass" style="width:180px;">
					<option id="noJava" value="" selected="selected">无JAVA处理类</option>
							<c:forEach items="${DecisionClassList}" var="decisionClass">
								<option value="${decisionClass.name}">${decisionClass.simpleName}</option>
							</c:forEach>
					</select>
				</td>
			</tr>
			
			<tr id="subProcessTr" style="display:none">
				<td align="right">子流程：</td>
				<td align=left>
					<input type="text" name="sub-process-key" id="sub-process-key" autoid=24 noinput=true 
						onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" >
				</td>
			</tr>
			
		</table>
		<div id=rightDiv style="display:none">
			<br/><br/>
			<div style="margin:0 20 0 20">权限信息：</div>
			<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
			
			<table border="0" cellpadding="5" cellspacing="10" width="100%" align="center" style="font-size: 12px !important;">
				
				<tr>
					<td align="center" colspan="2">
						<input type=checkbox name="isSelectUser" id="isSelectUser">无需选择下级办理人 
					</td>
				</tr>
				
				<tr>
					<td align="right" nowrap="nowrap">权限类型：</td>
					<td align=left style="width:0">
						<select id=rightType name=rightType onchange="changeRightType(this);">
							<option value="自定义权限">自定义权限</option>
							<option value="候选人">候选人</option>
							<option value="待办人">待办人</option>
							<option value="扩展类">扩展类</option>
						</select>
					</td>
				</tr>
				
				<tr id=departmentTr>
					<td align="right">部门：</td>
					<td align=left>
						<input type="text" name="department" id="department" autoid=10004 multiselect=true noinput=true
						onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" size=18>
					</td>
				</tr>
				
				<tr id=roleTr>
					<td align="right">角色：</td>
					<td align=left>
						<input type="text" name="role" id="role" autoid=10005 multiselect=true noinput=true 
						onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" size=18>
					</td>
				</tr>
				
				<tr id=userTr style="display:none">
					<td align="right">人员：</td>
					<td align=left>
						<input type="text" name="user" id="user" autoid=132 multiselect=true noinput=true 
						onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" size=18>
					</td>
				</tr>
				
				<tr id=expTr style="display:none">
					<td align="right">表达式：</td>
					<td align=left>
						<input type="text" name="candidateExp" id="candidateExp" >
					</td>
				</tr>
				
				<tr id=assigneeTr style="display:none">
					<td align="right">待办人员：</td>
					<td align=left>
						<input type="text" name="assignee" id="assignee">
					</td>
				</tr>
				
				<tr id=classTr style="display:none">
					<td align="right">类路径：</td>
					<td align=left>
						<input type="text" name="rightClass" id="rightClass">
					</td>
				</tr>
			</table>
		</div>
		
		<div id=msgDiv style="display:none">
			<br/><br/>
			<div style="margin:0 20 0 20">消息提醒：</div>
			<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
			
			<table border="0" cellpadding="5" cellspacing="10" width="100%" align="center" style="font-size: 12px !important;">
			
				<tr>
					<td align="right">发起人：</td>
					<td align=left nowrap="nowrap">
						<input type="checkbox" id="sletter" name="sletter" /> 站内信  
						<input type="checkbox" id="sMsg" name="sMsg" /> 手机短信 
					</td>
				</tr>
				
				<tr>
					<td align="right">办理人：</td>
					<td align=left nowrap="nowrap">
						<input type="checkbox" id="dLetter" name="dLetter" /> 站内信 
						<input type="checkbox" id="dMsg" name="dMsg" /> 手机短信
					</td>
				</tr>
			
			</table>
		</div>
	</div>
	
	<div id="processInfDiv" style="display: none;">
		<br/>
		<form name="thisForm" method="post" action="${pageContext.request.contextPath}/process.do?method=saveAndDeploy">
		<table border="0" cellpadding="5" cellspacing="10" width="100%" align="center">
			<tr>
				<td align="right" nowrap="nowrap">流程名称：</td>
				<td align=left>
					<input type="text" name="pname" id="pname" class="required" value="${processDeploy.pname }">
				</td>
			</tr>
			
			<tr>
				<td align="right">描述：</td>
				<td align=left>
					<textarea cols="70" rows="5" id="desccontent" name="desccontent">${processDeploy.desccontent}</textarea> 
				</td>
			</tr>
			   
		</table>
		<input type="hidden" name="id" id="id" value="${processDeploy.id}">
		<input type="hidden" name="jbpmXml" id="jbpmXml" value="${processDeploy.jbpmXml}">
		<input type="hidden" name="isAdd" id="isAdd" value="${isAdd}">
		<input type="hidden" name="isDeploy" id="isDeploy">
		<input type="hidden" name="isNotSelectUserNodes" id="isNotSelectUserNodes"  value="${processDeploy.notSelectUserNodes}"/>
		</form>
	</div>
	
	
	<form name="fieldForm" method="post" action="${pageContext.request.contextPath}/process.do?method=updateFormColumns" target="fieldFrame">
		<div id="filedWin" style="position:absolute;left:expression((document.body.clientWidth-150)/2);top:expression(this.offsetParent.scrollTop +250); z-index: 2"></div>
		
		<input type="hidden" name="node" id="node">
		<input type="hidden" name="processKey" id="processKey"> 
		<input type="hidden" name="formId" id="formId"> 
    </form>
    <iframe id=fieldFrame name=fieldFrame style="display:none;"></iframe>
  </body>
  
  <script type="text/javascript">
  	
  	function changeRightType(obj) {
  		var type = obj.value ;
  		var departmentTr = document.getElementById("departmentTr") ;
  		var roleTr = document.getElementById("roleTr") ;
  		var userTr = document.getElementById("userTr") ;
  		var expTr = document.getElementById("expTr") ;
  		var assigneeTr = document.getElementById("assigneeTr") ;
  		var classTr = document.getElementById("classTr") ;
  		
  		if(type == "自定义权限") {
  			departmentTr.style.display = "" ;
  			roleTr.style.display = "" ;
  			//userTr.style.display = "" ;
  		}else {
  			departmentTr.style.display = "none" ;
  			roleTr.style.display = "none" ;
  			//userTr.style.display = "none" ;
  		}
  		
  		if(type == "候选人") {
  			expTr.style.display = "" ;
  		}else {
  			expTr.style.display = "none" ;
  		}
  		
  		if(type == "待办人") {
  			assigneeTr.style.display = "" ;
  		}else {
  			assigneeTr.style.display = "none" ;
  		}
  		
  		if(type == "扩展类") {
  			classTr.style.display = "" ;
  		}else {
  			classTr.style.display = "none" ;
  		}
  		
  	}
  	
  	function changeDecisionType(obj) {
  		
  		var type = obj.value ;
  		var decisionTr1 = document.getElementById("decisionTr1") ;
  		var decisionTr2 = document.getElementById("decisionTr2") ;
  	
  		if(type == "表达式") {
  			decisionTr1.style.display = "" ;
  		}else {
  			decisionTr1.style.display = "none" ;
  		}
  		
  		if(type == "调用类") {
  			decisionTr2.style.display = "" ;
  		}else {
  			decisionTr2.style.display = "none" ;
  		}
  	}
  	
  	function clearMsgSelect() {
  		document.getElementById("sLetter").checked = false  ;
  		document.getElementById("dLetter").checked = false  ;
  		document.getElementById("sMsg").checked = false  ;
  		document.getElementById("dMsg").checked = false  ;
  	}
  	
  	function setMsgSelect(msg) {
  		
  		if(msg != null && !"".equals(msg)) {
			if(msg.indexOf("sLetter") > -1) {
				//给发起人发送站内短信
				document.getElementById("sLetter").checked = true  ;
				editNode.sLetter = true ;
			}
			if(msg.indexOf("sMsg") > -1) {
				//给发起人发送手机短信
				document.getElementById("sMsg").checked = true  ;
				editNode.sMsg = true ;
			}
			if(msg.indexOf("dLetter") > -1) {
				//给办理人发送站内短信
				document.getElementById("dLetter").checked = true  ;
				editNode.dLetter = true ;
			}
			if(msg.indexOf("dMsg") > -1) {
				//给办理人发送手机短信
				document.getElementById("dMsg").checked = true  ;
				editNode.dMsg = true ;
			}
			
		}
  	}
  	
  	function clearOther() {
  		var department = document.getElementById("department") ;
  		var role = document.getElementById("role") ;
  		var user = document.getElementById("user") ;
  		var candidateExp = document.getElementById("candidateExp") ;
  		var assignee = document.getElementById("assignee") ;
  		var rightType = document.getElementById("rightType").value ;
  		var rightClass = document.getElementById("rightClass").value ;
  		
  		if(rightType == "自定义权限") {
  			candidateExp.value = "" ;
  			assignee.value = "" ;
  			rightClass.value = "" ;
  			
  		}else if(rightType == "候选人") {
  			//department.value = "" ;
  	  		//role.value = "" ;
  	  		//user.value = "" ;
			Ext.getCmp("department").clear();
			Ext.getCmp("role").clear();
			Ext.getCmp("user").clear();
  	  		
  	  		assignee.value = "" ;
  	  		rightClass.value = "" ;
  		}else if(rightType == "待办人") {
  			//department.value = "" ;
  	  		//role.value = "" ;
  	  		//user.value = "" ;
  	  		Ext.getCmp("department").clear();
			Ext.getCmp("role").clear();
			Ext.getCmp("user").clear();
  	  		
  	  		candidateExp.value = "" ;
  	  		rightClass.value = "" ;
  		}else if(rightType == "扩展类") {
  		//	department.value = "" ;
  	  	//	role.value = "" ;
  	  	//	user.value = "" ;
  	  		
	  	  	Ext.getCmp("department").clear();
			Ext.getCmp("role").clear();
			Ext.getCmp("user").clear();
  	  		
  	  		candidateExp.value = "" ;
  	  		assignee.value = "" ;
  		}
  	}
  	
  	Ext.onReady(function (){
  		
  		//初始化xml成图
		var jbpmXml = document.getElementById("jbpmXml").value;
		if(jbpmXml != "") {
			var parse = new Jpdl.xml.JpdlParse(jbpmXml) ;
  	        parse.parse(Jpdl.model) ;
		}
  	}) ;
  
  	function save(isDeploy) {
  		showWaiting();
		document.getElementById("isDeploy").value = isDeploy;
  		var id = document.getElementById("id").value;
  		
		Jpdl.model.key = id;
		Jpdl.model.name = id;
		
  		var jbpmXml = Jpdl.model.serial();
  		
  		var mNodes = Jpdl.model.nodes;
  		var isNotSelectUserNodes = "" ;
  		for(var k in mNodes) {
			var mNode = mNodes[k] ;
			var mNodeName = mNode.name ;
			//不需要选择下级办理人的节点
			if(mNode.nodeName == "task"){
				if(mNode.selectUser == "是"){
					isNotSelectUserNodes += mNodeName + "|" ;
				}
			}
		}
  		
  		if(isNotSelectUserNodes != ""){
  			isNotSelectUserNodes = isNotSelectUserNodes.substr(0,isNotSelectUserNodes.length -1) ;
  		}
  		
  		document.getElementById("isNotSelectUserNodes").value = isNotSelectUserNodes ;
        
  		document.getElementById("jbpmXml").value = jbpmXml;
  		document.all.thisForm.submit();
  	}
  	
  	var preForm ;
  	var preNodeName ;
  	var grid ;
  	var win ;
  	function showFormAttr() {
  		var form = document.getElementById("form").value ;
  		var processKey = document.getElementById("id").value ;
  		var nodeName = document.getElementById("nodeName").value ;
  		
  		if(form == "") {
  			alert("请先选择对应表单!") ;
  			return ;
  		}
  		
  		if(form != preForm || nodeName != preNodeName) {
  			if(grid) grid.destroy();
  			if(win) win.destroy();
  			
  			var store = new Ext.data.JsonStore({
  	  		    url:'${pageContext.request.contextPath}/process.do?method=getFormColumns',
  	  	        fields:['uuid','name','isHide','isReadOnly','isReadOnly','isProcessVariable']
  	  		 });

  	  	    // 创建grid
  	  	    grid = new Ext.grid.GridPanel({
  	  	        store: store,
  	  	        columns: [  
  	  	            {header: "uuid", width: 120, dataIndex: 'uuid', sortable: false,hidden:true,renderer:uuidRender},
  	  	            {header: "列名", width: 220, dataIndex: 'name', sortable: false},
  	  	          	{header: "隐藏<input type='checkbox' onclick=selectAll(this,'isHide');>", width: 120,hidden:true, dataIndex: 'isHide', sortable: false,renderer:hideRender},
  	  	        	{header: "只读<input type='checkbox' onclick=selectAll(this,'isReadOnly');>", width: 120, dataIndex: 'isReadOnly', sortable: false,renderer:readOnlyRender},
  	  	        	{header: "流程变量<input type='checkbox' onclick=selectAll(this,'isProcessVariable');>", width: 120, dataIndex: 'isProcessVariable', sortable: false,renderer:processVariableRender}
  	  	        ]
  	  	    });
  	  	  	store.baseParams={formid:form,processKey:processKey,nodeName:nodeName};
  	  	    store.load();
  	  	    
  	  	    
  	  	  	win = new Ext.Window({
  				title:'表单列属性定义',
  				width:650,
  				height:500,
  		        closeAction:'hide',
  		      	renderTo:'filedWin',
  		        listeners   : {
  		        	'hide':{fn: function () {
  		        		win.hide();	         	
  		        	}}
  		        },
  		        items:[grid],
  		       	layout:'fit',
  		   	 	buttons:[{
  		           	text:'确定',
  		         	handler:function(){
  		         		document.getElementById("processKey").value = processKey ;
  		         		document.getElementById("node").value = nodeName ;
  		         		document.getElementById("formId").value = form ;
  		         		document.fieldForm.submit();
  		         		win.hide();
  		           	}
  		       	},{
  		           	text:'取消',
  		           	handler:function(){
  		           		win.hide();
  		           	}
  		       	}]
  		    }).show();
  	  	  	
  	  		preForm = form ;
  	  		preNodeName = nodeName ;
  		}else {
  			win.show();
  		}
  		
  	}
  	
  	function hideRender(val,metadata,record) {
  		 var checked = " checked = 'checked' " ;
  		 
  		 if(val == "否") {
  			checked = "" ;
  		 }
  		 
  		 return '<input type="checkbox" name="isHideCheck" '+checked+' onclick="checkChange(this,\'isHide' + record.get("uuid") + '\')">' ;
  	}
  	
  	function readOnlyRender(val,metadata,record) {
 		 var checked = " checked = 'checked' " ;
 		 
 		 if(val == "否") {
 			checked = "" ;
 		 }
 		
 		return '<input type="checkbox" name="isReadOnlyCheck" '+checked+' onclick="checkChange(this,\'isReadOnly' + record.get("uuid") + '\')">' ;
 	}
  	
  	function processVariableRender(val,metadata,record) {
		 var checked = " checked = 'checked' " ;
		 
		 if(val == "否") {
			checked = "" ;
		 }
		
		return '<input type="checkbox" name="isProcessVariableCheck" '+checked+' onclick="checkChange(this,\'isProcessVariable' + record.get("uuid") + '\')">' ;
	}
  	
  	
  	function uuidRender(val,metadata,record) {
 		
  		var hiddenHtml = '<input type="hidden" id="uuid' + record.get("uuid") + '" name="uuid" value="' + record.get("uuid") + '">' 
  					   + '<input type="hidden" id="isHide' + record.get("uuid") + '" name="isHide" value="' + record.get("isHide") + '">'
  					   + '<input type="hidden" id="isReadOnly' + record.get("uuid") + '" name="isReadOnly" value="' + record.get("isReadOnly") + '">' 
  					   + '<input type="hidden" id="isProcessVariable' + record.get("uuid") + '" name="isProcessVariable" value="' + record.get("isProcessVariable") + '">' ;
  					   
  		
 		return hiddenHtml ;
 	}
  	
  	function checkChange(obj,hiddenName) {
  		var value = obj.checked ? '是' : '否';
  		document.getElementById(hiddenName).value = value ;
  	}
  	
  	function selectAll(obj,name) {
  		var value = obj.checked ? '是' : '否';
  		var check = obj.checked;
  		var checkInput = document.getElementsByName(name+"Check") ;
  		var hiddenInput = document.getElementsByName(name) ;
  		for(var i=0;i<checkInput.length;i++) {
  			checkInput[i].checked = check ;
  			hiddenInput[i].value = value ;
  		}
  	}
  </script>
  
</html>
