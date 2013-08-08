package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * 2008.07.17
 * 前进之风 16:44:28
 * 只有这两个模板？ 
 * 胤 16:45:04
 * direction的话应该只影响这个 
 * 前进之风 16:46:05
 * 其它模板direction是只刷单边的 
 * 前进之风 16:46:40
 * 只有99,141模板direction是刷凭证的
 * 是吗？ 
 * 胤 16:46:48
 * 恩~~ 
 */

/**
 * 
 *1015公式：
 *参数：
 *direction　用来判断是刷单边还是全部；改为direction也刷全部，只是direction相同的凭证
 *assitem 用来判断是刷核算还是科目
 *subject 用来判断是刷所有分录还是指定科目分录
 *subjectname 刷指定科目分录时，定义科目名称，可以刷多科目，以“`”分隔
 *
 *返回值：
 *vchdate				：凭证日期
 *Serail				：序号
 *typenumber			: 凭证记号
 *summary				：凭证摘要
 *subjectname			：本科目
 *othersubjectname		：对方科目
 *subjectfullname22		：本科目的标准科目
 *subjectname1			：科目名称（没有区分借贷）
 *debitocc				：借方金额
 *creditocc				：贷方金额
 *occ					：金额（没有区分借贷）
 *subjectname2			：科目名称（区分借贷）
 *Createor				：抽凭人
 *judge					：抽凭说明
 *
 *assitemid				：核算编号（只有assitem＝1时才有）
 *assitemname			：核算名称（只有assitem＝1时才有）
 *
 */
