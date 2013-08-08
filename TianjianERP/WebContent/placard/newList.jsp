<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>	
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>内部短信管理</title>
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
		    height: document.body.clientHeight-Ext.get('my-tabs').getTop(),
		    width : document.body.clientWidth, 
		    defaults: {autoWidth:true,autoHeight:true},
		    items:[
		        {contentEl:'tab1', title:'未查看短信', id:'cur1'},
		        {contentEl:'tab2', title:'已查看短信', id:'cur2'},
		        {contentEl:'tab3', title:'已发送短信', id:'cur3'}
		    ]
		});
		
		tabs.on("tabchange",function(tabpanel,tab) {
	    	if(tab.id == "cur2") {
					goSearch_alreadyReadList();			
			}else if(tab.id == "cur3"){
					goSearch_alreadySendList();	
			}
	    }) ;

		goSearch_waitReadList();

var tbar_customer=new Ext.Toolbar({
	renderTo:'divBtn',
       items:[{
           text:'阅读短信',
           icon:'${pageContext.request.contextPath}/img/add.gif',
           handler:function(){
        	   goRead();
			}
	 },'-',{
         text:'发送短信',
         icon:'${pageContext.request.contextPath}/img/mytask.gif',
        	handler:function(){
        			var url = MATECH_SYSTEM_WEB_ROOT+"placard/AddPlacard.jsp?opt=newList" ;
        			var tab = parent.tab ;
     	        if(tab && tab.id == "mainFrameTab"){
						var n = tab.add({    
							title:"发送短信",
							closable:true,
							html:'<iframe scrolling="no" frameborder="0" width="100%" height="100%" src="'+url+'"></iframe>'    
						});
						tab.setActiveTab(n);
     	        }else {
     				window.open(url);
     			}	
			}
    	 },'-',{
           text:'批量删除短信',
           icon:'${pageContext.request.contextPath}/img/delete.gif',
           handler:function(){
        	   goDel(1);
			}
	  	 },'-',{
	           text:'删除所有短信',
	           icon:'${pageContext.request.contextPath}/img/delete.gif',
	           handler:function(){
	        	   goDel(2);
				}
		 },'-',{
	           text:'批量标记为已查看短信',
	           icon:'${pageContext.request.contextPath}/img/add.gif',
	           handler:function(){
	        	   signRead(1);
				}
		 },'-',{
	           text:'标记全部为已查看短信',
	           icon:'${pageContext.request.contextPath}/img/add.gif',
	           handler:function(){
	        	   signRead(2);
				}
		 },'-',{
			text:'查询',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
	   		 handler:queryWinFun
		},'-',{
			text:'刷新',
	   		icon:'${pageContext.request.contextPath}/img/refresh.gif',
	   		 handler:function(){
	   			notEmpty();
	   			goSearch_waitReadList();
	   		 }
		},'-',{
			text:'打印',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/print.gif',
			handler:function(){
			     print_waitReadList();
			}
		},'->'
			
    ]
    });  
        


var tbar_customer2=new Ext.Toolbar({
		renderTo:'divBtn2',
           items:[{
			text:'查询',
			id:'btn-query',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
	   		 handler:queryWinFun2
		},'-',{
			text:'刷新',
			id:'refurbish',
	   		icon:'${pageContext.request.contextPath}/img/refresh.gif',
	   		 handler:function(){
	   			alreadyReadEmpty();
	   			goSearch_alreadyReadList();
	   		 }
		},'-',{
			text:'打印',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/print.gif',
			handler:function(){
			     print_alreadyReadList();
			}
		},'->'
        ]
        });  
        
