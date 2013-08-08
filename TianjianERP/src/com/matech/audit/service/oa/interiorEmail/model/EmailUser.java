package com.matech.audit.service.oa.interiorEmail.model;

/**
 * @author Administrator
 *内部 邮箱 人员控制
 */
public class EmailUser {
	
    private String autoId;  // INT(11) NOT NULL AUTO_INCREMENT,
    private String uuid ; //VARCHAR(100) DEFAULT NULL COMMENT 'email外键',
    private String userId ; //VARCHAR(30) DEFAULT NULL COMMENT '用户ID',
    private String ctype ; //VARCHAR(30) DEFAULT NULL COMMENT '类型(抄送/密送/收件人 /外部)',
    private String instationRemind = "否" ; //VARCHAR(20) DEFAULT '否' COMMENT '内部短信提醒',
    private String receiveRemind = "否" ; //VARCHAR(20) DEFAULT '否' COMMENT '收条(第一次阅读提醒)',
    private String isRead = "否" ; //VARCHAR(20) DEFAULT '否' COMMENT '是否阅读',
    private String dustbin ;  //垃圾箱
    private String readTime="0" ; //VARCHAR(20) DEFAULT '0' COMMENT '阅读次数',
    private String readDate;     //阅读日期
    private String property ; //VARCHAR(200) DEFAULT NULL COMMENT '备用',
	private String mobilePhoneRemind = "否"; //手机短信提醒
    
    
    public String getMobilePhoneRemind() {
		return mobilePhoneRemind;
	}
	public void setMobilePhoneRemind(String mobilePhoneRemind) {
		this.mobilePhoneRemind = mobilePhoneRemind;
	}
	public String getReadDate() {
		return readDate;
	}
	public void setReadDate(String readDate) {
		this.readDate = readDate;
	}
	public String getDustbin() {
		return dustbin;
	}
	public void setDustbin(String dustbin) {
		this.dustbin = dustbin;
	}
	public String getAutoId() {
		return autoId;
	}
	public String getReceiveRemind() {
		return receiveRemind;
	}
	public void setReceiveRemind(String receiveRemind) {
		this.receiveRemind = receiveRemind;
	}
	public void setAutoId(String autoId) {
		this.autoId = autoId;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getCtype() {
		return ctype;
	}
	public void setCtype(String ctype) {
		this.ctype = ctype;
	}
	public String getInstationRemind() {
		return instationRemind;
	}
	public void setInstationRemind(String instationRemind) {
		this.instationRemind = instationRemind;
	}
	public String getIsRead() {
		return isRead;
	}
	public void setIsRead(String isRead) {
		this.isRead = isRead;
	}
	public String getReadTime() {
		return readTime;
	}
	public void setReadTime(String readTime) {
		this.readTime = readTime;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
}
