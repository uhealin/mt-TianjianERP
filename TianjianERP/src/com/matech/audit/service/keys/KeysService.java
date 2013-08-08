package com.matech.audit.service.keys;

/**
 * 对auditKeys模块进行的增加、修改操作，都是针对全局的
 */
import java.sql.*;

import java.util.ArrayList;
import com.matech.audit.service.keys.model.KeysTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.Debug;

public class KeysService {
	private Connection conn = null;

	public KeysService(Connection conn) {
		this.conn = conn;
	}

	public void delAKey(String id, String departID) throws Exception {
		DbUtil.checkConn(conn);
		com.matech.audit.service.keys.KeyValue kv = new com.matech.audit.service.keys.KeyValue();

		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		String sid = "";
		try {
			if (id != null) {

				if (departID == null || "".equals(departID)) {
					departID = "0";
				}

				sql = "select a.autoid from k_key a,k_key b where b.autoid='"
						+ id + "' and a.key1=b.key2 and a.key2=b.key1 ";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if (rs.next()) {
					sid = rs.getString(1);
				}
				sql = "delete from k_key where autoid='" + id
						+ "' or autoid = '" + sid + "'";
				ps = conn.prepareStatement(sql);
				ps.execute();
				ps.execute("Flush tables");

				kv.process(conn, departID);
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public ArrayList getDetail(String id) throws Exception {
		DbUtil.checkConn(conn);

		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList al = new ArrayList();
		try {

			ps = conn
					.prepareStatement("select autoid,key1,key2 from k_key where autoid='"
							+ id + "'");
			rs = ps.executeQuery();
			if (rs.next()) {
				al.add(rs.getString(1));
				al.add(rs.getString(2));
				al.add(rs.getString(3));
			}
			return al;
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 *  检查唯一性
	 * @return boolean
	 */
	public boolean SameSelect(KeysTable kt) throws Exception {
		DbUtil.checkConn(conn);

		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";

		try {

			sql = "select count(*) from k_key where concat(key1,key2)=concat('"
					+ kt.getKey1() + "','" + kt.getKey2()
					+ "') and departid in(0)";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				if (rs.getInt(1) > 0) {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public void AddOrModifyAKey(KeysTable kt, String act, String departID)
			throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try {
			if (departID == null || "".equals(departID)) {
				departID = "0";
			}
			if (!act.equals("ad")) {
				delAKey(kt.getAutoid(), departID);
			}

			KeyValue kv = new KeyValue();
			kv.addKey(kt.getKey1(), kt.getKey2(), departID, false);
			
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}

	}

}
