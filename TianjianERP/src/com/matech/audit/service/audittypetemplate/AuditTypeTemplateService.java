package com.matech.audit.service.audittypetemplate;

import java.io.File;
import java.io.Writer;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.audittypetemplate.model.AuditTemplateTask;
import com.matech.audit.service.audittypetemplate.model.AuditTypeTemplate;
import com.matech.audit.service.datamanage.BackupUtil;
import com.matech.audit.service.datamanage.DataZip;
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.pub.util.UTILString;
import com.matech.framework.pub.util.ZipUtil;

/**
 * <p>Title: 审计类型模版类</p>
 * <p>Description: 审计类型模版类的增删改查</p>
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
public class AuditTypeTemplateService {

	private Connection conn;

	private ZipUtil zipUtil;

	private Map parameters = null;

	private ManuFileService manuScriptService;	//底稿管理类

	//设置允许用户上传文件大小,单位:字节
	//共10M
	private long sizeMax = 1000485760;


	public AuditTypeTemplateService(Connection conn) throws Exception {
		DbUtil.checkConn(conn);
		this.conn = conn;
		this.zipUtil = new ZipUtil();
		this.manuScriptService = new ManuFileService(conn);
	}

	/**
	 * 删除审计模版
	 * @param typeId
	 * @throws Exception
	 */
	public void removeAuditTypeTemplate(String typeId) throws Exception {

		if (typeId == null) {
			throw new Exception("模版类型typeId不能为空");
		}

		DbUtil dbUtil = new DbUtil(conn);
		Object params[] = new Object[] {typeId};
		String sql = "select count(1) from `z_project` where AuditType=?";

		if (dbUtil.queryForInt(sql, params) > 0) {
			throw new Exception("该审计类型有相关的项目引用，不能删除。");
		} else if(!typeId.equals("0")){
			List list = new ArrayList();

			//从审计模版表中删除
			list.add("delete from asdb.k_audittypetemplate where TypeID = ? ");

			//从审计程序模版表中删除
			list.add("delete from asdb.k_proceduretemplate where TypeID = ? ");

			//从审计目标模版表中删除
			list.add("delete from asdb.k_targettemplate where TypeID = ? ");

			//从底稿任务模版表中删除
			list.add("delete from asdb.k_tasktemplate where TypeID = ? ");

			//从公式表中删除
			list.add("delete from asdb.k_areafunction where TypeID = ? ");

			//从底稿引用表中删除
			list.add("delete from asdb.k_taskrefertemplate where TypeID = ? ");

			//底稿有公式的单元格
			list.add("delete from asdb.k_taskformulatemplate where TypeID = ? ");

			//底稿有公式的单元格
			list.add("delete from asdb.k_sheettasktemplate where TypeID = ? ");

			for(int i=0; i < list.size(); i++) {
				try {
					dbUtil.execute((String)list.get(i), params);
				} catch (Exception e) {
					System.out.println("执行sql出错:" + list.get(i));
					e.printStackTrace();
				}
			}

			//删除底稿目录
			ManuFileService.deleteDirByTypeID(typeId);
		}
	}

	/**
	 * 添加到底稿模版表
	 * @param tid
	 * @param pid
	 * @throws Exception
	 */
	public void addTaskTemplate(String tid, String pid) throws Exception {
		DbUtil dbUtil = new DbUtil(conn);
		String sql = "insert into k_tasktemplate (Taskid,TaskName,TaskContent,Description,ParentTaskID,TypeID,IsLeaf,Level0,Fullpath,ManuID)"
					+ " SELECT Taskid,TaskName,TaskContent,Description,ParentTaskID,?,IsLeaf,Level0,Fullpath,ManuID FROM z_task"
					+ " where ProjectID=?";

		Object pamars[] = new Object[]{tid,pid};
		new DBConnect().changeDataBaseByProjectid(pid, conn);
		dbUtil.execute(sql, pamars);
	}

	/**
	 * 新增审计模版
	 * @param auditTypeTemplate
	 * @throws Exception
	 */
	public void addAuditTypeTemplate(AuditTypeTemplate auditTypeTemplate) throws Exception {
		DbUtil dbUtil = new DbUtil(conn);
		String sql = "INSERT INTO k_AuditTypeTemplate(TypeID,TypeName,Content,des,Property,updateUser,updateTime,mypassword,vocationId) VALUES(?,?,?,?,?,?,now(),?,?)";
		Object params[] = new Object[] {
			auditTypeTemplate.getTypeid(),
			auditTypeTemplate.getTypename(),
			auditTypeTemplate.getContent(),
			auditTypeTemplate.getDes(),
			auditTypeTemplate.getProperty(),
			auditTypeTemplate.getUpdateUser(),
			auditTypeTemplate.getPassword(),
			auditTypeTemplate.getVocationId()
		};

		dbUtil.executeUpdate(sql, params);

		//复制公式
		String copyType = auditTypeTemplate.getCopyType();
		if (copyType == null || "".equals(copyType)
				|| "-1".equals(copyType)) {
			//不作任何的处理

		} else {
			//开始复制公式
			sql = "insert into k_areafunction(id,typeid,strsql,memo,classpath,HiddenCol) "
					+ "select distinct id,?,strsql,memo,classpath,HiddenCol "
					+ "from k_areafunction "
					+ "where typeid= ? ";

			params = new Object[] {
					auditTypeTemplate.getTypeid(),
					copyType
			};
			dbUtil.executeUpdate(sql, params);
		}
	}

	public void updateAuditTypeTemplate(AuditTypeTemplate auditTypeTemplate) throws Exception {
		DbUtil dbUtil = new DbUtil(conn);
		String sql = "update k_AuditTypeTemplate set "
					+ " TypeName=?,Content=?,des=?,Property=?,updateUser=?,updateTime=now(),vocationId=? "
					+ " where TypeID=?";
		Object params[] = new Object[] {
				auditTypeTemplate.getTypename(),
				auditTypeTemplate.getContent(),
				auditTypeTemplate.getDes(),
				auditTypeTemplate.getProperty(),
				auditTypeTemplate.getUpdateUser(),
				auditTypeTemplate.getVocationId(),
				
				auditTypeTemplate.getTypeid()
		};

		dbUtil.executeUpdate(sql, params);
	}

	/**
	 * 根据项目id获得模版
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public AuditTypeTemplate getAuditTypeTemplateByProjectId(String projectId)
			throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;
		ASFuntion CHF = new ASFuntion();
		AuditTypeTemplate auditTypeTemplate = new AuditTypeTemplate();

		try {
			String strSql = "select * from asdb.k_AuditTypeTemplate "
							+ " where TypeID = ( "
							+ " 	select AuditType from asdb.z_project "
							+ " 	where projectid = ?)";
			ps = conn.prepareStatement(strSql);
			ps.setString(1, projectId);
			rs = ps.executeQuery();
			if (rs.next()) {
				auditTypeTemplate.setTypeid(rs.getString("typeid"));
				auditTypeTemplate.setTypename(rs.getString("typename"));
				auditTypeTemplate.setProperty(rs.getString("property"));
				auditTypeTemplate.setContent(rs.getString("content"));
				auditTypeTemplate.setDes(CHF.showNull(rs.getString("des")));
				auditTypeTemplate.setUpdateTime(rs.getString("UpdateTime"));
				auditTypeTemplate.setUpdateUser(rs.getString("UpdateUser"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
		}

		return auditTypeTemplate;
	}

	public String getPropertyByProjectId(String projectId) throws Exception {
		String sql = "select property from asdb.k_AuditTypeTemplate "
						+ " where TypeID = ( "
						+ " 	select AuditType from asdb.z_project "
						+ " 	where projectid = ?)";
		Object[] params = new Object[] {projectId};
		DbUtil dbUtil = new DbUtil(conn);
		return dbUtil.queryForString(sql, params);
	}

	/**
	 * 修改模板密码
	 * @param typeId
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public boolean changePassword(String typeId, String password) throws Exception {
		DbUtil.checkConn(conn);
		boolean flag = false;
		PreparedStatement ps = null;
		String sql = "";
		try {
			sql = "update k_audittypetemplate set mypassword = ? where TypeID=?";
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, password);
			ps.setString(i++, typeId);
			ps.execute();
			flag = true;
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		return flag;
	}

	/**
	 * 查询密码
	 * @param typeId
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public String getPassword(String typeId) throws Exception {
		DbUtil.checkConn(conn);
		String result = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			sql = "select mypassword from k_audittypetemplate where TypeID=? and mypassword is not null and mypassword <> ''";
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, typeId);
			rs = ps.executeQuery();
			if(rs.next()) {
				result = rs.getString(1);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		return result;
	}

	/**
	 * 根据模板id获得模版
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public AuditTypeTemplate getAuditTypeTemplateByTypeId(String typeId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ASFuntion CHF = new ASFuntion();
		AuditTypeTemplate auditTypeTemplate = new AuditTypeTemplate();

		try {
			String strSql = "select * from k_AuditTypeTemplate where TypeID = ?";
			ps = conn.prepareStatement(strSql);
			ps.setString(1, typeId);
			rs = ps.executeQuery();
			if (rs.next()) {
				auditTypeTemplate.setTypeid(rs.getString("typeid"));
				auditTypeTemplate.setTypename(rs.getString("typename"));
				auditTypeTemplate.setProperty(rs.getString("property"));
				auditTypeTemplate.setContent(rs.getString("content"));
				auditTypeTemplate.setDes(CHF.showNull(rs.getString("des")));
				auditTypeTemplate.setUpdateTime(rs.getString("UpdateTime"));
				auditTypeTemplate.setUpdateUser(rs.getString("UpdateUser"));
				auditTypeTemplate.setPassword(rs.getString("mypassword"));
				auditTypeTemplate.setVocationId(rs.getString("vocationId"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
		}

		return auditTypeTemplate;
	}

	/**
	 * 返回最大的typeid + 1
	 * @return
	 * @throws Exception
	 */
	public int getMaxTypeId() throws Exception {
		DbUtil dbUtil = new DbUtil(conn);
		String sql = "select ifnull(max(typeid),0) + 1 from k_AuditTypeTemplate where typeid < 10000";
		return dbUtil.queryForInt(sql);
	}

	/**
	 * 该方法提供给备份恢复程序调用,如果模板中有大于10000的编号,就返回最大编号,如果没有,就返回最大编号+10000
	 * @return
	 * @throws Exception
	 */
	public int getMaxTypeIdByBackup() throws Exception {
		DbUtil dbUtil = new DbUtil(conn);
		String sql = " select if(maxtypeid > 10000,maxtypeid,maxtypeid + 10000) from ( "
				   + " select (ifnull(max(typeid),0) + 1) as maxtypeid "
				   + " from k_AuditTypeTemplate) a ";
		return dbUtil.queryForInt(sql);
	}

	/**
	 * 删除模板中的底稿
	 * @param typeId
	 * @param taskId
	 * @throws Exception
	 */
	public void removeTemplaeTask(String typeId, String taskId) throws Exception {
		String sql;
		ResultSet rs = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;

		try {
			if (taskId != null && !"".equals(taskId)) {

				//找到底稿唯一ID和底稿名称
				sql = "select manuid,taskName from asdb.k_tasktemplate "
					+ " where isleaf=1 "
					+ " and taskid= ? "
					+ " and TypeID= ? ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, taskId);
				ps.setString(2, typeId);
				rs = ps.executeQuery();

				if (rs.next()) {

					//删除底稿文件
					ManuFileService.deleteFileByTaskIdAndTypeID(taskId, typeId);

				}

				//从表页表中删除
				sql = "delete from asdb.k_sheettasktemplate "
					+ " where taskid= ? "
					+ " and typeId = ?";
				ps = conn.prepareStatement(sql);
				ps.setString(1, taskId);
				ps.setString(2, typeId);
				ps.execute();

				//从任务表中删除
				sql = "delete from asdb.k_tasktemplate "
					+ " where taskid= ? "
					+ " and typeId = ?";
				ps = conn.prepareStatement(sql);
				ps.setString(1, taskId);
				ps.setString(2, typeId);
				ps.execute();

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(ps2);
		}
	}

	/**
	 * 文件新增的处理方法
	 * @param typeId  或者是项目ID，或者是项目类型ID
	 * @return 返回非NULL的附件UNID字符串表示上传成功，返回NULL表示上传失败
	 */
	public String upload(String typeId, HttpServletRequest request) {
		// 定义一个HashMap，存放请求参数
		if (this.parameters == null) {
			parameters = new HashMap();
		}
		//上传项目只要足够小，就应该保留在内存里。
		//较大的项目应该被写在硬盘的临时文件上。
		//非常大的上传请求应该避免。
		//限制项目在内存中所占的空间，限制最大的上传请求，并且设定临时文件的位置。
		// Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload fu = new ServletFileUpload(factory);
		ManuFileService manuScriptService = new ManuFileService(conn);
		ASFuntion asfuntion = new ASFuntion();

		//设置允许用户上传文件大小,单位:字节
		//10M
		fu.setSizeMax(sizeMax);

		// 设置缓冲区大小，这里是4kb
		factory.setSizeThreshold(4096);

		Iterator iter = null;

		//读取上传信息
		try {
			List fileItems = fu.parseRequest(request);
			//处理上传项目
			//依次处理每个上传的文件
			iter = fileItems.iterator();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			return null;
		}

		/**
		 * 获取前台提交的部分值，请注意，这些值有可能通过需要request来取得，
		 * 也有可能需要通过form特殊域处理来取得
		 * 首先是分析request的代码段
		 */

		String taskId = "";
		String filename = "";

		String taskcode = asfuntion.showNull(request.getParameter("taskcode"));
		String taskProperty = asfuntion.showNull(request.getParameter("taskproperty")); //任务属性，1到9都是保留得固定任务；
		String subjectName = asfuntion.showNull(request.getParameter("subjectName"));
		//String ismust = asfuntion.showNull(request.getParameter("ismust"));
		String sql = "";
		String fileAddType = asfuntion.showNull(request.getParameter("addtype"));
		String taskContent = asfuntion.showNull(request.getParameter("taskContent"));
		String description = asfuntion.showNull(request.getParameter("description"));
		String taskProcess = asfuntion.showNull(request.getParameter("taskprocess"));
		String auditproperty = asfuntion.showNull(request.getParameter("auditproperty"));
		String manuid = asfuntion.showNull(request.getParameter("manuid"));
		

		byte[] byteData = null;

		PreparedStatement ps = null;
		ResultSet rs = null;

		String fieldFilename = ""; //文件域自己的文件名
		String paramFilename = asfuntion.showNull(request.getParameter("newfilename")); //输入框提供的文件名；
		Debug.print("paramFilename1=" + paramFilename);

		String myfilename = asfuntion.showNull(request.getParameter("myfilename")); //输入框提供的文件名；
		if (myfilename!=null && !myfilename.equals("")){
			paramFilename=myfilename;
		}
		Debug.print("myfilename=" + myfilename);

		/**
		 * 分析FORM特殊域的代码段
		 */
		try {


			//遍历取得所有域，为后面的分别处理做好准备，其中如果有上传文件，会把上传文件对应的item指针保留到attachItem
			FileItem attachItem = null;
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();

				//忽略其他不是文件域的所有表单信息
				if (!item.isFormField()) {
					//上传的是文件信息
					fieldFilename = item.getName();
					if ((fieldFilename == null) || fieldFilename.equals("")
							&& item.getSize() == 0) {
						continue;
					}
					//记住文件ITEM，已被后面处理
					/**
					 * 请注意，这种方法则意味着只支持一次上传一个附件
					 */
					attachItem = item;
				} else {

					try {
						//上传的是普通表单字域，这里读出放到map中备用
						String fieldName = item.getFieldName();
						String value = item.getString("utf-8"); //转换编码

						if ((fieldName == null) || fieldName.equals("")
								&& item.getSize() == 0) {
							continue;
						}

						if (fieldName.equals("newfilename")) {
							//对应新控件WinHTTPPostRequest的保存！
							paramFilename = URLDecoder.decode(value,"gbk");

							paramFilename=paramFilename.replaceAll("\n", "");

							paramFilename=paramFilename.replaceAll("\r", "");

							System.out.println("qwh:paramFilename="+paramFilename);

						}

						if (fieldName.equals("addtype")) {
							fileAddType = value;
						}

						if (fieldName.equals("taskcode")) {
							taskcode = value;
						}

						if (fieldName.equals("taskproperty")) {
							taskProperty = value;
						}

						if (fieldName.equals("subjectName") ) {
							subjectName = value;
						}

						if (fieldName.equals("taskContent") ) {
							taskContent = value;
						}

						if (fieldName.equals("description") ) {
							description = value;
						}

						if(fieldName.equals("taskprocess")) {
							taskProcess = value;
							//taskcode转换为taskid
							System.out.println("taskProcess====" + taskProcess);
							taskProcess = getTaskIdByTaskCode(taskProcess,typeId);
						}

						System.out.println("value:" + value);

						parameters.put(fieldName, value);

					} catch (Exception e) {
						e.printStackTrace();
					}

					//org.util.Debug.prtOut("fieldNames: " + fieldName + " ; " + value);
				}
			} //循环

			/**
			 * 文件名的生成逻辑是，如果前台有提供newfilename参数，则使用newfilename参数；
			 * 否则，直接使用附件名字(要去掉路径)；
			 */
			filename = "";
			if (paramFilename != null && !paramFilename.equals("")) {
				//用户提供了newfilename参数
				filename = paramFilename;
				Debug.print("filename1=" + filename);
			} else {
				//用户没提供，沿用文件名，但是要去掉路径
				filename = this.GetFileName(attachItem.getName()); //获取不带路径的文件名
				Debug.print("filename2=" + filename);
			}


			sql = "select ifnull(max(taskid),0)+1 from k_tasktemplate where typeid=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, typeId);
			rs = ps.executeQuery();
			if (rs.next()) {
				taskId = rs.getString(1);
			}
			rs.close();
			ps.close();

			String strSql = "INSERT INTO k_tasktemplate( "
						+ " Taskid,Taskcode,TaskName,TaskContent,Description, "
						+ " ParentTaskID,typeId,IsLeaf,Level0,ManuTemplateID, "
						+ " Fullpath,Property,OrderId,SubjectName,auditproperty, "
						+ " ManuID "
						+ ") VALUES( "
						+ " ?,?,?,?,?, "
						+ " ?,?,?,?,?, "
						+ " ?,?,?,?,?,"
						+ " ?)" ;

			ps = conn.prepareStatement(strSql);
			ps.setString(1, taskId);

			ps.setString(2, taskcode);
			ps.setString(3, filename);
			ps.setString(4, taskContent);
			ps.setString(5, description); //规程

			ps.setString(6, taskProcess); //所属流程
			ps.setString(7, typeId);
			ps.setInt(8, 1);
			ps.setInt(9, 1);
			ps.setLong(10, 0);

			ps.setString(11, taskId + "|");
			ps.setString(12, taskProperty);
			ps.setString(13, UTILString.getOrderId(taskcode));
			ps.setString(14, subjectName);
			ps.setString(15, auditproperty);
			
			ps.setString(16,manuid) ;

			ps.execute();

			//存储成文件
			try {
				if (fileAddType.equals("newexcel")) {
					manuScriptService.newFileByTypeIdAndTaskId(typeId,taskId, "0", "2");
				} else if (fileAddType.equals("newword")) {
					manuScriptService.newFileByTypeIdAndTaskId(typeId,taskId, "0", "1");
				} else if (fileAddType.equals("existfile")) {
					byteData = attachItem.get();
					byteData = new ZipUtil().gzipBytes(byteData);
					manuScriptService.saveFileByTaskId(typeId, taskId, byteData);
				}
			} catch (Exception e) {
				Debug.print(Debug.iError, "访问失败", e);
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			return null;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return taskId;
	}

	public String GetFileName(String filepath) {
		String returnstr = filepath;
		int length = filepath.trim().length();

		filepath = filepath.replace('\\', '/');
		if (length > 0) {
			int i = filepath.lastIndexOf("/");
			if (i >= 0) {
				filepath = filepath.substring(i + 1);
				returnstr = filepath;
			}
		}
		return returnstr;
	}

	/**
	 * 根据项目ID修复底稿全路径(level和fullpath错误时可以使用)
	 * 如果level正确,可以自己进mysql恢复:
	 *
	 * 1,恢复level=0的全路径:update `z_task`  set fullpath = concat(taskid,'|') where `Level0` = 0
	 * 2,恢复level=n(n必须按从小到大的顺序进行恢复)全路径:
	 *
	 * 		update `z_task` a,(select fullpath,taskid,projectid,`Level0` from `z_task` ) b
	 * 		set a.fullpath = concat(b.fullpath,a.taskid,'|')
	 * 		where  b.taskid = a.parenttaskid
	 * 		and a.projectid = b.projectid
	 * 		and a.Level0 = n
	 *
	 * @param projectId
	 * @throws Exception
	 */
	public void repairTaskFullPath(String typeId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {

			String sql = "select taskid from `k_tasktemplate` "
						+ " where typeId = ? "
						+ " and (property not like 'A%' or property is null) "
						+ " order by orderid";

			ps = conn.prepareStatement(sql);
			ps.setString(1, typeId);

			rs = ps.executeQuery();

			while (rs.next()) {
				String taskId = rs.getString(1);
				String fullPath = calFullPath(typeId,taskId);
				int level = fullPath.split("\\|").length - 1;
				sql = "update `k_tasktemplate` set "
						+ " fullpath= '" + fullPath + "', "
						+ " Level0= " + level + " "
						+ " where taskId = '" + taskId + "'"
						+ " and typeId = '" + typeId + "'";

				stmt = conn.createStatement();

				stmt.execute(sql);
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(stmt);
		}

	}

	/**
	 * 计算出底稿全路径
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	private String calFullPath(String taskId,String typeId) throws Exception {
		String fullPath = taskId + "|";
		String parentId = taskId;
		while (true) {
			parentId = getParentTaskId(typeId,parentId);
			if (parentId.equals("0")) {
				break;
			} else {
				fullPath = parentId + "|" + fullPath;
			}
		}
		return fullPath;
	}

	/**
	 * 根据任务id和项目id获得父结点的taskID
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public String getParentTaskId(String typeId, String taskId) throws Exception {
		String parentTaskId = "";
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			String strSql = "select parentTaskId from `k_tasktemplate` "
							+ " where taskid = ? "
							+ " and typeId = ?";

			ps = conn.prepareStatement(strSql);
			ps.setString(1, taskId);
			ps.setString(2, typeId);

			rs = ps.executeQuery();

			if (rs.next()) {
				parentTaskId = rs.getString(1);
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return parentTaskId;
	}

	public String getTaskProperty(String typeId, String taskId) throws Exception {
		String sql = " select property "
					+ " from asdb.k_tasktemplate "
					+ " where typeid=? "
					+ " and taskid=? ";

		Object[] args = new Object[]{typeId, taskId};

		String property = new DbUtil(conn).queryForString(sql,args);

		if(property == null || "null".equals(property)) {
			property = "";
		}

		return property;
	}

	/**
	 * 根据TaskId取得底稿详细信息
	 * @param taskId
	 * @return
	 */
	public AuditTemplateTask getTaskByTaskId(String taskId, String typeId) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		AuditTemplateTask task = new AuditTemplateTask();
		try {
			String sql = "Select * From k_tasktemplate "
						+ " Where TaskID = '" + taskId
						+ " ' and typeId = '" + typeId + "'";
			System.out.println(sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			if (rs.next()) {
				task.setTaskId(rs.getString("Taskid"));
				task.setTaskCode(rs.getString("TaskCode"));
				task.setTaskName(rs.getString("Taskname"));
				task.setTaskContent(rs.getString("TaskContent"));
				task.setDescription(rs.getString("Description"));

				task.setParentTaskId(rs.getString("ParentTaskID"));
				task.setTypeId(rs.getString("typeID"));
				task.setIsLeaf(rs.getInt("IsLeaf"));
				task.setLevel(rs.getInt("Level0"));
				task.setManuId(rs.getString("manuid"));

				task.setManuTemplateId(rs.getString("manutemplateid"));
				task.setProperty(rs.getString("property"));
				task.setFullPath(rs.getString("fullpath"));
				task.setSubjectName(rs.getString("subjectName"));
				task.setOrderId(rs.getString("orderid"));
				task.setDescription(rs.getString("description"));
				task.setAuditproperty(rs.getString("auditproperty"));

			}

			return task;
		} catch (Exception e) {
			Debug.print(Debug.iError, "获得底稿信息失败", e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 检查索引号是不是唯一
	 * @param typeId
	 * @param taskCode
	 * @return
	 * @throws Exception
	 */
	public String checkTaskCode(String typeId, String taskCode, String isleaf) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		String returnStr = "";
		try {
			sql = "select 1 from k_tasktemplate where typeId = ? and taskCode = ? isleaf=? ";
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, typeId);
			ps.setString(i++, taskCode);
			ps.setString(i++, isleaf);
			
			rs = ps.executeQuery();
			if(rs.next()) {
				returnStr = "NO";
			} else {
				returnStr = "OK";
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return returnStr;
	}

	public String checkTaskId(String typeId, String taskId) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		String returnStr = "";
		try {
			sql = "select 1 from k_tasktemplate where typeId = ? and taskId = ?";
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, typeId);
			ps.setString(i++, taskId);
			rs = ps.executeQuery();
			if(rs.next()) {
				returnStr = "YES";
			} else {
				returnStr = "NO";
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return returnStr;
	}

	/**
	 * 更新底稿
	 * @param task
	 * @throws Exception
	 */
	public void updateTask(AuditTemplateTask task) throws Exception {
		PreparedStatement ps = null;
		String sql;
		try {
			sql = "update k_tasktemplate set "
				+ " TaskName=?, TaskContent=?, Description=?, TaskCode=?, Orderid=?,parentTaskID=?,property=?,subjectName=?,auditproperty=?,manuid=? "
				+ " where TaskID = ? "
				+ " and typeId=? ";

			ps = conn.prepareStatement(sql);

			ps.setString(1, task.getTaskName());
			ps.setString(2, task.getTaskContent());
			ps.setString(3, task.getDescription());
			ps.setString(4, task.getTaskCode());
			ps.setString(5, UTILString.getOrderId(task.getTaskCode()));
			ps.setString(6, task.getParentTaskId());
			ps.setString(7, task.getProperty());
			ps.setString(8, task.getSubjectName());
			ps.setString(9, task.getAuditproperty());
			ps.setString(10, task.getManuId());
			ps.setString(11, task.getTaskId());
			ps.setString(12,task.getTypeId());

			ps.execute();

		} catch (Exception e) {
			Debug.print(Debug.iError, "更新底稿任务失败", e);
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 根据父底稿编号parentTaskId和是否叶子isleaf获取最大的taskcode
	 * 原方法：getMaxTaskCodeByIsLeaf(String ptaskId, String typeId, int isleaf);
	 *
	 * @param parentTaskId  底稿编号
	 * @param isleaf 是否叶子
	 * @return
	 */
	public String getMaxTaskCodeByParentTaskId(String parentTaskId, int isleaf, String typeId) throws Exception {
		String maxTaskCode = "";
		String sql = "";

		if (parentTaskId == null
				|| typeId == null
				|| "".equals(parentTaskId)
				|| "".equals(typeId) ) {

			return maxTaskCode;
		}

		DbUtil dbUtil = new DbUtil(conn);
		//构造数据库查询语句
		sql = "select distinct a.taskcode from k_tasktemplate a, k_tasktemplate b "
			+ " where a.typeId = ? "
			+ " and b.typeId = ? "
			+ " and b.taskid= ? "
			+ " and a.isleaf = ? "
			+ " and a.parenttaskid=b.taskid "
			+ " order by a.orderid desc";

		Object[] params = new Object[] {
				typeId,
				typeId,
				parentTaskId,
				new Integer(isleaf)
		};

		//如果parentTaskId为0
		if ("0".equals(parentTaskId)) {
			sql = "select taskcode from k_tasktemplate "
				+ " where typeId= ? "
				+ " and parenttaskid=0 "
				+ " order by orderid desc";
			params = new Object[] { typeId };

			//执行数据库的检索
			maxTaskCode = dbUtil.queryForString(sql,params);

			if(maxTaskCode == null) {
				maxTaskCode = "00";
			}

		} else {
			maxTaskCode = dbUtil.queryForString(sql,params);

			if(maxTaskCode == null) {
				sql = "select taskCode from k_tasktemplate where "
					+ " typeId = ? "
					+ " and taskId = ? ";
				params = new Object[] { typeId, parentTaskId};
				maxTaskCode = dbUtil.queryForString(sql, params);

				if(maxTaskCode != null) {
					maxTaskCode = maxTaskCode + "-0";
				}
			}

		}
		return maxTaskCode;
	}

	/**
	 * 获得最大的taskId
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	public String getMaxTaskId(String typeId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String taskId = null;

		try {
			String strSql = "select ifnull(max(taskid),0)+1 from k_tasktemplate where typeId = '" + typeId + "'";
			ps = conn.prepareStatement(strSql);
			rs = ps.executeQuery();
			if (rs.next()) {
				taskId = rs.getString(1);
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return taskId;
	}

	/**
	 * 添加流程
	 * @param task
	 * @throws Exception
	 */
	public void addProcess(AuditTemplateTask task) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String taskId = getMaxTaskId(task.getTypeId());
		try {
			String strSql = "INSERT INTO k_tasktemplate( "
						+ " Taskid,Taskcode,TaskName,TaskContent, "
						+ " ParentTaskID,typeId,IsLeaf,Property,orderid,manuid"
						+ ") VALUES( "
						+ " ?,?,?,?,?, "
						+ " ?,?,?,?,?)";

			ps = conn.prepareStatement(strSql);
			ps.setString(1, taskId);

			ps.setString(2, task.getTaskCode());
			ps.setString(3, task.getTaskName());
			ps.setString(4, task.getTaskContent());
			ps.setString(5, task.getParentTaskId());

			ps.setString(6, task.getTypeId()); //所属流程
			ps.setString(7, "0");
			ps.setString(8, task.getProperty());
			ps.setString(9, UTILString.getOrderId(task.getTaskCode()));
			ps.setString(10,task.getManuId()) ;
			ps.execute();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 根据parentTaskId返回该节点下的子结点和底稿数
	 * @param parentTaskId
	 * @return
	 * @throws Exception
	 */
	public int getChildrenCountByParentTaskId(String parentTaskId, String typeId) throws Exception {
		DbUtil dbUtil = new DbUtil(conn);

		String sql = "select count(*) from k_tasktemplate "
					+ " where parenttaskid=? "
					+ " and typeId=? ";

		Object[] params = new Object[] {
				parentTaskId,
				typeId
		};

		return dbUtil.queryForInt(sql, params);
	}

	/**
	 * 根据父节点taskid返回底稿列表
	 * @param parentTaskId
	 * @return
	 * @throws Exception
	 */
	public List getTaskListByParentTaskId(String parentTaskId,String typeId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		//updateOrderId();

		List taskList = new ArrayList();
		AuditTemplateTask task = null;

		try {
			String sql = "select distinct a.* from k_tasktemplate a,k_tasktemplate b "
						+ " where a.typeId= ? "
						+ " and b.typeId=? "
						+ " and b.taskid=? "
						+ " and a.FullPath like concat(b.fullpath,'%') "
						+ " order by a.orderid";

			ps = conn.prepareStatement(sql);
			ps.setString(1, typeId);
			ps.setString(2, typeId);
			ps.setString(3, parentTaskId);

			rs = ps.executeQuery();

			while (rs.next()) {
				task = new AuditTemplateTask();
				task.setTaskId(rs.getString("Taskid"));
				task.setTaskCode(rs.getString("TaskCode"));
				task.setTaskName(rs.getString("Taskname"));
				task.setTaskContent(rs.getString("TaskContent"));
				task.setDescription(rs.getString("Description"));
				task.setParentTaskId(rs.getString("ParentTaskID"));
				task.setTypeId(rs.getString("typeId"));
				task.setIsLeaf(rs.getInt("IsLeaf"));
				task.setLevel(rs.getInt("Level0"));
				task.setManuId(rs.getString("manuid"));
				task.setManuTemplateId(rs.getString("manutemplateid"));

				task.setProperty(rs.getString("property"));
				task.setFullPath(rs.getString("fullpath"));
				taskList.add(task);
			}

		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return taskList;
	}

	/**
	 * 修复全路径
	 * @param typeId
	 * @throws Exception
	 */
	public void repairFullPath(String typeId) throws Exception {
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		String sql = "";
		try {
			sql = "update k_tasktemplate set fullpath=taskid,level0=1 where parenttaskid=0 and typeid='" + typeId + "'";
			ps = conn.prepareStatement(sql);
			ps.executeUpdate();
			boolean flag = true;
			int level = 1;
			while(flag) {
				sql = "update k_tasktemplate a,k_tasktemplate b\n"
					+ "set a.fullpath=concat(b.fullpath,'|',a.taskId),\n"
					+ "a.level0 = b.level0+1\n"
					+ "where a.typeId='" + typeId + "' and b.typeId='" + typeId
					+ "' and a.parenttaskId=b.taskid and b.level0='" + level + "'";
				ps = conn.prepareStatement(sql);
				level ++;
				if(ps.executeUpdate()==0) {
					flag = false;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 根据taskcode获得任务详细信息
	 * @param taskCode
	 * @return
	 */
	public AuditTemplateTask getTaskByTaskCode(String taskCode,String typeId) throws Exception {

		String taskId = getTaskIdByTaskCode(taskCode,typeId);

		return getTaskByTaskId(taskId,typeId);
	}

	/**
	 * 根据TASKCODE获得taskId
	 * @param taskCode
	 * @return
	 * @throws Exception
	 */
	public String getTaskIdByTaskCode(String taskCode,String typeId) throws Exception {
		DbUtil dbUtil = new DbUtil(conn);
		String sql = "select taskId from k_tasktemplate "
				+ " where TaskCode = ? "
				+ "	and typeid= ? "
				+ " order by isleaf desc ";

		Object[] object = new Object[] {
				taskCode,
				typeId
		};
		return dbUtil.queryForString(sql, object);
	}

	public String getPath(String parentTaskId, String typeId) throws Exception {
		if(parentTaskId==null||"".equals(parentTaskId)) {
			return "";
		}
		if("0".equals(parentTaskId)) {
			return "->根目录";
		}
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "select fullpath from k_tasktemplate where typeId='"+typeId+"' and taskId='" + parentTaskId + "'";
		String fullpath = "";
		String returnStr = "";
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()) {
				fullpath = rs.getString(1);
				String[] ss = fullpath.split("\\|");
				for(int i=0 ; i<ss.length; i++) {
					String temp = getTaskByTaskId(ss[i],typeId).getTaskName();
					if(temp!=null&&!"".equals(temp)) {
						returnStr += "->" + temp;
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return returnStr;
	}


	/**
	 * 从数据库中取得最大的TaskCode
	 * 原方法：getMaxTaskCode(String ptaskId, String typeId);
	 *
	 * 前记：
	 * 1、一般情况下是根据父任务编号、当前项目编号来获得当前级别的最大任务编码，如果当前级别的
	 *   的记录集为空，意味着该父任务编号下没有子任务结点，则取父任务编码。
	 * 2、父任务编号为0，意味着它是最顶层的任务结点，则取与其同级的任务结点集中最大的任务编码。
	 *
	 * @param parentTaskId String
	 * @return String
	 */
	public String getMaxTaskCodeByParentTaskId(String parentTaskId, String typeId) throws Exception{
		String maxTaskCode = "";
		String sql = "";

		if (parentTaskId == null
				|| typeId == null
				|| "".equals(parentTaskId)
				|| "".equals(typeId) ) {

			return maxTaskCode;
		}

		DbUtil dbUtil = new DbUtil(conn);
		//构造数据库查询语句
		sql = "select distinct a.taskcode from k_tasktemplate a, k_tasktemplate b "
			+ " where a.typeId = ? "
			+ " and b.typeId = ? "
			+ " and b.taskid= ? "
			+ " and a.Level0=b.Level0+1 "
			+ " and a.parenttaskid=b.taskid "
			+ " order by a.orderid desc";

		Object[] params = new Object[] { typeId, typeId, parentTaskId };

		//如果parentTaskId为0
		if ("0".equals(parentTaskId)) {
			sql = "select taskcode from k_tasktemplate "
				+ " where typeId= ? "
				+ " and parenttaskid=0 "
				+ " order by orderid desc";
			params = new Object[] { typeId };

			//执行数据库的检索
			maxTaskCode = dbUtil.queryForString(sql,params);

			if(maxTaskCode == null) {
				maxTaskCode = "00";
			}

		} else {
			maxTaskCode = dbUtil.queryForString(sql,params);

			if(maxTaskCode == null) {
				sql = "select taskCode from k_tasktemplate where "
					+ " typeId = ? "
					+ " and taskid = ? ";
				params = new Object[] { typeId, parentTaskId};
				maxTaskCode = dbUtil.queryForString(sql, params);

				if(maxTaskCode != null) {
					maxTaskCode = maxTaskCode + "-0";
				}
			}

		}
		return maxTaskCode;

	}

	/**
	 * 批量上传的主要方法
	 * @param filePath
	 * @param parentTaskId
	 * @throws Exception
	 */
	public void upload(String filePath, String parentTaskId, String typeId) throws Exception {

		//临时解压输出目录
		File uploadFile = new File(filePath);
		String outputDirectory = uploadFile.getParent() + "/" + BackupUtil.getDateTime("yyyyMMddHHmmss");
		File file = new File(outputDirectory);

		try {
			new DataZip().unZipCHN(filePath, outputDirectory, true);
		} catch(Exception e) {
			throw new Exception("解压文件出错：" + e.getMessage());
		}

		try {
			File fileList[] = file.listFiles();


			//按照taskname 来排序
			java.util.Arrays.sort(fileList,new   java.util.Comparator(){
		        public   int   compare(Object   obj1,Object   obj2){
		        String   s1   =   ((java.io.File)obj1).getName(),
		        		 s2   =   ((java.io.File)obj2).getName();

		        //按taskcode来排序

		        s1=com.matech.framework.pub.util.UTILString.getOrderId(getTaskCode(s1));
		        s2=com.matech.framework.pub.util.UTILString.getOrderId(getTaskCode(s2));

		        return   s1.compareTo(s2);     //不就完了？
		        }
		    });



			for(int i=0; i < fileList.length; i++) {
				System.out.println("正在处理结点:" + fileList[i].getName());
				saveManu(fileList[i],parentTaskId,typeId);
			}
		} catch (Exception e) {
			throw new Exception("生成底稿出错：" + e.getMessage());
		} finally {
			ManuFileService.deleteFile(file);
		}
	}

	/**
	 * 把
	 * //out.println("getTaskCode="+getTaskCode("02-2-118应收账款附注表.xls")+"<br>");
	 * 返回  02-2-118
	 * //out.println("orderid="+com.matech.framework.pub.util.UTILString.getOrderId(getTaskCode("02-2-118应收账款附注表.xls"))+"<br>");
	 *
	 * @param fullname
	 * @return
	 */
	public String getTaskCode(String fullname) {

		int i=0;
		if(fullname !=null &&  !"".equals(fullname)){
			char c;

			for (i=0;i<fullname.length();i++){
				c=fullname.charAt(i);
				if ((c>='0' && c<='9') || c=='-'){

				}else{
					break;
				}
			}
		}

		if (fullname!=null && i<fullname.length()){
			return fullname.substring(0,i);
		}else{
			return "";
		}

	}

	/**
	 * 遍历文件夹,构建底稿树
	 * @param file
	 * @param parentTaskId
	 * @throws Exception
	 */
	private void saveManu(File file,String parentTaskId, String typeId) throws Exception {
		if(file.isDirectory()) {
			File fileList[] = file.listFiles();
			String taskId = newTask(file.getName(),parentTaskId, typeId);
			for(int i=0; i < fileList.length; i++) {
				saveManu(fileList[i],taskId,typeId);
			}
		} else {
			String fileName = file.getName().toLowerCase();
			if(fileName.indexOf(".xls") >= 0 ||
					fileName.indexOf(".doc") >= 0 ) {

				//将记录插到数据库中
				String taskId = newManuScript(fileName, parentTaskId, typeId);

				//将文件压缩后拷贝到项目底稿文件夹下
				byte[] byteData = zipUtil.fileToByteArray(file);
				byteData = zipUtil.gzipBytes(byteData);
				manuScriptService.saveFileByTaskId(typeId, taskId, byteData);
			}
		}
	}

	/**
	 * 增加一个新结点
	 * @param taskName
	 * @param parentTaskId
	 * @return
	 * @throws Exception
	 */
	private String newTask(String taskName, String parentTaskId, String typeId) throws Exception {
		//增加一个结点
		String fullPath = "";
		String taskCode = "";
		String taskid = getMaxTaskId(typeId);
		int lev;

		if("0".equals(parentTaskId)){
	        taskCode = getMaxTaskCodeByParentTaskId("0",typeId);
	        lev = 0;
	        //fullPath = taskid + "|";
		}else{
			taskCode = getMaxTaskCodeByParentTaskId(parentTaskId,typeId);

			AuditTemplateTask parentTask = getTaskByTaskId(parentTaskId,typeId);
			lev = parentTask.getLevel()+1;
			//fullPath = parentTable.getFullpath() + taskid + "|";
		}

		taskCode = UTILString.getNewTaskCode(taskCode);

		AuditTemplateTask task = new AuditTemplateTask();
		task.setTaskId(taskid);

		task.setTaskCode(taskCode);
		task.setTaskName(taskName);
		task.setTaskContent(taskName);
		task.setDescription(taskName);
		task.setLevel(lev);
		task.setParentTaskId(parentTaskId);
		task.setTypeId(typeId);
		task.setFullPath(fullPath);
		task.setIsLeaf(0); //IsLeaf 的值，暂时为0, 0为不是叶子  1为叶子
		task.setOrderId(UTILString.getOrderId(task.getTaskCode()));

		addTask(task);

		task = null;
		return taskid;
	}

	/**
	 * 增加一张新底稿
	 * @param taskName
	 * @param parentTaskId
	 * @return
	 * @throws Exception
	 */
	private String newManuScript(String taskName, String parentTaskId, String typeId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String taskCode = null;
		String fullPath = "";
		String taskid = null;

		try {
			int lev;

			//找出最大的taskID
			taskid = getMaxTaskId(typeId);

			//判断父结点是否为根结点
			if("0".equals(parentTaskId)){
		        taskCode = getMaxTaskCodeByParentTaskId("0",typeId);
		        lev = 0;
		        //fullPath = taskid + "|";
			}else{
				taskCode = getMaxTaskCodeByParentTaskId(parentTaskId,typeId);

				AuditTemplateTask parentTask = getTaskByTaskId(parentTaskId,typeId);
				lev = parentTask.getLevel()+1;
				//fullPath = parentTable.getFullpath() + taskid + "|";
			}

			taskCode = UTILString.getNewTaskCode(taskCode);

			String sql = "INSERT INTO k_tasktemplate "
						+ " (Taskid,Taskcode,TaskName,TaskContent, "
						+ " Description,ParentTaskID,typeId,IsLeaf, "
						+ " level0,ismust,Fullpath,Property,OrderId) "
						+ " VALUES(?,?,?,?,   ?,?,?,?,   ?,?,?,?,?)";

			ps = conn.prepareStatement(sql);
			ps.setString(1, taskid);
			ps.setString(2, taskCode);
			ps.setString(3, taskName);
			ps.setString(4, "");

			ps.setString(5, "");
			ps.setString(6, parentTaskId);
			ps.setString(7, typeId);
			ps.setInt(8, 1);

			ps.setInt(9, lev);
			ps.setString(10, "1");
			ps.setString(11, fullPath);
			ps.setString(12, "");
			ps.setString(13, UTILString.getOrderId(taskCode));

			ps.execute();

			System.out.println("保存底稿成功:" + taskCode + "|" + taskName);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return taskid;
	}

	/**
	 * 增加一张底稿,该方法会重新计算level,fullPath,orderid
	 * @param task
	 * @throws Exception
	 */
	public void addTask(AuditTemplateTask task) throws Exception {
		PreparedStatement ps = null;
		try {
			String parentTaskId = task.getParentTaskId();

			int level = task.getLevel();
			String fullPath = task.getFullPath();

			if("0".equals(parentTaskId)) {
				level = 0;
				fullPath = task.getTaskId() + "|";

			} else {
				AuditTemplateTask parentTask = getTaskByTaskId(parentTaskId,task.getTypeId());

				if(level == 0) {
					level = parentTask.getLevel() + 1;	//层次
				}

				if(fullPath == null) {
					fullPath = parentTask.getFullPath() + task.getTaskId() + "|";
				}
			}

			String sql = "INSERT INTO k_tasktemplate( "
						+ " Taskid,Taskcode,TaskName,TaskContent,Description,ParentTaskID, "
						+ " typeId,IsLeaf,Level0,Fullpath,Property,orderid) "
						+ " VALUES(?,?,?,?,?,?, ?,?,?,?,?,?)";

			ps = conn.prepareStatement(sql);

			ps.setString(1, task.getTaskId());
			ps.setString(2, task.getTaskCode());
			ps.setString(3, task.getTaskName());
			ps.setString(4, task.getTaskContent());
			ps.setString(5, task.getDescription());
			ps.setString(6, task.getParentTaskId());

			ps.setString(7, task.getTypeId());
			ps.setInt(8, task.getIsLeaf());
			ps.setInt(9, level);
			ps.setString(10, fullPath);
			ps.setString(11, task.getProperty());
			ps.setString(12, UTILString.getOrderId(task.getTaskCode()));

			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError, "增加底稿任务失败", e);
			throw e;
		} finally {
			DbUtil.close(ps);
		}

	}

	/**
	 * 根据项目ID修复底稿全路径(level和fullpath错误时可以使用)
	 * 如果level正确,可以自己进mysql恢复:
	 *
	 * 1,恢复level=0的全路径:update `z_task`  set fullpath = concat(taskid,'|') where `Level0` = 0
	 * 2,恢复level=n(n必须按从小到大的顺序进行恢复)全路径:
	 *
	 * 		update `z_task` a,(select fullpath,taskid,projectid,`Level0` from `z_task` ) b
	 * 		set a.fullpath = concat(b.fullpath,a.taskid,'|')
	 * 		where  b.taskid = a.parenttaskid
	 * 		and a.projectid = b.projectid
	 * 		and a.Level0 = n
	 *
	 * @param projectId
	 * @throws Exception
	 */
	public void repairTaskFullPath(Writer out, String typeId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Statement stmt = null;
		try {

			String sql = "select taskid from `k_tasktemplate` "
						+ " where typeId = ? "
						+ " and (property not like 'A%' or property is null) "
						+ " order by orderid";

			ps = conn.prepareStatement(sql);
			ps.setString(1, typeId);

			rs = ps.executeQuery();

			while (rs.next()) {
				String taskId = rs.getString(1);
				String fullPath = calFullPath(taskId,typeId);
				int level = fullPath.split("\\|").length - 1;
				sql = "update `k_tasktemplate` set "
						+ " fullpath= '" + fullPath + "', "
						+ " Level0= " + level + " "
						+ " where taskId = '" + taskId + "'"
						+ " and typeId = '" + typeId + "'";

				stmt = conn.createStatement();
				out.write(sql + "<br />");
				out.write("<script>");
				out.write("   window.scroll(0,document.body.scrollHeight);");
				out.write("</script>");
				out.flush();
				stmt.execute(sql);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(stmt);
		}

	}

	/**
	 * 移动底稿
	 * @param taskId
	 * @param newParentId
	 * @param typeId
	 * @throws Exception
	 */
	public void move(String taskId, String newParentId,String typeId) throws Exception {
		PreparedStatement ps = null;
		String sql = "";
		try {
			sql = "update k_tasktemplate set parentTaskId='" + newParentId + "' where taskId = '" + taskId + "' and typeId = '" + typeId + "'";
			System.out.print(sql);
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 拿到上一张底稿的信息
	 * @param orderid
	 * @return
	 * @throws Exception
	 */
	public Map getPrevTaskInfo(String taskId, String typeId) throws Exception {

		Map taskMap = new HashMap();

		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";

		try {
			sql = "select taskid,taskcode,taskname,orderid from k_tasktemplate "
				+" where typeId='"+typeId+"' and orderid < (select orderid from k_tasktemplate where typeid='" + typeId
				+  "' and taskId='"+ taskId + "') and isleaf=1 order by orderid desc limit 1 ";
			System.out.println("zyq1="+sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			if(rs.next()) {
				taskMap.put("taskid",rs.getString(1) );
				taskMap.put("taskcode",rs.getString(2) );
				taskMap.put("taskname",rs.getString(3));
				taskMap.put("orderid",rs.getString(4));
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
	 * 拿到下一张底稿的信息
	 * @param orderid
	 * @return
	 * @throws Exception
	 */
	public Map getNextTaskInfo(String taskId, String typeId) throws Exception {

		Map taskMap = new HashMap();

		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";

		try {

			sql = "select taskid,taskcode,taskname,orderid from k_tasktemplate "
				+" where typeId='"+typeId+"' and orderid > (select orderid from k_tasktemplate where typeid='" + typeId
				+  "' and taskId='"+ taskId + "') and isleaf=1 order by orderid asc limit 1 ";
			System.out.println("zyq2="+sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			if(rs.next()) {
				taskMap.put("taskid",rs.getString(1) );
				taskMap.put("taskcode",rs.getString(2) );
				taskMap.put("taskname",rs.getString(3));
				taskMap.put("orderid",rs.getString(4));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return taskMap;
	}

	public void repairTaskCode(String typeId) throws Exception {
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		Statement stmt = null;

		try {

			String sql = " select taskId,taskcode from k_tasktemplate "
					   + " where typeid=? "
					   + " and isleaf='0' "
					   + " order by orderid ";

			ps = conn.prepareStatement(sql);
			ps.setString(1, typeId);

			rs = ps.executeQuery();

			String taskCode = "";
			stmt = conn.createStatement();

			while (rs.next()) {
				taskCode = rs.getString(2);

				sql = " select taskId from k_tasktemplate where typeid=? and parenttaskid=? order by Level0 ";
				ps2 = conn.prepareStatement(sql);
				ps2.setString(1, typeId);
				ps2.setString(2, rs.getString(1));

				rs2 = ps2.executeQuery();

				int i=1;

				while(rs2.next()) {
					String sql2 = " update k_tasktemplate set taskcode='" + (taskCode + "-" + i) + "' "
								+ " where typeId='" + typeId + "' and taskid='" + rs2.getString(1) + "' ";
					System.out.println(sql2);
					i++;
					stmt.execute(sql2);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(rs2);
			DbUtil.close(ps);
			DbUtil.close(ps2);
			DbUtil.close(stmt);
		}

	}

	/**
	 * 取得底稿列表
	 * @param typeId
	 * @param parentTaskId
	 * @return
	 * @throws Exception
	 */
	public List getTaskList(String typeId, String parentTaskId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		List list = new ArrayList();
		Map map = new HashMap();

		try {

			String sql = " select a.taskid,a.taskcode,a.taskname,b.taskname "
						+ " from k_tasktemplate a, k_tasktemplate b "
						+ " where a.typeId =? "
						+ " and b.typeId=? "
						+ " and a.isleaf =0 "
						+ " and b.parenttaskId=? "
						+ " and a.parenttaskId=b.taskid "
						+ " order by a.orderid ";

			ps = conn.prepareStatement(sql);
			ps.setString(1, typeId);
			ps.setString(2, typeId);
			ps.setString(3, parentTaskId);
			rs = ps.executeQuery();

			while (rs.next()) {
				map = new HashMap();
				map.put("taskId", rs.getString(1));
				map.put("taskCode", rs.getString(2));
				map.put("taskName", rs.getString(3));
				map.put("parentTaskName", rs.getString(4));

				list.add(map);
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
	 * 重算任务ORDERID
	 * @param projectid
	 * @throws Exception
	 */
	public void updateOrderId(String typeId) throws Exception {
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		String sql = "select taskid,taskcode from k_tasktemplate where typeId = ? ";

		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, typeId);
			rs = ps.executeQuery();

			while (rs.next()) {
				String taskId = rs.getString(1);
				String taskcode = rs.getString(2);

				sql = "update k_tasktemplate set orderid = ? "
						+ " where taskid = ? "
						+ " and typeId = ? ";

				ps2 = conn.prepareStatement(sql);
				ps2.setString(1, UTILString.getOrderId(taskcode));
				ps2.setString(2, taskId);
				ps2.setString(3, typeId);

				ps2.execute();
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(ps2);
		}
	}
	
	/**
	 * 清理垃圾文件
	 * @param typeId
	 * @throws Exception
	 */
	public int clearFile(String typeId) throws Exception {
	
		int count = 0;
		try {
			
			String strSql = " select ifnull(group_concat(taskid),-1) from k_tasktemplate where typeid=? and isleaf=1 ";
			String taskIds = new DbUtil(conn).queryForString(strSql, new Object[]{typeId});
			
			taskIds = "," + taskIds + ",";
			String filePath = ManuFileService.getTemplateDir(typeId).getAbsolutePath();
			File templateDataFile = new File(filePath);
			
			System.out.println(filePath);
		
			BackupUtil backupUtil = new BackupUtil();
			
			String backupPath = BackupUtil.getDATABASE_PATH() + "../TemplateDataBackup/" + typeId + "/";
			
			if(templateDataFile.exists()) {
				
				File[] files = templateDataFile.listFiles();
				String taskId = "";
				for(int i=0; i < files.length; i++) {
					taskId = files[i].getName();
					
					//System.out.println(taskId);

					if(taskIds.indexOf("," + taskId + ",") < 0) {
						backupUtil.copyFiles(files[i], new File(backupPath + taskId + "_" + BackupUtil.getCurrentDateTime()));
						System.out.println("删除垃圾文件：" + taskId);
						ManuFileService.deleteFileByTaskIdAndTypeID(taskId, typeId);
						count++;
					}
				}
			}
			
			try {
				File recycleFile = new File(filePath + "/recycle");
				ManuFileService.deleteFile(recycleFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return count;
	}
	
	//保存k_dic
	public void saveDic() throws Exception{
		DbUtil dbUtil = new DbUtil(conn);
		String sql = "delete from k_dic where ctype = '审计类型' ";
		dbUtil.executeUpdate(sql);
		
		sql = "SELECT GROUP_CONCAT(DISTINCT content) as content FROM k_AuditTypeTemplate WHERE typeId<>'0' AND Content<>'合并报表' ";
		String content = dbUtil.queryForString(sql);
		if("".equals(content)) return ;
		String [] str = content.split(",");
		sql = "";
		for(int i = 0;i<str.length;i++){
			sql += "union select '"+str[i]+"' as name,'"+str[i]+"' as value,'审计类型' as ctype ";
		}
		sql = sql.substring(5);
		
		sql = "insert into k_dic (name,value,ctype) " +
		"	select * " +
		"	from (" +
		sql +
		"	) a " ;
		dbUtil.executeUpdate(sql);
	}

	public static void main(String[] args) throws Exception{
		System.out.println("53".split("\\|").length);
		System.out.println("53".split("\\|")[0] + "********" + "53".split("|")[1]);
		Connection c = new DBConnect().getConnect("");
		AuditTypeTemplateService auditTypeTemplateService = new AuditTypeTemplateService(c);
		auditTypeTemplateService.repairTaskCode("2");
		auditTypeTemplateService.updateOrderId("2");
	}
}
