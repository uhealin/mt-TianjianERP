package com.matech.audit.service.car;

import java.text.MessageFormat;

import org.junit.Test;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.car.model.CarApplyVO;
import com.matech.audit.service.car.model.CarMotormanVO;
import com.matech.audit.service.department.model.KAreaVO;
import com.matech.audit.service.department.model.KDepartmentVO;
import com.matech.audit.service.user.model.UserVO;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.StringUtil;
import com.matech.framework.pub.util.TestUtil;
import com.matech.sms.SmsOpt;

public class CarTest extends TestUtil {

	   private CarApplyVO carApplyVO;
	
	   public CarTest(){
		  try {
			carApplyVO=dbUtil.load(CarApplyVO.class, "47831933-5b7b-42a7-a3a7-672ee3ccb2cf");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   }
	
	   @Test
       public void testRemind(){
			//CarApplyVO carApplyVO=null;
			String msgPattern="{0}{1} {2} {3}个人申请于{4}到{5}，请尽快处理！",msgContext="";
			String msgPattern2="你申请的{0}到{1}的用车申请已获批，请直接联系{2}（手机号:{3}）",msgContext2="";
			UserVO userVO=null;
			KDepartmentVO kDepartmentVO=null;
			KAreaVO kAreaVO=null;
			CarMotormanVO carMotormanVO=null;
			try {
				//conn=new DBConnect().getConnect();
	           // dbUtil=new DbUtil(conn);
	            //carApplyVO=dbUtil.load(CarApplyVO.class, uuid);
	            //carApplyVO.setAuditing("是");
	            //dbUtil.update(carApplyVO);
	            carMotormanVO=dbUtil.load(CarMotormanVO.class, carApplyVO.getMotormanid());
	            userVO=dbUtil.load(UserVO.class, String.valueOf(carApplyVO.getPeople_uid()));
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
	            
	            msgContext2=MessageFormat.format(msgPattern2,
	            		bdate,
	            		carApplyVO.getTermini(),
	            		carMotormanVO.getName(),
	            		carMotormanVO.getPhone()
	            );
	            SmsOpt.sendSm(userVO.getMobilePhone(), msgContext2);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				DbUtil.close(conn);
			}
       }
       
}
