package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 2015公式：是按指定［单位］指定［科目］来刷抽凭
 * 参数：
 * subjectname		：刷指定科目分录时，定义科目名称
 * customer			：用于刷单位名称的标志
 * customerrname 	：单位的名称，用于只刷本单位的抽凭。注：一定要全路径(核算或科目全路径)
 * remain			：用于刷期初数的标志
 * direction		：发生的方向，1为增加，-1为减少；根据科目方向刷出指定方向的抽凭：科目方向与凭证方向相同为增加，否则为减少
 * assitem			：用来判断是刷核算还是科目
 * 
 * 返回：
 * 1、customer = 1 时 
 * SubjectID		: 科目编号，当 assitem=1 时，核算编号
 * SubjectName		: 科目名称，当 assitem=1 时，核算名称
 * SubjectFullName	: 科目全路径，当 assitem=1 时，核算全路径
 * 
 * 2、remain = 1 时
 * DebitRemain		：科目借方期初数，当 assitem=1 时，核算借方期初数
 * CreditRemain		：科目贷方期初数，当 assitem=1 时，核算贷方期初数
 * Remain			：科目期初数(借-贷) ，当 assitem=1 时，核算期初数(借-贷)
 * 
 * 3、其它情况，刷抽凭
 * vchdate				：凭证日期
 * Serail				：序号
 * typenumber			: 凭证记号
 * summary				：凭证摘要
 * subjectname			：本科目
 * othersubjectname		：对方科目
 * subjectname1			：科目名称（没有区分借贷）
 * debitocc				：借方金额
 * creditocc			：贷方金额
 * occ					：金额（没有区分借贷）
 * subjectname2			：科目名称（区分借贷）
 * Createor				：抽凭人
 * judge				：抽凭说明
 * 
 * 例：
 * 刷单位名称:
 * =取列公式覆盖(2015, "111", "SubjectFullName","&subjectname=应收账款&customer=1")
 * =取列公式覆盖(2015, "111", "SubjectFullName","&subjectname=应收账款&customer=1&assitem=1")
 * 
 * 刷期初数:
 * =取列公式覆盖(2015, "111", "remain","&subjectname=应收账款&remain=1&customerrname=应收账款")
 * =取列公式覆盖(2015, "111", "remain","&subjectname=应收账款&remain=1&customerrname=客户/无分类/中联重工浦沅分公司&assitem=1")
 * 
 * 刷抽凭:
 * =取列公式覆盖(2015, "111", "vchdate","&subjectname=应收账款&customerrname=应收账款")
 * =取列公式覆盖(2015, "111", "vchdate","&subjectname=应收账款&customerrname=应收账款&direction=1")
 * =取列公式覆盖(2015, "111", "vchdate","&subjectname=应收账款&customerrname=应收账款&direction=-1")
 * 
 * =取列公式覆盖(2015, "111", "vchdate","&subjectname=应收账款&customerrname=客户/无分类/中联重工浦沅分公司&assitem=1")
 * =取列公式覆盖(2015, "111", "vchdate","&subjectname=应收账款&customerrname=客户/无分类/中联重工浦沅分公司&assitem=1&direction=1")
 * =取列公式覆盖(2015, "111", "vchdate","&subjectname=应收账款&customerrname=客户/无分类/中联重工浦沅分公司&assitem=1&direction=-1")
 * 
 * =取列公式覆盖(2015, "111", "vchdate","&subjectname=应收账款")
 * =取列公式覆盖(2015, "111", "vchdate","&subjectname=应收账款&assitem=1")
 */
public class _2015_0 extends AbstractAreaFunction {

