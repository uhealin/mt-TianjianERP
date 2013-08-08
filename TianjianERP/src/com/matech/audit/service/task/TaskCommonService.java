package com.matech.audit.service.task;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.auditPeople.AuditPeopleService;
import com.matech.audit.service.audittypetemplate.AuditTypeTemplateService;
import com.matech.audit.service.audittypetemplate.model.AuditTemplateTask;
import com.matech.audit.service.datamanage.DataZip;
import com.matech.audit.service.fileupload.MyFileUpload;
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.task.model.Task;
import com.matech.audit.service.taskAttach.TaskAttachService;
import com.matech.audit.service.taskAttach.model.TaskAttachTable;
import com.matech.audit.service.user.UserService;
import com.matech.audit.service.user.model.User;
import com.matech.audit.service.userdef.UserdefService;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.sys.UTILSysProperty;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.pub.util.ZipUtil;

/**
 * <p>Title: 底稿任务公共类</p>
 * <p>Description:提供获取最大底稿索引号,检查索引号是否唯一,底稿编制审核等..</p>
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
 * 2007-6-28
 */
public class TaskCommonService {
	/**
	 * 数据库连接
	 */
	private Connection conn = null;

	/**
	 * 项目编号
	 */
	private String projectId = null;

	/**
	 * 必做状态
	 */
	public static final int TASK_STATE_CODE_MUST = 1;

	/**
	 * 关注状态
	 */
	public static final int TASK_STATE_CODE_ATTENTION = 2;

	/**
	 * 有数据状态
	 */
	public static final int TASK_STATE_CODE_DATA = 3;

	/**
	 * 已处理保存状态
	 */
	public static final int TASK_STATE_CODE_SAVED = 4;

	public static final String TASK_STATE_COLOR_MUST = "#B33232";	//必做
	public static final String TASK_STATE_COLOR_ATTENTION = "#D97125"; //关注
	public static final String TASK_STATE_COLOR_DATA = "#1A76B7"; //有数据
	public static final String TASK_STATE_COLOR_SAVED = "#66A72D"; //已处理保存

	/**
	 * 构造方法
	 * @param conn 数据库连接
	 * @param projectId 项目编号
	 * @throws Exception
	 */
	public TaskCommonService(Connection conn, String projectId) throws Exception {
		DbUtil.checkConn(conn);
		this.conn = conn;

		if ("".equals(projectId) || projectId == null) {
			throw new Exception("项目Id不能为空!");
		}

		this.projectId = projectId;
	}
 
	/**
	 * 构造方法,注意:该方法只供底稿模板使用
	 * @param conn 数据库连接
	 * @throws Exception
	 */
	public TaskCommonService(Connection conn) throws Exception {
		DbUtil.checkConn(conn);
		this.conn = conn;
	}

	/**
	 * 根据typeId和taskId获得底稿名称,注意:该方法只供底稿模板使用
	 * @param typeId
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public String getFileNameByTypeIdAndTaskId(String typeId,String taskId) throws Exception {
		String taskName = "";
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = "select taskName from k_tasktemplate "
							+ " where typeId = ? "
							+ " and taskid = ?";

			ps = conn.prepareStatement(sql);

			ps.setString(1, typeId);
			ps.setString(2, taskId);

			rs = ps.executeQuery();

			if (rs.next()) {
				taskName = rs.getString(1);
			}
			return taskName;

		} catch (Exception e) {

			Debug.print(Debug.iError, "访问失败", e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 改变底稿状态
	 * @param taskId
	 * @param stateCode
	 * @param state
	 * @throws Exception
	 */
	public void setTaskState(String taskId, int stateCode, boolean state) throws Exception {

		//首先无条件把状态去掉
		String sql = " update z_task set ismust=replace(ismust,?,'') "
					+ " where taskid=? "
					+ " and projectId=? ";

		//切换数据库
		new DBConnect().changeDataBaseByProjectid(this.projectId, this.conn);
		DbUtil dbUtil = new DbUtil(conn);
		Object[] args = new Object[] {String.valueOf(stateCode),
										taskId,
										this.projectId };

		dbUtil.executeUpdate(sql, args);

		//如果对应的状态为true,则在后面追加
		if(state) {
			sql = " update z_task set ismust=concat(ifnull(ismust,''),?) "
		   		+ " where taskid=? "
		   		+ " and projectId=? ";

			dbUtil.executeUpdate(sql, args);
		}
	}

	/**
	 * 返回底稿对应属性的状态
	 * @param taskId
	 * @param stateCode
	 * @return
	 * @throws Exception
	 */
	public boolean getTaskState(String taskId, int stateCode) throws Exception {
		String sql = " select ismust from z_task "
					+ " where taskid=? "
					+ " and projectId=? ";

		new DBConnect().changeDataBaseByProjectid(this.projectId, this.conn);
		DbUtil dbUtil = new DbUtil(conn);
		Object[] args = new Object[] {
							taskId,
							this.projectId
						};

		String result = "" + dbUtil.queryForString(sql, args);

		return result.indexOf(String.valueOf(stateCode)) > -1;
	}


	/**
	 * 检查用户是否有审核权限
	 * 原方法：checkRole(String projectid, String userid);
	 *
	 * @param userLoginId 用户登陆名
	 * @return
	 * @throws Exception
	 */
	public int checkRoleByUserLoginId(String userLoginId) throws Exception {
		DbUtil dbUtil = new DbUtil(conn);

		String sql = "Select IsAudit From asdb.z_auditPeople "
				+ " where ProjectId =  ?" + " and userid = "
				+ "(Select Id From asdb.k_User Where loginid= ? )";

		Object[] params = new Object[] { this.projectId, userLoginId };

		return dbUtil.queryForInt(sql, params);
	}


	/**
	 * 从数据库中取得最大的TaskCode
	 * 原方法：getMaxTaskCode(String ptaskId, String projectId);
	 *
	 * 前记：
	 * 1、一般情况下是根据父任务编号、当前项目编号来获得当前级别的最大任务编码，如果当前级别的
	 *   的记录集为空，意味着该父任务编号下没有子任务结点，则取父任务编码。
	 * 2、父任务编号为0，意味着它是最顶层的任务结点，则取与其同级的任务结点集中最大的任务编码。
	 *
	 * @param parentTaskId String
	 * @return String
	 */
	public String getMaxTaskCodeByParentTaskId(String parentTaskId) throws Exception{
		String maxTaskCode = "";
		String sql = "";

		if (parentTaskId == null
				|| projectId == null
				|| "".equals(parentTaskId)
				|| "".equals(projectId) ) {

			return maxTaskCode;
		}

		new DBConnect().changeDataBaseByProjectid(this.projectId, conn);

		DbUtil dbUtil = new DbUtil(conn);
		//构造数据库查询语句
		sql = "select distinct a.taskcode from z_task a, z_task b "
			+ " where a.projectid = ? "
			+ " and b.projectid = ? "
			+ " and b.taskid= ? "
			+ " and a.Level0=b.Level0+1 "
			+ " and a.parenttaskid=b.taskid "
			+ " order by a.orderid desc";

		Object[] params = new Object[] { this.projectId, this.projectId, parentTaskId };

		//如果parentTaskId为0
		if ("0".equals(parentTaskId)) {
			sql = "select taskcode from z_task "
				+ " where projectid= ? "
				+ " and parenttaskid=0 "
				+ " order by orderid desc";
			params = new Object[] { this.projectId };

			//执行数据库的检索
			maxTaskCode = dbUtil.queryForString(sql,params);

			if(maxTaskCode == null) {
				maxTaskCode = "00";
			}

		} else {
			maxTaskCode = dbUtil.queryForString(sql,params);

			if(maxTaskCode == null) {
				sql = "select taskCode from z_task where "
					+ " projectid = ? "
					+ " and taskid = ? ";
				params = new Object[] { this.projectId, parentTaskId};
				maxTaskCode = dbUtil.queryForString(sql, params);

				if(maxTaskCode != null) {
					maxTaskCode = maxTaskCode + "-0";
				}
			}

		}
		return maxTaskCode;

	}

