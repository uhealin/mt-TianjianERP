package com.matech.audit.service.scan;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.matech.audit.service.scan.model.Scan;
import com.matech.framework.pub.db.DbUtil;

public class ScanService {
	private Connection conn=null;
	
	public ScanService(Connection conn){
		this.conn=conn;
	}
	
	/*
	 * 插入一条扫描件上传记录
	 */
	public void add(Scan scan,String userId){
		PreparedStatement ps=null;
		try {
			String sql="insert into k_scan (scanname,remark,attachment,userid) values(?,?,?,?)";
			ps=conn.prepareStatement(sql);
			int i=1;
			ps.setString(i++, scan.getScanName());
			ps.setString(i++, scan.getRemark());
			ps.setString(i++, scan.getAttachment());
			ps.setString(i++, userId);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
}
