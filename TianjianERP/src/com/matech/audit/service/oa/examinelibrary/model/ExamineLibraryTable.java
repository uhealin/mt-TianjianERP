package com.matech.audit.service.oa.examinelibrary.model;

/**
 * 考核指标oa_examinelibrary
 * 
 * @author Administrator
 * 
 */
public class ExamineLibraryTable {
	private int autoid = 0;

	private String ctype = "";

	private String cname = "";

	private String ccal = "";

	private String cformula = "";

	private String originalsql = "";
	
	private String isenable = "";

	private double orderid = 0;

	private String property = "";

	private String memo = "";

	

	public int getAutoid() {
		return autoid;
	}

	public void setAutoid(int autoid) {
		this.autoid = autoid;
	}

	public String getCcal() {
		return ccal;
	}

	public void setCcal(String ccal) {
		this.ccal = ccal;
	}

	public String getCformula() {
		return cformula;
	}

	public void setCformula(String cformula) {
		this.cformula = cformula;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public String getCtype() {
		return ctype;
	}

	public void setCtype(String ctype) {
		this.ctype = ctype;
	}

	public String getIsenable() {
		return isenable;
	}

	public void setIsenable(String isenable) {
		this.isenable = isenable;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public double getOrderid() {
		return orderid;
	}

	public void setOrderid(double orderid) {
		this.orderid = orderid;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getOriginalsql() {
		return originalsql;
	}

	public void setOriginalsql(String originalsql) {
		this.originalsql = originalsql;
	}



}
