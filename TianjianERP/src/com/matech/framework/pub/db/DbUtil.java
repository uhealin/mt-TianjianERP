package com.matech.framework.pub.db;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.matech.audit.pub.db.DBConnect;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.ClassUtil;
import com.matech.framework.pub.util.StringUtil;
import com.mysql.jdbc.Driver;

/**
 * 
 * <p>
 * Title: 数据库专用操作类
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006, 2008 MaTech Corporation. All rights reserved.
 * </p>
 * <p>
 * Company: Matech 广州铭太信息科技有限公司
 * </p>
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有， 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 * 
 * 贡献者团队: 铭太科技 - 研发中心，审计开发组
 * 
 * @author winnerQ
 * @version 3.0
 */

public class DbUtil {
	
	public enum ColumnType{
		VARCHAR(String.class),CHAR(String.class),DATETIME(String.class),TIMESTAMP(String.class)
		,INT(Integer.class),DOUBLE(Double.class),TEXT(String.class),FLOAT(Float.class),DECIMAL(Double.class)
		,DATE(String.class),SMALLINT(Integer.class),BIGINT(Integer.class);
		
		 ColumnType(Class type){
			this.type=type;
		}
		private Class type;
		public Class getType() {
			return type;
		}
		
	}
	
	private static Map<String, String> SQL_CACHE=new HashMap<String, String>();
	
	private Connection conn;

	// 基于common-log和log4j实现的记录日志，但是比较麻烦，不推荐其它类使用
	private static Log log = LogFactory.getLog(DbUtil.class);

	/**
	 * 初始化对象，需要传入CONN对象
	 * 
	 * @param conn
	 *            DBConnect
	 */
	public DbUtil(Connection conn) throws Exception {
		this.conn = conn;
	}

	/**
	 * 执行SQL得到记录集,请记住,这个记录集并没有关闭, 请在调用本方法之后,显式的执行关闭记录rs.close()
	 * 
	 * @param strSql
	 *            String
	 * @return ResultSet
	 * @throws Exception
	 */
	public ResultSet getResultSet(String strSql) throws Exception {
		ResultSet rs = null;

		try {
			rs = conn.createStatement().executeQuery(strSql);
		} catch (Exception e) {
			log.error("getResultSet函数执行sql出错=:" + strSql);
			throw new Exception("执行sql出错", e);
		} finally {
		}

		return rs;
	}

	/**
	 * 如果连接对象为空，则抛出异常
	 * 
	 * @param con
	 *            Connection
	 * @throws Exception
	 */
	public static void checkConn(Connection con) throws MatechException {
		if (con == null) {
			throw new MatechException("连接对象不能为空");
		}
	}

