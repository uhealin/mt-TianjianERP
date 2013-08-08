package com.matech.audit.service.attachFileUploadService.model;


public class AttachFile {
	

	private String indexTable = "" ;
	private String indexMetaData = "" ;
	private String indexId = "" ;
	private String fileName = "" ;
	private String fileTempName = "" ;
	private String property = "" ;
	private String timeFlag = "" ;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileTempName() {
		return fileTempName;
	}
	public void setFileTempName(String fileTempName) {
		this.fileTempName = fileTempName;
	}
	public String getIndexId() {
		return indexId;
	}
	public void setIndexId(String indexId) {
		this.indexId = indexId;
	}
	public String getIndexMetaData() {
		return indexMetaData;
	}
	public void setIndexMetaData(String indexMetaData) {
		this.indexMetaData = indexMetaData;
	}
	public String getIndexTable() {
		return indexTable;
	}
	public void setIndexTable(String indexTable) {
		this.indexTable = indexTable;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getTimeFlag() {
		return timeFlag;
	}
	public void setTimeFlag(String timeFlag) {
		this.timeFlag = timeFlag;
	}
	
	

}
