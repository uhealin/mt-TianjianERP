package com.matech.audit.service.educationtime.model;

public class EducationTime {
	private String id;
	private String className;
	private String teacherName;
	private String time;
	private String examResult;
	private String attendanceNum;
	private String fullNum;
	private String attendancePercent;
	private String graduationReason;
	private String graduationNum;   //证书编号
	private String hoursType;     //学时形式
	private String hoursNum;      //学时数
	private String isPass;
	private String remark;
	private String username;   //学员姓名
	private String classNum;   //培训班编号
	private String educationtime;  //培训时间
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getClassNum() {
		return classNum;
	}
	public void setClassNum(String classNum) {
		this.classNum = classNum;
	}
	public String getEducationtime() {
		return educationtime;
	}
	public void setEducationtime(String educationtime) {
		this.educationtime = educationtime;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getTeacherName() {
		return teacherName;
	}
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getExamResult() {
		return examResult;
	}
	public void setExamResult(String examResult) {
		this.examResult = examResult;
	}
	public String getAttendanceNum() {
		return attendanceNum;
	}
	public void setAttendanceNum(String attendanceNum) {
		this.attendanceNum = attendanceNum;
	}
	public String getFullNum() {
		return fullNum;
	}
	public void setFullNum(String fullNum) {
		this.fullNum = fullNum;
	}
	public String getAttendancePercent() {
		return attendancePercent;
	}
	public void setAttendancePercent(String attendancePercent) {
		this.attendancePercent = attendancePercent;
	}
	public String getGraduationReason() {
		return graduationReason;
	}
	public void setGraduationReason(String graduationReason) {
		this.graduationReason = graduationReason;
	}
	public String getGraduationNum() {
		return graduationNum;
	}
	public void setGraduationNum(String graduationNum) {
		this.graduationNum = graduationNum;
	}
	public String getHoursType() {
		return hoursType;
	}
	public void setHoursType(String hoursType) {
		this.hoursType = hoursType;
	}
	public String getHoursNum() {
		return hoursNum;
	}
	public void setHoursNum(String hoursNum) {
		this.hoursNum = hoursNum;
	}
	public String getIsPass() {
		return isPass;
	}
	public void setIsPass(String isPass) {
		this.isPass = isPass;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
}
