<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>客户综合查询</title>
<script type="text/javascript">

function ext_init(){

	var root=new Ext.tree.TreeNode({
	   id:'0',
	   text:'所有合同'
	});
	
	var root1=new Ext.tree.TreeNode({
	   id:'1',
	   text:'收款提醒'
	});
	root.appendChild(root1);
		
//	root1=new Ext.tree.TreeNode({
//	   id:'2',
//	   text:'收费按业务组成分析'
//	});
//	root.appendChild(root1);
	
	root1=new Ext.tree.TreeNode({
	   id:'3',
	   text:'收费按部门组成分析'
	});
	root.appendChild(root1);
	
	root1=new Ext.tree.TreeNode({
	   id:'4',
	   text:'客户应收账款前十大分析'
	});
	root.appendChild(root1);
		
	root1=new Ext.tree.TreeNode({
	   id:'5',
	   text:'客户收款前十大分析'
	});
	root.appendChild(root1);
		
	root1=new Ext.tree.TreeNode({
	   id:'6',
	   text:'客户收益综合分析'
	});
	root.appendChild(root1);
		
	var tree=new Ext.tree.TreePanel({
		animate:true, 
		root:root,
		rootVisible:false,
	    autoScroll:true,
	    containerScroll: true,
	    border: true,
	    height:document.body.clientHeight - 38,
	    region:'west'
	});
	
	
	tree.on('click',function(node,event){
		switch(Math.abs(node.id)){
			case 1:
				Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/customer.do?method=remind";
	     		break;
     		
//	   		case 2:
//	   			Ext.get("taskFrame").dom.src = "";
//	     		break;

			case 3:
				Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/customer.do?method=charge";
	     		break;
	     	case 4:
	     		Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/customer.do?method=vable";
	     		break;	
	     	case 5:
	     		Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/customer.do?method=pact";
	     		break;	
	     	case 6:
	     		Ext.get("taskFrame").dom.src = "${pageContext.request.contextPath}/customer.do?method=gains";
	     		break;			
	     	default :
	     		break;	
		}
	
	});
	
	var hd = new Ext.Toolbar({
   		height:30,
   		region:'north',
   		defaults: {autoHeight: true,autoWidth:true},
           items:[{ 
            text:'关闭',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/close.gif',
            handler:function(){
				closeTab(parent.tab);
			}
      	}]
	});

    var left = new Ext.Panel({
    	id:'leftPanel',
    	region:'west',
        containerScroll: true, 
        split:true,
        collapsible: true,
        margins:'0 0 5 5',
        cmargins:'0 0 0 0',
        lines:false,
        collapseMode:'mini',
        hideCollapseTool : true,
        width: 200,
        autoScroll:true,//自动出现滚动条
		items:[
			tree
		]
	});

	var center = new Ext.Panel({
		layout:'fit',
		region:'center',
		border:true,
		margins:'0 0 0 5',
		html:'<iframe name="taskFrame" id="taskFrame" scrolling="no" frameborder="0" width="100%" height="100%" src="${pageContext.request.contextPath}/customer.do?method=remind"></iframe>'
	});
	
	var layout = new Ext.Viewport({
		layout:'border',
		items:[
			hd,
			left,
			center
		]
	});
	
	layout.doLayout();
}

window.attachEvent('onload',ext_init);
</script>  
</head>


<body>
<div id="divBtn" ></div>  
<div id="west"></div>
</body>
</html>
