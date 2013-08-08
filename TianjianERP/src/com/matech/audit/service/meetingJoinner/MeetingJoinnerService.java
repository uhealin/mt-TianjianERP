package com.matech.audit.service.meetingJoinner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.matech.audit.service.meetingJoinner.model.MeetingJoinner;
import com.matech.framework.pub.db.DbUtil;

public class MeetingJoinnerService {
	private Connection conn = null;

	public MeetingJoinnerService(Connection conn) {
			this.conn = conn;
	}
	
	/**
	 * 根据编号得到对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public MeetingJoinner getMeetingJoinner(String id) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = " select uuid,meetingOrderid,waitingUserId,initUserId,actualUserId,answerUserId, "
					   + " answerTime,historyUserId,batchNumber,optStatus,reason,property from k_meetingjoiner "
					   + " where uuid = ? ";
			
			ps=conn.prepareStatement(sql);
			ps.setString(1, id);
			rs = ps.executeQuery();
			
			MeetingJoinner mj = new MeetingJoinner();;
			
			if(rs.next()){
				mj.setUuid(id);
				mj.setMeetingOrderId(rs.getString("meetingOrderId"));
				mj.setWaitingUserId(rs.getString("waitingUserId"));
				mj.setInitUserId(rs.getString("initUserId"));
				mj.setActualUserId(rs.getString("actualUserId"));
				
				mj.setAnswerUserId(rs.getString("answerUserId"));
				mj.setAnswerTime(rs.getString("answerTime"));
				mj.setHistoryUserId(rs.getString("historyUserId"));
				mj.setBatchNumber(rs.getString("batchNumber"));
				mj.setOptStatus(rs.getString("optStatus"));
				mj.setReason(rs.getString("reason"));
				mj.setProperty(rs.getString("property"));
			}
			
			return mj;
			
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
	public MeetingJoinner getMeetingJoinner(String meetingOrderid,String waitingUserId) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = " select uuid,meetingOrderid,waitingUserId,initUserId,actualUserId,answerUserId, "
					   + " answerTime,historyUserId,batchNumber,optStatus,reason,property from k_meetingjoiner "
					   + " where meetingOrderid = ? and waitingUserId = ? ";
			
			ps=conn.prepareStatement(sql);
			ps.setString(1, meetingOrderid);
			ps.setString(2, waitingUserId);
			rs = ps.executeQuery();
			
			MeetingJoinner mj = new MeetingJoinner();;
			
			if(rs.next()){
				mj.setUuid(rs.getString("uuid"));
				mj.setMeetingOrderId(rs.getString("meetingOrderId"));
				mj.setWaitingUserId(rs.getString("waitingUserId"));
				mj.setInitUserId(rs.getString("initUserId"));
				mj.setActualUserId(rs.getString("actualUserId"));
				
				mj.setAnswerUserId(rs.getString("answerUserId"));
				mj.setAnswerTime(rs.getString("answerTime"));
				mj.setHistoryUserId(rs.getString("historyUserId"));
				mj.setBatchNumber(rs.getString("batchNumber"));
				mj.setOptStatus(rs.getString("optStatus"));
				mj.setReason(rs.getString("reason"));
				mj.setProperty(rs.getString("property"));
			}
			
			return mj;
			
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
	 * @param meetingOrderid
	 * @return
	 * @throws Exception
	 */
	public List getMeetingJoinnerList(String meetingOrderid) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		List list = null;
		try {
			String sql = " select uuid,meetingOrderid,waitingUserId,initUserId,actualUserId,answerUserId, "
					   + " answerTime,historyUserId,batchNumber,optStatus,reason,property from k_meetingjoiner "
					   + " where meetingOrderid = ? ";
		
			ps=conn.prepareStatement(sql);
			ps.setString(1, meetingOrderid);
			rs = ps.executeQuery();
		
			
			list = new ArrayList();
			
			MeetingJoinner mj = null;
				
			while(rs.next()){
				
				mj = new MeetingJoinner();;
				
				mj.setUuid(rs.getString("uuid"));
				mj.setMeetingOrderId(rs.getString("meetingOrderId"));
				mj.setWaitingUserId(rs.getString("waitingUserId"));
				mj.setInitUserId(rs.getString("initUserId"));
				mj.setActualUserId(rs.getString("actualUserId"));
				
				mj.setAnswerUserId(rs.getString("answerUserId"));
				mj.setAnswerTime(rs.getString("answerTime"));
				mj.setHistoryUserId(rs.getString("historyUserId"));
				mj.setBatchNumber(rs.getString("batchNumber"));
				mj.setOptStatus(rs.getString("optStatus"));
				mj.setReason(rs.getString("reason"));
				mj.setProperty(rs.getString("property"));
				
				list.add(mj);
				
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
	 * @param meetingOrderid,batchNumber
	 * @return
	 * @throws Exception
	 */
	public List getMeetingJoinnerList(String meetingOrderid,String batchNumber) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		List list = null;
		try {
			
			String sql = " select uuid,meetingOrderid,waitingUserId,initUserId,actualUserId,answerUserId, "
					   + " answerTime,historyUserId,batchNumber,optStatus,reason,property from k_meetingjoiner "
					   + " where meetingOrderid = ? and batchNumber = ? ";
		
			ps=conn.prepareStatement(sql);
			ps.setString(1, meetingOrderid);
			ps.setString(2, batchNumber);
			rs = ps.executeQuery();
		
			list = new ArrayList();
			
			MeetingJoinner mj = null;
				
			while(rs.next()){
				
				mj = new MeetingJoinner();;
				
				mj.setUuid(rs.getString("uuid"));
				mj.setMeetingOrderId(rs.getString("meetingOrderId"));
				mj.setWaitingUserId(rs.getString("waitingUserId"));
				mj.setInitUserId(rs.getString("initUserId"));
				mj.setActualUserId(rs.getString("actualUserId"));
				
				mj.setAnswerUserId(rs.getString("answerUserId"));
				mj.setAnswerTime(rs.getString("answerTime"));
				mj.setHistoryUserId(rs.getString("historyUserId"));
				mj.setBatchNumber(rs.getString("batchNumber"));
				mj.setOptStatus(rs.getString("optStatus"));
				mj.setReason(rs.getString("reason"));
				mj.setProperty(rs.getString("property"));
				
				list.add(mj);
				
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
	 * @param mj
	 * @return
	 * @throws Exception
	 */
	public void addMeetingJoinner(MeetingJoinner mj) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " insert into k_meetingjoiner (uuid,meetingOrderid,waitingUserId,initUserId,actualUserId, "
					   + " answerUserId,answerTime,historyUserId,batchNumber,optStatus,reason,property) "
				       + " values(?,?,?,?,?, ?,?,?,?,?,?,?)";
			
			ps=conn.prepareStatement(sql);
			
			int i = 1;
			
			ps.setString(i++, mj.getUuid());
			ps.setString(i++, mj.getMeetingOrderId());
			ps.setString(i++, mj.getWaitingUserId());
			ps.setString(i++, mj.getInitUserId());
			ps.setString(i++, mj.getActualUserId());
			
			ps.setString(i++, mj.getAnswerUserId());
			ps.setString(i++, mj.getAnswerTime());
			ps.setString(i++, mj.getHistoryUserId());
			ps.setString(i++, mj.getBatchNumber());
			ps.setString(i++, mj.getOptStatus());
			ps.setString(i++, mj.getReason());
			ps.setString(i++, mj.getProperty());
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		
	}
	
	
	/**
	 * 修改 
	 * @param mj
	 * @return
	 * @throws Exception
	 */
	public void updateMeetingJoinner(MeetingJoinner mj) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " update k_meetingjoiner set meetingOrderid=?,waitingUserId=?,initUserId=?,actualUserId=?, "
					   + " answerUserId=?,answerTime=?,historyUserId=?,batchNumber=?,optStatus=?,reason=?,property=? "
				       + " where uuid = ? ";
			
			ps=conn.prepareStatement(sql);
			
			int i = 1;
			
			ps.setString(i++, mj.getMeetingOrderId());
			ps.setString(i++, mj.getWaitingUserId());
			ps.setString(i++, mj.getInitUserId());
			ps.setString(i++, mj.getActualUserId());
			
			ps.setString(i++, mj.getAnswerUserId());
			ps.setString(i++, mj.getAnswerTime());
			ps.setString(i++, mj.getHistoryUserId());
			ps.setString(i++, mj.getBatchNumber());
			ps.setString(i++, mj.getOptStatus());
			ps.setString(i++, mj.getReason());
			ps.setString(i++, mj.getProperty());
			
			ps.setString(i++, mj.getUuid());
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	
	

	/**
	 * 同意参加会议：修改 
	 * @param mj
	 * @return
	 * @throws Exception
	 */
	public void updateAgree(MeetingJoinner mj) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " update k_meetingjoiner set answerUserId=?,answerTime=?,optStatus=?,reason=? "
				       + " where meetingOrderId = ? and waitingUserId = ? ";
			
			ps=conn.prepareStatement(sql);
			
			int i = 1;
			
			ps.setString(i++, mj.getAnswerUserId());
			ps.setString(i++, mj.getAnswerTime());
			ps.setString(i++, mj.getOptStatus());
			ps.setString(i++, mj.getReason());
			
			ps.setString(i++, mj.getMeetingOrderId());
			ps.setString(i++, mj.getWaitingUserId());
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	
	
	/**
	 * 不同意参加会议：修改 
	 * @param mj
	 * @return
	 * @throws Exception
	 */
	public void updateDisAgree(MeetingJoinner mj) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " update k_meetingjoiner set answerUserId=?,answerTime=?,optStatus=?,reason=? "
				 	   + " where meetingOrderId = ? and waitingUserId = ? ";
			
			ps=conn.prepareStatement(sql);
			
			int i = 1;
			
			ps.setString(i++, mj.getAnswerUserId());
			ps.setString(i++, mj.getAnswerTime());
			ps.setString(i++, mj.getOptStatus());
			ps.setString(i++, mj.getReason());
			
			ps.setString(i++, mj.getMeetingOrderId());
			ps.setString(i++, mj.getWaitingUserId());
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 更换：修改 
	 * @param mj
	 * @return
	 * @throws Exception
	 */
	public void updateChange(MeetingJoinner mj) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " update k_meetingjoiner set answerUserId=?,answerTime=?,optStatus=?,reason=?,historyUserId=? "
				       + " where meetingOrderId = ? and waitingUserId = ? ";
			
			ps=conn.prepareStatement(sql);
			
			int i = 1;
			
			ps.setString(i++, mj.getAnswerUserId());
			ps.setString(i++, mj.getAnswerTime());
			ps.setString(i++, mj.getOptStatus());
			ps.setString(i++, mj.getReason());
			ps.setString(i++, mj.getHistoryUserId());
			
			ps.setString(i++, mj.getMeetingOrderId());
			ps.setString(i++, mj.getWaitingUserId());
			
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
	public void deleteMeetingJoinner(String id) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " delete from k_meetingjoiner where uuid = ? ";
			
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
	 * @param meetingOrderid
	 * @throws Exception
	 */
	public void deleteByMeetingOrderId(String meetingOrderid) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " delete from k_meetingjoiner where meetingOrderid = ? ";
			
			ps=conn.prepareStatement(sql);
			
			ps.setString(1, meetingOrderid);
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		
	}
	
}
