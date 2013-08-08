package com.matech.audit.service.rectify;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class RectifyTable {
  private String accpackageId;
  private String entryId;
  private String assitemId;
  private String projectID;

  private String subjectId;
  public RectifyTable() {
  }

  public String getAccpackageId() {
    return accpackageId;
  }

  public void setAccpackageId(String accpackageId) {
    this.accpackageId = accpackageId;
  }

  public String getEntryId() {
    return entryId;
  }

  public void setEntryId(String entryId) {
    this.entryId = entryId;
  }

  public String getAssitemId() {
    return assitemId;
  }

  public void setAssitemId(String assitemId) {
    this.assitemId = assitemId;
  }

  public String getSubjectId() {
    return subjectId;
  }

  public String getProjectID() {
    return projectID;
  }

  public void setSubjectId(String subjectId) {
    this.subjectId = subjectId;
  }

  public void setProjectID(String projectID) {
    this.projectID = projectID;
  }
}
