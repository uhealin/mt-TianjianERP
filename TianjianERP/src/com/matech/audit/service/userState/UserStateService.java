package com.matech.audit.service.userState;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.userState.model.UserState;
import com.matech.framework.pub.db.DbUtil;

/**
 * <p>Title: 用户状态service类</p>
 * <p>Description: 保存用户项目最后任务状态</p>
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
 * 2007-8-19
 */
public class UserStateService {
	private Connection conn;
	
	/**
	 * 构造方法,初始化连接
	 * @param conn
	 */
	public UserStateService(Connection conn) throws Exception{
		DbUtil.checkConn(conn);
		this.conn=conn;
	}
	
	/**
	 * 保存用户项目最后打开的任务
	 * @param userState
	 * @return
	 * @throws Exception
	 */
	public int saveUserState(UserState userState) throws Exception {
		DbUtil dbUtl = new DbUtil(conn);
		
		String sql = "insert into asdb.k_userstate(userId,projectId,lastTaskId) "
					+ " values(?,?,?) ";
		
		Object[] params = new Object[]{
				userState.getUserId(),
				userState.getProjectId(),
				userState.getLastTaskId()
		};
		
		return dbUtl.executeUpdate(sql, params);
	}
	
	/**
	 * 获得用户最后状态
	 * @param userId
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public UserState getUserState(String userId, String projectId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		UserState userState = null;
		try {
			String sql = "select autoId,userId,projectId,lastTaskId "
						+ " from asdb.k_userstate "
						+ " where userId=? and projectId=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setString(2, projectId);
			rs = ps.executeQuery();
			
			if(rs.next()) {
				userState = new UserState();
				userState.setAutoId(rs.getString(1));
				userState.setUserId(rs.getString(2));
				userState.setProjectId(rs.getString(3));
				userState.setLastTaskId(rs.getString(4));
			}
			
			return userState;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 获得用户项目最后打开的底稿
	 * @param userId
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public String getLastTaskId(String userId, String projectId) throws Exception {

		String sql = "select lastTaskId "
					+ " from asdb.k_userstate "
					+ " where userId=? and projectId=? ";

		Object[] params = new Object[]{userId,projectId };
		
		return new DbUtil(conn).queryForString(sql, params);
	}
	
	/**
	 * 更新用户最后的状态
	 * @param userId
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public int updateUserState(UserState userState) throws Exception {

		String sql = "update asdb.k_userstate "
					+ " set lastTaskId=? "
					+ " where userId=? and projectId=? ";
		Object[] params = new Object[]{
				userState.getLastTaskId(),
				userState.getUserId(),
				userState.getProjectId()		
		};
		
		return new DbUtil(conn).executeUpdate(sql, params);
	}
	
	/**
	 * 根据项目编号删除用户最后的状态
	 * @param userId
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public int removeUserStateByProjectId(String projectId) throws Exception {

		String sql = "delete from asdb.k_userstate "
					+ " where projectId=? ";
		Object[] params = new Object[]{ projectId };
		
		return new DbUtil(conn).executeUpdate(sql, params);
	}
	
	/**
	 * 根据用户编号删除用户最后的状态
	 * @param userId
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public int removeUserStateByUserId(String userId) throws Exception {

		String sql = "delete from asdb.k_userstate "
					+ " where userId=? ";
		Object[] params = new Object[]{ userId };
		
		return new DbUtil(conn).executeUpdate(sql, params);
	}
}
