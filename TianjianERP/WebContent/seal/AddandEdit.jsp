<%@page import="java.util.UUID"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp"%>
<%String fileName="" ;%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>印章申请</title>
<script type="text/javascript">
var tree;
var tabs;
Ext.onReady(function() {
		
		new Ext.Toolbar({
			renderTo : "divBtn",
			height : 30,
			defaults : {
				autoHeight : true,
				autoWidth : true
			},
			items : [ {
				id : 'saveBtn',
				text : '暂存',
				icon : '${pageContext.request.contextPath}/img/save.gif',
				handler : function() {
					queryWinFun("");
				}
			}, '-', {
				text : '保存并发起申请',
				icon : '${pageContext.request.contextPath}/img/import.gif',
				handler : function() {
					queryWinFun("发起");
				}
			}, '-', {
				text : '返回',
				icon : '${pageContext.request.contextPath}/img/back.gif',
				handler : function() {
					window.history.back();
				}
			}, '->' ]
		});
		
		//单位/部门树
		var root;
			var Tree = Ext.tree;
			
			document.getElementById("sealTreeDiv").innerHTML = "";

			var data=new Ext.tree.TreeLoader({
				url:'${pageContext.request.contextPath}/department.do?method=getTree&hideOrgan=true&department=${seal.applyDepartId}'
			});
			
			tree = new Tree.TreePanel({
			    animate:true, 
			    autoScroll:true,
			    containerScroll: true,
			    loader:data,
			    border: true,
			    width: 200,
		        height: 300,
			    rootVisible:false,
			    dropConfig: {appendOnly:true}
			    
			}); 
			
			tree.on('checkchange', function(node, checked) {   
				node.expand();   
				//node.attributes.checked = checked; 
				
				node.eachChild(function(child) {  
					child.ui.toggleCheck(checked);   
					//child.attributes.checked = checked;  
				});   
			}, tree); 
			
			tree.on('click', function(node, checked) {
				if(node.isLeaf()){   
					document.getElementById("applyDepartId").value=node.id.replace("depart_","");
					document.getElementById("applyDepartName").value=node.attributes.departname;
					businessWin.hide();
				}
			}, tree); 
			
			data.on('beforeload',function(treeLoader,node){
				//&checked=false
				//this.baseParams.userpopedom = "userpopedom";//复选框
				this.baseParams.loginid = "${loginid}";
				this.baseParams.menuid = powerid;
				
				this.baseParams.departid = node.attributes.departid,
				this.baseParams.areaid = node.attributes.areaid,
				this.baseParams.departname = node.attributes.departname,
				this.baseParams.isSubject = node.attributes.isSubject
			},data);
			
			root=new Ext.tree.AsyncTreeNode({
			   id:'0',
			   text:'显示全部'
			});
			tree.setRootNode(root);

			tree.render('tree'); 
			//tree.expandAll();
			root.expand();
			
			
			tabs = new Ext.TabPanel({
			    renderTo: 'my-tabs',
			    activeTab: 0,
			    layoutOnTabChange:true, 
			    forceLayout : true,
			    deferredRender:false,
			    defaults: {autoWidth:true,autoHeight:true},
			    items:[
			        {contentEl:'tab1', title:'按人员', id:'cur1'},
			        {contentEl:'tab2', title:'按角色', id:'cur2'} 
			    ]
			});
			  
			  var Tree = Ext.tree;
	
	var data = new Tree.TreeLoader({
		 //dataUrl:'${pageContext.request.contextPath}/interiorEmail.do?method=getUserJsonTree&addUser=true&checked=false',
		 dataUrl:'${pageContext.request.contextPath}/department.do?method=getTree&addUser=true&hideAreaChecked=true&hideOrgan=true&checked=false',
		 baseParams:{joinUser:'${joinUser}',joinUserDepartmentId:'${joinUserDepartmentId}'}
	});
	
	var tree = new Tree.TreePanel({
        el:'userTreeDiv',
        id:'userTree',
        autoScroll:true,
        animate:true,
        height:300, 
        rootVisible:false,
        containerScroll: true, 
        loader: data
    });
    /*
    data.on('beforeload',function(treeLoader,node){
		this.baseParams.type = node.attributes.type,
		this.baseParams.departmentId = node.attributes.departmentId
	},data);
    */
    data.on('beforeload',function(treeLoader,node){
		this.baseParams.departid = node.attributes.departid,
		this.baseParams.areaid = node.attributes.areaid,
		this.baseParams.departname = node.attributes.departname,
		this.baseParams.isSubject = node.attributes.isSubject
	},data);
    
	tree.on('checkchange', function(node, checked) {   
		node.expand();   
		
		node.attributes.checked = checked; 
			 
			node.eachChild(function(child) {  
				child.ui.toggleCheck(checked);   
				child.attributes.checked = checked;   
				child.fireEvent('checkchange', child, checked);   
			});
		 
		
	}, tree);  
	/*
    var root = new Tree.AsyncTreeNode({
        text: '机构人员列表',
        draggable:false,
        id:'root'
    });
	*/
	var root=new Ext.tree.AsyncTreeNode({
		   id:'0',
		   draggable:false,
		   text:'显示全部'
		});
    tree.setRootNode(root);

    tree.render(); 
				
				
				// 角色
				var TreeRole = Ext.tree;
				
				var dataRole = new TreeRole.TreeLoader({
					 //dataUrl:'${pageContext.request.contextPath}/enterpriseQualification.do?method=getRoleList',
					 dataUrl:'${pageContext.request.contextPath}/interiorEmail.do?method=getRoleList',
					 baseParams:{roleId:'${joinRole}'}
				});
				
				var treeRole = new TreeRole.TreePanel({
			        el:'roleTreeDiv',
			        id:'roleTree',
			        autoScroll:true,
			        animate:true,
			        height:300, 
			        rootVisible:false,
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
					
					node.eachChild(function(child) {  
						child.ui.toggleCheck(checked);   
						child.attributes.checked = checked;   
						child.fireEvent('checkchange', child, checked);   
					});   
					
				}, treeRole);  
				
			    var rootRole = new TreeRole.AsyncTreeNode({
			        text: '角色列表',
			        draggable:false,
			        id:'root'
			    });
			    treeRole.setRootNode(rootRole);

			    treeRole.render();
	});

	var businessWin ;
	var powerid;
	function treeWin() {
		document.getElementById("search").style.display = "";
		if(businessWin == null) { 
			businessWin = new Ext.Window({
				title: '使用部门',
				width: 215,
				height:350,
				contentEl:'search', 
				//renderTo:'searchWin',
		        closeAction:'hide',
		        listeners:{
					'hide':{fn: function () {
						 new BlockDiv().hidden();
						 document.getElementById("search").style.display = "none";
					}}
				},
		        layout:'fit',
			    buttons:[{
		            text:'确定',
		          	handler:function() {
		          		businessWin.hide();
		          	}
		        },{
		            text:'取消',
		            handler:function(){
		            	businessWin.hide();
		            }
		        }]
		    });
		}
		new BlockDiv().show();
		businessWin.show();
	}

	//刷新树
	function refreshTree() {
		try{
			var node = tree.getSelectionModel().getSelectedNode();	
			var path = node.getPath('id'); 
			tree.getLoader().load(root,function () {tree.expandPath(path,'id');});
		}catch(e){
			tree.getLoader().load(root,function () {tree.expand();});
		}   
	}
	function getUserPopedom(power){
		//refreshTree();
		//powerid = power;
		treeWin("sealTreeDiv");
	}

