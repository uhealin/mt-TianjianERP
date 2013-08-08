package com.matech.audit.service.audittypetemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.task.TaskCommonService;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

/**
 * <p>Title: 项目底稿树</p>
 * <p>Description: 项目底稿树,用于底稿导出和底稿备份</p>
 * <p>Copyright: Copyright (c) 2006, 2008 MaTech Corporation.All rights reserved. </p>
 * <p>Company: Matech  广州铭太信息科技有限公司</p>
 *
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有，
 * 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 *
 * 贡献者团队:
 *     铭太科技 - 研发中心，审计开发组
 *
 * @author void
 * 2007-7-16
 */
public class TemplateTaskTreeService {
	private ASFuntion CHF = new ASFuntion();

	private String typeId; //当前项目ID

	private Connection conn = null;

	public TemplateTaskTreeService(Connection conn, String typeId) throws Exception {
		DbUtil.checkConn(conn);
		this.conn = conn;

		if ("".equals(typeId) || typeId == null) {
			throw new Exception("模板typeId不能为空!");
		}

		this.typeId = typeId;
	}

	/**
	 * 带过滤条件的底稿树
	 * @param sqlWhere
	 * @return
	 * @throws Exception
	 */
	public String getTree(String sqlWhere) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			if (sqlWhere != null && !"".equals(sqlWhere)) {
				return getSubTree(0, sqlWhere);
			} else {
				return getSubTree(0);
			}

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

