<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>批量删除客户</title>
<script type="text/javascript">
var readyFun = function () {
	var tbar_customerMutiDelete = new Ext.Toolbar({
				renderTo:'gridDiv_customerMutiDelete',
	            items:[{
		            text:'删除选中',
		            cls:'x-btn-text-icon',
		            icon:'${pageContext.request.contextPath}/img/delete.gif',
		            handler:function(){
		            	goMutiDelete();
					}
	       		},'-',{
		            text:'查询',
		            id:'btn-query',
		            cls:'x-btn-text-icon',
		            icon:'${pageContext.request.contextPath}/img/query.gif',
		            handler:queryWinFun
	        },'-',{
		            text:'返回',
		            cls:'x-btn-text-icon',
		            icon:'${pageContext.request.contextPath}/img/back.gif',
		           	handler:function(){
						window.history.back();
					}
		       }]
	        }); 
} 

var queryWin = null;
	        
	function queryWinFun() {
		var searchDiv = document.getElementById("search") ;
		searchDiv.style.display = "" ;
		if(!queryWin) {
			var searchDiv = document.getElementById("search") ;
		    queryWin = new Ext.Window({
		     title: '客户批量删除查询',
		     contentEl:'search',
		     width: 455,
		     height:295,
		  	 modal:true,
		        closeAction:'hide',
		        listeners   : {
		        	'hide':{fn: function () {
		        		searchDiv.style.display = "none" ;
						queryWin.hide();	         	
		        	}}
		        },
		       layout:'fit',
		    buttons:[{
		           text:'确定',
		         	handler:function(){
		               	queryWin.hide();
		               	goSearch_customerMutiDelete();
		            
		           }
		       },{
		           text:'取消',
		           handler:function(){
		               queryWin.hide();
		           }
		       }]
		    });
	   }
	   queryWin.show();
	}

window.attachEvent("onload",readyFun);
</script>
</head>
<body leftmargin="0" topmargin="0">


<div id="search" style="display:none">
<br/><br/><br/>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
	<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="">
		<tr align="center">
			
			<td align="right">客户编号：</td>
			<td align=left><input  type="text" name="CustomerID" id="CustomerID" onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);"  class="validate-alpha" valuemustexist=true autoid=2/></td>
			</tr><tr>
			<td align="right">联系人：</td>
			<td align=left><input type="text" name="linkMan" id="linkMan"  value="" /></td>
			</tr>
			<tr>	
			<td align="right">联系电话：</td>
			<td align=left><input type="text" name="phoneNumber" id="phoneNumber" value="" /></td>
			</tr>
			<tr>
			<td align="right">会计制度类型：</td>
			<td align=left>
			<input  type="text" 
					name="inType" 
					value=""
					hideresult=true 
					onkeydown="onKeyDownEvent();" 
					onkeyup="onKeyUpEvent();" 
					onclick="onPopDivClick(this);" 
					valuemustexist=true 
					autoid=93 
					noinput=true>
			</td>
			
		</tr>
	 </tr>
       <tr>
      <td align="right"><div >所属集团单位：</div></td>
      <td align=left><input name="groupname" type="text" id="groupname" value="" onKeyDown="onKeyDownEvent();" onKeyUp="onKeyUpEvent();"  onClick="onPopDivClick(this);"   valuemustexist="true" autoid=614  maxlength="20">
      </td>
    </tr>
	
	</table>
</div>

<form name="thisForm" method="post" action="">
<div style="height:expression(document.body.clientHeight-29);">
<mt:DataGridPrintByBean name="customerMutiDelete" message="请选择单位编号" />
</div>
</form>



</body>
</html>
<script>


	
	function goClose() {
		if (top.location == self.location) {
			window.close();
		} else {
			window.history.back();
		}
	}
	
	function goMutiDelete() {
	
			var choose_customerMutiDelete = getChooseValue("customerMutiDelete");
			if(choose_customerMutiDelete == ""){
				alert("请选择要要删除的客户！");
				return ;
			}
			if(confirm("删除客户的同时也会客户下的帐套以及项目信息,你确定要删除吗?","提示")){
				document.thisForm.action = "${pageContext.request.contextPath}/customer.do?method=mutiDeleteCustomer&departIds="+choose_customerMutiDelete ;
				document.thisForm.submit() ;
			}
}
</script>
