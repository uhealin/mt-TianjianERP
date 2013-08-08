package com.matech.audit.service.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.task.model.Tache;
import com.matech.framework.pub.db.DbUtil;

/**
 * <p>Title: 项目审计环节管理</p>
 * <p>Description: 选择项目的审计环节,对审计环节下的审计程序进行分工等</p>
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
 * 2007-8-3
 */
public class TacheService {
	private Connection conn = null;

	private String projectId = null;

	/**
	 * 构造方法,初始化数据库连接和项目编号
	 * @param conn	数据库连接
	 * @param projectId	项目编号
	 * @throws Exception
	 */
	public TacheService(Connection conn, String projectId) throws Exception {
		DbUtil.checkConn(conn);
		this.conn = conn;

		if("".equals(projectId) || projectId == null) {
			throw new Exception("项目Id不能为空!");
		}

		this.projectId = projectId;
	}

	/**
	 * 保存选择的环节以及分工,有4种情况出现：
	 * 	1、构选新的环节,将copy该环节下的所有子环节、目标、程序以及底稿
	 * 	2、取消原来所选的环节,将z_task表里该环节下的所有子环节，目标等移动到z_taskdelete下，其他不作处理
	 * 	3、重新选择环节,直接从z_taskdelete表种拷贝相关记录到z_task中
	 * 	4、只更改了执行人,将更新环节下的所有审计程序的执行人
	 *
	 * @param taskIds	所选的一级环节编号
	 * @param executors
	 * @throws Exception
	 */
	public void saveSelectTache(String[] taskIds, String[] executors ) throws Exception {

		if(taskIds != null && executors !=null ) {

			//判断环节数跟分工数是否一致
			if(taskIds.length == executors.length) {
				List tacheList= getTacheList();	//获得项目中的一级环节列表

				List removeList = tacheList;
				List saveList = new ArrayList(Arrays.asList(taskIds));
				List executorList = new ArrayList(Arrays.asList(executors));

				List tempList = new ArrayList(tacheList);
				tempList.retainAll(saveList); //求出交集

				//求出需要增加的环节taskId列表,因为这里要同步删除分工人,所有不能用removeAll
				for(int j=0; j < tempList.size(); j++) {
					for(int i=0; i < saveList.size(); i++) {
						if(saveList.get(i).equals(tempList.get(j))) {
							//更新执行人
							updateExecutor((String)saveList.get(i),(String)executorList.get(i));
							saveList.remove(i);
							executorList.remove(i);
							break;
						}
					}
				}

				//求出需要删除的环节taskId列表
				removeList.removeAll(tempList);

				//删除环节
				for(int i=0; i <removeList.size(); i++) {
					removeTache((String)removeList.get(i));
				}

				//保存环节
				for(int i=0; i <saveList.size(); i++) {
					String taskId = (String)saveList.get(i);
					String executor = (String)executorList.get(i);
					saveTache(taskId,executor);
				}
			}
		}
	}

