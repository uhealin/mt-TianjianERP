package com.matech.framework.listener;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpSession;

/**
 * <p>Title: 记录用户在线状态</p>
 * <p>
 * Description:
 * 	UserSession对象主要用来记录用户在线状态，
 * 	从用户登陆开始，UserSession对象就放在session中跟随着用户，
 * 	只有当用户退出系统时，UserSession对象才会销毁。
 *  <br/>
 *  使用例子：例如需要设置当前项目编号curProjectId：
 *  <ul>
 *  	<li>1、从session取得当前用户的UserSession对象：<br/>
 *  			UserSession userSession = (UserSession)session.getAttribute("userSession");</li>
 *
 *  	<li>2、设置当前项目编号：<br/>
 *  			userSession.setCurProjectId("20076301");</li>
 *
 *  	<li>3、把用户的UserSession对象放回session中：<br/>
 *  			session.setAttribute("userSession",userSession);</li>
 *
 *  	<li>4、需要使用时可以在session中取出用户的UserSession对象：<br/>
 *  			UserSession userSession = (UserSession)session.getAttribute("userSession");<br/>
 *  			String projectId = userSession.getCurProjectId();</li>
 *  </ul>
 *  <br/>
 * </p>
 * <p>Copyright: Copyright (c) 2006, 2008 MaTech Corporation.All rights reserved. </p>
 * <p>Company: Matech  广州铭太信息科技有限公司</p>
 *
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有，
 * 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 *
 * 贡献者团队:
 *     铭太科技 - 研发中心，审计开发组
 *
 * @author void
 * 2007-6-13
 */
public class UserSession implements Serializable  {

	/**
	 * 加密狗相关信息
	 */
	private String clientDogSysUi; // 加密狗全球唯一ID

	private String clientDogSysCo; // 加密狗事务所名称

	private String clientDogInfo;// 狗信息

	/**
	 * 客户相关信息
	 */
	private String curCustomerId; // 当前客户id,原来：curDepartId

	private String curCustomerName; // 当前客户名称,原来：curDepartName

	private String curCustomerStandByName; // 当前客户第二名称,原来：curAuditDeptName

	private String curCustomerAccPackageYears; // 当前客户的所有帐套的年份，原来：curAccPackageYears

	/**
	 * 项目相关信息
	 */
	private String curProjectId; // 当前项目Id

	private String curProjectName; // 当前项目名称

	private String curProjectBeginYear; // 当前项目开始年份

	private String curProjectBeginMonth; // 当前项目开始月份

	private String curProjectEndYear; // 当前项目结束年份

	private String curProjectEndMonth; // 当前项目结束月份

	private String curProjectUserRole; // 当前用户在项目中的角色

	private String curProjectState; // 当前项目状态

	private String curProjectProperty;//当前项目属性

	private String curProjectManuscriptCustomerName;//当前项目底稿的客户名称

	private int curProjectSystemId;	//当前项目合并报表体系编号
	
	private String curProjectPostil;	//当前项目附注类型

	/**
	 * 当前审计类型模版
	 */
	private String curAuditTypeProperty; // 当前项目审计类型模版属性

	/**
	 * 当前审计类型
	 */
	private String curAuditType;	//当前项目审计类型

	/**
	 * 查询选择相关
	 */
	private String curChoiceBeginYear; // 当前用户选择开始年份

	private String curChoiceBeginMonth; // 当前用户选择开始月份

	private String curChoiceEndYear; // 当前用户选择结束年份

	private String curChoiceEndMonth; // 当前用户选择结束月份

	private String curChoiceAccPackageId; // 当前用户选择账套编号,原：partAccPackageID

	private String curChoiceCurrencyName;// 用户选择的货币
	/**
	 * 当前账套相关信息
	 */
	private String curAccPackageId; // 当前帐套编号

	private String curAccCurrencyName;//当前账套货币名称


	/**
	 * 底稿相关信息
	 */
	private Set userCurTasks = new HashSet();

	/**
	 * 用户相关信息
	 */
	private String userIp; // 用户IP

