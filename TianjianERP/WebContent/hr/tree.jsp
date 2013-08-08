<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>节点树</title>
<script type="text/javascript">
//所有岗位/岗位名称
var tree;
var root;
function tree(divName){
	//用Ext做一个树
	var Tree = Ext.tree;
	//将树节点插入到页面中
	document.getElementById(divName).innerHTML= "";
	//到后台拿取数据
	var data = new Ext.tree.TreeLoader({
		url:'${pageContext.request.contextPath}/tree.do?method=getTree'
	});
	
	tree = new Tree.TreePanel({
	//设置节点树的一些标签
		animate:true,
		autoScroll:true,
		containerScroll:true,
		//加载数据
		loader:data,
		border:true,
		width :200,
		heigth:document.body.clientHeight - 29,
		rootVisible:false,
		dropConfig:{appendOnly:true}
	
	});
	//加载所显示
	tree.on('click',function(node,e){
		//var divIFrame = document.getElementById("divIframe");
		//divIframe.src = "${pageContext.request.contextPath}/formDefine.do?method=formListView&uuid=8252d0e7-6f0d-4ae1-b34d-97327b6270d5&jobname="+node.text;
	    document.getElementById("jobname").value=node.text;
	    jobForm.submit();
	    stopWaiting();
	});
	root=new Ext.tree.AsyncTreeNode({
	   id:'0',
	   text:'显示全部'
	});
	tree.setRootNode(root);

	tree.render(divName); 
	//tree.expandAll();
	root.expand();
}

Ext.onReady(function (){
   tree('tree');
});  //Ext.onReady(function (){
</script>
</head>
<body>
<div id="divBtn"></div>

<form name="jobForm" method="post" target="divIframe" action="${pageContext.request.contextPath}/formDefine.do">
<input name="method" value="formListView" type="hidden" />
<input name="uuid" value="8252d0e7-6f0d-4ae1-b34d-97327b6270d5" type="hidden" />
  <input name="jobname" type="hidden" id="jobname" />
</form>
<form name="thisForm" method="post" action="" class="autoHeightDiv">
<table align="center" border="0" cellpadding="0" cellspacing="0" width="100%" height="100%">
	<tr>
		<td valign="top" width="15%">
			<div id="tree"  style="height: 300px;"></div>
		</td>
		<td valign="top" width="85%">
			<div style="height:expression(document.body.clientHeight-60);" >
				<iframe id="divIframe" name="divIframe" scrolling="auto" 
				frameborder="0" width="100%" 
				height="100%" 
				src="${pageContext.request.contextPath}/formDefine.do?method=formListView&formTypeId=a3703544-e921-4749-8d6b-0efd5c45c9bb&uuid=8252d0e7-6f0d-4ae1-b34d-97327b6270d5"></iframe>
			</div>
		</td>
	</tr>
</table>
</form>
</body>
</html>