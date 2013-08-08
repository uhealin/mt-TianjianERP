package com.matech.audit.service.search;

public class ABSSearch {
  private String cdpID="";
  private String starYear="";
  private String starMonth="";
  private String endYear="";
  private String endMonth="";
  private String section="";
  private String dataType="";
  private String dataDirection="";
  private String subjectLevel="";
  private String subjects="";
  private String d1;
  private String d2;
  private String d3;
  private String d4;
  private String d5;
  private String chartType;
  private String whetherTypeID;
  private String directions;
  private String deul;
  private String isavg;
  private String datedeul;
  public ABSSearch() {
  }

  public void setCdpID(String cdpID) {
    if(cdpID==null){
      this.cdpID="";
    }else{
      this.cdpID = cdpID;
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

  public void setDataType(String dataType) {
    if(dataType==null){
      this.dataType="";
    }else{
      this.dataType = dataType;
    }
  }

  public void setDataDirection(String dataDirection) {
    if(dataDirection==null){
      this.dataDirection="";
    }else{
      this.dataDirection = dataDirection;
    }
  }

  public void setSubjectLevel(String subjectLevel) {
    if(subjectLevel==null){
      this.subjectLevel="";
    }else{
      this.subjectLevel = subjectLevel;
    }
  }

  public void setSubjects(String subjects) {
    if(subjects==null){
      this.subjects="";
    }else{
      this.subjects = subjects;
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

  public String getCdpID() {
    return cdpID;
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

  public String getDataType() {
    return dataType;
  }

  public String getDataDirection() {
    return dataDirection;
  }

  public String getSubjectLevel() {
    return subjectLevel;
  }

  public String getSubjects() {
    return subjects;
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

public String getDirections() {
	return directions;
}

public void setDirections(String directions) {
	this.directions = directions;
}

public String getD5() {
	return d5;
}

public void setD5(String d5) {
	this.d5 = d5;
}

public String getDeul() {
	return deul;
}

public void setDeul(String deul) {
	this.deul = deul;
}

public String getIsavg() {
	return isavg;
}

public void setIsavg(String isavg) {
	this.isavg = isavg;
}

public String getDatedeul() {
	return datedeul;
}

public void setDatedeul(String datedeul) {
	this.datedeul = datedeul;
}


}