package com.matech.audit.service.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.customer.model.BusinessChannel;
import com.matech.audit.service.customer.model.CustomerAgree;
import com.matech.framework.pub.db.DbUtil;

public class BusinessChannelService {

	private Connection conn = null;
	
	public BusinessChannelService(Connection conn1) 
			throws Exception {
		this.conn = conn1;
	}
	//安联修改（客户满意度调查修改）
	public void addCustomerAgree(CustomerAgree ca){
		PreparedStatement ps = null;
		String sql = "insert into k_customeragree (years,customername,customerlevel,profession,service,conversation,zhiyedaode,total,remark)"+
					"values(?,?,?,?,?, ?,?,?,?)";
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, ca.getYears());
			ps.setString(2, ca.getCustomername());
			ps.setString(3, ca.getCustomerlevel());
			ps.setString(4, ca.getProfession());
			ps.setString(5, ca.getService());
			ps.setString(6, ca.getConversation());
			ps.setString(7, ca.getZhiyedaode());
			ps.setString(8, ca.getTotal());
			ps.setString(9, ca.getRemark());
			ps.execute();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			  DbUtil.close(conn);
		}
	}
	public void save(BusinessChannel businessChannel){
		 PreparedStatement ps = null;
		 String sql="insert into k_businesschanel (company,channel,ifpartner,memberCount,linkman,linkrank," +
		 		"phone,email,QQorMSN,headuser,headrank,headphone,manager,memo,createUser,createDepartment,createTime)" +
		 		"values(?,?,?,?,? ,?,?,?,?,?, ?,?,?,?,?, ?,?) ";
		  try{
			  ps = conn.prepareStatement(sql);
			  ps.setString(1, businessChannel.getCompany());
			  ps.setString(2, businessChannel.getChannel());
			  ps.setString(3, businessChannel.getIfpartner());
			  ps.setString(4, businessChannel.getMenberCount());
			  ps.setString(5, businessChannel.getLinkMan());
			  ps.setString(6, businessChannel.getLinkrank());
			  ps.setString(7, businessChannel.getPhone());
			  ps.setString(8, businessChannel.getEmail());
			  ps.setString(9, businessChannel.getQQorMSN());
			  ps.setString(10,businessChannel.getHeadUser());
			  ps.setString(11, businessChannel.getHeadrank());
			  ps.setString(12, businessChannel.getHeadphone());
			  ps.setString(13, businessChannel.getManager());
			  ps.setString(14, businessChannel.getMemo());
			  ps.setString(15,businessChannel.getCreateUser());
			  ps.setString(16, businessChannel.getCreateDepartment());
			  ps.setString(17, businessChannel.getCreatTime());
			  ps.execute();
		  }catch(Exception e){
			  e.printStackTrace();
		  }finally{
			  DbUtil.close(conn);
		  }
		 
	}
	public BusinessChannel getBusinessChannel(String id){
		PreparedStatement ps = null;
		ResultSet rs = null;
		BusinessChannel businessChannel = new BusinessChannel();
		try{
			String sql="SELECT autoId,company,channel,ifpartner,memberCount,LinkMan,linkrank,PHONE,EMAIL,QQorMSN,headuser," +
					"headrank,headphone,manager,Memo FROM k_businesschanel where autoId="+id;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				businessChannel.setAutoId(rs.getString("autoId"));
				businessChannel.setCompany(rs.getString("company"));
				businessChannel.setChannel(rs.getString("channel"));
				businessChannel.setIfpartner(rs.getString("ifpartner"));
				businessChannel.setMenberCount(rs.getString("memberCount"));
				businessChannel.setLinkMan(rs.getString("LinkMan"));
				businessChannel.setLinkrank(rs.getString("linkrank"));
				businessChannel.setPhone(rs.getString("PHONE"));
				businessChannel.setEmail(rs.getString("EMAIL"));
				businessChannel.setQQorMSN(rs.getString("QQorMSN"));
				businessChannel.setHeadUser(rs.getString("headuser"));
				businessChannel.setHeadrank(rs.getString("headrank"));
				businessChannel.setHeadphone(rs.getString("headphone"));
				businessChannel.setManager(rs.getString("manager"));
				businessChannel.setMemo(rs.getString("memo"));
				
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return businessChannel;
	}
	
	public void update(BusinessChannel businessChannel){
		PreparedStatement ps = null;
		try{
			String sql="update k_businesschanel set company=?,channel=?,ifpartner=?,memberCount=?,LinkMan=?,linkrank=?,PHONE=?," +
					"EMAIL=?,QQorMSN=?,headuser=?,headrank=?,headphone=?,manager=?,Memo=? where autoId=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, businessChannel.getCompany());
			ps.setString(2, businessChannel.getChannel());
			ps.setString(3, businessChannel.getIfpartner());
			ps.setString(4, businessChannel.getMenberCount());
			ps.setString(5, businessChannel.getLinkMan());
			ps.setString(6, businessChannel.getLinkrank());
			ps.setString(7, businessChannel.getPhone());
			ps.setString(8, businessChannel.getEmail());
			ps.setString(9, businessChannel.getQQorMSN());
			ps.setString(10,businessChannel.getHeadUser());
			ps.setString(11,businessChannel.getHeadrank());
			ps.setString(12,businessChannel.getHeadphone());
			ps.setString(13,businessChannel.getManager());
			ps.setString(14, businessChannel.getMemo());
			ps.setString(15,businessChannel.getAutoId());
			ps.execute();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	
	public void delete(String autoId){
		 PreparedStatement ps = null;
		 try{
			 String sql="delete from k_businesschanel where autoId="+autoId;
			 ps = conn.prepareStatement(sql);
			 ps.execute();
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 DbUtil.close(ps);
		 }
		
	}
	public void deleteAgree(String autoId){
		PreparedStatement ps = null;
		 try{
			 String sql="delete from `k_customeragree` where autoId="+autoId;
			 ps = conn.prepareStatement(sql);
			 ps.execute();
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 DbUtil.close(ps);
		 }
		
	}
	
	
}
