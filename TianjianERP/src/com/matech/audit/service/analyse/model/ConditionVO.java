package com.matech.audit.service.analyse.model;

import com.matech.framework.pub.db.Table;

@Table(name="an_condition",pk="uuid")
public class ConditionVO { protected String uuid ;
protected String formid ;
protected String caption ;
protected String jsonstr ;
protected String sqlstr ;
protected String lastuser ;
protected String lasttime ;
protected String htmlstr;

public String getUuid(){ return this.uuid; }
public void setUuid(String uuid){ this.uuid=uuid; }
public String getFormid(){ return this.formid; }
public void setFormid(String formid){ this.formid=formid; }
public String getCaption(){ return this.caption; }
public void setCaption(String caption){ this.caption=caption; }
public String getJsonstr(){ return this.jsonstr; }
public void setJsonstr(String jsonstr){ this.jsonstr=jsonstr; }
public String getSqlstr(){ return this.sqlstr; }
public void setSqlstr(String sqlstr){ this.sqlstr=sqlstr; }
public String getLastuser(){ return this.lastuser; }
public void setLastuser(String lastuser){ this.lastuser=lastuser; }
public String getLasttime(){ return this.lasttime; }
public void setLasttime(String lasttime){ this.lasttime=lasttime; }
public String getHtmlstr() {
	return htmlstr;
}
public void setHtmlstr(String htmlstr) {
	this.htmlstr = htmlstr;
}

}
