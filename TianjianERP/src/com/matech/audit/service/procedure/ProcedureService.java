package com.matech.audit.service.procedure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.audit.service.procedure.model.Procedure;
import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.user.UserService;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;

/**
 * <p>Title: 审计程序类</p>
 * <p>Description: 更新,删除,查询审计程序等</p>
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
public class ProcedureService {
	private Connection conn = null;

	private String projectId = null;

	private String tempTableName = "tt_z_procedure";

	private boolean isLib = false;

	public final static String PROCEDURE_STATE_COMPLETED = "已完成";
	public final static String PROCEDURE_STATE_STANDBY = "备用";
	public final static String PROCEDURE_STATE_NOT_COMPLETE = "未完成";
	public final static String PROCEDURE_STATE_NOT_APPLY = "不适用";

	/**
	 * 构造方法,初始化数据库连接和项目编号
	 * @param conn	数据库连接
	 * @param projectId	项目编号
	 * @throws Exception
	 */
	public ProcedureService(Connection conn, String projectId) throws Exception {
		DbUtil.checkConn(conn);
		this.conn = conn;

		if("".equals(projectId) || projectId == null) {
			throw new Exception("项目Id不能为空!");
		}

		this.projectId = projectId;
	}

	/**
	 * 根据autoid返回一个审计程序
	 * @param id
	 * @return
	 */
	public Procedure getProcedureById(String id) throws Exception {
		Procedure procedure = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			String strSql = "select * from z_procedure where autoid = ? and projectId=? ";
			ps = conn.prepareStatement(strSql);
			ps.setString(1, id);
			ps.setString(2, this.projectId);
			rs = ps.executeQuery();

			if(rs.next()) {
				procedure = new Procedure();

				procedure.setAutoId(rs.getString("AutoID"));
				procedure.setProjectId(rs.getString("ProjectID"));
				procedure.setTaskId(rs.getString("TaskID"));
				procedure.setState(rs.getString("State"));
				procedure.setDefineId(rs.getString("DefineID"));
				procedure.setAuditProcedure(rs.getString("AuditProcedure"));
				procedure.setManuScript(rs.getString("Manuscript"));
				procedure.setExecutor(rs.getString("Executor"));
				procedure.setRemark(rs.getString("Remark"));
				procedure.setCognizance(rs.getString("cognizance"));
				procedure.setFullpath(rs.getString("fullpath"));
				procedure.setLevel0(rs.getInt("level0"));
				procedure.setParentId(rs.getString("parentId"));
				procedure.setProperty(rs.getString("property"));
				procedure.setApprovalMan(rs.getString("approvalMan")) ;
				procedure.setNotApplicableMan(rs.getString("notApplicableMan")) ;
				procedure.setApprovalDate(rs.getString("approvalDate")) ;
				procedure.setNotApplicableDate(rs.getString("notApplicableDate")) ;
			}

			return procedure;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 新增审计程序
	 * @param procedure
	 * @return autoId
	 */
	public int addProcedure(Procedure procedure) throws Exception {
		PreparedStatement ps = null;
		Statement stmt = null;
		ResultSet rs = null;
		int autoId = 0;
		try {

			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

	        stmt = conn.createStatement();
	        rs = stmt.executeQuery("select ifnull(max(autoid),0) from z_procedure where projectId='" + this.projectId + "'");
			if (rs.next()) {
				autoId = rs.getInt(1) + 1;
			}

			String strSql = "insert into z_procedure(ProjectID,TaskID,State,DefineID,AuditProcedure,"
						  + " Manuscript,Executor,Remark,AutoID,cognizance,parentId, fullpath, level0) "
						  + " values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
			ps = conn.prepareStatement(strSql);

			ps.setString(1,this.projectId);
			ps.setString(2,procedure.getTaskId());
			ps.setString(3,procedure.getState());
			ps.setString(4,procedure.getDefineId());
			ps.setString(5,procedure.getAuditProcedure());
			ps.setString(6,procedure.getManuScript());
			ps.setString(7,procedure.getExecutor());
			ps.setString(8,procedure.getRemark());
			ps.setInt(9,autoId);
			ps.setString(10,procedure.getCognizance());
			ps.setString(11, procedure.getParentId());
			ps.setString(12, procedure.getFullpath());
			ps.setInt(13, procedure.getLevel0());

			if(ps.executeUpdate() > 0) {
				return autoId;
			} else {
				return 0;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(stmt);
			DbUtil.close(ps);
		}
	}

	/**
	 * 更新审计程序
	 * @param procedure
	 */
	public void updateProcedure(Procedure procedure) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			String strSql = "update z_procedure set ProjectID = ?,TaskID = ?,State = ?,DefineID =?,AuditProcedure = ?,Manuscript = ?,Executor = ?,Remark = ? where autoid = ? and projectId=? ";
			ps = conn.prepareStatement(strSql);

			ps.setString(1,procedure.getProjectId());
			ps.setString(2,procedure.getTaskId());
			ps.setString(3,procedure.getState());
			ps.setString(4,procedure.getDefineId());
			ps.setString(5,procedure.getAuditProcedure());
			ps.setString(6,procedure.getManuScript());
			ps.setString(7,procedure.getExecutor());
			ps.setString(8,procedure.getRemark());
			ps.setString(9,procedure.getAutoId());
			ps.setString(10,this.projectId);

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
	 * 根据autoid删除审计程序,如果程序是引用的，则只会删除引用
	 * @param autoId
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public int removeProcedureByAutoIdAndTaskId(String autoId,String taskId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

	        String refTaskid = this.getProcedureById(autoId).getTaskId();
	        String[] taskids = refTaskid.split("~~");

	        String strSql;

	        if(taskids.length > 1) {
	        	refTaskid = refTaskid.replaceAll("~" + taskId + "~", "");
	        	strSql = "update z_procedure set taskid=? where autoid = ? and projectId=? ";
	        	ps = conn.prepareStatement(strSql);
	        	ps.setString(1,refTaskid);
	        	ps.setString(2,autoId);
	        	ps.setString(3,this.projectId);
	        } else {
	        	strSql = "delete from z_procedure where autoid = ? and projectId=? ";
	        	ps = conn.prepareStatement(strSql);
	        	ps.setString(1,autoId);
	        	ps.setString(2,this.projectId);
	        }

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
	 * 根据autoid删除审计程序
	 * @param autoid
	 */
	public int removeProcedureByAutoId(String autoid) throws Exception {
		PreparedStatement ps = null;

		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

	        String strSql = "delete from z_procedure where projectId=? and autoid = ?";
			ps = conn.prepareStatement(strSql);
			ps.setString(1,this.projectId);
			ps.setString(2,autoid);
			
			ps.executeUpdate();

			return 1;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 根据taskid删除多个审计程序
	 * @param autoid
	 */
	public void removeProcedureByTaskId(String taskId) throws Exception {

		Set procedureSet = this.getProceduresByTaskId(taskId);
		Iterator it = procedureSet.iterator();

		while(it.hasNext()) {
			Procedure auditProcedureTable = (Procedure)it.next();
			this.removeProcedureByAutoIdAndTaskId(auditProcedureTable.getAutoId(), taskId);
		}
	}
	
	/**
	 * 根据taskid返回多个审计程序
	 * @param taskId
	 * @return
	 */
	public List getProcedureList(String taskId) throws Exception {
		Procedure procedure = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List procedureList = new ArrayList();

		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			String strSql = " select * from z_procedure "
							+ " where projectid=? "
							+ " and taskid=? "
							+ " order by DefineID ";
			ps = conn.prepareStatement(strSql);
			ps.setString(1, projectId);
			ps.setString(2, taskId);
			rs = ps.executeQuery();
			
			String manuscript = "";
			
			while(rs.next()) {
				procedure = new Procedure();

				procedure.setAutoId(rs.getString("AutoID"));
				procedure.setProjectId(rs.getString("ProjectID"));
				procedure.setTaskId(rs.getString("TaskID"));
				procedure.setState(rs.getString("State"));
				procedure.setDefineId(rs.getString("DefineID"));
				procedure.setAuditProcedure(rs.getString("AuditProcedure"));
				procedure.setExecutor(rs.getString("Executor"));
				procedure.setRemark(rs.getString("Remark"));
				procedure.setCognizance(rs.getString("cognizance"));
				manuscript = rs.getString("Manuscript") == null ? "" : rs.getString("Manuscript");
				procedure.setManuScript(manuscript);
				procedure.setManuLinks(this.getLinksByTaskCodes(manuscript));
		

				procedureList.add(procedure);
			}

			return procedureList;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 根据taskid返回多个审计程序
	 * @param taskId
	 * @return
	 */
	public Set getProceduresByTaskId(String taskId) throws Exception {
		Procedure procedure = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Set procedureSet = null;

		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			String strSql = "select * from z_procedure where taskid like ? and projectid = ? order by DefineID ";
			ps = conn.prepareStatement(strSql);
			ps.setString(1, "%~" + taskId + "~%");
			ps.setString(2, projectId);
			rs = ps.executeQuery();

			String manuscript = "";
			procedureSet = new LinkedHashSet();
			while(rs.next()) {
				procedure = new Procedure();

				procedure.setAutoId(rs.getString("AutoID"));
				procedure.setProjectId(rs.getString("ProjectID"));
				procedure.setTaskId(rs.getString("TaskID"));
				procedure.setState(rs.getString("State"));
				procedure.setDefineId(rs.getString("DefineID"));
				procedure.setAuditProcedure(rs.getString("AuditProcedure"));
				procedure.setExecutor(rs.getString("Executor"));
				procedure.setRemark(rs.getString("Remark"));
				procedure.setCognizance(rs.getString("cognizance"));
				manuscript = rs.getString("Manuscript") == null ? "" : rs.getString("Manuscript");
				procedure.setManuScript(manuscript);
				procedure.setManuLinks(this.getLinksByTaskCodes(manuscript));

				procedureSet.add(procedure);
			}

			return procedureSet;

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
	public int updateProcedureByAutoId(String autoId, String att, String val) throws Exception {
		PreparedStatement ps = null;

		try {

			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			String strSql = "update z_procedure set " + att + " = ? "
						  + " where autoId = ? and projectId=? ";
			ps = conn.prepareStatement(strSql);

			ps.setString(1,val);
			ps.setString(2,autoId);
			ps.setString(3,this.projectId);

			return ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 更新审计目标单个属性
	 * @param taskId
	 * @param att
	 * @param val
	 */
	public int updateProcedureState(String autoId, String state, String userId) throws Exception {
		PreparedStatement ps = null;

		try {

			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			String strSql = "update z_procedure set state=?,notApplicableMan=?,notApplicableDate=now() "
						  + " where autoId = ? and projectId=? ";
			ps = conn.prepareStatement(strSql);

			ps.setString(1,state);
			ps.setString(2,userId);
			ps.setString(3,autoId);
			ps.setString(4,this.projectId);

			return ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 更新多个审计程序属性
	 * @param taskId
	 * @param att
	 * @param val
	 */
	public void batchUpdateProcedureState(String autoIds, String state, String userId, String parentId, String parentTaskId) throws Exception {
		PreparedStatement ps = null;

		String[] autoId = autoIds.split(",");
		try {

			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			Procedure parentProcedure = getProcedureById(parentId);

			String strSql = "update z_procedure set "
						  + " state=?,notApplicableMan=?,parentId=?,taskId=?,fullpath=?,level0=?,notApplicableDate=now() "
						  + " where autoId = ? and projectId=? ";
			ps = conn.prepareStatement(strSql);

			ps.setString(1,state);
			ps.setString(2,userId);
			ps.setString(3,parentId);
			ps.setString(4,parentTaskId);
			ps.setString(8,this.projectId);

			//批量更新
			for(int i=0; i < autoId.length; i++) {
				if("".equals(autoId[i])) {
					continue;
				}

				if(!"0".equals(parentId)) {
					ps.setString(5,parentProcedure.getFullpath() + "|" + parentProcedure.getAutoId());
					ps.setInt(6,parentProcedure.getLevel0() + 1);
				} else {
					ps.setString(5,"0");
					ps.setInt(6,0);
				}

				ps.setString(7, autoId[i]);
				ps.executeUpdate();
			}


		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 根据taskId获得审计程序
	 * @param taskId
	 * @return
	 */
	public void getProcedureListByTaskId(List list, String taskId, String parentTaskId, boolean isMust) throws Exception {
		Procedure procedure = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			String strSql = "select * from z_procedure "
						  + " where taskid like ? "
						  + " and projectid = ? "
						  + " and parentId=? ";

			if(!isMust) {
				strSql += " and property='必做'";
			} 
			
			strSql += " order by defineid ";

			ps = conn.prepareStatement(strSql);
			ps.setString(1, taskId);
			ps.setString(2, projectId);
			ps.setString(3, parentTaskId);

			rs = ps.executeQuery();

			while(rs.next()) {
				procedure = new Procedure();

				String autoId = rs.getString("AutoID");

				procedure.setAutoId(autoId);
				procedure.setProjectId(rs.getString("ProjectID"));
				procedure.setTaskId(taskId);
				procedure.setState(rs.getString("State"));
				procedure.setDefineId(rs.getString("DefineID"));
				procedure.setAuditProcedure(rs.getString("AuditProcedure"));
				procedure.setManuScript(rs.getString("Manuscript"));
				procedure.setExecutor(rs.getString("Executor"));
				procedure.setRemark(rs.getString("Remark"));
				procedure.setCognizance(rs.getString("cognizance"));
				procedure.setManuLinks(getLinksByTaskCodes(rs.getString("Manuscript") ));
				procedure.setParentId(rs.getString("parentId"));
				procedure.setLevel0(rs.getInt("level0"));
				procedure.setFullpath(rs.getString("fullpath"));
				procedure.setProperty(rs.getString("property"));

				procedure.setReDefineid(rs.getString("reDefineId"));
				procedure.setReAuditProcedure(rs.getString("reAuditProcedure"));
				procedure.setReManuscript(rs.getString("reManuscript"));
				procedure.setReCognizance(rs.getString("reCognizance"));
				procedure.setReExecutor(rs.getString("reExecutor"));
				procedure.setReManuLinks(getLinksByTaskCodes(rs.getString("reManuscript") ));
				
				list.add(procedure);

				getProcedureListByTaskId(list, taskId, autoId, isMust);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	/**
	 * 根据taskId获得审计程序
	 * @param taskId
	 * @return
	 */
	public void getProcedureListByTaskId(List list, String taskId, String parentTaskId) throws Exception {
		Procedure procedure = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			String strSql = "select * from z_procedure "
						  + " where taskid like ? "
						  + " and projectid = ? "
						  + " and parentId=? ";

			//if(!this.isLib()) {
				strSql += " and state<>'备用'";
			//}

			strSql += " order by DefineID ";

			ps = conn.prepareStatement(strSql);
			ps.setString(1, taskId);
			ps.setString(2, projectId);
			ps.setString(3, parentTaskId);

			rs = ps.executeQuery();

			while(rs.next()) {
				procedure = new Procedure();

				String autoId = rs.getString("AutoID");

				procedure.setAutoId(autoId);
				procedure.setProjectId(rs.getString("ProjectID"));
				procedure.setTaskId(taskId);
				procedure.setState(rs.getString("State"));
				procedure.setDefineId(rs.getString("DefineID"));
				procedure.setAuditProcedure(rs.getString("AuditProcedure"));
				procedure.setManuScript(rs.getString("Manuscript"));
				procedure.setExecutor(rs.getString("Executor"));
				procedure.setRemark(rs.getString("Remark"));
				procedure.setCognizance(rs.getString("cognizance"));
				procedure.setManuLinks(getLinksByTaskCodes(rs.getString("Manuscript") ));
				procedure.setParentId(rs.getString("parentId"));
				procedure.setLevel0(rs.getInt("level0"));
				procedure.setFullpath(rs.getString("fullpath"));
				procedure.setProperty(rs.getString("property"));

				list.add(procedure);

				getProcedureListByTaskId(list, taskId, autoId);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 返回下级程序
	 * @param taskId
	 * @param parentId
	 * @return
	 * @throws Exception
	 */
	public List getChildProcedureListByParentId(String taskId, String parentId) throws Exception {
		Procedure procedure = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List childProcedureList = null;

		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			String strSql = "select * from z_procedure "
						  + " where taskid like ? "
						  + " and projectid = ? "
						  + " and parentId=? ";


			//if(!this.isLib()) {
				strSql += " and state<>'备用' ";
			//}

			strSql += " order by DefineID ";

			ps = conn.prepareStatement(strSql);
			ps.setString(1, taskId);
			ps.setString(2, projectId);
			ps.setString(3, parentId);

			rs = ps.executeQuery();

			childProcedureList = new ArrayList();
			while(rs.next()) {
				procedure = new Procedure();

				procedure.setAutoId(rs.getString("AutoID"));
				procedure.setProjectId(rs.getString("ProjectID"));
				procedure.setTaskId(rs.getString("TaskID"));
				procedure.setState(rs.getString("State"));
				procedure.setDefineId(rs.getString("DefineID"));
				procedure.setAuditProcedure(rs.getString("AuditProcedure"));
				procedure.setManuScript(rs.getString("Manuscript"));
				procedure.setExecutor(rs.getString("Executor"));
				procedure.setRemark(rs.getString("Remark"));
				procedure.setCognizance(rs.getString("cognizance"));
				procedure.setParentId(rs.getString("parentId"));
				procedure.setLevel0(rs.getInt("level0"));
				procedure.setFullpath(rs.getString("fullpath"));
				procedure.setProperty(rs.getString("property"));
				procedure.setManuLinks(getLinksByTaskCodes(rs.getString("Manuscript") ));

				childProcedureList.add(procedure);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return childProcedureList;
	}

	/**
	 * 返回相关底稿的连接
	 * @param taskcodes
	 * @return
	 * @throws Exception
	 */
	public String getLinksByTaskCodes(String taskcodes) throws Exception {

		if(taskcodes == null){
			return "";
		}

		String[] taskcode = taskcodes.split(",");
		String links = "";
		for(int i = 0; i < taskcode.length; i++) {
			if(!"".equals(taskcode[i])) {
				links += getLinkByTaskCode(taskcode[i]) + "<br/>";
			}

		}
		return links;
	}

	/**
	 * 根据taskcode获得底稿连接
	 * @param taskcode
	 * @return
	 * @throws Exception
	 */
	public String getLinkByTaskCode(String taskcode) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;
		String link = "";
		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			String strSql = "select manuid,taskid,taskname,taskcode from z_task where taskcode = ? and projectid = ? and isleaf=1 ";
			ps = conn.prepareStatement(strSql);
			ps.setString(1, taskcode);
			ps.setString(2, projectId);
			rs = ps.executeQuery();

			if(rs.next()) {
				link = "<a href='#' onclick=\"openFileByLink('" + rs.getString(2) + "');\">" + rs.getString(4) + " " + rs.getString(3) + "</a>";
			} else {
				strSql = "select taskid,sheettaskcode,sheetname from z_sheettask where sheettaskcode = ? and projectid = ? ";
				ps = conn.prepareStatement(strSql);
				ps.setString(1, taskcode);
				ps.setString(2, projectId);
				rs = ps.executeQuery();

				if(rs.next()) {
					link = "<a href='#' onclick=\"openFileByLink2('" + rs.getString(1) + "','" + rs.getString(2) + "');\">" + rs.getString(2) + " " + rs.getString(3) + "</a>";
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return link;
	}

	/**
	 * 引用审计程序
	 * @param autoid
	 * @param taskid
	 * @throws Exception
	 */
	public void referProcedure(String[] autoids, String taskid) throws Exception {
		Procedure procedure = null;
		String refTaskId = null;

		for(int i = 0; i < autoids.length; i++) {
			//取出对应autoid的审计程序,更新taskid字段
			procedure = this.getProcedureById(autoids[i]);
			refTaskId = procedure.getTaskId() + "~" + taskid + "~";
			procedure.setTaskId(refTaskId);
			this.updateProcedure(procedure);
		}
	}

	/**
	 * 复制审计程序
	 * @param autoids
	 * @param taskid
	 * @throws Exception
	 */
	public void cloneProcedure(String[] autoids, String taskid) throws Exception {
		Procedure procedure = null;

		for(int i = 0; i < autoids.length; i++) {
			//取出对应autoid的审计程序,更新taskid字段
			procedure = this.getProcedureById(autoids[i]);

			//"~" + taskid + "~";
			procedure.setTaskId("~" + taskid + "~");
			procedure.setState("未完成");
			procedure.setDefineId("");
			procedure.setExecutor("");

			this.addProcedure(procedure);
		}
	}

	/**
	 * 返回比当前程序优先需要完成的程序autoId
	 * @param autoId
	 * @return
	 */
	public String getNoFinishProcedure(String autoId) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String noFinish = "";

		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			String sql = " select group_concat(t.defineid) from ("
						+ " select b.defineid from z_procedure a,z_procedure b "
						+ " where a.projectId=? "
						+ " and b.projectId=? "
						+ " and a.autoid=? "
						+ " and b.taskid=a.taskid "
						+ " and b.defineid < a.defineid "
						+ " and b.state='未完成' "
						+ " and a.parentId=b.parentId"
						+ " order by b.defineid ) t";

			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);
			ps.setString(2, this.projectId);
			ps.setString(3, autoId);
			rs = ps.executeQuery();

			if(rs.next()) {
				noFinish = rs.getString(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return noFinish;

	}

	/**
	 * 设置程序的必做
	 * @param hidden
	 * @param show
	 * @param taskId
	 * @throws Exception
	 */
	public void setProcedureProperty(String hidden, String show, String fromTaskId, String toTaskId) throws Exception {
		String[] hiddenAutoId = hidden.split(",");

		String hiddenAutoIds = "-1";

		//处理taskcode,使其可以放在in里面
		for(int i=0; i < hiddenAutoId.length; i++) {
			if(!"".equals(hiddenAutoId[i])) {
				hiddenAutoIds += "," + hiddenAutoId[i];
			}
		}

		String[] showAutoId = show.split(",");

		String showAutoIds = "-1";

		//处理taskcode,使其可以放在in里面
		for(int i=0; i < showAutoId.length; i++) {
			if(!"".equals(showAutoId[i])) {
				showAutoIds += "," + showAutoId[i];
			}
		}

		new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
		DbUtil dbUtil = new DbUtil(conn);

		//是本节点
		String sql = null;

		if(fromTaskId.equals(toTaskId)) {
			//隐藏程序
			sql = " update z_procedure "
				+ " set property='' "
				+ " where projectId='" + this.projectId + "'"
				+ " and autoId in(" + hiddenAutoIds + ") ";

			dbUtil.executeUpdate(sql);

			//显示程序
			sql = " update z_procedure "
				+ " set property='必做' "
				+ " where projectId='" + this.projectId + "'"
				+ " and autoId in(" + showAutoIds + ") ";

			dbUtil.executeUpdate(sql);
		} else {
			//复制程序

			//找出最大的autoId
			sql = " select ifnull(max(autoId),0) + 1 "
				+ " from z_procedure "
				+ " where projectId='" + this.projectId + "'";

			int maxAutoId = dbUtil.queryForInt(sql);

			//显示程序
			sql = " insert into z_procedure(autoId,ProjectID,TaskID,State,DefineID,AuditProcedure,Remark,cognizance,parentId, property) "
				+ " select autoId+" + maxAutoId + " as autoId,ProjectID," + toTaskId + " as TaskID,'未完成' as State, "
				+ " DefineID,AuditProcedure,Remark,cognizance,if(parentId=0,0,parentId+" + maxAutoId + ") as parentId,'必做' as property "
				+ " from z_procedure "
				+ " where projectId='" + this.projectId + "'"
				+ " and autoId in(" + showAutoIds + ") ";

			dbUtil.executeUpdate(sql);
		}
	}
	
	/**
	 * 设置程序的隐显
	 * @param hidden
	 * @param show
	 * @param taskId
	 * @throws Exception
	 */
	public void setProcedureState(String hidden, String show, String fromTaskId, String toTaskId) throws Exception {
		String[] hiddenAutoId = hidden.split(",");

		String hiddenAutoIds = "-1";

		//处理taskcode,使其可以放在in里面
		for(int i=0; i < hiddenAutoId.length; i++) {
			if(!"".equals(hiddenAutoId[i])) {
				hiddenAutoIds += "," + hiddenAutoId[i];
			}
		}

		String[] showAutoId = show.split(",");

		String showAutoIds = "-1";

		//处理taskcode,使其可以放在in里面
		for(int i=0; i < showAutoId.length; i++) {
			if(!"".equals(showAutoId[i])) {
				showAutoIds += "," + showAutoId[i];
			}
		}

		new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
		DbUtil dbUtil = new DbUtil(conn);

		//是本节点
		String sql = null;

		if(fromTaskId.equals(toTaskId)) {
			//隐藏程序
			sql = " update z_procedure "
				+ " set state='不适用' "
				+ " where projectId='" + this.projectId + "'"
				+ " and autoId in(" + hiddenAutoIds + ") ";

			dbUtil.executeUpdate(sql);

			//显示程序
			sql = " update z_procedure "
				+ " set state='未完成' "
				+ " where projectId='" + this.projectId + "'"
				+ " and autoId in(" + showAutoIds + ") ";

			dbUtil.executeUpdate(sql);
		} else {
			//复制程序

			//找出最大的autoId
			sql = " select ifnull(max(autoId),0) + 1 "
				+ " from z_procedure "
				+ " where projectId='" + this.projectId + "'";

			int maxAutoId = dbUtil.queryForInt(sql);

			//显示程序
			sql = " insert into z_procedure(autoId,ProjectID,TaskID,State,DefineID,AuditProcedure,Remark,cognizance,parentId) "
				+ " select autoId+" + maxAutoId + " as autoId,ProjectID," + toTaskId + " as TaskID,'未完成' as State, "
				+ " DefineID,AuditProcedure,Remark,cognizance,if(parentId=0,0,parentId+" + maxAutoId + ") as parentId "
				+ " from z_procedure "
				+ " where projectId='" + this.projectId + "'"
				+ " and autoId in(" + showAutoIds + ") ";

			dbUtil.executeUpdate(sql);
		}
	}

	public static void main(String[] args) throws Exception {
		Connection conn = null;

		try {
			conn = new DBConnect().getConnect("");
			new ProcedureService(conn,"20081602").updateData("123");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

	}

	public boolean isLib() {
		return isLib;
	}

	public void setLib(boolean isLib) {
		this.isLib = isLib;
	}

	/**
	 * 新建表
	 * @throws MatechException
	 */
	public void createTable() throws Exception {
		new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
		dropTable();
		String sql = " create table " + this.tempTableName + " like z_procedure  ";
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
	public void dropTable() throws Exception {
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
	public void insertData() throws Exception{
		new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;

		try {
			String sql = " select DefineID,AuditProcedure,cognizance,Manuscript, if(state='' or state is null,'未完成',state) as state, Executor, remark, projectId, taskId "
					   + " from " + this.tempTableName
					   + " where AuditProcedure <>'' and defineid <> '' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			String sql2 = " insert into z_procedure(DefineID,AuditProcedure,cognizance,Manuscript, state, Executor, remark, projectId, taskId, autoId ) "
				+ " values(?,?,?,?,?, ?,?,?,?,?) ";
			ps2 = conn.prepareStatement(sql2);

			while(rs.next()) {
				ps2.setString(1, rs.getString(1));
				ps2.setString(2, rs.getString(2));
				ps2.setString(3, rs.getString(3));
				ps2.setString(4, rs.getString(4));
				ps2.setString(5, rs.getString(5));
				ps2.setString(6, rs.getString(6));
				ps2.setString(7, rs.getString(7));
				ps2.setString(8, rs.getString(8));
				ps2.setString(9, rs.getString(9));
				ps2.setString(10,getMaxAutoId());
				ps2.executeUpdate();
			}

			try {
				DbUtil.close(ps2);
				DbUtil.close(rs);
				DbUtil.close(ps);
			} catch (Exception e) {
				e.printStackTrace();
			}


			sql = "select taskId from " + this.tempTableName;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			String taskId = null;

			if(rs.next()) {
				taskId = rs.getString(1);
				updateData(taskId);
			}

			dropTable();
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(ps2);
		}
	}

	/**
	 * 批量导入完后，更新信息，例如上下级关系等
	 * @param taskId
	 * @throws Exception
	 */
	public void updateData(String taskId) throws Exception {
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;

		try {
			//找出所有导进去的程序
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
			String sql = " select defineId,autoid,taskId,level0 "
						+ " from z_procedure "
						+ " where projectId=? "
						+ " and taskId=? "
						+ " and state<>'备用 ' "
						+ " order by length(defineid),defineid ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);
			ps.setString(2, taskId);

			rs = ps.executeQuery();

			String defineId = null;
			String autoId = null;
			int level0 = 0;

			//更新父ID
			String sql2 = " update z_procedure set parentid=?,level0=? "
						+ " where projectId=? "
						+ " and taskId=?  "
						+ " and state<>'备用 ' "
						+ " and defineid like ? and defineid<>? ";
			ps2 = conn.prepareStatement(sql2);

			while(rs.next()) {
				defineId = rs.getString(1);
				autoId = rs.getString(2);
				level0 = rs.getInt(4);

				if(defineId != null) {

					level0++;
					ps2.setString(1, autoId);
					ps2.setInt(2, level0);
					ps2.setString(3, this.projectId);
					ps2.setString(4, taskId);
					ps2.setString(5, defineId + "%" );
					ps2.setString(6, defineId);
					ps2.executeUpdate();
				}
			}

			//重算level0
			sql = " update z_procedure set level0=length(defineid)-length(replace(defineid,'.','')) "
				+ " where projectId=? "
				+ " and taskId=?  "
				+ " and state<>'备用 ' ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);
			ps.setString(2, taskId);

			ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(ps2);
		}

	}

	/**
	 * 获得最大AUTOID
	 * @return
	 * @throws Exception
	 */
	public String getMaxAutoId() throws Exception {
		new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

		String sql = " select ifnull(max(autoId),0) + 1 "
					+ " from z_procedure "
					+ " where projectId=? ";
		Object[] objects = {this.projectId};

		return new DbUtil(conn).queryForString(sql, objects);
	}

	public String getTempTableName() {
		return tempTableName;
	}

	public void setTempTableName(String tempTableName) {
		this.tempTableName = tempTableName;
	}
	
	/**
	 * 检查所有适用的程序是否都标记成已完成
	 * @throws MatechException
	 */
	public boolean checkProcedureFinish() throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = "select count(*) from z_procedure where projectID =? and state = '未完成'";
			ps = conn.prepareStatement(sql);
			ps.setString(1,this.projectId) ;
			rs = ps.executeQuery();
			
			if(rs.next()) {
				
				int i = rs.getInt(1) ;
				
				if(i > 0) {
					return false ;
				}
				return true ;
			}
		
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return true ;
	}
	
	/**
	 * 所有不适用的程序,是否都经过审批
	 * @throws MatechException
	 */
	public boolean checkProcedureApproval() throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = "select count(*) from z_procedure where projectID =? and state = '不适用' and (approvalMan is null or approvalMan = '')";
			ps = conn.prepareStatement(sql);
			ps.setString(1,this.projectId) ;
			rs = ps.executeQuery();
			
			if(rs.next()) {
				
				int i = rs.getInt(1) ;
				
				if(i > 0) {
					return false ;
				}
				return true ;
			}
		
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return true ;
	}
	
	
	/**
	 * 不适用的程序退回,发送消息给改为不适用的人
	 * @throws MatechException
	 */
	public void sendBackProMessage(String projectId,String procedureId,String sendUserId,String reason) {
		
		   try {  
		    	
				ASFuntion CHF = new ASFuntion() ;
				
				PlacardTable pt = new PlacardTable() ;
				PlacardService pls = new PlacardService(conn) ;
				ProcedureService pds = new ProcedureService(conn,projectId) ;
				Procedure procedure = pds.getProcedureById(procedureId) ;
				String notApprovalMan = procedure.getNotApplicableMan() ;
System.out.println("###"+procedure.getAutoId()+"####"+notApprovalMan);				
				pt.setAddresser(sendUserId) ;
				String userName = new UserService(conn).getUser(sendUserId,"id").getName();
				pt.setAddresserTime(CHF.getCurrentDate()+" "+CHF.getCurrentTime()) ;
				pt.setCaption("您改为不适用的程序被退回了！") ;
				String projectName = new ProjectService(conn).getProjectById(projectId).getProjectName() ;
				
				String Matter ="["+userName+"]已经将项目["+projectName+"]中的不适用程序<br> 【"+procedure.getAuditProcedure()+"】退回，请您处理。<br> <a href=\"../AuditProject.do?method=login&pid="+projectId+"\">登陆该项目</a>" ;
				
				pt.setMatter(Matter) ;
				pt.setIsRead(0) ;
				pt.setIsReversion(0) ;
				pt.setIsNotReversion(0) ;
				pt.setAddressee(notApprovalMan) ;
				pls.AddPlacard(pt) ;
				
			} catch (Exception e) {
				 Debug.print(Debug.iError,"����ʧ��",e);
			}
		}
	
	

}
