package com.matech.audit.service.form.impl.car;

import java.sql.Connection;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.matech.audit.service.form.FormExtInterface;
import com.matech.audit.service.placard.PlacardService;
import com.matech.audit.service.placard.model.PlacardTable;
import com.matech.framework.listener.UserSession;

public class Car implements FormExtInterface {

	@Override
	public String beforeAdd(Connection conn, String formId,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String afterAdd(Connection conn, String formId, String dataUUID,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String beforeUpdate(Connection conn, String formId, String dataUUID,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		PlacardTable placardTable=new PlacardTable(); 
		//String todayTime = new Date().;
		
		DateFormat df11 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
		String todaytime = df11.format(new Date());
		
		String people_uid=req.getParameter("people_uid");

       
		
		String sbString=MessageFormat.format("{0}您好，您的申请已经批准，安排的司机是：{1}、手机号码：{2}、车牌号：{3}、车颜色：{4}、出车开始时间：{5}、出车结束时间：{6}", 
				req.getParameter("people")
			   ,req.getParameter("motormanid1") //
			   ,req.getParameter("motormanphone")
			   ,req.getParameter("registration")
			   ,req.getParameter("color")
			   ,req.getParameter("btime")
			   ,req.getParameter("etime")
				
				); 
	
		placardTable.setIsReversion(0);
		placardTable.setAddresserTime(todaytime);
		placardTable.setCaption("邮件提醒");
		placardTable.setMatter(sbString);
		placardTable.setIsRead(0);
		placardTable.setIsNotReversion(0);
		placardTable.setUuid(UUID.randomUUID().toString());
		placardTable.setUrl("interiorEmail.do?method=emailMain&isReadOnly=true&back=true");
		placardTable.setUuidName("uuid");
		placardTable.setModel("内部邮件");
		placardTable.setAddresser(people_uid);//发起
		PlacardService placardService = new PlacardService(conn);
        placardService.AddPlacard(placardTable);
		
        return null;
	}

	@Override
	public String afterUpdate(Connection conn, String formId, String dataUUID,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String beforeDelete(Connection conn, String formId, String dataUUID,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String afterDelete(Connection conn, String formId, String dataUUID,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void beforeView(Connection conn, String formId, String dataUUID,
			HttpServletRequest req, HttpServletResponse res,
			ModelAndView modelAndView) {
		// TODO Auto-generated method stub
		
	}

	

	
}
