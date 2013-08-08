package com.matech.audit.service.news.model;

import com.matech.framework.pub.db.Table;

@Table(name="oa_news",pk="autoId",insertPk=false)
public class News {
	protected Integer autoId ;
protected String title ;
protected String contents ;
protected String updateTime ;
protected String publishUserId ;
protected String attachmentId ;
protected String memo ;
protected String property ;
protected String big_type ;
protected String type ;
protected String dept_type ;
protected String area ;
protected String nameId ;
protected String menuid ;
protected String sub_title ;
protected String doc_no ;


public Integer getAutoId(){ return this.autoId; }
public void setAutoId(Integer autoId){ this.autoId=autoId; }
public String getTitle(){ return this.title; }
public void setTitle(String title){ this.title=title; }
public String getContents(){ return this.contents; }
public void setContents(String contents){ this.contents=contents; }
public String getUpdateTime(){ return this.updateTime; }
public void setUpdateTime(String updateTime){ this.updateTime=updateTime; }
public String getPublishUserId(){ return this.publishUserId; }
public void setPublishUserId(String publishUserId){ this.publishUserId=publishUserId; }
public String getAttachmentId(){ return this.attachmentId; }
public void setAttachmentId(String attachmentId){ this.attachmentId=attachmentId; }
public String getMemo(){ return this.memo; }
public void setMemo(String memo){ this.memo=memo; }
public String getProperty(){ return this.property; }
public void setProperty(String property){ this.property=property; }
public String getBig_type(){ return this.big_type; }
public void setBig_type(String big_type){ this.big_type=big_type; }
public String getType(){ return this.type; }
public void setType(String type){ this.type=type; }
public String getDept_type(){ return this.dept_type; }
public void setDept_type(String dept_type){ this.dept_type=dept_type; }
public String getArea(){ return this.area; }
public void setArea(String area){ this.area=area; }
public String getNameId(){ return this.nameId; }
public void setNameId(String nameId){ this.nameId=nameId; }
public String getMenuid(){ return this.menuid; }
public void setMenuid(String menuid){ this.menuid=menuid; }
public String getSub_title(){ return this.sub_title; }
public void setSub_title(String sub_titile){ this.sub_title=sub_titile; }
public String getDoc_no(){ return this.doc_no; }
public void setDoc_no(String doc_no){ this.doc_no=doc_no; }}
