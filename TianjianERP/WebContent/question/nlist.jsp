<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>部门列表</title>
<script type="text/javascript">
//单位/部门树
var tree;
var root;
function tree(divName){
	var Tree = Ext.tree;
	

	var data=new Ext.tree.TreeLoader({
		url:'${pageContext.request.contextPath}/question.do?method=getTree'
	});
	
	tree = new Tree.TreePanel({
		region: "west",
	    animate:true, 
	    autoScroll:true,
	    containerScroll: true,
	    loader:data,
	    border: true,
	    width: 200,
	    rootVisible:true
	    
	}); 
	
	data.on('beforeload',function(treeLoader,node){
		this.baseParams.pid = node.attributes.pid,
		this.baseParams.tablename = node.attributes.tablename
		
	},data);

	tree.on('click',function(node,event){
		var divIframe = document.getElementById("divIframe");
		if(node.id == 1){
			divIframe.src = "${pageContext.request.contextPath}/question.do?method=main&rand="+Math.random();
		}else{
			divIframe.src = "${pageContext.request.contextPath}/question.do?method=main&pid="+node.attributes.pid+"&rand="+Math.random();	
		}
		
		
	});
	
	root=new Ext.tree.AsyncTreeNode({
	   id:'1',
	   pid:'1',
	   text:'所有常用审计知识及案例库 '
	});
	tree.setRootNode(root);

	//tree.expandAll();
	root.expand();
}

function ext_init(){
	//主菜单
	tree("tree"); 
	//var divIframe1 = document.getElementById("divIframe"); 
	var tbar_divBtn = new Ext.Toolbar({
		region: "north",
		height:27,
 		items:[{
			text:'关闭',
			cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/close.gif',
			handler:function () {
				closeTab(parent.tab);
			}
		},new Ext.Toolbar.Fill()]
	});
	
	 var layout = new Ext.Viewport({
			layout:'border',
			items:[tbar_divBtn
			     ,tree,
			     new Ext.Panel({
					region:'center',
					id:'north-panel',
					margins:'0 0 0 0',
					split:true,
					collapsible :true, 
					hideCollapseTool : true,
					cmargins:'0 0 0 0',
			        lines:false,
			        collapseMode:'mini',
			        html:'<iframe id="divIframe" name="divIframe" scrolling="no" frameborder="0" width="100%" height="100%" src="${pageContext.request.contextPath}/question.do?method=main&rand="'+Math.random()+'></iframe>'
				})
			 ]
		});
	
	     
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
<body>

</body>
</html>
<script type="text/javascript">

Ext.onReady(function(){
	ext_init();
	//tree("tree");
});
</script>