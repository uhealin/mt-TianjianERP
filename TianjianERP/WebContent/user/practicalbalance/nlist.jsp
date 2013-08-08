<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>收款与发票登记</title>

<script type="text/javascript">  

function ext_init(){
	var tbar_practicalbalance = new Ext.Toolbar({
		renderTo: 'gridDiv_nlistbalance',
		items:[{
			text:'新增', 
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/add.gif',
			handler:function () {
				goAdd();
			}
		},'-',{
            text:'修改',
            cls:'x-btn-text-icon',
            icon:'/AuditSystem/img/edit.gif',
           	handler:function(){
				goEdit();
			}
        },'-',{
            text:'删除',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/delete.gif',
            handler:function () {
            	goDelete();
            }
        },'-',{
			text:'查询', 
			id:'btn-query',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:function () {
            	queryWinFun();
            }
		},'-',{
			text:'打印',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/print.gif',
			handler:function () {
				print_nlistbalance();
			}
		},'-',{
			text:'关闭',
			cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/close.gif',
			handler:function () {
				closeTab(parent.tab);
				//parent.tab.remove(parent.tab.getActiveTab()); 
			}
		}]
	});
	
	
} 

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
	     	height:150,
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
          			goSearch_nlistbalance();
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

window.attachEvent('onload',ext_init);

</script>



</head>

<body >

<form name="thisForm" method="post" action="">

<div style="height:expression(document.body.clientHeight-23);" >
<mt:DataGridPrintByBean name="nlistbalance" />
</div>

<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth)/2);top:expression((document.body.clientHeight)/2); z-index: 2"></div>

<div id="search" style="display:none">
<br/>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
<input name="loginid" type="hidden" id="loginid" value="${loginid }">
<table border="0" cellpadding="0" cellspacing="0" width="100%"  bgcolor="" >
	<tr align="center">
		<td align="right">项目名称：</td>
		<td align=left>
			<input type="text" id="projectid" name="projectid"  refer="loginid" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"  autoid=139  />
		</td>
	</tr>
</table>
</div>
</form>
</body>
</html>
<Script>

function goAdd(){
	window.location="${pageContext.request.contextPath}/practicalbalance.do?method=nedit";
}

function goDelete(){	
	if(document.getElementById("chooseValue_nlistbalance").value==""){
		alert("请选择要删除的收款登记记录！");
	}else {
		//alert(document.thisForm.chooseValue.value);
		if(confirm("确定删除此收款登记记录？")){
			var autoid = document.getElementById("chooseValue_nlistbalance").value;
			window.location="${pageContext.request.contextPath}/practicalbalance.do?method=del&opt=1&autoid="+autoid;
		}
	}
}

function goEdit(){
	var autoid = document.getElementById("chooseValue_nlistbalance").value;
	if(autoid ==""){
		alert("请选择要修改的收款登记记录！");
	}else{
		window.location="${pageContext.request.contextPath}/practicalbalance.do?method=nedit&autoid="+autoid;
	}
}
</Script>