package com.matech.audit.service.leaveType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.matech.audit.service.leaveType.model.LeaveType;
import com.matech.framework.pub.db.DbUtil;

/**
 * @author Administrator
 *请假类型设置
 */
public class LeaveTypeService {
	Connection conn = null;
	
	public LeaveTypeService(Connection conn){
		this.conn = conn;
	}
	
	/**
	 * 新增
	 * @param leaveType
	 * @return
	 */
	public boolean add(LeaveType  leaveType){
		
		boolean result = false;
		
		PreparedStatement ps = null;
		
		String sql = "INSERT INTO `k_leavetypeSetUp` \n"
					+"(`name`,`applyLimit`,`yearDayLimit`,`yearCountLimit`, \n"
					+"`monthDayLimit`,`monthCountLimit`,`deductMoney`,`minTime`,`memo`) \n"
					+"VALUES (?,?,?,?,?,?,?,?,?);";
		try {
			
			int i = 1;
			
			ps = conn.prepareStatement(sql);
			
			ps.setString(i++, leaveType.getName());
			ps.setString(i++, leaveType.getApplyLimit());
			ps.setString(i++, leaveType.getYearDayLimit());
			ps.setString(i++, leaveType.getYearCountLimit());
			ps.setString(i++, leaveType.getMonthDayLimit());
			ps.setString(i++, leaveType.getMonthCountLimit());
			ps.setString(i++, leaveType.getDeductMoney());
			ps.setString(i++, leaveType.getMinTime());
			ps.setString(i++, leaveType.getMemo());
			
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
	public boolean update(LeaveType leaveType){
		
		boolean result = false;
		
		PreparedStatement ps = null;
		
		String sql = "UPDATE `k_leavetypeSetUp` \n"
					+"SET \n"
					+"  `name` = ?, \n"
					+"  `applyLimit` = ?, \n"
					+"  `yearDayLimit` = ?, \n"
					+"  `yearCountLimit` = ?, \n"
					+"  `monthDayLimit` = ?, \n"
					+"  `monthCountLimit` = ?, \n"
					+"  `deductMoney` = ?, \n"
					+"  `minTime` = ?, \n"
					+"  `memo` =? \n"
					+"WHERE `autoId` =?;";
		try {
			
			int i = 1;
			System.out.println(sql);
			ps = conn.prepareStatement(sql);
			
			ps.setString(i++, leaveType.getName());
			ps.setString(i++, leaveType.getApplyLimit());
			ps.setString(i++, leaveType.getYearDayLimit());
			ps.setString(i++, leaveType.getYearCountLimit());
			ps.setString(i++, leaveType.getMonthDayLimit());
			ps.setString(i++, leaveType.getMonthCountLimit());
			ps.setString(i++, leaveType.getDeductMoney());
			ps.setString(i++, leaveType.getMinTime());
			ps.setString(i++, leaveType.getMemo());
			ps.setString(i++, leaveType.getAutoId());
			
			ps.execute();
			
			result=true;
			
		} catch (SQLException e) {
			System.out.println("修公印章出错："+e.getMessage());
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
	public boolean delete(String autoId){
		
		boolean result = false;
		
		PreparedStatement ps = null;
		
		String sql = "delete from k_leavetypeSetUp where autoid=?";
		try {
			
			int i = 1;
			
			ps = conn.prepareStatement(sql);
			
			ps.setString(i++, autoId);
			
			ps.execute();
			
			result=true;
			
		} catch (SQLException e) {
			System.out.println("删除印章出错："+e.getMessage());
		}finally{
			DbUtil.close(ps);
		}
		return result;
	}
	
	/**
	 *得到请假类型信息 
	 * @param uuid
	 * @return
	 */
	public LeaveType getLeaveType(String autoId){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		LeaveType leaveType = new LeaveType();
		
		String sql = "SELECT`autoId`,`name`,`applyLimit`," +
							"`yearDayLimit`,`yearCountLimit`," +
							"`monthDayLimit`,`monthCountLimit`," +
							"`deductMoney`,`minTime`,`memo` "+
					  "FROM `k_leavetypeSetUp` where autoId=?";
		
		try {
		
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, autoId);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				
				leaveType.setAutoId(autoId);
				leaveType.setName(rs.getString("name"));
				leaveType.setApplyLimit(rs.getString("applyLimit"));
				leaveType.setYearDayLimit(rs.getString("yearDayLimit"));
				leaveType.setYearCountLimit(rs.getString("yearCountLimit"));
				leaveType.setMonthDayLimit(rs.getString("monthDayLimit"));
				leaveType.setMonthCountLimit(rs.getString("monthCountLimit"));
				leaveType.setDeductMoney(rs.getString("deductMoney"));
				leaveType.setMinTime(rs.getString("minTime"));
				leaveType.setMemo(rs.getString("memo"));
				
			}
			
		} catch (SQLException e) {
			System.out.println("获取印章信息错误："+e.getMessage());
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return leaveType;
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
}
