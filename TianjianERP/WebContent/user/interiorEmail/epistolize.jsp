<%@page import="java.util.UUID"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>	
<%@ taglib uri="http://ckeditor.com" prefix="ckeditor" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>写信</title>
<script type="text/javascript" src="baiduUeditor/editor_all.js"></script>
<script type="text/javascript" src="baiduUeditor/editor_config.js"></script>
<link rel="stylesheet" href="baiduUeditor/themes/default/ueditor.css"/>

<style type="text/css">
.table 
{ 
	border-collapse: collapse; 
	border: none; 
	vertical-align:middle;
} 
.td{ 
	border: solid #cfe7f7 1px; 
} 
button{
	width: 80px;
	height: 35px;
}
</style>

<script type="text/javascript"><!--
Ext.override(Ext.tree.TreeNode, {   
    allChildExpand : function(animate,callback,scope){   
      // 先展开本节点   
      var checked = this.attributes.checked ;
      var length = this.childNodes.length ;
      var expandCount = 0 ;
      
      this.expand(false, animate, function(){   
          // 展开子节点   
          var childs = this.childNodes ;
          var curLength = childs.length ;
          for(var i = 0; i < curLength; i++) {   
          	
          	childs[i].ui.toggleCheck(checked);   
          	childs[i].attributes.checked = checked;  
          	
          	//最后一点节点并且没子节点
          	if(i == curLength -1) {
          		if(childs[i].childNodes.length <= 0) {
             		 	this.runCallback(callback, scope || this, [this]); 
                      return;   
          		}
          	}
          }   
          
      }, this);   
  }   
});  
var result = false;
var tabs1,tabs2,tabs3 ;
Ext.onReady(function (){
	tabs1 = new Ext.TabPanel({
	    renderTo: 'my-tabs1',
	    activeTab: 0,
	    layoutOnTabChange:true, 
	    forceLayout : true,
	    deferredRender:false,
	    defaults: {autoWidth:true,autoHeight:true},
	    items:[
	        {contentEl:'tab1-1', title:'按人员', id:'cur11'},
	        {contentEl:'tab1-2', title:'按角色', id:'cur12'} 
	    ]
	});
	
	tabs2 = new Ext.TabPanel({
	    renderTo: 'my-tabs2',
	    activeTab: 0,
	    layoutOnTabChange:true, 
	    forceLayout : true,
	    deferredRender:false,
	    defaults: {autoWidth:true,autoHeight:true},
	    items:[
	        {contentEl:'tab2-1', title:'按人员', id:'cur21'},
	        {contentEl:'tab2-2', title:'按角色', id:'cur22'} 
	    ]
	});
	
	tabs3 = new Ext.TabPanel({
	    renderTo: 'my-tabs3',
	    activeTab: 0,
	    layoutOnTabChange:true, 
	    forceLayout : true,
	    deferredRender:false,
	    defaults: {autoWidth:true,autoHeight:true},
	    items:[
	        {contentEl:'tab3-1', title:'按人员', id:'cur31'},
	        {contentEl:'tab3-2', title:'按角色', id:'cur32'} 
	    ]
	});
	
	new Ext.Toolbar({
		renderTo: "historyBtn",
		height:30,
		defaults: {autoHeight: true,autoWidth:true},
       items:[{ 
           text:'<b>立即发送</b>',
           icon:'${pageContext.request.contextPath}/img/mytask.gif', 
           handler:function(){
        	   //goCheckContent("true"); //检测闭合性
        	   //return ;
        	   mySubmit("立即发送");
   		}
     	},'-',{ 
           text:'<b>保存到草稿箱</b>',
           icon:'${pageContext.request.contextPath}/img/task.gif', 
           handler:function(){
        	   mySubmit("草稿");
   		}
     	},'-',{ 
        text:'<b>返回</b>',
        icon:'${pageContext.request.contextPath}/img/back.gif', 
        handler:function(){
			window.history.back();
        	parent.document.getElementById("menuShow").style.display ="block";
        	parent.document.getElementById("showImage").src = "${pageContext.request.contextPath}/images/default/layout/mini-left.gif";
		}
  	},'->']
	});
    
});
function checkDiv(obj){
	if(obj == "userPanel"){
		return "1";
	}else if(obj == "copyPanel"){
		return "2";
	}else if(obj == "secretPanel"){
		return "3";
	}
}
function isShowDivTree(objDiv){
	var objTypeCount = objDiv.substring(objDiv.length-1,objDiv.length);
	var objType = objDiv.substring(0,objDiv.length-1);
	if(objTypeCount == "1"){
		document.getElementById(objType+"2").style.display="none";
		document.getElementById(objType+"3").style.display="none";
	}else if(objTypeCount == "2"){
		document.getElementById(objType+"1").style.display="none";
		document.getElementById(objType+"3").style.display="none";
	}else{
		document.getElementById(objType+"1").style.display="none";
		document.getElementById(objType+"2").style.display="none";
	}
	document.getElementById(objDiv).style.display = "block";
	//document.getElementById(objDiv).innerHTML = "";
}
//人员树

