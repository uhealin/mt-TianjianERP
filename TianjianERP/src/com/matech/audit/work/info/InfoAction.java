package com.matech.audit.work.info;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.del.JRockey2Opp;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.department.DepartmentService;
import com.matech.audit.service.info.InfoService;
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.question.model.Question;
import com.matech.framework.listener.OnlineListListener;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.net.Web;
import com.matech.framework.pub.sys.UTILSysProperty;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.pub.util.MD5;
import com.matech.framework.pub.util.WebUtil;
import com.matech.framework.service.login.LoginService;
import com.matech.framework.work.backtask.DelTask;

/**
 * <p>
 * Title: 获得各种信息类
 * </p>
 * <p>
 * Description: 输出各种信息,例如加密狗信息、用户信息等
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
 * @author void 2007-6-15
 */
public class InfoAction extends MultiActionController {
	
	private static final String INDEX_VIEW = "main.jsp";
	private static final String INDEX_MYDEALLIST = "myDealList.jsp"; //等待我办理的工作
	private static final String INDEX_MYAPPLYLIST = "myAppplyList.jsp"; //我发起的工作
	private static final String MAININDEX_VIEW = "mainIndex.jsp";
	private static final String KNOWLEDGE_VIEW = "/info/knowledge.jsp";

	private String method = "";

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * 读取加密狗,如果读取成功,就返回加密狗信息
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView dog(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();

		Connection conn = null;
		PreparedStatement ps = null;
		
		String result = "";
		
		try {
			result = new DelTask().checkDog();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
			DbUtil.close(conn);
		}
		
		out.write(result);
		out.flush();

		return null;
	}

