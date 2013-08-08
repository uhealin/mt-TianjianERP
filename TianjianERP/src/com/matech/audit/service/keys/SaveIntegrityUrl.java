package com.matech.audit.service.keys;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;

import com.matech.audit.service.autotoken.AutoTokenService;
import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.project.model.Project;
import com.matech.audit.service.rectify.RectifyService;
import com.matech.audit.service.rectify.RectifyTable;
import com.matech.audit.service.rectify.SubjectEntryTable;
import com.matech.audit.service.rectify.VoucherTable;
import com.matech.audit.service.subjectType.SubjectTypeService;
import com.matech.framework.pub.autocode.DELAutocode;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class SaveIntegrityUrl {
	private String proid;
	private String pkgid;
	private String tempTable;
	private String userid;
	private String username;
	private HttpServletRequest request;
	private Connection conn;
	
	ASFuntion CHF = new ASFuntion();
	
	public  SaveIntegrityUrl(Connection conn){
		this.conn = conn;
	}
	
	public SaveIntegrityUrl(Connection conn, String proid, String pkgid,
			String tempTable, String userid, String username,
			HttpServletRequest request) {
		this.conn=conn;
		this.proid = proid; // 项目ID
		this.pkgid = pkgid; // 帐套编号
		this.tempTable = tempTable; // 临时表名称
		
		this.userid = userid; // 帐套编号
		this.username = username; // 临时表名称
		
		this.request = request; // 网页信息
	}
	

	
	public String getResult() throws Exception {
		String result = "";
		
		try{
			if(this.tempTable.toLowerCase().equals("t_subjectIntegrity".toLowerCase())){
				//调整EXCEL导入
				saveIntegrity();
				
			}
			
			
			return result;
		} catch (Exception e) {
			throw e;
		}
	}
	
	private void saveIntegrity() throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			
			String year = "0";
			sql = "select distinct myyear from t_subjectintegrity where projectid = " + this.proid;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				year = rs.getString(1);
			}
			DbUtil.close(rs);
            DbUtil.close(ps);
            
            String tempTab = "tt_"+DELUnid.getCharUnid();
            
            String vocationid = "0",dpID = pkgid.substring(0, 6) ;
            sql = "select * from k_customer where departid='" + dpID + "'";
            ps = conn.prepareStatement(sql);
            ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				vocationid = rs.getString("VocationID");
			}
			DbUtil.close(rs);
            DbUtil.close(ps);
            
			//初始化tt_subjectintegrity表，9000公式刷新的结果表
            setSql( pkgid, proid, vocationid, tempTab, year);
            
//			try {
//				sql = "select 1 from tt_subjectintegrity limit 1 ";
//				ps = conn.prepareStatement(sql);
//				rs = ps.executeQuery();
//				if(!rs.next()){
//					setSql( pkgid, proid, vocationid, tempTab, year);
//				}
//			} catch (Exception e) {
//				setSql( pkgid, proid, vocationid, tempTab, year);
//			}
			
			sql = "drop table if exists  " + tempTab;
			ps = conn.prepareStatement(sql);
			ps.execute();
            DbUtil.close(ps);
			
            /**
             * 删除没有改变的完整性对照
             */
