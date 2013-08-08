<%@page import="java.util.UUID"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>	
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>公告管理</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/baiduUeditor/editor_all.js"  ></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/baiduUeditor/editor_config.js"  ></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/baiduUeditor/themes/default/ueditor.css"/>
<script type="text/javascript">
Ext.override(Ext.tree.TreeNode, {   
    allChildExpand : function(animate,callback,scope){   
      // 先展开本节点   
      var checked = this.attributes.checked ;
      var length = this.childNodes.length ;
      var expandCount = 0 ;
      
      this.expand(false, animate, function(){   
          // 展开子节点   
          var childs = this.childNodes ;
          var curLength = childs.length ;
          for(var i = 0; i < curLength; i++) {   
          	
          	childs[i].ui.toggleCheck(checked);   
          	childs[i].attributes.checked = checked;  
          	childs[i].fireEvent('checkchange', childs[i], checked,true);       

          	
          	//最后一点节点并且没子节点
          	if(i == curLength -1) {
          		if(childs[i].childNodes.length <= 0) {
             		 	this.runCallback(callback, scope || this, [this]); 
                      return;   
          		}
          	}
          	
          	/*if(i == curLength -1) {
          		if(childs[i].childNodes.length <= 0) {
             		 	this.runCallback(callback, scope || this, [this]); 
                      return;   
          		}else {
          			childs[i].allChildExpand(false,callback,this);	
          		}
          	}else {
          		childs[i].allChildExpand(false,callback,this);	
          	}*/
          }   
          
      }, this);   
  }   
});  

Ext.override(Ext.tree.TreeNodeUI,{
 toggleCheck : function(value){
   var cb = this.checkbox;
   if(cb){
     cb.checked = (value === undefined ? !cb.checked : value);
   }
 }
});
var tabs ;
Ext.onReady(function () {
	tabs = new Ext.TabPanel({
	    animate:true, 
	    autoScroll:true,
	    containerScroll: true,
	    activeTab:0,  
	    border: true,
        height: 20,
	    rootVisible:false,
	    dropConfig: {appendOnly:true},
	    renderTo: 'my-tabs',
	    activeTab: 0,
	 //   layoutOnTabChange:true, 
	  //  forceLayout : true,
	 //  deferredRender:false,
	 //   defaults: {autoWidth:true,autoHeight:true},
	     items:[
	        {contentEl:'tab1', title:'审批', id:'cur1'},
	        {contentEl:'tab2', title:'修改公告', id:'cur2'}
	    ]
	});
	tabs.on("tabchange",function(tabpanel,tab) {
    	if(tab.id == "cur2") {
    		document.getElementById("prmTable").style.display = "block";		
		} 
    }) ;
	
	tabs2 = new Ext.TabPanel({
	    renderTo: 'my-User',
	    activeTab: 0,
	    layoutOnTabChange:true, 
	    forceLayout : true,
	    deferredRender:false,
	    height: document.body.clientHeight,
	    //width : document.body.clientWidth, 
	    width:200,
	    defaults: {autoWidth:true,autoHeight:true},
	    items:[
	        {contentEl:'user', title:'按人员', id:'cur3'},
	        {contentEl:'role', title:'按角色', id:'cur4'} 
	    ]
	});
	
	new Ext.Toolbar({
		renderTo : "divBtn",
		height : 30,
		defaults : {
			autoHeight : true,
			autoWidth : true
		},
		items : [{
			text : '通过',
			icon : '${pageContext.request.contextPath}/img/start.png',
			handler : function() {
				goAudit();
			}
		},'-',{
			text : '驳回',
			icon : '${pageContext.request.contextPath}/img/reset.gif',
			handler : function() {
				goReject();	 
			}
		},'-',{
			text : '返回',
			icon : '${pageContext.request.contextPath}/img/back.gif',
			handler : function() {
			//	window.history.back();
			  window.location.href="${pageContext.request.contextPath}/proclamationSy.do?method=auditList";
			}
		},'-', {
			text : '关闭',
			icon : '${pageContext.request.contextPath}/img/close.gif',
			handler : function() {
				closeTab(parent.tab);
			}
		}, '->' ]
	});
	
	//tree("treeUserDiv");
	
	var Tree = Ext.tree;
	//document.getElementById("treeUserDiv").innerHTML = "";

	var data=new Tree.TreeLoader({
		//url:'${pageContext.request.contextPath}/proclamationSy.do?method=getTree&addUser=true'  之前注释的
		//url:'${pageContext.request.contextPath}/department.do?method=getTree&addUser=true'	2012-5-8注释
		//dataUrl:'${pageContext.request.contextPath}/interiorEmail.do?method=getUserJsonTree&addUser=true&checked=false',	
		dataUrl:'${pageContext.request.contextPath}/department.do?method=getTree&addUser=true&hideAreaChecked=true&hideOrgan=true&checked=false',	
		baseParams:{joinUser:'${joinUser}',joinUserDepartmentId:'${joinUserDepartmentId}'}
	});
	
	
	var tree = new Tree.TreePanel({
	    animate:true, 
	    autoScroll:true,
	    id:'treeUser',
	    el:'treeUserDiv',
	    containerScroll: true,
	    loader:data,
	    title:'<input type="checkbox" name="allUser" id="allUser">全选人员 &nbsp;<input type="checkbox" name="mobilePhoneMsg" id="mobilePhoneMsg" value="是">手机短信通知',
	    border: true,
        height: document.body.clientHeight-25,
        width:200,
	    rootVisible:false
	    //dropConfig: {appendOnly:true}
	}); 
	
	data.on('beforeload',function(treeLoader,node){
		//&checked=false
		//this.baseParams.checked = false;
		this.baseParams.departid = node.attributes.departid,
		this.baseParams.areaid = node.attributes.areaid,
		this.baseParams.departname = node.attributes.departname,
		this.baseParams.isSubject = node.attributes.isSubject
	},data);
	
	tree.on('checkchange', function(node, checked) {   
		//node.attributes.checked = checked; 
		//node.expand(true,false,function(){   
			/*node.eachChild(function(child) {  
				child.ui.toggleCheck(checked);   
				child.attributes.checked = checked; 
				child.fireEvent('checkchange', child, checked); 
			});*/   
		//});
		node.allChildExpand(false,function (){
			 
		},this);
	}, tree); 
	
	var root=new Ext.tree.AsyncTreeNode({
	   id:'0',
	   //departid:'${departId}',
	   draggable:false,
	   text:'显示全部'
	});
	tree.setRootNode(root);

	//tree.render('treeUserDiv'); 
	//tree.expandAll();   之前就注释了的 
	tree.render();
	//root.expand();
	
	
	createRoleTree("roleDiv");
});

