package com.matech.audit.service.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.framework.pub.db.DbUtil;

/**
 * <p>Title: 外部项目</p>
 * <p>Description: 外部项目</p>
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
 * 2007-8-13
 */
public class OuterProjectService {
	private Connection conn = null;
	
	public OuterProjectService(Connection conn) throws Exception {
		DbUtil.checkConn(conn);
		this.conn = conn;
	}
	
	/**
	 * 更新审计人员
	 * @param auditPeople
	 * @param autoId
	 * @throws Exception
	 */
	public void updateAuditPeople(String auditPeople,String autoId) throws Exception {
		String sql = " update asdb.z_outerproject "
					+ " set auditpeople=? "
					+ " where autoid= ?";
		Object[] params = new Object[]{auditPeople,autoId};
		new DbUtil(conn).execute(sql, params);
	}
	
	/**
	 * 更新用户权限
	 * @param sqlIn
	 * @throws Exception
	 */
	public void updateUserPopedom(String sqlIn) throws Exception {
		String sql = "update asdb.k_user "
					+ " set popedom=concat(popedom,'125010.') "
					+ " where loginid in(" + sqlIn + ") "
					+ " and popedom not like '%.125010.%'";
		
		DbUtil dbUtil = new DbUtil(conn);
		dbUtil.execute(sql);
		dbUtil.execute("Flush tables");
	}
	
	public String getAuditPeople(String sqlIn) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		// 重新构造auditPeople，带名字和登录名
		String auditPeople = "";
		
		try {
			
//			String sql = "select name,loginid from asdb.k_user "
//						+ " where loginid in(" + sqlIn + ")";
			String sql = "select name,loginid from asdb.k_user "
				+ " where id in(" + sqlIn + ")";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				auditPeople += rs.getString("name") + ","
						+ rs.getString("loginid") + ";";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return auditPeople;
	}
	
	/**
	 * 删除外部项目
	 * @param autoId
	 * @return
	 * @throws Exception
	 */
	public int removeOuterProject(String autoId) throws Exception {
		String sql="delete from asdb.z_outerproject where autoid= ? ";
		Object[] params = new Object[]{autoId};
		
		return new DbUtil(conn).executeUpdate(sql,params);
	}
}
