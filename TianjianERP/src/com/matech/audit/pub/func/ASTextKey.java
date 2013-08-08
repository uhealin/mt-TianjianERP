package com.matech.audit.pub.func;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.framework.pub.db.DbUtil;

public class ASTextKey {
	private Connection conn = null;
	public ASTextKey(Connection conn) {
		this.conn = conn;
	}

	public String getACurrRate(String acc) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String str = "";
		try {
			String sql = "select CurrName from asdb.c_accpackage where AccPackageID = '"
					+ acc + "'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				str = rs.getString(1);
			}
			return str;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);

		}

	}
	
	public String TextCustomerName(String cid) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String result = "";
		try {
			
			String sql = "select DepartName from asdb.k_customer where departid='"
					+ cid + "'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getString(1);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	//会计制度类型 信息
	public String TextCustomerType(String cid) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String result = "";
		try {
			
			String sql = "select IndustryName from asdb.k_customer a left join asdb.k_industry b on a.VocationID=b.IndustryID where a.departid='"
					+ cid + "'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getString(1);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
}
