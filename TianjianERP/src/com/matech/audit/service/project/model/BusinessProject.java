package com.matech.audit.service.project.model;

public class BusinessProject {
	
	private String projectID = "" ; 
	private String projectName = "" ;
	private String EntrustCustomerId = "" ;
	private String customerId = "" ;
	private String payCustomerId = "" ;
	private String auditpara = "" ;
	private String typeId = "" ;
	private String isSpecialProject = "" ;
	private String isNewTakeProject = "" ;
	private String isReport = "" ;
	private String customerType = "" ;
	private String managerUserId = "" ;
	private String departManagerUserId = "" ;
	private String partnerUserId = "" ;
	private String ristPartnerUserId = "" ;
	private String seniorCpaUserId = "" ;
	private String projectPartner1 = "" ;
	private String projectPartner2 = "" ;
	private String qualityPartner = "" ;
	private String ristLevel = "" ;
	private String isStore = "" ;
	private String signedDate = "" ;
	private String businessCost = "" ;
	private String state = "" ;
	private String property = "" ;
	private String entrustNumber = "" ;
	private String reportNumeber = "" ;
	private String signedCpa1 = "" ;
	private String signedCpa2 = "" ;
	private String departmentId = "" ;
	private String creator = "" ;
	private String createTime = "" ;
	private String companyType = "" ;
	private String business = "" ;
	private String contactUser = "" ;
	private String contactPhone = "" ;
	private String businesChannel = "" ;
	private String reportRequire = "" ;
	private String reportDate = "" ;
	private String outdays = "" ;
	private String auditpeopleCount = "" ;
	private String scheduleBegin = "" ;
	private String scheduleEnd = "" ;
	private String reportType = "" ;
	private String sealOrSign = "" ;
	private String remark = "" ;
	private String filename = "" ;
	private String filetempname = "" ;
	private String reportCopies = "" ;
	private String reportPrint = "" ;
	private String licenseCount = "" ;
	private String latestReportDate = "" ;
	private String attachDesc = "" ;
	private String reportFileTempName = "" ;
	private String reportFileName = "" ;
	private String secretFileName = "" ;
	private String secretFileTempName = "" ;
	private String money = "" ;
	private String receivemoney = "" ;
	private String printUser = "" ;
	private String checkMoney = "" ;
	private String bingUser = "" ;
	private String reportSign = "" ;
	private String reportUsage = "" ;
	private String reportUser = "" ;
	private String isStock = "" ;
	private String registerNum;// 登记流水号
	private String travelAgree;// 差旅费约定
	private String isNewBusiness;// 是否新承接业务
	private String businessResource;// 是否新承接业务
	private String continueUser;// 是否新承接业务
	private String introduceUser;// 是否新承接业务
	
	private String costPromise ; //合同金额约定
	private String businessDesc ; //委托业务约定（简述）
	private String travelPromise ; //差旅费其他备注
	private String finishYear ; //预计完成年度
	
	private String isExtactFee ; //是否支付预算外费用
	private String payRate ;     //支付比例
	private String auditPayRate ; //审核比例
	
	private String parentRegisterNum ;
	
	//安联要加的字段
	private String instalment_AnLian ;//分期付款
	private String projectFile_AnLian; //项目附件
	private String planDate_AnLian; //计划结算日期
	
	private String expertUserId; //专职复核人
	private String qualityUserId; //质控合伙人
	
	private String signaturePartnerUserId;  //签字合伙人：大华需求
	
