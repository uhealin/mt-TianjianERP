package com.matech.audit.service.education;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.matech.audit.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;

public class EducationUpdateService implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Connection conn=null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		String nowTime=format.format(new Date());
		DateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		try {
			conn = new DBConnect().getConnect("");
			String sql="select registrationStartTime,registrationEndTime,id from k_education where state='1'";
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			while(rs.next()){
				String startTime=rs.getString(1);
				String endTime=rs.getString(2);
				String id=rs.getString(3);
				Date start=df.parse(startTime);
				Date end=df.parse(endTime);
				Date now=df.parse(nowTime);
				//把等待报名状态的改为报名中
				if(start.getTime()<=now.getTime() && now.getTime()<=end.getTime()){
					String sqlState1="update k_education set state='2' where id="+id;
					ps=conn.prepareStatement(sqlState1);
					ps.execute();
				}
			}
			//把过了报名时间的更新为报名截止
			String endSql="select id,registrationEndTime from k_education where state='2' or state='4'";
			ps=conn.prepareStatement(endSql);
			rs=ps.executeQuery();
			while(rs.next()){
				String id=rs.getString(1);
				String endTime=rs.getString(2);
				Date end=df.parse(endTime);
				Date now=df.parse(nowTime);
				if(end.getTime()<now.getTime()){
					String sqlState2="update k_education set state='3' where id="+id;
					ps=conn.prepareStatement(sqlState2);
					ps.execute();
				}
			}
			//把过了培训时间的更新为结束
			String overSql="select id,trainendtime from k_education where state!='5' and state!='6'";
			ps=conn.prepareStatement(overSql);
			rs=ps.executeQuery();
			while(rs.next()){
				String id=rs.getString(1);
				String endTime=rs.getString(2);
				Date end=df.parse(endTime);
				Date now=df.parse(nowTime);
				if(end.getTime()<now.getTime()){
					String sqlState2="update k_education set state='5' where id="+id;
					ps=conn.prepareStatement(sqlState2);
					ps.execute();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(conn);
		}
	}

}
