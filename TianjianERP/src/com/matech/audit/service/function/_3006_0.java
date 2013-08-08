package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 公式跟3007一样
 * 刷新出的列内容包括:
 * subjectid : 科目或核算编号
 * subjectname : 科目或核算名称
 * 
 * initbalance1 : 期初余额(不含调整)
 * initDebitbalance ：期初借方余额(含借方期初调整)
 * initCreditbalance ：期初贷方余额(含贷方期初调整)
 * initbalance ：期初余额(含期初调整)
 * 
 * debitocc ：借方发生(不含调整)
 * sddebittotalocc ：借方发生(含借方期末调整)
 * sddebittotalocc1 ： 借方发生(含所有借方调整)
 * 
 * creditocc ：贷方发生(不含调整)
 * sdcredittotalocc ：贷方发生(含贷方期末调整)
 * sdcredittotalocc1 ： 贷方发生(含所有贷方调整)
 * 
 * Balance :期末余额(不含调整)
 * sdDebitBalance ：期末借方余额(含借方期末调整)
 * sdDebitBalance1 ：期末借方余额(含所有借方调整)
 * sdCreditbalance ：期末贷方余额(含贷方期末调整)
 * sdCreditbalance1 ：期末贷方余额(含所有贷方调整)
 * sdBalance :期末余额(含期末调整)
 * sdBalance1 :期末余额(含所有调整)
 * 
 * initsov ：期初调整
 * debitsov1 : 期末调整借
 * creditsov1 : 期末调整贷
 * debitsov2 : 期末重分类借
 * creditsov2 ： 期末重分类贷
 * debitsov3 ：期末调整借 + 期末重分类借
 * creditsov3 ：期末调整贷 + 期末重分类贷
 * debitsov4 ：期末调整借 + 期初调整借 + 账表不符借
 * creditsov4 ：期末调整贷 + 期初调整贷 + 账表不符贷
 * debitsov5 ：期末重分类借 + 期初重分类借
 * creditsov5 ：期末重分类贷 + 期初重分类贷
 * 
 * sov1 ：期末调整借 - 期末调整贷
 * sov2 ：期末重分类借 - 期末重分类贷
 * sov3 ：(期末调整借 - 期末调整贷) + (期末重分类借 - 期末重分类贷)  
 * 
 * dataname : 币种
 * exchangerate ：汇率
 */

public class _3006_0 extends AbstractAreaFunction {
	
	public ResultSet process(HttpSession session, HttpServletRequest request, HttpServletResponse response, Connection conn, Map args) throws Exception {
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
        		correlation=" having exists (\n		select 1 from asdb.k_connectcompanys where k_connectcompanys.customerid="+customerid+" and connectcompanysname=subjectname111\n        )   \n";
        	}else{
        		//显示非关联客户的
        		correlation=" having not exists (\n		select 1 from asdb.k_connectcompanys where k_connectcompanys.customerid="+customerid+" and connectcompanysname=subjectname111\n        )   \n";
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
                
            }
            st = conn.createStatement();
            String sName = changeSubjectName(conn,projectid,subjectname);
            if(!"".equals(sName)){
            	subjectname = sName; 
            }            
            args.put("subjectname",subjectname);
            
            String[] result=this.getClientIDAndDirectionByStandName(conn, accpackageid, projectid,
                    subjectname);
            String subjectid = result[0];
            
            String sqlassitem = "select distinct asstotalname from c_assitem where accpackageid='" + accpackageid + "' and Level0=1 " +
    		" and ( asstotalname like '%客户%' or asstotalname like '%供应商%' or asstotalname like '%关联%' or asstotalname like '%往来%' ) ";
    
