package com.matech.audit.pub.imagesBrowser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.matech.audit.service.attachFileUploadService.model.Attach;
import com.matech.framework.pub.db.DbUtil;

public class ImagesBrowserService {
	
	Connection conn = null;
	
	public ImagesBrowserService(Connection conn){
		this.conn = conn;
	}
	
	/**
	 * 根据indexid 得到所有图片
	 * @param indexId
	 * @return
	 */
	public List<Attach> getImages(String indexId){
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String sql = " select "
					+ " 	ATTACHID, ATTACHNAME, ATTACHFILE, ATTACHFILEPATH, ATTACHTYPE, "
					+ " 	UPDATEUSER, UPDATETIME, INDEXTABLE, INDEXMETADATA, INDEXID, "
					+ " 	PROPERTY,FILESIZE  FROM `MT_COM_ATTACH` where  indexid=? ";
		List<Attach> listImage = new ArrayList<Attach>();
		
		try {
		
			ps = conn.prepareStatement(sql);
			
			ps.setString(1, indexId);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				Attach attach = new Attach();

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
				listImage.add(attach);
				
			}
			
			
		} catch (SQLException e) {
			System.out.println("得到附件信息错误："+e.getMessage());
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return listImage;
		
	} 
	
	
	
}
