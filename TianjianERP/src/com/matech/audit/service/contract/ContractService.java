package com.matech.audit.service.contract;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.matech.audit.service.contract.model.Contract;
import com.matech.framework.pub.db.DbUtil;

public class ContractService {
	private Connection conn= null;
	
	public ContractService(Connection conn){
		
		this.conn =conn;
	}
    
	//增加	
	public boolean addcontract(Contract contract) throws Exception{
		DbUtil.checkConn(conn);	
		PreparedStatement ps = null;
				
		try {
			
			//系统当前时间
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String nowtime = sdf.format(new Date());
			
			String sql= "insert into oa_contract " +
					"(bargainid,armour,second,bargainmoney,paymentfashion,curname,sharelist," +
					     "estate,cmemo,draftout,bargainterm,linkman,endamendtime,startuptime,startuppeople,blankouttime,blankoutpeople,customerid,contractname,contractfile,property,projects,departmentid) " +
					     "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, contract.getBargainid());
			ps.setString(2, contract.getArmour());
			ps.setString(3, contract.getSecond());
			ps.setString(4, contract.getBargainmoney());
			ps.setString(5, contract.getPaymentfashion());			
			ps.setString(6,contract.getCurname());	
			ps.setString(7, contract.getSharelist());
			ps.setString(8, contract.getEstate());
			ps.setString(9, contract.getCmemo());
			ps.setString(10, contract.getDraftout());		
			ps.setString(11, contract.getBargainterm());			
			ps.setString(12,contract.getLinkman());			
			ps.setString(13,nowtime );
			ps.setString(14, contract.getStartuptime());
			ps.setString(15, contract.getStartuppeople());
			ps.setString(16, contract.getBlankoutpeople());
			ps.setString(17, contract.getBlankouttime());
			ps.setString(18, contract.getCustomerid());
			ps.setString(19, contract.getContractname());
			ps.setString(20, contract.getContractfile());
			ps.setString(21, contract.getProperty());
			
			ps.setString(22, contract.getProjects());
			
			ps.setString(23, contract.getDepartmentid());//归属部门
			ps.execute();
					
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();						
		}finally{			
			DbUtil.close(ps);
		}
		
		return false;
	}
	
	
	//启用
	
	public boolean startcontract(String autoid,Contract contract) throws Exception{
		
		DbUtil.checkConn(conn);	
		PreparedStatement ps = null;
		
		try {
			
			String sql ="update oa_contract set estate=?, startuptime=?, startuppeople=? where autoid=? ";
			
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, contract.getEstate());		
			ps.setString(2, contract.getStartuptime());
			ps.setString(3, contract.getStartuppeople());		
			ps.setString(4, autoid);
			
			ps.execute();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			DbUtil.close(ps);
		}
				
		return false;
	}
	
	//作废
	
	public boolean blankoutcontract(String autoid,Contract contract) throws Exception{
		
		DbUtil.checkConn(conn);	
		PreparedStatement ps = null;
		
		try {
			
			String sql ="update oa_contract set estate=? ,blankouttime =?, blankoutpeople=? where autoid=? ";
		
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, contract.getEstate());
			ps.setString(2, contract.getBlankouttime());
			ps.setString(3, contract.getBlankoutpeople());			
			ps.setString(4, autoid);
			
			ps.execute();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			DbUtil.close(ps);
		}
			
		return false;
	}

	public String haveestate(String autoid) throws Exception {

		DbUtil.checkConn(conn);
	
		PreparedStatement ps = null;
		ResultSet rs = null;
	
		try {		
			    String sql = "select estate from oa_contract where autoid="+autoid;
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
							
				String estate = "";
				while(rs.next()){
					estate = rs.getString(1);
				}	
				
				return estate;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("执行更新失败", e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		} 
	}
	
	public String haveestate(String autoid,String test) throws Exception {

		DbUtil.checkConn(conn);
	
		PreparedStatement ps = null;
		ResultSet rs = null;
	
		try {		
			    String sql = "select examiner from oa_customerlevel where recordtime='"+autoid+"'";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
							
				String estate = "";
				while(rs.next()){
					estate = rs.getString(1);
				}	
				
				return estate;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("执行更新失败", e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		} 
	}
	
	public void addBargain(String contractid) throws Exception {
		DbUtil.checkConn(conn);
		
		PreparedStatement ps = null;
		String sql = "";
		try {
			sql = "insert into oa_bargainbalance(bargainid,firstparty,secondparty,plandate,planmoney,checkinname,checkintime) \n"
				+" select bargainid,armour,second,startuptime,bargainmoney,startuppeople,startuptime " 
				+" from oa_contract where bargainid='"+contractid+"' \n";
				
			ps = conn.prepareStatement(sql);
			ps.execute();
			
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		} finally {
			DbUtil.close(ps);
		}
	}
}