		    rs = st.executeQuery(sqlassitem);
		    String sqlstring = "";
		    while(rs.next()){
		    	sqlstring += " subjectfullname2 like '"+rs.getString(1)+"/%' or" ;
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
                args.put("allassitem","  and  ( assitemid <>'' "+sqlstring+" ) ");
            }else{
                //扫描全部辅助核算
                args.put("allassitem"," ");
            }
            
            
            String sql = "select distinct subjectid " +
            		" 	from z_manuaccount a " +
            		"	where a.projectid='${curProjectid}'  \n" +
        			" 	and (case when a.accfullname1='' then subjectfullname2 else accfullname1 end like '${subjectname}/%' or case when a.accfullname1='' then subjectfullname2 else accfullname1 end = '${subjectname}') \n" +
        			"	and a.isleaf1 = 1 \n" +
        			"	and a.dataname='0' \n" +
        			"	${allassitem} \n" ;
            sql = this.setSqlArguments(sql, args);
            org.util.Debug.prtOut("py:3006:resultSql11="+sql);
            rs = st.executeQuery(sql);
            String subject = "''";
            while (rs.next()) {
            	subject += ",'"+rs.getString(1)+"'";
            }
            
            
		    
            if (allassitem==null || allassitem.equals("")){
                //只扫描往来、客户、供应商
                args.put("allassitem","  and ((assitemid ='' and subjectid not in ("+subject+") ) or ( assitemid <>'' "+sqlstring+" )) ");
            }else{
                //扫描全部辅助核算
                args.put("allassitem"," and ((assitemid ='' and subjectid not in ("+subject+")) or assitemid <>'' ) ");
            }
            
            
           resultSql = getSql(subjectid);
           
           resultSql = this.setSqlArguments(resultSql, args);
           org.util.Debug.prtOut("py:3006:resultSql="+resultSql);
           
           st = conn.createStatement();
           rs = st.executeQuery(resultSql);
        	
