<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; utf-8">
<title>客户公司成立周年</title>
<script type="text/javascript">
Ext.onReady(function(){
  var btndiv = new Ext.Toolbar({
		renderTo: 'btndiv',
		items:[{
			text:'查看详情',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/start.png',
			handler:function(){
		      	var customer = document.getElementById("chooseValue_departDate").value;
				if(customer==""){
						alert("请选择您要查看的客户!");
				}else{
						window.location = "${pageContext.request.contextPath}/Customer/CustomerInformationFrame.jsp?customer="+customer+"&agent=agent";
				}	
		  }
		},'-',{
			text:'关闭',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/close.gif',
			handler:function () {
				closeTab(parent.tab);
			}
		}
	]
	});
	})
</script>
</head>
<body>
<div id="btndiv"></div>
<div style="height:expression(document.body.clientHeight-29);width:100%">

<mt:DataGridPrintByBean name="departDate"  message="请选择单位编号" />
</div>
</body>