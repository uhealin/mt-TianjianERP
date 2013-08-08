package com.matech.audit.service.process.model;

public class ProcessDeploy {
	private String id = "";
	private String pdId = "";
	private String pkey = "";
	private String pname = "";
	private String desccontent = "";
	private String property = "";
	private String jbpmXml = "";
	private String flowFile = "";
	private String updateUser = "";
	private String updateTime = "";
	private String relateForm = "" ;
	private String notSelectUserNodes = "" ;
	private String orderByRelateForm = "" ;
	private String processDes = "" ;
	protected String join_sql ;
	 protected String join_head_jarr ;
	 protected String hidden_cols ;
	
	public String getProcessDes() {
		return processDes;
	}

	public void setProcessDes(String processDes) {
		this.processDes = processDes;
	}

	public String getOrderByRelateForm() {
		return orderByRelateForm;
	}

	public void setOrderByRelateForm(String orderByRelateForm) {
		this.orderByRelateForm = orderByRelateForm;
	}

	public String getNotSelectUserNodes() {
		return notSelectUserNodes;
	}

	public void setNotSelectUserNodes(String notSelectUserNodes) {
		this.notSelectUserNodes = notSelectUserNodes;
	}

	public String getRelateForm() {
		return relateForm;
	}

	public void setRelateForm(String relateForm) {
		this.relateForm = relateForm;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getJbpmXml() {
		return jbpmXml;
	}

	public String getFlowFile() {
		return flowFile;
	}

	public void setFlowFile(String flowFile) {
		this.flowFile = flowFile;
	}

	public void setJbpmXml(String jbpmXml) {
		this.jbpmXml = jbpmXml;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPdId() {
		return pdId;
	}

	public void setPdId(String pdId) {
		this.pdId = pdId;
	}

	public String getPkey() {
		return pkey;
	}

	public void setPkey(String pkey) {
		this.pkey = pkey;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	public String getDesccontent() {
		return desccontent;
	}

	public void setDesccontent(String desccontent) {
		this.desccontent = desccontent;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getJoin_sql() {
		return join_sql;
	}

	public void setJoin_sql(String join_sql) {
		this.join_sql = join_sql;
	}

	public String getJoin_head_jarr() {
		return join_head_jarr;
	}

	public void setJoin_head_jarr(String join_head_jarr) {
		this.join_head_jarr = join_head_jarr;
	}

	public String getHidden_cols() {
		return hidden_cols;
	}

	public void setHidden_cols(String hidden_cols) {
		this.hidden_cols = hidden_cols;
	}

}
