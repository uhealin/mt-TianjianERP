package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.project.model.Project;
import com.matech.audit.service.usersubject.SubjectAssitemService;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;


/**
 *	往来函证
 *	=取列公式插入(9004,"subjectid","subjectid","&SubjectName=应收账款")
 *	=取列公式覆盖(9004,"assitemid","assitemid","&SubjectName=应收账款")
 *	
 *	SubjectName 为标准科目 
 *
 *返回：
 * "是否多科目挂账", "opt" - 1为挂账科目 
 * "科目编号", "SubjectID"
 * "核算编号", "AssItemID"
 * "科目/核算名称", "accName"
 * "币种", "DataName"
 * "期初", "initBalance"
 * "借发生", "DebitTotalOcc"
 * "贷发生", "CreditTotalOcc"
 * "重分类值", "occ"
 * "方向", "direction"
 * "原币余额", "Balance"
 * "汇率", "exchangerate"
 * "本位币余额", "exrate"
 * "贵公司欠", "ggsq"
 * "欠贵公司", "qggs"
 *
 */
public class _9004_1 extends AbstractAreaFunction {

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
			String SubjectName = CHF.showNull((String)request.getParameter("SubjectName"));	//标准科目
			String SubjectID = "";
			if(!"".equals(SubjectName)){
				SubjectName = " and (a.Subjectfullname2 = '"+SubjectName+"' or a.Subjectfullname2 like '"+SubjectName+"/%')  ";
			}else{
				throw new Exception ("标准科目不能为空");
			}
			
			this.tempTable = "tt_"+DELUnid.getCharUnid();	//临时表
			
			Project project = new ProjectService(conn).getProjectById(projectid);
			
			String begin = "".equals(project.getAuditTimeBegin()) ? "0" : project.getAuditTimeBegin().substring(0,4);
			String end = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(0,4);
			String bMonth = "".equals(project.getAuditTimeBegin()) ? "0" : project.getAuditTimeBegin().substring(5,7);
			String eMonth = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(5,7);

			int bYear = Integer.parseInt(begin) * 12 + Integer.parseInt(bMonth);
			int eYear = Integer.parseInt(end) * 12 + Integer.parseInt(eMonth);

			sql = "select ifnull(group_concat(\"'\",subjectid,\"'\") ,'') as subjectID " +
			" from c_account a " +
			" where  accpackageid='"+acc+"' and SubMonth = "+eMonth+"  and isleaf1=1 " + SubjectName ; 
			rs = st.executeQuery(sql);
			if(rs.next()){
				SubjectID = rs.getString("SubjectID");
				if(!"".equals(SubjectID)){
					SubjectID = " and a.SubjectID in ("+SubjectID+") ";
				}
			}
			DbUtil.close(rs);
			
			String s1 = "\n and not exists (select 1 from c_accountall where 1=1 and accpackageid='"+acc+"' and SubMonth = "+eMonth+" and isleaf1=1 and accsign = 1 and a.subjectid=subjectid )";
			String s2 = "\n and not exists (select 1 from c_assitementryacc where 1=1 and accpackageid='"+acc+"' and SubMonth = "+eMonth+" and isleaf1=1 and a.subjectid=accid )";
			String s3 = "\n and not exists (select 1 from c_assitementryacc where 1=1 and accpackageid='"+acc+"' and SubMonth = "+eMonth+" and isleaf1=1 and a.subjectid=accid)";
			String s4 = "\n and not exists (select 1 from c_assitementryaccall where 1=1 and accpackageid='"+acc+"' and SubMonth = "+eMonth+" and isleaf1=1 and accsign = 1 and a.subjectid=accid and a.assitemid=assitemid)";
			String s5 = "";
			
			String a1 = "\n and not exists (select 1 from c_usersubject where 1=1 and accpackageid='"+acc+"' and oriaccid='' and  a.subjectid=orisubjectid and oriDataName=0) ";
			String a2 = "\n and not exists (select 1 from c_usersubject where 1=1 and accpackageid='"+acc+"' and oriaccid='' and a.subjectid=orisubjectid and a.DataName = oriDataName) ";
			String a3 = "\n and not exists (select 1 from c_usersubject where 1=1 and accpackageid='"+acc+"' and oriDataName=0 and a.subjectid = oriaccid and a.assitemid=orisubjectid) ";
			String a4 = "\n and not exists (select 1 from c_usersubject where 1=1 and accpackageid='"+acc+"' and a.subjectid = oriaccid and a.assitemid=orisubjectid and a.DataName =oriDataName) ";

