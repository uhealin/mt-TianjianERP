package com.matech.audit.pub.func;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>Title: 数据系统类</p>
 * <p>Description: 用于重建所有业务库视图,更新所有业务库等</p>
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
 * 2007-9-2
 */
public class MatechSystem {

	private String ip; //数据库IP地址
	private String port; //数据库端口号

	private int startDB = 000000;

	private int endDB = 999999;

	public int getEndDB() {
		return endDB;
	}

	public void setEndDB(int endDB) {
		this.endDB = endDB;
	}

	public MatechSystem(String ip, String port) throws Exception {
		if (ip == null || "".equals(ip)) {
			throw new Exception("IP地址不能为空,ip=" + ip);
		}

		if (port == null || "".equals(port)) {
			throw new Exception("端口不能为空,port=" + port);
		}
		this.ip = ip;
		this.port = port;
	}

	private Connection getConnection() throws Exception {
		String url = "jdbc:mysql://" + this.ip + ":" + this.port
					+ "/asdb?characterEncoding=GBK";
		String userName = "xoops_root";
		String password = "654321";

		Class.forName("com.mysql.jdbc.Driver");
		return DriverManager.getConnection(url, userName, password);
	}

	private Connection getConnection(String databaseName) throws Exception {
		String url = "jdbc:mysql://" + this.ip + ":" + this.port
					+ "/"+databaseName+"?characterEncoding=GBK";
		String userName = "xoops_root";
		String password = "654321";

		Class.forName("com.mysql.jdbc.Driver");
		return DriverManager.getConnection(url, userName, password);
	}

