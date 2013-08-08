package com.matech.audit.service.invoiceentry.model;
/**
 * 指标设置的ＶＯ，表名为k_rule
 * @author Administrator
 *
 */
public class BillManagerTable {

	//字段：
	//编号     客户名称（下拉） 合同编号（下拉）   发票号码   发票代码    发票名称   金额         税种   
	//autoid   customerName    bargainId         billId    billCode   billName  billMoney    tax

	//类型   state(是否作废)  登记时间       备注
	//taxtype     state       billTime      property
	
	private String autoid;

	private String customerName;

	private String bargainId;

	private String billId;

	private String billCode;

	private String billName;

	private String billMoney;
	
	private String tax;
	
	private String taxtype;
	
	private String state;
	
	private String billTime;
	
	private String property;
	
	
	

	public BillManagerTable() {
		// TODO Auto-generated constructor stub
	}

	public String getAutoid() {
		return autoid;
	}

	public void setAutoid(String autoid) {
		this.autoid = autoid;
	}

	public String getBargainId() {
		return bargainId;
	}

	public void setBargainId(String bargainId) {
		this.bargainId = bargainId;
	}

	public String getBillCode() {
		return billCode;
	}

	public void setBillCode(String billCode) {
		this.billCode = billCode;
	}

	public String getBillId() {
		return billId;
	}

	public void setBillId(String billId) {
		this.billId = billId;
	}

	public String getBillMoney() {
		return billMoney;
	}

	public void setBillMoney(String billMoney) {
		this.billMoney = billMoney;
	}

	public String getBillName() {
		return billName;
	}

	public void setBillName(String billName) {
		this.billName = billName;
	}

	public String getBillTime() {
		return billTime;
	}

	public void setBillTime(String billTime) {
		this.billTime = billTime;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getTax() {
		return tax;
	}

	public void setTax(String tax) {
		this.tax = tax;
	}

	public String getTaxtype() {
		return taxtype;
	}

	public void setTaxtype(String taxtype) {
		this.taxtype = taxtype;
	}
 

}
