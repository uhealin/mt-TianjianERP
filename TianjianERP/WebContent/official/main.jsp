<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>入职申请</title>





 <style type="text/css">
   iframe{
     width: 100%;
   }
 </style>


 <script type="text/javascript">
  
 var viewport, mytab1;
 

 
 Ext.onReady(function(){

	    var h= window.parent.clientHeight-155;
        h=$(document).height();
	    var el1={title:"入职申请",html:"<iframe src='${pageContext.request.contextPath}/cadet/beforeCadetSkip.jsp' height='"+h+"'  scrolling='yes'></iframe>" };
	    var el2={title:"转正申请",html:"<iframe src='${pageContext.request.contextPath}/official/beforeApply.jsp' height='"+h+"'  scrolling='yes'></iframe>" };	    
	    mytab = new Ext.TabPanel({
	        id: "tab1",
	        region:'center',
	        activeTab:0, //选中第一个 tab
	        height: document.body.clientHeight-27, 
	         
	        items:[
	             el1,el2
	            ]
	    });

	    viewport= new Ext.Viewport({
	    	layout:"border",
	    	items:[
              mytab
	    	       ]
	    });
 });  
 
 </script>

</head>
<body>
    
</body>
</html>