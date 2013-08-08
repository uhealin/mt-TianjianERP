package com.matech.audit.service.educationtime;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.educationtime.model.EducationTime;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.exception.MatechException;
import com.matech.framework.pub.util.Debug;

public class EducationTimeService {
	private Connection conn=null;
	
	public EducationTimeService(Connection conn){
		this.conn=conn;
	}
	

	
	/**
	 * 学时
	 * @param id
	 * @return
	 */
	public EducationTime getEducationTime(String id){
		String sql ="select id,educationtime,username,hoursNum,hoursType,classNum,graduationNum from k_educationtime where id ="+id;
		EducationTime educationTime = new EducationTime();
	
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			ps = conn.prepareStatement(sql);
		    rs = ps.executeQuery();
			while(rs.next()){
				educationTime.setId(rs.getString("id"));
				educationTime.setEducationtime(rs.getString("educationtime"));
				educationTime.setUsername(rs.getString("username"));
				educationTime.setHoursNum(rs.getString("hoursNum"));
				educationTime.setHoursType(rs.getString("hoursType"));
				educationTime.setClassNum(rs.getString("classNum"));
				educationTime.setGraduationNum(rs.getString("graduationNum"));
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return educationTime; 
		
	}
	
	
	public void delTable() throws MatechException {
		String sql = "DROP TABLE IF EXISTS tt_k_educationtime";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
		} finally {
			DbUtil.close(ps);
		}

	}
	/**
    * EXCEL导入　
    */

   public void newTable() throws MatechException {
		delTable();
		String sql = "CREATE TABLE tt_k_educationtime like k_educationtime";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);

		} catch (Exception e) {
			Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
		} finally {
			DbUtil.close(ps);
		}
	}
	public String CheckUpData()throws MatechException{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		String result = "";
		try {
			
			sql = "select a.username from k_educationtime a,tt_k_educationtime b where a.graduationNum  = b.graduationNum and  a.classNum  = b.classNum";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				result +="<br>[<font color=blue>"+rs.getString(1)+"</font>]的学员重复，这条记录被忽略！";
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			return result;
		} catch (Exception e) {
			Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public void insertData()throws MatechException{
		PreparedStatement ps = null;
		try {
			//更新已有用户信息
			String sql =" UPDATE k_educationtime a , tt_k_educationtime b 	SET \n "+	 
						" a.educationtime = b.educationtime,a.time=YEAR(b.educationtime),  a.username = b.username,   a.hoursNum = b.hoursNum , a.hoursType = b.hoursType, \n" +
						" a.classNum  = b.classNum, a.graduationNum  = b.graduationNum,a.className=b.className,a.teacherName=b.teacherName \n" + 
						" WHERE a.graduationNum  = b.graduationNum and  a.classNum	= b.classNum";
			ps = conn.prepareStatement(sql);
			ps.execute();
			ps.execute("Flush tables");
			DbUtil.close(ps);
			
			//增加新用户	
			sql= "insert into k_educationtime \n" +
				  " (username,`time`,educationtime,hoursNum,hoursType,classNum,graduationNum,className,teacherName) \n"+
				  "	select distinct username,YEAR(educationtime),educationtime,\n"+
				  " hoursNum,hoursType,classNum, \n"+
				  " graduationNum,className,teacherName from tt_k_educationtime a where a.username not in \n"+
				  " (select b.username from k_educationtime a , tt_k_educationtime b \n" +
				  " where  a.graduationNum  = b.graduationNum and  a.classNum = b.classNum )";
			ps = conn.prepareStatement(sql);
			ps.execute();
			ps.execute("Flush tables");
			DbUtil.close(ps);

			delTable(); 
		}catch (Exception e) {
			Debug.print(Debug.iError,"访问失败",e);
			throw new MatechException("访问失败："+e.getMessage(),e);
		} finally {
			DbUtil.close(ps);
		}
	}

	public void updateTime(EducationTime educationtime,int id){
		   PreparedStatement ps = null;
		   try{
			   String sql ="delete from k_educationtime where id = "+educationtime.getId();
			   ps = conn.prepareStatement(sql);
			   ps.execute();
			   DbUtil.close(ps);
			   
			   sql = "insert into k_educationtime (educationtime,username,hoursNum,hoursType,classNum,graduationNum)" +
			   		" values (?,?,?,?,?,?)";
		      ps = conn.prepareStatement(sql);
		      ps.setString(1, educationtime.getEducationtime());
		      ps.setString(2, educationtime.getUsername());
		      ps.setString(3, educationtime.getHoursNum());
		      ps.setString(4, educationtime.getHoursType());
		      
		      ps.setString(5, educationtime.getClassNum());
		      ps.setString(6, educationtime.getGraduationNum());
		      ps.execute(); 
		   }catch(Exception e){
			   e.printStackTrace();
		   }finally{
			   DbUtil.close(ps);
		   }
		
	}
	/**
	 * 删除学时
	 */
	public void remove(int id){
		  PreparedStatement ps = null;
		  try{
			  String sql ="delete from k_educationtime where id = "+id;
			  ps = conn.prepareStatement(sql);
			  ps.execute();
		  }catch(Exception e){
			  e.printStackTrace();
		  }finally{
			  DbUtil.close(ps);
		  }
		
	}
	/*
	 * 查找登录人某年的总学时数
	 */
	public String getEducationTime(String year,String username){
		PreparedStatement ps = null;
		ResultSet rs = null;
		String time="0";
		try {
			String sql="select sum(hoursNum) hoursNum from k_educationtime where time='"+year+"' and username='"+username+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				time=rs.getString("hoursNum");
				if(time==null){
					time="0";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return time;
	}
}

	
