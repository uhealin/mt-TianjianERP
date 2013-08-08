<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>部门列表</title>
<script type="text/javascript">
//单位/部门树
var tree;
var root;
function tree(divName){
	var Tree = Ext.tree;
	
	document.getElementById(divName).innerHTML = "";

	var data=new Ext.tree.TreeLoader({
		url:'${pageContext.request.contextPath}/employment.do?method=emTree'
	});
	
	 var treeToolBar = new Ext.Toolbar({
			items:[
				{ 
					text:'刷新',
					icon:'${pageContext.request.contextPath}/img/setting.gif' ,
					handler:function(){
						var node = tree.getSelectionModel().getSelectedNode();
						if(node) {
							var parentId = node.id;
							typeWinFun(parentId);
						} else {
							alert("请先选择一个上级分类");
						}	
					}
				}
			]
		});
	
	tree = new Tree.TreePanel({
	    animate:true, 
	    autoScroll:true,
	    containerScroll: true,
	    loader:data,
	    border: true,
	    width: 300,
        height: document.body.clientHeight,
	    rootVisible:false,
	    dropConfig: {appendOnly:true}
	    ,toolbar:[treeToolBar]
	}); 
	
	data.on('beforeload',function(treeLoader,node){
		this.baseParams.departid = node.attributes.departid,
		this.baseParams.areaid = node.attributes.areaid,
		this.baseParams.departname = node.attributes.departname,
		this.baseParams.isSubject = node.attributes.isSubject,
		this.baseParams.emtype = node.attributes.emtype
	},data);

	tree.on('click',function(node,event){
		//alert(node.attributes.departid+"|"+node.attributes.areaid+"|"+node.attributes.isSubject);
		var divIframe = document.getElementById("divIframe"); 
		var fform=document.forms["fform"];
		if(node.attributes.isSubject == "0"){
			//无设置区域 -> 打开区域页面:可以移动部门到区域中
			//divIframe.src = "${pageContext.request.contextPath}/department.do?method=area&issubject="+node.attributes.isSubject+"&organid="+node.attributes.departid+"&ifAdd=1&rand="+Math.random();
		}else if(node.attributes.isSubject == "1"){
			//单位 -> 打开单位修改页面
			//divIframe.src = "${pageContext.request.contextPath}/department.do?method=organ&issubject="+node.attributes.isSubject+"&departid="+node.attributes.departid+"&rand="+Math.random();
		}else if(node.attributes.isSubject == "2"){
			//区域 -> 打开区域页面:只能修改区域负责人
			//divIframe.src = "${pageContext.request.contextPath}/department.do?method=area&issubject="+node.attributes.isSubject+"&departid="+node.attributes.areaid+"&rand="+Math.random();
		}else if(node.attributes.isSubject == "3"){
			//区域 -> 打开区域页面:只能修改区域负责人
			//divIframe.src = "${pageContext.request.contextPath}/department.do?method=area&issubject="+node.attributes.isSubject+"&departid="+node.attributes.areaid+"&rand="+Math.random();
			//divIframe.src = "${pageContext.request.contextPath}/employment.do?method=ListEm&judge=userReamd&departmentid="+node.attributes.departid;
            fform.action="${pageContext.request.contextPath}/employment.do?method=ListEm&judge=userReamd";
            fform.departmentid.value=node.attributes.departid;
            var pn=node;
            
            while(!pn.attributes.emtype&&pn.parentNode){
            	pn=pn.parentNode;
            }
            
            fform.emtype.value=pn.attributes.emtype;
            fform.submit();
            stopWaiting();
		}else if(node.attributes.isSubject=="usersearch"){
	          fform.action="${pageContext.request.contextPath}/employment.do?method=ListEm&judge=userReamd&queryId="+node.id;
            fform.departmentid.value=node.attributes.departid;
            fform.emtype.value=node.attributes.emtype;
            fform.submit();
            stopWaiting();
		}else if(node.attributes.isSubject=="item"){
            fform.action=node.attributes.url;
            fform.departmentid.value=node.attributes.departid;
            fform.emtype.value=node.attributes.emtype;
            fform.submit();
            stopWaiting();
		}else if(node.attributes.isSubject == "emtype") {
			//部门 -> 打开单位修改页面
			//divIframe.src = "${pageContext.request.contextPath}/department.do?method=depart&issubject="+node.attributes.isSubject+"&departid="+node.attributes.departid+"&rand="+Math.random();
			 //var deparId=node.id.split("_")[0];
            var pn=node;
            
            while(!pn.attributes.emtype&&pn.parentNode){
            	pn=pn.parentNode;
            }
            
            fform.emtype.value=pn.attributes.emtype;
            fform.action="${pageContext.request.contextPath}/employment.do?method=ListEm&judge=userReamd";
            fform.departmentid.value="";
            //fform.emtype.value=emtype;
            fform.submit();
            stopWaiting();
			//divIframe.src = "${pageContext.request.contextPath}/user.do?method=ListEm&judge=userReamd&departmentid="+deparId+"&emtype="+emtype;
           
			//divIframe.docuemnt.getElementById("departmentid").value=deparId;
			//divIframe.docuemnt.getElementById("emtype").value=deparId;
			//divIframe.docuemet.goSearch_user();
		}
		else if(node.attributes.isSubject == "4") {
			//部门 -> 打开单位修改页面
			//divIframe.src = "${pageContext.request.contextPath}/department.do?method=depart&issubject="+node.attributes.isSubject+"&departid="+node.attributes.departid+"&rand="+Math.random();
			 var deparId=node.id.split("_")[0];
            var emtype=encodeURIComponent(node.id.split("_")[1]);
            fform.action="${pageContext.request.contextPath}/employment.do?method=ListEm&judge=userReamd";
            fform.departmentid.value=deparId;
            fform.emtype.value=emtype;
            fform.submit();
            stopWaiting();
			//divIframe.src = "${pageContext.request.contextPath}/user.do?method=ListEm&judge=userReamd&departmentid="+deparId+"&emtype="+emtype;
           
			//divIframe.docuemnt.getElementById("departmentid").value=deparId;
			//divIframe.docuemnt.getElementById("emtype").value=deparId;
			//divIframe.docuemet.goSearch_user();
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
	//主菜单
	var divIframe1 = document.getElementById("divIframe"); 
	
	
	var tbar_divBtn = new Ext.Toolbar({
		//renderTo: "divBtn",
 		items:[{
			text:'新增单位',
			id:'btn-query',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/add.gif',
			handler:function () {
				divIframe1.src = "${pageContext.request.contextPath}/department.do?method=organ&issubject=1&ifAdd=1&rand="+Math.random();
			}
		},'-',{
			text:'新增区域',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/add.gif',
            handler:function () {
            	var organid = "";
            	try{
            		var node = tree.getSelectionModel().getSelectedNode();
            		organid = node.attributes.departid;
            		if(node.attributes.isSubject == 3){
            			organid = node.attributes.bparentid;
            		}
					//alert(node.attributes.departid+"|"+node.attributes.areaid+"|"+node.attributes.isSubject);
				}catch(e){}
				divIframe1.src = "${pageContext.request.contextPath}/department.do?method=area&organid="+organid+"&issubject=2&ifAdd=1&rand="+Math.random();
			}
      	},'-',{
			text:'部门排序设置',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/flow.gif',
			handler:function () {
				setOrderBy();
			}
		},'-',{
			text:'关闭',
			cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/close.gif',
			handler:function () {
				closeTab(parent.tab);
			}
		},'-',new Ext.Toolbar.Fill()]
	});
	
	//副菜单
	var tbar_divTab = new Ext.Toolbar({
		//renderTo: "divTab",
 		items:[{
			text:'修改',
			id:'btn-divTab',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/edit.gif',
			handler:function () {
				var divIframe = document.frames["divIframe"];
				if(divIframe.ifAdd == ""){
					divIframe.ifAdd = "1";
					divIframe.update();
					
					var btn = Ext.getCmp("btn-divTab");
					btn.setText("保存");
					btn.setIcon("${pageContext.request.contextPath}/img/save.gif");
				}else{
					divIframe.save();
				}
			}
		},'-',{
			text:'新建下级部门',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/add.gif',
            handler:function () {
            	try{
					var node = tree.getSelectionModel().getSelectedNode();
					var parentid = node.attributes.departid;
					var areaid = node.attributes.areaid;
					var issubject = node.attributes.isSubject;
					//alert(node.attributes.departid+"|"+node.attributes.areaid+"|"+node.attributes.isSubject);
					divIframe1.src = "${pageContext.request.contextPath}/department.do?method=depart&issubject=3&parentid="+parentid+"&areaid="+areaid+"&ifAdd=1&rand="+Math.random();
				}catch(e){
					alert("请选中左边的单位/区域/部门");
				}
			}
      	},'-',{
			text:'删除当前单位/区域/部门',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/delete.gif',
			handler:function () {
				try{
					var node = tree.getSelectionModel().getSelectedNode();
					//alert(node.attributes.departid+"|"+node.attributes.areaid+"|"+node.attributes.isSubject);
					
					if(node.attributes.departid == "555555" && node.attributes.isSubject == "1"){
						alert("授权机构单位不能删除！");
						return;
					}
					if(node.attributes.isSubject == "0"){
						alert("这个不是区域节点，不能删除");
						return;
					}
					
					Ext.Ajax.request({
						method:'POST',
						params : { 
							departid : node.attributes.departid,
							areaid : node.attributes.areaid,
							issubject : node.attributes.isSubject
						},
						url:"${pageContext.request.contextPath}/department.do?method=del&rand="+Math.random(),
						success:function (response,options) {
							var result = response.responseText;
							alert(result.substring(3));	
							if(result.indexOf("OK")>-1){
								//成功
								//页面指向授权机构555555
								divIframe1.src = "${pageContext.request.contextPath}/department.do?method=organ&issubject=1&departid=555555&rand="+Math.random();
								refreshTree();
							}
						},
						failure:function (response,options) {
							alert("后台出现异常,获取文件信息失败!");
						}
					});
				}catch(e){
					alert("请选中左边要删除的单位/区域/部门");
				}
			}
		}]
	});	 
	
	      
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

var setOrderByWin = null;
function setOrderBy() {

	var url="${pageContext.request.contextPath}/department.do?method=setOrderBy&flag=window&rand=" + Math.random();
		
	if(!setOrderByWin) { 
		setOrderByWin = new Ext.Window({
	     	renderTo : Ext.getBody(),
	     	width: 500,
	     	id:'setOrderByWin',
	     	height:350,
	     	title:'设置部门排序',
	     	closable:'flase',
        	closeAction:'hide',
       	    listeners : {
	         	'hide':{
	         		fn: function () {
	         			new BlockDiv().hidden();
						setOrderByWin.hide();
						
					}
				}
	        },
	       	html:'<iframe name="orderByFrame" id="orderByFrame" scrolling="no" frameborder="0" width="100%" height="100%" src="' + url + '"></iframe>',
        	layout:'fit'
	    });
    } else {
    	document.getElementById("orderByFrame").src = url;
    }
	
	new BlockDiv().show();
	setOrderByWin.show(); 
}

</script>
</head>
<body>
<div id="divBtn"></div>
<form name="fform" target="divIframe" method="post">
   <input type="hidden" name="departmentid" />
   <input type="hidden" name="emtype" />  
   <input type="hidden" name="qryWhere_em" /> 
   <input type="hidden" name="qryJoin_em" /> 
</form>
<form name="thisForm" method="post" action="" class="autoHeightDiv">


<table align="center" border="0" cellpadding="0" cellspacing="0" width="100%" height="100%">
	<tr>
		<td valign="top" width="15%">
			<div id="tree" ></div>
		</td>
		<td valign="top" width="85%">
			<div id="divTab"></div>
			<div style="height:expression(document.body.clientHeight);" >
				<iframe id="divIframe" name="divIframe" scrolling="auto" frameborder="0" width="100%" height="100%"  ></iframe>
			</div>
		</td>
	</tr>
</table>

</form>
</body>
</html>
<script type="text/javascript">

Ext.onReady(function(){
	ext_init();
	tree("tree");
});
</script>