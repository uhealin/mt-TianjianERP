package com.matech.audit.service.oa.learncircs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.oa.learncircs.model.LearncircsTable;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.Debug;

public class LearncircsService {
	private Connection conn = null;

	public LearncircsService(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 删除信息
	 * 
	 * @param id
	 * @throws MatechException
	 */
	public void del(String id) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		try {
			sql = "delete from oa_learncircs where autoid = '" + id + "' ";
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 添加信息
	 * 
	 * @param lt
	 * @throws MatechException
	 */
	public void add(LearncircsTable lt) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		try {
			sql = " insert into oa_learncircs(userid,startlearndate,endlearndate,learncontent,learncertificate,"
					+ " learnachievement,learnframework,learnlocus,checkinperson,checkindate,remark,property,uploadFileName,uploadTempName,"
					+ " learntype,learnperiod) "
					+ " values (?,?,?,?,?,?,?,?,?,now(),?,?,?,?,?,?)";
			System.out.println("sql:" + sql);
			ps = conn.prepareStatement(sql);
			ps.setString(1, lt.getUserid());
			ps.setString(2, lt.getStartlearndate());
			ps.setString(3, lt.getEndlearndate());
			ps.setString(4, lt.getLearncontent());
			ps.setString(5, lt.getLearncertificate());
			ps.setString(6, lt.getLearnachievement());
			ps.setString(7, lt.getLearnframework());
			ps.setString(8, lt.getLearnlocus());
			ps.setString(9, lt.getCheckinperson());
			ps.setString(10, lt.getRemark());
			ps.setString(11, "");
			ps.setString(12,lt.getFileNames()) ;
			ps.setString(13, lt.getFileRondomNames()) ;
			ps.setString(14, lt.getLearntype());
			ps.setString(15, lt.getLearnperiod());
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 根据指定的ID返回整条记录的值
	 * 
	 * @param id
	 * @return
	 * @throws MatechException
	 */
	public LearncircsTable getLearncircs(String id) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		LearncircsTable lt = new LearncircsTable();
		try {
			sql = "select *  from oa_learncircs where autoid = '"+ id + "' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				lt.setAutoid(rs.getInt("autoid"));
				lt.setUserid(rs.getString("userid"));
				lt.setStartlearndate(rs.getString("startlearndate"));
				lt.setEndlearndate(rs.getString("endlearndate"));
				lt.setLearncontent(rs.getString("learncontent"));
				lt.setLearncertificate(rs.getString("learncertificate"));
				lt.setLearnachievement(rs.getString("learnachievement"));
				lt.setLearnframework(rs.getString("learnframework"));
				lt.setLearnlocus(rs.getString("learnlocus"));
				lt.setCheckinperson(rs.getString("checkinperson"));
				lt.setCheckindate(rs.getString("checkindate"));
				lt.setRemark(rs.getString("remark"));
				lt.setProperty(rs.getString("property"));
				lt.setFileNames(rs.getString("uploadFileName")) ;
				lt.setFileRondomNames(rs.getString("uploadTempName")) ;
				lt.setLearntype(rs.getString("learntype"));
				lt.setLearnperiod(rs.getString("learnperiod"));
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return lt;
	}

	/**
	 * 更新信息
	 * 
	 * @param lt
	 * @throws MatechException
	 */
	public void update(LearncircsTable lt) throws MatechException {
		DbUtil.checkConn(conn);
		String sql = "";
		PreparedStatement ps = null;
		try {
			sql = "update oa_learncircs set userid=?,startlearndate=?,endlearndate=?,learncontent=?,learncertificate=?,learnachievement=?,learnframework=?,"
					+ "learnlocus=?,remark=?,uploadFileName=?,uploadTempName=?,learntype=?,learnperiod=? where autoid=?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, lt.getUserid());
			ps.setString(2, lt.getStartlearndate());
			ps.setString(3, lt.getEndlearndate());
			ps.setString(4, lt.getLearncontent());
			ps.setString(5, lt.getLearncertificate());
			ps.setString(6, lt.getLearnachievement());
			ps.setString(7, lt.getLearnframework());
			ps.setString(8, lt.getLearnlocus());
			ps.setString(9, lt.getRemark());
			ps.setString(10,lt.getFileNames()) ;
			ps.setString(11, lt.getFileRondomNames()) ;
			
			ps.setString(12, lt.getLearntype()) ;
			ps.setString(13, lt.getLearnperiod()) ;
			
			ps.setInt(14, lt.getAutoid());
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError, "访问失败", e);
			throw new MatechException("访问失败：" + e.getMessage(), e);
		} finally {
			DbUtil.close(ps);
		}
	}

}