function createUserTree(objDiv){
	//isShowDivTree(objDiv); //显示隐藏DIV
    var Tree = Ext.tree;
    var treeData = new Tree.TreeLoader({
		 //dataUrl:'${pageContext.request.contextPath}/interiorEmail.do?method=getUserJsonTree&addUser=true&checked=false&rand='+Math.random(),	
		 dataUrl:'${pageContext.request.contextPath}/department.do?method=getTree&hideOrgan=true&hideAreaChecked=true&commonlyUsed=true&addUser=true&checked=false&rand='+Math.random(),	
		 baseParams:{joinUser:'${joinUser}',joinUserDepartmentId:'${joinUserDepartmentId}'}
	});
	var treeUser = new Tree.TreePanel({
      //  renderTo:objDiv,
        id:'departmentTree'+objDiv,
        autoScroll:true,
        animate:true,
        height:320, 
        rootVisible:false,
        bodyStyle:'overflow-y:auto;overflow-x:hidden;',
        containerScroll: true, 
        loader: treeData
    });
	treeData.on('beforeload',function(treeLoader,node){
		this.baseParams.departid = node.attributes.departid,
		this.baseParams.areaid = node.attributes.areaid,
		this.baseParams.departname = node.attributes.departname,
		this.baseParams.isSubject = node.attributes.isSubject
	},treeData);
	
	treeUser.on('click', function(node, checked) {
		node.attributes.checked = checked; 
		node.expand(true,false,function(){   
				child.fireEvent('checkchange', child, checked);   
		 });
	}, treeUser);
	
    treeUser.on('checkchange', function(node, checked) {
		node.allChildExpand(false,function (){
		},this);
	}, treeUser);  
    
    var treeRoot=new Ext.tree.AsyncTreeNode({
	   id:'0',
	   draggable:false,
	   text:'显示全部'+objDiv
	});
    //treeRoot.expand(true);
	treeUser.setRootNode(treeRoot);
	treeUser.render(objDiv);
	
	return treeUser;
}

//角色树
function createRoleTree(objDiv){
	//isShowDivTree(objDiv); //显示隐藏DIV
	var Tree = Ext.tree;
	var treeRoleData = new Tree.TreeLoader({
		 dataUrl:'${pageContext.request.contextPath}/interiorEmail.do?method=getRoleTree&rand='+Math.random(),
		 baseParams:{roleId:'${joinRole}'}
	});
	var treeRoleUser = new Tree.TreePanel({
       // el:objDiv,
        id:'roleTree'+objDiv,
        autoScroll:true,
        animate:true,
        height:320, 
        rootVisible:false,
        containerScroll: true, 
        bodyStyle:'overflow-y:auto;overflow-x:hidden;',
        loader:treeRoleData
    });
	treeRoleData.on('beforeload',function(treeLoader,node){
		this.baseParams.type = node.attributes.type,
		this.baseParams.departmentId = node.attributes.departmentId;
		this.baseParams.id = node.id;
	},treeRoleData);
    treeRoleUser.on('checkchange', function(node, checked) {   
		node.allChildExpand(false,function (){

		},this);
	}, treeRoleUser);  
    var treeRoleRoot = new Tree.AsyncTreeNode({
        text: '角色列表',
        draggable:false,
        id:'0'
    });
    treeRoleUser.setRootNode(treeRoleRoot);
    treeRoleUser.render(objDiv);
    
    return treeRoleUser;
}

