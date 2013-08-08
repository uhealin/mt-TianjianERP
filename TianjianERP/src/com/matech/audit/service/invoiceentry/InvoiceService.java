package com.matech.audit.service.invoiceentry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;


import com.matech.audit.service.invoiceentry.model.InvoiceTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

/**
 * 开票
 * @author Administrator
 *
 */
public class InvoiceService {
	private Connection conn = null;

	public InvoiceService(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 根据autoid得到开票
	 * @param autoid
	 * @return
	 * @throws Exception
	 */
	public InvoiceTable getInvoiceTableByAutoid(String autoid) throws Exception{
		DbUtil.checkConn(conn);
		InvoiceTable  it= null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = " select projectid,username,times,createUser,invoicenumber,receiceUser,money,cdate,remark,property,companyName,invoiceItem,customerCode,companyProperties,incomeItem,ifPlanGathering,payUnitId from k_invoice where autoid = ? ";
			ps=conn.prepareStatement(sql);
			ps.setString(1, autoid);
			rs = ps.executeQuery();
			it = new InvoiceTable();
			if(rs.next()){
				it.setAutoid(autoid);
				it.setProjectid(rs.getString("projectid"));
				it.setInvoicenumber(rs.getString("invoicenumber"));
				it.setReceiceUser(rs.getString("receiceUser"));
				it.setMoney(rs.getString("money"));
				it.setCdate(rs.getString("cdate"));
				it.setRemark(rs.getString("remark"));
				it.setProperty(rs.getString("property"));
				it.setCreateUser(rs.getString("createUser"));
				it.setUsername(rs.getString("username"));
				it.setTime(rs.getString("times"));
				it.setCompanyName(rs.getString("companyName"));
				it.setInvoiceItem(rs.getString("invoiceItem"));
				it.setCompanyProperties(rs.getString("companyProperties"));
				it.setCustomerCode(rs.getString("customerCode"));
				it.setIncomeItem(rs.getString("incomeItem"));
				it.setIfPlanGathering(rs.getString("ifPlanGathering"));
				it.setPayUnitId(rs.getString("payUnitId"));
			}
			return it;
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return null;
	}
	
	/**
	 * 根据projectid得到开票
	 * @param autoid
	 * @return
	 * @throws Exception
	 */
	public InvoiceTable getInvoiceTableByProjectcid(String projectid) throws Exception{
		DbUtil.checkConn(conn);
		InvoiceTable  it= null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = " select autoid,invoicenumber,receiceUser,money,cdate,remark,property,companyName,invoiceItem,ifPlanGathering from k_invoice where projectid = ? ";
			ps=conn.prepareStatement(sql);
			ps.setString(1, projectid);
			rs = ps.executeQuery();
			if(rs.next()){
				it = new InvoiceTable();
				it.setAutoid(rs.getString("autoid"));
				it.setInvoicenumber(rs.getString("invoicenumber"));
				it.setReceiceUser(rs.getString("receiceUser"));
				it.setMoney(rs.getString("money"));
				it.setCdate(rs.getString("cdate"));
				it.setRemark(rs.getString("remark"));
				it.setProperty(rs.getString("property"));
				it.setCompanyName(rs.getString("companyName"));
				it.setInvoiceItem(rs.getString("invoiceItem"));
				it.setIfPlanGathering(rs.getString("ifPlanGathering"));
			}
			return it;
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return null;
	}
	
	
	
	/**
	 * 新增发票
	 * @param group
	 * @return
	 * @throws Exception
	 */
	public boolean addInvoice(InvoiceTable it) throws Exception{
		 
		DbUtil.checkConn(conn);
		PreparedStatement ps=null;
		try {
			String sql = " insert into k_invoice (projectid,invoicenumber,receiceUser,money,cdate,remark,createUser,property,username,times,companyName,invoiceItem,departmentId,customerCode,companyProperties,incomeItem,ifPlanGathering,`state`,payUnitId ) "
				      +  " values(?,?,?,?,?,?,?,?, ?,?,?,?,?,?,?,?,?,?,?)";
			ps=conn.prepareStatement(sql);
			ps.setString(1, it.getProjectid());
			ps.setString(2, it.getInvoicenumber());
			ps.setString(3, it.getReceiceUser());
			ps.setString(4, it.getMoney());
			ps.setString(5, it.getCdate());
			ps.setString(6, it.getRemark());
			ps.setString(7, it.getCreateUser());
			ps.setString(8, it.getProperty());
			
			ps.setString(9, it.getUsername());
			ps.setString(10,it.getTime());
			ps.setString(11,it.getCompanyName());
			ps.setString(12,it.getInvoiceItem());
			ps.setString(13,it.getDepartmentId());
			ps.setString(14, it.getCustomerCode());
			ps.setString(15, it.getCompanyProperties());
			ps.setString(16, it.getIncomeItem());
			ps.setString(17, it.getIfPlanGathering());
			ps.setString(18, it.getState());
			ps.setString(19, it.getPayUnitId()); //付款单位
			ps.execute();
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return false;
	}
	
	/**
	 * 修改发票
	 * @param it
	 * @return
	 * @throws Exception
	 */
	public boolean updateInvoice(InvoiceTable it) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try {
			String sql = "update k_invoice set projectid=?,invoicenumber=?,receiceUser=?,money=?,cdate=?,remark=?,createUser=?,companyName=?,invoiceItem=?,customerCode=?,companyProperties=?,incomeItem=?,ifPlanGathering=? where autoid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, it.getProjectid());
			ps.setString(2, it.getInvoicenumber());
			ps.setString(3, it.getReceiceUser());
			ps.setString(4, it.getMoney());
			ps.setString(5, it.getCdate());
			ps.setString(6, it.getRemark());
			ps.setString(7, it.getCreateUser());
			ps.setString(8, it.getCompanyName());
			ps.setString(9, it.getInvoiceItem());
			ps.setString(10, it.getCustomerCode());
			ps.setString(11, it.getCompanyProperties());
			ps.setString(12, it.getIncomeItem());
			ps.setString(13, it.getIfPlanGathering());
			ps.setString(14, it.getAutoid());
			ps.execute();
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return false;
	}
	
	
	/**
	 * 删除发票
	 * @return
	 * @throws Exception
	 */
	public boolean delInvoice(String autoid) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try {
			String sql = "delete from k_invoice where autoid = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, autoid);
			ps.execute();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return false;
	}
	
	
	
	/**
	 * 根据项目编号得到项目信息
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	
	public String getProjectInfoJson(String projectid) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;
		ASFuntion CHF = new ASFuntion();
		try {

				
			String sql = " select p.isStock,replace(p.businessCost,',','') as businessCost,p.projectname,"
					   + " p.entrustNumber,p.reportNumber,c.departname as customername,f.departName as interrogeeName,p.partnerUserId,e.name as partnerUserName,p.customerType," 
					   	+ " p.auditpara,p.payCustomerId,d.departname,"
					   + " p.reportfilename,p.reportfiletempname,p.property  "
					   + " from z_projectbusiness p  "
					   + " left join k_customer c on p.payCustomerId = c.departid "
					   + " left join k_department d on p.departmentid = d.autoid "
					   + " left join k_user e on p.partnerUserId = e.id "
					   + " left join k_customer f on p.customerId = f.departid "  //被审客户
					   + " where 1=1 and p.projectid = ?";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectid);
			
			rs = ps.executeQuery();
			Map<String, String> map = new HashMap<String, String>();

			//客户编号
			String customerId = "";
			
			//单位性质    就是建项时填写的客户性质
			String customerType = "";
			
			//收入类项目  就是建项时填写的委托内容
			String auditpara = "";
			
			//业务合伙人Id
			String partnerUserId = "";
			
			//业务合伙人名字
			String partnerUserName = "";
			
			//被审客户
			String interrogeeName = "";
			
			//付款单位
			String customername = "";
			//委托号
			String entrustNumber = "";
			//报告号
			String reportNumber = "";
			//承接部门
			String departname = "";
			//业务费用
			String businessCost = "";
			//企业是否具有证劵业务
			String isStock = "";
			//业务约定书
			String reportfilename = "";
			//业务约定书
			String reportfiletempname = "";
			//业务状态
			String property = "";
			
			
			if (rs.next()) {
				customerId = rs.getString("payCustomerId");
				customerType = rs.getString("customerType");
				auditpara = rs.getString("auditpara");
				partnerUserId = rs.getString("partnerUserId");
				partnerUserName = rs.getString("partnerUserName");
				interrogeeName = rs.getString("interrogeeName");
				customername = rs.getString("customername");
				entrustNumber = rs.getString("entrustNumber");
				reportNumber = rs.getString("reportNumber");
				departname = rs.getString("departname");
				businessCost = rs.getString("businessCost");
				isStock = rs.getString("isStock");
				reportfilename = rs.getString("reportfilename");
				reportfiletempname = rs.getString("reportfiletempname");
				property = rs.getString("property");
			}
			map.put("customerId", CHF.showNull(customerId));
			map.put("customerType", CHF.showNull(customerType));
			map.put("auditpara", CHF.showNull(auditpara));
			map.put("partnerUserId", CHF.showNull(partnerUserId));
			map.put("partnerUserName", CHF.showNull(partnerUserName));
			map.put("interrogeeName", CHF.showNull(interrogeeName));
			map.put("customername", CHF.showNull(customername));
			map.put("entrustNumber", CHF.showNull(entrustNumber));
			map.put("reportNumber", CHF.showNull(reportNumber));
			map.put("departname", CHF.showNull(departname));
			map.put("businessCost", CHF.showNull(businessCost));
			map.put("isStock", CHF.showNull(isStock));
			
			if(!"".equals(CHF.showNull(reportfiletempname))){
				map.put("reportfilename", CHF.showNull(reportfilename));
			}else{
				map.put("reportfilename", "无");
			}
			
			map.put("reportfiletempname", CHF.showNull(reportfiletempname));
			
			if(!"".equals(CHF.showNull(property))){
				map.put("property", "已完结");
			}else{
				map.put("property", "未完结");
			}
			
			// 得到已开票金额
			String strSql = "select sum(money) from k_invoice where projectid = '"+projectid+"'";
			String invoicemoney = new DbUtil(conn).queryForString(strSql);
			if(invoicemoney == null || "".equals(invoicemoney)){
				invoicemoney = "0";
			}
			
			map.put("invoicemoney", invoicemoney);
			
			String json = JSONArray.fromObject(map).toString();
			return json;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return "";
	}
	

	/**
	 * 删除发票
	 * @return
	 * @throws Exception
	 */
	public boolean delInvoiceByProjectId(String projectId) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try {
			String sql = "delete from k_invoice where projectid = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectId);
			ps.execute();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return false;
	}
	
	
	
	
	/**
	 * 查询历史开票信息
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	
	public String getHistoryInfo(String projectid,String autoid) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
				
			String sql = " select i.autoid,projectid,cdate,invoicenumber,money,receiceUser,createUser,u.name,i.companyName,i.invoiceItem from k_invoice i "
					   + " left join k_user u on i.createUser = u.id "
					   + " where 1=1 and i.projectid = ? ";
			
			if(!"".equals(autoid) && null!=autoid){
				sql = sql + " and i.autoid!='"+autoid+"' ";
			}
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectid);
			
			rs = ps.executeQuery();
			
			List list = new ArrayList();
 
			while(rs.next()){
				InvoiceTable it = new InvoiceTable();
				it.setAutoid(rs.getString("autoid"));
				it.setProjectid(rs.getString("projectid"));
				it.setCdate(rs.getString("cdate"));
				it.setInvoicenumber(rs.getString("invoicenumber"));
				it.setMoney(rs.getString("money"));
				it.setCreateUser(rs.getString("name"));
				it.setReceiceUser(rs.getString("receiceUser"));
				it.setCompanyName(rs.getString("companyName"));
				it.setInvoiceItem(rs.getString("invoiceItem"));
				list.add(it);
			}
			
			String json = JSONArray.fromObject(list).toString();
			
			return json;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return "";
	}
	/**
	 * 根据projectid得到开票
	 * @param autoid
	 * @return
	 * @throws Exception
	 */
	public List<InvoiceTable> getInvoiceTableByProjectId(String projectId) throws Exception{
		List<InvoiceTable> list=new ArrayList<InvoiceTable>();
		InvoiceTable  it= null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = " select autoid,projectid,username,times,invoicenumber,receiceUser,money,cdate,remark,property,createUser,companyName,invoiceItem,customerCode,companyProperties,incomeItem from k_invoice where projectid = ? ";
			ps=conn.prepareStatement(sql);
			ps.setString(1, projectId);
			rs = ps.executeQuery();
			while(rs.next()){
				it = new InvoiceTable();
				 
				it.setAutoid(rs.getString("autoid"));
				it.setProjectid(rs.getString("projectid"));
				it.setInvoicenumber(rs.getString("invoicenumber"));
				it.setReceiceUser(rs.getString("receiceUser"));
				it.setMoney(rs.getString("money"));
				it.setCdate(rs.getString("cdate"));
				it.setRemark(rs.getString("remark"));
				it.setProperty(rs.getString("property"));
				it.setUsername(rs.getString("username"));
				it.setTime(rs.getString("times"));
				it.setCreateUser(rs.getString("createUser"));
				it.setCompanyName(rs.getString("companyName"));
				it.setInvoiceItem(rs.getString("invoiceItem"));
				it.setCustomerCode(rs.getString("customerCode"));
				it.setCompanyProperties(rs.getString("companyProperties"));
				it.setIncomeItem(rs.getString("incomeItem"));
				list.add(it);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return list;
	}
	/**
	 * 查询历史开票信息
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	
	public String getHistoryInfoNew(String projectid,String autoid) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
				
			String sql = " select i.autoid,projectid,cdate,invoicenumber,money,receiceUser,createUser,i.companyName,i.invoiceItem, u.name from k_invoice i "
					   + " left join k_user u on i.createUser = u.id "
					   + " where 1=1 and i.projectid = ? ";
			
			if(!"".equals(autoid) && null!=autoid){
				sql = sql + " and i.autoid='"+autoid+"' ";
			}
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectid);
			
			rs = ps.executeQuery();
			
			List list = new ArrayList();
 
			while(rs.next()){
				InvoiceTable it = new InvoiceTable();
				it.setAutoid(rs.getString("autoid"));
				it.setProjectid(rs.getString("projectid"));
				it.setCdate(rs.getString("cdate"));
				it.setInvoicenumber(rs.getString("invoicenumber"));
				it.setMoney(rs.getString("money"));
				it.setCreateUser(rs.getString("name"));
				it.setReceiceUser(rs.getString("receiceUser"));
				it.setCompanyName(rs.getString("companyName"));
				it.setInvoiceItem(rs.getString("invoiceItem"));
				list.add(it);
			}
			
			String json = JSONArray.fromObject(list).toString();
			
			return json;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return "";
	}
}
