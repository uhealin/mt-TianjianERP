<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" errorPage="/hasNoRight.jsp"%>
<%@ include file="/WEB-INF/Views/Sys_INCLUDE/include.jsp" %>
<%@page import="com.matech.framework.pub.autocode.DELUnid"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.sql.*"%>
<%@page import="com.matech.audit.service.fileupload.MyFileUpload"%>
<%@page import="com.matech.framework.pub.db.DBConnect"%>
<%@page import="com.matech.framework.service.excelupload.ExcelUploadService"%>
<%@page import="com.matech.audit.service.customer.connectcompanyExcelData"%>


<%
	Map parameters = null;
	String uploadtemppath = "";
	String strFullFileName = "";
	Connection conn = new DBConnect().getDirectConnect();
	String customerid = "";
	try {

		MyFileUpload myfileUpload = new MyFileUpload(request);
				
		String strTempFile =  DELUnid.getNumUnid() + ".xls";
				
		//正式上传文件
		uploadtemppath = myfileUpload.UploadFile(strTempFile,"c:\\temp\\");
		
		parameters = myfileUpload.getMap();
		customerid = (String) parameters.get("customerid");

		strFullFileName = uploadtemppath
				+ (String) parameters.get("filename");
		org.util.Debug.prtOut("strFullFileName=" + strFullFileName);
		uploadtemppath = (String) parameters.get("tempdir");
		if (uploadtemppath.equals(""))
			out.print("Error\n客户数据上传及预处理失败");
		else
			out.println("客户数据上传并分析成功!<br>正在加载，请等待<br><br><br>");

		// 分析帐套文件,取出帐套年份;

		out.println("预处理分析客户文件<br/>");
		out.flush();

	} catch (Exception e) {
		e.printStackTrace();
		out.println("无法联接数据库，请联系系统管理员，本次装载失败！<br>");
	}
	
	
//	初始化业务对象
	ExcelUploadService upload = null;
	connectcompanyExcelData ued = null;
	try {
		upload = new ExcelUploadService(conn,strFullFileName);
		ued = new connectcompanyExcelData(conn);
	} catch (Exception e) {
		e.printStackTrace();
		out.println("临时路径或者关联公司名称有误,请与系统管理员联系<br>");
	}

	
	try {
		out.println("正在分析EXCEL文件......");
		out.flush();
		upload.init();
		out.println("分析EXCEL文件完毕!<BR>");
		out.println("正在装载关联公司名称内容!......");
		out.flush();

		//创建临时表，并将Excel中的数据导入临时表中
		ued.newTable();
		
		String result="";
		upload.setExcelNum("");
		upload.setExcelString("关联公司名称");
		String[] exexlKmye = { "关联公司名称"};
		String[] tableKmye = { "connectcompanysname" };
		String[] exexlKmyeFixFields = {};
		String[] excelKmyeFixFieldValues = {};
		result=upload.LoadFromExcel("关联公司名称", "tt_k_connectcompanys", exexlKmye, tableKmye,
	            exexlKmyeFixFields, excelKmyeFixFieldValues);
		
		out.println("装载客户内容完毕!<BR>");

		out.flush();
		out.println("开始更新客户信息!......");
		out.flush();
		
		//将临时表的数据插入指定的数据表中
		ued.insertData(customerid);
		out.println("更新客户信息完毕!<BR>");
		if (result != null && result.length() > 0) {
			out.println("<br><br>装载非正常结果报告：<BR><font color='red'>");
			out.println(result);
			out.println("</font><br>");
		}
		//删除临时表
		ued.delTable();
		out.println("<hr>数据装载成功");
		%>
		
		<script language="javascript">
			alert("数据装载成功!");
			document.location.href="/AuditSystem/connectcompanys.do?chooseCustomer="+<%=customerid%>;
		</script>
		<%					
		
	} catch (Exception e) {
		e.printStackTrace();
		out.println("<font style=\"color:red\">装载处理出现错误:<br/>"+ "表页[关联公司名称]不存在，请修改！");
		out.println("<a href=\"/AuditSystem/connectcompanys.do?chooseCustomer=\"+customerid>返回装载页面</a>\"</font>");
	} finally {
		if (conn != null)conn.close();

	}

%>


