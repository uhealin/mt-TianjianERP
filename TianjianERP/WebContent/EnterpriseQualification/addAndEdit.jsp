<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style>
	 
	.tTable td,th {
		height:30px;
	}


</style>
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
          	
          	//最后一点节点并且没子节点
          	if(i == curLength -1) {
          		if(childs[i].childNodes.length <= 0) {
             		 	this.runCallback(callback, scope || this, [this]); 
                      return;   
          		}
          	}
          }   
          
      }, this);   
  }   
});  

var tbar_project;
var tab;
var userEditTree
function setStep(tab) {	
	var btnNext = Ext.getCmp("move-next");
	var btnBack = Ext.getCmp("move-prev");	
	    
    if (tab == 0) {
        btnBack.disable();
    } else {
        btnBack.enable();
    }

    if (tab == 2) {
        btnNext.disable();
    } else {
        btnNext.enable();
    }
}

function tabClose(flag) {
	window.history.back();
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
        			title: "企业资质信息", 
        			listeners: {
        				activate: function(){
        					setStep(0);
        				}
        			}
        		}
        /*
        ,{
        			contentEl: "tab2", 
        			title: "按角色查看",
        			listeners: {
						activate: function(){
        					setStep(1);
        				}
        			}
        		},{
        			contentEl: "tab3", 
        			title: "按人员查看",
        			listeners: {
						activate: function(){
        					setStep(2);
        				}
        			}
        		},{
        			contentEl: "tab4", 
        			title: "按人员维护附件",
        			listeners: {
						activate: function(){
        					setStep(3);
        				}
        			}
        		}
        	*/	
        ],
        bbar:[ '->',
               
               {
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
			},'-',
			
			{
				id:'finish',
				text:'完成',
				handler: save
			}
          ]
	});
	
	var flag = "${param.flag}";
	var text = "返回";
	var icon = "back.gif";
	
	tbar_project = new Ext.Toolbar({
		renderTo: "divBtn",
		items:[
			{ 
				text:text,
				cls:'x-btn-text-icon',
				icon:'${pageContext.request.contextPath}/img/' + icon,
				handler:function(){
					tabClose(flag);
				}
	   		},'-',
	   		{ 
				text:'保存',
				cls:'x-btn-text-icon',
				icon:'${pageContext.request.contextPath}/img/save.gif',
				handler:function(){
					save();
				}
	   		},'->'
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
		 //dataUrl:'${pageContext.request.contextPath}/enterpriseQualification.do?method=getDepartmentList',
		 //dataUrl:'${pageContext.request.contextPath}/interiorEmail.do?method=getUserJsonTree&addUser=true&checked=false',	
		 dataUrl:'department.do?method=getTree&addUser=true&hideOrgan=true&checked=false&hideAreaChecked=true&',
		 baseParams:{joinUser:'${joinUser}',joinUserDepartmentId:'${joinUserDepartmentId}'}
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
		//node.attributes.checked = checked; 
		node.allChildExpand(false,function (){
			
		},this);
		/*
		node.expand(true,false,function(){   
			node.eachChild(function(child) {  
				child.ui.toggleCheck(checked);   
				child.attributes.checked = checked;   
				child.fireEvent('checkchange', child, checked);   
			});
		 });*/
		
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
		 dataUrl:'${pageContext.request.contextPath}/enterpriseQualification.do?method=getRoleList',
		 //baseParams:{roleId:'${powerId}'}
		 baseParams:{roleId:'${joinRole}'}
	});
	
	var treeRole = new TreeRole.TreePanel({
        el:'roleTreeDiv',
        id:'roleTree',
        autoScroll:true,
        animate:true,
        height:320, 
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
         var joinRole = document.getElementById("joinRole");
		// 处理选中的 角色
		if(checked){
			if(joinRole.value.indexOf(node.attributes.roleId)<0 ){
				joinRole.value = joinRole.value + node.attributes.roleId+",";
			}
			
		}else{
			joinRole.value = joinRole.value.replace(node.attributes.roleId+",","");
		}
		
	}, treeRole);  
	
    var rootRole = new TreeRole.AsyncTreeNode({
        text: '角色列表',
        draggable:false,
        id:'root'
    });
    treeRole.setRootNode(rootRole);

    treeRole.render();
    
	var userEditData = new Tree.TreeLoader({
		 //dataUrl:'${pageContext.request.contextPath}/enterpriseQualification.do?method=getDepartmentList',
		 dataUrl:'${pageContext.request.contextPath}/department.do?method=getTree&addUser=true&hideAreaChecked=true&hideOrgan=true&checked=false',	
		 baseParams:{joinUser:'${eqm.maintainUser}',joinUserDepartmentId:'${joinUserDepartmentId}'}
	});
	
	userEditTree = new Tree.TreePanel({
        el:'userTreeDiv',
        id:'userTree',
        autoScroll:true,
        animate:true,
        height:320, 
        rootVisible:false,
        containerScroll: true, 
        loader: userEditData
    });
    /*
    data.on('beforeload',function(treeLoader,node){
		this.baseParams.type = node.attributes.type,
		this.baseParams.departmentId = node.attributes.departmentId
	},data);
    */
    userEditData.on('beforeload',function(treeLoader,node){
		this.baseParams.departid = node.attributes.departid,
		this.baseParams.areaid = node.attributes.areaid,
		this.baseParams.departname = node.attributes.departname,
		this.baseParams.isSubject = node.attributes.isSubject
	},userEditData);
    
    userEditTree.on('checkchange', function(node, checked) {   
		
    	node.allChildExpand(false,function (){
			
		},this);
		/*
		node.attributes.checked = checked; 
		node.expand(true,false,function(){   
			node.eachChild(function(child) {  
				child.ui.toggleCheck(checked);   
				child.attributes.checked = checked;   
				child.fireEvent('checkchange', child, checked);   
			});
		 });*/
		
	}, userEditTree);  
	var userRoot=new Ext.tree.AsyncTreeNode({
		   id:'0',
		   draggable:false,
		   text:'显示全部'
		});
	userEditTree.setRootNode(userRoot);

	userEditTree.render();
	
	mt_form_initAttachFile();

}


