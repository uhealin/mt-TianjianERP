package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.framework.pub.db.DbUtil;

/**
 *
 * <p>Title: 取余额明细记录的函数，供批量刷新调用，相当于原来的1007和1029</p>
 * <p>Description:
 * 1、分析余额表以及辅助核算余额表，有后者就优先刷后者，否则刷前者；
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
 *    IV、辅助核算范围参数：allassitem，如果没有设置，则只扫描【客户、供应商、往来】，设置了，就扫描全部辅助核算；
 *    V、关联客户参数：correlation ,不提供则是刷新全部，＝1，只显示关联客户的，＝0以及其他值，显示非关联客户的
 * </p>
 *
 * <p>Copyright: Copyright (c) 2007 matech LTD.</p>
 *
 * <p>Company: matech </p>
 *
 * @author 铭太E审通团队,ESPIERALY THANKS WINNERQ AND PENGYONG
 * @version 1.0
 */

public class _3007_0 extends AbstractAreaFunction {

    public ResultSet process(HttpSession session, HttpServletRequest request,
                             HttpServletResponse response, Connection conn,
                             Map args) throws Exception {

        String accpackageid = (String) args.get("curAccPackageID");
        String projectid = (String) args.get("curProjectid");
        String customerid=accpackageid.substring(0,6);

        String resultSql = "";

        
        //关联客户参数：correlation ,不提供则是刷新全部，＝1，只显示关联客户的，＝0以及其他值，显示非关联客户的
        String correlation=request.getParameter("correlation");
        if (correlation==null){
        	//没有提供
        	correlation=" ";
        }else{
        	//提供了
        	if ( correlation.equals("1")){
        		//只显示关联客户的；
        		correlation=" having exists (\n		select 1 from asdb.k_connectcompanys where k_connectcompanys.customerid="+customerid+" and connectcompanysname=corname\n        )   \n";
        	}else{
        		//显示非关联客户的
        		correlation=" having not exists (\n		select 1 from asdb.k_connectcompanys where k_connectcompanys.customerid="+customerid+" and connectcompanysname=corname\n        )   \n";
        	}
        }
        
        String del=request.getParameter("del");
        
        if("1".equals(del)){
        	if(" ".equals(correlation)){
        		correlation = " having abs(initbalance)+abs(balance)+abs(debitocc)+abs(creditocc)+abs(sov1)+abs(sov2)+abs(sov3)>0 ";
        	}else{
        		correlation += " and abs(initbalance)+abs(balance)+abs(debitocc)+abs(creditocc)+abs(sov1)+abs(sov2)+abs(sov3)>0 ";
        	}
        }
        
        args.put("correlation",correlation);
        
        
        Statement st = null;
        ResultSet rs = null;
        try {
        	st = conn.createStatement();
        	/**
			 * 上年调整影响年末 否：上海立信
			 */
        	String svalue = "";
			String sql = "select svalue from s_config where sname='上年调整影响年末'";
			rs = st.executeQuery(sql);
			while(rs.next()){
				svalue = rs.getString(1);
			}
			DbUtil.close(rs);
        	
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
            
            String strStartYearMonth="",strEndYearMonth="";
            String startmonth = (String) args.get("startmonth");
            String endmonth = (String) args.get("endmonth");
            String strYears ="",strLastYear="";
            if (startmonth==null || startmonth.equals("")
                || endmonth==null || endmonth.equals("")){
                //如果前台没有提供这个参数，就从项目取；
                int[] result=getProjectAuditAreaByProjectid(conn,projectid);
                strStartYearMonth=String.valueOf(result[0]*12+result[1]);
                strEndYearMonth=String.valueOf(result[2]*12+result[3]);

                if (result[0]==result[2]){
                    strYears = " = " + result[0];
                }else{
                    for (int i = result[0]; i <= result[2]; i++) {
                        strYears += "," + String.valueOf(i);
                    }
                    if (strYears.length() > 0) {
                        //去掉最开始得,
                        strYears = " in (" + strYears.substring(1) + ")";
                    }
                }
            }else{
                strStartYearMonth=String.valueOf(Integer.parseInt(accpackageid.substring(6))*12+Integer.parseInt(startmonth));
                strEndYearMonth=String.valueOf(Integer.parseInt(accpackageid.substring(6))*12+Integer.parseInt(endmonth));
                strYears=" = " +accpackageid.substring(6);
                
            }
            
            strLastYear = " = " +accpackageid.substring(6);
            
            
            args.put("StartYearMonth",strStartYearMonth);
            args.put("EndYearMonth",strEndYearMonth);
            args.put("Years",strYears);
            args.put("LastYear",strLastYear);


            //查找该科目在客户中的科目id,请注意即使有一对多的科目，这里也只是取其中的一条；
            String[] result=this.getClientIDAndDirectionByStandName(conn, accpackageid, projectid,
                    subjectname);
            String subjectid = result[0];

            //如果没有提供方向这个参数，则取科目余额方向
            String ptype = (String) args.get("ptype");
            if (ptype==null||ptype.equals("")){
                args.put("ptype",result[1]);
            }

            //判断该科目是否叶子并且有自增科目。
           
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

            if("否".equals(svalue)){
            	if (rs.next()) {
                    resultSql = getSql1("0", subjectid);
                } else {
                    resultSql = getSql1("1", subjectid);
                }
            }else{
            	if (rs.next()) {
                    resultSql = getSql("0", subjectid);
                } else {
                    resultSql = getSql("1", subjectid);
                }
            }
            

            String sqlassitem = "select distinct asstotalname from c_assitem where accpackageid='" + accpackageid + "' and Level0=1 " +
    		" and ( asstotalname like '%客户%' or asstotalname like '%供应商%' or asstotalname like '%费用%' or asstotalname like '%往来%' ) ";
    
		    rs = st.executeQuery(sqlassitem);
		    String sqlstring = "";
		    while(rs.next()){
		    	sqlstring += " asstotalname1 like '"+rs.getString(1)+"/%' or" ;
		    }
		    if(!"".equals(sqlstring)){
		    	sqlstring = " and ( " + sqlstring.substring(0,sqlstring.length()-2)+ ") ";
		    }else{
		    	sqlstring = " and 1=2 ";
		    }
		    
    
            //设置辅助核算扫描范围的参数
            String allassitem=request.getParameter("allassitem");
            if (allassitem==null || allassitem.equals("")){
                //只扫描往来、客户、供应商
//                args.put("allassitem","       and  \n       ( \n           asstotalname1 like '%客户%' \n           or asstotalname1 like '%供应商%' \n           or asstotalname1 like '%关联%' \n           or asstotalname1 like '%往来%' \n       ) \n");
                args.put("allassitem",sqlstring);
            }else{
                //扫描全部辅助核算
                args.put("allassitem"," ");
            }

            //最终查询结果
            resultSql = this.setSqlArguments(resultSql, args);
            org.util.Debug.prtOut("qwh:3007:resultSql="+resultSql);
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
    public String getSql1(String rectifySign,String subjectid){
                return ""
                +" select   \n"
                +"        IF(b2.assitemid is null,a2.subjectid,b2.assitemid) as subjectid,  \n"
                +"        IF(b2.assitemid is null,a2.accname,concat(a2.accname,'/',b2.assitemname)) as subjectname,  \n"
                +"          \n"
                +"        IF(b2.assitemid is null,a2.initbalance,b2.initbalance) initbalance1,  \n"
                
                //+"        IF(b2.assitemid is null,a2.initbalance + a2.initsov,b2.initbalance + b2.initsov) as initbalance,  \n"
                +"        IF(b2.assitemid is null,a2.initbalance ,b2.initbalance ) as initbalance,  \n"
                
                +"        IF(b2.assitemid is null,a2.balance,b2.balance) as balance,  \n"
                +"        IF(b2.assitemid is null,a2.balance,b2.balance ) as accbalance,  \n"
                +"        IF(b2.assitemid is null,a2.balance+a2.sov3,b2.balance+b2.sov3 ) as sdbalance,  \n"
                +"  \n"
                +"        IF(b2.assitemid is null,a2.debitocc,b2.debitocc) as debitocc,  \n"
                +"        IF(b2.assitemid is null,a2.creditocc,b2.creditocc) as creditocc,  \n"
                +"          \n"
                +"  \n"
                +"                               \n"
                +"        IF(b2.assitemid is null,a2.sov1,b2.sov1) * a2.rectifySign as sov1,  \n"
                +"        IF(b2.assitemid is null,a2.sov2,b2.sov2) * a2.rectifySign as sov2,  \n"
                +"        IF(b2.assitemid is null,a2.sov3,b2.sov3) * a2.rectifySign as sov3, \n"
                +"        IF(b2.assitemid is null,a2.debitsov1,b2.debitsov1) * a2.rectifySign as debitsov1,  \n"
                +"        IF(b2.assitemid is null,a2.creditsov1,b2.creditsov1) * a2.rectifySign as creditsov1,  \n"
                +"        IF(b2.assitemid is null,a2.debitsov2,b2.debitsov2) * a2.rectifySign as debitsov2,  \n"
                +"        IF(b2.assitemid is null,a2.creditsov2,b2.creditsov2) * a2.rectifySign as creditsov2, \n"
                +"        IF(b2.assitemid is null,a2.debitsov3,b2.debitsov3) * a2.rectifySign as debitsov3,  \n"
                +"        IF(b2.assitemid is null,a2.creditsov3,b2.creditsov3) * a2.rectifySign as creditsov3, \n"
                +"        IF(b2.assitemid is null,a2.initsov,b2.initsov) * a2.rectifySign as initsov, \n"
                
                
                +"        IF(b2.assitemid is null,a2.creditsov4,b2.creditsov4) * a2.rectifySign as creditsov4,  \n"
                +"        IF(b2.assitemid is null,a2.debitsov4,b2.debitsov4) * a2.rectifySign as debitsov4,  \n"
                +"        IF(b2.assitemid is null,a2.creditsov5,b2.creditsov5) * a2.rectifySign as creditsov5,  \n"
                +"        IF(b2.assitemid is null,a2.debitsov5,b2.debitsov5) * a2.rectifySign as debitsov5,  \n"
                
                
                +"        IF(b2.assitemid is null,a2.debitsov1+a2.debitsov2+a2.debitocc,b2.debitsov1+b2.debitsov2+b2.debitocc) * a2.rectifySign as sddebittotalocc,  \n"
                +"        IF(b2.assitemid is null,a2.creditsov1+a2.creditsov2+a2.creditocc,b2.creditsov1+b2.creditsov2+b2.creditocc) * a2.rectifySign as sdcredittotalocc, \n"
                
                +"        IF(b2.assitemid is null,a2.DebitBalance,b2.DebitBalance)  as sdDebitBalance, \n"
                +"        IF(b2.assitemid is null,a2.CreditBalance,b2.CreditBalance) as sdCreditBalance, \n"
                
                +"        IF(b2.assitemid is null,a2.accname,b2.assitemname) as corname,     \n"
                +"          \n"
                +"        IF(b2.assitemid is null,1,2) as orderid       \n"
                +"   \n"
                +"   \n"
                +" FROM  \n"
                +" (   /* a2是余额表的数据 */  \n"
                +"     select   \n"
                +"            a.*,b.accid, \n"
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
                +"              subjectid,accname,subjectfullname2,sum(if (subyearmonth*12+submonth=${EndYearMonth},balance * (${ptype}),0)) as balance,  \n"

                +"				sum(if (subyearmonth*12+submonth=${EndYearMonth},DebitBalance,0)) as DebitBalance, \n"
                +"				sum(if (subyearmonth*12+submonth=${EndYearMonth},(-1)*CreditBalance,0)) as CreditBalance, \n"				
                
                +"              sum(if (subyearmonth*12+submonth=${StartYearMonth},(debitremain+creditremain) * (${ptype}),0)) /**/ as initbalance,  \n"
                +"              sum(debitocc) as debitocc,sum(creditocc) as creditocc,"+rectifySign+" as rectifySign \n"
                +"       from c_account \n"
                +"       where subyearmonth*12+submonth>=${StartYearMonth} and subyearmonth*12+submonth<=${EndYearMonth} \n"
                +"       and isleaf1=1 \n"
                +"       and (subjectfullname2 like '${subjectname}/%' or subjectfullname2 = '${subjectname}')   \n"
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
                +"      and tipsubjectid='"+subjectid+"' \n"
                +"      and isleaf=1 \n"
                +"     ) a  \n"
                +"       \n"
                +"       \n"
                +"     left join  \n"
                +"     ( /*判断取辅助核算还是科目，就看这个表了。条件是1月份的余额是否相等*/  \n"
                +"      \n"
                +"       select  \n"
                +"             distinct accid  \n"
                +"       from c_assitementryacc  \n"
                +"       where subyearmonth ${LastYear}  \n"
                +"       and submonth=1 \n"
                +"       and isleaf1=1  ${allassitem} \n"
                +"     ) b  \n"
                +"     on a.subjectid=b.accid  \n"
                +"  \n"
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
                
                //(DebitTotalOcc1 + DebitTotalOcc2 + DebitTotalOcc4 + DebitTotalOcc5 + DebitTotalOcc6)
                
                +"         from z_accountrectify  \n"
                +"         where projectid = ${curProjectid}  \n"
                +"         and isleaf=1 "
                +"     ) e   \n"
                +"     on a.subjectid =e.subjectid  \n"
                +"       \n"
                +" ) a2  \n"
                +" left join  \n"
                +" (  \n"
                +"     /* b2 是辅助核算 */  \n"
                +"     SELECT   \n"
                +"            a.*,  \n"
                +"              \n"
                +"            ifnull(c.sov1,0) as sov1,  \n"
                +"            ifnull(c.sov2,0) as sov2,  \n"
                +"            ifnull(c.debitsov1,0) as debitsov1,  \n"
                +"            ifnull(c.creditsov1,0) as creditsov1,  \n"
                +"            ifnull(c.debitsov2,0) as debitsov2,  \n"
                +"            ifnull(c.creditsov2,0) as creditsov2,  \n"
                +"             \n"
                +"            ifnull(c.sov1+c.sov2,0) as sov3,  \n"
                +"            ifnull(c.debitsov1+c.debitsov2,0) as debitsov3,  \n"
                +"            ifnull(c.creditsov1+c.creditsov2,0) as creditsov3, \n"
                
                +"            ifnull(c.creditsov4,0) as creditsov4, \n"
                +"            ifnull(c.debitsov4,0) as debitsov4, \n"
                +"            ifnull(c.creditsov5,0) as creditsov5, \n"
                +"            ifnull(c.debitsov5,0) as debitsov5, \n"
                
                +"             \n"
                +"            ifnull(c.initsov,0) as initsov \n"
                +"     FROM  \n"
                +"     (  \n"
                +"         select   \n"
                +"                accid,assitemid,assitemname,  \n"
                +"                sum(if (subyearmonth*12+submonth=${EndYearMonth},balance  * (${ptype}),0)) as balance,  \n"

                +"					sum(if (subyearmonth*12+submonth=${EndYearMonth},DebitBalance,0)) as DebitBalance, \n"
                +"					sum(if (subyearmonth*12+submonth=${EndYearMonth},(-1)*CreditBalance,0)) as CreditBalance, \n"				

                +"                sum(if (subyearmonth*12+submonth=${StartYearMonth},(debitremain+creditremain) * (${ptype}),0)) /**/ as initbalance,  \n"
                +"                sum(debitocc) as debitocc, sum(creditocc ) as creditocc  \n"
                +"         from c_assitementryacc  \n"
                +"         where subyearmonth*12+submonth>=${StartYearMonth} and subyearmonth*12+submonth<=${EndYearMonth}  \n"
                +"         and isleaf1=1  ${allassitem} \n"
                +"         group by accid,assitemid \n"
                +"          \n"
                +"     ) a  \n"
                +"     /* 调整 */  \n"
                +"     left join   \n"
                +"     (     \n"
                +"         select subjectid,assitemid,(debittotalocc1 - credittotalocc1)  * (${ptype}) as sov1,(debittotalocc2 - credittotalocc2)  * (${ptype}) as sov2,  \n"
                +"         debittotalocc1 as debitsov1,  \n"
                +"         credittotalocc1 as creditsov1,  \n"
                +"         debittotalocc2 as debitsov2,  \n"
                +"         credittotalocc2 as creditsov2, \n"
                +"         (debittotalocc4 - credittotalocc4 + debittotalocc5 - credittotalocc5 + debittotalocc6 - credittotalocc6) * (${ptype}) as initsov, \n"
                
                
                +"                credittotalocc1+credittotalocc4+credittotalocc6 as creditsov4,  \n"
                +"                debittotalocc1+debittotalocc4+debittotalocc6 as debitsov4,  \n"
                +"                credittotalocc2+credittotalocc5 as creditsov5,  \n"
                +"                debittotalocc2+debittotalocc5 as debitsov5  \n"
                
                
                
                +"         from z_assitemaccrectify  \n"
                +"         where projectid =  ${curProjectid}  \n"
                +"  \n"
                +"     ) c   \n"
                +"     on a.accid =c.subjectid and a.assitemid=c.assitemid  \n"
                +" ) b2  \n"
                +" on a2.accid=b2.accid  \n"
               // +" having abs(initbalance)+abs(balance)+abs(debitocc)+abs(creditocc)+abs(sov1)+abs(sov2)+abs(sov3)>0 \n"
                +" ${correlation}  order by orderid,a2.subjectid,b2.assitemid";
        }

    public String getSql(String rectifySign,String subjectid){
        return ""
        +" select   \n"
        +"        IF(b2.assitemid is null,a2.subjectid,b2.assitemid) as subjectid,  \n"
        +"        IF(b2.assitemid is null,a2.accname,concat(a2.accname,'/',b2.assitemname)) as subjectname,  \n"
        +"          \n"
        +"        IF(b2.assitemid is null,a2.initbalance,b2.initbalance) initbalance1,  \n"
        
        +"        IF(b2.assitemid is null,a2.initbalance + a2.initsov,b2.initbalance + b2.initsov) as initbalance,  \n"
        +"        IF(b2.assitemid is null,a2.balance+ a2.initsov,b2.balance + b2.initsov) as balance,  \n"
        +"        IF(b2.assitemid is null,a2.balance,b2.balance ) as accbalance,  \n"
        +"        IF(b2.assitemid is null,a2.balance+a2.sov3+ a2.initsov,b2.balance+b2.sov3 + b2.initsov) as sdbalance,  \n"
        +"  \n"
        +"        IF(b2.assitemid is null,a2.debitocc,b2.debitocc) as debitocc,  \n"
        +"        IF(b2.assitemid is null,a2.creditocc,b2.creditocc) as creditocc,  \n"
        +"          \n"
        +"  \n"
        +"                               \n"
        +"        IF(b2.assitemid is null,a2.sov1,b2.sov1) * a2.rectifySign as sov1,  \n"
        +"        IF(b2.assitemid is null,a2.sov2,b2.sov2) * a2.rectifySign as sov2,  \n"
        +"        IF(b2.assitemid is null,a2.sov3,b2.sov3) * a2.rectifySign as sov3, \n"
        +"        IF(b2.assitemid is null,a2.debitsov1,b2.debitsov1) * a2.rectifySign as debitsov1,  \n"
        +"        IF(b2.assitemid is null,a2.creditsov1,b2.creditsov1) * a2.rectifySign as creditsov1,  \n"
        +"        IF(b2.assitemid is null,a2.debitsov2,b2.debitsov2) * a2.rectifySign as debitsov2,  \n"
        +"        IF(b2.assitemid is null,a2.creditsov2,b2.creditsov2) * a2.rectifySign as creditsov2, \n"
        +"        IF(b2.assitemid is null,a2.debitsov3,b2.debitsov3) * a2.rectifySign as debitsov3,  \n"
        +"        IF(b2.assitemid is null,a2.creditsov3,b2.creditsov3) * a2.rectifySign as creditsov3, \n"
        +"        IF(b2.assitemid is null,a2.initsov,b2.initsov) * a2.rectifySign as initsov, \n"
        
        
        +"        IF(b2.assitemid is null,a2.creditsov4,b2.creditsov4) * a2.rectifySign as creditsov4,  \n"
        +"        IF(b2.assitemid is null,a2.debitsov4,b2.debitsov4) * a2.rectifySign as debitsov4,  \n"
        +"        IF(b2.assitemid is null,a2.creditsov5,b2.creditsov5) * a2.rectifySign as creditsov5,  \n"
        +"        IF(b2.assitemid is null,a2.debitsov5,b2.debitsov5) * a2.rectifySign as debitsov5,  \n"
        
        
        +"        IF(b2.assitemid is null,a2.debitsov1+a2.debitsov2+a2.debitocc,b2.debitsov1+b2.debitsov2+b2.debitocc) * a2.rectifySign as sddebittotalocc,  \n"
        +"        IF(b2.assitemid is null,a2.creditsov1+a2.creditsov2+a2.creditocc,b2.creditsov1+b2.creditsov2+b2.creditocc) * a2.rectifySign as sdcredittotalocc, \n"
        
        +"        IF(b2.assitemid is null,a2.DebitBalance+(a2.debitsov4+a2.debitsov5)* a2.rectifySign,b2.DebitBalance+(b2.debitsov4+b2.debitsov5) * a2.rectifySign)  as sdDebitBalance, \n"
        +"        IF(b2.assitemid is null,a2.CreditBalance+(a2.creditsov4+a2.creditsov5) * a2.rectifySign,b2.CreditBalance+(b2.creditsov4+b2.creditsov5) * a2.rectifySign) as sdCreditBalance, \n"
        
        +"         IF(b2.assitemid is null,a2.accname,b2.assitemname) as corname,     \n"
        +"          \n"
        +"        IF(b2.assitemid is null,1,2) as orderid       \n"
        +"   \n"
        +"   \n"
        +" FROM  \n"
        +" (   /* a2是余额表的数据 */  \n"
        +"     select   \n"
        +"            a.*,b.accid, \n"
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
        +"              subjectid,accname,subjectfullname2,sum(if (subyearmonth*12+submonth=${EndYearMonth},balance * (${ptype}),0)) as balance,  \n"

        +"				sum(if (subyearmonth*12+submonth=${EndYearMonth},DebitBalance,0)) as DebitBalance, \n"
        +"				sum(if (subyearmonth*12+submonth=${EndYearMonth},(-1)*CreditBalance,0)) as CreditBalance, \n"				
        
        +"              sum(if (subyearmonth*12+submonth=${StartYearMonth},(debitremain+creditremain) * (${ptype}),0)) /**/ as initbalance,  \n"
        +"              sum(debitocc) as debitocc,sum(creditocc) as creditocc,"+rectifySign+" as rectifySign \n"
        +"       from c_account \n"
        +"       where subyearmonth*12+submonth>=${StartYearMonth} and subyearmonth*12+submonth<=${EndYearMonth} \n"
        +"       and isleaf1=1 \n"
        +"       and (subjectfullname2 like '${subjectname}/%' or subjectfullname2 = '${subjectname}')   \n"
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
        +"      and tipsubjectid='"+subjectid+"' \n"
        +"      and isleaf=1 \n"
        +"     ) a  \n"
        +"       \n"
        +"       \n"
        +"     left join  \n"
        +"     ( /*判断取辅助核算还是科目，就看这个表了。条件是1月份的余额是否相等*/  \n"
        +"      \n"
        +"       select  \n"
        +"             distinct accid  \n"
        +"       from c_assitementryacc  \n"
        +"       where subyearmonth ${LastYear}  \n"
        +"       and submonth=1 \n"
        +"       and isleaf1=1  ${allassitem} \n"
        +"     ) b  \n"
        +"     on a.subjectid=b.accid  \n"
        +"  \n"
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
        
        //(DebitTotalOcc1 + DebitTotalOcc2 + DebitTotalOcc4 + DebitTotalOcc5 + DebitTotalOcc6)
        
        +"         from z_accountrectify  \n"
        +"         where projectid = ${curProjectid}  \n"
        +"         and isleaf=1 "
        +"     ) e   \n"
        +"     on a.subjectid =e.subjectid  \n"
        +"       \n"
        +" ) a2  \n"
        +" left join  \n"
        +" (  \n"
        +"     /* b2 是辅助核算 */  \n"
        +"     SELECT   \n"
        +"            a.*,  \n"
        +"              \n"
        +"            ifnull(c.sov1,0) as sov1,  \n"
        +"            ifnull(c.sov2,0) as sov2,  \n"
        +"            ifnull(c.debitsov1,0) as debitsov1,  \n"
        +"            ifnull(c.creditsov1,0) as creditsov1,  \n"
        +"            ifnull(c.debitsov2,0) as debitsov2,  \n"
        +"            ifnull(c.creditsov2,0) as creditsov2,  \n"
        +"             \n"
        +"            ifnull(c.sov1+c.sov2,0) as sov3,  \n"
        +"            ifnull(c.debitsov1+c.debitsov2,0) as debitsov3,  \n"
        +"            ifnull(c.creditsov1+c.creditsov2,0) as creditsov3, \n"
        
        +"            ifnull(c.creditsov4,0) as creditsov4, \n"
        +"            ifnull(c.debitsov4,0) as debitsov4, \n"
        +"            ifnull(c.creditsov5,0) as creditsov5, \n"
        +"            ifnull(c.debitsov5,0) as debitsov5, \n"
        
        +"             \n"
        +"            ifnull(c.initsov,0) as initsov \n"
        +"     FROM  \n"
        +"     (  \n"
        +"         select   \n"
        +"                accid,assitemid,assitemname,  \n"
        +"                sum(if (subyearmonth*12+submonth=${EndYearMonth},balance  * (${ptype}),0)) as balance,  \n"

        +"					sum(if (subyearmonth*12+submonth=${EndYearMonth},DebitBalance,0)) as DebitBalance, \n"
        +"					sum(if (subyearmonth*12+submonth=${EndYearMonth},(-1)*CreditBalance,0)) as CreditBalance, \n"				

        +"                sum(if (subyearmonth*12+submonth=${StartYearMonth},(debitremain+creditremain) * (${ptype}),0)) /**/ as initbalance,  \n"
        +"                sum(debitocc) as debitocc, sum(creditocc ) as creditocc  \n"
        +"         from c_assitementryacc  \n"
        +"         where subyearmonth*12+submonth>=${StartYearMonth} and subyearmonth*12+submonth<=${EndYearMonth}  \n"
        +"         and isleaf1=1  ${allassitem} \n"
        +"         group by accid,assitemid \n"
        +"          \n"
        +"     ) a  \n"
        +"     /* 调整 */  \n"
        +"     left join   \n"
        +"     (     \n"
        +"         select subjectid,assitemid,(debittotalocc1 - credittotalocc1)  * (${ptype}) as sov1,(debittotalocc2 - credittotalocc2)  * (${ptype}) as sov2,  \n"
        +"         debittotalocc1 as debitsov1,  \n"
        +"         credittotalocc1 as creditsov1,  \n"
        +"         debittotalocc2 as debitsov2,  \n"
        +"         credittotalocc2 as creditsov2, \n"
        +"         (debittotalocc4 - credittotalocc4 + debittotalocc5 - credittotalocc5 + debittotalocc6 - credittotalocc6) * (${ptype}) as initsov, \n"
        
        
        +"                credittotalocc1+credittotalocc4+credittotalocc6 as creditsov4,  \n"
        +"                debittotalocc1+debittotalocc4+debittotalocc6 as debitsov4,  \n"
        +"                credittotalocc2+credittotalocc5 as creditsov5,  \n"
        +"                debittotalocc2+debittotalocc5 as debitsov5  \n"
        
        
        
        +"         from z_assitemaccrectify  \n"
        +"         where projectid =  ${curProjectid}  \n"
        +"  \n"
        +"     ) c   \n"
        +"     on a.accid =c.subjectid and a.assitemid=c.assitemid  \n"
        +" ) b2  \n"
        +" on a2.accid=b2.accid  \n"
       // +" having abs(initbalance)+abs(balance)+abs(debitocc)+abs(creditocc)+abs(sov1)+abs(sov2)+abs(sov3)>0 \n"
        +" ${correlation}  order by orderid,a2.subjectid,b2.assitemid";
}


    
    
    public ResultSet process1(HttpSession session, HttpServletRequest request,
            HttpServletResponse response, Connection conn,
            Map args) throws Exception {

			String accpackageid = (String) args.get("curAccPackageID");
			String projectid = (String) args.get("curProjectid");
			String customerid=accpackageid.substring(0,6);
			
			String resultSql = "";
			
			
			//关联客户参数：correlation ,不提供则是刷新全部，＝1，只显示关联客户的，＝0以及其他值，显示非关联客户的
			String correlation=request.getParameter("correlation");
			if (correlation==null){
			//没有提供
			correlation=" ";
			}else{
			//提供了
				if ( correlation.equals("1")){
	        		//只显示关联客户的；
	        		correlation=" having exists (\n		select 1 from asdb.k_connectcompanys where k_connectcompanys.customerid="+customerid+" and connectcompanysname=accname\n        )   \n";
	        	}else{
	        		//显示非关联客户的
	        		correlation=" having not exists (\n		select 1 from asdb.k_connectcompanys where k_connectcompanys.customerid="+customerid+" and connectcompanysname=accname\n        )   \n";
	        	}
			}	
			args.put("correlation",correlation);
			
			
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
			
			//args.put("subjectname",subjectname);
			
			}
			
			String sName = changeSubjectName(conn,projectid,subjectname);
			if(!"".equals(sName)){
				subjectname = sName; 
			}            
			args.put("subjectname",subjectname);
			
//			String strStartYearMonth="",strEndYearMonth="";
//			String startmonth = (String) args.get("startmonth");
//			String endmonth = (String) args.get("endmonth");
//			String strYears ="";
//			if (startmonth==null || startmonth.equals("")
//			|| endmonth==null || endmonth.equals("")){
//			//如果前台没有提供这个参数，就从项目取；
//			int[] result=getProjectAuditAreaByProjectid(conn,projectid);
//			strStartYearMonth=String.valueOf(result[0]*12+result[1]);
//			strEndYearMonth=String.valueOf(result[2]*12+result[3]);
//			
//			if (result[0]==result[2]){
//			   strYears = " = " + result[0];
//			}else{
//			   for (int i = result[0]; i <= result[2]; i++) {
//			       strYears += "," + String.valueOf(i);
//			   }
//			   if (strYears.length() > 0) {
//			       //去掉最开始得,
//			       strYears = " in (" + strYears.substring(1) + ")";
//			   }
//			}
//			}else{
//				strStartYearMonth=String.valueOf(Integer.parseInt(accpackageid.substring(6))*12+Integer.parseInt(startmonth));
//				strEndYearMonth=String.valueOf(Integer.parseInt(accpackageid.substring(6))*12+Integer.parseInt(endmonth));
//				strYears=" = " +accpackageid.substring(6);
//			}
//			args.put("StartYearMonth",strStartYearMonth);
//			args.put("EndYearMonth",strEndYearMonth);
//			args.put("Years",strYears);
			
			
			//查找该科目在客户中的科目id,请注意即使有一对多的科目，这里也只是取其中的一条；
			String[] result=this.getClientIDAndDirectionByStandName(conn, accpackageid, projectid,
			   subjectname);
			String subjectid = result[0];
			
			//如果没有提供方向这个参数，则取科目余额方向
			String ptype = (String) args.get("ptype");
			if (ptype==null||ptype.equals("")){
				args.put("ptype",result[1]);
			}
			
			//判断该科目是否叶子并且有自增科目。
			st = conn.createStatement();
//			resultSql = ""
//			 + " select 1 from  \n"
//			 + " c_account a \n"
//			 + " inner join \n"
//			 + " z_usesubject b \n"
//			 + " on a.subjectid=b.tipsubjectid \n"
//			 + " where a.accpackageid='" + accpackageid + "' \n"
//			 + "   and a.subjectfullname2='" + subjectname + "' \n"
//			 + "   and a.submonth=1 \n"
//			 + "   and a.isleaf1=1 \n"
//			 + "   and b.accpackageid='" + accpackageid + "' \n"
//			 + "   and b.projectid='" + projectid + "' \n";
//			rs = st.executeQuery(resultSql);
			
//			if (rs.next()) {
//			resultSql = getSql("0", subjectid);
//			} else {
//			resultSql = getSql("1", subjectid);
//			}
			
			
			resultSql = getSql();
			
			//设置辅助核算扫描范围的参数
			String allassitem=request.getParameter("allassitem");
			if (allassitem==null || allassitem.equals("")){
			//只扫描往来、客户、供应商
			//	args.put("allassitem","       and  \n       ( \n           asstotalname1 like '%客户%' \n           or asstotalname1 like '%供应商%' \n           or asstotalname1 like '%关联%' \n           or asstotalname1 like '%往来%' \n       ) \n");
				args.put("allassitem","  and (assitemid ='' or ( assitemid <>'' and ( subjectfullname2 like '%客户%' or subjectfullname2 like '%供应商%' or subjectfullname2 like '%费用%' or subjectfullname2 like '%往来%' ) )) ");
			}else{
			//扫描全部辅助核算
				args.put("allassitem"," ");
			}
			
			//最终查询结果
			resultSql = this.setSqlArguments(resultSql, args);
			org.util.Debug.prtOut("qwh:3007:resultSql="+resultSql);
			rs = st.executeQuery(resultSql);
			
			return rs;
		} catch (Exception e) {
		e.printStackTrace();
		throw new Exception(e.getMessage());
		}
	}

