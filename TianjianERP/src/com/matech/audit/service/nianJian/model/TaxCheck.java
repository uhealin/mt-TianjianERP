package com.matech.audit.service.nianJian.model;

import com.matech.framework.pub.db.Table;

@Table(name="z_tax_check",pk="uuid")
public class TaxCheck {
	 protected String uuid ;
	 protected String userId ;
	 protected String name ;
	 protected String sex ;
	 protected String birthday ;
	 protected String education ;
	 protected String cardNum ;
	 protected String unit ;
	 protected String phone ;
	 protected String registId ;
	 protected String rate ;
	 protected String rank ;
	 protected String referenceNo ;
	 protected String selfCondition ;
	 protected String year ;


	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getUserId(){ return this.userId; }
	 public void setUserId(String userId){ this.userId=userId; }
	 public String getName(){ return this.name; }
	 public void setName(String name){ this.name=name; }
	 public String getSex(){ return this.sex; }
	 public void setSex(String sex){ this.sex=sex; }
	 public String getBirthday(){ return this.birthday; }
	 public void setBirthday(String birthday){ this.birthday=birthday; }
	 public String getEducation(){ return this.education; }
	 public void setEducation(String education){ this.education=education; }
	 public String getCardNum(){ return this.cardNum; }
	 public void setCardNum(String cardNum){ this.cardNum=cardNum; }
	 public String getUnit(){ return this.unit; }
	 public void setUnit(String unit){ this.unit=unit; }
	 public String getPhone(){ return this.phone; }
	 public void setPhone(String phone){ this.phone=phone; }
	 public String getRegistId(){ return this.registId; }
	 public void setRegistId(String registId){ this.registId=registId; }
	 public String getRate(){ return this.rate; }
	 public void setRate(String rate){ this.rate=rate; }
	 public String getRank(){ return this.rank; }
	 public void setRank(String rank){ this.rank=rank; }
	 public String getReferenceNo(){ return this.referenceNo; }
	 public void setReferenceNo(String referenceNo){ this.referenceNo=referenceNo; }
	 public String getSelfCondition(){ return this.selfCondition; }
	 public void setSelfCondition(String selfCondition){ this.selfCondition=selfCondition; }
	 public String getYear(){ return this.year; }
	 public void setYear(String year){ this.year=year; }
}
