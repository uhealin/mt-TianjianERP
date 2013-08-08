package com.matech.audit.service.oa.insurancecircs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.oa.insurancecircs.model.InsurancecircsTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.Debug;

public class InsurancecircsService {
	private Connection conn = null;

	public InsurancecircsService(Connection conn) {
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
			sql = "delete from oa_insurancecircs where autoid = '" + id + "' ";
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
	 * @param it
	 * @throws MatechException
	 */
	public void add(InsurancecircsTable it) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		try {
			sql = " insert into oa_insurancecircs(userid,insurancetype,trusteeshipunit,startdate,enddate,"
					+ " finallymoney,finallydate,checkinperson,checkindate,property) "
					+ " values (?,?,?,?,?,?,?,?,now(),?)";
			System.out.println("sql:" + sql);
			ps = conn.prepareStatement(sql);
			ps.setString(1, it.getUserid());
			ps.setString(2, it.getInsurancetype());
			ps.setString(3, it.getTrusteeshipunit());
			ps.setString(4, it.getStartdate());
			ps.setString(5, it.getEnddate());
			ps.setString(6, it.getFinallymoney());
			ps.setString(7, it.getFinallydate());
			ps.setString(8, it.getCheckinperson());
			ps.setString(9, "");
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 返回指定ID的整条记录信息
	 * 
	 * @param id
	 * @return
	 * @throws MatechException
	 */
	public InsurancecircsTable getInsurancecircs(String id)
			throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		InsurancecircsTable it = new InsurancecircsTable();
		try {
			sql = "select autoid,userid,insurancetype,trusteeshipunit,startdate,enddate,"
					+ " finallymoney,finallydate,checkinperson,checkindate,property  from oa_insurancecircs where autoid = '"
					+ id + "' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				it.setAutoid(rs.getInt(1));
				it.setUserid(rs.getString(2));
				it.setInsurancetype(rs.getString(3));
				it.setTrusteeshipunit(rs.getString(4));
				it.setStartdate(rs.getString(5));
				it.setEnddate(rs.getString(6));
				it.setFinallymoney(rs.getString(7));
				it.setFinallydate(rs.getString(8));
				it.setCheckinperson(rs.getString(9));
				it.setCheckindate(rs.getString(10));
				it.setProperty(rs.getString(11));
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return it;
	}

	/**
	 * 更新信息
	 * 
	 * @param it
	 * @throws MatechException
	 */
	public void update(InsurancecircsTable it) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		try {
			sql = "update oa_insurancecircs set insurancetype=?,trusteeshipunit=?,startdate=?,enddate=?,finallymoney=?,finallydate=?,userid=? "
					+ " where autoid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, it.getInsurancetype());
			ps.setString(2, it.getTrusteeshipunit());
			ps.setString(3, it.getStartdate());
			ps.setString(4, it.getEnddate());
			ps.setString(5, it.getFinallymoney());
			ps.setString(6, it.getFinallydate());
			ps.setString(7, it.getUserid());
			ps.setInt(8, it.getAutoid());
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

}
