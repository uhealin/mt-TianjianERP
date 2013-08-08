<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript">
var tree;
var root ;
var contextMenuNode ;

function extInit(){

	
	var Tree = Ext.tree;
	var treeLoader = new Tree.TreeLoader({
		dataUrl:'${pageContext.request.contextPath}/formDefine.do?method=getFormTypeJSONTree'
	}); 
	
	tree = new Tree.TreePanel({
        id:'formTypeTree', 
        layout:'fit',
        rootVisible:true,
        enableDD:true,
        animCollapse:false,	
        animate: false,
        autoScroll:true,
        collapseMode:'mini',
        height:document.body.clientHeight-7,
        width: 248,
        border:false,
        loader: treeLoader
    });

	root = new Tree.AsyncTreeNode({
		id:'0',
		text: '表单类别',
		draggable:false
	});
	
    treeLoader.on('beforeload',function(treeLoader,node){
		this.baseParams.id = node.id
	},treeLoader);
	
	tree.setRootNode(root);
	
	root.expand();
    
    tree.on('click',function(node,event){
    	 event.stopEvent();
    	 var doc = Ext.get("listframe");
    	 
    	if (!node.isLeaf()){	
    		//非叶子
			//刷新底稿列表
			node.expand();
		}
		
		doc.dom.src = "${pageContext.request.contextPath}/formDefine.do?method=formList&formTypeId=" + node.id + "&rand=" + Math.random();
    });
    
    var treeToolBar = new Ext.Toolbar({
		items:[
			{ 
				text:'分类管理',
				icon:'${pageContext.request.contextPath}/img/setting.gif' ,
				handler:function(){
					var node = tree.getSelectionModel().getSelectedNode();
					if(node) {
						var parentId = node.id;
						typeWinFun(parentId);
					} else {
						alert("请先选择一个上级分类");
					}	
				}
			}
		]
	});
	
    var left = new Ext.Panel({
    	id:'leftPanel',
    	region:'west',
        containerScroll: true, 
        split:true,
        collapsible: true,
        margins:'0 0 0 0',
        cmargins:'0 0 0 0',
        lines:false,
        collapseMode:'mini',
        hideCollapseTool : true,
        width: 250,
		items:[
			treeToolBar,tree
		]
	});
	
	var center = new Ext.Panel({
		layout:'fit',
		region:'center',
		border:true,
		margins:'0 0 0 0',
		html:'<iframe name="listframe" id="listframe" scrolling="yes" frameborder="0" width="100%" height="100%" src="${pageContext.request.contextPath}/formDefine.do?method=formList"></iframe>'
	});
    	
	var layout = new Ext.Viewport({
		layout:'border',
		items:[
			left,
			center
		]
	});
	
}

var typeWin = null;
function typeWinFun(parentId) {
	if(typeWin == null) { 
	    typeWin = new Ext.Window({
			title: '分类管理',
			width: 600,
			height:450,
			html:'<iframe id="formTypeListFrame" name="formTypeListFrame" width="100%" scrolling="no" height="100%" src="" />',
	        closeAction:'hide',
	        modal:true,
	        layout:'fit',
	        listeners : { 
	        	'hide':function() {
	        		refreshTree();
	        	}
	        }
	    });
	}
	typeWin.show();
	document.getElementById("formTypeListFrame").src = "${pageContext.request.contextPath}/formDefine.do?method=formTypeList&parentId=" + parentId + "&rand=" + Math.random();
}


//刷新树
function refreshTree() {
	 var node = tree.getSelectionModel().getSelectedNode();   
     var path = node.getPath('id'); 
	 tree.getLoader().load(root,function () {tree.expandPath(path,'id');});
}

window.attachEvent('onload',extInit);
</script>
</head>
<body scroll="no">
<div id="north"></div>
<div id="west"></div>
<div id="south"></div>
</body>
</html>