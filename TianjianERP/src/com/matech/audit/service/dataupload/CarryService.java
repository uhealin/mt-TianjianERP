package com.matech.audit.service.dataupload;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.subjectType.SubjectTypeService;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class CarryService {
	
	private Connection conn = null;
	
	private int intOpt = 1; 
	
	public CarryService(Connection conn) {
		this.conn = conn;
	}
	
	//损益类科目 
	public String getCarrySubject(String AccPackageID) throws Exception {
		Statement st = null;
		ResultSet rs = null;
		String sql = "";
		try {
			
			st = conn.createStatement();
			
			//获得本年利润或利润分配在这套帐里面的叶子科目编号
			sql="select distinct subjectid from c_account \n"
				+"		where accpackageid="+AccPackageID+" \n"
				+"		and isleaf1=1  \n"
				+"		and submonth=1 \n"
				+"		and substr(subjectfullname2,1, if(locate('/',subjectfullname2) = 0, \n" 
				+"			length(subjectfullname2), locate('/',subjectfullname2) -1 ) \n" 
				+"		) in ('本年利润','利润分配') ";
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			String subjectids="";
			while(rs.next()) {
				subjectids+=",'" + rs.getString(1) +"'";
			}
			rs.close();
			org.util.Debug.prtOut("autosetEntryCarrydownProperty:subjectids:"+subjectids);
			
			String strWhere=" subjectid like '5%' ";
			if (new SubjectTypeService(conn).autoJudgeVocation(AccPackageID)==59){
				strWhere=" subjectid like '6%' ";
			}
			
			/**
			 *	根据标准科目求 损益类科目
			 */
			String strWhere1 = "";
			sql = "select group_concat(distinct \"'\",a.subjectid,\"'\")  from ( " +
				"	select * from c_account a where accpackageid = "+AccPackageID+" and SubMonth =1" +
				") a ,(" +
				"	select b.* " +
				"	from k_standsubject b ," +
				"	(select vocationid from k_customer where departid = "+AccPackageID.substring(0, 6)+" ) c, " +
				"	(select industryid from k_industry where industryname like '%新%') d " +
				"	where  b.subjectid like if(c.vocationid=d.industryid,'6%','5%')  " +
				"	and level0 = 1 " +
				"	and b.vocationid = c.vocationid " +
				"	and c.vocationid = d.industryid" +
				") b " +
				"where 1=1 " +
				"and (a.subjectfullname2 = b.subjectfullname or a.subjectfullname2 like concat(b.subjectfullname,'/%'))";
			rs = st.executeQuery(sql);
			while(rs.next()) {
				strWhere1 = " subjectid in ( "+rs.getString(1)+") ";
			}
			rs.close();
			
			strWhere = strWhere + " or " + strWhere1;
			
			return strWhere;
		} catch (Exception e) {
			System.out.println("createKmJzhz 出错的SQL:" + sql);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
		}
	}
	
	/**
	 * 建表 c_carryaccount
	 */
	public void createKmJzhz() throws Exception {
		Statement st = null;
		String sql = "";
		try {
			st = conn.createStatement();
			
			sql = "CREATE TABLE c_carryaccount ( " +
			"	AccPackageID int(14) NOT NULL default '0', " +       
			"	SubjectID varchar(50) NOT NULL default '',  " +            
			"	AccName varchar(80) NOT NULL default '',    " +            
			"	SubYearMonth int(10) default NULL,         " +             
			"	SubMonth int(10) NOT NULL default '0',      " +
			"	DataName varchar(30) default '0',   " +
                                               
			"	DebitOcc decimal(30,4) NOT NULL default '0.00', " +        
			"	CreditOcc decimal(30,4) NOT NULL default '0.00',    " + 
			"	carryDebitOcc decimal(30,4) default '0.0000',     " +
			"	carryCreditOcc decimal(30,4) default '0.0000',     " +
			"	carryOcc decimal(30,4) default '0.0000',      " +

			"	DebitOccF decimal(30,4) NOT NULL default '0.00',  " +       
			"	CreditOccF decimal(30,4) NOT NULL default '0.00',     " +
			"	carryDebitOccF decimal(30,4) default '0.0000',     " +
			"	carryCreditOccF decimal(30,4) default '0.0000',     " +
			"	carryOccF decimal(30,4) default '0.0000',   " +
                              
			"	AccSign int(4) NOT NULL default '0',   " +                 
			"	direction int(2) default '0',              " +             
			"	IsLeaf1 int(1) NOT NULL default '0',           " +         
			"	Level1 int(1) NOT NULL default '0',                " +     
			"	SubjectFullName1 varchar(250) default NULL,            " + 
			"	subjectfullname2 varchar(250) NOT NULL default '',      " +
			"	direction2 int(11) NOT NULL default '0',                " +
			"	tokenid varchar(300) default NULL,                      " +
			"	standname varchar(80) default NULL,                     " +
			"	tz varchar(10) default '0',    " +

			"	PRIMARY KEY  (AccPackageID,SubjectID,SubMonth,DataName), " +   
			"	KEY subjectid (SubjectID),                            " +
			"	KEY year (SubYearMonth),                              " +
			"	KEY month (SubMonth),                                 " +
			"	KEY accName (AccName),                                " +
			"	KEY direction (direction),                            " +
			"	KEY subjectfullname2 (subjectfullname2),              " +
			"	KEY tokenid (tokenid),                                " +
			"	KEY SubjectFullName1 (SubjectFullName1)               " +

			"	) ENGINE=MyISAM DEFAULT CHARSET=gbk ";
			
			st.execute(sql);
		} catch (Exception e) {
			System.out.println("c_carryaccount 表已存在");
		} finally {
			DbUtil.close(st);
		}
	}
	
	/**
	 * 建表 c_carryassitemacc
	 */
	public void createXmJzhz() throws Exception {
		Statement st = null;
		String sql = "";
		try {
			st = conn.createStatement();
			
			sql = "CREATE TABLE c_carryassitemacc ( " +
			"	AccPackageID int(14) NOT NULL default '0',   " +       
			"	AccID varchar(50) NOT NULL default '',  " +
			"	AssItemID varchar(100) character set gbk collate gbk_bin NOT NULL,  " +            
			"	AssItemName varchar(100) default NULL,        " +            
			"	SubYearMonth int(10) default NULL,         " +             
			"	SubMonth int(10) NOT NULL default '0',      " +
			"	DataName varchar(30) default '0',   " +
                                               
			"	DebitOcc decimal(30,4) NOT NULL default '0.00', " +        
			"	CreditOcc decimal(30,4) NOT NULL default '0.00',    " + 
			"	carryDebitOcc decimal(30,4) default '0.0000',     " +
			"	carryCreditOcc decimal(30,4) default '0.0000',     " +
			"	carryOcc decimal(30,4) default '0.0000',      " +

			"	DebitOccF decimal(30,4) NOT NULL default '0.00',  " +       
			"	CreditOccF decimal(30,4) NOT NULL default '0.00',     " +
			"	carryDebitOccF decimal(30,4) default '0.0000',     " +
			"	carryCreditOccF decimal(30,4) default '0.0000',     " +
			"	carryOccF decimal(30,4) default '0.0000',   " +
                              
			"	AccSign int(4) NOT NULL default '0',   " +                 
			"	direction int(2) default '0',              " +             
			"	IsLeaf1 int(1) NOT NULL default '0',           " +         
			"	Level1 int(1) NOT NULL default '0',                " +     
			"	AssTotalName1 varchar(1000) default NULL,             " + 
			"	direction2 int(11) NOT NULL default '0',                " +

			"	PRIMARY KEY  (AccPackageID,AccID,AssItemID,DataName,SubMonth),    " +   
			"	KEY AccID (AccID),  " +
			"	KEY AssItemID (AssItemID),      " +
			"	KEY year (SubYearMonth),                              " +
			"	KEY month (SubMonth),                                 " +
			"	KEY direction (direction),                            " +
			"	KEY AssTotalName1 (AssTotalName1)                 " +

			"	) ENGINE=MyISAM DEFAULT CHARSET=gbk ";
			
			st.execute(sql);
		} catch (Exception e) {
			System.out.println("c_carryassitemacc 表已存在");
		} finally {
			DbUtil.close(st);
		}
	}
	
	/**
	 * 汇总科目结转数
	 */
	public void createKmJzhz(String AccPackageID) throws Exception {
		Statement st = null;
		String sql = "";
		try {
			ASFuntion CHF = new ASFuntion();
			st = conn.createStatement();
			
			createKmJzhz();
			
			String strWhere = getCarrySubject( AccPackageID);
			System.out.println("10:"+CHF.getCurrentTime());
			
			sql = "delete from c_carryaccount where accpackageid = '" + AccPackageID + "' ";
			st.execute(sql);
			
			/**
			 * 本位币的结转数
			 */
			sql = "select a.*, \n" +
			"	("+intOpt+") * (ifnull( a.occ,0) - ifnull(b.occ,0))  as carryOcc, \n" +
			"	ifnull(a.debitocc,0)  - ifnull(occ1,0)  as carryDebitOcc, \n" +
			"	ifnull(a.creditocc,0)  - ifnull(occ2,0)  as carryCreditOcc \n" +
			"	from ( \n" +

			"		select *,a.debitocc-a.creditocc as occ \n" +
			"		from c_account a    \n" +
			"		where 1=1 \n" +
			"		and accpackageid = '" + AccPackageID + "' \n" +
			"		and (" + strWhere + ")						 \n" +	
			"		and a.dataname = 0 \n" +
			"		and a.isleaf1 = 1  \n" +

			"	) a left join (  \n" +

			"		select subjectid,subyear,submonth, \n" +
			"		sum(dirction * occurvalue) as occ, \n" +
			"		sum(if(dirction = 1,occurvalue,0)) occ1, \n" +
			"		sum(if(dirction = -1,occurvalue,0)) occ2 from ( \n" +
			"			select  distinct a.* ,substring(a.VchDate,1,4) as subyear,substring(a.VchDate,6,2) as submonth \n" +
			"			from c_subjectentry a   \n" +
			"			where 1=1   \n" +
			"			and accpackageid = '" + AccPackageID + "' \n" +
			"			and (" + strWhere + ") \n" +
			"			and a.property like '%2%'    \n" +
			"		) a where 1=1 \n" +
			"		group by subjectid,subyear,submonth \n" +

			"	) b on a.subjectid = b.subjectid and a.subyearmonth = b.subyear and a.submonth = b.submonth \n";
			
//			System.out.println("本位币SQL:"+sql);
			sql = "insert into c_carryaccount ( \n" +
			" AccPackageID, SubjectID, AccName, SubYearMonth, SubMonth, DataName, \n" +
			" AccSign, direction, IsLeaf1, Level1, SubjectFullName1, subjectfullname2, direction2, tokenid, standname, tz, \n" +
			" DebitOcc, CreditOcc, carryDebitOcc, carryCreditOcc, carryOcc  \n" +
			")  \n" +
			"select  \n" +
			" AccPackageID, SubjectID, AccName, SubYearMonth, SubMonth, DataName, \n" +
			" AccSign, direction, IsLeaf1, Level1, SubjectFullName1, subjectfullname2, direction2, tokenid, standname, tz, \n" +
			" DebitOcc, CreditOcc, carryDebitOcc, carryCreditOcc, carryOcc  \n" +
			"from ( \n" +
			sql + 
			") a ";
			st.execute(sql);
			System.out.println("11:"+CHF.getCurrentTime());
			
			sql = "update c_carryaccount set " +
			"DebitOccF = DebitOcc, " +
			"CreditOccF = CreditOcc, " +
			"carryDebitOccF = carryDebitOcc, " +
			"carryCreditOccF = carryCreditOcc, " +
			"carryOccF = carryOcc " +
			"where accpackageid = '" + AccPackageID + "' ";
			st.execute(sql);
			System.out.println("12:"+CHF.getCurrentTime());
			
			/**
			 * 外币的结转数
			 */
			sql = "select a.*, \n" +
			"	("+intOpt+") * (ifnull( a.occ,0) - ifnull(b.occ,0))  as carryOcc, \n" +
			"	ifnull(a.debitocc,0)  - ifnull(occ1,0)  as carryDebitOcc, \n" +
			"	ifnull(a.creditocc,0)  - ifnull(occ2,0)  as carryCreditOcc, \n" +
			"	("+intOpt+") * (ifnull( a.occF,0) - ifnull(b.occF,0))  as carryOccF, \n" +
			"	ifnull(a.debitoccF,0)  - ifnull(occ1F,0)  as carryDebitOccF, \n" +
			"	ifnull(a.creditoccF,0)  - ifnull(occ2F,0)  as carryCreditOccF \n" +
			"	from ( \n" +

			"		select *,a.debitocc-a.creditocc as occ,a.debitoccF-a.creditoccF as occF \n" +
			"		from c_accountall a    \n" +
			"		where 1=1 \n" +
			"		and accpackageid = '" + AccPackageID + "' \n" +
			"		and (" + strWhere + ")						 \n" +	
			"		and accsign = 1	 \n" +
			"		and a.isleaf1 = 1  \n" +

			"	) a left join (  \n" +

			"		select subjectid,currency,subyear,submonth, \n" +
			"		sum(dirction * currvalue) as occ, \n" +
			"		sum(if(dirction = 1,currvalue,0)) occ1, \n" +
			"		sum(if(dirction = -1,currvalue,0)) occ2, \n" +
			"		sum(dirction * occurvalue) as occF, \n" +
			"		sum(if(dirction = 1,occurvalue,0)) occ1F, \n" +
			"		sum(if(dirction = -1,occurvalue,0)) occ2F \n" +
			"		from ( \n" +
			"			select  distinct a.* ,substring(a.VchDate,1,4) as subyear,substring(a.VchDate,6,2) as submonth \n" +
			"			from c_subjectentry a   \n" +
			"			where 1=1   \n" +
			"			and accpackageid = '" + AccPackageID + "' \n" +
			"			and (" + strWhere + ") \n" +
			"			and a.property like '%2%'    \n" +
			"			and currency <> '' " +
			"		) a where 1=1 \n" +
			"		group by subjectid,currency,subyear,submonth \n" +

			"	) b on a.subjectid = b.subjectid and a.dataName = currency and a.subyearmonth = b.subyear and a.submonth = b.submonth \n";
			
//			System.out.println("外币SQL:"+sql);
			sql = "insert into c_carryaccount ( \n" +
			" AccPackageID, SubjectID, AccName, SubYearMonth, SubMonth, DataName, \n" +
			" AccSign, direction, IsLeaf1, Level1, SubjectFullName1, subjectfullname2, direction2, tokenid, standname,  \n" +
			" DebitOcc, CreditOcc, carryDebitOcc, carryCreditOcc, carryOcc,  \n" +
			" DebitOccF, CreditOccF, carryDebitOccF, carryCreditOccF, carryOccF  \n" +
			")  \n" +
			"select  \n" +
			" AccPackageID, SubjectID, AccName, SubYearMonth, SubMonth, DataName, \n" +
			" AccSign, direction, IsLeaf1, Level1, SubjectFullName1, subjectfullname2, direction2, tokenid, standname,  \n" +
			" DebitOcc, CreditOcc, carryDebitOcc, carryCreditOcc, carryOcc,  \n" +
			" DebitOccF, CreditOccF, carryDebitOccF, carryCreditOccF, carryOccF  \n" +
			"from ( \n" +
			sql + 
			") a ";
			st.execute(sql);
			System.out.println("13:"+CHF.getCurrentTime());
			/**
			 * 汇总上级科目的结转数
			 */
			sql = "insert into c_carryaccount ( \n" +
			" AccPackageID, SubjectID, AccName, SubYearMonth, SubMonth, DataName,AccSign, \n" +
			" direction, IsLeaf1, Level1, SubjectFullName1, subjectfullname2, direction2, tokenid, standname, tz, \n" +
			" DebitOcc, CreditOcc, carryDebitOcc, carryCreditOcc, carryOcc,  \n" +
			" DebitOccF, CreditOccF, carryDebitOccF, carryCreditOccF, carryOccF  \n" +
			")  \n" +
			"select  \n" +
			" a.AccPackageID, a.SubjectID, a.AccName, b.SubYearMonth, b.SubMonth, b.DataName, b.AccSign,  \n" +
			" a.direction, a.IsLeaf1, a.Level1, a.SubjectFullName1, a.subjectfullname2, a.direction2, a.tokenid, a.standname, a.tz, \n" +
			" sum(b.DebitOcc), sum(b.CreditOcc), sum(b.carryDebitOcc), sum(b.carryCreditOcc), sum(b.carryOcc),  \n" +
			" sum(b.DebitOccF), sum(b.CreditOccF), sum(b.carryDebitOccF), sum(b.carryCreditOccF), sum(b.carryOccF)  \n" +
			"from c_account a,c_carryaccount b    \n" +
			"where a.accpackageid='" + AccPackageID + "'	\n" +
			"and b.accpackageid='" + AccPackageID + "'    \n" +
			"and a.submonth = 1 \n" +
			"and (" + CHF.replaceStr(strWhere, "subjectid", "a.subjectid") + ") \n" +
			"and (a.subjectfullname1 = b.subjectfullname1 or b.subjectfullname1 like concat(a.subjectfullname1,'/%')) \n" +
			"and a.isleaf1 = 0 \n" +
			"group by a.subjectid,b.subyearmonth,b.submonth ,b.dataname " ;
//			System.out.println("汇总上级科目的结转数SQL:"+sql);
			st.execute(sql);
			System.out.println("14:"+CHF.getCurrentTime());
		} catch (Exception e) {
			System.out.println("createKmJzhz 出错的SQL:" + sql);
			e.printStackTrace();
		} finally {
			DbUtil.close(st);
		}
	}
	
	/**
	 * 汇总核算结转数
	 */
	public void createXmJzhz(String AccPackageID) throws Exception {
		Statement st = null;
		String sql = "";
		try {
			ASFuntion CHF = new ASFuntion();
			st = conn.createStatement();
			
			createXmJzhz();
			
			String strWhere = getCarrySubject( AccPackageID);
			System.out.println("20:"+CHF.getCurrentTime());
			
			sql = "delete from c_carryassitemacc where accpackageid = '" + AccPackageID + "' ";
			st.execute(sql);
			System.out.println("20:"+CHF.getCurrentTime());
			/**
			 * 本位币的结转数
			 */
//			sql = "select a.*, \n" +
//			"	(ifnull( a.occ,0) - ifnull(b.occ,0))  as carryOcc, \n" +
//			"	ifnull(a.debitocc,0)  - ifnull(occ1,0)  as carryDebitOcc, \n" +
//			"	ifnull(a.creditocc,0)  - ifnull(occ2,0)  as carryCreditOcc \n" +
//			"	from ( \n" +
//
//			"		select *,a.debitocc-a.creditocc as occ \n" +
//			"		from c_assitementryacc a    \n" +
//			"		where 1=1 \n" +
//			"		and accpackageid = '" + AccPackageID + "' \n" +
//			"		and (" + CHF.replaceStr(strWhere, "subjectid", "accid") + ") \n" +	
//			"		and a.dataname = 0 \n" +
//			"		and a.isleaf1 = 1  \n" +
//
//			"	) a left join (  \n" +
//
//			"		select a.subjectid as accid ,a.assitemid ,subyear,submonth, \n" +
//			"		sum(dirction * AssItemSum) as occ, \n" +
//			"		sum(if(dirction = 1,AssItemSum,0)) occ1, \n" +
//			"		sum(if(dirction = -1,AssItemSum,0)) occ2 from ( \n" +
//			"			select  distinct a.* ,substring(a.VchDate,1,4) as subyear,substring(a.VchDate,6,2) as submonth \n" +
//			"			from c_assitementry a   \n" +
//			"			where 1=1   \n" +
//			"			and accpackageid = '" + AccPackageID + "' \n" +
//			"			and (" + strWhere + ") \n" +
//			"			and a.property like '%2%'    \n" +
//			"		) a where 1=1 \n" +
//			"		group by a.subjectid,a.assitemid ,subyear,submonth \n" +
//
//			"	) b on a.accid =b.accid and a.assitemid = b.assitemid and  a.submonth = b.submonth \n";

			sql = "select a.*, \n" +
			"	("+intOpt+") * (ifnull( (a.debitocc-a.creditocc),0) - ifnull(b.occ,0))  as carryOcc, \n" +
			"	ifnull(a.debitocc,0)  - ifnull(occ1,0)  as carryDebitOcc, \n" +
			"	ifnull(a.creditocc,0)  - ifnull(occ2,0)  as carryCreditOcc \n" +
			"	from c_assitementryacc a left join (  \n" +

			"		select a.subjectid as accid ,a.assitemid ,substring(a.VchDate,1,4) as subyear,substring(a.VchDate,6,2) as submonth, \n" +
			"		sum(dirction * AssItemSum) as occ, \n" +
			"		sum(if(dirction = 1,AssItemSum,0)) occ1, \n" +
			"		sum(if(dirction = -1,AssItemSum,0)) occ2  \n" +
			"		from c_assitementry a   \n" +
			"		where 1=1   \n" +
			"		and accpackageid = '" + AccPackageID + "' \n" +
			"		and (" + strWhere + ") \n" +
			"		and a.property like '%2%'    \n" +
			"		group by a.subjectid,a.assitemid ,substring(a.VchDate,1,4),substring(a.VchDate,6,2)  \n" +

			"	) b on accpackageid = '" + AccPackageID + "' and a.isleaf1 = 1 " +
			"	and a.accid =b.accid and a.assitemid = b.assitemid and a.submonth = b.submonth \n" +
			"	where 1=1 \n" +
			"	and a.accpackageid = '" + AccPackageID + "' \n" +
			"	and (" + CHF.replaceStr(strWhere, "subjectid", "a.accid") + ") \n" +	
			"	and a.dataname = 0 \n" +
			"	and a.isleaf1 = 1  \n" ;
			
			System.out.println("本位币SQL:"+sql);
			
			sql = "insert into c_carryassitemacc ( \n" +
			" AccPackageID, AccID, AssItemID, AssItemName, SubYearMonth, SubMonth,  \n" +
			" direction, IsLeaf1, Level1, AssTotalName1, DataName, AccSign, direction2, \n" +
			" DebitOcc, CreditOcc, carryDebitOcc, carryCreditOcc, carryOcc  \n" +
			")  \n" +
			"select  \n" +
			" AccPackageID, AccID, AssItemID, AssItemName, SubYearMonth, SubMonth,  \n" +
			" direction, IsLeaf1, Level1, AssTotalName1, DataName, AccSign, direction2, \n" +
			" DebitOcc, CreditOcc, carryDebitOcc, carryCreditOcc, carryOcc  \n" +
			"from ( \n" +
			sql + 
			") a ";
			st.execute(sql);
			System.out.println("21:"+CHF.getCurrentTime());
			
			sql = "update c_carryassitemacc set " +
			"DebitOccF = DebitOcc, " +
			"CreditOccF = CreditOcc, " +
			"carryDebitOccF = carryDebitOcc, " +
			"carryCreditOccF = carryCreditOcc, " +
			"carryOccF = carryOcc " +
			"where accpackageid = '" + AccPackageID + "' ";
			st.execute(sql);
			System.out.println("22:"+CHF.getCurrentTime());
			
			/**
			 * 外币的结转数
			 */
			sql = "select a.*, \n" +
			"	("+intOpt+") * (ifnull( a.occ,0) - ifnull(b.occ,0))  as carryOcc, \n" +
			"	ifnull(a.debitocc,0)  - ifnull(occ1,0)  as carryDebitOcc, \n" +
			"	ifnull(a.creditocc,0)  - ifnull(occ2,0)  as carryCreditOcc, \n" +
			"	("+intOpt+") * (ifnull( a.occF,0) - ifnull(b.occF,0))  as carryOccF, \n" +
			"	ifnull(a.debitoccF,0)  - ifnull(occ1F,0)  as carryDebitOccF, \n" +
			"	ifnull(a.creditoccF,0)  - ifnull(occ2F,0)  as carryCreditOccF \n" +
			"	from ( \n" +

			"		select *,a.debitocc-a.creditocc as occ,a.debitoccF-a.creditoccF as occF \n" +
			"		from c_assitementryaccall a    \n" +
			"		where 1=1 \n" +
			"		and accpackageid = '" + AccPackageID + "' \n" +
			"		and (" + CHF.replaceStr(strWhere, "subjectid", "accid") + ")						 \n" +	
			"		and accsign = 1	 \n" +
			"		and a.isleaf1 = 1  \n" +

			"	) a left join (  \n" +

			"		select a.subjectid as accid ,a.assitemid,currency,subyear,submonth, \n" +
			"		sum(dirction * currvalue) as occ, \n" +
			"		sum(if(dirction = 1,currvalue,0)) occ1, \n" +
			"		sum(if(dirction = -1,currvalue,0)) occ2, \n" +
			"		sum(dirction * AssItemSum) as occF, \n" +
			"		sum(if(dirction = 1,AssItemSum,0)) occ1F, \n" +
			"		sum(if(dirction = -1,AssItemSum,0)) occ2F \n" +
			"		from ( \n" +
			"			select  distinct a.* ,substring(a.VchDate,1,4) as subyear,substring(a.VchDate,6,2) as submonth \n" +
			"			from c_assitementry a   \n" +
			"			where 1=1   \n" +
			"			and accpackageid = '" + AccPackageID + "' \n" +
			"			and (" + strWhere + ") \n" +
			"			and a.property like '%2%'    \n" +
			"			and currency <> '' " +
			"		) a where 1=1 \n" +
			"		group by subjectid,assitemid,currency,subyear,submonth \n" +

			"	) b on a.accid =b.accid and a.assitemid = b.assitemid and a.dataName = currency and a.subyearmonth = b.subyear and a.submonth = b.submonth \n";
			
//			System.out.println("外币SQL:"+sql);
			
			sql = "insert into c_carryassitemacc ( \n" +
			" AccPackageID, AccID, AssItemID, AssItemName, SubYearMonth, SubMonth,  \n" +
			" direction, IsLeaf1, Level1, AssTotalName1, DataName, AccSign, direction2, \n" +
			" DebitOcc, CreditOcc, carryDebitOcc, carryCreditOcc, carryOcc,  \n" +
			" DebitOccF, CreditOccF, carryDebitOccF, carryCreditOccF, carryOccF  \n" +
			")  \n" +
			"select  \n" +
			" AccPackageID, AccID, AssItemID, AssItemName, SubYearMonth, SubMonth,  \n" +
			" direction, IsLeaf1, Level1, AssTotalName1, DataName, AccSign, direction2, \n" +
			" DebitOcc, CreditOcc, carryDebitOcc, carryCreditOcc, carryOcc,  \n" +
			" DebitOccF, CreditOccF, carryDebitOccF, carryCreditOccF, carryOccF  \n" +
			"from ( \n" +
			sql + 
			") a ";
			st.execute(sql);
			System.out.println("23:"+CHF.getCurrentTime());
			
			/**
			 * 汇总上级核算的结转数
			 */
			sql = "insert into c_carryassitemacc ( \n" +
			" AccPackageID, AccID, AssItemID, AssItemName, SubYearMonth, SubMonth,  \n" +
			" direction, IsLeaf1, Level1, AssTotalName1, DataName, AccSign, direction2, \n" +
			" DebitOcc, CreditOcc, carryDebitOcc, carryCreditOcc, carryOcc,  \n" +
			" DebitOccF, CreditOccF, carryDebitOccF, carryCreditOccF, carryOccF  \n" +
			")  \n" +
			"select  \n" +
			" a.AccPackageID, a.AccID, a.AssItemID, a.AssItemName, b.SubYearMonth, b.SubMonth,  \n" +
			" a.direction, a.IsLeaf1, a.Level1, a.AssTotalName1, b.DataName, b.AccSign, a.direction2, \n" +
			" sum(b.DebitOcc), sum(b.CreditOcc), sum(b.carryDebitOcc), sum(b.carryCreditOcc), sum(b.carryOcc),  \n" +
			" sum(b.DebitOccF), sum(b.CreditOccF), sum(b.carryDebitOccF), sum(b.carryCreditOccF), sum(b.carryOccF)  \n" +
			"from ( \n" +
			"	select * from c_assitementryacc a \n" +
			"	where a.accpackageid='" + AccPackageID + "'	 \n" +
			"	and a.submonth = 1 " +
			"	and (" + CHF.replaceStr(strWhere, "subjectid", "accid") + ")	 \n" +
			") a,c_carryassitemacc b   \n" +
			"where a.accpackageid='" + AccPackageID + "'	 \n" +
			"and b.accpackageid='" + AccPackageID + "'     \n" +
			"and a.accid = b.accid \n" +
			"and (a.AssTotalName1 = b.AssTotalName1 or b.AssTotalName1 like concat(a.AssTotalName1,'/%'))  \n" +
			"and a.isleaf1 = 0  \n" +
			"group by a.accid,a.AssItemID,b.subyearmonth,b.submonth ,b.dataname ";
//			System.out.println("汇总上级科目的结转数SQL:"+sql);
			st.execute(sql);
			System.out.println("24:"+CHF.getCurrentTime());
			
		} catch (Exception e) {
			System.out.println("createXmJzhz 出错的SQL:" + sql);
			e.printStackTrace();
		} finally {
			DbUtil.close(st);
		}
		
	}
	
	public void create(String AccPackageID) throws Exception {
		try {
			createKmJzhz( AccPackageID);
			createXmJzhz( AccPackageID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public void create1(String AccPackageID) throws Exception {
		Statement st = null;
		ResultSet rs = null;
		String sql = "";
		try {
			st = conn.createStatement();
			sql = "select 1 from c_carryaccount where AccPackageID = '" + AccPackageID + "' limit 1";
			rs = st.executeQuery(sql);
			if(!rs.next()){
				//c_carryaccount表为空，重汇结转数
				createKmJzhz( AccPackageID);
				createXmJzhz( AccPackageID);	
			}
		} catch (Exception e) {
			//c_carryaccount表不存在，重汇结转数
			createKmJzhz( AccPackageID);
			createXmJzhz( AccPackageID);	
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		ASFuntion CHF = new ASFuntion();
		Connection conn=null;
		conn= new DBConnect().getConnect("100651");
		CarryService cs = new CarryService(conn);
		System.out.println("1:"+CHF.getCurrentTime());
		cs.createKmJzhz("1006512009");
		System.out.println("2:"+CHF.getCurrentTime());
		cs.createXmJzhz("1006512009");
		System.out.println("3:"+CHF.getCurrentTime());
		DbUtil.close(conn);
	}
	
	
}
