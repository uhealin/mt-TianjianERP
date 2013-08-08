<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>客户接受审批</title>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/searchSession.js" charset=gbk></script>

<script type="text/javascript">

Ext.onReady(function(){
	
	var tbar_customer = new Ext.Toolbar({
		renderTo:'divBtn',
           items:[{
			text:'查询',
			id:'btn-query',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:queryWinFun
		},'-',{
            text:'打印',
            id:'btn-print',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/print.gif',
            handler:function(){
            	print_headApprovalList();
            }
        },'-',{
	            text:'关闭',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function () {
            		closeTab(parent.parent.tab);
	            }
	        },'->'
        ]
        });  

});


var queryWin = null;
function queryWinFun(id){
	// 设置时间 只读 
	var searchDiv = document.getElementById("search") ;
	searchDiv.style.display = "" ;
	if(!queryWin) { 
	    queryWin = new Ext.Window({
			title: '客户承接审批查询',
			contentEl:'search',
	     	renderTo : searchWin,
	     	width: 300,
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
          			goSearch_headApprovalList();
          			queryWin.hide();
            	}
        	},{
            	text:'清空',
          		handler:function(){
          			subClearSearch();
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
<body>
<div id="divBtn"></div>
<div id="divprint">
<form name="thisForm" method="post" action="">
<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>

<div id="search" style="display:none;">
<br>
<br>
		<table border="0" cellpadding="0" cellspacing="0" bgcolor="" align="center">
			<tr>
               <td align="right">客户名称：</td>
               <td align=left>
                 <input type="text" id="customerName" name="customerName" >
               </td>
            </tr>
            <tr>
               <td align="right">客户承接人：</td>
               <td align=left>
                 <input type="text" id="publishUserId" name="publishUserId" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" onClick="onPopDivClick(this);" valuemustexist=true autoid=252>
               </td>
			</tr>
			  <tr>
               <td align="right">所属行业：</td>
               <td align=left>
                 <input type="text" id="belongsIndustry" name="belongsIndustry" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" onClick="onPopDivClick(this);"  autoid=854>
               </td>
			</tr>
			 <tr>
               <td align="right">委托方：</td>
               <td align=left>
                 <input type="text" id="client" name="client" >
               </td>
			</tr>
			<tr>
               <td align="right">承接截止日期：</td>
               <td align=left>
                <input type="text"
					   name="deadlineDate"
					   id="deadlineDate" />
               </td>
			</tr>
		</table>
</div>	
</form>
</div>

<div style="height:410;overflow:auto;">
<mt:DataGridPrintByBean name="headApprovalList"/>
</div>
<Script>

new Ext.form.DateField({			
	applyTo : 'deadlineDate',
	width: 133,
	format: 'Y-m-d'	
});



//清空 
function subClearSearch()
{
	document.thisForm.deadlineDate.value="";
	document.thisForm.publishUserId.value="";
	document.thisForm.belongsIndustry.value="";
	document.thisForm.client.value="";
	document.thisForm.customerName.value="";
}

//查看
function goLook()
{
	if(document.getElementById("chooseValue_headApprovalList").value=="")
	{
		alert("请选择一项！");
		return ;
	}
	else
	{
		window.location="newCustomer.do?method=look&uuid="+document.getElementById("chooseValue_headApprovalList").value;
	}
}
</script>
</body>
</html>


