package com.matech.audit.service.audittypetemplate;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;

import com.matech.audit.service.audittypetemplate.model.AuditTemplateTask;
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.Debug;

public class TemplateTaskRecycleService {
	private Connection conn = null;
	private String typeId = null;
	
	private String userName = null;
	
	private String remark = null;

	public TemplateTaskRecycleService(Connection conn, String typeId)
			throws Exception {
		DbUtil.checkConn(conn);
		this.conn = conn;

		if ("".equals(typeId) || typeId == null) {
			throw new Exception("模板编号不能为空!");
		}
		this.typeId = typeId;
	}

	/**
	 * 复制底稿记录到回收站表
	 * @param projectId
	 * @param taskId
	 * @throws Exception
	 */
	public void moveToRecycle(String taskId, String saveType) throws Exception {
		
		PreparedStatement ps = null;
		int maxVersion = 0;
		try {

			//找出该底稿最大的版本号+1
			String strSql = " select ifnull(max(version),0) + 1 "
					+ " from asdb.k_taskrecycle "
					+ " where typeid = ? "
					+ " and taskId = ? ";
			DbUtil dbUtil = new DbUtil(conn);
			Object[] params = new Object[] { this.typeId, taskId };

			maxVersion = dbUtil.queryForInt(strSql, params);

			strSql = " insert into asdb.k_taskrecycle( "
					+ " 	version, versiondate, savetype, saveuser, remark, TaskID, TaskCode, TaskName, "
					+ " 	TaskContent, Description, ParentTaskID, TypeID, IsLeaf, Level0, ManuID, ManuTemplateID,"
					+ " 	Property,FullPath,orderid,ismust,SubjectName,Udate,Username,auditproperty) "
					+ " select ? as version,now() as versiondate,? as savetype,? as saveuser,? as mark,"
					+ " TaskID, TaskCode, TaskName,TaskContent, Description, ParentTaskID, TypeID, IsLeaf, Level0, ManuID, ManuTemplateID,"
					+ " Property,FullPath,orderid,ismust,SubjectName,Udate,Username,auditproperty "
					+ " from asdb.k_tasktemplate " 
					+ " where typeid=? "
					+ " and taskid=? ";
			ps = conn.prepareStatement(strSql);
			ps.setInt(1, maxVersion);
			ps.setString(2, saveType);
			ps.setString(3, this.userName);
			ps.setString(4, this.remark);
			ps.setString(5, this.typeId);
			ps.setString(6, taskId);
			
			if(ps.executeUpdate() > 0) {
				copyFile(taskId, maxVersion);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	
	public int getMaxVersion(String taskId) throws Exception {
		//找出该底稿最大的版本号+1
		String strSql = " select max(version) "
				+ " from asdb.k_taskrecycle "
				+ " where typeid = ? "
				+ " and taskId = ? ";
		DbUtil dbUtil = new DbUtil(conn);
		Object[] params = new Object[] { this.typeId, taskId };
		
		return dbUtil.queryForInt(strSql, params);
	}
	
    /**
     * 还原底稿到项目
     * @param autoId
     * @throws Exception
     */
    public void restore(String taskId) throws Exception {
        PreparedStatement ps = null;

        try {

            int version =getMaxVersion(taskId);
            
            AuditTypeTemplateService auditTypeTemplateService = new AuditTypeTemplateService(conn);
      
            moveToRecycle(taskId,"还原");

            //删除原来项目中的底稿
            auditTypeTemplateService.removeTemplaeTask(this.typeId, taskId);

            String strSql = "insert into k_tasktemplate (TaskID, TaskCode, TaskName,TaskContent, Description, "
            				+ " 	ParentTaskID, TypeID, IsLeaf, Level0, ManuID, ManuTemplateID,"
            				+ " 	Property,FullPath,orderid,ismust,SubjectName,Udate,Username,auditproperty) "
                            + " select TaskID, TaskCode, TaskName,TaskContent, Description, "
            				+ " ParentTaskID, TypeID, IsLeaf, Level0, ManuID, ManuTemplateID,"
            				+ " Property,FullPath,orderid,ismust,SubjectName,Udate,Username,auditproperty "
                            + " from asdb.k_taskrecycle "
                            + " where typeId=? "
                            + " and taskId=? "
                            + " and version=? ";

            ps = conn.prepareStatement(strSql);
            ps.setString(1, this.typeId);
            ps.setString(2, taskId);
            ps.setInt(3, version);

            Debug.print(strSql);
            if (ps.executeUpdate() <= 0) {
                throw new Exception("还原底稿失败!!");
            }

            String templatePath = ManuFileService.getTemplateDir(typeId).getPath();
            File taskFile = new File(templatePath + "/" + taskId);
            File recycleFile = new File(templatePath + "/recycle/" + taskId + "_" + version);

            //复制回收站底稿
            new ManuFileService(conn).copyFile(recycleFile, taskFile);

        } catch (Exception e) {
            throw e;
        } finally {
            DbUtil.close(ps);
        }
    }

	/**
	 * 拷贝项目底稿文件到回收站目录
	 * @param projectId
	 * @param taskId
	 * @param version
	 */
	public void copyFile(String taskId, int version) {
		try {
			ManuFileService manuScriptManage = new ManuFileService(conn);
			File template = ManuFileService.getTemplateDir(typeId);
			File task = new File(template.getPath() + "/" + taskId);
			File recycle = new File(template.getPath() + "/recycle/" + taskId
					+ "_" + version);

			recycle.getParentFile().mkdirs();
			manuScriptManage.copyFile(task, recycle);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
