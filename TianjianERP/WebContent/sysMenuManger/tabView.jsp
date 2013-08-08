<%@page import="com.matech.framework.servlet.extmenu.Menu"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
    <%
       List<Menu> menus=(List<Menu>)request.getAttribute("menus");
    %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<style type="text/css">
   iframe{
     width: 100%;
   }
 </style>
 
 <script type="text/javascript">
  
 var viewport, mytab1;
 

 
 Ext.onReady(function(){

	    var h= window.parent.document.body.clientHeight-155;
	    
	    
	    mytab = new Ext.TabPanel({
	        id: "tab1",
	        region:'center',
	        activeTab:0, //选中第一个 tab
	        height: document.body.clientHeight-27, 
	         
	    //    height: document.body.clientHeight-Ext.get('divTab').getTop(),
	   //     width : document.body.clientWidth, 
	   //     defaults: {autoWidth:true,autoHeight:true},
	        items:[
	             <%
	               for(int i=0;i<menus.size();i++){
	            	Menu menu=menus.get(i);
	             %>
	             {id:"<%=menu.getId()%>"
	            	 ,title:"<%=menu.getText()%>"
	            	 ,html:"<iframe src='<%=menu.getHref()%>' frameborder='0' name='frame_<%=menu.getId()%>' height='"+h+"'></iframe>" 
	                ,listeners:{// 添加监听器，点击此页面的tab时候要重新加载（刷新功能）
	                    dblclick:function(){
	                    	//refreshIndex();
	                    	 //getUpdater().refresh();
	                    	 window.frames["frame_<%=menu.getId()%>"].location.reload();   //点击我的工作台，进行刷新
	                     }
	                }
	             }
	              <% if(i!=menus.size()-1){%>,<%}%>
	             
	             <%}%>
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

<body  style="height: 100%">
    
</body>
</html>