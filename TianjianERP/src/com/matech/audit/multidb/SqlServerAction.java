package com.matech.audit.multidb;

import com.matech.framework.multidb.MultiDbIF;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.pub.sys.UTILSysProperty;
import java.sql.Connection;
import com.matech.framework.pub.db.DbUtil;
import java.sql.Statement;
import java.sql.ResultSet;


public class SqlServerAction extends com.matech.framework.multidb.SqlServerAction implements MultiDbIF{

    public SqlServerAction() {
    }

    /**
     * 创建客户数据库
     * @param conn Connection
     * @throws MatechException
     */
    public boolean createCustomerDb(String departID) throws MatechException {
        try {
            String path, databasePath;

            path = org.del.DelPublic.getClassRoot();
            path = path.substring(0, path.indexOf("/WEB-INF/classes/")) +
                   "/DatabaseManage/asdbtemplate";

            //databasePath="C:/E审通2.15/E审通系统/Database/Data";
            databasePath = UTILSysProperty.SysProperty.getProperty("DBPath");

            if (databasePath == null || "".equals(databasePath)) {
                //C:\E审通2.15\E审通系统\Database\Data
                databasePath = org.del.DelPublic.getClassRoot();
                databasePath = databasePath.substring(0,
                        databasePath.indexOf("/webRoot/")) + "/Database/Data";
            } else {
                databasePath += "/Data";
            }

            //org.del.DelPublic.copyDir2(path,databasePath+"/asdb_"+departID);

            new com.matech.audit.service.datamanage.DataZip().unZip(path.
                    substring(1) + ".zip", databasePath + "/asdb_" + departID, false);
            org.util.Debug.prtErr(databasePath);
            return true;
        } catch (Exception e) {
            Debug.print(Debug.iError, "创建数据库错误", e);
            return false;
        }
    }

    /**
     * 创建客户数据库
     * @param sourceDatabaseName String
     * @param target String
     * @return boolean
     * @throws MatechException
     */
    public boolean createCustomerDb(String sourceDatabaseName, String target) throws
            MatechException {
        try {
            String path, databasePath;

            ///D:/project/project2.16/AuditSystem2.16/AuditSystem/DatabaseManage/asdbtemplate
            path = org.del.DelPublic.getClassRoot();
            path = path.substring(0, path.indexOf("/WEB-INF/classes/")) +
                   "/DatabaseManage/" + sourceDatabaseName;

            //databasePath="C:/E审通2.15/E审通系统/Database/Data";
            databasePath = UTILSysProperty.SysProperty.getProperty("DBPath");

            if (databasePath == null || "".equals(databasePath)) {
                //C:\E审通2.15\E审通系统\Database\Data

                databasePath = org.del.DelPublic.getClassRoot();
                databasePath = databasePath.substring(0,
                        databasePath.indexOf("/webRoot/")) + "/Database/Data";
            } else {
                databasePath += "/Data";
            }

            //org.del.DelPublic.copyDir2(path,databasePath+"/"+target);

            new com.matech.audit.service.datamanage.DataZip().unZip(path.
                    substring(1) + ".zip", databasePath + "/" + target, false);

            return true;
        } catch (Exception e) {
            Debug.print(Debug.iError, "创建数据库错误", e);
            return false;
        }

    }

    /**
     * 遍历所有业务库，并执行SQL
     * @param conn Connection
     * @param strSql String
     * @return boolean
     */
    public boolean executeSqlAtAllDb(Connection conn,String strSql)
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
    
    public void dropAllYwDb(Connection conn) throws MatechException {
    	/**
    	 * @todo
    	 */
    }
}
