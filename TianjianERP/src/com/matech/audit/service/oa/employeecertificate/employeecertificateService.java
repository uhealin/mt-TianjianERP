package com.matech.audit.service.oa.employeecertificate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.oa.employeecertificate.model.employeecertificateTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.Debug;

public class employeecertificateService {
	private Connection conn = null;

	public employeecertificateService(Connection conn) {
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
			sql = "delete from oa_employeecertificate where autoid = '" + id
					+ "' ";
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
	public void add(employeecertificateTable et) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		try {
			sql = "insert into oa_employeecertificate \n"
					+ " (certificatetype, certificateid, \n"
					+ " hairdepartment,  \n" + " hairtime,  \n"
					+ " availabilitytime, \n" + " remark,  \n" + " userid,  \n"
					+ " property, \n" + "uploadFileName, \n" + "uploadTempName) \n values (?,?,?,?,?,?,?,?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, et.getCertificatetype());
			ps.setString(2, et.getCertificateid());
			ps.setString(3, et.getHairdepartment());
			ps.setString(4, et.getHairtime());
			ps.setString(5, et.getAvailabilitytime());
			ps.setString(6, et.getRemark());
			ps.setString(7, et.getUserid());
			ps.setString(8, "");
			ps.setString(9,et.getFileNames()) ;
			ps.setString(10,et.getFileRondomNames()) ;
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}
/**
 * 根据指定ID显示整条记录
 * @param id
 * @return
 * @throws MatechException
 */
	public employeecertificateTable getEmployeeCertificate(String id)
			throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		employeecertificateTable et = new employeecertificateTable();
		try {
			sql = "select autoid, certificatetype, certificateid, \n"
					+ " hairdepartment, hairtime,  \n"
					+ " availabilitytime, remark, userid,  \n"
					+ " property,uploadFileName,uploadTempName  from  \n"
					+ " oa_employeecertificate where autoid = '" + id + "' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				et.setAutoid(rs.getInt(1));
				et.setCertificatetype(rs.getString(2));
				et.setCertificateid(rs.getString(3));
				et.setHairdepartment(rs.getString(4));
				et.setHairtime(rs.getString(5));
				et.setAvailabilitytime(rs.getString(6));
				et.setRemark(rs.getString(7));
				et.setUserid(rs.getString(8));
				et.setProperty(rs.getString(9));
				et.setFileNames(rs.getString(10)) ;
				et.setFileRondomNames(rs.getString(11)) ;
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
	public void update(employeecertificateTable et) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		try {
			sql = "update oa_employeecertificate set certificatetype=?,certificateid=?,hairdepartment=?,hairtime=?,availabilitytime=?,remark=?,uploadFileName=?,uploadTempName=? where autoid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, et.getCertificatetype());
			ps.setString(2, et.getCertificateid());
			ps.setString(3, et.getHairdepartment());
			ps.setString(4, et.getHairtime());
			ps.setString(5, et.getAvailabilitytime());
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
