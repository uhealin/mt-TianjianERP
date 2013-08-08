package com.matech.audit.service.target;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.procedure.ProcedureService;
import com.matech.audit.service.target.model.Target;
import com.matech.audit.service.task.TaskService;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;

/**
 * <p>Title: 审计目标类</p>
 * <p>Description: 审计目标的增删改查等</p>
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
 * 2007-7-4
 */
public class TargetService {
	private Connection conn = null;

	private String projectId = null;

	private String tempTableName = "tt_z_target";

	/**
	 * 构造方法,初始化数据库连接和项目编号
	 * @param conn	数据库连接
	 * @param projectId	项目编号
	 * @throws Exception
	 */
	public TargetService(Connection conn, String projectId) throws Exception {
		DbUtil.checkConn(conn);
		this.conn = conn;

		if ("".equals(projectId) || projectId == null) {
			throw new Exception("项目Id不能为空!");
		}

		this.projectId = projectId;
	}

	/**
	 * 根据taskId返回审计目标和程序
	 * @param id
	 * @return
	 */
	public Target getTargetAndProByTaskId(String taskId) {
		Target target = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			String strSql = "select * from z_target where taskId = ? and projectid = ?";
			ps = conn.prepareStatement(strSql);
			ps.setString(1, taskId);
			ps.setString(2, projectId);
			rs = ps.executeQuery();

			if (rs.next()) {
				Set procedureSet = new ProcedureService(conn, projectId)
						.getProceduresByTaskId(taskId);
				target = new Target();

				target.setAutoID(rs.getString("AutoID"));
				target.setProjectID(rs.getString("ProjectID"));
				target.setTaskId(rs.getString("TaskID"));
				target.setExecuteIt(rs.getString("ExecuteIt"));
				target.setState(rs.getString("State"));
				target.setDefineId(rs.getString("DefineID"));
				target.setAuditTarget(rs.getString("AuditTarget"));
				target.setCorrelationExeProcedure(rs
						.getString("CorrelationExeProcedure"));
				target.setRemark(rs.getString("Remark"));
				target.setCognizance(rs.getString("cognizance"));
				target.setProperty(rs.getString("property"));
				target.setAuditProcedures(procedureSet);
			}

			return target;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return null;
	}

	/**
	 * 返回所有目标和程序,注意,该方法在数据量较大的时候将会很慢,
	 * 建议用getTargetListByParentTaskId(String parentTaskId)方法 + ajax返回审计程序
	 * @return
	 */
	public List getTargetAndProList() {
		Target target = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List list = null;

		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			String strSql = "select a.* from z_target a,z_task b "
					+ " where b.property = 'target' "
					+ " and a.taskid = b.taskid " + " and a.projectid = ?"
					+ " and b.projectid = ?" + " order by b.orderid";
			ps = conn.prepareStatement(strSql);
			ps.setString(1, projectId);
			ps.setString(2, projectId);
			rs = ps.executeQuery();

			list = new ArrayList();
			while (rs.next()) {
				String taskId = rs.getString("taskid");
				Set procedureSet = new ProcedureService(conn, projectId)
						.getProceduresByTaskId(taskId);
				target = new Target();

				target.setAutoID(rs.getString("AutoID"));
				target.setProjectID(rs.getString("ProjectID"));
				target.setTaskId(rs.getString("TaskID"));
				target.setExecuteIt(rs.getString("ExecuteIt"));
				target.setState(rs.getString("State"));
				target.setDefineId(rs.getString("DefineID"));
				target.setAuditTarget(rs.getString("AuditTarget"));
				target.setCorrelationExeProcedure(rs
						.getString("CorrelationExeProcedure"));
				target.setRemark(rs.getString("Remark"));
				target.setCognizance(rs.getString("cognizance"));
				target.setProperty(rs.getString("property"));
				target.setAuditProcedures(procedureSet);

				list.add(target);
			}

			return list;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return null;
	}

