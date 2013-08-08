package com.matech.audit.service.oa.insurancecircs.model;

/**
 * 员工保险情况管理oa_insurancecircs
 * 
 * @author Administrator
 * 
 */
public class InsurancecircsTable {
	private int autoid = 0;

	private String userid = "";

	private String insurancetype = "";

	private String trusteeshipunit = "";

	private String startdate = "";

	private String enddate = "";

	private String finallymoney = "";

	private String finallydate = "";

	private String checkinperson = "";

	private String checkindate = "";

	private String property = "";

	public int getAutoid() {
		return autoid;
	}

	public void setAutoid(int autoid) {
		this.autoid = autoid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getCheckindate() {
		return checkindate;
	}

	public void setCheckindate(String checkindate) {
		this.checkindate = checkindate;
	}

	public String getCheckinperson() {
		return checkinperson;
	}

	public void setCheckinperson(String checkinperson) {
		this.checkinperson = checkinperson;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public String getFinallydate() {
		return finallydate;
	}

	public void setFinallydate(String finallydate) {
		this.finallydate = finallydate;
	}

	public String getFinallymoney() {
		return finallymoney;
	}

	public void setFinallymoney(String finallymoney) {
		this.finallymoney = finallymoney;
	}

	public String getInsurancetype() {
		return insurancetype;
	}

	public void setInsurancetype(String insurancetype) {
		this.insurancetype = insurancetype;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getTrusteeshipunit() {
		return trusteeshipunit;
	}

	public void setTrusteeshipunit(String trusteeshipunit) {
		this.trusteeshipunit = trusteeshipunit;
	}

}
