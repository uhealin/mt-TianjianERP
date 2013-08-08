package com.matech.audit.service.autotoken;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.audit.pub.db.DBConnect;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class AutoTokenService {
	
	private Connection conn = null;
	
	public AutoTokenService(Connection conn) {
		this.conn = conn;
	}
	
	/**
	 * 连序性自动对照,一次只对照这个客户的一年的帐套
	 * @param departID
	 * @param accpackageid
	 * @throws Exception
	 */
	public void autoOne(String accpackageid)throws Exception {
		
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		ResultSet rs = null;
		try {
				ASFuntion CHF = new ASFuntion();
				
//				System.out.println("AutoTokenService20:"+CHF.getCurrentTime());
				//一级科目:一级标准科目名称
				String sql = "update c_account a set tokenid =subjectfullname2 ,standname=AccName where AccPackageID=? and level1 = 1 ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, accpackageid);
				ps.execute();
				DbUtil.close(ps);
				
//				System.out.println("AutoTokenService21:"+CHF.getCurrentTime());
				//二级及以上科目:一级标准科目名称/末级科目名称
				sql = "select distinct subjectfullname1,subjectfullname2 from c_account where submonth=1 and level1=1 and  AccPackageID =?";
				ps = conn.prepareStatement(sql);
				ps.setString(1, accpackageid);
				rs = ps.executeQuery();
				while(rs.next()){
					String string1 = rs.getString(1);
					String string2 = rs.getString(2);
					
					sql = "update c_account a  " +
					" set tokenid = concat(?,'/',a.AccName),standname=AccName " +
					" where  a.subjectfullname1 like concat(?, '/%') " +
					" and level1 >1 " +
					" and AccPackageID =?";
					ps1 = conn.prepareStatement(sql);
					ps1.setString(1, string2);
					ps1.setString(2, string1);
					ps1.setString(3, accpackageid);
					ps1.execute();
					
				}
				
				DbUtil.close(rs);
				DbUtil.close(ps);
				
				
				//作一个搜索:搜索同账套下有没有重复的, 按照 一级标准科目名称/上级/末级科目名称;
				sql = "select  tokenid from c_account where AccPackageID =? and submonth=1 and level1 >1  group by tokenid having count(tokenid)>1 ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, accpackageid);
				rs = ps.executeQuery();
				String tokenString = "";
				while(rs.next()){
					tokenString += "'"+rs.getString(1)+"',";
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
				
				//tokenString 为空，表示没有重复。重复就用科目全路径
				if(!"".equals(tokenString)){
					tokenString = tokenString.substring(0, tokenString.length()-1);
					
					sql = "update c_account a set tokenid =subjectfullname1 where AccPackageID =? and tokenid in ("+tokenString+") ";
					ps = conn.prepareStatement(sql);
					ps.setString(1, accpackageid);
					ps.execute();
					DbUtil.close(ps);
				}
			
			
			//修改c_accountall、c_subjectentry、z_manuaccount(暂不加)
			updateOne(accpackageid);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps1);
			DbUtil.close(ps);
		}
		
	}
	
	/**
	 * 连序性自动对照
	 * @param departID	客户ID
	 * @throws Exception
	 */
	public void auto(String departID)throws Exception {
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		ResultSet rs = null;
		try {
			
			//重算改成1年1年对！
			TokenInstallService tis = new TokenInstallService(conn);
			String [] Accps = tis.getAccPackages(departID, null, null); 
			
			for(int i = 0;i<Accps.length;i++){
				autoOne(Accps[i]);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps1);
			DbUtil.close(ps);
		}
	}
	
	
	/**
	 * 一次只对照一个帐套的一个科目的数据；
	 * @param departID
	 * @param accpackageid
	 * @param subjectid
	 * @throws Exception
	 */
	public void auto(String departID,String subjectfullname1)throws Exception {
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		ResultSet rs = null;
		try {
			ASFuntion CHF = new ASFuntion();
			
			//一级科目:一级标准科目名称
			String sql = "update c_account a set tokenid =subjectfullname2 ,standname=AccName where level1 = 1 and (subjectfullname1 = '"+subjectfullname1+"' or subjectfullname1 like '"+subjectfullname1+"/%') ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "select distinct subjectfullname2 from c_account where submonth=1 and level1=1 and (subjectfullname1 = '"+subjectfullname1+"' or subjectfullname1 like '"+subjectfullname1+"/%') ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				String string1 = rs.getString(1);
				sql = "update c_account a  " +
				" set tokenid = concat(?,'/',a.AccName),standname=AccName " +
				" where  a.subjectfullname2 like '"+string1+"/%' " +
				" and level1 >1 " +
				" and AccPackageID like concat(?,'%')";
				ps1 = conn.prepareStatement(sql);
				ps1.setString(1, string1);
				ps1.setString(2, departID);
				ps1.execute();
				
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
//			System.out.println("AutoTokenService22:"+CHF.getCurrentTime());
			
			//作一个搜索:搜索同账套下有没有重复的, 按照 一级标准科目名称/上级/末级科目名称;
			sql = "select distinct tokenid from (select  tokenid from c_account where AccPackageID like concat(?,'%') and submonth=1 and level1 >1  group by AccPackageID,tokenid having count(tokenid)>1 ) a";
			ps = conn.prepareStatement(sql);
			ps.setString(1, departID);
			rs = ps.executeQuery();
			String tokenString = "";
			while(rs.next()){
				tokenString += "'"+rs.getString(1)+"',";
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			//tokenString 为空，表示没有重复。重复就用科目全路径
			if(!"".equals(tokenString)){
				tokenString = tokenString.substring(0, tokenString.length()-1);
				
				sql = "update c_account a set tokenid =subjectfullname1 where AccPackageID like concat(?,'%') and tokenid in ("+tokenString+") and (subjectfullname1 = '"+subjectfullname1+"' or subjectfullname1 like '"+subjectfullname1+"/%') ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, departID);
				ps.execute();
				DbUtil.close(ps);
			}
			
			//修改c_accountall、c_subjectentry、z_manuaccount(暂不加)
			update(departID,subjectfullname1);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps1);
			DbUtil.close(ps);
		}
	}
	
	
	public void update(String departID,String subjectfullname1)  throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			TokenInstallService tis = new TokenInstallService(conn);
			String [] Accps = tis.getAccPackages(departID, null, null); 
			
			ASFuntion CHF = new ASFuntion();
			
			for(int i = 0;i<Accps.length;i++){
				
				String sql = "update c_accountall a ,c_account b " +
				" set a.tokenid=b.tokenid, a.standname=a.accname " +
				" where b.submonth=1 " +
				" and a.AccPackageID =? " +
				" and (a.subjectfullname1 = '"+subjectfullname1+"' or a.subjectfullname1 like '"+subjectfullname1+"/%') " +
				" and b.AccPackageID =? " +
				" and (b.subjectfullname1 = '"+subjectfullname1+"' or b.subjectfullname1 like '"+subjectfullname1+"/%') " +
				" and a.SubjectID=b.SubjectID ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, Accps[i]);
				ps.setString(2, Accps[i]);
				ps.execute();
				DbUtil.close(ps);
//				System.out.println("AutoTokenService311:"+CHF.getCurrentTime() + "|"+Accps[i] + "|" + sql);
				
				sql = "update c_subjectentry a ,c_account b" +
				" set a.tokenid=b.tokenid, a.standname=a.subjectname1 " +
				" where a.AccPackageID=? " +
				" and (a.subjectfullname1 = '"+subjectfullname1+"' or a.subjectfullname1 like '"+subjectfullname1+"/%') " +
				" and b.AccPackageID =? " +
				" and (b.subjectfullname1 = '"+subjectfullname1+"' or b.subjectfullname1 like '"+subjectfullname1+"/%') " +
				" and b.submonth=1" +
				" and a.SubjectID=b.SubjectID ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, Accps[i]);
				ps.setString(2, Accps[i]);
				ps.execute();
				DbUtil.close(ps);
//				System.out.println("AutoTokenService312:"+CHF.getCurrentTime() + "|"+Accps[i] + "|" + sql);
			}
			
			
			System.out.println("AutoTokenService30:"+CHF.getCurrentTime());
			//修改z_manuaccount(暂不加)
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 
	 * @param accpackageid
	 * @throws Exception
	 */
	public void updateOne(String accpackageid)  throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			ASFuntion CHF = new ASFuntion();
			
			String sql = "update c_accountall a ,c_account b " +
			" set a.tokenid=b.tokenid, a.standname=a.accname " +
			" where b.submonth=1 " +
			" and a.AccPackageID =? " +
			" and b.AccPackageID =? " +
			" and a.SubjectID=b.SubjectID ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, accpackageid);
			ps.setString(2, accpackageid);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "update c_subjectentry a ,c_account b" +
			" set a.tokenid=b.tokenid, a.standname=a.subjectname1 " +
			" where a.AccPackageID=? " +
			" and b.AccPackageID =? " +
			" and b.submonth=1" +
			" and a.SubjectID=b.SubjectID ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, accpackageid);
			ps.setString(2, accpackageid);
			ps.execute();
			DbUtil.close(ps);
			
			//System.out.println("AutoTokenService30:"+CHF.getCurrentTime());
			//修改z_manuaccount(暂不加)
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 修改c_accountall、c_subjectentry、z_manuaccount(暂不加) 的tokenid、standname的值
	 * @param departID
	 * @throws Exception
	 */
	public void update(String departID)  throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			TokenInstallService tis = new TokenInstallService(conn);
			String [] Accps = tis.getAccPackages(departID, null, null); 
			
			for(int i = 0;i<Accps.length;i++){
				
				updateOne( Accps[i]);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 返回对应的Tokenid
	 * 只接收单个subjectid
	 * @param accpackageid
	 * @param subjectid
	 * @return
	 * @throws Exception
	 */
	public String getTokenid(String accpackageid,String subjectid) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String tokenid = "";
		String sql = "";
		try {
			sql = " select distinct tokenid from c_account \n"
				+"  where accpackageid="+accpackageid+" and subjectid='"+subjectid+"' and submonth=1 ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()) {
				tokenid = rs.getString(1);
			}
			return tokenid; 
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 根据传过来的subjectids,返回Tokenids
	 * 返回的Tokenids格式为:'长期股权投资/广州市建筑集团(投资成本)','长期股权投资/广州市建筑集团(损益调整)'
	 * 传过来的subjectids用豆号分隔
	 * 支持单个跟多个subjectid
	 * @param accpackageid
	 * @param subjectids
	 * @return
	 * @throws Exception
	 */
	public String getTokenids(String accpackageid,String subjectids) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String tokenid = "";
		subjectids = subjectids.replaceAll("'", "");
		String subjectid = subjectids.replaceAll(",", "','");
		subjectid ="'"+subjectid+"'";
		accpackageid = accpackageid.replaceAll("'", "");
		accpackageid = accpackageid.replaceAll(",", "','");
		accpackageid = "'"+accpackageid+"'";
		String sql = "";
		try {
			sql = " select distinct tokenid from c_account \n"
				+"  where accpackageid in ("+accpackageid+") and subjectid in ("+subjectid+")  and submonth=1 ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()) {
				tokenid += rs.getString(1)+"','";
			}
			if(tokenid.length()>0) {//如果tokenid为空就返回空
				tokenid = tokenid.substring(0, tokenid.length()-2);
				tokenid = "'"+tokenid;
			} else {
				tokenid = "''";
			}
			return tokenid; 
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	/**
	 * 返回指定subjectid的所有叶子的Tokenid
	 * 只支持单个subjectid
	 * @param accpackageid
	 * @param subjectid
	 * @return
	 * @throws Exception
	 */
	public String getTokenidLeaf(String accpackageid,String subjectid) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String tokenid = "";
		String sql = "";
		try {
			sql = " select distinct tokenid from c_account \n"
				+" where 1=1 \n"
				+" and (subjectfullname1= \n"
				+" ( \n"
				+" select distinct subjectfullname1 from c_account \n"
				+" where subjectid='"+subjectid+"' and accpackageid="+accpackageid+"  and submonth=1\n"
				+" ) or subjectfullname1 like \n"
				+" ( \n"
				+" select distinct concat(subjectfullname1,\"/%\") from c_account \n"
				+" where subjectid='"+subjectid+"' and accpackageid="+accpackageid+"  and submonth=1 \n"
				+" )) ";
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()) {
				tokenid += rs.getString(1)+"','";
			}
			if(tokenid.length()>0) {//如果tokenid为空就返回空
				tokenid = tokenid.substring(0, tokenid.length()-2);
				tokenid = "'"+tokenid;
			} else {
				tokenid = "''";
			}
			return tokenid; 
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	/**
	 * 返回指定subjectids的所有的叶子的Tokenid
	 * 支持单个跟多个subjectid
	 * @param accpackageid
	 * @param subjectids
	 * @return
	 * @throws Exception
	 */
	public String getTokenidLeafs(String accpackageid,String subjectids) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String tokenid = "";
		subjectids = subjectids.replaceAll("'", "");
		String subjectid = subjectids.replaceAll(",", "','");
		subjectid = "'"+subjectid+"'";
		accpackageid = accpackageid.replaceAll("'", "");
		accpackageid = accpackageid.replaceAll(",", "','");
		accpackageid = "'"+accpackageid+"'";
		String sql = "";
		try {
			sql = " select distinct tokenid from c_account a, \n"
				 +" (  \n"
				 +" select distinct subjectfullname1 subjectname from c_account \n" 
				 +" where subjectid in ("+subjectid+") and accpackageid in ("+accpackageid+") \n" 
				 +" ) b \n"
				 +" where isleaf1=1 \n" 
				 +" and (subjectfullname1 =b.subjectname or subjectfullname1 like concat(b.subjectname,\"/%\") \n"
				 +" )  ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()) {
				tokenid += rs.getString(1)+"','";
			}
			if(tokenid.length()>0) {//如果tokenid为空就返回空
				tokenid = tokenid.substring(0, tokenid.length()-2);
				tokenid = "'"+tokenid;
			} else {
				tokenid = "''";
			}
			return tokenid; 
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	/**
	 * 根据传过来的subjectids,返回所有指定subjectids的下级subjectids
	 * subjectids支持多个,用豆号或单引号加豆号如:1001,1002或者'1001','1002'
	 * @param subjectids
	 * @return
	 * @throws Exception
	 */
	public String getSubjectChildren(String subjectids) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		subjectids = subjectids.replaceAll("'", "");
		subjectids = subjectids.replaceAll(",", "','");
		subjectids = "'"+subjectids+"'";
		String subjectChildren = "";
		String sql = " select group_concat(distinct a.subjectid) from c_account a, \n"
					+" (select distinct subjectfullname1 from c_account where subjectid in ("+subjectids+")) b \n"
					+" where a.subjectfullname1 like concat(b.subjectfullname1,'/%') or a.subjectfullname1=b.subjectfullname1";
		try {
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()) {
				subjectChildren += rs.getString(1);
			}
			subjectChildren = subjectChildren.replaceAll(",", "','");
			subjectChildren = "'"+subjectChildren+"'";
			return subjectChildren; 
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 返回指定subjectids下的所有下级结点的Tokenid
	 * @param accpackageid
	 * @param subjectids
	 * @return
	 * @throws Exception
	 */
	public String getSubjectChildrenTokenids(String accpackageid,String subjectids) throws Exception {
		AutoTokenService ats = new AutoTokenService(conn);
		subjectids = ats.getSubjectChildren(subjectids);
		String tokenids = ats.getTokenidLeafs(accpackageid, subjectids);
		return tokenids;
	}
	/**
	 * 指定返回科目全路径下的所有下级结点的Tokenid
	 * subjectfullnames以`~分隔
	 * @param accpackageid
	 * @param subjectfullnames
	 * @return
	 * @throws Exception
	 */
	public String getTokenidLeafids(String accpackageid,String subjectfullnames) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String tokenid = "";
		subjectfullnames = subjectfullnames.replaceAll("'", "\\\'");
		String subjectfullname = subjectfullnames.replaceAll("`~", "','");
		subjectfullname = "'"+subjectfullname+"'";
		accpackageid = accpackageid.replaceAll("'", "");
		accpackageid = accpackageid.replaceAll(",", "','");
		accpackageid = "'"+accpackageid+"'";
		String sql = "";
		try {
			sql = " select distinct tokenid from c_account a, \n"
				 +" (  \n"
				 +" select distinct subjectfullname2 subjectname from c_account \n" 
				 +" where subjectfullname2 in ("+subjectfullname+") and accpackageid in ("+accpackageid+") \n" 
				 +" ) b \n"
				 +" where isleaf1=1 \n" 
				 +" and (subjectfullname2 =b.subjectname or subjectfullname2 like concat(b.subjectname,\"/%\") \n"
				 +" )  ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()) {
				tokenid += rs.getString(1)+"','";
			}
			if(tokenid.length()>0) {//如果tokenid为空就返回空
				tokenid = tokenid.substring(0, tokenid.length()-2);
				tokenid = "'"+tokenid;
			} else {
				tokenid = "''";
			}
			return tokenid; 
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	
	/**
	 * 通过指定的subjectid 得到跨年时所有年份对应的 subjectid
	 * subjectfullnames以`~分隔
	 * @param accpackageid
	 * @param subjectfullnames
	 * @return
	 * @throws Exception
	 */
	public String[] getsubjectIdsBySubjectId(String customerid,int startyear,int endyear,String subjectid) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String tokenid = "";
		String[] subjectIds = null;
		String sql = "";
		try {
			
		
			tokenid = getTokenid(customerid+endyear,subjectid);
			
			sql = " select subjectid from c_account a where tokenid = '"+tokenid+"' and SubYearMonth>='"+startyear+"' and SubYearMonth<='"+endyear+"' and SubMonth = '1' group by SubYearMonth order by SubYearMonth\n";
				
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int i = 0;
			while(rs.next()) {
				
				subjectIds[i] = rs.getString(1);
				i++;
			}
			
			return subjectIds; 
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public static void main(String args[]) throws Exception{
		Connection conn = new DBConnect().getConnect("101076");
		AutoTokenService ats = new AutoTokenService(conn);
		System.out.println("mytest:"+ats.getTokenidLeafids("1010762005","应收股利a/广州工程总承包集团有限公司`~其他应收款a/社会保险自负部分"));
	}
}
