package com.matech.audit.work.physicalExamination;

import java.io.IOException;
import java.sql.Connection;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.news.NewsService;
import com.matech.audit.service.physicalExamination.AppointmentService;
import com.matech.audit.service.physicalExamination.InformService;
import com.matech.audit.service.physicalExamination.model.AppointmentVO;
import com.matech.audit.service.physicalExamination.model.InformVO;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.WebUtil;

public class AppointmentAction extends MultiActionController{
	private final String EDIT = "physicalExamination/appointmentEdit.jsp";
	
	/**
	 * 修改跳转
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ModelAndView edit(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView model = new ModelAndView(EDIT);
		String uuid = request.getParameter("uuid");
		Connection conn = null;
		InformVO inform = null;
		try {
			conn = new DBConnect().getConnect("");
			InformService informService = new InformService(conn);
			inform = informService.getInformByUUID(uuid);
			model.addObject("inform", inform);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(conn);
		}
		return model;
	}
	
	/**
	 * 新增保存
	 * @param request
	 * @param response
	 * @param appointment
	 * @return
	 * @throws IOException
	 */
	public ModelAndView list(HttpServletRequest request,HttpServletResponse response)throws IOException{
		Connection conn = null;
		AppointmentService as = new AppointmentService(conn);
		WebUtil webUtil = new WebUtil(request, response);
		UserSession userSession = webUtil.getUserSession();
		String url_to = "formDefine.do?method=formListView&uuid=6594cb56-a0ce-4736-92cf-bf95318a52e0";
		boolean flag = false;
		try {
			AppointmentVO appointmentVO = webUtil.evalObject(AppointmentVO.class);
			conn = new DBConnect().getConnect();
//			ASFuntion CHF = new ASFuntion();
			
			DbUtil dbUtil=new DbUtil(conn);
			
			
			String userId = userSession.getUserId();
			flag = dbUtil.execute("delete from pe_appointment where user_id = ?",new String[]{userId});
			
			String informuuid = request.getParameter("inform_uuid");
			int choose_batch = Integer.valueOf(request.getParameter("choose_batch"));
//			Integer.valueOf(b); 
			String appointmentTime = as.getBatchTime(informuuid, choose_batch);
			//appointmentVO.setChoose_batch(choose_batch);
			appointmentVO.setUser_id(userId);
			appointmentVO.setAppointment_time(appointmentTime);
			appointmentVO.setUuid(UUID.randomUUID().toString());
			
			dbUtil.insert(appointmentVO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ModelAndView(url_to);
	}
	
	//删除
	public ModelAndView delete(HttpServletRequest request,HttpServletResponse response)throws IOException{
		String uuid = request.getParameter("uuid");
		System.out.println(uuid+"=============================");
		Connection conn = null;

		try {
			conn = new DBConnect().getConnect("");
					
				NewsService ns = new NewsService(conn);
				
				// 删除附件表中对应 的记录
				String sql = "DELETE from asdb.pe_inform where uuid  = '"+uuid+"'";
				new DbUtil(conn).execute(sql);		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
		response.sendRedirect("formDefine.do?method=formListView&uuid=6594cb56-a0ce-4736-92cf-bf95318a52e0&menuid=10000688");
		
		return null;
	}
}
