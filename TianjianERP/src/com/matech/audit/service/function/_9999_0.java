package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.project.model.Project;
import com.matech.audit.service.rectify.RectifyService;
import com.matech.audit.service.usersubject.SubjectAssitemService;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;


/**
 * 
 * 9999公式：引用指标动态刷3007和3015公式
 * 参数：
 * 1、科目名称 : 要刷出来的科目。注：要用标准科目名称。必填（但可以读取底稿的对应科目）
 * 2、包含核算 : 表示是否要刷出核算。值：是/否
 * 3、核算名称 : 表示核算的类型，以“;”分隔。例：客户;供应商;关联;往来（默认）。
 * 4、支持外币 : 表示是否要刷出外币，值：支持/不支持（默认）。支持表示如果有外币就刷外币，没有外币就刷本位币
 * 5、关联客户 : 表示是否关联客户，值：所有客户（默认）/关联客户/其它客户。关联客户表示刷关联客户，其它客户表示刷非关联客户
 * 6、科目类型 : 表示刷科目级别，值：末级（默认）/一级/二级。
 * 7、刷前几名 : 表示只刷前几列
 * 8、排序　　 : 表示按什么来排序   
 * 
 * 返回值
 * 
 * 1、余额值:
 * 期初数、借方期初数、贷方期初数、借发生、贷发生、净发生、期末数、借方期末数、贷方期末数 （和审计区间的日期有关）
 * 年初数、年借方期初数、年贷方期初数、年借发生、年贷发生、年净发生、年末数、年借方期末数、年贷方期末数 （和帐上的发生有关）
 * 审定期初、审定期末、（和审计区间的日期有关，包含了调整）
 * 折合期初数、折合借方期初数、折合贷方期初数、折合借发生、折合贷发生、折合净发生、折合期末数、折合借方期末数、折合贷方期末数 （和科目的外币折合成本位币）
		
 * 2、账龄:
 * 1年以内、1年到2年、2年到3年、3年到4年、4年到5年、5年以上 （不含调整）
 * 审定1年以内、审定1年到2年、审定2年到3年、审定3年到4年、审定4年到5年、审定5年以上 （含调整）
		
 * 3、调整:（本项目的当年调整）
 * 期末调整借、期末调整贷
 * 期末重分类借、期末重分类贷
 * 期末不符未调借、期末不符未调贷
 * 期初调整借、期初调整贷
 * 期初重分类借、期初重分类贷
 * 期末不符未调借、期末不符未调贷
 * 账表不符借、账表不符贷
		
 * 4、历年调整（包含1－5年前的历年调整）
 * 1年前期末调整借、1年前期末调整贷
 * 1年前期末重分类借、1年前期末重分类贷
 * 1年前期末不符未调借、1年前期末不符未调贷
 * 1年前期初调整借、1年前期初调整贷
 * 1年前期初重分类借、1年前期初重分类贷
 * 1年前期末不符未调借、1年前期末不符未调贷
 * 1年前账表不符借、1年前账表不符贷
 * 
 * 2年前期末调整借、2年前期末调整贷
 * 2年前期末重分类借、2年前期末重分类贷
 * 2前期末不符未调借、2年前期末不符未调贷
 * 2年前期初调整借、2年前期初调整贷
 * 2年前期初重分类借、2年前期初重分类贷
 * 2年前期末不符未调借、2年前期末不符未调贷
 * 2年前账表不符借、2年前账表不符贷
	
 * 3年前期末调整借、3年前期末调整贷
 * 3年前期末重分类借、3年前期末重分类贷
 * 3前期末不符未调借、3年前期末不符未调贷
 * 3年前期初调整借、3年前期初调整贷
 * 3年前期初重分类借、3年前期初重分类贷
 * 3年前期末不符未调借、3年前期末不符未调贷
 * 3年前账表不符借、3年前账表不符贷
 * 
 * 4年前期末调整借、4年前期末调整贷
 * 4年前期末重分类借、4年前期末重分类贷
 * 4前期末不符未调借、4年前期末不符未调贷
 * 4年前期初调整借、4年前期初调整贷
 * 4年前期初重分类借、4年前期初重分类贷
 * 4年前期末不符未调借、4年前期末不符未调贷
 * 4年前账表不符借、4年前账表不符贷
 * 
 * 5前期末调整借、5年前期末调整贷
 * 5年前期末重分类借、5年前期末重分类贷
 * 5前期末不符未调借、5年前期末不符未调贷
 * 5年前期初调整借、5年前期初调整贷
 * 5年前期初重分类借、5年前期初重分类贷
 * 5年前期末不符未调借、5年前期末不符未调贷
 * 5年前账表不符借、5年前账表不符贷
 * 
 * 例：
 * =取列公式覆盖(9999,"1111","科目名称","&支持外币=支持&核算名称=客户;供应商;关联;往来")
 * =取列公式覆盖(9999,"1111","币种","&支持外币=支持&核算名称=客户;供应商;关联;往来")
 * =取列公式覆盖(9999,"1111","期初数","&支持外币=支持&核算名称=客户;供应商;关联;往来")
 * =取列公式覆盖(9999,"1111","科目名称","&支持外币=支持&核算名称=客户;供应商;关联;往来&刷前几名=10")
 * =取列公式覆盖(9999,"1111","科目名称","&支持外币=支持&核算名称=客户;供应商;关联;往来&刷前几名=10&排序=审定期末")
 * =取列公式覆盖(9999,"","凭证日期","&关联客户=其它客户&包含核算=是")
 */
public class _9999_0 extends AbstractAreaFunction {
	
