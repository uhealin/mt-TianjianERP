package com.matech.audit.service.rankWages;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.matech.audit.service.rankWages.model.RankWages;
import com.matech.framework.pub.db.DbUtil;

public class RankWargesService {

	Connection conn = null;
	
	public RankWargesService(Connection conn){
		
		this.conn = conn ;
		
	}
	
	/**
	 * 新增
	 * @param rank
	 * @return
	 */
	public boolean add(RankWages rankWages){
		
		boolean result = false;
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String sql = "INSERT INTO `asdb`.`k_rankwages` \n"
		            +"(`uuid`,`interiorId`,`rankId`,`wagesName`,`getValue`,`updateTache`,`orderId`,`remark`,`propenty`,`valueType`,groupFlag) \n"
		            +"VALUES (?,?,?,?,?,?,?,?,?,?);";
		try {
			
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, rankWages.getUuid());
			ps.setString(i++, rankWages.getInteriorId());
			ps.setString(i++, rankWages.getRankId());
			ps.setString(i++, rankWages.getWagesName());
			ps.setString(i++, rankWages.getGetValue());
			ps.setString(i++, rankWages.getUpdateTache());
			ps.setString(i++, rankWages.getOrderId());
			ps.setString(i++, rankWages.getRemark());
			ps.setString(i++, rankWages.getPropenty());
			ps.setString(i++, rankWages.getValueType());
			ps.setString(i++, rankWages.getGroupFlag());
			
			ps.execute();
			result = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return result;
	}
	
	/**
	 * 修改
	 * @param rank
	 * @return
	 */
	public  boolean update(RankWages rankWages){
		boolean result = false;
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String sql = "UPDATE `asdb`.`k_rankwages` \n"
					 +"SET  \n"
					 +" `interiorId` = ?, \n"
					 +" `rankId` = ?, \n"
					 +" `wagesName` = ?, \n"
					 +" `getValue` = ?, \n"
					 +" `updateTache` =?, \n"
					 +" `orderId` =?, \n"
					 +" `remark` =?, \n"
					 +" `propenty` = ?, \n"
					 +" `valueType` = ?, \n"
					 +" `groupFlag` = ? \n"
					+"WHERE `uuid` = ?;";
		try {
			
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, rankWages.getInteriorId());
			ps.setString(i++, rankWages.getRankId());
			ps.setString(i++, rankWages.getWagesName());
			ps.setString(i++, rankWages.getGetValue());
			ps.setString(i++, rankWages.getUpdateTache());
			ps.setString(i++, rankWages.getOrderId());
			ps.setString(i++, rankWages.getRemark());
			ps.setString(i++, rankWages.getPropenty());
			ps.setString(i++, rankWages.getValueType());
			ps.setString(i++, rankWages.getGroupFlag());
			
			ps.setString(i++, rankWages.getUuid());
			
			ps.execute();
			result = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return result;
	}
	
	/**
	 * 删除
	 * @param uuid
	 * @return
	 */
	public  boolean del(String uuid){
		boolean result = false;
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String sql = "delete from k_rankwages WHERE `uuid` = ?;";
		try {
			
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, uuid);
			
			ps.execute();
			result = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return result;
	}
	
	/**
	 * 得到工资设定信息
	 * @param uuid
	 * @return
	 */
	public  RankWages getRankWages(String uuid){
		RankWages rankWages = new RankWages();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String sql = "SELECT`uuid`,`interiorId`,`rankId`,`wagesName`,`getValue`,`updateTache`,`orderId`,`remark`,`propenty`,`valueType`,groupFlag \n"
					+"FROM `asdb`.`k_rankwages` \n"
					+"WHERE `uuid` = ?;";
		try {
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, uuid);
			rs = ps.executeQuery();
		
			while (rs.next()) {
				rankWages.setUuid(uuid);
				rankWages.setInteriorId(rs.getString("interiorId"));
				rankWages.setRankId(rs.getString("rankId"));
				rankWages.setWagesName(rs.getString("wagesName"));
				rankWages.setGetValue(rs.getString("getValue"));
				rankWages.setUpdateTache(rs.getString("updateTache"));
				rankWages.setOrderId(rs.getString("orderId"));
				rankWages.setRemark(rs.getString("remark"));
				rankWages.setPropenty(rs.getString("propenty"));
				rankWages.setValueType(rs.getString("valueType"));
				rankWages.setGroupFlag(rs.getString("groupFlag"));
			} 
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return rankWages;
	}
 
}
