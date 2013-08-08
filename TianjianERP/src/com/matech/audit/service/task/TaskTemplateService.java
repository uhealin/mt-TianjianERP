package com.matech.audit.service.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.task.model.SheetTask;
import com.matech.audit.service.task.model.Task;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.Debug;

public class TaskTemplateService {
	private Connection conn = null;

	private String typeId = null;

	/**
	 * 构造方法,初始化数据库连接和项目编号
	 * @param conn	数据库连接
	 * @param typeId	模板编号
	 * @throws Exception
	 */
	public TaskTemplateService(Connection conn, String typeId) throws Exception {
		DbUtil.checkConn(conn);
		this.conn = conn;
		this.typeId = typeId;
	}

	/**
	 * 根据TaskId取得模板底稿详细信息
	 * @param taskId
	 * @return
	 */
	public Task getTaskTemplateByTaskId(String taskId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Task task = new Task();

		try {
			String sql = "select * from asdb.k_tasktemplate "
						+ " where taskId = ? "
						+ " and typeId = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, taskId);
			ps.setString(2, this.typeId);
			rs = ps.executeQuery();

			if (rs.next()) {
				task.setTaskId(rs.getString("Taskid"));
				task.setTaskCode(rs.getString("TaskCode"));
				task.setTaskName(rs.getString("Taskname"));
				task.setTaskContent(rs.getString("TaskContent"));
				task.setDescription(rs.getString("Description"));
				task.setParentTaskId(rs.getString("ParentTaskID"));
				task.setProjectId(rs.getString("typeId"));
				task.setIsLeaf(rs.getInt("IsLeaf"));
				task.setLevel(rs.getInt("Level0"));
				task.setManuId(rs.getString("manuid"));
				task.setManuTemplateId(rs.getString("manutemplateid"));
				task.setProperty(rs.getString("property"));
				task.setFullPath(rs.getString("fullpath"));
				task.setSubjectName(rs.getString("subjectName"));
				task.setOrderId(rs.getString("orderid"));
			}

			return task;
		} catch (Exception e) {
			Debug.print(Debug.iError, "获得模板底稿信息失败", e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public String getTaskIdByTaskName(String taskName, boolean isleaf) throws Exception {
		String sql = " select taskId from k_tasktemplate "
					+ " where taskname=? "
					+ " and typeId=? "
					+ " and isleaf=? ";
		
		Object[] args = null;
		
		if(isleaf) {
			args = new Object[]{taskName, this.typeId, "1"};
		} else {
			args = new Object[]{taskName, this.typeId, "0"};
		}
		
		return new DbUtil(conn).queryForString(sql, args);
	}

	public Task getTaskTemplateByTaskCode(String taskCode) throws Exception {

		String taskId = this.getTaskTemplateIdByTaskCode(taskCode);

		return this.getTaskTemplateByTaskId(taskId);
	}

	public String getTaskTemplateIdByTaskCode(String taskCode) throws Exception {
		DbUtil dbUtil = new DbUtil(conn);
		String sql = "select taskId from asdb.k_tasktemplate "
				+ " where TaskCode = ? "
				+ "	and typeId= ? ";

		Object[] object = new Object[] { taskCode, this.typeId };
		return dbUtil.queryForString(sql, object);
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
			String sql = "select distinct a.* from k_tasktemplate a,k_tasktemplate b "
						+ " where a.typeId= ? "
						+ " and b.typeId=? "
						+ " and b.taskid=? "
						+ " and a.FullPath like concat(b.fullpath,'%') "
						+ " order by a.orderid";

			ps = conn.prepareStatement(sql);
			ps.setString(1, this.typeId);
			ps.setString(2, this.typeId);
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
				task.setIsLeaf(rs.getInt("IsLeaf"));
				task.setLevel(rs.getInt("Level0"));
				task.setManuId(rs.getString("manuid"));
				task.setManuTemplateId(rs.getString("manutemplateid"));

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

			String sql = "select distinct typeId,taskid,sheettaskcode,taskcode,sheetname,property "
						+ " from k_sheettasktemplate "
						+ " where taskid=? "
						+ " and typeId=? "
						+ " order by sheettaskcode ";

			ps = conn.prepareStatement(sql);
			ps.setString(1, taskId);
			ps.setString(2, this.typeId);

			rs = ps.executeQuery();

			while(rs.next()) {
				sheetTask = new SheetTask();

				//sheetTask.setProjectId(rs.getString(1));
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

			String strSql = "select parentTaskId from `k_tasktemplate` "
							+ " where taskid = ? "
							+ " and typeId = ?";

			ps = conn.prepareStatement(strSql);
			ps.setString(1, taskId);
			ps.setString(2, this.typeId);

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

	public static void main(String[] args) {

		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			Task task = new TaskTemplateService(conn,"92").getTaskTemplateByTaskCode("SA5-3");
			System.out.println(task.getTaskCode());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

	}
}
