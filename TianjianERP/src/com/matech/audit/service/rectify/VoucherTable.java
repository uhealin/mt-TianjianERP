package com.matech.audit.service.rectify;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class VoucherTable {
  private int autoid; //	���			���������
  private String accpackageid; //	���ױ��	��session �� request �еõ�

  private String projectID;
  private int voucherid; //	ƾ֤���	���������
  private String typeid; //	ƾ֤����		"��"
  private String vchdate; //	��������		��ǰ����(ϵͳʱ��)
  private String filluser; //	�Ƶ���		��ǰ�û���¼��
  private String audituser; //	�����		null
  private String keepuser; //	������		null
  private String director; //	���		null
  private int affixcount; //	��������	1
  private String description; //	��ע		����
  private String doubtuserid; //	���Ա		null
  private String property; //	����
  public VoucherTable() {
  }

  public String getAccpackageid() {
    return accpackageid;
  }

  public void setAccpackageid(String accpackageid) {
    this.accpackageid = accpackageid;
  }

  public int getAffixcount() {
    return affixcount;
  }

  public void setAffixcount(int affixcount) {
    this.affixcount = affixcount;
  }

  public String getAudituser() {
    return audituser;
  }

  public void setAudituser(String audituser) {
    this.audituser = audituser;
  }

  public int getAutoid() {
    return autoid;
  }

  public void setAutoid(int autoid) {
    this.autoid = autoid;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDirector() {
    return director;
  }

  public void setDirector(String director) {
    this.director = director;
  }

  public String getDoubtuserid() {
    return doubtuserid;
  }

  public void setDoubtuserid(String doubtuserid) {
    this.doubtuserid = doubtuserid;
  }

  public String getFilluser() {
    return filluser;
  }

  public void setFilluser(String filluser) {
    this.filluser = filluser;
  }

  public String getKeepuser() {
    return keepuser;
  }

  public void setKeepuser(String keepuser) {
    this.keepuser = keepuser;
  }

  public String getProperty() {
    return property;
  }

  public void setProperty(String property) {
    this.property = property;
  }

  public String getTypeid() {
    return typeid;
  }

  public void setTypeid(String typeid) {
    this.typeid = typeid;
  }

  public String getVchdate() {
    return vchdate;
  }

  public void setVchdate(String vchdate) {
    this.vchdate = vchdate;
  }

  public int getVoucherid() {
    return voucherid;
  }

  public String getProjectID() {
    return projectID;
  }

  public void setVoucherid(int voucherid) {
    this.voucherid = voucherid;
  }

  public void setProjectID(String projectID) {
    this.projectID = projectID;
  }

}
