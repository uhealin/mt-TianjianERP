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
    String uuidHandled="5c4331cd-db93-4d13-9c8b-bea683097f81";
 String uuidUnHandled="75468806-4a69-4843-90c7-bfa4e2945a66";
 String urlFormPattern="formDefine.do?method=formListView&uuid={0}";
 String urlHandled=MessageFormat.format(urlFormPattern, uuidHandled);
 String urlUnhandled=MessageFormat.format(urlFormPattern, uuidUnHandled);
 %>

 
 <script type="text/javascript"> 
	var mytab;
	function ext_init(){
		 
	    var height = document.body.clientHeight - 32 ;
	    
	    mytab = new Ext.TabPanel({
	        id: "tab",
	        renderTo: "divTab",
	        activeTab: 0, //选中第一个 tab
	        autoScroll:false,
	        frame: false,
	        height: 12, 
	        defaults: {autoHeight: true,autoWidth:true},
	        items:[
	            {title: "待审批",
	             id:"tab1",
	             html:'<iframe id="tab1Iframe" name="tab1Iframe" scrolling="no" frameborder="0" width="100%" height="'+height+'" src="/AuditSystem/meetingOrderSy.do?method=goAudit"></iframe>'
	             },
	             {title: "已通过",
	             id:"tab2",
	             html:'<iframe id="tab2Iframe" name="tab2Iframe" scrolling="no" frameborder="0" width="100%" height="'+height+'" src="/AuditSystem/meetingOrderSy.do?method=goPass"></iframe>'
	             }
	             //,
	             //{title: "不通过",
	             //id:"tab3",
	             //html:'<iframe id="tab3Iframe" name="tab3Iframe" scrolling="no" frameborder="0" width="100%" height="'+height+'" src="/AuditSystem/meetingOrder.do?method=goNotPass"></iframe>'
	             //}
	        ]
	    });
	    
	    
    }
    
    window.attachEvent('onload',ext_init);
 
</script>
</head>
<body>
 
<div id="divTab" >
	
</div>	
<div style="height:expression(document.body.clientHeight-10);overflow: auto;" >
</div>
 
</body>
</html>