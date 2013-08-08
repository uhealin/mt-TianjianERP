package com.matech.audit.service.waresStock.model;

import com.matech.framework.pub.db.Table;

@Table(name="k_waresstock_grant",pk="uuid")
public class WaresStockGrantVO {
	 protected String uuid ;
	 protected String wareStockId ;
	 protected String granter_id ;
	 protected String giveto_id ;
	 protected String checker_id ;
	 protected String remark ;
	 protected String departmentid ;
	 protected String state ;
	 protected String signing_time ;
	 protected String userid ;
     protected Integer qutity=1;

	 public Integer getQutity() {
		return qutity;
	}
	public void setQutity(Integer qutity) {
		this.qutity = qutity;
	}
	public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getWareStockId(){ return this.wareStockId; }
	 public void setWareStockId(String wareStockId){ this.wareStockId=wareStockId; }
	 public String getGranter_id(){ return this.granter_id; }
	 public void setGranter_id(String granter_id){ this.granter_id=granter_id; }
	 public String getGiveto_id(){ return this.giveto_id; }
	 public void setGiveto_id(String giveto_id){ this.giveto_id=giveto_id; }
	 public String getChecker_id(){ return this.checker_id; }
	 public void setChecker_id(String checker_id){ this.checker_id=checker_id; }
	 public String getRemark(){ return this.remark; }
	 public void setRemark(String remark){ this.remark=remark; }
	 public String getDepartmentid(){ return this.departmentid; }
	 public void setDepartmentid(String departmentid){ this.departmentid=departmentid; }
	 public String getState(){ return this.state; }
	 public void setState(String state){ this.state=state; }
	 public String getSigning_time(){ return this.signing_time; }
	 public void setSigning_time(String signing_time){ this.signing_time=signing_time; }
	 public String getUserid(){ return this.userid; }
	 public void setUserid(String userid){ this.userid=userid; }}
