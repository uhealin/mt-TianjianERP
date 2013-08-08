package com.matech.audit.service.project;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.del.DelPublic;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.affairreport.model.AffairReportTable;
import com.matech.audit.service.audittypetemplate.AuditTypeTemplateService;
import com.matech.audit.service.datamanage.BackupUtil;
import com.matech.audit.service.datamanage.DataBackup;
import com.matech.audit.service.datamanage.TemplateBackup;
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.audit.service.net.NetService;
import com.matech.audit.service.project.model.AuditPlan;
import com.matech.audit.service.project.model.Project;
import com.matech.audit.service.rectify.RectifyService;
import com.matech.audit.service.task.TaskCommonService;
import com.matech.audit.service.task.TaskRecycleService;
import com.matech.audit.service.task.model.Task;
import com.matech.audit.service.userState.UserStateService;
import com.matech.audit.work.repair.Repair;
import com.matech.framework.pub.autocode.DELAutocode;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.pub.util.UTILString;

/**
 * <p>Title: 项目管理类</p>
 * <p>Description: 实现项目详细信息的管理，如生成审计类型树等</p>
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
 * 2007-8-3
 */
public class ProjectService {
	
	private String removeSkip = null;

	/**
	 * 进行中
	 */
	public final static int STATE_NORMAL = 1;

	/**
	 * 已归档
	 */
	public final static int STATE_END = 2;

	/**
	 * 删除的
	 */
	public final static int STATE_REMOVE = 3;

	/**
	 * 等待审核
	 */
	public final static int STATE_WAITING_AUDITING  = 4;

	Connection conn;

