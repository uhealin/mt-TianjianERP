<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>手机短信历史查询</title>
<script type="text/javascript">
 
function ext_init(){
	    var tbar = new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		defaults: {autoHeight: true,autoWidth:true},
            items:[{
			text:'查询',
			id:'btn-query',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/query.gif',
			handler:function(){
				queryWinFun();
			}
		}
			]
        });

    }
    
var queryWin = null;
function queryWinFun(id){
	var searchDiv = document.getElementById("search") ;
	searchDiv.style.display = "" ; 
	    queryWin = new Ext.Window({
			title: '查询',
			contentEl:'search',
	     	renderTo : searchWin,
	     	width: 400,
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
            	text:'确定',
          		handler:function(){
          			goSearch_smsList();
          			queryWin.hide();
            	}
        	},{ 
            	text:'取消',
            	handler:function(){
               		queryWin.hide();
            	}
        	}]
	    });
    new BlockDiv().show();
    queryWin.show();
}   


var queryWinMsg = null;
function queryWinMsgFun(id){
	 var userId = getChooseValue("userMessagerList");
	 if(userId == "" || userId == null)
	 {
		 alert("请选择人员");
		 return ;
	 }
	document.getElementById("userIds").value = userId;
	var searchDiv = document.getElementById("messagerDiv") ;
	searchDiv.style.display = "" ; 
	queryWinMsg = new Ext.Window({
			title: '发送短息',
			contentEl:'messagerDiv',
	     	//renderTo : messagerDiv,
	     	width: 400,
	     	height:230,
        	closeAction:'hide',
       	    listeners : {
	         	'hide':{
	         		fn: function () {
	         			new BlockDiv().hidden();
	         			queryWinMsg.hide();
					}
				}
	        },
        	layout:'fit',
	    	buttons:[{
            	text:'发送',
          		handler:function(){
          			goMsg();
          			queryWinMsg.hide();
            	}
        	},{ 
            	text:'取消',
            	handler:function(){
            		queryWinMsg.hide();
            	}
        	}]
	    });
    new BlockDiv().show();
    queryWinMsg.show();
}   
    
    
    
Ext.onReady(function(){
	ext_init();
});
</script>
</head>
<body>
<form name="thisForm" method="post" action="" >
<DIV ID="divBtn"></DIV>
 
<div style="height:expression(document.body.clientHeight-30);" >
		<mt:DataGridPrintByBean name="smsList"  />
 </div>
<input type="hidden" id="parentid" name="parentid" value="">
<input type="hidden" id="areaid" name="areaid" value="">

<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>
<div id="search" style="display:none;">
<br>
<div style="margin:0 20 0 20">请在下面输入查询条件：</div>
<div style="border-bottom:1px solid #AAAAAA; height: 1px;margin:0 20 4 20" ></div>
<table border="0" cellpadding="0" cellspacing="0" bgcolor="" width="100%" align="center">
	<tr>
      <td align="right">姓名：</td>
      <td align=left>
      	<input autoid=3035 type="text" id="jsUserId"  name="jsUserId"  onkeydown="onKeyDownEvent();" onkeyup="onKeyUpEvent();" onclick="onPopDivClick(this);" >
      </td>
	</tr>	
		<tr>
      <td align="right">发送日期：</td>
      <td align=left>
      	<input type="text" id="createDate"  name="createDate" >
      </td>
	</tr>			
</table>
</div>
	
	<div id="messagerDiv" style="display: none;">
		<table border="0" cellpadding="0" cellspacing="0" bgcolor="" width="100%" align="center">
			<tr>
		      <td colspan="2" style="height: 20px;">
					<input type="hidden" id="userIds" name="userIds">		      
		      </td>
			</tr>
			<tr>
			 <td align="right">短信内容：</td>
		      <td align="left">
		      	<textarea style="height: 80px;width: 280px;overflow: visible;" name="conter" id="conter"></textarea>
		      </td>
			</tr>	
			<tr>
			 <td>&nbsp;</td>
			  <td colspan="2" align="left" style="padding-top: 15px;">
		      	 注意：短信内容最多<font color="red">500</font>个汉字或<font color="red">1000</font>个纯英文！
		      </td>
			</tr>		
		</table>
		
	</div>

</form>

</body>
<script type="text/javascript">
new Ext.form.DateField({			
	applyTo : 'createDate',
	width: 133,
	format: 'Y-m-d'	
});
function goMsg()
{
	var userIds = document.getElementById("userIds").value;
	var conter = document.getElementById("conter").value;
	if(conter ==""){
		alert("请输入要发送的内容！");
		return ;
	}
	if(userIds !="" || userIds !=null || conter !=""){
		
	if(confirm("您确定要发送短信吗？","yes")){
		Ext.Ajax.request({
				method:'POST',
				params : { 
					userIds :userIds,
					conter:conter,
					rand :Math.random()
				},
				url:"${pageContext.request.contextPath}/user.do?method=userMessager",
				success:function (response,options) {
					  var request = response.responseText;
				 	alert(request);
				},
				failure:function (response,options) {
					alert("后台出现异常,获取文件信息失败!");
				}
			});
	}	
	}else{
			alert("请先选择人员");
			return;
	}
}
</script>
</html>
