package com.matech.audit.service.analyse.model;

import com.matech.framework.pub.db.Table;

@Table(name="an_table",pk="uuid")
public class TableVO {

	 protected String uuid ;
	 protected String caption ;
	 protected String lastuser ;
	 protected String lasttime ;


	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getCaption(){ return this.caption; }
	 public void setCaption(String caption){ this.caption=caption; }
	 public String getLastuser(){ return this.lastuser; }
	 public void setLastuser(String lastuser){ this.lastuser=lastuser; }
	 public String getLasttime(){ return this.lasttime; }
	 public void setLasttime(String lasttime){ this.lasttime=lasttime; }
}
