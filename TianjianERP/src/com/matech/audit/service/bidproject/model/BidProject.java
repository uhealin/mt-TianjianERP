package com.matech.audit.service.bidproject.model;

public class BidProject {

	private String uuid;//主键
	private String auditUnit;//被审计单位
	private String trustOrgan;//委托机构
	private String serviceStartTime;//业务区间开始
	private String serviceEndTime;//业务区间结束
	private String serviceType;//业务类型
	private String projectName;//项目名称
	private String projectSimpleName;//项目简称
	private String unitName;//单位名称
	private String vocationType;//会计制度类型
	private String unitEngName;//单位英文名称
	private String hylx;//行业类型
	private String register;//注册资本
	private String curName;//货币类型
	private String unitSimpleName;//单位简称
	private String endDate;// 截至期限
	private String bidMember;// 应标成员（多选）
	private String bidMemberName;// 应标成员（多选）
	private String duty;// 职责
	private String bidAttachId;// 上传招标书
	private String bidCompetitor;// 竞争对手
	private String bidAttachFileId;// 投书文件
	private String createId;// 创建人
	private String createName;// 创建人
	private String createDate;// 创建日期
	private String auditorId;// 审核人
	private String auditorName;// 审核人
	private String auditDate;// 审核日期
	private String auditStatus;// 审核状态
	private String getBidPerson;// 中标人
	private String getBidPrice;// 中标价
	private String isGetBidProject;// 是否中标
	private String bidStatus;// 状态
	private String reason;// 原因
	private String property;// 备用字段
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getAuditUnit() {
		return auditUnit;
	}
	public void setAuditUnit(String auditUnit) {
		this.auditUnit = auditUnit;
	}
	public String getTrustOrgan() {
		return trustOrgan;
	}
	public void setTrustOrgan(String trustOrgan) {
		this.trustOrgan = trustOrgan;
	}
	public String getServiceStartTime() {
		return serviceStartTime;
	}
	public void setServiceStartTime(String serviceStartTime) {
		this.serviceStartTime = serviceStartTime;
	}
	public String getServiceEndTime() {
		return serviceEndTime;
	}
	public void setServiceEndTime(String serviceEndTime) {
		this.serviceEndTime = serviceEndTime;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getProjectSimpleName() {
		return projectSimpleName;
	}
	public void setProjectSimpleName(String projectSimpleName) {
		this.projectSimpleName = projectSimpleName;
	}
	public String getUnitName() {
		return unitName;
	}
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	public String getVocationType() {
		return vocationType;
	}
	public void setVocationType(String vocationType) {
		this.vocationType = vocationType;
	}
	public String getUnitEngName() {
		return unitEngName;
	}
	public void setUnitEngName(String unitEngName) {
		this.unitEngName = unitEngName;
	}
	public String getHylx() {
		return hylx;
	}
	public void setHylx(String hylx) {
		this.hylx = hylx;
	}
	public String getRegister() {
		return register;
	}
	public void setRegister(String register) {
		this.register = register;
	}
	public String getCurName() {
		return curName;
	}
	public void setCurName(String curName) {
		this.curName = curName;
	}
	public String getUnitSimpleName() {
		return unitSimpleName;
	}
	public void setUnitSimpleName(String unitSimpleName) {
		this.unitSimpleName = unitSimpleName;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getBidMember() {
		return bidMember;
	}
	public void setBidMember(String bidMember) {
		this.bidMember = bidMember;
	}
	public String getBidMemberName() {
		return bidMemberName;
	}
	public void setBidMemberName(String bidMemberName) {
		this.bidMemberName = bidMemberName;
	}
	public String getDuty() {
		return duty;
	}
	public void setDuty(String duty) {
		this.duty = duty;
	}
	public String getBidAttachId() {
		return bidAttachId;
	}
	public void setBidAttachId(String bidAttachId) {
		this.bidAttachId = bidAttachId;
	}
	public String getBidCompetitor() {
		return bidCompetitor;
	}
	public void setBidCompetitor(String bidCompetitor) {
		this.bidCompetitor = bidCompetitor;
	}
	public String getBidAttachFileId() {
		return bidAttachFileId;
	}
	public void setBidAttachFileId(String bidAttachFileId) {
		this.bidAttachFileId = bidAttachFileId;
	}
	public String getCreateId() {
		return createId;
	}
	public void setCreateId(String createId) {
		this.createId = createId;
	}
	public String getCreateName() {
		return createName;
	}
	public void setCreateName(String createName) {
		this.createName = createName;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getAuditorId() {
		return auditorId;
	}
	public void setAuditorId(String auditorId) {
		this.auditorId = auditorId;
	}
	public String getAuditorName() {
		return auditorName;
	}
	public void setAuditorName(String auditorName) {
		this.auditorName = auditorName;
	}
	public String getAuditDate() {
		return auditDate;
	}
	public void setAuditDate(String auditDate) {
		this.auditDate = auditDate;
	}
	public String getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}
	public String getGetBidPerson() {
		return getBidPerson;
	}
	public void setGetBidPerson(String getBidPerson) {
		this.getBidPerson = getBidPerson;
	}
	public String getGetBidPrice() {
		return getBidPrice;
	}
	public void setGetBidPrice(String getBidPrice) {
		this.getBidPrice = getBidPrice;
	}
	public String getIsGetBidProject() {
		return isGetBidProject;
	}
	public void setIsGetBidProject(String isGetBidProject) {
		this.isGetBidProject = isGetBidProject;
	}
	public String getBidStatus() {
		return bidStatus;
	}
	public void setBidStatus(String bidStatus) {
		this.bidStatus = bidStatus;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	
	
}
