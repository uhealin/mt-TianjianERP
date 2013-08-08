package com.matech.audit.service.keys;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.matech.audit.pub.db.DBConnect;
//import com.matech.audit.service.manuaccount.ManuacCountService;
import com.matech.audit.service.task.TaskCommonService;
import com.matech.audit.service.usersubject.UserSubjectService;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.sys.UTILSysProperty;

//记得更新update程序.
//用这个算法，可以取消`了。
//实际使用时，把key2的'`'取消
public class KeyValue {

	public boolean process(Connection conn, String departID) {

		if (!delRepeatedKey(conn)) {
			org.util.Debug.prtErr("执行方法delRepeatedKey时出错");
			return false;
		}
		if (!reAccountKey(conn)) {
			org.util.Debug.prtErr("执行方法reAccountKey");
			return false;
		}

		if (departID == null || "".equals(departID) || "0".equals(departID)) {
		} else {
			if (!createKeyResult(conn, departID)) {
				org.util.Debug.prtErr("执行方法createKeyResult");
				return false;
			}
		}

		return true;
	}

	public boolean process(String departID) {

		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			return process(conn, departID);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				org.util.Debug.prtErr("什么？关闭连接也报错？  -_-#");
				e.printStackTrace();
			}
		}
	}

	public boolean delRepeatedKey() {

		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			return delRepeatedKey(conn);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				org.util.Debug.prtErr("什么？关闭连接也报错？  -_-#");
				e.printStackTrace();
			}
		}
	}

	public boolean delRepeatedKey(Connection conn) {
		//		Connection conn=null;
		java.sql.Statement st = null;
		java.sql.ResultSet rs = null;

		try {
			st = conn.createStatement();

			String sql = "";

			sql = "update k_key set key1=replace(key1,'`',''),key2=replace(key2,'`','')";
			st.execute(sql);
			st.execute("Flush tables");

			sql = " select GROUP_CONCAT(maxid) from( \n"
					+ "     select max(autoid) as maxid from k_key \n"
					+ "     group by key1,key2,departid  \n" 
					+ ") a \n";

			rs = st.executeQuery(sql);
			if (rs.next()) {
				
				String str = rs.getString(1);
				
				sql = "drop table if exists k_key1";
				st.execute(sql);
				
				sql = "create table k_key1 like k_key";
				st.execute(sql);
				
				sql = "insert into k_key1(key1,key2,departid) select key1,key2,departid from k_key where autoid in ("+str+") order by autoid";
				st.execute(sql);
				
				sql = "drop table if exists k_key";
				st.execute(sql);
				
				sql = "rename table k_key1 to k_key";
				st.execute(sql);
				st.execute("Flush tables");
				
//				sql = "  " + " delete from k_key where autoid not in ("
//						+ rs.getString(1) + ") \n";
//				org.util.Debug.prtErr(String.valueOf(st.executeUpdate(sql)));
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (st != null)
					st.close();
			} catch (Exception e) {
				org.util.Debug.prtErr("什么？关闭连接也报错？  -_-#");
				e.printStackTrace();
			}
		}
	}

	//用于初始化z_keyresult
	public boolean createKeyResult(String departID) {

		Connection conn = null;

		try {
			conn = new DBConnect().getConnect(departID);
			return createKeyResult(conn, departID);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (conn != null)
					conn.close();

			} catch (Exception e) {
				System.out.print("什么？关闭连接也报错？  -_-#");
				e.printStackTrace();
			}
		}

	}

	//用于初始化z_keyresult
	/*
	 *
	 */
	public boolean createKeyResult(Connection conn, String departID) {

		String keyResultProperty = UTILSysProperty.SysProperty
				.getProperty("KeyResult");
		if ("1".equals(keyResultProperty)) {
//			System.out.println("qwh:1");
			return this.createKeyResultMatch(conn, departID);
		} else {
//			System.out.println("qwh:2");
			return this.createKeyResultAll(conn, departID);
		}

	}

	//完全放大版
	public boolean createKeyResultAll(Connection conn, String departID) {

		//Connection conn=null;
		java.sql.Statement st = null;
		java.sql.ResultSet rs = null;

		try {
			String sql = "";
			st = conn.createStatement();

			//===========查出当前单位的行业id
			String vocationID = "0";
			sql = "select vocationid from k_customer where departid="
					+ departID;
			rs = st.executeQuery(sql);

			if (rs.next()) {
				vocationID = rs.getString(1);
			}

			//===========重算前删除所有之前的［非下级］对照。
			sql = "delete from z_keyresult where level0 = 1";

			st.execute(sql);

			//============从第二级开始对照。最多maxi级。如果取消maxi限制，则可以计算出所有的的可能性，但遇到 [股本] 之类的就好慢了。
			int maxi = 3;

			//============进行第一次放大
			//			  替换次数（层次）

			//            fullpath 存放的是已经对照过的关键字，格式是:  key1^key2
			int i = 1;
			//先插入一级的对照
			sql = "  "
					+ " insert into z_keyresult(customerid,standid,standkey,userkey,fullpath,`changelevel`,property) \n"
					+ " select distinct "
					+ departID
					+ ",b.subjectid, b.subjectfullname, \n"
					+ " trim(replace(concat(b.subjectfullname,'                                             '),a.key1,a.key2)) as userkey,concat('`',a.key1,'^',a.key2,'`') as fullpath,"
					+ String.valueOf(i)
					+ " as changelevel,b.property \n"
					+ " from k_key a,( select distinct subjectid ,subjectfullname ,property from k_standsubject where vocationid='"
					+ vocationID + "') b \n"
					+ " where b.subjectfullname like concat('%',key1,'%') \n"
					+ " and a.departid in (0," + departID + ")";

			//进行二次以上放大
			while (st.executeUpdate(sql) > 0 && i < maxi) {
				sql = "  "
						+ " insert into z_keyresult(customerid,standid,standkey,userkey,fullpath,`changelevel`,property) \n"
						+

						" select distinct "
						+ departID
						+ " as customerid,b.standid, b.standkey, \n"
						+ " trim(replace(concat(b.userkey,'                                             '),a.key1,a.key2)) as userkey, \n"
						+ " max(concat(b.fullpath,a.key1,'^',a.key2,'`')) as fullpath, "
						+ String.valueOf(i + 1)
						+ " as `changelevel`,b.property\n"
						+ " from k_key a,z_keyresult b \n"
						+ " where b.standkey like concat('%',key1,'%') \n"
						+ "   and b.userkey like concat('%',key1,'%') \n"
						+ "   and b.fullpath not like concat('%`',a.key1,'^',a.key2,'`%') \n"
						+ "   and b.fullpath not like concat('%`',a.key2,'^',a.key1,'`%') \n"
						+ "   and b.changelevel = " + String.valueOf(i++)
						+ " \n" + "   and a.departid in (0," + departID + ")"
						+ "  group by \n" + "  customerid,standkey,userkey\n"
						+ "  \n";
			}

			//按小彭和屈总的需要，插入没有对照的标准科目
			sql = "insert into z_keyresult(customerid,standid,standkey,userkey,property) "
					+ " select distinct "
					+ departID
					+ ",subjectid,subjectfullname,subjectfullname,property from k_standsubject where vocationid="
					+ vocationID;
			st.execute(sql);

			//根据z_keyresult生成subjectfullname2
			this.createFullPath(conn, departID);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (st != null)
					st.close();
			} catch (Exception e) {
				System.out.print("什么？关闭连接也报错？  -_-#");
				e.printStackTrace();
			}
		}
	}

	//匹配放大版
	public boolean createKeyResultMatch(Connection conn,
			String departID) {

		//Connection conn=null;
		java.sql.Statement st = null;
		java.sql.ResultSet rs = null;

		try {
			String sql = "";
			st = conn.createStatement();

			//===========查出当前单位的行业id
			String vocationID = "0";
			sql = "select vocationid from k_customer where departid="
					+ departID;
			rs = st.executeQuery(sql);

			if (rs.next()) {
				vocationID = rs.getString(1);
			}

			//===========重算前删除所有之前的［非下级］对照。
			sql = "delete from z_keyresult where level0 = 1";

			st.execute(sql);

			//============从第二级开始对照。最多maxi级。如果取消maxi限制，则可以计算出所有的的可能性，但遇到 [股本] 之类的就好慢了。
			int maxi = 3;

			//============进行第一次放大
			//			  替换次数（层次）

			//            fullpath 存放的是已经对照过的关键字，格式是:  key1^key2
			int i = 1;
			//先插入一级的对照
			sql = "  "
					+ " insert into z_keyresult(customerid,standid,standkey,userkey,fullpath,`changelevel`,property) \n"
					+ " select distinct "
					+ departID
					+ ",b.subjectid, b.subjectfullname, \n"
					+ " trim(replace(concat(b.subjectfullname,'                                             '),a.key1,a.key2)) as userkey,concat('`',a.key1,'^',a.key2,'`') as fullpath,"
					+ String.valueOf(i)
					+ " as changelevel,b.property \n"
					+ " from k_key a,( select distinct subjectid ,subjectfullname,property from k_standsubject where vocationid='"
					+ vocationID + "') b \n"
					+ " where b.subjectfullname like concat('%',key1,'%') \n"
					+ " and a.departid in (0," + departID + ")";

			//进行二次以上放大
			while (st.executeUpdate(sql) > 0 && i < maxi) {
				sql = "  "
						+ " insert into z_keyresult(customerid,standid,standkey,userkey,fullpath,`changelevel`,property) \n"
						+

						" select distinct "
						+ departID
						+ " as customerid,b.standid, b.standkey, \n"
						+ " trim(replace(concat(b.userkey,'                                             '),a.key1,a.key2)) as userkey, \n"
						+ " max(concat(b.fullpath,a.key1,'^',a.key2,'`')) as fullpath, "
						+ String.valueOf(i + 1)
						+ " as `changelevel`,b.property\n"
						+ " from k_key a,z_keyresult b \n"
						+ " where b.standkey like concat('%',key1,'%') \n"
						+ "   and b.userkey like concat('%',key1,'%') \n"
						+ "   and b.fullpath not like concat('%`',a.key1,'^',a.key2,'`%') \n"
						+ "   and b.fullpath not like concat('%`',a.key2,'^',a.key1,'`%') \n"
						+ "   and b.changelevel = " + String.valueOf(i++)
						+ " \n" + "   and a.departid in (0," + departID + ")"
						+ "  group by \n" + "  customerid,standkey,userkey\n"
						+ "  \n";
			}

			//删除不在客户和标准科目不存在的科目
			sql = " delete a from z_keyresult a \n";
			sql += " left join c_accpkgsubject b  \n";
			sql += " on a.userkey=b.subjectfullname  \n";
			sql += " where b.subjectid is null  \n";
			st.execute(sql);

			//删除重复的数据

			//按小彭和屈总的需要，插入没有对照的标准科目
			sql = "insert into z_keyresult(customerid,standid,standkey,userkey,property) "
					+ " select distinct "
					+ departID
					+ ",subjectid,subjectfullname,subjectfullname,property from k_standsubject where vocationid="
					+ vocationID;
			st.execute(sql);

			//根据z_keyresult生成subjectfullname2
			this.createFullPath(conn, departID);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (st != null)
					st.close();
			} catch (Exception e) {
				System.out.print("什么？关闭连接也报错？  -_-#");
				e.printStackTrace();
			}
		}
	}

	public boolean reAccountKey(Connection conn) {
		java.sql.Statement st = null;
		java.sql.ResultSet rs = null;

		try {
			st = conn.createStatement();

			String sql = "";

			int beginIndex = 10;

			//查出相关的资料
			sql = "select max(autoid) as m,count(autoid) as c from k_key";
			rs = st.executeQuery(sql);

			if (rs.next()) {
				int count = rs.getInt("c");

				if (count > 1) {
					int maxid = rs.getInt("m");
					if (maxid > count * 100) {
						//为了保证重算成功。先无条件为autoid加上最大值
						sql = "update k_key set autoid = autoid+"
								+ String.valueOf(maxid);
						st.execute(sql);
						st.execute("Flush tables");

						//重算autoid
						sql = "set @ii=" + String.valueOf(beginIndex);
						st.addBatch(sql);

						sql = "update k_key set autoid = (@ii:=@ii+1)";
						st.addBatch(sql);
						
						st.executeBatch();
						st.execute("Flush tables");

						st.clearBatch();

						//重新设置k_key表的autoid自增量
						sql = "ALTER TABLE asdb.k_key AUTO_INCREMENT = "
								+ String.valueOf(count + beginIndex);
						st.execute(sql);
						st.execute("Flush tables");


					}
				}
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (st != null)
					st.close();
			} catch (Exception e) {
				System.out.print("什么？关闭连接也报错？  -_-#");
				e.printStackTrace();
			}
		}
	}

	/**
	 * 增加条对照记录
	 * @param key1
	 * @param key2
	 * @param dpID  当前修改单位的单位编号。
	 * @param sign  是否在k_key插入单位编号。(是否全局对照的区分)
	 * 					注意dpID和departID的區別
	 * @return
	 *   0   正常增加
	 *   1   已经有此对照，无需要再增加
	 *   2   出现异常情况
	 * @throws Exception
	 */
	public int addKey(String key1, String key2, String dpID, boolean sign)
			throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "";

		if (dpID == null || "".equals(dpID)) {
			dpID = "0";
		}

		try {

			//插到k_key的单位编号。
			int departID = 0;
			if (sign) {
				departID = Integer.parseInt(dpID);
			}

			conn = new DBConnect().getConnect(dpID);
			int i = 1;

			sql = "select 1 from k_key where key1 = ? and key2= ? and departID=?";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, key1);
			ps.setString(i++, key2);
			ps.setInt(i++, departID);
			if (ps.executeQuery().next()) {
				return 1;
			} else {
				if(!"0".equals(dpID)){
					sql = "INSERT INTO c_key (key1,key2,departid) VALUES(?,?,?)";	
				}else{
					sql = "INSERT INTO k_key (key1,key2,departid) VALUES(?,?,?)";
				}

				i = 1;
				ps = conn.prepareStatement(sql);
				ps.setString(i++, key1);
				ps.setString(i++, key2);
				ps.setInt(i++, departID);
				ps.addBatch();

				i = 1;
				ps.setString(i++, key2);
				ps.setString(i++, key1);
				ps.setInt(i++, departID);
				ps.addBatch();

				ps.executeBatch();
				ps.execute("Flush tables");


				if (!"0".equals(dpID)) {
					this.process(conn, dpID);
				}

				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 2;
		} finally {
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}

	}

	public int addKey1(String key1, String key2, String dpID, boolean sign,String subjectid) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "";

		if (dpID == null || "".equals(dpID)) {
			dpID = "0";
		}

		try {

			//插到k_key的单位编号。
			int departID = 0;
			if (sign) {
				departID = Integer.parseInt(dpID);
			}

			conn = new DBConnect().getConnect(dpID);
			int i = 1;
			
			org.util.Debug.prtOut("C1=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());
			sql = "select 1 from k_key where key1 = ? and key2= ? and departID=?";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, key1);
			ps.setString(i++, key2);
			ps.setInt(i++, departID);
			if (ps.executeQuery().next()) {
				org.util.Debug.prtOut("C2=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());
//				return 1;
			} else {
				org.util.Debug.prtOut("C3=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());

				sql = "delete from z_keyresult where userkey = ? and customerid=?";
				i = 1;
				ps = conn.prepareStatement(sql);
				ps.setString(i++, key2);
				ps.setInt(i++, departID);
				ps.execute();
				ps.close();
				
				if(!"0".equals(dpID)){
					sql = "INSERT INTO c_key (key1,key2,departid) VALUES(?,?,?)";	
				}else{
					sql = "INSERT INTO k_key (key1,key2,departid) VALUES(?,?,?)";
				}

				i = 1;
				ps = conn.prepareStatement(sql);
				ps.setString(i++, key1);
				ps.setString(i++, key2);
				ps.setInt(i++, departID);
				ps.addBatch();

				i = 1;
				ps.setString(i++, key2);
				ps.setString(i++, key1);
				ps.setInt(i++, departID);
				ps.addBatch();
				org.util.Debug.prtOut("C4=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());
				ps.executeBatch();
				ps.execute("Flush tables");


//				return 0;
			}
			
			if (!"0".equals(dpID)) {
				org.util.Debug.prtOut("C5=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());
				addFullPath(key1,key2,dpID);
				
				org.util.Debug.prtOut("C6=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());
				
				if (!"".equals(subjectid)){
//					new ManuacCountService(conn).updateCustomerID(dpID,subjectid);
				}
				
				org.util.Debug.prtOut("C16=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());
				
				updateTaskState(conn,dpID);
				org.util.Debug.prtOut("C17=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());
				
			}
			
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 2;
		} finally {
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}

	}

	public int addKey2(String key1, String key2, String dpID, boolean sign,String subjectid) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "";

		if (dpID == null || "".equals(dpID)) {
			dpID = "0";
		}

		try {

			//插到k_key的单位编号。
			int departID = 0;
			if (sign) {
				departID = Integer.parseInt(dpID);
			}

			conn = new DBConnect().getConnect(dpID);
			int i = 1;
			
			org.util.Debug.prtOut("C1=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());
			sql = "select 1 from k_key where key1 = ? and key2= ? and departID=?";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, key1);
			ps.setString(i++, key2);
			ps.setInt(i++, departID);
			if (ps.executeQuery().next()) {
				org.util.Debug.prtOut("C2=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());
				return 1;
			} else {
				org.util.Debug.prtOut("C3=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());
				
				if(!"0".equals(dpID)){
					sql = "INSERT INTO c_key (key1,key2,departid) VALUES(?,?,?)";	
				}else{
					sql = "INSERT INTO k_key (key1,key2,departid) VALUES(?,?,?)";
				}
				
				i = 1;
				ps = conn.prepareStatement(sql);
				ps.setString(i++, key1);
				ps.setString(i++, key2);
				ps.setInt(i++, departID);
				ps.addBatch();

				i = 1;
				ps.setString(i++, key2);
				ps.setString(i++, key1);
				ps.setInt(i++, departID);
				ps.addBatch();
				org.util.Debug.prtOut("C4=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());
				ps.executeBatch();
				ps.execute("Flush tables");


				if (!"0".equals(dpID)) {
					org.util.Debug.prtOut("C5=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());
					addFullPath1(key1,key2,dpID);
					
					org.util.Debug.prtOut("C6=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());
					
					if (!"".equals(subjectid)){
//						new ManuacCountService(conn).updateCustomerID(dpID,subjectid);
					}
					
					org.util.Debug.prtOut("C16=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());
					
					updateTaskState(conn,dpID);
					org.util.Debug.prtOut("C17=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());
					
				}

				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 2;
		} finally {
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}

	}
	
	public int addFullPath1(String standName, String subjectName, String dpID) throws Exception {
		Connection conn = null;
		Statement st = null;
		Statement querySt = null;
		ResultSet rs = null;
		String sql = "";

		try {

			if (dpID == null || "".equals(dpID)) {
				throw new Exception("单位编号不能为空。");
			}

			if (standName == null || "".equals(standName)) {
				throw new Exception("标准科目不能为空。");
			}

			if (subjectName == null || "".equals(subjectName)) {
				throw new Exception("客户科目不能为空。");
			}
			int departID = Integer.parseInt(dpID);

			conn = new DBConnect().getConnect(dpID);
			st = conn.createStatement();
			querySt = conn.createStatement();

			//org.util.Debug.prtOut("C7=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());
			sql = "delete from z_keyresult where  standkey='"+standName+"' and userkey='"+subjectName+"'";
			st.execute(sql);
			//org.util.Debug.prtOut("C8=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());
			
			String standID = "";
			String property = "";
			String direction = "";
			sql = " select subjectid,a.property,case a.property when 2 then -1 else a.property end as direction " +
					"from k_standsubject a ,k_customer b  where subjectfullname = '"
					+ standName + "' and a.vocationid=b.vocationid  and b.departid="+dpID ;
			rs = querySt.executeQuery(sql);
			if (rs.next()) {
				standID = rs.getString(1);
				property = rs.getString(2);
				direction = rs.getString(3);
			} else {
				return 0 ;
			}
			org.util.Debug.prtOut("C9=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());
			
			sql = "insert into z_keyResult(level0,standid,standkey,userkey,customerid,property) "
					+ " values(2,'"
					+ standID
					+ "','"
					+ standName
					+ "','"
					+ subjectName + "'," + departID + ",'" + property + "')";
			st.execute(sql);
			org.util.Debug.prtOut("C10=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());
			
			//分库后已经不存在不同单位的数据了。
			sql = " update c_account set subjectfullname2 = " +
					" concat('"+standName+"',substring(subjectfullname1,CHAR_LENGTH('"+subjectName+"')+1))," +
					" direction2=" + direction +
					" where subjectfullname1 = '"+ subjectName + "' or subjectfullname1 like '"+ subjectName + "/%'";
			//st.addBatch(sql);
			st.execute(sql);
			org.util.Debug.prtOut("C11=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());
			
			sql = " update c_accountall set subjectfullname2 = " +
				" concat('"+standName+"',substring(subjectfullname1,CHAR_LENGTH('"+subjectName+"')+1))," +
				" direction2=" + direction +
				" where subjectfullname1 = '"+ subjectName + "' or subjectfullname1 like '"+ subjectName + "/%'";
			//st.addBatch(sql);
			st.execute(sql);
			org.util.Debug.prtOut("C12=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());
			
			//更新辅助核算标准科目方向 winner add on 20070905
			sql = " update c_assitementryacc a join (select accpackageid,subjectid,direction2 from c_account b "
				+"where b.submonth=1 and (b.subjectfullname1 = '" + subjectName + "' or b.subjectfullname1 like '"+ subjectName + "/%')) b "
					+"on a.accpackageid=b.accpackageid and a.accid=b.subjectid set a.direction2=b.direction2";
			//st.addBatch(sql);
			st.execute(sql);
			org.util.Debug.prtOut("C13=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());

			sql = " update c_assitementryaccall a join (select accpackageid,subjectid,direction2 from c_account b "
				+"where b.submonth=1 and (b.subjectfullname1 = '" + subjectName + "' or b.subjectfullname1 like '"+ subjectName + "/%')) b "
				+"on a.accpackageid=b.accpackageid and a.accid=b.subjectid set a.direction2=b.direction2";
			//st.addBatch(sql);
			System.out.println(sql);
			st.execute(sql);
			
			sql = " update c_assitementryacc set direction2 = direction where direction2 = 0 ";
			st.execute(sql);
			
			sql = " update c_assitementryaccall set direction2 = direction where direction2 = 0 ";
			st.execute(sql);
			
			/**
			 * 修改［科目辅助核算披露设置］中的标准科目
			 */
			sql = "update c_subjectassitem a,c_account b  set a.subjectfullname2 = b.subjectfullname2 where b.submonth = 1 and a.accpackageid = b.accpackageid and a.subjectid = b.subjectid";
			st.execute(sql);
			
			org.util.Debug.prtOut("C14=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());
			
			//st.executeBatch();
			//st.clearBatch();
			
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 2;
		} finally {

			if (rs != null)
				rs.close();
			if (st != null)
				st.close();
			if (querySt != null)
				querySt.close();
			if (conn != null)
				conn.close();
		}

	}
	
	public int addFullPath(String standName, String subjectName, String dpID) throws Exception {
		Connection conn = null;
		Statement st = null;
		Statement querySt = null;
		ResultSet rs = null;
		String sql = "";

		try {

			if (dpID == null || "".equals(dpID)) {
				throw new Exception("单位编号不能为空。");
			}

			if (standName == null || "".equals(standName)) {
				throw new Exception("标准科目不能为空。");
			}

			if (subjectName == null || "".equals(subjectName)) {
				throw new Exception("客户科目不能为空。");
			}
			int departID = Integer.parseInt(dpID);

			conn = new DBConnect().getConnect(dpID);
			st = conn.createStatement();
			querySt = conn.createStatement();

			//org.util.Debug.prtOut("C7=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());
			sql = "delete from z_keyresult where level0 = 1 and standkey='"+standName+"' and userkey='"+subjectName+"'";
			st.execute(sql);
			//org.util.Debug.prtOut("C8=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());
			
			String standID = "";
			String property = "";
			String direction = "";
			sql = " select subjectid,a.property,case a.property when 2 then -1 else a.property end as direction " +
					"from k_standsubject a ,k_customer b  where subjectfullname = '"
					+ standName + "' and a.vocationid=b.vocationid and b.departid="+dpID ;
			rs = querySt.executeQuery(sql);
			if (rs.next()) {
				standID = rs.getString(1);
				property = rs.getString(2);
				direction = rs.getString(3);
			} else {
				return 0 ;
			}
			org.util.Debug.prtOut("C9=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());
			
			sql = "insert into z_keyResult(level0,standid,standkey,userkey,customerid,property) "
					+ " values(1,'"
					+ standID
					+ "','"
					+ standName
					+ "','"
					+ subjectName + "'," + departID + ",'" + property + "')";
			st.execute(sql);
			org.util.Debug.prtOut("C10=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());
			
			//分库后已经不存在不同单位的数据了。
			sql = " update c_account set subjectfullname2 = " +
					" concat('"+standName+"',substring(subjectfullname1,CHAR_LENGTH('"+subjectName+"')+1))," +
					" direction2=" + direction +
					" where subjectfullname1 = '"+ subjectName + "' or subjectfullname1 like '"+ subjectName + "/%'";
			//st.addBatch(sql);
			st.execute(sql);
			org.util.Debug.prtOut("C11=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());
			
			sql = " update c_accountall set subjectfullname2 = " +
				" concat('"+standName+"',substring(subjectfullname1,CHAR_LENGTH('"+subjectName+"')+1))," +
				" direction2=" + direction +
				" where subjectfullname1 = '"+ subjectName + "' or subjectfullname1 like '"+ subjectName + "/%'";
			//st.addBatch(sql);
			st.execute(sql);
			org.util.Debug.prtOut("C12=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());
			
			//更新辅助核算标准科目方向 winner add on 20070905
			sql = " update c_assitementryacc a join (select accpackageid,subjectid,direction2 from c_account b "
				+"where b.submonth=1 and (b.subjectfullname1 = '" + subjectName + "' or b.subjectfullname1 like '"+ subjectName + "/%')) b "
					+"on a.accpackageid=b.accpackageid and a.accid=b.subjectid set a.direction2=b.direction2";
			//st.addBatch(sql);
			st.execute(sql);
			org.util.Debug.prtOut("C13=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());

			sql = " update c_assitementryaccall a join (select accpackageid,subjectid,direction2 from c_account b "
				+"where b.submonth=1 and (b.subjectfullname1 = '" + subjectName + "' or b.subjectfullname1 like '"+ subjectName + "/%')) b "
				+"on a.accpackageid=b.accpackageid and a.accid=b.subjectid set a.direction2=b.direction2";
			//st.addBatch(sql);
			System.out.println(sql);
			st.execute(sql);
			
			sql = " update c_assitementryacc set direction2 = direction where direction2 = 0 ";
			st.execute(sql);
			
			sql = " update c_assitementryaccall set direction2 = direction where direction2 = 0 ";
			st.execute(sql);
			
			/**
			 * 修改［科目辅助核算披露设置］中的标准科目
			 */
			sql = "update c_subjectassitem a,c_account b  set a.subjectfullname2 = b.subjectfullname2 where b.submonth = 1 and a.accpackageid = b.accpackageid and a.subjectid = b.subjectid";
			st.execute(sql);
			
			org.util.Debug.prtOut("C14=" + new com.matech.framework.pub.util.ASFuntion().getCurrentTime());
			
			//st.executeBatch();
			//st.clearBatch();
			
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 2;
		} finally {

			if (rs != null)
				rs.close();
			if (st != null)
				st.close();
			if (querySt != null)
				querySt.close();
			if (conn != null)
				conn.close();
		}

	}


	/**
	 * 删除对照记录
	 * @param key1
	 * @param key2
	 * @param dpID  单位编号
	 * @return
	 *   0   正常删除
	 1   删除k_key成功。但k_resultResult还有值　
	 *   2   出现异常情况
	 * @throws Exception
	 */

	public int delKey(String key1, String key2, String dpID) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "";

		if (dpID == null || "".equals(dpID)) {
			dpID = "0";
		}

		try {

			int departID = Integer.parseInt(dpID);

			conn = new DBConnect().getConnect(dpID);
			int i = 1;
			if(!"0".equals(dpID)){
				sql = "delete from c_key where key1=? and key2=? and departid = ?";
			}else{
				sql = "delete from asdb.k_key where key1=? and key2=? and departid = ?";
			}
			ps = conn.prepareStatement(sql);
			ps.setString(i++, key1);
			ps.setString(i++, key2);
			ps.setInt(i++, departID);
			ps.addBatch();

			i = 1;
			ps.setString(i++, key2);
			ps.setString(i++, key1);
			ps.setInt(i++, departID);
			ps.addBatch();

			ps.executeBatch();
			ps.execute("Flush tables");
			ps.clearBatch();

			this.process(conn, dpID);

			i = 1;
			sql = " select 1 from z_keyresult where standkey = ? and userkey = ? and customerid = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, key1);
			ps.setString(i++, key2);
			ps.setInt(i++, departID);
			if (ps.executeQuery().next()) {
				return 1;
			} else {
				return 0;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return 2;
		} finally {
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}

	}

	public int delKey1(String key1, String key2, String dpID) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "";

		if (dpID == null || "".equals(dpID)) {
			dpID = "0";
		}

		try {

			int departID = Integer.parseInt(dpID);

			conn = new DBConnect().getConnect(dpID);
			int i = 1;
			if(!"0".equals(dpID)){
				sql = "delete from c_key where key1=? and key2=? and departid = ?";
			}else{
				sql = "delete from asdb.k_key where key1=? and key2=? and departid = ?";
			}
			ps = conn.prepareStatement(sql);
			ps.setString(i++, key1);
			ps.setString(i++, key2);
			ps.setInt(i++, departID);
			ps.addBatch();

			i = 1;
			ps.setString(i++, key2);
			ps.setString(i++, key1);
			ps.setInt(i++, departID);
			ps.addBatch();

			ps.executeBatch();
			ps.execute("Flush tables");
			ps.clearBatch();

			restoreFullPath1( key1,  key2,  dpID);

			i = 1;
			sql = " select 1 from z_keyresult where standkey = ? and userkey = ? and customerid = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, key1);
			ps.setString(i++, key2);
			ps.setInt(i++, departID);
			if (ps.executeQuery().next()) {
				return 1;
			} else {
				return 0;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return 2;
		} finally {
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}

	}


	public int restoreFullPath1(String standName, String subjectName, String dpID) throws Exception {
		Connection conn = null;
		Statement st = null;
		String sql = "";
		ResultSet rs = null;
		try {

			if (dpID == null || "".equals(dpID)) {
				throw new Exception("单位编号不能为空。");
			}

			if (standName == null || "".equals(standName)) {
				throw new Exception("标准科目不能为空。");
			}

			if (subjectName == null || "".equals(subjectName)) {
				throw new Exception("客户科目不能为空。");
			}
			int departID = Integer.parseInt(dpID);

			conn = new DBConnect().getConnect(dpID);
			st = conn.createStatement();

			sql = " delete from  z_keyResult \n"
					+ " where level0=1 and standkey = '" + standName
					+ "' and userkey = '" + subjectName + "' and customerid= "
					+ departID;
			st.execute(sql);

			sql = " delete from  z_keyResult \n"
				+ " where 1=1 "
				+ " and (userkey = '" + subjectName + "' or userkey like '" + subjectName + "/%') "
				+ " and customerid= "+ departID;
			st.execute(sql);
		
			String standID = "";
			String property = "";
			String direction = "";
			sql = " select subjectid,a.property,case a.property when 2 then -1 else a.property end as direction " +
					"from k_standsubject a ,k_customer b  where subjectfullname = '"
					+ subjectName + "' and a.vocationid=b.vocationid and b.departid="+dpID ;
			rs = st.executeQuery(sql);
			if (rs.next()) {
				standID = rs.getString(1);
				property = rs.getString(2);
				direction = rs.getString(3);
				
				sql = "insert into z_keyResult(level0,standid,standkey,userkey,customerid,property) "
					+ " values(1,'"
					+ standID
					+ "','"
					+ subjectName
					+ "','"
					+ subjectName + "'," + departID + ",'" + property + "')";
				st.execute(sql);
				
			} 
			
			int level1 = 0;
			sql = "select distinct level1 from c_account where subjectfullname1 = '"+subjectName+"' ";
			rs = st.executeQuery(sql);
			if(rs.next()){
				level1 = rs.getInt(1);
			}
			DbUtil.close(rs);
			
			if(level1 > 1){		//二级解除
				sql = "update c_account a,( \n"
					+ " select distinct AccPackageID ,subjectID,subjectfullname2,subjectfullname1,direction2 \n"
					+ " from c_account where '" + subjectName + "' like concat(subjectfullname1,'/%') \n"
					+ " and level1 = "+level1+"-1 \n"
					+ " ) b  \n"
//					+ " set a.subjectfullname2 = concat(b.subjectfullname2,'/',a.accname),a.direction2=b.direction2 \n"
					+ " set a.subjectfullname2 = concat(b.subjectfullname2,substring(a.subjectfullname1,CHAR_LENGTH(b.subjectfullname1)+1)),a.direction2=b.direction2 \n"
					+ " where 1=1  \n"
					+ " and a.AccPackageID = b.AccPackageID \n"
					+ " and a.subjectfullname1 like concat(b.subjectfullname1,'/%') \n"
					+ " and (a.subjectfullname1 = '" + subjectName + "' or a.subjectfullname1 like '" + subjectName + "/%') ";
				st.execute(sql);
				
				sql = "update c_accountall a,( \n"
					+ " select distinct AccPackageID ,subjectID,subjectfullname2,subjectfullname1,direction2 \n"
					+ " from c_account where '" + subjectName + "' like concat(subjectfullname1,'/%') \n"
					+ " and level1 = "+level1+"-1 \n"
					+ " ) b  \n"
//					+ " set a.subjectfullname2 = concat(b.subjectfullname2,'/',a.accname),a.direction2=b.direction2 \n"
					+ " set a.subjectfullname2 = concat(b.subjectfullname2,substring(a.subjectfullname1,CHAR_LENGTH(b.subjectfullname1)+1)),a.direction2=b.direction2 \n"
					+ " where 1=1  \n"
					+ " and a.AccPackageID = b.AccPackageID \n"
					+ " and a.subjectfullname1 like concat(b.subjectfullname1,'/%') \n"
					+ " and (a.subjectfullname1 = '" + subjectName + "' or a.subjectfullname1 like '" + subjectName + "/%') ";
				st.execute(sql);
				
			}else{		//一级解除
//				分库后已经不存在不同单位的数据了。并且科目对照是单位级的
				
				sql = " update c_account set subjectfullname2 = subjectfullname1,direction2=direction \n"
						+ " where subjectfullname1 = '"
						+ subjectName
						+ "' or subjectfullname1 like '" + subjectName + "/%'";
				st.execute(sql);

				sql = " update c_accountall set subjectfullname2 = subjectfullname1,direction2=direction \n"
						+ " where subjectfullname1 = '"
						+ subjectName
						+ "' or subjectfullname1 like '" + subjectName + "/%'";
				st.execute(sql);

			}
			
//			更新辅助核算标准科目方向 winner add on 20070905
			sql = " update c_assitementryacc a join (select accpackageid,subjectid,direction2 from c_account b "
				+"where b.submonth=1 and (b.subjectfullname1 = '" + subjectName + "' or b.subjectfullname1 like '"+ subjectName + "/%')) b "
					+"on a.accpackageid=b.accpackageid and a.accid=b.subjectid set a.direction2=a.direction";
			st.execute(sql);

			sql = " update c_assitementryaccall a join (select accpackageid,subjectid,direction2 from c_account b "
				+"where b.submonth=1 and (b.subjectfullname1 = '" + subjectName + "' or b.subjectfullname1 like '"+ subjectName + "/%')) b "
				+"on a.accpackageid=b.accpackageid and a.accid=b.subjectid set a.direction2=a.direction";
			st.execute(sql);
			
			sql = " update c_assitementryacc set direction2 = direction where direction2 = 0 ";
			st.execute(sql);
			
			sql = " update c_assitementryaccall set direction2 = direction where direction2 = 0 ";
			st.execute(sql);
			
			/**
			 * 修改［科目辅助核算披露设置］中的标准科目
			 */
			sql = "update c_subjectassitem a,c_account b  set a.subjectfullname2 = b.subjectfullname2 where b.submonth = 1 and a.accpackageid = b.accpackageid and a.subjectid = b.subjectid";
			st.execute(sql);
			
//			st.executeBatch();

//			new ManuacCountService(conn).updateCustomerID(dpID);

			updateTaskState(conn,dpID);
			
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 2;
		} finally {
			if (st != null)
				st.close();
			if (conn != null)
				conn.close();
		}

	}


	/**
	 * 删除对照记录的下级对照。
	 * @param key1
	 * @param key2
	 * @param dpID  单位编号
	 * @return
	 *   0   正常删除
	 1   删除k_key成功。但k_resultResult还有值　(暂时没有提供)
	 *   2   出现异常情况
	 * @throws Exception
	 */

	public int delKeySon(String key1, String key2, String dpID)
			throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "";

		if (dpID == null || "".equals(dpID)) {
			dpID = "0";
		}

		try {

			int departID = Integer.parseInt(dpID);

			conn = new DBConnect().getConnect(dpID);
			int i = 1;

			if(!"0".equals(dpID)){
				sql = "delete from c_key where (key1 = ? or key1 like concat(?,'/%')) and (key2 = ? or key2 like concat(?,'/%')) and departid = ?";
			}else{
				sql = "delete from asdb.k_key where (key1 = ? or key1 like concat(?,'/%')) and (key2 = ? or key2 like concat(?,'/%')) and departid = ?";
			}

			ps = conn.prepareStatement(sql);
			ps.setString(i++, key1);
			ps.setString(i++, key1);
			ps.setString(i++, key2);
			ps.setString(i++, key2);
			ps.setInt(i++, departID);
			ps.addBatch();

			i = 1;
			ps.setString(i++, key2);
			ps.setString(i++, key2);
			ps.setString(i++, key1);
			ps.setString(i++, key1);
			ps.setInt(i++, departID);
			ps.addBatch();

			ps.executeBatch();
			ps.execute("Flush tables");
			ps.clearBatch();

			ps.close();
			
			//多删除1张表z_keyresult,兼容小彭的改科目名称造成的问题
			sql="delete from z_keyresult where standkey=? and userkey = ? ";
			System.out.println("qwh:sql="+sql);
			ps = conn.prepareStatement(sql);
			ps.setString(1, key1);
			ps.setString(2, key2);
			ps.execute();    
			
			
			return 0;

		} catch (Exception e) {
			e.printStackTrace();
			return 2;
		} finally {
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}

	}

	/**
	 * 修改科目的全路径。用于底稿刷数。
	 * 现在只支持　将　客户科目　调到　标准科目　的下级。
	 * @param standName
	 * @param subjectName
	 * @param dpID
	 * @return
	 *   0  正常增加
	 *   1  客户没有标准科目的一级科目，不能对照
	 *   2  出现异常情况
	 * @throws Exception
	 */
	public int modifyFullPath(String standName, String subjectName, String dpID)
			throws Exception {
		Connection conn = null;
		Statement st = null;
		Statement querySt = null;
		ResultSet rs = null;
		String sql = "";

		try {

			if (dpID == null || "".equals(dpID)) {
				throw new Exception("单位编号不能为空。");
			}

			if (standName == null || "".equals(standName)) {
				throw new Exception("标准科目不能为空。");
			}

			if (subjectName == null || "".equals(subjectName)) {
				throw new Exception("客户科目不能为空。");
			}
			int departID = Integer.parseInt(dpID);

			conn = new DBConnect().getConnect(dpID);
			st = conn.createStatement();
			querySt = conn.createStatement();

			//======判断客户是否存在该一级科目
			sql = "select 1 from c_account where subjectfullname2 ='"
					+ standName + "'";
			rs = querySt.executeQuery(sql);
			if (!rs.next()) {
				return 1;
			}

			String standID = "";
			String property = "";
			String direction = "";
			sql = " select subjectid,a.property,case a.property when 2 then -1 else a.property end as direction " +
					" from k_standsubject a left join k_customer b on a.VocationID=b.VocationID  where subjectfullname = '"
					+ standName + "' and b.DepartID ='"+dpID+"' ";
			rs = querySt.executeQuery(sql);

			if (rs.next()) {
				standID = rs.getString(1);
				property = rs.getString(2);
				direction = rs.getString(3);
			} else {
				//找不到标准科目编号处理代码
			}

			sql = "delete from z_keyresult where userkey = '"+subjectName+"' and customerid="+departID;
			st.execute(sql);
			
			
			sql = "insert into z_keyResult(level0,standid,standkey,userkey,customerid,property) "
					+ " values(2,'"
					+ standID
					+ "','"
					+ standName
					+ "','"
					+ subjectName + "'," + departID + ",'" + property + "')";
			st.addBatch(sql);

			//分库后已经不存在不同单位的数据了。
			sql = " update c_account set subjectfullname2 = concat('"
					+ standName + "/',subjectfullname1),direction2="
					+ direction + " \n" + " where subjectfullname1 = '"
					+ subjectName + "' or subjectfullname1 like '"
					+ subjectName + "/%'";
			st.addBatch(sql);

			sql = " update c_accountall set subjectfullname2 = concat('"
					+ standName + "/',subjectfullname1),direction2="
					+ direction + " \n" + " where subjectfullname1 = '"
					+ subjectName + "' or subjectfullname1 like '"
					+ subjectName + "/%'";
			st.addBatch(sql);

			//更新辅助核算标准科目方向 winner add on 20070905
			sql = " update c_assitementryacc a join (select accpackageid,subjectid,direction2 from c_account b "
				+"where b.submonth=1 and (b.subjectfullname1 = '" + subjectName + "' or b.subjectfullname1 like '"+ subjectName + "/%')) b "
					+"on a.accpackageid=b.accpackageid and a.accid=b.subjectid set a.direction2=b.direction2";
			st.addBatch(sql);

			sql = " update c_assitementryaccall a join (select accpackageid,subjectid,direction2 from c_account b "
				+"where b.submonth=1 and (b.subjectfullname1 = '" + subjectName + "' or b.subjectfullname1 like '"+ subjectName + "/%')) b "
				+"on a.accpackageid=b.accpackageid and a.accid=b.subjectid set a.direction2=b.direction2";
			st.addBatch(sql);

			st.executeBatch();
			st.clearBatch();

			sql = " update c_assitementryacc set direction2 = direction where direction2 = 0 ";
			st.execute(sql);
			
			sql = " update c_assitementryaccall set direction2 = direction where direction2 = 0 ";
			st.execute(sql);
			
			/**
			 * 修改［科目辅助核算披露设置］中的标准科目
			 */
			sql = "update c_subjectassitem a,c_account b  set a.subjectfullname2 = b.subjectfullname2 where b.submonth = 1 and a.accpackageid = b.accpackageid and a.subjectid = b.subjectid";
			st.execute(sql);
			
//			new ManuacCountService(conn).updateCustomerID(dpID);

			updateTaskState(conn,dpID);
			
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 2;
		} finally {

			if (rs != null)
				rs.close();
			if (st != null)
				st.close();
			if (querySt != null)
				querySt.close();
			if (conn != null)
				conn.close();
		}

	}

	/**
	 * 还原科目的全路径。用于底稿刷数。
	 *
	 * @param standName
	 * @param subjectName
	 * @param dpID
	 * @return
	 *   0  正常增加
	 *   1　还原科目的全路径成功。但k_resultResult还有值　
	 *   2  出现异常情况
	 * @throws Exception
	 */
	public int restoreFullPath(String standName, String subjectName, String dpID)
			throws Exception {
		Connection conn = null;
		Statement st = null;
		String sql = "";
		ResultSet rs = null;
		try {

			if (dpID == null || "".equals(dpID)) {
				throw new Exception("单位编号不能为空。");
			}

			if (standName == null || "".equals(standName)) {
				throw new Exception("标准科目不能为空。");
			}

			if (subjectName == null || "".equals(subjectName)) {
				throw new Exception("客户科目不能为空。");
			}
			int departID = Integer.parseInt(dpID);

			conn = new DBConnect().getConnect(dpID);
			st = conn.createStatement();

			sql = " delete from  z_keyResult \n"
					+ " where level0=2 and standkey = '" + standName
					+ "' and userkey = '" + subjectName + "' and customerid= "
					+ departID;
			st.execute(sql);

			String standID = "";
			String property = "";
			String direction = "";
			sql = " select subjectid,a.property,case a.property when 2 then -1 else a.property end as direction " +
					"from k_standsubject a ,k_customer b  where subjectfullname = '"
					+ subjectName + "' and a.vocationid=b.vocationid and b.departid="+dpID ;
			rs = st.executeQuery(sql);
			if (rs.next()) {
				standID = rs.getString(1);
				property = rs.getString(2);
				direction = rs.getString(3);
				
				sql = "insert into z_keyResult(level0,standid,standkey,userkey,customerid,property) "
					+ " values(1,'"
					+ standID
					+ "','"
					+ subjectName
					+ "','"
					+ subjectName + "'," + departID + ",'" + property + "')";
				st.execute(sql);
				
			} 
			
			int level1 = 0;
			sql = "select distinct level1 from c_account where subjectfullname1 = '"+subjectName+"' ";
			rs = st.executeQuery(sql);
			if(rs.next()){
				level1 = rs.getInt(1);
			}
			DbUtil.close(rs);
			
			if(level1 > 1){		//二级解除
				sql = "update c_account a,( \n"
					+ " select distinct AccPackageID ,subjectID,subjectfullname2,subjectfullname1,direction2 \n"
					+ " from c_account where '" + subjectName + "' like concat(subjectfullname1,'/%') \n"
					+ " and level1 = "+level1+"-1 \n"
					+ " ) b  \n"
					+ " set a.subjectfullname2 = concat(b.subjectfullname2,'/',a.accname),a.direction2=b.direction2 \n"
					+ " where 1=1  \n"
					+ " and a.AccPackageID = b.AccPackageID \n"
					+ " and a.subjectfullname1 like concat(b.subjectfullname1,'/%') \n"
					+ " and (a.subjectfullname1 = '" + subjectName + "' or a.subjectfullname1 like '" + subjectName + "/%') ";
				st.execute(sql);
				
				sql = "update c_accountall a,( \n"
					+ " select distinct AccPackageID ,subjectID,subjectfullname2,subjectfullname1,direction2 \n"
					+ " from c_account where '" + subjectName + "' like concat(subjectfullname1,'/%') \n"
					+ " and level1 = "+level1+"-1 \n"
					+ " ) b  \n"
					+ " set a.subjectfullname2 = concat(b.subjectfullname2,'/',a.accname),a.direction2=b.direction2 \n"
					+ " where 1=1  \n"
					+ " and a.AccPackageID = b.AccPackageID \n"
					+ " and a.subjectfullname1 like concat(b.subjectfullname1,'/%') \n"
					+ " and (a.subjectfullname1 = '" + subjectName + "' or a.subjectfullname1 like '" + subjectName + "/%') ";
				st.execute(sql);
				
				
			}else{	
//				分库后已经不存在不同单位的数据了。并且科目对照是单位级的
				sql = " update c_account set subjectfullname2 = subjectfullname1,direction2=direction \n"
						+ " where subjectfullname1 = '"
						+ subjectName
						+ "' or subjectfullname1 like '" + subjectName + "/%'";
				st.execute(sql);

				sql = " update c_accountall set subjectfullname2 = subjectfullname1,direction2=direction \n"
						+ " where subjectfullname1 = '"
						+ subjectName
						+ "' or subjectfullname1 like '" + subjectName + "/%'";
				st.execute(sql);
			}
			

//			更新辅助核算标准科目方向 winner add on 20070905
			sql = " update c_assitementryacc a join  (select accpackageid,subjectid,direction2 from c_account b "
				+"where b.submonth=1 and (b.subjectfullname1 = '" + subjectName + "' or b.subjectfullname1 like '"+ subjectName + "/%')) b "
					+"on a.accpackageid=b.accpackageid and a.accid=b.subjectid set a.direction2=a.direction";
			st.execute(sql);

			sql = " update c_assitementryaccall a join (select accpackageid,subjectid,direction2 from c_account b "
				+"where b.submonth=1 and (b.subjectfullname1 = '" + subjectName + "' or b.subjectfullname1 like '"+ subjectName + "/%')) b "
				+"on a.accpackageid=b.accpackageid and a.accid=b.subjectid set a.direction2=a.direction";
			st.execute(sql);

			sql = " update c_assitementryacc set direction2 = direction where direction2 = 0 ";
			st.execute(sql);
			
			sql = " update c_assitementryaccall set direction2 = direction where direction2 = 0 ";
			st.execute(sql);
			
			/**
			 * 修改［科目辅助核算披露设置］中的标准科目
			 */
			sql = "update c_subjectassitem a,c_account b set a.subjectfullname2 = b.subjectfullname2 where b.submonth = 1 and a.accpackageid = b.accpackageid and a.subjectid = b.subjectid";
			st.execute(sql);
			
//			st.executeBatch();

//			new ManuacCountService(conn).updateCustomerID(dpID);

			updateTaskState(conn,dpID);
			
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 2;
		} finally {
			if (st != null)
				st.close();
			if (conn != null)
				conn.close();
		}

	}


	/**
	 * 根据z_keyResult生成指定帐套制定科目编号的余额表的subjectfullname2
	 * @param accpackageid 帐套编号
	 * @param subjectid 科目编号
	 *
	 */
	public int createFullPath(Connection conn, String accpackageid,String subjectid) throws Exception {

		PreparedStatement ps = null;
		Statement querySt = null;
		ResultSet rs = null;
		Statement st = null;
		String sql = "";

		try {
			querySt = conn.createStatement();

			//先无条件的初始化subjectfullname2
			if (subjectid==null || subjectid.equals("")){
				//不提供科目编号参数，就无条件刷新本帐套的
				sql = "update c_account set subjectfullname2=subjectfullname1,direction2=direction where accpackageid="
					+accpackageid;
				querySt.execute(sql);

				//把客户一级科目名称，更新为标准科目名称
				sql="update c_account a join z_keyresult b on b.standkey not like '%/%' "
					+"and (a.subjectfullname1=b.userkey  or a.subjectfullname1 like concat(b.userkey,'/','%')) "
					+"set subjectfullname2=( "
					+"case when b.level0=1 then concat(b.standkey,substring(a.subjectfullname1,locate('/',a.subjectfullname1))) "
					+"when b.level0=2 then concat(b.standkey,'/',a.subjectfullname1) "
					+"else a.subjectfullname1 end),"
					+"direction2=(case b.property when 2 then -1 else b.property end)"
					+"where a.accpackageid="+accpackageid;
				querySt.execute(sql);

			}else{
				sql = "update c_account set subjectfullname2=subjectfullname1,direction2=direction where accpackageid="
					+accpackageid+" and subjectid='"+ subjectid + "'";
				querySt.execute(sql);

				//把客户一级科目名称，更新为标准科目名称
				sql="update c_account a join z_keyresult b on b.standkey not like '%/%' "
					+"and (a.subjectfullname1=b.userkey  or a.subjectfullname1 like concat(b.userkey,'/','%')) "
					+"set subjectfullname2=( "
					+"case when b.level0=1 then concat(b.standkey,substring(a.subjectfullname1,locate('/',a.subjectfullname1))) "
					+"when b.level0=2 then concat(b.standkey,'/',a.subjectfullname1) "
					+"else a.subjectfullname1 end),"
					+"direction2=(case b.property when 2 then -1 else b.property end)"
					+"where a.accpackageid="+accpackageid+" and a.subjectid='" + subjectid +"'";
				querySt.execute(sql);
			}
			st.executeBatch();

//			new ManuacCountService(conn).insertAccPackageID(accpackageid);
			
			updateTaskState(conn,accpackageid.substring(0, 6));
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 2;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(querySt);
			DbUtil.close(st);
		}

	}

	/**
	 * 根据z_keyResult生成subjectfullname2
	 * @param dpID 单位编号
	 *
	 */
	public int createFullPath(Connection conn, String dpID) throws Exception {

		PreparedStatement ps = null;
		Statement querySt = null,stmt=null;
		ResultSet rs = null;
		String sql = "";
		
		Connection conn1=null;
		
		try {

			if (dpID == null || "".equals(dpID)) {
				throw new Exception("单位编号不能为空。");
			}

//			org.util.Debug.prtOut("py1: "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));

			querySt = conn.createStatement();
//			st = conn.createStatement();
			
			sql = " delete a from z_keyresult a join z_keyresult b on  b.level0=2 and a.standkey = b.userkey where a.level0=1 and a.standkey = a.userkey" ;
			
			try{
				querySt.execute(sql);
			}catch(Exception e){
				//暂时忽略，不清楚原因；
				e.printStackTrace();
				System.out.println("1:"+e.getMessage());
			}
			
			sql = " update z_keyresult a, ( " +
			" 	select distinct b.standkey  " +
			" 	from c_account a ,(select distinct userkey,standkey from z_keyresult where level0=1) b  " +
			" 	where 1=1 " +
			" 	and submonth=1 " +
			" 	and a.subjectfullname1=b.userkey  " +
			" 	group by accpackageid,standkey having count(standkey) >1 " +
			" ) b  " +
			" set level0 = 2 " +
			" where a.standkey =b.standkey " +
			" and a.standkey <> a.userkey " ;
			querySt.execute(sql);
			
			org.util.Debug.prtOut("py1: "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));
			sql = "select distinct accpackageid from c_account ";
			rs = querySt.executeQuery(sql);
			while(rs.next()){
				String str = rs.getString(1);
				
				int iMonth = 1;
				
					sql = "update c_account set subjectfullname2='' ,direction2=0 where accpackageid="+ str + " and submonth =" + iMonth;
					ps = conn.prepareStatement(sql);
					ps.execute();
					DbUtil.close(ps);
					
					org.util.Debug.prtOut("iMonth ="+iMonth+" py 1: "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));

					sql = "update c_account a join z_keyresult b on a.accpackageid='"+ str+"' and a.subjectfullname1=b.userkey  " +
						" set subjectfullname2=( " +
						"	case when b.level0=1 then concat(b.standkey,substring(a.subjectfullname1,CHAR_LENGTH(b.userkey)+1))   " +
						"	when b.level0=2 then concat(b.standkey,'/',a.subjectfullname1) else a.subjectfullname1 end" +
						"	),direction2=(case b.property when 2 then -1 else b.property end) where a.accpackageid   =" + str + " and a.submonth =" + iMonth;
					ps = conn.prepareStatement(sql);
					ps.execute();
					DbUtil.close(ps);
					org.util.Debug.prtOut("iMonth ="+iMonth+" py 2: "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));

					sql = "update c_account a  join (" +
						" 		select distinct SubjectFullName1,subjectfullname2,direction2 from c_account " +
						"		where AccPackageID="+str+" and subjectfullname2<>'' and level1 <>1 and submonth =" + iMonth +
						" ) b on a.SubjectFullName1 like concat(b.SubjectFullName1,'/%') and a.SubjectFullName2=''" +
						" set a.subjectfullname2=concat(b.SubjectFullName2,substring(a.subjectfullname1,CHAR_LENGTH(b.subjectfullname1)+1))," +
						" a.direction2=b.direction2" +
						" where a.accpackageid   ="+str+" and a.SubjectFullName2='' and a.submonth =" + iMonth;
					ps = conn.prepareStatement(sql);
					ps.execute();
					DbUtil.close(ps);
					
					org.util.Debug.prtOut("iMonth ="+iMonth+" py 211: "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));
					
					sql = "update c_account a  join (" +
						" 		select distinct SubjectFullName1,subjectfullname2,direction2 from c_account " +
						"		where AccPackageID="+str+" and subjectfullname2<>'' and submonth =" + iMonth +
						" ) b on a.SubjectFullName1 like concat(b.SubjectFullName1,'/%') and a.SubjectFullName2=''" +
						" set a.subjectfullname2=concat(b.SubjectFullName2,substring(a.subjectfullname1,CHAR_LENGTH(b.subjectfullname1)+1))," +
						" a.direction2=b.direction2" +
						" where a.accpackageid   ="+str+" and a.SubjectFullName2='' and a.submonth =" + iMonth;
					ps = conn.prepareStatement(sql);
					ps.execute();
					DbUtil.close(ps);
					org.util.Debug.prtOut("iMonth ="+iMonth+" py 3: "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));

					sql = "update c_account  set subjectfullname2=subjectfullname1 ,direction2=direction where accpackageid   ="+str+" and SubjectFullName2='' and submonth =" + iMonth;
					ps = conn.prepareStatement(sql);
					ps.execute();
					DbUtil.close(ps);
					org.util.Debug.prtOut("iMonth ="+iMonth+" py 4: "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));



					sql = "update c_accountall set subjectfullname2='' ,direction2=0 where accpackageid="+ str + " and submonth =" + iMonth ;
					ps = conn.prepareStatement(sql);
					ps.execute();
					DbUtil.close(ps);
					org.util.Debug.prtOut("iMonth ="+iMonth+" py 11: "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));

					sql = "update c_accountall a join z_keyresult b on a.accpackageid='"+ str+"' and a.subjectfullname1=b.userkey  " +
						" set subjectfullname2=( " +
						"	case when b.level0=1 then concat(b.standkey,substring(a.subjectfullname1,CHAR_LENGTH(b.userkey)+1))   " +
						"	when b.level0=2 then concat(b.standkey,'/',a.subjectfullname1) else a.subjectfullname1 end" +
						"	),direction2=(case b.property when 2 then -1 else b.property end) where a.accpackageid   =" + str + " and a.submonth =" + iMonth;
					ps = conn.prepareStatement(sql);
					ps.execute();
					DbUtil.close(ps);
					org.util.Debug.prtOut("iMonth ="+iMonth+" py 12: "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));

					sql = "update c_accountall a  join (" +
						" 		select distinct SubjectFullName1,subjectfullname2,direction2 from c_accountall " +
						"		where AccPackageID="+str+" and subjectfullname2<>'' and submonth =" + iMonth +
						" ) b on a.SubjectFullName1 like concat(b.SubjectFullName1,'/%') and a.SubjectFullName2=''" +
						" set a.subjectfullname2=concat(b.SubjectFullName2,substring(a.subjectfullname1,CHAR_LENGTH(b.subjectfullname1)+1))," +
						" a.direction2=b.direction2" +
						" where a.accpackageid   ="+str+" and a.SubjectFullName2='' and a.submonth =" + iMonth;
					ps = conn.prepareStatement(sql);
					ps.execute();
					DbUtil.close(ps);
					org.util.Debug.prtOut("iMonth ="+iMonth+" py 13: "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));

					sql = "update c_accountall  set subjectfullname2=subjectfullname1 ,direction2=direction where accpackageid   ="+str+" and SubjectFullName2='' ";
					ps = conn.prepareStatement(sql);
					ps.execute();
					DbUtil.close(ps);
					org.util.Debug.prtOut("iMonth ="+iMonth+" py 14: "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));

//					更新辅助核算标准科目方向 winner add on 20070905
					sql="update c_assitementryacc a join c_account b "
						+"on b.submonth=1 and a.accpackageid=b.accpackageid and a.accid=b.subjectid set a.direction2=b.direction2 " +
						" where  a.accpackageid='"+ str+"' and b.accpackageid='"+ str+"' and a.submonth =" + iMonth;
					ps = conn.prepareStatement(sql);
					ps.execute();
					DbUtil.close(ps);
					org.util.Debug.prtOut("iMonth ="+iMonth+" py 5: "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));

					sql="update c_assitementryaccall a join c_account b "
						+"on b.submonth=1 and a.accpackageid=b.accpackageid and a.accid=b.subjectid set a.direction2=b.direction2" +
						" where  a.accpackageid='"+ str+"' and b.accpackageid='"+ str+"' and a.submonth =" + iMonth;
					ps = conn.prepareStatement(sql);
					ps.execute();
					DbUtil.close(ps);
					org.util.Debug.prtOut("iMonth ="+iMonth+" py 6: "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));

				

				for(iMonth=2 ; iMonth<=12 ;iMonth ++){
					sql = "update c_account a,(" +
					" 		select  subjectid,subjectfullname2,direction2 from c_account " +
					"		where AccPackageID="+str+" and submonth =1" +
					"	) b set a.subjectfullname2=b.subjectfullname2 ,a.direction2=b.direction2 " +
					"	where a.accpackageid="+ str + " and a.submonth =" + iMonth + 
					"	and a.subjectid = b.subjectid ";
					ps = conn.prepareStatement(sql);
					ps.execute();
					DbUtil.close(ps);
					
					sql = "update c_accountall a,(" +
					" 		select  subjectid,dataname,subjectfullname2,direction2 from c_account " +
					"		where AccPackageID="+str+" and submonth =1" +
					"	) b set a.subjectfullname2=b.subjectfullname2 ,a.direction2=b.direction2 " +
					"	where a.accpackageid="+ str + " and a.submonth =" + iMonth + 
					"	and a.subjectid = b.subjectid ";
					ps = conn.prepareStatement(sql);
					ps.execute();
					DbUtil.close(ps);
					
					sql = "update c_assitementryacc a,(" +
					" 		select  subjectid,direction2 from c_account " +
					"		where AccPackageID="+str+" and submonth =1" +
					"	) b set a.direction2=b.direction2 " +
					"	where a.accpackageid="+ str + " and a.submonth =" + iMonth + 
					"	and a.accid = b.subjectid  ";
					ps = conn.prepareStatement(sql);
					ps.execute();
					DbUtil.close(ps);
					
					sql = "update c_assitementryaccall a,(" +
					" 		select  subjectid,direction2 from c_account " +
					"		where AccPackageID="+str+" and submonth =1" +
					"	) b set a.direction2=b.direction2 " +
					"	where a.accpackageid="+ str + " and a.submonth =" + iMonth + 
					"	and a.accid = b.subjectid  ";
					ps = conn.prepareStatement(sql);
					ps.execute();
					DbUtil.close(ps);
				}
				
			}
			
			sql = " update c_assitementryacc set direction2 = direction where direction2 = 0 ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql = " update c_assitementryaccall set direction2 = direction where direction2 = 0 ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			/**
			 * 修改［科目辅助核算披露设置］中的标准科目
			 */
			sql = "update c_subjectassitem a,c_account b  set a.subjectfullname2 = b.subjectfullname2 where b.submonth = 1 and a.accpackageid = b.accpackageid and a.subjectid = b.subjectid";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
//			st.executeBatch();
			org.util.Debug.prtOut("py2: "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));
//			new ManuacCountService(conn).insertCustomerID(dpID);
			
			updateTaskState(conn,dpID);
			
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 2;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(querySt);
		}

	}

	/**
	 * 更新所有数据库的z_keyResult和subjectfullname2
	 * @return
	 */
	public int updateAllKeyResult(String keyNumber) {
		Connection conn = null;
		Statement st = null,st1=null;
		ResultSet rs = null,rs1=null;
		String sql = "";

		try {
			DBConnect db = new DBConnect();
			conn = db.getDirectConnect("");
			st = conn.createStatement();
			st1 = conn.createStatement();
			//===========查出这对k_key的值及单位编号
			sql = "select key1,key2,departid from k_key where autoid="
					+ keyNumber;
			rs = st.executeQuery(sql);
			String key1 = "";
			String key2 = "";
			String keydepartid = "";
			if (rs.next()) {
				key1 = rs.getString(1);
				key2 = rs.getString(2);
				keydepartid = rs.getString(3);
			} else {
				return -1;
			}

			sql = "select departid from k_customer where departid!=\"555555\"";
			rs = st.executeQuery(sql);

			String dpID = "";

			while (rs.next()) {
				//切换数据库
				dpID = rs.getString(1);
				db.changeDataBase(dpID, conn);

				if (this.updateSingleKeyResult(conn, dpID, key1,key2,keydepartid)){
					org.util.Debug.prtErr("完成[" + dpID + "] \n");;
				}else{
					org.util.Debug.prtErr("对照[" + dpID + "]失败 \n");;
				}

				sql="select accpackageid from c_accpackage where customerid="+dpID;
				rs1 = st1.executeQuery(sql);
				while (rs1.next()){
					new UserSubjectService(conn).updateOriParent(rs1.getString(1));
				}
				rs1.close();
			}

			return 0;

		} catch (Exception e) {
			e.printStackTrace();
			return -2;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(rs1);
			DbUtil.close(st);
			DbUtil.close(st1);
			DbUtil.close(conn);
		}

	}

	/**
	 * 更新所有数据库的z_keyResult和subjectfullname2
	 * @return
	 */
	public String getFullPath(String departID, String standsubject,
			String subject) {
		Connection conn = null;
		Statement querySt = null;
		ResultSet rs = null;
		String sql = "select fullPath from z_keyresult \n" + "where standkey='"
				+ standsubject + "' and userkey ='" + subject + "'";

		try {
			conn = new DBConnect().getConnect(departID);
			querySt = conn.createStatement();

			rs = querySt.executeQuery(sql);

			if (rs.next()) {
				return rs.getString(1);
			} else {
				return "";
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (querySt != null)
					querySt.close();

				if (conn != null)
					conn.close();

			} catch (Exception e) {
				e.printStackTrace();
				org.util.Debug.prtErr("什么?关闭连接也出错? -_-#");
			}
		}

	}

	/**
	 * 临时使用的方法
	 * 把除了［缺省］行业之外的行业 标准科目更新 为缺省 的标准科目体系
	 * @return
	 */
	public void setDefaultStandSubjectToOther() {

		Connection conn = null;

		Statement st = null;

		String sql = "";

		try {
			conn = new DBConnect().getConnect("");
			st = conn.createStatement();
			sql = "delete from k_standsubject where vocationid not in(0,52)";
			st.execute(sql);
			sql = ""
					+ " insert into k_standsubject(vocationid,subjectid,parentsubjectid,subjectname,subjectfullname,assistcode,isleaf,`level0`,property) \n"
					+ " select b.industryid,subjectid,parentsubjectid,subjectname,subjectfullname,assistcode,isleaf,`level0`,property \n"
					+ " from k_standsubject a,k_industry b where b.industryID not in(0,52) and a.vocationid=0\n";
			st.execute(sql);
			st.execute("Flush tables");

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			DbUtil.close(st);
			DbUtil.close(conn);
		}

	}

	//用于更新一条k_key对k_result和subjectfullname2的影响
	public boolean updateSingleKeyResult(Connection conn,
			String departID, String key1,String key2,String keydepartid) {

		java.sql.Statement st = null;
		java.sql.ResultSet rs = null;

		try {
			String sql = "";
			st = conn.createStatement();

			//===========查出当前单位的行业id
			String vocationID = "0";
			sql = "select vocationid from k_customer where departid="
					+ departID;
			rs = st.executeQuery(sql);
			if (rs.next()) {
				vocationID = rs.getString(1);
			}

			//===========构造出临时k_key表
			String keyTable = " (" + " select '" + key1 + "' as key1,'" + key2
					+ "' as key2," + keydepartid + " as departid \n"
					+ " union \n" + " select '" + key2 + "' as key1,'" + key1
					+ "' as key2," + keydepartid + " as departid \n" + " ) ";

			sql = "select 1 from z_keyresult where fullpath like '%`" + key1
					+ "^" + key2 + "`%' " + " or fullpath like '%`" + key2
					+ "^" + key1 + "`%' ";
			rs = st.executeQuery(sql);
			if (rs.next()) {
				//===========已经对照了。就直接跳过了。
				return true;
			}

			//============从第二级开始对照。最多maxi级。如果取消maxi限制，则可以计算出所有的的可能性，但遇到 [股本] 之类的就好慢了。
			int maxi = 1003;

			//============进行第一次放大
			//			  替换次数（层次）

			//            fullpath 存放的是已经对照过的关键字，格式是:  key1^key2
			int i = 1001;
			//先插入一级的对照
			sql = "  "
					+ " insert into z_keyresult(customerid,standid,standkey,userkey,fullpath,`changelevel`,property) \n"
					+ " select distinct "
					+ departID
					+ ",b.subjectid, b.subjectfullname, \n"
					+ " trim(replace(concat(b.subjectfullname,'                                             '),a.key1,a.key2)) as userkey,concat('`',a.key1,'^',a.key2,'`') as fullpath,"
					+ String.valueOf(i)
					+ " as changelevel,b.property \n"
					+ " from "
					+ keyTable
					+ " a,( select distinct subjectid ,subjectfullname,property from k_standsubject where vocationid='"
					+ vocationID + "') b \n"
					+ " where b.subjectfullname like concat('%',key1,'%') \n"
					+ " and a.departid in (0," + departID + ")";

			//进行二次以上放大
			while (st.executeUpdate(sql) > 0 && i < maxi) {
				sql = "  "
						+ " insert into z_keyresult(customerid,standid,standkey,userkey,fullpath,`changelevel`,property) \n"
						+

						" select distinct "
						+ departID
						+ " as customerid,b.standid, b.standkey, \n"
						+ " trim(replace(concat(b.userkey,'                                             '),a.key1,a.key2)) as userkey, \n"
						+ " max(concat(b.fullpath,a.key1,'^',a.key2,'`')) as fullpath, "
						+ String.valueOf(i + 1)
						+ " as `changelevel`,b.property\n"
						+ " from k_key a,z_keyresult b \n"
						+ " where b.standkey like concat('%',key1,'%') \n"
						+ "   and b.userkey like concat('%',key1,'%') \n"
						+ "   and b.fullpath not like concat('%`',a.key1,'^',a.key2,'`%') \n"
						+ "   and b.changelevel = " + String.valueOf(i++)
						+ " \n" + "   and a.departid in (0," + departID + ")"
						+ "  group by \n" + "  customerid,standkey,userkey\n"
						+ "  \n";
			}

			//根据z_keyresult生成subjectfullname2
			this.createFullPath(conn, departID);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (st != null)
					st.close();
			} catch (Exception e) {
				System.out.print("什么？关闭连接也报错？  -_-#");
				e.printStackTrace();
			}
		}

	}


	public void updateTaskToSubjectName(Connection conn,String projectID) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "";
			sql = "select * from z_project a,k_customer b where projectid='"+projectID+"' and b.DepartID=a.customerid";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			String dpID ="";
			String VocationID = "";
			if(rs.next()){
				dpID = rs.getString("customerid");
				VocationID = rs.getString("VocationID");
			}

			sql = "update z_task a left join (" +
					" 	select a.subjectname,replace(CONCAT(a.subjectname,'                                     '),b.key1,b.key2) exSubjectName" +
					" 	from (    " +
					" 		select  distinct subjectname from k_standsubject where VocationID="+VocationID+" and level0=1" +
					" 	) a,k_key b" +
					" 	where  b.departid in ('0','"+dpID+"') " +
					"	and a.subjectname like concat('%',b.key1,'%') " +

					" 	union" +

					"	select distinct a.subjectname,TRIM(replace(replace(CONCAT(a.subjectname,'                                     '),b.key1,b.key2),c.key1,c.key2))  exSubjectName" +
					" 	from (    " +
					" 		select  distinct subjectname from k_standsubject where VocationID="+VocationID+" and level0=1" +
					"	) a,k_key b,k_key c" +
					"	where  b.departid in ('0','"+dpID+"') " +
					" 	and  c.departid in ('0','"+dpID+"') " +
					"	and a.subjectname like concat('%',b.key1,'%')  " +
					"	and a.subjectname like concat('%',c.key1,'%') " +

					"	union " +

					"	select distinct a.subjectname,TRIM(replace(replace(replace(CONCAT(a.subjectname,'                                     '),b.key1,b.key2),c.key1,c.key2),d.key1,d.key2))  exSubjectName" +
					" 	from (    " +
					" 		select  distinct subjectname from k_standsubject where VocationID="+VocationID+" and level0=1" +
					"	) a,k_key b,k_key c,k_key d  " +
					"	where  b.departid in ('0','"+dpID+"') " +
					" 	and  c.departid in ('0','"+dpID+"') " +
					" 	and  d.departid in ('0','"+dpID+"') " +
					"	and a.subjectname like concat('%',b.key1,'%')  " +
					"	and a.subjectname like concat('%',c.key1,'%')  " +
					"	and a.subjectname like concat('%',d.key1,'%') " +
					" ) b on exsubjectname=a.subjectname " +
					" set a.subjectname=b.subjectname" +
					" where isleaf=1 and projectid="+projectID+" and a.subjectname<>'' and b.subjectname is not null" ;

			ps = conn.prepareStatement(sql);
			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 根据客户编号更新底稿有数据状态
	 * @param conn
	 * @param customerId
	 */
	public void updateTaskState(Connection conn, String customerId) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		TaskCommonService taskCommonService = null;

		try {
			String sql = "select projectId from z_project where CustomerId=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, customerId);

			rs = ps.executeQuery();

			while(rs.next()) {
				taskCommonService = new TaskCommonService(conn,rs.getString(1));
				taskCommonService.updateTaskHasData();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	
	public String getType(String standName, String subjectName, String dpID){
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = new DBConnect().getConnect(dpID);
			st = conn.createStatement();
			String sql = "select distinct a.* from z_keyresult a,c_account b where standkey='"+standName+"' and b.AccName = userkey order by level0  limit 1";
			rs = st.executeQuery(sql);
			if(rs.next()){
				String str = rs.getString("userkey");
				String level0 = rs.getString("level0");
				if("1".equals(level0)){
					sql = "select distinct  * from c_account where AccName='"+str+"' and level1=1 limit 1";
					rs = st.executeQuery(sql);
					if(rs.next()){
						sql = "select distinct  * from c_account where AccName='"+subjectName+"' limit 1";
						rs = st.executeQuery(sql);
						if(rs.next()){
							return "2";
						}else{
							return "1";	
						}
					}else{
						return "1";
					}
				}else{
					return "1";
				}
			}else{
				return "1";	
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
			DbUtil.close(conn);
		}
	}
	
	//根据
	public String getSubjectBySubjectfullname1(Connection conn,String accpackageid,String strSubjectfullname1){
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String strSql="select subjectid from c_account where accpackageid=? and submonth=1 and subjectfullname1=?";
			ps = conn.prepareStatement(strSql);
			ps.setString(1, accpackageid);
			ps.setString(2, strSubjectfullname1);
			
			rs = ps.executeQuery();

			while(rs.next()) {
				return rs.getString(1);
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return "";
	}

	
	
//	＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
//	新的完整性对照，支持二级
//	＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	
	/**
	 * 新的完整性对照，支持二级
	 * @param conn
	 * @param dpID
	 * @return
	 * @throws Exception
	 */
	public int createFullPath1(Connection conn, String dpID) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Statement st = null;
		String sql = "";
		try {
			
			
			
			
			
		} catch (Exception e) {
			System.out.println("出错的SQL="+sql);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
			DbUtil.close(ps);
		}
		
		
		
		return 0;
	}
	
	
	/**
	 * 加入c_key表
	 */
	public void auto(String dpID) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			conn = new DBConnect().getConnect(dpID);
			try {
				sql = "select 1 from c_key where 1=1 limit 1";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					//表示已经初始化过
					return;
				}
				DbUtil.close(rs);
			} catch (Exception e) {
				//表还不存在，要重新建表
				sql = "create table c_key like asdb.k_key";
				ps = conn.prepareStatement(sql);
				ps.execute();
			} 
			DbUtil.close(ps);			
			
			//初始化
			sql = "insert into c_key (key1,key2,departid) " +
			" select key1,key2,departid " +
			" from asdb.k_key " +
			" where asdb.k_key.departid = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, dpID);
			ps.execute();
			
			sql = " delete from asdb.k_key  where asdb.k_key.departid = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, dpID);
			ps.execute();
			
			try {
				sql = "create or replace view k_key as " +
				" select * from asdb.k_key " +
				" where asdb.k_key.departid = 0 " +
				" union " +
				" select * from c_key";
				ps = conn.prepareStatement(sql);
				ps.execute();	
			} catch (Exception e) {

			}
			
			
		} catch (Exception e) {
			System.out.println("出错的SQL="+sql);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}
	}
	
	
	
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			new KeyValue().updateTaskState(conn,"100001");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

	}
	
}
