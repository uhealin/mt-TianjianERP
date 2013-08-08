package com.matech.audit.service.customer.model;
/**
 * 客户立项管理
 * @author Ymm
 *立项表
	  CREATE TABLE `k_customerproject` (
	  `autoId` int(11) NOT NULL auto_increment,
	  `customerId`  ;//varchar(100) default NULL,
	  `customerName`  ;//varchar(300) default NULL,
	  `customerRank`  ;//varchar(200) default NULL COMMENT '客户级别',
	  `customerSource`  ;//varchar(250) default NULL COMMENT '客户来源',
	  `businessType`  ;//varchar(250) default NULL COMMENT '行业类型',
	  `properties`  ;//varchar(200) default NULL COMMENT '公司性质',
	  `projectType`  ;//varchar(200) default NULL COMMENT '项目类型',
	  `contractMoney`  ;//varchar(120) default NULL COMMENT '合同金额',
	  `workingHours`  ;//varchar(50) default NULL COMMENT '预计工时',
	  `admissionTime`  ;//varchar(150) default NULL COMMENT '入场时间',
	  `distributeUser`  ;//varchar(100) default NULL COMMENT '分配人',
	  `followUser`  ;//varchar(100) default NULL COMMENT '跟进人',
	  `auditUser`  ;//varchar(100) default NULL COMMENT '审批人',
	  `state`  ;//varchar(100) default NULL COMMENT '状态',
	  `createUser`  ;//varchar(100) default NULL COMMENT '创建人',
	  `createDate`  ;//varchar(150) default NULL COMMENT '创建时间',
	  `createDepartment`  ;//varchar(100) default NULL COMMENT '创建部门',
	  PRIMARY KEY  (`autoId`),
	  KEY `NewIndex1` (`customerId`),
	  KEY `NewIndex2` (`followUser`),
	  KEY `NewIndex3` (`distributeUser`),
	  KEY `NewIndex4` (`customerId`)
	) ENGINE=MyISAM DEFAULT CHARSET=gbk
 */
public class CustomerProject {
	private String autoId;//int(11) NOT NULL
	private String customerId ;//varchar(100) NULL
	private String customerName ;//varchar(300) NULL
	private String customerRank ;//varchar(200) NULL客户级别
	private String customerSource ;//varchar(250) NULL客户来源
	private String businessType ;//varchar(250) NULL行业类型
	private String properties ;//varchar(200) NULL公司性质
	private String projectType ;//varchar(200) NULL项目类型
	private String contractMoney ;//varchar(120) NULL合同金额
	private String workingHours ;//varchar(50) NULL预计工时
	private String admissionTime ;//varchar(150) NULL入场时间
	private String distributeUser ;//varchar(100) NULL分配人
	private String followUser ;//varchar(100) NULL跟进人
	private String auditUser ;//varchar(100) NULL审批人
	private String state ;//varchar(100) NULL状态
	private String createUser ;//varchar(100) NULL创建人
	private String createDate ;//varchar(150) NULL创建时间
	private String createDepartment ;//varchar(100) NULL创建部门
	private String reportFileName ;//varchar(100) 上传文件名
	private String reportFileTempName;//空的文件
	private String filename = "" ;
	private String filetempname = "" ;
	
	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getFiletempname() {
		return filetempname;
	}
	public void setFiletempname(String filetempname) {
		this.filetempname = filetempname;
	}
	public String getReportFileTempName() {
		return reportFileTempName;
	}
	public void setReportFileTempName(String reportFileTempName) {
		this.reportFileTempName = reportFileTempName;
	}
	public String getReportFileName() {
		return reportFileName;
	}
	public void setReportFileName(String reportFileName) {
		this.reportFileName = reportFileName;
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
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getCustomerRank() {
		return customerRank;
	}
	public void setCustomerRank(String customerRank) {
		this.customerRank = customerRank;
	}
	public String getCustomerSource() {
		return customerSource;
	}
	public void setCustomerSource(String customerSource) {
		this.customerSource = customerSource;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public String getProperties() {
		return properties;
	}
	public void setProperties(String properties) {
		this.properties = properties;
	}
	public String getProjectType() {
		return projectType;
	}
	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}
	public String getContractMoney() {
		return contractMoney;
	}
	public void setContractMoney(String contractMoney) {
		this.contractMoney = contractMoney;
	}
	public String getWorkingHours() {
		return workingHours;
	}
	public void setWorkingHours(String workingHours) {
		this.workingHours = workingHours;
	}
	public String getAdmissionTime() {
		return admissionTime;
	}
	public void setAdmissionTime(String admissionTime) {
		this.admissionTime = admissionTime;
	}
	public String getDistributeUser() {
		return distributeUser;
	}
	public void setDistributeUser(String distributeUser) {
		this.distributeUser = distributeUser;
	}
	public String getFollowUser() {
		return followUser;
	}
	public void setFollowUser(String followUser) {
		this.followUser = followUser;
	}
	public String getAuditUser() {
		return auditUser;
	}
	public void setAuditUser(String auditUser) {
		this.auditUser = auditUser;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getCreateDepartment() {
		return createDepartment;
	}
	public void setCreateDepartment(String createDepartment) {
		this.createDepartment = createDepartment;
	}
}
