package com.matech.audit.service.autotoken;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class TokenInstallService {

	private Connection conn = null;

	public TokenInstallService(Connection conn) {
		this.conn = conn;
	}
	
	public boolean isTokenID(String CustomerID)throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select * from c_account where submonth=1 and AccPackageID like concat(?,'%') and ifnull(tokenid,'') = ''";
			ps = conn.prepareStatement(sql);
			ps.setString(1, CustomerID);
			rs = ps.executeQuery();
			if(rs.next()){
				return true;
			}else{
				return false;
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
	 * 得到帐套区间内的帐套数
	 * 
	 * @param CustomerID
	 * @param BeginYear
	 * @param EndYear
	 * @return
	 * @throws Exception
	 */
	public int getAccPackageCount(String CustomerID, String BeginYear,
			String EndYear) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			String string = "";
			if(BeginYear != null && EndYear != null && !"".equals(BeginYear) && !"".equals(EndYear)){
				string = "and (AccPackageYear>=? and AccPackageYear<=? )";
			}
			
			String sql = "select count(1) from c_accpackage where CustomerID=? "+string+" order by AccPackageYear";
			ps = conn.prepareStatement(sql);
			ps.setString(1, CustomerID);
			if(BeginYear != null && EndYear != null && !"".equals(BeginYear) && !"".equals(EndYear)){
				ps.setString(2, BeginYear);
				ps.setString(3, EndYear);
			}
			rs = ps.executeQuery();
			rs.next();

			return rs.getInt(1);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 得到帐套区间内的所有帐套编号
	 * 
	 * @param CustomerID
	 * @param BeginYear
	 * @param EndYear
	 * @return
	 * @throws Exception
	 */
	public String[] getAccPackages(String CustomerID, String BeginYear,
			String EndYear) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String string = "";
			if(BeginYear != null && EndYear != null && !"".equals(BeginYear) && !"".equals(EndYear)){
				string = "and (AccPackageYear>=? and AccPackageYear<=? )";
			}
			
			String sql = "select * from c_accpackage where CustomerID=? "+string+" order by AccPackageYear";
			ps = conn.prepareStatement(sql);
			ps.setString(1, CustomerID);
			if(BeginYear != null && EndYear != null && !"".equals(BeginYear) && !"".equals(EndYear)){
				ps.setString(2, BeginYear);
				ps.setString(3, EndYear);
			}
			rs = ps.executeQuery();
			String[] str = new String[getAccPackageCount(CustomerID, BeginYear,
					EndYear)];
			int ii = 0;
			while (rs.next()) {
				str[ii] = new String();
				str[ii] = rs.getString("AccPackageID");
				ii++;
			}

			return str;

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public void CreateTable(String TabName, String CustomerID, String BeginYear,String BeginDate, String EndYear,String EndDate) throws Exception {
		PreparedStatement ps = null;
		try {
			String string = "";
			String sql = "";

			String [] YearMonth = getYearMonth(CustomerID, BeginYear, BeginDate,  EndYear, EndDate);
			
			for (int ii = 0; ii < getAccPackageCount(CustomerID, BeginYear,EndYear); ii++) {
				string += " AccPackageID" + ii + " varchar(10) default '',";
				string += " subjectid" + ii + " varchar(30) default '',";
				string += " subjectfullname" + ii + " varchar(250) default '',";
				string += " standfullname" + ii + " varchar(250) default '',";
				string += " direction" + ii + " int(10) default null,";
				
				string += " Remain" + ii + " decimal(15,2) default '0.00',";
				
				String [] years = YearMonth[ii].split(",");
				for(int j=0; j<years.length; j++){
					if(!"".equals(years[j])){
						string += " monthBalance" + j + ii + " decimal(15,2) default '0.00',";
					}
				}
				
//				string += " Balance" + ii + " decimal(15,2) default '0.00',";
				string += " IsLeaf" + ii + " int(1)   default '0',";
				string += " Level" + ii + " int(1)   default '0',";

			}

			sql = "CREATE TABLE `" + TabName + "` ("
					+ " id int(10) NOT NULL auto_increment," + string
					+ " tokenid varchar(80) default NULL, "
					+ " standname varchar(80) default NULL, "
					+ " tz varchar(10) default '0',  " 
					+ " orderid varchar(30) default '',  " 
					+ " PRIMARY KEY  (id),"
					+ " KEY tokenid (tokenid) "
					+ ") ENGINE=MyISAM DEFAULT CHARSET=gbk";

			org.util.Debug.prtOut("PY TokenInstallService : CreateTable : sql ="+ sql);

			ps = conn.prepareStatement(sql);
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
		
	}
	
	public void CreateTable(String TabName, String CustomerID, String BeginYear,String EndYear) throws Exception {
		PreparedStatement ps = null;
		try {
			String string = "";
			String sql = "";

			for (int ii = 0; ii < getAccPackageCount(CustomerID, BeginYear,EndYear); ii++) {
				string += " AccPackageID" + ii + " varchar(10) default '',";
				string += " subjectid" + ii + " varchar(30) default '',";
				string += " subjectfullname" + ii + " varchar(250) default '',";
				string += " standfullname" + ii + " varchar(250) default '',";
				string += " direction" + ii + " int(10) default null,";
				
				string += " Remain" + ii + " decimal(15,2) default '0.00',";
				
				string += " DebitTotalOcc" + ii + " decimal(15,2) default '0.00',";		//借发生额
				string += " CreditTotalOcc" + ii + " decimal(15,2) default '0.00',";	//贷发生额
				
				string += " Balance" + ii + " decimal(15,2) default '0.00',";
				string += " IsLeaf" + ii + " int(1)   default '0',";
				string += " Level" + ii + " int(1)   default '0',";

			}

			sql = "CREATE TABLE `" + TabName + "` ("
					+ " id int(10) NOT NULL auto_increment," + string
					+ " tokenid varchar(80) default NULL, "
					+ " standname varchar(80) default NULL, "
					+ " tz varchar(10) default '0',  " 
					+ " orderid varchar(30) default '',  " 
					+ " PRIMARY KEY  (id),"
					+ " KEY tokenid (tokenid) " 
					+ ") ENGINE=MyISAM DEFAULT CHARSET=gbk";

			org.util.Debug.prtOut("PY TokenInstallService : CreateTable : sql ="+ sql);

			ps = conn.prepareStatement(sql);
			ps.execute();

			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	/**
	 * 创建临时表。为了显示科目连续性
	 * 
	 * @param CustomerID
	 * @param BeginYear
	 * @param EndYear
	 * @return
	 * @throws Exception
	 */
	public String CreateTable(String CustomerID, String BeginYear,String EndYear) throws Exception {
		String TabName = "tt_" + DELUnid.getCharUnid();
		CreateTable( TabName,  CustomerID,  BeginYear, EndYear);
		return TabName;
	}
	
	public void DataToTable1(String TabName, String CustomerID,String BeginYear,String BeginDate, String EndYear,String EndDate,String strSql) throws Exception {
		PreparedStatement ps = null;
		try {
			ASFuntion CHF=new ASFuntion();
			String sql = "";
			String[] accps = getAccPackages(CustomerID, BeginYear, EndYear);

			if(accps.length == 0) return;
			
			sql = "delete from `" + TabName + "` ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			String [] YearMonth1 = getYearMonth(CustomerID, BeginYear, BeginDate,  EndYear, EndDate) ;
			String [] YearMonth = new String[accps.length];
			for(int i = 0,ii=0; i<YearMonth1.length; i++){
				if(YearMonth1[i].indexOf(accps[ii].substring(6))>-1){
					YearMonth[ii] = new String();
					YearMonth[ii] = YearMonth1[i];
					ii++;
				}
			}
			
			System.out.println("条件:"+strSql);
			for (int i = 0; i < YearMonth.length; i++) {
				String [] years = YearMonth[i].split(","); 
				
				String string = " select AccPackageID,subjectid ,subjectfullname1,subjectfullname2,direction2 as direction,"
						+ " sum(if(concat(SubYearMonth,LPAD(SubMonth,2,'0')) = '"+years[0]+"', direction2 * (DebitRemain+CreditRemain), 0 )) Remain,";
				for(int j=0;j<years.length;j++){
					string += " sum(if(concat(SubYearMonth,LPAD(SubMonth,2,'0')) = '"+years[j]+"', "+strSql+" , 0 )) as monthBalance"+j+",";
					
				}
				string += " IsLeaf1,Level1,"
					+ " tokenid,standname,tz"
					+ " from c_account"
					+ " where substring(AccPackageID,1,6) =? " 
					+ " and concat(SubYearMonth,LPAD(SubMonth,2,'0')) in ('" + CHF.replaceStr(YearMonth[i].substring(0,YearMonth[i].length()-1), ",", "','") + "') " 
					+ " group by subjectid";
				
				sql = "update `" + TabName + "` a , (" + string + ") b "
					+ " set a.AccPackageID" + i + " = b.AccPackageID ,"
					+ " a.subjectid" + i + " = b.subjectid ,"
					+ " a.subjectfullname" + i + " = b.subjectfullname1 ,"
					+ " a.standfullname" + i + " = b.subjectfullname2 ,"
					+ " a.direction" + i + " = b.direction ,"
					+ " a.Remain" + i + " = b.Remain ,";
				for(int j=0;j<years.length;j++){
					sql += " a.monthBalance" +j + i + " = b.monthBalance" + j + " ," ;
				}
				sql += " a.IsLeaf" + i + " = b.IsLeaf1 ,"
					+ " a.Level" + i + " = b.Level1 ,"
					+ " a.tokenid = b.tokenid ,"
					+ " a.standname = b.standname "
					+ " where  a.tokenid = b.tokenid ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, CustomerID);
				ps.execute();

				sql = "insert into `" + TabName + "` (" 
					+ " AccPackageID" + i+ "," 
					+ " subjectid" + i + "," 
					+ " subjectfullname" + i + "," 
					+ " standfullname" + i + "," 
					+ " direction" + i + "," 
					+ " Remain" + i + "," ;
					for(int j=0;j<years.length;j++){
						sql += " monthBalance" +j + i + ",";
					}
					
				sql += " IsLeaf" + i + ","
					+ " Level" + i + "," 
					+ " tokenid," 
					+ " standname,"
					+ " tz" 
					+ ") select b.* from `" + TabName + "` a right join (" 
					+ string 
					+ " ) b on a.tokenid = b.tokenid "
					+ " where a.tokenid is null ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, CustomerID);
				ps.execute();
				
			}

			for (int i = 0; i < accps.length; i++) {
				String sql1 = " and orderid ='' ";
				if(i == 0){
					sql1 = "";
				}
				sql = "update " + TabName + " set orderid=subjectid" + (accps.length - i - 1) + " where 1=1 " + sql1;
				ps = conn.prepareStatement(sql);
				ps.execute();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	
//	多年余额表查询的备份
	public void DataToTable1(String TabName, String CustomerID,String BeginYear,String BeginDate, String EndYear,String EndDate) throws Exception {
		PreparedStatement ps = null;
		try {
			ASFuntion CHF=new ASFuntion();
			String sql = "";
			String[] accps = getAccPackages(CustomerID, BeginYear, EndYear);

			if(accps.length == 0) return;
			
			sql = "delete from `" + TabName + "` ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			String [] YearMonth1 = getYearMonth(CustomerID, BeginYear, BeginDate,  EndYear, EndDate) ;
			String [] YearMonth = new String[accps.length];
			for(int i = 0,ii=0; i<YearMonth1.length; i++){
				if(YearMonth1[i].indexOf(accps[ii].substring(6))>-1){
					YearMonth[ii] = new String();
					YearMonth[ii] = YearMonth1[i];
					ii++;
				}
			}
			
			for (int i = 0; i < YearMonth.length; i++) {
				String [] years = YearMonth[i].split(","); 
				
				String string = " select AccPackageID,subjectid ,subjectfullname1,subjectfullname2,direction2 as direction,"
						+ " sum(if(concat(SubYearMonth,LPAD(SubMonth,2,'0')) = '"+years[0]+"', direction2 * (DebitRemain+CreditRemain), 0 )) Remain,";
				for(int j=0;j<years.length;j++){
					string += " sum(if(concat(SubYearMonth,LPAD(SubMonth,2,'0')) = '"+years[j]+"', direction2 * Balance , 0 )) as monthBalance"+j+",";
					
				}
				string += " IsLeaf1,Level1,"
					+ " tokenid,standname,tz"
					+ " from c_account"
					+ " where substring(AccPackageID,1,6) =? " 
					+ " and concat(SubYearMonth,LPAD(SubMonth,2,'0')) in ('" + CHF.replaceStr(YearMonth[i].substring(0,YearMonth[i].length()-1), ",", "','") + "') " 
					+ " group by subjectid";
				
				sql = "update `" + TabName + "` a , (" + string + ") b "
					+ " set a.AccPackageID" + i + " = b.AccPackageID ,"
					+ " a.subjectid" + i + " = b.subjectid ,"
					+ " a.subjectfullname" + i + " = b.subjectfullname1 ,"
					+ " a.standfullname" + i + " = b.subjectfullname2 ,"
					+ " a.direction" + i + " = b.direction ,"
					+ " a.Remain" + i + " = b.Remain ,";
				for(int j=0;j<years.length;j++){
					sql += " a.monthBalance" +j + i + " = b.monthBalance" + j + " ," ;
				}
				sql += " a.IsLeaf" + i + " = b.IsLeaf1 ,"
					+ " a.Level" + i + " = b.Level1 ,"
					+ " a.tokenid = b.tokenid ,"
					+ " a.standname = b.standname "
					+ " where  a.tokenid = b.tokenid ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, CustomerID);
				ps.execute();

				sql = "insert into `" + TabName + "` (" 
					+ " AccPackageID" + i+ "," 
					+ " subjectid" + i + "," 
					+ " subjectfullname" + i + "," 
					+ " standfullname" + i + "," 
					+ " direction" + i + "," 
					+ " Remain" + i + "," ;
					for(int j=0;j<years.length;j++){
						sql += " monthBalance" +j + i + ",";
					}
					
				sql += " IsLeaf" + i + ","
					+ " Level" + i + "," 
					+ " tokenid," 
					+ " standname,"
					+ " tz" 
					+ ") select b.* from `" + TabName + "` a right join (" 
					+ string 
					+ " ) b on a.tokenid = b.tokenid "
					+ " where a.tokenid is null ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, CustomerID);
				ps.execute();
				
			}

			for (int i = 0; i < accps.length; i++) {
				String sql1 = " and orderid ='' ";
				if(i == 0){
					sql1 = "";
				}
				sql = "update " + TabName + " set orderid=subjectid" + (accps.length - i - 1) + " where 1=1 " + sql1;
				ps = conn.prepareStatement(sql);
				ps.execute();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	
	public void DataToTable(String TabName, String CustomerID,String BeginYear,String BeginDate, String EndYear,String EndDate) throws Exception {
		PreparedStatement ps = null;
		try {
			ASFuntion CHF=new ASFuntion();
			String sql = "";
			String[] accps = getAccPackages(CustomerID, BeginYear, EndYear);

			if(accps.length == 0) return;
			
			sql = "delete from `" + TabName + "` ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);

//			int begin = Integer.parseInt(BeginYear) * 12 + Integer.parseInt(BeginDate);
//			int end = Integer.parseInt(EndYear) * 12 + Integer.parseInt(EndDate);
			
			String [] YearMonth1 = getYearMonth(CustomerID, BeginYear, BeginDate,  EndYear, EndDate) ;
			String [] YearMonth = new String[accps.length];
			for(int i = 0,ii=0; i<YearMonth1.length; i++){
				if(YearMonth1[i].indexOf(accps[ii].substring(6))>-1){
					YearMonth[ii] = new String();
					YearMonth[ii] = YearMonth1[i];
					ii++;
				}
			}
			
			for (int i = 0; i < YearMonth.length; i++) {
				String [] years = YearMonth[i].split(","); 
				
				String string = " select AccPackageID,subjectid ,subjectfullname1,subjectfullname2,direction2 as direction,"
						+ " sum(if(concat(SubYearMonth,LPAD(SubMonth,2,'0')) = '"+years[0]+"', direction2 * (DebitRemain+CreditRemain), 0 )) Remain,"
						+ " sum(DebitOcc) DebitTotalOcc,"
						+ " sum(CreditOcc) CreditTotalOcc,"
						+ " sum(if(concat(SubYearMonth,LPAD(SubMonth,2,'0')) = '"+years[years.length-1]+"', direction2 * Balance , 0 )) Balance,"
						+ " IsLeaf1,Level1,"
						+ " tokenid,standname,tz"
						+ " from c_account"
						+ " where substring(AccPackageID,1,6) =? " 
						+ " and concat(SubYearMonth,LPAD(SubMonth,2,'0')) in ('" + CHF.replaceStr(YearMonth[i].substring(0,YearMonth[i].length()-1), ",", "','") + "') " 
						+ " group by subjectid";

				sql = "update `" + TabName + "` a , (" + string + ") b "
						+ " set a.AccPackageID" + i + " = b.AccPackageID ,"
						+ " a.subjectid" + i + " = b.subjectid ,"
						+ " a.subjectfullname" + i + " = b.subjectfullname1 ,"
						+ " a.standfullname" + i + " = b.subjectfullname2 ,"
						+ " a.direction" + i + " = b.direction ,"
						+ " a.Remain" + i + " = b.Remain ,"
						
						+ " a.DebitTotalOcc" + i + " = b.DebitTotalOcc ," 
						+ " a.CreditTotalOcc" + i + " = b.CreditTotalOcc ,"
						
						+ " a.Balance" + i + " = b.Balance ," 
						+ " a.IsLeaf" + i + " = b.IsLeaf1 ,"
						+ " a.Level" + i + " = b.Level1 ,"
						+ " a.tokenid = b.tokenid ,"
						+ " a.standname = b.standname "
						+ " where  a.tokenid = b.tokenid ";
				System.out.println(sql);
				ps = conn.prepareStatement(sql);
				ps.setString(1, CustomerID);
				ps.execute();

				sql = "insert into `" + TabName + "` (" 
					+ " AccPackageID" + i+ "," 
					+ " subjectid" + i + "," 
					+ " subjectfullname" + i + "," 
					+ " standfullname" + i + "," 
					+ " direction" + i + "," 
					+ " Remain" + i + "," 
					+ " DebitTotalOcc" + i + "," 
					+ " CreditTotalOcc" + i + "," 
					+ " Balance" + i + "," 
					+ " IsLeaf" + i + ","
					+ " Level" + i + "," 
					+ " tokenid," 
					+ " standname,"
					+ " tz" 
					+ ") select b.* from `" + TabName + "` a right join (" 
					+ string 
					+ " ) b on a.tokenid = b.tokenid "
					+ " where a.tokenid is null ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, CustomerID);
				ps.execute();
				
			}

			for (int i = 0; i < accps.length; i++) {
				String sql1 = " and orderid ='' ";
				if(i == 0){
					sql1 = "";
				}
				sql = "update " + TabName + " set orderid=subjectid" + (accps.length - i - 1) + " where 1=1 " + sql1;
				ps = conn.prepareStatement(sql);
				ps.execute();
			} 
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 为临时表插入值
	 * 
	 * @param TabName
	 * @param CustomerID
	 * @param BeginYear
	 * @param EndYear
	 * @throws Exception
	 */
	public void DataToTable(String TabName, String CustomerID,
			String BeginYear, String EndYear) throws Exception {
		DataToTable( TabName,  CustomerID, BeginYear, "01",  EndYear, "12");
	}

	/**
	 * 得到所有一级的tokenid
	 * @param TabName
	 * @param CustomerID
	 * @param BeginYear
	 * @param EndYear
	 * @return
	 * @throws Exception
	 */
	public List getOneSubject(String TabName, String CustomerID,
			String BeginYear, String EndYear) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			int iCount = getAccPackageCount(CustomerID, BeginYear, EndYear);

			List list = new ArrayList();

			String[] str = new String[iCount];
			String string = "";
			for (int i = 0; i < iCount; i++) {
				string += " level" + i + " =1 or";
			}
			if ("".equals(string)) {
				return null;
			}
			String sql = "select tokenid from `" + TabName + "` where ("
					+ string.substring(0, string.length() - 2) + ") ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int ii = 0;
			while (rs.next()) {
				list.add(rs.getString(1));
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 得到指定科目的一级tokenid
	 * @param TabName
	 * @param CustomerID
	 * @param BeginYear
	 * @param EndYear
	 * @param SubjectID
	 * @return
	 * @throws Exception
	 */
	public List getOneSubject(String TabName, String CustomerID,
			String BeginYear, String EndYear, String SubjectID)
			throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			int iCount = getAccPackageCount(CustomerID, BeginYear, EndYear);

			List list = new ArrayList();

			String[] str = new String[iCount];
			String string = "";
			for (int i = 0; i < iCount; i++) {
				string += " level" + i + " =1 or";
			}
			if ("".equals(string)) {
				return null;
			}
			String sql = "select tokenid from `" + TabName + "` where ("
					+ string.substring(0, string.length() - 2) + ") ";

			if (!"".equals(SubjectID)) {
				String tokenid = getLevelSubject(TabName, CustomerID, EndYear,SubjectID, String.valueOf(iCount - 1));
				sql += " and tokenid = '" + tokenid + "' ";
			}

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int ii = 0;
			while (rs.next()) {
				list.add(rs.getString(1));
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 得到科目的tokenid
	 * @param TabName
	 * @param CustomerID
	 * @param EndYear
	 * @param SubjectID
	 * @param AccpOpt
	 * @return
	 * @throws Exception
	 */
	public String getLevelSubject(String TabName, String CustomerID,
			String EndYear, String SubjectID, String AccpOpt) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select tokenid from `" + TabName
					+ "` where AccPackageID" + AccpOpt + "=" + CustomerID
					+ EndYear + " and subjectid" + AccpOpt + "='" + SubjectID
					+ "' ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			}

			return "";
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 检查科目连续性的tokenid是否重复
	 * @param tokenid
	 * @param AccPackageID
	 * @param Subjects
	 * @return
	 * @throws Exception
	 */
	public int checkToken( String tokenid,String[] AccPackageID, String[] Subjects)throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			for (int i = 0; i < AccPackageID.length; i++) {
				String string = AccPackageID[i];
				String sql = "select * from c_account where AccPackageID=? and tokenid=? and submonth=1 ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, string);
				ps.setString(2, tokenid);
				rs = ps.executeQuery();
				if(rs.next()){
					String sid = rs.getString("subjectid");
					if(!Subjects[i].equals(sid)){
						return Integer.parseInt(rs.getString("AccPackageID").substring(6));
					}
				}
			}
			
			
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 保存科目连续性
	 * @param TabName
	 * @param CustomerID
	 * @param BeginYear
	 * @param EndYear
	 * @param tokenid
	 * @param standname
	 * @param AccPackageID
	 * @param Subjects
	 * @throws Exception
	 */
	public void saveToken(String TabName, String CustomerID, String BeginYear,
			String EndYear, String tokenid, String standname,
			String[] AccPackageID, String[] Subjects) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

//			new AutoTokenService(conn).auto(CustomerID);

			String sql = "update c_account a set tokenid =? ,standname=? ,tz=1 where AccPackageID =? and subjectid = ?";
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < AccPackageID.length; i++) {
				ps.setString(1, tokenid);
				ps.setString(2, standname);
				ps.setString(3, AccPackageID[i]);
				ps.setString(4, Subjects[i]);
				ps.addBatch();
			}
			ps.executeBatch();
			DbUtil.close(ps);

			new AutoTokenService(conn).update(CustomerID);

			// sql = "update c_accountall a set tokenid =? ,standname=? where
			// AccPackageID =? and subjectid = ?";
			// ps = conn.prepareStatement(sql);
			// for(int i=0;i<AccPackageID.length;i++){
			// ps.setString(1, tokenid);
			// ps.setString(2, standname);
			// ps.setString(3, AccPackageID[i]);
			// ps.setString(4, Subjects[i]);
			// ps.addBatch();
			// }
			// ps.executeBatch();
			// DbUtil.close(ps);
			//			
			// sql = "update c_subjectentry a set tokenid =? ,standname=? where
			// AccPackageID =? and subjectid = ?";
			// ps = conn.prepareStatement(sql);
			// for(int i=0;i<AccPackageID.length;i++){
			// ps.setString(1, tokenid);
			// ps.setString(2, standname);
			// ps.setString(3, AccPackageID[i]);
			// ps.setString(4, Subjects[i]);
			// ps.addBatch();
			// }
			// ps.executeBatch();
			// DbUtil.close(ps);

			DataToTable(TabName, CustomerID, BeginYear, EndYear);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 解除科目连续性
	 * @param TabName
	 * @param CustomerID
	 * @param BeginYear
	 * @param EndYear
	 * @param tokenid
	 * @throws Exception
	 */
	public void delToken(String TabName, String CustomerID, String BeginYear,String EndYear, String tokenid)throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			token( CustomerID, tokenid);
			new AutoTokenService(conn).update(CustomerID);
			DataToTable(TabName, CustomerID, BeginYear, EndYear);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 连序性自动对照
	 * @param departID	客户ID
	 * @throws Exception
	 */
	public void token(String CustomerID,String tokenid)throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			//一级科目:一级标准科目名称
			String sql = "update c_account a set tokenid =subjectfullname2 ,standname=AccName where level1 = 1 and  AccPackageID like concat(?,'%') and (tokenid=? or tokenid like concat(?,'/%'))";
			ps = conn.prepareStatement(sql);
			ps.setString(1, CustomerID);
			ps.setString(2, tokenid);
			ps.setString(3, tokenid);
			ps.execute();
			DbUtil.close(ps);
			
			//二级及以上科目:一级标准科目名称/末级科目名称
			sql = "update c_account a , " +
			" (select distinct subjectfullname2 from c_account where submonth=1 and level1=1 ) b " +
			" set tokenid = concat(b.subjectfullname2,'/',a.AccName),standname=AccName " +
			" where (a.subjectfullname2 = b.subjectfullname2 or a.subjectfullname2 like concat(b.subjectfullname2,'/%')) " +
			" and level1 >1 " +
			" and AccPackageID like concat(?,'%')" +
			" and (tokenid=? or tokenid like concat(?,'/%')) ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, CustomerID);
			ps.setString(2, tokenid);
			ps.setString(3, tokenid);
			ps.execute();
			DbUtil.close(ps);
			
			//作一个搜索:搜索同账套下有没有重复的, 按照 一级标准科目名称/上级/末级科目名称;
			sql = "select distinct tokenid from c_account where AccPackageID like concat(?,'%') and submonth=1 and level1 >1 and (tokenid=? or tokenid like concat(?,'/%'))  group by AccPackageID,tokenid having count(tokenid)>1 ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, CustomerID);
			ps.setString(2, tokenid);
			ps.setString(3, tokenid);
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
				
				sql = "update c_account a set tokenid =subjectfullname1 where AccPackageID like concat(?,'%') and tokenid in ("+tokenString+") and (tokenid=? or tokenid like concat(?,'/%'))";
				ps = conn.prepareStatement(sql);
				ps.setString(1, CustomerID);
				ps.setString(2, tokenid);
				ps.setString(3, tokenid);
				ps.execute();
				DbUtil.close(ps);
			}
			
			//修改c_accountall、c_subjectentry、z_manuaccount(暂不加)
//			update(departID);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public  String [] getYearMonth(String T1,String BeginYear,String BeginDate, String EndYear,String EndDate )throws Exception {
		String [] AccPackages = getAccPackages(T1, BeginYear, EndYear);
		
		if(AccPackages.length == 0) return null;
		
		String [] result = new String [AccPackages.length];
		int jj = Integer.parseInt(BeginDate);
		
		EndYear = AccPackages[AccPackages.length-1].substring(6);
		for(int i = 0;i<AccPackages.length;i++ ){
			BeginYear = AccPackages[i].substring(6);
			result[i] = new String();
			for(;(jj<13 && !(BeginYear.equals(EndYear))) || (BeginYear.equals(EndYear) && jj<=Integer.parseInt(EndDate)) ;jj++){
				result[i] += BeginYear + (String.valueOf(jj).length()<2 ?"0"+String.valueOf(jj):String.valueOf(jj)) + ",";
			}
//			BeginYear = String.valueOf(Integer.parseInt(BeginYear) + 1);
			jj = 1;
		}
		
		return result;
	}
	
	public boolean ExistsTable(String TabName) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean bool = false;
		try {	
			String sql = "show TABLES  like '" + TabName + "'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				bool = true;
			}
			return bool;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}
	
	public void DelTempTable(String TabName) throws Exception {		
		PreparedStatement ps = null;
		try {			
			// DROP TABLE IF EXISTS `tt`;
			String sql = "DROP TABLE IF EXISTS `" + TabName + "`";
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);	
		}
	}
}
