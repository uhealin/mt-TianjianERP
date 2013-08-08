package com.matech.audit.service.pccpa.model;

import com.matech.framework.pub.db.Table;

@Table(name="xcar_shop4s",pk="uuid")
public class XCarShop4sVO {	 protected String uuid ;
protected String name ;
protected String address ;
protected String sell_phone ;
protected String pid ;
protected String catalog ;
protected String service_phone ;
protected String remark;


public String getUuid(){ return this.uuid; }
public void setUuid(String uuid){ this.uuid=uuid; }
public String getName(){ return this.name; }
public void setName(String name){ this.name=name; }
public String getAddress(){ return this.address; }
public void setAddress(String address){ this.address=address; }
public String getSell_phone(){ return this.sell_phone; }
public void setSell_phone(String sell_phone){ this.sell_phone=sell_phone; }
public String getPid(){ return this.pid; }
public void setPid(String pid){ this.pid=pid; }
public String getCatalog(){ return this.catalog; }
public void setCatalog(String catalog){ this.catalog=catalog; }
public String getService_phone(){ return this.service_phone; }
public void setService_phone(String service_phone){ this.service_phone=service_phone; }
public String getRemark() {
	return remark;
}
public void setRemark(String remark) {
	this.remark = remark;
}



}
