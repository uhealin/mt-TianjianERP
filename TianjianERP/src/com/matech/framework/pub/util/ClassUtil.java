package com.matech.framework.pub.util;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ClassUtil {
	/**
	 * 给一个接口，返回这个接口的所有实现类
	 * 
	 * @param c
	 * @return
	 */
	public static List getClassListByInterface(Class c) {
		List classList = getClassListByInterface(c, "");
		return classList;
	}

	/**
	 * 给一个接口，返回这个接口的所有实现类
	 * 
	 * @param c
	 * @return
	 */
	public static List getClassListByInterface(Class c, String packageName) {

		List classList = new ArrayList();

		// 如果不是一个接口，则不做处理
		if (c.isInterface()) {

			if ("".equals(packageName))
				packageName = c.getPackage().getName(); // 获得当前的包名

			try {
				Set allClass = getClasses(packageName); // 获得当前包下以及子包下的所有类

				Iterator it = allClass.iterator();

				while (it.hasNext()) {
					Class classes = (Class) it.next();

					// 判断是否是同一个接口
					if (c.isAssignableFrom(classes)) {
						if (!c.equals(classes)) { // 本身不加进去
							classList.add(classes);
						}
					}
				}

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		ComparatorClass comparatorClass = new ComparatorClass();
		Collections.sort(classList, comparatorClass);
		// classList.
		return classList;
	}

	/**
	 * 从一个包中查找出所有的类，在jar包中不能查找
	 * 
	 * @param packageName
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private static Set<Class> getClasses(String packageName)
			throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		Set classes = new HashSet();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes;
	}

	/**
	 * 查找类
	 * 
	 * @param directory
	 * @param packageName
	 * @return
	 * @throws ClassNotFoundException
	 */
	private static List<Class> findClasses(File directory, String packageName)
			throws ClassNotFoundException {
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file,
						packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName + '.' +

				file.getName().substring(0, file.getName().length() - 6)));
			}
		}
		return classes;
	}
	
	
	public static <A extends Annotation> A getAnnotation(Class cls,Class<A> annCls){
	     A ann=null;
	     Annotation[] anns=cls.getDeclaredAnnotations();
	     for(Annotation a : anns){
	    	 if(annCls.equals(a.getClass())){
	    		 ann=(A)a;
	    		 break;
	    	 }
	     }
	     return ann;
	}
}
   

class ComparatorClass implements Comparator {

	public int compare(Object arg0, Object arg1) {
		Class class1 = (Class) arg0;
		Class class2 = (Class) arg1;

		// 首先比较年龄，如果年龄相同，则比较名字
		return class1.getSimpleName().compareTo(class2.getSimpleName());
	}
}
