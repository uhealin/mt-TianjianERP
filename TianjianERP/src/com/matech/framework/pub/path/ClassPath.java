package com.matech.framework.pub.path;

import java.io.UnsupportedEncodingException;

public class ClassPath {
	
	public String getClassAbsolutePath(String className) {

		if (!className.startsWith("/")) {
			className = "/" + className;
		}
		className = className.replace('.', '/');
		// className = className + ".class";

		java.net.URL classUrl = this.getClass().getResource(className);

		if (classUrl != null) {
			return classUrl.getFile();
		}
		else {
			return null;
		}
	}
	
	/**
	 * 取得.../WEB-INF/classes路径
	 * @return
	 * since 2007-7-3 上午10:23:45
	 */
	public static String getClassPath(){
		String cp="";
		if(ClassPath.class.getClassLoader().getResource("").getPath()!=null&&ClassPath.class.getClassLoader().getResource("").getPath().indexOf("/WEB-INF/classes")>-1){
			cp=ClassPath.class.getClassLoader().getResource("").getPath();
		}
		else if(ClassPath.class.getClassLoader().getResource("/").getPath()!=null&&ClassPath.class.getClassLoader().getResource("/").getPath().indexOf("/WEB-INF/classes")>-1){
			cp=ClassPath.class.getClassLoader().getResource("/").getPath();
		}
		else if(ClassPath.class.getResource("/").getPath()!=null&&ClassPath.class.getResource("/").getPath().indexOf("/WEB-INF/classes")>-1){
			cp=ClassPath.class.getResource("/").getPath();
		}
		try {
			cp =java.net.URLDecoder.decode(cp,"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return cp;
	}
	
	public static void main(String[] args){
	    System.out.println(ClassPath.getClassPath());
	}

}
