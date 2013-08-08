package com.matech.audit.service.auditPeople;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.auditPeople.model.AuditPeople;
import com.matech.audit.service.user.UserService;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

/**
 * <p>Title: 审计人员</p>
 * <p>Description: 提供审计人员查找等操作</p>
 * <p>Copyright: Copyright (c) 2006, 2008 MaTech Corporation.All rights reserved. </p>
 * <p>Company: Matech  广州铭太信息科技有限公司</p>
 *
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有，
 * 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 *
 * 贡献者团队:
 *     铭太科技 - 研发中心，审计开发组
 *
 * @author void
 * 2007-6-30
 */
public class AuditPeopleService {
	
	private Connection conn = null;

	private String projectId;

	/**
	 * 构造方法
	 * @param conn 数据库连接
	 * @param projectId 项目编号
	 * @throws Exception
	 */
	public AuditPeopleService(Connection conn, String projectId) throws Exception {
		DbUtil.checkConn(conn);
		this.conn = conn;

		if("".equals(projectId) || projectId == null) {
			throw new Exception("项目Id不能为空!");
		}

		this.projectId = projectId;
	}

	/**
	 * 返回有权审核的所有审计人员ID
	 * 原来方法名为getAuditPeopleNameByPid(String projectId)
	 * @return
	 * @throws Exception
	 */
	public List getAuditPeopleId() throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		List peopleList = null;

