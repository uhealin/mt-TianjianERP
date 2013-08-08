<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ page import="java.sql.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="com.matech.framework.pub.autocode.DELUnid"%>
<%@ page import="com.matech.audit.pub.db.DBConnect"%>
<%@ page import="com.matech.audit.service.fileupload.MyFileUpload"%>
<%@ page import="com.matech.framework.pub.db.DbUtil" %>
<%@ page import="com.matech.framework.pub.sys.UTILSysProperty" %>
<%@ page import="com.matech.framework.pub.util.UTILString" %>

<%
	
	String flagOpt = request.getParameter("flagOpt");

	String localpath=UTILSysProperty.SysProperty.getProperty("远程临时目录映射后");
	String serverpath=UTILSysProperty.SysProperty.getProperty("远程临时目录");
	if (localpath==null || "".equals(localpath)){
		localpath="c:\\temp\\";
	}
	if (serverpath==null || "".equals(serverpath)){
		serverpath="c:/temp/";
	}
	//处理装载
	
	MyFileUpload myfileUpload = new MyFileUpload(request);
	String strTempFile =  DELUnid.getNumUnid() + ".txt";
	//正式上传文件
	myfileUpload.UploadFile(strTempFile,localpath);		
	System.out.println("文件上传成功dddddddddddddd：strTempFile111="+strTempFile);
	
	//把文件导入到数据库
	Connection conn = null;
	PreparedStatement ps = null;
	String strSql="";
	try {
		conn = new DBConnect().getConnect("");
		DbUtil db = new DbUtil(conn);
		
		String sql = "CREATE TABLE tt_"+flagOpt+" (flagOpt varchar(500) default NULL COMMENT '批次号',excelname varchar(500) default NULL COMMENT 'excel表的值') ENGINE=MyISAM DEFAULT CHARSET=gbk";
		db.execute(sql);
		
		BufferedReader reader = null;
		try {
			String line;
			File tempFile = new File(localpath+strTempFile);
			if(!tempFile.exists()) {
				throw new Exception("文件不存在,请检查!!");
			}
			
			reader = new BufferedReader(new FileReader(tempFile));
			boolean first = true;
			while ((line = reader.readLine()) != null) {
				String t1[] = UTILString.getVaribles(line);
				for(int i=0;t1!=null && i<t1.length;i++){
					//插入到数据库
					Map map = new HashMap();
					map.put("flagopt", flagOpt);
					map.put("excelname", t1[i]);
					db.add("tt_"+flagOpt, "", map);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(reader!=null) {
				reader.close();
			}
		}
	
		out.write("保存成功");
		
	}catch (Exception e) {
		out.println("执行导入失败！");
		System.out.println("执行EXCEL保存后台导入失败,sql="+strSql);
		e.printStackTrace();
	} finally {
		
		//关闭
		DbUtil.close(ps);
		DbUtil.close(conn);
		
		try{
			//删除文档
			//new File(localpath+strTempFile).delete();
		}catch(Exception e){}
	}
		

%>