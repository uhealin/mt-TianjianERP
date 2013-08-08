package com.matech.audit.service.project;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.datamanage.DataZip;
import com.matech.audit.service.manuscript.ManuFileService;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.sys.UTILSysProperty;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
public class Download {

    private Connection conn = null;
    private String projectId = null;
    private String customerId = null;
    private String expFileName = "0";
    
    public static final String FILE_LIST_NAME = "fileList";
    
    public static final String TEMP_TABLE_NAME = "t_tasklist_";
    
    public static final String PROJECT_DOWNLOAD_TEMP_PATH = UTILSysProperty.SysProperty.getProperty("系统临时目录") + "APDownload/";

    public Download(Connection conn, String projectId) {
        this.conn = conn;
        this.projectId = projectId;
    }
    
    public Download(Connection conn, String customerId, String third) {
        this.conn = conn;
        this.customerId = customerId;
    }
    
    /**
     * 创建md5文件
     * @param filePath
     */
    private void createFileList(String filePath) {
    	File file = new File(filePath);
    	
    	if(file.exists()) {
    		file.delete();
    	}
    	
    	try {
			
	    	StringBuffer sql = new StringBuffer();
	    	
	    	sql.append(" select taskid,taskcode,taskname,projectid,Udate ");
	    	sql.append(" from z_task ");
	    	sql.append(" where projectid='" + this.projectId + "' ");
	    	sql.append(" and isleaf=1 ");
	    	sql.append(" and (property not like 'A%' or property is null) ");
	    	sql.append(" order by orderid"); 
			sql.append(" into outfile '" + filePath + "'" );
			sql.append(" FIELDS TERMINATED BY '\t'  ");
			sql.append(" LINES TERMINATED BY '\n' ");
			
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
			DbUtil dbUtil = new DbUtil(conn);
			
			dbUtil.execute(sql.toString());
		
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * 创建临时表
     * @param localMd5FilePath
     * @return
     */
    private String createTempTable(String localFileList) {

    	File file = new File(localFileList);
    	
    	if(!file.exists()) {
    		System.out.println(localFileList + ",文件不存在..");
    		return null;
    	}
    	
    	String tempTableName = TEMP_TABLE_NAME + DELUnid.getNumUnid();
    	
    	try {
			StringBuffer sql = new StringBuffer();
				
			sql.append(" CREATE TABLE " + tempTableName);
			sql.append(" select taskid,taskcode,taskname,projectid,udate ");
			sql.append(" from z_task where 1=2 ");
			
			new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
			DbUtil dbUtil = new DbUtil(conn);
			
			System.out.println("从文件中装载：" + localFileList);
			
			//创建临时表
			dbUtil.execute(sql.toString());
			
			//装载文件到临时表
			sql = null;
			sql = new StringBuffer();
			sql.append(" load data infile '" + localFileList + "' ");
			sql.append(" into table " + tempTableName );
			sql.append(" FIELDS TERMINATED BY '\t' " );
			sql.append(" LINES TERMINATED BY '\n' ");
			dbUtil.execute(sql.toString());
			
			System.out.println("生成临时表：" + tempTableName);
			
			return tempTableName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
    	
    }
    
    /**
     * 删除表
     * @param tableName
     */
    private void removeTable(String tableName) {
    	
    	if(tableName == null || "".equals(tableName) || "null".equalsIgnoreCase(tableName)) {
    		return;
    	}
    	
    	try {
            String sql = " drop table IF EXISTS " + tableName;
            new DbUtil(conn).execute(sql);
		} catch (Exception e) {
			System.out.println("删除临时表出错：" + tableName);
			e.printStackTrace();
		}
    }
    
    /**
     * 获取到解压文件
     * @param localFileList
     * @return
     */
    public String getZipFile(String localFileList) {
    	   	
    	String tempFileDir = DELUnid.getNumUnid();
    	String tempFileName =  tempFileDir + ".zip";

        PreparedStatement ps = null;
        ResultSet rs = null;
        FileOutputStream os = null;
        String tempTableName = "";
        
        try {
            DbUtil.checkConn(conn);
            
            new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
            
            String temp = PROJECT_DOWNLOAD_TEMP_PATH + tempFileDir;

            if (!new File(temp).exists()) {
                new File(temp).mkdirs();
            }
            
            //创建并填充临时表
            tempTableName = createTempTable(localFileList);
            
            //找出需要更新的底稿
            StringBuffer sql = new StringBuffer();
            
            if(tempTableName == null) {
            	sql.append(" select a.taskId,a.taskname,a.TaskCode  "); 
    			sql.append(" from z_task a ");
    			sql.append(" where a.projectId=? ");
    			sql.append(" and a.isleaf=1  ");
    			sql.append(" and a.Udate<>'' and a.Udate is not null  ");
    			sql.append(" and (a.property not like 'A%' or a.property is null) ");
    			
    			ps = conn.prepareStatement(sql.toString());
	            ps.setString(1, this.projectId);
            } else {
            	
                sql.append(" select a.taskId,a.taskname,a.TaskCode  "); 
    			sql.append(" from z_task a left join `" + tempTableName + "` b on b.projectId=? and a.taskname=b.taskname ");
    			sql.append(" where a.projectId=? ");
    			sql.append(" and a.isleaf=1  ");
    			sql.append(" and a.Udate<>'' and a.Udate is not null  ");
    			sql.append(" and (a.property not like 'A%' or a.property is null) ");
    			sql.append(" and (a.Udate<>b.Udate or b.Udate is null) ");
    			
    			ps = conn.prepareStatement(sql.toString());
	            ps.setString(1, this.projectId);
	            ps.setString(2, this.projectId);
            }
            
          
            rs = ps.executeQuery();

            
            String fileName = "";
            String taskId = "";

            while (rs.next()) {
            	taskId = rs.getString("taskId");
                fileName = rs.getString("taskname");
                fileName = this.fileNameFilter(fileName);
                os = new FileOutputStream(new File(temp + "/" + fileName));

                byte[] bs = new ManuFileService(conn).getFileByProjectIdAndTaskId(this.projectId, taskId);

                bs = new com.matech.framework.pub.util.ZipUtil().
                     ungzipBytes(bs);

                os.write(bs);
                os.close();
            }
            
            //创建文件列表
            createFileList(temp + "/" + FILE_LIST_NAME);

            //压缩文件
            DataZip dataZip = new DataZip();
            dataZip.zip(PROJECT_DOWNLOAD_TEMP_PATH + tempFileDir, PROJECT_DOWNLOAD_TEMP_PATH + tempFileName);
            
        } catch (Exception e) {
            Debug.print(Debug.iError,"访问失败",e);
        } finally {
        	
        	removeTable(tempTableName);
        	
            DbUtil.close(rs);
            DbUtil.close(ps);
        }
        return tempFileName;
    }
    
    /**
     * 获取到解压文件
     * @param localFileList
     * @return
     */
    public String getZipFile() {
    	   	
    	String tempFileDir = DELUnid.getNumUnid();
    	String tempFileName =  tempFileDir + ".zip";

        PreparedStatement ps = null;
        ResultSet rs = null;
        FileOutputStream os = null;
        
        try {
            DbUtil.checkConn(conn);
            
            new DBConnect().changeDataBaseByProjectid(conn, this.projectId);
            
            String temp = PROJECT_DOWNLOAD_TEMP_PATH + tempFileDir;

            if (!new File(temp).exists()) {
                new File(temp).mkdirs();
            }

            //找出需要更新的底稿
            StringBuffer sql = new StringBuffer();

        	sql.append(" select a.taskId,a.taskname,a.TaskCode  "); 
			sql.append(" from z_task a ");
			sql.append(" where a.projectId=? ");
			sql.append(" and a.isleaf=1  ");
			sql.append(" and (a.property not like 'A%' or a.property is null) ");
			
			ps = conn.prepareStatement(sql.toString());
            ps.setString(1, this.projectId);
          
            
          
            rs = ps.executeQuery();

            
            String fileName = "";
            String taskId = "";

            while (rs.next()) {
            	taskId = rs.getString("taskId");
                fileName = rs.getString("taskname");
                fileName = this.fileNameFilter(fileName);
                os = new FileOutputStream(new File(temp + "/" + fileName));

                byte[] bs = new ManuFileService(conn).getFileByProjectIdAndTaskId(this.projectId, taskId);

                bs = new com.matech.framework.pub.util.ZipUtil().ungzipBytes(bs);

                os.write(bs);
                os.close();
            }

            //压缩文件
            DataZip dataZip = new DataZip();
            dataZip.zip(PROJECT_DOWNLOAD_TEMP_PATH + tempFileDir, PROJECT_DOWNLOAD_TEMP_PATH + tempFileName);
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
      
            DbUtil.close(rs);
            DbUtil.close(ps);
        }
        return tempFileName;
    }

    /**
     * 创建要下载的临时文件
     * @param args
     * @return
     */
    public String createTempFile(String args) {
        if (args == null || args.equals("")) {
            return "";
        }

        String tempDir = UTILSysProperty.SysProperty.getProperty("系统临时目录");
        String[] taskIds = args.split(",");

        String temp = tempDir + "APDownload\\" + DELUnid.getNumUnid();
        if (!new File(tempDir).exists()) {
            new File(tempDir).mkdir();
        }
        if (!new File(tempDir + "APDownload").exists()) {
            new File(tempDir + "APDownload").mkdir();
        }
        if (!new File(temp).exists()) {
            new File(temp).mkdir();
            org.util.Debug.prtOut(temp); //===================创建临文件夹
        }

        String sql = "select taskname,TaskCode from z_task where taskId=? and projectId=? ";

        PreparedStatement ps = null;
        ResultSet rs = null;
        FileOutputStream os = null;

        try {
            DbUtil.checkConn(conn);

            ps = conn.prepareStatement(sql);

            new DBConnect().changeDataBaseByProjectid(conn, this.projectId);

            for (int i = 0; i < taskIds.length; i++) { //创建临时文件

                if (taskIds[i] == null || taskIds[i].equals("")) {
                    continue;
                }

                ps.setString(1, taskIds[i]);
                ps.setString(2, this.projectId);

                rs = ps.executeQuery();
                String fileName = "";

                if (rs.next()) {
                	if("1".equals(this.getExpFileName())) {
                		fileName = rs.getString("taskname");
                	} else {
                		fileName = rs.getString("TaskCode") + rs.getString("taskname");
                	}
                   
                    fileName = this.fileNameFilter(fileName);
                    os = new FileOutputStream(new File(temp + "\\" + fileName));

                    //根据UNID取得相关底稿文件
                    byte[] bs = new ManuFileService(conn).getFileByProjectIdAndTaskId(this.projectId, taskIds[i]);

                    bs = new com.matech.framework.pub.util.ZipUtil().
                         ungzipBytes(bs);

                    os.write(bs);
                    os.close();
                } else {
                    org.util.Debug.prtOut("Error ！！！ 文件" + taskIds[i] + "不存在！");
                }
            }

            //创建zip文件
            ZipUtil zu = new ZipUtil();
            return zu.gzip(temp, null);

        } catch (Exception e) {
            Debug.print(Debug.iError,"访问失败",e);
        } finally {
            DbUtil.close(rs);
            DbUtil.close(ps);
        }
        return "";
    }
    
    /**
     * 创建要下载的临时文件
     * @param args
     * @return
     */
    public String createTempAttachFile() {
     
    	String tempDir = UTILSysProperty.SysProperty.getProperty("系统临时目录");
        String temp = tempDir + "AttachDownload\\" + DELUnid.getNumUnid();
        if (!new File(temp).exists()) {
            new File(temp).mkdirs();
        }

        String sql  = "select id,typename,parentid from k_attachtype a"
					+ " inner join"
					+ " ("
					+ "     select typeid from k_attach where departid = '"+ this.customerId + "' and filename !='' "
					+ " )b"
					+ " on a.id = b.typeid "
					+ " order by a.id ";

        PreparedStatement ps = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        ResultSet rs3 = null;
        FileOutputStream os = null;

        String attchdate = DELUnid.getNumUnid();
        
        try {
            DbUtil.checkConn(conn);

            ps = conn.prepareStatement(sql);

            new DBConnect().getConnect("");
            rs = ps.executeQuery();
            
            String fileId = "";
            String fileName = "";
            String parentId = "";
            String parentName = "";
        
            while(rs.next()) {
            	fileId = rs.getString("id");
                fileName = rs.getString("typename");
                parentId = rs.getString("parentid");                
                fileName = this.fileNameFilter(fileName);
                boolean flag = true;
                
                if("0".equals(parentId)){               	
                	if (!new File(temp +"\\"+ fileName).exists()) {
                        new File(temp +"\\"+ fileName).mkdir();
                    }
                }else{                	
                	sql = "select typename from k_attachtype where id ='"+parentId+"'";
                	ps2 = conn.prepareStatement(sql);
                	rs3 = ps2.executeQuery();
                	
                	while(rs3.next()){
                		parentName = rs3.getString("typename");
                	}
                	rs3.close();
                	ps2.close();
                	
                	if (!new File(temp + "\\"+ parentName +"\\"+ fileName).exists()) {
                        new File(temp + "\\"+ parentName +"\\"+ fileName).mkdirs();
                    }
                	
                	flag = false;
                }
                
                String Strsql = "select unid,filename from k_attach where typeid = "+fileId+" and departid = '"+this.customerId+"' and filename !=''"; 
                ps = conn.prepareStatement(Strsql);
                
                rs2 = ps.executeQuery();
                
                while(rs2.next()){    	
                	//根据UNID取得相关附件文件
                    byte[] bs = new ManuFileService(conn).getFileBycustomerIdAndUnid(customerId, rs2.getString("unid"));                 
                    
                    if(flag){
                    	os = new FileOutputStream(new File(temp + "\\" + fileName + "\\" +rs2.getString("filename")));         	
                    }else{
                    	os = new FileOutputStream(new File(temp + "\\"+parentName+ "\\"+ fileName + "\\" +rs2.getString("filename")));
                    }
                    
                    os.write(bs);
                    os.close();
                }   
            } 
            
    		List list = new ArrayList();
    		list.add("");
    				
    		new DataZip().zip(temp,UTILSysProperty.SysProperty.getProperty("系统临时目录") + "AttachDownload/"+attchdate+".zip", list);
            
        } catch (Exception e) {
            Debug.print(Debug.iError,"访问失败",e);
        } finally {
            DbUtil.close(rs);
            DbUtil.close(ps);
            DbUtil.close(rs2);
            DbUtil.close(ps2); 
        }
        return UTILSysProperty.SysProperty.getProperty("系统临时目录") + "AttachDownload/"+attchdate+".zip";
    }

    public String fileNameFilter(String FileName) {
        String[] sign = {"\\", "/", ":", "*", "?", "\"", "|"};
        String result = FileName;
        ASFuntion asf = new ASFuntion();
        for (int i = 0; i < sign.length; i++) {
            result = asf.replaceStr(result, sign[0], "_");
        }

        return result;
    }

    public void zip(String fileName) {

        ZipEntry ze = null;
        try {

            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(
                    "C:\\zip.zip"));
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(new File(fileName)));

            ze = new ZipEntry("apple.xls");
            zos.putNextEntry(ze);

            byte b[] = new byte[512];
            int len;

            while ((len = bis.read(b)) != -1) {
                zos.write(b, 0, len);
            }

            zos.finish();
            zos.close();
        } catch (Exception ex) {
            Debug.print(Debug.iError,"访问失败",ex);
        }
    }


    /**
     * 根据项目ＩＤ，下载底稿。
     * 如果ＵＮＩＤＳ不为，则增加where 条件
     * @param os
     * @param projectid
     * @param taskIds
     */
    public void downloadZipFileByProjectID(java.io.OutputStream os,
                                           String projectid, String taskIds) {

        //参数处理
        taskIds = com.matech.framework.pub.util.UTILString.killEndToken(taskIds,
                ",");

        java.sql.Statement st = null;
        java.sql.ResultSet rs = null;

        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs1 = null;

        try {
            DbUtil.checkConn(conn);

            org.apache.tools.zip.ZipOutputStream zos = new org.apache.tools.zip.
                    ZipOutputStream(os);

            //切换到当前项目的数据库

            new DBConnect().changeDataBaseByProjectid(projectid, conn);

            st = conn.createStatement();

            //定义区

            ManuFileService mm = new ManuFileService(conn);
            com.matech.framework.pub.util.ZipUtil zu = new com.matech.framework.
                    pub.util.ZipUtil();

            String taskid = "";
            String fullPath = "";
            String parenttaskid = "";

//			byte b[] = new byte[1024];
//			int len;


            //找出所需要的底稿
            String sql = "select taskcode,taskname,parenttaskid,taskid from z_task where projectid= " +
                         projectid;
            if (taskIds != null && !taskIds.equals("")) {
                sql += " and taskId in (" + taskIds + ")";
            }

            //得到所有的底稿名字
            rs = st.executeQuery(sql);

            sql = "select parenttaskid,taskcode,taskname from z_task where projectid = " +
                    projectid + " and taskid = ?";
            ps = conn.prepareStatement(sql);

            while (rs.next()) {
                taskid = rs.getString("taskid");
                parenttaskid = rs.getString("parenttaskid");
                
               	if("1".equals(this.getExpFileName())) {
               		fullPath = rs.getString("taskname");
            	} else {
            		fullPath = rs.getString("taskcode") + rs.getString("taskname");
            	}
                
                //找出每张底稿的全路径
                while (parenttaskid != null && !"0".equals(parenttaskid)) {
                    ps.setString(1, parenttaskid);
                    rs1 = ps.executeQuery();
                    if (rs1.next()) {
                        parenttaskid = rs1.getString("parenttaskid");
     
                    	if("1".equals(this.getExpFileName())) {
                    		fullPath = rs1.getString("taskname") + "/" + fullPath;
                    	} else {
                    		fullPath = rs1.getString("taskcode") + rs1.getString("taskname") + "/" + fullPath;
                    	}
                    	
                    } else {
                        //如果已经找不到他上级了。并且他的parenttaskid不为０，就异常跳出来了。
                        break;
                    }
                }

                zos.putNextEntry(new org.apache.tools.zip.ZipEntry(fullPath));

                //写数据
                zos.write(zu.ungzipBytes(mm.getFileByProjectIdAndTaskId(projectid, taskid)));
                zos.closeEntry();
            }
            zos.close();
        } catch (Exception e) {
            Debug.print(Debug.iError,"访问失败",e);
        } finally {
            DbUtil.close(rs);
            DbUtil.close(ps);
        }

    }

    public static void main(String[] args) {
    	Connection conn = null;
    	
    	try {
    		conn = new DBConnect().getConnect("");
    		
    		new Download(conn,"2009922").getZipFile("c:/ssss.txt");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
    	
    	
    }

	public String getExpFileName() {
		return expFileName;
	}

	public void setExpFileName(String expFileName) {
		this.expFileName = expFileName;
	}
}
