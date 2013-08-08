package com.matech.audit.service.analyse.model;

import com.matech.framework.pub.db.Table;

@Table(name="an_tableresult",pk="uuid")
public class TableResultVO {

	 protected String uuid ;
	 protected String tableid ;
	 protected String jsonstr ;
	 protected String htmlstr ;
     protected String caption;

	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
     
	 public String getTableid() {
		return tableid;
	}
	public void setTableid(String tableid) {
		this.tableid = tableid;
	}
	public String getJsonstr(){ return this.jsonstr; }
	 public void setJsonstr(String jsonstr){ this.jsonstr=jsonstr; }
	 public String getHtmlstr(){ return this.htmlstr; }
	 public void setHtmlstr(String htmlstr){ this.htmlstr=htmlstr; }
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	 
	 
}
