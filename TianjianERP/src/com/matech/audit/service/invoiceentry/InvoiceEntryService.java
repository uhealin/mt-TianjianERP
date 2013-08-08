package com.matech.audit.service.invoiceentry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.invoiceentry.model.BillManagerTable;
import com.matech.framework.pub.db.DbUtil;

/**
 * 科目绝对数分析的类
 * @author Cooler
 *
 */
public class InvoiceEntryService {

	private Connection conn = null;

	public InvoiceEntryService(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 新增发票的方法
	 * @param group
	 * @return
	 * @throws Exception
	 */
	public boolean addBill(BillManagerTable bill) throws Exception{
		 
		DbUtil.checkConn(conn);
		PreparedStatement ps=null;
		try {
			String sql = " insert into oa_bill_manage (customerName,bargainId,billId,billCode,billName,billMoney,tax,taxtype,state,billTime,property) "
				      +  " values(?,?,?,?,?,?,?,?,?,?,?)";
			ps=conn.prepareStatement(sql);
			ps.setString(1, bill.getCustomerName());
			ps.setString(2, bill.getBargainId());
			ps.setString(3, bill.getBillId());
			ps.setString(4, bill.getBillCode());
			ps.setString(5, bill.getBillName());
			ps.setString(6, bill.getBillMoney());
			ps.setString(7, bill.getTax());
			ps.setString(8, bill.getTaxtype());
			ps.setString(9, "启用中");
			ps.setString(10, bill.getBillTime());
			ps.setString(11, bill.getProperty()); 
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
	 * 修改发票的方法
	 * @param group
	 * @return
	 * @throws Exception 
	 */
	public boolean updateBill(BillManagerTable bill) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps=null;
		try {
			String sql = " update oa_bill_manage set bargainId=?,billId=?,billCode=?,billName=?,billMoney=?,taxtype=?,billTime=?," 
				       + " property=? where autoid = ?";
			ps=conn.prepareStatement(sql);
			int i = 1; 
			ps.setString(i++, bill.getBargainId());
			ps.setString(i++, bill.getBillId());
			ps.setString(i++, bill.getBillCode());
			ps.setString(i++, bill.getBillName());
			ps.setString(i++, bill.getBillMoney());
			ps.setString(i++, bill.getTaxtype()); 
			ps.setString(i++, bill.getBillTime());
			ps.setString(i++, bill.getProperty()); 
			ps.setString(i++, bill.getAutoid()); 
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
	 *  根据编号得到对象的方法
	 * @param autoid
	 * @return
	 */
	public BillManagerTable getNnvoiceeById(String autoid) throws Exception{
		DbUtil.checkConn(conn);
		BillManagerTable bill=null;
		ResultSet rs=null;
		PreparedStatement ps=null;
		try {
			String sql = " select customerName,bargainId,billId,billCode,billName,billMoney,tax,taxtype,state,billTime,property from " 
					   + " oa_bill_manage where autoid = ?";
			ps=conn.prepareStatement(sql);
			ps.setString(1, autoid);
			rs=ps.executeQuery();
			if(rs.next()){
				bill = new BillManagerTable();
				bill.setAutoid(autoid);
				bill.setCustomerName(rs.getString(1));
				bill.setBargainId(rs.getString(2));
				bill.setBillId(rs.getString(3));
				bill.setBillCode(rs.getString(4));
				bill.setBillName(rs.getString(5));
				bill.setBillMoney(rs.getString(6));
				bill.setTax(rs.getString(7));
				bill.setTaxtype(rs.getString(8));
				bill.setState(rs.getString(9));
				bill.setBillTime(rs.getString(10));
				bill.setProperty(rs.getString(11));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return bill;
	}

	/**
	 *  根据编号得到发票状态的方法
	 * @param autoid
	 * @return
	 */
	public String getNnvoiceeStateById(String autoid) throws Exception{
		DbUtil.checkConn(conn);
		String state = null;
		ResultSet rs=null;
		PreparedStatement ps=null;
		try {
			String sql = " select state from oa_bill_manage where autoid = ?";
			ps=conn.prepareStatement(sql);
			ps.setString(1, autoid);
			rs=ps.executeQuery();
			if(rs.next()){
				state = rs.getString("state");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return state;
	}
	
	
	/**
	 *  判断是否重复的方法
	 * @param autoid
	 * @return
	 */
	public String decisionRepeat(String isRepeat) throws Exception{
		DbUtil.checkConn(conn);
		String value = null;
		ResultSet rs=null;
		PreparedStatement ps=null;
		try {
			String sql = " select 1 from oa_bill_manage where billId = ? or billCode = ?";
			ps=conn.prepareStatement(sql);
			ps.setString(1, isRepeat);
			ps.setString(2, isRepeat);
			
			rs=ps.executeQuery();
			if(rs.next()){
				value = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return value;
	}
	
	
	/**
	 * 修改发票状态的方法
	 * @param group
	 * @return
	 * @throws Exception 
	 */
	public boolean updateBillState(String autoid,String state) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps=null;
		try {
			String sql = "";
			if(state.equals("end")){
				sql = " update oa_bill_manage set state='作废' where autoid = ?";
			}else if(state.equals("start")){
				sql = " update oa_bill_manage set state='启用中' where autoid = ?";
			}
			ps=conn.prepareStatement(sql);
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
}