package com.matech.audit.service.toHTML;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ToHTML {
	
	
	public void convertToHtml(String url,String name,String path) {

		  String line = "";       
		  String totalStr = "";       
		  InputStream is = null ;
		  BufferedReader br = null ;
		  HttpURLConnection httpConn = null ;
		  try {
			  if(url!=null)   
			  {
				  URL cUrl = new URL(url) ;
				  httpConn = (HttpURLConnection)cUrl.openConnection() ;
				  httpConn.setRequestProperty("contentType", "UTF-8");
				  httpConn.connect() ;
				  is = httpConn.getInputStream() ;
				  br = new BufferedReader(new InputStreamReader(is,"UTF-8")) ;
				  
				  while((line = br.readLine()) != null) {
					  totalStr += line + "\n" ;
				  }
			  }
			  
			  File dirFile = new File(path) ;
			  if(!dirFile.exists()) {
				  dirFile.mkdirs() ;
			  }
			  //开始写入html文件
              File f = new File(path,name); 
              if(f.exists()) {
            	  //先删除原有的文件
            	  f.delete() ;
              }
               // 创建新文件 
              f.createNewFile() ;
              FileOutputStream fileWriter = new FileOutputStream(f);
	  		  OutputStreamWriter outStreamWriter = new OutputStreamWriter(fileWriter, "UTF-8");
	  		  PrintWriter print = new PrintWriter(outStreamWriter);
	  		  print.write(totalStr);
	  		  print.close();
		  }catch(Exception e) {
			  e.printStackTrace() ;
		  }finally {
				try {
					if(is !=null) is.close() ;
					if(br !=null) br.close() ;
				} catch (IOException e) {
					e.printStackTrace();
				}
			  
		  }
	}

}
