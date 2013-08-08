package com.matech.audit.service.oa.practicalbalance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.oa.practicalbalance.model.PracticalBalanceTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
/**
 * 实际结算登记oa_practicalbalance
 * @author Administrator
 *
 */
public class PracticalBalanceService {
	private Connection conn = null;

	public PracticalBalanceService(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 删除信息
	 * 
	 * @param id
	 * @throws MatechException
	 */
	public void del(String id) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		try {
			sql = "delete from oa_practicalbalance where autoid = '" + id
					+ "' ";
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 添加信息
	 * 
	 * @param pbt
	 * @throws MatechException
	 */
	public void add(PracticalBalanceTable pbt) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		try {
			sql = "insert into oa_practicalbalance (" +
			"cid, firstparty, firstpartyid, secondparty, secondpartyid, " +
			"bargaindate, bargaintype, bargainmoney, invoicenumber,bargainplan, " +
			"loginid, loginname, logindate, property, projectid, " +
			"billMoney, recipient,receiptState,invoiceState " +
			") values (" +
			"?,?,?,?,?," +
			"?,?,?,?,?," +
			"?,?,now(),?,?," +
			"?,?,?,?" +
			") ";
			int i = 1;
			ps = conn.prepareStatement(sql);
			ps.setString(i++, pbt.getCid());
			ps.setString(i++, pbt.getFirstparty());
			ps.setString(i++, pbt.getFirstpartyid());
			ps.setString(i++, pbt.getSecondparty());
			ps.setString(i++, pbt.getSecondpartyid());
			
			ps.setString(i++, pbt.getBargaindate());
			ps.setString(i++, pbt.getBargaintype());
			ps.setString(i++, pbt.getBargainmoney());
			ps.setString(i++, pbt.getInvoicenumber());
			ps.setString(i++, pbt.getBargainplan());
			
			ps.setString(i++, pbt.getLoginid());
			ps.setString(i++, pbt.getLoginName());
			ps.setString(i++, pbt.getProperty());
			ps.setString(i++, pbt.getProjectid());
			
			ps.setString(i++, pbt.getBillMoney());
			ps.setString(i++, pbt.getRecipient());
			ps.setString(i++, pbt.getReceiptState());
			ps.setString(i++, pbt.getInvoiceState());
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 根据指定ID返回整条记录信息
	 * 
	 * @param id
	 * @return
	 * @throws MatechException
	 */
	public PracticalBalanceTable getPracticalBalance(String id) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		PracticalBalanceTable pbt = new PracticalBalanceTable();
		try {
			ASFuntion CHF = new ASFuntion();
			
			sql = "select * from oa_practicalbalance  where autoid = '"+ id + "' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				pbt.setAutoid(CHF.showNull(rs.getString("autoid"))); 
				pbt.setCid(CHF.showNull(rs.getString("cid")));
				pbt.setFirstparty(CHF.showNull(rs.getString("firstparty")));
				pbt.setFirstpartyid(CHF.showNull(rs.getString("firstpartyid")));
				pbt.setSecondparty(CHF.showNull(rs.getString("secondparty")));
				
				pbt.setSecondpartyid(CHF.showNull(rs.getString("secondpartyid")));
				pbt.setBargaindate(CHF.showNull(rs.getString("bargaindate")));
				pbt.setBargaintype(CHF.showNull(rs.getString("bargaintype")));
				pbt.setBargainmoney(CHF.showNull(rs.getString("bargainmoney")));
				pbt.setInvoicenumber(CHF.showNull(rs.getString("invoicenumber")));
				
				pbt.setBargainplan(CHF.showNull(rs.getString("bargainplan")));
				pbt.setLoginid(CHF.showNull(rs.getString("loginid")));
				pbt.setLoginName(CHF.showNull(rs.getString("loginName")));
				pbt.setLogindate(CHF.showNull(rs.getString("logindate")));
				pbt.setProperty(CHF.showNull(rs.getString("property")));
				
				pbt.setProjectid(CHF.showNull(rs.getString("projectid")));
				pbt.setBillMoney(CHF.showNull(rs.getString("billMoney")));
				pbt.setRecipient(CHF.showNull(rs.getString("recipient")));
				pbt.setReceiptState(CHF.showNull(rs.getString("receiptState")));
				pbt.setInvoiceState(CHF.showNull(rs.getString("invoiceState")));
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return pbt;
	}

	/**
	 * 更新信息
	 * 
	 * @param pbt
	 * @throws MatechException
	 */
	public void update(PracticalBalanceTable pbt) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		try {
			sql = " update oa_practicalbalance set " +
			" cid = ?, firstparty = ?, firstpartyid = ?, secondparty = ?, secondpartyid = ?," +
			" bargaindate = ?, bargaintype = ?, bargainmoney = ?, invoicenumber = ?, bargainplan = ?," +
			" property = ?, projectid = ?, billMoney = ? ,recipient = ? ,receiptState = ?," +
			" invoiceState = ? " +
			" where autoid=?";
			int i = 1;
			ps = conn.prepareStatement(sql);
			ps.setString(i++, pbt.getCid());
			ps.setString(i++, pbt.getFirstparty());
			ps.setString(i++, pbt.getFirstpartyid());
			ps.setString(i++, pbt.getSecondparty());
			ps.setString(i++, pbt.getSecondpartyid());
			
			ps.setString(i++, pbt.getBargaindate());
			ps.setString(i++, pbt.getBargaintype());
			ps.setString(i++, pbt.getBargainmoney());
			ps.setString(i++, pbt.getInvoicenumber());
			ps.setString(i++, pbt.getBargainplan());
			
			ps.setString(i++, pbt.getProperty());
			ps.setString(i++, pbt.getProjectid());
			ps.setString(i++, pbt.getBillMoney());
			ps.setString(i++, pbt.getRecipient());
			ps.setString(i++, pbt.getReceiptState());
			
			ps.setString(i++, pbt.getInvoiceState());
			
			ps.setString(i++, pbt.getAutoid());
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

}
