package com.matech.audit.service.contract;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.matech.audit.service.contract.model.ContractMoneyRecord;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class ContractMoneyService {
	private Connection conn = null;
	ASFuntion CHF = new ASFuntion();
	
	public ContractMoneyService(Connection conn){				
		this.conn =conn;
	}
	
	/**
	 * 增加合同收款记录
	 * @param getMoney
	 * @return
	 */
	public boolean addGetMoney(ContractMoneyRecord getMoney,String getOrpay)
			throws Exception {
		DbUtil.checkConn(conn);

		PreparedStatement ps = null;
		
		try {
			String sql = "insert into oa_contractpay (customerid,customername,company,planmoney," 
					   + "plandate,factmoney,factdate,moneytype,recorder,recorddate,getorpay,memo)" 
					   + " values(?,?,?,?,?,?,?,?,?,?,?,?)";

			ps = conn.prepareStatement(sql);
			
			ps.setString(1, getMoney.getCustomerid());
			ps.setString(2, getMoney.getCustomername());
			ps.setString(3, getMoney.getCompany());
			ps.setString(4, getMoney.getPlanmoney());
			ps.setString(5, getMoney.getPlandate());
			ps.setString(6, getMoney.getFactmoney());
			ps.setString(7, getMoney.getFactdate());
			ps.setString(8, getMoney.getMoneytype());
			ps.setString(9, getMoney.getRecorder());
			ps.setString(10, getMoney.getRecorddate());
			ps.setString(11, getOrpay);
			ps.setString(12, getMoney.getMemo());

			ps.execute();

			return true;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

		return false;

	}
	
	/**
	 * 修改合同收款记录
	 * @param getMoney
	 * @param autoid
	 * @return
	 */
	public boolean updateMoneyRecord(ContractMoneyRecord getMoney,
			String autoid) throws Exception {
		DbUtil.checkConn(conn);

		PreparedStatement ps = null;

		try {
			String sql = "update oa_contractpay set customerid=?,customername=?,company=?,planmoney=?,plandate=?,factmoney=?," 
					   + "factdate=?,moneytype=?,recorder=?,recorddate=?,memo=?"
					   + " where autoid=?";

			ps = conn.prepareStatement(sql);

			ps.setString(1,getMoney.getCustomerid());
			ps.setString(2,getMoney.getCustomername());
			ps.setString(3,getMoney.getCompany());
			ps.setString(4,getMoney.getPlanmoney());
			ps.setString(5,getMoney.getPlandate());
			ps.setString(6,getMoney.getFactmoney());
			ps.setString(7,getMoney.getFactdate());
			ps.setString(8,getMoney.getMoneytype());
			ps.setString(9,getMoney.getRecorder());
			ps.setString(10,getMoney.getRecorddate());
			ps.setString(11,getMoney.getMemo());
			ps.setString(12,autoid);
			
			ps.execute();

			return true;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

		return false;
	}
	
	/**
	 * 删除合同收款记录
	 * 
	 * @param autoid
	 * @return
	 */
	public boolean removeMoneyRecord(String autoid) throws Exception {
		DbUtil.checkConn(conn);

		PreparedStatement ps = null;

		try {
			String sql = "delete from oa_contractpay where autoid=?";

			ps = conn.prepareStatement(sql);

			ps.setString(1, autoid);
			ps.execute();

			return true;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

		return false;
	}

	
	/**
	 * 获得合同收款记录
	 * 
	 * @param autoid
	 * @return
	 */
	public ContractMoneyRecord getMoneyRecord(String autoid)
			throws Exception {
		DbUtil.checkConn(conn);

		PreparedStatement ps = null;
		ResultSet rs = null;

		ContractMoneyRecord getMoney = new ContractMoneyRecord();

		try {
			String sql = "select * from oa_contractpay where autoid=?";

			ps = conn.prepareStatement(sql);

			ps.setString(1, autoid);
			rs = ps.executeQuery();

			while (rs.next()) {
				getMoney.setCustomerid(CHF.showNull(rs.getString(2)));
				getMoney.setCompany(CHF.showNull(rs.getString(4)));
				getMoney.setPlanmoney(CHF.showNull(rs.getString(5)));
				getMoney.setPlandate(CHF.showNull(rs.getString(6)));
				getMoney.setFactmoney(CHF.showNull(rs.getString(7)));
				getMoney.setFactdate(CHF.showNull(rs.getString(8)));
				getMoney.setMoneytype(CHF.showNull(rs.getString(9)));
				getMoney.setRecorder(CHF.showNull(rs.getString(10)));
				getMoney.setRecorddate(CHF.showNull(rs.getString(11)));
				getMoney.setMemo(CHF.showNull(rs.getString(13)));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return getMoney;
	}
	
}