</script>
</head>
<body style="background-color: #e7f1f8; overflow: hidden;margin: 0px;padding: 0px;">
	<div id="historyBtn" style="margin: 0px;padding: 0px;"></div>
 	<div style="height: 90%;overflow: auto;">  
	<form action="" method="post" name="thisForm" id="thisForm">
	<input type="hidden" name="ctype" id="ctype">
	<input type="hidden" name="uuid" id="uuid" value="${email.uuid}">
	<input type="hidden" name="reply" id="reply" value="${reply}">
	<input type="hidden" name="roleIds" id="roleIds" value="${roleIds}">
	<span style="margin-left: 30px;padding-top: 30px;"> 
	<img alt="" src="${pageContext.request.contextPath}/images/email/new_email.gif">写邮件</span>
		<table width="90%" align="center" style="background-color: white;" class="table">
			<tr>
				<td class="td" colspan="5" height="10px;"></td>
			</tr>
			<tr>
				<td class="td"  align="right" nowrap="nowrap" style="width: 100">收件人：</td>
				<td class="td" colspan="4" nowrap="nowrap">
					<input type="hidden" name="addresseeId"  id="addresseeId"  value="${email.addresser}${userIdAll}">
					<textarea title="请选择人员" class='required' style="width: 500px;height: 29px;text-shadow:red 0 1px 0; " onkeydown="return false;" name="addressee" readonly="readonly"  id="addressee">${userName}</textarea>
					<span style="margin-left: 15px"><a href="#" onclick="queryWinFun1('1','userPanel')">添加联系人</a> </span><br>
					<div style="margin-left: 10px;">
						<a href="javascript:void(0)" id="11" onclick="showDiv('1','11','抄送','copyUser')">添加抄送 </a>   -- 
						<a href="javascript:void(0)" id="22" onclick="showDiv('2','22','密送','secretUser')">添加密送</a>
					<div style="margin-left: 30px;margin-top: 6px;display: none;"> 
						<a href="javascript:void(0)" id="33" onclick="showDiv('3','33','外部收件人')">添加外部收件人 </a>  -- 
						<a href="javascript:void(0)" id="44" onclick="showDiv('4','44','最近联系人')">最近联系人 </a>
					</div>
					</div>
					<br>
				</td>
			</tr>
			<tr style="display: none;" id="1">
				<td class="td"  align="right" nowrap="nowrap">抄送：</td>
				<td class="td" colspan="4" nowrap="nowrap">
					<input size="80" class='required' name="copyUserId" id="copyUserId" type="hidden" value="${copyUserId }">  
					<input size="80" class='required' name="copyUser" id="copyUser" readonly="readonly" value="${copyUser }">  
					<a href="#" onclick="queryWinFun2('2','copyPanel')">选人</a>
				</td>
			</tr>
			<tr style="display: none;" id="2">
				<td class="td"  align="right" nowrap="nowrap"> 密送：</td>
				<td class="td" colspan="4" nowrap="nowrap">
					<input size="80" class='required' name="secretUserId" id="secretUserId" type="hidden" value="${secretUserId }">  
					<input size="80" class='required' name="secretUser" id="secretUser" readonly="readonly" value="${secretUser }">  
					<a href="#" onclick="queryWinFun3('3','secretPanel')">选人</a>
				</td>
			</tr>
			<tr style="display: none;" id="3">
				<td class="td"  align="right" nowrap="nowrap">外部收信人：</td>
				<td class="td" colspan="2" nowrap="nowrap">
					<input size="80" class='required'> 
					<a href="#" >选人</a>
				</td>
			</tr>
			<tr style="display: none;" id="4">
				<td class="td"  align="right">最近联系人：</td>
				<td class="td" colspan="2">
					<input size="80" class='required' name="recentlyUser" id="recentlyUser"> 
					<a href="#" onclick="queryWinFun('4')">选人</a>
				</td>
			</tr>
			<tr>
				<td class="td"  align="right" nowrap="nowrap">邮件主题：</td>
				<td class="td" colspan="4" nowrap="nowrap">
				<input size="81" class='required' name="title" id="title" value="${email.title}"> 
				<span style="margin-left: 20px">
					重要性：<select id="importance" name="importance">
					<option value="一般邮件">一般邮件</option>
					<option value="重要邮件" style="color: #FF8C00;">重要邮件</option>
					<option value="非常重要" style="color: red;font-size: 25px;">非常重要</option>
					</select>
				</span>
				</td>
			</tr>
			<tr>
				<td class="td"  align="right" nowrap="nowrap">提醒：</td>
				<td class="td"  nowrap="nowrap" colspan="4">
				<input type="checkbox" value="是" id="instationRemind" name="instationRemind" checked="checked"> 使用内部短信提醒&nbsp;
				<input type="checkbox" value="是" id="mobilePhoneRemind" name="mobilePhoneRemind" > 使用手机短信提醒&nbsp;  
				<span style="margin-left: 30px;"> 收条： <input type="checkbox" value="是" id="receiveRemind" name="receiveRemind" >
				请求阅读收条 (收件人第一次阅读邮件时，短信提醒发件人) </span></td>
			</tr>
			<tr>
				<td class="td"  align="right" height="10">附件：</td>
				<td class="td" colspan="4"><br>	<input type="hidden" name="fileId" id="fileId" value="${email.fileId}">
					<script type="text/javascript">
							if("${email.fileId}"==""){
								<%
										String fileName=UUID.randomUUID().toString();  //生成uuid
								%>
								document.getElementById("fileId").value="<%=fileName%>";
								attachInit('email','<%=fileName%>');		
							}else{
								attachInit('email','${email.fileId}');
							}
					</script>
				 &nbsp; </td>
			</tr>
			<tr>
				<td class="td"  align="right" nowrap="nowrap">内容：</td>
				<td class="td" colspan="4" nowrap="nowrap" height="260" width="900"> 
  					  <div style="overflow: auto; width: 100%;overflow-x:hidden;" > 				  		
				    	<textarea  style="width: 100%;" name="content" id="content" >${email.content}</textarea>
					  </div>
				 </td>
			</tr>
		</table>
	</form>
	</div>
	<div id="divCheck_select1" style="display: none;">
		<div id="my-tabs1" >
			<div id="tab1-1">
				<div id="departmentTreeDiv1" style="width: 340px;overflow: hidden;"></div>
			</div>
			
			<div id="tab1-2">
				<div id="roleTreeDiv1" style="width: 340px;overflow: hidden;"></div>
			</div>
		</div>
	</div>
	
	<div id="divCheck_select2" style="display: none;">
		<div id="my-tabs2" >
			<div id="tab2-1">
				<div id="departmentTreeDiv2" style="width: 340px;overflow: hidden;"></div>
			</div>
			
			<div id="tab2-2">
				<div id="roleTreeDiv2" style="width: 340px;overflow: hidden;"></div>
			</div>
		</div>
	</div>
	
	<div id="divCheck_select3" style="display: none;">
		<div id="my-tabs3" >
			<div id="tab3-1">
				<div id="departmentTreeDiv3" style="width: 340px;overflow: hidden;"></div>
			</div>
			
			<div id="tab3-2">
				<div id="roleTreeDiv3" style="width: 340px;overflow: hidden;"></div>
			</div>
		</div>
	</div>
	<div id="divContent" style="display: none;">${email.content}</div>
	<form action="${pageContext.request.contextPath}/interiorEmail.do?method=checkContent" target="checkIframe" id="checkForm" name="checkForm" method="post">
		<textarea style="display: none;" id="checkContent" name="checkContent"></textarea>
	</form>
	<iframe style="display: none;" id="checkIframe" name="checkIframe" src=""></iframe>
