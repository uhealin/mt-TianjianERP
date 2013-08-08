<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script type="text/javascript">
Ext.onReady(function(){
	var tbar_customer = new Ext.Toolbar({
		renderTo:'divBtn',
           items:[{
            text:'关闭',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/close.gif',
            handler:function(){
            	closeTab(parent.parent.tab);
			}
      	},'->'
        ]
    });  
});
</script>
</head>
<body>
<div id="divBtn"></div>
<div>
<form name="thisForm" method="post" action="">
<div id="searchWin" style="position:absolute;left:expression((document.body.clientWidth-250)/2);top:expression(this.offsetParent.scrollTop +200); z-index: 2"></div>
<div style="height:expression(document.body.clientHeight-30);overflow:auto;">
<mt:DataGridPrintByBean name="teacherHistory"/>
</div>
</form>
</div>
</body>
<script type="text/javascript">
function grid_dblclick(obj, tableId) {
	var id = obj.educationId;
	if(id ==""){
		return ;
	}
	openPage(id);
}
function openPage(id){
	var url = "evaluate.do?method=assessResultList&opt=2&id="+id+"&rand="+Math.random();
		var pageId = new Date().getTime();
		parent.openTab(pageId,"评价结果",url);
}
</script>
</html>