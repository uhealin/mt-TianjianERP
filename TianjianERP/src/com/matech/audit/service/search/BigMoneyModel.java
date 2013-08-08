package com.matech.audit.service.search;





public class BigMoneyModel extends VoucherSearch {
  private String condition="";
  private String accpakID="";
  private String dpID="";
  private String meoneyType="0";
  public String getCondition() {
    return condition;
  }

  public String getAccpakID() {
    return accpakID;
  }

  public String getDpID() {
    return dpID;
  }

  public void setCondition(String condition) {
    if (condition == null) {
      this.condition = "";
    }
    else {
      this.condition = condition;
    }
  }

  public void setAccpakID(String accpakID) {
    if(accpakID==null){
      this.accpakID="";
    }else{
      this.accpakID = accpakID;
    }
  }

  public void setDpID(String dpID) {
    if(dpID==null){
      this.dpID="";
    }else{
      this.dpID = dpID;
    }
  }

public String getMeoneyType() {
	return meoneyType;
}

public void setMeoneyType(String meoneyType) {
	this.meoneyType = meoneyType;
}

}
