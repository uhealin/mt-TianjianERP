<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>角色授权</title>	

<script Language=JavaScript>

	function ext_init(){
	    
	    var tbar = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		defaults: {autoHeight: true,autoWidth:true},
            items:[{
	            text:'授权',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/save.gif',
	            handler:function(){
	            	goSave();
				}
       		},'-',{
	            text:'返回',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/back.gif',
	            handler:function(){
					goBack();
				}
       		},'->'
       		${menuLocation}
			]
        });
        
	}
	
	//单位/部门树
	var tree;
	var root;
	var businessWin ;
	var powerid;
	function tree(divName){
		var Tree = Ext.tree;
		
		document.getElementById(divName).innerHTML = "";

		var data=new Ext.tree.TreeLoader({
			url:'${pageContext.request.contextPath}/department.do?method=getTree'
		});
		
		tree = new Tree.TreePanel({
		    animate:true, 
		    autoScroll:true,
		    containerScroll: true,
		    loader:data,
		    border: false,
		    width: 300,
	        height: 280,
		    rootVisible:false,
		    dropConfig: {appendOnly:true}
		    
		}); 
		
		data.on('beforeload',function(treeLoader,node){
			//&checked=false
			//alert(powerid);
			this.baseParams.userpopedom = "userpopedom";
			this.baseParams.property = "role";
			this.baseParams.roleid = "${id}";
			this.baseParams.omenuid = powerid;
			
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
		
		root=new Ext.tree.AsyncTreeNode({
		   id:'0',
		   text:'显示全部'
		});
		tree.setRootNode(root);

		tree.render('tree'); 
		//tree.expandAll();
		root.expand();
	}

	function treeWin() {
		document.getElementById("search").style.display = "";
		if(businessWin == null) { 
			businessWin = new Ext.Window({
				title: '部门授权',
				width: 315,
				height:350,
				contentEl:'search', 
				renderTo:'searchWin',
		        closeAction:'hide',
		        listeners:{
					'hide':{fn: function () {
						 new BlockDiv().hidden();
						 document.getElementById("search").style.display = "none";
					}}
				},
		        layout:'fit',
			    buttons:[{
		            text:'确定',
		          	handler:function() {
		          		var nodes = tree.getChecked();
		          		var departmentid = "";
		          		for(var i =0;i<nodes.length;i++){
		        			//alert(nodes[i].id);
		        			departmentid += nodes[i].attributes.departid + ",";
		        			
		        			//去掉选中
		        			//nodes[i].attributes.checked = false;

		        		}
		          		//alert("2:"+powerid+"|"+departmentid);
		          		if(departmentid == "") departmentid = "-1";
		          		document.getElementById("departmentid" + powerid).value = departmentid;
		          		businessWin.hide();
		          	}
		        },{
		            text:'取消',
		            handler:function(){
		            	businessWin.hide();
		            }
		        }]
		    });
		}
		new BlockDiv().show();
		businessWin.show();
	}

	var businessWin1 ;
	function treeWin1() {
		document.getElementById("search1").style.display = "";
		if(businessWin1 == null) { 
			businessWin1 = new Ext.Window({
				title: '字段权限',
				width: 315,
				height:350,
				contentEl:'search1', 
				renderTo:'searchWin',
		        closeAction:'hide',
		        listeners:{
					'hide':{fn: function () {
						 new BlockDiv().hidden();
						 document.getElementById("search1").style.display = "none";
					}}
				},
		        layout:'fit',
			    buttons:[{
		            text:'确定',
		          	handler:function() {
		          		/*
		          		var nodes = tree.getChecked();
		          		var departmentid = "";
		          		for(var i =0;i<nodes.length;i++){
		        			//alert(nodes[i].id);
		        			departmentid += nodes[i].attributes.departid + ",";
		        			
		        			//去掉选中
		        			//nodes[i].attributes.checked = false;

		        		}
		          		*/
		          		//alert("2:"+powerid+"|"+departmentid);
		          		//document.getElementById("departmentid" + powerid).value = departmentid;
		          		businessWin1.hide();
		          	}
		        },{
		            text:'取消',
		            handler:function(){
		            	businessWin1.hide();
		            }
		        }]
		    });
		}
		new BlockDiv().show();
		businessWin1.show();
	}
	
	//刷新树
	function refreshTree() {
		try{
			var node = tree.getSelectionModel().getSelectedNode();	
			var path = node.getPath('id'); 
			tree.getLoader().load(root,function () {tree.expandPath(path,'id');});
		}catch(e){
			tree.getLoader().load(root,function () {tree.expand();});
		}   
	}	
	
	function getObj(obj){
		getReading(obj);
		getEditing(obj);
	}
	//可读
	function getReading(obj){
		var menuid = obj.menuid;
		var reads = document.getElementsByName("reads");
		var reading = "";
		for(var i= 0;i<reads.length;i++){
			if(reads[i].checked){
				reading += "|" + reads[i].fieldid;// +"=" + reads[i].value;
			}
		}
		
		document.getElementById("reading" + menuid).value = reading;

	}
	
	//编辑
	function getEditing(obj){
		var menuid = obj.menuid;
		var edits = document.getElementsByName("edits");
		var editing = "";
		for(var i= 0;i<edits.length;i++){
			if(edits[i].checked){
				editing += "|" + edits[i].fieldid;// +"=" + edits[i].value;
			}
		}
		
		document.getElementById("editing" + menuid).value = editing;
		
	}
</script>

</head>
<body leftmargin="0" topmargin="0">

<div id="divBtn"></div>

<form name="thisForm" method="post" action=""  class="autoHeightForm">


	<table width="30%" cellspacing="0" cellpadding="0"><tr><td height="5"></td></tr></table>
	<table width="30%" cellspacing="0" cellpadding="0"><tr><td height="5"></td></tr></table>	
	<table width="100%" cellspacing="0" cellpadding="0"><tr><td height="20" align=center><font size=3 color=red><b>［${string2}］的授权</b></font></td></tr></table>
	<table width="30%" cellspacing="0" cellpadding="0"><tr><td height="5"></td></tr></table>		
	<table width="100%" cellpadding="5" border="1" bordercolor="#000000" style="border:1px outset #000000">
	<tr>
	
	<td width="100%" valign="top"><span style="height:10 vertical-align:bottom">系统菜单&nbsp;&nbsp;&nbsp;&nbsp;<a href="#" onClick="selectAll();">全选</a>&nbsp;&nbsp;&nbsp;&nbsp;<a href="#" onClick="selectNone();">清空</a></span>
	<div style="width:100%;height:350;overflow: auto;">
	${string}

	</div></td>
	</tr>	
	</table>
	<input type="hidden" name="stAll" value="">
	<input type="hidden" name="stRole" value=".${id}.">
	
<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-100)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>
<div id="search" style="display: none;">
	<table width="100%">
		<tr><td >
				<div id="tree" ></div>
			</td>
		</tr>
	</table>
	
</div>	
<div id="search1" style="display: none;">
	<table width="100%">
		<tr><td >
				<div style="height: 280px">
				<mt:DataGridPrintByBean name="FieldPopedom" outputData="false" />
				</div>	
			</td>
		</tr>
	</table>
	
</div>
<input type="hidden" name="roleid" id = "roleid" value="${id }">

</form>
<Script type="text/javascript">

function selectAll()
{
	var len = document.thisForm.MenuID.length;
	var i = 0;
	for(i=0;i<len;i++)
	{
		if(document.thisForm.MenuID[i].disabled==false){
		document.thisForm.MenuID[i].checked = true;
		}
	}
}
function selectNone()
{
	var len = document.thisForm.MenuID.length;
	var i = 0;
	for(i=0;i<len;i++)
	{
		if(document.thisForm.MenuID[i].disabled==false){
		document.thisForm.MenuID[i].checked = false;
		}
	}
}
function getSubTree(id)
{
	var objId = "subTree"+id;
	var objImg = "ActImg"+id;
	var objBank = "subImg"+id;
	var objM=document.getElementById(objImg);
	var objB=document.getElementById(objBank);
	var obj=document.getElementById(objId);
	if(obj.style.display=="block")
	{
		obj.style.display = "none";
		objB.style.display = "none";
		objM.src="${pageContext.request.contextPath}/images/plus.jpg";
	}
	else
	{
		obj.style.display = "block";
		objB.style.display = "block";
		objM.src="${pageContext.request.contextPath}/images/nofollow.jpg";
	}
}
function setEnableTree(temp) {
	
	if(temp.checked == true) {
		var len = document.thisForm.MenuID.length;
		
		for(var i=0;i<len;i++) {
			if(document.thisForm.MenuID[i].ParentID==temp.MyID) {
				
				document.thisForm.MenuID[i].checked = true;
				if(document.thisForm.MenuID[i].depth!="0") {
					setEnableTree(document.thisForm.MenuID[i]);
				}
			}
		}
		while(temp.ParentID!="00"&&temp.ParentID!="000") {
			for(var i=0;i<len;i++) {
				if(document.thisForm.MenuID[i].MyID == temp.ParentID) {
					if(document.thisForm.MenuID[i].checked==false) {
						document.thisForm.MenuID[i].checked = true;
						temp = document.thisForm.MenuID[i];
						break;
					} else {
						temp = document.thisForm.MenuID[i];
						break;
					}
				}
			}
		}
	} else {
		var len = document.thisForm.MenuID.length;
		for(var i=0;i<len;i++) {
			if(document.thisForm.MenuID[i].ParentID==temp.MyID) {
				document.thisForm.MenuID[i].checked = false;
				if(document.thisForm.MenuID[i].depth!="0") {
					setEnableTree(document.thisForm.MenuID[i]);
				}
			}
		}
	}
}
function setCountEnable()
{
	stAll = ".";
}

function goSave(){
	var i = 0;

	stAll = ".";
	var len = document.thisForm.MenuID.length;
	
	for(i=0;i<len;i++)
	{
		if(document.thisForm.MenuID[i].checked == true)
		{
			stAll = stAll + document.thisForm.MenuID[i].value + ".";
		}
	}
	if(stAll == "."){
		stAll = "";
	}
	document.thisForm.stAll.value = stAll;

	
	thisForm.action="${pageContext.request.contextPath}/role.do?method=UpdatePopedom";
	thisForm.submit();
}

function goBack(){
	window.history.back();
//	window.location="List.jsp?menuid=121015";
}

//部门授权
function getUserPopedom(power){
	powerid = power;
	try{
		tree("tree");	
	}catch(e){
		refreshTree();
	}
	treeWin(powerid);
}

//字段权限
function getFieldPopedom(power){
	//alert(power);
	
	document.getElementById("menuid").value = power;
	goSearch_FieldPopedom();
	
	treeWin1(powerid);
}
</Script>

<script type="text/javascript">

Ext.onReady(function(){
	ext_init();
	//tree("tree");
});
</script>

</body>
</html>