			if(new SubjectAssitemService(conn).ifnew(acc)){
				s2 = "\n and not exists (select 1 from c_assitementryacc b left join c_subjectassitem c on c.accpackageid='"+acc+"' and c.ifequal = 1 and c.subjectid = b.accid where b.accpackageid='"+acc+"' and c.accpackageid is  null and b.SubMonth = "+eMonth+" and b.isleaf1=1 and a.subjectid= b.accid)";
				s3 = "\n and not exists (select 1 from c_assitementryacc b left join c_subjectassitem c on c.accpackageid='"+acc+"' and c.ifequal = 1 and c.subjectid = b.accid where b.accpackageid='"+acc+"' and c.accpackageid is  null and b.SubMonth = "+eMonth+" and b.isleaf1=1 and a.subjectid= b.accid)";
				s5 = "\n and exists(select 1 from c_subjectassitem where 1=1 and accpackageid='"+acc+"'  and a.subjectid = subjectid and (a.AssTotalName1 = AssTotalName1 or a.AssTotalName1 like concat(AssTotalName1,'/%')))";
			}
			
			
//			if("1".equals(isCheck)){
//				s2 = "";
//				s3 = "";
//				a3 = " and 1=2 ";
//				a4 = " and 1=2 ";
//			}


			sql = "select *,ifnull(round(exrate/Balance,4),1) as  exchangerate from (" +
			"\n select '0' opt,a.subjectid,a.assitemid,a.accname,subjectfullname1," +
			"\n case a.DataName when '0' then '本位币' else concat('外币：',a.DataName) end dataname," +
			"\n case a.direction when 1 then '借' else '贷' end direction," +
			"\n a.direction*initBalance initBalance,DebitTotalOcc,CreditTotalOcc," +
			"\n a.direction*Balance Balance,a.direction*a.occ occ,a.direction*(Balance+occ) remain,a.direction*exrate as exrate,DataName dName," +
			"\n if(  (if ((balance+occ) * a.direction <0 ,a.direction*-1,a.direction))=1,abs(balance+occ),0)  as ggsq," +
			"\n if(  (if ((balance+occ) * a.direction <0 ,a.direction*-1,a.direction))=-1,abs(balance+occ),0)  as qggs " +
			"\n from (	" +
//			科目
			"\n	select if(ifnull(c.subjectid,'0')='0',a.subjectid,concat(a.subjectid,'00')) subjectid,AssItemID,if(ifnull(c.subjectid,'0')='0',a.accname,concat(a.accname,'(原帐金额)')) accname,SubjectFullName1, DataName,direction,Balance,exrate,initBalance,DebitTotalOcc,CreditTotalOcc ,ifnull((DebitTotalOcc2-CreditTotalOcc2),0) occ  from ( " +
			"\n		select subjectid,'' AssItemID,accname,SubjectFullName1, DataName,direction2 direction," +
			"\n		sum(case subyearmonth*12+submonth when "+eYear+" then Balance else 0 end) Balance,  " +
			"\n		sum(case subyearmonth*12+submonth when "+eYear+" then Balance else 0 end) exrate,  " +
			"\n		sum(case subyearmonth*12+submonth when "+bYear+" then (DebitRemain+CreditRemain) else 0 end)  initBalance,  " +
			"\n		sum(DebitOcc) DebitTotalOcc,  " +
			"\n		sum(CreditOcc) CreditTotalOcc " +
			"\n		from c_account a where  subyearmonth*12+submonth>="+bYear+" and subyearmonth*12+submonth<="+eYear+"  and isleaf1=1  " + SubjectID +
			"\n		group by subjectid " +
			"\n		union " +
			"\n		select subjectID,'' AssItemID,SubjectName,SubjectFullName,0 DataName, case Property  when 01 then 1 when 02 then -1 else 0 end direction,0 Balance,0,0,0,0  from z_usesubject where projectID='"+projectid+"' and isleaf=1 " +
			"\n	) a  " +
			"\n	left join (select * from z_accountrectify b where projectID='"+projectid+"' and b.isleaf=1 ) b on a.SubjectID=b.SubjectID " +
			"\n	left join z_usesubject c on a.SubjectId=c.ParentSubjectId and c.projectID='"+projectid+"'  " +
			"\n	where 1=1 " + SubjectID + s1 + s2 + a1 + //加条件

