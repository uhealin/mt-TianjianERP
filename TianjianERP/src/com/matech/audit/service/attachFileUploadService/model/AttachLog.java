package com.matech.audit.service.attachFileUploadService.model;

/**
 * @author Administrator
 *附件日志
 */
public class AttachLog {
	  
    private String autoId;// INT(100) NOT NULL AUTO_INCREMENT COMMENT '主键Id',
    private String indexId;// VARCHAR(100) DEFAULT NULL COMMENT '附件批次号Id',
    private String indexTable;// VARCHAR(30) DEFAULT NULL COMMENT '附件所属对象',
    private String fileId;// VARCHAR(100) DEFAULT NULL COMMENT '附件Id',
    private String fileName;// VARCHAR(100) DEFAULT NULL COMMENT '附件名称',
    private String filePath;// VARCHAR(300) DEFAULT NULL COMMENT '附件路径',
    private String lookDate;// VARCHAR(30) DEFAULT NULL COMMENT '日期',
    private String userName ;//VARCHAR(30) DEFAULT NULL COMMENT '用户',
    private String userIp ; //Ip
    private String property ;//VARCHAR(100) DEFAULT NULL COMMENT '备用',
    
    
	public String getAutoId() {
		return autoId;
	}
	public void setAutoId(String autoId) {
		this.autoId = autoId;
	}
	public String getIndexId() {
		return indexId;
	}
	public void setIndexId(String indexId) {
		this.indexId = indexId;
	}
	public String getIndexTable() {
		return indexTable;
	}
	public void setIndexTable(String indexTable) {
		this.indexTable = indexTable;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getLookDate() {
		return lookDate;
	}
	public void setLookDate(String lookDate) {
		this.lookDate = lookDate;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserIp() {
		return userIp;
	}
	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	  
}
