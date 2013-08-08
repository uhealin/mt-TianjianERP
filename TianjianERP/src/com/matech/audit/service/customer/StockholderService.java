package com.matech.audit.service.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.matech.audit.service.customer.model.Stockholder;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class StockholderService {

	private Connection conn;

	public StockholderService(Connection conn) throws Exception {
		DbUtil.checkConn(conn);
		this.conn = conn;

	}

	public void deleteByCustomerid(String customerid) throws Exception {
		PreparedStatement ps = null;

		customerid = new ASFuntion().showNull(customerid);

		String sql = "delete from asdb.k_stockholder where customerid = ?";

		try {

			ps = conn.prepareStatement(sql);
			ps.setString(1, customerid);
			ps.execute();
		} catch (Exception e) {
			throw new Exception("数据清除出错！");
		} finally {
			DbUtil.close(ps);
		}
	}

	public void save(List list, String customerid) throws Exception {

		Iterator it = list.iterator();
		PreparedStatement ps = null;

		try {
			String sql = "insert into asdb.k_stockholder (customerid, name, totalFund, registerFund, percentOfFund, factFund, percentage) values(?,?,?,?,?,?,?)";

			Stockholder holder = null;
			while (it.hasNext()) {
				holder = (Stockholder) it.next();

				ps = conn.prepareStatement(sql);
				ps.setString(1, customerid);
				ps.setString(2, holder.getName());
				ps.setString(3, holder.getTotalFund());
				ps.setString(4, holder.getRegisterFund());
				ps.setString(5, holder.getPercentOfFund());
				ps.setString(6, holder.getFactFund());
				ps.setString(7, holder.getPercentage());
				ps.execute();

			}
		} catch (Exception e) {
			throw new Exception("添加数据出错！");
		} finally {
			DbUtil.close(ps);
		}
	}

	public List getStockholderByCustomerid(String customerid) throws Exception {

		List list = new ArrayList();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			Stockholder holder = null;
			String sql = "select * from asdb.k_stockholder where customerid = ? order by percentage desc,name asc";

			ps = conn.prepareStatement(sql);
			ps.setString(1, customerid);
			rs = ps.executeQuery();

			while (rs.next()) {
				holder = new Stockholder();

				holder.setName(rs.getString("name"));
				holder.setTotalFund(rs.getString("totalFund"));
				holder.setRegisterFund(rs.getString("registerFund"));
				holder.setPercentOfFund(rs.getString("percentOfFund"));
				holder.setFactFund(rs.getString("factFund"));
				holder.setPercentage(rs.getString("percentage"));

				list.add(holder);
			}

		} catch (Exception e) {
			throw new Exception("读取数据出错！");
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return list;
	}

	public void saveStockHolderList(String stockowner, String customerid)
			throws Exception {
		PreparedStatement ps = null;
		try {

			String sql = "update asdb.k_customer set stockowner = ? where DepartID = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, stockowner);
			ps.setString(2, customerid);
			ps.execute();

		} catch (Exception e) {
			throw new Exception("更新数据出错！");
		} finally {

			DbUtil.close(ps);
		}
	}

	public void deleteStockHolderList(String customerid) throws Exception {
		PreparedStatement ps = null;
		try {

			String sql = "update asdb.k_customer set stockowner = '' where DepartID = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, customerid);
			ps.execute();
		} catch (Exception e) {
			throw new Exception("删除数据出错！");
		} finally {
			DbUtil.close(ps);
		}

	}

	public String getCurname(String customerid) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		String curname = "";
		String register = "";

		try {

			String sql = "select register,curname from asdb.k_customer where DepartID = ?";

			ps = conn.prepareStatement(sql);
			ps.setString(1, customerid);

			rs = ps.executeQuery();

			while(rs.next()){
				register = rs.getString(1);
				curname = rs.getString(2);
			}

			return register+","+curname;

		} catch (Exception e) {
			throw new Exception("删除数据出错！");
		} finally {
			DbUtil.close(ps);
		}
	}

}
