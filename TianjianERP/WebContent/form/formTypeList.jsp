<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript">
//EXT初始化
function ext_init(){
	new Ext.Toolbar({
		renderTo:'GridDiv_formTypeList',
		items:[
			{ 
				text:'新增',
				icon:'${pageContext.request.contextPath}/img/add.gif' ,
				handler:function(){
					add();
				}
			},'-',{ 
				text:'修改',
				icon:'${pageContext.request.contextPath}/img/edit.gif' ,
				handler:function(){
					edit();
				}
			},'-',{ 
				text:'删除',
				icon:'${pageContext.request.contextPath}/img/delete.gif' ,
				handler:function(){
					remove();
				}
			},'-',{ 
				text:'查询',
				icon:'${pageContext.request.contextPath}/img/query.gif' ,
				handler:function(){
					queryWinFun();
				}
			},'-',{ 
				text:'查看所有',
				icon:'${pageContext.request.contextPath}/img/refresh.gif' ,
				handler:function(){
				    var parentId ="${parentId}";
				    window.location = "${pageContext.request.contextPath}/formDefine.do?method=formTypeList&parentId="+parentId;
				}
			}
		]
	});
}
var queryWin = null;
function queryWinFun() {
	document.getElementById("search").style.display = "";
	if(queryWin == null) { 
	    queryWin = new Ext.Window({
			title: '查询',
			width: 400,
			height:200,
			contentEl:'search', 
	        closeAction:'hide',
	        modal:true,
	        listeners:{
				'hide':{fn: function () {
					 document.getElementById("search").style.display = "none";
				}}
			},
	        layout:'fit',
	        contentEl:'search',
		     buttons:[{
	            text:'确定',
	            icon:'${pageContext.request.contextPath}/img/confirm.gif',
	          	handler:function() {
	        		goSearch_formTypeList(2);
	          		queryWin.hide();
	          	}
	        },{
	            text:'重置',
	            icon:'${pageContext.request.contextPath}/img/refresh.gif',
	            handler:function(){
	            	reset("thisForm");
	            }
	        },{
	            text:'取消',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function(){
	            	queryWin.hide();
	            }
	        }]
	    });
	}
	 queryWin.show();
}

window.attachEvent('onload',ext_init);
</script>
</head>

<body>

<div class="autoHeightDiv">
	<mt:DataGridPrintByBean name="formTypeList" />
</div>

<div id="search" style="display: none;">
	<form name="thisForm" id="thisForm" method="post">
		<br/>
		<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
		<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
		
		<table border="0" cellpadding="5" cellspacing="10" width="100%" align="center">
			<tr>
				<td align="right">分类名称：</td>
				<td><input type="text" name="formTypeName" id="formTypeName" class="required"/></td>
			</tr>
		</table>	
	</form>			
</div>
</body>

<script type="text/javascript">
var parentId = "${parentId}";
 
//新增
function add() {
	window.location = "${pageContext.request.contextPath}/formDefine.do?method=formTypeAdd&parentId=" + parentId;
}

//修改
function edit() {
	var chooseValue = document.getElementById("chooseValue_formTypeList").value;
	if(chooseValue=="") {
		alert("请选择需要修改的分类！");
		return;
	} else {
		window.location = "${pageContext.request.contextPath}/formDefine.do?method=formTypeEdit&formTypeId=" + chooseValue + "&parentId=" + parentId;
	}
}

//删除
function remove() {

	var chooseValue = document.getElementById("chooseValue_formTypeList").value;
	 
	if(chooseValue=="") {
		alert("请选择需要删除的分类！");
		return;
	} else {
		 //删除
		 if(confirm("您的操作可能会造成数据丢失，您确定要删除该记录吗？","提示")){
		 	window.location = "${pageContext.request.contextPath}/formDefine.do?method=removeFormType&formTypeId=" + chooseValue + "&parentId=" + parentId;
		 }

	}
}


//双击
function grid_dblclick(obj, tableId) {	
 	var formTypeId=obj.typeId;
 	if(formTypeId==""){
 		return ;
 	}else{
 		window.location ="${pageContext.request.contextPath}/formDefine.do?method=formTypeEdit&formTypeId=" + typeId + "&parentId=" + parentId;
 	}
}
</script>
</html>