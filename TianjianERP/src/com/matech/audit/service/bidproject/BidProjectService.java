package com.matech.audit.service.bidproject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.matech.audit.service.bidproject.model.BidProject;
import com.matech.framework.pub.db.DbUtil;

public class BidProjectService {
	
	private Connection conn = null;

	public BidProjectService(Connection conn) {
			this.conn = conn;
	}
	

	/**
	 * 根据编号得到对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public BidProject getBidProject(String id) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = " select uuid,auditUnit,trustOrgan,serviceStartTime,serviceEndTime, "
					   + " serviceType,projectName,projectSimpleName,unitName,vocationType, "
					   + " unitEngName,hylx,register,curName,unitSimpleName, "
					   + " endDate,bidMember,bidMemberName,duty,bidAttachId,bidCompetitor,bidAttachFileId, "
					   + " createId,createName,createDate,auditorId,auditStatus,auditorName," 
					   + " auditDate,getBidPerson,getBidPrice,bidStatus,reason,property ,isGetBidProject"
					   + " from k_bidProject where uuid = ? ";
			
			ps=conn.prepareStatement(sql);
			ps.setString(1, id);
			rs = ps.executeQuery();
			
			BidProject  bp= new BidProject();;
			
			if(rs.next()){
				bp.setUuid(id);
				bp.setAuditUnit(rs.getString("auditUnit"));
				bp.setTrustOrgan(rs.getString("trustOrgan"));
				bp.setServiceStartTime(rs.getString("serviceStartTime"));
				bp.setServiceEndTime(rs.getString("serviceEndTime"));
				
				bp.setServiceType(rs.getString("serviceType"));
				bp.setProjectName(rs.getString("projectName"));
				bp.setProjectSimpleName(rs.getString("projectSimpleName"));
				bp.setUnitName(rs.getString("unitName"));
				bp.setVocationType(rs.getString("vocationType"));
				
				bp.setUnitEngName(rs.getString("unitEngName"));
				bp.setHylx(rs.getString("hylx"));
				bp.setRegister(rs.getString("register"));
				bp.setCurName(rs.getString("curName"));
				bp.setUnitSimpleName(rs.getString("unitSimpleName"));
				
				bp.setEndDate(rs.getString("endDate"));
				bp.setBidMember(rs.getString("bidMember"));
				bp.setBidMemberName(rs.getString("bidMemberName"));
				bp.setDuty(rs.getString("duty"));
				bp.setBidAttachId(rs.getString("bidAttachId"));
				bp.setBidCompetitor(rs.getString("bidCompetitor"));
				bp.setBidAttachFileId(rs.getString("bidAttachFileId"));
				
				bp.setCreateId(rs.getString("createId"));
				bp.setCreateName(rs.getString("createName"));
				bp.setCreateDate(rs.getString("createDate"));
				bp.setAuditorId(rs.getString("auditorId"));
				bp.setAuditStatus(rs.getString("auditStatus"));
				bp.setAuditorName(rs.getString("auditorName"));
				
				bp.setAuditDate(rs.getString("auditDate"));
				bp.setGetBidPerson(rs.getString("getBidPerson"));
				bp.setGetBidPrice(rs.getString("getBidPrice"));
				bp.setBidStatus(rs.getString("bidStatus"));
				bp.setReason(rs.getString("reason"));
				bp.setProperty(rs.getString("property"));
				bp.setIsGetBidProject(rs.getString("isGetBidProject"));
			}
			return bp;
			
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
	 * @param bp
	 * @return
	 * @throws Exception
	 */
	public void addBidProject(BidProject bp) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " insert into k_bidProject (uuid,auditUnit,trustOrgan,serviceStartTime,serviceEndTime, "
					   + " serviceType,projectName,projectSimpleName,unitName,vocationType, "
					   + " unitEngName,hylx,register,curName,unitSimpleName, "
					   + " endDate,bidMember,bidMemberName,duty,bidAttachId,bidCompetitor,bidAttachFileId, "
					   + " createId,createName,createDate,auditorId,auditStatus,auditorName," 
					   + " auditDate,getBidPerson,getBidPrice,bidStatus,isGetBidProject,property) "
				       + " values(?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?,?,?, ?,?,?,?,?,?, ?,?,?,?,?,?)";
			
