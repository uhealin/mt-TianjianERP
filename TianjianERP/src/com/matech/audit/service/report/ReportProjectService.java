package com.matech.audit.service.report;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.audittypetemplate.AuditTypeTemplateService;
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.task.TaskCommonService;
import com.matech.audit.service.task.TaskService;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.UTILString;

/**
 * <p>Title: 构造合并报表底稿树类</p>
 * <p>Description: 构造合并报表底稿树</p>
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
 * 2007-9-21
 */
public class ReportProjectService {
	private Connection conn;	//数据库连接
	private String projectId;	//项目编号
	private String systemId;	//合并报表体系编号
	private boolean isCoalitionNode;

	/**
	 * 构造方法
	 * @param conn	连接
	 * @param projectId	项目编号
	 * @param systemId	合并报表体系编号
	 * @throws Exception
	 */
	public ReportProjectService(Connection conn, String projectId, String systemId) throws Exception {
		DbUtil.checkConn(conn);
		this.conn = conn;

		if(projectId == null || "".equals(projectId)) {
			throw new Exception("项目编号不能为空:projectId=" + projectId);
		} else {
			this.projectId = projectId;
		}

		if(systemId == null || "".equals(systemId)) {
			throw new Exception("体系编号不能为空:systemId=" + systemId);
		} else {
			this.systemId = systemId;
		}
	}

