package com.matech.audit.service.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.del.JRockey2Opp;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.task.model.TaskTreeNode;
import com.matech.audit.service.userdef.UserdefService;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.sys.UTILSysProperty;
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

	private boolean isTaskLib = true;
	
	//是否显示所有
	private boolean showAll = false;
	
	//我的任务userId
	private String userId = null;
	
	//必做、有数据、已保存、关注分类
	private String state = null;
	
	//是否显示一审、二审...关注底稿
	private boolean showAuditProperty = false;

	public boolean isShowAuditProperty() {
		return showAuditProperty;
	}

	public void setShowAuditProperty(boolean showAuditProperty) {
		this.showAuditProperty = showAuditProperty;
	}

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
			throw new Exception("项目Id不能为空!");
		}
		this.projectId = projectId;
		
		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
		} catch (Exception e) {
			e.printStackTrace();
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

		PreparedStatement ps2 = null;
		ResultSet rs2 = null;


		if (parentTaskId == null) {
			return "";
		}

		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);


			String taskCodes = "'-1'";

			String sql = " select ifnull(group_concat(\"'\",taskcodes ,\"'\"),\"'-1'\") from ( "
						+ " select DISTINCT t.TaskCode as taskcodes "
						+ " from z_procedure p, z_Task t  "
						+ " where t.isleaf=1  "
						+ " and p.TaskID = ?  "
						+ " and t.ProjectID = ?  "
						+ " and p.ProjectID = ?  "
						+ " and concat(',',p.manuscript,',') like concat('%,',t.taskcode,',%')  "
						+ " and p.state<>'不适用' and p.state<>'备用' "
						+ " union  "
						+ " select DISTINCT t.sheettaskcode as taskcodes  "
						+ " from z_procedure p, z_sheettask t  "
						+ " where p.TaskID = ?  "
						+ " and t.ProjectID = ?  "
						+ " and p.ProjectID = ?  "
						+ " and concat(',',p.manuscript,',') like concat('%,',t.sheettaskcode,',%')  "
						+ " and p.state<>'不适用' and p.state<>'备用'  "
						+ " ) b  ";

			ps = conn.prepareStatement(sql);
			ps.setString(1, parentTaskId);
			ps.setString(2, this.projectId);
			ps.setString(3, this.projectId);
			ps.setString(4, parentTaskId);
			ps.setString(5, this.projectId);
			ps.setString(6, this.projectId);

			rs = ps.executeQuery();

			if(rs.next()) {
				taskCodes = rs.getString(1);
			}

			sql = " select count(1) from c_account a, z_project b "
				+ " where a.accpackageId=b.accpackageId "
				+ " and b.projectid=? ";

			Object[] args = new Object[] {
				this.projectId
			};

			int hasAccount = new DbUtil(conn).queryForInt(sql,args);

			sql = "select distinct IsLeaf,TaskID,TaskName,ManuID,b.name,taskcode,user1,user2,user3,User4,user5,a.property,ismust,parentTaskId,orderid,subjectName "
						+ " from z_Task a left join k_user b on  a.user0=b.id  "
						+ " where ParentTaskID = ? "
						+ " and ProjectID = ? "
						+ " and (property not like 'A%' or property is null) ";

			//不显示叶子节点,也就是底稿
			if(!isleaf) {
				sql += " and isleaf=0 ";
			}
			
			//我的任务
			if(userId!=null && !"null".equals(userId) && !"".equals(userId)) {
				sql += " and ((isleaf=1 and user0='" + userId + "') or (isleaf=0 and exists(select 1 from z_task b where b.parentTaskId=a.taskId and b.user0='"+userId+"')))";
			} 
			
			if(state!=null && !"null".equals(state) && !"".equals(state)) {
				sql += " and (isleaf=0 or (isleaf=1 and ismust like '%" + state + "%')) ";
			}

			//如果使用底稿库
			if((isTaskLib && hasAccount > 0) && !showAll) {
				
				sql += " and ("
//					+ "	(a.SubjectName = '' or a.SubjectName is  null or a.ismust like '%3%' or a.ismust like '%1%' )"
					 //+ "	(a.isleaf=0 and ( a.SubjectName = '' or a.SubjectName is  null or a.ismust like '%3%' or a.ismust like '%1%' )   )  "
					 + "	a.ismust like '%1%' or a.ismust like '%3%' or a.SubjectName = '' or a.SubjectName is  null  "
					 //+ "	or (a.isleaf=1 and a.ismust like '%1%') "
					 + " 	or taskcode in (" + taskCodes + ") "
					 + " ) ";
				
				sql = "select * from ("
					+ sql + ") a where (isleaf=1 or (isleaf=0 and exists (select 1 from z_task b "
					+ " where (property not like 'A%' or property is null)"
					+ " and ProjectID = '" + this.projectId + "'"
					+ " and (ismust like '%1%' or ismust like '%3%' or SubjectName = '' or SubjectName is  null "
					+ " or taskcode in (" + taskCodes + ")) "
					+ " and b.parenttaskId = a.taskId))"
					+ " )";
			}

			sql += " order by orderid,taskcode";

			System.out.println("zxs:" + sql);
			
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
					tcode = "<img src=\"images/coalition.gif\" width=\"18\" height=\"16\">&nbsp;" + tcode;
				}

				sb.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");


				if (rs.getInt(1) == 0) {
					sb.append("<tr style=\"cursor: hand;\">");
					sb.append("<td onclick=\"getSubTree('" + taskId + "');\" width=\"11\" height=\"11\" align=\"right\">");
					sb.append("<img id=\"ActImg" + taskId + "\" src=\"images/plus.jpg\" width=\"11\" height=\"11\" />");
					sb.append("</td>");
					sb.append("<td onclick=\"showObj('" + taskId + "');\" align=left valign=\"bottom\" nowrap>");


					if(isleaf) {
						//显示底稿列表 
						url = "task.do?method=list&parentTaskId=" + taskId;
						
						if(showAll) {
							url += "&showAll=yes";
						} else {
							url += "&showAll=no";
						}
					} else {
						//不显示底稿列表,只显示程序和目标
						url = "task.do?method=list&parentTaskId=" + taskId + "&projectId=" + this.projectId + "&isLeaf=" + isleaf;
					}
					
					if(userId!=null && !"null".equals(userId) && !"".equals(userId)) {
						url += "&userId=" + userId;
					}
					
					if(state!=null && !"null".equals(state) && !"".equals(state)) {
						url += "&state=" + state;
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
					String ismust = asf.showNull(rs.getString(13));
					String stateHTML = getTaskStateHTML(ismust);

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
					boolean otherFile = false ;    //不是word或excel文件
					if (".doc".equalsIgnoreCase(extendName)) {
						picName = "word" + picName + ".gif";
					} else if (".xls".equalsIgnoreCase(extendName)) {
						picName = "excel" + picName + ".gif";
					}else {
						otherFile = true ;
						picName = "attachFile.gif";
					}

					sb.append("<img id=\"ActImg\" src=\"images/office/" + picName + "\" />");
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
					if(otherFile) {
						sb.append(" goDownload('"+taskId+"','"+taskName+"');\">&nbsp;") ;
					}else {
						sb.append(" goUrl(" + goUrl + ");\" >&nbsp;");
					}
					sb.append(tcode + " " + taskName + " " + taskusr + " " + stateHTML);
					sb.append("</a>");
					sb.append("</td></tr>");

					String temp = new ASFuntion().showNull(UTILSysProperty.SysProperty.getProperty("允许使用底稿库的模板属性"));
					String templateProperty = new ProjectService(conn).getProjectById(this.projectId).getTemplateType() ;
					if(!"".equals(temp) && !"".equals(templateProperty)) {
						this.isTaskLib = temp.indexOf(templateProperty) > -1;
					}
					
					if(isTaskLib) {
						//sheet.gif
						String sql2 = " select distinct sheettaskcode,sheetname "
									+ " from z_sheettask "
									+ " where projectId=? "
									+ " and taskId=? ";

						if(hasAccount > 0) {
							sql2 += " and (property like '%1%' or sheettaskcode in (" + taskCodes + ") ) ";
						}

						sql2 += " order by sheettaskcode ";
						ps2 = conn.prepareStatement(sql2);
						ps2.setString(1, this.projectId);
						ps2.setString(2, taskId);

						rs2 = ps2.executeQuery();

						String sheetTaskCode = "";
						String sheetName = "";

						while(rs2.next()) {
							sheetTaskCode = rs2.getString(1);
							sheetName = rs2.getString(2);
							sb.append("<tr style=\"cursor: hand;\">");
							sb.append("	<td width=\"16\" height=\"16\" nowrap align=\"right\" style=\"padding-left:11px;\">");
							sb.append("		<img id=\"ActImg\" src=\"images/office/sheet.gif\" />");
							sb.append("	</td>");
							sb.append("	<td align=left valign=\"bottom\" nowrap>");
							sb.append("		<a onclick=\"goUrl2('" + taskId + "','" + sheetTaskCode + "');\" >&nbsp;");
							sb.append(sheetTaskCode + " " + sheetName);
							sb.append("		</a>");
							sb.append("	</td>");
							sb.append("</tr>");
						}

						DbUtil.close(rs2);
						DbUtil.close(ps2);
					}
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
	 * 获得子树
	 * @param parentTaskId
	 * @return
	 * @throws Exception
	 */
	public List getTreeList(TaskTreeNode treeNode) throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		int hasAccount = 0;
		String taskCodes = "-1";
		List list = null ;
		
		if (treeNode == null || treeNode.getTaskId() == null) {
			return null;
		}
		
		try {
			String sql = " select ifnull(group_concat(\"'\",taskcodes ,\"'\"),\"'-1'\") from ( "
						+ " select DISTINCT t.TaskCode as taskcodes "
						+ " from z_procedure p, z_Task t  "
						+ " where t.isleaf=1  "
						+ " and p.TaskID = ?  "
						+ " and t.ProjectID = ?  "
						+ " and p.ProjectID = ?  "
						+ " and concat(',',p.manuscript,',') like concat('%,',t.taskcode,',%')  "
						+ " and p.state<>'不适用' and p.state<>'备用' "
						+ " union  "
						+ " select DISTINCT t.sheettaskcode as taskcodes  "
						+ " from z_procedure p, z_sheettask t  "
						+ " where p.TaskID = ?  "
						+ " and t.ProjectID = ?  "
						+ " and p.ProjectID = ?  "
						+ " and concat(',',p.manuscript,',') like concat('%,',t.sheettaskcode,',%')  "
						+ " and p.state<>'不适用' and p.state<>'备用'  "
						+ " ) b  ";

			ps = conn.prepareStatement(sql);
			ps.setString(1, treeNode.getTaskId());
			ps.setString(2, this.projectId);
			ps.setString(3, this.projectId);
			ps.setString(4, treeNode.getTaskId());
			ps.setString(5, this.projectId);
			ps.setString(6, this.projectId);

			rs = ps.executeQuery();

			if(rs.next()) {
				taskCodes = rs.getString(1);
			}

			sql = " select count(1) from c_account a, z_project b "
				+ " where a.accpackageId=b.accpackageId "
				+ " and b.projectid=? ";

			Object[] args = new Object[] {
				this.projectId
			};

			hasAccount = new DbUtil(conn).queryForInt(sql,args);
			
			if(!this.showAll && hasAccount > 0) {
				//更新有对应科目的底稿状态
				try {
					new TaskCommonService(conn,projectId).updateTaskHasData();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
	
			list =  getTree(treeNode, taskCodes, hasAccount);
	
			if(showAuditProperty) {
				//追加关注底稿
				sql = "select id,stepname from k_auditconfig where id>0 and id<7" ;
				ps = conn.prepareStatement(sql) ;
				rs = ps.executeQuery() ;
				while(rs.next()) {
					TaskTreeNode node = new TaskTreeNode() ;
					List<TaskTreeNode> childList = new ArrayList<TaskTreeNode>() ;
					node.setLeaf(false);
					node.setId("auditproperty"+rs.getString(1));
					node.setText(rs.getString(2)+"关注底稿");
					sql = "select taskId,taskName,taskcode,isleaf,auditproperty from z_task where auditproperty like '%"+rs.getInt(1)+ "%' and isLeaf=1 and projectId=?" ;
					
					ps = conn.prepareStatement(sql) ;
					ps.setString(1,projectId) ;
					rs2 = ps.executeQuery() ;
					while(rs2.next()) {
						TaskTreeNode chileNode = new TaskTreeNode() ;
						String chileTaskId = rs2.getString(1) ;
						String childTaskName = rs2.getString(2) ;
						String childTaskCode = rs2.getString(3) ;
						chileNode.setTaskId(chileTaskId);
						chileNode.setTaskName(childTaskName);
						chileNode.setTaskCode(childTaskCode);
						chileNode.setLeaf(rs2.getInt("isleaf") == 1);
						chileNode.setId(chileTaskId);
						chileNode.setText(childTaskCode + " " + childTaskName);
						if(childTaskName.indexOf(".xls") > -1 || childTaskName.indexOf(".xlsx") > -1 || childTaskName.indexOf(".xlsm") > -1  || childTaskName.indexOf(".xlsb") > -1 ) {
							chileNode.setIcon("/AuditSystem/img/excel.gif") ;
						}else if(childTaskName.indexOf(".doc") > -1 || childTaskName.indexOf(".docx") > -1 || childTaskName.indexOf(".docm") > -1) {
							chileNode.setIcon("/AuditSystem/img/word.gif") ;
						}else {
							chileNode.setIcon("/AuditSystem/img/attachFile.gif") ;
						}
						childList.add(chileNode) ;
					}
					node.setChildren(childList) ;
					
					list.add(node) ;
				}
			}
			//增加回收站
			TaskTreeNode node2 = new TaskTreeNode() ;
			node2.setLeaf(false);
			node2.setGroupId("taskRecycle");
			node2.setText("回收站");
			node2.setIcon("/AuditSystem/img/recycle.png") ;
			
			sql = "select autoid,taskId,taskName,taskcode,isleaf from asdb.z_taskrecycle   where projectid = ?  and saveType='0' ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);
			rs = ps.executeQuery();
			List<TaskTreeNode> childList = new ArrayList<TaskTreeNode>() ;
			while(rs.next()){
				TaskTreeNode chileNode = new TaskTreeNode() ;
				String chileTaskId = rs.getString("taskId") ;
				String childTaskName = rs.getString("taskName") ;
				String childTaskCode = rs.getString("taskcode") ;
				chileNode.setGroupId("taskRecycle");
				chileNode.setTaskId(chileTaskId);
				chileNode.setTaskName(childTaskName);
				chileNode.setTaskCode(childTaskCode);
				chileNode.setLeaf(rs.getInt("isleaf") == 1);
				chileNode.setId(rs.getString("autoid"));
				chileNode.setText(childTaskCode + " " + childTaskName);
				if(rs.getInt("isleaf") == 1){
					if(childTaskName.indexOf(".xls") > -1 || childTaskName.indexOf(".xlsx") > -1 || childTaskName.indexOf(".xlsm") > -1  || childTaskName.indexOf(".xlsb") > -1 ) {
						chileNode.setIcon("/AuditSystem/img/excel.gif") ;
					}else if(childTaskName.indexOf(".doc") > -1 || childTaskName.indexOf(".docx") > -1 || childTaskName.indexOf(".docm") > -1) {
						chileNode.setIcon("/AuditSystem/img/word.gif") ;
					}else {
						chileNode.setIcon("/AuditSystem/img/attachFile.gif") ;
					}
				}
				childList.add(chileNode) ;
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			node2.setChildren(childList) ;
			
			//增加永久性底稿
			TaskTreeNode node1 = new TaskTreeNode() ;
			node1.setId("taskForever");
			node1.setLeaf(false);
			node1.setGroupId("taskForever");
			node1.setText("永久性资料");
			
			sql = " select customerid from  z_project  where projectid=? ";
			args = new Object[] {
				this.projectId
			};
			String customerid = new DbUtil(conn).queryForString(sql,args);
			
			node1.setChildren(getAttachList("0",customerid)) ;
			
			list.add(node2);
			list.add(node1);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return list ;
	}
	
	public List getAttachList(String pid,String customerid)throws Exception{
		List list = new ArrayList();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			
			String sql = "select * from k_attachtype where ParentID = ? order by fullpath,id";
			ps = conn.prepareStatement(sql);
			ps.setString(1, pid);
			rs = ps.executeQuery();
			while(rs.next()){
				List list1 = new ArrayList();
				TaskTreeNode chileNode = new TaskTreeNode() ;
				String id = rs.getString("id") ;
				String typename = rs.getString("typename") ;
				String isleaf = rs.getString("isleaf") ;
				chileNode.setId(id);
				chileNode.setText(typename);
				chileNode.setLeaf(false);
				chileNode.setGroupId("taskForever");
				if("0".equals(isleaf)){
					list1 = getAttachList(id, customerid);
				}
				
				getAttach(list1,id,customerid);
				
				chileNode.setChildren(list1);
				list.add(chileNode);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return list;
	}
	
	public void getAttach(List list,String typeid,String customerid)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			String sql = "select * from k_attach where filename > '' and typeid = ? and departid = ? order by lastDate desc";
			ps = conn.prepareStatement(sql);
			ps.setString(1, typeid);
			ps.setString(2, customerid);
			rs = ps.executeQuery();
			while(rs.next()){
				TaskTreeNode chileNode = new TaskTreeNode() ;
				String id = rs.getString("UNID") ;
				String filename = rs.getString("filename") ;
				
				chileNode.setId(id);
				chileNode.setTaskId(typeid);
				chileNode.setText(filename);
				chileNode.setLeaf(true);
				chileNode.setGroupId("taskForever");
				if(filename.indexOf(".xls") > -1 || filename.indexOf(".xlsx") > -1 || filename.indexOf(".xlsm") > -1  || filename.indexOf(".xlsb") > -1 ) {
					chileNode.setIcon("/AuditSystem/img/excel.gif") ;
				}else if(filename.indexOf(".doc") > -1 || filename.indexOf(".docx") > -1 || filename.indexOf(".docm") > -1) {
					chileNode.setIcon("/AuditSystem/img/word.gif") ;
				}else {
					chileNode.setIcon("/AuditSystem/img/attachFile.gif") ;
				}
				
				list.add(chileNode);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 获得子树
	 * @param parentTaskId
	 * @return
	 * @throws Exception
	 */
	public List getTree(TaskTreeNode treeNode, String taskCodes, int hasAccount) throws Exception {
		ASFuntion asf = new ASFuntion();
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSet rs2 = null ;
		
		List list = new ArrayList();
		ASFuntion CHF = new ASFuntion() ;
		
		String parentTaskId = treeNode.getTaskId();

		try {

			String sql = "select distinct IsLeaf,TaskID,TaskName,ManuID,b.name,taskcode,user1,user2,user3,User4,user5,a.property,ismust,parentTaskId,orderid,subjectName,helpname "
						+ " from z_Task a left join k_user b on  a.user0=b.id  "
						+ " left join ( "
						+ "		select projectid as pid,b.taskid as tid,helpname "
						+ "		from z_project a,asdb.k_tasktemplatehelp b "
						+ "		where a.projectid = '"+this.projectId+"' and a.audittype = b.typeid "
						+ " ) c on a.projectid = c.pid and a.taskid = c.tid"
						+ " where ParentTaskID = ? "
						+ " and ProjectID = ? "
						+ " and (property not like 'A%' or property is null) ";
			
			//税审、年审底稿
			sql += this.getTaxFilter() ;
			
			//不显示叶子节点,也就是底稿
			if(!isleaf) {
				sql += " and isleaf=0 ";
			}
			
//			我的任务
			if(userId!=null && !"null".equals(userId) && !"".equals(userId)) {
				sql += " and ((isleaf=1 and user0='" + userId + "') or (isleaf=0 and exists(select 1 from z_task b where b.fullPath like concat(a.fullPath,'%') and b.user0='"+userId+"')))";
			} 
			
			if(state!=null && !"null".equals(state) && !"".equals(state)) {
				
				String tempState = "" ;
				if(state.indexOf(",") >-1) {
					tempState = "	(SubjectName = '' or SubjectName is  null or (ismust like '%" + state.split(",")[0] + "%' and ismust like '%" + state.split(",")[1] + "%') )" ;
				}else {
					tempState = " (SubjectName = '' or SubjectName is  null or ismust like '%" + state + "%') " ;
				}
				
				sql += " and (isleaf=0 or (isleaf=1 and "+ tempState +")) ";
			}

			//如果使用底稿库
			if(hasAccount > 0 && !showAll) {
				
				sql += " and ( "
					 + "	a.ismust like '%3%' or a.SubjectName = '' or a.SubjectName is  null  "
					 + " 	or taskcode in (" + taskCodes + ") "
					 + " ) ";
				
				sql = "select * from ("
					+ sql + ") a where (isleaf=1 or (isleaf=0 and not exists(select 1 from z_task b where b.parenttaskId = a.taskId) ) or (isleaf=0 and exists (select 1 from z_task b "
					+ " where (property not like 'A%' or property is null)" ;
					
					if(state!=null && !"null".equals(state) && !"".equals(state)) {
						String tempState = "" ;
						if(state.indexOf(",") >-1) {
							tempState = "	(SubjectName = '' or SubjectName is  null or (ismust like '%" + state.split(",")[0] + "%' and ismust like '%" + state.split(",")[1] + "%') )" ;
						}else {
							tempState = " (SubjectName = '' or SubjectName is  null or ismust like '%" + state + "%') " ;
						}
						
						sql += " and (isleaf=0 or (isleaf=1 and "+ tempState +")) ";
					}
					sql += " and ProjectID = '" + this.projectId + "'"
					+ " and (ismust like '%1%' or ismust like '%3%' or SubjectName = '' or SubjectName is  null "
					+ " or taskcode in (" + taskCodes + ")) "
					+ " and b.parenttaskId = a.taskId))"
					+ " )";
			}

			sql += " order by taskcode";
 			ps = conn.prepareStatement(sql);
			ps.setString(1, parentTaskId);
			ps.setString(2, this.projectId);
			rs = ps.executeQuery();
			
			TaskTreeNode childTaskNode = null;

			String taskName;
			String taskCode;
			String taskId;
			String user0;
			String helpname ;
			while (rs.next()) {
				 
				childTaskNode = new TaskTreeNode();
				
				//分工人
				user0 = rs.getString(5);
				taskName = rs.getString("taskName");
				taskCode = rs.getString("taskcode");
				taskId = rs.getString("taskId");
				helpname =  rs.getString("helpname");
				
				if (user0 != null && !"".equals(user0)) {
					user0 = "(" + user0 + ")";
				} else {
					user0 = "";
				}
				
				childTaskNode.setTaskCode(taskCode);
				childTaskNode.setTaskName(taskName);
				childTaskNode.setTaskId(taskId);
				childTaskNode.setLeaf(rs.getInt("isleaf") == 1);
				childTaskNode.setId(taskId);
				childTaskNode.setHelpname(helpname);
				
				if (rs.getInt(1) == 0) {
					//非叶子 
					childTaskNode.setText(taskCode + " " + taskName);
					childTaskNode.setChildren(getTree(childTaskNode,taskCodes,hasAccount));
				} else {
					//叶子
					String user1 = asf.showNull(rs.getString(7));
					String user2 = asf.showNull(rs.getString(8));
					String user3 = asf.showNull(rs.getString(9));
					String user4 = asf.showNull(rs.getString(10));
					String user5 = asf.showNull(rs.getString(11));
					String ismust = asf.showNull(rs.getString(13));
					String stateHTML = getTaskStateHTML(ismust);

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
					boolean otherFile = false ;    //不是word或excel文件
					if (".doc".equalsIgnoreCase(extendName)) {
						picName = "word" + picName + ".gif";
					} else if (".xls".equalsIgnoreCase(extendName)) {
						picName = "excel" + picName + ".gif";
					}else {
						otherFile = true ;
						picName = "attachFile.gif";
					}
					
					childTaskNode.setIcon("/AuditSystem/img/" + picName);

					String text = taskCode + " " + taskName + user0;

					if (!"".equals(user4)) {
						text = "<span style=\"font-weight:bold; color:red;\" >" + text + "</span>";
					}

					childTaskNode.setText(text);

				}
				
				list.add(childTaskNode);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return list;

	}

	
	
	public List getTreeByAuditPlan(String parentId) throws Exception {
		ASFuntion asf = new ASFuntion();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		List list = new ArrayList();
		
		try {

			String sql = "select distinct IsLeaf,a.TaskID,TaskName,ManuID,b.name,taskcode,a.user1,a.user2,a.user3,a.User4,a.user5,a.property,ismust,parentTaskId," 
					   + " orderid,subjectName,c.user0 u0,c.user5 u5,c.user2 u2,c.user3 u3,c.user4 u4,c.endtime"
						+ " from z_Task a left join k_user b on  a.user0=b.id  "
						+ " left join z_auditPlan c on a.taskId = c.taskId and a.projectId = c.projectId "
						+ " where ParentTaskID = ? "
						+ " and a.ProjectID = ? "
						+ " and (a.property not like 'A%' or a.property is null) ";
			//不显示叶子节点,也就是底稿
			if(!isleaf) {
				sql += " and isleaf=0 ";
			}
			
//			我的任务
			if(userId!=null && !"null".equals(userId) && !"".equals(userId)) {
				sql += " and ((isleaf=1 and a.user0='" + userId + "') or (isleaf=0 and exists(select 1 from z_task b where b.parentTaskId=a.taskId and b.user0='"+userId+"')))";
			} 
			
			if(state!=null && !"null".equals(state) && !"".equals(state)) {
				  
				String tempState = "" ;
				if(state.indexOf(",") >-1) {
					tempState = "	(SubjectName = '' or SubjectName is  null or (ismust like '%" + state.split(",")[0] + "%' and ismust like '%" + state.split(",")[1] + "%') )" ;
				}else {
					tempState = " (SubjectName = '' or SubjectName is  null or ismust like '%" + state + "%') " ;
				}
				
				sql += " and (isleaf=0 or (isleaf=1 and "+ tempState +")) ";
			}


			sql += " order by orderid,taskcode";
 			ps = conn.prepareStatement(sql);
			ps.setString(1, parentId);
			ps.setString(2, this.projectId);
			
			rs = ps.executeQuery();
			
			Map childMap = null ;

			String taskName;
			String taskCode;
			String taskId;
			String user0;
			String endtime,u0,u5,u2,u3,u4;
			while (rs.next()) {
				
				childMap = new HashMap();
				
				//分工人
				user0 = rs.getString(5);
				taskName = rs.getString("taskName");
				taskCode = rs.getString("taskcode");
				taskId = rs.getString("taskId");
				endtime = asf.showNull(rs.getString("endtime"));
				
				u0 = asf.showNull(rs.getString("u0"));
				u5 = asf.showNull(rs.getString("u5"));
				u2 = asf.showNull(rs.getString("u2"));
				u3 = asf.showNull(rs.getString("u3"));
				u4 = asf.showNull(rs.getString("u4"));
				if (user0 != null && !"".equals(user0)) {
					user0 = "(" + user0 + ")";
				} else {
					user0 = "";
				}
				childMap.put("user0","<input id=\"user0"+taskId+"\" preValue=\""+u0+"\" style=\"background:#E8FFDF\" name=\"user0\" type=text onkeydown=\"onKeyDownEvent();\" value=\""+u0+"\" "
	                    		+" hideresult=true onkeyup=\"onKeyUpEvent();\" onclick=\"onPopDivClick(this);\" onchange=\"expandChild(\'"+taskId+"\',this,'user0');\" autoid=164>") ;
				
				childMap.put("user5","<input id=\"user5"+taskId+"\" preValue=\""+u5+"\" style=\"background:#E8FFDF\" name=\"user5\" type=text onkeydown=\"onKeyDownEvent();\" value=\""+u5+"\" "
                		+" hideresult=true onkeyup=\"onKeyUpEvent();\" onclick=\"onPopDivClick(this);\" onchange=\"expandChild(\'"+taskId+"\',this,'user5');\" autoid=164>") ;
				
				childMap.put("user2","<input id=\"user2"+taskId+"\" preValue=\""+u2+"\" style=\"background:#E8FFDF\" name=\"user2\" type=text onkeydown=\"onKeyDownEvent();\" value=\""+u2+"\" "
                		+" hideresult=true onkeyup=\"onKeyUpEvent();\" onclick=\"onPopDivClick(this);\" onchange=\"expandChild(\'"+taskId+"\',this,'user2');\" autoid=2041 refer=二审人>") ;
				
				childMap.put("user3","<input id=\"user3"+taskId+"\" preValue=\""+u3+"\" style=\"background:#E8FFDF\" name=\"user3\" type=text onkeydown=\"onKeyDownEvent();\" value=\""+u3+"\" "
                		+" hideresult=true onkeyup=\"onKeyUpEvent();\" onclick=\"onPopDivClick(this);\" onchange=\"expandChild(\'"+taskId+"\',this,'user3');\" autoid=2041 refer=三审人>") ;
				
				childMap.put("user4","<input id=\"user4"+taskId+"\" preValue=\""+u4+"\" style=\"background:#E8FFDF\" name=\"user4\" type=text onkeydown=\"onKeyDownEvent();\" value=\""+u4+"\" "
                		+" hideresult=true onkeyup=\"onKeyUpEvent();\" onclick=\"onPopDivClick(this);\" onchange=\"expandChild(\'"+taskId+"\',this,'user4');\" autoid=2041 refer=签发人>") ;
				
				childMap.put("leaf", rs.getInt("isleaf") == 1) ;
				childMap.put("id", taskId) ;
				childMap.put("endtime", "<input id=\"date"+taskId+"\" value=\""+endtime+"\" name=\"date\" type=\"text\" class=\"date\" taskId=\""+taskId+"\"> "
	                    		+"<input name=\"taskId\" type=\"hidden\" value=\""+taskId+"\">");
				
				if (rs.getInt(1) == 0) {
					//非叶子
					childMap.put("text", taskCode + " " + taskName) ;
					childMap.put("children",getTreeByAuditPlan(taskId));
				} else {
					//叶子
					String user1 = asf.showNull(rs.getString(7));
					String user2 = asf.showNull(rs.getString(8));
					String user3 = asf.showNull(rs.getString(9));
					String user4 = asf.showNull(rs.getString(10));
					String user5 = asf.showNull(rs.getString(11));
					String ismust = asf.showNull(rs.getString(13));

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
					boolean otherFile = false ;    //不是word或excel文件
					if (".doc".equalsIgnoreCase(extendName)) {
						picName = "word" + picName + ".gif";
					} else if (".xls".equalsIgnoreCase(extendName)) {
						picName = "excel" + picName + ".gif";
					}else {
						otherFile = true ;
						picName = "attachFile.gif";
					}
					
					childMap.put("icon","/AuditSystem/img/" + picName);
					
					String text = taskCode + " " + taskName + user0;

					if (!"".equals(user4)) {
						text = "<span style=\"font-weight:bold; color:red;\" >" + text + "</span>";
					}

					childMap.put("text",text);
					childMap.put("children","[]");
				}
				
				list.add(childMap);
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

			ps = conn.prepareStatement("select parenttaskid,level0 from z_task where projectid=? and taskid=?");
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

	/**
	 * 获得审计环节和审计目标的树
	 * @param parentTaskId
	 * @return
	 * @throws Exception
	 */
	public String getTacheSubTree(String parentTaskId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";

		if (parentTaskId == null) {
			return "";
		}

		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			sql = "select IsLeaf,TaskID,TaskName,taskcode,property from z_Task "
				+ " where ParentTaskID = ? "
				+ " and ProjectID = ? "
				+ " and (property ='tache' or property ='target') order by orderid,taskcode";

			ps = conn.prepareStatement(sql);
			ps.setString(1, parentTaskId);
			ps.setString(2, this.projectId);
			rs = ps.executeQuery();

			StringBuffer sb = new StringBuffer("");
			while (rs.next()) {

				String isTache = rs.getString(5);
				String taskId = rs.getString(2);
				String taskName = rs.getString(3);
				String taskcode = rs.getString(4);

				sb.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");

				if ("tache".equals(isTache.toLowerCase())) {
					sb.append("<tr style=\"cursor: hand;\">");
					sb.append("<td  onclick=\"getSubTree('" + taskId + "');\" width=\"11\" height=\"11\" align=\"right\">");
					sb.append("<img id=\"ActImg" + taskId + "\" src=\"images/plus.jpg\" width=\"11\" height=\"11\" />");
					sb.append("</td>");
					sb.append("<td align=left valign=\"bottom\" nowrap>");
					sb.append("<a id='a" + taskId + "' href=\"tache.do?method=list&parentTaskId=" + taskId + "\" target=\"TaskMainFrame\" onclick=\"doIt(this);showObj('" + taskId + "');\">");
					sb.append("		<font size=2>&nbsp;" + taskcode + " " + taskName);
					sb.append("</a></td>");
					sb.append("</tr>");
				} else {
					sb.append("<tr style=\"cursor: hand;\">");
					sb.append("<td align=left valign=\"bottom\" nowrap>");
					sb.append("<a id='a" + taskId + "' href=\"tache.do?method=procedureList&taskId=" + taskId + "\" target='TaskMainFrame' onclick='doIt(this);'>");
					sb.append("		<font size=2>&nbsp;" + taskcode + " " + taskName);
					sb.append("</a></td>");
					sb.append("</tr>");
				}

				sb.append("<tr>");
				sb.append("<td id='subImg" + taskId + "' style='display:none'></td>");
				sb.append("<td id='subTree" + taskId + "' style='display:none'></td>");
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

	/**
	 * 获得审计环节和审计目标的树 用于底稿分工
	 * @param pid
	 * @param proid
	 * @return
	 * @throws Exception
	 */
	public String getTacheSubTree2(String parentTaskId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";

		if (parentTaskId == null) {
			return "";
		}

		try {
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

			sql = "select IsLeaf,TaskID,TaskName,taskcode,property from z_Task where ParentTaskID = ?"
				+ " and ProjectID = ?"
				+ " and (property ='tache' or property ='target') order by orderid,taskcode";

			ps = conn.prepareStatement(sql);
			ps.setString(1, parentTaskId);
			ps.setString(2, this.projectId);
			rs = ps.executeQuery();

			StringBuffer sb = new StringBuffer("");
			while (rs.next()) {

				String isTache = rs.getString(5);
				String taskId = rs.getString(2);
				String taskName = rs.getString(3);
				String taskcode = rs.getString(4);

				sb.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");

				if ("tache".equals(isTache.toLowerCase())) {
					sb.append("<tr style=\"cursor: hand;\">");
					sb.append("<td  onclick=\"getSubTree('" + taskId + "');\" width=\"11\" height=\"11\" align=\"right\">");
					sb.append("<img id=\"ActImg" + taskId + "\" src=\"../images/plus.jpg\" width=\"11\" height=\"11\" />");
					sb.append("</td>");
					sb.append("<td align=left valign=\"bottom\" nowrap>");
					sb.append("<a id='a" + taskId + "' taskid=\""+taskId+"\" href=\"javascript:doIt('"+taskId+"');getSubTree('" + taskId + "');\">");
					sb.append("		<font size=2>&nbsp;" + taskcode + " " + taskName);
					sb.append("</a></td>");
					sb.append("</tr>");
				} else {
					sb.append("<tr style=\"cursor: hand;\">");
					sb.append("<td align=left valign=\"bottom\" nowrap>");
					sb.append("<a id='a" + taskId + "' taskid=\""+taskId+"\" href=\"javascript:doIt('"+taskId+"')\">");
					sb.append("		<font size=2>&nbsp;" + taskcode + " " + taskName);
					sb.append("</a></td>");
					sb.append("</tr>");
				}

				sb.append("<tr>");
				sb.append("<td id='subImg" + taskId + "' style='display:none'></td>");
				sb.append("<td id='subTree" + taskId + "' style='display:none'></td>");
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
	
	
	/**
	 * 获得审计环节和审计目标的树 用于底稿分工
	 * @param pid
	 * @param proid
	 * @return
	 * @throws Exception
	 */
	public String getTaxFilter() throws Exception {
		
		try {
			ASFuntion asfFunction = new ASFuntion();
			//税审、年审底稿
			Map dogInfo = JRockey2Opp.getInfoFromDog();
			if (dogInfo==null){
				//没读到，就多读一次DLL狗
				dogInfo=JRockey2Opp.readInfo();
			}
			String sysVn = asfFunction.showNull((String)dogInfo.get("sysVn")) ;
			
			UserdefService us = new UserdefService(conn) ;
			String taxAudit = asfFunction.showNull(us.getValue("年审/税审","common",this.projectId));
			
			StringBuffer sql = new StringBuffer() ; 
			
			if(sysVn.indexOf("税审版") >-1 && sysVn.indexOf("企业版") >-1) {
				//年税合一的狗，再判断建项时选了年审还是税审
				if(taxAudit.indexOf("税审版") > -1 && taxAudit.indexOf("企业版") > -1) {
					sql.append(" and (manuid like '-1%' or manuid = '-2' or manuid not like '-%' or manuid is null) ");
				}else if(taxAudit.indexOf("税审版") > -1) {
					sql.append(" and (manuid = '-2' or manuid = '-12' or manuid not like '-%' or manuid is null) ");
				}else if(taxAudit.indexOf("企业版") > -1) {
					sql.append(" and (manuid like '-1%' or manuid not like '-%' or manuid is null) ");
				}else {
					//两个都没选的，可能是旧项目，就全部底稿都显示
					sql.append(" and (manuid not like '-%' or manuid is null) ");
				}
			}else if(sysVn.indexOf("企业版") >-1) {
				sql.append(" and (manuid like '-1%' or manuid not like '-%' or manuid is null) ");
			}else if(sysVn.indexOf("税审版") >-1) {
				sql.append(" and (manuid like '-2%' or manuid not like '-%' or manuid is null) ");
			} 
			return sql.toString() ;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
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

	public boolean isIsleaf() {
		return isleaf;
	}

	public void setIsleaf(boolean isleaf) {
		this.isleaf = isleaf;
	}

	public boolean isShowAll() {
		return showAll;
	}

	public void setShowAll(boolean showAll) {
		this.showAll = showAll;
	}
	
	public static void main(String[] args) {
		Connection conn = null;
		
		try {
			conn = new DBConnect().getConnect("");
			
			TaskTreeNode taskTreeNode = new TaskTreeNode();
			taskTreeNode.setTaskId("0");
			List list = new TaskTreeService(conn,"20099234").getTreeList(taskTreeNode);
			
			System.out.println(list.size());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
	}
	
	public String getTaskJSON(String parentTaskId) {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		
		try {
			
			String sql = " select taskId,taskcode,taskname,isleaf from z_task "
					+ " where projectid=? "
					+ " and parentTaskId=? ";
			
			if(this.state!=null && !"null".equals(this.state) && !"".equals(this.state)) {
				sql += " and (isleaf=0 or (isleaf=1 and ismust like '%" + this.state + "%')) ";
			}
			
			sql += " order by taskCode ";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);
			ps.setString(2, parentTaskId);
			
			rs = ps.executeQuery();
			sb.append("[");
			while(rs.next()) {
				String taskId = rs.getString(1);
				String taskCode = rs.getString(2);
				String taskName = rs.getString(3);
				String isLeaf = rs.getString(4);
				
				sb.append(" {cls:'folder',")
					.append("leaf:").append("1".equals(isLeaf)).append(",")
					.append("id:'").append(taskId).append("',")
					.append("text:'").append(taskCode + " " + taskName).append("' ");
				
				if(!"1".equals(isLeaf)) {
					sb.append(" ,children:").append(getTaskJSON(taskId));
				}
				
				sb.append("}");
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
