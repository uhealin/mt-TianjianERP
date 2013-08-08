package com.matech.audit.service.customer.model;

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
public class CustomerNote {
	private String autoId; //自动编号
	private String customerId; //客户编号
	private String linkMan; //联系人
	private String title; //标题
	private String content; //内容
	private String fileName; //文件名
	private String filePath; //存储的路径
	private String userName; //记录人
	private String noteTime; //走访时间
	private String udate; //记录时间

	private String type0; //服务类型
	
	public String getType0() {
		return type0;
	}

	public void setType0(String type0) {
		this.type0 = type0;
	}

	public String getAutoId() {
		return autoId;
	}

	public void setAutoId(String autoId) {
		this.autoId = autoId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getLinkMan() {
		return linkMan;
	}

	public void setLinkMan(String linkMan) {
		this.linkMan = linkMan;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNoteTime() {
		return noteTime;
	}

	public void setNoteTime(String noteTime) {
		this.noteTime = noteTime;
	}

	public String getUdate() {
		return udate;
	}

	public void setUdate(String udate) {
		this.udate = udate;
	}
}
