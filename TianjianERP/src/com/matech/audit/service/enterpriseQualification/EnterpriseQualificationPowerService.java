package com.matech.audit.service.enterpriseQualification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.matech.audit.service.enterpriseQualification.model.EnterpriseQualificationPower;
import com.matech.framework.pub.db.DbUtil;

public class EnterpriseQualificationPowerService {
	private Connection conn = null;

	public EnterpriseQualificationPowerService(Connection conn) {
			this.conn = conn;
	}
	
	/**
	 * 根据编号得到对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public EnterpriseQualificationPower getEnterpriseQualificationPower(String id) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = " select uuid,enterpriseQualificationId,powerId,powerType,modelName,property " 
					   + " from k_enterpriseQualificationPower where uuid = ? ";
			
			ps=conn.prepareStatement(sql);
			ps.setString(1, id);
			rs = ps.executeQuery();
			
			EnterpriseQualificationPower  eqp = new EnterpriseQualificationPower();;
			
			if(rs.next()){
				eqp.setUuid(id);
				eqp.setEnterpriseQualificationId(rs.getString("enterpriseQualificationId"));
				eqp.setPowerId(rs.getString("powerId"));
				eqp.setPowerType(rs.getString("powerType"));
				eqp.setModelName(rs.getString("modelName"));
				eqp.setProperty(rs.getString("property"));
			}
			return eqp;
			
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
	public List getEnterpriseQualificationPowerList(String id) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			String sql = " select uuid,enterpriseQualificationId,powerId,powerType,modelName,property " 
					   + " from k_enterpriseQualificationPower where enterpriseQualificationId = ? ";
			
			ps=conn.prepareStatement(sql);
			ps.setString(1, id);
			rs = ps.executeQuery();
			
			
			while(rs.next()){
				EnterpriseQualificationPower  eqp = new EnterpriseQualificationPower();;
				eqp.setUuid(id);
				eqp.setEnterpriseQualificationId(rs.getString("enterpriseQualificationId"));
				eqp.setPowerId(rs.getString("powerId"));
				eqp.setPowerType(rs.getString("powerType"));
				eqp.setModelName(rs.getString("modelName"));
				eqp.setProperty(rs.getString("property"));
				
				list.add(eqp);
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
	 * @param eq
	 * @return
	 * @throws Exception
	 */
	public void addEnterpriseQualificationPower(EnterpriseQualificationPower eqp) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " insert into k_enterpriseQualificationPower (uuid,enterpriseQualificationId,powerId,powerType,modelName,property ) "
				       + " values(?,?,?,?,?, ?)";
			
			ps=conn.prepareStatement(sql);
			
			int i = 1;
			
			ps.setString(i++, eqp.getUuid());
			ps.setString(i++, eqp.getEnterpriseQualificationId());
			ps.setString(i++, eqp.getPowerId());
			ps.setString(i++, eqp.getPowerType());
			ps.setString(i++, eqp.getModelName());
			
			ps.setString(i++, eqp.getProperty());
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		
	}
	
	

	/**
	 * 修改 
	 * @param eq
	 * @return
	 * @throws Exception
	 */
	public void updateEnterpriseQualificationPower(EnterpriseQualificationPower eqp) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " update k_enterpriseQualificationPower set powerId=?,powerType=?,modelName=?,property=? "
				       + " where enterpriseQualificationId = ? ";
			
			ps=conn.prepareStatement(sql);
			
			int i = 1;
			
			ps.setString(i++, eqp.getPowerId());
			ps.setString(i++, eqp.getPowerType());
			ps.setString(i++, eqp.getModelName());
			ps.setString(i++, eqp.getProperty());
			
			ps.setString(i++, eqp.getEnterpriseQualificationId());
			
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
	public void delete(String id) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " delete from k_enterpriseQualificationPower where uuid = ? ";
			
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
