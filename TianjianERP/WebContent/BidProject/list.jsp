<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/Validate_include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
 
<%@ taglib prefix="mt" uri="http://www.matech.cn/tag" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>招投标信息</title>
 
<Script type="text/javascript">
var queryWinTake =null;
var tabs="";
Ext.onReady(function(){
<c:if test="${opt=='audit'}">
	 tabs = new Ext.TabPanel({
		    renderTo: 'my-tab',
		    activeTab: 0,
		    layoutOnTabChange:true, 
		    forceLayout : true,
		    deferredRender:false,
		    height: document.body.clientHeight-Ext.get('my-tab').getTop(),
		    width : document.body.clientWidth, 
		    defaults: {autoWidth:true,autoHeight:true},
		    items:[
		        {contentEl:'tab1', title:'未审核', id:'cur1'},
		        {contentEl:'tab2', title:'已审核', id:'cur2'}
		    ]
		});
		
		tabs.on("tabchange",function(tabpanel,tab){
	    	if(tab.id == "cur2"){
				goSearch_bidProjectAlreadyList();			
			}
	    });

		goSearch_bidProjectList();
	</c:if>
	
	var tbar = new Ext.Toolbar({
		renderTo: 'divBtn',
		items:[
		<c:if test="${opt=='' || opt=='add' || opt=='update' }">
		{
			text:'新增',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/add.gif',
			handler:function () {
				goAdd();
			}
		},'-',
		{
           text:'修改',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/edit.gif',
          	handler:function(){
				goEdit();
			}
        },'-'/*,
		{
            text:'<c:choose><c:when test="${gsName ==\'安联\'}">发起立项申请</c:when><c:otherwise>发起客户承接登记</c:otherwise></c:choose>',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/start.png',
           	handler:function(){
           		<c:choose>
	           		<c:when test="${gsName =='安联'}">
	           		if("${isGetBidProject}" =='否'){
           			 	if(document.getElementById("chooseValue_bidProjectList").value==""){
							alert("请选择一条数据！");
							return ;
						}else{
		          	     parent.openTab("customerProjectId","客户立项申请","bidProject.do?method=goProjectApproval&id="+document.getElementById("chooseValue_bidProjectList").value);
						}
					}
					else{
						alert("中标状态为否，不可以发起立项申请！");
						return ;
					}
	           		</c:when>
	           		<c:otherwise>
	           			goStartCustomer();
	           		</c:otherwise>
           		</c:choose>
 				
 			}
         },'-'*/,
        </c:if>
        <c:if test="${opt=='finish'}">
         {
           text:'完善招投标结果',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/edit.gif',
          	handler:function(){
				goEditCompetitor();
			}
        },'-',
        </c:if>
		<c:if test="${opt=='uploadFile'}">
        {
           text:'上传标书',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/edit.gif',
          	handler:function(){
				goAddAttach();
			}
        },'-',
        </c:if> 
        <c:if test="${opt=='audit'}">
        {
           text:'审核',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/edit.gif',
           handler:function(){
				goAudit();
			}
        },'-',
        </c:if>
        {
			text:'查询',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:queryWinFun 
		},'-',
		<c:if test="${opt=='' || opt=='add' || opt=='update' || opt=='finish'}">
		{
			text:'作废',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/delete.gif',
			handler:f_cancle
		},'-',
		{
			text:'删除',
			id:'del',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/delete.gif',
			handler:f_delete
		},'-',
		</c:if>
		 {
				text:'查看',
				cls:'x-btn-text-icon',
				icon:'${pageContext.request.contextPath}/img/query.gif',
				handler:function () {
					var autoid = document.getElementById("chooseValue_bidProjectList").value;
					if(autoid ==""){
						alert("请选择一项");
						return ;
					}
					window.location="${pageContext.request.contextPath}/bidProject.do?method=look&id="+autoid;
			}
			},'-',{
	            text:'打印',
	            id:'btn-print',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/print.gif',
	            handler:function(){
	            	print_bidProjectList();
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
	var tbar2 = new Ext.Toolbar({
	  renderTo:'divBtn2',
	  items:[{
		text:'查询',
		cls:'x-btn-text-icon',
		icon:'${pageContext.request.contextPath}/img/query.gif',
		handler:queryWinFuns
		},'-',
		 {
				text:'查看',
				cls:'x-btn-text-icon',
				icon:'${pageContext.request.contextPath}/img/query.gif',
				handler:function () {
					var autoid = document.getElementById("chooseValue_bidProjectAlreadyList").value;
					if(autoid ==""){
						alert("请选择一项");
						return ;
					}
					window.location="${pageContext.request.contextPath}/bidProject.do?method=look&id="+autoid;
			}
			},'-',
		{
	            text:'打印',
	            id:'btn-print',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/print.gif',
	            handler:function(){
	            	print_bidProjectAlreadyList();
	            }
	      },'-',
	      {
            text:'关闭',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/close.gif',
            handler:function () {
           		closeTab(parent.tab);
            }
	     },'->'
		]
	});

  //window.attachEvent('onload',ext_init);


})
function goStartCustomer(){
	var autoid = document.getElementById("chooseValue_bidProjectList").value;
	if(autoid ==""){
		alert("请选择一项");
		return ;
	}
	
	if(confirm("您确定要发起客户承接登记吗？","yes")){
		
		parent.openTab("startContinueApply","客户承接登记","bidProject.do?method=startCustomerContinue&id="+autoid);
	}
  }


</script>
 
 
</head>
<body>
<div id="my-tab">

	<div id="tab1">
		<div id="divBtn"></div> 
		<div style="height:expression(document.body.clientHeight-28);" >
			<mt:DataGridPrintByBean name="bidProjectList" />
		</div>
	</div> 
	<div id="tab2">
		<div id="divBtn2"></div> 
		<div style="height:expression(document.body.clientHeight-28);" >
			<mt:DataGridPrintByBean name="bidProjectAlreadyList" />
		</div>
	</div> 
</div>


<div id="divBtn"></div>

<form id="thisForm" name="thisForm" method="post" action="">
<div style="height:expression(document.body.clientHeight-23);" >
<mt:DataGridPrintByBean name="bidProjectList"  />
</div>


<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>

<div id="search" style="display:none">
<input type="hidden" name="cdate" id="cdate" type="text" size="20"  />
<input type="hidden" name="invoicenumber" id="invoicenumber" type="text" size="20"  />
<fieldset>
    <legend style="font-size:12px;">招投标查询</legend>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
		<tr align="center">
			<td align="right" >客户：</td>
			<td align="left" colspan="3">
				<input type="text"
				   name="departname"
				   id="departname"
				   maxlength="10"
				   title="请输入有效的值" 
				   size="20"  /> 
			</td>
		</tr>
		<tr align="center">
			<td align="right" >参加人：</td>
			<td align="left" colspan="3">
				<input name="bidMember" id="bidMember" type="text" size="20"  />
			</td>
		</tr>
		
		<tr align="center">
			<td align="right" >截止期限：</td>
			<td align="left" >
				<input name="endDate1" id="endDate1" type="text" size="20"  />
			</td>
			<td align="center" >至</td>
			<td align="left" >
				<input name="endDate2" id="endDate2" type="text" size="20"  />
			</td>
		</tr>
		<tr align="center">
			<td align="right" >创建日期：</td>
			<td align="left" >
				<input name="createDate1" id="createDate1" type="text" size="20"  />
			</td>
			<td align="center" >至</td>
			<td align="left" >
				<input name="createDate2" id="createDate2" type="text" size="20"  />
			</td>
		</tr>
		 
	</table>
</fieldset>
</div>

<input type="hidden" id="endDate" name="endDate">
<input type="hidden" id="createDate" name="createDate">
<input type="hidden" id="nowDate" name="nowDate" value="${nowDate}">




<!-- 

<div id="searchWin1" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>

<div id="search1" style="display:none">
<input type="hidden" name="cdate" id="cdate" type="text" size="20"  />
<input type="hidden" name="invoicenumber" id="invoicenumber" type="text" size="20"  />
<fieldset>
    <legend style="font-size:12px;">招投标查询</legend>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
		<tr align="center">
			<td align="right" >客户：</td>
			<td align="left" colspan="3">
				<input type="text"
				   name="departname"
				   id="departname"
				   maxlength="10"
				   title="请输入有效的值" 
				   size="20"  /> 
			</td>
		</tr>
		<tr align="center">
			<td align="right" >参加人：</td>
			<td align="left" colspan="3">
				<input name="bidMember" id="bidMember" type="text" size="20"  />
			</td>
		</tr>
		
		<tr align="center">
			<td align="right" >截止期限：</td>
			<td align="left" >
				<input name="endDate1" id="endDate1" type="text" size="20"  />
			</td>
			<td align="center" >至</td>
			<td align="left" >
				<input name="endDate2" id="endDate2" type="text" size="20"  />
			</td>
		</tr>
		<tr align="center">
			<td align="right" >创建日期：</td>
			<td align="left" >
				<input name="createDate1" id="createDate1" type="text" size="20"  />
			</td>
			<td align="center" >至</td>
			<td align="left" >
				<input name="createDate2" id="createDate2" type="text" size="20"  />
			</td>
		</tr>
		 
	</table>
</fieldset>
</div>

<input type="hidden" id="endDate" name="endDate">
<input type="hidden" id="createDate" name="createDate">
<input type="hidden" id="nowDate" name="nowDate" value="${nowDate}">




 -->



</form>

</body>
</html>


<script type="text/javascript">

function go_select()
{
   if(document.getElementById("ChooseItem").style.display=="none"){
            document.getElementById("ChooseItem").style.display="";
      }else{
            document.getElementById("ChooseItem").style.display="none";
      } 
}



function goSearch(){
	var endDate1 = document.getElementById("endDate1").value;
	var endDate2 = document.getElementById("endDate2").value;
	var createDate1 = document.getElementById("createDate1").value;
	var createDate2 = document.getElementById("createDate2").value;
	
	if(endDate1.trim()!=""){
		document.getElementById("endDate").value=" endDate <= '"+endDate1+"'";
	}
	if(endDate2.trim()!=""){
		document.getElementById("endDate").value=" endDate >= '"+endDate2+"'";
	}
	
	
	if(endDate1.trim()!="" && endDate2.trim()!=""){
		document.getElementById("endDate").value= " endDate between '"+endDate1+"' and '"+endDate2+"'"  ;
	}
	
	if(createDate1.trim()!=""){
		document.getElementById("createDate").value=" createDate <= '"+createDate1+"'";
	}
	if(createDate2.trim()!=""){
		document.getElementById("createDate").value=" createDate >= '"+createDate2+"'";
	}
	
	if(createDate1.trim()!="" && createDate2.trim()!=""){
		document.getElementById("createDate").value= " createDate between '"+createDate1+"' and '"+createDate2+"'"  ;
	}
	queryWin.hide();
    goSearch_bidProjectList(); 
} 




function goSearch1(){
	var endDate1 = document.getElementById("endDate1").value;
	var endDate2 = document.getElementById("endDate2").value;
	var createDate1 = document.getElementById("createDate1").value;
	var createDate2 = document.getElementById("createDate2").value;
	
	if(endDate1.trim()!=""){
		document.getElementById("endDate").value=" endDate <= '"+endDate1+"'";
	}
	if(endDate2.trim()!=""){
		document.getElementById("endDate").value=" endDate >= '"+endDate2+"'";
	}
	
	
	if(endDate1.trim()!="" && endDate2.trim()!=""){
		document.getElementById("endDate").value= " endDate between '"+endDate1+"' and '"+endDate2+"'"  ;
	}
	
	if(createDate1.trim()!=""){
		document.getElementById("createDate").value=" createDate <= '"+createDate1+"'";
	}
	if(createDate2.trim()!=""){
		document.getElementById("createDate").value=" createDate >= '"+createDate2+"'";
	}
	
	if(createDate1.trim()!="" && createDate2.trim()!=""){
		document.getElementById("createDate").value= " createDate between '"+createDate1+"' and '"+createDate2+"'"  ;
	}
	queryWin.hide();
    goSearch_bidProjectAlreadyList(); 
} 












// 清空
function goClear(){
	document.getElementById("departname").value="";
	document.getElementById("bidMember").value="";
	document.getElementById("endDate1").value="";
	document.getElementById("endDate2").value="";
	document.getElementById("createDate1").value="";
	document.getElementById("createDate2").value="";
	
	queryWin.hide();
    goSearch_bidProjectList(); 
    
}

// 添加
function goAdd(){
	//document.getElementById("thisForm").action = "${pageContext.request.contextPath}/bidProject.do?method=go&opt=add"; 
	document.getElementById("thisForm").action = "${pageContext.request.contextPath}/bidProject.do?method=addAndEdit&opt=add"; 
	document.getElementById("thisForm").submit();
}

//编辑
function goEdit(){
	var id = document.getElementById("chooseValue_bidProjectList").value;
	if(id==""){
		alert("请选择一项！");
	}else{
		var rs = getAuditStatus(id);
		if(rs=="N"){
			document.getElementById("thisForm").action = "${pageContext.request.contextPath}/bidProject.do?method=addAndEdit&opt=update&id="+id; 
			document.getElementById("thisForm").submit();
		}else{
			alert("已经审核通过，不能再修改！");
		}
	}
}



//加 竞争对手
function goEditCompetitor(){
	var id = document.getElementById("chooseValue_bidProjectList").value;
	if(id==""){
		alert("请选择一项！");
	}else{
		document.getElementById("thisForm").action = "${pageContext.request.contextPath}/bidProject.do?method=goAddCompetitor&id="+id; 
		document.getElementById("thisForm").submit();
	}
}

// 写标书人 上传标书
function goAddAttach(){
	var id = document.getElementById("chooseValue_bidProjectList").value;
	if(id==""){
		alert("请选择一项！");
	}else{
		var eDate = getEndDate(id);
		var nDate = document.getElementById("nowDate").value;

		//if(eDate>nDate){
		//	var rs = getAuditStatus(id);
		//	if(rs=="N"){
				document.getElementById("thisForm").action = "${pageContext.request.contextPath}/bidProject.do?method=goAddAttach&id="+id; 
				document.getElementById("thisForm").submit();
		//	}else{
		//		alert("已经审核通过，不能再上传标书！");
		//	}
		//}else{
		//	alert("招投标截止日期已过，不能再上传标书！");
		//}
	}
}

// 审核
function goAudit(){
	var id = document.getElementById("chooseValue_bidProjectList").value;
	if(id==""){
		alert("请选择一项！");
	}else{
		document.getElementById("thisForm").action = "${pageContext.request.contextPath}/bidProject.do?method=goAudit&id="+id; 
		document.getElementById("thisForm").submit();
	}
}







// 条件查询2
var queryWin = null;
function queryWinFuns(id){
	var searchDiv = document.getElementById("search") ;
	searchDiv.style.display = "" ;
	if(!queryWin) { 
	    queryWin = new Ext.Window({
			title: '招投标查询',
			contentEl:'search',
	     	renderTo : searchWin,
	     	width: 620,
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
          			goSearch1();
            	}
        	},{
            	text:'清空',
          		handler:function(){  
          			goClear();
            	}
        	},{
            	text:'取消',
            	handler:function(){
               		queryWin.hide();
            	}
        	}]
	    });
	    
	    

	    
	    var endDate1 = new Ext.form.DateField({
			applyTo : 'endDate1',
			width: 135,
			format: 'Y-m-d',
			emptyText: '' 
		});
	    var endDate2 = new Ext.form.DateField({
			applyTo : 'endDate2',
			width: 135,
			format: 'Y-m-d', 
			emptyText: '' 
		});
		
	    var createDate1 = new Ext.form.DateField({
			applyTo : 'createDate1',
			width: 135,
			format: 'Y-m-d',
			emptyText: '' 
		});
	    var createDate2 = new Ext.form.DateField({
			applyTo : 'createDate2',
			width: 135,
			format: 'Y-m-d', 
			emptyText: '' 
		});
	    
    }
    new BlockDiv().show();
    queryWin.show();
}













// 条件查询
var queryWin = null;
function queryWinFun(id){
	var searchDiv = document.getElementById("search") ;
	searchDiv.style.display = "" ;
	if(!queryWin) { 
	    queryWin = new Ext.Window({
			title: '招投标查询',
			contentEl:'search',
	     	renderTo : searchWin,
	     	width: 620,
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
          			goSearch();
            	}
        	},{
            	text:'清空',
          		handler:function(){  
          			goClear();
            	}
        	},{
            	text:'取消',
            	handler:function(){
               		queryWin.hide();
            	}
        	}]
	    });
	    
	    

	    
	    var endDate1 = new Ext.form.DateField({
			applyTo : 'endDate1',
			width: 135,
			format: 'Y-m-d',
			emptyText: '' 
		});
	    var endDate2 = new Ext.form.DateField({
			applyTo : 'endDate2',
			width: 135,
			format: 'Y-m-d', 
			emptyText: '' 
		});
		
	    var createDate1 = new Ext.form.DateField({
			applyTo : 'createDate1',
			width: 135,
			format: 'Y-m-d',
			emptyText: '' 
		});
	    var createDate2 = new Ext.form.DateField({
			applyTo : 'createDate2',
			width: 135,
			format: 'Y-m-d', 
			emptyText: '' 
		});
	    
    }
    new BlockDiv().show();
    queryWin.show();
}




