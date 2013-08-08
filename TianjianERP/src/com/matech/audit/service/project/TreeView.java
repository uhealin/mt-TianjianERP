package com.matech.audit.service.project;

import com.matech.audit.pub.db.DBConnect;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.util.*;


public class TreeView {
	private int id = 0;
	private Map map ;

	public TreeView() {
	}



	public void setUsrMap(String ProID)throws Exception{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map map = new HashMap();
		try {
			conn = new DBConnect().getConnect("");
			String sql = "select * from k_user a INNER join k_accright b on b.DepartID = (select customerid from z_project where projectid='"+ProID+"') and (concat('[',a.departmentid,']')=b.userid or a.id=b.userid)";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				map.put(rs.getString("id"), rs.getString("name"));
			}
		//	org.util.Debug.prtOut(map.isEmpty());
			if(map.isEmpty())map.put("00", "公开");
			this.map = map;

		}catch (Exception e) {
			e.printStackTrace();

		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}

	}

	public String isUsrPop(String UID){
		String result = "无权访问客户";
		Set coll = map.keySet();
		for (Iterator iter = coll.iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			String value = (String) map.get(key);
			if(key.equals("00")){
				result = "公开";
				break;
			}
			if(UID.equals(key)){
				result = "已授权";
				break;
			}
		}
		return result;
	}
	public String getSubTree() throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new DBConnect().getConnect("");
			String sql = "select DepartID,DepartName FROM k_customer where Property=2";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int i = 0;
			StringBuffer sb = new StringBuffer("");
			while (rs.next()) {
				sb
						.append("<table  width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
				sb.append("<tr style=\"cursor: hand;\">");
				sb.append("<td onclick=\"getSubTree('" + rs.getString(1)
						+ "');\" width=\"100%\" height=\"18\" align=\"left\">");
				sb
						.append("<img id=\"ActImg"
								+ rs.getString(1)
								+ "\" src=\"../images/plus.jpg\" width=\"11\" height=\"11\" />");
				sb.append(rs.getString(2) + "</td>");
				sb.append("</tr>");

				sb.append("<tr>");

				sb.append("<td id =\"sonParent" + rs.getString(1)
						+ "\" style=\"display:none\" >");
				sb.append("</td>");

				sb.append("</tr>");

				sb.append("</table>");
			}
			// rs.close();//在下面的finally里关
			// ps.close();
			// conn.close();
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}

	}

	public String getSonTree(String args) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new DBConnect().getConnect("");
			String sql = "select DepartID,DepartName FROM k_customer";
			ps = conn
					.prepareStatement("select id,name from k_user where DepartID=?");
			ps.setString(1, args);
			rs = ps.executeQuery();
			int i = 0;
			StringBuffer sb = new StringBuffer("");
			sb
					.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
			while (rs.next()) {

				sb.append("<tr style=\"cursor: hand;\">");
				sb.append("<td onclick=\"getUser('" + rs.getString(1) + "','"
						+ rs.getString(2)
						+ "');\" width=\"100%\" height=\"18\" align=\"left\">");
				sb.append("<img src=\"/AuditSystem/images/sjx1.gif\">");
				sb.append(rs.getString(2) + "</td>");
				sb.append("</tr>");

			}
			rs.last();
			if (rs.getRow() <= 0) {
				sb.append("<tr><td>&nbsp;&nbsp;&nbsp;没有数据</td></tr>");
			}
			sb.append("</table>");
			// rs.close(); //在下面的finally里关
			// ps.close();
			// conn.close();
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}

	}

	public String getTree(String args) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new DBConnect().getConnect("");
			return this.treeRecursion(conn, args);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}
	}

	private String treeRecursion(Connection conn, String dpID) throws Exception {
		String sql = "select autoid,departname,url from k_department where  parentid=?";
		String sql2 = "select id,name from k_user where departmentid=? and state=0";
		
		String sql3 = "select a.id,a.rolename,a.property "
					+ "from k_role a,k_userrole b "
					+ "where userid = ? "
					+ "and b.rid=a.id "
					+ "and a.property = "
					+ "( "
					+ "    select max(a.property ) "
					+ "    from k_role a,k_userrole b "
					+ "    where userid = ? "
					+ "    and b.rid=a.id "
					+ ")";

		//如果上级部门为"555555",则默认加上个空部门
		if("555555".equals(dpID)) {
			sql = "select '' as autoid,'无部门人员' as departname,'' as url union " + sql;
		}
		
		StringBuffer sb = new StringBuffer("");
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSet rs2 = null;

		String userRoleName = "";
		
		try {		
			ps = conn.prepareStatement(sql);
			ps.setString(1, dpID);
			rs = ps.executeQuery();
	
			int i = 0;
			
			sb.append("<table   border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
			while (rs.next()) {
				String url=rs.getString("url");
				if("".equals(url)){
					sb.append("<tr style=\"cursor: hand;\" onclick=\"getSubTree('"
							+ rs.getString(1) + "');\" >");
					sb.append("<td   height=\"18\" width=\"15\" align=\"left\">");
					sb
							.append("<img id=\"ActImg"
									+ rs.getString(1)
									+ "\" src=\"../images/plus.jpg\" width=\"11\" height=\"11\" /></td>");
					sb.append("<td align=\"left\">" + rs.getString(2) + "</td>");
	
					sb.append("</tr>");
	
					sb.append("<tr  id =\"sonParent" + rs.getString(1)
							+ "\" style=\"display:none\" >");
	
					sb.append("<td>");
					sb.append("</td>");
					sb.append("<td align=\"left\" >");
					sb.append(this.treeRecursion(conn, rs.getString(1)));
					sb.append("</td>");
					sb.append("</tr>");
				}else{
					sb.append("<tr style=\"cursor: hand;\" onclick=\"getSubTree('"
							+ rs.getString(1) + "');\" >");
					sb.append("<td   height=\"18\" width=\"15\" align=\"left\">");
					sb
							.append("<img id=\"ActImg"
									+ rs.getString(1)
									+ "\" src=\"../images/plus.jpg\" width=\"11\" height=\"11\" /></td>");
					sb.append("<td align=\"left\">" + rs.getString(2) + "</td>");
	
					sb.append("</tr>");
	
					sb.append("<tr  id =\"sonParent" + rs.getString(1)
							+ "\" style=\"display:none\" >");
	
					sb.append("<td>");
					sb.append("</td>");
					sb.append("<td align=\"left\" >");
	
					sb.append("<table   border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
	
					sb.append("<tr style=\"cursor: hand;\" onclick=\"getOuterUser('"+rs.getString("autoid")+"','外部参与人("+rs.getString("departname")+")');\">");
					sb.append("<td   height=\"18\"  width=\"15\" align=\"left\">");
					sb.append("<img src=\"/AuditSystem/images/sjx1.gif\">");
					sb.append("</td>");
					sb.append("<td>");
					sb.append( "<font color='blue'>外部参与人</font></td>");
					sb.append("</tr>");
	
					sb.append("</table>");
	
	
					sb.append("</td>");
					sb.append("</tr>");
				}
	
			}
	
			ps = conn.prepareStatement(sql2);
			ps.setString(1, dpID);
			rs = ps.executeQuery();
			i = 0;
	
			while (rs.next()) {
				String result = isUsrPop(rs.getString(1));
				if("无权访问客户".equals(result)){
					sb.append("<tr style=\"cursor: hand;\" onclick=\"getTempUser('"
							+ rs.getString(1) + "','" + rs.getString(2) + "');\">");
	
					sb.append("<td   height=\"18\"  width=\"15\" align=\"left\">");
					sb.append("<img src=\"/AuditSystem/images/sjx1.gif\">");
					sb.append("</td>");
					sb.append("<td title='【"+rs.getString(2)+"】尚无权访问该保密客户' >");
					sb.append(rs.getString(2) + "<font color='red'>("+result+")</font></td>");
					sb.append("</tr>");
				}else{
					
					ps = conn.prepareStatement(sql3);
					ps.setString(1, rs.getString(1));
					ps.setString(2, rs.getString(1));
					
					rs2 = ps.executeQuery();			
					while(rs2.next()){
						userRoleName+=rs2.getString("rolename")+",";		
					}
					
					if(userRoleName.length()>1){
						userRoleName = userRoleName.substring(0,userRoleName.length()-1);						
					}
										
					sb.append("<tr style=\"cursor: hand;\" onclick=\"getUser('"
							+ rs.getString(1) + "','" + rs.getString(2) + "','" + userRoleName + "');\">");
					sb.append("<td   height=\"18\"  width=\"15\" align=\"left\">");
					sb.append("<img src=\"/AuditSystem/images/sjx1.gif\">");
					sb.append("</td>");
					sb.append("<td>");
					sb.append(rs.getString(2) + "<font color='blue'>("+result+")</font></td>");
					sb.append("</tr>");
					
					userRoleName = "";
				}
			}
	
			sb.append("</table>");
				
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (rs != null)
				rs.close();
			if (rs2 != null)
				rs2.close();
			if (ps != null)
				ps.close();			
		}
		
		return sb.toString();
	}
}
