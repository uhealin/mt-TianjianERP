package com.matech.audit.service.accpackageext;

import java.sql.*;

import com.matech.framework.pub.db.DbUtil;

/**
 * 
 * <p>Title: 帐套常量</p>
 * <p>Description: TODO</p>
 * <p>Copyright: Copyright (c) 2006, 2008 MaTech Corporation.All rights reserved. </p>
 * <p>Company: Matech  广州铭太信息科技有限公司</p>
 * 
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有，
 * 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 *
 * <p>贡献者团队:
 *     铭太科技 - 研发中心，审计开发组
 *
 * @author LuckyStar
 * 2008-1-3
 */


public class AccPackageExtService {
	
	private Connection conn = null;
	
	public AccPackageExtService(Connection conn) {
		this.conn = conn;	
	}
	
	public void save(String accpackageid,String ctype,String value,String property) throws Exception{
		PreparedStatement ps = null;
		try {
			String sql = "insert into c_accpackageext (AccPackageID, ctype, value, Property) values (?, ?, ?, ?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, accpackageid);
			ps.setString(2, ctype);
			ps.setString(3, value);
			ps.setString(4, property);
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	public String get(String accpackageid,String ctype )throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String result = "";
			
			String sql = "select * from c_accpackageext where AccPackageID=? and ctype=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, accpackageid);
			ps.setString(2, ctype);
			rs = ps.executeQuery();
			if(rs.next()){
				result = rs.getString("value");
			}
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public void del(String accpackageid,String ctype )throws Exception{
		PreparedStatement ps = null;
		try {
			String sql = "delete from c_accpackageext where AccPackageID=? and ctype=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, accpackageid);
			ps.setString(2, ctype);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	
	/**
	 * 自定义的帐套常量
	 */
	
	/**
	 * 插入allsubjectid值 ：在凭证分录表c_subjectentry的所有科目ID, 例：'1001','1002'
	 */
	public String subjectentry(String accpackageid)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String result = "";
			
			String sql = "select group_concat(distinct \"'\", subjectid,\"'\") from c_subjectentry where  AccPackageID=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, accpackageid);
			rs = ps.executeQuery();
			if(rs.next()){
				result = rs.getString(1);
			}
			save(accpackageid,"allsubjectid",result,"");
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
}
