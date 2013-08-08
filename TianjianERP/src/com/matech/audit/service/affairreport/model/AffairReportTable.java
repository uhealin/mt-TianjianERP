package com.matech.audit.service.affairreport.model;

/**
 * <p>Title:�ش��¼��㱨����ֶ� </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 2.2
 */

public class AffairReportTable {
	
	private int ID;
	private int projectID;//��ĿID
	private int pID;//��ID
	private String author;//������
	private String executer;//ִ����
	private String caption;//����
	private String matter;//����
	private String createTime;//����ʱ��
	private String name;
	private String Porperty;
	private String IsRead;
	private String IsSame;
	private String status;
	private String principal;
	private String taskCodeList;
	private String timeLimit;
	private String lastUpdateTime;
	private String SubjectFullName1;
	
	public String getSubjectFullName1() {
		return SubjectFullName1;
	}
	public void setSubjectFullName1(String subjectFullName1) {
		SubjectFullName1 = subjectFullName1;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public int getPID() {
		return pID;
	}
	public void setPID(int pid) {
		pID = pid;
	}
	public int getProjectID() {
		return projectID;
	}
	public void setProjectID(int projectID) {
		this.projectID = projectID;
	}
	public String getExecuter() {
		return executer;
	}
	public void setExecuter(String executer) {
		this.executer = executer;
	}
	public int getID() {
		return ID;
	}
	public void setID(int id) {
		ID = id;
	}
	public String getMatter() {
		return matter;
	}
	public void setMatter(String matter) {
		this.matter = matter;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPorperty() {
		return Porperty;
	}
	public void setPorperty(String porperty) {
		Porperty = porperty;
	}
	public String getIsRead() {
		return IsRead;
	}
	public void setIsRead(String isRead) {
		IsRead = isRead;
	}
	public String getIsSame() {
		return IsSame;
	}
	public void setIsSame(String isSame) {
		IsSame = isSame;
	}
	public String getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public String getPrincipal() {
		return principal;
	}
	public void setPrincipal(String principal) {
		this.principal = principal;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getTaskCodeList() {
		return taskCodeList;
	}
	public void setTaskCodeList(String taskCodeList) {
		this.taskCodeList = taskCodeList;
	}
	public String getTimeLimit() {
		return timeLimit;
	}
	public void setTimeLimit(String timeLimit) {
		this.timeLimit = timeLimit;
	}
	

}

