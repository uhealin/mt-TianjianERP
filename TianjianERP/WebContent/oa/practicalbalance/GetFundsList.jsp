<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/Validate_include.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
 
<%@ taglib prefix="mt" uri="http://www.matech.cn/tag" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>收款信息</title>
 

<script type="text/javascript">


function ext_init(){ 
	var loginid = document.getElementById("loginid").value;
	if(loginid=='admin'){
		var tbar_policy = new Ext.Toolbar({
			renderTo: 'divBtn',
			items:[{
				text:'新增',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/add.gif',
				handler:function () {
					window.self.location='/AuditSystem/oa/practicalbalance/GetFundsEdit.jsp';
				}
			},'-',{
	           text:'修改',
	           cls:'x-btn-text-icon',
	           icon:'${pageContext.request.contextPath}/img/edit.gif',
	          	handler:function(){
					goEdit();
				}
	        },'-',{
				text:'打印',
				cls:'x-btn-text-icon',
				icon:'${pageContext.request.contextPath}/img/print.gif',
				handler:function () {
					goPrint();
				}
			},'-',{
				text:'查询',
				cls:'x-btn-text-icon',
				icon:'${pageContext.request.contextPath}/img/query.gif',
				handler:queryWinFun 
			},'-',{
				text:'作废',
				cls:'x-btn-text-icon',
				icon:'${pageContext.request.contextPath}/img/delete.gif',
				handler:updateMoney
			},'-',{
					text:'删除',
					id:'del',
					cls:'x-btn-text-icon',
					icon:'${pageContext.request.contextPath}/img/delete.gif',
					handler:f_delete
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
	   }else{
		   var tbar_policy = new Ext.Toolbar({
				renderTo: 'divBtn',
				items:[{
					text:'新增',
		            cls:'x-btn-text-icon',
		            icon:'${pageContext.request.contextPath}/img/add.gif',
					handler:function () {
						window.self.location='/AuditSystem/oa/practicalbalance/GetFundsEdit.jsp';
					}
				},'-',{
		           text:'修改',
		           cls:'x-btn-text-icon',
		           icon:'${pageContext.request.contextPath}/img/edit.gif',
		          	handler:function(){
						goEdit();
					}
		        },'-',{
					text:'打印',
					cls:'x-btn-text-icon',
					icon:'${pageContext.request.contextPath}/img/print.gif',
					handler:function () {
						goPrint();
					}
				},'-',{
					text:'查询',
					cls:'x-btn-text-icon',
					icon:'${pageContext.request.contextPath}/img/query.gif',
					handler:queryWinFun 
				},'-',{
					text:'作废',
					cls:'x-btn-text-icon',
					icon:'${pageContext.request.contextPath}/img/delete.gif',
					handler:updateMoney
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
		   
	   }

}
window.attachEvent('onload',ext_init);

</script>

</head>
 
<body>

<div id="divBtn"></div>

 
	
<form name="thisForm" method="post" action="">
<input type="hidden" id="loginid" name="loginid" value="${userSession.userLoginId}">
<div style="height:expression(document.body.clientHeight-23);" >
<mt:DataGridPrintByBean name="GetFundsList" outputData="true" outputType="invokeSearch" />
</div>

<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>
  
<div id="nav" style=" display:none;">

<iframe src="direct.jsp" style="width: 100%; height:100%; padding: 0px;margin: 0px;" frameborder="0" scrolling="no"></iframe>

</div>


<div id="search" style="display:none">
<input type="hidden" name="receicedate" id="receicedate" type="text" size="20"  />
<input type="hidden" name="receiceMoney" id="receiceMoney" type="text" size="20"  />
<fieldset>
    <legend style="font-size:12px;">收款</legend>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
		<tr align="center">
			<td align="right" >项目名称：</td>
			<td align="left">
				<input type="text"
				   name="projectid"
				   id="projectid"
				   maxlength="10"
				   title="请输入有效的值" 
				   size="20"  /> 
			</td>
			<td align="right" >凭证号：</td>
			<td align="left" colspan="3">
				<input type="text"
				   name="ctypenumber"
				   id="ctypenumber"
				   maxlength="10"
				   title="请输入有效的值" 
				   size="20"  /> 
			</td>
		</tr>
		<tr align="center">
			<td align="right" >付款单位：</td>
			<td align="left">
				<input name="customerId" id="customerId" type="text" size="20"
					   onkeydown="onKeyDownEvent();"
					   onkeyup="onKeyUpEvent();"
					   onclick="onPopDivClick(this);"
					   valuemustexist=true
					   autoid=2  />
			</td>
			<td align="right" >账面分类：</td>
			<td align="left" >
				<input name="accounttype" id="accounttype" type="text" size="20" 
						   onKeyDown="onKeyDownEvent();" 
					       onKeyUp="onKeyUpEvent();" 
					       onClick="onPopDivClick(this);" 
					       valuemustexist=true 
					        autoid=4574  />
			</td>
		</tr>
		<tr align="center">
			<td align="right" >收款金额：</td>
			<td align="left" >
				<input name="receiceMoney1" id="receiceMoney1" type="text" size="20"  />
			</td>
			<td align="center" >至</td>
			<td align="left" >
				<input name="receiceMoney2" id="receiceMoney2" type="text" size="20"  />
			</td>
		</tr>
		<tr align="center">
			<td align="right" >收款日期：</td>
			<td align="left" >
				<input name="receicedate1" id="receicedate1" type="text" size="20"  />
			</td>
			<td align="center" >至</td>
			<td align="left" >
				<input name="receicedate2" id="receicedate2" type="text" size="20"  />
			</td>
		</tr>
		<tr align="center">
			<td align="right" >收款形式：</td>
			<td align="left">
				<input type="text"
				   name="ctype"
				   id="ctype"
				   maxlength="10"
				   title="请输入有效的值"
				   onkeydown="onKeyDownEvent();"
				   onkeyup="onKeyUpEvent();"
				   onclick="onPopDivClick(this);"
				   size="20"
				   autoid=4573 /> 
			</td>
			<td align="right" >委托号：</td>
			<td align="left" >
				<input name="entrustNumber" id="entrustNumber" type="text" size="20"  />
			</td>
		</tr>
		<tr align="center">
			<td align="right" >报告号：</td>
			<td align="left" >
				<input name="reportNumber" id="reportNumber" type="text" size="20"  />
			</td>
			<td align="right" >承接部门：</td>
			<td align="left" >
				<input name="departname" id="departname" type="text" size="20" 
						   onKeyDown="onKeyDownEvent();" 
					       onKeyUp="onKeyUpEvent();" 
					       onClick="onPopDivClick(this);" 
					       valuemustexist=true 
					       autoid=123  />
			</td>
		</tr>
		 
	</table>
</fieldset>
</div>
</form>
</body>
</html>

<Script type="text/javascript">

function go_select()
{
   if(document.getElementById("ChooseItem").style.display=="none"){
            document.getElementById("ChooseItem").style.display="";
      }else{
            document.getElementById("ChooseItem").style.display="none";
      } 
}


function goSearch(){
	document.getElementById("receicedate").value="";
	document.getElementById("receiceMoney").value="";
	var receicedate1 = document.getElementById("receicedate1").value;
	var receicedate2 = document.getElementById("receicedate2").value;
	var receiceMoney1 = document.getElementById("receiceMoney1").value;
	var receiceMoney2 = document.getElementById("receiceMoney2").value;
	if(receicedate1!="" && receicedate2!=""){
		document.getElementById("receicedate").value=receicedate1+"!"+receicedate2;
	}
	if(receiceMoney1!="" && receiceMoney2!=""){
		document.getElementById("receiceMoney").value=receiceMoney1+"!"+receiceMoney2;
	}
	queryWin.hide();
    goSearch_GetFundsList(); 
    
    document.body.onselectstart = function () {} 
	document.body.unselectable="off" ;
} 


function goPrint(){
	try{
		var printSQL = document.getElementById("printSql_GetFundsList").value;
		if(printSQL==null||printSQL==""){
			alert("请先进行查询，再使用本功能！");
		}else{		
			print_GetFundsList();
		}
	}catch(e){
		alert("请先进行查询，再使用本功能！");
	}			
}

//编辑
function goEdit(){
	if(document.getElementById("chooseValue_GetFundsList").value=="")
	{
		alert("请选择一项！");
	}
	else
	{
		window.location="getFunds.do?method=getGetFundsById&&autoid="+document.getElementById("chooseValue_GetFundsList").value;
	}
}



// 条件查询
var queryWin = null;
function queryWinFun(id){
	var searchDiv = document.getElementById("search") ;
	searchDiv.style.display = "" ;
	if(!queryWin) { 
	    queryWin = new Ext.Window({
			title: '菜单查询',
			contentEl:'search',
	     	renderTo : searchWin,
	     	width: 600,
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
          			goSearch();
            	}
        	},{
            	text:'清空',
          		handler:function(){  
          			window.location="getFunds.do";
            	}
        	},{
            	text:'取消',
            	handler:function(){
               		queryWin.hide();
            	}
        	}]
	    });
	    
	    var cdate1 = new Ext.form.DateField({
			applyTo : 'receicedate1',
			width: 135,
			format: 'Y-m-d',
			emptyText: '' 
		});
	    var cdate2 = new Ext.form.DateField({
			applyTo : 'receicedate2',
			width: 135,
			format: 'Y-m-d', 
			emptyText: '' 
		});
	    
    }
    new BlockDiv().show();
    queryWin.show();
}





// 作废 
function updateMoney(){
	if(document.getElementById("chooseValue_GetFundsList").value=="")
	{
		alert("请选择一项！");
	}
	else
	{
		var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		var url="getFunds.do?method=seeMoney&autoid="+document.getElementById("chooseValue_GetFundsList").value ;
		oBao.open("POST",url,false);
		oBao.send();
		var resText = oBao.responseText ; 
		if(resText*1 == 0){
			alert("该发票金额已为0！"); 
		}else{
			if(confirm("是否确定作废？","提示")){
				var oBao = new ActiveXObject("Microsoft.XMLHTTP");
				var url="getFunds.do?method=updateMoney&autoid="+document.getElementById("chooseValue_GetFundsList").value;
				oBao.open("POST",url,false);
				oBao.send();
				var resText2 = oBao.responseText;
				if(resText2=="Y"){
					alert("作废成功！");
				}else{
					alert("作废失败！");
				}
			}
		}
	}
	
}





// 修改发票状态
function goUpdateBillState(){
	if(document.getElementById("chooseValue_GetFundsList").value=="")
	{
		alert("请选择一项！");
	}
	else
	{
		var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		var url="getFunds.do?method=getInvoiceStateById&autoid="+document.getElementById("chooseValue_GetFundsList").value ;
		oBao.open("POST",url,false);
		oBao.send();
		var resText = oBao.responseText ; 
		if(resText == "作废"){
			alert("该发票状态已为作废了");
		}else{
			if(confirm("是否确定作废？","提示")){
				window.location="getFunds.do?method=excuteBill&&p=updateState&&state=end&&autoid="+document.getElementById("chooseValue_GetFundsList").value;
			}
		}
	}
}

// 启用发票
function goStartBillState(){
	if(document.getElementById("chooseValue_GetFundsList").value=="")
	{
		alert("请选择一项！");
	}
	else
	{
		var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		var url="getFunds.do?method=getInvoiceStateById&autoid="+document.getElementById("chooseValue_GetFundsList").value ;
		oBao.open("POST",url,false);
		oBao.send();
		var resText = oBao.responseText ; 
		if(resText == "启用中"){
			alert("该发票状态已为启用中");
		}else{
			if(confirm("是否确定启用该发票？","提示")){
				window.location="getFunds.do?method=excuteBill&&p=updateState&&state=start&&autoid="+document.getElementById("chooseValue_GetFundsList").value;
			}
		}
	}
}

function grid_dblclick(obj){
	
}


// 删除
function f_delete(){
	if(document.getElementById("chooseValue_GetFundsList").value==""){
		alert("请选择一项！");
	}else{
		if(confirm("是否确定删除该发票吗？","提示")){
			window.location="getFunds.do?method=deleteGetFunds&&autoid="+document.getElementById("chooseValue_GetFundsList").value;
		}
	}
}

Ext.onReady(function () {
	document.body.onselectstart = function () {} 
	document.body.unselectable="off" ;
})


</Script>
