package com.matech.audit.service.task;

import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.customer.CustomerService;
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.task.model.SheetTask;
import com.matech.audit.service.task.model.Task;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.pub.util.UTILString;

public class TaskService {

	private Connection conn = null;

	private String projectId = null;

	/**
	 * 构造方法,初始化数据库连接和项目编号
	 * @param conn	数据库连接
	 * @param projectId	项目编号
	 * @throws Exception
	 */
	public TaskService(Connection conn, String projectId) throws Exception {
		DbUtil.checkConn(conn);
		this.conn = conn;

		if("".equals(projectId) || projectId == null) {
			throw new Exception("项目Id不能为空!");
		}

		this.projectId = projectId;
	}

	/**
	 * 增加一张底稿,该方法会重新计算level,fullPath,orderid
	 * @param task
	 * @throws Exception
	 */
	public void addTask(Task task) throws Exception {
		PreparedStatement ps = null;
		try {
			new DBConnect().changeDataBaseByProjectid(conn, projectId);

			String parentTaskId = task.getParentTaskId();

			int level = task.getLevel();
			String fullPath = task.getFullPath();

			if("0".equals(parentTaskId)) {
				level = 0;
				fullPath = task.getTaskId() + "|";

			} else {
				Task parentTask = getTaskByTaskId(parentTaskId);

				if(level == 0) {
					level = parentTask.getLevel() + 1;	//层次
				}

				if(fullPath == null) {
					fullPath = parentTask.getFullPath() + task.getTaskId() + "|";
				}
			}

			String sql = "INSERT INTO z_Task( "
						+ " Taskid,Taskcode,TaskName,TaskContent,Description,ParentTaskID, "
						+ " ProjectID,IsLeaf,Level0,Fullpath,Property,orderid,subjectname,auditproperty) "
						+ " VALUES(?,?,?,?,?,?, ?,?,?,?,?,?, ?,?)";

			ps = conn.prepareStatement(sql);

			ps.setString(1, task.getTaskId());
			ps.setString(2, task.getTaskCode());
			ps.setString(3, task.getTaskName());
			ps.setString(4, task.getTaskContent());
			ps.setString(5, task.getDescription());
			ps.setString(6, task.getParentTaskId());

			ps.setString(7, projectId);
			ps.setInt(8, task.getIsLeaf());
			ps.setInt(9, level);
			ps.setString(10, fullPath);
			ps.setString(11, task.getProperty());
			ps.setString(12, UTILString.getOrderId(task.getTaskCode()));
			ps.setString(13, task.getSubjectName());

			ps.setString(14, task.getAuditproperty());

			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError, "增加底稿任务失败", e);
			throw e;
		} finally {
			DbUtil.close(ps);
		}

	}

	/**
	 * 计算出底稿全路径
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	private String calFullPath(String taskId) throws Exception {
		String fullPath = taskId + "|";
		String parentId = taskId;
		while (true) {
			parentId = getParentTaskId(parentId);
			if (parentId == null || parentId.equals("0")) {
				break;
			} else {
				fullPath = parentId + "|" + fullPath;
			}
		}
		return fullPath;
	}

	/**
	 * 检查是否有user1、user2、user3不为空的底稿（表示用户已经编制过）。
	 * @return
	 * @throws Exception
	 */
	public int checkTask() throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "select count(TaskId) from z_task "
					+ " where (user1 <> '' or user2 <> '' or user3 <> '') "
					+ " and projectId = ? ";

		try {
			new DBConnect().changeDataBaseByProjectid(conn, projectId);

			ps = conn.prepareStatement(sql);
			ps.setString(1, projectId);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
			return 0;
		} catch (Exception e) {
			Debug.print(Debug.iError, "检查底稿失败", e);
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 根据底稿id获得审计目标
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public String getAuditTarget(String taskId) throws Exception {
		StringBuffer sb = new StringBuffer();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = "select AuditTarget from `z_target` "
						+ " where AuditTarget > '' "
						+ " and projectid = ? "
						+ " and taskid = ? "
						+ " order by autoid";

			new DBConnect().changeDataBaseByProjectid(conn, projectId);
			ps = conn.prepareStatement(sql);

			ps.setString(1, projectId);
			ps.setString(2, taskId);

			rs = ps.executeQuery();

			int i = 1;

			while (rs.next()) {
				sb.append(i + "、" + rs.getString(1) + "; ");
				i++;
			}
			return sb.toString();

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 获得底稿全路径
	 *
	 * @param taskId 底稿ID
	 * @param curProjectId 当前项目ID
	 * @return
	 * @throws Exception
	 */
	public String getFullPath(String taskId) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;
		String fullPath = null;

		try {
			if ("0".equals(taskId)) {
				fullPath = "0";
			} else {
				new DBConnect().changeDataBaseByProjectid(conn, projectId);

				String sql = "select fullpath from z_Task "
							+ " where TaskID= ? "
							+ " and ProjectID = ? ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, taskId);
				ps.setString(2, projectId);
				rs = ps.executeQuery();

				if (rs.next()) {
					fullPath = rs.getString("fullpath");
				}
			}

			return fullPath;

		} catch (Exception e) {
			Debug.print(Debug.iError, "获取底稿全路径失败", e);
			throw e;

		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 获得最大的taskId
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public String getMaxTaskId() throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String taskId = null;

		try {
			new DBConnect().changeDataBaseByProjectid(conn, projectId);
			String strSql = "select ifnull(max(taskid),0)+1 from z_task";
			ps = conn.prepareStatement(strSql);
			rs = ps.executeQuery();
			if (rs.next()) {
				taskId = rs.getString(1);
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return taskId;
	}

	/**
	 * 根据任务id和项目id获得父结点的taskID
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public String getParentTaskId(String taskId) throws Exception {
		String parentTaskId = "";
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			String strSql = "select parentTaskId from `z_task` "
							+ " where taskid = ? "
							+ " and projectId = ?";

			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			ps = conn.prepareStatement(strSql);
			ps.setString(1, taskId);
			ps.setString(2, this.projectId);

			rs = ps.executeQuery();

			if (rs.next()) {
				parentTaskId = rs.getString(1);
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return parentTaskId;
	}

	/**
	 * 根据taskName获得任务详细信息
	 * @param taskCode
	 * @return
	 */
	public Task getTaskByTaskName(String taskName) throws Exception {

		new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
		DbUtil dbUtil = new DbUtil(conn);
		String sql = "select taskId from z_task "
				+ " where taskName LIKE CONCAT(?,'%') "
				+ "	and projectid= ? "
				+ " and isleaf = 1"
				+ " order by isleaf desc ";

		Object[] object = new Object[] {
				taskName,
				this.projectId
		};
		String taskId = dbUtil.queryForString(sql, object);
		
		return this.getTaskByTaskId(taskId);
	}
	
	/**
	 * 根据property获得任务详细信息
	 * @param property
	 * @return
	 */
	public String getTaskIdByProperty(String property) throws Exception {

		new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
		DbUtil dbUtil = new DbUtil(conn);
		String sql = "select taskId from z_task "
				+ "	where property= ? "
				+ "	and projectid= ? "
				+ " and isleaf = 1"
				+ " order by isleaf desc ";

		Object[] object = new Object[] {
				property,
				this.projectId
		};
		String taskId = dbUtil.queryForString(sql, object);
		
		return taskId;
	}
	
	/**
	 * 根据property获得环节
	 * @param property
	 * @return
	 */
	public String getSortIdByProperty(String property) throws Exception {

		new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
		DbUtil dbUtil = new DbUtil(conn);
		String sql = "select taskId from z_task "
				+ "	where property= ? "
				+ "	and projectid= ? "
				+ " and isleaf = 0"
				+ " order by isleaf desc ";

		Object[] object = new Object[] {
				property,
				this.projectId
		};
		String taskId = dbUtil.queryForString(sql, object);
		
		return taskId;
	}
	
	/**
	 * 根据taskcode获得任务详细信息
	 * @param taskCode
	 * @return
	 */
	public Task getTaskByTaskCode(String taskCode) throws Exception {

		String taskId = this.getTaskIdByTaskCode(taskCode);

		return this.getTaskByTaskId(taskId);
	}

	/**
	 * 根据taskcode获得任务详细信息,叶子节点
	 * @param taskCode
	 * @return
	 */
	public Task getTaskByTaskCodeIsleaf(String taskCode) throws Exception {

		String taskId = this.getTaskIdByTaskCodeIsLeaf(taskCode);

		return this.getTaskByTaskId(taskId);
	}

	/**
	 * 根据TaskId取得底稿详细信息
	 * @param taskId
	 * @return
	 */
	public Task getTaskByTaskId(String taskId) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		Task task = new Task();
		try {
			new DBConnect().changeDataBaseByProjectid(conn, projectId);

			String sql = "Select * From z_Task "
						+ " Where TaskID = ? "
						+ " and ProjectId = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, taskId);
			ps.setString(2, this.projectId);
			rs = ps.executeQuery();

			if (rs.next()) {
				task.setTaskId(rs.getString("Taskid"));
				task.setTaskCode(rs.getString("TaskCode"));
				task.setTaskName(rs.getString("Taskname"));
				task.setTaskContent(rs.getString("TaskContent"));
				task.setDescription(rs.getString("Description"));

				task.setParentTaskId(rs.getString("ParentTaskID"));
				task.setProjectId(rs.getString("ProjectID"));
				task.setIsLeaf(rs.getInt("IsLeaf"));
				task.setLevel(rs.getInt("Level0"));
				task.setManuId(rs.getString("manuid"));

				task.setManuTemplateId(rs.getString("manutemplateid"));
				task.setUser0(rs.getString("user0"));
				task.setUser1(rs.getString("user1"));
				task.setUser2(rs.getString("user2"));
				task.setUser3(rs.getString("user3"));

				task.setUser4(rs.getString("user4"));
				task.setUser5(rs.getString("user5"));
				
				task.setDate1(rs.getString("date1"));
				task.setDate2(rs.getString("date2"));
				task.setDate3(rs.getString("date3"));
				task.setDate4(rs.getString("date4"));
				task.setDate5(rs.getString("date5"));
				
				task.setProperty(rs.getString("property"));
				task.setFullPath(rs.getString("fullpath"));
				task.setSubjectName(rs.getString("subjectName"));
				task.setOrderId(rs.getString("orderid"));

				task.setDescription(rs.getString("description"));

				task.setAuditproperty(rs.getString("auditproperty"));
				
				task.setUserName(rs.getString("username"));
				task.setUdate(rs.getString("udate"));

			}

			return task;
		} catch (Exception e) {
			Debug.print(Debug.iError, "获得底稿信息失败", e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 获得分类下的底稿数
	 * @param parentTaskId
	 * @return
	 * @throws Exception
	 */
	public int getTaskCountByParentTaskId(String parentTaskId) throws Exception {
		new DBConnect().changeDataBaseByProjectid(conn, projectId);
		DbUtil dbUtil = new DbUtil(conn);

		String sql = "select count(*) from z_task "
							+ " where parenttaskid = ? "
							+ " and projectid = ? "
							+ " and isleaf=1 ";

		Object[] object = new Object[] {
				parentTaskId,
				new Integer(this.projectId)
		};

		return dbUtil.queryForInt(sql, object);
	}

	/**
	 * 根据TASKCODE获得taskId
	 * @param taskCode
	 * @return
	 * @throws Exception
	 */
	public String getTaskIdByTaskCode(String taskCode) throws Exception {
		new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
		DbUtil dbUtil = new DbUtil(conn);
		String sql = "select taskId from z_task "
				+ " where TaskCode = ? "
				+ "	and projectid= ? "
				+ " order by isleaf desc ";

		Object[] object = new Object[] {
				taskCode,
				this.projectId
		};
		return dbUtil.queryForString(sql, object);
	}
	
	/**
	 * 根据TASKCODE获得taskId
	 * @param taskCode
	 * @return
	 * @throws Exception
	 */
	public String getTaskIdByTaskCode(String taskCode,int isleaf) throws Exception {
		new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
		DbUtil dbUtil = new DbUtil(conn);
		String sql = "select taskId from z_task "
				+ " where TaskCode = ? "
				+ "	and projectid= ? "
				+ " and isleaf=? "
				+ " order by isleaf desc ";

		Object[] object = new Object[] {
				taskCode,
				this.projectId,
				isleaf
		};
		return dbUtil.queryForString(sql, object);
	}

	public String getTaskIdByTaskCode(String taskCode,String taskName) throws Exception {
		new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
		DbUtil dbUtil = new DbUtil(conn);
		String sql = "select taskId from z_task "
				+ " where TaskCode = ? "
				+ "	and projectid= ? and taskname= ?"
				+ " order by isleaf desc ";

		Object[] object = new Object[] {
				taskCode,
				this.projectId,
				taskName
		};
		String result=dbUtil.queryForString(sql, object);
		org.util.Debug.prtOut("qwh:result="+result+"|"+taskName);
		if (result==null || "".equals(result)){
			return getTaskIdByTaskCode(taskCode);
		}else
			return result;
	}

	/**
	 * 根据TASKCODE获得taskId,只获得叶子节点
	 * @param taskCode
	 * @return
	 * @throws Exception
	 */
	public String getTaskIdByTaskCodeIsLeaf(String taskCode) throws Exception {
		new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
		DbUtil dbUtil = new DbUtil(conn);
		String sql = "select taskid "
				   + " from z_Task "
				   + " where taskcode=? "
				   + " and projectid=? "
				   + " and isleaf=1 "
				   + " union "
				   + " select taskid "
				   + " from z_sheettask "
				   + " where sheettaskcode=? "
				   + " and projectid=? ";

		Object[] object = new Object[] {
				taskCode,this.projectId,
				taskCode,this.projectId
		};
		return dbUtil.queryForString(sql, object);
	}

	/**
	 * 根据用户ID返回底稿列表
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public List getTaskList(String userId) throws Exception {
		List taskList = new ArrayList();
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			String sql = "select * from ( "
					+ "	select taskCode,taskid,manuid from z_task "
					+ "		where projectid = ? "
					+ " 	and isleaf=1 "
					+ "		and taskname not like '%.doc' "
					+ "		and (user0='' or user0 is null or user0 = ?) "
					+ "		and property<>'tache' and property <> 'target' "
					+ "	order by orderid)t1 "

					+ "union "

					+ "select * from ( select  taskCode,taskid,manuid "
					+ "		from z_task "
					+ "		where projectid = ? "
					+ "		and isleaf=1 "
					+ "		and taskname like '%.doc' "
					+ "		and (user0='' or user0 is null or user0 = ?) "
					+ "		and property<>'tache' and property <> 'target' "
					+ "	order by orderid)t2 ";

			// 根据项目ID切换数据库。
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);
			ps.setString(2, userId);
			ps.setString(3, this.projectId);
			ps.setString(4, userId);

			rs = ps.executeQuery();

			while (rs.next()) {
				Map taskMap = new HashMap();
				taskMap.put("taskCode", rs.getString(1));
				taskMap.put("taskId", rs.getString(2));
				taskList.add(taskMap);
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return taskList;
	}
	
	/**
	 * 根据父节点taskid返回底稿列表
	 * @param parentTaskId
	 * @return
	 * @throws Exception
	 */
	public List getTaskListByParentTaskId(String parentTaskId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		//updateOrderId();

		List taskList = new ArrayList();
		Task task = null;

		try {
			String sql = "select distinct a.* from z_task a,z_task b "
						+ " where a.projectid= ? "
						+ " and b.projectid=? "
						+ " and b.taskid=? "
						+ " and a.FullPath like concat(b.fullpath,'%') "
						+ " order by a.orderid";

			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);
			ps.setString(2, this.projectId);
			ps.setString(3, parentTaskId);

			rs = ps.executeQuery();

			while (rs.next()) {
				task = new Task();
				task.setTaskId(rs.getString("Taskid"));
				task.setTaskCode(rs.getString("TaskCode"));
				task.setTaskName(rs.getString("Taskname"));
				task.setTaskContent(rs.getString("TaskContent"));
				task.setDescription(rs.getString("Description"));
				task.setParentTaskId(rs.getString("ParentTaskID"));
				task.setProjectId(rs.getString("ProjectID"));
				task.setIsLeaf(rs.getInt("IsLeaf"));
				task.setLevel(rs.getInt("Level0"));
				task.setManuId(rs.getString("manuid"));
				task.setManuTemplateId(rs.getString("manutemplateid"));

				task.setUser0(rs.getString("user0"));
				task.setUser1(rs.getString("user1"));
				task.setUser2(rs.getString("user2"));
				task.setUser3(rs.getString("user3"));
				task.setUser4(rs.getString("user4"));
				task.setUser5(rs.getString("user5"));

				task.setProperty(rs.getString("property"));
				task.setFullPath(rs.getString("fullpath"));
				task.setSheetList(getSheetTaskList(task.getTaskId()));
				taskList.add(task);
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return taskList;
	}
	

	/**
	 * 根据父节点taskid返回叶子底稿列表
	 * @param parentTaskId
	 * @return
	 * @throws Exception
	 */
	public List getLeafTaskListByParentTaskId(String parentTaskId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		List taskList = new ArrayList();
		Task task = null;

		try {
			String sql = "select * from z_task where parenttaskid=? and projectid=? and isleaf=1";

			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			ps = conn.prepareStatement(sql);
			ps.setString(1, parentTaskId);
			ps.setString(2, this.projectId);
  
			rs = ps.executeQuery();

			while (rs.next()) {
				task = new Task();
				task.setTaskId(rs.getString("Taskid"));
				task.setTaskCode(rs.getString("TaskCode"));
				task.setTaskName(rs.getString("Taskname"));
				task.setTaskContent(rs.getString("TaskContent"));
				task.setDescription(rs.getString("Description"));
				task.setParentTaskId(rs.getString("ParentTaskID"));
				task.setProjectId(rs.getString("ProjectID"));
				task.setIsLeaf(rs.getInt("IsLeaf"));
				task.setLevel(rs.getInt("Level0"));
				task.setManuId(rs.getString("manuid"));
				task.setManuTemplateId(rs.getString("manutemplateid"));

				task.setUser0(rs.getString("user0"));
				task.setUser1(rs.getString("user1"));
				task.setUser2(rs.getString("user2"));
				task.setUser3(rs.getString("user3"));
				task.setUser4(rs.getString("user4"));
				task.setUser5(rs.getString("user5"));

				task.setProperty(rs.getString("property"));
				task.setFullPath(rs.getString("fullpath"));
				task.setSheetList(getSheetTaskList(task.getTaskId()));
				taskList.add(task);
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return taskList;
	}

	/**
	 * 根据底稿编号返回底稿列表
	 * @param parentTaskId
	 * @return
	 * @throws Exception
	 */
	public List getTaskListByTaskCodes(String taskCodes) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		//updateOrderId();

		List taskList = new ArrayList();
		Task task = null;

		String [] taskcode = null;
		String taskCodesStr = "'-1'";

		if(taskCodes != null) {
			taskcode = taskCodes.split(",");

			for(int i=0; i < taskcode.length; i++) {
				if(!"".equals(taskcode[i])) {
					taskCodesStr += "," + "'" + taskcode[i] + "'";
				}
			}
		}

		try {
			String sql = "select distinct * from z_task "
						+ " where projectid= ? "
						+ " and taskcode in(" + taskCodesStr + ") and isleaf=1 "
						+ " order by orderid";

			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);

			rs = ps.executeQuery();

			while (rs.next()) {
				task = new Task();
				task.setTaskId(rs.getString("Taskid"));
				task.setTaskCode(rs.getString("TaskCode"));
				task.setTaskName(rs.getString("Taskname"));
				task.setTaskContent(rs.getString("TaskContent"));
				task.setDescription(rs.getString("Description"));
				task.setParentTaskId(rs.getString("ParentTaskID"));
				task.setProjectId(rs.getString("ProjectID"));
				task.setIsLeaf(rs.getInt("IsLeaf"));
				task.setLevel(rs.getInt("Level0"));
				task.setManuId(rs.getString("manuid"));
				task.setManuTemplateId(rs.getString("manutemplateid"));

				task.setUser0(rs.getString("user0"));
				task.setUser1(rs.getString("user1"));
				task.setUser2(rs.getString("user2"));
				task.setUser3(rs.getString("user3"));
				task.setUser4(rs.getString("user4"));
				task.setUser5(rs.getString("user5"));

				task.setProperty(rs.getString("property"));
				task.setFullPath(rs.getString("fullpath"));
				task.setSheetList(getSheetTaskList(task.getTaskId()));
				taskList.add(task);
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return taskList;
	}

	/**
	 * 获得任务列表
	 * @param parentTaskId	上级编号
	 * @return
	 * @throws Exception
	 */
	public List getTaskListByWork(String parentTaskId) throws Exception {
		List taskList = new ArrayList();
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = " select b.taskid,b.taskcode,b.taskname "
					+ " from z_task b "
					+ " where b.projectid = ? "
					+ " and b.parentTaskId = ? "
					+ " and b.isleaf=0 "
					+ " order by b.orderid " ;

		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);
			ps.setString(2, parentTaskId);

			Task task;
			rs = ps.executeQuery();
			while(rs.next()) {
				task = new Task();
				String taskId = rs.getString(1);
				task.setTaskId(taskId);
				task.setTaskCode(rs.getString(2));
				task.setTaskName(rs.getString(3));
				task.setExecutor(getTaskExecutor(taskId));

				taskList.add(task);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return taskList;
	}

	/**
	 * 获得环节下的所有审计程序的执行人,用","分隔
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	private String getTaskExecutor(String taskId) throws Exception {

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
	 * 获得底稿的表页
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public List getSheetTaskListByTaskCodes(String taskCodes) throws Exception {
		List sheetTaskList = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String [] taskcode = null;
		String taskCodesStr = "'-1'";

		if(taskCodes != null) {
			taskcode = taskCodes.split(",");

			for(int i=0; i < taskcode.length; i++) {
				if(!"".equals(taskcode[i])) {
					taskCodesStr += "," + "'" + taskcode[i] + "'";
				}
			}
		}

		try {
			sheetTaskList = new ArrayList();
			SheetTask sheetTask = null;

			String sql = "select distinct projectid,taskid,sheettaskcode,taskcode,sheetname,property "
						+ " from z_sheettask "
						+ " where sheettaskcode in (" + taskCodesStr +") "
						+ " and projectid=? "
						+ " and taskId is not null "
						+ " order by sheettaskcode ";

			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);

			rs = ps.executeQuery();

			while(rs.next()) {
				sheetTask = new SheetTask();

				sheetTask.setProjectId(rs.getString(1));
				sheetTask.setTaskId(rs.getString(2));
				sheetTask.setSheetTaskCode(rs.getString(3));
				sheetTask.setTaskCode(rs.getString(4));
				sheetTask.setSheetName(rs.getString(5));
				sheetTask.setProperty(rs.getString(6));

				sheetTaskList.add(sheetTask);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return sheetTaskList;
	}

	/**
	 * 获得底稿的表页
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public List getSheetTaskList(String taskId) throws Exception {
		List sheetTaskList = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			sheetTaskList = new ArrayList();
			SheetTask sheetTask = null;

			String sql = "select distinct projectid,taskid,sheettaskcode,taskcode,sheetname,property "
						+ " from z_sheettask "
						+ " where taskid=? "
						+ " and projectId=? "
						+ " order by sheettaskcode ";

			ps = conn.prepareStatement(sql);
			ps.setString(1, taskId);
			ps.setString(2, this.projectId);

			rs = ps.executeQuery();

			while(rs.next()) {
				sheetTask = new SheetTask();

				sheetTask.setProjectId(rs.getString(1));
				sheetTask.setTaskId(rs.getString(2));
				sheetTask.setSheetTaskCode(rs.getString(3));
				sheetTask.setTaskCode(rs.getString(4));
				sheetTask.setSheetName(rs.getString(5));
				sheetTask.setProperty(rs.getString(6));

				sheetTaskList.add(sheetTask);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return sheetTaskList;
	}

	/**
	 * 根据底稿taskID获得底稿名称
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public String getTaskNameByTaskId(String taskId) throws Exception {
		String taskName = "";
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = "select taskName from z_task "
							+ " where projectid = ? "
							+ " and taskid = ?";
			new DBConnect().changeDataBaseByProjectid(conn, projectId);

			ps = conn.prepareStatement(sql);

			ps.setString(1, projectId);
			ps.setString(2, taskId);

			rs = ps.executeQuery();

			if (rs.next()) {
				taskName = rs.getString(1);
			}
			return taskName;

		} catch (Exception e) {

			Debug.print(Debug.iError, "访问失败", e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 根据项目ID和底稿ID找出该分类下的分工人
	 * @param parenttaskId
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public String getTaskUser(String taskId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		String strSql = " select GROUP_CONCAT(taskuser.name)"
				+ "	FROM("
				+ "	select DISTINCT concat(conv(b.id,10,10),'-',b.name) as name from z_task a,k_user b"
				+ "		where parenttaskid = ? "
				+ "		and projectid = ?"
				+ "		and a.user0 = b.id "
				+ "		order by b.id) taskuser";

		try {

			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
			ps = conn.prepareStatement(strSql);

			ps.setString(1, taskId);
			ps.setString(2, this.projectId);

			rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getString(1);
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return "";
	}

	/**
	 * 根据底稿TaskId获得编制人名称
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public String getTaskUser1(String taskId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = "select k.username from z_task z,k_user k"
					+ " where taskId = ? "
					+ " and projectid = ?"
					+ " and z.user1 = k.name";

			new DBConnect().changeDataBaseByProjectid(conn, projectId);

			ps = conn.prepareStatement(sql);
			ps.setString(1, taskId);
			ps.setString(2, projectId);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "获取编制人信息出错", e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return "";
	}


	/**
	 * 返回底稿显示的表页名称
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public String getShowSheetTaskNames(String taskId) throws Exception {
		String sheetTaskNames = "";

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = " select sheettaskcode,sheetname \n "
						+ " from z_sheettask \n "
						+ " where projectId=? \n "
						+ " and taskId=? \n "
						+ " and property like '%1%' \n "
						+ " union \n "
						+ " select c.sheetTaskcode,c.sheetname \n "
						+ " from z_task a,z_procedure b,z_sheettask c \n "
						+ " where a.taskid=? \n "
						+ " and c.taskId=? \n "
						+ " and a.projectid=? \n "
						+ " and b.projectid=? \n "
						+ " and c.projectid=? \n "
						+ " and a.parenttaskId=b.taskId \n "
						+ " and concat(',',b.manuscript,',') like concat('%,',c.sheettaskcode,',%') \n ";

			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);
			ps.setString(2, taskId);
			ps.setString(3, taskId);
			ps.setString(4, taskId);
			ps.setString(5, this.projectId);
			ps.setString(6, this.projectId);
			ps.setString(7, this.projectId);

			rs = ps.executeQuery();

			while(rs.next()) {
				//兼容 表页叫做 审定表 和 SA审定表的情况
				sheetTaskNames += "`" + rs.getString(2) +"`" + rs.getString(1)+ rs.getString(2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return sheetTaskNames;
	}

	/**
	 * 返回底稿隐藏的表页
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public String getHiddenSheetTaskNames(String taskId) throws Exception {
		String sheetTaskNames = "";

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = " select distinct sheettaskcode,sheetname "
						+ " from z_sheettask "
						+ " where projectId=? "
						+ " and taskId=? "
						+ " and (property not like '%1%' or property is null )";

			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);
			ps.setString(2, taskId);

			rs = ps.executeQuery();

			while(rs.next()) {
				sheetTaskNames += "`" + rs.getString(2)+"`" + rs.getString(1)+ rs.getString(2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return sheetTaskNames;
	}

	/**
	 * 根据底稿编号和表页编号返回表页名称
	 * @param taskId
	 * @param sheetTaskCode
	 * @return
	 * @throws Exception
	 */
	public String getSheetNameBySheetTaskCode(String taskId, String sheetTaskCode) throws Exception {
		String sheetTaskName = "";

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = " select distinct sheettaskcode,sheetname "
						+ " from z_sheettask "
						+ " where projectId=? "
						+ " and taskId=? "
						+ " and sheetTaskCode = ? ";

			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);
			ps.setString(2, taskId);
			ps.setString(3, sheetTaskCode);

			rs = ps.executeQuery();

			if(rs.next()) {
				sheetTaskName = rs.getString(2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return sheetTaskName;
	}

	/**
	 * 根据底稿ID返回底稿名和项目名
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public String[] getTNameAndPname(String taskId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String[] tNameAndPname = { "", "", "", "" };

		try {
			String sql = "select z.taskName,p.projectName,z.taskcode,z.manuId"
				+ " from z_task z,z_project p"
				+ " where z.taskid = ?"
				+ " and z.projectid = ?"
				+ " and z.projectid = p.projectid";

			new DBConnect().changeDataBaseByProjectid(conn, projectId);

			ps = conn.prepareStatement(sql);
			ps.setString(1, taskId);
			ps.setString(2, projectId);
			rs = ps.executeQuery();
			if (rs.next()) {
				tNameAndPname[0] = rs.getString(1);
				tNameAndPname[1] = rs.getString(2);
				tNameAndPname[2] = rs.getString(3);
				tNameAndPname[3] = rs.getString(4);
			}
			return tNameAndPname;
		} catch (Exception e) {
			Debug.print(Debug.iError, "获取底稿名称和项目名称失败", e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 移动底稿
	 *
	 * @param taskId  需要移动的底稿的ID
	 * @param oldParentId 原上级目录ID
	 * @param newParentId 新上级目录ID
	 * @param curProjectId  当前项目ID
	 * @throws Exception
	 * @author void 2006-9-30
	 */
	public void moveTask(String taskId, String oldParentId, String newParentId ) throws Exception {

		Statement stmt = null;

		String newFullPath = ""; // 新全路径
		String oldFullPath = ""; // 原全路径

		String updateParentId = null;
		String updateFullPath = null;

		if (!newParentId.equals("0")) {
			//新上级目录不为根目录

			//获得新上级目录全路径
			newFullPath = getFullPath(newParentId);

			//当新上级目录不是根目录时,更新 ParentTaskID 的SQL语句
			updateParentId = "update z_task set ParentTaskID= '" + newParentId
							+ "' where TaskID = '" + taskId + "' "
							+ " and ProjectID = '" + projectId + "'";

		} else {
			//新上级目录为根目录，ParentTaskID直接为0

			//当新上级目录为根目录时,更新 ParentTaskID 的SQL语句
			updateParentId = " update z_task set "
							+ " ParentTaskID= '0',Level0=0 "
							+ " where TaskID = '" + taskId + "' "
							+ " and ProjectID = '" + projectId + "'";
		}

		oldFullPath = getFullPath(taskId);

		if (!oldParentId.equals("0")) {
			//原上级目录不为根目录
			//当原路径不为根目录时,更新fullpath的sql语句
			updateFullPath = "update z_task set "
							+ " FullPath = REPLACE(fullpath,'" + oldFullPath + "','" + newFullPath + taskId + "|')"
							+ " where FullPath like '" + oldFullPath + "%'"
							+ " and ProjectID = '" + projectId + "'";

		} else {
			//原上级目录为根目录

			//当原路径为根目录时,更新fullpath的sql语句
			updateFullPath = "update z_task set "
							+ " FullPath = concat('" + newFullPath + "',FullPath) "
							+ " where FullPath like '" + oldFullPath + "%' "
							+ " and ProjectID = '" + projectId + "'";
		}

		String updateLevel = "update z_task a,z_task b set "
							+ " a.Level0 = b.Level0+1 "
							+ " where a.parenttaskid = b.TaskID "
							+ " and a.ProjectID = '" + projectId + "'";

		try {
			new DBConnect().changeDataBaseByProjectid(conn, projectId);

			stmt = conn.createStatement();

			stmt.execute(updateParentId);
			stmt.execute(updateFullPath);
			stmt.execute(updateLevel);

		} catch (Exception e) {
			Debug.print(Debug.iError, "移动底稿失败", e);
			throw e;
		} finally {
			DbUtil.close(stmt);
		}
	}

	/**
	 * 从模板处重置单张底稿
	 *
	 * @param taskId
	 * @param projectId
	 * @throws Exception
	 */
	public void recoverTask(String taskId) throws Exception {

		Task task = new TaskService(conn, this.projectId).getTaskByTaskId(taskId);

		String taskCode = task.getTaskCode();

		recoverTaskByTaskCode(taskCode);

	}

	/**
	 * 从模板处重置单张底稿
	 *
	 * @param taskCode
	 * @param projectId
	 * @throws Exception
	 */
	public void recoverTaskByTaskCode(String taskCode) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;

		try {

			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
			Task task = new TaskService(conn, this.projectId).getTaskByTaskCodeIsleaf(taskCode);
			String typeId = new ProjectService(conn).getProjectById(this.projectId).getAuditType();

			String sql = " select taskid "
				 		+ " from k_tasktemplate "
						+ " where taskcode=? "
						+ " and typeid=? and isleaf=1 ";
			String templateTaskId = new DbUtil(conn).queryForString(sql, new Object[]{taskCode, typeId});

			if(templateTaskId != null) {

				ManuFileService ManuScriptService = new ManuFileService(conn);
				byte[] fileByte = ManuScriptService.getFileByTypeIdAndTaskId(typeId, templateTaskId);
				ManuScriptService.saveFileByTaskId(this.projectId, task.getTaskId(), fileByte);

				//重置任务
				sql = " update z_task set "
					+ " user1 = null,user2 = null,user3 = null,user4 = null,user5 = null, "
					+ " date1 = null,date2 = null,date3 = null,date4 = null,date5 = null "
					+ " where taskcode = ? "
					+ " and ProjectId = ? and isleaf=1 ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, taskCode);
				ps.setString(2, this.projectId);
				ps.executeUpdate();

				if(ps.executeUpdate() > 0) {
					sql = "delete from z_taskrefer where projectid=? and taskcode=? ";
					ps = conn.prepareStatement(sql);
					ps.setString(1,this.projectId);
					ps.setString(2,taskCode);
					ps.executeUpdate();

					sql = " delete from z_manudata "
						+ " where projectid=? and taskCode=? ";
					ps = conn.prepareStatement(sql);
					ps.setString(1,this.projectId);
					ps.setString(2,taskCode);
					ps.executeUpdate();

					sql = "	delete from z_taskreferdata "
						+ " where projectid=? and taskCode=? ";
					ps = conn.prepareStatement(sql);
					ps.setString(1,this.projectId);
					ps.setString(2,taskCode);
					ps.executeUpdate();

					sql = "	insert into z_taskrefer "
						+ "select ?,taskid,TaskCode,SheetName,CellAddress,refertaskcode,refersheetname,refercelladdress1,refercelladdress2,property "
						+ " from k_taskrefertemplate "
						+ " where typeid=? and taskCode=? ";
					ps = conn.prepareStatement(sql);
					ps.setString(1,this.projectId);
					ps.setString(2,typeId);
					ps.setString(3,taskCode);
					ps.executeUpdate();
				}
			} else {
				throw new Exception("模板中找不到该底稿");
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 根据底稿编号删除一张底稿
	 * @param taskId
	 * @throws Exception
	 */
	public void removeTask(String taskId) throws Exception {
		String sql;
		ResultSet rs = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;

		try {
			if (taskId != null) {
				new DBConnect().changeDataBaseByProjectid(this.projectId, conn);

				//删除函证
				sql = "delete b from z_task a inner join z_letters b "
						+ " on a.projectid = ? "
						+ " and b.projectid = ? "
						+ " and a.taskid= ? "
						+ " and a.taskId = b.ManuID ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, this.projectId);
				ps.setString(2, this.projectId);
				ps.setString(3, taskId);
				ps.execute();

				String customerId = new CustomerService(conn).getCustomerIdByProjectId(this.projectId);

				//移动到回收站
				new TaskRecycleService(conn,projectId).moveToRecycle(taskId,"0");


				//从表页表中删除
				sql = "delete from z_sheettask "
					+ " where taskid= ? "
					+ " and projectid = ?";
				ps = conn.prepareStatement(sql);
				ps.setString(1, taskId);
				ps.setString(2, this.projectId);
				ps.execute();

				//从任务表中删除
				sql = "delete from z_task "
					+ " where taskid= ? "
					+ " and projectid = ?";
				ps = conn.prepareStatement(sql);
				ps.setString(1, taskId);
				ps.setString(2, this.projectId);
				ps.execute();

				//删除底稿文件
				ManuFileService.deleteFileByTaskId(taskId, this.projectId, customerId);
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "删除底稿失败", e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(ps2);
		}

	}

	/**
	 * 根据项目ID修复底稿全路径(level和fullpath错误时可以使用)
	 * 如果level正确,可以自己进mysql恢复:
	 *
	 * 1,恢复level=0的全路径:update `z_task`  set fullpath = concat(taskid,'|') where `Level0` = 0
	 * 2,恢复level=n(n必须按从小到大的顺序进行恢复)全路径:
	 *
	 * 		update `z_task` a,(select fullpath,taskid,projectid,`Level0` from `z_task` ) b
	 * 		set a.fullpath = concat(b.fullpath,a.taskid,'|')
	 * 		where  b.taskid = a.parenttaskid
	 * 		and a.projectid = b.projectid
	 * 		and a.Level0 = n
	 *
	 * @param projectId
	 * @throws Exception
	 */
	public void repairTaskFullPath(Writer out) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {

			String sql = "select taskid from `z_task` "
						+ " where projectId = ? "
						+ " and (property not like 'A%' or property is null) "
						+ " order by orderid";

			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);

			rs = ps.executeQuery();

			while (rs.next()) {
				String taskId = rs.getString(1);
				String fullPath = calFullPath(taskId);
				int level = fullPath.split("\\|").length - 1;
				sql = "update `z_task` set "
						+ " fullpath= '" + fullPath + "', "
						+ " Level0= " + level + " "
						+ " where taskId = '" + taskId + "'"
						+ " and projectid = '" + projectId + "'";

				stmt = conn.createStatement();
				out.write(sql + "<br />");
				out.write("<script>");
				out.write("   window.scroll(0,document.body.scrollHeight);");
				out.write("</script>");
				out.flush();
				stmt.execute(sql);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(stmt);
		}

	}

	/**
	 * 根据项目ID修复底稿全路径(level和fullpath错误时可以使用)
	 * 如果level正确,可以自己进mysql恢复:
	 *
	 * 1,恢复level=0的全路径:update `z_task`  set fullpath = concat(taskid,'|') where `Level0` = 0
	 * 2,恢复level=n(n必须按从小到大的顺序进行恢复)全路径:
	 *
	 * 		update `z_task` a,(select fullpath,taskid,projectid,`Level0` from `z_task` ) b
	 * 		set a.fullpath = concat(b.fullpath,a.taskid,'|')
	 * 		where  b.taskid = a.parenttaskid
	 * 		and a.projectid = b.projectid
	 * 		and a.Level0 = n
	 *
	 * @throws Exception
	 */
	public void repairTaskFullPath() throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {

			String sql = "select taskid from `z_task` "
						+ " where projectId = ? "
						+ " and (property not like 'A%' or property is null) "
						+ " order by orderid";

			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);

			rs = ps.executeQuery();
			
			
			while (rs.next()) {
				String taskId = rs.getString(1);
				String fullPath = calFullPath(taskId);
				int level = fullPath.split("\\|").length - 1;
				sql = "update `z_task` set "
						+ " fullpath= '" + fullPath + "', "
						+ " Level0= " + level + " "
						+ " where taskId = '" + taskId + "'"
						+ " and projectid = '" + projectId + "'";
				ps = conn.prepareStatement(sql) ;
				System.out.println(sql);
				ps.execute();
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(stmt);
		}

	}

	public void repairTaskCode() throws Exception {
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		Statement stmt = null;

		try {

			String sql = " select taskId,taskcode from z_task "
					   + " where projectid=? "
					   + " and isleaf='0' "
					   + " order by orderid ";

			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);

			rs = ps.executeQuery();

			String taskCode = "";
			stmt = conn.createStatement();

			while (rs.next()) {
				taskCode = rs.getString(2);

				sql = " select taskId from z_task where projectid=? and parenttaskid=? order by Level0 ";
				ps2 = conn.prepareStatement(sql);
				ps2.setString(1, this.projectId);
				ps2.setString(2, rs.getString(1));

				rs2 = ps2.executeQuery();

				int i=1;

				while(rs2.next()) {
					String sql2 = " update z_task set taskcode='" + (taskCode + "-" + i) + "' "
								+ " where projectId='" + this.projectId + "' and taskid='" + rs2.getString(1) + "' ";
					System.out.println(sql2);
					i++;
					stmt.execute(sql2);
				}
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(rs2);
			DbUtil.close(ps);
			DbUtil.close(ps2);
			DbUtil.close(stmt);
		}

	}

	/**
	 * 重算任务ORDERID
	 * @param projectid
	 * @throws Exception
	 */
	public void updateOrderId() throws Exception {
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		String sql = "select taskid,taskcode from z_task where projectid = ? ";

		try {
			new DBConnect().changeDataBaseByProjectid(conn, projectId);

			ps = conn.prepareStatement(sql);
			ps.setString(1, projectId);
			rs = ps.executeQuery();

			while (rs.next()) {
				String taskId = rs.getString(1);
				String taskcode = rs.getString(2);

				sql = "update z_task set orderid = ? "
						+ " where taskid = ? "
						+ " and projectid = ? ";

				ps2 = conn.prepareStatement(sql);
				ps2.setString(1, UTILString.getOrderId(taskcode));
				ps2.setString(2, taskId);
				ps2.setString(3, projectId);

				ps2.execute();
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(ps2);
		}
	}

	/**
	 * 更新底稿任务
	 * @param task
	 * @throws Exception
	 */
	public void updateTask(Task task) throws Exception {
		PreparedStatement ps = null;
		String sql;
		try {

			new DBConnect().changeDataBaseByProjectid(conn, projectId);
			boolean isNum = false;

			try {
				Integer.parseInt(task.getProperty());
				isNum = true;
			} catch (NumberFormatException e) {
				//e.printStackTrace();
			}

			if (!"".equals(task.getProperty()) && isNum) {

				sql = "update z_task set property = null "
					+ " where projectid = ?"
					+ " and property = ? ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, this.projectId);
				ps.setString(2, task.getProperty());
				ps.execute();

			}


			sql = "update z_task set property = ? "
				+ " where ProjectId = ? "
				+ " and TaskId = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, task.getProperty() );
			ps.setString(2, this.projectId );
			ps.setString(3, task.getTaskId() );
			ps.execute();


			sql = "update z_Task set "
				+ " TaskName=?, TaskContent=?, Description=?, TaskCode=?, Orderid=?,subjectName=?,auditproperty=? "
				+ " where TaskID = ? "
				+ " and projectid=? ";

			ps = conn.prepareStatement(sql);

			ps.setString(1, task.getTaskName());
			ps.setString(2, task.getTaskContent());
			ps.setString(3, task.getDescription());
			ps.setString(4, task.getTaskCode());
			ps.setString(5, UTILString.getOrderId(task.getTaskCode()));
			ps.setString(6, task.getSubjectName());
			ps.setString(7, task.getAuditproperty());
			ps.setString(8, task.getTaskId());
			ps.setString(9, this.projectId);

			ps.execute();

			//更新扩展的缓存数据表
			sql="update z_taskrefer set taskcode=? where projectid=? and  taskid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, task.getTaskCode());
			ps.setString(2, this.projectId);
			ps.setString(3, task.getTaskId());
			ps.execute();

			sql="update z_taskreferdata set taskcode=? where projectid=? and  taskid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, task.getTaskCode());
			ps.setString(2, this.projectId);
			ps.setString(3, task.getTaskId());
			ps.execute();

			sql="update z_manudata set taskcode=? where projectid=? and  taskid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, task.getTaskCode());
			ps.setString(2, this.projectId);
			ps.setString(3, task.getTaskId());
			ps.execute();

		} catch (Exception e) {
			Debug.print(Debug.iError, "更新底稿任务失败", e);
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 根据parentTaskId返回该节点下的子结点和底稿数
	 * @param parentTaskId
	 * @return
	 * @throws Exception
	 */
	public int getChildrenCountByParentTaskId(String parentTaskId) throws Exception {
		new DBConnect().changeDataBaseByProjectid(conn, projectId);
		DbUtil dbUtil = new DbUtil(conn);

		String sql = "select count(*) from z_task "
					+ " where parenttaskid=? "
					+ " and projectid=? ";

		Object[] params = new Object[] {
				parentTaskId,
				this.projectId
		};

		return dbUtil.queryForInt(sql, params);
	}

	/**
	 * 判断该底稿是否已编制或者已经复核
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public boolean isCompleteByTaskId(String taskId) throws Exception {
		new DBConnect().changeDataBaseByProjectid(conn, projectId);
		DbUtil dbUtil = new DbUtil(conn);

		String sql = "select 1 from z_task "
					+ " where  Projectid=? "
					+ " and taskid =? "
					+ " and (user1 is not null or user2 is not null or user3 is not null)";

		Object[] params = new Object[] {
				this.projectId,
				taskId
		};

		String temp = dbUtil.queryForString(sql, params);

		return temp != null;

	}

	/**
	 * 判断底稿编号是否存在，返回不存在的编号
	 * @param taskCodes
	 * @return
	 * @throws Exception
	 */
	public String validateTaskCode(String taskCodes) throws Exception {
		String taskCode[] = taskCodes.split("~");

		String noTaskCodes = "";

		new DBConnect().changeDataBaseByProjectid(conn, projectId);

		for(int i=0; i < taskCode.length; i++) {
			if(getTaskIdByTaskCodeIsLeaf(taskCode[i]) == null) {
				noTaskCodes += taskCode[i] + "~";
			}
		}

		if(!"".equals(noTaskCodes)) {
			noTaskCodes = noTaskCodes.substring(0,noTaskCodes.lastIndexOf("~"));
		}

		return noTaskCodes;
	}

	/**
	 * 设置需要显示的底稿
	 * @param taskcodes
	 * @return
	 * @throws Exception
	 */
	public boolean setShowTask(String taskcodes) throws Exception {

		String[] taskCode = taskcodes.split(",");

		String tempTaskCodes = "'-1'";

		//处理taskcode,使其可以放在in里面
		for(int i=0; i < taskCode.length; i++) {
			if(!"".equals(taskCode[i])) {
				tempTaskCodes += "," + "'" + taskCode[i] + "'";
			}
		}

		//切换数据库
		new DBConnect().changeDataBaseByProjectid(this.projectId, this.conn);
		DbUtil dbUtil = new DbUtil(conn);
		Object[] args = new Object[] {String.valueOf(TaskCommonService.TASK_STATE_CODE_MUST),
									  String.valueOf(TaskCommonService.TASK_STATE_CODE_DATA),
									  this.projectId };

		//首先无条件把状态去掉
		String sql = " update z_task set ismust=replace(replace(ismust,?,''),?,'') "
					+ " where taskcode in (" + tempTaskCodes + ") "
					+ " and projectId=? "
					+ " and isleaf=1 ";
		dbUtil.executeUpdate(sql, args);

		sql = " update z_task set ismust=concat(concat(ifnull(ismust,''),?),?) "
	   		+ " where taskcode in(" + tempTaskCodes + ") "
	   		+ " and projectId=? ";

		dbUtil.executeUpdate(sql, args);

		args = new Object[] {String.valueOf(TaskCommonService.TASK_STATE_CODE_MUST),
				  				this.projectId };

		//处理sheettask表
		sql = " update z_sheettask set property=replace(property,?,'') "
					+ " where sheettaskcode in (" + tempTaskCodes + ") "
					+ " and projectId=? ";
		dbUtil.executeUpdate(sql, args);

		sql = " update z_sheettask set property=concat(ifnull(property,''),?) "
	   		+ " where sheettaskcode in(" + tempTaskCodes + ") "
	   		+ " and projectId=? ";

		dbUtil.executeUpdate(sql, args);

		return true;
	}
	
	/**
	 * 设置需要显示的底稿
	 * @param taskcodes
	 * @return
	 * @throws Exception
	 */
	public boolean setTaskShowAndHidden(String taskcodes) throws Exception {

		String[] taskCode = taskcodes.split(",");

		String tempTaskCodes = "'-1'";

		//处理taskcode,使其可以放在in里面
		for(int i=0; i < taskCode.length; i++) {
			if(!"".equals(taskCode[i])) {
				tempTaskCodes += "," + "'" + taskCode[i] + "'";
			}
		}

		//切换数据库
		new DBConnect().changeDataBaseByProjectid(this.projectId, this.conn);
		DbUtil dbUtil = new DbUtil(conn);
		Object[] args = new Object[] {String.valueOf(TaskCommonService.TASK_STATE_CODE_MUST),
									  String.valueOf(TaskCommonService.TASK_STATE_CODE_DATA),
									  this.projectId };

		//首先无条件把状态去掉
		String sql = " update z_task set ismust=replace(replace(ismust,?,''),?,'') "
					+ " where taskcode in (" + tempTaskCodes + ") "
					+ " and projectId=? "
					+ " and isleaf=1 ";
		dbUtil.executeUpdate(sql, args);

		sql = " update z_task set ismust=concat(concat(ifnull(ismust,''),?),?) "
	   		+ " where taskcode in(" + tempTaskCodes + ") "
	   		+ " and projectId=? ";

		dbUtil.executeUpdate(sql, args);

		args = new Object[] {String.valueOf(TaskCommonService.TASK_STATE_CODE_MUST),
				  				this.projectId };

		//处理sheettask表
		//把涉及到的所有表页都设置成隐藏
		sql = " update z_sheettask a,( select distinct taskid "
			+ " 	from z_sheettask " 
			+ " 	where projectid=? "
			+ " 	and sheettaskcode in(" + tempTaskCodes + ")  "
			+ " ) b "
			+ " set a.property=0 " 
			+ "	where a.projectid=? "
			+ "	and a.taskid=b.taskid ";

		args = new Object[]{
					this.projectId, 
					this.projectId
				};
		dbUtil.executeUpdate(sql, args);

		
		//设置成必做
		sql = " update z_sheettask a,z_procedure b,( " 
			+ "  select distinct taskid  " 
			+ " 	from z_sheettask  " 
			+ " 	where projectid=?  "
			+ " 	and sheettaskcode in(" + tempTaskCodes + ")  "
			+ " ) c "
			+ " set a.property=1   "
			+ " where a.projectid=? "
			+ " and b.projectid=?   "
			+ " and a.taskid=c.taskid "
			+ " and b.manuscript like concat('%',a.sheettaskcode,'%')  ";

		args = new Object[]{
				this.projectId, 
				this.projectId,
				this.projectId
			};
		dbUtil.executeUpdate(sql, args);

		return true;
	}
	
	/**
	 * 隐藏底稿或表页,该方法考虑到有些底稿还跟其他程序关联的情况
	 * @param taskcodes
	 * @return
	 * @throws Exception
	 */
	public boolean setTaskHidden(String taskcodes) throws Exception {

		String[] taskCode = taskcodes.split(",");

		String tempTaskCodes = "'-1'";

		//处理taskcode,使其可以放在in里面
		for(int i=0; i < taskCode.length; i++) {
			if(!"".equals(taskCode[i])) {
				tempTaskCodes += "," + "'" + taskCode[i] + "'";
			}
		}

		//切换数据库
		new DBConnect().changeDataBaseByProjectid(this.projectId, this.conn);
		DbUtil dbUtil = new DbUtil(conn);
		Object[] args = new Object[] {this.projectId,
									  this.projectId };

		//隐藏底稿
		String sql = " update z_task a,z_procedure b set ismust=replace(ismust,1,'') "
					+ " where a.taskcode in (" + tempTaskCodes + ") "
					+ " and a.projectId=? "
					+ " and b.projectId=? "
					+ " and a.isleaf=1 "
					+ " and concat(',',b.manuscript,',') not like concat('%,',a.taskcode,',%')  ";;
		dbUtil.executeUpdate(sql, args);

		//隐藏表页
		sql = " update z_sheettask a "
			+ " set a.property=0 "
			+ " where a.projectid=? "
			+ " and a.sheettaskcode in(" + tempTaskCodes + ") ";
		args = new Object[]{
				this.projectId
			};
		dbUtil.executeUpdate(sql, args);
		
		//显示有程序关联的表页
		sql = " update z_sheettask a,z_procedure b "
			+ " set a.property=1   "
			+ " where a.projectid=? "
			+ " and b.projectid=?   "
			+ " and a.sheettaskcode in(" + tempTaskCodes + ") "
			+ " and concat(',',b.manuscript,',') like concat('%,',a.sheettaskcode,',%')  ";

		args = new Object[]{
				this.projectId, 
				this.projectId
			};
		dbUtil.executeUpdate(sql, args);

		return true;
	}

	/**
	 * 设置要隐藏的任务
	 * @param taskcodes
	 * @return
	 * @throws Exception
	 */
	public boolean setHiddenTask(String taskcodes) throws Exception {

		String[] taskCode = taskcodes.split(",");

		String tempTaskCodes = "'-1'";

		//处理taskcode,使其可以放在in里面
		for(int i=0; i < taskCode.length; i++) {
			if(!"".equals(taskCode[i])) {
				tempTaskCodes += "," + "'" + taskCode[i] + "'";
			}
		}

		//切换数据库
		new DBConnect().changeDataBaseByProjectid(this.projectId, this.conn);
		DbUtil dbUtil = new DbUtil(conn);
		Object[] args = new Object[] {String.valueOf(TaskCommonService.TASK_STATE_CODE_MUST),
									  String.valueOf(TaskCommonService.TASK_STATE_CODE_DATA),
									  this.projectId };

		//首先无条件把状态去掉
		String sql = " update z_task set ismust=replace(replace(ismust,?,''),?,'') "
					+ " where taskcode in (" + tempTaskCodes + ") "
					+ " and projectId=? "
					+ " and isleaf=1 ";
		dbUtil.executeUpdate(sql, args);

		args = new Object[] {String.valueOf(TaskCommonService.TASK_STATE_CODE_MUST),
  				this.projectId };

		//处理sheettask表
		sql = " update z_sheettask set property=replace(property,?,'') "
			+ " where sheettaskcode in (" + tempTaskCodes + ") "
			+ " and projectId=? ";
		dbUtil.executeUpdate(sql, args);

		return true;
	}

	/**
	 * 获得必做底稿编号
	 * @param parentTaskId
	 * @return
	 * @throws Exception
	 */
	public String getMustTaskCodes(String parentTaskId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		PreparedStatement ps2 = null;
		ResultSet rs2 = null;

		String taskCodes = "";

		try {
			String sql = " select distinct taskId,taskCode "
					   + " from z_task "
					   + " where parenttaskId=? "
					   + " and projectId=? "
					   + " and isleaf=1 "
					   + " and ismust like '%1%' and ismust like '%3%'";

			new DBConnect().changeDataBaseByProjectid(this.projectId, this.conn);
			ps = conn.prepareStatement(sql);
			ps.setString(1, parentTaskId);
			ps.setString(2, this.projectId);

			rs = ps.executeQuery();

			sql = " select distinct sheettaskcode "
				+ " from z_sheettask "
				+ " where projectId=? "
				+ " and taskId=? "
				+ " and property like '%1%' "
				+ " order by sheettaskcode ";

			ps2 = conn.prepareStatement(sql);

			while(rs.next()) {
				String taskId = rs.getString(1);

				taskCodes += "," + rs.getString(2);

				ps2.setString(1, this.projectId);
				ps2.setString(2, taskId);

				rs2 = ps2.executeQuery();

				while(rs2.next()) {
					taskCodes += "," + rs2.getString(1);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return taskCodes + ",";
	}

	/**
	 * 判断是否所有必做底稿都已经一审通过
	 * @param ProjectId
	 * @return
	 * @throws Exception
	 */
	public boolean isMustTaskDone() throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = " select count(*) "
					   + " from z_task "
					   + " where projectId=? "
					   + " and isleaf=1 "
					   + " and ismust like '%1%'"
					   + " and (user5 is null or user5 = '')"
					   + " and  (auditproperty = '' or auditproperty is null or auditproperty like '%1%')" ;
			ps = conn.prepareStatement(sql) ;

			ps.setString(1,projectId) ;

			rs = ps.executeQuery() ;

			if(rs.next()) {

				int i = rs.getInt(1) ;

				if(i>0) {
					return false ;
				}
			   return true ;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return true ;
	}


	/**
	 * 判断是否所有退回底稿都已经整改完了
	 * @param ProjectId
	 * @return
	 * @throws Exception
	 */
	public boolean isBackTaskDone() throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = " select count(*) "
					   + " from z_task "
					   + " where projectId=? "
					   + " and isleaf=1 "
					   + " and ismust like '%1%'"
					   + " and (user4 is not null or user4 <> '')"
					   + " and (user1 is null or user1 = '')"  ;

			ps = conn.prepareStatement(sql) ;

			ps.setString(1,projectId) ;

			rs = ps.executeQuery() ;

			if(rs.next()) {

				int i = rs.getInt(1) ;

				if(i>0) {
					return false ;
				}
			   return true ;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return true ;
	}

	/**
	 * 判断项目中的底稿与模板库中的差异
	 * @param ProjectId
	 * @return
	 * @throws Exception
	 */
	public String getSynchronizeStr(String auditType) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			DbUtil dbUtil = new DbUtil(conn);
			StringBuffer sql = new StringBuffer();

			sql.append("select GROUP_CONCAT(taskId SEPARATOR '|') from k_tasktemplate a ") ;
			sql.append(" where typeid=? ");
			sql.append(" and not exists (  ");
			sql.append("    select 1 from z_task b");
			sql.append("      where b.ProjectID=?");
			sql.append(" 	   and a.taskid=b.taskid\n");
			sql.append(" )");

			Object[] params = new Object[] {
					auditType,
					this.projectId
			};

			return dbUtil.queryForString(sql.toString(),params);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return null ;
	}

	/**
	 * 判断项目中的底稿与模板库中的差异
	 * @param ProjectId
	 * @return
	 * @throws Exception
	 */
	public String getSynchronizeHtml(String typeId,String parentTaskId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ASFuntion CHF = new ASFuntion() ;

		try {
			StringBuffer sql = new StringBuffer();

			sql.append("select * from k_tasktemplate a ") ;
			sql.append(" where typeid=? and ParentTaskID=?");
			sql.append(" and not exists (  ");
			sql.append("    select 1 from z_task b");
			sql.append("      where b.ProjectID=?");
			sql.append("      and b.ParentTaskID=?");
			sql.append(" 	   and a.taskid=b.taskid\n");
			sql.append(" )");

			ps = conn.prepareStatement(sql.toString()) ;
			ps.setString(1,typeId) ;
			ps.setString(2,parentTaskId) ;
			ps.setString(3,this.projectId) ;
			ps.setString(4,parentTaskId) ;

			rs = ps.executeQuery() ;
			StringBuffer htmlStr = new StringBuffer();
			String bgColor = "#C5D8FC";
			while(rs.next()) {
				String isleaf = CHF.showNull(rs.getString("isLeaf"));
				String taskId = CHF.showNull(rs.getString("taskId")) ;
				String taskCode = CHF.showNull(rs.getString("taskCode"));
				String taskName = CHF.showNull(rs.getString("taskName")) ;

				if("1".equals(isleaf)){
					bgColor = "#f3f5f8";
				}

				htmlStr.append("<tr id=\""+typeId+"_"+parentTaskId+"1\" isleaf='"+isleaf+"' taskId='"+taskId+"' parentId='"+parentTaskId+"' height=18 onMouseOver=\"this.bgColor='#E4E8EF';\" dataRow=\"yes\" onMouseOut=\"this.bgColor='"
						+ bgColor + "';\"  bgColor=\""
						+ bgColor + "\">");
				htmlStr.append("<td align=\"center\">") ;
				htmlStr.append("<input type=\"checkbox\"   name=\"choose_synchronizeTemplateList\" id=\"choose_synchronizeTemplateList\" value='"+taskId+"' onmouseup=\"nothing();\" ");
				htmlStr.append("onClick=\"rowSelectStyle('synchronizeTemplateList');setChooseValue_CH_synchronizeTemplateList();\" style='height:16px;width:16px;'>");
				htmlStr.append("</td>") ;
				htmlStr.append("<td onselectstart=\"return false\" onmousemove=\"return selectMultiCell(event);\" onmousedown=\"return selectCell(event)\"> ");
						if("0".equals(isleaf)) {
							htmlStr.append("<img src=\"images/plus.jpg\" onclick=\"changeImg(this);goSubSort(this);\">") ;
						}
				htmlStr.append(taskCode+"</td>");
				htmlStr.append("<td onselectstart=\"return false\" onmousemove=\"return selectMultiCell(event);\" onmousedown=\"return selectCell(event)\"> "+ taskName+"</td>") ;
				if("0".equals(isleaf)){
					htmlStr.append("<td onselectstart=\"return false\" onmousemove=\"return selectMultiCell(event);\" onmousedown=\"return selectCell(event)\">环节</td>") ;
				}else {
					htmlStr.append("<td onselectstart=\"return false\" onmousemove=\"return selectMultiCell(event);\" onmousedown=\"return selectCell(event)\">底稿</td>") ;
				}

				htmlStr.append("</tr>") ;
			}
			htmlStr.append("<input type=\"hidden\" name=\""+typeId+"_"+parentTaskId+"\" id=\""+typeId+"_"+parentTaskId+"\" value=\"\" >") ;
			return htmlStr.toString() ;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return "" ;
	}


	/**
	 * 更新底稿任务
	 * @param task
	 * @throws Exception
	 */
	public void updateTaskPeople(String userOpt,String userId,String dateOpt) throws Exception {
		PreparedStatement ps = null;
		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
			String sql = "update z_task set "+userOpt+"=?,"+dateOpt+"=now() where isleaf=1 and projectId=?" ;
			ps = conn.prepareStatement(sql) ;
			ps.setString(1, userId) ;
			ps.setString(2,this.projectId) ;
			ps.executeUpdate() ;
		} catch (Exception e) {
			Debug.print(Debug.iError, "更新底稿任务失败", e);
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 更新底稿父结点
	 * @param task
	 * @throws Exception
	 */
	public void updateTaskParentId(String taskId,String parentTaskId) throws Exception {
		PreparedStatement ps = null;
		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
			String fullPath = getTaskByTaskId(parentTaskId).getFullPath()+ taskId ;
			String sql = "update z_task set ParentTaskID=?,FullPath=? where taskId=?" ;
			ps = conn.prepareStatement(sql) ;
			ps.setString(1,parentTaskId) ;
			ps.setString(2,fullPath) ;
			ps.setString(3,taskId) ;
			ps.executeUpdate() ;
		} catch (Exception e) {
			Debug.print(Debug.iError, "更新底稿父结点失败", e);
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 更新底稿名称
	 * @param task
	 * @throws Exception
	 */
	public void updateTaskName(String taskId,String taskName,String taskCode) throws Exception {
		PreparedStatement ps = null;
		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
			String sql = "update z_task set taskName=?,taskCode=? where taskId=? and projectId=?" ;
			ps = conn.prepareStatement(sql) ;
			ps.setString(1,taskName) ;
			ps.setString(2,taskCode) ;
			ps.setString(3,taskId) ;
			ps.setString(4,this.projectId); 
			ps.executeUpdate() ;
		} catch (Exception e) {
			Debug.print(Debug.iError, "更新底稿名称失败", e);
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 检查底稿名称唯一
	 * @param fileName
	 * @throws Exception
	 */
	public String checkNameOnly(String fileName) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
			String sql = "select 1 from z_task where projectId = '" + projectId + "' and taskname= '" + fileName + "'" ;
			System.out.println(sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()) {
				return "yes";
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DbUtil.close(ps);
		}
		return "no";
	}
	
	/**
	 * 返回必做底稿
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public String getMustSheetTask(String taskId, boolean isAllSheet) throws Exception {

		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
			String sql = " select ifnull(group_concat(sheettaskcode),'') from z_sheettask "
						+ " where projectid=? "
						+ " and taskid=? ";
			
			if(!isAllSheet) {
				sql += " and property like '%1%'" ;
			}					
			
			Object[] object = {this.projectId,taskId};
			
			return new DbUtil(conn).queryForString(sql, object);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return "";
	}
	
	/**
	 * 返回指定模板指定类别的底稿
	 * @return
	 * @throws Exception
	 */
	public String getTasksBytemplateIdAndType(String sqlWhere, String tempId) throws Exception {

		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
			
			
			String sql = " select group_concat(taskId) from k_tasktemplate where isleaf=1 and typeId=?"+ sqlWhere;
			
			
			Object[] object = {tempId};
			
			return new DbUtil(conn).queryForString(sql, object);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return "";
	}
	
	/**
	 * 根据底稿编号返回底稿列表
	 * @param parentTaskId
	 * @return
	 * @throws Exception
	 */
	public List getTaskListByTaskIds(String taskIds) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		List taskList = new ArrayList();
		Task task = null;

		String [] taskId = null;
		String taskIdsStr = "'-1'";

		if(taskIds != null) {
			taskId = taskIds.split(",");

			for(int i=0; i < taskId.length; i++) {
				if(!"".equals(taskId[i])) {
					taskIdsStr += "," + "'" + taskId[i] + "'";
				}
			}
		}

		try {
			String sql = "select distinct a.*,b.name as name0 from z_task a "
						+ " left join k_user b on a.user0 = b.id "
						+ " where projectid= ? "
						+ " and taskId in(" + taskIdsStr + ") "
						+ " order by orderid";

			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);

			rs = ps.executeQuery();
			
			while (rs.next()) {
				task = new Task();
				task.setTaskId(rs.getString("Taskid"));
				task.setTaskCode(rs.getString("TaskCode"));
				task.setTaskName(rs.getString("Taskname"));
				task.setTaskContent(rs.getString("TaskContent"));
				task.setDescription(rs.getString("Description"));
				task.setParentTaskId(rs.getString("ParentTaskID"));
				task.setProjectId(rs.getString("ProjectID"));
				task.setIsLeaf(rs.getInt("IsLeaf"));
				task.setLevel(rs.getInt("Level0"));
				task.setManuId(rs.getString("manuid"));
				task.setManuTemplateId(rs.getString("manutemplateid"));

				task.setUser0(rs.getString("name0"));
				task.setUser1(rs.getString("user1"));
				task.setUser2(rs.getString("user2"));
				task.setUser3(rs.getString("user3"));
				task.setUser4(rs.getString("user4"));
				task.setUser5(rs.getString("user5"));

				task.setProperty(rs.getString("property"));
				task.setFullPath(rs.getString("fullpath"));
				task.setSheetList(getSheetTaskList(task.getTaskId()));
				taskList.add(task);
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return taskList;
	}

	/**
	 * main方法,用于测试
	 *
	 * @param args
	 */
	public static void main(String args[]) throws Exception {
		Connection conn = null;
		try {
			String url = "jdbc:mysql://192.168.1.3:5188/asdb?characterEncoding=GBK";
			String userName = "xoops_root";
			String password = "654321";

			Class.forName("com.mysql.jdbc.Driver");
			conn =  DriverManager.getConnection(url, userName, password);

			TaskService taskService = new TaskService(conn,"20089942");
//			String temp = taskService.getTaskByTaskCode("A3").getTaskName();
//			System.out.println(temp);
//
//			System.out.println(temp);
//
//			System.out.println(taskService.getTaskList("19").size());
			//String taskCodes = "cccc~ddd";
			System.out.println(taskService.getMustTaskCodes("103117"));
			//taskService.updateOrderId();
			//System.out.println(taskService.getTaskListByParentTaskId("10501"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

	}
}
