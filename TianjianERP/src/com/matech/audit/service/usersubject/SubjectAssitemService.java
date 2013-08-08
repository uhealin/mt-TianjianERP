package com.matech.audit.service.usersubject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class SubjectAssitemService {

	private Connection conn;
	
//http://127.0.0.1:8080/AuditSystem/come.do?method=subject
//	科目辅助核算刷新关系表
//	
//	CREATE TABLE `c_subjectassitem` (                        
//	`AccPackageID` int(14) NOT NULL default '0',           
//	`SubjectID` varchar(50) NOT NULL default '',           
//	`SubjectFullName1` varchar(250) default '',            
//	`SubjectFullName2` varchar(250) default '',            
//	`AssitemID` varchar(50) NOT NULL default '',           
//	`AssTotalName1` mediumtext,                            
//	`ifequal` int(1) default '0',                          
//	`Property` varchar(10) default '',                     
//	PRIMARY KEY  (`AccPackageID`,`SubjectID`,`AssitemID`)  
//	) ENGINE=MyISAM DEFAULT CHARSET=gbk ROW_FORMAT=DYNAMIC   
	
//	修改 c_subjectassitem 表
//	alter table `c_subjectassitem` change `AssitemID` `AssitemID` varchar (50)  NULL  ;
//	alter table `c_subjectassitem` drop PRIMARY key, add PRIMARY key (`AccPackageID`, `SubjectID`, `AssitemID`);

/**
	select * from s_autohintselect
	where id =56
	
	select distinct assitemid,asstotalname from( 
		select assitemid,asstotalname from c_assitem where AccPackageID='1003622008' and isleaf =1
		and accid='2205' and asstotalname not like '%现金流量%' 
		union
		select assitemid,AssTotalName1 from c_assitementryacc where AccPackageID='1003622008' and isleaf1 =1
		and accid='2205' and submonth=1 and asstotalname1 not like '%现金流量%' 
	) a where assitemid=''
	union 
	select 1,1 
	from
	(
		select accid,assitemid,asstotalname from c_assitem where AccPackageID='1003622008' and isleaf =1
		and accid='2205' 
		and asstotalname not like '%现金流量%' 
		union
		select accid,assitemid,AssTotalName1 from c_assitementryacc where AccPackageID='1003622008' and isleaf1 =1
		and accid='2205' 
		and submonth=1 and asstotalname1 not like '%现金流量%' 
	) a 
	left join c_subjectassitem b 
	on AccPackageID='1003622008' 
	and ifequal = 1
	and a.accid = b.subjectid
	where b.AccPackageID is  null
	having count(*)=0
*/	   
	/**
	 * /AuditSystem/src/com/matech/audit/service/rectify/RectifyService.java　－－－负值重分类保存 X
	 * /AuditSystem/src/com/matech/audit/service/usersubject/UserSubjectService.java　－－－多科目挂帐 X
	 * /AuditSystem/src/com/matech/audit/work/usersubject/UserSubjectAction.java　－－－多科目挂帐打印 X
	 * /AuditSystem/src/com/matech/audit/work/rectify/ClassifiCationAction.java　－－－负值重分类 X
	 * /AuditSystem/src/com/matech/audit/work/funcCase/FuncCaseAction.java		－－－函证 X
	 * /AuditSystem/src/com/matech/audit/service/function/_9000_0.java		－－－－9999公式 X
	 * /AuditSystem/src/com/matech/audit/work/repair/RepairAction.java  －－－项目科目体系维护 X
	 */
	
	
	public SubjectAssitemService(Connection conn) {
		this.conn = conn;
	}

	
	public void delall(String AccPackageID)throws Exception{
		PreparedStatement ps = null;
		try {
			String sql = "";
			
			sql = "delete from c_subjectassitem where AccPackageID = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	
	public String assitem(String AccPackageID,String AssItemID)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "";
			sql = "select group_concat(distinct AssTotalName1) " +
			" from c_assitementryacc where AccPackageID = ? " +
			" and AssItemID in ("+AssItemID+") " +
			" and submonth = 1 " +
			" and level1 = 1";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			rs = ps.executeQuery();
			if(rs.next()){
				return rs.getString(1);
			}
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	
	public void save(String AccPackageID,String [] SubjectID,String [] AssItemID)throws Exception{
		PreparedStatement ps = null;
		try {
			delall(AccPackageID);
			
			String sql = "";
			
			sql = "insert into c_subjectassitem (AccPackageID,SubjectID,AssTotalName1,property) values (?,?,?,1) ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			for(int i = 0;i < SubjectID.length; i++){
				if(AssItemID[i] != null && !"".equals(AssItemID[i].trim())){
					ps.setString(2, SubjectID[i]);
					ps.setString(3, AssItemID[i]);
//					ps.setString(4, assitem(AccPackageID,AssItemID[i]));
					ps.addBatch();
				}
			}
			ps.executeBatch();
			DbUtil.close(ps);
			
			sql = " update c_subjectassitem  a ,c_account b \n"+
			" set a.SubjectFullName1 = b.SubjectFullName1,a.SubjectFullName2 = b.SubjectFullName2 \n"+
			" where a.AccPackageID =? \n"+
			" and b.AccPackageID =? \n"+
			" and b.submonth=1 \n"+
			" and a.subjectid = b.subjectid";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2, AccPackageID);
			ps.execute();
			DbUtil.close(ps);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}

	public boolean ifEquals(String AccPackageID,String SubjectID,String AssItemID)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			boolean bool = false;
			String sql = "";
			
			ASFuntion CHF=new ASFuntion();
			
			AssItemID = CHF.replaceStr(AssItemID, ",", "','");
			AssItemID = "'" + AssItemID + "'";
			
			sql = " select  " +
			" sum(a.debitocc-b.debitocc) as debitDiff, " + 
			" sum(a.creditocc-b.creditocc) as creditDiff,  " +
			" sum(a.balance-b.balance) as balanceDiff  " +

			" from c_account a,c_assitementryacc b,c_assitem c " + 
			" where a.accpackageid=? " +
			" and b.accpackageid=?  " +
			" and c.accpackageid=?  " +
			" and c.Level0=1  " +
			" and a.SubjectID=b.accid " + 
			" and a.submonth=b.submonth  " +
			" and c.AssItemID=b.AssItemID  " +
			" and c.accid=a.subjectid  " +

			" and b.AssTotalName1 in ("+AssItemID+") " +
			" and a.subjectid in ("+SubjectID+")";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2, AccPackageID);
			ps.setString(3, AccPackageID);
			rs = ps.executeQuery();
			if(rs.next()){
				double dou = rs.getDouble("balanceDiff");
				if(dou == 0){
					bool = true;
				}else{
					bool = false;
				}
				
			}
			
			return bool;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 取数时得到核算
	 * @param AccPackageID
	 * @param SubjectFullName2
	 * @return
	 * @throws Exception
	 */
	public String getFunction(String AccPackageID,String SubjectFullName2)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ASFuntion CHF=new ASFuntion();
			String AssTotalName1 = "";
			
			String sql = "select distinct a.*  " + 
			" from c_subjectassitem a ,(" +
			"	select * from c_account " +
			"	where AccPackageID = ?  " +
			"	and (SubjectFullName2 = ? or SubjectFullName2 like concat(?,'/%'))" +
			"	and submonth = 1 " +
			" ) b " +
			" where a.AccPackageID = ? " +
			" and (b.subjectfullname1 = a.subjectfullname1 or b.subjectfullname1 like concat(a.subjectfullname1,'/%')) ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2, SubjectFullName2);
			ps.setString(3, SubjectFullName2);
			ps.setString(4, AccPackageID);
			rs = ps.executeQuery();
			while(rs.next()){
				AssTotalName1 += "," + CHF.showNull(rs.getString("AssTotalName1")) ;
			}
			if(!"".equals(AssTotalName1)){
				AssTotalName1 = AssTotalName1.substring(1);
				AssTotalName1 = CHF.replaceStr(AssTotalName1, ",", ";");
			}
			AssTotalName1 += getFunction1( AccPackageID, SubjectFullName2);
			
			return AssTotalName1;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
	}
	
	/**
	 * 取数时得到科目的新增核算
	 * @param AccPackageID
	 * @param SubjectFullName2
	 * @return
	 * @throws Exception
	 */
	public String getFunction1(String AccPackageID,String SubjectFullName2)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ASFuntion CHF=new ASFuntion();
			String SubjectID = "";
			String AssTotalName1 = "";
			
			String sql = "select * from c_subjectassitem where AccPackageID = ? and SubjectFullName2 = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2, SubjectFullName2);
			rs = ps.executeQuery();
			if(rs.next()){
				SubjectID = CHF.showNull(rs.getString("SubjectID"));
				AssTotalName1 = CHF.showNull(rs.getString("AssTotalName1"));
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			if("".equals(SubjectID)){
				return "";
			}
			
			sql = "select distinct a.AssTotalName from (" +
			"	select distinct assitemid,AssItemName,AssTotalName" +
			"	from c_assitem a" +
			"	where AccPackageID = ? " +
			"	and a.level0 =1" +
			") a,(" +
			"	select distinct a.accid,a.assitemid,a.AssItemName,a.AssTotalName1 " +
			"	from c_assitementryacc a " +
			"	left join c_assitem b" +
			"	on a.AccPackageID = ? " +
			"	and b.AccPackageID = ?" +
			"	and a.submonth=1  " +
			"	and a.accid = b.accid" +
			"	and a.assitemid = b.assitemid" +
			"	where 1=1" +
			"	and a.AccPackageID = ? " +
			"	and a.submonth=1 " +
			"	and a.accid like concat(?,'%') " +
			"	and b.accid is null" +
			") b where (a.AssTotalName = b.AssTotalName1 or b.AssTotalName1 like concat(a.AssTotalName,'/%')) " +
			"and instr(?,AssTotalName ) >0 ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2, AccPackageID);
			ps.setString(3, AccPackageID);
			ps.setString(4, AccPackageID);
			ps.setString(5, SubjectID);
			ps.setString(6, AssTotalName1);
			rs = ps.executeQuery();
			String AssTotalName = "";
			while(rs.next()){
				AssTotalName += ";" + CHF.showNull(rs.getString("AssTotalName"));
			}
			return AssTotalName;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
		
	}
	
	
	/**
	 * 初始化：用默认核算求科目辅助核算披露设置
	 * @param AccPackageID
	 * @throws Exception
	 */
	public void autoSetup(String AccPackageID) throws Exception{
		PreparedStatement ps = null;
		try {
			
			delall(AccPackageID);
			
			String [] AssItem = new String[]{"客户","供应商","往来","费用"};	//默认核算
			
			String sql = "";
			
			sql = "alter table c_subjectassitem change AssitemID AssitemID varchar (50)  NULL  ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			
			try {
				sql = "alter table c_subjectassitem drop PRIMARY key" ;
				ps = conn.prepareStatement(sql);
				ps.execute();
				
				sql = "alter table c_subjectassitem add index AccPackageID (AccPackageID, SubjectID, AssitemID) ";
				ps = conn.prepareStatement(sql);
				ps.execute();
			} catch (Exception e) {} 
			
			sql = "";
			for (int i = 0; i < AssItem.length; i++) {
				String string = AssItem[i];
				sql += "or AssTotalName1 like '%"+string+"%' ";
			}
			if(!"".equals(sql)){
				sql = " and (" + sql.substring(2) + ") \n";
			}
			
			auto( "c_subjectassitem", AccPackageID, sql);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 登录项目时，检查是否有最新的披露核算
	 * 1、披露核算表为空，自动初始化
	 * 2、披露核算表不为空
	 * 2.1：有值，按值来解释
	 * 2.2：无值，按初始化来解释
	 * @param AccPackageID
	 * @throws Exception
	 */
	public void autoSetup1(String AccPackageID) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			String [] AssItem = new String[]{"客户","供应商","往来","费用"};	//默认核算
			int ii = 1;
			
			sql = "select * from c_subjectassitem where AccPackageID = ? limit 1";
			ps = conn.prepareStatement(sql);
			ps.setString(ii++, AccPackageID);
			rs = ps.executeQuery();
			if(rs.next()){
				//2、披露核算表不为空
				DbUtil.close(rs);
				DbUtil.close(ps);
				
				ii = 1;
				sql = "select * from c_subjectassitem where AccPackageID = ?  and property = 1 limit 1"; 
				ps = conn.prepareStatement(sql);
				ps.setString(ii++, AccPackageID);
				rs = ps.executeQuery();
				if(rs.next()){
					//表示是最新的披露
					return;
				}
				
				sql = "alter table c_subjectassitem change AssitemID AssitemID varchar (50)  NULL  ";
				ps = conn.prepareStatement(sql);
				ps.execute();
				
				try {
					sql = "alter table c_subjectassitem drop PRIMARY key" ;
					ps = conn.prepareStatement(sql);
					ps.execute();
					
					sql = "alter table c_subjectassitem add index AccPackageID (AccPackageID, SubjectID, AssitemID) ";
					ps = conn.prepareStatement(sql);
					ps.execute();
				} catch (Exception e) {} 
				
				sql = "drop table if exists tt_subjectassitem";
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
				
				sql = "drop table if exists tt_subjectassitem1";
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
				
				sql = "create table tt_subjectassitem like c_subjectassitem";
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
				
				sql = "create table tt_subjectassitem1 like c_subjectassitem";
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
				
				sql = "";
				for (int i = 0; i < AssItem.length; i++) {
					String string = AssItem[i];
					sql += "or AssTotalName1 like '%"+string+"%' ";
				}
				if(!"".equals(sql)){
					sql = " and (" + sql.substring(2) + ") \n";
				}
				
				auto( "tt_subjectassitem", AccPackageID, sql);
				
				sql = "insert into tt_subjectassitem1 " +
				" select b.* " +
				" from c_subjectassitem a,tt_subjectassitem b " +
				" where a.AccPackageID=? " +
				" and b.AccPackageID=? " +
				" and a.subjectfullname2 = b.subjectfullname2 " +
				" and instr(a.asstotalname1,b.asstotalname1)>0";
				ii = 1;
				ps = conn.prepareStatement(sql);
				ps.setString(ii++, AccPackageID);
				ps.setString(ii++, AccPackageID);
				ps.execute();

				sql = " delete a  " +
				" from tt_subjectassitem a " +
				" left join tt_subjectassitem1 b on a.subjectfullname2 = b.subjectfullname2 " +
				" left join tt_subjectassitem1 c on a.subjectfullname2 = c.subjectfullname2 and a.asstotalname1 = c.asstotalname1 " +
				" where b.AccPackageID is not null " +
				" and c.AccPackageID is null ";
				ps = conn.prepareStatement(sql);
				ps.execute();
				
				/**
				 * 从临时表到主表
				 */
				delall(AccPackageID);
				
				sql = " insert into c_subjectassitem (AccPackageID,SubjectID,SubjectFullName1,SubjectFullName2,AssitemID,AssTotalName1,ifequal,Property) \n" +
				" select a.accpackageid,a.subjectid,a.subjectfullname1,a.subjectfullname2,\n" +
				" ifnull(b.assitemid,'') assitemid,ifnull(b.asstotalname,'') asstotalname,if(b.assitemid is null ,1,0) ifequal, 1 as Property  \n" +
				" from (\n" +
				" 	select a.accpackageid,a.subjectid,a.subjectfullname1,a.subjectfullname2, \n" +
				" 	group_concat(if(asstotalname1 = '',null,asstotalname1)) as asstotalname1 \n" +
				" 	from tt_subjectassitem a \n" +
				" 	group by subjectid \n" +
				" ) a left join ( \n" +
				" 	select distinct assitemid,asstotalname from c_assitem where AccPackageID =? and level0 = 1 \n" +   
				" ) b  \n" +
				" on 1=1 \n" +
				" and instr(asstotalname1,asstotalname)>0 \n"; 
				
				ps = conn.prepareStatement(sql);
				ps.setString(1, AccPackageID);
				ps.execute();
				DbUtil.close(ps);
				
				sql = "drop table if exists tt_subjectassitem";
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
				
				sql = "drop table if exists tt_subjectassitem1";
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
				
			}else{
				//1、披露核算表为空，自动初始化
				autoSetup( AccPackageID);
			}
			
		} catch (Exception e) {
			System.out.println("出错的SQL：" + sql);
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	
	public void auto(String table,String AccPackageID,String strSql) throws Exception{
		PreparedStatement ps = null;
		try {
			
			String sql = " insert into "+table+" (AccPackageID,SubjectID,subjectfullname1,subjectfullname2,assitemid,AssTotalName1,ifequal,property) \n" +

			" 	select  distinct \n" +
			" 	a.accpackageid,a.accid,a.subjectfullname,a.subjectfullname2, \n" + 
			" 	if(d.subjectid is null , if(a.asstotalname1 like '现金流量%','' , a.assitemid) ,ifnull(b.assitemid,'')) as assitemid, \n" +
			" 	if(d.subjectid is null , if(a.asstotalname1 like '现金流量%','' , a.AssTotalName1) ,ifnull(b.AssTotalName1,'')) as AssTotalName1, \n" +
			" 	if(d.subjectid is null , if(a.asstotalname1 like '现金流量%',1 ,0),if(b.assitemid is null,1 ,0)) as ifequal, \n" +
			" 	1 as property   \n" +
			" 	from ( \n" + 
			" 		select a.accpackageid,accid, assitemid , AssTotalName1 ,subjectfullname,b.subjectid,subjectfullname1,subjectfullname2 \n" +
			" 		from c_accpkgsubject a ,(  \n" +
			" 			select subjectid,subjectfullname1,subjectfullname2 from c_account b \n" + 
			" 			where 1=1  \n" +
			" 			and b.accpackageid = ? \n" +   
			" 			and b.level1=1    \n" +
			" 			and b.submonth=1    \n" +
			" 		) b ,(" +
			"			select * from c_assitementryacc c \n" +
			"			where c.accpackageid = ?  \n" +
			"			and c.level1 = 1    \n" +
			"			and c.submonth = 1   \n" +
			"		) c   \n" +
			" 		where c.accpackageid = ? \n" + 
			" 		and a.accpackageid = ?   \n" +
			" 		and c.level1 = 1   \n" +
			" 		and c.submonth = 1   \n" +
			" 		and a.subjectid = c.accid \n" +
			" 		and (a.subjectfullname = b.subjectfullname1 or a.subjectfullname like concat(b.subjectfullname1,'/%')) \n" + 

			" 	) a left join (  \n" +
			" 		select distinct accid,assitemid,AssTotalName1 from c_assitementryacc \n" + 
			" 		where accpackageid = ?   \n" +
			" 		and level1 = 1   \n" +
			" 		and submonth = 1   \n" +
			strSql + 
			" 	) b on a.accid = b.accid and a.assitemid = b.assitemid \n" +
			" 	left join (   \n" +
			" 		select distinct accid,assitemid,AssTotalName1 from c_assitementryacc \n" + 
			" 		where accpackageid = ?    \n" +
			" 		and level1 = 1    \n" +
			" 		and submonth = 1    \n" +
			strSql + 
			"  	) c on a.accid = c.accid   \n" +

			" 	left join (  \n" +
			" 		select distinct b.subjectid \n" +
			" 		from c_accpkgsubject a,c_accpkgsubject b,(" +
			"			select * from c_assitementryacc c \n" +
			"			where c.accpackageid = ?  \n" +
			"			and c.level1 = 1    \n" +
			"			and c.submonth = 1   \n" +
			"		) c \n" +
			" 		where c.accpackageid = ?   \n" +
			" 		and a.accpackageid = ?   \n" +
			" 		and b.accpackageid = ?   \n" +
			" 		and b.level0 = 1   \n" +
			" 		and c.level1 = 1   \n" +
			" 		and c.submonth = 1   \n" +
			strSql + 
			" 		and a.subjectid = c.accid \n" +
			" 		and (a.subjectfullname = b.subjectfullname or a.subjectfullname like concat(b.subjectfullname,'/%')) \n" +
			" 	) d on a.subjectid = d.subjectid \n" +
			" 	where 1=1  \n" +
			" 	and (c.accid is null or b.accid is not null) \n" +
			" 	order by a.accid "; 
			
			int i = 1;
			System.out.println(sql);
			ps = conn.prepareStatement(sql);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, AccPackageID);
			
			ps.setString(i++, AccPackageID);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, AccPackageID);
			
			ps.execute();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	
	/**
	 * 明细核算下拉
	 */
	public ResultSet getAssitem(String tdId,String AccPackageID,String SubjectID)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			StringBuffer sb = new StringBuffer("");
			ASFuntion CHF=new ASFuntion();
			
			String sql = "select *," +
			"if(ifnull(assitemid,'') ='','',if((Balance - bBalance) = 0.00 ,'',concat('总帐与[',AssTotalName1,']核算帐不平，将影响底稿明细表结果。'))) as memo " +
			"from ( " +
			
			"\n select if(a.accid=a.subjectid1,concat(a.accid,'_1'),a.accid) as _id,a.accid as subjectid,a.subjectid1 as _parent,'true' as _is_leaf, a.*, " +
			"\n group_concat(distinct \"'\",if(b.AssitemID='',null,b.AssitemID),\"'\") as AssitemID," +
			"\n group_concat(distinct if(b.AssTotalName1 = '',null,b.AssTotalName1)) as AssTotalName1,  " +
			"\n sum(b.Balance) as bBalance " +
			
			"\n from ( " +
			"\n 	select a.accpackageid,a.subjectid as subjectid1,a.accname as subjectname1,a.subjectfullname2 as subjectfullname," +
			"\n		accid,bfullname as SubjectName," +
			"\n		group_concat(distinct AssTotalName1) as AssTotalName,sum(b.Balance ) as Balance " +
			"\n 	from c_account a,( " +
			"\n			select subjectfullname1 as bfullname,a.Balance*a.direction2 as Balance,c.* " + 
			"\n 		from c_account a ,( " +
			"\n 			select accid,assitemid,AssTotalName1 " +
			"\n 			from c_assitementryacc " +
			"\n 			where 1=1 " +
			"\n 			and level1=1 " +
			"\n 			and accpackageid = ? " +
			"\n 			and submonth = 1 " +
			"\n 		) c,c_accpkgsubject d  " +
			"\n 		where a.accpackageid = ? and a.submonth = 12 and a.subjectid = accid " +
			
			"\n			and d.AccPackageID = ? and d.subjectid =? " +
			"\n			and (a.subjectfullname1 = d.subjectfullname or a.subjectfullname1 like concat(d.subjectfullname,'/%')) " +
			
			"\n 	) b " +
			"\n 	where 1=1 " +
			"\n 	and a.AccPackageID = ? " +
			"\n 	and a.level1=1 " +
			"\n		and a.submonth=1 " +
			"\n 	and (a.subjectfullname1 = b.bfullname or b.bfullname like concat(a.subjectfullname1,'/%')) " +
			"\n 	group by accid  " +
			"\n 	order by subjectid " +
			"\n ) a left join (" +
			
			"\n 	select a.* ,b.Balance*b.direction2 as Balance " +
			"\n 	from c_subjectassitem a left join c_assitementryacc b " +
			"\n 	on 1=1 " +
			"\n 	and a.accpackageid = ? " + 
			"\n 	and b.accpackageid = ?  " +
			"\n 	and b.submonth = 12 " +
			"\n 	and a.subjectid = b.accid " +
			"\n 	and a.assitemid = b.assitemid " +
			"\n 	where 1=1 " +
			"\n 	and a.accpackageid = ? " +
			
			"\n ) b " +
			"\n on 1=1  " +
			"\n and a.accpackageid = ? " +
			"\n and b.accpackageid = ? " +
			"\n and a.accid = b.subjectid " +
			"\n where 1=1 " +
			"\n and a.subjectid1 = ? " +
			"\n group by a.accid  " +
			
			") a  order by a.accid " ;
			
			System.out.println(sql);
			int i = 1;
			ps = conn.prepareStatement(sql);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, SubjectID);
			ps.setString(i++, AccPackageID);
			
			ps.setString(i++, AccPackageID);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, AccPackageID);
			ps.setString(i++, SubjectID);
			rs = ps.executeQuery();
			
			String bgColor = "#B2C2D2";
			i = 1;
