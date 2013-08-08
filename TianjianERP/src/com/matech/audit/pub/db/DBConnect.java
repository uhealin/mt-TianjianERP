package com.matech.audit.pub.db;

import java.sql.Connection;
import java.sql.Statement;

import com.matech.framework.multidb.MysqlAction;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.Debug;

/**
 * <p>Title: 数据库连接类</p>
 * <p>Description: 获得连接,切换数据库</p>
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
 * 2007-8-11
 */
public class DBConnect {
	private static final String USERNAME = "xoops_root";

	private static final String PASSWORD = "654321";


	/**
	 * 不论是否存在连接池，都获取直接的数据库连接
	 *
	 * @return Connection
	 * @throws Exception
	 *             departID : 为空则取系统库。否则取客户编号的数据库。
	 */
	public Connection getDirectConnect(String departID) throws Exception {
		try {
			Connection DBConn = new com.matech.framework.pub.db.DBConnect(
					USERNAME, PASSWORD).getDirectConnect();

			this.changeDataBase(departID, DBConn);

			return DBConn;
		} catch (Exception e) {
			Debug.print(Debug.iError, "创建数据库连接失败", e);
			return null;
		}
	}

	/**
	 * 不论是否存在连接池，都获取直接的数据库连接,备份恢复使用,默认连接到asdb数据库
	 *
	 * @param databaseName
	 *            连接到的数据库名
	 * @param flag
	 *            标记,暂时无用
	 * @return
	 * @throws Exception
	 */
	public Connection getDirectConnect(String databaseName, boolean flag)
			throws Exception {
		try {
			String dbname = databaseName;
			if ("".equals(dbname))
				dbname = "asdb";

			return new com.matech.framework.pub.db.DBConnect(USERNAME,
					PASSWORD).getDirectConnect();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			return null;
		}
	}

	/**
	 * 根据客户编号获得连接
	 * @param departID
	 * @return
	 * @throws Exception
	 */
	public Connection getConnect(String departID) throws Exception {
		try {
			Connection DBConn = new com.matech.framework.pub.db.DBConnect(
					USERNAME, PASSWORD).getConnect();

			// 分库处理
			changeDataBase(departID, DBConn);

			return DBConn;

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			return null;
		}
	}
	
	/**
	 * 获取连接
	 * @param departID
	 * @return
	 * @throws Exception
	 */
	public Connection getConnect() throws Exception {
		try {
			Connection DBConn = new com.matech.framework.pub.db.DBConnect(
					USERNAME, PASSWORD).getConnect();

			return DBConn;

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			return null;
		}
	}

	/**
	 * 返回statement
	 * @param DBConn
	 * @return
	 */
	public Statement createStatement(Connection DBConn) {
		try {
			return DBConn.createStatement(
					java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE,
					java.sql.ResultSet.CONCUR_READ_ONLY);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据客户编号转换数据库
	 *
	 * @param conn
	 *            Connection
	 * @param departID
	 *            String
	 * @return boolean
	 */
	public boolean changeDataBase(String departID, Connection conn) {

		try {
			DbUtil db = new DbUtil(conn);
			DbUtil.checkConn(conn);
			String dbName = "asdb";

			if (departID == null || "".equals(departID) || "0".equals(departID)) {
				db.execute("use " + dbName);
			} else {
				
				try {
					db.execute("use " + dbName +"_" + departID);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Debug.print(Debug.iError, "切换数据库出错", e);
			return false;
		}
	}

	/**
	 * 根据项目编号转换数据库 因为很多已有的方法都是conn在前面，所以多态了这个方法
	 *
	 * @param conn
	 *            Connection
	 * @param strProjectid
	 *            String
	 * @return boolean
	 */
	public boolean changeDataBaseByProjectid(Connection conn,
			String strProjectid) {
		return changeDataBaseByProjectid(strProjectid, conn);
	}

	/**
	 * 根据项目编号切换数据库
	 *
	 * @param strProjectid
	 * @param conn
	 * @return
	 */
	public boolean changeDataBaseByProjectid(String strProjectid,
			Connection conn) {

		try {
			DbUtil db = new DbUtil(conn);

			Object[] params = new Object[] { strProjectid };
			String sql = "select customerid from z_project where projectid=?";

			String strDepartid = db.queryForString(sql, params);
			;
			String dbName = "asdb";

			if (strDepartid == null || "".equals(strDepartid)
					|| "0".equals(strDepartid)) {
				db.execute("use " + dbName);
			} else {
				//db.execute("use " + dbName + "_" + strDepartid);
				
				changeDataBase(strDepartid, conn);
			}
			return true;
		} catch (Exception e) {
			Debug.print(Debug.iError, "根据projectId切换数据库出错,projectId="
					+ strProjectid, e);
			return false;
		}
	}

	/**
	 * 根据账套编号转换数据库
	 * @param accPackageId
	 * @param conn
	 * @return
	 */
	public boolean changeDataBaseByAccPackageId(String accPackageId, Connection conn) {

		try {

			String customerId = accPackageId.substring(0, 6);

			changeDataBase(customerId, conn);
			return true; 
		} catch (Exception e) {
			Debug.print(Debug.iError, "根据账套ID切换数据库出错,accPackageId=" + accPackageId, e);
			return false;
		}
	}

	public static void main(String[] args) throws Exception {
		DBConnect conn = new DBConnect();
		Connection c = conn.getConnect("");
		System.out.println(c);
		conn.changeDataBaseByAccPackageId("1000012002", c);
		System.out.println(c);
		conn.changeDataBaseByProjectid(c, "2009922");
		System.out.println(c);
		conn.changeDataBaseByProjectid("2009922",c);
		System.out.println(c);
	}
}
