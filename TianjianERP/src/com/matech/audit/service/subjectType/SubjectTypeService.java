package com.matech.audit.service.subjectType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.matech.framework.pub.db.DbUtil;

public class SubjectTypeService {

	public Connection conn = null;
	public SubjectTypeService(Connection conn){
		this.conn = conn;
	}
	
	public List getSubjectType(String AccID) throws Exception {
		DbUtil.checkConn(conn);
		List list = new ArrayList();
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			String sql = "select * from c_subjectType where AccpackageID = ? order by subid";
			ps = conn.prepareStatement(sql);
			ps.setString(1, AccID);
			
			rs = ps.executeQuery();
			while(rs.next()){
				list.add(rs.getString("subid")+"_"+rs.getString("ctype"));
			}
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return list;
	}
	
	//自动判断是新准则还是旧准则
	public int autoJudgeVocation(String accpackageid){
		int iResult=1;//旧准则；
		
		//如果4字头有本年利润、或者利润分配，且6字头有收入或支出或费用（注意是或），就判断是新准则
		String strSql="select exists( \n"
			+"	select 1  \n"
			+"		from c_accpkgsubject \n" 
			+"		where accpackageid="+accpackageid+" \n" 
			+"		and subjectid like '4%'  \n"
			+"		and (subjectname like '%本年利润%' or subjectname like '%利润分配%') \n"
			+"	) and exists( \n"
			+"		select 1  \n"
			+"		from c_accpkgsubject \n" 
			+"		where accpackageid="+accpackageid+" \n" 
			+"		and subjectid like '6%'  \n"
			+"		and (subjectname like '%支出%' or subjectname like '%收入%'  or subjectname like '%费用%') \n"
			+"	) as myresult";
		ResultSet rs = null;
		Statement st = null;
		try {
			st = conn.createStatement();
			rs = st.executeQuery(strSql);
			if(rs.next()) {
				if (rs.getBoolean(1)){
					return 59;
				}else{
					return 1;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
		}
		return iResult;
	}
	
	//按照凭证是否结转凭证来设置SubjectEntry表的property字段，是就追加2
	public void autosetEntryCarrydownProperty(String accpackageid) throws Exception{
		
		ResultSet rs = null;
		Statement st = null;
		String strSql="";
		try{
			//获得本年利润或利润分配在这套帐里面的叶子科目编号
			strSql="select distinct subjectid from c_account \n"
				+"		where accpackageid="+accpackageid+" \n"
				+"		and isleaf1=1  \n"
				+"		and submonth=1 \n"
				+"		and substr(subjectfullname2,1, if(locate('/',subjectfullname2) = 0, \n" 
				+"			length(subjectfullname2), locate('/',subjectfullname2) -1 ) \n" 
				+"		) in ('本年利润','利润分配') ";
			st = conn.createStatement();
			rs = st.executeQuery(strSql);
			String subjectids="";
			while(rs.next()) {
				subjectids+=",'" + rs.getString(1) +"'";
			}
			rs.close();
			org.util.Debug.prtOut("autosetEntryCarrydownProperty:subjectids:"+subjectids);
			
			String strWhere=" b.subjectid like '5%' ";
			if (autoJudgeVocation(accpackageid)==59){
				strWhere=" b.subjectid like '6%' ";
			}
			
			/**
			 *	根据标准科目求 损益类科目
			 */
			String strWhere1 = "";
			strSql = "select group_concat(distinct \"'\",a.subjectid,\"'\")  from ( " +
				"	select * from c_account a where accpackageid = "+accpackageid+" and SubMonth =1" +
				") a ,(" +
				"	select b.* from k_standsubject b ,(select vocationid from k_customer where departid = "+accpackageid.substring(0, 6)+" ) c " +
				"	where  b.subjectid like if(c.vocationid=59,'6%','5%')  and level0 = 1 and b.vocationid = b.vocationid " +
				") b " +
				"where 1=1 " +
				" and (a.subjectfullname2 = b.subjectfullname or a.subjectfullname2 like concat(b.subjectfullname,'/%'))";
			rs = st.executeQuery(strSql);
			while(rs.next()) {
				strWhere1 = " b.subjectid in ( "+rs.getString(1)+") ";
			}
			rs.close();
			
			strWhere = strWhere + " or " + strWhere1;
			/**
			 * 按照一边是本年利润或利润分配、一边是损益类科目的方式来找结转凭证；
			 * 然后过滤掉所有还有银行、现金、其他货币资金分录的凭证；

			strSql="select subid from c_subjecttype where accpackageid="+accpackageid+" and ctype='损益类'";
			rs = st.executeQuery(strSql);
			String strWhere="";
			if (rs.next()){
				strWhere=" b.subjectid like '"+rs.getString(1)+"%'";
			}else{
				strWhere= " b.subjectid like '"+strId+"%' ";
			}
			rs.close();
						 */
			org.util.Debug.prtOut("autosetEntryCarrydownProperty:strWhere:"+strWhere);
			
			//将结转凭证的第二个属性位设置为2
			strSql="select group_concat(distinct a.voucherid) as voucherids \n"
				+"from c_subjectentry a, c_subjectentry b    \n"
				+"where a.accpackageid="+accpackageid+" \n"
				+"	and a.subjectid in ('-1'"+subjectids+")   \n"
				+"and b.accpackageid="+accpackageid+" \n"
				+"and ("+strWhere+")   \n"
				+"and a.voucherid=b.voucherid  \n"
				+"and  not exists ( \n"
				+"	select subjectid from c_account c \n"
				+"	where accpackageid="+accpackageid+" \n"
				+"	and submonth=1 \n"
				+"	and  \n"
				+"	(subjectfullname2 like '库存现金%' \n"
				+"		or subjectfullname2 like '现金%' \n"
				+"		or subjectfullname2 like '银行存款%' \n"
				+"		or subjectfullname2 like '其他货币资金%' \n"
				+"	) \n"
				+"	and concat(a.debitsubjects,a.creditsubjects) like concat('%,',c.subjectid,',%') \n" 
				+")";
			rs = st.executeQuery(strSql);
			String vchids="-1";
			if (rs.next()){
				vchids=rs.getString(1);
			}
			
			strSql="update c_subjectentry a set a.property=insert(a.property,2,1,'2') " +
				"where accpackageid="+accpackageid+" and voucherid in ("+vchids+") and property not like '%3%'";
			st.execute(strSql);

			
			//追加了对辅助核算分录的位数设置
			strSql="update c_assitementry a set a.property=insert(a.property,2,1,'2') "+
					"where accpackageid="+accpackageid+" and voucherid in ("+vchids+") and property not like '%3%'";
			st.execute(strSql);
			
		}catch (Exception e) {
			System.out.println("sql执行出错:"+strSql);
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
		}
	}
	
	//设置科目的缺省类别属性，会按照这个客户的对应科目来设置
	public void autoset(String accpackageid) throws Exception{
		try {
			String sql = "delete from c_subjecttype where accpackageid = "+accpackageid+"";
			new DbUtil(conn).executeUpdate(sql);
			
			sql ="insert into c_subjecttype(accpackageid,subid,ctype) \n"
				+"select "+accpackageid+",a.value,if(c.name is null,b.name,c.name) as name \n" 
				+"from ( \n"
				+"	select distinct substring(subjectid,1,1) value \n"
				+"	from c_accpkgsubject where accpackageid="+accpackageid+" \n"
				+") a  \n"
				+"left join asdb.k_dic b \n"
				+"on b.ctype='subjectType' and a.value = b.value \n"
				+"left join ( \n"
				+"  select b.* from k_customer a ,k_dic b \n"
				+"  where departid=substring('"+accpackageid+"',1,6) \n"
				+"  and b.ctype=concat('subjectType' ,a.vocationid) \n"
				+")c \n"
				+"on a.value = c.value \n"
				+"order by a.value";
			new DbUtil(conn).executeUpdate(sql);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}
	
	public void save(String AccID, String sid, String ctype) throws Exception {
		DbUtil.checkConn(conn);
		
		PreparedStatement ps = null;
		try{
			String sql = "update c_subjectType set ctype = ? where subid = ? and accpackageid = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, ctype);
			ps.setString(2, sid);
			ps.setString(3, AccID);
			
			ps.execute();
			
			
			//更改科目性质后，需要重新设置凭证分录属性
			autosetEntryCarrydownProperty(AccID);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbUtil.close(ps);
		}
	}
}
