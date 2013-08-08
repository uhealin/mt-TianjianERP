package com.matech.audit.service.datamanage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import com.matech.framework.pub.sys.UTILSysProperty;

public class DataZip {

	private String comment = "";

	private final static String TEMP_PATH = UTILSysProperty.SysProperty
			.getProperty("系统临时目录");

	private List deleteList = null;

	public List getDeleteList() {
		return deleteList;
	}

	public void setDeleteList(List deleteList) {
		this.deleteList = deleteList;
	}

	/**
	 * 先拷贝，后压缩
	 * 
	 * @param inputFileName
	 *            需要压缩的根目录,例如 D:/aaa/
	 * @param outputFileName
	 * @param filter
	 *            需要压缩的子目录列表
	 * @throws Exception
	 */
	public void zipByCopyFirst(String inputFileName, String outputFileName,
			List filter) throws Exception {
		if (filter != null && !filter.isEmpty()) {
			// 拷贝文件到临时目录
			String oldPath = "";
			String newPath = TEMP_PATH + getCurrentDateTime() + "/";

			File oldFile = null;
			File newFile = null;

			try {

				for (int i = 0; i < filter.size(); i++) {
					oldPath = "" + filter.get(i);

					if (oldPath != "") {
						oldFile = new File(inputFileName + oldPath);
						newFile = new File(newPath + oldPath);

						System.out
								.println("待压缩目录：" + newFile.getAbsoluteFile());
						copyFiles(oldFile, newFile);
					}
				}

				// 删除无用的文件再压缩
				try {
					if (this.deleteList != null) {
						File tempFile = null;
						String filePath = "";
						for (int i = 0; i < deleteList.size(); i++) {
							filePath = newPath + deleteList.get(i);
							tempFile = new File(filePath);
							System.out.println("删除文件夹：" + filePath);
							deleteFile(tempFile);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				zip(newPath, outputFileName);
			} catch (Exception e) {
				e.printStackTrace();

				// 失败后采用第二种方法
				System.out.println("采用第一种压缩方法失败：" + e.getMessage());
				zip(inputFileName, outputFileName, filter);
			} finally {
				deleteFile(new File(newPath));
			}

		} else {
			zip(inputFileName, outputFileName, filter);
		}
	}

	/**
	 * 压缩文件夹下的所有文件
	 * 
	 * @param inputFileName
	 *            等待压缩的文件或者目录
	 * @param outputFileName
	 *            压缩完成的包
	 * @throws Exception
	 */
	public void zip(String inputFileName, String outputFileName)
			throws Exception {

		long startTime = System.currentTimeMillis(); // 开始解压时间

		System.out.println("开始压缩:" + inputFileName);

		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
				outputFileName));
		zip(out, new File(inputFileName), "");
		long time = System.currentTimeMillis() - startTime;
		System.out.println("压缩完成,生成文件:" + outputFileName + ",耗时" + time + "ms");
		out.close();
	}

	/**
	 * 压缩文件夹下的所有与filter匹配的文件
	 * 
	 * @param filter
	 *            待压缩文件列表
	 * @param inputFileName
	 *            等待压缩的文件或者目录
	 * @param outputFileName
	 *            压缩完成的包
	 * @throws Exception
	 */
	public void zip(String inputFileName, String outputFileName, List filter)
			throws Exception {

		long startTime = System.currentTimeMillis(); // 开始解压时间

		System.out.println("开始压缩:" + inputFileName);

		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
				outputFileName));
		zip(out, new File(inputFileName), "", filter);
		long time = System.currentTimeMillis() - startTime;
		System.out.println("压缩完成,生成文件:" + outputFileName + ",耗时" + time + "ms");
		out.close();
	}

	/**
	 * 压缩文件
	 * 
	 * @param inputFileName
	 * @param os
	 * @throws Exception
	 */
	public void zip(String inputFileName, OutputStream os) throws Exception {

		long startTime = System.currentTimeMillis(); // 开始解压时间

		System.out.println("开始压缩:" + inputFileName);

		ZipOutputStream out = new ZipOutputStream(os);
		zip(out, new File(inputFileName), "");
		long time = System.currentTimeMillis() - startTime;
		System.out.println("压缩完成,耗时" + time + "ms");
		out.close();
	}

	/**
	 * 压缩filter匹配的文件夹
	 * 
	 * @param inputFileName
	 * @param os
	 * @param filter
	 * @throws Exception
	 */
	public void zip(String inputFileName, OutputStream os, List filter)
			throws Exception {

		long startTime = System.currentTimeMillis(); // 开始解压时间

		System.out.println("开始压缩:" + inputFileName);

		ZipOutputStream out = new ZipOutputStream(os);
		zip(out, new File(inputFileName), "", filter);
		long time = System.currentTimeMillis() - startTime;
		System.out.println("压缩完成,耗时" + time + "ms");
		out.close();
	}

	private void zip(ZipOutputStream out, File f, String base) throws Exception {
		if (!f.exists()) {
			throw new Exception("文件或者目录不存在,请重新选择");
		}

		if (f.isDirectory()) {

			File[] fl = f.listFiles();

			out.putNextEntry(new ZipEntry(base + "/"));
			base = base.length() == 0 ? "" : base + "/";

			for (int i = 0; i < fl.length; i++) {
				zip(out, fl[i], base + fl[i].getName());
			}

		} else {
			ZipEntry ze = new ZipEntry(base);
			ze.setComment(comment);
			out.putNextEntry(ze);
			FileInputStream in = new FileInputStream(f);

			int realLength; // 实际的长度
			final int BUFFER = 1024; // 每次读取的长度
			byte[] buffer = new byte[BUFFER];

			// 写入数据
			while ((realLength = in.read(buffer, 0, BUFFER)) != -1) {
				out.write(buffer, 0, realLength);
			}

			in.close();
		}
	}

	private void zip(ZipOutputStream out, File f, String base, List filter)
			throws Exception {
		if (!f.exists()) {
			throw new Exception("文件或者目录不存在,请重新选择");
		}

		if (f.isDirectory()) {

			File[] fl = f.listFiles();

			if (isExist(base, filter)) {
				out.putNextEntry(new ZipEntry(base + "/"));
			}

			base = base.length() == 0 ? "" : base + "/";

			for (int i = 0; i < fl.length; i++) {
				zip(out, fl[i], base + fl[i].getName(), filter);
			}

		} else {

			// 判断文件是不是在要压缩的目录
			if (isExist(base, filter)) {
				ZipEntry ze = new ZipEntry(base);
				ze.setComment(comment);
				out.putNextEntry(ze);
				FileInputStream in = new FileInputStream(f);

				int realLength; // 实际的长度
				final int BUFFER = 1024; // 每次读取的长度
				byte[] buffer = new byte[BUFFER];

				// 写入数据
				while ((realLength = in.read(buffer, 0, BUFFER)) != -1) {
					out.write(buffer, 0, realLength);
				}

				in.close();
			}

		}
	}

	/**
	 * 判断list中的某个元素是否存在在base字符串中
	 * 
	 * @param base
	 * @param list
	 * @return
	 */
	public boolean isExist(String base, List list) {
		if (list != null && !list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				if (base.indexOf((String) list.get(i)) >= 0) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 解压文件
	 * 
	 * @param filePath
	 *            待解压的文件,要绝对路径,例如"c:\\zz.zip"
	 * @param outputPath
	 *            存放解压出来文件的目录,例如"c:\\temp"
	 * @param deleteFile
	 *            是否删除原文件
	 * @throws Exception
	 */
	public void unZip(String filePath, String outputPath, boolean deleteFile)
			throws Exception {

		final int BUFFER = 1024;

		try {
			File file = new File(filePath);

			if (!file.exists()) {
				throw new Exception("文件不存在,请重新选择");
			}

			File outputFile = new File(outputPath);
			if (!outputFile.exists()) {
				outputFile.mkdirs();
			}
			long startTime = System.currentTimeMillis(); // 开始解压时间

			System.out.println("开始解压:" + filePath);

			BufferedOutputStream dest = null;
			FileInputStream fis = new FileInputStream(file);
			ZipInputStream zis = new ZipInputStream(
					new BufferedInputStream(fis));

			java.util.zip.ZipEntry entry;

			while ((entry = zis.getNextEntry()) != null) {

				if (entry.isDirectory()) {
					File f = new File(outputPath + File.separator
							+ entry.getName());
					f.mkdirs();
					continue;
				} else {
					File f = new File(outputPath + File.separator
							+ entry.getName());
					f.getParentFile().mkdirs();
				}
				int count;
				byte data[] = new byte[BUFFER];
				FileOutputStream fos = new FileOutputStream(outputPath
						+ File.separator + entry.getName());
				dest = new BufferedOutputStream(fos, BUFFER);

				while ((count = zis.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, count);
				}

				dest.flush();
				dest.close();
			}
			if (zis != null) {
				zis.closeEntry();
				zis.close();
			}

			// deleteFile为true,则删除文件
			if (deleteFile) {
				file.delete();
			}

			long time = System.currentTimeMillis() - startTime;
			System.out.println("解压完成:" + filePath + ",耗时" + time + "ms");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 解压文件,支持压缩包里的中文目录和文件名
	 * 
	 * @param filePath
	 *            待解压的文件,要绝对路径,例如"c:\\zz.zip"
	 * @param outputPath
	 *            存放解压出来文件的目录,例如"c:\\temp"
	 * @param deleteFile
	 *            是否删除原文件
	 * @throws Exception
	 */
	public void unZipCHN(String filePath, String outputDirectory,
			boolean deleteFile) throws Exception {
		try {
			File file = new File(filePath);

			if (!file.exists()) {
				throw new Exception("文件不存在,请重新选择");
			}

			File outputFile = new File(outputDirectory);
			if (!outputFile.exists()) {
				outputFile.mkdirs();
			}

			long startTime = System.currentTimeMillis(); // 开始解压时间

			System.out.println("开始解压:" + filePath);

			ZipFile zipFile = new ZipFile(filePath);
			Enumeration e = zipFile.getEntries();
			ZipEntry zipEntry = null;

			while (e.hasMoreElements()) {

				zipEntry = (ZipEntry) e.nextElement();

				if (zipEntry.isDirectory()) {
					String name = zipEntry.getName();
					name = name.substring(0, name.length() - 1);
					String path = outputDirectory + File.separator + name;

					File f = new File(path);
					System.out.println("创建文件夹：" + path + "," + f.mkdirs());
				} else {
					File f = new File(outputDirectory + File.separator
							+ zipEntry.getName());
					f.getParentFile().mkdirs();
					f.createNewFile();
					InputStream in = zipFile.getInputStream(zipEntry);
					FileOutputStream out = new FileOutputStream(f);
					int c;
					byte[] by = new byte[1024];
					while ((c = in.read(by)) != -1) {
						out.write(by, 0, c);
					}
					out.close();
					in.close();
				}
			}

			long time = System.currentTimeMillis() - startTime;
			System.out.println("解压完成:" + filePath + ",耗时" + time + "ms");

			if (zipFile != null) {
				zipFile.close();
			}

			// deleteFile为true,则删除文件
			if (deleteFile) {
				file.delete();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public String getComment() {
		return comment;
	}

	/**
	 * 设置zip文件注释
	 * 
	 * @param comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * 返回zip文件的注释
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getZipComment(String filePath) {
		String comment = "";
		try {
			ZipFile zf = new ZipFile(filePath);
			Enumeration e = zf.getEntries();

			while (e.hasMoreElements()) {
				ZipEntry ze = (ZipEntry) e.nextElement();

				comment = ze.getComment();
				if (comment != null && !comment.equals("")
						&& !comment.equals("null")) {
					break;
				}
			}

			zf.close();
		} catch (Exception e) {
			System.out.println("获取zip文件注释信息失败:" + e.getMessage());
		}

		return comment;
	}

	/**
	 * 拷贝文件夹或者文件 例如：copyFiles(new File("d:/111"), new File("e:/444")); 将 d:/111
	 * 目录下的所有文件及文件夹拷贝到 e:/444 目录下
	 * 
	 * @param oldDir
	 * @param newDir
	 * @return
	 * @throws Exception
	 */
	public void copyFiles(File oldDir, File newDir) throws Exception {
		if (oldDir.isDirectory()) {
			newDir.mkdirs();
			File[] files = oldDir.listFiles();
			for (int i = 0; i < files.length; i++) {
				File file = new File(newDir.getPath() + "/"
						+ files[i].getName());
				copyFiles(files[i], file);
			}
		} else {
			FileInputStream input = new FileInputStream(oldDir);
			FileOutputStream output = new FileOutputStream(newDir);
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = input.read(b)) != -1) {
				output.write(b, 0, len);
			}
			output.flush();
			output.close();
			input.close();
		}
	}

	/**
	 * 获取文件列表
	 * 
	 * @param file
	 *            需要遍历的文件夹
	 * @param list
	 *            返回LIST里面放着Map,map有多个key：fileName,fileFullPath,fileSize
	 * @param fileExt
	 *            文件扩展名,如果需要全部,请传一个空字符串的数组
	 * @throws Exception
	 */
	public static void getFileList(File file, List list, String[] fileExt)
			throws Exception {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				getFileList(files[i], list, fileExt);
			}
		} else {
			Map map = new HashMap();
			String fileName = file.getName();

			if (fileExt != null) {

				for (int i = 0; i < fileExt.length; i++) {
					if (fileName.toLowerCase()
							.indexOf(fileExt[i].toLowerCase()) > -1) {
						map.put("fileName", fileName);
						map.put("fileFullPath", file.getAbsoluteFile());
						map.put("fileSize", String.valueOf(file.length()));

						list.add(map);
					}
				}
			}
		}
	}

	/**
	 * 获得当前时间日期
	 * 
	 * @return
	 */
	public synchronized static String getCurrentDateTime() {
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date currentdate = new Date();
		return ("" + dateformat.format(currentdate));
	}

	/**
	 * 删除目录和目录下的所有文件和文件夹
	 * 
	 * @param file
	 * @throws Exception
	 */
	public static void deleteFile(File file) {

		try {
			if (file.exists()) {
				// 如果是文件,则直接删除
				if (file.isFile()) {
					file.delete();
				} else {
					// 如果是目录,则先删除里面的文件
					File[] filelist = file.listFiles();

					// 如果目录下有文件或者目录
					if (filelist.length != 0) {
						// 遍历目录,先删除目录下的文件
						for (int i = 0; i < filelist.length; i++) {
							// 如果是文件夹,则递归调用
							if (filelist[i].isDirectory()) {
								deleteFile(filelist[i]);
							} else {
								filelist[i].delete();
							}
						}
						file.delete();
					} else {
						file.delete();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 使用ant zip压缩 压缩一个给定目录下的所有文件
	 * @param zipDirectory 压缩目录的路径
	 * @param zipFile  输缩文件输出路径
	 * @throws Exception
	 */
	public void dozip(String zipDirectory, String zipFile)
			throws Exception {
		File file = new File(zipDirectory) ;
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
		out.setEncoding("GBK") ;
		dozip(out, file, "");
        out.close();
	}
	
	/**
	 * @param f
	 * @param out
	 * @param base
	 * @throws Exception
	 */
	private void dozip(ZipOutputStream out,File f,String base)
			throws Exception {
		
		if (f.isDirectory()) {
            File[] fl = f.listFiles();
            out.putNextEntry(new org.apache.tools.zip.ZipEntry(base + "/"));
            base = base.length() == 0 ? "" : base + "/";
            for (int i = 0; i < fl.length; i++) {
            	zip(out, fl[i], base + fl[i].getName());
            }
        }else {
            out.putNextEntry(new org.apache.tools.zip.ZipEntry(base));
            FileInputStream in = new FileInputStream(f);
            int b;
            while ((b = in.read()) != -1) {
             out.write(b);
            }
            in.close();
        }
	}

	public static void main(String[] args) throws Exception {

		// String path = "D:/Project/AuditSystem3.0/Database/data/";
		//
		// System.out.println(path);
		// List list = new ArrayList();
		// list.add("Data/backup_asdb_20081229171521"); // 系统临时库
		// list.add("Data/backup_cusomer_asdb_20081229171521"); // 客户临时库
		// list.add("ManuScriptData/100008/2008101598");
		//
		// new DataZip().zipByCopyFirst(path + "../", "c:/ssss.zip", list);
		/*
		List list = new ArrayList();
		String[] fileExt = { ".zip", ".myd" };
		getFileList(new File("C:/temp"), list, fileExt);

		System.out.println(list.size());
		Map map = new HashMap();
		for (int i = 0; i < list.size(); i++) {
			map = (Map) list.get(i);
			System.out.println(map.get("fileName"));
			System.out.println(map.get("fileFullPath"));
			System.out.println(map.get("fileSize"));
		}*/
		
		new DataZip().dozip("c:/temp","c:/aa.zip") ;
	}
}