var treeRoleUser;
function createRoleTree(objDiv){
	var Tree = Ext.tree;
	var treeRoleData = new Tree.TreeLoader({
		 dataUrl:'${pageContext.request.contextPath}/interiorEmail.do?method=getRoleList&rand='+Math.random(),
		 baseParams:{roleId:'${joinRole}'}
	});
	treeRoleUser = new Tree.TreePanel({
       // el:objDiv,
        id:'roleTree',
        autoScroll:true,
        animate:true,
        rootVisible:false,
        containerScroll: true, 
        height: document.body.clientHeight-25,
        width: 200,
        bodyStyle:'overflow-y:auto;overflow-x:hidden;',
        loader:treeRoleData
    });
	treeRoleData.on('beforeload',function(treeLoader,node){
		this.baseParams.type = node.attributes.type,
		this.baseParams.departmentId = node.attributes.departmentId
	},treeRoleData);
    treeRoleUser.on('checkchange', function(node, checked) {   
		node.allChildExpand(false,function (){

		},this);
	}, treeRoleUser);  
    var treeRoleRoot = new Tree.AsyncTreeNode({
        text: '角色列表',
        draggable:false,
        id:'root'
    });
    treeRoleUser.setRootNode(treeRoleRoot);
    treeRoleUser.render(objDiv);
    
}
 
</script>
<style type="text/css">

.before{
	border: 0px;
	background-color: #FFFFFF !important;
}

