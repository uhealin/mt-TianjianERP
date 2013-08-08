package com.matech.audit.service.oa.family;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.oa.family.model.Family;
import com.matech.framework.pub.db.DbUtil;

public class FamilyService {

	private Connection conn = null;

	public FamilyService(Connection conn) {

		this.conn = conn;
	}
/**
 * 添加信息
 * @param family
 * @return
 * @throws Exception
 */
	public boolean addFamily(Family family) throws Exception {

		DbUtil.checkConn(conn);
		PreparedStatement ps = null;

		try {

			String sql = "insert into oa_family"
					+ "(compellation,footing,workunit,phone,government,userid,uploadFileName,uploadTempName)"
					+ "values(?,?,?,?,?,?,?,?)";

			ps = conn.prepareStatement(sql);

			ps.setString(1, family.getCompellation());
			ps.setString(2, family.getFooting());
			ps.setString(3, family.getWorkunit());
			ps.setString(4, family.getPhone());
			ps.setString(5, family.getGovernment());
			ps.setString(6, family.getUserid());
			ps.setString(7, family.getFileNames()) ;
			ps.setString(8, family.getFileRondomNames()) ;

			ps.execute();

			return true;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			DbUtil.close(ps);
		}

		return false;

	}
	
	public Family getFamily(String autoId) {
		
		PreparedStatement ps = null;
		ResultSet rs = null ;
		Family family = new Family() ;

		try {
			DbUtil.checkConn(conn);
		String sql = "select 	autoid, compellation,footing,workunit,phone,government,userid, \n"
				+ " uploadFileName, \n"
				+ " uploadTempName \n"
				+ " from  \n"
				+ " oa_family  where autoid = '" + autoId + "' ";

			ps = conn.prepareStatement(sql);

			rs = ps.executeQuery() ;
			
			if(rs.next()) {
				family.setAutoid(rs.getString(1)) ;
				family.setCompellation(rs.getString(2)) ;
				family.setFooting(rs.getString(3)) ;
				family.setWorkunit(rs.getString(4)) ;
				family.setPhone(rs.getString(5)) ;
				family.setGovernment(rs.getString(6)) ;
				family.setUserid(rs.getString(7)) ;
				family.setFileNames(rs.getString(8)) ;
				family.setFileRondomNames(rs.getString(9)) ;
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			DbUtil.close(ps);
		}

		return family;
	}
/**
 * 更新信息
 * @param family
 * @param autoid
 * @return
 * @throws Exception
 */
	public boolean updateFamily(Family family, String autoid) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;

		try {

			String sql = "update oa_family set compellation =?,footing=?,workunit=?,phone=?,government=?,uploadFileName=?,uploadTempName=? where autoid=?";

			ps = conn.prepareStatement(sql);

			ps.setString(1, family.getCompellation());

			ps.setString(2, family.getFooting());

			ps.setString(3, family.getWorkunit());

			ps.setString(4, family.getPhone());

			ps.setString(5, family.getGovernment());
			
			ps.setString(6, family.getFileNames());
			
			ps.setString(7, family.getFileRondomNames());

			ps.setString(8, autoid);

			ps.execute();

			return true;

		} catch (Exception e) {
			e.printStackTrace();

		} finally {

			DbUtil.close(ps);
		}

		return false;

	}
/**
 * 删除指定ID的信息
 * @param autoid
 * @return
 * @throws Exception
 */
	public boolean removeLabor(String autoid) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;

		try {

			String sql = "delete from oa_family where autoid=?";

			ps = conn.prepareStatement(sql);
			ps.setString(1, autoid);

			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

		return false;

	}

}
