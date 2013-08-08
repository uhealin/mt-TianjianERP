package com.matech.audit.service.analyse.model;

import com.matech.framework.pub.db.Table;

@Table(name="an_tablecols",pk="uuid",excludeColumns={"caption"})
public class TableColVO {

	 protected String uuid ;
	 protected String tableid ;
	 protected Integer orderid ;
	 protected String conid ;
	 protected String caption;


	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getTableid(){ return this.tableid; }
	 public void setTableid(String tableid){ this.tableid=tableid; }
	 public Integer getOrderid(){ return this.orderid; }
	 public void setOrderid(Integer orderid){ this.orderid=orderid; }
	 public String getConid(){ return this.conid; }
	 public void setConid(String conid){ this.conid=conid; }
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	 
}
