package com.matech.audit.service.fileupload;

import java.io.File;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.datamanage.BackupUtil;
import com.matech.audit.service.log.LogService;
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.audit.service.task.TaskCommonService;
import com.matech.audit.service.task.TaskRecycleService;
import com.matech.audit.service.task.TaskService;
import com.matech.audit.service.task.model.Task;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.net.Web;
import com.matech.framework.pub.sys.UTILSysProperty;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.pub.util.MyEncrypt;
import com.matech.framework.pub.util.UTILString;
import com.matech.framework.pub.util.ZipUtil;
import com.matech.framework.pub.zip.Zip;

/**
 * <p>Title: 处理文件上传(新增和修改替换原有文件)的类</p>
 * <p>Description: 处理文件上传(新增和修改替换原有文件)的类</p>
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
 * 2008-6-7
 */
public class MyFileUpload {
	private Connection conn = null;

	private String tempPath = UTILSysProperty.SysProperty.getProperty("系统临时目录");

	private Map parameters = null;

	private String fileRondomNames = "" ;

	private String fileNames = "" ;
	
	private boolean showProcess = false ;
	
	private  String uploadBeanName = "" ;
	 
	public String getFileNames() {
		return fileNames;
	}

	public void setFileNames(String fileNames) {
		this.fileNames = fileNames;
	}

	public String getFileRondomNames() {
		return fileRondomNames;
	}

	public void setFileRondomNames(String fileRondomNames) {
		this.fileRondomNames = fileRondomNames;
	}

	public Map getMap() {
		return this.parameters;
	}

	public void setMap(Map map) {
		this.parameters = map;
	}

	//获取的上传请求
	private HttpServletRequest request = null;

	//设置最多只允许在内存中存储的数据,单位:字节
	private int sizeThreshold = DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD;

	//设置允许用户上传文件大小,单位:字节,共10M
	private long sizeMax = 10004857600L;

