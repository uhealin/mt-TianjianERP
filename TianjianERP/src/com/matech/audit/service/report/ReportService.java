package com.matech.audit.service.report;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.pub.db.GetResult;
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.framework.pub.autocode.DELAutocode;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.pub.util.UTILString;

/**
 *
 * <p>Title: 合并报表的管理SERVICE</p>
 *
 * <p>Description: 作为通用的模版，供开发组复用</p>
 *
 * <p>Copyright: Copyright (c) 2006, 2008 MaTech Corporation.
 * All rights reserved. </p>
 * <p>Company: Matech  广州铭太信息科技有限公司</p>
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有，
 * 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 *
 * 贡献者团队:
 *     铭太科技 - 研发中心，审计开发组
 *
 * @author winnerQ
 * @version 3.0
 */

public class ReportService {

	private Connection conn = null;

	public ReportService(Connection conn) {
		this.conn = conn;
	}

	public int[] addProject(String projectId, String systemID,
			String projectName, String auditTimeBegin, String auditTimeEnd,
			String projectCreated, String ProjectEnd, String customerListValue,
			String auditType) throws MatechException {

		DbUtil.checkConn(conn);

		//值处理
		if (projectCreated == null || projectCreated.length() < 8) {
			projectCreated = "1900-01-01";
		}

		java.sql.Statement st = null;
		java.sql.PreparedStatement ps = null;
		java.sql.ResultSet rs = null;

		String sql = "";
		int projectID = 0;

		try {
			st = conn.createStatement();

			//先查出top one母公司的数据库 id

			String dbID = "";
			int motherAutoID = 0;
			sql = "";
			sql += "  select customerid,autoid from k_customerrelation   \n";
			sql += "  where level0=2 and substring(property,1,1)=0  \n";
			sql += "    and systemid=  " + systemID + "\n";
			rs = st.executeQuery(sql);
			if (rs.next()) {
				dbID = rs.getString("customerid");
				motherAutoID = rs.getInt("autoid");
			} else {
				throw new Exception("找不到母公司的关系记录");
			}

			if (projectId == null || projectId.length() < 1) {
				projectID = getProjectID(auditType);
				//增加一个项目
				sql = "INSERT INTO z_Project(customerid,ProjectID,ProjectName,AuditTimeBegin,AuditTimeEnd,projectCreated,ProjectEnd,systemid ,auditdept,audittype,auditpara,state)  \n"
						+ "VALUES("
						+ dbID
						+ ","
						+ String.valueOf(projectID)
						+ ",'"
						+ projectName
						+ "','"
						+ auditTimeBegin
						+ "','"
						+ auditTimeEnd
						+ "','"
						+ projectCreated
						+ "','"
						+ ProjectEnd
						+ "','"
						+ systemID
						+ "',555555,'"
						+ auditType + "','合并报表',1)";

				st.execute(sql);
			} else {
				//修改一个项目
				sql = "update z_project set ProjectName='" + projectName
						+ "',AuditTimeBegin='" + auditTimeBegin
						+ "',AuditTimeEnd='" + auditTimeEnd
						+ "',projectCreated='" + projectCreated
						+ "',ProjectEnd='" + ProjectEnd + "' where projectid="
						+ projectId;
				st.execute(sql);
				return new int[] { Integer.parseInt(projectId), 0 };
			}
			//			修改一个项目现在有另一个方法完成。editProject
			//			else{
			//				projectID=Integer.parseInt(_projectID);
			////				修改一个项目
			//				sql= "update z_Project set customerid="+dbID+",ProjectName='"+projectName+"',AuditTimeBegin='"+auditTimeBegin+"',AuditTimeEnd='"+auditTimeEnd+"',projectCreated='"+projectCreated+"',ProjectEnd='"+ProjectEnd+"',systemid='"+systemID+"' where ProjectID="+String.valueOf(projectID)+"  \n";
			//
			//				st.execute(sql);
			//			}

			//增加到科目对应的分录表上。
			sql = "insert into asdb.z_projectEntry(projectid,relationid,relationProjectid) values(?,?,?)";
			ps = conn.prepareStatement(sql);
			String[] customerValue = customerListValue.split(";");

			for (int i = 0; i < customerValue.length; i++) {
				if (customerValue[i].indexOf(",") < 0) {
					continue;
				}
				String[] customer = customerValue[i].split(",");
				ps.setInt(1, projectID);
				ps.setInt(2, Integer.parseInt(customer[0]));
				ps.setInt(3, Integer.parseInt(customer[1]));
				ps.addBatch();

			} //for end

			ps.executeBatch();
			ps.clearBatch();

			//查母公司的accpackageid
			sql = " select accpackageid from  z_project a  \n"
					+ " inner join z_projectentry b on a.projectid=b.relationProjectid \n"
					+ " where b.relationid=" + motherAutoID
					+ " and b.projectid=" + projectID;

			//更新z_project表的accpackageid字段
			rs = st.executeQuery(sql);
			if (rs.next()) {
				String motherApkID = rs.getString("accpackageid");
				sql = "update z_project set accpackageid=" + motherApkID
						+ "  \n where projectid=" + projectID;
				st.execute(sql);
			} else {
				//"找不到母公司的账套" 选择了手制报表。

				sql = "select customerid from k_customerrelation where autoid="
						+ motherAutoID;
				rs = st.executeQuery(sql);
				//没有母公司，那么就给他默认的年份1900
				if (rs.next()) {
					sql = "update z_project set accpackageid="
							+ rs.getString("customerid")
							+ auditTimeEnd.substring(0, 4)+"  \n where projectid=" + projectID;
					st.execute(sql);
//					sql = "update z_project set accpackageid="
//						+ rs.getString("customerid")
//						+ "1900  \n where projectid=" + projectID;
//					st.execute(sql);
				} else {
					//考虑是否需要抛异常？
				}

			}

			//构建底稿树
			new ReportProjectService(conn, String.valueOf(projectID), systemID)
					.createTree(auditType);

		} catch (Exception e) {
			Debug.print(Debug.iError, "新增合并报表项目失败", e);
			throw new MatechException("新增合并报表项目失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(st);
		}
		return new int[] { projectID, 0 };
	}

	/**
	 * 生成z_task
	 * @param conn
	 * @throws Exception
	 */
	public void createTaskAndManuScript(Connection conn, String systemID,
			String projectID, String dbID) throws MatechException {

		//切换到母公司数据库
		DBConnect dbconn = new DBConnect();
		dbconn.changeDataBase(dbID, conn);

		try {
			this.createTaskAndManuScriptRecursion(conn, systemID, projectID, 0,
					null, 0, 1);
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		}

	}

	public void createTaskAndManuScriptRecursion(Connection conn,
			String systemID, String projectID, int parentID,
			String parentTaskCode, int parentTaskID, int taskID)
			throws Exception {

		GetResult gr = new GetResult(conn);

		String sql = "";

		java.sql.PreparedStatement ps = null;
		java.sql.Statement st = null;
		java.sql.ResultSet rs = null;
		java.sql.ResultSet recursionRs = null;

		st = conn.createStatement();

		sql = " select autoid,customerid,a.property,b.relationprojectid,a.nodeName,c.departName,a.level0 \n"
				+ " from asdb.k_customerrelation a \n"
				+ " left join asdb.z_projectentry b \n"
				+ " on a.autoid=b.relationid and b.projectid ="
				+ projectID
				+ " \n"
				+ " left join k_customer c \n"
				+ " on a.customerid=c.departid \n"
				+ " where a.systemid = "
				+ systemID
				+ " \n"
				+ " and a.parentid = ? \n"
				+ " and substring(a.property,2,1) = 0    \n"
				+ " order by a.property  \n";
		ps = conn.prepareStatement(sql);
		ps.setInt(1, parentID);
		recursionRs = ps.executeQuery();

		int i = 1;
		while (recursionRs.next()) {
			//

			//得到各公司的关系autoid
			int autoID = recursionRs.getInt("autoid");
			//得到各公司的customerid
			String customerID = recursionRs.getString("customerid");
			//得到各公司的名字，作为taskName
			String customerName = recursionRs.getString("departName");
			//得到各公司的参与项目编号 -1 手制报表 -2 不参与合并
			String relationProjectID = recursionRs
					.getString("relationprojectid");
			//属性值 0 母公司 1 子公司 2 汇总结点
			String property = gr.getProperty(recursionRs.getString("property"),
					1);
			//层次
			String level = recursionRs.getString("level0");
			//结点名称
			String nodeName = recursionRs.getString("nodeName");

			//出错信息
			String err = "";

			if ("2".equals(property)) {

				if (parentID != 0) {
					sql = " select 1 as a \n"
							+ " from asdb.k_customerrelation a \n"
							+ " inner join asdb.z_projectentry b \n"
							+ " on a.autoid=b.relationid and b.projectid ="
							+ projectID + " \n" + " where a.systemid = "
							+ systemID + " \n" + " and a.parentid = " + autoID
							+ " \n"
							+ " and substring(a.property,1,2) = '00'    \n"
							+ " and b.relationprojectid!='-2'   \n"
							+ " order by a.property  \n";
					rs = st.executeQuery(sql);

					//本结点的母公司参加。
					if (rs.next()) {

					} else { //本结点的母公司不参加。说明他的其它子公司也不会参加。则直接跳出
						continue;
					}
				}

				//					生成汇总结点 而且再显示层面上再降一级
				int curRLevelTaskID = taskID++;
				String curRLevelTaskCode = getTaskCode(parentTaskCode, i++);
				sql = " insert into z_task(               `TaskID`,  ManuID,                             `TaskCode`,              `TaskName`,  `ParentTaskID`,   `ProjectID`, `IsLeaf`,  `level0`,Description) \n"
						+ " values( '"
						+ String.valueOf(curRLevelTaskID)
						+ "',       0,'"
						+ curRLevelTaskCode
						+ "' ,'"
						+ nodeName
						+ "' ,"
						+ parentTaskID
						+ ","
						+ projectID
						+ " ,        0," + level + ",'" + autoID + "'  )\n";
				st.execute(sql);
				//					显示层面上再降一级
				parentTaskCode = curRLevelTaskCode;
				parentTaskID = curRLevelTaskID;

				//					生成 合并报表结点
				for (int j = 0; true; j++) {

					try {

							int curTaskID = 0;

							//								生成 合并报表结点
							curTaskID = taskID++;
							String curTaskCode = getTaskCode(parentTaskCode, 1);
							sql = " insert into z_task(               `TaskID`,  ManuID,                             `TaskCode`,              `TaskName`,  `ParentTaskID`,   `ProjectID`, `IsLeaf`,  `level0`,property,orderid,Description) \n"
									+ " values( '"
									+ String.valueOf(curTaskID)
									+ "',       0,'"
									+ curTaskCode
									+ "' ,'"
									+ nodeName
									+ " 合并报表' ,"
									+ parentTaskID
									+ ","
									+ projectID
									+ " ,        0,"
									+ level
									+ " ,'16','"
									+ curTaskCode
									+ "' ,'"
									+ autoID + "')\n";
							st.execute(sql);

							int curSonTaskID = taskID++;

							sql = " insert into z_task(               `TaskID`,  ManuID,                             `TaskCode`,              `TaskName`,  `ParentTaskID`,   `ProjectID`, `IsLeaf`,  `level0`,property,orderid,Description,manutemplateid) \n"
									+ " values( '"
									+ String.valueOf(curSonTaskID)
									+ "','','"
									+ getTaskCode(curTaskCode, 1)
									+ "' ,'"
									+ nodeName
									+ ".xls' ,"
									+ curTaskID
									+ ","
									+ projectID
									+ " ,        1,"
									+ level
									+ " ,15 ,'"
									+ getTaskCode(curTaskCode, 1)
									+ "','" + autoID + "',3)\n";
							st.execute(sql);

							ManuFileService msm = new ManuFileService(conn);
							msm.newFileByProjectIdAndTaskId(projectID, String
									.valueOf(curSonTaskID), "0", "3");
							break;

					} catch (Exception e) {
						e.printStackTrace();
						if (j > 20) {
							err = e.getMessage();
							break;
						}
					}
				}

				//生成 参与公司报表结点
				String curTaskCode = getTaskCode(parentTaskCode, 2);
				int curTaskID = taskID++;
				sql = " insert into z_task(            `TaskID`,                               `TaskCode`,              `TaskName`,  `ParentTaskID`,        `ProjectID`, `IsLeaf`,  `level0`,property,orderid,Description) \n"
						+ " values( '"
						+ String.valueOf(curTaskID)
						+ "','"
						+ curTaskCode
						+ "' ,'"
						+ nodeName
						+ " 参与公司报表' ,"
						+ parentTaskID
						+ ","
						+ projectID
						+ "  ,        0,"
						+ level
						+ " ,'18','"
						+ curTaskCode
						+ "' ,'"
						+ autoID
						+ "')\n";
				st.execute(sql);

				this.createTaskAndManuScriptRecursion(conn, systemID,
						projectID, autoID, curTaskCode, curTaskID, taskID);

			} else {

				for (int j = 0; true; j++) {
					try {

							//手制报表需要小LU提供
							if ("-1".equals(relationProjectID)) {
								//									得到taskID
								int curTaskID = taskID++;
								String taskCode = this.getTaskCode(
										parentTaskCode, i++);


								sql = " insert into z_task(            `TaskID`,     `TaskCode`,          `TaskName`,   `ParentTaskID`,  `ProjectID`, `IsLeaf`,    `level0`,  `ManuID`,property,orderid,Description,manutemplateid) \n"
										+ " values("
										+ String.valueOf(curTaskID)
										+ ", '"
										+ taskCode
										+ "',  '"
										+ customerName
										+ ".xls', "
										+ parentTaskID
										+ ","
										+ projectID
										+ ",      1  , "
										+ level
										+ " , "
										+ "''"
										+ ",15, '"
										+ taskCode
										+ "','" + autoID + "',4) \n";
								st.execute(sql);

								ManuFileService msm = new ManuFileService(conn);
								msm.newFileByProjectIdAndTaskId(projectID,
										String.valueOf(curTaskID), "0", "4");

								//更新z_projectEntry对应的taskID
								//									sql="update asdb.z_projectEntry set taskid="+String.valueOf(curTaskID)+" where projectid="+projectID+" and relationid="+String.valueOf(autoID)+" \n";
								//									st.execute(sql);

							} else if ("-2".equals(relationProjectID)) {
								break;
							} else {
								int curTaskID = taskID++;
								//									得到taskID
								String taskCode = this.getTaskCode(
										parentTaskCode, i++);

								//得到相应的关系项目的TASKID,unid
								String correspondTaskID = "";

								sql = "select taskid as tunid from asdb_"
										+ customerID
										+ ".z_task where projectid="
										+ relationProjectID
										+ " and property=15 and IsLeaf = 1";
								rs = st.executeQuery(sql);
								if (rs.next()) {
									correspondTaskID = rs.getString("taskid");

									sql = " insert into z_task(                 `TaskID`,                `TaskCode`,                      `TaskName`, `TaskContent`,                          `Description`,                    `ParentTaskID`,               `ProjectID`,     `IsLeaf`,           `level0`,           `ManuID`, `ManuTemplateID`, `user0`, `User1`, `User2`, `User3`, `User4`, `User5`, `Property`, `FullPath`, `date1`, `date2`, `date3`, `date4`, `date5`, `createdate`,                          `orderid`, `ismust`, `SubjectName`) \n"
											+ " select "
											+ String.valueOf(curTaskID)
											+ " as TaskID, '"
											+ taskCode
											+ "' as TaskCode,  '"
											+ customerName
											+ ".xls' as TaskName, `TaskContent`,'"
											+ autoID
											+ "' as `Description`,"
											+ parentTaskID
											+ " as ParentTaskID,"
											+ projectID
											+ " as ProjectId, 1 as IsLeaf, "
											+ level
											+ " as level0, "
											+ "''"
											+ " as ManuID, `ManuID`, `user0`, `User1`, `User2`, `User3`, `User4`, `User5`, `Property`, `FullPath`, `date1`, `date2`, `date3`, `date4`, `date5`, `createdate`,'"
											+ taskCode
											+ "' as `orderid`, `ismust`, `SubjectName` \n"
											+ " from asdb_"
											+ customerID
											+ ".z_task where projectid="
											+ relationProjectID
											+ " and property=15  and IsLeaf = 1 \n";
									st.execute(sql);

									ManuFileService msm = new ManuFileService(
											conn);
									msm.saveFileByTaskId(projectID, String.valueOf(curTaskID), msm
											.getFileByProjectIdAndTaskId(
													relationProjectID,
													correspondTaskID));
								} else {
									//如果没有记录，则插入一条手制报表

									sql = " insert into z_task(            `TaskID`,     `TaskCode`,          `TaskName`,   `ParentTaskID`,  `ProjectID`, `IsLeaf`,    `level0`,  `ManuID`,property,orderid,Description,manutemplateid) \n"
											+ " values("
											+ String.valueOf(curTaskID)
											+ ", '"
											+ taskCode
											+ "',  '"
											+ customerName
											+ ".xls', "
											+ parentTaskID
											+ ","
											+ projectID
											+ ",      1  , "
											+ level
											+ " , "
											+ "''"
											+ ",15, '"
											+ taskCode
											+ "','"
											+ autoID
											+ "',4) \n";
									st.execute(sql);

									ManuFileService msm = new ManuFileService(
											conn);
									msm.newFileByProjectIdAndTaskId(
													projectID,
													String.valueOf(curTaskID),
													"0", "4");

								}
							}

						break;
					} catch (Exception e) {
						e.printStackTrace();
						if (j > 20) {
							err = e.getMessage();
							break;
						}
					}

				} //for end
			}
			if (err.length() > 0) {
				throw new Exception(err);
			}
		} //end while

	}

	public java.util.Map getProjectDetail(String projectID)
			throws MatechException {

		DbUtil.checkConn(conn);

		java.util.Map result = new java.util.HashMap();
		java.sql.Statement st = null;
		java.sql.ResultSet rs = null;

		try {

			String sql = "select systemID,projectName,auditTimeBegin,auditTimeEnd,projectCreated,ProjectEnd from z_project where projectid ="
					+ projectID;
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			if (rs.next()) {
				result.put("systemID", rs.getString("systemID"));
				result.put("projectName", rs.getString("projectName"));
				result.put("auditTimeBegin", rs.getString("auditTimeBegin"));
				result.put("auditTimeEnd", rs.getString("auditTimeEnd"));
				result.put("projectCreated", rs.getString("projectCreated"));
				result.put("ProjectEnd", rs.getString("ProjectEnd"));
			} else {
				throw new Exception("找不到项目" + projectID);
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(rs);
		}
		return result;
	}

	/**
	 * 返回编号格式为 年份+00+4位数字 例如(2006001001) 目前正在使用
	 *
	 * @param typeId
	 * @return
	 * @throws Exception
	 */
	public int getProjectID(String typeId) throws Exception {
		DELAutocode t = new DELAutocode();
		String id = UTILString.nCharToString('0', 2 - typeId.length()) + typeId;
		String[] temp = new String[] { id };
		return Integer.parseInt(t.getAutoCode("XMBH", "", temp));
	}

	/**
	 * 返回新的TaskCode 10ABDC
	 */
	public String getTaskCode(String parentTaskCode, int i) {
		String sonID = "";

		if (i <= 9) {
			sonID = String.valueOf(i);
		} else {

			sonID = String.valueOf((char) (i + 55));
		}

		if (parentTaskCode == null) {
			parentTaskCode = "";
		}

		return parentTaskCode + sonID;

	}

	/**
	 * 通过底稿TASKID来获得合并报表系统中的对应客户编号
	 * @todo  不支持多级，要待修改
	 * @param projectid
	 * @param taskid
	 * @return
	 */
	public String getCustomeridByTaskid(String projectid  ,String IdOrCode ,String taskid){

		java.sql.Statement st = null;
		java.sql.ResultSet rs = null;

		try {

			st = conn.createStatement();

			String sql = "";
			sql = "select c.property,c.taskname,c.taskid \n"
				+"from z_Task a,z_Task b,z_Task c \n"
				+"where a.projectid="+projectid+" \n"
				+"and b.projectid="+projectid+" \n"
				+"and c.projectid="+projectid+" \n"
				+"and a."+IdOrCode+"='"+taskid+"' \n"
				+"and b.taskid=a.parenttaskid \n"
				+"and c.taskid=b.parenttaskid";
			String property="",taskname="";
			rs = st.executeQuery(sql);
			if (rs.next()) {
				property=rs.getString(1);
				taskname=rs.getString(2);
			}
			rs.close();

			org.util.Debug.prtOut("执行的SQL="+sql);

			org.util.Debug.prtOut("找到的节点名字="+taskname+"|property="+property);

			if ("20".equals(property)){
				//单体,找到 同节点名字的customerid
				sql="select c.departid \n"
					+"from z_project a,k_customerrelation b,k_customer c \n"
					+"where a.projectid="+projectid+" \n"
					+"and a.systemid=b.systemid \n"
					+"and b.customerid=c.departid \n"
					+"and c.departname='"+taskname+"'";
				rs = st.executeQuery(sql);
				if (rs.next()) {
					org.util.Debug.prtOut("合并单体节点,找到 同节点名字的customerid="+rs.getString(1));
					return rs.getString(1);
				}
				rs.close();



			}else{
				//合并，通过项目来找
				org.util.Debug.prtOut("合并汇总节点,projectid="+projectid);

				return getCustomeridByProjectid(projectid);


			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
		}
		return "";
	}


	public String getCustomeridByProjectid(String projectID) throws MatechException {

		java.sql.Statement st = null;
		java.sql.ResultSet rs = null;
		DbUtil.checkConn(conn);

		try {

			st = conn.createStatement();

			String sql = "select customerid from z_project where projectid="+ projectID ;
			rs = st.executeQuery(sql);
			if (rs.next()) {
				return rs.getString(1);
			} else {
				return "";
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
		}
		return "";
	}



	/**
	 * 判断projectid是否为合并报表项目。
	 * @param projectID
	 * @return
	 */
	public int isReportProject(String projectID) throws MatechException {

		java.sql.Statement st = null;
		java.sql.ResultSet rs = null;
		DbUtil.checkConn(conn);

		try {

			st = conn.createStatement();

			String sql = "";
			sql = "select systemid from z_project where projectid='"
					+ projectID + "'";
			rs = st.executeQuery(sql);
			if (rs.next() && rs.getInt("systemid") > 0) {
				return 1;
			} else {
				return 0;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
		}
		return -1;
	}

	/**
	 * 得到引用底稿相关的数据。
	 * @param projectID
	 * @return
	 */
	public java.util.Map getReferDetail(String projectID, String taskCode)
			throws MatechException {
		DbUtil.checkConn(conn);
		java.sql.Statement st = null;
		java.sql.ResultSet rs = null;
		java.util.HashMap resultMap = new java.util.HashMap();

		try {
			st = conn.createStatement();

			String sql = "";

			String customerid = "";

			sql = "select customerid,accpackageid from asdb.z_project where projectid='"
					+ projectID + "'";
			rs = st.executeQuery(sql);
			if (rs.next()) {
				customerid = rs.getString("customerid");
			} else {
				throw new Exception("找不到项目［" + projectID + "］的单位编号\nsql=" + sql);
			}

			String description = "";
			sql = "select description from asdb_" + customerid
					+ ".z_task where projectid=" + projectID
					+ " and taskCode ='" + taskCode + "' \n";
			rs = st.executeQuery(sql);
			if (rs.next()) {
				description = rs.getString("description");
			} else {
				throw new Exception("找不到项目［" + projectID + "］的任务编号［" + taskCode
						+ "］\nsql=" + sql);
			}

			String referProjectid = "";
			sql = "select relationProjectid from asdb.z_projectEntry where projectid = "
					+ projectID + " and relationid=" + description + " \n";
			rs = st.executeQuery(sql);
			if (rs.next()) {
				referProjectid = rs.getString("relationProjectid");
			} else {
				throw new Exception("找不到项目［" + projectID + "］的relationid［"
						+ description + "］\nsql=" + sql);
			}

			String referAccpackageid = "";
			sql = "select accpackageid from z_project where projectid="
					+ referProjectid;
			rs = st.executeQuery(sql);
			if (rs.next()) {
				referAccpackageid = rs.getString("accpackageid");
			} else {
				throw new Exception("找不到引用项目［" + referProjectid + "］\nsql="
						+ sql);
			}

			String referTaskCode = "";
			sql = "select taskCode from asdb_"
					+ referAccpackageid.substring(0, 6)
					+ ".z_task where projectid=" + referProjectid
					+ " and property='15' and IsLeaf = 1 \n";
			rs = st.executeQuery(sql);
			if (rs.next()) {
				referTaskCode = rs.getString("taskCode");
			} else {
				throw new Exception("找不到引用项目［" + referProjectid
						+ "］的 参与合并底稿\nsql=" + sql);
			}

			resultMap.put("referProjectid", referProjectid);
			resultMap.put("referAccpackageid", referAccpackageid);
			resultMap.put("referTaskCode", referTaskCode);

			return resultMap;
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			return new java.util.HashMap();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
		}
	}

	public String getTree(String systemID) throws Exception {

		DbUtil.checkConn(conn);

		Connection conn = null;
		java.sql.Statement st = null;
		java.sql.ResultSet rs = null;

		try {
			return this.treeRecursion(conn, systemID, "0");
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
		}
	}

	public String treeRecursion(Connection conn, String systemID,
			String parentID) throws Exception {

		GetResult gr = new GetResult(conn);
		String sql = "select a.autoid,a.customerid,b.DepartName,a.nodeName,a.property,a.parentid from \n"
				+ " (select autoid,customerid,nodeName,property,parentid from k_customerrelation where substring(property,2,1) = 0 and systemid=? and parentid=?) a \n"
				+ " left join  \n"
				+ " k_customer b  \n"
				+ " on a.customerid=b.departid  \n"
				+ " order by property asc,customerid asc";

		PreparedStatement ps = null;
		ResultSet rs = null;

		ps = conn.prepareStatement(sql);
		ps.setString(1, systemID);
		ps.setString(2, parentID);
		rs = ps.executeQuery();

		StringBuffer sb = new StringBuffer("");
		sb.append("<table  width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
		while (rs.next()) {

			if ("2".equals(gr.getProperty(rs.getString("property"), 1))) {
				sb.append("<tr id=\"valueTR\" name=\"valueTR\" _autoid=\""
								+ rs.getString("autoid")
								+ "\" _property=\""
								+ gr.getProperty(rs.getString("property"), 1)
								+ "\" _parentid=\""
								+ rs.getString("parentid")
								+ "\" class=\"cursorstate\" ondblclick=\"editInfo(this);\" onclick=\"getSubTree('"
								+ rs.getString(1)
								+ "');\" onmousedown='startDelete(this);'  onMouseOver=\"this.bgColor='#E4E8EF';\" onMouseOut=\"this.bgColor='';\">");
				sb.append("<td   height=\"18\" width=\"15\" align=\"left\">");
				sb.append("<img id=\"ActImg"
								+ rs.getString(1)
								+ "\" src=\"../images/nofollow.jpg\" width=\"11\" height=\"11\" /></td>");
				sb.append("<td align=\"left\">" + rs.getString("nodeName")
						+ "</td>");

				sb.append("</tr>");

				sb.append("<tr  id =\"sonParent" + rs.getString(1) + "\"  >");

				sb.append("<td>");
				sb.append("</td>");
				sb.append("<td align=\"left\" >");
				sb.append(this.treeRecursion(conn, systemID, rs.getString(1)));
				sb.append("</td>");
				sb.append("</tr>");
			} else {

				if ("0".equals(gr.getProperty(rs.getString("property"), 1))) {
					sb.append("<tr id=\"valueTR\" name=\"valueTR\" _autoid=\""
									+ rs.getString("autoid")
									+ "\" _property=\""
									+ gr.getProperty(rs.getString("property"),
											1)
									+ "\" _parentid=\""
									+ rs.getString("parentid")
									+ "\" class=\"cursorstate\" ondblclick=\"editInfo(this);\"  onMouseOver=\"this.bgColor='#E4E8EF';\" onMouseOut=\"this.bgColor='';\">");
					sb.append("<td   height=\"18\"  width=\"15\" align=\"left\">");
					sb.append("<img src=\"/AuditSystem/images/sjx1.gif\">");
					sb.append("</td>");
					sb.append("<td>");
					sb.append("<font color=#009933>"
							+ rs.getString("customerid")
							+ "</font><font color='blue'>("
							+ rs.getString("departName") + ")</font></td>");
					sb.append("</tr>");
				} else {
					sb.append("<tr id=\"valueTR\" name=\"valueTR\" _autoid=\""
									+ rs.getString("autoid")
									+ "\" _property=\""
									+ gr.getProperty(rs.getString("property"),
											1)
									+ "\" _parentid=\""
									+ rs.getString("parentid")
									+ "\" class=\"cursorstate\" ondblclick=\"editInfo(this);\" onmousedown='startDelete(this);' onMouseOver=\"this.bgColor='#E4E8EF';\" onMouseOut=\"this.bgColor='';\">");
					sb.append("<td   height=\"18\"  width=\"15\" align=\"left\">");
					sb.append("<img src=\"/AuditSystem/images/sjx1.gif\">");
					sb.append("</td>");
					sb.append("<td>");
					sb.append(rs.getString("customerid")
							+ "<font color='blue'>("
							+ rs.getString("departName") + ")</font></td>");
					sb.append("</tr>");
				}

			}

		}

		sb.append("</table>");

		return sb.toString();

	}


	/**
	 * 合并报表专用,根据合并节点的某张底稿找出所有同级单体下面的同名底稿
	 * @param projectId
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public List getTaskIdList(String projectId, String taskId) throws Exception {
		List taskIdList = new ArrayList();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			StringBuffer sql = new StringBuffer();
			sql.append("	select taskId from z_task   									\n ");
			sql.append("		where parenttaskId in( 										\n ");
			sql.append("			select taskId from z_task  								\n ");
			sql.append("			where parenttaskId in ( 								\n ");
			sql.append("				select taskId from z_task  							\n ");
			sql.append("				where parenttaskId=( 								\n ");
			sql.append("					select taskId from z_task 						\n ");
			sql.append("					where parenttaskId=( 							\n ");
			sql.append("						select parenttaskId from z_task 			\n ");
			sql.append("						where taskId=( 								\n ");
			sql.append("							select parenttaskId from z_task 		\n ");
			sql.append("							where taskId=( 							\n ");
			sql.append("								select parenttaskId from z_task 	\n ");
			sql.append("								where taskId=? 						\n ");	//1.taskId
			sql.append("								and projectId=? 					\n ");	//2.projectId
			sql.append("							)  	and projectId="+projectId+" \n ");
			sql.append("						) 	and projectId="+projectId+" 											\n ");
			sql.append("					) and property='21'		and projectId="+projectId+" 							\n ");
			sql.append("				) 	and projectId="+projectId+" 													\n ");
			sql.append("			) 	and projectId="+projectId+" 														\n ");
			sql.append("		) and isleaf='1' 											\n ");
			sql.append("			and projectId=? 										\n ");	//3.projectId
			sql.append("			and taskname=( 											\n ");
			sql.append("				select taskname from z_task 						\n ");
			sql.append("				where taskId=? 										\n ");	//4.taskId
			sql.append("				and projectId=? 									\n ");	//5.projectId
			sql.append("			) 														\n ");

			new DBConnect().changeDataBaseByProjectid(conn, projectId);

			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, taskId);
			ps.setString(2, projectId);
			ps.setString(3, projectId);
			ps.setString(4, taskId);
			ps.setString(5, projectId);

			rs = ps.executeQuery();

			while(rs.next()) {
				taskIdList.add(rs.getString(1));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return taskIdList;
	}

	/**
	 * 合并报表专用,根据合并节点的某张底稿找出所有同级单体下面的同名底稿
	 * @param projectId
	 * @param taskId
	 * @return  底稿索引号
	 * @throws Exception
	 */
	public List getTaskCodeList(String projectId, String taskId) throws Exception {
		List taskIdList = new ArrayList();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			StringBuffer sql = new StringBuffer();
			sql.append("	select taskcode from z_task   									\n ");
			sql.append("		where parenttaskId in( 										\n ");
			sql.append("			select taskId from z_task  								\n ");
			sql.append("			where parenttaskId in ( 								\n ");
			sql.append("				select taskId from z_task  							\n ");
			sql.append("				where parenttaskId=( 								\n ");
			sql.append("					select taskId from z_task 						\n ");
			sql.append("					where parenttaskId=( 							\n ");
			sql.append("						select parenttaskId from z_task 			\n ");
			sql.append("						where taskId=( 								\n ");
			sql.append("							select parenttaskId from z_task 		\n ");
			sql.append("							where taskId=( 							\n ");
			sql.append("								select parenttaskId from z_task 	\n ");
			sql.append("								where taskId="+taskId+" 						\n ");	//1.taskId
			sql.append("								and projectId="+projectId+" 					\n ");	//2.projectId
			sql.append("							) 	and projectId="+projectId+"  										\n ");
			sql.append("						) 											\n ");
			sql.append("					) and property='21'		and projectId="+projectId+" 							\n ");
			sql.append("				) 	and projectId="+projectId+" 													\n ");
			sql.append("			) 		and projectId="+projectId+" 													\n ");
			sql.append("		) and isleaf='1' 											\n ");
			sql.append("			and projectId="+projectId+" 										\n ");	//3.projectId
			sql.append("			and taskname=( 											\n ");
			sql.append("				select taskname from z_task 						\n ");
			sql.append("				where taskId="+taskId+" 										\n ");	//4.taskId
			sql.append("				and projectId="+projectId+" 									\n ");	//5.projectId
			sql.append("			) 														\n ");

			new DBConnect().changeDataBaseByProjectid(conn, projectId);

			org.util.Debug.prtOut("\nqwh:sql＝"+sql.toString());
			ps = conn.prepareStatement(sql.toString());

			rs = ps.executeQuery();

			while(rs.next()) {
				taskIdList.add(rs.getString(1));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return taskIdList;
	}

	/**
	 * 合并报表专用,根据合并节点的某张底稿找出所有同级单体下面的同名底稿
	 * @param projectId
	 * @param taskId
	 * @return  底稿索引号
	 * @throws Exception
	 */
	public List getTaskCodeList(String projectId, String taskId,String taskname) throws Exception {
		List taskIdList = new ArrayList();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			StringBuffer sql = new StringBuffer();
			sql.append("	select taskcode from z_task   									\n ");
			sql.append("		where parenttaskId in( 										\n ");
			sql.append("			select taskId from z_task  								\n ");
			sql.append("			where parenttaskId in ( 								\n ");
			sql.append("				select taskId from z_task  							\n ");
			sql.append("				where parenttaskId=( 								\n ");
			sql.append("					select taskId from z_task 						\n ");
			sql.append("					where parenttaskId=( 							\n ");
			sql.append("						select parenttaskId from z_task 			\n ");
			sql.append("						where taskId=( 								\n ");
			sql.append("							select parenttaskId from z_task 		\n ");
			sql.append("							where taskId=( 							\n ");
			sql.append("								select parenttaskId from z_task 	\n ");
			sql.append("								where taskId="+taskId+" 						\n ");	//1.taskId
			sql.append("								and projectId="+projectId+" 					\n ");	//2.projectId
			sql.append("							) 	and projectId="+projectId+"  										\n ");
			sql.append("						) 		and projectId="+projectId+" 										\n ");
			sql.append("					) and property='21'		and projectId="+projectId+" 							\n ");
			sql.append("				) 	and projectId="+projectId+" 													\n ");
			sql.append("			) 	and projectId="+projectId+" 														\n ");
			sql.append("		) and isleaf='1' 											\n ");
			sql.append("			and projectId="+projectId+" 										\n ");	//3.projectId
			sql.append("			and taskname='"+taskname+"'");

			new DBConnect().changeDataBaseByProjectid(conn, projectId);

			org.util.Debug.prtOut("\nqwh:sql＝"+sql.toString());
			ps = conn.prepareStatement(sql.toString());

			rs = ps.executeQuery();

			while(rs.next()) {
				taskIdList.add(rs.getString(1));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return taskIdList;
	}

	/**
	 * 合并报表专用,根据合并节点的某张底稿找出所有同级单体下面的同名底稿
	 * @param projectId
	 * @param taskId
	 * @param taskName
	 * @return
	 * @throws Exception
	 */
	public List getTaskIdList(String projectId, String taskId, String taskName) throws Exception {
		List taskIdList = new ArrayList();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			StringBuffer sql = new StringBuffer();
			sql.append("	select taskId from z_task   									\n ");
			sql.append("		where parenttaskId in( 										\n ");
			sql.append("			select taskId from z_task  								\n ");
			sql.append("			where parenttaskId in ( 								\n ");
			sql.append("				select taskId from z_task  							\n ");
			sql.append("				where parenttaskId=( 								\n ");
			sql.append("					select taskId from z_task 						\n ");
			sql.append("					where parenttaskId=( 							\n ");
			sql.append("						select parenttaskId from z_task 			\n ");
			sql.append("						where taskId=( 								\n ");
			sql.append("							select parenttaskId from z_task 		\n ");
			sql.append("							where taskId=( 							\n ");
			sql.append("								select parenttaskId from z_task 	\n ");
			sql.append("								where taskId=? 						\n ");	//1.taskId
			sql.append("								and projectId=? 					\n ");	//2.projectId
			sql.append("							) 	and projectId="+projectId+"  										\n ");
			sql.append("						) 	and projectId="+projectId+" 											\n ");
			sql.append("					) and property='21'		and projectId="+projectId+" 							\n ");
			sql.append("				) 	and projectId="+projectId+" 													\n ");
			sql.append("			) 		and projectId="+projectId+" 													\n ");
			sql.append("		) and isleaf='1' 											\n ");
			sql.append("			and projectId=? 										\n ");	//3.projectId
			sql.append("			and taskname=?											\n ");	//4.taskName

			new DBConnect().changeDataBaseByProjectid(conn, projectId);

			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, taskId);
			ps.setString(2, projectId);
			ps.setString(3, projectId);
			ps.setString(4, taskName);

			rs = ps.executeQuery();

			while(rs.next()) {
				taskIdList.add(rs.getString(1));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return taskIdList;
	}

	/**
	 * 通过项目编号、当前底稿编号和指定底稿的名字，获得指定底稿的TASKCODE
	 * @param projectId 项目编号
	 * @param taskId  当前底稿编号
	 * @param taskName 指定底稿的名字
	 * @param iMode 模式：1：普通项目，2：合并报表项目，单体节点的底稿（附注表、明细表、合并报表）中，通过底稿名称定位明细表中的指定一张底稿的taskcode；
	 * 					   其他：暂不支持；
	 * @return
	 * @throws Exception
	 */
	public String getTaskCodeByTaskName(String projectId, String taskId, String taskName,int iMode ) throws Exception {

		StringBuffer sql = new StringBuffer();

		Object[] params =null;

		switch (iMode){
		case 1:
			//普通项目
			sql.append("select taskcode from z_task \n");
			sql.append("where projectid=? and taskname=?  \n");

			params = new Object[]{projectId,taskName};

			break;
		case 2:
			//合并报表项目，单体节点的底稿（附注表、明细表、合并报表）中，通过底稿名称定位明细表中的指定一张底稿的taskcode；
			sql.append("select b2.taskcode from z_task a2,z_task b2 \n");
			sql.append("where a2.projectid=? \n");
			sql.append("and a2.parenttaskid= ( \n");
			sql.append("	select a1.parenttaskid from \n");
			sql.append("	z_task a1,z_task b1 \n");
			sql.append("	where a1.projectid=? \n");
			sql.append("	and b1.projectid=? \n");
			sql.append("	and b1.taskid=? \n");
			sql.append("	and a1.taskid = b1.parenttaskid  \n");
			sql.append(") and a2.taskname=\"明细表\" \n");
			sql.append("and b2.projectid=? \n");
			sql.append("and b2.parenttaskid=a2.taskid \n");
			sql.append("and b2.taskname=? \n");

			params = new Object[]{projectId,projectId,projectId,taskId,projectId,taskName};

			break;
		default:
			throw new Exception("暂不支持，目前只支持1、2");
		}

		new DBConnect().changeDataBaseByProjectid(conn, projectId);
		return new DbUtil(conn).queryForString(sql.toString(), params);

	}


	/**
	 * 合并报表专用,根据taskId返回parentTaskName结点下面名字为taskName的底稿
	 * @param projectId
	 * @param taskId
	 * @param parentTaskName
	 * @param taskName
	 * @return
	 * @throws Exception
	 */
	public String getTaskId(String projectId, String taskId, String parentTaskName, String taskName) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append(" select taskId from z_task \n");
		sql.append(" where parenttaskId=(\n select taskId \n");
		sql.append("			from z_task \n");
		sql.append("			where parenttaskId=( \n");


		sql.append("				select b.parenttaskId from z_task a,z_task b \n");
		sql.append("				where a.taskId=? \n");
		sql.append("				and a.projectId=?	 \n");
		sql.append("				and b.projectId=?	 \n");
		sql.append("				and b.taskid=a.parenttaskid \n");

		sql.append(" 			) \n");
		sql.append(" 			and taskName=? 	and projectId="+projectId+" \n");
		sql.append(" ) \n");
		sql.append(" and taskName=? \n");
		sql.append(" and projectId=? \n");


		org.util.Debug.prtOut("qwh:getTaskId="+sql.toString()
				+"|taskId="+taskId
				+"|projectId="+projectId
				+"|parentTaskName="+parentTaskName
				+"|taskName="+taskName
				);


		Object[] params = new Object[]{taskId,projectId,projectId,parentTaskName,taskName,projectId};

		new DBConnect().changeDataBaseByProjectid(conn, projectId);
		return new DbUtil(conn).queryForString(sql.toString(), params);

	}


	/**
	 * 合并报表专用,根据taskId返回parentTaskName结点下面名字为taskName的底稿
	 * @param projectId
	 * @param taskId
	 * @param parentTaskName
	 * @param taskName
	 * @return
	 * @throws Exception
	 */
	public String getTaskCode(String projectId, String taskId, String parentTaskName, String taskName) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append(" select taskcode from z_task \n");
		sql.append(" where parenttaskId=(\n select taskId \n");
		sql.append("			from z_task \n");
		sql.append("			where parenttaskId=( \n");

		sql.append("				select b.parenttaskId from z_task a,z_task b \n");
		sql.append("				where a.taskId=? \n");
		sql.append("				and a.projectId=?	 \n");
		sql.append("				and b.projectId=?	 \n");
		sql.append("				and b.taskid=a.parenttaskid \n");

		sql.append(" 			) \n");
		sql.append(" 			and taskName=? 	and projectId="+projectId+" \n");
		sql.append(" ) \n");
		sql.append(" and taskName=? \n");
		sql.append(" and projectId=? \n");


		org.util.Debug.prtOut("qwh:getTaskCode="+sql.toString()
				+"|taskId="+taskId
				+"|projectId="+projectId
				+"|parentTaskName="+parentTaskName
				+"|taskName="+taskName
				);


		Object[] params = new Object[]{taskId,projectId,projectId,parentTaskName,taskName,projectId};

		new DBConnect().changeDataBaseByProjectid(conn, projectId);
		return new DbUtil(conn).queryForString(sql.toString(), params);

	}

	/**
	 *
	 * @param projectId
	 * @param taskId
	 * @param parentTaskName
	 * @param taskName
	 * @return
	 * @throws Exception
	 */
	public String getTaskCodeBySameNode(String projectId, String taskId, String taskName) throws Exception {
		StringBuffer sql = new StringBuffer();

		sql.append(" select taskcode from z_task \n");
		sql.append(" where parenttaskId=( 		 \n");
		sql.append("			select parenttaskId from z_task  \n");
		sql.append("			where taskId="+taskId+"  \n");
		sql.append("			and projectId=? \n");
		sql.append(") 	and projectId="+projectId+"  	and taskname='"+taskName+"'");

		org.util.Debug.prtOut("qwh:getTaskCodeBySameNode="+sql.toString());

		Object[] params = new Object[]{projectId};

		new DBConnect().changeDataBaseByProjectid(conn, projectId);
		return new DbUtil(conn).queryForString(sql.toString(), params);

	}



	public void deleteTree(String autoid) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select systemid,customerid from k_customerrelation where autoid = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, autoid);
			rs = ps.executeQuery();
			String customerid = "";
			String systemid = "";
			if(rs.next()){
				systemid  = rs.getString(1);
				customerid = rs.getString(2);

			}

			sql = "delete from k_customerrelation  where autoid=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, autoid);
			ps.execute();

			sql = "delete from  k_customercontrol where systemid=? and beconedcustomerid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, systemid);
			ps.setString(2, customerid);
			ps.execute();

			sql = "select autoid from k_customerrelation where parentid=?  ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, autoid);
			rs = ps.executeQuery();
			while(rs.next()){
				deleteTree(rs.getString(1));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public static void main(String[] args) {
		Connection conn = null;
		String url = "jdbc:mysql://192.168.1.2:5188/asdb?characterEncoding=GBK";
		String userName = "xoops_root";
		String password = "654321";


		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url, userName, password);
			new ReportService(conn).deleteTree("518");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

	}
}
