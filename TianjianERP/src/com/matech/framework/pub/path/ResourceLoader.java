package com.matech.framework.pub.path;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class ResourceLoader {
	private ResourceLoader() {
	}

	/**
	 * 
	 * Description:用来加载本类所在的ClassPath的路径下的资源文件,可以使用../符号来加载classpath外部的资源。
	 * 
	 * @param relativePath
	 *            相当路径
	 * @return URL对象
	 */
	public static URL getResource(String relativePath) {
		URL resourceAbsoluteURL = null;
		try {
			relativePath = getStringForNum(ResourceLoader.class.getName()
					.split("\\.").length - 1, "../")
					+ relativePath;
			if (relativePath.indexOf("../") < 0) {
				return ResourceLoader.class.getResource(relativePath);
			}
			String classPath = ResourceLoader.class.getResource("").toString();
			if (relativePath.substring(0, 1).equals("/")) {
				relativePath = relativePath.substring(1);
			}
			String wildcardString = relativePath.substring(0,
					relativePath.lastIndexOf("../") + 3);
			relativePath = relativePath.substring(relativePath
					.lastIndexOf("../") + 3);
			int containSum = containSum(wildcardString, "../");
			classPath = cutLastString(classPath, "/", containSum);
			String resourceAbsolutePath = classPath + relativePath;
			resourceAbsoluteURL = new URL(resourceAbsolutePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resourceAbsoluteURL;
	}

	public File getResourceFile(String relativePath) {
		try {
			return new File(getResource(relativePath).getFile());
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 取得本类所在的classpath下面的资源文件,可以使用../符号来加载classpath外部的资源。
	 * 
	 * @param relativePath
	 *            相当路径
	 * @return 输入流
	 */
	public static InputStream getStream(String relativePath) {
		try {
			return getStream(getResource(relativePath));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * Description:取得本类所在的classpath下面的Properties文件,可以使用../符号来加载classpath外部的资源。
	 * 
	 * @param resource
	 *            相当路径
	 * @return Properties 对象
	 */
	public static Properties getProperties(String resource) {
		Properties properties = new Properties();
		InputStream in = null;
		try {
			in = getStream(resource);
			properties.load(in);
			return properties;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/***************************************************************************
	 * 
	 * /** 计算字符串source包含dest的数目
	 * 
	 * @param source
	 * @param dest
	 * @return source中包含dest的数目
	 */
	private static int containSum(String source, String dest) {
		int containSum = 0;
		int destLength = dest.length();
		while (source.indexOf(dest) >= 0) {
			containSum = containSum + 1;
			source = source.substring(destLength);
		}
		return containSum;
	}

	/**
	 * 
	 * Description:通过url取得流
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private static InputStream getStream(URL url) {
		try {
			if (url != null)
				return url.openStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 字符串source从后向前去掉num个字符串dest
	 * 
	 * @param source
	 * @param dest
	 * @param num
	 * @return
	 */
	private static String cutLastString(String source, String dest, int num) {
		for (int i = 0; i < num; i++)
			source = source.substring(0,
					source.lastIndexOf(dest, source.length() - 2) + 1);
		return source;
	}

	/**
	 * 
	 * Description:将指定字符串str进行num次连接
	 * 
	 * @param num
	 * @param str
	 * @return
	 */
	private static String getStringForNum(int num, String str) {
		String ret = "";
		for (; num > 0; num--)
			ret += str;
		return ret;
	}

	public static void main(String[] args) {
		System.out.println(ResourceLoader.getResource("../../").getPath());
	}
}