.data_tb {
	background-color: #ffffff;
	text-align:center;
	margin:0 0px;
	width:90%;
	height:98%;
	border:#8db2e3 1px solid; 
	BORDER-COLLAPSE: collapse; 
	margin-top: 20px;
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
</head>
<body style="margin: 0px;padding: 0px;border: 0px;" >
<form action="${pageContext.request.contextPath }/proclamationSy.do?method=auditTransit" name="thisForm" id="thisForm" method="post">
<div id="my-tabs">
<div id="tab1" style="overflow: auto; margin-bottom: 20px;height: 455;">
	<table width="100%" height="100%" style="margin: 0px;padding: 0px;border: 0px;">
	<tr>
		<td   width="200">
		<div id="my-User">
			<div  id="user">
				<div id="treeUserDiv" style="height: 100%; border: 0px;border-style: solid;border-color: #f0f0f0;"></div>
			</div>
			<div id="role">
				<div id="roleDiv" style="overflow:hidden;  border: 0px;border-style: solid;border-color: #f0f0f0;"></div>
			</div>
		</div>
		</td>
		<td>
			<div id="divBtn" style="width: 100%;padding-top: 0px;"> </div>
			<div style="overflow: auto;height:100%">
			<iframe src="${pageContext.request.contextPath}/proclamationSy.do?method=auditDetails&pdid=${pdid}" style="vertical-align: top;height:73%;width:100%;border:0px;">
			</iframe>
				<hr color="#f0f0f0">
					<span style="text-align: center;vertical-align:middle;height:70px; padding-left: 200px;" >备注：</span>
					<textarea id="titleValue" name="titleValue" style="height: 70px;width: 500px;overflow: visible;">通过</textarea>
					<br><br>
				<input type="hidden" name="readUserId" id="readUserId" >
				<input type="hidden" name="pdid" id="pdid"  value="${pdid }">
				<input type="hidden" name="taskId" id="taskId" value="${taskId}">
				<input type="hidden" name="mpMsg" id="mpMsg">
			</div>
		</td>
	</tr>
	</table>
</div>
<div id="tab2">
	<table width="100%" class="data_tb" align="center" id="prmTable" style="display: none;">
	<tr style="display: none;">
		<td class="data_tb_alignright" align="right" width="15%"><font color="red" size=3>*</font>标题：</td>
		<td class="data_tb_content"style="paddingpadding-left: 50px;"><input id="title" type="text" class="required" maxlength="90"
			name="title" title="请输入，不能为空！" size="85"  value="${proclamation.title}" /></td>
	</tr>
	<tr height="20" style="display: none;">
				  <td class="data_tb_alignright" align="right"><font color="red" size=3>*</font>分类：</td>
			      <td  align="left" class="data_tb_content">
			      	<input name="ctype" id="ctype" title="请输入标题" class='required' value="${proclamation.ctype }"
			      	 	noinput=true
			      	 	size="68"
					   title="请输入有效的值"
					   onkeydown="onKeyDownEvent();"
					   onkeyup="onKeyUpEvent();"
					   onclick="onPopDivClick(this);"
					   onchange="setProjectName();"
					   autoid=853
			      	 />
				  </td>
			</tr>
	<tr style="display: none;">
		<td align="right"  style="vertical-align: top;margin-top: 30px;" class="data_tb_alignright">附件：</td>
			<td class="data_tb_content">
				<input type="hidden" id="fileName" name="fileName" value="${proclamation.fileName}">
				<div style="vertical-align: middle;width: 100%;margin-top: 10px;">
				<script type="text/javascript">
				if("${proclamation.fileName}"==""){
					<%
							String fileName=UUID.randomUUID().toString();  //生成uuid
					%>
					document.getElementById("fileName").value="<%=fileName%>";
					attachInit('proclamation','<%=fileName%>');					
				}else{
					attachInit('proclamation','${proclamation.fileName}');
				}
				</script>
				</div>
			</td>
	</tr>
	<tr>
		<td align="right" width="15%" class="data_tb_alignright">
				<div><font color="red" size=3>*</font>内容：</div>
		</td>
		<td   class="data_tb_content">
		      <div style="height:390; overflow: auto; width: 100%;overflow-x:hidden;" > 
				<textarea  id="content" name="content"  style="width: 100%" >${proclamation.content}</textarea>
			   <script type="text/javascript" defer="defer" >
			    var editor = new baidu.editor.ui.Editor({
			    	textarea:'content',
			    	elementPathEnabled : false, //隐藏body
			    	wordCount:false,  //隐藏字符统计
			    	autoFloatEnabled: false 
			    });
			    editor.render("content");
				</script>
				</div><br>
		</td>
	</tr>
</table>
</div>

</div>
</form>
</body>
<script type="text/javascript">
function goReject(){
	 var titleValue = document.getElementById("titleValue").value;
	 if(titleValue == ""){
		 alert("驳回必须填写备注!");
		 return ;
	 }
	if("${taskId}" == ""){
		alert("流程taksId为空！");
		return ;
	}	
	 if(confirm("您确定要驳回此条公告吗?","yes")){
		document.getElementById("thisForm").action = "${pageContext.request.contextPath }/proclamationSy.do?method=auditReject";
		document.getElementById("thisForm").submit();
	 } 
}
function goAudit(){
	var mobilePhoneMsg = document.getElementById("mobilePhoneMsg");
	if(mobilePhoneMsg.checked){
		document.getElementById("mpMsg").value = "是";
	}else{
		document.getElementById("mpMsg").value = "否";
	}
	var allUser = document.getElementById("allUser");
	var usrs = "" ;
	if(allUser.checked){
		usrs = "allUser";
	}else{
		var treeUser = Ext.getCmp("treeUser");
		var selects = treeUser.getChecked();
		for(var i=0;i<selects.length;i++) {
			if(selects[i].isLeaf()) {
				usrs += selects[i].id + ",";
				usrs = usrs.replace("user_","");
			}
		}
		var roleTree = Ext.getCmp("roleTree");
		var roleSel = roleTree.getChecked();
		var userRole = "";
		for(var i=0;i<roleSel.length;i++) {
			if(roleSel[i].isLeaf()) {
				userRole += roleSel[i].id + ",";
			}
		}
		if(userRole != "") {
			userRole = userRole.substr(0,userRole.length-1);
		}
		if(usrs != "") {
			usrs = usrs.substr(0,usrs.length-1);
		}
		
		if(usrs != "") {
			if(userRole != ""){
				usrs = usrs+","+userRole;
			}
		}else{
			usrs = userRole;
		}
		
	}
	document.getElementById("readUserId").value =usrs;
	if(document.getElementById("readUserId").value==""){
		alert("请选择需要阅读的人员！");
		return ;
	}
	if("${taskId}" == ""){
		alert("流程taksId为空！");
		return ;
	}	
	if(!editor.hasContents()){ //判断是否填写了内容
		alert("请编辑内容！");
		return ;
	}  
	if(editor.hasContents()){  //提交条件满足时提交内容
	    editor.sync();
	    showWaiting();
		document.getElementById("thisForm").submit();
	}   
}
</script>
</html> 