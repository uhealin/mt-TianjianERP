package com.matech.audit.service.rule;



public class RuleSearch {
  private String prjectIdOrCustomerId="";
  private String starYear="";
  private String starMonth="";
  private String endYear="";
  private String endMonth="";
  private String section="";
  private String method="";
  private String ruleProperty="";
  private String rules="";
  private String d1;
  private String d2;
  private String d3;
  private String d4;
  private String d5;
  private String chartType;
  private String whetherTypeID;
  private String bi;
  public RuleSearch() {
  }



  public String getPrjectIdOrCustomerId() {
	return prjectIdOrCustomerId;
}



public void setPrjectIdOrCustomerId(String prjectIdOrCustomerId) {
	if(prjectIdOrCustomerId==null){
	      this.prjectIdOrCustomerId="";
	    }else{
	      this.prjectIdOrCustomerId = prjectIdOrCustomerId;
	    }
}



public void setStarYear(String starYear) {
    if(starYear==null){
      this.starYear="";
    }else{
      this.starYear = starYear;
    }
  }

  public void setStarMonth(String starMonth) {
    if(starMonth==null){
      this.starMonth="";
    }else{
      this.starMonth = starMonth;
    }
  }

  public void setEndYear(String endYear) {
    if(endYear==null){
      this.endYear="";
    }else{
      this.endYear = endYear;
    }

  }

  public void setEndMonth(String endMonth) {
    if(endMonth==null){
      this.endMonth="";
    }else{
      this.endMonth = endMonth;
    }
  }

  public void setSection(String section) {
    if(section==null){
      this.section="";
    }else{
      this.section = section;
    }
  }

  

  public String getBi() {
	return bi;
}

public void setBi(String bi) {
	 if(bi==null){
	      this.bi="";
	    }else{
	      this.bi = bi;
	    }
}

public String getMethod() {
	return method;
}

public void setMethod(String method) {
	 if(method==null){
	      this.method="";
	    }else{
	      this.method = method;
	    }
}

public String getRuleProperty() {
	return ruleProperty;
}

public void setRuleProperty(String ruleProperty) {
	 if(ruleProperty==null){
	      this.ruleProperty="";
	    }else{
	      this.ruleProperty = ruleProperty;
	    }
}

public String getRules() {
	return rules;
}

public void setRules(String rules) {
	 if(rules==null){
	      this.rules="";
	    }else{
	      this.rules = rules;
	    }
}

public void setD1(String d1) {
    if(d1==null){
      this.d1="";
    }else{
      this.d1 = d1;
    }
  }

  public void setD2(String d2) {
    if(d2==null){
      this.d2="";
    }else{
      this.d2 = d2;
    }
  }

  public void setD3(String d3) {
    if(d3==null){
      this.d3 = "";
    }else{
      this.d3 = d3;
    }
  }

  public void setD4(String d4) {
    if(d4==null){
      this.d4="";
    }else{
      this.d4 = d4;
    }
  }

  public void setChartType(String chartType) {
    this.chartType = chartType;
  }



  public String getStarYear() {
    return starYear;
  }

  public String getStarMonth() {
    return starMonth;
  }

  public String getEndYear() {
    return endYear;
  }

  public String getEndMonth() {
    return endMonth;
  }

  public String getSection() {
    return section;
  }



  public String getD1() {
    return d1;
  }

  public String getD2() {
    return d2;
  }

  public String getD3() {
    return d3;
  }

  public String getD4() {
    return d4;
  }

  public String getChartType() {
    return chartType;
  }

public String getWhetherTypeID() {
	return whetherTypeID;
}

public void setWhetherTypeID(String whetherTypeID) {
	this.whetherTypeID = whetherTypeID;
}



public String getD5() {
	return d5;
}

public void setD5(String d5) {
	this.d5 = d5;
}


}