	/**
	 * 根据模板类型typeId创建合并报表的底稿树
	 * @param typeId
	 * @throws Exception
	 */
	public void createTree(String typeId) throws Exception {

		String taskId = copyTemplate(typeId);

		String sql = " select autoId from asdb.k_customerrelation where systemid = ? and parentid = 0 ";
		Object[] params = new Object[] {this.systemId};
		String parentId = new DbUtil(conn).queryForString(sql,params);
		setTree(parentId, taskId, typeId);

		//修复全路径
		try {
			new TaskService(conn,this.projectId).repairTaskFullPath();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

    /**
     * 递归方法,主要根据合并报表体系节点构建底稿树
     * @param parentId
     * @param taskId
     * @param typeId
     * @throws Exception
     */
    private void setTree(String parentId, String taskId, String typeId) throws Exception {
    	PreparedStatement ps = null;
    	ResultSet rs = null;

    	try {
    		 String sql = " select a.autoid,a.customerid,a.nodename,a.property,b.departName,c.relationprojectid "
    			 		+ " from asdb.k_customerrelation a "
    			 		+ " left join asdb.k_customer b on a.customerid = b.departId "
    			 		+ " left join asdb.z_projectentry c on a.autoid=c.relationid and c.projectid = ? "
    			 		+ " where a.systemid = ? "
    			 		+ " and a.parentid= ? "
    			 		+ " order by a.property  ";

    		ps = conn.prepareStatement(sql);
    		ps.setString(1, this.projectId);
    		ps.setString(2, systemId);
    		ps.setString(3, parentId);

    		rs = ps.executeQuery();

    		while(rs.next()) {
    			//如果是不参与合并,则跳过继续
    			String relationprojectid = rs.getString(6);

    			if("-2".equals(relationprojectid)) {
    				continue;
    			}

    			//如果是合并节点,则copy整棵树过来
    			if("2000000000".equals(rs.getString(4))) {
    				hasCoalitionNode(rs.getString(1));

    				//如果下级节点有参加合并
    				if(isCoalitionNode) {
    					this.isCoalitionNode = false;
    					String tempTaskId = createCoalitionNode(taskId, rs.getString(3), typeId);
        				setTree(rs.getString(1),tempTaskId,typeId);
    				}

    			} else {
    				//如果是公司节点,则copy单体模版
    				createSingle(taskId, rs.getString(5), typeId);
    			}
    		}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
    }

    /**
     * 拷贝基本模板
     * @param typeId
     * @return
     * @throws Exception
     */
    private String copyTemplate(String typeId ) throws Exception {
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	String taskId = "";
    	try {

    		new DBConnect().changeDataBaseByProjectid(conn, projectId);

    		String taskIds = "''";
    		String sql = "select group_concat(a.taskid) from k_tasktemplate a, "
						+ " (select taskid from k_tasktemplate where typeid = ? and property = '20' ) b "
						+ " where a.typeid = ? and a.isleaf=1 "
						+ " and a.fullpath not like concat('%',b.taskid,'|%')";
    		ps = conn.prepareStatement(sql);
    		ps.setString(1, typeId);
    		ps.setString(2, typeId);

    		rs = ps.executeQuery();


    		if (rs.next()) {
    			taskIds = rs.getString(1);
    		}

    		rs.close();
    		ps.close();

    		//拷贝模版底稿文件
    		new ManuFileService(conn).copyTemplateToProject(typeId, projectId, taskIds);

    		//从任务模板中恢复任务
    		sql = "delete from z_task where ProjectID=?";
    		ps = conn.prepareStatement(sql);
    		ps.setString(1, projectId);
    		ps.execute();
    		ps.close();
    		sql = "insert into z_task(TaskID,TaskCode,TaskName,TaskContent,Description,"
    				+ "ParentTaskID,ProjectID,IsLeaf,level0,ManuID,ManuTemplateID,Property,FullPath,orderid,ismust,subjectname) "
    				+ "select a.TaskID,a.TaskCode,a.TaskName,a.TaskContent,a.Description,a.ParentTaskID,?,"
    				+ "a.IsLeaf,a.level0,a.ManuTemplateID,a.ManuTemplateID,a.Property,a.FullPath,a.orderid,a.ismust,a.subjectname "
    				+ "from k_tasktemplate a, "
						+ " (select taskid from k_tasktemplate where typeid = ? and property = '20' ) b "
						+ " where a.typeid = ? "
						+ " and a.fullpath not like concat('%',b.taskid,'|%') ";
    		ps = conn.prepareStatement(sql);
    		ps.setString(1, projectId);
    		ps.setString(2, typeId);
    		ps.setString(3, typeId);
    		ps.execute();
    		ps.close();

    		sql = "update  z_task set manuid=null where ProjectID=? and isleaf=0";
    		ps = conn.prepareStatement(sql);
    		ps.setString(1, projectId);
    		ps.execute();
    		ps.close();

    		//找出新插入的单体节点taskId
    		sql = " select taskId from z_task where parentTaskId = ? and property = '21' ";
    		ps = conn.prepareStatement(sql);
    		ps.setString(1, "0");
    		rs = ps.executeQuery();

    		if(rs.next()) {
    			taskId = rs.getString(1);
    		}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return taskId;
    }

    /**
     * 拷贝单个结点
     * @param parentTaskId
     * @param typeId
     * @param taskId
     * @return
     * @throws Exception
     */
    private String copyTask(String parentTaskId,String typeId, String taskId) throws Exception {
    	PreparedStatement ps = null;

    	String newTaskId = "";
    	try {

    		newTaskId = getMaxTaskId();
    		String taskCode = new TaskCommonService(conn,projectId).getMaxTaskCodeByParentTaskId(parentTaskId, 0);
    		taskCode = UTILString.getNewTaskCode(taskCode);

    		String orderId = UTILString.getOrderId(taskCode);

    		String sql = "insert into z_task(TaskID,TaskCode,TaskName,TaskContent,Description,"
				+ "ParentTaskID,ProjectID,IsLeaf,ManuID,ManuTemplateID,Property,orderid ) "
				+ " select ?,?,TaskName,TaskContent,Description,"
				+ " ?,?,IsLeaf,ManuTemplateID, ManuTemplateID,Property,? "
				+ " from k_tasktemplate "
				+ " where typeid = ? "
				+ " and taskid = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, newTaskId);
			ps.setString(2, taskCode);

			ps.setString(3, parentTaskId);
			ps.setString(4, projectId);
			ps.setString(5, orderId);
			ps.setString(6, typeId);
			ps.setString(7, taskId);
			ps.execute();
			ps.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

		return newTaskId;
    }

    /**
     * 拷贝单张底稿
     * @param parentTaskId
     * @param typeId
     * @param taskId
     * @throws Exception
     */
    private void copyManuScript(String parentTaskId,String typeId, String taskId) throws Exception {
    	PreparedStatement ps = null;

    	try {
    		String newTaskId = getMaxTaskId();
    		String taskCode = new TaskCommonService(conn,projectId).getMaxTaskCodeByParentTaskId(parentTaskId, 1);
    		taskCode = UTILString.getNewTaskCode(taskCode);

    		String orderId = UTILString.getOrderId(taskCode);

			//拷贝底稿文件
    		new ManuFileService(conn).copyTemplateToProject(typeId, projectId,taskId,newTaskId);

    		//
    		String sql = "insert into z_task(TaskID,TaskCode,TaskName,TaskContent,Description,"
						+ "ParentTaskID,ProjectID,IsLeaf,ManuID,ManuTemplateID,Property,orderId ) "
						+ " select ?,?,TaskName,TaskContent,Description,"
						+ " ?,?,IsLeaf,?,ManuTemplateID,Property,? "
						+ " from k_tasktemplate "
						+ " where typeid = ? "
						+ " and taskid = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, newTaskId);
			ps.setString(2, taskCode);

			ps.setString(3, parentTaskId);
			ps.setString(4, projectId);
			ps.setInt(5, 0);
			ps.setString(6, orderId);
			ps.setString(7, typeId);
			ps.setString(8, taskId);
			ps.execute();
			ps.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
    }

    /**
     * 拷贝底稿树
     * @param parentTaskId
     * @param newParentTaskId
     * @param typeId
     */
    private void copyTree(String parentTaskId,String newParentTaskId, String typeId) {
    	PreparedStatement ps = null;
    	ResultSet rs = null;

    	try {
    		String sql = "select taskId,isleaf from k_tasktemplate "
    					+ " where parentTaskId = ? and typeId = ? order by orderid ";
    		ps = conn.prepareStatement(sql);
    		ps.setString(1, parentTaskId);
    		ps.setString(2, typeId);
    		rs = ps.executeQuery();

    		while(rs.next()) {
    			if(rs.getInt(2) == 0) {
    				//如果是非叶子节点,则继续往下找
    				String newTaskId = copyTask(newParentTaskId,typeId, rs.getString(1));

    				copyTree(rs.getString(1),newTaskId,typeId);
    			} else {
    				//如果是叶子节点
    				copyManuScript(newParentTaskId,typeId, rs.getString(1));
    				//sql = ""
    			}
    		}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

    }

    /**
     * 创建单体
     * @param parentTaskId
     * @param taskName
     * @param typeId
     */
    private void createSingle(String parentTaskId,String taskName, String typeId) {
    	PreparedStatement ps = null;
    	ResultSet rs = null;

    	try {
    		//找到单体
    		String sql = "select taskid,isleaf "
    					+ " from k_tasktemplate "
    					+ " where typeid = ? and property = '20' order by orderid ";

    		String newTaskId = getMaxTaskId();
    		String taskId = "";

			String isleaf = "";

    		ps = conn.prepareStatement(sql);
    		ps.setString(1, typeId);
    		rs = ps.executeQuery();

    		if(rs.next()) {
    			taskId = rs.getString(1);
    			isleaf = rs.getString(2);
    		}

    		rs.close();
    		ps.close();

    		String taskCode = new TaskCommonService(conn,projectId).getMaxTaskCodeByParentTaskId(parentTaskId, 0);
    		taskCode = UTILString.getNewTaskCode(taskCode);

    		String orderId = UTILString.getOrderId(taskCode);

    		sql = " insert into z_task(TaskID,TaskCode,TaskName,ParentTaskID,ProjectID,IsLeaf,Property,orderId ) "
    			+ " values(?,?,?,?,?,?,?,?) ";
    		ps = conn.prepareStatement(sql);
    		ps.setString(1, newTaskId);
    		ps.setString(2, taskCode);
    		ps.setString(3, taskName);
    		ps.setString(4, parentTaskId);
    		ps.setString(5, projectId);
    		ps.setString(6, isleaf);
    		ps.setString(7, "20");
    		ps.setString(8, orderId);

    		ps.execute();
    		ps.close();


    		copyTree(taskId,newTaskId,typeId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
			DbUtil.close(rs);
		}

    }

    /**
     * 拷贝合并结点
     * @param parentTaskId
     * @param newParentTaskId
     * @param typeId
     */
    private void copyCoalitionNode(String parentTaskId,String newParentTaskId, String typeId) {
    	PreparedStatement ps = null;
    	ResultSet rs = null;

    	try {
        	String sql = "select a.taskid, a.isleaf from k_tasktemplate a, "
						+ " (select taskid from k_tasktemplate where typeid = ? and property = '20' ) b "
						+ " where a.typeid = ? "
						+ " and a.parentTaskId = ? "
						+ " and a.fullpath not like concat('%',b.taskid,'|%') order by a.orderid";
    		ps = conn.prepareStatement(sql);
    		ps.setString(1, typeId);
    		ps.setString(2, typeId);
    		ps.setString(3, parentTaskId);
    		rs = ps.executeQuery();

    		while(rs.next()) {
    			if(rs.getInt(2) == 0) {
    				//如果是非叶子节点,则继续往下找
    				String newTaskId = copyTask(newParentTaskId,typeId, rs.getString(1));

    				copyCoalitionNode(rs.getString(1),newTaskId,typeId);
    			} else {
    				//如果是叶子节点
    				copyManuScript(newParentTaskId,typeId, rs.getString(1));
    				//sql = ""
    			}
    		}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

    }

    /**
     * 创建合并结点
     * @param parentTaskId
     * @param taskName
     * @param typeId
     * @return
     */
    private String createCoalitionNode(String parentTaskId,String taskName, String typeId) {


    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	String nodeTaskId = "";
    	try {

    		String newTaskId = getMaxTaskId();

    		String taskCode = new TaskCommonService(conn,projectId).getMaxTaskCodeByParentTaskId(parentTaskId, 0);
    		taskCode = UTILString.getNewTaskCode(taskCode);

    		String orderId = UTILString.getOrderId(taskCode);

    		String sql = " insert into z_task(TaskID,TaskCode,TaskName,ParentTaskID,ProjectID,IsLeaf,orderId,property ) "
    					+ " values(?,?,?,?,?,?,?,?) ";
    		ps = conn.prepareStatement(sql);
    		ps.setString(1, newTaskId);
    		ps.setString(2, taskCode);
    		ps.setString(3, taskName);
    		ps.setString(4, parentTaskId);
    		ps.setString(5, projectId);
    		ps.setString(6, "0");
    		ps.setString(7, orderId);
    		ps.setString(8, "22");

    		ps.execute();
    		ps.close();


    		copyCoalitionNode("0",newTaskId,typeId);

    		sql = " select taskId from z_task where parentTaskId = ? and property = '21' ";
    		ps = conn.prepareStatement(sql);
    		ps.setString(1, newTaskId);
    		rs = ps.executeQuery();

    		if(rs.next()) {
    			nodeTaskId = rs.getString(1);
    		}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
			DbUtil.close(rs);
		}

		return nodeTaskId;

    }

    /**
     * 判断下级节点有没有参加合并
     * @param autoId
     * @throws Exception
     */
    private void hasCoalitionNode(String autoId) throws Exception {
    	PreparedStatement ps = null;
    	ResultSet rs = null;

    	try {
    		String sql = " select a.autoId,b.relationprojectid "
						+ " from asdb.k_customerrelation a  "
						+ " left join asdb.z_projectentry b on a.autoid=b.relationid and b.projectid = ? "
						+ " where a.systemid = ? "
						+ " and a.parentId = ? ";

    		ps = conn.prepareStatement(sql);
    		ps.setString(1, this.projectId);
    		ps.setString(2, this.systemId);
    		ps.setString(3, autoId);

    		rs = ps.executeQuery();
    		while(rs.next()) {
    			if("-1".equals(rs.getString(2))) {
    				isCoalitionNode = true;
    				break;
    			}

    			hasCoalitionNode(rs.getString(1));
    		}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
    }

	/**
	 * 获得最大的taskId
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	private String getMaxTaskId() throws Exception {
		new DBConnect().changeDataBaseByProjectid(conn, projectId);
		String strSql = "select ifnull(max(taskid),0)+1 from z_task";
		return new DbUtil(conn).queryForString(strSql);
	}

	public int saveToTemplate(String auditType, String strAuditName, String userid, String singleTemplateTaskId) throws Exception {
		int newType = new ProjectService(conn).copyProject(
													Integer.parseInt(this.projectId),
													Integer.parseInt(auditType),
													strAuditName,
													userid,"");

		System.out.println("新增模板：" + newType);

		//修复全路径
		new AuditTypeTemplateService(conn).repairTaskFullPath(String.valueOf(newType));

		//删除多余单体
		removeSingle(String.valueOf(newType),singleTemplateTaskId);

		return newType;
	}

	/**
	 * 删除单体
	 * @param typeId
	 * @throws Exception
	 */
	public void removeSingle(String typeId,String singleTemplateTaskId) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			//找出其余的没用的数据
			String sql = " select taskId from k_tasktemplate "
						+ " where parenttaskid in ( "
						+ " select taskid from k_tasktemplate "
						+ " where property='21'and typeId=? and parentTaskId='0') "
						+ " and taskId<>? "
						+ " and typeId=? ";

			ps = conn.prepareStatement(sql);
			ps.setString(1, typeId);
			ps.setString(2, singleTemplateTaskId);
			ps.setString(3, typeId);

			rs = ps.executeQuery();

			//删除其余单体下面的底稿
			while(rs.next()) {
				removeTask(typeId, rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 删除底稿
	 * @param typeId
	 * @param parentTaskId
	 * @throws Exception
	 */
	private void removeTask(String typeId, String parentTaskId) throws Exception {
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;

		//找出所有单体属性不为模板的任务编号
		String sql = " select taskId,isleaf from k_tasktemplate "
					+ " where fullpath like concat('%',?,'|%') "
					+ " and typeId=? "
					+ " order by orderid desc ";

		//删除任务
		String sql2 = " delete from k_tasktemplate where typeid=? and taskId=? ";

		try {

			AuditTypeTemplateService auditTypeTemplateService = new AuditTypeTemplateService(conn);

			ps = conn.prepareStatement(sql);
			ps.setString(1, parentTaskId);
			ps.setString(2, typeId);

			rs = ps.executeQuery();

			ps2 = conn.prepareStatement(sql2);
			ps2.setString(1, typeId);

			//逐张删除底稿
			while(rs.next()) {
				String taskId = rs.getString(1);
				if("1".equals(rs.getString(2))) {
					auditTypeTemplateService.removeTemplaeTask(typeId,taskId);
				} else {
					ps2.setString(2, taskId);
					ps2.execute();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(ps2);
		}
	}

	/**
	 * 合并报表单体建项
	 * @param typeId
	 * @return
	 * @throws Exception
	 */
	public int saveSingleProject(String XML) throws Exception {
		ASFuntion asf = new ASFuntion();
		PreparedStatement ps = null;

		String str = "";
		// 获取审计类型
		int typeId = Integer.parseInt(asf.getXMLData(XML, "AuditType"));
		// 生成项目编号
		int projectId = new ProjectService(conn).getProjectID(typeId);

		String databaseName = "asdb_" + asf.getXMLData(XML, "CustomerId") + ".";

		try {
			new DBConnect().changeDataBase(asf.getXMLData(XML, "CustomerId"),
					conn);

			/* 插入项目表 */
			str = "INSERT INTO "
					+ databaseName
					+ "z_Project(ProjectID,AuditDept,CustomerId,AccPackageID,AuditType,AuditPara,ProjectName,ProjectCreated,State,AuditPeople,Property,AuditTimeBegin,AuditTimeEnd,ProjectEnd,RealStartDate,RealEndDate,standbyname,systemId) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			ps = conn.prepareStatement(str);
			ps.setInt(1, projectId);
			ps.setString(2, asf.getXMLData(XML, "AuditDept"));
			ps.setString(3, asf.getXMLData(XML, "CustomerId"));
			ps.setString(4, asf.getXMLData(XML, "AccPackageID"));
			ps.setInt(5, Integer.parseInt(asf.getXMLData(XML, "AuditType")));
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
			ps.setInt(18, 99999999);	//单体项目

			ps.execute();
			ps.close();

			try {
				ManuFileService manuFileService = new ManuFileService(conn);
				//删除底稿目录
				manuFileService.deleteDirByProjectID(String.valueOf(projectId));
			} catch (Exception e) {
				e.printStackTrace();
			}

			this.projectId = String.valueOf(projectId);
			createSingle("0",asf.getXMLData(XML, "ProjectName"),String.valueOf(typeId));

			//修复全路径
			try {
				new TaskService(conn,this.projectId).repairTaskFullPath();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

		return projectId;
	}

	public static void main(String[] args) {
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			//new ReportProjectService(conn, "20081244", "1").saveToTemplate("-1","测试", "admin");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
	}

}
