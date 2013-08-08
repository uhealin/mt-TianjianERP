<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>客户接洽跟踪记录</title>

<script type="text/javascript">  

function ext_init(){
	
	var tbar_customerTrackList = new Ext.Toolbar({
		renderTo: 'gridDiv_customerTrackList',
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
            icon:'/AuditSystem/img/edit.gif',
           	handler:function(){
				goUpdate();
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
			handler:function () {
            	queryWinFun();
            }
		},'-',{
			text:'查看',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:function () {
				var autoid = document.getElementById("chooseValue_customerTrackList").value;
				if(autoid ==""){
					alert("请选择一项");
					return ;
				}
				window.location="${pageContext.request.contextPath}/customerConsult.do?method=look&autoid="+autoid;
		}
		},'-',{
			text:'打印',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/print.gif',
			handler:function () {
				print_customerTrackList();
			}
		}
<c:if test="${frameTree != 1}">  		
		,'-',{
			text:'客户接洽统计',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/edit.gif',
			handler:function () {
			
				var randStr = Math.random();
		        var url = "${pageContext.request.contextPath}/customerConsult.do?method=consultCount";  		
		        var tab = parent.tab ;
		        if(tab){ 
					n = tab.add({    
						'title':"客户接洽统计",    
						closable:true,  //通过html载入目标页    
						html:'<iframe name="test_' + randStr + '" scrolling="auto" frameborder="0" width="100%" height="100%" src=""></iframe>'   
					}); 
			        tab.setActiveTab(n);
				}
		        thisForm.action = url;
		        thisForm.target = "test_" + randStr;
		        thisForm.submit();
        
			}
		},
 		
		'-',{
			text:'关闭',
			cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/close.gif',
			handler:function () {
				closeTab(parent.tab);
				//parent.tab.remove(parent.tab.getActiveTab()); 
			}
		}
</c:if>		
		]
	});
	
} 

var queryWin = null;
function queryWinFun(id){
	var searchDiv = document.getElementById("search") ;
	searchDiv.style.display = "" ; 	
	if(!queryWin) { 
	    queryWin = new Ext.Window({
			title: '查询',
			contentEl:'search',
		    renderTo :'searchWin',
	     	width: 350,
	     	height:200,
        	closeAction:'hide',
       	    listeners : {
	         	'hide':{
	         		fn: function () {
	         			new BlockDiv().hidden();
	         			searchDiv.style.display = "none" ;
						queryWin.hide();
					}
				}
	        },
        	layout:'fit',
	    	//html:searchDiv.innerHTML,
	    	buttons:[{
            	text:'确定',
          		handler:function(){
          			goSearch_customerTrackList();
    	           	queryWin.hide();
               		
            	}
          		
        	},{
            	text:'取消',
            	handler:function(){
               		queryWin.hide();
            	}
        	}]
	    });
	    
	    var udate,edate;
	    
	    if(!udate) {
		    udate = new Ext.form.DateField({
				applyTo : 'visitTime',
				width: 135,
				format: 'Y-m-d',
				emptyText: '' 
			});
	    }
		
    }
    new BlockDiv().show();
    queryWin.show();
}
window.attachEvent('onload',ext_init);
</script>

</head>

<body >

<form action="" method="post" id="thisForm" name="thisForm">

<div style="height:expression(document.body.clientHeight-23);" >
<mt:DataGridPrintByBean name="customerTrackList" outputData="true" outputType="invokeSearch" />
</div>
<input type="hidden" name="customerid" id="customerid" value="${customerid}"> 
<input name="customerName" type="hidden"  value="${customerName}" >&nbsp;

<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth)/2);top:expression((document.body.clientHeight)/2); z-index: 2"></div>

<div id="search" style="display:none">
<br/>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>

<table border="0" cellpadding="0" cellspacing="0" width="100%"  bgcolor="" >
	<tr align="center">
		
		<td align="right">客户名称：</td>
		<td align=left>
			<input  type="text" name="customerName" id="customerName" size="20" />
		</td>
	</tr>
	<tr align="center">
		
		<td align="right">联系人：</td>
		<td align=left>
			<input type="text" name="recorder" id="recorder" />
		</td>
	</tr>
	<tr align="center">
		<td align="right">来访时间：</td>
		<td align=left>
			<input type="text" name="visitTime" id="visitTime"  class="validate-date-cn"/>
		</td>
	</tr>	
</table>
</div>

</form>
</body>

<script>

//添加客户追踪记录
function goAdd(){
    
	if("${customerid}"==""){
		thisForm.action = "${pageContext.request.contextPath}/Customer/AddcustomerConsult.jsp?isAll=all" ;
	}else{
		thisForm.action = "${pageContext.request.contextPath}/Customer/AddcustomerConsult.jsp?customerid="+"${customerid}"
	}
	thisForm.target = "";	
	thisForm.submit();       
}                                 

//修改客户追踪记录
function goUpdate(){
	var choose_customerTrackList = document.getElementById("chooseValue_customerTrackList").value;

	if(choose_customerTrackList == ""){
		alert("请选择要修改的客户追踪记录！");
	} else {		
		thisForm.action = "${pageContext.request.contextPath}/customerConsult.do?method=exitCustomerTrack&autoid=" + choose_customerTrackList+"&customerid="+"${customerid}&flag=${flag}" ;
		thisForm.target = "";
		thisForm.submit();  		
	}
	
}

//删除客户追踪记录
function goDelete(){
	var choose_customerTrackList = document.getElementById("chooseValue_customerTrackList").value;

	if(choose_customerTrackList == ""){
		alert("请选择要删除的客户追踪记录！");
	} else {
		if(confirm("您的操作可能会造成数据丢失，您确定要删除该记录吗？","提示")){
			thisForm.action = "${pageContext.request.contextPath}/customerConsult.do?method=removeCustomerTrack&autoid=" + choose_customerTrackList+"&customerid="+"${customerid}" ;
			thisForm.target = "";
			thisForm.submit();  
			
		}
	}
}

//清空
function clearAll(){	
	if("${customerid}"==""){
		window.location = "${pageContext.request.contextPath}/customerConsult.do?";
	}else{
		window.location = "${pageContext.request.contextPath}/customerConsult.do?customerid=${customerid}";
	}	
}
if("${consultSkip}" == "add"){
	parent.openTab("commerceRegister","商业登记","customer.do?method=businessAdd&customerid=${customerId}");
	
}
</script>
</body>
</html>