	/**
	 * 根据parentTaskId返回环节下的审计目标和目标下的审计程序个数,风险导向审计环节
	 * @param parentTaskId
	 * @return
	 */
	public List getTargetListByParentTaskId(String parentTaskId) {
		Target target = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List list = null;

		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			if ("0".equals(parentTaskId)) {
				String strSql = " select a.*,c.proCount from "
						+ " (select a.*,b.orderid "
						+ " 	from z_target a,z_task b "
						+ " 	where b.property = 'target'  "
						+ " 	and a.projectid = ? " + " 	and b.projectid = ? "
						+ " 	and a.taskid = b.taskid) a " + " left join  "
						+ " (select count(taskid) as proCount,taskId "
						+ " 	from z_procedure  " + " 	group by taskid ) c "
						+ " on c.taskId like concat('~',a.taskId,'~') "
						+ " order by a.orderid";

				ps = conn.prepareStatement(strSql);
				ps.setString(1, projectId);
				ps.setString(2, projectId);
			} else {

				String fullpath = new TaskService(conn, this.projectId)
						.getFullPath(parentTaskId);
				String strSql = " select a.*,c.proCount from "
						+ " (select a.*,b.orderid "
						+ " 	from z_target a,z_task b "
						+ " 	where b.property = 'target'  "
						+ " 	and b.FullPath like ? " + " 	and a.projectid = ? "
						+ " 	and b.projectid = ? "
						+ " 	and a.taskid = b.taskid) a " + " left join  "
						+ " (select count(taskid) as proCount,taskId "
						+ " 	from z_procedure  " + " 	group by taskid ) c "
						+ " on c.taskId like concat('~',a.taskId,'~') "
						+ " order by a.orderid";
				ps = conn.prepareStatement(strSql);
				ps.setString(1, fullpath + "%");
				ps.setString(2, projectId);
				ps.setString(3, projectId);
			}

			rs = ps.executeQuery();

			list = new ArrayList();
			while (rs.next()) {

				target = new Target();

				target.setAutoID(rs.getString("AutoID"));
				target.setProjectID(rs.getString("ProjectID"));
				target.setTaskId(rs.getString("TaskID"));
				target.setExecuteIt(rs.getString("ExecuteIt"));
				target.setState(rs.getString("State"));
				target.setDefineId(rs.getString("DefineID"));
				target.setAuditTarget(rs.getString("AuditTarget"));
				target.setCorrelationExeProcedure(rs
						.getString("CorrelationExeProcedure"));
				target.setRemark(rs.getString("Remark"));
				target.setProCount(rs.getInt("proCount"));
				target.setCognizance(rs.getString("cognizance"));
				target.setProperty(rs.getString("property"));

				list.add(target);
			}

			return list;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return null;
	}

	/**
	 * 根据taskId返回环节下的审计目标和程序,风险导向审计环节
	 * @param parentTaskId
	 * @return
	 */
	public List getTargetAndProListByTaskId(String parentTaskId) {
		Target target = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List list = null;

		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			String strSql = " select a.* " + " from z_target a,z_task b "
					+ " where b.property = 'target'  "
					+ " and (b.parentTaskId = ? or a.taskId=?)"
					+ " and a.projectid = ? " + " and b.projectid = ? "
					+ " and a.taskid = b.taskid " + " order by b.orderid";
			ps = conn.prepareStatement(strSql);
			ps.setString(1, parentTaskId);
			ps.setString(2, parentTaskId);
			ps.setString(3, projectId);
			ps.setString(4, projectId);

			rs = ps.executeQuery();

			list = new ArrayList();
			ProcedureService procedureService = new ProcedureService(conn,
					this.projectId);
			while (rs.next()) {

				target = new Target();

				target.setAutoID(rs.getString("AutoID"));
				target.setProjectID(rs.getString("ProjectID"));
				target.setTaskId(rs.getString("TaskID"));
				target.setExecuteIt(rs.getString("ExecuteIt"));
				target.setState(rs.getString("State"));
				target.setDefineId(rs.getString("DefineID"));
				target.setAuditTarget(rs.getString("AuditTarget"));
				target.setCorrelationExeProcedure(rs
						.getString("CorrelationExeProcedure"));
				target.setRemark(rs.getString("Remark"));
				target.setCognizance(rs.getString("cognizance"));
				target.setProperty(rs.getString("property"));

				target.setAuditProcedures(procedureService
						.getProceduresByTaskId(rs.getString("TaskID")));
				list.add(target);
			}

			return list;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return null;
	}

