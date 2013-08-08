package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class _2002_59 extends AbstractAreaFunction {


	public ResultSet process(HttpSession session, HttpServletRequest request, HttpServletResponse response, Connection conn, Map args) throws Exception {


//		&type=1&subjectNameC=银行存款&startmonth=12&endmonth=12


		String apkID=(String)args.get("curAccPackageID");
		String prjID=(String)args.get("curProjectid");

		String subjectNameC=(String)args.get("subjectNameC");
		String startmonth=(String)args.get("startmonth");
		String endmonth=(String)args.get("endmonth");
		String type=(String)args.get("type");



		String resultSql="";


		Statement st=null;
		ResultSet rs=null;
		try{
			st=conn.createStatement();

//			查找该科目在客户中的科目id
			String sql="";
			String subjectid=getClientIDByStandName(conn, apkID, prjID, subjectNameC);



//			判断该科目是否叶子并且有自增科目。
			sql=""
			+" select * from  \n"
			+" c_account a \n"
			+" inner join \n"
			+" z_usesubject b \n"
			+" on a.subjectid=b.tipsubjectid \n"
			+" where a.accpackageid='"+apkID+"' \n"
			+"   and a.subjectfullname2='"+subjectNameC+"' \n"
			+"   and a.submonth=1 \n"
			+"   and a.isleaf1=1 \n"
			+"   and b.accpackageid='"+apkID+"' \n"
			+"   and b.projectid='"+prjID+"' \n" ;
			rs=st.executeQuery(sql);

			if(rs.next()){
				resultSql=getSql("0",subjectid);
			}else{
				resultSql=getSql("1",subjectid);
			}

			//最终查询结果
			resultSql=this.setSqlArguments(resultSql, args);
			System.out.println(resultSql);
			rs=st.executeQuery(resultSql);

			return rs;
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}

	}

	public String getSql(String rectifySign,String subjectid){
		return ""
		+" /*参数:type 1 和 -1 与前后余额相乘,subjectNameC ,startmonth,endmonth  \n"
		+"   根据科目名称取其下级科目的前后余额，期总发生额,调整,重分类 \n"
		+" */ \n"
		+" select \n"
		+"   accname as subjectname, \n"
		+"   (a.DebitRemain + a.CreditRemain) * (${type}) as initbalance, \n"
		+"   (balance+IFNULL((sum(c.DebitTotalOcc5)-sum(CreditTotalOcc5)),0.00)* rectifySign )* (${type}) as balance , \n"
		+"   IFNULL(sum(c.DebitTotalOcc1)-sum(CreditTotalOcc1) + sum(c.DebitTotalOcc2)-sum(CreditTotalOcc2)+sum(c.DebitTotalOcc5)-sum(CreditTotalOcc5),0.00) * rectifySign * (${type})  + balance * (${type}) as sdbalance, \n"
		+"  \n"
		+"  \n"
		+"   debitocc, \n"
		+"   creditocc, \n"
		+"   debittotalocc, \n"
		+"   credittotalocc, \n"
		+"   IFNULL(sum(c.DebitTotalOcc1),0.00) * (${type}) * rectifySign as debitrectify, \n"
		+"   IFNULL(sum(c.DebitTotalOcc2),0.00) * (${type}) * rectifySign as  debitsortagain, \n"
		+"   IFNULL(sum(CreditTotalOcc1),0.00) * (${type}) * rectifySign as creditrectify, \n"
		+"   IFNULL(sum(CreditTotalOcc2),0.00) * (${type}) * rectifySign as  creditsortagain, \n"
		+"    \n"
		+"   IFNULL((sum(c.DebitTotalOcc4)-sum(CreditTotalOcc4)),0.00) * (${type}) * rectifySign as initrectify, \n"
		+"   IFNULL((sum(c.DebitTotalOcc1)-sum(CreditTotalOcc1)),0.00) * (${type}) * rectifySign as rectify, \n"
		+"   IFNULL((sum(c.DebitTotalOcc5)-sum(CreditTotalOcc5)),0.00) * (${type}) * rectifySign as  initsortagain, \n"
		+"   IFNULL((sum(c.DebitTotalOcc2)-sum(CreditTotalOcc2)),0.00) * (${type}) * rectifySign as  sortagain, \n"
		+"   IFNULL((sum(c.DebitTotalOcc1)-sum(CreditTotalOcc1)),0.00) * (${type}) + IFNULL((sum(c.DebitTotalOcc2)-sum(CreditTotalOcc2)),0.00) * (${type}) * rectifySign as rectify2, \n"
		+"    \n"
		+"   if(d.cbalance=0.00 or d.cbalance is null,a.balance,d.cbalance) as cbalance, \n"
		+"   if(d.dataname > '',d.dataname,e.currname) as currname \n"
		+" FROM  \n"
		+" (     \n"
		+"       select  \n"
		+"            subjectid, \n"
		+"            accname, \n"
		+"            subjectfullname2, \n"
		+"            balance, \n"
		+"            debitocc, \n"
		+"            creditocc, \n"
		+"            debittotalocc, \n"
		+"            credittotalocc, \n"
		+"            accpackageid, \n"
		+"            DebitRemain, \n"
		+"            CreditRemain, \n"
		+"            isleaf1 as isleaf, \n"
		+"            "+rectifySign+" as rectifySign \n"
		+"       from c_account a\n"
		+" 		 where submonth >= ${startmonth} \n"
		+" 	     and   submonth <= ${endmonth} \n"
		+"       and   a.isleaf1=1 \n"
		+"       and   accpackageid=${curPackageid} \n"
		+"       and   ((a.subjectfullname2 like concat('${subjectNameC}','/%') or a.subjectfullname2 ='${subjectNameC}' ))\n"
		+"         \n"
		+"       union \n"
		+"        \n"
		+"       select \n"
		+"            subjectid, \n"
		+"            subjectname as accname, \n"
		+"            subjectfullname as subjectfuallname2, \n"
		+"            0 as balance, \n"
		+"            0 as  debitocc, \n"
		+"            0 as  creditocc, \n"
		+"            0 as  debittotalocc, \n"
		+"            0 as  credittotalocc, \n"
		+"            accpackageid, \n"
		+"            0 as DebitRemain, \n"
		+"            0 as CreditRemain, \n"
		+"            1 as isleaf, \n"
		+"            1 as rectifySign \n"
		+"      from z_usesubject \n"
		+"      where projectid=${curProjectid}  \n"
		+"      and accpackageid=${curPackageid}  \n"
		+"      and tipsubjectid='"+subjectid+"' \n"
		+"      and isleaf=1 \n"
		+" ) a   \n"
		+" left JOIN z_accountrectify c \n"
		+" on ${curPackageid}=c.accpackageid and c.subjectid like concat(a.subjectid,'%') and c.projectid=${curProjectid} \n"
		+"         and c.isleaf=1 "
		+" left join \n"
		+" ( \n"
		+"     select balance * (${type}) cbalance,subjectid,dataname from c_accountall where accpackageid = '${curPackageid}' and submonth>=${startmonth} and submonth<=${endmonth} \n"
		+" ) d on a.subjectid=d.subjectid \n"
		+" inner JOIN \n"
		+" ( \n"
		+"   select currname from c_accpackage where accpackageid='${curPackageid}' \n"
		+" ) e \n"


		+" group by a.subjectid \n"
		+" order by a.subjectid \n"
		+" \n";
	}
}
