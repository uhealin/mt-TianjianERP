<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>规章制度列表</title>
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
			text:'查询',
			id:'btn-query',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:queryWinFun
		},'-',{
			text:'职级维护',
			id:'btn_zjwh',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/department.gif',
			handler:function () {
				window.location="${pageContext.request.contextPath}/rank.do?method=list";
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
			title: '工资设定查询',
			contentEl:'search',
	     	renderTo : searchWin,
	     	width: 300,
	     	height:240,
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
          			goSearch_rankWagesList();
          			queryWin.hide();
            	}
        	},{
            	text:'清空',
          		handler:function(){
          			emprty();
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
<body style="overflow: hidden;">
<div id="divBtn"></div>
<div id="divprint">
<form name="thisForm" method="post" action="rankWages.do">
<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>

<div id="search" style="display:none;">
<br>
<br>
		<table border="0" cellpadding="0" cellspacing="0" bgcolor="" align="center" style="width:100%">
		   <tr>
				  <td  align="right" width="30%">工资项名称：</td>
			      <td  align="left">
			      	<input name="wagesName" id="wagesName"  class='required' />
				  </td>
			</tr>
			<tr >
				  <td  align="right" width="10%" >职级分类：</td>
			      <td    align="left">
					<input type="text" name="rankId" id="rankId" 
					onkeydown="onKeyDownEvent();"
					onkeyup="onKeyUpEvent();"
					onclick="onPopDivClick(this);"
					noinput=true
					autoid=863
					>
						 
				  </td>
			</tr>
			<tr >
				  <td  align="right">金额：</td>
			      <td  align="left">
			      	<input name="getValue" id="getValue"   />
				  </td>
			</tr>
			<tr>
				<td  align="right" >备注：</td>
				<td  align="left">
					<textarea style="width: 140;height: 30px;overflow: visible;" name="remark" id="remark"></textarea>
			    </td>
			</tr>
		</table>
</div>	
</form>
</div>
<div style="height:expression(document.body.clientHeight-27);">
<mt:DataGridPrintByBean name="rankWagesList"/>
</div>
<Script>


function goInitPage()
{
	window.location="rankWages.do";
}

// 添加 
function goAdd()
{
	window.location="${pageContext.request.contextPath}/rankWages.do?method=addSkip&rankId=${rankId}";
}


// 删除 
function goDelete(){
	if(document.getElementById("chooseValue_rankWagesList").value==""){
		alert("请选择一项！");
	}
	else{
		if(confirm("删除此项将导致资料丢失，是否继续")){
			window.location="${pageContext.request.contextPath}/rankWages.do?method=del&rankId=${rankId}&&uuid="+document.getElementById("chooseValue_rankWagesList").value;
		}	
	}
}


// 修改 
function goEdit()
{
	if(document.getElementById("chooseValue_rankWagesList").value=="")
	{
		alert("请选择一项！");
	}
	else
	{
		window.location="${pageContext.request.contextPath}/rankWages.do?method=updateSkip&rankId=${rankId}&uuid="+document.getElementById("chooseValue_rankWagesList").value;
	}
}

function emprty()
{
	document.getElementById("wagesName").value="";
	document.getElementById("rankId").value="";
	document.getElementById("getValue").value="";
	document.getElementById("remark").value="";
}
 
//双击
function grid_dblclick(obj) {
	if(obj.uuid !=""){
		
		window.location="${pageContext.request.contextPath}/rankWages.do?method=viewRegulations&autoId="+obj.uuid;
	}
}

</script>
</body>
</html>
