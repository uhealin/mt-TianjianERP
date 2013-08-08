package com.matech.audit.service.oa.personCapture.model;

/**
 * 人事档案缴费管理oa_personcapture
 * 
 * @author Administrator
 * 
 */
public class PersonCaptureTable {
	private int autoid = 0;

	private String userid = "";

	private String units = "";

	private String starttime = "";

	private String endtime = "";

	private String capturemoney = "";

	private String endcapturetime = "";

	private String booker = "";

	private String checkintime = "";

	private String property = "";

	public int getAutoid() {
		return autoid;
	}

	public void setAutoid(int autoid) {
		this.autoid = autoid;
	}

	public String getBooker() {
		return booker;
	}

	public void setBooker(String booker) {
		this.booker = booker;
	}

	public String getCapturemoney() {
		return capturemoney;
	}

	public void setCapturemoney(String capturemoney) {
		this.capturemoney = capturemoney;
	}

	public String getCheckintime() {
		return checkintime;
	}

	public void setCheckintime(String checkintime) {
		this.checkintime = checkintime;
	}

	public String getEndcapturetime() {
		return endcapturetime;
	}

	public void setEndcapturetime(String endcapturetime) {
		this.endcapturetime = endcapturetime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}
}
