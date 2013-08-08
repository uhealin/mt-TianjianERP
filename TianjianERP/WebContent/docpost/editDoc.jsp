<%@page import="com.matech.framework.pub.path.Path"%>
<%@page import="com.matech.audit.service.manuscript.ManuFileService"%>
<%@page import="java.text.MessageFormat"%>
<%@page import="java.io.IOException"%>
<%@page import="java.io.File"%>
<%@page import="com.matech.audit.service.doc.DocPostService.Node"%>
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
<%@ page import="com.zhuozhengsoft.ZSOfficeX.*, java.awt.*"%>
<jsp:useBean id="ZSCtrl" scope="page" class="com.zhuozhengsoft.ZSOfficeX.ZSOfficeCtrl"></jsp:useBean>
   
 
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp"%>


<%!

  	public File getWordFile(String basePath,String fileName) throws Exception{
		
		System.out.println("================basePath:"+basePath);
		System.out.println("================fileName:"+fileName);
		
		File fileDir=new File(basePath),
		fileWord=new File(basePath+"\\"+fileName),
		fileWordTemplate=new File(basePath+"\\redheadTJCF.doc");
		System.out.println("================fileWordTemplate:"+basePath+"\\redheadTJCF.doc");
		if(!fileDir.exists()){
			fileDir.mkdir();
		}
		if(!fileWordTemplate.exists()){
			throw new IOException(MessageFormat.format("模板不存在，请创建:{0}", fileWordTemplate.toString()));
		}
		if(!fileWord.exists()){
			ManuFileService mfs = new ManuFileService() ;
			mfs.copyFile(fileWordTemplate, fileWord) ;
		}
		return fileWord;
         
	}
%>