</script>
</head>
<body>
<div id="panel"></div>
<div id="divBtn" ></div>
<form name="thisForm" method="post" action="" id="thisForm" class="autoHeightForm">

<br/>	
<div id="divTab" style="overflow:auto">
	<div id="tab1" class="tabDiv">
		<table cellpadding="5" align="left" border="0" cellspacing="10">
			<tr>
				<th width="150" style="text-align: right">标题<span class="mustSpan">[*]</span>：</th>
				<td align="left" colspan="3">
					<input type="text" 
						   name="title" 
						   id="title"
						   title="必填"
						   maxlength="80"  
						   size="50"
						   value="${eqm.title}" class="required" />
				</td>
			</tr>
			
			<tr>
				<th width="150" style="text-align: right">模块名称：</th>
				<td align="left" colspan="3">
					<input type="text"
						   id="modelName"
						   name="modelName"
						   value="${modelName}"
						   maxlength="500"
						   size="50" />
				</td>
			</tr>
			
			<tr>
				<th width="150" style="text-align: right">选择显示人员：</th>
				<td align="left" colspan="3">
				<input type="text" id="joinUser1" name="joinUser1" value="${joinUser1}"  size="50" >
				<a href="javascript:show_selectUser('joinUser1','joinUser')">选择</a>
				<input type="checkbox" id="lookUserAll" name="lookUserAll" <c:if test="${joinUser=='allUser'}">checked</c:if> />&nbsp;全部人员
					
				</td>
			</tr>			
			
			<tr>
				<th width="150" style="text-align: right">附件：</th>
				<td align="left" colspan="3">
					<input type="hidden" 
						   id="attachFileId"
						   name="attachFileId" 
						   ext_type=attachFile
						   attachFile=true
						   maxlength="50" 
						   size="50"   
						   indexTable="${eqm.attachFileId}"
						   value="${eqm.attachFileId}" />
					
				</td>
			</tr>
			

		</table>
	</div>
	
	<!-- 成员 -->
	<div id="tab2" class="x-hide-display tabDiv">
		<table width="98%" height="320" cellspacing="10">
			<tr>
				<!-- 项目分工左边的部门人员树 -->
				<td id="roleTreeDiv" style="overflow-y:auto;" width="30%" valign="top">
				</td>
			</tr>		
		</table>	
	</div>
	
	<!-- 成员 -->
	<div id="tab3" class="x-hide-display tabDiv">
		<div id="lookUserDiv" style="margin-left: 20px;font-weight: 900;"><input type="checkbox" id="lookUserAll1" name="lookUserAll1">&nbsp;全部人员</div>
		<table width="98%" height="320" cellspacing="10">
			<tr>
				<!-- 项目分工左边的部门人员树 -->
				<td id="departmentTreeDiv" style="overflow-y:auto;" width="30%" valign="top">
				</td>
			</tr>		
		</table>	
	</div>
	<div id="tab4" class="x-hide-display tabDiv">
			<div id="editUserDiv" style="margin-left: 20px;font-weight: 900;"><input type="checkbox" id="editUserAll" name="editUserAll">&nbsp;全部人员</div>
			<table width="98%" height="320" cellspacing="10">
			<tr>
				<!-- 项目分工左边的部门人员树 -->
				<td id="userTreeDiv" style="overflow-y:auto;" width="30%" valign="top">
				</td>
			</tr>		
		</table>	
	</div>
	
