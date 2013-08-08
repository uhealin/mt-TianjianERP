package com.matech.audit.service.user.model;

/**
 * 员工报到(个人信息表)
 * @author Ymm
 *k_staffregister
 */
public class StaffRegister {
	
	private String id ;//int(20) NOT NULL
	private String name  ;// (20) NOT NULL姓名
	private String loginid  ;// (80) NULL登录名
	private String password  ;// (50) NOT NULL密码
	private String sex  ;// (5) NULL性别
	private String bornDate  ;// (10) NULL出生日期
	private String educational  ;// (50) NULL文化程度
	private String diploma  ;// (50) NULL毕业学校
	private String DepartID  ;// (10) NULL
	private String Rank  ;// (10) NULL职级
	private String Post  ;// (20) NULL岗位
	private String specialty  ;// (100) NULL特长
	private String ParentGroup  ;// (20) NOT NULL
	private String Popedom ;//mediumtext NULL权限
	private String IsTips ;//int(1) NOT NULL
	private String departmentid  ;// (10) NULL部门编号
	private String ProjectPopedom ; //mediumtext NULL项目权限
	private String clientDogSysUi  ;// (20) NULL加密狗
	private String state ; //int(1) NULL状态
	private String userPhoto  ;// (100) NULL存放照片的
	private String userPhotoTemp  ;// (100) NULL
	private String mobilePhone  ;// (20) NULL手机
	private String phone  ;// (20) NULL电话
	private String email  ;// (50) NULL邮件
	private String cpano  ;// (20) NULL
	private String floor  ;// (100) NULL楼层
	private String house  ;// (100) NULL房间号
	private String station  ;// (100) NULL工位号
	private String identityCard  ;// (25) NULL身份证号
	private String paperstype  ;// (100) NULL证件类型
	private String papersnumber  ;// (100) NULL证件号码
	private String nation  ;// (100) NULL民族
	private String marriage  ;// (100) NULL婚姻状况
	private String category  ;// (100) NULL户口性质
	private String place  ;// (100) NULL籍贯
	private String residence  ;// (100) NULL户口所在地
	private String politics  ;// (100) NULL政治面貌
	private String partytime  ;// (100) NULL入党团时间
	private String relationships  ;// (100) NULL组织关系所在单位
	private String profession  ;// (100) NULL专业
	private String compact  ;// (100) NULL合同类型
	private String workstate  ;// (100) NULL员工状态
	private String leavetype  ;// (100) NULL离职类型
	private String english  ;// (100) NULL英语能力
	private String diplomatime  ;// (100) NULL毕业时间
	private String entrytime  ;// (100) NULL入职时间
	private String ip  ;// (100) NULLIP
	private String forbiddenDate  ;// (100) NULL禁用时间
	private String resume; // mediumtext NULL个人简历描述
	private String employWay  ;// (150) NULL应聘途径
	private String referrer  ;// (150) NULL推荐人
	private String contactWay  ;// (150) NULL推荐人联系方式
	private String cpa  ;// (100) NULL是否参加全国CPA考试
	private String examSubject  ;// (200) NULL通过考试科目
	private String createDate  ;// (150) NULL创建时间
	private String createUser  ;// (100) NULL创建人
	private String createDepartment  ;// (100) NULL创建部门
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getBornDate() {
		return bornDate;
	}
	public void setBornDate(String bornDate) {
		this.bornDate = bornDate;
	}
	public String getEducational() {
		return educational;
	}
	public void setEducational(String educational) {
		this.educational = educational;
	}
	public String getLoginid() {
		return loginid;
	}
	public void setLoginid(String loginid) {
		this.loginid = loginid;
	}
	public String getDiploma() {
		return diploma;
	}
	public void setDiploma(String diploma) {
		this.diploma = diploma;
	}
	public String getDepartID() {
		return DepartID;
	}
	public void setDepartID(String departID) {
		DepartID = departID;
	}
	public String getRank() {
		return Rank;
	}
	public void setRank(String rank) {
		Rank = rank;
	}
	public String getPost() {
		return Post;
	}
	public void setPost(String post) {
		Post = post;
	}
	public String getSpecialty() {
		return specialty;
	}
	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}
	public String getParentGroup() {
		return ParentGroup;
	}
	public void setParentGroup(String parentGroup) {
		ParentGroup = parentGroup;
	}
	public String getPopedom() {
		return Popedom;
	}
	public void setPopedom(String popedom) {
		Popedom = popedom;
	}
	public String getIsTips() {
		return IsTips;
	}
	public void setIsTips(String isTips) {
		IsTips = isTips;
	}
	public String getDepartmentid() {
		return departmentid;
	}
	public void setDepartmentid(String departmentid) {
		this.departmentid = departmentid;
	}
	public String getProjectPopedom() {
		return ProjectPopedom;
	}
	public void setProjectPopedom(String projectPopedom) {
		ProjectPopedom = projectPopedom;
	}
	public String getClientDogSysUi() {
		return clientDogSysUi;
	}
	public void setClientDogSysUi(String clientDogSysUi) {
		this.clientDogSysUi = clientDogSysUi;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
	public String getIdentityCard() {
		return identityCard;
	}
	public void setIdentityCard(String identityCard) {
		this.identityCard = identityCard;
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
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
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
	public String getEnglish() {
		return english;
	}
	public void setEnglish(String english) {
		this.english = english;
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
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getForbiddenDate() {
		return forbiddenDate;
	}
	public void setForbiddenDate(String forbiddenDate) {
		this.forbiddenDate = forbiddenDate;
	}
	public String getResume() {
		return resume;
	}
	public void setResume(String resume) {
		this.resume = resume;
	}
	public String getEmployWay() {
		return employWay;
	}
	public void setEmployWay(String employWay) {
		this.employWay = employWay;
	}
	public String getReferrer() {
		return referrer;
	}
	public void setReferrer(String referrer) {
		this.referrer = referrer;
	}
	public String getContactWay() {
		return contactWay;
	}
	public void setContactWay(String contactWay) {
		this.contactWay = contactWay;
	}
	public String getCpa() {
		return cpa;
	}
	public void setCpa(String cpa) {
		this.cpa = cpa;
	}
	public String getExamSubject() {
		return examSubject;
	}
	public void setExamSubject(String examSubject) {
		this.examSubject = examSubject;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getCreateDepartment() {
		return createDepartment;
	}
	public void setCreateDepartment(String createDepartment) {
		this.createDepartment = createDepartment;
	}
	
	
}
