package com.matech.audit.service.process.impl.car;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.MessageFormat;

import org.jbpm.api.listener.EventListenerExecution;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.car.model.CarApplyVO;
import com.matech.audit.service.car.model.CarMotormanVO;
import com.matech.audit.service.department.model.KAreaVO;
import com.matech.audit.service.department.model.KDepartmentVO;
import com.matech.audit.service.process.impl.base.NodeHandler;
import com.matech.audit.service.user.model.UserVO;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;
import com.matech.sms.SmsOpt;

public class CarCheckNodeHandler extends NodeHandler {

	@Override
	public void nodeStart(EventListenerExecution execution) {
		// TODO Auto-generated method stub
		String uuid=(String)execution.getVariable("uuid");
		Connection conn=null;
		DbUtil dbUtil=null;
		CarApplyVO carApplyVO=null;
		String msgPattern="{0}{1} {2} {3}个人申请于{4}到{5}，请尽快处理！",msgContext="";
		UserVO userVO=null;
		KDepartmentVO kDepartmentVO=null;
		KAreaVO kAreaVO=null;
		CarMotormanVO carMotormanVO=null;
		try {
			conn=new DBConnect().getConnect();
            dbUtil=new DbUtil(conn);
            carApplyVO=dbUtil.load(CarApplyVO.class, uuid);
            //carApplyVO.setAuditing("是");
            //dbUtil.update(carApplyVO);
            carMotormanVO=dbUtil.load(CarMotormanVO.class, carApplyVO.getMotormanid());
            userVO=dbUtil.load(UserVO.class, carApplyVO.getPeople_uid());
            kDepartmentVO=dbUtil.load(KDepartmentVO.class, Integer.parseInt(userVO.getDepartmentid()));
            kAreaVO=dbUtil.load(KAreaVO.class, Integer.parseInt(kDepartmentVO.getAreaid()));
            String bdate=StringUtil.showDate(carApplyVO.getBegintime(), "showDate:MM月dd日");
            msgContext=MessageFormat.format(msgPattern,
            		kAreaVO.getName(),
            		kDepartmentVO.getDepartname(),
            		userVO.getName(),
            		carApplyVO.getPeoplenumber(),
            		bdate,
            		carApplyVO.getTermini()
            );
			
            SmsOpt.sendSm(carMotormanVO.getPhone(), msgContext);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
	}

	@Override
	public void nodeEnd(EventListenerExecution execution) {
		// TODO Auto-generated method stub
		String uuid=(String)execution.getVariable("uuid");
		Connection conn=null;
		DbUtil dbUtil=null;
		CarApplyVO carApplyVO=null;
		String msgPattern="你申请的{0}到{1}的用车申请已获批，请直接联系{2}（手机号:{3}）",msgContext="";
		UserVO userVO=null;
		KDepartmentVO kDepartmentVO=null;
		KAreaVO kAreaVO=null;
		CarMotormanVO carMotormanVO=null;
		try {
			conn=new DBConnect().getConnect();
            dbUtil=new DbUtil(conn);
            carApplyVO=dbUtil.load(CarApplyVO.class, uuid);
            carApplyVO.setAuditing("是");
            dbUtil.update(carApplyVO);
            carMotormanVO=dbUtil.load(CarMotormanVO.class, carApplyVO.getMotormanid());
            userVO=dbUtil.load(UserVO.class, carApplyVO.getPeople_uid());
            kDepartmentVO=dbUtil.load(KDepartmentVO.class, Integer.parseInt(userVO.getDepartmentid()));
            kAreaVO=dbUtil.load(KAreaVO.class, Integer.parseInt(kDepartmentVO.getAreaid()));
            String bdate=StringUtil.showDate(carApplyVO.getBegintime(), "showDate:MM月dd日");
            msgContext=MessageFormat.format(msgPattern,
            		bdate,
            		carApplyVO.getTermini(),
            		carMotormanVO.getName(),
            		carMotormanVO.getPhone()
            );
					//System.out.println("更改了多少条：" + count);
            SmsOpt.sendSm(userVO.getMobilePhone(), msgContext);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DbUtil.close(conn);
		}
	}

}
