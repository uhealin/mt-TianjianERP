package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;


/**
 *	显示负值重分类 TB模式 
 */
public class _9003_0 extends AbstractAreaFunction {

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

			this.tempTable = "tt_"+DELUnid.getCharUnid();	//临时表
			
			int year = Integer.parseInt(acc.substring(6));
			
			String Summary = CHF.showNull(request.getParameter("Summary")); //摘要
			String sql1 = "					and a.summary like '%负值重分类%' \n";
			if(!"".equals(Summary) ){
				if("all".equals(Summary)){
					sql1 = "";
				}else{
					sql1 = "					and a.summary like '%"+Summary+"%' \n";
				}
			}
			
			//序号、摘要、一级标准科目、科目编号|科目名称、核算编号|核算名称、类型、上年借、上年贷、本年借、本年贷
			
			sql = "create table "+tempTable+" select * from ( \n"+ 
			"		select  \n"+
			"		'0' as AutoId,'0' as ProjectID,'' as VoucherID,'' as VchDate,'0' as Serail,'' as Summary,'' as SubjectID,'' as assitemid, '' as property,\n"+
			"		'0' as aDirction,'0' as aOccurValue,'0' as aCurrRate,'0' as aCurrValue,'' as aCurrency,'0' as aDebit,'0' as aCredit, \n"+
			"		'0' as bDirction,'0' as bOccurValue,'0' as bCurrRate,'0' as bCurrValue,'' as bCurrency,'0' as bDebit,'0' as bCredit,  \n"+
			"		'' as SidorName,'' as AidorName,'' as subjectname ,'' as assitemname,'' as subjectfullname2, '0' as direction2,'' as fullname,'' as name, \n"+
			"		'' as typeProperty,@tt9003i:=0 as id,@tt9003v:=0 as prevoucherid \n"+
			"	) a where 1=2   \n"+

			"	union  \n"+

			"	select * , \n" +
			"	if( property like '3%','调整', if( property like '4%' , '重分类' ,'')) as typeProperty, "+
			"	if(@tt9003v != voucherid,@tt9003i:=@tt9003i+1,@tt9003i) as id,@tt9003v:=voucherid as prevoucherid \n"+
			"	from ( \n"+
			"		select a.*,b.subjectfullname2,b.direction2,  " +
			"		if(a.assitemname is null,b.SubjectFullName1,concat(b.SubjectFullName1,'/',a.assitemname)) as fullname," +
			"		ifnull(a.assitemname,a.subjectname) as name " +
			"		from ( \n"+
			"			select a.*,concat(c.subjectid,'|',c.SubjectName) as SidorName,ifnull(concat(d.AssItemID,'|',d.AssItemName),'') as AidorName," +
			"			subjectname,assitemname \n"+
			"			from ( \n"+
					
			"				select  \n"+
			"				AutoId, \n"+
			"				ProjectID, \n"+
			"				VoucherID, \n"+
			"				VchDate, \n"+
			"				Serail, \n"+
			"				Summary, \n"+
			"				SubjectID, \n"+
			"				assitemid,  \n" +
			
			"				property, \n"+
						
			"				a.Dirction as aDirction, \n"+
			"				a.OccurValue as aOccurValue, \n"+
			"				a.CurrRate as aCurrRate, \n"+
			"				a.CurrValue as aCurrValue, \n"+
			"				a.Currency as aCurrency, \n"+
			"				if(a.Dirction=1,a.OccurValue,'') as aDebit, \n"+
			"				if(a.Dirction=-1,a.OccurValue,'') as aCredit, \n"+
			
			"				'0' as bDirction, \n"+
			"				'0' as bOccurValue, \n"+
			"				'0' as bCurrRate, \n"+
			"				'0' as bCurrValue, \n"+
			"				'0' as bCurrency, \n"+
			"				'0' as bDebit, \n"+
			"				'0' as bCredit \n"+
			
