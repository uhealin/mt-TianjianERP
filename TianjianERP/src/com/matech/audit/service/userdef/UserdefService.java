package com.matech.audit.service.userdef;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.user.UserService;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.sys.UTILSysProperty;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.pub.util.UTILString;

/**
 * 
 * <p>
 * Title: 自定义信息
 * </p>
 * <p>
 * Description: 自定义信息表的增、删、查、改
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006, 2008 MaTech Corporation.All rights reserved.
 * </p>
 * <p>
 * Company: Matech 广州铭太信息科技有限公司
 * </p>
 * 
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有， 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 * 
 * <p>
 * 贡献者团队: 铭太科技 - 研发中心，审计开发组
 * 
 * @author 彭勇 2007-6-11
 */
public class UserdefService {
	private Connection conn = null;

	public UserdefService(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 用于删除用户自定义信息 
	 * 
	 * @param contrastid
	 *            编号
	 * @param property
	 *            属性：user 用户,cust 客户,depart 部门,com 单位
	 * @throws MatechException
	 */
	public void removeUserdef(String contrastid, String property)
			throws MatechException {
		DbUtil.checkConn(conn);

		String sql = "";

		PreparedStatement ps = null;
		try {
			sql = "delete from k_UserDef where ContrastID='" + contrastid
					+ "' and property='" + property + "' ";
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

	public void removeUserdef(Userdef userdef, String contrastid,
			String property) throws MatechException {
		DbUtil.checkConn(conn);

		String sql = "";

		PreparedStatement ps = null;
		try {
			sql = "delete from k_UserDef where ContrastID=? and property=? and name = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, contrastid);
			ps.setString(2, property);
			ps.setString(3, userdef.getName());
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 用于新增或修改用户自定义信息 算法：先删除同编号同属性的自定义信息，再新增自定义信息
	 * 
	 * @param userdefVo
	 *            自定义信息数组
	 * @param contrastid
	 *            编号
	 * @param property
	 *            属性：user 用户,cust 客户,depart 部门,com 单位
	 * @throws MatechException
	 */
	public void addOrupdateUserdef(Userdef[] userdef, String contrastid,
			String property) throws MatechException {
		DbUtil.checkConn(conn);

		String sql = "";

		PreparedStatement ps = null;
		try {
			removeUserdef(contrastid, property);

			for (int i = 0; i < userdef.length && userdef[i].getName() != null; i++) {

				sql = "insert into k_UserDef(ContrastID,Name,Value,Property) VALUES(?,?,?,?)";
				ps = conn.prepareStatement(sql);

				ps.setString(1, contrastid);
				ps.setString(2, userdef[i].getName());
				ps.setString(3, userdef[i].getValue());
				ps.setString(4, property);

				ps.execute();

			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 项目使用的
	 * @param defNames
	 * @param defValues
	 * @param property
	 * @param contrastId
	 * @throws Exception
	 */
	public void saveByProject(String[] defNames, String[] defValues, String property, String contrastId) throws Exception {

		PreparedStatement ps = null;
		try {
			//删除原来的信息
			String sql = " delete from k_userdef where contrastId=? and Property=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, contrastId);
			ps.setString(2, property);
			ps.executeUpdate();
			
			
			sql = "insert into k_UserDef(ContrastID,Name,Value,Property) VALUES(?,?,?,?)";
			ps = conn.prepareStatement(sql);

			if(defNames != null) {
				for(int i=0; i < defNames.length; i++ ){
					ps.setString(1, contrastId);
					ps.setString(2, defNames[i]);
					ps.setString(3, defValues[i]);
					ps.setString(4, property);

					ps.executeUpdate();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}

	public void addOrupdateUserdef(Userdef userdef, String contrastid,
			String property) throws MatechException {
		DbUtil.checkConn(conn);

		String sql = "";

		PreparedStatement ps = null;
		try {
			removeUserdef(userdef, contrastid, property);

			sql = "insert into k_UserDef(ContrastID,Name,Value,Property) VALUES(?,?,?,?)";
			ps = conn.prepareStatement(sql);

			ps.setString(1, contrastid);
			ps.setString(2, userdef.getName());
			ps.setString(3, userdef.getValue());
			ps.setString(4, property);

			ps.execute();

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}
	
	public void addOrupdateUserdef(Userdef userdef) throws MatechException {
		DbUtil.checkConn(conn);

		String sql = "";

		PreparedStatement ps = null;
		try {
			removeUserdef(userdef, userdef.getContrastid(), userdef.getProperty());

			sql = "insert into k_UserDef(ContrastID,Name,Value,Property) VALUES(?,?,?,?)";
			ps = conn.prepareStatement(sql);

			ps.setString(1, userdef.getContrastid());
			ps.setString(2, userdef.getName());
			ps.setString(3, userdef.getValue());
			ps.setString(4, userdef.getProperty());

			ps.execute();

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 用于得到用户自定义信息数组
	 * 
	 * @param contrastid
	 *            编号
	 * @param property
	 *            属性：user 用户,cust 客户,depart 部门,com 单位
	 * @return
	 * @throws MatechException
	 */
	public Userdef[] getUserdef(String contrastid, String property)
			throws MatechException {
		DbUtil.checkConn(conn);

		String sql = "";

		PreparedStatement ps = null;
		ResultSet rs = null;
		Userdef[] userdef = null;
		try {
			sql = "select * from k_UserDef where ContrastID='" + contrastid
					+ "' and property='" + property + "' order by id asc ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			rs.last();
			userdef = new Userdef[rs.getRow()];
			rs.beforeFirst();
			int i = 0;
			while (rs.next()) {
				userdef[i] = new Userdef();
				userdef[i].setName(rs.getString("name"));
				userdef[i].setValue(rs.getString("value"));
				i++;
			} 
			return userdef;
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	/**
	 * 判断审计权限
	 * 
	 * @param projectid
	 * @param names
	 * @return
	 * @throws MatechException
	 */
	public String getAuthority(String projectid, String[] names)
			throws MatechException {
		DbUtil.checkConn(conn);

		String sql = "";

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			if (names.length == 2) {
				sql = "select group_concat(Value) from k_userdef where ContrastID = '"
						+ projectid
						+ "' and (name='"
						+ names[0]
						+ "' or name='" + names[1] + "')";
			} else {
				sql = "select group_concat(Value) from k_userdef where ContrastID = '"
						+ projectid + "' and name='" + names[0] + "'";
			}
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return null;
	}

	/**
	 * 判断用户是否有二审权限
	 * 
	 * @param userid
	 * @param projectid
	 * @param names
	 * @return
	 * @throws MatechException
	 */
	public boolean hasAuthority2(String userid, String projectid)
			throws MatechException {
		DbUtil.checkConn(conn);

		String sql = "";

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			//先找出人员属于哪些角色
			sql = "select group_concat(rid) from k_userrole where userid = '"
				+ userid + "'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			String userRoles = "" ;
			if (rs.next()) {
				userRoles = rs.getString(1);
			}
			
			//看二审人角色里是否包含了这些角色
			String role = new ASFuntion().showNull(UTILSysProperty.SysProperty
					.getProperty("二审人角色"));
			String[] hasRightRoles = userRoles.split(",");
			String[] roles = role.split(",");
			for (int i = 0; i < hasRightRoles.length; i++) {
				for (int j = 0; j < roles.length; j++) {
					if (hasRightRoles[i].equals(roles[j])) {
						return true;
					}
				}
			}
			
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return false;
	}

	/**
	 * 判断用户是否有三审权限
	 * 
	 * @param userid
	 * @param projectid
	 * @param names
	 * @return
	 * @throws MatechException
	 */
	public boolean hasAuthority3(String userid, String projectid)
			throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		String hasRightRole = "";
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			sql = "select group_concat(rid) from k_userrole where userid = '"
					+ userid + "'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				hasRightRole = rs.getString(1);
			}

			String role = new ASFuntion().showNull(UTILSysProperty.SysProperty.getProperty("三审人角色"));
			String[] hasRightRoles = hasRightRole.split(",");
			String[] roles = role.split(",");
			for (int i = 0; i < hasRightRoles.length; i++) {
				for (int j = 0; j < roles.length; j++) {
					if (hasRightRoles[i].equals(roles[j])) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return false;
	}
	
	
	/**
	 * 判断用户是否有签发权限
	 * 
	 * @param userid
	 * @param projectid
	 * @param names
	 * @return
	 * @throws MatechException
	 */
	public boolean hasAuthority4(String userid, String projectid)
			throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		String hasRightRole = "";
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			sql = "select group_concat(rid) from k_userrole where userid = '"
					+ userid + "'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				hasRightRole = rs.getString(1);
			}

			String role = new ASFuntion().showNull(UTILSysProperty.SysProperty.getProperty("签发人角色"));
			String[] hasRightRoles = hasRightRole.split(",");
			String[] roles = role.split(",");
			for (int i = 0; i < hasRightRoles.length; i++) {
				for (int j = 0; j < roles.length; j++) {
					if (hasRightRoles[i].equals(roles[j])) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return false;
	}

	/**
	 * 判断用户是否有三审权限(指定到具体的人）
	 * 
	 * @param userid
	 * @param projectid
	 * @param names
	 * @return
	 * @throws MatechException
	 */
	public boolean hasAuthority3toPerson(String userid, String projectid)
			throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			sql = "select * from k_userdef where contrastid=? and name='三审人员' and value=? and property=0";
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectid);
			ps.setString(2, userid);
			rs = ps.executeQuery();
			if (rs.next()) {
				return true;
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return false;
	}

	public void saveAuditPerson(String Name, String value, String projectId)
			throws Exception {

		PreparedStatement ps = null;

		try {
			String sql = "insert into k_userdef(ContrastID,Name,Value,Property) values(?,?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectId);
			ps.setString(2, Name);
			ps.setString(3, value);
			ps.setString(4, "0");
			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

	}

	public void deleteAuditPerson(String projectId) throws Exception {

		PreparedStatement ps = null;

		try {
			String sql = "delete from k_userdef where (Name='二审人员' or Name='独立审计人员') and ContrastID=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectId);

			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

	}

	/**
	 * 检查是否所有独立审核人都通过二审
	 * 
	 * @throws MatechException
	 */
	public boolean CheckAuditPersonAgree(String projectId) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSet rs2 = null;

		try {

			String sql = "select 1 from k_userdef where Name='二审人员' and ContrastID=? and property='1'";
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectId);
			rs = ps.executeQuery();
			if (rs.next()) {
				sql = "select property from k_userdef where Name='独立审计人员' and ContrastID=?";
				ps = conn.prepareStatement(sql);
				ps.setString(1, projectId);

				rs2 = ps.executeQuery();

				while (rs2.next()) {

					String property = rs2.getString("property");
					if (!"1".equals(property)) {
						return false;
					}

				}
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		return false;

	}

	public void updateAuditByUserId(String userId, String projectId)
			throws Exception {

		PreparedStatement ps = null;

		try {
			String sql = "update k_userdef set Property='1' where ContrastID=?  and name='二审人员' ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectId);

			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

	}

	public void addAuditUser(Userdef ud) throws Exception {

		PreparedStatement ps = null;
		try {
			String sql = "insert into k_userdef(contrastid,name,value,property) values (?,?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, ud.getContrastid());
			ps.setString(2, ud.getName());
			ps.setString(3, ud.getValue());
			ps.setString(4, ud.getProperty());
				
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

	}

	public String getAuditPeopleByProjectId(String ProjectId) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			String sql = "select Value from k_userdef where ContrastID=? and Name='二审人员'";
			ps = conn.prepareStatement(sql);
			ps.setString(1, ProjectId);
			String persons = "";

			rs = ps.executeQuery();

			while (rs.next()) {

				persons += rs.getString("Value") + ",";
			}

			return UTILString.killEndToken(persons, ",");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		return "";

	}

	public String getLonelyPeopleByProjectId(String ProjectId) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			String sql = "select Value from k_userdef where ContrastID=? and Name='独立审计人员'";
			ps = conn.prepareStatement(sql);
			ps.setString(1, ProjectId);
			String persons = "";

			rs = ps.executeQuery();

			while (rs.next()) {

				persons += rs.getString("Value") + ",";
			}

			return UTILString.killEndToken(persons, ",");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		return "";

	}

	public Userdef getUserDefByNameAndValue(String Name, String Value) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		Userdef userdef = new Userdef();

		try {
			String sql = "select *	 from k_userdef where Name=? and Value=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, Name);
			ps.setString(2, Value);
			rs = ps.executeQuery();

			if (rs.next()) {
				userdef.setId(rs.getInt("id"));
				userdef.setContrastid(rs.getString("ContrastID"));
				userdef.setName(rs.getString("Name"));
				userdef.setValue(rs.getString("Value"));
				userdef.setProperty(rs.getString("Property"));

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		return userdef;

	}
	
	public String getValueByNameAndProperty(String Name, String property) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select value from k_userdef where Name=? and property=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, Name);
			ps.setString(2, property);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString(1) ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		return "";

	}
	
	public String getValue(String Name, String property,String contrastID) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select value from k_userdef where Name=? and property=? and contrastID=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, Name);
			ps.setString(2, property);
			ps.setString(3, contrastID);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString(1) ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		return ""; 

	}
	
	public String getPrpertyByNameAndValue(String name,String value,String contrastId) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select property from k_userdef where Name=? and value=? and ContrastID=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, name);
			ps.setString(2, value);
			ps.setString(3, contrastId);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString(1) ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		return "";

	}

	

}
