package com.matech.audit.service.attachFileUploadService.model;

public class Attach {
	private String attachId;
	private String attachName;
	private String attachFile;
	private String attachFilePath;
	private String attachType;
	private String updateUser;
	private String updateTime;
	private String indexTable;
	private String indexMetadata;
	private String indexId;
	private String property;
	private long fileSize;

	private String width ;
	private String height ;
	
	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getAttachId() {
		return attachId;
	}

	public void setAttachId(String attachId) {
		this.attachId = attachId;
	}

	public String getAttachName() {
		return attachName;
	}

	public void setAttachName(String attachName) {
		this.attachName = attachName;
	}

	public String getAttachFile() {
		return attachFile;
	}

	public void setAttachFile(String attachFile) {
		this.attachFile = attachFile;
	}

	public String getAttachFilePath() {
		return attachFilePath;
	}

	public void setAttachFilePath(String attachFilePath) {
		this.attachFilePath = attachFilePath;
	}

	public String getAttachType() {
		return attachType;
	}

	public void setAttachType(String attachType) {
		this.attachType = attachType;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getIndexTable() {
		return indexTable;
	}

	public void setIndexTable(String indexTable) {
		this.indexTable = indexTable;
	}

	public String getIndexMetadata() {
		return indexMetadata;
	}

	public void setIndexMetadata(String indexMetadata) {
		this.indexMetadata = indexMetadata;
	}

	public String getIndexId() {
		return indexId;
	}

	public void setIndexId(String indexId) {
		this.indexId = indexId;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
}