	public void init() {
		try {
			if("".equals(tempPath) || tempPath == null) {
				tempPath = "c:/temp/";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 构造方法
	 * @param request
	 * @throws Exception
	 */
	public MyFileUpload(HttpServletRequest request) throws Exception {
		this.request = request;
		
		init();
	}

	/**
	 * 构造方法,从外部传链接进来
	 * @param request
	 * @param conn
	 * @throws Exception
	 */
	public MyFileUpload(HttpServletRequest request, Connection conn)
			throws Exception {
		this.request = request;
		this.conn = conn;
		
		init();
	}

	
	/**
	 * 预处理文件；主要是解密、解压；
	 * @param name  文件全路径 
	 * @param pwd   密码
	 * @return
	 * @throws Exception
	 */
	public String preHandleUploadData(String name , String pwd,String temp)
			throws Exception{

		//文件保存处理
		String filename = this.getFileName(name).toLowerCase(); //获取不带路径的文件名

		if (filename.indexOf(".zip") > 0) {
			//是zip文件，就解压
			org.util.Debug.prtOut("filename1="+filename);
			//Zip.extZipFileList(swapfile, temp);
			Zip.unZip(name, temp, false);
		}

		if (filename.indexOf(".mt_") > 0) {
			//是mt_文件，先解密再解压
			org.util.Debug.prtOut("filename2="+filename);

			//再解压
			try {
				//解密文件
				MyEncrypt my = new MyEncrypt();
				if (pwd == null || pwd.equals(""))
					pwd = "mtsoft";
				my.decryptFile(name, pwd,1024);
				
			} catch (Exception e) {
				Debug.print(Debug.iError, "访问失败", e);
				throw new Exception("密码错误");
			}
			//解压文件
			Zip.unZip(name + ".zip", temp, false);
		}

		if (filename.indexOf(".mtd_") > 0) {

			org.util.Debug.prtOut("filename3="+filename);

			//是mt_文件，先解密再解压
			//再解压
			try {
				//解密文件
				MyEncrypt my = new MyEncrypt();
				if (pwd == null || pwd.equals(""))
					pwd = "mtsoft";
				my.decryptFile(name, pwd,16000);
			} catch (Exception e) {
				Debug.print(Debug.iError, "访问失败", e);
				throw new Exception("密码错误");
			}
			//解压文件
			Zip.unZip(name + ".zip", temp, false);
		}


		if (filename.indexOf(".mt3_") > 0) {

			org.util.Debug.prtOut("filename4="+filename);

			//是mt_文件，先解密再解压
			//再解压
			try {
				//解密文件
				MyEncrypt my = new MyEncrypt();
				if (pwd == null || pwd.equals(""))
					pwd = "mtsoft";
				my.decryptFile3(name, pwd);
			} catch (Exception e) {
				Debug.print(Debug.iError, "访问失败", e);
				throw new Exception("密码错误");
			}
			//解压文件
			org.util.Debug.prtOut("filename5="+filename);
			//Zip.extZipFileList(swapfile + ".zip", temp);
			Zip.unZip(name + ".zip", temp, false);
			org.util.Debug.prtOut("filename6="+filename);
		}
		
		//记录文件名，用于生成客户信息
		return filename;
		
	}
	
	/**
	 * 为上传准备临时目录，如果目录不存在，会自己建立
	 * @return
	 */
	public String prepareTempDir() {
		
		String temp = this.tempPath + DELUnid.getNumUnid() + "\\";
		if (!new File(this.tempPath).exists()) {
			new File(this.tempPath).mkdir();
		}
		if (!new File(temp).exists()) {
			new File(temp).mkdir();
		}
		
		return temp;
	}
	
	/**
	 * 处理上传采集的数据文件的方法,先上传，再解压；
	 * @return 返回存放文件的目录的字符串表示上传并导入成功，返回空字符串表示失败
	 */
	public String UploadData() throws Exception {

		Iterator iterator = getFileItems();

		//检查临时目录是否存在,不存在就创建
		String temp = prepareTempDir();

		String pwd = "";
		String swapfile="";
		while (iterator.hasNext()) {
			FileItem item = (FileItem) iterator.next();

			//忽略其他不是文件域的所有表单信息
			if (!item.isFormField()) {
				//上传的是文件信息
				//String fieldName = item.getFieldName();
				String name = item.getName();
				if ((name == null) || name.equals("") && item.getSize() == 0) {
					continue;
				}

				try {
					//文件保存处理
					String filename = this.getFileName(name).toLowerCase(); //获取不带路径的文件名

					//先保存到临时目录
					swapfile = temp + filename;
					item.write(new File(swapfile));

					//记录文件名，用于生成客户信息
					parameters.put("filename", filename); 

					//密码是在文件选择的后面，所以这里不能直接解压，要等到pwd属性设置后才解压；
					//preHandleUploadData(swapfile,pwd,temp);
					
					//只有一个上传文件，所有处理完后直接退出，不用考虑其他事项
					//break;
				} catch (Exception e) {
					Debug.print(Debug.iError, "访问失败", e);
					throw e;
				}
			} else {

				//上传的是普通表单字域，这里只读出不做任何处理
				String fieldName = item.getFieldName();
				String value = item.getString("utf-8"); //转换编码,支持导账时指定客户名称

				parameters.put(fieldName, value);
				if (fieldName != null
						&& fieldName.equalsIgnoreCase("mypassword"))
					pwd = value;
			}

		} //循环

		try {
			//文件保存处理
			if (swapfile!=null && swapfile.length()>0){
				System.out.println("qwh:fileupload:密码："+pwd);
				preHandleUploadData(swapfile,pwd,temp);
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw e;
		}
		
		
		
		parameters.put("tempdir", temp);
		return temp;
	}

	/**
	 * 上传并保存文件到指定目录
	 *
	 * @param strFileName String 文件名，这个参数如果这是为NULL或者""，
	 *            则会使用上传文件的文件名作为文件名
	 * @param strDir String 目录路径，这个参数设置为NULL，或者""，
	 *            则会无条件在c:\temp\目录下新建一个临时目录（以避免并发冲突）；
	 *
	 * @return String  保存的文件的绝对路径
	 *            保存文件的文件名和路径，可以通过：
	 *            Map parameters=null;
	 *            MyFileUpload myfileUpload =new MyFileUpload(request);
	 *            uploadtemppath=myfileUpload.UploadFile();
	 *            parameters=myfileUpload.getMap();
	 *            filename=(String)parameters.get("filename");
	 *            tempdir=(String)parameters.get("tempdir");
	 *            clientFilePath=(String)parameters.get("clientFilePath");  //客户端上传文件的路径
	 *           来获取，其他表单内的变量也是采用类似方式。
	 */
	public String UploadFile(String strFileName, String strDir) {

		Iterator iterator = getFileItems();
		//检查临时目录是否存在,不存在就创建
		String temp = "";
		if (strDir == null || strDir.equals(""))
			temp = this.tempPath + DELUnid.getNumUnid() + "\\";
		else
			temp = strDir;
		if (!new File(this.tempPath).exists()) {
			new File(this.tempPath).mkdir();
		}
		if (!new File(temp).exists()) {
			new File(temp).mkdir();
		}
		while (iterator.hasNext()) {
			FileItem fileItem = (FileItem) iterator.next();

			//其他不是文件域的所有表单信息,会放到MAP中
			if (!fileItem.isFormField()) {
				//上传的是文件信息

				String name = fileItem.getName();
				if ((name == null) || name.equals("") && fileItem.getSize() == 0) {
					continue;
				}

				parameters.put("clientFilePath", name);
				String mime = fileItem.getContentType();
				try {
					//文件保存处理
					String filename = "";
					if (strFileName == null || strFileName.equals("")) {
						filename = this.getFileName(name); //获取不带路径的文件名
					} else {
						filename = strFileName;
					}

					//先保存到临时目录
					String swapfile = temp + filename;
					fileItem.write(new File(swapfile));

					//把最终的文件名页放到MAP中
					parameters.put("filename", filename);
					parameters.put("mime", mime);
					//只有一个上传文件，所有处理完后直接退出，不用考虑其他事项
					continue;
				} catch (Exception e) {
					Debug.print(Debug.iError, "访问失败", e);
					return "";
				}
			} else {

				try {
					//上传的是普通表单字域，这里只读出不做任何处理
					String fieldName = fileItem.getFieldName();

					String value = fileItem.getString("utf-8");
					parameters.put(fieldName, value);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		} //循环

		parameters.put("tempdir", temp);
		return temp;
	}

	/**
	 * 初始化并读取上传信息
	 * @return
	 */
	private Iterator getFileItems() {
		// 定义一个HashMap，存放请求参数
		if (this.parameters == null) {
			parameters = new HashMap();
		}

		DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
		ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);

		//设置允许用户上传文件大小,单位:字节,10M
		servletFileUpload.setSizeMax(sizeMax);
		
		if(this.showProcess) {
			servletFileUpload.setProgressListener(new UploadListener(request.getSession(),uploadBeanName));  
			request.getSession().setAttribute(uploadBeanName,initFileUploadStatusBean(request));   
	//		storeFileUploadStatusBean(request.getSession(),initFileUploadStatusBean(request)); 
		}
		
		// 设置缓冲区大小，这里是4kb
		diskFileItemFactory.setSizeThreshold(sizeThreshold);

		Iterator iterator = null;

		//读取上传信息
		try {
			List fileItems = servletFileUpload.parseRequest(request);

			//处理上传项目
			//依次处理每个上传的文件
			iterator = fileItems.iterator();
		} catch (Exception e) {
			Debug.print(Debug.iError, "读取上传信息失败", e);
		}

		return iterator;
	}

	/**
	 * 从路径中获取单独文件名
	 * @param filepath
	 * @return
	 */
	public String getFileName(String filepath) {
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
	 * 上传并保存文件到指定目录
	 *
	 * @param 		isUseDefautName boolean 是否用随机名字替换原来的文件名，
	 *           true为是,
	 * @param strDir String 目录路径，这个参数设置为NULL，或者""，
	 *            则会无条件在c:\temp\目录下新建一个临时目录（以避免并发冲突）；
	 *
	 * @return String  保存的文件的绝对路径
	 *            保存文件的文件名和路径，可以通过：
	 *            Map parameters=null;
	 *            MyFileUpload myfileUpload =new MyFileUpload(request);
	 *            uploadtemppath=myfileUpload.UploadFile();
	 *            parameters=myfileUpload.getMap();
	 *            filename=(String)parameters.get("filename");
	 *            tempdir=(String)parameters.get("tempdir");
	 *            clientFilePath=(String)parameters.get("clientFilePath");  //客户端上传文件的路径
	 *           来获取，其他表单内的变量也是采用类似方式。
	 */
	public String UploadFiles(boolean isUseDefautName, String strDir) {

		Iterator iterator = getFileItems();

		//检查临时目录是否存在,不存在就创建
		String temp = "";
		if (strDir == null || strDir.equals(""))
			temp = this.tempPath + DELUnid.getNumUnid() + "\\";
		else
			temp = strDir;
		if (!new File(this.tempPath).exists()) {
			new File(this.tempPath).mkdir();
		}
		if (!new File(temp).exists()) {
			new File(temp).mkdir();
		}
		int i = 0 ;
		while (iterator.hasNext()) {
			FileItem fileItem = (FileItem) iterator.next();

			//其他不是文件域的所有表单信息,会放到MAP中
			if (!fileItem.isFormField()) {
				//上传的是文件信息

				String name = fileItem.getName();
				if ((name == null) || name.equals("") && fileItem.getSize() == 0) {
					continue;
				}

				parameters.put("clientFilePath", name);
				String mime = fileItem.getContentType();

				fileNames += this.getFileName(name) + "," ;

				try {
					//文件保存处理
					String filename = "";
					if (isUseDefautName) {
						filename = this.getFileName(name); //获取不带路径的文件名
					} else {
						String randomName = DELUnid.getNumUnid()+i;
						filename = randomName;

						fileRondomNames += randomName + "," ;
					}

					//先保存到临时目录
					String swapfile = temp + filename;
					fileItem.write(new File(swapfile));

					//把最终的文件名页放到MAP中
					parameters.put("filename", filename);
					parameters.put("mime", mime);
					i++ ;
					continue;
				} catch (Exception e) {
					Debug.print(Debug.iError, "访问失败", e);
					return "";
				}
			} else {

				try {
					//上传的是普通表单字域，这里只读出不做任何处理
					String fieldName = fileItem.getFieldName();

					String value = fileItem.getString("utf-8");
					parameters.put(fieldName, value);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		} //循环

		if(!"".equals(fileNames) && !"".equals(fileRondomNames)) {
			fileNames = fileNames.substring(0,fileNames.length()-1) ;
			fileRondomNames = fileRondomNames.substring(0,fileRondomNames.length()-1) ;
		}

		parameters.put("tempdir", temp);
		return temp;
	}
	
	    /**  
	     * 初始化文件上传状态Bean  
	     * @param request  
	     * @return  
	     */  
	    private FileUploadStatus initFileUploadStatusBean(HttpServletRequest request){   
	        FileUploadStatus fUploadStatus=new FileUploadStatus();   
	        fUploadStatus.setStatus("正在准备处理");   
	        fUploadStatus.setUploadTotalSize(request.getContentLength());   
	        fUploadStatus.setProcessStartTime(System.currentTimeMillis());   
	     //   fUploadStatus.setBaseDir(request.getContextPath()+UPLOAD_DIR);   
	        return fUploadStatus;   
	    }

	

		public void setUploadProcess(boolean showProcess,String uploadBeanName) {
			this.showProcess = showProcess ;
			this.uploadBeanName = uploadBeanName;
		}

		/**
	 * 更新指定taskId和projectId或TypeId的底稿,返回taskId
	 *
	 * @param taskId
	 * @param projectIdOrTypeId
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public boolean UploadUpdate(String taskId, String projectIdOrTypeId, String username) throws Exception {

		Iterator iterator = getFileItems();

		/**
		 * 没有办法，如果前台是采用控件提交，则文件名和实际文件名会不一致；
		 * 需要把原来的文件名用FIELD来上传；
		 * 所以在代码中需要检查是否有FIELDFILENAME存在，有就强行使用这个域
		 */
		while (iterator.hasNext()) {
			FileItem item = (FileItem) iterator.next();

			//忽略其他不是文件域的所有表单信息
			if (!item.isFormField()) {
				//上传的是文件信息

				String name = item.getName();
				if ((name == null) || name.equals("") && item.getSize() == 0) {
					continue;
				}

				PreparedStatement ps = null;
				ResultSet rs = null;

				try {
					//文件保存处理
					String udate = new ASFuntion().getCurrentDate() + " " + new ASFuntion().getCurrentTime(); //获取今天日期

					//获得文件数据
					byte[] byteData = item.get();
					
					//前台压缩还是后台压缩；
					String zipByClient=request.getParameter("zipByClient");
					if (!"1".equals(zipByClient)){
						
						//去掉文件未尾的回车和换行
						int temp = 0;
						for (int i = byteData.length-1; i >= 0; i--) {
						if(byteData[i] != 13 && byteData[i] != 10) {
								break;
							}
							
							temp++;
						}
						
						byte[] byteTemp = new byte[byteData.length-temp] ;
						
						for (int i = 0; i < byteTemp.length; i++) {
							byteTemp[i] =  byteData[i];
						}
						
						//不是前台压缩，就后台自己压缩
						byteData = new ZipUtil().gzipBytes(byteTemp);
					}

					String sql = "select taskcode,taskName from k_tasktemplate c where taskId= ? and typeId=? ";

			        ps = conn.prepareStatement(sql);
			        ps.setString(1, taskId);
			        ps.setString(2, projectIdOrTypeId);
			        rs = ps.executeQuery();

			        //如果是模版底稿文件
			        if (rs.next()) {
			        	String taskCode = rs.getString(1);
			        	String taskName = rs.getString(2);

			        	sql = "update asdb.k_tasktemplate set udate=?,username=? "
							+ " where taskId= ? and typeId=? ";

						ps = conn.prepareStatement(sql);

						ps.setString(1, udate);
						ps.setString(2, username);
						ps.setString(3, taskId);
						ps.setString(4, projectIdOrTypeId);
						ps.execute();

						//保存文件时备份
						try {
				            ManuFileService manuScriptManage = new ManuFileService(conn);
				            String typeBakPath =  BackupUtil.getDATABASE_PATH() + "../TemplateDataBackup/" + projectIdOrTypeId;
				            File task = new File(ManuFileService.getTemplateDir(projectIdOrTypeId) + "/" + taskId);
				            File recycle = new File(typeBakPath + "/" + taskCode + "_" + taskName + "_" + taskId + "_" + username + "_" + BackupUtil.getCurrentDateTime());

				            recycle.getParentFile().mkdirs();
				            manuScriptManage.copyFile(task, recycle);
						} catch (Exception e) {
							org.util.Debug.prtOut("保存模板备份出错:" + e.getMessage());
						}

						try {
							new ManuFileService(conn).saveFileByTypeIdAndTaskId(projectIdOrTypeId, taskId,byteData);
							
						} catch (Exception ex) {
							Debug.print(Debug.iError, "底稿保存失败", ex);
							throw ex;
						}
			        } else {
			        	//对项目底稿进行更新处理
			        	new DBConnect().changeDataBaseByProjectid(conn, projectIdOrTypeId);
			        	
			        	Task task = new TaskService(conn,projectIdOrTypeId).getTaskByTaskId(taskId);

						sql = "update z_task set udate=?,username=? "
							+ " where taskId= ? and projectId=? ";

						ps = conn.prepareStatement(sql);

						ps.setString(1, udate);
						ps.setString(2, username);
						ps.setString(3, taskId);
						ps.setString(4, projectIdOrTypeId);
						ps.execute();

						try {
							new TaskRecycleService(conn,projectIdOrTypeId).moveToRecycle(taskId,"1");
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							new TaskCommonService(conn,projectIdOrTypeId).setTaskState(taskId, TaskCommonService.TASK_STATE_CODE_SAVED, true);
						} catch (Exception e) {
							e.printStackTrace();
							//throw e;
						}

						try {
							new ManuFileService(conn).saveFileByProjectIdAndTaskId(projectIdOrTypeId, taskId,byteData);
							
							try {
								if(task != null) {
									
									UserSession userSession = new UserSession();
									userSession.setUserIp(Web.getIp(request));
									userSession.setUserName(username);
									userSession.setUserLoginId(username);
								
									LogService.addTOLog(userSession, conn, null, projectIdOrTypeId + "," + task.getTaskId() + "," + task.getTaskCode() + "," + task.getTaskName(), "底稿保存");
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							
						} catch (Exception ex) {
							Debug.print(Debug.iError, "底稿保存失败", ex);
							throw ex;
						}
						
						try {
							//整合到合并报表
							String userLoginId = username;
							
							try {
								sql = " select loginid from k_user where name=? ";
								Object[] args = {username};
								userLoginId = new DbUtil(conn).queryForString(sql, args);
								
							} catch (Exception e) {
								e.printStackTrace();
							}
							
							new TaskCommonService(conn, projectIdOrTypeId).saveToUnite(task.getTaskCode(), byteData, userLoginId);
						} catch (Exception e) {
							e.printStackTrace();
						}
			        }
				} catch (Exception e) {
					Debug.print(Debug.iError, "访问失败", e);
					return false;
				} finally {
					DbUtil.close(rs);
					DbUtil.close(ps);

				} // end try~catch~finally
			} else {

				try {
					//上传的是普通表单字域，这里只读出不做任何处理
					String fieldName = item.getFieldName();
					String value = item.getString("utf-8"); //转换编码

					if ((fieldName == null) || fieldName.equals("")
							&& item.getSize() == 0) {
						continue;
					}

					parameters.put(fieldName, value);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		} //循环
		return true;
	}

		/**
	 * 文件新增的处理方法
	 * @param projectIdOrTypeId
	 * @param customerId
	 * @return
	 */
	public String Upload(String projectIdOrTypeId, String customerId) {
		ManuFileService manuScriptService = new ManuFileService(conn);
		ASFuntion asfuntion = new ASFuntion();

		Iterator iterator = getFileItems();

		//获取前台提交的部分值，请注意，这些值有可能通过需要request来取得，
		//也有可能需要通过form特殊域处理来取得
		//首先是分析request的代码段
		String fileName = "";

		String taskCode = asfuntion.showNull(request.getParameter("taskcode"));
		String taskProperty = asfuntion.showNull(request.getParameter("taskproperty")); //任务属性，1到9都是保留得固定任务；
		String subjectName = asfuntion.showNull(request.getParameter("subjectName"));
		String sql = "";
		String fileAddType = asfuntion.showNull(request.getParameter("addtype"));
		String templateTaskId = asfuntion.showNull(request.getParameter("templateTaskId"));
		String taskContent = asfuntion.showNull(request.getParameter("taskContent"));
		String description = asfuntion.showNull(request.getParameter("description"));
		String taskAttribute = asfuntion.showNull(request.getParameter("taskAttribute"));
		String taskId = "";

		String participator = asfuntion.showNull(request.getParameter("participator"));
		String auditproperty = asfuntion.showNull(request.getParameter("auditproperty"));

		
		//前台压缩还是后台压缩；
		String zipByClient=request.getParameter("zipByClient");
		
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

		//分析FORM特殊域的代码段
		try {

			//遍历取得所有域，为后面的分别处理做好准备，其中如果有上传文件，会把上传文件对应的item指针保留到attachItem
			FileItem attachItem = null;
			while (iterator.hasNext()) {
				FileItem item = (FileItem) iterator.next();

				//忽略其他不是文件域的所有表单信息
				if (!item.isFormField()) {
					//上传的是文件信息
					fieldFilename = item.getName();
					if ((fieldFilename == null) || fieldFilename.equals("")
							&& item.getSize() == 0) {
						continue;
					}
					//记住文件ITEM，已被后面处理
					//请注意，这种方法则意味着只支持一次上传一个附件
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

							org.util.Debug.prtOut("qwh:paramFilename="+paramFilename);

						}

						if (fieldName.equals("addtype")) {
							fileAddType = value;
						} else if (fieldName.equals("taskcode")) {
							taskCode = value;
						} else if (fieldName.equals("taskproperty")) {
							taskProperty = value;
						} else if (fieldName.equals("subjectName") ) {
							subjectName = value;
						} else if (fieldName.equals("templateTaskId") ) {
							templateTaskId = value;
						} else if (fieldName.equals("taskContent") ) {
							taskContent = value;
						} else if (fieldName.equals("description") ) {
							description = value;
						} else if (fieldName.equals("taskAttribute")){
							taskAttribute = value;
						} else if(fieldName.equals("participator")){
							participator = value;
						} else if(fieldName.equals("auditproperty")) {
							auditproperty = value;
						}
						org.util.Debug.prtOut("value:" + value);

						parameters.put(fieldName, value);

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			} //循环

			//文件名的生成逻辑是，如果前台有提供newfilename参数，则使用newfilename参数；否则，直接使用附件名字(要去掉路径)
			fileName = "";
			if (paramFilename != null && !paramFilename.equals("")) {
				//用户提供了newfilename参数
				fileName = paramFilename;
				Debug.print("filename1=" + fileName);
			} else {
				//用户没提供，沿用文件名，但是要去掉路径
				fileName = this.getFileName(attachItem.getName()); //获取不带路径的文件名
				Debug.print("filename2=" + fileName);
			}
			parameters.put("filename", fileName) ;
			//清理原来设置了属性得节点
			if (taskProperty != null && !taskProperty.equals("")) {
				try {
					if (Integer.parseInt(taskProperty) > 0) {
						//是数字才修改底稿（因为数字类特殊底稿有唯一性，而字母类特殊底稿则可以任意设置）
						ps = conn.prepareStatement("update z_Task set property=\"\" where projectid=? and property=? ");
						ps.setString(1, projectIdOrTypeId);
						ps.setString(2, taskProperty);
						ps.execute();
					}
				} catch (Exception e) {
					//e.printStackTrace() ;
				} finally {
					DbUtil.close(ps);
				}
			}

			TaskService taskService = new TaskService(conn, projectIdOrTypeId);

			//增加到任务表
			String parentTaskId = request.getParameter("parentTaskId"); //获取上级任务得节点
			
			if(parentTaskId == null || "".equals(parentTaskId)) {
				String parentTaskCode = request.getParameter("parentTaskCode");
				parentTaskId = taskService.getTaskIdByTaskCode(parentTaskCode,0);
			}

			Task task = new Task();
			task = taskService.getTaskByTaskId(parentTaskId);

			int lev = task.getLevel() + 1;

			//取得最大的taskId
			sql = "select ifnull(max(taskid),0)+1 from z_task";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				taskId = rs.getString(1);
			}
			rs.close();
			ps.close();

			String strSql = "INSERT INTO z_Task( "
						+ " Taskid,Taskcode,TaskName,TaskContent,Description, "
						+ " ParentTaskID,ProjectID,IsLeaf,level0,ManuID, "
						+ " Fullpath,Property,OrderId,SubjectName,auditproperty"
						+ ") VALUES( "
						+ " ?,?,?,?,?, "
						+ " ?,?,?,?,?, "
						+ " ?,?,?,?,?)";

			if(!participator.equals("")){
				strSql = "INSERT INTO z_Task( "
					+ " Taskid,Taskcode,TaskName,TaskContent,Description, "
					+ " ParentTaskID,ProjectID,IsLeaf,level0,ManuID, "
					+ " Fullpath,Property,OrderId,SubjectName,auditproperty,user0"
					+ ") VALUES( "
					+ " ?,?,?,?,?, "
					+ " ?,?,?,?,?, "
					+ " ?,?,?,?,?,'"+participator+"')";
			}
			ps = conn.prepareStatement(strSql);
			ps.setString(1, taskId);

			ps.setString(2, taskCode);
			ps.setString(3, fileName);
			ps.setString(4, taskContent);
			ps.setString(5, description);

			ps.setString(6, task.getTaskId());
			ps.setString(7, projectIdOrTypeId);
			ps.setInt(8, 1);
			ps.setInt(9, lev);
			ps.setInt(10, 0);
			
			String fullPath = task.getFullPath() ;
			if("|".equals(fullPath.substring(fullPath.length()-1))){
				fullPath = fullPath + taskId + "|" ;
			}else {
				fullPath = fullPath + "|" + taskId + "|" ;
			}
			ps.setString(11, fullPath);
			ps.setString(12, taskProperty);
			ps.setString(13, UTILString.getOrderId(taskCode));
			ps.setString(14, subjectName);
			ps.setString(15, auditproperty);

			ps.execute();

			if("1".equals(taskAttribute) || "print".equals(taskProperty)){
				TaskCommonService taskCommonService = new TaskCommonService(conn, projectIdOrTypeId);

				taskCommonService.setTaskState(taskId, TaskCommonService.TASK_STATE_CODE_MUST, true);
			}

			//存储成文件
			try {
				if (fileAddType.equals("newexcel")) {
					//新增excel
					manuScriptService.newFileByProjectIdAndTaskId(projectIdOrTypeId,taskId, "0", "2");

				} else if (fileAddType.equals("newword")) {
					//新增word
					manuScriptService.newFileByProjectIdAndTaskId(projectIdOrTypeId,taskId, "0", "1");

				} else if (fileAddType.equals("existfile")) {
					
					//上传已存在文件
					byteData = attachItem.get();
					if (!"1".equals(zipByClient)){
						//不是前台压缩，就后台自己压缩
						byteData = new ZipUtil().gzipBytes(byteData);
					}
					
					manuScriptService.saveFileByTaskId(projectIdOrTypeId, taskId,byteData);

				} else if (fileAddType.equals("template")) {
					//从模板复制文件
					manuScriptService.newFileByProjectIdAndTaskId(projectIdOrTypeId,taskId, "0", templateTaskId);

				}
			} catch (Exception e) {
				Debug.print(Debug.iError, "保存底稿文件失败", e);
			}
			
			try {
				if(task != null) {
					UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
					
					if(userSession == null) {
						userSession = new UserSession();
						userSession.setUserIp(Web.getIp(request));
						userSession.setUserName("");
						userSession.setUserLoginId("");
					}
					
					LogService.addTOLog(userSession, conn, null, projectIdOrTypeId + "," + taskId + "," + taskCode + "," + fileName, "底稿保存");
				}
			} catch (Exception e) {
				e.printStackTrace();
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

		public boolean UploadUpdateHelp(String taskId, String typeId, String username) throws Exception {

		Iterator iterator = getFileItems();

		/**
		 * 没有办法，如果前台是采用控件提交，则文件名和实际文件名会不一致；
		 * 需要把原来的文件名用FIELD来上传；
		 * 所以在代码中需要检查是否有FIELDFILENAME存在，有就强行使用这个域
		 */
		while (iterator.hasNext()) {
			FileItem item = (FileItem) iterator.next();

			//忽略其他不是文件域的所有表单信息
			if (!item.isFormField()) {
				//上传的是文件信息

				String name = item.getName();
				if ((name == null) || name.equals("") && item.getSize() == 0) {
					continue;
				}

				PreparedStatement ps = null;
				ResultSet rs = null;

				try {
					//文件保存处理
					String udate = new ASFuntion().getCurrentDate() + " " + new ASFuntion().getCurrentTime(); //获取今天日期

					//获得文件数据
					byte[] byteData = item.get();
					
					//前台压缩还是后台压缩；
					String zipByClient=request.getParameter("zipByClient");
					if (!"1".equals(zipByClient)){
						//不是前台压缩，就后台自己压缩
						byteData = new ZipUtil().gzipBytes(byteData);
					}

					String sql = "select * from k_tasktemplatehelp c where taskId= ? and typeId=? ";

			        ps = conn.prepareStatement(sql);
			        ps.setString(1, taskId);
			        ps.setString(2, typeId);
			        rs = ps.executeQuery();

			        //如果是模版底稿文件
			        if (rs.next()) {

			        	sql = "update asdb.k_tasktemplatehelp set udate=?,username=? "
							+ " where taskId= ? and typeId=? ";

						ps = conn.prepareStatement(sql);

						ps.setString(1, udate);
						ps.setString(2, username);
						ps.setString(3, taskId);
						ps.setString(4, typeId);
						ps.execute();

						try {
							new ManuFileService(conn).saveFileByHelpId(typeId, taskId,byteData);
							
						} catch (Exception ex) {
							Debug.print(Debug.iError, "底稿保存失败", ex);
							throw ex;
						}
			        }
				} catch (Exception e) {
					Debug.print(Debug.iError, "访问失败", e);
					return false;
				} finally {
					DbUtil.close(rs);
					DbUtil.close(ps);

				} // end try~catch~finally
			} else {

				try {
					//上传的是普通表单字域，这里只读出不做任何处理
					String fieldName = item.getFieldName();
					String value = item.getString("utf-8"); //转换编码

					if ((fieldName == null) || fieldName.equals("")
							&& item.getSize() == 0) {
						continue;
					}

					parameters.put(fieldName, value);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		} //循环
		return true;
	}

}
