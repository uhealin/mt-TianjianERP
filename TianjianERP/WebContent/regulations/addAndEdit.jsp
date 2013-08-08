<%@page import="java.util.UUID"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ taglib uri="http://ckeditor.com" prefix="ckeditor" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/ckeditor/ckeditor.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/ckfinder/ckfinder.js"></script>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>规章制度管理</title>
<!-- 旧版 副文本编辑框
<script src="<%=request.getContextPath()%>/AS_INCLUDE/images/editor.js" charset=GBK></script>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/images/DhtmlEdit.js" charset=GBK></script>
	 -->
<style>
	#t {
		{border-collapse:collapse;border:none;};
	}
	
	#t td{
		{border:solid #6595d6 1px;};
	}
</style>
	
<script type="text/javascript">
var tbar_project;
var tab;
var treeRole;
function tabClose(flag) {
	window.history.back();
}

function extInit(){
	//var IframeID=document.getElementById("oblog_Composition").contentWindow;
	//IframeID.document.body.innerHTML = document.getElementById("cts").value;
	tab = new Ext.TabPanel({
        id: "tab",
        renderTo: "divTab",
        activeTab: 0, //选中第一个 tab
        autoScroll:true,
        frame: true,
        height: document.body.clientHeight-Ext.get('divTab').getTop()-50, 
        defaults: {autoHeight: true,autoWidth:true},
        items:[{
        			contentEl: "tab1", 
        			title: "规章制度信息", 
        			listeners: {
        				activate: function(){
        					 
        				}
        			}
        		},{
        			contentEl: "tab2", 
        			title: "按角色权限",
        			listeners: {
						activate: function(){
        					  
        				}
        			}
        		},{
        			contentEl: "tab3", 
        			title: "按人员权限",
        			listeners: {
						activate: function(){
        					 
        				}
        			}
        		}
        ] 
	});
	
	var flag = "${param.flag}";
	var text = "返回";
	var icon = "back.gif";
	
	tbar_project = new Ext.Toolbar({
		renderTo: "divBtn",
		items:[{ 
			text:'保存',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/save.gif',
			handler:function(){
				setBargainType();
			}
   		},'-',{ 
				text:text,
				cls:'x-btn-text-icon',
				icon:'${pageContext.request.contextPath}/img/' + icon,
				handler:function(){
					tabClose(flag);
				}
	   		}
	   		,'->'
   		]
    });
      	
 	new Ext.Viewport({
		defaults:{border:false},
		items:[
			tbar_project
		]
	})
	
	var Tree = Ext.tree;
	
	var data = new Tree.TreeLoader({
		 dataUrl:'${pageContext.request.contextPath}/regulations.do?method=getDepartmentList',
		 baseParams:{joinUser:'${regulations.lookUser}',joinUserDepartmentId:'${regulations.lookUser}'}
	});
	
	var tree = new Tree.TreePanel({
        el:'departmentTreeDiv',
        id:'departmentTree',
        autoScroll:true,
        animate:true,
        height:320, 
        rootVisible:false,
        containerScroll: true, 
        loader: data
    });
    
    data.on('beforeload',function(treeLoader,node){
		this.baseParams.type = node.attributes.type,
		this.baseParams.departmentId = node.attributes.departmentId
	},data);
		
	tree.on('checkchange', function(node, checked) {   
		node.expand();   
		
		node.attributes.checked = checked; 
		
		node.eachChild(function(child) {  
			child.ui.toggleCheck(checked);   
			child.attributes.checked = checked;   
			child.fireEvent('checkchange', child, checked);   
		});   

		// 处理选中的 人员
		if(checked){
			if(joinUser.value.indexOf(node.attributes.id.substring(5,node.attributes.id.length))<0 && node.attributes.id.indexOf("department_")<0){
				joinUser.value = joinUser.value + node.attributes.id.substring(5,node.attributes.id.length)+",";
			}
		}else{
			joinUser.value = joinUser.value.replace(node.attributes.id.substring(5,node.attributes.id.length)+",","");
		}
		
		node.eachChild(function(child) {  
			child.ui.toggleCheck(checked);   
			child.attributes.checked = checked;   
			child.fireEvent('checkchange', child, checked);   
		});   
		
	}, tree);  
	
    var root = new Tree.AsyncTreeNode({
        text: '机构人员列表',
        draggable:false,
        id:'root'
    });
    tree.setRootNode(root);

    tree.render();
	
	
	// 角色
	var TreeRole = Ext.tree;
	
	var dataRole = new TreeRole.TreeLoader({
		 dataUrl:'${pageContext.request.contextPath}/enterpriseQualification.do?method=getRoleList',
		 baseParams:{roleId:'${regulations.lookRole}'}
	});
	
	treeRole = new TreeRole.TreePanel({
        el:'roleTreeDiv',
        id:'roleTree',
        autoScroll:true,
        animate:true,
        height:320, 
        useArrows: true,
        rootVisible:true,
        containerScroll: true,
        loader: dataRole
    });
    
    dataRole.on('beforeload',function(treeLoader,node){
		this.baseParams.type = node.attributes.type,
		this.baseParams.departmentId = node.attributes.departmentId
	},dataRole);
		
	treeRole.on('checkchange', function(node, checked) {   
		node.expand();   
		
		node.attributes.checked = checked; 
		
		node.eachChild(function(child) {  
			child.ui.toggleCheck(checked);   
			child.attributes.checked = checked;   
			child.fireEvent('checkchange', child, checked);   
		});   

		// 处理选中的 角色
		if(checked){
			if(node.isLeaf()) {
				if(joinRole.value.indexOf(node.attributes.roleId)<0 ){
					joinRole.value = joinRole.value + node.attributes.roleId+",";
				}
			}
			document.getElementById("joinRole").value = joinRole.value;
		}else{
			joinRole.value = joinRole.value.replace(node.attributes.roleId+",","");
			document.getElementById("joinRole").value = joinRole.value;
			
		}
		
		node.eachChild(function(child) {  
			child.ui.toggleCheck(checked);   
			child.attributes.checked = checked;   
			child.fireEvent('checkchange', child, checked);   
		});   
		
	}, treeRole);  
	
    var rootRole = new TreeRole.AsyncTreeNode({
        text: '全选',
        draggable:false,
        checked:false,
        id:'root' 
    });
    treeRole.setRootNode(rootRole);
    treeRole.render();
}


