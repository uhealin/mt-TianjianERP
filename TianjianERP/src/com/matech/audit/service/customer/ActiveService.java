package com.matech.audit.service.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.matech.audit.service.customer.model.Active;
import com.matech.audit.service.customer.model.ActiveCompany;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;

public class ActiveService {
	private Connection conn = null;

	public ActiveService(Connection conn1) 
			throws Exception {
		this.conn = conn1;
	}
	
	public void save(Active active){
		PreparedStatement ps = null;
		try{
			String sql="insert into k_active (activeName,activeType,startdate,enddate,address,partners,createUser,createDepartment,createTime) " +
					"values (?,?,?,?,?, ?,?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, active.getActiveName());
			ps.setString(2, active.getActiveType());
			ps.setString(3, active.getStartDate());
			ps.setString(4, active.getEndDate());
			ps.setString(5, active.getAddress());
			ps.setString(6, active.getPartners());
			ps.setString(7, active.getCreateUser());
			ps.setString(8, active.getCreateDepartmentId());
			ps.setString(9, active.getCreateTime());
			ps.execute();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	
	public void update(Active active){
		PreparedStatement ps = null;
		try{
			String sql="update k_active set activeName=?,activeType=?,startdate=?,enddate=?,address=?,partners=? where autoId=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, active.getActiveName());
			ps.setString(2, active.getActiveType());
			ps.setString(3, active.getStartDate());
			ps.setString(4, active.getEndDate());
			ps.setString(5, active.getAddress());
			ps.setString(6, active.getPartners());
			ps.setString(7, active.getAutoId());
			ps.execute();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		
	}
	
	public Active getActive(String autoId){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Active active = new Active();
		try{
			String sql="SELECT autoId,activeName,activeType,startDate,EndDate,Address,partners FROM k_active where autoId="+autoId;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				active.setAutoId(rs.getString("autoId"));
				active.setActiveName(rs.getString("activeName"));
				active.setActiveType(rs.getString("activeType"));
				active.setStartDate(rs.getString("startDate"));
				active.setEndDate(rs.getString("EndDate"));
				active.setAddress(rs.getString("Address"));
				active.setPartners(rs.getString("partners"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			DbUtil.close(rs);
			
		}
		return active;
	}
	
	public void delete(String autoId){
		 PreparedStatement ps = null;
		 try{
			 String sql="delete from k_active where autoId="+autoId;
			 ps = conn.prepareStatement(sql);
			 ps.execute();
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 DbUtil.close(ps);
		 }
	}
	
	public void saveCompany(ActiveCompany activeCompany){
		PreparedStatement ps = null;
		try{
			String sql="insert into k_activecompany (company,active,custlevel,custsource,indistry,companytype,demandtype,demand" +
					",linkman,linkrank,linkphone,email,QQorMSN,memo) values(?,?,?,?,?, ?,?,?,?,?, ?,?,?,? )";
			ps = conn.prepareStatement(sql);
			ps.setString(1, activeCompany.getCompany());
			ps.setString(2, activeCompany.getActive());
			ps.setString(3, activeCompany.getCustLevel());
			ps.setString(4, activeCompany.getCustSource());
			ps.setString(5, activeCompany.getIndistry());
			ps.setString(6, activeCompany.getCompanytype());
			ps.setString(7, activeCompany.getDemandtype());
			ps.setString(8, activeCompany.getDemand());
			ps.setString(9, activeCompany.getLinkman());
			ps.setString(10, activeCompany.getLinkrank());
			ps.setString(11, activeCompany.getLinkphone());
			ps.setString(12, activeCompany.getEmail());
			ps.setString(13, activeCompany.getQQorMSN());
			ps.setString(14, activeCompany.getMemo());
			ps.execute();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	
	public ActiveCompany getCompany(String autoId){
		ActiveCompany activeCompany = new ActiveCompany();
		ASFuntion asf = new ASFuntion();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			String sql="select autoId,company,active,custlevel,custsource,indistry,companytype,demandtype,demand,linkman,linkrank," +
			   "linkphone,email,QQorMSN,memo from k_activecompany where autoId="+autoId;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				activeCompany.setAutoId(rs.getString("autoId"));
				activeCompany.setCompany(rs.getString("company"));
				activeCompany.setActive(rs.getString("active"));
				activeCompany.setCustLevel(rs.getString("custlevel"));
				activeCompany.setCustSource(rs.getString("custsource"));
				activeCompany.setIndistry(rs.getString("indistry"));
				activeCompany.setCompanytype(rs.getString("companytype"));
				activeCompany.setDemandtype(rs.getString("demandtype"));
				activeCompany.setDemand(rs.getString("demand"));
				activeCompany.setLinkman(rs.getString("linkman"));
				activeCompany.setLinkrank(rs.getString("linkrank"));
				activeCompany.setLinkphone(rs.getString("linkphone"));
				activeCompany.setEmail(rs.getString("email"));
				activeCompany.setQQorMSN(rs.getString("QQorMSN"));
				activeCompany.setMemo(rs.getString("memo"));
				ps.execute();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return activeCompany;
	}
	
	public void updateCompany(ActiveCompany activeCompany){
		  PreparedStatement ps = null;
		  try{
			  String sql="update k_activecompany set company=?,active=?,custlevel=?,custsource=?,indistry=?,companytype=?,demandtype=?," +
			  		"demand=?,linkman=?,linkrank=?,linkphone=?,email=?,QQorMSN=?,memo=? where autoId=?";
			  ps = conn.prepareStatement(sql);
			  ps.setString(1, activeCompany.getCompany());
			  ps.setString(2, activeCompany.getActive());
			  ps.setString(3, activeCompany.getCustLevel());
			  ps.setString(4, activeCompany.getCustSource());
			  ps.setString(5, activeCompany.getIndistry());
			  ps.setString(6, activeCompany.getCompanytype());
			  ps.setString(7, activeCompany.getDemandtype());
			  ps.setString(8, activeCompany.getDemand());
			  ps.setString(9, activeCompany.getLinkman());
			  ps.setString(10,activeCompany.getLinkrank());
			  ps.setString(11, activeCompany.getLinkphone());
			  ps.setString(12, activeCompany.getEmail());
			  ps.setString(13, activeCompany.getQQorMSN());
			  ps.setString(14, activeCompany.getMemo());
			  ps.setString(15, activeCompany.getAutoId());
			  ps.execute();
		  }catch(Exception e){
			  e.printStackTrace();
		  }finally{
			  DbUtil.close(ps);
		  }
	}
	
	public void deleteCompany(String autoId){
		PreparedStatement ps = null;
		try{
			String sql ="delete from k_activecompany where autoId="+autoId;
			ps = conn.prepareStatement(sql);
			ps.execute();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
   
	public void newTable() throws MatechException{
		delTable();
		PreparedStatement ps = null;
		try{
			String sql ="create table tt_k_activecompany like k_activecompany";
			ps = conn.prepareStatement(sql);
			ps.execute();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	public void delTable() throws MatechException {
		String sql = "DROP TABLE  IF EXISTS tt_k_activecompany";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
		} finally {
			DbUtil.close(ps);
		}

	}
	public void updateDate(String userId,String departmentId,String createTime){
		PreparedStatement ps = null;
		try{
			String sql="update tt_k_activecompany a ,k_active b set a.active=b.autoId,a.createUser ='"+userId+"'," +
					"a.createDepartment ='"+departmentId+"',a.createTime='"+createTime+"' where a.active=b.activeName";
			ps = conn.prepareStatement(sql);
			ps.execute();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	
	public void insertData() throws MatechException{
		 PreparedStatement ps = null;
		 try{
			 String sql ="INSERT INTO k_activecompany ("+
						 "  company,active,custlevel,custsource,indistry,companytype," +
						"  demandtype,demand,linkman,linkrank,linkphone,email,QQorMSN,memo,createUser,createDepartment,createTime"+
						" ) SELECT a.company,a.active,a.custlevel,a.custsource,a.indistry,a.companytype,"+
						"  a.demandtype,a.demand,a.linkman,a.linkrank,a.linkphone,a.email,a.QQorMSN,a.memo,a.createUser,a.createDepartment,a.createTime  "+ 
						" FROM tt_k_activecompany a \n" +
						"INNER JOIN k_dic b ON a.custsource = b.value AND b.ctype='客户来源' \n"+
						"INNER JOIN k_dic c ON a.custlevel = c.name  AND c.ctype='客户级别' \n"+
						"INNER JOIN k_active d ON a.active = d.autoId \n" ;
			System.out.println("sssssssssssssss="+sql);
			 ps = conn.prepareStatement(sql);
			 ps.execute();
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 DbUtil.close(ps);
		 }
		 
	}
	
	/*//同时插入到oa_oa_business表单中
	public void insertOA_buseness (){
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			//select a.autoid,a.customername,a.customerlevel,a.source,a.indistry,a.companyType,a.demandType,a.demand,a.contact,a.rank,a.contactway" +
	   		//",a.email,a.QQorMSN,b.name as distriman,c.name as follow from oa_business a left join k_user b on a.distriman = b.Id" +
	   		//" left join k_user c on a.follow= c.Id "
			String sql ="INSERT INTO oa_business("+
			"  customername,customerlevel,source,indistry,companyType," +
			"  demandType,demand,contact,rank,contactway,email,QQorMSN"+
			" ) SELECT a.company,a.custlevel,a.custsource,a.indistry,a.companytype,"+
			"  a.demandtype,a.demand,a.linkman,a.linkrank,a.linkphone,a.email,a.QQorMSN"+ 
			" FROM tt_k_activecompany a \n" +
			"INNER JOIN k_dic b ON a.custsource = b.value AND b.ctype='客户来源' \n"+
			"INNER JOIN k_dic c ON a.custlevel = c.name  AND c.ctype='客户级别' \n";
		  ps = conn.prepareStatement(sql);
		  ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		 }finally{
			 DbUtil.close(ps);
		 }
	}
	//要匹配的表单数据
	*/
	
	
	public List<ActiveCompany> getListCompany(){
		
		ASFuntion asf = new ASFuntion();
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<ActiveCompany> listCompany = null;
		try{
			String sql="select autoId,company,active,custlevel,custsource,indistry,companytype,demandtype,demand,linkman,linkrank," +
			   "linkphone,email,QQorMSN,memo from tt_k_activecompany ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				ActiveCompany activeCompany = new ActiveCompany();
				
				activeCompany.setAutoId(rs.getString("autoId"));
				activeCompany.setCompany(rs.getString("company"));
				activeCompany.setActive(rs.getString("active"));
				activeCompany.setCustLevel(rs.getString("custlevel"));
				activeCompany.setCustSource(rs.getString("custsource"));
				activeCompany.setIndistry(rs.getString("indistry"));
				activeCompany.setCompanytype(rs.getString("companytype"));
				activeCompany.setDemandtype(rs.getString("demandtype"));
				activeCompany.setDemand(rs.getString("demand"));
				activeCompany.setLinkman(rs.getString("linkman"));
				activeCompany.setLinkrank(rs.getString("linkrank"));
				activeCompany.setLinkphone(rs.getString("linkphone"));
				activeCompany.setEmail(rs.getString("email"));
				activeCompany.setQQorMSN(rs.getString("QQorMSN"));
				activeCompany.setMemo(rs.getString("memo"));
				//ps.execute();
				if(listCompany == null){
					listCompany = new ArrayList<ActiveCompany>();
				}
				listCompany.add(activeCompany);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return listCompany;
	}
	//批量删除
	public void delAPlacard(String id){
		 PreparedStatement ps = null;
		 try{
			 String sql="delete from k_activecompany where autoId="+id;
			 ps = conn.prepareStatement(sql);
			 ps.execute();
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 DbUtil.close(ps);
		 }
	}
	/*public void delAPlacard(String id) throws Exception{
		PreparedStatement ps = null;
		 try{
			 String sql="delete from k_activecompany  where autoId="+id;
			 ps = conn.prepareStatement(sql);
			 ps.execute();
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 DbUtil.close(ps);
		 }
	}*/
}