			ps=conn.prepareStatement(sql);
			
			int i = 1;
			
			ps.setString(i++, bp.getUuid());
			ps.setString(i++, bp.getAuditUnit());
			ps.setString(i++, bp.getTrustOrgan());
			ps.setString(i++, bp.getServiceStartTime());
			ps.setString(i++, bp.getServiceEndTime());
			
			ps.setString(i++, bp.getServiceType());
			ps.setString(i++, bp.getProjectName());
			ps.setString(i++, bp.getProjectSimpleName());
			ps.setString(i++, bp.getUnitName());
			ps.setString(i++, bp.getVocationType());
			
			ps.setString(i++, bp.getUnitEngName());
			ps.setString(i++, bp.getHylx());
			ps.setString(i++, bp.getRegister());
			ps.setString(i++, bp.getCurName());
			ps.setString(i++, bp.getUnitSimpleName());
			
			ps.setString(i++, bp.getEndDate());
			ps.setString(i++, bp.getBidMember());
			ps.setString(i++, bp.getBidMemberName());
			ps.setString(i++, bp.getDuty());
			ps.setString(i++, bp.getBidAttachId());
			ps.setString(i++, bp.getBidCompetitor());
			ps.setString(i++, bp.getBidAttachFileId());
			
			ps.setString(i++, bp.getCreateId());
			ps.setString(i++, bp.getCreateName());
			ps.setString(i++, bp.getCreateDate());
			ps.setString(i++, bp.getAuditorId());
			ps.setString(i++, bp.getAuditStatus());
			ps.setString(i++, bp.getAuditorName());
			
