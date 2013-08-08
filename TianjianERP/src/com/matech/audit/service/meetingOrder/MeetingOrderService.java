package com.matech.audit.service.meetingOrder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.matech.audit.service.meetingOrder.model.MeetingOrder;
import com.matech.framework.pub.db.DbUtil;

public class MeetingOrderService {

	private Connection conn = null;

	public MeetingOrderService(Connection conn) {
			this.conn = conn;
	}
	
	/**
	 * 根据编号得到对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public MeetingOrder getMeetingOrder(String id) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = " select uuid,name,title,event,meetingRoomId,startTime, "
					   + " endTime,requirements,equipment,departmentId,describes,attachFileId, "
					   + " createDate,createUserId,auditDate,auditUserId,status,reason,propertys " 
					   + " from k_meetingorder where uuid = ? ";
			
			ps=conn.prepareStatement(sql);
			ps.setString(1, id);
			rs = ps.executeQuery();
			
			MeetingOrder mo = new MeetingOrder();;
			
			if(rs.next()){
				mo.setUuid(id);
				mo.setName(rs.getString("name"));
				mo.setTitle(rs.getString("title"));
				mo.setEvent(rs.getString("event"));
				mo.setMeetingRoomId(rs.getString("meetingRoomId"));
				mo.setStartTime(rs.getString("startTime"));
				
				mo.setEndTime(rs.getString("endTime"));
				mo.setRequirements(rs.getString("requirements"));
				mo.setEquipment(rs.getString("equipment"));
				mo.setDepartmentId(rs.getString("departmentId"));
				mo.setDescribes(rs.getString("describes"));
				mo.setAttachFileId(rs.getString("attachFileId"));
				
				mo.setCreateDate(rs.getString("createDate"));
				mo.setCreateUserId(rs.getString("createUserId"));
				mo.setAuditDate(rs.getString("auditDate"));
				mo.setAuditUserId(rs.getString("auditUserId"));
				mo.setStatus(rs.getString("status"));
				mo.setReason(rs.getString("reason"));
				mo.setPropertys(rs.getString("propertys"));

			}
			
			return mo;
			
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
	 * @param meetingRoomId
	 * @return
	 * @throws Exception
	 */
	public List getMeetingOrderList(String meetingRoomId) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		List list = null;
		try {
			String sql = " select uuid,name,title,event,meetingRoomId,startTime, "
					   + " endTime,requirements,equipment,departmentId,describes,attachFileId, "
					   + " createDate,createUserId,auditDate,auditUserId,status,reason,propertys" 
					   + " from k_meetingorder where meetingRoomId = ? ";
		
			ps=conn.prepareStatement(sql);
			ps.setString(1, meetingRoomId);
			rs = ps.executeQuery();
		
			
			list = new ArrayList();
			
			MeetingOrder mo = null;
				
			while(rs.next()){
				
				mo = new MeetingOrder();;
				
				mo.setUuid(rs.getString("id"));
				mo.setName(rs.getString("name"));
				mo.setTitle(rs.getString("title"));
				mo.setEvent(rs.getString("event"));
				mo.setMeetingRoomId(rs.getString("meetingRoomId"));
				mo.setStartTime(rs.getString("startTime"));
				
				mo.setEndTime(rs.getString("endTime"));
				mo.setRequirements(rs.getString("requirements"));
				mo.setEquipment(rs.getString("equipment"));
				mo.setDepartmentId(rs.getString("departmentId"));
				mo.setDescribes(rs.getString("describes"));
				mo.setAttachFileId(rs.getString("attachFileId"));
				
				mo.setCreateDate(rs.getString("createDate"));
				mo.setCreateUserId(rs.getString("createUserId"));
				mo.setAuditDate(rs.getString("auditDate"));
				mo.setAuditUserId(rs.getString("auditUserId"));
				mo.setStatus(rs.getString("status"));
				mo.setPropertys(rs.getString("propertys"));
				
				list.add(mo);
				
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
	 * @param mo
	 * @return
	 * @throws Exception
	 */
	public void addMeetingOrder(MeetingOrder mo) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " insert into k_meetingorder (uuid,name,title,event,meetingRoomId,startTime, "
				   	   + " endTime,requirements,equipment,departmentId,describes,attachFileId,createDate, "
				   	   + " createUserId,auditDate,auditUserId,status,reason,propertys) "
				       + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			
			ps=conn.prepareStatement(sql);
			
			int i = 1;
			
			ps.setString(i++, mo.getUuid());
			ps.setString(i++, mo.getName());
			ps.setString(i++, mo.getTitle());
			ps.setString(i++, mo.getEvent());
			ps.setString(i++, mo.getMeetingRoomId());
			ps.setString(i++, mo.getStartTime());
			
			ps.setString(i++, mo.getEndTime());
			
			ps.setString(i++, mo.getRequirements());
			ps.setString(i++, mo.getEquipment());
			ps.setString(i++, mo.getDepartmentId());
			ps.setString(i++, mo.getDescribes());
			ps.setString(i++, mo.getAttachFileId());
			ps.setString(i++, mo.getCreateDate());
			
			ps.setString(i++, mo.getCreateUserId());
			ps.setString(i++, mo.getAuditDate());
			ps.setString(i++, mo.getAuditUserId());
			ps.setString(i++, mo.getStatus());
			ps.setString(i++, mo.getReason());
			ps.setString(i++, mo.getPropertys());
			
			System.out.println("sql = "+sql); 
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		
	}
	
	
	/**
	 * 修改 
	 * @param mo
	 * @return
	 * @throws Exception
	 */
	public void updateMeetingOrder(MeetingOrder mo) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " update k_meetingorder set name=?,title=?,event=?,meetingRoomId=?,startTime=?,endTime=?, "
				   	   + " requirements=?,equipment=?,departmentId=?,describes=?,attachFileId=?,createDate=?,createUserId=?,auditDate=?, "
				   	   + " auditUserId=?,status=?,propertys=? "
				       + " where uuid = ? ";
			
			ps=conn.prepareStatement(sql);
			
			int i = 1;
			
			ps.setString(i++, mo.getName());
			ps.setString(i++, mo.getTitle());
			ps.setString(i++, mo.getEvent());
			ps.setString(i++, mo.getMeetingRoomId());
			ps.setString(i++, mo.getStartTime());
			ps.setString(i++, mo.getEndTime());
			
			ps.setString(i++, mo.getRequirements());
			ps.setString(i++, mo.getEquipment());
			ps.setString(i++, mo.getDepartmentId());
			ps.setString(i++, mo.getDescribes());
			ps.setString(i++, mo.getAttachFileId());
			ps.setString(i++, mo.getCreateDate());
			ps.setString(i++, mo.getCreateUserId());
			ps.setString(i++, mo.getAuditDate());
			
			ps.setString(i++, mo.getAuditUserId());
			ps.setString(i++, mo.getStatus());
			ps.setString(i++, mo.getPropertys());
			
			ps.setString(i++, mo.getUuid());
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	

	
	/**
	 * 审核 
	 * @param mo
	 * @return
	 * @throws Exception
	 */
	public void audit(MeetingOrder mo) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " update k_meetingorder set auditDate=?,auditUserId=?,status=?,reason=? "
				       + " where uuid = ? ";
			
			ps=conn.prepareStatement(sql);
			
			int i = 1;
			
			ps.setString(i++, mo.getAuditDate());
			ps.setString(i++, mo.getAuditUserId());
			ps.setString(i++, mo.getStatus());
			ps.setString(i++, mo.getReason());
			
			ps.setString(i++, mo.getUuid());
			
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
	public void deleteMeetingOrder(String id) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " delete from k_meetingorder where uuid = ? ";
			
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
	public void deleteByMeetingRoomId(String meetingRoomId) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " delete from k_meetingorder where meetingRoomId = ? ";
			
			ps=conn.prepareStatement(sql);
			
			ps.setString(1, meetingRoomId);
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		
	}
	
	

	public List getListBySql(String sql) {

		PreparedStatement ps = null ;
		ResultSet rs = null ;
		List list = new ArrayList();
		try {
			ps = conn.prepareStatement(sql) ;
			rs = ps.executeQuery() ;
			ResultSetMetaData RSMD = rs.getMetaData();
			while(rs.next()){
				Map map = new HashMap();
				for (int i = 1; i<=RSMD.getColumnCount(); i++) {
					map.put(RSMD.getColumnName(i).toLowerCase(),rs.getString(RSMD.getColumnName(i).toLowerCase()));
				}
				list.add(map);
			}
			return list ;
		}catch(Exception e) {
			e.printStackTrace() ;
		}finally{
			DbUtil.close(rs) ;
			DbUtil.close(ps) ;
			
		}
		return null ;
	}
}
