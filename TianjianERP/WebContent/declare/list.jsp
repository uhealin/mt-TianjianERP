<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>声明</title>
<script type="text/javascript">

Ext.onReady(function(){
	
	var tbar_customer = new Ext.Toolbar({
		renderTo:'gridDiv_${tableid}',
           items:[
<c:if test="${flag != 'project'}">		           
           {
            text:'新增年度独立性声明',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/add.gif',
            handler:function(){
            	goAdd();
			}
      	}, '-',
</c:if>{
		    text:'打印',
		    id:'btn-print',
		    cls:'x-btn-text-icon',
		    icon:'${pageContext.request.contextPath}/img/print.gif',
		    handler:function(){
		    	print_${tableid}();
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

});

</script>


</head>
<body>
<form name="thisForm" method="post" action="" >
<div style="height:expression(document.body.clientHeight-23);" >
<mt:DataGridPrintByBean name="${tableid}"  />
</div>

<input name="flag" type="hidden" id="flag" value="${flag }">
<input name="opt" type="hidden" id="opt" value="${opt }">
<input name="userid" type="hidden" id="userid" value="${userid }">

</form>
</body>
</html>
<script type="text/javascript">

function goAdd(){
	 var url="${pageContext.request.contextPath}/declare.do?method=judgeAllow";
	 var request= ajaxLoadPageSynch(url,"");
	 if(request == "succeed"){
		thisForm.action = "${pageContext.request.contextPath}/declare.do?method=add";
		thisForm.target = "";
		thisForm.submit();	
	 }else{
		 alert(request);
		 return ;
	 }
}


</script>