//window 面板 进行查询
var queryWin = null;
function queryWinFun(obj){
	if (!formSubmitCheck('thisForm')) return;
	if(obj !=""){
		resizable:false;
		var searchDiv = document.getElementById("divCheck_select") ;
		searchDiv.style.display = "block" ;
		document.getElementById("userTreeDiv").style.display = "block"; //人员树
		if(!queryWin) { 
		    queryWin = new Ext.Window({
				title: '发送系统通知',
				resizable:false,   //禁止用户 四角 拖动
				contentEl:'divCheck_select',
		     	//renderTo : searchWin,
		     	width: 354,
		     	height:400,
	        	closeAction:'hide',
	       	    listeners : {
		         	'hide':{
		         		fn: function () {
		         			new BlockDiv().hidden();
							queryWin.hide();
						}
					}
		        },
	        	layout:'fit',
		    	buttons:['<input type="checkbox" name="sjdx" id="sjdx" value="是" style="cursor: pointer;">手机短信 &nbsp; <input type="checkbox" style="cursor: pointer;" name="znxx" id="znxx" value="是" checked> 站内消息',{
	            	text:'确认',
	          		handler:function(){
	          			
	          			//选择人员
						var userId = getUrsValue("userTree","id");//Id
						var userName = getUrsValue("userTree","name"); //name
						
						//选择角色
						var roleIds = getUrsValue("roleTree","id");//Id
						var roleUserName = getUrsValue("roleTree","name"); //name
						
						if(userId ==""){
							userId=roleIds;	//其实是存的人员ID，这个第二版
						}else{
							userId+=roleIds;
						}
						
						if(userName ==""){
							userName=roleUserName;
						}else{
							userName+=roleUserName;
						}
						document.getElementById("msgUserId").value = userId ;
						if(document.getElementById("sjdx").checked){
							document.getElementById("mobilePhoneMsg").value=document.getElementById("sjdx").value;
						};
						if(document.getElementById("znxx").checked){
							document.getElementById("instationMsg").value=document.getElementById("znxx").value;
						};
						if (!formSubmitCheck('thisForm')) return;
						queryWin.hide();
					     
						mySubmit(obj);
	            	}
	        	},{
	            	text:'取消',
	            	handler:function(){
	               		queryWin.hide();
	               		//document.getElementById("userTreeDiv").style.display = "none" ;
	            	}
	        	}]
		    });
	    }
	    new BlockDiv().show();
	    queryWin.show();
	}else{
		mySubmit(obj);
	}
	
}	
</script>

