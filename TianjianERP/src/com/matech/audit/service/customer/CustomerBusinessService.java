package com.matech.audit.service.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;


import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class CustomerBusinessService {
	private Connection conn = null;
	
	private String departmentid = "";
	
	public String getDepartmentid() {
		return departmentid;
	}

	public void setDepartmentid(String departmentid) {
		this.departmentid = departmentid;
	}

	public CustomerBusinessService( Connection conn ) {
		this.conn = conn; 
	}
	
	public Map get(String autoid) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			Map map = new HashMap();
			sql = " select a.*,b.name as iname,c.name as aname,d.name as tname,ifnull(e.name,a.owner) as ownername   " +
			"	from oa_business a " +
			"	left join k_user b on a.iuser = b.id " +
			"	left join k_user c on a.auser = c.id " +
			"	left join k_user d on a.tracking = d.id " +
			"	left join k_user e on a.owner = e.id " +//商机责任人
			"	where autoid = ?  ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, autoid);
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
	
	public Map getCustomer(String customerid) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			ASFuntion CHF=new ASFuntion();
			
			String customername = "",linkman = "",phone = "",source = "";
			Map map = new HashMap();
			sql = "select * from k_customer where DepartID = ? or DepartName = ? order by if(departmentid = '"+this.departmentid+"',0,1) ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, customerid);
			ps.setString(2, customerid);
			rs = ps.executeQuery();
			if(rs.next()){
				customerid = CHF.showNull(rs.getString("DepartID")); 
				customername = CHF.showNull(rs.getString("DepartName")); 
				linkman = CHF.showNull(rs.getString("linkman"));
				phone = CHF.showNull(rs.getString("phone"));
				source = CHF.showNull(rs.getString("approach"));
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			map.put("customerid", customerid);
			map.put("customername", customername);
			map.put("contact", linkman);
			map.put("contactway", phone);
			map.put("source", source);
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
			ASFuntion CHF=new ASFuntion();
			String autoid =  CHF.showNull((String)parameters.get("autoid"));
			
			sql = "select * from oa_business where 1=2 ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			ResultSetMetaData RSMD = rs.getMetaData();
			
			if("".equals(autoid)){	//新增
				sql = "";
				String sql1 = "", sql2 = "";
				for (int i = 1; i <= RSMD.getColumnCount(); i++) {
					if(!"autoid".equals(RSMD.getColumnLabel(i).toLowerCase())){	//自动编号
						String string = (String)parameters.get(RSMD.getColumnLabel(i).toLowerCase());
						if(string == null) continue;
						sql1 += ","+RSMD.getColumnLabel(i).toLowerCase()+" ";
		 				sql2 += ",? ";
					}
				}
				
				sql = "insert into  oa_business (" + sql1.substring(1) + ") values (" + sql2.substring(1) + ") ";
				ps = conn.prepareStatement(sql);
				int ii = 1;
				for (int i = 1; i <= RSMD.getColumnCount(); i++) {
					if(!"autoid".equals(RSMD.getColumnLabel(i).toLowerCase())){	
						String string = (String)parameters.get(RSMD.getColumnLabel(i).toLowerCase());
						if(string == null) continue;
						ps.setString(ii ++, string);
					}
				}
				ps.execute();
			}else{//修改
				String sql1 = "";
				for (int i = 1; i <= RSMD.getColumnCount(); i++) {
					if(!"autoid".equals(RSMD.getColumnLabel(i).toLowerCase())){	//自动编号
						String string = (String)parameters.get(RSMD.getColumnLabel(i).toLowerCase());
						if(string == null) continue;
						sql1 += ","+RSMD.getColumnLabel(i).toLowerCase()+" = ? ";
					}
				}
				
				sql = "update oa_business set " + sql1.substring(1) + " where autoid = ? ";
				ps = conn.prepareStatement(sql);
				int ii = 1;
				for (int i = 1; i <= RSMD.getColumnCount(); i++) {
					if(!"autoid".equals(RSMD.getColumnLabel(i).toLowerCase())){	
						String string = (String)parameters.get(RSMD.getColumnLabel(i).toLowerCase());
						if(string == null) continue;
						ps.setString(ii ++, string);
					}
				}
				ps.setString(ii, autoid);
				ps.execute();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public String del(String autoid) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			
			sql = "select 1 from oa_business where autoid = ? and state = '待审核' ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, autoid);
			rs = ps.executeQuery();
			//只有state = '待审核' 才能删除
			if(rs.next()){
				DbUtil.close(rs);
				DbUtil.close(ps);
				sql = "delete from oa_business where autoid = ? ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, autoid);
				ps.execute();
				return "删除成功！";
			}else{
				return "商机只有在【待审核】中才能删除，删除失败！";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 临时表
	 * @param autoid
	 * @return
	 * @throws Exception
	 */
	public List<Map> getT() throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		List<Map> listMap = new ArrayList();
		try {
			
			sql = " select *  from t_oa_business  ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			ResultSetMetaData RSMD = rs.getMetaData();
			while(rs.next()){
				Map map = new HashMap();
				for (int i = 1; i <= RSMD.getColumnCount(); i++) {
					map.put(RSMD.getColumnLabel(i).toLowerCase(), rs.getString(RSMD.getColumnLabel(i)));
				}
				
				/*map.put("customerid", rs.getString("customerid"));
				map.put("customername", rs.getString("customername"));
				map.put("caption", rs.getString("caption"));
				map.put("memo", rs.getString("memo"));
				map.put("source", rs.getString("source"));
				map.put("contact", rs.getString("contact"));
				map.put("contactway", rs.getString("contactway"));
				map.put("deadtime", rs.getString("deadtime"));
				map.put("owner", rs.getString("owner"));
				map.put("iuser", rs.getString("iuser"));
				map.put("adate", rs.getString("adate"));
				map.put("Auditopinion", rs.getString("Auditopinion"));
				map.put("Tracking", rs.getString("Tracking"));
				map.put("Tdate", rs.getString("Tdate"));
				map.put("result", rs.getString("result"));
				map.put("state", rs.getString("state"));*/
				
				listMap.add(map);
			}
			 
			return listMap;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
}
