package com.matech.audit.service.oa.bargainbalance.model;
/**
 * 合同结算计划oa_bargainbalance
 * @author Administrator
 *
 */
public class BargainBalanceTable {

	private int autoid; // 编号

	private String bargainid; // 合同编号

	private String firstparty;// 甲方

	private String secondparty;// 乙方;

	private String plandate; // 计划结算日期

	private String planmoney; // 计划计算金额

	private String planfashion; // 计划结算方式

	private String checkinname;// 登记人
	
	private String checkId;   //登记人Id

	private String checkintime;// 登记时间

	private String plancondition;// 结算条件选填

	private String property;// 备用字段

	public int getAutoid() {
		return autoid;
	}

	public void setAutoid(int autoid) {
		this.autoid = autoid;
	}

	public String getBargainid() {
		return bargainid;
	}

	public void setBargainid(String bargainid) {
		this.bargainid = bargainid;
	}

	public String getCheckinname() {
		return checkinname;
	}

	public void setCheckinname(String checkinname) {
		this.checkinname = checkinname;
	}

	public String getCheckintime() {
		return checkintime;
	}

	public void setCheckintime(String checkintime) {
		this.checkintime = checkintime;
	}


	public String getFirstparty() {
		return firstparty;
	}

	public void setFirstparty(String firstparty) {
		this.firstparty = firstparty;
	}

	public String getPlancondition() {
		return plancondition;
	}

	public void setPlancondition(String plancondition) {
		this.plancondition = plancondition;
	}

	public String getPlandate() {
		return plandate;
	}

	public void setPlandate(String plandate) {
		this.plandate = plandate;
	}

	public String getPlanfashion() {
		return planfashion;
	}

	public void setPlanfashion(String planfashion) {
		this.planfashion = planfashion;
	}

	public String getPlanmoney() {
		return planmoney;
	}

	public void setPlanmoney(String planmoney) {
		this.planmoney = planmoney;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getSecondparty() {
		return secondparty;
	}

	public void setSecondparty(String secondparty) {
		this.secondparty = secondparty;
	}

	public String getCheckId() {
		return checkId;
	}

	public void setCheckId(String checkId) {
		this.checkId = checkId;
	}

}