public class _1015_99 extends AbstractAreaFunction {
	public ResultSet process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {

		

		String resultSql = "";

		Statement st = null;
		ResultSet rs = null;
		try {
			
			String direction = (String) args.get("direction");	//用来判断是刷单边还是全部
			String assitem = (String) args.get("assitem");	//用来判断是刷核算还是科目

			String subject = (String) args.get("subject");//用来判断是刷所有分录还是指定科目分录
			String subjectname = (String) args.get("subjectname"); //刷指定科目分录时，定义科目名称，可以刷多科目，以“`”分隔
			
			String acc = (String) args.get("curPackageid");
			String curProjectid = (String) args.get("curProjectid");
			
			st = conn.createStatement();

			String strStartYearMonth="",strEndYearMonth="";
			int[] result=getProjectAuditAreaByProjectid(conn,curProjectid);
			strStartYearMonth=String.valueOf(result[0]*12+result[1]);
            strEndYearMonth=String.valueOf(result[2]*12+result[3]);
            
			String sql = "";
			
			int iMode = 0;
			String dir = direction;
			if (direction != null && !"".equals(direction)) {
				iMode = 1;
				direction=" and a.entrydirction = " + direction;
//				direction=" and a.dirction = " + direction;
			} else {
				direction=" ";
			}
			
			String sqlassitem = "select distinct asstotalname from c_assitem where accpackageid='" + acc + "' and Level0=1 " +
    			" and ( asstotalname like '%客户%' or asstotalname like '%供应商%' or asstotalname like '%关联%' or asstotalname like '%往来%' ) ";
    
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
		    args.put("sqlstring", sqlstring);
		    
			if(subject == null || "".equals(subject)){		//指定科目分录
				
				if(assitem == null || "".equals(assitem)){
					resultSql = getSql(iMode,direction,"1",dir);
				}else{
					resultSql = getSql1(iMode,direction,"1",dir);
				}
				
				String[] sName = subjectname.split("`");
				sql = "";
				for (int i = 0; i < sName.length; i++) {
					String sql1 = " select distinct accname as gs,subjectfullname2 as subjectfullname22  \n" +
					" from c_account  \n" +
					" where subjectfullname2='"+sName[i]+"'   \n" +

					" and SubYearMonth*12 + SubMonth>= "+strStartYearMonth+" " +
					" and SubYearMonth*12 + SubMonth<= "+strEndYearMonth+" " ;
					
//					" and submonth=1  \n" +
//					" and accpackageid ='"+acc+"' ";
					
//					System.out.println(sql1);
					rs = st.executeQuery(sql1);
					if(rs.next()){
						args.put("gs", rs.getString(1));
						args.put("subjectfullname22", rs.getString(2));
					}else{
						args.put("gs"," ");
						args.put("subjectfullname22", " ");
					}
					rs.close();
					
					sql1 = "select group_concat(distinct \"'\",subjectid,\"'\") as gs  \n" +
							"from c_account   \n" +
							"where 1=1" +
//							"and accpackageid ='"+acc+"'   \n" +
//							"and submonth=1   \n" +
							" and SubYearMonth*12 + SubMonth>= "+strStartYearMonth+" " +
							" and SubYearMonth*12 + SubMonth<= "+strEndYearMonth+" " +
							" and (subjectfullname2='"+sName[i]+"' or subjectfullname2 like '"+sName[i]+"/%')  " ;
					
//					System.out.println(sql1);
					
					rs = st.executeQuery(sql1);
					String s1 = "";
					if(rs.next()){
						s1 = rs.getString(1);
						if(s1 == null || "".equals(s1.trim())){
							args.put("subjectid", "''");
						}else{
							args.put("subjectid", rs.getString(1));
						}
					}else{
						args.put("subjectid", "''");
						s1 = "''";
					}
					rs.close();
					
					sql1 = "select group_concat(distinct vchid) from z_voucherspotcheck a " +
							"where a.ProjectID = '"+curProjectid+"'  " +
							"and subjectid in ("+s1+") ";
					rs = st.executeQuery(sql1);
					if(rs.next()){
						s1 = rs.getString(1);
						if(s1 == null || "".equals(s1.trim())){
							args.put("vchids", "-1");
						}else{
							args.put("vchids",rs.getString(1));
						}
						
					}else{
						args.put("vchids", "-1");
					}
					rs.close();

					sql += this.setSqlArguments(resultSql, args) + " union";

				}
				sql = sql.substring(0,sql.length()-5);
				
			}else{			//刷所有分录
				String sql1 = "select group_concat (distinct vchid) from z_voucherspotcheck a " +
				"where a.ProjectID = '"+curProjectid+"'  ";
				rs = st.executeQuery(sql1);
				if(rs.next()){
					args.put("vchids",rs.getString(1));
				}else{
					args.put("vchids", "-1");
				}
				rs.close();
				
				if(assitem == null || "".equals(assitem)){
					resultSql = getSql(iMode,direction,"0",dir);
				}else{
					resultSql = getSql1(iMode,direction,"0",dir);
				}
				
				sql = this.setSqlArguments(resultSql, args);
			}
			
			if(assitem == null || "".equals(assitem)){
				sql = " "+sql+" order by  Createor,vchdate,typenumber,Serail"; 
			}else{
				sql = " "+sql+" order by  Createor,AssItemName,vchdate,typenumber,Serail"; 
			}
			System.out.println("resultSql= \n" + sql);
			
			rs = st.executeQuery(sql);
			return rs;

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	//刷辅助核算的
	public String getSql1(int iMode,String direction,String opt,String dir){
		String strSql = "";

		String str1 = "";
		String str2 = "";
		if("1".equals(opt)){
			opt = "case  when (entrysubjectfullname1 like '${gs}/%' or entrysubjectfullname1='${gs}') then entrysubjectfullname1 else '---' end as subjectname, \n" +
				" case  when (entrysubjectfullname1 like '${gs}/%' or entrysubjectfullname1='${gs}') then '---' else entrysubjectfullname1 end  as othersubjectname, \n" +
				" '${subjectfullname22}' as subjectfullname22 ,";
			
			str1 = " 	and subjectid in (${subjectid})  \n" ;
			str2 = "	and (entrysubjectfullname1 = '${gs}' or entrysubjectfullname1 like '${gs}/%') \n ";
		}else{
			opt = " case entrydirction when 1 then entrysubjectfullname1 else '---' end as subjectname, \n" +
			" case entrydirction when 1 then '---' else entrysubjectfullname1 end  as othersubjectname, \n" +
			" '' as subjectfullname22 ,";
			
		}
		
		String str3 = "";
		if("-1".equals(dir)){
			str3 = " and not exists (" +
				" select distinct b.vchid,b.entrysubjectname1 from z_voucherspotcheck b " +
				" where 1=1 and b.ProjectID = '${curProjectid}'" +
				" and b.subjectid in (${subjectid})" +
				" and (b.entrysubjectfullname1 = '${gs}' or b.entrysubjectfullname1 like '${gs}/%') " +
				" and b.entrydirction = 1 and a.vchid=b.vchid " +
				" ) ";
		}
		
		if (iMode == 1) {//刷单边
			
			strSql = "\n select distinct a.*,  \n" +
			" if(left(other,1)=',',substring(other,2),other) as othersubjectname,  \n" +
			" if(left(other,1)=',',substring(other,2),other) as subjectname2  \n" +
			" from(  \n" +
			" 	select a.*, a.subjectname1 as   subjectname, replace(b.allsubjectname,a.accname,'')  as other  \n" +
			"	from ( \n" +
			"		select distinct a.entryvchdate vchdate,vchid voucherid,entrySerail serail, \n" +
			"		concat(if( length(entrytypeid)=0 || entrytypeid is null,'',concat(entrytypeid,'-')),cast(entryoldvoucherid as char)) as typenumber, \n" +
			"		entrysummary summary,entrysubjectfullname1 as subjectname1,concat(',',entrysubjectname1) as accname,  \n" +
			"		case entrydirction when 1 then entryoccurvalue else 0 end as debitocc,  \n" +
			"		case entrydirction when -1 then entryoccurvalue else 0 end  as creditocc,  \n" +
			"		entryoccurvalue as occ,  \n" +
			"		case entrydirction when 1 then concat('借:',entrysubjectfullname1) else concat('贷:',entrysubjectfullname1) end as subjectname22,  \n" +
			"		group_concat(distinct b.name) Createor,group_concat(distinct judge) judge,  \n" +
			"		ifnull(f.assitemid,'') as assitemid,ifnull(f.assitemname,entrysubjectfullname1) as assitemname " +
			
			"		from z_voucherspotcheck a  \n" +
			"		left join k_user b on a.Createor=b.id \n" +
//			"		left join (" +
//			"           select a.vchid,group_concat(distinct b.name) as name " +
//			"           from z_voucherspotcheck a,k_user b " +
//			"			where a.ProjectID = '${curProjectid}'  " +
//			"			and a.vchid in (${vchids}) \n" + 
//			              str1 + 
//			              str2 +
//			              direction +
//			"       	and a.Createor=b.id  " +
//			"		) b on a.vchid=b.vchid \n" +
			
			" 		left join ( \n" +
	 		"			select distinct voucherid,a.assitemid,subjectid subjectid1 ,asstotalname1,assitemname from c_assitementry a ,c_assitementryacc b  \n" +
	 		" 			where a.accpackageid ='${curPackageid}' and b.accpackageid ='${curPackageid}' and submonth=1  and a.voucherid in (${vchids}) \n" +
	 		" 			${sqlstring} \n" +
	 		" 			and a.assitemid=b.assitemid and a.subjectid =b.accid  \n" +
	 		"		) f on a.vchid=f.voucherid  \n" +
	 		
			"		where a.ProjectID = '${curProjectid}'  " +
			"		and a.vchid in (${vchids}) \n" + 
			str1 + 
//			str2 +
//			direction +
			
			"		group by vchdate,Serail,typenumber) a ,( \n" +
			"		select distinct a.vchid voucherid,concat(',',group_concat(distinct entrysubjectname1)) as allsubjectname  \n" +
			"		from (select distinct a.vchid,entrysubjectname1 " +
			"		from z_voucherspotcheck a  \n" +
			"		where 1=1 \n" +
			str1 + 
			str2 +
			direction +
			str3 + 
			"		) a  group by vchid   \n" +
			"	) b where a.voucherid=b.voucherid   \n" +
			" )a ";
			
			
					

		} else {//刷整张凭证
			
			strSql = "\n select * from (" +
					"select a.*,ifnull(f.assitemid,'') as assitemid,ifnull(f.assitemname,subjectname1) as assitemname from ( \n" +
					" 	select distinct a.entryvchdate vchdate,a.vchid as voucherid,entrySerail Serail, \n" +
					" 	concat(if( length(entrytypeid)=0 || entrytypeid is null,'',concat(entrytypeid,'-')),cast(entryoldvoucherid as char)) as typenumber,entrysummary as summary,  \n" +
					opt + 
					" 	entrysubjectfullname1 as subjectname1, \n" +
					" 	case entrydirction when 1 then entryoccurvalue else 0 end as debitocc, \n" +
					" 	case entrydirction when -1 then entryoccurvalue else 0 end  as creditocc, \n" +
					" 	entryoccurvalue as occ, \n" +
					" 	case entrydirction when 1 then concat('借:',entrysubjectfullname1) else concat('贷:',entrysubjectfullname1) end as subjectname2, \n" +
					" 	group_concat(distinct b.name) Createor,group_concat(distinct judge) judge \n" +
					" 	from z_voucherspotcheck a \n" +
					"	left join k_user b on a.Createor=b.id \n" +
//					" 	left join (" +
//					"       select a.vchid,group_concat(distinct b.name ) as name " +
//					"       from z_voucherspotcheck ,k_user b  \n" +
//					" 	    where a.ProjectID = '${curProjectid}'  " +
//					"	    and a.vchid in (${vchids}) \n" +
//					        str1 + 
//					"       and a.Createor=b.id" +
//					"		group by a.vchid " +
//					"    ) b \n" +
//					" 	on a.vchid=b.vchid  \n" +
					" 	where a.ProjectID = '${curProjectid}'  " +
					"	and a.vchid in (${vchids}) \n" +
					str1 + 
					" 	group by vchdate,Serail,typenumber) a left join ( \n" +
			 		"		select distinct voucherid,a.assitemid,subjectid,asstotalname1,assitemname from c_assitementry a ,c_assitementryacc b  \n" +
			 		" 		where a.accpackageid ='${curPackageid}' and b.accpackageid ='${curPackageid}' and submonth=1  \n" +
			 		" 		${sqlstring} \n" +
			 		"		and a.voucherid in (${vchids}) " +
			 		" 		and a.assitemid=b.assitemid and a.subjectid =b.accid  \n" +
			 		") f on a.voucherid=f.voucherid  \n" +
			
					") e";
			
			

		}
		return strSql;
	}
	
	//只刷科目的
	public String getSql(int iMode,String direction,String opt,String dir) {
		String strSql = "";

		String str1 = "";
		String str2 = "";
		if("1".equals(opt)){
			opt = "case  when (entrysubjectfullname1 like '${gs}/%' or entrysubjectfullname1='${gs}') then entrysubjectfullname1 else '---' end as subjectname, \n" +
				" case  when (entrysubjectfullname1 like '${gs}/%' or entrysubjectfullname1='${gs}') then '---' else entrysubjectfullname1 end  as othersubjectname, \n" +
				" '${subjectfullname22}' as subjectfullname22 ,";
			
			str1 = " 	and subjectid in (${subjectid}) \n" ;
			str2 = "	and (entrysubjectfullname1 = '${gs}' or entrysubjectfullname1 like '${gs}/%') \n ";
		}else{
			opt = "case entrydirction when 1 then entrysubjectfullname1 else '---' end as subjectname, \n" +
			" case entrydirction when 1 then '---' else entrysubjectfullname1 end  as othersubjectname, \n" +
			" '' as subjectfullname22 ,";
		}
		
		String str3 = "";
		if("-1".equals(dir)){
			str3 = " and not exists (" +
				" select distinct b.vchid,entrysubjectname1 from z_voucherspotcheck b " +
				" where 1=1 and b.ProjectID = '${curProjectid}'" +
				" and b.subjectid in (${subjectid})" +
				" and (b.entrysubjectfullname1 = '${gs}' or b.entrysubjectfullname1 like '${gs}/%') " +
				" and b.entrydirction = 1 and a.vchid=b.vchid " +
				" ) ";
		}
		
		if (iMode == 1) {
			strSql = "\n select distinct a.*,  \n" +
					" if(left(other,1)=',',substring(other,2),other) as othersubjectname,  \n" +
					" if(left(other,1)=',',substring(other,2),other) as subjectname2  \n" +
					" from(  \n" +
					" 	select a.*, a.subjectname1 as   subjectname, replace(b.allsubjectname,a.accname,'')  as other  \n" +
					"	from ( \n" +
					"		select distinct a.entryvchdate vchdate,vchid voucherid,entrySerail serail, \n" +
					"		concat(if( length(entrytypeid)=0 || entrytypeid is null,'',concat(entrytypeid,'-')),cast(entryoldvoucherid as char)) as typenumber, \n" +
					"		entrysummary summary,entrysubjectfullname1 as subjectname1,concat(',',entrysubjectname1) as accname,  \n" +
					"		case entrydirction when 1 then entryoccurvalue else 0 end as debitocc,  \n" +
					"		case entrydirction when -1 then entryoccurvalue else 0 end  as creditocc,  \n" +
					"		entryoccurvalue as occ,  \n" +
					"		case entrydirction when 1 then concat('借:',entrysubjectfullname1) else concat('贷:',entrysubjectfullname1) end as subjectname22,  \n" +
					"		group_concat(distinct b.name) Createor,group_concat(distinct judge) judge  \n" +
					"		from z_voucherspotcheck a  \n" +
					"		left join k_user b on a.Createor=b.id \n" +
//					"		left join (" +
//					"             select a.vchid,group_concat(distinct b.name)  as name from z_voucherspotcheck a ,k_user b \n" +
//					"		      where a.ProjectID = '${curProjectid}'  and a.vchid in (${vchids})\n" + 
//									str1 + 
//									str2 +
//									direction +
//					"     		  and a.Createor=b.id group by a.vchid " +
//					"    )b on a.vchid=b.vchid  \n" +
					"		where a.ProjectID = '${curProjectid}'  and a.vchid in (${vchids})\n" + 
						str1 + 
//						str2 +
//						direction +
					"	group by vchdate,Serail,typenumber) a ,( \n" +
					"		select distinct a.vchid voucherid,concat(',',group_concat(distinct entrysubjectname1)) as allsubjectname  from(\n" +
					"		select distinct a.vchid,entrysubjectname1 " +
					"		from z_voucherspotcheck a  \n" +
					"		where 1=1 and a.ProjectID = '${curProjectid}'\n" +
						str1 + 
						str2 +
						direction +
						str3 + 
					"		) a	group by vchid   \n" +
					"	) b where a.voucherid=b.voucherid   \n" +
					" )a ";
			
			

		} else {
			strSql = "\n select * from ( \n" +
					" 	select distinct a.entryvchdate vchdate,a.vchid as voucherid,entrySerail Serail, \n" +
					" 	concat(if( length(entrytypeid)=0 || entrytypeid is null,'',concat(entrytypeid,'-')),cast(entryoldvoucherid as char)) as typenumber,entrysummary as summary,  \n" +
					opt + 
					" 	entrysubjectfullname1 as subjectname1, \n" +
					" 	case entrydirction when 1 then entryoccurvalue else 0 end as debitocc, \n" +
					" 	case entrydirction when -1 then entryoccurvalue else 0 end  as creditocc, \n" +
					" 	entryoccurvalue as occ, \n" +
					" 	case entrydirction when 1 then concat('借:',entrysubjectfullname1) else concat('贷:',entrysubjectfullname1) end as subjectname2, \n" +
					" 	group_concat(distinct b.name) Createor,group_concat(distinct judge) judge \n" +
					" 	from z_voucherspotcheck a \n" +
					"	left join k_user b on a.Createor=b.id \n" +
//					" 	left join (" +
//					"		select vchid,group_concat(distinct b.name) as name " +
//					"       from z_voucherspotcheck a,k_user b" +
//					" 	    where a.ProjectID = '${curProjectid}'  and a.vchid in (${vchids}) \n" + str1 +
//					" 	    and a.Createor=b.id  \n" +
//					"       group by a.vchid" +
//					"   )b on a.vchid=b.vchid " +
					" 	where a.ProjectID = '${curProjectid}'  and a.vchid in (${vchids})\n" +
					str1 + 
					" group by vchdate,Serail,typenumber ) a ";
			
		}
		return strSql;

	}

}
