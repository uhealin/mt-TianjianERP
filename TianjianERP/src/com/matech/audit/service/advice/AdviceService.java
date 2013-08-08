package com.matech.audit.service.advice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.advice.model.Advice;
import com.matech.framework.pub.db.DbUtil;

/**
 * <p>Title: 底稿意见类</p>
 * <p>Description: 管理底稿意见</p>
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
 * 2007-7-5
 */
public class AdviceService {
	private Connection conn = null;

	private String projectId = null;
	
	/**
	 * 构造方法
	 * @param conn 连接
	 * @param projectId	项目编号
	 * @throws Exception
	 */
	public AdviceService(Connection conn, String projectId) throws Exception {
		DbUtil.checkConn(conn);
		this.conn = conn;
		
		if("".equals(projectId) || projectId == null) {
			throw new Exception("项目Id不能为空!");
		}
		this.projectId = projectId;
	}
	/**
	 * 添加底稿意见
	 * 
	 * @param advice	底稿意见的model对象
	 * @throws Exception
	 */
	public void addAdvice(Advice advice) throws Exception {
		PreparedStatement ps = null;

		try {
			new DBConnect().changeDataBaseByProjectid(this.conn, this.projectId);
			String strSql  = "insert into z_auditAdvice "
							+ " (projectId,taskId,UserId,advice,adviceType,adviceDate) "
							+ " values(?,?,?,?,?,now())";
			ps = conn.prepareStatement(strSql);

			ps.setString(1, this.projectId);
			ps.setString(2, advice.getTaskId());
			ps.setString(3, advice.getUserId());
			ps.setString(4, advice.getAdvice());
			ps.setString(5, advice.getAdviceType());

			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}

	/**
	 * 根据底稿ID查询出意见集,将每一条查询出来的记录转换成model对象,然后放入List里,传递给jsp显示
	 * @param TaskId 底稿Id	
	 * @return List	存放model对象的集合
	 * @throws Exception
	 */
	public List getAdviceByTaskId(String taskId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		List adviceList = null;
		Advice advice = null;
		
		try {
			new DBConnect().changeDataBaseByProjectid(this.conn, this.projectId);
			adviceList = new ArrayList();
			
			String strSql = "select a.*,u.loginId,u.name "
							+ " from z_auditadvice a,k_user u"
							+ " where projectId = ? "
							+ " and a.taskId = ? "
							+ " and a.userid = u.id "
							+ " order by a.adviceDate ";
			ps = conn.prepareStatement(strSql);

			ps.setString(1, this.projectId);
			ps.setString(2, taskId);

			rs = ps.executeQuery();
			
			while(rs.next()) {
				advice = new Advice();
				
				advice.setId(rs.getInt("id"));
				advice.setProjectId(rs.getString("projectId"));
				advice.setTaskId(rs.getString("taskId"));
				advice.setAdviceDate(rs.getString("adviceDate"));
				advice.setUserId(rs.getString("UserId"));
				advice.setAdvice(rs.getString("advice"));
				advice.setAdviceType(rs.getString("adviceType"));
				advice.setUserName(rs.getString("name"));
				advice.setUserLoginId(rs.getString("loginId"));
				
				adviceList.add(advice);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return adviceList;
	}
	
	/**
	 * 查询出意见集,将每一条查询出来的记录转换成model对象,然后放入List里,传递给jsp显示
	 * @return List	存放model对象的集合
	 * @throws Exception
	 */
	public List getAdviceByPid() throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		List adviceList = null;
		Advice advice = null;
		
		try {
			new DBConnect().changeDataBaseByProjectid(this.conn, this.projectId);
			adviceList = new ArrayList();
			String strSql = "select a.*,u.loginId,u.name "
							+ " from z_auditadvice a,k_user u"
							+ " where projectId = ? "
							+ " and a.userid = u.id "
							+ " order by adviceDate,taskId";
			ps = conn.prepareStatement(strSql);
			ps.setString(1, this.projectId);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				advice = new Advice();
				
				advice.setId(rs.getInt("id"));
				advice.setProjectId(rs.getString("projectId"));
				advice.setTaskId(rs.getString("taskId"));
				advice.setAdviceDate(rs.getString("adviceDate"));
				advice.setUserId(rs.getString("UserId"));
				advice.setAdvice(rs.getString("advice"));
				advice.setAdviceType(rs.getString("adviceType"));
				advice.setUserName("name");
				advice.setUserLoginId("loginId");
				
				adviceList.add(advice);
			}
			
			return adviceList;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	/**
	 * 记录最后保存人
	 * @param projectId
	 * @param taskId
	 * @param userName
	 * @throws Exception
	 */
	public void lastModified(String projectId,String taskId,String userId) throws Exception {
		PreparedStatement ps = null;
		try {
			new DBConnect().changeDataBaseByProjectid(this.conn, this.projectId);
			String strSql = "update z_task set username=(select name from k_user where id="+userId+") where taskid="+taskId+" and projectid="+projectId+"";
			System.out.println("strSql:"+strSql);
			ps = conn.prepareStatement(strSql);
			ps.execute();
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	public static void main(String args[]) throws Exception {
		Connection conn = new DBConnect().getConnect("");
		try {
			AdviceService adviceService = new AdviceService(conn,"2007683");
			
			System.out.println(adviceService.getAdviceByTaskId("10501"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
	}
	
}
