package com.matech.audit.service.attachFileUploadService;


import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.matech.audit.service.attachFileUploadService.model.AttachFile;
import com.matech.framework.pub.db.DbUtil;


public class AttachFileUploadService {
	
	private Connection conn;

	public AttachFileUploadService(Connection conn){
		this.conn = conn;
	}
	
	
	public void save(AttachFile attachFile) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "insert into k_attachfile(autoid,indexTable,indexMetaData,indexId,fileName,fileTempName,property,timeFlag) values (?,?,?,?,?,?,?,?)";
			ps = conn.prepareStatement(sql) ;
			int i = 1;
			ps.setString(i++,UUID.randomUUID().toString()) ;
			ps.setString(i++,attachFile.getIndexTable()) ;
			ps.setString(i++,attachFile.getIndexMetaData()) ;
			ps.setString(i++,attachFile.getIndexId()) ;
			ps.setString(i++,attachFile.getFileName()) ;
			ps.setString(i++,attachFile.getFileTempName()) ;
			ps.setString(i++,attachFile.getProperty()) ;
			ps.setString(i++,attachFile.getTimeFlag()) ;
			
			ps.execute() ;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public void delete(String indexTable,String indexId,String fileTempName) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "delete from k_attachfile where indexTable=? and indexId=? and fileTempName=?";
			ps = conn.prepareStatement(sql) ;
			ps.setString(1,indexTable) ;
			ps.setString(2,indexId) ;
			ps.setString(3,fileTempName) ;
			ps.execute() ;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public List getAttachFile(String indexTable,String indexId) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			List attachFileList = new ArrayList() ;
			
			String sql = "select indexTable,indexMetaData,indexId,fileName,fileTempName from k_attachfile where indexTable=? and indexId=? order by autoId";
			ps = conn.prepareStatement(sql);
			ps.setString(1,indexTable) ;
			ps.setString(2,indexId) ;
			rs = ps.executeQuery();
			while(rs.next()){
				AttachFile attachFile = new AttachFile() ;
				attachFile.setIndexTable(rs.getString("indexTable"));
				attachFile.setIndexMetaData(rs.getString("indexMetaData"));
				attachFile.setIndexId(rs.getString("indexId"));
				attachFile.setFileName(rs.getString("fileName"));
				attachFile.setFileTempName(rs.getString("fileTempName"));
				
				attachFileList.add(attachFile) ;
			}
			return attachFileList;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public AttachFile getAttachFile(String indexTable,String indexId,String fileTempName) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			String sql = "select indexTable,indexMetaData,indexId,fileName,fileTempName from k_attachfile where indexTable=? and indexId=? and fileTempName=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1,indexTable) ;
			ps.setString(2,indexId) ;
			ps.setString(3,fileTempName) ;
			rs = ps.executeQuery();
			AttachFile attachFile = new AttachFile() ;
			if(rs.next()){
				attachFile.setIndexTable(rs.getString("indexTable"));
				attachFile.setIndexMetaData(rs.getString("indexMetaData"));
				attachFile.setIndexId(rs.getString("indexId"));
				attachFile.setFileName(rs.getString("fileName"));
				attachFile.setFileTempName(rs.getString("fileTempName"));
			}
			return attachFile;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public void delFileTiming() throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		try {
			String sql = "select * from k_attachfile";
			ps = conn.prepareStatement(sql) ;
			rs = ps.executeQuery() ;
			
			while(rs.next()) {
				
				sql = "select 1 from "+rs.getString("indexTable")+" where "+rs.getString("indexMetaData")+"='"+rs.getString("indexId")+"'" ;
				ps = conn.prepareStatement(sql) ;
				rs2 = ps.executeQuery() ;
				if(!rs2.next()) { //无记录，删除
					
					//先删除文件
					String fileName = rs.getString("fileTempName") ;
			//		String path = BackupUtil.getDATABASE_PATH()+"../attachFile/"+rs.getString("indexTable")+"/";
					String path = "";
					File file = new File(path+fileName) ;
					if(file.exists()) {
						file.delete() ;
					}
					
					//删除记录
					delete(rs.getString("indexTable"),rs.getString("indexId"),fileName) ;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public void delModuleFile(String indexTable,String indexId) throws Exception{
		DbUtil.checkConn(conn);
		
			String path = this.getClass().getResource("/").getPath()+"../../common/attachFile/"+indexTable+"/";
			List files = getAttachFile(indexTable, indexId) ;
			for(Iterator<AttachFile> iter = files.iterator();iter.hasNext();) {
				try {
					AttachFile af = iter.next() ;
					delete(indexTable, indexId, af.getFileTempName()) ;  //删除数据库记录
					File file = new File(path+af.getFileTempName());     //删除文件
					if(file.exists()) {
						file.delete();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
	}
	
	public void addDownInfo(String userId,String module,String indexId) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		try {
			
			String sql = "insert into k_downloadInfo(userId,module,indexid) values(?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1,userId) ;
			ps.setString(2,module) ;
			ps.setString(3,indexId) ;
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace(); 
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	
	public boolean isDownInfoExist(String userId,String module,String indexId) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null ;
		try {
			
			String sql = "select 1 from k_downloadInfo where userId=? and module=? and indexId=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1,userId) ;
			ps.setString(2,module) ;
			ps.setString(3,indexId) ;
			rs = ps.executeQuery();
			if(rs.next()) {
				return true ;
			}else {
				return false;
			}
 		} catch (Exception e) {
			e.printStackTrace(); 
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}

	public List getAttachFiles(String indexTable,String indexId,String userId) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			List attachFileList = new ArrayList() ;
			
			String sql = "select distinct indexTable,indexMetaData,indexId,fileName,fileTempName from asdb.k_attachfile where indexTable=? and indexId=? and property=? order by autoId";
			ps = conn.prepareStatement(sql);
			ps.setString(1,indexTable) ;
			ps.setString(2,indexId) ;
			ps.setString(3,userId) ;
			rs = ps.executeQuery();
			while(rs.next()){
				AttachFile attachFile = new AttachFile() ;
				attachFile.setIndexTable(rs.getString("indexTable"));
				attachFile.setIndexMetaData(rs.getString("indexMetaData"));
				attachFile.setIndexId(rs.getString("indexId"));
				attachFile.setFileName(rs.getString("fileName"));
				attachFile.setFileTempName(rs.getString("fileTempName"));
				
				attachFileList.add(attachFile) ;
			}
			return attachFileList;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public void delete(String indexTable,String indexId,String fileTempName,String userId) throws Exception{
		DbUtil.checkConn(conn);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "delete from asdb.k_attachfile where indexTable=? and indexId=? and fileTempName=? and property=? ";
			ps = conn.prepareStatement(sql) ;
			ps.setString(1,indexTable) ;
			ps.setString(2,indexId) ;
			ps.setString(3,fileTempName) ;
			ps.setString(4,userId) ;
			ps.execute() ;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
}
