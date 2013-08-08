<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; utf-8">
<title>部门员工生日</title>
<script type="text/javascript">
  Ext.onReady(function(){
  var btndiv = new Ext.Toolbar({
		renderTo: 'btndiv',
		items:[
		/*{
			text:'查看详情',
			cls:'x-btn-text-icon',
			icon:'${pageContext.request.contextPath}/img/start.png',
			handler:function(){
			   	if(document.getElementById("chooseValue_birthToDepart").value==""){
					alert("请选择要查看的对象！");
				}else{
					window.location = "${pageContext.request.contextPath}/oa/UserInformationFrame.jsp?myUserid="+document.getElementById("chooseValue_birthToDepart").value+"&judge=${judge}&agent=agent";
				}
			}
		},'-',
		*/{
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

<mt:DataGridPrintByBean name="birthToDepart"  message="请选择单位编号" />
</div>
</body>