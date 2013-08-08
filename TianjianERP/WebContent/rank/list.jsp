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
	            text:'返回工资组成维护',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/back.gif',
	            handler:function () {
            		window.location="${pageContext.request.contextPath}/rankWages.do?method=list";
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
			title: '职级查询',
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
          			goSearch_rankList();
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
<body>
<div id="divBtn"></div>
<div id="divprint">
<form name="thisForm" method="post" action="rank.do">
<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>

<div id="search" style="display:none;">
<br>
<br>
		<table border="0" cellpadding="0" cellspacing="0" bgcolor="" align="center" style="width:100%">
		   <tr>
				  <td  align="right" width="30%">职级名称：</td>
			      <td  align="left">
			      	<input name="name" id="name"  class='required' />
				  </td>
			</tr>
			<tr >
				  <td  align="right" width="10%" >职级分类：</td>
			      <td    align="left">
					<select name="ctype" id="ctype" class='required' style="width: 130">
						<option value="">请选择</option>
				      	<option value="试用职级">试用职级</option>
				      	<option value="正式职级">正式职级</option>
			      	</select>
				  </td>
			</tr>
			<tr >
				  <td  align="right">所属组：</td>
			      <td  align="left">
			      	<input name="group" id="group"  
			      	onkeydown="onKeyDownEvent();"
					onkeyup="onKeyUpEvent();"
					onclick="onPopDivClick(this);"
					noinput=true
					autoid=862
			      	 />
				  </td>
			</tr>
			<tr>
				<td  align="right" >备注：</td>
				<td  align="left">
					<textarea style="width: 140;height: 50px;overflow: visible;" name="explain" id="explain"></textarea>
			    </td>
			</tr>
		</table>
</div>	
</form>
</div>
<div style="height:expression(document.body.clientHeight-27);">
<mt:DataGridPrintByBean name="rankList"/>
</div>
<Script>


function goInitPage()
{
	window.location="rank.do";
}

// 添加 
function goAdd()
{
	window.location="rank.do?method=addSkip";
}


// 删除 
function goDelete(){
	if(document.getElementById("chooseValue_rankList").value==""){
		alert("请选择一项！");
	}
	else{
		if(confirm("删除此项将导致资料丢失，是否继续")){
			window.location="${pageContext.request.contextPath}/rank.do?method=del&autoId="+document.getElementById("chooseValue_rankList").value;
		}	
	}
}


// 修改 
function goEdit()
{
	if(document.getElementById("chooseValue_rankList").value=="")
	{
		alert("请选择一项！");
	}
	else
	{
		window.location="${pageContext.request.contextPath}/rank.do?method=updateSkip&autoId="+document.getElementById("chooseValue_rankList").value;
	}
}

function emprty()
{
	document.getElementById("name").value="";
	document.getElementById("group").value="";
	document.getElementById("ctype").value="";
	document.getElementById("explain").value="";
}
 
//双击
function grid_dblclick(obj) {
	if(obj.uuid !=""){
		
		window.location="${pageContext.request.contextPath}/rank.do?method=viewRegulations&autoId="+obj.uuid;
	}
}

</script>
</body>
</html>
