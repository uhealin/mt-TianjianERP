<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ 
page import="org.del.JRockey2Opp"%><%@ 
page import="java.sql.*"%><%@ 
page import="java.util.List"%><%@ 
page import="com.matech.audit.pub.db.DBConnect"%><%@ 
page import="com.matech.framework.pub.db.DbUtil"%><%@
page import="com.matech.framework.listener.UserSession"%><%

	String refresh=request.getParameter("refresh");
	if (refresh!=null && !refresh.equals("")){
		request.setAttribute("refresh",refresh);
	}

	String sysUi = JRockey2Opp.getInfoFromDog("sysVn");

	//如果提供了templatetaskid,则按照第二种方式来走,
	//也就是直接打开模板文件并刷新;
	String templateid=request.getParameter("templateid");
	if (templateid==null || templateid.equals("")){
		//没有提供 模板编号,就无条件赋值为0
		templateid="0";
	}

	String templatetaskid=request.getParameter("templatetaskid"),templatefilename="1.xls";
	if (templatetaskid==null ){
		templatetaskid="";
	}else{
		//取出底稿对应的名字
		String strSql="select taskname from k_tasktemplate where typeid="+templateid+" and taskid="+templatetaskid;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			DBConnect db = new DBConnect();
			conn = db.getConnect("");

			//不论是保存的单元格，还是引用的单元格，都返回，以便做平衡性校验
			ps = conn.prepareStatement(strSql);

			rs=ps.executeQuery();
			if (rs.next()){
				templatefilename=rs.getString(1);
			}else{
				//找不到底稿,就说明底稿被删除了,导航到错误页面
				session.setAttribute("errmsg","此找不到底稿,就说明底稿被删除了。请与系统管理员联系!!");
				String TRY_URL = "${pageContext.request.contextPath}/AS_SYSTEM/error_page.jsp";
			    response.sendRedirect(TRY_URL);
			}
		} catch (Exception e) {
			out.println("执行导入失败！");
			System.out.println("执行EXCEL保存后台导入失败,sql=" + strSql);
			e.printStackTrace();
		} finally {

			//关闭
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);

		}

	}


	String[] filename=request.getParameterValues("filename");
	if (filename==null){
		String singlefilename=(String)request.getAttribute("filename");

		if (singlefilename==null || "".equals(singlefilename)){
			//传过来的是LIST
			java.util.List l=(List)request.getAttribute("filenameList");
			if (l!=null){
				filename=new String[l.size()];
				for(int i=0;i<l.size();i++){
					filename[i]=(String)l.get(i);
				}
			}


		}else{
			filename=new String[]{singlefilename};
		}
	}
	
	String bVpage = request.getParameter("bVpage");
	UserSession userSession = (UserSession)session.getAttribute("userSession");
	boolean inOcx = false;
	
	if(userSession != null) {
		//userSession.setInOcx(true); //根据这个变量来判断是否在管控中打开
		inOcx = userSession.isBInOcx();
	}
%>
<script type="text/javascript">


var getlocationhost="http:\/\/"+window.location.host;
var url=getlocationhost+"${pageContext.request.contextPath}/Excel/tempdata/Download.jsp?filename=<%= filename[0] %>";
var inOcx = "<%=inOcx%>";
if(inOcx == "true") {
	window.location = url;
} else {

	var AuditReport =  new ActiveXObject("AuditReportPoject.AuditReport");
	var orifilename="<%= filename[0] %>";
	
	//打开文件
	try{
	  AuditReport.pUTF8=true;
	
	<%
		if (templatetaskid== null || templatetaskid.equals("")){
	%>
		    AuditReport.pFileName = "<%= filename[0] %>";
			
			AuditReport.pOpenUrl = url;
			
			AuditReport.pOpenMode=1;
			
			AuditReport.pUrlParameter= getlocationhost+"|&curProjectid=${userSession.curProjectId}&curPackageid=${userSession.curAccPackageId}&userId=${userSession.userId}";
	
			AuditReport.funOpenUrlFile(2);
	
	
			//bHDirection缺省为true,为纵向；设置为false，为横向
			AuditReport.subSetExcelScreenUpdating(false);
	
	
		  	AuditReport.subExcelPageSetTitleAndFooters("${strPrintTitleRows}","","","","","","","");
	
			try{
			   //修复不能打印的BUG；
			   AuditReport.subRepairExcelPrintBug();
	
			//刷新图形
			if("${refresh}" !=""){
				AuditReport.subRefreshExcelData(2);
			}
	
			 }catch(e){}
	
			AuditReport.subSetExcelScreenUpdating(true);
	<%
	
			//多个的情况下进行合并
			for(int i=1; i<filename.length ; i++){
	%>
	
					var fileName = Math.random() + ".xls";
	
					var tempfile = AuditReport.funGetTempDir() + fileName;
					var url=getlocationhost+"${pageContext.request.contextPath}/Excel/tempdata/Download.jsp?filename=<%= filename[i] %>";
	
					result=AuditReport.funDownloadFile(url,tempfile);
	
					if (result==""){
						AuditReport.funCombineFile(tempfile);
					}else{
						alert("下载指定底稿失败:"+result);
					}
	
	<%
			}
		}else{
	
	%>
			AuditReport.pFileName = "<%= templatefilename %>";
	
			var url=getlocationhost+"${pageContext.request.contextPath}/taskCommon.do?method=fileDownload&typeId=<%=templateid%>&taskId=<%=templatetaskid%>";
	
			AuditReport.pOpenUrl = url;
			AuditReport.pUrlParameter= getlocationhost+"|&curProjectid=${userSession.curProjectId}&curPackageid=${userSession.curAccPackageId}&userId=${userSession.userId}";
	
			AuditReport.funOpenUrlFile(2);
			AuditReport.subRefreshData();
	
	<%
		}
	%>
	 
			try{
				AuditReport.subSetPageDirection(<%=bVpage%>);
				AuditReport.subDefaultExcelPageSet();
				AuditReport.subAddManuInfo();
			}catch(e){alert(e)}
	
	
	  orifilename=AuditReport.funGetTempDir()+orifilename;
	
	
		AuditReport.subChangeSheetName("sheet1","${saveasfilename}");
	
	}catch(e){
	  alert(e);
		   //出错了，说明控件安装不成功，导航到专门的安装界面
	  window.location="${pageContext.request.contextPath}/AS_SYSTEM/ocxsetup.htm";
	}
	
	try {
		AuditReport.subBeforeTerminate();
	}catch(e){
	}
}

</script>