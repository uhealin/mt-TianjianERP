<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>客户走访记录列表</title>

<script type="text/javascript">  

function ext_init(){
	
	var tbar_customerNoteList = new Ext.Toolbar({
		renderTo: 'gridDiv_customerNoteList',
		items:[{
			text:'新增', 
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/add.gif',
			handler:function () {
				add();
			}
		},'-',{
            text:'修改',
            cls:'x-btn-text-icon',
            icon:'/AuditSystem/img/edit.gif',
           	handler:function(){
				edit();
			}
        },'-',{
            text:'删除',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/delete.gif',
            handler:function () {
            	remove();
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
				var autoid = document.getElementById("chooseValue_customerNoteList").value;
				if(autoid ==""){
					alert("请选择一项");
					return ;
				}
				window.location="${pageContext.request.contextPath}/customerNote.do?method=look&autoId="+autoid;
		}
		},'-',{
			text:'打印',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/print.gif',
			handler:function () {
				print_customerNoteList();
			}
		},'-',{
			text:'客户服务统计',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/edit.gif',
			handler:function () {
			
				var randStr = Math.random();
		        var url = "${pageContext.request.contextPath}/customerNote.do?method=noteCount";  		
		        var tab = parent.tab ;
		        if(tab){ 
					n = tab.add({    
						'title':"客户服务统计",    
						closable:true,  //通过html载入目标页    
						html:'<iframe name="test_' + randStr + '" scrolling="auto" frameborder="0" width="100%" height="100%" src=""></iframe>'   
					}); 
			        tab.setActiveTab(n);
				}
		        thisForm.action = url;
		        thisForm.target = "test_" + randStr;
		        thisForm.submit();
        
			}
		}]
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
          			goSearch_customerNoteList();
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
<form id="thisForm" method="post">

<div style="height:expression(document.body.clientHeight-23);" >
<mt:DataGridPrintByBean name="customerNoteList"/>
</div>
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
		
		<td align="right">服务联系人：</td>
		<td align=left>
			<input type="text" name="recorder" id="recorder" />
		</td>
	</tr>
	<tr align="center">
		<td align="right">服务时间：</td>
		<td align=left>
			<input type="text" name="visitTime" id="visitTime" class="validate-date-cn"/>
		</td>
	</tr>	
</table>
</div>

	<input type="hidden" name="fileDirPath" id="fileDirPath" value="${fileDirPath }">
	<input type="hidden" name="filePath" id="filePath">
	<input type="hidden" name="fileName" id="fileName">
</form>


</body>
<script type="text/javascript">
//-----------------------
// 查看记录
//-----------------------
function viewNote(trObj){
	var autoId = trObj.autoId;
	alert(autoId);
}

//-----------------------
// 删除记录
//-----------------------
function remove(){
	var autoId = document.getElementById("chooseValue_customerNoteList").value;

	if(autoId == "") {
		alert("请选择要删除的记录");
		return;
	}

	if(confirm("是否要删除该走访记录??")) {
		thisForm.action = "${pageContext.request.contextPath}/customerNote.do?method=remove&autoId=" + autoId;
		thisForm.target = "";	
		thisForm.submit();
	}
}

//-----------------------
// 新增记录
//-----------------------
function add(){
	thisForm.action = "${pageContext.request.contextPath}/customerNote.do?method=edit";
	thisForm.target = "";	
	thisForm.submit();
}

//-----------------------
// 删除记录
//-----------------------
function edit(){
	var autoId = document.getElementById("chooseValue_customerNoteList").value;

	if(autoId == "") {
		alert("请选择要修改的记录");
		return;
	}

	thisForm.action = "${pageContext.request.contextPath}/customerNote.do?method=edit&autoId=" + autoId;
	thisForm.target = "";	
	thisForm.submit();
}
//-------------------------------
//查看附件
//-------------------------------
function openFile(filepath,filename){
	document.getElementById("filePath").value = document.getElementById("fileDirPath").value + filepath;
	document.getElementById("fileName").value = filename;
    
	var thisForm = document.getElementById("thisForm");
	thisForm.action = "${pageContext.request.contextPath}/common.do?method=download";
	thisForm.target = "";	
	thisForm.submit();

}

//清空
function clearAll(){
	window.location = "${pageContext.request.contextPath}/customerNote.do";
}
</script>

</html>