	/**
	 * 根据taskId返回审计目标
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public List getTargetListByTaskId(String taskId) throws Exception {
		Target target = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List targetList = null;

		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			String strSql = " Select * From z_Target "
					+ " Where ProjectId =  ? "
					+ " And TaskID = ? order by defineid ";
			ps = conn.prepareStatement(strSql);
			ps.setString(1, projectId);
			ps.setString(2, taskId);
			rs = ps.executeQuery();

			targetList = new ArrayList();
			ProcedureService procedureService = new ProcedureService(conn,
					projectId);
			while (rs.next()) {
				target = new Target();

				target.setAutoID(rs.getString("AutoID"));
				target.setProjectID(rs.getString("ProjectID"));
				target.setTaskId(rs.getString("TaskID"));
				target.setExecuteIt(rs.getString("ExecuteIt"));
				target.setState(rs.getString("State"));
				target.setDefineId(rs.getString("DefineID"));
				target.setAuditTarget(rs.getString("AuditTarget"));
				target.setCorrelationExeProcedure(rs
						.getString("CorrelationExeProcedure"));
				target.setRemark(rs.getString("Remark"));
				target.setCognizance(rs.getString("cognizance"));
				target.setProperty(rs.getString("property"));

				target.setAuditProcedures(procedureService
						.getProceduresByTaskId(rs.getString("TaskID")));
				targetList.add(target);
			}

			return targetList;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 返回审计目标
	 * @param id
	 * @return
	 */
	public Target getTargetByTaskId(String taskId) {
		Target target = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			String strSql = "select * from z_target where taskId = ? and projectid = ?";
			ps = conn.prepareStatement(strSql);
			ps.setString(1, taskId);
			ps.setString(2, projectId);
			rs = ps.executeQuery();

			if (rs.next()) {
				target = new Target();

				target.setAutoID(rs.getString("AutoID"));
				target.setProjectID(rs.getString("ProjectID"));
				target.setTaskId(rs.getString("TaskID"));
				target.setExecuteIt(rs.getString("ExecuteIt"));
				target.setState(rs.getString("State"));
				target.setDefineId(rs.getString("DefineID"));
				target.setAuditTarget(rs.getString("AuditTarget"));
				target.setCorrelationExeProcedure(rs
						.getString("CorrelationExeProcedure"));
				target.setRemark(rs.getString("Remark"));
				target.setCognizance(rs.getString("cognizance"));
				target.setProperty(rs.getString("property"));
			}

			return target;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return null;
	}

	/**
	 * 增加审计目标
	 * @param target
	 */
	public void addTarget(Target target) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			String strSql = "insert into z_target (ProjectID, TaskID, ExecuteIt, State, DefineID, AuditTarget, CorrelationExeProcedure, Remark,cognizance,property) "
					+ " values(?,?,?,?,?,?,?,?,?,?)";
			ps = conn.prepareStatement(strSql);

			ps.setString(1, this.projectId);
			ps.setString(2, target.getTaskId());
			ps.setString(3, target.getExecuteIt());
			ps.setString(4, target.getState());
			ps.setString(5, target.getDefineId());
			ps.setString(6, target.getAuditTarget());
			ps.setString(7, target.getCorrelationExeProcedure());
			ps.setString(8, target.getRemark());
			ps.setString(9, target.getCognizance());
			ps.setString(10, target.getProperty());

			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 更新审计目标
	 * @param target
	 */
	public int updateTarget(Target target) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			String strSql = "update z_target set ExecuteIt=? , State=?, DefineID=?, AuditTarget=?, CorrelationExeProcedure=?, Remark=?,cognizance=?,property=? "
					+ " where taskId = ? and projectId = ?";
			ps = conn.prepareStatement(strSql);

			ps.setString(1, target.getExecuteIt());
			ps.setString(2, target.getState());
			ps.setString(3, target.getDefineId());
			ps.setString(4, target.getAuditTarget());
			ps.setString(5, target.getCorrelationExeProcedure());
			ps.setString(6, target.getRemark());

			ps.setString(7, target.getTaskId());
			ps.setString(8, target.getCognizance());
			ps.setString(9, target.getProperty());

			ps.setString(10, projectId);

