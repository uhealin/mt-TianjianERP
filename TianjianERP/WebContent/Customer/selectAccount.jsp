<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>	
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp" %>
<%@page import="com.matech.framework.pub.util.ASFuntion"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>从账套选择</title>
<script>
  function onSelect(){
  	
  	if(document.getElementById("AccPackageID").value == ""){
  		alert("请先完成账套编号！");
  		return false;
  	}
  	document.thisForm.action = "/AuditSystem/selectFromAccount.do?method=select&customerid="+document.getElementById("CustomerID").value+"&menuid="+document.getElementById("menuid").value;
  	document.thisForm.submit();
  	  	
  }
  
  function goSelectOrDelete(flag){
  		var choose = getChooseValue("SelectFromAccount");
  		if(choose == "" ) {
  			alert("请选择要设置的记录！");
  			return ;
  		}
  		document.getElementById("customerName").value = choose ;
  		if(flag == "save") {
  			document.thisForm.action = "selectFromAccount.do?method=saveRelateCustomer&customerid="+document.getElementById("CustomerID").value ;
  		}else if(flag == "delete") {
  			document.thisForm.action = "selectFromAccount.do?method=deleteRelateCustomer&customerid="+document.getElementById("CustomerID").value ;
  		}
  		document.thisForm.submit() ;
  }
  
  function selectOrNot(obj){
  	if(obj.checked){
  		obj.value = obj.parentElement.parentElement.childNodes(3).innerText;
  		obj.parentElement.parentElement.lastChild.innerText = "[是关联客户]";
  		var oBao = new ActiveXObject("Microsoft.XMLHTTP");
  		var url = "selectFromAccount.do?method=saveByAJAX";
  		var request = "&customerName=" + obj.value + "&customerid="+document.getElementById("CustomerID").value + "&t="+new Date().getTime();
		if(ajaxLoadPageSynch(url,request)!="ok") {
			alert("保存出错");
		}
  	}
  	else{
  		var tempName = obj.parentElement.parentElement.childNodes(3).innerText;
  		obj.value = "";
  		obj.parentElement.parentElement.lastChild.innerText = "";
  		var url = "selectFromAccount.do?method=deleteByAJAX";
  		var request = "&customerName=" + tempName + "&customerid="+document.getElementById("CustomerID").value + "&t="+new Date().getTime();
		if(ajaxLoadPageSynch(url,request)!="ok") {
			alert("保存出错");
		}
  	}
  }
  
  function onSave(){
  	var AccID = document.getElementById("AccID").value;
  	var menuid = document.getElementById("menuid").value;
  	if(menuid == ""){
  		window.opener.document.location.reload();
  	}
  	document.thisForm.action = "/AuditSystem/selectFromAccount.do?method=save&AccPackageID="+AccID+"&menuid="+menuid;
  	document.thisForm.submit();
  }
  
  function goSort(){

	var obj=getTR();
	
	var AssItemID = obj.childNodes(2).innerText;
	
	var url;
	
	if(AssItemID == ""){
		url = "account.do?method=list&AccPackageID="+document.getElementById("AccID").value+"&subjectID="+obj.childNodes(1).innerText;
	}
	else{
		url = "assitementryacc.do?AccPackageID="+document.getElementById("AccID").value+"&SubjectID2="+obj.childNodes(1).innerText+"&txtType=1";
	}

	window.open(url,"_blank","resizable=yes,toolbar=no,menubar=no,titlebar=no");	
  }
  
  function ext_init(){
        
	    var tbar = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		defaults: {autoHeight: true,autoWidth:true},
            items:[{
	            text:'设置关联客户',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/save.gif',
	            handler:function(){
	            	goSelectOrDelete("save") ;
				}  
       		},{
	            text:'取消关联客户',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/delete.gif',
	            handler:function(){
	            	goSelectOrDelete("delete") ;
				}  
       		},{
	            text:'查询',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/query.gif',
	            handler:function(){
	            	queryWinFun();
				}  
       		},{
        	text:'关闭',
			icon:'${pageContext.request.contextPath}/img/close.gif',
           	handler:function(){
           		closeTab(parent.tab);
			}
        },'->'
			]
        });
        
    }
    window.attachEvent('onload',ext_init);
    
    
    var queryWin = null;
	        
	function queryWinFun() {
		if(!queryWin) {
			var searchDiv = document.getElementById("query") ;
		    queryWin = new Ext.Window({
		     title: '关联单位查询',
		     renderTo :'searchWin',
		     width: 455,
		     height:295,
		  	 //modal:true,
		        closeAction:'hide',
		        listeners   : {
		        	'hide':{fn: function () {
					new BlockDiv().hidden();
					queryWin.hide();	         	
		        	}}
		        },
		       layout:'fit',
			  html:searchDiv.innerHTML,
		    buttons:[{
		           text:'确定',
		         	handler:function(){
		         		onSelect();
		         	//	onSearch();
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

<form method="post" id="thisForm" name="thisForm">
<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>
<input type="hidden" name="AccID" id="AccID" value="${requestScope.AccPackageID}">
<input type="hidden" name="condition" id="condition" value="${requestScope.search}">
<input type="hidden" name="menuid" id="menuid" value="<%=new ASFuntion().showNull(request.getParameter("menuid")) %>">
<input type="hidden" name="customerName" id="customerName" value="">

<c:set var="menuid" value="${param.menuid}" />
<c:if test="${requestScope.action != 'selected'}"><script>window.attachEvent('onload',queryWinFun); </script></c:if>	
<c:if test="${requestScope.action == 'selected'}">
<div style="height:expression(document.body.clientHeight-27);">
<mt:DataGridPrintByBean name="SelectFromAccount" outputData="true" outputType="invokeSearch"/>
</div>
</c:if>

</form>

</body>
<div id="query" style="display:none;">
<br><br><br>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
	<table border="0" width="100%"  align="center">
		<tr>
			<td align="right">单位编号：</td>
			<td>
				<c:choose>
					<c:when test="${requestScope.lock == '1'}">
						<input type="text" 
								name="CustomerID" 
								id="CustomerID" 
								size="10" 
								onkeydown="onKeyDownEvent();" 
								onkeyup="onKeyUpEvent();" 
								onclick="onPopDivClick(this);" 
								valuemustexist=true 
								autoid=2 
								noinput="true" 
								class="required"
								disabled="disabled"
								value="${requestScope.CustomerID}" 
								refreshtarget="AccPackageID" title="单位编号" />
					</c:when>
				
					<c:otherwise>
						<input type="text" 
								name="CustomerID" 
								id="CustomerID" 
								size="10" 
								onkeydown="onKeyDownEvent();" 
								onkeyup="onKeyUpEvent();" 
								onclick="onPopDivClick(this);" 
								valuemustexist=true 
								noinput="true" 
								autoid=2 
								class="required"
								value="${CustomerID}" 
								title="单位编号" />
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<tr>
			<td align="right">账套编号：</td>
			<td>
				<input type="text" 
						name="AccPackageID" 
						id="AccPackageID"
						autoid="230"
						onfocus="onPopDivClick(this);"
						onkeydown="onKeyDownEvent();"
						onkeyup="onKeyUpEvent();"
						onclick="onPopDivClick(this);"
						norestorehint=true
						value="${requestScope.AccPackageID}" 
						noinput=true
						class="required"
						refer="CustomerID"
						hideresult=true>
			</td>
		
				<c:choose>
					<c:when test="${requestScope.action == 'selected'}">
						<tr>
						<td align="right">科目名称：</td>
							<td><input type="text" name="search" id="search" class="required"></td>
							</tr>
						</c:when>
				</c:choose>
	</table>
	</div>
	
<script>
	new Validation("thisForm");
	if(document.getElementById("AccPackageID").value!='' && ${myselect!='myselect'}){
		onSelect();
	}
</script>
</html>

