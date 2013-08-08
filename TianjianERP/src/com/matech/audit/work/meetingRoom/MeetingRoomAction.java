package com.matech.audit.work.meetingRoom;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.meetingRoom.MeetingRoomService;
import com.matech.audit.service.meetingRoom.model.MeetingRoom;
import com.matech.audit.service.userpopedom.UserPopedomService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.StringUtil;

public class MeetingRoomAction extends MultiActionController{
	private static final String list = "/MeetingRoom/list.jsp";
	private static final String listTotalCount = "/MeetingRoom/listTotalCount.jsp";
	private static final String listRoomCount = "/MeetingRoom/listRoomCount.jsp";
	private static final String listDepartCount = "/MeetingRoom/listDepartCount.jsp";
	private static final String listCount = "/MeetingRoom/listCount.jsp";
	private static final String addAndEdit = "MeetingRoom/addAndEdit.jsp";
	
	
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		System.out.println("进入到meetingroom中");
		Connection conn = null;
		ModelAndView model = new ModelAndView(list) ;
		try {
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String userid = userSession.getUserId(); 
			String areaid = userSession.getAreaid(); 
			
			conn = new DBConnect().getConnect("");
			String menuid = StringUtil.showNull(request.getParameter("menuid")); //菜单ID
			if("".equals(menuid)) menuid = "10000680";
	    	String departments = new UserPopedomService(conn).getUserIdPopedom(userid, menuid);
	    	
			DataGridProperty pp = new DataGridProperty();

			
			String sql = " select uuid,name,organ,containPerson,device,describes,place,creatorId,createTime,property from k_meetingRoom " 
				       + " where 1=1 and (organ = '"+areaid+"' or organ in (select distinct areaid from k_department where autoid in ("+departments+"))) " +
				       " ${name} ${organ} ${place} ${device} ${describes} ";

			pp.addColumn("会议室名称", "name");
			pp.addColumn("可容纳人数", "containPerson");
			
			pp.addColumn("设备情况", "device");
			pp.addColumn("所在地点", "place");
			pp.addColumn("会议室描述", "describes");
			
			pp.addSqlWhere("name", " and name like '%${name}%' ");
			pp.addSqlWhere("organ", " and organ like '%${organ}%' ");
			pp.addSqlWhere("place", " and place like '%${place}%' ");
			pp.addSqlWhere("device", " and device like '%${device}%' ");
			pp.addSqlWhere("describes", " and describes like '%${describes}%' ");

			pp.setTableID("MeetingRoomList");
			
			pp.setPageSize_CH(50);
			
			pp.setOrderBy_CH("createTime");
			pp.setDirection_CH("desc");
			
			pp.setInputType("radio");
			pp.setWhichFieldIsValue(1);
			
			pp.setCustomerId("") ;
			
			pp.setTrActionProperty(true) ;
			pp.setTrAction(" uuid=${uuid} ") ;
			
			pp.setSQL(sql.toString());
			
			System.out.println("111111111 会议室 list  sql="+sql);

			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		return model;
		
	}
	
	