	private String userLoginId; // 用户登陆名

	private String userName; // 用户真实姓名

	private String userId; // 用户编号

	private String userPwd; // 用户加密前密码

	private String userLoginTime; // 用户登陆时间

	private String userSessionId; // 用户sessionId

	private HttpSession userSession;// 用户的Session
	
	//新增属性start
	
	private String userMobilePhone; //用户的手机号码
	
	private String userPhone; //用户的电话号码
	
	private String userFloor;//用户所在部门的楼层
	
	private String userAddress;//用户的详细地址
	
	private String userRank;//用户的职级
	
	private String userSex;//性别
	
	private String userBornDate;//--出生日期
	
	private String userEntrytime;//--入职时间
	
	private String userProfession;//--专业
	
	private String userDegree;//学历
	
	private String userEducation;//学位
	
	private String userWrok_time;//--参加工作时间
	
	private String school;	//毕业学校
	
	private String firstSignDate;	//首次签约日期
	
	private String lastContractLimit;	//上次合同期限
	
	private String contractEnd;       //上次合同终止期
	
	private String identitycard;	//身份证
	
	//end
	
	private String userAuditOrganId; //事务所机构ID（对应k_organ表）  如：555555
	private String userAuditOrganName;//事务所机构名称（对应k_organ表） 如：天健会计师事务所 
	
	private String userAuditOfficeId; //一级部门的ID（对应k_department的level0=1）
	private String userAuditOfficeName;//一级部门的名称（对应k_department的level0=1）
	
	private String userAuditDepartmentName;//自己所在部门的id（对应k_department的autoid和自己k_user的departmentid）
	private String userAuditDepartmentId;//自己所在部门的名称（对应k_department的autoid和自己k_user的departmentid）
	
	private String areaid;		//地区编号  //
	private String areaname;	//地区名称	//杭州总部

	private String userAuditCardIdByEOA; //ID卡
	
	private String userAuditOfficeIdByEOA;//事务所机构ID,从OA里面传过来的
	
	private String userAuditOfficeNameByEOA;//事务所机构名称,从OA里面传过来的

	private String userAuditOfficeStandbyName;//事务所机构备用名称

	

	private String userIsTips;//是否打开贴士

	private String userPopedom;//用户权限

	private String userAuditOfficePopedom;//用户事务所拥有的所有权限

	private String userPageSize;//用户分页数

	private String userRole;
	
	private String userWorkPath;
	
	private boolean bInOcx=false;//表示这个SESSION是控件创建的，不是人干D
	
	private String userAuditDepartId ;
	
	private String userAuditDepartName ;
	
	private String ProjectPopedomDepartmentids;  //一个人监管的项目
	
	//加当前人最大角色的优化级
	private int userRoleOptimization = 0;
	
	public int getUserRoleOptimization() {
		return userRoleOptimization;
	}

	public void setUserRoleOptimization(int userRoleOptimization) {
		this.userRoleOptimization = userRoleOptimization;
	}

	public String getUserAuditDepartId() {
		return userAuditDepartId;
	}

	public void setUserAuditDepartId(String userAuditDepartId) {
		this.userAuditDepartId = userAuditDepartId;
	}

	public String getUserAuditDepartName() {
		return userAuditDepartName;
	}

	public void setUserAuditDepartName(String userAuditDepartName) {
		this.userAuditDepartName = userAuditDepartName;
	}

	public boolean getInOcx() {
		return bInOcx;
	}

	public void setInOcx(boolean bInOcx) {
		this.bInOcx = bInOcx;
	}

	
	/**
	 * @return the userRole
	 */
	public String getUserRole() {
		return userRole;
	}

	/**
	 * @param userRole the userRole to set
	 */
	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	/**
	 * @return the clientDogInfo
	 */
	public String getClientDogInfo() {
		return clientDogInfo;
	}

	/**
	 * @param clientDogInfo the clientDogInfo to set
	 */
	public void setClientDogInfo(String clientDogInfo) {
		this.clientDogInfo = clientDogInfo;
	}

