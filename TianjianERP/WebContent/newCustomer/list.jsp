<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>新闻列表</title>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/searchSession.js" charset=gbk></script>

<script type="text/javascript">

Ext.onReady(function(){
	var mytab = new Ext.TabPanel({
        id: "tab",
        renderTo: "divTab",
        activeTab:0, //选中第一个 tab
        autoScroll:true,
        frame: true,
        height: document.body.clientHeight-Ext.get('divTab').getTop(), 
        defaults: {autoHeight: true,autoWidth:true},
        items:[
            {contentEl: "noLaunch",title:"未发起",id:"noLaunchTab"},
            {contentEl: "isLaunch",title:"已发起",id:"isLaunchTab",html:
            	"<iframe src=\"${pageContext.request.contextPath}/newCustomer.do?method=isLaunchList\" style=\"width: 100%;height: 550\" id=\"launch\" scrolling=\"no\"></iframe>"}
        ]
    });
    
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
            text:'查 看',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/query.gif',
            handler:function () {
            	 goLook();
            }
        },'-',{
            text:'发起客户承接审批',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/start.png',
            handler:function () {
            	if(getState()!="未发起"){
        			alert("已发起或已通过状态，不能再次进行发起!");
        			return ;
        		}else{
        		   if(confirm("您确定要发起吗?发起后将无法再进行修改","yes")){
	            	 var uuid = document.getElementById("chooseValue_newCustomerList").value;
	            	 var url="${pageContext.request.contextPath}/newCustomer.do?method=statrAudit";
	            	 var requestString = "&uuid="+uuid;
	            	 var request= ajaxLoadPageSynch(url,requestString);
	            
	            	 alert(request);
	            	 goSearch_newCustomerList();
	           } 	 
            }
           }
        },'-',{
			text:'查询',
			id:'btn-query',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:queryWinFun
		},'-',{
            text:'打印',
            id:'btn-print',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/print.gif',
            handler:function(){
            	print_newCustomerList();
            }
        },'-',{
	            text:'关闭',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function () {
            		closeTab(parent.tab);
	            }
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
			title: '客户承接查询',
			contentEl:'search',
	     	width: 300,
	     	height:230,
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
          			goSearch_newCustomerList();
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
<div id="divprint">
<form name="thisForm" method="post" action="">
<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>

<div id="search" style="display:none;">
<br>
<br>
		<table border="0" cellpadding="0" cellspacing="0" bgcolor="" align="center">
			<tr>
               <td align="right">客户名称：</td>
               <td align=left>
                 <input type="text" id="customerName" name="customerName" >
               </td>
            </tr>
            <tr>
               <td align="right">客户承接人：</td>
               <td align=left>
                 <input type="text" id="publishUserId" name="publishUserId" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" onClick="onPopDivClick(this);" valuemustexist=true autoid=252>
               </td>
			</tr>
			  <tr>
               <td align="right">所属行业：</td>
               <td align=left>
                 <input type="text" id="belongsIndustry" name="belongsIndustry" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();" onClick="onPopDivClick(this);"  autoid=854>
               </td>
			</tr>
			 <tr>
               <td align="right">委托方：</td>
               <td align=left>
                 <input type="text" id="client" name="client" >
               </td>
			</tr>
			<tr>
               <td align="right">承接截止日期：</td>
               <td align=left>
                <input type="text"
					   name="deadlineDate"
					   id="deadlineDate" />
               </td>
			</tr>
		</table>
</div>	
</form>
</div>

<div>
<form name="thisForm1" method="post" action="">
<div id="divTab">
	<div id="noLaunch">
		<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>
		<div id="divBtn"></div>
		<div style="height:expression(document.body.clientHeight-55);overflow:auto;">
			<mt:DataGridPrintByBean name="newCustomerList"/>
		</div>
	</div>
</div>
<div id="isLaunch" style="overflow: visible;height: 550"></div>
</form>
</div>
<Script>

new Ext.form.DateField({			
	applyTo : 'deadlineDate',
	width: 133,
	format: 'Y-m-d'	
});



//清空 
function subClearSearch()
{
	document.thisForm.deadlineDate.value="";
	document.thisForm.publishUserId.value="";
	document.thisForm.belongsIndustry.value="";
	document.thisForm.client.value="";
	document.thisForm.customerName.value="";
}

function goInitPage()
{
	window.location="newCustomer.do";
}

// 添加 
function goAdd()
{
	window.location="${pageContext.request.contextPath}/newCustomer.do?method=addSkip";
}

//查看
function goLook()
{
	if(document.getElementById("chooseValue_newCustomerList").value=="")
	{
		alert("请选择一项！");
		return ;
	}
	else
	{
		window.location="newCustomer.do?method=look&uuid="+document.getElementById("chooseValue_newCustomerList").value;
	}
}


// 删除 
function goDelete(){
	if(document.getElementById("chooseValue_newCustomerList").value==""){
		alert("请选择一项！");
		return ;
	}
	else{
		if(getState()=="已发起"){
			alert("已发起审批，不能再进行删除!");
			return ;
		}
		if(confirm("您确定要删除此资料吗？删除后将无法恢复！")){
			window.location="newCustomer.do?method=del&uuid="+document.getElementById("chooseValue_newCustomerList").value;
		}	
	}
}


// 修改 
function goEdit()
{
	if(document.getElementById("chooseValue_newCustomerList").value=="")
	{
		alert("请选择一项！");
		return ;
	}
	else
	{
		if(getState()=="已发起"){
			alert("已发起审批，不能再进行修改!");
			return ;
		}
		window.location="newCustomer.do?method=updateSkip&uuid="+document.getElementById("chooseValue_newCustomerList").value+"&opt=update";
	}
}

function getState(){
	
	var uuid = document.getElementById("chooseValue_newCustomerList").value;
	
	if(uuid=="")
	{
		alert("请选择一项！");
		return ;
	}
	 var url="${pageContext.request.contextPath}/newCustomer.do?method=getState";
	 var requestString = "&uuid="+uuid;
	 var request= ajaxLoadPageSynch(url,requestString);
	 return request;
}

//双击
function grid_dblclick(obj) {
	window.location="newCustomer.do?method=viewNews&&autoId="+obj.autoId;
}

 
</script>
</body>
</html>


