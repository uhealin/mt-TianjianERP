<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>表单编辑   ${datetime} </title>

<style>

.before{
	border: 0px;
	background-color: #FFFFFF !important;
}

</style>

<script type="text/javascript">
function ext_init(){
    new Ext.Toolbar({
	   		renderTo: "divBtn",
	   		defaults: {autoHeight: true,autoWidth:true},
            items:[{
			text:'关闭',
            icon:'${pageContext.request.contextPath}/img/close.gif',
            handler:function(){
				closeTab(parent.mainTab) ;
			}
       	},'->']
	});
   
}

function view(){
	var form_obj = document.all; 
	//form的值
	for (var i=0; i < form_obj.length; i++ ) {
		e=form_obj[i];
		if (e.tagName=='INPUT' || e.tagName=='TEXTAREA') {
			e.readOnly = true ;
			e.className = "before";
		}
		if(e.tagName=='SELECT'){
			e.disabled= true;
			e.className = "before";
		}
		//alert(e.tagName);
		if(e.tagName == 'A'){
			e.style.display = "none";
		}
		if(e.tagName == "IMG"){
			e.style.display = "none";
		}
	}
}
</script>


</head>
<body>
<div id="divBtn"></div>
<div class="autoHeightDiv" style="overflow: auto;" >
${html}
</div>
</body>
</html>
<script type="text/javascript">

Ext.onReady(function(){
	ext_init();
	if("${param.view}" == "true") {
		//view();
	}
	 mt_form_initDateSelect();
		mt_form_initAttachFile();
});

</script>

