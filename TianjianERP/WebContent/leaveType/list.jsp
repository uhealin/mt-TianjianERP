<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>请假类型设置</title>
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
			title: '请假类型设置查询',
			contentEl:'search',
	     	renderTo : searchWin,
	     	width: 300,
	     	height:160,
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
          			goSearch_leavTypeList();
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

<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-210)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>

<div id="search" style="display:none;">
<br>
<br>
		<table border="0" cellpadding="0" cellspacing="0" bgcolor="" align="center">
		<tr>
			<td class="data_tb_alignright" align="right" >名称：</td>
			<td class="data_tb_content"  nowrap="nowrap"><input type="text" id="name" 
				maxlength="30"  name="name"   />
				</td>
	    </tr>
	    <tr>
			<td class="data_tb_alignright" align="right">提前申请限制数：</td>
			<td class="data_tb_content"><input type="text" id="applyLimit"
				maxlength="50" name="applyLimit" /></td>
		</tr>
		</table>
</div>	
</form>
</div>
<div style="height:expression(document.body.clientHeight-34);">
<mt:DataGridPrintByBean name="leavTypeList"/>
</div>
<Script>

//清空 
function subClearSearch()
{
	document.thisForm.name.value="";
	document.thisForm.applyLimit.value="";
}

// 添加 
function goAdd()
{
	window.location="${pageContext.request.contextPath}/leaveType.do?method=addSkip";
}
 

// 删除 
function goDelete(){
	if(document.getElementById("chooseValue_leavTypeList").value==""){
		alert("请选择一项！");
		return ;
	}
	else{
		if(confirm("您确定要删除此资料吗？删除后将无法恢复！")){
			window.location="${pageContext.request.contextPath}/leaveType.do?method=del&autoId="+document.getElementById("chooseValue_leavTypeList").value;
		}	
	}
}


// 修改 
function goEdit()
{
	if(document.getElementById("chooseValue_leavTypeList").value=="")
	{
		alert("请选择一项！");
		return ;
	}
	else
	{
		window.location="${pageContext.request.contextPath}/leaveType.do?method=updateSkip&autoId="+document.getElementById("chooseValue_leavTypeList").value;
	}
}

//双击
function grid_dblclick(obj) {
	window.location="leaveType.do?method=viewNews&&autoId="+obj.autoId;
}

 
</script>
</body>
</html>


