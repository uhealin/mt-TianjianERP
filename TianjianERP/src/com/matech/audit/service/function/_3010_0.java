package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
*
*	用于批量刷新数量帐
* <p>Title: 取余额明细记录的函数，供批量刷新调用，相当于原来的1008 </p>
* <p>Description:
* 1、分析余额表和调整表，与3007的区别就是不刷辅助核算；
*   列刷类型。
* 2、刷新出的列内容包括：
*    科目或核算编号（subjectid），科目或核算名称（subjectname），数量单位(DataName)
*    数量帐：期初数（initbalance），借方发生（debitocc），贷方发生（creditocc），余额（balance），
*    本位币：
*    期初数（cnyinitbalance），借方发生（cnydebitocc），贷方发生（cnycreditocc），余额（cnybalance），
*    期初调整 initsov, 	
*    净期末调整 sov1
*    净期末重分类 sov2
*    净期末调整 + 净期末重分类 sov3
*    期末调整借 debitsov1
*    期末调整贷 creditsov1
*    期末重分类借 debitsov2
*    期末重分类贷 creditsov2	
*    期末调整借 + 期末重分类借  debitsov3
*    期末调整贷 + 期末重分类贷 creditsov3
*    期末调整借 + 期初调整借 + 账表不符借 debitsov4
*    期末调整贷 + 期初调整贷 + 账表不符贷 creditsov4 
*    期末重分类借 + 期初重分类借 debitsov5
*    期末重分类贷 + 期初重分类贷 creditsov5
*    期初 + 期初调整 + 期初重分类 + 账表不符 cnyinitbalance1
*    期末 + 期初调整 + 期初重分类 + 账表不符 cnybalance1
*    期末 + 调整 + 重分类 + 账表不符	cnysdbalance
*    借发生 + 期末调整借 + 期末重分类借 sddebittotalocc
*    贷发生 + 期末调整贷 + 期末重分类贷 sdcredittotalocc
*    期末借 + 期末调整借 + 期末重分类借 + 期初调整借 + 期初重分类借 + 账表不符借 cnysdDebitBalance
*    期末贷 + 期末调整贷 + 期末重分类贷 + 期初调整贷 + 期初重分类贷 + 账表不符贷 cnysdCreditBalance
* 3、支持自定义科目；
* 4、参数录入说明：
*    I、区间支持2个参数（起始月startmonth、结束月endmonth），若没有提供，就从项目的审计区间取；
*    II、对应科目参数：subjectname,如果科目参数为空，则从底稿的对应科目取名称；
*    III、科目余额方向参数：ptype,如果没有设置则取标准科目（或自增科目）的科目方向；
*    IV、关联客户参数：correlation ,不提供则是刷新全部，＝1，只显示关联客户的，＝0以及其他值，显示非关联客户的
* </p>
*
* </p>
*
* <p>Copyright: Copyright (c) 2007</p>
*
* <p>Company: matech</p>
*
* @author : winnerQ and py
* @version 1.0
*/

