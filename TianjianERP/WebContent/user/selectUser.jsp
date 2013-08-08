<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>选择人员</title>
<script type="text/javascript">
var parentList = window.dialogArguments; //接收传参
var parentUserName = parentList.userName;  //用来显示 人员名称(父)
var parentUserId = parentList.userId;   //用来设置  隐藏的id (父)
var partentWindow = parentList.partentWindowObj; //父窗口window对象

var showName = ""; //设置人员
var hideUserId = ""; //隐藏的id
var root;
var filter;
//创建input
function createInput(type,objId){
	try{
		var objInput = partentWindow.document.createElement("input");
		objInput.type = type; //设置类型
		objInput.name = objId; //设置名称
		objInput.id = objId;  //设置id
		objInput.readOnly = true;  //设置 readOnly=true; 
		partentWindow.document.body.appendChild(objInput);
	}catch(e){
		alert("无法创建文本域,请手动创建!");
	}
}

//回写 人员(点击确定的时候调用)2
function setParentUser(){
	try{
		var domList = partentWindow.document.all;
		var userNamelist = document.getElementById("userNames").value;
		var showName = "";
		var hideUserId = "";
		
		if(userNamelist !=""){
			if(userNamelist.substring(userNamelist.length-1,userNamelist.length) ==","){
				userNamelist = userNamelist.substring(0,userNamelist.length-1);
			 }
			json = userNamelist.split(",");
			for(var i=0;i<json.length;i++){
				 id=json[i].split("~")[0];
				 name=json[i].split("~")[1];
				 showName +=name+",";
				 hideUserId +=id+",";
			} 
			 if(showName.substring(showName.length-1,showName.length) ==","){
				 showName= showName.substring(0,showName.length-1);
				 hideUserId = hideUserId.substring(0,hideUserId.length-1);
			 }
		}
		for(var j = 0;j <domList.length; j++){
			
			if(domList[j].id == parentUserName){ //回写名称
				domList[j].value = showName;
			}
			if(domList[j].id == parentUserId){ 
					domList[j].value = hideUserId;
			}
		}
	}catch(e){
		alert("无法回写人员数据,请检测是否已连接服务器!");
	}
}

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

function seUserTree(){
	try{
		 //如果父窗口的id和name 都未创建， 我帮它创建
		 if(!partentWindow.document.getElementById(parentUserId) && !partentWindow.document.getElementById(parentUserName)){
			 createInput("text",parentUserName);
			 createInput("hidden",parentUserId);
		 }
		 hideUserId = partentWindow.document.getElementById(parentUserId).value;
		 if(hideUserId.substring(hideUserId.length-1,hideUserId.length) !=","){
			 hideUserId = hideUserId+",";
		 }
		document.getElementById("Users").value = hideUserId ;
	}catch(e){
		alert("初始化设置值出错!");
	}
	
	var Tree = Ext.tree;
			
	var data = new Tree.TreeLoader({
		 dataUrl:'${pageContext.request.contextPath}/user.do?method=departmentTree&hideAreaChecked=true&hideOrgan=true&commonlyUsed=true&onLineUser=true',	
		 baseParams:{joinUser:'${joinUser}',joinUserDepartmentId:'${joinUserDepartmentId}'}
	});
	
	var tree = new Tree.TreePanel({
        el:'departmentTree',
        id:'userTree',
        autoScroll:true,
        animate:true,
        height:document.body.clientHeight-150, 
        width:180,
        rootVisible:false,
        containerScroll: true, 
        loader: data
    });
 
    data.on('beforeload',function(treeLoader,node){
		this.baseParams.departid = node.attributes.departid,
		this.baseParams.areaid = node.attributes.areaid,
		this.baseParams.departname = node.attributes.departname,
		this.baseParams.isSubject = node.attributes.isSubject
	},data);
    
    tree.on('dblclick', function(node, checke){ 
    	var b=confirm("确实要删除自定义用户:"+node.text+" 吗？");
    	if(b==true){
		var attr=node.attributes;
		var uuid=attr["id"];
	    var url="${pageContext.request.contextPath}/user.do?method=deleteuserFav&uuid="+uuid;
		//var request="&ids="+uids+"&rand="+Math.random();
		var result=ajaxLoadPageSynch(url,"");
		alert(result);
		root.reload();
		
    	}
    	}, tree); 
    
	tree.on('click', function(node, checked) {
		var attr=node.attributes;
		var uids=attr["fav_user_ids"];
		if(uids){
			
		    document.getElementById("Users").value=attr["fav_user_ids"];
		    uids=uids.substring(1);
		   //alert(uids);
		    var url="${pageContext.request.contextPath}/department.do?method=getUserTree";
			var request="&ids="+uids+"&rand="+Math.random();
			var names="";
			
			$.getJSON(url+request,{},function(jarr){
				for(var i=0;i<jarr.length;i++){
					//alert(jarr[i]["name"]);
					var val=jarr[i]["id"]+"~"+jarr[i]["name"];
					names+=val+",";
					//alert(names);
				}
				document.getElementById("userNames").value=names;
			    //loadUserToDiv();
			    hadSelectUser();
			});
		    //seUserTree();
		    //createRoleTree();
		    //var url="${pageContext.request.contextPath}/department.do?method=getUserTree";
			//var request="&ids="+users+"&rand="+Math.random();
			//var result=ajaxLoadPageSynch(url,request);
			//var json=eval("("+result+")");
		    //loadUserToDiv();
		    //hadSelectUser();
			//return;
		}
		node.expand();   
		addCon(node.attributes["departid"]);   //departid
		node.attributes.checked = checked; 
			node.eachChild(function(child) {  
				child.ui.toggleCheck(checked);   
				child.attributes.checked = checked;   
				child.fireEvent('checkchange', child, checked);   
			});
		
	}, tree);  
	 root=new Ext.tree.AsyncTreeNode({
		   id:'0',
		   draggable:false,
		   text:'显示全部'
		});
    tree.setRootNode(root);

    tree.render(); 
   /* 
    filter = new Ext.tree.TreeFilter(tree, {
  	  clearBlank: true,
  	  autoClear: true
  	 });
   
    filter.filterBy(function(n){
        alert(n.text);
        var textval = n.text;

        return n["text"]!="总所荣誉注税" ;
       });

       // hide empty packages that weren't filtered
       hiddenPkgs = [];
       tree.root.cascade(function(n) {
           if(!n.isLeaf()&& n.ui.ctNode.offsetHeight<3){
               n.ui.hide();
               hiddenPkgs.push(n);
           }
       });
       */
}

