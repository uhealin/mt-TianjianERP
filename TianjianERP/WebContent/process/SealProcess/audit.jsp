<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>公章审批</title>
<script type="text/javascript">
var tabs;
 var result;
	Ext.onReady(function() {
 var url = "${pageContext.request.contextPath}/sealNotFlow.do?method=getAccessory";
 var requestString = "&table=seal&uuid=${seal.fileName}";
 result = ajaxLoadPageSynch(url,requestString);
	 
		//if("${activeName}".indexOf("电子章")>-1){
	   if(result==""){
			var mytab = new Ext.TabPanel({
		        id: "tab",
		        renderTo: "divTab",
		        activeTab:0, //选中第一个 tab
		        autoScroll:true,
		        frame: true,
		        height: document.body.clientHeight-Ext.get('divTab').getTop(), 
		        defaults: {autoHeight: true,autoWidth:true},
		        items:[
		            {contentEl: "sealContent",title:"申请信息",id:"sealContentTab"}         
		        ]
		    });
		
			// }
		//if("${activeName}".indexOf("电子章")>-1){
			
			//fileOpen("${attachFileName}","${seal.fileName}") ;
			//}

		
		new Ext.Toolbar({
			renderTo : "divBtn",
			height : 30,
			defaults : {
				autoHeight : true,
				autoWidth : true
			},
			items : [ {
				id : 'saveBtn',
				text : '通过',
				icon : '${pageContext.request.contextPath}/img/start.png',
				handler : function() {
					//mytab.setActiveTab(0);
					goAudit();
				}
			},'-', {
				text :  '结束',
				icon : '${pageContext.request.contextPath}/img/close.gif',
				handler : function() {
					 endSeal();
				}
			}, '-', {
				text : '返回',
				icon : '${pageContext.request.contextPath}/img/back.gif',
				handler : function() {
					mytab.setActiveTab(0);
					showWaiting();//等待提示
					AuditReport.funCloseFile();
					//window.history.back();
					window.location.href="${pageContext.request.contextPath}/seal.do?method=auditList";
				}
			},
			 '->' ]
		});
	}else{
	   			var mytab = new Ext.TabPanel({
		        id: "tab",
		        renderTo: "divTab",
		        activeTab:0, //选中第一个 tab
		        autoScroll:true,
		        frame: true,
		        height: document.body.clientHeight-Ext.get('divTab').getTop(), 
		        defaults: {autoHeight: true,autoWidth:true},
		        items:[
		            {contentEl: "sealContent",title:"申请信息",id:"sealContentTab"}
	
			         //,{contentEl: "sealFile",title:"附件",id:"sealFileTab"} 
     	         
		        ]
		        
		    });
			// }
		//if("${activeName}".indexOf("电子章")>-1){
			
			//fileOpen("${attachFileName}","${seal.fileName}") ;
			//}

	  			//fileOpen("${attachFileName}","${seal.fileName}");
		
		new Ext.Toolbar({
			renderTo : "divBtn",
			height : 30,
			defaults : {
				autoHeight : true,
				autoWidth : true
			},
			items : [ {
				id : 'saveBtn',
				text : '通过',
				icon : '${pageContext.request.contextPath}/img/start.png',
				handler : function() {
					if (!formSubmitCheck('thisForm')) return;
					mytab.setActiveTab(0);
					goAudit();
				}
			},'-',{
				text : '查看附件',
				icon : '${pageContext.request.contextPath}/img/mytask.gif',
				handler : function() {
				   //fileOpen("${attachFileName}","${seal.fileName}");
				       var url = 'seal.do?method=filePage&file=${seal.attachname}&uuid=${seal.uuid}&rand='+Math.random();			
			           parent.openTab("fileId","附件",url);
				}
			}, '-', {
				text : '结束',
				icon : '${pageContext.request.contextPath}/img/close.gif',
				handler : function() {
					endSeal();
				}
			},	'-', {
				text : '返回',
				icon : '${pageContext.request.contextPath}/img/back.gif',
				handler : function() {
					mytab.setActiveTab(0);
					showWaiting();//等待提示
					AuditReport.funCloseFile();
					window.location.href="${pageContext.request.contextPath}/seal.do?method=auditList";
					//window.history.back();
				}
			},
			 '->' ]
		});
	}
	   if("${activeName}".indexOf("分支机构办公室")>-1){ 
		   
		   document.getElementById("tab3").style.display="block";
			tabs = new Ext.TabPanel({
			    renderTo: 'my-tabs',
			    activeTab: 0,
			    layoutOnTabChange:true, 
			    forceLayout : true,
			    deferredRender:false,
			    defaults: {autoWidth:true,autoHeight:true},
			    items:[
			        {contentEl:'tab1', title:'按人员', id:'cur1'},
			        {contentEl:'tab2', title:'按角色', id:'cur2'},
			        {contentEl:'tab3', title:'选择审核人', id:'cur3'}
			        
			    ]
			});
			tabs.setActiveTab(2);
	   }else{
			tabs = new Ext.TabPanel({
			    renderTo: 'my-tabs',
			    activeTab: 0,
			    layoutOnTabChange:true, 
			    forceLayout : true,
			    deferredRender:false,
			    defaults: {autoWidth:true,autoHeight:true},
			    items:[
			        {contentEl:'tab1', title:'按人员', id:'cur1'},
			        {contentEl:'tab2', title:'按角色', id:'cur2'}
			        
			    ]
			});
	   }
		
		  var Tree = Ext.tree;
			
		var data = new Tree.TreeLoader({
		 //dataUrl:'${pageContext.request.contextPath}/interiorEmail.do?method=getUserJsonTree&addUser=true&checked=false',	
		 dataUrl:'${pageContext.request.contextPath}/department.do?method=getTree&addUser=true&hideAreaChecked=true&hideOrgan=true&checked=false',
		 baseParams:{joinUser:'${joinUser}',joinUserDepartmentId:'${joinUserDepartmentId}'}
	});
	
	var tree = new Tree.TreePanel({
        el:'userTreeDiv',
        id:'userTree',
        autoScroll:true,
        animate:true,
        height:320, 
        rootVisible:false,
        containerScroll: true, 
        loader: data
    });
    /*
    data.on('beforeload',function(treeLoader,node){
		this.baseParams.type = node.attributes.type,
		this.baseParams.departmentId = node.attributes.departmentId
	},data);
    */
    data.on('beforeload',function(treeLoader,node){
		this.baseParams.departid = node.attributes.departid,
		this.baseParams.areaid = node.attributes.areaid,
		this.baseParams.departname = node.attributes.departname,
		this.baseParams.isSubject = node.attributes.isSubject
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
	/*
    var root = new Tree.AsyncTreeNode({
        text: '机构人员列表',
        draggable:false,
        id:'root'
    });
	*/
	var root=new Ext.tree.AsyncTreeNode({
		   id:'0',
		   draggable:false,
		   text:'显示全部'
		});
    tree.setRootNode(root);

    tree.render(); 
				
				
				// 角色
				var TreeRole = Ext.tree;
				
				var dataRole = new TreeRole.TreeLoader({
					 //dataUrl:'${pageContext.request.contextPath}/enterpriseQualification.do?method=getRoleList',
					 dataUrl:'${pageContext.request.contextPath}/interiorEmail.do?method=getRoleList',
					 baseParams:{roleId:'${joinRole}'}
				});
				
				var treeRole = new TreeRole.TreePanel({
			        el:'roleTreeDiv',
			        id:'roleTree',
			        autoScroll:true,
			        animate:true,
			        height:300, 
			        rootVisible:false,
			        containerScroll: true, 
			        loader: dataRole
			    });
			    dataRole.on('beforeload',function(treeLoader,node){
					this.baseParams.type = node.attributes.type,
					this.baseParams.departmentId = node.attributes.departmentId
				},dataRole);
					
				treeRole.on('checkchange', function(node, checked) {   
					node.expand();   
					node.attributes.checked = checked; 
					
					node.eachChild(function(child) {  
						child.ui.toggleCheck(checked);   
						child.attributes.checked = checked;   
						child.fireEvent('checkchange', child, checked);   
					});   
					
					node.eachChild(function(child) {  
						child.ui.toggleCheck(checked);   
						child.attributes.checked = checked;   
						child.fireEvent('checkchange', child, checked);   
					});   
					
				}, treeRole);  
				
			    var rootRole = new TreeRole.AsyncTreeNode({
			        text: '角色列表',
			        draggable:false,
			        id:'root'
			    });
			    treeRole.setRootNode(rootRole);

			    treeRole.render();
	});
    //结束印章流程
    function endSeal(){
    	if(confirm("您确定要结束次印章流程吗？结束后此次申请将作废掉，无法重新再次发起!","yes")){
    		
	       	document.getElementById("thisForm").action="${pageContext.request.contextPath}/seal.do?method=end";
	        document.getElementById("thisForm").submit();
    	}
    }
	//window 面板 进行查询
	function queryWinFun(obj){
		
		var queryWin = null;
		resizable:false;
		var searchDiv = document.getElementById("divCheck_select") ;
		searchDiv.style.display = "block" ;
		document.getElementById("userTreeDiv").style.display = "block"; //人员树
		if(!queryWin) { 
		    queryWin = new Ext.Window({
				title: '发送系统通知',
				resizable:false,   //禁止用户 四角 拖动
				contentEl:'divCheck_select',
		     	//renderTo : searchWin,
		     	width: 345,
		     	height:400,
	        	closeAction:'hide',
	       	    listeners : {
		         	'hide':{
		         		fn: function () {
		         			new BlockDiv().hidden();
							queryWin.hide();
						}
					}
		        },
	        	layout:'fit',
		    	buttons:[
		    	         '&nbsp;&nbsp;<input type="checkbox" name="sjdx" id="sjdx" value="是">手机短信 &nbsp; <input type="checkbox" name="znxx" id="znxx" checked value="是"> 站内消息',
		    	{
	            	text:'确认',
	          		handler:function(){
	          			
	          			//选择人员
						var userId = getUrsValue("userTree","id");//Id
						var userName = getUrsValue("userTree","name"); //name
						//setUser(obj,userId,userName);
						//document.getElementById("userTreeDiv").style.display = "none" ;
						
						//选择角色
						
						var roleIds = getUrsValue("roleTree","id");//Id
						var roleUserName = getUrsValue("roleTree","name"); //name
						if(userId ==""){
							userId=roleIds;	//其实是存的人员ID，这个第二版
						}else{
							userId+=roleIds;
						}
						
						if(userName ==""){
							userName=roleUserName;
						}else{
							userName+=roleUserName;
						}
						document.getElementById("msgUserId").value = userId ;
						if(document.getElementById("sjdx").checked){
							document.getElementById("mobilePhoneMsg").value=document.getElementById("sjdx").value;
						};
						if(document.getElementById("znxx").checked){
							document.getElementById("instationMsg").value=document.getElementById("znxx").value;
						};
						 
						document.getElementById("auditUserId").value=document.getElementById("shUserId").value;
						if("${activeName}".indexOf("分支机构办公室")>-1){ 
							if(document.getElementById("auditUserId").value ==""){
								alert("请选择下一步审核人!");
								return ;
							}
						}
						showWaiting();//等待提示
						document.getElementById("thisForm").action="${pageContext.request.contextPath}/seal.do?method=audit";
						document.getElementById("thisForm").submit();
						
						queryWin.hide();
						
	            	}
	        	},{
	            	text:'取消',
	            	handler:function(){
	               		queryWin.hide();
	               		//document.getElementById("userTreeDiv").style.display = "none" ;
	            	}
	        	}]
		    });
	    }
	    new BlockDiv().show();
 
	    queryWin.show();
	}

</script>

<style type="text/css">

.data_tb {
	background-color: #ffffff;
	text-align: center;
	margin: 0 0px;
	width: 80%;
	border: #8db2e3 1px solid;
	BORDER-COLLAPSE: collapse;
	margin-top: 20px;
}

.data_tb_alignright {
	BACKGROUND: #e4f4fe;
	white-space: nowrap;
	padding: 5px;
	border-top: #8db2e3 1px solid;
	border-left: #8db2e3 1px solid;
	BORDER-RIGHT: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid;
	height: 30px;
	background-color: #d3e1f1;
	font-size: 13px;
	font-family: "宋体";
}

.data_tb_content {
	PADDING-LEFT: 2px;
	BORDER-TOP: #8db2e3 1px solid;
	BORDER-LEFT: #8db2e3 1px solid;
	BORDER-RIGHT: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid;
	WORD-BREAK: break-all;
	TEXT-ALIGN: center;
	WORD-WRAP: break-word
}
</style>
</head>
<body >
<div id="divBtn"></div>
	<form name="thisForm" method="post" action="" id="thisForm">
<div id=divTab>
  <div id="sealContent">
	<div style="height: 65%;width:100%;position:relative; overflow:auto;">
		<input type="hidden" id="msgUserId" name="msgUserId" >
		<input type="hidden" id="taskId" name="taskId" value="${taskId}">
		<input type="hidden" id="pdId" name="pdId" value="${pdId}">
		<input type="hidden" id="uuid" name="uuid" value="${uuid}">
		<input type="hidden" id="activeName" name="activeName" value="${activeName}">
		<input type="hidden" id="instationMsg" name="instationMsg">
		<input type="hidden" id="mobilePhoneMsg" name="mobilePhoneMsg">
		<input type="hidden" id="auditUserId" name="auditUserId">
		<span class="formTitle"><br> 公章申领信息</span>
	
	<table border="0" style="line-height: 28px" class="data_tb"
		align="center">
		<tr>
			<td colspan="4" style="height: 15px;" class="data_tb_alignright">
			公章申领信息</td>
		</tr>
		<tr>
			<td align="right" width="20%" class="data_tb_alignright">申请人：</td>
			<td class="data_tb_content" width="30%" align="left">
				 ${seal.userId} 
			</td>
	
			<td align="right" width="20%" class="data_tb_alignright">申请时间：</td>
			<td class="data_tb_content" align="left">
				${seal.applyDate} 
			</td>
		</tr>
		<tr>
			<td align="right" width="20%" class="data_tb_alignright">申请事项：</td>
			<td class="data_tb_content" align="left">
				 ${seal.matter} 
			</td>
	
			<td align="right" width="20%" class="data_tb_alignright">公章类型：</td>
			<td class="data_tb_content" align="left">
				 ${seal.ctype} 
			</td>
		</tr>
		<tr>
			<td align="right" width="20%" class="data_tb_alignright">申请份数：</td>
			<td class="data_tb_content" align="left">
				<input type="text" id="sealCount" name="sealCount" value="${seal.sealCount}" size="10" class="required validate-digits">  
			</td>
	
			<td align="right" width="20%" class="data_tb_alignright">使用部门：</td>
			<td class="data_tb_content" align="left">
				<input  type="text" name="applyDepartName" id="applyDepartName" class="required" onfocus="onPopDivClick(this);"
				onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();"
				onclick="onPopDivClick(this);" 
				autoid=4402
				value="${seal.applyDepartId}" />
			</td>
		</tr>
		<tr>
			<td align="right" width="20%" class="data_tb_alignright">备注：</td>
			<td class="data_tb_content" style="text-align: left ;" colspan="3">
				 ${seal.remark} 
			</td>
	 
		</tr>
	</table >
		<div style="width: 90%;margin-top:20px;text-align: left;padding-left: 150px;">
			<table>
				<tr>
					<td width="50px;">附件：</td>
					<td>
					<script type="text/javascript">
						attachInit('seal','${seal.fileName}',"showButton:false,remove:false");
					</script>
					</td>
				</tr>
			</table>
		 </div>
		<br><br>
		<c:forEach items="${nodeList}" var="node">
			<table border="0" cellSpacing="0" cellPadding="0" width="80%"
				align="center">
				<tr>
					<td width="100%" align="middle"><img
						src="${pageContext.request.contextPath}/images/downline.jpg"></td>
				</tr>
			</table>
			<table border="0" cellSpacing="1" cellPadding="2" width="80%"
				bgColor="#99BBE8" align="center" class="appTable">
				<tr bgColor="#DDE9F9">
					<td colSpan="2" style="height: 25px;"><b><span style="width: 30%;margin-left: 10px;">${node.nodeName}</span>
					<span style="width: 15%;">处理人：${node.dealUserId}</span> <span
						style="width: 25%;">处理时间：${node.dealTime}</span> </b></td>
				</tr>
				<c:forEach items="${node.formList}" var="form">
		
					<tr bgColor="#ffffff" style="height: 20px;">
						<td width="20%" align="right">${form.key}：</td>
						<td width="80%" style="padding-left: 5px;"><c:choose>
							<c:when test="${form.property != ''}">
								<a href=# onclick="fileOpen('${form.property}','${form.value}')">${form.value}
								</a>
							</c:when>
							<c:otherwise>
											${form.value}
										</c:otherwise>
						</c:choose></td>
					</tr>
				</c:forEach>
			</table>
		</c:forEach>
	</div>
		<br>
		<br>
		<div align="center">
			审批意见：<textarea style="height: 100px;width: 500px;overflow: visible;" id="remark" name="remark" >通过</textarea>
		</div>
		<div style="height: 50px;"> </div>
	</div>
	
	 <div id="sealFile" class="x-hide-display" style="height:expression(document.body.clientHeight-62);width:100%">
			
			<object classid="clsid:C20572B8-6104-45B8-A3EE-303B42C26ABF" id="oframe" width="100%" height="100%">
	         	<param name="BorderStyle" value="1">
	        	<param name="SideBarVisible" value="0">
	       	 	<param name="Titlebar" value="0">
	      	   	<param name="Menubar" value="1">
	       </object>
			
	</div>
	
	<div id="divCheck_select" style="display: none;">
		<div id="my-tabs" >
			<div id="tab1">
				<div id="userTreeDiv" style="width: 330px;height: 300px;overflow: hidden;"></div>
			</div>
			
			<div id="tab2">
				<div id="roleTreeDiv" style="width: 330px;height: 300px;overflow: hidden;"></div>
			</div>
			<div id="tab3" style="height: 300px;display: none;">
				<fieldset style="width: 95%;height: 280px;margin-left: 15px;"><legend>选择审核人</legend>
				<div id="" style="margin-left: 20px;margin-top: 10px;">
				下一步审核人：
					<input name="shUserId" id="shUserId"
						      	   noinput=true
								   onkeydown="onKeyDownEvent();"
								   onkeyup="onKeyUpEvent();"
								   onclick="onPopDivClick(this);"
								   onchange="setProjectName();"
								   autoid=4610
						      	 />
				</div>
				</fieldset>
			</div>
		</div>
		
	</div>
</div>

</form>

<script type="text/javascript">
	new Validation('thisForm');
	
  
  if(result!="" ){	
	 	var AuditReport =  new ActiveXObject("AuditReportPoject.AuditReport");
		AuditReport.pUTF8=true;
		var oframe = document.getElementById("oframe");
		AuditReport.pDSOFramer=oframe;
		
	}
	
	function closeFile(){
		try{
			AuditReport.funCloseFile();
			//AuditReport.FunCloseFile();
	}catch(e){}
	}
	
function getUrsValue(obj,objType) {
	var tree = Ext.getCmp(obj);
	var selects = tree.getChecked();
	var usrs = "" ;
	for(var i=0;i<selects.length;i++) {
		if(selects[i].isLeaf()) {
			if(objType =="id"){
				usrs += selects[i].id + ",";
				usrs = usrs.replace("user_","");
			}else if(objType =="name"){
				if(obj == "userTree"){
					usrs += selects[i].attributes.username  + ",";
				}else{
					usrs += selects[i].attributes.userName  + ",";
				}
			}
		}
	}
	//if(usrs != "") {
		//usrs = usrs.substr(0,usrs.length-1);
	//}
	return usrs;
}
 
	new Validation("thisForm");
	
	setObjDisabled("applyDepartName");
	
	//if("${activeName}".indexOf("电子章")>-1){
		var AuditReport =  new ActiveXObject("AuditReportPoject.AuditReport");
		AuditReport.pUTF8=true;
		var oframe = document.getElementById("oframe");
		AuditReport.pDSOFramer=oframe;
	//}
	function lookCase(p){
		AuditReport.pShowRev=p;
	}
	
	 
	function fileOpen(fileName,textFileName) {
		AuditReport.pOpenMode=0;
		AuditReport.pFileName = fileName; 
		AuditReport.pAppUser="${userSession.userName}";
		AuditReport.pTrackRev=true;
		//查看终稿、
		//AuditReport.pShowRev=false;
		//查看修改痕迹，用
		//AuditReport.pShowRev=true;
		
		var locaHost = window.location.host;

		AuditReport.pFileDir="c:\\manu\\workflow" ;
		//AuditReport.pZipByClient=true; //
		AuditReport.pOpenUrl="http:\/\/"+locaHost + "${pageContext.request.contextPath}/seal.do?method=downloadFile&textFileName="+textFileName+"&indexTable=seal";
		AuditReport.pSaveUrl="http:\/\/"+locaHost + "${pageContext.request.contextPath}/seal.do?method=uploadFile&fileTempName=${attachid}&indexTable=seal&uuid="+textFileName;
		AuditReport.pPrintCountUrl="http:\/\/"+locaHost + "${pageContext.request.contextPath}/AS_SYSTEM/getPrintCount.jsp?uuid=${seal.uuid}";
		var UrlParameter="&manuname=${attachid}";
		AuditReport.pUrlParameter="http:\/\/"+window.location.host + "|" + UrlParameter;
		AuditReport.funOpenUrlFile();
	} 
 
	function goAudit()
	{

		queryWinFun();
		//if("${activeName}".indexOf("电子章")>-1){
			
			
		//}	
		
	} 

</script>

</body>
</html>
