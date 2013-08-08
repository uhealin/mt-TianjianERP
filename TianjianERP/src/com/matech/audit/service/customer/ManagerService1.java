package com.matech.audit.service.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;


import com.matech.audit.service.customer.model.Manager1;
import com.matech.framework.pub.db.DbUtil;

public class ManagerService1 {
private Connection conn = null;
	
	public ManagerService1(Connection conn){
		
		this.conn=conn;
	}
	
	 public boolean Manageradd(Manager1 manager1)throws Exception{
		 
		 DbUtil.checkConn(conn);	
		 PreparedStatement ps = null;
		 
		 
		 try {
			 
			// String autoid = DELUnid.getCharUnid();
			 
			 String sql = "insert into k_manager"
				 		 +"(customerid,position, name, sex, qualification,mobilephone, fixedphone, email, contact1,contact2,resume,birthday)"
				 		 +"values(?,?,?,?,?,?,?,?,?,?,?,?)";
			
			 ps = conn.prepareStatement(sql);
			 
			 ps.setString(1,manager1.getCustomerid());
			 ps.setString(2,manager1.getPosition());
			 ps.setString(3,manager1.getName());
			 ps.setString(4,manager1.getSex());
			 ps.setString(5,manager1.getQualification());
			 ps.setString(6,manager1.getMobilephone());
			 ps.setString(7,manager1.getFixedphone());
			 ps.setString(8,manager1.getEmail());
			 ps.setString(9,manager1.getContact1());
			 ps.setString(10,manager1.getContact2());
			 ps.setString(11, manager1.getResume());
			 ps.setString(12,manager1.getBirthday()) ;
			 
			 ps.execute();
			 
			 
			 return true;
			 
			 
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			DbUtil.close(ps);
		}
		 
		 return false;
		 
	 }
	 
	 public boolean updateManager(Manager1 manager1,String autoid) throws Exception{
			DbUtil.checkConn(conn);
			PreparedStatement ps = null;
			
			try {
				
				String sql = "update k_manager set position=?, name=?, sex=?,qualification=?,mobilephone=?, fixedphone=?, email=?, contact1=?,contact2=?,resume=?,birthday=? where autoid=?";
				
				ps = conn.prepareStatement(sql);
			
				
				ps.setString(1,manager1.getPosition());
				
				ps.setString(2,manager1.getName());
				
				ps.setString(3,manager1.getSex());
				
				ps.setString(4,manager1.getQualification());
				
				ps.setString(5,manager1.getMobilephone());
				ps.setString(6,manager1.getFixedphone());
				ps.setString(7,manager1.getEmail());
				ps.setString(8,manager1.getContact1());
				ps.setString(9,manager1.getContact2());
				ps.setString(10,manager1.getResume());
				ps.setString(11,manager1.getBirthday());
				
				ps.setString(12, autoid);
				
				ps.execute();
				
				return true;
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
				
				
			}finally{
				
				DbUtil.close(ps);
			}
			
			return false;
			
		}
	 
		public boolean removeManager(String autoid) throws Exception{
			DbUtil.checkConn(conn);
			PreparedStatement ps = null;

			
			try {
				
				
				
				String sql = "delete from k_manager where autoid=?";
				
				ps = conn.prepareStatement(sql);
				ps.setString(1, autoid);
				
				ps.execute();

				
				
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				DbUtil.close(ps);
			}
			
			return  false;
				
			
		}
			

}