	/**
	 * 根据parentTaskCode从数据库中取得最大的TaskCode
	 * 原方法：getMaxTaskCode1(String ptaskCode, String projectId);
	 *
	 * 前记：
	 * 1、一般情况下是根据父任务编号、当前项目编号来获得当前级别的最大任务编码，如果当前级别的
	 *   的记录集为空，意味着该父任务编号下没有子任务结点，则取父任务编码。
	 * 2、父任务编号为0，意味着它是最顶层的任务结点，则取与其同级的任务结点集中最大的任务编码。
	 * 和getMaxTaskCode函数的区别就是，一个是根据taskid来获得，还有一个是根据taskcode来获得
	 *
	 * @param parentTaskCode 底稿索引号
	 * @return parentTaskCode下面最大的底稿索引号
	 */
	public String getMaxTaskCodeByParentTaskCode(String parentTaskCode) throws Exception {
		String maxTaskCode = "";
		String sql = "";

		if (parentTaskCode == null
				|| projectId == null
				|| "".equals(parentTaskCode)
				|| "".equals(projectId) ) {

			return maxTaskCode;
		}

		new DBConnect().changeDataBaseByProjectid(this.projectId, conn);

		DbUtil dbUtil = new DbUtil(conn);
		//构造数据库查询语句
		sql = "select distinct a.taskcode from z_task a, z_task b "
			+ " where a.projectid = ? "
			+ " and b.projectid = ? "
			+ " and b.taskcode = ? "
			+ " and a.Level0=b.Level0+1 "
			+ " and a.parenttaskid=b.taskid "
			+ " order by a.orderid desc";

		Object[] params = new Object[] { this.projectId, this.projectId, parentTaskCode };

		//如果parentTaskCode为0
		if ("0".equals(parentTaskCode)) {
			sql = "select taskcode from z_task "
				+ " where projectid= ? "
				+ " and parenttaskid=0 "
				+ " order by orderid desc";
			params = new Object[] { this.projectId };

			//执行数据库的检索
			maxTaskCode = dbUtil.queryForString(sql,params);

			if(maxTaskCode == null) {
				maxTaskCode = "00";
			}

		} else {
			maxTaskCode = dbUtil.queryForString(sql,params);

			if(maxTaskCode == null) {
				sql = "select taskCode from z_task where "
					+ " projectid = ? "
					+ " and taskcode = ? ";

				params = new Object[] { this.projectId, parentTaskCode};
				maxTaskCode = dbUtil.queryForString(sql, params);

				if(maxTaskCode != null) {
					maxTaskCode = maxTaskCode + "-0";
				}
			}

		}
		return maxTaskCode;
	}

	/**
	 * 根据父底稿编号parentTaskId和是否叶子isleaf获取最大的taskcode
	 * 原方法：getMaxTaskCodeByIsLeaf(String ptaskId, String projectId, int isleaf);
	 *
	 * @param parentTaskId  底稿编号
	 * @param isleaf 是否叶子
	 * @return
	 */
	public String getMaxTaskCodeByParentTaskId(String parentTaskId, int isleaf) throws Exception {
		String maxTaskCode = "";
		String sql = "";

		if (parentTaskId == null
				|| projectId == null
				|| "".equals(parentTaskId)
				|| "".equals(projectId) ) {

			return maxTaskCode;
		}

		new DBConnect().changeDataBaseByProjectid(this.projectId, conn);

		DbUtil dbUtil = new DbUtil(conn);
		//构造数据库查询语句
		sql = "select distinct a.taskcode from z_task a, z_task b "
			+ " where a.projectid = ? "
			+ " and b.projectid = ? "
			+ " and b.taskid= ? "
			+ " and a.isleaf = ? "
			+ " and a.parenttaskid=b.taskid "
			+ " order by a.orderid desc";

		Object[] params = new Object[] {
				this.projectId,
				this.projectId,
				parentTaskId,
				new Integer(isleaf)
		};

		//如果parentTaskId为0
		if ("0".equals(parentTaskId)) {
			sql = "select taskcode from z_task "
				+ " where projectid= ? "
				+ " and parenttaskid=0 "
				+ " order by orderid desc";
			params = new Object[] { this.projectId };

			//执行数据库的检索
			maxTaskCode = dbUtil.queryForString(sql,params);

			if(maxTaskCode == null) {
				maxTaskCode = "00";
			}

		} else {
			maxTaskCode = dbUtil.queryForString(sql,params);

			if(maxTaskCode == null) {
				sql = "select taskCode from z_task where "
					+ " projectid = ? "
					+ " and taskid = ? ";
				params = new Object[] { this.projectId, parentTaskId};
				maxTaskCode = dbUtil.queryForString(sql, params);

				if(maxTaskCode != null) {
					maxTaskCode = maxTaskCode + "-0";
				}
			}

		}
		return maxTaskCode;
	}

	/**
	 * 检查底稿索引号taskcode是否唯一
	 * @param taskCode 索引号
	 * @return
	 * @throws Exception
	 */
	public String checkIsOnlyByTaskCode2(String taskCode) throws Exception {
		new DBConnect().changeDataBaseByProjectid(this.projectId, conn);
		DbUtil dbUtil = new DbUtil(conn);

		String sql = "select taskname from z_task "
					+ " where projectid=? "
					+ " and taskcode= ? ";

		Object[] params = new Object[] { this.projectId, taskCode};

		return dbUtil.queryForString(sql, params);
	}

	/**
	 * 检查底稿索引号taskcode是否唯一,只针对底稿
	 * @param taskCode 索引号
	 * @return
	 * @throws Exception
	 */
	public String checkIsOnlyByTaskCode(String taskCode) throws Exception {
		new DBConnect().changeDataBaseByProjectid(this.projectId, conn);
		DbUtil dbUtil = new DbUtil(conn);

		String sql = "select taskname from z_task "
					+ " where projectid=? "
					+ " and taskcode= ? "
					+ " and isleaf=1 ";

		Object[] params = new Object[] { this.projectId, taskCode};

		return dbUtil.queryForString(sql, params);
	}
	
