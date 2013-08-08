package com.matech.audit.service.log;



import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;

public class LogService {

	private Connection conn = null;

	public LogService(Connection conn) {
		this.conn = conn;
	}

	public void delAMenu(int id) throws Exception {
		DbUtil.checkConn(conn);
		if (id != 0) {

			PreparedStatement ps = null;
			try {

				ps = conn.prepareStatement("delete from t_log where id=" + id
						+ "");
				ps.execute();
			} catch (Exception e) {
				Debug.print(Debug.iError, "访问失败", e);
				throw new MatechException("访问失败：" + e.getMessage(), e);
			} finally {
				//  DbUtil.close(rs);
				DbUtil.close(ps);
			}
		}
	}

	//	    public void delLogs(String date) throws Exception{
	//	        Connection conn = null;
	//	    	PreparedStatement ps = null;
	//	    	try {
	//	    		conn = new DBConnect().getConnect();
	//	    		ps = conn.prepareStatement("delete from t_log where UDate <= '"+ date +"'");
	//	            ps.execute();
	//	        }catch(Exception ex) {
	//	        	ex.printStackTrace();
	//	        }finally {
	//	        	if( ps != null )
	//	        		ps.close();
	//	        	if( conn != null)
	//	        		conn.close();
	//	        }
	//	    }

