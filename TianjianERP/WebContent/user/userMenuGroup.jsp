<%@page import="net.sf.json.JSONObject"%>
<%@page import="net.sf.json.JSONArray"%>
<%@page import="com.matech.framework.listener.UserSession"%>
<%@page import="com.matech.audit.service.user.model.UserMenuGroupVO"%>
<%@page import="java.util.*"%>
<%@page import="com.matech.audit.pub.db.DBConnect"%>
<%@page import="com.matech.framework.pub.db.DbUtil"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.matech.framework.pub.util.WebUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>



<%
     WebUtil webUtil=new WebUtil(request,response);
     Connection conn=null;
     DbUtil dbUtil=null;
     List<UserMenuGroupVO> userMenuGroupVOs=new ArrayList<UserMenuGroupVO>();
     UserSession userSession=webUtil.getUserSession();
     List<UserMenuGroupVO> userMenuGroupVOsTree=new ArrayList<UserMenuGroupVO>();
     try{
    	
    	conn=new DBConnect().getConnect();
    	dbUtil=new DbUtil(conn);
    	//userMenuGroupVOs=dbUtil.select(UserMenuGroupVO.class, "select * from {0} where userid=?", userSession.getUserId());
    	//userMenuGroupVOsTree=getMenuTree(userMenuGroupVOs, "root");
     }catch(Exception ex){
    	 
     }finally{
    	 DbUtil.close(conn);
     }
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>

<script type="text/javascript">
Ext.onReady(function(){
    
    // NOTE: This is an example showing simple state management. During development,
    // it is generally best to disable state management as dynamically-generated ids
    // can change across page loads, leading to unpredictable results.  The developer
    // should ensure that stable state ids are set for stateful components in real apps.
    Ext.state.Manager.setProvider(new Ext.state.CookieProvider());
    
    var viewport = new Ext.Viewport({
        layout: 'border',
        items: [
        // create instance immediately
       {
            // lazily created panel (xtype:'panel' is default)
            region: 'west',
            contentEl: 'west',
            split: true,
            width: 250,
            minSize: 100,
          
            collapsible: true,
            title: 'South',
            margins: '0 0 0 0',
			autoScroll: true
			
        },
        // in this instance the TabPanel is not wrapped by another panel
        // since no title is needed, this Panel is added directly
        // as a Container
        {
		title: 'center',
		  region: 'center',
            contentEl: 'center',
			autoScroll: true
		}
	   ]
    });
    // get a reference to the HTML element with id "hideit" and add a click listener to it 
    
    
    
    
    
    
    var store=Ext.create("Ext.data.TreeStore",{
    	proxy:{
    		type:"ajax",
    		url:'${pageContext.request.contextPath}/userMenuGroup.do?method=treeMenuGroup'
    	}
    });
    
    var tree=Ext.create("Ext.treePanel",{
    	renderTo:"grouptree",
    	store:store,
    	rootVisible:true
    });
    
});





</script>

</head>
<body>

     <div id="center" class="x-hide-display">
       <iframe id="frameMenu" frameborder="0" style="width: 100%;height: 400px" ></iframe>
	 </div>
    
    
    <div id="west" class="x-hide-display">
        <div id="grouptree"></div>
    </div>

</body>
</html>