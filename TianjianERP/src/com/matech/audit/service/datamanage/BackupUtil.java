package com.matech.audit.service.datamanage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.del.DelPublic;
import org.del.JRockey2Opp;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.matech.audit.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.pub.util.MD5;

public class BackupUtil {

	protected static String BACKUP_PATH;

	private PrintWriter out;
	
	private boolean isPrint = true;

	//各种备份相关路径
	protected static String ALL_PATH; //全库备份路径
	protected static String BASIC_PATH; //基本数据备份路径
	protected static String POLICY_PATH; //法律法规备份路径
	protected static String CASE_PATH; //案例库备份路径
	protected static String TEMPLATE_PATH; //底稿模板备份路径
	protected static String PROJECT_PATH; //项目备份路径
	protected static String CUSTOMER_PATH; //客户备份路径
	protected static String ACCPACKAGE_PATH; //帐套备份路径
	protected static String DATABASE_PATH; //数据库路径
	protected static String MANUSCRIPT_PATH; //底稿路径

	//数据库修复相关
	protected static String REPAIR_COMMAND; //数据库修复命令

	//各种恢复提示信息
	protected static String ALL_TYPENAME; //全库备份提示信息
	protected static String BASIC_TYPENAME; //基本数据备份提示信息
	protected static String POLICY_TYPENAME; //法律法规备份提示信息
	protected static String CASE_TYPENAME; //案例库备份提示信息
	protected static String TEMPLATE_TYPENAME; //底稿模板提示信息
	protected static String PROJECT_TYPENAME; //项目备份提示信息
	protected static String CUSTOMER_TYPENAME; //客户备份提示信息
	protected static String ACCPACKAGE_TYPENAME;//帐套备份提示信息
	protected static String MANUSCRIPT_TYPENAME;//底稿备份提示信息

	protected static String ALLOW_VALIDATE; //需要加密的类型

	public final static int ALL_CODE = 1; //全库
	public final static int ACCPACKAGE_CODE = 2; //帐套
	public final static int PROJECT_CODE = 3; //项目备份
	public final static int CUSTOMER_CODE = 4; //客户备份
	public final static int TEMPLATE_CODE = 5; //模板
	public final static int POLICY_CODE = 6; //法律法规
	public final static int MANUSCRIPT_CODE = 7; //底稿
	public final static int CASE_CODE = 8; //案例
	public final static int BASIC_CODE = 9; //基本数据

	static {
		String path = DelPublic.getClassRoot();
		init(path, "BackupConfig.xml");
	}

	/**
	 * 判断文件信息是否正确
	 * @param type
	 * @return
	 */
	public static boolean equalsType(int type, String fileInfo) {
		int isSuited; //判断文件是否匹配
		switch (type) {
		case 1:
			isSuited = fileInfo.indexOf("全库");
			break; //全库
		case 2:
			isSuited = fileInfo.indexOf("帐套");
			break; //帐套备份
		case 3:
			isSuited = fileInfo.indexOf("项目");
			break; //项目备份
		case 4:
			isSuited = fileInfo.indexOf("客户");
			break; //客户备份
		case 5:
			isSuited = fileInfo.indexOf("模板");
			break; //模板
		case 6:
			isSuited = fileInfo.indexOf("法律");
			break; //法律法规
		case 7:
			isSuited = fileInfo.indexOf("底稿");
			break; //底稿
		case 8:
			isSuited = fileInfo.indexOf("案例");
			break; //案例
		case 9:
			isSuited = fileInfo.indexOf("基本");
			break; //基本数据

		default:
			isSuited = -1;
			break;
		}

		return isSuited >= 0;
	}

	/**
	 * 获得全库备份的路径
	 * @return
	 */
	public static String getALL_PATH() {
		return ALL_PATH;
	}

	/**
	 * 获得备份的路径
	 * @return
	 */
	public static String getBACKUP_PATH() {
		return BACKUP_PATH;
	}

	/**
	 * 获得基本数据备份的路径
	 * @return
	 */
	public static String getBASIC_PATH() {
		return BASIC_PATH;
	}

	/**
	 * 获得案例库的备份路径
	 * @return
	 */
	public static String getCASE_PATH() {
		return CASE_PATH;
	}

	/**
	 * 获得客户备份的路径
	 * @return
	 */
	public static String getCUSTOMER_PATH() {
		return CUSTOMER_PATH;
	}

	/**
	 * 获得数据库的绝对路径
	 * @return
	 */
	public static String getDATABASE_PATH() {
		return DATABASE_PATH;
	}

	public static String getACCPACKAGE_PATH() {
		return ACCPACKAGE_PATH;
	}

	/**
	 * 返回格式化后的时间日期,例如：
	 *
	 * getDateTime("yyyy-MM-dd")返回: 2007-03-01
	 * getDateTime("HH:mm:ss")返回: 21:11:34
	 *
	 * @param format	时间格式
	 * @return
	 */
	public static String getDateTime(String format) {
		SimpleDateFormat dateformat = new SimpleDateFormat(format);
		return dateformat.format(new Date());
	}

	/**
	 * 返回底稿备份的路径
	 * @return
	 */
	public static String getMANUSCRIPT_PATH() {
		return MANUSCRIPT_PATH;
	}

	/**
	 * 根据类型返回各种备份的路径
	 * @param type
	 * @return
	 */
	public static String getPath(int type) {
		String path = "";
		switch (type) {
		case 1:
			path = ALL_PATH;
			break; //全库
		case 2:
			path = ACCPACKAGE_PATH;
			break; //帐套备份
		case 3:
			path = PROJECT_PATH;
			break; //项目备份
		case 4:
			path = CUSTOMER_PATH;
			break; //客户备份
		case 5:
			path = TEMPLATE_PATH;
			break; //模板
		case 6:
			path = POLICY_PATH;
			break; //法律法规
		case 7:
			path = MANUSCRIPT_PATH;
			break; //底稿路径
		case 8:
			path = CASE_PATH;
			break; //案例
		case 9:
			path = BASIC_PATH;
			break; //基本数据

		default:
			path = BACKUP_PATH;
			break; //基本路径
		}

		return path;
	}

	/**
	 * 获得法律法规备份的路径
	 * @return
	 */
	public static String getPOLICY_PATH() {
		return POLICY_PATH;
	}

	/**
	 * 返回项目备份的路径
	 * @return
	 */
	public static String getPROJECT_PATH() {
		return PROJECT_PATH;
	}

