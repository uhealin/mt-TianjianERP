package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class _1008_59 extends AbstractAreaFunction {


    public ResultSet process(HttpSession session, HttpServletRequest request,
                             HttpServletResponse response, Connection conn,
                             Map args) throws Exception {

//		&subjectNameC=营业费用&startmonth=9&endmonth=9&ptype=1


        String apkID = (String) args.get("curAccPackageID");
        String prjID = (String) args.get("curProjectid");

        String subjectNameC = (String) args.get("subjectNameC");
        String startmonth = (String) args.get("startmonth");
        String endmonth = (String) args.get("endmonth");
        String type = (String) args.get("type");

        String resultSql = "";

        Statement st = null;
        ResultSet rs = null;
        try {
            st = conn.createStatement();

//			查找该科目在客户中的科目id
            String sql = "";
            String subjectid = this.getClientIDByStandName(conn, apkID, prjID,
                    subjectNameC);

//			判断该科目是否叶子并且有自增科目。
            sql = ""
                  + " select * from  \n"
                  + " c_account a \n"
                  + " inner join \n"
                  + " z_usesubject b \n"
                  + " on a.subjectid=b.tipsubjectid \n"
                  + " where a.accpackageid='" + apkID + "' \n"
                  + "   and a.subjectfullname2='" + subjectNameC + "' \n"
                  + "   and a.submonth=1 \n"
                  + "   and a.isleaf1=1 \n"
                  + "   and b.accpackageid='" + apkID + "' \n"
                  + "   and b.projectid='" + prjID + "' \n";
            rs = st.executeQuery(sql);

            if (rs.next()) {
                resultSql = getSql("0", subjectid);
            } else {
                resultSql = getSql("1", subjectid);
            }

            //最终查询结果
            resultSql = this.setSqlArguments(resultSql, args);
            //System.out.println(resultSql);
            rs = st.executeQuery(resultSql);

            return rs;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }

    }

    public String getSql(String rectifySign, String subjectid) {
        return ""
                + "select \n"
                +"           a.subjectid,a.accname as subjectname,a.subjectfullname,a.subjectfullname2,\n"
                +"           a.balance+ ifnull(e.initsov,0) * a.rectifySign as balance,\n"
                +"           a.initbalance + ifnull(e.initsov,0) * a.rectifySign as initbalance,\n"
                +"           a.balance+ ifnull(e.sov1+e.sov2+e.initsov,0) * a.rectifySign as sdbalance,\n"
                +"           a.debitocc,a.creditocc,debittotalocc,credittotalocc,DebitRemain,CreditRemain, \n"
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
                
                + "           ifnull(e.debitsov1 + e.debitsov2 ,0.00) + a.debittotalocc as sddebittotalocc, \n"
                + "           ifnull(e.creditsov1 + e.creditsov2 ,0.00) + a.credittotalocc as sdcredittotalocc, \n"
                
                + "           ifnull(e.initsov,0) * a.rectifySign  as initsov \n"

                + " FROM  \n"

                + " (     \n"
                + "       select  \n"
                + "            subjectid, \n"
                + "            accname, \n"
                + "            subjectfullname1 as subjectfullname, \n"
                + "            subjectfullname2, \n"
                + "            balance * (${ptype}) as balance, \n"
                +
                "            (debitremain+creditremain)* (${ptype})  as initbalance,                    \n"
                + "            debitocc, \n"
                + "            creditocc, \n"
                + "            debittotalocc, \n"
                + "            credittotalocc, \n"
                + "            accpackageid, \n"
                + "            DebitRemain * (${ptype}) as DebitRemain, \n"
                + "            CreditRemain * (${ptype}) as CreditRemain, \n"
                + "            isleaf1 as isleaf, \n"
                + "            " + rectifySign + " as rectifySign \n"
                + "       from c_account a\n"
                + " 		 where submonth >= ${startmonth} \n"
                + " 	     and   submonth <= ${endmonth} \n"
                + "       and   a.isleaf1=1 \n"
                + "       and   accpackageid=${curPackageid} \n"
                + "       and   (a.subjectfullname2 like '${subjectNameC}/%' or a.subjectfullname2 ='${subjectNameC}' )\n"
                + "         \n"
                + "       union \n"
                + "        \n"
                + "       select \n"
                + "            subjectid, \n"
                + "            subjectname as accname, \n"
                + "            subjectfullname , \n"
                + "            subjectfullname as subjectfuallname2, \n"
                + "            0 as balance, \n"
                + "            0 as initbalance, \n"
                + "            0 as  debitocc, \n"
                + "            0 as  creditocc, \n"
                + "            0 as  debittotalocc, \n"
                + "            0 as  credittotalocc, \n"
                + "            accpackageid, \n"
                + "            0 as DebitRemain, \n"
                + "            0 as CreditRemain, \n"
                + "            1 as isleaf, \n"
                + "            1 as rectifySign \n"
                + "      from z_usesubject \n"
                + "      where projectid=${curProjectid}  \n"
                + "      and accpackageid=${curPackageid}  \n"
                + "      and tipsubjectid='" + subjectid + "' \n"
                + "      and isleaf=1 \n"
                + " ) a   \n"
                + " left join \n"
                + "     (  \n"
                + "         select   \n"
                + "                subjectid,(debittotalocc1 - credittotalocc1)  * (${ptype}) as sov1,  \n"
                +
                "                (debittotalocc2 - credittotalocc2)  * (${ptype}) as sov2,  \n"
                +
                "                debittotalocc1   as debitsov1,  \n"
                +
                "                credittotalocc1   as creditsov1,  \n"
                +
                "                debittotalocc2   as debitsov2,  \n"
                +
                "                credittotalocc2  as creditsov2, \n"
                + "                (debittotalocc4 - credittotalocc4 + debittotalocc5 - credittotalocc5 + debittotalocc6 - credittotalocc6) * (${ptype}) as initsov \n"
                + "         from z_accountrectify  \n"
                + "         where projectid = ${curProjectid}  \n"
                + "         and isleaf=1 "
                + "     ) e   \n"
                + " on a.subjectid =e.subjectid  \n"
                + " order by a.subjectid \n"

                + " \n";
    }
}