	/**
	 * @return the userAuditDepartmentId
	 */
	public String getUserAuditDepartmentId() {
		return userAuditDepartmentId;
	}

	/**
	 * @param userAuditDepartmentId the userAuditDepartmentId to set
	 */
	public void setUserAuditDepartmentId(String userAuditDepartmentId) {
		this.userAuditDepartmentId = userAuditDepartmentId;
	}

	/**
	 * @return the userAuditDepartmentName
	 */
	public String getUserAuditDepartmentName() {
		return userAuditDepartmentName;
	}

	/**
	 * @param userAuditDepartmentName the userAuditDepartmentName to set
	 */
	public void setUserAuditDepartmentName(String userAuditDepartmentName) {
		this.userAuditDepartmentName = userAuditDepartmentName;
	}

	/**
	 * @return the userAuditOfficeName
	 */
	public String getUserAuditOfficeName() {
		return userAuditOfficeName;
	}

	/**
	 * @param userAuditOfficeName the userAuditOfficeName to set
	 */
	public void setUserAuditOfficeName(String userAuditOfficeName) {
		this.userAuditOfficeName = userAuditOfficeName;
	}

	/**
	 * 返回当前账套ID
	 * @return
	 */
	public String getCurAccPackageId() {
		return curAccPackageId;
	}

	/**
	 * 设置当前账套ID
	 * @param curAccPackageId
	 */
	public void setCurAccPackageId(String curAccPackageId) {
		this.curAccPackageId = curAccPackageId;
	}

	/**
	 * 返回当前项目审计模版属性
	 * @return
	 */
	public String getCurAuditTypeProperty() {
		return curAuditTypeProperty;
	}

	/**
	 * 设置当前项目审计类型模版属性
	 * @param curAuditTypeProperty
	 */
	public void setCurAuditTypeProperty(String curAuditTypeProperty) {
		this.curAuditTypeProperty = curAuditTypeProperty;
	}

	/**
	 * 返回当前用户选择的账套ID
	 * @return
	 */
	public String getCurChoiceAccPackageId() {
		return curChoiceAccPackageId;
	}

	/**
	 * 设置当前用户选择的账套ID
	 * @param curChoiceAccPackageId
	 */
	public void setCurChoiceAccPackageId(String curChoiceAccPackageId) {
		this.curChoiceAccPackageId = curChoiceAccPackageId;
	}

	/**
	 * 返回用户选择开始月份
	 * @return
	 */
	public String getCurChoiceBeginMonth() {
		return curChoiceBeginMonth;
	}

	/**
	 * 设置用户选择开始月份
	 * @param curChoiceBeginMonth
	 */
	public void setCurChoiceBeginMonth(String curChoiceBeginMonth) {
		this.curChoiceBeginMonth = curChoiceBeginMonth;
	}

	/**
	 * 返回用户选择开始年份
	 * @return
	 */
	public String getCurChoiceBeginYear() {
		return curChoiceBeginYear;
	}

	/**
	 * 设置用户选择开始年份
	 * @param curChoiceBeginYear
	 */
	public void setCurChoiceBeginYear(String curChoiceBeginYear) {
		this.curChoiceBeginYear = curChoiceBeginYear;
	}

	/**
	 * 返回用户选择结束月份
	 * @return
	 */
	public String getCurChoiceEndMonth() {
		return curChoiceEndMonth;
	}

	/**
	 * 设置用户选择结束月份
	 * @param curChoiceEndMonth
	 */
	public void setCurChoiceEndMonth(String curChoiceEndMonth) {
		this.curChoiceEndMonth = curChoiceEndMonth;
	}

	/**
	 * 返回用户选择结束年份
	 * @return
	 */
	public String getCurChoiceEndYear() {
		return curChoiceEndYear;
	}

	/**
	 * 设置用户选择结束年份
	 * @param curChoiceEndYear
	 */
	public void setCurChoiceEndYear(String curChoiceEndYear) {
		this.curChoiceEndYear = curChoiceEndYear;
	}