var treeRoleRoot;

//角色树
function createRoleTree(){
	var Tree = Ext.tree;
	var treeRoleData = new Tree.TreeLoader({
		 dataUrl:'${pageContext.request.contextPath}/department.do?method=getRoleTree&rand='+Math.random(),
		 baseParams:{roleId:'${joinRole}'}
	});
	var treeRoleUser = new Tree.TreePanel({
      	el:'roleTreeDiv',
        id:'roleTree',
        autoScroll:true,
        animate:true,
        height:document.body.clientHeight-150, 
        width:180,
        rootVisible:false,
        containerScroll: true, 
        loader:treeRoleData
    });
	treeRoleUser.on('click', function(node, checked) {   
		node.expand();   
		addRoleUser(node.id);
		node.attributes.checked = checked; 
			node.eachChild(function(child) {  
				child.ui.toggleCheck(checked);   
				child.attributes.checked = checked;   
				child.fireEvent('checkchange', child, checked);   
			});
		
	}, treeRoleUser);
	
	treeRoleData.on('beforeload',function(treeLoader,node){
		//this.baseParams.departmentId = node.attributes.departmentId;
		this.baseParams.id = node.id;
	},treeRoleData);
	
    treeRoleUser.on('checkchange', function(node, checked) {   
		node.allChildExpand(false,function (){

		},this);
	}, treeRoleUser);  
    
     treeRoleRoot = new Tree.AsyncTreeNode({
        text: '角色列表',
        draggable:false,
        id:'0'
    });
    treeRoleUser.setRootNode(treeRoleRoot);
    treeRoleUser.render();
    
}
function show_hideDiv(obj){  
	if(obj.innerText.indexOf("角色")>-1){
		document.getElementById("departmentTree").style.display="none";
		document.getElementById("roleTreeDiv").style.display="block";
		obj.innerText = "按部门选择";
	}else{
		obj.innerText = "按角色选择";
		document.getElementById("roleTreeDiv").style.display="none";
		document.getElementById("departmentTree").style.display="block";
	}
}
</script>
<style type="text/css">
.secherInput{
	border:#8db2e3 1px solid;
	width: 240px;
	height: 30px;
	line-height:20px;
	font-size: 15px; 
	border-right-width: 0px;
}
</style>
</head>
<body onload="seUserTree(),createRoleTree(),loadUserToDiv()">
	<br/>
	<div align="left" style="margin-left:30px;">
		<table>
			<tr>
			<td>
				姓名模糊查询：<input id="keywords" name="keywords"  class="secherInput" maxlength="10" onkeydown="if(event.keyCode==13){goSerach()};" ><input type="button" style="height: 30px;width:32px; border:#8db2e3 1px solid;border-left-width: 0px;padding-left: 0px;background-color: transparent;background-image: url('${pageContext.request.contextPath}/images/search48.png');"onclick="goSerach()">
			</td>
			</tr>
		</table>
	</div>
	<br>
	<input type="hidden" id="Users" name="Users" /><!-- 这个是可查看人员树选中的人员id -->
	<input type="hidden" id="userNames" name="userNames" /><!-- 这个是可查看人员树选中的人员name -->
	<span style="margin-left: 15px;">
		<a id="aslectType" href="javascript:void(0)" onclick="show_hideDiv(this)">按角色选择</a>
		
		<span style="text-align: right;width: 80%;"><input type="button" onclick="hadSelectUser()" value="查看已选择人员" class="flyBT" /> &nbsp;<input type="button" value="清空选择" onclick="goCleanUser();" class="flyBT">
			<input type="button" value="全选" onclick="check('全选');" class="flyBT">
			<input type="button" value="反选" onclick="check('反选');" class="flyBT"></span>
	</span>
	<hr>
	<table border="0" >
	        
			<tr>
				<td>
					<div id="departmentTree" style="display: block;"></div>
					<div id="roleTreeDiv" style="display: none;"></div>
				</td>
				<td>
				    
					<div id="playUser" style="padding-left: 10px;padding-top:5px;height:270px;margin-top: 5px;overflow: auto;width: 310px;"><div align="center">尚未选择人员!</div></div>
				</td>
			</tr>
		</table>
		<div align="center" style="padding-top: 10px;">
			<span style="color: red">(双击自定义用户可删除)</span>
			<input type="button" value="确定" onclick="sureAdd();" class="flyBT">
			<input type="button" value="保存为自定义" onclick="doSaveFav();" class="flyBT">
			<input type="button" value="关闭" onclick="window.close();" class="flyBT">
		</div>
