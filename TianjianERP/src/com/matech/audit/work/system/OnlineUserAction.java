package com.matech.audit.work.system;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.framework.listener.OnlineListListener;
import com.matech.framework.listener.UserCurTask;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.net.Web;
import com.matech.framework.pub.single.LockInfoVO;
import com.matech.framework.pub.single.Single;
import com.matech.framework.pub.sys.UTILSysProperty;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.service.login.LoginService;

/**
 * <p>Title: 在线用户管理</p>
 * <p>Description: 在线用户管理</p>
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
 * 2007-6-21
 */
public class OnlineUserAction extends MultiActionController{
	private static final String ONLINE_USER_VIEW = "AS_SYSTEM/userlist.jsp";

	/**
	 * 返回在线用户列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception{
		ModelAndView modelAndView = new ModelAndView(ONLINE_USER_VIEW);
		
		//无条件刷新用户数
		try {
			OnlineListListener.refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {

			Map map =  Single.getLockList();
			Set set = map.keySet();

			List lockList = new ArrayList();
			Iterator it = set.iterator();

			while(it.hasNext()) {
				String key = (String)it.next();
				lockList.add((LockInfoVO)map.get(key));
			}

			modelAndView.addObject("lockList", lockList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		UserSession userSession = null;
		String allUserId = "";
		String userId = "";
		List userList = OnlineListListener.getList();

		try {

			ASFuntion asf = new ASFuntion();

			if(userList != null && !userList.isEmpty()) {
				for(int i=0; i < userList.size(); i++){
					userSession = (UserSession)userList.get(i);

					if(userSession == null) {
						continue;
					}

					userId = asf.showNull(userSession.getUserId());

					if(allUserId.indexOf(userId) == -1){
						allUserId += userId + ",";
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		modelAndView.addObject("userList", userList);
		modelAndView.addObject("allUserId", allUserId);

		return modelAndView;
	}

	/**
	 * 踢除在线人员
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView kickUser(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String sessionId = request.getParameter("sessionId");

		List list = OnlineListListener.getList();

		System.out.println("开始执行T人程序,嘿嘿..");
		System.out.println("前台sessionId | 用户sessionId | IP | 用户名 ");
		for(int i=0; i < list.size(); i++) {
			try {
			
				UserSession userSession = (UserSession) list.get(i);
	
				System.out.print(sessionId + "|");
				System.out.print(userSession.getUserSessionId() + "|" );
				System.out.print(userSession.getUserIp() + "|" );
				System.out.println(userSession.getUserName() + "|" );
	
				if(userSession.getUserSessionId().equals(sessionId)) {
	
					System.out.println("\n找到了,很遗憾,sorry了,开T!!!" + userSession.getUserSessionId() + "|" + sessionId + "\n");
					
					try {
						System.out.println("注销用户session");
						userSession.getUserSession().invalidate();
					} catch (Exception e) {
						e.printStackTrace();
					} 
	
					try {
						//无条件删除
						System.out.println("把用户从list中删除");
						//list.remove(userSession);
						OnlineListListener.removeUserSession(userSession);
						System.out.println("从list中删除用户成功!!");
					} catch (Exception e) {
						e.printStackTrace();
					}
	
					break;
				} // end if
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("\nT人程序执行完毕!!!");
		return null;
	}

	/**
	 * 强行关闭用户打开的底稿
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView closeTask(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String sessionId = request.getParameter("sessionId");

		List list = OnlineListListener.getList();

		for(int i=0; i < list.size(); i++) {
			
			try {
			
				UserSession userSession = (UserSession) list.get(i);
				if(userSession.getUserSessionId().equals(sessionId)) {
					Set userCurTasks = userSession.getUserCurTasks();
					Iterator it = userCurTasks.iterator();
					ASFuntion asf = new ASFuntion();
	
					String taskId = asf.showNull(request.getParameter("taskId"));
					String projectId = asf.showNull(request.getParameter("projectId"));
	
					while(it.hasNext()) {
						UserCurTask userCurTask = (UserCurTask)it.next();
	
						if (taskId.equals(userCurTask.getCurTaskId()) && projectId.equals(userCurTask.getCurTaskProjectId())) {
							userCurTasks.remove(userCurTask);
							break;
						}
					}
	
					System.out.println("用户当前打开底稿数：" + userCurTasks.size());
					break;
				} // end if
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 给在线用户发送消息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView sendMessage(HttpServletRequest request, HttpServletResponse response) throws Exception{
		response.setContentType("text/html;charset=utf-8");

		//收件人
		String userId = request.getParameter("userId");

		String[] userIndex=null;


		ASFuntion CHF = new ASFuntion();

		Connection conn = null;
		PrintWriter out = response.getWriter();
		try {
			HttpSession session = request.getSession();
			UserSession userSession = (UserSession)session.getAttribute("userSession");

			if(userSession == null) {
				userSession = new UserSession();
				userSession.setUserId("19");
			}

			conn = new DBConnect().getConnect("");

			PlacardService placardService = new PlacardService(conn);
			String title = request.getParameter("txtTitle");
			String content = request.getParameter("txtMsg");
			String addresser = userSession.getUserId();
			String addressee = "";
			if(userId.indexOf(",")<=-1){
				addressee = userId;

				if(!"".equals(addresser) && !"".equals(addressee)) {
					PlacardTable placard = new PlacardTable();
					placard.setCaption(title);
					placard.setMatter(content);
					placard.setIsReversion(0);
					placard.setAddresser(addresser);
					placard.setAddressee(addressee);
					placard.setAddresserTime(CHF.getCurrentDate()+" "+CHF.getCurrentTime());
					placard.setIsRead(0);

					placardService.AddPlacard(placard);
				}
			}else{

					 userIndex = userId.split(",");

				for(int i=0;i<userIndex.length;i++){
					addressee=userIndex[i];

					if(!"".equals(addresser) && !"".equals(addressee)) {
						PlacardTable placard = new PlacardTable();
						placard.setCaption(title);
						placard.setMatter(content);
						placard.setIsReversion(0);
						placard.setAddresser(addresser);
						placard.setAddressee(addressee);
						placard.setAddresserTime(CHF.getCurrentDate()+" "+CHF.getCurrentTime());
						placard.setIsRead(0);

						placardService.AddPlacard(placard);
					}
				}


			}
				out.write("<center><br /><br /><br />发送消息成功<br /><br /><br />");
				out.write("<input type='button' value='关闭窗口' class='flyBT'  onClick='self.close();'></center>");
				return null;


		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}

	}

	/**
	 * 当前用户打开底稿
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView userCurTask(HttpServletRequest request, HttpServletResponse response) throws Exception{

		response.setContentType("text/html;charset=utf-8");
		HttpSession session = request.getSession();
		UserSession userSession = (UserSession)session.getAttribute("userSession");

		Set userCurTasks = userSession.getUserCurTasks();
		PrintWriter out = response.getWriter();
		if(userCurTasks.size() <= 0 ) {
			out.write("ok");
		} else {
			out.write("对不起,你打开了以下底稿,请关闭后再进入项目! \n");

			Iterator it = userCurTasks.iterator();
			while(it.hasNext()) {
				UserCurTask userCurTask = (UserCurTask)it.next();
				out.write(userCurTask.getCurTaskCode() + "  " + userCurTask.getCurTaskName() + "\n");
			}
		}

		return null;
	}

	/**
	 *
	 * 检查用户是否在线,检查依据：用户id,用户密码,用户ip
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView checkUserOnline(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		ASFuntion asf = new ASFuntion();

		String userLoginId = asf.showNull(request.getParameter("userLoginId"));
		String password = asf.showNull(request.getParameter("password"));
		String userIp = asf.showNull(Web.getIp(request));

		String temp = asf.showNull(UTILSysProperty.SysProperty.getProperty("是否允许非本机同名用户登录"));

		boolean isNoSameLoginId = "否".equals(temp);

		Connection conn = null;

		try {
			conn = new DBConnect().getConnect("");

			List list = new LoginService(conn).getResumeList(userLoginId, password);

			PrintWriter out = response.getWriter();

			if(list != null) {

				List userList = OnlineListListener.getList();
				UserSession userSession = null;
				boolean isOnline = false;

				//寻找该用户
				for(int i=0; i < userList.size(); i++) {
					userSession = (UserSession)userList.get(i);
					if((userIp.equals(userSession.getUserIp()) && userLoginId.equals(userSession.getUserLoginId()) )
						|| (isNoSameLoginId && userLoginId.equals( userSession.getUserLoginId() ) ) ) {

						isOnline = true;
						break;
					}
				}

				if(isOnline) {
					//该用户在线,
					out.print(userSession.getUserSessionId());
				} else {
					out.print("offLine");
				}
			} else {
				//用户名或密码错误,忽略
				out.print("noUser");
			}

		} catch(Exception e) {

		} finally {
			DbUtil.close(conn);
		}

		return null;
	}


	/**
	 * 在线用户信息列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getOnlineUserList(HttpServletRequest request, HttpServletResponse response) throws Exception {

		response.setContentType("text/html;charset=utf-8");
		UserSession userSession = null;

		String userId = "";  //用户ID
		String userName = "";  //用户名
		String loginName = "";  //登陆名
		String curProjectName = ""; //登陆项目名
		String curProjectId = "";  //登陆项目ID
		String userIp = "";  //用户IP地址
		String userLoginTime = ""; //用户登陆时间
		String sessionId = ""; //用户SessionID
		String clientDogSysUi = ""; //客户端加密狗编号

		List userList = OnlineListListener.getList();

		try {

			ASFuntion asf = new ASFuntion();

			PrintWriter out = response.getWriter();
			if(userList != null && !userList.isEmpty()) {
				for(int i=0; i < userList.size(); i++){
					userSession = (UserSession)userList.get(i);

					if(userSession == null) {
						continue;
					}

					userId = asf.showNull(userSession.getUserId());
					userName = asf.showNull(userSession.getUserName());
					loginName = asf.showNull(userSession.getUserLoginId());
					userIp = asf.showNull(userSession.getUserIp());
					userLoginTime = asf.showNull(userSession.getUserLoginTime());
					curProjectName = asf.showNull(userSession.getCurProjectName());
					if("".equals(curProjectName)){
						curProjectName = "无";
					}
					curProjectId = asf.showNull(userSession.getCurProjectId());
					if("".equals(curProjectId)){
						curProjectId = "无";
					}
					sessionId = asf.showNull(userSession.getUserSession().getId());
					
					clientDogSysUi = asf.showNull(userSession.getClientDogSysUi());

					out.print(userName + "``");
					out.print(loginName + "``");
					out.print(userId + "``");
					out.print(userIp + "``");
					out.print(userLoginTime + "``");
					out.print(curProjectName + "``");
					out.print(curProjectId + "``");
					out.print(sessionId + "``");
					out.print(clientDogSysUi + "||");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

}