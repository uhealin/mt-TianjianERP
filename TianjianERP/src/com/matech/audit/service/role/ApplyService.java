package com.matech.audit.service.role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class ApplyService {
	
	private Connection conn=null;
	
	public ApplyService(Connection conn) {
		this.conn=conn;
	}
	
	public String getPopedomNg(String ppm) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "";
			if(!"".equals(ppm)){
				ppm = new ASFuntion().replaceStr(ppm, ".", "','");
				ppm = ppm.substring(2,ppm.length()-2);
			}else{
				ppm = "''";
			}
			
			String result = "";
			sql = "select id, name,menu_id from s_sysmenu where  id in (" + ppm + ") where parentid!='000' order by menu_id";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);	
		}
		return "";
	}
	
	
	public void saveApply(String applyuserid,String applyPopedom,String applydate,String applymemo) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "insert into k_rightapply(applyuserid,applyPopedom,applydate,applymemo) values('"+applyuserid+"','"+applyPopedom+"','"+applydate+"','"+applymemo+"')";
		
		
			ps = conn.prepareStatement(sql);
			ps.executeUpdate();
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);	
		}
		
	}
	
	
	public String getApplyPopedom(String id) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select applyPopedom from k_rightapply where autoid="+id;
	
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			String applyPopedom="";
			if(rs.next()){
				
				applyPopedom = rs.getString(1);
			}
			
			return applyPopedom;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);	
		}	
	}
	
	public String getUserIdByRightApplyId(String id) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select applyuserid from k_rightapply where autoid="+id;
	
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			String applyuserid="";
			if(rs.next()){
				
				applyuserid = rs.getString(1);
			}
			
			return applyuserid;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);	
		}
		
	}
}
