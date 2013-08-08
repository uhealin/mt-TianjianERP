<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@ page import="com.matech.framework.pub.sys.*;"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>环境设置</title>

<script type="text/javascript">

Ext.onReady(function(){
	var tbar_customer = new Ext.Toolbar({
		renderTo:'divBtn',
           items:[{
            text:'修改',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/edit.gif',
            handler:function(){
            	goUpdate();
			}
      	},'-',{
           text:'刷新缓存',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/refresh.gif',
          	handler:function(){
				refreshCache();
			}
        },'-',{
            text:'打印',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/print.gif',
            handler:function () {
				print_CircumstanceList();
            }
        },'->'
        ]
        });  

});

</script>
</head>
<body>
<div id="divBtn"></div>
<div style="height:expression(document.body.clientHeight-27);">
<mt:DataGridPrintByBean name="CircumstanceList" outputData="true" outputType="invokeSearch" />
</div>
</body>

<script>
function goUpdate(){
	var choose_circumstance = document.getElementById("chooseValue_CircumstanceList").value;

	if(choose_circumstance == ""){
		alert("请选择要修改的记录！");
	} else {
		window.location="${pageContext.request.contextPath}/circumstance.do?method=exitcircumstance&autoid=" + choose_circumstance;
	}
}

//---------------------------
// 刷新缓存
//---------------------------
function refreshCache() {
	var url = "${pageContext.request.contextPath}/circumstance.do?method=refresh";
	ajaxLoadPageSynch(url,null);
	alert('刷新成功');
}
</script>
</html>