	public void delLogs(String BeginDate, String EndDate) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		String sql = "";
		if (BeginDate.equals("")) {
			sql = "delete from t_log where UDate <='" + EndDate + "'";
		} else if (EndDate.equals("")) {
			sql = "delete from t_log where UDate >='" + BeginDate + "'";
		}
		if (!BeginDate.equals("") && !EndDate.equals("")) {
			sql = "delete from t_log where UDate >= '" + BeginDate
					+ "' and UDate <='" + EndDate + "'";
		}
		org.util.Debug.prtOut("new LogMan().delLogs :" + sql);
		try {

			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			// DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public static String exportLogs(String BeginDate, String EndDate,
			String dirPath, String filename, Connection conn)
			throws MatechException {
		DbUtil.checkConn(conn);
		ASFuntion funtion = new ASFuntion();

		File dir = new File(dirPath);

		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;

		String sql = "";

		String stroutput = "";

		if (dir.exists()) {

		} else {
			dir.mkdirs();
		}
		PrintStream file = null;
		try {
			file = new PrintStream(new BufferedOutputStream(
					new FileOutputStream(dirPath + "/" + filename)));
		} catch (FileNotFoundException ex) {
		}

		if (BeginDate.equals("")) {
			sql = "select * from t_log where UDate <='" + EndDate + "'";
		}
		if (EndDate.equals("")) {
			sql = "select * from t_log where UDate >='" + BeginDate + "'";
		}
		if (!BeginDate.equals("") && !EndDate.equals("")) {
			sql = "select * from t_log where UDate >= '" + BeginDate
					+ "' and UDate <='" + EndDate + "'";
		}

		org.util.Debug.prtOut(sql);

		try {

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			rsmd = rs.getMetaData();

			while (rs.next()) {
				stroutput = "";
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					if (i == rsmd.getColumnCount()) {
						stroutput += funtion.showNull(rs.getString(i));
					} else {
						stroutput += funtion.showNull(rs.getString(i)) + ",\t";
					}
				}
				file.println(stroutput);
			}
			file.close();
			return "suc";
		} catch (Exception e) {
			e.printStackTrace();
			return "fail";
		} finally {

			DbUtil.close(rs);
			DbUtil.close(ps);

		}
	}

	//	    public static String exportLogs(String date, String dirPath, String filename){
	//	      File dir = new File(dirPath);
	//	      Connection conn = null;
	//	      PreparedStatement ps = null;
	//	      ResultSet rs = null;
	//	      ResultSetMetaData rsmd = null;
	//	      ASFuntion asf = new ASFuntion();
	//	      String sql = "";
	//
	//	      String stroutput = "";
	//
	//	      if(dir.exists()){
	//
	//	      }else{
	//	        dir.mkdirs();
	//	      }
	//	      PrintStream file = null;
	//	      try {
	//	        file = new PrintStream(new BufferedOutputStream(new FileOutputStream(
	//	            dirPath + "/" + filename)));
	//	      }
	//	      catch (FileNotFoundException ex) {
	//	      }
	//	      sql = "select * from t_log where UDate<='" + date + "'";
	//
	//	      try{
	//	        conn = new DBConnect().getConnect();
	//	        ps = conn.prepareStatement(sql);
	//	        rs = ps.executeQuery();
	//	        rsmd = rs.getMetaData();
	//
	//	        while(rs.next()){
	//	          stroutput = "";
	//	          for(int i=1; i <= rsmd.getColumnCount(); i++){
	//	            if(i == rsmd.getColumnCount()){
	//	              stroutput += asf.showNull(rs.getString(i));
	//	            }else{
	//	              stroutput += asf.showNull(rs.getString(i)) + ",\t";
	//	            }
	//	          }
	//	          file.println(stroutput);
	//	        }
	//	        file.close();
	//	        return "suc";
	//	      }catch(Exception e){
	//	        e.printStackTrace();
	//	        return "fail";
	//	      }finally{
	//	        try{
	//	          if(rs != null) rs.close();
	//	          if(ps != null) ps.close();
	//	          if(conn != null) conn.close();
	//	        }catch(SQLException e){
	//	          e.printStackTrace();
	//	        }
	//	      }
	//	    }

	
	/**
	 * @param userSession   userSession变量
	 * @param conn 			 数据库连接
	 * @param menuID 		 可填入菜单Id或菜单名称 
	 * @param description   要填入日志中的信息，例如：“删除 ×× 客户”
	 */
	public static void addTOLog(UserSession userSession,Connection conn, String menuID,
			String description) throws MatechException {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		String menuStr = "";
		String userIP = "";
		
		ASFuntion CHF = new ASFuntion();
		try {
			if(menuID != null){
				menuStr=LogService.change(conn, menuID);
			}
			String UID = "";
			String UName = "";
			if(userSession != null){
				UID =userSession.getUserLoginId();
				UName = userSession.getUserName();
				userIP = userSession.getUserIp();
			} 
			
			
			String UDate = CHF.getCurrentDate();
			String UTime = CHF.getCurrentTime();
			String CMDName = menuStr;
			int i = 1;
			ps = conn
					.prepareStatement("INSERT INTO t_log (UDate,UTime,loginid,UserName,CMDName,memo,userid) values(?,?,?,?,?,?,?)");
			ps.setString(i++, UDate);
			ps.setString(i++, UTime);
			ps.setString(i++, UID);
			ps.setString(i++, UName);
			ps.setString(i++, CMDName);
			ps.setString(i++, description);
			ps.setString(i++, userIP);
			ps.execute();

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 仅供AJAX方法，不提供MANUID的情况下调用
	 * @param userSession
	 * @param conn
	 * @param menuID
	 * @param description
	 * @param ManuName
	 * @throws MatechException
	 */
	public static void addTOLog(UserSession userSession,Connection conn, String menuID,
			String description,String ManuName) throws MatechException {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		ASFuntion CHF = new ASFuntion();
		try {
			String UID = "";
			String UName = "";
			String userIP = "";
			if(userSession != null){
				UID =userSession.getUserLoginId();
				UName = userSession.getUserName();
				 userIP = userSession.getUserIp();
			} 
			
			
			String UDate = CHF.getCurrentDate();
			String UTime = CHF.getCurrentTime();
			int i = 1;
			ps = conn
					.prepareStatement("INSERT INTO t_log (UDate,UTime,loginid,UserName,CMDName,memo,userid) values(?,?,?,?,?,?,?)");
			ps.setString(i++, UDate);
			ps.setString(i++, UTime);
			ps.setString(i++, UID);
			ps.setString(i++, UName);
			ps.setString(i++, ManuName);
			ps.setString(i++, description);
			ps.setString(i++, userIP);
			ps.execute();

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * @param userSession    userSession变量
	 * @param conn 			 数据库连接
	 * @param menuID 		 可填入菜单Id或菜单名称 
	 * @param description    要填入日志中的信息
	 * @param projectname    项目名称
	 */
	public static void addLogs(UserSession userSession,Connection conn, String menuID,String[] subMsg,String projectname) throws MatechException {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
//		String sql = "";
		String menuStr = "";
		
		ASFuntion CHF = new ASFuntion();
		try {
		menuStr=LogService.change(conn, menuID);
			String UID =userSession.getUserLoginId();
			
			String UName = userSession.getUserName();
			String userIP = userSession.getUserIp();
			
			String UDate = CHF.getCurrentDate();
			String UTime = CHF.getCurrentTime();
			
			String CMDName = menuStr;
			
			ps = conn.prepareStatement("INSERT INTO t_log (UDate,UTime,loginid,UserName,CMDName,memo,userid) values(?,?,?,?,?,?,?)");
			
			for(int j = 0 ; j < subMsg.length; j++){
				int i = 1;
				ps.setString(i++, UDate);
				ps.setString(i++, UTime);
				ps.setString(i++, UID);
				ps.setString(i++, UName);
				ps.setString(i++, CMDName);
				ps.setString(i++, "导出"+projectname+"<br/>"+"底稿："+subMsg[j]);
				ps.setString(i++, userIP);
				ps.execute();
			}
						
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	
	public static void addLog(UserSession userSession,Connection conn, String msg) throws MatechException {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		ASFuntion CHF = new ASFuntion();
		try {
	
			String UID =userSession.getUserLoginId();			
			String UName = userSession.getUserName();
			String userIP = userSession.getUserIp();
			
			String UDate = CHF.getCurrentDate();
			String UTime = CHF.getCurrentTime();
			String CMDName = "工作底稿->保存为模板";
						
			ps = conn.prepareStatement("INSERT INTO t_log (UDate,UTime,loginid,UserName,CMDName,memo,userid) values(?,?,?,?,?,?,?)");
			
			int i = 1;
			ps.setString(i++, UDate);
			ps.setString(i++, UTime);
			ps.setString(i++, UID);
			ps.setString(i++, UName);
			ps.setString(i++, CMDName);
			ps.setString(i++, msg);
			ps.setString(i++, userIP);
	
			ps.execute();
			
						
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * @param userSession   userSession变量
	 * @param conn 			 数据库连接
	 * @param menuID 		 可填入菜单Id或菜单名称 
	 * @param description   要填入日志中的信息，例如：“删除 ×× 客户”
	 */
	public static String change(Connection conn, String menuID) throws MatechException {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		String menuStr = "";
		
		ASFuntion CHF = new ASFuntion();

		try {
			try{
				Integer.parseInt(menuID);
			while (true) {
				ps = conn
						.prepareStatement("select parentid,name from s_sysmenu where menu_id ='"
								+ menuID + "'");
				rs = ps.executeQuery();
				if (rs.next()) {
					menuID = rs.getString("parentid");
					if ("".equals(menuStr)) {
						menuStr = rs.getString("name");
					} else {
						menuStr = rs.getString("name") + "->" + menuStr;
					}
				} else {
					break;
				}
			}
			}catch(Exception e){
				menuStr=menuID;
			}
			return menuStr;
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	

	//	copy 上面那个方法的测试版本

	//	      private void addLog( java.sql.Connection arg, String menuID,
	//	                              String description) {
	//	             Connection conn = arg;
	//	             PreparedStatement ps = null;
	//	             ResultSet rs = null;
	//	             String sql = "";
	//	             String menuStr = "";
	//	             ASFuntion CHF = new ASFuntion();
	//	             try {
	////	               conn = new DBConnect().getConnect();
	//	               while (true) {
	//	                 ps = conn.prepareStatement(
	//	                     "select parentid,name from s_sysmenu where menu_id ='" + menuID +
	//	                     "'");
	//	                 rs = ps.executeQuery();
	//	                 if (rs.next()) {
	//	                   menuID = rs.getString("parentid");
	//	                   if ("".equals(menuStr)) {
	//	                     menuStr = rs.getString("name");
	//	                   }
	//	                   else {
	//	                     menuStr = rs.getString("name") + "->" + menuStr;
	//	                   }
	//	                 }
	//	                 else {
	//	                   break;
	//	                 }
	//	               }
	//
	//	               String UID = "user";
	//	               String UName = "username";
	//	               String UDate = CHF.getCurrentDate();
	//	               String UTime = CHF.getCurrentTime();
	//	               String CMDName = menuStr;
	//	               int i=1;
	//	               ps = conn.prepareStatement("INSERT INTO t_log (UDate,UTime,UserId,UserName,CMDName,memo) values(?,?,?,?,?,?)");
	//	               ps.setString(i++,UDate);
	//	               ps.setString(i++,UTime);
	//	               ps.setString(i++,UID);
	//	               ps.setString(i++,UName);
	//	               ps.setString(i++,CMDName);
	//	               ps.setString(i++,description);
	//	               ps.execute();
	//
	//
	//	             }
	//	             catch (Exception e) {
	//	               e.printStackTrace();
	//	             }
	//	             finally {
	//	               try {
	//	                 if (rs != null)
	//	                   rs.close();
	//	                 if (ps != null)
	//	                   ps.close();
	//	                 if (conn != null)
	//	                   conn.close();
	//	               }
	//	               catch (Exception ex) {
	//	                 ex.printStackTrace();
	//	               }
	//	             }
	//	           }

	/**
	 * @param userSession   userSession变量
	 * @param infor         要填入日志中的信息，例如：“删除 ×× 客户”
	 * @param conn 			 数据库连接
	 */
	public static void updateToLog(UserSession userSession,String infor, Connection conn)
			throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String userLogId=userSession.getUserLoginId();
		int id = 0;
		String sql1 = "select max(id) from t_log where loginid='"+userLogId+"'";
		
		ps = conn.prepareStatement(sql1);
		rs = ps.executeQuery();

		if (rs.next()) {

			id = rs.getInt(1);

		}
		String sql2 = "update t_log set memo='" + infor + "'  where id =" + id;
		conn.prepareStatement(sql2).executeUpdate();

		if (rs != null)
			rs.close();
		if (ps != null)
			ps.close();

	}
	
	
//	/**
//	 * 设置向日志中写入的信息
//	 * 
//	 * @param name       表中的”name“设置
//	 * @param id         对应表中的id属性
//	 * @param tableName  表名
//	 * @param conn 
//	 * @throws Exception
//	 */
//	
//	public static void addToLog(String name, String id, String tableName,
//			Connection conn) throws Exception {
//
//		
//		String information = "";
//		String Name = "";
//	
//		String sql = "";
//		String accpackageyear = "";
//
//		String bool = "成功";
//
//		Name = name.split(",")[0];
//
//		accpackageyear = name.split(",")[1];
//
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		try {
//			if (tableName.equals("k_customer")) {
//				sql = "select departName from k_customer where departid='" + id
//						+ "'";
//				information = "删除客户";
//			}
//			if (tableName.equals("c_accpackage")) {
//				sql = "select departname,accpackageyear from c_accpackage a,k_customer c where a.accpackageid="
//						+ id + " and c.departid=a.customerid ";
//				information = "删除账套";
//			}
//			if (tableName.equals("k_aa")) {
//				sql = "select departName from k_customer where departid=" + id;
//				information = "删除工程";
//			}
//
//			ps = conn.prepareStatement(sql);
//			rs = ps.executeQuery();
//
//			if (rs.next()) {
//
//				bool = "失败";
//
//			}
//
//		} catch (Exception e) {
//			Debug.print(Debug.iError, "访问失败", e);
//		} finally {
//
//			if (rs != null)
//				rs.close();
//			if (ps != null)
//				ps.close();
//
//		}
//
//		
//
//	}
//
//	/**
//	 * 得到表中的”name“属性值
//	 * 
//	 * @param name      
//	 * @param id         对应表中的id属性
//	 * @param tableName  表名
//	 * @param conn 
//	 * @throws Exception
//	 */
//	public static String getName(String id, String tableName, Connection conn)
//			throws Exception {
//
//		String Name = "";
//		String sql = "";
//		String accpackageyear = " ";
//
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		try {
//			if (tableName.equals("k_customer")) {
//				sql = "select departName from k_customer where departid='" + id
//						+ "'";
//			}
//			if (tableName.equals("c_accpackage")) {
//				sql = "select departname,accpackageyear from c_accpackage a,k_customer c where a.accpackageid='"
//						+ id + "' and c.departid=a.customerid ";
//				accpackageyear = "0";
//			}
//			if (tableName.equals("k_aa")) {
//				sql = "select departName from k_customer where departid=" + id;
//			}
//
//			ps = conn.prepareStatement(sql);
//			rs = ps.executeQuery();
//
//			while (rs.next()) {
//				Name = rs.getString("departName");
//				System.out.println("name=" + Name);
//				if (accpackageyear.equals("0"))
//					accpackageyear = rs.getString(2);
//
//			}
//
//		} catch (Exception e) {
//			Debug.print(Debug.iError, "访问失败", e);
//		} finally {
//
//			if (rs != null)
//				rs.close();
//			if (ps != null)
//				ps.close();
//		}
//
//		return Name + "," + accpackageyear;
//	}

	
	public static void main(String[] args) {
		// TODO 自动生成方法存根

	}

}
