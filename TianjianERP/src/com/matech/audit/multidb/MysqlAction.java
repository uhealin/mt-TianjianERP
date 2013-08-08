package com.matech.audit.multidb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.datamanage.DataZip;
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.sys.UTILSysProperty;
import com.matech.framework.pub.util.Debug;

public class MysqlAction extends com.matech.framework.multidb.MysqlAction
		implements MultiDbIF {
	public MysqlAction() {
	}

	/**
	 * 清空数据
	 * @param databaseName
	 * @return
	 * @throws Exception
	 */
	public boolean truncateDateBase(String databaseName) throws Exception {
		if(databaseName == null || "".equals(databaseName)) {
			return false;
		}

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Statement stmt = null;

		try {

			conn = new DBConnect().getConnect("");

			String sql = "	select TABLE_SCHEMA,TABLE_NAME from asdb.TABLES "
					   + " where TABLE_SCHEMA=? "
					   + " and table_type='BASE TABLE'" ;

			ps = conn.prepareStatement(sql);
			ps.setString(1, databaseName);
			rs = ps.executeQuery();

			stmt = conn.createStatement();
			while(rs.next()) {
				sql = " truncate table " + rs.getString(1) + "." + rs.getString(2);
				System.out.println("清空表：" + sql);
				stmt.execute(sql);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(stmt);
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}

		return true;

	}

	/**
	 * 创建客户数据库
	 *
	 * @param conn Connection
	 * @throws MatechException
	 */
	public boolean createCustomerDb(String departID) throws MatechException {
		try {
			String path, databasePath;

			path = org.del.DelPublic.getClassRoot();
			path = path.substring(0, path.indexOf("/WEB-INF/classes/")) + "/DatabaseManage/asdbtemplate";

			databasePath = UTILSysProperty.SysProperty.getProperty("DBPath");

			if (databasePath == null || "".equals(databasePath)) {
				databasePath = org.del.DelPublic.getClassRoot();
				if(databasePath.indexOf("webRoot")>-1){
					
					databasePath = databasePath.substring(0, databasePath.indexOf("/webRoot/")) + "/Database/Data";
				}else{
					databasePath = databasePath.substring(0, databasePath.indexOf("/AuditSystem/")) + "/Database/Data";
				}
			} else {
				databasePath += "/Data";
			}

			// org.del.DelPublic.copyDir2(path,databasePath+"/asdb_"+departID);

			new com.matech.audit.service.datamanage.DataZip().unZip(path.substring(1) + ".zip", databasePath + "/asdb_" + departID, false);
			org.util.Debug.prtErr(databasePath);
			return true;
		} catch (Exception e) {
			Debug.print(Debug.iError, "创建数据库错误", e);
			return false;
		}
	}

	/**
	 * 遍历所有业务库，并执行SQL
	 *
	 * @param conn
	 *            Connection
	 * @param strSql
	 *            String
	 * @return boolean
	 */
	public boolean executeSqlAtAllDb(Connection conn, String strSql)
			throws MatechException {

		DbUtil.checkConn(conn);

		Statement queryst = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			queryst = conn.createStatement();
			st = conn.createStatement();
			rs = queryst.executeQuery(" select departId from asdb.k_customer ");

			while (rs.next()) {
				try {
					st.execute("use asdb_" + rs.getString(1));
					st.execute(strSql);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
			DbUtil.close(queryst);
		}

	}

	/**
	 * 删除所有的业务数据库
	 * @param conn
	 * @throws MatechException
	 */
	public void dropAllYwDb(Connection conn) throws MatechException {
		DbUtil.checkConn(conn);
		Statement stmt = null, stmt1 = null;
		ResultSet rs = null;
		try {
			String sql = " select departId from asdb.k_customer ";
			stmt = conn.createStatement();
			stmt1 = conn.createStatement();
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				sql = "DROP database IF EXISTS asdb_" + rs.getString(1);
				stmt1.execute(sql);
				System.out.println("执行sql="+sql);
				ManuFileService.deleteDirByCustomerID(rs.getString(1));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(stmt);
			DbUtil.close(stmt1);
		}
	}


	/**
	 * 创建客户数据库
	 *
	 * @param sourceDatabaseName
	 *            String
	 * @param target
	 *            String
	 * @return boolean
	 * @throws MatechException
	 */
	public boolean createCustomerDb(String sourceDatabaseName, String target)
			throws MatechException {
		try {
			String path, databasePath;

			// /D:/project/project2.16/AuditSystem2.16/AuditSystem/DatabaseManage/asdbtemplate
			path = org.del.DelPublic.getClassRoot();
			path = path.substring(0, path.indexOf("/WEB-INF/classes/"))
					+ "/DatabaseManage/" + sourceDatabaseName;

			// databasePath="C:/E审通2.15/E审通系统/Database/Data";
			databasePath = UTILSysProperty.SysProperty.getProperty("DBPath");

			if (databasePath == null || "".equals(databasePath)) {
				// C:\E审通2.15\E审通系统\Database\Data

				databasePath = org.del.DelPublic.getClassRoot();
				databasePath = databasePath.substring(0, databasePath.indexOf("/webRoot/")) + "/Database/Data";
			} else {
				databasePath += "/Data";
			}

			// org.del.DelPublic.copyDir2(path,databasePath+"/"+target);

			new DataZip().unZip(path.substring(1) + ".zip", databasePath + "/" + target, false);

			return true;
		} catch (Exception e) {
			Debug.print(Debug.iError, "创建数据库错误", e);
			return false;
		}

	}

	/**
	 * 拷贝文件夹或者文件
	 * 例如：copyFiles(new File("d:/111"), new File("e:/444"));
	 * 		将 d:/111 目录下的所有文件及文件夹拷贝到 e:/444 目录下
	 * @param oldDir
	 * @param newDir
	 * @return
	 * @throws Exception
	 */
	public void copyFiles(File oldDir, File newDir) throws Exception {
		if (oldDir.isDirectory()) {
			newDir.mkdirs();
			File[] files = oldDir.listFiles();
			for (int i = 0; i < files.length; i++) {
				File file = new File(newDir.getPath() + "/"
						+ files[i].getName());
				copyFiles(files[i], file);
			}
		} else {
			FileInputStream input = new FileInputStream(oldDir);
			FileOutputStream output = new FileOutputStream(newDir);
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = input.read(b)) != -1) {
				output.write(b, 0, len);
			}
			output.flush();
			output.close();
			input.close();
		}
	}

}
