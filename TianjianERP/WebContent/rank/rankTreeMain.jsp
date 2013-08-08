<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>	
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>站内邮件管理</title>
</head>

<style type="text/css">

.before{
	border: 0px;
	background-color: #FFFFFF !important;
}

.data_tb {
	background-color: #ffffff;
	text-align:center;
	margin:0 0px;
	width:60%;
	border:#8db2e3 1px solid; 
	BORDER-COLLAPSE: collapse; 
}
.data_tb_alignright {	
	BACKGROUND: #e4f4fe; 
	white-space:nowrap;
	padding:5px;
	border-top: #8db2e3 1px solid;
	border-left: #8db2e3 1px solid;
	BORDER-RIGHT: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid; 
	height:30px;
	background-color: #d3e1f1;
	width:20%;
	font-size: 13px;
	font-family:"宋体";
}
.data_tb_content {
	PADDING-LEFT: 2px; 
	BORDER-TOP: #8db2e3 1px solid; 
	BORDER-LEFT: #8db2e3 1px solid;
	BORDER-RIGHT: #8db2e3 1px solid;
	BORDER-BOTTOM: #8db2e3 1px solid;  
	WORD-BREAK: break-all; 
	TEXT-ALIGN: left; 
	WORD-WRAP: break-word
}

</style>
<script type="text/javascript">
var result = false;
Ext.onReady(function (){
	  var Tree = Ext.tree;
	  var tree = new Tree.TreePanel({
      id:'userTree',
      animate:true, 
	  renderTo:'userTreeDiv',
	  title:'职级分类',
	  autoScroll:true,
	  width:200,
	  height:520,
	  loader: new Tree.TreeLoader({dataUrl:"${pageContext.request.contextPath}/rank.do?method=getRankJsonTree"}),
      enableDD:true,
      root:new Tree.AsyncTreeNode({
	  text: '职级分类',
	  draggable:false,
	  expanded : true, //默认展开根节点
	  id:'source'
	     }),
	     containerScroll: true
	 });

	  tree.on('click', function(node, checked) {  
		  
		  //是叶子的时候执行
		  if (node.isLeaf()){
			   showRank(node.id);
		  }else{
			  node.expand(); //展开
		  }
	  }, tree);
	
	  tree.on('checkchange', function(node, checked) {   
			node.expand();   
			node.attributes.checked = checked; 
			
			node.eachChild(function(child) {  
				child.ui.toggleCheck(checked);   
				child.attributes.checked = checked;  
			});   
		}, tree);  
		
    
});
</script>
<body style="overflow: hidden;">
  <TABLE style="width:100%;height=100%" border =0 class="data_tb">
  <TR>
	<TD rowspan="3" width="15%" height=100% class="data_tb_content">
		<div id="userTreeDiv" style="width: 100%;height:99%;overflow: auto;vertical-align: top;"></div>
	</TD>
	<TD width="85%" class="data_tb_content" style="display: none;">
		<input name="autoId" type="hidden" id="autoId" value="${rank.autoId}" />
		<table class="data_tb" align="center"  border="0">
			<tr >
				  <td class="data_tb_alignright" align="right">职级名称：</td>
			      <td class="data_tb_content" align="left" id="name">
			       
				  </td>
		 
				  <td class="data_tb_alignright" align="right" width="10%" >职级分类：</td>
			      <td  class="data_tb_content"  align="left" id="ctype">
			        
				  </td>
			</tr>
			<tr >
				  <td class="data_tb_alignright" align="right">所属组：</td>
			      <td class="data_tb_content" align="left"  id="group">
			       
				  </td>
		 
				  <td class="data_tb_alignright" align="right">排序号：</td>
			      <td class="data_tb_content" align="left" id="sequenceNumber">
			      	 
				  </td>
			</tr>
			<tr>
				<td class="data_tb_alignright" align="right" >备注：</td>
				<td class="data_tb_content" align="left" colspan="3" id="explain">
			    </td>
			</tr>
		</table>
	</TD>
  </TR>
  <TR>
	<TD style="height: 100%">
		<iframe src="${pageContext.request.contextPath}/rankWages.do?method=list" frameborder="0" style="width: 100%;overflow: hidden;" height="100%" id="rankWagesIframe"></iframe>	
	</TD>
  </TR>
  </TABLE>
</body>
<script type="text/javascript">
		
function openPage(url){
	if(url == "")
		return;
	else
		
	document.getElementById("mainIframe").src=url+"&rand="+Math.random();
}
 function  showRank(obj){
	 if(obj !=""){
		 Ext.Ajax.request({
				method:'POST',
				params : { 
					autoId :obj,
					rand :Math.random()
				},
				url:"${pageContext.request.contextPath}/rank.do?method=ajaxSkip",
				success:function (response,options) {
					var request = response.responseText;
					  var jsonObj = eval("("+request+")"); 
					for(var i =0;i<jsonObj.length;i++){
						document.getElementById("name").innerText=jsonObj[i].name;
						document.getElementById("ctype").innerText=jsonObj[i].ctype;
						document.getElementById("group").innerText=jsonObj[i].group;
						document.getElementById("sequenceNumber").innerText=jsonObj[i].sequenceNumber;
						document.getElementById("explain").innerText=jsonObj[i].explain;
						document.getElementById("rankWagesIframe").src="${pageContext.request.contextPath}/rankWages.do?method=list&rankId="+jsonObj[i].autoId+"&rand="+Math.random();
					}
				},
				failure:function (response,options) {
					alert("后台出现异常,获取文件信息失败!");
				}
			});
	 }
 }
 
</script>
</html> 