<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
 
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>企业咨询</title>
 
<Script type="text/javascript">

function ext_init(){ 
	new Ext.Toolbar({
		renderTo: 'divBtn',
		items:[{
				text:'发送短信',
	            cls:'x-btn-text-icon',
	            icon:'${pageContext.request.contextPath}/img/mytask.gif',
				handler:function () {
					var uuids = getChooseValue("${DataGrid}");
	          		//alert(uuids);
	          		if(uuids == ""){
	          			alert("请选择要发短信的用户！");
	          			return;
	          		}
	          		document.getElementById("uuids").value = uuids;          		
	          		queryWinSms();	
				}
			},'-',{
				text:'归还登记',
				cls:'x-btn-text-icon',
				icon:'${pageContext.request.contextPath}/img/edit.gif',
	          	handler:function(){
	          		var uuids = getChooseValue("${DataGrid}");
	          		//alert(uuids);
	          		if(uuids == ""){
	          			alert("请选择要归还的证书！");
	          			return;
	          		}
	          		document.getElementById("uuids").value = uuids;          		
	          		queryWinFun();	
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

	mt_form_initDateSelect(document.getElementById("returntime"));

}


var queryWin = null; //归还
var querySms = null; //短信

function queryWinFun() {
	document.getElementById("search").style.display = "";
	if(queryWin == null) { 
	    queryWin = new Ext.Window({
			title: '归还登记',
			width: 470,
			height:170,
			contentEl:'search', 
	        closeAction:'hide',
	        modal:true,
	        listeners:{
				'hide':{fn: function () {
					 document.getElementById("search").style.display = "none";
				}}
			},
	        layout:'fit',
	        contentEl:'search',
		    buttons:[{
	            text:'确定',
	            id:'buton',
	            icon:'${pageContext.request.contextPath}/img/confirm.gif',
	          	handler:function() {
	          		if (!formSubmitCheck('search')) return;
	          		
	          		Ext.Ajax.request({
	    				method:'POST',
	    				params : { 
	    					uuids : document.getElementById("uuids").value,
		          			returntime : document.getElementById("returntime").value,
		          			returnstate : document.getElementById("returnstate").value										
	    				},
	    				url:MATECH_SYSTEM_WEB_ROOT + "/enterpriseQualification.do?method=borrowSave&rand="+Math.random(),
	    				success:function (response,options) {
	    					var result = response.responseText;
	    					if(result != ""){
								alert(result);
	    						goSearch_${DataGrid}(); 
	    		          		queryWin.hide();    						
	    						
	    					}
	    									
	    				},
	    				failure:function (response,options) {
	    					alert("后台出现异常,获取文件信息失败!");
	    				}
	    			});

	          		
	          	}
	        },{
	            text:'取消',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function(){
	            	queryWin.hide();
	            }
	        }]
	    });
	}

	queryWin.show();
}

function queryWinSms() {
	document.getElementById("searchSms").style.display = "";
	if(querySms == null) { 
	    querySms = new Ext.Window({
			title: '发送短信',
			width: 470,
			height:170,
			contentEl:'searchSms', 
	        closeAction:'hide',
	        modal:true,
	        listeners:{
				'hide':{fn: function () {
					 document.getElementById("searchSms").style.display = "none";
				}}
			},
	        layout:'fit',
		    buttons:[{
	            text:'确定',
	            id:'buton',
	            icon:'${pageContext.request.contextPath}/img/confirm.gif',
	          	handler:function() {
	          		if (!formSubmitCheck('searchSms')) return;
	          		
	          		Ext.Ajax.request({
	    				method:'POST',
	    				params : { 
	    					uuids : document.getElementById("uuids").value,
		          			returntime : document.getElementById("returntime").value,
		          			returnstate : document.getElementById("returnstate").value										
	    				},
	    				url:MATECH_SYSTEM_WEB_ROOT + "/enterpriseQualification.do?method=sendSms&rand="+Math.random(),
	    				success:function (response,options) {
	    					var result = response.responseText;
	    					if(result != ""){
								alert(result);
	    						goSearch_${DataGrid}(); 
	    		          		querySms.hide();    						
	    						
	    					}
	    									
	    				},
	    				failure:function (response,options) {
	    					alert("后台出现异常,获取文件信息失败!");
	    				}
	    			});

	          		
	          	}
	        },{
	            text:'取消',
	            icon:'${pageContext.request.contextPath}/img/close.gif',
	            handler:function(){
	            	querySms.hide();
	            }
	        }]
	    });
	}

	querySms.show();
}





Ext.onReady(function(){
	ext_init();
});

</script>
 
 
</head>
<body>

<div id="divBtn"></div>

<form id="thisForm" name="thisForm" method="post" action="">
<div style="height:expression(document.body.clientHeight-23);" >
<mt:DataGridPrintByBean name="${DataGrid}"  />
</div>

<input id="uuids" name="uuids" type="hidden" value="">
<div id="search" style="display: none;">
		<br/>
		<table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
			<tr>
				<td align="right">实际归还日期：</td>
				<td align=left>
					<input type="text" name="returntime" id="returntime" value="${returntime}" class="required"/>
				</td>
			</tr>
			<tr>
				<td align="right">归还时证书状态：</td>
				<td align=left>
					<textarea rows="5" cols="35" id="returnstate" name="returnstate" class="required"></textarea>
				</td>
			</tr>					
		</table>		
</div>

<div id="searchSms" style="display: none;">
		<br/>
		<table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
			<tr>
				<td align="right">短信消息：</td>
				<td align=left>
					<textarea rows="5" cols="35" id="smsvalue" name="smsvalue" class="required">${smsvalue}</textarea>
				</td>
			</tr>					
		</table>		
</div>
</form>

</body>
</html>