	/**
	 * 会议室统计：按部门
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView listDepartCount(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		
		ModelAndView model = new ModelAndView(listDepartCount) ;
		Connection conn = null;
		try {
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String userid = userSession.getUserId(); 
			String areaid = userSession.getAreaid(); 
			
			conn = new DBConnect().getConnect("");
			String menuid = StringUtil.showNull(request.getParameter("menuid")); //菜单ID
			if("".equals(menuid)) menuid = "10000684";
	    	String departments = new UserPopedomService(conn).getUserIdPopedom(userid, menuid);
		
			DataGridProperty pp = new DataGridProperty();
	
			// 使用部门				使用期：**年**月至**年**月
			// 使用日期	开始时间	结束时间	会议室名称	会议名称	参会人员	设备使用情况
			
	//		String sql = " select ifnull(concat(substring(mo.startTime,1,10),'至',substring(mo.endTime,1,10)),'暂未占用') as useTime, "
	//			   + " ifnull(mo.startTime,'暂未占用') as startTime,ifnull(mo.endTime,'暂未占用') as endTime," 
	//			   + " ifnull(d.departname,'暂未占用') as department,mr.name as roomName,ifnull(mo.name,'暂未占用') as orderName," 
	//			   + " ifnull(mj.waitingUserid,'暂未占用') as waitingUserid,ifnull(mj.waitingUserName,'暂未占用') as waitingUserName," 
	//			   + " ifnull(mo.equipment,'暂未占用') as equipment,mr.createTime "
	//			   + " from k_meetingRoom mr "
	//			   + " left join k_meetingOrder mo on  mo.meetingRoomId  like concat('%',mr.name,'%') "
	//			   + " left join  "
	//			   + " ( "
	//			   + " 	select meetingOrderId,group_concat(distinct waitingUserid) as waitingUserid," 
	//			   + " 	group_concat(distinct name) as waitingUserName " 
	//			   + " 	from  "
	//			   + " 		( "
	//			   + " 			select meetingOrderId,waitingUserid,u.name from k_meetingjoiner m "
	//			   + " 			left join k_user u on m.waitingUserid = u.id "
	//			   + " 		) j group by meetingOrderId "
	//			   + " ) "
	//			   + " mj on mo.uuid = mj.meetingOrderId "
	//			   + " left join k_department d on mo.departmentId = d.autoid "
	//			   + " where 1=1 ${departmentName} ";
			
			String sql =" select ifnull(concat(substring(mo.startTime,1,10),'至',substring(mo.endTime,1,10)),'') as useTime, " +
						" ifnull(mo.startTime,'') as startTime,ifnull(mo.endTime,'暂未占用') as endTime," +
						"ifnull(d.departname,'') as department,mr.name as roomName," +
						"ifnull(mo.name,'') as orderName," +
						"ifnull(mo.equipment,'')as equipment,mr.createTime ,mr.uuid ," +
						"ifnull(j.waitingUserid,'') as waitingUserid," +
						"ifnull(j.waitingUserName,'') as waitingUserName ,mr.containPerson AS containPerson" +
						" from k_meetingRoom mr  left join k_meetingOrder mo on mr.uuid = mo.meetingRoomId " +
						"left join  k_department d on mo.departmentId = d.autoid " +
						"left join (select meetingOrderId,group_concat(distinct waitingUserid) as waitingUserid, " +
							"group_concat(distinct name) as waitingUserName from  " +
							"(select meetingOrderId,waitingUserid,u.name from k_meetingjoiner m " +
							"left join k_user u on m.waitingUserid = u.id ) j group by meetingOrderId )j " +
						"on mr.uuid = j.meetingOrderId where 1=1 " +
						"	and (organ = '"+areaid+"' or organ in (select distinct areaid from k_department where autoid in ("+departments+"))) " +
						" ${departmentName}";
			
			
			pp.addColumn("会议室名称", "roomName");
			pp.addColumn("申请部门", "department");
			pp.addColumn("会议名称", "orderName");
			pp.addColumn("设备使用情况", "equipment");
			pp.addColumn("使用日期", "useTime");
			pp.addColumn("开始时间", "startTime");
			pp.addColumn("可容纳人数", "containPerson");
			pp.addColumn("结束时间", "endTime");
			pp.addColumn("参会人员", "waitingUserName");
			
			pp.setTableID("listDepartmentCount");
			
			pp.addSqlWhere("departmentName", " and mo.departmentId = '${departmentName}' ");
			
			pp.setPageSize_CH(50);
			
			pp.setOrderBy_CH("createTime");
			pp.setDirection_CH("desc");
			
			pp.setCustomerId("") ;
			
			pp.setSQL(sql.toString());
			
			pp.setColumnWidth("12,12,12,10,15,20,10,20,20");
			System.out.println("111111111 会议室 listCount  sql="+sql);
	
			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		return model;
		
	}
	
	

	/**
	 * 会议室统计：按会议室
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView listRoomCount(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ModelAndView model = new ModelAndView(listRoomCount) ;
		Connection conn = null;
		try {
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String userid = userSession.getUserId(); 
			String areaid = userSession.getAreaid(); 
			
			conn = new DBConnect().getConnect("");
			String menuid = StringUtil.showNull(request.getParameter("menuid")); //菜单ID
			if("".equals(menuid)) menuid = "10000684";
	    	String departments = new UserPopedomService(conn).getUserIdPopedom(userid, menuid);
				
			DataGridProperty pp = new DataGridProperty();

			// 第 * 会 议 室				使用期：**年**月至**年**月
			// 使用日期	开始时间	结束时间	申请部门	会议名称	参会人员	设备使用情况
	//
//			String sql = " select ifnull(concat(substring(mo.startTime,1,10),'至',substring(mo.endTime,1,10)),'暂未占用') as useTime, "
//					   + " ifnull(mo.startTime,'暂未占用') as startTime,ifnull(mo.endTime,'暂未占用') as endTime," 
//					   + " ifnull(d.departname,'暂未占用') as department,mr.name as roomName,ifnull(mo.name,'暂未占用') as orderName," 
//					   + " ifnull(mj.waitingUserid,'暂未占用') as waitingUserid,ifnull(mj.waitingUserName,'暂未占用') as waitingUserName," 
//					   + " ifnull(mo.equipment,'暂未占用') as equipment,mr.createTime "
//					   + " from k_meetingRoom mr "
//					   + " left join k_meetingOrder mo on  mo.meetingRoomId  like concat('%',mr.name,'%') "
//					   + " left join  "
//					   + " ( "
//					   + " 	select meetingOrderId,group_concat(distinct waitingUserid) as waitingUserid," 
//					   + " 	group_concat(distinct name) as waitingUserName " 
//					   + " 	from  "
//					   + " 		( "
//					   + " 			select meetingOrderId,waitingUserid,u.name from k_meetingjoiner m "
//					   + " 			left join k_user u on m.waitingUserid = u.id "
//					   + " 		) j group by meetingOrderId "
//					   + " ) "
//					   + " mj on mo.uuid = mj.meetingOrderId "
//					   + " left join k_department d on mo.departmentId = d.autoid "
//					   + " where 1=1 ${meetingRoomName} ";
			/**
			String sql =" select ifnull(concat(substring(mo.startTime,1,10),'至',substring(mo.endTime,1,10)),'') as useTime,  " +
						"ifnull(mo.startTime,'') as startTime,ifnull(mo.endTime,'暂未占用') as endTime," +
						"ifnull(d.departname,'') as department,mr.name as roomName," +
						"ifnull(mo.name,'') as orderName, " +
						"ifnull(mo.equipment,'')as equipment,mr.createTime ,mr.uuid,mr.containPerson as containPerson" +
						"from k_meetingRoom mr  left join k_meetingOrder mo on mr.uuid = mo.meetingRoomId " +
						"left join  k_department d on mo.departmentId = d.autoid left join " +
						"(select meetingOrderId,group_concat(distinct waitingUserid) as waitingUserid," +
							" group_concat(distinct name) as waitingUserName  from  " +
							"(select meetingOrderId,waitingUserid,u.name " +
							"from k_meetingjoiner m  left join k_user u on m.waitingUserid = u.id ) " +
						"j group by meetingOrderId )j on mr.uuid = j.meetingOrderId where 1=1 ${meetingRoomName} "; 
			
			**/
			
