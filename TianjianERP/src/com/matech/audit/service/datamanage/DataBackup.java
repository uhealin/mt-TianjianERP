package com.matech.audit.service.datamanage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.matech.audit.service.manuscript.ManuFileService;

/**
 * 数据库备份类
 * @author void
 *
 */
public class DataBackup extends BackupUtil {

	/**
	 * 全库备份
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public String backup(String fileName, String userName) throws Exception {

		//全库备份的路径
		String localPath = ALL_PATH;

		File file = new File(localPath);

		//判断路径是否存在,不存在就创建
		if(!file.exists()) {
			file.mkdirs();
		}

		if(("").equals(fileName) || fileName == null) {
			localPath = localPath + "全库数据备份_" + getCurrentDateTime() + ".zip";
		} else {
			localPath = localPath + fileName;
		}

		try {

			//备份所有以asdb开头的库
			List list = new ArrayList();
			list.add("asdb");
			list.add("TemplateData");
			list.add("ManuScriptData");

			//备份文件注释
			StringBuffer sb = new StringBuffer();
			sb.append("备份用户：" + userName + "`");		//备份人
			sb.append("备份类型：全库数据备份" + "`");		//备份类型
			sb.append("备份日期：" + getDateTime("yyyy-MM-dd")+ "`");	//备份日期
			sb.append("备份时间：" + getDateTime("HH:mm:ss")+ "`");		//备份时间

			sb.append(getValidate(BackupUtil.ALL_CODE));

			DataZip dataZip = new DataZip();
			dataZip.setComment(sb.toString());
			//dataZip.zip(DATABASE_PATH,localPath,list);
			dataZip.zip(DATABASE_PATH + "../",localPath,list);
			return localPath;

		} catch(Exception ex) {
			ex.printStackTrace();
		}

		return "";
	}

	/**
	 * 全库还原
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public boolean recover(String fileName) throws Exception {

		//全库备份文件的路径
		String localPath = ALL_PATH;

		String localFile = localPath + fileName;

		File file = new File(localFile);

		if(!file.exists() || !file.isFile()) {
			//备份文件不存在
			return false;
		}

//		try {
//			dropAll();
//			File tempLataFile = new File(DATABASE_PATH + "../TemplateData");
//			File manuScriptFile = new File(DATABASE_PATH + "../ManuScriptData");
//			ManuFileService.deleteFile(tempLataFile);		//删除底稿模版目录
//			ManuFileService.deleteFile(manuScriptFile);	//删除所有客户的项目底稿
//			new DataZip().unZip(localFile, DATABASE_PATH + "../", false);
//			return true;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		return false;
	}
}
