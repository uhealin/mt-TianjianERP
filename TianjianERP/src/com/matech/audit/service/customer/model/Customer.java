package com.matech.audit.service.customer.model;

/**
 * <p>
 * Title: 客户类
 * </p>
 * <p>
 * Description: 客户类
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006, 2008 MaTech Corporation.All rights reserved.
 * </p>
 * <p>
 * Company: Matech 广州铭太信息科技有限公司
 * </p>
 *
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有， 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 *
 * 贡献者团队: 铭太科技 - 研发中心，审计开发组
 *
 * @author void 2007-6-13
 */
/**
 * @author Administrator
 *
 */
public class Customer {

	private String departId = "";

	private String address = "";

	private String alias = "";

	private String bpr = "";

	private String businessBegin = "";

	private String businessBound = "";

	private String businessEnd = "";

	private String corporate = "";

	private String countryCess = "";

	private String departDate = "";

	private String departName = "";
	
	private String departEnName  = "";

	private String email = "";

	private String enterpriseCode = "";

	private String isCollect;

	private String linkMan = "";

	private String loginAddress = "";

	private String parentDepartId = "";

	private String phone = "";

	private String property = "";

	private String remark = "";

	private String terraCess = "";

	private String vocationId;

	private String fax = "";

	private String postalcode = "";

	private String register = "";

	private String standbyname = "";

	private String stockowner = "";

	private String taxpayer = "";
	
	private String hylx = "";

	private String curname = "";
	
	private String custdepartid = "";
	
	private String recordtime = "";
	
	private String practitioner = "";//从业人数
	private String fashion = "";//经营方式
	private String calling = "";//行业
	private String estate = "";//客户状态
	private String approach = "";//客户资料来源途径
	private String mostly = "";//主负责人
	private String subordination = "";//副负责人
	private String groupname  = ""; //客户所属集团名称
	private String groupplate = ""; //所属集团板块
	
	private String customerShortName  = ""; //客户简称
	
	//单位曾用名
	private String beforeName = "";
	//组织机构性质
	private String iframework = "";
	//所属板块
	private String plate = "";
	//介绍人姓名
	private String intro = "";
	//控股股东/上级公司
	private String parentName = "";	
	//控股方 holding
	private String holding = "";
	//公司性质 companyProperty
	private String companyProperty = "";	
  
//一下是后期加的字段	
	private String sMarket = "";	    //证券市场1	
	private String sockCode = "";	// 股票代码1	
	private String sMarket2 = "";	    //证券市场2	
	private String sockCode2 = "";	// 股票代码2	
	private String customerIeve = "";//客户级别	
	private String webSite = "";     //网    址
	private String projectState = ""; //项目状态	
	private String state = "";       //状态	
	
//报备，报告信息字段
	private String iTmentName = "";	//投资人员
 	private String agency = "";	    //审批机构批准文号
	private String aStateDate = "";	//日期
	private String busineLicense = ""; // 营业执照批准文号
	private String bstateDate = "";	 //日期
	private String directorName = "";	//董事长姓名
	private String directorPhone = "";   // 董事长电话
	private String dSecretary = "";	     //  懂秘
	private String secretaryPhone = "";	//董秘电话
	private String ctaffQuantity = "";  	//职工总数
	private String sAccountant = "";	    //总会计师
	private String fDirector = "";	    //财务总监
	private String accountanrPhone = "";	//总会记师电话
	private String fManager = "";	    //财务经理
	private String fPhone = "";	        // 财务经理电话
	private String stockStartDate = "";	//股票发行日期
	private String stockListingDate = "";//股票上市日期
	private String pOfficeAddress = "";	// 办公地址(省) 
	private String cOfficeAddress = "";	//办公地址（市）
	private String fbusineDate = "";     // 首次承接业务日期
	private String ischange = "";         //是否变更事务所
	private String explain = "";          //独资或控股子公司说明
	private String mergerQuantity = "";   //纳入合并报表范围子公司数量
	private String agoOffice = "";        // 前任事务所
	private String cReason = "";          //变更原因
	
	private String nation = "";          //国别
	private String totalassets = "";          //资产总额
	private String totalcurname = "";          //币种（资产总额）
	
	private String bank = ""; //开户行
	private String vip = ""; //是否是vip
	
	public String getVip() {
		return vip;
	}

	public void setVip(String vip) {
		this.vip = vip;
	}

	public String getsMarket2() {
		return sMarket2;
	}

	public void setsMarket2(String sMarket2) {
		this.sMarket2 = sMarket2;
	}

	public String getSockCode2() {
		return sockCode2;
	}

