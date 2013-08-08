package com.matech.audit.service.oa.insuranceType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.oa.insuranceType.model.InsuranceTypeTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.Debug;

public class InsuranceTypeService {
	private Connection conn = null;

	public InsuranceTypeService(Connection conn) {
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
			sql = "delete from oa_insurancetype where autoid = '" + id + "' ";
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
 * @param itt
 * @throws MatechException
 */
	public void add(InsuranceTypeTable itt) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		try {
			sql = " insert into oa_insurancetype(ctype,carea,ctime,cmoney,insurance,"
					+ " property, \n" 
					+ "uploadFileName, \n" + "uploadTempName \n)"
					+ " values (?,?,?,?,?,?,?,?)";
			System.out.println("sql:" + sql);
			ps = conn.prepareStatement(sql);
			ps.setString(1, itt.getCtype());
			ps.setString(2, itt.getCarea());
			ps.setString(3, itt.getCtime());
			ps.setString(4, itt.getCmoney());
			ps.setString(5, itt.getInsurance());
			ps.setString(6, itt.getProperty());
			ps.setString(7,	itt.getFileNames()) ;
			ps.setString(8,itt.getFileRondomNames()) ;
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
 * @param id
 * @return
 * @throws MatechException
 */
	public InsuranceTypeTable getInsuranceType(String id)
			throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		InsuranceTypeTable itt = new InsuranceTypeTable();
		try {
			sql = "select autoid, ctype, carea, ctime, cmoney,insurance,property,uploadFileName,uploadTempName"
					+ " from oa_insurancetype  where autoid = '" + id + "' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				itt.setAutoid(rs.getInt(1));
				itt.setCtype(rs.getString(2));
				itt.setCarea(rs.getString(3));
				itt.setCtime(rs.getString(4));
				itt.setCmoney(rs.getString(5));
				itt.setInsurance(rs.getString(6));
				itt.setProperty(rs.getString(7));
				itt.setFileNames(rs.getString(8)) ;
				itt.setFileRondomNames(rs.getString(9)) ;
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return itt;
	}
/**
 * 更新信息
 * @param itt
 * @throws MatechException
 */
	public void update(InsuranceTypeTable itt) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		try {
			sql = "update oa_insurancetype set ctype=?,carea=?,ctime=?,cmoney=?,insurance=?,uploadFileName=?,uploadTempName=? "
					+ " where autoid=?";
			ps = conn.prepareStatement(sql);
			// ps.setString(1, it.getCaptureperson());
			ps.setString(1, itt.getCtype());
			ps.setString(2, itt.getCarea());
			ps.setString(3, itt.getCtime());
			ps.setString(4, itt.getCmoney());
			ps.setString(5, itt.getInsurance());
			ps.setString(6,itt.getFileNames()) ;
			ps.setString(7,itt.getFileRondomNames()) ;
			ps.setInt(8, itt.getAutoid());
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

}
