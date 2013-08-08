package com.matech.audit.service.contract.model;

public class ContractMoneyRecord {
	private String autoid;//记录编号
	private String customerid;//客户编号
	private String customername;//单位名称
	private String company;//己方单位名称
	private String planmoney;//计划金额
	private String plandate;//计划日期
	private String factmoney;//实际金额
	private String factdate;//实际日期
	private String moneytype;//币种
	private String recorder;//记录人
	private String recorddate;//记录日期
	private String getorpay;//收款or付款
	private String memo;//备注
	
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getAutoid() {
		return autoid;
	}
	public void setAutoid(String autoid) {
		this.autoid = autoid;
	}
	public String getCustomerid() {
		return customerid;
	}
	public void setCustomerid(String customerid) {
		this.customerid = customerid;
	}
	public String getPlanmoney() {
		return planmoney;
	}
	public void setPlanmoney(String planmoney) {
		this.planmoney = planmoney;
	}
	public String getPlandate() {
		return plandate;
	}
	public void setPlandate(String plandate) {
		this.plandate = plandate;
	}
	public String getFactmoney() {
		return factmoney;
	}
	public void setFactmoney(String factmoney) {
		this.factmoney = factmoney;
	}
	public String getFactdate() {
		return factdate;
	}
	public void setFactdate(String factdate) {
		this.factdate = factdate;
	}
	public String getRecorder() {
		return recorder;
	}
	public void setRecorder(String recorder) {
		this.recorder = recorder;
	}
	public String getRecorddate() {
		return recorddate;
	}
	public void setRecorddate(String recorddate) {
		this.recorddate = recorddate;
	}
	public String getCustomername() {
		return customername;
	}
	public void setCustomername(String customername) {
		this.customername = customername;
	}
	public String getGetorpay() {
		return getorpay;
	}
	public void setGetorpay(String getorpay) {
		this.getorpay = getorpay;
	}
	public String getMoneytype() {
		return moneytype;
	}
	public void setMoneytype(String moneytype) {
		this.moneytype = moneytype;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}	
}