	/**
	 * 初始化一个新创建的 ProjectMan 对象，它表示一个项目管理对象。
	 */
	public ProjectService(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 根据传递进来的级别参数，动态生成相应的审计类型树。<br>
	 * <p>
	 * 此处可写例子
	 * </p>
	 *
	 * @param level String 树的级别
	 * @return String 返回用HTML代码表示的审计类型树字符串
	 * @exception 抛出的异常
	 */
	/*public String getSubTree(String level) { //生成审计类型树
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "Select TypeID, TypeName, Content, Des, Property From k_audittypetemplate Where Exists(Select 1 From z_Project Where z_Project.AuditType = k_audittypetemplate.TypeID) Order By TypeID";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			StringBuffer sb = new StringBuffer("");
			while (rs.next()) {

				sb.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
				sb.append("<tr style=\"cursor: hand;\">");
				sb.append("<td onclick=\"getSubTree('1','"
								+ rs.getString("TypeID")
								+ "','','');\" width=\"11\" height=\"11\" align=\"right\">");
				sb.append("<img id=\"ActImg"
								+ level
								+ rs.getString("TypeID")
								+ "\" src=\"../images/plus.jpg\" width=\"11\" height=\"11\" />");
				sb.append("</td>");
				sb.append("<td onclick=\"getSubTree('1','"
								+ rs.getString("TypeID")
								+ "','','');\" align=\"left\" valign=\"bottom\" nowrap>"
								+ "<a href=\"MultiAPList.jsp?level=" + level
								+ "&typeID=" + rs.getString("TypeID")
								+ "&year=" + 1
								+ "\" target='projectMainFrame'>"
								+ "<font size=2>&nbsp;"
								+ rs.getString("TypeName") + "</font></a></td>");
				sb.append("</tr>");
				sb.append("<tr>");
				sb.append("<td id='subImg" + level + rs.getString("TypeID")
						+ "' style='display:none'></td>");
				sb.append("<td id='subTree" + level + rs.getString("TypeID")
						+ "' style='display:none'></td>");
				sb.append("</tr>");
				sb.append("</table>");
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}*/
	
	/**
	 * 根据传递进来的级别参数，动态生成相应的审计类型树。<br>
	 * <p>
	 * 此处可写例子
	 * </p>
	 *
	 * @param level String 树的级别
	 * @return String 返回用HTML代码表示的审计类型树字符串
	 * @exception 抛出的异常
	 */
	public String getSubTree(String level) { //生成业务类型树
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select distinct Content from k_AuditTypeTemplate where TypeID <> '0'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			StringBuffer sb = new StringBuffer("");
			while (rs.next()) {

				sb.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
				sb.append("<tr style=\"cursor: hand;\">");
				sb.append("<td onclick=\"getSubTree('1','"
								+ rs.getString("Content")
								+ "','','');\" width=\"11\" height=\"11\" align=\"right\">");
				sb.append("<img id=\"ActImg"
								+ level
								+ rs.getString("Content")
								+ "\" src=\"../images/plus.jpg\" width=\"11\" height=\"11\" />");
				sb.append("</td>");
				sb.append("<td onclick=\"getSubTree('1','"
								+ rs.getString("Content")
								+ "','','');\" align=\"left\" valign=\"bottom\" nowrap>"
								+ "<a href=\"MultiAPList.jsp?level=" + level
								+ "&typeID=" + java.net.URLEncoder.encode(rs.getString("Content"),"GB2312")
								+ "&year=" + 1
								+ "\" target='projectMainFrame'>"
								+ "<font size=2>&nbsp;"
								+ rs.getString("Content") + "</font></a></td>");
				sb.append("</tr>");
				sb.append("<tr>");
				sb.append("<td id='subImg" + level + rs.getString("Content")
						+ "' style='display:none'></td>");
				sb.append("<td id='subTree" + level + rs.getString("Content")
						+ "' style='display:none'></td>");
				sb.append("</tr>");
				sb.append("</table>");
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
	
//	生成审计年份树
	public String getSubTree(String level, String auditPara) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "Select DISTINCT Substring(ProjectCreated,1,4) From z_Project Where AuditPara = '"
					+ auditPara +"' order by projectcreated desc,projectid desc";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			StringBuffer sb = new StringBuffer("");
			
			
			while (rs.next()) {
				sb.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
				sb.append("<tr style=\"cursor: hand;\">");
				sb.append("<td onclick=\"getSubTree('2','"
								+ auditPara
								+ "',"
								+ "'"
								+ rs.getString(1)
								+ "','');\" width=\"11\" height=\"11\" align=\"right\">");
				sb.append("<img id=\"ActImg"
								+ level
								+ auditPara
								+ rs.getString(1)
								+ "\" src=\"../images/plus.jpg\" width=\"11\" height=\"11\" />");
				sb.append("</td>");
				sb.append("<td onclick=\"getSubTree('2','" + auditPara + "',"
						+ "'" + rs.getString(1)
						+ "','');\" align=\"left\" valign=\"bottom\" nowrap>"
						+ "<a href=\"MultiAPList.jsp?level=" + level
						+ "&typeID=" + java.net.URLEncoder.encode(auditPara,"GB2312") + "&year=" + rs.getString(1)
						+ "\" target='projectMainFrame'>"
						+ "<font size=2>&nbsp;" + rs.getString(1) + "</td>");
				sb.append("</tr>");
				sb.append("<tr>");
				sb.append("<td id='subImg" + level + auditPara
						+ rs.getString(1) + "' style='display:none'></td>");
				sb.append("<td id='subTree" + level + auditPara
						+ rs.getString(1) + "' style='display:none'></td>");
				sb.append("</tr>");
				sb.append("</table>");
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
	
	//生成审计项目树
	public String getSubTree(String level, String auditPara, String year) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "Select ProjectID,AuditType,ProjectName,ProjectCreated From z_Project Where AuditPara = '"
					+ auditPara
					+ "' And substring(ProjectCreated, 1, 4) = '"
					+ year + "'"+" order by projectcreated desc,projectid desc";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			//      int i = 0;
			StringBuffer sb = new StringBuffer("");
			while (rs.next()) {
				//        i++;
				sb.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
				sb.append("<tr style=\"cursor: hand;\">");
				//sb.append("<td onclick=\"getSubTree('3','" + auditType + "'," +
				//"'" + year + "','" + rs.getString("ProjectID") + "');\" width=\"20\" height=\"18\" align=\"right\">");
				sb.append("<td width=\"11\" height=\"11\" align=\"right\">");
				sb.append("<img id=\"ActImg"
								+ level
								+ auditPara
								+ year
								+ rs.getString("ProjectID")
								+ "\" src=\"../images/sjx1.gif\" width=\"11\" height=\"11\" />");
				sb.append("</td>");
				sb.append("<td align=\"left\" valign=\"bottom\" nowrap><a onclick=\"linkChange(this);\" href=\"ProjectManage.jsp?projectid="
								+ rs.getString("ProjectID")
								+ "&projectname="
								+ rs.getString("ProjectName")
								+ "&projectcreated="
								+ rs.getString("ProjectCreated")
								+ "\" target='projectMainFrame'>"
								+ "<font size=2>&nbsp;"
								+ rs.getString("ProjectName") + "</a></td>");
				sb.append("</tr>");
				sb.append("<tr>");
				sb.append("<td id='subImg" + level + auditPara + year
						+ rs.getString("ProjectID")
						+ "' style='display:none'></td>");
				sb.append("<td id='subTree" + level + auditPara + year
						+ rs.getString("ProjectID")
						+ "' style='display:none'></td>");
				sb.append("</tr>");
				sb.append("</table>");
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

	/*//生成审计年份树
	public String getSubTree(String level, String auditType) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "Select DISTINCT Substring(ProjectCreated,1,4) From z_Project Where AuditType = "
					+ auditType +" order by projectcreated desc,projectid desc";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			StringBuffer sb = new StringBuffer("");
			while (rs.next()) {
				sb.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
				sb.append("<tr style=\"cursor: hand;\">");
				sb.append("<td onclick=\"getSubTree('2','"
								+ auditType
								+ "',"
								+ "'"
								+ rs.getString(1)
								+ "','');\" width=\"11\" height=\"11\" align=\"right\">");
				sb.append("<img id=\"ActImg"
								+ level
								+ auditType
								+ rs.getString(1)
								+ "\" src=\"../images/plus.jpg\" width=\"11\" height=\"11\" />");
				sb.append("</td>");
				sb.append("<td onclick=\"getSubTree('2','" + auditType + "',"
						+ "'" + rs.getString(1)
						+ "','');\" align=\"left\" valign=\"bottom\" nowrap>"
						+ "<a href=\"MultiAPList.jsp?level=" + level
						+ "&typeID=" + auditType + "&year=" + rs.getString(1)
						+ "\" target='projectMainFrame'>"
						+ "<font size=2>&nbsp;" + rs.getString(1) + "</td>");
				sb.append("</tr>");
				sb.append("<tr>");
				sb.append("<td id='subImg" + level + auditType
						+ rs.getString(1) + "' style='display:none'></td>");
				sb.append("<td id='subTree" + level + auditType
						+ rs.getString(1) + "' style='display:none'></td>");
				sb.append("</tr>");
				sb.append("</table>");
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}*/

	//生成审计项目树
/*	public String getSubTree(String level, String auditType, String year) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			String sql = "Select ProjectID,AuditType,ProjectName,ProjectCreated From z_Project Where AuditType = "
					+ auditType
					+ " And substring(ProjectCreated, 1, 4) = '"
					+ year + "'"+" order by projectcreated desc,projectid desc";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			//      int i = 0;
			StringBuffer sb = new StringBuffer("");
			while (rs.next()) {
				//        i++;
				sb.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
				sb.append("<tr style=\"cursor: hand;\">");
				//sb.append("<td onclick=\"getSubTree('3','" + auditType + "'," +
				//"'" + year + "','" + rs.getString("ProjectID") + "');\" width=\"20\" height=\"18\" align=\"right\">");
				sb.append("<td width=\"11\" height=\"11\" align=\"right\">");
				sb.append("<img id=\"ActImg"
								+ level
								+ auditType
								+ year
								+ rs.getString("ProjectID")
								+ "\" src=\"../images/sjx1.gif\" width=\"11\" height=\"11\" />");
				sb.append("</td>");
				sb.append("<td align=\"left\" valign=\"bottom\" nowrap><a onclick=\"linkChange(this);\" href=\"ProjectManage.jsp?projectid="
								+ rs.getString("ProjectID")
								+ "&projectname="
								+ rs.getString("ProjectName")
								+ "&projectcreated="
								+ rs.getString("ProjectCreated")
								+ "\" target='projectMainFrame'>"
								+ "<font size=2>&nbsp;"
								+ rs.getString("ProjectName") + "</a></td>");
				sb.append("</tr>");
				sb.append("<tr>");
				sb.append("<td id='subImg" + level + auditType + year
						+ rs.getString("ProjectID")
						+ "' style='display:none'></td>");
				sb.append("<td id='subTree" + level + auditType + year
						+ rs.getString("ProjectID")
						+ "' style='display:none'></td>");
				sb.append("</tr>");
				sb.append("</table>");
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}*/

	/**
	 * 关闭项目
	 * @param id
	 * @throws Exception
	 */
	public void closeProject(String projectId,String auditPara) throws Exception {

		PreparedStatement ps = null;
		String sqlStr = "";
		try {
			if (projectId != null) {

				sqlStr = "update z_Project set state = ?,ProjectEnd = ?,AuditPara=? where ProjectID= ?";

				ps = conn.prepareStatement(sqlStr);

				ps.setInt(1, 2);
				ps.setString(2, new ASFuntion().getCurrentDate());
				ps.setString(3, auditPara); 
				ps.setString(4, projectId);

				ps.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	/**
	 * 撤消归档
	 * @param projectId
	 * @throws Exception
	 */
	public void cancelClose(String projectId) throws Exception {

		PreparedStatement ps = null;
		String sqlStr = "";
		try {
			if (projectId != null) {

				sqlStr = "update z_Project set state = ?,ProjectEnd = null where ProjectID= ? ";

				ps = conn.prepareStatement(sqlStr);

				ps.setInt(1, 1);
				ps.setString(2, projectId);

				ps.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 返回项目总数
	 * @return
	 * @throws Exception
	 */
	public int getProjectCount() throws Exception {
		int intCount = 0;

		Statement stmt = null;
		ResultSet rs = null;
		String strSql = "select count(*) from z_Project";

		try {

			stmt = conn.createStatement();
			rs = stmt.executeQuery(strSql);
			if (rs.next()) {
				intCount = rs.getInt(1);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(stmt);
		}
		return intCount;
	}

	/**
	 * 删除项目
	 * @param projectId
	 * @throws Exception
	 */
	public void removeProject(String projectId) throws Exception {

		try {
			new TaskRecycleService(conn, projectId).clearRecycle();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			new ManuFileService(conn).deleteDirByProjectID(projectId);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			new UserStateService(conn).removeUserStateByProjectId(projectId);
		} catch(Exception e) {
			e.printStackTrace();
		}

		new DBConnect().changeDataBaseByProjectid(conn, projectId);
		DbUtil dbUtil = new DbUtil(conn);
		Object[] params = new Object[]{projectId};
				
		try {
			String path = DelPublic.getClassRoot();
			String fileName = "TableConfig.xml";
			
			SAXBuilder sb = new SAXBuilder();
			Document doc = sb.build(new FileInputStream(path + fileName));
			Element root = doc.getRootElement();
			Element project = root.getChild("project");
			Element table = null;
			String sql = null;
			
			List tableList = project.getChildren();;
			String skip = "";
			for(int i=0; i < tableList.size(); i++) {
				
				table = (Element)tableList.get(i);
				
				if(table.getAttribute("skip") != null) {
					skip = table.getAttribute("skip").getValue();
					
					if(skip != null && this.getRemoveSkip() != null && skip.indexOf(this.getRemoveSkip()) > -1) {
						System.out.println("删除项目,跳过表：" + table.getText());
						continue;
					}
				}
				
				
				try {
					sql = " delete from " + table.getText() + " where projectId = ? ";
					System.out.println("删除项目：" + sql);
					dbUtil.execute(sql, params);
					
				} catch (Exception e) {
					System.out.println("删除项目出错:" + e.getMessage() + ",sql=" + sql);
					e.printStackTrace();
					continue;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
	}

	public void AddOrModifyAMenu(String XML, String act) throws Exception {
		ASFuntion asf = new ASFuntion();

		PreparedStatement ps = null;

		int i = 1;
		try {
			new DBConnect().changeDataBase(asf.getXMLData(XML, "CustomerId"),
					conn);

			String str;
			if (act.equals("ad")) {

				//这段添加项目的代码疑似已经废弃，后面有一个addproject方法才是对的。

				org.util.Debug.prtOut(XML);

				str = "INSERT INTO z_Project(ProjectID,AuditDept,CustomerId,AccPackageID,AuditType,AuditPara,ProjectName,ProjectCreated,State,AuditPeople,Property) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
				ps = conn.prepareStatement(str);
				ps.setInt(i++, Integer.parseInt(asf.getXMLData(XML, "ProjectID")));
				ps.setString(i++, asf.getXMLData(XML, "AuditDept"));
				ps.setString(i++, asf.getXMLData(XML, "CustomerId"));
				ps.setString(i++, asf.getXMLData(XML, "AccPackageID"));
				ps.setInt(i++, Integer.parseInt(asf.getXMLData(XML, "AuditType")));
				ps.setString(i++, asf.getXMLData(XML, "AuditPara"));
				ps.setString(i++, asf.getXMLData(XML, "ProjectName"));
				ps.setDate(i++, Date.valueOf(asf.getXMLData(XML,"ProjectCreated")));
				ps.setInt(i++, Integer.parseInt(asf.getXMLData(XML, "State")));
				ps.setString(i++, asf.getXMLData(XML, "AuditPeople"));
				ps.setString(i++, asf.getXMLData(XML, "Property"));
				ps.execute();

				str = "insert into z_task (Taskid,TaskName,TaskContent,Description,ParentTaskID,ProjectID,IsLeaf,Level0,Fullpath,ManuID)"
						+ " SELECT Taskid,TaskName,TaskContent,Description,ParentTaskID,'"
						+ Integer.parseInt(asf.getXMLData(XML, "ProjectID"))
						+ "',IsLeaf,Level0,Fullpath,manutemplateid FROM k_tasktemplate"
						+ " where typeid='"
						+ Integer.parseInt(asf.getXMLData(XML, "AuditType"))
						+ "'";
				org.util.Debug.prtOut(str);
				ps = conn.prepareStatement(str);
				ps.execute();
			} else {
				i = 1;
				//	org.util.Debug.prtOut("else");
				ps = conn.prepareStatement("update z_Project set AuditDept=?,CustomerId=?,AccPackageID=?,AuditType=?,AuditPara=?,ProjectName=?,ProjectCreated=?,State=?,AuditPeople=?,Property=? where ProjectID=?");

				ps.setString(i++, asf.getXMLData(XML, "AuditDept"));
				ps.setString(i++, asf.getXMLData(XML, "CustomerId"));
				ps.setString(i++, asf.getXMLData(XML, "AccPackageID"));
				ps.setInt(i++, Integer.parseInt(asf.getXMLData(XML, "AuditType")));
				ps.setString(i++, asf.getXMLData(XML, "AuditPara"));
				ps.setString(i++, asf.getXMLData(XML, "ProjectName"));
				ps.setDate(i++, Date.valueOf(asf.getXMLData(XML,"ProjectCreated")));
				ps.setInt(i++, Integer.parseInt(asf.getXMLData(XML, "State")));
				ps.setString(i++, asf.getXMLData(XML, "AuditPeople"));
				ps.setString(i++, asf.getXMLData(XML, "Property"));

				ps.setInt(i++, Integer.parseInt(asf.getXMLData(XML, "ProjectID")));
				ps.execute();
			}

			/**
			 * py 重建ManuacCount表
			 */
//			new ManuacCountService(conn).insertManuacCount(asf.getXMLData(XML, "ProjectID"));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

	}

	public ArrayList getUsers() throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList al = new ArrayList();
		try {
			String sql = "SELECT Name,DepartName from k_User u LEFT JOIN  k_Customer c on c.DepartID=u.DepartID order by DepartName";

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				ArrayList alrs = new ArrayList();
				alrs.add(rs.getString(1));
				alrs.add(rs.getString(2));
				al.add(alrs);
			}
			return al;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	public String[] getAuditPeople(String projectId) throws Exception {
		String str = "";

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			ps = conn.prepareStatement("select AuditPeople from z_Project where ProjectID=?");
			ps.setString(1, projectId);
			rs = ps.executeQuery();
			if (rs.next()) {
				str = rs.getString("AuditPeople");
				//org.util.Debug.prtOut(str);
				String[] tt = str.split(",");
				return tt;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	/**
	 * 		alrs.add(rs.getString(1));	//taskId
	 *		alrs.add(rs.getString(2));	//projectId
	 *		alrs.add(rs.getString(3));	//taskName
	 *		alrs.add(rs.getString(4));	//user1
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public ArrayList getTask(String projectId) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList al = new ArrayList();
		try {

			new DBConnect().changeDataBaseByProjectid(conn, projectId);

			String sql = "select taskId,ProjectID,taskName,user1 "
						+ " from z_task "
						+ " where ProjectID=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectId);
			rs = ps.executeQuery();

			while (rs.next()) {
				ArrayList alrs = new ArrayList();
				alrs.add(rs.getString(1));	//
				alrs.add(rs.getString(2));
				alrs.add(rs.getString(3));
				alrs.add(rs.getString(4));
				al.add(alrs);
			}
			return al;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public void saveTaskWork(String projectId, String manuid, String userId)
			throws Exception {

		PreparedStatement ps = null;

		try {
			//根据项目ＩＤ切换数据库。
			DBConnect dbc = new DBConnect();
			dbc.changeDataBaseByProjectid(conn, projectId);

			String sql = "";
			sql = "update z_task set user1=? "
				+ " where ProjectID=? "
				+ " and ManuID =? ";

			ps = conn.prepareStatement(sql);
			ps.setString(1,userId);
			ps.setString(2,projectId);
			ps.setString(3,manuid);
			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

	}

	public void delTaskWork(String id, String name) throws Exception {

		PreparedStatement ps = null;
		try {
			//根据项目ＩＤ切换数据库。
			DBConnect dbc = new DBConnect();
			dbc.changeDataBaseByProjectid(conn, id);

			String sql = "";
			sql = "update z_task set user1=null where ProjectID=?  and user1=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1,id);
			ps.setString(2,name);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}

	public String getAMenuDetail(String id) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;
		ASFuntion ch = new ASFuntion();
		try {
			String str = "";
			ps = conn.prepareStatement("select a.*,b.DepartName from z_Project a,k_customer b where ProjectID='"
							+ id + "' and a.CustomerId = b.DepartID");
			rs = ps.executeQuery();
			if (rs.next()) {//ProjectEnd
				str = "<ProjectID>" + rs.getString("ProjectID")
						+ "</ProjectID><AuditDept>" + rs.getString("AuditDept")
						+ "</AuditDept><CustomerId>"
						+ rs.getString("CustomerId")
						+ "</CustomerId><AuditType>"
						+ rs.getString("AuditType") + "</AuditType><AuditPara>"
						+ rs.getString("AuditPara")
						+ "</AuditPara><ProjectName>"
						+ rs.getString("ProjectName")
						+ "</ProjectName><AccPackageID>"
						+ rs.getString("AccPackageID")
						+ "</AccPackageID><ProjectCreated>"
						+ rs.getString("ProjectCreated")
						+ "</ProjectCreated><ProjectEnd>"
						+ rs.getString("ProjectEnd") + "</ProjectEnd><State>"
						+ rs.getString("State") + "</State><AuditPeople>"
						+ rs.getString("AuditPeople")
						+ "</AuditPeople><Property>" + rs.getString("Property")
						+ "</Property><AuditTimeBegin>"
						+ rs.getString("AuditTimeBegin")
						+ "</AuditTimeBegin><AuditTimeEnd>"
						+ rs.getString("AuditTimeEnd")
						+ "</AuditTimeEnd><DepartName>"
						+ rs.getString("DepartName")
						+ "</DepartName><standbyname>"
						+ ch.showNull(rs.getString("standbyname"))
						+ "</standbyname><TemplateType>"
						+ rs.getString("TemplateType")
						+"</TemplateType><DepartmentId>"
						+ ch.showNull(rs.getString("DepartmentId"))
						+"</DepartmentId>";
			}
			//             org.util.Debug.prtOut(str);
			return str;
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
	 * 获得项目
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public Project getProjectById(String projectId) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;
		Project project = new Project();
		try {

			ps = conn.prepareStatement("select * from z_Project where ProjectID=?");
			ps.setString(1, projectId);
			rs = ps.executeQuery();
			if (rs.next()) {
				
				project.setProjectId(rs.getString("ProjectID"));
				project.setAuditDept(rs.getString("AuditDept"));
				project.setCustomerId(rs.getString("CustomerId"));
				project.setAccPackageId(rs.getString("AccPackageID"));
				project.setAuditType(rs.getString("AuditType"));
				
				project.setAuditPara(rs.getString("AuditPara"));
				project.setProjectName(rs.getString("ProjectName"));
				project.setProjectCreated(rs.getString("ProjectCreated"));
				project.setProjectEnd(rs.getString("projectEnd"));
				project.setState(rs.getString("State"));
				
				project.setAuditPeople(rs.getString("AuditPeople"));
				project.setProperty(rs.getString("Property"));
				project.setAuditTimeBegin(rs.getString("AuditTimeBegin"));
				project.setAuditTimeEnd(rs.getString("AuditTimeEnd"));
				project.setRealStartDate(rs.getString("realStartDate"));
				
				project.setRealEndDate(rs.getString("realEndDate"));
				project.setStandbyName(rs.getString("standbyName"));
				//systemid
				project.setTemplateType(rs.getString("TemplateType")) ;//?
				project.setDepartmentId(rs.getString("departmentId"));
				
				project.setGroupName(rs.getString("groupName"));
				project.setGroupProjectName(rs.getString("groupProjectName"));
				project.setGroupProjectId(rs.getString("groupProjectId"));
				project.setProjectType(rs.getString("projectType"));
				project.setCreateTime(rs.getString("createtime"));
				
				project.setCreateUser(rs.getString("createUser"));
				project.setProjectYear(rs.getString("ProjectYear"));
				project.setShortName(rs.getString("shortName"));
				project.setPostil(rs.getString("postil"));
				
				
			}
			return project;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	public String getCurrName(String acc) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String result = "";
		try {

			String sql = "select CurrName from c_accpackage where accpackageid = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, acc);
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

	public ArrayList getLogin(String pid) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ArrayList al = new ArrayList();

			String sql = " select substring(a.AuditTimeBegin,1,4) beginyear , "
						+ " substring(a.AuditTimeBegin,6,2) beginmonth, "
						+ " substring(a.AuditTimeEnd,1,4) endyear, "
						+ " substring(a.AuditTimeEnd,6,2) endmonth, "
						+ " a.accpackageid,a.ProjectName,a.CustomerId,b.DepartName,c.Role,a.property,a.standbyname,a.State,a.systemId,a.postil "
						+ " from z_Project a inner join k_customer b on a.CustomerId=b.DepartID"
						+ " left join z_auditpeople c on a.projectid=c.projectID" 
						+ " where a.projectid = ? " ;
						
			ps = conn.prepareStatement(sql);
			ps.setString(1, pid);
			ps.execute();
			ASFuntion CHF = new ASFuntion();
			rs = ps.executeQuery();
			if (rs.next()) {
				al.add(rs.getString("accpackageid"));
				al.add(rs.getString("ProjectName"));
				al.add(rs.getString("CustomerId"));
				al.add(rs.getString("DepartName"));
				al.add(rs.getString("Role"));
				al.add(rs.getString("beginyear"));
				al.add(rs.getString("beginmonth"));
				al.add(rs.getString("endyear"));
				al.add(rs.getString("endmonth"));
				al.add(rs.getString("property"));
				al.add(CHF.showNull(rs.getString("standbyname")));
				al.add(CHF.showNull(rs.getString("State")));
				al.add(rs.getString("systemId"));
				al.add(rs.getString("postil"));

				return al;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 返回参与的项目总数
	 * @param userName
	 * @return
	 * @throws Exception
	 */
	public int getUserProjectCount(String userName) throws Exception {
		int intCount = 0;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String strSql = "select count(*)"
					  + " from z_project"
					  + " where projectid in ( "
					  + " 		select projectid from z_auditpeople "
					  + "		where userid= (select id from k_user where loginid=?) "
					  + " ) ";

		try {

			ps = conn.prepareStatement(strSql);

			ps.setString(1, userName);
			rs = ps.executeQuery();
			if (rs.next()) {
				intCount = rs.getInt(1);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);

		}
		return intCount;
	}

	/**
	 * 判断项目是否已经关闭
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public boolean isClose(String projectId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			String strSql = "select 1 from z_project where projectId = ? and State = 2";

			ps = conn.prepareStatement(strSql);

			ps.setString(1, projectId);

			rs = ps.executeQuery();

			if (rs.next()) {
				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
		}
		return false;
	}

	/**
	 * 判断用户是否参与了项目分工
	 * @param projectId，userid
	 * @return
	 * @throws Exception
	 */
	public int isJoin(String projectid, String userid) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select count(*) from z_auditpeople,k_user where projectid ="

				+ projectid + " and loginid='" + userid + "' and userid=id";

		try {

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			//ps.setInt(4,Integer.parseInt(projectid));
			rs.next();
			if (rs.getInt(1) > 0) {
				return 1;
			} else {
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			DbUtil.close(rs);
		}
	}

	public String getByName(String Sid) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		ASFuntion CHF = new ASFuntion();
		String sql = "";
		String result = "";
		try {

			sql = "select departname,standbyname from k_customer where Property=2 and departid='"
					+ Sid + "'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				String s = CHF.showNull(rs.getString(2)).equals("") ? CHF
						.showNull(rs.getString(1)) : CHF.showNull(rs
						.getString(2));
				result = CHF.showNull(rs.getString(1)) + "`" + s;
			}
			return result;
		} catch (Exception e) {
			Debug.print(Debug.iError, "出错时sql=" + sql);
			Debug.print(Debug.iError, "访问失败", e);
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}



	/**
	 * 返回编号格式为 年份+审计类型+4位数字 例如(2006111001) 目前正在使用
	 *
	 * @param typeId
	 * @return
	 * @throws Exception
	 */
	public int getProjectID(int typeId) throws Exception {
		
		return Integer.parseInt(getProjectId(String.valueOf(typeId)));
	}
	
	/**
	 * 返回编号格式为 年份+审计类型+4位数字 例如(2006111001) 目前正在使用
	 *
	 * @param typeId
	 * @return
	 * @throws Exception
	 */
	public String getProjectId(String typeId) throws Exception {
		
		DELAutocode t = new DELAutocode();
		String id = UTILString.nCharToString('0', 2 - typeId.length()) + typeId;
		String[] temp = new String[] { id };

		String strProjectId = t.getAutoCode("XMBH", "", temp);

		try {
			Integer.parseInt(strProjectId);
		} catch(Exception e) {
			System.out.println("项目编号超出范围,无条件取出前面4位年份标识..");
			strProjectId = strProjectId.substring(4);
		}
		
		String sql = " select count(1) from z_project where projectid=? ";
		Object[] objects = new Object[]{strProjectId};
		int count = new DbUtil(conn).queryForInt(sql,objects);
		
		if(count > 0) {
			strProjectId = getProjectId(typeId);
		}

		return strProjectId;
	}
	
	/**
	 * 保存项目的方法
	 * @param project
	 * @return
	 * @throws Exception
	 */
	public String save(Project project) throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String projectId = null;
		
		try {
			
			String typeId = project.getAuditType();
			projectId = getProjectId(typeId);

			//插入项目表
			StringBuffer sql = new StringBuffer();
			
			sql.append(" insert into asdb.z_project( ")
				.append(" ProjectID, AuditDept, CustomerId, AccPackageID, AuditType, ")
				.append(" AuditPara, ProjectName, ProjectCreated, ProjectEnd, State, ")
				.append(" AuditPeople, Property, AuditTimeBegin, AuditTimeEnd, RealStartDate, ")
				.append(" RealEndDate, standbyname, systemid, TemplateType, groupName, ")
				.append(" groupProjectName, groupProjectId, projectType, createTime, createUser,")
				.append(" departmentId,projectYear,shortName,postil ) ")
				.append(" values(?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,now(),?, ?,?,?,? ) ");
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, projectId);
			ps.setString(2, project.getAuditDept());
			ps.setString(3, project.getCustomerId());
			ps.setString(4, project.getAccPackageId());
			ps.setString(5, project.getAuditType());
			
			ps.setString(6, project.getAuditPara());
			ps.setString(7, project.getProjectName());
			ps.setString(8, project.getProjectCreated());
			ps.setString(9, project.getProjectEnd());
			ps.setString(10, project.getState());
			
			ps.setString(11, project.getAuditPeople());
			ps.setString(12, project.getProperty());
			ps.setString(13, project.getAuditTimeBegin());
			ps.setString(14, project.getAuditTimeEnd());
			ps.setString(15, project.getRealStartDate());
			
			ps.setString(16, project.getRealEndDate());
			ps.setString(17, project.getStandbyName());
			ps.setString(18, "0");
			ps.setString(19, project.getTemplateType());
			ps.setString(20, project.getGroupName());
			
			ps.setString(21, project.getGroupProjectName());
			ps.setString(22, project.getGroupProjectId());
			ps.setString(23, project.getProjectType());
			ps.setString(24, project.getCreateUser());
			
			ps.setString(25, project.getDepartmentId());
			ps.setString(26, project.getProjectYear());
			ps.setString(27, project.getShortName());
			ps.setString(28, project.getPostil());
			
			ps.execute();
			
			
			insertOther(projectId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return projectId;
	}
	//增加合同金额
	public void addcontract(String projectId,String cost){
		PreparedStatement ps = null;
		try{
			String sql ="insert into oa_contract(bargainID,bargainmoney) values(?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1,projectId);
			ps.setString(2,cost);
			ps.execute();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	public void updatecontract(String projectId,String cost){
		PreparedStatement ps = null;
		try{
			String sql ="update oa_contract set bargainmoney=? where bargainID=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, cost);
			ps.setString(2, projectId);
			ps.execute();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	public void insertOther(String projectId) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			
			Project project = getProjectById(projectId);
			String typeId = project.getAuditType();
			
			new DBConnect().changeDataBase(project.getCustomerId(), conn);
			
			/* 注释这段,兼容导入模板前就已经有其它底稿的情况
			try {
				ManuFileService excelMan = new ManuFileService(conn);
				//删除底稿目录
				excelMan.deleteDirByProjectID(projectId);
			} catch (Exception e) {
				Debug.print(Debug.iError, "访问失败", e);
			} */
			
			//拷贝模版底稿文件
			new ManuFileService(conn).copyTemplateToProject(typeId, projectId);
	
			// 从任务模板中恢复任务
			/*注释这段,兼容导入模板前就已经有其它底稿的情况
			String str = "delete from z_task where ProjectID=?";
			ps = conn.prepareStatement(str);
			ps.setString(1, projectId);
			ps.execute();
			ps.close();
			*/
			String str = "insert into z_task(TaskID,TaskCode,TaskName,TaskContent,Description,"
					+ "ParentTaskID,ProjectID,IsLeaf,Level0,ManuTemplateID,Property,FullPath,orderid,ismust,subjectname,auditproperty) "
					+ "select TaskID,TaskCode,TaskName,TaskContent,Description,ParentTaskID,?,"
					+ "IsLeaf,Level0,ManuTemplateID,Property,FullPath,orderid,ismust,subjectname,auditproperty "
					+ "from k_tasktemplate where typeid=?";
			ps = conn.prepareStatement(str);
			ps.setString(1, projectId);
			ps.setString(2, typeId);
			ps.execute();
			ps.close();
	
			//底稿表页
			str = "delete from z_sheettask where ProjectID=?";
			ps = conn.prepareStatement(str);
			ps.setString(1, projectId);
			ps.execute();
			ps.close();
			str = "insert into z_sheettask(ProjectID,taskid,sheettaskcode,taskcode,sheetname,property )"
				+ "select ? as ProjectID,taskid,sheettaskcode,taskcode,sheetname,property   "
				+ "from k_sheettasktemplate where typeid=? ";
			ps = conn.prepareStatement(str);
			ps.setString(1, projectId);
			ps.setString(2, typeId);
			ps.execute();
			org.util.Debug.prtOut("拷贝z_sheettask模板成功！");
	
			//审计目标
			str = "delete from  z_target where ProjectID=?";
			ps = conn.prepareStatement(str);
			ps.setString(1, projectId);
			ps.execute();
			ps.close();
			str = "insert into  z_target(ProjectID,TaskID,ExecuteIt,State,DefineID,";
			str = str + "AuditTarget,CorrelationExeProceDure,Remark,cognizance,property )";
			str = str + "select ? as ProjectID,";
			str = str + "TaskID,ExecuteIt,State,DefineID,";
			str = str + "AuditTarget,CorrelationExeProceDure,Remark,cognizance,property  ";
			str = str + "from k_targettemplate where typeid=?";
			ps = conn.prepareStatement(str);
			ps.setString(1, projectId);
			ps.setString(2, typeId);
			ps.execute();
	
			org.util.Debug.prtOut("拷贝z_target模板成功！");
	
			//审计程序
			str = "delete from  z_procedure where ProjectID=?";
			ps = conn.prepareStatement(str);
			ps.setString(1, projectId);
			ps.execute();
			ps.close();
			str = "insert into  z_procedure(autoId,ProjectID,TaskID,State,DefineID,AuditProcedure,";
			str = str + "Manuscript,Executor,Remark,cognizance,parentId,level0,fullpath,property )";
			str = str + "select autoId,? as ProjectID,";
			str = str + "TaskID,State,DefineID,AuditProcedure,";
			str = str + "Manuscript,Executor,Remark,cognizance,parentId,level0,fullpath,property  ";
			str = str + "from k_proceduretemplate where typeid=?";
			ps = conn.prepareStatement(str);
			ps.setString(1, projectId);
			ps.setString(2, typeId);
			ps.execute();
	
			org.util.Debug.prtOut("拷贝z_procedure模板成功！");
	
	
			str = "delete from  z_taskrefer where ProjectID=?";
			ps = conn.prepareStatement(str);
			ps.setString(1, projectId);
			ps.execute();
			ps.close();
			str = "insert into z_taskrefer \n"
				+"select ?,TaskID,TaskCode,SheetName,CellAddress,ReferTaskCode,ReferSheetName,ReferCellAddress1,ReferCellAddress2,property \n"
				+"from asdb.k_taskrefertemplate where typeid=?";
			ps = conn.prepareStatement(str);
			ps.setString(1, projectId);
			ps.setString(2, typeId);
			ps.execute();
	
			org.util.Debug.prtOut("拷贝z_taskrefer模板成功！");
	
//			//更新有对应科目的底稿状态
//			try {
//				new TaskCommonService(conn,projectId).updateTaskHasData();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			
			try {
				str = " insert into z_step(typeId,step,taskCode,menuId,projectId) "
					+ " select typeId,step,taskCode,menuId,?"
					+ " from asdb.k_stepconfig where typeid=?";
				ps = conn.prepareStatement(str);
				ps.setString(1, projectId);
				ps.setString(2, typeId);
				ps.execute();
				ps.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 保存集团项目的方法
	 * @param project
	 * @return
	 * @throws Exception
	 */
	public String groupSave(Project project) throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String projectId = null;
		
		try {
			new DBConnect().changeDataBase(project.getCustomerId(), conn);
			
			String typeId = project.getAuditType();
			projectId = getProjectId(typeId);

			//插入项目表
			StringBuffer sql = new StringBuffer();
			
			sql.append(" insert into asdb.z_project( ")
				.append(" ProjectID, AuditDept, CustomerId, AccPackageID, AuditType, ")
				.append(" AuditPara, ProjectName, ProjectCreated, ProjectEnd, State, ")
				.append(" AuditPeople, Property, AuditTimeBegin, AuditTimeEnd, RealStartDate, ")
				.append(" RealEndDate, standbyname, systemid, TemplateType, groupName, ")
				.append(" groupProjectName, groupProjectId, projectType, createTime, createUser,")
				.append(" departmentId) ")
				.append(" values(?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,now(),?, ? ) ");
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, projectId);
			ps.setString(2, project.getAuditDept());
			ps.setString(3, project.getCustomerId());
			ps.setString(4, project.getAccPackageId());
			ps.setString(5, project.getAuditType());
			
			ps.setString(6, project.getAuditPara());
			ps.setString(7, project.getProjectName());
			ps.setString(8, project.getProjectCreated());
			ps.setString(9, project.getProjectEnd());
			ps.setString(10, project.getState());
			
			ps.setString(11, "");
			ps.setString(12, project.getProperty());
			ps.setString(13, project.getAuditTimeBegin());
			ps.setString(14, project.getAuditTimeEnd());
			ps.setString(15, project.getRealStartDate());
			
			ps.setString(16, project.getRealEndDate());
			ps.setString(17, project.getStandbyName());
			ps.setString(18, "0");
			ps.setString(19, project.getTemplateType());
			ps.setString(20, project.getGroupName());
			
			ps.setString(21, project.getProjectName());
			ps.setString(22, project.getGroupProjectId());
			ps.setString(23, project.getProjectType());
			ps.setString(24, project.getCreateUser());
			
			ps.setString(25, project.getDepartmentId());
			
			ps.execute();
			ps.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return projectId;
	}
	

	/**
	 * 新建一个项目，返回新的projectID 完成的操作包括： 生成PROJECTID，插入z_project一条记录
	 * 从上年项目中拷贝数据到当前项目
	 *
	 *
	 * @param XML String
	 * @return int
	 * @throws Exception
	 */
	public int addLastYearProject(String XML,List reUselist) throws Exception {
		ASFuntion asf = new ASFuntion();
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		PreparedStatement ps4 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;

		String str = "";


		/*
		 屈文浩修改于20070808,修正了取以往项目得方式
		 */
		// 获取上年审计项目编号
		String lastYearProjectid = asf.getXMLData(XML, "LastYearProject");

		//取得上年项目类型和模版编号
		int typeid= Integer.parseInt(getProjectById(lastYearProjectid).getAuditType());

		String auditType = asf.getXMLData(XML, "AuditType");
		if(!"重用上年项目".equals(auditType) && !"".equals(auditType)) {
			typeid = Integer.parseInt(auditType);
		}

		// 生成新得项目编号
		int projectid = getProjectID(typeid);
		
		//新项目的帐套编号
		String accpackageid = asf.getXMLData(XML, "AccPackageID");
		
		//客户数据库
		String databaseName = "asdb_" + asf.getXMLData(XML, "CustomerId") + ".";
		
		try {
			new DBConnect().changeDataBase(asf.getXMLData(XML, "CustomerId"),
					conn);

			// 插入项目表
			str = "INSERT INTO "
					+ databaseName
					+ "z_Project(ProjectID,AuditDept,CustomerId,AccPackageID,AuditType,AuditPara,ProjectName,ProjectCreated,State,AuditPeople,Property,AuditTimeBegin,AuditTimeEnd,ProjectEnd,RealStartDate,RealEndDate,standbyname,TemplateType,DepartmentId) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			ps = conn.prepareStatement(str);
			ps.setInt(1, projectid);
			ps.setString(2, asf.getXMLData(XML, "AuditDept"));
			ps.setString(3, asf.getXMLData(XML, "CustomerId"));
			ps.setString(4, accpackageid);
			ps.setInt(5, typeid);
			ps.setString(6, asf.getXMLData(XML, "AuditPara"));
			ps.setString(7, asf.getXMLData(XML, "ProjectName"));
			ps.setDate(8, Date.valueOf(asf.getXMLData(XML, "ProjectCreated")));
			ps.setInt(9, Integer.parseInt(asf.getXMLData(XML, "State")));
			// ps.setString(10, asf.getXMLData(XML, "AuditPeople"));
			ps.setString(10, "");
			ps.setString(11, asf.getXMLData(XML, "Property"));
			ps.setString(12, asf.getXMLData(XML, "AuditTimeBegin"));
			ps.setString(13, asf.getXMLData(XML, "AuditTimeEnd"));
			ps.setString(14, asf.getXMLData(XML, "ProjectEnd"));
			ps.setString(15, asf.getXMLData(XML, "RealStartDate"));
			ps.setString(16, asf.getXMLData(XML, "RealEndDate"));
			ps.setString(17, asf.getXMLData(XML, "standbyname"));
			ps.setString(18, asf.getXMLData(XML, "TemplateType"));
			
			ps.setString(19, asf.getXMLData(XML, "DepartmentId"));	//部门
			
		    // ps.setString(15, asf.getXMLData(XML, "PlanStartDate"));
			ps.execute();
			ps.close();


			String chouping = "";
			String feilu = "";
			String doubtReport = "";
			String caseReport = "";
			String manuscript = "";

			Iterator ite = reUselist.iterator();

			while(ite.hasNext()){
				chouping = (String)ite.next();
				feilu = (String)ite.next();
				doubtReport = (String)ite.next();
				caseReport = (String)ite.next();
				manuscript = (String)ite.next();
			}

			//重用抽凭记录
			if("useCP".equals(chouping)){

				try {
					str = "insert into z_voucherspotcheck(ProjectID,VchID,Believe,judge,Createor,"
						+" QuestDate,Property,subjectid,entryAccPackageID,entryOldVoucherID,entryTypeID,"
						+" entryVchDate,flowid,entrysubjectid,entrySerail,entrySummary,entryDirction,entryOccurValue,"
						+" entryCurrRate,entryCurrValue,entryCurrency,entryQuantity,entryUnitPrice,entryUnitName,"
						+" entryBankID,entryProperty,entrysubjectname1,entrySubjectFullName1,entryId,voucherDebitOcc,voucherCreditOcc)"
						+" select ?,VchID,Believe,judge,Createor,"
						+" QuestDate,Property,subjectid,entryAccPackageID,entryOldVoucherID,entryTypeID,"
						+" entryVchDate,flowid,entrysubjectid,entrySerail,entrySummary,entryDirction,entryOccurValue,"
						+" entryCurrRate,entryCurrValue,entryCurrency,entryQuantity,entryUnitPrice,entryUnitName,"
						+" entryBankID,entryProperty,entrysubjectname1,entrySubjectFullName1,entryId,voucherDebitOcc,voucherCreditOcc "
						+" from z_voucherspotcheck where projectid= ?";

					ps = conn.prepareStatement(str);
					ps.setInt(1,projectid);
					ps.setString(2,lastYearProjectid);
					ps.execute();

					ps.close();
					
					str = "insert into z_vouchersampleflow(flowid,sampleDate,userId,projectId,sampleFlow,"
						+" sampleMethod,selectSample,subjectId,property) "
						+" select flowid,sampleDate,userId,?,sampleFlow, "
						+" sampleMethod,selectSample,subjectId,property "
						+" from z_vouchersampleflow where projectid= ?";

					ps = conn.prepareStatement(str);
					ps.setInt(1,projectid);
					ps.setString(2,lastYearProjectid);
					ps.execute();

					ps.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			//重用调整分录
			if("useTZ".equals(feilu)){
				String sql = "";
				String sql1 = "";
				String oldautoid = "";
				String oldautoid1 = "";
				String VKeyId = "";
				String VKeyId1 = "";
				String str1 = "";
				String str2 = "";
				try {
					
					//无条件重用自定义科目；
					str="insert into z_usesubject \n"
						+"select ?,"+accpackageid+",subjectid,parentsubjectid,tipsubjectid, \n"
						+"subjectname,subjectfullname,property,level0,isleaf \n"
						+"from z_usesubject where projectid=?";
					ps = conn.prepareStatement(str);
					ps.setInt(1,projectid);
					ps.setString(2,lastYearProjectid);
					ps.execute();
					ps.close();
					
					//重用调整
					str = "insert into z_voucherrectify(autoid,AccPackageID,projectid,VoucherID,TypeID,VchDate,FillUser,AuditUser,KeepUser,"
						+"	Director,AffixCount,Description,DoubtUserId,Property)"
						+"	select ?,"+accpackageid+",?,VoucherID,TypeID,VchDate,FillUser,AuditUser,KeepUser,"
						+"	Director,AffixCount,Description,DoubtUserId,Property"
						+"	from z_voucherrectify"
						+"	where projectid = ? and autoid=?";
					ps1 = conn.prepareStatement(str);

					//重用调整分录
					str1 = "insert into z_subjectentryrectify("
						+"	autoid,AccPackageID,projectid,VoucherID,OldVoucherID,TypeID,VchDate,Serail,Summary,SubjectID,Dirction,"
						+"	OccurValue,CurrRate,CurrValue,Currency,Quantity,UnitPrice,BankID,Property"
						+"	)"
						+"	select ?,"+accpackageid+",?,?,OldVoucherID,TypeID,VchDate,Serail,Summary,SubjectID,Dirction,"
						+"	OccurValue,CurrRate,CurrValue,Currency,Quantity,UnitPrice,BankID,Property"
						+"	from z_subjectentryrectify"
						+"	where projectid = ? and autoid=?";
					ps3 = conn.prepareStatement(str1);

					//重用核算
					str2 = " insert into z_assitementryrectify(accpackageId,ProjectID,entryId,AssItemID,subjectId) "
						 +" select "+accpackageid+",?,?,AssItemID,subjectId from z_assitementryrectify where projectid=? and entryid=?";
					ps4 = conn.prepareStatement(str2);

//					重用调整
					sql = " select autoid from z_voucherrectify where projectid="+lastYearProjectid+" ";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()) {
						VKeyId = new DELAutocode().getAutoCode("AUVO", "");
						oldautoid = rs.getString(1);

						ps1.setString(1,VKeyId);
						ps1.setInt(2,projectid);
						ps1.setString(3,lastYearProjectid);
						ps1.setString(4,oldautoid);
						ps1.addBatch();

						//重用调整分录
						sql1 = " select distinct autoid from z_subjectentryrectify where projectid="+lastYearProjectid+" and voucherid="+oldautoid+" ";
						ps2 = conn.prepareStatement(sql1);
						rs1 = ps2.executeQuery();
						while(rs1.next()) {
							VKeyId1 = new DELAutocode().getAutoCode("SUAU", "");
							oldautoid1 = rs1.getString(1);

							ps3.setString(1,VKeyId1);
							ps3.setInt(2,projectid);
							ps3.setString(3,VKeyId);
							ps3.setString(4,lastYearProjectid);
							ps3.setString(5,oldautoid1);
							ps3.addBatch();

							ps4.setInt(1, projectid);
							ps4.setString(2, VKeyId1);
							ps4.setString(3, lastYearProjectid);
							ps4.setString(4, oldautoid1);
							ps4.addBatch();

						}
					}
					ps1.executeBatch();
					ps3.executeBatch();
					ps4.executeBatch();

					//这里直接close有时会报错
					/*rs.close();
					rs1.close();
					ps.close();
					ps1.close();
					ps2.close();
					ps3.close();
					ps4.close();*/
					
					DbUtil.close(rs);
					DbUtil.close(rs1);
					DbUtil.close(ps);
					DbUtil.close(ps1);
					DbUtil.close(ps2);
					DbUtil.close(ps3);
					DbUtil.close(ps4);
					
					

					//汇总

					Repair repair = new Repair(conn);
					repair.insertData(accpackageid,String.valueOf(projectid));

					RectifyService vm = new RectifyService(conn);
					vm.createTzhz(accpackageid,String.valueOf(projectid));
					vm.createWbTzhz(accpackageid, String.valueOf(projectid));
					vm.createAssitem1(accpackageid,String.valueOf(projectid));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			//重用疑点记录
			if("useYD".equals(doubtReport)){

				try {
					str = "insert into z_question("
						+"	projectid,QuestDate,VchID,Judge,QuestMoney,AccID,Createor,"
						+"	Property,AccPackageID,OldVoucherID,TypeID,VchDate"
						+"	)"
						+"	select ?,QuestDate,VchID,Judge,QuestMoney,AccID,Createor,"
						+"	Property,"+accpackageid+",OldVoucherID,TypeID,VchDate"
						+"	from z_question"
						+"	where projectid = ?";

					ps = conn.prepareStatement(str);
					ps.setInt(1,projectid);
					ps.setString(2,lastYearProjectid);
					ps.execute();

					ps.close();

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			//重用函证记录
			if("useHZ".equals(caseReport)){

				try {
					str = "insert into z_letters("
						+"	createuser,createtime,projectid,subjectid,`name`,AssItemID,MANUID,hasreturn,memo,returntime,property,"
						+"	funcOccur,letOccour,factOcc,isReplace,DataName"
						+"	)"
						+"	select createuser,createtime,?,subjectid,`name`,AssItemID,MANUID,hasreturn,memo,returntime,property,"
						+"	funcOccur,letOccour,factOcc,isReplace,DataName"
						+"	from z_letters"
						+"	where projectid = ?";
					ps = conn.prepareStatement(str);
					ps.setInt(1,projectid);
					ps.setString(2,lastYearProjectid);
					ps.execute();

					ps.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			//重用底稿
			if("useManuScript".equals(manuscript)){

				try {
					ManuFileService excelMan = new ManuFileService(conn);
					//删除底稿目录
					excelMan.deleteDirByProjectID(String.valueOf(projectid));
				} catch (Exception e) {
					Debug.print(Debug.iError, "访问失败", e);
				}

				//拷贝模版底稿文件
				new ManuFileService(conn).copyProjectToProject(String
						.valueOf(projectid), lastYearProjectid);

				// 从任务模板中恢复任务
				str = "delete from z_task where ProjectID=?";
				ps = conn.prepareStatement(str);
				ps.setInt(1, projectid);
				ps.execute();
				ps.close();
				str = "insert into z_task(TaskID,TaskCode,TaskName,TaskContent,Description,"
						+ "ParentTaskID,ProjectID,IsLeaf,Level0,ManuID,ManuTemplateID,Property,FullPath,orderid,ismust,subjectname) "
						+ "select TaskID,TaskCode,TaskName,TaskContent,Description,ParentTaskID,?,"
						+ "IsLeaf,Level0,ManuID,ManuTemplateID,Property,FullPath,orderid,ismust,subjectname "
						+ "from z_task where projectid=? "
						+ " and (property<>'print' or property is null or property='')";
				ps = conn.prepareStatement(str);
				ps.setInt(1, projectid);
				ps.setString(2, lastYearProjectid);
				ps.execute();
				ps.close();

				str = "update  z_task set manuid=null where ProjectID=? and isleaf=0";
				ps = conn.prepareStatement(str);
				ps.setInt(1, projectid);
				ps.execute();
				ps.close();

				/**/
				str = "delete from  z_target where ProjectID=?";
				ps = conn.prepareStatement(str);
				ps.setInt(1, projectid);
				ps.execute();
				ps.close();
				str = "insert into  z_target(ProjectID,TaskID,ExecuteIt,State,DefineID,";
				str = str + "AuditTarget,CorrelationExeProceDure,Remark)";
				str = str + "select ? as ProjectID,";
				str = str + "TaskID,ExecuteIt,State,DefineID,";
				str = str + "AuditTarget,CorrelationExeProceDure,Remark ";
				str = str + "from z_target where projectid=?";
				ps = conn.prepareStatement(str);
				ps.setInt(1, projectid);
				ps.setString(2, lastYearProjectid);
				ps.execute();

				org.util.Debug.prtOut("拷贝上年z_target成功！");

				str = "delete from  z_procedure where ProjectID=?";
				ps = conn.prepareStatement(str);
				ps.setInt(1, projectid);
				ps.execute();
				ps.close();
				str = "insert into  z_procedure(ProjectID,TaskID,State,DefineID,AuditProcedure,";
				str = str + "Manuscript,Executor,Remark)";
				str = str + "select ? as ProjectID,";
				str = str + "TaskID,State,DefineID,AuditProcedure,";
				str = str + "Manuscript,Executor,Remark ";
				str = str + "from z_procedure where projectid=?";
				ps = conn.prepareStatement(str);
				ps.setInt(1, projectid);
				ps.setString(2, lastYearProjectid);
				ps.execute();

				org.util.Debug.prtOut("拷贝上年z_procedure成功！");


				str = "delete from  z_taskrefer where ProjectID=?";
				ps = conn.prepareStatement(str);
				ps.setInt(1, projectid);
				ps.execute();
				ps.close();
				str = "insert into z_taskrefer \n"
					+"select ?,TaskID,TaskCode,SheetName,CellAddress,ReferTaskCode,ReferSheetName,ReferCellAddress1,ReferCellAddress2,property \n"
					+"from z_taskrefer where projectid=?";
				ps = conn.prepareStatement(str);
				ps.setInt(1, projectid);
				ps.setString(2, lastYearProjectid);
				ps.execute();

				org.util.Debug.prtOut("拷贝上年z_taskrefer成功！");

			} else {
				try {
					ManuFileService excelMan = new ManuFileService(conn);
					//删除底稿目录
					excelMan.deleteDirByProjectID(String.valueOf(projectid));
				} catch (Exception e) {
					Debug.print(Debug.iError, "访问失败", e);
				}

				//拷贝模版底稿文件
				new ManuFileService(conn).copyTemplateToProject(String
						.valueOf(typeid), String.valueOf(projectid));

				// 从任务模板中恢复任务
				str = "delete from z_task where ProjectID=?";
				ps = conn.prepareStatement(str);
				ps.setInt(1, projectid);
				ps.execute();
				ps.close();
				str = "insert into z_task(TaskID,TaskCode,TaskName,TaskContent,Description,"
						+ "ParentTaskID,ProjectID,IsLeaf,Level0,ManuID,ManuTemplateID,Property,FullPath,orderid,ismust,subjectname,auditproperty) "
						+ "select TaskID,TaskCode,TaskName,TaskContent,Description,ParentTaskID,?,"
						+ "IsLeaf,Level0,ManuTemplateID,ManuTemplateID,Property,FullPath,orderid,ismust,subjectname,auditproperty "
						+ "from k_tasktemplate where typeid=?";
				ps = conn.prepareStatement(str);
				ps.setInt(1, projectid);
				ps.setInt(2, typeid);
				ps.execute();
				ps.close();

				//底稿表页
				str = "delete from z_sheettask where ProjectID=?";
				ps = conn.prepareStatement(str);
				ps.setInt(1, projectid);
				ps.execute();
				ps.close();
				str = "insert into z_sheettask(ProjectID,taskid,sheettaskcode,taskcode,sheetname,property )"
					+ "select ? as ProjectID,taskid,sheettaskcode,taskcode,sheetname,property   "
					+ "from k_sheettasktemplate where typeid=? ";
				ps = conn.prepareStatement(str);
				ps.setInt(1, projectid);
				ps.setInt(2, typeid);
				ps.execute();
				org.util.Debug.prtOut("拷贝z_sheettask模板成功！");

				//审计目标
				str = "delete from  z_target where ProjectID=?";
				ps = conn.prepareStatement(str);
				ps.setInt(1, projectid);
				ps.execute();
				ps.close();
				str = "insert into  z_target(ProjectID,TaskID,ExecuteIt,State,DefineID,";
				str = str + "AuditTarget,CorrelationExeProceDure,Remark,cognizance,property )";
				str = str + "select ? as ProjectID,";
				str = str + "TaskID,ExecuteIt,State,DefineID,";
				str = str + "AuditTarget,CorrelationExeProceDure,Remark,cognizance,property  ";
				str = str + "from k_targettemplate where typeid=?";
				ps = conn.prepareStatement(str);
				ps.setInt(1, projectid);
				ps.setInt(2, typeid);
				ps.execute();

				org.util.Debug.prtOut("拷贝z_target模板成功！");

				//审计程序
				str = "delete from  z_procedure where ProjectID=?";
				ps = conn.prepareStatement(str);
				ps.setInt(1, projectid);
				ps.execute();
				ps.close();
				str = "insert into  z_procedure(autoId,ProjectID,TaskID,State,DefineID,AuditProcedure,";
				str = str + "Manuscript,Executor,Remark,cognizance,parentId,level0,fullpath,property )";
				str = str + "select autoId,? as ProjectID,";
				str = str + "TaskID,State,DefineID,AuditProcedure,";
				str = str + "Manuscript,Executor,Remark,cognizance,parentId,level0,fullpath,property  ";
				str = str + "from k_proceduretemplate where typeid=?";
				ps = conn.prepareStatement(str);
				ps.setInt(1, projectid);
				ps.setInt(2, typeid);
				ps.execute();

				org.util.Debug.prtOut("拷贝z_procedure模板成功！");


				str = "delete from  z_taskrefer where ProjectID=?";
				ps = conn.prepareStatement(str);
				ps.setInt(1, projectid);
				ps.execute();
				ps.close();
				str = "insert into z_taskrefer \n"
					+"select ?,TaskID,TaskCode,SheetName,CellAddress,ReferTaskCode,ReferSheetName,ReferCellAddress1,ReferCellAddress2,property \n"
					+"from asdb.k_taskrefertemplate where typeid=?";
				ps = conn.prepareStatement(str);
				ps.setInt(1, projectid);
				ps.setInt(2, typeid);
				ps.execute();

				org.util.Debug.prtOut("拷贝z_taskrefer模板成功！");

				Repair repair = new Repair(conn);
				repair.insertData(accpackageid,String.valueOf(projectid));
				//调整汇总
				RectifyService vm =new RectifyService(conn);
				vm.createTzhz(accpackageid,String.valueOf(projectid));
				vm.createWbTzhz(accpackageid, String.valueOf(projectid));
				vm.createAssitem1(accpackageid,String.valueOf(projectid));

//				new ManuacCountService(conn).insertManuacCount(String.valueOf(projectid));

				//更新有对应科目的底稿状态
				try {
					new TaskCommonService(conn,String.valueOf(projectid)).updateTaskHasData();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			/**
			 * py 重建ManuacCount表
			 */
//			new ManuacCountService(conn).insertManuacCount(String.valueOf(projectid));

		} catch (Exception e) {
			org.util.Debug.prtOut("出错时sql=" + str);
			Debug.print(Debug.iError, "访问失败", e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return projectid;
	}

	/**
	 *
	 * 把当前项目的所有资料另存为模版，以便建项时复用
	 *
	 * @param projectid
	 *            int
	 * @param auditType
	 *            int auditType=-1的时候新建一个项目底稿类型， 返回新的auditType，否则返回老的auditType；
	 * @param strAuditName
	 *            String
	 * @return int
	 * @throws Exception
	 */
	public int copyProject(int projectid, int auditType, String strAuditName,
			String userid, String password) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			//	    	   切换数据库
			new DBConnect().changeDataBaseByProjectid(conn, String
					.valueOf(projectid));

			String str;

			if (auditType == -1) {
				// 新建项目类型时生成项目类型
				// str = "select max(typeid) from k_audittypetemplate";
				// ps = conn.prepareStatement(str);
				// rs = ps.executeQuery();
				// if (rs.next()) {
				// auditType = rs.getInt(1) + 1;
				// }
				// rs.close();
				// ps.close();

				auditType = new AuditTypeTemplateService(conn).getMaxTypeId();
				//Integer.parseInt(new DELAutocode().getAutoCode("AUTT", ""));

				str = "insert into k_audittypetemplate (typeid,typename,content,updateTime,updateUser,Property,mypassword) "
						+ "select '" + auditType + "','" + strAuditName + "',content,now(),'" + userid + "',a.Property,'" + password + "' from k_audittypetemplate a,z_project b "
						+ "where b.projectid= '" + projectid + "' and  a.typeid=b.audittype";

				System.out.println(str);
				
				ps = conn.prepareStatement(str);
				/*ps.setInt(1, auditType);
				ps.setString(2, strAuditName);
				ps.setString(3, userid);
				ps.setInt(4, projectid);*/
				ps.execute();
				ps.close();
				//
				//					/* 新建底稿类型时还要复制一套原有底稿模版的底稿块取数公式 */
				//					str = "insert into k_areafunction "
				//							+ "select id,?,strsql,memo from k_areafunction a,z_project b "
				//							+ "where b.projectid=? and a.typeid=b.audittype";
				//					ps = conn.prepareStatement(str);
				//					ps.setInt(1, auditType);
				//					ps.setInt(2, projectid);
				//					ps.execute();
				//					ps.close();
				//					org.util.Debug.prtOut("复制到k_areafunction成功！");
			} else {
				//无条件备份模板
				try {

					String fileName = "系统自动备份_模板覆盖[" + auditType + "]_" + "_" + DataBackup.getCurrentDateTime() + ".zip";;
					new TemplateBackup().backup(fileName, String.valueOf(auditType), "系统自动备份");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			try {
				//删除底稿目录
				ManuFileService.deleteDirByTypeID(String.valueOf(auditType));
			} catch (Exception e) {
				Debug.print(Debug.iError, "访问失败", e);
			}


			//对文件进行处理
			ManuFileService manuScriptManage = new ManuFileService(conn);
			manuScriptManage.copyProjectToTemplate(String.valueOf(projectid),
					String.valueOf(auditType));

			List list = getOtherTaskList(String.valueOf(projectid));
			for (Iterator it = list.iterator(); it.hasNext();) {
				ManuFileService.deleteFileByTaskIdAndTypeID(
						(String) it.next(), String.valueOf(auditType));
			}


			/* 删除指定任务模板 */
			str = "delete from k_tasktemplate WHERE typeid=?";
			ps = conn.prepareStatement(str);
			ps.setInt(1, auditType);
			ps.execute();
			ps.close();
			org.util.Debug.prtOut("预先删除k_tasktemplate成功！");

			//清理taskcode为空的数据
			/*
			str = "delete from z_task WHERE taskcode='' and projectid=? ";
			ps = conn.prepareStatement(str);
			ps.setLong(1, projectid);
			ps.execute();
			ps.close();
			*/

			/* 插入任务模板表 */
			str = "insert into k_tasktemplate(TaskID, TaskCode, TaskName, TaskContent, Description, ParentTaskID,TypeID, "
				+ " IsLeaf,Level0, ManuID, ManuTemplateID, Property, FullPath, orderid, ismust, SubjectName, Udate, Username,auditproperty) "
				+ "select a.TaskID,a.TaskCode,a.TaskName,a.TaskContent,a.Description,a.ParentTaskID,"
				+ "?,a.IsLeaf,a.Level0,0,0,a.Property,a.FullPath,a.orderid,a.ismust,a.subjectname,null,null,a.auditproperty "
				+ "from ("
				+ "		select TaskID,TaskCode,TaskName,TaskContent,Description,ParentTaskID,"
				+ "		IsLeaf,Level0,Property,FullPath,manuid,orderid, if(ismust like '%1%',1,'') as ismust,subjectname,auditproperty "
				+ "		from z_task where projectid=?  and (property<>'print' or property is null) "
				+ ") a ";
			ps = conn.prepareStatement(str);
			ps.setInt(1, auditType);
			ps.setLong(2, projectid);

			ps.execute();
			ps.close();
			org.util.Debug.prtOut("复制到k_tasktemplate成功！");

			/* 导入表页到模板 */
			str = "delete from k_sheettasktemplate where typeid=?";
			ps = conn.prepareStatement(str);
			ps.setInt(1, auditType);
			ps.execute();
			ps.close();
			str = "INSERT into k_sheettasktemplate (typeid,taskid,sheettaskcode,taskcode,sheetname,property )"
					+ " select ?,taskid,sheettaskcode,taskcode,sheetname,property  "
					+ " from z_sheettask where projectid=? ";
			ps = conn.prepareStatement(str);
			ps.setInt(1, auditType);
			ps.setInt(2, projectid);
			ps.execute();
			ps.close();
			org.util.Debug.prtOut("复制到k_sheettasktemplate成功！");

			/* 导入目标表到模板表 */
			str = "delete from k_targettemplate where typeid=?";
			ps = conn.prepareStatement(str);
			ps.setInt(1, auditType);
			ps.execute();
			ps.close();
			str = "INSERT into k_targettemplate (typeid,taskid,ExecuteIt,state,defineid,audittarget,CorrelationExeProcedure,remark,cognizance,property )"
					+ "select ?,taskid,0,state,defineid,audittarget,CorrelationExeProcedure,remark,cognizance,property  "
					+ "from z_target where projectid=?";
			ps = conn.prepareStatement(str);
			ps.setInt(1, auditType);
			ps.setInt(2, projectid);
			ps.execute();
			ps.close();
			org.util.Debug.prtOut("复制到k_targettemplate成功！");

			/* 导入程序表到模板表 */
			str = "delete from k_proceduretemplate where typeid=?";
			ps = conn.prepareStatement(str);
			ps.setInt(1, auditType);
			ps.execute();
			ps.close();

			str = "insert into k_proceduretemplate(autoid,typeid, taskid, state, defineid,"
					+ "auditprocedure, manuscript, executor,remark,cognizance,parentId,level0,fullpath,property)"
					+ "select autoid,?, taskid, state, defineid, auditprocedure, manuscript,"
					+ "'', remark,cognizance,parentId,level0,fullpath,property  "
					+ " from z_procedure where projectid =?";
			ps = conn.prepareStatement(str);
			ps.setInt(1, auditType);
			ps.setInt(2, projectid);
			ps.execute();
			ps.close();

			str = "update k_proceduretemplate set state='未完成' where typeid =? and state='已完成'";
			ps = conn.prepareStatement(str);
			ps.setInt(1, auditType);
			ps.execute();
			ps.close();

			org.util.Debug.prtOut("复制到k_proceduretemplate成功！");




			/* 导入底稿引用表到模板表 */
			str = "delete from asdb.k_taskrefertemplate where typeid=?";
			ps = conn.prepareStatement(str);
			ps.setInt(1, auditType);
			ps.execute();
			ps.close();
			str = "insert into asdb.k_taskrefertemplate "
				+"select ?,TaskID,TaskCode,SheetName,CellAddress,ReferTaskCode,ReferSheetName,ReferCellAddress1,ReferCellAddress2,property \n"
				+"from z_taskrefer where projectid=?";
			ps = conn.prepareStatement(str);
			ps.setInt(1, auditType);
			ps.setInt(2, projectid);
			ps.execute();
			ps.close();
			org.util.Debug.prtOut("复制到k_taskrefertemplate成功！");

			/* 更新模板时间 */
			str = "update k_audittypetemplate set updateTime = now(),updateUser = ? where typeid = ?";
			ps = conn.prepareStatement(str);
			ps.setString(1, userid);
			ps.setInt(2, auditType);
			ps.execute();
			ps.close();

			//	===========更新底稿公式
			//				先查出本项目的底稿类型
			int curAuditType = 0;
			str = "select audittype from z_project where projectid=?";
			ps = conn.prepareStatement(str);
			ps.setInt(1, projectid);
			rs = ps.executeQuery();
			if (rs.next()) {
				curAuditType = rs.getInt(1);
			} else {
				throw new Exception("在z_project表中找不到projectid=[" + projectid
						+ "]的记录");
			}

			//如果覆盖的是当前项目，就没必要copy公式
			if (curAuditType == auditType) {

			} else {
				//复制前先删除原有的公式
				str = "delete from asdb.k_areafunction where typeid=?";
				ps = conn.prepareStatement(str);
				ps.setInt(1, auditType);
				ps.execute();

				/* 新建或者复制底稿类型时还要复制一套原有底稿模版的底稿块取数公式 */
				str = "insert into asdb.k_areafunction(id,typeid,strsql,memo,classpath,HiddenCol) "
						+ " select id,?,strsql,memo,classpath,HiddenCol from asdb.k_areafunction  "
						+ " where typeid=?";
				ps = conn.prepareStatement(str);
				ps.setInt(1, auditType);
				ps.setInt(2, curAuditType);
				ps.execute();
				org.util.Debug.prtOut("复制到k_areafunction成功！");
			}

			ps.close();

			try {
				File file = new File(ManuFileService.getTemplateDir(String.valueOf(auditType)).getAbsolutePath() + "/recycle");
				ManuFileService.deleteFile(file);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new Exception("更新模板失败", e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return auditType;
	}

	// 新建审计人员
	public void addPeople(String auditPeople, int projectID) throws Exception {
		// ASFuntion asf = new ASFuntion();
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;

		int projectid = projectID;
		String[] ap = auditPeople.split(";");
		String str;
		try {

			for (int i = 0; i < ap.length; i++) {
				String[] aps = ap[i].split(",");
				str = "insert into z_auditpeople (projectid,userid,role,isAudit) values(?,?,?,?)";
				ps = conn.prepareStatement(str);
				ps.setInt(1, projectid);
				ps.setString(2, aps[0]);
				ps.setString(3, aps[1]);
				ps.setString(4, aps[2]);
				ps.execute();
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
		} finally {
			DbUtil.close(ps);

		}

	}
	
	/**
	 * 更新项目信息
	 * @param project
	 * @throws Exception
	 */
	public void update(Project project) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			
			new DBConnect().changeDataBaseByProjectid(project.getProjectId(), conn);
			
			boolean bChangeAccPackageId=false;
			//检查是否有替换结束帐套；
			sql="select count(*) as hj from z_project where projectid=? and AccPackageID=?";
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, project.getProjectId());
			ps.setString(2, project.getAccPackageId());
			rs=ps.executeQuery();
			if (rs.next()){
				System.out.println(rs.getInt(1));
				if (!(rs.getInt(1)>0)){
					//说明替换了项目帐套区间
					bChangeAccPackageId=true;
				}
			}
			DbUtil.close(rs);
			
			//执行更新
			sql="update z_Project "
				+" set projectType=?,AuditDept=?, CustomerId=?, AccPackageID=?, AuditType=?, "
				+" AuditPara=?, ProjectName=?, ProjectCreated=?, ProjectEnd=?, State=?, "
				+" AuditPeople=?, Property=?, AuditTimeBegin=?, AuditTimeEnd=?, RealStartDate=?, "
				+" RealEndDate=?, standbyname=?, systemid=?, TemplateType=?, groupName=?, "
				+" groupProjectName=?, groupProjectId=?,departmentId=?,createTime=?,createUser=?, "
				+" shortName=?,postil=? "
				+" where ProjectID=? ";
			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(1, project.getProjectType());
			ps.setString(2, project.getAuditDept());
			ps.setString(3, project.getCustomerId());
			ps.setString(4, project.getAccPackageId());
			ps.setString(5, project.getAuditType());
			
			ps.setString(6, project.getAuditPara());
			ps.setString(7, project.getProjectName());
			ps.setString(8, project.getProjectCreated());
			ps.setString(9, project.getProjectEnd());
			ps.setString(10, project.getState());
			
			ps.setString(11, project.getAuditPeople());
			ps.setString(12, project.getProperty());
			ps.setString(13, project.getAuditTimeBegin());
			ps.setString(14, project.getAuditTimeEnd());
			ps.setString(15, project.getRealStartDate());
			
			ps.setString(16, project.getRealEndDate());
			ps.setString(17, project.getStandbyName());
			ps.setString(18, "0");
			ps.setString(19, project.getTemplateType());
			ps.setString(20, project.getGroupName());
			
			ps.setString(21, project.getGroupProjectName());
			ps.setString(22, project.getGroupProjectId());
			ps.setString(23, project.getDepartmentId());
			ps.setString(24, project.getCreateTime());
			ps.setString(25, project.getCreateUser());
			
			ps.setString(26, project.getShortName());
			ps.setString(27, project.getPostil());
			
			ps.setString(28, project.getProjectId());

			ps.executeUpdate();
			ps.close();
			
			if  (bChangeAccPackageId){
				//同步修改新增科目表，原来是准备新增的，现在看来必须删除了！
				sql="delete from z_usesubject where projectid='"+project.getProjectId()+"' and accpackageid<>'"+project.getAccPackageId()+"'";
				ps = conn.prepareStatement(sql);
				ps.executeUpdate();
			}
			
			
		} catch (Exception e) {
			System.out.println("sql="+sql);
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	// 修改项目
	public void editProject(String XML, int projectID,String Setdef,String Setdef2) throws Exception {
		ASFuntion asf = new ASFuntion();
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;

		int projectid = projectID;
		String sql = "";

		try {

			String theproperty = projectid + "";

			sql = "delete from k_userdef where (contrastid ='common' or contrastid='self') and property="+projectid;

			ps = conn.prepareStatement(sql);
			ps.execute();


			if(!"".equals(Setdef)){
				String []theSetdefs = Setdef.split("\\|");

				for(int i = 0; i < theSetdefs.length; i++){

					sql = "insert into k_userdef(name,value,property,ContrastID) values(?,?,?,?)";
					ps = conn.prepareStatement(sql);

					String []def = theSetdefs[i].split("`");

					ps.setString(1,def[0]);
					ps.setString(2,def[1]);
					ps.setString(3,theproperty);
					ps.setString(4,"common");

					ps.execute();
					
					saveProjectext( String.valueOf(projectid), def[0], def[1]); 

				}
			}

			if(!"".equals(Setdef2)){

			    String []theSetdefs2 = Setdef2.split("\\|");

				for(int i = 0; i < theSetdefs2.length; i++){

					sql = "insert into k_userdef(name,value,property,ContrastID) values(?,?,?,?)";
					ps = conn.prepareStatement(sql);

					String []def = theSetdefs2[i].split("`");

					ps.setString(1,def[0]);
					ps.setString(2,def[1]);
					ps.setString(3,theproperty);
					ps.setString(4,"self");

					ps.execute();
					
					saveProjectext( String.valueOf(projectid), def[0], def[1]); 

				}

			}

			ps.close();





			new DBConnect().changeDataBaseByProjectid(String.valueOf(projectid), conn);
			String str;

			str = "update z_Project set AuditDept=?,CustomerId=?,AccPackageID=?,ProjectName=?,ProjectCreated=?,State=?,AuditPeople=?,Property=?,AuditTimeBegin=?,AuditTimeEnd=?,ProjectEnd=?,standbyname=?,TemplateType=?,DepartmentId=? where  ProjectID=? ";
			ps = conn.prepareStatement(str);
			ps.setInt(15, projectid);
			
			ps.setString(1, asf.getXMLData(XML, "AuditDept"));
			ps.setString(2, asf.getXMLData(XML, "CustomerId"));
			ps.setString(3, asf.getXMLData(XML, "AccPackageID"));
			// ps.setInt(4, Integer.parseInt(asf.getXMLData(XML, "AuditPara")));
			ps.setString(4, asf.getXMLData(XML, "ProjectName"));
			ps.setDate(5, Date.valueOf(asf.getXMLData(XML, "ProjectCreated")));
			ps.setInt(6, Integer.parseInt(asf.getXMLData(XML, "State")));
			ps.setString(7, "");
			ps.setString(8, asf.getXMLData(XML, "Property"));
			ps.setString(9, asf.getXMLData(XML, "AuditTimeBegin"));
			ps.setString(10, asf.getXMLData(XML, "AuditTimeEnd"));
			ps.setString(11, asf.getXMLData(XML, "ProjectEnd"));

			ps.setString(12, asf.getXMLData(XML, "standbyname"));
			ps.setString(13,  asf.getXMLData(XML, "TemplateType")) ;
			
			ps.setString(14,  asf.getXMLData(XML, "DepartmentId")) ;	//部门
			
			ps.execute();

			/**
			 * py 重建ManuacCount表
			 */
//			RectifyService vm =new RectifyService(conn);
//			String acc = asf.getXMLData(XML, "AccPackageID");
//			vm.createTzhz(acc,String.valueOf(projectid));
//			vm.createWbTzhz(acc, String.valueOf(projectid));
//			vm.createAssitem(acc,String.valueOf(projectid));

//			new ManuacCountService(conn).insertManuacCount(String.valueOf(projectid));

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
		} finally {
			DbUtil.close(ps);
		}
	}

	public void editPeople(String auditPeople, int projectID) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		int projectid = projectID;
		String[] ap = auditPeople.split(";");

		try {

			String str;

			str = "delete from z_auditpeople where projectid=?  and departmentid=0 ";
			ps = conn.prepareStatement(str);
			ps.setInt(1, projectid);
			ps.execute();

			for (int i = 0; i < ap.length; i++) {
				String[] aps = ap[i].split(",");
				str = "insert into z_auditpeople (projectid,userid,role,isAudit,istarandpro) values(?,?,?,?,?)";
				ps = conn.prepareStatement(str);
				ps.setInt(1, projectid);
				ps.setString(2, aps[0]);
				ps.setString(3, aps[1]);
				ps.setString(4, aps[2]);
				ps.setString(5, aps[3]);
				ps.execute();

				ps.close();
				/**
				 * 增加用户临时权限
				 */
				String sql = "select * from k_accright a,z_project b where  b.projectid='"+projectid+"' and a.DepartID= b.CustomerId ";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					sql = "select * from k_accright a,z_project b where a.userid = '"+aps[0]+"' and b.projectid='"+projectid+"' and a.DepartID= b.CustomerId";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					if(!rs.next()){
						sql = "select * from k_accright a,z_project b,k_user c where c.id = '"+aps[0]+"' and a.userid = concat('[',c.departmentid,']') and b.projectid='"+projectid+"' and a.DepartID= b.CustomerId";
						ps = conn.prepareStatement(sql);
						rs = ps.executeQuery();
						if(!rs.next()){
							java.text.SimpleDateFormat dateformat = new java.text.SimpleDateFormat("yyyy-MM-dd");
							Calendar obj = Calendar.getInstance();
							obj.add(Calendar.MONTH, 1);
							String sDate = dateformat.format(obj.getTime());

							sql = "insert into k_accright (DepartID,userid,Property) select CustomerId,'"+aps[0]+"','"+sDate+"' from z_project where projectid='"+projectid+"' ";
							ps = conn.prepareStatement(sql);
							ps.execute();
						}
					}
				}



			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	/*项目分工。增加部门
	 * 返回值：出错信息 "" 正常
	 */
	public String editDepartment(String auditDepartment, int projectID)
			throws Exception {

		if (auditDepartment == null || auditDepartment.trim().length() <= 0) {
			return "";

		}

		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		Statement st = null;
		ResultSet rs = null;
		String errMsg = "本操作失败";
		int projectid = projectID;
		String[] ap = auditDepartment.split(";");

		try {

			st = conn.createStatement();

			String str;
			String sql = "";
			//	==========第一步验证，本地网络地址是否存在
			String url = "";
			String localDepartName = "";
			String localDepartNameURI = "";
			sql = "select stockowner,departName from k_customer where departid=555555";
			rs = st.executeQuery(sql);
			if (rs.next()) {
				url = rs.getString("stockowner");
				localDepartName = rs.getString("departName");
				localDepartNameURI = java.net.URLEncoder.encode(
						localDepartName, "GBK");
			} else {
				return errMsg + "\n 找不到当前机构的记录！";
			}

			if (url == null || url.trim().length() <= 0) {
				return errMsg + "\n原因:\n 当前机构的网络地址还没填写！\n 请到［机构信息维护］填写。";
			}

			//	==========第二步验证，外地网络地址是否ping通
			com.matech.framework.pub.net.Web web = new com.matech.framework.pub.net.Web();
			for (int i = 0; i < ap.length; i++) {
				String[] aps = ap[i].split(",");

				str = "select url,departname from k_department where autoid="
						+ aps[0];
				rs = st.executeQuery(str);
				if (rs.next()) {
					if (!web.canAccess(web.getEstOuterUrl(rs.getString("url"))
							+ "../")) {
						return errMsg + "\n原因:\n 单位［"
								+ rs.getString("departname") + "］的\n网络地址［"
								+ rs.getString("url") + "］连接不通！";
					}
				} else {
					return errMsg + "\n原因:\n 找不到部门，编号为［" + aps[0] + "］！";
				}

			}

			//查找出本项目名称。
			String projectName = "";
			String projectNameURI = "";
			sql = "select projectname from z_project where projectid="
					+ projectid;
			rs = st.executeQuery(sql);
			if (rs.next()) {
				projectName = rs.getString("projectname");
				projectNameURI = java.net.URLEncoder.encode(projectName, "GBK");
			} else {
				return errMsg + "\n原因:\n 找不到项目，编号为［" + projectid + "］！";
			}

			String returnHTML = "";
			for (int i = 0; i < ap.length; i++) {
				String[] aps = ap[i].split(",");

				//向本地数据库插数据
				str = "select 1 from z_auditpeople where projectid="
						+ projectid + " and departmentid=" + aps[0];
				rs = st.executeQuery(str);
				if (!rs.next()) {

					//访问异地
					sql = "select url from k_department where autoid=" + aps[0];
					rs = st.executeQuery(sql);
					if (rs.next()) {
						returnHTML = web.getUrlHtml(web.getEstOuterUrl(rs
								.getString("url"))
								+ "OuterProject/AddOuterProject.jsp?projectid="
								+ projectid
								+ "&projectname="
								+ projectNameURI
								+ "&departname="
								+ localDepartNameURI
								+ "&outerdepartid=" + aps[0] + "&url=" + url);
						if (returnHTML.indexOf("suc") < 0) {
							return errMsg + "\n原因:\n 访问不到部门编号［" + aps[0]
									+ "］的网络地址！或者对方服务器接受失败！";
						}
					} else {
						return errMsg + "\n原因:\n 找不到部门编号［" + aps[0] + "］的网络地址！";
					}

					//本地访问
					str = " insert into z_auditpeople (userid,projectid,departmentid,role,isAudit, istarandpro) values(0,?,?,?,?,?) \n";
					ps = conn.prepareStatement(str);
					ps.setInt(1, projectid);
					ps.setString(2, aps[0]);
					ps.setString(3, aps[1]);
					ps.setString(4, aps[2]);
					ps.setString(5, aps[3]);
					ps.execute();
				}

			}
			return "";
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			return errMsg;
		} finally {
			DbUtil.close(ps);
		}

	}

	//		用于初始化修改人员分工页面。
	public String[][] getAuditDepartment(int projectID) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;

		String[][] result;
		try {

			String str = "select a.departmentid,concat('外部参与人(',b.departname,')'),a.role, "
				       + " isaudit,case isaudit when 1 then 'checked' when 0 THEN '' end as isaudit, "
				       + " istarandpro,case istarandpro when 1 then 'checked' when 0 THEN '' end as istarandpro "
				       + " from z_auditpeople a,k_department b "
				       + " where a.projectid = ? and a.departmentid=b.autoid";
			ps = conn.prepareStatement(str);
			ps.setInt(1, projectID);
			rs = ps.executeQuery();
			rs.last();

			result = new String[rs.getRow()][5];
			rs.beforeFirst();

			for (int i = 0; rs.next(); i++) {
				for (int j = 0; j < 5; j++) {
					result[i][j] = rs.getString(j + 1);
				}
			}
			return result;
		} finally {
			DbUtil.close(ps);
		}

	}

	public String[][] getAuditPeople(int projectID) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;

		String[][] result;
		try {

			String str = "select a.userid,b.name,a.role, isaudit, "
					   + " case isaudit when 1 then 'checked' when 0 THEN '' end as isaudit, "
					   + " case istarandpro when 1 then 'checked' when 0 then '' end as istarandpro "
					   + " from z_auditpeople a,k_user b "
					   + " where a.projectid = ? and a.userid=b.id and a.departmentid=0";
			ps = conn.prepareStatement(str);
			ps.setInt(1, projectID);
			rs = ps.executeQuery();
			rs.last();

			result = new String[rs.getRow()][6];
			rs.beforeFirst();

			for (int i = 0; rs.next(); i++) {
				for (int j = 0; j < 6; j++) {
					result[i][j] = rs.getString(j + 1);
				}
			}
			return result;
		} finally {
			DbUtil.close(ps);
		}

	}

	// 获取立项日期
	public String getProjectCreated(String projectid) {

		Statement stmt = null;
		ResultSet rs = null;
		// String strsql = "";
		try {
			DbUtil.checkConn(conn);
			stmt = conn.createStatement();
			rs = stmt
					.executeQuery("Select ProjectCreated From z_Project Where ProjectID = "
							+ projectid);
			if (rs.next()) {
				return rs.getString(1);
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(stmt);
		}
		return "";
	}

	// 2007.4.21增加的方法
	public HashMap getStatByProjectID(String projectid, Connection conn) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		String cid = null;

		HashMap hm = null;
		if (conn == null) {
			return hm;
		}
		try {
			ps = conn
					.prepareStatement("select customerid from z_project where projectid="
							+ projectid);
			rs = ps.executeQuery();
			rs.next();
			cid = rs.getString("customerid");

			StringBuffer sb = new StringBuffer("");

			sb.append("select projectname,");
			//总底稿数
			sb.append("(select count(*) from asdb_" + cid + ".z_task "
					+ "where projectid=" + projectid + " and isleaf=1 "
					+ "and (property not like '%A%' or property is null)) as total,");
			//编制数
			sb.append("(select count(*) from asdb_" + cid + ".z_task "
					+ "where projectid=" + projectid + " and isleaf=1 "
					+ "and user1 is not null and user1 >'') as usr1cnt,");
			//一级复核数
			sb.append("(select count(*) from asdb_" + cid + ".z_task "
					+ "where projectid=" + projectid + " and isleaf=1 "
					+ "and user5 is not null and user4 >'') as usr5cnt,");
			//二级复核数
			sb.append("(select count(*) from asdb_" + cid + ".z_task "
					+ "where projectid=" + projectid + " and isleaf=1 "
					+ "and user2 is not null and user2 >'') as usr2cnt,");
			//三级复核数
			sb.append("(select count(*) from asdb_" + cid + ".z_task "
					+ "where projectid=" + projectid + " and isleaf=1 "
					+ "and user3 is not null and user2 >'') as usr3cnt,");
			//参与人数
			sb.append("(select count(*) from asdb.z_auditpeople "
					+ "where projectid=" + projectid + ") as joincnt,");
			//工时
			sb
					.append("ifnull(to_days(realenddate)-to_days(realstartdate),0) as manhaur ");
			sb.append("from asdb.z_project ");
			sb.append("where projectid=" + projectid);

			ps = conn.prepareStatement(sb.toString());

			rs = ps.executeQuery();

			//Debug.print(sb.toString());

			rs.next();
			hm = new HashMap();
			hm.put("projectname", rs.getString("projectname"));
			hm.put("total", rs.getString("total"));
			hm.put("usr1cnt", rs.getString("usr1cnt"));
			hm.put("usr5cnt", rs.getString("usr5cnt"));
			hm.put("usr2cnt", rs.getString("usr2cnt"));
			hm.put("usr3cnt", rs.getString("usr3cnt"));
			hm.put("joincnt", rs.getString("joincnt"));
			hm.put("manhaut", rs.getString("manhaur"));

			return hm;

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return null;
	}

	public Integer getProjectCnt(String userid, String projectidList,
			Connection conn) { 

		PreparedStatement ps = null;
		ResultSet rs = null;

		int cnt = 0;

		try {

			StringBuffer sb = new StringBuffer("");

			sb.append("select count(*) from asdb.z_auditpeople where userid = "
					+ userid + " and projectid in(" + projectidList + ") ");

			ps = conn.prepareStatement(sb.toString());

			rs = ps.executeQuery();

			rs.next();

			cnt = rs.getInt(1);

			return new Integer(cnt);

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			return new Integer(0);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public String[] getCustomerIds(String userid, String projectidList,
			Connection conn) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		String[] customerIds = null;

		try {

			StringBuffer sb = new StringBuffer("");

			sb
					.append("select count(*) from (select distinct customerid from asdb.z_auditpeople a, asdb.z_project b where a.userid="
							+ userid + " ");
			sb.append("and b.projectid in (" + projectidList + ") ");
			sb.append("and b.projectid in (" + projectidList + ") ");
			sb.append("and a.projectid=b.projectid) as t ");
			ps = conn.prepareStatement(sb.toString());

			rs = ps.executeQuery();
			rs.next();
			customerIds = new String[rs.getInt(1)];

			sb = new StringBuffer("");

			sb
					.append("select distinct customerid from asdb.z_auditpeople a, asdb.z_project b where a.userid="
							+ userid + " ");
			sb.append("and b.projectid in (" + projectidList + ") ");
			sb.append("and b.projectid in (" + projectidList + ") ");
			sb.append("and a.projectid=b.projectid ");

			ps = conn.prepareStatement(sb.toString());

			rs = ps.executeQuery();

			int i = 0;
			while (rs.next()) {
				customerIds[i++] = new String(rs.getString("customerid"));
			}

			return customerIds;

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			return customerIds;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public Integer getUsrCnt(String userid, String[] customerIds,
			String projectidList, String usr, Connection conn) {

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			StringBuffer sb = new StringBuffer("");
			sb.append("select 0");

			for (int i = 0; i < customerIds.length; i++) {

				sb.append("+(select count(*) from asdb_" + customerIds[i]
						+ ".z_task a,asdb.k_user b ");
				sb.append("where a.projectid in (" + projectidList + ") ");
				sb.append("and id=" + userid + " ");
					sb.append("and " + usr + " = " + userid + ") ");
			}

			sb.append(" as usrcnt");

		//	System.out.println("KKKKK:"+sb);

			ps = conn.prepareStatement(sb.toString());

			rs = ps.executeQuery();
			rs.next();

			return new Integer(rs.getInt(1));

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			return new Integer(0);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public String[] getAuditPeopleIDs(String projectidList, Connection conn) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		String[] uids = null;

		try {

			StringBuffer sb = new StringBuffer("");
			sb
					.append("select count(*) from (select distinct userid from asdb.z_auditpeople where projectid in ("
							+ projectidList + ")) as t");

			ps = conn.prepareStatement(sb.toString());

			rs = ps.executeQuery();
			rs.next();
			uids = new String[rs.getInt(1)];

			sb = new StringBuffer("");

			sb
					.append("select distinct userid from asdb.z_auditpeople where projectid in ("
							+ projectidList + ") order by userid");

			ps = conn.prepareStatement(sb.toString());

			rs = ps.executeQuery();

			int i = 0;

			while (rs.next()) {
				uids[i++] = new String(rs.getString("userid"));
			}
			return uids;

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			return null;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public HashMap getStatByUID(String userid, String projectidList,
			Connection conn) {
		HashMap hm = null;
		if (conn == null) {
			return hm;
		}
		hm = new HashMap();

		hm.put("name", getAuditPeopleName(userid, conn));
		hm.put("joincnt", getProjectCnt(userid, projectidList, conn));

		String[] customerIds = getCustomerIds(userid, projectidList, conn);

		hm.put("user0cnt", getUsrCnt(userid, customerIds, projectidList, "user0",
				conn));
		hm.put("user1cnt", getUsrCnt(userid, customerIds, projectidList, "user1",
				conn));
		hm.put("user5cnt", getUsrCnt(userid, customerIds, projectidList, "user5",
				conn));
		hm.put("user2cnt", getUsrCnt(userid, customerIds, projectidList, "user2",
				conn));
		hm.put("user3cnt", getUsrCnt(userid, customerIds, projectidList, "user3",
				conn));

		return hm;
	}

	public String getAuditPeopleName(String userid, Connection conn) {

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			StringBuffer sb = new StringBuffer("");

			sb.append("select `name` from asdb.k_user where id=?" );

			ps = conn.prepareStatement(sb.toString());
			ps.setString(1, userid);

			rs = ps.executeQuery();

			if(rs.next()){
				return rs.getString(1);
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return "";
	}

	// 2006.9.22增加的方法
	public String getStat(String colName, String projectid) {

		StringBuffer bs = new StringBuffer();
		bs.append(" SELECT \n ");
		bs
				.append(" 24*MAX(TO_DAYS(date1)-to_days(ProjectCreated)) as max1,\n 24*MAX(TO_DAYS(date5)-to_days(ProjectCreated)) as max5, 24*MAX(TO_DAYS(date2)-to_days(ProjectCreated)) as max2, \n 24*MAX(TO_DAYS(date3)-to_days(ProjectCreated)) as max3, \n");
		bs
				.append(" 24*Min(TO_DAYS(date1)-to_days(ProjectCreated)) as min1,\n 24*Min(TO_DAYS(date5)-to_days(ProjectCreated)) as min5, 24*Min(TO_DAYS(date2)-to_days(ProjectCreated)) as min2, \n 24*Min(TO_DAYS(date3)-to_days(ProjectCreated)) as min3, \n");
		bs
				.append(" 24*format(avg(TO_DAYS(date1)-to_days(ProjectCreated)),2) as avg1,\n 24*format(avg(TO_DAYS(date5)-to_days(ProjectCreated)),2) as avg5, 24*format(avg(TO_DAYS(date2)-to_days(ProjectCreated)),2) as avg2, \n 24*format(avg(TO_DAYS(date3)-to_days(ProjectCreated)),2) as avg3  \n");
		bs.append(" from \n");
		bs.append(" ( \n");
		bs.append(" select projectid,date1,date5,date2,date3 \n");
		bs.append(" from z_task where projectid=? \n");
		bs.append(" )a left join  ");
		bs.append(" ( ");
		bs.append(" select projectid,projectcreated from ");
		bs.append(" z_project where projectid=? \n");
		bs.append(" )b \n");
		bs.append(" on a.projectid=b.projectid ");

		System.out.println("wsh:"+bs.toString());

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			DbUtil.checkConn(conn);
			new DBConnect().changeDataBaseByProjectid(conn,projectid);
			
			ps = conn.prepareStatement(bs.toString());
			ps.setString(1, projectid);
			ps.setString(2, projectid);
			rs = ps.executeQuery();

			if (rs.next()) {
				String temp = rs.getString(colName);
				if(temp==null||temp.equals("")){
					return "小时";
				}else{
					int tt = ((int)Double.parseDouble(temp)+1)*24;
					return new Integer(tt).toString()+"小时";
				}
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return "";
	}

	public String getStatIdio(String unumber, String uname, String projectid) {
		StringBuffer sb = new StringBuffer();
		//if (unumber.equals("0")) {
		//	sb.append(" select COUNT(a.taskid) from z_task a,k_user b \n");
		//	sb.append(" where projectid=? \n");
		//	sb.append(" and a.user" + unumber +"=b.id and b.id=? \n");
		//} else {
			sb.append(" select COUNT(taskid) from z_task \n");
			sb.append(" where projectid = ? AND user" + unumber + "=? \n");
		//}

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			DbUtil.checkConn(conn);
			ps = conn.prepareStatement(sb.toString());
			ps.setString(1, projectid);
			ps.setString(2, uname);
			rs = ps.executeQuery();

			if (rs.next()) {
				String temp = rs.getString(1);
				return temp == null ? "" : temp;
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return "";
	}

	public Map getNameRole(String pid) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			DbUtil.checkConn(conn);

			String sql = "select id,role from k_user a,z_auditpeople b where a.id=b.userid and b.projectid='"
					+ pid + "' order by name";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			Map map = new HashMap();
			while (rs.next()) {
				map.put(rs.getString(1), rs.getString(2));
			}
			return map;

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			return null;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 获得一个随机生成的9位数字
	 *
	 * @return
	 */
	public String getRandom() {
		java.text.DecimalFormat df = new DecimalFormat("####");
		String i = df.format(Math.random() * 1000000000);
		return i;
	}

	/**
	 * 获得打印另存为的底稿或者函证底稿
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	private List getOtherTaskList(String projectId) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {

			new DBConnect().changeDataBaseByProjectid(conn, String
					.valueOf(projectId));

			String sql = "select taskid from z_task where projectid=? and property='print'";
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectId);
			rs = ps.executeQuery();

			while (rs.next()) {
				list.add(rs.getString(1));
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return list;
	}

	public void editHQDepartment(int projectid) {
		PreparedStatement ps = null;
		Statement st = null;
		ResultSet rs = null;

		try {

			String HQ = com.matech.framework.pub.sys.UTILSysProperty.SysProperty
					.getProperty("HQ");

			if (HQ == null || "".equals(HQ))
				return;
			st = conn.createStatement();

			String str;
			String sql = "";
			//	==========第一步验证，本地网络地址是否存在
			String url = "";
			String localDepartName = "";
			String localDepartNameURI = "";
			sql = "select stockowner,departName from k_customer where departid=555555";
			rs = st.executeQuery(sql);
			if (rs.next()) {
				url = rs.getString("stockowner");
				localDepartName = rs.getString("departName");
				localDepartNameURI = java.net.URLEncoder.encode(
						localDepartName, "GBK");
			}

			if (url == null || url.trim().length() <= 0) {
				throw new Exception("\n原因:\n 当前机构的网络地址还没填写！\n 请到［机构信息维护］填写。");
			}

			//	==========第二步验证，外地网络地址是否ping通
			NetService web = new NetService();

			//查找出本项目名称。
			String projectName = "";
			String projectNameURI = "";
			sql = "select projectname from z_project where projectid="
					+ projectid;
			rs = st.executeQuery(sql);
			if (rs.next()) {
				projectName = rs.getString("projectname");
				projectNameURI = java.net.URLEncoder.encode(projectName, "GBK");
			} else {
				throw new Exception("\n原因:\n 找不到项目，编号为［" + projectid + "］！");
			}

			//向本地数据库插数据
			str = "select 1 from z_auditpeople where projectid=" + projectid
					+ " and departmentid=" + HQ;
			rs = st.executeQuery(str);
			if (!rs.next()) {

				//访问异地
				sql = "select url from k_department where autoid=" + HQ;
				rs = st.executeQuery(sql);
				if (rs.next()) {
					web.getUrlHtml(web.getEstOuterUrl(rs.getString("url"))
							+ "OuterProject/AddOuterProject.jsp?projectid="
							+ projectid + "&projectname=" + projectNameURI
							+ "&departname=" + localDepartNameURI
							+ "&outerdepartid=" + HQ + "&url=" + url);

				}

				//本地访问
				str = " insert into z_auditpeople (userid,projectid,departmentid,role,isAudit) values(0,?,?,?,?) \n";
				ps = conn.prepareStatement(str);
				ps.setInt(1, projectid);
				ps.setString(2, HQ);
				ps.setString(3, "外部监控人员");
				ps.setString(4, "0");
				ps.execute();
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (Exception e) {

			}
		}

	}

	/**
	 * 验证项目是否参加了合并报表
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public String removeValidation(String projectId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			String strSql = "select projectid from z_projectentry where relationprojectid= ?";
			ps = conn.prepareStatement(strSql);

			ps.setString(1, projectId);
			rs = ps.executeQuery();

			if(rs.next()){
				return "<projectid>"+  rs.getString("1") + "</projectid>";
			}else{
				return "suc";
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
     * 根据项目编号，获取项目的起始年月和结束年月信息，放到4个字符串的数组中返回
     * result[0]:起始年
     * result[1]:起始月
     * result[1]:结束年
     * result[1]:结束月
     * @param conn Connection
     * @param projectid String
     * @return String[]
     * @throws Exception
     */
    public int[] getProjectAuditAreaByProjectid(String projectid) throws
            Exception {
        int[] result = {0, 0, 0, 0};
        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();
            String sql = "select audittimebegin,audittimeend from asdb.z_project where projectid=" + projectid;
            rs = st.executeQuery(sql);
            if (rs.next()) {
                String strStart = rs.getString(1);
                String strEnd = rs.getString(2);

                if (strStart != null && strStart.length() == 10) {
                    result[0] = Integer.parseInt(strStart.substring(0, 4));
                    result[1] = Integer.parseInt(strStart.substring(5, 7));
                }

                if (strEnd != null && strEnd.length() == 10) {
                    result[2] = Integer.parseInt(strEnd.substring(0, 4));
                    result[3] = Integer.parseInt(strEnd.substring(5, 7));
                }
            }
        } finally {
            if (rs != null)
                rs.close();
            if (st != null)
                st.close();
        }
        return result;
    }

    /**
     * 获得项目进度
     * @param projectid
     * @return
     */
    public String getProjectState(String projectid){

    	String projectstate = "";

    	PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			String strSql = "select state from z_project where projectid= ?";
			ps = conn.prepareStatement(strSql);

			ps.setString(1, projectid);
			rs = ps.executeQuery();

			if(rs.next()){
				int state = rs.getInt(("state"));

				switch(state){
					case STATE_NORMAL:
						projectstate = "该项目正在进行中...";
						break;
					case STATE_END:
						projectstate = "该项目已归档...";
						break;
					case STATE_REMOVE:
						projectstate = "该项目已删除...";
						break;
					case STATE_WAITING_AUDITING:
						projectstate = "该项目尚未审核...";
						break;

					default:
						projectstate = "状态错误...";
						break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return projectstate;
    }

    /**
     * 获得我的底稿
     * @param projectid
     * @return
     */
    public List getMyTask(String projectid,String userid){

    	List finishedTask = new ArrayList();

    	PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			String strSql = "select taskcode,taskname "
						  + " from z_task "
						  +	" where projectid= ? "
						  + " and (user0=? or user0 is null or user0 ='' ) "
						  + " and (property not like '%A%' or property is null) "
						  + " and isleaf=1 "
						  + " order by orderid ";
			ps = conn.prepareStatement(strSql);

			ps.setString(1, projectid);
			ps.setString(2, userid);
			rs = ps.executeQuery();

			while(rs.next()){
				Task taskobj = new Task();

				taskobj.setTaskCode(rs.getString("taskcode"));
				taskobj.setTaskName(rs.getString("taskname"));

				finishedTask.add(taskobj);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return finishedTask;
    }

    /**
     * 获得已编制底稿
     * @param projectid
     * @return
     */
    public List getFinishedTask(String projectid){

    	List finishedTask = new ArrayList();

    	PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			String strSql = "select taskcode,taskname from z_task where projectid= ? and user1 is not null and user1!=''";
			ps = conn.prepareStatement(strSql);

			ps.setString(1, projectid);
			rs = ps.executeQuery();

			while(rs.next()){
				Task taskobj = new Task();

				taskobj.setTaskCode(rs.getString("taskcode"));
				taskobj.setTaskName(rs.getString("taskname"));

				finishedTask.add(taskobj);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return finishedTask;
    }

    /**
     * 获得一级已复核底稿
     * @param projectid
     * @return
     */
    public List getFirstCheckedTask(String projectid){

    	List finishedTask = new ArrayList();

    	PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			String strSql = "select taskcode,taskname from z_task where projectid= ? \n"
				          +" and user5 is not null and user5!='' \n"
					      +" and (user2 is null or user2='')";
			ps = conn.prepareStatement(strSql);

			ps.setString(1, projectid);
			rs = ps.executeQuery();

			while(rs.next()){
				Task taskobj = new Task();

				taskobj.setTaskCode(rs.getString("taskcode"));
				taskobj.setTaskName(rs.getString("taskname"));

				finishedTask.add(taskobj);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return finishedTask;
    }

    /**
     * 获得二级已复核底稿
     * @param projectid
     * @return
     */
    public List getSecondCheckedTask(String projectid){

    	List finishedTask = new ArrayList();

    	PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			String strSql = "select taskcode,taskname from z_task where projectid= ? \n"
					      +" and user2 is not null and user2!='' \n"
					      +" and (user3 is null or user3='')";
			ps = conn.prepareStatement(strSql);

			ps.setString(1, projectid);
			rs = ps.executeQuery();

			while(rs.next()){
				Task taskobj = new Task();

				taskobj.setTaskCode(rs.getString("taskcode"));
				taskobj.setTaskName(rs.getString("taskname"));

				finishedTask.add(taskobj);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return finishedTask;
    }

    /**
     * 获得三级已复核底稿
     * @param projectid
     * @return
     */
    public List getThirdCheckedTask(String projectid){

    	List finishedTask = new ArrayList();

    	PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			String strSql = "select taskcode,taskname from z_task where projectid= ? and user3 is not null and user3!=''";
			ps = conn.prepareStatement(strSql);

			ps.setString(1, projectid);
			rs = ps.executeQuery();

			while(rs.next()){
				Task taskobj = new Task();

				taskobj.setTaskCode(rs.getString("taskcode"));
				taskobj.setTaskName(rs.getString("taskname"));

				finishedTask.add(taskobj);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return finishedTask;
    }

    /**
     * 获得已退回底稿
     * @param projectid
     * @return
     */
    public List getBackedTask(String projectid){

    	List finishedTask = new ArrayList();

    	PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			String strSql = "select taskcode,taskname from z_task where projectid= ? and user4 is not null and user4!=''";
			ps = conn.prepareStatement(strSql);

			ps.setString(1, projectid);
			rs = ps.executeQuery();

			while(rs.next()){
				Task taskobj = new Task();

				taskobj.setTaskCode(rs.getString("taskcode"));
				taskobj.setTaskName(rs.getString("taskname"));

				finishedTask.add(taskobj);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return finishedTask;
    }

    /**
     * 获得到期(必做)未做底稿
     * @param projectid
     * @return
     */
    public List getNotFinishedTask(String projectid){

    	List notfinishedTask = new ArrayList();

    	PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			String strSql = " select distinct taskcode, TaskName From z_Task t,z_project p \n"
						  +" Where t.ProjectId=? \n"
				          +" and t.ismust like '%1%' \n"
				          +" and (t.user1 is null or t.user1 ='') \n"
				          +" and p.projectend < now()";
			ps = conn.prepareStatement(strSql);

			ps.setString(1, projectid);
			rs = ps.executeQuery();

			while(rs.next()){
				Task taskobj = new Task();

				taskobj.setTaskCode(rs.getString("taskcode"));
				taskobj.setTaskName(rs.getString("taskname"));

				notfinishedTask.add(taskobj);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return notfinishedTask;
    }

    /**
     * 获得分工给我的审计程序
     * @param projectid
     * @return
     */
    public List getMyProcedure(String projectid,String username){

    	List myProcedure = new ArrayList();

    	PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			String strSql = "select auditprocedure from z_procedure where projectid=? and concat(',',executor,',') like'%,"+username+",%'";
			ps = conn.prepareStatement(strSql);

			ps.setString(1, projectid);
			rs = ps.executeQuery();

			while(rs.next()){
				myProcedure.add(rs.getString("auditprocedure"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return myProcedure;
    }

    /**
     * 获得分工给我的审计目标
     * @param projectid
     * @return
     */
    public List getMyTarget(String projectid,String username){

    	List myTarget = new ArrayList();

    	PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			String curauditTypeProperty = new AuditTypeTemplateService(conn).getPropertyByProjectId(projectid);

			if("1".equals(curauditTypeProperty)){
				String strSql = "select group_concat(distinct concat('\\'',taskid,'\\''))as taskid from z_procedure "
					          +"where projectid=? and concat(',',executor,',') like'%,"+username+",%'";
				ps = conn.prepareStatement(strSql);

				ps.setString(1, projectid);
				rs = ps.executeQuery();
				String taskid = "";
				while(rs.next()){
					taskid = rs.getString(1);
				}
				rs.close();

				strSql="select audittarget from z_target where projectid=? and concat('~',taskid,'~') in("+taskid+")";
				ps = conn.prepareStatement(strSql);

				ps.setString(1, projectid);
				rs = ps.executeQuery();
				while(rs.next()){
					myTarget.add(rs.getString("audittarget"));
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return myTarget;
    }

    /**
     * 获得由我汇报的重大事项
     * @param projectid
     * @return
     */
    public List getMyReport(String projectid,String userid){

    	List myReport = new ArrayList();

    	PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			String strSql = "select caption,createtime from z_affairreport where projectid=? and author=? and pid=0 and executer=''";
			ps = conn.prepareStatement(strSql);

			ps.setString(1, projectid);
			ps.setString(2,userid);
			rs = ps.executeQuery();

			while(rs.next()){
				AffairReportTable report = new AffairReportTable();
				String createtime = rs.getString("createtime");

				report.setCaption(rs.getString("caption"));
				report.setCreateTime(createtime.substring(0,createtime.length()-2));

				myReport.add(report);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return myReport;
    }

    /**
     * 获得由我回复的重大事项
     * @param projectid
     * @return
     */
    public List getMyRevert(String projectid,String userid){

    	List myRevert = new ArrayList();

    	PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			String strSql = "select caption,createtime from z_affairreport where projectid=? and author=? and pid!=0 and (executer='' or executer is null)";
			ps = conn.prepareStatement(strSql);

			ps.setString(1, projectid);
			ps.setString(2,userid);
			rs = ps.executeQuery();

			while(rs.next()){
				AffairReportTable revert = new AffairReportTable();

				String[] caption = rs.getString("caption").split(">");

				String createtime = rs.getString("createtime");
				revert.setCaption(caption[1]);
				revert.setCreateTime(createtime.substring(0,createtime.length()-2));

				myRevert.add(revert);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return myRevert;
    }

	public void removeDef(String projectid){
		PreparedStatement ps = null;

		try{
			String sql = "delete from k_userdef where property="+projectid;

			ps = conn.prepareStatement(sql);
			ps.execute();

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}

	/**
	 * 设置项目状态
	 * @param projectId
	 * @param state
	 */
	public int getState(String projectId){
		PreparedStatement ps = null;
		ResultSet rs = null;

		try{
			String sql = "select state from z_project "
						+ " where projectId=? ";

			ps = conn.prepareStatement(sql);
			ps.setString(1, projectId);

			rs = ps.executeQuery();

			if(rs.next()) {
				return rs.getInt(1);
			}

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}

		return -1;
	}

	/**
	 * 设置项目状态
	 * @param projectId
	 * @param state
	 */
	public void setProjectState(String projectId, int state){
		PreparedStatement ps = null;

		try{
			String sql = "update z_project set state=? "
						+ " where projectId=? ";

			ps = conn.prepareStatement(sql);
			ps.setInt(1, state);
			ps.setString(2, projectId);

			ps.executeUpdate();

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}

	public String createHTNL(ArrayList keys,ArrayList values){
		PreparedStatement ps = null;

		String[] keyss = new String[keys.size()];
		String[] valuess = new String[values.size()];
		int tempA=0;
		int tempB=0;
		try{

			for (Iterator iter = keys.iterator(); iter.hasNext();) {


					keyss[tempA] = (String) iter.next();

					tempA++;
			}

			for (Iterator iter = values.iterator(); iter.hasNext();) {


					valuess[tempB] = (String) iter.next();

					tempB++;

			}

			StringBuffer html = new StringBuffer();

			for(int i = 0;i < keyss.length;i++){

				html.append("<tr>");
				html.append("<td>");
				html.append("<div align=\"right\"> "+keyss[i]+"：&nbsp;&nbsp;</div>");
				html.append("</td>");
				html.append("<td colspan=\"2\">");

				if(i<valuess.length){
					html.append("<input type=\"text\" id=\"s"+i+"\" name=\"s"+i+"\" value = \""+valuess[i]+"\"> ");
				}else{
					html.append("<input type=\"text\" id=\"s"+i+"\" name=\"s"+i+"\" value = \"\"> ");
				}
				html.append("</td>");
				html.append("</tr>");

			}

			return html.toString();

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}

		return "";
	}



	public List getProjectIdsByCustomerId(String customerId) {

		PreparedStatement ps = null;
		ResultSet rs = null;
		List customerIds = new ArrayList();

		try {

			String sql = "select ProjectID from z_project where CustomerId=?" ;

			ps = conn.prepareStatement(sql);
			ps.setString(1, customerId);

			rs = ps.executeQuery() ;

			while(rs.next()) {

				customerIds.add(rs.getString("ProjectID"));
			}


		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			return customerIds;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return customerIds ;
	}

	/**
	 * 检查调整汇总表有没有值,没有的话登陆项目的时候重新生成调整汇总表
	 * @param projectId
	 * @param accpackageId
	 * @throws Exception
	 */
	public boolean checkRectify(String projectId, String accpackageId) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			sql = "select 1 from z_accountrectify where ProjectID = '" + projectId + "'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()) {
				return true;
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return false;
	}
	
	/**
	 * 获取项目区间的帐套编号
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public String getAccpackageIds(String projectId) throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			String sql = "select ifnull(group_concat(b.accpackageid order by b.accpackageid),'') "
					+ " from asdb.`z_project` a,c_accpackage b "
					+ " where projectid = ? "
					+ " and a.customerid = b.customerid "
					+ " and b.accpackageyear >= substring(a.audittimebegin,1,4) "
					+ " and substring(b.accpackageid,7,4) <= substring(a.audittimeend,1,4) ";

			ps = conn.prepareStatement(sql);
			ps.setString(1, projectId);
			rs = ps.executeQuery();
			if(rs.next()) {
				return rs.getString(1);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return "";
	}
	
	/**
	 * 根据被审单位和审计区间检查编号是否存在
	 * @param customerId
	 * @param auditTimeBegin
	 * @param auditTimeEnd
	 * @return
	 * @throws Exception
	 */
	public String checkProject(String customerId, String auditTimeBegin, String auditTimeEnd) throws Exception {
		String result = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select projectId from z_project where customerId = ? and auditTimeBegin = ? and auditTimeEnd = ?";
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, customerId);
			ps.setString(i++, auditTimeBegin);
			ps.setString(i++, auditTimeEnd);
			rs = ps.executeQuery();
			if(rs.next()) {
				result = rs.getString(1);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 加入项目收费
	 * @param projectId
	 * @param pvalue
	 * @param price
	 * @throws Exception
	 */
	public void saveProjectext(String projectId,String pvalue,String price) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			if(!"项目收费".equals(pvalue.trim())) return;
				
			String sql = "select * from z_projectext where projectId = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectId);
			rs = ps.executeQuery();
			if(rs.next()){
				sql = "update z_projectext set price = ? where projectId = ? ";
			}else{
				sql = "insert into z_projectext (price,projectId) values (?,?)";
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, price.trim());
			ps.setString(2, projectId);
			ps.execute();
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public String getJSONTree(String projectIds, String type) throws Exception {
		
		String result = null;
		if(type != null && type.trim().length() > 0) {
			if("GroupProject".equals(type)) {
				//集团项目类别
				result = getGroupNames(projectIds);
				
			} else if("noGroupProject".equals(type)) {
				//非集团项目类别
				//result = getCustomerYears(projectIds);
				result = getCustomers(projectIds, null);
			} else if(type.indexOf("customerYear_") > -1 ) {
				String year = type.split("_")[1];
				//result = getCustomers(projectIds, year);
				
			} else if(type.indexOf("customer_") > -1 ) {
				//客户
				String customerId = type.split("_")[1];
				//String year = type.split("_")[2];
				result = getProjectsByCustomerId(customerId, projectIds);
				
			} else if(type.indexOf("group_") > -1 ) {
				//集团,
				String groupId = type.split("_")[1];
				result = getCustomers(projectIds, groupId);
				//result = getProjectsByGroupName(groupProjectId, projectIds);
				//result = getChildProjectsByGroupName(groupName, projectIds);
				
			} else if(type.indexOf("groupProject_") > -1 ) {
				//集团项目
				String projectType = type.split("_")[1];
				String groupProjectId = type.split("_")[2];

				//result = getProjectsByProjectType(projectType, projectIds);
				result = "";
			} else if(type.indexOf("root") > -1 ) {
				//集团项目
				StringBuffer sb = new StringBuffer();
				//非集团项目
				sb.append(" [ ");
				
				sb.append(" { ")
					.append("cls:'folder',")
					.append("leaf:false,")
					.append("id:'noGroupProject',")
					.append("text:'非集团项目'} ");

				// 集团项目名称
				//   年度
				//     子项目
				//集团项目
				sb.append(" ,{ ")
					.append("cls:'folder',")
					.append("leaf:false,")
					.append("id:'GroupProject',")
					.append("text:'集团项目' ")
					.append("}]");
				
				result = sb.toString();
			} 
		}
		
		return result;
	}
	
	public String getCustomerYears(String projectIds) throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuffer sb = new StringBuffer();
		
		try {
			String sql = " select distinct substring(auditTimeend,1,4) "
				   + " from z_project "
				   + " where (projecttype is null or projecttype='') "
				   + " and projectId in (" + projectIds + ")"
				   + " order by substring(auditTimeend,1,4) desc ";
	
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			String year;
			
			sb.append("[");
			while(rs.next()) {
				year = rs.getString(1);
				
				sb.append(" {cls:'folder',")
					.append("leaf:false,")
					.append("id:'customerYear_").append(year).append("',")
					.append("text:'").append(year).append("'} ");
				if(!rs.isLast()) {
					sb.append(",");
				}
			}
			
			sb.append("]");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return sb.toString();
	
	}
	
	/**
	 * 
	 * @param projectIds
	 * @param groupId
	 * @return
	 * @throws Exception
	 */
	public String getCustomers(String projectIds, String groupId) throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuffer sb = new StringBuffer();
		
		try {
			String sql = " select distinct departid,departname "
				   + " from z_project a,k_customer b "
				   + " where a.projectId in (" + projectIds + ")"
				   + " and a.customerId=b.departid ";
				   
			if(groupId == null || "".equals(groupId)) {
				sql += " and (b.groupname is null or b.groupname='') ";
			} else {
				sql += " and b.groupname='" + groupId + "' ";
			}
				   	  
			sql += " order by departid asc ";
	
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			String customerId;
			String customerName;
			sb.append("[");
			while(rs.next()) {
				customerId = rs.getString(1);
				customerName = rs.getString(2);
				
				sb.append(" {cls:'folder',")
					.append("leaf:false,")
					.append("id:'customer_").append(customerId).append("',")
					.append("customerId:'").append(customerId).append("',")
					.append("customerName:'").append(customerName).append("',")
					.append("text:'").append(customerName).append("'} ");
				if(!rs.isLast()) {
					sb.append(",");
				}
			}
			
			sb.append("]");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return sb.toString();
	
	}
	
	public String getGroupNames(String projectIds) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuffer sb = new StringBuffer();
		
		try {
			String sql = " select distinct b.groupId,b.groupName "
						+ " from z_project a,k_group b,k_customer c "
						+ " where a.customerId=c.departId "
						+ " and b.groupId=c.groupName "
						+ " and projectId in (" + projectIds + ") ";
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			String groupId;
			String groupName;
			
			sb.append("[");
			
			while(rs.next()) {
				groupId = rs.getString(1);
				groupName = rs.getString(2);
				
				sb.append(" {cls:'folder',")
					.append("leaf:false,")
					.append("id:'group_").append(groupId).append("',")
					.append("groupName:'").append(groupId).append("',")
					.append("text:'").append(groupName).append("'} ");
				if(!rs.isLast()) {
					sb.append(",");
				}
			}
			
			sb.append("]");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return sb.toString();
	}
	
	/**
	 * 根据集团名称获得集团项目JSON数据
	 * @param groupName
	 * @return
	 * @throws Exception
	 */
	public String getProjectsByGroupName(String groupProjectId, String projectIds) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuffer sb = new StringBuffer();
		
		try {
			
			String sql = " select distinct groupProjectName,projectType "
					  + " from z_project "
					  + " where projectId in (" + projectIds + ")"
					  + " and groupProjectId=? "
					  + " order by groupProjectName desc ";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, groupProjectId);
			rs = ps.executeQuery();
			
			String groupProjectName;
			String projectType;

			sb.append("[");
			
			while(rs.next()) {
				groupProjectName = rs.getString(1);
				projectType = rs.getString(2);
				
				sb.append(" {cls:'folder',")
				.append("leaf:false,")
				.append("id:'groupProject_").append(projectType).append("_").append(groupProjectId).append("_").append("',")
				.append("groupProjectName:'").append(groupProjectName).append("',")
				.append("text:'").append(groupProjectName).append("'} ");
				if(!rs.isLast()) {
					sb.append(",");
				}
			}		
			
			sb.append("]");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return sb.toString();
	}
	
	/**
	 * 获得集团项目下的子项目JSON数据
	 * @param groupProjectId
	 * @return
	 * @throws Exception
	 */
	public String getProjectsByGroupProjectId(String groupProjectId, String projectIds) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuffer sb = new StringBuffer();
		
		try {
			
			String sql = " select projectId,projectName "
				   + " from z_project "
			       + " where groupProjectId=? "
			       + " and projectId in (" + projectIds + ")"
			       + " and projecttype > '' "
			       + " order by projectName ";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, groupProjectId);
			rs = ps.executeQuery();
			
			String projectId;
			String projectName;
			sb.append("[");
			
			while(rs.next()) {
				projectId = rs.getString(1);
				projectName = rs.getString(2);
				
				sb.append(" {cls:'file',")
				.append("leaf:true,")
				.append("id:'groupChildProject_").append(projectId).append("',")
				.append("projectId:'").append(projectId).append("',")
				.append("projectName:'").append(projectName).append("',")
				.append("projectType:'groupChildProject',")
				.append("text:'").append(projectName).append("'} ");
				if(!rs.isLast()) {
					sb.append(",");
				}
			}		
			
			sb.append("]");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return sb.toString();
	}
	
	
	/**
	 * 根据,号分隔的项目编号获得所有项目
	 * @param groupProjectId
	 * @return
	 * @throws Exception
	 */
	public List getProjectsByProjectIds(String projectIds) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuffer sb = new StringBuffer();
		List projectList = new ArrayList<Project>();
		try {
			
			String sql = " select * "
				   + " from z_project "
			       + " where projectId in (" + projectIds + ")"
			       + " order by projectName ";
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				Project project = new Project() ;
				project.setProjectId(rs.getString("ProjectID"));
				project.setAuditDept(rs.getString("AuditDept"));
				project.setCustomerId(rs.getString("CustomerId"));
				project.setAuditType(rs.getString("AuditType"));
				project.setAuditPara(rs.getString("AuditPara"));
				project.setProjectName(rs.getString("ProjectName"));
				project.setAccPackageId(rs.getString("AccPackageID"));
				project.setProjectCreated(rs.getString("ProjectCreated"));
				project.setState(rs.getString("State"));
				project.setAuditPeople(rs.getString("AuditPeople"));
				project.setProperty(rs.getString("Property"));
				project.setAuditTimeBegin(rs.getString("AuditTimeBegin"));
				project.setAuditTimeEnd(rs.getString("AuditTimeEnd"));
				project.setTemplateType(rs.getString("TemplateType")) ;
				project.setProjectEnd(rs.getString("projectEnd"));
				project.setCreateTime(rs.getString("createtime"));
				project.setCreateUser(rs.getString("createUser"));
				project.setGroupName(rs.getString("groupName"));
				project.setGroupProjectId(rs.getString("groupProjectId"));
				project.setGroupProjectName(rs.getString("groupProjectName"));
				project.setDepartmentId(rs.getString("departmentId"));
				project.setProjectType(rs.getString("projectType"));
				project.setShortName(rs.getString("shortName"));
				project.setPostil(rs.getString("postil"));
				
				projectList.add(project) ;
			}		
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return projectList;
	}
	
	

	public String getProjectsByProjectType(String projectType, String projectIds) throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuffer sb = new StringBuffer();
		
		try {
			
			
			String sql = " select b.parentId "
						+ " from z_project a,k_group b "
						+ " where projectType=? "
						+ " and projectId in (" + projectIds + ")"
						+ " and a.groupName=b.groupId "
						+ " having min(level) " 
						+ " order by b.fullpath ";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectType);
			rs = ps.executeQuery();
			
			String parentId = "0";
			if(rs.next()) {
				parentId = rs.getString(1);
				return "[" + getProjectsByProjectType(projectType, projectIds, parentId) + "]";
			}
			
			
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return "";
	}
	
	/**
	 * 获得集团项目下的子项目JSON数据
	 * @param groupProjectId
	 * @return
	 * @throws Exception
	 */
	public String getProjectsByProjectType(String projectType, String projectIds, String parentId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuffer sb = new StringBuffer();
		
		try {
			
			String sql = " select projectId,projectName,b.groupId "
					   + " from z_project a,k_group b "
					   + " where a.projectType=? "
					   + " and projectId in (" + projectIds + ")"
					   + " and b.parentId=? "
					   + " and a.groupName=b.groupId "
					   + " order by b.fullpath ";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectType);
			ps.setString(2, parentId);
			rs = ps.executeQuery();
			
			String projectId;
			String projectName;
			String groupId;
			
			while(rs.next()) {
				projectId = rs.getString(1);
				projectName = rs.getString(2);
				groupId = rs.getString(3);
				
				sb.append(" {cls:'file',")
				.append("id:'groupChildProject_").append(projectId).append("',")
				.append("projectId:'").append(projectId).append("',")
				.append("projectName:'").append(projectName).append("',")
				.append("projectType:'groupChildProject',")
				.append("text:'").append(projectName).append("',")
				.append("children:[").append(getProjectsByProjectType(projectType, projectIds, groupId)).append("] } ");
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
	 * 根据客户编号获得该客户下的项目JSON数据
	 * @param customerId
	 * @return
	 * @throws Exception
	 */
	public String getProjectsByCustomerId(String customerId, String projectIds) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuffer sb = new StringBuffer();
		
		try {
			
			String sql = " select projectId,projectName,projectType "
					   + " from z_project "
				       + " where customerId=? "
				       + " and projectId in (" + projectIds + ")"
				       + " and (groupName is null or groupName='' )"
				       + " order by substring(auditTimeend,1,4) desc,createTime desc ";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, customerId);

			rs = ps.executeQuery();
			
			String projectId;
			String projectName;
			String projectType;
			sb.append("[");

			while(rs.next()) {
				projectId = rs.getString(1);
				projectName = rs.getString(2);
				projectType = rs.getString(3);
				
				sb.append(" {cls:'file',")
				.append("leaf:true,")
				.append("id:'project_").append(projectId).append("',")
				.append("projectId:'").append(projectId).append("',")
				.append("projectName:'").append(projectName).append("',")
				.append("projectType:'").append(projectType).append("',")
				.append("text:'").append(projectName).append("'} ");
				if(!rs.isLast()) {
					sb.append(",");
				}
			}	
			
			sb.append("]");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return sb.toString();
	}
	
	/**
	 * 获得集团子项目
	 * @param groupProjectId
	 * @return
	 * @throws Exception
	 */
	public List getChildProject(String groupProjectId) throws Exception {
		List list = new ArrayList();
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			String sql = "select projectId from z_project "
					  + " where groupProjectId=? "
					  + " and projectType='groupChildProject' ";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, groupProjectId);
			
			rs = ps.executeQuery();
			
			while(rs.next()) {
				list.add(getProjectById(rs.getString(1)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return list;
	}
	
	
	/**
	 * 项目是否有排版
	 * @param groupProjectId
	 * @return
	 * @throws Exception
	 */
	public String hasSchedular(String projectId) throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			String sql = "select min(workDate),max(workDate) from oa_timesschedular where projectid=?";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectId);
			ASFuntion CHF = new ASFuntion();
			rs = ps.executeQuery();
			
			if(rs.next()) {
				if("".equals(CHF.showNull(rs.getString(1))) || "".equals(CHF.showNull(rs.getString(2)))) {
					return "" ;
				}
				return rs.getString(1) + "#" +rs.getString(2) ;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return "";
	}
	
	/**
	 * 项目是否有申报
	 * @param groupProjectId
	 * @return
	 * @throws Exception
	 */
	public String hasReport(String projectId) throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			String sql = "select min(workDate),max(workDate)  from oa_timesreport where projectid=?";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectId);
			ASFuntion CHF = new ASFuntion();
			rs = ps.executeQuery();
			
			if(rs.next()) {
				if("".equals(CHF.showNull(rs.getString(1))) || "".equals(CHF.showNull(rs.getString(2)))) {
					return "" ;
				}
				
				return rs.getString(1) + "#" +rs.getString(2) ;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return "";
	}
	
	public void saveAuidtPlan(AuditPlan auditPlan) {

		PreparedStatement ps = null;
		
		try {
			String sql = "insert into z_auditplan(uuid,taskid,projectid,user0,user5,user2,user3,user4,endtime,property)"
						+ " values(?,?,?,? ,?,?,?,? ,?,?)" ;
			ps = conn.prepareStatement(sql) ;
			ps.setString(1, auditPlan.getUuid());
			ps.setString(2, auditPlan.getTaskId());
			ps.setString(3, auditPlan.getProjectId());
			ps.setString(4, auditPlan.getUser0());
			ps.setString(5, auditPlan.getUser5());
			ps.setString(6, auditPlan.getUser2());
			ps.setString(7, auditPlan.getUser3());
			ps.setString(8, auditPlan.getUser4());
			ps.setString(9, auditPlan.getEndTime());
			ps.setString(10, auditPlan.getProperty());
			ps.execute() ;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		
	}
	
	public void delAuidtPlanByProjectId(String projectId) {

		PreparedStatement ps = null;
		
		try {
			String sql = "delete from z_auditplan where projectId=?" ;
			ps = conn.prepareStatement(sql) ;
			ps.setString(1,projectId) ;
			ps.execute() ;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		
	}
	
	public String getAuditPeopleByProjectId(String projectId) {

		PreparedStatement ps = null;
		ResultSet rs = null ;
		String auditPeople = "" ;
		try {
			String sql = "SELECT GROUP_CONCAT(distinct b.name) FROM z_auditpeople a LEFT JOIN k_user b ON a.userid = b.id WHERE projectid = ?" ;
			ps = conn.prepareStatement(sql) ;
			ps.setString(1,projectId) ;
			rs = ps.executeQuery() ;
			if(rs.next()) {
				auditPeople = rs.getString(1) ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		return auditPeople;
	}
	
	/**
	 * 项目进度分类统计表
	 */
	public Map getProjectStatistics(int year,String endDate,String departmentId) throws Exception{
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		String sql = "";
		try {
//			ArrayList list = new ArrayList();
			Map mapList = new HashMap();
			Map map = new HashMap();
			map.put("year", "0");
			map.put("month", "0");
			map.put("name", "以前年度");
			map.put("one1", "");//一审正常
			map.put("one2", "");//一审延误
			map.put("one3", "");//一审暂停
			
			map.put("two", "");//一审～二审
			map.put("three", "");//二审～三审
			map.put("issue", "");//三审～签发
			map.put("complete", "");//已完成
			map.put("total", "");//合计
			map.put("projectid", "");
			
//			list.add(map);
			mapList.put("pyear", map);
			
			sql = "	SELECT a.*,SUBSTRING(a.ProjectCreated,1,4) AS pYear,SUBSTRING(a.ProjectCreated,6,2) AS pMonth FROM ( \n" +
			"		SELECT projectid,ProjectCreated AS submitAuditTime,1 AS State,1 AS preState,ProjectCreated FROM z_project \n" +
			"		UNION \n" +
			"		SELECT a.projectid,a.submitAuditTime,a.curState,a.preState,b.ProjectCreated \n" + 
			"		FROM z_auditstep a ,z_project b  \n" +
			"		WHERE a.projectid = b.projectid \n" +
			"	) a ,( \n" +
			"		SELECT a.projectid,MAX(a.submitAuditTime) AS submitAuditTime \n" +	
			"		FROM ( \n" +
			"			SELECT projectid,ProjectCreated AS submitAuditTime,1 AS State,1 AS preState FROM z_project \n" +
			"			UNION \n" +
			"			SELECT projectid,submitAuditTime,curState,preState FROM z_auditstep \n" +
			"		) a ,z_project b \n" +
			"		WHERE 1=1 \n" +
			"		AND DATEDIFF(a.submitAuditTime,'" + endDate + "') <=0 \n" +
			"		AND b.DepartmentId = '" + departmentId + "' \n" +
			"		AND a.projectid = b.ProjectID \n" +
			"		GROUP BY a.projectid \n" +
			"	) b  \n" +
			"	WHERE 1=1 \n" +
			"	AND a.projectid = b.projectid \n" +
			"	AND a.submitAuditTime = b.submitAuditTime \n" +
			"	ORDER BY a.ProjectCreated";
			
			int one1 = 0,one2 = 0,one3 = 0,two = 0,three = 0,issue = 0,complete = 0,total = 0; //以前年度
			int pone1 = 0,pone2 = 0,pone3 = 0,ptwo = 0,pthree = 0,pissue = 0,pcomplete = 0,ptotal = 0; //当前年度
			int sone1 = 0,sone2 = 0,sone3 = 0,stwo = 0,sthree = 0,sissue = 0,scomplete = 0,stotal = 0; //总合计
			int month = 0;
			String pid1 = "",pid2 = "",pid3 = "";
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			sql = "select * from k_userdef " +
			"	WHERE contrastid = 'common' " +
			"	AND NAME = '提交一审时间'" +
			"	AND property = ? " +
			"	AND DATEDIFF(VALUE,?) <=0 ";
			ps1 = conn.prepareStatement(sql);
			
			while(rs.next()){
				String projectid1 = rs.getString("projectid");
				String submitAuditTime = rs.getString("submitAuditTime");
				
				int State = Math.abs(rs.getInt("State"));	//当前状态
				int preState = Math.abs(rs.getInt("preState"));//上次状态
				int pYear = rs.getInt("pYear");	//年
				int pMonth = rs.getInt("pMonth");//月
				
				pid3 += "," + projectid1;
				
				if(year != pYear){ //以前年度
//					状态:
//					1-1(没有开始流程),1-2,2-3,3-4,4-8,1-7,(-1)-7
//					2-(-1),3-(-1),4-(-1)
//					3-(-2),4-(-2)
//					4-(-3)
					
					pid1 += "," + projectid1 ; //以前年度
					
					switch (State) {
					case 1://现场开始～一审
						//判断一审时间是[正常]还是[延误]
						ps1.setString(1, projectid1);
						ps1.setString(2, submitAuditTime);
						rs1 = ps1.executeQuery();
						if(rs1.next()){
							//有表示[延误]了
							one2 ++;
							sone2 ++;
						}else{//[正常]
							one1 ++;
							sone1 ++;
						}
						DbUtil.close(rs1);
						break;
					case 2://一审～二审
						two ++;			
						stwo ++;		
						break;
					case 3://二审～三审
						three ++;
						sthree ++;
						break;
					case 4://三审～签发
						issue ++;
						sissue ++;
						break;
					case 7: //一审暂停
						if(preState == 1){
							one3 ++;
							sone3 ++;
						}
						if(preState == 2){
							two ++;			
							stwo ++;	
						}
						if(preState == 3){
							three ++;
							sthree ++;
						}
						if(preState == 4){
							issue ++;
							sissue ++;
						}
						break;	
					case 8://完成
					case 9:
						complete ++;
						scomplete ++;
						break;
					}
					total ++; //合计
					stotal ++;
					
				}else{//同一年
					
					if(month != 0 && month != pMonth){
						Map pmap = new HashMap();
						pmap.put("year", year);
						pmap.put("month", month);
						pmap.put("type", year);
						pmap.put("name", month+"月份");
						pmap.put("one1", pone1);//一审正常
						pmap.put("one2", pone2);//一审延误
						pmap.put("one3", pone3);//一审暂停
						pmap.put("two", ptwo);//一审～二审
						pmap.put("three", pthree);//二审～三审
						pmap.put("issue", pissue);//三审～签发
						pmap.put("complete", pcomplete);//已完成
						pmap.put("total", ptotal);//合计
						pmap.put("projectid", "".equals(pid2) ? pid2 : pid2.substring(1));//项目列表，以”,“分隔
//						list.add(pmap);
						mapList.put("month" + month, pmap); 
						
						pone1 = 0;
						pone2 = 0;
						pone3 = 0;
						ptwo = 0;
						pthree = 0;
						pissue = 0;
						pcomplete = 0;
						ptotal = 0;
						
						pid2 = "";
					}
					
					pid2 += "," + projectid1 ; 
					
					switch (State) {
					case 1://现场开始～一审
						//判断一审时间是[正常]还是[延误]
						ps1.setString(1, projectid1);
						ps1.setString(2, submitAuditTime);
						rs1 = ps1.executeQuery();
						if(rs1.next()){
							//有表示[延误]了
							pone2 ++;
							sone2 ++;
						}else{//[正常]
							pone1 ++;
							sone1 ++;
						}
						DbUtil.close(rs1);
						break;
					case 2://一审～二审
						ptwo ++;
						stwo ++;	
						break;
					case 3://二审～三审
						pthree ++;
						sthree ++;
						break;
					case 4://三审～签发
						pissue ++;
						sissue ++;
						break;
					case 7: //一审暂停
						if(preState == 1){
							pone3 ++;
							sone3 ++;
						}
						break;	
					case 8://完成
					case 9:
						pcomplete ++;
						scomplete ++;
						break;
					}
					ptotal ++; //合计
					stotal ++;
					
					month = pMonth;
				}
				
			}
			
			Map pmap = new HashMap();
			pmap.put("year", year);
			pmap.put("month", month);
			pmap.put("name", month+"月份");
			pmap.put("one1", pone1);//一审正常
			pmap.put("one2", pone2);//一审延误
			pmap.put("one3", pone3);//一审暂停
			pmap.put("two", ptwo);//一审～二审
			pmap.put("three", pthree);//二审～三审
			pmap.put("issue", pissue);//三审～签发
			pmap.put("complete", pcomplete);//已完成
			pmap.put("total", ptotal);//合计
			pmap.put("projectid", "".equals(pid2) ? pid2 : pid2.substring(1));//项目列表，以”,“分隔
			mapList.put("month" + month, pmap); 
			
			map.put("one1", one1);//一审正常
			map.put("one2", one2);//一审延误
			map.put("one3", one3);//一审暂停
			map.put("two", two);//一审～二审
			map.put("three", three);//二审～三审
			map.put("issue", issue);//三审～签发
			map.put("complete", complete);//已完成
			map.put("total", total);//合计
			map.put("projectid", "".equals(pid1) ? pid1 : pid1.substring(1));//项目列表，以”,“分隔
			
			Map smap = new HashMap();
			smap.put("year", year);
			smap.put("month", "0");
			smap.put("name", "合计");
			smap.put("one1", sone1);//一审正常
			smap.put("one2", sone2);//一审延误
			smap.put("one3", sone3);//一审暂停
			smap.put("two", stwo);//一审～二审
			smap.put("three", sthree);//二审～三审
			smap.put("issue", sissue);//三审～签发
			smap.put("complete", scomplete);//已完成
			smap.put("total", stotal);//合计
			smap.put("projectid", "".equals(pid3) ? pid3 : pid3.substring(1) );//项目列表，以”,“分隔
//			list.add(smap);
			mapList.put("syear", smap); 
			
//			return list;
			return mapList;
		} catch (Exception e) {
			System.out.println("出错SQL："+sql);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(rs1);
			DbUtil.close(ps);
			DbUtil.close(ps1);
		}
	}
	
	/**
	 * 拷贝项目数据
	 * @param accPackageId
	 * @param projectId
	 * @param newProjectId
	 */
	public synchronized void copyProjectData(String accPackageId, String projectId, String newProjectId) {
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		PreparedStatement ps4 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		String strSql = "";
		
		try {
			new DBConnect().changeDataBaseByAccPackageId(accPackageId, conn);
			String sql = "";
			
			//删除新项目的抽凭和调整
			sql = "delete from z_voucherspotcheck where projectid= ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1,newProjectId);
			ps.execute();
			DbUtil.close(ps);
			sql = "delete from z_vouchersampleflow where projectid= ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1,newProjectId);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "delete from z_usesubject where projectid= ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1,newProjectId);
			ps.execute();
			DbUtil.close(ps);
			sql = "delete from z_voucherrectify where projectid= ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1,newProjectId);
			ps.execute();
			DbUtil.close(ps);
			sql = "delete from z_subjectentryrectify where projectid= ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1,newProjectId);
			ps.execute();
			DbUtil.close(ps);
			sql = "delete from z_assitementryrectify where projectid= ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1,newProjectId);
			ps.execute();
			DbUtil.close(ps);
			
			try {
				strSql = "insert into z_voucherspotcheck(ProjectID,VchID,Believe,judge,Createor,"
					+" QuestDate,Property,subjectid,entryAccPackageID,entryOldVoucherID,entryTypeID,"
					+" entryVchDate,flowid,entrysubjectid,entrySerail,entrySummary,entryDirction,entryOccurValue,"
					+" entryCurrRate,entryCurrValue,entryCurrency,entryQuantity,entryUnitPrice,entryUnitName,"
					+" entryBankID,entryProperty,entrysubjectname1,entrySubjectFullName1,entryId,voucherDebitOcc,voucherCreditOcc)"
					
					+" select ?,VchID,Believe,judge,Createor,"
					+" QuestDate,Property,subjectid,entryAccPackageID,entryOldVoucherID,entryTypeID,"
					+" entryVchDate,?,entrysubjectid,entrySerail,entrySummary,entryDirction,entryOccurValue,"
					+" entryCurrRate,entryCurrValue,entryCurrency,entryQuantity,entryUnitPrice,entryUnitName,"
					+" entryBankID,entryProperty,entrysubjectname1,entrySubjectFullName1,entryId,voucherDebitOcc,voucherCreditOcc "
					+" from z_voucherspotcheck where projectid= ? and flowid = ? ";

				ps1 = conn.prepareStatement(strSql);
				//2,4
				ps1.setString(1,newProjectId);
				ps1.setString(3,projectId);
				
				strSql = "insert into z_vouchersampleflow(flowid,sampleDate,userId,projectId,sampleFlow,"
					+" sampleMethod,selectSample,subjectId,property) "
					+" select ?,sampleDate,userId,?,sampleFlow, "
					+" sampleMethod,selectSample,subjectId,property "
					+" from z_vouchersampleflow where projectid= ? and flowid = ? ";
				

				ps2 = conn.prepareStatement(strSql);
				//1,4
				ps2.setString(2,newProjectId);
				ps2.setString(3,projectId);

//				flowId = DELUnid.getNumUnid();
				sql = " select distinct flowid from z_vouchersampleflow where projectid="+projectId+" ";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
//				int flowid1 = Integer.parseInt(DELUnid.getNumUnid());
				
				while(rs.next()) {
					String flowid1 = String.valueOf(Math.random()).substring(2);
					System.out.println("flowid1="+flowid1);
					String flowid = rs.getString("flowid");
					
					ps1.setString(2, flowid1);
					ps1.setString(4, flowid);
					ps1.addBatch();
					
					ps2.setString(1, flowid1);
					ps2.setString(4, flowid);
					ps2.addBatch();
					
				}
				
				ps1.executeBatch();
				ps2.executeBatch();
				
				DbUtil.close(rs);
				DbUtil.close(ps);
				DbUtil.close(ps1);
				DbUtil.close(ps2);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			String sql1 = "";
			String oldautoid = "";
			String oldautoid1 = "";
			String VKeyId = "";
			String VKeyId1 = "";
			String str1 = "";
			String str2 = "";
			try {
				
				//无条件重用自定义科目；
				strSql="insert into z_usesubject \n"
					+"select ?,"+accPackageId+",subjectid,parentsubjectid,tipsubjectid, \n"
					+"subjectname,subjectfullname,property,level0,isleaf \n"
					+"from z_usesubject where projectid=?";
				ps = conn.prepareStatement(strSql);
				ps.setString(1,newProjectId);
				ps.setString(2,projectId);
				ps.execute();
				ps.close();
				
				//重用调整
				strSql = "insert into z_voucherrectify(autoid,AccPackageID,projectid,VoucherID,TypeID,VchDate,FillUser,AuditUser,KeepUser,"
					+"	Director,AffixCount,Description,DoubtUserId,Property)"
					+"	select ?,"+accPackageId+",?,VoucherID,TypeID,VchDate,FillUser,AuditUser,KeepUser,"
					+"	Director,AffixCount,Description,DoubtUserId,Property"
					+"	from z_voucherrectify"
					+"	where projectid = ? and autoid=?";
				ps1 = conn.prepareStatement(strSql);

				//重用调整分录
				str1 = "insert into z_subjectentryrectify("
					+"	autoid,AccPackageID,projectid,VoucherID,OldVoucherID,TypeID,VchDate,Serail,Summary,SubjectID,Dirction,"
					+"	OccurValue,CurrRate,CurrValue,Currency,Quantity,UnitPrice,BankID,Property"
					+"	)"
					+"	select ?,"+accPackageId+",?,?,OldVoucherID,TypeID,VchDate,Serail,Summary,SubjectID,Dirction,"
					+"	OccurValue,CurrRate,CurrValue,Currency,Quantity,UnitPrice,BankID,Property"
					+"	from z_subjectentryrectify"
					+"	where projectid = ? and autoid=?";
				ps3 = conn.prepareStatement(str1);

				//重用核算
				str2 = " insert into z_assitementryrectify(accpackageId,ProjectID,entryId,AssItemID,subjectId) "
					 +" select "+accPackageId+",?,?,AssItemID,subjectId from z_assitementryrectify where projectid=? and entryid=?";
				ps4 = conn.prepareStatement(str2);

//				重用调整
				sql = " select autoid from z_voucherrectify where projectid="+projectId+" ";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()) {
					VKeyId = new DELAutocode().getAutoCode("AUVO", "");
					oldautoid = rs.getString(1);

					ps1.setString(1,VKeyId);
					ps1.setString(2,newProjectId);
					ps1.setString(3,projectId);
					ps1.setString(4,oldautoid);
					ps1.addBatch();

					//重用调整分录
					sql1 = " select distinct autoid from z_subjectentryrectify where projectid="+projectId+" and voucherid="+oldautoid+" ";
					ps2 = conn.prepareStatement(sql1);
					rs1 = ps2.executeQuery();
					while(rs1.next()) {
						VKeyId1 = new DELAutocode().getAutoCode("SUAU", "");
						oldautoid1 = rs1.getString(1);

						ps3.setString(1,VKeyId1);
						ps3.setString(2,newProjectId);
						ps3.setString(3,VKeyId);
						ps3.setString(4,projectId);
						ps3.setString(5,oldautoid1);
						ps3.addBatch();

						ps4.setString(1, newProjectId);
						ps4.setString(2, VKeyId1);
						ps4.setString(3, projectId);
						ps4.setString(4, oldautoid1);
						ps4.addBatch();

					}
				}
				ps1.executeBatch();
				ps3.executeBatch();
				ps4.executeBatch();

				
				DbUtil.close(rs);
				DbUtil.close(rs1);
				DbUtil.close(ps);
				DbUtil.close(ps1);
				DbUtil.close(ps2);
				DbUtil.close(ps3);
				DbUtil.close(ps4);

				//汇总

				Repair repair = new Repair(conn);
				repair.insertData(accPackageId,newProjectId);

				RectifyService vm = new RectifyService(conn);
				vm.createTzhz(accPackageId,newProjectId);
				vm.createWbTzhz(accPackageId, newProjectId);
				vm.createAssitem1(accPackageId,newProjectId);
				
				/*
				//拷贝TB表
				String taskId = null;
	
				new DBConnect().changeDataBaseByProjectid(conn, projectId);
				sql = " select taskId from z_task where projectId=? and property=6 ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, projectId);
				rs = ps.executeQuery();
				
				if(rs.next()) {
					taskId = rs.getString(1);
				}
				
				System.out.println("找到源项目TB表：" + taskId);
				
				DbUtil.close(rs);
				DbUtil.close(ps);
				
				if(taskId != null) {
					File taskFile = new ManuFileService(conn).getProjectTaskFile(projectId, taskId);
					File newFile = null;
					new DBConnect().changeDataBaseByProjectid(conn, newProjectId);
					ps = conn.prepareStatement(sql);
					ps.setString(1, newProjectId);
					rs = ps.executeQuery();
					
					BackupUtil backupUtil = new BackupUtil();
					
					while(rs.next()) {
						String newTaskId = rs.getString(1);
						System.out.println("找到目标项目需要覆盖的TB表：" + newTaskId);
						newFile = new ManuFileService(conn).getProjectTaskFile(newProjectId, newTaskId);
						backupUtil.copyFiles(taskFile, newFile);
					}
				}
				*/
				
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(rs1);
			DbUtil.close(ps);
			DbUtil.close(ps1);
			DbUtil.close(ps2);
			DbUtil.close(ps3);
			DbUtil.close(ps4);
		}
	
	}

	public String getRemoveSkip() {
		return removeSkip;
	}

	public void setRemoveSkip(String removeSkip) {
		this.removeSkip = removeSkip;
	}
}
