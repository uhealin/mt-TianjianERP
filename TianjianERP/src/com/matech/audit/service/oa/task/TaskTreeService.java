package com.matech.audit.service.oa.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;

/**
 * <p>Title: 底稿任务树</p>
 * <p>Description: 输出底稿任务树</p>
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
 * 2007-6-26
 */
public class TaskTreeService {

	private Connection conn = null;

	private String projectId = null;

	private boolean isleaf = true;

	/**
	 * 构造方法
	 * @param conn 连接
	 * @param projectId	项目编号
	 * @throws Exception
	 */
	public TaskTreeService(Connection conn, String projectId) throws Exception {
		DbUtil.checkConn(conn);
		this.conn = conn;

		if("".equals(projectId) || projectId == null) {
			//throw new Exception("项目Id不能为空!");
			this.projectId = "0";
		} else {
			this.projectId = projectId;
		}
	}

	/**
	 * 获得子树
	 * @param parentTaskId
	 * @return
	 * @throws Exception
	 */
	public String getSubTree(String parentTaskId) throws Exception {
		ASFuntion asf = new ASFuntion();
		PreparedStatement ps = null;
		ResultSet rs = null;

		if (parentTaskId == null) {
			return "";
		}

		try {

			String sql = "select distinct IsLeaf,TaskID,TaskName,ManuID,b.name,taskcode,user1,user2,user3,User4,user5,a.property,ismust "
						+ " from jbpm_z_Task a left join k_user b on  a.user0=b.id  "
						+ " where ParentTaskID = ? "
						+ " and ProjectID = ? "
						+ " and (property not like 'A%' or property is null) ";

			//不显示叶子节点,也就是底稿
			if(!isleaf) {
				sql += " and isleaf=0 ";
			}

			sql += " order by orderid,taskcode";

			ps = conn.prepareStatement(sql);
			ps.setString(1, parentTaskId);
			ps.setString(2, this.projectId);

			rs = ps.executeQuery();

			int i = 0;
			StringBuffer sb = new StringBuffer("");

			while (rs.next()) {
				i++;
				String taskusr = rs.getString(5);

				if (taskusr != null && !"".equals(taskusr)) {
					taskusr = "(" + taskusr + ")";
				} else {
					taskusr = "";
				}

				String property = rs.getString(12);

				//coalition.gif

				String taskId = rs.getString(2);
				String tcode = rs.getString(6);
				String taskName = rs.getString(3);
				String manuId = rs.getString(4);
				String url = "";

				if("22".equals(property)){
					tcode = "<img src=\"../images/coalition.gif\" width=\"18\" height=\"16\">&nbsp;" + tcode;
				}

				sb.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");


				if (rs.getInt(1) == 0) {
					sb.append("<tr style=\"cursor: hand;\">");
					sb.append("<td onclick=\"getSubTree('" + taskId + "');\" width=\"11\" height=\"11\" align=\"right\">");
					sb.append("<img id=\"ActImg" + taskId + "\" src=\"../images/plus.jpg\" width=\"11\" height=\"11\" />");
					sb.append("</td>");
					sb.append("<td onclick=\"showObj('" + taskId + "');\" align=left valign=\"bottom\" nowrap>");


					if(isleaf) {
						//显示底稿列表
						url = "/AuditSystem/oa/task.do?method=list&parentTaskId=" + taskId;
					} else {
						//不显示底稿列表,只显示程序和目标
						url = "/AuditSystem/oa/task.do?method=tarAndPro&parentTaskId=" + taskId + "&projectId=" + this.projectId + "&isLeaf=" + isleaf;
					}

					sb.append("<a id=\"a" + taskId + "\" href=\"" + url + "\" target='TaskMainFrame' onclick='doIt(this);'> ");
					sb.append("<font size=2>&nbsp;" + tcode + " " + taskName );
					sb.append("</a></td></tr>");
				} else {
					String user1 = asf.showNull(rs.getString(7));
					String user2 = asf.showNull(rs.getString(8));
					String user3 = asf.showNull(rs.getString(9));
					String user4 = asf.showNull(rs.getString(10));
					String user5 = asf.showNull(rs.getString(11));
					//String ismust = asf.showNull(rs.getString(13));
					String stateHTML = "";//getTaskStateHTML(ismust);

					String picName = "";

					if(!"".equals(user1)) {
						picName = "1";
					}

					if(!"".equals(user5)) {
						picName = "5";
					}

					if(!"".equals(user2)) {
						picName = "2";
					}

					if(!"".equals(user3)) {
						picName = "3";
					}

					if(!"".equals(user4)) {
						picName = "4";
					}

					sb.append("<tr style=\"cursor: hand;\">");
					sb.append("<td width=\"27\" height=\"16\" nowrap align=\"right\">");
					String extendName = taskName;


					if (extendName.lastIndexOf(".") <= -1) {
						continue;
					}

					extendName = extendName.substring(extendName.lastIndexOf("."));

					if (".doc".equalsIgnoreCase(extendName)) {
						picName = "word" + picName + ".gif";
					} else if (".xls".equalsIgnoreCase(extendName)) {
						picName = "excel" + picName + ".gif";
					}

					sb.append("<img id=\"ActImg\" src=\"../images/office/" + picName + "\" />");
					sb.append("</td>");

					String goUrl = "'" + manuId + "','" + taskId + "'";

					String style = "style=\"cursor:hand;\"";
					String title = "";
					String isuntread = "no";
					if (!"".equals(user4)) {
						style = " style=\" cursor:hand;font-weight:bold; color:red; \" ";
						title = " title=\"被退回的底稿\" ";
						isuntread = "yes";
					}

					sb.append("<td align=left valign=\"bottom\" nowrap>");
					sb.append("<a isuntread='" + isuntread + "'" + title + " id='a" + taskId + "' " + style +  " onclick=\"doIt(this);");
					sb.append(" goUrl(" + goUrl + ");\" >&nbsp;");
					sb.append(tcode + " " + taskName + " " + taskusr + " " + stateHTML);
					sb.append("</a>");
					sb.append("</td></tr>");
				}

				sb.append("<tr>");
				sb.append("<td id='subImg" + taskId + "' style='display:none'></td>");
				sb.append("<td id='subTree" + taskId + "' style='display:none'></td>");
				sb.append("</tr>");
				sb.append("</table>");
			}
			return sb.toString();
		} catch (Exception e) {
			Debug.print(Debug.iError, "获得子树失败", e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	/**
	 * 输出脚本
	 * @param taskid
	 * @return
	 * @throws Exception
	 */
	public String getScript(String taskid) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;
		String level = "";
		String result = "";

		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			ps = conn.prepareStatement("select parenttaskid,level0 from jbpm_z_task where projectid=? and taskid=?");
			ps.setString(1, this.projectId);

			while (!level.equals("0")) {
				ps.setString(2, taskid);
				rs = ps.executeQuery();
				if (rs.next()) {
					result = "getSubTree('" + taskid + "');" + result;
					level = rs.getString("level0");
					taskid = rs.getString("parenttaskid");
				} else {
					break;
				}
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "输出脚本出错", e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return result;
	}

	public static void main(String[] args) throws Exception {
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			String ss = new TaskTreeService(conn,"2007631623").getSubTree("0");
			System.out.println(ss);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

	}

	public boolean isIsleaf() {
		return isleaf;
	}

	public void setIsleaf(boolean isleaf) {
		this.isleaf = isleaf;
	}
}