	/**
	 * 返回当前客户的所有账套年份
	 * @return
	 */
	public String getCurCustomerAccPackageYears() {
		return curCustomerAccPackageYears;
	}

	/**
	 * 设置当前客户的所有账套年份
	 * @param curCustomerAccPackageYears
	 */
	public void setCurCustomerAccPackageYears(String curCustomerAccPackageYears) {
		this.curCustomerAccPackageYears = curCustomerAccPackageYears;
	}

	/**
	 * 返回当前客户编号
	 * @return
	 */
	public String getCurCustomerId() {
		return curCustomerId;
	}

	/**
	 * 设置当前客户编号
	 * @param curCustomerId
	 */
	public void setCurCustomerId(String curCustomerId) {
		this.curCustomerId = curCustomerId;
	}

	/**
	 * 返回当前客户名称
	 * @return
	 */
	public String getCurCustomerName() {
		return curCustomerName;
	}

	/**
	 * 设置当前客户名称
	 * @param curCustomerName
	 */
	public void setCurCustomerName(String curCustomerName) {
		this.curCustomerName = curCustomerName;
	}

	/**
	 * 返回当前项目开始月份
	 * @return
	 */
	public String getCurProjectBeginMonth() {
		return curProjectBeginMonth;
	}

	/**
	 * 设置当前项目开始月份
	 * @param curProjectBeginMonth
	 */
	public void setCurProjectBeginMonth(String curProjectBeginMonth) {
		this.curProjectBeginMonth = curProjectBeginMonth;
	}

	/**
	 * 返回当前项目开始年份
	 * @return
	 */
	public String getCurProjectBeginYear() {
		return curProjectBeginYear;
	}

	/**
	 * 设置当前项目开始年份
	 * @param curProjectBeginYear
	 */
	public void setCurProjectBeginYear(String curProjectBeginYear) {
		this.curProjectBeginYear = curProjectBeginYear;
	}

	/**
	 * 返回当前项目结束月份
	 * @return
	 */
	public String getCurProjectEndMonth() {
		return curProjectEndMonth;
	}

	/**
	 * 设置当前项目结束月份
	 * @param curProjectEndMonth
	 */
	public void setCurProjectEndMonth(String curProjectEndMonth) {
		this.curProjectEndMonth = curProjectEndMonth;
	}

	/**
	 * 返回当前项目结束年份
	 * @return
	 */
	public String getCurProjectEndYear() {
		return curProjectEndYear;
	}

	/**
	 * 设置当前项目结束年份
	 * @param curProjectEndYear
	 */
	public void setCurProjectEndYear(String curProjectEndYear) {
		this.curProjectEndYear = curProjectEndYear;
	}

	/**
	 * 返回当前项目编号
	 * @return
	 */
	public String getCurProjectId() {
		return curProjectId;
	}

	/**
	 * 设置当前项目编号
	 * @param curProjectId
	 */
	public void setCurProjectId(String curProjectId) {
		this.curProjectId = curProjectId;
	}

	/**
	 * 返回当前项目名称
	 * @return
	 */
	public String getCurProjectName() {
		return curProjectName;
	}

	/**
	 * 设置当前项目名称
	 * @param curProjectName
	 */
	public void setCurProjectName(String curProjectName) {
		this.curProjectName = curProjectName;
	}

	/**
	 * 返回当前项目状态
	 * @return
	 */
	public String getCurProjectState() {
		return curProjectState;
	}

	/**
	 * 设置当前项目状态
	 * @param curProjectState
	 */
	public void setCurProjectState(String curProjectState) {
		this.curProjectState = curProjectState;
	}

	/**
	 * 返回用户在当前项目中的审计角色
	 * @return
	 */
	public String getCurProjectUserRole() {
		return curProjectUserRole;
	}

	/**
	 * 设置用户在当前项目中的审计角色
	 * @param curProjectUserRole
	 */
	public void setCurProjectUserRole(String curProjectUserRole) {
		this.curProjectUserRole = curProjectUserRole;
	}

