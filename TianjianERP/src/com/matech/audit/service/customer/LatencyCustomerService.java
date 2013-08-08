package com.matech.audit.service.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.matech.audit.service.customer.model.LatencyCustomer;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class LatencyCustomerService {

	private Connection conn = null;
	ASFuntion CHF = new ASFuntion();

	public LatencyCustomerService(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 增加客户潜在项目
	 * 
	 * @param latencyCustomer
	 * @return
	 */
	public boolean addLatencyCustomer(LatencyCustomer latencyCustomer,String customerId)
			throws Exception {
		DbUtil.checkConn(conn);

		PreparedStatement ps = null;

		try {
			String sql = "insert into oa_latencyCustomer (customerId,projectId,projectInformation,planTime,viable,recoder,recodeTime,nextDenote,nextPrincipal,denotePerson,denoteTime)"
					+ " values (?,?,?,?,?,?,?,?,?,?,?)";

			ps = conn.prepareStatement(sql);

			ps.setString(1, customerId);
			ps.setString(2, latencyCustomer.getProjectId());
			ps.setString(3, latencyCustomer.getProjectInformation());
			ps.setString(4, latencyCustomer.getPlanTime());
			ps.setString(5, latencyCustomer.getViable());
			ps.setString(6, latencyCustomer.getRecoder());
			ps.setString(7, latencyCustomer.getRecodeTime());
			ps.setString(8, latencyCustomer.getNextDenote());
			ps.setString(9, latencyCustomer.getNextPrincipal());
			ps.setString(10, latencyCustomer.getDenotePerson());
			ps.setString(11, latencyCustomer.getDenoteTime());

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
	 * 修改客户潜在项目
	 * 
	 * @param latencyCustomer
	 * @param autoid
	 * @return
	 */
	public boolean updateLatencyCustomer(LatencyCustomer latencyCustomer,
			String autoid) throws Exception {
		DbUtil.checkConn(conn);

		PreparedStatement ps = null;

		try {
			String sql = "update oa_latencyCustomer set projectId=?, projectInformation=?,planTime=?,viable=?,recoder=?,"
					+ "recodeTime=?,nextDenote=?,nextPrincipal=?,denotePerson=?,denoteTime=? "
					+ "where autoid=?";

			ps = conn.prepareStatement(sql);

			ps.setString(1, latencyCustomer.getProjectId());
			ps.setString(2, latencyCustomer.getProjectInformation());
			ps.setString(3, latencyCustomer.getPlanTime());
			ps.setString(4, latencyCustomer.getViable());
			ps.setString(5, latencyCustomer.getRecoder());
			ps.setString(6, latencyCustomer.getRecodeTime());
			ps.setString(7, latencyCustomer.getNextDenote());
			ps.setString(8, latencyCustomer.getNextPrincipal());
			ps.setString(9, latencyCustomer.getDenotePerson());
			ps.setString(10, latencyCustomer.getDenoteTime());
			ps.setString(11, autoid);

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
	 * 删除客户潜在项目
	 * 
	 * @param autoid
	 * @return
	 */
	public boolean removeLatencyCustomer(String autoid) throws Exception {
		DbUtil.checkConn(conn);

		PreparedStatement ps = null;

		try {
			String sql = "delete from oa_latencyCustomer where autoid=?";

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
	 * 获得客户潜在项目
	 * 
	 * @param autoid
	 * @return
	 */
	public LatencyCustomer getLatencyCustomer(String autoid)
			throws Exception {
		DbUtil.checkConn(conn);

		PreparedStatement ps = null;
		ResultSet rs = null;

		LatencyCustomer latencyCustomer = new LatencyCustomer();

		try {
			String sql = "select projectId,projectInformation,planTime,viable,recoder,recodeTime,nextDenote,nextPrincipal,"
					   + "denotePerson,denoteTime from oa_latencyCustomer where autoid=?";
			
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, autoid);
			rs = ps.executeQuery();
			
			while(rs.next()){
				latencyCustomer.setProjectId(CHF.showNull(rs.getString(1)));
				latencyCustomer.setProjectInformation(CHF.showNull(rs.getString(2)));
				latencyCustomer.setPlanTime(CHF.showNull(rs.getString(3)));
				latencyCustomer.setViable(CHF.showNull(rs.getString(4)));
				latencyCustomer.setRecoder(CHF.showNull(rs.getString(5)));
				latencyCustomer.setRecodeTime(CHF.showNull(rs.getString(6)));
				latencyCustomer.setNextDenote(CHF.showNull(rs.getString(7)));
				latencyCustomer.setNextPrincipal(CHF.showNull(rs.getString(8)));
				latencyCustomer.setDenotePerson(CHF.showNull(rs.getString(9)));
				latencyCustomer.setDenoteTime(CHF.showNull(rs.getString(10)));
			}
					
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return latencyCustomer;
	}

}
