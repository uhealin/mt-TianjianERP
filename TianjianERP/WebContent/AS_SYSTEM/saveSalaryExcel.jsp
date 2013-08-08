<%@ page contentType="text/html;charset=utf-8"%><%@ 
page import="java.sql.Connection"%><%@ 
page import="java.util.Map"%><%@ 
page import="java.io.File"%><%@ 
page import="com.matech.audit.pub.db.DBConnect"%><%@ 
page import="com.matech.framework.pub.db.DbUtil"%><%@ 
page import="com.matech.framework.pub.util.ASFuntion"%><%@ 
page import="com.matech.audit.service.fileupload.MyFileUpload"%><%@ 
page import="com.matech.audit.service.salary.SalaryService"%><%@ 
page import="com.matech.audit.service.manuscript.ManuFileService"%><%
	
	ASFuntion asf = new ASFuntion();
	
	String pch = asf.showNull(request.getParameter("pch"));
	
	Connection conn=null;
	
	try {
		conn = new DBConnect().getDirectConnect("");
		
		/**
		 * 上传文件处理段
		 */
		Map parameters = null;
	
		String uploadtemppath = "";
		String strFullFileName = "";
		
		String strFileName="pch.zip";
	
		MyFileUpload myfileUpload = new MyFileUpload(request);
		uploadtemppath = myfileUpload.UploadFile(strFileName, null);
		parameters = myfileUpload.getMap();
		System.out.println(parameters);
	
		uploadtemppath = (String) parameters.get("tempdir");
		strFullFileName = uploadtemppath+ strFileName;
		org.util.Debug.prtOut("strFullFileName=" + strFullFileName);
		
		uploadtemppath = (String) parameters.get("tempdir");
		if (uploadtemppath.equals("")){
			throw new Exception("工资文件上传失败");
		}
	
		//拷贝到指定目录
		SalaryService salaryService = new SalaryService(conn);
		String newfile=salaryService.getExeclDir()+"/"+pch+".zip";
		String sourcefile=uploadtemppath+strFileName;
		
		File file = new File(newfile);
		if(!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		
		ManuFileService mfs = new ManuFileService();
		mfs.copyFile(new File(sourcefile),file);
		
		out.println("工资数据装载成功");
			
	} catch (Exception e) {
		e.printStackTrace();
		out.println("工资数据装载失败:"+e.getMessage());
	} finally {
		DbUtil.close(conn);
	}
%>