<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>会议室统计</title>

<script type="text/javascript">
	var mytab;
	function ext_init(){
		 
	    var height = document.body.clientHeight - 39 ;
	    
	    mytab = new Ext.TabPanel({
	        id: "tab",
	        renderTo: "divTab",
	        activeTab: 0, //选中第一个 tab
	        autoScroll:false,
	        frame: false,
	        height: document.body.clientHeight-Ext.get('divTab').getTop()-10, 
	        defaults: {autoHeight: true,autoWidth:true},
	        items:[
	            {title: "按会议室统计",
	             id:"tab1",
	             html:'<iframe id="tab1Iframe" name="tab1Iframe" scrolling="no" frameborder="0" width="100%" height="'+height+'" src="${pageContext.request.contextPath}/meetingRoom.do?method=listRoomCount&menuid=${param.menuid}"></iframe>'
	             },
	             {title: "按部门统计",
	             id:"tab2",
	             html:'<iframe id="tab2Iframe" name="tab2Iframe" scrolling="no" frameborder="0" width="100%" height="'+height+'" src="${pageContext.request.contextPath}/meetingRoom.do?method=listDepartCount&menuid=${param.menuid}"></iframe>'
	             }
	        ]
	    });
	    
	    
    }
    
    window.attachEvent('onload',ext_init);

</script>
</head>
<body>

<div style="height:expression(document.body.clientHeight-10);overflow: auto;" >

<div id="divTab" >
	
</div>	

</div>

</body>
</html>
