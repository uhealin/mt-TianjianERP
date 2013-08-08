package com.matech.audit.service.search;

public class takeOutVoucher extends VoucherSearch{
  private String createor="";
  
  private String cSubjectid="";
  public String getCreateor() {
    return createor;
  }

  public void setCreateor(String createor) {
    if(createor==null){
      this.createor = "";
    }else{
      this.createor = createor;
    }
  }

public String getCSubjectid() {
	return cSubjectid;
}

public void setCSubjectid(String cSubjectid) {
    if(cSubjectid==null){
        this.cSubjectid = "";
      }else{
        this.cSubjectid = cSubjectid;
      }
}

}