	/**
	 * 返回随机字符
	 * @return
	 * @throws Exception
	 */
	public static String getRamdonString() throws Exception {
		try {
			String ramStr = String.valueOf(new Random().nextLong());
			return MD5.getMD5String(ramStr);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * 返回修复命令
	 * @return
	 */
	public static String getREPAIR_COMMAND() {
		return REPAIR_COMMAND;
	}

	/**
	 * 获得底稿模板备份的路径
	 * @return
	 */
	public static String getTEMPLATE_PATH() {
		return TEMPLATE_PATH;
	}

	/**
	 * 根据类型返回各种备份的信息
	 * @param type
	 * @return
	 */
	public static String getTypeInfo(int type) {
		String typeInfo = "";
		switch (type) {
		case 1:
			typeInfo = ALL_TYPENAME;
			break; //全库
		case 2:
			typeInfo = ACCPACKAGE_TYPENAME;
			break; //帐套
		case 3:
			typeInfo = PROJECT_TYPENAME;
			break; //项目备份
		case 4:
			typeInfo = CUSTOMER_TYPENAME;
			break; //客户备份
		case 5:
			typeInfo = TEMPLATE_TYPENAME;
			break; //模板
		case 6:
			typeInfo = POLICY_TYPENAME;
			break; //法律法规
		case 7:
			typeInfo = MANUSCRIPT_TYPENAME;
			break; //案例
		case 8:
			typeInfo = CASE_TYPENAME;
			break; //案例
		case 9:
			typeInfo = BASIC_TYPENAME;
			break; //基本数据

		default:
			typeInfo = "";
			break; //基本信息
		}

		return typeInfo;
	}

	/**
	 * 获取mysql数据库的参数,例如:
	 * getVariablesValue("basedir") 返回 D:\project\AuditSystem2.2\Database\
	 * getVariablesValue("datadir") 返回 D:\project\AuditSystem2.2\Database\data\
	 *
	 * 暂时不支持中文目录,因为中文在mysql中就给截断了
	 * @param part
	 * @return
	 * @throws Exception
	 */
	public static String getVariablesValue(String part) throws Exception {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String strSql = "show VARIABLES like ?";
			conn = new DBConnect()
					.getDirectConnect("mysql", false);
			ps = conn.prepareStatement(strSql);
			ps.setString(1, part);
			rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getString("value");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}
		return "";
	}

	/**
	 * 初始化
	 * @param path
	 * @param fileName
	 */
	private static void init(String path, String fileName) {
		try {
			SAXBuilder sb = new SAXBuilder();
			Document doc = sb.build(new FileInputStream(path + fileName));
			Element root = doc.getRootElement();
			String databasePath = "";

			try {

				BACKUP_PATH = com.matech.framework.pub.sys.UTILSysProperty.SysProperty.getProperty("系统临时目录"); 

				ALL_PATH = BACKUP_PATH
						+ root.getChild("dump").getChildText("path");
				BASIC_PATH = BACKUP_PATH
						+ root.getChild("basic").getChildText("path");
				POLICY_PATH = BACKUP_PATH
						+ root.getChild("policy").getChildText("path");
				CASE_PATH = BACKUP_PATH
						+ root.getChild("case").getChildText("path");
				TEMPLATE_PATH = BACKUP_PATH
						+ root.getChild("template").getChildText("path");
				PROJECT_PATH = BACKUP_PATH
						+ root.getChild("project").getChildText("path");
				CUSTOMER_PATH = BACKUP_PATH
						+ root.getChild("customer").getChildText("path");
				MANUSCRIPT_PATH = BACKUP_PATH
						+ root.getChild("manuscript").getChildText("path");
				ACCPACKAGE_PATH = BACKUP_PATH
						+ root.getChild("accpackage").getChildText("path");

				ALL_TYPENAME = root.getChild("dump").getChildText("message");
				BASIC_TYPENAME = root.getChild("basic").getChildText("message");
				POLICY_TYPENAME = root.getChild("policy").getChildText(
						"message");
				CASE_TYPENAME = root.getChild("case").getChildText("message");
				TEMPLATE_TYPENAME = root.getChild("template").getChildText(
						"message");
				PROJECT_TYPENAME = root.getChild("project").getChildText(
						"message");
				CUSTOMER_TYPENAME = root.getChild("customer").getChildText(
						"message");
				MANUSCRIPT_TYPENAME = root.getChild("manuscript").getChildText(
						"message");
				ACCPACKAGE_TYPENAME = root.getChild("accpackage").getChildText(
						"message");

				REPAIR_COMMAND = root.getChild("repair")
						.getChildText("command")
						+ " ";

				ALLOW_VALIDATE = ("" + root.getChild("allowValidate")
						.getChildText("list")).toLowerCase();

			} catch (Exception ex) {
				Debug.print("配置文件的内容有误，无法定位全局变量" + ex.getMessage());
			}

			try {
				//配置文件里的路径
				databasePath = root.getChild("database").getChildText("path");
				Debug.print("配置文件的数据库路径:" + databasePath);

				if (!new File(databasePath).exists()) {
					//e审通目录下的数据库路径
					databasePath = DelPublic.getClassRoot();
					databasePath = databasePath.substring(1, databasePath
							.indexOf("/webRoot/"))
							+ "/Database/Data/";
					Debug.print("e审通目录下的数据库路径:" + databasePath);
				}

			} catch (Exception ex) {
				Debug.print("本地数据库路径错误：" + ex.getMessage());

				//数据库路径,该方法不支持中文路径
				databasePath = getVariablesValue("datadir");
				Debug.print("数据库存放路径(英文):" + databasePath);

			}
			DATABASE_PATH = databasePath.replaceAll("\\\\", "/");

		} catch (Exception ex) {
			Debug.print("读取配置文件出错：" + ex.getMessage());
		}
		Debug.print("数据库路径：" + DATABASE_PATH);
	}

	public static void main(String[] args) {
		try {
			//List list = new BackupUtil().getTablesByFile("asdb", ".", "k_");
//			List list = new BackupUtil().getTablesByFile("asdb","asdb_Temp", ".", "k_");
//			
//			
//			for (int i = 0; i < list.size(); i++) {
//				System.out.println(list.get(i));
//			}
//			boolean isOk = new BackupUtil().createTemplateDB();
//			System.out.println(isOk);
			
			new BackupUtil().createInformationColumns();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 复制数据库表
	 *
	 * @param sourceDB
	 * @param sourceTable
	 * @param targetDB
	 * @return
	 * @throws Exception
	 */
	public boolean cloneTable(String sourceDB, String sourceTable,
			String targetDB) throws Exception {
		return cloneTable(sourceDB, sourceTable, targetDB, sourceTable);
	}

	/**
	 * 复制数据库表
	 * 复制sourceDB库中的sourceTable表到targetDB库中的targetTable表
	 *
	 * @param sourceDB		//源数据库名
	 * @param sourceTable	//源表
	 * @param targetDB		//目标库
	 * @param targetTable	//目标表
	 * @return
	 * @throws Exception
	 */
	public boolean cloneTable(String sourceDB, String sourceTable,
			String targetDB, String targetTable) throws Exception {
		try {
			//克隆表的结构
			copyTableStructure(sourceDB, sourceTable, targetDB, sourceTable);

			//复制数据
			copyData(sourceDB, sourceTable, targetDB, sourceTable);

			return true;
		} catch (Exception e) {
			throw new Exception("复制表出错：" + e.getMessage());
		}
	}

	/**
	 * 拷贝数据
	 * @param sourceDB
	 * @param sourceTable
	 * @param targetDB
	 * @return
	 * @throws Exception
	 */
	public boolean copyData(String sourceDB, String sourceTable, String targetDB)
			throws Exception {
		return copyData(sourceDB, sourceTable, targetDB, sourceTable);
	}

	/**
	 * 拷贝数据
	 * @param sourceDB
	 * @param sourceTable
	 * @param targetDB
	 * @param targetTable
	 * @return
	 * @throws Exception
	 */
	public boolean copyData(String sourceDB, String sourceTable,
			String targetDB, String targetTable) throws Exception {
		Connection conn = null;
		Statement stmt = null;

		try {
			conn = new DBConnect().getConnect("");
			String strSql = "insert into `" + targetDB + "`.`" + targetTable
					+ "`" + " select * from `" + sourceDB + "`.`" + sourceTable
					+ "`";
			stmt = conn.createStatement();
			Debug.print("拷贝数据：" + strSql);
			stmt.execute(strSql);

			return true;
		} catch (Exception e) {
			throw new Exception("拷贝数据出错：" + e.getMessage());
		} finally {
			DbUtil.close(stmt);
			DbUtil.close(conn);
		}
	}

	/**
	 * 复制整张表,主键等字段属性不复制
	 * @param sourceDB	源数据库名
	 * @param sourceTable	表名
	 * @param targetDB	目标数据库
	 * @return
	 * @throws Exception
	 */
	public boolean copyTable(String sourceDB, String sourceTable,
			String targetDB) throws Exception {
		return copyTable(sourceDB, sourceTable, targetDB, sourceTable);
	}

	/**
	 * 复制整张表,主键等字段属性不复制
	 * @param sourceDB	源数据库名
	 * @param sourceTable	源表名
	 * @param targetDB	目标数据库名
	 * @param targetTable	目标表名
	 * @return
	 * @throws Exception
	 */
	public boolean copyTable(String sourceDB, String sourceTable,
			String targetDB, String targetTable) throws Exception {
		Connection conn = null;
		Statement stmt = null;

		try {
			conn = new DBConnect().getConnect("");
			String strSql = "create table `" + targetDB + "`.`" + targetTable
					+ "` " + " select * from `" + sourceDB + "`.`"
					+ sourceTable + "`";
			stmt = conn.createStatement();
			stmt.execute(strSql);

			return true;
		} catch (Exception e) {
			throw new Exception("复制数据库表出错：" + e.getMessage());
		} finally {
			DbUtil.close(stmt);
			DbUtil.close(conn);
		}
	}

	/**
	 * 拷贝表结构
	 * @param sourceDB
	 * @param sourceTable
	 * @param targetDB
	 * @return
	 * @throws Exception
	 */
	public boolean copyTableStructure(String sourceDB, String sourceTable,
			String targetDB) throws Exception {
		return copyTableStructure(sourceDB, sourceTable, targetDB, sourceTable);
	}

	/**
	 * 拷贝表结构
	 * @param sourceDB
	 * @param sourceTable
	 * @param targetDB
	 * @param targetTable
	 * @return
	 * @throws Exception
	 */
	public boolean copyTableStructure(String sourceDB, String sourceTable,
			String targetDB, String targetTable) throws Exception {
		Connection conn = null;
		Statement stmt = null;

		try {
			conn = new DBConnect().getConnect("");
			String strSql = "create table `" + targetDB + "`.`" + targetTable
					+ "` like `" + sourceDB + "`.`" + sourceTable + "`";
			stmt = conn.createStatement();
			stmt.execute(strSql);

			return true;
		} catch (Exception e) {
			throw new Exception("克隆表结构出错：" + e.getMessage());
		} finally {
			DbUtil.close(stmt);
			DbUtil.close(conn);
		}
	}

	/**
	 * 创建临时库
	 * @param dataBaseName
	 * @return
	 * @throws Exception
	 */
	public boolean createTempDB(String dataBaseName) throws Exception {
		Connection conn = null;
		Statement stmt = null;

		try {
			conn = new DBConnect().getConnect("");
			stmt = conn.createStatement();
			String strSql = "create database " + dataBaseName;
			stmt.execute(strSql);

			return true;
		} catch (Exception e) {
			throw new Exception("创建临时库失败：" + e.getMessage());
		} finally {
			DbUtil.close(stmt);
			DbUtil.close(conn);
		}
	}

	/**
	 * 将asdbName库中的所有用到userId的表userId字段都更改为新在系统库的id
	 * @param tables
	 * @param asdbName
	 * @param conn
	 * @throws Exception
	 */
	public void setUserId(String[] tables, String asdbName, Connection conn)
			throws Exception {

		Statement st = null;
		ResultSet rs = null;

		try {
			for (int i = 0; i < tables.length; i++) {
				String[] temp = tables[i].split(",");

				try {
					
					String sql = "select distinct " + temp[1] + " from " + temp[0];
					
					st = conn.createStatement();
					rs = st.executeQuery(sql);

					String oldUserId = "";
					String newUserId = "";
					while (rs.next()) {
						oldUserId = rs.getString(1);
						if (oldUserId != null && !"".equals(oldUserId)) {
							newUserId = checkUser(oldUserId, asdbName, conn);
							updateUserId(tables[i], oldUserId, newUserId, conn);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
		}
	}

	/**
	 * 在系统库中检查用户名是否已经存在,如果存在返回用户id,不存在新增并返回新的用户id
	 * @param userId
	 * @param dbName
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public String checkUser(String userId, String dbName, Connection conn)
			throws Exception {
		String sql = " select id from asdb.k_user "
				+ " where name=(select name from " + dbName
				+ ".k_user where id=?)";
		Object[] params = new Object[] { userId };

		DbUtil dbUtil = new DbUtil(conn);
		String newUserId = dbUtil.queryForString(sql, params);

		//如果没有找到该用户,则新增一个用户
		if (newUserId == null) {
			//找出最大的userid + 1
			sql = " select max(b.id) + 1 from ( select id from asdb.k_user union select id from "
					+ dbName + ".k_user ) b";
			newUserId = dbUtil.queryForString(sql);

			//复制记录到asdb.k_user库
			sql = "insert into asdb.k_user(id,Name,loginid,Password,Sex,BornDate,Educational,Diploma,DepartID,Rank,Post,Specialty,ParentGroup,Popedom,IsTips,departmentid,ProjectPopedom, clientDogSysUi,state) "
					+ " select '"
					+ newUserId
					+ "',Name,loginid,Password,Sex,BornDate,Educational,Diploma,DepartID,"
					+ " Rank,Post,Specialty,'',Popedom,IsTips,'',ProjectPopedom, clientDogSysUi,1 "
					+ " from  " + dbName + ".k_user where id='" + userId + "'";

			System.out.println("新增用户,id=:" + newUserId);
			dbUtil.execute(sql);
			dbUtil.execute("Flush tables");
		}

		return newUserId;
	}

	/**
	 * 在系统库中检查用户名是否已经存在,如果存在返回用户id,不存在新增并返回新的用户id
	 * @param userName
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public String checkUser(String userName, Connection conn) throws Exception {
		String sql = " select id from asdb.k_user " + " where name = ? ";
		Object[] params = new Object[] { userName };

		DbUtil dbUtil = new DbUtil(conn);
		String newUserId = dbUtil.queryForString(sql, params);

		//如果没有找到该用户,则新增一个用户
		if (newUserId == null) {
			//找出最大的userid + 1
			sql = " select max(id) + 1 from asdb.k_user ";
			newUserId = dbUtil.queryForString(sql);

			//复制记录到asdb.k_user库
			sql = "insert into asdb.k_user(id,Name,loginid,DepartID,state,Password) "
					+ " values( ?,?,?,?,?,md5('1') ) ";

			params = new Object[] { newUserId, userName, newUserId, "555555",
					"1" };
			System.out.println("新增用户,id=:" + newUserId);
			dbUtil.execute(sql, params);
			dbUtil.execute("Flush tables");
		}

		return newUserId;
	}

	/**
	 * 更新临时库中用户ID为系统库的id
	 * @param tables
	 * @param oldUserId
	 * @param newUserId
	 * @param conn
	 * @throws Exception
	 */
	public void updateUserId(String table, String oldUserId, String newUserId,
			Connection conn) throws Exception {
		try {
			DbUtil dbUtil = new DbUtil(conn);
			Object[] params = new Object[] { newUserId, oldUserId };
			String[] temp = table.split(",");

			String sql = "update " + temp[0] + " set " + temp[1] + "=? where "
					+ temp[1] + "=?";

			System.out.println("sql:" + sql + ",oldUserId:" + oldUserId
					+ ",newUserId=" + newUserId);

			dbUtil.executeUpdate(sql, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除表数据
	 * @param database
	 * @param tableName
	 * @param field
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public boolean deleteData(Connection conn, String database,
			String tableName, String field, String value) throws Exception {

		Statement stmt = null;

		try {
			String strSql = " delete from `" + database + "`.`" + tableName
					+ "` " + " where `" + field + "` = '" + value + "'";
			stmt = conn.createStatement();
			Debug.print("删除数据：" + strSql);

			stmt.execute(strSql);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			//throw new Exception("删除数据出错：" + e.getMessage());
		} finally {
			DbUtil.close(stmt);
		}

		return false;
	}

	/**
	 * 删除asdb库中所有k_开头的表
	 * @return
	 * @throws Exception
	 */
	protected int dropBasic() throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		int errors = 0; //记录错误数

		try {
			//获得一个连接,指向asdb数据库
			conn = new DBConnect()
					.getDirectConnect("asdb", false);

			String strSql;

			//获得asdb库里面所有k_开头的表
			List filterList = getTablesByFile("asdb", ".", "k_");

			//删除所有k_开头的表
			if (!filterList.isEmpty()) {
				for (int i = 0; i < filterList.size(); i++) {
					try {
						strSql = "drop table IF EXISTS "
								+ filterList.get(i).toString();
						ps = conn.prepareStatement(strSql);
						ps.executeUpdate(strSql);
					} catch (Exception ex) {
						errors++;
					}
				}
			}

			return errors;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}

		return errors;
	}

	/**
	 * 删除数据库,返回结果为错误数
	 * 例如 dropDataBase("backup_"); 删除所有backup_开头的备份库,\\为转义
	 *
	 * @param dataBaseName
	 * @return
	 * @throws Exception
	 */
	protected int dropDataBaseByFile(String dataBaseName) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		Statement stmt = null;
		ResultSet rs = null;
		int errors = 0; //记录错误数
		
		
		File dataDir = new File(DATABASE_PATH);

		try {
			//获得一个连接,指向asdb数据库
			conn = new DBConnect().getDirectConnect("asdb", false);

			String strSql = "";

			if(dataDir != null && dataDir.exists()) {
				
				File[] databaseDir = dataDir.listFiles();
				
				String tempDataBaseName = "";
				
				for (int i = 0; i < databaseDir.length; i++) {
					tempDataBaseName = databaseDir[i].getName();
					
					if(tempDataBaseName.indexOf(dataBaseName) > -1) {
						
						try {
							
							strSql = "drop database IF EXISTS " + tempDataBaseName;
							Debug.print(strSql);
							stmt = conn.createStatement();
							stmt.execute(strSql);
						} catch (Exception ex) {
							ex.printStackTrace();
							
							//sql删数据库失败的话，用文件方式删除
							File file = new File(DATABASE_PATH +  tempDataBaseName);
							
							if(file.exists()) {
								deleteFile(file);
							}
						}
					}
				}
			}

			return errors;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}

		return errors;
	}

	/**
	 * 判断帐套是否已经存在于系统库中
	 * @param accPackageId
	 * @return
	 * @throws Exception
	 */
	protected boolean equalAccPackage(String accPackageId) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new DBConnect().getConnect("");
			String strSql = " select 1 from c_accpackage"
					+ " where AccPackageID = ? ";

			ps = conn.prepareStatement(strSql);
			ps.setString(1, accPackageId);

			rs = ps.executeQuery();

			return rs.next();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}
		return false;
	}

	/**
	 * 判断备份系统库中的底稿模板在系统库中是否存在,存在就返回底稿编号
	 * @param typeName	模板名
	 * @param content	模板类型
	 * @param updateTime	最后一次更新时间
	 * @return
	 * @throws Exception
	 */
	protected int equalAuditType(String typeName, String content,
			String updateTime) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new DBConnect().getConnect("");
			String strSql = " select typeid from k_audittypetemplate"
					+ " where typeName = ? " + " and content = ? "
					+ " and updateTime = ? ";

			ps = conn.prepareStatement(strSql);
			ps.setString(1, typeName);
			ps.setString(2, content);
			ps.setString(3, updateTime);

			rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getInt(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}
		return -1;
	}
	
	/**
	 * 判断备份系统库中的底稿模板在系统库中是否存在,存在就返回底稿编号
	 * @param typeName	模板名
	 * @param content	模板类型
	 * @param updateTime	最后一次更新时间
	 * @return
	 * @throws Exception
	 */
	protected int equalAuditType(String typeId) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new DBConnect().getConnect("");
			String strSql = " select typeid from k_audittypetemplate"
					+ " where typeId = ? ";

			ps = conn.prepareStatement(strSql);
			ps.setString(1, typeId);

			rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getInt(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}
		return -1;
	}

	/**
	 * 获得2个库中表名相同的相同字段
	 * @param dbName1
	 * @param dbName2
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public String getColumn(String dbName1, String dbName2, String tableName) {
		String result = "";

		try {
			result = getColumnByRS(dbName1, dbName2, tableName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	
	/**
	 * 获得库中数据库表字段
	 * @param dbName
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public String getColumn(String dbName,String tableName) {
		String result = "";

		try {
			result = getColumnByRS(dbName, tableName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 获得2个库中表名相同的相同字段，该方法是rs比较,建议使用
	 * @param dbName1
	 * @param dbName2
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	protected String getColumnByRS(String dbName1, String dbName2, String tableName)
			throws Exception {

		Connection conn = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;

		ResultSet rs1 = null;
		ResultSet rs2 = null;

		ResultSetMetaData rsmd1 = null;
		ResultSetMetaData rsmd2 = null;

		try {
			//获得一个连接,指向asdb数据库
			conn = new DBConnect().getDirectConnect("asdb", false);

			String str1 = " select * from " + dbName1 + "." + tableName + " where 1=2 ";
			String str2 = " select * from " + dbName2 + "." + tableName + " where 1=2 ";

			ps1 = conn.prepareStatement(str1);
			ps2 = conn.prepareStatement(str2);

			rs1 = ps1.executeQuery();
			rs2 = ps2.executeQuery();

			rsmd1 = rs1.getMetaData();
			rsmd2 = rs2.getMetaData();

			List colList1 = new ArrayList();
			List colList2 = new ArrayList();

			for(int i=1; i <= rsmd1.getColumnCount(); i++) {
				if(!rsmd1.isAutoIncrement(i)) {
					colList1.add(rsmd1.getColumnLabel(i).toLowerCase());
				}
			}

			for(int i=1; i <= rsmd2.getColumnCount(); i++) {
				if(!rsmd2.isAutoIncrement(i)) {
					colList2.add(rsmd2.getColumnLabel(i).toLowerCase());
				}
			}

			//取交集
			colList1.retainAll(colList2);

			String result = "";
			if(!colList1.isEmpty()) {
				for(int i=0; i < colList1.size(); i++) {
					result += "`" + colList1.get(i) + "`,";
				}

				//去掉最后一个","
				if(!"".equals(result) &&  result.lastIndexOf(",") == result.length() - 1){
					result = result.substring(0, result.length() - 1);
				}
			}

			return result;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs1);
			DbUtil.close(rs2);

			DbUtil.close(ps1);
			DbUtil.close(ps2);

			DbUtil.close(conn);
		}

		return "";
	}
	
	/**
	 * 获得表字段，该方法是rs比较,建议使用
	 * @param dbName
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	protected String getColumnByRS(String dbName, String tableName)
			throws Exception {

		Connection conn = null;
		PreparedStatement ps1 = null;


		ResultSet rs1 = null;


		ResultSetMetaData rsmd1 = null;


		try {
			//获得一个连接,指向asdb数据库
			conn = new DBConnect().getDirectConnect("asdb", false);

			String str1 = " select * from " + dbName + "." + tableName + " where 1=2 ";

			ps1 = conn.prepareStatement(str1);

			rs1 = ps1.executeQuery();

			rsmd1 = rs1.getMetaData();

			List colList1 = new ArrayList();

			for(int i=1; i <= rsmd1.getColumnCount(); i++) {
				if(!rsmd1.isAutoIncrement(i)) {
					colList1.add(rsmd1.getColumnLabel(i).toLowerCase());
				}
			}

			String result = "";
			if(!colList1.isEmpty()) {
				for(int i=0; i < colList1.size(); i++) {
					result += "`" + colList1.get(i) + "`,";
				}

				//去掉最后一个","
				if(!"".equals(result) &&  result.lastIndexOf(",") == result.length() - 1){
					result = result.substring(0, result.length() - 1);
				}
			}

			return result;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs1);
			DbUtil.close(ps1);
			DbUtil.close(conn);
		}

		return "";
	}

	/**
	 * 执行在线更线数据库配置文件方法
	 * @param databaseName 要更新的数据库名
	 * isContinue 失败后是否继续执行 true为继续 false失败后退出
	 * @return 是否执行成功
	 * @throws Exception
	 */
	public boolean updateDatabase(String databaseName, boolean isContinue) {

		BufferedReader reader = null;
		BufferedWriter writer = null;
		Connection conn = null;
		String line = "";
		PreparedStatement ps = null;

		try {
			conn = new DBConnect().getDirectConnect("");

			new DbUtil(conn).execute("use " + databaseName);

			String sqlFilePath = getDATABASE_PATH() + "../../Update/SQLUpdate.dat";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSS");

			File logFileFoder = new File(getDATABASE_PATH() + "../../Update/restorelog/");
			if (!logFileFoder.exists()) {
				logFileFoder.mkdirs();
			}

			File logFile = new File(logFileFoder, sdf.format(new Date())
					+ ".txt");

			if (!logFile.exists()) {
				logFile.createNewFile();
			}

			File databaseFile = new File(sqlFilePath);

			writer = new BufferedWriter(new FileWriter(logFile, true));

			//数据库文件不存在,记录日志并返加FASLE
			if (!databaseFile.exists()) {
				writer.newLine();
				writer.write("找不到SQLUpdate.dat文件,更新失败!\n");
				writer.close();
				return false;
			}
			reader = new BufferedReader(new FileReader(databaseFile));
			while ((line = reader.readLine()) != null) {
				if (!(line.indexOf("#") > -1)) {//判断执行的SQL是否被注释
					try {
						ps = conn.prepareStatement(line);
						ps.execute();
						writer.newLine();
						writer.write("执行SQL语句成功: " + line + "\n");
					} catch (SQLException e3) {
						try {
							writer.newLine();
							writer.write("执行SQL语句失败: " + line);
							writer.newLine();
							writer.write("SQL语句错误原因:" + e3.getMessage() + "\n");
							if (!isContinue) {
								return false;
							}
						} catch (IOException e4) {
							Debug.print(Debug.iError, "写日志文件出错！", e4);
						}
						//e3.printStackTrace();
					}

				}
			}

		} catch (IOException e1) {
			Debug.print(Debug.iError, "读取文件出错！", e1);
			if (!isContinue) {
				return false;
			}
			//e1.printStackTrace();
		} catch (Exception e5) {
			try {
				writer.newLine();
				writer.write("获取数据库连接出错: " + line);
				if (!isContinue) {
					return false;
				}
			} catch (IOException e6) {
				Debug.print(Debug.iError, "写日志文件出错！", e6);
			}
			//e5.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e7) {
				Debug.print(Debug.iError, "关闭日志文件出错!！", e7);
			}
			DbUtil.close(ps);
			DbUtil.close(conn);

		}
		return true;

	}

	/**
	 * 执行在线更线数据库配置文件方法
	 * @param databaseName 要更新的数据库名
	 * isContinue 失败后是否继续执行 true为继续 false失败后退出
	 * startStr 配置文件开始更新处的字符串
	 * @return 是否执行成功
	 * @throws Exception
	 */
	public boolean updateDatabase(String databaseName, boolean isContinue,String startStr) {

		BufferedReader reader = null;
		BufferedReader reader2 = null;
		BufferedWriter writer = null;
		Connection conn = null;
		String line = "";
		PreparedStatement ps = null;
		boolean includeStartStr = false ;

		try {

			if("".equals(startStr) || startStr == null) {
				includeStartStr = false ;
			}

			conn = new DBConnect().getDirectConnect("");

			new DbUtil(conn).execute("use " + databaseName);

			String sqlFilePath = getDATABASE_PATH() + "../../Update/SQLUpdate.dat";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSS");

			File logFileFoder = new File(getDATABASE_PATH() + "../../Update/restorelog/");
			if (!logFileFoder.exists()) {
				logFileFoder.mkdirs();
			}

			File logFile = new File(logFileFoder, sdf.format(new Date())
					+ ".txt");

			if (!logFile.exists()) {
				logFile.createNewFile();
			}

			File databaseFile = new File(sqlFilePath);

			writer = new BufferedWriter(new FileWriter(logFile, true));

			//数据库文件不存在,记录日志并返加FASLE
			if (!databaseFile.exists()) {
				writer.newLine();
				writer.write("找不到SQLUpdate.dat文件,更新失败!\n");
				writer.close();
				return false;
			}
			reader = new BufferedReader(new FileReader(databaseFile));
			while ((line = reader.readLine()) != null) {
			  if(line.indexOf(startStr) > -1) {
				  includeStartStr = true ;

			  }

			  if(includeStartStr) {
					if (!(line.indexOf("#") > -1)) {//判断执行的SQL是否被注释
						try {
							ps = conn.prepareStatement(line);
							ps.execute();
							writer.newLine();
							writer.write("执行SQL语句成功: " + line + "\n");
						} catch (SQLException e3) {
							try {
								writer.newLine();
								writer.write("执行SQL语句失败: " + line);
								writer.newLine();
								writer.write("SQL语句错误原因:" + e3.getMessage() + "\n");
								writer.newLine();
								if (!isContinue) {
									return false;
								}
							} catch (IOException e4) {
								Debug.print(Debug.iError, "写日志文件出错！", e4);
							}
						//	e3.printStackTrace();
						}

					}
			  }
			}
//			reader.mark((int)databaseFile.length()+1);
//			reader.mark(0);
			if(!includeStartStr) {
				reader.close() ;
				reader2 = new BufferedReader(new FileReader(databaseFile));
				 while ((line = reader2.readLine()) != null) {
						if (!(line.indexOf("#") > -1)) {//判断执行的SQL是否被注释
							try {
								ps = conn.prepareStatement(line);
								ps.execute();
								writer.newLine();
								writer.write("执行SQL语句成功: " + line + "\n");
							} catch (SQLException e3) {
								try {
									writer.newLine();
									writer.write("执行SQL语句失败: " + line);
									writer.newLine();
									writer.write("SQL语句错误原因:" + e3.getMessage() + "\n");
									if (!isContinue) {
										return false;
									}
								} catch (IOException e4) {
									Debug.print(Debug.iError, "写日志文件出错！", e4);
								}
						//		e3.printStackTrace();
							}

						}

			 }

			}


		} catch (IOException e1) {
			Debug.print(Debug.iError, "读取文件出错！", e1);
			if (!isContinue) {
				return false;
			}
		//	e1.printStackTrace();
		} catch (Exception e5) {
			try {
				writer.newLine();
				writer.write("获取数据库连接出错: " + line);
				if (!isContinue) {
					return false;
				}
			} catch (IOException e6) {
				Debug.print(Debug.iError, "写日志文件出错！", e6);
			}
		//	e5.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e7) {
				Debug.print(Debug.iError, "关闭日志文件出错!！", e7);
			}
			DbUtil.close(ps);
			DbUtil.close(conn);

		}
		return true;

	}

	/**
	 * 返回执行sql后的第一行第一列
	 * @param strSql
	 * @return
	 * @throws Exception
	 */
	public String getFirstResult(String strSql) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = new DBConnect().getDirectConnect("");
			stmt = conn.createStatement();
			Debug.print("执行sql：" + strSql);
			rs = stmt.executeQuery(strSql);

			if (rs.next()) {
				return rs.getString(1);
			}
		} catch (Exception e) {
			throw new Exception("执行sql出错：" + e.getMessage());
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}

		return "";
	}

