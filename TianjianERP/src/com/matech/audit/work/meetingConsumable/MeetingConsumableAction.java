package com.matech.audit.work.meetingConsumable;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.meetingConsumable.MeetingConsumableService;
import com.matech.audit.service.meetingConsumable.model.MeetingConsumable;
import com.matech.audit.service.userpopedom.UserPopedomService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.StringUtil;

public class MeetingConsumableAction extends MultiActionController {
	private static final String list = "/MeetingConsumable/list.jsp";
	private static final String addAndEdit = "/MeetingConsumable/addAndEdit.jsp";
	private static final String view = "/MeetingConsumable/view.jsp";
	
	
	/**
	 * 看到 自己登记的 耗材
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn = null;
		ModelAndView model = new ModelAndView(list) ;
		try {
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String userid = userSession.getUserId();
			
			conn = new DBConnect().getConnect("");
			String menuid = StringUtil.showNull(request.getParameter("menuid")); //菜单ID
			if("".equals(menuid)) menuid = "10000683";
	    	String departments = new UserPopedomService(conn).getUserIdPopedom(userid, menuid);
	    	
			DataGridProperty pp = new DataGridProperty();

//			String sql = " select mc.batchnumber,(select name from k_meetingorder mo where mo.uuid =mc. meetingOrderId ) as aa,group_concat(names) as names,max(recordtime) as times from k_meetingConsumable mc" 
//					   + " where recordUser = '"+userid+"' "
//					   + " group by meetingOrderId,batchnumber ";
			String sql = "select mc.batchnumber,mc.meetingOrderId,mo.name as aa ,mc.names as names,mc.recordtime as times, mc.moneys as moneys,mc.counts as counts " +
					"from k_meetingConsumable  mc " +
					"left join k_meetingorder mo on mo.uuid = mc.meetingOrderId " +
					"left join k_user u on mc.recordUser = u.id " +
					"where (recordUser = '"+userid+"' or u.departmentid in ("+departments+"))  " ;
					//"group by meetingOrderId,batchnumber ";
			
			pp.addColumn("耗材名称", "names");
			pp.addColumn("耗材数量", "counts");
			pp.addColumn("耗材金额", "moneys");
			pp.addColumn("会议名称", "aa");
			pp.addColumn("登记时间", "times");
			
			pp.setTableID("meetingConsumableList");
			
			pp.setPageSize_CH(50);
			
			pp.setOrderBy_CH("times");
			pp.setDirection_CH("desc");
			
			pp.setInputType("radio");
			pp.setWhichFieldIsValue(1);
			
			pp.setCustomerId("") ;
			
			pp.setTrActionProperty(true) ;
			pp.setTrAction(" meetingOrderId=${meetingOrderId} ") ;
			
			pp.setSQL(sql.toString());
			
			System.out.println("  耗材 list  sql="+sql);

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
	 *  新增、修改
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView go(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView model = new ModelAndView(addAndEdit);
		
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		String userName = userSession.getUserName();
		
		ASFuntion af = new ASFuntion();
		
		String nowDate = af.getCurrentDate();
		
		String opt = af.showNull(request.getParameter("opt"));
		String batchNumber = af.showNull(request.getParameter("id"));
		String pageOpt = af.showNull(request.getParameter("pageOpt"));
		
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect("");
			
			List list = new ArrayList();
			
			String meetingOrderId = "";
			
			if("update".equalsIgnoreCase(opt)){
				
				MeetingConsumableService mcs = new MeetingConsumableService(conn);
				list = mcs.getByBatchNumber(batchNumber);
				
				meetingOrderId = new DbUtil(conn).queryForString(" select meetingOrderId from k_meetingConsumable where batchnumber=? ",new Object[]{batchNumber});
			}
			
		
			
			model.addObject("meetingOrderId",meetingOrderId);
			model.addObject("batchNumber",batchNumber);
			model.addObject("pageOpt",pageOpt);
			model.addObject("lists", list);
			model.addObject("userName",userName);
			model.addObject("nowDate",nowDate);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn);
		}
		
		model.addObject("opt",opt);
		return model;
	}
	
	/**
	 *  查看
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView goView(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView model = new ModelAndView(view);
		
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		String userName = userSession.getUserName();
		
		ASFuntion af = new ASFuntion();
		
		String nowDate = af.getCurrentDate();
		
		String opt = af.showNull(request.getParameter("opt"));
		String batchNumber = af.showNull(request.getParameter("id"));
		String pageOpt = af.showNull(request.getParameter("pageOpt"));
		
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect("");
			
			List list = new ArrayList();
			
			String meetingOrderId = "";
			
		
			
			//添加查看
			if("view".equalsIgnoreCase(opt)){
				
				MeetingConsumableService mcs = new MeetingConsumableService(conn);
				list = mcs.getByBatchNumber(batchNumber);
				
				meetingOrderId = new DbUtil(conn).queryForString(" select meetingOrderId from k_meetingConsumable where batchnumber=? ",new Object[]{batchNumber});
			}
			
			model.addObject("meetingOrderId",meetingOrderId);
			model.addObject("batchNumber",batchNumber);
			model.addObject("pageOpt",pageOpt);
			model.addObject("lists", list);
			model.addObject("userName",userName);
			model.addObject("nowDate",nowDate);
			
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
	public ModelAndView save(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
		String userId = userSession.getUserId();
		
		ASFuntion af = new ASFuntion();
		
		String opt = request.getParameter("opt");
		String pageOpt = request.getParameter("pageOpt");
		String meetingOrderId = request.getParameter("meetingOrderId");
		String batchNumber = request.getParameter("batchNumber");
		
		Connection conn = null;
		try {
			
			conn = new DBConnect().getConnect("");
			
			// 耗材信息
			String[] names = request.getParameterValues("names");
			String[] counts = request.getParameterValues("counts");
			String[] moneys = request.getParameterValues("moneys");
			
			MeetingConsumableService mcs = new MeetingConsumableService(conn);
			
			// 如果是修改耗材 的 话 就 先删除之前的
			if("update".equalsIgnoreCase(opt)){
				mcs.deleteById("batchNumber",batchNumber);
			}
			
			String uuid = UUID.randomUUID().toString();
			
			for (int i = 0; i < names.length; i++) {
				
				MeetingConsumable mc = new MeetingConsumable();
				
				mc.setUuid(UUID.randomUUID().toString());
				mc.setMeetingOrderId(meetingOrderId);
				mc.setNames(names[i]);
				mc.setCounts(counts[i]);
				mc.setMoneys(moneys[i]);
				mc.setRecordUser(userId);
				mc.setRecordTime(af.getCurrentDate());
				mc.setBatchNumber(uuid);
				mc.setProperty(i+"");
				
				// 添加耗材信息
				mcs.addMeetingConsumable(mc);

			}
			
			if("listPass".equalsIgnoreCase(pageOpt)){
				// 转到会议已通过
				response.sendRedirect(request.getContextPath()+"/meetingOrder.do?method=goPass");
			}else{
				response.sendRedirect(request.getContextPath()+"/meetingConsumable.do?method=list");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			DbUtil.close(conn);
		}
		
		return null;
	}
	

	/**
	 * 已经 登记过的 会议的 耗材
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView alreadyRegist(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/html;charset=utf-8");

		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", 0);

		Connection conn = null;

		ASFuntion af = new ASFuntion();
		
		String id = af.showNull(request.getParameter("id"));
		
		PrintWriter out = response.getWriter();

		try{
			conn = new DBConnect().getConnect("");

			String rs = new DbUtil(conn).queryForString(" select recordTime from k_meetingConsumable where meetingorderId = ? ",new Object[]{id});
			rs = af.showNull(rs);
			
			out.write(rs);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}
	
	
	/**
	 * 删除 登记过的 会议的 耗材
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView delAlreadyRegist(HttpServletRequest request,
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

			new DbUtil(conn).execute(" delete from k_meetingConsumable where meetingorderId = ? ",new Object[]{id});
			
			out.write("");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}
	
	
	/**
	 * 删除 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView delete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Connection conn = null;

		String id = request.getParameter("id");
		
		try{
			conn = new DBConnect().getConnect("");

			MeetingConsumableService mcs = new MeetingConsumableService(conn);
			mcs.deleteById("batchnumber",id);
			
			response.sendRedirect(request.getContextPath()+"/meetingConsumable.do?method=list");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}

		return null;
	}
	
}
