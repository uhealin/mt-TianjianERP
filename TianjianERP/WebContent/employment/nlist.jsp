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
		url:'${pageContext.request.contextPath}/employment.do?method=emTree'
	});
	
	 var treeToolBar = new Ext.Toolbar({
			items:[
				{ 
					text:'刷新',
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
	    ,toolbar:[treeToolBar]
	}); 
	
	data.on('beforeload',function(treeLoader,node){
		this.baseParams.departid = node.attributes.departid,
		this.baseParams.areaid = node.attributes.areaid,
		this.baseParams.departname = node.attributes.departname,
		this.baseParams.isSubject = node.attributes.isSubject,
		this.baseParams.emtype = node.attributes.emtype
	},data);

	tree.on('click',function(node,event){
		//alert(node.attributes.departid+"|"+node.attributes.areaid+"|"+node.attributes.isSubject);
		var divIframe = document.getElementById("divIframe"); 
		var fform=document.forms["fform"];
		if(node.attributes.isSubject == "0"){
			//无设置区域 -> 打开区域页面:可以移动部门到区域中
			//divIframe.src = "${pageContext.request.contextPath}/department.do?method=area&issubject="+node.attributes.isSubject+"&organid="+node.attributes.departid+"&ifAdd=1&rand="+Math.random();
		}else if(node.attributes.isSubject == "1"){
			//单位 -> 打开单位修改页面
			//divIframe.src = "${pageContext.request.contextPath}/department.do?method=organ&issubject="+node.attributes.isSubject+"&departid="+node.attributes.departid+"&rand="+Math.random();
		}else if(node.attributes.isSubject == "2"){
			//区域 -> 打开区域页面:只能修改区域负责人
			//divIframe.src = "${pageContext.request.contextPath}/department.do?method=area&issubject="+node.attributes.isSubject+"&departid="+node.attributes.areaid+"&rand="+Math.random();
		}else if(node.attributes.isSubject == "3"){
			//区域 -> 打开区域页面:只能修改区域负责人
			//divIframe.src = "${pageContext.request.contextPath}/department.do?method=area&issubject="+node.attributes.isSubject+"&departid="+node.attributes.areaid+"&rand="+Math.random();
			//divIframe.src = "${pageContext.request.contextPath}/employment.do?method=ListEm&judge=userReamd&departmentid="+node.attributes.departid;
            fform.action="${pageContext.request.contextPath}/employment.do?method=ListEm&judge=userReamd";
            fform.departmentid.value=node.attributes.departid;
            var pn=node;
            
            while(!pn.attributes.emtype&&pn.parentNode){
            	pn=pn.parentNode;
            }
            
            fform.emtype.value=pn.attributes.emtype;
            fform.submit();
            stopWaiting();
		}else if(node.attributes.isSubject=="usersearch"){
	          fform.action="${pageContext.request.contextPath}/employment.do?method=ListEm&judge=userReamd&queryId="+node.id;
            fform.departmentid.value=node.attributes.departid;
            fform.emtype.value=node.attributes.emtype;
            fform.submit();
            stopWaiting();
		}else if(node.attributes.isSubject=="item"){
            fform.action=node.attributes.url;
            fform.departmentid.value=node.attributes.departid;
            fform.emtype.value=node.attributes.emtype;
            fform.submit();
            stopWaiting();
		}else if(node.attributes.isSubject == "emtype") {
			//部门 -> 打开单位修改页面
			//divIframe.src = "${pageContext.request.contextPath}/department.do?method=depart&issubject="+node.attributes.isSubject+"&departid="+node.attributes.departid+"&rand="+Math.random();
			 //var deparId=node.id.split("_")[0];
            var pn=node;
            
            while(!pn.attributes.emtype&&pn.parentNode){
            	pn=pn.parentNode;
            }
            
            fform.emtype.value=pn.attributes.emtype;
            fform.action="${pageContext.request.contextPath}/employment.do?method=ListEm&judge=userReamd";
            fform.departmentid.value="";
            //fform.emtype.value=emtype;
           // fform.submit();
           // stopWaiting();
			//divIframe.src = "${pageContext.request.contextPath}/user.do?method=ListEm&judge=userReamd&departmentid="+deparId+"&emtype="+emtype;
           
			//divIframe.docuemnt.getElementById("departmentid").value=deparId;
			//divIframe.docuemnt.getElementById("emtype").value=deparId;
			//divIframe.docuemet.goSearch_user();
		}
		else if(node.attributes.isSubject == "4") {
			//部门 -> 打开单位修改页面
			//divIframe.src = "${pageContext.request.contextPath}/department.do?method=depart&issubject="+node.attributes.isSubject+"&departid="+node.attributes.departid+"&rand="+Math.random();
			 var deparId=node.id.split("_")[0];
            var emtype=encodeURIComponent(node.id.split("_")[1]);
            fform.action="${pageContext.request.contextPath}/employment.do?method=ListEm&judge=userReamd";
            fform.departmentid.value=deparId;
            fform.emtype.value=emtype;
            fform.submit();
            stopWaiting();
			//divIframe.src = "${pageContext.request.contextPath}/user.do?method=ListEm&judge=userReamd&departmentid="+deparId+"&emtype="+emtype;
           
			//divIframe.docuemnt.getElementById("departmentid").value=deparId;
			//divIframe.docuemnt.getElementById("emtype").value=deparId;
			//divIframe.docuemet.goSearch_user();
		}
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
            width: 250,
            
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
    		  icon:'${pageContext.request.contextPath}/img/add.gif',
    		  handler:function () {
    			 root.reload();		
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
   <input type="hidden" name="departmentid" />
   <input type="hidden" name="emtype" />  
   <input type="hidden" name="qryWhere_em" /> 
   <input type="hidden" name="qryJoin_em" /> 
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
				<iframe id="divIframe" name="divIframe" scrolling="auto" frameborder="0" width="100%" height="100%"  ></iframe>
			</div>
 </div>

<div id="props-panel" class="x-hide-display" style="width:200px;height:200px;overflow:hidden;">

</div>

</body>
</html>