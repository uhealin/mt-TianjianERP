package com.matech.audit.service.sysMenuManger;

import java.sql.*;
import java.util.*;

import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;

public class sysMenuMangerService {
	private Connection conn = null;

	public sysMenuMangerService(Connection conn) {
		this.conn = conn;
	}

	public void delAMenu(int id) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try {
			if (id != 0) {

				ps = conn.prepareStatement("delete from s_sysmenu where id="
						+ id + "");
				ps.execute();
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			//  DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	public void AddOrModifyAMenu(String XML, String act) throws Exception {
		ASFuntion asf = new ASFuntion();
		DbUtil.checkConn(conn);

		System.out.println(asf.showNull(act).equals("ad"));
		PreparedStatement ps = null;
		String sql = "";
		try {

			if (asf.showNull(act).equals("ad")) {
				sql = "INSERT INTO s_sysmenu(Menu_ID,PARENTID,CTYPE,DEPTH,TARGET,ACT,NAME,HELPACT,ISVALIDATE,ActiveX_method,power) VALUES('"
						+ asf.getXMLData(XML, "menu_id")
						+ "','"
						+ asf.getXMLData(XML, "parentid")
						+ "','"
						+ asf.getXMLData(XML, "type")
						+ "',"
						+ asf.getXMLData(XML, "depth")
						+ ",'"
						+ asf.getXMLData(XML, "target")
						+ "','"
						+ asf.getXMLData(XML, "act")
						+ "','"
						+ asf.getXMLData(XML, "name")
						+ "','"
						+ asf.getXMLData(XML, "helpact")
						+ "','"
						+ asf.getXMLData(XML, "isvalidate")
						+ "','"
						+ asf.getXMLData(XML, "ActiveX_method")
						+ "','"
						+ asf.getXMLData(XML, "power")
						+ "')";
				System.out.println("zyq=" + sql);
				ps = conn.prepareStatement(sql);

				ps.execute();
			} else if (asf.showNull(act).equals("ed")) {
				ps = conn.prepareStatement("update s_sysmenu set Menu_ID='"
						+ asf.getXMLData(XML, "menu_id") + "',parentid='"
						+ asf.getXMLData(XML, "parentid") + "',ctype='"
						+ asf.getXMLData(XML, "type") + "',depth="
						+ asf.getXMLData(XML, "depth") + ",target='"
						+ asf.getXMLData(XML, "target") + "',act='"
						+ asf.getXMLData(XML, "act") + "',name='"
						+ asf.getXMLData(XML, "name") + "',helpact='"
						+ asf.getXMLData(XML, "helpact") + "',isvalidate='"
						+ asf.getXMLData(XML, "isvalidate") +"',ActiveX_method='"
						+ asf.getXMLData(XML, "ActiveX_method") +"',power='"
						+ asf.getXMLData(XML, "power") + "' where id="
						+ asf.getXMLData(XML, "ID"));
				ps.execute();
			}
			
			ps = conn.prepareStatement("update s_sysmenu set power = id ");
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			// DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public String getAMenuDetail(int id) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String str = "";

			ps = conn.prepareStatement("select * from s_sysmenu where id=" + id
					+ "");
			rs = ps.executeQuery();
			if (rs.next()) {
				str = "<ID>" + rs.getInt("ID") + "</ID><menu_id>"
						+ rs.getString("menu_id") + "</menu_id><parentid>"
						+ rs.getString("parentid") + "</parentid><depth>"
						+ rs.getInt("depth") + "</depth><type>"
						+ rs.getString("ctype") + "</type><name>"
						+ rs.getString("name") + "</name><act>"
						+ rs.getString("act") + "</act><target>"
						+ rs.getString("target") + "</target><helpact>"
						+ rs.getString("helpact") + "</helpact><isvalidate>"
						+ rs.getString("isvalidate") + "</isvalidate><ActiveX_method>"
						+ rs.getString("ActiveX_method") + "</ActiveX_method><power>"
						+ rs.getString("power") + "</power>";
			}
			return str;
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}
	
	public Vector getParent() throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			Vector vc = new Vector();

			ps = conn
					.prepareStatement("SELECT DISTINCT menu_id,name FROM s_sysmenu WHERE DEPTH<>0 ORDER BY menu_id");
			rs = ps.executeQuery();
			while (rs.next()) {
				vc.add(new String[]{rs.getString(1),rs.getString(2)});
			}
			return vc;
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}
	
	public Vector getParentid() throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			Vector vc = new Vector();

			ps = conn
					.prepareStatement("SELECT DISTINCT menu_id FROM s_sysmenu WHERE DEPTH<>0 ORDER BY menu_id");
			rs = ps.executeQuery();
			while (rs.next()) {
				vc.add(rs.getString(1));
				
			}
			return vc;
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	/**
	 * 根据menu_id得到权限id
	 *
	 * @param parentid
	 *            部门的autoid,机构ID为555555
	 * @return
	 * @throws Exception
	 */

	public String getIdByMenuId(String menuid) throws Exception {

		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String power = "";
			String sql = "select id from s_sysmenu where parentid!='000' and  menu_id="
					+ menuid;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				power = rs.getString(1);
			}
			return power;
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	/**
	 * 设置加密狗版本
	 * @param id
	 * @param dogversions
	 * @return
	 * @throws Exception
	 */
	public void setDogversions(String id, String dogversions) throws Exception {

		DbUtil.checkConn(conn);
		PreparedStatement ps = null;

		try {

			String[] dogVersion = dogversions.split(",");

			String sql = "delete from k_menuversion where menuid = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, id);
			ps.execute();

			sql = "insert into k_menuversion values(?,?,?)";

			ps = conn.prepareStatement(sql);

			for (int i = 0; i < dogVersion.length; i++) {
				ps.setString(1, id);
				ps.setString(2, dogVersion[i]);
				ps.setString(3, "");

				ps.execute();
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "设置菜单加密狗版本权限出错", e);
			throw new MatechException("设置菜单加密狗版本权限出错：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 返回菜单的路径
	 * @param menuId
	 * @return
	 * @throws Exception
	 */
	public String getMenuAct(String menuId) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;

		String act = "error_page.jsp";

		try {
			String sql = "select act from s_sysmenu where id=? ";

			ps = conn.prepareStatement(sql);
			ps.setString(1, menuId);
			rs = ps.executeQuery();
			if (rs.next()) {
				act = new ASFuntion().showNull(rs.getString(1));

				if ("".equals(act)) {
					act = "error_page.jsp";
				}

				if (act.indexOf("?") >= 0) {
					act = act + "&";
				} else {
					act = act + "?";
				}

				act += "menuid=" + menuId;
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "获取菜单路径出错", e);
			throw new MatechException("获取菜单路径出错：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return act;
	}

	/**
	 * 返回菜单的路径
	 * @param menuId
	 * @return
	 * @throws Exception
	 */
	public String getMenuValidate(String menuId) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;

		String validate = "-1";

		try {
			String sql = "select isvalidate from s_sysmenu where id=? ";

			ps = conn.prepareStatement(sql);
			ps.setString(1, menuId);
			rs = ps.executeQuery();
			if (rs.next()) {
				validate = rs.getString(1);
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "获取菜单人员角色", e);
			throw new MatechException("获取菜单人员角色：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return "".equals(validate) ? "-1" : validate;
	}
	
	/**
	 * 记录菜单状态
	 * @param userId
	 * @param menuId
	 * @param state
	 * @throws Exception
	 */
	public void recordMenuState(String userId, String menuId, String state) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = "delete from k_user_menu_state where userId = '" + userId + "' and menuId = '" + menuId + "'";
			ps = conn.prepareStatement(sql);
			ps.executeUpdate();
			
			sql = "insert into k_user_menu_state(id,userId,menuId,state) values(null,?,?,?)";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setString(2, menuId);
			ps.setString(3, state);
			ps.executeUpdate();
			
		} catch (Exception e) {
			Debug.print(Debug.iError, "记录菜单状态", e);
			throw new MatechException("记录菜单状态：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public String getMenuState(String userId, String menuId) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = "select state from k_user_menu_state where userId = '" + userId + "' and menuId = '" + menuId + "'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			if(rs.next()) {
				return rs.getString(1);
			}
			
		} catch (Exception e) {
			Debug.print(Debug.iError, "记录菜单状态", e);
			throw new MatechException("记录菜单状态：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return null;
	}
}