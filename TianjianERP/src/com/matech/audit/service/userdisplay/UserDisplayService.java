package com.matech.audit.service.userdisplay;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.framework.pub.db.DbUtil;
/**
 * <p>Title: 用户分辨率以及分页数管理类</p>
 * <p>Description: 用户分辨率以及分页数管理类</p>
 * <p>Copyright: Copyright (c) 2006, 2008 MaTech Corporation.All rights reserved. </p>
 * <p>Company: Matech  广州铭太信息科技有限公司</p>
 *
 * 本程序及其相关的所有资源均为铭太科技公司研发中心审计开发组所有，
 * 版本发行及解释权归研发中心，公司网站为：http://www.matech.cn
 *
 * 贡献者团队:
 *     铭太科技 - 研发中心，审计开发组
 *
 * @author k
 * 2007-6-14
 */
public class UserDisplayService {

	private Connection conn;

	public UserDisplayService(Connection conn) throws Exception {
		DbUtil.checkConn(conn);
		this.conn=conn;
	}
	/**
	 * @param uId	用户名
	 * @param pId	项目ID
	 * @throws Exception
	 */
	public void setLastProject(String uId,String pId) throws Exception {
		if( (uId != null && !uId.equals("")) && (pId != null && !pId.equals("")) ) {

			PreparedStatement ps = null;

		    try {
		    	String strSql = "update k_userdisplay set lastproject = ? where userid = ?";

		    	ps = conn.prepareStatement(strSql);
		    	ps.setString(1, pId);
		    	ps.setString(2, uId);

		    	ps.execute();
		    }catch(Exception ex) {
				ex.printStackTrace();
			}finally {
		    	if (ps != null)
		    		ps.close();

			}
		}
	}

	/**
	 * @param user	用户名
	 * @param taskInfo	底稿信息	例如:2007635`101937`1621560451` 表示：项目ID`底稿ID`UNID`类型ID
	 * @throws Exception
	 */
	public void setLastTask(String user,String taskInfo) throws Exception {
		if( (user != null && !user.equals("")) && (taskInfo != null && !taskInfo.equals("")) ) {

			PreparedStatement ps = null;

		    try {
		    	String strSql = "update k_userdisplay set lastTask = ? where userid = ?";

		    	ps = conn.prepareStatement(strSql);
		    	ps.setString(1, taskInfo);
		    	ps.setString(2, user);

		    	ps.execute();
		    }catch(Exception ex) {
				ex.printStackTrace();
			}finally {
		    	if (ps != null)
		    		ps.close();

			}
		}
	}

	/**
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public String getLastProject(String uId) throws Exception {
		String pId = null;
		PreparedStatement ps = null;
	    ResultSet rs = null;

	    try {
	    	String strSql = "select lastproject from k_userdisplay where userid = ?";
	    	ps = conn.prepareStatement(strSql);
	    	ps.setString(1, uId);
	    	rs = ps.executeQuery();

	    	if( rs.next() ) {
	    		pId = rs.getString(1);
	    	}

	    	return pId;
	    }catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
		finally {
			if (rs != null)
	    		rs.close();
	    	if (ps != null)
	    		ps.close();

		}
	}

	/**
	 * 返回用户最后使用的底稿信息,例如:2007635`101937`1621560451` 表示：项目ID`底稿ID`UNID`类型ID
	 * @param uId
	 * @return
	 * @throws Exception
	 */
	public String getLastTask(String uId) throws Exception {
		String pId = null;
		PreparedStatement ps = null;
	    ResultSet rs = null;

	    try {
	    	String strSql = "select lastTask from k_userdisplay where userid = ?";
	    	ps = conn.prepareStatement(strSql);
	    	ps.setString(1, uId);
	    	rs = ps.executeQuery();

	    	if( rs.next() ) {
	    		pId = rs.getString(1);
	    	}

	    	return pId;
	    }catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
		finally {
			if (rs != null)
	    		rs.close();
	    	if (ps != null)
	    		ps.close();
		}
	}

