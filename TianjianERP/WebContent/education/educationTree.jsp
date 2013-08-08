<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>培训班树</title>
<script type="text/javascript">
Ext.onReady(function () {
	
	
	var Tree = Ext.tree;

	var data=new Ext.tree.TreeLoader({
		url:'${pageContext.request.contextPath}/education.do?method=getEducationTree'
	});
	
	data.on('beforeload',function(treeLoader,node){

		this.baseParams.id = node.id;
	},data);
	
	tree = new Tree.TreePanel({
	    animate:true, 
	    autoScroll:true,
	    id:'tree',
	    containerScroll: true,
	    loader:data,
	    title:'培训班',
	    border: true,
        height: 519,
        width:248,
	    rootVisible:false,
	    dropConfig: {appendOnly:true}
	    
	}); 
	
	var root=new Ext.tree.AsyncTreeNode({
		   id:'0',
		   text:'显示全部'
		});
		tree.setRootNode(root);

		root.expand();
		
	tree.on('click', function(node, event) {
		 event.stopEvent();
		var id = node.id;
		//alert(id);
		var doc = Ext.get("fileIframe");
		doc.dom.src="${pageContext.request.contextPath}/education.do?method=list&coursetype="+node.id+"&rand="+Math.random();
		
	
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
	        autoHeight:true,
	        width: 250,
			items:[
				tree
			]
		});
		
		var center = new Ext.Panel({
			layout:'fit',
			region:'center',
			border:true,
			margins:'0 0 0 0',
			html:'<iframe name="fileIframe" id="fileIframe" scrolling="no" frameborder="0" width="100%" height="100%" src="${pageContext.request.contextPath}/education.do?method=list"></iframe>'
		});
	    	
		var layout = new Ext.Viewport({
			layout:'border',
			items:[
				left,
				center
			]
		});
});
</script>
</head>
<body>

</body>
</html>