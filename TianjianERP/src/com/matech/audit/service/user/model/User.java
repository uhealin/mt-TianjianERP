package com.matech.audit.service.user.model;

import com.matech.audit.service.userdef.Userdef;
import com.matech.framework.pub.db.Table;

/**
 * 
 * <p>Title: TODO</p>
 * <p>Description: TODO</p>
 * <p>Copyright: Copyright (c) 2006, 2008 MaTech Corporation.All rights reserved. </p>
 * <p>Company: Matech  广州铭太信息科技有限公司</p>
 * 
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有，
 * 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 *
 * 贡献者团队:
 *     铭太科技 - 研发中心，审计开发组
 *
 * @author LuckyStar
 * 2007-6-9
 */
@Table(name="k_user",pk="id",excludeColumns={"userdefs","roles","wrokState"},insertPk=false)
public class User {
	private String id;
	private String name = "";
	private String loginid = "";
	private String password = "";
	//	  private String password_two;
	private String sex = "";
	private String borndate = "";
	private String educational = "";
	private String diploma = "";
	private String departid = "";
	private String rank = "";
	private String post = "";
	private String specialty = "";	
	private String parentgroup = "";
	private String popedom = "";
	private String istips = "";
	private String departmentid = "";
	private String state = "";
	private String roles = "";
	private String clientDogSysUi = "";
	private String userPhoto = "" ; //附件名
	private String userPhotoTemp = "" ;//附件随机名字
	private String mobilePhone = ""; //手机号
	private String phone = "" ;  //座机号
	private String email = "" ;  //邮件
	private String cpano = "" ;  //cpano
	
	private String floor = "" ; //楼层 
	private String house = "" ; //房间号
	private String station = "" ; //工位号
	private String address ="";//详细地址
	
	private String wrokState="";//工作状态
	
	private Userdef [] userdefs= null;
	
	private String paperstype =""; //证件类型
	private String papersnumber =""; //证件号码
	private String nation =""; //民族
	private String marriage =""; //婚姻状况
	private String place =""; //籍贯

	private String residence =""; //户口所在地
	private String politics =""; //政治面貌
	private String partytime =""; //入党团时间
	private String relationships =""; //组织关系所在单位
	private String profession =""; //专业

	private String compact =""; //合同类型
	private String workstate =""; //员工状态
	private String leavetype =""; //离职类型
	private String english = ""; //英语能力
	private String diplomatime = ""; //毕业时间
	
	private String entrytime = ""; //入职时间
	
	private String identityCard = "";  //身份证号码
	
	private String ip="";		//ip地址
	
	private String resume="";    //个人简历综述
	
	protected String emtype="";
	
	protected String pccpa_id;
	
