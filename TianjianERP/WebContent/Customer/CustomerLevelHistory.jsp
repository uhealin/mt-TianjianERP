<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/calendar_include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>客户评级历史记录</title>

<script type="text/javascript">  

function ext_init(){
	
	var tbar_levellist = new Ext.Toolbar({
		renderTo: 'gridDiv_levellist',
		items:[{
			text:'新增', 
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/add.gif',
			handler:function () {
				addLevel();
			}
		},'-',{
            text:'查看',
            cls:'x-btn-text-icon',
            icon:'/AuditSystem/img/edit.gif',
           	handler:function(){
				checkLevel();
			}
        },'-',{
            text:'审批',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/confirm.gif',
            handler:function () {
            	passExamine();
            }
        },'-',{
			text:'打印',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/print.gif',
			handler:function () {
				print_levellist();
			}
		},'-',{
			text:'查询', 
			id:'btn-query',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:function () {
            	queryWinFun();
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
          			goSearch_levellist();
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
				applyTo : 'examineTime',
				width: 135,
				format: 'Y-m-d',
				emptyText: '' 
			});
	    }
		
    }
    new BlockDiv().show();
    queryWin.show();
}

function grid_dblclick(trObj,tableId) {

}
window.attachEvent('onload',ext_init);




</script>

</head>
<body >



<input type="hidden" name="customerid" id="customerid" value="${customerid}">


<div style="height:expression(document.body.clientHeight-23);" >
<mt:DataGridPrintByBean name="levellist"  />
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
		
		<td align="right">提交人：</td>
		<td align=left>
			<input type="text" name="recorder" id="recorder" />
		</td>
	</tr>
	<tr align="center">
		<td align="right">审批时间：</td>
		<td align=left>
			<input type="text" name="examineTime" id="examineTime"  class="validate-date-cn"/>
		</td>
	</tr>	
</table>
</div>

</body>
<script>
	
	//判断值情况方法
	function getEstate(autoid){
		var aJax = new ActiveXObject("Microsoft.XMLHTTP");
	    aJax.open("POST","${pageContext.request.contextPath}/customer.do?method=getestate&autoid="+autoid,false);
	    aJax.send();
	    
	    var result = aJax.responseText;

	    if(result=="yes"){   	
	    	alert("该记录已评审通过,不能重新评审!");    	
	    	return false;
	    }else{
	    	return true;
	    }
	}

	function passExamine(){
		var recode = document.getElementById("chooseValue_levellist").value;
		var trValue = document.getElementById("trValueId_" + recode);
	//	alert(recode+"|"+trValue);
		if(recode==""){
			alert("请选择要审批的评级记录！");
			return;
		}else{
			if(getEstate(trValue.recordtime)){
				if("${customerid}"==""){
					window.location = "${pageContext.request.contextPath}/customer.do?method=exitCustomerLevel&customerid="+trValue.recordtime+"&onlylook=false&toall=true";		
				}else{
					window.location = "${pageContext.request.contextPath}/customer.do?method=exitCustomerLevel&customerid="+trValue.recordtime+"&onlylook=false";				
				}			
			}				
		}
	}
	
	function addLevel(){	
		var customerid = document.getElementById("customerid").value;

		if(customerid==""){			
			window.location = "${pageContext.request.contextPath}/customer.do?method=setLevel&toall=true&customerid="+customerid;				
		}else{
			window.location = "${pageContext.request.contextPath}/customer.do?method=setLevel&customerid="+customerid;
		}		
	}
	
	
	function checkLevel(){
		var recode = document.getElementById("chooseValue_levellist").value;
		var trValue = document.getElementById("trValueId_" + recode);
		if(recode==""){
			alert("请选择要查看的评级记录！");
			return;
		}else{
			if("${customerid}"==""){
				window.location = "${pageContext.request.contextPath}/customer.do?method=exitCustomerLevel&customerid="+trValue.recordtime+"&onlylook=true&toall=true";		
			}else{
				window.location = "${pageContext.request.contextPath}/customer.do?method=exitCustomerLevel&customerid="+trValue.recordtime+"&onlylook=true";		
			}
		}		
	}
	
	function goSort(recode){
		myOpenUrlByWindowOpen("${pageContext.request.contextPath}/customer.do?method=exitCustomerLevel&customerid="+recode,"","");
	}
	
	function clearAll(){	
		if("${customerid}"==""){
			window.location = "${pageContext.request.contextPath}/customer.do?method=levelHistory";
		}else{
			window.location = "${pageContext.request.contextPath}/customer.do?method=levelHistory&customerid=${customerid}";
		}	
	}
	
</script>
</html>