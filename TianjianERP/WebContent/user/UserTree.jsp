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
            text:'全选',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/import.gif',
            handler:function(){
				selectAll();
			}
      	},'-',{
            text:'清空',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/import.gif',
            handler:function(){
				selectNone();
			}
      	},'-',{
            text:'返回',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/back.gif',
            handler:function(){
				window.history.back();
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
[${name}]&nbsp;&nbsp;的&nbsp;&nbsp;操&nbsp;&nbsp;作&nbsp;&nbsp;员&nbsp;&nbsp;权&nbsp;&nbsp;限
</span>


<table width="98%" border="0" cellpadding="0" cellspacing="0">
<tr>
	<td height="30" width="160" valign="top" align="right">&nbsp;</td>
	<td>
	<div id="divTree"  style="overflow:auto;height: 450" >
	${sTable }
	</div>
	</td>
</tr>
</table>	

<input type="hidden" name="stAll" value="">
<input type="hidden" name="loginid" value="${loginid }">

<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-100)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>
<div id="search" style="display: none;">
	<table width="100%">
		<tr><td >
				<div id="tree" ></div>
			</td>
		</tr>
	</table>
	
</div>
		
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
	stAll = ".";
	var len = document.thisForm.MenuID.length;
	var i = 0;
	for(i=0;i<len;i++)
	{
		if(document.thisForm.MenuID[i].checked == true)
		{
			stAll = stAll + document.thisForm.MenuID[i].value + ".";
		}
	}
	document.thisForm.stAll.value = stAll;
	//alert(stAll);
	
	document.thisForm.action="user.do?method=UserPopedom&opt=save";
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
</script>
<script type="text/javascript">

Ext.onReady(function(){
	ext_init();
	tree("tree");
});
</script>
