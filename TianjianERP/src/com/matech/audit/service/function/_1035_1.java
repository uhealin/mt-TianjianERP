package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.framework.pub.util.ASFuntion;


public class _1035_1 extends AbstractAreaFunction {

	/**
	 * 先找出目标科目(ds 新公式的这个参数已经与科目借贷无关了)的全年发生额 A1= 借－贷，
	 * 再找出同时有目标科目和另一个参照科目（cs）的凭证里面该目标科目的发生额 A2= 借 - 贷
	 * 最终返回值就是 A1-A2
	 * 
	 * 参照1039的方法来修改1035，也废弃了三大费用的方法；
	 * 主要区别是这个公式是求出所有的下级科目的汇总发生数，而不是明细
	 * 本公式主要供未审报表调用
	 * 
	 *  * 另外增加了2个参数，使得参数体系和1039完全一致
	 * year=-1&thisyear=1
	 * 不提供的情况下，year默认=-1（参考上年），thisyear默认＝1(本年)
	 * 
	 * 还有一个month,等于xxx取调整;不给取全年;=01到12,取指定月
	 * 
	 * 
	 * 税审底稿专用取数公式,和_0的相比,12月份的发生数会无条件加上年末调整数
	 * 
	 * 
	 */
	public ResultSet process(HttpSession session, HttpServletRequest request, HttpServletResponse response, Connection conn, Map args) throws Exception {
		
		ASFuntion asf=new ASFuntion();
		
		String accpackageid=(String)args.get("curAccPackageID");
		String projectid=(String)args.get("curProjectid");
		
		//分割符
		String s=(String)args.get("s");
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
		
		//取列时的月份
		String month=request.getParameter("month");
		if (month==null){
			month="";
		}else{
			//把1到9改为01到09
			if (month.length()==1)
				month="0"+month;
		}
		
		//年份划定符,-1表示取去年和今年，1表示今年和下年
		String year=request.getParameter("year");
		if (year==null || year.equals("")){
			year="-1";
		}
		
		//统计年份，1表示当前年，-1表示另1参考年
		String thisyear=request.getParameter("thisyear");
		if (thisyear==null || thisyear.equals("")){
			thisyear="1";
		}
		
		//另一个年份的账套ID
		String anotherApkID="";
		
		//统计的账套编号
		String nowAccpackageid="";
		
		
		/**
		 * accpackageid : 当前项目的帐套编号
		 * anotherApkID ：比较年份的帐套编号，可以上一年，也可以下一年
		 * nowAccpackageid ：当前刷新的帐套编号，是前面2个帐套编号中的某一个；
		 */
		String sql="";
		
		Statement st=null;
		ResultSet rs=null;
		try{
			
			anotherApkID=String.valueOf((Integer.parseInt(accpackageid)+Integer.parseInt(year)));
			
			if("1".equals(thisyear)){
				nowAccpackageid=accpackageid;
			}else{
				nowAccpackageid=anotherApkID;
			}
			st=conn.createStatement();

			//取项目起止日期，在本公式中有作用的只是结束月
            String endmonth = (String) args.get("endmonth");
            String[] result=getProjectAuditAreaStringByProjectid(conn,projectid);
            if (endmonth==null || endmonth.equals("")){
                //如果前台没有提供这个参数，就从项目取；
                endmonth=result[3];
            }
            
            String enddate=result[2]+"-"+endmonth+"-31";
			
            //找出参数科目在客户中的所有科目编号（包括底层和非底层）
			String myds1="";
			int mylevel1=1;
			sql = " select  group_concat(subjectid SEPARATOR '\\',\\'') as subjectid,min(level1) \n"
					+ " from ( select subjectid,level1 from  c_account \n"
					+ " where submonth=1 \n"
					+ " and accpackageid =" + nowAccpackageid + " \n"
					+ " and subjectfullname2 like '" + ds + "%' \n" + " ) a";
			rs = st.executeQuery(sql);
			if (rs.next()) {
				myds1 = rs.getString(1);
				mylevel1=rs.getInt(2);
			} else {
				myds1 = "''";
			}
            
			//找出参数科目在客户中的底层科目的科目编号（只包括底层）
			sql=" select  \n" 
			+" 	group_concat(subjectid SEPARATOR '\\',\\'') as subjectid \n"
			+" from ( select subjectid from  c_account \n" 
			+" where isleaf1=1 \n" 
			+" and submonth=1 \n" 
			+" and accpackageid ="+nowAccpackageid+" \n" 
			+" and subjectfullname2 like '"+ds+"%' \n"
			+" ) a";
			rs=st.executeQuery(sql);
			if(rs.next()){
				myds=rs.getString(1);
			}else{
				myds="''";
			}
			
			//找出冲销参数科目在客户中的所有科目编号（包括底层和非底层）
			sql=" select  \n" 
				+" 	group_concat(subjectid SEPARATOR '\\',\\'') as subjectid \n"
				+" from ( select subjectid from  c_account \n" 
				+" where submonth=1 \n" 
				+" and accpackageid ="+nowAccpackageid+" \n" 
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
			rs.close();
			
			String string = "";
			if(isFunctionType(conn,nowAccpackageid)){
				string = " and a.occurvalue * a.dirction * b.occurvalue * b.dirction <=0 ";
			}
			
			
			boolean bHasEntry=false;
			sql="select 1 from c_subjectentry where accpackageid="+nowAccpackageid + " limit 1" ;
			rs=st.executeQuery(sql);
			if(rs.next()){
				bHasEntry=true;
			}
			
			
			//取调整数
			sql="select sum(if(b.occurvalue is null,0,b.occurvalue))* ("+ptype+") as occ \n"
				+"from \n"
				+"( \n"
				+"	select distinct accname as subjectname,subjectfullname1 as subjectfullname \n"
				+"	from c_account  \n"
				+"	where accpackageid in("+accpackageid+","+anotherApkID+") \n"
				+"	and isleaf1=1 \n"
				+"	and subjectfullname2 like '"+ds+"%' \n"
				+")a  \n"
				+"left join \n" 
				+"( \n"
				+"	select b.subjectfullname,(DebitTotalOcc1+DebitTotalOcc2-CreditTotalOcc1-CreditTotalOcc2) as occurvalue \n"
				+"	from z_accountrectify a   \n"
				+"	inner join    \n"
				+"	c_accpkgsubject b \n"  
				+"	on a.subjectid=b.subjectid \n"  
				+"	where a.projectid="+projectid+" \n"
				+"	  and b.accpackageid  = "+accpackageid+" \n"
				+"	and   b.subjectid in ('"+myds+"') \n" 
				+")b \n"
				+"on a.subjectfullname=b.subjectfullname \n";
			rs=st.executeQuery(sql);
			

			//最后的组装
			if (month.equals("xxx")){
				//调整数之前已经执行过了,不作任何操作就好了
			}else{
				
				//先取调整数
				rs.next();
				double bTz = rs.getDouble(1); 
				rs.close();
				
				if (month.equals("")){
					//全年
					if (bHasEntry){
					
						//取余额表发生数（本年的或者上年/下年的）
						sql="select (if(b.occ1 is null,0,b.occ1)-if(c.occ2 is null,0,c.occ2))* ("+ptype+") + "+ bTz +" as occ \n"
						+"from ( \n"
						+"	select sum(DebitTotalOcc)-sum(CreditTotalOcc) as occ1 \n" 
						+"	from c_account \n"
						+"	where accpackageid="+nowAccpackageid+" \n"
						+"	and submonth="+endmonth+" \n"
						+"	and level1="+mylevel1+" \n"
						+"	and subjectid  in ('"+myds1+"') \n"
						+")b, \n"
						+"( \n"
						+"select sum(a.occurvalue* a.dirction) as occ2 \n" 
						+"	from ( \n"
						+"		select distinct a.* "	
						+"		from c_subjectentry a, c_subjectentry b \n"
						+"		where a.accpackageid="+nowAccpackageid+" \n"
						+"		and a.subjectid in ('"+myds1+"') \n"
						+"      and a.vchdate <= '"+enddate+"' \n"
						+"		and b.accpackageid="+nowAccpackageid+" \n"
						+"		and b.subjectid in ('"+mycs+"') \n"
						+"      and b.vchdate <='"+enddate+"' \n"
						+"		and a.voucherid=b.voucherid \n"
						+string
						+"	)a \n"
						+")c";
					}else{
						//没有记录的情况下，先考虑借发生－贷发生，为0的话就取同方向的发生
						sql = "	select case sum(DebitTotalOcc)-sum(CreditTotalOcc) when 0 then  sum(DebitTotalOcc)  else (sum(DebitTotalOcc)-sum(CreditTotalOcc)) * direction2 end  + "+ bTz +" as occ \n" 
							+"	from c_account \n"
							+"	where accpackageid="+nowAccpackageid+" \n"
							+"	and submonth="+endmonth+" \n"
							+"	and level1="+mylevel1+" \n"
							+"	and subjectid  in ('"+myds1+"') \n";
						
					}
					
					/**
					 * @todo
					 */
					
					
					
				}else{
					
					if (!month.equals("12")) bTz=0;
					
					if (bHasEntry){
						//取余额表指定月份的发生数(本年或者上年的)
						sql="select (if(b.occ1 is null,0,b.occ1)-if(c.occ2 is null,0,c.occ2))* ("+ptype+")  + "+ bTz +" as occ \n"
							+"from ( \n"
							+"	select sum(debitocc)-sum(creditocc) as occ1 \n" 
							+"	from c_account \n"
							+"	where accpackageid="+nowAccpackageid+" \n"
							+"	and submonth="+month+" \n"
							+"	and level1="+mylevel1+" \n"
							+"	and subjectid  in ('"+myds1+"') \n"
							+")b, \n"
							+"( \n"
							+"select sum(a.occurvalue* a.dirction) as occ2 \n" 
							+"	from ( \n"
							+"		select distinct a.* "	
							+"		from c_subjectentry a, c_subjectentry b \n"
							+"		where a.accpackageid="+nowAccpackageid+" \n"
							+"		and a.subjectid in ('"+myds1+"') \n"
							+"      and a.vchdate like '%-"+month+"-%' \n"
							+"		and b.accpackageid="+nowAccpackageid+" \n"
							+"		and b.subjectid in ('"+mycs+"') \n"
							+"      and b.vchdate like '%-"+month+"-%' \n"
							+"		and a.voucherid=b.voucherid \n"
							+string
							+"	)a \n"
							+")c";
					}else{
						//没有记录的情况下，先考虑借发生－贷发生，为0的话就取同方向的发生
						sql = "	select case sum(debitocc)-sum(creditocc) when 0 then  sum(debitocc)  else (sum(debitocc)-sum(creditocc)) * direction2  end  + "+ bTz +" as occ \n" 
							+"	from c_account \n"
							+"	where accpackageid="+nowAccpackageid+" \n"
							+"	and submonth="+month+" \n"
							+"	and level1="+mylevel1+" \n"
							+"	and subjectid  in ('"+myds1+"') \n";

						
					}
				}
				
				System.out.println("qwh:sql3111="+sql);  
				rs=st.executeQuery(sql);
				
			}

				
			return rs;
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}
}
