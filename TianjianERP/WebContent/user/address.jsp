<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>通讯录</title>
<script type="text/javascript">
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
	    rootVisible : true,
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
			//无设置区域 -> 打开区域页面:可以移动部门到区域中
			document.getElementById("parentid").value = node.attributes.departid;
			document.getElementById("areaid").value = node.attributes.areaid;
			document.getElementById("departmentid").value = "";
		}else if(node.attributes.isSubject == "1"){
			//单位 -> 打开单位修改页面
			document.getElementById("parentid").value = node.attributes.departid;
			document.getElementById("areaid").value = "";
			document.getElementById("departmentid").value = "";
		}else if(node.attributes.isSubject == "2"){
			//区域 -> 打开区域页面:只能修改区域负责人
			document.getElementById("parentid").value = node.attributes.departid;
			document.getElementById("areaid").value = node.attributes.areaid;
			document.getElementById("departmentid").value = "";
		}else {
			//部门 -> 打开单位修改页面
			document.getElementById("parentid").value = "";
			document.getElementById("areaid").value = "";
			document.getElementById("departmentid").value = node.attributes.departid;
		}
		goSearch_${tableid}();
	});
	
	root=new Ext.tree.AsyncTreeNode({
	   id:'0',
	   text:'显示全部',
	   departid : "",
	   areaid : ""
	});
	tree.setRootNode(root);

	tree.render('tree'); 
	//tree.expandAll();
	root.expand();
}

	function ext_init(){
	    var tbar = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		defaults: {autoHeight: true,autoWidth:true},
            items:[{
			text:'查询',
			id:'btn-query',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:function(){
				queryWinFun();
			}
		},'-',{
			text:'打印',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/print.gif',
			handler:function () {
				print_${tableid}();
			}
		},'-',{
	            text:'关闭',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function(){
					closeTab(parent.tab);
					//window.history.back();
				}
       		}
			]
        });

    }
    
var queryWin = null;
function queryWinFun(id){
	var searchDiv = document.getElementById("search") ;
	searchDiv.style.display = "" ; 
	    queryWin = new Ext.Window({
			title: '查询',
			contentEl:'search',
	     	renderTo : searchWin,
	     	width: 400,
	     	height:360,
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
            	text:'确定',
          		handler:function(){
          			goSearch_${tableid}();
          			queryWin.hide();
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
}    
    
    
    
Ext.onReady(function(){
	ext_init();
	tree("tree");
});
</script>
</head>
<body>
<form name="thisForm" method="post" action="" >

<table align="center" border="0" cellpadding="0" cellspacing="0" width="100%" height="100%">
<tr><td colspan="2"><div id="divBtn"></div></td></tr>
	<tr>
		<td valign="top" width="15%">
			<div id="tree" ></div>
		</td>
		<td valign="top" width="85%">
			<div style="height:expression(document.body.clientHeight-30);" >
				<mt:DataGridPrintByBean name="${tableid}"  />
			</div>
		</td>
	</tr>
</table>

<input type="hidden" id="parentid" name="parentid" value="">
<input type="hidden" id="areaid" name="areaid" value="">

<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>
<div id="search" style="display:none;">
<br>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
<table border="0" cellpadding="0" cellspacing="0" bgcolor="" width="100%" align="center">
	<tr>
      <td align="right">所属部门：</td>
      <td align=left>
      	<input autoid=123 type="text" id="departmentid"  name="departmentid"  onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" valuemustexist=true noinput=true autoHeight=200 >
      </td>
	</tr>
	<tr>
      <td align="right">楼层：</td>
      <td align=left>
      	<input type="text" id="floor"  name="floor"  >
      </td>
	</tr>	
	<tr>
      <td align="right">房间号：</td>
      <td align=left>
      	<input type="text" id="house"  name="house"  >
      </td>
	</tr>
	<tr>
      <td align="right">姓名：</td>
      <td align=left>
      	<input type="text" id="loginid"  name="loginid"  onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" norestorehint=true  
				 autoid=867 multilevel=true valuemustexist=true autoHeight=200 >
      </td>
	</tr>			
</table>
</div>

</form>

</body>
</html>