	/**
	 * 找出在dbName库中所有tableFlag开头的表,库名与表名之间用split连接。例如:
	 * 		getBasicTables("asdb","/","k\\_"),返回asdb库中所有"k_"开头的表,"k\\_"是为了转义,
	 * 		输出结果:asdb/k_accright, asdb/k_areafunction.........
	 *
	 * @param dbName
	 * @param split
	 * @param tableFlag
	 * @return
	 * @throws Exception
	 */
	protected List getTablesByFile(String dbName, String split, String tableFlag)
			throws Exception {
	
		List tablsList = new ArrayList(); //记录错误数

		try {
			File dbDir = new File(DATABASE_PATH + dbName);

			//如果连接符为空,则只显示表名
			if ("".equals(split)) {
				dbName = "";
			}

			if(dbDir != null && dbDir.exists()) {
				FileFilter fileFilter = new FileFilter(){
			        public boolean accept(File file) {
			            String fileName = file.getName().toLowerCase();
			            if(fileName.endsWith(".myd")){
			                return true;
			            }
			            return false;
			        }
			    };
			    
				File[] dbTableList = dbDir.listFiles(fileFilter);
				for (int i = 0; i < dbTableList.length; i++) {
					
					if(dbTableList[i].getName().toLowerCase().indexOf(tableFlag.toLowerCase()) > -1) {
						String tableName = dbTableList[i].getName().toLowerCase().replaceAll(".myd", "");
						tablsList.add(dbName + split + tableName);
					}
				}
			}
			return tablsList;

		} catch (Exception e) {
			e.printStackTrace();
		} 

		return tablsList;
	}