	/**
	 * 更新环节下面的所有程序的执行人
	 * @param taskId
	 * @param executor
	 * @throws Exception
	 */
	private void updateExecutor(String taskId,String executor) throws Exception {
		String sql = " update z_procedure a,z_task b set a.executor = ? "
			+ " where a.taskId like concat('%~',b.taskId,'~%') "
			+ " and b.fullpath like ? "
			+ " and a.projectId = ? "
			+ " and b.projectId = ? ";

		Object[] params = new Object[]{executor, taskId + "|%",this.projectId,this.projectId};
		new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
		DbUtil dbUtil = new DbUtil(conn);
		dbUtil.executeUpdate(sql, params);

		String userIds = "";

		//根据名字找到id
		if(!"".equals(executor)) {
			String executors = "'-1'";
			String[] userId = executor.split(",");

			for(int i=0; i < userId.length; i++) {
				executors += ",'" + userId[i] + "'";
			}

			sql = " select ifnull(group_concat(id),-1) from k_user where name in(" + executors +") and state=0 ";
			userIds = dbUtil.queryForString(sql);
		}

		//更新环节的user0字段
		sql = "update z_task set user0=? where projectId=? and taskId=? ";
		params = new Object[]{userIds, this.projectId, taskId };
		dbUtil.executeUpdate(sql, params);
	}
	/**
	 * 拷贝环节
	 * @param taskId
	 * @throws Exception
	 */
	private void saveTache(String taskId,String executor) throws Exception {

		DbUtil dbUtil = new DbUtil(conn);
		String sql = "select count(1) from z_task where taskId = ? and projectId = ? ";
		Object[] params = new Object[]{taskId,this.projectId};
		if(dbUtil.queryForInt(sql,params) > 0) {
			throw new Exception("该环节已经存在...");
		}

		sql = "select count(1) from z_taskdelete where taskId = ? and projectId = ?";
		params = new Object[]{taskId,this.projectId};

		int count = dbUtil.queryForInt(sql,params);

		System.out.println(count);
		if(count > 0) {
			//从备份表中复制
			sql = " insert into z_task "
				+ " SELECT * FROM z_taskdelete"
				+ " where projectid = ? "
				+ " and fullpath like ? ";
			params = new Object[]{this.projectId, taskId + "|%"};

			if(dbUtil.executeUpdate(sql, params) > 0) {
				//删除备份表中的信息
				sql = " delete from z_taskdelete "
					+ " where projectid = ? "
					+ " and fullpath like ? ";
				dbUtil.executeUpdate(sql, params);
			}

			sql = " update z_procedure a,z_task b set a.executor = ? "
				+ " where a.taskId like concat('%~',b.taskId,'~%') "
				+ " and b.fullpath like ? "
				+ " and a.projectId = ? "
				+ " and b.projectId = ? ";

			params = new Object[]{executor, taskId + "|%",this.projectId,this.projectId};
			dbUtil.executeUpdate(sql, params);

		} else {
			//获得审计类型编号
			String auditType = new ProjectService(conn).getProjectById(this.projectId).getAuditType();

			sql = " insert into z_task( ProjectID,Taskid,taskcode,TaskName,TaskContent,"
				+ " Description,property,ParentTaskID,IsLeaf,Level0, "
				+ " Fullpath,ManuID,ManuTemplateID,orderid,ismust,SubjectName) "

				+ " SELECT ?,Taskid,taskcode,TaskName,TaskContent, "
				+ " Description,property,ParentTaskID,IsLeaf,Level0, "
				+ " Fullpath,manutemplateid,ManuTemplateID,orderid,ismust,SubjectName"
				+ " FROM k_tasktemplate "
				+ " where typeId = ? "
				+ " and fullpath like ? ";

			params = new Object[]{this.projectId,auditType, taskId + "|%"};
			dbUtil.executeUpdate(sql, params);

			params = new Object[]{auditType, taskId + "|%"};
			//获得模版中所有底稿taskId
			sql = "select group_concat(taskId) from  k_tasktemplate where typeid=? and isleaf=1 and fullpath like ? ";
			String taskIds = dbUtil.queryForString(sql,params);

			if(taskIds != null && !"".equals(taskIds) && !"null".equals(taskIds)) {
				//拷贝底稿文件到项目文件夹
				new ManuFileService(conn).copyTemplateToProject(auditType, this.projectId, taskIds);
			}

			sql = "update z_task set manuid=null where ProjectID=? and isleaf=0 ";
			params = new Object[]{this.projectId };
			dbUtil.executeUpdate(sql, params);

			String userIds = "";

			//根据名字找到id
			if(!"".equals(executor)) {
				String executors = "'-1'";
				String[] userId = executor.split(",");

				for(int i=0; i < userId.length; i++) {
					executors += ",'" + userId[i] + "'";
				}

				sql = " select ifnull(group_concat(id),-1) from k_user where name in(" + executors +") and state=0 ";
				userIds = dbUtil.queryForString(sql);
			}

			//更新环节的user0字段
			sql = "update z_task set user0=? where projectId=? and taskId=? ";
			params = new Object[]{userIds, this.projectId, taskId };
			dbUtil.executeUpdate(sql, params);

			//拷贝表页
			sql = "insert into z_sheettask(ProjectID,taskid,sheettaskcode,taskcode,sheetname,property ) "
				+ " select ?,a.TaskID,a.sheettaskcode,a.taskcode,a.sheetname,a.property "
				+ " from k_sheettasktemplate a,k_tasktemplate b  "
				+ " where a.typeid= ? "
				+ " and b.typeid= ? "
				+ " and a.taskId = b.taskId "
				+ " and b.fullpath like ? ";

			params = new Object[]{this.projectId,auditType,auditType,taskId + "|%"};
			dbUtil.executeUpdate(sql, params);

			//拷贝审计目标
			sql = "insert into z_target(ProjectID,TaskID,ExecuteIt,State,DefineID,AuditTarget,CorrelationExeProceDure,Remark) "
				+ " select ?,a.TaskID,a.ExecuteIt,a.State,a.DefineID,a.AuditTarget,a.CorrelationExeProceDure,a.Remark "
				+ " from k_targettemplate a,k_tasktemplate b  "
				+ " where a.typeid= ? "
				+ " and b.typeid= ? "
				+ " and a.taskId = b.taskId "
				+ " and b.fullpath like ? ";

			params = new Object[]{this.projectId,auditType,auditType,taskId + "|%"};
			dbUtil.executeUpdate(sql, params);

			//拷贝审计程序
			sql = "insert into z_procedure(autoId,ProjectID,TaskID,State,DefineID,AuditProcedure,Manuscript,Executor,Remark)"
				+ " select autoId,?,a.TaskID,a.State,a.DefineID,a.AuditProcedure,a.Manuscript,?,a.Remark "
				+ " from k_proceduretemplate a,k_tasktemplate b "
				+ " where a.typeid= ? "
				+ " and b.typeid= ? "
				+ " and a.taskid like concat('%~',b.taskid,'~%') "
				+ " and b.fullpath like ? ";
			params = new Object[]{this.projectId,executor,auditType,auditType,taskId + "|%"};
			dbUtil.executeUpdate(sql, params);
		}
	}

