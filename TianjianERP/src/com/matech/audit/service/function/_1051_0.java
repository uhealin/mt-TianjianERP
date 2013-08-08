package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.framework.pub.util.ASFuntion;


public class _1051_0 extends AbstractAreaFunction {

	/**
	 * 
	 * 参照1039的方法来修改1051，也废弃了三大费用的方法；
	 * 主要区别是这个公式是求出指定科目的每个月的发生数。
	 * 
	 * 计算费用的方法：
	 * 先找出目标科目(ds 新公式的这个参数已经与科目借贷无关了)的全年发生额 A1= 借－贷，
	 * 再找出同时有目标科目和另一个参照科目（cs）的凭证里面该目标科目的发生额 A2= 借 - 贷
	 * 最终返回值就是 A1-A2
	 *
	 * 主要参数：
	 * 1、一个subjectid，提供的话取指定科目编号的发生逐月累计其发生额；
	 *    不指定的话取ds科目，逐月累计其发生额；
	 * 2、还有一个month,不给取项目结束月;=01到12,取指定月 
	 * 
	 * 次要参数：
	 *    和其他1039衍生公式不同，去掉了year=-1&thisyear=1这2个参数
	 * 
	 * 公式使用举例：
	 * 取指定科目主营业务收入的每个月（至项目结束月）的发生额；
	 * =取列公式覆盖(1051, "", "occ", "&s=;&cs=本年利润;利润分配&ds=主营业务收入&dt=1")
	 * 
	 * 取指定科目5101.02.01的（至项目结束月）的发生额；
	 * =取列公式覆盖(1051, "", "occ", "&s=;&cs=本年利润;利润分配&ds=主营业务收入&dt=1&subjectid=5101.02.01")
	 * 
	 * 主要用于63年审模板，主营业务收入等审定表
	 * 
	 */
	public ResultSet process(HttpSession session, HttpServletRequest request, HttpServletResponse response, Connection conn, Map args) throws Exception {
		
		ASFuntion asf=new ASFuntion();
		
		String accpackageid=(String)args.get("curAccPackageID");
		String projectid=(String)args.get("curProjectid");
		
		//分割符
		String s=(String)args.get("s");
		if (s==null || "".equals(s)){
			s=";";
		}
		
		//第一个科目（下面称借方科目）
		String ds=(String)args.get("ds");
		String myds="";
		
		//第二个科目（下面称贷方科目）
		String cs=(String)args.get("cs");
		cs=asf.replaceStr(cs, s, "','");
		String mycs="";
		
		//余额方向 1 
		String dt=(String)args.get("dt"),ptype="";
		if (dt!=null && dt.equals("-1")){
			ptype="-1";
		}else{
			ptype="1";
		}
		
		String sql="";
		
		Statement st=null;
		ResultSet rs=null;
		try{
			
			st=conn.createStatement();
			
			//取项目起止日期，在本公式中有作用的只是结束月
            String endmonth = (String) args.get("month");
            String endyear=accpackageid.substring(6);
            
            String[] result=getProjectAuditAreaStringByProjectid(conn,projectid);
            
            if (endmonth==null || endmonth.equals("")){
                //如果前台没有提供这个参数，就从项目取；
                endmonth=result[3]; 
            }
            int month=0;
            try{
            	month=Integer.parseInt(endmonth);
            }catch(Exception e){}
            if (month<1 || month>12){
            	month=12;
            }
            endmonth=String.valueOf(month);
            if (endmonth.length()==1)
            	endmonth="0"+endmonth;
			
            int strBegin = Integer.parseInt(result[0]) * 12 + Integer.parseInt(result[1]);
            int strEnd = Integer.parseInt(result[2]) * 12 + Integer.parseInt(result[3]);
            
            String begindate=result[0]+"-"+result[1]+"-01";
            String enddate=result[2]+"-"+endmonth+"-31";
            
			//找出参数科目在客户中的底层科目的科目编号
            String subjectid=request.getParameter("subjectid");
            if (subjectid==null || "".equals(subjectid)){
				sql=" select  \n" 
				+" 	group_concat(subjectid SEPARATOR '\\',\\'') as subjectid \n"
				+" from ( select distinct subjectid from  c_account \n" 
				+" where isleaf1=1 \n" 
//				+" and submonth=1 \n" 
//				+" and accpackageid ="+accpackageid+" \n"
				+"	and SubYearMonth *12 +SubMonth >= "+strBegin+" \n" 
				+"	and SubYearMonth *12 +SubMonth <= "+strEnd+" \n" 
				+" and subjectfullname2 like '"+ds+"%' \n"
				+" ) a";
				rs=st.executeQuery(sql);
				if(rs.next()){
					myds=rs.getString(1);
				}else{
					myds="";
				}
            }else{
            	//提供了参数科目，但是还是需要找一下这个科目的所有下级科目；
            	sql=" select  \n" 
    				+" 	group_concat(subjectid SEPARATOR '\\',\\'') as subjectid \n"
    				+" from ( select distinct subjectid from  c_account \n" 
    				+" where isleaf1=1 \n" 
//    				+" and submonth=1 \n" 
//    				+" and accpackageid ="+accpackageid+" \n"
    				+"	and SubYearMonth *12 +SubMonth >= "+strBegin+" \n" 
    				+"	and SubYearMonth *12 +SubMonth <= "+strEnd+" \n" 
    				+" and subjectid like '"+subjectid+"%' \n"
    				+" ) a";
    			rs=st.executeQuery(sql);
    			if(rs.next()){
    					myds=rs.getString(1);
    				}else{
    					myds="";
    			}
            }
			
			//找出冲销参数科目在客户中的底层科目的科目编号
			sql=" select  \n" 
				+" 	group_concat(subjectid SEPARATOR '\\',\\'') as subjectid \n"
				+" from ( select distinct subjectid from  c_account \n" 
				+" where isleaf1=1 \n" 
//				+" and submonth=1 \n" 
//				+" and accpackageid ="+accpackageid+" \n"
				+"	and SubYearMonth *12 +SubMonth >= "+strBegin+" \n" 
				+"	and SubYearMonth *12 +SubMonth <= "+strEnd+" \n" 
				+" and substr(subjectfullname2,1, \n" 
				+" 			if(locate('/',subjectfullname2) = 0, \n" 
				+" 					length(subjectfullname2), \n" 
				+" 					locate('/',subjectfullname2) -1 ) \n" 
				+"             ) in ('"+asf.replaceStr(cs, s, "','")+"')  \n" 
				+" ) a";
			rs=st.executeQuery(sql);
			if(rs.next()){
				mycs=rs.getString(1);
			}else{
				mycs="''";
			}
			
			//最后的组装
			sql="select a.submonth, (if(a.occ1 is null,0,a.occ1)-if(c.occ2 is null,0,c.occ2))* ("+ptype+") as occ  \n"
				+"from \n"
				+"( \n"
				+"select submonth,sum(debitocc-creditocc) as occ1 \n" 
				+"from c_account \n"
				+"where 1=1 "
//				+"and accpackageid="+accpackageid+" \n" 
//				+"and submonth<="+month+"  \n"
				+"	and SubYearMonth *12 +SubMonth >= "+strBegin+" \n" 
				+"	and SubYearMonth *12 +SubMonth <= "+strEnd+" \n" 
				// +"and isleaf1=1  \n"
				+"and subjectid in ('"+myds+"') \n" 
				+"group by submonth \n"
				+")a  \n"
				+"left join \n" 
				+"(  \n"
				+"	select cast(substring(a.vchdate,6,2)as UNSIGNED ) as submonth,sum(a.occurvalue* a.dirction) as occ2 \n" 
				+"	from (		select distinct a.* 		from c_subjectentry a, c_subjectentry b  \n"
				+"	where 1=1 "
//				+"and a.accpackageid="+accpackageid+"  \n"
				+"	and a.subjectid in ('"+myds+"') \n" 
//				+"	and a.vchdate <= '"+endyear+"-"+endmonth+"-31' \n"
				+"      and a.vchdate >= '"+begindate+"' \n"
				+"      and a.vchdate <= '"+enddate+"' \n"
				+"	and b.accpackageid="+accpackageid+"  \n"
				+"	and b.subjectid in ('"+mycs+"')  \n"
//				+"	and b.vchdate <= '"+endyear+"-"+endmonth+"-31' \n"
				+"      and b.vchdate >= '"+begindate+"' \n"
				+"      and b.vchdate <= '"+enddate+"' \n"
				+"	and a.voucherid=b.voucherid  \n"
				+"	)a	group by  cast(substring(a.vchdate,6,2)as UNSIGNED ) \n"
				+")c  \n"
				+"on a.submonth=c.submonth \n"
				+"order by a.submonth";
			System.out.println("qwh:sql3="+sql);
			rs=st.executeQuery(sql);
				
			return rs;
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
}
