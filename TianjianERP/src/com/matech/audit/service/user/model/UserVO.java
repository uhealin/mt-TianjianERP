package com.matech.audit.service.user.model;

import com.matech.framework.pub.db.Table;


@Table(name="k_user",pk="id",insertPk=false)
public class UserVO {
	
	
		 protected Integer id ;
		 protected String Name ;
		 protected String loginid ;
		 protected String Password ;
		 protected String Sex ;
		 protected String BornDate ;
		 protected String Educational ;
		 protected String Diploma ;
		 protected String DepartID ;
		 protected String Rank ;
		 protected String Post ;
		 protected String Specialty ;
		 protected String ParentGroup ;
		 protected String Popedom ;
		 protected Integer IsTips ;
		 protected String departmentid ;
		 protected String ProjectPopedom ;
		 protected String clientDogSysUi ;
		 protected Integer state ;
		 protected String userPhoto ;
		 protected String userPhotoTemp ;
		 protected String mobilePhone ;
		 protected String phone ;
		 protected String email ;
		 protected String cpano ;
		 protected String floor ;
		 protected String house ;
		 protected String station ;
		 protected String identityCard ;
		 protected String paperstype ;
		 protected String papersnumber ;
		 protected String nation ;
		 protected String marriage ;
		 protected String place ;
		 protected String residence ;
		 protected String politics ;
		 protected String partytime ;
		 protected String relationships ;
		 protected String profession ;
		 protected String compact ;
		 protected String workstate ;
		 protected String leavetype ;
		 protected String english ;
		 protected String diplomatime ;
		 protected String entrytime ;
		 protected String ip ;
		 protected String resume ;
		 protected String address ;
		 protected String emtype ;
		 protected String pccpa_id ;


