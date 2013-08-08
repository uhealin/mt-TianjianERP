<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>单位人员体检预约统计表</title>
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
		},'->'
        ]
        });  
	
	
	if ('${informuuid}'==''){
		//还没选择的话，就显示窗口让用户选择
	//	queryWinFun();
	}

});

var queryWin = null;
function queryWinFun(){
	// 设置时间 只读 
	document.getElementById("informuuid").readOnly=true;
	var searchDiv = document.getElementById("search") ;
	searchDiv.style.display = "" ;
	if(!queryWin) { 
	    queryWin = new Ext.Window({
			title: '体检名称选择',
			contentEl:'search',
	     	renderTo : searchWin,
	     	width: 500,
	     	height:200,
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
            	text:'确认',
          		handler:function(){
          			subSearch();
            	}
        	}]
	    });
    }
    new BlockDiv().show();
    queryWin.show();
}

function subSearch()
{
	if(document.thisForm.informuuid.value == "")
	{
		alert("至少填写一个查询条件！");
		return;
	}
	
	document.thisForm.action="statistics.do";
	document.thisForm.submit();
		
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
		<table class="formTable">
			<tr>
               <th colspan="1">请选择查询的体检名称：</td>
               <td align=left>
                 <input type="text" id="informuuid" name="informuuid" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" onClick="onPopDivClick(this);" valuemustexist=true autoid=10023>
               </td>
			</tr>
			<input type="hidden" id="uuid" name="uuid" value="${uuid}"/>
		</table>
</div>	
</form>
</div>
<div style="height:expression(document.body.clientHeight-27);">
<mt:DataGridPrintByBean name="statisticsList"   outputData="${outputData}" />
</div>
<Script>


function goInitPage()
{
	window.location="statistics.do";
}

// 窗体加载 
//Ext.onReady(init);

//双击
function grid_dblclick(obj) {
	//window.location="statistics.do?method=viewNews&&uuid="+obj.uuid;
}

//修改 
function goEdit()
{
	if(document.getElementById("chooseValue_statisticsList").value=="")
	{
		alert("请选择一项！");
	}
	else
	{
		window.location="statistics.do?method=goUpdate&&autoId="+document.getElementById("chooseValue_statisticsList").value;
	}
}
</script>
</body>
</html>