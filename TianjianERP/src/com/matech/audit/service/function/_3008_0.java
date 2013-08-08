package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * <p>Title: 取余额明细记录的函数，供批量刷新调用，相当于原来的1008 </p>
 * <p>Description:
 * 1、分析余额表和调整表，与3007的区别就是不刷辅助核算；
 *   列刷类型。
 * 2、刷新出的列内容包括：
 *    科目或核算编号（subjectid），科目或核算名称（subjectname），期初数（initbalance），
 *    借方发生（debitocc），贷方发生（creditocc），余额（balance），审定数（sdbalance），
 *    年末调整（sov1），年末重分类（sov2），年末总调整数（sov3），年初总调整数（initsov）
 *    年末调整借（debitsov1），年末调整贷（creditsov1），
 *    年末重分类借（debitsov2），年末重分类贷（creditsov2），
 *    年末总调整借（debitsov3），年末总调整贷（creditsov3）
 *    审定借方发生（sddebittotalocc）、审定贷方发生（sdcredittotalocc）
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

public class _3008_0 extends AbstractAreaFunction {

    public ResultSet process(HttpSession session, HttpServletRequest request,
                             HttpServletResponse response, Connection conn,
                             Map args) throws Exception {

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
            	if (result[1] == null || result[1].equals("")) {
            		args.put("ptype", "1");
            	}else{
            		args.put("ptype", result[1]);
            	}
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
            //System.out.println("resultSql="+resultSql);
            rs = st.executeQuery(resultSql);

            return rs;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }


    /**
     *
     * @param rectifySign String
     *     这个rectifySign不是余额方向，而是为了避免把多极科目调整重复汇总的标志，有下级就为0，否则为1
     * @param subjectid String
     * @return String
     */
    public String getSql(String rectifySign, String subjectid) {
        return "     select   \n"
                +"           a.subjectid,a.subjectname,a.subjectfullname,\n"
                +"           a.balance+ ifnull(e.initsov,0) * a.rectifySign as balance,\n"
                
                +"           a.balance * a.rectifySign as accbalance,\n"
                
                +"           a.initbalance  as initbalance1,\n"
                
                +"           a.initbalance + ifnull(e.initsov,0) * a.rectifySign as initbalance,\n"
                +"           a.balance+ ifnull(e.sov1+e.sov2+e.initsov,0) * a.rectifySign as sdbalance,\n"
                +"           a.debitocc,a.creditocc, \n"
                + "              \n"
                + "            ifnull(e.sov1,0) * a.rectifySign as sov1,  \n"
                + "            ifnull(e.sov2,0) * a.rectifySign  as sov2,  \n"
                + "            ifnull(e.debitsov1,0) * a.rectifySign  as debitsov1,  \n"
                + "            ifnull(e.creditsov1,0) * a.rectifySign  as creditsov1,  \n"
                + "            ifnull(e.debitsov2,0) * a.rectifySign  as debitsov2,  \n"
                + "          ifnull(e.creditsov2,0) * a.rectifySign  as creditsov2,  \n"
                + "              \n"
                + "          ifnull(e.sov1+e.sov2,0) * a.rectifySign  as sov3,  \n"
                +
                "            ifnull(e.debitsov1+e.debitsov2,0) * a.rectifySign  as debitsov3,  \n"
                +
                "            ifnull(e.creditsov1+e.creditsov2,0) * a.rectifySign  as creditsov3, \n"
                + "             \n"
                
                +"            ifnull(e.creditsov4,0)* a.rectifySign as creditsov4, \n"
                +"            ifnull(e.debitsov4,0)* a.rectifySign as debitsov4, \n"
                +"            ifnull(e.creditsov5,0)* a.rectifySign as creditsov5, \n"
                +"            ifnull(e.debitsov5,0)* a.rectifySign as debitsov5, \n"
                
                
                
                
                + "           ifnull(e.debitsov1 + e.debitsov2 ,0.00) + a.debitocc as sddebittotalocc, \n"
                + "           ifnull(e.creditsov1 + e.creditsov2 ,0.00) + a.creditocc as sdcredittotalocc, \n"
                
                + "           ifnull(e.initsov,0) * a.rectifySign  as initsov \n"
                
                + "              \n"
                + "     from   \n"
                + "     (  \n"
                + "       select   \n"
                + "              subjectid,accname as subjectname,subjectfullname2 as subjectfullname,sum(if (subyearmonth*12+submonth=${EndYearMonth},balance * (${ptype}),0)) as balance,  \n"
                + "              sum(if (subyearmonth*12+submonth=${StartYearMonth},(debitremain+creditremain) * (${ptype}),0)) /**/ as initbalance,  \n"
                +
                "              sum(debitocc) as debitocc,sum(creditocc) as creditocc,"+rectifySign+" as rectifySign \n"
                + "       from c_account \n"
                +"        where subyearmonth*12+submonth>=${StartYearMonth} and subyearmonth*12+submonth<=${EndYearMonth} \n"
                +"        and isleaf1=1 \n"
                +"        and (subjectfullname2 like '${subjectname}/%' or subjectfullname2 = '${subjectname}')   \n ${strCorrelation1} "
                + "       group by subjectid \n"
                + "       union \n"
                + "       select \n"
                + "            subjectid, \n"
                + "            subjectname as accname, \n"
                + "            subjectfullname as subjectfullname2, \n"
                + "            0 as balance, \n"
                + "            0 as initbalance,  \n"
                + "            0 as  debitocc, \n"
                + "            0 as  creditocc, \n"
                + "            1 as rectifySign \n"
                + "      from z_usesubject \n"
                +"       where projectid=${curProjectid}  \n"
                +"       and tipsubjectid='"+subjectid+"' \n ${strCorrelation2} "
                +"       and isleaf=1 \n"
                + "     ) a  \n"
                + "     /* 调整 */  \n"
                + "     left join  \n"
                + "     (  \n"
                + "         select   \n"
                + "                subjectid,(debittotalocc1 - credittotalocc1)  * (${ptype}) as sov1,  \n"
                +
                "                (debittotalocc2 - credittotalocc2)  * (${ptype})   as sov2,  \n"
                +
                "                debittotalocc1 as debitsov1,  \n"
                +
                "                credittotalocc1 as creditsov1,  \n"
                +
                "                debittotalocc2 as debitsov2,  \n"
                +
                "                credittotalocc2 as creditsov2, \n"
                
                +"                credittotalocc1+credittotalocc4+credittotalocc6 as creditsov4,  \n"
                +"                debittotalocc1+debittotalocc4+debittotalocc6 as debitsov4,  \n"
                +"                credittotalocc2+credittotalocc5 as creditsov5,  \n"
                +"                debittotalocc2+debittotalocc5 as debitsov5,  \n"
                
                + "                (debittotalocc4 - credittotalocc4 + debittotalocc5 - credittotalocc5 + debittotalocc6 - credittotalocc6) * (${ptype}) as initsov \n"
                + "         from z_accountrectify  \n"
                + "         where projectid = ${curProjectid}  \n"
                + "     ) e   \n"
                + "     on a.subjectid =e.subjectid  \n"
		        +" having abs(initbalance)+abs(balance)+abs(debitocc)+abs(creditocc)+abs(sov1)+abs(sov2)+abs(sov3)>0 \n"
		        +" order by subjectid";
        		
    }

}

