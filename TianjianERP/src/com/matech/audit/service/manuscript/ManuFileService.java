package com.matech.audit.service.manuscript;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.matech.audit.service.customer.CustomerService;
import com.matech.audit.service.datamanage.BackupUtil;
import com.matech.audit.service.datamanage.DataZip;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.pub.util.ZipUtil;

/**
 * <p>Title: 底稿文件操作类</p>
 * <p>Description: 对底稿进行文件保存</p>
 * <p>Copyright: Copyright (c) 2006, 2008 MaTech Corporation.All rights reserved. </p>
 * <p>Company: Matech  广州铭太信息科技有限公司</p>
 *
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有，
 * 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 *
 * 贡献者团队:
 *     铭太科技 - 研发中心，审计开发组
 *
 * @author void
 * 2007-6-26
 */
public class ManuFileService {

	private Connection conn = null;

	/**
	 * 存放底稿文件的目录
	 */
	private static String MANU_SCRIPT_PATH = "../ManuScriptData/";

	/**
	 * 存放模版文件的目录
	 */
	private static String TEMPLATE_PATH = "../TemplateData/";

	/**
	 * 底稿作业指导
	 */
	private static String TEMPLATEHELP_PATH = "../TemplateHelp/";
	
	/**
	 * 文件保存类型
	 */
	private final static String FILE_TYPE = "";

	static {
		//获得程序发布路径
		String dataBasePath = BackupUtil.getDATABASE_PATH();
		dataBasePath += "../";

		MANU_SCRIPT_PATH = dataBasePath + "ManuScriptData/";
		TEMPLATE_PATH = dataBasePath + "TemplateData/";
		
		TEMPLATEHELP_PATH = dataBasePath + "TemplateHelp/";	
	}