</body>
<script type="text/javascript">
new Validation('thisForm');
var editor ;
function goCheckContent(objType){
	document.getElementById("checkContent").value = editor.getContent()+"<span>aaa";
	if(document.getElementById("checkContent").value !=""){ //判断是否填写了内容
		//var url="${pageContext.request.contextPath}/interiorEmail.do?method=checkContent";
		//var requestString = "&checkContent="+document.getElementById("checkContent").value;
		//var request= ajaxLoadPageSynch(url,requestString);
		//alert(request);
		//return ;
		if(objType =="false"){
			alert(" 您当前录入的邮件可能是使用了复制网页形式，其中有部分HTML标签未封闭，为了避免收件人阅读困难，系统将自动去除了所有HTML标签!");
		}else if(objType ==""){
			document.getElementById("checkForm").submit();
		}
	}
}
function mySubmit(obj){
	
	var fileId = document.getElementById("fileId").value;
	var fileName = getStringBySql("SELECT attachname FROM `k_attachext` WHERE indexid='"+fileId+"' AND indextable = 'email' LIMIT 1 ");
	if(document.getElementById("title").value == "" && fileName == "0"){
		alert("请填写主题!");
		return ;
	}
	if(document.getElementById("title").value == ""){
		if(fileName.indexOf(".")>-1){
			fileName= fileName.substring(0,fileName.indexOf(".")); 
		}
		document.getElementById("title").value = fileName ;
	}
	if (!formSubmitCheck('thisForm')) return ;
	var uuid = document.getElementById("uuid").value;
	//if(!editor.hasContents()){ //判断是否填写了内容
	//	alert("请编辑内容！");
	//	return ;
	//} 
	document.getElementById("ctype").value=obj;
	 
	if(document.getElementById("addresseeId").value ==""){
		alert("请选择收件人!");
		return ;
	}

	//if("${reply}" !=""){ //回复
	//	document.thisForm.action="${pageContext.request.contextPath}/interiorEmail.do?method=reply";
	//}else{
		document.thisForm.action="${pageContext.request.contextPath}/interiorEmail.do?method=saveAddressee";
	//}
	editor.sync();           //此处的editor是页面实例化出来的编辑器对象
	window.parent.location.reload();  //刷新父窗口
	showWaiting();//等待提示
	document.thisForm.submit();
}

