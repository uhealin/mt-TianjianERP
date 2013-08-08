package com.matech.audit.service.oa.staff;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.oa.staff.model.StaffTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.Debug;

public class staffService {
	private Connection conn = null;

	public staffService(Connection conn) {
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
			sql = "delete from oa_Staff where autoid = '" + id + "' ";
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
	 * @param st
	 * @throws MatechException
	 */
	public void add(StaffTable st) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		try {
			sql = "insert into oa_staff \n"
					+ "     (cname, sex, department, post,mobilephone,email,identitycard, nation, \n"
					+ " 	familytelephone,  \n"
					+ " 	officetelephone,  \n"
					+ " 	birthday,  \n"
					+ " 	marriage,  \n"
					+ " 	workstarttime, \n"
					+ " 	workstate,  \n"
					+ " 	graduateschool, \n"
					+ " 	speciality,  \n"
					+ " 	schoolage,  \n"
					+ " 	duty,  \n"
					+ " 	government, \n"
					+ " 	health,  \n"
					+ " 	consortname, \n"
					+ " 	childname,  \n"
					+ " 	bank,  \n"
					+ " 	bankaccounts, \n"
					+ " 	dakcoding,  \n"
					+ " 	address,  \n"
					+ " 	dimissiontime, \n"
					+ " 	hukou,qq,nativeplace,property \n"
					+ " 	) "
					+ " 	values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, st.getCname());
			ps.setString(2, st.getSex());
			ps.setString(3, st.getDepartment());
			ps.setString(4, st.getPost());
			ps.setString(5, st.getPost());
			ps.setString(6, st.getPost());

			ps.setString(7, st.getIdentitycard());

			ps.setString(8, st.getNation());
			ps.setString(9, st.getFamilytelephone());
			ps.setString(10, st.getOfficetelephone());
			ps.setString(11, st.getBirthday());
			ps.setString(12, st.getMarriage());

			ps.setString(13, st.getWorkstarttime());
			ps.setString(14, st.getWorkstate());
			ps.setString(15, st.getGraduateschool());
			ps.setString(16, st.getSpeciality());
			ps.setString(17, st.getSchoolage());

			ps.setString(18, st.getDuty());
			ps.setString(19, st.getGovernment());
			ps.setString(20, st.getHealth());
			ps.setString(21, st.getConsortname());
			ps.setString(22, st.getChildname());

			ps.setString(23, st.getBank());
			ps.setString(24, st.getBankaccounts());
			ps.setString(25, st.getDakcoding());
			ps.setString(26, st.getAddress());

			ps.setString(27, st.getDimissiontime());
			ps.setString(28, st.getHukou());
			ps.setString(29, st.getQq());
			ps.setString(30, st.getNativeplace());
			ps.setString(31, st.getProperty());
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
	public StaffTable getStaff(String id) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		StaffTable st = new StaffTable();
		try {
			sql = "select 	autoid, cname, sex, department, post,mobilephone,email, \n"
					+ "	identitycard,  \n"
					+ "	nation,  \n"
					+ "	familytelephone, \n"
					+ "	officetelephone,  \n"
					+ "	birthday,  \n"
					+ "	marriage,  \n"
					+ "	workstarttime, \n"
					+ "	workstate,  \n"
					+ "	graduateschool, \n"
					+ "	speciality,  \n"
					+ "	schoolage,  \n"
					+ "	duty,  \n"
					+ "	government, \n"
					+ "	health,  \n"
					+ "	consortname, \n"
					+ "	childname,  \n"
					+ "	bank,  \n"
					+ "	bankaccounts, \n"
					+ "	dakcoding,  \n"
					+ "	address,  \n"
					+ "	dimissiontime, \n"
					+ "	hukou,  \n"
					+ "	qq,  \n"
					+ "	nativeplace, \n"
					+ "	property \n"
					+ "	from  \n"
					+ "	oa_staff  \n"
					+ " where autoid = '"
					+ id
					+ "' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				st.setAutoid(rs.getInt(1));
				st.setCname(rs.getString(2));
				st.setSex(rs.getString(3));
				st.setDepartment(rs.getString(4));
				st.setPost(rs.getString(5));
				st.setMobilephone(rs.getString(6));
				st.setEmail(rs.getString(7));

				st.setIdentitycard(rs.getString(8));
				st.setNation(rs.getString(9));
				st.setFamilytelephone(rs.getString(10));
				st.setOfficetelephone(rs.getString(11));
				st.setBirthday(rs.getString(12));

				st.setMarriage(rs.getString(13));
				st.setWorkstarttime(rs.getString(14));
				st.setWorkstate(rs.getString(15));
				st.setGraduateschool(rs.getString(16));
				st.setSpeciality(rs.getString(17));
				st.setSchoolage(rs.getString(18));
				st.setDuty(rs.getString(19));
				st.setGovernment(rs.getString(20));
				st.setHealth(rs.getString(21));

				st.setConsortname(rs.getString(22));
				st.setChildname(rs.getString(23));
				st.setBank(rs.getString(24));
				st.setBankaccounts(rs.getString(25));
				st.setDakcoding(rs.getString(26));
				st.setAddress(rs.getString(27));
				st.setDimissiontime(rs.getString(28));
				st.setHukou(rs.getString(29));
				st.setQq(rs.getString(30));
				st.setNativeplace(rs.getString(31));
				st.setProperty(rs.getString(32));
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return st;
	}

	/**
	 * 更新信息
	 * 
	 * @param st
	 * @throws MatechException
	 */
	public void update(StaffTable st) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		try {
			sql = "update oa_staff set"
					+ " cname=?, sex=?, department=?, post=?,mobilephone=?,email=?, \n"
					+ "	identitycard=?,  \n" + "	nation=?,  \n"
					+ "	familytelephone=?, \n" + "	officetelephone=?,  \n"
					+ "	birthday=?,  \n" + "	marriage=?,  \n"
					+ "	workstarttime=?, \n" + "	workstate=?,  \n"
					+ "	graduateschool=?, \n" + "	speciality=?,  \n"
					+ "	schoolage=?,  \n" + "	duty=?,  \n"
					+ "	government=?, \n" + "	health=?,  \n"
					+ "	consortname=?, \n" + "	childname=?,  \n"
					+ "	bank=?,  \n" + "	bankaccounts=?, \n"
					+ "	dakcoding=?,  \n" + "	address=?,  \n"
					+ "	dimissiontime=?, \n" + "	hukou=?,  \n" + "	qq=?,  \n"
					+ "	nativeplace=?, \n" + "	property=? \n"
					+ "	where autoid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, st.getCname());
			ps.setString(2, st.getSex());
			ps.setString(3, st.getDepartment());
			ps.setString(4, st.getPost());
			ps.setString(5, st.getMobilephone());
			ps.setString(6, st.getEmail());

			ps.setString(7, st.getIdentitycard());

			ps.setString(8, st.getNation());
			ps.setString(9, st.getFamilytelephone());
			ps.setString(10, st.getOfficetelephone());
			ps.setString(11, st.getBirthday());
			ps.setString(12, st.getMarriage());

			ps.setString(13, st.getWorkstarttime());
			ps.setString(14, st.getWorkstate());
			ps.setString(15, st.getGraduateschool());
			ps.setString(16, st.getSpeciality());
			ps.setString(17, st.getSchoolage());

			ps.setString(18, st.getDuty());
			ps.setString(19, st.getGovernment());
			ps.setString(20, st.getHealth());
			ps.setString(21, st.getConsortname());
			ps.setString(22, st.getChildname());

			ps.setString(23, st.getBank());
			ps.setString(24, st.getBankaccounts());
			ps.setString(25, st.getDakcoding());
			ps.setString(26, st.getAddress());

			ps.setString(27, st.getDimissiontime());
			ps.setString(28, st.getHukou());
			ps.setString(29, st.getQq());
			ps.setString(30, st.getNativeplace());
			ps.setString(31, st.getProperty());
			ps.setInt(32, st.getAutoid());
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

}