	/**
	 * 构造方法
	 */
	public ManuFileService(Connection conn) {
		try {
			DbUtil.checkConn(conn);
			this.conn = conn;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ManuFileService() {

	}


	/**
	 * 拷贝目录,将目录压缩成zip包再解压到其他地方
	 * @param oldDir
	 * @param newDir
	 * @throws Exception
	 */
	public void copyDir(File oldDir, File newDir) throws Exception {
		//压缩包的文件名,为避免重复,使用当前日期时间
		String fileName = BackupUtil.getDateTime("yyyyMMddHHmmss");

		//压缩包路径
		String filePath = newDir.getPath() + "/" + fileName;

		//如果目标文件夹不存在,则创建
		if (!newDir.exists()) {
			newDir.mkdirs();
		}

		//压缩oldDir目录下的全部文件
		List list = new ArrayList();
		list.add("");
		new DataZip().zip(oldDir.getPath(), filePath, list);

		//解压到目标文件夹,并删除原来的压缩文件
		new DataZip().unZip(filePath, newDir.getPath(), true);
	}

	/**
	 * 拷贝文件
	 * @param oldFile
	 * @param newFile
	 * @return
	 * @throws Exception
	 */
	public String copyFile(File oldFile, File newFile) throws Exception {
		
		try {
			if(newFile.exists()) {
				newFile.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		FileInputStream input = new FileInputStream(oldFile);
		FileOutputStream output = new FileOutputStream(newFile);
		byte[] b = new byte[1024 * 5];
		int len;
		while ((len = input.read(b)) != -1) {
			output.write(b, 0, len);
		}
		output.flush();
		output.close();
		input.close();

		return newFile.getPath();
	}

	/**
	 * 拷贝文件夹或者文件
	 * 例如：copyFiles(new File("d:/111"), new File("e:/444"));
	 * 		将 d:/111 目录下的所有文件及文件夹拷贝到 e:/444 目录下
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
	 * 拷贝文件或者目录
	 * 例如：copyFiles("d:/111", "e:/444");
	 * 		将 d:/111 目录下的所有文件及文件夹拷贝到 e:/444 目录下
	 * @param oldPath	源文件夹
	 * @param newPath	目标文件夹
	 * @throws Exception
	 */
	public void copyFiles(String oldPath, String newPath) throws Exception {
		File oldFile = new File(oldPath);
		File newFile = new File(newPath);

		if (!oldFile.exists()) {
			throw new Exception("将要拷贝的目录不存在");
		}

		copyFiles(oldFile, newFile);
	}


	/**
	 * 删除目录和目录下的所有文件和文件夹
	 * @param file
	 * @throws Exception
	 */
	public static void deleteFile(File file) throws Exception {
		if (file.exists()) {
			//如果是文件,则直接删除
			if (file.isFile()) {
				file.delete();
			} else {
				//如果是目录,则先删除里面的文件
				File[] filelist = file.listFiles();

				//如果目录下有文件或者目录
				if (filelist.length != 0) {
					//遍历目录,先删除目录下的文件
					for (int i = 0; i < filelist.length; i++) {
						//如果是文件夹,则递归调用
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

	}

	/**
	 * 重命名文件
	 * @param path
	 * @param oldFileName
	 * @param newFileName
	 * @throws Exception
	 */
	public String renameFile(String path, String oldFileName, String newFileName)
			throws Exception {
		File oldFile = new File(path + "/" + oldFileName);
		File newFile = new File(path + "/" + newFileName);
		oldFile.renameTo(newFile);

		return newFile.getPath();
	}


	/**
	 * 更改文件的后缀
	 * @param fileName
	 * @throws Exception
	 */
	private String replaceFileType(String fileName, String fileType) {
		if (fileName.lastIndexOf(".") >= 0) {
			fileName = fileName.substring(0, fileName.lastIndexOf("."))
					+ fileType;
		}

		return fileName;
	}

	/**
	 * 保存为文件
	 * @param fileName
	 * @throws Exception
	 */
	public void saveFile(Map fileInfo) throws Exception {

		byte[] fileData = (byte[]) fileInfo.get("fileData"); //文件数据
		String fileName = (String) fileInfo.get("fileName"); //文件名
		String fileDir = (String) fileInfo.get("fileDir"); //文件目录
		String fileTypePath = (String) fileInfo.get("fileTypePath"); //文件路径

		fileName = replaceFileType(fileName, FILE_TYPE); //更改文件后缀

		String fileFullPath = fileTypePath + fileDir + "/" + fileName;
		File filePath = new File(fileTypePath + fileDir);

		//检查目录是否存在,不存在就创建
		if (!filePath.exists()) {
			filePath.mkdirs();
		}
		
		//无条件删同名文件
		try {
			File file = new File(fileFullPath);
			
			if(file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 

		RandomAccessFile raf = null;
		try {
			Debug.print(fileFullPath);
			//写入文件
			raf = new RandomAccessFile(fileFullPath, "rw");
			raf.write(fileData);

		} catch (Exception e) {
			throw new Exception("保存成文件错误!!" + e.getMessage());
		} finally {
			if (raf != null)
				raf.close();
		}
	}
	/**
	 * 根据typeId和taskId获得解压的底稿文件
	 * @param typeId
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public byte[] getUnZipFileByTypeIdAndTaskId(String typeId, String taskId)
			throws Exception {
		return new ZipUtil().ungzipBytes(getFileByTypeIdAndTaskId(typeId,
				taskId));
	}
	
	/**
	 * 根据项目ID和taskId获得压缩后的底稿二进制文件
	 * @param projectId
	 * @param taskid
	 * @return
	 * @throws Exception
	 */
	public byte[] getFileByTypeIdAndTaskId(String typeId, String taskid)
			throws Exception {
		byte[] fileByte = new byte[0];
		try {
			String filePath = TEMPLATE_PATH + typeId + "/" + taskid;
			File file = new File(filePath);

			if (file.exists()) {
				fileByte = new ZipUtil().fileToByteArray(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return fileByte;
	}

		/**
	 * 根据项目ID和底稿编号返回项目的底稿
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public File getProjectTaskFile(String projectId,String taskId) throws Exception {
		String customerId = new CustomerService(conn)
				.getCustomerIdByProjectId(projectId);
		String filePath = MANU_SCRIPT_PATH + customerId + "\\" + projectId + "\\" + taskId;
		return new File(filePath);
	}
	
	/**
	 * 根据项目ID和taskId获得压缩后的底稿二进制文件
	 * @param projectId
	 * @param taskid
	 * @return
	 * @throws Exception
	 */
	public byte[] getFileByProjectIdAndTaskId(String projectId, String taskid)
			throws Exception {
		byte[] fileByte = new byte[0];
		String customerId = new CustomerService(conn)
				.getCustomerIdByProjectId(projectId);

		try {
			String filePath = MANU_SCRIPT_PATH + customerId + "/" + projectId
					+ "/" + taskid;
			File file = new File(filePath);

			if (file.exists()) {
				fileByte = new ZipUtil().fileToByteArray(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return fileByte;
	}
	
	
	/**
	 * 根据客户ID和unId获得压缩后的附件二进制文件
	 * @param customerId
	 * @param unid
	 * @return
	 * @throws Exception
	 */
	public byte[] getFileBycustomerIdAndUnid(String customerId, String unid)
			throws Exception {
		byte[] fileByte = new byte[0];

		try {
			String filePath = MANU_SCRIPT_PATH + customerId + "/attach/" + unid;
			File file = new File(filePath);

			if (file.exists()) {
				fileByte = new ZipUtil().fileToByteArray(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return fileByte;
	}
	
	/**
	 * 根据客户ID删除整个目录
	 * @param customerId
	 * @throws Exception
	 */
	public static void deleteDirByCustomerID(String customerId)
			throws Exception {

		String dirPath = MANU_SCRIPT_PATH + customerId;
		File dir = new File(dirPath);

		//检查文件是否存在,存在就删除
		if (dir.exists() && dir.isDirectory()) {
			try {
				deleteFile(dir);
			} catch (Exception e) {
				throw new Exception("删除客户目录出错:" + e.getMessage());
			}
		}
	}

	/**
	 * 根据项目ID删除项目的底稿文件夹
	 * @param projectid
	 * @throws Exception
	 */
	public void deleteDirByProjectID(String projectid) throws Exception {

		File projectDir = this.getProjectDir(projectid);

		//检查文件是否存在,存在就删除
		if (projectDir.exists() && projectDir.isDirectory()) {
			try {
				deleteFile(projectDir);
			} catch (Exception e) {
				throw new Exception("删除项目底稿目录出错:" + e.getMessage());
			}

		}
	}

	/**
	 * 根据模版类型ID删除模版底稿文件夹
	 * @param projectid
	 * @throws Exception
	 */
	public static void deleteDirByTypeID(String typeId) throws Exception {
		File templateDir = getTemplateDir(typeId);

		//检查文件是否存在,存在就删除
		if (templateDir.exists() && templateDir.isDirectory()) {
			try {
				deleteFile(templateDir);
			} catch (Exception e) {
				throw new Exception("删除模版目录出错:" + e.getMessage());
			}

		}
	}

	

	/**
	 * 根据taskId和模版类型ID删除模版文件夹中的一个文件
	 * @param taskid
	 * @param typeId
	 * @throws Exception
	 */
	public static void deleteFileByTaskIdAndTypeID(String taskid, String typeId)
			throws Exception {
		String path = getTemplateDir(typeId).getPath() + "\\" + taskid;
		File file = new File(path);

		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 根据taskId和模版类型ID删除作业指导文件夹中的一个文件
	 * @param taskid
	 * @param typeId
	 * @throws Exception
	 */
	public static void deleteFileByHelpID(String taskid, String typeId)throws Exception {
		String path = getHelpDir(typeId).getPath() + "\\" + taskid;
		File file = new File(path);

		if (file.exists()) {
			file.delete();
		}
	}
	
	/**
	 * 根据taskId和项目编号删除项目文件夹中的一个文件
	 * @param taskid
	 * @param typeId
	 * @throws Exception
	 */
	public static void deleteFileByTaskId(String taskid, String projectId,
			String customerId) throws Exception {
		String path = MANU_SCRIPT_PATH + customerId + "\\" + projectId + "\\"
				+ taskid;
		File file = new File(path);

		if (file.exists()) {
			file.delete();
		}
	}
	
	/**
	 * 根据模版类型ID返回作业指导所在的文件夹
	 * @param typeId
	 * @return
	 * @throws Exception
	 */
	public static File getHelpDir(String typeId) throws Exception {
		String filePath = TEMPLATEHELP_PATH + typeId;
		return new File(filePath);
	}
	

	/**
	 * 根据项目ID返回项目的底稿所在文件夹
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public File getProjectDir(String projectId) throws Exception {
		String customerId = new CustomerService(conn)
				.getCustomerIdByProjectId(projectId);
		String filePath = MANU_SCRIPT_PATH + customerId + "\\" + projectId;

		return new File(filePath);
	}
	

	/**
	 * 根据模版类型ID返回模版所在的文件夹
	 * @param typeId
	 * @return
	 * @throws Exception
	 */
	public static File getTemplateDir(String typeId) throws Exception {
		String filePath = TEMPLATE_PATH + typeId;
		return new File(filePath);
	}
	
	/**
	 * 将项目另存成模版
	 * @param projectid
	 * @param typeid
	 * @throws Exception
	 */
	public void copyProjectToTemplate(String projectid, String typeid)
			throws Exception {
		File projectDir = getProjectDir(projectid);
		File templateDir = getTemplateDir(typeid);

		copyDir(projectDir, templateDir);
	}

	/**
	 * 拷贝模版到项目
	 * @param projectid
	 * @param typeid
	 * @throws Exception
	 */
	public void copyTemplateToProject(String typeid, String projectid)
			throws Exception {
		File projectDir = getProjectDir(projectid);
		File templateDir = getTemplateDir(typeid);

		copyDir(templateDir, projectDir);
	}

	/**
	 * 拷贝模版中的某个底稿到项目
	 * @param typeid
	 * @param projectid
	 * @param taskId
	 * @throws Exception
	 */
	public void copyTemplateToProject(String typeid, String projectid,
			String taskIds) throws Exception {
		if (taskIds == null) {
			throw new Exception("底稿编号不能为空");
		}

		String[] taskId = taskIds.split(",");

		for (int i = 0; i < taskId.length; i++) {
			File file = new File(getProjectDir(projectid).getPath());
			if (!file.exists()) {
				file.mkdirs();
			}
			File newFile = new File(getProjectDir(projectid).getPath() + "/"
					+ taskId[i]);

			if (!newFile.exists()) {
				File oldFile = new File(getTemplateDir(typeid).getPath() + "/"
						+ taskId[i]);
				copyFile(oldFile, newFile);
			}
		}
	}

	/**
	 * 拷贝模版中的某个底稿到项目
	 * @param typeid
	 * @param projectid
	 * @param taskId
	 * @throws Exception
	 */
	public void copyTemplateToProject(String typeid, String projectid,
			String taskId, String newTaskId) throws Exception {
		if (taskId == null || "".equals(taskId)) {
			throw new Exception("底稿编号不能为空");
		}
		File file = new File(getProjectDir(projectid).getPath());
		if (!file.exists()) {
			file.mkdirs();
		}
		File newFile = new File(getProjectDir(projectid).getPath() + "/"
				+ newTaskId);

		if (!newFile.exists()) {
			File oldFile = new File(getTemplateDir(typeid).getPath() + "/"
					+ taskId);
			copyFile(oldFile, newFile);
		}
	}

	/**
	 * 拷贝项目中的某个底稿到模版中
	 */
	public void copyProjectToTemplate(String projectId, String typeId,
			 String projectTaskId, String typeTaskId) throws Exception {
		if (projectTaskId == null || "".equals(projectTaskId)) {
			throw new Exception("底稿编号不能为空");
		}

		File templateFile = new File(getTemplateDir(typeId).getPath() + "/" + typeTaskId);

		if(templateFile.exists()) {
			templateFile.delete();
		}

		File projectFile = new File(getProjectDir(projectId).getPath() + "/" + projectTaskId);

		copyFile(projectFile, templateFile);
	}

	/**
	 * 拷贝项目到项目
	 * @param projectid
	 * @param typeid
	 * @throws Exception
	 */
	public void copyProjectToProject(String tagerProjectid,
			String sourceProjectid) throws Exception {
		File tagerProjectDir = getProjectDir(tagerProjectid);
		File sourceProjecDir = getProjectDir(sourceProjectid);

		copyDir(sourceProjecDir, tagerProjectDir);
	}
	
	/**
	 * 新增文件,从模版目录中拷贝到项目目录
	 * 例如：newFileByProjectIdAndTaskId("20076323", "10940", "newExcel");
	 *      将TemplateData/目录下的newExcel 文件拷贝到 ManuScriptData/客户id/20076323/目录,并命名为 10940
	 *
	 * @param projectId
	 * @param taskId
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public String newFileByProjectIdAndTaskId(String projectId, String taskId,
			String typeId, String templateTaskId) throws Exception {
		String customerId = new CustomerService(conn)
				.getCustomerIdByProjectId(projectId);

		//先按照模板来拷贝
		try {
			File newDir = new File(MANU_SCRIPT_PATH + customerId + "/"
					+ projectId);
			if (!newDir.exists()) {
				newDir.mkdirs();
			}

			File newFile = new File(MANU_SCRIPT_PATH + customerId + "/"
					+ projectId + "/" + taskId);
			File oldFile = new File(TEMPLATE_PATH + typeId + "/"
					+ templateTaskId);

			copyFile(oldFile, newFile);
			
			return MANU_SCRIPT_PATH + customerId + "/" + projectId;
		} catch (Exception e) {
			e.printStackTrace();
		}

		//再按照非模板来拷贝
		try {
			File newDir = new File(MANU_SCRIPT_PATH + customerId + "/"
					+ projectId);
			if (!newDir.exists()) {
				newDir.mkdirs();
			}

			File newFile = new File(MANU_SCRIPT_PATH + customerId + "/"
					+ projectId + "/" + taskId);
			File oldFile = new File(MANU_SCRIPT_PATH + customerId + "/"
					+ projectId + "/" + templateTaskId);

			copyFile(oldFile, newFile);
			
			return MANU_SCRIPT_PATH + customerId + "/" + projectId;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return "";
	}

	/**
	 * 新增文件,从模版目录中拷贝到项目目录
	 *
	 * @param newTypeId
	 * @param taskId
	 * @param typeId
	 * @param templateTaskId
	 * @return
	 * @throws Exception
	 */
	public String newFileByTypeIdAndTaskId(String newTypeId, String taskId,
			String typeId, String templateTaskId) throws Exception {
		try {
			File newDir = getTemplateDir(newTypeId);
			if (!newDir.exists()) {
				newDir.mkdirs();
			}

			File newFile = new File(newDir.getPath() + "/" + taskId);
			File oldFile = new File(TEMPLATE_PATH + typeId + "/"
					+ templateTaskId);

			copyFile(oldFile, newFile);

			return TEMPLATE_PATH + newTypeId + "/" + taskId;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}
	
	public void saveFileByHelpId(String typeId, String taskId,byte[] fileData) throws Exception {
		DbUtil.checkConn(conn);

		Map fileInfo = new HashMap();
		fileInfo.put("fileData", fileData);
		fileInfo.put("fileName", taskId);
		fileInfo.put("fileDir", typeId);
		fileInfo.put("fileTypePath", TEMPLATEHELP_PATH);

		saveFile(fileInfo);
	}
	
	/**
	 * 根据模板编号和底稿编号保存文件
	 * @param typeId
	 * @param taskId
	 * @param fileData
	 * @throws Exception
	 */
	public void saveFileByTypeIdAndTaskId(String typeId, String taskId,
			byte[] fileData) throws Exception {
		Map fileInfo = new HashMap();
		fileInfo.put("fileData", fileData);
		fileInfo.put("fileName", taskId);
		fileInfo.put("fileDir", typeId);
		fileInfo.put("fileTypePath", TEMPLATE_PATH);

		saveFile(fileInfo);
	}

	/**
	 * 根据AUTOID返回回收站中的某张底稿
	 * @param autoId
	 * @return
	 * @throws Exception
	 */
	public Map getFileByAutoId(String autoId) throws Exception {
		Map fileMap = new HashMap();
		byte[] fileByte = new byte[0];
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String strSql = "select taskid,version,taskName,projectId,mime "
					+ " from asdb.z_taskrecycle " + " where autoid = ?";
			ps = conn.prepareStatement(strSql);
			ps.setString(1, autoId);
			rs = ps.executeQuery();

			if (rs.next()) {
				String taskid = rs.getString(1);
				String version = rs.getString(2);
				String taskName = rs.getString(3);
				String projectId = rs.getString(4);
				String mime = rs.getString(5);

				String customerId = new CustomerService(conn)
						.getCustomerIdByProjectId(projectId);

				try {
					String filePath = MANU_SCRIPT_PATH + customerId + "/"
							+ projectId + "/recycle/" + taskid + "_" + version;
					File file = new File(filePath);

					if (file.exists()) {
						fileByte = new ZipUtil().fileToByteArray(file);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				fileMap.put("fileByte", fileByte);
				fileMap.put("fileName", taskName);
				fileMap.put("mime", mime);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return fileMap;
	}
	
	/**
	 * 保存底稿附件
	 * @param projectId
	 * @param attachId
	 * @param fileData
	 * @throws Exception
	 */
	public void saveAttachByProjectIdAndAttachId(String projectId, String attachId,
			byte[] fileData) throws Exception {
		DbUtil.checkConn(conn);

		String customerId = new CustomerService(conn)
				.getCustomerIdByProjectId(projectId);

		Map fileInfo = new HashMap();
		fileInfo.put("fileData", fileData);
		fileInfo.put("fileName", attachId);
		fileInfo.put("fileDir", customerId + "/" + projectId + "/attach");
		fileInfo.put("fileTypePath", MANU_SCRIPT_PATH);

		saveFile(fileInfo);
	}
	
	
	/**
	 * 根据项目编号或类型编号和taskId保存文件
	 * @param taskId
	 * @param projectIdOrTypeId
	 * @param fileData
	 * @throws Exception
	 */
	public void saveFileByTaskId(String projectIdOrTypeId, String taskId,
			byte[] fileData) throws Exception {

		DbUtil.checkConn(conn);

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String strSql = "select taskid,typeId from k_tasktemplate c where typeId = ? ";

			ps = conn.prepareStatement(strSql);
			ps.setString(1, projectIdOrTypeId);
			rs = ps.executeQuery();

			//如果是模版底稿文件
			if (rs.next()) {
				saveFileByTypeIdAndTaskId(projectIdOrTypeId, taskId, fileData);
			} else {
				//项目底稿
				saveFileByProjectIdAndTaskId(projectIdOrTypeId, taskId,
						fileData);
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "保存底稿失败", e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 根据项目编号和底稿编号保存文件
	 * @param projectId
	 * @param taskId
	 * @param fileData
	 * @throws Exception
	 */
	public void saveFileByProjectIdAndTaskId(String projectId, String taskId,
			byte[] fileData) throws Exception {
		DbUtil.checkConn(conn);

		String customerId = new CustomerService(conn)
				.getCustomerIdByProjectId(projectId);

		Map fileInfo = new HashMap();
		fileInfo.put("fileData", fileData);
		fileInfo.put("fileName", taskId);
		fileInfo.put("fileDir", customerId + "/" + projectId);
		fileInfo.put("fileTypePath", MANU_SCRIPT_PATH);

		saveFile(fileInfo);
	}
	

	/**
	 * 模版底稿改名
	 * @param oldTypeId
	 * @param newTypeId
	 * @throws Exception
	 */
	public static void renameFileByTypeId(String oldTypeId, String newTypeId)
			throws Exception {
		File oldFile = getTemplateDir(oldTypeId);
		File newFile = getTemplateDir(newTypeId);

		oldFile.renameTo(newFile);
	}

	/**
	 * 项目底稿目录改名
	 * @param oldProjectId
	 * @param newProjectId
	 * @throws Exception
	 */
	public static void renameFileByCustomerIdAndProjectId(String customerId,
			String oldProjectId, String newProjectId) throws Exception {
		File oldProjectFile = new File(MANU_SCRIPT_PATH + customerId + "\\"
				+ oldProjectId);
		File newProjectFile = new File(MANU_SCRIPT_PATH + customerId + "\\"
				+ newProjectId);

		if (!oldProjectFile.exists()) {
			throw new Exception("找不到该项目底稿目录...");
		} else {
			oldProjectFile.renameTo(newProjectFile);
		}
	}

		/**
	 * 根据projectId和taskId获得解压的底稿文件
	 * @param projectId
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public byte[] getUnZipFileByProjectIdAndTaskId(String projectId,
			String taskId) throws Exception {
		return new ZipUtil().ungzipBytes(getFileByProjectIdAndTaskId(projectId,
				taskId));
	}

	/**
	 * 根据作业指导ID和taskId获得压缩后的底稿二进制文件
	 * @param typeId
	 * @param taskid
	 * @return
	 * @throws Exception
	 */
	public byte[] getFileByHelpId(String typeId, String taskid) throws Exception {
		byte[] fileByte = new byte[0];
		try {
			String filePath = TEMPLATEHELP_PATH + typeId + "/" + taskid;
			File file = new File(filePath);
		
			if (file.exists()) {
				fileByte = new ZipUtil().fileToByteArray(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return fileByte;
	}
	
	
	

	/**
	 * 根据typeId和taskId获得解压的底稿文件
	 * @param typeId
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public byte[] getUnZipFileByHelpId(String typeId, String taskId) throws Exception {
		return new ZipUtil().ungzipBytes(getFileByHelpId(typeId,taskId));
	}

	
}