<style type="text/css">

.before{
	border: 0px;
	background-color: #FFFFFF !important;
}

.data_tb {
	background-color: #ffffff;
	text-align:center;
	margin:0 0px;
	width:65%;
	border:#8db2e3 1px solid; 
	BORDER-COLLAPSE: collapse; 
	margin-top: 20px;
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
</head>
<body style="width: 90%;height: 100%">
<div id="divBtn"></div>
<form name="thisForm" method="post" action="" id="thisForm" >
<div style="height: 490;width: 1360;overflow: auto;">
<input type="hidden" id="uuid" name="uuid" value="${seal.uuid }" />
<input type="hidden" id="isBeginFile" name="isBeginFile" />
<input type="hidden" id="msgUserId" name="msgUserId" >
<input type="hidden" id="instationMsg" name="instationMsg">
<input type="hidden" id="mobilePhoneMsg" name="mobilePhoneMsg">
<span class="formTitle" ><br>印章申请信息</span>
<table   border="0" style="line-height: 30px;"
	class="data_tb" align="center" >
	<tr>
		<td colspan="2" class="data_tb_alignright">
		印章申请信息<br />
		</td>
	</tr>
	<tr>
		<td class="data_tb_alignright" align="right" width="20%"><font color="red" size=3>*</font>公章用途：</td>
		<td class="data_tb_content" style="paddingpadding-left: 50px;">
			<input  type="text" name="matter" id="matter" class="required" onfocus="onPopDivClick(this);"
				onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();"
				onclick="onPopDivClick(this);"
				autoid=4580
				value="${seal.matter }" />
			 
		</td>
	</tr>
	<tr>
		<td class="data_tb_alignright" align="right" width="20%">
				<div><font color="red" size=3>*</font>公章名称：</div>
		</td>
		 <td class="data_tb_content" style="paddingpadding-left: 50px;">
			<input  type="text" name="ctype" id="ctype" class="required" onfocus="onPopDivClick(this);"
				onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();"
				onclick="onPopDivClick(this);"
				autoid=4581
				value="${seal.ctype }" />
			 
		</td>
	</tr>
	<tr>
		<td class="data_tb_alignright" align="right" width="20%">
				<div><font color="red" size=3>*</font>申请事项：</div>
		</td>
		 <td class="data_tb_content" style="paddingpadding-left: 50px;">
			<input  type="text" name="applyDepartment" id="applyDepartment" class="required" onfocus="onPopDivClick(this);"
				onkeydown="onKeyDownEvent();" 
				onkeyup="onKeyUpEvent();"
				onclick="onPopDivClick(this);"
				autoid=868 noinput=true 
				value="${seal.applyDepartment}" />
			 
		</td>
	</tr>
	<tr>
		<td class="data_tb_alignright" align="right" width="20%">
				<div><font color="red" size=3>*</font>用章部门：</div>
		</td>
		 <td class="data_tb_content" style="paddingpadding-left: 50px;">
			<input  type="text" name="applyDepartName" id="applyDepartName" class="required" onfocus="getUserPopedom(1);onPopDivClick(this);"
				onkeydown="onKeyDownEvent();"
				onkeyup="onKeyUpEvent();"
				onclick="onPopDivClick(this);" 
				autoid=4402
				value="${seal.applyDepartId}" /><a href="javascript:void(0)" onclick="getUserPopedom(1)">&nbsp;选择部门</a>
			 <input type="hidden" id="applyDepartId" name="applyDepartId" value="${seal.applyDepartId}" >
		</td>
	</tr>
	<tr>
		<td class="data_tb_alignright" align="right" width="20%">
				<div><font color="red" size=3>*</font>盖章数量：</div>
		</td>
		 <td class="data_tb_content" style="paddingpadding-left: 50px;">
			<input  type="text" name="sealCount" id="sealCount" class="required validate-digits"  value="${seal.sealCount}"/>
			 
		</td>
	</tr>
	<tr>
		<td class="data_tb_alignright" align="right" width="20%">
				<div>备注：</div>
		</td>
		 <td class="data_tb_content" style="paddingpadding-left: 50px;">
			<textarea name="remark" id="remark"  style="overflow: visible;height: 130px;width: 400px;">${seal.remark}</textarea>
			 
		</td>
	</tr>
	<tr>
			<td class="data_tb_alignright" align="right"  style="vertical-align: top;margin-top: 30px;">附件：</td>
			<td class="data_tb_content">
				<input type="hidden" id="fileName" name="fileName" value="${seal.fileName}" >
				<div style="vertical-align: middle;width: 100%;margin-top: 10px;">
				
				<c:choose>
				   <c:when test="${seal.fileName ==null }">
				       <%
							 fileName=UUID.randomUUID().toString();  //生成uuid
						%>
						<script type="text/javascript">
									document.getElementById("fileName").value="<%=fileName%>";
									attachInit('seal','<%=fileName%>');	
						</script>
				   </c:when>
				   <c:otherwise>
				   		<script type="text/javascript">
				      		attachInit('seal','${seal.fileName}');
				      	</script>
				   </c:otherwise>
				</c:choose>
			
				</div>
			</td>
		</tr>
		<tr>
				<td class="data_tb_alignright" align="right" width="20%">
				      选择的盖章文件
				</td>	
				<td  class="data_tb_content" style="paddingpadding-left: 50px;">
				  	<input  type="text" name="attachname" id="attachname"  onfocus="onPopDivClick(this);"
						onkeydown="onKeyDownEvent();"
						onkeyup="onKeyUpEvent();"
						onclick="onPopDivClick(this);"
						autoid=4586 noinput=true  class="required"
						refer="<%=fileName%>${seal.fileName }" hideresult=true  /><span id="warnupdate" style="color: red;font-size: 12"></span>
				</td>
			</tr>

</table>
<br><br><br><br><br><br>
	<div id="search" style="display: none;">
		<table width="100%">
		<tr><td >
				<div id="tree" ></div>
			</td>
		</tr>
	</table>
	</div>
	<div id="sealTreeDiv"> </div>
	<center><div id="sbtBtn" ></div></center>
	<div id="divCheck_select" style="display: none;">
	
		<div id="my-tabs" >
			<div id="tab1">
				<div id="userTreeDiv" style="width: 340px;height: 300px;overflow: hidden;"></div>
			</div>
			
			<div id="tab2">
				<div id="roleTreeDiv" style="width: 340px;height: 300px;overflow: hidden;"></div>
			</div>
		</div>
	</div>
	</div>
 </form>

<script type="text/javascript">
	
	new Validation('thisForm');
	
	setObjDisabled("applyDepartName");
	

	//获取选择 的人员和角色
	function getUrsValue(obj,objType) {
		var tree = Ext.getCmp(obj);
		var selects = tree.getChecked();
		var usrs = "" ;
		for(var i=0;i<selects.length;i++) {
			if(selects[i].isLeaf()) {
				if(objType =="id"){
					usrs += selects[i].id + ",";
					usrs = usrs.replace("user_","");
				}else if(objType =="name"){
					if(obj=="userTree"){
						usrs += selects[i].attributes.username  + ",";						
					}else{
						usrs += selects[i].attributes.userName  + ",";
					}
					
				}
			}
		}
		//if(usrs != "") {
			//usrs = usrs.substr(0,usrs.length-1);
		//}
		return usrs;
	}
	
	document.getElementById("sbtBtn").style.display = "none";
	function mySubmit(obj) {
		document.getElementById("isBeginFile").value=arguments[0];
		var filename= document.getElementById("fileName").value; //附件编号
		var ctype= document.getElementById("ctype").value;   //公章类型
		var attachname = document.getElementById("attachname").value;  //需要盖章的附件
       
       
        if(getAttachCount()!="0" && attachname==""){
				alert("请选择需要盖章的附件");
				return;
		} 
		if(attachname!=""){ 
		    var endName = attachname.substring(attachname.lastIndexOf(".")+1).toLocaleLowerCase ();
    		if( endName!='xls' && endName!= 'docx' &&  endName!='doc' && endName!='xlsx' && endName!='pdf'){
		      document.getElementById("warnupdate").innerHTML="支持打开的文件为execl、word、pdf";
		      return;
		  }
		    var attachid= document.getElementById("advice-attachname").value ;
			document.getElementById("attachname").value=attachid;
		}
		//getAttachCount(); 验证附件是否上传函数，返回数值，如果返回1就代表有一个附件，以此类推。0就是没有上传附件
		
		  if (!formSubmitCheck('thisForm')) return;
		   
		 // var url = "${pageContext.request.contextPath}/sealNotFlow.do?method=getAccessory";
		  //var requestString = "&table=seal&uuid="+filename;
		  //var result ="";
		    

		
		    if(ctype.indexOf("电子")>-1){
			   // result = ajaxLoadPageSynch(url,requestString);
				//if(result == ""){
				//	alert("请上传附件！");
				//	return ;
				//}
				
				//var resultTwo = result.split(",");
				//if(resultTwo[1] !=""){
				//	alert("一次只能申请一次电子章!");
				//	return ;
				//}
				if(getAttachCount() =="0"){
					alert("请上传附件！");
					return ;
				}
				//else if(getAttachCount() !="1" && getAttachCount() !="0"){
				//	alert("一次只能申请一次电子章!");
				//	return ;
			//	}
			
		   } 
		  
		if (document.getElementById("uuid").value != "") {
			document.thisForm.action = "${pageContext.request.contextPath}/sealNotFlow.do?method=update";
		} else {
			document.thisForm.action = "${pageContext.request.contextPath}/sealNotFlow.do?method=add";
		}
		document.thisForm.submit();
	}
 
</script>

</body>
</html>
