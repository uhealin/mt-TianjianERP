package com.matech.audit.service.physicalExamination;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.physicalExamination.model.AppointmentVO;
import com.matech.audit.service.physicalExamination.model.InformVO;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.WebUtil;

public class AppointmentService {
	Connection conn = null;
	
	public AppointmentService(Connection conn){
		this.conn = conn;
	}
	/**
	 * 新增一个appointment对象
	 * @param appointment
	 * @return
	 */
	public boolean addAppointment(AppointmentVO appointment){
		PreparedStatement ps = null;
		String sql = null;
		boolean result = false;
		try {
			sql = "insert into pe_appointment (choose_batch,appointment_time,user_id) values (?,?,?)";
			conn = new DBConnect().getConnect();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, appointment.getChoose_batch());
			ps.setString(2, appointment.getUser_id());
			ps.setString(3, appointment.getAppointment_time());
			
			ps.execute();
			result = true;
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);			
		}
		return result;
	}
	
	/**
	 * 通过映射的方式根据选择的批次数获取对应的时间
	 * @param informuuid
	 * @param batchNumber
	 * @return
	 */
	public String getBatchTime(String informuuid,int batchNumber){
		String batchTime = null;
		InformVO inform = new InformService(conn).getInformByUUID(informuuid);
		String methodName = "getBatch_time_" + batchNumber;
		Method method = null;
		try {
			method = inform.getClass().getDeclaredMethod(methodName);
			Object obj = method.invoke(inform);
			batchTime = obj.toString();			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return batchTime;
	}
	/**
	 * 得到一个AppointmentVO对象
	 * @param uuid
	 * @return
	 */
	public AppointmentVO getAppointmentByUUID(String uuid){
		AppointmentVO appointment = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = null;
		try {
			sql = "select choose_batch,user_id,examination_get,results_get,inform_uuid,appointment_time from pe_appointment where uuid = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, uuid);
			rs = ps.executeQuery();
			if(rs.next()){
				appointment = new AppointmentVO();
				appointment.setChoose_batch(rs.getInt("choose_batch"));
				appointment.setUser_id(rs.getString("user_id"));
				appointment.setExamination_get(rs.getString("examination_get"));
				appointment.setResults_get(rs.getString("results_get"));
				appointment.setInform_uuid(rs.getString("inform_uuid"));
				appointment.setAppointment_time(rs.getString("appointment_time"));
			}
			return appointment;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		return appointment;
	}
	
	/**
	 * 通过uuid，user_id更新Appointment数据
	 * 
	 * @param uuid
	 * @return
	 */
	public boolean updateByUUIDAndUserId(AppointmentVO appointment,String uuid,String userid){
		PreparedStatement ps = null;
		String sql = null;
		boolean result = false;
		try {
			conn = new DBConnect().getConnect();
			sql = "update pe_appointment set examination_get = ?, Results_get = ? where uuid = ? and user_id = ?";
			ps.setString(1, appointment.getExamination_get());
			ps.setString(2, appointment.getResults_get());
			ps.setString(3, uuid);
			ps.setString(4, userid);
			
			ps.execute();
			result = true;
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return result;
	}
}
