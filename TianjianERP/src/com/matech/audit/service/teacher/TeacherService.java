package com.matech.audit.service.teacher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.teacher.model.Teacher;
import com.matech.framework.pub.autocode.DELAutocode;
import com.matech.framework.pub.db.DbUtil;


public class TeacherService {
	private Connection conn=null;
	
	public TeacherService(Connection conn){
		this.conn=conn;
	}
	
	/*
	 * 插入一条老师记录
	 */
	public void add(Teacher teacher){
		PreparedStatement ps=null;
		DELAutocode da=new DELAutocode();
		try {
			String num=da.getAutoCode("JSBH", "");
			String sql="insert into k_teacher (name,professional,company,title,position,sex,ugg,teacherNum,state)"
						+" values(?,?,?,?,  ?,?,?,?, ?)";
			int i=1;
			ps=this.conn.prepareStatement(sql);
			ps.setString(i++, teacher.getName());
			ps.setString(i++, teacher.getProfessional());
			ps.setString(i++, teacher.getCompany());
			ps.setString(i++, teacher.getTitle());
			ps.setString(i++, teacher.getPosition());
			ps.setString(i++, teacher.getSex());
			ps.setString(i++, teacher.getUgg());
			ps.setString(i++, num);
			ps.setString(i++, teacher.getState());
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	
	/*
	 * 根据id查找
	 */
	public Teacher findById(int id){
		PreparedStatement ps=null;
		ResultSet rs=null;
		Teacher teacher=new Teacher();
		try {
			String sql="select * from k_teacher where id="+id+"";
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){
				teacher.setId(Integer.valueOf(rs.getString(1)));
				teacher.setCompany(rs.getString(4));
				teacher.setName(rs.getString(2));
				teacher.setPosition(rs.getString(6));
				teacher.setProfessional(rs.getString(3));
				teacher.setSex(rs.getString(7));
				teacher.setTeacherNum(rs.getString(9));
				teacher.setTitle(rs.getString(5));
				teacher.setUgg(rs.getString(8));
				teacher.setState(rs.getString("state"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
		return teacher;
	}
	
	/*
	 * 更新老师记录
	 */
	public void update(Teacher teacher){
		PreparedStatement ps=null;
		try {
			String sql="update k_teacher set name='"+teacher.getName()+"',professional='"+teacher.getProfessional()+"',company='"+teacher.getCompany()
						+"',title='"+teacher.getTitle()+"',position='"+teacher.getPosition()+"',sex='"+teacher.getSex()
						+"',ugg='"+teacher.getUgg()+"',teacherNum='"+teacher.getTeacherNum()+"', state='"+teacher.getState()+"' where id="+teacher.getId();
			System.out.println(sql);
			ps=this.conn.prepareStatement(sql);
//			int i=1;
//			ps.setString(i++, teacher.getName());
//			ps.setString(i++, teacher.getProfessional());
//			ps.setString(i++, teacher.getCompany());
//			ps.setString(i++, teacher.getTitle());
//			
//			ps.setString(i++, teacher.getPosition());
//			ps.setString(i++, teacher.getSex());
//			ps.setString(i++, teacher.getUgg());
//			ps.setString(i++, teacher.getTeacherNum());
//			
//			ps.setInt(i++, teacher.getId());
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	
	/*
	 * 删除老师数据
	 */
	public void del(int id){
		PreparedStatement ps=null;
		try {
			String sql="delete from k_teacher where id="+id+"";
			System.out.println(sql);
			ps=this.conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
}
