package com.matech.audit.service.question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.matech.framework.pub.db.DbUtil;

public class TreeView {
	private int id = 0;
	private Connection conn = null;

	public TreeView(Connection conn) {
		this.conn = conn;
	}

	public List getJsonTree(String tablename ,String pid) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			List list = new ArrayList();
			//问题
			String sql = "select IsLeaf,id,TypeName from "+tablename+" where ParentID = '"+ pid + "' and id <> '1'";
			System.out.println("sql="+sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int opt = 0;
			while(rs.next()){
				String id = rs.getString("id");
				String TypeName = rs.getString("TypeName");
				String IsLeaf = rs.getString("IsLeaf");
				Map map = new HashMap();
				map.put("id", tablename + id);
				map.put("text", TypeName);
				map.put("leaf", "1".equals(IsLeaf));
				map.put("cls", "folder");
				map.put("pid", id);
				map.put("tablename", tablename);
				list.add(map);
				opt++;
			}
			return list;	
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
	}
	
	public String getSubTree(String pid) throws Exception {
		if (pid == null) {
			return "";
		}
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			ps = conn
					.prepareStatement("select IsLeaf,id,TypeName from p_Questiontype where ParentID = '"
							+ pid + "' and id <> '1'");
			rs = ps.executeQuery();
			int i = 0;
			StringBuffer sb = new StringBuffer("");
			while (rs.next()) {
				i++;
				sb
						.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
				if (rs.getInt(1) == 0) {
					sb.append("<tr onclick=\"getSubTree(" + rs.getInt(2)
							+ ");\" style=\"cursor: hand;\">");
					sb
							.append("<td width=\"20\" height=\"18\" align=\"right\">");
					sb
							.append("<img id=\"ActImg"
									+ rs.getInt(2)
									+ "\" src=\"../images/plus.jpg\" width=\"11\" height=\"11\" />");
					sb.append("</td>");
					sb
							.append("<td align=left valign=\"bottom\" nowrap><a href=\"List.jsp?pid="
									+ rs.getInt(2)
									+ "\" target='PolicyMainFrame' onclick='doIt(this);'><font size=2>&nbsp;"
									+ rs.getString(3) + "</font></a></td>");
					sb.append("</tr>");
				} else {
					sb.append("<tr style=\"cursor: hand;\">");
					sb
							.append("<td width=\"20\" height=\"18\" align=\"right\">");
					sb
							.append("<img id=\"ActImg\" src=\"../images/sjx1.gif\" width=\"11\" height=\"11\" />");
					sb.append("</td>");
					sb
							.append("<td align=left valign=\"bottom\" nowrap><a href=\"List.jsp?pid="
									+ rs.getInt(2)
									+ "\" target='PolicyMainFrame' onclick='doIt(this);'><font size=2>&nbsp;"
									+ rs.getString(3) + "</font></a></td>");
					sb.append("</tr>");
				}
				sb.append("<tr>");
				sb.append("<td id='subImg" + rs.getInt(2)
						+ "' style='display:none'></td>");
				sb.append("<td id='subTree" + rs.getInt(2)
						+ "' style='display:none'></td>");
				sb.append("</tr>");
				sb.append("</table>");
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();

		}

	}
}
