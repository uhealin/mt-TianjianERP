package com.matech.audit.service.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.matech.audit.service.customer.model.Manager;
import com.matech.framework.pub.db.DbUtil;

public class ManagerService {
	
	private Connection conn = null;
	
	public ManagerService(Connection conn) throws Exception {
		DbUtil.checkConn(conn);
		this.conn = conn;
	}

	public void addManagers(List list, String customerid) throws Exception{	
		
		Iterator it = list.iterator();
		Manager manager = null;
		
		String sql = "insert into k_manager(customerid, position, name, sex, qualification, mobilephone, fixedphone, email, contact1,contact2)";
		sql += " values(?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		try{
			while(it.hasNext()){
				manager = (Manager)it.next();
				ps = conn.prepareStatement(sql);
				
				ps.setString(1, customerid);
				ps.setString(2, manager.getPosition());
				ps.setString(3, manager.getName());
				ps.setString(4, manager.getSex());
				ps.setString(5, manager.getQualification());
				ps.setString(6, manager.getMobilephone());
				ps.setString(7, manager.getFixedphone());
				ps.setString(8, manager.getEmail());
				ps.setString(9, manager.getOther_contact1());
				ps.setString(10, manager.getOther_contact2());
				
				ps.execute();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	
	public List getManagerByCustomerid(String customerid) throws Exception {
		
		List list = new ArrayList();
		Manager manager = null;
		
		String sql = "select * from k_manager where customerid = ?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try{
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, customerid);
			rs = ps.executeQuery();
			
			while(rs.next()){
				manager = new Manager();
				
				manager.setPosition(rs.getString("position"));
				manager.setName(rs.getString("name"));
				manager.setSex(rs.getString("sex"));
				manager.setQualification(rs.getString("qualification"));
				manager.setMobilephone(rs.getString("mobilephone"));
				manager.setFixedphone(rs.getString("fixedphone"));
				manager.setEmail(rs.getString("email"));
				manager.setOther_contact1(rs.getString("contact1"));
				manager.setOther_contact2(rs.getString("contact2"));
				
				list.add(manager);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return list;
	}
	
	public void deleteByCustomerid(String customerid) throws Exception {
		
		PreparedStatement ps = null;
		String sql = "delete from k_manager where customerid = ?";
		try{
			ps = conn.prepareStatement(sql);
			ps.setString(1, customerid);
			ps.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
}