	/**
	 * 检查底稿索引号taskcode是否唯一
	 * @param taskCode 索引号
	 * @return
	 * @throws Exception
	 */
	public String checkIsOnlyByTaskCode(String taskCode, String isleaf) throws Exception {
		new DBConnect().changeDataBaseByProjectid(this.projectId, conn);
		DbUtil dbUtil = new DbUtil(conn);

		String sql = "select taskname from z_task "
					+ " where projectid=? "
					+ " and taskcode= ? "
					+ " and isleaf=? ";

		Object[] params = new Object[] { this.projectId, taskCode, isleaf};

		return dbUtil.queryForString(sql, params);
	}

	/**
	 * 提供底稿编制和审核服务
	 * @param userId	用户编号,例如："19"
	 * @param taskId	底稿任务编号,例如："10031"
	 * @param type	类型,例如：1
	 * 	<ul>
	 * 		<li>type = 1：编制</li>
	 * 		<li>type = 2：二级复核</li>
	 * 		<li>type = 3：三级复核</li>
	 * 		<li>type = 4：退回</li>
	 * 		<li>type = 5：一级复核</li>
	 * 	</ul>
	 *
	 * @return 编制或审核结果
	 * 	<ul>
	 * 		<li>"ok"：编制或审核更新成功,成功</li>
	 * 		<li>"fail"：编制或审核更新失败,失败!!</li>
	 * 		<li>"samepeople"：编制人跟审核人是同一人,失败!!</li>
	 * 		<li>"nopopedom"：无权复核,失败!!</li>
	 * 	</ul>
	 * @throws Exception
	 */
	public String auditing(String userId, String taskId, int type,String levelBack,String curDate) throws Exception {
		new DBConnect().changeDataBaseByProjectid(this.projectId, conn);

		//取得用户信息
		User user = new UserService(conn).getUser(userId, "id");
		if(user == null) {
			throw new Exception("用户编号[userId]为：" + userId + "的用户不存在,系统错误!!");
		}

		System.out.println("userid==============================" + user.getId());
		
		//取得底稿任务信息
		Task task = new TaskService(this.conn, this.projectId).getTaskByTaskId(taskId);
		
		String result;

		if(task == null) {
			throw new Exception("底稿编号[taskId]为：" + taskId + "的底稿不存在,系统错误!!");
		}

		switch(type) {
			case 1:
				//编制
				task.setDate1(curDate) ;
				result = auditingByUser1(task,user);
				break;

			case 2:
				//二级复核
				result = auditingByUser2(task,user);
				break;

			case 3:
				//三级复核
				result = auditingByUser3(task,user);
				break;

			case 4:
				//退回
				result = auditingByUser4(task,user,levelBack);
				break;

			case 5:
				//一级复核
				task.setDate5(curDate) ;
				result = auditingByUser5(task,user);
				break;

			default:
				result = "fail";
				break;
		}

		return result;
	}


	/**
	 * 编制底稿
	 * @param task
	 * @param user
	 * @return
	 * @throws Exception
	 */
	private String auditingByUser1(Task task,User user) throws Exception {
		new DBConnect().changeDataBaseByProjectid(this.projectId, conn);
		UserdefService udf = new UserdefService(conn);
		DbUtil dbUtil = new DbUtil(conn);

		ASFuntion asf = new ASFuntion();
		String user0 = asf.showNull(task.getUser0());
		
		String auditProperty = task.getAuditproperty();


		
		Object[] params1 = null;
		String sql1 = "";
		int result = 0;
		
		boolean isAudit = false;
		
		if(auditProperty!=null&&!"".equals(auditProperty)) {
			if(auditProperty.indexOf("1")==0) {
				isAudit = new AuditPeopleService(this.conn, this.projectId).hasAudit(user.getId());
				if(isAudit) {
					sql1 =  " Update z_Task Set User1 = ?, Date1 = NOW(), User5 = ?, Date5 = NOW(), User4 = Null, Date4 = null "
								+ " Where ProjectID = ? "
								+ " And TaskID = ? ";
					params1 = new Object[] {
							user.getId(),
							user.getId(),
							this.projectId,
							task.getTaskId()
					};
					result = dbUtil.executeUpdate(sql1, params1);
				} else {
					return "nopopedom";
				}
			} else if(auditProperty.indexOf("2")==0) {
				//二审人、独立审核人
				isAudit = udf.hasAuthority2(user.getId(), this.projectId);
				if(isAudit) {
					sql1 =  " Update z_Task Set User2 = ?, Date2 = NOW(), User1 = ?, Date1 = NOW(), User4 = Null, Date4 = null "
						+ " Where ProjectID = ? "
						+ " And TaskID = ? ";
					params1 = new Object[] {
							user.getId(),
							user.getId(),
							this.projectId,
							task.getTaskId()
					};
					result = dbUtil.executeUpdate(sql1, params1);
				} else {
					return "nopopedom2";
				}
			} else if(auditProperty.indexOf("3")==0) {
				//三审人
				isAudit = udf.hasAuthority3(user.getId(), this.projectId);
				if(isAudit) {
					sql1 =  " Update z_Task Set User3 = ?, Date3 = NOW(), User1 = ?, Date1 = NOW(), User4 = Null, Date4 = null "
						+ " Where ProjectID = ? "
						+ " And TaskID = ? ";
					params1 = new Object[] {
							user.getId(),
							user.getId(),
							this.projectId,
							task.getTaskId()
					};
					result = dbUtil.executeUpdate(sql1, params1);
				} else {
					return "nopopedom3";
				}
			}
			if (result > 0) {
				return "ok";
			} else {//编制失败
				return "fail";
			}

		} else {
		
			//当前用户有权编制底稿或不进行底稿分工
			if ("".equals(user0) || user0.equals(user.getId())) {
				
				String sql = "Update z_Task Set User1 = ?, Date1 = ?, User4 = Null, Date4 = null "
							+ " Where ProjectID = ? "
							+ " And TaskID = ? ";
				
				String date1 = task.getDate1() ;
				if("".equals(date1) || date1 == null) date1 =new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
	
				Object[] params = new Object[] {
						user.getId(),
						date1,
						this.projectId,
						task.getTaskId()
				};
	
				if (dbUtil.executeUpdate(sql, params) > 0) {//编制完成
	
					//将用户名追加到审计程序表中
					sql = " update z_procedure set Executor = concat(Executor,? )"
							+ " where manuscript like ? "
							+ " and projectid = ? "
							+ " and Executor not like ? ";
	
					params = new Object[] {
							user.getName() + ",",
							"%" + task.getTaskCode() + ",%",
							this.projectId,
							"%" + user.getName() + ",%"
					};
	
					dbUtil.executeUpdate(sql, params);
	
					return "ok";
				} else {//编制失败
					return "fail";
				}
	
			} else {//当前用户无权编制底稿
				return "nopopedom";
			}
		}
	}
	
	

