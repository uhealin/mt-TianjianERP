<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@page import="com.matech.framework.pub.sys.UTILSysProperty"%>
<%@page import="com.matech.framework.pub.util.ASFuntion"%>  
<%
	ASFuntion CHF = new ASFuntion() ;
	String svalue = CHF.showNull(UTILSysProperty.SysProperty.getProperty("系统应用事务所"));
%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<title>客户管理</title>
	<script type="text/javascript"><!--

function goEdit() {
	if(document.getElementById("chooseValue_managerList").value=="") {
		alert("请选择要分配的客户！");
	} else {
		window.location="customer.do?method=update&&act=update&&CustomerID="+document.getElementById("chooseValue_managerList").value;
	}
}
	function readyFun() {
		var tbar_customer = new Ext.Toolbar({
			renderTo:'gridDiv_managerList',
            items:[{
	            text:'分配',
	            cls:'x-btn-text-icon',
	            icon:'/AuditSystem/img/add.gif',
	            handler:function(){
	            	goEdit();
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
            	print_managerList();
            }
        }
<%if(!"立信".equals(svalue)){%>
	,{
    	text:'关闭',
		icon:'${pageContext.request.contextPath}/img/close.gif',
       	handler:function(){
       		closeTab(parent.tab);
		}
    }
<%	}%>	       		
       	
       	]
       });  
	}
	
	var queryWin = null;
	        
	function queryWinFun() {
		if(!queryWin) {
			var searchDiv = document.getElementById("search") ;
		    queryWin = new Ext.Window({
		     title: '客户查询',
		     renderTo :'searchWin',
		     width: 455,
		     height:250,
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
		               	queryWin.hide();
		               	goSearch_managerList();
		            
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
<body leftmargin="0" topmargin="0">
<form name="thisForm" method="post" action="" style="width:100%">
<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>

<div style="height:expression(document.body.clientHeight-27);width:100%">
<mt:DataGridPrintByBean name="managerList"/>
</div>
</form>


<div id="search" style="display:none">
<br/>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
		<tr align="center">
			<td align="right" width="30%" >客户名称：</td>
			<td align=left><input  type="text" name="CustomerID" id="CustomerID" /></td>
		</tr>
		<tr align="center">
			<td align="right">所属部门：</td>
			<td align=left><input name="departmentid" type="text" id="departmentid" value="" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();"  onClick="onPopDivClick(this);"   valuemustexist="true" autoid=123 noinput=true  maxlength="20"></td>
		</tr>
		<tr align="center">
			<td align="right">承做人：</td>
			<td align=left>
				<input  type="text" name="user" id="user" />
				<input  type="checkbox" name="user_no" id="user_no" value=1 onclick="if(this.checked){this.value=0;document.getElementById('user').value=''; }else {this.value = 1;}" />还无承做人
			</td>
		</tr>
		<tr align="center">
			<td align="right">所属集团：</td>
			<td align=left><input name="groupname" type="text" id="groupname" value="" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();"  onClick="onPopDivClick(this);"   valuemustexist="true" autoid=621  maxlength="20"></td>
		</tr>					
	</table>
</div>

<script type="text/javascript">
		Ext.onReady(readyFun);
		
</script>

</body>
</html>

