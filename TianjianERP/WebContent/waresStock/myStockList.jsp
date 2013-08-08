<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>	
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>我的物品管理</title>
<script type="text/javascript">

var queryWinTake =null;
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
		        {contentEl:'tab1', title:'我申请的物品', id:'cur1'},
		        {contentEl:'tab2', title:'我领用的物品', id:'cur2'} 
		    ]
		});
		
		tabs.on("tabchange",function(tabpanel,tab) {
	    	if(tab.id == "cur2") {
					goSearch_applyNeckList();			
			} 
	    }) ;

		goSearch_myWaresApplyList();

var tbar_customer=new Ext.Toolbar({
	renderTo:'divBtn',
       items:[{
		text:'查询',
   		icon:'${pageContext.request.contextPath}/img/query.gif',
   		 handler:queryWinFun
	},'-',{
		text:'发起物品申请',
   		icon:'${pageContext.request.contextPath}/img/start.png',
   		 handler:function(){
   			parent.openTab("officeStockId","办公物品库存查询","waresStock.do?method=list");
   			//window.location="${pageContext.request.contextPath}/waresStock.do?method=list";
   			
   		 }
	},'-',{
		text:'删除',
   		icon:'${pageContext.request.contextPath}/img/delete.gif',
   		 handler:function(){
   			goDel();
   		 }
	},'-',{
		text:'刷新',
   		icon:'${pageContext.request.contextPath}/img/refresh.gif',
   		 handler:function(){
   			empty();
   			goSearch_myWaresApplyList();
   		 }
	} 
    ]
    });  
        
      
