<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>	
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>公告管理</title>
<script type="text/javascript">
Ext.onReady(function () {
	
	new Ext.Toolbar({
		renderTo : "divBtn",
		height : 30,
		defaults : {
			autoHeight : true,
			autoWidth : true
		},
		items : [ {
			text : '通过',
			icon : '${pageContext.request.contextPath}/img/start.png',
			handler : function() {
				goAudit();
			}
		},
		
		//'-',{
		//	text : '驳回',
		//	icon : '${pageContext.request.contextPath}/img/reset.gif',
		//	handler : function() {
		//		alert("${pdid}");
		//	}
		//},
		
		'-',{
			text : '返回',
			icon : '${pageContext.request.contextPath}/img/back.gif',
			handler : function() {
				window.history.back();
			}
		},'-', {
			text : '关闭',
			icon : '${pageContext.request.contextPath}/img/close.gif',
			handler : function() {
				closeTab(parent.tab);
			}
		}, '->' ]
	});
	
	//tree("treeUserDiv");
	
	var Tree = Ext.tree;
	document.getElementById("treeUserDiv").innerHTML = "";

	var data=new Ext.tree.TreeLoader({
		url:'${pageContext.request.contextPath}/proclamation.do?method=getTree&addUser=true'
	});
	
	
	var tree = new Tree.TreePanel({
	    animate:true, 
	    autoScroll:true,
	    id:'tree',
	    containerScroll: true,
	    loader:data,
	    title:'选择人员',
	    border: true,
        height: 519,
        width:200,
	    rootVisible:false,
	    dropConfig: {appendOnly:true}
	    
	}); 
	tree.on('checkchange', function(node, checked) {   
		node.expand();   
		node.attributes.checked = checked; 
		
		node.eachChild(function(child) {  
			child.ui.toggleCheck(checked);   
			child.attributes.checked = checked;  
		});   
	}, tree); 
	
	data.on('beforeload',function(treeLoader,node){
		//&checked=false
		this.baseParams.checked = false;
		this.baseParams.departid = node.attributes.departid,
		this.baseParams.areaid = node.attributes.areaid,
		this.baseParams.departname = node.attributes.departname,
		this.baseParams.isSubject = node.attributes.isSubject
	},data);
	var root=new Ext.tree.AsyncTreeNode({
	   id:'0',
	   departid:'${departId}',
	   text:'显示全部'
	});
	tree.setRootNode(root);

	tree.render('treeUserDiv'); 
	//tree.expandAll();
	root.expand();
});

 
</script>

</head>

<body >
<form action="${pageContext.request.contextPath }/proclamation.do?method=auditTransit" name="thisForm" id="thisForm" method="post">
<table border="0" width="100%" height="100%">
<tr>
	<td   width="200">
	<div  style="height: 100%;border: 1px;border-style: solid;border-color: #f0f0f0;">
		<div id="treeUserDiv" style="width: 200;"></div>
	</div>
	</td>
	<td>
		<div id="divBtn" style="width: 100%;padding-top: 0px;"> </div>
		<iframe src="${pageContext.request.contextPath}/proclamation.do?method=auditDetails&pdid=${pdid}" style="vertical-align: top;height:73%;width:100%;border:1px;">
		</iframe>
			<hr color="#f0f0f0">
			<div style="text-align: center;height: 10%;margin-top: 5px;">备注：
				<textarea id="titleValue" name="titleValue" style="height: 70px;width: 500px;overflow: visible;"></textarea>
			</div>
			<input type="hidden" name="readUserId" id="readUserId" >
			<input type="hidden" name="pdid" id="pdid"  value="${pdid }">
			<input type="hidden" name="taskId" id="taskId" value="${taskId}">
	</td>
</tr>
</table>
</form>
</body>
<script type="text/javascript">
function goAudit(){
	var treeUser = Ext.getCmp("tree");
	var selects = treeUser.getChecked();
	var usrs = "" ;
	for(var i=0;i<selects.length;i++) {
		if(selects[i].isLeaf()) {
			usrs += selects[i].id + ",";
		}
	}
	
	if(usrs != "") {
		usrs = usrs.substr(0,usrs.length-1);
	}
	document.getElementById("readUserId").value =usrs;
	if(document.getElementById("readUserId").value==""){
		alert("请选择需要阅读的人员！");
		return ;
	}
	if("${taskId}" == ""){
		alert("流程taksId为空！");
		return ;
	}	
	document.getElementById("thisForm").submit();
}
</script>
</html> 