			String sql ="SELECT IFNULL(CONCAT(SUBSTRING(mo.startTime,1,10),'至',SUBSTRING(mo.endTime,1,10)),'') AS useTime," +
					      " IFNULL(mo.startTime,'') AS startTime,IFNULL(mo.endTime,'暂未占用') AS endTime," +
					      "IFNULL(d.departname,'') AS department,mr.name AS roomName," +
					      "IFNULL(mo.name,'') AS orderName, IFNULL(mo.equipment,'')AS equipment,mr.createTime ,mr.uuid,mr.containPerson AS containPerson " +
					      "FROM k_meetingRoom mr LEFT JOIN k_meetingOrder mo ON mr.uuid = mo.meetingRoomId " +
					      "LEFT JOIN  k_department d ON mo.departmentId = d.autoid LEFT JOIN " +
					      "(SELECT meetingOrderId,GROUP_CONCAT(DISTINCT waitingUserid) AS waitingUserid, " +
					           "GROUP_CONCAT(DISTINCT NAME) AS waitingUserName  FROM  " +
					           "(SELECT meetingOrderId,waitingUserid,u.name FROM k_meetingjoiner m  LEFT JOIN k_user u ON m.waitingUserid = u.id ) " +
					           "j GROUP BY meetingOrderId )j ON mr.uuid = j.meetingOrderId  where 1=1 " +
					           "	and (organ = '"+areaid+"' or organ in (select distinct areaid from k_department where autoid in ("+departments+"))) " +
					           "${meetingRoomName}";
			
			
			pp.addColumn("会议室名称", "roomName");
			pp.addColumn("申请部门", "department");
			pp.addColumn("使用日期", "useTime");
			pp.addColumn("开始时间", "startTime");
			
			pp.addColumn("结束时间", "endTime");
			//pp.addColumn("会议名称", "orderName");
			//pp.addColumn("参会人员", null);
			pp.addColumn("可容纳人数", "containPerson");
			pp.addColumn("设备使用情况", "equipment");
			
			pp.setTableID("listRoomCount");
			
			pp.addSqlWhere("meetingRoomName", " and mr.uuid = '${meetingRoomName}' ");
			
			pp.setPageSize_CH(50);
			
			pp.setOrderBy_CH("createTime");
			pp.setDirection_CH("desc");
			
			pp.setCustomerId("") ;
			
			pp.setSQL(sql.toString());
			
