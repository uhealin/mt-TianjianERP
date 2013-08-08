package com.matech.audit.service.project.model;

/**
 * 项目跟进表
 * @author Ymm
 *
 */
public class ProjectSchedule {
	
	private String autoId ;//int(11) NOT NULL
	private String projectId ;//varchar(100) NULL
	private String projectName ;//varchar(300) NULL项目名称
	private String projectType; //项目类型
	private String responsibleUser ;//varchar(150) NULL负责人
	private String enterPdate ;//varchar(150) NULL项目进场计划时间
	private String enterRdate ;//varchar(150) NULL项目进场实际时间
	private String outworkerPdate ;//varchar(150) NULL外勤结束计划时间
	private String outworkerRdate ;//varchar(150) NULL外勤结束实际时间
	private String internalPdate ;//varchar(150) NULL内勤结束计划时间
	private String internalRdate ;//varchar(150) NULL内勤结束实际时间
	private String firstPdate ;//varchar(150) NULL一审计划时间
	private String firstRdate ;//varchar(150) NULL一审实际时间
	private String twoPdate ;//varchar(150) NULL二审计划时间
	private String twoRdate ;//varchar(150) NULL二审实际时间
	private String threePdate ;//varchar(150) NULL三审计划时间
	private String threeRdate ;//varchar(150) NULL三审实际时间
	private String reportPdate ;//varchar(150) NULL报告计划时间
	private String reportRdate ;//varchar(150) NULL报告实际时间
	private String archivesPdate ;//varchar(150) NULL归档计划时间
	private String archivesRdate ;//varchar(150) NULL归档实际时间
	private String createUser ;//varchar(100) NULL
	private String createDate ;//varchar(150) NULL
	private String createDepartment ;//varchar(100) NULL
	
	public String getProjectType() {
		return projectType;
	}
	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}
	public String getAutoId() {
		return autoId;
	}
	public void setAutoId(String autoId) {
		this.autoId = autoId;
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getResponsibleUser() {
		return responsibleUser;
	}
	public void setResponsibleUser(String responsibleUser) {
		this.responsibleUser = responsibleUser;
	}
	public String getEnterPdate() {
		return enterPdate;
	}
	public void setEnterPdate(String enterPdate) {
		this.enterPdate = enterPdate;
	}
	public String getEnterRdate() {
		return enterRdate;
	}
	public void setEnterRdate(String enterRdate) {
		this.enterRdate = enterRdate;
	}
	public String getOutworkerPdate() {
		return outworkerPdate;
	}
	public void setOutworkerPdate(String outworkerPdate) {
		this.outworkerPdate = outworkerPdate;
	}
	public String getOutworkerRdate() {
		return outworkerRdate;
	}
	public void setOutworkerRdate(String outworkerRdate) {
		this.outworkerRdate = outworkerRdate;
	}
	public String getInternalPdate() {
		return internalPdate;
	}
	public void setInternalPdate(String internalPdate) {
		this.internalPdate = internalPdate;
	}
	public String getInternalRdate() {
		return internalRdate;
	}
	public void setInternalRdate(String internalRdate) {
		this.internalRdate = internalRdate;
	}
	public String getFirstPdate() {
		return firstPdate;
	}
	public void setFirstPdate(String firstPdate) {
		this.firstPdate = firstPdate;
	}
	public String getFirstRdate() {
		return firstRdate;
	}
	public void setFirstRdate(String firstRdate) {
		this.firstRdate = firstRdate;
	}
	public String getTwoPdate() {
		return twoPdate;
	}
	public void setTwoPdate(String twoPdate) {
		this.twoPdate = twoPdate;
	}
	public String getTwoRdate() {
		return twoRdate;
	}
	public void setTwoRdate(String twoRdate) {
		this.twoRdate = twoRdate;
	}
	public String getThreePdate() {
		return threePdate;
	}
	public void setThreePdate(String threePdate) {
		this.threePdate = threePdate;
	}
	public String getThreeRdate() {
		return threeRdate;
	}
	public void setThreeRdate(String threeRdate) {
		this.threeRdate = threeRdate;
	}
	public String getReportPdate() {
		return reportPdate;
	}
	public void setReportPdate(String reportPdate) {
		this.reportPdate = reportPdate;
	}
	public String getReportRdate() {
		return reportRdate;
	}
	public void setReportRdate(String reportRdate) {
		this.reportRdate = reportRdate;
	}
	public String getArchivesPdate() {
		return archivesPdate;
	}
	public void setArchivesPdate(String archivesPdate) {
		this.archivesPdate = archivesPdate;
	}
	public String getArchivesRdate() {
		return archivesRdate;
	}
	public void setArchivesRdate(String archivesRdate) {
		this.archivesRdate = archivesRdate;
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
