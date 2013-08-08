package com.matech.audit.service.checkInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.customer.CustomerService;
import com.matech.audit.service.essentiality.ReportModel;
import com.matech.audit.service.project.ProjectService;
import com.matech.audit.service.project.model.Project;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class CorrespondVoucher {

	private Connection conn = null;

	private String accpackageid = "";
	private String percentage = ""; //重要性水平大额区间：80
	public String getPercentage() {
		return percentage;
	}

	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}

	public String getAccpackageid() {
		return accpackageid;
	}

	public void setAccpackageid(String accpackageid) {
		this.accpackageid = accpackageid;
	}

	
	public CorrespondVoucher() {  
	}
	
	public CorrespondVoucher(Connection conn) {
		this.conn=conn;
	}

	//取科目全称
	public static String getSubjectName(String subjectid, String accpackageid) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		String subjectfullname = "";

		DBConnect dbc = new DBConnect();
		try {
			sql = "select * from c_accpkgsubject where subjectid=? and accpackageid=?";
			conn = dbc.getConnect(accpackageid.substring(0, 6));
			ps = conn.prepareStatement(sql);
			ps.setString(1, subjectid);
			ps.setString(2, accpackageid);
			//      conn.prepareStatement()
			rs = ps.executeQuery();
			if (rs.next())
				subjectfullname = rs.getString("subjectfullname");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return subjectfullname;
	}

	//取科目名称
	public static String getSubjectName(String subjectid, String accpackageid,
			int flag) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		String subjectname = "";

		DBConnect dbc = new DBConnect();
		try {
			sql = "select * from c_accpkgsubject where subjectid=? and accpackageid=?";
			conn = dbc.getConnect(accpackageid.substring(0, 6));
			ps = conn.prepareStatement(sql);
			ps.setString(1, subjectid);
			ps.setString(2, accpackageid);
			//      conn.prepareStatement()
			rs = ps.executeQuery();
			if (rs.next())
				subjectname = rs.getString("subjectname");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return subjectname;
	}

	//实现对应凭证分析的查询字符串
	public String getCVCSQL(String cusotmerid, String begyear, String begmonth,
			String endyear, String endmonth, String subjectid, String direction) {
		StringBuffer sb = new StringBuffer("");
		String revdirection = "";
		String accpackageid = cusotmerid + endyear;
		if ("-1".equals(direction))
			revdirection = "1";
		else
			revdirection = "-1";
		sb
				.append("select vchdate,typeid,oldvoucherid,t1.summary,t1.subjectid,subjectname,case dirction when 1 then '借' when -1 then '贷' end as dirction,occurvalue,");
		sb.append("       voucherid,accpackageid,t1.autoid ");
		sb.append("from (");
		sb
				.append("      select vchdate,typeid,oldvoucherid,summary,subjectid,dirction,occurvalue,voucherid,accpackageid ");
		sb.append("      from c_subjectentry ");
		sb.append("      where c_subjectentry.dirction =" + revdirection + " ");
		sb.append("            and c_subjectentry.voucherid in (");
		sb
				.append("                                             select voucherid from c_subjectentry ");
		sb
				.append("                                             where (accpackageid>='"
						+ cusotmerid
						+ begyear
						+ "' and accpackageid<='"
						+ accpackageid + "') ");
		sb
				.append("                                                   and property like '1%' ");
		sb
				.append("                                                   and subjectid like '"
						+ subjectid + "%' ");
		sb
				.append("                                                   and dirction ="
						+ direction + " ");
		sb
				.append("                                                   and vchdate between concat('"
						+ begyear
						+ "','-',lpad('"
						+ begmonth
						+ "',2,'0'),'-01') and concat('"
						+ endyear
						+ "','-',lpad('" + endmonth + "',2,'0'),'-31') ");
		sb.append("                                             )) as t1 ");
		sb.append("      left join ");
		sb.append("      (select subjectid,subjectname from c_accpkgsubject ");
		sb.append("       where c_accpkgsubject.accpackageid>='" + cusotmerid
				+ begyear + "' and c_accpkgsubject.accpackageid<='"
				+ accpackageid + "') as t2 ");
		sb.append("      on t1.subjectid=t2.subjectid ");
		return sb.toString();
	}

	//对应凭证分的改进版
	public static String getSQL(String cusotmerid, String begyear,
			String begmonth, String endyear, String endmonth,
			String ssubjectid, String sdirection, String csubjectid,
			String cdirection) {
		StringBuffer sb = new StringBuffer("");
		String accpackageid = cusotmerid + endyear;
		sb
				.append("select vchdate,typeid,oldvoucherid,summary,t1.subjectid,subjectfullname,case dirction when 1 then '借' when -1 then '贷' end as direction,occurvalue,voucherid,accpackageid,0 ");
		sb.append("from ");
		sb
				.append("(select vchdate,typeid,oldvoucherid,summary,subjectid,dirction,occurvalue,voucherid,accpackageid ");
		sb.append(" from c_subjectentry ");
		sb.append(" where c_subjectentry.accpackageid>='" + cusotmerid
				+ begyear + "' and c_subjectentry.accpackageid<='"
				+ accpackageid + "') ");
		sb.append("       and voucherid in(");
		sb.append("             select voucherid from c_subjectentry ");
		sb.append("             where (accpackageid>='" + cusotmerid + begyear
				+ "' and accpackageid<='" + accpackageid + "') ");
		sb.append("                   and dirction=" + sdirection + " ");
		sb.append("                   and subjectid like '" + ssubjectid
				+ "%' ");
		sb.append("                   and vchdate between concat('" + begyear
				+ "','-',lpad('" + begmonth + "',2,'0'),'-01') and concat('"
				+ endyear + "','-',lpad('" + endmonth + "',2,'0'),'-31') ");
		sb.append("                   and property like '1%') ");
		sb.append("       and subjectid like '" + csubjectid + "%' ");
		sb.append("       and dirction=" + cdirection + " ");
		sb.append("       and property like '1%' ");
		sb.append("                   ) as t1 ");
		sb.append("left join ");
		sb
				.append("(select subjectid,subjectname,subjectfullname from c_accpkgsubject where c_accpkgsubject.accpackageid>='"
						+ cusotmerid
						+ begyear
						+ "' and c_accpkgsubject.accpackageid<='"
						+ accpackageid + "') as t2 ");
		sb.append("on t1.subjectid=t2.subjectid");
		return sb.toString();
	}

	public static String getSQL(String cusotmerid, String begyear,
			String begmonth, String endyear, String endmonth,
			String ssubjectid, String sdirection, String csubjectid,
			String cdirection, String projectid, String user) {
		StringBuffer sb = new StringBuffer("");
		String accpackageid = cusotmerid + endyear;
		//  sb.append("select y.vchid as p1,z.vchid as p2,0 as p3,vchdate,typeid,oldvoucherid,summary,t1.subjectid,subjectfullname,case dirction when 1 then '借' when -1 then '贷' end as direction,occurvalue,voucherid,accpackageid,currrate,CurrValue,Currency,Quantity,Unitprice,Unitname,autoid ");
		sb
				.append("select y.vchid as p1,z.vchid as p2,0 as p3,x.vchid as p4,t1.vchdate,t1.typeid,t1.oldvoucherid,t1.summary,t1.subjectid,subjectfullname,case dirction when 1 then '借' when -1 then '贷' end as direction,occurvalue,voucherid,t1.accpackageid,currrate,CurrValue,Currency,Quantity,Unitprice,Unitname,autoid ");

		sb.append("from ");
		sb
				.append("(select vchdate,typeid,oldvoucherid,summary,subjectid,dirction,occurvalue,voucherid,accpackageid,autoid,currrate,CurrValue,Currency,Quantity,Unitprice,Unitname ");
		sb.append(" from c_subjectentry ");
		sb.append(" where (c_subjectentry.accpackageid>='" + cusotmerid
				+ begyear + "' and c_subjectentry.accpackageid<='"
				+ accpackageid + "') ");
		sb.append("       and voucherid in(");
		sb.append("             select voucherid from c_subjectentry ");
		sb.append("             where (accpackageid>='" + cusotmerid + begyear
				+ "' and accpackageid<='" + accpackageid + "') ");
		sb.append("                   and (dirction=" + sdirection
				+ " or ( dirction=(-1) * " + sdirection
				+ " and occurvalue<0 ))");
		sb.append("                   and subjectid like '" + ssubjectid
				+ "%' ");
		sb.append("                   and vchdate between concat('" + begyear
				+ "','-',lpad('" + begmonth + "',2,'0'),'-01') and concat('"
				+ endyear + "','-',lpad('" + endmonth + "',2,'0'),'-31') ");
		sb.append("                   and property like '1%') ");
		sb.append("       and subjectid like '" + csubjectid + "%' ");
		sb.append(" and (dirction= -1 * (" + sdirection + ") or ( dirction= ("
				+ sdirection + ") and occurvalue<0 )) ");
		sb.append("       and property like '1%' ");
		sb.append("        ");
		sb.append("        ");
		sb.append("                   ) as t1 ");
		sb.append("left join ");
		sb
				.append("(select subjectid,subjectname,subjectfullname from c_accpkgsubject where c_accpkgsubject.accpackageid>='"
						+ cusotmerid
						+ begyear
						+ "' and c_accpkgsubject.accpackageid<='"
						+ accpackageid + "') as t2 ");
		sb.append("on t1.subjectid=t2.subjectid ");

		sb
				.append(" left join (select distinct vchid from z_voucherspotcheck  where projectid='"
						+ projectid
						+ "' and createor='"
						+ user
						+ "'   )  y on t1.voucherid=y.vchid \n ");

		sb
				.append(" left join (select distinct vchid from z_question  where projectid='"
						+ projectid
						+ "' and createor='"
						+ user
						+ "'   )  z on t1.voucherid=z.vchid \n ");

		sb
				.append(" left join (select distinct vchid from z_taxcheck  where projectid='"
						+ projectid
						+ "' and createor='"
						+ user
						+ "'   ) x on t1.autoid=x.vchid \n ");

		System.out.println("zyq=" + sb.toString());

		return sb.toString();
	}

	/*
	 numbers[0]:金额，numbers[1]:占总发生额的百分比，
	 numbers[2]:平均单笔金额，numbers[3]:最大单笔金额
	 */
	public static int calculate(String sql, double[] numbers, String departID) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int rowCount;
		//记录数
		rowCount = 0;
		//最大金额
		numbers[3] = 0;

		DBConnect dbc = new DBConnect();
		try {
			conn = dbc.getConnect(departID);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				++rowCount;
				numbers[0] += rs.getDouble("OccurValue");
				if (rs.getDouble("OccurValue") > numbers[3]) {
					numbers[3] = rs.getDouble("OccurValue");
				}
				//        org.util.Debug.prtOut("rowCount="+rowCount);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}
		return rowCount;
	}

	public static String setRed(String number) {
		if (number.indexOf("-") >= 0) {
			return "<font color='red'>" + number + "</font>";
		} else {
			return number;
		}
	}

	public static String getLeafSubjects(String cusotmerid, String begyear,
			String begmonth, String endyear, String endmonth,
			String ssubjectid, String sdirection, String[] csubjectids,
			String cdirection) {
		StringBuffer sb = new StringBuffer("");
		String accpackageid = cusotmerid + endyear;
		sb.append("select t1.subjectid,t2.subjectfullname,occurValue from (");
		sb.append("select subjectid,sum(occurvalue) as occurValue ");
		sb.append("from c_subjectentry ");
		sb.append("where (accpackageid>='" + cusotmerid + begyear
				+ "' and accpackageid<='" + accpackageid + "') ");
		sb.append("      and voucherid in( ");
		sb.append("          select voucherid from c_subjectentry ");
		sb.append("          where (accpackageid>='" + cusotmerid + begyear
				+ "' and accpackageid<='" + accpackageid + "') ");
		sb.append("                and dirction=" + sdirection + " ");
		sb.append("                and subjectid like '" + ssubjectid + "%'");
		sb.append("                and vchdate between concat('" + begyear
				+ "','-',lpad('" + begmonth + "',2,'0'),'-01') and concat('"
				+ endyear + "','-',lpad('" + endmonth + "',2,'0'),'-31')");
		sb.append("                and property like '1%')");
		sb.append("      and subjectid in (");

		for (int i = 0; i < csubjectids.length; i++) {
			if (i != 0)
				sb.append(" union ");
			sb
					.append("          select subjectid from c_accpkgsubject where isleaf=1 and (accpackageid>='"
							+ cusotmerid
							+ begyear
							+ "' and accpackageid<='"
							+ accpackageid
							+ "') and subjectid like '"
							+ csubjectids[i] + "%' ");
		}
		sb.append("                )");
		sb.append("      and dirction=" + cdirection + " ");
		sb.append("      and property like '1%' ");
		sb.append("group by subjectid  ");
		sb
				.append(") t1 left join (select * from c_accpkgsubject where (accpackageid>='"
						+ cusotmerid
						+ begyear
						+ "' and accpackageid<='"
						+ accpackageid + "')) t2 on t1.subjectid=t2.subjectid");

		return sb.toString();
	}

	public static Collection getSubjectObj(String sql, String departID) {

		Collection cole = new ArrayList();
		CorSub cs = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = new DBConnect().getConnect(departID);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				cs = new CorSub();
				cs.setSubjectId(rs.getString("subjectid"));
				cs.setSubjectFullName(rs.getString("subjectfullname"));
				cs.setOccurValue(rs.getDouble("occurValue"));
				cole.add(cs);
			}
			return cole;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return cole;
	}

	//对应凭证分所在的voucherid
	public static String getSQLVoucherID(String cusotmerid, String begyear,
			String begmonth, String endyear, String endmonth,
			String ssubjectid, String sdirection, String cdirection,String reversedirction

	) {
		String result = "''";
		StringBuffer sb = new StringBuffer("");
		String accpackageid = cusotmerid + endyear;

		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		DBConnect dbc = new DBConnect();
		try {
			sb.append("  select ifnull(group_concat(distinct voucherid),-1) from c_subjectentry ");
			sb.append("  where accpackageid='" + accpackageid + "' ");

			sb.append("  and   dirction=" + sdirection);          

			sb.append("  and   occurvalue>0");        
			sb.append("  and tokenid in (" + ssubjectid + ") ");
			sb.append("  and vchdate between concat('" + begyear
					+ "','-',lpad('" + begmonth + "',2,'0'),'-01') and concat('"
					+ endyear + "','-',lpad('" + endmonth + "',2,'0'),'-31') ");
			sb.append("                   and property like '1%' ");
		//	System.out.println("zyq:getSQLVoucherID:" + sb.toString());
			conn = dbc.getConnect(cusotmerid);
			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getString(1);
			}
			rs.close();
			ps.close();
			sb = new StringBuffer();
			sb.append("  select ifnull(group_concat(distinct voucherid),-1) from c_subjectentry ");
			sb.append("  where accpackageid='" + accpackageid + "' ");

			sb.append("  and   dirction=" + sdirection);          

			sb.append("  and   occurvalue<0");   
			sb.append("  and tokenid in (" + ssubjectid + ") ");
			sb.append("  and vchdate between concat('" + begyear
					+ "','-',lpad('" + begmonth + "',2,'0'),'-01') and concat('"
					+ endyear + "','-',lpad('" + endmonth + "',2,'0'),'-31') ");
			sb.append("                   and property like '1%' ");
		//	System.out.println("zyq:getSQLVoucherID:" + sb.toString());
			conn = dbc.getConnect(cusotmerid);
			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();
			if (rs.next()) {
				result = result+"#"+rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return result;
	}

	//优化后的对应凭证分的改进版
	public String getNumberArray(String cusotmerid, String begyear,
			String begmonth, String endyear, String endmonth,
			String ssubjectid, String sdirection, String csubjectid,
			String cdirection, String voucherids, String summary, String totle,
			String subjectid, String subjectname, String reversedirction,String total) 
			throws Exception{
	
		if (conn==null){
			throw new Exception("数据库连接不能为空");
		}
		String tempSubjectId = subjectid;
		if (!"".equals(subjectid) && subjectid != null) {
			subjectid = " and  bbb like '" + subjectid + "%'";
		} else {
			subjectid = "";
		}
		StringBuffer sb = new StringBuffer("");
		String accpackageid = cusotmerid + endyear;

		//原方向处理
	
		 String strDWhere = " and dirction= -1 * " + cdirection + " \n";

		 String[] voucherIds = voucherids.split("#");

		//先找出符合条件的凭证号
		PreparedStatement ps = null;
		ResultSet rs = null;

		//  
		try {
			
			//清理临时表
			ps = conn.prepareStatement("truncate table t_CorrespondVoucher1");
			ps.execute();
			ps.close();
			ps = conn.prepareStatement("truncate table t_CorrespondVoucher2");
			ps.execute();
			ps.close();
			
			//------------------------------------
			sb.append(" select  group_concat(distinct voucherid)  from c_subjectentry where   accpackageid='"+accpackageid+"' \n");		
			sb.append("   and property like '1%' \n");
			sb.append("   and summary like '%"+summary+"%' \n");
			sb.append("   and voucherid IN( \n");
			sb.append("                  "+voucherIds[0]+"      \n");
			sb.append("   ) \n");
		
			sb.append("   and ((dirction= " + cdirection + " and occurvalue>0) or ( dirction=(-1) * "+ cdirection + " and occurvalue<0 and tokenid not in ("+ssubjectid+")))\n");
			sb.append("   and tokenid IN( \n");
			sb.append("                  "+csubjectid+"      \n");
			sb.append("   ) \n");
			 
			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();

			String resultVid = null;
			if (rs.next()) {
				resultVid = rs.getString(1);
			} 
			if(resultVid==null) {
				resultVid = "-1";
			}
			rs.close();
			ps.close();
				
			//插入临时表
			sb= new StringBuffer(); 
			sb.append("insert into t_CorrespondVoucher1  \n");
			sb.append("     select subjectid,Serail,subjectfullname1 ,voucherid,sum(if(dirction=(-1)*"+cdirection+",occurvalue*(-1),occurvalue)) as occurvalue,UnitPrice,UnitName,'"+cdirection+"' as dirction ,    \n");
			sb.append("   if(-1 * " + cdirection + ">0,if(length(creditsubjects)-length(replace(creditsubjects,',',''))>3,1,0),if(length(debitsubjects)-length(replace(debitsubjects,',',''))>3,1,0)) as ifsome1 \n");
			sb.append("  from c_subjectentry \n ");
	        sb.append("     where  accpackageid='"+accpackageid+"'       \n");
	        sb.append("     and property like '1%' \n");
	        sb.append("     and voucherid IN( "+resultVid+"  \n");
	        sb.append("     ) \n");
	        sb.append("   and ((dirction= " + cdirection + " and occurvalue>0) or ( dirction=(-1) * "+ cdirection + " and occurvalue<0 and tokenid not in ("+ssubjectid+")))\n");
	        sb.append("     and tokenid IN(  "+csubjectid+"  ) \n");
	        sb.append("   group by subjectid,voucherid  \n" );
	        
	        ps = conn.prepareStatement(sb.toString());
			ps.execute();
			ps.close();
			
			sb= new StringBuffer();
			sb.append("insert into t_CorrespondVoucher2 select voucherid,sum(if(dirction=" + cdirection
					+ ",occurvalue*(-1),occurvalue)) as occurvalue ,      \n");
			sb.append("   if(length(debitsubjects)-length(replace(debitsubjects,',',''))>3&&length(creditsubjects)-length(replace(creditsubjects,',',''))>3,1,0) as type1 ,\n");
			sb.append("   if(-1 * " + cdirection + ">0,if(length(debitsubjects)-length(replace(debitsubjects,',',''))>3,1,0),if(length(creditsubjects)-length(replace(creditsubjects,',',''))>3,1,0)) as ifsome2 \n");			
			sb.append("     from   \n");
			sb.append("     c_subjectentry  where   accpackageid='"+ accpackageid + "'      \n");
			sb.append("     and voucherid IN(" + resultVid + ")         \n");
			sb.append("     and tokenid in (" + ssubjectid + ")            \n");
			sb.append(strDWhere);
			sb.append("     group by voucherid \n");
			
			ps = conn.prepareStatement(sb.toString());
			
			ps.execute();
			ps.close();
	 //-----------------------------------------     
			sb= new StringBuffer();
			sb.append(" select  group_concat(distinct voucherid)  from c_subjectentry where   accpackageid='"+accpackageid+"' \n");		
			sb.append("   and property like '1%' \n");
			sb.append("   and summary like '%"+summary+"%' \n");
			sb.append("   and voucherid IN( \n");
			sb.append("                  "+voucherIds[1]+"      \n");
			sb.append("   ) \n");
		
			sb.append("   and ((dirction= " + cdirection + " and occurvalue<0) or ( dirction=(-1) * "+ cdirection + " and occurvalue>0 and tokenid not in ("+ssubjectid+")))\n");
			sb.append("   and tokenid IN( \n");
			sb.append("                  "+csubjectid+"      \n");
			sb.append("   ) \n");
		
			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();

			String resultVid1 = null;
			if (rs.next()) {
				resultVid1 = rs.getString(1);
			} 
			if(resultVid==null) {
				resultVid1 = "-1";
			}
			rs.close();
			ps.close();
			
//			插入临时表
			sb= new StringBuffer();
			sb.append("insert into t_CorrespondVoucher1  \n");
			sb.append("     select subjectid,Serail,subjectfullname1 ,voucherid,sum(if(dirction=(-1)*"+cdirection+",occurvalue*(-1),occurvalue)) as occurvalue,UnitPrice,UnitName,'"+cdirection+"' as dirction ,    \n");
			sb.append("   if(-1 * " + cdirection + "<0,if(length(creditsubjects)-length(replace(creditsubjects,',',''))>3,1,0),if(length(debitsubjects)-length(replace(debitsubjects,',',''))>3,1,0)) as ifsome1 \n");
			sb.append("  from c_subjectentry \n ");
			sb.append("     where  accpackageid='"+accpackageid+"'       \n");
	        sb.append("     and property like '1%' \n");
	        sb.append("     and voucherid IN( "+resultVid1+"  \n");
	        sb.append("     ) \n");
	        sb.append("   and ((dirction= " + cdirection + " and occurvalue<0) or ( dirction=(-1) * "+ cdirection + " and occurvalue>0 and tokenid not in ("+ssubjectid+")))\n");
	        sb.append("     and tokenid IN(  "+csubjectid+"  ) \n");
	        sb.append("    group by subjectid,voucherid \n");
	        
	        ps = conn.prepareStatement(sb.toString());
			ps.execute();
			ps.close();
			
			sb= new StringBuffer();
			sb.append("insert into t_CorrespondVoucher2 select voucherid,sum(if(dirction=" + cdirection
					+ ",occurvalue*(-1),occurvalue)) as occurvalue ,      \n");
			sb.append("   if(length(debitsubjects)-length(replace(debitsubjects,',',''))>3&&length(creditsubjects)-length(replace(creditsubjects,',',''))>3,1,0) as type1 ,\n");
			sb.append("   if(-1 * " + cdirection + "<0,if(length(debitsubjects)-length(replace(debitsubjects,',',''))>3,1,0),if(length(creditsubjects)-length(replace(creditsubjects,',',''))>3,1,0)) as ifsome2 \n");
			sb.append("     from   \n");
			sb.append("     c_subjectentry  where   accpackageid='"+ accpackageid + "'      \n");
			sb.append("     and voucherid IN(" + resultVid1 + ")         \n");
			sb.append("     and tokenid in (" + ssubjectid + ")            \n");
			sb.append(strDWhere);
			sb.append("     group by voucherid \n");
			System.out.println("t_CorrespondVoucher2 = |"+sb.toString());
			ps = conn.prepareStatement(sb.toString());
			ps.execute();
			ps.close();

			
			String sql = "select ifnull(group_concat(voucherid),-1) from (select voucherid,count(1) as aaa from t_correspondvoucher1 group by voucherid having aaa>1)a";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			rs.next();
			String tempVoucherids = rs.getString(1);
			rs.close();
			ps.close();
			
			sql = "update t_correspondvoucher1 set ifsome1 = '1' where voucherid in ("+tempVoucherids+")";
			ps = conn.prepareStatement(sql);
			ps.execute();
			ps.close();
			
			sql = "select ifnull(group_concat(voucherid),-1) from (select voucherid,count(1) as aaa from t_correspondvoucher2 group by voucherid having aaa>1)a";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			rs.next();
			tempVoucherids = rs.getString(1);
			rs.close();
			ps.close();
			
			sql = "update t_correspondvoucher2 set ifsome2 = '1' where voucherid in ("+tempVoucherids+")";
			ps = conn.prepareStatement(sql);
			ps.execute();
			ps.close();
			
			sb = new StringBuffer();
			sb.append(" select  ("+totle+"-sum(if(ifsome1=1,a_occurvalue,if(ifsome2=1,b_occurvalue,if(b_occurvalue>=a_occurvalue,a_occurvalue,b_occurvalue))))) as sum_a_occurvalue,(100-sum(if(ifsome1=1,a_occurvalue,if(ifsome2=1,b_occurvalue,if(b_occurvalue>=a_occurvalue,a_occurvalue,b_occurvalue))))*100/"
					+ totle
					+ ") as v_occurvalue \n");
			sb.append(" from         \n");
			sb.append("   (         \n");
			sb.append("   select a.occurvalue as a_occurvalue,b.occurvalue as b_occurvalue ,ifsome1,ifsome2  \n");
			sb.append("   from      \n");	
			sb.append("  t_CorrespondVoucher1 a         \n");
			sb.append("   inner join        \n");
			sb.append("   t_CorrespondVoucher2 b         \n");
			sb.append("   on a.voucherid=b.voucherid         \n");
			
			//sb.append("   and a.Serail<>b.Serails        \n");
			 
			sb.append("   where b.type1!='1'        \n");
			sb.append("  ) a        \n");
			sb.append(" where 1=1  \n");
			sb.append(subjectid);
			
			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();
			String sum_a_occurvalue = "";
			String v_occurvalue = "";
			while (rs.next()) {
				sum_a_occurvalue = rs.getString(1);
				v_occurvalue = rs.getString(2);
			}
			rs.close();
			ps.close();
			
			PreparedStatement ps1 = null;
			try {
				sb = new StringBuffer();
				sb.append(" delete from c_correspondlist where charid='"+accpackageid+"_"+subjectname+"_"+sdirection+"_0_"+tempSubjectId+"'   \n"); 
				
				ps1 = conn.prepareStatement(sb.toString());
				ps1.execute();
				
				sb = new StringBuffer();
				sb.append("insert into  c_correspondlist     \n");
			} catch (Exception e) {
				sb = new StringBuffer();
				sb.append("create table c_correspondlist     \n");
			}finally{
				if (ps1!=null)ps1.close();
			}
			
			
			//构造最后的SELECT			
				sb.append(" select a.bbb,ifnull(a.subjectfullname,'多借多贷凭证') as subjectfullname2,a.cdirection,a.sum_b_occurvalue,a.v_occurvalue,a.count_occurvalue,a.sum_UnitPrice,a.UnitName,a.avg_occurvalue,a.max_occurvalue, "
								+"a.sum_a_occurvalue,if(a.sum_b_occurvalue!=a.sum_a_occurvalue,1,0) as opt,opt1,a.voucherids,group_concat(distinct charid) as charid from ( \n");
				sb.append(" select bbb , sum(if(ifsome1=1,a_occurvalue,if(ifsome2=1,b_occurvalue,if(b_occurvalue>=a_occurvalue,a_occurvalue,b_occurvalue)))) as sum_a_occurvalue, sum(if(ifsome1=1,a_occurvalue,if(ifsome2=1,b_occurvalue,if(b_occurvalue>=a_occurvalue,a_occurvalue,b_occurvalue)))) as sum_b_occurvalue,sum(if(ifsome1=1,a_occurvalue,if(ifsome2=1,b_occurvalue,if(b_occurvalue>=a_occurvalue,a_occurvalue,b_occurvalue))))*100/"
								+ totle
								+ " as v_occurvalue,count(b_occurvalue) as count_occurvalue,avg(if(ifsome1=1,a_occurvalue,if(ifsome2=1,b_occurvalue,if(b_occurvalue>=a_occurvalue,a_occurvalue,b_occurvalue)))) as avg_occurvalue,max(if(ifsome1=1,a_occurvalue,if(ifsome2=1,b_occurvalue,if(b_occurvalue>=a_occurvalue,a_occurvalue,b_occurvalue)))) as max_occurvalue,group_concat(voucherid) as voucherids,sum(UnitPrice) as sum_UnitPrice,UnitName,if(dirction=1,'借','贷') as cdirection,opt1,subjectfullname, \n");
				sb.append(" '"+accpackageid+"_"+subjectname+"_"+sdirection+"_0_"+tempSubjectId+"' as charid from         \n");
				sb.append("   (         \n");
				sb.append("   select a.voucherid,a.subjectid,a.subjectfullname1,a.occurvalue as a_occurvalue,b.occurvalue as b_occurvalue ,a.UnitPrice,a.UnitName," +
						" c.subjectid as bbb,c.subjectfullname ,if(a.subjectid!=c.subjectid,1,0) as opt1,dirction,type1,ifsome1,ifsome2,level0  \n");
				sb.append("   from      \n");
	
				sb.append(" t_CorrespondVoucher1 a         \n");
				sb.append("   inner join         \n");
				sb.append("   t_CorrespondVoucher2 b         \n");
				sb.append("   on a.voucherid=b.voucherid          \n");
				
				//sb.append("   and a.Serail<>b.Serails        \n");
				
				sb.append("   inner join         \n");
				sb.append("   (select subjectid,subjectfullname,level0 from c_accpkgsubject where accpackageid='"+accpackageid+"')  c         \n");
				sb.append("   where b.type1!='1'        \n");	 
				sb.append("   and (a.subjectfullname1 = c.SubjectFullName  or a.subjectfullname1  like concat(c.SubjectFullName,'/%') )        \n");	 
				sb.append("  ) a        \n");
				sb.append(" where 1=1 and level0=1\n");
				sb.append(subjectid);
				sb.append("  group by bbb   \n");
				sb.append("  union  \n");
				sb.append("  select '多借多贷凭证','0','"+sum_a_occurvalue+"','"+v_occurvalue+"',count(1) as count_occurvalue,'','',group_concat(voucherid) as voucherids,'','',if("+cdirection+"=1,'借','贷') ,'1','多借多贷凭证','"+accpackageid+"_"+subjectname+"_"+sdirection+"_0_"+tempSubjectId+"'  from t_CorrespondVoucher2 where type1='1'  \n");  
				sb.append("  ) a  \n");
				sb.append("  where 1=1 ${COLUMN_QUERY}\n");
				sb.append(" group by  bbb \n");
				
				org.util.Debug.prtOut("zyq:getNumberArray:Sql=" + sb.toString());
					ps = conn.prepareStatement(sb.toString().replaceAll("\\$\\{COLUMN_QUERY\\}",""));
					ps.executeUpdate();
					ps.close();

					
					sb = new StringBuffer();
				sb.append("select * from  c_correspondlist  where charid='"+accpackageid+"_"+subjectname+"_"+sdirection+"_0_"+tempSubjectId+"' \n");
				org.util.Debug.prtOut("zyq:getNumberArray:Sql=" + sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		return sb.toString();
	}

//	优化后的对应凭证分的改进版
	public String getNumberArray(String cusotmerid, String begyear,
			String begmonth, String endyear, String endmonth,
			String ssubjectid, String sdirection, String csubjectid,
			String cdirection, String voucherids, String summary, String totle,
			String subjectid, String subjectname, String opt) 
			throws Exception{
		
		if (conn==null){
			throw new Exception("数据库连接不能为空");
		}
		String tempType1=null;
		if(opt.equals("1")){
			if(subjectid.equals("多借多贷凭证")){
				subjectid = "";
			}
			tempType1 = "and b.type1='1'";
		}else{
			tempType1 = "and b.type1='0'";
		}
		int tempLength = subjectid.length();
		String tempSubjectid = subjectid;
		if (!"".equals(subjectid) && subjectid != null) {
			subjectid = " and  bbb like '" + subjectid + "%'";
		} else {
			subjectid = "";
		}
		StringBuffer sb = new StringBuffer("");
		String accpackageid = cusotmerid + endyear;
		
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			sb = new StringBuffer();
			sb
					.append("  select level0 from c_accpkgsubject where accpackageid = '"
							+ accpackageid
							+ "' and length(subjectid) = '"
							+ tempLength + "' limit 1");
			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();

			int level0 = 0;
			if (rs.next()) {
				level0 = rs.getInt(1);
			}
			level0++;
			rs.close();
			ps.close();

			sb = new StringBuffer();
			sb.append("insert into c_correspondlist ");
			if(opt.equals("0")){
				sb.append(" select a.bbb,a.subjectfullname as subjectfullname2,a.cdirection,a.sum_b_occurvalue,a.v_occurvalue,a.count_occurvalue,a.sum_UnitPrice,a.UnitName,a.avg_occurvalue,a.max_occurvalue, "
						+"a.sum_a_occurvalue,if(a.sum_b_occurvalue!=a.sum_a_occurvalue,1,0) as opt,opt1,a.voucherids,concat('"+accpackageid+"_"+csubjectid+"_"+sdirection+"_"+opt+"_"+tempSubjectid+"','') as charid from ( \n");
				sb.append(" select bbb , sum(if(type1=0,if(ifsome1=1,a_occurvalue,if(ifsome2=1,b_occurvalue,if(b_occurvalue>=a_occurvalue,a_occurvalue,b_occurvalue))),a_occurvalue)) as sum_a_occurvalue, sum(if(type1=0,if(ifsome1=1,a_occurvalue,if(ifsome2=1,b_occurvalue,if(b_occurvalue>=a_occurvalue,a_occurvalue,b_occurvalue))),b_occurvalue)) as sum_b_occurvalue,sum(if(b_occurvalue>=a_occurvalue,a_occurvalue,b_occurvalue))*100/"
								+ totle
								+ " as v_occurvalue,count(b_occurvalue) as count_occurvalue,avg(if(b_occurvalue>=a_occurvalue,a_occurvalue,b_occurvalue)) as avg_occurvalue,max(if(b_occurvalue>=a_occurvalue,a_occurvalue,b_occurvalue)) as max_occurvalue,group_concat(voucherid) as voucherids,sum(UnitPrice) as sum_UnitPrice,UnitName,if(dirction=1,'借','贷') as cdirection,opt1,subjectfullname \n");
				sb.append(" from         \n");
				sb.append("   (         \n");
				sb.append("   select a.voucherid,a.subjectid,a.subjectfullname1,a.occurvalue as a_occurvalue,b.occurvalue as b_occurvalue ,a.UnitPrice,a.UnitName," +
						" c.subjectid as bbb,c.subjectfullname ,if(a.subjectid!=c.subjectid,1,0) as opt1,dirction,type1,ifsome1,ifsome2,level0  \n");
				sb.append("   from      \n");
	
				sb.append(" t_CorrespondVoucher1 a         \n");
				sb.append("   inner join         \n");
				sb.append("   t_CorrespondVoucher2 b         \n");
				sb.append("   on a.voucherid=b.voucherid         \n");
			//	sb.append("   and a.Serail<>b.Serails        \n");
				sb.append("   inner join         \n");
				sb.append("   (select subjectid,subjectfullname,level0 from c_accpkgsubject where accpackageid='"+accpackageid+"' and level0="+level0+")  c         \n");
				sb.append("   where 1=1 "+tempType1+"        \n");	 
				sb.append("   and (a.subjectfullname1 = c.SubjectFullName  or a.subjectfullname1  like concat(c.SubjectFullName,'/%') )        \n");	 
				sb.append("  ) a        \n");
				sb.append(" where 1=1  \n");
				sb.append(subjectid);
				sb.append("  group by bbb   \n");
				sb.append("  ) a  \n");
				sb.append("  where 1=1 ${COLUMN_QUERY}\n");
				sb.append(" group by  bbb \n");
			}else{
				String sql = "select ifnull(group_concat(voucherid),-1) from t_correspondvoucher2 where type1=1";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();

				String  tempVoucherids = null;
				if (rs.next()) {
					tempVoucherids = rs.getString(1);
				}
				rs.close();
				ps.close();
				
				sb.append("  select b.subjectid,b.subjectfullname as subjectfullname2,a.cdirection,sum(ifnull(a.toccurvalue,0))as sum_b_occurvalue,\n a.v_occurvalue,count(distinct a.voucherid) as count_occurvalue,sum(UnitPrice) as sum_UnitPrice,a.UnitName,a.avg_occurvalue,a.max_occurvalue,a.sum_a_occurvalue,a.opt,if(a.subjectid!=b.subjectid,1,0) as opt1,ifnull(group_concat(distinct a.voucherid),-1) as voucherids,concat('"+accpackageid+"_"+csubjectid+"_"+sdirection+"_"+opt+"_"+tempSubjectid+"','') as charid from ( \n");
				sb.append("  select a.subjectid ,a.SubjectFullName1,if(dirction=1,'借','贷') as cdirection,b.toccurvalue,\n '0' as v_occurvalue,UnitPrice,a.UnitName,'0' as avg_occurvalue,'0' as max_occurvalue, "
						+"'0' as sum_a_occurvalue,'1' as opt,a.voucherid from  \n (select * from t_correspondvoucher1 where voucherid in ("+tempVoucherids+"))a  \n left join \n (select * from c_correspondvoucher where  localsubjectid='"+csubjectid+"' and direction='"+sdirection+"')b \n");
				sb.append("  on a.voucherid = b.voucherid and a.Serail = b.Serail AND a.subjectid = b.subjectid  \n");
				sb.append("  )a inner join  \n");
				sb.append("  (select subjectid,subjectfullname,level0 from c_accpkgsubject where accpackageid='"+accpackageid+"' and level0='"+level0+"' and subjectid like '"+tempSubjectid+"%') b  \n");
				sb.append("  where 1=1 and (a.subjectfullname1 = b.SubjectFullName  or a.subjectfullname1  like concat(b.SubjectFullName,'/%')) \n");
				sb.append("  group by b.subjectid \n");
				
			}
			org.util.Debug.prtOut("zyq:getNumberArray:Sql=" + sb.toString());
			
			
			String sql = " delete from c_correspondlist where charid='"+accpackageid+"_"+csubjectid+"_"+sdirection+"_"+opt+"_"+tempSubjectid+"' \n"; 
			String forCheck = "0";
			ps = conn.prepareStatement(sql);
			ps.execute();
			ps.close();
			
			if(forCheck.equals("0")){
				ps = conn.prepareStatement(sb.toString().replaceAll("\\$\\{COLUMN_QUERY\\}",""));
				ps.executeUpdate();
				ps.close();
			}
			sb = new StringBuffer();
			sb.append("select * from  c_correspondlist  where  charid='"+accpackageid+"_"+csubjectid+"_"+sdirection+"_"+opt+"_"+tempSubjectid+"'   \n");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		return sb.toString();
	}
	
	
	//优化后的对应凭证分的改进版
	public static String getTotle(String cusotmerid, String begyear,
			String begmonth, String endyear, String endmonth,
			String ssubjectid, String csubjectid, String cdirection,
			String voucherids, String summary,String reversedirction) {
		String sum = "0";
		StringBuffer sb = new StringBuffer("");
		int startTime = Integer.parseInt(endyear)*12+Integer.parseInt(begmonth);
		int endTime = Integer.parseInt(endyear)*12+Integer.parseInt(endmonth);
		String tempStr = "";
		if(cdirection.equals("1")){
			tempStr = "DebitOcc";
		}else{
			tempStr = "CreditOcc";
		}

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			sb.append("select sum("+tempStr+") from c_account where tokenid in("+ssubjectid+") and SubYearMonth*12+SubMonth>="+startTime+" and SubYearMonth*12+SubMonth<="+endTime+"");
			conn = new DBConnect().getConnect(cusotmerid);
			ps = conn.prepareStatement(sb.toString());
			rs = ps.executeQuery();
			if (rs.next()) {
				sum = rs.getString(1);
			}
			rs.close();
			ps.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		return sum;

	}

	//优化后的对应凭证分的打印sql
	public static String getPrintSql(String cusotmerid, String begyear,
			String begmonth, String endyear, String endmonth,
			String ssubjectid, String sdirection, String csubjectid,
			String cdirection, String voucherids) {
		StringBuffer sb = new StringBuffer("");
		String accpackageid = cusotmerid + endyear;
//		String sql = "select * from "

		System.out.println("zyq:getPrintSql:" + sb.toString());
		return sb.toString();
	}

	//优化后的对应凭证分的打印sql
	public static String getSubjectStlye(String subjectid) {

		if (subjectid.indexOf(".") > -1) {

			subjectid = subjectid.split(".")[0];

		} else {

			if ((subjectid.length() - 3) % 2 == 0) {

				subjectid = "aaa";
			} else {
				subjectid = "aaaa";
			}
		}

		return subjectid.length() + "";

	}

	/**
	 * 得到对应科目
	 */
	public String getSubjects(String direction,String cvc_begyear,String cvc_begmonth,String cvc_endmonth,String ssID2)throws Exception{
		String sql = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String Subjects = "";
			int i = 0;
				
			if ("1".equals(direction)){

			  sql = "select distinct subjectid,subjectname1 from ("
				+" select  a.subjectid,a.subjectname1 \n"
				+ " from c_subjectentry a, \n"
				+ " ( \n"
				+ " select  distinct voucherid,subjectid  from  \n"
				+ " c_subjectentry  \n"
				+ " where vchdate>='"+cvc_begyear+"-"+cvc_begmonth+"-01'  \n"
				+ " and vchdate<='"+cvc_begyear+"-"+cvc_endmonth+"-31'  \n"
				+ " and subjectid like '"+ssID2+"%' \n"
				+ " and dirction >0 and occurvalue>0 \n"
				+ " )b \n"
				+ " where ((a.dirction<0  and  a.occurvalue>0)or (a.dirction>0 and  a.occurvalue<0 and a.subjectid not like '"+ssID2+"%')) \n"
				+ " and a.voucherid=b.voucherid   \n"
				+ "  "
				+ " union all "
				+ " select distinct a.subjectid,a.subjectname1 \n"
				+ " from c_subjectentry a, \n"
				+ " ( \n"
				+ " select  distinct voucherid,subjectid  from  \n"
				+ " c_subjectentry  \n"
				+ " where vchdate>='"+cvc_begyear+"-"+cvc_begmonth+"-01'  \n"
				+ " and vchdate<='"+cvc_begyear+"-"+cvc_endmonth+"-31'  \n"
				+ " and subjectid like '"+ssID2+"%' \n"
				+ " and dirction >0 and occurvalue<0 \n"
				+ " )b \n"
				+ " where ((a.dirction<0 and   a.occurvalue<0)or (a.dirction>0 and  a.occurvalue>0 and a.subjectid not like '"+ssID2+"%')) \n"
				+ " and a.voucherid=b.voucherid   \n"
				+ " order by subjectid )a";
				
			  }else{
				  sql = "select distinct subjectid,subjectname1 from ("
			    +" select  a.subjectid,a.subjectname1 \n"
				+ " from c_subjectentry a, \n"
				+ " ( \n"
				+ " select  distinct voucherid,subjectid   from  \n"
				+ " c_subjectentry  \n"
				+ " where vchdate>='"+cvc_begyear+"-"+cvc_begmonth+"-01'  \n"
				+ " and vchdate<='"+cvc_begyear+"-"+cvc_endmonth+"-31'  \n"
				+ " and subjectid like '"+ssID2+"%' \n"
				+ " and dirction <0  and occurvalue>0 \n"
				+ " )b \n"
				+ " where ((a.dirction>0 and  a.occurvalue>0)or (a.dirction<0 and  a.occurvalue<0 and a.subjectid not like '"+ssID2+"%')) \n"
				+ " and a.voucherid=b.voucherid  \n"
				+ "  "
				+ " union all "
				+ " select  a.subjectid,a.subjectname1 \n"
				+ " from c_subjectentry a, \n"
				+ " ( \n"
				+ " select  distinct voucherid,subjectid  from  \n"
				+ " c_subjectentry  \n"
				+ " where vchdate>='"+cvc_begyear+"-"+cvc_begmonth+"-01'  \n"
				+ " and vchdate<='"+cvc_begyear+"-"+cvc_endmonth+"-31'  \n"
				+ " and subjectid like '"+ssID2+"%' \n"
				+ " and dirction <0 and occurvalue<0 \n"
				+ " )b \n"
				+ " where ((a.dirction>0 and   a.occurvalue<0)or (a.dirction<0 and  a.occurvalue>0 and a.subjectid not like '"+ssID2+"%')) \n"
				+ " and a.voucherid=b.voucherid   \n"
				+ " order by subjectid )a";
				
			}
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				Subjects += rs.getString(1) + "|";
			}
			
			return Subjects;
		} catch (Exception e) {
			System.out.println("error sql:" + sql);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//优化后的对应凭证分的打印sql
	public static void qqqq() {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ASFuntion aa = new ASFuntion();

		DBConnect dbc = new DBConnect();
		try {

			String sql = ""

					+ " select a.*,b.subjectfullname2 from ( \n"
					+ " 		  select bbb , sum(occurvalue) as sum_occurvalue,count(occurvalue) as count_occurvalue,avg(occurvalue) as avg_occurvalue,max(occurvalue) as max_occurvalue,group_concat(voucherid) as voucherids,sum(UnitPrice) as sum_UnitPrice,UnitName  \n"
					+ " 		  from          \n"
					+ " 	  (          \n"
					+ " 		    select a.voucherid,subjectid,subjectfullname1,if(abs(a.occurvalue) < abs(b.occurvalue),a.occurvalue,b.occurvalue) as occurvalue ,UnitPrice,UnitName,subString(subjectid,1,8) as bbb     \n"
					+ " 		    from       \n"
					+ " 	    (          \n"
					+ " 	    -- 每张凭证的显示科目         \n"
					+ " 	      select subjectid,subjectfullname1 ,voucherid,sum(occurvalue) as occurvalue,UnitPrice,UnitName     from c_subjectentry    \n"
					+ " 	      where (accpackageid>='1000022006' and accpackageid<='1000022008')  \n"
					+ " 		      and property like '1%'  \n"
					+ " 	      and voucherid IN(  \n"
					+ " 		                     1276,1236,1195,1069,1154,2452,2397,2425,1966,1930,1894,1858,1822,1786,934,2478,602,842,794,746,698,650,554,506,458,410,362,311,260,207,152,95,36,1152,456,205,309,258,360,93,150,34,1750,1714,1678,1606,1642,2002,1570,1534,1498,1462,1426,1389,1352,1113,1315,1277,1237,1196,1155,1070,1026,980,1460,1424,1387,1350,1313,1275,1235,1194,1153,1111,2036,1068,2202,2169,2136,2103,2069,2034,1998,1962,1926,1890,1854,1818,1782,1746,1710,1674,1638,1602,1566,1530,1528,1564,1456,1492,1420,1346,1309,1271,1231,1190,1149,1107,1064,1020,974,928,882,836,788,833,1525,1489,1453,1417,1380,1343,1306,1268,1146,1104,1061,1017,1957,925,879,2710,785,737,689,251,641,593,545,497,449,1886,1850,2476,1922,2501,1742,1706,1670,1634,1598,1562,1526,1490,1454,1418,1381,1344,1307,1269,1229,1188,1147,1105,1958,2638,2729,2594,2030,2572,2525,2549,2616,1778,2450,2422,2329,2065,2393,2362,2296,2264,2231,2198,2165,2132,2099,1994,2711,1422,1385,1348,1311,1273,1233,1192,1151,1109,1066,1022,976,930,884,838,790,742,694,646,598,2814,2797,145,2693,2780,2763,738,2746,2675,1814,2657,595,547,451,403,355,304,253,200,88,29,254,201,146,89,30,2330,2297,2265,2363,2232,2199,2166,2133,2100,2066,2031,1995,692,740,548,596,500,452,404,1063,356,1062,1018,972,926,880,834,690,642,594,546,498,450,402,354,303,252,199,971,144,87,1455,1148,1230,1189,1106,973,787,927,881,1019,835,739,691,643,2167,2134,2101,2067,2032,1996,1960,202,1924,1888,1852,1816,1780,1744,1708,1672,1600,1636,2197,28,3005,2991,2392,2977,2846,2963,2949,2935,2921,2907,2892,2877,1187,2830,2813,2796,2779,2762,2745,693,645,597,255,1457,501,453,405,357,306,1383,2423,2394,2364,31,2331,147,90,2266,2298,2200,2233,2138,2105,2072,2037,1965,2001,1929,1893,1857,1821,1785,1749,1713,1677,1641,1605,1569,2130,2097,2063,2028,1992,1884,1920,1956,1848,1812,1776,1740,1704,1668,1267,1632,1596,1560,1524,1488,1452,1416,1227,1379,1342,1305,1186,1145,1103,1060,1016,970,1851,1887,1923,1959,1815,1779,1743,1707,1671,1635,1599,1563,1527,1491,499,1419,1382,1345,1308,1270,2728,2862,2692,2674,2656,2637,2615,2548,2593,2571,2524,2500,2475,2449,2421,2361,2328,2263,2230,2164,2131,2098,2064,2029,1993,1921,1885,1849,1813,1777,1741,1705,1669,1633,1597,1561,401,353,302,400,198,143,86,27,2391,2360,2327,2295,2262,2229,2196,2163,496,544,352,301,250,142,85,26,2948,3119,3089,2228,3099,3079,3069,3059,3049,3039,783,3028,1955,3017,3004,2990,924,878,832,784,736,688,640,592,448,2976,2962,2934,2920,2906,2891,2876,2861,2845,2829,2812,2795,2778,2761,2744,2727,2709,2691,2294,2673,2655,2636,2592,2570,3109,2547,2523,2499,2474,2448,2420,2390,2359,2326,2261,2195,2162,2129,2096,2062,2027,1266,1991,1919,1883,1847,1811,1775,1739,1703,84,1631,1595,1559,1523,1487,1451,1415,1378,1341,1304,1226,1185,1144,1102,1059,1015,969,923,877,831,735,687,639,591,543,495,447,399,300,249,196,141,1667,25,1314,1351,1388,1461,1497,1533,1425,2038,2073,1712,1748,1784,1820,1856,1892,1928,1964,2000,2071,408,2395,2365,33,92,149,204,361,35,94,310,206,151,259,47,409,457,505,553,601,649,697,745,793,841,887,933,1999,2035,2424,2070,2104,2137,2170,2203,2236,2269,2301,2550,503,2334,2366,2396,2451,2477,2502,2526,504,552,600,648,696,744,792,840,886,932,978,1024,1496,1532,1676,1568,1604,1640,1274,1312,1349,1386,1423,1459,1495,1675,1531,1567,1711,1603,1639,1747,1783,1819,1855,1891,1927,1963,257,308,359,407,455,551,599,647,695,743,791,839,885,931,977,1023,1067,1110,1193,1234,1458,1494,979,1025,1112,2171,2204,2237,2270,2302,2335,2367,502,550,2235,2268,2333,2300,2168,2201,2234,2267,2299,1529,2332,256,307,32,91,148,203,358,406,454,1493,1565,1601,1709,1673,1421,1817,1637,1745,1781,1853,1889,1925,1961,1997,2033,2135,2068,2102,741,789,837,883,929,975,1272,1347,1065,1108,1150,1021,1232,1191,1310,1384,549,2106,2139,2172,2205,2238,2271,2303,2336,2368,2398,2426,2453,2479,2503,2573,2639,2551,2527,888,2595,2617,6109,4694,6050,5637,5731,5828,5684,5779,5541,5935,5881,5991,5493,5352,5305,5164,4656,5072,5446,5258,5399,5211,4853,5118,5027,4983,4939,4895,4812,4619,4733,5589,4582,4545,4508,4472,4436,4400,4364,4329,4294,4260,4227,4164,4195,4772,4132,4100,4363,4328,3897,3871,5930,3847,5536,4223,5631,5583,4069,4040,4011,3982,3955,3928,5730,3901,3874,4467,4431,4395,4359,4324,4289,4256,4651,4191,5679,4160,4128,4096,4065,4036,3978,3951,3924,4007,4396,4360,3802,4290,4257,4224,4192,4161,4129,4097,4066,4037,4008,3979,4580,4506,4470,4434,4398,4362,4327,4292,4259,4226,3900,5300,5253,5206,5159,5113,5067,5022,4978,4934,4890,4848,4807,4767,5986,4728,5394,4689,4614,4577,4540,4503,4163,4131,4099,4068,4039,4010,3954,3981,3927,4194,4810,4543,6104,6045,5876,5726,5823,5774,5584,5632,5488,5441,5347,5161,4130,4098,4038,4009,5349,4653,3980,3953,3926,4469,3899,3873,3849,3826,4067,3803,3952,3898,3925,3872,3825,4325,5537,5489,3848,5442,5395,5348,5301,5254,5207,5160,5114,5068,5023,4979,4935,4891,4849,4808,4768,4729,4504,4690,4652,4615,4578,4541,4468,4432,3782,6105,6046,5987,5931,5877,5824,5775,5727,5680,5633,5585,6108,6049,5990,5934,5880,5778,5683,5636,5588,5540,5492,5445,4254,4221,4189,4158,4126,4094,4063,4005,4034,3976,3949,3922,3895,3870,3846,3823,3801,3781,3762,3743,3705,3724,3686,3667,3648,3629,3610,3591,3573,3556,3539,3524,3509,3494,3479,3464,3449,3434,3420,5586,5538,5443,5396,5302,5255,5208,5115,5069,5024,4980,4936,4892,4850,4809,4769,4730,4691,4616,4579,4542,4505,4433,4397,4361,4326,4291,4258,4225,4193,4162,4293,6107,6048,5989,5682,5635,5879,5826,5777,5933,5729,5490,6106,6047,5988,5932,5878,5825,5776,5728,5681,5634,5587,5539,5491,5444,5397,5350,5303,5256,4937,5209,5162,5116,5070,5025,4981,4893,4851,4770,4731,4692,4654,4617,5398,5351,5304,5257,5210,5163,5117,5071,5026,5827,4982,4938,4894,4852,4618,4771,4732,4693,4811,4655,4471,4581,4544,4507,4435,4399,4843,4802,4762,4684,4646,4609,4572,4498,4462,4426,5721,4390,4354,4319,4284,4251,4218,4186,5247,5200,5153,5107,5061,5016,4972,4928,4884,4842,4801,4761,4722,4683,4645,4608,4571,4534,4497,4461,3721,4425,4389,4353,4318,4283,4250,4185,4217,4154,4122,4060,4031,4002,3973,3946,3919,3892,3843,3867,3820,3798,3778,3759,3740,4127,4095,4064,4035,4006,3977,3950,5393,4159,3923,6102,5984,5928,5874,3334,3345,3324,3315,3306,3298,3292,3286,3280,3274,3269,3264,3261,3258,3252,3255,3250,3248,3246,3244,3240,3238,6134,6136,6135,3242,6137,3570,6138,4123,4723,4885,3491,6097,6039,5980,5924,5870,5817,5768,5720,5673,5626,5578,5530,5482,5435,5388,5341,5294,6139,5769,5674,5627,4155,5579,5531,5483,5436,5342,5295,5248,5201,5154,5108,5062,5017,4973,4090,3702,3683,3664,3645,3626,3607,3588,3553,3536,3521,3506,3476,3461,3446,3431,3417,3404,3392,3380,3368,3356,3703,3589,3571,3447,3554,3537,3522,3507,3492,3477,3462,5675,3418,3432,3405,6099,6040,5981,5925,5871,5818,5770,5722,5628,5580,5532,5484,5437,5390,5343,5296,5249,5202,5155,5109,4156,5018,5063,4974,4930,4886,4803,4252,4724,4763,4685,4647,4610,4573,4536,4499,4463,4427,4391,4355,4320,4285,4219,4187,3608,4124,4092,4061,4032,3920,4003,3974,3947,3893,3868,3844,3821,3799,3779,3760,3741,3722,3684,4844,3665,3646,3627,3293,3287,3281,3275,3270,5110,5156,3265,6100,6041,5982,5926,5872,5819,3590,3572,3555,3538,3523,3508,3493,3478,3463,3448,3433,3419,3406,3393,3381,3369,3822,3357,3346,3335,3325,3316,3299,3307,4611,4574,4537,4500,4464,4428,4392,4356,4286,3894,4220,4188,4157,4125,4093,4062,4033,4004,3975,3948,3921,3647,3869,3845,3780,3761,3742,3723,3704,3685,3666,4321,3800,3628,3609,5771,6101,6042,5983,5927,5873,5820,5723,5676,5629,5581,5533,5485,5438,5391,5344,5297,5250,5203,5064,5019,4975,4931,4887,4845,4804,4764,4725,4253,4686,4648,4649,4612,4575,4538,4501,4465,4429,4393,4357,4322,4287,4929,4535,4091,6043,5821,5772,5724,5677,5630,5439,5392,5345,3896,5534,5486,5298,5251,5204,5157,5111,5065,5020,4976,4932,4888,4846,4805,4726,4765,4687,5773,3407,3394,3382,3358,3347,3336,3326,3317,3308,3370,5021,5066,4977,4889,4806,4847,5205,4727,4688,4613,4933,4650,4576,4539,4502,4466,4430,4394,4358,4323,4288,4255,4222,4190,3824,6103,6044,5985,5929,5875,5822,5725,5487,5535,5112,5299,5678,5252,4766,5440,5346,5158,6778,6364,6352,7295,7267,7239,7212,7186,7162,7135,7108,7082,7057,6341,7406,7471,7377,7346,7319,7292,7266,7240,7213,7187,7159,7134,7109,7083,7056,7030,7005,6979,6955,6930,6908,6884,6862,6841,6818,6798,6304,7436,6507,6492,6463,6477,6400,6448,6435,6423,6411,6387,6375,6478,6462,6449,6436,6424,6412,6399,6313,6331,6322,7031,7004,6956,6980,6932,6907,6887,6865,6842,6820,6799,6779,6759,6743,6726,6709,6692,6674,6657,6639,6621,6604,6586,6570,6555,6538,6522,6493,6508,7271,7244,7216,7166,7191,7139,7113,7087,7035,6984,7009,6960,6912,6936,6890,6846,6868,6824,6803,6783,6747,6730,7061,6256,6713,6696,6678,6661,6643,6625,6608,6542,6511,6481,6496,6467,6452,6439,6427,6415,6404,6391,6379,6368,6355,6526,6344,6333,6323,6314,6305,6296,6288,6280,7474,7441,7411,7353,7381,6590,7325,7298,6263,7475,7440,7380,7410,7350,7323,7297,7270,7243,7217,7190,7163,7138,7112,6574,6559,6763,6465,6450,6437,6425,6413,6402,6389,7189,6377,6366,6272,7161,7438,7408,7379,7321,7348,7294,7268,7241,7215,7188,7136,7111,7084,7058,6957,7032,7007,6981,6933,6910,6886,6864,6509,6479,6494,6843,6332,6821,6800,6762,6780,6744,6727,6710,6693,6677,6658,6640,6622,6605,6589,6571,6554,6539,6523,6160,6153,6157,6150,6146,6148,6144,6731,6142,6141,6140,6363,6353,6342,6376,7470,7437,7407,7376,7349,7322,6801,6889,6867,6844,6822,6781,6761,6745,6728,6711,6694,6676,6659,6641,6623,6606,6588,6572,6557,6540,6524,6388,7351,7324,7296,7269,7242,7214,7164,7137,7110,7085,7059,7033,7006,6982,6958,6934,6909,7472,7439,7409,7378,6265,6453,7133,6440,6429,6416,6403,6392,6380,6356,6367,6345,6335,6324,6315,6306,6297,6289,6281,6257,6249,6243,6237,6232,6225,6213,6207,6219,6200,6196,6189,6183,6177,6171,6166,7476,7443,7412,7382,7326,7352,7299,7273,7245,7218,7192,7165,7140,7114,7088,7063,7036,7010,6985,6961,6937,6913,6869,6891,6847,6825,6804,6784,6765,6748,6714,6681,6662,6645,6626,6273,6697,6609,6592,6558,6575,6527,6543,6512,6497,6466,6482,7060,7086,7034,6983,7008,6959,6935,6911,6888,6866,6845,6823,6802,6782,6764,6746,6729,6712,6679,6660,6695,6642,6624,6607,6573,6591,6556,6541,6525,6510,6495,6464,6480,6451,6438,6426,6414,6401,6390,6378,6365,6354,6343  \n"
					+ " 		      )  \n"
					+ " 		      and (dirction= 1 or ( dirction=(-1) * 1 and occurvalue<0 )) \n"
					+ " 		      and tokenid IN(  \n"
					+ " 		                     '库存现金/现金RMB','银行存款/澳新银行RMB','银行存款/工商行第一支行','银行存款/工商银行七宝支行','银行存款/工行苏州吴中支行','银行存款/澳新银行USD','应收账款','其他应收款/单位其他应收款','预付款项','包装物/其他','分期收款发出商品','待摊费用','固定资产/机器设备','固定资产/办公设备','固定资产/电子设备','在建工程/机器设备','其他长期资产','短期借款/美元','应付账款','应付职工薪酬/外方','应付职工薪酬/中方','应交税费/进项税额','应交税费/未交增值税','其他应交款','其他应付款/其他应付款-个人','其他应付款/其他应付款-单位','预提费用','制造费用/福利费','制造费用/差旅费','制造费用/水电费','制造费用/办公费','制造费用/物料消耗','制造费用/修理费','制造费用/其他','制造费用/通讯费','制造费用/制版费','制造费用/劳防用品','制造费用/培训费','制造费用/测试费','其他业务成本','销售费用/差旅费','销售费用/广告费','销售费用/销售服务费','销售费用/运杂费','销售费用/办公费','销售费用/交际费','销售费用/其他','销售费用/福利费','销售费用/水电费','销售费用/试验费','销售费用/开发费','销售费用/通讯费','销售费用/快递费','管理费用/办公费','管理费用/差旅费','管理费用/租赁费','管理费用/水电费','管理费用/修理费','管理费用/财产保险费','管理费用/环境保护费','管理费用/职工培训费','管理费用/交际费','管理费用/其他','管理费用/福利费','管理费用/公积金','管理费用/审计费','管理费用/印花税','管理费用/维护费','管理费用/通讯费','管理费用/快递费','管理费用/年检咨询费','管理费用/招聘费','财务费用/手续费','财务费用/汇兑损益','待摊费用/上海马克','待摊费用/北京代表处','待摊费用/其他','制造费用/公积金','销售费用/公积金','销售费用/社会保险费','管理费用/光纤摊销','银行存款/建行上海卢湾支行','制造费用/社会保险费','管理费用/补偿金','管理费用/律师费','管理费用/社会保险费','应交税费/应交个人所得税','待摊费用/保险费','待摊费用/苏州三威','预提费用/电费','预提费用/销售服务费','预提费用/借款利息','待摊费用/光纤费','预提费用/审计费','预提费用/中华村房租','管理费用/研发费'       \n"
					+ " 	      )        \n"
					+ " 	      group by subjectid,voucherid   \n"
					+ " 	    ) a          \n"
					+ " 	    inner join         \n"
					+ " 	    (          \n"
					+ " 	    --  不显示的科目          \n"
					+ " 	      select voucherid,sum(occurvalue) as occurvalue  \n"
					+ " 	      from    \n"
					+ " 	      c_subjectentry  where (accpackageid>='1000022006' and  accpackageid<='1000022008')   \n"
					+ " 	      and voucherid IN(1276,1236,1195,1069,1154,2452,2397,2425,1966,1930,1894,1858,1822,1786,934,2478,602,842,794,746,698,650,554,506,458,410,362,311,260,207,152,95,36,1152,456,205,309,258,360,93,150,34,1750,1714,1678,1606,1642,2002,1570,1534,1498,1462,1426,1389,1352,1113,1315,1277,1237,1196,1155,1070,1026,980,1460,1424,1387,1350,1313,1275,1235,1194,1153,1111,2036,1068,2202,2169,2136,2103,2069,2034,1998,1962,1926,1890,1854,1818,1782,1746,1710,1674,1638,1602,1566,1530,1528,1564,1456,1492,1420,1346,1309,1271,1231,1190,1149,1107,1064,1020,974,928,882,836,788,833,1525,1489,1453,1417,1380,1343,1306,1268,1146,1104,1061,1017,1957,925,879,2710,785,737,689,251,641,593,545,497,449,1886,1850,2476,1922,2501,1742,1706,1670,1634,1598,1562,1526,1490,1454,1418,1381,1344,1307,1269,1229,1188,1147,1105,1958,2638,2729,2594,2030,2572,2525,2549,2616,1778,2450,2422,2329,2065,2393,2362,2296,2264,2231,2198,2165,2132,2099,1994,2711,1422,1385,1348,1311,1273,1233,1192,1151,1109,1066,1022,976,930,884,838,790,742,694,646,598,2814,2797,145,2693,2780,2763,738,2746,2675,1814,2657,595,547,451,403,355,304,253,200,88,29,254,201,146,89,30,2330,2297,2265,2363,2232,2199,2166,2133,2100,2066,2031,1995,692,740,548,596,500,452,404,1063,356,1062,1018,972,926,880,834,690,642,594,546,498,450,402,354,303,252,199,971,144,87,1455,1148,1230,1189,1106,973,787,927,881,1019,835,739,691,643,2167,2134,2101,2067,2032,1996,1960,202,1924,1888,1852,1816,1780,1744,1708,1672,1600,1636,2197,28,3005,2991,2392,2977,2846,2963,2949,2935,2921,2907,2892,2877,1187,2830,2813,2796,2779,2762,2745,693,645,597,255,1457,501,453,405,357,306,1383,2423,2394,2364,31,2331,147,90,2266,2298,2200,2233,2138,2105,2072,2037,1965,2001,1929,1893,1857,1821,1785,1749,1713,1677,1641,1605,1569,2130,2097,2063,2028,1992,1884,1920,1956,1848,1812,1776,1740,1704,1668,1267,1632,1596,1560,1524,1488,1452,1416,1227,1379,1342,1305,1186,1145,1103,1060,1016,970,1851,1887,1923,1959,1815,1779,1743,1707,1671,1635,1599,1563,1527,1491,499,1419,1382,1345,1308,1270,2728,2862,2692,2674,2656,2637,2615,2548,2593,2571,2524,2500,2475,2449,2421,2361,2328,2263,2230,2164,2131,2098,2064,2029,1993,1921,1885,1849,1813,1777,1741,1705,1669,1633,1597,1561,401,353,302,400,198,143,86,27,2391,2360,2327,2295,2262,2229,2196,2163,496,544,352,301,250,142,85,26,2948,3119,3089,2228,3099,3079,3069,3059,3049,3039,783,3028,1955,3017,3004,2990,924,878,832,784,736,688,640,592,448,2976,2962,2934,2920,2906,2891,2876,2861,2845,2829,2812,2795,2778,2761,2744,2727,2709,2691,2294,2673,2655,2636,2592,2570,3109,2547,2523,2499,2474,2448,2420,2390,2359,2326,2261,2195,2162,2129,2096,2062,2027,1266,1991,1919,1883,1847,1811,1775,1739,1703,84,1631,1595,1559,1523,1487,1451,1415,1378,1341,1304,1226,1185,1144,1102,1059,1015,969,923,877,831,735,687,639,591,543,495,447,399,300,249,196,141,1667,25,1314,1351,1388,1461,1497,1533,1425,2038,2073,1712,1748,1784,1820,1856,1892,1928,1964,2000,2071,408,2395,2365,33,92,149,204,361,35,94,310,206,151,259,47,409,457,505,553,601,649,697,745,793,841,887,933,1999,2035,2424,2070,2104,2137,2170,2203,2236,2269,2301,2550,503,2334,2366,2396,2451,2477,2502,2526,504,552,600,648,696,744,792,840,886,932,978,1024,1496,1532,1676,1568,1604,1640,1274,1312,1349,1386,1423,1459,1495,1675,1531,1567,1711,1603,1639,1747,1783,1819,1855,1891,1927,1963,257,308,359,407,455,551,599,647,695,743,791,839,885,931,977,1023,1067,1110,1193,1234,1458,1494,979,1025,1112,2171,2204,2237,2270,2302,2335,2367,502,550,2235,2268,2333,2300,2168,2201,2234,2267,2299,1529,2332,256,307,32,91,148,203,358,406,454,1493,1565,1601,1709,1673,1421,1817,1637,1745,1781,1853,1889,1925,1961,1997,2033,2135,2068,2102,741,789,837,883,929,975,1272,1347,1065,1108,1150,1021,1232,1191,1310,1384,549,2106,2139,2172,2205,2238,2271,2303,2336,2368,2398,2426,2453,2479,2503,2573,2639,2551,2527,888,2595,2617,6109,4694,6050,5637,5731,5828,5684,5779,5541,5935,5881,5991,5493,5352,5305,5164,4656,5072,5446,5258,5399,5211,4853,5118,5027,4983,4939,4895,4812,4619,4733,5589,4582,4545,4508,4472,4436,4400,4364,4329,4294,4260,4227,4164,4195,4772,4132,4100,4363,4328,3897,3871,5930,3847,5536,4223,5631,5583,4069,4040,4011,3982,3955,3928,5730,3901,3874,4467,4431,4395,4359,4324,4289,4256,4651,4191,5679,4160,4128,4096,4065,4036,3978,3951,3924,4007,4396,4360,3802,4290,4257,4224,4192,4161,4129,4097,4066,4037,4008,3979,4580,4506,4470,4434,4398,4362,4327,4292,4259,4226,3900,5300,5253,5206,5159,5113,5067,5022,4978,4934,4890,4848,4807,4767,5986,4728,5394,4689,4614,4577,4540,4503,4163,4131,4099,4068,4039,4010,3954,3981,3927,4194,4810,4543,6104,6045,5876,5726,5823,5774,5584,5632,5488,5441,5347,5161,4130,4098,4038,4009,5349,4653,3980,3953,3926,4469,3899,3873,3849,3826,4067,3803,3952,3898,3925,3872,3825,4325,5537,5489,3848,5442,5395,5348,5301,5254,5207,5160,5114,5068,5023,4979,4935,4891,4849,4808,4768,4729,4504,4690,4652,4615,4578,4541,4468,4432,3782,6105,6046,5987,5931,5877,5824,5775,5727,5680,5633,5585,6108,6049,5990,5934,5880,5778,5683,5636,5588,5540,5492,5445,4254,4221,4189,4158,4126,4094,4063,4005,4034,3976,3949,3922,3895,3870,3846,3823,3801,3781,3762,3743,3705,3724,3686,3667,3648,3629,3610,3591,3573,3556,3539,3524,3509,3494,3479,3464,3449,3434,3420,5586,5538,5443,5396,5302,5255,5208,5115,5069,5024,4980,4936,4892,4850,4809,4769,4730,4691,4616,4579,4542,4505,4433,4397,4361,4326,4291,4258,4225,4193,4162,4293,6107,6048,5989,5682,5635,5879,5826,5777,5933,5729,5490,6106,6047,5988,5932,5878,5825,5776,5728,5681,5634,5587,5539,5491,5444,5397,5350,5303,5256,4937,5209,5162,5116,5070,5025,4981,4893,4851,4770,4731,4692,4654,4617,5398,5351,5304,5257,5210,5163,5117,5071,5026,5827,4982,4938,4894,4852,4618,4771,4732,4693,4811,4655,4471,4581,4544,4507,4435,4399,4843,4802,4762,4684,4646,4609,4572,4498,4462,4426,5721,4390,4354,4319,4284,4251,4218,4186,5247,5200,5153,5107,5061,5016,4972,4928,4884,4842,4801,4761,4722,4683,4645,4608,4571,4534,4497,4461,3721,4425,4389,4353,4318,4283,4250,4185,4217,4154,4122,4060,4031,4002,3973,3946,3919,3892,3843,3867,3820,3798,3778,3759,3740,4127,4095,4064,4035,4006,3977,3950,5393,4159,3923,6102,5984,5928,5874,3334,3345,3324,3315,3306,3298,3292,3286,3280,3274,3269,3264,3261,3258,3252,3255,3250,3248,3246,3244,3240,3238,6134,6136,6135,3242,6137,3570,6138,4123,4723,4885,3491,6097,6039,5980,5924,5870,5817,5768,5720,5673,5626,5578,5530,5482,5435,5388,5341,5294,6139,5769,5674,5627,4155,5579,5531,5483,5436,5342,5295,5248,5201,5154,5108,5062,5017,4973,4090,3702,3683,3664,3645,3626,3607,3588,3553,3536,3521,3506,3476,3461,3446,3431,3417,3404,3392,3380,3368,3356,3703,3589,3571,3447,3554,3537,3522,3507,3492,3477,3462,5675,3418,3432,3405,6099,6040,5981,5925,5871,5818,5770,5722,5628,5580,5532,5484,5437,5390,5343,5296,5249,5202,5155,5109,4156,5018,5063,4974,4930,4886,4803,4252,4724,4763,4685,4647,4610,4573,4536,4499,4463,4427,4391,4355,4320,4285,4219,4187,3608,4124,4092,4061,4032,3920,4003,3974,3947,3893,3868,3844,3821,3799,3779,3760,3741,3722,3684,4844,3665,3646,3627,3293,3287,3281,3275,3270,5110,5156,3265,6100,6041,5982,5926,5872,5819,3590,3572,3555,3538,3523,3508,3493,3478,3463,3448,3433,3419,3406,3393,3381,3369,3822,3357,3346,3335,3325,3316,3299,3307,4611,4574,4537,4500,4464,4428,4392,4356,4286,3894,4220,4188,4157,4125,4093,4062,4033,4004,3975,3948,3921,3647,3869,3845,3780,3761,3742,3723,3704,3685,3666,4321,3800,3628,3609,5771,6101,6042,5983,5927,5873,5820,5723,5676,5629,5581,5533,5485,5438,5391,5344,5297,5250,5203,5064,5019,4975,4931,4887,4845,4804,4764,4725,4253,4686,4648,4649,4612,4575,4538,4501,4465,4429,4393,4357,4322,4287,4929,4535,4091,6043,5821,5772,5724,5677,5630,5439,5392,5345,3896,5534,5486,5298,5251,5204,5157,5111,5065,5020,4976,4932,4888,4846,4805,4726,4765,4687,5773,3407,3394,3382,3358,3347,3336,3326,3317,3308,3370,5021,5066,4977,4889,4806,4847,5205,4727,4688,4613,4933,4650,4576,4539,4502,4466,4430,4394,4358,4323,4288,4255,4222,4190,3824,6103,6044,5985,5929,5875,5822,5725,5487,5535,5112,5299,5678,5252,4766,5440,5346,5158,6778,6364,6352,7295,7267,7239,7212,7186,7162,7135,7108,7082,7057,6341,7406,7471,7377,7346,7319,7292,7266,7240,7213,7187,7159,7134,7109,7083,7056,7030,7005,6979,6955,6930,6908,6884,6862,6841,6818,6798,6304,7436,6507,6492,6463,6477,6400,6448,6435,6423,6411,6387,6375,6478,6462,6449,6436,6424,6412,6399,6313,6331,6322,7031,7004,6956,6980,6932,6907,6887,6865,6842,6820,6799,6779,6759,6743,6726,6709,6692,6674,6657,6639,6621,6604,6586,6570,6555,6538,6522,6493,6508,7271,7244,7216,7166,7191,7139,7113,7087,7035,6984,7009,6960,6912,6936,6890,6846,6868,6824,6803,6783,6747,6730,7061,6256,6713,6696,6678,6661,6643,6625,6608,6542,6511,6481,6496,6467,6452,6439,6427,6415,6404,6391,6379,6368,6355,6526,6344,6333,6323,6314,6305,6296,6288,6280,7474,7441,7411,7353,7381,6590,7325,7298,6263,7475,7440,7380,7410,7350,7323,7297,7270,7243,7217,7190,7163,7138,7112,6574,6559,6763,6465,6450,6437,6425,6413,6402,6389,7189,6377,6366,6272,7161,7438,7408,7379,7321,7348,7294,7268,7241,7215,7188,7136,7111,7084,7058,6957,7032,7007,6981,6933,6910,6886,6864,6509,6479,6494,6843,6332,6821,6800,6762,6780,6744,6727,6710,6693,6677,6658,6640,6622,6605,6589,6571,6554,6539,6523,6160,6153,6157,6150,6146,6148,6144,6731,6142,6141,6140,6363,6353,6342,6376,7470,7437,7407,7376,7349,7322,6801,6889,6867,6844,6822,6781,6761,6745,6728,6711,6694,6676,6659,6641,6623,6606,6588,6572,6557,6540,6524,6388,7351,7324,7296,7269,7242,7214,7164,7137,7110,7085,7059,7033,7006,6982,6958,6934,6909,7472,7439,7409,7378,6265,6453,7133,6440,6429,6416,6403,6392,6380,6356,6367,6345,6335,6324,6315,6306,6297,6289,6281,6257,6249,6243,6237,6232,6225,6213,6207,6219,6200,6196,6189,6183,6177,6171,6166,7476,7443,7412,7382,7326,7352,7299,7273,7245,7218,7192,7165,7140,7114,7088,7063,7036,7010,6985,6961,6937,6913,6869,6891,6847,6825,6804,6784,6765,6748,6714,6681,6662,6645,6626,6273,6697,6609,6592,6558,6575,6527,6543,6512,6497,6466,6482,7060,7086,7034,6983,7008,6959,6935,6911,6888,6866,6845,6823,6802,6782,6764,6746,6729,6712,6679,6660,6695,6642,6624,6607,6573,6591,6556,6541,6525,6510,6495,6464,6480,6451,6438,6426,6414,6401,6390,6378,6365,6354,6343)  \n"
					+ " 	      and tokenid in ('银行存款/澳新银行RMB','银行存款/工商行第一支行','银行存款/工商银行七宝支行','银行存款/工行苏州吴中支行','银行存款/澳新银行USD','银行存款/澳新银行AUD','银行存款/建行上海卢湾支行')  \n"
					+ " 	      and (dirction= -1 * 1 or ( dirction= 1 and occurvalue<0 )) \n"
					+ " 	      group by voucherid  \n"
					+ " 	    ) b          \n"
					+ " 	    on a.voucherid=b.voucherid       \n"
					+ " 	   ) a         \n"
					+ " 	 where length(bbb)>=8 \n"
					+ " 	   group by bbb    \n"
					+ " 	  )a left join   \n"
					+ " 	 (  \n"
					+ " 	 select subjectid,subjectfullname2 from c_account where   accpackageid='1000022008' and submonth = '1'  \n"
					+ " 	 ) b  \n" + " 	  on a.bbb = b.subjectid  \n";

			conn = dbc.getConnect("100002");

			System.out.println("1=" + aa.getCurrentTime());
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			System.out.println("2=" + aa.getCurrentTime());
			double sum = 0;
			while (rs.next()) {
				sum = sum + rs.getDouble("sum_occurvalue");
			}
			System.out.println("2=" + aa.getCurrentTime());
			System.out.println("sum=" + sum);
			rs.close();
			ps.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

	}

	
	/**
	 *	科目发生额分层统计表
	 *	对应科目发生额分析表 
	 */
	//删除临时表
	public void DelTempTable(String TabName) throws Exception {
		PreparedStatement ps = null;
		try {
			// DROP TABLE IF EXISTS `tt`;
			String sql = "DROP TABLE IF EXISTS " + TabName + "";
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	//在凭证中增加一级科目以及汇总同一笔凭证有多个同科目的金额用于确定一笔凭证中同方向的科目是唯一的
	public void tmpSubjectEntry(String tmpName,String bYear,String eYear,String bdate,String edate)throws Exception{
		tmpSubjectEntry( tmpName, bYear, eYear, bdate, edate, "");
	}
	
	public void tmpSubjectEntry(String tmpName,String bYear,String eYear,String bdate,String edate,String subjectid)throws Exception{
		int bTime = Integer.parseInt(bYear) * 12 + Integer.parseInt(bdate);
		int eTime = Integer.parseInt(eYear) * 12 + Integer.parseInt(edate);
		
		tmpSubjectEntry( tmpName, bTime, eTime, subjectid);
		
	}
	
	public void tmpSubjectEntry(String tmpName,int bTime,int eTime,String subjectid)throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {
			DelTempTable(tmpName); //删除临时表
			
			String strSql = "";
			if(!"".equals(subjectid)){
				sql = "SELECT GROUP_CONCAT(DISTINCT voucherid) AS voucherids " +
				"	FROM 	c_subjectentry a,(SELECT * FROM c_accpkgsubject WHERE 1=1 AND subjectid  in ('"+subjectid+"') ) b " +
				"	WHERE 1=1" +
				"	AND SUBSTRING(a.vchdate,1,4) *12 +SUBSTRING(a.vchdate,6,2) >= "+bTime+" " +
				"	AND SUBSTRING(a.vchdate,1,4) *12 +SUBSTRING(a.vchdate,6,2) <= "+eTime+"  " +
				"	AND a.accpackageid = b.accpackageid " +
				"	AND (a.SubjectFullName1 = b.SubjectFullName OR a.SubjectFullName1 LIKE CONCAT(b.SubjectFullName,'/%')) ";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				if(rs.next()){
					String voucherids = rs.getString("voucherids");
					if(!"".equals(voucherids)){
						strSql = " and a.voucherid in ("+voucherids+") ";
					}
				}
				if("".equals(strSql))strSql = " and 1=2 ";
			}
			
			//生成一张临时表
			sql = "create table " + tmpName + "_0 as " +
			"			SELECT accpackageid,voucherid,vchdate, \n" +
			"			GROUP_CONCAT(distinct IF(dirction = 1,subjectid,NULL)) AS debitsubjects, \n" +
			"			GROUP_CONCAT(distinct IF(dirction = -1,subjectid,NULL)) AS creditsubjects \n" +
			"			FROM c_subjectentry a \n" +
			"			WHERE 1=1 \n" +
			"			AND SUBSTRING(a.vchdate,1,4) *12 +SUBSTRING(a.vchdate,6,2) >= "+bTime+" \n" +
			"			AND SUBSTRING(a.vchdate,1,4) *12 +SUBSTRING(a.vchdate,6,2) <= "+eTime+" \n" +
			"			GROUP BY accpackageid,voucherid,vchdate \n" ;
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);	
			sql = "alter table " + tmpName + "_0 " +
			"	add index `accpackageid` (`accpackageid`)," +
			"	add index `voucherid` (`voucherid`)," +
			"	add index `vchdate` (`vchdate`)";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
//			isfrom1
//			1  一借一贷
//			2  多借一贷
//			3  一借多贷
//			4  多借多贷
//			5  同贷
//			6  同借
			for(int ii = bTime; ii <= eTime; ii++){
				if(ii == bTime){
					sql = "create table " + tmpName + " as \n" ;
				}else{
					sql = "INSERT INTO "+tmpName+" ";
				}
				
				sql +="	SELECT *," +
				"	IF((a.debit = 0 AND a.credit = 0),1,IF((a.debit > 0 AND a.credit = 0),2,IF((a.debit = 0 AND a.credit > 0),3,4))) AS isfrom, \n" +
				"	IF((a.debit1 = 0 AND a.credit1 = 0),1,IF((a.debit1 > 0 AND a.credit1 = 0),2,IF((a.debit1 = 0 AND a.credit1 > 0),3,if(a.debit1 is null,5,if(a.credit1 is null,6,4)) ))) AS isfrom1 \n" +
				"	FROM ( \n" +
				"		SELECT \n" +
				"		a.accpackageid,a.voucherid,a.oldvoucherid,a.typeid,a.vchdate,a.subjectid,a.subjectname1,a.subjectfullname1,a.dirction, \n" +
				"		SUM(a.occurvalue) AS Occurvalue,IF(a.dirction * a.occurvalue>0,1,-1) AS opt,a.debitsubjects,a.creditsubjects, \n" +
				"		CEIL((LENGTH(TRIM(BOTH ',' FROM  a.debitsubjects ))-LENGTH(REPLACE(TRIM(BOTH ',' FROM  a.debitsubjects ),',',''))) / LENGTH(',') ) AS debit, \n" +
				"		CEIL((LENGTH(TRIM(BOTH ',' FROM  a.creditsubjects ))-LENGTH(REPLACE(TRIM(BOTH ',' FROM  a.creditsubjects ),',',''))) / LENGTH(',') ) AS credit, \n" +
				
				"		c.debitsubjects as debitsubjects1,c.creditsubjects as creditsubjects1," +
				"		CEIL((LENGTH(TRIM(BOTH ',' FROM  c.debitsubjects ))-LENGTH(REPLACE(TRIM(BOTH ',' FROM  c.debitsubjects ),',',''))) / LENGTH(',') ) AS debit1, \n" +
				"		CEIL((LENGTH(TRIM(BOTH ',' FROM  c.creditsubjects ))-LENGTH(REPLACE(TRIM(BOTH ',' FROM  c.creditsubjects ),',',''))) / LENGTH(',') ) AS credit1, \n" +
				"		b.subjectid AS bsubjectid,b.SubjectFullName AS bSubjectFullName \n" +
				"		FROM ( \n" +
				"			select * from c_subjectentry a \n" +
				"			WHERE 1=1 \n" +
				"			AND SUBSTRING(a.vchdate,1,4) *12 +SUBSTRING(a.vchdate,6,2) = "+ii+" \n" +
				strSql + 
				"		) a,(" +
				"			SELECT * FROM c_accpkgsubject WHERE 1=1 and Level0 =1 " +
				"		) b ," + tmpName + "_0 c \n" +
				"		WHERE 1=1 \n" +
				//"		AND SUBSTRING(a.vchdate,1,4) *12 +SUBSTRING(a.vchdate,6,2) <= "+eTime+"  \n" +
				"		AND SUBSTRING(c.vchdate,1,4) *12 +SUBSTRING(c.vchdate,6,2) = "+ii+" \n" +
				"		AND a.accpackageid = c.accpackageid \n" +
				"		AND a.voucherid = c.voucherid \n" +
				"		AND a.vchdate = c.vchdate \n" +
				"		AND a.accpackageid = b.accpackageid \n" +
				"		AND (a.SubjectFullName1 = b.SubjectFullName OR a.SubjectFullName1 LIKE CONCAT(b.SubjectFullName,'/%')) \n" +
				"		GROUP BY a.accpackageid,a.voucherid,a.oldvoucherid,a.typeid,a.vchdate,a.subjectid,a.dirction" +
				"	) a";
				//System.out.println(sql);
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);	
			}
			
			sql = "ALTER TABLE " + tmpName + " " +
			"ADD INDEX accpackageid (accpackageid)," +
			"ADD INDEX voucherid (voucherid)," +
			"ADD INDEX bsubjectid (bsubjectid)," +
			"ADD INDEX dirction (dirction)," +
			"ADD INDEX opt (opt)";
			ps = conn.prepareStatement(sql);
			ps.execute();
			DbUtil.close(ps);
			
			DelTempTable(tmpName + "_0"); 
		} catch (Exception e) {
			System.out.println("error sql : "+ sql);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}
	
	public void createTable(String tmpName)throws Exception{
		Statement st = null;
		try {
			DelTempTable(tmpName); //删除临时表
			
			st = conn.createStatement();
			
			String sql = "CREATE TABLE "+tmpName+" (" +
			"	bsubjectid VARCHAR(200)  DEFAULT ''," +
			"	bSubjectFullName VARCHAR(250) DEFAULT ''," +
			"	dirction INT(1)  DEFAULT '0'," +
			"	setname VARCHAR(500)  DEFAULT ''," +
			"	opt VARCHAR(500)  DEFAULT ''," +
			"	countValue INT(21)  DEFAULT '0'," +
			"	sumValue DECIMAL(20,2) DEFAULT NULL," +
			"	setting INT(20)  DEFAULT '0'," +
			"	voucherids MEDIUMTEXT, " +
			"	KEY bsubjectid (bsubjectid,setting) " +
			") ENGINE=MYISAM DEFAULT CHARSET=gbk";
			st.execute(sql);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(st);
		}
	}
	
	//用重要性水平来做分层统计表
	public void tmpSubjectEss(String tmpName,String tmpName1,String subjectid,String acc,String projectid,String [] settings)throws Exception{
		Statement st = null;
		String sql = "";
		try {
			ASFuntion CHF=new ASFuntion();
			st = conn.createStatement();
			
			createTable(tmpName); //创建tmpName 表
			
			String strSql1 = "",strSql2 = "",strSql3 = "",strSql4 = "",strSql5 = "" ,strSql6 = "",strSql7 = "";
			
			//获取重要性水平报表值
			Project project = new ProjectService(conn).getProjectById(projectid);
			ReportModel rm = new ReportModel(acc,projectid,project.getAuditTimeEnd().substring(5,7));
			String choseEL = rm.getChoseEL(); //选中的报表重要性水平
			String reportResult = "";
//			rm.getReportResult(0,2); //总资产(重要性水平)
//			rm.getReportResult(0,3); //总资产(自定义)
//			rm.getReportResult(1,2); //净资产(重要性水平)
//			rm.getReportResult(1,3); //净资产(自定义)
//			rm.getReportResult(2,2); //营业收入（销售收入）(重要性水平)
//			rm.getReportResult(2,3); //营业收入（销售收入）(自定义)
//			rm.getReportResult(3,2); //净利润(重要性水平)
//			rm.getReportResult(3,3); //净利润(自定义)
//			rm.getReportResult(4,2); //费用总额(重要性水平)
//			rm.getReportResult(4,3); //费用总额(自定义)
//			rm.getReportResult(5,2); //毛利(重要性水平)
//			rm.getReportResult(5,3); //毛利(自定义)
			switch(Integer.parseInt(choseEL)){
			case 1:
				reportResult = CHF.showMoney2(rm.getReportResult(0,3));
				if("".equals(reportResult) || Double.parseDouble(reportResult) == 0.00){
					//自定义值为空或0，选择默认值
					reportResult = CHF.showMoney2(rm.getReportResult(0,2)); 
				}
				break;
			case 2:
				reportResult = CHF.showMoney2(rm.getReportResult(1,3));
				if("".equals(reportResult) || Double.parseDouble(reportResult) == 0.00){
					//自定义值为空或0，选择默认值
					reportResult = CHF.showMoney2(rm.getReportResult(1,2)); 
				}
				break;
			case 3:
				reportResult = CHF.showMoney2(rm.getReportResult(2,3));
				if("".equals(reportResult) || Double.parseDouble(reportResult) == 0.00){
					//自定义值为空或0，选择默认值
					reportResult = CHF.showMoney2(rm.getReportResult(2,2)); 
				}
				break;
			case 4:
				reportResult = CHF.showMoney2(rm.getReportResult(3,3));
				if("".equals(reportResult) || Double.parseDouble(reportResult) == 0.00){
					//自定义值为空或0，选择默认值
					reportResult = CHF.showMoney2(rm.getReportResult(3,2)); 
				}
				break;
			case 5:
				reportResult = CHF.showMoney2(rm.getReportResult(4,3));
				if("".equals(reportResult) || Double.parseDouble(reportResult) == 0.00){
					//自定义值为空或0，选择默认值
					reportResult = CHF.showMoney2(rm.getReportResult(4,2)); 
				}
				break;
			case 6:
				reportResult = CHF.showMoney2(rm.getReportResult(5,3));
				if("".equals(reportResult) || Double.parseDouble(reportResult) == 0.00){
					//自定义值为空或0，选择默认值
					reportResult = CHF.showMoney2(rm.getReportResult(5,2)); 
				}
				break;
			}
			strSql7 = "union all select a.accpackageid,a.subjectid,'"+reportResult+"' as s1,ROUND('"+reportResult+"',2)*'"+percentage+"'/100 as s2,'大额区间(正)','','' from c_account a where accpackageid='"+acc+"' and submonth=12 and level1 = 1 ";
			strSql7 +="union all select a.accpackageid,a.subjectid,'-"+reportResult+"' as s1,ROUND('-"+reportResult+"',2)*'"+percentage+"'/100 as s2,'大额区间(负)','','' from c_account a where accpackageid='"+acc+"' and submonth=12 and level1 = 1 ";
			
			if(!"".equals(subjectid)){
				strSql1 = "a.subjectid,a.subjectname1,";
				strSql2 = " inner join c_accpkgsubject c on c.subjectid in ('"+subjectid+"') AND (a.SubjectFullName1 = c.SubjectFullName OR a.SubjectFullName1 LIKE CONCAT(c.SubjectFullName,'/%')) ";
				strSql3 = " a.subjectid,a.dirction,b.opt ";
			}else{
				strSql1 = "a.bsubjectid,a.bSubjectFullName,";
				strSql3 = " a.bsubjectid,a.dirction,b.opt ";
			}
			
			String tmpName2 = tmpName1 + "_0";
			
			String customerid = acc.substring(0,6);
			CustomerService customerService = new CustomerService(new DBConnect().getConnect(customerid));
			String upSubSql = "a.subjectid like '1%' or a.subjectid like '2%' or a.subjectid like '3%'";
			String downSubSql = "a.subjectid like '4%' or a.subjectid like '5%'";
			if(customerService.getCustomer(customerid).getVocationId().equals("59")){
				upSubSql = "a.subjectid like '1%' or a.subjectid like '2%' or a.subjectid like '3%' or a.subjectid like '4%'";
				downSubSql = "a.subjectid like '5%' or a.subjectid like '6%'";
			}
			
			strSql4 = 			"			select a.AccpackageID,a.subjectid,a.accname,abs(a.balance) as BOrD,  essentiality1, essentiality2, essentiality3 " +
			"			from  ( " +
			"				select * from c_account where accpackageid='"+acc+"' and submonth=12 " +
			"			) a inner join ( " +
			"				select * from z_essentiality  where projectid='"+projectid+"' " +
			"			)b  on a.subjectid=b.subjectid  " +
			"			where  ("+upSubSql+") " +
			"	 		union " +
			"			select a.AccpackageID,a.subjectid,a.accname,abs(a.debittotalocc) as BOrD,  essentiality1, essentiality2, essentiality3 " +
			"			from  ( " +
			"				select * from c_account where accpackageid='"+acc+"' and submonth=12 " +
			"			) a inner join ( " +
			"				select * from z_essentiality  where projectid='"+projectid+"' " +
			"			)b  on a.subjectid=b.subjectid  " +
			"			where  ("+downSubSql+") ";
			
			
			for (int i = 0; i < settings.length; i++) {
				if("[-∞]".equals(settings[i])){
					strSql6 += "	select '"+settings[i]+"' as setname,'-100000' as sett ,"+i+" as setting 	";
				}else if("[+∞]".equals(settings[i])){
					strSql5 += "union	select '"+settings[i]+"' as setname,'100000' as sett ,"+i+" as setting 	";
				}else if("0.0".equals(settings[i])){
					strSql5 += "union  select '"+settings[i]+"' as setname,'"+settings[i]+"' as sett ,"+i+" as setting 	";
				}else{
					strSql5 += "union  select '"+settings[i]+"' as setname,'"+settings[i]+"' as sett ,"+i+" as setting 	";
					strSql5 += "union  select '-"+settings[i]+"' as setname,'-"+settings[i]+"' as sett ,"+i+" as setting 	";
				}
			}
			
			sql = "create table "+tmpName2+" \n" +
//			"	select a.AccpackageID,a.subjectid,a.a1,a.sett,b.setting," +
//			"	concat(if(a.setname='[-∞]',a.setname,FORMAT(a.essentiality,2)),' ~ ',if(b.setname='[+∞]',b.setname,FORMAT(b.essentiality,2))) as opt, " +
//			"	if(a.essentiality =0 and b.essentiality=0 and a.setname='[-∞]','-999999999', a.essentiality) as Greater," +
//			"	if(a.essentiality =0 and b.essentiality=0 and b.setname='[+∞]','999999999', b.essentiality) as less" +
			"select a.*, " +
			"	if(SUBSTRING_INDEX(bstr,',',-1) = '[-∞]',concat('[-∞] ~ ',FORMAT(less,2)),if(a.setname = '[+∞]',concat(FORMAT(Greater,2),' ~ [+∞]'),concat(FORMAT(Greater,2),' ~ ',FORMAT(less,2)))) as opt, " +
			"	length(bstr)-length(replace(bstr,',','')) + 1  as setting, " +
			"	SUBSTRING_INDEX(bstr,',',-1) as bset " +
			"	from (" +
			
			"	select a.AccpackageID,a.subjectid,a.setname,max(b.essentiality) as Greater,a.essentiality as less,group_concat(distinct b.setname order by b.essentiality) as bstr  " +
			"	from (" +
			
			"	select AccpackageID,subjectid,a1,ROUND(if(a1 = 0 and setname='[-∞]','-999999999',if(a1 = 0 and setname='[+∞]', '999999999', essentiality)),2) as essentiality,setname,sett,setting " +
			"	from ( " +
			
			"		select AccpackageID,subjectid,ifnull(essentiality3,if(ifnull(essentiality2,0)=0,BOrD,essentiality2)) as a1,ROUND(ifnull(essentiality3,if(ifnull(essentiality2,0)=0,BOrD,essentiality2)) * (b.sett),2) as essentiality ,b.* " +
			"		from (" +
			strSql4 + 
			"		) a ,(" +
			strSql6 + strSql5 +
			"		) b " +
			
			"	)a " +
			
			//union 重要性水平
			strSql7 + 
			"	) a ,(" +
			
			"	select AccpackageID,subjectid,a1,ROUND(if(a1 = 0 and setname='[-∞]','-999999999',if(a1 = 0 and setname='[+∞]', '999999999', essentiality)),2) as essentiality,setname,sett,setting " +
			"	from ( " +

			"		select AccpackageID,subjectid,ifnull(essentiality3,if(ifnull(essentiality2,0)=0,BOrD,essentiality2)) as a1,ROUND(ifnull(essentiality3,if(ifnull(essentiality2,0)=0,BOrD,essentiality2)) * (b.sett),2) as essentiality ,b.* " +
			"		from (" +
			strSql4 + 
			"		) a ,(" +
			strSql6 + strSql5 + 
			"		) b " +
			
			"	)a " +
			//union 重要性水平
			strSql7 + 
			"	) b " +
			"	where a.AccpackageID = b.AccpackageID and a.subjectid =b.subjectid " +
			"	and a.essentiality > b.essentiality " +
			"	group by a.AccpackageID,a.subjectid,a.essentiality" +
			"	order by a.AccpackageID,a.subjectid,a.essentiality " +
			
			"	) a";
			System.out.println("tmpName2 sql=|"+sql);
			st.execute(sql);
			
			sql = "INSERT INTO "+tmpName+" (bsubjectid,bSubjectFullName,dirction,opt,countValue,sumValue,setting,voucherids,setname) " + 
			"	select "+strSql1+" a.dirction,b.opt, " +
			"	COUNT(*) AS countValue,SUM(a.Occurvalue) AS sumValue, " +
			"	b.setting,group_concat(distinct a.voucherid) as voucherids,b.bset as setname " +
			"	from "+tmpName1+" a " +
			"	left join "+tmpName2+" b " +
			"	on a.bsubjectid = b.subjectid  " +
			"	and occurvalue >= Greater and occurvalue < less " +
			strSql2 +
			"	group by " + strSql3;
			System.out.println(sql);
			st.execute(sql);
			
			sql = "INSERT INTO "+tmpName+" (bsubjectid,bSubjectFullName,dirction,opt,countValue,sumValue,setting,voucherids,setname) " +
			"	SELECT bsubjectid,bSubjectFullName,dirction,'合计', SUM(countValue)  AS countValue,SUM(sumValue) AS sumValue,'0' AS setting,GROUP_CONCAT(voucherids) AS voucherids,'' AS setname " +
			"	FROM "+tmpName+"	" +
			"	GROUP BY bsubjectid,dirction";
			st.execute(sql);
			
			DelTempTable(tmpName2); //删除临时表
			
		} catch (Exception e) {
			System.out.println("error sql : "+ sql);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(st);
		}
		
		
	}
	
	//科目发生额分层统计表 
	public void tmpSubjectEntry(String tmpName,String tmpName1,String subjectid,String [] settings)throws Exception{
		Statement st = null;
		String sql = "";
		try {
			ASFuntion CHF=new ASFuntion();
			
			st = conn.createStatement();
			
			createTable(tmpName); //创建tmpName 表
			
			String string = "",strSql1 = "",strSql2 = "",strSql3 = "",strSql4 = "";
			if(!"".equals(subjectid)){
				strSql1 = "a.subjectid,a.subjectname1,";
				strSql2 = " ,c_accpkgsubject b ";
				strSql3 = " and b.subjectid in ('"+subjectid+"') AND (a.SubjectFullName1 = b.SubjectFullName OR a.SubjectFullName1 LIKE CONCAT(b.SubjectFullName,'/%')) ";
				strSql4 = " a.subjectid,a.dirction ";
			}else{
				strSql1 = "a.bsubjectid,a.bSubjectFullName,";
				strSql4 = " a.bsubjectid,a.dirction ";
			}
			
			for(int i=1;i<settings.length;i++){
				String set = "";
				if(!"[+∞]".equals(settings[1])){
					if("".equals(string)){
						set = " AND (a.Occurvalue) <" + settings[i];
					} else if(i==settings.length-1){
						set = " AND (a.Occurvalue) >=" + string;
					} else{
						set = " AND (a.Occurvalue) >=" + string + " AND (a.Occurvalue) < " + settings[i];
					}
				}
				
				String opt = ("".equals(string) ? "[-∞]" : CHF.showMoney3(string))+" - "+("[+∞]".equals(settings[i]) ? settings[i] : CHF.showMoney3(settings[i]));

				sql = "SELECT "+strSql1+" a.dirction,'"+opt+"' AS opt,COUNT(*) AS countValue,SUM(a.Occurvalue) AS sumValue,'"+i+"' AS setting,group_concat(distinct a.voucherid) as voucherids,'"+("".equals(string) ? "[-∞]" : CHF.showMoney3(string))+"' as setname " +
				"	from (" +
				"		select distinct a.* FROM "+tmpName1+" a " + strSql2 + 
				"		WHERE 1=1  " +
				set + strSql3 + 
				"	) a  " +
				"	GROUP BY " + strSql4;
				
				System.out.println(opt + "|" + sql);
				sql = "insert into " + tmpName + "(bsubjectid,bSubjectFullName,dirction,opt,countValue,sumValue,setting,voucherids,setname) " + sql;
				st.execute(sql);
				
				string = settings[i];
			}
			
			sql = "INSERT INTO "+tmpName+" (bsubjectid,bSubjectFullName,dirction,opt,countValue,sumValue,setting,voucherids,setname) " +
			"	SELECT bsubjectid,bSubjectFullName,dirction,'合计', SUM(countValue)  AS countValue,SUM(sumValue) AS sumValue,'0' AS setting,GROUP_CONCAT(voucherids) AS voucherids,'' as setname " +
			"	FROM "+tmpName+"	" +
			"	GROUP BY bsubjectid,dirction";
			st.execute(sql);
			
		} catch (Exception e) {
			System.out.println("error sql : "+ sql);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(st);
		}
	}
	
	//对应科目发生额分析表
	public void tmpSubjectEntry(String tmpName,String tmpName1)throws Exception{
		Statement st = null;
		String sql = "";
		try {
			
			String tmpName2 = tmpName1 + "_1";
//			DelTempTable(tmpName); //删除临时表
			DelTempTable(tmpName2);
			
			st = conn.createStatement();
			
			sql = "CREATE TABLE "+tmpName2+" AS \n" +
			"	SELECT *," +
			"	CASE a.isfrom " +
			"	WHEN 5 THEN IF(a.dirction * a.occurvalue>0,1,-1) " +
			"	WHEN 6 THEN IF(a.dirction * a.occurvalue>0,1,-1) " +
			"	ELSE a.dirction END AS opt FROM (" +			
			"		SELECT  accpackageid,bsubjectid,bsubjectfullname, voucherid,dirction,isfrom1 as isfrom,SUM((Occurvalue)) AS Occurvalue \n" +
			"		FROM "+tmpName1+"  \n" +
			"		GROUP BY accpackageid,bsubjectid,bsubjectfullname, voucherid,dirction,isfrom1 \n" +
			"	) a " ;
			st.execute(sql);
			sql = "ALTER TABLE "+tmpName2+" " +
			"ADD INDEX voucherid ( voucherid )," +
			"ADD INDEX accpackageid ( accpackageid )," +
			"ADD INDEX bsubjectid ( bsubjectid )," +
			"ADD INDEX dirction ( dirction )," +
			"ADD INDEX opt ( opt )," +
			"ADD INDEX isfrom ( isfrom )	";
			st.execute(sql);
			
			//一借一贷，一借多贷，一贷多借
//			sql = "CREATE TABLE "+tmpName+" AS \n" + 
			sql = "INSERT INTO "+tmpName+" (accpackageid,subjectid,subjectfullname,bsubjectid,bsubjectfullname,opt,Occurvalue,countValue,avgValue,`maxValue`,voucherids,perValue,orderid) \n" +
			"SELECT a.*,ROUND(Occurvalue/allOccurvalue * 100,2) as perValue,1 AS orderid FROM ( \n" +
			"	SELECT a.accpackageid,a.bsubjectid AS subjectid,a.bsubjectfullname AS subjectfullname,b.bsubjectid,b.bsubjectfullname,(-1)*a.dirction AS opt, \n" +
			"	SUM(( \n" +
			"		CASE a.isfrom \n" +
			"		WHEN 2 THEN IF(a.dirction = 1 ,a.Occurvalue,b.Occurvalue) \n" +
			"		WHEN 3 THEN IF(a.dirction = 1 ,b.Occurvalue,a.Occurvalue) \n" +
			"		WHEN 5 THEN (-1)*b.Occurvalue \n" +
			"		WHEN 6 THEN (-1)*b.Occurvalue \n" +
			"		ELSE b.Occurvalue \n" +
			"		END  \n" +
			"	)) AS Occurvalue, \n" +
			"	COUNT(*) AS countValue, \n" +
			"	ROUND(SUM(( \n" +
			"		CASE a.isfrom \n" +
			"		WHEN 2 THEN IF(a.dirction = 1 ,a.Occurvalue,b.Occurvalue) \n" +
			"		WHEN 3 THEN IF(a.dirction = 1 ,b.Occurvalue,a.Occurvalue) \n" +
			"		WHEN 5 THEN (-1)*b.Occurvalue \n" +
			"		WHEN 6 THEN (-1)*b.Occurvalue \n" +
			"		ELSE b.Occurvalue \n" +
			"		END  \n" +
			"	))/COUNT(*),2) AS avgValue, \n" +
			"	MAX(ABS( \n" +
			"		CASE a.isfrom \n" +
			"		WHEN 2 THEN IF(a.dirction = 1 ,a.Occurvalue,b.Occurvalue) \n" +
			"		WHEN 3 THEN IF(a.dirction = 1 ,b.Occurvalue,a.Occurvalue) \n" +
			"		WHEN 5 THEN (-1)*b.Occurvalue \n" +
			"		WHEN 6 THEN (-1)*b.Occurvalue \n" +
			"		ELSE b.Occurvalue \n" +
			"		END  \n" +
			"	)) AS `maxValue`,GROUP_CONCAT(DISTINCT a.voucherid) AS voucherids \n" +
			"	FROM "+tmpName2+" a ,"+tmpName2+" b \n" +
			"	WHERE 1=1 \n" +
			"	AND a.isfrom <>4 AND b.isfrom <>4 " +
			"	AND a.accpackageid = b.accpackageid \n" +
			"	AND a.voucherid = b.voucherid \n" +
			"	AND a.opt = (-1)*b.opt \n" +
			"	GROUP BY  a.bsubjectid ,b.bsubjectid,b.opt \n" +
			"	ORDER BY a.bsubjectid,b.bsubjectid,a.opt,b.opt \n" +
			") a,( \n" +
			"	SELECT a.bsubjectid,(-1)*dirction AS opt, SUM((Occurvalue)) AS allOccurvalue \n" +
			"	FROM "+tmpName2+"  a \n" +
			"	GROUP BY bsubjectid,dirction \n" +
			") b \n" +
			"where a.subjectid = b.bsubjectid AND a.opt = b.opt";
			st.execute(sql);
			
//			sql = "ALTER TABLE " + tmpName + " " +
//			"ADD INDEX subjectid (subjectid)," +
//			"ADD INDEX bsubjectid (bsubjectid)," +
//			"ADD INDEX opt (opt)";
//			st.execute(sql);
			
			//合计
			sql = "INSERT INTO "+tmpName+" (accpackageid,subjectid,subjectfullname,bsubjectid,bsubjectfullname,opt,Occurvalue,orderid) " +
			"	SELECT a.accpackageid,a.bsubjectid,a.bsubjectfullname,'合计','合计',(-1)*dirction," +
			"	SUM((Occurvalue)) AS Occurvalue,4 AS orderid" +
			"	FROM "+tmpName1+" a " +
			"	GROUP BY bsubjectid,dirction";
			st.execute(sql);
			
			String strSql = "";
			if(!"".equals(accpackageid)){
				strSql = " and a.accpackageid = '"+accpackageid+"' ";
			}
			//多借多贷凭证
			sql = "INSERT INTO "+tmpName+" (accpackageid,subjectid,subjectfullname,bsubjectid,bsubjectfullname,opt,Occurvalue,countValue,voucherids,orderid) \n" + 
			"	SELECT a.accpackageid,a.subjectid,a.subjectfullname,'多借多贷凭证','多借多贷凭证',a.opt,a.Occurvalue - IFNULL(b.Occurvalue,0),IFNULL(c.countValue,0),IFNULL(c.voucherids,'') AS voucherids,2 AS orderid \n" +
			"	FROM "+tmpName+" a LEFT JOIN ( \n" +
			"		SELECT accpackageid,subjectid,opt,SUM(Occurvalue) AS Occurvalue \n" +
			"		FROM "+tmpName+" a \n" +
			"		WHERE 1=1 "+strSql+" and a.orderid = 1 \n" +
			"		GROUP BY accpackageid,subjectid,opt \n" +
			"	) b  \n" +
			"	ON a.orderid = 4 \n" +
			"	AND a.accpackageid = b.accpackageid AND a.subjectid = b.subjectid \n" +
			"	AND a.opt = b.opt \n" +
			"	LEFT JOIN ( \n" +
			"		SELECT a.accpackageid,a.bsubjectid AS subjectid,a.bsubjectfullname AS subjectfullname,a.opt,COUNT(*) AS countValue,GROUP_CONCAT(DISTINCT a.voucherid) AS voucherids \n" +
			"		FROM ( \n" +
			"			SELECT  accpackageid,bsubjectid,bsubjectfullname, voucherid,dirction as opt,SUM((Occurvalue)) AS Occurvalue,isfrom1 as isfrom,debit,credit \n" +
			"			FROM "+tmpName1+"  \n" +
			"			WHERE isfrom1 =4 \n" +
			"			GROUP BY accpackageid,bsubjectid,bsubjectfullname, voucherid,dirction \n" +
			"		) a  \n" +
			"		GROUP BY  a.accpackageid,a.bsubjectid ,a.opt \n" +
			"	) c ON a.accpackageid = c.accpackageid and a.subjectid = c.subjectid \n" +
			"	AND a.opt = (-1)*c.opt \n" +
			"	WHERE 1=1 "+strSql+" and a.orderid = 4 \n" +
			"	ORDER BY a.subjectid";
			System.out.println(sql);
			st.execute(sql);
			
			sql = "INSERT INTO "+tmpName+" (accpackageid,subjectid,subjectfullname,bsubjectid,bsubjectfullname,opt,countValue,voucherids,orderid) \n" +
			"	SELECT a.accpackageid,a.bsubjectid AS subjectid,a.bsubjectfullname AS subjectfullname,b.bsubjectid,b.bsubjectfullname,(-1)*a.dirction AS opt, \n" +
			"	COUNT(*) AS countValue,GROUP_CONCAT(DISTINCT a.voucherid) AS voucherids,3 as orderid \n" +
			"	FROM "+tmpName2+" a ,"+tmpName2+" b \n" +
			"	WHERE 1=1 \n" +
			"	AND a.isfrom =4 AND b.isfrom =4 " +
			"	AND a.accpackageid = b.accpackageid \n" +
			"	AND a.voucherid = b.voucherid \n" +
			"	AND a.opt = (-1)*b.opt \n" +
			"	GROUP BY  a.bsubjectid ,b.bsubjectid,b.opt \n" +
			"	ORDER BY a.bsubjectid,b.bsubjectid,a.opt,b.opt \n" ;		
			st.execute(sql);
			
			if("c_corList".toLowerCase().equals(tmpName.toLowerCase())){
				//只有插入物理表(c_corList)时，才要插入多借多贷凭证(c_corVoucher)
				//插入多借多贷凭证到c_corVoucher表
				sql = "INSERT INTO c_corlistvoucher (" +
				"	accpackageid,voucherid,oldvoucherid,typeid,vchdate,Serail," +
				"	subjectid,subjectname1,subjectfullname1,dirction,Occurvalue," +
				"	opt,debitsubjects1,creditsubjects1,debit1,credit1," +
				"	bsubjectid,bSubjectFullName,isfrom1" +
				"	) " +
				"	SELECT a.accpackageid,a.voucherid,a.oldvoucherid,a.typeid,a.vchdate,c.Serail," +
				"	c.subjectid,c.subjectname1,c.subjectfullname1,c.dirction,c.Occurvalue," +
				"	a.opt,a.debitsubjects1,a.creditsubjects1,a.debit1,a.credit1," +
				"	a.bsubjectid,a.bSubjectFullName,a.isfrom1 " +
				"	FROM "+tmpName1+" a,c_subjectentry b ,c_subjectentry c" +
				"	WHERE a.isfrom1 = 4 " +
				"	AND a.accpackageid = b.accpackageid " +
				"	AND a.voucherid = b.voucherid " +
				"	AND a.subjectid = b.subjectid " +
				"	AND a.vchdate = b.vchdate " +
				"	AND b.AccPackageID = c.AccPackageID" +
				"	AND b.VoucherID = c.VoucherID" +
				"	AND b.vchdate = c.vchdate" +
				"	AND b.Dirction = (-1)*c.Dirction";
				st.execute(sql);
			}
			
			sumValue(tmpName);
			
			DelTempTable(tmpName2);
			
		} catch (Exception e) {
			System.out.println("error sql : "+ sql);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(st);
		}	
		
	}
	
	//对应科目发生额分析表 ：展开多借多贷
	public String tmpSubjectEntry(String tmpName,String accpackageid,String subjectid)throws Exception{
		Statement st = null;
		ResultSet rs = null;
		String sql = "";
		try {
			StringBuffer sb = new StringBuffer("");
			ASFuntion CHF=new ASFuntion();
			
			st = conn.createStatement();
			
			sql = "SELECT * FROM ( \n" +
			"		SELECT a.accpackageid,a.subjectid,a.subjectfullname, \n" +
			"		a.orderid,a.voucherids," +
			"		a.bsubjectid,a.bsubjectfullname,a.occurvalue,a.perValue,a.countvalue,a.avgvalue,a.maxvalue, \n" +
			"		b.voucherids AS bvoucherids," +
			"		b.bsubjectid AS bbsubjectid,b.bsubjectfullname AS bbsubjectfullname,b.occurvalue AS boccurvalue,b.perValue as bperValue,b.countvalue AS bcountvalue,b.avgvalue AS bavgvalue,b.maxvalue AS bmaxvalue \n" +
			"		FROM "+tmpName+" a \n" +
			"		LEFT JOIN "+tmpName+" b  \n" +
			"		ON a.opt = 1 \n" +
			"		AND b.opt = -1 \n" +
			"		AND a.subjectid = b.subjectid \n" +
			"		AND a.bsubjectid = b.bsubjectid \n" +
			"		AND a.orderid = b.orderid \n" +
			"		WHERE a.opt = 1 and a.orderid = 3 \n" +
			"		UNION  \n" +
			"		SELECT b.accpackageid,b.subjectid,b.subjectfullname, \n" +
			"		b.orderid,a.voucherids," +
			"		a.bsubjectid,a.bsubjectfullname,a.occurvalue,a.perValue,a.countvalue,a.avgvalue,a.maxvalue, \n" +
			"		b.voucherids AS bvoucherids," +
			"		b.bsubjectid AS bbsubjectid,b.bsubjectfullname AS bbsubjectfullname,b.occurvalue AS boccurvalue,b.perValue as bperValue,b.countvalue AS bcountvalue,b.avgvalue AS bavgvalue,b.maxvalue AS bmaxvalue \n" +
			"		FROM "+tmpName+" a \n" +
			"		RIGHT JOIN "+tmpName+" b \n" + 
			"		ON a.opt = 1 \n" +
			"		AND b.opt = -1 \n" +
			"		AND a.subjectid = b.subjectid \n" +
			"		AND a.bsubjectid = b.bsubjectid \n" +
			"		AND a.orderid = b.orderid \n" +
			"		WHERE b.opt = -1 and b.orderid = 3 \n" +
			"	) a \n" +
			"	where 1=1 \n" +
			"	AND a.accpackageid = '"+accpackageid+"' " + 
			"	AND a.subjectid = '"+subjectid+"' " +
			"	ORDER BY subjectid,IFNULL(bsubjectid,bbsubjectid),orderid";
			rs = st.executeQuery(sql);
			String bgColor = "#B2C2D2";
			
			while(rs.next()){

				String acc = CHF.showNull(rs.getString("accpackageid"));
				String sid = CHF.showNull(rs.getString("subjectid"));
				String orderid = CHF.showNull(rs.getString("orderid"));
				
				String bbsubjectid = CHF.showNull(rs.getString("bsubjectid"));
				String bbsubjectfullname = CHF.showNull(rs.getString("bsubjectfullname"));
				String bcountvalue = CHF.showNull(rs.getString("countvalue"));
				String bvoucherids = CHF.showNull(rs.getString("voucherids"));
				String boccurvalue = CHF.showNull(rs.getString("occurvalue"));
				
				String bsubjectid = CHF.showNull(rs.getString("bbsubjectid"));
				String bsubjectfullname = CHF.showNull(rs.getString("bbsubjectfullname"));
				String countvalue = CHF.showNull(rs.getString("bcountvalue"));
				String voucherids = CHF.showNull(rs.getString("bvoucherids"));
				String occurvalue = CHF.showNull(rs.getString("boccurvalue"));
				
				String value="",bvalue="",ovalue = "",bovalue="";
				ovalue = "<td align='right'>--</td>"; 
				if(!"".equals(bsubjectid)) {
					if(!"".equals(occurvalue)) {
						ovalue = CHF.showMoney(occurvalue);
					}
					value = "<a href=\"#\" onclick=\"getClick(this)\">点击凭证分析</a>";
				}else{
					ovalue = "<td align='right'></td>"; 
				}
				
				bovalue = "<td align='right'>--</td>"; 
				if(!"".equals(bbsubjectid)) {
					if(!"".equals(boccurvalue)) {
						bovalue = CHF.showMoney(boccurvalue);
					}
					bvalue = "<a href=\"#\" onclick=\"getClick(this)\">点击凭证分析</a>";
				}else{
					bovalue = "<td align='right'></td>"; 
				}
				//点击基于凭证分录进行分析
					
				sb.append("<tr onmouseover=\"this.bgColor='#E4E8EF';\" style='CURSOR: hand'  onmouseout=\"this.bgColor='"+bgColor+"';\" bgColor='"+bgColor+"' height='18' accpackageid = '"+acc+"' orderid='"+orderid+"' voucherids='"+voucherids+"' subjectid='"+sid+"' bsubjectid='"+bsubjectid+"' occurvalue='"+occurvalue+"' countvalue='"+countvalue+"' bbsubjectid='"+bbsubjectid+"' bcountvalue='"+bcountvalue+"' bvoucherids='"+bvoucherids+"' boccurvalue='"+boccurvalue+"' >");
				sb.append("<td>&nbsp;</td>");
				sb.append("<td>&nbsp;</td>");
				
				sb.append("<td>"+bsubjectid+"</td>");
				sb.append("<td>"+bsubjectfullname+"</td>");
				sb.append(ovalue);
				sb.append("<td>&nbsp;</td>");
				sb.append("<td style=\"text-align:center;\">"+countvalue+"</td>");
				sb.append("<td >"+value+"</td>");
//				sb.append("<td>&nbsp;</td>");
				
				sb.append("<td>"+bbsubjectid+"</td>");
				sb.append("<td>"+bbsubjectfullname+"</td>");
				sb.append(bovalue);
				sb.append("<td>&nbsp;</td>");
				sb.append("<td style=\"text-align:center;\">"+bcountvalue+"</td>");
				sb.append("<td >"+bvalue+"</td>");
//				sb.append("<td>&nbsp;</td>");
				
				sb.append("</tr>");
			}
			
			return sb.toString();
		} catch (Exception e) {
			System.out.println("error sql : "+ sql);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
		}			
	}
	
	public String tmpSubjectEntry(String tmpName,String accpackageid,String subjectid,String dirction)throws Exception{
		Statement st = null;
		ResultSet rs = null;
		String sql = "";
		try {
			StringBuffer sb = new StringBuffer("");
			ASFuntion CHF=new ASFuntion();
			
			st = conn.createStatement();
			
			sql = "SELECT *,if(opt=-1,'贷','借') as dirction " +
			"	FROM " + tmpName + 
			"	WHERE subjectid = '"+subjectid+"'" +
			"	and orderid=3" +
			"	and opt = " + dirction+
			"	ORDER BY subjectid,bsubjectid,orderid ";
			rs = st.executeQuery(sql);
			String bgColor = "#B2C2D2";
			
			while(rs.next()){

				String acc = CHF.showNull(rs.getString("accpackageid"));
				String sid = CHF.showNull(rs.getString("subjectid"));
				String orderid = CHF.showNull(rs.getString("orderid"));
				
				String bsubjectid = CHF.showNull(rs.getString("bsubjectid"));
				String bsubjectfullname = CHF.showNull(rs.getString("bsubjectfullname"));
				String countvalue = CHF.showNull(rs.getString("countvalue"));
				String voucherids = CHF.showNull(rs.getString("voucherids"));
				String occurvalue = CHF.showNull(rs.getString("occurvalue"));
				String dir = CHF.showNull(rs.getString("dirction"));
				
				String value="",bvalue="";
				if(!"".equals(bsubjectid)) {
					if("".equals(occurvalue)) occurvalue = "--"; 
					value = "<a href=\"#\" onclick=\"getClick(this)\">点击基于凭证分录进行分析</a>";
				}
					
				sb.append("<tr onmouseover=\"this.bgColor='#E4E8EF';\" style='CURSOR: hand'  onmouseout=\"this.bgColor='"+bgColor+"';\" bgColor='"+bgColor+"' height='18' accpackageid = '"+acc+"' orderid='"+orderid+"' voucherids='"+voucherids+"' subjectid='"+sid+"' bsubjectid='"+bsubjectid+"' occurvalue='"+occurvalue+"' countvalue='"+countvalue+"' dirction='"+dir+"' >");
				
				sb.append("<td>"+bsubjectid+"</td>");
				sb.append("<td>"+bsubjectfullname+"</td>");
				sb.append("<td>"+dir+"</td>");
				sb.append("<td align='right'>"+occurvalue+"</td>");
				sb.append("<td>&nbsp;</td>");
				sb.append(CHF.showMoney(countvalue));
				sb.append("<td colspan='2'>"+value+"</td>");
//				sb.append("<td>&nbsp;</td>");
				
				sb.append("</tr>");
			}
			
			return sb.toString();
		} catch (Exception e) {
			System.out.println("error sql : "+ sql);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
		}			
	}
	
	//判断是否已经做过对应科目发生额分析表
	public boolean isCorrespond(String tmpName,String acc) throws Exception {
		return isCorrespond( tmpName, acc, "");
	}
	public boolean isCorrespond(String tmpName,String acc,String subjectid) throws Exception {
		Statement st = null;
		ResultSet rs = null;
		String sql = "";
		try {
			boolean bool = false;
			String string = "";
			if(!"".equals(subjectid)) string = " and subjectid = '"+subjectid+"' ";
			st = conn.createStatement();
			
			sql = "select * from "+ tmpName + " where accpackageid = " + acc + string;
			rs = st.executeQuery(sql);
			if(rs.next()){
				bool = true;
			}
			
			sumValue(tmpName);
			return bool;
		} catch (Exception e) {
			System.out.println("error sql : "+ sql);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
		}		
	}
	
	//求数值
	public String get(String tmpName,String acc,String subjectid,String dirction,String orderid)throws Exception {
		Statement st = null;
		ResultSet rs = null;
		String sql = "";
		try {
			String result = "0.00";
			st = conn.createStatement();
			
			sql = "select * from "+ tmpName + 
			" where accpackageid = " + acc + 
			" and subjectid = '"+subjectid+"' " +
			" and opt = " + dirction + 
			" and orderid = " + orderid;
			rs = st.executeQuery(sql);
			if(rs.next()){
				result = rs.getString("occurvalue");
			}
			
			return result;
		} catch (Exception e) {
			System.out.println("error sql : "+ sql);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
		}		
	}
	
	//求合计的凭证数
	public void sumValue(String tmpName)throws Exception {
		Statement st = null;
		String sql = "";
		try {
			st = conn.createStatement();
			sql = "UPDATE " +
			"	"+tmpName+" a,( " +
			"		SELECT accpackageid,subjectid,opt,SUM(countValue) AS countValue FROM "+tmpName+" " +  
			"		WHERE orderid IN (1,2) " +
			"		GROUP BY accpackageid,subjectid,opt " +
			"	) b " +
			"	SET a.countValue = b.countValue " +
			"	WHERE a.orderid = 4 " +
			"	AND a.accpackageid = b.accpackageid " +
			"	AND a.subjectid = b.subjectid " +
			"	AND a.opt = b.opt";
			st.execute(sql);
		} catch (Exception e) {
			System.out.println("error sql : "+ sql);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(st);
		}		
	}
	
	//创建c_corList的临时表
	public void create(String tmpName)throws Exception {
		PreparedStatement ps = null;
		String sql = "";
		try {
			sql = "create table "+tmpName+" like c_corlist ";
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			//出错就表示没有c_corlist表
			try{
				sql = "CREATE TABLE "+tmpName+" (    " +
				"		accpackageid varchar(14) NOT NULL default '',  " +  
				"		subjectid varchar(50) NOT NULL default '',     " +
				"		subjectfullname varchar(500) default NULL,     " +
				"		bsubjectid varchar(50) NOT NULL default '',    " +
				"		bsubjectfullname varchar(500) default NULL,    " +
				"		opt varchar(10) NOT NULL default '',           " +
				"		Occurvalue decimal(30,2) default NULL,         " +
				"		countValue int(10) default NULL,               " +
				"		avgValue decimal(30,2) default NULL,           " +
				"		`maxValue` decimal(30,2) default NULL,         " +
				"		perValue decimal(30,2) default NULL,           " +
				"		voucherids mediumtext,                         " +
				"		orderid int(1) default NULL,                   " +
				"		KEY subjectid (subjectid),                     " +
				"		KEY bsubjectid (bsubjectid),                   " +
				"		KEY opt (opt),                               " +
				"		KEY orderid (orderid)                        " +
				"	) ENGINE=MyISAM DEFAULT CHARSET=gbk ";
				ps = conn.prepareStatement(sql);
				ps.execute();
			} catch (Exception ex) {
				ex.printStackTrace();
				throw e;
			}
		} finally {
			DbUtil.close(ps);
		}	
	}
	
}
