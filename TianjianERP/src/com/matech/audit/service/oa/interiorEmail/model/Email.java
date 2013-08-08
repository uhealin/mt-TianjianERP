package com.matech.audit.service.oa.interiorEmail.model;

/**
 * @author YMM
 *内部邮件
 */
public class Email {
	
    private String uuid ;  //VARCHAR(100) NOT NULL COMMENT 'uuid',
    private String title;  //标题 
    private String importance ;  //VARCHAR(20) DEFAULT NULL COMMENT '重要性(一般邮件/重要右键/非常重要)',
    private String content;  //MEDIUMTEXT COMMENT '内容',
    private String fileId ;  //VARCHAR(100) DEFAULT NULL COMMENT '附件',
    private String addresser ;  //VARCHAR(30) DEFAULT NULL COMMENT '发件人',
    private String sendDate ;  //VARCHAR(30) DEFAULT NULL COMMENT '发送时间',
    private String property ;  //VARCHAR(100) DEFAULT NULL COMMENT '备用',
	private String addressee ; //收件人
	
	public String getAddressee() {
		return addressee;
	}
	public void setAddressee(String addressee) {
		this.addressee = addressee;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getImportance() {
		return importance;
	}
	public void setImportance(String importance) {
		this.importance = importance;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getAddresser() {
		return addresser;
	}
	public void setAddresser(String addresser) {
		this.addresser = addresser;
	}
	public String getSendDate() {
		return sendDate;
	}
	public void setSendDate(String sendDate) {
		this.sendDate = sendDate;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
}