public class _3010_0 extends AbstractAreaFunction {
	public ResultSet process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {
		
		String accpackageid = (String) args.get("curAccPackageID");
        String projectid = (String) args.get("curProjectid");
        String customerid=accpackageid.substring(0,6);

        String resultSql = "";
        
        //关联客户参数：correlation ,不提供则是刷新全部，＝1，只显示关联客户的，＝0以及其他值，显示非关联客户的
        String correlation=request.getParameter("correlation"),strCorrelation1,strCorrelation2;
        if (correlation==null){
        	//没有提供
        	strCorrelation1=" ";
        	strCorrelation2=" ";
        }else{
        	//提供了
        	if ( correlation.equals("1")){
        		//只显示关联客户的；
        		strCorrelation1="        and exists (\n		select 1 from asdb.k_connectcompanys where k_connectcompanys.customerid="+customerid+" and connectcompanysname=c_account.accname\n        )\n";
        		strCorrelation2="        and exists (\n		select 1 from asdb.k_connectcompanys where k_connectcompanys.customerid="+customerid+" and connectcompanysname=z_usesubject.subjectname\n        )\n";
        	}else{
        		//显示非关联客户的
        		strCorrelation1="        and not exists (\n		select 1 from asdb.k_connectcompanys where k_connectcompanys.customerid="+customerid+" and connectcompanysname=c_account.accname\n        )\n";
        		strCorrelation2="        and not exists (\n		select 1 from asdb.k_connectcompanys where k_connectcompanys.customerid="+customerid+" and connectcompanysname=z_usesubject.subjectname\n        )\n";
        	}
        }
        args.put("strCorrelation1",strCorrelation1);
        args.put("strCorrelation2",strCorrelation2);
        
        Statement st = null;
        ResultSet rs = null;
        try {
            String subjectname = (String) args.get("subjectname");
            if (subjectname==null || subjectname.equals("")){
                String manuid=(String)args.get("manuid");
                if (manuid==null || manuid.equals("")){
                    subjectname=getTaskSubjectNameByTaskCode(conn,projectid,(String)request.getParameter("curTaskCode"));
                }else{
                    //如果科目名称为空，则通过前台提交的刷新底稿编号去取得对应任务得科目名称；
                    subjectname = getTaskSubjectNameByManuID(conn, manuid);
                }
               
            }
            
            String sName = changeSubjectName(conn,projectid,subjectname);
            if(!"".equals(sName)){
            	subjectname = sName; 
            }            
            args.put("subjectname",subjectname);
            

            String strStartYearMonth = "", strEndYearMonth = "";
            String startmonth = (String) args.get("startmonth");
            String endmonth = (String) args.get("endmonth");
            String strYears = "";
            if (startmonth == null || startmonth.equals("")
                || endmonth == null || endmonth.equals("")) {
                //如果前台没有提供这个参数，就从项目取；
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
                        //去掉最开始得,
                        strYears = " in (" + strYears.substring(1) + ")";
                    }
                }
            } else {
                strStartYearMonth = String.valueOf(Integer.parseInt(
                        accpackageid.substring(6)) * 12 +
                        Integer.parseInt(startmonth));
                strEndYearMonth = String.valueOf(Integer.parseInt(accpackageid.
                        substring(6)) * 12 + Integer.parseInt(endmonth));
                strYears = " = " + accpackageid.substring(6);
            }
            args.put("StartYearMonth", strStartYearMonth);
            args.put("EndYearMonth", strEndYearMonth);
            args.put("Years", strYears);

            //查找该科目在客户中的科目id,请注意即使有一对多的科目，这里也只是取其中的一条；
            String[] result = this.getClientIDAndDirectionByStandName(conn,
                    accpackageid, projectid,
                    subjectname);
            String subjectid = result[0];

            //如果没有提供方向这个参数，则取科目余额方向
            String ptype = (String) args.get("ptype");
            if (ptype == null || ptype.equals("")) {
                args.put("ptype", result[1]);
            }

            //判断该科目是否叶子并且有自增科目。
            st = conn.createStatement();
            resultSql = ""
                        + " select 1 from  \n"
                        + " c_account a \n"
                        + " inner join \n"
                        + " z_usesubject b \n"
                        + " on a.subjectid=b.tipsubjectid \n"
                        + " where a.accpackageid='" + accpackageid + "' \n"
                        + "   and a.subjectfullname2='" + subjectname + "' \n"
                        + "   and a.submonth=1 \n"
                        + "   and a.isleaf1=1 \n"
                        + "   and b.accpackageid='" + accpackageid + "' \n"
                        + "   and b.projectid='" + projectid + "' \n";
            rs = st.executeQuery(resultSql);

            //把有无下级科目刷新放进去
            if (rs.next()) {
                resultSql = getSql("0", subjectid);
            } else {
                resultSql = getSql("1", subjectid);
            }



            //最终查询结果
            resultSql = this.setSqlArguments(resultSql, args);
            System.out.println("resultSql="+resultSql);
            rs = st.executeQuery(resultSql);