	/**
	 * 找出在dbName1和dbName2库中所有tableFlag开头,表名相同的表,库名与表名之间用split连接。例如:
	 * 		getBasicTables("asdb","/","k\\_"),返回asdb库中所有"k_"开头的表,"k\\_"是为了转义,
	 * 		输出结果:asdb/k_accright, asdb/k_areafunction.........
	 *
	 * @param dbName
	 * @param split
	 * @param tableFlag
	 * @return
	 * @throws Exception
	 */
	protected List getTablesByFile(String dbName1, String dbName2, String split,
			String tableFlag) throws Exception {
		
		List tablsList = new ArrayList(); //记录错误数

		try {
			
			File db1Dir = new File(DATABASE_PATH + dbName1);
			File db2Dir = new File(DATABASE_PATH + dbName2);
			
			//如果连接符为空,则只显示表名
			if ("".equals(split)) {
				dbName1 = "";
			}
			
			if(db1Dir != null && db1Dir.exists() && db2Dir != null && db2Dir.exists() ) {
				
				FileFilter fileFilter = new FileFilter(){
			        public boolean accept(File file) {
			            String fileName = file.getName().toLowerCase();
			            if(fileName.endsWith(".myd")){
			                return true;
			            }
			            return false;
			        }
			    };
			    
				File[] db1TableList = db1Dir.listFiles(fileFilter);
				File[] db2TableList = db2Dir.listFiles(fileFilter);
				
				for (int i = 0; i < db1TableList.length; i++) {
					for (int j = 0; j < db2TableList.length; j++) {
						
						String fileName1 = db1TableList[i].getName().toLowerCase();
						String fileName2 = db2TableList[j].getName().toLowerCase();
						
						if(fileName1.equals(fileName2) &&  fileName1.indexOf(tableFlag.toLowerCase()) > -1) {
							String tableName = fileName1.replaceAll(".myd", "");
							tablsList.add(dbName1 + split + tableName);
						}
					}
				}
			}

			return tablsList;

		} catch (Exception e) {
			e.printStackTrace();
		} 

		return tablsList;
	}

