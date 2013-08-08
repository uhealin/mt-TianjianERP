package com.matech.audit.service.oa.practicalbalance.model;

/**
 * 合同实际结算登记oa_practicalbalance
 * 
 * @author Administrator
 * 
 */
public class PracticalBalanceTable {
	private String autoid = "";
	private String cid = "";
	private String firstparty = "";
	private String firstpartyid = "0";
	private String secondparty = "";
	
	private String secondpartyid = "0";
	private String bargaindate = "";
	private String bargaintype = "";
	private String bargainmoney = "0";
	private String invoicenumber = "";
	
	private String bargainplan = "";
	private String loginid = "";
	private String loginName = "";
	private String logindate = "";
	private String property = "";
	
	private String projectid = ""; //项目编号
	private String billMoney = "0"; //发票金额
	private String recipient = ""; //发票客户接收人
	
	private String receiptState = ""; //收款状态
	private String invoiceState = ""; //开票状态
	
	public String getReceiptState() {
		return receiptState;
	}

	public void setReceiptState(String receiptState) {
		this.receiptState = receiptState;
	}

	public String getInvoiceState() {
		return invoiceState;
	}

	public void setInvoiceState(String invoiceState) {
		this.invoiceState = invoiceState;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getAutoid() {
		return autoid;
	}

	public void setAutoid(String autoid) {
		this.autoid = autoid;
	}

	public String getBargaindate() {
		return bargaindate;
	}

	public void setBargaindate(String bargaindate) {
		this.bargaindate = bargaindate;
	}

	public String getBargainmoney() {
		return bargainmoney;
	}

	public void setBargainmoney(String bargainmoney) {
		this.bargainmoney = bargainmoney;
	}

	public String getBargainplan() {
		return bargainplan;
	}

	public void setBargainplan(String bargainplan) {
		this.bargainplan = bargainplan;
	}

	public String getBargaintype() {
		return bargaintype;
	}

	public void setBargaintype(String bargaintype) {
		this.bargaintype = bargaintype;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getFirstparty() {
		return firstparty;
	}

	public void setFirstparty(String firstparty) {
		this.firstparty = firstparty;
	}

	public String getInvoicenumber() {
		return invoicenumber;
	}

	public void setInvoicenumber(String invoicenumber) {
		this.invoicenumber = invoicenumber;
	}

	public String getLogindate() {
		return logindate;
	}

	public void setLogindate(String logindate) {
		this.logindate = logindate;
	}

	public String getLoginid() {
		return loginid;
	}

	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getSecondparty() {
		return secondparty;
	}

	public void setSecondparty(String secondparty) {
		this.secondparty = secondparty;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getProjectid() {
		return projectid;
	}

	public void setProjectid(String projectid) {
		this.projectid = projectid;
	}

	public String getBillMoney() {
		return billMoney;
	}

	public void setBillMoney(String billMoney) {
		this.billMoney = billMoney;
	}

	public String getFirstpartyid() {
		return firstpartyid;
	}

	public void setFirstpartyid(String firstpartyid) {
		this.firstpartyid = firstpartyid;
	}

	public String getSecondpartyid() {
		return secondpartyid;
	}

	public void setSecondpartyid(String secondpartyid) {
		this.secondpartyid = secondpartyid;
	}

}
