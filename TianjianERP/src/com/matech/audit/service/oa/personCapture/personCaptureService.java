package com.matech.audit.service.oa.personCapture;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.oa.personCapture.model.PersonCaptureTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.Debug;

public class personCaptureService {
	private Connection conn = null;

	public personCaptureService(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 删除记录
	 * 
	 * @param id
	 * @throws MatechException
	 */
	public void del(String id) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		try {
			sql = "delete from oa_personcapture where autoid = '" + id + "' ";
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
	 * @param pct
	 * @throws MatechException
	 */
	public void add(PersonCaptureTable pct) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		try {
			sql = "insert into oa_personcapture(userid,units,starttime,endtime,capturemoney,endcapturetime,booker,checkintime,property) values (?,?,?,?,?,?,?,now(),?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, pct.getUserid());
			ps.setString(2, pct.getUnits());
			ps.setString(3, pct.getStarttime());
			ps.setString(4, pct.getEndtime());
			ps.setString(5, pct.getCapturemoney());
			ps.setString(6, pct.getEndcapturetime());
			ps.setString(7, pct.getBooker());
			ps.setString(8, "");
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 返回指定ID的整条记录的信息
	 * 
	 * @param id
	 * @return
	 * @throws MatechException
	 */
	public PersonCaptureTable getPerson(String id) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		PersonCaptureTable pct = new PersonCaptureTable();
		try {
			sql = "select autoid,userid,units,starttime,endtime,capturemoney,endcapturetime,booker,checkintime,property from oa_personcapture where autoid = '"
					+ id + "' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				pct.setAutoid(rs.getInt(1));
				pct.setUserid(rs.getString(2));
				pct.setUnits(rs.getString(3));
				pct.setStarttime(rs.getString(4));
				pct.setEndtime(rs.getString(5));
				pct.setCapturemoney(rs.getString(6));
				pct.setEndcapturetime(rs.getString(7));
				pct.setBooker(rs.getString(8));
				pct.setCheckintime(rs.getString(9));
				pct.setProperty(rs.getString(10));
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return pct;
	}

	/**
	 * 更新信息
	 * 
	 * @param pct
	 * @throws MatechException
	 */
	public void update(PersonCaptureTable pct) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		try {
			sql = "update oa_personcapture set units=?,starttime=?,endtime=?,capturemoney=?,endcapturetime=?,userid=? where autoid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, pct.getUnits());
			ps.setString(2, pct.getStarttime());
			ps.setString(3, pct.getEndtime());
			ps.setString(4, pct.getCapturemoney());
			ps.setString(5, pct.getEndcapturetime());
			ps.setString(6, pct.getUserid());
			ps.setInt(7, pct.getAutoid());
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

}
