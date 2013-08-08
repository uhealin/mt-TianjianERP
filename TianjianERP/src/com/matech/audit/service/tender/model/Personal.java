package com.matech.audit.service.tender.model;

public class Personal {
	private long id;
	private String unitId;
	private String deptId;
	private String sex;
	private int age;
	private String degreeEducation;
	private String profTechQualifi;
	private String rank;
	private String qualificationId;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUnitId() {
		return unitId;
	}
	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getDegreeEducation() {
		return degreeEducation;
	}
	public void setDegreeEducation(String degreeEducation) {
		this.degreeEducation = degreeEducation;
	}
	public String getProfTechQualifi() {
		return profTechQualifi;
	}
	public void setProfTechQualifi(String profTechQualifi) {
		this.profTechQualifi = profTechQualifi;
	}
	public String getRank() {
		return rank;
	}
	public void setRank(String rank) {
		this.rank = rank;
	}
	public String getQualificationId() {
		return qualificationId;
	}
	public void setQualificationId(String qualificationId) {
		this.qualificationId = qualificationId;
	}
}
