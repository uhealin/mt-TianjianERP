package com.matech.audit.service.rank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.matech.audit.service.rank.model.Rank;
import com.matech.framework.pub.db.DbUtil;

public class RankService {
	
	Connection conn = null;
	
	public RankService(Connection conn){
		
		this.conn = conn ;
		
	}
	
	/**
	 * 新增
	 * @param rank
	 * @return
	 */
	public boolean add(Rank rank){
		
		boolean result = false;
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String sql = "INSERT INTO `k_rank` \n"
		            +"(`autoId`,`name`,`ctype`,`sequenceNumber`,`group`,`explain`,baseSalary,timeSalary,`propenty`) \n"
		            +"VALUES (?,?,?,?,?,?,?,?,?);";
		try {
			
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, rank.getAutoId());
			ps.setString(i++, rank.getName());
			ps.setString(i++, rank.getCtype());
			ps.setString(i++, rank.getSequenceNumber());
			ps.setString(i++, rank.getGroup());
			ps.setString(i++, rank.getExplain());
			ps.setString(i++, rank.getBaseSalary());
			ps.setString(i++, rank.getTimeSalary());
			ps.setString(i++, rank.getPropenty());
			
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
	public  boolean update(Rank rank){
		boolean result = false;
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String sql = "UPDATE `k_rank` \n"
					+"SET  \n"
					+"  `name` =? , \n"
					+"  `ctype` = ?, \n"
					+"  `sequenceNumber` = ?, \n"
					+"  `group` = ?, \n"
					+"  `explain` = ?, \n"
					+"   baseSalary=?," +
					"    timeSalary=?, "
					+"  `propenty` = ? \n"
					+"WHERE `autoId` = ?;";
		try {
			
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, rank.getName());
			ps.setString(i++, rank.getCtype());
			ps.setString(i++, rank.getSequenceNumber());
			ps.setString(i++, rank.getGroup());
			ps.setString(i++, rank.getExplain());
			ps.setString(i++, rank.getBaseSalary());
			ps.setString(i++, rank.getTimeSalary());
			ps.setString(i++, rank.getPropenty());
			ps.setString(i++, rank.getAutoId());
			
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
	 * @param autoId
	 * @return
	 */
	public  boolean del(String autoId){
		boolean result = false;
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String sql = "delete from k_rank WHERE `autoId` = ?;";
		try {
			
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, autoId);
			
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
	 * 得到职级信息
	 * @param autoId
	 * @return
	 */
	public  Rank getRank(String autoId){
		Rank rank = new Rank();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String sql = "SELECT `autoId`,`name`,`ctype`,`sequenceNumber`,`group`,`explain`,baseSalary,timeSalary,`propenty` \n"
					+"FROM `k_rank` \n"
					+"WHERE `autoId` = ?;";
		try {
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, autoId);
			rs = ps.executeQuery();
		
			while (rs.next()) {
				rank.setAutoId(autoId);
				rank.setName(rs.getString("name"));
				rank.setCtype(rs.getString("ctype"));
				rank.setSequenceNumber(rs.getString("sequenceNumber"));
				rank.setGroup(rs.getString("group"));
				rank.setExplain(rs.getString("explain"));
				rank.setBaseSalary(rs.getString("baseSalary"));
				rank.setTimeSalary(rs.getString("timeSalary"));
				rank.setPropenty(rs.getString("propenty"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return rank;
	}
	
	/**
	 * 查询当前职级是否存在
	 * @param name
	 * @param ctype
	 * @return
	 */
	public  String getRankName(String name,String ctype){
		PreparedStatement ps = null;
		ResultSet rs = null;
		String rankName = "";
		String sql = "SELECT `name` FROM `k_rank` WHERE `name` = ? and ctype=? ";
		try {
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, name);
			ps.setString(2, ctype);
			rs = ps.executeQuery();
		
			while (rs.next()) {
				rankName = rs.getString("name");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return rankName;
	}
	/*
	 * 跟据id找对应的rank
	 */
	public Rank getRankById(String rankId){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Rank rank=null;
		try {
			String sql="select * from k_rank where id="+rankId;
			ps=this.conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){
				rank=new Rank();
				rank.setAutoId(rs.getString("autoId"));
				rank.setBaseSalary(rs.getString("baseSalary"));
				rank.setCtype(rs.getString("ctype"));
				rank.setExplain(rs.getString("explain"));
				rank.setGroup(rs.getString("group"));
				rank.setName(rs.getString("name"));
				rank.setPropenty(rs.getString("propenty"));
				rank.setSequenceNumber(rs.getString("sequenceNumber"));
				rank.setTimeSalary(rs.getString("timeSalary"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return rank;
	}
	/*
	 * 根据职级名称找职级id
	 */
	public String getRankId(String name){
		String rankId=null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql="select autoId from k_rank where name='"+name+"'";
			ps=this.conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){
				rankId=rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return rankId;
	}
}
