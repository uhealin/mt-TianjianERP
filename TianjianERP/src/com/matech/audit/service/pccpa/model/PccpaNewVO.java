package com.matech.audit.service.pccpa.model;

import com.matech.framework.pub.db.Table;

@Table(name="newsandclass",pk="NewsID")
public class PccpaNewVO {
	protected String NewsID ;
	 protected String Title ;
	 protected String ClassID ;
	 protected String FileName ;
	 protected String AddDate ;
	 protected String KeyWords ;
	 protected String TxtSource ;
	 protected String Author ;
	 protected Integer ID ;
	 protected String SubTitle ;
	 protected String Content ;
	 protected String DelTime ;
	 protected String NewsTemplet ;
	 protected String banbudate ;
	 protected String zhixingdate ;
	 protected String FSQX ;
	 protected String ClassEName ;
	 protected String ClassCName ;
	 protected String ParentID ;
	 protected String ClassTemp ;
	 protected String NewsTemp ;
	 protected String AddTime ;
	 protected Integer DelFlag ;
	 protected String ClassDelTime ;
	 protected String SaveFilePath ;
	 protected String menuid ;
	 protected String area ;


	 public String getNewsID(){ return this.NewsID; }
	 public void setNewsID(String NewsID){ this.NewsID=NewsID; }
	 public String getTitle(){ return this.Title; }
	 public void setTitle(String Title){ this.Title=Title; }
	 public String getClassID(){ return this.ClassID; }
	 public void setClassID(String ClassID){ this.ClassID=ClassID; }
	 public String getFileName(){ return this.FileName; }
	 public void setFileName(String FileName){ this.FileName=FileName; }
	 public String getAddDate(){ return this.AddDate; }
	 public void setAddDate(String AddDate){ this.AddDate=AddDate; }
	 public String getKeyWords(){ return this.KeyWords; }
	 public void setKeyWords(String KeyWords){ this.KeyWords=KeyWords; }
	 public String getTxtSource(){ return this.TxtSource; }
	 public void setTxtSource(String TxtSource){ this.TxtSource=TxtSource; }
	 public String getAuthor(){ return this.Author; }
	 public void setAuthor(String Author){ this.Author=Author; }
	 public Integer getID(){ return this.ID; }
	 public void setID(Integer ID){ this.ID=ID; }
	 public String getSubTitle(){ return this.SubTitle; }
	 public void setSubTitle(String SubTitle){ this.SubTitle=SubTitle; }
	 public String getContent(){ return this.Content; }
	 public void setContent(String Content){ this.Content=Content; }
	 public String getDelTime(){ return this.DelTime; }
	 public void setDelTime(String DelTime){ this.DelTime=DelTime; }
	 public String getNewsTemplet(){ return this.NewsTemplet; }
	 public void setNewsTemplet(String NewsTemplet){ this.NewsTemplet=NewsTemplet; }
	 public String getBanbudate(){ return this.banbudate; }
	 public void setBanbudate(String banbudate){ this.banbudate=banbudate; }
	 public String getZhixingdate(){ return this.zhixingdate; }
	 public void setZhixingdate(String zhixingdate){ this.zhixingdate=zhixingdate; }
	 public String getFSQX(){ return this.FSQX; }
	 public void setFSQX(String FSQX){ this.FSQX=FSQX; }
	 public String getClassEName(){ return this.ClassEName; }
	 public void setClassEName(String ClassEName){ this.ClassEName=ClassEName; }
	 public String getClassCName(){ return this.ClassCName; }
	 public void setClassCName(String ClassCName){ this.ClassCName=ClassCName; }
	 public String getParentID(){ return this.ParentID; }
	 public void setParentID(String ParentID){ this.ParentID=ParentID; }
	 public String getClassTemp(){ return this.ClassTemp; }
	 public void setClassTemp(String ClassTemp){ this.ClassTemp=ClassTemp; }
	 public String getNewsTemp(){ return this.NewsTemp; }
	 public void setNewsTemp(String NewsTemp){ this.NewsTemp=NewsTemp; }
	 public String getAddTime(){ return this.AddTime; }
	 public void setAddTime(String AddTime){ this.AddTime=AddTime; }
	 public Integer getDelFlag(){ return this.DelFlag; }
	 public void setDelFlag(Integer DelFlag){ this.DelFlag=DelFlag; }
	 public String getClassDelTime(){ return this.ClassDelTime; }
	 public void setClassDelTime(String ClassDelTime){ this.ClassDelTime=ClassDelTime; }
	 public String getSaveFilePath(){ return this.SaveFilePath; }
	 public void setSaveFilePath(String SaveFilePath){ this.SaveFilePath=SaveFilePath; }
	 public String getMenuid(){ return this.menuid; }
	 public void setMenuid(String menuid){ this.menuid=menuid; }
	 public String getArea(){ return this.area; }
	 public void setArea(String area){ this.area=area; }
}