	private String tel_shortno = ""; //手机短号
	private String phone_shortno = ""; //办公短号
	private String bank_card_no = ""; //银行卡号
	private String bank_card_name = ""; //银行卡名
	
	
	public String getTel_shortno() {
		return tel_shortno;
	}
	public void setTel_shortno(String tel_shortno) {
		this.tel_shortno = tel_shortno;
	}
	public String getPhone_shortno() {
		return phone_shortno;
	}
	public void setPhone_shortno(String phone_shortno) {
		this.phone_shortno = phone_shortno;
	}
	public String getBank_card_no() {
		return bank_card_no;
	}
	public void setBank_card_no(String bank_card_no) {
		this.bank_card_no = bank_card_no;
	}
	public String getBank_card_name() {
		return bank_card_name;
	}
	public void setBank_card_name(String bank_card_name) {
		this.bank_card_name = bank_card_name;
	}
	public String getResume() {
		return resume;
	}
	public void setResume(String resume) {
		this.resume = resume;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getIdentityCard() {
		return identityCard;
	}
	public void setIdentityCard(String identityCard) {
		this.identityCard = identityCard;
	}
	public String getDiplomatime() {
		return diplomatime;
	}
	public void setDiplomatime(String diplomatime) {
		this.diplomatime = diplomatime;
	}
	public String getEntrytime() {
		return entrytime;
	}
	public void setEntrytime(String entrytime) {
		this.entrytime = entrytime;
	}
	public String getEnglish() {
		return english;
	}
	public void setEnglish(String english) {
		this.english = english;
	}
	public String getPaperstype() {
		return paperstype;
	}
	public void setPaperstype(String paperstype) {
		this.paperstype = paperstype;
	}
	public String getPapersnumber() {
		return papersnumber;
	}
	public void setPapersnumber(String papersnumber) {
		this.papersnumber = papersnumber;
	}
	public String getNation() {
		return nation;
	}
	public void setNation(String nation) {
		this.nation = nation;
	}
	public String getMarriage() {
		return marriage;
	}
	public void setMarriage(String marriage) {
		this.marriage = marriage;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getResidence() {
		return residence;
	}
	public void setResidence(String residence) {
		this.residence = residence;
	}
	public String getPolitics() {
		return politics;
	}
	public void setPolitics(String politics) {
		this.politics = politics;
	}
	public String getPartytime() {
		return partytime;
	}
	public void setPartytime(String partytime) {
		this.partytime = partytime;
	}
	public String getRelationships() {
		return relationships;
	}
	public void setRelationships(String relationships) {
		this.relationships = relationships;
	}
	public String getProfession() {
		return profession;
	}
	public void setProfession(String profession) {
		this.profession = profession;
	}
	public String getCompact() {
		return compact;
	}
	public void setCompact(String compact) {
		this.compact = compact;
	}
	public String getWorkstate() {
		return workstate;
	}
	public void setWorkstate(String workstate) {
		this.workstate = workstate;
	}
	public String getLeavetype() {
		return leavetype;
	}
	public void setLeavetype(String leavetype) {
		this.leavetype = leavetype;
	}
	public String getWrokState() {
		return wrokState;
	}
	public void setWrokState(String wrokState) {
		this.wrokState = wrokState;
	}
	
	public Userdef[] getUserdefs() {
		return userdefs;
	}
	public void setUserdefs(Userdef[] userdefs) {
		this.userdefs = userdefs;
	}
	public String getBorndate() {
		return borndate;
	}
	public void setBorndate(String borndate) {
		this.borndate = borndate;
	}
	public String getDepartid() {
		return departid;
	}
	public void setDepartid(String departid) {
		this.departid = departid;
	}
	public String getDiploma() {
		return diploma;
	}
	public void setDiploma(String diploma) {
		this.diploma = diploma;
	}
	public String getEducational() {
		return educational;
	}
	public void setEducational(String educational) {
		this.educational = educational;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPost() {
		return post;
	}
	public void setPost(String post) {
		this.post = post;
	}
	public String getRank() {
		return rank;
	}
	public void setRank(String rank) {
		this.rank = rank;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getSpecialty() {
		return specialty;
	}
	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}
	public String getLoginid() {
		return loginid;
	}
	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDepartmentid() {
		return departmentid;
	}
	public void setDepartmentid(String departmentid) {
		this.departmentid = departmentid;
	}
	public String getIstips() {
		return istips;
	}
	public void setIstips(String istips) {
		this.istips = istips;
	}
	public String getParentgroup() {
		return parentgroup;
	}
	public void setParentgroup(String parentgroup) {
		this.parentgroup = parentgroup;
	}
	public String getPopedom() {
		return popedom;
	}
	public void setPopedom(String popedom) {
		this.popedom = popedom;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getClientDogSysUi() {
		return clientDogSysUi;
	}
	public void setClientDogSysUi(String clientDogSysUi) {
		this.clientDogSysUi = clientDogSysUi;
	}
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}
	public String getUserPhoto() {
		return userPhoto;
	}
	public void setUserPhoto(String userPhoto) {
		this.userPhoto = userPhoto;
	}
	public String getUserPhotoTemp() {
		return userPhotoTemp;
	}
	public void setUserPhotoTemp(String userPhotoTemp) {
		this.userPhotoTemp = userPhotoTemp;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getCpano() {
		return cpano;
	}
	public void setCpano(String cpano) {
		this.cpano = cpano;
	}
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	public String getHouse() {
		return house;
	}
	public void setHouse(String house) {
		this.house = house;
	}
	public String getStation() {
		return station;
	}
	public void setStation(String station) {
		this.station = station;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getEmtype() {
		return emtype;
	}
	public void setEmtype(String emtype) {
		this.emtype = emtype;
	}
	public String getPccpa_id() {
		return pccpa_id;
	}
	public void setPccpa_id(String pccpa_id) {
		this.pccpa_id = pccpa_id;
	}

	
	
	
}
