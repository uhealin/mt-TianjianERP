package com.matech.audit.service.official;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.matech.framework.pub.db.DbUtil;

public class OfficialService {
	
	
	Connection conn = null;
	
	public OfficialService(Connection conn) {
		this.conn = conn;
	}
	
	/**
	 * 检查是否大于45天
	 * @return
	 */
	public String checkDate(String cardNum){
		
		PreparedStatement ps=null;
		
		ResultSet rs=null;
		
		String result="1";
		
		
		String sql="SELECT  case when DATEDIFF(now(),assume_office_time)>45 or isneed='1' then '0' else '1' end as flag FROM HR_EMPLOYEE_REGISTER WHERE idcard=?";

		try {
			ps=conn.prepareStatement(sql);
			ps.setString(1,cardNum);
			rs=ps.executeQuery();
			
			while(rs.next()){
				result=rs.getString("flag");
			}
			
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			DbUtil.close(rs);
		}
		
		return result;
		
	}
	
	
}
