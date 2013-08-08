package com.matech.audit.service.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.customer.model.CustomerProject;
import com.matech.framework.pub.db.DbUtil;

public class CustomerProjectService {
	
	private Connection conn = null;
	
	public CustomerProjectService(Connection conn){
		this.conn = conn;
	}
	
	public boolean add(CustomerProject cp){
		boolean result = false;
		PreparedStatement ps = null;
		try{
			String sql="INSERT INTO `asdb`.`k_customerproject`" +
						"(`customerId`,`customerName`,`customerRank`,`customerSource`,`businessType`," +
						"`properties`,`projectType`,`contractMoney`,`workingHours`,`admissionTime`," +
						"`distributeUser`,`followUser`,`auditUser`,`state`,`createUser`," +
						"`createDate`,`createDepartment`) " +
						"values (?,?,?,?,?," +
								"?,?,?,?,?," +
								"?,?,?,?,?,now()," +
								"?)";
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++,cp.getCustomerId());
			ps.setString(i++,cp.getCustomerName());
			ps.setString(i++,cp.getCustomerRank());
			ps.setString(i++,cp.getCustomerSource());
			ps.setString(i++,cp.getBusinessType());
			
			ps.setString(i++,cp.getProperties());
			ps.setString(i++,cp.getProjectType());
			ps.setString(i++,cp.getContractMoney());
			ps.setString(i++,cp.getWorkingHours());
			ps.setString(i++,cp.getAdmissionTime());
			
			ps.setString(i++,cp.getDistributeUser());
			ps.setString(i++,cp.getFollowUser());
			ps.setString(i++,cp.getAuditUser());
			ps.setString(i++,cp.getState());
			ps.setString(i++,cp.getCreateUser());
			
			ps.setString(i++,cp.getCreateDepartment());
			ps.execute();
			
			result = true;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return result;

	}
	
	public boolean update(CustomerProject cp){
		boolean result = false;
		PreparedStatement ps = null;
		  try{
			  String sql ="UPDATE `asdb`.`k_customerproject`" +
			  			  "SET" +
			  			  "`customerId` = ?," +
			  			  "`customerName` = ?," +
			  			  "`customerRank` = ?," +
			  			  "`customerSource` = ?," +
			  			  "`businessType` = ?," +
			  			  "`properties` = ?," +
			  			  "`projectType` = ?," +
			  			  "`contractMoney` = ?," +
			  			  "`workingHours` = ?," +
			  			  "`admissionTime` = ?," +
			  			  "`distributeUser` = ?," +
			  			  "`followUser` = ?," +
			  			  "`auditUser` = ?" +
			  			  " where autoId=?";
			  
			  ps = conn.prepareStatement(sql);
			  int i = 1;
			  ps.setString(i++,cp.getCustomerId());
			  ps.setString(i++,cp.getCustomerName());
			  ps.setString(i++,cp.getCustomerRank());
			  ps.setString(i++,cp.getCustomerSource());
			  ps.setString(i++,cp.getBusinessType());
			
			  ps.setString(i++,cp.getProperties());
			  ps.setString(i++,cp.getProjectType());
			  ps.setString(i++,cp.getContractMoney());
			  ps.setString(i++,cp.getWorkingHours());
			  ps.setString(i++,cp.getAdmissionTime());
			
			  ps.setString(i++,cp.getDistributeUser());
			  ps.setString(i++,cp.getFollowUser());
			  ps.setString(i++,cp.getAuditUser());
			  
			  ps.setString(i++,cp.getAutoId());
			  ps.execute();
			  result = true;
		  }catch(Exception e){
			  e.printStackTrace();
		  }finally{
			  DbUtil.close(ps);
		  }
		  return result;
		  
	} 
	
	public CustomerProject getCustomerProject(String autoId){
		  
		  CustomerProject cp = new CustomerProject();
		  PreparedStatement ps = null;
		  ResultSet rs = null;
		  try{
			  String sql ="SELECT`customerId`,`customerName`,`customerRank`,`customerSource`,`businessType`," +
			  			  "`properties`,`projectType`,`contractMoney`,`workingHours`,`admissionTime`," +
			  			  "`distributeUser`,`followUser`,`auditUser`,`state`,`createUser`," +
			  			  "`createDate`,`createDepartment`,reportfilename,reportfiletempname " +
			  			  " FROM `asdb`.`k_customerproject` where autoId="+autoId;
			  ps = conn.prepareStatement(sql);
			  rs = ps.executeQuery();
			  if(rs.next()){
				  
				  cp.setAutoId(autoId);
				  
				  cp.setCustomerId(rs.getString("customerId"));
				  cp.setCustomerName(rs.getString("customerName"));
				  cp.setCustomerRank(rs.getString("customerRank"));
				  cp.setCustomerSource(rs.getString("customerSource"));
				  cp.setBusinessType(rs.getString("businessType"));
				  
				  cp.setProperties(rs.getString("properties"));
				  cp.setProjectType(rs.getString("projectType"));
				  cp.setContractMoney(rs.getString("contractMoney"));
				  cp.setWorkingHours(rs.getString("workingHours"));
				  cp.setAdmissionTime(rs.getString("admissionTime"));
				  
				  cp.setDistributeUser(rs.getString("distributeUser"));
				  cp.setFollowUser(rs.getString("followUser"));
				  cp.setAuditUser(rs.getString("auditUser"));
				  cp.setState(rs.getString("state"));
				  cp.setCreateUser(rs.getString("createUser"));
				  
				  cp.setCreateDate(rs.getString("createDate"));
				  cp.setCreateDepartment(rs.getString("createDepartment"));
				  // 增加业务约定书上传
				  cp.setReportFileName(rs.getString("reportfilename"));
				  cp.setReportFileTempName(rs.getString("reportfiletempname"));
				  cp.setFilename(rs.getString("filename"));
				  cp.setFiletempname(rs.getString("filetempname"));
				  
			  }
		  }catch(Exception e){
			  e.printStackTrace();
		  }finally{
			  DbUtil.close(ps);
			  DbUtil.close(rs);
		  }
		  return cp;
	  }

	public boolean delete(String autoId){
		PreparedStatement ps = null;
		boolean result = false;
		try{
			String sql = "delete from k_customerproject where autoId="+autoId;
			ps = conn.prepareStatement(sql);
			ps.execute();
			result = true;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return result;
	}
	
	public CustomerProject getCustomerProjectByautoId(String autoId){
		  
		  CustomerProject cp = new CustomerProject();
		  PreparedStatement ps = null;
		  ResultSet rs = null;
		  try{
			  String sql =" select * from oa_business where autoId="+autoId;
			  ps = conn.prepareStatement(sql);
			  rs = ps.executeQuery();
			  if(rs.next()){
				  
				 // cp.setAutoId(autoId);
				  cp.setCustomerName(rs.getString("customername"));
				  cp.setCustomerId(rs.getString("customerId"));
				  cp.setCustomerRank(rs.getString("customerlevel"));
				  cp.setCustomerSource(rs.getString("source"));
				  cp.setBusinessType(rs.getString("indistry"));
				  
				  cp.setProperties(rs.getString("companyType"));
				  cp.setProjectType(rs.getString("demandType"));
				  
 				  
				  cp.setDistributeUser(rs.getString("distriman"));
				  cp.setFollowUser(rs.getString("follow"));
				  
			  }
		  }catch(Exception e){
			  e.printStackTrace();
		  }finally{
			  DbUtil.close(ps);
			  DbUtil.close(rs);
		  }
		  return cp;
	  }
}