	/**
	 * 删除环节
	 * @param taskId
	 * @throws Exception
	 */
	private void removeTache(String taskId) throws Exception {
		new DBConnect().changeDataBaseByProjectid(conn, projectId);
		String sql = "insert into z_taskdelete "
					+ " select * from z_task "
					+ " where fullpath like ?";

		DbUtil dbUtil = new DbUtil(conn);
		Object[] params = new Object[]{taskId + "|%" };

		if(dbUtil.executeUpdate(sql,params) > 0) {
			sql = "delete from z_task "
				+ " where fullpath like ?";
			dbUtil.executeUpdate(sql,params);
		}
	}


	/**
	 * 获得项目中现有的环节列表
	 * @return
	 * @throws Exception
	 */
	private List getTacheList() throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		List tacheList = new ArrayList();
		try {
			new DBConnect().changeDataBaseByProjectid(conn, projectId);
			String sql = " select taskid from z_task "
						+ " where projectid = ? "
						+ " and parentTaskId = '0' "
						+ " and property = 'tache' " ;
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectId);
			rs = ps.executeQuery();

			while(rs.next()) {
				tacheList.add(rs.getString(1));
			}

			return tacheList;
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 获得任务列表
	 * @param parentTaskId	上级编号
	 * @return
	 * @throws Exception
	 */
	public List getTacheList(String parentTaskId) throws Exception {
		List taskTemplateList = new ArrayList();
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = " select b.taskid,b.taskcode,b.taskname, "
					+ " exists(select 1 from z_task a where projectid = ? and a.taskid = b.taskid) isAdd "
					+ " from k_tasktemplate b,z_project c "
					+ " where c.projectid = ? "
					+ " and c.audittype =b.typeid "
					+ " and b.parentTaskId = ? "
					+ " and b.property = 'tache'"
					+ " order by b.orderid " ;

		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);
			ps.setString(2, this.projectId);
			ps.setString(3, parentTaskId);