/*
 select
            a.subjectid,a.subjectname,a.subjectfullname,
            a.balance+ e.initsov * a.rectifySign as balance,
            a.initbalance + e.initsov * a.rectifySign as initbalance,
            a.balance+ (e.sov1+e.sov2+e.initsov) * a.rectifySign as sdbalance,
            a.debitocc,a.creditocc,

             e.sov1 * a.rectifySign as sov1,
             e.sov2 * a.rectifySign  as sov2,
             e.debitsov1 * a.rectifySign  as debitsov1,
             e.creditsov1 * a.rectifySign  as creditsov1,
             e.debitsov2 * a.rectifySign  as debitsov2,
           e.creditsov2 * a.rectifySign  as creditsov2,

           (e.sov1+e.sov2) * a.rectifySign  as sov3,
             (e.debitsov1+e.debitsov2) * a.rectifySign  as debitsov3,
             (e.creditsov1+e.creditsov2) * a.rectifySign  as creditsov3,

            e.initsov * a.rectifySign  as initsov

      from
      (
        select
               subjectid,accname as subjectname,subjectfullname2 as subjectfullname,sum(if (subyearmonth*12+submonth=24096,balance * (1),0)) as balance,
               sum(if (subyearmonth*12+submonth=24085,(debitremain+creditremain) * (1),0))  as initbalance,
               sum(debitocc) as debitocc,sum(creditocc) as creditocc,1 as rectifySign
        from c_account
        where subyearmonth*12+submonth>=24085 and subyearmonth*12+submonth<=24096
        and (subjectfullname2 = concat('银行存款/',accname) or (subjectfullname2 = '银行存款' and isleaf1=1) )
        group by subjectid
        union

        select
             subjectid,
             subjectname as accname,
             subjectfullname as subjectfullname2,
             0 as balance,
             0 as initbalance,
             0 as  debitocc,
             0 as  creditocc,
             1 as rectifySign
       from z_usesubject
       where projectid=2007632
       and (parentSubjectid='1002' or (subjectid ='1002' and isleaf = 1) )
      ) a
      left join
      (
          select
                 subjectid,(debittotalocc1 - credittotalocc1)  * (1) as sov1,
                 (debittotalocc2 - credittotalocc2)  * (1)   as sov2,
                 debittotalocc1  * (1) as debitsov1,
                 credittotalocc1  * (1) as creditsov1,
                 debittotalocc2  * (1)  as debitsov2,
                 credittotalocc2   * (1) as creditsov2,
                 (debittotalocc4 - credittotalocc4 + debittotalocc5 - credittotalocc5 + debittotalocc6 - credittotalocc6) * (1) as initsov
          from z_accountrectify
          where projectid = 2007632
      ) e
      on a.subjectid =e.subjectid


*/