</script>
</head>
<body  >

<div id="divBtn"></div>
	<form name="thisForm" action="${pageContext.request.contextPath}/regulations.do" class="autoHeightForm" method="post" >
		<input name="autoId" type="hidden" id="autoId" value="${autoId}" />
		<input name="attachmentId" type="hidden" id="attachmentId" />
		<input name="fileName" type="hidden" id="fileName" value="${fileName}" />
		<input name="filePath" type="hidden" id="filePath" value="${filePath}" />
		

	<div id="divTab" style="overflow:auto;height: 500px;width: 98%">
	<div id="tab1" >
		
		<table id="t" height="100%" width="100%" border="1" align="left" cellpadding="2" cellspacing="1"  >
			<tr height="20">
				  <td height="23" align="center" width="10%" bgColor="#EEEEEE"><strong>标&nbsp;&nbsp;&nbsp;&nbsp;题：</strong></td>
			      <td height="23" align="left">
			      	<input name="title" id="title" title="请输入标题" class='required' value="${regulations.title }" style="width: 580px;border: 1px solid gray" />
				  </td>
			</tr>
			<tr height="20">
				  <td height="23" align="center" width="10%" bgColor="#EEEEEE"><strong>分&nbsp;&nbsp;&nbsp;&nbsp;类：</strong></td>
			      <td height="23" align="left">
			      	<input name="ctype" id="ctype" title="请输入标题" class='required' value="${regulations.ctype }"
			      	 	noinput=true
					   title="请输入有效的值"
					   onkeydown="onKeyDownEvent();"
					   onkeyup="onKeyUpEvent();"
					   onclick="onPopDivClick(this);"
					   onchange="setProjectName();"
					   autoid=853
			      	 />
				  </td>
			</tr>
			<tr>
				<td height="23" align="center" bgColor="#EEEEEE"><strong>附件上传：</strong></td>
				<td height="15" align="left"  style="vertical-align: middle;">
				<input type="hidden" name="attachmentIds" id="attachmentIds">
				<script type="text/javascript">
						if("${regulations.attachmentId}"==""){
							<%
									String fileName=UUID.randomUUID().toString();  //生成uuid
							%>
							document.getElementById("attachmentIds").value="<%=fileName%>";
							attachInit('regulations','<%=fileName%>');					
						}else{
							attachInit('regulations','${regulations.attachmentId}');
						}
				</script>
			    </td>
		   
			</tr>
			<tr height=18>
				  <td height="320" align="center" bgColor="#EEEEEE"><strong>内&nbsp;&nbsp;&nbsp;&nbsp;容：</strong></td>
				  <td align="left" >
					 <label style="height:95%;text-align: left;"> 
					   <!--  <jsp:include page="../AS_INCLUDE/images/edit.html"/> --> 
					   <textarea rows="2" cols="1" style="display: none;" name="contents" id="contents">${regulations.contents}</textarea>
					  <ckeditor:replace replace="contents" basePath="/ckeditor/" />
					  </label>
				 </td>
		 	 </tr>
			<tr style="display: none">
				<td height="23" align="center" bgColor="#EEEEEE"><strong>备&nbsp;&nbsp;&nbsp;&nbsp;注：</strong></td>
				<td height="23" align="left"><input name="memo" id="memo" type="text" size="30" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" maxlength="50"  onClick="onPopDivClick(this);" valuemustexist=true autoid=371>
			    </td>
		   
			</tr>
		</table>
	</div>
	
	<!-- 成员 -->
	<div id="tab2" class="x-hide-display tabDiv">
		<table width="98%" height="320" cellspacing="10">
			<tr>
				<!-- 项目分工左边的部门人员树 -->
				<td id="roleTreeDiv" style="overflow-y:auto;" width="30%" valign="top">
				</td>
			</tr>		
		</table>	
	</div>
	
	<!-- 成员 -->
	<div id="tab3" class="x-hide-display tabDiv">
		<table width="98%" height="320" cellspacing="10">
			<tr>
				<!-- 项目分工左边的部门人员树 -->
				<td id="departmentTreeDiv" style="overflow-y:auto;" width="30%" valign="top">
				</td>
			</tr>		
		</table>	
	</div>
	
	</div>
	
	<input type="hidden" id="joinUser" name="joinUser" value="${regulations.lookUser}">
	<input type="hidden" id="joinRole" name="joinRole" value="${regulations.lookRole}">
	</form>