			ps.setString(i++, bp.getAuditDate());
			ps.setString(i++, bp.getGetBidPerson());
			ps.setString(i++, bp.getGetBidPrice());
			ps.setString(i++, bp.getBidStatus());
			ps.setString(i++, bp.getIsGetBidProject());
			ps.setString(i++, bp.getProperty());
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		
	}
	
	
	/**
	 * 修改
	 * @param bp
	 * @return
	 * @throws Exception
	 */
	public void updateBidProject(BidProject bp) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " update k_bidProject set auditUnit=?,trustOrgan=?,serviceStartTime=?,serviceEndTime=?, "
					   + " serviceType=?,projectName=?,projectSimpleName=?,unitName=?,vocationType=?, "
					   + " unitEngName=?,hylx=?,register=?,curName=?,unitSimpleName=?, "
					   + " endDate=?,bidMember=?,bidMemberName=?,duty=?,bidAttachId=?,bidCompetitor=?,bidAttachFileId=?, "
					   + " createId=?,createName=?,createDate=?,auditorId=?,auditStatus=?,auditorName=?," 
					   + " auditDate=?,getBidPerson=?,getBidPrice=?,bidStatus=?,property=?  "
				       + " where uuid = ? ";
			   
			ps=conn.prepareStatement(sql);
			
			int i = 1;
			
			ps.setString(i++, bp.getAuditUnit());
			ps.setString(i++, bp.getTrustOrgan());
			ps.setString(i++, bp.getServiceStartTime());
			ps.setString(i++, bp.getServiceEndTime());
			
			ps.setString(i++, bp.getServiceType());
			ps.setString(i++, bp.getProjectName());
			ps.setString(i++, bp.getProjectSimpleName());
			ps.setString(i++, bp.getUnitName());
			ps.setString(i++, bp.getVocationType());
			
			ps.setString(i++, bp.getUnitEngName());
			ps.setString(i++, bp.getHylx());
			ps.setString(i++, bp.getRegister());
			ps.setString(i++, bp.getCurName());
			ps.setString(i++, bp.getUnitSimpleName());
			
			ps.setString(i++, bp.getEndDate());
			ps.setString(i++, bp.getBidMember());
			ps.setString(i++, bp.getBidMemberName());
			ps.setString(i++, bp.getDuty());
			ps.setString(i++, bp.getBidAttachId());
			ps.setString(i++, bp.getBidCompetitor());
			ps.setString(i++, bp.getBidAttachFileId());
			
			ps.setString(i++, bp.getCreateId());
			ps.setString(i++, bp.getCreateName());
			ps.setString(i++, bp.getCreateDate());
			ps.setString(i++, bp.getAuditorId());
			ps.setString(i++, bp.getAuditStatus());
			ps.setString(i++, bp.getAuditorName());
			
			ps.setString(i++, bp.getAuditDate());
			ps.setString(i++, bp.getGetBidPerson());
			ps.setString(i++, bp.getGetBidPrice());
			ps.setString(i++, bp.getBidStatus());
			ps.setString(i++, bp.getProperty());
			
			ps.setString(i++, bp.getUuid());
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		
	}
	
	
	/**
	 * 修改 中标人 中标价
	 * @param bp
	 * @return
	 * @throws Exception
	 */
	public void updateBidProjectAfter(BidProject bp) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " update k_bidProject set getBidPerson=?,getBidPrice=?,bidCompetitor=?,bidStatus=?,isGetBidProject=? "
				       + " where uuid = ? ";
			   
			ps=conn.prepareStatement(sql);
			
			int i = 1;
			
			ps.setString(i++, bp.getGetBidPerson());
			ps.setString(i++, bp.getGetBidPrice());
			ps.setString(i++, bp.getBidCompetitor());
			ps.setString(i++, bp.getBidStatus());
			ps.setString(i++, bp.getIsGetBidProject());
			
			ps.setString(i++, bp.getUuid());
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		
	}
	
	
	/**
	 * 修改 标书 附件 
	 * @param bp
	 * @return
	 * @throws Exception
	 */
	public void updateBidAttachId(BidProject bp) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " update k_bidProject set bidAttachId=? "
				       + " where uuid = ? ";
			   
			ps=conn.prepareStatement(sql);
			
			int i = 1;
			
			ps.setString(i++, bp.getBidAttachId());
			
			ps.setString(i++, bp.getUuid());
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		
	}
	
	

	/**
	 * 修改  审核人 信息
	 * @param bp
	 * @return
	 * @throws Exception
	 */
	public void updateBidAuditor(BidProject bp) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " update k_bidProject set auditorId=?,auditorName=?,auditDate=?,auditStatus=?,reason=? "
				       + " where uuid = ? ";
			   
			ps=conn.prepareStatement(sql);
			
			int i = 1;
			
			ps.setString(i++, bp.getAuditorId());
			ps.setString(i++, bp.getAuditorName());
			ps.setString(i++, bp.getAuditDate());
			ps.setString(i++, bp.getAuditStatus());
			ps.setString(i++, bp.getReason());
			
			ps.setString(i++, bp.getUuid());
			
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
	public void deleteBidProject(String id) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " delete from k_bidProject where uuid = ? ";
			
			ps=conn.prepareStatement(sql);
			
			ps.setString(1, id);
			
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
	
	
	/**
	 * 数组转 list
	 * @param temp1
	 * @param temp2
	 * @return
	 */
	public List toList(String[] temp0,String[] temp1,String[] temp2){
		List list = new ArrayList();
		for (int i = 0; i < temp1.length; i++) {
			Map map = new HashMap();
			map.put("bidMemberId", temp0[i]);
			map.put("bidMemberName", temp1[i]);
			map.put("duty", temp2[i]);
			list.add(map);
		}
		return list;
	}
	
}
