<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>报表模板</title>

<script Language=JavaScript>

var tree;
var root;
var businessWin ;
var powerid;
var ii = "${iCount}";
function tree(divName){
	var Tree = Ext.tree;
	
	document.getElementById(divName).innerHTML = "";

	var data=new Ext.tree.TreeLoader({
		url:'${pageContext.request.contextPath}/print.do?method=tree'
	});
	
	tree = new Tree.TreePanel({
	    animate:true, 
	    autoScroll:true,
	    containerScroll: true,
	    loader:data,
	    border: false,
	    autoWidth:true,
        height: document.body.clientHeight-35,
	    rootVisible:false,
	    dropConfig: {appendOnly:true}
	    
	}); 
	
	data.on('beforeload',function(treeLoader,node){
		this.baseParams.subsetid = node.attributes.subsetid,
		this.baseParams.isSubject = node.attributes.isSubject,
		this.baseParams.emtype = node.attributes.emtype
		
	},data);
	
	/*
	tree.on('checkchange', function(node, checked) {   
		node.expand();   
		node.attributes.checked = checked; 
			
		node.eachChild(function(child) {  
			child.ui.toggleCheck(checked);   
			child.attributes.checked = checked;   
			child.fireEvent('checkchange', child, checked);   
		});   
	}, tree); 
	*/
	tree.on('click',function(node,event){
		if(node.leaf){
			//叶子
			//alert(node.attributes.subname);
			var subname = node.attributes.subname;
			subname = "{" + subname + "." + ii +"}";
			AuditReport.setExcelFormula("$"+subname);
			ii ++;
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

	function ext_init(){
		var html = "报表模板名称：<input type='text' id='templatename' name = 'templatename' value='${map.templatename}' size=20 >";
	    var tbar = new Ext.Toolbar({
	   		renderTo: "divBtn",
            items:[html,'-',{
	            text:'保存',
	            icon:'${pageContext.request.contextPath}/img/save.gif',
	            handler:function(){
					save();
				}
       		},'-',{
	            text:'返回',
	            icon:'${pageContext.request.contextPath}/img/back.gif',
	            handler:function(){
					window.history.back();
				}
       		},'-','<font color=red>如果无法滚动或直接编辑，请在Excel上双击后再继续</font>','->'
			]
        });
        
    }
	
	Ext.onReady(function(){
		ext_init();
		tree("tree");
	});
    
</script>

</head>
<body leftmargin="0" topmargin="0">
<form name="thisForm" action="" method="post">
<div id="divBtn"></div>
<input id="uuid" name="uuid" value="${map.uuid}" type="hidden">
<input id="flagOpt" name="flagOpt" value="${flagOpt}" type="hidden" title="批次号">
<table style="height:expression(document.body.clientHeight-38);width:100%" border='0' ><tr><td style="width: 80%" >
<object classid="clsid:C20572B8-6104-45B8-A3EE-303B42C26ABF" id="oframe" width="100%" height="96%">
	<param name="BorderStyle" value="1">
	<param name="SideBarVisible" value="0">
	<param name="Titlebar" value="0">
	<param name="Menubar" value="1">
</object>
</td><td >

<div id="tree" ></div>

</td></tr></table>

</form>
</body>
</html>
<script>

//---------------
//获取主机地址
//---------------
function getlocationhost(){
	return "http:\/\/"+window.location.host;
}

function print(){
	AuditReport.funPrint();
}

var AuditReport =null;
var oframe=null;
try{
	AuditReport=new ActiveXObject("AuditReportPoject.AuditReport");
	oframe=document.getElementById('oframe');
	AuditReport.pDSOFramer=oframe;
	
	if (!AuditReport || !oframe){
		//出错了，说明控件安装不成功，导航到专门的安装界面
		alert("验证控件安装失败或者没安装,请安装,详细错误原因:"+e);
		window.location="${pageContext.request.contextPath}/ocx/ocx.jsp";
	}
	
	var uuid = "${map.uuid}";
	if(uuid == ""){
		//打开空白EXCEL
		AuditReport.pFileName = '1.xls';
		AuditReport.pOpenUrl = getlocationhost()+'${pageContext.request.contextPath}/Excel/tempdata/Download1.jsp?filename=1';
	}else{
		AuditReport.pFileName = '${map.uuid}.xls';
	    AuditReport.pOpenUrl = getlocationhost()+'${pageContext.request.contextPath}/Excel/tempdata/Download1.jsp?filename=' + uuid;
	}
	AuditReport.funOpenUrlFile();
	
}catch(e){
	alert("验证控件安装失败或者没安装,请安装,详细错误原因:"+e);
	window.location="${pageContext.request.contextPath}/ocx/ocx.jsp";
}

function save(){
	var uuid = document.getElementById('uuid').value;
	var templatename = document.getElementById('templatename').value;
	if(templatename == ""){
		alert("报表模板名称不能为空");
		return;
	}
	try{
		//alert(uuid+"|" + templatename);
		var url = getlocationhost()+"${pageContext.request.contextPath}/print.do?method=save&flagOpt=${flagOpt}&userId=${userSession.userId}&uuid="+uuid + "&newfilename=" + templatename + "&menuid=${param.menuid}";
		AuditReport.pSaveUrl = url;
		AuditReport.pUrlParameter = getlocationhost() + "|&flagOpt=${flagOpt}";
		url = getlocationhost()+"${pageContext.request.contextPath}/AS_SYSTEM/saveExcelFromUrl.jsp?savemode=fulldata&flagOpt=${flagOpt}";
		AuditReport.pUTF8=true;
		if (AuditReport.pFileOpened){
			//alert(url);
			var t1 = AuditReport.funSaveExcelToUrl(true,false,url);
			var t = AuditReport.funSaveUrlFile();
		} else alert('文件未打开！');
	}catch(e){}
	
}


</script>
