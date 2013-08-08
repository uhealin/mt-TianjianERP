<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>新闻列表</title>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/searchSession.js" charset=gbk></script>

<script type="text/javascript">

Ext.onReady(function(){
	var tbar_customer = new Ext.Toolbar({
		renderTo:'divBtn',
           items:[
			/*
                  {
            text:'新增',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/add.gif',
            handler:function(){
            	goAdd();
			}
      	},'-',{
            text:'修改',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/edit.gif',
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
        }
         
         ,'-',{
            text:'打 印',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/print.gif',
            handler:function () {
            	print_newsList();
            }
        },'-',
        */
        {
			text:'查询',
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

});


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

</script>

</head>
<body>
<div id="divBtn"></div>
<div id="divprint">
<form name="thisForm" method="post" action="">
<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>
<input type="hidden" name="opt" id="opt" value="<%=request.getParameter("opt")%>">
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
               <td align="right">类别：</td>
               <td align=left>
                <input type="text" name="type"   id="type" />
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
<Script>


function goInitPage()
{
	window.location="news.do";
}

// 添加 
function goAdd()
{
	window.location="news.do?method=goAdd&opt=<%=request.getParameter("opt")%>";
}

//删除 
function goDelete(){
	
	var ids = getChooseValue("newsList");
	if(ids == "" ) {
		alert("请选择一项！");
	}else {
		 
		if(confirm("删除此项将导致资料丢失，是否继续")){
			window.location="news.do?method=deleteNews&&autoId="+ids+"&opt=<%=request.getParameter("opt")%>";
		}	
	}
	
	 
}


// 修改 
function goEdit()
{
	var ids = getChooseValue("newsList");
	
	
	if(ids == "" ) {
		alert("请选择一项！");
	}else {
		if(ids.indexOf(",")>0){
			alert("请选择一项！");
			return ;
		}
		window.location="news.do?method=goUpdate&&autoId="+ids+"&opt=<%=request.getParameter("opt")%>";
	}
}

// 搜索 
function subSearch()
{
	 goSearch_newsList();
	 queryWin.hide();
}


// 清空 
function subClearSearch()
{
	document.thisForm.title.value="";
	document.thisForm.publishUserId.value="";
	document.thisForm.updateTime.value="";
	window.location="news.do";
}



//表单初始化
function init() {
	//document.getElementById('updateTime').setAttribute('readonly','readonly');
	//document.getElementById("updateTime").readOnly=true;
	//alert(document.getElementById("updateTime").readOnly);
	
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
</body>
</html>


