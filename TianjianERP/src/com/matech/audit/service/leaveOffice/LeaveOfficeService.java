package com.matech.audit.service.leaveOffice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.matech.audit.service.leaveOffice.model.LeaveOffice;
import com.matech.audit.service.leaveOffice.model.LeaveOfficeFlow;
import com.matech.audit.service.leaveOffice.model.Leaveofficesurvey;
import com.matech.framework.pub.db.DbUtil;

public class LeaveOfficeService {
	
	Connection conn = null;
	
	public LeaveOfficeService(Connection conn){
		this.conn = conn;
	}
	
	/**
	 * 新增
	 * @param leaveType
	 * @return
	 */
	public boolean add(LeaveOffice  leaveOffice){
		
		boolean result = false;
		
		PreparedStatement ps = null;
		
		String sql = "INSERT INTO `k_leaveoffice` \n"
					 +"(`uuid`,`userId`,`applyDate`,`predictLeaveDate`,`reason`,`status`,`property`) \n"
					 +" VALUES (?,?,?,?,?,?,?);";
		try {
			
			int i = 1;
			
			ps = conn.prepareStatement(sql);
			
			ps.setString(i++, leaveOffice.getUuid());
			ps.setString(i++, leaveOffice.getUserId());
			ps.setString(i++, leaveOffice.getApplyDate());
			ps.setString(i++, leaveOffice.getPredictLeaveDate());
			ps.setString(i++, leaveOffice.getReason());
			ps.setString(i++, leaveOffice.getStatus());
			ps.setString(i++, leaveOffice.getProperty());
			
			ps.execute();
			
			result=true;
			
		} catch (SQLException e) {
			System.out.println("新增出错："+e.getMessage());
		}finally{
			DbUtil.close(ps);
		}
		return result;
	}

	/**
	 * 修改
	 * @param leaveType
	 * @return
	 */
	public boolean update(LeaveOffice  leaveOffice){
		
		boolean result = false;
		
		PreparedStatement ps = null;
		
		String sql = "UPDATE `k_leaveoffice` \n"
					+"SET "
					+"  `predictLeaveDate` = ?, \n"
					+"  `reason` = ?, \n"
					+"  `property` = ? \n"
					+" WHERE `uuid` = ?;";
		try {
			
			int i = 1;
			System.out.println(sql);
			ps = conn.prepareStatement(sql);
			
			ps.setString(i++, leaveOffice.getPredictLeaveDate());
			ps.setString(i++, leaveOffice.getReason());
			ps.setString(i++, leaveOffice.getProperty());
			ps.setString(i++, leaveOffice.getUuid());
			
			ps.execute();
			
			result=true;
			
		} catch (SQLException e) {
			System.out.println("出错："+e.getMessage());
		}finally{
			DbUtil.close(ps);
		}
		return result;
	}

	/**
	 * 删除
	 * @param uuid
	 * @return
	 */
	public boolean delete(String uuid){
		
		boolean result = false;
		
		PreparedStatement ps = null;
		
		String sql = "delete from k_leaveoffice where uuid=?";
		try {
			
			int i = 1;
			
			ps = conn.prepareStatement(sql);
			
			ps.setString(i++, uuid);
			
			ps.execute();
			
			result=true;
			
		} catch (SQLException e) {
			System.out.println("删除出错："+e.getMessage());
		}finally{
			DbUtil.close(ps);
		}
		return result;
	}
	
	/**
	 *得到请假信息 
	 * @param uuid
	 * @return
	 */
	public LeaveOffice getLeaveOffice(String uuid){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		LeaveOffice leaveOffice = new LeaveOffice();
		
		String sql = "SELECT `uuid`,`userId`,`applyDate`,`predictLeaveDate`,`reason`,`status`,`property` \n"
					 +" FROM `k_leaveoffice` where uuid=?";
		
		try {
		
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, uuid);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				
				leaveOffice.setUuid(uuid);
				leaveOffice.setUserId(rs.getString("userId"));
				leaveOffice.setApplyDate(rs.getString("applyDate"));
				leaveOffice.setPredictLeaveDate(rs.getString("predictLeaveDate"));
				leaveOffice.setReason(rs.getString("reason"));
				leaveOffice.setStatus(rs.getString("status"));
				leaveOffice.setProperty(rs.getString("property"));
				
			}
			
		} catch (SQLException e) {
			System.out.println("错误："+e.getMessage());
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return leaveOffice;
	}
	  
	/**
	 * 根据sql 得到一列值
	 * @param sql
	 * @return
	 */
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
	
	/**
	 * 修改莫列的值
	 * @param sql
	 * @return
	 */
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
	
	/**
	 * 添加流程信息
	 * @param leaveFlow
	 * @return
	 */
	public boolean addProcss(LeaveOfficeFlow leaveOfficeFlow) {
		int i = 1;
		boolean result = false;
		PreparedStatement ps = null;
		String sql = "insert j_leaveOfficeprocss (ProcessInstanceId,uuid,Applyuser,ApplyDate,State,ctype,Property) "
				+ "value (?,?,?,?,?,?,?)";
		try {
			ps = conn.prepareStatement(sql);

			ps.setString(i++, leaveOfficeFlow.getProcessInstanceId());
			ps.setString(i++, leaveOfficeFlow.getUuid());
			ps.setString(i++, leaveOfficeFlow.getApplyuser());
			ps.setString(i++, leaveOfficeFlow.getApplyDate());
			ps.setString(i++, leaveOfficeFlow.getState());
			ps.setString(i++, leaveOfficeFlow.getCtype());
			ps.setString(i++, leaveOfficeFlow.getProperty());

			result = ps.execute();

			result = true;
		} catch (SQLException e) {

			System.out.println("新增流程信息失败service:" + e.getMessage());
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

		return result;
	}
	
	/**
	 * 新增离职调查表记录
	 * @param leaveofficesurvey
	 * @return
	 */
	public boolean addLeaveofficesurvey(Leaveofficesurvey  leaveofficesurvey){
		
		boolean result = false;
		
		PreparedStatement ps = null;
		
		String sql = "INSERT INTO `asdb`.`k_leaveofficesurvey`" +
					 " (`employmentType`,`userRelation`,`superiorRelation`,`workEnvironment`,`leaveOfficeReason`,`suggest`,`createUser`,`createDepartmentId`,`creataDate`,`property`) \n"
					 +" VALUES (?,?,?,?,?,?,?,?,?,?);";
		try {
			
			int i = 1;
			
			ps = conn.prepareStatement(sql);
			
			ps.setString(i++, leaveofficesurvey.getEmploymentType());
			ps.setString(i++, leaveofficesurvey.getUserRelation());
			ps.setString(i++, leaveofficesurvey.getSuperiorRelation());
			ps.setString(i++, leaveofficesurvey.getWorkEnvironment());
			ps.setString(i++, leaveofficesurvey.getLeaveOfficeReason());
			ps.setString(i++, leaveofficesurvey.getSuggest());
			ps.setString(i++, leaveofficesurvey.getCreateUser());
			ps.setString(i++, leaveofficesurvey.getCreateDepartmentId());
			ps.setString(i++, leaveofficesurvey.getCreataDate());
			ps.setString(i++, leaveofficesurvey.getProperty());
			
			ps.execute();
			
			result=true;
			
		} catch (SQLException e) {
			System.out.println("新增出错："+e.getMessage());
		}finally{
			DbUtil.close(ps);
		}
		return result;
	}
}