//            sql = "delete b from tt_subjectintegrity a , t_subjectintegrity b " +
//            " where b.projectid ="+proid+" and  a.subjectid = b.subjectid " +
//            " and ifnull(a.standkey1,'') = ifnull(b.standkey,'') ";
            sql = "DELETE b " +
            "	FROM tt_subjectintegrity a INNER JOIN t_subjectintegrity b  " +
            "	ON b.projectid ="+proid+" AND  a.subjectid = b.subjectid  " +
            "	AND IFNULL(a.standkey1,'') = IFNULL(b.standkey,'') " +
            "	LEFT JOIN  ( " +
            "		SELECT DISTINCT " + 
            "		SUBSTRING(a.subjectfullname,1,IF(INSTR(a.subjectfullname,'/') = 0,LENGTH(a.subjectfullname),INSTR(a.subjectfullname,'/')-1)) AS keyname " +
            "		FROM tt_subjectintegrity a , t_subjectintegrity b  " +
            "		WHERE b.projectid ="+proid+" AND  a.subjectid = b.subjectid " + 
            "		AND IFNULL(a.standkey1,'') <> IFNULL(b.standkey,'') " +
            "	) c " +
            "	ON SUBSTRING(a.subjectfullname,1,IF(INSTR(a.subjectfullname,'/') = 0,LENGTH(a.subjectfullname),INSTR(a.subjectfullname,'/')-1)) = c.keyname " +
            "	WHERE c.keyname IS NULL"; 
            ps = conn.prepareStatement(sql);
			ps.execute();
            DbUtil.close(ps);
			
            /**
             * 没有记录，就表示不用重新做完整性
             */
            sql = "select 1 from t_subjectintegrity limit 1 ";
            ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(!rs.next()){
				
				/**
				 * 删除tt_subjectintegrity表
				 */
				sql = "drop table if exists tt_subjectintegrity　";
				ps = conn.prepareStatement(sql);
				ps.execute();
	            DbUtil.close(ps);
				
				
				return ;
			}
			DbUtil.close(rs);
            DbUtil.close(ps);
            
            /**
             * 反->求一级的标准科目
             * 检查一级 
             * 	1、对照：2级或末级都一样时，一级的对照=2级或末级的对照
             * 	2、对照：2级或末级都不一样时，一级的对照=还是用来的对照
             */
            //修改一级对照，一级的对照=2级或末级的最多的对照
            sql = "UPDATE  " +
            "	t_subjectintegrity a,( " +
            "		SELECT a.subjectfullname , " +
            "		MAX(SUBSTRING(b.standkey,1,IF(INSTR(b.standkey,'/') = 0,LENGTH(b.standkey),INSTR(b.standkey,'/')-1))) AS aStandkey " +
            "		FROM tt_subjectintegrity a,( " +
            "			SELECT a.subjectfullname,a.IsLeaf,a.Level0,b.* " +
            "			FROM tt_subjectintegrity a , t_subjectintegrity b  " +
            "			WHERE b.projectid ="+proid+" and trim(b.subjectname) = a.subjectfullname	 " +
            "		) b    " +
            "		WHERE  (b.subjectfullname = a.subjectfullname OR b.subjectfullname LIKE CONCAT(a.subjectfullname,'/%')) " +         
            "		AND a.level0 = 1 " +
            "		AND b.standkey <> '无需对照' " +
            "		GROUP BY a.subjectfullname  " +
            "	) b  " +
            "	SET a.standkey = b.aStandkey " +
            "	WHERE trim(a.subjectname) = b.subjectfullname " +
            "	AND a.standkey = '无需对照'";
            ps = conn.prepareStatement(sql);
			ps.execute();
            DbUtil.close(ps);
            
            //删除与一级对照一样的2级或末级对照
            sql = "DELETE a FROM  " +
            "	t_subjectintegrity a,( " +
            "		SELECT DISTINCT a.subjectfullname,b.standkey AS aStandkey " +
            "		FROM tt_subjectintegrity a,( " +
            "			SELECT a.subjectfullname,a.IsLeaf,a.Level0,b.* " +
            "			FROM tt_subjectintegrity a , t_subjectintegrity b  " +
            "			WHERE b.projectid ="+proid+" and trim(b.subjectname) = a.subjectfullname " +
            "			AND a.level0 = 1 " +
            "		) b    " +
            "		WHERE  (a.subjectfullname = b.subjectfullname OR a.subjectfullname LIKE CONCAT(b.subjectfullname,'/%')) " +         
            "		AND a.level0 <> 1 " +
            "	) b  " +
            "	WHERE trim(a.subjectname) = b.subjectfullname " +
            "	AND a.standkey = b.aStandkey " +
            "	AND a.standkey <> '坏账准备'";
            ps = conn.prepareStatement(sql);
			ps.execute();
            DbUtil.close(ps);
            
            /**
             * 完整性对对照
             */
            KeyValue kv=new KeyValue();
            
            boolean bool = false;
            sql = "select b.*,a.subjectfullname,a.isleaf,a.level0,a.standkey as standname " +
            " from tt_subjectintegrity a , t_subjectintegrity b,c_account c  " +
            " where b.projectid ="+proid+" and (trim(b.subjectname) = a.subjectfullname )" +
            " AND c.accpackageid ="+pkgid+" AND c.submonth =1 " +
            " AND b.subjectid = c.subjectid " +
            //" AND b.standkey <> c.subjectfullname2" +
            " ORDER BY b.subjectid ";
            ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				bool = true;
				
				String iAccName = rs.getString("standkey");	
				String iSubjectName = rs.getString("subjectfullname");	
				String iSubjectid = rs.getString("subjectid");
				
				String standname = rs.getString("standname");
				
				if(!"".equals(iAccName)){
					String str = kv.getType(iAccName,iSubjectName,dpID);
					if("1".equals(str)){
						kv.addKey1(iAccName,iSubjectName,dpID,true,iSubjectid);
					} else if("2".equals(str)){
						kv.modifyFullPath(iAccName,iSubjectName,dpID);
					}	
				}else{
					//为空就是还原对照
					kv.delKeySon(standname,iSubjectName,dpID);
					kv.delKey1(standname,iSubjectName,dpID);
					
				}
				
				if (!"".equals(iSubjectid) && !"".equals(iAccName)){
					org.util.Debug.prtOut("A4=" + new ASFuntion().getCurrentTime());
				
					//开始科目连续性的自动对照
					new AutoTokenService(conn).auto(dpID,iSubjectName);
				
					org.util.Debug.prtOut("A5=" + new ASFuntion().getCurrentTime());
				}
			}
			DbUtil.close(rs);
            DbUtil.close(ps);
			
            /**
			 * 删除t_subjectintegrity表
			 */
			sql = "delete from t_subjectintegrity where projectid = " + this.proid;
			ps = conn.prepareStatement(sql);
			ps.execute();
            DbUtil.close(ps);
            
			/**
			 * 删除tt_subjectintegrity表
			 */
			sql = "drop table if exists tt_subjectintegrity　";
			ps = conn.prepareStatement(sql);
			ps.execute();
            DbUtil.close(ps);
            
			org.util.Debug.prtOut("1111A2=" + new ASFuntion().getCurrentTime());
			
			if(bool){//有修改过完整性对照，需要重新设置
				//开始进行默认的往来帐设置
				new com.matech.audit.service.usersubject.ComeService(conn).AssistCode(dpID);	
				
				org.util.Debug.prtOut("A3=" + new ASFuntion().getCurrentTime());
				
				//更新这套帐的多科目挂账结果 
				new com.matech.audit.service.usersubject.UserSubjectService(conn).updateOriParent(this.pkgid);
				
				//更新结转凭证
				sql = "select * from  c_accpackage where customerid=" + dpID + " order by AccPackageID ";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				SubjectTypeService sts = new SubjectTypeService(conn);
				while(rs.next()){
					String apkID1 = rs.getString("AccPackageID");
					sts.autosetEntryCarrydownProperty(apkID1);
				}
				rs.close();
				ps.close();
			}
            
		} catch (Exception e) {
			System.out.println("出错的SQL："+sql);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
            DbUtil.close(ps);
		}
	}
	
	
	/**
	 * 生成9000公式，和保存9000公式的临时表
	 * @param acc
	 * @param projectid
	 * @param vocationid
	 * @param tempTab
	 */
	public void setSql(String acc,String projectid,String vocationid,String tempTab,String year){
		Statement st = null;
		ResultSet rs = null;
		String sql = "";
		try {
			st = conn.createStatement();
			
			String customerid=acc.substring(0,6);
			
			Project project = new ProjectService(conn).getProjectById(projectid);
	        
	        if("".equals(year.trim())){
	        	year = "-1";
	        }
	        
	        String begin = "".equals(project.getAuditTimeBegin()) ? "0" : project.getAuditTimeBegin().substring(0,4);
			String end = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(0,4);
			String bMonth = "".equals(project.getAuditTimeBegin()) ? "0" : project.getAuditTimeBegin().substring(5,7);
			String eMonth = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(5,7);
			
			String acc_1 = customerid + begin;
			String acc_2 = customerid + end;
			
			
			
			String strStartYearMonth="",strEndYearMonth="";
			strStartYearMonth = String.valueOf((Integer.parseInt(begin))*12+Integer.parseInt(bMonth));
			
			strEndYearMonth = String.valueOf(Integer.parseInt(end)*12+Integer.parseInt(eMonth));
			
			String strWhere2=" a.subjectid like '5%' ";
			if (new SubjectTypeService(conn).autoJudgeVocation(acc)==59){
				strWhere2=" a.subjectid like '6%' ";
			}
			
			/**
			 *	根据标准科目求 损益类科目
			 */
//			String strWhere1 = "";
//			String strSql = "select group_concat(distinct \"'\",a.subjectid,\"'\")  from ( " +
//				"	select * from c_account a where SubYearMonth * 12  +SubMonth >= "+strStartYearMonth+" and SubYearMonth * 12  +SubMonth <= "+strEndYearMonth+"  and SubMonth =1" +
//				") a ,(" +
//				"	select b.* from k_standsubject b ,(select vocationid from k_customer where departid = "+acc.substring(0, 6)+" ) c " +
//				"	where  b.subjectid like if(c.vocationid=59,'6%','5%')  and level0 = 1 and b.vocationid = b.vocationid " +
//				") b " +
//				" where 1=1 " +
//				" and (a.subjectfullname2 = b.subjectfullname or a.subjectfullname2 like concat(b.subjectfullname,'/%'))";
//			rs = st.executeQuery(strSql);
//			while(rs.next()) {
//				strWhere1 = " a.subjectid in ( "+rs.getString(1)+") ";
//			}
//			rs.close();
			
//			String strWhere =" and ("+strWhere2 + " or " + strWhere1+") ";
			
			/**
			 * 科目对照
			 */
			sql = " select ifnull(b.tokenid,a.subjectfullname) as tokenid,a.subjectid,a.subjectname,a.subjectfullname,case substring(a.property,2,1) when 1 then 1 when 2 then -1 end  direction, \n" +
			" 	ifnull(remain,0) as remain,  \n" +
			" 	ifnull(DebitOcc,0) as DebitOcc, \n" +
			" 	ifnull(CreditOcc,0) as CreditOcc, \n" +
			" 	ifnull(Balance,0) as Balance, \n" +
			" 	IsLeaf,Level0, \n" +
			" 	ifnull(subjectfullname2,subjectfullname) as subjectfullname2, \n" +
			" 	ifnull(direction2,case substring(a.property,2,1) when 1 then 1 when 2 then -1 end) as direction2 \n" +
			" 	from c_accpkgsubject a left join ( \n" +
			" 		select tokenid,subjectid,accname,  \n" +
			" 		sum(if(SubYearMonth * 12 + SubMonth = "+strStartYearMonth+",DebitRemain + CreditRemain,0)) as remain, \n" +
			" 		sum(DebitOcc) as DebitOcc, \n" +
			" 		sum(CreditOcc) as CreditOcc, \n" +
			" 		sum(if(SubYearMonth * 12 + SubMonth = "+strEndYearMonth+",Balance,0)) as Balance, \n" +
			" 		IsLeaf1,Level1,subjectfullname2,direction2 \n" +
			" 		from c_account \n" +
			" 		where SubYearMonth * 12 + SubMonth >= "+strStartYearMonth+" \n" +
			" 		and SubYearMonth * 12 + SubMonth <= "+strEndYearMonth+" \n" +
			" 		group by subjectid \n" +
			" 		order by subjectid \n" +
			" 	) b on a.subjectid = b.subjectid \n" +
			" 	where AccPackageID >= "+acc_1+" and AccPackageID <= "+acc_2+" \n" +
//			" 	and level0<=2 \n" +
	
			" 	union  \n" +
	
			" 	select SubjectFullName as tokenid,subjectID,SubjectName,SubjectFullName,case substring(property,2,1) when 1 then 1 when 2 then -1 end  direction, \n" +
			" 	0,0,0,0, \n" +
			" 	isleaf,level0 ,SubjectFullName,case substring(a.property,2,1) when 1 then 1 when 2 then -1 end  \n" +
			" 	from z_usesubject a \n" +
			" 	where projectid = "+projectid+" \n" +
//			" 	and level0<=2 \n" +
	
			" 	order by subjectid"; 

			sql = "create table "+tempTab+" as " + sql ;
			st.execute(sql);
			
			sql = "delete t1 from "+tempTab+" as t1,"+tempTab+" as t2 where t1.isleaf=1 and t1.subjectid=t2.subjectid and t1.isleaf<>t2.isleaf ";
			st.execute(sql);
			
			
			sql="alter table "+tempTab+" " +
			"add column standkey varchar (200) DEFAULT ''," +
			"add column direction21 varchar (10) DEFAULT ''," +
			"add column level01 varchar (10) DEFAULT ''," +			
			"add column CarryOver decimal(15,2) DEFAULT '0.00'," +
			"add column yearCarryOver decimal(15,2) DEFAULT '0.00'" +
			" ";
			st.execute(sql);
			
//			sql = "update "+tempTab+" a ,(" +
//			
//			"	select (-1)*(ifnull( occ1,0) - ifnull(occ2,0))*a.direction2  as CarryOver   ,a.tokenid \n"+
//			"	from (    \n"+
//			"		select A.tokenid,a.direction2,sum(a.debitocc-a.creditocc) as occ1 \n"+    
//			"		from c_account a   ,"+tempTab+" b \n"+
//			"		where a.SubYearMonth * 12  + a.SubMonth >=  '"+strStartYearMonth+"'      \n"+
//			"		and a.SubYearMonth * 12  + a.SubMonth <=  '"+strEndYearMonth+"'     \n"+
//			"		and a.tokenid = b.tokenid \n"+
//			"		and a.dataname=0 \n" +
//			strWhere + 
//			"		group by a.tokenid    \n" +
//			
//			"	) a left join (    \n" +
//			"		select a.tid as tokenid ,sum(Dirction * OccurValue) as occ2 from (" +
//			
//			"		select  distinct a.*  , b.tokenid as tid   " +
//			"		from c_subjectentry a ,"+tempTab+" b " +
//			"		where substring(a.VchDate,1,4) * 12  +substring(a.VchDate,6,2) >=  '"+strStartYearMonth+"'      \n"+
//			"		and substring(a.VchDate,1,4) * 12  +substring(a.VchDate,6,2) <=  '"+strEndYearMonth+"'     \n"+
//			strWhere + 
//			"		and a.property like '%2%'" +
//			"		and (a.SubjectFullName1 = b.SubjectFullName or a.SubjectFullName1 like concat(b.SubjectFullName,'/%')) " +
//			
//			"		) a group by tid " +
//			"	) b on a.tokenid = b.tokenid  \n"+
//			
//			" ) b" +
//			" set a.CarryOver = b.CarryOver" +
//			" where a.tokenid = b.tokenid  ";
//			st.execute(sql);
			
			for(int i = -1; i >= Integer.parseInt(year) ; i--){
				String strStartYearMonth1="",strEndYearMonth1 = "",acc1="";
				strStartYearMonth1 = String.valueOf(Integer.parseInt(strStartYearMonth) + i * 12);
				strEndYearMonth1 = String.valueOf(Integer.parseInt(strEndYearMonth) + i * 12);
				acc1 = String.valueOf(Integer.parseInt(acc) + i);
				
				acc_1 = customerid + (Integer.parseInt(begin) - i);
				acc_2 = customerid + (Integer.parseInt(end) - i);
				
				sql = " select ifnull(b.tokenid,a.subjectfullname) as tokenid,a.subjectid,a.subjectname,a.subjectfullname,case substring(a.property,2,1) when 1 then 1 when 2 then -1 end  direction, \n" +
				" 	0 as remain,  \n" +
				" 	0 as DebitOcc, \n" +
				" 	0 as CreditOcc, \n" +
				" 	0 as Balance, \n" +
				" 	IsLeaf,Level0, \n" +
				" 	ifnull(subjectfullname2,SubjectFullName) as subjectfullname2, \n" +
				" 	ifnull(direction2,case substring(a.property,2,1) when 1 then 1 when 2 then -1 end) as direction2 \n" +
				" 	from c_accpkgsubject a left join ( \n" +
				" 		select tokenid,subjectid,accname,  \n" +
				" 		sum(if(SubYearMonth * 12 + SubMonth = "+strStartYearMonth1+",DebitRemain + CreditRemain,0)) as remain, \n" +
				" 		sum(DebitOcc) as DebitOcc, \n" +
				" 		sum(CreditOcc) as CreditOcc, \n" +
				" 		sum(if(SubYearMonth * 12 + SubMonth = "+strEndYearMonth1+",Balance,0)) as Balance, \n" +
				" 		IsLeaf1,Level1,subjectfullname2,direction2 \n" +
				" 		from c_account \n" +
				" 		where SubYearMonth * 12 + SubMonth >= "+strStartYearMonth1+" \n" +
				" 		and SubYearMonth * 12 + SubMonth <= "+strEndYearMonth1+" \n" +
				" 		group by subjectid \n" +
				" 		order by subjectid \n" +
				" 	) b on a.subjectid = b.subjectid \n" +
				" 	where AccPackageID >= "+acc_1+" and AccPackageID <= "+acc_2+" \n" +
//				" 	and level0<=2 \n" +
		
				" 	union  \n" +
		
				" 	select SubjectFullName as tokenid,subjectID,SubjectName,SubjectFullName,case substring(property,2,1) when 1 then 1 when 2 then -1 end  direction, \n" +
				" 	0,0,0,0, \n" +
				" 	isleaf,level0 ,SubjectFullName,case substring(a.property,2,1) when 1 then 1 when 2 then -1 end \n" +
				" 	from z_usesubject a \n" +
				" 	where projectid = "+projectid+" \n" +
//				" 	and level0<=2 \n" +
		
				" 	order by subjectid"; 
				
				sql = "insert into "+tempTab+" (tokenid,subjectid,subjectname,subjectfullname,direction, \n" +
				" 	remain, DebitOcc, CreditOcc,Balance, \n" +
				" 	IsLeaf,Level0, \n" +
				" 	subjectfullname2, \n" +
				" 	direction2 )" +
				" select b.* from "+tempTab+" a right join (" + sql + " ) b on a.tokenid = b.tokenid " +
				" where a.tokenid is null ";
				
				st.execute(sql);
				
//				sql = "delete t1 from "+tempTab+" as t1,"+tempTab+" as t2 where t1.isleaf=1 and t1.subjectid=t2.subjectid and t1.isleaf<>t2.isleaf ";
//				st.execute(sql);
				
//				strSql = "select group_concat(distinct \"'\",a.subjectid,\"'\")  from ( " +
//				"	select * from c_account a where SubYearMonth * 12  +SubMonth >= "+strStartYearMonth1+" and SubYearMonth * 12  +SubMonth <= "+strEndYearMonth1+"  and SubMonth =1" +
//				") a ,(" +
//				"	select b.* from k_standsubject b ,(select vocationid from k_customer where departid = "+acc.substring(0, 6)+" ) c " +
//				"	where  b.subjectid like if(c.vocationid=59,'6%','5%')  and level0 = 1 and b.vocationid = b.vocationid " +
//				") b " +
//				" where 1=1 " +
//				" and (a.subjectfullname2 = b.subjectfullname or a.subjectfullname2 like concat(b.subjectfullname,'/%'))";
//				rs = st.executeQuery(strSql);
//				while(rs.next()) {
//					strWhere1 = " a.subjectid in ( "+rs.getString(1)+") ";
//				}
//				rs.close();
//				
//				strWhere =" and ("+strWhere2 + " or " + strWhere1+") ";
//				
//				sql = "update "+tempTab+" a ,(" +
//				
//				"	select (-1)*(ifnull( occ1,0) - ifnull(occ2,0))*a.direction2  as CarryOver   ,a.tokenid \n"+
//				"	from (    \n"+
//				"		select A.tokenid,a.direction2,sum(a.debitocc-a.creditocc) as occ1 \n"+    
//				"		from c_account a   ,"+tempTab+" b \n"+
//				"		where a.SubYearMonth * 12  + a.SubMonth >=  '"+strStartYearMonth1+"'      \n"+
//				"		and a.SubYearMonth * 12  + a.SubMonth <=  '"+strEndYearMonth1+"'     \n"+
//				"		and a.tokenid = b.tokenid \n"+
//				"		and a.dataname=0 \n" +
//				strWhere + 
//				"		group by a.tokenid    \n" +
//				
//				"	) a left join (    \n" +
//				"		select a.tid as tokenid ,sum(Dirction * OccurValue) as occ2 from (" +
//				
//				"		select  distinct a.*  , b.tokenid as tid   " +
//				"		from c_subjectentry a ,"+tempTab+" b " +
//				"		where substring(a.VchDate,1,4) * 12  +substring(a.VchDate,6,2) >=  '"+strStartYearMonth1+"'      \n"+
//				"		and substring(a.VchDate,1,4) * 12  +substring(a.VchDate,6,2) <=  '"+strEndYearMonth1+"'     \n"+
//				strWhere + 
//				"		and a.property like '%2%'" +
//				"		and (a.SubjectFullName1 = b.SubjectFullName or a.SubjectFullName1 like concat(b.SubjectFullName,'/%')) " +
//				
//				"		) a group by tid " +
//				"	) b on a.tokenid = b.tokenid  \n"+
//				
//				" ) b" +
//				" set a.yearCarryOver = b.CarryOver" +
//				" where a.tokenid = b.tokenid  ";
//				
//				st.execute(sql);
			}
			
			
			sql = " update "+tempTab+" a left join z_keyresult b on a.subjectfullname = userkey " +
			" set a.standkey = b.standkey," +
			" a.direction21 = (case b.property when 2 then -1 else b.property end)," +
			" a.level01 = b.level0";
			
			st.execute(sql);
			
			sql = "update " + tempTab + " a,(" +
			"	select * from " + tempTab + " where ifnull(standkey,'') <> '' " +
			" ) b " +
			" set " +
			" a.standkey = b.standkey ," +
			" a.direction21 = b.direction21," +
			" a.subjectfullname2 = if(a.subjectfullname2 = '',case when b.level01=1 then concat(b.standkey,substring(a.subjectfullname,CHAR_LENGTH(b.subjectfullname)+1)) when b.level01=2 then concat(b.standkey,'/',a.subjectfullname) else a.subjectfullname end , a.subjectfullname2), " +
			" a.direction2 = b.direction21 " +
			
			" where (a.subjectfullname = b.subjectfullname or a.subjectfullname like concat(b.subjectfullname,'/%'))" +
			" and ifnull(a.standkey,'') = '' " ;
			st.execute(sql);
			
			//去掉除坏账准备以外的二级标准科目，变成1级标准科目
//			sql= "update "+tempTable
//				+"\n set standkey=if(INSTR(standkey,'/')>0 ,LEFT(standkey,INSTR(standkey,'/')-1),standkey) \n"
//				+"where standkey not like '坏账准备%'";
//			st.execute(sql);
			
			//坏账准备补齐；
//			String[] standsubjectnames={"坏账准备/应收账款","坏账准备/预付账款","坏账准备/其他应收款","坏账准备/长期应收款","坏账准备/应收票据"};
//			for (int i=0;i<standsubjectnames.length;i++){
//				sql="insert into "+tempTab +" \n"
//					+"			(tokenid,subjectid,subjectname,subjectfullname,standkey,level0,isleaf) \n"
//					+"			select subjectname,subjectid,subjectname,subjectname,'"+standsubjectnames[i]+"',2,1  \n"
//					+"			from "+tempTab +" a \n"
//					+"			where standkey='坏账准备' \n"
//					+"			and not exists \n"
//					+"			( \n"
//					+"			select 1 from "+tempTab +" b \n"
//					+"			where standkey='"+standsubjectnames[i]+"' \n"
//					+"			) \n";
//				st.execute(sql);
////				System.out.println("\n出错的SQL："+ sql);
//			}
			
			
			//最后的结果输出
			sql = "drop table if exists tt_subjectintegrity ";
			st.execute(sql);
			
			sql = "create table tt_subjectintegrity as " +  
			
			" select b.groupname as groupname1 ," +
			" if(a.level0=1 and a.isleaf=0,'无需对照',groupname) as groupname," +
			" a.*,concat(REPEAT('  ',a.level0 - 1), a.subjectname) as AccName," +
//			" if(a.isleaf=1,if(INSTR(standkey,'/')>0 ,LEFT(standkey,INSTR(standkey,'/')-1),standkey),if(a.level0=1,'无需对照',if(INSTR(standkey,'/')>0 ,LEFT(standkey,INSTR(standkey,'/')-1),standkey)) ) as standkey1 " +
			" if(a.level0=1,if(a.isleaf=1,standkey,'无需对照'),standkey) as standkey1 " +
			" from " + tempTab + " a " +
			" left join k_standsubject b " +
			" on b.VocationID = '"+vocationid+"' " +
			" and b.level0 = 1 " +
			" and (a.standkey = b.SubjectFullName or a.standkey like concat(b.SubjectFullName,'/%')) " +
			" where 1=1 " +
			" order by a.subjectid,standkey ";
			st.execute(sql);
			
			
			
		} catch (Exception e) {
			System.out.println("出错的SQL："+sql);
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
		}
	}
	
	
	public static void main(String[] args) {
		System.out.println("1002402008".substring(6));
	}
}
