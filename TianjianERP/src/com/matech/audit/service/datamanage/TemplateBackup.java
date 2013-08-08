package com.matech.audit.service.datamanage;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.audittypetemplate.AuditTypeTemplateService;
import com.matech.audit.service.audittypetemplate.model.AuditTypeTemplate;
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.framework.pub.db.DbUtil;

public class TemplateBackup extends BackupUtil {
	/**
	 * 底稿模版备份
	 *
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public String backup(String fileName, String typeId, String userName) throws Exception {

		super.log("欢迎进入模板备份,该过程可能需时比较长,请耐心等待");

		super.logStart("这个在准备备份信息");

		// 案例库备份的路径
		String localPath = TEMPLATE_PATH;

		String templateDataBaseName = "template_backup_" + getCurrentDateTime(); // 备份库名

		File file = new File(TEMPLATE_PATH);

		// 判断路径是否存在,不存在就创建
		if (!file.exists()) {
			file.mkdirs();
		}

		Connection conn = null;
		AuditTypeTemplate auditTypeTemplate = null;
		try {
			conn = new DBConnect().getConnect("");
			auditTypeTemplate = new AuditTypeTemplateService(conn).getAuditTypeTemplateByTypeId(typeId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}


		if (("").equals(fileName)) {
			localPath = localPath + "模板数据备份[" + auditTypeTemplate.getTypeid() + "]_" + auditTypeTemplate.getTypename() + "_" + getCurrentDateTime() + ".zip";
		} else {
			localPath = localPath + fileName;
		}

		try {
			super.logEndAndStart("创建临时库");
			// 创建临时库
			createTempDB(templateDataBaseName);

			super.logEndAndStart("正在备份公式数据");
			copyTable("k_areafunction",templateDataBaseName,"typeid",typeId);		//复制公式表

			super.logEndAndStart("正在备份模板信息数据");
			copyTable("k_audittypetemplate",templateDataBaseName,"typeid",typeId);	//复制模版表

			super.logEndAndStart("正在备份审计程序数据");
			copyTable("k_proceduretemplate",templateDataBaseName,"typeid",typeId);	//复制程序模版表

			super.logEndAndStart("正在备份审计目标数据");
			copyTable("k_targettemplate",templateDataBaseName,"typeid",typeId);		//复制目标模版表

			super.logEndAndStart("正在备份底稿数据");
			copyTable("k_tasktemplate",templateDataBaseName,"typeid",typeId);		//复制任务模版表

			//copyTable("k_manuscriptdata",templateDataBaseName,"projectid",typeId);	//复制底稿表

			super.logEndAndStart("正在备份底稿引用数据");
			copyTable("k_taskrefertemplate",templateDataBaseName,"Typeid",typeId);	//复制底稿引用模板表

			//super.logEndAndStart("正在备份单元格公式数据");
			//copyTable("k_taskformulatemplate",templateDataBaseName,"Typeid",typeId);	//复制底稿单元格公式模板表

			super.logEndAndStart("正在备份底稿表页数据");
			copyTable("k_sheettasktemplate",templateDataBaseName,"typeid",typeId);	//复制底稿单元格公式模板表
			
			super.logEndAndStart("正在备份模板指导数据");
			copyTable("k_tasktemplatehelp",templateDataBaseName,"typeid",typeId);	//复制模板指导表

//			try {
//				//重算k_manuscriptdata表
//				super.revocerManuscriptdata(templateDataBaseName,"");
//			} catch (Exception e) {
//				e.printStackTrace();
//			}


			// 备份文件注释
			StringBuffer sb = new StringBuffer();
			sb.append("备份用户：" + userName + "`"); // 备份人
			sb.append("备份类型：" + "模板数据备份" + "`"); // 备份类型
			sb.append("备份日期：" + getDateTime("yyyy-MM-dd") + "`"); // 备份日期
			sb.append("备份时间：" + getDateTime("HH:mm:ss") + "`"); // 备份时间

			sb.append(getValidate(BackupUtil.TEMPLATE_CODE));

			String randomPath = TEMPLATE_PATH + "/" + getCurrentDateTime();

			//先拷贝到临时目录,再进行压缩,避免太多的文件查找
			copyFiles(DATABASE_PATH + "/" + templateDataBaseName, randomPath + "/Data/" + templateDataBaseName);
			copyFiles(DATABASE_PATH + "../" + "TemplateData/" + typeId, randomPath + "/" + "TemplateData/" + typeId);

			super.logEndAndStart("正在清理无效文件");
			try {
				conn = new DBConnect().getConnect("");
				String strSql = " select ifnull(group_concat(taskid),-1) from " + templateDataBaseName + ".k_tasktemplate where typeid=? and isleaf=1 ";
				String taskIds = new DbUtil(conn).queryForString(strSql, new Object[]{typeId});
				
				taskIds = "," + taskIds + ",";
				
				File templateDataFile = new File(randomPath + "/" + "TemplateData/" + typeId);
				
				if(templateDataFile.exists()) {
					
					File[] files = templateDataFile.listFiles();
					String tempFileName = "";
					for(int i=0; i < files.length; i++) {
						tempFileName = files[i].getName();
	
						if(taskIds.indexOf("," + tempFileName + ",") < 0) {
							System.out.println("删除垃圾文件：" + tempFileName);
							files[i].delete();
						}
					}

				}
				
				try {
					File recycleFile = new File(randomPath + "/" + "TemplateData/" + typeId + "/recycle");
					ManuFileService.deleteFile(recycleFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DbUtil.close(conn);
			}
			
			super.logEndAndStart("开始压缩数据");
			DataZip dataZip = new DataZip();
			dataZip.setComment(sb.toString());
			dataZip.zip(randomPath, localPath);

			File tempFile = new File(randomPath);
			deleteFile(tempFile);

			super.logEnd();

			super.log("备份成功,生成备份文件：<font color=\"blue\">" + new File(localPath).getName() + "</font>");

			return localPath;

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// 清理临时数据库
			dropDataBaseByFile("template_backup_");
		}

		return "";
	}

	/*
	 * 复制整张表,主键等字段属性不复制
	 * (non-Javadoc)
	 * @see com.ASSys.work.datamanage.BackupUtil#copyTable(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean copyTable(String tableName, String targetDB, String field, String value) throws Exception {
		Connection conn = null;
		Statement stmt = null;

		try {
			conn = new DBConnect().getDirectConnect("");
			String strSql = "create table `" + targetDB + "`.`" + tableName + "` "
						  + " select * from `asdb`.`" + tableName + "` "
						  + " where " + field + " = '" + value + "'";
			stmt = conn.createStatement();
			System.out.println("复制表：" + strSql);
			stmt.execute(strSql);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			//throw new Exception("复制数据库表出错：" + e.getMessage());
		} finally {
			DbUtil.close(stmt);
			DbUtil.close(conn);
		}

		return false;
	}

	/**
	 * 解压临时库,返回临时库名
	 *
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	private Map unzip(String fileName) throws Exception {

		String localFile = TEMPLATE_PATH + fileName;
		File file = new File(localFile);
		Map fileMap = new HashMap();

		if (!file.exists() || !file.isFile()) {
			return fileMap;
		}

		// 临时解压目录
		String tempPath = DATABASE_PATH + "../"+ fileName;
		File fileUnzip = new File(tempPath);

		if (!fileUnzip.exists()) {
			fileUnzip.mkdirs();
		}

		try {

			new DataZip().unZip(localFile, tempPath, true);

			File dataFile = new File(tempPath + "/Data");
			File[] dataFileList = dataFile.listFiles();
			for (int i = 0; i < dataFileList.length; i++) {
				if (dataFileList[i].exists() && dataFileList[i].isDirectory()) {
					if (dataFileList[i].getName().indexOf("template_backup_") == 0) {
						// 移动文件夹到数据库目录下
						File fileTemp = new File(DATABASE_PATH + dataFileList[i].getName());
						fileMap.put("template_backup_", dataFileList[i].getName());
						dataFileList[i].renameTo(fileTemp);
					}
				}
			}

			File templateFile = new File(tempPath + "/TemplateData");
			File[] templateFileList = templateFile.listFiles();
			for (int i = 0; i < templateFileList.length; i++) {
				if (templateFileList[i].exists() && templateFileList[i].isDirectory()) {
					// 移动文件夹到数据库目录下
					File fileTemp = new File(DATABASE_PATH + "../TemplateData/" + fileName + "_" + templateFileList[i].getName());
					fileMap.put("template", fileName + "_" +  templateFileList[i].getName());
					templateFileList[i].renameTo(fileTemp);
				}
			}

			ManuFileService.deleteFile(fileUnzip);
			return fileMap;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return fileMap;
	}

	/**
	 * 底稿模版数据还原
	 * @param fileName
	 * @param recoverType	模版类型编号,如果是"-1"表示新增
	 * @return
	 * @throws Exception
	 */
	public boolean recover(String fileName, String recoverType)
			throws Exception {

		super.log("欢迎进入底稿模板恢复,该过程可能需时比较长,请耐心等待...");

		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;

		// 做一次清理,把临时备份库目录删除
		if (dropDataBaseByFile("template_backup_") > 0) {
			// 如果清理失败,则返回
			return false;
		}
		
		String strSql = "";

		try {
			super.logStart("正在解压数据");

			Map map = unzip(fileName);
			String templateDataBaseName = (String)map.get("template_backup_");
			String template = (String)map.get("template");

			conn = new DBConnect().getDirectConnect("");
			stmt = conn.createStatement();

			//如果是新增底稿
			if("-1".equals(recoverType)) {

				String typeId = null;

				strSql = " select typeid from " + templateDataBaseName + ".k_audittypetemplate ";
				stmt = conn.createStatement();
				rs = stmt.executeQuery(strSql);

				if(rs.next()) {
					typeId = rs.getString(1);
				}

				super.logEndAndStart("获取底稿模板编号：" + typeId);

				strSql = " select typeId from asdb.k_audittypetemplate where typeId='" + typeId + "'";
				rs = stmt.executeQuery(strSql);

				//如果编号已经存在,则重新生成一个编号
				if(rs.next()) {
					//获得一个新的底稿模板ID
					typeId = String.valueOf(new AuditTypeTemplateService(conn).getMaxTypeId());

					super.logEndAndStart("底稿编号已经存在,重新生成编号：" + typeId);

				}
				
				//删除原来的模版数据
				try {
					//无条件清理一下
					deleteData(conn,"asdb","k_areafunction","typeid",typeId);
					deleteData(conn,"asdb","k_audittypetemplate","typeid",typeId);
					deleteData(conn,"asdb","k_proceduretemplate","typeid",typeId);
					deleteData(conn,"asdb","k_targettemplate","typeid",typeId);
					deleteData(conn,"asdb","k_tasktemplate","typeid",typeId);
					deleteData(conn,"asdb","k_taskrefertemplate","typeid",typeId);
					deleteData(conn,"asdb","k_sheettasktemplate","typeid",typeId);
					deleteData(conn,"asdb","k_tasktemplatehelp","typeid",typeId);
					ManuFileService.deleteDirByTypeID(typeId);
				} catch (Exception e) {
					e.printStackTrace();
				}

				super.logEndAndStart("正在更新备份库模板公式数据");
				updateData(conn,templateDataBaseName,"k_areafunction","typeid",typeId);

				super.logEndAndStart("正在更新备份库模板信息数据");
				updateData(conn,templateDataBaseName,"k_audittypetemplate","typeid",typeId);

				super.logEndAndStart("正在更新备份库模板审计程序数据");
				updateData(conn,templateDataBaseName,"k_proceduretemplate","typeid",typeId);

				super.logEndAndStart("正在更新备份库模板审计目标数据");
				updateData(conn,templateDataBaseName,"k_targettemplate","typeid",typeId);

				super.logEndAndStart("正在更新备份库模板审底稿数据");
				updateData(conn,templateDataBaseName,"k_tasktemplate","typeid",typeId);

				super.logEndAndStart("正在更新备份库模板审底稿引用数据");
				updateData(conn,templateDataBaseName,"k_taskrefertemplate","typeid",typeId);

				//super.logEndAndStart("正在更新备份库模板单元格公式数据");
				//updateData(conn,templateDataBaseName,"k_taskformulatemplate","typeid",typeId);

				super.logEndAndStart("正在更新备份库模板底稿表页数据");
				updateData(conn,templateDataBaseName,"k_sheettasktemplate","typeid",typeId);
				
				super.logEndAndStart("正在更新备份库模板指导数据");
				updateData(conn,templateDataBaseName,"k_tasktemplatehelp","typeid",typeId);

				ManuFileService.renameFileByTypeId(template, typeId);

				try {
					super.logEndAndStart("清理底稿模板回收站");
					File file = new File(ManuFileService.getTemplateDir(typeId).getAbsolutePath() + "/recycle");
					ManuFileService.deleteFile(file);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {

				super.logEndAndStart("正在删除模板公式数据：" + recoverType);

				//删除原来的模版数据
				deleteData(conn,"asdb","k_areafunction","typeid",recoverType);

				super.logEndAndStart("正在删除模板信息数据,模板编号：" + recoverType);
				deleteData(conn,"asdb","k_audittypetemplate","typeid",recoverType);

				super.logEndAndStart("正在删除模板审计程序数据,模板编号：" + recoverType);
				deleteData(conn,"asdb","k_proceduretemplate","typeid",recoverType);

				super.logEndAndStart("正在删除模板审计目标数据,模板编号：" + recoverType);
				deleteData(conn,"asdb","k_targettemplate","typeid",recoverType);

				super.logEndAndStart("正在删除模板底稿数据,模板编号：" + recoverType);
				deleteData(conn,"asdb","k_tasktemplate","typeid",recoverType);

				super.logEndAndStart("正在删除模板底稿引用数据,模板编号：" + recoverType);
				deleteData(conn,"asdb","k_taskrefertemplate","typeid",recoverType);

				//super.logEndAndStart("正在删除模板单元格公式数据,模板编号：" + recoverType);
				//deleteData(conn,"asdb","k_taskformulatemplate","typeid",recoverType);

				super.logEndAndStart("正在删除模板底稿表页数据,模板编号：" + recoverType);
				deleteData(conn,"asdb","k_sheettasktemplate","typeid",recoverType);
				
				super.logEndAndStart("正在删除模板指导数据,模板编号：" + recoverType);
				deleteData(conn,"asdb","k_sheettasktemplate","typeid",recoverType);

				super.logEndAndStart("正在删除模板底稿文件,模板编号：" + recoverType);
				ManuFileService.deleteDirByTypeID(recoverType);

				//更新备份库中的typeid
				super.logEndAndStart("正在更新备份库模板公式数据");
				updateData(conn,templateDataBaseName,"k_areafunction","typeid",recoverType);

				super.logEndAndStart("正在更新备份库模板信息数据");
				updateData(conn,templateDataBaseName,"k_audittypetemplate","typeid",recoverType);

				super.logEndAndStart("正在更新备份库模板审计程序数据");
				updateData(conn,templateDataBaseName,"k_proceduretemplate","typeid",recoverType);

				super.logEndAndStart("正在更新备份库模板审计目标数据");
				updateData(conn,templateDataBaseName,"k_targettemplate","typeid",recoverType);

				super.logEndAndStart("正在更新备份库模板底稿数据");
				updateData(conn,templateDataBaseName,"k_tasktemplate","typeid",recoverType);

				super.logEndAndStart("正在更新备份库模板底稿引用数据");
				updateData(conn,templateDataBaseName,"k_taskrefertemplate","typeid",recoverType);

				//super.logEndAndStart("正在更新备份库模板单元格公式数据");
				//updateData(conn,templateDataBaseName,"k_taskformulatemplate","typeid",recoverType);

				super.logEndAndStart("正在更新备份库模板底稿表页数据");
				updateData(conn,templateDataBaseName,"k_sheettasktemplate","typeid",recoverType);
				
				super.logEndAndStart("正在更新备份库模板指导数据");
				updateData(conn,templateDataBaseName,"k_tasktemplatehelp","typeid",recoverType);

				super.logEndAndStart("正在更新模板底稿文件");
				ManuFileService.renameFileByTypeId(template, recoverType);

				try {
					super.logEndAndStart("清理底稿模板回收站");
					File file = new File(ManuFileService.getTemplateDir(recoverType).getAbsolutePath() + "/recycle");
					ManuFileService.deleteFile(file);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			super.logEndAndStart("正在恢复底稿模板数据");

			//获得备份库的所有表
			List templateBakTables = new ArrayList();//getTables("asdb", templateDataBaseName,"", "");//getTables(customerDBName, "", "");
			
			templateBakTables.add("k_areafunction");
			templateBakTables.add("k_proceduretemplate");
			templateBakTables.add("k_targettemplate");
			templateBakTables.add("k_tasktemplate");
			templateBakTables.add("k_taskrefertemplate");
			templateBakTables.add("k_sheettasktemplate");
			templateBakTables.add("k_tasktemplatehelp");
			templateBakTables.add("k_audittypetemplate");


			if(!templateBakTables.isEmpty()) {
				stmt = conn.createStatement();
				for(int i=0; i < templateBakTables.size(); i++) {
					String tableName = templateBakTables.get(i).toString();

					String colConcat =  getColumn("asdb", templateDataBaseName,tableName);

					//将备份系统库中的表数据插到系统库中,auto_increment列除外
					if(!"".equals(colConcat)) {

						strSql = " insert into asdb." + tableName + "(" + colConcat + ") "
							   + " select " + colConcat
							   + " from " + templateDataBaseName + "." + tableName;

						System.out.println(strSql);
						stmt.execute(strSql);	//方便监控
						//stmt.addBatch(strSql);
					}
				}
				//stmt.executeBatch();
			}

			updateAreaFunction(conn);

			super.logEnd();
			super.log("恢复成功!!!");

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("出错SQL：" + strSql + ",错误信息："+ e.getMessage());
		} finally {
			dropDataBaseByFile("template_backup_");

			DbUtil.close(rs);
			DbUtil.close(stmt);
			DbUtil.close(conn);
		}

		//return false;
	}

	private void updateAreaFunction(Connection conn) {
		try {
			DbUtil dbUtil = new DbUtil(conn);

			String sql = "update k_areafunction set strsql=replace(strsql,'c_accountallrectify','z_accountallrectify') where  strsql like '%c_accountallrectify%';";
			System.out.println("正在更新公式表,替换c_accountallrectify为z_accountallrectify,影响：" + dbUtil.executeUpdate(sql));

			sql = "update k_areafunction set strsql=replace(strsql,'c_accountrectify','z_accountrectify') where  strsql like '%c_accountrectify%';";
			System.out.println("正在更新公式表,替换c_accountrectify为z_accountrectify,影响：" + dbUtil.executeUpdate(sql));

			sql = "update k_areafunction set strsql=replace(strsql,'c_assitemaccallrectify','z_assitemaccallrectify') where  strsql like '%c_assitemaccallrectify%';";
			System.out.println("正在更新公式表,替换c_assitemaccallrectify为z_assitemaccallrectify,影响：" + dbUtil.executeUpdate(sql));

			sql = "update k_areafunction set strsql=replace(strsql,'c_assitemaccrectify','z_assitemaccrectify') where  strsql like '%c_assitemaccrectify%';";
			System.out.println("正在更新公式表,替换c_assitemaccrectify为z_assitemaccrectify,影响：" + dbUtil.executeUpdate(sql));

			sql = "update k_areafunction set strsql=replace(strsql,'c_assitementryrectify','z_assitementryrectify') where  strsql like '%c_assitementryrectify%';";
			System.out.println("正在更新公式表,替换c_assitementryrectify为z_assitementryrectify,影响：" + dbUtil.executeUpdate(sql));

			sql = "update k_areafunction set strsql=replace(strsql,'c_subjectentryrectify','z_subjectentryrectify') where  strsql like '%c_subjectentryrectify%';";
			System.out.println("正在更新公式表,替换c_subjectentryrectify为z_subjectentryrectify,影响：" + dbUtil.executeUpdate(sql));

			sql = "update k_areafunction set strsql=replace(strsql,'c_voucherrectify','z_voucherrectify') where  strsql like '%c_voucherrectify%';";
			System.out.println("正在更新公式表,替换c_voucherrectify为z_voucherrectify,影响：" + dbUtil.executeUpdate(sql));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("更新公式表失败,请手工更新....");
		}
	}
}
