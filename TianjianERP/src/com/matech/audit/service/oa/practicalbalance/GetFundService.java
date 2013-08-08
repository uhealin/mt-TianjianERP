package com.matech.audit.service.oa.practicalbalance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;


import com.matech.audit.service.invoiceentry.model.InvoiceTable;
import com.matech.audit.service.oa.practicalbalance.model.GetFundsTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

/**
 * 收款
 * @author Administrator
 *
 */
public class GetFundService {
	private Connection conn = null;

	public GetFundService(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 根据autoid得到收款
	 * @param autoid
	 * @return
	 * @throws Exception
	 */
	public GetFundsTable getGetFundsTableByAutoid(String autoid) throws Exception{
		DbUtil.checkConn(conn);
		GetFundsTable  gt= null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = " select projectid,ctype,ctypenumber,receiceMoney,receicedate,accounttype,remark,createUser,property,certificateNumber,customerCode,payCustomerId,continueDepartId,invoicenumber from k_getFunds  where autoid = ? ";
			ps=conn.prepareStatement(sql);
			ps.setString(1, autoid);
			rs = ps.executeQuery();
			if(rs.next()){
				gt = new GetFundsTable();
				gt.setAutoid(autoid);
				gt.setProjectid(rs.getString("projectid"));
				gt.setCtype(rs.getString("ctype"));
				gt.setCtypenumber(rs.getString("ctypenumber"));
				gt.setReceiceMoney(rs.getString("receiceMoney"));
				gt.setReceicedate(rs.getString("receicedate"));
				gt.setAccounttype(rs.getString("accounttype"));
				gt.setCreateUser(rs.getString("createUser"));
				gt.setRemark(rs.getString("remark"));
				gt.setProperty(rs.getString("property"));
				gt.setCertificateNumber(rs.getString("certificateNumber"));
				gt.setCustomerCode(rs.getString("customerCode"));
				gt.setPayCustomerId(rs.getString("payCustomerId"));
				gt.setContinueDepartId(rs.getString("continueDepartId"));
				gt.setInvoicenumber(rs.getString("invoicenumber"));
			}
			return gt;
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return null;
	}
	
	/**
	 * 根据projectid得到收款
	 * @param autoid
	 * @return
	 * @throws Exception
	 */
	public GetFundsTable getInvoiceTableByProjectcid(String projectid) throws Exception{
		DbUtil.checkConn(conn);
		GetFundsTable  gt= null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = " select autoid,projectid,ctype,ctypenumber,receiceMoney,receicedate,accounttype,createUser,remark,property,certificateNumber,customerCode from k_getFunds where projectid = ? ";
			ps=conn.prepareStatement(sql);
			ps.setString(1, projectid);
			rs = ps.executeQuery();
			if(rs.next()){
				gt = new GetFundsTable();
				gt.setAutoid(rs.getString("autoid"));
				gt.setProjectid(rs.getString("projectid"));
				gt.setCtype(rs.getString("ctype"));
				gt.setCtypenumber(rs.getString("ctypenumber"));;
				gt.setReceiceMoney(rs.getString("receiceMoney"));
				gt.setReceicedate(rs.getString("receicedate"));
				gt.setAccounttype(rs.getString("accounttype"));
				gt.setCreateUser(rs.getString("createUser"));
				gt.setRemark(rs.getString("remark"));
				gt.setProperty(rs.getString("property"));
				gt.setCertificateNumber(rs.getString("certificateNumber"));
				gt.setCustomerCode(rs.getString("customerCode"));
			}
			return gt;
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return null;
	}
	
	
	
	/**
	 * 新增收款
	 * @param group
	 * @return
	 * @throws Exception
	 */
	public boolean addGetFunds(GetFundsTable gt) throws Exception{
		 
		DbUtil.checkConn(conn);
		PreparedStatement ps=null;
		try {
			String sql = " insert into k_getFunds (projectid,ctype,ctypenumber,receiceMoney,receicedate,accounttype,createUser,remark,property,invoicenumber,certificateNumber,customerCode,payCustomerId,continueDepartId) "
				      +  " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			ps=conn.prepareStatement(sql);
			ps.setString(1, gt.getProjectid());
			ps.setString(2, gt.getCtype());
			ps.setString(3, gt.getCtypenumber());
			ps.setString(4, gt.getReceiceMoney());
			ps.setString(5, gt.getReceicedate());
			ps.setString(6, gt.getAccounttype());
			ps.setString(7, gt.getCreateUser());
			ps.setString(8, gt.getRemark());
			ps.setString(9, gt.getProperty());
			ps.setString(10,gt.getInvoicenumber());
			ps.setString(11, gt.getCertificateNumber());
			ps.setString(12, gt.getCustomerCode());
			ps.setString(13, gt.getPayCustomerId());
			ps.setString(14, gt.getContinueDepartId());
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
	 * 修改收款
	 * @param gt
	 * @return
	 * @throws Exception
	 */
	public boolean updateGetFunds(GetFundsTable gt) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try {
			String sql = "update k_getFunds set projectid=?,ctype=?,ctypenumber=?,receiceMoney=?," +
						" receicedate=?,accounttype=?,createUser=?,remark=?,certificateNumber=?,customerCode=?,continueDepartId=? where autoid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, gt.getProjectid());
			ps.setString(2, gt.getCtype());
			ps.setString(3, gt.getCtypenumber());
			ps.setString(4, gt.getReceiceMoney());
			ps.setString(5, gt.getReceicedate());
			ps.setString(6, gt.getAccounttype());
			ps.setString(7, gt.getCreateUser());
			ps.setString(8, gt.getRemark());
			ps.setString(9, gt.getCertificateNumber());
			ps.setString(10, gt.getCustomerCode());
			ps.setString(11, gt.getContinueDepartId());
			ps.setString(12, gt.getAutoid());
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
	 * 删除收款
	 * @return
	 * @throws Exception
	 */
	public boolean delGetFunds(String autoid) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try {
			String sql = "delete from k_getFunds where autoid = ?";
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

			String sql = " select p.isStock,replace(p.businessCost,',','') as businessCost,b.invoicenumber,p.projectname,p.entrustNumber," 
					   + " p.reportNumber,c.departname as customername,d.departname,p.departmentid, "
					   + " p.reportfilename,p.reportfiletempname,p.property  "
					   + " from z_projectbusiness p "
					   + " left join k_customer c on p.payCustomerId = c.departid "
					   + " left join k_department d on p.departmentid = d.autoid "
				       + " INNER JOIN k_invoice b ON p.projectId= b.projectId"   	   
					   + " where 1=1 and p.projectid = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectid);
			
			rs = ps.executeQuery();
			Map<String, String> map = new HashMap<String, String>();

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
			//发票编号
			String invoicenumber="";
			
			String departmentid = "";
			if (rs.next()) {
				businessCost = rs.getString("businessCost");
				customername = rs.getString("customername");
				entrustNumber = rs.getString("entrustNumber");
				reportNumber = rs.getString("reportNumber");
				departname = rs.getString("departname");
				departmentid = rs.getString("departmentid");
				isStock = rs.getString("isStock");
				reportfilename = rs.getString("reportfilename");
				reportfiletempname = rs.getString("reportfiletempname");
				property = rs.getString("property");
			//	invoicenumber=rs.getString("invoicenumber");
				
			}
			map.put("businessCost", CHF.showNull(businessCost));
			map.put("customername", CHF.showNull(customername));
			map.put("entrustNumber", CHF.showNull(entrustNumber));
			map.put("reportNumber", CHF.showNull(reportNumber));
			map.put("departname", CHF.showNull(departname));
			map.put("isStock", CHF.showNull(isStock));
			map.put("departmentId", CHF.showNull(departmentid));
			map.put("invoicenumber",CHF.showNull(invoicenumber));
			
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
			
			// 得到已收款金额
			String strSql = "select sum(receiceMoney) from k_getfunds where projectid = '"+projectid+"'";
			String getFundsMoney = new DbUtil(conn).queryForString(strSql);
			if(getFundsMoney==null || "".equals(getFundsMoney)){
				getFundsMoney = "0";
			}
			map.put("getFundsMoney",getFundsMoney);
			
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
	 * 删除收款
	 * @return
	 * @throws Exception
	 */
	public boolean delGetFundsByProjectId(String projectId) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try {
			
			String sql = "delete from k_getFunds where projectid = ?";
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
	 * 查询历史收款信息
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	
	public String getHistoryInfo(String projectid,String autoid) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
				
			String sql = " select autoId,projectid,receicedate,receicemoney,createUser,u.name,g.certificateNumber from k_getFunds g "  
					   + " left join k_user u on g.createUser = u.id "  
					   + " where 1=1 and g.projectid = ? ";
			
			if(!"".equals(autoid) && null!=autoid){
				sql = sql + " and g.autoid!='"+autoid+"' ";
			}
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectid);
			
			rs = ps.executeQuery();
			
			List list = new ArrayList();
 
			while(rs.next()){
				GetFundsTable gf = new GetFundsTable();
				gf.setAutoid(rs.getString("autoid"));
				gf.setProjectid(rs.getString("projectid"));
				gf.setReceicedate(rs.getString("receicedate"));
				gf.setReceiceMoney(rs.getString("receiceMoney"));
				gf.setCreateUser(rs.getString("name"));
				gf.setCertificateNumber(rs.getString("certificateNumber"));
				list.add(gf);
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
	
	public String getCount(String beginTime,String endTime){
		String count=null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql="";
			if(beginTime!=null && !beginTime.equals("") && endTime!=null && !endTime.equals("")){
				sql="SELECT SUM(receicemoney) FROM k_getfunds where receicedate between "+beginTime+" and "+endTime;
			}else{
				sql="SELECT SUM(receicemoney) FROM k_getfunds";
			}
			ps=this.conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){
				count=rs.getString(1);
			}else{
				count="0";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return count;
	}
	
	/**
	 * 根据projectid得到收款
	 * @param autoid
	 * @return
	 * @throws Exception
	 */
	public List<GetFundsTable> getGetFundsTableByProjectId(String projectId) throws Exception{
		List<GetFundsTable> list=null;
		GetFundsTable gt=null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = " select autoid,projectid,ctype,ctypenumber,receiceMoney,receicedate,accounttype,remark,createUser,property,certificateNumber from k_getFunds  where projectId = ? ";
			ps=conn.prepareStatement(sql);
			ps.setString(1, projectId);
			rs = ps.executeQuery();
			while(rs.next()){
				if(list==null){
					list=new ArrayList<GetFundsTable>();
				}
				gt = new GetFundsTable();
				gt.setAutoid(rs.getString("autoid"));
				gt.setProjectid(rs.getString("projectid"));
				gt.setCtype(rs.getString("ctype"));
				gt.setCtypenumber(rs.getString("ctypenumber"));
				gt.setReceiceMoney(rs.getString("receiceMoney"));
				gt.setReceicedate(rs.getString("receicedate"));
				gt.setAccounttype(rs.getString("accounttype"));
				gt.setCreateUser(rs.getString("createUser"));
				gt.setRemark(rs.getString("remark"));
				gt.setProperty(rs.getString("property"));
				gt.setCertificateNumber(rs.getString("certificateNumber"));
				list.add(gt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return list;
	}
	
	/**
	 * 查询历史收款信息
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	
	public String getHistoryInfoNew(String projectid,String autoid) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
				
			String sql = " select autoId,projectid,receicedate,receicemoney,createUser,u.name,certificateNumber from k_getFunds g "  
					   + " left join k_user u on g.createUser = u.id "  
					   + " where 1=1 and g.projectid = ? ";
			
			if(!"".equals(autoid) && null!=autoid){
				sql = sql + " and g.autoid='"+autoid+"' ";
			}
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectid);
			
			rs = ps.executeQuery();
			
			List list = new ArrayList();
 
			while(rs.next()){
				GetFundsTable gf = new GetFundsTable();
				gf.setAutoid(rs.getString("autoid"));
				gf.setProjectid(rs.getString("projectid"));
				gf.setReceicedate(rs.getString("receicedate"));
				gf.setReceiceMoney(rs.getString("receiceMoney"));
				gf.setCreateUser(rs.getString("name"));
				gf.setCertificateNumber(rs.getString("certificateNumber"));
				list.add(gf);
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
