package com.matech.audit.service.nianJian.model;

import com.matech.framework.pub.db.Table;

@Table(name="z_society_check",pk="uuid")
public class SocietyCheck {
	protected String uuid ;
	 protected String sex ;
	 protected String birthday ;
	 protected String currentAddress ;
	 protected String nation ;
	 protected String education ;
	 protected String cardNum ;
	 protected String specialty ;
	 protected String political ;
	 protected String workYear ;
	 protected String zhiyezgNum ;
	 protected String zhiyebaNum ;
	 protected String zhiyehyNum ;
	 protected String phone ;
	 protected String address ;
	 protected String postcode ;
	 protected String zhiyeUnit ;
	 protected String rank ;
	 protected String lastResult ;
	 protected String feePay ;
	 protected String continueEducate ;
	 protected String punishment ;
	 protected String badRecord ;
	 protected String year ;
	 protected String userId ;
	 protected String name;

	 public String getUuid(){ return this.uuid; }
	 public void setUuid(String uuid){ this.uuid=uuid; }
	 public String getSex(){ return this.sex; }
	 public void setSex(String sex){ this.sex=sex; }
	 public String getBirthday(){ return this.birthday; }
	 public void setBirthday(String birthday){ this.birthday=birthday; }
	 public String getCurrentAddress(){ return this.currentAddress; }
	 public void setCurrentAddress(String currentAddress){ this.currentAddress=currentAddress; }
	 public String getNation(){ return this.nation; }
	 public void setNation(String nation){ this.nation=nation; }
	 public String getEducation(){ return this.education; }
	 public void setEducation(String education){ this.education=education; }
	 public String getCardNum(){ return this.cardNum; }
	 public void setCardNum(String cardNum){ this.cardNum=cardNum; }
	 public String getSpecialty(){ return this.specialty; }
	 public void setSpecialty(String specialty){ this.specialty=specialty; }
	 public String getPolitical(){ return this.political; }
	 public void setPolitical(String political){ this.political=political; }
	 public String getWorkYear(){ return this.workYear; }
	 public void setWorkYear(String workYear){ this.workYear=workYear; }
	 public String getZhiyezgNum(){ return this.zhiyezgNum; }
	 public void setZhiyezgNum(String zhiyezgNum){ this.zhiyezgNum=zhiyezgNum; }
	 public String getZhiyebaNum(){ return this.zhiyebaNum; }
	 public void setZhiyebaNum(String zhiyebaNum){ this.zhiyebaNum=zhiyebaNum; }
	 public String getZhiyehyNum(){ return this.zhiyehyNum; }
	 public void setZhiyehyNum(String zhiyehyNum){ this.zhiyehyNum=zhiyehyNum; }
	 public String getPhone(){ return this.phone; }
	 public void setPhone(String phone){ this.phone=phone; }
	 public String getAddress(){ return this.address; }
	 public void setAddress(String address){ this.address=address; }
	 public String getPostcode(){ return this.postcode; }
	 public void setPostcode(String postcode){ this.postcode=postcode; }
	 public String getZhiyeUnit(){ return this.zhiyeUnit; }
	 public void setZhiyeUnit(String zhiyeUnit){ this.zhiyeUnit=zhiyeUnit; }
	 public String getRank(){ return this.rank; }
	 public void setRank(String rank){ this.rank=rank; }
	 public String getLastResult(){ return this.lastResult; }
	 public void setLastResult(String lastResult){ this.lastResult=lastResult; }
	 public String getFeePay(){ return this.feePay; }
	 public void setFeePay(String feePay){ this.feePay=feePay; }
	 public String getContinueEducate(){ return this.continueEducate; }
	 public void setContinueEducate(String continueEducate){ this.continueEducate=continueEducate; }
	 public String getPunishment(){ return this.punishment; }
	 public void setPunishment(String punishment){ this.punishment=punishment; }
	 public String getBadRecord(){ return this.badRecord; }
	 public void setBadRecord(String badRecord){ this.badRecord=badRecord; }
	 public String getYear(){ return this.year; }
	 public void setYear(String year){ this.year=year; }
	 public String getUserId(){ return this.userId; }
	 public void setUserId(String userId){ this.userId=userId; }
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	 
}