			Tache tache;
			rs = ps.executeQuery();
			while(rs.next()) {
				tache = new Tache();
				String taskId = rs.getString(1);
				tache.setTaskId(taskId);
				tache.setTaskCode(rs.getString(2));
				tache.setTaskName(rs.getString(3));
				tache.setIsAdd(rs.getInt(4));
				tache.setExecutor(getTacheExecutor(taskId));

				taskTemplateList.add(tache);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return taskTemplateList;
	}

	/**
	 * 获得任务列表(项目中的)
	 * @param parentTaskId	上级编号
	 * @return
	 * @throws Exception
	 */
	public List getTacheListForProject(String parentTaskId) throws Exception {
		List tacheList = new ArrayList();
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = " select taskid,taskcode,taskname, "
					+ " from z_task "
					+ " where parentTaskId = ? "
					+ " and projectid = ? "
					+ " and property = 'tache'"
					+ " order by orderid " ;

		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			ps = conn.prepareStatement(sql);
			ps.setString(1, parentTaskId);
			ps.setString(2, this.projectId);

			Tache tache = null;
			rs = ps.executeQuery();
			while(rs.next()) {
				tache = new Tache();
				tache.setTaskId(rs.getString(1));
				tache.setTaskCode(rs.getString(2));
				tache.setTaskName(rs.getString(3));

				tacheList.add(tache);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return tacheList;
	}

	/**
	 * 获得环节下的所有审计程序的执行人,用","分隔
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	private String getTacheExecutor(String taskId) throws Exception {

		String sql = " select GROUP_CONCAT(DISTINCT b.Executor) as Executor "
			+ " from z_task a,z_procedure b,z_task c "
			+ " where a.projectid=? "
			+ " and b.projectid=?  "
			+ " and c.projectid=?  "
			+ " and a.taskid=? "
			+ " and c.fullpath like concat(a.fullpath,'%') "
			+ " and b.taskid=c.taskid ";

		Object[] params = new Object[]{this.projectId,this.projectId,this.projectId, taskId};

		new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
		return new DbUtil(conn).queryForString(sql,params);

	}

	/**
	 * 根据审计环节设置分工人
	 * @param taskId
	 * @param executor
	 * @throws Exception
	 */
	public void setExecutorByTache(String taskId, String executor) throws Exception {

		String sql = "update z_task a,z_procedure b,z_task c "
					+ " set b.Executor=? "
					+ " where a.projectid=? "
					+ " and b.projectid=?  "
					+ " and c.projectid=?  "
					+ " and a.taskid=? "
					+ " and c.fullpath like concat(a.fullpath,'%') "
					+ " and b.taskid=c.taskid ";

		Object[] params = new Object[]{executor,this.projectId,this.projectId,this.projectId, taskId};

		//如果是最上级
		if(taskId == null || "0".equals(taskId) || "".equals(taskId)) {

			sql = " update z_procedure set Executor=? "
				+ " where projectid=? ";

			params = new Object[]{executor,this.projectId};
		}
		new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
		new DbUtil(conn).executeUpdate(sql,params);
	}

	/**
	 * test method
	 * @param args
	 */
	public static void main(String[] args) {
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			//new TacheService(conn,"20077721").saveTache("102082", "void");
			String ss = new TacheService(conn,"20077722").getTacheExecutor("102082");

			System.out.println(ss);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
	}
}