</div>

<input type="hidden" id="uuid" name="uuid" value="${eqm.uuid}" >
<input type="hidden" id="opt" name="opt" value="${opt}" >
<input type="hidden" id="joinUser" name="joinUser" value="${joinUser}">
<input type="hidden" id="joinRole" name="joinRole" value="${joinRole}">
<input type="hidden" id="powerId" name="powerId" value="">
<input type="hidden" id="powerType" name="powerType" value="">
<input type="hidden" id="joinUserDepartmentId" name="joinUserDepartmentId" value="${joinUserDepartmentId}" >
<input type="hidden" id="maintainUser" name="maintainUser" value="${eqm.maintainUser}">
</form>
</body>

<script type="text/javascript">

//var joinUser = document.getElementById("joinUser");
//var joinRole = document.getElementById("joinRole");

//ext初始化
Ext.onReady(extInit);


//保存项目
function save() {
	 
	//tab1,tab2等分别是div的ID
	//tab.setActiveTab(1); //这个就是定位显示对应的TAB
	if (!formSubmitCheck('tab1')) {
		tab.setActiveTab(0);
		return;
	} else if (!formSubmitCheck('tab2')) {
		tab.setActiveTab(1);
		return;
	}else if (!formSubmitCheck('tab3')) {
		tab.setActiveTab(2);
		return;
	}
	
	var joinUser = document.getElementById("joinUser").value + "";
	
	var lookUserAll = document.getElementById("lookUserAll");
	var usrs = "" ;
	if(lookUserAll.checked){
		usrs = "allUser";
	}else{
		var treeUser = Ext.getCmp("departmentTree");
		var selects = treeUser.getChecked();
		for(var i=0;i<selects.length;i++) {
			if(selects[i].isLeaf()) {
				usrs += selects[i].id + ",";
				usrs = usrs.replace("user_","");
			}
		}
	}
	if(usrs !=""){
		document.getElementById("joinUser").value=usrs;
	}
	joinUser = document.getElementById("joinUser").value;
	
	var joinRole = document.getElementById("joinRole").value + "";
	var treeRole=Ext.getCmp("roleTree");
	var selectRole=treeRole.getChecked();
	var roles="";
	for(var i=0;i<selectRole.length;i++) {
		if(selectRole[i].isLeaf()) {
			roles += selectRole[i].id + ",";
		}
	}
	if(roles !=""){
		document.getElementById("joinRole").value=roles;
	}
	joinRole = document.getElementById("joinRole").value;

	if(joinUser=="" && joinRole==""){
		alert("请选择角色或人员!");
		return;
	}
	/*else if(joinUser!="" && joinRole!=""){
		alert("请不要同时选择角色和人员!");
		return;
	}*/
	else{
	/*
		if(joinUser!=""){
			document.getElementById("powerId").value = document.getElementById("joinUser").value;
			document.getElementById("powerType").value = "人员";
		}
		if(joinRole!=""){
			document.getElementById("powerId").value = document.getElementById("joinRole").value;
			document.getElementById("powerType").value = "角色";
		}
	*/
		var form = document.getElementById("thisForm");
		form.action = "${pageContext.request.contextPath}/enterpriseQualification.do?method=save";
		
		var editUserAll = document.getElementById("editUserAll");
		var usrsMId = "" ;
		if(editUserAll.checked){
			usrsMId = "allUser";
		}else{
			var maintainUser = document.getElementById("maintainUser").value + "";
			var userTree = Ext.getCmp("userTree");
			var selectsUser = userTree.getChecked();
			for(var i=0;i<selectsUser.length;i++) {
				if(selectsUser[i].isLeaf()) {
					usrsMId += selectsUser[i].id + ",";
					usrsMId = usrsMId.replace("user_","");
				}
			}
			if(usrsMId !=""){
				document.getElementById("maintainUser").value  = usrsMId;
			}
		}
		
		showWaiting();
		form.submit();
	}
	
}

	 
</script>
</html>