    public String getSql(){
    	return "" +
    		" select " +
    		" case when a.assitemid='' then a.subjectid else a.assitemid end subjectid, \n" +
    		" case when a.assitemid='' then a.subjectname else concat(a.accname1,'/',a.subjectname) end subjectname, \n" +
    		" direction2 * initbalance initbalance1,  \n" +
    		" (debitremain + (DebitTotalOcc4 + DebitTotalOcc5 + DebitTotalOcc6) * rectifySign ) initDebitbalance,  \n" +
    		" ((-1)*creditremain + (CreditTotalOcc4 + CreditTotalOcc5 + CreditTotalOcc6) * rectifySign ) initCreditbalance,  \n" +
    		" direction2 * (initbalance + ((DebitTotalOcc4 - CreditTotalOcc4) + (DebitTotalOcc5-CreditTotalOcc5) + (DebitTotalOcc6-CreditTotalOcc6)) * rectifySign) initbalance,  \n" +
    		
    		" DebitTotalOcc  debitocc,  \n" +
    		" (DebitTotalOcc + (DebitTotalOcc1 + DebitTotalOcc2) * rectifySign ) sddebittotalocc,  \n" +
    		" (DebitTotalOcc + (DebitTotalOcc1 + DebitTotalOcc2 + DebitTotalOcc4 + DebitTotalOcc5 + DebitTotalOcc6) * rectifySign ) sddebittotalocc1,  \n" +
    		
    		" CreditTotalOcc creditocc,	  \n" +
    		" (CreditTotalOcc + (CreditTotalOcc1 + CreditTotalOcc2) * rectifySign) sdcredittotalocc,  \n" +
    		" (CreditTotalOcc + (CreditTotalOcc1 + CreditTotalOcc2 + CreditTotalOcc4 + CreditTotalOcc5 + CreditTotalOcc6) * rectifySign ) sdcredittotalocc1,  \n" +
    		
    		" direction2 * Balance accbalance,  \n" +
    		" direction2 * (Balance + (DebitTotalOcc4 + DebitTotalOcc5 + DebitTotalOcc6 - (CreditTotalOcc4 + CreditTotalOcc5 + CreditTotalOcc6)) * rectifySign ) balance, \n" +
    		" (DebitBalance + (DebitTotalOcc1 + DebitTotalOcc2) * rectifySign) sdDebitBalance,	  \n" +
    		" (DebitBalance + (DebitTotalOcc1 + DebitTotalOcc2 + DebitTotalOcc4 + DebitTotalOcc5 + DebitTotalOcc6) * rectifySign ) sdDebitBalance1,  \n" +
    		" ((-1)*CreditBalance + (CreditTotalOcc1 + CreditTotalOcc2) * rectifySign) sdCreditbalance,  \n" +
    		" ((-1)*CreditBalance + (CreditTotalOcc1 + CreditTotalOcc2 + CreditTotalOcc4 + CreditTotalOcc5 + CreditTotalOcc6) * rectifySign) sdCreditbalance1,  \n" +
    		" direction2 * (Balance + (DebitTotalOcc1 + DebitTotalOcc2 - (CreditTotalOcc1 + CreditTotalOcc2)) * rectifySign ) sdBalance1,  \n" +
    		" direction2 * (Balance + (DebitTotalOcc1 + DebitTotalOcc2 + DebitTotalOcc4 + DebitTotalOcc5 + DebitTotalOcc6 - (CreditTotalOcc1 + CreditTotalOcc2 + CreditTotalOcc4 + CreditTotalOcc5 + CreditTotalOcc6)) * rectifySign ) sdBalance,  \n" +
    		
    		" direction2 * (debittotalocc4 - credittotalocc4 + debittotalocc5 - credittotalocc5 + debittotalocc6 - credittotalocc6) * rectifySign as initsov,  \n" +
    		
    		" debittotalocc1 * rectifySign as debitsov1,    \n" +
    		" credittotalocc1 * rectifySign as creditsov1,  \n" +
    		" debittotalocc2 * rectifySign as debitsov2,    \n" +
    		" credittotalocc2 * rectifySign as creditsov2,   \n" +
    		" (debittotalocc1 + debittotalocc2) * rectifySign as debitsov3,    \n" +
    		" (credittotalocc1 + credittotalocc2) * rectifySign as creditsov3,   \n" +
    		" (debittotalocc1+debittotalocc4+debittotalocc6) * rectifySign as debitsov4,  \n" +
    		" (credittotalocc1+credittotalocc4+credittotalocc6) * rectifySign as creditsov4,  \n" +
    		" (debittotalocc2+debittotalocc5) * rectifySign as debitsov5,  \n" +
    		" (credittotalocc2+credittotalocc5) * rectifySign as creditsov5,  \n" +
    		
    		" direction2 *(debittotalocc1 - credittotalocc1) * rectifySign sov1,  \n" +
    		" direction2 *(debittotalocc2 - credittotalocc2) * rectifySign sov2,  \n" +
    		" direction2 *(debittotalocc1 - credittotalocc1 + debittotalocc2 - credittotalocc2) * rectifySign sov3, \n" +
    		
    		" subjectname accname \n" +
    		" from z_manuaccount a \n" +
    		" where a.projectid='${curProjectid}'  \n" +
    		" and (case when a.accfullname1='' then subjectfullname2 else accfullname1 end like '${subjectname}/%' or case when a.accfullname1='' then subjectfullname2 else accfullname1 end = '${subjectname}')  \n" +
    		" and a.dataname='0' and a.isleaf1 = 1 \n" +
    		" and a.accid='' ${allassitem}  \n" +
    		" and abs(initbalance) +  abs(debitremain       ) + abs(creditremain      ) + abs(DebitTotalOcc     ) + abs(CreditTotalOcc    ) + abs(Balance           ) + abs(DebitBalance      ) + abs(CreditBalance     ) + abs(DebitTotalOcc1    ) + abs(CreditTotalOcc1   ) + abs(DebitTotalOcc2    ) + abs(CreditTotalOcc2   ) + abs(DebitTotalOcc3    ) + abs(CreditTotalOcc3   ) + abs(DebitTotalOcc4    ) + abs(CreditTotalOcc4   ) + abs(DebitTotalOcc5    ) + abs(CreditTotalOcc5   ) + abs(DebitTotalOcc6    ) + abs(CreditTotalOcc6   ) + abs(DebitTotalOcc0    ) + abs(CreditTotalOcc0   ) >0 \n" +
    		
    		" ${correlation} order by subjectid,assitemid,dataname ";
    		
    }
}


