<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript">
//EXT初始化
function ext_init(){
		
	var Tree = Ext.tree;
	   
    var tree = new Tree.TreePanel({
       	width: 248,
        region:'west',	
        rootVisible:true,
        border:false,
        autoScroll:true,
     	height:document.body.clientHeight-33,
        loader: new Tree.TreeLoader({
            dataUrl:'${pageContext.request.contextPath}/kdic.do?method=getTree'
        })
    });
    
     tree.on('click',function(node,event){
           	event.stopEvent(); 
           	
          if (node.isLeaf()){
        	  
	          	document.getElementById("ctype").value=node.id;
	          	document.getElementById("thisForm").action="${pageContext.request.contextPath}/kdic.do?method=list";
	          	document.getElementById("thisForm").submit();
	          	
          }else{
        	 	document.getElementById("ctype").value="0";
	          	document.getElementById("thisForm").action="${pageContext.request.contextPath}/kdic.do?method=list";
	          	document.getElementById("thisForm").submit();
          } 
          stopWaiting();
      });
     
     tree.on('contextmenu',function(node,event){
      	if(!node.isLeaf()) {  
 	         event.preventDefault(); //这行是必须的 
 	         node.select();
 	         contextMenuNode = node ; //当前点击的结点
 	         //rightClick.showAt(event.getXY());//取得鼠标点击坐标，展示菜单 
          }else{
        	 event.preventDefault(); //这行是必须的 
  	         node.select();
  	         contextMenuNode = node ; //当前点击的结点
  	         //rightClick2.showAt(event.getXY());//取得鼠标点击坐标，展示菜单 
          } 
       }); 

    // set the root node
    var root = new Tree.AsyncTreeNode({
        text: '字典分类',
        draggable:false,
        id:'0'
    });
    
    tree.setRootNode(root);
    root.expand();
    
    var treeTbar=new Ext.Toolbar({
		items:[{ 
			text:'刷新',
			icon:'${pageContext.request.contextPath}/img/refresh.gif' ,
			handler:function(){
				tree.getLoader().load(root);

			}
		}]
	});
    
    var layout = new Ext.Viewport({
		layout:'border',
		items:[{
			region:'west',
			margins:'0 0 0 0',
			split:true,
			cmargins:'0 0 0 0',
			width: 250,
			collapsible: true,
			containerScroll: true, 
        	split:true,
        	collapseMode:'mini',
        	hideCollapseTool : true,
	        lines:false,
	        items:[treeTbar,tree]
		},{
			region:'center',
			contentEl: 'centerDiv',
			margins:'0 0 0 0',
			split:true,
			cmargins:'0 0 0 0',
	        lines:false,
			html:'<iframe id="treeHTML" name="treeHTML" scrolling="no" frameborder="0" width="100%" height="100%" src="${pageContext.request.contextPath}/kdic.do?method=list&ctype=treeView" ></iframe>' 
	         
		}]
    });
	     
	
	//定义右键菜单 
    var rightClick = new Ext.menu.Menu({ 
        id :'rightClickCont', 
        items : [{ 
            id:'rMenu1', 
            text:'新增分类', 
            icon:'${pageContext.request.contextPath}/img/add.gif',
            //增加菜单点击事件 
            handler:function (node){  
            	document.getElementById("ctype").value=contextMenuNode.id;
            	document.getElementById("thisForm").action="${pageContext.request.contextPath}/kdic.do?method=getCtype";
	          	document.getElementById("thisForm").submit();
            	//Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/caseTemplate.do?method=caseDataTypeEdit&caseId=${param.caseId}&parentId="+contextMenuNode.id;
            } 
        }] 
     }); 
	
    var rightClick2 = new Ext.menu.Menu({ 
        id :'rightClickCont2', 
        items : [{ 
            id:'rMenu1', 
            text:'新增子类', 
            icon:'${pageContext.request.contextPath}/img/add.gif',
            handler:function (node){  
            	document.getElementById("ctype").value=contextMenuNode.id;
            	document.getElementById("thisForm").action="${pageContext.request.contextPath}/kdic.do?method=getCtype";
	          	document.getElementById("thisForm").submit();
            } 
        }] 
     }); 
	
}
window.attachEvent('onload',ext_init);



</script>
</head>

<body>
	

	<div id="centerDiv">
	
	<form action="" id="thisForm" name="thisForm" method="post" target="treeHTML">

	<input name="ctype" type="hidden" id="ctype" >
				 
	</form>				
	
</div>
	
	

</body>

<script type="text/javascript">

function goSearchall(){
	document.getElementById("dicName").value="";
	document.getElementById("dicValue").value="";
	document.getElementById("dicType").value="";
}


function openTab(url,pName) {
	var tab = parent.tab ;
    if(tab && tab.id == "mainFrameTab"){
		n = tab.add({    
			'title':pName,    
			closable:true,  //通过html载入目标页    
			html:'<iframe name="fdfd" scrolling="auto" frameborder="0" width="100%" height="100%" src="'+url+'"></iframe>'   
		}); 
        tab.setActiveTab(n);
	}else {
		window.open(url);
	}
}

function grid_dblclick(obj, tableId) {
	var id=obj.autosId;
	if(id==""){
		return ;
	}else{
		
		var url = "${pageContext.request.contextPath}/policy.do?method=updateViewCount";
		var request = "&id=" + id;
		var result = ajaxLoadPageSynch(url, request);//增加浏览次数
		
		 window.location ="${pageContext.request.contextPath}/policy.do?method=getPolicyandUpdate&id="+id+"&parement=loadAll&look=look";	
	}
	
}

function empty(){
	document.getElementById("wordNum").value="";
	document.getElementById("title").value="";
	document.getElementById("content").value="";
	document.getElementById("sendDept").value="";
	document.getElementById("beginDate").value="";
	document.getElementById("endDate").value="";
}
</script>
</html>