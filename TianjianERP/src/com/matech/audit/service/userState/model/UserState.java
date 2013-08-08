package com.matech.audit.service.userState.model;

/**
 * <p>
 * Title: 用户状态类
 * </p>
 * <p>
 * Description: 用户状态类
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006, 2008 MaTech Corporation.All rights reserved.
 * </p>
 * <p>
 * Company: Matech 广州铭太信息科技有限公司
 * </p>
 * 
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有， 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 * 
 * 贡献者团队: 铭太科技 - 研发中心，审计开发组
 * 
 * @author void 2007-8-19
 */
public class UserState {
	private String autoId; // 自动编号
	private String userId; // 用户编号
	private String projectId; // 项目编号
	private String lastTaskId; // 最后打开的任务

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getAutoId() {
		return autoId;
	}

	public void setAutoId(String autoId) {
		this.autoId = autoId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLastTaskId() {
		return lastTaskId;
	}

	public void setLastTaskId(String lastTaskId) {
		this.lastTaskId = lastTaskId;
	}

}