var tbar_customer3=new Ext.Toolbar({
	renderTo:'divBtn3',
       items:[{
		text:'查询',
		id:'btn-query',
   		icon:'${pageContext.request.contextPath}/img/query.gif',
   		 handler:myInboxWindow
	},'-',{
		text:'刷新',
		id:'refurbish',
   		icon:'${pageContext.request.contextPath}/img/refresh.gif',
   		 handler:function(){
   			alreadySendEmpty();
   			goSearch_alreadySendList();
   		 }
	},'-',{
		text:'打印',
		cls:'x-btn-text-icon',
		icon:'${pageContext.request.contextPath}/img/print.gif',
		handler:function(){
		     print_alreadySendList();
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
			title: '未查看短信查询',
			resizable:false,   //禁止用户 四角 拖动
			contentEl:'divCheck_select',
	     	//renderTo : searchWin,
	     	width: 300,
	     	height:220,
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
          			goSearch_waitReadList();
					queryWin.hide();
            	}
        	},{
            	text:'清空',
            	handler:function(){
            		notEmpty();
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
	var searchDiv2 = document.getElementById("alreadyRead") ;
	searchDiv2.style.display = "block" ;
	if(!queryWin2) { 
	    queryWin2 = new Ext.Window({
			title: '已查看短信查询',
			resizable:false,   //禁止用户 四角 拖动
			contentEl:'alreadyRead',
	     	//renderTo : searchWin,
	     	width: 320,
	     	height:220,
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
          			goSearch_alreadyReadList();
					queryWin2.hide();
            	}
        	},{
            	text:'清空',
            	handler:function(){
            		alreadyReadEmpty();
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


var alreadySend=null;
function myInboxWindow(id){
	resizable:false;
	document.getElementById("alreadySend").style.display = "block" ;
	if(!alreadySend) { 
		alreadySend = new Ext.Window({
			title: '已发短信查询',
			resizable:false,   //禁止用户 四角 拖动
			contentEl:'alreadySend',
	     	//renderTo : searchWin,
	     	width: 320,
	     	height:200,
        	closeAction:'hide',
       	    listeners : {
	         	'hide':{
	         		fn: function () {
	         			new BlockDiv().hidden();
	         			alreadySend.hide();
					}
				}
	        },
        	layout:'fit',
	    	buttons:[{
            	text:'搜索',
          		handler:function(){
          			goSearch_alreadySendList();
          			alreadySend.hide();
            	}
        	},{
            	text:'清空',
            	handler:function(){
            		alreadySendEmpty();
            	}
        	},{
            	text:'取消',
            	handler:function(){
            		alreadySend.hide();
            	}
        	}]
	    });
    }
    new BlockDiv().show();
    alreadySend.show();
}

});

</script>
</head>
<body >

<div id="my-tabs">

	<div id="tab1">
		<div id="divBtn"></div> 
		<div style="height:expression(document.body.clientHeight-55);" >
			<mt:DataGridPrintByBean name="waitReadList" />
		</div>
	</div> 
	
	<div id="tab2">
		<div id="divBtn2"></div> 
		<div style="height:expression(document.body.clientHeight-55);" >
			<mt:DataGridPrintByBean name="alreadyReadList" />
		</div>
	</div> 
	
	<div id="tab3">
	<div id="divBtn3"></div>
		<div style="height:expression(document.body.clientHeight-55);" >
			<mt:DataGridPrintByBean name="alreadySendList" />
		</div>
	</div>
</div>
	
<div id="divCheck_select" style="display: none;">
	<table border="0" align="center" width="100%">
		<tr>
			<td colspan="2" height="10"></td>
		</tr>
		<tr>
			<td width="35%" align="right">发信人：</td>
			<td><input type="text" id="waitReadAddressee" name="waitReadAddressee"></td>
		</tr>
		<tr>
			<td  align="right">发信人所属部门：</td>
			<td><input type="text" id="waitReadDepartName" name="waitReadDepartName"></td>
		</tr>
		<tr>
			<td align="right">发信时间：</td>
			<td><input type="text" id="waitReadDate" name="waitReadDate"></td>
		</tr>
		 <tr>
		 	<td align="right">短信标题：</td>
		 	<td>
		 		<input type="text" id="waitReadCaption" name="waitReadCaption"    >
		 	</td>
		 </tr>
	</table>
</div>

<div id="alreadyRead" style="display: none;">
	<table border="0" align="center" width="100%">
		<tr>
			<td colspan="2" height="10"></td>
		</tr>
		<tr>
			<td colspan="2" height="10"></td>
		</tr>
		<tr>
			<td width="35%" align="right">发信人：</td>
			<td><input type="text" id="alreadyReadAddressee" name="alreadyReadAddressee"></td>
		</tr>
		<tr>
			<td  align="right">发信人所属部门：</td>
			<td><input type="text" id="alreadyReadDepartName" name="alreadyReadDepartName"></td>
		</tr>
		<tr>
			<td align="right">发信时间：</td>
			<td><input type="text" id="alreadyReadDate" name="alreadyReadDate"></td>
		</tr>
		 <tr>
		 	<td align="right">短信标题：</td>
		 	<td>
		 		<input type="text" id="alreadyReadCaption" name="alreadyReadCaption"    >
		 	</td>
		 </tr>
	</table>
</div>
 
 <div id="alreadySend" style="display: none;">
 	<table style="border: 1px;width: 96%;line-height: 20px;" align="center">
		<tr>
			<td colspan="2" height="20px;">&nbsp; </td>
		</tr>
		 <tr>
		 	<td align="right">收信人：</td>
		 	<td>
		 		<input type="text" id="addressee"  name="addressee">
		 	</td>
		 </tr>
		  <tr>
		 	<td align="right">发送时间：</td>
		 	<td>
		 		<input type="text" id="sendDate"   name="sendDate"   >
		 	</td>
		 </tr>
		 <tr>
		 	<td align="right">短信标题：</td>
		 	<td>
		 		<input type="text" id="caption"  name="caption"    >
		 	</td>
		 </tr>
	</table>
 </div>

<Script>

new Ext.form.DateField({			
	applyTo : 'sendDate',
	width: 133,
	format: 'Y-m-d'	
});
 
new Ext.form.DateField({			
	applyTo : 'alreadyReadDate',
	width: 133,
	format: 'Y-m-d'	
});
 
new Ext.form.DateField({			
	applyTo : 'waitReadDate',
	width: 133,
	format: 'Y-m-d'	
});
 
 
 
 function notEmpty(){
		document.getElementById("waitReadAddressee").value="";
		document.getElementById("waitReadDepartName").value="";
		document.getElementById("waitReadDate").value="";
		document.getElementById("waitReadCaption").value="";
 }
 
 function alreadyReadEmpty(){
		document.getElementById("alreadyReadCaption").value="";
	 	document.getElementById("alreadyReadAddressee").value="";
		document.getElementById("alreadyReadDepartName").value="";
		document.getElementById("alreadyReadDate").value="";
}
 function alreadySendEmpty(){
	 	document.getElementById("addressee").value="";
		document.getElementById("sendDate").value="";
		document.getElementById("caption").value="";
		 
}
 
//dategrid双击事件
 function grid_dblclick(obj, tableId) {
 	var seadId=obj.seadId;
 	if(id==""){
 		return ;
 	}else{
 		 window.location ="${pageContext.request.contextPath}/placard/View.jsp?ids="+seadId;	
 	}
 }

 
 function goRead(){
	 var ids = getChooseValue("waitReadList");
	 
	 if(ids == ""){
		 alert("请选择要阅读的对象");
		 return ;
	 }else{
		 
		 	window.location="${pageContext.request.contextPath}/placard/View.jsp?ids="+ids;
	 }
	 
 }

  
 //标记为
 function signRead(obj){
	 var ids = getChooseValue("waitReadList");
	 
	 if(obj == 1){ //批量删除
			if(ids ==""){
				alert("请选择要标记为已查看的短信!");
				return ;
			}		 
	 }else if(obj ==2){ //全部删除
		 
	 }
		 if(confirm("您确定要把已选择的短信标记为已查看短信吗？")){
			  var url="${pageContext.request.contextPath}/placard.do?method=signRead";
			  var requestString = "&ids="+ids;
			  var request= ajaxLoadPageSynch(url,requestString);
			  
			  if(request == "true"){
				  goSearch_waitReadList();
				  alert("标记成功");
				  return ;
			  }else{
				  alert("标记失败");
				  return ;
			  }
		 }
 }
 
 //删除
 function goDel(obj){
	 
	 var ids = getChooseValue("waitReadList");
	 
	 if(obj == 1){ //批量删除
		if(ids ==""){
			alert("请选择要删除的短信!");
			return ;
		}		 
	 }else if(obj ==2){ //全部删除
		 
	 }
	 
	if(confirm("您确定要删除短信吗？删除后将无法恢复！","yes")){
		 
		  var url="${pageContext.request.contextPath}/placard.do?method=del";
		  var requestString = "&ids="+ids;
		  var request= ajaxLoadPageSynch(url,requestString);
		  
		  if("true" == request){
			  alert("删除成功！");
			  goSearch_waitReadList();
		  	  return ;
		  }else{
			  alert("删除失败！");
		  	 return;
		  }
	 }
 }
   
</Script>