package com.matech.audit.service.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.matech.audit.service.project.model.BusinessProjectAssign;
import com.matech.framework.pub.db.DbUtil;

public class BusinessProjectAssignService {
	private Connection conn = null;

	public BusinessProjectAssignService(Connection conn) {
			this.conn = conn;
	}
	
	/**
	 * 根据编号得到对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public BusinessProjectAssign getBusinessProjectAssign(String id) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			
			String sql = " select autoId,entrustNum,ssassignType,ssassignUser,disProportion, " 
				   + " disMoney,StaffProportion,StaffMoney,orderid,property, "
				   + " ctype "
				   + " from z_projectbusinessassign where autoId = ?";
			
			ps=conn.prepareStatement(sql);
			ps.setString(1, id);
			rs = ps.executeQuery();
			
			BusinessProjectAssign  bpa= new BusinessProjectAssign();;
			
			if(rs.next()){
				bpa.setAutoId(rs.getString("autoId"));
				bpa.setEntrustNum(rs.getString("entrustNum"));
				bpa.setSsassignType(rs.getString("ssassignType"));
				bpa.setSsassignUser(rs.getString("ssassignUser"));
				bpa.setDisProportion(rs.getString("disProportion")) ;
				
				bpa.setDisMoney(rs.getString("disMoney")) ;
				bpa.setStaffProportion(rs.getString("StaffProportion")) ;
				bpa.setStaffMoney(rs.getString("StaffMoney")) ;
				bpa.setOrderid(rs.getString("orderid"));
				
				bpa.setProperty(rs.getString("property"));
				bpa.setCtype(rs.getString("ctype")) ;
			}
			return bpa;
			
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
	 * @param entrustNum
	 * @return
	 * @throws Exception
	 */
	public List getByEntrustNum(String entrustNum,String ctype) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = " select autoId,entrustNum,ssassignType,ssassignUser,disProportion, " 
					   + " disMoney,StaffProportion,StaffMoney,orderid,property, "
					   + " ctype "
					   + " from z_projectbusinessassign where entrustNum = ? and ctype=? order by orderid ";
			
			ps=conn.prepareStatement(sql);
			ps.setString(1, entrustNum);
			ps.setString(2,ctype) ;
			rs = ps.executeQuery();
			
			List list = new ArrayList();
			
			while(rs.next()){
				
				BusinessProjectAssign  bpa= new BusinessProjectAssign();
				
				bpa.setAutoId(rs.getString("autoId"));
				bpa.setEntrustNum(rs.getString("entrustNum"));
				bpa.setSsassignType(rs.getString("ssassignType"));
				bpa.setSsassignUser(rs.getString("ssassignUser"));
				bpa.setDisProportion(rs.getString("disProportion")) ;
				
				bpa.setDisMoney(rs.getString("disMoney")) ;
				bpa.setStaffProportion(rs.getString("StaffProportion")) ;
				bpa.setStaffMoney(rs.getString("StaffMoney")) ;
				bpa.setOrderid(rs.getString("orderid"));
				
				bpa.setProperty(rs.getString("property"));
				bpa.setCtype(rs.getString("ctype")) ;
				
				list.add(bpa);
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
	 * @param bpa
	 * @return
	 * @throws Exception
	 */
	public void addBusinessProjectAssign(BusinessProjectAssign bpa) throws Exception{
		
		
		PreparedStatement ps=null;
		try {
			String sql = " insert into asdb.z_projectbusinessassign (" 
					   + " entrustNum,ssassignType,ssassignUser,disProportion," 
					   + " disMoney,StaffProportion,StaffMoney,orderid," 
					   + " property,ctype,disDate) "
				       + " values(?,?,?,?, ?,?,?,?, ?,?,?)";
			
			ps=conn.prepareStatement(sql);
			
			int i = 1;
			
			ps.setString(i++, bpa.getEntrustNum());
			ps.setString(i++, bpa.getSsassignType());
			ps.setString(i++, bpa.getSsassignUser());
			ps.setString(i++, bpa.getDisProportion());
			
			ps.setString(i++, bpa.getDisMoney());
			ps.setString(i++, bpa.getStaffProportion());
			ps.setString(i++, bpa.getStaffMoney());
			ps.setString(i++, bpa.getOrderid());
			
			ps.setString(i++, bpa.getProperty());
			ps.setString(i++,bpa.getCtype()) ;
			ps.setString(i++, bpa.getDisDate()) ;
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		
	}
	
	
	/**
	 * 修改
	 * @param bpa
	 * @return
	 * @throws Exception
	 */
	public void updateBusinessProjectAssign(BusinessProjectAssign bpa) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " update z_projectbusinessassign " 
					   + " set entrustNum=?,ssassignType=?,ssassignUser=?,disProportion=?," 
					   + " disMoney=?,StaffProportion=?,StaffMoney=?,orderid=?, "
					   + " property=?,ctype=? "
				       + " where autoId=? ";
			
			ps=conn.prepareStatement(sql);
			
			int i = 1;
			
			ps.setString(i++, bpa.getEntrustNum());
			ps.setString(i++, bpa.getSsassignType());
			ps.setString(i++, bpa.getSsassignUser());
			ps.setString(i++, bpa.getDisProportion());
			
			ps.setString(i++, bpa.getDisMoney());
			ps.setString(i++, bpa.getStaffProportion());
			ps.setString(i++, bpa.getStaffMoney());
			ps.setString(i++, bpa.getOrderid());
			
			ps.setString(i++, bpa.getProperty());
			ps.setString(i++, bpa.getCtype());
			
			ps.setString(i++, bpa.getAutoId());
			
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
	public void deleteBusinessProjectAssign(String id) throws Exception{
		 
		PreparedStatement ps=null;
		try {
			String sql = " delete from z_projectbusinessassign where autoid = ? ";
			
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
	 * @param entrustNum
	 * @throws Exception
	 */
	public void deleteByEntrustNum(String entrustNum) throws Exception{
		 
		System.out.println("entrustNum="+entrustNum);
		
		PreparedStatement ps=null;
		try {
			String sql = " delete from asdb.z_projectbusinessassign where entrustNum = ? ";
			
			ps=conn.prepareStatement(sql);
			
			ps.setString(1, entrustNum);
			
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		
	}
	
	
}
