/*
 * 创建日期 2007-11-26
 *
 */
package com.matech.framework.pub.path;

import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

/**
 * 获取系统相关路径
 * @author Administrator
 *
 */
public class Path {    
    public static String getPath(String url,HttpServletRequest request){
        return url.replaceAll("http://" + request.getServerName() + ":" + request.getServerPort(), "");
    }
    
    public static String getRootPath(){
        String classPath = ClassPath.getClassPath();
        return  classPath.substring(0,classPath.indexOf("WEB-INF"));
    }
	/**
	 * 得到工程目录下WEB-INF/classes的路径
	 * 
	 * @return
	 */
	public static String getClassRoot() {
		URL url = null;
		try {
			url = Path.class.getClassLoader().getResource("");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return url.getPath();
	}

	/**
	 * 得到工程目录下WEB-INF的路径
	 * 
	 * @return
	 */
	public static String getWebInfoPath() {
		String path = getCurClsPath(null);
		if (path != null)
			return path.substring(0, path.indexOf("classes"));
		return null;
	}

	/**
	 * 得到工程目录下WEB-INF的上一级目录，即war包的路径
	 * 
	 * @return
	 */
	public static String getWarPath() {
		String path = ResourceLoader.getResource("").getPath() + "../../";
		System.out.println("系统部署路径：" + path);
		return path;
	}

	/**
	 * 得到当前class的路径
	 * 
	 * @return
	 */
	public static String getCurClsPath(Class cls) {
		URL url = null;
		try {
			if (cls != null)
				url = cls.getClass().getResource("");
			else
				url = Path.class.getResource("");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return url.getPath();
	}

	/**
	 * 得到当前war的路径
	 * 
	 * @return
	 */
	public static String getWarPath(ServletConfig config) {
		String path = config.getServletContext().getRealPath("/");
		return path;
	}    
}