		 public Integer getId(){ return this.id; }
		 public void setId(Integer id){ this.id=id; }
		 public String getName(){ return this.Name; }
		 public void setName(String Name){ this.Name=Name; }
		 public String getLoginid(){ return this.loginid; }
		 public void setLoginid(String loginid){ this.loginid=loginid; }
		 public String getPassword(){ return this.Password; }
		 public void setPassword(String Password){ this.Password=Password; }
		 public String getSex(){ return this.Sex; }
		 public void setSex(String Sex){ this.Sex=Sex; }
		 public String getBornDate(){ return this.BornDate; }
		 public void setBornDate(String BornDate){ this.BornDate=BornDate; }
		 public String getEducational(){ return this.Educational; }
		 public void setEducational(String Educational){ this.Educational=Educational; }
		 public String getDiploma(){ return this.Diploma; }
		 public void setDiploma(String Diploma){ this.Diploma=Diploma; }
		 public String getDepartID(){ return this.DepartID; }
		 public void setDepartID(String DepartID){ this.DepartID=DepartID; }
		 public String getRank(){ return this.Rank; }
		 public void setRank(String Rank){ this.Rank=Rank; }
		 public String getPost(){ return this.Post; }
		 public void setPost(String Post){ this.Post=Post; }
		 public String getSpecialty(){ return this.Specialty; }
		 public void setSpecialty(String Specialty){ this.Specialty=Specialty; }
		 public String getParentGroup(){ return this.ParentGroup; }
		 public void setParentGroup(String ParentGroup){ this.ParentGroup=ParentGroup; }
		 public String getPopedom(){ return this.Popedom; }
		 public void setPopedom(String Popedom){ this.Popedom=Popedom; }
		 public Integer getIsTips(){ return this.IsTips; }
		 public void setIsTips(Integer IsTips){ this.IsTips=IsTips; }
		 public String getDepartmentid(){ return this.departmentid; }
		 public void setDepartmentid(String departmentid){ this.departmentid=departmentid; }
		 public String getProjectPopedom(){ return this.ProjectPopedom; }
		 public void setProjectPopedom(String ProjectPopedom){ this.ProjectPopedom=ProjectPopedom; }
		 public String getClientDogSysUi(){ return this.clientDogSysUi; }
		 public void setClientDogSysUi(String clientDogSysUi){ this.clientDogSysUi=clientDogSysUi; }
		 public Integer getState(){ return this.state; }
		 public void setState(Integer state){ this.state=state; }
		 public String getUserPhoto(){ return this.userPhoto; }
		 public void setUserPhoto(String userPhoto){ this.userPhoto=userPhoto; }
		 public String getUserPhotoTemp(){ return this.userPhotoTemp; }
		 public void setUserPhotoTemp(String userPhotoTemp){ this.userPhotoTemp=userPhotoTemp; }
		 public String getMobilePhone(){ return this.mobilePhone; }
		 public void setMobilePhone(String mobilePhone){ this.mobilePhone=mobilePhone; }
		 public String getPhone(){ return this.phone; }
		 public void setPhone(String phone){ this.phone=phone; }
		 public String getEmail(){ return this.email; }
		 public void setEmail(String email){ this.email=email; }
		 public String getCpano(){ return this.cpano; }
		 public void setCpano(String cpano){ this.cpano=cpano; }
		 public String getFloor(){ return this.floor; }
		 public void setFloor(String floor){ this.floor=floor; }
		 public String getHouse(){ return this.house; }
		 public void setHouse(String house){ this.house=house; }
		 public String getStation(){ return this.station; }
		 public void setStation(String station){ this.station=station; }
		 public String getIdentityCard(){ return this.identityCard; }
		 public void setIdentityCard(String identityCard){ this.identityCard=identityCard; }
		 public String getPaperstype(){ return this.paperstype; }
		 public void setPaperstype(String paperstype){ this.paperstype=paperstype; }
		 public String getPapersnumber(){ return this.papersnumber; }
		 public void setPapersnumber(String papersnumber){ this.papersnumber=papersnumber; }
		 public String getNation(){ return this.nation; }
		 public void setNation(String nation){ this.nation=nation; }
		 public String getMarriage(){ return this.marriage; }
		 public void setMarriage(String marriage){ this.marriage=marriage; }
		 public String getPlace(){ return this.place; }
		 public void setPlace(String place){ this.place=place; }
		 public String getResidence(){ return this.residence; }
		 public void setResidence(String residence){ this.residence=residence; }
		 public String getPolitics(){ return this.politics; }
		 public void setPolitics(String politics){ this.politics=politics; }
		 public String getPartytime(){ return this.partytime; }
		 public void setPartytime(String partytime){ this.partytime=partytime; }
		 public String getRelationships(){ return this.relationships; }
		 public void setRelationships(String relationships){ this.relationships=relationships; }
		 public String getProfession(){ return this.profession; }
		 public void setProfession(String profession){ this.profession=profession; }
		 public String getCompact(){ return this.compact; }
		 public void setCompact(String compact){ this.compact=compact; }
		 public String getWorkstate(){ return this.workstate; }
		 public void setWorkstate(String workstate){ this.workstate=workstate; }
		 public String getLeavetype(){ return this.leavetype; }
		 public void setLeavetype(String leavetype){ this.leavetype=leavetype; }
		 public String getEnglish(){ return this.english; }
		 public void setEnglish(String english){ this.english=english; }
		 public String getDiplomatime(){ return this.diplomatime; }
		 public void setDiplomatime(String diplomatime){ this.diplomatime=diplomatime; }
		 public String getEntrytime(){ return this.entrytime; }
		 public void setEntrytime(String entrytime){ this.entrytime=entrytime; }
		 public String getIp(){ return this.ip; }
		 public void setIp(String ip){ this.ip=ip; }
		 public String getResume(){ return this.resume; }
		 public void setResume(String resume){ this.resume=resume; }
		 public String getAddress(){ return this.address; }
		 public void setAddress(String address){ this.address=address; }
		 public String getEmtype(){ return this.emtype; }
		 public void setEmtype(String emtype){ this.emtype=emtype; }
		 public String getPccpa_id(){ return this.pccpa_id; }
		 public void setPccpa_id(String pccpa_id){ this.pccpa_id=pccpa_id; }


}
