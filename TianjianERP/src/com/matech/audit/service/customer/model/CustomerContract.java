package com.matech.audit.service.customer.model;

public class CustomerContract {
	private String autoid;
	private String contractId;//合同编号
	private String contractMan;//合同人
	private String contractDate;//签定日期
	private String salory;//基本工资
	private String validTime;//有效期限
	private String contractAdjunct;//合同附件
	private String mome;//备注
	private String recoder;//记录人
	private String recodeTime;//记录时间
	
	public String getAutoid() {
		return autoid;
	}
	public void setAutoid(String autoid) {
		this.autoid = autoid;
	}	
	public String getContractId() {
		return contractId;
	}
	public void setContractId(String contractId) {
		this.contractId = contractId;
	}
	public String getContractMan() {
		return contractMan;
	}
	public void setContractMan(String contractMan) {
		this.contractMan = contractMan;
	}
	public String getContractDate() {
		return contractDate;
	}
	public void setContractDate(String contractDate) {
		this.contractDate = contractDate;
	}
	public String getSalory() {
		return salory;
	}
	public void setSalory(String salory) {
		this.salory = salory;
	}
	public String getValidTime() {
		return validTime;
	}
	public void setValidTime(String validTime) {
		this.validTime = validTime;
	}
	public String getMome() {
		return mome;
	}
	public void setMome(String mome) {
		this.mome = mome;
	}
	public String getRecoder() {
		return recoder;
	}
	public void setRecoder(String recoder) {
		this.recoder = recoder;
	}
	public String getRecodeTime() {
		return recodeTime;
	}
	public void setRecodeTime(String recodeTime) {
		this.recodeTime = recodeTime;
	}
	public String getContractAdjunct() {
		return contractAdjunct;
	}
	public void setContractAdjunct(String contractAdjunct) {
		this.contractAdjunct = contractAdjunct;
	}
}