</body>
<script type="text/javascript">
//初始化时 加载人员
function loadUserToDiv(){
	var users = document.getElementById("Users").value;
	
	if(users !=""){
		try{
			var url="${pageContext.request.contextPath}/department.do?method=getUserTree";
			var request="&ids="+users+"&rand="+Math.random();
			var result=ajaxLoadPageSynch(url,request);
			var json=eval("("+result+")");
			
			var newTable = createTable(json,"background: #CAE8EA  no-repeat;");
			createObject(newTable); //newTable
			checkedAdd();//检测选中
		}catch(e){
			alert("无法获取部门里的人员，可能服务器已断开!");
		}
	}
}

function loadUserToDiv2(){
	var users = document.getElementById("Users").value;
	
	if(users !=""){
		try{
			var url="${pageContext.request.contextPath}/department.do?method=getUserTree";
			var request="&ids="+users+"&rand="+Math.random();
			var result=ajaxLoadPageSynch(url,request);
			var json=eval("("+result+")");
			
			var newTable = createTable(json,"background: #CAE8EA  no-repeat;");
			createObject(newTable); //newTable
			checkedAdd();//检测选中
			hadSelectUser();
		}catch(e){
			alert("无法获取部门里的人员，可能服务器已断开!");
		}
	}
}

//查询部门里人员的数据
function getUserTreeById(autoid,keywords){
	try{
		var url="${pageContext.request.contextPath}/department.do?method=getUserTree";
		var request="&autoid="+autoid+"&keywords="+keywords+"&rand="+Math.random();
		var result=ajaxLoadPageSynch(url,request);
		var json=eval("("+result+")");
		return json;
	}catch(e){
		alert("无法获取部门里的人员，可能服务器已断开!");
	}
}

//创建div(用来显示人员)
function createObject(html){
	try{
		
		var myDiv=document.createElement("div");
		myDiv.innerHTML=html;
		if(html.indexOf("td")<0){
			myDiv.innerHTML="<div align=\"center\">当前所选部门无人员!</div";	
		}
		myDiv.id="playTreeUser";
		document.getElementById("playUser").innerHTML="";
		document.getElementById("playUser").appendChild(myDiv);
	}catch(e){
		alert("当前浏览器不支持或当前对象不存在!");
	}
}