	/**
	 * 清空数据库表数据
	 * @param dbName
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public boolean truncateTable(String dbName, String tableName)
			throws Exception {
		Connection conn = null;
		Statement stmt = null;

		try {
			conn = new DBConnect().getDirectConnect("");
			stmt = conn.createStatement();
			String strSql = "TRUNCATE table `" + dbName + "`.`" + tableName
					+ "` ";
			stmt.execute(strSql);
			return true;
		} catch (Exception e) {
			throw new Exception("清空表数据失败：" + e.getMessage());
		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * 更新表数据
	 * @param database
	 * @param tableName
	 * @param field
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public boolean updateData(Connection conn, String database,
			String tableName, String field, String value) throws Exception {
		Statement stmt = null;

		try {
			String strSql = " update `" + database + "`.`" + tableName + "` "
					+ " set `" + field + "` = '" + value + "'";
			stmt = conn.createStatement();
			Debug.print("更新数据：" + strSql);
			stmt.execute(strSql);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			//throw new Exception("更新数据出错：" + e.getMessage());
		} finally {
			DbUtil.close(stmt);
		}

		return false;
	}

	public static String getCurrentDate() {
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
		Date currentdate = new java.util.Date();
		return ("" + dateformat.format(currentdate));
	}

	/**
	 * 获得当前时间日期
	 * @return
	 */
	public synchronized static String getCurrentDateTime() {
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMddHHmmss");
		Date currentdate = new Date();
		return ("" + dateformat.format(currentdate));
	}