	/**
	 * 重建试图
	 * @throws Exception
	 */
	public void createOrReplaceView(HttpServletResponse response) throws Exception {
		PrintWriter writer=response.getWriter();

		Connection conn = null;
		Statement stmt = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			conn = getConnection();
			//找出系统库的所有表
			String sql = "select TABLE_NAME from "
					+ " asdb.TABLES "
					+ " where TABLE_TYPE='BASE TABLE' and TABLE_SCHEMA = 'asdb'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			List sqlList = new ArrayList();

			writer.println("<font color=blue>正在填充需要执行的SQL...</font><br>");
			while (rs.next()) {
				sql = "create or replace view " + rs.getString(1) + " as "
						+ " select * from asdb." + rs.getString(1);

				writer.println("<b>"+sql+"</b><br>");
				sqlList.add(sql);
			}

			writer.println("<font color=blue>SQL填充完毕,等待执行。</font><br>");

			//找出所有非系统库
			sql = " select departId from asdb.k_customer ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			stmt = conn.createStatement();

			while (rs.next()) {
				
				try {
					writer.println("<font color=blue>正在更新 " + rs.getString(1) + " 库...</font><br>");
					sql = "use asdb_" + rs.getString(1);
					stmt.execute(sql);

					for (int i = 0; i < sqlList.size(); i++) {
						//writer.println("<font color=red>正在执行：</font><b>" + sqlList.get(i)+"</b><br>");
						stmt.execute((String) sqlList.get(i));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			writer.println("<font color=blue>更新完毕</font><br>");


		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			rs.close();
			ps.close();
			stmt.close();
			conn.close();
		}
	}
	/**
	 * 重建试图
	 * @throws Exception
	 */
	public void createOrReplaceView() throws Exception {


		Connection conn = null;
		Statement stmt = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			conn = getConnection();
			//找出系统库的所有表
			String sql = "select TABLE_NAME from "
					+ " asdb.TABLES "
					+ " where TABLE_TYPE='BASE TABLE' and TABLE_SCHEMA = 'asdb'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			List sqlList = new ArrayList();

			System.out.println("正在填充需要执行的SQL...");
			while (rs.next()) {
				sql = "create or replace view " + rs.getString(1) + " as "
						+ " select * from asdb." + rs.getString(1);

				System.out.println("sql");
				sqlList.add(sql);
			}

			System.out.println("SQL填充完毕,等待执行");

			//找出所有非系统库
			sql = " select departId from asdb.k_customer ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			stmt = conn.createStatement();

			while (rs.next()) {

				try {
					System.out.println("正在更新 " + rs.getString(1) + " 库...");
					sql = "use asdb_" + rs.getString(1);
					stmt.execute(sql);

					for (int i = 0; i < sqlList.size(); i++) {
						//writer.println("<font color=red>正在执行：</font><b>" + sqlList.get(i)+"</b><br>");
						stmt.execute((String) sqlList.get(i));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}

			System.out.println("更新完毕");


		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			rs.close();
			ps.close();
			stmt.close();
			conn.close();
		}
	}

	/**
	 * 更新业务库
	 * @param sql
	 * @throws Exception
	 */
	public void update(String[] sql,HttpServletResponse response) throws Exception  {
		PrintWriter writer=response.getWriter();

		Connection conn = null;
		Statement stmt = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			conn = getConnection();


			//找出所有非系统库
			String strSql = " select departId from asdb.k_customer where departId > ? and departId < ? ";

			ps = conn.prepareStatement(strSql);
			ps.setInt(1, this.startDB);
			ps.setInt(2, this.endDB);

			rs = ps.executeQuery();

			stmt = conn.createStatement();

			while (rs.next()) {

				try {
					writer.println("<font color=blue>正在更新 " + rs.getString(1) + " 库...</font><br>");
					writer.flush();
					strSql = "use asdb_" + rs.getString(1);
					stmt.execute(strSql);
					for (int i = 0; i < sql.length; i++) {
						writer.println("<font color=blue>正在执行：</font><b>" + sql[i]+"</b><br>");
						writer.flush();

						try {
							stmt.execute(sql[i]);
						} catch (Exception e) {
							writer.println("<font color=red>出错:" + e.getMessage()+"</font><br>");
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}

			writer.println("<font color=blue>更新完毕</font><br>");


		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			rs.close();
			ps.close();
			stmt.close();
			conn.close();
			writer.close();
		}
	}

	/**
	 * 更新系统库
	 * @param sql
	 * @throws Exception
	 */
	public void updateSystem(String[] sql,HttpServletResponse response) throws Exception  {
		PrintWriter writer=response.getWriter();

		Connection conn = null;
		Statement stmt = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			conn = getConnection();


			String strSql = "use asdb";
			stmt.execute(strSql);
			
			for (int i = 0; i < sql.length; i++) {
				writer.println("<font color=red>正在执行：</font><b>" + sql[i]+"</b><br>");

				try {
					stmt.execute(sql[i]);
				} catch (Exception e) {
					writer.println("<font color=red>出错:" + e.getMessage()+"</font><br>");
				}
			}

			writer.println("<font color=blue>更新完毕</font><br>");


		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			rs.close();
			ps.close();
			stmt.close();
			conn.close();
		}
	}


	/**
	 * 更新业务库
	 * @param sql
	 * @throws Exception
	 */
	public void update(String[] sql) throws Exception  {

		Connection conn = null;
		Statement stmt = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			conn = getConnection();

			//找出所有非系统库
			String strSql = "select departId from asdb.k_customer where departId > ? and departId < ? ";

			ps = conn.prepareStatement(strSql);
			ps.setInt(1, this.startDB);
			ps.setInt(2, this.endDB);

			rs = ps.executeQuery();

			stmt = conn.createStatement();

			while (rs.next()) {
				try {
					System.out.println("正在更新 " + rs.getString(1) + " 库...");
	
					strSql = "use asdb_" + rs.getString(1);
					stmt.execute(strSql);
					for (int i = 0; i < sql.length; i++) {
						System.out.println("正在执行：" + sql[i]+"");
	
						try {
							System.out.println("更新了:" + stmt.executeUpdate(sql[i]) + "条记录!!");
						} catch (Exception e) {
							System.err.println("出错:" + e.getMessage());
						}
	
					}
				
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			System.out.println("更新完毕");


		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			rs.close();
			ps.close();
			stmt.close();
			conn.close();
		}
	}

	/**
	 * 获得执行的sql语句字符串
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public String getExecSqlString(String filePath) throws Exception {
		StringBuffer sb = new StringBuffer();

		BufferedReader reader = null;
		String line;

		File tempFile = new File(filePath);
		if(!tempFile.exists()) {
			throw new Exception("文件不存在,请检查!!");
		}

		try {

			reader = new BufferedReader(new FileReader(tempFile));

			boolean first = true;
			while ((line = reader.readLine()) != null) {
				if(first) {
					sb.append("   \" " + line + " \" \n");
					first = false;
				} else {
					sb.append(" + \" " + line + " \" \n");
				}

				if(line.equals("")) {
					sb.append(", \n");
					first = true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(reader!=null) {
				reader.close();
			}
		}

		return sb.toString();
	}
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public int getStartDB() {
		return startDB;
	}

	public void setStartDB(int startDB) {
		this.startDB = startDB;
	}

	public static void main(String[] args) throws Exception {
		MatechSystem matechSystem = new MatechSystem("127.0.0.1", "5188");

		matechSystem.setStartDB(100000);	//开始客户编号
		matechSystem.setEndDB(100000);	//结束客户编号
		//System.out.println(matechSystem.getExecSqlString("c:/sql.txt"));

		String[] sql = new String[]{
				   " update k_user set loginid=1 where 1=2 "


		};
		matechSystem.update(sql);
	}
}
