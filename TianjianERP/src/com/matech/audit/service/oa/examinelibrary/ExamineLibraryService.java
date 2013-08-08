package com.matech.audit.service.oa.examinelibrary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.oa.examinelibrary.model.ExamineLibraryTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.Debug;

public class ExamineLibraryService {
	private Connection conn = null;

	public ExamineLibraryService(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 删除信息
	 * 
	 * @param id
	 * @throws MatechException
	 */
	public void del(String id) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		try {
			sql = "delete from oa_examinelibrary where autoid = '" + id + "' ";
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
	 * 
	 * @param elt
	 * @throws MatechException
	 */
	public void add(ExamineLibraryTable elt) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		try {
			sql = " insert into oa_examinelibrary \n"
					+ " (ctype, cname, ccal, cformula, isenable, \n"
					+ " orderid, property, memo) \n"
					+ " values (?,?,?,?,?,?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, elt.getCtype());
			ps.setString(2, elt.getCname());
			ps.setString(3, elt.getCcal());
			ps.setString(4, elt.getCformula());
			ps.setString(5, elt.getIsenable());
			ps.setDouble(6, elt.getOrderid());
			ps.setString(7, elt.getProperty());
			ps.setString(8, elt.getMemo());
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 根据指定ID显示整条记录信息
	 * 
	 * @param id
	 * @return
	 * @throws MatechException
	 */
	public ExamineLibraryTable getExamineLibrary(String id)
			throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		ExamineLibraryTable elt = new ExamineLibraryTable();
		try {
			sql = " select 	autoid, ctype, cname, ccal, cformula, \n"
					+ " isenable, orderid, property, Memo \n"
					+ "  from oa_examinelibrary where autoid = '" + id + "' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				elt.setAutoid(rs.getInt(1));
				elt.setCtype(rs.getString(2));
				elt.setCname(rs.getString(3));
				elt.setCcal(rs.getString(4));
				elt.setCformula(rs.getString(5));
				elt.setIsenable(rs.getString(6));
				elt.setOrderid(rs.getDouble(7));
				elt.setProperty(rs.getString(8));
				elt.setMemo(rs.getString(9));
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return elt;
	}

	/**
	 * 更新信息
	 * 
	 * @param elt
	 * @throws MatechException
	 */
	public void update(ExamineLibraryTable elt) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		try {
			sql = "update oa_examinelibrary set ctype=?,cname=?,ccal=?,cformula=?,isenable=?,orderid=?,Memo=?  "
					+ " where autoid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, elt.getCtype());
			ps.setString(2, elt.getCname());
			ps.setString(3, elt.getCcal());
			ps.setString(4, elt.getCformula());
			ps.setString(5, elt.getIsenable());
			ps.setDouble(6, elt.getOrderid());
			//ps.setString(7, elt.getProperty());
			ps.setString(7, elt.getMemo());
			ps.setInt(8, elt.getAutoid());
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

}
