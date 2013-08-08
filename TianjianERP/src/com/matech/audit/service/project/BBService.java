package com.matech.audit.service.project;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.matech.audit.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;

public class BBService {

	public String send(String urlAddr, Map paramMap) throws Exception {
	
		HttpURLConnection conn = null;
		
		String responseContent = "";

		StringBuffer params = new StringBuffer();

		Iterator it = paramMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry element = (Entry) it.next(); // 将要发送的信息拼成name=value方式
			params.append(element.getKey());
			params.append("=");
			params.append(element.getValue());
			params.append("&");
		}

		if (params.length() > 0) {
			params.deleteCharAt(params.length() - 1);
		}

		OutputStreamWriter out = null;
		try {
			conn = (HttpURLConnection) new URL(urlAddr).openConnection();
			
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(params.length()));
			conn.setDoInput(true);
			conn.connect();

			out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			out.write(params.toString());
			out.flush();
			out.close();

			InputStream in = conn.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(in,"UTF-8"));
			String tempLine = rd.readLine();

			int code = conn.getResponseCode();

			if (code != 200) {
				return null;
			} else {
				responseContent = tempLine;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			conn.disconnect();
		}
		System.out.println("############ responseContent="+responseContent+"##");
		return responseContent;
	}
	
	
	/**
	 * 得到项目信息
	 * @param projectId
	 * @return
	 */
	public Map getProjectInfo(String projectId){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		Map map = null;
		try{
			map = new HashMap();
			String sql = " select departname,projectname from z_project p left join k_customer c on p.customerid = c.departid "
			   	   		+ " where projectid = ? ";
			conn = new DBConnect().getConnect("");
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectId);
			rs = ps.executeQuery();
			if(rs.next()){
				map.put("wtdwmc", rs.getString("departname"));
				map.put("bsdwmc", rs.getString("departname"));
				map.put("xmmc", rs.getString("projectname"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}
		
		return map ;
	}
}
