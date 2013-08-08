package com.matech.audit.service.assitem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.framework.pub.db.DbUtil;

public class AssItemService {

	private Connection conn;

	public AssItemService(Connection conn) {
		this.conn = conn;
	}

	public String getSubTree(String acc) throws Exception {
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			String sql = "select IsLeaf, AccID,AssItemID,AssItemName from c_assitem where AccPackageID='"+acc+"' and ParentAssItemId='' order by assitemid,accid ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int i = 0;
			StringBuffer sb = new StringBuffer("");
			int depth = 0;
			String SubjectID = "";
			String AssItemID = "";
			String AssItemName = "";
			
			while (rs.next()) {
				depth = rs.getInt(1);
				SubjectID = rs.getString(2);
				AssItemID = rs.getString(3);
				AssItemName = rs.getString(4);
				i++;
				
				sb.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
				if (depth == 0) {
					sb.append("<tr style=\"cursor: hand;\">");
					sb.append("<td width=\"20\" height=\"18\" align=\"right\" onclick=\"getSubTree('"
									+ AssItemID + "','"+SubjectID+"');\">");
					sb.append("<img id=\"ActImg"
									+ AssItemID + "_" + SubjectID
									+ "\" src=\"/AuditSystem/images/plus.jpg\" width=\"11\" height=\"11\" />");
					sb.append("</td>");
					sb.append("<td align=left valign=\"bottom\" nowrap>&nbsp;<input type=\"checkbox\" name=\"menuID\" value=\""
									+ AssItemID + "`" + SubjectID
									+ "\" onclick=\"return setCountEnable();\" "
									+ "><span onclick=\"getSubTree('"
									+ AssItemID
									+ "','"+SubjectID+"');\"><font size=2>『"
									+ AssItemID
									+ "』"
									+ AssItemName
									+ "</font>&nbsp;&nbsp;<font color=blue>科目:"+SubjectID+"</font></span></td>");
					sb.append("</tr>");
					sb.append("<tr>");
					sb.append("<td id=\"subImg" + AssItemID + "_" + SubjectID + "\" style=\"display:none\"></td>");
					sb.append("<td id=\"subTree" + AssItemID + "_" + SubjectID + "\" style=\"display:none\">"
//							+ getSubTree(AssItemID,SubjectID, acc)  //用于一次性输出核算体系
							+ "</td>");
					sb.append("</tr>");
					sb.append("</table>");
				} else {
					sb.append("<tr style=\"cursor: hand;\">");
					sb.append("<td width=\"20\" height=\"18\" align=\"right\">");
					sb.append("<img id=\"ActImg\" src=\"/AuditSystem/images/sjx1.gif\" width=\"11\" height=\"11\" />");
					sb.append("</td>");
					sb.append("<td align=left valign=\"bottom\" nowrap>&nbsp;<input type=\"checkbox\" name=\"menuID\" value=\""
									+ AssItemID + "`" + SubjectID
									+ "\" onclick=\"return setCountEnable();\" "
									+ "><font size=2>『"
									+ AssItemID
									+ "』"
									+ AssItemName + "</font>&nbsp;&nbsp;<font color=blue>科目:"+SubjectID+"</font></td>");
					sb.append("</tr>");
					sb.append("<tr>");
					sb.append("<td id=\"subImg" + AssItemID + "_" + SubjectID + "\" style=\"display:none\"></td>");
					sb.append("<td id=\"subTree" + AssItemID + "_" + SubjectID + "\" style=\"display:none\"></td>");
					sb.append("</tr>");
					sb.append("</table>");
				}
				
			}

			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public String getSubTree(String pAssItemID, String AccID, String acc) throws Exception {
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			String sql = "select IsLeaf, AccID,AssItemID,AssItemName from c_assitem where AccPackageID='"+acc+"' and ParentAssItemId='"+pAssItemID+"' and AccID='"+AccID+"'  order by assitemid,accid ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int i = 0;
			StringBuffer sb = new StringBuffer("");
			int depth = 0;
			String SubjectID = "";
			String AssItemID = "";
			String AssItemName = "";
			
			while (rs.next()) {
				depth = rs.getInt(1);
				SubjectID = rs.getString(2);
				AssItemID = rs.getString(3);
				AssItemName = rs.getString(4);
				i++;
				
				sb.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
				if (depth == 0) {
					sb.append("<tr style=\"cursor: hand;\">");
					sb.append("<td width=\"20\" height=\"18\" align=\"right\" onclick=\"getSubTree('"
									+ AssItemID + "','"+SubjectID+"');\">");
					sb.append("<img id=\"ActImg"
									+ AssItemID + "_" + SubjectID
									+ "\" src=\"/AuditSystem/images/plus.jpg\" width=\"11\" height=\"11\" />");
					sb.append("</td>");
					sb.append("<td align=left valign=\"bottom\" nowrap>&nbsp;<input type=\"checkbox\" name=\"menuID\" value=\""
									+ AssItemID + "`" + SubjectID
									+ "\" onclick=\"return setCountEnable();\" "
									+ "><span onclick=\"getSubTree('"
									+ AssItemID
									+ "','"+SubjectID+"');\"><font size=2>『"
									+ AssItemID
									+ "』"
									+ AssItemName
									+ "</font>&nbsp;&nbsp;<font color=blue>科目:"+SubjectID+"</font></span></td>");
					sb.append("</tr>");
					sb.append("<tr>");
					sb.append("<td id=\"subImg" + AssItemID + "_" + SubjectID + "\" style=\"display:none\"></td>");
					sb.append("<td id=\"subTree" + AssItemID + "_" + SubjectID + "\" style=\"display:none\">"
//							+ getSubTree(AssItemID,SubjectID, acc) //用于一次性输出核算体系
							+ "</td>");
					sb.append("</tr>");
					sb.append("</table>");
				} else {
					sb.append("<tr style=\"cursor: hand;\">");
					sb.append("<td width=\"20\" height=\"18\" align=\"right\">");
					sb.append("<img id=\"ActImg\" src=\"/AuditSystem/images/sjx1.gif\" width=\"11\" height=\"11\" />");
					sb.append("</td>");
					sb.append("<td align=left valign=\"bottom\" nowrap>&nbsp;<input type=\"checkbox\" name=\"menuID\" value=\""
									+ AssItemID + "`" + SubjectID
									+ "\" onclick=\"return setCountEnable();\" "
									+ "><font size=2>『"
									+ AssItemID
									+ "』"
									+ AssItemName + "</font>&nbsp;&nbsp;<font color=blue>科目:"+SubjectID+"</font></td>");
					sb.append("</tr>");
					sb.append("<tr>");
					sb.append("<td id=\"subImg" + AssItemID + "_" + SubjectID + "\" style=\"display:none\"></td>");
					sb.append("<td id=\"subTree" + AssItemID + "_" + SubjectID + "\" style=\"display:none\"></td>");
					sb.append("</tr>");
					sb.append("</table>");
				}
				
			}

			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	
	public String getAssitemTypeById(String assItemID, String accid) throws Exception {
		ResultSet rs = null;
		PreparedStatement ps = null;
		String totleAssItemID = "";
		try {
			String sql = "select distinct if(locate('/',AssTotalName)=0,AssTotalName,substr(AssTotalName,1,locate('/',AssTotalName)-1)) as AssTotalName from  c_assitem where AccPackageID="+accid+" and AssItemID='"+assItemID+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				String AssItemName = rs.getString(1);
				ResultSet rs1 = null;
				PreparedStatement ps1 = null;
				sql = "select distinct AssItemID from c_assitem where AssItemName='"+AssItemName+"' and AccPackageID="+accid;
				ps1 = conn.prepareStatement(sql);
				rs1 = ps1.executeQuery();
				if(rs1.next()){
					totleAssItemID = rs1.getString(1);
				}
				rs1.close();
				ps1.close();
			}

			return totleAssItemID;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
}