			pp.setColumnWidth("11,11,11,10,15,10,20,20，20");
			
			System.out.println("会议室统计：按会议室 listCount  sql="+sql);

			request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		
		

		return model;
		
	}
	
	

	/**
	 * 会议统计
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView listCount(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		
		ModelAndView model = new ModelAndView(listTotalCount) ;

		return model;
		
	}
	
	
	/**
	 * 会议统计
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView listCount_bak(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		
		ModelAndView model = new ModelAndView(listCount) ;

		DataGridProperty pp = new DataGridProperty();

		
		String sql = " select mr.uuid,mr.name,mr.organ,mr.containPerson,mr.device,mr.place,mr.describes,mo.meetingRoomId," 
				   + " case ifnull(mo.meetingRoomId,'') when '' then '可用' else '占用' end as state "
				   + " from k_meetingRoom mr "
				   + " left join " 
				   + " (select meetingRoomId from k_meetingOrder group by meetingRoomId) mo " 
				   + " on mr.name = mo.meetingRoomid " 
			       + " where 1=1 ${name} ${organ} ";

		pp.addColumn("会议室名称", "name");
		pp.addColumn("可容纳人数", "containPerson");
		
		pp.addColumn("设备情况", "device");
		pp.addColumn("所在地点", "place");
		pp.addColumn("机构", "organ");
		pp.addColumn("会议室描述", "describes");
		pp.addColumn("使用状态", "state");
		
		pp.setTableID("meetingRoomListCount");
		
		pp.addSqlWhere("name", " and name like '%${name}%' ");
		pp.addSqlWhere("organ", " and organ like '%${organ}%' ");
		
		pp.setPageSize_CH(50);
		
		pp.setOrderBy_CH("createTime");
		pp.setDirection_CH("desc");
		
		pp.setCustomerId("") ;
		
		pp.setSQL(sql.toString());
		
		System.out.println("111111111 会议室 listCount  sql="+sql);

		request.getSession().setAttribute(DataGrid.sessionPre + pp.getTableID(), pp);

		return model;
		
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
		
		MeetingRoom mr = null;
		
		try {
			
			conn = new DBConnect().getConnect("");
			
			
			if("add".equalsIgnoreCase(opt)){
				mr = new MeetingRoom();
				mr.setUuid(UUID.randomUUID().toString());
				model.addObject("mr",mr);
			}else{
				// 会议室
				MeetingRoomService ms = new MeetingRoomService(conn);
				mr = ms.getMeetingRoom(id);
				
				model.addObject("mr",mr);
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
	 * 保存
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
		String organ = af.showNull(request.getParameter("organ"));
		String containPerson = af.showNull(request.getParameter("containPerson"));
		String device = af.showNull(request.getParameter("device"));
		String place = af.showNull(request.getParameter("place"));
		String describes = af.showNull(request.getParameter("describes"));
		
		String creatorId = userSession.getUserId();
		String createTime = af.getCurrentDate();
		
		Connection conn = null;
		
		MeetingRoom mr = new MeetingRoom();
		mr.setUuid(uuid);
		mr.setName(name);
		mr.setOrgan(organ);
		mr.setContainPerson(containPerson);
		mr.setDevice(device);
		mr.setPlace(place);
		
		mr.setDescribes(describes);
		mr.setCreatorId(creatorId);
		mr.setCreateTime(createTime);

		
		try {
			
			conn = new DBConnect().getConnect("");
			
			MeetingRoomService ms = new MeetingRoomService(conn);
			
			if("add".equalsIgnoreCase(opt)){
				// 添加
				ms.addMeetingRoom(mr);
			}else{
				// 修改
				ms.updateMeetingRoom(mr);
				
			}
		
			response.sendRedirect(request.getContextPath()+"/meetingRoom.do?method=list");
			
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
			
			MeetingRoomService ms = new MeetingRoomService(conn);
			
			// 删除会议室
			ms.deleteMeetingRoom(id);
		
			response.sendRedirect(request.getContextPath()+"/meetingRoom.do?method=list");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn);
		}
		
		return null;
	}
	
	

	/**
	 * 会议室是否重复
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView isNameRepeat(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);

		Connection conn = null;

		String name = request.getParameter("name");
		String id = request.getParameter("id");
		
		ASFuntion af = new ASFuntion();

		PrintWriter out = response.getWriter();

		try{
			conn = new DBConnect().getConnect("");

			String result = new DbUtil(conn).queryForString(" select uuid from k_meetingRoom where name = ? and uuid!= ? ",new Object[]{name,id}); 
			result = af.showNull(result);
			
			if(!"".equals(result) && request!=null){
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
	 * 得到时间
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView getTime(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);

		Connection conn = null;

		ASFuntion af = new ASFuntion();

		PrintWriter out = response.getWriter();
		
		
		String meetingRoomName = request.getParameter("meetingRoomName");
		
		if(meetingRoomName!=null && !"".equals(meetingRoomName)){
			/**
			String ssql = " select mo.startTime " 
				   + " from k_meetingRoom mr "
				   + " left join k_meetingOrder mo on  mo.meetingRoomId  like concat('%',mr.name,'%') "
				   + " left join  "
				   + " ( "
				   + " 	select meetingOrderId,group_concat(distinct waitingUserid) as waitingUserid," 
				   + " 	group_concat(distinct name) as waitingUserName " 
				   + " 	from  "
				   + " 		( "
				   + " 			select meetingOrderId,waitingUserid,u.name from k_meetingjoiner m "
				   + " 			left join k_user u on m.waitingUserid = u.id "
				   + " 		) j group by meetingOrderId "
				   + " ) "
				   + " mj on mo.uuid = mj.meetingOrderId "
				   + " left join k_department d on mo.departmentId = d.autoid "
				   + " where  mr.name = '"+meetingRoomName+"' and mo.startTime is not null order by mo.startTime asc limit 1 ";
		**/
			String sql ="SELECT IFNULL(CONCAT(SUBSTRING(mo.startTime,1,10),'至',SUBSTRING(mo.endTime,1,10)),'') AS useTime," +
				      " IFNULL(mo.startTime,'') AS startTime,IFNULL(mo.endTime,'暂未占用') AS endTime," +
				      "IFNULL(d.departname,'') AS department,mr.name AS roomName," +
				      "IFNULL(mo.name,'') AS orderName, IFNULL(mo.equipment,'')AS equipment,mr.createTime ,mr.uuid,mr.containPerson AS containPerson " +
				      "FROM k_meetingRoom mr LEFT JOIN k_meetingOrder mo ON mr.uuid = mo.meetingRoomId " +
				      "LEFT JOIN  k_department d ON mo.departmentId = d.autoid LEFT JOIN " +
				      "(SELECT meetingOrderId,GROUP_CONCAT(DISTINCT waitingUserid) AS waitingUserid, " +
				           "GROUP_CONCAT(DISTINCT NAME) AS waitingUserName  FROM  " +
				           "(SELECT meetingOrderId,waitingUserid,u.name FROM k_meetingjoiner m  LEFT JOIN k_user u ON m.waitingUserid = u.id ) " +
				           "j GROUP BY meetingOrderId )j ON mr.uuid = j.meetingOrderId where mr.name = '"+meetingRoomName+"'";
			
			
			
			String esql = " select mo.endTime "
				   + " from k_meetingRoom mr "
				   + " left join k_meetingOrder mo on  mo.meetingRoomId  like concat('%',mr.name,'%') "
				   + " left join  "
				   + " ( "
				   + " 	select meetingOrderId,group_concat(distinct waitingUserid) as waitingUserid," 
				   + " 	group_concat(distinct name) as waitingUserName " 
				   + " 	from  "
				   + " 		( "
				   + " 			select meetingOrderId,waitingUserid,u.name from k_meetingjoiner m "
				   + " 			left join k_user u on m.waitingUserid = u.id "
				   + " 		) j group by meetingOrderId "
				   + " ) "
				   + " mj on mo.uuid = mj.meetingOrderId "
				   + " left join k_department d on mo.departmentId = d.autoid "
				   + " where mr.name = '"+meetingRoomName+"' and mo.endTime is not null order by mo.endTime desc limit 1 ";
			


			try{
				conn = new DBConnect().getConnect("");
	
				String startTime = new DbUtil(conn).queryForString(sql); 
				String endTime = new DbUtil(conn).queryForString(esql); 
				
				startTime = af.showNull(startTime);
				endTime = af.showNull(endTime);
				
				if(startTime.length()>=10){
					startTime = startTime.substring(0,4)+"年"+startTime.substring(5,7)+"月"+startTime.substring(8,10)+"日";
				}
				
				if(endTime.length()>=10){
					endTime = endTime.substring(0,4)+"年"+endTime.substring(5,7)+"月"+endTime.substring(8,10)+"日";
				}
				
				System.out.println("meetingRoomName="+meetingRoomName+"<>startTime="+startTime+"<>endTime="+endTime);
				
				if("".equals(startTime)){
					out.print(endTime);
				}else{
					out.print(startTime+"至"+endTime);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				DbUtil.close(conn);
			}
		}

		return null;
	}
	
}
