package com.matech.audit.service.oa.worknote;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.oa.worknote.model.worknoteTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.Debug;

public class worknoteService {
	private Connection conn = null;

	public worknoteService(Connection conn) {
		this.conn = conn;
	}
/**
 * 删除信息
 * @param id
 * @throws MatechException
 */
	public void del(String id) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		try {
			sql = "delete from oa_worknote where autoid = '" + id + "' ";
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}
/**
 * 添加信息
 * @param wt
 * @throws MatechException
 */
	public void add(worknoteTable wt) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		try {
			sql = "insert into oa_worknote \n"
					+ " (starttime, endtime, workunit, \n" + " job,  \n"
					+ " proveman, \n" + " workcircs,  \n" + " userid,  \n"
					+ " property, \n" + "uploadFileName, \n" + "uploadTempName \n) values (?,?,?,?,?,?,?,?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, wt.getStarttime());
			ps.setString(2, wt.getEndtime());
			ps.setString(3, wt.getWorkunit());
			ps.setString(4, wt.getJob());
			ps.setString(5, wt.getProveman());
			ps.setString(6, wt.getWorkcircs());
			ps.setString(7, wt.getUserid());
			ps.setString(8, "");
			ps.setString(9,wt.getFileNames()) ;
			ps.setString(10,wt.getFileRondomNames()) ;
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}
/**
 * 根据指定的ID返回整条记录信息
 * @param id
 * @return
 * @throws MatechException
 */
	public worknoteTable getWorknote(String id) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		worknoteTable wt = new worknoteTable();
		try {
			sql = "select autoid, starttime, endtime, workunit, \n"
					+ " job,  \n" + " proveman, \n" + " workcircs,  \n"
					+ " userid,  \n" + " property, \n"+ "uploadFileName, \n" + "uploadTempName \n" + " from  \n"
					+ " oa_worknote   where autoid = '" + id + "' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				wt.setAutoid(rs.getInt(1));
				wt.setStarttime(rs.getString(2));
				wt.setEndtime(rs.getString(3));
				wt.setWorkunit(rs.getString(4));
				wt.setJob(rs.getString(5));
				wt.setProveman(rs.getString(6));
				wt.setWorkcircs(rs.getString(7));
				wt.setUserid(rs.getString(8));
				wt.setProperty(rs.getString(9));
				wt.setFileNames(rs.getString(10)) ;
				wt.setFileRondomNames(rs.getString(11)) ;
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return wt;
	}
/**
 * 更新信息
 * @param wt
 * @throws MatechException
 */
	public void update(worknoteTable wt) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		try {
			sql = "update oa_worknote set starttime=?,endtime=?,workunit=?,job=?,proveman=?,workcircs=?,uploadFileName=?,uploadTempName=? where autoid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, wt.getStarttime());
			ps.setString(2, wt.getEndtime());
			ps.setString(3, wt.getWorkunit());
			ps.setString(4, wt.getJob());
			ps.setString(5, wt.getProveman());
			ps.setString(6, wt.getWorkcircs());
			ps.setString(7,wt.getFileNames()) ;
			ps.setString(8,wt.getFileRondomNames()) ;
			ps.setInt(9, wt.getAutoid());
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

}