	/**
	 * 删除重复行
	 * @param tableName 表名
	 * @param key 区别的唯一主键
	 * @param column 不允许重复的列
	 * @return
	 * @throws Exception
	 */
	public int deleteSameRow(String tableName, String key, String column)
			throws Exception {
		Connection conn = null;
		Statement stmt = null;

		try {
			conn = new DBConnect().getConnect("");
			String[] columns = column.split(",");

			StringBuffer sql = new StringBuffer();

			sql.append("delete a ");
			sql.append(" from " + tableName + " a,(select * from " + tableName
					+ " group by ");
			sql.append(column);
			sql.append(" ) b \n ");
			sql.append("where a." + key + " != b." + key + " \n");

			for (int i = 0; i < columns.length; i++) {
				sql.append(" and a." + columns[i] + "=b." + columns[i] + " \n");
			}

			System.out.println(sql.toString());

			stmt = conn.createStatement();
			return stmt.executeUpdate(sql.toString());
		} catch (Exception e) {
			throw new Exception("删除重复行出错:" + e);
		} finally {
			DbUtil.close(stmt);
			DbUtil.close(conn);
		}
	}

	/**
	 * 删除重复行
	 * @param conn
	 * @param tableName
	 * @throws Exception
	 */
	public void deleteSameRow(String tableName) throws Exception {
		Connection conn = null;
		Statement stmt = null;

		String curTime = getCurrentDateTime();

		try {
			conn = new DBConnect().getConnect("");
			stmt = conn.createStatement();

			String sql = " create table " + tableName + curTime + " like "
					+ tableName;
			stmt.execute(sql);

			//插入数据到临时表(去除重复记录)
			sql = " insert into " + tableName + curTime
					+ " select distinct * from " + tableName;
			stmt.execute(sql);

			//删除表
			sql = " drop table " + tableName;
			stmt.execute(sql);

			//重命名临时表
			sql = " rename table " + tableName + curTime + " to " + tableName;
			stmt.execute(sql);

		} catch (Exception e) {
			throw new Exception("删除重复行出错:" + e);
		} finally {
			DbUtil.close(stmt);
			DbUtil.close(conn);
		}
	}

	/**
	 * 获取验证信息
	 * @param type
	 * @return
	 */
	public String getValidate(int type) {

		Map dogMap = JRockey2Opp.getInfoFromDog();

		String sysCo = "";
		if(dogMap != null) {
			sysCo = "" + dogMap.get("sysCo");
		}
		sysCo = "机构信息：" + sysCo;

		boolean isValidate = false;

		switch (type) {
		case ALL_CODE:
			isValidate = ALLOW_VALIDATE.indexOf("dump") > -1;
			break;

		case ACCPACKAGE_CODE: //帐套
			isValidate = ALLOW_VALIDATE.indexOf("accpackage") > -1;
			break;

		case PROJECT_CODE: //项目备份
			isValidate = ALLOW_VALIDATE.indexOf("project") > -1;
			break;

		case CUSTOMER_CODE: //客户备份
			isValidate = ALLOW_VALIDATE.indexOf("customer") > -1;
			break;

		case TEMPLATE_CODE: //模板
			isValidate = ALLOW_VALIDATE.indexOf("template") > -1;
			break;

		case POLICY_CODE: //法律法规
			isValidate = ALLOW_VALIDATE.indexOf("policy") > -1;
			break;

		case MANUSCRIPT_CODE: //案例
			isValidate = ALLOW_VALIDATE.indexOf("manuscript") > -1;
			break;

		case CASE_CODE: //案例
			isValidate = ALLOW_VALIDATE.indexOf("case") > -1;
			break;

		case BASIC_CODE: //基本数据
			isValidate = ALLOW_VALIDATE.indexOf("basic") > -1;
			break;

		default:
			isValidate = false;
			break;
		}

		return isValidate ? sysCo : "";
	}

	/**
	 * 获得当前时间日期
	 * @return
	 */
	public static String getCurrentDateTimeCN() {
		SimpleDateFormat dateformat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Date currentdate = new java.util.Date();
		return ("" + dateformat.format(currentdate));
	}

	/**
	 * 更新抽凭相关表改动的表
	 * @param databaseName
	 * @throws Exception
	 */
	public void updateDB(String databaseName) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;

		StringBuffer sb = new StringBuffer();
		String sql = null;

