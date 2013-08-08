package com.matech.audit.service.datamanage;

import java.io.File;
import java.sql.Connection;
import java.util.List;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.framework.pub.db.DbUtil;

public class BasicBackup extends BackupUtil {

	/**
	 * 基本数据备份
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public String backup(String fileName, String userName) throws Exception {

		//全库备份的路径
		String localPath = BASIC_PATH;

		File file = new File(localPath);

		//判断路径是否存在,不存在就创建
		if(!file.exists()) {
			file.mkdirs();
		}

		if(("").equals(fileName)) {
			localPath = localPath + "基本数据备份_" + getCurrentDateTime() + ".zip";
		} else {
			localPath = localPath + fileName;
		}

		try {

			//备份asdb库下的所有以k_开头的表
			List filterList = getTablesByFile("asdb","/", "k_");
			filterList.add("TemplateData");
			filterList.add("ManuScriptData");

			//备份文件注释
			StringBuffer sb = new StringBuffer();
			sb.append("备份用户：" + userName + "`");		//备份人
			sb.append("备份类型：" + "基本数据备份" + "`");	//备份类型
			sb.append("备份日期：" + getDateTime("yyyy-MM-dd")+ "`");	//备份日期
			sb.append("备份时间：" + getDateTime("HH:mm:ss")+ "`");		//备份时间

			sb.append(getValidate(BackupUtil.BASIC_CODE));

			DataZip dataZip = new DataZip();
			dataZip.setComment(sb.toString());
			//dataZip.zip(DATABASE_PATH, localPath, filterList);
			dataZip.zip(DATABASE_PATH + "../",localPath,filterList);

			return localPath;

		} catch(Exception ex) {
			ex.printStackTrace();
		}

		return "";
	}

	/**
	 * 基本数据还原
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public boolean recover(String fileName) throws Exception {


		//全库备份文件的路径
		String localPath = BASIC_PATH;

		String localFile = localPath + fileName;

		File file = new File(localFile);

		if(!file.exists() || !file.isFile()) {
			return false;
		}

		Connection conn = null;
		try {
			dropBasic();
			File tempLataFile = new File(DATABASE_PATH + "../TempLateData");
			File manuScriptFile = new File(DATABASE_PATH + "../ManuScriptData");
			conn = new DBConnect().getDirectConnect("");
			ManuFileService.deleteFile(tempLataFile);		//删除底稿模版目录
			ManuFileService.deleteFile(manuScriptFile);	//删除所有客户的项目底稿
			new DataZip().unZip(localFile, DATABASE_PATH + "../", true);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

		//return false;
	}
}
