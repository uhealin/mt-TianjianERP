<%@page import="java.text.MessageFormat"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>


 <%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
 <%
   
 String urlFormPattern="formDefine.do?method=formListView&uuid={0}";
 String urlChecked=MessageFormat.format(urlFormPattern, "f360c71a-eca4-4335-bbaa-11ee896c41d3");
 String urlUnchecked=MessageFormat.format(urlFormPattern, "eb2a9808-0727-48b4-85ce-2210d532ca07");
 String urlFinished=MessageFormat.format(urlFormPattern, "53817ea7-7c71-46e3-8aa7-5f07a2405099");
 %>
 <style type="text/css">
 
   iframe{
     width: 100%;
    
   }
 </style>
 
 <script type="text/javascript">
 
 var viewport, mytab1;
 var h= window.parent.document.body.clientHeight-155;
var el1={title:"未审核",html:"<iframe src='<%=urlUnchecked%>' height='"+h+"' ></iframe>" };
var el2={title:"已审核",html:"<iframe src='<%=urlChecked%>' height='"+h+"' ></iframe>"};
var el3={title:"已办结",html:"<iframe src='<%=urlFinished%>' height='"+h+"' ></iframe>"};
 Ext.onReady(function(){


	    
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