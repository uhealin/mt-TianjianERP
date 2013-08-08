package com.matech.audit.service.educationSetTime;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


import com.matech.audit.service.educationSetTime.model.EducationSetTime;
import com.matech.framework.pub.db.DbUtil;

public class EducationSetTimeService {
	private Connection conn=null;
	
	public EducationSetTimeService(Connection conn){
		this.conn=conn;
	}
	
	/*
	 * 插入一条纪录
	 */
	public void insertOne(EducationSetTime es){
		PreparedStatement ps=null;
		try {
			String sql="insert into k_educationsettime (userid,rankid,yearone,yeartwo,timeone,timetwo,stateone,statetwo) "
						+" values(?,?,?,?,?, ?,'未完成','未完成')";
			int i=1;
			ps=this.conn.prepareStatement(sql);
			ps.setString(i++, es.getUserId());
			ps.setString(i++, es.getRankId());
			ps.setString(i++, es.getYearOne());
			ps.setString(i++, es.getYearTwo());
			ps.setString(i++, es.getTimeOne());
			ps.setString(i++, es.getTimeTwo());
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	/*
	 * 删除一条记录
	 */
	public void del(String id){
		PreparedStatement ps=null;
		try {
			String sql="delete from k_educationsettime where id="+id+"";
			ps=this.conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	
	/*
	 * 更新记录
	 */
	public void updateOne(EducationSetTime est){
		PreparedStatement ps=null;
		try {
			String sql="update k_educationsettime set userid="+est.getUserId()+",rankid="+est.getRankId()
						+",yearone="+est.getYearOne()+",yeartwo="+est.getYearTwo()+",timeone="+est.getTimeOne()
						+",timetwo="+est.getTimeTwo()+" where id="+est.getId();
			ps=conn.prepareStatement(sql);
//			int i=1;
//			ps.setString(i++, est.getUserId());
//			ps.setString(i++, est.getRankId());
//			ps.setString(i++, est.getYearOne());
//			ps.setString(i++, est.getYearTwo());
//			ps.setString(i++, est.getTimeOne());
//			
//			ps.setString(i++, est.getTimeTwo());
//			ps.setString(i++, est.getId());
			System.out.println(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(ps);
		}
	}
	
	/*
	 * 跟据id查找数据
	 */
	public EducationSetTime findById(String id){
		PreparedStatement ps=null;
		ResultSet rs=null;
		EducationSetTime es=new EducationSetTime();
		try {
			String sql="select * from k_educationsettime where id="+id+"";
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){
				int i=1;
				
				es.setId(rs.getString(i++));
				es.setUserId(rs.getString(i++));
				es.setRankId(rs.getString(i++));
				es.setYearOne(rs.getString(i++));
				es.setYearTwo(rs.getString(i++));
				
				es.setTimeOne(rs.getString(i++));
				es.setTimeTwo(rs.getString(i++));
				es.setStateOne(rs.getString(i++));
				es.setStateTwo(rs.getString(i++));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return es;
	}
	/*
	 * 根据人员id,年份查找必修学时
	 */
	public EducationSetTime findByIdYear(String id,String year){
		EducationSetTime es=new EducationSetTime();
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			String sql="select * from k_educationsettime where rankid="+id+" and (yearone="+year+" or yeartwo="+year+")";
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){
				int i=1;
				
				es.setId(rs.getString(i++));
				es.setUserId(rs.getString(i++));
				es.setRankId(rs.getString(i++));
				es.setYearOne(rs.getString(i++));
				es.setYearTwo(rs.getString(i++));
				
				es.setTimeOne(rs.getString(i++));
				es.setTimeTwo(rs.getString(i++));
				es.setStateOne(rs.getString(i++));
				es.setStateTwo(rs.getString(i++));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return es;
	}
	/*
	 * 根据人员职级,年份查找必修学时
	 */
	public EducationSetTime findByRankYear(String id,String year){
		EducationSetTime es=new EducationSetTime();
		PreparedStatement ps=null;
		ResultSet rs=null;
		try {
			String sql="select * from k_educationsettime where rankid="+id+" and (yearone="+year+" or yeartwo="+year+")";
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){
				int i=1;
				
				es.setId(rs.getString(i++));
				es.setUserId(rs.getString(i++));
				es.setRankId(rs.getString(i++));
				es.setYearOne(rs.getString(i++));
				es.setYearTwo(rs.getString(i++));
				
				es.setTimeOne(rs.getString(i++));
				es.setTimeTwo(rs.getString(i++));
				es.setStateOne(rs.getString(i++));
				es.setStateTwo(rs.getString(i++));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return es;
	}
	/*
	 * 查找最大groupNum
	 */
	public String getMaxGroupNum(){
		PreparedStatement ps=null;
		ResultSet rs=null;
		String groupNum=null;
		try {
			String sql="select max(groupNum) from k_educationsettime";
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){
				groupNum=rs.getString(1);
				if(groupNum==null){
					groupNum="0";
				}
			}
			else{
				groupNum="0";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return groupNum;
	}
}
