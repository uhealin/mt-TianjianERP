package com.matech.audit.service.process.model;

import com.matech.framework.pub.db.Table;

@Table(name="mt_jbpm_processdeploy",pk="ID")
public class ProcessDeployVO {
	 protected String ID ;
	 protected String PDID ;
	 protected String PKEY ;
	 protected String PNAME ;
	 protected String DESCCONTENT ;
	 protected String PROPERTY ;
	 protected String FLOWFILE ;
	 protected String UPDATEUSER ;
	 protected String UPDATETIME ;
	 protected String JBPMXML ;
	 protected String relateForm ;
	 protected String notSelectUserNodes ;
	 protected String orderByRelateForm ;
	 protected String processDes ;
	 protected String join_sql ;
	 protected String join_head_jarr ;
	 protected String hidden_cols ;

	 public String getID(){ return this.ID; }
	 public void setID(String ID){ this.ID=ID; }
	 public String getPDID(){ return this.PDID; }
	 public void setPDID(String PDID){ this.PDID=PDID; }
	 public String getPKEY(){ return this.PKEY; }
	 public void setPKEY(String PKEY){ this.PKEY=PKEY; }
	 public String getPNAME(){ return this.PNAME; }
	 public void setPNAME(String PNAME){ this.PNAME=PNAME; }
	 public String getDESCCONTENT(){ return this.DESCCONTENT; }
	 public void setDESCCONTENT(String DESCCONTENT){ this.DESCCONTENT=DESCCONTENT; }
	 public String getPROPERTY(){ return this.PROPERTY; }
	 public void setPROPERTY(String PROPERTY){ this.PROPERTY=PROPERTY; }
	 public String getFLOWFILE(){ return this.FLOWFILE; }
	 public void setFLOWFILE(String FLOWFILE){ this.FLOWFILE=FLOWFILE; }
	 public String getUPDATEUSER(){ return this.UPDATEUSER; }
	 public void setUPDATEUSER(String UPDATEUSER){ this.UPDATEUSER=UPDATEUSER; }
	 public String getUPDATETIME(){ return this.UPDATETIME; }
	 public void setUPDATETIME(String UPDATETIME){ this.UPDATETIME=UPDATETIME; }
	 public String getJBPMXML(){ return this.JBPMXML; }
	 public void setJBPMXML(String JBPMXML){ this.JBPMXML=JBPMXML; }
	 public String getRelateForm(){ return this.relateForm; }
	 public void setRelateForm(String relateForm){ this.relateForm=relateForm; }
	 public String getNotSelectUserNodes(){ return this.notSelectUserNodes; }
	 public void setNotSelectUserNodes(String notSelectUserNodes){ this.notSelectUserNodes=notSelectUserNodes; }
	 public String getOrderByRelateForm(){ return this.orderByRelateForm; }
	 public void setOrderByRelateForm(String orderByRelateForm){ this.orderByRelateForm=orderByRelateForm; }
	 public String getProcessDes(){ return this.processDes; }
	 public void setProcessDes(String processDes){ this.processDes=processDes; }
	 public String getJoin_sql(){ return this.join_sql; }
	 public void setJoin_sql(String join_sql){ this.join_sql=join_sql; }
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
