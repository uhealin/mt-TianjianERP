<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript">
//EXT初始化
function ext_init(){
	var tbar_customer = new Ext.Toolbar({
		renderTo:'tbar',
           items:[{
            text:'新增',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/add.gif',
            handler:function(){
            	window.location="${pageContext.request.contextPath}/kdic.do?method=add";
			}
      	},'-',{
           text:'修改',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/edit.gif',
          	handler:function(){
	          	if(document.getElementById("chooseValue_dicList").value==""){
					alert("请选择要修改名称的对象！");
					return;
				}
          	
				window.location="${pageContext.request.contextPath}/kdic.do?method=edit&&autoId="+document.getElementById("chooseValue_dicList").value;
			}
        },'-',{
            text:'删除',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/delete.gif',
            handler:function () {
            	if(document.getElementById("chooseValue_dicList").value==""){
					alert("请选择要删除的对象！");
					return;
				}
				if(confirm("确定删除此对象？")){
					window.location="${pageContext.request.contextPath}/kdic.do?method=delete&&autoId="+document.getElementById("chooseValue_dicList").value;
				}
            }
        },'-',{
            text:'打 印',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/print.gif',
            handler:function () {
            	print_dicList();
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
	        }
        ]
        });  
	
var queryWin = null;
function queryWinFun(){
	var searchDiv = document.getElementById("search") ;
	searchDiv.style.display = "" ; 	
	if(!queryWin) { 
	    queryWin = new Ext.Window({
			title: '查询',
			contentEl:'search',
		    //renderTo :'searchWin',
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
	    	buttons:[{
            	text:'确定',
          		handler:function(){
    	           	queryWin.hide();
    	            goSearch_treeList();
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
}
window.attachEvent('onload',ext_init);



</script>
</head>

<body>
	<div id="tbar"></div>
	
	 <div class="autoHeightDiv"> 
		<mt:DataGridPrintByBean name="treeList" outputData="true" outputType="invokeSearch" /> 
	</div>
	
	<form action="" id="thisForm" name="thisForm" method="post">

	<div id="search" style="display:none">
	<br/>
	<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
	<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
			<tr align="center">
				<td align="right">名字：</td>
				<td align=left>
					<input type="text" name="dicName" id="dicName" >
				</td>
			</tr>
			
			<tr align="center">
				<td align="right">值：</td>
				<td align=left>
					<input type="text" name="dicValue" id="dicValue">
				</td>
			</tr>
			 
			<tr align="center">
				<td align="right">dic类型：</td>
				<td align=left>
					<input name="dicType" type="text" id="dicType" size="20" maxlength="40" onkeydown="onKeyDownEvent();"
					onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"
					valuemustexist="true" autoid="2022" >
				</td>
			</tr>
	</table>
	</div>
	</form>				
	
</body>

<script type="text/javascript">
 	
 	function goAdd(){
 		
 	}
</script>
</html>