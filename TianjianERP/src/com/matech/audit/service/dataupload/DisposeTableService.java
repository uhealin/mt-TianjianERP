package com.matech.audit.service.dataupload;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;

public class DisposeTableService {
	private Connection conn = null;

	public DisposeTableService(Connection conn1) throws Exception {
		DbUtil.checkConn(conn1);
		this.conn = conn1;
	}

	/**
	 * 获取指定表指定字段的最大值
	 * 
	 * @param table
	 *            String
	 * @param idfieldname
	 *            String
	 * @return long
	 */
	public int getMaxIdFromTable(String table, String idfieldname)
			throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement("select max(" + idfieldname + ") from "
					+ table);
			rs = ps.executeQuery();
			rs.next();
			return rs.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 完成指定表数据从临时表到工作表的转移;
	 * 
	 * @param table
	 *            String
	 * @param fields
	 *            String
	 * @param AccPackageID
	 *            String
	 * @return boolean
	 * @throws Exception
	 */
	public boolean CopyData(String table, String sql, String AccPackageID)
			throws Exception {
		DbUtil.checkConn(conn);

		// 首先清空指定表的数据;
		try {
			DeleteData(table, AccPackageID);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("删除指定表数据失败:" + e.getMessage(), e);
		}

		// 开始转移数据
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("执行失败:" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}

		// 删除临时表数据
		// 首先清空指定表的数据;
		try {
			DeleteData("t_" + table, AccPackageID);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("删除指定表数据失败:" + e.getMessage(), e);
		}
		return true;
	}

	/**
	 * 1、清空table的等于AccPackageID的记录 2、执行sql 3、清空tempTable的等于AccPackageID的记录
	 * 
	 * @param table
	 *            String
	 * @param tempTable
	 *            String
	 * @param sql
	 *            String
	 * @param AccPackageID
	 *            String
	 * @return boolean
	 * @throws Exception
	 */
	public boolean CopyData(String table, String tempTable, String sql,
			String AccPackageID) throws Exception {
		if (conn == null) {
			new Exception("数据库联结不能为空");
		}

		// 首先清空指定表的数据;
		try {
			DeleteData(table, AccPackageID);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("删除指定表数据失败:" + e.getMessage(), e);
		}

		// 开始转移数据
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("执行失败:" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}

		// 删除临时表数据
		// 首先清空指定表的数据;
		try {
			DeleteData(tempTable, AccPackageID);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("删除指定表数据失败:" + e.getMessage(), e);
		}

		if (conn.getAutoCommit() == false) {
			conn.commit();
		}
		return true;
	}

	/**
	   * 在数据库表没有这个字段的情况下，添加这个字段，并返回真；
	   * 否则返回假；
	   * 举例：
	   * addTableField("z_question","AccPackageID","int(14) default '0'")
	   * @param strTable String
	   * @param strField String
	   * @param strType String
	   * @return boolean
	   * @throws Exception
	   */
	  public boolean addTableField(String strTable,String strField,String strType) throws Exception{
	      PreparedStatement ps = null;
	      boolean bResult = false;
	      try {
	          if (checkTableFieldExist(strTable,strField)==false){
	              //如果该表没有这个字段，就增加
	              String strSql = "alter table "+strTable +" add "+strField+ " " + strType;
	              ps = conn.prepareStatement(strSql);
	              ps.execute();
	              bResult = true;
	          }
	      } catch (Exception e) {
	      } finally {
	          try {
	              if (ps != null)
	                  ps.close();
	          } catch (Exception e) {

	          }
	      }
	      return bResult;
	  }
	
	
	/**
	 * 清除指定临时表的上载帐套数据,避免冲突;
	 * 
	 * @param table
	 *            String
	 * @param AccPackageID
	 *            String
	 * @return boolean
	 */
	public boolean DeleteData(String table, String AccPackageID)
			throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("delete from " + table
					+ " where AccPackageID='" + AccPackageID + "'");
			ps.execute();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("执行失败:" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

	public boolean checkTableExist(String strTable){
	      ResultSet rs=null;
	      PreparedStatement ps=null;
	      boolean bResult=false;
	      try{
	          String strSql = "select 1 from " + strTable +" where 1=2";
	          ps=conn.prepareStatement(strSql); 
	          rs=ps.executeQuery();
	          bResult=true;
	      }catch (Exception e){
	      }finally{
	    	  DbUtil.close(rs);
	    	  DbUtil.close(ps);
	      }
	      return bResult;
	  }

	  public boolean checkTableFieldExist(String strTable,String strField){
	      ResultSet rs=null;
	      PreparedStatement ps=null;
	      boolean bResult=false;
	      try{
	          String strSql = "SELECT "+strField+" FROM "+strTable+" where 1=2";
	          ps=conn.prepareStatement(strSql);	         
	          rs=ps.executeQuery();
	          bResult=true;
	      }catch (Exception e){
	      }finally{
	    	  DbUtil.close(rs);
	    	  DbUtil.close(ps);
	      }
	      return bResult;
	  }
	  
	  public void dropTable(String strTable){
		  PreparedStatement ps=null;
	      try {
	    	  String sql = "drop table " + strTable;
	    	  ps=conn.prepareStatement(sql);	
	    	  ps.execute();
	      } catch (Exception e) {
	      }finally{
	    	  DbUtil.close(ps);
	      }
	  }
	  
	  public void dropTableNew(String strTable,String AccPackageID){
		  PreparedStatement ps=null;
		  String sql1=""; 
		  String sql2=""; 
		  String sql3=""; 
		  String sql4="";
		  String sql5="";

		  String numUnid = DELUnid.getNumUnid();
		  sql1="create table "+strTable+"_temp"+numUnid+" like "+strTable;
		  sql2="insert into "+strTable+"_temp"+numUnid+" select * from "+strTable+" where AccPackageID<>"+AccPackageID;	
		  sql3="TRUNCATE table "+strTable; 
		  sql4="insert into "+strTable+" select * from "+strTable+"_temp"+numUnid+"";
		  sql5="drop table "+strTable+"_temp"+numUnid+"";
	      try {
	    	  ps=conn.prepareStatement(sql1);	
	    	  ps.execute();
	    	  ps=conn.prepareStatement(sql2);	
	    	  ps.execute();
	    	  ps=conn.prepareStatement(sql3);	
	    	  ps.execute();
	    	  ps=conn.prepareStatement(sql4);	
	    	  ps.execute();	
	    	  ps=conn.prepareStatement(sql5);	
	    	  ps.execute();
	      } catch (Exception e) {
	      }finally{
	    	  DbUtil.close(ps);
	      }
	  }
}
