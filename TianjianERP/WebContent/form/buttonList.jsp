<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>表单按钮维护</title>

<script type="text/javascript">
function ext_init(){
	new Ext.Toolbar({
		renderTo: "divBtn",
		height:30,
		defaults: {autoHeight: true,autoWidth:true},
       items:[{
        		text:'新增',
                icon:'${pageContext.request.contextPath}/img/add.gif',
                handler:function () {
                	var formId = document.getElementById("formId").value;
	     		    window.location="${pageContext.request.contextPath}/formQueryConfig.do?method=addOrEdit&formId="+formId;  
                }
          },'-',{
          		   text:'修改',
                   icon:'${pageContext.request.contextPath}/img/edit.gif',
          	       handler:function(){
          	            var value=Ext.getDom('chooseValue_formButton').value;
          	       	    if(value==''){
          	       	    	alert('请选择要修改的列!');
          	       	    	return;
          	       	    }
          	       	    var formId = document.getElementById("formId").value;
          	       	    window.location="${pageContext.request.contextPath}/formQueryConfig.do?method=addOrEdit&uuid="+value+"&formId="+formId;  
          	       }  	 
           },'-',{ 
		             text:'删除',
		             icon:'${pageContext.request.contextPath}/img/delete.gif' ,
		             handler:function(){
		             	var value=Ext.getDom('chooseValue_formButton').value;
          	       	    if(value==''){
          	       	    	alert('请选择要删除的列!');
          	       	    	return;
          	       	    }
          	       	    if(confirm("您确定删除该行吗?")){
          	       	    	var formId = document.getElementById("formId").value;
		                   window.location="${pageContext.request.contextPath}/formQueryConfig.do?method=delButton&uuid="+value+"&formId="+formId;  
		                }
		             }
		         },'-',{ 
           text:'返回',
           cls:'x-btn-text-icon',
           icon:'${pageContext.request.contextPath}/img/back.gif' ,
           handler:function(){
        	   window.history.back();
		   }
     	 },'->']
});
}
window.attachEvent('onload',ext_init);

</script>
</head>
<body>
	<div id="divBtn"></div>
	<input type="hidden" id="formId" value="${formId }">
	<div style="height:expression(document.body.clientHeight-27);width:100%"> 
		<mt:DataGridPrintByBean name="formButton"/>
	</div>	
</body>

</html>