		try {
			conn = new DBConnect().getDirectConnect("");
			DbUtil dbUtil = new DbUtil(conn);

			sql = "use " + databaseName;
			dbUtil.execute(sql);

			//抽凭表增加字段
			try {
				//测试有没有新增的字段
				sql = " select entrysubjectid from z_voucherspotcheck limit 1 ";
				dbUtil.execute(sql);
				return;
			} catch (Exception e) {
				//#抽凭表：
				sb.append("		alter table z_voucherspotcheck			\n  ");
				sb.append("		add entrysubjectid varchar(80),			\n  ");
				sb.append("		add entrySerail int(10),				\n  ");
				sb.append("		add entrySummary varchar(200),			\n  ");
				sb.append("		add entryDirction int(1),				\n  ");
				sb.append("		add entryOccurValue decimal(15,2),		\n  ");
				sb.append("		add entryCurrRate decimal(15,2),		\n  ");
				sb.append("		add entryCurrValue decimal(15,2),		\n  ");
				sb.append("		add entryCurrency varchar(20),			\n  ");
				sb.append("		add entryQuantity decimal(15,2),		\n  ");
				sb.append("		add entryUnitPrice decimal(15,2),		\n  ");
				sb.append("		add entryUnitName varchar(20),			\n  ");
				sb.append("		add entryBankID	varchar(50),			\n  ");
				sb.append("		add entryProperty varchar(10),			\n  ");
				sb.append("		add entrysubjectname1 varchar(80),		\n  ");
				sb.append("		add entrySubjectFullName1 varchar(250),	\n  ");
				sb.append("		add entryId int(10),					\n  ");
				sb.append("		add voucherDebitOcc decimal(15,2),		\n  ");
				sb.append("		add voucherCreditOcc decimal(15,2)		\n  ");

				System.out.println("增加z_voucherspotcheck字段影响记录数："
						+ dbUtil.executeUpdate(sb.toString()));
			}

			//修改抽凭表字段名
			try {
				sql = " select entryOldVoucherID from z_voucherspotcheck limit 1 ";
				dbUtil.execute(sql);
				return;
			} catch (Exception e) {
				//修改字段名
				sb = null;
				sb = new StringBuffer();
				sb.append("		alter table z_voucherspotcheck						\n  ");
				sb
						.append("		change AccPackageID entryAccPackageID int(14),		\n  ");
				sb
						.append("		change OldVoucherID entryOldVoucherID varchar(20),	\n  ");
				sb.append("		change TypeID entryTypeID varchar(20),				\n  ");
				sb.append("		change VchDate entryVchDate varchar(20);			\n  ");
				System.out.println("修改z_voucherspotcheck字段影响记录数："
						+ dbUtil.executeUpdate(sb.toString()));
			}

			//新增凭证分录表字段名
			try {
				sql = " select voucherDebitOcc from c_subjectentry limit 1 ";
				dbUtil.execute(sql);
				return;
			} catch (Exception e) {
				//修改字段名
				sb = null;
				sb = new StringBuffer();
				//#凭证分录表：
				sb.append("		alter table c_subjectentry					\n  ");
				sb.append("		add voucherFillUser	varchar(20),			\n  ");
				sb.append("		add voucherAuditUser	varchar(20),		\n  ");
				sb.append("		add voucherKeepUser	varchar(20),			\n  ");
				sb.append("		add voucherAffixCount int(10),				\n  ");
				sb.append("		add voucherDebitOcc	decimal(15,2),			\n  ");
				sb.append("		add voucherCreditOcc	decimal(15,2),		\n  ");
				sb.append("		add voucherDirector   varchar(20);			\n  ");

				System.out.println("新增c_subjectentry字段影响记录数："
						+ dbUtil.executeUpdate(sb.toString()));
			}

			//更新凭证分录表记录
			sb = null;
			sb = new StringBuffer();
			sb.append("		update c_subjectentry a,c_voucher b set	\n  ");
			sb.append("		a.voucherFillUser = b.FillUser,			\n  ");
			sb.append("		a.voucherAuditUser = b.AuditUser,		\n  ");
			sb.append("		a.voucherKeepUser = b.KeepUser,			\n  ");
			sb.append("		a.voucherAffixCount = b.AffixCount,		\n  ");
			sb.append("		a.voucherDebitOcc = b.DebitOcc,			\n  ");
			sb.append("		a.voucherCreditOcc = b.CreditOcc		\n  ");
			sb.append("		where a.voucherid = b.autoid			\n  ");

			System.out.println("更新c_subjectentry影响记录数："
					+ dbUtil.executeUpdate(sb.toString()));

			//#创建临时表：
			sb = null;
			sb = new StringBuffer();
			sb.append("	CREATE TABLE `z_voucherspotcheck2` like z_voucherspotcheck  ");

			System.out.println("创建z_voucherspotcheck2影响记录数："
					+ dbUtil.executeUpdate(sb.toString()));

			//往临时表插记录
			sb = null;
			sb = new StringBuffer();
			sb.append("		insert into z_voucherspotcheck2(  																						 \n  ");
			sb.append("			ProjectID, VchID, Believe, Judge, Createor, QuestDate,  														     \n  ");
			sb.append("			Property, subjectid, entryAccPackageID, entryOldVoucherID, entryTypeID, entryVchDate,  								 \n  ");
			sb.append("			flowid, entrysubjectid, entrySerail, entrySummary, entryDirction, entryOccurValue,  								 \n  ");
			sb.append("			entryCurrRate, entryCurrValue, entryCurrency, entryQuantity, entryUnitPrice, entryUnitName,  						 \n  ");
			sb.append("			entryBankID, entryProperty, entrysubjectname1, entrySubjectFullName1,entryId,voucherDebitOcc,voucherCreditOcc) 		 \n  ");
			sb.append("		select a.ProjectID, a.VchID, a.Believe, a.Judge, a.Createor, a.QuestDate,												 \n  ");
			sb.append("			a.Property, a.subjectid, b.AccPackageID as entryAccPackageID, b.OldVoucherID as entryOldVoucherID, b.TypeID as entryTypeID, b.VchDate as entryVchDate,   	\n  ");
			sb.append("			flowid,b.subjectid as entrysubjectid, b.Serail as entrySerail,b.summary as entrySummary,b.Dirction as entryDirction,b.OccurValue as entryOccurValue,  		\n  ");
			sb.append("			b.CurrRate as entryCurrRate, b.CurrValue as entryCurrValue, b.Currency as entryCurrency, b.Quantity as entryQuantity, b.UnitPrice as entryUnitPrice, b.UnitName as entryUnitName,		\n  ");
			sb.append("			b.BankID as entryBankID, b.Property as entryProperty, b.subjectname1 as entrysubjectname1, b.SubjectFullName1 as entrySubjectFullName1,b.autoid as entryId,b.voucherDebitOcc as voucherDebitOcc,	\n  ");
			sb.append("			b.voucherCreditOcc as voucherCreditOcc  \n  ");
			sb.append("		from z_voucherspotcheck a,c_subjectentry b  \n  ");
			sb.append("			where a.vchid=b.voucherid	 \n  ");

			System.out.println("添加z_voucherspotcheck2影响记录数："
					+ dbUtil.executeUpdate(sb.toString()));

			//删除抽凭表
			sb = null;
			sb = new StringBuffer();
			sb.append("		drop table z_voucherspotcheck	");
			System.out.println("删除z_voucherspotcheck影响记录数："
					+ dbUtil.executeUpdate(sb.toString()));

			//重命名临时表
			sb = null;
			sb = new StringBuffer();
			sb.append("		rename table z_voucherspotcheck2 to z_voucherspotcheck  ");
			System.out.println("重命名z_voucherspotcheck2影响记录数："
					+ dbUtil.executeUpdate(sb.toString()));

		} catch (SQLException e) {
			System.out.println("执行该SQL出错:" + sb.toString());
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
			DbUtil.close(conn);
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
			if (!newDir.getParentFile().exists()) {
				newDir.getParentFile().mkdirs();
			}

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

	/**
	 * 拷贝文件或者目录
	 * 例如：copyFiles("d:/111", "e:/444");
	 * 		将 d:/111 目录下的所有文件及文件夹拷贝到 e:/444 目录下
	 * @param oldPath	源文件夹
	 * @param newPath	目标文件夹
	 * @throws Exception
	 */
	public void copyFiles(String oldPath, String newPath) throws Exception {
		File oldFile = new File(oldPath);
		File newFile = new File(newPath);

		if (!oldFile.exists()) {
			throw new Exception("将要拷贝的目录不存在");
		}

		copyFiles(oldFile, newFile);
	}

	public static void deleteFile(File file) throws Exception {
		if (file.exists()) {
			//如果是文件,则直接删除
			if (file.isFile()) {
				file.delete();
			} else {
				//如果是目录,则先删除里面的文件
				File[] filelist = file.listFiles();

				//如果目录下有文件或者目录
				if (filelist.length != 0) {
					//遍历目录,先删除目录下的文件
					for (int i = 0; i < filelist.length; i++) {
						//如果是文件夹,则递归调用
						if (filelist[i].isDirectory()) {
							deleteFile(filelist[i]);
						} else {
							filelist[i].delete();
						}
					}
					file.delete();
				} else {
					file.delete();
				}
			}
		}

	}
	
	
	/**
	 * 创建模板库
	 * @return
	 */
	public boolean createTemplateDB() {
		try {
			String path;

			path = org.del.DelPublic.getClassRoot();
			path = path.substring(0, path.indexOf("/WEB-INF/classes/")) + "/DatabaseManage/asdbtemplate.zip";

			new DataZip().unZip(path, DATABASE_PATH + "asdbtemplate", false);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 创建数据库表的缓存表
	 */
	public void createInformationTables() {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = new DBConnect().getConnect("");
			stmt = conn.createStatement();
			System.out.println("创建tables缓存表...");
			String sql = " drop table IF EXISTS asdb.tables";
			stmt.execute(sql);
			
			sql = "create table asdb.tables "
			    + " as "
			    + " select * from information_schema.TABLES "
				+ " where (TABLE_SCHEMA='asdb' or TABLE_SCHEMA='asdbTemplate') "
			    + " and TABLE_TYPE='BASE TABLE'";
			stmt.execute(sql);

			System.out.println("tables缓存表创建成功!");
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			DbUtil.close(stmt);
			DbUtil.close(conn);
		}
	}
	
	/**
	 * 检查有没有缓存表存在，没有就新建
	 */
	public void checkInformation() {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = new DBConnect().getConnect("");
			stmt = conn.createStatement();
			
			System.out.println("检查缓存表是否存在...");
			try {
				String sql = " select 1 from asdb.tables";
				stmt.executeQuery(sql);
				
				sql = " select 1 from asdb.columns";
				stmt.executeQuery(sql);
				
				System.out.println("缓存表已经存在!");
			} catch (Exception e) {
				System.out.println("缓存表不存在，开始创建：");
				resetInformation();
				System.out.println("缓存表创建完成!!");
			}

		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			DbUtil.close(stmt);
			DbUtil.close(conn);
		}
	}
	
	/**
	 * 删除数据库
	 * @param databaseName
	 */
	public void dropDatabase(String databaseName) {
		
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = new DBConnect().getConnect("");
			stmt = conn.createStatement();
			System.out.println("删除数据库:" + databaseName );
			
			String sql = " drop database IF EXISTS " + databaseName;
			stmt.execute(sql);

			System.out.println("删除数据库完成!!");
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			DbUtil.close(stmt);
			DbUtil.close(conn);
		}
	}
	/**
	 * 重置缓存表
	 */
	public void resetInformation() {
		createTemplateDB();
		createInformationTables();
		createInformationColumns();
		dropDatabase("asdbtemplate");
	}
	
