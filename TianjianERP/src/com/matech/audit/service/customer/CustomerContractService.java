package com.matech.audit.service.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.matech.audit.service.customer.model.CustomerContract;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class CustomerContractService {

	private Connection conn = null;
	ASFuntion CHF = new ASFuntion();

	public CustomerContractService(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 增加客户合同记录
	 * 
	 * @param customerContract
	 * @return
	 */
	public boolean addLatencyCustomer(CustomerContract customerContract,String customerId)
			throws Exception {
		DbUtil.checkConn(conn);

		PreparedStatement ps = null;

		try {
			String sql = "insert into oa_customercontract(customerId,contractId,contractMan,contractDate,salory,validTime,"
					+ "contractAdjunct,mome,recoder,recodeTime) values(?,?,?,?,?,?,?,?,?,?)";

			ps = conn.prepareStatement(sql);

			ps.setString(1, customerId);
			ps.setString(2, customerContract.getContractId());
			ps.setString(3, customerContract.getContractMan());
			ps.setString(4, customerContract.getContractDate());
			ps.setString(5, customerContract.getSalory());
			ps.setString(6, customerContract.getValidTime());
			ps.setString(7, customerContract.getContractAdjunct());
			ps.setString(8, customerContract.getMome());
			ps.setString(9, customerContract.getRecoder());
			ps.setString(10, customerContract.getRecodeTime());

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
	 * 修改客户合同记录
	 * 
	 * @param customerContract
	 * @param autoid
	 * @return
	 */
	public boolean updateLatencyCustomer(CustomerContract customerContract,
			String autoid) throws Exception {
		DbUtil.checkConn(conn);

		PreparedStatement ps = null;

		try {
			String sql = "update oa_customercontract set contractId=?,contractMan=?,contractDate=?,salory=?,validTime=?,"
					+ "contractAdjunct=?,mome=?,recoder=?,recodeTime=? where autoid=?";

			ps = conn.prepareStatement(sql);

			ps.setString(1, customerContract.getContractId());
			ps.setString(2, customerContract.getContractMan());
			ps.setString(3, customerContract.getContractDate());
			ps.setString(4, customerContract.getSalory());
			ps.setString(5, customerContract.getValidTime());
			ps.setString(6, customerContract.getContractAdjunct());
			ps.setString(7, customerContract.getMome());
			ps.setString(8, customerContract.getRecoder());
			ps.setString(9, customerContract.getRecodeTime());
			ps.setString(10, autoid);

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
	 * 删除客户合同记录
	 * 
	 * @param autoid
	 * @return
	 */
	public boolean removeLatencyCustomer(String autoid) throws Exception {
		DbUtil.checkConn(conn);

		PreparedStatement ps = null;

		try {
			String sql = "delete from oa_customercontract where autoid=?";

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
	 * 获得客户合同记录
	 * 
	 * @param autoid
	 * @return
	 */
	public CustomerContract getCustomerContract(String contractId)
			throws Exception {
		DbUtil.checkConn(conn);

		PreparedStatement ps = null;
		ResultSet rs = null;

		CustomerContract customerContract = new CustomerContract();

		try {
			String sql = "select * from oa_customercontract where autoid=?";

			ps = conn.prepareStatement(sql);

			ps.setString(1, contractId);
			rs = ps.executeQuery();

			while (rs.next()) {
				customerContract.setContractId(CHF.showNull(rs.getString(3)));
				customerContract.setContractMan(CHF.showNull(rs.getString(4)));
				customerContract.setContractDate(CHF.showNull(rs.getString(5)));
				customerContract.setSalory(CHF.showNull(rs.getString(6)));
				customerContract.setValidTime(CHF.showNull(rs.getString(7)));
				customerContract.setContractAdjunct(CHF.showNull(rs.getString(8)));
				customerContract.setMome(CHF.showNull(rs.getString(9)));
				customerContract.setRecoder(CHF.showNull(rs.getString(10)));
				customerContract.setRecodeTime(CHF.showNull(rs.getString(11)));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return customerContract;
	}

}
