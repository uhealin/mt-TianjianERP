package com.matech.audit.service.audittypetemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.matech.audit.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;

public class TreeView {

	private Connection conn = null;

	public TreeView(Connection conn) {
		this.conn = conn;
	}

	public String getFullTree(String typeId, String parentaskId) throws Exception {
		if (typeId == null) {
			return "";
		}
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			String sql = "select taskId,taskCode,taskName,isleaf "
						+ " from k_tasktemplate "
						+ " where parenttaskid=? "
						+ " and typeId=? order by taskcode";

			ps = conn.prepareStatement(sql);
			ps.setString(1, parentaskId);
			ps.setString(2, typeId);

			rs = ps.executeQuery();
			StringBuffer sb = new StringBuffer("");
			while (rs.next()) {
				sb.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
				if (rs.getInt(4) == 0) {
					sb.append("<tr style=\"cursor: hand;\">");
					sb.append("<td width=\"1\" height=\"1\" align=\"left\">");
					sb.append("<img onclick=\"getSubTree(" + rs.getString(1) + ");\" id=\"ActImg" + rs.getString(1) + "\" src=\"../images/nofollow.jpg\" width=\"11\" height=\"11\" />");
					sb.append("</td>");
					sb.append("<td align=left valign=\"bottom\" nowrap><span style='color:#0000CC;' onclick=\"doIt(this," + rs.getString(1) + ");\"><font size=2>&nbsp;"
									+ rs.getString(2) + "&nbsp;" + rs.getString(3) + "</font></span></td>");
					sb.append("</tr>");
					sb.append("<tr>");
					sb.append("<td id='subImg" + rs.getString(1)+ "' ></td>");
					sb.append("<td id='subTree" + rs.getString(1) + "'>");
					sb.append(getFullTree(typeId,rs.getString(1)));
					sb.append("</td>");
					sb.append("</tr>");
				} else {
					String picName = "";
					sb.append("<tr style=\"cursor: hand;\">");
					sb.append("<td width=\"27\" height=\"16\" nowrap align=\"left\">");
					String extendName = rs.getString(3);
					if (extendName.lastIndexOf(".") <= -1) {
						continue;
					}

					extendName = extendName.substring(extendName.lastIndexOf("."));

					if (".doc".equalsIgnoreCase(extendName)) {
						picName = "word" + picName + ".gif";
					} else if (".xls".equalsIgnoreCase(extendName)) {
						picName = "excel" + picName + ".gif";
					}

					sb.append("<img id=\"ActImg\" src=\"/AuditSystem/images/office/" + picName + "\" />");
					sb.append("</td>");
					sb.append("<td align=left valign=\"bottom\" nowrap><span style='color:#0000CC;' onclick=\"doOpenIt(this," + rs.getString(1) + ");\"><font size=2>&nbsp;"
									+ rs.getString(2) + "&nbsp;" + rs.getString(3) + "</font></span></td>");
					sb.append("</tr>");
				}
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

	public String getSubTree(String typeId, String parentaskId) throws Exception {
		if (typeId == null) {
			return "";
		}
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			String sql = "select taskId,taskCode,taskName,isleaf "
						+ " from k_tasktemplate "
						+ " where parenttaskid=? "
						+ " and typeId=? "
						+ " order by orderid";

			ps = conn.prepareStatement(sql);
			ps.setString(1, parentaskId);
			ps.setString(2, typeId);

			rs = ps.executeQuery();
			StringBuffer sb = new StringBuffer("");
			while (rs.next()) {
				sb.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
				if (rs.getInt(4) == 0) {
					sb.append("<tr style=\"cursor: hand;\">");
					sb.append("<td width=\"1\" height=\"1\" align=\"right\">");
					sb.append("<img onclick=\"getSubTree(" + rs.getString(1) + ");\" id=\"ActImg" + rs.getString(1) + "\" src=\"../images/plus.jpg\" width=\"11\" height=\"11\" />");
					sb.append("</td>");
					sb.append("<td align=left valign=\"bottom\" nowrap><span style='color:#0000CC;' onclick=\"doIt(this," + rs.getString(1) + ");\"><font size=2>&nbsp;" + rs.getString(2) + "&nbsp;" + rs.getString(3) + "</font></span></td>");
					sb.append("</tr>");
				} else {
					String picName = "";
					sb.append("<tr style=\"cursor: hand;\">");
					sb.append("<td width=\"27\" height=\"16\" nowrap align=\"left\">");
					String extendName = rs.getString(3);
					if (extendName.lastIndexOf(".") <= -1) {
						continue;
					}

					extendName = extendName.substring(extendName.lastIndexOf("."));

					if (".doc".equalsIgnoreCase(extendName)) {
						picName = "word" + picName + ".gif";
					} else if (".xls".equalsIgnoreCase(extendName)) {
						picName = "excel" + picName + ".gif";
					}

					sb.append("<img id=\"ActImg\" src=\"/AuditSystem/images/office/" + picName + "\" />");
					sb.append("</td>");
					sb.append("<td align=left valign=\"bottom\" nowrap><span style='color:#0000CC;' onclick=\"doOpenIt(this," + rs.getString(1) + ");\"><font size=2>&nbsp;"
									+ rs.getString(2) + "&nbsp;" + rs.getString(3) + "</font></span></td>");
					sb.append("</tr>");
				}
				sb.append("<tr>");
				sb.append("<td id='subImg" + rs.getString(1) + "' style='display:none'></td>");
				sb.append("<td id='subTree" + rs.getString(1) + "' style='display:none'></td>");
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

	public String getTaskTree(String typeId, String parentaskId) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			String sql = "select taskId,taskCode,taskName,isleaf,ifnull(property,'') "
						+ " from k_tasktemplate "
						+ " where parenttaskid=? "
						+ " and typeId=?"
						+ " and isleaf=0 "
						+ " order by orderid";

			ps = conn.prepareStatement(sql);
			ps.setString(1, parentaskId);
			ps.setString(2, typeId);

			rs = ps.executeQuery();
			StringBuffer sb = new StringBuffer("");
			while (rs.next()) {
				String taskName = rs.getString(3);
				String taskId = rs.getString(1);
				String property = rs.getString(5);

				if(rs.getInt(4) == 0) {

					sb.append("  <tr>");
					sb.append("    <td nowrap vAlign=\"center\" style=\"padding-right:20px;background:#e4e8ef;border:2px solid #CCCCCC;\" height=\"22\"><span style=\"MARGIN-LEFT: 10px\">");
					sb.append("    <span style=\"font-size: 9pt\"><img src=\"/AuditSystem/images/sjx1.gif\">&nbsp;</span>\n");
					sb.append("    <span style=\"font-size: 12px;color:#0000CC\"><a href=\"javascript:goTaskTemplate('" + typeId + "','" + taskId + "','" + property + "');\">" + taskName + "</a></span></span></td>\n");
					sb.append("  </tr>\n");
				}
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

	public String getTaskTreeHTML(String typeId, String parentTaskId) throws Exception {
		if (typeId == null || "".equals(typeId) || parentTaskId == null || "".equals(parentTaskId)) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("<table width=\"178\" style=\"border-collapse:collapse;border:2px solid #CCCCCC; padding:0;\"><tbody>\n");
		String temp = getTaskTree(typeId,parentTaskId);

		if(!"".equals(temp)) {
			sb.append(temp);
		} else {
			return "";
		}

		sb.append("</tbody></table>");
		return sb.toString();
	}

	//ext 模板树
	public List getExtTree(String typeId, String parentaskId,String property,String checked) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		List list = null;
		
		String sql = "";
		try {
			int ii = 0; //区域树无值，返回"";
			list = new ArrayList();
			
			sql = "select taskId,taskCode,taskName,isleaf  " +
			"	from k_tasktemplate " +
			"	where typeid=? " +
			"	and parenttaskid=? " +
			"	and property=?  " +
			"	order by taskid";
			ps = conn.prepareStatement(sql);
			ps.setString(1, typeId);
			ps.setString(2, parentaskId);
			ps.setString(3, property);
			rs = ps.executeQuery();
			
//			TaskTreeNode childTaskNode = null;
			String taskName;
			String taskCode;
			String taskId;
			
			while(rs.next()){
				
				taskId = rs.getString("taskId");
				taskCode = rs.getString("taskCode");
				taskName = rs.getString("taskName");
				
				Map taskMap = new HashMap();
				taskMap.put("taskCode", taskCode);
				taskMap.put("taskName", taskName);
				taskMap.put("taskId", taskId);
				taskMap.put("leaf", rs.getInt("isleaf") == 1);
				taskMap.put("id", taskId);
				
				if(checked != null && !"".equals(checked)){
					taskMap.put("checked", "true".equals(checked));
				}
				
				if (rs.getInt("isleaf") == 0) {
					//非叶子 
					taskMap.put("text", taskCode + " " + taskName);
					taskMap.put("children" ,getExtTree( typeId,  taskId, property, checked));
				} else {
					//叶子

					String picName = "";
					String extendName = taskName;

					if (extendName.lastIndexOf(".") <= -1) {
						continue;
					}

					extendName = extendName.substring(extendName.lastIndexOf("."));
					boolean otherFile = false ;    //不是word或excel文件
					if (".doc".equalsIgnoreCase(extendName)) {
						picName = "word" + picName + ".gif";
					} else if (".xls".equalsIgnoreCase(extendName)) {
						picName = "excel" + picName + ".gif";
					}else {
						otherFile = true ;
						picName = "attachFile.gif";
					}
					taskMap.put("icon","/AuditSystem/img/" + picName);

					String text = taskCode + " " + taskName ;

					taskMap.put("text", text);

				}
				list.add(taskMap);
				ii++;
			}
			
			
			if(ii == 0) return null;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return list;
	}
	
	public static void main(String[] args) throws Exception {
		Connection c = new DBConnect().getConnect("");
		System.out.println(new TreeView(c).getTaskTreeHTML("3", "0"));
	}
}