//			while(rs.next()){
//				String subjectid = CHF.showNull(rs.getString("subjectid"));
//				String subjectname = CHF.showNull(rs.getString("subjectname"));
//				String subjectfullname = CHF.showNull(rs.getString("subjectfullname"));
//				String accid = CHF.showNull(rs.getString("accid"));
//				String bfullname = CHF.showNull(rs.getString("bfullname"));
//				String AssTotalName = CHF.showNull(rs.getString("AssTotalName"));
//				String AssitemID = CHF.showNull(rs.getString("AssitemID"));
//				String AssTotalName1 = CHF.showNull(rs.getString("AssTotalName1"));
//				//onmousemove=\"return selectMultiCell(event);\" onselectstart=\"return false\" onmousedown=\"return selectCell(event)\"
//				sb.append("<tr id='"+tdId+"' onmousemove=\"return selectMultiCell(event);\" onselectstart=\"return false\" onmousedown=\"return selectCell(event)\"  onmouseover=\"this.bgColor='#E4E8EF';\" style='CURSOR: hand'  onmouseout=\"this.bgColor='"+bgColor+"';\" bgColor='"+bgColor+"' height='18'  SubjectID='"+accid+"' subMonth='0' >");
//				sb.append("<td  noWrap>"+accid+"</td>");
//				sb.append("<td  noWrap>"+bfullname+"</td>");
//				sb.append("<td  noWrap>"+AssTotalName+"</td>");
//				sb.append("<td  noWrap>");
//						
//				sb.append("<input type='hidden' id='SubjectID" + subjectid + "_" +  i + "' name = 'SubjectID' value = \""+accid+"\">");
//				sb.append("<input accid =\""+accid+"\" name='AssItemID' id='AssItemID" + subjectid + "_" +  i + "' type='text' size='30'  value='"+AssTotalName1+"' onkeydown='onKeyDownEvent();' onkeyup='onKeyUpEvent();' onclick='onPopDivClick(this);' valuemustexist=true multiselect=true autoid=518 refer=AccPackageID refer1=SubjectID"+ subjectid +  "_" + i +" title='核算类型' />");
//				
//				sb.append("<input type='hidden' name='AssItemName' id='AssItemName"+ subjectid + "_" +  i +"' value = '' >");
//				sb.append("<input type='hidden' name='ifEqual' id='ifEqual"+ subjectid + "_" +  i +"' value = '0' >");
//				
//				sb.append("<input onclick='getAssitemID(this);' rowIndex = '"+ subjectid + "_" + i +"'  type='checkbox' id='gifEqual" + subjectid +  "_" + i + "' name = 'gifEqual' value = '0' title='选择是否已科目方式来分析本科目。\n选中就表示此科目不考虑核算类型；\n不选就请选择对应核算类型！' />");
//				sb.append("不披露&nbsp;&nbsp");
//				
//				sb.append("</td>");
//				sb.append("</tr>");	
//				
//				i++;
//			}
			
			return rs;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} 
		
	}
	
	
	public void save(String AccPackageID,String [] SubjectID,String [] AssItemID,String [] ifEqual)throws Exception{
		PreparedStatement ps = null;
		try {
			
			String sql = "drop table if exists tt_subjectassitem";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "drop table if exists tt_subjectassitem1";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "create table tt_subjectassitem like c_subjectassitem";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "create table tt_subjectassitem1 like c_subjectassitem";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "insert into tt_subjectassitem1 (AccPackageID,SubjectID,AssTotalName1,ifEqual) values (?,?,?,?) ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			for(int i = 0;i < SubjectID.length; i++){
				ps.setString(2, SubjectID[i]);
				ps.setString(3, AssItemID[i]);
				ps.setString(4, ifEqual[i]);
				ps.addBatch();
			}
			ps.executeBatch();
			DbUtil.close(ps);
			
			sql = " update tt_subjectassitem1  a ,c_account b \n"+
			" set a.SubjectFullName1 = b.SubjectFullName1,a.SubjectFullName2 = b.SubjectFullName2,a.property = b.isleaf1 \n"+
			" where a.AccPackageID =? \n"+
			" and b.AccPackageID =? \n"+
			" and b.submonth=1 \n"+
			" and a.subjectid = b.subjectid";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2, AccPackageID);
			ps.execute();
			DbUtil.close(ps);
			
			auto( "tt_subjectassitem", AccPackageID, "");
			
			String strSql = "select subjectid,subjectfullname1,subjectfullname2,ifequal,property, \n" +
			"	b.assitemid,\n" +
			"	b.asstotalname \n" +
			"	from tt_subjectassitem1 a left join ( \n" +
			"		select distinct assitemid,asstotalname from c_assitem where AccPackageID =? and level0 = 1 \n" +  
			"	) b \n" +
			"	on  a.AccPackageID =? \n" +
			"	and instr(concat(',',asstotalname1,','),concat(',',asstotalname,','))>0  \n" +
			"	where 1=1  \n" ;

			sql = "update  \n" +
			"	tt_subjectassitem a \n" +
			"	left join ( \n" +
			
			strSql +
			
			"	) b   \n" +
			"	on 1=1 \n" +
			"	and b.property = 0 \n" +
			"	and (a.subjectfullname1 = b.subjectfullname1 or a.subjectfullname1 like concat(b.subjectfullname1,'/%')) \n" +
			"	and a.assitemid = b.assitemid \n" +

			"	set  \n" +
			"	a.asstotalname1 = ifnull(b.asstotalname,'')";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2, AccPackageID);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "update tt_subjectassitem a,( \n" +
			"		select a.subjectid ,a.assitemid, \n" +
			"		group_concat(if(a.assitemid = b.assitemid,b.asstotalname,null)) as asstotalname \n" +
			"		from  tt_subjectassitem a,( \n" +
			strSql +
			"		) b   \n" +
			"		where 1=1 \n" +
			"		and b.property = 1 \n" +
			"		and a.subjectid = b.subjectid \n" +
			"		group by a.subjectid ,a.assitemid \n" +
			"	) b \n" +
			"	set a.asstotalname1 = ifnull(b.asstotalname,'') \n" +
			"	where a.subjectid = b.subjectid  \n" +
			"	and a.assitemid = b.assitemid"; 
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2, AccPackageID);
			ps.execute();
			DbUtil.close(ps);
			
			/**
			 * 从临时表到主表
			 */
			delall(AccPackageID);
			
			sql = " insert into c_subjectassitem (AccPackageID,SubjectID,SubjectFullName1,SubjectFullName2,AssitemID,AssTotalName1,ifequal,Property) \n" +
			" select a.accpackageid,a.subjectid,a.subjectfullname1,a.subjectfullname2,\n" +
			" ifnull(b.assitemid,'') assitemid,ifnull(b.asstotalname,'') asstotalname,if(b.assitemid is null ,1,0) ifequal, 1 as Property  \n" +
			" from (\n" +
			" 	select a.accpackageid,a.subjectid,a.subjectfullname1,a.subjectfullname2, \n" +
			" 	group_concat(if(asstotalname1 = '',null,asstotalname1)) as asstotalname1 \n" +
			" 	from tt_subjectassitem a \n" +
			" 	group by subjectid \n" +
			" ) a left join ( \n" +
			" 	select distinct assitemid,asstotalname from c_assitem where AccPackageID =? and level0 = 1 \n" +   
			" ) b  \n" +
			" on 1=1 \n" +
			" and instr(concat(',',asstotalname1,','),concat(',',asstotalname,','))>0  \n"; 
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "drop table if exists tt_subjectassitem";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			sql = "drop table if exists tt_subjectassitem1";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	   
	
	/**
	 * 是否可以新增核算
	 * @param AccPackageID
	 * @param SubjectID
	 * @param AssitemID
	 * @return
	 * @throws Exception
	 */
	public String isAssitem(String AccPackageID,String SubjectID,String AssitemID) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			String result = "1",sql = "select * from c_subjectassitem where AccPackageID = ? and SubjectID = ? and AssitemID = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			ps.setString(2, SubjectID);
			ps.setString(3, AssitemID);
			rs = ps.executeQuery();
			if(rs.next()){
				result = rs.getString("ifequal");
			}
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	
	}
	
	/**
	 * 是否是新的披露设置
	 * @param AccPackageID
	 * @return
	 * @throws Exception
	 */
	public boolean ifnew(String AccPackageID) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			String sql = "select * from c_subjectassitem where AccPackageID = ? and property = 1 ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccPackageID);
			rs = ps.executeQuery();
			if(rs.next()){
				return true;
			}
			
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	
	}
	
}