// 作废 
function f_cancle(){
	var id = document.getElementById("chooseValue_bidProjectList").value;
	if(id==""){
		alert("请选择一项！");
	}else{
		var rs = getAuditStatus(id);
		if(rs=="N"){
			var oBao = new ActiveXObject("Microsoft.XMLHTTP");
			var url="bidProject.do?method=cancle&opt=seeStatus&id="+id ;
			oBao.open("POST",url,false);
			oBao.send();
			var resText = oBao.responseText ; 
			if(resText=="0"){
				alert("该标已是废标状态！"); 
			}else{
				if(confirm("是否确定作废？")){
					var oBao = new ActiveXObject("Microsoft.XMLHTTP");
					var url="bidProject.do?method=cancle&opt=updateStatus&id="+id;
					oBao.open("POST",url,false);
					oBao.send();
					var resText2 = oBao.responseText;
					if(resText2=="1"){
						alert("作废成功！");
						// 刷新记录
						goSearch_bidProjectList(); 
					}else{
						alert("作废失败！");
					}
				}
			}
		}else{
			alert("已经审核通过，不能作废了！");
		}
	}
}

 
function grid_dblclick(obj){
	
}


// 删除
function f_delete(){
	var id = document.getElementById("chooseValue_bidProjectList").value;
	if(id==""){
		alert("请选择一项！");
	}else{
		var rs = getAuditStatus(id);
		if(rs=="N"){
			if(confirm("是否确定删除该招投标信息吗？","提示")){
				window.location="bidProject.do?method=delete&&id="+id;
			}
		}else{
			alert("已经审核通过，不能删除！");
		}
	}
}

// 得到状态
function getAuditStatus(id){
	var url = "${pageContext.request.contextPath}/bidProject.do?method=getAuditStatus";
	var requestString = "id=" + id;
	var result = ajaxLoadPageSynch(url,requestString);
	return result;
}

// 得到 endDate
function getEndDate(id){
	var url = "${pageContext.request.contextPath}/bidProject.do?method=getEndDate";
	var requestString = "id=" + id;
	var result = ajaxLoadPageSynch(url,requestString);
	return result;
}

</Script>


