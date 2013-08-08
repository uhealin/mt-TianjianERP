<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>规章制度列表</title>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/searchSession.js" charset=gbk></script>

<script type="text/javascript">
var tabs="";
Ext.onReady(function(){
	
	tabs = new Ext.TabPanel({
	    renderTo: 'my-tabs',
	    activeTab: 0,
	    layoutOnTabChange:true, 
	    forceLayout : true,
	    deferredRender:false,
	    height: document.body.clientHeight-Ext.get('my-tabs').getTop()+14,
	    width : document.body.clientWidth, 
	    defaults: {autoWidth:true,autoHeight:true},
	    items:[
	        {contentEl:'tab1', title:'我发起的', id:'cur1'},
	        {contentEl:'tab2', title:'我查阅的', id:'cur2'} 
	    ]
	});
	
	tabs.on("tabchange",function(tabpanel,tab) {
    	if(tab.id == "cur2") {
				goSearch_regulationsLookList();			
		} 
    }) ;

	goSearch_regulationsList();
	
	var tbar_customer = new Ext.Toolbar({
		renderTo:'divBtn',
           items:[{
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
        },'-',{
            text:'打 印',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/print.gif',
            handler:function () {
            	print_regulationsList();
            }
        },'-',{
			text:'查询',
			id:'btn-query',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:queryWinFun
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
			title: '规章制度查询',
			contentEl:'search',
	     	//renderTo : searchWin,
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
            		document.getElementById("title").value="";
            		document.getElementById("publishUserId").value="";
            		document.getElementById("updateTime").value="";
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
<body style="overflow: hidden;">
<form name="thisForm" id="thisForm" method="post" action="regulations.do">
	<div id="my-tabs">
		<div id="tab1">
			<div id="divBtn"></div>
			<div id="search" style="display:none;">
				<table border="0" cellpadding="0" cellspacing="0" bgcolor="" align="center">
					<tr>
						<td colspan="2" style="height: 20px;"></td>
					</tr>
					<tr>
		               <td align="right">标题：</td>
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
			<div style="height:expression(document.body.clientHeight-56);">
				<mt:DataGridPrintByBean name="regulationsList"/>
			</div>
		</div>
		<div id="tab2">
			<div style="height:expression(document.body.clientHeight-27);">
			<mt:DataGridPrintByBean name="regulationsLookList"/>
			</div>
		</div>
	</div>
	</form>
<Script>


function goInitPage()
{
	window.location="regulations.do";
}

// 添加 
function goAdd()
{
	window.location="regulations.do?method=goAdd";
}


// 删除 
function goDelete(){
	if(document.getElementById("chooseValue_regulationsList").value==""){
		alert("请选择一项！");
	}
	else{
		if(confirm("删除此项将导致资料丢失，是否继续")){
			window.location="regulations.do?method=deleteRegulations&&autoId="+document.getElementById("chooseValue_regulationsList").value;
		}	
	}
}


// 修改 
function goEdit()
{
	if(document.getElementById("chooseValue_regulationsList").value=="")
	{
		alert("请选择一项！");
	}
	else
	{
		window.location="regulations.do?method=goUpdate&&autoId="+document.getElementById("chooseValue_regulationsList").value;
	}
}

// 搜索 
function subSearch()
{
	var flag = false;
	if(document.thisForm.title.value != "")
	{
		flag = true;
	}
	var publishUserId = document.getElementById("publishUserId").value;
	if(publishUserId+"" != "")
	{
		flag = true;
	}
	if(document.getElementById("updateTime").value != "")
	{
		flag = true;
	}
	if(flag)
	{
		document.thisForm.action="regulations.do";
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
	document.getElementById("title").value="";
	document.getElementById("publishUserId").value="";
	document.getElementById("updateTime").value="";
	window.location="regulations.do";
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
	window.location="regulations.do?method=viewRegulations&autoId="+obj.autoId;
}

</script>
</body>
</html>
