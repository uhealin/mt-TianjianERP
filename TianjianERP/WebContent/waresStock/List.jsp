<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>	
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>物品库存管理</title>
<script type="text/javascript">

Ext.onReady(function(){
	 
var tbar_customer=new Ext.Toolbar({
	renderTo:'divBtn',
       items:[{
           text:'新增物品',
           icon:'${pageContext.request.contextPath}/img/add.gif',
           handler:function(){
           				goAdd();
			}
     	},'-',{
            text:'已有物品入库登记',
            icon:'${pageContext.request.contextPath}/img/add.gif',
            handler:function(){
            				goStock();
 			}
      	},'-',{
            text:'物品归还登记',
            icon:'${pageContext.request.contextPath}/img/confirm.gif',
            handler:function(){
            	goReturn();
 			}
      	},'-',{
            text:'物品报废登记',
            icon:'${pageContext.request.contextPath}/img/notpass.png',
            handler:function(){
            	goScrap();
 			}
      	},'-',{
			text:'领用清单查询',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
	   		 handler:function(){
	   			var uuid =  document.getElementById("chooseValue_waresList").value;	
	   			
	   			parent.openTab("applyInventoryList","物品领用清单查询","waresStock.do?method=applyInventoryList&uuid="+uuid);
	   		 }
		},'-',{
			text:'办公用品统计查询',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
	   		 handler:function(){
	   			var uuid =  document.getElementById("chooseValue_waresList").value;
	   			parent.openTab("waresStatisticsList","办公用品统计查询","waresStock.do?method=waresStatisticsList&uuid="+uuid);
	   		 }
		},'-',{
			text:'修改',
	   		icon:'${pageContext.request.contextPath}/img/edit.gif',
	   		handler:function(){
	   			goUpdate();
	   		}
		},'-',{
			text:'删除',
	   		icon:'${pageContext.request.contextPath}/img/delete.gif',
	   		handler:function(){
	   			goDel();
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
	   			goSearch_waresList();
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
			title: '物品库存查询',
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
          			goSearch_waresList();
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
<body >
		<div id="divBtn"></div> 
		<div style="height:expression(document.body.clientHeight-28);" >
			<mt:DataGridPrintByBean name="waresList" />
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
 
 function goAdd(){
	 window.location="${pageContext.request.contextPath}/waresStock.do?method=addSkip";
 }

 
 function inboxTake(){
	 
	 var receiveUserId = document.getElementById("receiveUserId").value;
	 var packageCodeId = document.getElementById("packageCodeId").value;
	
	 if(receiveUserId ==""){
		 alert("请选择领取人！");
		 return ;
	 }
	 
	 if(packageCodeId ==""){
		 alert("请选择包裹条码！");
		 return ;
	 }
	 var url="${pageContext.request.contextPath}/waresStock.do?method=existsPackageCodeId";
	 var requestString = "&packageCodes="+packageCodeId;
	 var request= ajaxLoadPageSynch(url,requestString);
	 
	 if(request !=""){
		 alert(request);
		 return ;
	 }
	 
	   url="${pageContext.request.contextPath}/waresStock.do?method=updateStatus";
	   requestString = "&packageCodes="+packageCodeId+"&receiveUserId="+receiveUserId;
	   request= ajaxLoadPageSynch(url,requestString);
	  
	   if(request !=""){
		   alert(request);
		   return ;
	   }else{
		   alert("领取成功!");
		   queryWinTake.hide();
		   goSearch_waresList();
	   }
 }
 
 //已有物品入库登记
 function goStock(){
	 
	 var uuids = document.getElementById("chooseValue_waresList").value;	
	 
	 	if(uuids=="" || uuids==null){
			alert("请选择要物品入库登记的对象！");
			return;
	   	}else{
	   		 var url="${pageContext.request.contextPath}/waresStock.do";
	   		 $.post(url,{uuid:uuids,method:"doCheckPurc"},function(text){
	   			if(text=="0"){
	   			 window.location = url+"?method=stockRegisterSkip&uuid="+uuids;
	   			} else{
	   				alert(text);
	   			}
	   		 });
		} 
 }
 
 //归还
 function goReturn(){
	 
	 var uuids = document.getElementById("chooseValue_waresList").value;	
	 
	 	if(uuids=="" || uuids==null){
			alert("请选择要物品归还登记的对象！");
			return;
	   	}else{
			 window.location = "${pageContext.request.contextPath}/waresStock.do?method=stockReturnSkip&uuid="+uuids;
		} 
 }
 
 //报废
 function goScrap(){
	 
	 var uuids = document.getElementById("chooseValue_waresList").value;	
	 
	 	if(uuids=="" || uuids==null){
			alert("请选择要物品报废登记的对象！");
			return;
	   	}else{
			 window.location = "${pageContext.request.contextPath}/waresStock.do?method=stockScrapSkip&uuid="+uuids;
		} 
 }
 
 //删除
 function goDel(){
	 var uuids = document.getElementById("chooseValue_waresList").value;	
	 
	 	if(uuids=="" || uuids==null){
			alert("请选择要删除的对象！");
			return;
	   	}else{
			  
		 if(confirm("您确定要删除对象吗？删除后将无法恢复！","yes")){
			 
			 window.location = "${pageContext.request.contextPath}/waresStock.do?method=delete&uuid="+uuids;
		 }
				 
		}
 }
 
 
 function goUpdate(){
	 	var uuids =  document.getElementById("chooseValue_waresList").value;	
	 
	 	if(uuids=="" || uuids==null){
			alert("请选择要修改的对象！");
			return;
	   	}else{
			 window.location = "${pageContext.request.contextPath}/waresStock.do?method=updateSkip&uuid="+uuids;
				 
		}
 }
 
</Script>