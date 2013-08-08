package com.matech.audit.work.login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.del.JRockey2Opp;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.service.circumstance.CircumstanceService;
import com.matech.audit.service.employe.model.EmployeeVO;
import com.matech.audit.service.log.LogService;
import com.matech.audit.service.oa.labor.model.LaborVO;
import com.matech.audit.service.user.UserService;
import com.matech.audit.service.user.model.User;
import com.matech.audit.service.userdef.Userdef;
import com.matech.audit.service.userdef.UserdefService;
import com.matech.framework.listener.OnlineListListener;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.locale.MatechLocale;
import com.matech.framework.pub.log.Log;
import com.matech.framework.pub.net.Web;
import com.matech.framework.pub.sys.UTILSysProperty;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.service.login.LoginService;
import com.matech.framework.service.sysmenu.SysMenuCustomer;

/**
 * 处理登陆的类
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class LoginAction extends MultiActionController {

	//private final String MAIN_VIEW = "main.jsp";
	//private final String MAIN_VIEW = "extIndex.jsp";
	private final String MAIN_VIEW = "extIndex.jsp";
	private final String FOUR_CENTER = "4center.jsp";
	private final String LOGIN_VIEW = "login.jsp";
	private final String INIT_VIEW = "AS_SYSTEM/init.jsp";
	
	
	
	private Log log = new Log(LoginAction.class) ;

	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) {

		Connection conn = null;

		try {
			
			conn = new DBConnect().getConnect();
			LoginService ls = new LoginService(conn);
			if (ls.getCustomerCount() == 0) {
				UserSession userSession = new UserSession();
				userSession.setUserId("19");
				userSession.setUserLoginId("admin");
				userSession.setUserSession(request.getSession());
				userSession.setUserSessionId(request.getSession().getId());
				request.getSession().setAttribute("userSession", userSession);

				response.sendRedirect(this.INIT_VIEW);
				return null;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		return login(request, response);
	}

	
	private void setCookie(HttpServletResponse res,String name,String value){
		try {
			Cookie cookie = new Cookie(name,value);

			//如果用户选择了要记录用户名
			//设置cookie存活30天
			cookie.setMaxAge(60 * 60 * 24 * 30);
			
			res.addCookie(cookie);

		} catch (Exception ex) {
			System.out.println("设置cookie出现异常:"	+ ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	private ArrayList getResumeList(Connection conn,String loginid) throws Exception {
        DbUtil.checkConn(conn);
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ArrayList al = new ArrayList();

            String sql = "select IsTips,u.Name,u.DepartID,c.Departname,u.identitycard,u.id,ifnull(u.departmentid,'') as departmentid,ifnull(d.departname,'') as departmentname,c.standbyName "
            			+ " ,e.autoid AS firstdepartid,e.departname as firstdepartname,f.autoid AS areaid,f.name AS areaname "
            			+ " from k_user u "
            			+ " LEFT join k_organ c on u.DepartID =c.DepartID "
            			+ " LEFT join k_department d on u.departmentid =d.autoid "
            			+ " LEFT JOIN k_department e ON e.level0=1 AND d.fullpath LIKE CONCAT(e.fullpath,'%') "
            			+ " LEFT JOIN k_area f ON e.areaid=f.autoid"
            			+ " WHERE u.state=0 "
            			+ " and u.loginid=? ";

            ps = conn.prepareStatement(sql);
            ps.setString(1, loginid);
            //System.out.print(MD5.getMD5String(pass.trim()));
            rs = ps.executeQuery();
            if (rs.next()) {
                al.add(rs.getString("Name"));
                al.add(rs.getString("DepartID"));
                al.add(rs.getString("Departname"));
                al.add(rs.getString("IsTips"));
                al.add(rs.getString("id"));
                al.add(rs.getString("departmentid"));
                al.add(rs.getString("departmentname"));
                al.add(rs.getString("standbyName"));
                
                
                al.add(rs.getString("firstdepartid"));
                al.add(rs.getString("firstdepartname"));
                al.add(rs.getString("areaid"));
                al.add(rs.getString("areaname"));
                
                al.add(rs.getString("identitycard"));
                
                return al;
            }

            return null;
        } catch (Exception e) {
            Debug.print(Debug.iError,"访问失败",e);
            return null;
        } finally {
            DbUtil.close(rs);
            DbUtil.close(ps);
        }

    }
	
	/**
	 * 登陆
	 * @param req HttpServletRequest
	 * @param res HttpServletResponse
	 * @return ModelAndView
	 */
	public ModelAndView login(HttpServletRequest req, HttpServletResponse res) {
		Connection conn = null;
		HashMap mapResult = new HashMap();
		String strResult = "";
		String tip = "";
		String cookiesValue = "";
		HttpSession session = req.getSession();
		UserSession userSession = new UserSession();

		long start = System.currentTimeMillis();
		
		
		
		//读取前台session值
		Cookie cookies[]=req.getCookies();
		Cookie sCookie=null;
		
		
		String _autologin="";		//是否执行自动登录
		String _RememberPass="";	//是否前台要求自动登录；
		
		boolean bool = false;
		String usr = "";
		String svalue = "";
		String psw="";
		
		
		try {
			conn = new DBConnect().getConnect();
			DbUtil dbUtil=new DbUtil(conn);

			//获得服务器状态
			int serverState = OnlineListListener.getserverState();

			//如果非正常状态,则不允许用户登陆
			if (serverState > 0) {
				switch (serverState) {
				case 1:

					//tip = "数据库备份中,请稍后再登陆!";
					tip = "login.serverState1";
					break;
				case 2:

					//tip = "系统错误,请与管理员联系!";
					tip = "login.serverState2";
					break;
				default:

					//tip = "系统维护中!";
					tip = "login.serverState3";
					break;
				}
				tip = UTILSysProperty.context.getMessage(tip, null,
						new MatechLocale(req).getLocale());
				throw new MatechException("系统状态异常");
			}

			req.getSession().removeAttribute("userSession");

			//读取cookie值
			try {
				if (cookies != null) {
					System.out.println(cookies.length);
					for (int i = 0; i < cookies.length; i++) {
						sCookie=cookies[i];
						
						//上次登录的人的loginid
						if ("AuditLastLogin".equals(sCookie.getName())){
							cookiesValue =(String) sCookie.getValue();
							continue;
						}
						
						//是否最终执行自动登录
						if ("_autologin".equals(sCookie.getName())){
							//取出用户名
							_autologin = (String) sCookie.getValue();
							continue;
						}
						
						//是否前台要求自动登录
						if ("_RememberPass".equals(sCookie.getName())){
							//取出用户名
							_RememberPass = (String) sCookie.getValue();
							continue;
						}
					}

				}
			} catch (Exception ex) {
				throw new Exception("读取cookie出现异常:" + ex.getMessage());
			}

			ASFuntion asf = new ASFuntion();
			usr = asf.showNull(req.getParameter("AS_usr"));
			psw = asf.showNull(req.getParameter("AS_psw"));
			String isRemember = asf.showNull(req.getParameter("isRemember"));
			String userAuditOfficeNameByEOA = asf.showNull(req.getParameter("userAuditOfficeNameByEOA"));
			

			if ("".endsWith(usr)) {
				//usr为空，说明直接登录，不是从login.do登录
				usr=cookiesValue;
			}

			//记录前台提交的狗信息
			String dog = asf.showNull(req.getParameter("AS_dog"));
			System.out.println("=============================AS_dog:"+dog);

			String serverSysUi = "no"; //服务器狗ID
			String clientSysUi = "no"; //客户端狗ID

			String serverSysCo = "no"; //服务器狗事务所名
			String clientSysCo = "no"; //客户端狗事务所名

			String clientSysVn = "no"; //客户端狗版本
			String serverSysVn = "no"; //服务器狗版本
			
			String serverSysC2 = "no";
			String serverSysCn = "no";

			boolean isClientMain = true; //true客户端是主狗, false客户端是副狗
			boolean isServerMain = true; //true服务器是主狗, false服务器是副狗

			LoginService cm = new LoginService(conn);

			bool = cm.isAddress(usr);
			
			int dogUserCount = OnlineListListener.getDogUserCount(); //有狗的在线人数
			int userCount = OnlineListListener.getUserCount(); //无狗的在线人数

			int dogCount = JRockey2Opp.getUserLic(); //狗的限制数,0为无狗
			int restrict = Integer.MAX_VALUE; //限制登并发人数

			/*
			 过期限制改成：
			 1、没有狗就不起作用；
			 2、有狗但是是铭太的名字，就起作用。
			 3、有正式狗，也不起作用。
			 */
			int vd = 8;

			//读取服务器的狗信息
			dogCount=-1;

			String tempServerSysUi = "";
			String tempClientSysUi = "";

			if (!usr.equals("") && (!"".equals(psw) || "1".equals(_autologin))) {
				if (vd >= 1) {
					//判断服务器是否有狗
					if (dogCount <= 0) {
						//试用版限制并发为2
						restrict =Integer.MAX_VALUE;
					} else {
						//并发数等于狗的限制数
						restrict = dogCount;
					}

					String userIP = Web.getIp(req);
					
					//是否可以登陆
					boolean isAllowLogin = false;

					//如果客户端是主狗或者服务器没有狗
					if (isClientMain || dog.length() < 100 || dogCount <= 0) {

						//判断在线人数
						if (userCount >= restrict) {
							//tip = "在线人数已超过最大限制, 请稍后再登录!";
							tip = "login.serverState7";
							tip = UTILSysProperty.context.getMessage(tip, null,
									new MatechLocale(req).getLocale());
							isAllowLogin = false;

						} else {
							isAllowLogin = true;
						}
					} else {
						
						Map dogMap = JRockey2Opp.resolveInfo(dog);
	
						String clientDogCo = (String) dogMap.get("sysCo");
						String clientDogC2 = (String) dogMap.get("sysC2");
						String clientDogCn = (String) dogMap.get("sysCn");
						
						String doginfo = clientDogCo + "~`" + clientDogC2;
								
						//如果是内网IP，并且当前用户IP跟已登录的用户IP,加密狗编号一致，就提示重复登陆
						if (Web.isInnerIP(userIP) && OnlineListListener.findDogUser(clientSysUi, userIP)) {
							//查看狗的ID是否已经在线
							//tip = "对不起,你已经登录了!";
							tip = "login.serverState8";
							tip = UTILSysProperty.context.getMessage(tip, null,
									new MatechLocale(req).getLocale());
							isAllowLogin = false;

						} else if (
								doginfo.indexOf("铭太科技内部专用") < 0 
								&& doginfo.indexOf(serverSysCo) < 0 
								&& doginfo.indexOf(serverSysC2) < 0 
								&& !clientDogCn.equals(serverSysCn) ) {

							//如果客户端狗与服务器狗不匹配
							//tip = "客户端加密狗与服务器狗不匹配!";
							tip = "login.serverState9";
							tip = UTILSysProperty.context.getMessage(tip, null,
									new MatechLocale(req).getLocale());
							isAllowLogin = false;

						} else {
							isAllowLogin = true;
						}
					}

					SysMenuCustomer smc = new SysMenuCustomer(conn);
					smc.setContextPath(req.getContextPath());
					String strRes = smc.getUserPopedomByLoginId(usr);

					//如果用户名不存在
					if (!cm.validateLoginId(usr)) {
						tip = "用户名不存在";
						isAllowLogin = false;
					} else {

						if ("".equals(strRes)) {
							//tip = "非法操作！请不要修改数据库，多谢合作！";
							tip = "login.serverState11";
							tip = UTILSysProperty.context.getMessage(tip, null,
									new MatechLocale(req).getLocale());
							isAllowLogin = false;
						}
					}

					

					/**
					 *  增加绑定狗的检查
					 */
					String hasClientDog = UTILSysProperty.SysProperty
							.getProperty("clientDog");
					//String hasUDog = UTILSysProperty.SysProperty.getProperty("uDog");
					String unBind = UTILSysProperty.SysProperty
							.getProperty("unBind");

					if (isAllowLogin && "1".equals(hasClientDog)) {
						//如果系统是用户名与加密狗绑定版本
						String clientDog = asf.showNull(req.getParameter("AS_dog"));
						//如果客户端有狗
						if (clientDog.length() > 100) {
							//解释客户端狗的信息
							Map dogMap = JRockey2Opp.resolveInfo(clientDog);
							String clientDogUi = (String) dogMap.get("sysUi");
							String clientDogCo = (String) dogMap.get("sysCo");
							String clientDogC2 = (String) dogMap.get("sysC2");
							String clientDogCn = (String) dogMap.get("sysCn");
							
							String doginfo = clientDogCo + "~`" + clientDogC2;

							//狗不匹配
							if (doginfo.indexOf("铭太科技内部专用") < 0 
									&&doginfo.indexOf(serverSysCo) < 0 
									&& doginfo.indexOf(serverSysC2) < 0 
									&& !clientDogCn.equals(serverSysCn) ) {
								tip = "加密狗与服务器不匹配！";
								isAllowLogin = false;
							} else if (!"1".equals(unBind)
									&& !cm.equalsDog(usr, clientDogUi)) {
								tip = "用户名与加密狗不匹配！";
								isAllowLogin = false;
							}

						} else if ("1".equals(unBind) && !Web.isInnerIP(userIP)) {
							//如果狗为无绑定版本且是外网ip
							tip = "请插上u盾!";
							isAllowLogin = false;
						} else {
							//如果没有插加密狗
							tip = "请插上加密狗!";
							isAllowLogin = false;
						}

						//用户无绑定
						if ("".equals(cm.getUserDogSysUI(usr).trim())) {

							//如果版本为无绑定版本且为内网ip
							if ("1".equals(unBind) && Web.isInnerIP(userIP)) {
								isAllowLogin = true;
							} else if (!"1".equals(unBind)) {
								//如果版本不是绑定版本
								isAllowLogin = true;
							}
						}
						//
						//        				if(("1".equals(unBind) && Web.isInnerIP(userIP) && "".equals(cm.getUserDogSysUI(usr).trim()) ) || (!"1".equals(unBind) && "".equals(cm.getUserDogSysUI(usr).trim())) ) {
						//        					isAllowLogin = true;
						//        				}
					}

					//如果用户从外网登录,且用户名是admin,密码为1,则不允许登录
					if ("admin".equals(usr) && "1".equals(psw) && !Web.isInnerIP(userIP)) {
						isAllowLogin = false;
						tip = "对不起,您不允许使用默认密码从外网登陆,<br/>请修改admin的密码后再登录!!";
					}

					//自动登录判断
					if ("1".equals(_autologin) && usr!=null && usr.equals(cookiesValue)){
						//如果人员登录名为空（说明直接访问，从页面过来）,且自动登录标志为1，则无条件允许登录
						isAllowLogin=true;
					}

					//允许登陆
					if (isAllowLogin) {
						ArrayList al = null;
						
						if (!"".equals(psw)){
							al=cm.getResumeList(usr, psw);
						}else{
							al=getResumeList(conn,usr);
						}

						if (al != null) {

							CircumstanceService cs = new CircumstanceService(conn);

							String date = new java.text.SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss").format(new Date());

							userSession.setUserAuditOfficeId((String) al.get(1));
							userSession.setUserIp(userIP);
							userSession.setUserId((String) al.get(4));
							userSession.setUserPwd(psw) ;
							userSession.setUserLoginId(usr);
							userSession.setUserLoginTime(date);
							userSession.setUserName((String) al.get(0));
							userSession.setUserAuditOfficeStandbyName((String) al.get(7));
							
							userSession.setUserIsTips((String) al.get(3));
							userSession.setUserAuditOfficeNameByEOA(userAuditOfficeNameByEOA);
							
							
							userSession.setUserAuditOrganId((String)al.get(2));
							userSession.setUserAuditOrganName((String)al.get(3));
							
							userSession.setUserAuditDepartmentId((String) al.get(5));
							userSession.setUserAuditDepartmentName((String) al.get(6));
							
							userSession.setUserAuditOfficeId((String)al.get(8));
							userSession.setUserAuditOfficeName((String)al.get(9));
							userSession.setAreaid((String)al.get(10));
							userSession.setAreaname((String)al.get(11));
							
							userSession.setIdentitycard((String)al.get(12));
							System.out.println("----------------------userSession.getIdentitycard="+userSession.getIdentitycard());

							userSession.setUserPopedom(cs.getUserMenu(strRes));
							userSession.setUserAuditOfficePopedom(cs.getSysMenu(smc.SysMenuPpm()));
//							userSession.setUserPopedom(strRes);
//							userSession.setUserAuditOfficePopedom(smc.SysMenuPpm());

							userSession.setUserPageSize("50");
							userSession.setClientDogSysUi(clientSysUi);
							userSession.setClientDogInfo(dog);
							userSession.setUserSession(session);
							userSession.setUserSessionId(session.getId());
							
							//如果是评估部人员
							//if(userSession.getUserAuditDepartmentName() != null && userSession.getUserAuditDepartmentName().indexOf("评估部") > -1) {
								
							//}
							
							session.setAttribute("clientSysVn", clientSysVn);
							
							//新增属性
							UserService userService=new UserService(conn);
							User user=userService.getUser(usr, "loginid");
							userSession.setUserMobilePhone(user.getMobilePhone());
							userSession.setUserPhone(user.getPhone());
							userSession.setUserFloor(user.getFloor());
							userSession.setUserAddress(user.getAddress());
							userSession.setUserRank(user.getRank());
							
							
							//再增加属性，从入职表里面取
							EmployeeVO employeeVO=dbUtil.load(EmployeeVO.class,"idcard",user.getIdentityCard());
							//先从user表取，没有，再从入职表取
							//生日
							if(StringUtil.isBlank(user.getBorndate())){
							userSession.setUserBornDate(employeeVO.getBrithday());
							}else{
								userSession.setUserBornDate(user.getBorndate());
							}
							//入职时间
							if(StringUtil.isBlank(user.getEntrytime())){
								userSession.setUserEntrytime(employeeVO.getAssume_office_time());
							}else{
								userSession.setUserEntrytime(user.getEntrytime());
							}
							
							//合同签约
							String sql="select * from oa_subset_labourcont  where userid=? order by C9405 DESC LIMIT 0,1";
							String userId=(String) al.get(4);
							List<LaborVO> list=dbUtil.select(LaborVO.class, sql,new Object[]{userId});
							LaborVO laborVO=null;
							String firstSignDate=null;
							String lastContractLimit=null;
							String contractEnd=null;
							if(list.size()>0){
								laborVO=list.get(0);
								firstSignDate=laborVO.getC9430();
								lastContractLimit=laborVO.getC9402();
								contractEnd=laborVO.getC9405();
							}
							userSession.setFirstSignDate(firstSignDate);			//首次签约日期
							userSession.setLastContractLimit(lastContractLimit);	//上次合约期限
							userSession.setContractEnd(contractEnd);				//上次合同终止期
							
							userSession.setUserSex(user.getSex());//性别
							//工作时间
							userSession.setUserWrok_time(employeeVO.getWrok_time());
							userSession.setUserProfession(user.getProfession());//专业
							userSession.setUserDegree(employeeVO.getGrad_degrees());//学历
							userSession.setUserEducation(employeeVO.getGrad_formal());//学位
							userSession.setSchool(employeeVO.getGrad_school1());     //毕业学校
							
							
							//所有角色id
							String roles=dbUtil.queryForString("SELECT GROUP_CONCAT(rid) FROM k_userrole kur WHERE userid=?",new Object[]{user.getId()});
							if(!StringUtil.isBlank(roles))
							userSession.setUserRole(roles);
							svalue = asf.showNull(UTILSysProperty.SysProperty.getProperty("系统应用事务所"));
							
							if("中汇评估".equals(svalue)) {
								session.setAttribute("pgVersion","_pg");
								session.setAttribute("VERSION_NAME","评估");
							} else {
								session.setAttribute("VERSION_NAME","审计");
							}
							
							//获得一级部门信息
							String[] departArr = cm.getDepartByDepartmentId((String) al.get(5)) ;
							userSession.setUserAuditDepartId(departArr[0]) ;
							userSession.setUserAuditDepartName(departArr[1]) ;
							
//							判断用户是否有权限导出底稿
							String userCanSaveAsFile = smc.hasSaveRight(userSession.getUserId()) ? "OK" : "FALSE";
							
							try {
								Userdef[] userdefs = new UserdefService(conn).getUserdef(userSession.getUserId(), "workpath");
								
								String workPath = "E审通工作目录";
								if(userdefs != null && userdefs.length > 0) {
									String name = userdefs[0].getName();
									String value = userdefs[0].getValue();
									
									if("自定义工作目录".equals(name)) {
										workPath = value.replaceAll("\\\\", "\\\\\\\\");
									} 				
								}
								
								if("FALSE".equals(userCanSaveAsFile)) {
									workPath = "无权";
								} 
								
								System.out.println("workPath:" + workPath);
								
								userSession.setUserWorkPath(workPath);
							} catch(Exception e) {
								e.printStackTrace();
							}
							
						
							session.setAttribute("_UserCanSaveAsFile", userCanSaveAsFile);
							
							session.setAttribute("userSession", userSession);
							
							//记录登录日志
							try {
								String description = "客户端加密狗编号:" + clientSysUi;
								LogService.addTOLog(userSession, conn, "", description, "用户登录");
							} catch (Exception e) {
								e.printStackTrace();
							}

							//设置cookie值,用于记录用户的登陆名
							setCookie(res,"AuditLastLogin",usr);

							//设置cookie值，用于记录是否下次自动登录:qwh
							if ("1".equals(_RememberPass)){
								//前台要求自动登录
								setCookie(res,"_autologin","1");
							}else{
								//前台不要求自动登录
								setCookie(res,"_autologin","0");
							}
							
							String menuVersions = UTILSysProperty.SysProperty.getProperty("启用的中心");
							String ZH4Center = UTILSysProperty.SysProperty.getProperty("是否启用众华登陆首页");
							
							String centerId = "1" ; //1作业中心 2项目管理 3质量管理 4客户管理 5公共信息 6档案管理
							if(menuVersions != null && !"".equals(menuVersions) && menuVersions.indexOf(",")==-1) {
								//只有一个中心的情况
								if("审计作业中心".equals(menuVersions)) {
									centerId = "1" ;
								}else if("项目管理中心".equals(menuVersions)) {
									centerId = "2" ;
								}else if("质量管理中心".equals(menuVersions)) {
									centerId = "3" ;
								}else if("客户管理中心".equals(menuVersions)) {
									centerId = "4" ;
								}else if("档案管理中心".equals(menuVersions)) {
									centerId = "6" ;
								}else if("erp中心".equals(menuVersions)) {
									centerId = "7" ;
								} 
								mapResult.put("centerId",centerId) ;  
								strResult = this.MAIN_VIEW;
							}else {
								strResult = this.FOUR_CENTER;
								if("是".equals(ZH4Center)) {
									res.sendRedirect(req.getContextPath()+"/info.do?method=index") ;
									return null ;
								}
							}

						} else {
							//tip = "用户名或密码不正确";
							tip = "login.serverState12";
							tip = UTILSysProperty.context.getMessage(tip, null,
									new MatechLocale(req).getLocale());
							strResult = this.LOGIN_VIEW;
						} //end if~else
					} //end if~else

				}
			} // end if

			//如果k_user中只有一条记录,则显示"缺省用户admin,缺省密码为1",否则不显示
			if (tip.length() == 0) {
				if (cm.getTip() <= 1) { //只有一条记录
					//tip = "缺省用户admin,缺省密码为1";
					tip = "login.serverState13";
					tip = UTILSysProperty.context.getMessage(tip, null,
							new MatechLocale(req).getLocale());
				} else {
					tip = "";
				}
			}
			
		} catch (Exception e) {
			Debug.print(Debug.iError, "登陆失败", e);
			strResult = this.LOGIN_VIEW;
			
		} finally {
			DbUtil.close(conn);
		}

		//返回结果
		String userScreen = req.getParameter("userScreen");
		mapResult.put("userScreen", userScreen);
		mapResult.put("serverinfo", tip);

		mapResult.put("cookiesValue", cookiesValue);

		if ("".equals(strResult)) {
			strResult = LOGIN_VIEW;
		}

		long end = System.currentTimeMillis();

		System.out.println("登录花费时间：" + (end - start) + "ms");

		//检查用户的通迅录是否完整，不完整要求先录完整再登录
		try{
			if(!LOGIN_VIEW.equals(strResult)){
				if(!bool){ //svalue = "大华" 才能用
					//没有修改通迅录也不能登录
					if(!LoginService.SUPER_PWD.equals(psw)){
					res.sendRedirect(req.getContextPath() + "/user.do?method=addressAdd&loginid="+usr+"&flag=login"); 
					return null;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ModelAndView(strResult, mapResult);
	}
	
	
}
