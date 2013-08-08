<%@page import="com.matech.framework.pub.util.StringUtil"%>
<%@page import="java.sql.Connection"%>

<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>

<%@page import="com.matech.audit.pub.db.DBConnect"%>
<%@page import="com.matech.framework.pub.db.DbUtil"%>

<%@page import="com.matech.audit.service.doc.model.DocPostVO"%>
<%@page import="com.matech.framework.listener.UserSession"%>
<%@page import="com.matech.framework.pub.util.WebUtil"%>
<%@page import="com.matech.framework.listener.OnlineListListener"%>
<%@page import="com.matech.framework.listener.UserCurTask"%>
<%@page import="com.matech.framework.listener.UserSession"%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>


<%@ page import="com.zhuozhengsoft.ZSOfficeX.*, java.awt.*"%>
<jsp:useBean id="ZSCtrl" scope="page" class="com.zhuozhengsoft.ZSOfficeX.ZSOfficeCtrl"></jsp:useBean>

<%
  String basePath="http://"+request.getLocalAddr()+":"+request.getLocalPort()+request.getContextPath();
  
  ASFuntion asf = new ASFuntion();
  
  String uuid=asf.showNull(request.getParameter("uuid"));
  WebUtil webUtil=new WebUtil(request,response);
  UserSession userSession=webUtil.getUserSession();
  DocPostVO docPostVO=null;
  Connection conn=null;
  DbUtil dbUtil=null;
  String mode=request.getParameter("mode");
  mode=mode==null?"":mode;
  String tt=asf.showNull(webUtil.getPreUrl());
  boolean isAllowSign=mode.contains("allow_sign");
  
  
  boolean newmode=tt.contains("editDoc"); 
  System.out.println("mode="+tt+"|newmode="+newmode);
  
  boolean isReadOnly=false;
  
  
  try{
		conn=new DBConnect().getConnect();
		dbUtil=new DbUtil(conn);
		docPostVO=dbUtil.load(DocPostVO.class, uuid);
	  
		//检查在线并发打开文档的代码
		// list存放着所有在线用户的各种信息
		String userSessionId = asf.showNull(session.getId());
		List list = OnlineListListener.getList();
		if (!list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {

				UserSession us = (UserSession) list.get(i);
				if (us.getUserCurTasks()==null){
					continue;
				}
				Iterator it = us.getUserCurTasks().iterator();
				while (it.hasNext()) {
					UserCurTask userCurTask = (UserCurTask) it.next();
					
					if ((uuid).equals(userCurTask.getCurTaskCode())){
						//文件已经被打开
						
						if (!userSessionId.equals(us.getUserSessionId())){
							//不是本人，就要只读
							isReadOnly=true;
							break;
						}
						
					}
				}
			}
		}
		
		
		//登记打开文件信息
		UserCurTask userCurTask = new UserCurTask();
		userCurTask.setCurTaskProjectId("0"); // 底稿项目编号
		userCurTask.setCurTaskName(docPostVO.getTitle()+".doc"); // 文件名/不知道是VO的哪个，临时用docPostVO.getTitle
		userCurTask.setCurTaskEditTime(asf.getCurrentDate() + " "
				+ asf.getCurrentTime()); // 开始编辑时间
		userCurTask.setCurTaskCode(uuid); // 索引号
		userCurTask.setCurTaskId(uuid); // 任务编号

		// 如果其他用户打开，就登记为只读打开
		if (isReadOnly) {
			// 只读
			userCurTask.setCurTaskOpenType("0");
		} else {
			userCurTask.setCurTaskOpenType("1");
		}
		
		userSession.getUserCurTasks().add(userCurTask);
		session.setAttribute("userSession", userSession);
		
		request.setAttribute("isReadOnly",isReadOnly);
		
		
		//设置文件打开控制信息
		// 设置ZSOFFICE服务页面
		ZSCtrl.ServerURL = "/erp/zsserver.do";

		ZSCtrl.MenubarStyle = 5;
		ZSCtrl.Caption = "编辑"+docPostVO.getTitle();
		
		//设置服务路径
		//ZSCtrl.SaveDocURL = "savedoc.jsp";
		ZSCtrl.SaveDocURL = "docpost/savedoc.jsp?uuid="+uuid;
		
		//设置打开路径
		//ZSCtrl.webOpen("doc/1.doc", 2, "张佚名", "Word.Document");
		String openurl="docpost.do?method=openWord&uuid="+uuid;
		ZSCtrl.webOpen(openurl, 2, userSession.getUserName(), "Word.Document");
		
		ZSCtrl.CanCopy=false;
  }catch(Exception ex){
	  
  }finally{
	  
  }
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>编辑文稿  <%=docPostVO.getTitle() %></title>




