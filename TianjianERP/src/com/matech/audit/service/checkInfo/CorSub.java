package com.matech.audit.service.checkInfo;

public class CorSub {
  private String subjectId;
  private String subjectFullName;
  private double occurValue;
  public String getSubjectId() {
    return subjectId;
  }

  public String getSubjectFullName() {
    return subjectFullName;
  }

  public double getOccurValue() {
    return occurValue;
  }

  public void setSubjectId(String subjectId) {
    this.subjectId = subjectId;
  }

  public void setSubjectFullName(String subjectFullName) {
    this.subjectFullName = subjectFullName;
  }

  public void setOccurValue(double occurValue) {
    this.occurValue = occurValue;
  }
}
