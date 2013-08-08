package com.matech.audit.service.teacher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.matech.audit.service.teacher.model.Student;
import com.matech.framework.pub.autocode.DELAutocode;
import com.matech.framework.pub.db.DbUtil;

public class StudentService {
	/*公司规定，如果是list的查询业务直接在Action里面完成，相对较繁琐的业务则是在service里面操作完成
	 */
	//定义数据库的连接
	private Connection conn = null;
	//使用构造方法获得连接
	public StudentService(Connection conn) {
		this.conn = conn;
	}
	//增加数据
	public void add(Student student){
		//封装sql语句
		PreparedStatement ps = null;
		//DELAutocode da = new DELAutocode();
		try {
			String sql = "insert into k_studnet(stuName,gender,description) values(?,?,?)";
			ps = this.conn.prepareStatement(sql);
			//int i = 1;
			ps.setString(1, student.getStuName());
			ps.setString(2, student.getGender());
			ps.setString(3, student.getDescription());
			//执行
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	public Student findById(int id ) {
		Student student = new Student();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select * from k_student where id=" + id +"";
			ps = this.conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				student.setStuName(rs.getString(1));
				student.setId(Integer.parseInt(rs.getString(2)));
				student.setGender(rs.getString(3));
				student.setDescription(rs.getString(4));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
			//DbUtil.close(rs);
		}
		return student;
	}
	public void del(int id) {
		PreparedStatement ps = null;
		try {
			String sql = "delect from k_studnet where id="+id;
			ps = this.conn.prepareStatement(sql);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
	public void edit(Student student) {
		PreparedStatement ps = null;
		try {
			String sql = "udata k_student s set s.stuName="+student.getStuName()+"s.gender"+student.getGender()+"s.description"+student.getDescription();
			ps = this.conn.prepareStatement(sql);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
}
