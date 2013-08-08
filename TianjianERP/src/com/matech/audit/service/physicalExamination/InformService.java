package com.matech.audit.service.physicalExamination;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.physicalExamination.model.InformVO;
import com.matech.framework.pub.db.DbUtil;

public class InformService {
	private Connection conn = null;
	public InformService(Connection conn) {
		this.conn = conn;
	}
	
	/**
	 * 根据uuid获取InformVO对象
	 * @param uuid
	 * @return
	 */
	public InformVO getInformByUUID(String uuid) {
		InformVO inform = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = null;
		try {
			sql = "select uuid,notice_title,detailed_description,batch_number,number_limit," +
					"closing_date,reality_number,get_physicalList_count,get_result_count," +
					"batch_time_1,batch_time_2,batch_time_3,batch_time_4,batch_time_5,batch_time_6," +
					"batch_time_7,batch_time_8,person_select_ids,person_select_names,release_date," +
					"release_department from pe_inform where uuid = ?";
			conn = new DBConnect().getConnect();
			ps = conn.prepareStatement(sql);
			ps.setString(1, uuid);
			rs = ps.executeQuery();
			
			if(rs.next()){
				int i = 1;
				inform = new InformVO();
				inform.setUuid(rs.getString(i++));
				inform.setNotice_title(rs.getString(i++));
				inform.setDetailed_description(rs.getString(i++));
				inform.setBatch_number(rs.getInt(i++));
				inform.setNumber_limit(rs.getInt(i++));
				inform.setClosing_date(rs.getString(i++));
				inform.setReality_number(rs.getInt(i++));
				inform.setGet_physicalList_count(rs.getInt(i++));
				inform.setGet_result_count(rs.getInt(i++));
				inform.setBatch_time_1(rs.getString(i++));
				inform.setBatch_time_2(rs.getString(i++));
				inform.setBatch_time_3(rs.getString(i++));
				inform.setBatch_time_4(rs.getString(i++));
				inform.setBatch_time_5(rs.getString(i++));
				inform.setBatch_time_6(rs.getString(i++));
				inform.setBatch_time_7(rs.getString(i++));
				inform.setBatch_time_8(rs.getString(i++));
			}
			return inform;
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return inform;
	}
}
