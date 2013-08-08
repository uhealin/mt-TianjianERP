package com.matech.audit.service.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.task.TaskCommonService;
import com.matech.audit.service.task.TaskTreeService;
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
public class ExportTreeService {
	private ASFuntion CHF = new ASFuntion();

	private String projectId; //当前项目ID

	private Connection conn = null;

	public ExportTreeService(Connection conn, String projectId) throws Exception {
		DbUtil.checkConn(conn);
		this.conn = conn;

		if ("".equals(projectId) || projectId == null) {
			throw new Exception("项目Id不能为空!");
		}

		this.projectId = projectId;
		
		new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
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
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			if (sqlWhere != null) {
				
				//先获取所有taskids全集
				if(!"".equals(sqlWhere) ) {
					sqlWhere = " and "+sqlWhere ;
				}
				
				String sql = "select group_concat(replace(FullPath,'|',',')) from z_task "
					+ " where projectid=? "
					+ " and (property not like 'A%' or property is null or property='' ) "
					+ " and (property<>'target' or property is null or property='' ) "
					+  sqlWhere
					+ " order by orderid";
				
				ps = conn.prepareStatement(sql) ;
				ps.setString(1,this.projectId) ;
				
				rs = ps.executeQuery() ;
				String taskids = "," ;
				if(rs.next()) {
					taskids += new ASFuntion().showNull(rs.getString(1));
				}

				return getSubTree(conn, 0, sqlWhere,taskids+",");
			} else {
				return getSubTree(conn, 0);
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
	 * 获得子树
	 * @param parentTaskId
	 * @return
	 * @throws Exception
	 */
	public String getJOSNTree(String parentTaskId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuffer sb = new StringBuffer();

		try {
			StringBuffer sql = new StringBuffer();

			sql.append(" select a.taskname,a.taskcode,a.taskid,ifnull(b.parenttaskid,-1) as isleaf2 ")
				.append("  from z_task a " )
				.append("  left join ")
				.append(" 		(select parenttaskid,projectId from z_task " )
				.append("  			where projectId=? ")
				.append("  			and isleaf='0' ")
				.append("  			group by parenttaskid) b ")
				.append("  on a.taskid=b.parenttaskid ")
				.append("  where a.projectId=? ")
				.append("  and a.isleaf='0' ")
				.append("  and (a.user0 is null or a.user0='') ")
				.append("  and a.parenttaskid=? ")
				.append("  order by orderid,taskcode ");
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, this.projectId);
			ps.setString(2, this.projectId);
			ps.setString(3, parentTaskId);
			
			rs = ps.executeQuery();
			String taskName;
			String taskCode;
			String taskId;
			boolean isLeaf = false;
			

			while (rs.next()) {
		
				taskName = rs.getString("taskName");
				taskCode = rs.getString("taskcode");
				taskId = rs.getString("taskId");
				isLeaf = rs.getInt("isleaf2") == -1;
				
				sb.append(" { ")
				.append("cls:'folder',")
				.append("leaf:").append(isLeaf).append(",")
				.append("id:'").append(taskId).append("',")
				.append("taskId:'").append(taskId).append("',")
				.append("checkValue:'").append(taskId + "```" + taskCode + "```" + taskName + "```" + isLeaf).append("',")
				.append("checked:").append("false").append(",")
				.append("text:'").append(taskCode + " " + taskName).append("'");
				
				if(!isLeaf) {
					sb.append(",children:[ ").append(getJOSNTree(taskId)).append("]");
				}
				
				sb.append("}");
				if(!rs.isLast()) {
					sb.append(",");
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return sb.toString();

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
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			return getSubTree(conn, 0);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public String getSubTree(Connection conn, int parentTID, String sqlWhere,String taskids) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			TaskTreeService tts = new TaskTreeService(conn,this.projectId) ;
			
			String taxFilter = tts.getTaxFilter() ;
			
			String sql = "select a.*,b.name from z_task a left join k_user b on a.user0=b.id "
						+ " where projectid=? "
						+ "	and parenttaskid=?"
						+ " and (property not like 'A%' or property is null or property='' ) "
						+ " and (property<>'target' or property is null or property='' ) "
						+ taxFilter
						+ " order by orderid";
			
	
			//org.util.Debug.prtOut("出错:"+sql);
			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);
			ps.setInt(2, parentTID);
			rs = ps.executeQuery();

			StringBuffer sb = new StringBuffer("");
			int isleaf = 0;
			int taskId = 0;

			String taskCode = "";
			String taskName = "";
			String fullpath = "";
			String user3 = "";
			String user0 = "";

			String stateHTML = "";

			String user1 = "";
			String user2 = "";
			String user4 = "";
			String user5 = "";
			String ismust = "";

			String property = "";
			//int parenttaskid = 0;
			while (rs.next()) {

				isleaf = rs.getInt("isleaf");
				taskId = rs.getInt("taskid");
				taskCode = rs.getString("taskcode");
				taskName = rs.getString("taskname");
				//parenttaskid = rs.getInt("parenttaskid");
				user3 = CHF.showNull(rs.getString("user3"));

				property = CHF.showNull(rs.getString("property"));

				if (user3.equals("")) {
					user3 = "no";
				}
				
				String isCheck = "" ;
				if(taskids.indexOf(","+taskId+",") >=0) {
					isCheck = "checked" ; 
				}else {
					isCheck = "" ;
				}

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
					
					if(fullpath.lastIndexOf("|") != fullpath.length()-1) {
						fullpath += "|" ;
					}
					
					sb.append("<td align=\"left\" valign=\"bottom\" nowrap><input type=\"checkbox\" "+isCheck+" id='"
									+ fullpath
									+ "' name='TaskID' isleaf='"
									+ isleaf
									+ "' value='"
									+ taskId
									+ "' user3='"
									+ user3
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
							+ getSubTree(conn, taskId, sqlWhere,taskids) + "</td>");
					sb.append("</tr>");
					sb.append("</table>");
				} else {

					user0 = CHF.showNull(rs.getString("name"));
					user1 = CHF.showNull(rs.getString("user1"));
					user2 = CHF.showNull(rs.getString("user2"));
					user3 = CHF.showNull(rs.getString("user3"));
					user4 = CHF.showNull(rs.getString("user4"));
					user5 = CHF.showNull(rs.getString("user5"));
					ismust = CHF.showNull(rs.getString("ismust"));
					stateHTML = getTaskStateHTML(ismust);

					if(!"".equals(user0)) {
						user0 = "<font color='blue'>(" + user0 +  ")</font>";
					}

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
					//是叶子
					sb.append("<tr style=\"cursor: hand;\">");
					sb.append("<td width=\"11\" height=\"11\" align=\"right\">");
					sb.append("<img id=\"ActImg\" src=\"/AuditSystem/images/office/" + picName + "\" />");
					sb.append("</td>");
					sb.append("<td align=\"left\" valign=\"bottom\" nowrap><input type='checkbox' "+isCheck+" taskid='"
									+ taskId
									+ "' id='"
									+ fullpath
									+ "' name='TaskID' isleaf='"
									+ isleaf
									+ "' value='"
									+ taskId
									+ "' user3='"
									+ user3
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
					sb.append(taskName + user0 + "&nbsp;" + stateHTML + "</font></td>");
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
		//	System.out.println("treeeeee:"+sb.toString()) ;
			return sb.toString();
			
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	public String getSubTree(Connection conn, int parentTID) {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			
			TaskTreeService tts = new TaskTreeService(conn,this.projectId) ;
			String taxFilter = tts.getTaxFilter() ;
			
			String sql = " select a.*,b.name from z_task a left join k_user b on a.user0=b.id  "
					+ " where projectid=? "
					+ "	and parenttaskid=?"
					+ " and (property not like 'A%' or property is null or property='' ) "
					+ " and (property<>'target' or property is null or property='' ) "
					+ taxFilter
					+ " order by orderid";
			//org.util.Debug.prtOut("出错11:"+sql);
			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);
			ps.setInt(2, parentTID);
			rs = ps.executeQuery();

			StringBuffer sb = new StringBuffer("");
			int isleaf = 0;
			int taskid = 0;

			String taskcode = "";
			String taskname = "";
			String fullpath = "";
			String user3 = "";
			String user0 = "";
			String stateHTML = "";

			String user1 = "";
			String user2 = "";
			String user4 = "";
			String user5 = "";

			String ismust = "";

			String property = "";

			while (rs.next()) {

				isleaf = rs.getInt("isleaf");
				taskid = rs.getInt("taskid");
				taskcode = rs.getString("taskcode");
				taskname = rs.getString("taskname");
				property = CHF.showNull(rs.getString("property"));

				if (user3.equals("")) {
					user3 = "no";
				}

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
					if(fullpath.lastIndexOf("|") != fullpath.length()-1) {
						fullpath += "|" ;
					}
					sb.append("<td align=\"left\" valign=\"bottom\" nowrap>&nbsp;<input type=\"checkbox\" id='"
									+ fullpath
									+ "' name='TaskID' isleaf='"
									+ isleaf
									+ "' value='"
									+ taskid
									+ "' user3='"
									+ user3
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
							+ getSubTree(conn, taskid) + "</td>");
					sb.append("</tr>");
					sb.append("</table>");
				} else {

					user0 = CHF.showNull(rs.getString("name"));
					user1 = CHF.showNull(rs.getString("user1"));
					user2 = CHF.showNull(rs.getString("user2"));
					user3 = CHF.showNull(rs.getString("user3"));
					user4 = CHF.showNull(rs.getString("user4"));
					user5 = CHF.showNull(rs.getString("user5"));
					ismust = CHF.showNull(rs.getString("ismust"));
					property = CHF.showNull(rs.getString("property"));
					stateHTML = getTaskStateHTML(ismust);

					if(!"".equals(user0)) {
						user0 = "<font color='blue'>(" + user0 +  ")</font>";
					}

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


					String extendName = taskname;


					if (extendName.lastIndexOf(".") <= -1) {
						continue;
					}

					extendName = extendName.substring(extendName.lastIndexOf("."));

					if (".doc".equalsIgnoreCase(extendName)) {
						picName = "word" + picName + ".gif";
					} else if (".xls".equalsIgnoreCase(extendName)) {
						picName = "excel" + picName + ".gif";
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
									+ "' user3='"
									+ user3
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
					sb.append(taskname + user0 + "&nbsp;" + stateHTML + "</font></td>");
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
	
	public static void main(String[] args) {
		Connection conn = null;
		
		try {
			conn = new DBConnect().getConnect("");
			String ss = new ExportTreeService(conn,"20099218").getJOSNTree("0");
			System.out.println(ss);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
	}
}