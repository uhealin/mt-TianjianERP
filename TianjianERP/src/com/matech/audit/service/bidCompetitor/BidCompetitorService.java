package com.matech.audit.service.bidCompetitor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.matech.audit.service.bidCompetitor.model.BidCompetitor;
import com.matech.framework.pub.db.DbUtil;

public class BidCompetitorService {
	
	private Connection conn = null;

	public BidCompetitorService(Connection conn) {
			this.conn = conn;
	}
	
	/**
	 * 根据编号得到对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public BidCompetitor getBidCompetitor(String id) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = " select bidCompetitor,bidProjectId,bidCompetitorPrice,bidMemberSuperiority," 
					   + " bidMemberDisadvantaged,property "
					   + " from k_bidCompetitor where uuid = ? ";
			
			ps=conn.prepareStatement(sql);
			ps.setString(1, id);
			rs = ps.executeQuery();
			
			BidCompetitor  bc= new BidCompetitor();;
			
			if(rs.next()){
				
				bc.setUuid(id);
				bc.setBidCompetitor(rs.getString("bidCompetitor"));
				bc.setBidProjectId(rs.getString("bidProjectId"));
				bc.setBidCompetitorPrice(rs.getString("bidCompetitorPrice"));
				bc.setBidMemberSuperiority(rs.getString("bidMemberSuperiority"));
				
				bc.setBidMemberDisadvantaged(rs.getString("bidMemberDisadvantaged"));
				bc.setProperty(rs.getString("property"));

			}
			
			return bc;
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return null;
	}
	
	
	/**
	 * 根据编号得到对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public List getBidCompetitorList(String bidProjectId) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		List list = null;
		try {
			String sql = " select uuid,bidCompetitor,bidProjectId,bidCompetitorPrice,bidMemberSuperiority," 
					   + " bidMemberDisadvantaged,property "
					   + " from k_bidCompetitor where bidProjectId = ? order by property ";
			
			ps=conn.prepareStatement(sql);
			ps.setString(1, bidProjectId);
			rs = ps.executeQuery();
			
			list = new ArrayList();
			
			BidCompetitor bc = null;
				
			while(rs.next()){
				
				bc = new BidCompetitor();;
				
				bc.setUuid("uuid");
				bc.setBidCompetitor(rs.getString("bidCompetitor"));
				bc.setBidProjectId(rs.getString("bidProjectId"));
				bc.setBidCompetitorPrice(rs.getString("bidCompetitorPrice"));
				bc.setBidMemberSuperiority(rs.getString("bidMemberSuperiority"));
				
				bc.setBidMemberDisadvantaged(rs.getString("bidMemberDisadvantaged"));
				bc.setProperty(rs.getString("property"));
				
				list.add(bc);
				
			}
			
			return list;
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return null;
	}
	
	
	
	/**
	 * 新增 
	 * @param bc
	 * @return
	 * @throws Exception
	 */
	public void addBidCompetitor(BidCompetitor bc) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " insert into k_bidCompetitor (uuid,bidCompetitor,bidProjectId,bidCompetitorPrice,bidMemberSuperiority," 
					   + " bidMemberDisadvantaged,property) "
				       + " values(?,?,?,?,?, ?,?)";
			
			ps=conn.prepareStatement(sql);
			
			int i = 1;
			
			ps.setString(i++, bc.getUuid());
			ps.setString(i++, bc.getBidCompetitor());
			ps.setString(i++, bc.getBidProjectId());
			ps.setString(i++, bc.getBidCompetitorPrice());
			ps.setString(i++, bc.getBidMemberSuperiority());
			
			ps.setString(i++, bc.getBidMemberDisadvantaged());
			ps.setString(i++, bc.getProperty());
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		
	}
	
	
	/**
	 * 修改
	 * @param bc
	 * @return
	 * @throws Exception
	 */
	public void updateBidCompetitor(BidCompetitor bc) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " update k_bidCompetitor set bidCompetitor=?,bidProjectId=?,bidCompetitorPrice=?,bidMemberSuperiority=?," 
					   + " bidMemberDisadvantaged=?,property=? "
				       + " where uuid = ? ";
			
			ps=conn.prepareStatement(sql);
			
			int i = 1;
			
			ps.setString(i++, bc.getBidCompetitor());
			ps.setString(i++, bc.getBidProjectId());
			ps.setString(i++, bc.getBidCompetitorPrice());
			ps.setString(i++, bc.getBidMemberSuperiority());
			ps.setString(i++, bc.getBidMemberDisadvantaged());
			
			ps.setString(i++, bc.getProperty());
			
			ps.setString(i++, bc.getUuid());
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		
	}
	
	/**
	 * 删除
	 * @param id
	 * @throws Exception
	 */
	public void deleteBidCompetitor(String id) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " delete from k_bidCompetitor where uuid = ? ";
			
			ps=conn.prepareStatement(sql);
			
			ps.setString(1, id);
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		
	}
	
	
	/**
	 * 删除
	 * @param id
	 * @throws Exception
	 */
	public void deleteByBidProjectId(String bidProjectId) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " delete from k_bidCompetitor where bidProjectId = ? ";
			
			ps=conn.prepareStatement(sql);
			
			ps.setString(1, bidProjectId);
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		
	}
	
}
