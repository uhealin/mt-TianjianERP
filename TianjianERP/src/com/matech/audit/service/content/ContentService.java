package com.matech.audit.service.content;

import java.sql.Connection;

import com.matech.framework.pub.autocode.DELAutocode;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class ContentService {
	private Connection conn = null;
	
	public ContentService(Connection conn) {
		this.conn = conn;
	}
	
	//自动编号
	public String getAutoCode(String atype,String tablename,String field) throws Exception{
		String autoCode = "";
		try {
			DbUtil db=new DbUtil(conn);
			ASFuntion asf = new ASFuntion();
			DELAutocode t = new DELAutocode();
			String date = asf.replaceStr(asf.getCurrentDate(), "-", "").substring(0,6);
			
			//通过表得到最大的编号
			String maxId = asf.showNull(db.queryForString("select max("+field+") as maxId from " + tablename));
			if("".equals(maxId) || maxId.indexOf(date) == -1){
				//不存在就表示不是同一月，k_autocode还原
				db.update("k_autocode", "atype", atype, "curnum1", "0");
			}
			
			autoCode = t.getAutoCode(atype, "", new String[]{date});
			
			try {
				//重新标记成已用
				String sql="update k_autocodeused set state=?,applydate=? where atype=? and year=? and fullnumber=? ";
				db.execute(sql, new String[]{"1",asf.getCurrentDate(),atype,asf.getCurrentDate("yyyy"),autoCode})  ;
			} catch (Exception e) {

			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
		return autoCode;
	}
	
}
