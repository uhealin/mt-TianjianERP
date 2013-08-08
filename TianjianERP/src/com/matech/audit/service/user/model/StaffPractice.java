package com.matech.audit.service.user.model;

/**
 * 员工报到( 执 业 信 息)
 * @author Ymm
 * k_staffPractice
 */
public class StaffPractice {
	private String autoId ;// int(11) NOT NULL
	private String id  ;//(100) NULL所属人员Id
	private String spname ;//(300) NULL资质名称
	private String rank ;//(200) NULL资质等级
	private String cNumber ;//(200) NULL证书编号
	private String ratifyOrgan ;//(300) NULL认可机关
	private String yearMax ;//(50) NULL批准年限
	private String referenceNumber ;//(200) NULL批准文号
	private String qualifiedCertificate ;//(200) NULL合格证号
	private String property ;//(200) NULL备用
	private String createDate ;//(150) NULL创建时间
	private String createUser ;//(100) NULL创建人
	private String createDepartment ;//(100) NULL创建部门
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
	public String getSpname() {
		return spname;
	}
	public void setSpname(String spname) {
		this.spname = spname;
	}
	public String getRank() {
		return rank;
	}
	public void setRank(String rank) {
		this.rank = rank;
	}
	public String getcNumber() {
		return cNumber;
	}
	public void setcNumber(String cNumber) {
		this.cNumber = cNumber;
	}
	public String getRatifyOrgan() {
		return ratifyOrgan;
	}
	public void setRatifyOrgan(String ratifyOrgan) {
		this.ratifyOrgan = ratifyOrgan;
	}
	public String getYearMax() {
		return yearMax;
	}
	public void setYearMax(String yearMax) {
		this.yearMax = yearMax;
	}
	public String getReferenceNumber() {
		return referenceNumber;
	}
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}
	public String getQualifiedCertificate() {
		return qualifiedCertificate;
	}
	public void setQualifiedCertificate(String qualifiedCertificate) {
		this.qualifiedCertificate = qualifiedCertificate;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
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
	
	
	
}
