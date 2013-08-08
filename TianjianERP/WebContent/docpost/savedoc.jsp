<%@ page contentType="text/html; charset=utf-8" language="java" import="java.util.*,java.io.*,java.sql.*" errorPage="" %>
<%@ page import="java.text.MessageFormat"%>
<%@ page import="com.matech.audit.service.manuscript.ManuFileService"%>
<%@ page import="com.zhuozhengsoft.ZSOfficeX.*"%>
<%request.setCharacterEncoding("utf-8");%>
<%response.setCharacterEncoding("utf-8");%>
<%@page pageEncoding="utf-8"%>
<%!
	private File getWordFile(String fileName) throws Exception{
		String path = "d:/docpost/";
		File fileDir=new File(path),
		fileWord=new File(path+"/"+fileName),
		fileWordTemplate=new File(path+"/template.doc");
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

	SaveDocObj   SaveObj  = new SaveDocObj(request, response);
	//SaveObj.FileName; //获取文件名
	//SaveObj.FileExtName;  //获取文件扩展名
	//SaveObj.FileSize;  //获取文件大小，以字节为单位
	//SaveObj.getFileBytes();  //获取文件二进制流，可以保存到数据库字段
	String uuid=request.getParameter("uuid");
	System.out.println("============uuid="+uuid);
	try{
		//
		String fiename=getWordFile(uuid+".doc").getPath();
		System.out.println("============filename="+fiename);
		//SaveObj.saveToFile(request.getRealPath("doc/") + "\\" + SaveObj.FileName);  // saveToFile 的参数是文档的物理绝对路径，例如: “D:\\doc\\abc.doc”
		SaveObj.saveToFile(fiename);  
		SaveObj.returnOK();
	}
	finally {
		SaveObj.close();
	}
		
	
%>