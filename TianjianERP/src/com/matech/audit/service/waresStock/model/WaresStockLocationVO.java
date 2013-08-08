package com.matech.audit.service.waresStock.model;

import com.matech.framework.pub.db.Table;

@Table(name="k_waresstock_location",pk="uuid")
public class WaresStockLocationVO {
	 protected String uuid ;
	 protected String code ;
	 protected String address ;


	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getCode(){ return this.code; }
	 public void setCode(String code){ this.code=code; }
	 public String getAddress(){ return this.address; }
	 public void setAddress(String address){ this.address=address; }
}
