package com.matech.audit.service.audittypetemplate.model;


/**
 * <p>Title: TODO</p>
 * <p>Description: TODO</p>
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
 * 2007-6-26
 */
public class AuditTypeTemplate {
	private String typeid;// 类型编号，

	private String typename;// 类型名称，

	private String content;

	private String des;

	private String property;// 属性

	private String updateTime;//更新时间

	private String updateUser;//更新人
	
	private String copyType;//拷贝类型
	
	private String password;
	
	private String vocationId;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCopyType() {
		return copyType;
	}

	public void setCopyType(String copyType) {
		this.copyType = copyType;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getTypeid() {
		return typeid;
	}

	public void setTypeid(String typeid) {
		this.typeid = typeid;
	}

	public String getTypename() {
		return typename;
	}

	public void setTypename(String typename) {
		this.typename = typename;
	}

	public String getVocationId() {
		return vocationId;
	}

	public void setVocationId(String vocationId) {
		this.vocationId = vocationId;
	}
}
