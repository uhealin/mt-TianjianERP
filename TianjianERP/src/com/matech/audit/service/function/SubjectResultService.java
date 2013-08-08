package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.framework.pub.db.DbUtil;
import com.matech.audit.pub.db.*;

public class SubjectResultService {

	private Connection conn;

	private String pkgid;

	private String keyResultProperty = "0";
	
	public SubjectResultService(Connection conn, String pkgid) {
		keyResultProperty=com.matech.framework.pub.sys.UTILSysProperty.SysProperty.getProperty("KeyResult");
		this.conn = conn;
		this.pkgid = pkgid; // 帐套编号
	}

	public String getNewPackageID(String acc) throws Exception {
		String result = "";
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			result = acc.substring(0, 6);
			int intStr = Integer.parseInt(acc.substring(6)) - 1;
			result += String.valueOf(intStr);
			sql = "select 1 from c_accpackage where AccPackageID = '" + result
					+ "' limit 1";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				return result;
			} else {
				return "该单位没有去年的帐套!";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "程序出错!";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public boolean isNull(String tabName, String colName, String FullName,String AccName) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		String str = "";
		// pkgid
		try {
			if (tabName.equalsIgnoreCase("c_accpkgsubject")) {
				str = " and " + colName + " = '" + FullName + "' ";
			} else if (tabName.equalsIgnoreCase("c_assitem")) {
				if (colName.equalsIgnoreCase("000")) {
					str = " and AssItemName = '"
							+ FullName
							+ "' and accid = (select subjectid from c_accpkgsubject where AccPackageID='"
							+ pkgid + "' and SubjectName in " + AccName + ")";
				} else {
					str = " and AssTotalName = '"
							+ FullName
							+ "' and accid = (select subjectid from c_accpkgsubject where AccPackageID='"
							+ pkgid + "' and SubjectFullName in " + AccName
							+ ")";
				}
			}
			sql = "select count(*) from " + tabName + " where AccPackageID='"
					+ pkgid + "' " + str;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				if (rs.getInt(1) > 0) {
					return true;
				} else {
					return false;
				}
			}
			return false;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	public String SubjectResult(String key1) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String did = pkgid.substring(0, 6);
			String str = "(";
			int ii = 0;
			String sql = "select distinct subjectfullname1 from c_account where AccPackageID='"
					+ pkgid + "' and subjectfullname2 = '" + key1 + "'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				ii++;
				str += "'" + rs.getString(1) + "',";
			}