	public String getExpertUserId() {
		return expertUserId;
	}
	public void setExpertUserId(String expertUserId) {
		this.expertUserId = expertUserId;
	}
	public String getQualityUserId() {
		return qualityUserId;
	}
	public void setQualityUserId(String qualityUserId) {
		this.qualityUserId = qualityUserId;
	}
	public String getPlanDate_AnLian() {
		return planDate_AnLian;
	}
	public void setPlanDate_AnLian(String planDate_AnLian) {
		this.planDate_AnLian = planDate_AnLian;
	}
	public String getInstalment_AnLian() {
		return instalment_AnLian;
	}
	public void setInstalment_AnLian(String instalment_AnLian) {
		this.instalment_AnLian = instalment_AnLian;
	}
	public String getProjectFile_AnLian() {
		return projectFile_AnLian;
	}
	public void setProjectFile_AnLian(String projectFile_AnLian) {
		this.projectFile_AnLian = projectFile_AnLian;
	}
	public String getParentRegisterNum() {
		return parentRegisterNum;
	}
	public void setParentRegisterNum(String parentRegisterNum) {
		this.parentRegisterNum = parentRegisterNum;
	}
	public String getIsExtactFee() {
		return isExtactFee;
	}
	public void setIsExtactFee(String isExtactFee) {
		this.isExtactFee = isExtactFee;
	}
	public String getPayRate() {
		return payRate;
	}
	public void setPayRate(String payRate) {
		this.payRate = payRate;
	}
	public String getAuditPayRate() {
		return auditPayRate;
	}
	public void setAuditPayRate(String auditPayRate) {
		this.auditPayRate = auditPayRate;
	}
	public String getCostPromise() {
		return costPromise;
	}
	public void setCostPromise(String costPromise) {
		this.costPromise = costPromise;
	}
	public String getBusinessDesc() {
		return businessDesc;
	}
	public void setBusinessDesc(String businessDesc) {
		this.businessDesc = businessDesc;
	}
	public String getTravelPromise() {
		return travelPromise;
	}
	public void setTravelPromise(String travelPromise) {
		this.travelPromise = travelPromise;
	}
	public String getFinishYear() {
		return finishYear;
	}
	public void setFinishYear(String finishYear) {
		this.finishYear = finishYear;
	}
	public String getProjectID() {
		return projectID;
	}
	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getEntrustCustomerId() {
		return EntrustCustomerId;
	}
	public void setEntrustCustomerId(String entrustCustomerId) {
		EntrustCustomerId = entrustCustomerId;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getPayCustomerId() {
		return payCustomerId;
	}
	public void setPayCustomerId(String payCustomerId) {
		this.payCustomerId = payCustomerId;
	}
	public String getAuditpara() {
		return auditpara;
	}
	public void setAuditpara(String auditpara) {
		this.auditpara = auditpara;
	}
	public String getTypeId() {
		return typeId;
	}
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	public String getIsSpecialProject() {
		return isSpecialProject;
	}
	public void setIsSpecialProject(String isSpecialProject) {
		this.isSpecialProject = isSpecialProject;
	}
	public String getIsNewTakeProject() {
		return isNewTakeProject;
	}
	public void setIsNewTakeProject(String isNewTakeProject) {
		this.isNewTakeProject = isNewTakeProject;
	}
	public String getIsReport() {
		return isReport;
	}
	public void setIsReport(String isReport) {
		this.isReport = isReport;
	}
	public String getCustomerType() {
		return customerType;
	}
	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}
	public String getManagerUserId() {
		return managerUserId;
	}
	public void setManagerUserId(String managerUserId) {
		this.managerUserId = managerUserId;
	}
	public String getDepartManagerUserId() {
		return departManagerUserId;
	}
	public void setDepartManagerUserId(String departManagerUserId) {
		this.departManagerUserId = departManagerUserId;
	}
	public String getPartnerUserId() {
		return partnerUserId;
	}
	public void setPartnerUserId(String partnerUserId) {
		this.partnerUserId = partnerUserId;
	}
	public String getRistPartnerUserId() {
		return ristPartnerUserId;
	}
	public void setRistPartnerUserId(String ristPartnerUserId) {
		this.ristPartnerUserId = ristPartnerUserId;
	}
	public String getSeniorCpaUserId() {
		return seniorCpaUserId;
	}
	public void setSeniorCpaUserId(String seniorCpaUserId) {
		this.seniorCpaUserId = seniorCpaUserId;
	}
	public String getProjectPartner1() {
		return projectPartner1;
	}
	public void setProjectPartner1(String projectPartner1) {
		this.projectPartner1 = projectPartner1;
	}
	public String getProjectPartner2() {
		return projectPartner2;
	}
	public void setProjectPartner2(String projectPartner2) {
		this.projectPartner2 = projectPartner2;
	}
	public String getQualityPartner() {
		return qualityPartner;
	}
	public void setQualityPartner(String qualityPartner) {
		this.qualityPartner = qualityPartner;
	}
	public String getRistLevel() {
		return ristLevel;
	}
	public void setRistLevel(String ristLevel) {
		this.ristLevel = ristLevel;
	}
	public String getIsStore() {
		return isStore;
	}
	public void setIsStore(String isStore) {
		this.isStore = isStore;
	}
	public String getSignedDate() {
		return signedDate;
	}
	public void setSignedDate(String signedDate) {
		this.signedDate = signedDate;
	}
	public String getBusinessCost() {
		return businessCost;
	}
	public void setBusinessCost(String businessCost) {
		this.businessCost = businessCost;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getEntrustNumber() {
		return entrustNumber;
	}
	public void setEntrustNumber(String entrustNumber) {
		this.entrustNumber = entrustNumber;
	}
	public String getReportNumeber() {
		return reportNumeber;
	}
	public void setReportNumeber(String reportNumeber) {
		this.reportNumeber = reportNumeber;
	}
	public String getSignedCpa1() {
		return signedCpa1;
	}
	public void setSignedCpa1(String signedCpa1) {
		this.signedCpa1 = signedCpa1;
	}
	public String getSignedCpa2() {
		return signedCpa2;
	}
	public void setSignedCpa2(String signedCpa2) {
		this.signedCpa2 = signedCpa2;
	}
	public String getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getCompanyType() {
		return companyType;
	}
	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}
	public String getBusiness() {
		return business;
	}
	public void setBusiness(String business) {
		this.business = business;
	}
	public String getContactUser() {
		return contactUser;
	}
	public void setContactUser(String contactUser) {
		this.contactUser = contactUser;
	}
	public String getContactPhone() {
		return contactPhone;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	public String getBusinesChannel() {
		return businesChannel;
	}
	public void setBusinesChannel(String businesChannel) {
		this.businesChannel = businesChannel;
	}
	public String getReportRequire() {
		return reportRequire;
	}
	public void setReportRequire(String reportRequire) {
		this.reportRequire = reportRequire;
	}
	public String getReportDate() {
		return reportDate;
	}
	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}
	public String getOutdays() {
		return outdays;
	}
	public void setOutdays(String outdays) {
		this.outdays = outdays;
	}
	public String getAuditpeopleCount() {
		return auditpeopleCount;
	}
	public void setAuditpeopleCount(String auditpeopleCount) {
		this.auditpeopleCount = auditpeopleCount;
	}
	public String getScheduleBegin() {
		return scheduleBegin;
	}
	public void setScheduleBegin(String scheduleBegin) {
		this.scheduleBegin = scheduleBegin;
	}
	public String getScheduleEnd() {
		return scheduleEnd;
	}
	public void setScheduleEnd(String scheduleEnd) {
		this.scheduleEnd = scheduleEnd;
	}
	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	public String getSealOrSign() {
		return sealOrSign;
	}
	public void setSealOrSign(String sealOrSign) {
		this.sealOrSign = sealOrSign;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
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
	public String getReportCopies() {
		return reportCopies;
	}
	public void setReportCopies(String reportCopies) {
		this.reportCopies = reportCopies;
	}
	public String getReportPrint() {
		return reportPrint;
	}
	public void setReportPrint(String reportPrint) {
		this.reportPrint = reportPrint;
	}
	public String getLicenseCount() {
		return licenseCount;
	}
	public void setLicenseCount(String licenseCount) {
		this.licenseCount = licenseCount;
	}
	public String getLatestReportDate() {
		return latestReportDate;
	}
	public void setLatestReportDate(String latestReportDate) {
		this.latestReportDate = latestReportDate;
	}
	public String getAttachDesc() {
		return attachDesc;
	}
	public void setAttachDesc(String attachDesc) {
		this.attachDesc = attachDesc;
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
	public String getSecretFileName() {
		return secretFileName;
	}
	public void setSecretFileName(String secretFileName) {
		this.secretFileName = secretFileName;
	}
	public String getSecretFileTempName() {
		return secretFileTempName;
	}
	public void setSecretFileTempName(String secretFileTempName) {
		this.secretFileTempName = secretFileTempName;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getReceivemoney() {
		return receivemoney;
	}
	public void setReceivemoney(String receivemoney) {
		this.receivemoney = receivemoney;
	}
	public String getPrintUser() {
		return printUser;
	}
	public void setPrintUser(String printUser) {
		this.printUser = printUser;
	}
	public String getCheckMoney() {
		return checkMoney;
	}
	public void setCheckMoney(String checkMoney) {
		this.checkMoney = checkMoney;
	}
	public String getBingUser() {
		return bingUser;
	}
	public void setBingUser(String bingUser) {
		this.bingUser = bingUser;
	}
	public String getReportSign() {
		return reportSign;
	}
	public void setReportSign(String reportSign) {
		this.reportSign = reportSign;
	}
	public String getReportUsage() {
		return reportUsage;
	}
	public void setReportUsage(String reportUsage) {
		this.reportUsage = reportUsage;
	}
	public String getReportUser() {
		return reportUser;
	}
	public void setReportUser(String reportUser) {
		this.reportUser = reportUser;
	}
	public String getIsStock() {
		return isStock;
	}
	public void setIsStock(String isStock) {
		this.isStock = isStock;
	}
	public String getRegisterNum() {
		return registerNum;
	}
	public void setRegisterNum(String registerNum) {
		this.registerNum = registerNum;
	}
	public String getTravelAgree() {
		return travelAgree;
	}
	public void setTravelAgree(String travelAgree) {
		this.travelAgree = travelAgree;
	}
	public String getIsNewBusiness() {
		return isNewBusiness;
	}
	public void setIsNewBusiness(String isNewBusiness) {
		this.isNewBusiness = isNewBusiness;
	}
	public String getBusinessResource() {
		return businessResource;
	}
	public void setBusinessResource(String businessResource) {
		this.businessResource = businessResource;
	}
	public String getContinueUser() {
		return continueUser;
	}
	public void setContinueUser(String continueUser) {
		this.continueUser = continueUser;
	}
	public String getIntroduceUser() {
		return introduceUser;
	}
	public void setIntroduceUser(String introduceUser) {
		this.introduceUser = introduceUser;
	}
	public String getSignaturePartnerUserId() {
		return signaturePartnerUserId;
	}
	public void setSignaturePartnerUserId(String signaturePartnerUserId) {
		this.signaturePartnerUserId = signaturePartnerUserId;
	}
	
}