	public ResultSet process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {
		ASFuntion CHF=new ASFuntion();
		Statement st = null;
		ResultSet rs = null;
		String sql = "";
		try {
			String sql1 = "",sql2 = "",sql3 = "";
			
			st = conn.createStatement();
			
			this.tempTable = "tt_"+DELUnid.getCharUnid();
			
			String areaid = request.getParameter("areaid");
			
			//有［多科目］,以“|”分隔 : 现金,银行存款,其它货币资金
			String SubjectNames = CHF.showNull((String)args.get("多科目")).trim();	
			
			//有［比对科目］
			String bdSubjectName = CHF.showNull((String)args.get("比对科目")).trim();		//比对科目
			
			String acc = CHF.showNull((String) args.get("curAccPackageID"));
	        String projectid = CHF.showNull((String) args.get("curProjectid"));
	        
	        //看看有没有z_rectifysubject 、z_rectifyassitem 表
	        RectifyService rectifyService = new RectifyService(conn);
	        try {
	        	//看看z_rectifysubject 、z_rectifyassitem 表有没有这个项目的汇总
	        	String str = "select 1 from z_rectifysubject where projectid = '"+projectid+"' limit 1 ";
	        	rs = st.executeQuery(str);
	        	if(!rs.next()){
	        		DbUtil.close(rs);
	        		str = "select 1 from z_subjectentryrectify where projectid = '"+projectid+"' limit 1 ";
	        		rs = st.executeQuery(str);
	        		if(rs.next()){
	        			//看看这个项目有没有调整
	        			rectifyService.createSubject(acc, projectid);
	        			rectifyService.createAssitemNew(acc, projectid);
	        		}
	        	}
			} catch (Exception e) {
				//没有z_rectifysubject 、z_rectifyassitem 表
        		rectifyService.createSubject(acc, projectid);
        		rectifyService.createAssitemNew(acc, projectid);	
			}finally {
				DbUtil.close(rs);
			}
	       //看看有没有z_rectifysubject 、z_rectifyassitem 表
	        
	        
			String SubjectName = CHF.showNull((String)args.get("科目名称"));		//科目名称
			if (SubjectName==null || SubjectName.equals("")){
                String manuid=(String)args.get("manuid");
                if (manuid==null || manuid.equals("")){
                    SubjectName=getTaskSubjectNameByTaskCode(conn,projectid,(String)request.getParameter("curTaskCode"));
                }else{
                    //如果科目名称为空，则通过前台提交的刷新底稿编号去取得对应任务得科目名称；
                    SubjectName = getTaskSubjectNameByManuID(conn, manuid);
                }
                
            }

            String sName1 = changeSubjectName(conn,projectid,SubjectName);
            if(!"".equals(sName1)){
            	SubjectName = sName1; 
            }            
            args.put("SubjectName",SubjectName);
            args.put("科目名称",SubjectName);

            
            String allfield = request.getParameter("allfield");
            String bdallfield = request.getParameter("allfield");
            args.put("allfield",allfield);
            
			System.out.println(bdSubjectName + " allfield = " + allfield);
			
			
			if(!"".equals(SubjectNames)){ //有多科目 
				
				String subSql = "";
				String [] Subject = SubjectNames.split("\\|");
				for(int iSub = 0; iSub < Subject.length; iSub++ ){
					String table = tempTable + iSub;
					
					args.put("科目名称",Subject[iSub]);
					process1( session,  request, response,  conn,  args, table);
					
					subSql += "union select * from " + table + " " ;
				}
				if(!"".equals(subSql)){
					subSql = subSql.substring(5);
					
					sql = "create table " + tempTable + " " + subSql;
					st.execute(sql);
					
					for(int iSub = 0; iSub < Subject.length; iSub++ ){
						String table = tempTable + iSub;
						new com.matech.audit.work.subjectentry.SubjectEntry(conn).DelTempTable(table);
					}
					
				}
				
				/**
				 * 显示的字段
				 */
				sql = "select ifnull(evalue,fieldvalue) evalue,fieldvalue from k_areafunctionfields  where areaid='"+areaid+"' and ? like concat('%',fieldvalue ,'%') \n";
				String [] ballfield = allfield.split("`");
				PreparedStatement ps = null;
				for(int i=0;i<ballfield.length;i++) {
					String value1 = CHF.replaceStr(ballfield[i],"＋","`+`");
					value1 = CHF.replaceStr(value1,"－","`-`");
					value1 = CHF.replaceStr(value1,"＊","`*`");
					value1 = CHF.replaceStr(value1,"／","`/`");
					value1 = CHF.replaceStr(value1,"（","`(`");
					value1 = CHF.replaceStr(value1,"）","`)`");
					value1 = "`"+value1+"`";
					
					String value2 = CHF.replaceStr(ballfield[i],"＋","`");
					value2 = CHF.replaceStr(value2,"+","`");
					value2 = CHF.replaceStr(value2,"－","`");
					value2 = CHF.replaceStr(value2,"-","`");
					value2 = CHF.replaceStr(value2,"＊","`");
					value2 = CHF.replaceStr(value2,"*","`");
					value2 = CHF.replaceStr(value2,"／","`");
					value2 = CHF.replaceStr(value2,"/","`");
					value2 = CHF.replaceStr(value2,"（","`");
					value2 = CHF.replaceStr(value2,"(","`");
					value2 = CHF.replaceStr(value2,"）","`");
					value2 = CHF.replaceStr(value2,")","`");
					value2 = "`" + value2 + "`";

					String [] value3 = value2.split("`"); 
					java.util.ArrayList al = new ArrayList();
					for(int iValue = 0 ; iValue < value3.length; iValue++){
						if(value3[iValue] != null && !"".equals(value3[iValue].trim())){
							al.add(value3[iValue]);
						}
					}
					
					ps = conn.prepareStatement(sql);
					ps.setString(1,ballfield[i]);
					rs = ps.executeQuery();
					int iRsCount = 0;
					while(rs.next()) {//有对应列取对应列 
						String evalue = rs.getString("evalue");
						String fieldvalue = rs.getString("fieldvalue");
						if(("`" + value2+ "`").indexOf("`" + fieldvalue + "`") >-1){
							iRsCount ++;
							value1 = CHF.replaceStr(value1,"`"+fieldvalue+"`",evalue);
						}
						
					}
					value1 = CHF.replaceStr(value1,"`","");
					
					if(iRsCount<=1){
						if(iRsCount == 0){
							throw new Exception("公式访问参数设置有误，没有［"+ballfield[i]+"］列！");
						}else if(iRsCount != al.size()){
							throw new Exception("公式访问参数设置有误，没有［"+ballfield[i]+"］列！");
						}
						if(!"subjectname".toLowerCase().equals(value1.toLowerCase()) 
								&& !"fullname".toLowerCase().equals(value1.toLowerCase())
								&& !"parentsubjectname".toLowerCase().equals(value1.toLowerCase())
							){
							sql1 += value1 +",";		
						}
					}else{
						if(iRsCount != al.size()){
							throw new Exception("公式访问参数设置有误，没有［"+ballfield[i]+"］列！");
						}
						if(!"subjectname".toLowerCase().equals(value1.toLowerCase()) 
								&& !"fullname".toLowerCase().equals(value1.toLowerCase())
								&& !"parentsubjectname".toLowerCase().equals(value1.toLowerCase())
							){
							sql1 +=  value1 +" as d"+i+",";
						}
					}
					
					DbUtil.close(rs);
					DbUtil.close(ps);
				}
			} else{
				//主营业务成本 allfield = 科目名称`结转数`比对结转数
				if(!"".equals(bdSubjectName)){//有［比对科目］
					String tmpTable1 = tempTable + "1", tmpTable2 = tempTable + "2"; //两个临时表
					allfield = CHF.replaceStr(allfield, "比对", ""); 
					args.put("allfield",allfield);
					System.out.println(bdallfield + "|" + allfield);
					
					process1( session,  request, response,  conn,  args, tmpTable1);	//完成有［科目名称］的SQL
					
					args.put("科目名称",bdSubjectName);
					process1( session,  request, response,  conn,  args, tmpTable2);	//完成有［比对科目］的SQL
					
					/**
					 * 显示的字段
					 */
					sql = "select ifnull(evalue,fieldvalue) evalue,fieldvalue from k_areafunctionfields  where areaid='"+areaid+"' and ? like concat('%',fieldvalue ,'%') \n";
					String [] ballfield = allfield.split("`");		//去掉“比对”
					String [] bdballfield = bdallfield.split("`"); 	//原来的
					
					PreparedStatement ps = null;
					
					for(int i=0;i<ballfield.length;i++) {
						String value1 = CHF.replaceStr(ballfield[i],"＋","`+`");
						value1 = CHF.replaceStr(value1,"－","`-`");
						value1 = CHF.replaceStr(value1,"＊","`*`");
						value1 = CHF.replaceStr(value1,"／","`/`");
						value1 = CHF.replaceStr(value1,"（","`(`");
						value1 = CHF.replaceStr(value1,"）","`)`");
						value1 = "`"+value1+"`";
						
						String value2 = CHF.replaceStr(ballfield[i],"＋","+");
						value2 = CHF.replaceStr(value2,"+","`");
						value2 = CHF.replaceStr(value2,"－","`");
						value2 = CHF.replaceStr(value2,"-","`");
						value2 = CHF.replaceStr(value2,"＊","`");
						value2 = CHF.replaceStr(value2,"*","`");
						value2 = CHF.replaceStr(value2,"／","`");
						value2 = CHF.replaceStr(value2,"/","`");
						value2 = CHF.replaceStr(value2,"（","`");
						value2 = CHF.replaceStr(value2,"(","`"); 
						value2 = CHF.replaceStr(value2,"）","`");
						value2 = CHF.replaceStr(value2,")","`");
						value2 = "`" + value2 + "`";
						
						String [] value3 = value2.split("`"); 
						java.util.ArrayList al = new ArrayList();
						for(int iValue = 0 ; iValue < value3.length; iValue++){
							if(value3[iValue] != null && !"".equals(value3[iValue].trim())){
								al.add(value3[iValue]);
							}
						}
						
						ps = conn.prepareStatement(sql);
						ps.setString(1,ballfield[i]);
						rs = ps.executeQuery();
						int iRsCount = 0;
						
						System.out.println(value2);
						
						while(rs.next()) {//有对应列取对应列 
							String evalue = rs.getString("evalue");
							String fieldvalue = rs.getString("fieldvalue");
							if(("`" + value2+ "`").indexOf("`" + fieldvalue + "`") >-1){
								iRsCount ++;
								value1 = CHF.replaceStr(value1,"`" + fieldvalue + "`",evalue);
							}
							
						}
						value1 = CHF.replaceStr(value1,"`","");
						
						if(iRsCount<=1){
							
							if(iRsCount == 0){
								throw new Exception("公式访问参数设置有误，没有［"+bdballfield[i]+"］列！");
							}else if(iRsCount != al.size()){
								throw new Exception("公式访问参数设置有误，没有［"+bdballfield[i]+"］列！");
							}
							
							if(!"subjectname".toLowerCase().equals(value1.toLowerCase()) 
								&& !"fullname".toLowerCase().equals(value1.toLowerCase())
								&& !"parentsubjectname".toLowerCase().equals(value1.toLowerCase())
							){
								if(bdballfield[i].indexOf("比对") > -1){
									sql1 += "bd" + value1 + ",";
									sql2 += "sum(" + value1 + ") as bd" + value1 + ",";
									sql3 += "ifnull(b.bd" + value1 + ",0) as bd" + value1 + ",";
									
								}else{
									sql1 += value1 + ",";
									sql2 += "sum(" + value1 + ") as " + value1 + ",";
									sql3 += "ifnull(a." + value1 + ",0) as " + value1 + ",";
								}
							}
						}else{
							
							if(iRsCount != al.size()){
								throw new Exception("公式访问参数设置有误，没有［"+bdballfield[i]+"］列！");
							}
							
							if(!"subjectname".toLowerCase().equals(value1.toLowerCase()) 
									&& !"fullname".toLowerCase().equals(value1.toLowerCase())
									&& !"parentsubjectname".toLowerCase().equals(value1.toLowerCase())
								){
									if(bdballfield[i].indexOf("比对") > -1){
										
										sql1 += "d" + i + ",";
										sql2 += "sum(" + value1 + ") as d" + i + ",";
										sql3 += "ifnull(b.d" + i + ",0) as d" + i + ",";
										
									}else{
										sql1 += "d" + i + ",";
										sql2 += "sum(" + value1 + ") as d" + i + ",";
										sql3 += "ifnull(a.d" + i + ",0) as d" + i + ",";
									}
								}
							
							
						}
						
						DbUtil.close(rs);
						DbUtil.close(ps);
					}
					
					sql = "create table " + tempTable + " " +
					"\n	select ifnull(a.isSubject,b.isSubject) as isSubject," +
					"\n	ifnull(a.sid,b.sid) as sid," +
					"\n	ifnull(a.fullname,b.fullname) as fullname," +
					"\n	ifnull(a.subjectname,b.subjectname) as subjectname," +
					"\n	ifnull(a.dataname,b.dataname) as dataname," +
					"\n	ifnull(a.unitname,b.unitname) as unitname," +
					sql3 + 
					"\n	a.direction2 as direction2," +
					"\n	b.direction2 as bddirection2 " +
					"\n	from (" +
					"\n		select isSubject,sid," +
					"\n		replace (replace (substring(subjectname,INSTR(subjectname,'/') + 1) ,'收入',''),'成本','') as fullname," +
					"\n		replace (replace (substring(subjectname,INSTR(subjectname,'/') + 1) ,'收入',''),'成本','') as subjectname ," +
					"\n		dataname ,unitname ,direction2 ," + sql2 + " 0 " +
					"\n		from " + tmpTable1 + " " +
					"\n		group by replace (replace (substring(subjectname,INSTR(subjectname,'/') + 1) ,'收入',''),'成本','') " +
					"\n	) a left join (" +
					"\n		select isSubject,sid," +
					"\n		replace (replace (substring(subjectname,INSTR(subjectname,'/') + 1) ,'收入',''),'成本','') as fullname," +
					"\n		replace (replace (substring(subjectname,INSTR(subjectname,'/') + 1) ,'收入',''),'成本','') as subjectname ," +
					"\n		dataname ,unitname ,direction2 ," + sql2 + " 0 " +
					"\n		from " + tmpTable2 + " " +
					"\n		group by replace (replace (substring(subjectname,INSTR(subjectname,'/') + 1) ,'收入',''),'成本','') " +
					"\n	) b on a.fullname = b.fullname" +
					"\n	union " +
					"\n	select ifnull(a.isSubject,b.isSubject) as isSubject," +
					"\n	ifnull(a.sid,b.sid) as sid," +
					"\n	ifnull(a.fullname,b.fullname) as fullname," +
					"\n	ifnull(a.subjectname,b.subjectname) as subjectname," +
					"\n	ifnull(a.dataname,b.dataname) as dataname," +
					"\n	ifnull(a.unitname,b.unitname) as unitname," +
					sql3 + 
					"\n	a.direction2 as direction2," +
					"\n	b.direction2 as bddirection2 " +
					"\n	from (" +
					"\n		select isSubject,sid," +
					"\n		replace (replace (substring(subjectname,INSTR(subjectname,'/') + 1) ,'收入',''),'成本','') as fullname," +
					"\n		replace (replace (substring(subjectname,INSTR(subjectname,'/') + 1) ,'收入',''),'成本','') as subjectname ," +
					"\n		dataname ,unitname ,direction2 ," + sql2 + " 0 " +
					"\n		from " + tmpTable1 + " " +
					"\n		group by replace(replace (substring(subjectname,INSTR(subjectname,'/') + 1) ,'收入',''),'成本','') " +
					"\n	) a right join (" +
					"\n		select isSubject,sid," +
					"\n		replace (replace (substring(subjectname,INSTR(subjectname,'/') + 1) ,'收入',''),'成本','') as fullname," +
					"\n		replace (replace (substring(subjectname,INSTR(subjectname,'/') + 1) ,'收入',''),'成本','') as subjectname ," +
					"\n		dataname ,unitname ,direction2 ," + sql2 + " 0 " +
					"\n		from " + tmpTable2 + " " +
					"\n		group by replace(replace (substring(subjectname,INSTR(subjectname,'/') + 1) ,'收入',''),'成本','') " +
					"\n	) b on a.fullname = b.fullname" +
					"	";
					
//					System.out.println(sql);
					ps = conn.prepareStatement(sql);
					ps.execute();
					
					new com.matech.audit.work.subjectentry.SubjectEntry(conn).DelTempTable(tmpTable1);
					new com.matech.audit.work.subjectentry.SubjectEntry(conn).DelTempTable(tmpTable2);
					
				}else{//无［比对科目］
					
					process1( session,  request, response,  conn,  args, tempTable);
					
					/**
					 * 显示的字段
					 */
					sql = "select ifnull(evalue,fieldvalue) evalue,fieldvalue from k_areafunctionfields  where areaid='"+areaid+"' and ? like concat('%',fieldvalue ,'%') \n";
					System.out.println(allfield);
					String [] ballfield = allfield.split("`");
					PreparedStatement ps = null;
					for(int i=0;i<ballfield.length;i++) {
						String value1 = CHF.replaceStr(ballfield[i],"＋","`+`");
						value1 = CHF.replaceStr(value1,"－","`-`");
						value1 = CHF.replaceStr(value1,"＊","`*`");
						value1 = CHF.replaceStr(value1,"／","`/`");
						value1 = CHF.replaceStr(value1,"（","`(`");
						value1 = CHF.replaceStr(value1,"）","`)`");
						value1 = "`"+value1+"`";
						
						String value2 = CHF.replaceStr(ballfield[i],"＋","`");
						value2 = CHF.replaceStr(value2,"+","`");
						value2 = CHF.replaceStr(value2,"－","`");
						value2 = CHF.replaceStr(value2,"-","`");
						value2 = CHF.replaceStr(value2,"＊","`");
						value2 = CHF.replaceStr(value2,"*","`");
						value2 = CHF.replaceStr(value2,"／","`");
						value2 = CHF.replaceStr(value2,"/","`");
						value2 = CHF.replaceStr(value2,"（","`");
						value2 = CHF.replaceStr(value2,"(","`");
						value2 = CHF.replaceStr(value2,"）","`");
						value2 = CHF.replaceStr(value2,")","`");
						value2 = CHF.replaceStr(value2," ","");
						value2 = "`" + value2 + "`";

						String [] value3 = value2.split("`"); 
						java.util.ArrayList al = new ArrayList();
						for(int iValue = 0 ; iValue < value3.length; iValue++){
							if(value3[iValue] != null && !"".equals(value3[iValue].trim())){
								al.add(value3[iValue].trim());
							}
						}
						
						System.out.println(ballfield[i]+"|"+value1+"|"+value2);
						ps = conn.prepareStatement(sql);
						ps.setString(1,ballfield[i]);
						rs = ps.executeQuery();
						int iRsCount = 0;
						while(rs.next()) {//有对应列取对应列 
							String evalue = rs.getString("evalue");
							String fieldvalue = rs.getString("fieldvalue");
							
							if(("`" + value2.trim()+ "`").indexOf("`" + fieldvalue + "`") >-1){
								iRsCount ++;
								value1 = CHF.replaceStr(value1.trim(),"`"+fieldvalue+"`",evalue);
							}
							
						}
						value1 = CHF.replaceStr(value1,"`","");
						
						if(iRsCount<=1){
							if(iRsCount == 0){
								throw new Exception("公式访问参数设置有误，没有［"+ballfield[i]+"］列！");
							}else if(iRsCount != al.size()){
								//throw new Exception("公式访问参数设置有误，没有［"+ballfield[i]+"］列！");
							}
							if(!"subjectname".toLowerCase().equals(value1.toLowerCase()) 
									&& !"fullname".toLowerCase().equals(value1.toLowerCase())
									&& !"parentsubjectname".toLowerCase().equals(value1.toLowerCase())
								){
								sql1 += value1+",";		
							}
						}else{
							if(iRsCount != al.size()){
								//throw new Exception("公式访问参数设置有误，没有［"+ballfield[i]+"］列！");
							}
							if(!"subjectname".toLowerCase().equals(value1.toLowerCase()) 
									&& !"fullname".toLowerCase().equals(value1.toLowerCase())
									&& !"parentsubjectname".toLowerCase().equals(value1.toLowerCase())
								){
								sql1 += value1+" as d"+i+",";
							}
						}
						
						DbUtil.close(rs);
						DbUtil.close(ps);
					}
					
				}
			}
			
			/**
			 * 输出rs
			 */
			String orderby = CHF.showNull((String)args.get("排序"));
			String limit = CHF.showNull((String)args.get("刷前几名"));
			
			String subejctType = CHF.showNull((String)args.get("科目类型"));
			
			sql="select fieldvalue,evalue,groupid,INSTR('"+allfield+"',fieldvalue)>0 as d1 ,orderid from k_areafunctionfields \n"	+
			"where areaid="+areaid+" and typeid=0 \n"	+
//			"and ifnull(groupid,'') <> ''  \n"	+
			"order by groupid,orderid";
			
			rs=st.executeQuery(sql);
			while (rs.next()){
				if (!"".equals(orderby) && rs.getString("fieldvalue").equals(orderby)){
					orderby=" "+rs.getString("evalue")+" desc ";
				}
			}
			
			if("全部".equals(subejctType)){
				if("".equals(orderby)){
					orderby = " subjectname ";
				}
			}else{
				if("".equals(orderby)){
					orderby = " isSubject,sid ";
				}	
			}
			
			if(!"".equals(limit)){
				limit = " limit 0," + limit;
			}
			
			
			sql = "update "+tempTable+" set unitname=dataname where unitname is null ";
			st.execute(sql);
			
			sql = "select fullname,subjectname ,dataname ,unitname ,direction2 ," +
				"SUBSTRING(subjectname,1," +
				"	IF(INSTR(CONCAT(subjectname,'`|`'),CONCAT('/',fullname,'`|`')) = 0," +
				"	LENGTH(subjectname)," +
				"	INSTR(CONCAT(subjectname,'`|`'),CONCAT('/',fullname,'`|`'))-1)" +
				") AS parentSubjectName," + 
				sql1 + " 0 from "+ tempTable + " where 1=1 order by " + orderby + limit;
			System.out.println(sql);
			rs = st.executeQuery(sql);
			
			this.tempTable = "";
			
			return rs;
			
		} catch (Exception e) {
			System.out.println("出错的SQL："+ sql);
			e.printStackTrace();
			DbUtil.close(rs);
			DbUtil.close(st);
			throw e;
		}
	}
	
	
	public ResultSet process1(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args,String tmpTable)
			throws Exception {
		ASFuntion CHF=new ASFuntion();
		Statement st = null;
		ResultSet rs = null;
		String sql = "";
		try {
		    
			st = conn.createStatement();
			
			String acc = CHF.showNull((String) args.get("curAccPackageID"));
	        String projectid = CHF.showNull((String) args.get("curProjectid"));
//	        String customerid=acc.substring(0,6);
	       
			String SubjectName = CHF.showNull((String)args.get("科目名称"));		//科目名称
			if (SubjectName==null || SubjectName.equals("")){
                String manuid=(String)args.get("manuid");
                if (manuid==null || manuid.equals("")){
                    SubjectName=getTaskSubjectNameByTaskCode(conn,projectid,(String)request.getParameter("curTaskCode"));
                }else{
                    //如果科目名称为空，则通过前台提交的刷新底稿编号去取得对应任务得科目名称；
                    SubjectName = getTaskSubjectNameByManuID(conn, manuid);
                }
                
//                args.put("subjectname",subjectname);
                
            }

            String sName1 = changeSubjectName(conn,projectid,SubjectName);
            if(!"".equals(sName1)){
            	SubjectName = sName1; 
            }            
            args.put("SubjectName",SubjectName);
            args.put("科目名称",SubjectName);
   
//            isAssItem  	AssItem
//            ""			""			先披露，再"客户;供应商;关联;往来;费用";
//            ""			!""			AssItem
//            否				""			科目
//            否				!""			AssItem
//            是				""			先披露，再"客户;供应商;关联;往来;费用";
//            是				!""			AssItem
            
            String isAssItem = CHF.showNull((String)args.get("包含核算"));
			String AssItem = CHF.showNull((String)args.get("核算名称"));
			
			
			if("".equals(isAssItem) && "".equals(AssItem)){
				AssItem="客户;供应商;往来;费用";
				
				/**
				 * 根据科目辅助核算披露，得到默认核算名称。没有值默认为［客户;供应商;往来;费用］
				 * 小卢说去掉“关联”
				 */
				String newAssItem = new SubjectAssitemService(conn).getFunction(acc, SubjectName);
				if(!"".equals(newAssItem)){
					AssItem = newAssItem;
				}
				args.put("核算名称", AssItem);
			}else if("".equals(isAssItem) && !"".equals(AssItem)){
				//不用
			}else if("否".equals(isAssItem) && "".equals(AssItem)){
				//不用
			}else if("否".equals(isAssItem) && !"".equals(AssItem)){
				//不用
			}else if("是".equals(isAssItem) && "".equals(AssItem)){
				AssItem="客户;供应商;往来;费用";
				
				/**
				 * 根据科目辅助核算披露，得到默认核算名称。没有值默认为［客户;供应商;关联;往来;费用］
				 */
				String newAssItem = new SubjectAssitemService(conn).getFunction(acc, SubjectName);
				if(!"".equals(newAssItem)){
					AssItem = newAssItem;
				}
				args.put("核算名称", AssItem);
			}else if("是".equals(isAssItem) && !"".equals(AssItem)){
				//不用
			}
			
			String subejctType = CHF.showNull((String)args.get("科目类型"));// 末级(一级、二级)
			if("一级".equals(subejctType) || "二级".equals(subejctType)){
				args.put("包含核算", "否");
				args.put("核算名称", "");
			}
			
//			String allfield=request.getParameter("allfield");
			String allfield=CHF.showNull((String)args.get("allfield"));
			
			System.out.println("9999 allfield = " + allfield);
			
			Project project = new ProjectService(conn).getProjectById(projectid);
			args.put("project", project);
			
//			String strStartYear=(String)args.get("起始年");
//			String strStartMonth=(String)args.get("起始月");
//			String strEndYear=(String)args.get("结束年");
//			String strEndMonth=(String)args.get("结束月");
			
			String allYear = CHF.showNull((String)args.get("比较年份"));	//比较年份=-1
			
			String Year= CHF.showNull((String)args.get("年度"));
			String Month= CHF.showNull((String)args.get("月份"));
			if("".equals(Month)){
				Month = "0";
			}
			
			String begin = "".equals(project.getAuditTimeBegin()) ? "0" : project.getAuditTimeBegin().substring(0,4);
			String end = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(0,4);
			String bMonth = "".equals(project.getAuditTimeBegin()) ? "0" : project.getAuditTimeBegin().substring(5,7);
			String eMonth = "".equals(project.getAuditTimeEnd()) ? "0" : project.getAuditTimeEnd().substring(5,7);
			
			String strStartYearMonth="",strEndYearMonth="";
			if("".equals(allYear)||allYear==null){
				allYear="0";
			}
			strStartYearMonth = String.valueOf((Integer.parseInt(begin)+Integer.parseInt(allYear))*12+Integer.parseInt(bMonth));
			strEndYearMonth = String.valueOf(Integer.parseInt(end)*12+Integer.parseInt(eMonth));
			
			
			String[] result = getClientIDAndDirectionByStandName(conn,acc, project.getProjectId(),SubjectName);
            String subjectid = result[0];
			
            RuleService ruleService = new RuleService(conn);
			
            
            String isCurname = "";
			if (allfield.indexOf("原币")>-1){
				isCurname="支持";
				args.put("支持外币", "支持");
			}
			String isUnitName = "";
			if (allfield.indexOf("数量")>-1){
				isUnitName="支持";
				args.put("支持数量", "支持");
			}
            
            
			String strSelectSql = ruleService.getRuleSQL(SubjectName, strStartYearMonth, strEndYearMonth, subjectid, end, args);
			System.out.println(strSelectSql);
			
			//System.out.println("0000:" + CHF.getCurrentTime());
//			tempTable = "tt_"+DELUnid.getCharUnid();
			sql = "create table " + tmpTable + " " + strSelectSql;
			st.execute(sql);
			
			sql="alter table "+tmpTable+" add column unitname varchar (20)  , add column unitsign varchar (20)  ";
			st.execute(sql);
			
			if ("支持".equals(isCurname) && "支持".equals(isUnitName)){
				
				args.put("外币数量", "都要");
				
				strSelectSql = CHF.replaceStr(strSelectSql, "and a.accsign=1", "and a.accsign=2");
				
				sql = "update "+tmpTable+" a ,(" +
					strSelectSql +
					" ) b set a.unitname = if(b.dataname='本位币','',b.dataname)  where a.tokenid=b.tokenid and a.issubject=b.issubject and a.sName = b.sName";
				
				st.execute(sql); 
				
				sql = "update "+tmpTable+" a, " +
					" ( " +
					" 	select tokenid,sName,max(dataname)as dataname from "+tmpTable+" " +
					" 	group by tokenid,sName " +
					" 	having count(*)>1 " +
					" )b " +
					" set unitsign=0 " +
					" where a.tokenid=b.tokenid  and a.sName = b.sName and a.dataname <> b.dataname " ;
				
				
				st.execute(sql);
				
			}
			sql = "update "+tmpTable+" set unitsign=1 where unitsign is null ";
			st.execute(sql);
			
			
			//临时表追加字段，并完成fields等的翻译；
//			String sql1 = "";
			sql="";
			
			String orderby = CHF.showNull((String)args.get("排序"));
			
			String areaid = request.getParameter("areaid");
			
			sql="select fieldvalue,evalue,groupid,INSTR('"+allfield+"',fieldvalue)>0 as d1 ,orderid from k_areafunctionfields \n"	+
			"where areaid="+areaid+" and typeid=0 \n"	+
			"and ifnull(groupid,'') <> ''  \n"	+
			"order by groupid,orderid";
			
			rs=st.executeQuery(sql);
			sql="";
			
			String value = CHF.replaceStr(allfield,"＋","`");
			value = CHF.replaceStr(value,"+","`");
			value = CHF.replaceStr(value,"－","`");
			value = CHF.replaceStr(value,"-","`");
			value = CHF.replaceStr(value,"＊","`");
			value = CHF.replaceStr(value,"*","`");
			value = CHF.replaceStr(value,"／","`");
			value = CHF.replaceStr(value,"/","`");
			value = CHF.replaceStr(value,"（","`");
			value = CHF.replaceStr(value,"(","`");
			value = CHF.replaceStr(value,"）","`");
			value = CHF.replaceStr(value,")","`");
			value = "`" + value + "`";
			
			String[] fields=value.split("`");
			
			GuideLineProperty[] gps=null,gps1=null;
			if (fields.length>0){
				gps1=new GuideLineProperty[fields.length];
			}
			int iGps=0;
			
			while (rs.next()){
				sql+= ", add column "+rs.getString("evalue")+" decimal (15,2) DEFAULT '0.00' ";
				//追加fields
				
				String fieldvalue = rs.getString("fieldvalue");
				
				if (rs.getInt("d1")>0 && value.indexOf("`" + fieldvalue + "`") >-1){
					gps1[iGps++]=new GuideLineProperty(rs.getString("evalue"),rs.getString("groupid"));
				}
				
				if (!"".equals(orderby) && rs.getString("fieldvalue").equals(orderby)){
					orderby=" "+rs.getString("evalue")+" desc ";
				}
				
			}
			DbUtil.close(rs);
			
			if (!sql.equals("")){
				//增加字段列
				st.execute("alter table " + tmpTable + sql.substring(1) );
				
				try{
					st.execute("alter table " + tmpTable + " change sid sid varchar (3000)  NULL ");	//关联单位
				
					st.execute("alter table " + tmpTable + " change connect connect varchar (10) DEFAULT ''");	//关联单位
					
					st.execute("alter table " + tmpTable + " change funccase funccase varchar (10) DEFAULT ''");	//发函单位
					
					st.execute("alter table " + tmpTable + " change standname standname varchar (50) DEFAULT ''");	//标准科目
					st.execute("alter table " + tmpTable + " change subjectIDorName subjectIDorName varchar (300) DEFAULT ''");	//科目编号与名称
					st.execute("alter table " + tmpTable + " change assitemIDorName assitemIDorName varchar (300) DEFAULT ''");	//核算编号与名称
					st.execute("alter table " + tmpTable + " change vchDate vchDate varchar (300) DEFAULT ''");	//最后凭证日期
					
					st.execute("alter table " + tmpTable + " change DebitVchDate DebitVchDate varchar (300) DEFAULT ''");	//最后借方凭证日期
					st.execute("alter table " + tmpTable + " change CreditVchDate CreditVchDate varchar (300) DEFAULT ''");	//最后贷方凭证日期
					st.execute("alter table " + tmpTable + " change DebitSummary DebitSummary varchar (5000) DEFAULT ''");	//最后借方凭证摘要
					st.execute("alter table " + tmpTable + " change CreditSummary CreditSummary varchar (5000) DEFAULT ''");	//最后贷方凭证摘要
					
					st.execute("alter table " + tmpTable + " add index sName (sName),add index dataname (dataname),add index sid (sid)");
				}catch(Exception e){
					System.out.println("sid太短了");
				}
				try {
					st.execute("alter table " + tmpTable + " change tokenid tokenid varchar (2000)  NULL  COLLATE gbk_chinese_ci");
					st.execute("alter table " + tmpTable + " add index tokenid (tokenid) ");
				} catch (Exception e) {
					System.out.println("706:tokenid sql 出错!!!!");
				}
 
				
				
			}
			
			/**
			 * 单独对一级进行分支
			 */
			
			String lowerTable = tmpTable + "1";
			String lowerLevel = "";
			if("一级".equals(subejctType)){
				int level = 1, level1 = 0,level2 = 0;
				
				/**
				 * 求科目的最小的层次
				 */
				sql = "select level1 " +
				" from c_account a" +
				" where 1=1" +
				" and AccPackageID = "+acc+" and submonth = 1  \n" +
				" and (a.subjectfullname2 like '"+SubjectName+"/%'  or a.subjectfullname2 = '"+SubjectName+"' )  \n" + 
				" order by level1 limit 1 ";
				rs = st.executeQuery(sql);
				if(rs.next()){
					level = rs.getInt("level1");
				}
				DbUtil.close(rs);
				
				/**
				 * 求出科目下级被分出去的科目
				 */
				sql = "select a.* " +
				" from c_account a ,(" +
				"	select * " +
				"	from c_account " +
				"	where AccPackageID = "+acc+" " +
				"	and submonth = 1 " +
				"	and (subjectfullname2 = '"+SubjectName+"' or subjectfullname2 like '"+SubjectName+"/%')" +
				" ) b " +
				" where a.AccPackageID = "+acc+" " +
				" and a.submonth = 1 " +
				" and a.level1 =" + String.valueOf(level + 1) + 
				" and (a.subjectfullname1 = b.subjectfullname1 or a.subjectfullname1 like concat(b.subjectfullname1,'/%')) " +
				" and not (a.subjectfullname2 = '"+SubjectName+"' or a.subjectfullname2 like '"+SubjectName+"/%') " +
				" order by a.level1 limit 1";
				
				rs = st.executeQuery(sql);
				if(rs.next()){
					level1 = rs.getInt("level1");
				}
				DbUtil.close(rs);
				
				/**
				 * 求出科目是否在同一个科目树下
				 */
				sql = "select b.* from (" +
				"	select * 	" +
				"	from c_account 	" +
				"	where AccPackageID = "+acc+" 	" +
				"	and submonth = 1 	" +
				"	and (subjectfullname2 = '"+SubjectName+"' or subjectfullname2 like '"+SubjectName+"/%')" +
				" 	order by level1 limit 1" +
				" ) a ,(	" +
				"	select * 	" +
				"	from c_account 	" +
				"	where AccPackageID = "+acc+" 	" +
				"	and submonth = 1 " + 
				"	and (subjectfullname2 = '"+SubjectName+"' or subjectfullname2 like '"+SubjectName+"/%')" +
				" ) b " +
				" where 1=1 " +
				" and b.level1 = " + String.valueOf(level + 1) + 
				" and not (b.subjectfullname1 = a.subjectfullname1 or b.subjectfullname1 like concat(a.subjectfullname1,'/%')) " +
				" order by b.level1 limit 1" ;

				rs = st.executeQuery(sql);
				if(rs.next()){
					level2 = rs.getInt("level1");
				}
				DbUtil.close(rs);
				
				if(level1 == 0 && level2 == 0){
					lowerLevel = "";
				}else{
					if(level1 >= level2){
						lowerLevel = String.valueOf(level1);
					}else{
						lowerLevel = String.valueOf(level2);
					}
				}
				
				
			}
			
			/**
			 * 显示的字段
			 */
//			sql = "select ifnull(evalue,fieldvalue) evalue,fieldvalue from k_areafunctionfields  where areaid='"+areaid+"' and ? like concat('%',fieldvalue ,'%') \n";
//			String [] ballfield = allfield.split("`");
//			PreparedStatement ps = null;
//			for(int i=0;i<ballfield.length;i++) {
//				String value1 = CHF.replaceStr(ballfield[i],"＋","+");
//				value1 = CHF.replaceStr(value1,"－","-");
//				value1 = CHF.replaceStr(value1,"＊","*");
//				value1 = CHF.replaceStr(value1,"／","`/");
//				value1 = CHF.replaceStr(value1,"（","(");
//				value1 = CHF.replaceStr(value1,"）",")");
//				
//				String value2 = CHF.replaceStr(ballfield[i],"＋","+");
//				value2 = CHF.replaceStr(value2,"+","`");
//				value2 = CHF.replaceStr(value2,"－","`");
//				value2 = CHF.replaceStr(value2,"-","`");
//				value2 = CHF.replaceStr(value2,"＊","`");
//				value2 = CHF.replaceStr(value2,"*","`");
//				value2 = CHF.replaceStr(value2,"／","`");
//				value2 = CHF.replaceStr(value2,"/","`");
//				value2 = CHF.replaceStr(value2,"（","`");
//				value2 = CHF.replaceStr(value2,"(","`");
//				value2 = CHF.replaceStr(value2,"）","`");
//				value2 = CHF.replaceStr(value2,")","`");
//				value2 = "`" + value2 + "`";
//				
//				ps = conn.prepareStatement(sql);
//				ps.setString(1,ballfield[i]);
//				rs = ps.executeQuery();
//				int iRsCount = 0;
//				
//				while(rs.next()) {//有对应列取对应列 
//					String evalue = rs.getString("evalue");
//					String fieldvalue = rs.getString("fieldvalue");
//					if(("`" + value2+ "`").indexOf("`" + fieldvalue + "`") >-1){
//						iRsCount ++;
//						value1 = CHF.replaceStr(value1,fieldvalue,evalue);
//					}
//					
//				}
//				
//				if(iRsCount<=1){
//					sql1 += value1+",";		
//				}else{
//					sql1 += value1+" as d"+i+",";
//				}
//				
//				DbUtil.close(rs);
//				DbUtil.close(ps);
//			}
			
			//去掉多余的null数组单元
			if (iGps>0){
				gps=new GuideLineProperty[iGps];
				for (int i=0;i<iGps;i++){
					gps[i]=gps1[i];
				}
			}
 
			
			//分组排序
			if (iGps>0){
				java.util.Arrays.sort(gps,new   java.util.Comparator(){
			        public   int   compare(Object   obj1,Object   obj2){
				        String   s1   =   ((GuideLineProperty)obj1).group,
				        		 s2   =   ((GuideLineProperty)obj2).group;
				        return   s1.compareTo(s2);     //不就完了？
			        }
			    });
			}
			String oldGroup="";
			int iGroupFields=0,iInnerFields=0;
			String strGroupFields[][]=new String[iGps][];
			String temp[]=new String[iGps];
			
			GuideLineProperty gp=null;
			String strGroup[]=null;
			
			if (iGps>0){
				strGroup=new String[iGps];
			
				gp=gps[0];
				oldGroup=gp.group;
				temp[iInnerFields++]=gp.field;
				for(int i=1; i<iGps; i++){
					gp=gps[i];
					
					if (gp.group.indexOf(oldGroup)>=0){
						//是一个分组的，继续追加
						temp[iInnerFields++]=gp.field;
					}else{
						//更新分组
						String temp1[]=new String[iInnerFields];
						for (int j=0;j<iInnerFields;j++){
							temp1[j]=temp[j];
						}
						strGroupFields[iGroupFields]=temp1;
						strGroup[iGroupFields++]=oldGroup;
						         
						//重置中间变量
						oldGroup=gp.group;
						iInnerFields=0;
						temp =new String[iGps];
						temp[iInnerFields++]=gp.field;
					}
				}
				if (gp.group.indexOf(oldGroup)>=0){
					//更新分组
					strGroupFields[iGroupFields]=temp;
					strGroup[iGroupFields++]=oldGroup;
				}
			
				String tSql1 = "",tSql2 = "",tSql3 = "";
				String [][] strGroupFields1 = new String [strGroupFields.length][];
				for(int i=0;i<strGroupFields1.length;i++){
					if(strGroupFields[i] != null){
						
						//System.out.println("strGroup="+strGroup[i]);
						
						strGroupFields1[i] = new String [strGroupFields[i].length] ;
						for(int j=0;j<strGroupFields1[i].length;j++){
							if(strGroupFields[i][j]!= null){
								strGroupFields1[i][j] = strGroupFields[i][j];	
								
								tSql1 += "," + strGroupFields[i][j];
								tSql2 += ",sum(" + strGroupFields[i][j] + ") as " + strGroupFields[i][j];
								tSql3 += ",a." + strGroupFields[i][j] + "=b." + strGroupFields[i][j];
							}
								
							
						}
					}
				}
				
				if(!"".equals(lowerLevel)){
//					表示显示一级时有一部的科目被分出去了
					sql = "create table " + lowerTable + " like " + tmpTable;
					st.execute(sql);
					
					st.execute("alter table " + lowerTable + " add column higherTokenid varchar(300) default NULL ");	//上级的tokenid
					
					if("1".equals(lowerLevel)){
						args.put("科目类型", "一级");
					}else if("2".equals(lowerLevel)){
						args.put("科目类型", "二级");
					}else{
						args.put("科目类型", "末级");
					}
					
					strSelectSql = ruleService.getRuleSQL(SubjectName, strStartYearMonth, strEndYearMonth, subjectid, end, args);
					System.out.println((String)args.get("科目类型") + "|" + strSelectSql);
					
					sql = "insert into " + lowerTable + " (fullName ,subjectname , tokenid, dataname , isSubject , sName ,direction2 ,sid ,recsign ) " +  strSelectSql;
					st.execute(sql);
					
					/**
					 * 求上级的tokenid`
					 */
					sql = "update  " + lowerTable + " a ,( \n" +
					" 	select a.*,b.tokenid as higherTokenid from ( \n" +
					" 		select distinct a.subjectid,a.subjectfullname1,a.subjectfullname2,a.tokenid ,isleaf1,level1 \n" +
					" 		from c_Account a \n" +
					" 		where a.subyearmonth*12+a.submonth>='"+strStartYearMonth+"' and a.subyearmonth*12+a.submonth<='"+strEndYearMonth+"'  \n" +
					" 		and (a.subjectfullname2 = '"+SubjectName+"' or a.subjectfullname2 like '"+SubjectName+"/%' ) \n" + 
					" 		union  \n" +
					" 		select a.subjectid,a.subjectfullname,a.subjectfullname,a.subjectfullname as tokenid ,isleaf,level0 \n" +
					" 		from z_usesubject a where projectid = "+projectid+"  \n" +
					" 		and tipsubjectid in ( \n" +
					" 			select distinct subjectid from c_account a where 1=1 \n" +
					" 			and a.subyearmonth*12+a.submonth>='"+strStartYearMonth+"' and a.subyearmonth*12+a.submonth<='"+strEndYearMonth+"' \n" +
					" 			and (a.subjectfullname2 like '"+SubjectName+"/%'  or a.subjectfullname2 = '"+SubjectName+"' ) \n" + 
					" 			and level1 = 1 \n" +
					" 		)  \n" +
					" 		union  \n" +
					" 		select a.subjectid,a.subjectfullname,a.subjectfullname,a.subjectfullname as tokenid ,isleaf,level0 \n" +
					" 		from z_usesubject  a where projectid = "+projectid+"  \n" +
					" 		and (a.subjectfullname like '"+SubjectName+"/%'  or a.subjectfullname = '"+SubjectName+"' ) \n" +
					" 	) a left join ( \n" +
					" 		select distinct a.subjectid,a.subjectfullname1,a.subjectfullname2,a.tokenid ,isleaf1,level1 \n" +
					" 		from c_Account a \n" +
					" 		where a.subyearmonth*12+a.submonth>='"+strStartYearMonth+"' and a.subyearmonth*12+a.submonth<='"+strEndYearMonth+"'  \n" +
					" 		and (a.subjectfullname2 = '"+SubjectName+"' or a.subjectfullname2 like '"+SubjectName+"/%' ) \n" + 
					" 		union  \n" +
					" 		select a.subjectid,a.subjectfullname,a.subjectfullname,a.subjectfullname as tokenid ,isleaf,level0 \n" +
					" 		from z_usesubject a where projectid = "+projectid+"  \n" +
					" 		and tipsubjectid in ( \n" +
					" 			select distinct subjectid from c_account a where 1=1 \n" +
					" 			and AccPackageID = "+acc+" and submonth = 1 \n" +
					" 			and (a.subjectfullname2 like '"+SubjectName+"/%'  or a.subjectfullname2 = '"+SubjectName+"' ) \n" + 
					" 			and level1 = 1 \n" +
					" 		)  \n" +
					" 		union  \n" +
					" 		select a.subjectid,a.subjectfullname,a.subjectfullname,a.subjectfullname as tokenid ,isleaf,level0 \n" +
					" 		from z_usesubject  a where projectid = "+projectid+"  \n" +
					" 		and (a.subjectfullname like '"+SubjectName+"/%'  or a.subjectfullname = '"+SubjectName+"' ) \n" +
					" 	) b on a.subjectfullname1 like concat(b.subjectfullname1,'/%') and a.level1 = b.level1 + 1 \n" +
					" ) b  \n" +
					" set a.higherTokenid = b.higherTokenid \n" +
					" where a.tokenid =b.tokenid ";
					st.execute(sql);
					
					ruleService.setBatchProjectValue(project.getProjectId(),Year,Month,lowerTable,strGroupFields,strGroup,args);
					
					/**
					 * 关联汇总
					 */
					sql = "insert into " + tmpTable + " (fullName ,subjectname , tokenid, dataname , isSubject , sName ,direction2 ,sid ,recsign "+tSql1+") " +
					" select fullName ,subjectname , tokenid, dataname , isSubject , sName ,direction2 ,sid ,recsign " + tSql1 + 
					" from " + lowerTable + 
					" where ifnull(higherTokenid,'') = '' ";
					st.execute(sql);
					
					sql = "update " + tmpTable + " a,(" +
					"	select higherTokenid " + tSql2 + 
					"	from " + lowerTable + " " +
					"	where higherTokenid is not null " +
					"	group  by higherTokenid" +
					" )b " +
					" set " + tSql3.substring(1) +
					" where a.tokenid = b.higherTokenid";
					st.execute(sql);
					
					/**
					 * 删除lowerTable临时表
					 */
					new com.matech.audit.work.subjectentry.SubjectEntry(conn).DelTempTable(lowerTable);
					
				}else{
					ruleService.setBatchProjectValue(project.getProjectId(),Year,Month,tmpTable,strGroupFields,strGroup,args);	
				}
				
			}
			
			//System.out.println("5555:" + CHF.getCurrentTime());
			
			st.executeQuery("set   charset   gbk;");   
			
			
			String limit = CHF.showNull((String)args.get("刷前几名"));
			
			
			if("".equals(orderby)){
				orderby = " isSubject,sid ";
			}
			if(!"".equals(limit)){
				limit = " limit 0," + limit;
			}
			
			
			sql = "update "+tmpTable+" set unitname=dataname where unitname is null ";
			st.execute(sql);
			
			
//			sql = "select fullname,subjectname ,dataname ,unitname ,direction2 ," + sql1 + " 0 from "+ tmpTable + " where 1=1 order by " + orderby + limit;
//			System.out.println(sql);
//			rs = st.executeQuery(sql);
			return null;
		} catch (Exception e) {
			System.out.println("出错的SQL："+ sql);
			e.printStackTrace();
			DbUtil.close(rs);
			DbUtil.close(st);
			throw e;
		}
		
		
	}

}




