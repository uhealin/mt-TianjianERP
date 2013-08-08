package com.matech.audit.service.oa.procedure;

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
import com.matech.audit.service.oa.procedure.model.Procedure;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;

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
			//throw new Exception("项目Id不能为空!");
			this.projectId = "0";
		} else {
			this.projectId = projectId;
		}
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


			String strSql = "select * from jbpm_z_procedure where autoid = ? and projectId=? ";
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



	        stmt = conn.createStatement();
	        rs = stmt.executeQuery("select ifnull(max(autoid),0) from jbpm_z_procedure where projectId='" + this.projectId + "'");
			if (rs.next()) {
				autoId = rs.getInt(1) + 1;
			}

			String strSql = "insert into jbpm_z_procedure(ProjectID,TaskID,State,DefineID,AuditProcedure,"
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


			String strSql = "update jbpm_z_procedure set ProjectID = ?,TaskID = ?,State = ?,DefineID =?,AuditProcedure = ?,Manuscript = ?,Executor = ?,Remark = ? where autoid = ? and projectId=? ";
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


	        String refTaskid = this.getProcedureById(autoId).getTaskId();
	        String[] taskids = refTaskid.split("~~");

	        String strSql;

	        if(taskids.length > 1) {
	        	refTaskid = refTaskid.replaceAll("~" + taskId + "~", "");
	        	strSql = "update jbpm_z_procedure set taskid=? where autoid = ? and projectId=? ";
	        	ps = conn.prepareStatement(strSql);
	        	ps.setString(1,refTaskid);
	        	ps.setString(2,autoId);
	        	ps.setString(3,this.projectId);
	        } else {
	        	strSql = "delete from jbpm_z_procedure where autoid = ? and projectId=? ";
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


	        String strSql = "delete from jbpm_z_procedure where projectId=? and autoid = ?";
			ps = conn.prepareStatement(strSql);
			ps.setString(1,this.projectId);
			ps.setString(2,autoid);

			if(ps.executeUpdate() > 0) {
				strSql = "delete from jbpm_z_procedure where projectId=? and parentId = ?";
				ps = conn.prepareStatement(strSql);
				ps.setString(1,this.projectId);
				ps.setString(2,autoid);
			}

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
	public Set getProceduresByTaskId(String taskId) throws Exception {
		Procedure procedure = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Set procedureSet = null;

		try {


			String strSql = "select * from jbpm_z_procedure where taskid like ? and projectid = ? order by DefineID ";
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



			String strSql = "update jbpm_z_procedure set " + att + " = ? "
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
	 * 根据taskId获得审计程序
	 * @param taskId
	 * @return
	 */
	public void getProcedureListByTaskId(List list, String taskId, String parentTaskId) throws Exception {
		Procedure procedure = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {


			String strSql = "select * from jbpm_z_procedure "
						  + " where taskid like ? "
						  + " and projectid = ? "
						  + " and parentId=? ";

			if(!this.isLib()) {
				strSql += " and state<>'不适用' ";
			}

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


			String strSql = "select * from jbpm_z_procedure "
						  + " where taskid like ? "
						  + " and projectid = ? "
						  + " and parentId=? ";


			if(!this.isLib()) {
				strSql += " and state<>'不适用' ";
			}

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


			String strSql = "select manuid,taskid,taskname,taskcode from jbpm_z_task where taskcode = ? and projectid = ? ";
			ps = conn.prepareStatement(strSql);
			ps.setString(1, taskcode);
			ps.setString(2, projectId);
			rs = ps.executeQuery();

			if(rs.next()) {
				link = "<a href='#' onclick=\"openFileByLink('" + rs.getString(2) + "');\">" + rs.getString(4) + " " + rs.getString(3) + "</a>";
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


			String sql = " select group_concat(t.defineid) from ("
						+ " select b.defineid from jbpm_z_procedure a,jbpm_z_procedure b "
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


		DbUtil dbUtil = new DbUtil(conn);

		//是本节点
		String sql = null;

		if(fromTaskId.equals(toTaskId)) {
			//隐藏程序
			sql = " update jbpm_z_procedure "
				+ " set state='不适用' "
				+ " where projectId='" + this.projectId + "'"
				+ " and autoId in(" + hiddenAutoIds + ") ";

			dbUtil.executeUpdate(sql);

			//显示程序
			sql = " update jbpm_z_procedure "
				+ " set state='未完成' "
				+ " where projectId='" + this.projectId + "'"
				+ " and autoId in(" + showAutoIds + ") ";

			dbUtil.executeUpdate(sql);
		} else {
			//复制程序

			//找出最大的autoId
			sql = " select ifnull(max(autoId),0) + 1 "
				+ " from jbpm_z_procedure "
				+ " where projectId='" + this.projectId + "'";

			int maxAutoId = dbUtil.queryForInt(sql);

			//显示程序
			sql = " insert into jbpm_z_procedure(autoId,ProjectID,TaskID,State,DefineID,AuditProcedure,Remark,cognizance,parentId) "
				+ " select autoId+" + maxAutoId + " as autoId,ProjectID," + toTaskId + " as TaskID,'未完成' as State, "
				+ " DefineID,AuditProcedure,Remark,cognizance,if(parentId=0,0,parentId+" + maxAutoId + ") as parentId "
				+ " from jbpm_z_procedure "
				+ " where projectId='" + this.projectId + "'"
				+ " and autoId in(" + showAutoIds + ") ";

			dbUtil.executeUpdate(sql);
		}
	}

	public static void main(String[] args) throws Exception {
		Connection conn = null;

		try {
			conn = new DBConnect().getConnect("");
			String result = new ProcedureService(conn,"2007924").getNoFinishProcedure("30999");
			System.out.println(result);
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

		dropTable();
		String sql = " create table " + this.tempTableName + " ( "
                   + " 		AutoID bigint(4) NOT NULL auto_increment,  "
                   + " 		ProjectID int(14) NOT NULL default '0',   "
                   + " 		TaskID varchar(100) default NULL,      "
                   + " 		State varchar(10) default NULL,      "
                   + " 		DefineID varchar(20) default NULL,      "
                   + " 		AuditProcedure varchar(1000) default NULL,  "
                   + " 		Manuscript varchar(300) default NULL,    "
                   + " 		Executor varchar(100) default NULL,    "
                   + " 		remark varchar(100) default NULL,     "
                   + " 		cognizance varchar(100) default NULL,  "
                   + " 		parentId varchar(30) default '0',     "
                   + " 		level0 int(4) default '0',         "
                   + " 		fullpath varchar(100) default NULL,  "
                   + " 		property varchar(30) default NULL, "
                   + " 	UNIQUE KEY AutoID (AutoID,ProjectID) "
                   + " ) ENGINE=MyISAM DEFAULT CHARSET=gbk  ";
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

		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;

		try {
			String sql = " select DefineID,AuditProcedure,cognizance,Manuscript, state, Executor, remark, projectId, taskId "
					   + " from " + this.tempTableName;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			String sql2 = " insert into jbpm_z_procedure(DefineID,AuditProcedure,cognizance,Manuscript, state, Executor, remark, projectId, taskId, autoId ) "
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
	 * 获得最大AUTOID
	 * @return
	 * @throws Exception
	 */
	public String getMaxAutoId() throws Exception {


		String sql = " select ifnull(max(autoId),0) + 1 "
					+ " from jbpm_z_procedure "
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
}