			"\n	union " +
//			科目外币
			"\n	select a.*,ifnull((DebitTotalOcc2-CreditTotalOcc2),0) occ from ( " +
			"\n		select subjectid,'' AssItemID, accname,SubjectFullName1, DataName,direction2 direction," +
			"\n		sum(case subyearmonth*12+submonth when "+eYear+" then Balance else 0 end) Balance,  " +
			"\n		sum(case subyearmonth*12+submonth when "+eYear+" then BalanceF else 0 end) exrate,  " +
			"\n		sum(case subyearmonth*12+submonth when "+bYear+" then (DebitRemain+CreditRemain) else 0 end)  Remain," +
			"\n		sum(DebitOcc) DebitTotalOcc," +
			"\n		sum(CreditOcc) CreditTotalOcc	  " +
			"\n		from c_accountall a where  subyearmonth*12+submonth>="+bYear+" and subyearmonth*12+submonth<="+eYear+"  and isleaf1=1 and accsign = 1 " + SubjectID +
			"\n		group by subjectid,DataName" +
			"\n ) a " +
			"\n	left join z_accountallrectify b on projectID='"+projectid+"' and b.isleaf1=1  and a.SubjectID=b.SubjectID and a.DataName=b.DataName  " +
			"\n	where 1=1 " + SubjectID + s3 + a2 +	//加条件

			"\n	union " +
//			核算
			"\n	select a.*,ifnull((DebitTotalOcc2-CreditTotalOcc2),0) occ from ( " +
			"\n		select  accid subjectid,AssItemID,AssItemName,AssTotalName1, DataName,direction2 direction," +
			"\n		sum(case subyearmonth*12+submonth when "+eYear+" then Balance else 0 end) Balance,  " +
			"\n		sum(case subyearmonth*12+submonth when "+eYear+" then Balance else 0 end) exrate,  " +
			"\n		sum(case subyearmonth*12+submonth when "+bYear+" then (DebitRemain+CreditRemain) else 0 end)  Remain," +
			"\n		sum(DebitOcc) DebitTotalOcc," +
			"\n		sum(CreditOcc) CreditTotalOcc   " +
			"\n		from c_assitementryacc a where  subyearmonth*12+submonth>="+bYear+" and subyearmonth*12+submonth<="+eYear+"  and isleaf1=1  " + SubjectID.replaceAll(".SubjectID ", ".accid ")  +
			"\n		group by accid ,AssItemID" +
			"\n	) a " +
			"\n	left join z_assitemaccrectify b on projectID='"+projectid+"'  and a.subjectid=b.SubjectID and a.AssItemID=b.AssItemID  " +
			"\n	where 1=1 " + SubjectID + s4 + a3 + s5 +//加条件

			"\n	union " +
//			核算外币
			"\n	select a.*,ifnull((DebitTotalOcc2-CreditTotalOcc2),0) occ from ( " +
			"\n		select  accid subjectid,AssItemID,AssItemName,AssTotalName1, DataName,direction2 direction," +
			"\n		sum(case subyearmonth*12+submonth when "+eYear+" then Balance else 0 end) Balance,  " +
			"\n		sum(case subyearmonth*12+submonth when "+eYear+" then BalanceF else 0 end) exrate,  " +
			"\n		sum(case subyearmonth*12+submonth when "+bYear+" then (DebitRemain+CreditRemain) else 0 end)  Remain," +
			"\n		sum(DebitOcc) DebitTotalOcc," +
			"\n		sum(CreditOcc) CreditTotalOcc   " +
			"\n		from c_assitementryaccall a where  subyearmonth*12+submonth>="+bYear+" and subyearmonth*12+submonth<="+eYear+"  and isleaf1=1 and accsign = 1  " + SubjectID.replaceAll(".SubjectID ", ".accid ")  +
			"\n		group by accid ,AssItemID,DataName" +
			"\n	) a left join z_assitemaccallrectify b on projectID='"+projectid+"'  and a.subjectid=b.SubjectID and a.AssItemID=b.AssItemID and a.DataName=b.DataName  " +
			"\n	where 1=1 " + SubjectID  +  a4 + s5 +//加条件

