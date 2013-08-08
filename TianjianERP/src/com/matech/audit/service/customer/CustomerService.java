package com.matech.audit.service.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.matech.audit.multidb.MultiDbIF;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.customer.model.Business;
import com.matech.audit.service.customer.model.Customer;
import com.matech.audit.service.customer.model.Follow;
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.sys.UTILSysProperty;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;

/**
 * <p>
 * Title: 客户管理和事务所机构信息管理
 * </p>
 * <p>
 * Description: 提供客户和事务所机构管理
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006, 2008 MaTech Corporation.All rights reserved.
 * </p>
 * <p>
 * Company: Matech 广州铭太信息科技有限公司
 * </p>
 *
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有， 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 *
 * 贡献者团队: 铭太科技 - 研发中心，审计开发组
 *
 * @author void 2007-6-12
 */
public class CustomerService {
	private Connection conn = null;

	/**
	 * 构造方法,获得数据库连接
	 *
	 * @param conn
	 * @throws Exception
	 */
	public CustomerService(Connection conn) throws Exception {
		DbUtil.checkConn(conn);
		this.conn = conn;
	}
	
	public String replaceFromKdic(String str) {
		String sql = " select name,value from k_dic where ctype='客户名称模糊匹配' ";
		ResultSet rs = null;
		PreparedStatement ps = null;
		
		str = str.replaceAll("\\s*", "").toLowerCase();
		
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			String name = "";
			String value = "";
			
			while(rs.next()) {
				name = rs.getString(1).toLowerCase();
				value = rs.getString(2).toLowerCase();
				
				if(!"".equals(name)) {
					str = str.replaceAll(name, value);
				}
				
				if(!"".equals(value)) {
					str = str.replaceAll(value, name);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return str;
	}
	
	/**
	 * 客户名称模糊匹配
	 * @param customerName
	 */
	public String checkCustomerName(String customerName) {
		
		ResultSet rs = null;
		PreparedStatement ps = null;
		String result = "";
		
		System.out.println("输入值：" + customerName);

		customerName = replaceFromKdic(customerName);
		
		try {
			
			String sql = " select departId,departname from k_customer where property='1' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			String departname = "";
			String departId = "";
			String tempDepartName = "";
			
			while(rs.next()) {
				departId = rs.getString(1);
				departname = rs.getString(2);
					
				tempDepartName = replaceFromKdic(departname);
				
				if(customerName.indexOf(tempDepartName) > -1) {
					result += "[" + departId + "]" + departname + ",";
				} else if(tempDepartName.indexOf(customerName) > -1) {
					result += "[" + departId + "]" + departname + ",";
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return result;
	}

	/**
	 * 返回客户总数
	 *
	 * @return int类型
	 * @throws Exception
	 */
	public int getCustomerCount() throws Exception {

		String strSql = "select count(*) from asdb.k_customer where Property = '1'";

		DbUtil db = new DbUtil(conn);
		return db.queryForInt(strSql);

	}

	/**
	 * 删除客户
	 *
	 * @param customerId
	 *            String
	 * @throws Exception
	 */
	public void remove(String customerId) throws Exception {
		String sql;
		try {
			if (customerId != null) {
				DbUtil db = new DbUtil(conn);

				// 删除前先判断该客户是否存在帐套
				sql = " SELECT count(*) from c_accpackage where customerid = ?";
				Object[] params = new Object[] { customerId };

				if (!db.queryForString(sql, params).equals("0")) {
					throw new Exception("该客户尚有相关帐套，不能删除。");
				}
				
				//删除前先判断该客户是否存在项目
				sql = " select count(*) from z_project where CustomerId= '"+customerId+"'";
				if (db.queryForInt(sql) > 0) {
					throw new Exception("该客户尚有相关项目，不能删除。");
				}

				// 删除前先判断该客户是否存在单位
				sql = " select count(*) from k_user where DepartID= '"+customerId+"'";
				if (db.queryForInt(sql) > 0) {
					throw new Exception("该单位尚有用户，不能删除。");
				}

				// 删除用户
				sql = "delete from k_customer where DepartID = ? ";
				db.execute(sql, params);

				// 删除k_key相关数据
				sql = "delete from k_key where DepartID = ? ";
				db.execute(sql, params);

				// 删除k_itemstat相关数据
				//params = new Object[] { "'" + customerId + "%'" };
				//sql = "delete from asdb.k_itemstat where accpackageid like ? ";
				//db.execute(sql, params);

				dropDatabase(customerId);	//删除数据库
				ManuFileService.deleteDirByCustomerID(customerId);	//删除底稿等

				sql = "use asdb";
				db.execute(sql);
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "删除客户出错", e);
			throw e;
		}
	}

	/**
	 * 新增客户
	 *
	 * @param customer
	 * @throws Exception
	 */
	public void addCustomer(Customer customer) throws Exception {
		PreparedStatement ps = null;
				
  String strSql = "insert into asdb.k_customer("
				+ " DepartID,DepartName,VocationID,Address,LinkMan,Phone,"
				+ " Email,Corporate,CountryCess,TerraCess,EnterpriseCode,DepartDate,"
				+ " LoginAddress,BusinessBegin,BusinessEnd,BusinessBound,Remark,Property,"
				+ " BPR,register, stockowner,fax,postalcode,taxpayer, "
				+ " standbyname,hylx,curname,departcode,recordtime,practitioner,"  
				+ " fashion, calling, estate,approach, mostly, subordination , "  
				+ " DepartEnName,groupname,departmentid,beforeName,customerShortName,iframework,"
				+ " plate,intro,parentName,holding,companyProperty,sMarket,"  
				+ " sockCode,sMarket2,sockCode2,customerIeve,webSite,projectState,state,iTmentName,"
				+ " agency,aStateDate,busineLicense,bstateDate,directorName,directorPhone,"  
				+ " dSecretary,secretaryPhone,ctaffQuantity,sAccountant,fDirector,accountanrPhone,"
				+ " fManager,fPhone,stockStartDate,stockListingDate,pOfficeAddress,cOfficeAddress," 
				
				+ " fbusineDate,ischange,`explain`,mergerQuantity,agoOffice,cReason,groupplate,"
				+ " nation,totalassets,totalcurname,vip"
				+ " ) values("
				+ " ?,?,?,?,?,?,?,?,?,?,?,?," 
				+ " ?,?,?,?,?,?,?,?,?,?,?,?,"
				+ " ?,?,?,?,?,?,?,?,?,?,?,?,"
				+ " ?,?,?,?,?,?,?,?,?,?,?,?,"
				+ " ?,?,?,?,?,?,?,?,?,?,?,?,"
				+ " ?,?,?,?,?,?,?,?,?,?,?,?,"
				+ " ?,?,?,?,?,?,?,?,?,?,?,?"
				+ " ,?)";
		System.out.print("==================="+strSql);

		try {
			int i=1;
			ps = conn.prepareStatement(strSql);

			ps.setString(i++, customer.getDepartId());
			ps.setString(i++, customer.getDepartName());
			ps.setString(i++, customer.getVocationId());
			ps.setString(i++, customer.getAddress());
			ps.setString(i++, customer.getLinkMan());
			ps.setString(i++, customer.getPhone());
			
			ps.setString(i++, customer.getEmail());
			ps.setString(i++, customer.getCorporate());
			ps.setString(i++, customer.getCountryCess());
			ps.setString(i++, customer.getTerraCess());
			ps.setString(i++, customer.getEnterpriseCode());
			ps.setString(i++, customer.getDepartDate());
			
			ps.setString(i++, customer.getLoginAddress());
			ps.setString(i++, customer.getBusinessBegin());
			ps.setString(i++, customer.getBusinessEnd());
			ps.setString(i++, customer.getBusinessBound());
			ps.setString(i++, customer.getRemark());
			ps.setString(i++, customer.getProperty());
			
			ps.setString(i++, customer.getBpr());
			ps.setString(i++, customer.getRegister());
			ps.setString(i++, customer.getStockowner());
			ps.setString(i++, customer.getFax());
			ps.setString(i++, customer.getPostalcode());
			ps.setString(i++, customer.getTaxpayer());
			
			ps.setString(i++, customer.getStandbyname());
			ps.setString(i++, customer.getHylx());
			ps.setString(i++, customer.getCurname());
			ps.setString(i++, customer.getCustdepartid());
			ps.setString(i++, customer.getSubordination());
			ps.setString(i++, customer.getPractitioner());
			
			ps.setString(i++, customer.getFashion());
			ps.setString(i++, customer.getCalling());
			ps.setString(i++, customer.getEstate());
			ps.setString(i++, customer.getApproach());
			ps.setString(i++, customer.getMostly());
			ps.setString(i++, customer.getSubordination());
			
			ps.setString(i++, customer.getDepartEnName());
			ps.setString(i++, customer.getGroupname());
			ps.setString(i++, customer.getDepartmentid());
			//beforeName,customerShortName,iframework,plate,intro,parentName,holding,companyProperty
			ps.setString(i++, customer.getBeforeName());
			ps.setString(i++, customer.getCustomerShortName());
			ps.setString(i++, customer.getIframework());
			
			ps.setString(i++, customer.getPlate());
			ps.setString(i++, customer.getIntro());
			ps.setString(i++, customer.getParentName());
			ps.setString(i++, customer.getHolding());
			ps.setString(i++, customer.getCompanyProperty());
			//后期所添加的字段
			ps.setString(i++, customer.getsMarket());
			
			ps.setString(i++, customer.getSockCode());
			ps.setString(i++, customer.getsMarket2());
			
			ps.setString(i++, customer.getSockCode2());
			ps.setString(i++, customer.getCustomerIeve());
			ps.setString(i++, customer.getWebSite());
			ps.setString(i++, customer.getProjectState());
			ps.setString(i++, customer.getState());
			//报备报告信息字段
			ps.setString(i++, customer.getiTmentName());
			
			ps.setString(i++, customer.getAgency());
			ps.setString(i++, customer.getaStateDate());
			ps.setString(i++, customer.getBusineLicense());
			ps.setString(i++, customer.getBstateDate());
			ps.setString(i++, customer.getDirectorName());
			ps.setString(i++, customer.getDirectorPhone());
			
			ps.setString(i++, customer.getdSecretary());
			ps.setString(i++, customer.getSecretaryPhone());
			ps.setString(i++, customer.getCtaffQuantity());
			ps.setString(i++, customer.getsAccountant());
			ps.setString(i++, customer.getfDirector());
			ps.setString(i++, customer.getAccountanrPhone());
			
			ps.setString(i++, customer.getfManager());
			ps.setString(i++, customer.getfPhone());
			ps.setString(i++, customer.getStockStartDate());
			ps.setString(i++, customer.getStockListingDate());
			ps.setString(i++, customer.getpOfficeAddress());
			ps.setString(i++, customer.getcOfficeAddress());
			
			ps.setString(i++, customer.getFbusineDate());
			ps.setString(i++, customer.getIschange());
			ps.setString(i++, customer.getExplain());
			ps.setString(i++, customer.getMergerQuantity());
			ps.setString(i++, customer.getAgoOffice());
			ps.setString(i++, customer.getcReason());
			ps.setString(i++, customer.getGroupplate());
			
			//2011-11-1
			ps.setString(i++, customer.getNation());
			ps.setString(i++, customer.getTotalassets());
			ps.setString(i++, customer.getTotalcurname());
			ps.setString(i++, customer.getVip());
			ps.execute();

			String departid = customer.getDepartId();
			if (!"555555".equals(departid)) {
				createDataBase(departid);
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "新增客户出错", e);
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 更新客户信息
	 *
	 * @param customer
	 * @throws Exception
	 */
	public void updateCustomer(Customer customer) throws Exception {

		PreparedStatement ps = null;

		String strSql = "update asdb.k_customer "
				+ " set DepartID=?, DepartName=?, VocationID=?, Address=?, LinkMan=?, "
				+ " Phone=?, Email=?, Corporate=?, CountryCess=?, TerraCess=?, "
				+ " EnterpriseCode=?, DepartDate=?, LoginAddress=?, BusinessBegin=?, BusinessEnd=?, "
				+ " BusinessBound=?, Remark=?, Property=?, BPR=?, register=?, "
				+ " stockowner=?, fax=?, postalcode=?, taxpayer=?, standbyname=?," 
				+ " hylx=?,curname=?,departcode=?,practitioner=?, fashion=?, " 
				+ " calling=?, estate=?, approach=?, mostly=?, subordination=?," 
				+ " DepartEnName=?,groupname=?,departmentid=?,beforeName=?,customerShortName=?,"
				+ " iframework=?,plate=?,intro=?,parentName=?,holding=?,"
                
				+ " companyProperty=?,sMarket=?,sockCode=?,sMarket2=?,sockCode2=?,customerIeve=?,webSite=?,"
                + " projectState=?,state=?,iTmentName=?,agency=?,aStateDate=?," 
                + " busineLicense=?,bstateDate=?,directorName=?,directorPhone=?,dSecretary=?,"
                + " secretaryPhone=?,ctaffQuantity=?,sAccountant=?,fDirector=?,accountanrPhone=?,"
                
                + " fManager=?,fPhone=?,stockStartDate=?,stockListingDate=?,pOfficeAddress=?,"
                + " cOfficeAddress=?,fbusineDate=?,ischange=?,`explain`=?,mergerQuantity=?,"
                + " agoOffice=?,cReason=?,groupplate=?,nation=?,totalassets=?,totalcurname=?,vip=? "
                
				+ " where DepartID = ? ";

		try {
			int i = 1;
			ps = conn.prepareStatement(strSql);

			ps.setString(i++, customer.getDepartId());
			ps.setString(i++, customer.getDepartName());
			ps.setString(i++, customer.getVocationId());
			ps.setString(i++, customer.getAddress());
			ps.setString(i++, customer.getLinkMan());

			ps.setString(i++, customer.getPhone());
			ps.setString(i++, customer.getEmail());
			ps.setString(i++, customer.getCorporate());
			ps.setString(i++, customer.getCountryCess());
			ps.setString(i++, customer.getTerraCess());

			ps.setString(i++, customer.getEnterpriseCode());
			ps.setString(i++, customer.getDepartDate());
			ps.setString(i++, customer.getLoginAddress());
			ps.setString(i++, customer.getBusinessBegin());
			ps.setString(i++, customer.getBusinessEnd());

			ps.setString(i++, customer.getBusinessBound());
			ps.setString(i++, customer.getRemark());
			ps.setString(i++, customer.getProperty());
			ps.setString(i++, customer.getBpr());
			ps.setString(i++, customer.getRegister());

			ps.setString(i++, customer.getStockowner());
			ps.setString(i++, customer.getFax());
			ps.setString(i++, customer.getPostalcode());
			ps.setString(i++, customer.getTaxpayer());
			ps.setString(i++, customer.getStandbyname());
			
			ps.setString(i++, customer.getHylx());
			ps.setString(i++, customer.getCurname());
			ps.setString(i++, customer.getCustdepartid());
			ps.setString(i++, customer.getPractitioner());
			ps.setString(i++, customer.getFashion());
			
			ps.setString(i++, customer.getCalling());
			ps.setString(i++, customer.getEstate());
			ps.setString(i++, customer.getApproach());
			ps.setString(i++, customer.getMostly());
			ps.setString(i++, customer.getSubordination());
			
			ps.setString(i++, customer.getDepartEnName()) ;
			ps.setString(i++, customer.getGroupname()) ;			
			ps.setString(i++, customer.getDepartmentid());		
			//beforeName,customerShortName,iframework,plate,intro,parentName,holding,companyProperty
			ps.setString(i++, customer.getBeforeName());
			ps.setString(i++, customer.getCustomerShortName());
			
			ps.setString(i++, customer.getIframework());
			ps.setString(i++, customer.getPlate());
			ps.setString(i++, customer.getIntro());
			ps.setString(i++, customer.getParentName());
			ps.setString(i++, customer.getHolding());
		
			ps.setString(i++, customer.getCompanyProperty());	
			 //后期添加字段
			ps.setString(i++, customer.getsMarket());
			ps.setString(i++, customer.getSockCode());
			ps.setString(i++, customer.getsMarket2());
			ps.setString(i++, customer.getSockCode2());
			ps.setString(i++, customer.getCustomerIeve());
			ps.setString(i++, customer.getWebSite());
			
			ps.setString(i++, customer.getProjectState());
			ps.setString(i++, customer.getState());  
			//报备报告信息字段
			ps.setString(i++, customer.getiTmentName());
			ps.setString(i++, customer.getAgency());
			ps.setString(i++, customer.getaStateDate());
			
			ps.setString(i++, customer.getBusineLicense());
			ps.setString(i++, customer.getBstateDate());
			ps.setString(i++, customer.getDirectorName());
			ps.setString(i++, customer.getDirectorPhone());
			ps.setString(i++, customer.getdSecretary());
		
			ps.setString(i++, customer.getSecretaryPhone());
			ps.setString(i++, customer.getCtaffQuantity());
			ps.setString(i++, customer.getsAccountant());
			ps.setString(i++, customer.getfDirector());
			ps.setString(i++, customer.getAccountanrPhone());
			
			ps.setString(i++, customer.getfManager());	
			ps.setString(i++, customer.getfPhone());
			ps.setString(i++, customer.getStockStartDate());
			ps.setString(i++, customer.getStockListingDate());
			ps.setString(i++, customer.getpOfficeAddress());
			
			ps.setString(i++, customer.getcOfficeAddress());
			ps.setString(i++, customer.getFbusineDate());
			ps.setString(i++, customer.getIschange());			
			ps.setString(i++, customer.getExplain());
			ps.setString(i++, customer.getMergerQuantity());
			
			ps.setString(i++, customer.getAgoOffice());
			ps.setString(i++, customer.getcReason()); 
			ps.setString(i++, customer.getGroupplate());
			
			//2011-11-1
			ps.setString(i++, customer.getNation());
			ps.setString(i++, customer.getTotalassets());
			ps.setString(i++, customer.getTotalcurname());
			ps.setString(i++, customer.getVip());
			ps.setString(i++, customer.getDepartId());

			ps.execute();
			ps.close();
			
			strSql="update z_project a join k_customer b "
				+"on a.customerid=b.departid and b.departid='"+customer.getDepartId()+"' set a.departmentid=b.departmentid";
			System.out.println("qwh="+strSql);
			ps = conn.prepareStatement(strSql);
			ps.execute();
			
			
			
			
		} catch (Exception e) {
			Debug.print(Debug.iError, "修改客户出错", e);
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 返回客户信息
	 *
	 * @param customerId
	 * @return
	 * @throws Exception
	 */
	public Customer getCustomer(String customerId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Customer customer = null;
		try {
			ASFuntion CHF = new ASFuntion();
			String strSql = "select * from asdb.k_customer where DepartID= ? ";

			ps = conn.prepareStatement(strSql);

			ps.setString(1, customerId);
			rs = ps.executeQuery();
			if (rs.next()) {
				customer = new Customer();

				customer.setDepartId(CHF.showNull(rs.getString("departid")));
				customer.setDepartName(CHF.showNull(rs.getString("departname")));
				customer.setVocationId(CHF.showNull(rs.getString("vocationid")));
				customer.setLinkMan(CHF.showNull(rs.getString("linkman")));
				customer.setPhone(CHF.showNull(rs.getString("phone")));

				customer.setEmail(CHF.showNull(rs.getString("email")));
				customer.setAddress(CHF.showNull(rs.getString("address")));
				customer.setCorporate(CHF.showNull(rs.getString("corporate")));
				customer.setCountryCess(CHF.showNull(rs.getString("countrycess")));
				customer.setTerraCess(CHF.showNull(rs.getString("terracess")));

				customer.setEnterpriseCode(CHF.showNull(rs.getString("enterprisecode")));
				customer.setDepartDate(CHF.showNull(rs.getString("departdate")));
				customer.setLoginAddress(CHF.showNull(rs.getString("loginaddress")));
				customer.setBusinessBegin(CHF.showNull(rs.getString("businessbegin")));
				customer.setBusinessEnd(CHF.showNull(rs.getString("businessend")));

				customer.setBusinessBound(CHF.showNull(rs.getString("businessbound")));
				customer.setRemark(CHF.showNull(rs.getString("remark")));
				customer.setProperty(CHF.showNull(rs.getString("property")));
				customer.setBpr(CHF.showNull(rs.getString("BPR")));
				customer.setRegister(CHF.showNull(rs.getString("register")));

				customer.setStockowner(CHF.showNull(rs.getString("stockowner")));
				customer.setPostalcode(CHF.showNull(rs.getString("postalcode")));
				customer.setFax(CHF.showNull(rs.getString("fax")));
				customer.setTaxpayer(CHF.showNull(rs.getString("taxpayer")));
				customer.setStandbyname(CHF.showNull(rs.getString("standbyname")));
				customer.setHylx(CHF.showNull(rs.getString("hylx")));
				customer.setCurname(CHF.showNull(rs.getString("curname")));
				customer.setCustdepartid(CHF.showNull(rs.getString("departcode")));
	
				
				customer.setPractitioner(CHF.showNull(rs.getString("practitioner")));
				customer.setFashion(CHF.showNull(rs.getString("fashion")));
				customer.setCalling(CHF.showNull(rs.getString("calling")));
				customer.setEstate(CHF.showNull(rs.getString("estate")));
				customer.setApproach(CHF.showNull(rs.getString("approach")));
				customer.setMostly(CHF.showNull(rs.getString("mostly")));
				customer.setSubordination(CHF.showNull(rs.getString("subordination")));
				customer.setDepartEnName(CHF.showNull(rs.getString("DepartEnName"))) ;
				customer.setGroupname(CHF.showNull(rs.getString("groupname"))) ;
				
				customer.setDepartmentid(CHF.showNull(rs.getString("departmentid"))) ;
				
				//beforeName,customerShortName,iframework,plate,intro,parentName,holding,companyProperty
				customer.setBeforeName(CHF.showNull(rs.getString("beforeName"))) ;
				customer.setCustomerShortName(CHF.showNull(rs.getString("customerShortName"))) ;
				customer.setIframework(CHF.showNull(rs.getString("iframework"))) ;
				customer.setPlate(CHF.showNull(rs.getString("plate"))) ;
				customer.setIntro(CHF.showNull(rs.getString("intro"))) ;
				customer.setParentName(CHF.showNull(rs.getString("parentName"))) ;
				customer.setHolding(CHF.showNull(rs.getString("holding"))) ;
				customer.setCompanyProperty(CHF.showNull(rs.getString("companyProperty"))) ;
				customer.setGroupplate(CHF.showNull(rs.getString("groupplate")));
				
				//后期添加字段
				customer.setsMarket(CHF.showNull(rs.getString("sMarket")));
				customer.setSockCode(CHF.showNull(rs.getString("sockCode")));
				customer.setSockCode2(CHF.showNull(rs.getString("sockCode2")));
				customer.setCustomerIeve(CHF.showNull(rs.getString("customerIeve")));
				customer.setWebSite(CHF.showNull(rs.getString("webSite")));
				customer.setProjectState(CHF.showNull(rs.getString("projectState")));
				customer.setState(CHF.showNull(rs.getString("state")));
				
				//报备报告信息字段
				customer.setiTmentName(CHF.showNull(rs.getString("iTmentName")));
				customer.setAgency(CHF.showNull(rs.getString("agency")));
				customer.setaStateDate(CHF.showNull(rs.getString("aStateDate")));
				customer.setBusineLicense(CHF.showNull(rs.getString("busineLicense")));
				customer.setBstateDate(CHF.showNull(rs.getString("bstateDate")));
				customer.setDirectorName(CHF.showNull(rs.getString("directorName")));
				
				customer.setDirectorPhone(CHF.showNull(rs.getString("directorPhone")));
				customer.setdSecretary(CHF.showNull(rs.getString("dSecretary")));
				customer.setSecretaryPhone(CHF.showNull(rs.getString("secretaryPhone")));
				customer.setCtaffQuantity(CHF.showNull(rs.getString("ctaffQuantity")));
				customer.setsAccountant(CHF.showNull(rs.getString("sAccountant")));
				customer.setfDirector(CHF.showNull(rs.getString("fDirector")));
				
				customer.setAccountanrPhone(CHF.showNull(rs.getString("accountanrPhone")));
				customer.setfManager(CHF.showNull(rs.getString("fManager")));
				customer.setfPhone(CHF.showNull(rs.getString("fPhone")));
				customer.setStockStartDate(CHF.showNull(rs.getString("stockStartDate")));
				customer.setStockListingDate(CHF.showNull(rs.getString("stockListingDate")));
				customer.setpOfficeAddress(CHF.showNull(rs.getString("pOfficeAddress")));
				
				customer.setcOfficeAddress(CHF.showNull(rs.getString("cOfficeAddress")));
				customer.setFbusineDate(CHF.showNull(rs.getString("fbusineDate")));
				customer.setIschange(CHF.showNull(rs.getString("ischange")));
				customer.setExplain(CHF.showNull(rs.getString("explain")));
				customer.setMergerQuantity(CHF.showNull(rs.getString("mergerQuantity")));
				customer.setAgoOffice(CHF.showNull(rs.getString("agoOffice")));
				
				customer.setcReason(CHF.showNull(rs.getString("cReason")));
				
				//2011-11-1
				customer.setNation(CHF.showNull(rs.getString("nation")));
				customer.setTotalassets(CHF.showNull(rs.getString("totalassets")));
				customer.setTotalcurname(CHF.showNull(rs.getString("totalcurname")));
				customer.setVip(CHF.showNull(rs.getString("vip")));
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "获取客户详细信息出错" + e.getMessage(), e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return customer;
	}

	/**
	 * 判断客户名是否存在 存在返回 客户ID 不存在返回 ok
	 * 全所同名
	 * @param customerName
	 * @return
	 * @throws Exception
	 */
	public String getCustomerId(String customerName) throws Exception {
		String result = null;

		String strSql = "select departid from k_customer where departname = ? and Property = '1'";
		Object[] params = new Object[] { customerName };
		result = new DbUtil(conn).queryForString(strSql, params);

		if (result != null) {
			return result;
		}
		return "ok";

	}
	/**
	 * 本部门同名
	 * @param customerName
	 * @param departmentid
	 * @return
	 * @throws Exception
	 */
	public String getCustomerId(String customerName,String departmentid) throws Exception {
		String result = null;

		String strSql = "select departid from k_customer where departname = ? and departmentid = ? and Property = '1'";
		Object[] params = new Object[] { customerName,departmentid };
		result = new DbUtil(conn).queryForString(strSql, params);

		if (result != null) {
			return result;
		}
		return "ok";

	}
	
	public String getCustomerName(String customerId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String customerName = "";

		String strSql = "select DepartName from k_customer where DepartID=?";
		
		try {
			ps = conn.prepareStatement(strSql);
			ps.setString(1, customerId);
			
			rs = ps.executeQuery();
			
			while(rs.next()){
				customerName = rs.getString(1);
			}		
		} catch (Exception e) {
			
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return customerName;
	}

	/**
	 * 根据项目ID获得客户ID
	 *
	 * @param projectID
	 * @return String类型,客户ID或者"fail"
	 * @throws Exception
	 */
	public String getCustomerIdByProjectId(String projectID) throws Exception {
		String result = null;

		String strSql = "select customerid from z_project where projectid = ? ";
		Object[] params = new Object[] { projectID };
		result = new DbUtil(conn).queryForString(strSql, params);

		if (result != null) {
			return result;
		} else {
			return "fail";
		}
	}

	/**
	 * 根据客户ID返回所有该客户的帐套年份列表
	 *
	 * @param customerId
	 * @return
	 * @throws Exception
	 */
	public List getAccPackageYears(String customerId) throws Exception {

		List packAgeList = new ArrayList();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String strSql = "select AccPackageYear from c_accpackage where CustomerID = ? order by 1";

			ps = conn.prepareStatement(strSql);
			ps.setString(1, customerId);

			rs = ps.executeQuery();

			while (rs.next()) {
				packAgeList.add(rs.getString(1));
			}

			return packAgeList;

		} catch (Exception e) {
			Debug.print(Debug.iError, "获取帐套列表失败", e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 创建客户数据库
	 *
	 * @param departID
	 *            String
	 * @return boolean
	 */
	public boolean createDataBase(String departID) throws MatechException {
		/**
		 * 创建数据库,不同的数据库类型有不同的处理方法 这里用到了SPRING的反射注入；
		 */
		MultiDbIF action = (MultiDbIF) UTILSysProperty.context
				.getBean("AuditMultiDbAction");
		return action.createCustomerDb(departID);
	}

	public boolean createDataBase(String sourceDatabaseName, String target)
			throws MatechException {
		MultiDbIF action = (MultiDbIF) UTILSysProperty.context
				.getBean("AuditMultiDbAction");
		return action.createCustomerDb(sourceDatabaseName, target);

	}

	/**
	 * 根据客户ID删除客户库
	 *
	 * @param departID
	 * @return
	 * @throws Exception
	 */
	public boolean dropDatabase(String departID) throws Exception {

		try {
			DbUtil dbUtil = new DbUtil(conn);
			String strSql = "drop database if exists asdb_" + departID;
			dbUtil.execute(strSql);
		} catch (Exception e) {
			Debug.print(Debug.iError, "删除数据库出错", e);
			throw e;
		}

		return true;
	}

	/**
	 * 更新所有模版
	 *
	 * @param strSql
	 * @throws MatechException
	 */
	public void updateAllTempTable(String strSql) throws MatechException {
		MultiDbIF action = (MultiDbIF) UTILSysProperty.context
				.getBean("MultiDbAction");
		action.executeSqlAtAllDb(conn, strSql);

	}


	/**
	 * 新增一个客户
	 * @param customerid		客户编号
	 * @param customername		客户名称
	 * @param IndustryID		客户行业类型（允许为"",此时会取默认行业类型）
	 * @throws Exception
	 */
	public void newCustomer(String customerid,String customername, String IndustryID) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;
		
		SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
		String nowtime = smf.format(new Date());
		
		try {

			int s = 1;
			if (IndustryID==null || "".equals(IndustryID)) {
				ps = conn
						.prepareStatement("select IndustryID from k_industry where IsDefault=1");
				rs = ps.executeQuery();
				if (rs.next()) {
					s = rs.getInt("IndustryID");
				} else {
					ps = conn
							.prepareStatement("select min(IndustryID) as IndustryID from k_industry");
					rs = ps.executeQuery();
					if (rs.next()) {
						s = rs.getInt("IndustryID");
					}
				}
			} else {
				s = Integer.parseInt(IndustryID);
			}

			ps = conn.prepareStatement("insert into k_customer (departid,departname,VocationID,property,recordtime) values (?,?,?,'1',?)");
			ps.setString(1, customerid);
			ps.setString(2, customername);
			ps.setInt(3, s);
			ps.setString(4, nowtime);
			ps.execute();
			DbUtil.close(ps);
			
			try{
				ps = conn.prepareStatement("update k_customer set departcode=departid where departid=?");
				ps.setString(1, customerid);
				ps.execute();
			}catch (Exception e) {
				//适应外审和企审通，因为企审通没有departcode字段；
			}
			
			org.util.Debug.prtOut("新建客户成功！");
			return;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("检查客户记录插入失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

	
	public void updateCustomerDepartmentID(String customerid,String departmentid) throws Exception{
		PreparedStatement ps = null;
		try {
			if(departmentid == null || "".equals(departmentid.trim())) return;
			
			String sql = "update k_customer set departmentid = ? where departid = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, departmentid);
			ps.setString(2, customerid);
			ps.execute();
			
		} catch (Exception e) {
			System.out.println("修改客户表的departmentid字段出错");
		} finally {
			DbUtil.close(ps);
		}
	}
	
	
	/**
	 * 检查客户的指定年份的帐套是否已经存在
	 *
	 * @return int 返回
	 * @throws Exception
	 */
	public int checkPackageExist(String customerId,String nf) throws Exception {

		if (customerId == null || customerId.equals("")) {
			throw new Exception("请先设置customerId属性");
		}

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn
					.prepareStatement("select count(*) from c_accpackage where customerid=? and accpackageyear=?");
			ps.setString(1, customerId);
			ps.setString(2, nf);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			} else {
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("检查客户帐套是否存在的SQL执行失败" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public static void main(String[] args) {
		
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			
			String sql = " select group_concat(departname) from k_customer where property=1 ";
			String result = new DbUtil(conn).queryForString(sql);
			System.out.println("现有客户列表：" + result + "\n");
			
			
			CustomerService customerService = new CustomerService(conn);
			
			String customerName = "广 州     					 市 铭 太";
			result = customerService.checkCustomerName(customerName);
			System.out.println("匹配结果：" + result);
			
			customerName = "广东省广州市铭太信息科技有限公司";
			result = customerService.checkCustomerName(customerName);
			System.out.println("匹配结果：" + result);
			
			customerName = "客户e";
			result = customerService.checkCustomerName(customerName);
			System.out.println("匹配结果：" + result);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 *
	 * 根据项目编号查询帐套编号
	 * @param projectID
	 * @return
	 * @throws Exception
	 */

	public String getAccPackageIdByProjectId(String projectID) throws Exception {

		String sql = "select accpackageID from z_project where projectid = ? order by audittimebegin";

		PreparedStatement ps = null;
		ResultSet rs = null;
		String AccPackageID = "";
		try{
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectID);
			rs = ps.executeQuery();

			if(rs.next()){
				AccPackageID = rs.getString("accpackageID");
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return AccPackageID;
	}
	
	/**
	 * 根据行业编号获得描述信息
	 * @param autoId
	 * @return
	 * @throws Exception
	 */
	public String getDescription(String autoId) throws Exception {

		String sql = "select description from k_description where autoId = ?";

		PreparedStatement ps = null;
		ResultSet rs = null;
		String description = "";
		try{
			ps = conn.prepareStatement(sql);
			ps.setString(1, autoId);
			rs = ps.executeQuery();

			if(rs.next()){
				description = rs.getString("description");
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return description;
	}
	
	/**
	 * 根据客户编号拿到客户简称
	 * @param customerId
	 * @return
	 * @throws Exception
	 */
	public String getShortName(String customerId) throws Exception {
		DbUtil.checkConn(conn);
		String result = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select Value from k_userdef where contrastID='" + customerId + "' and Name = '单位简称' and Property = 'com_cust'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()) {
				result = rs.getString(1);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return result;
	}
	
	public String getCustomerJson(String customer){
		PreparedStatement ps = null;
		ResultSet rs = null;
		String customerIeve ="";  //客户级别
		String hylx ="";   //行业类型
		String companyProperty ="";   //公司性质
		String approach  = ""; //客户来源
		try{
			String sql ="select customerIeve,hylx,companyProperty,approach from k_customer where departname='"+customer+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				customerIeve = rs.getString("customerIeve");
				hylx = rs.getString("hylx");
				companyProperty = rs.getString("companyProperty");
				approach = rs.getString("approach");
			}
			Map<String, String> customerMap = new HashMap<String, String>();
			customerMap.put("customerIeve", customerIeve);
			customerMap.put("hylx", hylx);
			customerMap.put("companyProperty", companyProperty);
			customerMap.put("approach", approach);
			
			String json = JSONArray.fromObject(customerMap).toString();
			return json;
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return "";
	}
	
	public void annianSave(Business business){
		
		  PreparedStatement ps = null;
		  try{
			   String sql ="insert into oa_business (customerid,customername,customerlevel,source,indistry,companyType,demandType,demand," +
			   		"contact,rank,contactway,email,QQorMSN,distriman,follow ,iuser,idate) " +
			   		" values(?,?,?,?,? ,?,?,?,?,? ,?,?,?,?,?, ?,now())";//, ?,now()
			   ps = conn.prepareStatement(sql);
			   ps.setString(1, business.getCustomerId());
			   ps.setString(2, business.getCustomername());
			   ps.setString(3, business.getCustomerlevel());
			   ps.setString(4, business.getSource());
			   ps.setString(5, business.getIndistry());
			   ps.setString(6, business.getCompanyType());
			   ps.setString(7, business.getDemandType());
			   ps.setString(8, business.getDemand());
			   ps.setString(9, business.getContact());
			   ps.setString(10, business.getRank());
			   ps.setString(11,business.getContactway());
			   ps.setString(12, business.getEmail());
			   ps.setString(13,business.getQQorMSN());
			   ps.setString(14, business.getDistriman());
			   ps.setString(15, business.getFollow());
			   ps.setString(16, business.getIuser());
			   ps.execute();
		  }catch(Exception e){
			  e.printStackTrace();
		  }finally{
			  DbUtil.close(ps);
		  }
	}
	
	public Business getBusiness(String autoId){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Business business = new Business();
		try{
			String sql="select autoid,customerid,customername,customerlevel,source,indistry,companyType,demandType,demand,contact,rank,contactway" +
		   		",email,QQorMSN,distriman,follow from oa_business where autoId ="+autoId;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				business.setAutoId(rs.getString("autoid"));
				business.setCustomerId(rs.getString("customerid"));
				business.setCustomername(rs.getString("customername"));
				business.setCustomerlevel(rs.getString("customerlevel"));
				business.setSource(rs.getString("source"));
				business.setIndistry(rs.getString("indistry"));
				business.setCompanyType(rs.getString("companyType"));
				business.setDemandType(rs.getString("demandType"));
				business.setDemand(rs.getString("demand"));
				business.setContact(rs.getString("contact"));
				business.setRank(rs.getString("rank"));
				business.setContactway(rs.getString("contactway"));
				business.setEmail(rs.getString("email"));
				business.setQQorMSN(rs.getString("QQorMSN"));
				business.setDistriman(rs.getString("distriman"));
				business.setFollow(rs.getString("follow"));
			}	
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return business;
	}
	
	public void annianUpdate(Business business){
		  PreparedStatement ps = null;
		  try{
			  String sql="update oa_business set customerid=?,customername=?,customerlevel=?,source=?,indistry=?,companyType=?," +
			  		"demandType=?,demand=?,contact=?,rank=?,contactway=?,email=?,QQorMSN=?,distriman=?,follow=?,auser=?,adate =now() where autoId=?";//,auser=?,adate =now()
			  ps = conn.prepareStatement(sql);
			  ps.setString(1,business.getCustomerId());
			  ps.setString(2,business.getCustomername());
			  ps.setString(3,business.getCustomerlevel());
			  ps.setString(4, business.getSource());
			  ps.setString(5, business.getIndistry());
			  ps.setString(6, business.getCompanyType());
			  ps.setString(7, business.getDemandType());
			  ps.setString(8, business.getDemand());
			  ps.setString(9, business.getContact());
			  ps.setString(10,business.getRank());
			  ps.setString(11,business.getContactway());
			  ps.setString(12,business.getEmail());
			  ps.setString(13, business.getQQorMSN());
			  ps.setString(14, business.getDistriman());
			  ps.setString(15, business.getFollow());
			  ps.setString(16, business.getAuser());
			  ps.execute();
		  }catch(Exception e){
			  e.printStackTrace();
		  }finally{
			  DbUtil.close(ps);
		  }
	}
	public void annianDelete(String autoId){
		PreparedStatement ps = null;
		try{
			String sql="delete from oa_business where autoId ="+autoId;
			ps = conn.prepareStatement(sql);
			ps.execute();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	public void annianSetmul(String customer,String flag,String userId){
		  PreparedStatement ps = null;
		  try{
			  String sql="";
			  if(flag.equals("distri")){
			      sql="update oa_business set distriman ='"+userId+"' where autoId in("+customer+")";
			  }
			  if(flag.equals("follow")){
				  sql="update oa_business set follow ='"+userId+"' where autoId in("+customer+")";
			  }
			  ps = conn.prepareStatement(sql);
			  ps.execute();
		  }catch(Exception e){
			  e.printStackTrace();
		  }finally{
			  DbUtil.close(ps);
		  }
	}
	
	public void annianAddFollow(String autoId,String userId,String departmentId,String createTime){
		  PreparedStatement ps = null;
		  ResultSet rs = null;
		  int id =0;
		  try{
			  String sql ="insert into k_follow (customer,linkpeople,disman,followman) " +
			  		"select customerid,contact,distriman,follow from oa_business where autoId ="+autoId;
			  ps = conn.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
			  ps.execute();
			  rs = ps.getGeneratedKeys();
			  if(rs.next()){
				  id = rs.getInt(1);
			  }
			  sql="update k_follow set createUser='"+userId+"',createDepartment='"+departmentId+"',createTime='"+createTime+"'" +
			  		" where autoId ="+id;
			  ps.close();
			  ps = conn.prepareStatement(sql);
			  ps.execute();
		  }catch(Exception e){
			  e.printStackTrace();
		  }finally{
			  DbUtil.close(rs);
			  DbUtil.close(ps);
		  }
	}
	//批量删除
	public void delAPlacard(String id){
		 PreparedStatement ps = null;
		 try{
			 String sql="delete from oa_business  where autoId="+id;
			 ps = conn.prepareStatement(sql);
			 ps.execute();
		 }catch(Exception e){
			 e.printStackTrace();
		 }finally{
			 DbUtil.close(ps);
		 }
	}

}
