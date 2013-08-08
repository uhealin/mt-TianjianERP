package com.matech.audit.service.fileupload;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.matech.framework.pub.autocode.DELUnid;

/**
 * <p>
 * Title: 处理文件上传(新增和修改替换原有文件)的类
 * </p>
 * <p>
 * Description: 处理文件上传(新增和修改替换原有文件)的类
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006, 2008 MaTech Corporation.All rights reserved.
 * </p>
 * <p>
 * Company: Matech 广州铭太信息科技有限公司
 * </p>
 * 
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有， 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 * 
 * 贡献者团队: 铭太科技 - 研发中心，审计开发组
 * 
 * @author void 2008-6-7
 */
public class FileUpload {

	private String tempPath = "c:\\temp\\";

	private Map parameters = null;

	/**
	 * 获取的上传请求
	 */
	private HttpServletRequest request = null;

	/**
	 * 设置最多只允许在内存中存储的数据,单位:字节
	 */
	private int sizeThreshold = DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD;

	/**
	 * 设置允许用户上传文件大小,单位:字节
	 */
	private long sizeMax = 1000485760;

	private boolean showProcess = false;

	private String uploadBeanName = "";

	/**
	 * 构造方法
	 * 
	 * @param request
	 * @throws Exception
	 */
	public FileUpload(HttpServletRequest request) throws Exception {
		this.request = request;
	}

	/**
	 * 上传并保存文件到指定目录
	 * 
	 * @param fileName
	 *            这个参数如果这是为NULL或者""，则会使用上传文件的文件名作为文件名
	 * @param filePath
	 *            目录路径，这个参数设置为NULL，或者""，则会无条件在c:\temp\目录下新建一个临时目录（以避免并发冲突）；
	 * @return clientFilePath 客户端文件路径, fileName 文件名, filePath 文件路径
	 */
	public Map UploadFile(String fileName, String filePath) {

		Iterator iterator = getFileItems();
		// 检查临时目录是否存在,不存在就创建
		String temp = "";
		if (filePath == null || filePath.equals("")) {
			temp = this.tempPath + DELUnid.getNumUnid() + "\\";
		} else {
			temp = filePath;
		}

		if (!new File(temp).exists()) {
			new File(temp).mkdirs();
		}

		while (iterator.hasNext()) {
			FileItem fileItem = (FileItem) iterator.next();

			// 其他不是文件域的所有表单信息,会放到MAP中
			if (!fileItem.isFormField()) {
				// 上传的是文件信息

				String name = fileItem.getName();
				if ((name == null) || name.equals("")
						&& fileItem.getSize() == 0) {
					continue;
				}

				parameters.put("clientFilePath", name);
				parameters.put("clientFileName", getFileName(name));
				String mime = fileItem.getContentType();
				try {
					// 文件保存处理
					String filename = "";
					if (fileName == null || fileName.equals("")) {
						filename = this.getFileName(name); // 获取不带路径的文件名
					} else {
						filename = fileName;
					}

					// 先保存到临时目录
					String swapfile = temp + filename;
					File file = new File(swapfile);
					fileItem.write(file);

					// 把最终的文件名页放到MAP中
					parameters.put("fileName", filename);
					parameters.put("mime", mime);
					parameters.put("fileSize", file.length());
					// 只有一个上传文件，所有处理完后直接退出，不用考虑其他事项
					continue;
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {

				try {
					// 上传的是普通表单字域，这里只读出不做任何处理
					String fieldName = fileItem.getFieldName();

					String value = fileItem.getString("utf-8");
					parameters.put(fieldName, value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

		parameters.put("filePath", temp);

		return this.parameters;
	}

	/**
	 * 初始化并读取上传信息
	 * 
	 * @return
	 */
	private Iterator getFileItems() {
		// 定义一个HashMap，存放请求参数
		if (this.parameters == null) {
			parameters = new HashMap();
		}

		DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
		ServletFileUpload servletFileUpload = new ServletFileUpload(
				diskFileItemFactory);

		// 设置允许用户上传文件大小,单位:字节,10M
		servletFileUpload.setSizeMax(sizeMax);
		servletFileUpload.setHeaderEncoding("UTF-8");

		// 设置缓冲区大小，这里是4kb
		diskFileItemFactory.setSizeThreshold(sizeThreshold);
		
		servletFileUpload.setProgressListener(new AttachUploadProgressListener(request));

		Iterator iterator = null;

		// 读取上传信息
		try {
			List fileItems = servletFileUpload.parseRequest(request);
			// 处理上传项目
			// 依次处理每个上传的文件
			iterator = fileItems.iterator();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return iterator;
	}

	/**
	 * 从路径中获取单独文件名
	 * 
	 * @param filepath
	 * @return
	 */
	private String getFileName(String filepath) {
		String returnstr = filepath;
		int length = filepath.trim().length();

		filepath = filepath.replace('\\', '/');
		if (length > 0) {
			int i = filepath.lastIndexOf("/");
			if (i >= 0) {
				filepath = filepath.substring(i + 1);
				returnstr = filepath;
			}
		}
		return returnstr;
	}
	
	class AttachUploadProgressListener implements ProgressListener{
		private HttpServletRequest request = null;
		AttachUploadProgressListener(HttpServletRequest request){
			this.request = request;
		}
		
		public void update(long pBytesRead, long pContentLength, int pItems) {
			double percentage = ((double)pBytesRead/(double)pContentLength);
			request.getSession().setAttribute("uploadPercentage", percentage);
		}
	}
	
}
