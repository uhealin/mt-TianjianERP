package com.matech.audit.service.task;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.audittypetemplate.AuditTypeTemplateService;
import com.matech.audit.service.datamanage.BackupUtil;
import com.matech.audit.service.datamanage.DataZip;
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.audit.service.task.model.Task;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.UTILString;
import com.matech.framework.pub.util.ZipUtil;

/**
 * <p>Title: 批量底稿导入</p>
 * <p>Description:
 * 		根据上传的zip包,
 * 		把zip包里面文件夹转成底稿结点,doc或xls文件转成底稿文件,
 * 		按照zip包里面文件存储结构保存到系统中,形成底稿树
 * </p>
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
 * 2007-6-29
 */
public class BatchUploadService {

	private Connection conn = null;

	private String projectId;					//当前项目ID
	private String taskProperty;				//结点属性

	private ZipUtil zipUtil;					//数据压缩类
	private ManuFileService manuScriptService;	//底稿管理类

	private Task task;
	private TaskService taskService;
	private TaskCommonService taskCommonService;
	
	private String taskCodeType;
	
	private String zero = "0";
	
	private boolean followParent = false;

	public boolean isFollowParent() {
		return followParent;
	}

	public void setFollowParent(boolean followParent) {
		this.followParent = followParent;
	}

	public String getTaskCodeType() {
		return taskCodeType;
	}

	public void setTaskCodeType(String taskCodeType) {
		this.taskCodeType = taskCodeType;
	}