	/**
	 * 底稿树
	 * @return
	 * @throws Exception
	 */
	public String getTree() throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			return getSubTree(0);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public String getSubTree(int parentTID, String sqlWhere) {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = "select * from k_tasktemplate "
						+ " where typeid=? "
						+ "	and parenttaskid=?"
						+ " and ( isleaf=0 or " + sqlWhere + "   ) "
						+ " order by orderid";

			//org.util.Debug.prtOut("出错:"+sql);
			ps = conn.prepareStatement(sql);
			ps.setString(1, this.typeId);
			ps.setInt(2, parentTID);
			rs = ps.executeQuery();

			StringBuffer sb = new StringBuffer("");
			int isleaf = 0;
			int taskId = 0;

			String taskCode = "";
			String taskName = "";
			String fullpath = "";

			String stateHTML = "";
			
			String ismust = "";

			String property = "";
			//int parenttaskid = 0;

			while (rs.next()) {

				isleaf = rs.getInt("isleaf");
				taskId = rs.getInt("taskid");
				taskCode = rs.getString("taskcode");
				taskName = rs.getString("taskname");
				//parenttaskid = rs.getInt("parenttaskid");

				property = CHF.showNull(rs.getString("property"));

				fullpath = rs.getString("fullpath");
				sb.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
				if (isleaf <= 0) {
					//不是叶子
					sb.append("<tr style=\"cursor: hand;\">");
					sb.append("<td width=\"11\" height=\"11\" align=\"right\" onclick=\"getSubTree("
									+ taskId + ");\">");
					sb.append("<img id=\"ActImg"
									+ taskId
									+ "\" src=\"/AuditSystem/images/plus.jpg\" width=\"11\" height=\"11\" />");
					sb.append("</td>");
					sb.append("<td align=\"left\" valign=\"bottom\" nowrap><input type=\"checkbox\" id='"
									+ fullpath
									+ "' name='TaskID' isleaf='"
									+ isleaf
									+ "' value='"
									+ taskId
									+ "' taskcode='"
									+ taskCode
									+ "' onclick='setEnableTree(this);'><font size=2 color=\"blue\" >"
									+ taskCode + "</font>");
					sb.append("&nbsp;&nbsp<span onclick=\"getSubTree(" + taskId
							+ ");\">&nbsp;<font size=\"2\" color=\"blue\" >");
					sb.append(taskName + "</font></span></td>");
					sb.append("</tr>");

					sb.append("<tr>");
					sb.append("<td id='subImg" + taskId
							+ "' style='display:none'></td>");
					sb.append("<td id='subTree" + taskId
							+ "' style='display:none' colspan='2'>"
							+ getSubTree(taskId, sqlWhere) + "</td>");
					sb.append("</tr>");
					sb.append("</table>");
				} else {

					ismust = CHF.showNull(rs.getString("ismust"));
					stateHTML = getTaskStateHTML(ismust);

					String picName = "";

					String extendName = taskName;


					if (extendName.lastIndexOf(".") > -1) {
						extendName = extendName.substring(extendName.lastIndexOf("."));

						if (".doc".equalsIgnoreCase(extendName)) {
							picName = "word" + picName + ".gif";
						} else if (".xls".equalsIgnoreCase(extendName)) {
							picName = "excel" + picName + ".gif";
						}
					} else {
						picName = "excel.gif";
					}
					
					//是叶子
					sb.append("<tr style=\"cursor: hand;\">");
					sb.append("<td width=\"11\" height=\"11\" align=\"right\">");
					sb.append("<img id=\"ActImg\" src=\"/AuditSystem/images/office/" + picName + "\" />");
					sb.append("</td>");
					sb.append("<td align=\"left\" valign=\"bottom\" nowrap><input type='checkbox' taskid='"
									+ taskId
									+ "' id='"
									+ fullpath
									+ "' name='TaskID' isleaf='"
									+ isleaf
									+ "' value='"
									+ taskId
									+ "' property='"
									+ property
									+ "' taskcode='"
									+ taskCode
									+ "' onclick='setEnableTree(this);' msg='"
									+ taskCode
									+ " "
									+ taskName
									+ "' ismust='"
									+ ismust
									+ "'>&nbsp;<font size=2>" + taskCode + "</font>");
					sb.append("&nbsp;<font size=\"2\">");
					sb.append(taskName + "&nbsp;" + stateHTML + "</font></td>");
					sb.append("</tr>");
					sb.append("<tr>");
					sb.append("<td id='subImg" + taskId
							+ "' style='display:none'></td>");
					sb.append("<td id='subTree" + taskId
							+ "' style='display:none' colspan=\"2\"></td>");
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

	public String getSubTree(int parentTID) {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = " select * from k_tasktemplate "
					+ " where typeId=? "
					+ "	and parenttaskid=?"
					+ " order by orderid";

			ps = conn.prepareStatement(sql);
			ps.setString(1, this.typeId);
			ps.setInt(2, parentTID);
			rs = ps.executeQuery();

			StringBuffer sb = new StringBuffer("");
			int isleaf = 0;
			int taskid = 0;

			String taskcode = "";
			String taskname = "";
			String fullpath = "";
			String stateHTML = "";

			String ismust = "";

			String property = "";

			while (rs.next()) {

				isleaf = rs.getInt("isleaf");
				taskid = rs.getInt("taskid");
				taskcode = rs.getString("taskcode");
				taskname = rs.getString("taskname");
				property = CHF.showNull(rs.getString("property"));

				fullpath = rs.getString("fullpath");
				sb.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
				if (isleaf <= 0) {
					sb.append("<tr style=\"cursor: hand;\">");
					sb.append("<td width=\"11\" height=\"11\" align=\"right\" onclick=\"getSubTree("
									+ taskid + ");\">");
					sb.append("<img id=\"ActImg"
									+ taskid
									+ "\" src=\"/AuditSystem/images/plus.jpg\" width=\"11\" height=\"11\" />");
					sb.append("</td>");
					sb.append("<td align=\"left\" valign=\"bottom\" nowrap>&nbsp;<input type=\"checkbox\" id='"
									+ fullpath
									+ "' name='TaskID' isleaf='"
									+ isleaf
									+ "' value='"
									+ taskid
									+ "' taskcode='"
									+ taskcode
									+ "' onclick='setEnableTree(this);'><font size=2 color=\"blue\" >"
									+ taskcode + "</font>");
					sb.append("&nbsp;&nbsp<span onclick=\"getSubTree(" + taskid
							+ ");\">&nbsp;<font size=\"2\" color=\"blue\" >");
					sb.append(taskname + "</font></span></td>");
					sb.append("</tr>");

					sb.append("<tr>");
					sb.append("<td id='subImg" + taskid
							+ "' style='display:none'></td>");
					sb.append("<td id='subTree" + taskid
							+ "' style='display:none' colspan='2'>"
							+ getSubTree(taskid) + "</td>");
					sb.append("</tr>");
					sb.append("</table>");
				} else {

					ismust = CHF.showNull(rs.getString("ismust"));
					property = CHF.showNull(rs.getString("property"));
					stateHTML = getTaskStateHTML(ismust);

					String picName = "";

					String extendName = taskname;


					if (extendName.lastIndexOf(".") > -1) {
						extendName = extendName.substring(extendName.lastIndexOf("."));

						if (".doc".equalsIgnoreCase(extendName)) {
							picName = "word" + picName + ".gif";
						} else if (".xls".equalsIgnoreCase(extendName)) {
							picName = "excel" + picName + ".gif";
						}
					} else {
						picName = "excel.gif";
					}

					sb.append("<tr style=\"cursor: hand;\">");
					sb.append("<td width=\"11\" height=\"11\" align=\"right\">");
					sb.append("<img id=\"ActImg\" src=\"/AuditSystem/images/office/" + picName + "\"  />");
					sb.append("</td>");
					sb.append("<td align=\"left\" valign=\"bottom\" nowrap>&nbsp;<input type='checkbox' taskid='"
									+ taskid
									+ "' id='"
									+ fullpath
									+ "' name='TaskID' isleaf='"
									+ isleaf
									+ "' value='"
									+ taskid
									+ "' property='"
									+ property
									+ "' taskcode='"
									+ taskcode
									+ "' onclick='setEnableTree(this);' msg='"
									+ taskcode
									+ " "
									+ taskname
									+ "' ismust='"
									+ ismust
									+ "'>&nbsp;<font size=2>" + taskcode + "</font>");
					sb.append("&nbsp;<font size=\"2\">");
					sb.append(taskname + "&nbsp;" + stateHTML + "</font></td>");
					sb.append("</tr>");
					sb.append("<tr>");
					sb.append("<td id='subImg" + taskid
							+ "' style='display:none'></td>");
					sb.append("<td id='subTree" + taskid
							+ "' style='display:none' colspan=\"2\"></td>");
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

	/**
	 * 返回状态的HTML代码
	 * @param state
	 * @return
	 * @throws Exception
	 */
	public String getTaskStateHTML(String state) throws Exception {

		final String spanStart = "<span style=\"height:12px; overflow:hidden; width:12px; border: 1px solid #000000; padding:0px;margin: 0px; background-color:";
		final String spanEnd = ";\"></span>&nbsp;";
		StringBuffer sb = new StringBuffer();

		if(state.indexOf(String.valueOf(TaskCommonService.TASK_STATE_CODE_MUST)) > -1) {
			sb.append(spanStart);
			sb.append(TaskCommonService.TASK_STATE_COLOR_MUST);
			sb.append(spanEnd);

		}

		if(state.indexOf(String.valueOf(TaskCommonService.TASK_STATE_CODE_ATTENTION)) > -1) {
			sb.append(spanStart);
			sb.append(TaskCommonService.TASK_STATE_COLOR_ATTENTION);
			sb.append(spanEnd);
		}

		if(state.indexOf(String.valueOf(TaskCommonService.TASK_STATE_CODE_DATA)) > -1) {
			sb.append(spanStart);
			sb.append(TaskCommonService.TASK_STATE_COLOR_DATA);
			sb.append(spanEnd);
		}

		if(state.indexOf(String.valueOf(TaskCommonService.TASK_STATE_CODE_SAVED)) > -1) {
			sb.append(spanStart);
			sb.append(TaskCommonService.TASK_STATE_COLOR_SAVED);
			sb.append(spanEnd);
		}



		return sb.toString();
	}
}