	/**
	 * 三级审核
	 * @param task
	 * @param user
	 * @return
	 * @throws Exception
	 */
	private String auditingByUser3(Task task,User user) throws Exception {
		new DBConnect().changeDataBaseByProjectid(this.projectId, conn);

		//判断该用户是否有权复核
		//boolean isAudit = new AuditPeopleService(this.conn, this.projectId).hasAudit(user.getId());
		boolean hasAuthority = new UserdefService(conn).hasAuthority3(user.getId(), this.projectId);
		
		DbUtil dbUtil = new DbUtil(conn);

		if (hasAuthority) {//有权复核
			//判断是否同一人
			if (!isSame(task.getTaskId(),user.getId(),3)) {
				String sql = "Update z_Task Set User3 = ?, Date3 = NOW(), User4 = Null, Date4 = null "
						+ " Where ProjectID = ? "
						+ " And TaskID = ? ";

				Object[] params = new Object[] {
						user.getId(),
						this.projectId,
						task.getTaskId()
				};

				//执行三级复核更新操作
				if (dbUtil.executeUpdate(sql, params) > 0) {
					//检查项目是否完成
					sql = "Select Count(*) From z_Task "
						+ " Where ProjectID = ? "
						+ " And IsLeaf = 1 "
						+ " And (User3 Is Null Or User3 = '')";
					params = new Object[] { this.projectId,};

					if (dbUtil.queryForInt(sql, params) <= 0) {
						sql = "Update z_Project Set State = 2 Where ProjectID = ? ";
						params = new Object[] { this.projectId,};
						dbUtil.executeUpdate(sql, params);
					}

					try {
						//底稿3级复核后自动完成程序
						StringBuffer sb = new StringBuffer();
						sb.append("  update z_procedure a, ");
						sb.append("  ( select a.autoid,group_concat(b.taskcode),group_concat(b.sheettaskcode) from z_procedure a ");
						sb.append("  inner join (  ");
						sb.append("  select a.user3,a.projectid,a.taskcode,b.sheettaskcode ");
						sb.append("  from z_task a left join z_sheettask b ");
						sb.append("  on a.taskid=b.taskid ");
						sb.append("  and a.projectid=b.projectId ");
						sb.append("  where a.projectid=? ");
						sb.append("  and a.isleaf=1 ");
						sb.append("  ) b  ");
						sb.append("  on a.projectid=b.projectid ");
						sb.append("  and (a.Manuscript like concat('%,',b.taskcode,',%') or a.Manuscript like concat('%,',b.sheettaskcode,',%')) ");
						sb.append("  where a.projectid= ? ");
						sb.append("  and b.projectid= ? ");
						sb.append("  and a.state != '不适用'  ");
						sb.append("  and b.user3 is not null and b.user3 != '' ");
						sb.append("  group by a.manuscript ");
						sb.append("  having (LENGTH(group_concat(b.taskcode)) + 2 = length(a.manuscript) or LENGTH(group_concat(b.sheettaskcode)) + 2 = length(a.manuscript)) ");
						sb.append("  ) b set a.State='已完成' where a.autoid=b.autoid ");

						params = new Object[] { this.projectId,this.projectId, this.projectId};
						dbUtil.executeUpdate( sb.toString(),params);
					} catch (Exception e) {
						System.out.println("底稿3级复核后自动完成程序失败:" + e.getMessage());
					}


					return "ok";
				} else {
					return "fail";
				}
			} else {
				return "samepeople";
			}
		} else {
			return "nopopedom";
		}
	}

	/**
	 * 二级复核
	 * @param task
	 * @param user
	 * @return
	 * @throws Exception
	 */
	private String auditingByUser2(Task task,User user) throws Exception {
		//判断该用户是否有权复核
		//boolean isAudit = new AuditPeopleService(this.conn, this.projectId).hasAudit(user.getId());
		
		boolean hasAuthority = new UserdefService(conn).hasAuthority2(user.getId(), this.projectId);

		new DBConnect().changeDataBaseByProjectid(this.projectId, conn);
		DbUtil dbUtil = new DbUtil(conn);

		if (hasAuthority) {//有权复核且项目进去二审
			//判断是否同一人
			if (!isSame(task.getTaskId(),user.getId(),2)) {
				String sql = "Update z_Task Set User2 = ?, Date2 = NOW(), User4 = Null, Date4 = null "
							+ " Where ProjectID = ? "
							+ " And TaskID = ? ";

				Object[] params = new Object[] {
						user.getId(),
						this.projectId,
						task.getTaskId()
				};

				//执行二级复核更新操作
				if (dbUtil.executeUpdate(sql, params) > 0) {
					return "ok";
				} else {
					return "fail";
				}
			} else {
				return "samepeople";
			}
		} else {
			return "nopopedom";
		}
	}

	/**
	 * 退回底稿
	 * @param task
	 * @param user
	 * @return
	 * @throws Exception
	 */
	private String auditingByUser4(Task task,User user,String levelBack) throws Exception {
		//判断该用户是否有权复核
		boolean isAudit = new AuditPeopleService(this.conn, this.projectId).hasAudit(user.getId());
		
		UserdefService udf = new UserdefService(conn);

		boolean hasAuthority2 = udf.hasAuthority2(user.getId(), this.projectId);
		
		boolean hasAuthority3 = udf.hasAuthority3(user.getId(), this.projectId);
		
		new DBConnect().changeDataBaseByProjectid(this.projectId, conn);
		DbUtil dbUtil = new DbUtil(conn);
		String user1=task.getUser1();

		boolean flag = false;
		
		if("1".equals(levelBack)) {
			flag = isAudit;
		} else if("2".equals(levelBack)) {
			flag = hasAuthority2;
		} else if("3".equals(levelBack)) {
			flag = hasAuthority3;
		}
		
		ProjectService ps = new ProjectService(conn) ;
		if("1".equals(ps.getProjectById(this.projectId).getState())) {
			//项目进行中，一审也可以退回
			flag = isAudit;
		}
		
		if (flag) {//有权复核
			//编制和审核人是否一致,如果一致的话不允许审核
			if (!isSame(task.getTaskId(),user.getId())) {
				String sql = "Update z_Task Set "
							+ " User1 = null, Date1 = null, "
							+ " User2 = null, Date2 = null, "
							+ " User3 = null, Date3 = null, "
							+ " User4 =?, Date4 = NOW(), "
							+ " User5 = null, Date5 = null, "
							+ " levelBack = ?"
							+ " where ProjectID = ? "
							+ " and TaskID = ? ";
				
				Object[] params = new Object[] {
						task.getUser1(),
						levelBack,
						this.projectId,
						task.getTaskId()
				};

				//执行二级复核更新操作
				if (dbUtil.executeUpdate(sql, params) > 0) {
					return "ok|"+user1;
				} else {
					return "fail";
				}
			} else {
				return "samepeople";
			}
		} else {
			return "nopopedom";
		}
	}

