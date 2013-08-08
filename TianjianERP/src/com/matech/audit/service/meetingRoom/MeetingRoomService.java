package com.matech.audit.service.meetingRoom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.meetingRoom.model.MeetingRoom;
import com.matech.framework.pub.db.DbUtil;

public class MeetingRoomService {
	private Connection conn = null;

	public MeetingRoomService(Connection conn) {
			this.conn = conn;
	}
	
	/**
	 * 根据编号得到对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public MeetingRoom getMeetingRoom(String id) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = " select uuid,name,organ,containPerson,device, " 
					   + " describes,place,creatorId,createTime,property " 
					   + " from k_meetingRoom where uuid = ? ";
			
			ps=conn.prepareStatement(sql);
			ps.setString(1, id);
			rs = ps.executeQuery();
			
			MeetingRoom  mr= new MeetingRoom();;
			
			if(rs.next()){
				mr.setUuid(id);
				mr.setName(rs.getString("name"));
				mr.setOrgan(rs.getString("organ"));
				mr.setContainPerson(rs.getString("containPerson"));
				mr.setDevice(rs.getString("device"));
				
				mr.setDescribes(rs.getString("describes"));
				mr.setPlace(rs.getString("place"));
				mr.setCreatorId(rs.getString("creatorId"));
				mr.setCreateTime(rs.getString("createTime"));
				mr.setProperty(rs.getString("property"));
			}
			return mr;
			
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
	 * @param mr
	 * @return
	 * @throws Exception
	 */
	public void addMeetingRoom(MeetingRoom mr) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " insert into k_meetingRoom (uuid,name,organ,containPerson,device, " 
					   + " describes,place,creatorId,createTime,property ) "
				       + " values(?,?,?,?,?, ?,?,?,?,? )";
			
			ps=conn.prepareStatement(sql);
			
			int i = 1;
			
			ps.setString(i++, mr.getUuid());
			ps.setString(i++, mr.getName());
			ps.setString(i++, mr.getOrgan());
			ps.setString(i++, mr.getContainPerson());
			ps.setString(i++, mr.getDevice());
			
			ps.setString(i++, mr.getDescribes());
			ps.setString(i++, mr.getPlace());
			ps.setString(i++, mr.getCreatorId());
			ps.setString(i++, mr.getCreateTime());
			ps.setString(i++, mr.getProperty());
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		
	}
	
	
	/**
	 * 修改
	 * @param mr
	 * @return
	 * @throws Exception
	 */
	public void updateMeetingRoom(MeetingRoom mr) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " update k_meetingRoom set name=?,organ=?,containPerson=?,device=?, " 
					   + " describes=?,place=?,creatorId=?,createTime=?,property=? "
				       + " where uuid=? ";
			
			ps=conn.prepareStatement(sql);
			
			int i = 1;
			
			ps.setString(i++, mr.getName());
			ps.setString(i++, mr.getOrgan());
			ps.setString(i++, mr.getContainPerson());
			ps.setString(i++, mr.getDevice());
			
			ps.setString(i++, mr.getDescribes());
			ps.setString(i++, mr.getPlace());
			ps.setString(i++, mr.getCreatorId());
			ps.setString(i++, mr.getCreateTime());
			ps.setString(i++, mr.getProperty());
			
			ps.setString(i++, mr.getUuid());
			
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
	public void deleteMeetingRoom(String id) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " delete from k_meetingRoom where uuid = ? ";
			
			ps=conn.prepareStatement(sql);
			
			ps.setString(1, id);
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		
	}
	
}
