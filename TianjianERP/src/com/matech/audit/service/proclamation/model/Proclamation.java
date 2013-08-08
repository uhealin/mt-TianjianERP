package com.matech.audit.service.proclamation.model;


/**
 * @author YMM
 * 2.通知公告
 *
 */
public class Proclamation {
	
	private String uuid ;//(100) NOT NULLuuid
	private String title ;//(200) NULL标题
	private String departmentId ;//(50) NULL发布部门
	private String userId ;//(50) NULL发布人
	private String publishDate ;//(50) NULL发布时间
	private String content ;// NULL内容
	private String fileName ;//(200) NULL多附件名称(,)
	private String fileRondomNames ;//(200) NULL多附件随机名字(,)
	private String readUserId ;//(50) NULL阅读部门
	private String property ;//(200) NULL备用
	private String ctype ; //类型
	private String up  ;//置顶
	private int    upDates;//置顶天数
	private String goDate;//生效日期
	private String endGoDate;//失效日期
	
	
	public String getGoDate() {
		return goDate;
	}
	public void setGoDate(String goDate) {
		this.goDate = goDate;
	}
	public String getEndGoDate() {
		return endGoDate;
	}
	public void setEndGoDate(String endGoDate) {
		this.endGoDate = endGoDate;
	}
	public String getUp() {
		return up;
	}
	public void setUp(String up) {
		this.up = up;
	}
	public int getUpDates() {
		return upDates;
	}
	public void setUpDates(int upDates) {
		this.upDates = upDates;
	}
	public String getCtype() {
		return ctype;
	}
	public void setCtype(String ctype) {
		this.ctype = ctype;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getFileRondomNames() {
		return fileRondomNames;
	}
	public void setFileRondomNames(String fileRondomNames) {
		this.fileRondomNames = fileRondomNames;
	}
	public String getReadUserId() {
		return readUserId;
	}
	public void setReadUserId(String readUserId) {
		this.readUserId = readUserId;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
}