	/**
	 * 构造方法,初始化私有成员变量
	 * @param conn
	 * @param projectId
	 * @throws Exception
	 */
	public BatchUploadService(Connection conn, String projectId) throws Exception{
		DbUtil.checkConn(conn);
		this.conn = conn;

		if ("".equals(projectId) || projectId == null) {
			throw new Exception("项目Id不能为空!");
		}

		this.projectId = projectId;

		try {
			this.manuScriptService = new ManuFileService(conn);
			this.zipUtil = new ZipUtil();

			this.taskService = new TaskService(conn, this.projectId);
			this.taskCommonService = new TaskCommonService(conn, this.projectId);

			//如果是风险导向项目,则把任务属性设置为tache,也就是环节
			if("1".equals(new AuditTypeTemplateService(conn).getPropertyByProjectId(projectId))) {
				this.taskProperty = "tache";
			}else {
				this.taskProperty = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 构造方法
	 * @param conn
	 * @throws Exception
	 */
	public BatchUploadService(Connection conn)throws Exception {
		DbUtil.checkConn(conn);
		this.conn = conn;
	}

	/**
	 * 批量上传的主要方法
	 * @param filePath
	 * @param parentTaskId
	 * @throws Exception
	 */
	public void upload(String filePath, String parentTaskId) throws Exception {

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
				saveManu(fileList[i],parentTaskId);
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
	private void saveManu(File file,String parentTaskId) throws Exception {
		if(file.isDirectory()) {
			File fileList[] = file.listFiles();
			String taskId = newTask(file.getName(),parentTaskId);
			for(int i=0; i < fileList.length; i++) {
				saveManu(fileList[i],taskId);
			}
		} else {
			String fileName = file.getName().toLowerCase();
			
			  
			if(fileName.toLowerCase().indexOf(".html")>-1 
				|| fileName.toLowerCase().indexOf(".htm")>-1
				|| fileName.toLowerCase().indexOf(".jsp")>-1
				|| fileName.toLowerCase().indexOf(".asp")>-1
				|| fileName.toLowerCase().indexOf(".php")>-1
				|| fileName.toLowerCase().indexOf(".js")>-1
				|| fileName.toLowerCase().indexOf(".css")>-1
				|| fileName.toLowerCase().indexOf(".exe")>-1){
			
				return;
			}
			
		

			//将记录插到数据库中
			String taskId = newManuScript(fileName, parentTaskId);

			//将文件压缩后拷贝到项目底稿文件夹下
			byte[] byteData = zipUtil.fileToByteArray(file);
			byteData = zipUtil.gzipBytes(byteData);
			manuScriptService.saveFileByTaskId(this.projectId, taskId, byteData);
		}
	}

	/**
	 * 增加一个新结点
	 * @param taskName
	 * @param parentTaskId
	 * @return
	 * @throws Exception
	 */
	private String newTask(String taskName, String parentTaskId) throws Exception {
		//增加一个结点
		String fullPath = "";
		String taskCode = "";
		String taskid = getMaxTaskId();
		int lev=0;
		
		Task parentTask = new Task();
		
		if(!"0".equals(parentTaskId)) {
			parentTask = taskService.getTaskByTaskId(parentTaskId);
		}	
		
		String tempTaskName = taskName;
		
		if("2".equals(this.taskCodeType) || "3".equals(this.taskCodeType)) {
			try {
				String[] task = getTaskCodeByName(tempTaskName);
				taskCode = task[0];
				tempTaskName = task[1];
				
				if(this.followParent && !"0".equals(parentTaskId)) {
					taskCode = parentTask.getTaskCode() + "-" + taskCode;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		
		//如果根据文件名生成失败
		if(taskCode == null || "".equals(taskCode)) {		
			if("0".equals(parentTaskId)){
				tempTaskName = taskName;
		        taskCode = taskCommonService.getMaxTaskCodeByParentTaskId("0");
		        lev = 0;
		        //fullPath = taskid + "|";
			}else{
				taskCode = taskCommonService.getMaxTaskCodeByParentTaskId(parentTaskId);

				lev = parentTask.getLevel()+1;
				//fullPath = parentTable.getFullpath() + taskid + "|";
			}
			
			taskCode = UTILString.getNewTaskCode(taskCode);
		}		

		task = new Task();
		task.setTaskId(taskid);

		task.setTaskCode(taskCode);
		task.setTaskName(tempTaskName);
		task.setTaskContent(tempTaskName);
		task.setDescription(tempTaskName);
		task.setLevel(lev);
		task.setParentTaskId(parentTaskId);
		task.setProjectId(projectId);
		task.setFullPath(fullPath);
		task.setProperty(this.taskProperty);
		task.setIsLeaf(0); //IsLeaf 的值，暂时为0, 0为不是叶子  1为叶子
		task.setOrderId(UTILString.getOrderId(task.getTaskCode()));

		taskService.addTask(task);

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
	private String newManuScript(String taskName, String parentTaskId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String taskCode = null;
		String fullPath = "";
		String taskid = null;

		try {
			new DBConnect().changeDataBaseByProjectid(this.projectId, conn);

			Task parentTask = new Task();
			
			if(!"0".equals(parentTaskId)) {
				parentTask = taskService.getTaskByTaskId(parentTaskId);
			}	
			
			int lev =0 ;

			//找出最大的taskID
			taskid = getMaxTaskId();
			
			String tempTaskName = taskName;
			
			if("2".equals(this.taskCodeType) || "3".equals(this.taskCodeType)) {
				try {
					String[] task = getTaskCodeByName(tempTaskName);
					taskCode = task[0];
					tempTaskName = task[1];
					
					if(this.followParent && !"0".equals(parentTaskId)) {
						taskCode = parentTask.getTaskCode() + "-" + taskCode;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
			
			//如果根据文件名生成失败
			if(taskCode == null || "".equals(taskCode)) {	
				tempTaskName = taskName;
				
				//判断父结点是否为根结点
				if("0".equals(parentTaskId)){
			        taskCode = taskCommonService.getMaxTaskCodeByParentTaskId("0");
			        lev = 0;
			        //fullPath = taskid + "|";
				}else{
					taskCode = taskCommonService.getMaxTaskCodeByParentTaskId(parentTaskId);
					
					lev = parentTask.getLevel()+1;
					//fullPath = parentTable.getFullpath() + taskid + "|";
				}
				taskCode = UTILString.getNewTaskCode(taskCode);
				
			}

			String sql = "INSERT INTO z_Task "
						+ " (Taskid,Taskcode,TaskName,TaskContent, "
						+ " Description,ParentTaskID,ProjectID,IsLeaf, "
						+ " level0,ismust,Fullpath,Property,OrderId) "
						+ " VALUES(?,?,?,?,   ?,?,?,?,   ?,?,?,?,?)";

			ps = conn.prepareStatement(sql);
			ps.setString(1, taskid);
			ps.setString(2, taskCode);
			ps.setString(3, tempTaskName);
			ps.setString(4, tempTaskName);

			ps.setString(5, tempTaskName);
			ps.setString(6, parentTaskId);
			ps.setString(7, projectId);
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
	 * 2、空格分隔：FA 短期借款.xls
	 * 3、数字、字母与中文分隔：FA短期借款.xls、1-FA短期借款.xls、20FA短期借款.xls、01短期借款.xls
	 * 根据底稿文件名生成taskCode和底稿名称
	 * @param taskName
	 * @return
	 */
	private String[] getTaskCodeByName(String taskName) throws Exception {

		String[] task = {"",""};
		
		if("2".equals(this.taskCodeType)) {
			task = taskName.split(" ");
			
			if(task.length < 2) {
				return null;
			}
		} else if("3".equals(this.taskCodeType)) {
			int index = getCNIndex(taskName);
			if(index > 0) {
				task[0] = taskName.substring(0,index).trim();
				task[1] = taskName.substring(index,taskName.length()).trim();
				
				System.out.println(task[0]);
				System.out.println(task[1]);
			} else {
				return null;
			}
		}
		
		try {
			int tempZero = Integer.parseInt(this.zero);
			
			String temp = "";
			
			for(int i=0; i < tempZero; i++) {
				temp += "0";
			}
			
			int taskcode = Integer.parseInt(task[0]);
			
			if(!"".equals(temp)) {
				DecimalFormat df = new DecimalFormat(temp);
				System.out.println(task[0]);
				task[0] = df.format(taskcode);
			}			
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		
		return task;
	}
	
	private int getCNIndex(String str) {
		int index = 0;
		for(int i=0; i < str.length(); i++) {
		    if(str.substring(i, i+1).matches("[\u4e00-\u9fa5]")) {
		    	index = i;
		    	break;
		    }
		}
		
		System.out.println(str + " -> " + str.substring(0,index));
		return index;
	}
	
	/**
	 * 获得当前项目最大的taskId
	 * @return
	 */
	private String getMaxTaskId() throws Exception{
		new DBConnect().changeDataBaseByProjectid(this.projectId, conn);
		DbUtil dbUtil = new DbUtil(conn);

		String sql = "select ifnull(max(taskid)+1, 1) from z_task";
		return dbUtil.queryForString(sql);
	}

	public static void main(String[] args) throws Exception {

		Connection conn = null;
		try {
			conn = new DBConnect().getConnect("");
			//String filePath = "c:/temp/1.zip";
			BatchUploadService batchUpload = new BatchUploadService(conn,"2007772");
			//batchUpload.upload(filePath,"0");
			batchUpload.setTaskCodeType("3");
			batchUpload.setZero("3");

			String taskName = "111短期借款.xls";
			String[] task = batchUpload.getTaskCodeByName(taskName);
			
			System.out.println(task[0] + "," + task[1]);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}	
	}

	public String getZero() {
		return zero;
	}

	public void setZero(String zero) {
		this.zero = zero;
	}
}
