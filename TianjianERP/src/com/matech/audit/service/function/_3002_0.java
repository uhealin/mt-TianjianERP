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
 * <p>Title: 取外币,相当于原来的2002，不过参数和返回值的名字和原来相比都有变化 </p>
 * <p>Description:
 * 1、根据科目名称取其底层叶子科目的本位币余额表、外币余额表、本位币调整数、外币调整数
 *   如果没有外币，则外币余额表和外币调整数会和本位币余额表、调整数一摸一样
 *   不取辅助核算。
 *
 *   列刷类型。
 * 2、刷新出的列内容包括：
 *    和1007公式基本一致，但是外币余额表和外币调整数都是原来的值前面多了一个c
 *    本位币取值：
 *    科目或核算编号（subjectid），科目或核算名称（subjectname），期初数（initbalance），
 *    借方发生（debitocc），贷方发生（creditocc），余额（balance），审定数（sdbalance），
 *    年末调整（sov1），年末重分类（sov2），年末总调整数（sov3），年初总调整数（initsov）
 *    年末调整借（debitsov1），年末调整贷（creditsov1），
 *    年末重分类借（debitsov2），年末重分类贷（creditsov2），
 *    年末总调整借（debitsov3），年末总调整贷（creditsov3）
 *    外币取值：
 *    期初数（cinitbalance），
 *    借方发生（cdebitocc），贷方发生（ccreditocc），余额（cbalance），审定数（csdbalance），
 *    年末调整（csov1），年末重分类（csov2），年末总调整数（csov3），年初总调整数（cinitsov）
 *    年末调整借（cdebitsov1），年末调整贷（ccreditsov1），
 *    年末重分类借（cdebitsov2），年末重分类贷（ccreditsov2），
 *    年末总调整借（cdebitsov3），年末总调整贷（ccreditsov3）
 * 3、支持自定义科目；
 * 4、参数录入说明：
 *    I、区间支持2个参数（起始月startmonth、结束月endmonth），若没有提供，就从项目的审计区间取；
 *    II、对应科目参数：subjectname,如果科目参数为空，则从底稿的对应科目取名称；
 *    III、科目余额方向参数：ptype,如果没有设置则取标准科目（或自增科目）的科目方向；
 * </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: matech</p>
 *
 * @author : winnerQ
 * @version 1.0
 */

public class _3002_0 extends AbstractAreaFunction {


