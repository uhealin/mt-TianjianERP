package com.matech.audit.service.user.model;

/**
 * 员工报到(社保信息表)
 * @author Ymm
 * k_staffsocialsecurity
 */
public class StaffSocialseCurity {
	private String autoId; //int(11) NOT NULL
	private String id ; //(150) NULL所属人员(k_staffregister)
	private String paySort ; //(100) NULL缴费人员类别
	private String insuredSort ; //(100) NULL参保类别
	private String firstJobTime ; //(100) NULL首次参加工作日期
	private String baseNumber ; //(100) NULL上一单位医疗基数
	private String residencePermit ; //(100) NULL是否有工作居住证
	private String property ; //(100) NULL备用
	private String createDate ; //(150) NULL创建时间
	private String createUser ; //(100) NULL创建人
	private String createDepartment ; //(100) NULL创建部门
	
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
	public String getPaySort() {
		return paySort;
	}
	public void setPaySort(String paySort) {
		this.paySort = paySort;
	}
	public String getInsuredSort() {
		return insuredSort;
	}
	public void setInsuredSort(String insuredSort) {
		this.insuredSort = insuredSort;
	}
	public String getFirstJobTime() {
		return firstJobTime;
	}
	public void setFirstJobTime(String firstJobTime) {
		this.firstJobTime = firstJobTime;
	}
	public String getBaseNumber() {
		return baseNumber;
	}
	public void setBaseNumber(String baseNumber) {
		this.baseNumber = baseNumber;
	}
	public String getResidencePermit() {
		return residencePermit;
	}
	public void setResidencePermit(String residencePermit) {
		this.residencePermit = residencePermit;
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
