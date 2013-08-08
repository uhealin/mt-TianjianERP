<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>通讯录</title>
<style type="text/css">
 
</style>
<script type="text/javascript">
    function keyDown(){
	 if (event.keyCode == 8) {
           if (document.activeElement.type == "textarea") {
               if (document.activeElement.readOnly )
                    return false;
            }
            return true;
        }
	      
	      
   }
    
    
    Ext.override(Ext.tree.TreeNode, {   
          allChildExpand : function(animate,callback,scope){   
            // 先展开本节点   
            var checked = this.attributes.checked ;
            var length = this.childNodes.length ;
            var expandCount = 0 ;
            
            this.expand(false,animate, function(){   
                // 展开子节点   
                var childs = this.childNodes ;
                var curLength = childs.length ;
                
                for(var i = 0; i < curLength; i++) {   
                	
                	childs[i].ui.toggleCheck(checked);   
                	childs[i].attributes.checked = checked;  
                //	childs[i].fireEvent('checkchange', childs[i], checked,true);       
					
                	//最后一点节点并且没子节点
                	if(i == curLength -1) {
                		if(childs[i].childNodes.length <= 0) {
                   		 	this.runCallback(callback, scope || this, [this]); 
                            return;   
                		}else {
                			childs[i].allChildExpand(false,callback,this);	
                		}
                	}else {
                		childs[i].allChildExpand(false,callback,this);	
                	}
                }   
                
            }, this);   
        }   
    });  
    
    Ext.override(Ext.tree.TreeNodeUI,{
       toggleCheck : function(value){
         var cb = this.checkbox;
         if(cb){
           cb.checked = (value === undefined ? !cb.checked : value);
         }
       }
     });
    
    
    document.onkeydown = keyDown ;