var tbar_customer2=new Ext.Toolbar({
		renderTo:'divBtn2',
           items:[{
			text:'查询',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
	   		 handler:queryWinFun2
		},'-',{
			text:'报废',
	   		icon:'${pageContext.request.contextPath}/img/notpass.png',
	   		 handler:function(){
					goUpdate();
	   		 }
		},'-',{
			text:'刷新',
			id:'refurbish',
	   		icon:'${pageContext.request.contextPath}/img/refresh.gif',
	   		 handler:function(){
	   			emptyNeck();
	   			goSearch_applyNeckList();
	   		 }
		}
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
			title: '我申请的物品查询',
			resizable:false,   //禁止用户 四角 拖动
			contentEl:'divCheck_select',
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
          			goSearch_myWaresApplyList();
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

//window 面板 进行查询
var queryWin2 =null;
function queryWinFun2(id){
	resizable:false;
	var searchDiv2 = document.getElementById("divCheck_select2") ;
	searchDiv2.style.display = "block" ;
	if(!queryWin2) { 
	    queryWin2 = new Ext.Window({
			title: '我申领的物品查询',
			resizable:false,   //禁止用户 四角 拖动
			contentEl:'divCheck_select2',
	     	//renderTo : searchWin,
	     	width: 320,
	     	height:200,
        	closeAction:'hide',
       	    listeners : {
	         	'hide':{
	         		fn: function () {
	         			new BlockDiv().hidden();
						queryWin2.hide();
					}
				}
	        },
        	layout:'fit',
	    	buttons:[{
            	text:'搜索',
          		handler:function(){
          			goSearch_applyNeckList();
					queryWin2.hide();
            	}
        	},{
            	text:'清空',
            	handler:function(){
            		emptyNeck();
            	}
        	},{
            	text:'取消',
            	handler:function(){
               		queryWin2.hide();
            	}
        	}]
	    });
    }
    new BlockDiv().show();
    queryWin2.show();
}

});

</script>
</head>
<body >

<div id="my-tabs" >
	<div id="tab1">
		<div id="divBtn"></div> 
		<div style="height:expression(document.body.clientHeight-55);" >
			<mt:DataGridPrintByBean name="myWaresApplyList" />
		</div>
	</div> 
	
	<div id="tab2">
		<div id="divBtn2"></div> 
		<div style="height:expression(document.body.clientHeight-55);" >
			<mt:DataGridPrintByBean name="applyNeckList" />
		</div>
	</div> 
</div>
	
<div id="divCheck_select" style="display: none;">
	<table border="0" align="center" width="100%">
		<tr>
			<td colspan="2" height="10"></td>
		</tr>
		<tr>
			<td width="30%" align="right">名称：</td>
			<td><input type="text" id="name" name="name"></td>
		</tr>
		<tr>
			<td width="30%" align="right">数量：</td>
			<td><input type="text" id="quantity" name="quantity"></td>
		</tr>
		<tr>
			<td align="right">申请日期：</td>
			<td><input type="text" id="applyDate" name="applyDate"></td>
		</tr>
		<tr>
			<td align="right">申请原因：</td>
			<td><input type="text" id="applyReason" name="applyReason"></td>
		</tr>
	</table>
</div>

<div id="divCheck_select2" style="display: none;">
	<table border="0" align="center" width="100%">
		<tr>
			<td colspan="2" height="10"></td>
		</tr>
		<tr>
			<td width="30%" align="right">名称：</td>
			<td><input type="text" id="name2" name="name2"></td>
		</tr>
		<tr>
			<td width="30%" align="right">数量：</td>
			<td><input type="text" id="quantity2" name="quantity2"></td>
		</tr>
		<tr>
			<td align="right">申请日期：</td>
			<td><input type="text" id="applyDate2" name="applyDate2"></td>
		</tr>
		<tr>
			<td align="right">申请原因：</td>
			<td><input type="text" id="applyReason2" name="applyReason2"></td>
		</tr>
	</table>
</div>
<Script> 

new Ext.form.DateField({			
	applyTo : 'applyDate',
	width: 133,
	format: 'Y-m-d'	
});
new Ext.form.DateField({			
	applyTo : 'applyDate2',
	width: 133,
	format: 'Y-m-d'	
});
 
 //申请
 function empty(){
	 	document.getElementById("name").value="";
		document.getElementById("quantity").value="";
		document.getElementById("applyDate").value="";
		document.getElementById("applyReason").value="";
 }
 
 //申领
 function emptyNeck(){
	 	document.getElementById("name2").value="";
		document.getElementById("quantity2").value="";
		document.getElementById("applyDate2").value="";
		document.getElementById("applyReason2").value="";
}

 
 //删除
 function goDel(){
	 var uuids = document.getElementById("chooseValue_myWaresApplyList").value;	
	 
	 	if(uuids=="" || uuids==null){
			alert("请选择要删除的物品申请！");
			return;
	   	}else{
			 if(confirm("您确定要删除物品申请？删除后将无法恢复！","yes")){
				 
				 window.location = "${pageContext.request.contextPath}/waresStock.do?method=deleteWaresStream&uuid="+uuids;
			 }
		}
 }
 
 //function openByTab(url,name,id) {
 //		var tab = parent.tab ;
 //		var n = tabs.getComponent(id);  
 //		if (!n) { //判断是否已经打开该面板    
 //			n = tabs.add({    
 //				 id:id,    
 //				 title:name,  
 //				 closable:true,  //通过html载入目标页    
 //				 html:'<iframe id="tab'+id+'" scrolling="auto" frameborder="1" width="100%" height="100%" src=""></iframe>'   
 //			}).show();   

// 			document.getElementById("tab"+id).src=url;
// 		} 	 
// 		
// 		tabs.setActiveTab(n);
// 	}
 
 function goUpdate(){
	 	var uuids =  document.getElementById("chooseValue_applyNeckList").value;	
	 	if(uuids=="" || uuids==null){
			alert("请选择要报废的对象！");
			return;
	   	}else{
	   		//openByTab("${pageContext.request.contextPath}/waresStock.do?method=stockScrapSkip&uuid="+uuids,"物品报废","cancelWaresId");
			 window.location = "${pageContext.request.contextPath}/waresStock.do?method=stockScrapSkip&paramSkip=myAplly&uuid="+uuids;
				 
		}
 }
 
</Script>