<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.matech.framework.pub.excel.*"%>
<%
  java.io.BufferedInputStream bis=null;
  java.io.BufferedOutputStream  bos=null;
  String filename=request.getParameter("filename");

  String urlpath =SaveAsExcel.getExeclTemplateDir()+"/"+filename;
System.out.println("urlpath=|"+urlpath);
  
  try{
     response.setContentType("application/x-msdownload");
     response.setHeader("Content-disposition","attachment; filename="+filename);
     bis =new java.io.BufferedInputStream(new java.io.FileInputStream(urlpath));
     bos=new java.io.BufferedOutputStream(response.getOutputStream());
     byte[] buff = new byte[2048];
     int bytesRead;
     while(-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
       bos.write(buff,0,bytesRead);
     }
     
     /*这句话可以让这个JSP执行不抛异常*/
     out.clear();
     out = pageContext.pushBody();
     
  }
  catch(Exception e){
    e.printStackTrace();
  }
  finally {
    if (bis != null)bis.close();
    if (bos != null)bos.close();
  }
%>