            return rs;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
	}
	
	public String getSql(String rectifySign, String subjectid) {
		return "select *,ifnull(subject, cnysubjectid) subjectid,ifnull(accname,cnysubjectname) as subjectname " +
		" from ( "+ 
		" 	select  "+ 
		" 	subjectid as subject,accname,subjectfullname2 as subjectfullname, DataName,"+ 
		" 	sum(if (subyearmonth*12+submonth=${EndYearMonth},balance * (${ptype}),0)) as balance,  "+ 
		" 	sum(if (subyearmonth*12+submonth=${StartYearMonth},(debitremain+creditremain) * (${ptype}),0))  as initbalance,  "+ 
		" 	sum(debitocc) as debitocc,sum(creditocc) as creditocc "+ 
		" 	from c_accountall  "+ 
		" 	where 1=1 " +
		"	and (subjectfullname2 like '${subjectname}/%' or subjectfullname2 = '${subjectname}')"+ 
		" 	and subyearmonth*12+submonth>=${StartYearMonth}  "+ 
		" 	and subyearmonth*12+submonth<=${EndYearMonth} ${strCorrelation1} "+ 
		" 	and accsign=2 and isleaf1=1  "+ 
		" 	group by subjectid,DataName  "+ 
		" ) a right join ( " +
		"	select a.* ," +
		
		" 	(debittotalocc4 - credittotalocc4 + debittotalocc5 - credittotalocc5 + debittotalocc6 - credittotalocc6) * (${ptype}) * a.rectifySign as initsov, " +		
		" 	(debittotalocc1 - credittotalocc1)  * (${ptype}) * a.rectifySign as sov1," +
		" 	(debittotalocc2 - credittotalocc2)  * (${ptype}) * a.rectifySign as sov2," +
		" 	((debittotalocc1 - credittotalocc1) + (debittotalocc2 - credittotalocc2) ) * (${ptype}) * a.rectifySign as sov3," +
		" 	debittotalocc1 * a.rectifySign as debitsov1, " +
		" 	credittotalocc1 * a.rectifySign as creditsov1, " +
		" 	debittotalocc2 * a.rectifySign as debitsov2, " +
		" 	credittotalocc2 * a.rectifySign as creditsov2, " +
		
		" 	(debittotalocc1+debittotalocc2) * a.rectifySign as debitsov3, " +
		" 	(credittotalocc1+debittotalocc2) * a.rectifySign as creditsov3, " +
		" 	(debittotalocc1+debittotalocc4+debittotalocc6) * a.rectifySign as debitsov4, " +
		" 	(credittotalocc1+credittotalocc4+credittotalocc6) * a.rectifySign as creditsov4, " +
		" 	(debittotalocc2+debittotalocc5) * a.rectifySign as debitsov5 , " +
		" 	(credittotalocc2+credittotalocc5) * a.rectifySign as creditsov5, " +
		
		" 	cnyinitbalance +(debittotalocc4 - credittotalocc4 + debittotalocc5 - credittotalocc5 + debittotalocc6 - credittotalocc6) * (${ptype}) * a.rectifySign as cnyinitbalance1, " +
		" 	cnybalance +( (debittotalocc4 - credittotalocc4 + debittotalocc5 - credittotalocc5 + debittotalocc6 - credittotalocc6) * (${ptype})  )  * a.rectifySign as cnybalance1, " +
		" 	cnybalance + ( (debittotalocc4 - credittotalocc4 + debittotalocc5 - credittotalocc5 + debittotalocc6 - credittotalocc6) * (${ptype}) + ((debittotalocc1 - credittotalocc1) + (debittotalocc2 - credittotalocc2) ) * (${ptype}) )  * a.rectifySign as cnysdbalance, " +
		"	cnydebitocc + (debittotalocc1+debittotalocc2) * a.rectifySign as sddebittotalocc," +
		"	cnycreditocc + (credittotalocc1+credittotalocc2) * a.rectifySign as sdcredittotalocc, " + 
		"	cnyDebitBalance + (debittotalocc1+debittotalocc4+debittotalocc6+debittotalocc2+debittotalocc5) * a.rectifySign as  cnysdDebitBalance," +
		" 	cnyCreditBalance + (credittotalocc1+credittotalocc4+credittotalocc6+credittotalocc2+credittotalocc5) * a.rectifySign  as cnysdCreditBalance " + 
		
		"	from ("+ 
		" 		select  "+ 
		" 		subjectid as cnysubjectid,accname as cnysubjectname,subjectfullname2 as cnysubjectfullname, "+ 
		" 		sum(if (subyearmonth*12+submonth=${EndYearMonth},balance * (${ptype}),0)) as cnybalance,  "+ 
		" 		sum(if (subyearmonth*12+submonth=${EndYearMonth},DebitBalance,0)) as cnyDebitBalance,  "+ 
		" 		sum(if (subyearmonth*12+submonth=${EndYearMonth},CreditBalance * (-1),0)) as cnyCreditBalance,  "+ 
		
		" 		sum(if (subyearmonth*12+submonth=${StartYearMonth},(debitremain+creditremain) * (${ptype}),0))  as cnyinitbalance,  "+ 
		" 		sum(debitocc) as cnydebitocc,sum(creditocc) as cnycreditocc,"+rectifySign+" as rectifySign "+ 
		" 		from c_account "+ 
		" 		where 1=1" +
		"		and (subjectfullname2 like '${subjectname}/%' or subjectfullname2 = '${subjectname}')  "+ 
		" 		and subyearmonth*12+submonth>=${StartYearMonth}  "+ 
		" 		and subyearmonth*12+submonth<=${EndYearMonth} ${strCorrelation1} "+ 
		" 		and isleaf1=1  "+ 
		" 		group by subjectid  " +
		"	) a left join z_accountrectify  b on a.cnysubjectid = b.subjectid and b.projectid = ${curProjectid} and b.isleaf=1  "+ 
		" ) b on a.subject = b.cnysubjectid "+ 
		" where 1=1 "+ 
		" order by a.subject";
	}
}