    public ResultSet process(HttpSession session, HttpServletRequest request,
                             HttpServletResponse response, Connection conn,
                             Map args) throws Exception {

        String accpackageid = (String) args.get("curAccPackageID");
        String projectid = (String) args.get("curProjectid");

        String resultSql = "";

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
//                args.put("subjectname",subjectname);
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

            //取本位币名称
            st = conn.createStatement();
            resultSql = " select currname from  \n"
                        + " c_accpackage where accpackageid=" + accpackageid ;
            rs = st.executeQuery(resultSql);
            if (rs.next() && rs.getString(1)!=null && !rs.getString(1).equals("")) {
                args.put("currname",rs.getString(1));
            }else{
                args.put("currname","人民币");
            }
            rs.close();
            st.close();

            //判断该科目是否叶子并且有自增科目。
            st = conn.createStatement();
            resultSql = " select 1 from  \n"
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
            //System.out.println("resultSql=" + resultSql);
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
        return "select   \n"
                +"    a.subjectid,a.accname as subjectname,a.subjectfullname2 as subjectfullname,\n"
                +"    a.balance+ifnull(b.initsov,0) * a.rectifySign as balance,\n"
                +"    a.initbalance + ifnull(b.initsov,0) * a.rectifySign as initbalance,\n"
                +"    a.balance+ ifnull(b.sov1+b.sov2+b.initsov,0) * a.rectifySign as sdbalance,\n"
                +"    a.debitocc,a.creditocc,\n"
                +"   \n"
                +"    ifnull(b.sov1,0) * a.rectifySign as sov1,\n"
                +"    ifnull(b.sov2,0) * a.rectifySign  as sov2,  \n"
                +"    ifnull(b.debitsov1,0) * a.rectifySign  as debitsov1,  \n"
                +"    ifnull(b.creditsov1,0) * a.rectifySign  as creditsov1,  \n"
                +"    ifnull(b.debitsov2,0) * a.rectifySign  as debitsov2,  \n"
                +"    ifnull(b.creditsov2,0) * a.rectifySign  as creditsov2,  \n"
                +"    ifnull(b.sov1+b.sov2,0) * a.rectifySign  as sov3,  \n"
                +"    ifnull(b.debitsov1+b.debitsov2,0) * a.rectifySign  as debitsov3,  \n"
                +"    ifnull(b.creditsov1+b.creditsov2,0) * a.rectifySign  as creditsov3,\n"
                +"    ifnull(b.initsov,0) * a.rectifySign  as initsov,\n"
                +"   \n"
                +"    ifnull(c.cdataname,'${currname}') as dataname,\n"
                +"    ifnull(c.cdataname,'${currname}') as currname,\n"
                +"    ifnull(c.cbalance,a.balance) as cbalance,\n"
                +"    ifnull(c.cinitbalance,a.initbalance) as cinitbalance,\n"
                +"    ifnull(c.cdebitocc,a.debitocc) as cdebitocc,\n"
                +"    ifnull(c.ccreditocc,a.creditocc) as ccreditocc,\n"
                +"    \n"
                +"    ifnull(ifnull(d.sov1,b.sov1),0) * a.rectifySign as csov1,  \n"
                +"    ifnull(ifnull(d.sov2,b.sov2),0) * a.rectifySign  as csov2,  \n"
                +"    ifnull(ifnull(d.debitsov1,b.debitsov1),0) * a.rectifySign  as cdebitsov1,  \n"
                +"    ifnull(ifnull(d.creditsov1,b.creditsov1),0) * a.rectifySign  as ccreditsov1,  \n"
                +"    ifnull(ifnull(d.debitsov2,b.debitsov2),0) * a.rectifySign  as cdebitsov2,  \n"
                +"    ifnull(ifnull(d.creditsov2,b.creditsov2),0) * a.rectifySign  as ccreditsov2,  \n"
                +"    ifnull(ifnull(d.sov1,b.sov1)+ifnull(d.sov2,b.sov2),0) * a.rectifySign  as csov3,  \n"
                +"    ifnull(ifnull(d.debitsov1,b.debitsov1)+ifnull(d.debitsov2,b.debitsov2),0) * a.rectifySign  as cdebitsov3,  "
                +"    ifnull(ifnull(d.creditsov1,b.creditsov1)+ifnull(d.creditsov2,b.creditsov2),0) * a.rectifySign  as ccreditsov3,\n"
                +"    ifnull(ifnull(d.initsov,b.initsov),0) * a.rectifySign  as cinitsov \n"
                +"   \n"
                +"from( \n"
                +"        select subjectid,accname,subjectfullname2,'${currname}' as dataname,\n"
                +"                        sum(if (subyearmonth*12+submonth=${EndYearMonth},balance * (${ptype}),0)) as balance,\n"
                +"                        sum(if (subyearmonth*12+submonth=${StartYearMonth},(debitremain+creditremain) * (${ptype}),0)) as initbalance,\n"
                +"                        sum(debitocc) as debitocc,sum(creditocc) as creditocc,"+rectifySign+" as rectifySign \n"
                +"        from c_account \n"
                +"        where isleaf1=1 \n"
                +"        and subyearmonth*12+submonth>=${StartYearMonth} and subyearmonth*12+submonth<=${EndYearMonth} \n"
                +"        and (subjectfullname2 like '${subjectname}/%' or subjectfullname2 = '${subjectname}') \n"
                +"        group by subjectid \n"
                +" \n"
                +"        union \n"
                +" \n"
                +"        select subjectid,subjectname,subjectfullname, '${currname}' as dataname,\n"
                +"                0 as balance, \n"
                +"                0 as initbalance,  \n"
                +"                0 as  debitocc, \n"
                +"                0 as  creditocc, \n"
                +"                1 as rectifySign \n"
                +"        from z_usesubject \n"
                +"        where projectid=${curProjectid}  \n"
                +"        and tipsubjectid='"+subjectid+"' \n"
                +"        and isleaf=1 \n"
                +") a \n"
                /* 本位币调整 */
                +"left join  \n"
                +"(  \n"
                +"        select   \n"
                +"                subjectid,(debittotalocc1 - credittotalocc1)  * (${ptype}) as sov1,  \n"
                +"                (debittotalocc2 - credittotalocc2)  * (${ptype})   as sov2,  \n"
                +"                debittotalocc1  * (${ptype}) as debitsov1,  \n"
                +"                credittotalocc1  * (${ptype}) as creditsov1,  \n"
                +"                debittotalocc2  * (${ptype})  as debitsov2,  \n"
                +"                credittotalocc2   * (${ptype}) as creditsov2, \n"
                +"                (debittotalocc4 - credittotalocc4 + debittotalocc5 - credittotalocc5 + debittotalocc6 - credittotalocc6) * (${ptype}) as initsov \n"
                +"         from z_accountrectify  \n"
                +"         where projectid = ${curProjectid}  \n"
                +") b \n"
                +"on a.subjectid =b.subjectid \n"
                +" \n"
                /*外币余额表*/
                +"left join( \n"
                +"        select subjectid,dataname as cdataname,\n"
                +"                        sum(if (subyearmonth*12+submonth=${EndYearMonth},balance * (${ptype}),0)) as cbalance,\n"
                +"                        sum(if (subyearmonth*12+submonth=${StartYearMonth},(debitremain+creditremain) * (${ptype}),0)) as cinitbalance,\n"
                +"                        sum(debitocc) as cdebitocc,sum(creditocc) as ccreditocc\n"
                +"        from c_accountall where isleaf1=1 \n"
                +"        and dataname<>'${currname}'\n"
                +"        and subyearmonth*12+submonth>=${StartYearMonth} and subyearmonth*12+submonth<=${EndYearMonth} \n"
                +"        and (subjectfullname2 like '${subjectname}/%' or subjectfullname2 = '${subjectname}') \n"
                +"        group by subjectid,dataname\n"
                +") c \n"
                +"on a.subjectid=c.subjectid \n"
                /* 外币调整 */
                +" \n"
                +"left join  \n"
                +"(  \n"
                +"        select   \n"
                +"                subjectid,(debittotalocc1 - credittotalocc1)  * (${ptype}) as sov1,  \n"
                +"                (debittotalocc2 - credittotalocc2)  * (${ptype})   as sov2,  \n"
                +"                 debittotalocc1  * (${ptype}) as debitsov1,  \n"
                +"                credittotalocc1  * (${ptype}) as creditsov1,  \n"
                +"                debittotalocc2  * (${ptype})  as debitsov2,  \n"
                +"                credittotalocc2   * (${ptype}) as creditsov2, \n"
                +"                (debittotalocc4 - credittotalocc4 + debittotalocc5 - credittotalocc5 + debittotalocc6 - credittotalocc6) * (${ptype}) as initsov \n"
                +"         from z_accountallrectify  \n"
                +"         where projectid = ${curProjectid}  \n"
                +")d \n"
                +"on a.subjectid =d.subjectid \n"
                +"order by a.subjectid,a.accname";
    }

}
/*
 select
    a.subjectid,a.accname as subjectname,a.subjectfullname2 as subjectfullname,
    a.balance+b.initsov * a.rectifySign as balance,
    a.initbalance + b.initsov * a.rectifySign as initbalance,
    a.balance+ (b.sov1+b.sov2+b.initsov) * a.rectifySign as sdbalance,
    a.debitocc,a.creditocc,

    b.sov1 * a.rectifySign as sov1,
    b.sov2 * a.rectifySign  as sov2,
    b.debitsov1 * a.rectifySign  as debitsov1,
    b.creditsov1 * a.rectifySign  as creditsov1,
    b.debitsov2 * a.rectifySign  as debitsov2,
    b.creditsov2 * a.rectifySign  as creditsov2,
    (b.sov1+b.sov2) * a.rectifySign  as sov3,
    (b.debitsov1+b.debitsov2) * a.rectifySign  as debitsov3,
    (b.creditsov1+b.creditsov2) * a.rectifySign  as creditsov3,
    b.initsov * a.rectifySign  as initsov,

    ifnull(c.cdataname,'人民币') as dataname,
    ifnull(c.cbalance,a.balance) as cbalance,
    ifnull(c.cinitbalance,a.initbalance) as cinitbalance,
    ifnull(c.cdebitocc,a.debitocc) as cdebitocc,
    ifnull(c.ccreditocc,a.creditocc) as ccreditocc,

    ifnull(d.sov1,b.sov1) * a.rectifySign as csov1,
    ifnull(d.sov2,b.sov2) * a.rectifySign  as csov2,
    ifnull(d.debitsov1,b.debitsov1) * a.rectifySign  as cdebitsov1,
    ifnull(d.creditsov1,b.creditsov1) * a.rectifySign  as ccreditsov1,
    ifnull(d.debitsov2,b.debitsov2) * a.rectifySign  as cdebitsov2,
    ifnull(d.creditsov2,b.creditsov2) * a.rectifySign  as ccreditsov2,
    (ifnull(d.sov1,b.sov1)+ifnull(d.sov2,b.sov2)) * a.rectifySign  as csov3,
    (ifnull(d.debitsov1,b.debitsov1)+ifnull(d.debitsov2,b.debitsov2)) * a.rectifySign  as cdebitsov3,      (ifnull(d.creditsov1,b.creditsov1)+ifnull(d.creditsov2,b.creditsov2)) * a.rectifySign  as ccreditsov3,
    ifnull(d.initsov,b.initsov) * a.rectifySign  as cinitsov

from(
        select subjectid,accname,subjectfullname2,'人民币' as dataname,
                        sum(if (subyearmonth*12+submonth=24096,balance * (1),0)) as balance,
                        sum(if (subyearmonth*12+submonth=24085,(debitremain+creditremain) * (1),0)) as initbalance,
                        sum(debitocc) as debitocc,sum(creditocc) as creditocc,1 as rectifySign
        from c_account
        where isleaf1=1
        and subyearmonth*12+submonth>=24085 and subyearmonth*12+submonth<=24096
        and (subjectfullname2 like '银行存款/%' or subjectfullname2 = '银行存款')
        group by subjectid

        union

        select subjectid,subjectname,subjectfullname, '人民币' as dataname,
                0 as balance,
                0 as initbalance,
                0 as  debitocc,
                0 as  creditocc,
                1 as rectifySign
        from z_usesubject
        where projectid=2007632
        and tipsubjectid='1002'
        and isleaf=1
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
) b
on a.subjectid =b.subjectid

left join(
        select subjectid,dataname as cdataname,
                        sum(if (subyearmonth*12+submonth=24096,balance * (1),0)) as cbalance,
                        sum(if (subyearmonth*12+submonth=24085,(debitremain+creditremain) * (1),0)) as cinitbalance,
                        sum(debitocc) as cdebitocc,sum(creditocc) as ccreditocc
        from c_accountall where isleaf1=1
        and dataname<>'人民币'
        and subyearmonth*12+submonth>=24085 and subyearmonth*12+submonth<=24096
        and (subjectfullname2 like '银行存款/%' or subjectfullname2 = '银行存款')
        group by subjectid,dataname
) c
on a.subjectid=c.subjectid

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
         from z_accountallrectify
         where projectid = 2007632
)d
on a.subjectid =d.subjectid
*/
