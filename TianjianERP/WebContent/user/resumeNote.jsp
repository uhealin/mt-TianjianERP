<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>招聘计划</title>
<style>

.before{
	border: 0px;
	background-color: #FFFFFF !important;
}

.data_tb {
	background-color: #ffffff;
	text-align:center;
	margin:0 0px;
	width:80%;
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
var tree;
var root;
function tree(divName){
	var Tree = Ext.tree;
	
	document.getElementById(divName).innerHTML = "";

	var data=new Ext.tree.TreeLoader({
		url:'${pageContext.request.contextPath}/job.do?method=tree'
	});
	
	tree = new Tree.TreePanel({
	    animate:true, 
	    autoScroll:true,
	    containerScroll: true,
	    loader:data,
	    border: true,
	    width: 200,
        height: document.body.clientHeight - 29,
	    rootVisible : false,
	    dropConfig: {appendOnly:true}
	    
	}); 
	
	data.on('beforeload',function(treeLoader,node){
		this.baseParams.id = node.id,
		this.baseParams.jobname = node.attributes.jobname
	},data);

	tree.on('checkchange', function(node, checked) {   
		var nodes = tree.getChecked();
		var notename = document.getElementById("notename");
		var noteall = document.getElementById("noteall");
		var mobilephone = document.getElementById("mobilephone");
		var note1 = "";
		var note2 = "";
		var mhone = "";
		for(var i =0;i<nodes.length;i++){
			//alert(nodes[i].id);
			mhone  +=nodes[i].attributes.mobilephone+",";
			note1 += nodes[i].text + ",";
			note2 += nodes[i].text + "=" + nodes[i].attributes.mobilephone + ",";
		}
		notename.value = note1;	
		noteall.value = note2;
		mobilephone.value=mhone;
		
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
	    var tbar = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		defaults: {autoHeight: true,autoWidth:true},
            items:[{
	            text:'保存',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/save.gif',
	            handler:function(){
					save();
				}
       		},'-',{
	            text:'返回',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/back.gif',
	            handler:function(){
					//closeTab(parent.tab);
					window.history.back();
				}
       		},'->'
			]
        });
        
		new ExtButtonPanel({
			desc:'',
			renderTo:'sbtBtn',
			items:[
			{
                text: '保存',
                id:'appSubmit23', 
                icon:'${pageContext.request.contextPath}/img/receive.png' ,
                scale: 'large',
	            handler:function(){
	            	  save();
	   			}
	           },{
                text: '返回',
                id:'appSubmit25', 
                icon:'${pageContext.request.contextPath}/img/back_32.png' ,
                scale: 'large',
	               handler:function(){
	            	  //closeTab(parent.tab);
						window.history.back();
	   			   }
	           }
            ]  
		});        
		
    }
	
	Ext.onReady(function(){
		ext_init();
		tree("tree");
	});

</script>
</head>
<body>
<div id="divBtn"></div>
<form name="thisForm" method="post" action="" >
	<input type="hidden" id="mobilephone" name="mobilephone">
<div style="height:expression(document.body.clientHeight-27);overflow: auto;" >
<table align="center" border="0" cellpadding="0" cellspacing="0" width="100%" height="80%">
	<tr>
		<td valign="top" width="15%">
			<div id="tree" ></div>
		</td>
		<td valign="top" width="85%">
		<span class="formTitle" >发送短信<br/><br/> </span>
		<table  cellpadding="8" cellspacing="0" align="center" class="data_tb" >
		<tr>
		  <td class="data_tb_alignright"  width="15%" align="right">收信人：</td>
		  <td  class="data_tb_content" width="85%"><input class="required" name="notename" type="text" id="notename" size="100" value="${param.mobile}" ></td>
		</tr>
		<tr> 
		  <td class="data_tb_alignright"  width="15%" align="right">短信内容：</td>
		  <td  class="data_tb_content" ><textarea name="specialty" id="specialty" cols="100" rows="30" maxlength="500"  onkeyup="if(this.value.length>500)this.value=this.value.substring(0,500);">${edit.specialty}</textarea></td>
		</tr>
		</table>	
		<center><div id="sbtBtn" ></div></center>		
		</td>
	</tr>
</table>

<input name="noteall" id="noteall" type="hidden">

</div>
</form>
</body>
</html>
<script type="text/javascript">

function save(){
	var noteall = document.getElementById("noteall"); //暂时没有
	var notename = document.getElementById("notename"); //申请人名字
	var specialty = document.getElementById("specialty"); //短信内容
	//var mobilephone = document.getElementById("mobilephone"); //短信内容
	if(notename.value == ""){
		alert("收信人不能为空！");
		return ;
	}
	if(specialty.value == ""){
		alert("短信内容不能为空！");
		return ;
	}
	
	if(confirm("您确定要发送短信吗？","yes")){
		Ext.Ajax.request({
				method:'POST',
				params : { 
					notename :notename.value,
					specialty:specialty.value,
					rand :Math.random()
				},
				url:"${pageContext.request.contextPath}/job.do?method=userMessager",
				success:function (response,options) {
					  var request = response.responseText;
					  if(request == ""){
						  alert("手机号有误，请检查手机号是否正确!");
						  return ;
					  }else{
						  
					 	alert(request);
					  }
				 	window.location="${pageContext.request.contextPath}/job.do?method=resume";
				},
				failure:function (response,options) {
					alert("后台出现异常,获取文件信息失败!");
				}
			});
	}	
	
}

</script>