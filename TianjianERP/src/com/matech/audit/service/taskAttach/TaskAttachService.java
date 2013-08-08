package com.matech.audit.service.taskAttach;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.customer.CustomerService;
import com.matech.audit.service.datamanage.BackupUtil;
import com.matech.audit.service.datamanage.DataZip;
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.audit.service.taskAttach.model.TaskAttachTable;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.sys.UTILSysProperty;
import com.matech.framework.pub.util.ZipUtil;

/**
 * 底稿附件
 * 
 * @author yuanquan
 * 
 */
public class TaskAttachService {

	private Connection conn = null;

	private String projectId = null;
	
	public static final String TEMP_PATH = UTILSysProperty.SysProperty.getProperty("系统临时目录");

	public TaskAttachService(Connection conn, String projectId)
			throws Exception {
		DbUtil.checkConn(conn);
		this.conn = conn;

		if ("".equals(projectId) || projectId == null) {
			throw new Exception("项目Id不能为空!");
		}

		this.projectId = projectId;
	}

	/**
	 * 返回指定底稿附件编号的附件信息
	 * 
	 * @param attachId
	 * @return
	 * @throws Exception
	 */
	public TaskAttachTable getTaskAttachByAttachId(String attachId)
			throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		TaskAttachTable taskAttachTable = new TaskAttachTable();
		try {
			String sql;
			sql = " select attachId,attachCode,projectid,taskid,filename,filetempname,savedate,userId,orderid,property "
					+ " from z_attach where attachid=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, attachId);

			rs = ps.executeQuery();

			if (rs.next()) {
				taskAttachTable.setAttachId(rs.getString("attachId"));
				taskAttachTable.setAttachCode(rs.getString("attachcode"));
				taskAttachTable.setProjectid(rs.getString("projectid"));
				taskAttachTable.setTaskid(rs.getString("taskid"));
				taskAttachTable.setFilename(rs.getString("filename"));
				taskAttachTable.setSavedate(rs.getString("savedate"));
				taskAttachTable.setUserId(rs.getString("userid"));
				taskAttachTable.setOrderid(rs.getString("orderid"));
				taskAttachTable.setProperty(rs.getString("property"));
				taskAttachTable.setFiletempname(rs.getString("filetempname"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return taskAttachTable;
	}

	/**
	 * 删除底稿附件
	 * 
	 * @param attachId
	 * @throws Exception
	 */
	public void del(String attachId) throws Exception {
		PreparedStatement ps = null;

		try {
			String customerId = new CustomerService(conn).getCustomerIdByProjectId(projectId);
			String fileTempName = getTaskAttachByAttachId(attachId).getFiletempname();
			// 同时把对应的底稿删除
			deleteFileByAttachId(fileTempName, projectId, customerId);
	
			String sql = "delete from z_attach where attachid='" + attachId + "' ";
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 添加底稿附件
	 * 
	 * @param taskAttachTable
	 * @throws Exception
	 */
	public void add(TaskAttachTable taskAttachTable) throws Exception {
		PreparedStatement ps = null;

		int i = 1;
		try {
			String sql = " insert into z_attach  \n"
					+ " (attachcode, projectid, taskid, filename,filetempname,"
					+ " userid,orderid,property,savedate) values \n"
					+ " (?, ?, ?, ?,  ?, ?, ?, ?, now())";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, taskAttachTable.getAttachCode());
			ps.setString(i++, taskAttachTable.getProjectid());
			ps.setString(i++, taskAttachTable.getTaskid());
			ps.setString(i++, taskAttachTable.getFilename());
			ps.setString(i++, taskAttachTable.getFiletempname());// 添加时与文件名相同
			ps.setString(i++, taskAttachTable.getUserId());
			ps.setString(i++, taskAttachTable.getOrderid());
			ps.setString(i++, taskAttachTable.getProperty());
			ps.executeUpdate();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

	}
	
	//复制采样原件
	public void move(String fileTempName)throws Exception {
		
		String filePath =  TEMP_PATH + fileTempName;
		
		File file = new File(filePath);
		
		if(!file.exists()) {
			throw new Exception("上传文件不存在");
		}
		
		ZipUtil zipUtil = new ZipUtil();
		
		byte[] byteData = zipUtil.fileToByteArray(file);
		
		byteData = zipUtil.gzipBytes(byteData);
		
		String projectPath = this.projectId;
		if("-1".equals(this.projectId)) {
			projectPath = "../";
		} 
		
		new ManuFileService(conn).saveAttachByProjectIdAndAttachId(projectPath, fileTempName, byteData);

	}
	
	/**
	 * 添加底稿附件
	 * 
	 * @param taskAttachTable
	 * @throws Exception
	 */
	public void save(TaskAttachTable taskAttachTable) throws Exception {
		
		String fileTempName = taskAttachTable.getFiletempname();
		if(!"".equals(fileTempName)) move(fileTempName);
		
		PreparedStatement ps = null;

		int i = 1;
		try {
			String sql = " insert into z_attach  \n"
					+ " (attachcode, projectid, taskid, filename,filetempname,"
					+ " userid,orderid,property,savedate) values \n"
					+ " (?, ?, ?, ?,  ?, ?, ?, ?, now())";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, taskAttachTable.getAttachCode());
			ps.setString(i++, taskAttachTable.getProjectid());
			ps.setString(i++, taskAttachTable.getTaskid());
			ps.setString(i++, taskAttachTable.getFilename());
			ps.setString(i++, taskAttachTable.getFiletempname());// 添加时与文件名相同
			ps.setString(i++, taskAttachTable.getUserId());
			ps.setString(i++, taskAttachTable.getOrderid());
			ps.setString(i++, taskAttachTable.getProperty());
			ps.executeUpdate();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

	}

	/**
	 * 修改底稿附件信息
	 * 
	 * @param taskAttachTable
	 * @throws Exception
	 */
	public void update(TaskAttachTable taskAttachTable) throws Exception {
		
		String fileTempName = taskAttachTable.getFiletempname();
		if(!"".equals(fileTempName)) move(fileTempName); //有新文件覆盖旧文件
		
		PreparedStatement ps = null;
		int i = 1;
		try {
			
			String sql = " update  z_attach "
						+ " set attachcode=?,filename=?,taskId=?, savedate=now(),"
						+ " userid=?,orderid=?,property=? "
						+ " where attachId=? ";
			ps = conn.prepareStatement(sql);
			ps.setString(i++, taskAttachTable.getAttachCode());
			ps.setString(i++, taskAttachTable.getFilename());
			ps.setString(i++, taskAttachTable.getTaskid());
			ps.setString(i++, taskAttachTable.getUserId());
			ps.setString(i++, taskAttachTable.getOrderid());
			ps.setString(i++, taskAttachTable.getProperty());
			ps.setString(i++, taskAttachTable.getAttachId());
			ps.executeUpdate();
			DbUtil.close(ps);
			
			//修改文件
			if(!"".equals(fileTempName)){
				
				String customerId = new CustomerService(conn).getCustomerIdByProjectId(projectId);
				String fileName = getTaskAttachByAttachId(taskAttachTable.getAttachId()).getFiletempname();
				// 同时把对应的底稿删除
				deleteFileByAttachId(fileName, projectId, customerId);
		
				i = 1;
				sql = "update  z_attach set fileTempName = ? where attachId=? ";
				ps = conn.prepareStatement(sql);
				ps.setString(i++, fileTempName);
				ps.setString(i++, taskAttachTable.getAttachId());
				ps.execute();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

	}

	/**
	 * 删除底稿附件时，同时删除文件
	 * 
	 * @param attachId
	 * @param projectId
	 * @param customerId
	 * @throws Exception
	 */
	public static void deleteFileByAttachId(String fileTempName, String projectId,String customerId) throws Exception {
		String dataBasePath = "";
		String MANU_SCRIPT_PATH = "";
		dataBasePath = BackupUtil.getDATABASE_PATH();
		dataBasePath += "../";

		MANU_SCRIPT_PATH = dataBasePath + "ManuScriptData/";
		try {
			//求出底稿附件的名称
			String path = MANU_SCRIPT_PATH + customerId + "\\" + projectId + "\\attach\\" + fileTempName;
			
			File file = new File(path);

			if (file.exists()) {
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

	}

	/**
	 * 取文件名
	 * 
	 * @param attachId
	 * @return
	 * @throws Exception
	 */
	public String getFileName(String attachId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		String fileName = "";
		String fileTempName = "";
		String fileType = "";
		sql = "select filename,filetempname from z_attach where projectid="
				+ this.projectId + " and attachid=" + attachId + " ";
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				fileName = rs.getString("filename");
				fileTempName = rs.getString("filetempname");
			}

			fileType = fileTempName.substring(fileTempName.lastIndexOf("."),
					fileTempName.length());
			if (fileName.indexOf(fileType) == -1) {
				fileName = fileName + fileType;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return fileName;

	}
	
	/**
	 * 检查索引是否存在
	 * @param projectId
	 * @param taskId
	 * @param attachCode
	 * @return
	 * @throws Exception
	 */
	public String checkAttachCode(String projectId, String taskId, String attachCode) throws Exception {
		DbUtil.checkConn(conn);
		String result = "no";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select 1 from z_attach where projectId = ? and taskId = ? and attachCode = ?";
			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++, projectId);
			ps.setString(i++, taskId);
			ps.setString(i++, attachCode);
			rs = ps.executeQuery();
			if(rs.next()) {
				result = "yes";
			} else {
				result = "no";
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return result;
	}

	/**
	 * 创建要下载的临时文件
	 */
	public String createTempAttachFile() throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String customerId = new CustomerService(conn).getCustomerIdByProjectId(projectId);
			
			String dataBasePath = "";
			String MANU_SCRIPT_PATH = "";
			dataBasePath = BackupUtil.getDATABASE_PATH();
			dataBasePath += "../";
			MANU_SCRIPT_PATH = dataBasePath + "ManuScriptData/";
			String path = MANU_SCRIPT_PATH + customerId + "\\" + projectId + "\\attach\\" ;//+ fileTempName;
			
			String rand = DELUnid.getNumUnid();
			String temp = TEMP_PATH + rand;
			if (!new File(temp).exists()) {
	            new File(temp).mkdirs();
	        }
			
			FileOutputStream os = null;
			
			String sql = "select * from z_attach where projectid = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, projectId);
			rs = ps.executeQuery();
			while(rs.next()){
				String fileName = rs.getString("fileName") + ".jpg";
				String fileTempName = rs.getString("fileTempName");     
				
				String filePath = path + fileTempName;
				
				byte[] fileByte = new byte[0];
				File file = new File(filePath);
				if (file.exists()) {
					fileByte = new ZipUtil().fileToByteArray(file);
				}
				fileByte = new ZipUtil().ungzipBytes(fileByte);
				if (fileByte != null && fileByte.length > 0) {
					os = new FileOutputStream(new File(temp + "\\" + fileName));
					os.write(fileByte);
                    os.close();
				}
			}
			
    		List list = new ArrayList();
    		list.add("");
			new DataZip().zip(temp,TEMP_PATH + rand+".zip", list);
			
			return TEMP_PATH + rand+".zip";
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
	}
	
	
	public static void main(String[] args) throws Exception {
		Connection conn = new DBConnect().getConnect("100003");
		String projectId = "20090114";
		TaskAttachService taskAttachService = new TaskAttachService(conn,
				projectId);
		
		TaskAttachTable taskAttachTable = new TaskAttachTable();
		taskAttachTable.setFiletempname("1296115040396.jpg");
		taskAttachService.save(taskAttachTable);
	}
}