	/**
	 * 返回当前客户端加密狗中的事务所信息
	 * @return
	 */
	public String getClientDogSysCo() {
		return clientDogSysCo;
	}

	/**
	 * 设置当前客户端加密狗中的事务所信息
	 * @param clientDogSysCo
	 */
	public void setClientDogSysCo(String clientDogSysCo) {
		this.clientDogSysCo = clientDogSysCo;
	}

	/**
	 * 返回当前客户端加密狗中的全球唯一编号
	 * @return
	 */
	public String getClientDogSysUi() {
		return clientDogSysUi;
	}

	/**
	 * 设置当前客户端加密狗中的全球唯一编号
	 * @param clientDogSysUi
	 */
	public void setClientDogSysUi(String clientDogSysUi) {
		this.clientDogSysUi = clientDogSysUi;
	}

	/**
	 * 返回当前用户编号
	 * @return
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * 设置当前用户编号
	 * @param userId
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * 返回当前用户的IP地址
	 * @return
	 */
	public String getUserIp() {
		return userIp;
	}

	/**
	 * 设置当前用户的IP地址
	 * @param userIp
	 */
	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	/**
	 * 返回当前用户登陆名
	 * @return
	 */
	public String getUserLoginId() {
		return userLoginId;
	}

	/**
	 * 设置当前用户登陆名
	 * @param userLoginId
	 */
	public void setUserLoginId(String userLoginId) {
		this.userLoginId = userLoginId;
	}

	/**
	 * 返回用户登陆时间
	 * @return
	 */
	public String getUserLoginTime() {
		return userLoginTime;
	}

	/**
	 * 设置用户登陆时间
	 * @param userLoginTime
	 */
	public void setUserLoginTime(String userLoginTime) {
		this.userLoginTime = userLoginTime;
	}

	/**
	 * 返回用户真实姓名
	 * @return
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * 设置用户真实姓名
	 * @param userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * 返回用户Session
	 * @return
	 */
	public HttpSession getUserSession() {
		return userSession;
	}

	/**
	 * 设置用户Session
	 * @param userSession
	 */
	public void setUserSession(HttpSession userSession) {
		this.userSession = userSession;
	}

	/**
	 * 返回用户SessionId
	 * @return
	 */
	public String getUserSessionId() {
		return userSessionId;
	}

	/**
	 * 设置用户SessionId
	 * @param userSessionId
	 */
	public void setUserSessionId(String userSessionId) {
		this.userSessionId = userSessionId;
	}

	/**
	 * 返回当前选择客户编号
	 * @return
	 */
	public String getCurChoiceCustomerId() {
		if(curChoiceAccPackageId==null||curChoiceAccPackageId.length()<=6){
			return curChoiceAccPackageId;
		}else{
			return curChoiceAccPackageId.substring(0,6);
		}
	}

	/**
	 * @return the userIsTips
	 */
	public String getUserIsTips() {
		return userIsTips;
	}

	/**
	 * @param userIsTips the userIsTips to set
	 */
	public void setUserIsTips(String userIsTips) {
		this.userIsTips = userIsTips;
	}

	public String getUserAuditOfficePopedom() {
		return userAuditOfficePopedom;
	}

	public void setUserAuditOfficePopedom(String userAuditOfficePopedom) {
		this.userAuditOfficePopedom = userAuditOfficePopedom;
	}

	/**
	 * @return the userPopedom
	 */
	public String getUserPopedom() {
		return userPopedom;
	}

	/**
	 * @param userPopedom the userPopedom to set
	 */
	public void setUserPopedom(String userPopedom) {
		this.userPopedom = userPopedom;
	}

	/**
	 * @return the userPageSize
	 */
	public String getUserPageSize() {
		return userPageSize;
	}

	/**
	 * @param userPageSize the userPageSize to set
	 */
	public void setUserPageSize(String userPageSize) {
		this.userPageSize = userPageSize;
	}

	/**
	 * @return the curAccCurrencyName
	 */
	public String getCurAccCurrencyName() {
		return curAccCurrencyName;
	}

