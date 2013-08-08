package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.framework.pub.util.ASFuntion;

public class _3038_0 extends AbstractAreaFunction {

	/**
	 * 找出所有参照科目(CS)下的叶子科目名称,同名称的要合并, left join
	 * 当前科目(DS)下的余额表(科目名称/科目全称/科目期初数/借发生/贷发生/余额)和调整表(借调整/贷调整/);
	 * 
	 * 公式的设置方法： 
	 * =取列公式覆盖(3038, "", "balance", "&s=;&cs=累计摊销;无形资产减值准备;无形资产&ds=无形资产")
	 * =取列公式覆盖(3038, "", "subjectname", "&s=;&cs=累计摊销;无形资产减值准备;无形资产&ds=累计摊销")
	 * =取列公式覆盖(3038, "", "balance", "&s=;&cs=累计摊销;无形资产减值准备;无形资产&ds=无形资产减值准备")
	 * 
	 * 参数：
	 * subjectname　科目名称
	 * initbalance　期初数
	 * debitocc		借发生
	 * creditocc	贷发生
	 * balance		期末数
	 * DebitBalance		借期末数
	 * CreditBalance	贷期末数
	 * 
	 * initsov		期初调整审定数
	 * sov1			期末调整
	 * sov2			期末重分类
	 * sov3			期末调整 + 期末重分类
	 * 
	 * debitsov1	期末调整借
	 * creditsov1	期末调整贷
	 * debitsov2	期末重分类借
	 * creditsov2	期末重分类贷
	 * debitsov3	期末调整借 + 期末重分类借
	 * creditsov3	期末调整贷 + 期末重分类贷
	 * debitsov4	期末调整借 + 期末重分类借 + 账表不符借
	 * creditsov4	期末调整贷 + 期末重分类贷 + 账表不符贷
	 * debitsov5	期初调整借 + 期初重分类借
	 * creditsov5	期初调整贷 + 期初重分类贷
	 * 
	 */
	public ResultSet process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {

		ASFuntion asf = new ASFuntion();

		String accpackageid = (String) args.get("curAccPackageID");
		String projectid = (String) args.get("curProjectid");

		// 分割符
		String s = (String) args.get("s");
		// 第一个科目（下面称借方科目）
		String ds = (String) args.get("ds");
		String myds = "";

		// 第二个科目（下面称贷方科目）
		String cs = (String) args.get("cs");
		cs = asf.replaceStr(cs, s, "','");
		String mycs = "";

		String strStartYearMonth = "", strEndYearMonth = "";
		String startmonth = (String) args.get("startmonth");
		String endmonth = (String) args.get("endmonth");
		String strYears = "";
		if (startmonth == null || startmonth.equals("") || endmonth == null
				|| endmonth.equals("")) {
			// 如果前台没有提供这个参数，就从项目取；
			int[] result = getProjectAuditAreaByProjectid(conn, projectid);
			strStartYearMonth = String.valueOf(result[0] * 12 + result[1]);
			strEndYearMonth = String.valueOf(result[2] * 12 + result[3]);

			if (result[0] == result[2]) {
				strYears = " = " + result[0];
			} else {
				for (int i = result[0]; i <= result[2]; i++) {
					strYears += "," + String.valueOf(i);
				}
				if (strYears.length() > 0) {
					// 去掉最开始得,
					strYears = " in (" + strYears.substring(1) + ")";
				}
			}
		} else {
			strStartYearMonth = String.valueOf(Integer.parseInt(accpackageid.substring(6)) * 12 + Integer.parseInt(startmonth));
			strEndYearMonth = String.valueOf(Integer.parseInt(accpackageid.substring(6))* 12 + Integer.parseInt(endmonth));
			strYears = " = " + accpackageid.substring(6);
		}
		args.put("StartYearMonth", strStartYearMonth);
		args.put("EndYearMonth", strEndYearMonth);
		args.put("Years", strYears);

		/**
		 * accpackageid : 当前项目的帐套编号 anotherApkID ：比较年份的帐套编号，可以上一年，也可以下一年
		 * accpackageid ：当前刷新的帐套编号，是前面2个帐套编号中的某一个；
		 */

		String sql = "";

		Statement st = null;
		ResultSet rs = null;
		try {

			st = conn.createStatement();

			// 找出参数科目在客户中的底层科目的科目编号
//			sql = " select  \n"
//					+ " 	group_concat(subjectid SEPARATOR '\\',\\'') as subjectid \n"
//					+ " from ( select subjectid from  c_account \n"
//					+ " where isleaf1=1 \n" + " and submonth=1 \n"
//					+ " and accpackageid =" + accpackageid + " \n"
//					+ " and subjectfullname2 like '" + ds + "%' \n" + " ) a";
//			rs = st.executeQuery(sql);
//			if (rs.next()) {
//				myds = rs.getString(1);
//			} else {
//				myds = "";
//			}
//			rs.close();

			// 找出冲销参数科目在客户中的底层科目的科目编号
			sql = " select group_concat(subjectid SEPARATOR '\\',\\'') as subjectid \n"
					+ " from ( select subjectid from  c_account \n"
					+ " where isleaf1=1   and submonth=1 \n"
					+ " and accpackageid =" + accpackageid + " \n"
					+ " and substr(subjectfullname2,1, \n"
					+ " 			if(locate('/',subjectfullname2) = 0, \n"
					+ " 					length(subjectfullname2), \n"
					+ " 					locate('/',subjectfullname2) -1 ) \n"
					+ "             ) in ('" + asf.replaceStr(cs, s, "','")
					+ "')  \n" 
					+ " ) a";
			rs = st.executeQuery(sql);
			if (rs.next()) {
				mycs = rs.getString(1);
			} else {
				mycs = "";
			}
			rs.close();

			
			String[] result=this.getClientIDAndDirectionByStandName(conn, accpackageid, projectid,ds);
//            String subjectid = result[0];
            String ptype = (String) args.get("ptype");
            if (ptype==null||ptype.equals("")){
                args.put("ptype",result[1]);
            }
            
			String myos = "";
			sql = " select group_concat(subjectid SEPARATOR '\\',\\'') as subjectid \n"
				+ " from ( select subjectid from  c_account \n"
				+ " where level1=1  and submonth=1 \n"
				+ " and accpackageid =" + accpackageid + " \n"
				+ " and substr(subjectfullname2,1, \n"
				+ " 			if(locate('/',subjectfullname2) = 0, \n"
				+ " 					length(subjectfullname2), \n"
				+ " 					locate('/',subjectfullname2) -1 ) \n"
				+ "             ) in ('" + asf.replaceStr(cs, s, "','")
				+ "')  ) a";
			rs = st.executeQuery(sql);
			if (rs.next()) {
				myos = rs.getString(1);
			} else {
				myos = "";
			}
			rs.close();
			
            //判断该科目是否叶子并且有自增科目。
            sql = ""
                  + " select 1 from  \n"
                  + " c_account a \n"
                  + " inner join \n"
                  + " z_usesubject b \n"
                  + " on a.subjectid=b.tipsubjectid \n"
                  + " where a.accpackageid='" + accpackageid + "' \n"
                  + "   and a.subjectfullname2='" + ds + "' \n"
                  + "   and a.submonth=1 \n"
                  + "   and a.isleaf1=1 \n"
                  + "   and b.accpackageid='" + accpackageid + "' \n"
                  + "   and b.projectid='" + projectid + "' \n";
            rs = st.executeQuery(sql);
            String rectifySign="";
            if (rs.next()) {
            	rectifySign = "0";
            } else {
            	rectifySign = "1";
            }
            
            
            sql= "select * \n"
        	+"from \n"
        	+"( \n"
        	+"       select distinct accname as subjectname\n"
		    +"       from c_account \n"
		    +"       where subyearmonth*12+submonth=${EndYearMonth} \n"
		    +"       and isleaf1=1 \n"
		    +"       and subjectid in ('"+mycs+"')   \n"
		    +"        \n"
            +"       union \n"
            +"        \n"
            +"       select subjectname  \n"
            +"      from z_usesubject \n"
            +"      where projectid=${curProjectid}  \n"
            +"      and tipsubjectid in ('"+myos+"') \n"
            +"      and isleaf=1 \n"
            +"     ) a  \n"	   			   
            +"  left join  \n"
            +" (   /* a2是余额表的数据 */  \n"
            +"     select   \n"
            +"            a.*, \n"
            +"              \n"
            +"            ifnull(e.sov1,0) as sov1,  \n"
            +"            ifnull(e.sov2,0) as sov2,  \n"
            +"            ifnull(e.debitsov1,0) as debitsov1,  \n"
            +"            ifnull(e.creditsov1,0) as creditsov1,  \n"
            +"            ifnull(e.debitsov2,0) as debitsov2,  \n"
            +"            ifnull(e.creditsov2,0) as creditsov2,  \n"
            +"              \n"
            +"            ifnull(e.sov1+e.sov2,0) as sov3,  \n"
            +"            ifnull(e.debitsov1+e.debitsov2,0) as debitsov3,  \n"
            +"            ifnull(e.creditsov1+e.creditsov2,0) as creditsov3, \n"
            +"             \n"
            
            +"            ifnull(e.creditsov4,0) as creditsov4, \n"
            +"            ifnull(e.debitsov4,0) as debitsov4, \n"
            +"            ifnull(e.creditsov5,0) as creditsov5, \n"
            +"            ifnull(e.debitsov5,0) as debitsov5, \n"
            
            
            +"            ifnull(e.initsov,0) as initsov \n"
            +"              \n"
            +"     from   \n"
            +"     (  \n"
            +"       select   \n"
            +"              subjectid,accname,subjectfullname2,sum(if (subyearmonth*12+submonth=${EndYearMonth},balance * direction2,0)) as balance,  \n"

            +"				sum(if (subyearmonth*12+submonth=${EndYearMonth},DebitBalance,0)) as DebitBalance, \n"
            +"				sum(if (subyearmonth*12+submonth=${EndYearMonth},(-1)*CreditBalance,0)) as CreditBalance, \n"				
            
            +"              sum(if (subyearmonth*12+submonth=${StartYearMonth},(debitremain+creditremain) * direction2,0)) /**/ as initbalance,  \n"
            +"              sum(debitocc) as debitocc,sum(creditocc) as creditocc,"+rectifySign+" as rectifySign \n"
            +"       from c_account \n"
            +"       where subyearmonth*12+submonth>=${StartYearMonth} and subyearmonth*12+submonth<=${EndYearMonth} \n"
            +"       and isleaf1=1 \n"
            +"       and (subjectfullname2 like '${ds}/%' or subjectfullname2 = '${ds}')   \n"
            +"       group by subjectid "
            +"        \n"
            +"       union \n"
            +"        \n"
            +"       select \n"
            +"            subjectid, \n"
            +"            subjectname as accname, \n"
            +"            subjectfullname as subjectfullname2, \n"
            +"            0 as balance, 	0 as DebitBalance, 0 as CreditBalance, \n"
            +"            0 as initbalance,  \n"
            +"            0 as  debitocc, \n"
            +"            0 as  creditocc, \n"
            +"            1 as rectifySign \n"
            +"      from z_usesubject \n"
            +"      where projectid=${curProjectid}  \n"
            +"      and tipsubjectid in ('"+myos+"') \n"
            +"      and isleaf=1 \n"
            +"     ) a  \n"
            +"       \n"
            +"     /* 调整 */  \n"
            +"     left join  \n"
            +"     (  \n"
            +"         select   \n"
            +"                subjectid,(debittotalocc1 - credittotalocc1)  * (${ptype}) as sov1,  \n"
            +"                (debittotalocc2 - credittotalocc2)  * (${ptype}) as sov2,  \n"
            +"                debittotalocc1 as debitsov1,  \n"
            +"                credittotalocc1 as creditsov1,  \n"
            +"                debittotalocc2 as debitsov2,  \n"
            +"                credittotalocc2 as creditsov2, \n"
            +"                (debittotalocc4 - credittotalocc4 + debittotalocc5 - credittotalocc5 + debittotalocc6 - credittotalocc6) * (${ptype}) as initsov \n,"
            
            +"                credittotalocc1+credittotalocc4+credittotalocc6 as creditsov4,  \n"
            +"                debittotalocc1+debittotalocc4+debittotalocc6 as debitsov4,  \n"
            +"                credittotalocc2+credittotalocc5 as creditsov5,  \n"
            +"                debittotalocc2+debittotalocc5 as debitsov5  \n"
            
            +"         from z_accountrectify  \n"
            +"         where projectid = ${curProjectid}  \n"
            +"         and isleaf=1 "
            +"     ) e   \n"
            +"     on a.subjectid =e.subjectid  \n"
            +"       \n"
            +" ) a2  \n"
            +"on a.subjectname = a2.accname \n"
            +"order by subjectname";
			
            sql = this.setSqlArguments(sql, args);
			System.out.println("qwh:sql3=" + sql);
			rs = st.executeQuery(sql);

			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
}
