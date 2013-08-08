<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/Validate_include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
 
<%@ taglib prefix="mt" uri="http://www.matech.cn/tag" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>投资情况信息</title>
 
 
<script type="text/javascript">

function ext_init(){ 
	var tbar_policy = new Ext.Toolbar({
			renderTo: 'divBtn',
			items:[{
				text:'查询',
				cls:'x-btn-text-icon',
				icon:'${pageContext.request.contextPath}/img/query.gif',
				handler:queryWinFun 
			},'-',{
	            text:'打印',
	            id:'btn-print',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/print.gif',
	            handler:function(){
	            	print_forbitInvestList();
	            }
	        },'->'
	        ]
	        });  
}
window.attachEvent('onload',ext_init);

</script>
 
</head>
<body>
<div id="divBtn"></div>

<form id="thisForm" name="thisForm" method="post" action="">
<div style="height:expression(document.body.clientHeight-28);" >
<mt:DataGridPrintByBean name="forbitInvestList"  />
</div>

<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>
  
<div id="search" style="display:none">
<fieldset>
    <legend style="font-size:12px;">投资名单</legend>
	<table border="0" cellpadding="10" cellspacing="5" width="100%" bgcolor="">
		<tr align="center">
			<td align="right" >客户名称：</td>
			<td align="left" colspan="3">
				<input type="text"
				   name="customerName"
				   id="customerName"
				   size="30" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" onfocus="onPopDivClick(this);" autoid=35  /> 
			</td>
		</tr>
		<tr align="center">
			<td align="right" >客户股票编号：</td>
			<td align="left" colspan="3">
				<input name="sockCode" id="sockCode" type="text" size="30"  />
			</td>
		</tr>
		<tr align="center">
			<td align="right" >客户所属机构：</td>
			<td align="left" colspan="3">
				<input name="organname" id="organname" type="text" size="30"  />
			</td>
		</tr>
	</table>
</fieldset>
</div>

<input type="hidden" id="autoId" name="autoId">
</form>
</body>

<Script type="text/javascript"> 


// 条件查询
var queryWin = null;
function queryWinFun(id){
	var searchDiv = document.getElementById("search") ;
	searchDiv.style.display = "" ;
	if(!queryWin) { 
	    queryWin = new Ext.Window({
			title: '菜单查询',
			contentEl:'search',
	     	renderTo : searchWin,
	     	width: 500,
	     	height:230,
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
            	text:'搜索',
          		handler:function(){
          			goSearch();
            	}
        	},{
            	text:'清空',
          		handler:function(){  
          			document.getElementById("customerName").value = "";
          			document.getElementById("sockCode").value = "";
          			document.getElementById("organname").value = "";
          			queryWin.hide();
          			goSearch_forbitInvestList(); 
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

// 查询
function goSearch(){
	queryWin.hide();
    goSearch_forbitInvestList(); 
} 

</Script>


</html>