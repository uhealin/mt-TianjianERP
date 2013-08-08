package com.matech.audit.service.user;


import java.io.File;

import javax.servlet.http.HttpServletRequest;

public class Foder {
	
	private String dirName = "" ; 
	private HttpServletRequest req ;
	
	public Foder(String dirName,HttpServletRequest req) {
		this.dirName = dirName ; 
		this.req = req ;
	}
	
	
	public String getDirPath() {
		
		String path = req.getSession().getServletContext().getRealPath("/") ;
		
		String dirPath = path+"oa\\"+dirName +"\\";
		
		File file = new File(dirPath);
		
		if(!file.exists()) {
			file.mkdir() ;
		}
		
		
		System.out.println(dirPath) ;
		
		return dirPath ;
		
	}
	
	public String createFoder(String createPath) {
		
		String path = req.getSession().getServletContext().getRealPath("/") ;
		
		String dirPath = path+createPath+"\\";
		
		File file = new File(dirPath);
		
		if(!file.exists()) {
			file.mkdir() ;
		}
		
		return dirPath ;
		
	}

}
