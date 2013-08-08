package com.matech.audit.service.leaveCompany;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.matech.audit.service.leaveCompany.model.LeaveCompany;
import com.matech.framework.pub.db.DbUtil;

public class LeaveCompanyService {

	private Connection conn = null ;
	
	public LeaveCompanyService(Connection conn) {
		this.conn = conn ;
	}

	public void addLeaveCompany(LeaveCompany lc) throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			String sql="insert into k_leaveofficeTJ(uuid, userId, sex, departmentid, birthday, inworktime, incompanytime," +
					" education, degree, major, graduation, qualification, rank, techQualification, " +
					"mobilePhone, applyDate, predictLeaveDate, reason,  status, property) " +
					"values(?,?,?,?,?  ,?,?,?,?,? ,?,?,?,?,? ,?,?,?,?,?)";
			ps = conn.prepareStatement(sql);
			int i=1;
			ps.setString(i++, lc.getUUID());
			ps.setString(i++,lc.getUserId());
			ps.setString(i++,lc.getSex());
			ps.setString(i++,lc.getDepartmentid());
			ps.setString(i++,lc.getBirthday());
			ps.setString(i++,lc.getIncompanytime());
			ps.setString(i++,lc.getIncompanytime());
			ps.setString(i++,lc.getEducation());
			ps.setString(i++,lc.getDegree());
			ps.setString(i++,lc.getMajor());
			ps.setString(i++,lc.getGraduation());
			ps.setString(i++,lc.getQualification());
			ps.setString(i++,lc.getRank());
			ps.setString(i++,lc.getTechQualification());
			ps.setString(i++,lc.getMobilePhone());
			ps.setString(i++,lc.getApplyDate());
			ps.setString(i++,lc.getPredictLeaveDate());
			ps.setString(i++,lc.getReason());
			ps.setString(i++,"未发起");
			ps.setString(i++,lc.getProperty());
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}
	
	
	public LeaveCompany getLeaveCompany(String uuid) throws Exception {
		LeaveCompany lc=new LeaveCompany();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			StringBuffer sql = new StringBuffer();
			sql.append("select uuid, userId, sex, departmentid, birthday, inworktime, incompanytime," +
					" education, degree, major, graduation, qualification, rank, techQualification, " +
					"mobilePhone, applyDate, predictLeaveDate, reason,  status, property from k_leaveofficeTJ where uuid =?");
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1,uuid);
			rs = ps.executeQuery() ;
			int i=1;
			while(rs.next()){
				lc.setUUID(rs.getString("uuid"));
				lc.setUserId(rs.getString("userId"));
				lc.setSex(rs.getString("sex"));
				lc.setDepartmentid(rs.getString("departmentid"));
				lc.setBirthday(rs.getString("birthday"));
				lc.setInworktime(rs.getString("inworktime"));
				lc.setIncompanytime(rs.getString("incompanytime"));
				lc.setEducation(rs.getString("education"));
				lc.setDegree(rs.getString("degree"));
				lc.setMajor(rs.getString("major"));
				lc.setGraduation(rs.getString("graduation"));
				lc.setQualification(rs.getString("qualification"));
				lc.setRank(rs.getString("rank"));
				lc.setTechQualification(rs.getString("techQualification"));
				lc.setMobilePhone(rs.getString("mobilePhone"));
				lc.setApplyDate(rs.getString("applyDate"));
				lc.setPredictLeaveDate(rs.getString("predictLeaveDate"));
				lc.setReason(rs.getString("reason"));
				lc.setStatus(rs.getString("status"));
				lc.setProperty(rs.getString("property"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return lc ;
	}

	
	public void updateLeaveCompany(LeaveCompany lc) {
		PreparedStatement ps = null;
		try {

			StringBuffer sql = new StringBuffer();
			sql.append("update k_leaveofficeTJ set userId = ? , sex = ? , departmentid = ? , birthday = ? , inworktime = ? , incompanytime = ? ," +
					" education = ? , degree = ? , major = ? , graduation = ? , qualification = ? , rank = ? , techQualification = ? , " +
					"mobilePhone = ? , applyDate = ? , predictLeaveDate = ? , reason = ? ,  status = ? , property = ?   where uuid=?");
			
			ps = conn.prepareStatement(sql.toString());
			int i=1;
			ps.setString(i++,lc.getUserId());
			ps.setString(i++,lc.getSex());
			ps.setString(i++,lc.getDepartmentid());
			ps.setString(i++,lc.getBirthday());
			ps.setString(i++,lc.getIncompanytime());
			ps.setString(i++,lc.getIncompanytime());
			ps.setString(i++,lc.getEducation());
			ps.setString(i++,lc.getDegree());
			ps.setString(i++,lc.getMajor());
			ps.setString(i++,lc.getGraduation());
			ps.setString(i++,lc.getQualification());
			ps.setString(i++,lc.getRank());
			ps.setString(i++,lc.getTechQualification());
			ps.setString(i++,lc.getMobilePhone());
			ps.setString(i++,lc.getApplyDate());
			ps.setString(i++,lc.getPredictLeaveDate());
			ps.setString(i++,lc.getReason());
			ps.setString(i++,lc.getStatus());
			ps.setString(i++,lc.getProperty());
			ps.setString(i++, lc.getUUID());
			ps.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	
	public void deleteLeaveCompany(String uuid) {
		PreparedStatement ps = null;
		try {

			StringBuffer sql = new StringBuffer();
			sql.append("delete from k_leaveofficeTJ where uuid=?") ;
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1,uuid) ;
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
