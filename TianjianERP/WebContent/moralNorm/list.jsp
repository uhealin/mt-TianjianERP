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
		renderTo:'gridDiv_moralNorm',
           items:[          
           {
            text:'职业道德申明',
            cls:'x-btn-text-icon',
            icon:'${pageContext.request.contextPath}/img/add.gif',
            handler:function(){
            	goAdd();
			}
      	}, '-',
{
		    text:'打印',
		    id:'btn-print',
		    cls:'x-btn-text-icon',
		    icon:'${pageContext.request.contextPath}/img/print.gif',
		    handler:function(){
		    	print_moralNorm();
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
<mt:DataGridPrintByBean name="moralNorm"  />
</div>

</form>
</body>
</html>
<script type="text/javascript">

function goAdd(){
		thisForm.action = "${pageContext.request.contextPath}/declare.do?method=addMoralNorm";
		thisForm.target = "";
		thisForm.submit();	
}


</script>
