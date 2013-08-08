package com.matech.audit.service.invoiceentry.model;

public class InvoiceTable {
	private String autoid;// 标识列
	private String projectid;// 项目编号
	private String invoicenumber;// 发票编号
	private String username;   //经办人
	private String time;      //经办时间
	private String receiceUser;// 收票人
	private String money;// 开票金额
	private String cdate;// 开票日期
	private String remark;// 备注
	private String createUser;// 操作人
	private String property;// 备用字段
	private String companyName;// 开票单位名称
	private String invoiceItem;// 开票项目
	private String departmentId;// 所在部门
	private String customerCode; //客户代码
	private String companyProperties; //单位性质
	private String incomeItem; //收入类项目
	private String ifPlanGathering; //是否预收款
	private String state; ///状态
	private String payUnitId; //付款客户
	
	public String getPayUnitId() {
		return payUnitId;
	}
	public void setPayUnitId(String payUnitId) {
		this.payUnitId = payUnitId;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getIfPlanGathering() {
		return ifPlanGathering;
	}
	public void setIfPlanGathering(String ifPlanGathering) {
		this.ifPlanGathering = ifPlanGathering;
	}
	public String getCustomerCode() {
		return customerCode;
	}
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}
	public String getCompanyProperties() {
		return companyProperties;
	}
	public void setCompanyProperties(String companyProperties) {
		this.companyProperties = companyProperties;
	}
	public String getIncomeItem() {
		return incomeItem;
	}
	public void setIncomeItem(String incomeItem) {
		this.incomeItem = incomeItem;
	}
	public String getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getInvoiceItem() {
		return invoiceItem;
	}
	public void setInvoiceItem(String invoiceItem) {
		this.invoiceItem = invoiceItem;
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
	public String getInvoicenumber() {
		return invoicenumber;
	}
	public void setInvoicenumber(String invoicenumber) {
		this.invoicenumber = invoicenumber;
	}
	public String getReceiceUser() {
		return receiceUser;
	}
	public void setReceiceUser(String receiceUser) {
		this.receiceUser = receiceUser;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getCdate() {
		return cdate;
	}
	public void setCdate(String cdate) {
		this.cdate = cdate;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}

}
