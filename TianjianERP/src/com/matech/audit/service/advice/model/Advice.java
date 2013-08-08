package com.matech.audit.service.advice.model;

/**
 * <p>Title: 审计意见</p>
 * <p>Description: 审计意见类</p>
 * <p>
 * 	Copyright: Copyright (c) 2006, 
 * 	2008 MaTech Corporation.All rights reserved.
 * </p>
 * <p>Company: Matech 广州铭太信息科技有限公司</p>
 * 
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有，
 * 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 * 
 * 贡献者团队: 铭太科技 - 研发中心，审计开发组
 * 
 * @author void 2007-7-3
 */
public class Advice {
	private int id; // autoId,自增

	private String projectId; // 项目Id

	private String taskId; // 底稿Id

	private String userId; // 用户登录名

	private String adviceDate; // 提交意见的日期时间,格式 YYYY-MM-DD hh-mm-ss

	private String advice; // 提交的意见

	private String adviceType; // 提交的意见类型:编制意见,二级审核意见,三级审核意见,退回意见
	
	private String userName; //用户真实姓名
	
	private String userLoginId; //用户真实姓名

	public String getUserLoginId() {
		return userLoginId;
	}

	public void setUserLoginId(String userLoginId) {
		this.userLoginId = userLoginId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the advice
	 */
	public String getAdvice() {
		return advice;
	}

	/**
	 * @param advice
	 *            the advice to set
	 */
	public void setAdvice(String advice) {
		this.advice = advice;
	}

	/**
	 * @return the adviceDate
	 */
	public String getAdviceDate() {
		return adviceDate;
	}

	/**
	 * @param adviceDate
	 *            the adviceDate to set
	 */
	public void setAdviceDate(String adviceDate) {
		this.adviceDate = adviceDate;
	}

	/**
	 * @return the adviceType
	 */
	public String getAdviceType() {
		return adviceType;
	}

	/**
	 * @param adviceType
	 *            the adviceType to set
	 */
	public void setAdviceType(String adviceType) {
		this.adviceType = adviceType;
	}

	/**
	 * @return the adviceUser
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param adviceUser
	 *            the adviceUser to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the projectId
	 */
	public String getProjectId() {
		return projectId;
	}

	/**
	 * @param projectId
	 *            the projectId to set
	 */
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	/**
	 * @return the taskId
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * @param taskId
	 *            the taskId to set
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

}