	public void setSockCode2(String sockCode2) {
		this.sockCode2 = sockCode2;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getiTmentName() {
		return iTmentName;
	}

	public void setiTmentName(String iTmentName) {
		this.iTmentName = iTmentName;
	}

	public String getNation() {
		return nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}

	public String getTotalassets() {
		return totalassets;
	}

	public void setTotalassets(String totalassets) {
		this.totalassets = totalassets;
	}

	public String getTotalcurname() {
		return totalcurname;
	}

	public void setTotalcurname(String totalcurname) {
		this.totalcurname = totalcurname;
	}

	public String getAgency() {
		return agency;
	}

	public void setAgency(String agency) {
		this.agency = agency;
	}

	public String getaStateDate() {
		return aStateDate;
	}

	public void setaStateDate(String aStateDate) {
		this.aStateDate = aStateDate;
	}

	public String getBusineLicense() {
		return busineLicense;
	}

	public void setBusineLicense(String busineLicense) {
		this.busineLicense = busineLicense;
	}

	public String getBstateDate() {
		return bstateDate;
	}

	public void setBstateDate(String bstateDate) {
		this.bstateDate = bstateDate;
	}

	public String getDirectorName() {
		return directorName;
	}

	public void setDirectorName(String directorName) {
		this.directorName = directorName;
	}

	public String getDirectorPhone() {
		return directorPhone;
	}

	public void setDirectorPhone(String directorPhone) {
		this.directorPhone = directorPhone;
	}

	public String getdSecretary() {
		return dSecretary;
	}

	public void setdSecretary(String dSecretary) {
		this.dSecretary = dSecretary;
	}

	public String getSecretaryPhone() {
		return secretaryPhone;
	}

	public void setSecretaryPhone(String secretaryPhone) {
		this.secretaryPhone = secretaryPhone;
	}

	public String getCtaffQuantity() {
		return ctaffQuantity;
	}

	public void setCtaffQuantity(String ctaffQuantity) {
		this.ctaffQuantity = ctaffQuantity;
	}

	public String getsAccountant() {
		return sAccountant;
	}

	public void setsAccountant(String sAccountant) {
		this.sAccountant = sAccountant;
	}

	public String getfDirector() {
		return fDirector;
	}

	public void setfDirector(String fDirector) {
		this.fDirector = fDirector;
	}

	public String getAccountanrPhone() {
		return accountanrPhone;
	}

	public void setAccountanrPhone(String accountanrPhone) {
		this.accountanrPhone = accountanrPhone;
	}

	public String getfManager() {
		return fManager;
	}

	public void setfManager(String fManager) {
		this.fManager = fManager;
	}

	public String getfPhone() {
		return fPhone;
	}

	public void setfPhone(String fPhone) {
		this.fPhone = fPhone;
	}

	public String getStockStartDate() {
		return stockStartDate;
	}

	public void setStockStartDate(String stockStartDate) {
		this.stockStartDate = stockStartDate;
	}

	public String getStockListingDate() {
		return stockListingDate;
	}

	public void setStockListingDate(String stockListingDate) {
		this.stockListingDate = stockListingDate;
	}

	public String getpOfficeAddress() {
		return pOfficeAddress;
	}

	public void setpOfficeAddress(String pOfficeAddress) {
		this.pOfficeAddress = pOfficeAddress;
	}

	public String getcOfficeAddress() {
		return cOfficeAddress;
	}

	public void setcOfficeAddress(String cOfficeAddress) {
		this.cOfficeAddress = cOfficeAddress;
	}

	public String getFbusineDate() {
		return fbusineDate;
	}

	public void setFbusineDate(String fbusineDate) {
		this.fbusineDate = fbusineDate;
	}

	public String getIschange() {
		return ischange;
	}

	public void setIschange(String ischange) {
		this.ischange = ischange;
	}

	public String getExplain() {
		return explain;
	}

	public void setExplain(String explain) {
		this.explain = explain;
	}

	public String getMergerQuantity() {
		return mergerQuantity;
	}

	public void setMergerQuantity(String mergerQuantity) {
		this.mergerQuantity = mergerQuantity;
	}

	public String getAgoOffice() {
		return agoOffice;
	}

	public void setAgoOffice(String agoOffice) {
		this.agoOffice = agoOffice;
	}

	public String getcReason() {
		return cReason;
	}

	public void setcReason(String cReason) {
		this.cReason = cReason;
	}
	public String getsMarket() {
		return sMarket;
	}

	public void setsMarket(String sMarket) {
		this.sMarket = sMarket;
	}

	public String getSockCode() {
		return sockCode;
	}

	public void setSockCode(String sockCode) {
		this.sockCode = sockCode;
	}

	public String getCustomerIeve() {
		return customerIeve;
	}

	public void setCustomerIeve(String customerIeve) {
		this.customerIeve = customerIeve;
	}

	public String getWebSite() {
		return webSite;
	}

	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}

	public String getProjectState() {
		return projectState;
	}