<script type="text/javascript">

   
    //登记文件关闭信息
	function killSession(){
	try {
	
		var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		oBao.open("POST","${pageContext.request.contextPath}/system.do?method=fileClose&taskId=<%=uuid %>", true);
		oBao.send();
		
	} catch(e) {
		//
	}
}
   
 	function doSign(methodName,ischeck){
 		
 		var url="${pageContext.request.contextPath}/docpost.do";
 		var param={uuids:"'<%=docPostVO.getUuid() %>'",method:methodName,ischeck:ischeck};
 		$.post(url,param,function(str){
 			alert(str);
 			//window.opener.location.reload();
 			//window.close();
 			toBack();
 			
 		});
 	}
 	
 	
 	function toBack(){
 		//alert(getParamObject()["preUrl"]);
		killSession();
		var url="<%=webUtil.getPreUrl()%>".replace("editDoc","");
 		window.location.href=url;
 	}
 	
	//检查控件打开的文件的状态
	function opTrace(state){
		var ZSOfficeCtrl=document.getElementById("ZSOfficeCtrl");
		ZSOfficeCtrl.ShowRevisions=state;
	}
	
	function opSaveFile(){

		var readOnly = "<%=isReadOnly %>";
		if( readOnly=="false") {
			var ZSOfficeCtrl=document.getElementById("ZSOfficeCtrl");
			ZSOfficeCtrl.WebSave();
			alert('保存成功');
		}else{
			alert('无权保存！');
		}s
	}
	
	
	function opCloseFile(){
		
	}

</script>


</head>
<body onunload="killSession();" >
   <div style="text-align: center; background-color: gray;">
    <% 
    String sql="select count(*) from oa_doc_log where doc_id=? and node_code=? and handler_id=?";
    if(isAllowSign&&"xm".equals(docPostVO.getNode_code())
    &&docPostVO.getProject_member_ids().contains(userSession.getUserId())
    &&dbUtil.queryForInt(sql,new Object[]{docPostVO.getUuid(),"xm"})<1){ 
			isReadOnly=true;
     %>
         <button type="button" onclick="doSign('doProjectSign')">项目相关签字</button>      
    <%}else if(isAllowSign&&StringUtil.isIn(docPostVO.getNode_code(), new String[]{"hq","qf"})
    	    &&(docPostVO.getCountersigner_ids().contains(userSession.getUserId())
    	    ||docPostVO.getDep_leader_ids().contains(userSession.getUserId()))
    	    &&dbUtil.queryForInt(sql,new Object[]{docPostVO.getUuid(),"hq",userSession.getUserId()})<1){ 
			
			isReadOnly=false;
			%>
          <button type="button" onclick="doSign('doCounterSign')">会签</button>    
           
    <%}else if(isAllowSign&&"qf".equals(docPostVO.getNode_code())
    	    &&docPostVO.getSignissuer_ids().contains(userSession.getUserId())
    	    &&dbUtil.queryForInt(sql,new Object[]{docPostVO.getUuid(),"qf",userSession.getUserId()})<1){ 
			
			isReadOnly=false;
			
			%>
   <button type="button" onclick="doSign('doSignissue','true')">签发通过</button>
   <button type="button" onclick="doSign('doSignissue','false')">签发不通过</button>
   
    <%}else if(isAllowSign&&"hy".equals(docPostVO.getNode_code())
    	    &&docPostVO.getChecker_ids().contains(userSession.getUserId())
    	    &&dbUtil.queryForInt(sql,new Object[]{docPostVO.getUuid(),"hy",userSession.getUserId()})<1){ 
			
			isReadOnly=true;
			%>
      <button type="button" onclick="doSign('doCheck','true')">核阅通过</button>
      <button type="button" onclick="doSign('doCheck','false')">核阅不通过</button>
    <%} %>
	
	 <% 
		//显示保存
		if( isReadOnly==false){
	 %>
		<button type="button" onclick="opSaveFile()">保存</button>
	 <% 
		}
		
		//显示痕迹
		if( newmode==false){
	 %>
	  <button type="button" onclick="opTrace(true)">查看修改痕迹</button>
	  <button type="button" onclick="opTrace(false)">查看终稿</button>
	 <% 
		}
	 %>
	  
      <button type="button" onclick="toBack()">关闭</button>
   </div>

   
   <form name="formData" method="post"  id="formData">
<!--**************   ZSOFFICE 客户端代码开始    ************************-->
	<SCRIPT language="JavaScript" event="OnInit()" for="ZSOfficeCtrl">
		// 控件打开文档前触发，用来初始化界面样式
	</SCRIPT>
	<SCRIPT language="JavaScript" event="OnDocumentOpened(str, obj)" for="ZSOfficeCtrl">
		// 控件打开文档后立即触发，添加自定义菜单，自定义工具栏，禁止打印，禁止另存，禁止保存等等
		
	</SCRIPT>
	<SCRIPT language="JavaScript" event="OnDocumentClosed()" for="ZSOfficeCtrl">
		
	</SCRIPT>
	<SCRIPT language="JavaScript" event="OnUserMenuClick(index, caption)" for="ZSOfficeCtrl">
		// 添加您的自定义菜单项事件响应
	</SCRIPT>
	<SCRIPT language="JavaScript" event="OnCustomToolBarClick(index, caption)" for="ZSOfficeCtrl">
		// 添加您的自定义工具栏按钮事件响应
	</SCRIPT>	
	<%=ZSCtrl.getDocumentView("ZSOfficeCtrl", request)%>
<!--**************   ZSOFFICE 客户端代码结束    ************************-->
</form>
    
</body>



</html>

<% DbUtil.close(conn); %>