<%@page import="java.text.MessageFormat"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>入职申请</title>

<link rel="shortcut icon" href="${pageContext.request.contextPath}/images/donggua.ico" 
mce_href="${pageContext.request.contextPath}/images/donggua.ico" type="image/x-icon">




 <%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
 <%
    String uuidHandled="5c4331cd-db93-4d13-9c8b-bea683097f81";
 String uuidUnHandled="75468806-4a69-4843-90c7-bfa4e2945a66";
 String urlFormPattern="formDefine.do?method=formListView&uuid={0}";
 String urlHandled=MessageFormat.format(urlFormPattern, uuidHandled);
 String urlUnhandled=MessageFormat.format(urlFormPattern, uuidUnHandled);
 %>
 
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
	    var el1={title:"员工登记",html:"<iframe src='${pageContext.request.contextPath}/employee/employee.jsp' height='"+h+"'  scrolling='yes'></iframe>" };
	    var el2={title:"报到指南",html:"<iframe src='employeeedit.do?opt=1' height='"+h+"'></iframe>"};
	    var el3={title:"报到通知打印",html:"<iframe src='employeeedit1.do?opt=1' height='"+h+"'></iframe>"};
	    
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
<body style="height: 100%">
    
</body>
</html>