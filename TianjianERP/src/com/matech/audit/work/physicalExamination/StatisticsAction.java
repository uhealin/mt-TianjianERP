package com.matech.audit.work.physicalExamination;

import java.lang.reflect.Method;
import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.physicalExamination.AppointmentService;
import com.matech.audit.service.physicalExamination.InformService;
import com.matech.audit.service.physicalExamination.model.AppointmentVO;
import com.matech.audit.service.physicalExamination.model.InformVO;
import com.matech.audit.service.user.model.User;
import com.matech.audit.service.userpopedom.UserPopedomService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.datagrid.DataGrid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.WebUtil;

public class StatisticsAction extends MultiActionController {
	
	private final String LIST = "physicalExamination/statisticsList.jsp";
	private final String PERSONAL = "physicalExamination/personal.jsp"; 
	/**
	 * 列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response) throws Exception{
		Connection conn = null;
		ModelAndView modelandview =new ModelAndView(LIST);
		try {
			conn = new DBConnect().getConnect();
			
			UserSession userSession = (UserSession) request.getSession().getAttribute("userSession");
			String userid = userSession.getUserId(); 
			String menuid = StringUtil.showNull(request.getParameter("menuid")); //菜单ID
			if("".equals(menuid)) menuid = "10000689";
	    	String departments = new UserPopedomService(conn).getUserIdPopedom(userid, menuid);
	    	
			DataGridProperty dp = new DataGridProperty(){};
			
			dp.setTableID("statisticsList");
			dp.setCustomerId("");
			dp.setPageSize_CH(20);
			
			String informuuid=  request.getParameter("informuuid");
			
			System.out.println("informuuid="+informuuid);
			if ("".equals(informuuid)||informuuid==null){
				modelandview.addObject("informuuid","");
				modelandview.addObject("outputData","false");
				return modelandview; 
			}
			
			modelandview.addObject("informuuid",informuuid);
			modelandview.addObject("outputData","true");
			
			String sql = 
					"select " + 
					"kd.departname,u.name,u.sex,u.residence,u.floor,u.rank," +
					"if(pin.batch_time_1 = pa.appointment_time,1,'') as batch_time_1," + 
					"if(pin.batch_time_2 = pa.appointment_time,1,'') as batch_time_2," +
					"if(pin.batch_time_3 = pa.appointment_time,1,'') as batch_time_3," +
					"if(pin.batch_time_4 = pa.appointment_time,1,'') as batch_time_4," +
					"if(pin.batch_time_5 = pa.appointment_time,1,'') as batch_time_5," +
					"if(pin.batch_time_6 = pa.appointment_time,1,'') as batch_time_6," +
					"if(pin.batch_time_7 = pa.appointment_time,1,'') as batch_time_7," +
					"if(pin.batch_time_8 = pa.appointment_time,1,'') as batch_time_8," +
					"pa.examination_get,pa.results_get " +
					"from pe_inform pin " +
					"left join k_user u on pin. person_select_ids like concat('%',u.id,'%') " +
					"left join pe_appointment pa on pa.user_id = u.id " +
					"left join k_department kd on kd.autoid = u.departmentid" +
					" where 1=1 and pin.uuid='"+informuuid+"' " +
					" and (u.id = '"+userid+"' or u.departmentid in ("+departments+")) ";
			
			dp.setSQL(sql);
			dp.setOrderBy_CH("u.name");
			dp.setDirection("desc");
			
			dp.setColumnWidth("8,6,4,8,4,10,8,12,16");
			
			
			String strHead="部门,姓名,性别,办公区域,楼层,人员类别,体检预约时间(批次){";
			dp.addColumn("部门", "departname");
			dp.addColumn("姓名", "name");
			dp.addColumn("性别", "sex");
			dp.addColumn("办公区域", "residence");
			dp.addColumn("楼层", "floor");
			dp.addColumn("人员类别", "rank");
			
			
			
			InformService informs = new InformService(conn);
			InformVO informVO = informs.getInformByUUID(informuuid);
			int num = informVO.getBatch_number();
			
			for (int i = 1; i <= num; i++){
				String getter = "getBatch_time_" + i;
				Method method = InformVO.class.getDeclaredMethod(getter);
				Object obj = method.invoke(informVO);
				String val = obj==null ? "" : obj.toString();
				String arr[] = val.split("-");
				String date = "";
				for(int j = 1; j < arr.length; j ++){
					date = arr[1] + "月" + arr[2] + "日";
				}
				System.out.println(date);
				dp.addColumn(date, "batch_time_" + i);
				strHead += "batch_time_" + i + ",";
			}
			strHead = strHead.substring(0, strHead.length()-1) + "},是否已领体检表,是否已领体检结果表";
			System.out.println("=================================");
			System.out.println("strHead ="+strHead);
			System.out.println("=================================");
			dp.setTableHead(strHead);
			
			dp.addColumn("是否已领体检表", "examination_get");
			dp.addColumn("是否已领体检结果表", "results_get");
			
			dp.setTrActionProperty(false);
			
			dp.setWhichFieldIsValue(1);
			dp.setPrintEnable(true);
			dp.setPrintVerTical(false);
			dp.setPrintTitle("单位人员体检预约统计表");

			request.getSession().setAttribute(DataGrid.sessionPre + dp.tableID, dp);
		} finally {
			DbUtil.close(conn);
		}
		
		return modelandview;
	}
	
	/**
	 * 点击查看跳转到个人体检信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView goUpdate(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView model = new ModelAndView(PERSONAL);
		String uuid = request.getParameter("uuid");
		WebUtil webUtil = new WebUtil(request, response);
		
		AppointmentVO appointment = webUtil.evalObject(AppointmentVO.class);
		User user = webUtil.evalObject(User.class);
		
		model.addObject("uuid", uuid);
		model.addObject("appointment", appointment);
		model.addObject("user", user);
		
		return model;
	}
	
	/**
	 * 修改后，点击提交按钮后保存
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView updateSave(HttpServletRequest request, HttpServletResponse response)throws Exception{
		Connection conn = null;
//		ASFuntion CHF = new ASFuntion();
		String examination_get = request.getParameter("examination_get");
		String results_get = request.getParameter("results_get");
		String user_id = request.getParameter("user_id");
		
		WebUtil webUtil = new WebUtil(request, response);
		AppointmentVO appointment = webUtil.evalObject(AppointmentVO.class);
		
		appointment.setExamination_get(examination_get);
		appointment.setResults_get(results_get);
	
		conn = new DBConnect().getConnect();
		AppointmentService as = new AppointmentService(conn);
		
		as.updateByUUIDAndUserId(appointment, appointment.getUuid(), user_id);
		
		DbUtil.close(conn);
		response.sendRedirect("statistics.do");
		return null;
	}
}
