package com.matech.audit.service.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.service.customer.model.Follow;
import com.matech.framework.pub.db.DbUtil;

public class FollowService {
	private Connection conn = null;

	public FollowService(Connection conn1) 
			throws Exception {
		this.conn = conn1;
	}
	
	public void add(Follow follow){
		PreparedStatement ps = null;
		try{
			String sql="insert into k_follow (customer,linkpeople,followtime,followcontent,followstatus,nexttime,nextcontent," +
					"nextstatus,disman,followman,createUser,createDepartment,createTime) values (?,?,?,?,? ,?,?,?,?,?, ?,?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, follow.getCustomer());
			ps.setString(2, follow.getLinkpeople());
			ps.setString(3, follow.getFollowtime());
			ps.setString(4, follow.getFollowcontent());
			ps.setString(5, follow.getFollowstatus());
			ps.setString(6, follow.getNexttime());
			ps.setString(7, follow.getNextcontent());
			ps.setString(8,follow.getNextstatus());
			ps.setString(9, follow.getDisman());
			ps.setString(10,follow.getFollowman());
			ps.setString(11,follow.getCreateUser());
			ps.setString(12,follow.getCreateDepartment());
			ps.setString(13,follow.getCreateTime());
			ps.execute();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}

	}
	public void update(Follow follow){
		  PreparedStatement ps = null;
		  try{
			  String sql ="update k_follow set customer=?,linkpeople=?,followtime=?,followcontent=?,followstatus=?,nexttime=?," +
			  		"nextcontent=?,nextstatus=?,disman=?,followman=? where autoId=?";
			  ps = conn.prepareStatement(sql);
			  ps.setString(1, follow.getCustomer());
			  ps.setString(2, follow.getLinkpeople());
			  ps.setString(3, follow.getFollowtime());
			  ps.setString(4, follow.getFollowcontent());
			  ps.setString(5, follow.getFollowstatus());
			  ps.setString(6, follow.getNexttime());
			  ps.setString(7, follow.getNextcontent());
			  ps.setString(8, follow.getNextstatus());
			  ps.setString(9, follow.getDisman());
			  ps.setString(10, follow.getFollowman());
			  ps.setString(11, follow.getAutoId());
			  ps.execute();
		  }catch(Exception e){
			  e.printStackTrace();
		  }finally{
			  DbUtil.close(ps);
		  }
		  
	} 
	public Follow getFollow(String autoId){
		  Follow follow = new Follow();
		  PreparedStatement ps = null;
		  ResultSet rs = null;
		  try{
			  String sql ="select autoId,customer,linkpeople,followtime,followcontent,followstatus,nexttime," +
			  		"nextcontent,nextstatus,disman,followman from k_follow where autoId="+autoId;
			  ps = conn.prepareStatement(sql);
			  rs = ps.executeQuery();
			  if(rs.next()){
				  follow.setAutoId(rs.getString("autoId"));
				  follow.setCustomer(rs.getString("customer"));
				  follow.setLinkpeople(rs.getString("linkpeople"));
				  follow.setFollowtime(rs.getString("followtime"));
				  follow.setFollowcontent(rs.getString("followcontent"));
				  follow.setFollowstatus(rs.getString("followstatus"));
				  follow.setNexttime(rs.getString("nexttime"));
				  follow.setNextcontent(rs.getString("nextcontent"));
				  follow.setNextstatus(rs.getString("nextstatus"));
				  follow.setDisman(rs.getString("disman"));
				  follow.setFollowman(rs.getString("followman"));
			  }
		  }catch(Exception e){
			  e.printStackTrace();
		  }finally{
			  DbUtil.close(ps);
			  DbUtil.close(rs);
		  }
		  return follow;
	  }
	public void delete(String autoId){
		PreparedStatement ps = null;
		try{
			String sql ="delete from k_follow where autoId="+autoId;
			ps = conn.prepareStatement(sql);
			ps.execute();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}

}