	/**
	 * 创建数据库列的缓存表
	 */
	public void createInformationColumns() {
		
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = new DBConnect().getConnect("");
			
			System.out.println("创建columns缓存表...");
			//String sql = " drop table IF EXISTS asdb.columns ";
			String sql = " drop TABLE IF EXISTS asdb.columns ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			
			String sql2 = " create table asdb.columns "
				+ " select a.* from information_schema.columns a,information_schema.TABLES b "
				+ " where b.TABLE_TYPE='BASE TABLE' " 
				+ " and (a.TABLE_SCHEMA='asdb' or a.TABLE_SCHEMA='asdbTemplate') "
				+ " and a.TABLE_SCHEMA=b.TABLE_SCHEMA "
				+ " and a.TABLE_NAME=b.TABLE_NAME ";
			System.out.println(sql2);
			ps = conn.prepareStatement(sql2);
			ps.execute();

			System.out.println("columns缓存表创建成功!");
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			DbUtil.close(ps);
			DbUtil.close(conn);
		}
	}

	/**
	 * 重新计算k_manuscriptdata表
	 * @param asdbName
	 * @param customerName
	 */
	public void revocerManuscriptdata(String asdbName, String customerName) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = new DBConnect().getConnect("");
			stmt = conn.createStatement();

			//创建临时表
			String sql = "  CREATE TABLE " + asdbName + ".t_manuscriptdata ( "
					+ " unid int(11) NOT NULL auto_increment, "
					+ " projectid varchar(40) default NULL, "
					+ " taskid varchar(40) default NULL,  "
					+ " PRIMARY KEY  (unid) "
					+ " ) ENGINE=MyISAM DEFAULT CHARSET=gbk ";
			System.out.println("创建临时表:" + sql);
			stmt.execute(sql);

			//插入数据到临时表
			sql = " insert into " + asdbName
					+ ".t_manuscriptdata(projectId,taskid) "
					+ " select typeId,taskId from " + asdbName
					+ ".k_tasktemplate " + " where isleaf='1' ";
			System.out.println("插入数据到临时表:" + sql);
			stmt.execute(sql);

			//更新所有底稿的ManuTemplateID
			sql = " update " + asdbName + ".k_tasktemplate a," + asdbName
					+ ".t_manuscriptdata b " + " set a.ManuTemplateID=b.unid "
					+ " where a.typeId=b.projectid "
					+ " and a.taskid=b.taskid " + " and a.isleaf='1' ";
			System.out.println("更新所有底稿的ManuTemplateID:" + sql);
			stmt.execute(sql);

			//更新所有任务结点的ManuTemplateID
			sql = " update " + asdbName + ".k_tasktemplate "
					+ " set ManuTemplateID='0' " + " where isleaf='0' ";
			System.out.println("更新所有任务结点的ManuTemplateID:" + sql);
			stmt.execute(sql);

			//清空k_manuscriptdata表
			sql = " truncate table " + asdbName + ".k_manuscriptdata ";
			System.out.println("清空k_manuscriptdata表:" + sql);
			stmt.execute(sql);

			//重新插数据回k_manuscriptdata表
			sql = " insert into " + asdbName
					+ ".k_manuscriptdata(unid,filename,projectid,property) "
					+ " select ManuTemplateID,taskname,typeId,0 " + " from "
					+ asdbName + ".k_tasktemplate "
					+ " where isleaf='1' and ManuTemplateID is not null ";
			System.out.println("重新插数据回k_manuscriptdata表:" + sql);
			stmt.execute(sql);

			//更新word的mime信息
			sql = " update " + asdbName + ".k_manuscriptdata "
					+ " set mime='application/msword' "
					+ " where filename like '%.doc'";
			System.out.println("更新word的mime信息:" + sql);
			stmt.execute(sql);

			//更新excel的mime信息
			sql = " update " + asdbName + ".k_manuscriptdata "
					+ " set mime='application/vnd.ms-excel' "
					+ " where filename like '%.xls'";
			System.out.println("更新excel的mime信息:" + sql);
			stmt.execute(sql);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				//删除临时表
				String sql = " drop table " + asdbName + ".t_manuscriptdata ";
				System.out.println("删除临时表:" + sql);
				stmt.execute(sql);
			} catch (Exception e) {
				e.printStackTrace();
			}

			DbUtil.close(stmt);
			DbUtil.close(conn);
		}

	}

	/**
	 * 记录日志
	 * @param message
	 */
	protected void log(String message) {
		try {
			webPrint(message, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param message:输出信息
	 * @param type:输出类型<br/>
	 *  type=0,完整的格式,例如：[2008-01-01 12:12:12]：欢迎(br换行) <br/>
	 *  type=1,只输出,不换行,例如：[2008-01-01 12:12:12]：欢迎 <br/>
	 *  type=2,不显示时间,只输出后换行,例如：欢迎(br换行) <br/>
	 *  type=3,不显示时间,只输出,例如：欢迎 <br/>
	 */
	protected void log(String message, int type) {
		try {
			webPrint(message, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void logEnd() {
		try {
			webPrint("<font color='blue'>完成</font>", 2);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void logStart(String message) {
		try {
			webPrint(message, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void logEndAndStart(String message) {
		try {
			logEnd();
			logStart(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 往前台打印信息
	 * @param message
	 */
	protected void webPrint(String message, int type) {
		if(this.out != null && isPrint) {

			switch(type) { 
				case 0:
					this.out.println("<font color='blue'>[" + getCurrentDateTimeCN() + "]</font> " + message + "<br/>");
					break; 

				case 1:
					this.out.println("<font color='blue'>[" + getCurrentDateTimeCN() + "]</font> " + message + "...<img src=\"images/indicator.gif\" />");
					break;

				case 2:
					this.out.println(message + "<br/>");
					this.out.write("<script>");
					this.out.write(" try { ");
					this.out.write("	document.getElementById(\"backupDiv\").scrollTop=document.getElementById(\"backupDiv\").scrollHeight;");
					this.out.write(" } catch(e) {} ");
					this.out.write("</script>");
					
					break;

				case 3:
					this.out.println(message);
					break;

				default:
					this.out.println(message);
					break;

			}

			this.out.flush();
		}
	}

	public PrintWriter getOut() {
		return out;
	}

	public void setOut(PrintWriter out) {
		this.out = out;

		if(this.out != null) {
			this.out.print("<link href=\"AS_CSS/css_main.css\" rel=\"stylesheet\" type=\"text/css\">");
			this.out.print("<link href=\"AS_CSS/style.css\" rel=\"stylesheet\" type=\"text/css\">");
		}
	}

	public boolean isPrint() {
		return isPrint;
	}

	public void setPrint(boolean isPrint) {
		this.isPrint = isPrint;
	}
}
