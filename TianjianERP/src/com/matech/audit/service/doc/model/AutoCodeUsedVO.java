package com.matech.audit.service.doc.model;

import com.matech.framework.pub.db.Table;

@Table(name="k_autocodeused",pk="uuid")
public class AutoCodeUsedVO {
	
	
		 protected String uuid ;
		 protected String number ;
		 protected String fullnumber ;
		 protected Integer year ;
		 protected String atype ;
		 protected Integer state ;
		 protected String applyuser ;
		 protected String applydate ;
		 protected String abandonuser ;
		 protected String abandondate ;
		 protected String property ;


		 public String getUuid(){ return this.uuid; }
		 public void setUuid(String uuid){ this.uuid=uuid; }
		 public String getNumber(){ return this.number; }
		 public void setNumber(String number){ this.number=number; }
		 public String getFullnumber(){ return this.fullnumber; }
		 public void setFullnumber(String fullnumber){ this.fullnumber=fullnumber; }
		 public Integer getYear(){ return this.year; }
		 public void setYear(Integer year){ this.year=year; }
		 public String getAtype(){ return this.atype; }
		 public void setAtype(String atype){ this.atype=atype; }
		 public Integer getState(){ return this.state; }
		 public void setState(Integer state){ this.state=state; }
		 public String getApplyuser(){ return this.applyuser; }
		 public void setApplyuser(String applyuser){ this.applyuser=applyuser; }
		 public String getApplydate(){ return this.applydate; }
		 public void setApplydate(String applydate){ this.applydate=applydate; }
		 public String getAbandonuser(){ return this.abandonuser; }
		 public void setAbandonuser(String abandonuser){ this.abandonuser=abandonuser; }
		 public String getAbandondate(){ return this.abandondate; }
		 public void setAbandondate(String abandondate){ this.abandondate=abandondate; }
		 public String getProperty(){ return this.property; }
		 public void setProperty(String property){ this.property=property; }


}
