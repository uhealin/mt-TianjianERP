package com.matech.audit.work.meetingOrder;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.department.DepartmentService;
import com.matech.audit.service.department.model.DepartmentVO;
import com.matech.audit.service.meetingJoinner.MeetingJoinnerService;
import com.matech.audit.service.meetingJoinner.model.MeetingJoinner;
import com.matech.audit.service.meetingOrder.MeetingOrderService;
import com.matech.audit.service.meetingOrder.model.MeetingOrder;
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.audit.service.user.model.User;
import com.matech.audit.service.userpopedom.UserPopedomService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.StringUtil;

public class MeetingOrderAction extends MultiActionController {
	private static final String list = "/MeetingOrder/list.jsp";
	private static final String myList = "/MeetingOrder/myList.jsp";
	private static final String listTotal = "/MeetingOrder/listTotal.jsp";
	private static final String listAudit = "/MeetingOrder/listAudit.jsp";
	private static final String listPass = "/MeetingOrder/listPass.jsp";
	private static final String listNotPass = "/MeetingOrder/listNotPass.jsp";
	private static final String addAndEdit = "MeetingOrder/addAndEdit.jsp";
	private static final String optAddAndEdit = "MeetingOrder/optAddAndEdit.jsp";
	private static final String audit = "MeetingOrder/audit.jsp";
	private static final String view = "MeetingOrder/view.jsp";
	private static final String printTime = "MeetingOrder/printTime.jsp";
	private static final String addAndEdit2 = "MeetingOrder/addAndEdit2.jsp";
	/**
	 * 看到 自己发起的自己审核的 自己参加的
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		
		ModelAndView model = new ModelAndView(list) ;
		
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		String userid = userSession.getUserId();
		
		String opt = request.getParameter("opt");

		DataGridProperty pp = new DataGridProperty();

		
		String sql = " select mo.uuid,mo.name as moname,(select name from k_meetingroom mm where mm.uuid=mo.meetingRoomId) as aa,mo.title,mo.event,mo.startTime,mo.endTime, "
				   + " mo.requirements,mo.equipment,mo.createDate,mo.auditDate,mo.status,u.name as username,u.mobilephone,kd.departname,ku.name as auditName"
				   + " from k_meetingOrder mo "
				   + " left join " 
				   + "(select meetingOrderid," 
				   + " group_concat(distinct waitingUserId order by waitingUserId) as  waitingUserId, "
				   + " group_concat(distinct inituserId order by initUserId) as  initUserId, "
				   + " group_concat(distinct actualUserId order by actualUserId) as  actualUserId, "
				   + " group_concat(distinct historyUserId order by historyUserId) as  historyUserId "
				   + " from k_meetingJoiner group by meetingOrderid " 
				   + ") mj on mo.uuid=mj.meetingOrderid " 
				   + " left join k_user u on mo.createUserId=u.id  " +
				   	" left join k_department kd on kd.autoid =u.departmentid " +
				   	" left join k_user ku on ku.id=mo.auditUserId "
				   + " where mo.createUserId='"+userid+"' or mo.auditUserId='"+userid+"' " 
				   + " or concat(',',waitingUserId,',') like '%,"+userid+",%' " 
				   + " or concat(',',initUserId,',') like '%,"+userid+",%' " 
				   + " or concat(',',actualUserId,',') like '%,"+userid+",%' " 
				   + " or concat(',',historyUserId,',') like '%,"+userid+",%' ";
		
		pp.addColumn("会议名称", "moname");
		pp.addColumn("会议室名称", "aa");
		pp.addColumn("会议起始时间", "startTime");
		pp.addColumn("结束起始时间", "endTime");
		pp.addColumn("会议发起时间", "createDate");
		pp.addColumn("会议发起人", "username");
		pp.addColumn("所属部门","departname");
		pp.addColumn("审核人","auditName");
		pp.addColumn("状态", "status");
		pp.addColumn("手机","mobilphone");
		
		
		
		
		pp.setColumnWidth("12,10,10,12,12,10,12,12,8,12");
		pp.setTableID("meetingOrderList");
		
		pp.setPageSize_CH(50);
		
		pp.setOrderBy_CH("createDate");
		pp.setDirection_CH("desc");
		
		pp.setInputType("radio");
		pp.setWhichFieldIsValue(1);
		
		pp.setCustomerId("") ;
		
		pp.setTrActionProperty(true) ;
		pp.setTrAction(" uuid=${uuid} ") ;
		
		pp.setSQL(sql.toString());
		
		//System.out.println("  会议 list  sql="+sql);

		request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);

		return model;
		
	}
	
	
	/**
	 * 待审核、已通过、不通过
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView listTotalBak(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		
		ModelAndView model = new ModelAndView(listTotal) ;

		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		String userid = userSession.getUserId();
		
		String opt = request.getParameter("opt");
		
		DataGridProperty pp = new DataGridProperty();
		
		String sql = " select mo.uuid,mo.name as moname,mo.meetingRoomId,mo.title,mo.event,mo.startTime,mo.endTime, "
				   + " mo.requirements,mo.equipment,mo.createDate,mo.auditDate,mo.status,u.name as username "
				   + " from k_meetingOrder mo "
				   + " left join k_user u on mo.createUserId=u.id  " ;
			
		if("audit".equalsIgnoreCase(opt)){
			model = new ModelAndView(listAudit);
			sql = sql + " where mo.auditUserId='"+userid+"' and mo.status='待审核' " ;
			
			pp.setTableID("meetingOrderListAudit");
			
		}else if("pass".equalsIgnoreCase(opt)){
			model = new ModelAndView(listPass);
			sql =  " select mo.uuid,mo.name as moname,mo.meetingRoomId,mo.title,mo.event,mo.startTime,mo.endTime, "
				   + " mo.requirements,mo.equipment,mo.createDate,mo.auditDate,mo.status,u.name as username "
				   + " from k_meetingOrder mo "
				   + " left join (select meetingOrderid," 
				   + " group_concat(distinct waitingUserId order by waitingUserId) as  waitingUserId, "
				   + " group_concat(distinct inituserId order by initUserId) as  initUserId, "
				   + " group_concat(distinct actualUserId order by actualUserId) as  actualUserId, "
				   + " group_concat(distinct historyUserId order by historyUserId) as  historyUserId "
				   + " from k_meetingJoiner group by meetingOrderid ) mj on mo.uuid=mj.meetingOrderid " 
				   + " left join k_user u on mo.createUserId=u.id  "
				   + " where mo.status='已通过' and mo.createUserId='"+userid+"' or mo.auditUserId='"+userid+"' " 
				   + " or concat(',',waitingUserId,',') like '%,"+userid+",%' " 
				   + " or concat(',',initUserId,',') like '%,"+userid+",%' " 
				   + " or concat(',',actualUserId,',') like '%,"+userid+",%' " 
				   + " or concat(',',historyUserId,',') like '%,"+userid+",%'" ;
			
			pp.setTableID("meetingOrderListPass");
			
		}else if("notPass".equalsIgnoreCase(opt)){
			model = new ModelAndView(listNotPass);
			sql =  " select mo.uuid,mo.name as moname,(select name from k_meetingroom mr mr.uuid=mo.meetingRoomId) as roomname,mo.title,mo.event,mo.startTime,mo.endTime, "
				   + " mo.requirements,mo.equipment,mo.createDate,mo.auditDate,mo.status,u.name as username "
				   + " from k_meetingOrder mo "
				   + " left join (select meetingOrderid," 
				   + " group_concat(distinct waitingUserId order by waitingUserId) as  waitingUserId, "
				   + " group_concat(distinct inituserId order by initUserId) as  initUserId, "
				   + " group_concat(distinct actualUserId order by actualUserId) as  actualUserId, "
				   + " group_concat(distinct historyUserId order by historyUserId) as  historyUserId "
				   + " from k_meetingJoiner group by meetingOrderid ) mj on mo.uuid=mj.meetingOrderid " 
				   + " left join k_user u on mo.createUserId=u.id  "
				   + " where mo.status='不通过' and mo.createUserId='"+userid+"' or mo.auditUserId='"+userid+"' " 
				   + " or concat(',',waitingUserId,',') like '%,"+userid+",%' " 
				   + " or concat(',',initUserId,',') like '%,"+userid+",%' " 
				   + " or concat(',',actualUserId,',') like '%,"+userid+",%' " 
				   + " or concat(',',historyUserId,',') like '%,"+userid+",%'" ;
			
			pp.setTableID("meetingOrderListNotPass");
			
		}
		
		

		
		
		pp.addColumn("会议名称", "moname");
		pp.addColumn("会议室名称", "roomname");
		pp.addColumn("会议起始时间", "startTime");
		pp.addColumn("结束起始时间", "endTime");
		pp.addColumn("会议发起时间", "createDate");
		pp.addColumn("会议发起人", "username");
		pp.addColumn("状态", "status");
		
		pp.setPageSize_CH(50);
		
		pp.setOrderBy_CH("createDate");
		pp.setDirection_CH("desc");
		
		pp.setInputType("radio");
		pp.setWhichFieldIsValue(1);
		
		pp.setCustomerId("") ;
		
		pp.setTrActionProperty(true) ;
		pp.setTrAction(" uuid=${uuid} ") ;
		
		pp.setSQL(sql.toString());
		
		System.out.println("opt="+opt+ "  ||　会议 list total  sql="+sql);

		request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		
		return model;
		
	}
	
	
	
	/**
	 * 待审核、已通过、不通过
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView listTotal(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		
		ModelAndView model = new ModelAndView(listTotal) ;

		return model;
		
	}
	
	
	
	/**
	 * 待审核  行政部会议管理员批准，还没有被 审核的
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView goAudit(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn = null;
		ModelAndView model = new ModelAndView(listAudit);
		
		try {
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String userid = userSession.getUserId(); 
			String departmentid = userSession.getUserAuditDepartmentId(); 
			
			conn = new DBConnect().getConnect("");
			String menuid = StringUtil.showNull(request.getParameter("menuid")); //菜单ID
			if("".equals(menuid)) menuid = "10000690";
	    	String departments = new UserPopedomService(conn).getUserIdPopedom(userid, menuid);
	    	
			ASFuntion af = new ASFuntion();
			String uuId = af.showNull(request.getParameter("uuId"));
			
			DataGridProperty pp = new DataGridProperty();
		
			String sql = " select mo.uuid,mo.name as moname,(select name from k_meetingroom mr where mr.uuid = mo.meetingRoomId ) as roomname,mo.title,mo.event,mo.startTime,mo.endTime, "
					   + " mo.requirements,mo.equipment,mo.createDate,mo.auditDate,mo.status1,u.name as username "
					   + " from k_meetingOrder mo  "
					   + " left join k_user u on mo.createUserId=u.id  " 
					   + " where mo.status1='待审核' and mo.status='已发起' "+
					  // + " and (u.departmentid = '"+departmentid+"' or u.departmentid in ("+departments+")) " +
					   	 " and mo.auditUserId='"+userid+"'";
				
		System.out.println(sql);
			
			pp.addColumn("会议名称", "moname");
			pp.addColumn("会议室名称", "roomname");
			pp.addColumn("会议起始时间", "startTime");
			pp.addColumn("结束起始时间", "endTime");
			pp.addColumn("会议发起时间", "createDate");
			pp.addColumn("会议发起人", "username");
			pp.addColumn("状态", "status1");
			
			pp.setPageSize_CH(50);
			
			pp.setTableID("meetingOrderListAudit");
			
			pp.setOrderBy_CH("createDate");
			pp.setDirection_CH("desc");
			
			pp.setInputType("radio");
			pp.setWhichFieldIsValue(1);
			
			pp.setCustomerId("") ;
			
			pp.setTrActionProperty(true) ;
			pp.setTrAction(" uuid=${uuid} ") ;
			
			pp.setSQL(sql.toString());
			
			System.out.println(" 待审核 ||　会议 list total  sql="+sql);

			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
			
			model.addObject("uuId", uuId);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		
		
		return model;
		
	}
	
	
	/**
	 * 已通过
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView goPass(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn = null;
		ModelAndView model = new ModelAndView(listPass);
		
		try {
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String userid = userSession.getUserId();
			String departmentid = userSession.getUserAuditDepartmentId(); 
			
			conn = new DBConnect().getConnect("");
			String menuid = StringUtil.showNull(request.getParameter("menuid")); //菜单ID
			if("".equals(menuid)) menuid = "10000690";
	    	String departments = new UserPopedomService(conn).getUserIdPopedom(userid, menuid);
	    	
			DataGridProperty pp = new DataGridProperty();
		
			String sql = " select (SELECT NAME FROM k_meetingroom mr WHERE mr.uuid = mo.meetingRoomId ) AS roomname, mc.batchNumber,mo.uuid,mo.meetingRoomId,mo.name as moname,mo.title,mo.title as motitle,mo.event,mo.startTime,mo.endTime, "
					   + " mo.requirements,mo.equipment,mo.createDate,mo.auditDate,mo.status,u.name as username "
					   + " from k_meetingOrder mo "
					   //+ " left join (select batchNumber,meetingOrderId from k_meetingConsumable group by meetingOrderId,batchNumber) mc on mo.name=mc.meetingOrderId "
					   + " left join (select max(batchNumber) as batchNumber,meetingOrderId from k_meetingConsumable group by meetingOrderId) mc on mo.name=mc.meetingOrderId "
					   + " left join (select meetingOrderid," 
					   + " group_concat(distinct waitingUserId order by waitingUserId) as  waitingUserId, "
					   + " group_concat(distinct inituserId order by initUserId) as  initUserId, "
					   + " group_concat(distinct actualUserId order by actualUserId) as  actualUserId, "
					   + " group_concat(distinct historyUserId order by historyUserId) as  historyUserId "
					   + " from k_meetingJoiner group by meetingOrderid ) mj on mo.uuid=mj.meetingOrderid " 
					   + " left join k_user u on mo.createUserId=u.id  "
					   + " where mo.status='已通过' and ( mo.createUserId='"+userid+"' or mo.auditUserId='"+userid+"' " 
					   + " or concat(',',waitingUserId,',') like '%,"+userid+",%' " 
					   + " or concat(',',initUserId,',') like '%,"+userid+",%' " 
					   + " or concat(',',actualUserId,',') like '%,"+userid+",%' " 
					   + " or concat(',',historyUserId,',') like '%,"+userid+",%' )" 
					  // + " and (u.departmentid = '"+departmentid+"' or u.departmentid in ("+departments+")) " +
						+ " and mo.auditUserId='"+userid+"'";
			
			pp.setTableID("meetingOrderListPass");
			
			pp.addColumn("会议名称", "moname");
			pp.addColumn("会议室名称", "roomname");
			pp.addColumn("会议起始时间", "startTime");
			pp.addColumn("结束起始时间", "endTime");
			pp.addColumn("会议发起时间", "createDate");
			pp.addColumn("会议发起人", "username");
			pp.addColumn("状态", "status");
			
			pp.setPageSize_CH(50);
			
			pp.setOrderBy_CH("createDate");
			pp.setDirection_CH("desc");
			
			pp.setInputType("radio");
			pp.setWhichFieldIsValue(1);
			
			pp.setCustomerId("") ;
			
			pp.setTrActionProperty(true) ;
			pp.setTrAction(" uuid=${uuid} ") ;
			
			pp.setSQL(sql.toString());
			
			System.out.println("已通过 ||　会议 list total  sql="+sql);

			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		
		
		return model;
		
	}
	
	
	/**
	 * 不通过
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView goNotPass(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		
		ModelAndView model = new ModelAndView(listNotPass);
		
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		String userid = userSession.getUserId();
		
		DataGridProperty pp = new DataGridProperty();
	
		String sql = " select mo.uuid,mo.name as moname,mo.meetingRoomId,mo.title,mo.event,mo.startTime,mo.endTime, "
				   + " mo.requirements,mo.equipment,mo.createDate,mo.auditDate,mo.status,u.name as username "
				   + " from k_meetingOrder mo "
				   + " left join (select meetingOrderid," 
				   + " group_concat(distinct waitingUserId order by waitingUserId) as  waitingUserId, "
				   + " group_concat(distinct inituserId order by initUserId) as  initUserId, "
				   + " group_concat(distinct actualUserId order by actualUserId) as  actualUserId, "
				   + " group_concat(distinct historyUserId order by historyUserId) as  historyUserId "
				   + " from k_meetingJoiner group by meetingOrderid ) mj on mo.uuid=mj.meetingOrderid " 
				   + " left join k_user u on mo.createUserId=u.id  "
				   + " where mo.status='不通过' and ( mo.createUserId='"+userid+"' or mo.auditUserId='"+userid+"' " 
				   + " or concat(',',waitingUserId,',') like '%,"+userid+",%' " 
				   + " or concat(',',initUserId,',') like '%,"+userid+",%' " 
				   + " or concat(',',actualUserId,',') like '%,"+userid+",%' " 
				   + " or concat(',',historyUserId,',') like '%,"+userid+",%' )" ;
		
		pp.setTableID("meetingOrderListNotPass");
		
		pp.addColumn("会议名称", "moname");
		pp.addColumn("会议室名称", "meetingRoomId");
		pp.addColumn("会议起始时间", "startTime");
		pp.addColumn("结束起始时间", "endTime");
		pp.addColumn("会议发起时间", "createDate");
		pp.addColumn("会议发起人", "username");
		pp.addColumn("状态", "status");
		
		pp.setPageSize_CH(50);
		
		pp.setOrderBy_CH("createDate");
		pp.setDirection_CH("desc");
		
		pp.setCustomerId("") ;
		
		pp.setTrActionProperty(true) ;
		pp.setTrAction(" uuid=${uuid} ") ;
		
		pp.setSQL(sql.toString());
		
		System.out.println("不通过 ||　会议 list total  sql="+sql);

		request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);

		return model;
		
	}
	
	
	/**
	 * 跳到到 审核 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView toAuidt(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView model = new ModelAndView(audit);
		
		ASFuntion af = new ASFuntion();
		
		String id = af.showNull(request.getParameter("taskId"));
		
		Connection conn = null;
		
		MeetingOrder mo = null;
		
		try {
			
			conn = new DBConnect().getConnect("");
			
			// 会议预约
			MeetingOrderService mos = new MeetingOrderService(conn);
			mo = mos.getMeetingOrder(id);
			
			// 根据 参与人 找出参与人所在 部门 去 勾 部门树
			DbUtil du = new DbUtil(conn);
			
			String sql = " select initUserId,name from k_meetingjoiner a,k_user b where meetingOrderId ='"+id+"' and a.inituserid = b.id ";
			
			List list = mos.getListBySql(sql);
			
			String joinUser = "";
			String joinUserName = "";
			String joinUserDepartmentId = "";
			String departmengId = "";
			if(list.size()>0){
				sql = " select departmentid from k_user where id=? ";
				for (Object object : list) {
					joinUser = joinUser + ((Map)object).get("inituserid")+",";
					joinUserName += ((Map)object).get("name")+",";
					departmengId = du.queryForString(sql, new Object[]{((Map)object).get("inituserid")});
					if(joinUserDepartmentId.indexOf(","+departmengId+",")<0){
						joinUserDepartmentId = joinUserDepartmentId + departmengId + "," ;
					}
				}
			}
			
			
			model.addObject("mo",mo);
			if(joinUser.indexOf(",")>-1){
				model.addObject("joinUser",joinUser.subSequence(0,joinUser.length()-1));
			}
		
			model.addObject("joinUserDepartmentId",joinUserDepartmentId);
			model.addObject("joinUserName",joinUserName); //已参加人
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn);
		}

		return model;
	
	}

	/**
	 * 审核 （是否通过）
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView saveAuidt(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		String userid = userSession.getUserId();
		String userName = userSession.getUserName();
		
		ASFuntion af = new ASFuntion();
		
		String id = af.showNull(request.getParameter("uuid"));
		String auditDate = af.getCurrentDate();
		String status = af.showNull(request.getParameter("status"));
		// 不通过原因
		String reason = af.showNull(request.getParameter("reason"));
		
		
		
		Connection conn = null;
		
		MeetingOrder mo = new MeetingOrder();
		mo.setUuid(id);
		mo.setAuditUserId(userid);
		mo.setAuditDate(auditDate);
		mo.setStatus(status);
		mo.setReason(reason);
		
		
		try {
			
			conn = new DBConnect().getConnect("");
			
			// 会议预约
			MeetingOrderService mos = new MeetingOrderService(conn);
			
			// 审核
			mos.audit(mo);
			
			// 审核通过了 发通知 到 会议参与人
			if("已通过".equals(status)){
				mo = mos.getMeetingOrder(id);
				String projectName = mo.getName();
				
				// 发通知 到 参与人 ： 这里 需要 算 出 所有 应该 收到 短消息的 人 
				List list = new MeetingJoinnerService(conn).getMeetingJoinnerList(mo.getUuid()); 
				
				PlacardService ps = new PlacardService(conn);
				
				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					MeetingJoinner mj = (MeetingJoinner) iterator.next();
					
					PlacardTable placard = new PlacardTable();
					
					placard.setCaption(projectName);
					
					// XXXXXX日XX时间在XXXX会议室举行的会议：会议标题。
					placard.setMatter(userName+"邀请您参与"+mo.getCreateDate()+"在"+mo.getMeetingRoomId()+"会议室举行的会议："+mo.getTitle());
					
					placard.setIsReversion(0);
					placard.setAddresser(userid);
					
					// 会议初始参与人：  这里 需要 算 出 所有 应该 收到 短消息的 人 
					placard.setAddressee(mj.getInitUserId());
					
					placard.setAddresserTime(af.getCurrentDate() + " " + af.getCurrentTime());
					placard.setIsRead(0);
	
					ps.AddPlacard(placard);
				}
			}
			
			if("已通过".equals(status)){
				response.sendRedirect(request.getContextPath()+"/meetingOrder.do?method=goAudit&uuId="+id);
			}else{
				response.sendRedirect(request.getContextPath()+"/meetingOrder.do?method=goAudit");
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn);
		}

		return null;
	
	}
	
	
	/**
	 * 获取部门列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getDepartmentList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		response.setContentType("text/html;charset=utf-8");
		
		String type = request.getParameter("type");
		String joinUser = request.getParameter("joinUser");
		String departmentId = request.getParameter("departmentId");
		
		UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
		
		Connection conn = null;
		
		StringBuffer sb = new StringBuffer();

		sb.append("[");

		try {
			
			conn = new DBConnect().getConnect("");
			
			String departmentIds = ",-9999999,";
			String userIds = ",-9999999,";
			
			if("department".equals(type)) {
					
				// 不用 默认勾选中 当前登录 人员 
				userIds += joinUser + ",";
				
				boolean check = false;
				
				//无部门人员
				if("0".equals(departmentId)) {
					departmentId = "";
				}
				
				List userList = new DepartmentService(conn).getUserList(departmentId);
				
				if(userList != null) {
					
					for(int j=0; j < userList.size(); j++) {
						User user = (User)userList.get(j);
						
						check = userIds.indexOf("," + user.getId() + ",") > -1;
						
						sb.append(" {cls:'file',")
							.append("leaf:true,")
							.append("checked:").append(check).append(",")
							.append("children:null,")
							.append("type:'user',")
							.append("icon:'img/").append(("M".equals(user.getSex()) || "男".equals(user.getSex())) ? "male.gif" : "female.gif").append("', ")
							.append("id:'user_").append(user.getId()).append("',")
							.append("departmentId:'").append(departmentId).append("',")
							.append("roleName:'").append(user.getRoles()).append("',")
							.append("userName:'").append(user.getName()).append("',")
							.append("userId:'").append(user.getId()).append("',")
							.append("text:'").append(user.getName()).append("'} ");
						if(j != userList.size()-1) {
							sb.append(",");
						}
					}
				}
			} else {
				List departmentList = new DepartmentService(conn).getDepartmentList(userSession.getUserAuditDepartmentId());

				departmentIds += ("".equals(userSession.getUserAuditDepartmentId()) ? "0" : userSession.getUserAuditDepartmentId()) + ",";
				
				// 对照 参与人字段，参与人有哪些就勾选哪些人
				// departmentIds = "0,";
				departmentIds = ","+request.getParameter("joinUserDepartmentId");
				
				boolean check = false;
				
				for(int i=0; i < departmentList.size(); i++) {
					DepartmentVO departmentVO = (DepartmentVO)departmentList.get(i);
					check = departmentIds.indexOf("," + departmentVO.getAutoId() + ",") > -1;
					
					sb.append(" { ")
						.append("cls:'folder',")
						.append("leaf:false,")
						.append("type:'department',")
						.append("departmentId:'").append(departmentVO.getAutoId()).append("',")
						.append("checked:").append(check).append(", ")
						.append("id:'department_" + departmentVO.getAutoId()).append("',")
						.append("text:'").append(departmentVO.getDepartmentName()).append("' ");
					
					sb.append("}");
					if(i != departmentList.size()-1) {
						sb.append(",");
					}
				}
				
			}
			sb.append("]");
			
			System.out.println("  sb.toString()="+sb.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		response.getWriter().write(sb.toString());
		return null;
	}
	
	
	
	/**
	 * 跳转
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView go(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		
		ModelAndView model = new ModelAndView(addAndEdit) ;

		ASFuntion af = new ASFuntion();
		
		String opt = af.showNull(request.getParameter("opt"));
		String id = af.showNull(request.getParameter("id"));
		
		Connection conn = null;
		
		MeetingOrder mo = null;
		
		try {
			
			conn = new DBConnect().getConnect("");
			
			
			if("add".equalsIgnoreCase(opt)){
				mo = new MeetingOrder();
				mo.setUuid(UUID.randomUUID().toString());
				mo.setAttachFileId(UUID.randomUUID().toString());
				
				model.addObject("mo",mo);
			}else{
				// 会议预约
				MeetingOrderService mos = new MeetingOrderService(conn);
				mo = mos.getMeetingOrder(id);
				
				// 根据 参与人 找出参与人所在 部门 去 勾 部门树
				DbUtil du = new DbUtil(conn);
				
				String sql = " select a.initUserId,u.name from k_meetingjoiner a left join k_user u on u.id=a.initUserId where meetingOrderId = '"+id+"' ";
				
				List list = mos.getListBySql(sql);
				
				String joinUser = "";
				String joinUserName="";
				String joinUserDepartmentId = "";
				String departmengId = "";
				if(list.size()>0){
					sql = " select departmentid from k_user where id=? ";
					for (Object object : list) {
						joinUserName +=((Map)object).get("name")+",";
						joinUser = joinUser + ((Map)object).get("inituserid")+",";
						departmengId = du.queryForString(sql, new Object[]{((Map)object).get("inituserid")});
						if(joinUserDepartmentId.indexOf(","+departmengId+",")<0){
							joinUserDepartmentId = joinUserDepartmentId + departmengId + "," ;
						}
					}
				}
				
				MeetingOrder meetingorder = mos.getMeetingOrder(id);
				
			
				
				StringBuffer sb=new StringBuffer(joinUserName);
				StringBuffer sf=new StringBuffer(joinUser);
				
				if(sb.indexOf(",")>-1){
					sb.deleteCharAt(sb.lastIndexOf(","));
				}
				
				if(sf.indexOf(",")>-1){
					sf.deleteCharAt(sf.lastIndexOf(","));
				}
				
				
				model.addObject("meetingorder",meetingorder);
				model.addObject("mo",mo);
				model.addObject("joinUser",sf.toString());
				model.addObject("joinUserName",sb.toString());
				model.addObject("joinUserDepartmentId",joinUserDepartmentId);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn);
		}
		model.addObject("opt",opt);
		return model;
		
	}
	
	/**
	 * 得到发起状态
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getStatus(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);

		Connection conn = null;

		String id = request.getParameter("uuid");
		
		PrintWriter out = response.getWriter();

		try{
			conn = new DBConnect().getConnect("");

			MeetingOrderService mos = new MeetingOrderService(conn);
			MeetingOrder mo = mos.getMeetingOrder(id);
			
			if("已发起".equals(mo.getStatus())){
				out.write("已发起");
			}else if("已通过".equals(mo.getStatus())){
				out.write("已通过");
			}else if("不通过".equals(mo.getStatus())){
				out.write("不通过");
			}else{
				out.write("未发起");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}
	
	
	/**
	 * 添加和修改保存
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView save(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		
		UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
		
		ASFuntion af = new ASFuntion();
		
		String opt = af.showNull(request.getParameter("opt"));
		
		
		String uuid = af.showNull(request.getParameter("uuid"));
		String name = af.showNull(request.getParameter("name"));
		String title = af.showNull(request.getParameter("title"));
		String meetingRoomId = af.showNull(request.getParameter("meetingRoomId"));
		String event = af.showNull(request.getParameter("event"));
		String startTime = af.showNull(request.getParameter("startTime"));
		String endTime = af.showNull(request.getParameter("endTime"));
		
		String requirements = af.showNull(request.getParameter("requirements"));
		String equipment = af.showNull(request.getParameter("equipment"));
		String departmentId = af.showNull(request.getParameter("departmentId"));
		String attachFileId = af.showNull(request.getParameter("attachFileId"));
		String describes = af.showNull(request.getParameter("describes"));
		
		String joinUser = af.showNull(request.getParameter("joinUser"));
		String auditUserId=af.showNull(request.getParameter("auditUserId"));
		
		
		
		String createUserId = userSession.getUserId();
		String createDate = af.getCurrentDate();
		
		Connection conn = null;
		
		MeetingOrder mo = new MeetingOrder();
		mo.setUuid(uuid);
		mo.setName(name);
		mo.setTitle(title);
		mo.setMeetingRoomId(meetingRoomId);
		mo.setEvent(event);
		mo.setStartTime(startTime);
		mo.setEndTime(endTime);
		
		
		mo.setRequirements(requirements);
		mo.setEquipment(equipment);
		mo.setDepartmentId(departmentId);
		mo.setAttachFileId(attachFileId);
		mo.setDescribes(describes);
		mo.setCreateDate(createDate);
		mo.setCreateUserId(createUserId);
		mo.setAuditUserId(auditUserId);
		mo.setStatus("未发起");
		
		try {
			
			conn = new DBConnect().getConnect("");
			
			MeetingOrderService mos = new MeetingOrderService(conn);
			
			if("add".equalsIgnoreCase(opt)){
				// 添加
				mos.addMeetingOrder(mo);
			}else{
				// 修改
				mos.updateMeetingOrder(mo);
			}
			
			// 会议参与人  k_meetingjoiner   joinUser
			MeetingJoinner mj = null;
			
			MeetingJoinnerService mjs = new MeetingJoinnerService(conn);
			
			if(joinUser.indexOf(",")>-1){
				String[] joinUsers = joinUser.split(",");
				
				// 删除 该会议的参与人
				mjs.deleteByMeetingOrderId(uuid);
				
				for (int i = 0; i < joinUsers.length; i++) {
					
					mj = new MeetingJoinner();
					
					mj.setUuid(UUID.randomUUID().toString());
					mj.setMeetingOrderId(uuid);
					mj.setWaitingUserId(joinUsers[i]);
					mj.setInitUserId(joinUsers[i]);
					mj.setBatchNumber(UUID.randomUUID().toString());
					mjs.addMeetingJoinner(mj);
				}
			}
		
			response.sendRedirect(request.getContextPath()+"/meetingOrder.do?method=list");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn);
		}

		
		return null;
		
	}
	
	/**
	 * 将未发起改为已发起
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView updateStatu(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		
		UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
		
		ASFuntion af = new ASFuntion();
		String uuid = af.showNull(request.getParameter("uuid"));
		
		String createDate = af.getCurrentDate();
		Connection conn = null;
		MeetingOrder mo = new MeetingOrder();
		DbUtil dbUtil=null;
		
		try {
			
			conn = new DBConnect().getConnect("");
			dbUtil=new DbUtil(conn);
			mo = dbUtil.load(mo, uuid);
			mo.setStatus("已发起");
			int i = dbUtil.update(mo);
			
			//response.sendRedirect(request.getContextPath()+"/meetingOrder.do?method=list");
			if(i==1){
				response.getWriter().write("true");
			}else {
				response.getWriter().write("false");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn);
		}

		
		return null;
		
	}
	
	
	/**
	 * 删除
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView delete(HttpServletRequest request, HttpServletResponse response){
		String id = request.getParameter("id");
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect("");
			
			MeetingOrderService mos = new MeetingOrderService(conn);
			
			// 删除会议
			mos.deleteMeetingOrder(id);
		
			response.sendRedirect(request.getContextPath()+"/meetingOrder.do?method=list");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn);
		}
		return null;
	}
	
	
	
	
	/**
	 * 查看详细 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView view(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView model = new ModelAndView(view);
		
		ASFuntion af = new ASFuntion();
		
		String id = af.showNull(request.getParameter("id"));
		String isSee = af.showNull(request.getParameter("isSee"));
		
		Connection conn = null;
		
		MeetingOrder mo = null;
		
		try {
			
			conn = new DBConnect().getConnect("");
			
			// 会议预约
			MeetingOrderService mos = new MeetingOrderService(conn);
			mo = mos.getMeetingOrder(id);
			
			// 根据 参与人 找出参与人所在 部门 去 勾 部门树
			DbUtil du = new DbUtil(conn);
			
			String sql = " select initUserId from k_meetingjoiner where meetingOrderId = '"+id+"' ";
			
			List list = mos.getListBySql(sql);
			
			String joinUser = "";
			
			String joinUserDepartmentId = "";
			String departmengId = "";
			if(list.size()>0){
				sql = " select departmentid from k_user where id=? ";
				for (Object object : list) {
					joinUser = joinUser + ((Map)object).get("inituserid")+",";
					departmengId = du.queryForString(sql, new Object[]{((Map)object).get("inituserid")});
					if(joinUserDepartmentId.indexOf(","+departmengId+",")<0){
						joinUserDepartmentId = joinUserDepartmentId + departmengId + "," ;
					}
				}
			}
			
			
			model.addObject("mo",mo);
			if(joinUser.indexOf(",")>-1){
				model.addObject("joinUser",joinUser.subSequence(0,joinUser.length()-1));
			}
			model.addObject("joinUserDepartmentId",joinUserDepartmentId);
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn);
		}

		model.addObject("isSee", isSee);
		return model;
	
	}
	
	

	/**
	 * 我参与的且审批通过的会议
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView myMeetingOrderList(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn = null;
		ModelAndView model = new ModelAndView(myList) ;
		
		try {
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String userid = userSession.getUserId();
			String departmentid = userSession.getUserAuditDepartmentId(); 
			
			conn = new DBConnect().getConnect("");
			String menuid = StringUtil.showNull(request.getParameter("menuid")); //菜单ID
			if("".equals(menuid)) menuid = "10000682";
	    	String departments = new UserPopedomService(conn).getUserIdPopedom(userid, menuid);
	    	
			DataGridProperty pp = new DataGridProperty();

			
			String sql = "select DIStinct(mo.uuid),mo.name as moname,(SELECT NAME FROM k_meetingroom WHERE UUID=mo.meetingRoomId) AS roomname,mo.title," +
					"mo.event,mo.startTime,mo.endTime,mo.requirements,mo.equipment,mo.createDate," +
					"mo.auditDate,mo.status,u.name as username,mj.optStatus  " +
					"from k_meetingOrder mo  left join k_meetingJoiner mj on mo.uuid=mj.meetingOrderid  " +
					"left join k_user u on mj.waitingUserId=u.id   where mo.status='已通过' and  waitingUserId = '"+userid+"' " +
					" and (u.departmentid = '"+departmentid+"' or u.departmentid in ("+departments+")) ";; 
			
		   System.out.println(sql);
			pp.addColumn("会议名称", "moname");
			pp.addColumn("会议室名称", "roomname");
			pp.addColumn("会议起始时间", "startTime");
			pp.addColumn("结束起始时间", "endTime");
			pp.addColumn("会议发起时间", "createDate");
			pp.addColumn("会议发起人", "username");
			pp.addColumn("状态", "status");
			pp.addColumn("操作状态", "optStatus");
			
			pp.setTableID("myMeetingOrderList");
			
			pp.setPageSize_CH(50);
			
			pp.setOrderBy_CH("startTime");
			pp.setDirection_CH("desc");
			
			pp.setInputType("radio");
	     	pp.setWhichFieldIsValue(1);
			
			pp.setCustomerId("") ;
			
			pp.setTrActionProperty(true) ;
			pp.setTrAction(" uuid=${uuid} ") ;
			
			pp.setSQL(sql.toString());
			
			System.out.println("  我参与 会议 list  sql="+sql);

			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		return model;
	}
	
//	public ModelAndView myMeetingOrderList(HttpServletRequest request, HttpServletResponse response)  throws Exception {
//		
//		ModelAndView model = new ModelAndView(myList) ;
//		
//		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
//		String userid = userSession.getUserId();
//		
//		String opt = request.getParameter("opt");
//
//		DataGridProperty pp = new DataGridProperty();
//
//		
//		String sql = " select mo.uuid,mo.name as moname,(select name from k_meetingroom mm where mm.uuid=mo.meetingRoomId) as aa,mo.title,mo.event,mo.startTime,mo.endTime, "
//				   + " mo.requirements,mo.equipment,mo.createDate,mo.auditDate,mo.status,u.name as username "
//				   + " from k_meetingOrder mo "
//				   + " left join " 
//				   + "(select meetingOrderid," 
//				   + " group_concat(distinct waitingUserId order by waitingUserId) as  waitingUserId, "
//				   + " group_concat(distinct inituserId order by initUserId) as  initUserId, "
//				   + " group_concat(distinct actualUserId order by actualUserId) as  actualUserId, "
//				   + " group_concat(distinct historyUserId order by historyUserId) as  historyUserId "
//				   + " from k_meetingJoiner group by meetingOrderid " 
//				   + ") mj on mo.uuid=mj.meetingOrderid " 
//				   + " left join k_user u on mo.createUserId=u.id  "
//				   + " where (mo.createUserId='"+userid+"' or mo.auditUserId='"+userid+"' " 
//				   + " or concat(',',waitingUserId,',') like '%,"+userid+",%' " 
//				   + " or concat(',',initUserId,',') like '%,"+userid+",%' " 
//				   + " or concat(',',actualUserId,',') like '%,"+userid+",%' " 
//				   + " or concat(',',historyUserId,',') like '%,"+userid+",%') and mo.status='已通过'";
//		
//		pp.addColumn("会议名称", "moname");
//		pp.addColumn("会议室名称", "aa");
//		pp.addColumn("会议起始时间", "startTime");
//		pp.addColumn("结束起始时间", "endTime");
//		pp.addColumn("会议发起时间", "createDate");
//		pp.addColumn("会议发起人", "username");
//		pp.addColumn("状态", "status");
//		pp.addColumn("操作状态", "optStatus");
//		
//		pp.setTableID("myMeetingOrderList");
//		
//		pp.setPageSize_CH(50);
//		
//		pp.setOrderBy_CH("startTime");
//		pp.setDirection_CH("desc");
//		
//		pp.setInputType("radio");
//		pp.setWhichFieldIsValue(1);
//		
//		pp.setCustomerId("") ;
//		
//		pp.setTrActionProperty(true) ;
//		pp.setTrAction(" uuid=${uuid} ") ;
//		
//		pp.setSQL(sql.toString());
//		
//		System.out.println("  我参与 会议 list  sql="+sql);
//
//		request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
//
//		return model;
//		
//	}
	
	/**
	 * 跳转到：同意、不同意、更换
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView goOpt(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
		String userId = userSession.getUserId();
		
		ModelAndView model = new ModelAndView(optAddAndEdit) ;

		ASFuntion af = new ASFuntion();
		
		String id = af.showNull(request.getParameter("id"));
		
		Connection conn = null;
		
		MeetingOrder mo = null;
		
		try {
			
			conn = new DBConnect().getConnect("");
			
			// 会议预约
			MeetingOrderService mos = new MeetingOrderService(conn);
			mo = mos.getMeetingOrder(id);
			
			String reason = new DbUtil(conn).queryForString(" select reason from k_meetingjoiner where meetingOrderId=? and waitingUserId=? ",new Object[]{id,userId});
			
			model.addObject("reason",reason);
			model.addObject("mo",mo);
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn);
		}

		return model;
		
	}
	
	/**
	 * 保存：同意、不同意、更换
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView saveOpt(HttpServletRequest request, HttpServletResponse response)  throws Exception {

		UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
		String userId = userSession.getUserId();
		
		ASFuntion af = new ASFuntion();
		
		String opt = af.showNull(request.getParameter("opt"));
		String id = af.showNull(request.getParameter("uuid"));
		
		// 更换人
		String joinUser = af.showNull(request.getParameter("joinUser"));
		// 原因
		String reason = af.showNull(request.getParameter("reason"));
		
		Connection conn = null;
		
		try {
			
			conn = new DBConnect().getConnect("");

			MeetingJoinnerService mjs = new MeetingJoinnerService(conn);
			MeetingJoinner mj = new MeetingJoinner();
			
			//    更加 会议 id 和 等待人进行修改
			
			// 点击同意：修改会议【会议参与人表】的答复参与人、答复参与时间；
			// 点击不同意：修改会议【会议参与人表】的答复参与人（拒绝）、答复参与时间
			// 点击更换人：选人，要按三个步骤来取:
			//			A:UPDATE会议参与人表：；
			//				autoid、会议ID、会议参与人(被邀请人)、实际参与人、答复参与时间、历史参与人（= concat(历史参与人,会议参阅人)）；
			//		  	B:把当前的这条记录修改为：
			//				答复参与人：转邀请人，答复参与时间；实际时间；
			//		  	C：在生成一条新的通知信息；
			
			if("agree".equalsIgnoreCase(opt)){
				mj.setMeetingOrderId(id);
				mj.setWaitingUserId(userId);
				mj.setAnswerUserId(userId);
				mj.setAnswerTime(af.getCurrentDate());
				mj.setOptStatus("同意");
				mj.setReason("");

				// 同意
				mjs.updateAgree(mj);
				
			}else if("disagree".equalsIgnoreCase(opt)){
				mj.setMeetingOrderId(id);
				mj.setWaitingUserId(userId);
				mj.setAnswerUserId(userId);
				mj.setAnswerTime(af.getCurrentDate());
				mj.setOptStatus("不同意");
				mj.setReason(reason);
				
				// 不同意
				mjs.updateDisAgree(mj);
				
			}else{
				
				MeetingJoinner mjj = mjs.getMeetingJoinner(id, userId);
				String historyUserId = mjj.getHistoryUserId();
				String batchNumber = mjj.getBatchNumber();
				
				if(historyUserId==null || "null".equals(historyUserId)){
					historyUserId = "";
				}
				
				mj.setMeetingOrderId(id);
				mj.setWaitingUserId(userId);
				mj.setAnswerUserId(userId);
				mj.setAnswerTime(af.getCurrentDate());
				mj.setOptStatus("更换");
				
				if((","+historyUserId).indexOf(","+userId+",")<0){
					historyUserId = historyUserId + userId + ",";
				}
				if(joinUser.indexOf(",")>0){
					String[] jus = joinUser.split(",");
					for (int i = 0; i < jus.length; i++) {
						if((","+historyUserId).indexOf(","+jus[i]+",")<0){
							historyUserId = historyUserId + jus[i]+",";
						}
					}
				}else{
					if((","+historyUserId).indexOf(","+joinUser+",")<0){
						historyUserId = historyUserId + joinUser + ",";	
					}
				}
				mj.setHistoryUserId(historyUserId);
				
				mj.setReason(reason);
				
				// 更换
				mjs.updateChange(mj);
				
				// 增加 会议 参与人  和 发通知 到 更换人 
				if(joinUser.indexOf(",")>0){
					String[] joinUsers = joinUser.split(",");
					
					MeetingOrder mo = new MeetingOrderService(conn).getMeetingOrder(id); 
					
					// 会议的发起人
					String createUserName = new DbUtil(conn).queryForString(" select name from k_user where id=? ",new Object[]{mo.getCreateUserId()});
					
					PlacardService ps = new PlacardService(conn);
					
					for (int i = 0; i < joinUsers.length; i++) {
						mj = new MeetingJoinner();
						mj.setUuid(UUID.randomUUID().toString());
						mj.setMeetingOrderId(id);
						mj.setWaitingUserId(joinUsers[i]);
						mj.setInitUserId(userId);
						mj.setBatchNumber(batchNumber);
						
						// 增加
						mjs.addMeetingJoinner(mj);
						
						
						// 发通知给更换后的参与人
						PlacardTable placard = new PlacardTable();
						
						placard.setCaption(mo.getName());
						
						// XXXXXX日XX时间在XXXX会议室举行的会议：会议标题。
						placard.setMatter(createUserName+"邀请您参与"+mo.getCreateDate()+"在"+mo.getMeetingRoomId()+"会议室举行的会议："+mo.getTitle());
						
						placard.setIsReversion(0);
						placard.setAddresser(userId);
						
						placard.setAddressee(joinUsers[i]);
						
						placard.setAddresserTime(af.getCurrentDate() + " " + af.getCurrentTime());
						placard.setIsRead(0);
		
						ps.AddPlacard(placard);
						
					}
				}
				
			}
			response.sendRedirect(request.getContextPath()+"/meetingOrder.do?method=myMeetingOrderList");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn);
		}
		return null;
	}
	
	
	
	/**
	 * 得到审核状态
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getAuditStatus(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);

		Connection conn = null;

		String id = request.getParameter("id");
		
		PrintWriter out = response.getWriter();

		try{
			conn = new DBConnect().getConnect("");

			MeetingOrderService mos = new MeetingOrderService(conn);
			MeetingOrder mo = mos.getMeetingOrder(id);
			
			if("已通过".equals(mo.getStatus())){
				
				out.write("Y");
			}else{
				out.write("N");
			
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}
	
	
	/**
	 * 打印
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView goPrint(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		
		ModelAndView model = new ModelAndView(printTime) ;

		ASFuntion af = new ASFuntion();
		
		String id = af.showNull(request.getParameter("id"));
		
		Connection conn = null;
		
		MeetingOrder mo = null;
		
		try {
			
			conn = new DBConnect().getConnect("");
			
			// 会议预约
			MeetingOrderService mos = new MeetingOrderService(conn);
			mo = mos.getMeetingOrder(id);
			
			// 找出会议部门
			DbUtil du = new DbUtil(conn);
			String sql = " select departname from k_department where autoid = ? ";
			String departname = du.queryForString(sql, new Object[]{mo.getDepartmentId()});
			
			// 找出参与人员
			sql = " select group_concat(distinct name order by name asc) as waitingUserName "
				+ " from( "
				+ " select meetingOrderId,waitingUserid,u.name from k_meetingjoiner m "  
				+ " left join k_user u on m.waitingUserid = u.id  where meetingOrderId=? " 
				+ " ) t ";
			
			String joinUser = du.queryForString(sql, new Object[]{mo.getUuid()});
			
			// 会议时间
			String startTime = af.showNull(mo.getStartTime());
			String endTime = af.showNull(mo.getEndTime());
			
			if(startTime.length()>=15){
				startTime = startTime.substring(0,4)+"年  "+startTime.substring(5,7)+"月  "+startTime.substring(8,10)+"日  ";
				if(Integer.parseInt(mo.getStartTime().substring(11, 13))>12){
					if((Integer.parseInt(mo.getStartTime().substring(11, 13))-12)>=10){
						startTime = startTime + "下午 "+(Integer.parseInt(mo.getStartTime().substring(11, 13))-12)+":"+mo.getStartTime().substring(14, 16);
					}else{
						startTime = startTime + "下午 0"+(Integer.parseInt(mo.getStartTime().substring(11, 13))-12)+":"+mo.getStartTime().substring(14, 16);
					}
				}else{
					startTime = startTime + "上午 "+mo.getStartTime().substring(11, 13)+":"+mo.getStartTime().substring(14, 16);
				}
			}
			
			if(endTime.length()>=15){
				endTime = endTime.substring(0,4)+"年  "+endTime.substring(5,7)+"月  "+endTime.substring(8,10)+"日  ";
				if(Integer.parseInt(mo.getEndTime().substring(11, 13))>12){
					if((Integer.parseInt(mo.getEndTime().substring(11, 13))-12)>=10){
						endTime = endTime + "下午 "+(Integer.parseInt(mo.getEndTime().substring(11, 13))-12)+":"+mo.getEndTime().substring(14, 16);
					}else{
						endTime = endTime + "下午 0"+(Integer.parseInt(mo.getEndTime().substring(11, 13))-12)+":"+mo.getEndTime().substring(14, 16);
					}
				}else{
					endTime = endTime + "上午 "+mo.getEndTime().substring(11, 13)+":"+mo.getEndTime().substring(14, 16);
				}
			}
			
			
			model.addObject("mo",mo);
			model.addObject("meetingTime",startTime+" 至<br> "+endTime);
			model.addObject("departname",departname);
			model.addObject("joinUser",joinUser);
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn);
		}
		
		return model;
	}
	
	

	/**
	 * 得到会议名称
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getMeetingOrderNameById(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);

		ASFuntion af = new ASFuntion();
		
		Connection conn = null;

		String id = af.showNull(request.getParameter("id"));
		
		PrintWriter out = response.getWriter();

		try{
			conn = new DBConnect().getConnect("");

			MeetingOrderService mos = new MeetingOrderService(conn);
			MeetingOrder mo = mos.getMeetingOrder(id);
			
			String name = af.showNull(mo.getName());
			
			out.write(name);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}
	
}
