<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"  errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>操作员权限设置</title>
<style>
.mySpan {
	color:#FF6600; 
	font-family:"宋体";
	font: normal;
	font-size: 9pt;
	padding: 0px;
	margin: 0px;
}

input {
	border: 1px solid #AEC9D3;
}

legend {
	color: #006699;
}

body {
	background-image:url("${pageContext.request.contextPath}/images/new_bg.gif");
	overflow:hidden ;
	
}
</style>
<style>
.divTitle {
	width:100%;
	font-weight: bold;
	padding: 5px;
	color:#416AA3;
	border: 1px solid #99BBE8;
	border-bottom: 0px;
	background-color: #D9E7F8;
}

.divMain {
	overflow:scroll; 
	width:100%; 
	height:300px; 
	border: 1px solid #99BBE8;
}

</style>
<script type="text/javascript" src="${pageContext.request.contextPath}/AS_INCLUDE/multiSelect.js" charset="GBK"></script>
<script Language=JavaScript>

function ext_init(){

	var tbar_customer = new Ext.Toolbar({
		renderTo: "divBtn",
		defaults: {autoHeight: true,autoWidth:true},
 		items:[{
			text:'保存',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/save.gif',
            handler:function () {
				goSave();
			}
      	},'-',{
            text:'关闭',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/close.gif',
            handler:function(){
            	closeTab(parent.tab);
			}
      	},'-',new Ext.Toolbar.Fill()]
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
	    border: true,
	    width: 200,
        height: 300,
	    rootVisible:false,
	    dropConfig: {appendOnly:true}
	    
	}); 
	
	data.on('beforeload',function(treeLoader,node){
		//&checked=false
		//alert(powerid);
		this.baseParams.userpopedom = "userpopedom";
		this.baseParams.loginid = "${loginid}";
		this.baseParams.omenuid = powerid;
		
		this.baseParams.departid = node.attributes.departid,
		this.baseParams.areaid = node.attributes.areaid,
		this.baseParams.departname = node.attributes.departname,
		this.baseParams.isSubject = node.attributes.isSubject
	},data);
	
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
			width: 215,
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
	          		//alert("2:"+powerid);
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

</script>
</head>
<body leftmargin="10" topmargin="5">

<div id="divBtn"></div>

<form name="thisForm" method="post" action="" class="autoHeightForm">
<span class="formTitle" >
[${name}]的快捷菜单
</span><br><br/>

<div id="oppDiv" style="padding: 0px;text-align: center;">
<table width="80%" cellpadding="0" cellspacing="0">
	<tr>
	
		<td width="48%" valign="bottom">
			<div id="treeDivTitle" class="divTitle">
				用户菜单列表：
				<input type="button" value="全选" class="flyBT" onclick="selectAll();">
				<input type="button" value="清空" class="flyBT" onclick="selectNone();">
			</div>
		</td>
		<td width="40"></td>
		<td width="48%" valign="bottom">
			<div id="workTitle" class="divTitle">
				自定义快捷菜单
			</div>
		</td>
	</tr>
	
	<tr>
		<td valign="top">
			<div id="divTree" class="divMain">
			${sTable }
			</div>
		</td>
		<td valign="middle" align="center">
			<input type="button" value="追加 >" class="flyBT" onclick="addWork();"><br/><br/>
			<input type="button" value="置顶" class="flyBT" onclick="moveTop(document.getElementById('multiSelect'));"><br/><br/>
			<input type="button" value="上移" class="flyBT" onclick="moveUp(document.getElementById('multiSelect'));"><br/><br/>
			<input type="button" value="下移" class="flyBT" onclick="moveDown(document.getElementById('multiSelect'));"><br/><br/>
			<input type="button" value="置底" class="flyBT" onclick="moveBottom(document.getElementById('multiSelect'));"><br/><br/>
			<input type="button" value="< 移除" class="flyBT" onclick="removeWork();">
		</td>
		<td valign="top">
			<div id="work" class="divMain">
				<select multiple name="multiSelect" id="multiSelect" size="23" style='width:300;' >
				<c:forEach items="${menuList}" var="menu">
					<option value="${menu.sysmenu}">${menu.menuname}</option>
				</c:forEach>
				</select>
			</div>
		</td>
	</tr>
</table>
</div>

<input type="hidden" name="stAll" value="">
<input type="hidden" name="stAllName" value="">
<input type="hidden" name="loginid" value="${loginid }">
<input type="hidden" name="userid" value="${userid}">
		
</form>
</body>
</html>
<script>
var stAll = ".";
function getSubTree(id)
{
	var objId = "subTree"+id;
	var objImg = "ActImg"+id;
	var objBank = "subImg"+id;
	var objM=document.getElementById(objImg);
	var objB=document.getElementById(objBank);
	var obj=document.getElementById(objId);
	if(obj.style.display=="")
	{
		obj.style.display = "none";
		objB.style.display = "none";
		objM.src="${pageContext.request.contextPath}/images/plus.jpg";
	}
	else
	{
		obj.style.display = "";
		objB.style.display = "";
		objM.src="${pageContext.request.contextPath}/images/nofollow.jpg";
	}
}

function selectAll(){
	var selectBox = document.getElementsByTagName("INPUT");
	for(var i=0;i<selectBox.length;i++){
		if(selectBox[i].type=='checkbox' && selectBox[i].disabled==false) {
			selectBox[i].checked = true;
		}
	}

}
function selectNone(){
	var selectBox = document.getElementsByTagName("INPUT");
	for(var i=0;i<selectBox.length;i++){
		if(selectBox[i].type=='checkbox'  && selectBox[i].disabled==false) {
			selectBox[i].checked = false;
		}
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

function goSave()
{
	var multiSelect = document.getElementById("multiSelect");
	var values = "-1";
	var names = "-1";
	for(var i=0; i < multiSelect.length; i++) {
		values += "," + multiSelect.options[i].value;
		names += "," + multiSelect.options[i].text;
	}
	
	if(!confirm("您设置的菜单将在下次登录后生效，是否保存设置？") ){
		return false;
	}
	
	document.thisForm.stAll.value = values;
	document.thisForm.stAllName.value = names;
	
	document.thisForm.action="${pageContext.request.contextPath}/sysMenuManger.do?method=custom&opt=save";
	document.thisForm.submit();
}

function getUserPopedom(power){
	powerid = power;
	try{
		tree("tree");	
	}catch(e){
		refreshTree();
	}
	treeWin(powerid);
}


function addWork() {
	var multiSelect = document.getElementById("multiSelect");
	var len = document.thisForm.MenuID.length;
	for(var i=0;i<len;i++){
		var MenuID = document.thisForm.MenuID[i]; 
		if(MenuID.checked == true){
			var menuId = MenuID.value;
			var menuName = MenuID.menuName;
			var depth = MenuID.depth; //depth=0,叶子
			if(depth == '0'){
				var bool = false;
				for(var j=0; j < multiSelect.length; j++) {
					if(multiSelect.options[j].value == menuId){
						bool = true;
						break;
					}	
				}	
				if(bool) continue;
				
				//新增一条
				var varItem = new Option(menuName,menuId);
				multiSelect.options.add(varItem);    

			}
			
		}
	}
}

function removeWork(){
	var multiSelect = document.getElementById("multiSelect");
	for(var i=multiSelect.length-1; i >=0 ; i--) {
		if(multiSelect.options[i].selected) {
			multiSelect.options.remove(i);
		}
	}
}

</script>
<script type="text/javascript">

Ext.onReady(function(){
	ext_init();
	//tree("tree");
});
</script>