			"\n ) a where 1=1  having abs(initBalance) + abs(DebitTotalOcc) + abs(CreditTotalOcc) + abs(Balance) + abs(occ) >0 " +

//			分组
			"\n union " +
			"\n select '1' opt,subjectid,assitemid,a.accname,a.SubjectFullName,dataname," +
			"\n case a.direction when 1 then '借' else '贷' end direction," +
			"\n	a.direction*Remain initBalance,DebitTotalOcc,CreditTotalOcc," +
			"\n a.direction*Balance Balance,a.direction*a.occ occ,a.direction*a.bal remain,a.direction*exrate as exrate, dName," +
			"\n ggsq as ggsq," +
			"\n qggs as qggs " +
			"\n from (" +

			"\n	select if(a.accid ='',a.subjectid,a.accid) subjectid," +
			"\n	if(a.accid ='','',a.subjectid ) assitemid,a.subjectname accname,a.SubjectFullName," +
			"\n	case a.oriDataName when '0' then '本位币' else concat('外币：',a.oriDataName) end dataname," +
			"\n	a.direction,sum(b.Remain) Remain,sum(b.DebitTotalOcc) DebitTotalOcc,sum(b.CreditTotalOcc) CreditTotalOcc,sum(b.Balance) Balance,sum(b.exrate) exrate,sum(b.occ) occ,sum(b.Balance+b.occ) bal ,oriDataName dName," +
			"\n sum(if((b.Balance+b.occ)>=0,abs(b.Balance+b.occ),0)) ggsq," +
			"\n sum(if((b.Balance+b.occ)<0,abs(b.Balance+b.occ),0)) qggs " +
			"\n	from c_usersubject a left join (" +

//			分组	科目
			"\n		select  a.*,ifnull((DebitTotalOcc2-CreditTotalOcc2),0) occ  from ( " +
			"\n			select '' accid,subjectid,accname,SubjectFullName1, DataName,direction2 direction,  " +
			"\n			sum(case subyearmonth*12+submonth when "+eYear+" then Balance else 0 end) Balance,  " +
			"\n			sum(case subyearmonth*12+submonth when "+eYear+" then Balance else 0 end) exrate,  " +
			"\n			sum(case subyearmonth*12+submonth when "+bYear+" then (DebitRemain+CreditRemain) else 0 end)  Remain," +
			"\n			sum(DebitOcc) DebitTotalOcc," +
			"\n			sum(CreditOcc) CreditTotalOcc  " +
			"\n			from c_account a where subyearmonth*12+submonth>="+bYear+" and subyearmonth*12+submonth<="+eYear+" and isleaf1=1  " +
			"\n			group by subjectid" +
			"\n		) a left join (select * from z_accountrectify b where projectID='"+projectid+"' and b.isleaf=1) b on a.SubjectID=b.SubjectID " +
			"\n 	where 1=1 " +
			"\n		and exists (select 1 from c_usersubject where 1=1 and accpackageid='"+acc+"' and oriaccid='' and oriDataName=0 and a.subjectid=orisubjectid)" +
			s1 + s2 +
			"\n		union " +

//			分组	科目外币
			"\n		select a.*,ifnull((DebitTotalOcc2-CreditTotalOcc2),0) occ from ( " +
			"\n			select '' accid,subjectid,accname,SubjectFullName1, DataName,direction2 direction," +
			"\n		  	sum(case subyearmonth*12+submonth when "+eYear+" then Balance else 0 end) Balance,  " +
			"\n		  	sum(case subyearmonth*12+submonth when "+eYear+" then BalanceF else 0 end) exrate,  " +
			"\n			sum(case subyearmonth*12+submonth when "+bYear+" then (DebitRemain+CreditRemain) else 0 end)  Remain," +
			"\n			sum(DebitOcc) DebitTotalOcc," +
			"\n			sum(CreditOcc) CreditTotalOcc    " +
			"\n			from c_accountall a where  subyearmonth*12+submonth>="+bYear+" and subyearmonth*12+submonth<="+eYear+" and isleaf1=1 and accsign = 1 " +
			"\n			group by subjectid,DataName" +
			"\n		) a left join z_accountallrectify b on projectID='"+projectid+"' and b.isleaf1=1  and a.SubjectID=b.SubjectID and a.DataName=b.DataName " +
			"\n		where 1=1 " +
			"\n		and exists (select 1 from c_usersubject where 1=1 and accpackageid='"+acc+"' and oriaccid='' and a.subjectid=orisubjectid and a.DataName = oriDataName)" +
			s3 +
			"\n		union " +

//			分组核算
			"\n		select a.*,ifnull((DebitTotalOcc2-CreditTotalOcc2),0) occ from ( " +
			"\n			select  accid subjectid,AssItemID,AssItemName,AssTotalName1, DataName,direction2 direction," +
			"\n			sum(case subyearmonth*12+submonth when "+eYear+" then Balance else 0 end) Balance,     " +
			"\n			sum(case subyearmonth*12+submonth when "+eYear+" then Balance else 0 end) exrate,     " +
			"\n			sum(case subyearmonth*12+submonth when "+bYear+" then (DebitRemain+CreditRemain) else 0 end)  Remain," +
			"\n			sum(DebitOcc) DebitTotalOcc," +
			"\n			sum(CreditOcc) CreditTotalOcc       " +
			"\n			from c_assitementryacc a where  subyearmonth*12+submonth>="+bYear+" and subyearmonth*12+submonth<="+eYear+" and isleaf1=1  " +
			"\n			group by accid ,AssItemID" +
			"\n		) a left join z_assitemaccrectify b on projectID='"+projectid+"'  and a.subjectid=b.SubjectID and a.AssItemID=b.AssItemID  " +
			"\n 	where 1=1" +
			"\n		and exists (select 1 from c_usersubject where 1=1 and accpackageid='"+acc+"' and oriDataName=0 and a.subjectid = oriaccid and a.assitemid=orisubjectid)" +
			 s4 + s5 +
			"\n		union " +

//			分组核算外币
			"\n		select a.*,ifnull((DebitTotalOcc2-CreditTotalOcc2),0) occ from ( " +
			"\n			select  accid subjectid,AssItemID,AssItemName,AssTotalName1, DataName,direction2 direction," +
			"\n			sum(case subyearmonth*12+submonth when "+eYear+" then Balance else 0 end) Balance,  " +
			"\n			sum(case subyearmonth*12+submonth when "+eYear+" then BalanceF else 0 end) exrate,  " +
			"\n			sum(case subyearmonth*12+submonth when "+bYear+" then (DebitRemain+CreditRemain) else 0 end)  Remain," +
			"\n			sum(DebitOcc) DebitTotalOcc," +
			"\n			sum(CreditOcc) CreditTotalOcc         " +
			"\n			from c_assitementryaccall a where  subyearmonth*12+submonth>="+bYear+" and subyearmonth*12+submonth<="+eYear+" and isleaf1=1 and accsign = 1  " +
			"\n			group by accid ,AssItemID,DataName" +
			"\n		) a left join z_assitemaccallrectify b on projectID='"+projectid+"'  and a.subjectid=b.SubjectID and a.AssItemID=b.AssItemID and a.DataName=b.DataName  " +
			"\n 	where 1=1" + s5 +
			"\n		and exists (select 1 from c_usersubject where 1=1 and accpackageid='"+acc+"' and  a.subjectid = oriaccid and a.assitemid=orisubjectid and a.DataName =oriDataName)" +

