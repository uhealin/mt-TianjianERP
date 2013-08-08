package com.matech.audit.service.manuscript;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.manuscript.model.ManuScript;
import com.matech.framework.pub.db.DbUtil;

/**
 * <p>Title: 底稿类</p>
 * <p>Description: 对数据库中的底稿文件信息进行操作</p>
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
public class ManuScriptService {
	private Connection conn = null;

	public ManuScriptService(Connection conn) throws Exception {
		DbUtil.checkConn(conn);
		this.conn = conn;
	}

	/**
	 * 根据UNID获得数据库中底稿信息
	 * @param UNID
	 * @return
	 * @throws Exception
	 */
	public ManuScript getManuScriptByUNID(String UNID) throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		ManuScript manuScript = null;
		try {
			String sql = "select * from asdb.k_ManuscriptData "
						+ " where unid = ? ";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, UNID);
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				manuScript = new ManuScript();
				manuScript.setUNID(rs.getString("unid"));
				manuScript.setMime(rs.getString("mime"));
				manuScript.setUdate(rs.getString("udate"));
				manuScript.setUserId(rs.getString("userid"));
				manuScript.setUserName(rs.getString("username"));
				
				manuScript.setFileName(rs.getString("filename"));
				manuScript.setProjectId(rs.getString("projectid"));
				manuScript.setData(rs.getString("data"));
				manuScript.setProperty(rs.getString("property"));
				manuScript.setId(rs.getString("id"));
				
				manuScript.setPid(rs.getString("pid"));
				manuScript.setTid(rs.getString("tid"));
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		
		return manuScript;
	}
	
	/**
	 * 更新文件名
	 * @param fileName
	 * @param UNID
	 * @return
	 * @throws Exception
	 */
	public int updateFileNameByUNID(String fileName,String UNID) throws Exception {
		DbUtil dbUtil = new DbUtil(conn);
		String sql = "update k_manuscriptdata set "
					+ " FileName = ? "
					+ " where UNID= ? ";
		
		Object[] params = new Object[] {	
				fileName,
				UNID
		};
		
		return dbUtil.executeUpdate(sql, params);
	}
	
	/**
	 * 根据UNID获得数据库中底稿信息
	 * @param UNID
	 * @return
	 * @throws Exception
	 */
	public ManuScript getManuScriptByPrjectIdAndTaskCode(String projectId, String taskCode) throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		ManuScript manuScript = null;
		try {
			
			String sql = "select a.* from k_ManuscriptData a, z_task b "
						+ " where b.TaskCode = ? "
						+ "	and b.projectid= ? "
						+ "	and a.projectid = ? "
						+ "	and a.unid = b.manuid";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, taskCode);
			ps.setString(2, projectId);
			ps.setString(3, projectId);
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				manuScript = new ManuScript();
				manuScript.setUNID(rs.getString("unid"));
				manuScript.setMime(rs.getString("mime"));
				manuScript.setUdate(rs.getString("udate"));
				manuScript.setUserId(rs.getString("userid"));
				manuScript.setUserName(rs.getString("username"));
				
				manuScript.setFileName(rs.getString("filename"));
				manuScript.setProjectId(rs.getString("projectid"));
				manuScript.setData(rs.getString("data"));
				manuScript.setProperty(rs.getString("property"));
				manuScript.setId(rs.getString("id"));
				
				manuScript.setPid(rs.getString("pid"));
				manuScript.setTid(rs.getString("tid"));
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		
		return manuScript;
	}
}