/**

 select
           a.subjectid,a.accname as subjectname,a.subjectfullname,a.subjectfullname2,
           a.balance+ ifnull(e.initsov,0) * a.rectifySign as balance,
           a.initbalance + ifnull(e.initsov,0) * a.rectifySign as initbalance,
           a.balance+ ifnull(e.sov1+e.sov2+e.initsov,0) * a.rectifySign as sdbalance,
           a.debitocc,a.creditocc,debittotalocc,credittotalocc,DebitRemain  * (1) as DebitRemain,CreditRemain  * (1) as CreditRemain,

            ifnull(e.sov1,0) * a.rectifySign as sov1,
            ifnull(e.sov2,0) * a.rectifySign  as sov2,
            ifnull(e.debitsov1,0) * a.rectifySign  as debitsov1,
            ifnull(e.creditsov1,0) * a.rectifySign  as creditsov1,
            ifnull(e.debitsov2,0) * a.rectifySign  as debitsov2,
          ifnull(e.creditsov2,0) * a.rectifySign  as creditsov2,

          ifnull(e.sov1+e.sov2,0) * a.rectifySign  as sov3,
            ifnull(e.debitsov1+e.debitsov2,0) * a.rectifySign  as debitsov3,
            ifnull(e.creditsov1+e.creditsov2,0) * a.rectifySign  as creditsov3,

           ifnull(e.initsov,0) * a.rectifySign  as initsov
 FROM
 (
       select
            subjectid,
            accname,
            subjectfullname1 as subjectfullname,
            subjectfullname2,
            balance,
            (debitremain+creditremain)  as initbalance,
            debitocc,
            creditocc,
            debittotalocc,
            credittotalocc,
            accpackageid,
            DebitRemain,
            CreditRemain,
            isleaf1 as isleaf,
            1 as rectifySign
       from c_account a
                 where submonth >= 12
             and   submonth <= 12
       and   a.isleaf1=1
       and   accpackageid=1000022007
       and   (a.subjectfullname2 like '无形资产/%' or a.subjectfullname2 ='无形资产' )

       union

       select
            subjectid,
            subjectname as accname,
            subjectfullname ,
            subjectfullname as subjectfuallname2,
            0 as balance,
            0 as initbalance,
            0 as  debitocc,
            0 as  creditocc,
            0 as  debittotalocc,
            0 as  credittotalocc,
            accpackageid,
            0 as DebitRemain,
            0 as CreditRemain,
            1 as isleaf,
            1 as rectifySign
      from z_usesubject
      where projectid=2007632
      and accpackageid=1000022007
      and tipsubjectid='1701'
      and isleaf=1
 ) a
 left join
     (
         select
                subjectid,(debittotalocc1 - credittotalocc1)  * (1) as sov1,
                (debittotalocc2 - credittotalocc2)  * (1) as sov2,
                debittotalocc1  * (1)  as debitsov1,
                credittotalocc1  * (1)  as creditsov1,
                debittotalocc2  * (1)  as debitsov2,
                credittotalocc2   * (1) as creditsov2,
                (debittotalocc4 - credittotalocc4 + debittotalocc5 - credittotalocc5 + debittotalocc6 - credittotalocc6) * (1) as initsov
         from z_accountrectify
         where projectid = 2007632
         and isleaf=1      ) e
 on a.subjectid =e.subjectid
 order by a.subjectid



 */
