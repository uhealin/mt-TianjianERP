package com.matech.audit.service.customer;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.customer.model.CustomerNote;
import com.matech.audit.service.datamanage.BackupUtil;
import com.matech.framework.pub.db.DbUtil;

/**
 * <p>Title: 客户走访记录</p>
 * <p>Description: 客户走访记录</p>
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
 * 2008-6-24
 */
public class CustomerNoteService {
	private Connection conn = null;

	public static String NOTE_FILE_PATH = "../oa/noteFile/";

	static {
		//获得程序发布路径
		String dataBasePath = BackupUtil.getDATABASE_PATH();

		NOTE_FILE_PATH = dataBasePath + NOTE_FILE_PATH;

		File file = new File(NOTE_FILE_PATH);
		if(!file.exists()) {
			file.mkdirs();
		}
	}

	public CustomerNoteService(Connection conn) throws Exception {
		DbUtil.checkConn(conn);
		this.conn = conn;
	}

	/**
	 * 更新走访记录
	 * @param customerNote
	 * @return
	 * @throws Exception
	 */
	public boolean updateCustomerNote(CustomerNote customerNote) throws Exception {
		DbUtil dbUtil = new DbUtil(conn);

		String sql = " update oa_customernote set "
				   + " customerid=?,linkman=?,notetime=?,title=?,content=?,filename=?,filepath=?,username=?,udate=now(),type0=? "
				   + " where autoid=? ";

		Object[] args = new Object[] {
				customerNote.getCustomerId(),
				customerNote.getLinkMan(),
				customerNote.getNoteTime(),
				customerNote.getTitle(),
				customerNote.getContent(),
				customerNote.getFileName(),
				customerNote.getFilePath(),
				customerNote.getUserName(),
				customerNote.getType0(),
				customerNote.getAutoId()
		};


		return dbUtil.executeUpdate(sql, args) > 0;

	}

	/**
	 * 保存走访记录
	 * @param customerNote
	 * @return
	 * @throws Exception
	 */
	public boolean saveCustomerNote(CustomerNote customerNote) throws Exception {
		DbUtil dbUtil = new DbUtil(conn);

		String sql = " insert into oa_customernote( "
				   + " customerid,linkman,notetime,title,content,filename,filepath,username,udate,type0) "
				   + " values(?,?,?,?,?,?,?,?,now(),?) ";

		Object[] args = new Object[] {
				customerNote.getCustomerId(),
				customerNote.getLinkMan(),
				customerNote.getNoteTime(),
				customerNote.getTitle(),
				customerNote.getContent(),
				customerNote.getFileName(),
				customerNote.getFilePath(),
				customerNote.getUserName(),
				customerNote.getType0()
		};


		return dbUtil.executeUpdate(sql, args) > 0;
	}

	/**
	 * 删除走访记录
	 * @param autoId
	 * @return
	 * @throws Exception
	 */
	public boolean removeCustomerNote(String autoId) throws Exception {

		//删除附件
		try {
			CustomerNote customerNote = getCustomerNote(autoId);

			if(customerNote != null) {
				String filePath = customerNote.getFilePath();

				File file = new File(NOTE_FILE_PATH + filePath);

				System.out.println(file.getAbsolutePath());
				if(file.exists()) {
					//删除文件
					System.out.println("删除文件了..");
					file.delete();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}


		DbUtil dbUtil = new DbUtil(conn);

		String sql = " delete from oa_customernote where autoid=? ";

		Object[] args = new Object[] {
				autoId
		};


		return dbUtil.executeUpdate(sql, args) > 0;
	}

	/**
	 * 获得走访记录
	 * @param autoId
	 * @return
	 * @throws Exception
	 */
	public CustomerNote getCustomerNote(String autoId) throws Exception {
		CustomerNote customerNote = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = " select * " 
					   + " from oa_customernote "
					   + " where autoid=? ";

			ps = conn.prepareStatement(sql);
			ps.setString(1, autoId);

			rs = ps.executeQuery();

			if(rs.next()) {
				customerNote = new CustomerNote();
				customerNote.setAutoId(rs.getString("autoid"));
				customerNote.setCustomerId(rs.getString("customerid"));
				customerNote.setLinkMan(rs.getString("linkman"));
				customerNote.setNoteTime(rs.getString("notetime"));
				customerNote.setTitle(rs.getString("title"));
				customerNote.setContent(rs.getString("content"));
				customerNote.setFileName(rs.getString("filename"));
				customerNote.setFilePath(rs.getString("filepath"));
				customerNote.setUserName(rs.getString("username"));
				customerNote.setUdate(rs.getString("udate"));
				customerNote.setType0(rs.getString("type0"));
			}

		} catch (Exception e) {
			System.out.println("获得走访记录失败：" + e.getMessage());
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

		return customerNote;
	}
}
