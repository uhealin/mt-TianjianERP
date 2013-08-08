package com.matech.audit.service.hr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.matech.framework.pub.db.DbUtil;

public class TreeService {
	 Connection conn = null;
	 public TreeService(Connection conn){
		 this.conn = conn;
	 }
	public JSONArray get() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		JSONArray jarr = new JSONArray();
		try {
			String sql = "select * from k_job";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
			  JSONObject json=new JSONObject();
			  for(int i=0;i<rs.getMetaData().getColumnCount();i++){
				  String key=rs.getMetaData().getColumnName(i+1);
				  String val=rs.getString(key);
				  json.put(key, val);
			  }
			  json.put("id", json.get("unid"));
			  json.put("text", json.get("jobname"));
			  json.put("leaf", true);
			  jarr.add(json);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return jarr;
	}
}
