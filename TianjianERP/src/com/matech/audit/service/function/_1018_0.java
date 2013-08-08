package com.matech.audit.service.function;

/**
 * 
 *1015公式：
 *参数：
 *direction　用来判断是刷单边还是全部
 *assitem 用来判断是刷核算还是科目 
 *subject 用来判断是刷所有分录还是指定科目分录
 *subjectname 刷指定科目分录时，定义科目名称，可以刷多科目，以“`”分隔
 *year 指定年份，也支持-1，-2等，表示从最后一年倒退或正推
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
 *assitemid				：核算编号（只有assitem＝1时才有）
 *assitemname			：核算名称（只有assitem＝1时才有）
 *entrysubjectname		：(科目编号)科目名称
 *=取列公式覆盖(1018, "", "entrysubjectname")
 */
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.framework.pub.util.ASFuntion;

public class _1018_0 extends AbstractAreaFunction {

	@Override
	public ResultSet process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {
		ASFuntion CHF=new ASFuntion();

		String resultSql = "";
		Statement st = null;
		ResultSet rs = null;
		try {
			
			String direction = (String) args.get("direction");	//用来判断是刷单边还是全部
			String assitem = (String) args.get("assitem");	//用来判断是刷核算还是科目

			String subject = (String) args.get("subject");//用来判断是刷所有分录还是指定科目分录
			String subjectname = (String) args.get("subjectname"); //刷指定科目分录时，定义科目名称，可以刷多科目，以“;”分隔
			
			String showtype = request.getParameter("showtype");  //刷分录还是整张凭证
System.out.println("###############"+showtype);			
			String acc = (String) args.get("curPackageid");
			String curProjectid = (String) args.get("curProjectid");
			
			String sqlJudge = "";
			String judge = CHF.showNull(request.getParameter("judge"));	
			//为空或为all，刷所有抽凭；为“关键项目”，只刷【关键项目】的抽凭；为“非关键项目”，刷除【关键项目】以外的抽凭
			
			if(!"".equals(judge) && !"all".equals(judge)){
				if(judge.indexOf("非")>-1){
					//有“无”字
					judge = CHF.replaceStr(judge, "非", "");
					sqlJudge = " and judge not like '%"+judge+"%' ";
				}else{
					sqlJudge = " and judge like '%"+judge+"%' ";
				}
			}
			
			if("".equals(subjectname) || subjectname == null) {
				 //如果科目名称为空，则通过前台提交的刷新底稿编号去取得对应任务得科目名称；
				subjectname = getTaskSubjectNameByTaskCode(conn,curProjectid,(String)request.getParameter("curTaskCode"));	
			}
			
			st = conn.createStatement();
			
			String strStartYearMonth="",strEndYearMonth="";
			int[] result=getProjectAuditAreaByProjectid(conn,curProjectid);
			strStartYearMonth=String.valueOf(result[0]*12+result[1]);
            strEndYearMonth=String.valueOf(result[2]*12+result[3]);
            
        	String year = (String) args.get("year");//8月25号新加年份
			String subYearSql = "";
			
            
        	if(year!=null&&!"".equals(year)){
				if(Integer.parseInt(year.trim())<=10){
					
					year = String.valueOf(result[2]+Integer.parseInt(year.trim().replaceAll("－", "-")));
					
				}
					subYearSql = " and entryVchDate like '"+year+"%'";
				
			}
			
			String sql = "";
			String dir = direction;
			int iMode = 0;  //单边
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
					resultSql = getSql(iMode,direction,"1",dir,showtype);
				}else{
					resultSql = getSql1(iMode,direction,"1",dir,showtype);
				}
				
				String[] sName =null;
				if (subjectname!=null && !"".equals(subjectname)){
					subjectname.replaceAll("`", ";");
					sName = subjectname.split(";");
				}
				
				sql = "";
				for (int i = 0; sName!=null && i < sName.length; i++) {
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
					
					//支持抽凭科目为一级时，用二级的科目来刷				
					String[] sNames = null;
					String subSqlWhere = "";
					if(sName[i].indexOf("/")>-1){
						
						sNames = sName[i].split("/");//不是一级科目名称
						String[] sNamess = new String[sNames.length];
						for (int j = 0; j < sNames.length; j++) {
							for(int h = 0;h<=j;h++){
								if(h==0){
									sNamess[j] = sNames[h];
								}else{
									sNamess[j] += "/"+sNames[h];
								}	
							}
							
							if(j==0){
								subSqlWhere = " subjectfullname2='"+sNamess[j]+"' ";
							}else{
								subSqlWhere += " or subjectfullname2='"+sNamess[j]+"' ";
							}
						}
						
						
					}
					
					
					String subSqlWhere1 = "";
					
					if(sNames!=null){//不是一级科目名称
						sql1 = "select group_concat(distinct \"'\",subjectid,\"'\") as gs  \n" +
						"from c_account   \n" +
						"where 1=1" +
//						"and accpackageid ='"+acc+"'   \n" +
//						"and submonth=1   \n" +
						" and SubYearMonth*12 + SubMonth>= "+strStartYearMonth+" " +
						" and SubYearMonth*12 + SubMonth<= "+strEndYearMonth+" " +
						"and (subjectfullname2 = '"+sName[i]+"' or subjectfullname2 like '"+sName[i]+"/%')  ";
						
						rs = st.executeQuery(sql1);
						String s1 = "";
						if(rs.next()){
							s1 = rs.getString(1);
						}
						rs.close();
						
						subSqlWhere1 = " and entrysubjectid in ("+s1+")  ";
						
					}else{
						subSqlWhere = " subjectfullname2 = '"+sName[i]+"' ";
					}
					
					sql1 = "select group_concat(distinct \"'\",subjectid,\"'\") as gs  \n" +
							"from c_account   \n" +
							"where 1=1" +
							" and SubYearMonth*12 + SubMonth>= "+strStartYearMonth+" " +
							" and SubYearMonth*12 + SubMonth<= "+strEndYearMonth+" " +
							"and ( "+subSqlWhere+" or subjectfullname2 like '"+sName[i]+"/%')   ";
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
					
					sql1 = "select group_concat(distinct vchid),group_concat(distinct Createor) from (" +
						"	select distinct vchid,Createor " +
						"	from z_voucherspotcheck a " +
						"	where a.ProjectID = '"+curProjectid+"' and  Judge not like '%-截止性抽凭%' "+subYearSql+" " +
						"	and subjectid in ("+s1+") "+subSqlWhere1+"" +
						"	group by vchid,Createor" +
						"	having count(*) <=500 " +
						") a ";
					rs = st.executeQuery(sql1);
					if(rs.next()){
						s1 = rs.getString(1);
						if(s1 == null || "".equals(s1.trim())){
							args.put("vchids", "-1");
							args.put("Createors","-1");
						}else{
							args.put("vchids",rs.getString(1));
							args.put("Createors",rs.getString(2));
						}
						
					}else{
						args.put("vchids", "-1");
						args.put("Createors","-1");
					}
					rs.close();

					sql1 = "update  z_voucherspotcheck a,(" +
					"		select b.voucherid  ,b.Serail ,b.subjectid ,concat('|核算：',group_concat(distinct AssItemName)) as assitemname" +
					"		from c_assitementry b ,c_assitem  c" +
					"		where 1=1  " +
					"		and b.subjectid in (${subjectid}) " +
					"		and c.accid in (${subjectid}) " +
					"		and b.voucherid in (${vchids}) " +
					"		and b.AccPackageID = c.AccPackageID" +
					"		and b.subjectid = c.accid" +
					"		and b.assitemid = c.assitemid" +
					"		group by b.voucherid  ,b.Serail ,b.subjectid " +
					"	) b " +
					"	set entrybankid = assitemname" +
					"	where a.vchid = b.voucherid  and entrySerail = b.Serail and a.entrysubjectid=b.subjectid ";
					sql1 = this.setSqlArguments(sql1, args);
					
					st.execute(sql1);
					
					System.out.println("========================");
					sql += this.setSqlArguments(resultSql, args) + " union";

				}
				if(sql.length()==0) {
					throw new Exception("请在底稿属性里选择对应科目或在公式里设置对应科目");
				} else {
					sql = sql.substring(0,sql.length()-5);
				}
				
				
			}else{			//刷所有分录
				String sql1 = "select group_concat(distinct vchid),group_concat(distinct Createor) " +
				"from (" +
				"	select distinct vchid,Createor " +
				"	from z_voucherspotcheck a " +
				"	where a.ProjectID = '"+curProjectid+"' and Judge not like '%-截止性抽凭%' "+subYearSql+" " +
				"	group by vchid,Createor " +
				"	having count(*) <=500 " +
				") a ";
				System.out.println("yzm:sql2= "+sql1);
				rs = st.executeQuery(sql1);
				if(rs.next()){
					//args.put("vchids",rs.getString(1));
					if(rs.getString(1) == null || "".equals(rs.getString(1).trim())){
						args.put("vchids", "-1");
						args.put("Createors", "-1");
					}else{
						args.put("vchids",rs.getString(1));
						args.put("Createors",rs.getString(2));
					}
				}else{
					args.put("vchids", "-1");
					args.put("Createors","-1");
				}
				rs.close();
				
				sql1 = "update  z_voucherspotcheck a,(" +
				"		select b.voucherid  ,b.Serail ,b.subjectid ,concat('|核算：',group_concat(distinct AssItemName)) as assitemname" +
				"		from c_assitementry b ,c_assitem  c" +
				"		where 1=1  " +
				"		and b.subjectid in (${subjectid}) " +
				"		and c.accid in (${subjectid}) " +
				"		and b.voucherid in (${vchids}) " +
				"		and b.AccPackageID = c.AccPackageID" +
				"		and b.subjectid = c.accid" +
				"		and b.assitemid = c.assitemid" +
				"		group by b.voucherid  ,b.Serail ,b.subjectid " +
				"	) b " +
				"	set entrybankid = assitemname" +
				"	where a.vchid = b.voucherid  and entrySerail = b.Serail and a.entrysubjectid=b.subjectid ";
				sql1 = this.setSqlArguments(sql1, args);
				st.execute(sql1);
				
				
				if(assitem == null || "".equals(assitem)){
					resultSql = getSql(iMode,direction,"0",dir,showtype);
				}else{
					resultSql = getSql1(iMode,direction,"0",dir,showtype);
				}	
				sql = this.setSqlArguments(resultSql, args);
			}
			
