package com.matech.audit.service.leaveCompany;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.matech.audit.service.leaveCompany.model.LeaveCompany;
import com.matech.audit.service.leaveCompany.model.LeaveCompanyFlow;
import com.matech.framework.pub.db.DbUtil;

public class LeaveCompanyFlowService {
	

	private Connection conn = null ;
	
	public LeaveCompanyFlowService(Connection conn) {
		this.conn = conn ;
	}


	public void addLeaveCompanyFlow(LeaveCompanyFlow lc) throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			String sql="insert into j_leavecompanyprocss(processInstanceId,uuid,applyuser,applyDate,state,ctype,property) " +
					"values(?,?,?,?,?,?)";
			ps = conn.prepareStatement(sql);
			int i=1;
			ps.setString(i++, lc.getProcessInstanceId());
			ps.setString(i++,lc.getUuid());
			ps.setString(i++,lc.getApplyuser());
			ps.setString(i++,lc.getApplyDate());
			ps.setString(i++,lc.getState());
			ps.setString(i++,lc.getCtype());
			ps.setString(i++,lc.getProperty());
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}
	
	
	public LeaveCompanyFlow getLeaveCompanyFlow(String uuid) throws Exception {
		LeaveCompanyFlow lc=new LeaveCompanyFlow();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			StringBuffer sql = new StringBuffer();
			sql.append("select processInstanceId,uuid,applyuser,applyDate,state,ctype,property from j_leavecompanyprocss where uuid =?");
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1,uuid);
			rs = ps.executeQuery() ;
			int i=0;
			while(rs.next()){
				lc.setProcessInstanceId(rs.getString(i++));
				lc.setUuid(rs.getString(i++));
				lc.setApplyuser(rs.getString(i++));
				lc.setState(rs.getString(i++));
				lc.setCtype(rs.getString(i++));
				lc.setProperty(rs.getString(i++));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return lc ;
	}

	
	public void updateLeaveCompanyFlow(LeaveCompanyFlow lc) {
		PreparedStatement ps = null;
		try {

			StringBuffer sql = new StringBuffer();
			sql.append("update k_leaveofficeTJ set processInstanceId = ? ,applyuser = ? ,applyDate = ? ,state = ? ,ctype = ? ,property = ?   where uuid=?");
			
			ps = conn.prepareStatement(sql.toString());
			int i=1;
			ps.setString(i++, lc.getProcessInstanceId());
			ps.setString(i++,lc.getApplyuser());
			ps.setString(i++,lc.getApplyDate());
			ps.setString(i++,lc.getState());
			ps.setString(i++,lc.getCtype());
			ps.setString(i++,lc.getProperty());
			ps.setString(i++,lc.getUuid());
			ps.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	
	public void deleteLeaveCompanyFlow(String uuid) {
		PreparedStatement ps = null;
		try {

			StringBuffer sql = new StringBuffer();
			sql.append("delete from j_leavecompanyprocss where uuid=?") ;
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, uuid) ;
			ps.execute() ;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	

	public boolean UpdateValueBySql(String sql){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean result = false;
		try {
		
			ps = conn.prepareStatement(sql);
			
			ps.executeUpdate();
			
			result = true;
		} catch (SQLException e) {
		
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return result;
	}
	
	public String getValueBySql(String sql){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String value = "";
		
		
		try {
		
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				value += rs.getString(1);
			}
			
		} catch (SQLException e) {
		
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return value;
	}


}
