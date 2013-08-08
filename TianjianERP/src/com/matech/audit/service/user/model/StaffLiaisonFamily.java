package com.matech.audit.service.user.model;

/**
 * 员工报到(员工家庭情况)
 * @author Ymm
 * k_staffliaisonfamily
 */
public class StaffLiaisonFamily {
	
	private String autoId; // int(11) NOT NULL
	private String id ;//(100) NULL外键ID
	private String familyName ;//(50) NULL姓名
	private String relation ;//(100) NULL关系
	private String jobPlace ;//(300) NULL工作/学习单位
	private String tel ;//(100) NULL电话
	private String identityCard ;//(200) NULL身份证
	private String property ;//(200) NULL备用
	private String createDate; // varchar(150) NULL创建日期
	private String createUser; // varchar(100) NULL创建人
	private String createDepartment; // varchar(100) NULL创建部门
	
	
	public String getFamilyName() {
		return familyName;
	}
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getCreateDepartment() {
		return createDepartment;
	}
	public void setCreateDepartment(String createDepartment) {
		this.createDepartment = createDepartment;
	}
	public String getAutoId() {
		return autoId;
	}
	public void setAutoId(String autoId) {
		this.autoId = autoId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRelation() {
		return relation;
	}
	public void setRelation(String relation) {
		this.relation = relation;
	}
	public String getJobPlace() {
		return jobPlace;
	}
	public void setJobPlace(String jobPlace) {
		this.jobPlace = jobPlace;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getIdentityCard() {
		return identityCard;
	}
	public void setIdentityCard(String identityCard) {
		this.identityCard = identityCard;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	
	
}
