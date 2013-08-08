package com.matech.audit.service.oa.bargainbalance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.oa.bargainbalance.model.BargainBalanceTable;
import com.matech.framework.pub.db.DbUtil;

public class BargainBalanceService {

	private Connection conn = null;

	public BargainBalanceService(Connection conn) {

		this.conn = conn;
	}

	/**
	 * 添加记录
	 * 
	 * @param blan
	 * @return
	 * @throws Exception
	 */
	public boolean addblan(BargainBalanceTable blan) throws Exception {

		DbUtil.checkConn(conn);
		PreparedStatement ps = null;

		try {

			String sql = "insert into oa_bargainbalance"
					+ "(bargainid,firstparty,secondparty,plandate,planmoney,planfashion,plancondition,checkinname,checkid,checkintime)"
					+ "values(?,?,?,?,?,?,?,?,?,now())";

			ps = conn.prepareStatement(sql);

			ps.setString(1, blan.getBargainid());
			ps.setString(2, blan.getFirstparty());
			ps.setString(3, blan.getSecondparty());
			ps.setString(4, blan.getPlandate());
			ps.setString(5, blan.getPlanmoney());
			ps.setString(6, blan.getPlanfashion());
			ps.setString(7, blan.getPlancondition());
			ps.setString(8, blan.getCheckinname());
			ps.setString(9, blan.getCheckId());

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
	 * 更新记录
	 * 
	 * @param blan
	 * @param autoid
	 * @return
	 * @throws Exception
	 */
	public boolean updateblan(BargainBalanceTable blan, String autoid)
			throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;

		try {

			String sql = "update oa_bargainbalance set bargainid=?,firstparty=?,secondparty=?,plandate=?,planmoney=?,planfashion=?,plancondition=? where autoid=?";

			ps = conn.prepareStatement(sql);

			ps.setString(1, blan.getBargainid());
			ps.setString(2, blan.getFirstparty());
			ps.setString(3, blan.getSecondparty());
			ps.setString(4, blan.getPlandate());
			ps.setString(5, blan.getPlanmoney());
			ps.setString(6, blan.getPlanfashion());
			ps.setString(7, blan.getPlancondition());
			ps.setString(8, autoid);

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
	 * 删除信息
	 * 
	 * @param autoid
	 * @return
	 * @throws Exception
	 */
	public boolean removeblan(String autoid) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;

		try {

			String sql = "delete from oa_bargainbalance where autoid=?";

			ps = conn.prepareStatement(sql);
			ps.setString(1, autoid);

			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

		return false;

	}

	/**
	 * 返回整条记录
	 * 
	 * @param autoid
	 * @return
	 * @throws Exception
	 */
	public BargainBalanceTable getBargainBalance(String autoid)
			throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		BargainBalanceTable bbt = new BargainBalanceTable();
		String sql = "";
		try {
			sql = " select 	autoid, bargainid, firstparty, \n"
					+ "	secondparty, plandate,planmoney,planfashion,  \n"
					+ "	plancondition,checkinname,checkintime,property \n"
					+ "  from oa_bargainbalance where autoid = " + autoid;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				bbt.setAutoid(rs.getInt(1));
				bbt.setBargainid(rs.getString(2));
				bbt.setFirstparty(rs.getString(3));
				bbt.setSecondparty(rs.getString(4));
				bbt.setPlandate(rs.getString(5));
				bbt.setPlanmoney(rs.getString(6));
				bbt.setPlanfashion(rs.getString(7));
				bbt.setPlancondition(rs.getString(8));
				bbt.setCheckinname(rs.getString(9));
				bbt.setCheckintime(rs.getString(10));
				bbt.setProperty(rs.getString(11));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
//			DbUtil.close(conn);
		}
		return bbt;

	}
}