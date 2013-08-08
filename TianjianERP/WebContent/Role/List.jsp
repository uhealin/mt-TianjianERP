<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>角色管理</title>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<script type="text/javascript">

Ext.onReady(function(){
	var tbar_customer = new Ext.Toolbar({
		renderTo:'gridDiv_role',
           items:[{
            text:'新增',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/add.gif',
            handler:function(){
            	window.location="${pageContext.request.contextPath}/role.do?method=del";
			}
      	},'-',{
           text:'修改',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/edit.gif',
          	handler:function(){
          	
	          	if(document.getElementById("chooseValue_role").value==""){
					alert("请选择要修改名称的角色！");
					return;
				}
          	
				window.location="${pageContext.request.contextPath}/role.do?method=del&&act=update&chooseValue="+document.getElementById("chooseValue_role").value;
			}
        },'-',{
            text:'删除',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/delete.gif',
            handler:function () {
            	if(document.getElementById("chooseValue_role").value==""){
					alert("请选择要删除的角色！");
					return;
				}
				if(confirm("确定删除此角色？")){
					window.location="${pageContext.request.contextPath}/role.do?method=del&act=del&&chooseValue="+document.getElementById("chooseValue_role").value;
				}
            }
        },'-',{
            text:'打 印',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/print.gif',
            handler:function () {
            	print_role();
            }
        },'-',{
            text:'角色权限',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/user.gif',
           	handler:function () {
           		
           		if(document.getElementById("chooseValue_role").value==""){
					alert("请选择要修改权限的角色！");
					return;
				}
           		window.location="${pageContext.request.contextPath}/role.do?method=UpdatePopedom&chooseValue="+document.getElementById("chooseValue_role").value;
           	}
        },'-',{
            text:'人员角色关系',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/user.gif',
           	handler:function () {
           		window.location="${pageContext.request.contextPath}/role.do?method=UserRole&chooseValue="+document.getElementById("chooseValue_role").value;
           	}
        },'-',{
            text:'查询',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/query.gif',
           	handler:function () {
           		queryWinFun();
           	}
        },'-',{
	            text:'关闭',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function () {	
	            	closeTab(parent.tab);
	            	//parent.tab.remove(parent.tab.getActiveTab()); 
	            }
	        },'->'
        //${menuLocation}
        ]
        });  

});

var queryWin = null;
function queryWinFun(id){
	var searchDiv = document.getElementById("search") ;
	searchDiv.style.display = "" ; 	
	if(!queryWin) { 
	    queryWin = new Ext.Window({
			title: '查询',
			contentEl:'search',
		    renderTo :'searchWin',
	     	width: 350,
	     	height:200,
        	closeAction:'hide',
       	    listeners : {
	         	'hide':{
	         		fn: function () {
	         			new BlockDiv().hidden();
	         			searchDiv.style.display = "none" ;
						queryWin.hide();
					}
				}
	        },
        	layout:'fit',
	    	//html:searchDiv.innerHTML,
	    	buttons:[{
            	text:'确定',
          		handler:function(){
          			goSearch_role();
    	           	queryWin.hide();
               		
            	}
          		
        	},{
            	text:'取消',
            	handler:function(){
               		queryWin.hide();
            	}
        	}]
	    });
	    
		
    }
    new BlockDiv().show();
    queryWin.show();
}



</script>
</head>
<body leftmargin="0" topmargin="0">
<form name="thisForm" method="post" action="" class="autoHeightDiv">
<mt:DataGridPrintByBean name="role"  />	

<div id="searchWin"  style="position:absolute;left:expression((document.body.clientWidth)/2);top:expression((document.body.clientHeight)/2); z-index: 2"></div>

<div id="search" style="display:none">
<br/>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>

<table border="0" cellpadding="0" cellspacing="0" width="100%"  bgcolor="" >
	<tr align="center">
		
		<td align="right">所属部门：</td>
		<td align=left>
			<input name="departmentid" type="text" id="departmentid" size="20"   title="请输入，不得为空" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" onClick="onPopDivClick(this);" valuemustexist=true autoid=123 >
		</td>
	</tr>
	<tr>
		<td align="right">角色名称：</td>
		<td align=left>
			<input name="rolename" type="text" id="rolename" size="20"   title="请输入，不得为空"  >
		</td>
	</tr>
	<tr>
		<td align="right">人员名称：</td>
		<td align="left">
			<input type="text" id="name" class="required"
			onfocus="onPopDivClick(this);"
			onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();"
			onclick="onPopDivClick(this);" norestorehint=true 
			  autoid=867 multilevel=true valuemustexist=true
				maxlength="10" size="20" name="name" />
		</td>
	</tr>
</table>
</div>

</form>
</body>
</html>