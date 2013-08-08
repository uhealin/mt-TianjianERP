package com.matech.audit.service.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.employment.model.PersonalInfo;
import com.matech.audit.service.news.model.News;
import com.matech.audit.service.user.model.ResumMassage;
import com.matech.framework.pub.db.DbUtil;

public class ResumMassageService {
	private Connection conn = null;

	public ResumMassageService(Connection conn) {
		this.conn = conn;
	}
	public void addNews(ResumMassage rm){
		PreparedStatement ps = null;
		String sql = null;
		
		try {
			sql = " insert into k_resummessage (title,content,type,updateTime)"
				+ " values(?,?,?,?)";
			int i = 1;
			ps = conn.prepareStatement(sql);
			ps.setString(i++, rm.getTitle());
			ps.setString(i++, rm.getContent());
			ps.setString(i++, rm.getType());
			ps.setString(i++, rm.getUpdateTime());
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	public void del(String autoid) {
		PreparedStatement ps = null;
		try {
			String sql = " DELETE FROM `k_resummessage` where autoid=" + autoid;
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	public ResumMassage get(String autoid) {
		PreparedStatement ps=null;
		ResultSet rs=null;
		ResumMassage resum=null;
		try {
			String sql="select * from k_resummessage where autoid="+autoid;
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){	
				resum=new ResumMassage();
				resum.setAutoid(rs.getString("autoid"));
				resum.setTitle(rs.getString("title"));
				resum.setType(rs.getString("type"));
				resum.setContent(rs.getString("content"));
	
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return resum;
	}
	public void update(ResumMassage resumMassage ,int id) {
		PreparedStatement ps = null;
		String sql = null;
		
		try {
			sql = " update k_resummessage set title = ?,content = ?,type = ?,updateTime = ? "
				+ " where autoId = " + id;
			int i = 1;
			ps = conn.prepareStatement(sql);
			ps.setString(i++, resumMassage.getTitle());
			ps.setString(i++, resumMassage.getContent());
			ps.setString(i++, resumMassage.getUpdateTime());
			ps.setString(i++, resumMassage.getType());
			
			
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
}