//创建table 
function createTable(json,tableColor){
	try{
		
		if(json){
			var conHtml = "<table style=\"border:#8db2e3 1px solid; BORDER-COLLAPSE: collapse;margin-right:10px;"+tableColor+"\" >" ;
			var id="";
			var name="";
			for(var i=0;i<json.length;i++){
				 id=json[i].id;
				 name=json[i].name;
				 departName=json[i].departName;
				 var check="";
				 if(document.getElementById("Users").value.indexOf(id+",")>-1){
				 	check="checked='checked'";
				 }
				 if(i%2 ==0){
					 conHtml=conHtml+"<tr><td style='width:150;border-right:1px solid #C1DAD7;padding-left:10px;'>";
					 conHtml=conHtml+"<input type=\"checkbox\" id=\"userid"+i+"\"  userId=\""+id+"\" style=\"border:0px;\" onclick=\"addUsrs(this)\" "+check+" title='"+departName+" - "+name+"'　 name=\"userid\" userName=\""+id+"~"+name+"\"/>&nbsp;"+name+"</td>";
				 }
				 if(i%2 !=0){
				 	 conHtml=conHtml+"<td  style='width:150;padding-left:10px;'><input type=\"checkbox\"  userId=\""+id+"\"  id=\"userid"+i+"\"  "+check+" style=\"border:0px;\"  title='"+departName+" - "+name+"' onclick=\"addUsrs(this)\"  userName=\""+id+"~"+name+"\" name=\"userid\" />&nbsp;"+name+"</td>";
					 conHtml=conHtml+"</tr>";
				 }
			} 
			conHtml=conHtml+"</table>";
			
			return conHtml;
		}
	}catch(e){
		alert("构建table失败!");
	}
}

//点击角色
function addRoleUser(roleId){
	try{
		if(roleId ==""){
			return ;
		}
		var url="${pageContext.request.contextPath}/department.do?method=getUserTree";
		var request="&roleId="+roleId+"&rand="+Math.random();
		var result=ajaxLoadPageSynch(url,request);
		var json=eval("("+result+")");
		
		var newTable = createTable(json,"");
		createObject(newTable); //newTable
		checkedAdd();//检测选中
	}catch(e){
		alert("无法获取部门里的人员，可能服务器已断开!");
	}
}

//点击树(左边的部门)用来加载部门里的人员
function addCon(autoid){
	try{
		autoid = autoid.replace("depart_","");
		var json=getUserTreeById(autoid,"");
		var newTable = createTable(json,"");
		createObject(newTable); //newTable
		checkedAdd();//检测选中
	}catch(e){
		//alert("构建人员视图失败!");
	}
}

//点击复选框调用的函数
function addUsrs(obj){
	try{
		var users=document.getElementById("Users").value;
		var userNames=document.getElementById("userNames").value;
		var addUser=obj.userId;
		var addUserName=obj.userName;
		if(obj.checked){
			if(users.indexOf(addUser+",")<0){ //不存在 再累加
				users=users+addUser+",";
				document.getElementById("Users").value=users;
			}
			if(userNames.indexOf(addUserName+",")<0){//不存在 再累加
				userNames=userNames+addUserName+",";
			}
		}else{
			if(users.indexOf(addUser+",")>-1){
				document.getElementById("Users").value=users.replace(addUser+",","");
			}
			if(userNames.indexOf(addUserName+",")>-1){
				userNames=userNames.replace(addUserName+",","");
			}
		}
		document.getElementById("userNames").value=userNames;
	}catch(e){
		alert("无法获取选择的对象，勾选人员失败！");
	}

}

//检测 如果有选中的，就添加到放到隐藏域
function checkedAdd(){
	try{
		
		var listCheck = document.getElementsByName("userid");
		if(listCheck){
			for(var i=0;i<listCheck.length;i++){
				if(listCheck[i].checked){
					if(document.getElementById("userid"+i)){
						addUsrs(document.getElementById("userid"+i));
					}
				}
			}
		}
	}catch(e){
		alert("检测选中项失败，无法加载数据!");
	}
}


//点击确定的时候 调用
function sureAdd(){ 
	try{
		checkedAdd(); //检测 选中项
		setParentUser(); //回写人员
		window.close();
	}catch(e){
		alert("得不到服务器网络连接,操作失败!");
	}
}