           return rs;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	public String getSql(String subjectid){
		
		return " " +
			" select " +
			" ifnull(b.subjectname1,a.subjectname1) subjectname111, \n" +
			
			" ifnull(b.subjectid,a.subjectid) subjectid, \n" +
			" ifnull(b.subjectname,a.subjectname) subjectname, \n" +
			" ifnull(b.initbalance1,a.initbalance1) initbalance1, \n" +
			" ifnull(b.initDebitbalance,a.initDebitbalance) initDebitbalance, \n" +
			" ifnull(b.initCreditbalance,a.initCreditbalance) initCreditbalance, \n" +
			" ifnull(b.initbalance,a.initbalance) initbalance, \n" +
			" ifnull(b.debitocc,a.debitocc) debitocc, \n" +
			" ifnull(b.sddebittotalocc,a.sddebittotalocc) sddebittotalocc, \n" +
			" ifnull(b.sddebittotalocc1,a.sddebittotalocc1) sddebittotalocc1, \n" +
			" ifnull(b.creditocc,a.creditocc) creditocc, \n" +
			" ifnull(b.sdcredittotalocc,a.sdcredittotalocc) sdcredittotalocc, \n" +
			" ifnull(b.sdcredittotalocc1,a.sdcredittotalocc1) sdcredittotalocc1, \n" +
			" ifnull(b.Balance,a.Balance) Balance, \n" +
			" ifnull(b.sdDebitBalance,a.sdDebitBalance) sdDebitBalance, \n" +
			" ifnull(b.sdDebitBalance1,a.sdDebitBalance1) sdDebitBalance1, \n" +
			" ifnull(b.sdCreditbalance,a.sdCreditbalance) sdCreditbalance, \n" +
			" ifnull(b.sdCreditbalance1,a.sdCreditbalance1) sdCreditbalance1, \n" +
			" ifnull(b.sdBalance,a.sdBalance) sdBalance, \n" +
			" ifnull(b.sdBalance1,a.sdBalance1) sdBalance1, \n" +
			" ifnull(b.initsov,a.initsov) initsov, \n" +
			" ifnull(b.debitsov1,a.debitsov1) debitsov1, \n" +
			" ifnull(b.creditsov1,a.creditsov1) creditsov1, \n" +
			" ifnull(b.debitsov2,a.debitsov2) debitsov2, \n" +
			" ifnull(b.creditsov2,a.creditsov2) creditsov2, \n" +
			" ifnull(b.debitsov3,a.debitsov3) debitsov3, \n" +
			" ifnull(b.creditsov3,a.creditsov3) creditsov3, \n" +
			" ifnull(b.debitsov4,a.debitsov4) debitsov4, \n" +
			" ifnull(b.creditsov4,a.creditsov4) creditsov4, \n" +
			" ifnull(b.debitsov5,a.debitsov5) debitsov5, \n" +
			" ifnull(b.creditsov5,a.creditsov5) creditsov5, \n" +
			" ifnull(b.sov1,a.sov1) sov1,  \n" +
			" ifnull(b.sov2,a.sov2) sov2, \n" +
			" ifnull(b.sov3,a.sov3) sov3, \n" +
			" ifnull(b.dataname,a.dataname) dataname, \n" +
			" ifnull(b.exchangerate,a.exchangerate) exchangerate \n" +
			" from ( \n" +
			" 	select  \n" +
			" 	subjectid subjectid1,assitemid assitemid1, a.subjectname subjectname1,\n" +
			" 	case when a.assitemid='' then a.subjectid else a.assitemid end subjectid, \n" +
			" 	case when a.assitemid='' then a.subjectname else concat(a.accname1,'/',a.subjectname) end subjectname, \n" +
			
			" 	direction2 * initbalance initbalance1, \n" +
			" 	(debitremain + (DebitTotalOcc4 + DebitTotalOcc5 + DebitTotalOcc6) * rectifySign ) initDebitbalance, \n" +
			" 	((-1)*creditremain + (CreditTotalOcc4 + CreditTotalOcc5 + CreditTotalOcc6) * rectifySign ) initCreditbalance, \n" +
			" 	direction2 * (initbalance + ((DebitTotalOcc4 - CreditTotalOcc4) + (DebitTotalOcc5-CreditTotalOcc5) + (DebitTotalOcc6-CreditTotalOcc6)) * rectifySign) initbalance, \n" +
			
			" 	DebitTotalOcc  debitocc, \n" +
			" 	(DebitTotalOcc + (DebitTotalOcc1 + DebitTotalOcc2) * rectifySign ) sddebittotalocc, \n" +
			" 	(DebitTotalOcc + (DebitTotalOcc1 + DebitTotalOcc2 + DebitTotalOcc4 + DebitTotalOcc5 + DebitTotalOcc6) * rectifySign ) sddebittotalocc1, \n" +
			
			" 	CreditTotalOcc creditocc,	 \n" +
			" 	(CreditTotalOcc + (CreditTotalOcc1 + CreditTotalOcc2) * rectifySign) sdcredittotalocc, \n" +
			"	(CreditTotalOcc + (CreditTotalOcc1 + CreditTotalOcc2 + CreditTotalOcc4 + CreditTotalOcc5 + CreditTotalOcc6) * rectifySign ) sdcredittotalocc1, \n" +
			
			"	direction2 * Balance Balance, \n" +
			"	(DebitBalance + (DebitTotalOcc1 + DebitTotalOcc2) * rectifySign) sdDebitBalance,	 \n" +
			"	(DebitBalance + (DebitTotalOcc1 + DebitTotalOcc2 + DebitTotalOcc4 + DebitTotalOcc5 + DebitTotalOcc6) * rectifySign ) sdDebitBalance1, \n" +
			"	((-1)*CreditBalance + (CreditTotalOcc1 + CreditTotalOcc2) * rectifySign) sdCreditbalance, \n" +
			"	((-1)*CreditBalance + (CreditTotalOcc1 + CreditTotalOcc2 + CreditTotalOcc4 + CreditTotalOcc5 + CreditTotalOcc6) * rectifySign) sdCreditbalance1, \n" +
			"	direction2 * (Balance + (DebitTotalOcc1 + DebitTotalOcc2 - (CreditTotalOcc1 + CreditTotalOcc2)) * rectifySign ) sdBalance, \n" +
			"	direction2 * (Balance + (DebitTotalOcc1 + DebitTotalOcc2 + DebitTotalOcc4 + DebitTotalOcc5 + DebitTotalOcc6 - (CreditTotalOcc1 + CreditTotalOcc2 + CreditTotalOcc4 + CreditTotalOcc5 + CreditTotalOcc6)) * rectifySign ) sdBalance1, \n" +
			
			"	direction2 * (debittotalocc4 - credittotalocc4 + debittotalocc5 - credittotalocc5 + debittotalocc6 - credittotalocc6) * rectifySign as initsov, \n" +
			
			"	debittotalocc1 * rectifySign as debitsov1,   \n" +
			"	credittotalocc1 * rectifySign as creditsov1,  \n" +
			"	debittotalocc2 * rectifySign as debitsov2,   \n" +
			"	credittotalocc2 * rectifySign as creditsov2,  \n" +
			"	(debittotalocc1 + debittotalocc2) * rectifySign as debitsov3,   \n" +
			"	(credittotalocc1 + credittotalocc2) * rectifySign as creditsov3,  \n" +
			"	(debittotalocc1+debittotalocc4+debittotalocc6) * rectifySign as debitsov4, \n" +
			"	(credittotalocc1+credittotalocc4+credittotalocc6) * rectifySign as creditsov4, \n" +
			"	(debittotalocc2+debittotalocc5) * rectifySign as debitsov5, \n" +
			"	(credittotalocc2+credittotalocc5) * rectifySign as creditsov5, \n" +
			
			"	direction2 *(debittotalocc1 - credittotalocc1) * rectifySign sov1, \n" +
			"	direction2 *(debittotalocc2 - credittotalocc2) * rectifySign sov2, \n" +
			"	direction2 *(debittotalocc1 - credittotalocc1 + debittotalocc2 - credittotalocc2) * rectifySign sov3, \n" +
			
			"	case dataname when '0' then '人民币' else dataname end dataname,exchangerate \n" +
			"	from z_manuaccount a \n" +
			"	where a.projectid='${curProjectid}'  \n" +
			
//			" 	and case when a.accfullname1='' then subjectfullname2 else accfullname1 end like '${subjectname}%' \n" +
			" 	and (case when a.accfullname1='' then subjectfullname2 else accfullname1 end like '${subjectname}/%' or case when a.accfullname1='' then subjectfullname2 else accfullname1 end = '${subjectname}') \n" +

//			"	and a.subjectid like '"+subjectid+"%'  \n" +
			"	and a.isleaf1 = 1 \n" +
			"	and a.dataname='0' \n" +
			"	${allassitem} \n" +
			" ) a left join ( \n" +
			" 	select  \n" +
			" 	subjectid subjectid1,assitemid assitemid1, a.subjectname subjectname1,\n" +
			" 	case when a.assitemid='' then a.subjectid else a.assitemid end subjectid, \n" +
			" 	case when a.assitemid='' then a.subjectname else concat(a.accname1,'/',a.subjectname) end subjectname, \n" +
			
			" 	direction2 * initbalance initbalance1, \n" +
			" 	(debitremain + (DebitTotalOcc4 + DebitTotalOcc5 + DebitTotalOcc6) * rectifySign ) initDebitbalance, \n" +
			" 	((-1)*creditremain + (CreditTotalOcc4 + CreditTotalOcc5 + CreditTotalOcc6) * rectifySign ) initCreditbalance, \n" +
			" 	direction2 * (initbalance + ((DebitTotalOcc4 - CreditTotalOcc4) + (DebitTotalOcc5-CreditTotalOcc5) + (DebitTotalOcc6-CreditTotalOcc6)) * rectifySign) initbalance, \n" +
			
			" 	DebitTotalOcc  debitocc, \n" +
			" 	(DebitTotalOcc + (DebitTotalOcc1 + DebitTotalOcc2) * rectifySign ) sddebittotalocc, \n" +
			" 	(DebitTotalOcc + (DebitTotalOcc1 + DebitTotalOcc2 + DebitTotalOcc4 + DebitTotalOcc5 + DebitTotalOcc6) * rectifySign ) sddebittotalocc1, \n" +
			
			" 	CreditTotalOcc creditocc,	 \n" +
			" 	(CreditTotalOcc + (CreditTotalOcc1 + CreditTotalOcc2) * rectifySign) sdcredittotalocc, \n" +
			"	(CreditTotalOcc + (CreditTotalOcc1 + CreditTotalOcc2 + CreditTotalOcc4 + CreditTotalOcc5 + CreditTotalOcc6) * rectifySign ) sdcredittotalocc1, \n" +
			
			"	direction2 * Balance Balance, \n" +
			"	(DebitBalance + (DebitTotalOcc1 + DebitTotalOcc2) * rectifySign) sdDebitBalance,	 \n" +
			"	(DebitBalance + (DebitTotalOcc1 + DebitTotalOcc2 + DebitTotalOcc4 + DebitTotalOcc5 + DebitTotalOcc6) * rectifySign ) sdDebitBalance1, \n" +
			"	((-1)*CreditBalance + (CreditTotalOcc1 + CreditTotalOcc2) * rectifySign) sdCreditbalance, \n" +
			"	((-1)*CreditBalance + (CreditTotalOcc1 + CreditTotalOcc2 + CreditTotalOcc4 + CreditTotalOcc5 + CreditTotalOcc6) * rectifySign) sdCreditbalance1, \n" +
			"	direction2 * (Balance + (DebitTotalOcc1 + DebitTotalOcc2 - (CreditTotalOcc1 + CreditTotalOcc2)) * rectifySign ) sdBalance, \n" +
			"	direction2 * (Balance + (DebitTotalOcc1 + DebitTotalOcc2 + DebitTotalOcc4 + DebitTotalOcc5 + DebitTotalOcc6 - (CreditTotalOcc1 + CreditTotalOcc2 + CreditTotalOcc4 + CreditTotalOcc5 + CreditTotalOcc6)) * rectifySign ) sdBalance1, \n" +
			
			"	direction2 * (debittotalocc4 - credittotalocc4 + debittotalocc5 - credittotalocc5 + debittotalocc6 - credittotalocc6) * rectifySign as initsov, \n" +
			
			"	debittotalocc1 * rectifySign as debitsov1,   \n" +
			"	credittotalocc1 * rectifySign as creditsov1,  \n" +
			"	debittotalocc2 * rectifySign as debitsov2,   \n" +
			"	credittotalocc2 * rectifySign as creditsov2,  \n" +
			"	(debittotalocc1 + debittotalocc2) * rectifySign as debitsov3,   \n" +
			"	(credittotalocc1 + credittotalocc2) * rectifySign as creditsov3,  \n" +
			"	(debittotalocc1+debittotalocc4+debittotalocc6) * rectifySign as debitsov4, \n" +
			"	(credittotalocc1+credittotalocc4+credittotalocc6) * rectifySign as creditsov4, \n" +
			"	(debittotalocc2+debittotalocc5) * rectifySign as debitsov5, \n" +
			"	(credittotalocc2+credittotalocc5) * rectifySign as creditsov5, \n" +
			
			"	direction2 *(debittotalocc1 - credittotalocc1) * rectifySign sov1, \n" +
			"	direction2 *(debittotalocc2 - credittotalocc2) * rectifySign sov2, \n" +
			"	direction2 *(debittotalocc1 - credittotalocc1 + debittotalocc2 - credittotalocc2) * rectifySign sov3, \n" +
			
			"	case dataname when '0' then '人民币' else dataname end dataname,exchangerate \n" +
			"	from z_manuaccount a \n" +
			"	where a.projectid='${curProjectid}'  \n" +
			
//			" 	and case when a.accfullname1='' then subjectfullname2 else accfullname1 end like '${subjectname}%' \n" +
			" 	and (case when a.accfullname1='' then subjectfullname2 else accfullname1 end like '${subjectname}/%' or case when a.accfullname1='' then subjectfullname2 else accfullname1 end = '${subjectname}') \n" +
 
//			"	and a.subjectid like '"+subjectid+"%'  \n" +
			"	and a.isleaf1 = 1 \n" +
			"	and a.dataname<>'0' \n" +
			"	 ${allassitem} \n" +
			" ) b on 1=1 \n" +
			" and a.subjectid1=b.subjectid1  \n" +
			" and a.assitemid1=b.assitemid1 \n" +
			
			" ${correlation} order by a.subjectid1,a.assitemid1";
	} 
}