			if (ii == 0) {
				sql = "select userkey from z_keyresult where customerid='"
						+ did
						+ "'  and standkey in (select standkey from z_keyresult where userkey='"
						+ key1 + "' and customerid='" + did + "')";
				org.util.Debug.prtOut("111 TextKey sql:" + sql);
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();

				while (rs.next()) {
					str += "'" + rs.getString(1) + "',";
				}
			}
			str += "'" + key1 + "')";
			org.util.Debug.prtOut("str=" + str);
			return str;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public String TextKey(String key1) throws Exception {		
		PreparedStatement ps = null;
		ResultSet rs = null;
		// union
		// select b.key2
		// from k_key b
		// where '实收资本' like concat('%',b.key1,'%')
		try {
			new DBConnect().changeDataBase("", conn);
			
			String sql = "select DISTINCT IFNULL(key1,gs) gs from k_key a right join (select '"
					+ key1
					+ "' as gs union select b.key2 gs from k_key b where b.key1 like concat('%','"
					+ key1
					+ "','%') union select trim(replace(replace(replace('"
					+ key1
					+ "                                                    ',b.key1,b.key2),c.key1,c.key2),d.key1,d.key2)) gs from k_key b,k_key c,k_key d where '"
					+ key1 + "' like concat('%',b.key1,'%') and '" + key1
					+ "' like concat('%',c.key1,'%') and '" + key1
					+ "' like concat('%',d.key1,'%')) b on a.key1=b.gs or a.key2=b.gs";
			
			org.util.Debug.prtOut("TextKey sql:"+sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			String str = "(";
			while (rs.next()) {
				str += "'" + rs.getString(1) + "',";
			}
			str += "'" + key1 + "')";
			org.util.Debug.prtOut("str=" + str);
			
			new DBConnect().changeDataBase(pkgid.substring(0,6), conn);
			return str;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public String TextKey(String[] key1) throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		// union
		// select b.key2
		// from k_key b
		// where '实收资本' like concat('%',b.key1,'%')
		try {
			new DBConnect().changeDataBase("", conn);
			
			String str = "(";
			for (int i = 0; i < key1.length; i++) {

				String sql = "select '"
						+ key1[i]
						+ "' as gs union select b.key2 from k_key b where b.key1 like concat('%','"
						+ key1[i]
						+ "','%') union select trim(replace(replace(replace('"
						+ key1[i]
						+ "                                               ',b.key1,b.key2),c.key1,c.key2),d.key1,d.key2)) from k_key b,k_key c,k_key d where '"
						+ key1[i] + "' like concat('%',b.key1,'%') and '"
						+ key1[i] + "' like concat('%',c.key1,'%') and '"
						+ key1[i] + "' like concat('%',d.key1,'%')";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();

				while (rs.next()) {
					str += "'" + rs.getString(1) + "',";
				}
				// str += "'" + key1[i] + "'";
			}
			str = str.substring(0, str.length() - 1) + ")";
			org.util.Debug.prtOut("str=" + str);
			new DBConnect().changeDataBase(pkgid.substring(0,6), conn);
			
			return str;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public String getTextKey(String key1)throws Exception {		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		String result = "";
		String str = "(";
		try {		
			sql = "select * from z_keyresult where  userkey = '"+key1+"' and customerid='"+pkgid.substring(0,6)+"'";
			org.util.Debug.prtOut(sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int ii=0;
			while(rs.next()){
				ii++;
				if(rs.getInt("level0")==1){
					sql = "select * from z_keyresult where standkey = '"+rs.getString("standkey")+"' and level0=1";
					org.util.Debug.prtOut(sql);
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					while(rs.next()){
						str += "'" + rs.getString("userkey") + "',";
					}
					
				}else{
					str += "'" + rs.getString("userkey") + "',";
				}
			}
			if(ii==0){
				sql = "select * from z_keyresult where  standkey = '"+key1+"' and customerid='"+pkgid.substring(0,6)+"'  and level0=1";
				org.util.Debug.prtOut(sql);
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					str += "'" + rs.getString("userkey") + "',";
				}
				str +="'" + key1 + "')";
			}else{
				str += "'" + key1 + "')";
			}
			return str;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public String getTextKeyAll(String key1) throws Exception {		
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSet rss = null;
		String sql = "";
		String result = "";
		try {
			
			Account(pkgid, key1);
			
			org.util.Debug.prtOut("acc:"+pkgid);		
			org.util.Debug.prtOut("key1:"+key1);
			String opt = getTextKey(key1);
			sql = "select * from c_account where accpackageid='"
					+ pkgid
					+ "'  and (Subjectid in "
					+ opt
					+ " or accname in "
					+ opt
					+ " or subjectfullname1 in "
					+ opt + " or subjectfullname2 in "+opt+") and submonth=1 ";
			org.util.Debug.prtOut("getTextKey:"+sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			String sid = "";
			int ii=0;
//			if(rs.next()){
			org.util.Debug.prtOut(opt);
			while (rs.next()) {
				ii++;
				if(opt.indexOf(rs.getString("subjectfullname2"))>-1 || opt.indexOf(rs.getString("subjectfullname1"))>-1 || opt.indexOf(rs.getString("subjectid"))>-1 ){
					sid = rs.getString("subjectid");
					key1 = rs.getString("subjectfullname2");
					result = rs.getString("subjectfullname1") + "|";
					
					org.util.Debug.prtOut("subjectfullname2:"+rs.getString("subjectfullname2"));
					
					sql = "select DISTINCT subjectid,level1 from c_account where (subjectfullname2 like concat('"
						+ rs.getString("subjectfullname2")
						+ "','/%') or subjectfullname2='"+rs.getString("subjectfullname2")+"')  and accpackageid='"
						+ pkgid + "' order by subjectid";
					org.util.Debug.prtOut("getTextKeyAll:"+sql);
					ps = conn.prepareStatement(sql);
					rss = ps.executeQuery();
					while (rss.next()) {
						if(sid.equals(rss.getString("subjectid"))){
							if(rss.getInt("level1")==1){
								result += rss.getString("subjectid") + "`";
							}else{
								result += rss.getString("subjectid") + "`";
								break;
							}
						}else{
							if(rss.getInt("level1")==1){
								result += rss.getString("subjectid") + "`";
							}
						}					
					}
				
				}
			}
			if(ii==0){
				String []str = opt.split("'");
				String [] ss = new String[str.length-1];
				int si = 0;
				for (int i = 0; i < str.length; i++) {
					if(!"".equals(str[i]) && !"(".equals(str[i]) && !",".equals(str[i]) && !")".equals(str[i])){
						ss[si++] = str[i];
					}
				}
				
				if("0".equals(keyResultProperty)){
					opt = TextKey(ss);
				}
				
				
				sql = "select * from c_accpkgsubject where accpackageid='"
					+ pkgid
					+ "'  and (Subjectid in "
					+ opt
					+ " or subjectname in "
					+ opt
					+ " or subjectfullname in "
					+ opt + ") ";
				org.util.Debug.prtOut("ii sql="+sql);
				
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					result = rs.getString("subjectfullname") + "|"+rs.getString("subjectid") + "`";
				}
				
			}
			org.util.Debug.prtOut(result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public String getTextKeyAll(String key1, String 	pid) throws Exception {		
		PreparedStatement ps = null;
		ResultSet rs = null;
		ResultSet rss = null;
		String sql = "";
		String result = "";
		try {						
			
			org.util.Debug.prtOut("key1:"+key1);
			String opt = getTextKey(key1);
			sql = "select * from c_account where accpackageid='"
					+ pkgid
					+ "'  and (Subjectid in "
					+ opt
					+ " or subjectfullname1 in "
					+ opt
					+ " or subjectfullname2 in "
					+ opt + " ) and submonth=1 ";
			org.util.Debug.prtOut("getTextKey:"+sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			String sid = "";
			int ii=0;
//			if(rs.next()){
			org.util.Debug.prtOut(opt);
			while (rs.next()) {
				ii++;
				if(opt.indexOf(rs.getString("subjectfullname2"))>-1 || opt.indexOf(rs.getString("subjectfullname1"))>-1 || opt.indexOf(rs.getString("subjectid"))>-1 ){
					ii++;
				sid = rs.getString("subjectid");
				key1 = rs.getString("subjectfullname2");
				result = rs.getString("subjectfullname1") + "|";
				
				org.util.Debug.prtOut("subjectfullname2:"+rs.getString("subjectfullname2"));
				
				
				sql = "select DISTINCT subjectid,level1 from c_account where (subjectfullname2 like concat('"
					+ rs.getString("subjectfullname2")
					+ "','/%') or subjectfullname2='"+rs.getString("subjectfullname2")+"')  and accpackageid='"
					+ pkgid + "' order by subjectid";
				org.util.Debug.prtOut("getTextKeyAll:"+sql);
				ps = conn.prepareStatement(sql);
				rss = ps.executeQuery();
				while (rss.next()) {
					if(sid.equals(rss.getString("subjectid"))){
						if(rss.getInt("level1")==1){
							result += rss.getString("subjectid") + "`";
						}else{
							result += rss.getString("subjectid") + "`";
							break;
						}
					}else{
						if(rss.getInt("level1")==1){
							result += rss.getString("subjectid") + "`";
						}
					}
					org.util.Debug.prtOut("result="+result);
				}
				
			}
			}
			
			if(ii==0){
				
				sql = "select * from c_accpkgsubject where accpackageid='"
					+ pkgid
					+ "'  and (Subjectid in "
					+ opt
					
					+ " or subjectfullname in "
					+ opt + ") ";
				org.util.Debug.prtOut("ii sql ="+sql);
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					result = rs.getString("subjectfullname") + "|"+rs.getString("subjectid") + "`";
				}else{
					sql = "select * from z_usesubject where AccPackageID='"+pkgid+"' and projectID='"+pid+"' " +
						"and (Subjectid in "+opt+" or subjectname in "+opt+" or subjectfullname in "+opt+") ";
					org.util.Debug.prtOut("iii sql ="+sql);
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();
					if(rs.next()){
						result = rs.getString("subjectfullname") + "|"+rs.getString("subjectid") + "`";
					}
				}
				
			}
			org.util.Debug.prtOut(result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	//得到所有的下级科目
	public String getDownSubjectAll(String subjectid) throws Exception {		
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		String result = "";
		try {			
			sql = "select * from c_accpkgsubject where accpackageid='"
					+ pkgid + "'  and parentsubjectid='" + subjectid + "' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				result += rs.getString("subjectid") + "`";
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public void Account(String acc,String SubjectID) throws Exception {		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select * from c_accpkgsubject a where a.AccPackageID='"+acc+"' and a.subjectid='"+SubjectID+"'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				sql = "select * from c_account a where a.AccPackageID='"+acc+"' and a.subjectid='"+SubjectID+"' limit 1";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(!rs.next()){
					sql = "insert into c_account(accpackageid,subjectid,accname,subjectfullname1,isleaf1, level1,SubYearMonth,SubMonth,DataName, direction,    DebitRemain,CreditRemain,DebitOcc,CreditOcc, Balance,DebitTotalOcc,CreditTotalOcc,DebitBalance,CreditBalance) select accpackageid,subjectid,SubjectName,SubjectFullName,IsLeaf,level0,substring(accpackageid,7) as year      ,b.submonth        ,0, case substring(property,2,1) when 2 then '-1' else substring(property,2,1) end as direction,  0,0,0,0,0,0,0,0,0 from c_accpkgsubject a inner join k_month b where b.monthtype=12 and   a.accpackageid='"+acc+"' and subjectid in ('"+SubjectID+"')";
					ps = conn.prepareStatement(sql);
					ps.execute();
					new com.matech.audit.service.keys.KeyValue().createFullPath(conn, acc.substring(0, 6),SubjectID);
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public String  getProjectEndYear(String projectID) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			 String sql = "select substring(audittimeend,1,4) from asdb.z_project where projectid='"+projectID+"' ";
			 ps = conn.prepareStatement(sql);
			 rs = ps.executeQuery();
			 rs.next();
			 return rs.getString(1);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
}
