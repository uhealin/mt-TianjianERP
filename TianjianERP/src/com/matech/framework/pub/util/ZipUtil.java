package com.matech.framework.pub.util;

/**
 * 类<code>ZipUtil</code>包含了常见的文件压缩操作方法.
 * <p>主要用于文件到数据之间的压缩和解压</p>
 *
 * @see com.ASSys.work.zip
 * @since 1.0
 */
import java.io.*;

import org.apache.tools.zip.ZipOutputStream;

public class ZipUtil {
	public ZipUtil() {
	}
	 
	public void zip(String zipDirectory, String zipFile) throws Exception {
		File file = new File(zipDirectory);
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
		out.setEncoding("GBK");
		zip(out, file, "");
		out.close();
	}

	/**
	 * 写文件将参数bytes数据写入参数 file 中
	 * @param file File
	 * @param bytes byte[]
	 * @throws Exception
	 */
	public void writeFile(java.io.File file, byte[] bytes) throws Exception {
		java.io.BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(file));
		bos.write(bytes);
		bos.flush();
		bos.close();
	}

	/**
	 * 在指定路径创建文件，并将参数bytes数据写入此文件中
	 * @param fileFullName String
	 * @param bytes byte[]
	 * @throws Exception
	 */
	public void writeFile(String fileFullName, byte[] bytes) throws Exception {
		java.io.BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(fileFullName)));
		bos.write(bytes);
		bos.flush();
		bos.close();
	}

	/**
	 * 压缩byte[] 使用GZIPOutputStream
	 * 注意 baos和zos共用一个流
	 * @param bytes byte[] 需要压缩的数据
	 * @return byte[]      压缩后的数据
	 * @throws Exception
	 */
	public byte[] gzipBytes(byte[] bytes) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		java.util.zip.GZIPOutputStream zos = new java.util.zip.GZIPOutputStream(baos);

		byte[] result = null;
		zos.write(bytes);
		zos.close();
		result = baos.toByteArray();
		baos.close();
		return result;
	}

	/**
	 * 解压缩byte[] 使用GZIPInputStream
	 * 注意 baos和zos共用一个流
	 * @param bytes byte[]  压缩数据
	 * @return byte[]       解压后的数据
	 * @throws Exception
	 */
	public byte[] ungzipBytes(byte[] bytes) throws Exception {
		int realLength;//实际的长度
		final int length = 1024;//每次读取的长度
		byte[] buffer = new byte[length];
		byte[] result = null;
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		java.util.zip.GZIPInputStream zos = new java.util.zip.GZIPInputStream(bais);

		while ((realLength = zos.read(buffer, 0, length)) != -1) {
			baos.write(buffer, 0, realLength);
		}
		result = baos.toByteArray();
		baos.close();
		bais.close();
		return result;
	}

	/**
	 * 将file类型数据转换成byte[]类型。
	 * @param file File
	 * @return byte[]
	 */
	public byte[] fileToByteArray(java.io.File file) {

		java.io.BufferedInputStream bis = null;
		java.io.ByteArrayOutputStream baos = null;
		byte[] result = null;
		try {
			final int length = 1024;
			int realLength;
			byte[] buffer = new byte[length];

			bis = new java.io.BufferedInputStream(new java.io.FileInputStream(
					file));
			baos = new java.io.ByteArrayOutputStream();

			while ((realLength = bis.read(buffer, 0, length)) != -1) {
				baos.write(buffer, 0, realLength);
			}
			result = baos.toByteArray();
			return result;

		} catch (java.io.IOException e) {
			e.printStackTrace();
			return result;
		} finally {
			try {
				if (bis != null)
					bis.close();
				if (baos != null)
					baos.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * @param f
	 * @param out
	 * @param base
	 * @throws Exception
	 */
	private void zip(ZipOutputStream out, File f, String base) throws Exception {

		if (f.isDirectory()) {
			File[] fl = f.listFiles();
			out.putNextEntry(new org.apache.tools.zip.ZipEntry(base + "/"));
			base = base.length() == 0 ? "" : base + "/";
			for (int i = 0; i < fl.length; i++) {
				zip(out, fl[i], base + fl[i].getName());
			}
		} else {
			out.putNextEntry(new org.apache.tools.zip.ZipEntry(base));
			FileInputStream in = new FileInputStream(f);
			int b;
			while ((b = in.read()) != -1) {
				out.write(b);
			}
			in.close();
		}
	}

}
