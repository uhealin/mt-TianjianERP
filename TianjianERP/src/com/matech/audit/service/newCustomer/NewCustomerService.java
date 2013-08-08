package com.matech.audit.service.newCustomer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.matech.audit.service.newCustomer.model.NewCustomer;
import com.matech.framework.pub.db.DbUtil;

public class NewCustomerService {

	Connection conn = null;
	
	public NewCustomerService (Connection conn){
		
		this.conn = conn;
	}
	
	/**
	 * 新增
	 * @param newCustomer
	 * @return
	 */
	public boolean add(NewCustomer newCustomer){
		
		boolean result = false;	
		PreparedStatement ps = null;
		try {	
		
			String sql = "INSERT INTO `k_newcustomer` \n"
			           +" (`uuid`,`customerName`,`projestId`,`belongsIndustry`,\n" +
		           		"`client`,`runScope`,`province`,`city`,`projestPartner`, \n" +
		           		"`projestManager`,`businessNature`,`deadlineDate`,`signBook`, \n" +
		           		"`mainShareholder`,`mainExecutives`,`predecessorOffice`,`corporationCount`, \n" +
		           		"`content`,`oneBearUserId`,`twoBearUserId`,`customerSource`,`optQuality`, \n" +
		           		"`optDepartment`,`mobilePhone`,`phone`,`remark`,`createDate`,`createUser`,`property`,`state`)"
			           +" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),?,?,?);";
			
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, newCustomer.getUuid());
			ps.setString(i++, newCustomer.getCustomerName());
			ps.setString(i++, newCustomer.getProjestId());
			ps.setString(i++, newCustomer.getBelongsIndustry());
			ps.setString(i++, newCustomer.getClient());
			ps.setString(i++, newCustomer.getRunScope());
			ps.setString(i++, newCustomer.getProvince());
			ps.setString(i++, newCustomer.getCity());
			ps.setString(i++, newCustomer.getProjestPartner());
			ps.setString(i++, newCustomer.getProjestManager());
			ps.setString(i++, newCustomer.getBusinessNature());
			ps.setString(i++, newCustomer.getDeadlineDate());
			ps.setString(i++, newCustomer.getSignBook());
			ps.setString(i++, newCustomer.getMainShareholder());
			ps.setString(i++, newCustomer.getMainExecutives());
			ps.setString(i++, newCustomer.getPredecessorOffice());
			ps.setString(i++, newCustomer.getCorporationCount());
			ps.setString(i++, newCustomer.getContent());
			ps.setString(i++, newCustomer.getOneBearUserId());
			ps.setString(i++, newCustomer.getTwoBearUserId());
			ps.setString(i++, newCustomer.getCustomerSource());
			ps.setString(i++, newCustomer.getOptQuality());
			ps.setString(i++, newCustomer.getOptDepartment());
			ps.setString(i++, newCustomer.getMobilePhone());
			ps.setString(i++, newCustomer.getPhone());
			ps.setString(i++, newCustomer.getRemark());
			ps.setString(i++, newCustomer.getCreateUser());
			ps.setString(i++, newCustomer.getProperty());
			ps.setString(i++, newCustomer.getState());
			ps.execute();
			
			result = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps) ;
		}
		return result;
	}

	/**
	 * 得到客户信息
	 * @param uuid
	 * @return
	 */
	public NewCustomer getNewCustomer(String uuid){
		ResultSet rs = null;
		PreparedStatement ps = null;
		
		NewCustomer newCustomer = new NewCustomer();
		
		String sql = "SELECT `uuid`,`customerName`,`projestId`,`belongsIndustry`, \n"
					+"`client`,`runScope`,`province`,`city`,`projestPartner`, \n"
					+"`projestManager`,`businessNature`,`deadlineDate`,`signBook`, \n"
					+"`mainShareholder`,`mainExecutives`,`predecessorOffice`,`corporationCount`, \n"
					+"`content`,`oneBearUserId`,`twoBearUserId`,`customerSource`,`optQuality`, \n"
					+"`optDepartment`,`mobilePhone`,`phone`,`remark`,`createDate`,`createUser`,`property`,`state` \n"
					+"FROM `k_newcustomer` WHERE `uuid` = ?";
		
		try {
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, uuid);
			rs = ps.executeQuery() ;
			
			while (rs.next()) {
				
				newCustomer.setUuid(uuid);
				newCustomer.setCustomerName(rs.getString("customerName"));
				newCustomer.setProjestId(rs.getString("projestId"));
				newCustomer.setBelongsIndustry(rs.getString("belongsIndustry"));
				newCustomer.setClient(rs.getString("client"));
				newCustomer.setRunScope(rs.getString("runScope"));
				newCustomer.setProvince(rs.getString("province"));
				newCustomer.setCity(rs.getString("city"));
				newCustomer.setProjestPartner(rs.getString("projestPartner"));
				newCustomer.setProjestManager(rs.getString("projestManager"));
				newCustomer.setBusinessNature(rs.getString("businessNature"));
				newCustomer.setDeadlineDate(rs.getString("deadlineDate"));
				newCustomer.setSignBook(rs.getString("signBook"));
				newCustomer.setMainShareholder(rs.getString("mainShareholder"));
				newCustomer.setMainExecutives(rs.getString("mainExecutives"));
				newCustomer.setPredecessorOffice(rs.getString("predecessorOffice"));
				newCustomer.setCorporationCount(rs.getString("corporationCount"));
				newCustomer.setContent(rs.getString("content"));
				newCustomer.setOneBearUserId(rs.getString("oneBearUserId"));
				newCustomer.setTwoBearUserId(rs.getString("twoBearUserId"));
				newCustomer.setCustomerSource(rs.getString("customerSource"));
				newCustomer.setOptQuality(rs.getString("optQuality"));
				newCustomer.setOptDepartment(rs.getString("optDepartment"));
				newCustomer.setMobilePhone(rs.getString("mobilePhone"));
				newCustomer.setPhone(rs.getString("phone"));
				newCustomer.setRemark(rs.getString("remark"));
				newCustomer.setCreateDate(rs.getString("createDate"));
				newCustomer.setCreateUser(rs.getString("createUser"));
				newCustomer.setProperty(rs.getString("property"));
				newCustomer.setState(rs.getString("state"));
				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps) ;
			DbUtil.close(rs) ;
		}
		return newCustomer;
	}

	/**
	 * 修改
	 * @param newCustomer
	 * @return
	 */
	public boolean update(NewCustomer newCustomer){
		boolean result = false;	
		PreparedStatement ps = null;
		try {	
		
			String sql = "UPDATE `k_newcustomer` \n"
						+"SET  \n"
						+"  `customerName` = ?,`projestId` = ?,`belongsIndustry` = ?,`client` = ?,`runScope` = ?,`province` = ?,`city` = ?, \n"
						+"  `projestPartner` = ?,`projestManager` = ?,`businessNature` = ?,`deadlineDate` = ?,`signBook` = ?, \n"
						+"  `mainShareholder` = ?,`mainExecutives` = ?,`predecessorOffice` = ?,`corporationCount` = ?, \n"
						+"  `content` = ?,`oneBearUserId` = ?,`twoBearUserId` = ?,`customerSource` = ?,`optQuality` = ?,`optDepartment` =?, \n"
						+"  `mobilePhone` = ?,`phone` = ?,  `remark` = ?,`property` = ? \n"
						+"  WHERE `uuid` = ?";
			
			ps = conn.prepareStatement(sql);
			int i  = 1;
			ps.setString(i++, newCustomer.getCustomerName());
			ps.setString(i++, newCustomer.getProjestId());
			ps.setString(i++, newCustomer.getBelongsIndustry());
			ps.setString(i++, newCustomer.getClient());
			ps.setString(i++, newCustomer.getRunScope());
			ps.setString(i++, newCustomer.getProvince());
			ps.setString(i++, newCustomer.getCity());
			ps.setString(i++, newCustomer.getProjestPartner());
			ps.setString(i++, newCustomer.getProjestManager());
			ps.setString(i++, newCustomer.getBusinessNature());
			ps.setString(i++, newCustomer.getDeadlineDate());
			ps.setString(i++, newCustomer.getSignBook());
			ps.setString(i++, newCustomer.getMainShareholder());
			ps.setString(i++, newCustomer.getMainExecutives());
			ps.setString(i++, newCustomer.getPredecessorOffice());
			ps.setString(i++, newCustomer.getCorporationCount());
			ps.setString(i++, newCustomer.getContent());
			ps.setString(i++, newCustomer.getOneBearUserId());
			ps.setString(i++, newCustomer.getTwoBearUserId());
			ps.setString(i++, newCustomer.getCustomerSource());
			ps.setString(i++, newCustomer.getOptQuality());
			ps.setString(i++, newCustomer.getOptDepartment());
			ps.setString(i++, newCustomer.getMobilePhone());
			ps.setString(i++, newCustomer.getPhone());
			ps.setString(i++, newCustomer.getRemark());
			ps.setString(i++, newCustomer.getProperty());
			ps.setString(i++, newCustomer.getUuid());
			
			ps.execute();
			
			result = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps) ;
		}
		return result;
		
		 
	}
	
	/**
	 * 删除
	 * @param uuid
	 * @return
	 */
	public boolean del(String uuid){
		boolean result = false;	
		PreparedStatement ps = null;
		try {	
		
			String sql = "delete from `k_newcustomer` \n"
						+"  WHERE `uuid` = ?";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, uuid);
			
			ps.execute();
			
			result = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps) ;
		}
		return result;
		
		 
	}
	
	
	/**
	 * 查询是否存在此客户名
	 * @param customerName
	 * @return
	 */
	public String  getIfCustomerName(String customerName){
		
		String customer = "";
		PreparedStatement ps =null;
		ResultSet rs = null;
		
		//客户表
		String sql = "SELECT c.departname,b.name,b.mobilePhone,b.fixedPhone FROM k_customer a \n"
					+"LEFT JOIN k_manager b ON a.departid = b.customerId \n"
					+"LEFT JOIN k_department c ON a.departmentid = c.autoid \n"
					+"WHERE a.departName =? ";
		try {
			
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, customerName);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				
				customer += "该客户已经是“" + rs.getString(1)+"”的签约客户，客户联系人是：" +
							""+rs.getString(2)+"，手机："+rs.getString(3)+"，电话："+rs.getString(4)+"@`@";
				
			}
			//客户表为空时 找 承接表
			if("".equals(customer)){
				sql = "SELECT c.departname,IFNULL(d.name,e.name) AS linkmain,IFNULL(a.mobilePhone,a.phone) \n"
					  +"FROM `k_newcustomer` a \n"
					  +"LEFT JOIN k_user b ON a.createUser = b.id \n"
					  +"LEFT JOIN k_department c ON b.departmentid = c.autoid \n"
					  +"LEFT JOIN k_user d ON a.oneBearUserId = d.id \n"
					  +"LEFT JOIN k_user e ON a.twoBearUserId = e.id \n"
					  +"WHERE a.customerName = ?";
				ps = conn.prepareStatement(sql);
				ps.setString(1, customerName);
				
				rs = ps.executeQuery();
				
				while (rs.next()) {
					
					customer += "该客户已经是“" + rs.getString(1)+"”的签约客户，客户联系人是：" +
								""+rs.getString(2)+"，电话："+rs.getString(3)+"@`@";
					
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return customer;
	}
}