			"				from ( \n"+
			"					select a.*,assitemid \n"+
			"					from z_subjectentryrectify  a \n"+ 
			"					left join z_assitementryrectify b on b.projectid = "+projectid+" and a.autoid = b.entryid \n"+
			"					where a.projectid = "+projectid+" \n"+
			sql1+
			"					and a.vchdate like '"+year+"%' \n"+
			"				) a  \n"+
			"				union  \n"+
						
			"				select  \n"+
			"				AutoId, \n"+
			"				ProjectID, \n"+
			"				VoucherID, \n"+
			"				VchDate, \n"+
			"				Serail, \n"+
			"				Summary, \n"+
			"				SubjectID, \n"+
			"				assitemid,  \n" +
			
			"				property, \n"+
			
			"				'0' as aDirction, \n"+
			"				'0' as aOccurValue, \n"+
			"				'0' as aCurrRate, \n"+
			"				'0' as aCurrValue, \n"+
			"				'0' as aCurrency, \n"+
			"				'0' as aDebit, \n"+
			"				'0' as aCredit, \n"+
					
			"				b.Dirction as bDirction, \n"+
			"				b.OccurValue as bOccurValue, \n"+
			"				b.CurrRate as bCurrRate, \n"+
			"				b.CurrValue as bCurrValue, \n"+
			"				b.Currency as bCurrency, \n"+
			"				if(b.Dirction=1,b.OccurValue,'') as bDebit, \n"+
			"				if(b.Dirction=-1,b.OccurValue,'') as bCredit \n"+
						
			"				from ( \n"+
			"					select a.*,assitemid \n"+
			"					from z_subjectentryrectify  a \n"+ 
			"					left join z_assitementryrectify b on b.projectid = "+projectid+" and a.autoid = b.entryid \n"+
			"					where a.projectid = "+projectid+" \n"+
			sql1+
			"					and a.vchdate like '"+(year-1)+"%' \n"+
			"				) b \n"+
			"			) a  \n"+
			"			inner join z_accountrectify c on c.projectid = "+projectid+" and a.subjectid = c.subjectid \n"+
			"			left join z_assitemaccrectify d on d.projectid = "+projectid+" and a.subjectid = d.subjectid and a.assitemid = d.assitemid \n"+
			"			order by a.autoid \n"+

			"		) a,( \n"+
			"			select a.subjectID pid,a.subjectfullname2,a.direction2,b.* from ( \n"+
			"				select SubjectID, AccName,SubjectFullName1,subjectfullname2,direction2 from c_account where accpackageid ="+acc+" and submonth = 1 and level1 =1 \n"+
			"				union  \n"+
			"				select subjectID,SubjectName,SubjectFullName,SubjectFullName,case substring(property,2,1) when 2 then -1 else 1 end as direction2 from z_usesubject where projectID="+projectid+" and level0 = 1 \n"+
			"			) a,( \n"+
			"				select SubjectID, AccName,SubjectFullName1 from c_account where accpackageid ="+acc+" and submonth = 1 \n"+
			"				union  \n"+
			"				select subjectID,SubjectName,SubjectFullName from z_usesubject where projectID="+projectid+" \n"+
			"			) b  \n"+
			"			where (b.SubjectFullName1 = a.SubjectFullName1 or b.SubjectFullName1 like concat(a.SubjectFullName1,'/%')) \n"+
			"			order by b.SubjectID \n"+
			"		) b where a.subjectid = b.subjectid \n"+
			"		order by a.autoid \n"+
			"	) a  \n";
			System.out.println(sql);
			st.execute(sql);
			
			sql="insert into "+this.tempTable+" (id,subjectfullname2) \n"
				+"select distinct id,'' from "+this.tempTable;
			st.execute(sql);
			
			
			sql="select * from " + this.tempTable +" order by abs(id),subjectfullname2 desc";
			rs = st.executeQuery(sql);
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