			return ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 更新审计目标单个属性
	 * @param taskId
	 * @param att
	 * @param val
	 */
	public void updateTarget(String taskId, String att, String val) {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			String strSql = "update z_target set " + att + " = ? "
					+ " where taskId = ? and projectId = ?";
			ps = conn.prepareStatement(strSql);

			ps.setString(1, val);
			ps.setString(2, taskId);
			ps.setString(3, projectId);

			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 更新审计目标单个属性
	 * @param taskId
	 * @param att
	 * @param val
	 */
	public int updateTargetByAutoId(String autoId, String att, String val)
			throws Exception {
		PreparedStatement ps = null;

		try {

			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			String strSql = "update z_target set " + att + " = ? "
					+ " where autoId = ? ";
			ps = conn.prepareStatement(strSql);

			ps.setString(1, val);
			ps.setString(2, autoId);

			return ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 根据taskid删除审计目标,和目标下的程序
	 * @param taskid
	 */
	public int removeTargetByTaskId(String taskId) throws Exception {
		PreparedStatement ps = null;

		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			//将目标下的所有程序都删除掉
			new ProcedureService(conn, projectId)
					.removeProcedureByTaskId(taskId);

			String strSql = "delete from z_target where taskid = ? and projectid = ?";
			ps = conn.prepareStatement(strSql);
			ps.setString(1, taskId);
			ps.setString(2, projectId);
			return ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 根据autoId删除审计目标
	 * @param taskid
	 */
	public int removeTargetByAutoId(String autoId) throws Exception {
		PreparedStatement ps = null;

		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			String strSql = "delete from z_target where autoId = ? ";
			ps = conn.prepareStatement(strSql);
			ps.setString(1, autoId);
			return ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 根据taskid判断环节是否有孩子
	 * @param taskId
	 * @param Property
	 * @return
	 */
	public boolean hasChildren(String taskId, String Property) {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			String strSql = "select 1 from z_task where ParentTaskID = ? and projectid = ? and Property = ?";
			ps = conn.prepareStatement(strSql);
			ps.setString(1, taskId);
			ps.setString(2, projectId);
			ps.setString(3, Property);
			rs = ps.executeQuery();

			if (rs.next()) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return false;
	}

	/**
	 * 根据taskid判断环节是否有孩子
	 * @param taskId
	 * @return
	 */
	public boolean hasChildren(String taskId) {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			String strSql = "select 1 from z_task where ParentTaskID = ? and projectid = ? ";
			ps = conn.prepareStatement(strSql);
			ps.setString(1, taskId);
			ps.setString(2, projectId);
			rs = ps.executeQuery();

			if (rs.next()) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return false;
	}

	/**
	 * 新建表
	 * @throws MatechException
	 */
	public void createTable() throws MatechException {
		new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
		dropTable();
		String sql = " create table " + this.tempTableName + " like z_target ";
		PreparedStatement ps = null;
		try {

			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

	}

	/**
	 * 删除临时表
	 * @throws MatechException
	 */
	public void dropTable() throws MatechException {
		new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
		String sql = "DROP TABLE IF EXISTS  " + this.tempTableName;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

	}

	/**
	 * 插入数据
	 * @throws MatechException
	 */
	public void insertData2() throws MatechException {
		new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;

		try {
			String sql = " select DefineID, AuditTarget, cognizance,state, CorrelationExeProcedure, projectId, taskId "
					+ " from " + this.tempTableName;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			String sql2 = " insert into z_target(DefineID, AuditTarget, cognizance,state, CorrelationExeProcedure, projectId, taskId, autoId ) "
					+ " values(?,?,?,?, ?,?,?,?) ";
			ps2 = conn.prepareStatement(sql2);

			while (rs.next()) {
				ps2.setString(1, rs.getString(1));
				ps2.setString(2, rs.getString(2));
				ps2.setString(3, rs.getString(3));
				ps2.setString(4, rs.getString(4));
				ps2.setString(5, rs.getString(5));
				ps2.setString(6, rs.getString(6));
				ps2.setString(7, rs.getString(7));
				ps2.setString(8, getMaxAutoId());
				ps2.executeUpdate();
			}

			dropTable();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(ps2);
		}
	}

	public void insertData() throws Exception {
		new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
		PreparedStatement ps = null;
		try {
			String sql = "insert into z_target(DefineID, AuditTarget, cognizance,state, CorrelationExeProcedure, projectId, taskId) "
					+ " select DefineID, AuditTarget, cognizance,state, CorrelationExeProcedure, projectId, taskId "
					+ " from " + this.tempTableName;
			ps = conn.prepareStatement(sql);
			ps.execute();
			dropTable();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 获得最大AUTOID
	 * @return
	 * @throws Exception
	 */
	public String getMaxAutoId() throws Exception {
		new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

		String sql = " select ifnull(max(autoId),0) + 1 " + " from z_target "
				+ " where projectId=? ";
		Object[] objects = { this.projectId };

		return new DbUtil(conn).queryForString(sql, objects);
	}

	public String getTempTableName() {
		return tempTableName;
	}

	public void setTempTableName(String tempTableName) {
		this.tempTableName = tempTableName;
	}

	public static void main(String[] args) throws Exception {
		Connection conn = new DBConnect().getConnect("");
		try {
			TargetService targetService = new TargetService(conn, "2007776");
			System.out.println(targetService.getTargetListByParentTaskId("0")
					.size());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
	}
}
