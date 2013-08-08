package com.matech.audit.service.user.model;

/**
 *  员工报到(员工联络卡)
 * @author Ymm
 *k_staffliaison
 */
public class StaffLiaison {
	private String autoId ;//int(11) NOT NULL
	private String id ;//(100) NULL所属人员
	private String residence ;//(250) NULL户籍所在地
	private String policeSubstation ;//(150) NULL所辖派出所
	private String homeAddress ;//(300) NULL家庭住址
	private String homePostcode ;//(50) NULL家庭住址的邮编
	private String mailAddress ;//(300) NULL邮寄地址
	private String mailPostcode ;//(50) NULL邮寄的邮编
	private String homeTel ;//(50) NULL家庭电话
	private String urgencyTel ;//(50) NULL紧急电话
	private String msn ;//(50) NULLmsn
	private String qq ;//(20) NULLqq
	private String archivesPlace ;//(300) NULL档案存放地点
	private String archivesId ;//(50) NULL存档编号
	private String property ;//(200) NULL备用
	private String createDate ;//(150) NULL创建时间
	private String createUser ;//(100) NULL创建人员
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
	public String getResidence() {
		return residence;
	}
	public void setResidence(String residence) {
		this.residence = residence;
	}
	public String getPoliceSubstation() {
		return policeSubstation;
	}
	public void setPoliceSubstation(String policeSubstation) {
		this.policeSubstation = policeSubstation;
	}
	public String getHomeAddress() {
		return homeAddress;
	}
	public void setHomeAddress(String homeAddress) {
		this.homeAddress = homeAddress;
	}
	public String getHomePostcode() {
		return homePostcode;
	}
	public void setHomePostcode(String homePostcode) {
		this.homePostcode = homePostcode;
	}
	public String getMailAddress() {
		return mailAddress;
	}
	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}
	public String getMailPostcode() {
		return mailPostcode;
	}
	public void setMailPostcode(String mailPostcode) {
		this.mailPostcode = mailPostcode;
	}
	public String getHomeTel() {
		return homeTel;
	}
	public void setHomeTel(String homeTel) {
		this.homeTel = homeTel;
	}
	public String getUrgencyTel() {
		return urgencyTel;
	}
	public void setUrgencyTel(String urgencyTel) {
		this.urgencyTel = urgencyTel;
	}
	public String getMsn() {
		return msn;
	}
	public void setMsn(String msn) {
		this.msn = msn;
	}
	public String getQq() {
		return qq;
	}
	public void setQq(String qq) {
		this.qq = qq;
	}
	public String getArchivesPlace() {
		return archivesPlace;
	}
	public void setArchivesPlace(String archivesPlace) {
		this.archivesPlace = archivesPlace;
	}
	public String getArchivesId() {
		return archivesId;
	}
	public void setArchivesId(String archivesId) {
		this.archivesId = archivesId;
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
