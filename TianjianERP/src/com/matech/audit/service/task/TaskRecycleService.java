package com.matech.audit.service.task;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.audit.service.task.model.Task;
import com.matech.audit.service.task.model.TaskRecycle;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.Debug;

/**
 * <p>Title: 底稿回收站类</p>
 * <p>Description: 对项目底稿在回收站的各种操作</p>
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
 * 2007-6-25
 */
public class TaskRecycleService {

    private Connection conn = null;
    private String projectId = null;

    public TaskRecycleService(Connection conn, String projectId) throws Exception{
		DbUtil.checkConn(conn);
		this.conn = conn;

		if("".equals(projectId) || projectId == null) {
			throw new Exception("项目Id不能为空!");
		}
		this.projectId = projectId;
    }

    /**
     * 复制底稿记录到回收站表
     * @param projectId
     * @param taskId
     * @throws Exception
     */
    public void moveToRecycle(String taskId,String saveType) throws Exception {

        ResultSet rs = null;
        PreparedStatement ps = null;
        int maxVersion = 0;
        try {
        	new DBConnect().changeDataBaseByProjectid(conn, projectId);

            //找出该底稿最大的版本号+1
            String strSql = " select ifnull(max(version),0) + 1 from asdb.z_taskrecycle "
		                    + " where projectId = ? "
		                    + " and taskId = ? ";
            DbUtil dbUtil = new DbUtil(conn);
            Object[] params = new Object[] {new Integer(projectId),taskId};

            maxVersion = dbUtil.queryForInt(strSql, params);

            strSql = " select '',b.Udate,'',b.UserName,'', " //line 1
                     + " b.TaskID,b.TaskCode,b.TaskName,b.TaskContent,b.Description,b.ParentTaskID,b.ProjectID,b.IsLeaf, " //line 2
                     + " b.Level0,b.ManuID,b.ManuTemplateID,b.user0,b.User1,b.User2,b.User3,b.User4,b.User5,b.Property, " //line 3
                     + " b.FullPath,b.date1,b.date2,b.date3,b.date4,b.date5,b.createdate,b.orderid,b.ismust,b.SubjectName," //line 4
                     + " b.auditproperty,b.levelBack "//line 5
                     + " from z_task b"
                     + " where b.projectId = ? "
                     + " and b.projectId = ? "
                     + " and b.taskId = ? ";
            ps = conn.prepareStatement(strSql);
            ps.setString(1, projectId);
            ps.setString(2, projectId);
            ps.setString(3, taskId);

            rs = ps.executeQuery();
            if (rs.next()) {
                TaskRecycle taskRecle = new TaskRecycle();

                //line 1
                taskRecle.setMime(rs.getString(1));
                taskRecle.setUdate(rs.getString(2));
                taskRecle.setUserId(rs.getString(3));
                taskRecle.setUserName(rs.getString(4));
                taskRecle.setMemo(rs.getString(5));

                //line 2
                taskRecle.setTaskID(rs.getString(6));
                taskRecle.setTaskCode(rs.getString(7));
                taskRecle.setTaskName(rs.getString(8));
                taskRecle.setTaskContent(rs.getString(9));
                taskRecle.setDescription(rs.getString(10));
                taskRecle.setParentTaskID(rs.getString(11));
                taskRecle.setProjectID(rs.getString(12));
                taskRecle.setIsLeaf(rs.getInt(13));

                //line 3
                taskRecle.setLevel(rs.getInt(14));
                taskRecle.setManuID(rs.getString(15));
                taskRecle.setManuTemplateID(rs.getString(16));
                taskRecle.setUser0(rs.getString(17));
                taskRecle.setUser1(rs.getString(18));
                taskRecle.setUser2(rs.getString(19));
                taskRecle.setUser3(rs.getString(20));
                taskRecle.setUser4(rs.getString(21));
                taskRecle.setUser5(rs.getString(22));
                taskRecle.setProperty(rs.getString(23));

                //line 4
                taskRecle.setFullPath(rs.getString(24));
                taskRecle.setDate1(rs.getString(25));
                taskRecle.setDate2(rs.getString(26));
                taskRecle.setDate3(rs.getString(27));
                taskRecle.setDate4(rs.getString(28));
                taskRecle.setDate5(rs.getString(29));
                taskRecle.setCreatedate(rs.getString(30));
                taskRecle.setOrderid(rs.getString(31));
                taskRecle.setIsmust(rs.getInt(32));
                taskRecle.setSubjectName(rs.getString(33));
                
                //line 5
                if("0".equals(saveType)) {
                	taskRecle.setSaveType("0") ;
                }else if("1".equals(saveType)) {
                	taskRecle.setSaveType("1") ;
                }
                
                taskRecle.setAuditproperty(rs.getString(34)) ;
                taskRecle.setLevelBack(rs.getString(35)) ;

                taskRecle.setVersion(maxVersion);

                saveToRecycle(taskRecle);
                copyFile(taskId, maxVersion);
            }
        } catch (Exception e) {
            Debug.print(Debug.iError,"访问失败",e);
        } finally {
            DbUtil.close(rs);
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
            File project = manuScriptManage.getProjectDir(projectId);
            File task = new File(project.getPath() + "/" + taskId);
            File recycle = new File(project.getPath() + "/recycle/" + taskId +
                                    "_" + version);

            recycle.getParentFile().mkdirs();
            manuScriptManage.copyFile(task, recycle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存到回收站表
     * @param taskRecle
     */
    public void saveToRecycle(TaskRecycle taskRecle) {

        PreparedStatement ps = null;

        try {
            String strSql =
                    " insert into asdb.z_taskrecycle (Mime,Udate,UserId,UserName,Memo, "
                    + " TaskID,TaskCode,TaskName,TaskContent,Description,ParentTaskID,ProjectID,IsLeaf, "
                    + " Level0,ManuID,ManuTemplateID,user0,User1,User2,User3,User4,User5,Property, "
                    + " FullPath,date1,date2,date3,date4,date5,createdate,orderid,ismust,SubjectName,version,versiondate,auditproperty,levelBack,saveType) "
                    + " values(?,?,?,?,?, " // line 1
                    + " 	   ?,?,?,?,?,?,?,?, " // line 2
                    + "        ?,?,?,?,?,?,?,?,?,?, " // line 3
                    + "        ?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?)"; // line 4

            ps = conn.prepareStatement(strSql);

            //line 1
            ps.setString(1, taskRecle.getMime());
            ps.setString(2, taskRecle.getUdate());
            ps.setString(3, taskRecle.getUserId());
            ps.setString(4, taskRecle.getUserName());
            ps.setString(5, taskRecle.getMemo());

            //line 2
            ps.setString(6, taskRecle.getTaskID());
            ps.setString(7, taskRecle.getTaskCode());
            ps.setString(8, taskRecle.getTaskName());
            ps.setString(9, taskRecle.getTaskContent());
            ps.setString(10, taskRecle.getDescription());
            ps.setString(11, taskRecle.getParentTaskID());
            ps.setString(12, taskRecle.getProjectID());
            ps.setInt(13, taskRecle.getIsLeaf());

            //line 3
            ps.setInt(14, taskRecle.getLevel());
            ps.setString(15, taskRecle.getManuID());
            ps.setString(16, taskRecle.getManuTemplateID());
            ps.setString(17, taskRecle.getUser0());
            ps.setString(18, taskRecle.getUser1());
            ps.setString(19, taskRecle.getUser2());
            ps.setString(20, taskRecle.getUser3());
            ps.setString(21, taskRecle.getUser4());
            ps.setString(22, taskRecle.getUser5());
            ps.setString(23, taskRecle.getProperty());

            //line 4
            ps.setString(24, taskRecle.getFullPath());
            ps.setString(25, taskRecle.getDate1());
            ps.setString(26, taskRecle.getDate2());
            ps.setString(27, taskRecle.getDate3());
            ps.setString(28, taskRecle.getDate4());
            ps.setString(29, taskRecle.getDate5());
            ps.setString(30, taskRecle.getCreatedate());
            ps.setString(31, taskRecle.getOrderid());
            ps.setInt(32, taskRecle.getIsmust());
            ps.setString(33, taskRecle.getSubjectName());

            ps.setInt(34, taskRecle.getVersion());
            ps.setString(35,taskRecle.getAuditproperty()) ;
            ps.setString(36,taskRecle.getLevelBack()) ;
            ps.setString(37,taskRecle.getSaveType()) ;

            ps.execute();
        } catch (Exception ex) {
        	ex.printStackTrace();
        } finally {
            DbUtil.close(ps);
        }
    }

    /**
     * 根据AUTOID获得回收站文件信息
     * @param autoId
     * @return
     * @throws Exception
     */
    public TaskRecycle getTaskRecycleByAutoId(String autoId) throws Exception {

        ResultSet rs = null;
        PreparedStatement ps = null;
        TaskRecycle taskRecle = new TaskRecycle();
        try {
            //找出该底稿最大的版本号+1
            String strSql = " select Mime,Udate,UserId,UserName,Memo, " //line 1
                            + " TaskID,TaskCode,TaskName,TaskContent,Description,ParentTaskID,ProjectID,IsLeaf, " //line 2
                            + " Level0,ManuID,ManuTemplateID,user0,User1,User2,User3,User4,User5,Property, " //line 3
                            + " FullPath,date1,date2,date3,date4,date5,createdate,orderid,ismust,SubjectName,version,versiondate,autoid,auditproperty,levelBack " //line 4
                            + " from asdb.z_taskrecycle "
                            + " where autoId = ? ";

            ps = conn.prepareStatement(strSql);
            ps.setString(1, autoId);

            rs = ps.executeQuery();
            if (rs.next()) {

                //line 1
                taskRecle.setMime(rs.getString(1));
                taskRecle.setUdate(rs.getString(2));
                taskRecle.setUserId(rs.getString(3));
                taskRecle.setUserName(rs.getString(4));
                taskRecle.setMemo(rs.getString(5));

                //line 2
                taskRecle.setTaskID(rs.getString(6));
                taskRecle.setTaskCode(rs.getString(7));
                taskRecle.setTaskName(rs.getString(8));
                taskRecle.setTaskContent(rs.getString(9));
                taskRecle.setDescription(rs.getString(10));
                taskRecle.setParentTaskID(rs.getString(11));
                taskRecle.setProjectID(rs.getString(12));
                taskRecle.setIsLeaf(rs.getInt(13));

                //line 3
                taskRecle.setLevel(rs.getInt(14));
                taskRecle.setManuID(rs.getString(15));
                taskRecle.setManuTemplateID(rs.getString(16));
                taskRecle.setUser0(rs.getString(17));
                taskRecle.setUser1(rs.getString(18));
                taskRecle.setUser2(rs.getString(19));
                taskRecle.setUser3(rs.getString(20));
                taskRecle.setUser4(rs.getString(21));
                taskRecle.setUser5(rs.getString(22));
                taskRecle.setProperty(rs.getString(23));

                //line 4
                taskRecle.setFullPath(rs.getString(24));
                taskRecle.setDate1(rs.getString(25));
                taskRecle.setDate2(rs.getString(26));
                taskRecle.setDate3(rs.getString(27));
                taskRecle.setDate4(rs.getString(28));
                taskRecle.setDate5(rs.getString(29));
                taskRecle.setCreatedate(rs.getString(30));
                taskRecle.setOrderid(rs.getString(31));
                taskRecle.setIsmust(rs.getInt(32));
                taskRecle.setSubjectName(rs.getString(33));
                taskRecle.setVersion(rs.getInt(34));
                taskRecle.setVersiondate(rs.getString(35));
                taskRecle.setAutoid(rs.getInt(36));
                taskRecle.setAuditproperty(rs.getString(37)) ;
                taskRecle.setLevelBack(rs.getString(38)) ;
            }

        } catch (Exception e) {
            Debug.print(Debug.iError,"访问失败",e);
        } finally {
            DbUtil.close(rs);
            DbUtil.close(ps);
        }

        return taskRecle;
    }

    /**
     * 根据AUTOID删除回收站的某条记录
     * @param autoId
     * @throws Exception
     */
    public void deleteRecycleByAutoId(String autoId) throws Exception {

        PreparedStatement ps = null;

        try {
            DbUtil.checkConn(conn);

            ManuFileService manuScriptManage = new ManuFileService(conn);
            TaskRecycle taskRecycle = getTaskRecycleByAutoId(autoId);
            File projectFile = manuScriptManage.getProjectDir(taskRecycle.
                    getProjectID());
            File taskFile = new File(projectFile.getPath() + "/recycle/" +
                                     taskRecycle.getTaskID() + "_" +
                                     taskRecycle.getVersion());

            DBConnect db=new DBConnect();
            db.changeDataBaseByProjectid(projectId, conn);



            //追加删除回收站的时候删除新增3大表内容；
            String strSql = "delete t1 from z_taskreferData t1,z_taskrecycle t2 \n"
            	+"where t2.autoid=? and t2.projectid=t1.projectid and t2.taskid=t1.taskid;";
            ps = conn.prepareStatement(strSql);
            ps.setString(1, autoId);
            ps.executeUpdate();
            DbUtil.close(ps);

            strSql = "delete t1 from z_taskrefer t1,z_taskrecycle t2 \n"
            	+"where t2.autoid=? and t2.projectid=t1.projectid and t2.taskid=t1.taskid;";
            ps = conn.prepareStatement(strSql);
            ps.setString(1, autoId);
            ps.executeUpdate();
            DbUtil.close(ps);

            strSql = "delete t1 from z_manudata t1,z_taskrecycle t2 \n"
            	+"where t2.autoid=? and t2.projectid=t1.projectid and t2.taskid=t1.taskid;";
            ps = conn.prepareStatement(strSql);
            ps.setString(1, autoId);
            ps.executeUpdate();
            DbUtil.close(ps);

            strSql = "delete from asdb.z_taskrecycle where autoid = ?";
            ps = conn.prepareStatement(strSql);
            ps.setString(1, autoId);
            if (ps.executeUpdate() > 0) {
                if (taskFile.exists()) {
                    taskFile.delete();
                }
            }
        } catch (Exception e) {
            Debug.print(Debug.iError,"访问失败",e);
        } finally {
            DbUtil.close(ps);
        }
    }


    /**
     * 还原底稿到项目
     * @param autoId
     * @throws Exception
     */
    public void recoverTaskByAutoId(String autoId, String parentTaskId) throws Exception {
        PreparedStatement ps = null;

        try {

            ManuFileService manuScriptManage = new ManuFileService(conn);
            TaskRecycle taskRecycle = getTaskRecycleByAutoId(autoId);

            String projectId = taskRecycle.getProjectID();
            String taskId = taskRecycle.getTaskID();
            int version = taskRecycle.getVersion();

            DBConnect dbc=new DBConnect();
        	dbc.changeDataBaseByProjectid(conn, projectId);

            TaskService taskService = new TaskService(conn,projectId);
            Task task = taskService.getTaskByTaskId(parentTaskId);
            String taskName = task.getTaskName();
            String fullPath = task.getFullPath() + taskId + "|";

            //判断父级底稿是否存在,不存在的话报错
            if ("".equals(taskName)) {
                throw new Exception("父级任务不存在,还原失败");
            }

            //删除原来项目中的底稿
            taskService.removeTask(taskId);

            String strSql = "insert into z_task (TaskID, TaskCode, TaskName, TaskContent, Description, ParentTaskID,ProjectID,IsLeaf,"
                            + "  Level0,ManuID,ManuTemplateID,user0,User1,User2,User3,User4,User5,Property,FullPath,date1,date2,date3,date4,date5,"
                            + "	createdate,orderid,ismust,SubjectName,udate,userName,auditproperty,levelBack) "

                            + " select TaskID, TaskCode, TaskName, TaskContent, Description, ?,ProjectID,IsLeaf,"
                            + " Level0,ManuID,ManuTemplateID,user0,User1,User2,User3,User4,User5,Property,?,date1,date2,date3,date4,date5,"
                            + " createdate,orderid,ismust,SubjectName,udate,userName,auditproperty,levelBack"
                            + " from asdb.z_taskrecycle where autoid = ? ";

            ps = conn.prepareStatement(strSql);
            ps.setString(1, parentTaskId);
            ps.setString(2, fullPath);
            ps.setString(3, autoId);

            Debug.print(strSql);
            if (ps.executeUpdate() <= 0) {
                throw new Exception("还原数据到任务表失败!!");
            }

            String projectPath = manuScriptManage.getProjectDir(projectId).
                                 getPath();
            File taskFile = new File(projectPath + "/" + taskId);
            File recycleFile = new File(projectPath + "/recycle/" + taskId +
                                        "_" + version);

            //复制回收站底稿到项目目录,然后删除回收站的数据
            recycleFile.renameTo(taskFile);
            deleteRecycleByAutoId(autoId);

        } catch (Exception e) {
            throw e;
        } finally {
            DbUtil.close(ps);
        }
    }

    /**
     * 清空项目底稿回收站
     * @throws Exception
     */
    public void clearRecycle() throws Exception {
        PreparedStatement ps = null;

        try {

            ManuFileService manuScriptManage = new ManuFileService(conn);
            File recycleDir = new File(manuScriptManage.getProjectDir(projectId).
                                       getPath() + "/recycle/");

            DBConnect db=new DBConnect();
            db.changeDataBaseByProjectid(projectId, conn);

            //追加删除回收站的时候删除新增3大表内容；
            String strSql = "delete t1 from z_taskreferData t1,z_taskrecycle t2 \n"
            	+"where t2.projectId=? and t2.projectid=t1.projectid and t2.taskid=t1.taskid;";
            ps = conn.prepareStatement(strSql);
            ps.setString(1, projectId);
            ps.executeUpdate();
            DbUtil.close(ps);

            strSql = "delete t1 from z_taskrefer t1,z_taskrecycle t2 \n"
            	+"where t2.projectId=? and t2.projectid=t1.projectid and t2.taskid=t1.taskid;";
            ps = conn.prepareStatement(strSql);
            ps.setString(1, projectId);
            ps.executeUpdate();
            DbUtil.close(ps);

            strSql = "delete t1 from z_manudata t1,z_taskrecycle t2 \n"
            	+"where t2.projectId=? and t2.projectid=t1.projectid and t2.taskid=t1.taskid;";
            ps = conn.prepareStatement(strSql);
            ps.setString(1, projectId);
            ps.executeUpdate();
            DbUtil.close(ps);


            strSql = "delete from asdb.z_taskrecycle where projectId = ?";
            ps = conn.prepareStatement(strSql);
            ps.setString(1, projectId);

            if (ps.executeUpdate() > 0) {
                if (recycleDir.exists()) {
                	ManuFileService.deleteFile(recycleDir);
                }
            }
        } catch (Exception e) {
        	throw e;
        } finally {
            DbUtil.close(ps);
        }
    }

    public static void main(String[] args) throws Exception {
        Connection conn=new DBConnect().getConnect("");
        TaskRecycleService taskRecycleMan = new TaskRecycleService(conn,"2007632");
//		taskRecycleMan.deleteRecycleByAutoId("2");

        taskRecycleMan.clearRecycle();
    }
}
