package com.matech.audit.service.search;



public class RVSSearch {
  public RVSSearch() {
  }

  private String subject="";
  private String moneyStr="";
  private String moneyItem="";
  private String moneyLogic=">";
  private String money="";
  private String startDate="";
  private String endDate="";
  private String voucherNumber="";
  private String summary="";
  private String typeID="";

  public void setSubject(String subject){
    if(subject==null){
      this.subject="";
    }
    else{
      this.subject=subject;
    }
  }

  public String getSubject(){
    return subject;
  }

  public void setMoneyStr(String moneyStr){
    if (moneyStr == null) {
      this.moneyStr = "";
    }
    else {
      this.moneyStr = moneyStr;
    }
  }

  public String getMoneyStr(){
  return moneyStr;
  }


  public void setMoney(String money){
    if (money == null) {
      this.money = "";
    }
    else {
      this.money = money;
    }
  }

  public String getMoney(){
  return money;
  }

  public void setMoneyItem(String moneyItem){
    if (moneyItem == null) {
      this.moneyItem = "";
    }
    else {
      this.moneyItem = moneyItem;
    }
  }

  public String getMoneyItem(){
  return moneyItem;
  }

  public void setMoneyLogic(String moneyLogic){
    if (moneyLogic == null) {
      this.moneyLogic = "";
    }
    else {
      this.moneyLogic = moneyLogic;
    }
  }

  public String getMoneyLogic(){
  return moneyLogic;
  }

  public void setStartDate(String startDate){
    if (startDate == null) {
      this.startDate = "";
    }
    else {
      this.startDate = startDate;
    }
  }

  public String getStartDate(){
  return startDate;
  }

  public void setEndDate(String endDate){
    if (endDate == null) {
      this.endDate = "";
    }
    else {
      this.endDate = endDate;
    }
  }

  public String getEndDate(){
  return endDate;
  }

  public void setVoucherNumber(String voucherNumber){
    if (voucherNumber == null) {
      this.voucherNumber = "";
    }
    else {
      this.voucherNumber = voucherNumber;
    }
  }

  public String getVoucherNumber(){
  return voucherNumber;
  }


  public void setSummary(String summary){
    if (summary == null) {
      this.summary = "";
    }
    else {
      this.summary = summary;
    }
  }

  public String getSummary(){
  return summary;
  }

  public void setTypeID(String typeID){
    if (typeID == null) {
      this.typeID = "";
    }
    else {
      this.typeID = typeID;
    }
  }
  public String getTypeID() {
    return typeID;
  }


}
