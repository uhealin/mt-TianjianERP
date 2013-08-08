package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.project.model.Project;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.db.DbUtil;

/**
 *
 * @author 铭太E审通团队,ESPIERALY THANKS WINNERQ AND PENGYONG
 * @version 1.0
 */

public class _3023_0 extends AbstractAreaFunction {

    public ResultSet process(HttpSession session, HttpServletRequest request,
                             HttpServletResponse response, Connection conn,
                             Map args) throws Exception {

        String accpackageid = (String) args.get("curAccPackageID");
        String projectid = (String) args.get("curProjectid");
        String vchdate = (String) args.get("vchdate");
        String n1 = (String) args.get("n1");
        String n2 = (String) args.get("n2");
        String localtion = (String) args.get("localtion");
        String direction = (String) args.get("direction");
        String allsubject = (String) args.get("allsubject");
        String lastdate = (String) args.get("lastdate");
        
        if(direction==null||"".equals(direction)){
        	direction="0";
        }
        
        if(allsubject==null||"".equals(allsubject)){
        	allsubject="0";
        }
        
        if(n1==null||"".equals(n1)){
           n1="10";
        }
        if(n2==null||"".equals(n2)){
            n2="10";
         }
        
        String resultSql = "";
      
        Statement st = null;
        ResultSet rs = null;
        try {
        	st = conn.createStatement();
        	
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

            //查找该科目在客户中的科目id,请注意即使有一对多的科目，这里也只是取其中的一条；
            String[] result=this.getClientIDAndDirectionByStandName(conn, accpackageid, projectid,
                    subjectname);
//            String subjectid = result[0];
  
            String AuditTimeBegin = "";
			String AuditTimeEnd = "";
			String sql = "select * from z_project where projectid='"+projectid+"'";
			rs = st.executeQuery(sql);
			if(rs.next()){
				AuditTimeBegin = rs.getString("AuditTimeBegin");
				AuditTimeEnd = rs.getString("AuditTimeEnd");
			}
			DbUtil.close(rs);
			int bYear = Integer.parseInt(AuditTimeBegin.substring(0, 4));
			int bMonth = Integer.parseInt(AuditTimeBegin.substring(5, 7));
			int eYear = Integer.parseInt(AuditTimeEnd.substring(0, 4));
			int eMonth = Integer.parseInt(AuditTimeEnd.substring(5, 7));
			
            int StartYearMonth = bYear * 12 + bMonth;
			int EndYearMonth = 	eYear * 12 + eMonth;
			/**
			 * 所有下级
			 */
            sql = "select group_concat(distinct \"'\",subjectid,\"'\") subjects from (" +
				" select distinct subjectid from c_Account a where 1=1 " +
				" and SubYearMonth * 12 + SubMonth >= "+StartYearMonth+" " +
				" and SubYearMonth * 12 + SubMonth <= "+EndYearMonth+" " +
				" and (a.subjectfullname2 = '"+subjectname+"' or a.subjectfullname2 like '"+subjectname+"/%' )" +
				" union \n" +
				" select distinct subjectid \n" +
				" from z_usesubject \n" +
				" where projectid="+projectid+" \n" +
				" and tipsubjectid in (" +
				"	select distinct subjectid from c_account a " +
				" 	where 1=1 " + 
				" 	and a.subyearmonth*12+a.submonth>='"+StartYearMonth+"' and a.subyearmonth*12+a.submonth<='"+EndYearMonth+"' " +
				"	and (a.subjectfullname2 like '"+subjectname+"/%'  or a.subjectfullname2 = '"+subjectname+"' ) and level1 = 1 " +
				" ) and isleaf = 1 \n" +
				" union " +
				" select distinct subjectid \n" +
				" from z_usesubject a \n" +
				" where projectid="+projectid+" \n" +
				" and (a.subjectfullname like '"+subjectname+"/%'  or a.subjectfullname = '"+subjectname+"' ) and isleaf = 1 " +
				" ) a" ;
            rs = st.executeQuery(sql);
            String subjects = "''";
			if(rs.next()){
				subjects = rs.getString(1);
			}
			DbUtil.close(rs);
			
			subjects =" and subjectid in ("+subjects+") ";
			
			if("".equals(vchdate)) vchdate = AuditTimeEnd;
			
            sql="select ifnull(group_concat(flowid SEPARATOR \"','\"),-1) as flowid from z_vouchersampleflow where projectid='${projectid}' and sampleMethod like'%-截止性抽凭%' ${subjects}  ";
//            if(vchdate!=null&&!"".equals(vchdate)){
//            	sql=sql+" and sampleflow='${vchdate}'";
//            	  args.put("vchdate",vchdate);
//            } 
//            sql=sql+" group by sampleflow order by sampleflow desc limit 1";
            args.put("subjects",subjects);
            args.put("projectid",projectid);
          
            resultSql = this.setSqlArguments(sql, args);  
            
            rs = st.executeQuery(resultSql);
            
            if(!"".equals(lastdate)&&lastdate!=null)
            	return rs;
            
            String  flowid = "-1";
            String  sampleflow = vchdate;
            if(rs.next()){
            	flowid = rs.getString(1);
//            	sampleflow = rs.getString(2);
            }
            
            rs.close();
            st.close();
            
            args.put("flowid",flowid);
            args.put("sampleflow",sampleflow);
     
            resultSql = getSql(localtion,allsubject,direction);
           
            //最终查询结果
            resultSql = this.setSqlArguments(resultSql, args);
            
            System.out.println("yzm:3023:resultSql="+resultSql);
            st = conn.createStatement();
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
    public String getSql(String localtion,String allsubject,String direction){
    	
    	String sql=" ";
    	if("before".equals(localtion)){//截止日以前
    	 sql=" "
    			+"select * from ( \n"
    			+"select concat(a.entryoldvoucherid,'-',a.entrytypeid) as oldvoucherid,a.entryvchdate as vchdate,a.entryoccurValue as occurValue,if(a.entrydirction=1,a.entryoccurvalue,0.00) debitocc,if(a.entrydirction=-1,a.entryoccurvalue,0.00)  creditocc,a.entrysubjectname1 as subjectname,a.entrysummary as summary,a.questdate as questdate,a.entrysubjectfullname1 as subjectfullname, '${sampleflow}' as 'lastdate' \n"
    	
    			+"from z_voucherspotcheck a \n"
    			+"where a.flowid in ('${flowid}') and a.entryvchdate<='${sampleflow}' \n";
    			if("1".equals(allsubject)){//是否刷单边
    				sql=sql+" and a.entrysubjectid like concat(a.subjectid,'%')";
    			}
    			if("1".equals(direction)){//是否只刷借
    				sql=sql+" and a.entrydirction = 1";
    			}else if("-1".equals(direction)){
    				sql=sql+" and a.entrydirction = -1";
    			}
    			sql+=" order by a.entryvchdate,a.entryoldvoucherid,a.entrytypeid  \n";
    			sql+=") a  \n"; 
    		return sql;
    	}else if("after".equals(localtion)){//截止日以后
    		
    	sql= " "
			+"select * from ( \n"
			+"select concat(a.entryoldvoucherid,'-',a.entrytypeid) as oldvoucherid,a.entryvchdate as vchdate,a.entryoccurValue as occurValue,if(a.entrydirction=1,a.entryoccurvalue,0.00) debitocc,if(a.entrydirction=-1,a.entryoccurvalue,0.00) creditocc,a.entrysubjectname1 as subjectname,a.entrysummary as summary,a.questdate as questdate,a.entrysubjectfullname1 as subjectfullname, '${sampleflow}' as 'lastdate' \n"
			+"from z_voucherspotcheck a \n"
			
			+"where a.flowid in ('${flowid}') and a.entryvchdate>'${sampleflow}' \n";
			if("1".equals(allsubject)){//是否刷单边
				sql=sql+" and a.entrysubjectid like concat(a.subjectid,'%')";
			}
			if("1".equals(direction)){//是否只刷借
				sql=sql+" and a.entrydirction = 1";
			}else if("-1".equals(direction)){
				sql=sql+" and a.entrydirction = -1";
			}
			sql+=" order by a.entryvchdate,a.entryoldvoucherid,a.entrytypeid  \n";
			sql+=") a \n"; 
		return sql;
    	}else{//全部
    		
    	sql= " "
			+"select * from ( \n"
			+"select  concat(a.entryoldvoucherid,'-',a.entrytypeid) as oldvoucherid,a.entryvchdate as vchdate,a.entryoccurValue as occurValue,if(a.entrydirction=1,a.entryoccurvalue,0.00) debitocc,if(a.entrydirction=-1,a.entryoccurvalue,0.00)  creditocc,a.entrysubjectname1 as subjectname,a.entrysummary as summary,a.questdate as questdate,a.entrysubjectfullname1 as subjectfullname, '${sampleflow}' as 'lastdate' \n"
			+"from z_voucherspotcheck a \n"
			+"where a.flowid in ('${flowid}')  \n";
			if("1".equals(allsubject)){//是否刷单边
				sql=sql+" and a.entrysubjectid like concat(a.subjectid,'%')";
			}
			if("1".equals(direction)){//是否只刷借
				sql=sql+" and a.entrydirction = 1";
			}else if("-1".equals(direction)){
				sql=sql+" and a.entrydirction = -1";
			}
			sql+=" order by a.entryvchdate,a.entryoldvoucherid,a.entrytypeid  \n"
				+") a \n";
			return sql;
    	}
    }

}