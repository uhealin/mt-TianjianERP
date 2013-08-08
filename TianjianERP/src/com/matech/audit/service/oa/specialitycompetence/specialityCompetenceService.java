package com.matech.audit.service.oa.specialitycompetence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.oa.specialitycompetence.model.SpecialityCompetenceTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.Debug;

public class specialityCompetenceService {
	private Connection conn = null;

	public specialityCompetenceService(Connection conn) {
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
			sql = "delete from oa_specialitycompetence where autoid = '" + id
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
	 * 
	 * @param sct
	 * @throws MatechException
	 */
	public void add(SpecialityCompetenceTable sct) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		try {
			sql = "insert into asdb.oa_specialitycompetence \n"
					+ " (certificate, certificateid, certificatedepartment, \n"
					+ " certificatetime,  \n" + " availabilitytime,  \n"
					+ " ifera,  \n" + " remark,  \n" + " userid,  \n" 
					+ "uploadFileName, \n" + "uploadTempName, \n"
					+ " property \n" + " ) values (?,?,?,?,?,?,?,?,?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, sct.getCertificate());
			ps.setString(2, sct.getCertificateid());
			ps.setString(3, sct.getCertificatedepartment());
			ps.setString(4, sct.getCertificatetime());
			ps.setString(5, sct.getAvailabilitytime());
			ps.setString(6, sct.getIfera());
			ps.setString(7, sct.getRemark());
			ps.setString(8, sct.getUserid());
			ps.setString(9, sct.getFileNames()) ;
			ps.setString(10,sct.getFileRondomNames()) ;
			ps.setString(11, "");
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
	public SpecialityCompetenceTable getSpeciality(String id)
			throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		SpecialityCompetenceTable sct = new SpecialityCompetenceTable();
		try {
			sql = "select 	autoid, certificate, certificateid, certificatedepartment, \n"
					+ " certificatetime,  \n"
					+ " availabilitytime,  \n"
					+ " ifera,  \n"
					+ " remark,  \n"
					+ " userid,  \n"
					+ " property, \n"
					+ " uploadFileName, \n"
					+ " uploadTempName \n"
					+ " from  \n"
					+ " oa_specialitycompetence  where autoid = '" + id + "' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				sct.setAutoid(rs.getInt(1));
				sct.setCertificate(rs.getString(2));
				sct.setCertificateid(rs.getString(3));
				sct.setCertificatedepartment(rs.getString(4));
				sct.setCertificatetime(rs.getString(5));
				sct.setAvailabilitytime(rs.getString(6));
				sct.setIfera(rs.getString(7));
				sct.setRemark(rs.getString(8));
				sct.setUserid(rs.getString(9));
				sct.setProperty(rs.getString(10));
				sct.setFileNames(rs.getString(11)) ;
				sct.setFileRondomNames(rs.getString(12)) ;
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return sct;
	}

	/**
	 * 更新信息
	 * 
	 * @param sct
	 * @throws MatechException
	 */
	public void update(SpecialityCompetenceTable sct) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		try {
			sql = "update oa_specialitycompetence set certificate=?,certificateid=?,certificatedepartment=?,certificatetime=?,availabilitytime=?,ifera=?,remark=?,userid=?,uploadFileName=?,uploadTempName=? where autoid=?";
			ps = conn.prepareStatement(sql);
			
System.out.println("service:"+sct.getCertificate());
System.out.println("service:"+sct.getAutoid());

			
			ps.setString(1, sct.getCertificate());
			ps.setString(2, sct.getCertificateid());
			ps.setString(3, sct.getCertificatedepartment());
			ps.setString(4, sct.getCertificatetime());
			ps.setString(5, sct.getAvailabilitytime());
			ps.setString(6, sct.getIfera());
			ps.setString(7, sct.getRemark());
			ps.setString(8, sct.getUserid());
			ps.setString(9,sct.getFileNames()) ;
			ps.setString(10,sct.getFileRondomNames()) ;
			ps.setInt(11, sct.getAutoid());
			ps.execute() ;
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

}