	/**
	 * @param curAccCurrencyName the curAccCurrencyName to set
	 */
	public void setCurAccCurrencyName(String curAccCurrencyName) {
		this.curAccCurrencyName = curAccCurrencyName;
	}

	/**
	 * @return the curChoiceCurrencyName
	 */
	public String getCurChoiceCurrencyName() {
		return curChoiceCurrencyName;
	}

	/**
	 * @param curChoiceCurrencyName the curChoiceCurrencyName to set
	 */
	public void setCurChoiceCurrencyName(String curChoiceCurrencyName) {
		this.curChoiceCurrencyName = curChoiceCurrencyName;
	}

	/**
	 * @return the curProjectProperty
	 */
	public String getCurProjectProperty() {
		return curProjectProperty;
	}

	/**
	 * @param curProjectProperty the curProjectProperty to set
	 */
	public void setCurProjectProperty(String curProjectProperty) {
		this.curProjectProperty = curProjectProperty;
	}

	/**
	 * @return the curCustomerStandByName
	 */
	public String getCurCustomerStandByName() {
		return curCustomerStandByName;
	}

	/**
	 * @param curCustomerStandByName the curCustomerStandByName to set
	 */
	public void setCurCustomerStandByName(String curCustomerStandByName) {
		this.curCustomerStandByName = curCustomerStandByName;
	}

	/**
	 * @return the curProjectManuscriptCustomerName
	 */
	public String getCurProjectManuscriptCustomerName() {
		return curProjectManuscriptCustomerName;
	}

	/**
	 * @param curProjectManuscriptCustomerName the curProjectManuscriptCustomerName to set
	 */
	public void setCurProjectManuscriptCustomerName(
			String curProjectManuscriptCustomerName) {
		this.curProjectManuscriptCustomerName = curProjectManuscriptCustomerName;
	}

	public String getUserAuditOfficeId() {
		return userAuditOfficeId;
	}

	public void setUserAuditOfficeId(String userAuditOfficeId) {
		this.userAuditOfficeId = userAuditOfficeId;
	}

	public Set getUserCurTasks() {
		return userCurTasks;
	}

	public void setUserCurTasks(Set userCurTasks) {
		this.userCurTasks = userCurTasks;
	}

	public int getCurProjectSystemId() {
		return curProjectSystemId;
	}

	public void setCurProjectSystemId(int curProjectSystemId) {
		this.curProjectSystemId = curProjectSystemId;
	}

	public String getCurAuditType() {
		return curAuditType;
	}

	public void setCurAuditType(String curAuditType) {
		this.curAuditType = curAuditType;
	}

	public String getUserAuditOfficeStandbyName() {
		return userAuditOfficeStandbyName;
	}