</body>


<script><!--

new Validation("thisForm");

var joinUser = document.getElementById("joinUser");
var joinRole = document.getElementById("joinRole"); 
 
function setBargainType(){
	var title = document.getElementById("title").value;
	//var IframeID=document.getElementById("oblog_Composition").contentWindow;
	//document.getElementById("contents").value = IframeID.document.body.innerHTML;
	var contents = document.getElementById("contents").value;
	
	if(title=="" || title==null){
		alert("标题不能为空!");
		document.getElementById("title").select();
		return;
	}
	//if(contents=="" || contents==null){ 
	//	alert("内容不能为空!");
	//	return;
	//}
	 
	
	var joinUser = document.getElementById("joinUser").value + "";
	var joinRole = document.getElementById("joinRole").value + "";
	
	if(joinUser=="" && joinRole==""){
		alert("请至少选择一个角色或人员进行查看!");
		return;
	} 
	if(joinUser ==""){
		var tree = Ext.getCmp("roleTree");
		var selects = tree.getChecked();
		var userName = "";
		for(var i=0;i<selects.length;i++) {
			if(selects[i].isLeaf()) {
				userName += selects[i].attributes.userName + ",";
			}
		}
		if(userName != "") {
			userName = userName.substr(0,userName.length-1);
			//alert(userName);
			//document.getElementById("joinRole").value=userName+"";
		}
	//alert(userName);
	//return ;
	}	 
	showWaiting(); 
	
	var autoId = document.getElementById("autoId").value;
    if(autoId !=""){
       document.getElementById("fileName").value = document.getElementById("attachmentIds").value;
       document.thisForm.action="${pageContext.request.contextPath}/regulations.do?method=updateSave";
       document.thisForm.submit();
    }else{
    	document.getElementById("attachmentId").value = document.getElementById("attachmentIds").value;
		document.thisForm.action="${pageContext.request.contextPath}/regulations.do?method=addSave";
		document.thisForm.submit();
	}
}


//下载 
function down(fileName,filePath){
	document.getElementById("fileName").value = fileName;
	document.getElementById("filePath").value = filePath;
	document.thisForm.action = "${pageContext.request.contextPath}/regulations.do?method=download";
	document.thisForm.submit();
}



//删除附件
function deleteFile(fileName,filePath,unid){
	if(confirm("您确定要删除附件吗?")){
		var filePath =  filePath;
		var attachmentId =  document.getElementById("attachmentId").value;
		var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		oBao.open("POST","${pageContext.request.contextPath}/regulations.do?method=deleteFile&filePath="+filePath+"&attachmentId="+unid,false);
		oBao.send();
		var result = oBao.responseText;
		if(result=="Y"){
			alert("删除成功!");
			document.getElementById(unid).style.display="none";
			
		}else if(result=="N"){
			alert("删除失败!");
		}else{
			alert("文件不存在!");
		}
	}
}
 
//window.attachEvent('onload',ext_init);

//ext初始化
Ext.onReady(extInit);
</script>
</html>
