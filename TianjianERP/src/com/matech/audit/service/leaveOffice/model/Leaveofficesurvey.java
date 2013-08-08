package com.matech.audit.service.leaveOffice.model;

/**
 * 离职调查表
 * @author Ymm
 *
 */
public class Leaveofficesurvey {

	private String autoId;   //
	private String employmentType; //再就业流向
	private String userRelation; // 人际关系
	private String superiorRelation; // 上下级关系
	private String workEnvironment; // 办公环境
	private String leaveOfficeReason; // 离职原因
	private String suggest; //  改进建议
	private String createUser;// 创建人员
	private String createDepartmentId; //创建部门
	private String creataDate; //创建时间
	private String property; //备用
	
	public String getAutoId() {
		return autoId;
	}
	public void setAutoId(String autoId) {
		this.autoId = autoId;
	}
	public String getEmploymentType() {
		return employmentType;
	}
	public void setEmploymentType(String employmentType) {
		this.employmentType = employmentType;
	}
	public String getUserRelation() {
		return userRelation;
	}
	public void setUserRelation(String userRelation) {
		this.userRelation = userRelation;
	}
	public String getSuperiorRelation() {
		return superiorRelation;
	}
	public void setSuperiorRelation(String superiorRelation) {
		this.superiorRelation = superiorRelation;
	}
	public String getWorkEnvironment() {
		return workEnvironment;
	}
	public void setWorkEnvironment(String workEnvironment) {
		this.workEnvironment = workEnvironment;
	}
	public String getLeaveOfficeReason() {
		return leaveOfficeReason;
	}
	public void setLeaveOfficeReason(String leaveOfficeReason) {
		this.leaveOfficeReason = leaveOfficeReason;
	}
	public String getSuggest() {
		return suggest;
	}
	public void setSuggest(String suggest) {
		this.suggest = suggest;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getCreateDepartmentId() {
		return createDepartmentId;
	}
	public void setCreateDepartmentId(String createDepartmentId) {
		this.createDepartmentId = createDepartmentId;
	}
	public String getCreataDate() {
		return creataDate;
	}
	public void setCreataDate(String creataDate) {
		this.creataDate = creataDate;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	
}
