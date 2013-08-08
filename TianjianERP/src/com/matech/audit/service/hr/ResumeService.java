package com.matech.audit.service.hr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import com.matech.audit.service.hr.model.ResumeVO;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.Debug;

public class ResumeService {
	private Connection conn = null;

	public ResumeService(Connection conn1) 
			throws Exception {
		this.conn = conn1;
	}
	//建立类似k_resume2的表
	public void newTable() throws MatechException{
		delTable();
		PreparedStatement ps = null;
		try{
			String sql ="create table tt_k_resume2 like k_resume";
			ps = conn.prepareStatement(sql);
			ps.execute();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	//删除创建的临时表
	public void delTable() throws MatechException {
		String sql = "DROP TABLE  IF EXISTS tt_k_resume2";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
		} finally {
			DbUtil.close(ps);
		}

	}

}
