package com.matech.audit.service.message.model;

import java.io.Serializable;

/**
 * 系统消息
 * 
 * @author METACH
 * @date 2012-03-06
 * 
 */
public class Message implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String uuid;				//1.流水号
	private String messageContent;			//2.消息内容
	private String msgType;			//3.消息类型
	private String sendUserid;		//4.发送人id
	private String sendTime;		//5.发生时间
	private String receiveUserid;		//6.接收人id
	private String isRead;			//7.未读0,已读1
	private String readTime;			//阅读时间
	private String batchId;			//批次号
	private String msgParam;			//消息参数
	private String msgTitle;			//消息标题
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getMessageContent() {
		return messageContent;
	}
	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	public String getSendUserid() {
		return sendUserid;
	}
	public void setSendUserid(String sendUserid) {
		this.sendUserid = sendUserid;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	public String getReceiveUserid() {
		return receiveUserid;
	}
	public void setReceiveUserid(String receiveUserid) {
		this.receiveUserid = receiveUserid;
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
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	public String getMsgParam() {
		return msgParam;
	}
	public void setMsgParam(String msgParam) {
		this.msgParam = msgParam;
	}
	public String getMsgTitle() {
		return msgTitle;
	}
	public void setMsgTitle(String msgTitle) {
		this.msgTitle = msgTitle;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	

}
