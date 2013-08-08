package com.matech.audit.service.leaveCompany.model;

import com.matech.framework.pub.db.Table;

@Table(name="k_leaveofficeTJ",pk="UUID")
public class LeaveCompany {
	  private String UUID;
	  private String userId ;//'离职人',
	  private String sex ;// '性别',
	  private String departmentid;//'部门id',
	  private String birthday;//COMMENT '出生年月',
	  private String inworktime;//'参加工作时间',
	  private String incompanytime;// '进所时间',
	  private String education ;// '学历',
	  private String degree ;// '学位',
	  private String major; //'所学专业',
	  private String graduation;// '毕业院校',
	  private String qualification ;// '执业资格',
	  private String rank ; //'现任职级',
	  private String techQualification;// '专业技术资格',
	  private String mobilePhone ;// '手机号码',
	  private String applyDate;// '申请日期',
	  private String predictLeaveDate ; //'预计离职日期',
	  private String reason ;// '离职原因',
	  private String status; //'状态',
	  public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	private String property;//'备用',
	  public String getUUID() {
		return UUID;
	}
	public void setUUID(String uUID) {
		UUID = uUID;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getDepartmentid() {
		return departmentid;
	}
	public void setDepartmentid(String departmentid) {
		this.departmentid = departmentid;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getInworktime() {
		return inworktime;
	}
	public void setInworktime(String inworktime) {
		this.inworktime = inworktime;
	}
	public String getIncompanytime() {
		return incompanytime;
	}
	public void setIncompanytime(String incompanytime) {
		this.incompanytime = incompanytime;
	}
	public String getEducation() {
		return education;
	}
	public void setEducation(String education) {
		this.education = education;
	}
	public String getDegree() {
		return degree;
	}
	public void setDegree(String degree) {
		this.degree = degree;
	}
	public String getMajor() {
		return major;
	}
	public void setMajor(String major) {
		this.major = major;
	}
	public String getGraduation() {
		return graduation;
	}
	public void setGraduation(String graduation) {
		this.graduation = graduation;
	}
	public String getQualification() {
		return qualification;
	}
	public void setQualification(String qualification) {
		this.qualification = qualification;
	}
	public String getRank() {
		return rank;
	}
	public void setRank(String rank) {
		this.rank = rank;
	}
	public String getTechQualification() {
		return techQualification;
	}
	public void setTechQualification(String techQualification) {
		this.techQualification = techQualification;
	}
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public String getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}
	public String getPredictLeaveDate() {
		return predictLeaveDate;
	}
	public void setPredictLeaveDate(String predictLeaveDate) {
		this.predictLeaveDate = predictLeaveDate;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}

}
