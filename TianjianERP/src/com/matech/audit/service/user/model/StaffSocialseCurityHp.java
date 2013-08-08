package com.matech.audit.service.user.model;

/**
 *  社保医疗信息 stsoseId 外键
 * @author Ymm
 *k_staffsocialsecurityhp
 */
public class StaffSocialseCurityHp {
	private String autoid;// int(11) NOT NULL
	private String id;// int(11) NOT NULL
	private String stsoseId ;//(100) NULL社保外键
	private String organizationNames ;//(200) NULL医疗定点机构名称
	private String coding ;//(150) NULL编码
	private String hospitalName ;//(250) NULL医院名称
	private String property ;//(200) NULL备用
	private String createDate; //varchar(150) NULL创建时间
	private String createUser; //varchar(100) NULL创建人员
	private String createDepartment; //tvarchar(100) NULL创建部门
	
	
	public String getCreateDepartment() {
		return createDepartment;
	}
	public void setCreateDepartment(String createDepartment) {
		this.createDepartment = createDepartment;
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
	 
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAutoid() {
		return autoid;
	}
	public void setAutoid(String autoid) {
		this.autoid = autoid;
	}
	public String getStsoseId() {
		return stsoseId;
	}
	public void setStsoseId(String stsoseId) {
		this.stsoseId = stsoseId;
	}
	public String getOrganizationNames() {
		return organizationNames;
	}
	public void setOrganizationNames(String organizationNames) {
		this.organizationNames = organizationNames;
	}
	public String getCoding() {
		return coding;
	}
	public void setCoding(String coding) {
		this.coding = coding;
	}
	public String getHospitalName() {
		return hospitalName;
	}
	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	
	
}
