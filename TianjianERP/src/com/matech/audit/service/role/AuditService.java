package com.matech.audit.service.role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class AuditService {
	
	private Connection conn=null;
	
	public AuditService(Connection conn) {
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
	
	
	public void saveAudit(String audituserid,String auditPopedomok,String auditPopedomng,String auditdate,String auditmemo,String id,String userId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "update  k_rightapply set audituserid='"+audituserid+"',auditPopedomok='"+auditPopedomok+"',auditPopedomng='"+auditPopedomng+"',auditdate='"+auditdate+"',auditmemo='"+auditmemo+"' where autoid="+id;
		
		
			ps = conn.prepareStatement(sql);
			ps.executeUpdate();
			
			updateUserPopedom(auditPopedomok,userId);
			
			sendMessage(audituserid,userId);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);	
		}
		
	}
	
	public void sendMessage(String audituserid ,String userId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ASFuntion CHF=new ASFuntion();
		try {
			
			PlacardTable placardTable=new PlacardTable(); 
			placardTable.setAddresser(audituserid);
			placardTable.setAddresserTime(CHF.getCurrentDate()+" "+CHF.getCurrentTime());
			placardTable.setCaption("权限审批结果");
			placardTable.setMatter("<a href=\"../role.do?method=applyAlist\">你申请的个人权限已经审批，请点击查看</a>！");
			placardTable.setAddressee(userId);
			placardTable.setIsRead(0);
			placardTable.setIsReversion(0);
			placardTable.setIsNotReversion(0);
			
			PlacardService placardService=new PlacardService(conn); 
			
			placardService.AddPlacard(placardTable);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);	
		}
		
	}
	
	public void updateUserPopedom(String auditPopedomok,String userId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			String userSql = "select ifnull(Popedom,'')  from  k_user  where id="+userId;
			
			ps = conn.prepareStatement(userSql);
			rs = ps.executeQuery();
			
			String Popedom="";
			if(rs.next()){
				
				Popedom = rs.getString(1);
				
			}
			rs.close();
			ps.close();
			
			if(Popedom.length()>1){
				
				Popedom = Popedom.substring(0,Popedom.length()-1);
			}
			
			String sql = "update  k_user set Popedom='"+Popedom+auditPopedomok+"' where id="+userId;
		
		
			ps = conn.prepareStatement(sql);
			ps.executeUpdate();
			ps.execute("Flush tables");
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);	
		}
	}
}
