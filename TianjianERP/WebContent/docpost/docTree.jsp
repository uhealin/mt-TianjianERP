<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>部门列表</title>
<script type="text/javascript">

var tree;
var root;
function tree(divName){
	var Tree = Ext.tree;
	
	document.getElementById(divName).innerHTML = "";

	var data=new Ext.tree.TreeLoader({
		url:'${pageContext.request.contextPath}/docpost.do?method=tree'
	});
	
	
	tree = new Tree.TreePanel({
	    animate:true, 
	    autoScroll:true,
	    containerScroll: true,
	    loader:data,
	    border: true,
	    autoWidth:true,
        height: document.body.clientHeight-25,
	    rootVisible:false,
	    dropConfig: {appendOnly:true}
	    
	}); 
	
	data.on('beforeload',function(treeLoader,node){
		
		this.baseParams.departid = node.attributes.departid,
		this.baseParams.areaid = node.attributes.areaid,
		this.baseParams.departname = node.attributes.departname,
		this.baseParams.isSubject = node.attributes.isSubject
		
	},data);

	tree.on('click',function(node,event){
		//alert(node.attributes.areaid+"|"+node.attributes.atype);
 		var url = "${pageContext.request.contextPath}/formDefine.do?method=formListExtView&uuid=34b2e755-fca4-4082-b1fa-c729d99cc5a3&where_id=00&rand="+Math.random();
 		if(node.attributes.areaid != undefined ){
 			url +="&areaid=" +node.attributes.areaid; 
 		}
 		if(node.attributes.atype != undefined ){
 			url +="&atype=" +node.attributes.atype; 
 		}
		var fform=document.forms["fform"];
        fform.action=url;
        fform.submit();
        stopWaiting();
		
	});
	
	root=new Ext.tree.AsyncTreeNode({
	   id:'0',
	   text:'显示全部'
	});
	tree.setRootNode(root);

	tree.render('tree'); 
	//tree.expandAll();
	root.expand();
}

Ext.onReady(function(){

    // NOTE: This is an example showing simple state management. During development,
    // it is generally best to disable state management as dynamically-generated ids
    // can change across page loads, leading to unpredictable results.  The developer
    // should ensure that stable state ids are set for stateful components in real apps.
    Ext.state.Manager.setProvider(new Ext.state.CookieProvider());
    
    var viewport = new Ext.Viewport({
        layout: 'border',
        items: [
        // create instance immediately
         {
            region: 'west',
           
            split: true,
            width: 200,
            collapseMode:'mini',
            contentEl:"west"
        },
        // in this instance the TabPanel is not wrapped by another panel
        // since no title is needed, this Panel is added directly
        // as a Container
        new Ext.Panel({
            region: 'center', // a center region is ALWAYS required for border layout
            deferredRender: false,
            activeTab: 0,     // first tab initially active
            contentEl:"centerDiv"
        })]
    });
    // get a reference to the HTML element with id "hideit" and add a click listener to it 
    
    new Ext.Toolbar({
    	renderTo:"tbDiv",
    	items:[
    	  {
    		  text:"刷新",
    		  icon:'${pageContext.request.contextPath}/img/refresh.gif',
    		  handler:function () {
    			  window.location.reload();
    			  //root.reload();	
    		 		//var url = "${pageContext.request.contextPath}/formDefine.do?method=formListExtView&uuid=34b2e755-fca4-4082-b1fa-c729d99cc5a3&where_id=00&rand="+Math.random();
    		 		//var fform=document.forms["fform"];
    		        //fform.action=url;
    		        //fform.submit();
    		        //stopWaiting();
				}
    	  }
    	]
    });
    
    
    tree("tree");
});
</script>
</head>
<body>
<form name="fform" target="divIframe" method="post">
	<input type="hidden" name="areaid" id="areaid" />
   	<input type="hidden" name="atype" id="atype" />
</form>
<!-- use class="x-hide-display" to prevent a brief flicker of the content -->
<div id="west" class="x-hide-display" >
    
      <div id="tbDiv"></div>
      <div style="height:expression(document.body.clientHeight+3);overflow:auto;" >
         <div id="tree"></div>
      </div>
    
</div>
<div id="centerDiv" class="x-hide-display">
			<div style="height:expression(document.body.clientHeight+3);" >
				<iframe id="divIframe" name="divIframe" scrolling="auto" frameborder="0" width="100%" height="100%" src="${pageContext.request.contextPath}/formDefine.do?method=formListExtView&uuid=34b2e755-fca4-4082-b1fa-c729d99cc5a3&where_id=00" ></iframe>
			</div>
 </div>

<div id="props-panel" class="x-hide-display" style="width:200px;height:200px;overflow:hidden;">

</div>

</body>
</html>