		try {
			peopleList = new ArrayList();
			String strSql;

			strSql = "select DISTINCT(userid)"
					+ " from asdb.z_auditpeople "
					+ " where isAudit = '1' "
					+ " and projectId = ? ";

			ps = conn.prepareStatement(strSql);
			ps.setString(1, this.projectId);
			rs = ps.executeQuery();

			while(rs.next()) {
				peopleList.add(rs.getString(1));
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return peopleList;
	}

	/**
	 * 判断用户是否属于项目组成员
	 * @param projectId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public boolean isAuditPeople(String userId) throws Exception {

		DbUtil dbUtil = new DbUtil(conn);
		String strSql = " select 1 from z_auditpeople "
						+ " where projectId = ? "
						+ " and userid = ?";

		Object[] params = new Object[]{this.projectId, userId};

		return dbUtil.queryForString(strSql, params) != null;
	}


	/**
	 * 判断用户是否有权复核
	 * @param projectId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public boolean hasAudit(String userId) throws Exception {
		DbUtil dbUtil = new DbUtil(conn);
		String strSql = " select 1 from z_auditpeople "
						+ " where projectId = ? "
						+ " and userid = ? "
						+ " and isaudit=1 ";

		Object[] params = new Object[]{this.projectId, userId};

		return dbUtil.queryForString(strSql, params) != null;
	}

	/**
	 * 判断用户是否有审计程序与目标维护的权限
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public boolean isTarAndPro(String userId) throws Exception {
		DbUtil dbUtil = new DbUtil(conn);
		String strSql = " select 1 from z_auditpeople "
						+ " where projectId = ? "
						+ " and userid = ? "
						+ " and istarandpro=1 ";

		Object[] params = new Object[]{this.projectId, userId};

		return dbUtil.queryForString(strSql, params) != null;
	}

	/**
	 * 获取拥有程序与目标维护权限的人员
	 * @return
	 * @throws Exception
	 */
	public String getTarAndPro() throws Exception {
		DbUtil dbUtil = new DbUtil(conn);
		String strSql = " select ifnull(group_concat(name),'暂无人员有权维护程序与目标,请在分工那里指定维护人员！！') from z_auditpeople a, k_user b "
						+ " where projectId = ? "
						+ " and istarandpro=1 "
						+ " and a.userid=b.id ";

		Object[] params = new Object[]{this.projectId};

		return dbUtil.queryForString(strSql, params);
	}


	/**
	 * 获取用户项目角色
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public String getAuditPeopleRole(String userId) throws Exception {
		DbUtil dbUtil = new DbUtil(conn);
		String strSql = " select role from z_auditpeople "
						+ " where projectId = ? "
						+ " and userid = ? ";

		Object[] params = new Object[]{this.projectId, userId};

		return dbUtil.queryForString(strSql, params);
	}
	
	/**
	 * 获取审计人员列表
	 * @return
	 * @throws Exception
	 */
	public List getAuditPeopleList() throws Exception {

		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		List list = new ArrayList();
		
		try {
			String strSql = " select projectid,userid, Role, IsAudit, departmentid, istarandpro from z_auditpeople "
				 		  + " where projectid=? and role not in('签字合伙人','复核合伙人','项目负责人')";
			
			ps = conn.prepareStatement(strSql);
			ps.setString(1, this.projectId);
			rs = ps.executeQuery();
			
			AuditPeople auditPeople;
			UserService userService = new UserService(conn);
			while(rs.next()) {
				auditPeople = new AuditPeople();
				
				auditPeople.setIsAudit(rs.getString("IsAudit"));
				auditPeople.setIsTarAndPro(rs.getString("istarandpro"));
				auditPeople.setDepartmentId(rs.getString("departmentid"));
				auditPeople.setRole(rs.getString("Role"));
				auditPeople.setUserId(rs.getString("userid"));
				auditPeople.setUser(userService.getUser(rs.getString("userid"),"id"));
				
				list.add(auditPeople);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return list;
	}
	
	/**
	 * 保存审计人员
	 * @param list
	 * @throws Exception
	 */
	public void saveAuditPeople(List list) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			String sql = " delete from z_auditpeople where projectid=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);
			ps.executeUpdate();
			
			sql = " insert into z_auditpeople(projectid,userid, Role, IsAudit, departmentid, istarandpro) "
				+ " values(?,?,?,?,?,?) ";
			ps = conn.prepareStatement(sql);
			
			
			AuditPeople auditPeople;
			for(int i=0; i < list.size(); i++) {
				auditPeople = (AuditPeople)list.get(i);
	
				ps.setString(1, this.projectId);
				ps.setString(2, auditPeople.getUserId());
				ps.setString(3, auditPeople.getRole());
				ps.setString(4, auditPeople.getIsAudit());
				ps.setString(5, auditPeople.getDepartmentId());
				ps.setString(6, auditPeople.getIsTarAndPro());
				
				ps.executeUpdate();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public void addOrUpdateAuditPeople(AuditPeople auditPeople) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			
			String sql = "select 1 from z_auditpeople where projectId=? and role=?";
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, this.projectId);
			ps.setString(2, auditPeople.getRole());
			
			rs = ps.executeQuery();
			
			if(rs.next()) {
				updateAuditPeople(auditPeople) ;
			}else {
				addAuditPeople(auditPeople);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public void addOrUpdateAuditPeopleSync(AuditPeople auditPeople) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			
			String sql = "select 1 from z_auditpeople_sync where projectId=? and role=?";
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, this.projectId);
			ps.setString(2, auditPeople.getRole());
			
			rs = ps.executeQuery();
			
			if(rs.next()) {
				updateAuditPeopleSync(auditPeople) ;
			}else {
				addAuditPeopleSync(auditPeople);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 保存审计人员
	 * @param list
	 * @throws Exception
	 */
	public void addAuditPeopleSync(AuditPeople auditPeople) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			
			String sql = " insert into z_auditpeople_sync(projectid,userid, Role, IsAudit, departmentid, istarandpro,appointdate) "
				+ " values(?,?,?,?,?,?,?) ";
			ps = conn.prepareStatement(sql);

			ps.setString(1, this.projectId);
			ps.setString(2, auditPeople.getUserId());
			ps.setString(3, auditPeople.getRole());
			ps.setString(4, auditPeople.getIsAudit());
			ps.setString(5, auditPeople.getDepartmentId());
			ps.setString(6, auditPeople.getIsTarAndPro());
			ps.setString(7,	auditPeople.getAppointdate()) ;
			ps.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public void updateAuditPeopleSync(AuditPeople auditPeople) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			
			String sql = "update z_auditpeople_sync set projectid=?,userid=?,Role=?,IsAudit=?,departmentid=?,istarandpro=?,appointdate=? where projectId=? and role=?";
			ps = conn.prepareStatement(sql);

			ps.setString(1, this.projectId);
			ps.setString(2, auditPeople.getUserId());
			ps.setString(3, auditPeople.getRole());
			ps.setString(4, auditPeople.getIsAudit());
			ps.setString(5, auditPeople.getDepartmentId());
			ps.setString(6, auditPeople.getIsTarAndPro());
			ps.setString(7, auditPeople.getAppointdate()) ;
			ps.setString(8,this.projectId);
			ps.setString(9, auditPeople.getRole());
			
			ps.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 保存审计人员
	 * @param list
	 * @throws Exception
	 */
	public void addAuditPeople(AuditPeople auditPeople) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			
			String sql = " insert into z_auditpeople(projectid,userid, Role, IsAudit, departmentid, istarandpro,appointdate) "
				+ " values(?,?,?,?,?,?,?) ";
			ps = conn.prepareStatement(sql);

			ps.setString(1, this.projectId);
			ps.setString(2, auditPeople.getUserId());
			ps.setString(3, auditPeople.getRole());
			ps.setString(4, auditPeople.getIsAudit());
			ps.setString(5, auditPeople.getDepartmentId());
			ps.setString(6, auditPeople.getIsTarAndPro());
			ps.setString(7,	auditPeople.getAppointdate()) ;
			ps.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public void updateAuditPeople(AuditPeople auditPeople) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			
			String sql = "update z_auditpeople set projectid=?,userid=?,Role=?,IsAudit=?,departmentid=?,istarandpro=?,appointdate=? where projectId=? and role=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);
			ps.setString(2, auditPeople.getUserId());
			ps.setString(3, auditPeople.getRole());
			ps.setString(4, auditPeople.getIsAudit());
			ps.setString(5, auditPeople.getDepartmentId());
			ps.setString(6, auditPeople.getIsTarAndPro());
			ps.setString(7, auditPeople.getAppointdate()) ;
			ps.setString(8,this.projectId);
			ps.setString(9, auditPeople.getRole());
			ps.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 保存其他合伙人
	 * @param list
	 * @throws Exception
	 */
	public void saveOtherAuditPeople(Map map) throws Exception {
		PreparedStatement ps = null;

		try {
			String sql = " delete from z_auditpeople where projectid=? and role in('签字合伙人','复核合伙人','项目负责人')";
			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);
			ps.executeUpdate();
			
			sql = " insert into z_auditpeople(projectid,userid, Role, IsAudit, departmentid, istarandpro) "
				+ " values(?,?,?,0,0,0) ";
			ps = conn.prepareStatement(sql);
			
			ASFuntion funtion = new ASFuntion();
			
			String otherUser1 = funtion.showNull((String)map.get("签字合伙人"));
			String otherUser2 = funtion.showNull((String)map.get("复核合伙人"));
			String otherUser3 = funtion.showNull((String)map.get("项目负责人"));
			if(!"".equals(otherUser1)){
				ps.setString(1, this.projectId);
				ps.setString(2, otherUser1);
				ps.setString(3, "签字合伙人");
				ps.executeUpdate();
			}
			
			if(!"".equals(otherUser2)) {
				ps.setString(1, this.projectId);
				ps.setString(2, otherUser2);
				ps.setString(3, "复核合伙人");
				ps.executeUpdate();
			}

			if(!"".equals(otherUser3)) {
				ps.setString(1, this.projectId);
				ps.setString(2, otherUser3);
				ps.setString(3, "项目负责人");
				ps.executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 保存其他合伙人
	 * @param list
	 * @throws Exception
	 */
	public Map getOtherAuditPeople() throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Map map = new HashMap();
		try {
			String sql = " select userId,role "
						+ " from z_auditpeople "
						+ " where projectid=? and role in('签字合伙人','复核合伙人','项目负责人')";
			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);
			
			rs = ps.executeQuery();
			
			while(rs.next()) {
				map.put(rs.getString(2), rs.getString(1));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return map;
	}


	
	/**
	 * 判断是否具有某个角色
	 * @param userId
	 * @param roleName
	 * @return
	 * @throws Exception
	 */
	public String getAuditName (String roleName) throws Exception {

		DbUtil dbUtil = new DbUtil(conn);
		String strSql = " select name from z_auditpeople a"
						+ " left join k_user b on a.userId = b.id"
						+ " where projectId = ? "
						+ " and Role = ?";
		Object[] params = new Object[]{this.projectId,roleName};
		return dbUtil.queryForString(strSql,params);
	}
	
	public String getAuditUserId (String roleName) throws Exception {

		DbUtil dbUtil = new DbUtil(conn);
		String strSql = " select userid from z_auditpeople a"
					  + " where projectId = ? "
					  + " and Role = ?";
		Object[] params = new Object[]{this.projectId,roleName};
		return dbUtil.queryForString(strSql,params);
	}
	
	public void delAuditPeople(String projectId,String role) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "delete from z_auditpeople where projectId=? and role=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);
			ps.setString(2, role);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	public void delAuditPeopleSync(String projectId,String role) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "delete from z_auditpeople_sync where projectId=? and role=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);
			ps.setString(2, role);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	public boolean checkReportRight(Connection conn,String projectId,String officeid){
		boolean flag = false;
		
	try{
		String sql="select a.autoid from k_auditright a,z_project b,z_projectext c  \n"+
		"where a.auditpara = b.auditpara  \n"+
		"and b.projectid = c.projectid \n "+
		" and a.departmentid =" +officeid+
		"  and b.projectid ="+projectId+
		"  and ( (c.s1 = '重大' and a.impReportRight = '是') \n" +
		"or (c.s1 = '非重大' and a.reportRight = '是'  )) ";
		Statement stmt=conn.createStatement();
		ResultSet rs=stmt.executeQuery(sql);
		if(rs.next()){
			 flag= true;
		}
	}catch (Exception e) {
		e.printStackTrace();
	}
	return flag;
	} 
	
		
	}
