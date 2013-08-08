<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/Validate_include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
 
<%@ taglib prefix="mt" uri="http://www.matech.cn/tag" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>家庭成员记录</title>


<script type="text/javascript">


function ext_init(){ 
	var tbar = new Ext.Toolbar({
		renderTo: 'divBtn',
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
           icon:'${pageContext.request.contextPath}/img/edit.gif',
           handler:function(){
				goUpdate();
		   }
        },'-',{
			text:'删除',
			id:'del',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/delete.gif',
			handler:function(){
				goDelete();
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
<mt:DataGridPrintByBean name="Familylist" outputData="true"
	outputType="invokeSearch" />
</div>

<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>
<div id="search" style="display:none">
<!-- 
<fieldset>
    <legend style="font-size:12px;">投资名单</legend>
	<table border="0" cellpadding="10" cellspacing="5" width="100%" bgcolor="">
		<tr align="center">
			<td align="right" >投资人名称：</td>
			<td align="left" colspan="3">
				<input type="text"
				   name="userName"
				   id="userName"
				   size="30"  /> 
			</td>
		</tr>
		<tr align="center">
			<td align="right" >投资人与本人关系：</td>
			<td align="left" colspan="3">
				<input name="relations" id="relations" type="text" size="30" 
				onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" onfocus="onPopDivClick(this);" autoid=700 refer="investRelations" />
			</td>
		</tr>
		<tr align="center">
			<td align="right" >深市股票号：</td>
			<td align="left" colspan="3">
				<input name="ssstockNum" id="ssstockNum" type="text" size="30"  />
			</td>
		</tr>
		<tr align="center">
			<td align="right" >沪市股票号：</td>
			<td align="left" colspan="3">
				<input name="hsstockNum" id="hsstockNum" type="text" size="30"  />
			</td>
		</tr>
	</table>
</fieldset>
 -->
</div>

<input type="hidden" id="autoId" name="autoId">
</form>
</body>
<script>
function goAdd(){
	
    window.location="${pageContext.request.contextPath}/family.do?method=exitFamily" ;
	       
}  
function goUpdate(){
	var choose_family = document.getElementById("chooseValue_Familylist").value;
	
	if(choose_family == ""){
		alert("请选择要修改的记录！");
	} else {		
		window.location="${pageContext.request.contextPath}/family.do?method=exitFamily&autoid=" + choose_family;		
	}
}

//删除劳动合同
function goDelete(){
	var choose_family = document.getElementById("chooseValue_Familylist").value;

	if(choose_family == ""){
		alert("请选择要删除的记录！");
	} else {
		if(confirm("您的操作可能会造成数据丢失，您确定要删除该记录吗？","提示")){
			window.location="${pageContext.request.contextPath}/family.do?method=removeFamily&autoid=" + choose_family;
		}
	}
} 



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
	     	height:270,
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
          			document.getElementById("userName").value = "";
          			document.getElementById("relations").value = "";
          			document.getElementById("ssstockNum").value = "";
          			document.getElementById("hsstockNum").value = "";
          			queryWin.hide();
          			goSearch_investList(); 
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
    goSearch_investList(); 
} 


</script>

</html>
