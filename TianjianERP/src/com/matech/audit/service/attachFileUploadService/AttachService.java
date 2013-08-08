package com.matech.audit.service.attachFileUploadService;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.matech.audit.service.attachFileUploadService.model.Attach;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.path.Path;
import com.matech.framework.pub.sys.UTILSysProperty;

/**
 * 附件管理
 * 
 * @author void
 * 
 */
public class AttachService {
	
	public static String ATTACH_FILE_PATH = "";
	public static final String ATTACH_FILE_DEFAULT_FOLDER = "other/";
	
	private static Log log = LogFactory.getLog(AttachService.class);

	private Connection conn = null;

	static {
		ATTACH_FILE_PATH = UTILSysProperty.SysProperty.getProperty("attachFilePath");
		
		if(ATTACH_FILE_PATH == null) {
			ATTACH_FILE_PATH = Path.getRootPath() + "../database/attachFile/" ;
		}else {
			if(ATTACH_FILE_PATH.lastIndexOf("/") != ATTACH_FILE_PATH.length()-1) {
				ATTACH_FILE_PATH += "/";
			}
		}
		
	}
	
	
	public AttachService(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 保存附件
	 * 
	 * @param attach
	 * @return
	 */
	public int save(Attach attach) {
		PreparedStatement ps = null;
		ResultSet rs = null;

		int result = -1;
		try {
			String sql = "insert into MT_COM_ATTACH( "
					+ " 	ATTACHID, ATTACHNAME, ATTACHFILE, ATTACHFILEPATH, ATTACHTYPE, "
					+ " 	UPDATEUSER, UPDATETIME, INDEXTABLE, INDEXMETADATA, INDEXID, "
					+ " 	PROPERTY,FILESIZE ) values (?,?,?,?,?, ?,?,?,?,?, ?,?)";

			ps = conn.prepareStatement(sql);
			ps.setString(1, attach.getAttachId());
			ps.setString(2, attach.getAttachName());
			ps.setString(3, attach.getAttachFile());
			ps.setString(4, attach.getAttachFilePath());
			ps.setString(5, attach.getAttachType());

			ps.setString(6, attach.getUpdateUser());
			ps.setString(7, attach.getUpdateTime());
			ps.setString(8, attach.getIndexTable());
			ps.setString(9, attach.getIndexMetadata());
			ps.setString(10, attach.getIndexId());

			ps.setString(11, attach.getProperty());
			ps.setString(12, String.valueOf(attach.getFileSize()));

			result = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return result;
	}

	/**
	 * 获取附件列表
	 * 
	 * @param indexTable
	 * @param indexId
	 * @return
	 * @throws Exception
	 */
	public List getAttachList(String indexTable, String indexId) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		List list = new ArrayList();
		
		try {

			String sql = "select ATTACHID "
					+ " from MT_COM_ATTACH "
					+ " where  " 
					+ " INDEXID=? "
					+ " order by UPDATETIME ";

			ps = conn.prepareStatement(sql);
			//ps.setString(1, indexTable);
			ps.setString(1, indexId);
			rs = ps.executeQuery();

			Attach attach = null;
			while (rs.next()) {

				attach = getAttach(rs.getString(1));
				list.add(attach);
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
	 * 删除附件
	 * @param attachId
	 * @return
	 * @throws Exception
	 */
	public int remove(Attach attach) throws Exception {

		PreparedStatement ps = null;
		int result = -1;
		
		if(attach == null) {
			return result;
		}
		
		try {
			String sql = " delete from MT_COM_ATTACH "
						+ " where ATTACHID = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, attach.getAttachId());
			result = ps.executeUpdate();
			
			if(result > 0) {
				File file = new File(attach.getAttachFilePath() + attach.getAttachFile());
				
				if(file.exists()) {
					file.delete();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
		
		return result;
	}
	
	
	/**
	 * 根据模块和索引删除附件
	 * @param indexTable
	 * @param indexId
	 * @throws Exception
	 */
	public void remove(String indexTable,  String indexId) throws Exception {

		List list = getAttachList(indexTable, indexId);
			
		for (int i = 0; i < list.size(); i++) {
			Attach attach = (Attach)list.get(i);
			
			try {
				remove(attach);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	
	}
	
	/**
	 * 获取附件
	 * @param attach
	 * @return
	 * @throws Exception
	 */
	public File getAttachFile(Attach attach) throws Exception {
		File file = new File(getAttachFilePath(attach));
			
		if(!file.exists()) {
			return null;
		}
		
		return file;
	}
	
	/**
	 * 返回附件路径
	 * @param attach
	 * @return
	 * @throws Exception
	 */
	public String getAttachFilePath(Attach attach) throws Exception {
		String filePath = "";
		
		if(attach != null) {
			filePath = ATTACH_FILE_PATH + attach.getAttachFilePath();
			log.info("附件路径：" + filePath);
		}
		
		return filePath;
	}
		
	/**
	 * 获取附件
	 * 
	 * @param attachId
	 * @return
	 * @throws Exception
	 */
	public Attach getAttach(String attachId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		Attach attach = null;
		try {

			String sql = " select "
					+ " 	ATTACHID, ATTACHNAME, ATTACHFILE, ATTACHFILEPATH, ATTACHTYPE, "
					+ " 	UPDATEUSER, UPDATETIME, INDEXTABLE, INDEXMETADATA, INDEXID, "
					+ " 	PROPERTY,FILESIZE " 
					+ " from MT_COM_ATTACH "
					+ " where ATTACHID = ?";

			ps = conn.prepareStatement(sql);

			ps.setString(1, attachId);
			rs = ps.executeQuery();

			if (rs.next()) {

				attach = new Attach();

				attach.setAttachId(rs.getString(1));
				attach.setAttachName(rs.getString(2));
				attach.setAttachFile(rs.getString(3));
				attach.setAttachFilePath(rs.getString(4));
				attach.setAttachType(rs.getString(5));

				attach.setUpdateUser(rs.getString(6));
				attach.setUpdateTime(rs.getString(7));
				attach.setIndexTable(rs.getString(8));
				attach.setIndexMetadata(rs.getString(9));
				attach.setIndexId(rs.getString(10));

				attach.setProperty(rs.getString(11));
				attach.setFileSize(rs.getLong(12));

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return attach;
	}

}
