package com.matech.audit.service.oa.postchange.model;

/**
 * 工作岗位变动记录oa_postchange
 * 
 * @author Administrator
 * 
 */
public class postchangeTable {
	private int autoid = 0;

	private String starttime = "";

	private String endtime = "";

	private String formerlypost = "";

	private String adjustpost = "";

	private String userid = "";

	private String property = "";
	
	private String fdepartmentid = "";
	
	private String adepartmentid = "";

	public String getFdepartmentid() {
		return fdepartmentid;
	}

	public void setFdepartmentid(String fdepartmentid) {
		this.fdepartmentid = fdepartmentid;
	}

	public String getAdepartmentid() {
		return adepartmentid;
	}

	public void setAdepartmentid(String adepartmentid) {
		this.adepartmentid = adepartmentid;
	}

	public String getAdjustpost() {
		return adjustpost;
	}

	public void setAdjustpost(String adjustpost) {
		this.adjustpost = adjustpost;
	}

	public int getAutoid() {
		return autoid;
	}

	public void setAutoid(int autoid) {
		this.autoid = autoid;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getFormerlypost() {
		return formerlypost;
	}

	public void setFormerlypost(String formerlypost) {
		this.formerlypost = formerlypost;
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

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

}
