package com.matech.audit.pub.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.framework.pub.db.DbUtil;

/**
 * <p>Title: 数据库函数</p>
 * <p>Description: 数据库函数</p>
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
 * 2008-5-5
 */
public class DBFunction {
	private Connection conn = null;
	private String GROUP_CONCAT_DEFAULT_VALUE = "-1";

	/**
	 * 构造方法
	 * @param conn
	 */
	public DBFunction(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 业务分库使用
	 * @param conn
	 * @param customerId
	 */
	public DBFunction(Connection conn, String customerId) {
		this.conn = conn;

		if(!"".equals(customerId) && customerId != null) {
			new DBConnect().changeDataBase(customerId, conn);
		}
	}

	/**
	 * 代替mysql的group_concat方法,该方法sql中无需group_concat函数,程序实现group_concat功能
	 * 例如：groupConcat("select distinct accpackageid from c_account")
	 * @param sql
	 * @return
	 */
	public String groupConcat(String sql) {
		String result = GROUP_CONCAT_DEFAULT_VALUE;

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			//拼接字符串
			while(rs.next()) {
				result += "," + rs.getString(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		//如果为""字符串,就返回默认值
		return result == "" ? GROUP_CONCAT_DEFAULT_VALUE : result;
	}

	/**
	 * 代替mysql的group_concat方法,该方法sql中无需group_concat函数,程序实现group_concat功能;
	 * 例如：groupConcat("select distinct accpackageid from c_account",":::")
	 * @param sql
	 * @param separator 分隔符
	 * @return
	 */
	public String groupConcat(String sql, String separator) {
		String result = GROUP_CONCAT_DEFAULT_VALUE;

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			//拼接字符串
			while(rs.next()) {
				result += separator + rs.getString(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		//如果为""字符串,就返回默认值
		return result == "" ? GROUP_CONCAT_DEFAULT_VALUE : result;
	}

	public static void main(String[] args) {
		Connection conn = null;

		try {
			conn = new DBConnect().getConnect("");
			String sql = " select accpackageid from c_account ";
			String ss = new DBFunction(conn,"100004").groupConcat(sql,"accpackageid");

			System.out.println(ss);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
	}
}