//查看已勾选人员
function hadSelectUser(){
	try{
		var userJson=document.getElementById("userNames").value;
		if(userJson.substring(userJson.length-1,userJson.length)==","){
			userJson=userJson.substring(0,userJson.length-1);
		}
		//alert("userjson:"+userJson);
		//创建已选择人员的table
		if(userJson!=""){
			var json=userJson.split(",");
			var id="";
			var name="";
			var conHtml = "<table style=\"border:#8db2e3 1px solid; BORDER-COLLAPSE: collapse;margin-right:10px;background: #CAE8EA  no-repeat; \" >" ;
			for(var i=0;i<json.length;i++){
				 id=json[i].split("~")[0];
				 name=json[i].split("~")[1];
				 var check="";
				 var lsUser = document.getElementById("Users").value;
				 if(lsUser.substring(lsUser.length-1,lsUser.length) !=","){
				 	document.getElementById("Users").value = document.getElementById("Users").value+",";
				 }
				 if(document.getElementById("Users").value.indexOf(id+",")>-1){
				 	check="checked='checked'";
				 }
				 if(i%2 ==0){
					 conHtml=conHtml+"<tr><td style='width:150;border-right:1px solid #C1DAD7;padding-left:10px;'>";
					 conHtml=conHtml+"<input style=\"border:0px;\" onclick=\"addUsrs(this)\" "+check+" type=\"checkbox\" id=\"userid"+i+"\" userId=\""+id+"\" name=\"userid\" userName=\""+id+"~"+name+"\"/>&nbsp;"+name+"</td>";
				 }
				 if(i%2 !=0){
				 	 conHtml=conHtml+"<td style='width:150;padding-left:10px;'><input "+check+" style=\"border:0px;\"  onclick=\"addUsrs(this)\" type=\"checkbox\" id=\"userid"+i+"\" userId=\""+id+"\" userName=\""+id+"~"+name+"\" name=\"userid\" />&nbsp;"+name+"</td>";
					 conHtml=conHtml+"</tr>";
				 }
			} 
			conHtml=conHtml+"</table>";
			
			createObject(conHtml);
		}else{
			document.getElementById("playUser").innerHTML = "<div align=\"center\">尚未选择人员!</div>";
		} 
	}catch(e){
		alert("无法查看已勾选人员，网络连接失败，失败原因"+e);
	}
}

 
//搜索所调用的函数
function goSerach(){
	try{
		var keywords = document.getElementById("keywords").value;
		if(keywords == ""){
			return ;
		}
		var json = getUserTreeById("",keywords);
		
		var newTable = createTable(json,"background: #e9f0fd  no-repeat;");
		 
		document.getElementById("playUser").innerHTML = "";
		document.getElementById("playUser").innerHTML = newTable;
	}catch(e){
		alert("查询失败，服务器连接故障!");
	}
}

//清空选择
function goCleanUser(){
	try{
		document.getElementById("Users").value="";
		document.getElementById("userNames").value="";
		var listCheck=document.getElementsByName("userid");
		if(listCheck){
			for(var i=0;i<listCheck.length;i++){
				if(listCheck[i].checked){
					document.getElementById("userid"+i).checked=false;
				}
			}
		}
		partentWindow.document.getElementById(parentUserId).value = ""; //清空 父窗口的id
		partentWindow.document.getElementById(parentUserName).value = ""; //情况 父窗口的名称
	}catch(e){
		alert("清空失败,当前对象不存在!");
	}
}

//全选、反选 事件
function check(obj){
	try{
		var listCheck = document.getElementsByName("userid");
		
		if(listCheck){
			
			if(obj == "全选"){
				for(var i=0;i<listCheck.length;i++){
					if(listCheck[i].checked == false){
						if(document.getElementById("userid"+i)){
							document.getElementById("userid"+i).checked = true;
							addUsrs(document.getElementById("userid"+i));
						}
					}
				}
			}
			
			if(obj == "反选"){
				for(var i=0;i<listCheck.length;i++){
					try{
						
						if(listCheck[i].checked){
							document.getElementById("userid"+i).checked=false;
							
						}else{
							document.getElementById("userid"+i).checked=true;
						}
						if(document.getElementById("userid"+i)){
							addUsrs(document.getElementById("userid"+i));
						}
					}catch(e){
						alert("反选对象不存在！");
					}
				}
			}
			
			if(obj == "全不选"){
				for(var i=0;i<listCheck.length;i++){
					if(listCheck[i].checked){
						document.getElementById("userid"+i).checked=false;
						addUsrs(document.getElementById("userid"+i));
					}
				}
			}
		}
	}catch(e){
		alert("无法访问对象,操作失败!");
	}

	
	
}

function doSaveFav(){
	Ext.MessageBox.prompt("保存自定义用户","请输入自定义类型名字",function(e,text){
		if(e!='ok'||text=='')return;
		var url="${pageContext.request.contextPath}/user.do";
		var fav_user_ids=document.getElementById("Users").value;
		$.post(url,{method:"doSaveFav",name:text,fav_user_ids:fav_user_ids},function(str){
			
			
			document.getElementById("aslectType").innerText = "按角色选择";
			document.getElementById("roleTreeDiv").style.display="none";
			document.getElementById("departmentTree").style.display="block";
			alert(str);
			root.reload();
			});
	});
}

</script>
</html>