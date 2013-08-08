package com.matech.audit.service.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.matech.framework.pub.db.DbUtil;


public class CustomermanagerService {

	private Connection conn = null;
	
	public CustomermanagerService( Connection conn ) {
		this.conn = conn; 
	}
	
	public Map get(String CustomerID) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			Map map = new HashMap();
			sql = " select * from k_customermanager where CustomerID = ?  ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, CustomerID);
			rs = ps.executeQuery();
			ResultSetMetaData RSMD = rs.getMetaData();
			if(rs.next()){
				for (int i = 1; i <= RSMD.getColumnCount(); i++) {
					map.put(RSMD.getColumnLabel(i).toLowerCase(), rs.getString(RSMD.getColumnLabel(i)));
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
	
	public void save(Map parameters) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			sql = "select * from k_customermanager where 1=2 ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			ResultSetMetaData RSMD = rs.getMetaData();
			sql = "";
			String sql1 = "", sql2 = "";
			for (int i = 1; i <= RSMD.getColumnCount(); i++) {
				if(!"id".equals(RSMD.getColumnLabel(i).toLowerCase())){	//自动编号
					sql1 += ","+RSMD.getColumnLabel(i).toLowerCase()+" ";
	 				sql2 += ",? ";
				}
			}
			try {
				//记录日志
				sql = "INSERT INTO `asdb`.`k_customermanager_log`" +
						" (`customerid`,`user1`,`user2`,`Property`,`createUser`,`createDate`)" +
						" SELECT `customerid`,`user1`,`user2`,`Property`,?,now() FROM k_customermanager" +
						" WHERE customerid=? ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, (String)parameters.get("createUser"));
				ps.setString(2, (String)parameters.get("customerid"));
				ps.execute();
				DbUtil.close(ps);
			} catch (Exception e) {
				
			}
			
			
			sql = "delete from k_customermanager where CustomerID = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, (String)parameters.get("customerid"));
			ps.execute();
			DbUtil.close(ps);
			
			sql = "insert into  k_customermanager (" + sql1.substring(1) + ") values (" + sql2.substring(1) + ") ";
			ps = conn.prepareStatement(sql);
			int ii = 1;
			for (int i = 1; i <= RSMD.getColumnCount(); i++) {
				String string = (String)parameters.get(RSMD.getColumnLabel(i).toLowerCase());
				string = (string == null || "".equals(string)) ? "" : string;
				ps.setString(ii, string);
				ii++ ;
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
	
	public List<Map> getManager_log(String CustomerID) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			List<Map> mapList = new ArrayList<Map>();
			sql = " select a.*,b.name as createUserName,c.name as user1Name,d.name as user2Name from k_customermanager_log a " +
					" LEFT JOIN K_USER b on b.id = a.createUser "+
					" LEFT JOIN K_USER c on c.id = a.user1 "+
					" LEFT JOIN K_USER d on d.id = a.user2 "+
					"where CustomerID = ?  order by a.createdate desc ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, CustomerID);
			rs = ps.executeQuery();
			ResultSetMetaData RSMD = rs.getMetaData();
			while(rs.next()){
				Map map = new HashMap();
				for (int i = 1; i <= RSMD.getColumnCount(); i++) {
					map.put(RSMD.getColumnLabel(i).toLowerCase(), rs.getString(RSMD.getColumnLabel(i)));
				}
				mapList.add(map);
			}
			
			return mapList;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
}
