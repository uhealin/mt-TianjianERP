package com.matech.audit.service.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.project.model.GroupProject;
import com.matech.framework.pub.autocode.DELAutocode;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.UTILString;

public class GroupProjectService {
	private Connection conn = null;

	/**
	 * 构造方法
	 * @param conn
	 */
	public GroupProjectService(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 更新集团项目记录
	 * @param groupProject
	 * @return
	 * @throws Exception
	 */
	public boolean updateGroupProject(GroupProject groupProject) throws Exception {
		DbUtil dbUtil = new DbUtil(conn);

		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(" update z_groupproject set ");
		stringBuffer.append(" parentProjectId=?, AuditDept=?, CustomerId=?,AccPackageID=?,AuditType=?,");
		stringBuffer.append(" AuditPara=?,ProjectName=?,ProjectCreated=?,ProjectEnd=?,State=?,AuditPeople=?,Property=?, ");
		stringBuffer.append(" AuditTimeBegin=?,AuditTimeEnd=?,RealStartDate=?, RealEndDate=?, standbyname=? ");
		stringBuffer.append(" where ProjectID=? ");

		Object[] args = new Object[] {
				groupProject.getParentProjectId(),
				groupProject.getAuditDept(),
				groupProject.getCustomerId(),
				groupProject.getAccPackageId(),
				groupProject.getAuditType(),

				groupProject.getAuditPara(),
				groupProject.getProjectName(),
				groupProject.getProjectCreated(),
				groupProject.getProjectEnd(),
				groupProject.getState(),
				groupProject.getAuditPeople(),
				groupProject.getProperty(),

				groupProject.getAuditTimeBegin(),
				groupProject.getAuditTimeEnd(),
				groupProject.getRealStartDate(),
				groupProject.getRealEndDate(),
				groupProject.getStandbyName(),

				groupProject.getProjectId()
		};


		return dbUtil.executeUpdate(stringBuffer.toString(), args) > 0;

	}

	/**
	 * 保存集团项目记录
	 * @param groupProject
	 * @return
	 * @throws Exception
	 */
	public boolean saveGroupProject(GroupProject groupProject) throws Exception {
		DbUtil dbUtil = new DbUtil(conn);

		String projectId = getProjectId(groupProject.getAuditType());
		groupProject.setProjectId(projectId);

		if(groupProject.getParentProjectId() == null || "".equals(groupProject.getParentProjectId())) {
			groupProject.setParentProjectId("0");
		}

		if(groupProject.getState() == null || "".equals(groupProject.getState())) {
			groupProject.setState("1");
		}

		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(" insert into z_groupproject( ");
		stringBuffer.append(" parentProjectId, ProjectID, AuditDept, CustomerId,AccPackageID,AuditType,");
		stringBuffer.append(" AuditPara,ProjectName,ProjectCreated,ProjectEnd,State,AuditPeople,");
		stringBuffer.append(" Property,AuditTimeBegin,AuditTimeEnd,RealStartDate,RealEndDate,standbyname) ");
		stringBuffer.append(" values(?,?,?,?,?,?, ?,?,?,?,?,?, ?,?,?,?,?,?) ");

		Object[] args = new Object[] {
				groupProject.getParentProjectId(),
				groupProject.getProjectId(),
				groupProject.getAuditDept(),
				groupProject.getCustomerId(),
				groupProject.getAccPackageId(),
				groupProject.getAuditType(),

				groupProject.getAuditPara(),
				groupProject.getProjectName(),
				groupProject.getProjectCreated(),
				groupProject.getProjectEnd(),
				groupProject.getState(),
				groupProject.getAuditPeople(),

				groupProject.getProperty(),
				groupProject.getAuditTimeBegin(),
				groupProject.getAuditTimeEnd(),
				groupProject.getRealStartDate(),
				groupProject.getRealEndDate(),
				groupProject.getStandbyName(),
		};


		return dbUtil.executeUpdate(stringBuffer.toString(), args) > 0;
	}

	/**
	 * 删除集团项目记录
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public boolean removeGroupProject(String projectId) throws Exception {

		DbUtil dbUtil = new DbUtil(conn);

		String sql = " select count(1) "
					+ " from z_groupproject "
					+ " where parentprojectId=? ";

		Object[] args = new Object[] {
				projectId
		};

		int childs = dbUtil.queryForInt(sql, args);
		if(childs > 0) {
			throw new Exception("该项目下还有[" + childs + "]个子项目,请先删除子项目!!!");
		}

		sql = " delete from z_groupproject where projectId=? ";

		if(dbUtil.executeUpdate(sql, args) > 0) {
			new GroupProjectGraphics(conn,projectId).removeGraphics();
		}

		return true;
	}

	/**
	 * 获得集团项目记录
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public GroupProject getGroupProject(String projectId) throws Exception {
		GroupProject groupProject = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append(" select ProjectID,parentProjectId, AuditDept, CustomerId,AccPackageID, ");
			stringBuffer.append(" AuditType,AuditPara,ProjectName,ProjectCreated,ProjectEnd,State, ");
			stringBuffer.append(" AuditPeople,Property,AuditTimeBegin,AuditTimeEnd,RealStartDate,RealEndDate, ");
			stringBuffer.append(" standbyname,systemid ");
			stringBuffer.append(" from z_groupproject ");
			stringBuffer.append(" where ProjectID=? ");

			ps = conn.prepareStatement(stringBuffer.toString());
			ps.setString(1, projectId);

			rs = ps.executeQuery();

			if(rs.next()) {
				groupProject = new GroupProject();
				groupProject.setProjectId(rs.getString(1));
				groupProject.setParentProjectId(rs.getString(2));
				groupProject.setAuditDept(rs.getString(3));
				groupProject.setCustomerId(rs.getString(4));
				groupProject.setAccPackageId(rs.getString(5));

				groupProject.setAuditType(rs.getString(6));
				groupProject.setAuditPara(rs.getString(7));
				groupProject.setProjectName(rs.getString(8));
				groupProject.setProjectCreated(rs.getString(9));
				groupProject.setProjectEnd(rs.getString(10));
				groupProject.setState(rs.getString(11));

				groupProject.setAuditPeople(rs.getString(12));
				groupProject.setProperty(rs.getString(13));
				groupProject.setAuditTimeBegin(rs.getString(14));
				groupProject.setAuditTimeEnd(rs.getString(15));
				groupProject.setRealStartDate(rs.getString(16));
				groupProject.setRealEndDate(rs.getString(17));

				groupProject.setStandbyName(rs.getString(18));
				groupProject.setSystemId(rs.getString(19));
			}

		} catch (Exception e) {
			System.out.println("获得集团项目记录失败：" + e.getMessage());
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return groupProject;
	}

	/**
	 * 获得下级项目列表
	 * @param parentDepartId
	 * @return
	 * @throws Exception
	 */
	public List getGroupProjectList(String parentProjectId) throws Exception {
		GroupProject groupProject = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List list = new ArrayList();

		try {
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append(" select ProjectID,parentProjectId, AuditDept, CustomerId,AccPackageID, ");
			stringBuffer.append(" AuditType,AuditPara,ProjectName,ProjectCreated,ProjectEnd,State, ");
			stringBuffer.append(" AuditPeople,Property,AuditTimeBegin,AuditTimeEnd,RealStartDate,RealEndDate, ");
			stringBuffer.append(" standbyname,systemid ");
			stringBuffer.append(" from z_groupproject ");
			stringBuffer.append(" where parentProjectId=? ");

			ps = conn.prepareStatement(stringBuffer.toString());
			ps.setString(1, parentProjectId);

			rs = ps.executeQuery();

			while(rs.next()) {
				groupProject = new GroupProject();
				groupProject.setProjectId(rs.getString(1));
				groupProject.setParentProjectId(rs.getString(2));
				groupProject.setAuditDept(rs.getString(3));
				groupProject.setCustomerId(rs.getString(4));
				groupProject.setAccPackageId(rs.getString(5));

				groupProject.setAuditType(rs.getString(6));
				groupProject.setAuditPara(rs.getString(7));
				groupProject.setProjectName(rs.getString(8));
				groupProject.setProjectCreated(rs.getString(9));
				groupProject.setProjectEnd(rs.getString(10));
				groupProject.setState(rs.getString(11));

				groupProject.setAuditPeople(rs.getString(12));
				groupProject.setProperty(rs.getString(13));
				groupProject.setAuditTimeBegin(rs.getString(14));
				groupProject.setAuditTimeEnd(rs.getString(15));
				groupProject.setRealStartDate(rs.getString(16));
				groupProject.setRealEndDate(rs.getString(17));

				groupProject.setStandbyName(rs.getString(18));
				groupProject.setSystemId(rs.getString(19));

				list.add(groupProject);
			}

		} catch (Exception e) {
			System.out.println("获得集团项目列表失败：" + e.getMessage());
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return list;
	}

	/**
	 * 获得项目编号
	 * @param typeId
	 * @return
	 * @throws Exception
	 */
	private String getProjectId(String typeId) throws Exception {
		DELAutocode t = new DELAutocode();
		String id = UTILString.nCharToString('0', 2 - typeId.length())
				  + typeId;
		String[] temp = new String[] { id };
		return t.getAutoCode("XMBH", "", temp);
	}

	/**
	 * 计算出底稿全路径
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	private String calFullPath(String projectId) throws Exception {
		String fullPath = projectId + "|";
		String parentProjectId = projectId;
		while (true) {
			parentProjectId = getParentProjectId(parentProjectId);
			if (parentProjectId.equals("0")) {
				break;
			} else {
				fullPath = parentProjectId + "|" + fullPath;
			}
		}
		return fullPath;
	}

	/**
	 * 根据项目id获得父项目的projectId
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public String getParentProjectId(String projectId) throws Exception {
		String parentProjectId = "";
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			String strSql = "select parentProjectId "
							+ " from z_groupproject "
							+ " where projectId = ?";
			ps = conn.prepareStatement(strSql);
			ps.setString(1, projectId);

			rs = ps.executeQuery();

			if (rs.next()) {
				parentProjectId = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return parentProjectId;
	}

	/**
	 * @throws Exception
	 */
	public void repairFullPath() throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {

			String sql = "select projectId from z_groupproject order by projectId ";

			ps = conn.prepareStatement(sql);

			rs = ps.executeQuery();

			while (rs.next()) {
				String projectId = rs.getString(1);
				String fullPath = calFullPath(projectId);
				int level = fullPath.split("\\|").length - 1;
				sql = "update z_groupproject set "
						+ " fullpath= '" + fullPath + "', "
						+ " Level0= " + level + " "
						+ " where projectid = '" + projectId + "'";

				stmt = conn.createStatement();
				//System.out.println(sql);
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

	public static void main(String[] args) {
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");

			new GroupProjectService(conn).repairFullPath();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
	}
}
