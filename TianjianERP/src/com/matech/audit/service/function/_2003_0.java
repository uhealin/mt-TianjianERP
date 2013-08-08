package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.framework.pub.util.ASFuntion;

public class _2003_0 extends AbstractAreaFunction {


	public ResultSet process(HttpSession session, HttpServletRequest request, HttpServletResponse response, Connection conn, Map args) throws Exception {
		
		
// =取自定义函数(2003,"occ","&subjectName=应收账款&inside= &notInside= &direction=1&startMonth=1&endMonth=12")
		
		ASFuntion asf=new ASFuntion();
		
		String apkID=(String)args.get("curAccPackageID");
		String prjID=(String)args.get("curProjectid");
		
		//统计外币
		String inside=asf.showNull((String)args.get("inside"));
		//排除外币
		String notInside=asf.showNull((String)args.get("notInside"));
		if(notInside.length()>2){
			notInside=","+notInside;
		}
		//科目名称
		String subjectName=asf.showNull((String)args.get("subjectName"));
		//方向
		String direction=asf.showNull((String)args.get("direction"));
		//开始月份
		String startMonth=asf.showNull((String)args.get("startMonth"));
		if(startMonth.length()==1){
			startMonth=("0"+startMonth);
		}
		//结束月份
		String endMonth=asf.showNull((String)args.get("endMonth"));
		if(endMonth.length()==1){
			endMonth=("0"+endMonth);
		}
		String sql="";
		
		Statement st=null;
		ResultSet rs=null;
		try{
			st=conn.createStatement();

//　　　　　找出参数科目在客户中的名字
			
			sql=""
				+" select  \n" 
				+" 	group_concat(accname SEPARATOR '\\',\\'')  \n" 
				
				+"  \n"
				+" from ( select accname from  c_account \n" 
				+" where level1=1 \n" 
				+" and submonth=1 \n" 
				+" and accpackageid="+apkID+" \n" 
				+" and subjectfullname2 = '"+subjectName+"' \n"
				
				+" union  \n"
				
				+"  	select substr(subjectfullname,1, \n" 
					+" 			if(locate('/',subjectfullname) = 0, \n" 
					+" 					length(subjectfullname), \n" 
					+" 					locate('/',subjectfullname) -1 ) \n" 
					+"             ) as accname \n"
				+"  	from   \n" 
				+"  	z_subjectentryrectify a  \n" 
				+"  	inner join   \n" 
				+"  	c_accpkgsubject b  \n" 
				+"  	on a.subjectid=b.subjectid  \n" 
				+"  	where a.accpackageid="+apkID+"  \n" 
				+"  	  and a.projectid="+prjID+"  \n" 
				+"  	  and b.accpackageid="+apkID+"  \n" 
				+"  	  and a.property in(311,411)  \n" 
				+" and   substr(subjectfullname,1, \n" 
				+" 			if(locate('/',subjectfullname) = 0, \n" 
				+" 					length(subjectfullname), \n" 
				+" 					locate('/',subjectfullname) -1 ) \n" 
				+"             ) in ('"+subjectName+"') \n" 
				
				
				+" ) a";
				

				
				rs=st.executeQuery(sql);
				if(rs.next()){
					subjectName=rs.getString(1);
				}else{
					subjectName="''";
				}
				
				sql=" select sum(dirction * occurvalue * ("+direction+")) as occ from c_subjectentry \n "
				   +" where accpackageid="+apkID+" \n "
				   +" and currency not in('','人民币'"+notInside+") \n "
				   +" and (subjectfullname1 = '"+subjectName+"' or subjectfullname1 like '"+subjectName+"/%')\n";
				if(inside.length()>2){
					sql+=" and currency     in("+inside+") \n ";
				}
				sql+=" and substring(vchdate,6,2) >= '"+startMonth+"' \n"
				    +" and substring(vchdate,6,2) <= '"+endMonth+"' \n"
				   ;
				
			rs=st.executeQuery(sql);
			
			return rs;
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
		
	}
	
	
}
