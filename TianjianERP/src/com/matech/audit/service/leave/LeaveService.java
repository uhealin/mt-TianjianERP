package com.matech.audit.service.leave;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.matech.audit.service.leave.model.Leave;
import com.matech.audit.service.leave.model.LeaveFlow;
import com.matech.audit.service.leaveType.model.LeaveType;
import com.matech.audit.service.seal.model.SealFlow;
import com.matech.audit.service.user.model.User;
import com.matech.framework.pub.db.DbUtil;

public class LeaveService {

	Connection conn = null;
	public LeaveService(Connection conn){
		this.conn = conn;
	}
	
	/**
	 * 新增
	 * @param leaveType
	 * @return
	 */
	public boolean add(Leave  leave){
		
		boolean result = false;
		
		PreparedStatement ps = null;
		
		String sql = "INSERT INTO `asdb`.`k_leave` \n"
		            +"(`uuid`,`userId`,`applyDate`,`leaveTypeId`,`leaveStartTime`, \n"
		            +" `leaveEndTime`,`leaveHourCount`,`destroyStartTime`,`destroyEndTime`, \n"
		            +"`destroyHourCount`,`RealStartTime`,`RealEndTime`,`realHourCount`,`memo`,`status`,`property`) \n"
		            +"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		try {
			
			int i = 1;
			
			ps = conn.prepareStatement(sql);
			
			ps.setString(i++, leave.getUuid());
			ps.setString(i++, leave.getUserId());
			ps.setString(i++, leave.getApplyDate());
			ps.setString(i++, leave.getLeaveTypeId());
			ps.setString(i++, leave.getLeaveStartTime());
			ps.setString(i++, leave.getLeaveEndTime());
			ps.setString(i++, leave.getLeaveHourCount());
			ps.setString(i++, leave.getLeaveStartTime());
			ps.setString(i++, leave.getLeaveEndTime());
			ps.setString(i++, leave.getLeaveHourCount());
			ps.setString(i++, leave.getLeaveStartTime());
			ps.setString(i++, leave.getLeaveEndTime());
			ps.setString(i++, leave.getLeaveHourCount());
			ps.setString(i++, leave.getMemo());
			ps.setString(i++, leave.getStatus());
			ps.setString(i++, leave.getProperty());
			
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
	public boolean update(Leave leave){
		
		boolean result = false;
		
		PreparedStatement ps = null;
		
		String sql = "UPDATE `k_leave` \n"
					+"SET \n"
					+"  `leaveTypeId` = ?, \n"
					+"  `leaveStartTime` = ?, \n"
					+"  `leaveEndTime` = ?, \n"
					+"  `leaveHourCount` = ?, \n"
					+"  `destroyStartTime` = ?, \n"
					+"  `destroyEndTime` = ?, \n"
					+"  `destroyHourCount` = ?, \n"
					+"  `RealStartTime` = ?, \n" 
					+"  `RealEndTime` = ?,\n"
					+"  `realHourCount` = ?, \n"
					+"  `memo` = ?, \n"
					+"  `property` = ? \n"
					+"WHERE `uuid` = ?;";
		try {
			
			int i = 1;
			System.out.println(sql);
			ps = conn.prepareStatement(sql);
			
			ps.setString(i++, leave.getLeaveTypeId());
			ps.setString(i++, leave.getLeaveStartTime());
			ps.setString(i++, leave.getLeaveEndTime());
			ps.setString(i++, leave.getLeaveHourCount());
			ps.setString(i++, leave.getLeaveStartTime());
			ps.setString(i++, leave.getLeaveEndTime());
			ps.setString(i++, leave.getLeaveHourCount());
			ps.setString(i++, leave.getLeaveStartTime());
			ps.setString(i++, leave.getLeaveEndTime());
			ps.setString(i++, leave.getLeaveHourCount());
			ps.setString(i++, leave.getMemo());
			ps.setString(i++, leave.getProperty());
			ps.setString(i++, leave.getUuid());
			
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
		
		String sql = "delete from k_leave where uuid=?";
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
	public Leave getLeave(String uuid){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		Leave leave = new Leave();
		
		String sql = "SELECT `userId`,`applyDate`,`leaveTypeId`,`leaveStartTime`, \n"
					+"`leaveEndTime`,`leaveHourCount`,`destroyStartTime`, \n"
					+"`destroyEndTime`,`destroyHourCount`,`RealStartTime`, \n"
					+"`RealEndTime`,`realHourCount`,`memo`,`property` \n"
					+"FROM `k_leave` where uuid=?";
		
		try {
		
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, uuid);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				
				leave.setUuid(uuid);
				leave.setUserId(rs.getString("userId"));
				leave.setApplyDate(rs.getString("applyDate"));
				leave.setLeaveTypeId(rs.getString("leaveTypeId"));
				leave.setLeaveStartTime(rs.getString("leaveStartTime"));
				leave.setLeaveEndTime(rs.getString("leaveEndTime"));
				leave.setLeaveHourCount(rs.getString("leaveHourCount"));
				leave.setDestroyStartTime(rs.getString("destroyStartTime"));
				leave.setDestroyEndTime(rs.getString("destroyEndTime"));
				leave.setDestroyHourCount(rs.getString("destroyHourCount"));
				leave.setRealStartTime(rs.getString("RealStartTime"));
				leave.setRealEndTime(rs.getString("RealEndTime"));
				leave.setRealHourCount(rs.getString("realHourCount"));
				leave.setMemo(rs.getString("memo"));
				leave.setProperty(rs.getString("property"));
				
			}
			
		} catch (SQLException e) {
			System.out.println("错误："+e.getMessage());
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return leave;
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
	 * 发起人 销假保存
	 * @param leave
	 * @return
	 */
	public boolean destroySave(Leave leave){
		
		boolean result = false;
		
		PreparedStatement ps = null;
		
		String sql = "UPDATE `k_leave` \n"
					+"SET \n"
					+"   `status` = ?, \n"
					+"  `destroyStartTime` = ?, \n"
					+"  `destroyEndTime` = ?, \n"
					+"  `destroyHourCount` = ? \n"
					+"WHERE `uuid` = ?;";
		try {
			
			int i = 1;
			System.out.println(sql);
			ps = conn.prepareStatement(sql);

			ps.setString(i++, leave.getStatus());
			ps.setString(i++, leave.getDestroyStartTime());
			ps.setString(i++, leave.getDestroyEndTime());
			ps.setString(i++, leave.getDestroyHourCount());
			ps.setString(i++, leave.getUuid());
			
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
	 * 获取人物性别
	 * @param id
	 * @return
	 */
	public String userDetail(String id){
		String sql ="select  case when Sex='M' or Sex='男' then '男' else '女' end Sex from k_user  where id =?";
		PreparedStatement ps  =  null;
		ResultSet rs = null;
		String result ="";
		try {
			 ps = conn.prepareStatement(sql);
			 ps.setString(1, id);
			 rs = ps.executeQuery();
			 if(rs.next()){
				 result = rs.getString("sex");
			 }
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return result;
	}
	
	/**
	 * 销假 审批通过
	 * @param leave
	 * @return
	 */
	public boolean destroy(String uuid){
		
		boolean result = false;
		
		PreparedStatement ps = null;
		
		String sql = "UPDATE `k_leave` \n"
					+"SET \n"
					+"   `status` = '结束', \n"
					+"  `RealStartTime` = destroyStartTime, \n" 
					+"  `RealEndTime` = destroyEndTime,\n"
					+"  `realHourCount` =destroyHourCount \n"
					+"WHERE `uuid` = ?;";
		try {
			
			int i = 1;
			System.out.println(sql);
			ps = conn.prepareStatement(sql);
			
			ps.setString(i++, uuid);
			
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
	 * 添加流程信息
	 * @param leaveFlow
	 * @return
	 */
	public boolean addProcss(LeaveFlow leaveFlow) {
		int i = 1;
		boolean result = false;
		PreparedStatement ps = null;
		String sql = "insert j_leaveprocss (ProcessInstanceId,uuid,Applyuser,ApplyDate,State,ctype,Property) "
				+ "value (?,?,?,?,?,?,?)";
		try {
			ps = conn.prepareStatement(sql);

			ps.setString(i++, leaveFlow.getProcessInstanceId());
			ps.setString(i++, leaveFlow.getUuid());
			ps.setString(i++, leaveFlow.getApplyuser());
			ps.setString(i++, leaveFlow.getApplyDate());
			ps.setString(i++, leaveFlow.getState());
			ps.setString(i++, leaveFlow.getCtype());
			ps.setString(i++, leaveFlow.getProperty());

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
}
