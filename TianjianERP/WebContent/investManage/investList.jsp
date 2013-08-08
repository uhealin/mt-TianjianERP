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
	var tbar = new Ext.Toolbar({
		renderTo: 'divBtn',
		items:[{
			text:'新增',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/add.gif',
			handler:function () {
				f_add();
			}
		},'-',{
           text:'修改',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/edit.gif',
           handler:function(){
				f_update();
		   }
        },'-',{
			text:'删除',
			id:'del',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/delete.gif',
			handler:function(){
				f_delete();
			}
		},'-',{
            text:'打印',
            id:'btn-print',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/print.gif',
            handler:function(){
            	print_investList();
            }
        },'-',{
           text:'查询',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/query.gif',
           handler:queryWinFun 
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
<mt:DataGridPrintByBean name="investList"  />
</div>

<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>

 
<div id="search" style="display:none">
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
			<td align="right" >股票类型：</td>
			<td align="left" colspan="3">
				<input name="investManageType" 
					   id="investManageType" 
					   type="text" 
					   size="30"
					   noinput="true" 
					   onkeydown="onKeyDownEvent();" 
					   onkeyup="onKeyUpEvent();" 
					   onclick="onPopDivClick(this);" 
					   onfocus="onPopDivClick(this);"
					   onpropertychange="f_change(this);" 
					   autoid=700 
					   refer="investManageTypes" />
			</td>
		</tr>
		<tr align="center" id="sgA">
			<td align="right" >深A股票号：</td>
			<td align="left" colspan="3">
				<input name="ssstockNum" id="ssstockNum" type="text" size="30"  />
			</td>
		</tr>
		<tr align="center" id="sgB">
			<td align="right" >深B股票号：</td>
			<td align="left" colspan="3">
				<input name="ssstockNum2" id="ssstockNum2" type="text" size="30"  />
			</td>
		</tr>
		<tr align="center" id="hgA">
			<td align="right" >沪A股票号：</td>
			<td align="left" colspan="3">
				<input name="hsstockNum" id="hsstockNum" type="text" size="30"  />
			</td>
		</tr>
		<tr align="center" id="hgB">
			<td align="right" >沪B股票号：</td>
			<td align="left" colspan="3">
				<input name="hsstockNum2" id="hsstockNum2" type="text" size="30"  />
			</td>
		</tr>
		<tr align="center" id="gg">
			<td align="right" >港市股票号：</td>
			<td align="left" colspan="3">
				<input name="gsstockNum" id="gsstockNum" type="text" size="30"  />
			</td>
		</tr>
	</table>
</fieldset>
</div>

<input type="hidden" id="autoId" name="autoId">
</form>
</body>

<Script type="text/javascript"> 

// 添加
function f_add(){
	document.getElementById("thisForm").action = "${pageContext.request.contextPath}/investManage.do?method=go&paramGo=add";
	document.getElementById("thisForm").submit();
}


// 修改
function f_update(){
	var autoId = document.getElementById("chooseValue_investList").value;
	if(autoId=="" || autoId==null){
		alert("请选择要修改的项！");
	}else{
		var rs = getIsMine(autoId);
		if(rs=="Y"){
			document.getElementById("autoId").value = autoId;
			document.getElementById("thisForm").action = "${pageContext.request.contextPath}/investManage.do?method=go&paramGo=update";
			document.getElementById("thisForm").submit();
		}else{
			alert("请修改您自己发起的项！");
		}
	}	
}

// 删除
function f_delete(){
	var autoId = document.getElementById("chooseValue_investList").value; 
	if(autoId=="" || autoId==null){
		alert("请选择要删除的项！");
	}else{
		if(confirm("您确定要删除吗?")){
			document.getElementById("autoId").value = autoId;
			document.getElementById("thisForm").action = "${pageContext.request.contextPath}/investManage.do?method=del";
			document.getElementById("thisForm").submit();
		}
	}	
}
 
// 穿透
function grid_dblclick(obj,tableId){
	document.getElementById("autoId").value = obj.autoId;
	document.getElementById("thisForm").action = "${pageContext.request.contextPath}/investManage.do?method=go&paramGo=update&p=view";
	document.getElementById("thisForm").submit();	
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
	     	height:390,
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
          			document.getElementById("ssstockNum2").value = "";
          			document.getElementById("hsstockNum").value = "";
          			document.getElementById("hsstockNum2").value = "";
          			document.getElementById("gsstockNum").value = "";
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

// 得到状态
function getIsMine(id){
	var url = "${pageContext.request.contextPath}/investManage.do?method=getIsMine";
	var requestString = "id=" + id;
	var result = ajaxLoadPageSynch(url,requestString);
	return result;
}

// 联动
function f_change(t){
	var v = t.value;
	if(v == "深A"){
		document.getElementById("sgA").style.display = "";
		document.getElementById("sgB").style.display = "none";
		document.getElementById("hgA").style.display = "none";
		document.getElementById("hgB").style.display = "none";
		document.getElementById("gg").style.display = "none";
	}else if(v == "深B"){
		document.getElementById("sgA").style.display = "none";
		document.getElementById("sgB").style.display = "";
		document.getElementById("hgA").style.display = "none";
		document.getElementById("hgB").style.display = "none";
		document.getElementById("gg").style.display = "none";
	}else if(v == "沪A"){
		document.getElementById("sgA").style.display = "none";
		document.getElementById("sgB").style.display = "none";
		document.getElementById("hgA").style.display = "";
		document.getElementById("hgB").style.display = "none";
		document.getElementById("gg").style.display = "none";
	}else if(v == "沪B"){
		document.getElementById("sgA").style.display = "none";
		document.getElementById("sgB").style.display = "none";
		document.getElementById("hgA").style.display = "none";
		document.getElementById("hgB").style.display = "";
		document.getElementById("gg").style.display = "none";
	}else if(v == "港股"){
		document.getElementById("sgA").style.display = "none";
		document.getElementById("sgB").style.display = "none";
		document.getElementById("hgA").style.display = "none";
		document.getElementById("hgB").style.display = "none";
		document.getElementById("gg").style.display = "";
	}else{
		document.getElementById("sgA").style.display = "";
		document.getElementById("sgB").style.display = "";
		document.getElementById("hgA").style.display = "";
		document.getElementById("hgB").style.display = "";
		document.getElementById("gg").style.display = "";
	}
}

</Script>
</html>