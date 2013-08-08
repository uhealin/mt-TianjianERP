package com.matech.audit.service.seal.model;

/**
 * @author YMM
 * 印章 
 *
 */
public class Seal {
	
	private String uuid ;//varchar(50) NOT NULLuuid
	private String userId ;//varchar(20) NULL申请人
	private String applyDate ;//varchar(30) NULL申请时间
	private String matter ;//varchar(30) NULL申请事项
	private String ctype ;//varchar(30) NULL公章类型
	private String status ;//varchar(15) NULL状态(通过/不通过)
	private String fileName ;//varchar(50) NULL附件名
	private String remark ;//mediumtext NULL备注
	private String applyDepartment; //申请事项
	private String applyDepartId;  //用章部门
	private String sealCount;   //盖章数量
	private String attachname;  //需要盖章的附件名
	private String printCount; //剩余打印份数
	
	
	public String getPrintCount() {
		return printCount;
	}
	public void setPrintCount(String printCount) {
		this.printCount = printCount;
	}
	public String getApplyDepartId() {
		return applyDepartId;
	}
	public void setApplyDepartId(String applyDepartId) {
		this.applyDepartId = applyDepartId;
	}
	public String getSealCount() {
		return sealCount;
	}
	public void setSealCount(String sealCount) {
		this.sealCount = sealCount;
	}
	public String getApplyDepartment() {
		return applyDepartment;
	}
	public void setApplyDepartment(String applyDepartment) {
		this.applyDepartment = applyDepartment;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}
	public String getMatter() {
		return matter;
	}
	public void setMatter(String matter) {
		this.matter = matter;
	}
	public String getCtype() {
		return ctype;
	}
	public void setCtype(String ctype) {
		this.ctype = ctype;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getAttachname() {
		return attachname;
	}
	public void setAttachname(String attachname) {
		this.attachname = attachname;
	}
}