	public ResultSet process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {
		Statement st = null;
		ResultSet rs = null;
		try {
			String acc = (String) args.get("curPackageid");
			String curProjectid = (String) args.get("curProjectid");
			
			st = conn.createStatement();
			
			/**
			 * 用户输入的参数
			 */
			String subjectname = (String) args.get("subjectname");	//刷指定科目分录时，定义科目名称，可以刷多科目，以“`”分隔
			String customer = (String) args.get("customer"); 	//用于刷单位名称的标志
			String customerrname = (String) args.get("customerrname"); //单位的名称，用于只刷本单位的抽凭。注：一定要全路径(核算或科目全路径)
			customerrname = customerrname.trim();
			String remain = (String) args.get("remain"); //用于刷期初数的标志
			String direction = (String) args.get("direction");	//发生的方向，1为增加，-1为减少；根据科目方向刷出指定方向的抽凭：科目方向与凭证方向相同为增加，否则为减少
			String assitem = (String) args.get("assitem"); //用来判断是刷核算还是科目
			
			/**
			 * 项目的区间
			 */
			String strStartYearMonth="",strEndYearMonth="";
			String startmonth = (String) args.get("startmonth");
            String endmonth = (String) args.get("endmonth");
            String strYears ="";
            if (startmonth==null || startmonth.equals("")
                || endmonth==null || endmonth.equals("")){
                //如果前台没有提供这个参数，就从项目取；
                int[] result=getProjectAuditAreaByProjectid(conn,curProjectid);
                strStartYearMonth=String.valueOf(result[0]*12+result[1]);
                strEndYearMonth=String.valueOf(result[2]*12+result[3]);

                if (result[0]==result[2]){
                    strYears = " =  " + result[0];
                }else{
                    for (int i = result[0]; i <= result[2]; i++) {
                        strYears += ", " + String.valueOf(i);
                    }
                    if (strYears.length() > 0) {
                        //去掉最开始得,
                        strYears = " in ( " + strYears.substring(1) + ")";
                    }
                }
            }else{
                strStartYearMonth=String.valueOf(Integer.parseInt(acc.substring(6))*12+Integer.parseInt(startmonth));
                strEndYearMonth=String.valueOf(Integer.parseInt(acc.substring(6))*12+Integer.parseInt(endmonth));
                strYears=" =  " +acc.substring(6);
            }
            
			String sql = "";

			sql = "select distinct asstotalname from c_assitem where accpackageid='" + acc + "' and Level0=1  \n" +
			" and ( asstotalname like '%客户%' or asstotalname like '%供应商%' or asstotalname like '%关联%' or asstotalname like '%往来%' ) ";

		    rs = st.executeQuery(sql);
		    String sqlassitem = "";
		    while(rs.next()){
		    	sqlassitem += " asstotalname1 like '"+rs.getString(1)+"/%' or" ;
		    }
		    if(!"".equals(sqlassitem)){
		    	sqlassitem = " and (  " + sqlassitem.substring(0,sqlassitem.length()-2)+ ") ";
		    }else{
		    	sqlassitem = " and 1=2 ";
		    }
//		    rs.close();
		    
		    String sqlsubject = "";
			if("".equals(subjectname)){		//subjectname为空，表示所有科目
				
			}else{							//指定科目
				String[] sName = subjectname.split("`");
				for (int i = 0; i < sName.length; i++) {
					sqlsubject += "(subjectfullname2='"+sName[i]+"' or subjectfullname2 like '"+sName[i]+"/%') or ";
				}
				if(!"".equals(sqlsubject)){
					sqlsubject = " and ("+sqlsubject+" 1=2 )";
				}else{
					sqlsubject = " and 1=2";
				}
			}
			
			sql = "select group_concat(\"'\",subjectid,\"'\") as gs from c_account " +
				" where accpackageid ='"+acc+"'    " +
				" and submonth=1    \n" + sqlsubject;
			rs = st.executeQuery(sql);
			
			sqlsubject = "";
			if(rs.next()){
				sqlsubject = rs.getString(1);
			}
			if("".equals(sqlsubject)){
				sqlsubject = "''";
			}
			rs.close();
			
			sql = "select group_concat(distinct vchid) from z_voucherspotcheck a  \n" +
			"where a.ProjectID = '"+curProjectid+"'   \n" +
			"and subjectid in ("+sqlsubject+") ";
			rs = st.executeQuery(sql);
			String vchids = "''";		//得到与科目关联的抽凭
			if(rs.next()){
				vchids = rs.getString(1);
			}
			rs.close();
			
			if("1".equals(customer)){	//用于刷单位名称
				if("".equals(assitem)){		//按科目来刷单位名称
					sql = "	select distinct entrysubjectid as SubjectID,entrysubjectname1 as SubjectName,entrySubjectFullName1 as SubjectFullName  \n" +
					"	from z_voucherspotcheck a \n" +
					"	where a.ProjectID =  " + curProjectid + 
					"	and a.vchid in ("+vchids+") and subjectid in ("+sqlsubject+") and entrysubjectid in ("+sqlsubject+") \n" +
					"	order by entrysubjectid" ;
					System.out.println("单位名称 sql1:"+sql);
					
					rs = st.executeQuery(sql);
					return rs;
					
				}else{						//按核算来刷单位名称.有核算刷核算，无核算刷科目
					sql = "select distinct ifnull(assitemid,a.SubjectID) SubjectID,ifnull(assitemname,SubjectName) SubjectName,ifnull(asstotalname1,SubjectFullName) SubjectFullName	 \n" +
					" from ( \n" +
					"	select distinct vchid,entrysubjectid as SubjectID,entrysubjectname1 as SubjectName,entrySubjectFullName1 as SubjectFullName  \n" +
					"	from z_voucherspotcheck a \n" +
					"	where a.ProjectID = " + curProjectid + 
					"	and a.vchid in ("+vchids+")  and subjectid in ("+sqlsubject+") and entrysubjectid in ("+sqlsubject+") \n" +
					" ) a left join ( \n" +
					"	select distinct voucherid,a.assitemid,subjectid,asstotalname1,assitemname  \n" +
					"	from c_assitementry a ,c_assitementryacc b  \n" +
					"	where a.accpackageid ='"+acc+"' and b.accpackageid ='"+acc+"' and submonth=1 \n" + sqlassitem + 
					"	and a.voucherid in ("+vchids+")  and subjectid in ("+sqlsubject+") \n" +
					"	and a.assitemid=b.assitemid and a.subjectid =b.accid  \n" +
					" ) b on a.vchid=b.voucherid  \n" +
					"where 1=1 order by SubjectID" ;
					System.out.println("单位名称 sql2:"+sql);
					
					rs = st.executeQuery(sql);
					return rs;
				}
			}
			
			if("1".equals(remain)){		//用于刷期初数的标志
				if("".equals(customerrname)) throw new Exception ("刷期初数时，单位名称［customerrname］的值不能为空!!");
				
				if("".equals(assitem)){		//按科目来刷单位名称的期初数
					sql = "select DebitRemain,(-1)* CreditRemain as CreditRemain ,direction2 * (DebitRemain+CreditRemain) as Remain  \n" +
					"	from c_account  \n" +
					"	where SubYearMonth * 12 + SubMonth =  " + strStartYearMonth + 
					"	and subjectfullname1 = '"+customerrname+"' and subjectid in ("+sqlsubject+") ";
					System.out.println("期初数 sql1:"+sql);
					
					rs = st.executeQuery(sql);
					return rs;
					
				}else{						//按核算来刷单位名称的期初数.有核算刷核算，无核算刷科目
					sql = "select sum(DebitRemain) DebitRemain,sum(CreditRemain) CreditRemain,sum(Remain) Remain from ( \n" +
					"	select DebitRemain,(-1)* CreditRemain as CreditRemain ,direction2 * (DebitRemain+CreditRemain) as Remain  \n" +
					"	from c_account  \n" +
					"	where SubYearMonth * 12 + SubMonth =  " + strStartYearMonth + 
					"	and subjectfullname1 = '"+customerrname+"' and subjectid in ("+sqlsubject+")  \n" +
					"	union  \n" +
					"	select DebitRemain,(-1)* CreditRemain as CreditRemain ,direction2 * (DebitRemain+CreditRemain) as Remain  \n" +
					"	from c_assitementryacc  \n" +
					"	where SubYearMonth * 12 + SubMonth =  " + strStartYearMonth + 
					"	and AssTotalName1 = '"+customerrname+"' and accid in ("+sqlsubject+")  \n" +
					"	) a " ;
					System.out.println("期初数 sql2:"+sql);
					
					rs = st.executeQuery(sql);
					return rs;
				}
			}
			
			/**
			 * 刷抽凭
			 */
			String sqlid = "";
			String sqldirection = "";
			String sqlassitemid = "";
			if(!"".equals(customerrname)){
				if("".equals(assitem)){	
					sql = "select *  \n" +
					"	from c_account  \n" +
					"	where SubYearMonth * 12 + SubMonth =  " + strStartYearMonth +
					"	and subjectfullname1 = '"+customerrname+"'  ";
					rs = st.executeQuery(sql);
					if(rs.next()){
						sqlid = rs.getString("subjectid");
						sqldirection = rs.getString("direction2");
					}
					
				}else{
					
					sql = "select * from c_account a , (  \n" +
						"	select accid,assitemid  \n" +
						"	from c_assitementryacc  \n" +
						"	where SubYearMonth * 12 + SubMonth =  " + strStartYearMonth + 
						"	and AssTotalName1 = '"+customerrname+"' 	 \n" +
						"	and accid in ("+sqlsubject+")  \n" +
						") b where  a.subjectid=b.accid and a.SubYearMonth * 12 + a.SubMonth = " + strStartYearMonth;
					
					rs = st.executeQuery(sql);
					if(rs.next()){
						sqlid = rs.getString("subjectid");
						sqldirection = rs.getString("direction2");
						sqlassitemid = rs.getString("assitemid");
					}else{
						sql = "select *  \n" +
						"	from c_account  \n" +
						"	where SubYearMonth * 12 + SubMonth = " + strStartYearMonth +
						"	and subjectfullname1 = '"+customerrname+"'  ";
						rs = st.executeQuery(sql);
						if(rs.next()){
							sqlid = rs.getString("subjectid");
							sqldirection = rs.getString("direction2");
						}
					}
					
				}	
				rs.close();
					
				if("1".equals(direction)){
					sqldirection = " and entryDirction="+ sqldirection;
				}else if ("-1".equals(direction)){
					sqldirection = " and entryDirction = (-1) * "+ sqldirection;
				}else{
					sqldirection = "";
				}
				
				if("".equals(sqlassitemid)){	
					sql = "select group_concat(distinct vchid) vchid from z_voucherspotcheck a  \n" +
						" where a.ProjectID = '"+ curProjectid + "'   \n" +
						" and subjectid in ('"+sqlid+"') and entrysubjectid in ('"+sqlid+"')  \n" + sqldirection;
					rs = st.executeQuery(sql);
					if(rs.next()){
						vchids = rs.getString(1);
					}
				}else{
					sql = "select group_concat(distinct vchid) vchid from z_voucherspotcheck a  \n" +
					" where a.ProjectID = '"+ curProjectid + "'   \n" +
					" and subjectid in ('"+sqlid+"') and entrysubjectid in ('"+sqlid+"')  \n" + sqldirection + 
					" and vchid in (select voucherid from c_assitementry where accpackageid ='"+acc+"' and assitemid='"+sqlassitemid+"' and subjectid in ('"+sqlid+"') )";
					rs = st.executeQuery(sql);
					if(rs.next()){
						vchids = rs.getString(1);
					}
				}
				rs.close();
				if(vchids == null) vchids = "''";
				
				sql = "select distinct a.entryvchdate vchdate,a.vchid as voucherid,entrySerail Serail,  \n" +
					" concat(if( length(entrytypeid)=0 || entrytypeid is null,'',concat(entrytypeid,'-')),cast(entryoldvoucherid as char)) as typenumber,entrysummary as summary,  \n" +
					" case  when entrysubjectid='"+sqlid+"' then entrysubjectfullname1 else '---' end as subjectname, \n" +
					" case  when entrysubjectid='"+sqlid+"' then '---' else entrysubjectfullname1 end  as othersubjectname, \n" +
					" entrysubjectfullname1 as subjectname1, \n" +
					" case entrydirction when 1 then entryoccurvalue else 0 end as debitocc, \n" +
					" case entrydirction when -1 then entryoccurvalue else 0 end  as creditocc, \n" +
					" entryoccurvalue as occ, \n" +
					" case entrydirction when 1 then concat('借:',entrysubjectfullname1) else concat('贷:',entrysubjectfullname1) end as subjectname2, \n" +
					" group_concat(distinct b.name) Createor,group_concat(distinct judge) judge  \n" +
					
					" from z_voucherspotcheck a  \n" +
					" left join k_user b on a.Createor=b.id  \n" +
					" where a.ProjectID = '"+ curProjectid + "'   \n" +
					" and subjectid in ('"+sqlid+"') and vchid in ("+vchids+") \n" +
					" group by vchdate,Serail,typenumber  \n" +
					" order by Createor,subjectid,vchid,entrySerail";
				System.out.println("刷抽凭 sql1:"+sql);
				
				rs = st.executeQuery(sql);
				return rs;
			}else{
				
				if(!"".equals(direction)){
					sqldirection = " and entryDirction="+ direction;
				}else{
					sqldirection = "";
				}
				
				if("".equals(assitem)){	
					sql = "select group_concat(distinct vchid) vchid from z_voucherspotcheck a  \n" +
					" where a.ProjectID = '"+ curProjectid + "'   \n" +
					" and subjectid in ("+sqlsubject+") and entrysubjectid in ("+sqlsubject+")  \n" + sqldirection;
					rs = st.executeQuery(sql);
					if(rs.next()){
						vchids = rs.getString(1);
					}
				
				}else{
					sql = "select group_concat(distinct vchid) vchid from z_voucherspotcheck a  \n" +
					" where a.ProjectID = '"+ curProjectid + "'   \n" +
					" and subjectid in ("+sqlsubject+") and entrysubjectid in ("+sqlsubject+")  \n" + sqldirection + 
					" and vchid in (select voucherid from c_assitementry where accpackageid ='"+acc+"'  and subjectid in ("+sqlsubject+") )";
					rs = st.executeQuery(sql);
					if(rs.next()){
						vchids = rs.getString(1);
					}
				}
				rs.close();
				if(vchids == null) vchids = "''";
				
				sql = "select distinct a.entryvchdate vchdate,a.vchid as voucherid,entrySerail Serail,  \n" +
				" concat(if( length(entrytypeid)=0 || entrytypeid is null,'',concat(entrytypeid,'-')),cast(entryoldvoucherid as char)) as typenumber,entrysummary as summary,  \n" +
				" case  when (entrysubjectfullname1 like concat(c.gs,'/%') or entrysubjectfullname1=c.gs) then entrysubjectfullname1 else '---' end as subjectname, \n" +
				" case  when (entrysubjectfullname1 like concat(c.gs,'/%') or entrysubjectfullname1=c.gs) then '---' else entrysubjectfullname1 end  as othersubjectname, \n" +
				" entrysubjectfullname1 as subjectname1, \n" +
				" case entrydirction when 1 then entryoccurvalue else 0 end as debitocc, \n" +
				" case entrydirction when -1 then entryoccurvalue else 0 end  as creditocc, \n" +
				" entryoccurvalue as occ, \n" +
				" case entrydirction when 1 then concat('借:',entrysubjectfullname1) else concat('贷:',entrysubjectfullname1) end as subjectname2, \n" +
				" group_concat(distinct b.name) Createor,group_concat(distinct judge) judge  \n" +
				
				" from z_voucherspotcheck a  \n" +
				" left join k_user b on a.Createor=b.id  \n" +
				" left join ( \n" +
				"	select subjectfullname1 as gs from c_account where SubYearMonth * 12 + SubMonth =  \n" + strStartYearMonth  +
				"	and subjectid in ("+sqlsubject+") and level1 = 1  \n" +
				" ) c on 1=1  \n" +
				" where a.ProjectID = '"+ curProjectid + "'   \n" +
				" and subjectid in ("+sqlsubject+") and vchid in ("+vchids+") \n" +
				" group by vchdate,Serail,typenumber  \n" +
				" order by Createor,subjectid,vchid,entrySerail";
				System.out.println("刷抽凭 sql2:"+sql);
				
				rs = st.executeQuery(sql);
				return rs;
			
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} 
	}

	
}