	/**
	 * 一级复核
	 * @param task
	 * @param user
	 * @return
	 * @throws Exception
	 */
	private String auditingByUser5(Task task,User user) throws Exception {
		//判断该用户是否有权复核
		boolean isAudit = new AuditPeopleService(this.conn, this.projectId).hasAudit(user.getId());

		new DBConnect().changeDataBaseByProjectid(this.projectId, conn);
		DbUtil dbUtil = new DbUtil(conn);
		
		//判断一级复核人是否可以复核自己底稿
		String mytip = "" + UTILSysProperty.SysProperty.get("一级复核人可以复核自己底稿");
		if (isAudit) {//有权复核
			//判断是否同一人
			if (!isSame(task.getTaskId(),user.getId(),5) || "是".equals(mytip)) {
				String sql = "Update z_Task Set User5 = ?, Date5 = ?, User4 = Null, Date4 = null "
							+ " Where ProjectID = ? "
							+ " And TaskID = ? ";
				
				String date5 = task.getDate5() ;
				if("".equals(date5) || date5 == null) date5 =new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
				Object[] params = new Object[] {
						user.getId(),
						date5,
						this.projectId,
						task.getTaskId()
				};

				//执行二级复核更新操作
				if (dbUtil.executeUpdate(sql, params) > 0) {
					return "ok";
				} else {
					return "fail";
				}
			} else {
				return "samepeople";
			}
		} else {
			return "nopopedom";
		}
	}

	/**
	 * 判断编制人和复核人是否为同一人
	 * @param taskId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public boolean isSame(String taskId, String userId) throws Exception {
		new DBConnect().changeDataBaseByProjectid(this.projectId, conn);
		DbUtil dbUtil = new DbUtil(conn);

		//判别编制人和复核人是否为同一人
		String sql = "Select 1 From z_Task "
					+ " Where ProjectID = ? "
					+ " And TaskID = ? "
					+ " And User1 = ? ";

		Object[] params = new Object[] {
				this.projectId,
				taskId,
				userId
		};

		//检查该用户是否为编制人
		return dbUtil.queryForString(sql, params) != null;
	}

	/**
	 * 判断编制人和复核人是否为同一人
	 * @param taskId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public boolean isSame(String taskId, String userId, int type) throws Exception {
		new DBConnect().changeDataBaseByProjectid(this.projectId, conn);
		DbUtil dbUtil = new DbUtil(conn);

		Object[] params;

		//判别编制人和复核人是否为同一人
		String sql = "Select 1 From z_Task "
					+ " Where ProjectID = ? "
					+ " And TaskID = ? "
					+ " And (User1 = ? ";

		//根据不同类型复核,加不同的sql条件
		switch(type) {
			case 2:
				//二级复核
				sql += " or user5=? ";
				params = new Object[] {
						this.projectId,
						taskId,
						userId,
						userId
				};
				break;

			case 3:
				//三级复核
				sql += " or user5=? or user2=? ";
				params = new Object[] {
						this.projectId,
						taskId,
						userId,
						userId,
						userId
				};
				break;

			case 5:
				//一级复核
				params = new Object[] {
						this.projectId,
						taskId,
						userId
				};
				break;

			default:
				params = new Object[] {
					this.projectId,
					taskId,
					userId
				};
				break;
		}

		sql += " )";



		//检查该用户是否为编制人
		return dbUtil.queryForString(sql, params) != null;
	}

	public Set getTaskInfo(String taskId) throws Exception {

		Set set = new HashSet();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			new DBConnect().changeDataBaseByProjectid(this.projectId, conn);

			StringBuffer sql = new StringBuffer();
			sql.append(" select  ");
			sql.append(" u0.name as User0, ");
			sql.append(" u1.name as User1,date1, ");
			sql.append(" u5.name as User5,date5, ");
			sql.append(" u2.name as User2,date2, ");
			sql.append(" u3.name as User3,date3, ");
			sql.append(" u4.name as User4,date4 ");
			sql.append(" from z_Task t");
			sql.append(" left join k_user u0 on t.user0 = u0.id ");
			sql.append(" left join k_user u1 on t.user1 = u1.id ");
			sql.append(" left join k_user u2 on t.user2 = u2.id ");
			sql.append(" left join k_user u3 on t.user3 = u3.id ");
			sql.append(" left join k_user u4 on t.user4 = u4.id ");
			sql.append(" left join k_user u5 on t.user5 = u5.id ");
			sql.append(" where ProjectID = ? ");
			sql.append(" and taskid = ? ");

			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, this.projectId);
			ps.setString(2, taskId);

			rs = ps.executeQuery();

			ASFuntion asf = new ASFuntion();
			if(rs.next()) {
				set.add("编制人=" + asf.showNull(rs.getString("User1") ) );
				set.add("编制日期=" + asf.showNull(rs.getString("date1") ) );
				set.add("一级复核人=" + asf.showNull(rs.getString("User5") ) );
				set.add("一级复核日期=" + asf.showNull(rs.getString("date5") ) );
				set.add("二级复核人=" + asf.showNull(rs.getString("User2") ) );
				set.add("二级复核日期=" + asf.showNull(rs.getString("date2") ) );
				set.add("三级复核人=" + asf.showNull(rs.getString("User3") ) );
				set.add("三级复核日期=" + asf.showNull(rs.getString("date3") ) );
			}

			String[] adviceType = {"一级复核","编制","二级复核","三级复核"};

			sql = null;
			sql = new StringBuffer();
			sql.append(" select advice from ");
			sql.append(" z_auditadvice ");
			sql.append(" where projectid=? ");
			sql.append(" and taskid=? ");
			sql.append(" and adviceType=? order by adviceDate desc limit 1 ");


			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, this.projectId);
			ps.setString(2, taskId);

			for (int i = 0; i < adviceType.length; i++) {
				ps.setString(3, adviceType[i]);
				rs = ps.executeQuery();
				String tempAdvice = "";
				if(rs.next()) {
					tempAdvice = asf.showNull(rs.getString(1));
				}

				set.add(adviceType[i] + "意见=" + tempAdvice );
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return set;
	}

	public Map getPrevTaskInfo(String orderid) throws Exception {

		Map taskMap = new HashMap();

		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";

		try {
			new DBConnect().changeDataBaseByProjectid(this.projectId, conn);

			sql = "select taskid,taskcode,taskname,orderid,manuid from z_task "
				+" where projectid='"+this.projectId+"' and orderid < '"+orderid+"' and isleaf=1 order by orderid desc limit 1 ";
			System.out.println("zyq1="+sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			if(rs.next()) {
				taskMap.put("taskid",rs.getString(1) );
				taskMap.put("taskcode",rs.getString(2) );
				taskMap.put("taskname",rs.getString(3));
				taskMap.put("orderid",rs.getString(4));
				taskMap.put("manuid",rs.getString(5));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return taskMap;
	}

	public Map getNextTaskInfo(String orderid) throws Exception {

		Map taskMap = new HashMap();

		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";

		try {
			new DBConnect().changeDataBaseByProjectid(this.projectId, conn);

			sql = "select taskid,taskcode,taskname,orderid,manuid from z_task "
				+" where projectid='"+this.projectId+"' and orderid > '"+orderid+"' and isleaf=1 order by orderid limit 1 ";
			System.out.println("zyq2="+sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			if(rs.next()) {
				taskMap.put("taskid",rs.getString(1) );
				taskMap.put("taskcode",rs.getString(2) );
				taskMap.put("taskname",rs.getString(3));
				taskMap.put("orderid",rs.getString(4));
				taskMap.put("manuid",rs.getString(5));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return taskMap;
	}

	/**
	 * 更新底稿分工人
	 * @param userId
	 * @param taskIds
	 * @throws Exception
	 */
	public void updateUser0(String userId, String taskIds) throws Exception {
		String sql = " update z_task set user0=? "
				   + " where projectId=? "
				   + " and (taskId in(" + taskIds + ") or parenttaskId in(" + taskIds + "))";

		new DBConnect().changeDataBaseByProjectid(this.projectId, this.conn);
		DbUtil dbUtil = new DbUtil(conn);
		Object[] args = new Object[] {
							userId,
							this.projectId
						};

		dbUtil.executeUpdate(sql, args);
	}
	
