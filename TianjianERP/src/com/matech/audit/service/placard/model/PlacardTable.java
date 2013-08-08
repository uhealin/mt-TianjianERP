package com.matech.audit.service.placard.model;

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
public class PlacardTable {
  private String addresser;  //发信人
  private String addresserTime; //发信时间
  private String caption;   //标题
  private String matter;    //原因(内容)
  private String addressee;  //收件人
  private int isRead;   //是否阅读
  private int isReversion;  //是否回复
  private int ID;  
  private int isNotReversion; //不用回复
  private String Property;   //类型
  private String mpShortMessage = "否";  //是否发送手机短息
  private String mpContent = ""; //短信内容
  private String ctype;
  private String myImage;
  private String name;
  private String uuid;        //邮件id
  private String model;			//标签名
  private String url;			//目标位置和方法
  private String uuidName;		//邮件id名
  
  public String getUuidName() {
	return uuidName;
}

public void setUuidName(String uuidName) {
	this.uuidName = uuidName;
}

public String getUuid() {
	return uuid;
}

public void setUuid(String uuid) {
	this.uuid = uuid;
}

public String getModel() {
	return model;
}

public void setModel(String model) {
	this.model = model;
}

public String getUrl() {
	return url;
}

public void setUrl(String url) {
	this.url = url;
}

public String getMpContent() {
	return mpContent;
 }

 public void setMpContent(String mpContent) {
	this.mpContent = mpContent;
 }

 public String getMpShortMessage() {
	return mpShortMessage;
  }

   public void setMpShortMessage(String mpShortMessage) {
		this.mpShortMessage = mpShortMessage;
   }

  public PlacardTable() {
  }

  public void setAddresser(String addresser) {
    this.addresser = addresser;
  }

  public void setAddresserTime(String addresserTime) {
    this.addresserTime = addresserTime;
  }

  public void setCaption(String caption) {
    this.caption = caption;
  }

  public void setMatter(String matter) {
    this.matter = matter;
  }

  public void setAddressee(String addressee) {
    this.addressee = addressee;
  }

  public void setIsRead(int isRead) {
    this.isRead = isRead;
  }

  public void setIsReversion(int isReversion) {
    this.isReversion = isReversion;
  }

  public void setName(String name) {
    this.name = name;
  }
  public void setImage(String myImage)
  {
	  this.myImage=myImage;
  }

  public void setID(int ID) {
    this.ID = ID;
  }
  public String getImage()
  {
	  return myImage;
  }

  public String getAddresser() {
    return addresser;
  }

  public String getAddresserTime() {
    return addresserTime;
  }

  public String getCaption() {
    return caption;
  }

  public String getMatter() {
    return matter;
  }

  public String getAddressee() {
    return addressee;
  }

  public int getIsRead() {
    return isRead;
  }

  public int getIsReversion() {
    return isReversion;
  }

  public String getName() {
    return name;
  }

  public int getID() {
    return ID;
  }

public int getIsNotReversion() {
	return isNotReversion;
}

public void setIsNotReversion(int isNotReversion) {
	this.isNotReversion = isNotReversion;
}

public String getProperty() {
	return Property;
}

public void setProperty(String property) {
	Property = property;
}

public String getCtype() {
	return ctype;
}

public void setCtype(String ctype) {
	this.ctype = ctype;
}
}