//根据sql返回string
function getStringBySql(sql){
	 
	var url="${pageContext.request.contextPath}/interiorEmail.do?method=getCount";
	var requestString = "&sql="+sql;
	return request= ajaxLoadPageSynch(url,requestString);
}
//var userPanel1,rolePanel1,userPanel2,rolePanel2,userPanel3,rolePanel3;
var userPanel1 , rolePanel1;
function queryWinFun1(obj,objPanel){
	//tree.getLoader().load(root);
	//treeRole.getLoader().load(rootRole);
	if (!userPanel1){
		userPanel1 = createUserTree("departmentTreeDiv1"); //人员
	}
	if (!rolePanel1){
		rolePanel1 = createRoleTree("roleTreeDiv1");   //角色
	}
	loadUser1(obj,"divCheck_select1",userPanel1.id,rolePanel1.id);
}
var userPanel2 , rolePanel2;
function queryWinFun2(obj,objPanel){
	
	if (!userPanel2){
		userPanel2 = createUserTree("departmentTreeDiv2"); //人员
	}
	if (!rolePanel2){
		rolePanel2 = createRoleTree("roleTreeDiv2");   //角色
	}
	loadUser1(obj,"divCheck_select2",userPanel2.id,rolePanel2.id);
}
var userPanel3 , rolePanel3;
function queryWinFun3(obj,objPanel){
	
	if (!userPanel3){
		userPanel3 = createUserTree("departmentTreeDiv3"); //人员
	}
	if (!rolePanel3){
		rolePanel3 = createRoleTree("roleTreeDiv3");   //角色
	}
	loadUser1(obj,"divCheck_select3",userPanel3.id,rolePanel3.id);
}

function loadUser1(obj,contentElDiv,userTreeId,roleTreeId){
		var queryWin = new Ext.Window({
				title: '添加联系人',
				resizable:false,   //禁止用户 四角 拖动
				contentEl:contentElDiv,
		     	width: 354,
		     	height:415,
		     	//autoScroll:true,
		     	//bodyStyle:'overflow-y:auto;overflow-x:hidden;',
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
		    	buttons:[{
	            	text:'确认',
	          		handler:function(){
						try{
		          			//选择人员
							var userId = getUrsValue(userTreeId,"user");//Id
							var userName = getUserName(userTreeId,"user"); //name
							setUser(obj,userId,userName,"0");
							//选择角色
							var roleIds = getUrsValue(roleTreeId,"role");//Id
							var roleUserName = getUserName(roleTreeId,"role");
							setUser(obj,roleIds,roleUserName,"1");
							queryWin.hide();
						}catch(e){
							alert(e);
						}
						
	            	}
	        	},{
	            	text:'取消',
	            	handler:function(){
	            		queryWin.hide();
	            	}
	        	}]
		    });
    new BlockDiv().show();
    queryWin.show();
    
    var searchDiv = document.getElementById(contentElDiv) ;
	searchDiv.style.display = "block" ;
}

