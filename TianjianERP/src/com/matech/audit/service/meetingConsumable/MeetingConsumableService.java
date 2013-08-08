package com.matech.audit.service.meetingConsumable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.matech.audit.service.meetingConsumable.model.MeetingConsumable;
import com.matech.framework.pub.db.DbUtil;

public class MeetingConsumableService {
	private Connection conn = null;

	public MeetingConsumableService(Connection conn) {
			this.conn = conn;
	}
	
	/**
	 * 根据编号得到对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public MeetingConsumable getMeetingConsumable(String id) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = " select uuid,meetingOrderId,names,counts,moneys,recordUser,recordTime,batchNumber,property " 
					   + " from k_meetingconsumable where uuid = ? ";
			
			ps=conn.prepareStatement(sql);
			ps.setString(1, id);
			rs = ps.executeQuery();
			
			MeetingConsumable mc = new MeetingConsumable();
			
			if(rs.next()){
				mc.setUuid(id);
				mc.setMeetingOrderId(rs.getString("meetingOrderId"));
				mc.setNames(rs.getString("names"));
				mc.setCounts(rs.getString("counts"));
				mc.setMoneys(rs.getString("moneys"));
				
				mc.setRecordUser(rs.getString("recordUser"));
				mc.setRecordTime(rs.getString("recordTime"));
				mc.setBatchNumber(rs.getString("batchNumber"));
				mc.setProperty(rs.getString("property"));
			}
			
			return mc;
			
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
	 * @param meetingOrderId
	 * @return
	 * @throws Exception
	 */
	public List getByMeetingOrderId(String meetingOrderId) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = " select uuid,meetingOrderId,names,counts,moneys,recordUser,recordTime,batchNumber,property " 
					   + " from k_meetingconsumable where meetingOrderId = ? ";
			
			ps=conn.prepareStatement(sql);
			ps.setString(1, meetingOrderId);
			rs = ps.executeQuery();
			
			List list = new ArrayList();
			
			MeetingConsumable mc = null;
			
			while(rs.next()){
				
				mc = new MeetingConsumable();
				
				mc.setUuid(rs.getString("uuid"));
				mc.setMeetingOrderId(rs.getString("meetingOrderId"));
				mc.setNames(rs.getString("names"));
				mc.setCounts(rs.getString("counts"));
				mc.setMoneys(rs.getString("moneys"));
				
				mc.setRecordUser(rs.getString("recordUser"));
				mc.setRecordTime(rs.getString("recordTime"));
				mc.setBatchNumber(rs.getString("batchNumber"));
				mc.setProperty(rs.getString("property"));
				
				list.add(mc);
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
	 * 根据编号得到对象
	 * @param meetingOrderId
	 * @return
	 * @throws Exception
	 */
	public List getByBatchNumber(String batchNumber) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = " select uuid,meetingOrderId,names,counts,moneys,recordUser,recordTime,batchNumber,property " 
					   + " from k_meetingconsumable where batchNumber = ? order by property ";
			
			ps=conn.prepareStatement(sql);
			ps.setString(1, batchNumber);
			rs = ps.executeQuery();
			
			List list = new ArrayList();
			
			MeetingConsumable mc = null;
			
			while(rs.next()){
				
				mc = new MeetingConsumable();
				
				mc.setUuid(rs.getString("uuid"));
				mc.setMeetingOrderId(rs.getString("meetingOrderId"));
				mc.setNames(rs.getString("names"));
				mc.setCounts(rs.getString("counts"));
				mc.setMoneys(rs.getString("moneys"));
				
				mc.setRecordUser(rs.getString("recordUser"));
				mc.setRecordTime(rs.getString("recordTime"));
				mc.setBatchNumber(rs.getString("batchNumber"));
				mc.setProperty(rs.getString("property"));
				
				list.add(mc);
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
	 * @param mc
	 * @return
	 * @throws Exception
	 */
	public void addMeetingConsumable(MeetingConsumable mc) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " insert into k_meetingconsumable (uuid,meetingOrderId,names,counts,moneys," 
					   + " recordUser,recordTime,batchNumber,property ) " 
				       + " values(?,?,?,?,?, ?,?,?,?)";
			
			ps=conn.prepareStatement(sql);
			
			int i = 1;
			
			ps.setString(i++, mc.getUuid());
			ps.setString(i++, mc.getMeetingOrderId());
			ps.setString(i++, mc.getNames());
			ps.setString(i++, mc.getCounts());
			ps.setString(i++, mc.getMoneys());
			
			ps.setString(i++, mc.getRecordUser());
			ps.setString(i++, mc.getRecordTime());
			ps.setString(i++, mc.getBatchNumber());
			ps.setString(i++, mc.getProperty());
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		
	}
	
	
	/**
	 * 修改
	 * @param mc
	 * @return
	 * @throws Exception
	 */
	public void updateMeetingConsumable(MeetingConsumable mc) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " update k_meetingconsumable set meetingOrderId=?,names=?,counts=?,moneys=?," 
					   + " recordUser=?,recordTime=?,batchNumber=?,property=? where uuid = ?";
			
			ps=conn.prepareStatement(sql);
			
			int i = 1;
			
			ps.setString(i++, mc.getMeetingOrderId());
			ps.setString(i++, mc.getNames());
			ps.setString(i++, mc.getCounts());
			ps.setString(i++, mc.getMoneys());
			ps.setString(i++, mc.getRecordUser());
			
			ps.setString(i++, mc.getRecordTime());
			ps.setString(i++, mc.getBatchNumber());
			ps.setString(i++, mc.getProperty());
			
			ps.setString(i++, mc.getUuid());
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
	public void deleteById(String id,String idValue) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			
			if("".equals(id) || id==null){
				id = "uuid";
			}
			
			String sql = " delete from k_meetingconsumable where "+id+" = '"+idValue+"' ";
			
			ps=conn.prepareStatement(sql);
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		
	}
}