	/**
	 * 获取用户分页数
	 *
	 * @param userId		用户ID
	 * @param userScreen	用户分辨率
	 * @return				分页数
	 * @throws Exception
	 */
	public int getUserPageSize(String userId,String userScreen) throws Exception {
		PreparedStatement ps = null;
	    ResultSet rs = null;

	    String strSql = "";
	    int pageSize = 50;
	    try {
			strSql = "select pageSize from k_userdisplay where userId = ? and screen = ?";
	    	ps = conn.prepareStatement(strSql);
	    	ps.setString(1, userId);
	    	ps.setString(2, userScreen);

	    	rs = ps.executeQuery();

	    	if(rs.next()) {
	    		pageSize = rs.getInt(1);
	    	} else {
	    		switch(Integer.parseInt(userScreen)) {
					case  800: pageSize = 10; break;
					case 1024: pageSize = 20; break;
					case 1280: pageSize = 30; break;
					case 1400: pageSize = 40; break;

					default: pageSize = 20; break;
	    		}
	    		setUserPageSize(userId,userScreen,pageSize);
	    	}
	    	return pageSize;
		}catch(Exception ex) {
			//ex.printStackTrace();
			return pageSize;
		}
		finally {
			if (rs != null)
	    		rs.close();
	    	if (ps != null)
	    		ps.close();
		}
	} //end method:getUserPageSize;

	/**
	 * 设置用户分辨率和分页数
	 *
	 * @param userId		用户ID
	 * @param userScreen	用户分辨率
	 * @param userPageSize	分页数
	 * @throws Exception
	 */
	public void setUserPageSize(String userId,String userScreen,int userPageSize) throws Exception {
		PreparedStatement ps = null;
	    ResultSet rs = null;

		String id = userId;
		String screen = userScreen;
		String strSql = "";

		int pageSize = userPageSize;

		try {
			if((id != null && !id.equals("")) && (screen != null && !screen.equals(""))) {
				strSql = "select userId,screen from k_userdisplay where userId = ? and screen = ?";
		    	ps = conn.prepareStatement(strSql);
		    	ps.setString(1, id);
		    	ps.setString(2, screen);

		    	rs = ps.executeQuery();

		    	if(rs.next()) {
		    		strSql = "update k_userdisplay set pageSize = ? where userId = ? and screen = ?";
		    		ps = null;
		    		ps = conn.prepareStatement(strSql);
		    		ps.setInt(1, pageSize);
		    		ps.setString(2, id);
		    		ps.setString(3, screen);

		    		ps.execute();
		    	} else {
		    		strSql = "insert into k_userdisplay(pageSize,userId,screen) values(?,?,?)";
		    		ps = null;
		    		ps = conn.prepareStatement(strSql);
		    		ps.setInt(1, pageSize);
		    		ps.setString(2, id);
		    		ps.setString(3, screen);

		    		ps.execute();
		    	}
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}finally {
			if (rs != null)
	    		rs.close();
	    	if (ps != null)
	    		ps.close();

		}
	}// end method:setUserPageSize;


	/**
	 * 根据用户ID删除用户自定义分页等信息
	 * @param id	autoid
	 * @throws Exception
	 */
	public void delByUserId(String id) throws Exception {
		PreparedStatement ps = null;

		try {

			String strSql = "delete from `k_userdisplay` where userid = ? ";
	    	ps = conn.prepareStatement(strSql);
	    	ps.setString(1, id);

	    	ps.executeUpdate();

		}catch(Exception ex) {
			ex.printStackTrace();
		}finally {
	    	if (ps != null)
	    		ps.close();
		}
	}

	/**
	 * test
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
//		UserDisplayService userDisplayMan = new UserDisplayService();
//		//userDisplayMan.setUserPageSize("admin", "1024", 50);
//		//org.util.Debug.prtOut(userDisplayMan.getUserPageSize("admin", "1024"));
//		userDisplayMan.setLastProject("admin", "22222");
	}
}// end class