var result = false;
var tabs;
Ext.onReady(function (){
    var tbar_user = new Ext.Toolbar({
		renderTo: 'divBtn',
		items:[	       
		{
			text:'发送短信',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/mytask.gif',
			handler:function(){
				submitMsg();
			}
		}]})	
    /*
		new ExtButtonPanel({
		    desc:'',
		    renderTo:'sbtBtn',
			items:[
			{
	            text: '发送短信',
	            id:'appSubmit23', 
	            icon:'${pageContext.request.contextPath}/img/receive.png' ,
	            scale: 'large',	
	            handler:function(){
	            	submitMsg();
	   			 }
	           }
	        ]  
	});  	*/	
	tabs = new Ext.TabPanel({
	    animate:true, 
	    autoScroll:true,
	    containerScroll: true,
	    activeTab:0,  
	    border: true,
	    width: 210,
        height: document.body.clientHeight,
	    rootVisible:false,
	    dropConfig: {appendOnly:true},
	    renderTo: 'my-tabs',
	    renderTo:'tree',
	 //   activeTab: 0,
	 //   layoutOnTabChange:true, 
	  //  forceLayout : true,
	 //  deferredRender:false,
	 //   defaults: {autoWidth:true,autoHeight:true},
	     items:[
	        {contentEl:'tab1', title:'按人员', id:'cur1'},
	        {contentEl:'tab2', title:'按角色', id:'cur2'} 
	    ]
	});
//得到选中的人员Id
function getUrsValue() {
	var tree = Ext.getCmp("usertree");
	var selects = tree.getChecked();
	var usrs = "" ;
	for(var i=0;i<selects.length;i++) {
		if(selects[i].isLeaf()) {
			usrs += selects[i].id + ",";
		}
	}
    var roletree = Ext.getCmp("roleTree");
    var rselects = roletree.getChecked();
    for(var i=0;i<rselects.length;i++) {
		if(rselects[i].isLeaf()) {
			usrs += rselects[i].attributes.id + ",";
		}
	}
	if(usrs != "") {
		//usrs = usrs.substr(0,usrs.length-1);
	}
	return usrs;
}

	//得到选中的人员名称
	function getUserName() {
		
		var strArr = new Array();
		var tree = Ext.getCmp("usertree");
		var selects = tree.getChecked();
		
		var userName = "";
		var usrs = "" ;
		for(var i=0;i<selects.length;i++) {
			if(selects[i].isLeaf()) {
				userName += selects[i].attributes.username + ",";
				usrs += selects[i].id + ",";
			}
		}
		
		var roletree = Ext.getCmp("roleTree");
	    var rselects = roletree.getChecked();
	    for(var i=0;i<rselects.length;i++) {
			if(rselects[i].isLeaf()) {
				userName += rselects[i].attributes.userName + ",";
				usrs += rselects[i].attributes.id + ",";
			}
		}
		if(userName != "") {
			//userName = userName.substr(0,userName.length-1);
		}
		strArr.push(userName) ;
		strArr.push(usrs) ;
		return strArr;
	}
	
    //人员树
	var usertree;
	var userroot;
	var Tree = Ext.tree;
	var data=new Ext.tree.TreeLoader({
		url:'${pageContext.request.contextPath}/enterpriseQualification.do?method=getTree&addUser=true&checked=false'
	});
	
	usertree = new Tree.TreePanel({
	    animate:true, 
	    autoScroll:true,
	    id:"usertree",
	    containerScroll: true,
	    loader:data,
	    border: true,
	    width: 210,
        height: document.body.clientHeight - 29,
	    rootVisible:false,
	    dropConfig: {appendOnly:true}
	    
	}); 
	
	data.on('beforeload',function(treeLoader,node){
		this.baseParams.departid = node.attributes.departid,
		this.baseParams.areaid = node.attributes.areaid,
		this.baseParams.departname = node.attributes.departname,
		this.baseParams.isSubject = node.attributes.isSubject
	},data);
	
	usertree.on('checkchange', function(node, checked,flag) {
		
		if(!flag) {
			//不是递归调用的
			if(node.isLeaf()) {
				 var userArr = getUserName() ;
				 document.getElementById("inusername").value=userArr[0];	
				 document.getElementById("inuserid").value=userArr[1];
			}
		}
		
		node.allChildExpand(false,function (){
			 var userArr = getUserName() ;
			 document.getElementById("inusername").value=userArr[0];	
			 document.getElementById("inuserid").value=userArr[1];
		},this,true);
		
		/*
		node.attributes.checked = checked; 
		var valuelist=document.getElementById("inusername").value;
		var valuelistId = document.getElementById("inuserid").value;

		node.expand(true,false,function(){
			node.eachChild(function(child) {  
					child.ui.toggleCheck(checked);   
					child.attributes.checked = checked;  
			 });
		});  */
		
	}, usertree); 
	
	
	
	userroot=new Ext.tree.AsyncTreeNode({
		id:'0',
		text:'显示全部'
	});
	
	usertree.setRootNode(userroot);

	usertree.render('userTreeDiv'); 
	//tree.expandAll();
	userroot.expand();
		
		// 角色
	var TreeRole = Ext.tree;	
	var dataRole = new TreeRole.TreeLoader({
			 //dataUrl:'${pageContext.request.contextPath}/enterpriseQualification.do?method=getRoleList',
  	    dataUrl:'${pageContext.request.contextPath}/interiorEmail.do?method=getRoleList',
		baseParams:{roleId:'${joinRole}'}
	});
		
	var treeRole = new TreeRole.TreePanel({
	    animate:true, 
	    autoScroll:true,
	    containerScroll: true,
	    loader:data,
	    border: true,
	    width: 210,
        height:document.body.clientHeight - 30,
	    rootVisible:false,
	    dropConfig: {appendOnly:true},
	    el:'roleTreeDiv',
	    id:'roleTree',
	    rootVisible:false,
	    containerScroll: true, 
	    loader: dataRole
	    });
	    
	 dataRole.on('beforeload',function(treeLoader,node){
			this.baseParams.type = node.attributes.type,
			this.baseParams.departmentId = node.attributes.departmentId
		},dataRole);
			
	 treeRole.on('checkchange', function(node,checked,flag) {
		 
		 if(!flag) {
			//不是递归调用的
			if(node.isLeaf()) {
				 var userArr = getUserName() ;
				 document.getElementById("inusername").value=userArr[0];	
				 document.getElementById("inuserid").value=userArr[1];
			}
		}
		 
		 node.allChildExpand(false,function (){
			 var userArr = getUserName() ;
			 document.getElementById("inusername").value=userArr[0];	
			 document.getElementById("inuserid").value=userArr[1];
		},this);
	  }, treeRole);  
		
	    var rootRole = new TreeRole.AsyncTreeNode({
	        text: '角色列表',
	        draggable:false,
	        id:'root'
	    });
	    treeRole.setRootNode(rootRole);

	    treeRole.render();
    
});
 //单位/部门树
