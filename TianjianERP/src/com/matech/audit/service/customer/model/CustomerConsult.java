package com.matech.audit.service.customer.model;

public class CustomerConsult {
	private int autoId;//记录编号
	private String linkMan;//联系人
	private String QQ;//联系途径
	private String PHONE;//联系方式
	private String EMAIL;//联系方式
	private String customerid;//客户编号
	private String customerName;//联系人所在客户
	private String visitTime;//来访时间
	private String problem;//来访事由
	private String state;//记录状态
	private String finishMan;//解决人
	private String dealTime;//解决时间
	private String filename;//附件名称
	private String finishRecode;//解决记录
	private String unfinishProblem;//未解决问题
	private String unfinishDepart;//未解决问题责任部门
	private String unfinishMan;//未解决问题责任人
	private String untillTime;//解决期限
	private String recoder;//记录人
	private String recodeTime;//记录时间
	
	public int getAutoId() {
		return autoId;
	}
	public void setAutoId(int autoId) {
		this.autoId = autoId;
	}
	public String getLinkMan() {
		return linkMan;
	}
	public void setLinkMan(String linkMan) {
		this.linkMan = linkMan;
	}
	
	public String getEMAIL() {
		return EMAIL;
	}
	public void setEMAIL(String email) {
		EMAIL = email;
	}
	public String getPHONE() {
		return PHONE;
	}
	public void setPHONE(String phone) {
		PHONE = phone;
	}
	public String getQQ() {
		return QQ;
	}
	public void setQQ(String qq) {
		QQ = qq;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getVisitTime() {
		return visitTime;
	}
	public void setVisitTime(String visitTime) {
		this.visitTime = visitTime;
	}
	public String getProblem() {
		return problem;
	}
	public void setProblem(String problem) {
		this.problem = problem;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getDealTime() {
		return dealTime;
	}
	public void setDealTime(String dealTime) {
		this.dealTime = dealTime;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getFinishRecode() {
		return finishRecode;
	}
	public void setFinishRecode(String finishRecode) {
		this.finishRecode = finishRecode;
	}
	public String getUnfinishProblem() {
		return unfinishProblem;
	}
	public void setUnfinishProblem(String unfinishProblem) {
		this.unfinishProblem = unfinishProblem;
	}
	public String getUnfinishDepart() {
		return unfinishDepart;
	}
	public void setUnfinishDepart(String unfinishDepart) {
		this.unfinishDepart = unfinishDepart;
	}
	public String getUnfinishMan() {
		return unfinishMan;
	}
	public void setUnfinishMan(String unfinishMan) {
		this.unfinishMan = unfinishMan;
	}
	public String getUntillTime() {
		return untillTime;
	}
	public void setUntillTime(String untillTime) {
		this.untillTime = untillTime;
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
	public String getCustomerid() {
		return customerid;
	}
	public void setCustomerid(String customerid) {
		this.customerid = customerid;
	}
	public String getFinishMan() {
		return finishMan;
	}
	public void setFinishMan(String finishMan) {
		this.finishMan = finishMan;
	}
}