	/**
	 * 更新底稿分工人
	 * @param userId
	 * @param taskIds
	 * @throws Exception
	 */
	public List getTaskUser0(String userId) throws Exception {
		List list = new ArrayList();
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" select a.taskid,a.taskcode,a.taskname ")
				.append(" from z_task a ")
				.append(" left join ")
				.append("	(select parenttaskid from z_task ")
				.append(" 		where projectId=? ")
				.append(" 		and isleaf='0'  ")
				.append("		group by parenttaskid ")
				.append("	) b  ")
				.append("	on a.taskid=b.parenttaskid  ")
				.append("	where a.projectId=? ")
				.append("	and a.isleaf='0'  ")
				.append("	and user0=? ")
				.append("	and b.parenttaskid is null ")
				.append("	order by orderid,taskcode ");

			new DBConnect().changeDataBaseByProjectid(this.projectId, this.conn);
			
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, projectId);
			ps.setString(2, projectId);
			ps.setString(3, userId);
			
			rs = ps.executeQuery();
			
			Task task = null;
			while(rs.next()) {
				task = new Task();
				task.setTaskId(rs.getString(1));
				task.setTaskCode(rs.getString(2));
				task.setTaskName(rs.getString(3));	
				
				list.add(task);
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
	 * 返回对应科目底稿编号
	 * @return
	 * @throws Exception
	 */
	public String getDydgTaskIds() throws Exception {
		StringBuffer strSql = new StringBuffer();
		strSql.append(" select ifnull(group_concat(subjectfullname),-1) from ( ");
		strSql.append(" 		select concat(\"'\",subjectfullname1,\"'\") as subjectfullname ");
		strSql.append(" 		from c_account a,z_project b  ");
		strSql.append(" 		where b.projectId=?  ");
		strSql.append(" 		and a.accpackageid = b.accpackageid  ");
		strSql.append(" 		and a.submonth=1   ");
		strSql.append(" 		and a.level1=1 ");
		strSql.append(" 		union   ");
		strSql.append(" 		select concat(\"'\",subjectfullname2,\"'\") as subjectfullname  ");
		strSql.append(" 		from c_account a,z_project b   ");
		strSql.append(" 		where b.projectId=?  ");
		strSql.append(" 		and a.accpackageid = b.accpackageid  ");
		strSql.append(" 		and a.submonth=1   ");
		strSql.append(" 		and a.level1=1 ");
		strSql.append(" 		union ");
		strSql.append(" 		select concat(\"'\",subjectfullname,\"'\") as subjectfullname ");
		strSql.append("			from z_usesubject ");
		strSql.append("			where projectid = ? ");
		strSql.append("			and level0=1  ");
		strSql.append(" ) a  ");

		new DBConnect().changeDataBaseByProjectid(this.projectId, this.conn);
		DbUtil dbUtil = new DbUtil(conn);
		Object[] args = new Object[] {
							this.projectId,
							this.projectId,
							this.projectId
						};

		String subjectNames =  dbUtil.queryForString(strSql.toString(), args);

		Debug.print("对应科目：" + subjectNames);

		strSql = new StringBuffer();

		strSql.append(" select ifnull(group_concat(taskid),-1) from z_task \n");
		strSql.append("	where projectid=? ");
		strSql.append(" and (subjectname='' or subjectname in(" + subjectNames + ") )");

		args = new Object[] {
					this.projectId
				};

		return dbUtil.queryForString(strSql.toString(), args);
	}

	/**
	 * 批量更新底稿状态
	 * @param state 更新的状态。例如 TaskCommonService.TASK_STATE_CODE_ATTENTION //关注状态
	 * @param taskIds 更新的taskId集合,用","分隔,前后不需要","。例如：  -1,134,545,32,342
	 * @return
	 * @throws Exception
	 */
	public int updateTaskState(int state, String taskIds) throws Exception {
		new DBConnect().changeDataBaseByProjectid(this.projectId, this.conn);
		DbUtil dbUtil = new DbUtil(conn);

		Object[] args = new Object[] {
							String.valueOf(state),
							this.projectId
						};

		//将原有的状态置空
		String sql = " update z_task set ismust=replace(ismust,?,'') "
				   + " where projectId=? "
				   + " and taskId in(" + taskIds + " ) ";

		dbUtil.executeUpdate(sql, args);

		//批量更新
		sql = " update z_task set ismust=concat(ifnull(ismust,''),?) "
			+ " where projectId=? "
			+ " and taskId in(" + taskIds + " ) ";

		return dbUtil.executeUpdate(sql, args);
	}

	/**
	 * 批量删除底稿状态
	 * @param state 更新的状态。例如 TaskCommonService.TASK_STATE_CODE_ATTENTION //关注状态
	 * @param taskIds 更新的taskId集合,用","分隔,前后不需要","。例如：  -1,134,545,32,342
	 * @return
	 * @throws Exception
	 */
	public int removeTaskState(int state, String taskIds) throws Exception {
		new DBConnect().changeDataBaseByProjectid(this.projectId, this.conn);
		DbUtil dbUtil = new DbUtil(conn);

		Object[] args = new Object[] {
							String.valueOf(state),
							this.projectId
						};

		//将原有的状态置空
		String sql = " update z_task set ismust=replace(ismust,?,'') "
				   + " where projectId=? "
				   + " and taskId in(" + taskIds + " ) ";

		return dbUtil.executeUpdate(sql, args);
	}

	/**
	 * 把所有"有对应科目"的底稿状态改成"有数据"状态
	 * @return
	 * @throws Exception
	 */
	public int updateTaskHasData() throws Exception {
		//获得有对应科目的底稿
		String taskIds = this.getDydgTaskIds();

		//更新到有数据的状态
		return this.updateTaskState(TASK_STATE_CODE_DATA, taskIds);
	}

	/**
	 * 根据分工用户编号,返回底稿列表
	 * @param user0
	 * @return
	 * @throws Exception
	 */
	public String getTaskListByUser0(String user0) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();

		try {
			new DBConnect().changeDataBaseByProjectid(this.projectId, this.conn);
			String sql = " select taskId,taskcode,taskname from z_task "
					   + " where user0=? and isleaf=1 ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, user0);
			rs = ps.executeQuery();

			while(rs.next()) {
				sb.append("<tr id=\"trTaskId" + rs.getString(1) + "\">");
				sb.append("	<td>" + rs.getString(2) + "</td>");
				sb.append("	<td>&nbsp;" + rs.getString(3) + "</td>");
				sb.append("</tr>");
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
	 * 用项目中的底稿更新模板中的底稿
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public boolean saveToTemplate(String taskId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			String typeId = new ProjectService(conn).getProjectById(this.projectId).getAuditType();
			
			Task task = new TaskService(conn,projectId).getTaskByTaskId(taskId);
			String taskCode = task.getTaskCode();
			
			//切换数据库
			new DBConnect().changeDataBaseByProjectid(this.projectId, this.conn);


			//判断底稿是否存在,存在就覆盖
			String sql = " update k_tasktemplate a, z_task b set a.taskname=b.taskname, a.property=b.property, a.ismust=b.ismust,a.udate=now(),a.auditproperty=b.auditproperty "
					   + " where b.projectId=? "
					   + " and b.taskcode=? "
					   + " and a.typeId=? "
					   + " and a.taskcode=? ";

			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);
			ps.setString(2, taskCode);
			ps.setString(3, typeId);
			ps.setString(4, taskCode);

			ps.executeUpdate();
			ps.close();
			
			AuditTemplateTask auditTemplateTask = new AuditTypeTemplateService(conn).getTaskByTaskCode(taskCode, typeId);

			//拷贝文件
			new ManuFileService(conn).copyProjectToTemplate(this.projectId, typeId, taskId, auditTemplateTask.getTaskId());

			//更新sheet表
			sql = " delete from k_sheettasktemplate "
				+ " where taskcode=? "
				+ " and typeId=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, taskCode);
			ps.setString(2, typeId);

			ps.executeUpdate();
			ps.close();

			sql = " insert into k_sheettasktemplate(typeId,taskId,sheettaskcode,taskcode,sheetname,property) "
				+ " select ? as typeId,taskId,sheettaskcode,taskcode,sheetname,property "
				+ " from z_sheettask "
				+ " where projectId=? "
				+ " and taskcode=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, typeId);
			ps.setString(2, this.projectId);
			ps.setString(3, taskCode);

			ps.executeUpdate();
			ps.close();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return true;
	}
	
	/**
	 * 获取自动生成的附件编号
	 * 
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public String getAttachIdByProjectId(String projectId) throws Exception {
		String sql = " select ifnull(max(attachId+1),'1') from z_attach where  "
				+ "projectId=? ";

		new DBConnect().changeDataBaseByProjectid(projectId, this.conn);
		DbUtil dbUtil = new DbUtil(conn);
		Object[] args = new Object[] { projectId };

		String result = "" + dbUtil.queryForString(sql, args);

		return result;
	}

	/**
	 * 保存附件
	 * 
	 * @param request
	 * @param conn
	 * @param projectId
	 * @throws Exception
	 */
	public String saveAttach(HttpServletRequest request, Connection conn)
			throws Exception {

		MyFileUpload fileUpload = new MyFileUpload(request, conn);
		fileUpload.UploadFile("", null);

		Map parameters = fileUpload.getMap();
		String taskId = (String) parameters.get("taskId");
		String uploadtype = (String) parameters.get("uploadtype");
		String attachCode = (String) parameters.get("attachCode");
		String filename = (String) parameters.get("filename");
		String tempdir = (String) parameters.get("tempdir");
		String userId = (String) parameters.get("userId");
		String projectId = request.getParameter("projectId");

		ZipUtil zipUtil = new ZipUtil();
		new DBConnect().changeDataBaseByProjectid(conn, projectId);
		ManuFileService manuFileService = new ManuFileService(conn);
		String[] allowed = new String[] { ".txt",".rar", ".jpg", ".bmp", ".pdf",".png",".ppt",
				".xls", ".doc", ".jpeg", ".gif", ".mp3", ".chm" ,".png", ".zip" ,".flv", ".eml" };
		try {
			if (uploadtype != null && "single".equals(uploadtype)) {
				// 单附件
				try {
					if (isFileAllowed(filename, allowed)) {
						TaskAttachTable taskAttachTable = new TaskAttachTable();
						String attachId = getAttachIdByProjectId(projectId);
						taskAttachTable.setAttachId(attachId);
						taskAttachTable.setAttachCode(attachCode);
						taskAttachTable.setProjectid(projectId);
						taskAttachTable.setTaskid(taskId);
						taskAttachTable.setFilename(filename);
						taskAttachTable.setSavedate(new SimpleDateFormat(
								"yyyy-MM-dd").format(new Date()));
						taskAttachTable.setUserId(userId);
						new TaskAttachService(conn, projectId)
								.add(taskAttachTable);

						byte[] byteData = zipUtil.fileToByteArray(new File(
								tempdir + filename));
						byteData = zipUtil.gzipBytes(byteData);
						manuFileService.saveAttachByProjectIdAndAttachId(
								projectId, attachId, byteData);
					}
				} catch (Exception e) {
					throw new Exception("上传附件出错：" + e.getMessage());
				}
			} else {
				// 批量
				String filePath = tempdir + filename;
				File uploadFile = new File(filePath);
				String outputDirectory = uploadFile.getParent() + "\\";
				File file = new File(outputDirectory);
				try {
					new DataZip().unZipCHN(filePath, outputDirectory, true);
					List files = getFileList(file);
					for (int i = 0; i < files.size(); i++) {
						File f = (File)files.get(i);
						String fname = f.getName();
						if (isFileAllowed(fname, allowed)) {
							TaskAttachTable taskAttachTable = new TaskAttachTable();
							String attachId = getAttachIdByProjectId(projectId);
							taskAttachTable.setAttachId(attachId);
							taskAttachTable
									.setAttachCode(getAttachCodeByTaskId(
											taskId, projectId));
							taskAttachTable.setProjectid(projectId);
							taskAttachTable.setTaskid(taskId);
							taskAttachTable.setFilename(f.getName());
							taskAttachTable.setSavedate(new SimpleDateFormat(
									"yyyy-MM-dd").format(new Date()));
							taskAttachTable.setUserId(userId);
							new TaskAttachService(conn, projectId)
									.add(taskAttachTable);
							byte[] byteData = zipUtil.fileToByteArray(f);
							byteData = zipUtil.gzipBytes(byteData);
							manuFileService.saveAttachByProjectIdAndAttachId(
									projectId, attachId, byteData);
						}
					}
				} catch (Exception e) {
					throw new Exception("批量上传附件出错：" + e.getMessage());
				}
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			ManuFileService.deleteFile(new File(tempdir));
		}
		return taskId;
	}
	
	/**
	 * 获取一个目录下的文件,过滤掉文件夹
	 * @param f
	 * @return
	 * @throws Exception
	 */
	public List getFileList(File f) throws Exception {
		List l = new ArrayList();
		if(f.isFile()) {
			l.add(f);
		} else {
			if(f.isDirectory()) {
				File[] files = f.listFiles();
				for(int i=0; i<files.length; i++) {
					l.addAll(getFileList(files[i]));
				}
			}
		}
		return l;
	}

	/**
	 * 获取自动生成的attachCode
	 * 
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public String getAttachCodeByTaskId(String taskId, String projectId)
			throws Exception {
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		String result = "";
		String taskCode = null;
		try {
			new DBConnect().changeDataBaseByProjectid(projectId, conn);
			if(taskId == null || "".equals(taskId) || "0".equals(taskId)) {
				taskCode = "FJ";
			} else {
				Task task = new TaskService(conn,projectId).getTaskByTaskId(taskId);
				taskCode = task.getTaskCode();
			}
			sql = "select max(attachcode) from z_attach where projectid=? and taskid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectId);
			ps.setString(2, taskId);
			rs = ps.executeQuery();
			if (rs.next()) {
				String attachCode = rs.getString(1);
				if(attachCode==null||"".equals(attachCode)||"null".equals(attachCode)) {
					return taskCode + "-01";
				}
				int index = attachCode.lastIndexOf("-");
				if(index<0) {
					return attachCode + "1";
				} else {
					String prefix = attachCode.substring(0,index);
					String temp = attachCode.substring(index+1);
					int i = 0;
					try{
						i = Integer.parseInt(temp) + 1;
						if(i<10) {
							result = prefix + "-0" + i;
						} else {
							result = prefix + "-" + i;
						}
					} catch(Exception e) {
						return attachCode + "1";
					}
				}
			} else {
				return taskCode + "-01";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return result;
	}

	/**
	 * 文件类型是否允许
	 * 
	 * @param fileName
	 * @param allowed
	 * @return
	 * @throws Exception
	 */
	public boolean isFileAllowed(String fileName, String[] allowed)
			throws Exception {
		boolean flag = false;
		if (allowed != null && allowed.length > 0) {
			String ext = fileName.substring(fileName.lastIndexOf("."));
			for (int i = 0; i < allowed.length; i++) {
				if (ext.toLowerCase().equals(allowed[i].toLowerCase())) {
					flag = true;
					break;
				}
			}
		}
		return flag;
	}
	
	/**
	 * 整合合并报表
	 * @param taskCode
	 * @param data
	 * @param loginId
	 */
	public void saveToUnite(String taskCode, byte[] data, String loginId) {
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		
		try {
			//找到是否存在于合并报表中,可能有多条记录
			String sql = " SELECT ItemID, CorpID "
					   + " FROM asdb.Unite_Corp "
					   + " WHERE EAuditProjectID=? "
					   + " and EAuditTaskID=? ";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, this.projectId);
			ps.setString(2, taskCode);
			
			rs = ps.executeQuery();
			
			while(rs.next()) {
				//更新文件
				sql = " update asdb.Unite_Data set RepRcv=RepRcv+1, ModifyDate=now(), RepData=? "
					+ " WHERE ItemID=? and CorpID=? and RepType=0 ";
				
				ps2 = conn.prepareStatement(sql);
				ps2.setBytes(1, data);
				ps2.setString(2, rs.getString(1));
				ps2.setString(3, rs.getString(2));
				
				ps2.execute();
				ps2.close();
				
				//插入到历史版本表中
				sql = " INSERT INTO asdb.Unite_History (Ver, ItemID, RepID, RepData, Comments, sTimestamp, Author) "
					+ " select RepRcv,ItemID,RepID,RepData,?,now(),? "
					+ " from asdb.Unite_Data "
					+ " where ItemID=? and CorpID=? "
					+ " order by RepRcv limit 1 ";
				ps2 = conn.prepareStatement(sql);
				ps2.setString(1, "E审通更新");
				ps2.setString(2, loginId);
				ps2.setString(3, rs.getString(1));
				ps2.setString(4, rs.getString(2));
				
				ps2.execute();
				ps2.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public void updateUserAndDate(String num,String userId,String taskId) {
		PreparedStatement ps = null;
		
		try {
			String sql = "update z_task set user"+num+"=?,date"+num+"=now() where projectId=? and taskId=?" ;
			
			if("0".equals(num)) {
				sql = "update z_task set user"+num+"=? where projectId=? and taskId=?" ;
			}
			
			ps = conn.prepareStatement(sql) ;
			ps.setString(1,userId) ;
			ps.setString(2,this.projectId) ;
			ps.setString(3,taskId) ;
			ps.execute() ;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	public void updateTaskByAuditPlan(String num,String userId,boolean updateEmpty) {
		PreparedStatement ps = null;
		
		try {
			String sql = "update z_auditplan a set a.user"+num+"=b.user"+num+" date"+num+"=now()"
				       + "left join z_task b on a.taskId = b.taskId and a.projectId = b.projectId"
				       + "where a.projectid =? " ;
			
			if(updateEmpty) {
				sql += "and (a.user"+num+"=? or a.user"+num+"='' " ;
			}else {
				sql += "and a.user"+num+"=?";
			}
			
			ps = conn.prepareStatement(sql) ;
			ps.setString(1,this.projectId) ;
			ps.setString(2,userId) ;
			ps.execute() ;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}

	public static void main(String[] args) throws Exception {
		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			TaskCommonService taskCommonService = new TaskCommonService(conn,"2009922");

			//Set ss = taskCommonService.getTaskInfo("101851");
			//System.out.println(taskCommonService);
			
			byte[] fileByte = null;

			File file = new File("c:/2.xls");

			if (file.exists()) {
				fileByte = new ZipUtil().fileToByteArray(file);
				fileByte = new ZipUtil().gzipBytes(fileByte);
			}
			
			taskCommonService.saveToUnite("A", fileByte, "admin");
			//System.out.println(taskCommonService.updateTaskHasData());
			//取得相应的状态值
			//boolean ss = taskCommonService.getTaskState("101851", TaskCommonService.TASK_STATE_CODE_ATTENTION);
			//System.out.println(ss);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
	}
}
