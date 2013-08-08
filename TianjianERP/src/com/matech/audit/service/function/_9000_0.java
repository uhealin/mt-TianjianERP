package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.audit.service.customer.CustomerService;
import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.project.model.Project;
import com.matech.audit.service.subjectType.SubjectTypeService;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.sys.UTILSysProperty;


public class _9000_0 extends AbstractAreaFunction {
	
	/** 
	 * 科目对照
	 * 加入结转数
	 * 公式：
	 * =取列公式覆盖(9000,"结转数","CarryOver")
	 * =取列公式覆盖(9000,"上年结转数","yearCarryOver")
	 */
	public ResultSet process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {
		ASFuntion CHF=new ASFuntion();
		Statement st = null;
		ResultSet rs = null;
		String sql = "";
		try {
			st = conn.createStatement();
			
			String acc = CHF.showNull((String) args.get("curAccPackageID"));		//账套编号
	        String projectid = CHF.showNull((String) args.get("curProjectid"));		//项目编号
	        
	        String accid = CHF.showNull(request.getParameter("accid")); //自定义账套编号
	        
	        String customerid=acc.substring(0,6);
	        
	        String vocationid = new CustomerService(conn).getCustomer(customerid).getVocationId();		//会计制度
			
//	        String display = CHF.showNull((String)args.get("显示内容")); //科目对照、科目体系、核算体系
	        
	        Project project = new ProjectService(conn).getProjectById(projectid);
	        
	        String year = CHF.showNull((String) args.get("year"));
	        if("".equals(year.trim())){
	        	year = "-1";
	        }
	        
	        String StartMonth = CHF.showNull(request.getParameter("StartMonth")); //开始月
	        String EndMonth = CHF.showNull(request.getParameter("EndMonth"));//结束月
	        
	        String opt = CHF.showNull(request.getParameter("opt"));//显示1、2级还是显示1、末级
//	        opt = "1";
	        
	        String begin = "".equals(project.getAuditTimeBegin()) ? "0" : project.getAuditTimeBegin().substring(0,4);
			String end = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(0,4);
			
			String bMonth = "".equals(project.getAuditTimeBegin()) ? "0" : project.getAuditTimeBegin().substring(5,7);
			String eMonth = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(5,7);
			
			if(!"".equals(accid)){
				acc = accid;
				begin = accid.substring(6); 
				end = accid.substring(6);
				bMonth = "01";
				eMonth = "12";
			}
//			if(!"".equals(StartMonth)){
//				bMonth = StartMonth;
//			}
//			
//			if(!"".equals(EndMonth)){
//				eMonth = EndMonth; 
//			}
			
			
			String acc_1 = customerid + begin;
			String acc_2 = customerid + end;
			
			
			
			String strStartYearMonth="",strEndYearMonth="";
			strStartYearMonth = String.valueOf((Integer.parseInt(begin))*12+Integer.parseInt(bMonth));
			
			strEndYearMonth = String.valueOf(Integer.parseInt(end)*12+Integer.parseInt(eMonth));
			
			this.tempTable = "tt_"+DELUnid.getCharUnid();
			//this.tempTable = ""+this.tempTable +"";
			//this.tempTable="tt_qwh";
			
			String strWhere2=" a.subjectid like '5%' ";
			if (new SubjectTypeService(conn).autoJudgeVocation(acc)==59){
				strWhere2=" a.subjectid like '6%' ";
			}
			
			String sqlOpt = " 	and level0<=2 \n";
			if("1".equals(opt)){
				sqlOpt = " 	and (level0=1 or isleaf = 1) \n";
			}
			/**
			 *	根据标准科目求 损益类科目
			 */
			String strWhere1 = "";
			String strSql = "select group_concat(distinct \"'\",a.subjectid,\"'\")  from ( " +
				"	select * from c_account a where SubYearMonth * 12  +SubMonth >= "+strStartYearMonth+" and SubYearMonth * 12  +SubMonth <= "+strEndYearMonth+"  and SubMonth =1" +
				") a ,(" +
				"	select b.* from k_standsubject b ,(select vocationid from k_customer where departid = "+acc.substring(0, 6)+" ) c " +
				"	where  b.subjectid like if(c.vocationid=59,'6%','5%')  and level0 = 1 and b.vocationid = b.vocationid " +
				") b " +
				" where 1=1 " +
				" and (a.subjectfullname2 = b.subjectfullname or a.subjectfullname2 like concat(b.subjectfullname,'/%'))";
			rs = st.executeQuery(strSql);
			//这里用错了吧？应该是if,而不是while
			//while(rs.next()) {
			if(rs.next()) {
				strWhere1 = " a.subjectid in ( "+rs.getString(1)+") ";
			}
			rs.close();
			//System.out.println("根据标准科目求 损益类科目=\n"+strSql);
			
			String strWhere =" and ("+strWhere2 + " or " + strWhere1+") ";
			
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
			sqlOpt +
	
			" 	union  \n" +
	
			" 	select SubjectFullName as tokenid,subjectID,SubjectName,SubjectFullName,case substring(property,2,1) when 1 then 1 when 2 then -1 end  direction, \n" +
			" 	0,0,0,0, \n" +
			" 	isleaf,level0 ,SubjectFullName,case substring(a.property,2,1) when 1 then 1 when 2 then -1 end  \n" +
			" 	from z_usesubject a \n" +
			" 	where projectid = "+projectid+" \n" +
			sqlOpt +
	
			" 	order by subjectid"; 

			System.out.println("科目对照\n"+sql);
			sql = "create table "+tempTable+" as " + sql ;
			st.execute(sql);
			
			sql = "delete t1 from "+this.tempTable+" as t1,"+this.tempTable+" as t2 where t1.isleaf=1 and t1.subjectid=t2.subjectid and t1.isleaf<>t2.isleaf ";
			st.execute(sql);
			
			
			
			
			
			sql="alter table "+this.tempTable+" " +
			"add column standkey varchar (200) DEFAULT ''," +
			"add column direction21 varchar (10) DEFAULT ''," +
			"add column level01 varchar (10) DEFAULT ''," +			
			"add column CarryOver decimal(15,2) DEFAULT '0.00'," +
			"add column yearCarryOver decimal(15,2) DEFAULT '0.00'" +
			" ";
			st.execute(sql);
			
			//加索引
			sql="alter table "+this.tempTable+" add index `tokenid` (`tokenid`)";
			st.execute(sql);
			sql="alter table "+this.tempTable+" add index `subjectid` (`subjectid`)";
			st.execute(sql);
			
			sql = "update "+this.tempTable+" a ,(" +
			
			"	select (ifnull( occ1,0) - ifnull(occ2,0))  as CarryOver   ,a.tokenid \n"+
			"	from (    \n"+
			"		select A.tokenid,a.direction2,sum(a.debitocc-a.creditocc) as occ1 \n"+    
			"		from c_account a   ,"+this.tempTable+" b \n"+
			"		where a.SubYearMonth * 12  + a.SubMonth >=  '"+strStartYearMonth+"'      \n"+
			"		and a.SubYearMonth * 12  + a.SubMonth <=  '"+strEndYearMonth+"'     \n"+
			"		and a.tokenid = b.tokenid \n"+
			"		and a.dataname=0 \n" +
			strWhere + 
			"		group by a.tokenid    \n" +
			
			"	) a left join (    \n" +
			"		select a.tid as tokenid ,sum(Dirction * OccurValue) as occ2 from (" +
			
			"		select  distinct a.*  , b.tokenid as tid   " +
			"		from c_subjectentry a ,"+this.tempTable+" b " +
			"		where substring(a.VchDate,1,4) * 12  +substring(a.VchDate,6,2) >=  '"+strStartYearMonth+"'      \n"+
			"		and substring(a.VchDate,1,4) * 12  +substring(a.VchDate,6,2) <=  '"+strEndYearMonth+"'     \n"+
			strWhere + 
			"		and a.property like '%2%'" +
			"		and (a.SubjectFullName1 = b.SubjectFullName or a.SubjectFullName1 like concat(b.SubjectFullName,'/%')) " +
			
			"		) a group by tid " +
			"	) b on a.tokenid = b.tokenid  \n"+
			
			" ) b" +
			" set a.CarryOver = b.CarryOver" +
			" where a.tokenid = b.tokenid  ";
			st.execute(sql);
			
			for(int i = -1; i >= Integer.parseInt(year) ; i--){
				String strStartYearMonth1="",strEndYearMonth1 = "",acc1="";
				strStartYearMonth1 = String.valueOf(Integer.parseInt(strStartYearMonth) + i * 12);
				strEndYearMonth1 = String.valueOf(Integer.parseInt(strStartYearMonth) + i * 1);
				acc1 = String.valueOf(Integer.parseInt(acc) + i);
				
				acc_1 = customerid + (Integer.parseInt(begin) + i);
				acc_2 = customerid + (Integer.parseInt(end) + i);
				
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
				sqlOpt +
		
				" 	union  \n" +
		
				" 	select SubjectFullName as tokenid,subjectID,SubjectName,SubjectFullName,case substring(property,2,1) when 1 then 1 when 2 then -1 end  direction, \n" +
				" 	0,0,0,0, \n" +
				" 	isleaf,level0 ,SubjectFullName,case substring(a.property,2,1) when 1 then 1 when 2 then -1 end \n" +
				" 	from z_usesubject a \n" +
				" 	where projectid = "+projectid+" \n" +
				sqlOpt +
		
				" 	order by subjectid"; 
				
				sql = "insert into "+tempTable+" (tokenid,subjectid,subjectname,subjectfullname,direction, \n" +
				" 	remain, DebitOcc, CreditOcc,Balance, \n" +
				" 	IsLeaf,Level0, \n" +
				" 	subjectfullname2, \n" +
				" 	direction2 )" +
				" select b.* from "+tempTable+" a right join (" + sql + " ) b on a.tokenid = b.tokenid and a.subjectid=b.subjectid " +
				" where a.tokenid is null ";
				
				st.execute(sql);
				
//				sql = "delete t1 from "+this.tempTable+" as t1,"+this.tempTable+" as t2 where t1.isleaf=1 and t1.subjectid=t2.subjectid and t1.isleaf<>t2.isleaf ";
//				st.execute(sql);
				
				
				strSql = "select group_concat(distinct \"'\",a.subjectid,\"'\")  from ( " +
				"	select * from c_account a where SubYearMonth * 12  +SubMonth >= "+strStartYearMonth1+" and SubYearMonth * 12  +SubMonth <= "+strEndYearMonth1+"  and SubMonth =1" +
				") a ,(" +
				"	select b.* from k_standsubject b ,(select vocationid from k_customer where departid = "+acc.substring(0, 6)+" ) c " +
				"	where  b.subjectid like if(c.vocationid=59,'6%','5%')  and level0 = 1 and b.vocationid = b.vocationid " +
				") b " +
				" where 1=1 " +
				" and (a.subjectfullname2 = b.subjectfullname or a.subjectfullname2 like concat(b.subjectfullname,'/%'))";
				rs = st.executeQuery(strSql);
				if(rs.next()) {
					strWhere1 = " a.subjectid in ( "+rs.getString(1)+") ";
				}
				rs.close();
				System.out.println("科目对照=\n"+strSql);
				
				
				strWhere =" and ("+strWhere2 + " or " + strWhere1+") ";
				
				sql = "update "+this.tempTable+" a ,(" +
				
				"	select a.subjectid,(ifnull( occ1,0) - ifnull(occ2,0))  as CarryOver   ,a.tokenid \n"+
				"	from (    \n"+
				"		select a.subjectid,A.tokenid,a.direction2,sum(a.debitocc-a.creditocc) as occ1 \n"+    
				"		from c_account a   ,"+this.tempTable+" b \n"+
				"		where a.SubYearMonth * 12  + a.SubMonth >=  '"+strStartYearMonth1+"'      \n"+
				"		and a.SubYearMonth * 12  + a.SubMonth <=  '"+strEndYearMonth1+"'     \n"+
				"		and a.tokenid = b.tokenid \n"+
				"		and a.dataname=0 \n" +
				strWhere + 
				"		group by a.tokenid    \n" +
				
				"	) a left join (    \n" +
				"		select a.tid as tokenid ,sum(Dirction * OccurValue) as occ2 from (" +
				
				"		select  distinct a.*  , b.tokenid as tid   " +
				"		from c_subjectentry a ,"+this.tempTable+" b " +
				"		where substring(a.VchDate,1,4) * 12  +substring(a.VchDate,6,2) >=  '"+strStartYearMonth1+"'      \n"+
				"		and substring(a.VchDate,1,4) * 12  +substring(a.VchDate,6,2) <=  '"+strEndYearMonth1+"'     \n"+
				strWhere + 
				"		and a.property like '%2%'" +
				"		and (a.SubjectFullName1 = b.SubjectFullName or a.SubjectFullName1 like concat(b.SubjectFullName,'/%')) " +
				
				"		) a group by tid " +
				"	) b on a.tokenid = b.tokenid  \n"+
				
				" ) b" +
				" set a.yearCarryOver = b.CarryOver" + 
				//这里遇到南京的帐，发现 B是 其他业务收入/其他/其他 60510701 ，A是 605107，结果无法赋值，先这么修改，等待小彭确认
				//" where a.tokenid = b.tokenid and a.subjectid = b.subjectid ";
				" where a.tokenid = b.tokenid and b.subjectid  like concat(a.subjectid,'%') ";
				
				
				System.out.println("最终\n"+sql);
				st.execute(sql);
			}
			
			
			sql = " update "+tempTable+" a left join z_keyresult b on a.subjectfullname = userkey " +
			" set a.standkey = b.standkey," +
			" a.direction21 = (case b.property when 2 then -1 else b.property end)," +
			" a.level01 = b.level0";
			
			st.execute(sql);
			
			sql = "update " + tempTable + " a,(" +
			"	select * from " + tempTable + " where ifnull(standkey,'') <> '' " +
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
//			sql= "update "+this.tempTable
//				+"\n set standkey=if(INSTR(standkey,'/')>0 ,LEFT(standkey,INSTR(standkey,'/')-1),standkey) \n"
//				+"where standkey not like '坏账准备%'";
//			st.execute(sql);
			
			//坏账准备补齐；
			String[] standsubjectnames={"坏账准备/应收账款","坏账准备/预付账款","坏账准备/其他应收款","坏账准备/长期应收款","坏账准备/应收票据"};
			for (int i=0;i<standsubjectnames.length;i++){
				sql="insert into "+this.tempTable +" \n"
					+"			(tokenid,subjectid,subjectname,subjectfullname,standkey,level0,isleaf) \n"
					+"			select subjectname,subjectid,subjectname,subjectname,'"+standsubjectnames[i]+"',2,1  \n"
					+"			from "+this.tempTable +" a \n"
					+"			where subjectfullname2='坏账准备' \n"
					+"			and not exists \n"
					+"			( \n"
					+"			select 1 from "+this.tempTable +" b \n"
					+"			where standkey='"+standsubjectnames[i]+"' \n"
					+"			) \n";
				st.execute(sql);
//				System.out.println("\n出错的SQL："+ sql);
			}
			
			
			sql="update "+this.tempTable +" set standkey = null where standkey = '坏账准备' and subjectfullname2 <> '坏账准备'";
			st.execute(sql);
			
			if("是".equals(String.valueOf(UTILSysProperty.SysProperty.get("利润分配无需对照")))) {
				sql="update "+this.tempTable +" set standkey = '无需对照' where standkey like '利润分配%'";
				st.execute(sql);
				System.out.println("qwh:sql="+sql);
			}
			
			
			//特别代码，专门适应结转科目：上年一级就是末级，当年却分了级的这种特殊情况；
			//这种情况下，插入一个假的科目，专门用于调平上年数
			sql="insert into "+this.tempTable +" \n"
				+"select concat(a.tokenid,'/上年无下级') as tokenid,concat(a.subjectid,'xxx') as subjectid, \n"
				+"	'上年无下级' as subjectname,concat(a.subjectfullname,'/上年无下级') as subjectfullname, a.direction, \n"
				+"	0,0,0,0,1 as isleaf,2 as level0, \n"
				+"	concat(a.subjectfullname2,'/上年无下级') as subjectfullname2,a.direction2,a.standkey,a.direction21,2,0,a.yearcarryover-yearcarryover11 \n"
				+"from ( \n"
				+"	select a.*,ifnull(b.yearcarryover,0) yearcarryover11 \n"
				+"	from "+this.tempTable +" a left join ( \n"
				+"	select a.subjectid,a.subjectname,sum(b.yearcarryover)  as yearcarryover \n"
				+"	from "+this.tempTable +" a,"+this.tempTable +" b \n"
				+"	where a.level0=1 and b.level0=2 \n"
				+"	and b.subjectfullname2 like concat(a.subjectfullname2,'/%') \n"
				+"	and b.subjectid  like concat(a.subjectid,'%') " //上年有科目，但科目编号不一样
				+"	group by a.subjectid,a.subjectname \n"
				+"	) b \n"
				+"on a.subjectid=b.subjectid \n"
				+"and a.subjectname=b.subjectname \n"
				+"where a.level0=1 and a.isleaf=0 \n"
				+") a where yearcarryover<>yearcarryover11";
			st.execute(sql);
			
			
			//最后的结果输出
			sql = "select distinct b.groupname as groupname1 ," +
			" if(a.level0=1 and a.isleaf=0,'无需对照',groupname) as groupname," +
			
			" a.*," +
			
			" concat(REPEAT('  ',a.level0 - 1), if(a.isleaf = 1,a.subjectfullname,a.subjectname)) as AccName," +
			
//			" if(a.isleaf=1,if(INSTR(standkey,'/')>0 ,LEFT(standkey,INSTR(standkey,'/')-1),standkey),if(a.level0=1,'无需对照',if(INSTR(standkey,'/')>0 ,LEFT(standkey,INSTR(standkey,'/')-1),standkey)) ) as standkey1 " +
			" if(a.level0=1,if(a.isleaf=1,standkey,'无需对照'),standkey) as standkey1, " +
			" if(a.isleaf=1,if(INSTR(standkey,'/')>0 ,LEFT(standkey,INSTR(standkey,'/')-1),standkey),if(a.level0=1,'无需对照',if(INSTR(standkey,'/')>0 ,LEFT(standkey,INSTR(standkey,'/')-1),standkey)) ) as standkey2 " +
			" from " + tempTable + " a " +
			" left join k_standsubject b " +
			" on b.VocationID = '"+vocationid+"' " +
			" and b.level0 = 1 " +
			" and (a.standkey = b.SubjectFullName or a.standkey like concat(b.SubjectFullName,'/%')) " +
			" where 1=1 and a.SubjectFullName2 is not null " +
			" order by a.subjectid,standkey ";
			System.out.println(sql);
			rs = st.executeQuery(sql);
			
			
			//System.out.println("tempTable="+tempTable);
			//tempTable = "";
			
			return rs;
		} catch (Exception e) {
			System.out.println("出错的SQL："+ sql);
			e.printStackTrace();
			DbUtil.close(rs);
			DbUtil.close(st);
			throw e;
		} 
		    
	}
	
	
}
