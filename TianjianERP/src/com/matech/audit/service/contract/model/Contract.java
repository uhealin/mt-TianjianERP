package com.matech.audit.service.contract.model;

public class Contract {	
	
	private String paymentfashion = "";//付款方式
	private String curname = "";//币种
	private String sharelist = "";//份数
	private String cmemo = "";//备注
	private String draftout = "";//起草人
	private String bargainterm = "";//合同期限
	private String linkman = "";//联系人
	private String endamendtime = "";//最后修改时间
	private String blankouttime = "";//作废时间
	private String blankoutpeople = "";//作废人
	
	private String departmentid = "";//归属部门
	
//	小陆录入
	private String bargainid = "";//合同编号(内部唯一编号)
	private String contractname = "";//合同名称
	private String armour = "";//甲方
	private String second = "";//乙方
	private String bargainmoney = "";//合同金额
	private String estate = "";//状态
	private String startuptime = "";//启用时间
	private String startuppeople = "";//启用人
	private String customerid = "";//客户编号
	private String contractfile = "";//合同附件
	private String property = "";//合同属性(显示编号)
	private String projects = "";//合同对应项目编号,以“,”分隔 ALTER TABLE `asdb`.`oa_contract`     ADD COLUMN `projects` VARCHAR(500) NULL
	
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getArmour() {
		return armour;
	}
	public String getContractfile() {
		return contractfile;
	}
	public void setContractfile(String contractfile) {
		this.contractfile = contractfile;
	}
	public void setArmour(String armour) {
		this.armour = armour;
	}
	public String getBargainid() {
		return bargainid;
	}
	public void setBargainid(String bargainid) {
		this.bargainid = bargainid;
	}
	public String getBargainmoney() {
		return bargainmoney;
	}
	public void setBargainmoney(String bargainmoney) {
		this.bargainmoney = bargainmoney;
	}
	
	public String getCurname() {
		return curname;
	}
	public void setCurname(String curname) {
		this.curname = curname;
	}
	public String getBlankoutpeople() {
		return blankoutpeople;
	}
	public void setBlankoutpeople(String blankoutpeople) {
		this.blankoutpeople = blankoutpeople;
	}
	public String getBlankouttime() {
		return blankouttime;
	}
	public void setBlankouttime(String blankouttime) {
		this.blankouttime = blankouttime;
	}
	public String getCmemo() {
		return cmemo;
	}
	public void setCmemo(String cmemo) {
		this.cmemo = cmemo;
	}
	public String getDraftout() {
		return draftout;
	}
	public void setDraftout(String draftout) {
		this.draftout = draftout;
	}
	
	public String getBargainterm() {
		return bargainterm;
	}
	public void setBargainterm(String bargainterm) {
		this.bargainterm = bargainterm;
	}
	public String getLinkman() {
		return linkman;
	}
	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}
	public String getEndamendtime() {
		return endamendtime;
	}
	public void setEndamendtime(String endamendtime) {
		this.endamendtime = endamendtime;
	}
	public String getEstate() {
		return estate;
	}
	public void setEstate(String estate) {
		this.estate = estate;
	}
	public String getPaymentfashion() {
		return paymentfashion;
	}
	public void setPaymentfashion(String paymentfashion) {
		this.paymentfashion = paymentfashion;
	}
	public String getSecond() {
		return second;
	}
	public void setSecond(String second) {
		this.second = second;
	}
	public String getSharelist() {
		return sharelist;
	}
	public void setSharelist(String sharelist) {
		this.sharelist = sharelist;
	}
	public String getStartuppeople() {
		return startuppeople;
	}
	public void setStartuppeople(String startuppeople) {
		this.startuppeople = startuppeople;
	}
	public String getStartuptime() {
		return startuptime;
	}
	public void setStartuptime(String startuptime) {
		this.startuptime = startuptime;
	}
	public String getCustomerid() {
		return customerid;
	}
	public void setCustomerid(String customerid) {
		this.customerid = customerid;
	}
	public String getContractname() {
		return contractname;
	}
	public void setContractname(String contractname) {
		this.contractname = contractname;
	}
	public String getProjects() {
		return projects;
	}
	public void setProjects(String projects) {
		this.projects = projects;
	}
	public String getDepartmentid() {
		return departmentid;
	}
	public void setDepartmentid(String departmentid) {
		this.departmentid = departmentid;
	}
	
	
}
