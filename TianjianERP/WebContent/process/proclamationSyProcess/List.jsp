<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>	
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>公告管理</title>
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
			text:'发起申请',
            icon:'${pageContext.request.contextPath}/img/start.png',
			handler:function () {
				  goFlow();
			}
		},'-',{
	           text:'作废',
	           cls:'x-btn-text-icon',
	           icon:'${pageContext.request.contextPath}/img/recycle.png',
	           handler:function(){
	        	   goCancel();
			   }
	    },'-',{
	           text:'已驳回再修改',
	           cls:'x-btn-text-icon',
	           icon:'${pageContext.request.contextPath}/img/edit.gif',
	           handler:function(){
	        	   goRejectUpdate();
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
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/delete.gif',
			handler:function(){
				goDelete();
			}
		},'-',{
			text:'批量删除',
			id:'del',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/delete.gif',
			handler:function(){
			       var url = 'proclamation.do?method=list&rand='+Math.random();			
		           parent.openTab("proclamationDel","公告批量删除",url);
			}
		},'-',{
	           text:'查看',
	           cls:'x-btn-text-icon',
	           icon:'${pageContext.request.contextPath}/img/query.png',
	           handler:function(){
	        	   goLook();
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
	   			goSearch_proclamationList();
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
				title: '公告查询',
				resizable:false,   //禁止用户 四角 拖动
				contentEl:'divCheck_select',
		     	//renderTo : searchWin,
		     	width: 330,
		     	height:320,
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
	          			goSearch_proclamationList();
						queryWin.hide();
	            	}
	        	},{
	            	text:'显示全部',
	            	handler:function(){
	            		empty();
	            		goSearch_proclamationList();
	            		queryWin.hide();
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
}
window.attachEvent('onload',ext_init);

</script>

</head>

<body leftmargin="0" topmargin="0">
<div id="divBtn"></div> 

<div style="height:expression(document.body.clientHeight-28);" >
	<mt:DataGridPrintByBean name="proclamationList" />
</div>

<div id="divCheck_select" style="display: none;">
	<table border="0" align="center" width="100%" style="line-height: 25px;">
		<tr>
			<td colspan="2" height="10"></td>
		</tr>
		<tr>
			<td width="30%" align="right">标题：</td>
			<td><input type="text" id="title" name="title"></td>
		</tr>
		<tr>
			<td width="30%" align="right">选择置顶类型：</td>
			<td>
				<select name="up" id="up">
					<option value="" selected="selected" id="up_id">全部
					<option value="置顶">置顶
					<option value="不置顶">不置顶
				</select>
			</td>
		</tr>
		<tr>
			<td width="30%" align="right">状态：</td>
			<td>
				<input type="text" name="status" id="status" 
					  autoid=4069
				      onfocus="onPopDivClick(this)"
					  onkeydown="onKeyDownEvent()"
					  onkeyup="onKeyUpEvent()"
					  onclick="onPopDivClick(this)"
				>
			</td>
		</tr>
		<tr>
			<td width="30%" align="right">类型：</td>
			<td>
				<input type="text" name="ctype" id="ctype" 
					  autoid=853
				      onfocus="onPopDivClick(this)"
					  onkeydown="onKeyDownEvent()"
					  onkeyup="onKeyUpEvent()"
					  onclick="onPopDivClick(this)"
				>
			</td>
		</tr>
		<tr>
			<td width="30%" align="right">发布开始日期：</td>
			<td><input type="text" id="beginDate" name="beginDate"></td>
		</tr>
		<tr>
			<td width="30%" align="right">发布结束日期：</td>
			<td><input type="text" id="endDate" name="endDate"></td>
		</tr>
		<tr>
			<td align="right">内容：</td>
			<td>
				<textarea rows="5" cols="25" id="content" name="content"></textarea>
			 </td>
		</tr>
	</table>
</div>
<Script>

new Ext.form.DateField({			
	applyTo : 'beginDate',
	width: 133,
	format: 'Y-m-d'	
});

new Ext.form.DateField({			
	applyTo : 'endDate',
	width: 133,
	format: 'Y-m-d'	
});
function goRejectUpdate(){
 	var uuids =  document.getElementById("chooseValue_proclamationList").value;	
 
 	if(uuids=="" || uuids==null){
		alert("请选择要修改的对象！");
		return;
   	}else{
   		 var url="${pageContext.request.contextPath}/proclamationSy.do?method=getStatus";
		 var requestString = "&uuid="+uuids;
		 var request= ajaxLoadPageSynch(url,requestString);
		 if(request !="已驳回"){
			 alert("您的公告状态是"+request+"，不能进行修改操作");
			 return ;
		 }
   		
		 window.location = "${pageContext.request.contextPath}/proclamationSy.do?method=rejectUpdateSkip&uuid="+uuids;
			 
	}
}

function goAdd()
{
	window.location="${pageContext.request.contextPath}/proclamationSy.do?method=addSkip";
}
function goDelete()
{	
	
	var uuid = document.getElementById("chooseValue_proclamationList").value;
	
	if(uuid=="")
	{
		alert("请选择要删除的公告！");
		return ;
	}
	else
	{
		if(confirm("确定删除此公告吗？","yes")){
			 var url="${pageContext.request.contextPath}/proclamationSy.do?method=getStatus";
			 var requestString = "&uuid="+uuid;
			 var request= ajaxLoadPageSynch(url,requestString);
			 if(request !="未发起"){
				 alert("您的公告状态是"+request+"，不能删除，只有状态为未发起的才能进行删除!");
				 return ;
			 }
			window.location="proclamationSy.do?method=delete&&uuid="+uuid;
		}
	}
}

function goFlow(){
	var uuid = document.getElementById("chooseValue_proclamationList").value;
	
	if(uuid=="")
	{
		alert("请选择要发起的公告！");
		return ;
	}
	else
	{
		if(confirm("确定要发起此公告吗？","yes")){
			 var url="${pageContext.request.contextPath}/proclamationSy.do?method=getStatus";
			 var requestString = "&uuid="+uuid;
			 var request= ajaxLoadPageSynch(url,requestString);
			 if(request !="未发起"){
				 alert("您的公告状态是"+request+"，不能再次发起!");
				 return ;
			 }
			window.location="proclamationSy.do?method=startFlow&uuid="+uuid;
		}
	}
}

function goUpdate(){
	 	var uuids =  document.getElementById("chooseValue_proclamationList").value;	
	 
	 	if(uuids=="" || uuids==null){
			alert("请选择要修改的对象！");
			return;
	   	}else{
	   		 var url="${pageContext.request.contextPath}/proclamationSy.do?method=getStatus";
			 var requestString = "&uuid="+uuids;
			 var request= ajaxLoadPageSynch(url,requestString);
			 if(request !="未发起"){
				 alert("您的公告状态是"+request+"，不能进行修改操作");
				 return ;
			 }
	   		
			 window.location = "${pageContext.request.contextPath}/proclamationSy.do?method=updateSkip&uuid="+uuids;
				 
		}
}

//作废
function goCancel(){
	
	var uuid = document.getElementById("chooseValue_proclamationList").value;
	
	if(uuid=="")
	{
		alert("请选择要作废的公告！");
		return ;
	}
	else
	{
		if(confirm("确定要作废此公告吗？","yes")){
			 var url="${pageContext.request.contextPath}/proclamationSy.do?method=getStatus";
			 var requestString = "&uuid="+uuid;
			 var request= ajaxLoadPageSynch(url,requestString);
			 if(request =="未发起" || request =="已审批"){
				 alert("您的公告状态是"+request+"，不能进行作废操作");
				 return ;
			 }
			 url="${pageContext.request.contextPath}/proclamationSy.do?method=updateStatus";
			 requestString = "&uuid="+uuid;
			 request = ajaxLoadPageSynch(url,requestString);
			 if(request =="true"){
				 alert("作废成功!");
				 goSearch_proclamationList();
				 return ;
			 }
		}
	}
	
}

function goLook(){
 	var uuids =  document.getElementById("chooseValue_proclamationList").value;	
 
 	if(uuids=="" || uuids==null){
		alert("请选择要查看的对象！");
		return;
   	}else{
		 window.location = "${pageContext.request.contextPath}/proclamationSy.do?method=look&uuid="+uuids;
			 
	}
}
function empty(){
 
	document.getElementById("title").value="";
	document.getElementById("beginDate").value=""; 
	document.getElementById("endDate").value=""
	document.getElementById("content").value="";
	document.getElementById("up_id").selected= true;
	document.getElementById("status").value="";
	document.getElementById("ctype").value="";
}
</Script>