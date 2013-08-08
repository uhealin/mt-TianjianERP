<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>实习生自助</title>

<link rel="shortcut icon" href="${pageContext.request.contextPath}/images/donggua.ico" 
mce_href="${pageContext.request.contextPath}/images/donggua.ico" type="image/x-icon">

<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>


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
	    var el1={title:"实习生资料登记",html:"<iframe src='${pageContext.request.contextPath}/cadet/list_zizhu.jsp' height='"+h+"'  scrolling='yes'></iframe>" };
	    var el2={title:"实习生考核申请",html:"<iframe src='${pageContext.request.contextPath}/cadet/list_kaohe.jsp' height='"+h+"'  scrolling='yes'></iframe>" };
	    var el3={title:"修改信息",html:"<iframe src='${pageContext.request.contextPath}/cadet/modifyInfo.jsp' height='"+h+"'  scrolling='yes'></iframe>" };
	    
	    mytab = new Ext.TabPanel({
	        id: "tab1",
	        region:'center',
	        activeTab:0, //选中第一个 tab
	        height: document.body.clientHeight-27, 
	         
	    //    height: document.body.clientHeight-Ext.get('divTab').getTop(),
	   //     width : document.body.clientWidth, 
	   //     defaults: {autoWidth:true,autoHeight:true},
	        items:[
	             el1,el2,el3
	            ]
	    });

	    viewport= new Ext.Viewport({
	    	layout:"border",
	    	items:[
              mytab
	    	       ]
	    });
 });  //Ext.onReady
 
 </script>










 
 
 

</head>
<body>
    
</body>
</html>