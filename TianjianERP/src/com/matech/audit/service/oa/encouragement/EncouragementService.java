package com.matech.audit.service.oa.encouragement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.oa.encouragement.model.EncouragementTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.Debug;

public class EncouragementService {
	private Connection conn = null;

	public EncouragementService(Connection conn) {
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
			sql = "delete from oa_encouragement where autoid = '" + id + "' ";
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
 * @param et
 * @throws MatechException
 */
	public void add(EncouragementTable et) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		try {
			sql = " insert into oa_encouragement(userid,pricedate,pricetype,whys,result,"
					+ " remark,checkinperson,checkindate,property,uploadFileName,uploadTempName) "
					+ " values (?,?,?,?,?,?,?,now(),?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, et.getUserid());
			ps.setString(2, et.getPricedate());
			ps.setString(3, et.getPricetype());
			ps.setString(4, et.getWhys());
			ps.setString(5, et.getResult());
			ps.setString(6, et.getRemark());
			ps.setString(7, et.getCheckinperson());
			ps.setString(8, "");
			ps.setString(9, et.getFileNames()) ;
			ps.setString(10, et.getFileRondomNames()) ;
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
 * @param id
 * @return
 * @throws MatechException
 */
	public EncouragementTable getEncouragement(String id)
			throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		EncouragementTable et = new EncouragementTable();
		try {
			sql = "select autoid,userid,pricedate,pricetype,whys,result,"
					+ " remark,checkinperson,checkindate,property,uploadFileName,uploadTempName  from oa_encouragement where autoid = '"
					+ id + "' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				et.setAutoid(rs.getInt(1));
				et.setUserid(rs.getString(2));
				et.setPricedate(rs.getString(3));
				et.setPricetype(rs.getString(4));
				et.setWhys(rs.getString(5));
				et.setResult(rs.getString(6));
				et.setRemark(rs.getString(7));
				et.setCheckinperson(rs.getString(8));
				et.setCheckindate(rs.getString(9));
				et.setProperty(rs.getString(10));
				et.setFileNames(rs.getString(11)) ;
				et.setFileRondomNames(rs.getString(12)) ;
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return et;
	}
/**
 * 更新信息
 * @param et
 * @throws MatechException
 */
	public void update(EncouragementTable et) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		try {
			sql = "update oa_encouragement set userid=?,pricedate=?,pricetype=?,whys=?,result=?,remark=?,uploadFileName=?,uploadTempName=?  "
					+ " where autoid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, et.getUserid());
			ps.setString(2, et.getPricedate());
			ps.setString(3, et.getPricetype());
			ps.setString(4, et.getWhys());
			ps.setString(5, et.getResult());
			ps.setString(6, et.getRemark());
			ps.setString(7,et.getFileNames()) ;
			ps.setString(8,et.getFileRondomNames()) ;
			ps.setInt(9, et.getAutoid());
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

}