	/**
	 * 执行SQL语句,执行成功返回true,否则记录日志,抛出异常,返回false
	 * 
	 * @param strSql
	 *            String 要执行的SQL语句
	 * @return boolean
	 * @throws Exception
	 */
	public boolean execute(String strSql) throws Exception {
		Statement stmt = null;

		try {
			stmt = conn.createStatement();
			return stmt.execute(strSql);
		} catch (Exception e) {
			log.error("execute函数执行sql出错=:" + strSql);
			throw new Exception("执行sql出错", e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ex) {
					log.warn("Exception in closing JDBC Statement", ex);
				}
			}
		}
	}

	/**
	 * 执行带参数的SQL语句,执行成功返回true,否则记录日志,抛出异常,返回false
	 * 
	 * @param strSql
	 *            String 要执行的SQL语句
	 * @return boolean
	 * @throws Exception
	 */
	public boolean execute(String strSql, Object[] args) throws Exception {
		PreparedStatement ps = null;

		try {
			if (args == null) {
				throw new Exception("参数不能为空!!");
			}

			ps = conn.prepareStatement(strSql);

			for (int i = 0; i < args.length; i++) {
				ps.setObject(i + 1, args[i]);
			}

			return ps.execute();

		} catch (Exception e) {
			log.error("execute函数执行sql出错=:" + strSql);
			throw new Exception("执行sql出错", e);
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException ex) {
					log.warn("Exception in closing JDBC PreparedStatement", ex);
				}
			}
		}
	}

	/**
	 * 执行SQL语句,执行成功返回实际影响行数,否则抛出异常
	 * 
	 * @param sql
	 *            String 要执行的SQL语句
	 * @return boolean
	 * @throws Exception
	 */
	public int executeUpdate(String sql) throws Exception {
		Statement stmt = null;

		try {
			stmt = conn.createStatement();
			return stmt.executeUpdate(sql);
		} catch (Exception e) {
			log.error("execute函数执行sql出错=:" + sql+"\n "+e.getLocalizedMessage());
			throw new Exception("执行sql出错", e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ex) {
					log.warn("Exception in closing JDBC Statement", ex);
				}
			}
		}
	}

	/**
	 * 执行带参数的SQL语句,执行成功返回实际影响行数,否则抛出异常
	 * 
	 * @param strSql
	 *            String 要执行的SQL语句
	 * @return boolean
	 * @throws Exception
	 */
	public int executeUpdate(String strSql, Object[] args) throws Exception {
		PreparedStatement ps = null;

		try {
			if (args == null) {
				throw new Exception("参数不能为空!!");
			}

			ps = conn.prepareStatement(strSql);

			for (int i = 0; i < args.length; i++) {
				ps.setObject(i + 1, args[i]);
			}

			return ps.executeUpdate();

		} catch (Exception e) {
			log.error("execute函数执行sql出错=:" + strSql);
			throw new Exception("执行sql出错", e);
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException ex) {
					log.warn("Exception in closing JDBC PreparedStatement", ex);
				}
			}
		}
	}

	/**
	 * 执行带参数的SQL语句,执行成功返回执行结果的第一行第一列的记录,否则记录日志,抛出异常,返回null
	 * 
	 * @param strSql
	 *            String 要执行的SQL语句
	 * @return Object类型,需要自己强制转换
	 * @throws Exception
	 */
	public Object queryForObject(String sql, Object[] args) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Object result = null;
		try {
			ps = conn.prepareStatement(sql);

			if (args == null) {
				throw new Exception("参数不能为空!!");
			}

			for (int i = 0; i < args.length; i++) {
				ps.setObject(i + 1, args[i]);
			}
			rs = ps.executeQuery();

			if (rs.next()) {
				result = rs.getObject(1);
			}
		} catch (Exception e) {
			log.error("executeQuery函数执行sql出错=:" + sql);
			throw new Exception("执行sql出错", e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					log.warn("Exception in closing JDBC ResulSet", ex);
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException ex) {
					log.warn("Exception in closing JDBC ps", ex);
				}
			}
		}
		return result;
	}

	/**
	 * 执行带参数的SQL语句,执行成功返回执行结果的第一行第一列的记录,否则记录日志,抛出异常,返回null;
	 * 
	 * @param strSql
	 * @return String类型
	 * @throws Exception
	 */
	public String queryForString(String strSql, Object[] object)
			throws Exception {
		String result = String.valueOf(queryForObject(strSql, object));
		if (!"null".equals(result)) {
			return result;
		}
		return null;
	}

	/**
	 * 执行带参数的SQL语句,执行成功返回执行结果的第一行第一列的记录,否则记录日志,抛出异常,返回-1;
	 * 
	 * @param strSql
	 * @return int类型
	 * @throws Exception
	 */
	public int queryForInt(String strSql, Object[] object) throws Exception {
		String result = String.valueOf(queryForObject(strSql, object));

		if (!"null".equals(result)) {
			return Integer.parseInt(result);
		}
		return -1;
	}

	/**
	 * 执行带参数的SQL语句,执行成功返回执行结果的第一行第一列的记录,否则记录日志,抛出异常,返回-1;
	 * 
	 * @param strSql
	 * @return int类型
	 * @throws Exception
	 */
	public double queryForDouble(String strSql) throws Exception {
		Statement stmt = null;
		ResultSet rs = null;
		double result = 0.00;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(strSql);

			if (rs.next()) {
				result = rs.getDouble(1);
			}
		} catch (final Exception e) {
			log.error("executeQuery函数执行sql出错=:" + strSql);
			throw new Exception("执行sql出错", e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (final SQLException ex) {
					log.warn("Exception in closing JDBC ResulSet", ex);
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (final SQLException ex) {
					log.warn("Exception in closing JDBC Statement", ex);
				}
			}
		}
		return result;
	}

	/**
	 * 执行SQL语句,执行成功返回执行结果的第一行第一列的记录,否则记录日志,抛出异常,返回null
	 * 
	 * @param strSql
	 *            String 要执行的SQL语句
	 * @return Object类型,需要自己强制转换
	 * @throws Exception
	 */
	public Object queryForObject(final String strSql) throws Exception {
		Statement stmt = null;
		ResultSet rs = null;
		Object result = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(strSql);

			if (rs.next()) {
				result = rs.getObject(1);
			}
		} catch (final Exception e) {
			log.error("executeQuery函数执行sql出错=:" + strSql);
			throw new Exception("执行sql出错", e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (final SQLException ex) {
					log.warn("Exception in closing JDBC ResulSet", ex);
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (final SQLException ex) {
					log.warn("Exception in closing JDBC Statement", ex);
				}
			}
		}
		return result;
	}

	/**
	 * 执行SQL语句,执行成功返回执行结果的第一行第一列的记录,否则记录日志,抛出异常,返回null;
	 * 
	 * @param strSql
	 * @return String类型
	 * @throws Exception
	 */
	public String queryForString(String strSql) throws Exception {
		String result = String.valueOf(queryForObject(strSql));
		if (!"null".equals(result)) {
			return result;
		}
		return null;
	}

	/**
	 * 执行SQL语句,执行成功返回执行结果的第一行第一列的记录,否则记录日志,抛出异常,返回-1;
	 * 
	 * @param strSql
	 * @return int类型
	 * @throws Exception
	 */
	public int queryForInt(String strSql) throws Exception {
		String result = String.valueOf(queryForObject(strSql));

		if (!"null".equals(result)) {
			return Integer.parseInt(result);
		}
		return -1;
	}

	/**
	 * 得到ResultSet记录集的行数。 其实不推荐使用本方法来获取行数,对效率的影响程度需要小郭测试
	 * 
	 * @param RS
	 *            ResultSet
	 * @return int 返回记录集的行数
	 */
	public int getRowCount(ResultSet RS) {
		int rowCount = 0;

		try {
			String rowStatus = "";
			int preRow = 0;

			if (RS.isBeforeFirst()) {
				rowStatus = "isBeforeFirst";
			} else {

				if (RS.isAfterLast()) {
					rowStatus = "isAfterLast";
				} else {
					rowStatus = "normal";
					preRow = RS.getRow();
				}
			}

			RS.last();
			rowCount = RS.getRow();

			if (rowStatus.equals("isBeforeFirst")) {
				RS.beforeFirst();
			} else {

				if (rowStatus.equals("isAfterLast")) {
					RS.afterLast();
				} else {
					RS.absolute(preRow);
				}
			}

		} catch (Exception e) {
			log.error("getRowCount函数执行出错=:" + e.getMessage());
			rowCount = -1;
		}
		return rowCount;
	}

	/**
	 * 得到记录集的列数。
	 * 
	 * @param RS
	 *            ResultSet
	 * @return int 返回记录集的列数
	 */
	public int getColCount(ResultSet RS) {
		int colCount = 0;

		try {
			ResultSetMetaData RSMD = RS.getMetaData();
			colCount = RSMD.getColumnCount();
		} catch (Exception e) {
			log.error("getColCount函数执行出错=:" + e.getMessage());
			colCount = -1;
		}

		return colCount;
	}

	// 分页
	public void absolutePage(ResultSet RS, int pageSize, int page) {

		try {
			RS.absolute(pageSize * (page - 1) + 1);
		} catch (Exception e) {
			log.error("absolutePage函数执行出错=:" + e.getMessage());
		}
	}

	// 从存储过程中得到返回值。
	public int getReturnCode(CallableStatement callablestatement, boolean flag) {
		int i = -1;
		ResultSet resultset = null;
		if (flag) {
			try {
				resultset = callablestatement.getResultSet();
				resultset.next();
				i = resultset.getInt(1);
			} catch (Exception e) {
				log.error("getReturnCode函数执行出错=:" + e.getMessage());
				i = -1;
			} finally {
				try {
					resultset.close();
				} catch (SQLException e) {
					log.warn("getReturnCode函数执行出错=:" + e.getMessage());
				}
			}
		}
		return i;
	}
	
	/**
	 * 回滚代码
	 * @param conn
	 */
	public static void rollback(Connection conn) {
		try {
			if (conn != null) {
				conn.rollback();
			}
		} catch (Exception e) {
			log.warn("回滚出错=:" + e.getMessage());
		}
	}

	/**
	 * 安全关闭RS的代码
	 * 
	 * @param rs
	 *            ResultSet
	 */
	public static void close(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (Exception e) {
			log.warn("安全关闭RS执行出错=:" + e.getMessage());
		}
	}

	/**
	 * 安全关闭Statement
	 * 
	 * @param st
	 *            Statement
	 */
	public static void close(Statement st) {
		try {
			if (st != null) {
				st.close();
			}
		} catch (Exception e) {
			log.warn("安全关闭St执行出错=:" + e.getMessage());
		}
	}

	/**
	 * 安全关闭数据库连接
	 * 
	 * @param conn1
	 *            Connection
	 */
	public static void close(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (Exception e) {
			log.warn("安全关闭conn执行出错=:" + e.getMessage());
		}
	}
	
	public static void close(Connection conn,Statement st,ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
			if (st != null) {
				st.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (Exception e) {
			log.warn("安全关闭执行出错=:" + e.getMessage());
		}
	}

	/**
	 * 返回第一行数据
	 * 
	 * @param sql
	 * @return
	 */
	public static String getRowOne(String sql) {
		return " SELECT * FROM ( " + sql + ") a WHERE rownum =1 ";
	}

	/**
	 * 返回前N行数据
	 * 
	 * @param sql
	 * @param n
	 * @return
	 */
	public static String getRows(String sql, int n) {
		return " SELECT * FROM ( " + sql + ") a WHERE rownum <= " + n;
	}

	/**
	 * 查询记录 多条记录
	 * 
	 * @param table
	 *            : 表名
	 * @param field
	 *            : 主键字段名
	 * @param value
	 *            : 主键的值
	 * @return Map ：K=表的字段名(小写),V=表单的值
	 */
	public List getList(String table, String field, String value)
			throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			List list = new ArrayList();
			String sql = "select * from " + table + " where " + field
					+ "=? order by 1 ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, value);
			rs = ps.executeQuery();
			ResultSetMetaData RSMD = rs.getMetaData();
			while (rs.next()) {
				Map map = new HashMap();
				for (int i = 1; i <= RSMD.getColumnCount(); i++) {
					map.put(RSMD.getColumnLabel(i).toLowerCase(),
							getStringValue(rs.getObject(RSMD.getColumnLabel(i)), RSMD.getColumnType(i)));
				}
				list.add(map);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	/**
	 * 查询记录 多条记录
	 * 
	 * @param table
	 *            : 表名
	 * @param field
	 *            : 主键字段名
	 * @param value
	 *            : 主键的值
	 *            
	 * @param orderby
	 *            : 排序
	 *            
	 * @return Map ：K=表的字段名(小写),V=表单的值
	 */
	public List getList(String table, String field, String value,String orderby)
			throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			List list = new ArrayList();
			String sql = "select * from " + table + " where " + field
					+ "=? ";
			
			String order = "1" ;
			if(!"".equals(orderby)){
				order = orderby + "," + order ;
			}
			sql +=  " order by " + order ;
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, value);
			rs = ps.executeQuery();
			ResultSetMetaData RSMD = rs.getMetaData();
			while (rs.next()) {
				Map map = new HashMap();
				for (int i = 1; i <= RSMD.getColumnCount(); i++) {
					map.put(RSMD.getColumnLabel(i).toLowerCase(),
							getStringValue(rs.getObject(RSMD.getColumnLabel(i)), RSMD.getColumnType(i)));
				}
				list.add(map);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}
	
	/**
	 * 通过SQL，得到一个记录的集合
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public List getList(String sql) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			List list = new ArrayList();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			ResultSetMetaData RSMD = rs.getMetaData();
			while (rs.next()) {
				Map map = new HashMap();
				for (int i = 1; i <= RSMD.getColumnCount(); i++) {
					map.put(RSMD.getColumnLabel(i).toLowerCase(),
							getStringValue(rs.getObject(RSMD.getColumnLabel(i)), RSMD.getColumnType(i)));
				}
				list.add(map);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	/**
	 * 查询记录
	 * 
	 * @param table
	 *            : 表名
	 * @param field
	 *            : 主键字段名
	 * @param value
	 *            : 主键的值
	 * @return Map ：K=表的字段名(小写),V=表单的值
	 */
	public Map get(String table, String field, String value) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Map map = new HashMap();
			String sql = "select * from " + table + " where " + field + "=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, value);
			rs = ps.executeQuery();
			ResultSetMetaData RSMD = rs.getMetaData();
			if (rs.next()) {
				for (int i = 1; i <= RSMD.getColumnCount(); i++) {
					map.put(RSMD.getColumnLabel(i).toLowerCase(),getStringValue(rs.getObject(RSMD.getColumnLabel(i)), RSMD.getColumnType(i)));
				}
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	/**
	 * 查询记录
	 * 
	 * @param table
	 *            : 表名
	 * @param field
	 *            : 主键字段名
	 * @param value
	 *            : 主键的值
	 * @param returnField
	 *            : 要返回的字段值
	 */
	public Object get(String table, String field, String value,
			String returnField) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select * from " + table + " where " + field + "=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, value);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getObject(returnField);
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	/**
	 * 新增记录
	 * 
	 * @param table
	 *            : 表名
	 * @param field
	 *            : 自动主键;新增时去掉自动主键,没有自动主键可以为空
	 * @param map
	 *            : 新增记录,K=表的字段名(小写),V=表单的值
	 */
	public void add(String table, String field, Map map) throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		
		try {
			String sql1 = "";
			String sql2 = "";
			sql = "select * from " + table + " where 1=2 ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			ResultSetMetaData RSMD = rs.getMetaData();
			
			Iterator it = map.keySet().iterator();
			
			List<String> valueList = new ArrayList<String>();
			
			while (it.hasNext()) {
				String key = String.valueOf(it.next()).toLowerCase();
				String value = String.valueOf(map.get(key));
				
				for (int i = 1; i <= RSMD.getColumnCount(); i++) {
					String columnName = RSMD.getColumnLabel(i).toLowerCase();
					
					if (!StringUtil.showNull(field).toLowerCase().equals(columnName) && key.equals(columnName)) {
						sql1 += ",`" + key+"`";
						sql2 += "," + "? ";
						//金额类型去掉千分位
						value = StringUtil.showNull(value);
						int fieldType = RSMD.getColumnType(i) ;
						if(fieldType == Types.NUMERIC || fieldType == Types.DOUBLE || fieldType == Types.DECIMAL) {
							value = value.replaceAll(",", "") ;
						}
						value = value.replaceAll("\"","&quot;") ;
						valueList.add(value);
					}
				}
				
			}

			sql = "insert into " + table + " (" + sql1.substring(1)
					+ ") values (" + sql2.substring(1) + ") ";
			
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < valueList.size(); i++) {
				ps.setString((i+1), valueList.get(i));
			}
			
			ps.execute();

		} catch (Exception e) {
			log.debug("执行SQL出错:" + sql);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 修改记录
	 * 
	 * @param table
	 *            : 表名
	 * @param field
	 *            : 主键字段名
	 * @param map
	 *            : 修改记录,K=表的字段名(小写),V=表单的值
	 */
	public void update(String table, String field, Map map) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "", sql1 = "";
			sql = "select * from " + table + " where 1=2 ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			ResultSetMetaData RSMD = rs.getMetaData();
			
			Iterator it = map.keySet().iterator();
			
			List<String> valueList = new ArrayList<String>();
			
			while (it.hasNext()) {
				String key = String.valueOf(it.next()).toLowerCase();
				String value = String.valueOf(map.get(key));
				
				for (int i = 1; i <= RSMD.getColumnCount(); i++) {
					String columnName = RSMD.getColumnLabel(i).toLowerCase();
					
					if (!StringUtil.showNull(field).toLowerCase().equals(columnName) && key.equals(columnName)) {
						sql1 += ",`" + key + "`=? ";
						//金额类型去掉千分位
						value = StringUtil.showNull(value);
						int fieldType = RSMD.getColumnType(i) ;
						if(fieldType == Types.NUMERIC || fieldType == Types.DOUBLE || fieldType == Types.DECIMAL) {
							value = value.replaceAll(",", "") ;
						}
						valueList.add(value);
					}
				}
				
			}
			
			String fieldValue = StringUtil.showNull(map.get(StringUtil.showNull(field).toLowerCase()));

			sql = "update " + table + " set " + sql1.substring(1) + " where " + field + " ='" + fieldValue + "'";
			System.out.println(sql);
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < valueList.size(); i++) {
				ps.setString((i+1), valueList.get(i));
			}
			
			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 修改表中单个字段
	 * 
	 * @param table
	 *            ：表名
	 * @param keyName
	 *            : 主键的字段名
	 * @param keyValue
	 *            ：主键的值
	 * @param field
	 *            　：修改的字段名
	 * @param value
	 *            　：修改的值
	 * @throws Exception
	 */
	public void update(String table, String keyName, String keyValue,
			String field, String value) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			String sql = "update " + table + " set `" + field + "` = ?  where "
					+ keyName + " = ? ";
			ps = conn.prepareStatement(sql);
			int ii = 1;
			ps.setString(ii++, value);
			ps.setString(ii++, keyValue);
			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			//DbUtil.close(rs);
			//DbUtil.close(ps);
		}
	}

	/**
	 * 删除记录
	 * 
	 * @param table
	 *            : 表名
	 * @param field
	 *            : 主键字段名
	 * @param value
	 *            : 主键的值
	 */
	public void del(String table, String field, String value) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "DELETE FROM " + table + " WHERE " + field + " = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, value);
			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			//DbUtil.close(ps);
		}
	}
	
	public static String getClobToString(Clob clob) {
		if(clob != null) {
			try {
				return clob.getSubString( (long) 1, (int) clob.length());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}	
		
		return null;
	}
	
	/**
	 * 普遍更新语句
	 * 
	 * @param updateSql
	 * @param setValues
	 *            替换sql中的？
	 */
	public void update(String updateSql, java.util.List<String> setValues) throws Exception {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(updateSql);
			if (setValues != null && setValues.size() > 0) {
				int n = 1;
				for (String currentArgument : setValues) {
					ps.setString(n++, currentArgument);
				}
			}
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 查询的普遍方法
	 * 
	 * @param querySQL
	 * @param setValues
	 *            替换sql中的？
	 * @param getArguments
	 *            需要返回的字段
	 * @return 根据 getArguments返回的结果 /可能是多条
	 */
	public List<Map<String, String>> query(String querySQL, java.util.List<String> setValues, java.util.List<String> getArguments) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		java.util.List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		try {
			ps = conn.prepareStatement(querySQL);
			if (setValues != null && setValues.size() > 0) {
				int n = 1;
				for (Object currentArgument : setValues) {
					ps.setString(n++, currentArgument.toString());
				}
			}
			rs = ps.executeQuery();

			while (rs.next()) {
				Map<String, String> row = new HashMap<String, String>();
				for (int i = 0; getArguments != null && i < getArguments.size(); i++) {
					row.put(getArguments.get(i), rs.getString(getArguments.get(i)));
				}
				result.add(row);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return result;
	}
	
	public static String getStringValue(Object value, int type) {
		
		String result = null;
		
		switch (type) {
			case Types.CLOB:
				Clob clob = (Clob)value;
				result = DbUtil.getClobToString(clob);
				break;
				
			case Types.NUMERIC:
			case Types.DECIMAL:
			case Types.DOUBLE:
				BigDecimal v = (BigDecimal)value;
				if(v != null) {
					result = new DecimalFormat("##0.00").format(v.doubleValue());
				} else {
					result = "0.00";
				}
				
				break;
				
			default:
				result = String.valueOf(value);
				break;
		}
		
		return StringUtil.showNull(result);
	}
	
	/**
	 * 
	 * @param table
	 * @return  实体类正文，包含属性定义和getset方法
	 * @throws SQLException
	 */
	public  String tableRefClassConext(String table) throws SQLException{
		String sql=MessageFormat.format("select * from {0}", table);
		StringBuffer sbrPro=new StringBuffer(""),sbrGetSet=new StringBuffer("");
		String proPattern="\t protected {0} {1} ;"
				,setterPattern="\t public void set{0}({1} {3})'{' this.{2}={3}; '}'"
				,getterPattern="\t public {1} get{0}()'{' return this.{2}; '}'";
		PreparedStatement ps=conn.prepareStatement(sql);
		ResultSet resultSet=ps.executeQuery();
		ResultSetMetaData rsmd= resultSet.getMetaData();
		int colCount=resultSet.getMetaData().getColumnCount();
		for(int i=1;i<=colCount;i++){
	    	String colName=resultSet.getMetaData().getColumnName(i);
	    	String colType=resultSet.getMetaData().getColumnTypeName(i).toUpperCase();
	    	String fName=colName.substring(0,1).toUpperCase()+colName.substring(1);
	    	Class proType=ColumnType.valueOf(colType).getType();
	    	
	    	sbrPro.append(MessageFormat.format(proPattern, proType.getSimpleName(),colName)).append("\n");
	    	sbrGetSet.append(MessageFormat.format(getterPattern,fName, proType.getSimpleName(),colName)).append("\n");
	    	sbrGetSet.append(MessageFormat.format(setterPattern,fName, proType.getSimpleName(),colName,colName)).append("\n");

		}
		
		sbrPro.append("\n\n").append(sbrGetSet);
		return sbrPro.toString();
	}
	
	public <T> T load(Class<T> cls,Object pk) throws Exception{
		 T t=cls.newInstance();
		 return load(t, pk);
	}
	
	public <T> T load(T t,Object pk) {
		if(t==null)return t;
		Table table=null;
		String sql="";
		Field pkField=null;
		PreparedStatement preparedStatement=null;
		ResultSet resultSet=null;
		try{
		table=t.getClass().getAnnotation(Table.class);
		 sql=MessageFormat.format("select * from {0} where {1}=?", table.name(),table.pk());
		pkField=t.getClass().getDeclaredField(table.pk());
	     preparedStatement=conn.prepareStatement(sql);
		if(pkField.getType().equals(String.class)){
			preparedStatement.setString(1, pk.toString());
		}else if(pkField.getType().equals(Integer.class)){
			preparedStatement.setInt(1, Integer.parseInt(pk.toString()));
		}
	    resultSet=preparedStatement.executeQuery();
		   
		if(resultSet.next()){
	    	t=evalObject(t, resultSet);
	    }
		}catch(Exception ex){ex.printStackTrace();}
		 finally{
			close(resultSet);
			close(preparedStatement);
		}

	
		return t;
	}
	
	public <T> T load(Class<T> cls,String column,Object pk) throws Exception{
		 T t=cls.newInstance();
		 return load(t,column, pk);
	}
	
	public <T> T load(T t,String column,Object pk) {
		if(t==null)return t;
		Table table=null;
		String sql="";
		Field pkField=null;
		PreparedStatement preparedStatement=null;
		ResultSet resultSet=null;
		try{
		table=t.getClass().getAnnotation(Table.class);
		 sql=MessageFormat.format("select * from {0} where {1}= ?", table.name(),column);
		pkField=t.getClass().getDeclaredField(column);
	     preparedStatement=conn.prepareStatement(sql);
		if(pkField.getType().equals(String.class)){
			preparedStatement.setString(1, pk.toString());
		}else if(pkField.getType().equals(Integer.class)){
			preparedStatement.setInt(1, Integer.parseInt(pk.toString()));
		}
	    resultSet=preparedStatement.executeQuery();
		   
		if(resultSet.next()){
	    	t=evalObject(t, resultSet);
	    }
		}catch(Exception ex){}
		 finally{
			close(resultSet);
			close(preparedStatement);
		}

	
		return t;
	}
	
	public int insert(Object...objs) throws Exception{
		
		int success=0;
		PreparedStatement preparedStatement=null;
		for(Object obj:objs){
			if(obj==null)continue;
			Table table=null;
			String  sqlPattern="insert into {0} ({1}) values ({2})",sql="",
					sqlCacheKey=obj.getClass().getSimpleName()+":insert",cols="",vals="";
			Field[] fields=null;
		    
	    		table=obj.getClass().getAnnotation(Table.class);
				fields=obj.getClass().getDeclaredFields();
		    	if(SQL_CACHE.containsKey(sqlCacheKey)){
		    		sql=SQL_CACHE.get(sqlCacheKey);
		    	}else{

					
					for(Field field:fields){
						if( StringUtil.isIn(field.getName(),table.excludeColumns())||
								   (!table.insertPk()&& field.getName().equalsIgnoreCase(table.pk()) )
						)continue;	
						cols+=MessageFormat.format(" `{0}`,",field.getName() );
						vals+=" ?,";
						
					}
					cols=StringUtil.trim(cols, ",");
					vals=StringUtil.trim(vals, ",");
		    	    sql=MessageFormat.format(sqlPattern, table.name(),cols,vals);
		    	    SQL_CACHE.put(sqlCacheKey, sql);
		    	    System.out.println(sql);
		    	}
		    	preparedStatement=conn.prepareStatement(sql);
		        int j=0;
				for(Field field:fields){
					if( StringUtil.isIn(field.getName(),table.excludeColumns())||
					   (!table.insertPk()&& field.getName().equalsIgnoreCase(table.pk()) )
					)continue;	
					String getterName="get"+field.getName().substring(0,1).toUpperCase()+field.getName().substring(1);
					Method m=obj.getClass().getDeclaredMethod(getterName);
					Object value=m.invoke(obj);
					preparedStatement.setObject(j+1, value);
					j++;
				}
				
				success+=preparedStatement.executeUpdate();
				
		   
		       close(preparedStatement);
		    
		}
		return success;
	}
	
	public int delete(Object...objs){
		
		int success=0;
		PreparedStatement preparedStatement=null;
		for(Object obj:objs){
			if(obj==null)continue;
			Table table=null;
			String  sqlPattern="delete from {0} where {1}=?",sql="",
					sqlCacheKey=obj.getClass().getSimpleName()+":delete";
			try{
			  table=obj.getClass().getAnnotation(Table.class);
			  if(SQL_CACHE.containsKey(sqlCacheKey)){
				sql=SQL_CACHE.get(sqlCacheKey);  
			  }else{
			    sql=MessageFormat.format(sqlPattern, table.name(),table.pk());
			    SQL_CACHE.put(sqlCacheKey,sql); 
			  }
			  System.out.println(sql);
			  String getterName="get"+table.pk().substring(0,1).toUpperCase()+table.pk().substring(1);;
			  Method method=obj.getClass().getDeclaredMethod(getterName);
			  Object value=method.invoke(obj);
			  preparedStatement=conn.prepareStatement(sql);
			  preparedStatement.setObject(1, value);
			  success+=preparedStatement.executeUpdate();
			}catch(Exception ex){
				ex.printStackTrace();
				continue;
			}
		}
		return success;
	}
	
	public int update(Object...objs){
		
		int success=0;
		PreparedStatement preparedStatement=null;
		for(Object obj : objs){
			if(obj==null)continue;
			Table table=null;
			String sqlPattern="update {0} set {1} where {2}=? ",key="",sql=""
					,sqlCacheKey=obj.getClass().getSimpleName()+":update";
			Field[] fields=null;
			Field pkField=null;
			try{
				table=obj.getClass().getAnnotation(Table.class);
				fields=obj.getClass().getDeclaredFields();
				pkField=obj.getClass().getDeclaredField(table.pk());
				if(SQL_CACHE.containsKey(sqlCacheKey)){
						sql=SQL_CACHE.get(sqlCacheKey);  
			}else{
	
			int i=0;
			for(Field field:fields){
				if(StringUtil.isIn(field.getName(),table.excludeColumns())||field.getName().equalsIgnoreCase(table.pk())){pkField=field;continue;}
				key+=MessageFormat.format(" `{0}` =? ,",field.getName() );
				i++;
			}
			key=StringUtil.trim(key, ",");
		    sql=MessageFormat.format(sqlPattern, table.name(),key,table.pk());
		    SQL_CACHE.put(sqlCacheKey,sql);
		    }
		    preparedStatement=conn.prepareStatement(sql);
		    int j=0;
			for(Field field:fields){
				if(StringUtil.isIn(field.getName(),table.excludeColumns())||field.getName().equalsIgnoreCase(table.pk()))continue;	
				String getterName="get"+field.getName().substring(0,1).toUpperCase()+field.getName().substring(1);
				Method m=obj.getClass().getDeclaredMethod(getterName);
				Object value=m.invoke(obj);
				preparedStatement.setObject(j+1, value);
				j++;
			}
			String getterName="get"+pkField.getName().substring(0,1).toUpperCase()+pkField.getName().substring(1);
			Method m=obj.getClass().getDeclaredMethod(getterName);
			Object value=m.invoke(obj);
			preparedStatement.setObject(j+1, value);
			System.out.println(sql);
			success+=preparedStatement.executeUpdate();
			
			}catch(Exception ex){
				ex.printStackTrace();
			}finally{
				close(preparedStatement);
			}
			
		}
		return success;
	}
	
	/**
	 * 
	 * @param cls
	 * @param resultSet
	 * @return  将resutlSet映射到泛型集合中
	 * @throws Exception
	 */
	public static  <T> List<T> evalList(Class<T> cls,ResultSet resultSet) throws Exception{
	    List<T> list=new ArrayList<T>();
	    while(resultSet.next()){
	    	list.add(evalObject(cls,resultSet));
	    }
	    return list;
	}
	
	
	/**
	 * 
	 * @param cls
	 * @param resultSet
	 * @return 将resutlSet的第一条记录创建成新对象并返回
	 * @throws Exception
	 */
	public static  <T> T evalObject(Class<T> cls,ResultSet resultSet) throws Exception{
		T t=cls.newInstance();
	    return evalObject(t, resultSet);
	}

	/**
	 * 
	 * @param t
	 * @param resultSet
	 * @return 将resutlSet的第一条记录映射如新对象中并返回
	 * @throws Exception
	 */
	public static  <T> T evalObject(T t,ResultSet resultSet) {
		if(t==null)return t;
		int colCount=0;
		try {
			colCount = resultSet.getMetaData().getColumnCount();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    Method m=null;
	    for(int i=1;i<=colCount;i++){

	        try{
		    	String colName=resultSet.getMetaData().getColumnLabel(i);
		    	String colType=resultSet.getMetaData().getColumnTypeName(i).toUpperCase();
		    	String setMethodName="set"+colName.substring(0,1).toUpperCase()+colName.substring(1);
		    	Class cls=ColumnType.valueOf(colType.toUpperCase()).getType();
		    	m=t.getClass().getDeclaredMethod(setMethodName, cls);
		    	Field field=t.getClass().getDeclaredField(colName);
		    	if(field.getType().equals(Integer.class)){
		    		m.invoke(t,resultSet.getInt(colName));
		    	}else if(field.getType().equals(Double.class)){
		    		m.invoke(t, resultSet.getDouble(colName));
		    	}else if(field.getType().equals(String.class)){
		    		m.invoke(t, resultSet.getString(colName));
		    	}else{
		    		m.invoke(t, resultSet.getString(colName));
		    	}
		    	/*
		    	if(ColumnType.DOUBLE.name().equalsIgnoreCase(colType)||ColumnType.DECIMAL.name().equalsIgnoreCase(colType)){
		    		m=t.getClass().getDeclaredMethod(setMethodName,Double.class); 
		    		m.invoke(t,resultSet.getDouble(colName));
		    	}else if(ColumnType.INT.name().equalsIgnoreCase(colType)||ColumnType.BIGINT.name().equalsIgnoreCase(colType)){
		    		m=t.getClass().getDeclaredMethod(setMethodName,Integer.class); 
		    		m.invoke(t,resultSet.getInt(colName));
		    	}
		    	else{
		    	    m.invoke(t, resultSet.getObject(colName));
		    	}
		    	/*
	    	if(colType==ColumnType.VARCHAR.name()
	    			||colType==ColumnType.CHAR.name()
	    			||colType==ColumnType.TEXT.name()){
	    		  m=t.getClass().getDeclaredMethod(setMethodName,String.class);
	    		  m.invoke(t,resultSet.getString(colName));
	    	}else  if(colType==ColumnType.INT.name()||
	    			){
	    		m=t.getClass().getDeclaredMethod(setMethodName,Integer.class);
	    		 m.invoke(t,resultSet.getInt(colName));
	    	}else if(colType==ColumnType.FLOAT.name()){
	    		m=t.getClass().getDeclaredMethod(setMethodName,Float.class);
	    		 m.invoke(t,resultSet.getFloat(colName));
	    	}else if(colType==ColumnType.DOUBLE.name()||colType==ColumnType.DECIMAL.name()){
	    		m=t.getClass().getDeclaredMethod(setMethodName,Double.class); 
	    		m.invoke(t,resultSet.getDouble(colName));
	    	}else if(colType==ColumnType.DATETIME.name()){
	    		m=t.getClass().getDeclaredMethod(setMethodName,Date.class);
	    		 m.invoke(t,resultSet.getDate(colName));
	    	}
	    	*/
	        }catch(Exception ex){
	        	//ex.printStackTrace();
	        	continue;
	        }
	    }
		return t;
	}
	
	
	/**
	 * 通过SQL，得到一个记录的集合
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
     */
	public List getListBySql(String sql) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			List list = new ArrayList();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			ResultSetMetaData RSMD = rs.getMetaData();
			while (rs.next()) {
				Map map = new HashMap();
				for (int i = 1; i <= RSMD.getColumnCount(); i++) {
					map.put(RSMD.getColumnLabel(i).toLowerCase(),
							getStringValue(rs.getObject(RSMD.getColumnLabel(i)), RSMD.getColumnType(i)));
				}
				list.add(map);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}
	
	public <T> List<T> select(Class<T> cls,String sql,Object ...params){
		PreparedStatement ps=null;
		List<T> list=null;
		ResultSet rs=null;
		Table table=cls.getAnnotation(Table.class);
		try {
			 if(table!=null){
				 sql=MessageFormat.format(sql, table.name(),table.pk());
			 }
			 System.out.println("=====select sql="+sql);
			 ps=this.conn.prepareStatement(sql);
			 for(int i=0;i<params.length;i++){
				 ps.setObject(i+1, params[i]);
			 }
			 rs=ps.executeQuery();
			 list=evalList(cls, rs);
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.close(null, ps, rs);
		}
		return list;
	}
	
	public <T> int executeUpdate(Class<T> cls,String sql,Object ...params){
		PreparedStatement ps=null;
		int eff=0;
		ResultSet rs=null;
		Table table=cls.getAnnotation(Table.class);
		try {
			 if(table!=null){
				 sql=MessageFormat.format(sql, table.name(),table.pk());
			 }
			 ps=this.conn.prepareStatement(sql);
			 for(int i=0;i<params.length;i++){
				 ps.setObject(i+1, params[i]);
			 }
			 eff=ps.executeUpdate();
			 
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			DbUtil.close(null, ps, rs);
		}
		return eff;
	}
	
	/**
	 * 查询记录 多条记录的字段类型
	 * 
	 * @param table
	 *            : 表名
	 * @param field
	 *            : 主键字段名
	 * @param value
	 *            : 主键的值
	 * @return Map ：K=表的字段名(小写),V=表单的值
	 */
	public Map getFieldType(String table)
			throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select * from " + table + " where 1=2 " ;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			ResultSetMetaData RSMD = rs.getMetaData();
			Map map = new HashMap();
			for (int i = 1; i <= RSMD.getColumnCount(); i++) {
				map.put(RSMD.getColumnLabel(i).toLowerCase(), RSMD.getColumnType(i));
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}
	
	public boolean checkFieldExists(String table,String fieldName)throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			//没有出错，字段存在
			String sql = " select "+fieldName+" from "+table+" where 1=2 " ;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			return true ;
			
		} catch (Exception e) {
			//出错，字段不存在
			return false;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
	}

}
