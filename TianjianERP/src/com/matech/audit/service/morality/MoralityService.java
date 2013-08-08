package com.matech.audit.service.morality;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.morality.model.Morality;
import com.matech.framework.pub.db.DbUtil;

public class MoralityService {
	private Connection conn = null ;
	
	public MoralityService(Connection conn) {
		this.conn = conn ;
	}
	/*
	 * 查找职业道德说明
	 */
	public Morality getMorality(){
		Morality m=null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql="select * from k_moralityEdit where type='职业道德说明' ";
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){
				m=new Morality();
				m.setId(rs.getString("id"));
				m.setContent(rs.getString("content"));
				m.setType(rs.getString("type"));
				m.setRemark(rs.getString("remark"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return m;
	}
	/*
	 * 查找年度独立性说明
	 */
	public Morality getYearMorality(){
		Morality m=null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql="select * from k_moralityEdit where type='年度独立性说明' ";
			ps=conn.prepareStatement(sql);
			rs=ps.executeQuery();
			if(rs.next()){
				m=new Morality();
				m.setId(rs.getString("id"));
				m.setContent(rs.getString("content"));
				m.setType(rs.getString("type"));
				m.setRemark(rs.getString("remark"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return m;
	}
	/*
	 * 保存职业道德说明
	 */
	public boolean saveMorality(String content){
		PreparedStatement ps = null;
		ResultSet rs = null;
		int count=0;
		boolean result=false;
		try {
			String countSql="select count(id) from k_moralityedit where type=?";
			ps=conn.prepareStatement(countSql);
			ps.setString(1, "职业道德说明");
			rs=ps.executeQuery();
			if(rs.next()){
				count=Integer.valueOf(rs.getString(1));
			}
			if(count>0){
				String updateSql="update k_moralityedit set content=? where type=?";
				ps=conn.prepareStatement(updateSql);
				ps.setString(1, content);
				ps.setString(2, "职业道德说明");
				ps.execute();
				result=true;
			}else{
				String insertSql="insert into k_moralityedit (content,`type`) values(?,?)";
				ps=conn.prepareStatement(insertSql);
				ps.setString(1, content);
				ps.setString(2, "职业道德说明");
				ps.execute();
				result=true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return result;
	}
	/*
	 * 保存年度独立性说明
	 */
	public boolean saveYear(String content){
		PreparedStatement ps = null;
		ResultSet rs = null;
		int count=0;
		boolean result=false;
		try {
			String countSql="select count(id) from k_moralityedit where type=? ";
			ps=conn.prepareStatement(countSql);
			ps.setString(1, "年度独立性说明");
			rs=ps.executeQuery();
			if(rs.next()){
				count=Integer.valueOf(rs.getString(1));
			}
			
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			if(count>0){
				String updateSql="update k_moralityedit set content=? where type=? ";
				ps=conn.prepareStatement(updateSql);
				ps.setString(1, content);
				ps.setString(2, "年度独立性说明");
				ps.execute();
				result=true;
			}else{
				String insertSql="insert into k_moralityedit (content,`type`) values(?,?)";
				ps=conn.prepareStatement(insertSql);
				ps.setString(1, content);
				ps.setString(2, "年度独立性说明");
				ps.execute();
				result=true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return result;
	}
}
