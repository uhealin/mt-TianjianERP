<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>	
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>办公物品库存查询</title>
<script type="text/javascript">

Ext.onReady(function(){
	 
var tbar_customer=new Ext.Toolbar({
	renderTo:'divBtn',
       items:[{
           text:'申领',
           icon:'${pageContext.request.contextPath}/img/add.gif',
           handler:function(){
        	   goApply();
			}
     	},'-',{
			text:'查看详细',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
	   		 handler:function (){
	   			 goView();
	   		 }
		},'-',{
			text:'查询',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
	   		 handler:queryWinFun
		},'-',{
			text:'刷新',
	   		icon:'${pageContext.request.contextPath}/img/refresh.gif',
	   		 handler:function(){
	   			empty();
	   			goSearch_list();
	   		 }
		},'-',{
            text:'关闭',
            icon:'${pageContext.request.contextPath}/img/close.gif',
            handler:function(){
            	closeTab(parent.tab);
            }
        },'->'
    ]
    });  
        
       
        

//window 面板 进行查询
var queryWin = null;
function queryWinFun(id){
	resizable:false;
	var searchDiv = document.getElementById("divCheck_select") ;
	searchDiv.style.display = "block" ;
	if(!queryWin) { 
	    queryWin = new Ext.Window({
			title: '办公物品库存查询',
			resizable:false,   //禁止用户 四角 拖动
			contentEl:'divCheck_select',
	     	//renderTo : searchWin,
	     	width: 320,
	     	height:280,
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
          			goSearch_list();
					queryWin.hide();
            	}
        	},{
            	text:'清空',
            	handler:function(){
            		empty();
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
});
</script>
</head>
<body>
		<div id="divBtn"></div> 
		<div style="height:expression(document.body.clientHeight-28);">
			<mt:DataGridPrintByBean name="list" />
		</div>
	
<div id="divCheck_select" style="display: none;">
	<table border="0" align="center" width="100%">
		<tr>
			<td colspan="2" height="10"></td>
		</tr>
		<tr>
			<td width="30%" align="right">物品名称：</td>
			<td><input type="text" id="name" name="name"></td>
		</tr>
		<tr>
			<td width="30%" align="right">物品类别：</td>
			<td><input type="text" id="type" name="type"></td>
		</tr>
		<tr>
			<td align="right">编码：</td>
			<td><input type="text" id="coding" name="coding"></td>
		</tr>
		<tr>
			<td align="right">最低库存：</td>
			<td><input type="text" id="lowestStock" name="lowestStock"></td>
		</tr>
		<tr>
			<td align="right">最低警告库存：</td>
			<td><input type="text" id="lowestWarnStock" name="lowestWarnStock"></td>
		</tr>
		<tr>
			<td align="right">最高警告库存：</td>
			<td><input type="text" id="highestWarnStock" name="highestWarnStock"></td>
		</tr>
		<tr>
			<td align="right">描述：</td>
			<td><input type="text" id="remark" name="remark"></td>
		</tr>
		 
	</table>
</div>
 
<Script>
 
 function empty(){
		document.getElementById("name").value="";
	 	document.getElementById("type").value="";
		document.getElementById("coding").value="";
		document.getElementById("lowestStock").value="";
		document.getElementById("lowestWarnStock").value="";
		document.getElementById("highestWarnStock").value="";
		document.getElementById("remark").value="";
 }
 
 //申领
 function goApply(){
	 var uuids = getChooseValue("list");
	 if(uuids ==""){
		 alert("请选择要申领的物品");
		 return ;
	 }else{
	 	window.location = "${pageContext.request.contextPath}/waresStock.do?method=ordinaryPeopleApplySkip&uuids="+uuids;
	 }
}
 
 //查看
 function goView(){
	 var uuids = getChooseValue("list");
	 if(uuids ==""){
		 alert("请选择要查看的物品");
		 return ;
	 }else{
		 if(uuids.indexOf(",")>-1){
			 alert("不能同时查看多条物品详情，请选择单条进行查看！");
			return ;
		 }else{
		 	window.location = "${pageContext.request.contextPath}/waresStock.do?method=viewSkip&uuid="+uuids;
		 }
	 }
 }
 
</Script>