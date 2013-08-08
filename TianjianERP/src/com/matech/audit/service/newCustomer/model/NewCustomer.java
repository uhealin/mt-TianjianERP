package com.matech.audit.service.newCustomer.model;

public class NewCustomer {
	
	  private String uuid ; //VAVRCHAR(100) NOT NULL,
	  private String customerName ; //VAVRCHAR(100) DEFAULT NULL COMMENT '客户名称',1
	  private String projestId ; //VAVRCHAR(50) DEFAULT NULL COMMENT '项目编号',1
	  private String belongsIndustry ; //VAVRCHAR(200) DEFAULT NULL COMMENT '所属行业',1
	  private String client ; //VAVRCHAR(100) DEFAULT NULL COMMENT '委托方',1
	  private String runScope ; //VAVRCHAR(100) DEFAULT NULL COMMENT '经营范围',1
	  private String province ; //VAVRCHAR(30) DEFAULT NULL COMMENT '所属省',1
	  private String city ; //VAVRCHAR(30) DEFAULT NULL COMMENT '所属市',1
	  private String projestPartner ; //VAVRCHAR(30) DEFAULT NULL COMMENT '我所项目负责人(合伙人)',1
	  private String projestManager ; //VAVRCHAR(30) DEFAULT NULL COMMENT '我所项目负责人(高级经理)',1
	  private String businessNature ; //VAVRCHAR(100) DEFAULT NULL COMMENT '业务性质',(业务性质)1
	  private String deadlineDate ; //VAVRCHAR(30) DEFAULT NULL COMMENT '接洽截止日期',1
	  private String signBook ; //VAVRCHAR(15) DEFAULT NULL COMMENT '是否签订业务接洽书',
	  private String mainShareholder ; //VAVRCHAR(30) DEFAULT NULL COMMENT '主要股东',1
	  private String mainExecutives ; //VAVRCHAR(30) DEFAULT NULL COMMENT '主要高管人员',1
	  private String predecessorOffice ; //VAVRCHAR(200) DEFAULT NULL COMMENT '前任事务所',1
	  private String corporationCount ; //VAVRCHAR(20) DEFAULT NULL COMMENT '主要控股子公司家数',1
	  private String content;  //TEXT COMMENT '服务内容',1
	  private String oneBearUserId ; //VAVRCHAR(20) DEFAULT NULL COMMENT '第一承做人',1
	  private String twoBearUserId ; //VAVRCHAR(20) DEFAULT NULL COMMENT '第二承做人',1
	  private String customerSource ; //VAVRCHAR(50) DEFAULT NULL COMMENT '客户来源',1
	  private String optQuality ; //VAVRCHAR(50) DEFAULT NULL COMMENT '业务承接性质',1
	  private String optDepartment ; //VAVRCHAR(50) DEFAULT NULL COMMENT '业务承接部门',1
	  private String mobilePhone ; //VAVRCHAR(20) DEFAULT NULL COMMENT '手机',1
	  private String phone ; //VAVRCHAR(20) DEFAULT NULL COMMENT '电话',1
	  private String remark;  //TEXT COMMENT '备注',
	  private String createDate ; //VAVRCHAR(30) DEFAULT NULL COMMENT '创建时间',
	  private String createUser ; //VAVRCHAR(30) DEFAULT NULL COMMENT '创建人',
	  private String property ; //VAVRCHAR(100) DEFAULT NULL COMMENT '备用',
	  private String state ; //VAVRCHAR(50) DEFAULT NULL COMMENT '状态',
	  
	  
		public String getState() {
		return state;
		}
		public void setState(String state) {
		this.state = state;
		}
		public String getUuid() {
			return uuid;
		}
		public void setUuid(String uuid) {
			this.uuid = uuid;
		}
		public String getCustomerName() {
			return customerName;
		}
		public void setCustomerName(String customerName) {
			this.customerName = customerName;
		}
		public String getProjestId() {
			return projestId;
		}
		public void setProjestId(String projestId) {
			this.projestId = projestId;
		}
		public String getBelongsIndustry() {
			return belongsIndustry;
		}
		public void setBelongsIndustry(String belongsIndustry) {
			this.belongsIndustry = belongsIndustry;
		}
		public String getClient() {
			return client;
		}
		public void setClient(String client) {
			this.client = client;
		}
		public String getRunScope() {
			return runScope;
		}
		public void setRunScope(String runScope) {
			this.runScope = runScope;
		}
		public String getProvince() {
			return province;
		}
		public void setProvince(String province) {
			this.province = province;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		public String getProjestPartner() {
			return projestPartner;
		}
		public void setProjestPartner(String projestPartner) {
			this.projestPartner = projestPartner;
		}
		public String getProjestManager() {
			return projestManager;
		}
		public void setProjestManager(String projestManager) {
			this.projestManager = projestManager;
		}
		public String getBusinessNature() {
			return businessNature;
		}
		public void setBusinessNature(String businessNature) {
			this.businessNature = businessNature;
		}
		public String getDeadlineDate() {
			return deadlineDate;
		}
		public void setDeadlineDate(String deadlineDate) {
			this.deadlineDate = deadlineDate;
		}
		public String getSignBook() {
			return signBook;
		}
		public void setSignBook(String signBook) {
			this.signBook = signBook;
		}
		public String getMainShareholder() {
			return mainShareholder;
		}
		public void setMainShareholder(String mainShareholder) {
			this.mainShareholder = mainShareholder;
		}
		public String getMainExecutives() {
			return mainExecutives;
		}
		public void setMainExecutives(String mainExecutives) {
			this.mainExecutives = mainExecutives;
		}
		public String getPredecessorOffice() {
			return predecessorOffice;
		}
		public void setPredecessorOffice(String predecessorOffice) {
			this.predecessorOffice = predecessorOffice;
		}
		public String getCorporationCount() {
			return corporationCount;
		}
		public void setCorporationCount(String corporationCount) {
			this.corporationCount = corporationCount;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getOneBearUserId() {
			return oneBearUserId;
		}
		public void setOneBearUserId(String oneBearUserId) {
			this.oneBearUserId = oneBearUserId;
		}
		public String getTwoBearUserId() {
			return twoBearUserId;
		}
		public void setTwoBearUserId(String twoBearUserId) {
			this.twoBearUserId = twoBearUserId;
		}
		public String getCustomerSource() {
			return customerSource;
		}
		public void setCustomerSource(String customerSource) {
			this.customerSource = customerSource;
		}
		public String getOptQuality() {
			return optQuality;
		}
		public void setOptQuality(String optQuality) {
			this.optQuality = optQuality;
		}
		public String getOptDepartment() {
			return optDepartment;
		}
		public void setOptDepartment(String optDepartment) {
			this.optDepartment = optDepartment;
		}
		public String getMobilePhone() {
			return mobilePhone;
		}
		public void setMobilePhone(String mobilePhone) {
			this.mobilePhone = mobilePhone;
		}
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		public String getRemark() {
			return remark;
		}
		public void setRemark(String remark) {
			this.remark = remark;
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
		public String getProperty() {
			return property;
		}
		public void setProperty(String property) {
			this.property = property;
		}
}
