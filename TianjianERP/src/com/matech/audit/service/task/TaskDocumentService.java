package com.matech.audit.service.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.task.model.TaskDocument;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.Debug;

public class TaskDocumentService {
	private Connection conn = null;
	private String ProjectId;

	public TaskDocumentService(Connection conn,String ProjectId) throws Exception {
		DbUtil.checkConn(conn);
		this.conn = conn;
		this.ProjectId = ProjectId;
	}

	public List getTaskDocListByTaskId(String ProjectId,String TaskId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		List docList = new ArrayList();
		TaskDocument doc = null;

		try {
			String sql = "select * from z_taskdocument "
						+ " where projectid= ? "
						+ " and taskid = ?";

			new DBConnect().changeDataBaseByProjectid(conn, ProjectId);

			ps = conn.prepareStatement(sql);
			ps.setString(1, ProjectId);
			ps.setString(2, TaskId);

			rs = ps.executeQuery();

			while (rs.next()) {
				doc = new TaskDocument();
				doc.setautoId( rs.getInt("AutoId"));
				doc.setprojectId(rs.getInt("ProjectId"));
				doc.settaskId(rs.getInt("TaskId"));
				doc.setdocName(rs.getString("DocName"));
				doc.setmemo(rs.getString("Memo"));
				doc.setmanuscript(rs.getString("Manuscript"));
				doc.setstatus(rs.getString("Status"));
				
				docList.add(doc);
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return docList;
	}
	
	public void addTaskDoc(TaskDocument doc) throws Exception	{
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			new DBConnect().changeDataBaseByProjectid(conn, this.ProjectId);

			String strSql = "insert into z_taskdocument (ProjectID, TaskID, DocName,Memo,Manuscript,Status) "
					+ " values(?,?,?,?,?,?)";
			ps = conn.prepareStatement(strSql);

			ps.setString(1, this.ProjectId);
			ps.setInt(2, doc.gettaskId());
			ps.setString(3, doc.getdocName());
			ps.setString(4, doc.getmemo());
			ps.setString(5, doc.getmanuscript());
			ps.setString(6, doc.getstatus());

			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public void delTaskDoc(String AutoId) throws Exception	{
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			new DBConnect().changeDataBaseByProjectid(conn, this.ProjectId);

			String strSql = "delete from  z_taskdocument where AutoId = ? and ProjectId = ?";
			ps = conn.prepareStatement(strSql);

			ps.setString(1, AutoId);
			ps.setString(2, this.ProjectId);		

			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public void updateTaskDoc(TaskDocument doc) throws Exception	{
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			new DBConnect().changeDataBaseByProjectid(conn, this.ProjectId);

			String strSql = "update z_taskdocument set ProjectID=?, TaskID=?, DocName =?,Memo =?,Manuscript =?,Status=? where AutoId = ? and ProjectId = ?";
			ps = conn.prepareStatement(strSql);

			ps.setInt(1, doc.getprojectId());
			ps.setInt(2, doc.gettaskId());
			ps.setString(3, doc.getdocName());
			ps.setString(4, doc.getmemo());
			ps.setString(5, doc.getmanuscript());
			ps.setString(6, doc.getstatus());
			ps.setInt(7, doc.getautoId());
			ps.setInt(8,doc.getprojectId());

			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public void updateTaskDocByAutoId(String autoId, String att, String val) throws Exception	{
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			new DBConnect().changeDataBaseByProjectid(conn, this.ProjectId);

			String strSql = "update z_taskdocument set "+ att +"=? where AutoId = ? and ProjectId = ?";
			ps = conn.prepareStatement(strSql);

			ps.setString(1, val);
			ps.setString(2, autoId);
			ps.setString(3, ProjectId);

			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

}
