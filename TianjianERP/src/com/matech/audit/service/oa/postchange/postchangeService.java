package com.matech.audit.service.oa.postchange;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.oa.postchange.model.postchangeTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.Debug;

public class postchangeService {
	private Connection conn = null;

	public postchangeService(Connection conn) {
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
			sql = "delete from oa_postchange where autoid = '" + id + "' ";
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
	 * @param pt
	 * @throws MatechException
	 */
	public void add(postchangeTable pt) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		try {
			sql = "insert into oa_postchange \n" +
					"(" +
					"starttime, endtime, formerlypost, adjustpost, userid,  " +
					"property,fdepartmentid,adepartmentid " +
					") values (?,?,?,?,?, ?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, pt.getStarttime());
			ps.setString(2, pt.getEndtime());
			ps.setString(3, pt.getFormerlypost());
			ps.setString(4, pt.getAdjustpost());
			ps.setString(5, pt.getUserid());
			
			ps.setString(6, "");
			ps.setString(7, pt.getFdepartmentid());
			ps.setString(8, pt.getAdepartmentid());
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 根据指定ID返回整条记录信息
	 * 
	 * @param id
	 * @return
	 * @throws MatechException
	 */
	public postchangeTable getPostchange(String id) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		postchangeTable pt = new postchangeTable();
		try {
			sql = "select * from  oa_postchange where autoid = '" + id + "' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				pt.setAutoid(rs.getInt("autoid"));
				pt.setStarttime(rs.getString("starttime"));
				pt.setEndtime(rs.getString("endtime"));
				pt.setFormerlypost(rs.getString("formerlypost"));
				pt.setAdjustpost(rs.getString("adjustpost"));
				pt.setUserid(rs.getString("userid"));
				pt.setProperty(rs.getString("property"));
				pt.setFdepartmentid(rs.getString("fdepartmentid"));
				pt.setAdepartmentid(rs.getString("adepartmentid"));
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return pt;
	}

	/**
	 * 更新信息
	 * 
	 * @param pt
	 * @throws MatechException
	 */
	public void update(postchangeTable pt) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		try {
			sql = "update oa_postchange set " +
					"starttime=?,endtime=?,formerlypost=?,adjustpost=?,userid=?," +
					"fdepartmentid=?,adepartmentid=? " +
					"where autoid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, pt.getStarttime());
			ps.setString(2, pt.getEndtime());
			ps.setString(3, pt.getFormerlypost());
			ps.setString(4, pt.getAdjustpost());
			ps.setString(5, pt.getUserid());
			ps.setString(6, pt.getFdepartmentid());
			ps.setString(7, pt.getAdepartmentid());
			
			ps.setInt(8, pt.getAutoid());
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

}