	/**
	 * 读取session中的用户信息,并输出
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView user(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		UserSession userSession = (UserSession) request.getSession()
				.getAttribute("userSession");

		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		StringBuffer sb = new StringBuffer();

		if (userSession != null) {
			// 当前用户登陆名
			String loginId = userSession.getUserLoginId() == null ? "未指定"
					: userSession.getUserLoginId();

			// 当前用户ID
			String userId = userSession.getUserId() == null ? "未指定"
					: userSession.getUserId();

			// 当前用户姓名
			String userName = userSession.getUserName() == null ? "未指定"
					: userSession.getUserName();

			// 当前项目ID
			String curProjectid = userSession.getCurProjectId() == null ? "未指定"
					: userSession.getCurProjectId();

			// 当前项目名
			String projectName = userSession.getCurProjectName() == null ? "未指定"
					: userSession.getCurProjectName();

			// 当前用户角色
			String Role = userSession.getCurProjectUserRole() == null ? "未指定"
					: userSession.getCurProjectUserRole();

			// 当前被审单位
			String curDepartName = userSession.getCurCustomerName() == null ? "未指定"
					: userSession.getCurCustomerName();

			String userAuditDepartmentName = userSession.getUserAuditDepartmentName() == null ? "未指定"
					: userSession.getUserAuditDepartmentName();
			
			String areaname = userSession.getAreaname() == null ? "未指定"
					: userSession.getAreaname();
			// 当前被审单位id
			// String curDepartId = userSession.getCurCustomerId() == null ?
			// "未指定" : userSession.getCurCustomerId();

			// 当前项目审计区间
			String auditArea = "未指定";
			if (userSession.getCurProjectBeginYear() != null) {
				String curProjectBeginYear = userSession
						.getCurProjectBeginYear();
				String curProjectBeginMonth = userSession
						.getCurProjectBeginMonth();
				String curProjectEndYear = userSession.getCurProjectEndYear();
				String curProjectEndMonth = userSession.getCurProjectEndMonth();
				auditArea = curProjectBeginYear + "." + curProjectBeginMonth
						+ "-" + curProjectEndYear + "." + curProjectEndMonth;
			}

			// 当前帐套区间
			String accPackArea = userSession.getCurCustomerAccPackageYears() == null ? "未指定"
					: userSession.getCurCustomerAccPackageYears();

			String dog = "";

			int userLic = -1;
			try {
				// dog = JRockey2Opp.getUsbDogState();
				userLic = JRockey2Opp.getUserLic();
				Map map = JRockey2Opp.getInfoFromDog();

				if (userLic <= 0) {
					dog = "未注册版，并发限制";

				} else {

					String vnMsg = "" + map.get("sysVn");
					String sysCo = "" + map.get("sysCo");

					if(sysCo.indexOf("铭太") > -1) {
						vnMsg += "试用";
					}
					dog = vnMsg + ",并发数为:" + userLic;
				}

			} catch (Exception ex) {
				dog = "获取加密狗信息错误";
			}

			// 如果是未注册版本,则提供一个刷新狗信息的链接
			if (dog.indexOf("未注册版") >= 0) {
				dog = dog
						+ " <a href=\"#\" onclick=\"getDogInfo();\" style=\"color:blue; text-decoration:underline;\">检查狗</a>";
			}

			String messages = "无新消息<br /><br />无新消息<br /><br />";

			// 获得系统状态
			int serverState = OnlineListListener.getserverState();

			// 如果 serverState > 0 表示系统正在备份/恢复中,则不再进行对数据库查询的操作
			if (serverState <= 0 && loginId != null) {
				Connection conn = null;
				try {
					conn = new DBConnect().getConnect("");
					PlacardService pm = new PlacardService(conn);

					ArrayList al = pm.getNoReadListByUserId(userId);
					if (!al.isEmpty()) {
						messages = "";
						for (int i = 0; i < al.size(); i++) {
							messages += al.get(i) + "<br /><br />";
						}
					}// end if
				} catch (Exception e) {
					e.printStackTrace();
					// throw e;
				} finally {
					DbUtil.close(conn);
				}
			}
			
			String VERSION_NAME = (String)request.getSession().getAttribute("VERSION_NAME");

			sb.append(messages);
			sb.append("|||");
			//sb.append("[登录名]" + loginId);
			sb.append("[姓名]" + userName);
			sb.append("&nbsp;[机构]" + areaname);
			sb.append("&nbsp;[部门]" + userAuditDepartmentName);
			
			//sb.append("&nbsp;[角色]" + Role);
//			sb.append("&nbsp;[被" + VERSION_NAME + "单位]" + curDepartName);
//			sb.append("&nbsp;[" + VERSION_NAME + "项目]" + projectName);
//			sb.append("&nbsp;[项目编号]" + curProjectid);
//			sb.append("&nbsp;[" + VERSION_NAME + "区间]" + auditArea);
			//sb.append("&nbsp;[帐套区间]" + accPackArea);
			//sb.append("&nbsp;[版本状态]" + dog);

		} else {
			sb.append(method);
			sb.append("error");
		}

		out.println(sb.toString());
		out.close();

		return null;
	}

	public ModelAndView hasDog(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();

		System.out.print("当前时间:"
				+ new SimpleDateFormat("HH:mm:ss").format(new Date()));
		if (JRockey2Opp.getUserLic() <= 0) {
			System.out.println(",no dog...");
			out.write("false");
		} else {
			System.out.println(",pass...");
			out.write("true");
		}

		return null;
	}

	/**
	 * 初始化加密狗版本设置
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView setDog(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String dogVersion = request.getParameter("dogVersion");

		if ("".equals(dogVersion) || dogVersion == null) {

		} else {
			String dogVersionKey = MD5.getMD5String(dogVersion);

			Connection conn = null;
			PreparedStatement ps = null;

			try {
				conn = new DBConnect().getConnect("");

				String sql = "update k_system set versionname=?,versionkey=?";
				ps = conn.prepareStatement(sql);

				ps.setString(1, dogVersion);
				ps.setString(2, dogVersionKey);

				ps.execute();

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				ps.close();
				conn.close();
			}
		}

		return null;
	}

	/**
	 * 根据服务器返回的加密串,更新试用期.
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView expend(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Connection conn = null;
		String validDate = "";
		String myPwd = "";
		String pwd = "";
		String sysUi = "";
		String returnValue = "";
		response.setContentType("text/html;charset=utf-8"); // 设置编码
		PrintWriter out = response.getWriter();
		try {

			JRockey2Opp.getDogState();

			Map dogMap = JRockey2Opp.getInfoFromDog();

			sysUi = "" + dogMap.get("sysUi");

			returnValue = new Web()
					.getUrlHtml("http://www.matech.cn/eaudit/getDogInfo.asp?sysUi="
							+ sysUi);

			if (returnValue.indexOf("sysVi") > -1) {
				String allValue[] = returnValue.split("\\|\\|");
				for (int i = 0; i < allValue.length; i++) {
					if (allValue[i].indexOf("serviceDate") > -1) {
						validDate = allValue[i];
						validDate = validDate.split("=")[1];
						System.out.println("validDate:" + validDate);
					}
				}
				// 获到日期的Md5加密
				myPwd = MD5.getMD5String(validDate);

				conn = new DBConnect().getConnect("");

				if (myPwd != null && myPwd.split("-").length > 1) {
					pwd = myPwd.split("-")[1];
				}

				LoginService myValidDate = new LoginService(conn);
				if (myValidDate.isDateSamePwd(validDate, myPwd)
						|| myValidDate.isDateSamePwd(validDate + "-" + sysUi,
								pwd)) { // 延长日期和密码一致，实现延长期限
					myValidDate.insertOrUptDate(validDate);
					new DelTask().setDogInvalidation(false); // 置为有效狗
					returnValue = "";
				} else { // 无权延长期限
					returnValue = "error";
				}

				out.print(returnValue);
				out.close();

			} else {
				out.print("error");
			}
		} catch (Exception e) {
			out.print("error");
			Debug.print(Debug.iError, "更新试用失败！", e);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}

	/**
	 * 更新试用狗为正式狗,授权
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView accredit(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Connection conn = null;
		String returnValue = "";
		Map map = new HashMap();
		String sysUi = "";
		PrintWriter out = response.getWriter();
		try {

			JRockey2Opp.getDogState();

			Map dogMap = JRockey2Opp.getInfoFromDog();

			sysUi = "" + dogMap.get("sysUi");

			returnValue = new Web()
					.getUrlHtml("http://www.matech.cn/eaudit/getDogInfo.asp?sysUi="
							+ sysUi);

			if (returnValue.indexOf("sysVi") > -1) {
				String allValue[] = returnValue.split("\\|\\|");
				for (int i = 0; i < allValue.length; i++) {
					if ("sysVn".equals(allValue[i].split("=")[0].trim())
							|| "sysCn".equals(allValue[i].split("=")[0].trim())
							|| "sysUs".equals(allValue[i].split("=")[0].trim())
							|| "sysVi".equals(allValue[i].split("=")[0].trim())
							|| "sysId".equals(allValue[i].split("=")[0].trim())
							|| "sysUi".equals(allValue[i].split("=")[0].trim())
							|| "sysCo".equals(allValue[i].split("=")[0].trim())
							|| "sysC2".equals(allValue[i].split("=")[0].trim())) {
						map.put(allValue[i].split("=")[0].trim(), allValue[i]
								.split("=")[1].trim());
					}
				}

				response.setContentType("text/html;charset=utf-8"); // 设置编码

				System.out.println("map:" + map);

				try {
					JRockey2Opp.writeInfo(map);
					out.print("");
				} catch (Exception e) {
					out.print("error");
					// org.util.Debug.prtOut("对不起,写狗失败!!!!");
					throw e;
				}
				out.close();

			} else {
				out.print("error");
			}

		} catch (Exception e) {
			out.print("error");
			Debug.print(Debug.iError, "更新试用失败！", e);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}
	
	/**
	 * 首页
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView modelAndView = new ModelAndView(INDEX_VIEW);
		Connection conn = null;
		
		try {
			conn = new DBConnect().getConnect("");
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession") ;
			
			// 公告通知
			PlacardService ps = new PlacardService(conn);
			List placardList = ps.getSpecialList(userSession.getUserId());
			modelAndView.addObject("placardList", placardList);
			
			/*
			// 代办事项
			CommonProcessService cps = new CommonProcessService(conn) ;
			List waitingList = cps.getWaitingTaskList(userSession.getUserId()) ;
			modelAndView.addObject("waitingList",waitingList);
			*/
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		return modelAndView;
	}
	
	/**
	 * 首页
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView mainIndex(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView modelAndView = new ModelAndView(MAININDEX_VIEW);
		Connection conn = null;
		ASFuntion CHF = new ASFuntion() ;
		try {  
			conn = new DBConnect().getConnect("");
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession") ;
			String userId = userSession.getUserId() ;
			
			DepartmentService ds = new DepartmentService(conn);
			boolean b  = ds.isTotalDep(userSession);
			//String aSql=b?" ":" and area ='"+userSession.getAreaid()+"'";
			String aSql=" area ='"+userSession.getAreaid()+"'";
			//公告通知
		//	String sql1 = " select uuid as autoid,title,b.name,publishDate as updateTime,'公告' as sort,1 as ctype, \n"
		//			   + " if(to_days(now()) - to_days(publishDate) <7,'<font color=red>NEW</font>','') as isNew \n"
		//			   + " from k_proclamation a \n"
		//			   + " left join k_user b on a.userId = b.id where 1=1 and concat(a.readUserId,',') like concat('%',"+userSession.getUserId()+",',%') \n" ;
			
			//天健资讯
			String sql1 = " select autoid,title,b.name,updateTime,'天健资讯',2 as ctype,type, \n"
				  	//   + " if(locate('"+userSession.getUserId()+"',nameid) =0,'<font color=red>NEW</font>','') as isNew \n"
				 // + " if(to_days(now()) - to_days(updateTime) <2,'<font color=red>NEW</font>','') as isNew \n"
				 + " if(to_days(updateTime)-to_days('2012-11-25') >0 and locate('"+userSession.getUserId()+"',nameid) =0,'<font color=red>NEW</font>','') as isNew \n"
				 + " from oa_news a \n"   
					  // + " left join k_user b on a.publishUserId = b.id where  dept_type ='天健' or "+aSql+" \n" ;
			   + " left join k_user b on a.publishUserId = b.id where  menuid in (select sm.id from s_sysmenu sm where sm.parentid in (SELECT ssm.menu_id FROM s_sysmenu ssm WHERE ssm.id=10000820) ) or "+aSql+" \n" ;
			//技术支持
			String sql2 = " select autoid,title,b.name,updateTime,'技术支持',2 as ctype, type,\n"
				  	   + " if(to_days(now()) - to_days(updateTime) <2,'<font color=red>NEW</font>','') as isNew \n"
				  	// + " if(locate('"+userSession.getUserId()+"',nameid) =0,'<font color=red>NEW</font>','') as isNew \n"
					   + " from oa_news a \n"
					   + " left join k_user b on a.publishUserId = b.id where big_type='2' \n" ;
			//期刊读物
			String sql3 = " select autoid,title,b.name,updateTime,'期刊读物',2 as ctype, type,\n"
				  	  // + " if(to_days(now()) - to_days(updateTime) <7,'<font color=red>NEW</font>','') as isNew \n"
				  	 + " if(locate('"+userSession.getUserId()+"',nameid) =0,'<font color=red>NEW</font>','') as isNew \n"
					   + " from oa_news a \n"
					   + " left join k_user b on a.publishUserId = b.id where big_type='3' \n" ;
			
			//部门规章
		//	String sql3 = " select autoid,title,b.name,updateTime,'部门规章',3 as ctype, \n"
		//		   + " if(to_days(now()) - to_days(updateTime) <7,'<font color=red>NEW</font>','') as isNew \n"
		//		   + " from oa_regulations a \n"
		//		   + " left join k_user b on a.publishUserId = b.id  \n" ;
			
			String allSql = sql1 ;//+ " union \n" + sql2;// + " union \n" + sql3 ;
			
			
			DataGridProperty pp1 = new DataGridProperty();
			
			pp1.setTableID("placardList") ;
			pp1.setWhichFieldIsValue(1);
			pp1.setColumnWidth("3,45,10,8") ;
			pp1.setCustomerId("") ;
			
			pp1.setPageSize_CH(50) ;
			pp1.setTrActionProperty(true) ;
			pp1.setTrAction(" autoId='${autoId}' ctype=${ctype}") ;
			
			pp1.addColumn("NEW", "isNew");
			pp1.addColumn("标题","title",null,null,"<a href=# onclick=\"show('${autoid}',${ctype},'${value}');\">${value}</a>") ;
			pp1.addColumn("分类", "type");
		
			pp1.addColumn("发布时间", "updateTime");
			
			pp1.setOrderBy_CH("updateTime") ;
			pp1.setDirection_CH("desc") ;
			pp1.setSQL(sql1);
			
			request.getSession().setAttribute(DataGrid.sessionPre+pp1.getTableID(),pp1);
			
			
			DataGridProperty pp2 = new DataGridProperty();
			
			pp2.setTableID("newsList") ;
			pp2.setWhichFieldIsValue(1);
			pp2.setColumnWidth("3,30,10,6,10") ;
			pp2.setCustomerId("") ;
			
			pp2.setPageSize_CH(50) ;
			pp2.setTrActionProperty(true) ;
			pp2.setTrAction(" autoId='${autoId}' ctype=${ctype} ") ;
			
			pp2.addColumn("NEW", "isNew");
			pp2.addColumn("标题","title",null,null,"<a href=# onclick=\"show('${autoid}',${ctype},'${value}');\">${value}</a>") ;
			pp2.addColumn("分类", "type");
			
			pp2.addColumn("发布时间", "updateTime");
			
			pp2.setOrderBy_CH("updateTime") ;
			pp2.setDirection_CH("desc") ;
			pp2.setSQL(sql2);
			
			request.getSession().setAttribute(DataGrid.sessionPre+pp2.getTableID(),pp2);
			
			
			DataGridProperty pp3 = new DataGridProperty();
			
			pp3.setTableID("regulationsList") ;
			pp3.setWhichFieldIsValue(1);
			pp3.setColumnWidth("3,30,10,6,10") ;
			pp3.setCustomerId("") ;
			
			pp3.setPageSize_CH(50) ;
			pp3.setTrActionProperty(true) ;
			pp3.setTrAction(" autoId='${autoId}' ctype=${ctype} ") ;
			
			pp3.addColumn("NEW", "isNew");
			pp3.addColumn("标题","title",null,null,"<a href=# onclick=\"show('${autoid}',${ctype},'${value}');\">${value}</a>") ;  
			pp3.addColumn("分类", "type");
			
			pp3.addColumn("发布时间", "updateTime");
			
			pp3.setOrderBy_CH("updateTime") ;
			pp3.setDirection_CH("desc") ;
			pp3.setSQL(sql3);
			
			request.getSession().setAttribute(DataGrid.sessionPre+pp3.getTableID(),pp3);
			
			
			DataGridProperty pp4 = new DataGridProperty();
			
			pp4.setTableID("allList") ;
			pp4.setWhichFieldIsValue(1);
			pp4.setColumnWidth("3,35,10,8") ;
			pp4.setCustomerId("") ;
			
			pp4.setPageSize_CH(50) ;
			pp4.setTrActionProperty(true) ;
			pp4.setTrAction(" autoId='${autoId}' ctype=${ctype} ") ;
			
			pp4.addColumn("NEW", "isNew");
			pp4.addColumn("标题","title",null,null,"<a href=# onclick=\"show('${autoid}',${ctype},'${value}');\">${value}</a>") ;  
			pp4.addColumn("分类", "type");
			
			pp4.addColumn("发布时间", "updateTime");
			
			pp4.setOrderBy_CH("updateTime") ;
			pp4.setDirection_CH("desc") ;
			pp4.setSQL(allSql);
			
			request.getSession().setAttribute(DataGrid.sessionPre+pp4.getTableID(),pp4);
			
			
			InfoService is = new InfoService(conn) ;
			List list = is.getCtype();
			String json = "{}";
			if(list != null){
				json = JSONArray.fromObject(list).toString();
			}
			modelAndView.addObject("json",json);
			
			//本月员工生日提醒
			//List<String> userBirthList = is.getUserBirth(userId) ;
			//modelAndView.addObject("userBirthList",userBirthList);
			
			//本月客户生日提醒
			//List<String> customerBirthList = is.getCustomerBirth(userId) ;
			//modelAndView.addObject("customerBirthList",customerBirthList);
			
			//通用提醒功能
			List<Map<String,String>> remindList = is.getTitleHint(userSession);//is.getRemind(userId,userSession.getUserLoginId(),userSession.getUserAuditDepartmentId()) ;
			modelAndView.addObject("remindList",remindList);
			
			
			//email 
			List<Map<String,String>> emailList = is.getEMail(userId);
			modelAndView.addObject("emailList",emailList);
			
			//公告通知
			List<Map<String,String>> proclamationList = is.getProclamation(userId);
			modelAndView.addObject("proclamationList",proclamationList);
			
			String homePageRefreshTime = UTILSysProperty.SysProperty.getProperty("首页刷新时间");  //首页刷新的时间
			modelAndView.addObject("homePageRefreshTime", homePageRefreshTime);
			
			//今天的日期
			modelAndView.addObject("todayTime", new ASFuntion().getCurrentDate());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		return modelAndView;
	}
	
	
	/**
	 * 等待我办理的工作
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView myDealList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(INDEX_MYDEALLIST);
		
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession") ;
			String userId = userSession.getUserId() ;
		
			String sql = " SELECT a.taskId,b.ApplyDate,e.name,a.auditStatus,d.processKey,d.processName FROM ( \n"+
							 " SELECT DISTINCT a.DBID_ AS taskId,b.ID_, a.EXECUTION_ID_,b.ACTIVITYNAME_ AS auditStatus, PROCDEFID_, \n"+
							 " GROUP_CONCAT(c.userID_ ) AS auditUserId \n"+
							 " FROM jbpm4_task a  \n"+
							 " INNER JOIN jbpm4_execution b ON a.EXECUTION_ID_ = b.ID_ \n"+ 
							 " LEFT JOIN  jbpm4_participation c ON a.DBID_ = c.TASK_  AND c.type_ = 'candidate' \n"+ 
							 " where 1=1 AND "+userId+" like concat(c.userID_,'%')  \n"+
							 " GROUP BY a.EXECUTION_ID_  \n"+
							 " ) a  \n"+
							 " INNER JOIN( \n"+ 
							 " select b.processinstanceid,b.applyDate,a.status,b.property,a.UserID as userid from k_seal  a \n"+   
							 " inner join j_sealprocss b on a.uuid= b.uuid   union   \n"+
							 " select b.processinstanceid,b.applyDate, a.status,b.property,a.UserID as userid from k_leave a \n"+   
							 " inner join j_leaveprocss b on a.uuid = b.uuid  and b.ctype='请假'  union   \n"+
							 " select b.processInstanseId,b.applyTime,a.state,b.property,a.creator as userid from  z_projectbusiness a \n"+   
							 " inner join j_taskaudit b on a.projectId	= b.projectid where a.creator = b.applyUserId   union   \n"+
							 " select b.processinstanceid,b.applyDate,a.state,b.property,a.createuser as userid from j_userpreferment a \n"+   
							 " inner join j_userprefermentprocss b on a.unid =b.unid  union   \n"+
							 " select b.processinstanceid,b.applyDate,a.status,b.property,a.UserID as userid from k_leaveoffice a \n"+  
							 " inner join j_leaveofficeprocss b on a.uuid=b.uuid   union   \n"+
							 " select b.processinstanceid,b.applyDate,a.status,b.property,a.UserID as userid  from k_leave a \n"+   
							 " inner join j_leaveprocss b on a.uuid= b.uuid where   b.ctype='销假'  union    \n"+
							 " select b.processinstanceid,b.applyDate,a.status,b.property,a.createUserid as userid from k_meetingorder a \n"+   
							 " inner join  j_meetingorderprocss b on a.uuid = b.uuid  union   \n"+
							 " select b.processinstanceid,b.applyDate,a.status ,b.property,a.userId as userid  from k_proclamation a \n"+   
							 " inner join j_proclamationprocss b on a.uuid = b.proclamationid  union    \n"+
							 " select c.processinstanceid,c.applyDate,b.status,c.property ,b.userid as userid from k_waresstock a  \n"+ 
							 " inner join k_waresstream b on a.uuid = b.waresStockId   \n"+
							 " inner join j_waresstreamprocss c on b.uuid = c.uuid   union \n"+  
							 " select c.processinstanceid,applyDate,b.status ,c.property,b.userid as userid from k_waresstock a \n"+   
							 " inner join k_waresstockdetails b on a.uuid = b.waresstockId   \n"+
							 " inner join j_waresstreamprocss c on b.uuid = c.uuid   union   \n"+
							 " select processInstanseId,applyTime,state,property,applyUserId as userid from j_docpost \n"+ 
							 " ) b ON a.ID_  = b.ProcessInstanceId \n"+
							 " LEFT JOIN jbpm4_deployprop c on a.PROCDEFID_ = c.STRINGVAL_ \n"+
							 " LEFT JOIN j_processdeploy d on c.objname_ = d.processKey \n"+
					 		 " LEFT JOIN k_user e ON b.userId = e.id ";
	
			DataGridProperty pp = new DataGridProperty();
			
			pp.setTableID("myDealList") ;
			pp.setWhichFieldIsValue(1);
			pp.setColumnWidth("18,10,15,10,10") ;
			pp.setCustomerId("") ;
			
			pp.setPageSize_CH(50) ;
			pp.setCancelOrderby(true) ;
			
			pp.addColumn("事项描述", "projectname");
			pp.addColumn("发起人", "name");
			pp.addColumn("最后处理时间","ApplyDate");
			pp.addColumn("流程名称", "processname");
			pp.addColumn("状态", "auditStatus");
			pp.addColumn("操作", "taskId",null,null,"<a href=# onclick=goDeal('${processKey}','${taskId}','${processname}');>【办理】</a>") ;
			
			pp.setOrderBy_CH("a.create_") ;
			pp.setDirection_CH("desc") ;
			
			pp.setSQL(sql);
			
			request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);

		
		return modelAndView;
		
	}
	
	/**
	 * 发起的工作
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView myApplyList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView(INDEX_MYAPPLYLIST);
		UserSession userSession = (UserSession)request.getSession().getAttribute("userSession") ;
		String userId = userSession.getUserId() ;
		
			String sql3 = " select a.processinstanceid,a.applyDate,b.ACTIVITYNAME_,a.property,e.processKey,e.processName from ( \n"+
						  " select b.processinstanceid,b.applyDate,a.status,b.property from k_seal  a \n"+
						  " inner join j_sealprocss b on a.uuid= b.uuid and a.STATUS = '已发起' and a.UserID = "+userId+" union \n"+
						  " select b.processinstanceid,b.applyDate, a.status,b.property from k_leave a \n"+
						  " inner join j_leaveprocss b on a.uuid = b.uuid and a.status='已发起'  and b.ctype='请假' and UserID = "+userId+" union \n"+
				          " select b.processInstanseId,b.applyTime,a.state,b.property from  z_projectbusiness a \n"+
				          " inner join j_taskaudit b on a.projectId	= b.projectid where a.creator = b.applyUserId  and a.state='已发起' and a.creator = "+userId+" union \n"+
				          " select b.processinstanceid,b.applyDate,a.state,b.property from j_userpreferment a \n"+
				          " inner join j_userprefermentprocss b on a.unid =b.unid where a.createuser = "+userId+" and a.state='已发起' union \n"+ 
						  " select b.processinstanceid,b.applyDate,a.status,b.property from k_leaveoffice a \n"+ 
						  " inner join j_leaveofficeprocss b on a.uuid=b.uuid where a.status = '已发起' and a.UserID = "+userId+" union \n"+ 
						  " select b.processinstanceid,b.applyDate,a.status,b.property from k_leave a \n"+ 
						  " inner join j_leaveprocss b on a.uuid= b.uuid where a.UserID = "+userId+" and  a.status='销假已发起' and  b.ctype='销假'  union  \n"+ 
						  " select b.processinstanceid,b.applyDate,a.status,b.property from k_meetingorder a \n"+ 
						  " inner join  j_meetingorderprocss b on a.uuid = b.uuid where a.createUserid = "+userId+" and a.status = '已发起' union \n"+ 
						  " select b.processinstanceid,b.applyDate,a.status ,b.property from k_proclamation a \n"+ 
						  " inner join j_proclamationprocss b on a.uuid = b.proclamationid where a.userId ="+userId+" and a.status = '已启动' union  \n"+ 
						  " select c.processinstanceid,c.applyDate,b.status,c.property from k_waresstock a \n"+ 
						  " inner join k_waresstream b on a.uuid = b.waresStockId \n"+ 
						  " inner join j_waresstreamprocss c on b.uuid = c.uuid \n"+ 
						  " where b.userid="+userId+" and b.status = '已发起'  union \n"+ 
						  " select c.processinstanceid,applyDate,b.status ,c.property from k_waresstock a \n"+ 
						  " inner join k_waresstockdetails b on a.uuid = b.waresstockId \n"+ 
						  " inner join j_waresstreamprocss c on b.uuid = c.uuid \n"+ 
						  " where b.userid = "+userId+" and b.status = '已发起' union \n"+ 
						  " select processInstanseId,applyTime,state,property from j_docpost where state='已发起' and applyUserId = "+userId+" \n"+ 
						  " ) a \n"+ 
						  " LEFT JOIN jbpm4_execution b ON a.ProcessInstanceId = b.id_  \n"+ 
						  " LEFT JOIN jbpm4_deployprop c ON b.PROCDEFID_ = c.STRINGVAL_   AND c.key_ = 'pdid' \n"+ 
						  " LEFT JOIN jbpm4_deployprop d ON d.OBJNAME_ = c.OBJNAME_ AND  d.DEPLOYMENT_ = c.DEPLOYMENT_ AND d.KEY_ = 'pdkey' \n"+ 
						  " LEFT JOIN j_processdeploy e ON d.STRINGVAL_ = e.processKey ";

		
		DataGridProperty pp3 = new DataGridProperty();
		
		pp3.setTableID("myApplyList") ;
		pp3.setWhichFieldIsValue(1);
		pp3.setColumnWidth("15,15,18,18") ;
		pp3.setCustomerId("") ;
		
		pp3.setPageSize_CH(50) ;
		pp3.setCancelOrderby(true) ;
		
		
		pp3.addColumn("事项描述", "property");
		pp3.addColumn("最后处理时间", "applyDate");
		pp3.addColumn("流程名称", "processName");
		pp3.addColumn("状态", "ACTIVITYNAME_");
		pp3.addColumn("操作", "processinstanceid",null,null,"<a href=# onclick=goView('${processKey}','${processinstanceid}','${processname}');>【查看流程图】</a>") ;
		
		pp3.setOrderBy_CH("a.create_") ;
		pp3.setDirection_CH("desc") ;
		
		pp3.setSQL(sql3);
		
		request.getSession().setAttribute(DataGrid.sessionPre+pp3.getTableID(),pp3);
		
		return modelAndView;
		
	}
	
	
	//刷新email
	public void reloadFlag(HttpServletRequest request,HttpServletResponse response) throws Exception {
		Connection conn = null;
		
		try {  
			conn = new DBConnect().getConnect("");
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession") ;
			String userId = userSession.getUserId() ;
			
			ASFuntion af = new ASFuntion();
			String flag = af.showNull(request.getParameter("flag"));
			
			InfoService is = new InfoService(conn) ;
			//email 
			List<Map<String,String>> map = null; 
			if("email".equals(flag)){
				map = is.getEMail(userId);
			}else if("proclamation".equals(flag)){
				map = is.getProclamation(userId);
			}
			
			String json = JSONArray.fromObject(map).toString() ;
			
			response.setContentType("text/html;charset=utf-8");  //设置编码
			PrintWriter out = response.getWriter();
			out.write(json);
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
	}
	
	//知识首页
	public ModelAndView knowledge(HttpServletRequest request,HttpServletResponse response) throws Exception {
		Connection conn = null;
		ModelAndView modelAndView = new ModelAndView(KNOWLEDGE_VIEW) ;
		try {  
			conn = new DBConnect().getConnect("");
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession") ;
			String userId = userSession.getUserId() ;
			
			InfoService is = new InfoService(conn) ;
			//我提的问题
			String sqlWhere = " AND createUserId ='" + userId + "'" ;
			String orderby = " order by GreateDate desc " ;
			List<Question> myQuestionList =  is.getQuestion(sqlWhere,orderby) ;
			
			//我关注的问题
			
			//最新问题
			sqlWhere = "" ;
			List<Question> newQuestionList =  is.getQuestion(sqlWhere,orderby) ;
			
			//最热门的问题
			sqlWhere = "" ;
			orderby = "" ;
			List<Question> hotQuestionList =  is.getQuestion(sqlWhere,orderby) ;
			
			//我的知识贡献度
			
			String sql = " SELECT ifnull(COUNT(*),0) AS answerCount FROM p_answer a GROUP BY userid " ;
			String sql2 = " SELECT ifnull(COUNT(*),0) AS questionCount FROM p_question a GROUP BY createUserId " ;
			
			DbUtil dbUtil = new DbUtil(conn) ;
			String myAnswerCount =  dbUtil.queryForString(sql) ;
			String myQuestionCount =  dbUtil.queryForString(sql2) ;
			
			//知识排名
			List<Map<String,String>> rankList = is.getRank() ;
			
			modelAndView.addObject("myQuestionList", myQuestionList) ;
			modelAndView.addObject("newQuestionList", newQuestionList) ;
			modelAndView.addObject("hotQuestionList", hotQuestionList) ;
			modelAndView.addObject("rankList", rankList) ;
			
			modelAndView.addObject("myAnswerCount", myAnswerCount) ;
			modelAndView.addObject("myQuestionCount", myQuestionCount) ;
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView ;
	}
	
	
	//我的日程表
	public void getSchedule(HttpServletRequest request,HttpServletResponse response) throws Exception {
		Connection conn = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM") ;
		try {  
			conn = new DBConnect().getConnect("");
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession") ;
			String loginId = userSession.getUserLoginId() ;
			
			String yearMonth = sdf.format(new Date()) ;
			String startDate = yearMonth + "-01" ;
			String endDate = yearMonth + "-31" ;
			
			InfoService is = new InfoService(conn) ;
			
			//email 
			List<Map<String,String>> map = is.getSchedule(loginId, startDate, endDate); 
			
			String json = JSONArray.fromObject(map).toString() ;
			response.setContentType("text/html;charset=utf-8");  //设置编码
			PrintWriter out = response.getWriter();
			out.write(json);
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
	}
	
}
