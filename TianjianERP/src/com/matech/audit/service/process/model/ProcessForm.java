package com.matech.audit.service.process.model;

import java.util.ArrayList;
import java.util.List;

public class ProcessForm {
	
	private String id = "" ;
	private String pId = "" ;
	private String key = "" ;
	private String value = "" ;
	private String nodeName = "" ;
	private String dealUserId = "" ;
	private String dealTime = "" ;
	private String property = "" ;
	private List<ProcessForm> formList = new ArrayList<ProcessForm>()  ;
	private String formId = "" ;
	private String formEntityId = "" ;
	private String arriveTime = "" ;
	private String processInstanseId="";
	
	public String getArriveTime() {
		return arriveTime;
	}
	public void setArriveTime(String arriveTime) {
		this.arriveTime = arriveTime;
	}
	public String getFormId() {
		return formId;
	}
	public void setFormId(String formId) {
		this.formId = formId;
	}
	public String getFormEntityId() {
		return formEntityId;
	}
	public void setFormEntityId(String formEntityId) {
		this.formEntityId = formEntityId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getpId() {
		return pId;
	}
	public void setpId(String pId) {
		this.pId = pId;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getDealUserId() {
		return dealUserId;
	}
	public void setDealUserId(String dealUserId) {
		this.dealUserId = dealUserId;
	}
	public String getDealTime() {
		return dealTime;
	}
	public void setDealTime(String dealTime) {
		this.dealTime = dealTime;
	}
	public List<ProcessForm> getFormList() {
		return formList;
	}
	public void setFormList(List<ProcessForm> formList) {
		this.formList = formList;
	}
	
	public static ProcessForm newInstance(String curNodeName,String value,String dealUserId,String dealTime,String foreignUUID) {
		ProcessForm processForm=new ProcessForm();
		processForm.setNodeName(curNodeName);
		processForm.setKey("意见");
		processForm.setValue(value);
		processForm.setDealUserId(dealUserId);
		processForm.setDealTime(dealTime);
		processForm.setFormEntityId(foreignUUID);
		return processForm;
	}
	public void setProcessInstanseId(String pdId) {
		// TODO Auto-generated method stub
		this.processInstanseId=pdId;
	}
	public String getProcessInstanseId() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
