package com.matech.audit.service.pccpa.model;

import com.matech.framework.pub.db.Table;

@Table(name="tj_wordfj",pk="ID")
public class WordFjVO {
	
	 protected Integer ID ;
	 protected String DocID ;
	 protected String FJPath ;
	 protected String FJName ;
	 protected String CTime ;
	 protected String CName ;


	 public Integer getID(){ return this.ID; }
	 public void setID(Integer ID){ this.ID=ID; }
	 public String getDocID(){ return this.DocID; }
	 public void setDocID(String DocID){ this.DocID=DocID; }
	 public String getFJPath(){ return this.FJPath; }
	 public void setFJPath(String FJPath){ this.FJPath=FJPath; }
	 public String getFJName(){ return this.FJName; }
	 public void setFJName(String FJName){ this.FJName=FJName; }
	 public String getCTime(){ return this.CTime; }
	 public void setCTime(String CTime){ this.CTime=CTime; }
	 public String getCName(){ return this.CName; }
	 public void setCName(String CName){ this.CName=CName; }
}
