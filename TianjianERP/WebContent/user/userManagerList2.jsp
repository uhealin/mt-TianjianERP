<%@page import="java.util.Iterator"%>
<%@page import="com.matech.audit.service.kdic.model.KDicVO"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"  errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%
List<KDicVO> emtypes=(List<KDicVO>)request.getAttribute("emtypes");


%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>人员管理</title>
<style type="text/css">
   iframe{
     width: 100%;
     
   }
 </style>
</head>
 <script type="text/javascript">
  
 var viewport, mytab1;
 Ext.onReady(function(){

	    var h= window.parent.document.body.clientHeight-25;
	  
	var mytab = new Ext.TabPanel({
        id: "tab1",
        region:'center',
        activeTab:0, //选中第一个 tab
        height: document.body.clientHeight-27, 
        items:[
				<%
					for(Iterator<KDicVO> it=emtypes.iterator();it.hasNext();){
                       KDicVO emtype=it.next();
				%>
				     {id:"myUser_<%=emtype.getValue()%>", title: "<%=emtype.getName()%>",
				    	 html: "<iframe src='user.do?method=userListByEmtype&departmentidNList=${departmentidNList}&areaid=${areaid}&emtype=<%=emtype.getValue()%>' frameborder='0' name='frame_<%=emtype.getValue()%>' height='"+h+"'></iframe>"
				     ,listeners:{
		                    dblclick:function(){
		                    	 window.frames["frame_<%=emtype.getValue()%>"].location.reload(); 
		                     }
		                }
				     }
				     <%if(it.hasNext()){%>,<%}%>
		          <%
		          }
		          %>
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