			if(assitem == null || "".equals(assitem)){
				sql = " "+sql+" where 1=1 "+sqlJudge+" order by  Createor,vchdate,typenumber,Serail"; 
			}else{
				sql = " "+sql+" where 1=1 "+sqlJudge+" order by  Createor,AssItemName,vchdate,typenumber,Serail"; 
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
	public String getSql1(int iMode,String direction,String opt,String dir,String showtype){
		String strSql = "";

		String str1 = "";
		String str2 = "";
		
		if("1".equals(opt)){
			opt = "case  when (entrysubjectfullname1 like '${gs}/%' or entrysubjectfullname1='${gs}') then entrysubjectfullname1 else '---' end as subjectname, \n" +
				" case  when (entrysubjectfullname1 like '${gs}/%' or entrysubjectfullname1='${gs}') then '---' else entrysubjectfullname1 end  as othersubjectname, \n" +
				" '${subjectfullname22}' as subjectfullname22 ,";
			str1 = " 	and a.subjectid in (${subjectid})  \n" ;
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
		
		String st = "" ;
		if("1".equals(showtype)) {
			st = " AND ((a.subjectid LIKE CONCAT(a.entrySubjectID,'%')) OR  (a.entrySubjectID LIKE CONCAT(a.subjectid,'%')))" ;
		}
		
		if (iMode == 1) {//刷单边
			
			strSql = "\n select distinct a.*,  \n" +
			" if(left(other,1)=',',substring(other,2),other) as othersubjectname,  \n" +
			" if(left(other,1)=',',substring(other,2),other) as subjectname2,a.subjectnames as othersubject  \n" +
			" from(  \n" +
			" 	select a.*, a.subjectname1 as   subjectname, replace(b.allsubjectname,a.accname,'')  as other ,CONCAT('(',entrysubjectid,')',entrysubjectname1) AS entrysubjectname \n" +
			"	from ( \n" +
			"		select distinct a.entryvchdate vchdate,vchid voucherid,entrySerail serail, \n" +
			"		concat(if( length(entrytypeid)=0 || entrytypeid is null,'',concat(entrytypeid,'-')),cast(entryoldvoucherid as char)) as typenumber, \n" +
			"		entrysummary summary,entrysubjectfullname1 as subjectname1,concat(',',entrysubjectname1) as accname,  \n" +
			"		case entrydirction when 1 then entryoccurvalue else 0 end as debitocc,  \n" +
			"		case entrydirction when -1 then entryoccurvalue else 0 end  as creditocc,  \n" +
			"		entryoccurvalue as occ,  \n" +
			"		case entrydirction when 1 then concat('借:',entrysubjectfullname1) else concat('贷:',entrysubjectfullname1) end as subjectname22,  \n" +
			"		group_concat(distinct b.name) Createor,group_concat(distinct judge) judge,  \n" +
			"		ifnull(f.assitemid,'') as assitemid,ifnull(f.assitemname,entrysubjectfullname1) as assitemname,subjectnames,entrysubjectid,	entrysubjectname1 " +
			
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
			"			SELECT a.*,GROUP_CONCAT(DISTINCT IF(subjectname!=b.subjectfullname&&CONCAT(IF(LOCATE('/',b.subjectfullname)=0,b.subjectfullname, \n" +
			"			SUBSTR(b.subjectfullname,1,LOCATE('/',b.subjectfullname)-1)),'/',subjectname)!=b.subjectfullname   , \n" +
			"			CONCAT(IF(LOCATE('/',b.subjectfullname)=0,b.subjectfullname,SUBSTR(b.subjectfullname,1,LOCATE('/',b.subjectfullname)-1)),'/../',subjectname)  ,b.subjectfullname)) subjectnames \n" +
			"			FROM ( \n" +
			"				SELECT a.*,REPLACE(REPLACE(IF(b.Dirction*b.occurvalue<0,REPLACE(b.debitsubjects,',,',','),REPLACE(b.creditsubjects,',,',',')),',,',','),',,',',') subjects  FROM ( \n"+
			"				SELECT DISTINCT autoid,vchdate,typeid,voucherid,OldVoucherID,a.assitemid,subjectid,asstotalname1,assitemname,a.Serail FROM c_assitementry a ,c_assitementryacc b \n"+
			"				WHERE a.accpackageid ='${curPackageid}' AND b.accpackageid ='${curPackageid}' AND submonth=1 \n"+
			"				AND a.voucherid IN (${vchids}) ${sqlstring} AND a.assitemid=b.assitemid AND a.subjectid =b.accid \n"+
			"			) a \n"+
			"			LEFT JOIN c_subjectentry b ON a.voucherid = b.voucherid AND a.Serail = b.Serail \n"+
			"		 ) a \n"+
			"		 LEFT JOIN ("+
			"			SELECT * FROM c_accpkgsubject WHERE  AccPackageID='${curPackageid}' \n"+
			"		 ) b ON a.subjects LIKE CONCAT('%,',b.subjectid,',%') GROUP BY a.autoid \n"+
			"		 ORDER BY vchdate DESC,typeid DESC,ABS(OldVoucherID) DESC \n"+
			/*
	 		"			select distinct voucherid,a.assitemid,subjectid subjectid1 ,asstotalname1,assitemname,a.serail from c_assitementry a ,c_assitementryacc b  \n" +
	 		" 			where a.accpackageid ='${curPackageid}' and b.accpackageid ='${curPackageid}' and submonth=1  and a.voucherid in (${vchids}) \n" +
	 		" 			${sqlstring} \n" +
	 		" 			and a.assitemid=b.assitemid and a.subjectid =b.accid  \n" + */
	 		"		) f on a.vchid=f.voucherid  and  a.entrySerail=f.serail\n" +
	 		
			"		where a.ProjectID = '${curProjectid}' and a.Judge not like '%-截止性抽凭%' " +
			"		and a.vchid in (${vchids}) \n" + 
		
			str1 + 
			st +
	//		str2 +
	//		direction +
			
			"		group by voucherid,Serail) a ,( \n" +
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
					"select a.*,ifnull(f.assitemid,'') as assitemid,ifnull(f.assitemname,subjectname1) as assitemname,f.subjectnames as othersubject,CONCAT('(',entrysubjectid,')',entrysubjectname1) AS entrysubjectname from ( \n" +
					" 	select distinct a.entryvchdate vchdate,a.vchid as voucherid,entrySerail Serail, \n" +
					" 	concat(if( length(entrytypeid)=0 || entrytypeid is null,'',concat(entrytypeid,'-')),cast(entryoldvoucherid as char)) as typenumber,entrysummary as summary,  \n" +
					opt + 
					" 	entrysubjectfullname1 as subjectname1, \n" +
					" 	case entrydirction when 1 then entryoccurvalue else 0 end as debitocc, \n" +
					" 	case entrydirction when -1 then entryoccurvalue else 0 end  as creditocc, \n" +
					" 	entryoccurvalue as occ, \n" +
					" 	case entrydirction when 1 then concat('借:',entrysubjectfullname1) else concat('贷:',entrysubjectfullname1) end as subjectname2, \n" +
					" 	group_concat(distinct b.name) Createor,group_concat(distinct judge) judge,entrysubjectid,	entrysubjectname1 \n" +
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
					" 	where a.ProjectID = '${curProjectid}'  and a.Judge not like '%-截止性抽凭%' " +
					"	and a.vchid in (${vchids}) \n" +
					str1 + 
					st +
					" 	group by voucherid,Serail) a left join ( \n" +
					
					"			SELECT a.*,GROUP_CONCAT(DISTINCT IF(subjectname!=b.subjectfullname&&CONCAT(IF(LOCATE('/',b.subjectfullname)=0,b.subjectfullname, \n" +
					"			SUBSTR(b.subjectfullname,1,LOCATE('/',b.subjectfullname)-1)),'/',subjectname)!=b.subjectfullname   , \n" +
					"			CONCAT(IF(LOCATE('/',b.subjectfullname)=0,b.subjectfullname,SUBSTR(b.subjectfullname,1,LOCATE('/',b.subjectfullname)-1)),'/../',subjectname)  ,b.subjectfullname)) subjectnames \n" +
					"			FROM ( \n" +
					"				SELECT a.*,REPLACE(REPLACE(IF(b.Dirction*b.occurvalue<0,REPLACE(b.debitsubjects,',,',','),REPLACE(b.creditsubjects,',,',',')),',,',','),',,',',') subjects  FROM ( \n"+
					"				SELECT DISTINCT autoid,vchdate,typeid,voucherid,OldVoucherID,a.assitemid,subjectid,asstotalname1,assitemname,a.Serail FROM c_assitementry a ,c_assitementryacc b \n"+
					"				WHERE a.accpackageid ='${curPackageid}' AND b.accpackageid ='${curPackageid}' AND submonth=1 \n"+
					"				AND a.voucherid IN (${vchids}) ${sqlstring} AND a.assitemid=b.assitemid AND a.subjectid =b.accid \n"+
					"			) a \n"+
					"			LEFT JOIN c_subjectentry b ON a.voucherid = b.voucherid AND a.Serail = b.Serail \n"+
					"		 ) a \n"+
					"		 LEFT JOIN ("+
					"			SELECT * FROM c_accpkgsubject WHERE  AccPackageID='${curPackageid}' \n"+
					"		 ) b ON a.subjects LIKE CONCAT('%,',b.subjectid,',%') GROUP BY a.autoid \n"+
					"		 ORDER BY vchdate DESC,typeid DESC,ABS(OldVoucherID) DESC \n"+
					
					/*
			 		"		select distinct voucherid,a.assitemid,subjectid,asstotalname1,assitemname,a.Serail from c_assitementry a ,c_assitementryacc b  \n" +
			 		" 		where a.accpackageid ='${curPackageid}' and b.accpackageid ='${curPackageid}' and submonth=1  \n" +
			 		" 		${sqlstring} \n" +
			 		"		and a.voucherid in (${vchids}) " +
			 		" 		and a.assitemid=b.assitemid and a.subjectid =b.accid  \n" +
			 		*/
			 		") f on a.voucherid=f.voucherid  and a.Serail=f.Serail\n" +
			
					") e";
			
			

		}
		return strSql;
	}
	
	//只刷科目的
	/**
	 * iMode 单边
	 * direction 方向
	 */
	public String getSql(int iMode,String direction,String opt,String dir,String showtype) {
		String strSql = "";

		String str1 = "";
		String str2 = "";
		
		
		if("1".equals(opt)){
			opt = "case  when (entrysubjectfullname1 like '${gs}/%' or entrysubjectfullname1='${gs}') then entrysubjectfullname1 else '---' end as subjectname, \n" +
				" case  when (entrysubjectfullname1 like '${gs}/%' or entrysubjectfullname1='${gs}') then '---' else entrysubjectfullname1 end  as othersubjectname, \n" +
				" '${subjectfullname22}' as subjectfullname22 ,";
			
			str1 = " 	and subjectid in (${subjectid}) \n" ;
			str2 = "	and (entrysubjectfullname1 = '${gs}' or entrysubjectfullname1 like '${gs}/%') \n ";
			
//			str4 = " and subjectid in (${subjectid})  and accid in (${subjectid}) ";
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
				str1 + 
				str2 +
				" and b.entrydirction = 1 and a.vchid=b.vchid " +
				" ) ";
		}
		
		String st = "" ;
		if("1".equals(showtype)) {
			st = " AND ((a.subjectid LIKE CONCAT(a.entrySubjectID,'%')) OR  (a.entrySubjectID LIKE CONCAT(a.subjectid,'%')))" ;
		}
		
		if (iMode == 1) {
			//刷借方或者贷方的单边
			strSql ="\n select * from ( \n" + 
					"	select distinct a.vchdate,a.voucherid,a.serail,  typenumber,a.summary,  \n" +
					"	accname,debitocc,creditocc,occ," +
					"	concat(subjectname1,entrybankid) as subjectname1, \n" +
					"	concat(subjectname22,entrybankid) as subjectname22, \n" +
					"	concat(subjectname,entrybankid) as subjectname,Createor,judge,other,\n" +
					"	if(left(other,1)=',',substring(other,2),other) as othersubjectname,  \n" +
					"	if(left(other,1)=',',substring(other,2),other) as subjectname2,othersubject, entrysubjectname  \n" +
					"	from(  \n" +
					"		select a.*, a.subjectname1 as   subjectname, replace(b.allsubjectname,a.accname,'')  as other  \n" +
					"		from ( \n" +
					
					"			SELECT a.*,subjectnames AS othersubject ,CONCAT('(',entrysubjectid,')',entrysubjectname1) AS entrysubjectname FROM (" +
					
					"			select a.entryvchdate vchdate,vchid voucherid,entrySerail serail, \n" +
					"			concat(if( length(entrytypeid)=0 || entrytypeid is null,'',concat(entrytypeid,'-')),cast(entryoldvoucherid as char)) as typenumber, \n" +
					"			entrysummary summary,entrysubjectfullname1 as subjectname1,concat(',',entrysubjectname1) as accname,  \n" +
					"			case entrydirction when 1 then entryoccurvalue else 0 end as debitocc,  \n" +
					"			case entrydirction when -1 then entryoccurvalue else 0 end  as creditocc,entryoccurvalue as occ,  \n" +
					"			case entrydirction when 1 then concat('借:',entrysubjectfullname1) else concat('贷:',entrysubjectfullname1) end as subjectname22,  \n" +
					"			b.Createor,judge,entryAccPackageID,entrysubjectid,entryoldvoucherid,entryVchDate,entrytypeid,entrySerail,entrybankid,entrysubjectname1  \n" +
					"			from z_voucherspotcheck a, \n" +
					"			(select group_concat(distinct b.name) Createor	 from k_user b where id in (-1,${Createors})	) b \n" +
					"			where a.ProjectID = '${curProjectid}'  and a.Judge not like '%-截止性抽凭%' \n and a.vchid in (${vchids})\n" + 
									str1 + 
									st +
					"			\n group by voucherid,Serail \n" +
					
					"			) a," +
					"		(SELECT vchDate,oldvoucherid,typeid,serail,GROUP_CONCAT(DISTINCT IF(subjectname!=b.subjectfullname&&CONCAT(IF(LOCATE('/',b.subjectfullname)=0,b.subjectfullname,SUBSTR(b.subjectfullname,1,LOCATE('/',b.subjectfullname)-1)),'/',subjectname)!=b.subjectfullname   ,CONCAT(IF(LOCATE('/',b.subjectfullname)=0,b.subjectfullname,SUBSTR(b.subjectfullname,1,LOCATE('/',b.subjectfullname)-1)),'/../',subjectname)  ,b.subjectfullname)) subjectnames " +
					"		FROM ( \n" +
					"			SELECT *,REPLACE(REPLACE(IF(Dirction*occurvalue<0,REPLACE(debitsubjects,',,',','),REPLACE(creditsubjects,',,',',')),',,',','),',,',',') subjects \n" +
					" 			FROM c_subjectentry )a \n" +
					" 			LEFT JOIN (SELECT * FROM c_accpkgsubject  WHERE  AccPackageID=${curPackageid} ) b \n" +
					" 			ON  a.subjects LIKE CONCAT('%,',b.subjectid,',%')  \n " +
					" 			WHERE  a.voucherid IN (${vchids}) \n " +
					" 			GROUP BY a.autoid \n " +   
					" 			ORDER BY vchdate DESC,typeid DESC,ABS(OldVoucherID) DESC \n " +
					"		) c \n" +
					"		where 1=1 " +
					"		AND a.entryvchdate = c.vchDate AND a.entryoldvoucherid = c.oldvoucherid AND a.entrytypeid = c.typeid " +
					"		AND a.entryoldvoucherid = c.oldvoucherid AND a.entryserail = c.serail " +
					
					"		) a ,( \n" +
					"			select distinct a.vchid voucherid,concat(',',group_concat(distinct entrysubjectname1)) as allsubjectname \n" + 
					"			from( \n" +
					"				select distinct a.vchid,entrysubjectname1 \n" +
					"				from z_voucherspotcheck a  \n" +
					"				where 1=1 and a.ProjectID = '${curProjectid}'\n" +
								str1 + 
								str2 +
								direction +
								str3 + 
					"			) a	group by vchid   \n" +
					"		) b where a.voucherid=b.voucherid   \n" +
					" 	)a \n" + 

					" )a ";
			
			

		} else {
			//刷全部
			strSql = "\nselect * from ( \n" +
					"\n	select distinct a.vchdate,a.voucherid,a.Serail, typenumber,a.summary, subjectname1," +
					"	concat(subjectname,entrybankid) as subjectname, \n" +
					"	concat(othersubjectname,entrybankid) as othersubjectname,subjectfullname22,\n" +
					"	concat(subjectname2,entrybankid) as subjectname2,debitocc,creditocc,occ,Createor, judge,subjectnames AS othersubject,CONCAT('(',entrysubjectid,')',entrysubjectname1) AS entrysubjectname \n" +
					"	from ( \n" +
					"		select a.entryvchdate vchdate,a.vchid as voucherid,entrySerail Serail,subjectid, \n" +
					"		concat(if( length(entrytypeid)=0 || entrytypeid is null,'',concat(entrytypeid,'-')),cast(entryoldvoucherid as char)) as typenumber,entrysummary as summary, \n" +
						opt + 
					"		entrysubjectfullname1 as subjectname1, \n" +
					"		case entrydirction when 1 then entryoccurvalue else 0 end as debitocc, \n" +
					"		case entrydirction when -1 then entryoccurvalue else 0 end  as creditocc, \n" +
					"		entryoccurvalue as occ, \n" +
					"		case entrydirction when 1 then concat('借:',entrysubjectfullname1) else concat('贷:',entrysubjectfullname1) end as subjectname2, \n" +
					"		b.Createor,judge, entryAccPackageID,entrysubjectid,entryoldvoucherid,entryVchDate,entrytypeid,entrySerail,entrybankid,c.subjectnames,entrysubjectname1 " +
					" 		from z_voucherspotcheck a, \n" +
					"		(select group_concat(distinct b.name) Createor	 from k_user b where id in (-1,${Createors})	) b, \n" +
					//加上了对方科目
					"		(SELECT vchDate,oldvoucherid,typeid,serail,GROUP_CONCAT(DISTINCT IF(subjectname!=b.subjectfullname&&CONCAT(IF(LOCATE('/',b.subjectfullname)=0,b.subjectfullname,SUBSTR(b.subjectfullname,1,LOCATE('/',b.subjectfullname)-1)),'/',subjectname)!=b.subjectfullname   ,CONCAT(IF(LOCATE('/',b.subjectfullname)=0,b.subjectfullname,SUBSTR(b.subjectfullname,1,LOCATE('/',b.subjectfullname)-1)),'/../',subjectname)  ,b.subjectfullname)) subjectnames " +
					"		FROM ( \n" +
					"			SELECT *,REPLACE(REPLACE(IF(Dirction*occurvalue<0,REPLACE(debitsubjects,',,',','),REPLACE(creditsubjects,',,',',')),',,',','),',,',',') subjects \n" +
					" 			FROM c_subjectentry )a \n" +
					" 			LEFT JOIN (SELECT * FROM c_accpkgsubject  WHERE  AccPackageID=${curPackageid} ) b \n" +
					" 			ON  a.subjects LIKE CONCAT('%,',b.subjectid,',%')  \n " +
					" 			WHERE  a.voucherid IN (${vchids}) \n " +
					" 			GROUP BY a.autoid \n " +   
					" 			ORDER BY vchdate DESC,typeid DESC,ABS(OldVoucherID) DESC \n " +
					"		) c \n" +
					
					" 		where a.ProjectID = '${curProjectid}'  and a.Judge not like '%-截止性抽凭%'  and a.vchid in (${vchids}) " +
					" 		AND a.entryvchdate = c.vchDate AND a.entryoldvoucherid = c.oldvoucherid AND a.entrytypeid = c.typeid \n" +
		 			"		AND a.entryoldvoucherid = c.oldvoucherid AND a.entryserail = c.serail \n" +
					str1 + 
					st +
					"	) a \n" +
					") a ";
			
		}
		return strSql;

	}

}