	public void setUserAuditOfficeStandbyName(String userAuditOfficeStandbyName) {
		this.userAuditOfficeStandbyName = userAuditOfficeStandbyName;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public String getUserAuditOfficeNameByEOA() {
		return userAuditOfficeNameByEOA;
	}

	public void setUserAuditOfficeNameByEOA(String userAuditOfficeNameByEOA) {
		this.userAuditOfficeNameByEOA = userAuditOfficeNameByEOA;
	}

	/**
	 * @return userAuditOfficeIdByEOA
	 */
	public String getUserAuditOfficeIdByEOA() {
		return userAuditOfficeIdByEOA;
	}

	/**
	 * @param userAuditOfficeIdByEOA 要设置的 userAuditOfficeIdByEOA
	 */
	public void setUserAuditOfficeIdByEOA(String userAuditOfficeIdByEOA) {
		this.userAuditOfficeIdByEOA = userAuditOfficeIdByEOA;
	}

	/**
	 * @return userAuditCardIdByEOA
	 */
	public String getUserAuditCardIdByEOA() {
		return userAuditCardIdByEOA;
	}

	/**
	 * @param userAuditCardIdByEOA 要设置的 userAuditCardIdByEOA
	 */
	public void setUserAuditCardIdByEOA(String userAuditCardIdByEOA) {
		this.userAuditCardIdByEOA = userAuditCardIdByEOA;
	}

	public String getUserWorkPath() {
		return userWorkPath;
	}

	public void setUserWorkPath(String userWorkPath) {
		this.userWorkPath = userWorkPath;
	}

	public String getCurProjectPostil() {
		return curProjectPostil;
	}

	public void setCurProjectPostil(String curProjectPostil) {
		this.curProjectPostil = curProjectPostil;
	}

	public String getProjectPopedomDepartmentids() {
		return ProjectPopedomDepartmentids;
	}

	public void setProjectPopedomDepartmentids(String projectPopedomDepartmentids) {
		ProjectPopedomDepartmentids = projectPopedomDepartmentids;
	}

	public boolean isBInOcx() {
		return bInOcx;
	}

	public void setBInOcx(boolean inOcx) {
		bInOcx = inOcx;
	}

	public String getUserMobilePhone() {
		return userMobilePhone;
	}

	public void setUserMobilePhone(String userMobilePhone) {
		this.userMobilePhone = userMobilePhone;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public String getUserFloor() {
		return userFloor;
	}

	public void setUserFloor(String userFloor) {
		this.userFloor = userFloor;
	}

	public String getUserAddress() {
		return userAddress;
	}

	public void setUserAddress(String userAddress) {
		this.userAddress = userAddress;
	}

	public String getUserRank() {
		return userRank;
	}

	public void setUserRank(String userRank) {
		this.userRank = userRank;
	}

	public String getUserAuditOrganId() {
		return userAuditOrganId;
	}

	public void setUserAuditOrganId(String userAuditOrganId) {
		this.userAuditOrganId = userAuditOrganId;
	}

	public String getUserAuditOrganName() {
		return userAuditOrganName;
	}

	public void setUserAuditOrganName(String userAuditOrganName) {
		this.userAuditOrganName = userAuditOrganName;
	}

	public String getAreaid() {
		return areaid;
	}

	public void setAreaid(String areaid) {
		this.areaid = areaid;
	}

	public String getAreaname() {
		return areaname;
	}

	public void setAreaname(String areaname) {
		this.areaname = areaname;
	}

	public String getUserSex() {
		return userSex;
	}

	public void setUserSex(String userSex) {
		this.userSex = userSex;
	}

	public String getUserBornDate() {
		return userBornDate;
	}

	public void setUserBornDate(String userBornDate) {
		this.userBornDate = userBornDate;
	}

	public String getUserEntrytime() {
		return userEntrytime;
	}

	public void setUserEntrytime(String userEntrytime) {
		this.userEntrytime = userEntrytime;
	}

	public String getUserProfession() {
		return userProfession;
	}

	public void setUserProfession(String userProfession) {
		this.userProfession = userProfession;
	}

	public String getUserDegree() {
		return userDegree;
	}

	public void setUserDegree(String userDegree) {
		this.userDegree = userDegree;
	}

	public String getUserEducation() {
		return userEducation;
	}

	public void setUserEducation(String userEducation) {
		this.userEducation = userEducation;
	}

	public String getUserWrok_time() {
		return userWrok_time;
	}

	public void setUserWrok_time(String userWrok_time) {
		this.userWrok_time = userWrok_time;
	}

	public boolean isbInOcx() {
		return bInOcx;
	}

	public void setbInOcx(boolean bInOcx) {
		this.bInOcx = bInOcx;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getFirstSignDate() {
		return firstSignDate;
	}

	public void setFirstSignDate(String firstSignDate) {
		this.firstSignDate = firstSignDate;
	}

	public String getLastContractLimit() {
		return lastContractLimit;
	}

	public void setLastContractLimit(String lastContractLimit) {
		this.lastContractLimit = lastContractLimit;
	}

	public String getContractEnd() {
		return contractEnd;
	}

	public void setContractEnd(String contractEnd) {
		this.contractEnd = contractEnd;
	}

	public String getIdentitycard() {
		return identitycard;
	}

	public void setIdentitycard(String identitycard) {
		this.identitycard = identitycard;
	}
	
	
//	
}