	public void setProjectState(String projectState) {
		this.projectState = projectState;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCompanyProperty() {
		return companyProperty;
	}

	public void setCompanyProperty(String companyProperty) {
		this.companyProperty = companyProperty;
	}

	public String getBeforeName() {
		return beforeName;
	}

	public void setBeforeName(String beforeName) {
		this.beforeName = beforeName;
	}


	public String getIframework() {
		return iframework;
	}

	public void setIframework(String iframework) {
		this.iframework = iframework;
	}

	public String getPlate() {
		return plate;
	}

	public void setPlate(String plate) {
		this.plate = plate;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getHolding() {
		return holding;
	}

	public void setHolding(String holding) {
		this.holding = holding;
	}

	public String getCustomerShortName() {
		return customerShortName;
	}

	public void setCustomerShortName(String customerShortName) {
		this.customerShortName = customerShortName;
	}

	private String departmentid;

	public String getDepartmentid() {
		return departmentid;
	}

	public void setDepartmentid(String departmentid) {
		this.departmentid = departmentid;
	}

	public String getCustdepartid() {
		return custdepartid;
	}

	public void setCustdepartid(String custdepartid) {
		this.custdepartid = custdepartid;
	}

	public String getRecordtime() {
		return recordtime;
	}

	public void setRecordtime(String recordtime) {
		this.recordtime = recordtime;
	}

	public String getCurname() {
		return curname;
	}

	public void setCurname(String curname) {
		this.curname = curname;
	}

	public String getHylx() {
		return hylx;
	}

	public void setHylx(String hylx) {
		this.hylx = hylx;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getBpr() {
		return bpr;
	}

	public void setBpr(String bpr) {
		this.bpr = bpr;
	}

	public String getBusinessBegin() {
		return businessBegin;
	}

	public void setBusinessBegin(String businessBegin) {
		this.businessBegin = businessBegin;
	}

	public String getBusinessBound() {
		return businessBound;
	}

	public void setBusinessBound(String businessBound) {
		this.businessBound = businessBound;
	}

	public String getBusinessEnd() {
		return businessEnd;
	}

	public void setBusinessEnd(String businessEnd) {
		this.businessEnd = businessEnd;
	}

	public String getCorporate() {
		return corporate;
	}

	public void setCorporate(String corporate) {
		this.corporate = corporate;
	}

	public String getCountryCess() {
		return countryCess;
	}

	public void setCountryCess(String countryCess) {
		this.countryCess = countryCess;
	}

	public String getDepartDate() {
		return departDate;
	}

	public void setDepartDate(String departDate) {
		this.departDate = departDate;
	}

	public String getDepartId() {
		return departId;
	}

	public void setDepartId(String departId) {
		this.departId = departId;
	}

	public String getDepartName() {
		return departName;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEnterpriseCode() {
		return enterpriseCode;
	}

	public void setEnterpriseCode(String enterpriseCode) {
		this.enterpriseCode = enterpriseCode;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getIsCollect() {
		return isCollect;
	}

	public void setIsCollect(String isCollect) {
		this.isCollect = isCollect;
	}

	public String getLinkMan() {
		return linkMan;
	}

	public void setLinkMan(String linkMan) {
		this.linkMan = linkMan;
	}

	public String getLoginAddress() {
		return loginAddress;
	}

	public void setLoginAddress(String loginAddress) {
		this.loginAddress = loginAddress;
	}

	public String getParentDepartId() {
		return parentDepartId;
	}

	public void setParentDepartId(String parentDepartId) {
		this.parentDepartId = parentDepartId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPostalcode() {
		return postalcode;
	}

	public void setPostalcode(String postalcode) {
		this.postalcode = postalcode;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getRegister() {
		return register;
	}

	public void setRegister(String register) {
		this.register = register;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getStandbyname() {
		return standbyname;
	}

	public void setStandbyname(String standbyname) {
		this.standbyname = standbyname;
	}

	public String getStockowner() {
		return stockowner;
	}

	public void setStockowner(String stockowner) {
		this.stockowner = stockowner;
	}

	public String getTaxpayer() {
		return taxpayer;
	}

	public void setTaxpayer(String taxpayer) {
		this.taxpayer = taxpayer;
	}

	public String getTerraCess() {
		return terraCess;
	}

	public void setTerraCess(String terraCess) {
		this.terraCess = terraCess;
	}

	public String getVocationId() {
		return vocationId;
	}

	public void setVocationId(String vocationId) {
		this.vocationId = vocationId;
	}

	public String getApproach() {
		return approach;
	}

	public void setApproach(String approach) {
		this.approach = approach;
	}

	public String getCalling() {
		return calling;
	}

	public void setCalling(String calling) {
		this.calling = calling;
	}

	public String getEstate() {
		return estate;
	}

	public void setEstate(String estate) {
		this.estate = estate;
	}

	public String getFashion() {
		return fashion;
	}

	public void setFashion(String fashion) {
		this.fashion = fashion;
	}

	public String getMostly() {
		return mostly;
	}

	public void setMostly(String mostly) {
		this.mostly = mostly;
	}

	public String getPractitioner() {
		return practitioner;
	}

	public void setPractitioner(String practitioner) {
		this.practitioner = practitioner;
	}

	public String getSubordination() {
		return subordination;
	}

	public void setSubordination(String subordination) {
		this.subordination = subordination;
	}

	public String getDepartEnName() {
		return departEnName;
	}

	public void setDepartEnName(String departEnName) {
		this.departEnName = departEnName;
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	public String getGroupplate() {
		return groupplate;
	}

	public void setGroupplate(String groupplate) {
		this.groupplate = groupplate;
	}
	
}