var tree;
var root;
function tree(divName){
	var Tree = Ext.tree;
	
	document.getElementById(divName).innerHTML = "";

	var data=new Ext.tree.TreeLoader({
		url:'${pageContext.request.contextPath}/department.do?method=getTree'
	});
	
	tree = new Tree.TreePanel({
	    animate:true, 
	    autoScroll:true,
	    containerScroll: true,
	    loader:data,
	    border: true,
	    width: 200,
        height: document.body.clientHeight - 29,
	    rootVisible:false,
	    dropConfig: {appendOnly:true}
	    
	}); 
	
	data.on('beforeload',function(treeLoader,node){
		this.baseParams.departid = node.attributes.departid,
		this.baseParams.areaid = node.attributes.areaid,
		this.baseParams.departname = node.attributes.departname,
		this.baseParams.isSubject = node.attributes.isSubject
	},data);

	tree.on('click',function(node,event){
		//alert(node.attributes.departid+"|"+node.attributes.areaid+"|"+node.attributes.isSubject);
		var divIframe = document.getElementById("divIframe"); 
		if(node.attributes.isSubject == "0"){
				return;
		}else if(node.attributes.isSubject == "1"){
				return;
		}else if(node.attributes.isSubject == "2"){
				return;
		}else {
			//部门 -> 打开单位修改页面
		    document.getElementById("departmentid").value=+node.attributes.departid;
		    goSearch_userMessagerList();
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
</script>
</head>
<body>

<form name="thisForm" method="post" action="" >
 <table align="center" border="0" cellpadding="0" cellspacing="0" width="100%" height="10%">
	<tr>
		<td valign="top" width="15%">
			<div id="tree" style="width: 212px;height: 300px;float:left"></div>
		</td>
		<td valign="top" width="85%">
			<div id="divTab" style="overflow: auto;">
			   <DIV ID="divBtn"></DIV>
			   <table width="750" style="line-height:20px;">
			    <tr><td height="10">&nbsp;</td></tr>
			    <tr>
			      <td width="80" height="50" align="right">所内接收人：</td>
			      <td align="left">
			      	<textarea id="inusername" onpropertychange="showcount()" name="inusername" readonly="readonly" style="width: 650;height:50px;"></textarea>
			     	  <input type="hidden" id="inuserid" name="inuserid"> </td>
			    </tr> 
			 
			    <tr>
			      <td width="80" height="50" align="right" >所外接收人：</td>
			      <td align="left">
			      		<br>
			      		<textarea onpropertychange="showcount()" id="outusername" name="outusername"  style="width: 650;height:20px;overflow: visible;"></textarea>
      					<span style="font-size: 12;color:#7AC5CD; " id="outInput">
      					<br>
      					多个手机号请用英文逗号(",")分割，  如：13588888888,13599999999 </span>
			      </td>
			    </tr>
			    <tr>
			      <td width="80" align="right">短信内容：</td><td align="left">
			         <textarea   id="content"  style="width: 650;height: 200px;overflow: visible" onpropertychange="showcount()";></textarea>
			         <p style="font-size: 12"> 注意：短信内容最多500个汉字或1000个纯英文！ </p>
			      </td>
			    </tr>
			    <tr>
			     	<td colspan="2" height="15" align="right" valign="bottom" style="color: blue;">
			     		发给所内<span style="font-weight: bold;color: red;font-size: 20px;" id="innerperson">0</span>人,
			     		发给所外<span style="font-weight: bold;color: red;font-size: 20px;" id="outerPerson">0</span>人,
			     		已经输入：<span id="count" style="color: red;font-weight: bold;font-size: 20px;">0</span>个字符
			     		<br>注意：如果有重复，人数将会包括重复的人员，将会发送多条信息!
			     	</td>
			    </tr>
			    <tr>
			      <td>&nbsp;</td>
			      <td align="left"  > 
			      <!--  <input value="发送短信" type="button" onclick="submitMsg()"
			        		style="width:64px;height: 54px;" > -->  
			        <div id="sbtBtn" style="text-align:right;width: 100%"> </div>
			        		
			      </td>
			    </tr>
			  </table>
			</div>
			
		</td>
	</tr>
</table>
<input type="hidden" id="parentid" name="parentid" value="">
<input type="hidden" id="areaid" name="areaid" value="">

<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>
<div id="search" style="display:none;">
<br>

</div>
</form>
           <div id="divCheck_select" style="display: none;">
				<div id="my-tabs" style="width: 212px;height: 300px;float:left" >
				   	<div id="tab1">
						<div id="userTreeDiv" style="width: 210px;overflow: hidden;"></div>
					</div>
			
				<div id="tab2">
					<div id="roleTreeDiv" style="width: 150px;overflow: hidden;"></div>
				</div>
			</div>
		
		 </div>
			
</body>
<script type="text/javascript">

function showcount(){
  var s = document.getElementById("content").value;   //文本域
  document.getElementById("count").innerHTML=s.length;
  var d = document.getElementById("inusername").value;   //所内接收人
  var outusername = document.getElementById("outusername").value;   //所外接收人
  if(d==""){
     document.getElementById("innerperson").innerHTML=0;
  }else{
  	document.getElementById("innerperson").innerHTML=d.substring(0,d.length-1).split(",").length;
  }
  
  var outuserLength = 0 ;
  if(outusername != ""){
	  outusername = outusername.replaceAll("，",",");
	  var outuser = outusername.split(",");
	  var outuserBack = outusername.substring(outusername.length-2,outusername.length);
	  if(outuserBack == ",,"){
		  outuser.pop();
		  document.getElementById("outusername").value=outusername.substring(0,(outusername.length - 1));
		  outuserLength = outuser.length - 1;
	  }else{
		  outuserLength = outuser.length;
	  }
	  
  }
  document.getElementById("outerPerson").innerText = outuserLength;
 }
//window 面板 进行查询
function queryWinFun1(obj){
  //  var userId = getChooseValue("userMessagerList");
  //  var userchoose=document.getElementById("inusername").value;
  //  var userchooseId=document.getElementById("inuserid").value;
  //  if(userId!=""){
  //	   	var userlist = userId.split(","); 
  //		    for(var i= 0;i<userlist.length;i++){	
  //		        if(userchoose.indexOf(userlist[i].split("-")[1])==-1){
  //		        	userchoose  +=userlist[i].split("-")[1]+=",";
  //		            userchooseId+=userlist[i].split("-")[0]+=",";
  //		        }
		    
  //		    }
		  
	
//	}
	

	
	var queryWin = null;
	resizable:false;
	var searchDiv = document.getElementById("divCheck_select") ;
	searchDiv.style.display = "block" ;
	document.getElementById("userTreeDiv").style.display = "block"; //人员树
	
	//document.getElementById("inusername").value=userchoose;
	//document.getElementById("inuserid").value=userchooseId;
	document.getElementById("sbtBtn").style.display = "none";  //立即发送按钮
	

	if(!queryWin) { 
	    queryWin = new Ext.Window({
			title: '发送手机短信',
			resizable:false,   //禁止用户 四角 拖动
			contentEl:'divCheck_select',
	     	//renderTo : searchWin,
	     	width: 620,
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
	    	buttons:[{
            	text:'发送',
          		handler:function(){
          		
          		  var mobiles = document.getElementById("outusername").value;
          
				   if(mobiles!="" && !checkmobile(mobiles)){
				     return;
				   }
				   if(document.getElementById("content").value==""){
				     	 alert("请输入短信内容");
				    	 return;
				   }
				   if(document.getElementById("content").value.length>1000){
				   		alert("您输入的内容太长，请分段发送");
				   		return;
				   }	
				    goMsg();
          			queryWin.hide();
            	}
           
        	},{
            	text:'取消',
            	handler:function(){
               		queryWin.hide();
               		document.getElementById("userTreeDiv").style.display = "none" ;
               		document.getElementById("sbtBtn").style.display = "block";  //立即发送按钮
            	}
        	}]
	    });
    }
    new BlockDiv().show();
    queryWin.show();
}

function checkmobile(mobilelist){
      var mlist = document.getElementById("outusername").value+",";
      var mobiles=mlist.split(",");    
             for(var i = 0;i<mobiles.length;i++){
             if(mobiles[i]=="" && i!=mobiles.length-1){
             		 alert("你输入所外的手机号码格式有误，请重新输入");
		        	 return false;
             }else if(mobiles[i]!="" && !(/^1[3|4|5|8][0-9]\d{4,8}$/.test(mobiles[i])))  {
		       		 alert("你输入所外的手机号码格式有误，请重新输入");
		        	 return false;
		     }
		  }
     
	
	  return true;
}

function submitMsg(){         		
	  var mobiles = document.getElementById("outusername").value;
	          
	  if(mobiles!="" && !checkmobile(mobiles)){
			return;
	}
	  if(document.getElementById("content").value==""){
		    alert("请输入短信内容");
		    return;
	 }
	  if(document.getElementById("content").value.length>1000){
			alert("您输入的内容太长，请分段发送");
			return;
	}	
		goMsg();
}

function goMsg()
{
	var userIds = document.getElementById("inuserid").value;
	userIds = userIds.substring(0,userIds.length-1);
	var mobilePhone = document.getElementById("outusername").value;

	var conter = document.getElementById("content").value;
	if(conter ==""){
		alert("请输入要发送的内容！");
		return ;
	}
	if(userIds !="" || mobilePhone!="" ){	
	if(confirm("您确定要发送短信吗？","yes")){
		Ext.Ajax.request({
				method:'POST',
				timeout:900000,
				params : { 
					userIds :userIds,
					conter:conter,
					mobilePhone:mobilePhone,
					rand :Math.random()
				},
				url:"${pageContext.request.contextPath}/user.do?method=userMessager",
				success:function (response,options) {
					  var request = response.responseText;
				 	alert(request);
				},
				failure:function (response,options) {
					try{
					alert("后台出现异常,获取文件信息失败!"+response.responseText);
					}catch(e){alert(e)}
				}
			});
	}	
	}else{
			alert("请先选择人员");
			return;
	}
}
</script>
</html>
