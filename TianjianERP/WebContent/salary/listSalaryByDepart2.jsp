<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>规章制度列表</title>
<script src="<%=request.getContextPath()%>/AS_INCLUDE/searchSession.js" charset=gbk></script>

<script type="text/javascript">
Ext.onReady(function(){
  <c:forEach var="datagrid" items="${gridList}" varStatus="status">	
  	var tbar_customer${status.index} = new Ext.Toolbar({
		renderTo:'divBtn${status.index+1}',
           items:[
			{
			text:'打印',
			id:'btn-print',
			cls:'x-btn-text-icon',
	   		icon:'${pageContext.request.contextPath}/img/print.gif',
			handler:function(){
				print_listSaraly${status.index+1}();
			}
		},
		'->'
        ]
        });  
</c:forEach>
	
 var tabs = new Ext.TabPanel({
	    renderTo: 'my-tabs',
	    id:"tabs",
	    activeTab: 0,
	    layoutOnTabChange:true, 
	    forceLayout : true,
	    deferredRender:false,
	    height: document.body.clientHeight,
	    width : document.body.clientWidth, 
	    defaults: {autoWidth:true,autoHeight:true},
	    items:${json}
	});
	tabs.on("tabchange",function(tabpanel,tab) {
		//alert(tab.id);
    	//var girdid = "grid_" + tab.id ;
    	var grid = tab.id;
    	eval("goSearch_"+grid+"();") ;     
	})
});

	    
</script>

</head>
<body >
<div id="my-tabs">
	<c:forEach var="datagrid" items="${gridList}" varStatus="status">
	<div  style="height:expression(document.body.clientHeight-57);" id="listSaraly${status.index+1}">
		<div id="divBtn${status.index+1}"></div>
		<mt:DataGridPrintByBean name="listSaraly${status.index+1}"  />
	</div>
	</c:forEach>
</div>
</body>
</html>
