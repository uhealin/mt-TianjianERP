<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>新闻列表</title>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/searchSession.js" charset=gbk></script>

<script type="text/javascript">
function ext_init(){
    var tbar = new Ext.Toolbar({
   		renderTo: "divBtn",
   		defaults: {autoHeight: true,autoWidth:true},
        items:[{text:'查询',
				id:'btn-query',
				cls:'x-btn-text-icon',
		   		icon:'${pageContext.request.contextPath}/img/query.gif',
				handler:queryWinFun
			},'-',{
	        	text:'关闭',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function () {
	        		closeTab(parent.tab);
	            }
	   		},'->'
			]
    });
    
}


var queryWin = null;
function queryWinFun(id){
	// 设置时间 只读 
	document.getElementById("updateTime").readOnly=true;
	var searchDiv = document.getElementById("search") ;
	searchDiv.style.display = "" ;
	if(!queryWin) { 
	    queryWin = new Ext.Window({
			title: '新闻查询',
			contentEl:'search',
	     	renderTo : searchWin,
	     	width: 300,
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
            	text:'搜索',
          		handler:function(){
          			subSearch();
            	}
        	},{
            	text:'清空',
          		handler:function(){
          			subClearSearch();
            	}
        	},{
            	text:'取消',
            	handler:function(){
	        		document.thisForm.title.value="";
	        		document.thisForm.publishUserId.value="";
	        		document.thisForm.updateTime.value="";
               		queryWin.hide();
            	}
        	}]
	    });
    }
    new BlockDiv().show();
    queryWin.show();
}



window.attachEvent('onload',ext_init);



//搜索 
function subSearch()
{
	var flag = false;
	if(document.thisForm.title.value != "")
	{
		flag = true;
	}
	if(document.thisForm.publishUserId.value != "")
	{
		flag = true;
	}
	if(document.thisForm.updateTime.value != "")
	{
		flag = true;
	}
	if(flag)
	{
		document.thisForm.action="news.do?method=more";
		document.thisForm.submit();
		
	}
	else
	{
		alert("至少填写一个查询条件！");
	}
}


// 清空 
function subClearSearch()
{
	document.thisForm.title.value="";
	document.thisForm.publishUserId.value="";
	document.thisForm.updateTime.value="";
	window.location="news.do?method=more";
}



//表单初始化
function init() { 
	
	//初始化日期控件
	new Ext.form.DateField({
		applyTo : 'updateTime',
		width: 137,
		format: 'Y-m-d'
	});
	
}

// 窗体加载 
Ext.onReady(init);



//双击
function grid_dblclick(obj) {
	window.location="news.do?method=viewNews&&autoId="+obj.autoId;
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
               <td align="right">新闻标题：</td>
               <td align=left>
                 <input type="text" id="title" name="title" >
               </td>
            </tr>
            <tr>
               <td align="right">发布人：</td>
               <td align=left>
                 <input type="text" id="publishUserId" name="publishUserId" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" onClick="onPopDivClick(this);" valuemustexist=true autoid=7>
               </td>
			</tr>
			
			<tr>
               <td align="right">发布时间：</td>
               <td align=left>
                <input type="text"
					   name="updateTime"
					   id="updateTime"
					   class="validate-date-cn" />
               </td>
			</tr>
		</table>
</div>	
</form>
</div>
<div style="height:expression(document.body.clientHeight-27);">
<mt:DataGridPrintByBean name="newsList"/>
</div> 


<script>

//搜索 
function subSearch()
{
	var flag = false;
	if(document.thisForm.title.value != "")
	{
		flag = true;
	}
	if(document.thisForm.publishUserId.value != "")
	{
		flag = true;
	}
	if(document.thisForm.updateTime.value != "")
	{
		flag = true;
	}
	if(flag)
	{
		document.thisForm.action="news.do?method=more";
		document.thisForm.submit();
		
	}
	else
	{
		alert("至少填写一个查询条件！");
	}
}


// 清空 
function subClearSearch()
{
	document.thisForm.title.value="";
	document.thisForm.publishUserId.value="";
	document.thisForm.updateTime.value="";
	window.location="news.do?method=more";
}




//双击
function grid_dblclick(obj) {
	window.location="news.do?method=viewNews&&autoId="+obj.autoId;
}
</script>
</body>
</html>
