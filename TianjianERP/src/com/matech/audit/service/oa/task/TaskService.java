package com.matech.audit.service.oa.task;

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

import com.matech.audit.service.oa.task.model.Task;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.pub.util.UTILString;

public class TaskService {

	/**
	 * 必做状态
	 */
	public static final int TASK_STATE_CODE_MUST = 1;

	/**
	 * 关注状态
	 */
	public static final int TASK_STATE_CODE_ATTENTION = 2;

	/**
	 * 有数据状态
	 */
	public static final int TASK_STATE_CODE_DATA = 3;

	/**
	 * 已处理保存状态
	 */
	public static final int TASK_STATE_CODE_SAVED = 4;

	public static final String TASK_STATE_COLOR_MUST = "#B33232";	//必做
	public static final String TASK_STATE_COLOR_ATTENTION = "#D97125"; //关注
	public static final String TASK_STATE_COLOR_DATA = "#1A76B7"; //有数据
	public static final String TASK_STATE_COLOR_SAVED = "#66A72D"; //已处理保存

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
			//throw new Exception("项目Id不能为空!");
			this.projectId = "0";
		} else {
			this.projectId = projectId;
		}
	}

	/**
	 * 增加一张底稿,该方法会重新计算level,fullPath,orderid
	 * @param task
	 * @throws Exception
	 */
	public void addTask(Task task) throws Exception {
		PreparedStatement ps = null;
		try {

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

			String sql = "INSERT INTO jbpm_z_task( "
						+ " Taskid,Taskcode,TaskName,TaskContent,Description,ParentTaskID, "
						+ " ProjectID,IsLeaf,Level0,Fullpath,Property,orderid,subjectname) "
						+ " VALUES(?,?,?,?,?,?, ?,?,?,?,?,?, ?)";

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
			if (parentId.equals("0")) {
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

		String sql = "select count(TaskId) from jbpm_z_task "
					+ " where (user1 <> '' or user2 <> '' or user3 <> '') "
					+ " and projectId = ? ";

		try {
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
			String sql = "select AuditTarget from `jbpm_z_target` "
						+ " where AuditTarget > '' "
						+ " and projectid = ? "
						+ " and taskid = ? "
						+ " order by autoid";

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

				String sql = "select fullpath from jbpm_z_task "
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

			String strSql = "select ifnull(max(taskid),0)+1 from jbpm_z_task";
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

			String strSql = "select parentTaskId from `jbpm_z_task` "
							+ " where taskid = ? "
							+ " and projectId = ?";


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

			String sql = "Select * From jbpm_z_task "
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
				task.setProperty(rs.getString("property"));
				task.setFullPath(rs.getString("fullpath"));
				task.setSubjectName(rs.getString("subjectName"));
				task.setOrderId(rs.getString("orderid"));

				task.setDescription(rs.getString("description"));

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
		DbUtil dbUtil = new DbUtil(conn);

		String sql = "select count(*) from jbpm_z_task "
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
		DbUtil dbUtil = new DbUtil(conn);
		String sql = "select taskId from jbpm_z_task "
				+ " where TaskCode = ? "
				+ "	and projectid= ? "
				+ " order by isleaf desc ";

		Object[] object = new Object[] {
				taskCode,
				this.projectId
		};
		return dbUtil.queryForString(sql, object);
	}

	public String getTaskIdByTaskCode(String taskCode,String taskName) throws Exception {
		DbUtil dbUtil = new DbUtil(conn);
		String sql = "select taskId from jbpm_z_task "
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
		DbUtil dbUtil = new DbUtil(conn);
		String sql = "select taskId from jbpm_z_task "
				+ " where TaskCode = ? "
				+ "	and projectid= ? "
				+ " and isleaf=1 ";

		Object[] object = new Object[] {
				taskCode,
				this.projectId
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
					+ "	select taskCode,taskid,manuid from jbpm_z_task "
					+ "		where projectid = ? "
					+ " 	and isleaf=1 "
					+ "		and taskname not like '%.doc' "
					+ "		and (user0='' or user0 is null or user0 = ?) "
					+ "		and property<>'tache' and property <> 'target' "
					+ "	order by orderid)t1 "

					+ "union "

					+ "select * from ( select  taskCode,taskid,manuid "
					+ "		from jbpm_z_task "
					+ "		where projectid = ? "
					+ "		and isleaf=1 "
					+ "		and taskname like '%.doc' "
					+ "		and (user0='' or user0 is null or user0 = ?) "
					+ "		and property<>'tache' and property <> 'target' "
					+ "	order by orderid)t2 ";

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
			String sql = "select taskName from jbpm_z_task "
							+ " where projectid = ? "
							+ " and taskid = ?";
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
				+ "	select DISTINCT concat(conv(b.id,10,10),'-',b.name) as name from jbpm_z_task a,k_user b"
				+ "		where parenttaskid = ? "
				+ "		and projectid = ?"
				+ "		and a.user0 = b.id "
				+ "		order by b.id) taskuser";

		try {

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
			String sql = "select k.username from jbpm_z_task z,k_user k"
					+ " where taskId = ? "
					+ " and projectid = ?"
					+ " and z.user1 = k.name";

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
						+ " from jbpm_z_task a,jbpm_z_procedure b,z_sheettask c \n "
						+ " where a.taskid=? \n "
						+ " and c.taskId=? \n "
						+ " and a.projectid=? \n "
						+ " and b.projectid=? \n "
						+ " and c.projectid=? \n "
						+ " and a.parenttaskId=b.taskId \n "
						+ " and b.manuscript like concat('%,',c.sheettaskcode,',%') \n ";

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
				sheetTaskNames += "`" + rs.getString(2);
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
				sheetTaskNames += "`" + rs.getString(2);
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
				+ " from jbpm_z_task z,z_project p"
				+ " where z.taskid = ?"
				+ " and z.projectid = ?"
				+ " and z.projectid = p.projectid";

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
			updateParentId = "update jbpm_z_task set ParentTaskID= '" + newParentId
							+ "' where TaskID = '" + taskId + "' "
							+ " and ProjectID = '" + projectId + "'";

		} else {
			//新上级目录为根目录，ParentTaskID直接为0

			//当新上级目录为根目录时,更新 ParentTaskID 的SQL语句
			updateParentId = " update jbpm_z_task set "
							+ " ParentTaskID= '0',Level0=0 "
							+ " where TaskID = '" + taskId + "' "
							+ " and ProjectID = '" + projectId + "'";
		}

		oldFullPath = getFullPath(taskId);

		if (!oldParentId.equals("0")) {
			//原上级目录不为根目录
			//当原路径不为根目录时,更新fullpath的sql语句
			updateFullPath = "update jbpm_z_task set "
							+ " FullPath = REPLACE(fullpath,'" + oldFullPath + "','" + newFullPath + taskId + "|')"
							+ " where FullPath like '" + oldFullPath + "%'"
							+ " and ProjectID = '" + projectId + "'";

		} else {
			//原上级目录为根目录

			//当原路径为根目录时,更新fullpath的sql语句
			updateFullPath = "update jbpm_z_task set "
							+ " FullPath = concat('" + newFullPath + "',FullPath) "
							+ " where FullPath like '" + oldFullPath + "%' "
							+ " and ProjectID = '" + projectId + "'";
		}

		String updateLevel = "update jbpm_z_task a,jbpm_z_task b set "
							+ " a.Level0 = b.Level0+1 "
							+ " where a.parenttaskid = b.TaskID "
							+ " and a.ProjectID = '" + projectId + "'";

		try {

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
	 * 根据项目ID修复底稿全路径(level和fullpath错误时可以使用)
	 * 如果level正确,可以自己进mysql恢复:
	 *
	 * 1,恢复level=0的全路径:update `jbpm_z_task`  set fullpath = concat(taskid,'|') where `Level0` = 0
	 * 2,恢复level=n(n必须按从小到大的顺序进行恢复)全路径:
	 *
	 * 		update `jbpm_z_task` a,(select fullpath,taskid,projectid,`Level0` from `jbpm_z_task` ) b
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

			String sql = "select taskid from `jbpm_z_task` "
						+ " where projectId = ? "
						+ " and (property not like 'A%' or property is null) "
						+ " order by orderid";

			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);

			rs = ps.executeQuery();

			while (rs.next()) {
				String taskId = rs.getString(1);
				String fullPath = calFullPath(taskId);
				int level = fullPath.split("\\|").length - 1;
				sql = "update `jbpm_z_task` set "
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
	 * 1,恢复level=0的全路径:update `jbpm_z_task`  set fullpath = concat(taskid,'|') where `Level0` = 0
	 * 2,恢复level=n(n必须按从小到大的顺序进行恢复)全路径:
	 *
	 * 		update `jbpm_z_task` a,(select fullpath,taskid,projectid,`Level0` from `jbpm_z_task` ) b
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

			String sql = "select taskid from `jbpm_z_task` "
						+ " where projectId = ? "
						+ " and (property not like 'A%' or property is null) "
						+ " order by orderid";

			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);

			rs = ps.executeQuery();

			while (rs.next()) {
				String taskId = rs.getString(1);
				String fullPath = calFullPath(taskId);
				int level = fullPath.split("\\|").length - 1;
				sql = "update `jbpm_z_task` set "
						+ " fullpath= '" + fullPath + "', "
						+ " Level0= " + level + " "
						+ " where taskId = '" + taskId + "'"
						+ " and projectid = '" + projectId + "'";

				stmt = conn.createStatement();
				System.out.println(sql);
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

	public void repairTaskCode() throws Exception {
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		Statement stmt = null;

		try {

			String sql = " select taskId,taskcode from jbpm_z_task "
					   + " where projectid=? "
					   + " and isleaf='0' "
					   + " order by orderid ";



			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);

			rs = ps.executeQuery();

			String taskCode = "";
			stmt = conn.createStatement();

			while (rs.next()) {
				taskCode = rs.getString(2);

				sql = " select taskId from jbpm_z_task where projectid=? and parenttaskid=? order by Level0 ";
				ps2 = conn.prepareStatement(sql);
				ps2.setString(1, this.projectId);
				ps2.setString(2, rs.getString(1));

				rs2 = ps2.executeQuery();

				int i=1;

				while(rs2.next()) {
					String sql2 = " update jbpm_z_task set taskcode='" + (taskCode + "-" + i) + "' "
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
		String sql = "select taskid,taskcode from jbpm_z_task where projectid = ? ";

		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, projectId);
			rs = ps.executeQuery();

			while (rs.next()) {
				String taskId = rs.getString(1);
				String taskcode = rs.getString(2);

				sql = "update jbpm_z_task set orderid = ? "
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
			boolean isNum = false;

			try {
				Integer.parseInt(task.getProperty());
				isNum = true;
			} catch (NumberFormatException e) {
				//e.printStackTrace();
			}

			if (!"".equals(task.getProperty()) && isNum) {

				sql = "update jbpm_z_task set property = null "
					+ " where projectid = ?"
					+ " and property = ? ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, this.projectId);
				ps.setString(2, task.getProperty());
				ps.execute();

				sql = "update jbpm_z_task set property = ? "
					+ " where ProjectId = ? "
					+ " and TaskId = ?";
				ps = conn.prepareStatement(sql);
				ps.setString(1, task.getProperty() );
				ps.setString(2, this.projectId );
				ps.setString(3, task.getTaskId() );
				ps.execute();
			}

			sql = "update jbpm_z_task set "
				+ " TaskName=?, TaskContent=?, Description=?, TaskCode=?, Orderid=?,subjectName=? "
				+ " where TaskID = ? "
				+ " and projectid=? ";

			ps = conn.prepareStatement(sql);

			ps.setString(1, task.getTaskName());
			ps.setString(2, task.getTaskContent());
			ps.setString(3, task.getDescription());
			ps.setString(4, task.getTaskCode());
			ps.setString(5, UTILString.getOrderId(task.getTaskCode()));
			ps.setString(6, task.getSubjectName());

			ps.setString(7, task.getTaskId());
			ps.setString(8, this.projectId);

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

		DbUtil dbUtil = new DbUtil(conn);

		String sql = "select count(*) from jbpm_z_task "
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
		DbUtil dbUtil = new DbUtil(conn);

		String sql = "select 1 from jbpm_z_task "
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
					   + " from jbpm_z_task "
					   + " where parenttaskId=? "
					   + " and projectId=? "
					   + " and isleaf=1 "
					   + " and ismust like '%1%' and ismust like '%3%'";

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
	 * 从数据库中取得最大的TaskCode
	 * 原方法：getMaxTaskCode(String ptaskId, String projectId);
	 *
	 * 前记：
	 * 1、一般情况下是根据父任务编号、当前项目编号来获得当前级别的最大任务编码，如果当前级别的
	 *   的记录集为空，意味着该父任务编号下没有子任务结点，则取父任务编码。
	 * 2、父任务编号为0，意味着它是最顶层的任务结点，则取与其同级的任务结点集中最大的任务编码。
	 *
	 * @param parentTaskId String
	 * @return String
	 */
	public String getMaxTaskCodeByParentTaskId(String parentTaskId) throws Exception{
		String maxTaskCode = "";
		String sql = "";

		if (parentTaskId == null
				|| projectId == null
				|| "".equals(parentTaskId)
				|| "".equals(projectId) ) {

			return maxTaskCode;
		}

		DbUtil dbUtil = new DbUtil(conn);
		//构造数据库查询语句
		sql = "select distinct a.taskcode from jbpm_z_task a, jbpm_z_task b "
			+ " where a.projectid = ? "
			+ " and b.projectid = ? "
			+ " and b.taskid= ? "
			+ " and a.Level0=b.Level0+1 "
			+ " and a.parenttaskid=b.taskid "
			+ " order by a.orderid desc";

		Object[] params = new Object[] { this.projectId, this.projectId, parentTaskId };

		//如果parentTaskId为0
		if ("0".equals(parentTaskId)) {
			sql = "select taskcode from jbpm_z_task "
				+ " where projectid= ? "
				+ " and parenttaskid=0 "
				+ " order by orderid desc";
			params = new Object[] { this.projectId };

			//执行数据库的检索
			maxTaskCode = dbUtil.queryForString(sql,params);

			if(maxTaskCode == null) {
				maxTaskCode = "00";
			}

		} else {
			maxTaskCode = dbUtil.queryForString(sql,params);

			if(maxTaskCode == null) {
				sql = "select taskCode from jbpm_z_task where "
					+ " projectid = ? "
					+ " and taskid = ? ";
				params = new Object[] { this.projectId, parentTaskId};
				maxTaskCode = dbUtil.queryForString(sql, params);

				if(maxTaskCode != null) {
					maxTaskCode = maxTaskCode + "-0";
				}
			}

		}
		return maxTaskCode;

	}

	/**
	 * 根据parentTaskCode从数据库中取得最大的TaskCode
	 * 原方法：getMaxTaskCode1(String ptaskCode, String projectId);
	 *
	 * 前记：
	 * 1、一般情况下是根据父任务编号、当前项目编号来获得当前级别的最大任务编码，如果当前级别的
	 *   的记录集为空，意味着该父任务编号下没有子任务结点，则取父任务编码。
	 * 2、父任务编号为0，意味着它是最顶层的任务结点，则取与其同级的任务结点集中最大的任务编码。
	 * 和getMaxTaskCode函数的区别就是，一个是根据taskid来获得，还有一个是根据taskcode来获得
	 *
	 * @param parentTaskCode 底稿索引号
	 * @return parentTaskCode下面最大的底稿索引号
	 */
	public String getMaxTaskCodeByParentTaskCode(String parentTaskCode) throws Exception {
		String maxTaskCode = "";
		String sql = "";

		if (parentTaskCode == null
				|| projectId == null
				|| "".equals(parentTaskCode)
				|| "".equals(projectId) ) {

			return maxTaskCode;
		}

		DbUtil dbUtil = new DbUtil(conn);
		//构造数据库查询语句
		sql = "select distinct a.taskcode from jbpm_z_task a, jbpm_z_task b "
			+ " where a.projectid = ? "
			+ " and b.projectid = ? "
			+ " and b.taskcode = ? "
			+ " and a.Level0=b.Level0+1 "
			+ " and a.parenttaskid=b.taskid "
			+ " order by a.orderid desc";

		Object[] params = new Object[] { this.projectId, this.projectId, parentTaskCode };

		//如果parentTaskCode为0
		if ("0".equals(parentTaskCode)) {
			sql = "select taskcode from jbpm_z_task "
				+ " where projectid= ? "
				+ " and parenttaskid=0 "
				+ " order by orderid desc";
			params = new Object[] { this.projectId };

			//执行数据库的检索
			maxTaskCode = dbUtil.queryForString(sql,params);

			if(maxTaskCode == null) {
				maxTaskCode = "00";
			}

		} else {
			maxTaskCode = dbUtil.queryForString(sql,params);

			if(maxTaskCode == null) {
				sql = "select taskCode from jbpm_z_task where "
					+ " projectid = ? "
					+ " and taskcode = ? ";

				params = new Object[] { this.projectId, parentTaskCode};
				maxTaskCode = dbUtil.queryForString(sql, params);

				if(maxTaskCode != null) {
					maxTaskCode = maxTaskCode + "-0";
				}
			}

		}
		return maxTaskCode;
	}

	/**
	 * 根据父底稿编号parentTaskId和是否叶子isleaf获取最大的taskcode
	 * 原方法：getMaxTaskCodeByIsLeaf(String ptaskId, String projectId, int isleaf);
	 *
	 * @param parentTaskId  底稿编号
	 * @param isleaf 是否叶子
	 * @return
	 */
	public String getMaxTaskCodeByParentTaskId(String parentTaskId, int isleaf) throws Exception {
		String maxTaskCode = "";
		String sql = "";

		if (parentTaskId == null
				|| projectId == null
				|| "".equals(parentTaskId)
				|| "".equals(projectId) ) {

			return maxTaskCode;
		}

		DbUtil dbUtil = new DbUtil(conn);
		//构造数据库查询语句
		sql = "select distinct a.taskcode from jbpm_z_task a, jbpm_z_task b "
			+ " where a.projectid = ? "
			+ " and b.projectid = ? "
			+ " and b.taskid= ? "
			+ " and a.isleaf = ? "
			+ " and a.parenttaskid=b.taskid "
			+ " order by a.orderid desc";

		Object[] params = new Object[] {
				this.projectId,
				this.projectId,
				parentTaskId,
				new Integer(isleaf)
		};

		//如果parentTaskId为0
		if ("0".equals(parentTaskId)) {
			sql = "select taskcode from jbpm_z_task "
				+ " where projectid= ? "
				+ " and parenttaskid=0 "
				+ " order by orderid desc";
			params = new Object[] { this.projectId };

			//执行数据库的检索
			maxTaskCode = dbUtil.queryForString(sql,params);

			if(maxTaskCode == null) {
				maxTaskCode = "00";
			}

		} else {
			maxTaskCode = dbUtil.queryForString(sql,params);

			if(maxTaskCode == null) {
				sql = "select taskCode from jbpm_z_task where "
					+ " projectid = ? "
					+ " and taskid = ? ";
				params = new Object[] { this.projectId, parentTaskId};
				maxTaskCode = dbUtil.queryForString(sql, params);

				if(maxTaskCode != null) {
					maxTaskCode = maxTaskCode + "-0";
				}
			}

		}
		return maxTaskCode;
	}

	/**
	 * 检查底稿索引号taskcode是否唯一
	 * @param taskCode 索引号
	 * @return
	 * @throws Exception
	 */
	public String checkIsOnlyByTaskCode2(String taskCode) throws Exception {
		DbUtil dbUtil = new DbUtil(conn);

		String sql = "select taskname from jbpm_z_task "
					+ " where projectid=? "
					+ " and taskcode= ? ";

		Object[] params = new Object[] { this.projectId, taskCode};

		return dbUtil.queryForString(sql, params);
	}

	/**
	 * 改变底稿状态
	 * @param taskId
	 * @param stateCode
	 * @param state
	 * @throws Exception
	 */
	public void setTaskState(String taskId, int stateCode, boolean state) throws Exception {

		//首先无条件把状态去掉
		String sql = " update jbpm_z_task set ismust=replace(ismust,?,'') "
					+ " where taskid=? "
					+ " and projectId=? ";

		DbUtil dbUtil = new DbUtil(conn);
		Object[] args = new Object[] {String.valueOf(stateCode),
										taskId,
										this.projectId };

		dbUtil.executeUpdate(sql, args);

		//如果对应的状态为true,则在后面追加
		if(state) {
			sql = " update jbpm_z_task set ismust=concat(ifnull(ismust,''),?) "
		   		+ " where taskid=? "
		   		+ " and projectId=? ";

			dbUtil.executeUpdate(sql, args);
		}
	}

	/**
	 * 返回底稿对应属性的状态
	 * @param taskId
	 * @param stateCode
	 * @return
	 * @throws Exception
	 */
	public boolean getTaskState(String taskId, int stateCode) throws Exception {
		String sql = " select ismust from jbpm_z_task "
					+ " where taskid=? "
					+ " and projectId=? ";

		DbUtil dbUtil = new DbUtil(conn);
		Object[] args = new Object[] {
							taskId,
							this.projectId
						};

		String result = "" + dbUtil.queryForString(sql, args);

		return result.indexOf(String.valueOf(stateCode)) > -1;
	}

	/**
	 * 检查底稿索引号taskcode是否唯一,只针对底稿
	 * @param taskCode 索引号
	 * @return
	 * @throws Exception
	 */
	public String checkIsOnlyByTaskCode(String taskCode) throws Exception {
		DbUtil dbUtil = new DbUtil(conn);

		String sql = "select taskname from jbpm_z_task "
					+ " where projectid=? "
					+ " and taskcode= ? "
					+ " and isleaf=1 ";

		Object[] params = new Object[] { this.projectId, taskCode};

		return dbUtil.queryForString(sql, params);
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
