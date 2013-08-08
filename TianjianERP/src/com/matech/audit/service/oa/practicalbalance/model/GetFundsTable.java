package com.matech.audit.service.oa.practicalbalance.model;

public class GetFundsTable {
	private String autoid;// 标识列
	private String projectid;// 项目编号
	private String ctype;// 收款形式
	private String ctypenumber;// 收款形式
	private String receiceMoney;// 收款金额
	private String receicedate;// 收款日期
	private String accounttype;// 账面分类
	private String createUser;// 操作人
	private String remark;// 备注
	private String property;// 备用字段
	private String invoicenumber;  //发票编号
	private String certificateNumber; //凭证号
	private String customerCode; //客户代码
	private String payCustomerId ; //付款单位
	private String continueDepartId; //承接部门
	
	public String getPayCustomerId() {
		return payCustomerId;
	}
	public void setPayCustomerId(String payCustomerId) {
		this.payCustomerId = payCustomerId;
	}
	public String getContinueDepartId() {
		return continueDepartId;
	}
	public void setContinueDepartId(String continueDepartId) {
		this.continueDepartId = continueDepartId;
	}
	public String getCustomerCode() {
		return customerCode;
	}
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	public String getCertificateNumber() {
		return certificateNumber;
	}
	public void setCertificateNumber(String certificateNumber) {
		this.certificateNumber = certificateNumber;
	}
	public String getInvoicenumber() {
		return invoicenumber;
	}
	public void setInvoicenumber(String invoicenumber) {
		this.invoicenumber = invoicenumber;
	}
	public String getAutoid() {
		return autoid;
	}
	public void setAutoid(String autoid) {
		this.autoid = autoid;
	}
	public String getProjectid() {
		return projectid;
	}
	public void setProjectid(String projectid) {
		this.projectid = projectid;
	}
	public String getCtype() {
		return ctype;
	}
	public void setCtype(String ctype) {
		this.ctype = ctype;
	}
	public String getCtypenumber() {
		return ctypenumber;
	}
	public void setCtypenumber(String ctypenumber) {
		this.ctypenumber = ctypenumber;
	}
	public String getReceiceMoney() {
		return receiceMoney;
	}
	public void setReceiceMoney(String receiceMoney) {
		this.receiceMoney = receiceMoney;
	}
	public String getReceicedate() {
		return receicedate;
	}
	public void setReceicedate(String receicedate) {
		this.receicedate = receicedate;
	}
	public String getAccounttype() {
		return accounttype;
	}
	public void setAccounttype(String accounttype) {
		this.accounttype = accounttype;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}

}
