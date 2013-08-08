package com.matech.audit.service.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.framework.pub.db.DbUtil;

public class CustomerNameConfigService {
	
	private Connection conn = null;
	
	public CustomerNameConfigService(Connection conn) {
		this.conn = conn; 
	}
	
//	CREATE TABLE `k_customernameconfig` (
//	  `autoid` int(11) NOT NULL auto_increment,
//	  `name1` varchar(500) default NULL,
//	  `name2` varchar(500) default NULL,
//	  `property` varchar(100) default '0',
//	  PRIMARY KEY  (`autoid`)
//	) ENGINE=MyISAM DEFAULT CHARSET=gbk
	/**
	 * 没有MD5过的名称
	 * true：能找到，false：表不存在或找不到
	 * @param strKey 狗名
	 * @param mtKey MT包的机构名
	 * @return
	 * @throws Exception
	 */
	public boolean checkKeyDat(String strKey,String mtKey)throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean bool = true;
		String sql = "";
		try {
			sql = "select 1 from asdb.k_customerNameConfig where name1 = ? and name2 = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, strKey);
			ps.setString(1, mtKey);
			rs = ps.executeQuery();
			if(rs.next()){
				bool = true;
			}else{
				bool = false;
			}
			
		} catch (Exception e) {
			//没表就表示不用判断
			bool = false;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return bool;
	}
	
	
}