<%
  
  String basePath="http://"+request.getLocalAddr()+":"+request.getLocalPort()+request.getContextPath();
  String phyPath=Path.getWarPath(config)+"docpost\\file";
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
  
  boolean isReadOnly=false,hasSign=false;
  File docFile=null;
  String saveUrl=webUtil.getWebRootPath()+"/docpost/saveDoc.jsp";
  String preUrl=webUtil.getPreUrl();


 
  try{
	  conn=new DBConnect().getConnect();
	  dbUtil=new DbUtil(conn);
	  docPostVO=dbUtil.load(DocPostVO.class, uuid);
	  
	  docFile= getWordFile(phyPath,docPostVO.getUuid()+".doc");
	  
  }catch(Exception ex){
	  
  }finally{
	  
  }
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>编辑文稿  <%=docPostVO.getTitle() %></title> 
</head>
<body  >

   <div style="text-align: center; background-color: gray;display: none;">
    <% 
    String sql="select count(*) from oa_doc_log where doc_id=? and node_code=? and handler_id=?";
     %>
	
	
	  <!--  
      <button type="button" onclick="toBack()">关闭</button>
      -->
   </div>

    
      <div>
    <!--**************   ZSOFFICE 客户端代码开始    ************************-->
	<SCRIPT language="JavaScript" event="OnInit()" for="ZSOfficeCtrl">
		// 控件打开文档前触发，用来初始化界面样式
	</SCRIPT>
	<SCRIPT language="JavaScript" event="OnDocumentOpened(str, obj)" for="ZSOfficeCtrl">
		// 控件打开文档后立即触发，添加自定义菜单，自定义工具栏，禁止打印，禁止另存，禁止保存等等
		ZSOfficeCtrl.EnableFileCommand(5) = false;
		//ZSOfficeCtrl.AppendToolButton(1, "保存", 1);
	    //ZSOfficeCtrl.AppendToolButton(5, "-");			
		ZSOfficeCtrl.AppendToolButton(2, "显示痕迹", 0);
		ZSOfficeCtrl.AppendToolButton(5, "-");
		ZSOfficeCtrl.AppendToolButton(3, "隐藏痕迹", 0);
		ZSOfficeCtrl.AppendToolButton(5, "-");		
		ZSOfficeCtrl.AppendToolButton(6, "全屏/还原", 4);
		ZSOfficeCtrl.AppendToolButton(5, "-");
		ZSOfficeCtrl.AppendToolButton(4, "关闭", 0);
		
	    <% 
	    
	     if(isAllowSign&&(StringUtil.isIn(docPostVO.getNode_code(), new String[]{"hq","qf"})
	    	    &&docPostVO.getCountersigner_ids().contains(userSession.getUserId())
	    	    &&dbUtil.queryForInt(sql,new Object[]{docPostVO.getUuid(),"hq",userSession.getUserId()})<1)
	    	    ){ 
	    	 //hasSign=false;
	    	 if("b".equals(docPostVO.getApply_type())&&userSession.getUserId().equals(docPostVO.getCur_hq_id())){ %>
	    	 ZSOfficeCtrl.AppendToolButton(5, "-");
	          ZSOfficeCtrl.AppendToolButton(7, "签字", 3);
	    <%}else if("a".equals(docPostVO.getApply_type())){ %> 
				ZSOfficeCtrl.AppendToolButton(5, "-");
	            ZSOfficeCtrl.AppendToolButton(7, "签字", 3);
	    <% }
	    		 }else if(isAllowSign&&"qf".equals(docPostVO.getNode_code())
	    	    &&docPostVO.getSignissuer_ids().contains(userSession.getUserId())
	    	    &&dbUtil.queryForInt(sql,new Object[]{docPostVO.getUuid(),"qf",userSession.getUserId()})<1){ 
	    			 //hasSign=false;
	    	    %>
	    	    ZSOfficeCtrl.AppendToolButton(5, "-");
	    	    ZSOfficeCtrl.AppendToolButton(8, "签字", 3);
	    <%}else if(isAllowSign&&"hy".equals(docPostVO.getNode_code())
	    	    &&docPostVO.getChecker_ids().contains(userSession.getUserId())
	    	    &&dbUtil.queryForInt(sql,new Object[]{docPostVO.getUuid(),"hy",userSession.getUserId()})<1){ 
	    	        //hasSign=false;
	    	    %>
	    	    ZSOfficeCtrl.AppendToolButton(5, "-");
	    	    ZSOfficeCtrl.AppendToolButton(9, "签字并核阅", 3);
	    	    <% if(StringUtil.isIn(docPostVO.getDoc_type(), new String[]{"FW_HYWJ"})
	    	    		||docPostVO.getDoc_type().startsWith("FW_TJ_")
	    	    		){ %>
	    	    ZSOfficeCtrl.AppendToolButton(5, "-");
	    	    ZSOfficeCtrl.AppendToolButton(10, "确认不核阅", 3);
	    	    <% }%>
	    <%} %>

	</SCRIPT>
	<SCRIPT language="JavaScript" event="OnDocumentClosed()" for="ZSOfficeCtrl">
		
	</SCRIPT>
	<SCRIPT language="JavaScript" event="OnUserMenuClick(index, caption)" for="ZSOfficeCtrl">
		// 添加您的自定义菜单项事件响应
	</SCRIPT>
	<SCRIPT language="JavaScript" event="OnCustomToolBarClick(index, caption)" for="ZSOfficeCtrl">
		// 添加您的自定义工具栏按钮事件响应
		
		if(index == 1) 	{ZSOfficeCtrl.WebSave();alert(" 保存成功");}
		else if(index==4){
			ZSOfficeCtrl.WebSave();
			toBack();}
		else if (index == 2) {
			ZSOfficeCtrl.ShowRevisions = true;
		}
		else if (index == 3) {
			ZSOfficeCtrl.ShowRevisions = false;
		}
		else if (index == 6) {
			ZSOfficeCtrl.FullScreen = !ZSOfficeCtrl.FullScreen;
		}else if (index==7){
			doSign('doCounterSign');
		}else if(index==8){
			doSign('doSignissue','true');
		}else if(index==9){
			doSign('doCheck','true');
		}else if(index==10){
			doSign('doCheck','false');
		}

	</SCRIPT>	
	<%
	WordResponse ZSWord = new WordResponse();
	  ZSWord.FormMode = false;

	  WordResDataRegion dataRegion = ZSWord.openDataRegion("test1");
	  //dataRegion.setValue("测试的内容1");
	  //dataRegion.setNeedSubmit(true);

	  //dataRegion = ZSWord.openDataRegion("test2");
	  //dataRegion.setValue("测试的内容2");

	  // 设置ZSOFFICE组件服务页面
	  ZSCtrl.ServerURL = webUtil.getWebRootPath()+"/zsserver.do";

	  ZSCtrl.MenubarStyle = 5;
	 // ZSCtrl.MenubarColor = Color.decode("#FF6633");
	  ZSCtrl.TitlebarColor = Color.decode("#FFFFFF");
	  ZSCtrl.TitlebarTextColor = Color.decode("#50C048");
	  ZSCtrl.Toolbars=true;
	  ZSCtrl.Menubar=false;
	  ZSCtrl.Caption = "发文文稿:"+docPostVO.getTitle();
	  //ZSCtrl.SaveDataURL =saveUrl;
      ZSCtrl.SaveDocURL = "saveDocZO.jsp";
	  ZSCtrl.assign(ZSWord); // 注意！不要忘记此句代码！
	  
	  
	  int openMode=0;
	  
	  
	  /*
	  if(userSession.getUserId().equals(docPostVO.getCreater_ids())){
		  if(StringUtil.isIn(docPostVO.getNode_code(), new String[]{Node.end.name()})){
			  openMode=3;
		  }else{
			  openMode=0;
		  }
	  }else{
		  if(StringUtil.isIn(docPostVO.getNode_code(), new String[]{Node.end.name()})||hasSign){
			  openMode=3;
		  }else{
			  openMode=0;
		  }
	  }
	  */
	  
	  
	  if(StringUtil.isIn(docPostVO.getNode_code(), new String[]{Node.end.name()})||hasSign){
			//如果是结束了，就不能改了
			openMode=3;
	  }else if(preUrl.contains("editDoc")){
		  openMode=2;  //新发起的不留痕迹
	  }
	  
	  isReadOnly= StringUtil.isIn(docPostVO.getNode_code(), new String[]{Node.end.name()});
	  if(isReadOnly){
		  openMode=3;
	  }

	  try{
		System.out.println("------------------------------docFile.getName()="+docFile.getName());
		System.out.println("------------------------------userSession.getUserName()="+userSession.getUserName());
		System.out.println("------------------------------openMode()="+openMode);
		System.out.println("------------------------------hasSign()="+hasSign);
		
	  
	  }catch(Exception e){
		System.out.println(e.getMessage());
	  }
	  
	  
	  
	 
	  
	  
	  ZSCtrl.webOpen("file/"+docFile.getName(), openMode, userSession.getUserName(), "Word.Document");
	%>
	<%=ZSCtrl.getDocumentView("ZSOfficeCtrl", request)%>
    <!--**************   ZSOFFICE 客户端代码结束    ************************-->
    
    </div>
    
</body>



<script type="text/javascript">

   
 	function doSign(methodName,ischeck){
 		ZSOfficeCtrl.WebSave();
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
		
		var url="<%=preUrl%>".replace("editDoc","");
 		window.location.href=url;
 	}
 	
	
	
	
</script>

</html>

<% DbUtil.close(conn); %>