			"\n	) b on a.accpackageid='"+acc+"' and a.oriaccid=b.accid and a.orisubjectid=b.subjectid and a.oriDataName = b.DataName" +
			"\n	where 1=1 and a.accpackageid='"+acc+"' group by a.oriDataName,concat(a.accid,'`',a.subjectid)" +

			"\n ) a where 1=1 " + SubjectID +  //加条件

			"\n having abs(initBalance) + abs(DebitTotalOcc) + abs(CreditTotalOcc) + abs(Balance) + abs(occ) >0 " +
			"\n ) a where 1=1 " +

			" order by subjectid,assitemid";

			sql = "select a.* ,c.subjectname from ("
			+ sql
			+ ") a " +
			" left join c_accpkgsubject c on c.accpackageid='"+acc+"' and a.subjectid like concat(c.subjectid,'%') and c.level0=1";
			
			sql = "create table " + this.tempTable + " " + sql ;
			st.execute(sql);
			
			sql = "update " + this.tempTable + " a , k_reationaddress b set a.accname =ifnull(b.fullname,a.accname) where a.accname =  b.relationname and b.customerid = '"+acc.substring(0,6)+"' ";
			st.execute(sql);
			
			/**
			 * 输出SQL
			 */
			sql = "select * from " + this.tempTable + " order by abs(exrate) desc ";
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
	
	public static void main(String[] args) {
		System.out.println( "银行存款|应付账款".split("\\|"));
	}
}