function setUser(obj,objUser,objUserName,objType){
	if(objType == "0"){
		if(obj == 1){//收件人
			document.getElementById("addresseeId").value =objUser;
			document.getElementById("addressee").innerText = objUserName;
		}else if(obj == 2){ //抄送
			document.getElementById("copyUserId").value = objUser;
			document.getElementById("copyUser").value = objUserName;
		}else if(obj == 3){ //密送
			document.getElementById("secretUserId").value = objUser;
			document.getElementById("secretUser").value = objUserName;
		}else if(obj == 4){ //最近联系
			document.getElementById("recentlyUser").value = objUser;
		} 
	}else{
		if(obj == 1){//收件人
			document.getElementById("addresseeId").value +=objUser;
			document.getElementById("addressee").innerText += objUserName;
		}else if(obj == 2){ //抄送
			document.getElementById("copyUserId").value += objUser;
			document.getElementById("copyUser").value += objUserName;
		}else if(obj == 3){ //密送
			document.getElementById("secretUserId").value += objUser;
			document.getElementById("secretUser").value += objUserName;
		}else if(obj == 4){ //最近联系
			document.getElementById("recentlyUser").value += objUser;
		} 
	}
}

//得到选中的人员Id
function getUrsValue(obj,objType) {
	var tree = Ext.getCmp(obj);
	var selects = tree.getChecked();
	var usrs = "" ;
	for(var i=0;i<selects.length;i++) {
		if(selects[i].isLeaf()) {
			if(objType =="user"){
				usrs += selects[i].id + ",";
				usrs = usrs.replace("user_","");
			}else if(objType =="role"){
				usrs += selects[i].id.replace("roleId_","") + ",";
			}
		}
	}
	return usrs;
}
//得到选中的人员名称
function getUserName(obj,objType) {
	var treeShu = Ext.getCmp(obj);
	var selects = treeShu.getChecked();
	var userName = "";
	for(var i=0;i<selects.length;i++) {
		if(selects[i].isLeaf()) {
			if(objType == "user"){
				userName += selects[i].attributes.username + ",";
			}else if(objType == "role"){
				userName += selects[i].attributes.userName + ",";
			}
		}
	}
	 
	return userName;
}

//显示隐藏
function showDiv(objDiv,obj,msg,obyText){
	var thisDiv = document.getElementById(objDiv);
	if(thisDiv.style.display =="none" ){
		thisDiv.style.display = "block";
		document.getElementById(obj).innerText = "删除"+msg;
	}else{
		document.getElementById(obj).innerText = "添加"+msg;
		thisDiv.style.display = "none";
	}
	document.getElementById(obyText+"id").value =""; 
	document.getElementById(obyText).value =""; 
}
parent.document.getElementById("menuShow").style.display ="none";
parent.document.getElementById("showImage").src = "${pageContext.request.contextPath}/images/default/layout/mini-right.gif";

if("${copyUserId}" !=""){
	document.getElementById("1").style.display = "block";
	document.getElementById("11").innerText = "删除抄送";
}
if("${secretUserId}" !=""){
	document.getElementById("2").style.display = "block";
	document.getElementById("22").innerText = "删除密送";
}
if("${mobilePhoneRemind}" =="是"){
	document.getElementById("mobilePhoneRemind").checked = true;
}

try{
	editor = new baidu.editor.ui.Editor({
		textarea:'content',
		elementPathEnabled : false, //隐藏body
		wordCount:false,  //隐藏字符统计
		autoFloatEnabled: false 
	});
	editor.render("content");
}catch (e){
	
}

//修改未读的数量
window.parent.notGet.innerText="${notGet}";

if("${email.importance}" !=""){
	document.getElementById("importance").value="${email.importance}";
}
var cot = document.getElementById("divContent").innerHTML;
if(cot !=""){
	if("${titleInfo}" !=""){
		var tiInfo = "<br><br><br><br><br>============= ${titleInfo} =============== "+cot;
		editor.setContent(tiInfo);
	}else{
		editor.setContent(cot);
	}
} 

</script>
</html>