<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>系统菜单列表</title>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/searchSession.js" charset=gbk></script>

<script type="text/javascript">

Ext.onReady(function(){
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
            	print_sysMenuManger();
            }
        },'-',{
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
	var searchDiv = document.getElementById("search") ;
	searchDiv.style.display = "" ;
	if(!queryWin) { 
	    queryWin = new Ext.Window({
			title: '菜单查询',
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
<input type="hidden" name=act value="666666" id="act"/>
<div id="divprint">
<form name="thisForm" method="post" action="">
<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>

<div id="search" style="display:none;">
<br>
<br>
		<table border="0" cellpadding="0" cellspacing="0" bgcolor="" align="center">
			<tr>
               <td align="right">菜单ID：</td>
               <td align=left>
                 <input type="text" name="menu_id" 
                 	 value="<%=session.getAttribute("menu_id")!=null?session.getAttribute("menu_id").toString():""%>">
               </td>
            </tr>
            <tr>
               <td align="right">名称：</td>
               <td align=left>
                 <input type="text" name="name" 
                  value="<%=session.getAttribute("name")!=null?session.getAttribute("name").toString():""%>">
               </td>
			</tr>
		</table>
</div>	
</form>
</div>
<div style="height:expression(document.body.clientHeight-27);">
<mt:DataGridPrintByBean name="sysMenuManger"/>
</div>
<Script>


function goInitPage()
{

	window.location="sysMenuManger.do";
}
function goSearch1()
{
	//alert("aa");菜单ID,父菜单ID,深度,类型,名称,行为,目标
	window.open('sysMenuManger/search5Grid.jsp','_blank','height=480,width=640, resizable=yes, toolbar=no,menubar=no,titlebar=no');
}
function goAdd()
{
	act.value="add";
	window.location="sysMenuManger.do?method=del&&act="+act.value;
}

function goDelete(){
	if(document.getElementById("chooseValue_sysMenuManger").value==""){
		alert("请选择一项！");
	}
	else{
          if(isExist(document.getElementById("chooseValue_sysMenuManger").value)){
            alert("删除此项将导致资料丢失，是否继续?");
          }
          else{
			if(confirm("删除此项将导致资料丢失，是否继续","qwe")){
			act.value="del";
				//window.changeGrid_CH$.src="sysMenuManger.do?method=del&&chooseValue="+document.getElementById("chooseValue_sysMenuManger").value+"&&act="+act.value;
				window.location="sysMenuManger.do?method=del&&chooseValue="+document.getElementById("chooseValue_sysMenuManger").value+"&&act="+act.value;
		}
          }
	}
}
function isExist(menuid){

  var oBao = new ActiveXObject("Microsoft.XMLHTTP");
  oBao.open("POST","${pageContext.request.contextPath}/sysMenuManger.do?method=isExist&&menuid=" + menuid + "&random="+Math.random(),false);
  oBao.send();
  var strResult = unescape(oBao.responseText)+"";
  if(strResult.indexOf("exist")>=0){
    return true;
  }else{
    return false;
  }
}
function goEdit()
{
	if(document.getElementById("chooseValue_sysMenuManger").value=="")
	{
		alert("请选择一项！");
	}
	else
	{
	act.value="update";
		window.location="sysMenuManger.do?method=del&&chooseValue="+document.getElementById("chooseValue_sysMenuManger").value+"&&act="+act.value;
	}
}
function subSearch()
{
	var flag = false;
	var strW = "";
	if(document.thisForm.menu_id.value != "")
	{
		
		flag = true;
	}
	if(document.thisForm.name.value != "")
	{
		
		flag = true;
	}
	if(flag)
	{
		document.thisForm.action="sysMenuManger.do"
		document.thisForm.submit();
		
	}
	else
	{
		alert("至少填写一个查询条件！");
	}
}
function subClearSearch()
{
	window.location="sysMenuManger.do";
}


</script>

