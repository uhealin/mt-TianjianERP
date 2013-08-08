package com.matech.audit.work.subjectentry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.*;

import org.del.JRockey2;
import org.del.JRockey2Opp;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.autotoken.AutoTokenService;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

public class SubjectEntry {

	private Connection conn = null;

	private boolean splitBool = false; //用来标志：明细账多外币时，每个外币分开显示
	
	public SubjectEntry(Connection conn) {
		this.conn = conn;
	}

	public boolean isExistAccPackage(String acc) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select 1 from c_accpkgsubject where AccPackageID = ?";
			ps = conn.prepareStatement(sql);
			ps.setString(1, acc);
			rs = ps.executeQuery();
			if (rs.next()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public String getSubFullName(String acc, String subjectid) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String result = "";
		try {
			String sql = "select subjectfullname from `c_accpkgsubject` where accpackageid='"
					+ acc + "' and subjectid='" + subjectid + "'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getString(1);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 获得一个随机生成的9位数字
	 * 
	 * @return
	 */
	public String getRandom() {
		java.text.DecimalFormat df = new DecimalFormat("####");
		String i = df.format(Math.random() * 1000000000);
		// return i;
		return com.matech.framework.pub.autocode.DELUnid.getNumUnid();
	}

	private String[] getAccIDYearMonth(String Cid, String BYear, String BMonth,
			String EYear, String EMonth) {
		int BY = Integer.parseInt(BYear);
		int BM = Integer.parseInt(BMonth);
		int EY = Integer.parseInt(EYear);
		int EM = Integer.parseInt(EMonth);
		String[] Month = new String[] { "01", "02", "03", "04", "05", "06",
				"07", "08", "09", "10", "11", "12" };
		String[] result = new String[12 * (EY - BY + 1)];
		int ii = 0;
		for (int i = BY; i <= EY; i++) {
			for (int j = 0; j < 12; j++) {
				if (i != BY && i != EY) {
					result[ii] = Cid + String.valueOf(i) + "-"
							+ String.valueOf(i) + "-";
					if (Integer.parseInt(Month[j]) == j + 1)
						result[ii] += Month[j];
				}
				if (BY == EY) {
					if (j + 1 >= BM && j + 1 <= EM) {
						result[ii] = Cid + String.valueOf(i) + "-"
								+ String.valueOf(i) + "-";
						if (Integer.parseInt(Month[j]) == j + 1)
							result[ii] += Month[j];

					}
				} else {
					if (i == BY && j + 1 >= BM) {
						result[ii] = Cid + String.valueOf(i) + "-"
								+ String.valueOf(i) + "-";
						if (Integer.parseInt(Month[j]) == j + 1)
							result[ii] += Month[j];
					}
					if (i == EY && j + 1 <= EM) {
						result[ii] = Cid + String.valueOf(i) + "-"
								+ String.valueOf(i) + "-";
						if (Integer.parseInt(Month[j]) == j + 1)
							result[ii] += Month[j];
					}
				}
				ii++;
			}
		}
		return result;
	}

	public String getSubjects(String acc, String SubjectID) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		try {
			String sql = "select SubjectID,AccName as subjectname from c_account "
					+ "where AccPackageID = '"
					+ acc
					+ "' and (SubjectID like '"
					+ SubjectID
					+ "%' or AccName like '%"
					+ SubjectID
					+ "%') and SubMonth='01' order by SubjectID";
			System.out.println("zyq1111111=" + sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			String bgColor = "";
			int i = 0;
			while (rs.next()) {
				if (i % 2 == 0) {
					bgColor = "#F3F5F8";
				} else {
					bgColor = "#ffffff";
				}
				String s = rs.getString("SubjectID");
				sb
						.append("<tr onclick=\"goSubmit('"
								+ s
								+ "');\" onmouseover=\"this.bgColor='#E4E8EF';\" style=\"CURSOR: hand\" onmouseout=\"this.bgColor='"
								+ bgColor + "';\" bgColor=\"" + bgColor
								+ "\" height=\"18\"> ");
				if (SubjectID.equals(s)) {
					sb.append("<td style=\"color:#FF0000 \">『" + s + "』"
							+ rs.getString("subjectname") + "</td>");
				} else {
					sb.append("<td>『" + s + "』" + rs.getString("subjectname")
							+ "</td>");
				}
				sb.append("</tr>");
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}
	
	public String getSubjectss(String acc, String SubjectID) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select max(level1) from  c_account where accpackageid=? and submonth=1 ";
//			sql = "select max(level0) from  c_accpkgsubject where accpackageid=?  ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, acc);
			rs = ps.executeQuery();
			int maxlevel = 0;
			if (rs.next())
				maxlevel = rs.getInt(1)+1;
			rs.close();
			ps.close();
			if (maxlevel <= 0)
				throw new Exception("科目树为空，或者节点的LEVEL属性设置错误");

			sql= "select distinct subjectID,AccName,if(assitemid is not null,'0',if(isleaf1=1,'1','0')) as isleaf1,level1,subjectID as orderBySubjectID,'0' as type1  from ( \n"
				+"	select subjectID,AccName,level1,isleaf1 from c_account where AccPackageID="+acc+" \n"
				+"	 and submonth='01' \n"
				+"	)a left join  \n"
				+"	( \n"
				+"	select distinct assitemid,asstotalname,accid from c_assitem \n"
				+"	where AccPackageID="+acc+"  \n"
				+"	 and isleaf= 1 \n"
				+"	)b on a.subjectID = b.accid \n"
				+"	#order by a.subjectID \n"
				+"	union all \n"
				+"	select assitemid,asstotalname,'1',level1+1 as level1,subjectID as orderBySubjectID,'1' as type1 from ( \n"
				+"	select subjectID,AccName,level1 from c_account where AccPackageID="+acc+" \n"
				+"	 and submonth='01'  \n"
				+"	)a inner join  \n"
				+"	( \n"
				+"	select assitemid,asstotalname,accid from c_assitem where AccPackageID="+acc+" and isleaf= 1 \n"
				+"	)b on a.subjectID = b.accid \n"
				+"	order by orderBySubjectID desc,type1 desc,subjectID desc \n";
			
			System.out.println("zyq1111111=" + sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int i = 0;
			StringBuffer levelTable[] = new StringBuffer[maxlevel];
			for (i = 0; i < maxlevel; i++) {
				levelTable[i] = new StringBuffer("");
			}

			int isleaf = 0, level = 0,type1 = 0;
			String subjectid = "";
			String subjectname = "";
			String orderBySubjectID = "";
			String bgColor = "";
			while (rs.next()) {
				isleaf = rs.getInt(3);
				subjectid = rs.getString(1);
				subjectname = rs.getString(2);
				level = rs.getInt(4);
				orderBySubjectID = rs.getString(5);
				type1 = rs.getInt(6);
				StringBuffer sbtt = new StringBuffer("");
				
				if (isleaf == 1) {
					
					sbtt.append("<table width=\"100%\"  border=\"0\" cellSpacing=\"1\" cellPadding=\"1\"   bgColor=\""+ bgColor + "\" id=\"subDataSubject\" name=\"subDataSubject\">");
					sbtt.append("<tr  onmouseover=\"this.bgColor='#E4E8EF';\" style=\"CURSOR: hand\" onmouseout=\"this.bgColor='"
									+ bgColor + "';\" bgColor=\"" + bgColor
									+ "\" height=\"18\"> "
									+ "<td width=\"11\" height=\"11\" align=\"right\">"
									+ "<img id=\"ActImg\" src=\"/AuditSystem/images/sjx1.gif\" width=\"11\" height=\"11\" />"
									+ "</td>"				
							);
					
					if (SubjectID.equals(subjectid)) {
						sbtt.append("<td onclick=\"goSubmit(this);\" value1='"+subjectid+"' type1='"+type1+"' orderBySubjectID='"+orderBySubjectID+"'>『" + subjectid + "』"
								+ subjectname + "</td>");
					} else {
						sbtt.append("<td onclick=\"goSubmit(this);\" value1='"+subjectid+"' type1='"+type1+"' orderBySubjectID='"+orderBySubjectID+"'>『" + subjectid + "』" + subjectname
								+ "</td>");
					}
					sbtt.append("</tr>"
							+ "<tr>"
							+ "<td id=\"subImg"
							+ subjectid
							+ "\" style=\"display:none\"></td>"
							+ "<td id=\"subTree"
							+ subjectid
							+ "\" style=\"display:none\"></td>"
							+ "</tr>" );
					sbtt.append("</table>");
				}else{
					
//					枝干节点
					if (level == maxlevel) {
						throw new Exception("科目树错误：层次最深的节点必须是叶子节点,错误的科目号＝"
								+ subjectid);
					}
					sbtt.append("<table width=\"100%\"  border=\"0\" cellSpacing=\"1\" cellPadding=\"1\"  bgColor=\""+ bgColor + "\" id=\"subDataSubject\" name=\"subDataSubject\" >");
					sbtt.append("<tr  onmouseover=\"this.bgColor='#E4E8EF';\" style=\"CURSOR: hand\" onmouseout=\"this.bgColor='"
							+ bgColor + "';\" bgColor=\"" + bgColor
							+ "\" height=\"18\"> "
					);
					sbtt.append("<td width=\"11\" height=\"11\" align=\"right\" onclick=\"getSubTree('"
							+ subjectid + "');\">");
					sbtt.append("<img id=\"ActImg"
							+ subjectid
							+ "\" src=\"/AuditSystem/images/plus.jpg\" width=\"11\" height=\"11\" />");
					sbtt.append("</td>");
			
					if (SubjectID.equals(subjectid)) {
						sbtt.append("<td style=\"color:#FF0000 \" onclick=\"goSubmit(this);\" value1='"+subjectid+"' type1='"+type1+"' orderBySubjectID='"+orderBySubjectID+"'>『" + subjectid + "』"
						+ subjectname + "</td>");
					} else {
						sbtt.append("<td onclick=\"goSubmit(this);\" value1='"+subjectid+"' type1='"+type1+"' orderBySubjectID='"+orderBySubjectID+"'>『" + subjectid + "』" + subjectname
								+ "</td>");
					}
					sbtt.append("</tr>");
					sbtt.append("<tr>");
					sbtt.append("<td id=\"subImg" + subjectid
							+ "\" style=\"display:none\"></td>");
					sbtt.append("<td id=\"subTree" + subjectid
							+ "\" style=\"display:none\">");

					sbtt.append(levelTable[level]); //追加下级的节点

					sbtt.append("</td>");
					sbtt.append("</tr>");
					sbtt.append("</table>");
					//下级节点因为已经汇总完毕，未避免重复汇总，清空
					levelTable[level] = new StringBuffer("");
					
				}
				//汇总到本级节点上去，记住必须是当前节点append原来的，否则顺序就倒了
				sbtt.append(levelTable[level - 1]);
				levelTable[level - 1] = sbtt;
			}
			return levelTable[0].toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	public String getSubTree(String acc, String SubjectID) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		try {
			String sql = "select dataname,ifnull(group_concat(concat(subjectid,'_',Level1,'_',IsLeaf1) order by subjectid SEPARATOR \",,\"),'') as subjectids,ifnull(group_concat(accname order by subjectid SEPARATOR \",,\"),'') as accnames from c_accountall where AccPackageID='"+acc+"' and submonth='1' and AccSign='1' and (subjectid like '"+SubjectID+"%' or accname like '%"+SubjectID+"%') group by dataname";

			org.util.Debug.prtOut("出错:"+sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			while (rs.next()) {
				String dataname = rs.getString(1);
				String[] subjectidss = rs.getString(2).split(",,");
				String[] accnamess = rs.getString(3).split(",,");
				sb.append("<tr height=\"18\" onmouseover=\"this.bgColor='#E4E8EF';\" style=\"CURSOR: hand\" onmouseout=\"this.bgColor='#F3F5F8';\" bgColor=\"#F3F5F8\">\n");
				sb.append("<td bgColor=\"#F3F5F8\">币种："+dataname+"</td>\n");
				for (int i = 0; i < accnamess.length; i++) {
					
				
					String[] subjectidsss = subjectidss[i].split("_");
					String space = "";
					for(int j=1;j<Integer.parseInt(subjectidsss[1]);j++){
						space += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"; 
					}
					String function = "";
				
					function = "onclick=\"goSubmit('"+ subjectidsss[0]+ "','"+dataname+"');\"";
				
					sb.append("<tr "+function+" onmouseover=\"this.bgColor='#E4E8EF';\" style=\"CURSOR: hand\" onmouseout=\"this.bgColor='#F3F5F8';\" bgColor=\"#F3F5F8\" height=\"18\"> \n");
					if (SubjectID.equals(subjectidsss[0])) {
						sb.append("<td style=\"color:#FF0000 \">"+space+"『" + subjectidsss[0] + "』"
						+ accnamess[i] + "</td> \n");
					} else {
						sb.append("<td>"+space+"『" + subjectidsss[0] + "』" + accnamess[i]
								+ "</td> \n");
					} 
				}
				
				sb.append("</tr>");
				
			}
		//	org.util.Debug.prtOut("出错:"+sb.toString());
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}
	
	public int SubjectProperty(String acc, String SubjectID) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select AccSign,count(AccSign) Acou from (select DISTINCT dataname,AccSign from c_accountAll a, c_accpkgsubject b where a.AccPackageID = '"
					+ acc
					+ "'  and b.AccPackageID = '"
					+ acc
					+ "' and a.subjectid='"
					+ SubjectID
					+ "' and a.subjectid=b.subjectid and b.isleaf=1 order by AccSign) a GROUP by AccSign";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int i = 0;
			while (rs.next()) {
				if (rs.getString("AccSign").equals("1")) {
					i += 1;
				}
				if (rs.getString("AccSign").equals("2")) {
					i += 2;
				}
			}
			return (i);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public boolean ExistsTable(String TabName) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean bool = false;
		try {
			String sql = "show TABLES  like '" + TabName + "'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				bool = true;
			}
			return bool;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	public int LowSubjectProperty(String acc, String SubjectID)
			throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select AccSign,count(AccSign) Acou from (select DISTINCT dataname,AccSign from c_accountAll a, c_accpkgsubject b where a.AccPackageID = '"
					+ acc
					+ "'  and b.AccPackageID = '"
					+ acc
					+ "' and a.subjectid like concat('"
					+ SubjectID
					+ "','%') and a.subjectid=b.subjectid and b.isleaf=1 order by AccSign) a GROUP by AccSign";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			int i = 0;
			while (rs.next()) {
				if (rs.getString("AccSign").equals("1")) {
					i += 1;
				}
				if (rs.getString("AccSign").equals("2")) {
					i += 2;
				}
			}
			return (i);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 0:非叶子与无外币和无数量的叶子
	 */
	public void CreateTable(String TabName) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "CREATE TABLE `"
					+ TabName
					+ "` ("
					+ " id int(10) NOT NULL auto_increment,"
					+ " autoid varchar(20) default NULL,"
					+ " voucherid varchar(20) default NULL,"
					// + " p1 varchar(10) default NULL,"
					// + " p2 varchar(10) default NULL,"
					// + " p3 varchar(10) default NULL,"
					+ " vchyear varchar(10) default NULL,"
					+ " vchmonth varchar(10) default NULL,"
					+ " vchdate varchar(20) default NULL,"
					+ " typeid varchar(20) default NULL,"
					+ " oldvoucherid varchar(50) default NULL,"

					+ " subjects text default NULL," // 对方科目
					+ " rsubject varchar(500) default NULL,"//发生科目
					
					+ " summary varchar(100) default NULL,"
					+ " debit varchar(20) default NULL,"
					+ " credit varchar(20) default NULL,"
					+ " dateRemain varchar(20) default NULL,"
					+ " yearRemain varchar(20) default NULL,"
					+ " PRIMARY KEY  (id)"
					+ ") ENGINE=MyISAM DEFAULT CHARSET=gbk";

			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

	}

	public void DataToTable(String TabName, String user, String proid,
			String Cid, String SubjectID, String BYear, String BMonth,
			String EYear, String EMonth) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ASFuntion CHF = new ASFuntion();
		String sql = "";
		try {

			/**
			 * 科目连续性
			 */
			String acc = Cid + EYear;
			AutoTokenService ats = new AutoTokenService(conn);
			String tokenid = CHF.showNull(ats.getTokenid(acc, SubjectID))
					.replaceAll("\\\\", "\\\\\\\\");
			String tokenid2 = CHF.showNull(ats.getTokenidLeaf(acc, SubjectID))
					.replaceAll("\\\\", "\\\\\\\\");

			int bDate = Integer.parseInt(BYear) * 12 + Integer.parseInt(BMonth);
			int eDate = Integer.parseInt(EYear) * 12 + Integer.parseInt(EMonth);
			org.util.Debug.prtOut("DataToTable11 = "
					+ String.valueOf(new java.util.Date(System
							.currentTimeMillis())));
			for (int ii = bDate; ii <= eDate; ii++) {
				String strSql = "";
				if(ii != bDate){
					strSql = " and not (DebitOcc=0 and CreditOcc =0 AND NOT EXISTS(SELECT 1 FROM (SELECT DISTINCT accpackageid,subjectid,SUBSTRING(vchdate,6,2) AS submonth FROM c_subjectentry a where Property like '1%' and a.tokenid in ("+ tokenid2+ ") and substring(a.vchdate,1,4)*12+substring(a.vchdate,6,2) = "+ ii +") b WHERE a.accpackageid = b.accpackageid AND a.subjectid = b.subjectid AND a.submonth = b.submonth) ) "; 
				}
//				strSql = " and not (DebitOcc=0 and CreditOcc =0) ";
				sql = "insert into `"+ TabName+ "` (autoid,voucherid,vchyear,vchmonth,vchdate,typeid,oldvoucherid,subjects,rsubject,summary,debit,credit) "
				+ " select * from ("
				+ " 	select autoid,voucherid,SUBSTRING(a.vchdate,1,4) vchyear,SUBSTRING(a.vchdate,6,2) vchmonth,a.VchDate,a.typeid, a.Oldvoucherid,"
				+ " 	REPLACE(REPLACE(if(Dirction*occurvalue<0,REPLACE(debitsubjects,',,',','),REPLACE(creditsubjects,',,',',')),',,',','),',,',',') subjects,"
				+ "		CONCAT(a.subjectname1,'(',a.subjectid,')') as rsubject,"
				+ " 	a.summary,IF(dirction=1,occurvalue,0.00) as Debit, IF(dirction=1,0.00,occurvalue) as Credit "
				+ " 	from c_subjectentry a  "
				+ " 	WHERE a.Property like '1%' "
				+ " 	and a.tokenid in ("+ tokenid2+ ") "
				+ " 	and substring(a.vchdate,1,4)*12+substring(a.vchdate,6,2) = " + ii
				+ " 	union "
				+ " 	select '' as autoid, '' as voucherid,SubYearMonth,LPAD(SubMonth,2,'0') vchmonth,concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'),'-97') as VchDate,'' as typeid, '' as Oldvoucherid,'',CONCAT(a.AccName,'(',a.subjectid,')') as rsubject,'>本月合计' as summary ,DebitOcc,CreditOcc "
				+ " 	from  c_account a "
				+ " 	WHERE 1=1 "
				+ " 	and a.tokenid = '"+ tokenid+ "' "
				+ " 	and a.SubYearMonth*12 + a.SubMonth ="+ ii + strSql
				+ " 	union "
				+ " 	select '' as autoid, '' as voucherid,SubYearMonth,LPAD(SubMonth,2,'0') vchmonth,concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'),'-98') as VchDate,'' as typeid, '' as Oldvoucherid,'',CONCAT(a.AccName,'(',a.subjectid,')') as rsubject,'>本年累计' as summary,DebitTotalOcc,CreditTotalOcc  "
				+ " 	from  c_account a "
				+ " 	WHERE 1=1 "
				+ " 	and a.tokenid = '"+ tokenid+ "' "
				+ " 	and a.SubYearMonth*12 + a.SubMonth ="+ ii + strSql
				+ " 	union "
				+ " 	select '' as autoid, '' as voucherid,SubYearMonth,LPAD(SubMonth,2,'0') vchmonth,concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'),'-00') as VchDate,'' as typeid, '' as Oldvoucherid,'',CONCAT(a.AccName,'(',a.subjectid,')') as rsubject,'>期初余额' as summary ,0.00 as Debit,0.00 as Credit "
				+ " 	from  c_account a "
				+ " 	WHERE 1=1 "
				+ " 	and a.tokenid = '"+ tokenid+ "' "
				+ " 	and a.SubYearMonth*12 + a.SubMonth ="+ ii + strSql
				+ " ) s order by VchDate,typeid,abs(Oldvoucherid),autoid ";
				org.util.Debug.prtOut("DataToTable sql = " + sql);
				ps = conn.prepareStatement(sql);
				ps.execute();
				DbUtil.close(ps);
			}

			org.util.Debug.prtOut("DataToTable22 = "
					+ String.valueOf(new java.util.Date(System
							.currentTimeMillis())));
			UpdateToTable1(TabName, Cid, SubjectID, BYear, BMonth, EYear, EMonth,"");
			org.util.Debug.prtOut("UpdateToTable33 = "
					+ String.valueOf(new java.util.Date(System
							.currentTimeMillis())));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	private void UpdateToTable1(String TabName, String Cid, String SubjectID,
			String BYear, String BMonth, String EYear, String EMonth,String strSql)
			throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ASFuntion CHF = new ASFuntion();
		String sql = "";
		try {
			
			String token = "",table = "c_account",strSql1 = "",strSql2 = "",strSql3 = "";
			if(strSql != null && !"".equals(strSql)){
				token = "F";
				table = "c_accountall";
				strSql1 = strSql.replaceAll("DataName", "rCurrency") ; 
				strSql2 = " and a.rCurrency = b.rCurrency ";
				strSql3 = " and a.rCurrency = b.DataName " ;
			}
			
			/**
			 * 科目连续性
			 */
			String acc = Cid + EYear;
			AutoTokenService ats = new AutoTokenService(conn);
			String tokenid = CHF.showNull(ats.getTokenid(acc, SubjectID)).replaceAll("\\\\", "\\\\\\\\");

			//org.util.Debug.prtOut("本币计算1 = "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));
			sql = "update `"+ TabName+ "` a join "+table+" b on AccPackageID like concat('"+ Cid+ "','%') "
				+ " and CONCAT(SubYearMonth,LPAD(SubMonth,2,'0'))>=concat('"+ BYear+ "','"+ BMonth+ "') "
				+ " and CONCAT(SubYearMonth,LPAD(SubMonth,2,'0'))<=concat('"+ EYear + "','"+ EMonth+ "') "
				+ " and tokenid = '"+ tokenid+ "' "
				+ strSql1 + strSql3
				+ " and submonth=vchmonth  and SubYearMonth=vchyear and substring(vchdate,9)='00' "
				+ " set dateRemain=(debitremain"+token+" + creditremain"+token+")";
			ps = conn.prepareStatement(sql);
			ps.execute();
			ps.close();
			//org.util.Debug.prtOut("本币计算2 = "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));
			sql = "update `"+ TabName+ "` a join "+table+" b on AccPackageID like concat('"+ Cid+ "','%') "
				+ " and SubYearMonth>='"+ BYear+ "' and SubYearMonth<='"+ EYear+ "' "
				+ " and SubMonth=1 "
				+ " and tokenid = '"+ tokenid+ "' "
				+ strSql1 + strSql3
				+ " and SubYearMonth=vchyear and substring(vchdate,9)='00' "
				+ " set yearRemain=(debitremain"+token+" + creditremain"+token+")";
			ps = conn.prepareStatement(sql);
			ps.execute();
			ps.close();
			//org.util.Debug.prtOut("本币计算3 = "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));
			sql = "update `"+ TabName+ "` a join `"+ TabName+ "` b on substring(a.vchdate,9)='00' and substring(b.vchdate,9)='97' "+strSql2+" and a.vchyear=b.vchyear and a.vchmonth=b.vchmonth set b.dateRemain=ROUND((a.dateRemain+b.debit-b.credit),2) ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			ps.close();
			//org.util.Debug.prtOut("本币计算4 = "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));
			sql = "update `"+ TabName+ "` a join `"+ TabName+ "` b on substring(a.vchdate,9)='00' and substring(b.vchdate,9)='98' "+strSql2+" and a.vchyear=b.vchyear and a.vchmonth=b.vchmonth set b.dateRemain=ROUND((a.yearRemain+b.debit-b.credit),2) ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			//org.util.Debug.prtOut("本币计算5 = "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));
			int i = 1;
			String vi = "@i_senc" + this.getRandom();
			ps.addBatch("set " + vi + ":=0.00");
			ps.executeBatch();
			sql = "UPDATE `" + TabName+ "` a set dateremain= if(concat(dateremain)>''," + vi+ ":=dateremain,if(concat(" + vi + ")>''," + vi+ ":=round(" + vi + "+debit-credit,2)," + vi+ ":=dateremain)) ";
			//org.util.Debug.prtOut("sql:" + sql);
			ps = conn.prepareStatement(sql);
			ps.executeUpdate();
			ps.addBatch("set " + vi + ":=null");
			ps.executeBatch();
			ps.clearBatch();

			//org.util.Debug.prtOut("本币计算0 = "+ String.valueOf(new java.util.Date(System.currentTimeMillis())) + " Num = " + i);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public Map SubjectCurrency(String acc, String SubjectID) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map map = new HashMap();
		try {
			String sql = "select DISTINCT dataname,AccSign from c_accountAll a, c_accpkgsubject b where a.AccPackageID = '"
					+ acc
					+ "'  and b.AccPackageID = '"
					+ acc
					+ "' and a.subjectid like concat('"
					+ SubjectID
					+ "','%') and a.subjectid=b.subjectid and b.isleaf=1 and a.AccSign=1 order by AccSign";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			String str = "Curr";
			int i = 1;
			while (rs.next()) {
				map.put(str + String.valueOf(i), rs.getString("dataname"));
				i++;
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	/**
	 * 1:叶子有外币无数量
	 * 
	 * @param TabName
	 * @param map
	 * @throws Exception
	 */
	public void CreateTable(String TabName, Map map) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "CREATE TABLE `"
					+ TabName
					+ "` ("
					+ " id int(10) NOT NULL auto_increment,"
					+ " autoid varchar(20) default NULL,"
					+ " voucherid varchar(20) default NULL,"
					// + " p1 varchar(10) default NULL,"
					// + " p2 varchar(10) default NULL,"
					+ " vchyear varchar(10) default NULL,"
					+ " vchmonth varchar(10) default NULL,"
					+ " vchdate varchar(20) default NULL,"
					+ " typeid varchar(20) default NULL,"
					+ " oldvoucherid varchar(50) default NULL,"

					+ " subjects text default NULL," // 对方科目
					+ " summary varchar(100) default NULL,"
					+ " rsubject varchar(500) default NULL,"//发生科目
					+ " rCurrency varchar(500) default NULL,";//币种或单位
			Set coll = map.keySet();
			int i = 1;
			for (Iterator iter = coll.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				
				if(this.isSplitBool()) {
					sql += "debitrate" + i + " varchar(20) default NULL,debitCurr" + i + " varchar(20) default NULL,";
					sql += "creditrate" + i + " varchar(20) default NULL,creditCurr" + i + " varchar(20) default NULL,";
					sql += "dRemainCurr" + i + " varchar(20) default NULL,yRemainCurr"+ i + " varchar(20) default NULL,";
					break;
				}
				
				sql += "debitrate" + i + " varchar(20) default NULL,debit"+ key + " varchar(20) default NULL,";
				sql += "creditrate" + i + " varchar(20) default NULL,credit"+ key + " varchar(20) default NULL,";
				sql += "dRemain" + key + " varchar(20) default NULL,yRemain"+ key + " varchar(20) default NULL,";
				i++;
			}
			sql += " debit varchar(20) default NULL,";
			sql += "credit varchar(20) default NULL,";
			sql += " dateRemain varchar(20) default NULL, yearRemain varchar(20) default NULL,PRIMARY KEY  (id)) ENGINE=MyISAM DEFAULT CHARSET=gbk ";
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}

	public void DataToTable(Map map, String TabName, String user, String proid,
			String Cid, String SubjectID, String BYear, String BMonth,
			String EYear, String EMonth) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ASFuntion CHF = new ASFuntion();
		String sql = "";
		try {
			/**
			 * 科目连续性
			 */
			String acc = Cid + EYear;
			AutoTokenService ats = new AutoTokenService(conn);
			String tokenid = CHF.showNull(ats.getTokenid(acc, SubjectID)).replaceAll("\\\\", "\\\\\\\\");
			String tokenid2 = CHF.showNull(ats.getTokenidLeaf(acc, SubjectID)).replaceAll("\\\\", "\\\\\\\\");

			Set coll = map.keySet();

			int bDate = Integer.parseInt(BYear) * 12 + Integer.parseInt(BMonth);
			int eDate = Integer.parseInt(EYear) * 12 + Integer.parseInt(EMonth);
			
			if(this.isSplitBool()){
				//用来标志：明细账多外币时，每个外币分开显示
				
				//生成本位币的明细账
				
				//生成外币和数量的明细账
				
				String s1 = "",s2 = "",s3 = "",s4 = "",s5 = "",s6 = "";
				int i = 1;
				for (Iterator iter = coll.iterator(); iter.hasNext();) {
					String key = (String) iter.next();
					String value = (String) map.get(key);
					
					s1 = "debitrate" + i + ",debitCurr" + i + ",creditrate" + i+ ",creditCurr" + i + ",";
					
					s2 = "if(Currency='" + value + "',CurrRate,'') debitrate" + i + ",";
					s2 += "if(Currency='" + value + "',IF(dirction=1,CurrValue,0.00),'') debitCurr" + i + ",";
					s2 += "if(Currency='" + value + "',CurrRate,'') creditrate" + i + ",";
					s2 += "if(Currency='" + value + "',IF(dirction=1,0.00,CurrValue),'') creditCurr" + i + ",";

					s3 = "'' DebitRate" + i + ",a.DebitOcc,'' CreditRate" + i + ", a.CreditOcc,";
					
					s5 = " and a.DataName ='" + value + "' ";
					
					s6 = " and a.Currency='" + value + "'"; 
					
					for (int ii = bDate; ii <= eDate; ii++) {
						
						String strSql = "";
						if(ii != bDate){
							strSql = " and not (a.DebitOcc=0 and a.CreditOcc =0 and a.DebitOccF=0 and a.CreditOccF =0 AND NOT EXISTS(SELECT 1 FROM (SELECT DISTINCT accpackageid,subjectid,SUBSTRING(vchdate,6,2) AS submonth FROM c_subjectentry a where Property like '1%' and a.tokenid in ("+ tokenid2+ ") and substring(a.vchdate,1,4)*12+substring(a.vchdate,6,2) = "+ ii +") b WHERE a.accpackageid = b.accpackageid AND a.subjectid = b.subjectid AND a.submonth = b.submonth) ) "; 
						}
						
						String s31 = s3 ; 
						sql = " insert into `"+ TabName+ "` (autoid,voucherid,vchyear,vchmonth,vchdate,typeid,oldvoucherid,subjects,summary,rsubject,rCurrency,"+ s1+ "debit,credit) "
						+ " select * from ("
						+ " select autoid,voucherid,SUBSTRING(a.vchdate,1,4) vchyear,SUBSTRING(a.vchdate,6,2) vchmonth,a.VchDate,a.typeid,a.Oldvoucherid,"
						+ " REPLACE(REPLACE(REPLACE(concat(REPLACE(debitsubjects,',,',','),REPLACE(creditsubjects,',,',',')),',,',','),concat(',',a.SubjectID,','),','),',,',',') subjects,"
						+ " a.summary,CONCAT(a.subjectname1,'(',a.subjectid,')') as rsubject,Currency as rCurrency," + s2 
						+ " IF(dirction=1,occurvalue,0.00) as Debit,IF(dirction=1,0.00,occurvalue) as Credit "
						+ " from c_subjectentry a  "
						+ " WHERE a.Property like '1%' "
						+ " and a.tokenid in (" + tokenid2 + ") "
						+ " and substring(a.vchdate,1,4)*12+substring(a.vchdate,6,2) = " + ii + s6
						
						+ " union "
						+ " select '' as autoid, '' as voucherid,a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-97') as VchDate,'' as typeid, '' as Oldvoucherid,'','>本月合计' as summary ,CONCAT(a.AccName,'(',a.subjectid,')') as rsubject,a.DataName as rCurrency,"+ s3 + " a.DebitOccF,a.CreditOccF " 
						+ " from c_accountall a " 
						+ " WHERE a.tokenid = '" + tokenid + "' " + strSql
						+ " and a.SubYearMonth*12 + a.SubMonth =" + ii + s5;

						s31 = s3.replaceAll("DebitOcc", "DebitTotalOcc").replaceAll( "CreditOcc", "CreditTotalOcc");
						sql += " union select '' as autoid, '' as voucherid, a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-98') as VchDate,'' as typeid, '' as Oldvoucherid,'','>本年累计' as summary ,CONCAT(a.AccName,'(',a.subjectid,')') as rsubject,a.DataName as rCurrency,"+ s31+ " a.DebitTotalOccF,a.CreditTotalOccF "
							+ " from c_accountall a "
							+ " WHERE a.tokenid = '" + tokenid + "' " + strSql
							+ " and a.SubYearMonth*12 + a.SubMonth =" + ii + s5;
	
						s31 = "";
						s31 += "'' debitRate" + i + ",0.00 debitCurr" + i + ",'' CreditRate" + i + ",0.00 CreditCurr" + i + ",";
						
						sql += " union select '' as autoid, '' as voucherid, a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-00') as VchDate,'' as typeid, '' as Oldvoucherid,'','>期初余额' as summary ,CONCAT(a.AccName,'(',a.subjectid,')') as rsubject,a.DataName as rCurrency,"+ s31+ " 0.00 Debit,0.00 Credit "
							+ " from  c_accountall a "
							+ " WHERE a.tokenid = '" + tokenid + "' " + strSql
							+ " and a.SubYearMonth*12 + a.SubMonth =" + ii + s5
							+ " ) s order by VchDate,typeid,abs(Oldvoucherid),autoid ";
						System.out.println("DataToTable Map sql = " + sql);
						ps = conn.prepareStatement(sql);
						ps.execute();
						
					}
					
					UpdateToTable1(TabName, Cid, SubjectID, BYear, BMonth, EYear, EMonth,s5);
					UpdateToTable(map, TabName, Cid, SubjectID, BYear, BMonth, EYear, EMonth);
					
					
				}
				
			}else{ //else isSplitBool 
				//用来标志：明细账多外币时，每个外币分开显示
				for (int ii = bDate; ii <= eDate; ii++) {
					String s1 = "";
					String s2 = "";
					String s3 = "";
					String s4 = "";
					String s5 = "";
					int i = 1;

					String strSql = "";
					if(ii != bDate){
						strSql = " and not (a.DebitOcc=0 and a.CreditOcc =0 AND NOT EXISTS(SELECT 1 FROM (SELECT DISTINCT accpackageid,subjectid,SUBSTRING(vchdate,6,2) AS submonth FROM c_subjectentry a where Property like '1%' and a.tokenid in ("+ tokenid2+ ") and substring(a.vchdate,1,4)*12+substring(a.vchdate,6,2) = "+ ii +") b WHERE a.accpackageid = b.accpackageid AND a.subjectid = b.subjectid AND a.submonth = b.submonth) ) "; 
					}
					
					for (Iterator iter = coll.iterator(); iter.hasNext();) {
						String key = (String) iter.next();
						String value = (String) map.get(key);

						s1 += "debitrate" + i + ",debit" + key + ",creditrate" + i+ ",credit" + key + ",";
						
						s2 += "if(Currency='" + value + "',CurrRate,'') debitrate" + i + ",";
						s2 += "if(Currency='" + value + "',IF(dirction=1,CurrValue,0.00),'') debit" + key + ",";
						s2 += "if(Currency='" + value + "',CurrRate,'') creditrate" + i + ",";
						s2 += "if(Currency='" + value + "',IF(dirction=1,0.00,CurrValue),'') credit" + key + ",";

						s3 += "'' DebitRate" + i + ",a" + i + ".DebitOcc,'' CreditRate" + i + ", a" + i + ".CreditOcc,";

						s4 += "c_accountall a" + i + ",";

						s5 += " and a" + i + ".tokenid = '" + tokenid + "' ";
						s5 += " and a" + i + ".SubYearMonth * 12 + a" + i + ".SubMonth =" + ii;
						s5 += " and a" + i + ".DataName ='" + value + "' ";
						s5 += " and a.SubYearMonth = a" + i + ".SubYearMonth";
						s5 += " and a.SubMonth = a" + i + ".SubMonth";
						s5 += " and a.AccPackageID= a" + i + ".AccPackageID";
						i++;
					}

					sql = " insert into `"+ TabName+ "` (autoid,voucherid,vchyear,vchmonth,vchdate,typeid,oldvoucherid,subjects,summary,rsubject,rCurrency,"+ s1+ "debit,credit) "
						+ " select * from ("
						+ " select autoid,voucherid,SUBSTRING(a.vchdate,1,4) vchyear,SUBSTRING(a.vchdate,6,2) vchmonth,a.VchDate,a.typeid,a.Oldvoucherid,"
						+ " REPLACE(REPLACE(REPLACE(concat(REPLACE(debitsubjects,',,',','),REPLACE(creditsubjects,',,',',')),',,',','),concat(',',a.SubjectID,','),','),',,',',') subjects,"
						+ " a.summary,CONCAT(a.subjectname1,'(',a.subjectid,')') as rsubject,Currency as rCurrency," + s2 
						+ " IF(dirction=1,occurvalue,0.00) as Debit,IF(dirction=1,0.00,occurvalue) as Credit "
						+ " from c_subjectentry a  "
						+ " WHERE a.Property like '1%' "
						+ " and a.tokenid in (" + tokenid2 + ") "
						+ " and substring(a.vchdate,1,4)*12+substring(a.vchdate,6,2) = " + ii
						+ " union "
						+ " select '' as autoid, '' as voucherid,a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-97') as VchDate,'' as typeid, '' as Oldvoucherid,'','>本月合计' as summary ,CONCAT(a.AccName,'(',a.subjectid,')') as rsubject,'',"+ s3 + " a.DebitOcc,a.CreditOcc " 
						+ " from " + s4 + " c_account a " 
						+ " WHERE a.tokenid = '" + tokenid + "' " + strSql
						+ " and a.SubYearMonth*12 + a.SubMonth =" + ii + s5;

					s3 = s3.replaceAll("DebitOcc", "DebitTotalOcc").replaceAll( "CreditOcc", "CreditTotalOcc");
					sql += " union select '' as autoid, '' as voucherid, a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-98') as VchDate,'' as typeid, '' as Oldvoucherid,'','>本年累计' as summary ,CONCAT(a.AccName,'(',a.subjectid,')') as rsubject,'',"+ s3+ " a.DebitTotalOcc,a.CreditTotalOcc "
						+ " from " + s4 + " c_account a "
						+ " WHERE a.tokenid = '" + tokenid + "' " + strSql
						+ " and a.SubYearMonth*12 + a.SubMonth =" + ii + s5;

					s3 = "";
					for (Iterator iter = coll.iterator(); iter.hasNext();) {
						String key = (String) iter.next();
						s3 += "'' debitRate" + i + ",0.00 debit" + key + ",'' CreditRate" + i + ",0.00 Credit" + key + ",";
					}
					sql += " union select '' as autoid, '' as voucherid, a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-00') as VchDate,'' as typeid, '' as Oldvoucherid,'','>期初余额' as summary ,CONCAT(a.AccName,'(',a.subjectid,')') as rsubject,'',"+ s3+ " 0.00 Debit,0.00 Credit "
						+ " from " + s4 + " c_account a "
						+ " WHERE a.tokenid = '" + tokenid + "' " + strSql
						+ " and a.SubYearMonth*12 + a.SubMonth =" + ii + s5
						+ " ) s order by VchDate,typeid,abs(Oldvoucherid),autoid ";
					//org.util.Debug.prtOut("DataToTable Map sql = " + sql);
					ps = conn.prepareStatement(sql);
					ps.execute();

				}

				//org.util.Debug.prtOut("DataToTable Map = " + String.valueOf(new java.util.Date(System .currentTimeMillis())));
				UpdateToTable1(TabName, Cid, SubjectID, BYear, BMonth, EYear, EMonth,"");
				UpdateToTable(map, TabName, Cid, SubjectID, BYear, BMonth, EYear, EMonth);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	public void FubiDataToTable(Map map, String TabName, String user, String proid,
			String Cid, String SubjectID, String BYear, String BMonth,
			String EYear, String EMonth,String crt) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ASFuntion CHF = new ASFuntion();
		String sql = "";
		try {
			/**
			 * 科目连续性
			 */
			String acc = Cid + EYear;
			AutoTokenService ats = new AutoTokenService(conn);
			String tokenid = CHF.showNull(ats.getTokenid(acc, SubjectID))
					.replaceAll("\\\\", "\\\\\\\\");
			String tokenid2 = CHF.showNull(ats.getTokenidLeaf(acc, SubjectID))
					.replaceAll("\\\\", "\\\\\\\\");

			Set coll = map.keySet();

			int bDate = Integer.parseInt(BYear) * 12 + Integer.parseInt(BMonth);
			int eDate = Integer.parseInt(EYear) * 12 + Integer.parseInt(EMonth);
			for (int ii = bDate; ii <= eDate; ii++) {
				String s1 = "";
				String s2 = "";
				String s3 = "";
				String s4 = "";
				String s5 = "";
				int i = 1;
				for (Iterator iter = coll.iterator(); iter.hasNext();) {
					String key = (String) iter.next();
					String value = (String) map.get(key);

					s1 += "debitrate" + i + ",debit" + key + ",creditrate" + i
							+ ",credit" + key + ",";

					s2 += "if(Currency='" + value + "',CurrRate,'') debitrate"
							+ i + ",";
					s2 += "if(Currency='" + value
							+ "',IF(dirction=1,CurrValue,0.00),'') debit" + key
							+ ",";
					s2 += "if(Currency='" + value + "',CurrRate,'') creditrate"
							+ i + ",";
					s2 += "if(Currency='" + value
							+ "',IF(dirction=1,0.00,CurrValue),'') credit"
							+ key + ",";

					s3 += "'' DebitRate" + i + ",a" + i
							+ ".DebitOcc,'' CreditRate" + i + ", a" + i
							+ ".CreditOcc,";

					s4 += "c_accountall a" + i + ",";

					s5 += " and a" + i + ".tokenid = '" + tokenid + "' ";
					s5 += " and a" + i + ".SubYearMonth * 12 + a" + i
							+ ".SubMonth =" + ii;
					s5 += " and a" + i + ".DataName ='" + value + "' ";
					s5 += " and a.SubYearMonth = a" + i + ".SubYearMonth";
					s5 += " and a.SubMonth = a" + i + ".SubMonth";
					s5 += " and a.AccPackageID= a" + i + ".AccPackageID";
					i++;
				}
			
				
				sql = " insert into `"
						+ TabName
						+ "` (autoid,voucherid,vchyear,vchmonth,vchdate,typeid,oldvoucherid,subjects,summary,"
						+ s1
						+ "debit,credit) "
						+ " select * from ("
						+ " select autoid,voucherid,SUBSTRING(a.vchdate,1,4) vchyear,SUBSTRING(a.vchdate,6,2) vchmonth,a.VchDate,a.typeid,a.Oldvoucherid,"
						+

						" REPLACE(REPLACE(REPLACE(concat(REPLACE(debitsubjects,',,',','),REPLACE(creditsubjects,',,',',')),',,',','),concat(',',a.SubjectID,','),','),',,',',') subjects,"
						+

						" a.summary,"
						+ s2
						+ " IF(dirction=1,occurvalue,0.00) as Debit,IF(dirction=1,0.00,occurvalue) as Credit "
						+ " from c_subjectentry a  "
						+
						// " left join z_voucherspotcheck y on
						// a.voucherid=y.vchid and y.projectid='"+ proid+ "' and
						// y.createor='"+ user+ "' " +
						// " left join z_question z on a.voucherid=z.vchid and
						// z.projectid='"+ proid+ "' and z.createor='"+ user+ "'
						// " +
						" WHERE a.Property like '1%' "
						+
						// " and a.SubjectID like concat('"+ SubjectID+ "','%')
						// " +
						" and a.tokenid in ("
						+ tokenid2
						+ ") and Currency='"+crt+"'"
						+

						" and substring(a.vchdate,1,4)*12+substring(a.vchdate,6,2) = "
						+ ii
						+ " union "
						+ " select '' as autoid, '' as voucherid,a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-97') as VchDate,'' as typeid, '' as Oldvoucherid,'','>本月合计' as summary ,"
						+ s3 + " a.DebitOcc,a.CreditOcc " + " from " + s4
						+ " c_accountall a " + " WHERE a.tokenid = '" + tokenid
						+ "' " + " and a.SubYearMonth*12 + a.SubMonth =" + ii
						+ s5;

				s3 = s3.replaceAll("DebitOcc", "DebitTotalOcc").replaceAll(
						"CreditOcc", "CreditTotalOcc");
				sql += " union select '' as autoid, '' as voucherid, a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-98') as VchDate,'' as typeid, '' as Oldvoucherid,'','>本年累计' as summary ,"
					+ s3
					+ " a.DebitTotalOcc,a.CreditTotalOcc "
					+ " from "
					+ s4
					+ " c_accountall a "
					+ " WHERE a.tokenid = '"
					+ tokenid
					+ "' "
					+ " and a.SubYearMonth*12 + a.SubMonth =" + ii + s5;

				s3 = "";
				for (Iterator iter = coll.iterator(); iter.hasNext();) {
					String key = (String) iter.next();
					s3 += "'' debitRate" + i + ",0.00 debit" + key
							+ ",'' CreditRate" + i + ",0.00 Credit" + key + ",";
				}
				sql += " union select '' as autoid, '' as voucherid, a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-00') as VchDate,'' as typeid, '' as Oldvoucherid,'','>期初余额' as summary ,"
						+ s3
						+ " 0.00 Debit,0.00 Credit "
						+ " from "
						+ s4
						+ " c_accountall a "
						+ " WHERE a.tokenid = '"
						+ tokenid
						+ "' "
						+ " and a.SubYearMonth*12 + a.SubMonth ="
						+ ii
						+ s5
						+ " ) s order by VchDate,typeid,abs(Oldvoucherid),autoid ";
				//org.util.Debug.prtOut("DataToTable Map sql = " + sql);
				ps = conn.prepareStatement(sql);
				ps.execute();

			}

			//org.util.Debug.prtOut("DataToTable Map = "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));
			UpdateToTable1(TabName, Cid, SubjectID, BYear, BMonth, EYear, EMonth,"");
			UpdateToTable(map, TabName, Cid, SubjectID, BYear, BMonth, EYear,
					EMonth);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}
	
	private void UpdateToTable(Map map, String TabName, String Cid,
			String SubjectID, String BYear, String BMonth, String EYear,
			String EMonth) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ASFuntion CHF = new ASFuntion();
		String sql = "";
		try {

			/**
			 * 科目连续性
			 */
			String acc = Cid + EYear;
			AutoTokenService ats = new AutoTokenService(conn);
			String tokenid = CHF.showNull(ats.getTokenid(acc, SubjectID)).replaceAll("\\\\", "\\\\\\\\");

			Set coll = map.keySet();
			for (Iterator iter = coll.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				String value = (String) map.get(key);
				
				
				String strSql1 = "",strSql2 = "";
				if(this.isSplitBool()){
					key = "Curr1";
					strSql1 = " and a.rCurrency = '"+value+"' ";
					strSql2 = " and a.rCurrency = b.rCurrency ";
					
				}
				
				//org.util.Debug.prtOut("外币计算1 = "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));
				sql = "update `"+ TabName+ "` a join c_accountall b on AccPackageID like concat('"+ Cid+ "','%') and CONCAT(SubYearMonth,LPAD(SubMonth,2,'0'))>=concat('"+ BYear+ "','"+ BMonth+ "') and CONCAT(SubYearMonth,LPAD(SubMonth,2,'0'))<=concat('"+ EYear+ "','"+ EMonth+ "') and tokenid='"+ tokenid+ "' "+strSql1+" and DataName='"+ value+ "' and submonth=vchmonth  and SubYearMonth=vchyear and substring(vchdate,9)='00' set  dRemain"+ key + "=(debitremain+creditremain)";
				ps = conn.prepareStatement(sql);
				ps.execute();
				//org.util.Debug.prtOut("外币计算2 = "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));
				sql = "update `"+ TabName+ "` a join c_accountall b on AccPackageID like concat('"+ Cid+ "','%') and SubYearMonth>='"+ BYear+ "' and SubYearMonth<='"+ EYear+ "' and SubMonth=1 and tokenid='"+ tokenid+ "' "+strSql1+" and DataName='"+ value+ "' and SubYearMonth=vchyear and substring(vchdate,9)='00' set  yRemain"+ key + "=(debitremain+creditremain)";
				ps = conn.prepareStatement(sql);
				ps.execute();
				//org.util.Debug.prtOut("外币计算3 = "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));
				sql = "update `"+ TabName+ "` a join `"+ TabName+ "` b on substring(a.vchdate,9)='00' and substring(b.vchdate,9)='97' "+strSql2+" and a.vchyear=b.vchyear and a.vchmonth=b.vchmonth set b.dRemain"+ key + "=ROUND((a.dRemain" + key + " + b.debit" + key+ " - b.credit" + key + "),2) ";
				ps = conn.prepareStatement(sql);
				ps.execute();
				//org.util.Debug.prtOut("外币计算4 = "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));
				sql = "update `"+ TabName+ "` a join `"+ TabName+ "` b on substring(a.vchdate,9)='00' and substring(b.vchdate,9)='98' "+strSql2+" and a.vchyear=b.vchyear and a.vchmonth=b.vchmonth set b.dRemain"+ key + "=ROUND((a.yRemain" + key + " + b.debit" + key+ " - b.credit" + key + " ),2) ";
				//org.util.Debug.prtOut("sql:=|" + sql);
				ps = conn.prepareStatement(sql);
				ps.execute();
				//org.util.Debug.prtOut("外币计算5 = "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));

				String vi = "@i_senc" + this.getRandom();
				ps.addBatch("set " + vi + ":=0.00");
				ps.executeBatch();
				sql = "UPDATE `" + TabName + "` a set dRemain" + key+ "= if(concat(dRemain" + key + ")>''," + vi+ ":=dRemain" + key + ",if(concat(" + vi + ")>''," + vi+ ":=round(" + vi + "+debit" + key + "-credit" + key+ ",2)," + vi + ":=dRemain" + key + ")) ";
				ps = conn.prepareStatement(sql);
				//org.util.Debug.prtOut("sql:" + sql);
				ps.executeUpdate();
				ps.addBatch("set " + vi + ":=null");
				ps.executeBatch();
				ps.clearBatch();

				//org.util.Debug.prtOut("外币计算6 = "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));
			}
			//org.util.Debug.prtOut("外币计算0 = "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	public Map SubjectUnitName(String acc, String SubjectID) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map map = new HashMap();
		try {
			String sql = "select DISTINCT dataname,AccSign from c_accountAll a, c_accpkgsubject b where a.AccPackageID = '"
					+ acc
					+ "'  and b.AccPackageID = '"
					+ acc
					+ "' and a.subjectid like concat('"
					+ SubjectID
					+ "','%') and a.subjectid=b.subjectid and b.isleaf=1 and a.AccSign=2 order by AccSign";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			String str = "Unit";
			int i = 1;
			while (rs.next()) {
				map.put(str + String.valueOf(i), rs.getString("dataname"));
				i++;
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	public void CreateUnitTable(String TabName, Map map) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "CREATE TABLE `" + TabName + "` ("
					+ " id int(10) NOT NULL auto_increment,"
					+ " autoid varchar(20) default NULL,"
					+ " voucherid varchar(20) default NULL,"
					+ " p1 varchar(10) default NULL,"
					+ " p2 varchar(10) default NULL,"
					+ " vchyear varchar(10) default NULL,"
					+ " vchmonth varchar(10) default NULL,"
					+ " vchdate varchar(20) default NULL,"
					+ " typeid varchar(20) default NULL,"
					+ " oldvoucherid varchar(50) default NULL,"

					+ " subjects text default NULL," // 对方科目
					+ " rsubject varchar(500) default NULL,"//发生科目
					+ " rUnitName varchar(500) default NULL,"//币种或单位
					+ " summary varchar(100) default NULL,";
			Set coll = map.keySet();
			int i = 1;
			for (Iterator iter = coll.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				
				if(this.isSplitBool()) {
					sql += " rCurrency varchar(500) default NULL,";//币种或单位
					sql += "debitrate" + i + " varchar(20) default NULL,debitCurr" + i + " varchar(20) default NULL,";
					sql += "creditrate" + i + " varchar(20) default NULL,creditCurr" + i + " varchar(20) default NULL,";
					sql += "dRemainCurr" + i + " varchar(20) default NULL,yRemainCurr"+ i + " varchar(20) default NULL,";
					break;
				}
				
				sql += "debitPrice" + i + " varchar(20) default NULL,debit"+ key + " varchar(20) default NULL,";
				sql += "creditPrice" + i + " varchar(20) default NULL,credit"+ key + " varchar(20) default NULL,";
				sql += "dRemain" + key + " varchar(20) default NULL,yRemain"+ key + " varchar(20) default NULL,";
				i++;
			}
			sql += " debit varchar(20) default NULL,";
			sql += "credit varchar(20) default NULL,";
			sql += " dateRemain varchar(20) default NULL, yearRemain varchar(20) default NULL,PRIMARY KEY  (id)) ENGINE=MyISAM DEFAULT CHARSET=gbk ";
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}

	public void DataToUnitTable(Map map, String TabName, String user,
			String proid, String Cid, String SubjectID, String BYear,
			String BMonth, String EYear, String EMonth) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ASFuntion CHF = new ASFuntion();
		String sql = "";
		try {

			/**
			 * 科目连续性
			 */
			String acc = Cid + EYear;
			AutoTokenService ats = new AutoTokenService(conn);
			String tokenid = CHF.showNull(ats.getTokenid(acc, SubjectID)).replaceAll("\\\\", "\\\\\\\\");
			String tokenid2 = CHF.showNull(ats.getTokenidLeaf(acc, SubjectID)).replaceAll("\\\\", "\\\\\\\\");

			Set coll = map.keySet();

			int bDate = Integer.parseInt(BYear) * 12 + Integer.parseInt(BMonth);
			int eDate = Integer.parseInt(EYear) * 12 + Integer.parseInt(EMonth);
			
			if(this.isSplitBool()){
				//用来标志：明细账多外币时，每个外币分开显示
				
				//生成本位币的明细账
				
				//生成外币和数量的明细账
				
				String s1 = "",s2 = "",s3 = "",s4 = "",s5 = "",s6 = "";
				int i = 1;
				for (Iterator iter = coll.iterator(); iter.hasNext();) {
					String key = (String) iter.next();
					String value = (String) map.get(key);
					
					s1 = "debitrate" + i + ",debitCurr" + i + ",creditrate" + i+ ",creditCurr" + i + ",";
					
					s2 = "if(UnitName='" + value + "',UnitPrice,'') debitrate" + i + ",";
					s2 += "if(UnitName='" + value + "',IF(dirction=1,Quantity,0.00),'') debitCurr" + i + ",";
					s2 += "if(UnitName='" + value + "',UnitPrice,'') creditrate" + i + ",";
					s2 += "if(UnitName='" + value + "',IF(dirction=1,0.00,Quantity),'') creditCurr" + i + ",";

					s3 = "'' DebitRate" + i + ",a.DebitOcc,'' CreditRate" + i + ", a.CreditOcc,";
					
					s5 = " and a.DataName ='" + value + "' ";
					
					s6 = " and a.UnitName='" + value + "'"; 
					
					for (int ii = bDate; ii <= eDate; ii++) {
						
						String strSql = "";
						if(ii != bDate){
							strSql = " and not (a.DebitOcc=0 and a.CreditOcc =0 and a.DebitOccF=0 and a.CreditOccF =0 AND NOT EXISTS(SELECT 1 FROM (SELECT DISTINCT accpackageid,subjectid,SUBSTRING(vchdate,6,2) AS submonth FROM c_subjectentry a where Property like '1%' and a.tokenid in ("+ tokenid2+ ") and substring(a.vchdate,1,4)*12+substring(a.vchdate,6,2) = "+ ii +") b WHERE a.accpackageid = b.accpackageid AND a.subjectid = b.subjectid AND a.submonth = b.submonth) ) "; 
						}
						
						String s31 = s3 ; 
						sql = " insert into `"+ TabName+ "` (autoid,voucherid,vchyear,vchmonth,vchdate,typeid,oldvoucherid,subjects,summary,rsubject,rCurrency,"+ s1+ "debit,credit) "
						+ " select * from ("
						+ " select autoid,voucherid,SUBSTRING(a.vchdate,1,4) vchyear,SUBSTRING(a.vchdate,6,2) vchmonth,a.VchDate,a.typeid,a.Oldvoucherid,"
						+ " REPLACE(REPLACE(REPLACE(concat(REPLACE(debitsubjects,',,',','),REPLACE(creditsubjects,',,',',')),',,',','),concat(',',a.SubjectID,','),','),',,',',') subjects,"
						+ " a.summary,CONCAT(a.subjectname1,'(',a.subjectid,')') as rsubject,UnitName as rCurrency," + s2 
						+ " IF(dirction=1,occurvalue,0.00) as Debit,IF(dirction=1,0.00,occurvalue) as Credit "
						+ " from c_subjectentry a  "
						+ " WHERE a.Property like '1%' "
						+ " and a.tokenid in (" + tokenid2 + ") "
						+ " and substring(a.vchdate,1,4)*12+substring(a.vchdate,6,2) = " + ii + s6
						
						+ " union "
						+ " select '' as autoid, '' as voucherid,a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-97') as VchDate,'' as typeid, '' as Oldvoucherid,'','>本月合计' as summary ,CONCAT(a.AccName,'(',a.subjectid,')') as rsubject,a.DataName as rCurrency,"+ s3 + " a.DebitOccF,a.CreditOccF " 
						+ " from c_accountall a " 
						+ " WHERE a.tokenid = '" + tokenid + "' " + strSql
						+ " and a.SubYearMonth*12 + a.SubMonth =" + ii + s5;

						s31 = s3.replaceAll("DebitOcc", "DebitTotalOcc").replaceAll( "CreditOcc", "CreditTotalOcc");
						sql += " union select '' as autoid, '' as voucherid, a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-98') as VchDate,'' as typeid, '' as Oldvoucherid,'','>本年累计' as summary ,CONCAT(a.AccName,'(',a.subjectid,')') as rsubject,a.DataName as rCurrency,"+ s31+ " a.DebitTotalOccF,a.CreditTotalOccF "
							+ " from c_accountall a "
							+ " WHERE a.tokenid = '" + tokenid + "' " + strSql
							+ " and a.SubYearMonth*12 + a.SubMonth =" + ii + s5;
	
						s31 = "";
						s31 += "'' debitRate" + i + ",0.00 debitCurr" + i + ",'' CreditRate" + i + ",0.00 CreditCurr" + i + ",";
						
						sql += " union select '' as autoid, '' as voucherid, a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-00') as VchDate,'' as typeid, '' as Oldvoucherid,'','>期初余额' as summary ,CONCAT(a.AccName,'(',a.subjectid,')') as rsubject,a.DataName as rCurrency,"+ s31+ " 0.00 Debit,0.00 Credit "
							+ " from  c_accountall a "
							+ " WHERE a.tokenid = '" + tokenid + "' " + strSql
							+ " and a.SubYearMonth*12 + a.SubMonth =" + ii + s5
							+ " ) s order by VchDate,typeid,abs(Oldvoucherid),autoid ";
						//org.util.Debug.prtOut("DataToTable Map sql = " + sql);
						ps = conn.prepareStatement(sql);
						ps.execute();
						
					}
					
					UpdateToTable1(TabName, Cid, SubjectID, BYear, BMonth, EYear, EMonth,s5);
					UpdateToTable(map, TabName, Cid, SubjectID, BYear, BMonth, EYear, EMonth);
					
					
				}
				
			}else{
				for (int ii = bDate; ii <= eDate; ii++) {
					String s1 = "";
					String s2 = "";
					String s3 = "";
					String s4 = "";
					String s5 = "";
					// Set coll = map.keySet();
					int i = 1;
					String strSql = "";
					if(ii != bDate){
						strSql = " and not (a.DebitOcc=0 and a.CreditOcc =0 AND NOT EXISTS(SELECT 1 FROM (SELECT DISTINCT accpackageid,subjectid,SUBSTRING(vchdate,6,2) AS submonth FROM c_subjectentry a where Property like '1%' and a.tokenid in ("+ tokenid2+ ") and substring(a.vchdate,1,4)*12+substring(a.vchdate,6,2) = "+ ii +") b WHERE a.accpackageid = b.accpackageid AND a.subjectid = b.subjectid AND a.submonth = b.submonth) ) "; 
					}
					for (Iterator iter = coll.iterator(); iter.hasNext();) {
						String key = (String) iter.next();
						String value = (String) map.get(key);

						s1 += "debitPrice" + i + ",debit" + key + ",creditPrice" + i + ",credit" + key + ",";

						s2 += "if(UnitName='" + value + "',UnitPrice,'') debitPrice" + i + ",";
						s2 += "if(UnitName='" + value + "',IF(dirction=1,Quantity,0.00),'') debit" + key + ",";
						s2 += "if(UnitName='" + value + "',UnitPrice,'') creditPrice" + i + ",";
						s2 += "if(UnitName='" + value + "',IF(dirction=1,0.00,Quantity),'') credit" + key + ",";

						s3 += "'' debitPrice" + i + ",a" + i + ".DebitOcc,'' creditPrice" + i + ", a" + i + ".CreditOcc,";
						s4 += "c_accountall a" + i + ",";

						s5 += " and a" + i + ".tokenid = '" + tokenid + "' ";
						s5 += " and a" + i + ".SubYearMonth * 12 + a" + i + ".SubMonth =" + ii;
						s5 += " and a" + i + ".DataName ='" + value + "' ";
						s5 += " and a.SubYearMonth = a" + i + ".SubYearMonth";
						s5 += " and a.SubMonth = a" + i + ".SubMonth";
						s5 += " and a.AccPackageID= a" + i + ".AccPackageID";
						i++;
					}
	 
					sql = " insert into `" + TabName + "` (autoid,voucherid,vchyear,vchmonth,vchdate,typeid,oldvoucherid,subjects,summary,rsubject,rUnitName,"+ s1+ "debit,credit) "
						+ " select * from ("
						+ " select autoid,voucherid,SUBSTRING(a.vchdate,1,4) vchyear,SUBSTRING(a.vchdate,6,2) vchmonth,a.VchDate,a.typeid,a.Oldvoucherid,"
						+ " REPLACE(REPLACE(REPLACE(concat(REPLACE(debitsubjects,',,',','),REPLACE(creditsubjects,',,',',')),',,',','),concat(',',a.SubjectID,','),','),',,',',') subjects,"
						+ " a.summary,CONCAT(a.subjectname1,'(',a.subjectid,')') as rsubject,UnitName as rUnitName," + s2
						+ " IF(dirction=1,occurvalue,0.00) as Debit,IF(dirction=1,0.00,occurvalue) as Credit "
						+ " from c_subjectentry a  "
						+ " WHERE a.Property like '1%' "
						+ " and a.tokenid in (" + tokenid2 + ") "
						+ " and substring(a.vchdate,1,4)*12+substring(a.vchdate,6,2) = " + ii
						+ " union "
						+ " select '' as autoid, '' as voucherid,a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-97') as VchDate,'' as typeid, '' as Oldvoucherid,'','>本月合计' as summary ,CONCAT(a.AccName,'(',a.subjectid,')') as rsubject,'',"+ s3 + " a.DebitOcc,a.CreditOcc " 
						+ " from " + s4 + " c_account a " 
						+ " WHERE a.tokenid = '" + tokenid+ "' " + strSql
						+ " and a.SubYearMonth*12 + a.SubMonth =" + ii+ s5;

					s3 = s3.replaceAll("DebitOcc", "DebitTotalOcc").replaceAll( "CreditOcc", "CreditTotalOcc");
					sql += " union select '' as autoid, '' as voucherid, a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-98') as VchDate,'' as typeid, '' as Oldvoucherid,'','>本年累计' as summary ,CONCAT(a.AccName,'(',a.subjectid,')') as rsubject,''," + s3 + " a.DebitTotalOcc,a.CreditTotalOcc "
						+ " from " + s4 + " c_account a "
						+ " WHERE a.tokenid = '" + tokenid + "' " + strSql
						+ " and a.SubYearMonth*12 + a.SubMonth =" + ii + s5;

					s3 = "";
					for (Iterator iter = coll.iterator(); iter.hasNext();) {
						String key = (String) iter.next();
						s3 += "'' debitPrice" + i + ",0.00 debit" + key + ",'' CreditPrice" + i + ",0.00 Credit" + key + ",";
					}
					sql += " union select '' as autoid, '' as voucherid, a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-00') as VchDate,'' as typeid, '' as Oldvoucherid,'','>期初余额' as summary ,CONCAT(a.AccName,'(',a.subjectid,')') as rsubject,''," + s3 + " 0.00 Debit,0.00 Credit "
						+ " from " + s4 + " c_account a "
						+ " WHERE a.tokenid = '" + tokenid + "' " + strSql
						+ " and a.SubYearMonth*12 + a.SubMonth =" + ii + s5
						+ " ) s order by VchDate,typeid,abs(Oldvoucherid),autoid ";
					//org.util.Debug.prtOut("DataToTable Map sql = " + sql);
					ps = conn.prepareStatement(sql);
					ps.execute();

				}

				//org.util.Debug.prtOut("DataToUnitTable = " + String.valueOf(new java.util.Date(System .currentTimeMillis())));
				UpdateToTable1(TabName, Cid, SubjectID, BYear, BMonth, EYear, EMonth,"");
				UpdateToTable(map, TabName, Cid, SubjectID, BYear, BMonth, EYear, EMonth);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	public void CreateTable(String TabName, Map Cmap, Map Umap)
			throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "CREATE TABLE `"
					+ TabName
					+ "` ("
					+ " id int(10) NOT NULL auto_increment,"
					+ " autoid varchar(20) default NULL,"
					+ " voucherid varchar(20) default NULL,"
					// + " p1 varchar(10) default NULL,"
					// + " p2 varchar(10) default NULL,"
					+ " vchyear varchar(10) default NULL,"
					+ " vchmonth varchar(10) default NULL,"
					+ " vchdate varchar(20) default NULL,"
					+ " typeid varchar(20) default NULL,"
					+ " oldvoucherid varchar(50) default NULL,"

					+ " subjects text default NULL," // 对方科目
					+ " rsubject varchar(500) default NULL,"//发生科目
					+ " rCurrency varchar(500) default NULL,"//币种
					+ " rUnitName varchar(500) default NULL,"//数量
					+ " summary varchar(100) default NULL,";
			Set Ucoll = Umap.keySet();
			Set Ccoll = Cmap.keySet();
			int i = 1;
			for (Iterator iter = Ucoll.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				sql += "debitPrice" + i + " varchar(20) default NULL,debit"
						+ key + " varchar(20) default NULL,";
				i++;
			}
			i = 1;
			for (Iterator iter = Ccoll.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				sql += "debitrate" + i + " varchar(20) default NULL,debit"
						+ key + " varchar(20) default NULL,";
				i++;
			}
			sql += " debit varchar(20) default NULL,";
			i = 1;
			for (Iterator iter = Ucoll.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				sql += "creditPrice" + i + " varchar(20) default NULL,credit"
						+ key + " varchar(20) default NULL,";
				i++;
			}
			i = 1;
			for (Iterator iter = Ccoll.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				sql += "creditrate" + i + " varchar(20) default NULL,credit"
						+ key + " varchar(20) default NULL,";
				i++;
			}
			sql += "credit varchar(20) default NULL,";
			for (Iterator iter = Ucoll.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				sql += "dRemain" + key + " varchar(20) default NULL,yRemain"
						+ key + " varchar(20) default NULL,";
			}
			for (Iterator iter = Ccoll.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				sql += "dRemain" + key + " varchar(20) default NULL,yRemain"
						+ key + " varchar(20) default NULL,";
			}
			sql += " dateRemain varchar(20) default NULL, yearRemain varchar(20) default NULL,PRIMARY KEY  (id)) ENGINE=MyISAM DEFAULT CHARSET=gbk ";
			ps = conn.prepareStatement(sql);
			ps.execute();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}

	public void DataToTable(Map Cmap, Map Umap, String TabName, String user,
			String proid, String Cid, String SubjectID, String BYear,
			String BMonth, String EYear, String EMonth) throws Exception {

		PreparedStatement ps = null;
		ResultSet rs = null;
		ASFuntion CHF = new ASFuntion();
		String sql = "";
		try {
			/**
			 * 科目连续性
			 */
			String acc = Cid + EYear;
			AutoTokenService ats = new AutoTokenService(conn);
			String tokenid = CHF.showNull(ats.getTokenid(acc, SubjectID)).replaceAll("\\\\", "\\\\\\\\");
			String tokenid2 = CHF.showNull(ats.getTokenidLeaf(acc, SubjectID)).replaceAll("\\\\", "\\\\\\\\");

			Set Ccoll = Cmap.keySet();
			Set Ucoll = Umap.keySet();

			int bDate = Integer.parseInt(BYear) * 12 + Integer.parseInt(BMonth);
			int eDate = Integer.parseInt(EYear) * 12 + Integer.parseInt(EMonth);
			for (int ii = bDate; ii <= eDate; ii++) {
				String s1 = "";
				String s2 = "";
				String s3 = "";
				String s4 = "";
				String s5 = "";
				int i = 1;
				
				String strSql = "";
				if(ii != bDate){
					strSql = " and not (a.DebitOcc=0 and a.CreditOcc =0 AND NOT EXISTS(SELECT 1 FROM (SELECT DISTINCT accpackageid,subjectid,SUBSTRING(vchdate,6,2) AS submonth FROM c_subjectentry a where Property like '1%' and a.tokenid in ("+ tokenid2+ ") and substring(a.vchdate,1,4)*12+substring(a.vchdate,6,2) = "+ ii +") b WHERE a.accpackageid = b.accpackageid AND a.subjectid = b.subjectid AND a.submonth = b.submonth) ) "; 
				}
				
				for (Iterator iter = Ccoll.iterator(); iter.hasNext();) {
					String key = (String) iter.next();
					String value = (String) Cmap.get(key);
					s1 += "debitrate" + i + ",debit" + key + ",creditrate" + i + ",credit" + key + ",";
					s2 += "if(Currency='" + value + "',CurrRate,'') debitrate" + i + ",";
					s2 += "if(Currency='" + value + "',IF(dirction=1,CurrValue,0.00),'') debit" + key + ",";
					s2 += "if(Currency='" + value + "',CurrRate,'') creditrate" + i + ",";
					s2 += "if(Currency='" + value + "',IF(dirction=1,0.00,CurrValue),'') credit" + key + ",";
					s3 += "'' DebitRate" + i + ",a" + i + ".DebitOcc,'' CreditRate" + i + ", a" + i + ".CreditOcc,";
					s4 += "c_accountall a" + i + ",";

					s5 += " and a" + i + ".tokenid = '" + tokenid + "' ";
					s5 += " and a" + i + ".SubYearMonth * 12 + a" + i + ".SubMonth =" + ii;

					s5 += " and a" + i + ".DataName ='" + value + "' ";
					s5 += " and a.SubYearMonth = a" + i + ".SubYearMonth";
					s5 += " and a.SubMonth = a" + i + ".SubMonth";
					s5 += " and a.AccPackageID= a" + i + ".AccPackageID";
					i++;
				}
				i = 1;
				for (Iterator iter = Ucoll.iterator(); iter.hasNext();) {
					String key = (String) iter.next();
					String value = (String) Umap.get(key);
					s1 += "debitPrice" + i + ",debit" + key + ",creditPrice" + i + ",credit" + key + ",";

					s2 += "if(UnitName='" + value + "',UnitPrice,'') debitPrice" + i + ",";
					s2 += "if(UnitName='" + value + "',IF(dirction=1,Quantity,0.00),'') debit" + key + ",";
					s2 += "if(UnitName='" + value + "',UnitPrice,'') creditPrice" + i + ",";
					s2 += "if(UnitName='" + value + "',IF(dirction=1,0.00,Quantity),'') credit" + key + ",";
					s3 += "'' debitPrice" + i + ",b" + i + ".DebitOcc,'' creditPrice" + i + ", b" + i + ".CreditOcc,";
					s4 += "c_accountall b" + i + ",";

					s5 += " and b" + i + ".tokenid = '" + tokenid + "' ";
					s5 += " and b" + i + ".SubYearMonth * 12 + b" + i + ".SubMonth =" + ii;
					s5 += " and b" + i + ".DataName ='" + value + "' ";
					s5 += " and a.SubYearMonth = b" + i + ".SubYearMonth";
					s5 += " and a.SubMonth = b" + i + ".SubMonth";
					s5 += " and a.AccPackageID= b" + i + ".AccPackageID";
					i++;
				}

				sql = " insert into `" + TabName + "` (autoid,voucherid,vchyear,vchmonth,vchdate,typeid,oldvoucherid,subjects,summary,rsubject,rCurrency,rUnitName," + s1 + "debit,credit) "
					+ " select * from ("
					+ " select autoid,voucherid,SUBSTRING(a.vchdate,1,4) vchyear,SUBSTRING(a.vchdate,6,2) vchmonth,a.VchDate,a.typeid,a.Oldvoucherid,"
					+ " REPLACE(REPLACE(REPLACE(concat(REPLACE(debitsubjects,',,',','),REPLACE(creditsubjects,',,',',')),',,',','),concat(',',a.SubjectID,','),','),',,',',') subjects,"
					+ " a.summary,CONCAT(a.subjectname1,'(',a.subjectid,')') as rsubject,Currency as rCurrency,UnitName as rUnitName," + s2
					+ " IF(dirction=1,occurvalue,0.00) as Debit,IF(dirction=1,0.00,occurvalue) as Credit "
					+ " from c_subjectentry a  "
					+ " WHERE a.Property like '1%' "
					+ " and a.tokenid in (" + tokenid2 + ") "
					+ " and substring(a.vchdate,1,4)*12+substring(a.vchdate,6,2) = " + ii
					+ " union "
					+ " select '' as autoid, '' as voucherid,a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-97') as VchDate,'' as typeid, '' as Oldvoucherid,'','>本月合计' as summary ,CONCAT(a.AccName,'(',a.subjectid,')') as rsubject,'','',"+ s3 + " a.DebitOcc,a.CreditOcc " 
					+ " from " + s4 + " c_account a " 
					+ " WHERE a.tokenid = '" + tokenid + "' " + strSql
					+ " and a.SubYearMonth*12 + a.SubMonth =" + ii + s5;

				s3 = s3.replaceAll("DebitOcc", "DebitTotalOcc").replaceAll("CreditOcc", "CreditTotalOcc");
				sql += " union select '' as autoid, '' as voucherid, a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-98') as VchDate,'' as typeid, '' as Oldvoucherid,'','>本年累计' as summary ,CONCAT(a.AccName,'(',a.subjectid,')') as rsubject,'',''," + s3 + " a.DebitTotalOcc,a.CreditTotalOcc "
					+ " from " + s4 + " c_account a "
					+ " WHERE a.tokenid = '" + tokenid + "' " + strSql
					+ " and a.SubYearMonth*12 + a.SubMonth =" + ii + s5;

				s3 = "";
				for (Iterator iter = Ccoll.iterator(); iter.hasNext();) {
					String key = (String) iter.next();
					s3 += "'' debitRate" + i + ",0.00 debit" + key + ",'' CreditRate" + i + ",0.00 Credit" + key + ",";
				}
				for (Iterator iter = Ucoll.iterator(); iter.hasNext();) {
					String key = (String) iter.next();
					s3 += "'' debitPrice" + i + ",0.00 debit" + key + ",'' CreditPrice" + i + ",0.00 Credit" + key + ",";
				}
				sql += " union select '' as autoid, '' as voucherid, a.SubYearMonth,LPAD(a.SubMonth,2,'0') vchmonth,concat(a.SubYearMonth,'-',LPAD(a.SubMonth,2,'0'),'-00') as VchDate,'' as typeid, '' as Oldvoucherid,'','>期初余额' as summary ,CONCAT(a.AccName,'(',a.subjectid,')') as rsubject,'',''," + s3 + " 0.00 Debit,0.00 Credit "
					+ " from " + s4 + " c_account a "
					+ " WHERE a.tokenid = '" + tokenid + "' " + strSql
					+ " and a.SubYearMonth*12 + a.SubMonth =" + ii + s5
					+ " ) s order by VchDate,typeid,abs(Oldvoucherid),autoid ";
				//org.util.Debug.prtOut("DataToTable Map sql = " + sql);
				ps = conn.prepareStatement(sql);
				ps.execute();

			}

			//org.util.Debug.prtOut("DataToTable Map Map = " + String.valueOf(new java.util.Date(System.currentTimeMillis())));
			UpdateToTable1(TabName, Cid, SubjectID, BYear, BMonth, EYear, EMonth,"");
			UpdateToTable(Cmap, TabName, Cid, SubjectID, BYear, BMonth, EYear,EMonth);
			UpdateToTable(Umap, TabName, Cid, SubjectID, BYear, BMonth, EYear,EMonth);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	public void DelTempTable(String TabName) throws Exception {
		PreparedStatement ps = null;
		try {
			// DROP TABLE IF EXISTS `tt`;
			String sql = "DROP TABLE IF EXISTS `" + TabName + "`";
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}

	public boolean isLeaf(String acc, String sid) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select * from c_accpkgsubject where accpackageid='"
					+ acc + "' and subjectid='" + sid + "' and isleaf=1";
			//org.util.Debug.prtOut(sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public String getSubject(String acc, String sid) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String result = "";
		try {
			String sql = "select a.SubjectId from c_account a,(" +
			"		select SubjectId from c_accpkgsubject where accpackageid='"+ acc + "' and ParentSubjectId='" + sid + "'" +
			"	) b 	" +
			"	where accpackageid='"+ acc + "'" +
			"	and submonth = 1" +
			"	and a.SubjectId=b.SubjectId";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			result = sid + "`";
			boolean bool = true;
			while (rs.next()) {
				result += rs.getString(1) + "`";
				bool = false;
			}
			if(bool){
				result += sid + "`";
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public void CreateTable(String TabName, String sDate) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "CREATE TABLE `" + TabName + "` ("
					+ " id int(10) NOT NULL auto_increment,"
					+ " autoid varchar(20) default NULL,"
					+ " voucherid varchar(20) default NULL,"
					+ " p1 varchar(10) default NULL,"
					+ " p2 varchar(10) default NULL,"
					+ " vchyear varchar(10) default NULL,"
					+ " vchmonth varchar(10) default NULL,"
					+ " vchdate varchar(20) default NULL,"
					+ " typeid varchar(20) default NULL,"
					+ " oldvoucherid varchar(50) default NULL,"
					
					+ " subjects text default NULL," // 对方科目
					
					+ " summary varchar(100) default NULL,";

			String[] sAll = sDate.split("`");
			for (int i = 1; i < sAll.length; i++) {
				sql += " debit" + i + " varchar(20) default NULL,";
				sql += " credit" + i + " varchar(20) default NULL,";
				
				sql += " date" + i + " varchar(20) default NULL,";	//本月累计
				sql += " year" + i + " varchar(20) default NULL,";	//本年累计
			}
			sql += " dateRemain varchar(20) default NULL,"
					+ " yearRemain varchar(20) default NULL,"
					+ " PRIMARY KEY  (id)"
					+ ") ENGINE=MyISAM DEFAULT CHARSET=gbk";
			//org.util.Debug.prtOut(sql);
			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

	}

	// 多栏帐
	public void DataToTable(String sDate, String TabName, String user,
			String proid, String Cid, String SubjectID, String BYear,
			String BMonth, String EYear, String EMonth) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "";
		try {
			/**
			 * 科目连续性
			 */
			String[] result = getAccIDYearMonth(Cid, BYear, BMonth, EYear,EMonth);
			//org.util.Debug.prtOut("DataToTable = "	+ String.valueOf(new java.util.Date(System.currentTimeMillis())));

			String[] sAll = sDate.split("`");
			String s0 = "";
			String s1 = "";
			String s2 = "";
			String s3 = "";
			String s4 = "";

			String s5 = "";

			for (int i = 1; i < sAll.length; i++) {
				s0 += "debit" + i + ",credit" + i + ",";

				s1 += "if(a.SubjectID like '" + sAll[i]+ "%',IF(dirction=1,occurvalue,0.00),0.00) as Debit"+ i + "," 
					+ "if(a.SubjectID like '" + sAll[i]+ "%',IF(dirction=1,0.00,occurvalue),0.00) as Credit"+ i + ",";

				s2 += "ifnull((select DebitOcc from  c_account  WHERE  AccPackageID = a.AccPackageID  and SubjectID ='"+ sAll[i]+ "' and a.submonth=submonth and a.subyearmonth=subyearmonth),0)  DebitOcc"+ i+ ","
					+ "ifnull((select CreditOcc from  c_account  WHERE  AccPackageID = a.AccPackageID  and SubjectID ='"+ sAll[i]+ "' and a.submonth=submonth and a.subyearmonth=subyearmonth),0)  CreditOcc"+ i + ",";

				s3 += "ifnull((select DebitTotalOcc from  c_account  WHERE  AccPackageID = a.AccPackageID  and SubjectID ='"+ sAll[i]+ "' and a.submonth=submonth and a.subyearmonth=subyearmonth),0)  DebitTotalOcc"+ i+ ","
					+ "ifnull((select CreditTotalOcc from  c_account  WHERE  AccPackageID = a.AccPackageID  and SubjectID ='"+ sAll[i]+ "' and a.submonth=submonth and a.subyearmonth=subyearmonth),0)  CreditTotalOcc"+ i + ",";

				s4 += "0.00 as Debit" + i + ",0.00 as Credit" + i + ",";

				s5 += " a.SubjectID like '" + sAll[i] + "%' or";
			}

			s0 = s0.substring(0, s0.length() - 1);
			s1 = s1.substring(0, s1.length() - 1);
			s2 = s2.substring(0, s2.length() - 1);
			s3 = s3.substring(0, s3.length() - 1);
			s4 = s4.substring(0, s4.length() - 1);

			s5 = " and ( " + s5.substring(0, s5.length() - 2) + ")";

			int bDate = Integer.parseInt(BYear) * 12 + Integer.parseInt(BMonth);
			int eDate = Integer.parseInt(EYear) * 12 + Integer.parseInt(EMonth);

			for (int ii = bDate; ii <= eDate; ii++) {
//				String strSql = "";
//				if(ii != bDate){
//					strSql = " and not (a.DebitOcc=0 and a.CreditOcc =0 AND NOT EXISTS(SELECT 1 FROM (SELECT DISTINCT accpackageid,subjectid,SUBSTRING(vchdate,6,2) AS submonth FROM c_subjectentry ) b WHERE a.accpackageid = b.accpackageid AND a.subjectid = b.subjectid AND a.submonth = b.submonth) ) "; 
//				}
				sql = "insert into `"+ TabName+ "` (autoid,voucherid,vchyear,vchmonth,vchdate,typeid,oldvoucherid,subjects,summary,"+ s0 + ") ";
				sql += " select * from ("
					+ " select autoid,voucherid,SUBSTRING(a.vchdate,1,4) vchyear,SUBSTRING(a.vchdate,6,2) vchmonth,a.VchDate,a.typeid, a.Oldvoucherid," 
					+ " REPLACE(REPLACE(REPLACE(concat(REPLACE(debitsubjects,',,',','),REPLACE(creditsubjects,',,',',')),',,',','),concat(',',a.SubjectID,','),','),',,',',') subjects,"
					+ " a.summary,"+ s1
					+ " from c_subjectentry a  "
					+ " WHERE a.Property like '1%'  "
					+ " and a.SubjectID like concat('"+ sAll[0]+ "','%') "
					+ " and substring(a.vchdate,1,4)*12+substring(a.vchdate,6,2) = "+ ii + s5
					
					+ " union "
					
					+ " select '' as autoid, '' as voucherid,SubYearMonth,LPAD(SubMonth,2,'0') vchmonth,concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'),'-97') as VchDate,'' as typeid, '' as Oldvoucherid,'' as subjects,'>本月合计' as summary ,"+ s2
					+ " from  c_account a "
					+ " WHERE  a.SubjectID ='"+ sAll[0]+ "' "
					+ " and a.SubYearMonth*12 + a.SubMonth ="+ ii 
					
					+ " union "
					
					+ " select '' as autoid, '' as voucherid,SubYearMonth,LPAD(SubMonth,2,'0') vchmonth,concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'),'-98') as VchDate,'' as typeid, '' as Oldvoucherid,'' as subjects,'>本年累计' as summary,"+ s3
					+ " from  c_account a "
					+ " WHERE  a.SubjectID ='"+ sAll[0]+ "' "
					+ " and a.SubYearMonth*12 + a.SubMonth ="+ ii 
					
					+ " union "
					+ " select '' as autoid, '' as voucherid,SubYearMonth,LPAD(SubMonth,2,'0') vchmonth,concat(SubYearMonth,'-',LPAD(SubMonth,2,'0'),'-00') as VchDate,'' as typeid, '' as Oldvoucherid,'' as subjects,'>期初余额' as summary ,"	+ s4
					+ " from  c_account a WHERE  a.SubjectID ='"+ sAll[0]+ "' "
					+ " and a.SubYearMonth*12 + a.SubMonth ="+ ii 
				+ " ) s order by VchDate,typeid,abs(Oldvoucherid),autoid ";
				org.util.Debug.prtOut("DataToTable sql = " + sql);
				ps = conn.prepareStatement(sql);
				ps.execute();
			}

			//org.util.Debug.prtOut("DataToTable = "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));
			UpdateToTable(sDate, TabName, Cid, SubjectID, BYear, BMonth, EYear,EMonth);
			//org.util.Debug.prtOut("UpdateToTable = "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	// 多栏帐
	private void UpdateToTable(String sDate, String TabName, String Cid,
			String SubjectID, String BYear, String BMonth, String EYear,
			String EMonth) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		try {

			String[] sAll = sDate.split("`");
			String ss = "",ss1 = "",ss2 = "";
			for (int i = 1; i < sAll.length; i++) {
				
				sql = "update `"+ TabName+ "` a join c_account b on AccPackageID like concat('"+ Cid+ "','%') and CONCAT(SubYearMonth,LPAD(SubMonth,2,'0'))=concat('"+ BYear+ "','"+ BMonth+ "') and subjectid='"+ sAll[i]+ "' and submonth=vchmonth  and SubYearMonth=vchyear and substring(vchdate,9)='00' set  date"+i+"=(debitremain+creditremain)";
				ps = conn.prepareStatement(sql);
				ps.execute();
				sql = "update `"+ TabName+ "` a join c_account b on AccPackageID like concat('"+ Cid+ "','%') and SubYearMonth>='"+ BYear+ "' and SubYearMonth<='"+ EYear+ "' and SubMonth=1 and subjectid='"+ sAll[i]+ "' and SubYearMonth=vchyear and substring(vchdate,9)='00' set  year"+i+"=(debitremain+creditremain)";
				ps = conn.prepareStatement(sql);
				ps.execute();
				
				ss1+= "b.date" + i + "+";
				ss2+= "b.year" + i + "+";
				
				ss += "(b.debit" + i + "-b.credit" + i + ")+";
			}
			ss1 = ss1.substring(0, ss1.length() - 1);
			ss2 = ss2.substring(0, ss2.length() - 1);
			
			ss = ss.substring(0, ss.length() - 1);

			// String acc = Cid + EYear;
			//org.util.Debug.prtOut("多栏帐计算1 = "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));
//			sql = "update `"+ TabName+ "` a join c_account b on AccPackageID like concat('"+ Cid+ "','%') and CONCAT(SubYearMonth,LPAD(SubMonth,2,'0'))=concat('"+ BYear+ "','"+ BMonth+ "') and subjectid='"+ SubjectID+ "' and submonth=vchmonth  and SubYearMonth=vchyear and substring(vchdate,9)='00' set  dateRemain=(debitremain+creditremain)";
			sql = "update `"+ TabName+ "` a join "+ TabName+ " b on substring(a.vchdate,9)='00' and a.vchdate = b.vchdate and a.vchyear=b.vchyear and a.vchmonth=b.vchmonth   set  a.dateRemain=" +ss1;
			ps = conn.prepareStatement(sql);
			ps.execute();
			//org.util.Debug.prtOut("多栏帐计算2 = "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));
//			sql = "update `"+ TabName+ "` a join c_account b on AccPackageID like concat('"+ Cid+ "','%') and SubYearMonth>='"+ BYear+ "' and SubYearMonth<='"+ EYear+ "' and SubMonth=1 and subjectid='"+ SubjectID+ "' and SubYearMonth=vchyear and substring(vchdate,9)='00' set  yearRemain=(debitremain+creditremain)";
			sql = "update `"+ TabName+ "` a join "+ TabName+ " b on substring(a.vchdate,9)='00' and a.vchdate = b.vchdate and a.vchyear=b.vchyear and a.vchmonth=b.vchmonth   set  a.yearRemain=" +ss2;
			ps = conn.prepareStatement(sql);
			ps.execute();
			//org.util.Debug.prtOut("多栏帐计算3 = "+ String.valueOf(new java.util.Date(System.currentTimeMillis())));

			// ////////////////////////////
			sql = "update `"+ TabName+ "` a join `"+ TabName+ "` b on substring(a.vchdate,9)='00' and substring(b.vchdate,9)='98' and a.vchyear=b.vchyear and a.vchmonth=b.vchmonth set b.dateRemain=ROUND((a.yearRemain+"+ ss + "),2) ";
			ps = conn.prepareStatement(sql);
			ps.execute();
			//org.util.Debug.prtOut("多栏帐计算4 = "+ String.valueOf(new java.util.Date(System	.currentTimeMillis())));

			sql = "update `"+ TabName+ "` a join `"+ TabName+ "` b on  substring(a.vchdate,9)='98' and substring(b.vchdate,9)='00' and a.vchyear=b.vchyear and LPAD(a.vchmonth+1,2,'0')=b.vchmonth set b.dateRemain = a.dateRemain ";
			ps = conn.prepareStatement(sql);
			ps.execute();

			sql = "update `"+ TabName+ "` a join `"+ TabName+ "` b on substring(a.vchdate,9)='00' and substring(b.vchdate,9)='97' and a.vchyear=b.vchyear and a.vchmonth=b.vchmonth set b.dateRemain=ROUND((a.dateRemain+"+ ss + "),2) ";
			//org.util.Debug.prtOut(sql);
			ps = conn.prepareStatement(sql);
			ps.execute();

			//org.util.Debug.prtOut("多栏帐计算5 = "+ String.valueOf(new java.util.Date(System	.currentTimeMillis())));
			int i = 1;

			String vi = "@i_senc" + this.getRandom();
			ps.addBatch("set " + vi + ":=0.00");
			ps.executeBatch();
			sql = "UPDATE `" + TabName	+ "` b set dateremain= if(concat(dateremain)>''," + vi+ ":=dateremain,if(concat(" + vi + ")>''," + vi+ ":=round(" + vi + "+" + ss + ",2)," + vi+ ":=dateremain)) ";
			//org.util.Debug.prtOut("sql:" + sql);
			ps = conn.prepareStatement(sql);
			ps.executeUpdate();
			ps.addBatch("set " + vi + ":=null");
			ps.executeBatch();
			ps.clearBatch();

			//org.util.Debug.prtOut("多栏帐计算0 = "+ String.valueOf(new java.util.Date(System.currentTimeMillis())) + " Num = " + i);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public String getSName(String acc, String sid) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String result = "";
		try {
			String sql = "select SubjectName from c_accpkgsubject where accpackageid='"
					+ acc + "' and SubjectId='" + sid + "'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getString(1);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	public String getSName1(String acc, String sid) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String result = "";
		try {
			String sql = "select SubjectId from c_accpkgsubject where accpackageid='"
					+ acc
					+ "' and (SubjectId like '"
					+ sid
					+ "%') order by SubjectId ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getString(1);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/** ******数量帐******** */
	public boolean isNotUnit(String acc) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sql = "";
		boolean result = false;
		try {
			sql = "select COUNT(*) from c_accountall where accpackageid='"
					+ acc + "' and accsign='2'";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				if (rs.getInt(1) > 0) {
					result = true;
				}
			}
			return result;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	// 得到所有发生了数量帐的科目

	public String getSubjects(String acc) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		try {
			String sql = "select DISTINCT a.subjectid,accname from c_accountall a,c_accpkgsubject b where a.accpackageid='"
					+ acc
					+ "' and b.accpackageid='"
					+ acc
					+ "' and accsign=2 and b.isleaf=1 and a.subjectid=b.subjectid order by a.subjectid";

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			String bgColor = "";
			int i = 0;

			while (rs.next()) {
				if (i % 2 == 0) {
					bgColor = "#F3F5F8";
				} else {
					bgColor = "#ffffff";
				}
				String s = rs.getString("SubjectID");
				sb
						.append("<tr onclick=\"goSubmit('"
								+ s
								+ "');\" onmouseover=\"this.bgColor='#E4E8EF';\" style=\"CURSOR: hand\" onmouseout=\"this.bgColor='"
								+ bgColor + "';\" bgColor=\"" + bgColor
								+ "\" height=\"18\"> ");
				sb
						.append("<td>『" + s + "』" + rs.getString("accname")
								+ "</td>");
				sb.append("</tr>");
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}

	/**
	 * 日记帐
	 */

	public void DataToTable(String TabName, String user, String proid,
			String Cid, String SubjectID, String Begin, String End)
			throws Exception {

		Statement st = null;
//		Statement st1 = null;
		ResultSet rs = null;
		ASFuntion CHF = new ASFuntion();
		String sql = "";
		
		PreparedStatement ps = null;
		
		try {
			String acc = Cid + End.substring(0, 4);
			AutoTokenService ats = new AutoTokenService(conn);
			
			String tokenid = CHF.showNull(ats.getTokenid(acc, SubjectID)).replaceAll("\\\\", "\\\\\\\\");
			
			String tokenid2 = CHF.showNull(ats.getTokenidLeaf(acc, SubjectID)).replaceAll("\\\\", "\\\\\\\\");
			System.out.println("日记帐 DataToTable2:|"+CHF.getCurrentTime());
			System.out.println("＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝");
			
			st = conn.createStatement();
//			st1 = conn.createStatement();

			sql = "select distinct vchdate from c_subjectentry a where a.tokenid in("+ tokenid2+ ") and a.vchdate >='"+ Begin+ "' and a.vchdate <='" + End + "' order by vchdate";
			rs = st.executeQuery(sql);
//			int i = 0;
			
			sql = "insert into `"+ TabName+ "` (autoid,voucherid,vchdate,typeid,oldvoucherid,subjects,summary,debit,credit,vchmonth) "
				+ " select autoid,voucherid,VchDate,typeid, Oldvoucherid,subjects,summary,Debit,Credit,opt from ("
				+ " 	select autoid,voucherid,a.VchDate,a.typeid, a.Oldvoucherid,"
				+ "		REPLACE(REPLACE(concat(REPLACE(debitsubjects,',,',','),REPLACE(creditsubjects,',,',',')),',,',','),concat(',',a.SubjectID,','),',') subjects,"
				+ "		a.summary,case dirction when 1 then occurvalue else 0.00 end  as Debit, case dirction when -1 then occurvalue else 0.00 end as Credit ,'1' opt"
				+ "		from c_subjectentry a  "
				+ "		WHERE a.Property like '1%' "
				+ "		and a.tokenid in("+ tokenid2+ ") "
				+ " 	and a.vchdate =? "
				
				+ " 	union "
				
				+ "		select '' as autoid, '' as voucherid,?  VchDate,'' as typeid, '' as Oldvoucherid,'','>本日合计' as summary ,sum(Debit) DebitOcc,sum(Credit) CreditOcc ,'2' opt"
				+ "		from("
				+ "			select case dirction when 1 then occurvalue else 0.00 end  as Debit, case dirction when -1 then occurvalue else 0.00 end  as Credit "
				+ "			from c_subjectentry a  "
				+ "			WHERE a.Property like '1%' "
				+ "			and a.tokenid in("+ tokenid2+ ") "
				+ " 		and a.vchdate =? "
				+ "		) a "
				
				+ "		union "
				
				+ "		select '' as autoid, '' as voucherid, ? VchDate,'' as typeid, '' as Oldvoucherid,'','>当前合计' as summary ,sum(Debit) DebitOcc,sum(Credit) CreditOcc ,'3' opt"
				+ " 	from(	"
				+ "			select case dirction when 1 then occurvalue else 0.00 end  as Debit, case dirction when -1 then occurvalue else 0.00 end  as Credit "
				+ "			from c_subjectentry a  "
				+ "			WHERE a.Property like '1%' "
				+ "			and a.tokenid in("+ tokenid2+ ") "
				+ " 		and a.vchdate >=? "
				+ " 		and a.vchdate <=? "
				+ "			union all "
				+ "			select DebitTotalOcc,CreditTotalOcc "
				+ "			from c_account "
				+ "			where tokenid ='"+ tokenid+ "' and submonth= ? "
				+ " 		and subyearmonth=?  "
				+ "		) a "
				
				+ " 	union "
				
				+ "		select '' as autoid, '' as voucherid, ? VchDate,'' as typeid, '' as Oldvoucherid,'','>本日期初' as summary ,0 DebitOcc,0 CreditOcc ,'0' opt"
				
				+ " ) a order by VchDate,opt,typeid,abs(Oldvoucherid),autoid ";
			
			ps = conn.prepareStatement(sql);
			
			while (rs.next()) {
				String VchDate = rs.getString("vchdate");
				String byear = VchDate.substring(0, 4);
				String bmonth = VchDate.substring(5, 7);
				
				String VchDate1 = VchDate.substring(0, 8)+ "01";
				int i = 1;
				
				ps.setString(i++, VchDate);
				ps.setString(i++, VchDate);
				ps.setString(i++, VchDate);
				ps.setString(i++, VchDate);
				ps.setString(i++, VchDate1);
				
				ps.setString(i++, VchDate);
				ps.setInt(i++, Integer.parseInt(bmonth) - 1);
				ps.setString(i++, byear);
				ps.setString(i++, VchDate);
				
				ps.addBatch();
				
			}
			
			ps.executeBatch();
			

			System.out.println("日记帐 DataToTable3:|"+CHF.getCurrentTime());
			UpdateToTable(TabName, Cid, SubjectID, Begin, End);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
//			DbUtil.close(st1);
			DbUtil.close(st);
			DbUtil.close(ps);
		}
	}
	
	public void DataToTable(Map map, String TabName, String user, String proid,
			String Cid, String SubjectID, String Begin, String End,
			String tValue) throws Exception {
		Statement st = null;
//		Statement st1 = null;
		ResultSet rs = null;
		ASFuntion CHF = new ASFuntion();
		String sql = "";
		
		PreparedStatement ps = null;
		
		try {
			String acc = Cid + End.substring(0, 4);
			AutoTokenService ats = new AutoTokenService(conn);
			String tokenid = CHF.showNull(ats.getTokenid(acc, SubjectID)).replaceAll("\\\\", "\\\\\\\\");
			String tokenid2 = CHF.showNull(ats.getTokenidLeaf(acc, SubjectID)).replaceAll("\\\\", "\\\\\\\\");

			String ss1 = "Currency";
			String ss2 = "CurrRate";
			String ss3 = "CurrValue";
			if ("Quantity".equals(tValue)) {
				ss1 = "UnitName";
				ss2 = "UnitPrice";
				ss3 = "Quantity";
			}

			Set coll = map.keySet();
			String s1 = "";
			String s2 = "";
			String s3 = "";
			String s4 = "";

			String s5 = "";
			String s6 = "";
			String s7 = "";
			int i = 1;
			for (Iterator iter = coll.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				String value = (String) map.get(key);

				s1 += "debitrate" + i + ",debit" + key + ",creditrate" + i + ",credit" + key + ",";

				s2 += "case " + ss1 + " when '" + value + "' then " + ss2+ " else '' end debitrate" + i + ",";
				s2 += "case " + ss1 + " when '" + value + "' then case dirction when 1 then " + ss3 + " else 0.00 end else '' end debit" + key + ",";
				s2 += "case " + ss1 + " when '" + value + "' then " + ss2 + " else '' end creditrate" + i + ",";
				s2 += "case " + ss1 + " when '" + value + "' then case dirction when -1 then " + ss3 + " else 0.00 end else '' end credit" + key + ",";

				s3 += "'' DebitRate" + i + ",sum(debit" + key + ") debit" + key + ",'' CreditRate" + i + ", sum(credit" + key+ ") credit" + key + ",";

				s4 += "'' debitrate" + i + ",0 debit" + key + ",'' creditrate"+ i + ",0 credit" + key + ",";

				s5 += "'',a" + i + ".DebitTotalOcc,'',a" + i+ ".CreditTotalOcc  ,";
				s6 += " c_accountall a" + i + ",";

				s7 += " and a" + i + ".dataname='" + value+ "' and a.subjectid=a" + i + ".subjectid and a.submonth=a" + i+ ".submonth and a.subyearmonth=a" + i + ".subyearmonth";
				i++;
			}

			st = conn.createStatement();

			sql = "select distinct vchdate from c_subjectentry a where a.tokenid in ("+ tokenid2+ ") and a.vchdate >='"+ Begin+ "' and a.vchdate <='" + End + "' order by vchdate";
			rs = st.executeQuery(sql);
			
			sql = "insert into `"+ TabName+ "` (autoid,voucherid,vchdate,typeid,oldvoucherid,subjects,summary,debit,credit,"+ s1+ " vchmonth) "
				+ " select autoid,voucherid,VchDate,typeid, Oldvoucherid,subjects,summary,Debit,Credit,"+ s1+ " opt "
				+ " from ( 	"
				+ " 	select autoid,voucherid,a.VchDate,a.typeid, a.Oldvoucherid, "
				+ "		REPLACE(REPLACE(concat(REPLACE(debitsubjects,',,',','),REPLACE(creditsubjects,',,',',')),',,',','),concat(',',a.SubjectID,','),',') subjects,a.summary,"
				+ "		case dirction when 1 then occurvalue else 0.00 end as Debit, "
				+ "		case dirction when -1 then occurvalue else 0.00 end as Credit ,"+ s2 + "	'1' opt "
				+ "		from c_subjectentry a "
				+ "		WHERE a.Property like '1%' "
				+ "		and a.tokenid in ("+ tokenid2+ ") "
				+ " 	and a.vchdate =? "
				
				+ " 	union "
				
				+ "		select '' as autoid, '' as voucherid,?  VchDate,'' as typeid, '' as Oldvoucherid,'','>本日合计' as summary ,sum(Debit) DebitOcc,sum(Credit) CreditOcc ,"+ s3+ " '2' opt"
				+ "		from ("
				+ "			select case dirction when 1 then occurvalue else 0.00 end  as Debit, case dirction when -1 then occurvalue else 0.00 end  as Credit, "+ s2+ " 0 "
				+ "			from c_subjectentry a  "
				+ "			WHERE a.Property like '1%' "
				+ "			and a.tokenid in ("+ tokenid2+ ") "
				+ " 		and a.vchdate =? "
				+ "		) a"
				
				+ "		union "
				
				+ "		select '' as autoid, '' as voucherid, ? VchDate,'' as typeid, '' as Oldvoucherid,'','>当前合计' as summary ,sum(Debit) DebitOcc,sum(Credit) CreditOcc ,"+ s3+ " '3' opt"
				+ " 	from(	"
				+ "			select case dirction when 1 then occurvalue else 0.00 end  as Debit, case dirction when -1 then occurvalue else 0.00 end  as Credit, "+ s2+ " 0 "
				+ "			from c_subjectentry a  "
				+ "			WHERE a.Property like '1%' "
				+ "			and a.tokenid in ("+ tokenid2+ ") "
				+ " 		and a.vchdate >=? "
				+ " 		and a.vchdate <=? "
				+ "			union all "
				+ "			select a.DebitTotalOcc,a.CreditTotalOcc, "+ s5+ "0 "
				+ "			from "+ s6+ " c_account a "
				+ "			where a.tokenid ='"+ tokenid+ "' and a.submonth= ? "
				+ " 		and a.subyearmonth=? "+ s7
				+ "		) a "
				
				+ "		union "
				
				+ "		select '' as autoid, '' as voucherid, ? VchDate,'' as typeid, '' as Oldvoucherid,'','>本日期初' as summary ,0 DebitOcc,0 CreditOcc ,"+ s4+ " '0' opt"
				
				+ " ) a order by VchDate,opt,typeid,abs(Oldvoucherid),autoid ";
			
			ps = conn.prepareStatement(sql);
			
			while (rs.next()) {
				String VchDate = rs.getString("vchdate");
				String byear = VchDate.substring(0, 4);
				String bmonth = VchDate.substring(5, 7);
				
				String VchDate1 = VchDate.substring(0, 8)+ "01";
				int ii = 1;
				
				ps.setString(ii++, VchDate);
				ps.setString(ii++, VchDate);
				ps.setString(ii++, VchDate);
				ps.setString(ii++, VchDate);
				
				ps.setString(ii++, VchDate1);
				ps.setString(ii++, VchDate);
				ps.setString(ii++, byear);
				ps.setInt(ii++, Integer.parseInt(bmonth) - 1);
				ps.setString(ii++, VchDate);
				
				ps.addBatch();
				
			}
			ps.executeBatch();

			UpdateToTable(TabName, Cid, SubjectID, Begin, End);
			UpdateToTable(map, TabName, Cid, SubjectID, Begin, End, tValue);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
			
			DbUtil.close(ps);
		}

	}

	public void DataToTable(Map Cmap, Map Umap, String TabName, String user,
			String proid, String Cid, String SubjectID, String Begin, String End)
			throws Exception {
		Statement st = null;
		ResultSet rs = null;
		ASFuntion CHF = new ASFuntion();
		String sql = "";
		
		PreparedStatement ps = null;
		
		try {

			String acc = Cid + End.substring(0, 4);
			AutoTokenService ats = new AutoTokenService(conn);
			String tokenid = CHF.showNull(ats.getTokenid(acc, SubjectID)).replaceAll("\\\\", "\\\\\\\\");
			String tokenid2 = CHF.showNull(ats.getTokenidLeaf(acc, SubjectID)).replaceAll("\\\\", "\\\\\\\\");

			Set Ccoll = Cmap.keySet();
			Set Ucoll = Umap.keySet();

			String s1 = "";
			String s2 = "";
			String s3 = "";
			String s4 = "";

			String s5 = "";
			String s6 = "";
			String s7 = "";
			int i = 1;

			/**
			 * 外币
			 */
			for (Iterator iter = Ccoll.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				String value = (String) Cmap.get(key);

				s1 += "debitrate" + i + ",debit" + key + ",creditrate" + i + ",credit" + key + ",";

				s2 += "case Currency when '" + value + "' then CurrRate else '' end debitrate" + i + ",";
				s2 += "case Currency when '" + value + "' then case dirction when 1 then CurrValue else 0.00 end else '' end debit" + key + ",";
				s2 += "case Currency when '" + value + "' then CurrRate else '' end creditrate" + i + ",";
				s2 += "case Currency when '" + value + "' then case dirction when -1 then CurrValue else 0.00 end else '' end credit" + key + ",";

				s3 += "'' DebitRate" + i + ",sum(debit" + key + ") debit" + key + ",'' CreditRate" + i + ", sum(credit" + key + ") credit" + key + ",";

				s4 += "'' debitrate" + i + ",0 debit" + key + ",'' creditrate" + i + ",0 credit" + key + ",";

				s5 += "'',a" + i + ".DebitTotalOcc,'',a" + i + ".CreditTotalOcc  ,";
				s6 += " c_accountall a" + i + ",";

				s7 += " and a" + i + ".dataname='" + value + "' and a.subjectid=a" + i + ".subjectid and a.submonth=a" + i + ".submonth and a.subyearmonth=a" + i + ".subyearmonth";
				i++;
			}

			i = 1;
			/**
			 * 数量
			 */
			for (Iterator iter = Ucoll.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				String value = (String) Umap.get(key);

				s1 += "debitPrice" + i + ",debit" + key + ",creditPrice" + i + ",credit" + key + ",";

				s2 += "case UnitName when '" + value + "' then UnitPrice else '' end debitPrice" + i + ",";
				s2 += "case UnitName when '" + value + "' then case dirction when 1 then Quantity else 0.00 end else '' end debit" + key + ",";
				s2 += "case UnitName when '" + value + "' then UnitPrice else '' end creditPrice" + i + ",";
				s2 += "case UnitName when '" + value + "' then case dirction when -1 then Quantity else 0.00 end else '' end credit" + key + ",";

				s3 += "'' debitPrice" + i + ",sum(debit" + key + ") debit" + key + ",'' creditPrice" + i + ", sum(credit" + key + ") credit" + key + ",";

				s4 += "'' debitPrice" + i + ",0 debit" + key + ",'' creditPrice" + i + ",0 credit" + key + ",";

				s5 += "'',b" + i + ".DebitTotalOcc,'',b" + i + ".CreditTotalOcc  ,";
				s6 += " c_accountall b" + i + ",";

				s7 += " and b" + i + ".dataname='" + value + "' and a.subjectid=b" + i + ".subjectid and a.submonth=b" + i + ".submonth and a.subyearmonth=b" + i + ".subyearmonth";
				i++;
			}

			st = conn.createStatement();

			sql = "select distinct vchdate from c_subjectentry a where a.tokenid in ("+ tokenid2 + ") and a.vchdate >='"+ Begin + "' and a.vchdate <='" + End + "' order by vchdate";
			rs = st.executeQuery(sql);
			
			sql = "insert into `"+ TabName+ "` (autoid,voucherid,vchdate,typeid,oldvoucherid,subjects,summary,debit,credit,"+ s1+ " vchmonth) "
				+ " select autoid,voucherid,VchDate,typeid, Oldvoucherid,subjects,summary,Debit,Credit,"+ s1+ " opt "
				+ " from ( 	"
				+ " 	select autoid,voucherid,a.VchDate,a.typeid, a.Oldvoucherid, "
				+ "		REPLACE(REPLACE(concat(REPLACE(debitsubjects,',,',','),REPLACE(creditsubjects,',,',',')),',,',','),concat(',',a.SubjectID,','),',') subjects,a.summary,"
				+ "		case dirction when 1 then occurvalue else 0.00 end as Debit, "
				+ "		case dirction when -1 then occurvalue else 0.00 end as Credit ,"+ s2+ "	'1' opt "
				+ "		from c_subjectentry a "
				+ "		WHERE a.Property like '1%' "
				+ "		and a.tokenid in ("+ tokenid2+ ") "
				+ " 	and a.vchdate =? "
				
				+ " 	union "
				
				+ "		select '' as autoid, '' as voucherid,?  VchDate,'' as typeid, '' as Oldvoucherid,'','>本日合计' as summary ,sum(Debit) DebitOcc,sum(Credit) CreditOcc ,"+ s3+ " '2' opt"
				+ "		from ("
				+ "			select case dirction when 1 then occurvalue else 0.00 end  as Debit, case dirction when -1 then occurvalue else 0.00 end  as Credit, "+ s2+ " 0 "
				+ "			from c_subjectentry a  "
				+ "			WHERE a.Property like '1%' "
				+ "			and a.tokenid in ("+ tokenid2+ ") "
				+ " 		and a.vchdate =? "
				+ "		) a"
				
				+ "		union "
				
				+ "		select '' as autoid, '' as voucherid,? VchDate,'' as typeid, '' as Oldvoucherid,'','>当前合计' as summary ,sum(Debit) DebitOcc,sum(Credit) CreditOcc ,"+ s3+ " '3' opt"
				+ " 	from(	"
				+ "			select case dirction when 1 then occurvalue else 0.00 end  as Debit, case dirction when -1 then occurvalue else 0.00 end  as Credit, "+ s2+ " 0 "
				+ "			from c_subjectentry a  "
				+ "			WHERE a.Property like '1%' "
				+ "			and a.tokenid in ("+ tokenid2+ ") "
				+ " 		and a.vchdate >= ? "
				+ " 		and a.vchdate <= ? "
				+ "			union all "
				+ "			select a.DebitTotalOcc,a.CreditTotalOcc, "+ s5+ "0 "
				+ "			from "+ s6+ " c_account a "
				+ "			where a.tokenid ='"+ tokenid+ "' and a.submonth= ? "
				+ " 		and a.subyearmonth=? "+ s7
				+ "		) a "
				
				+ "		union "
				
				+ "		select '' as autoid, '' as voucherid, ? VchDate,'' as typeid, '' as Oldvoucherid,'','>本日期初' as summary ,0 DebitOcc,0 CreditOcc ,"+ s4+ " '0' opt"
				
				+ " ) a order by VchDate,opt,typeid,abs(Oldvoucherid),autoid ";
			
			ps = conn.prepareStatement(sql);
			
			while (rs.next()) {
				String VchDate = rs.getString("vchdate");
				String byear = VchDate.substring(0, 4);
				String bmonth = VchDate.substring(5, 7);
				
				String VchDate1 = VchDate.substring(0, 8)+ "01";
				int ii = 1;
				
				ps.setString(ii++, VchDate);
				ps.setString(ii++, VchDate);
				ps.setString(ii++, VchDate);
				ps.setString(ii++, VchDate);
				
				ps.setString(ii++, VchDate1);
				ps.setString(ii++, VchDate);
				ps.setString(ii++, byear);
				ps.setInt(ii++, Integer.parseInt(bmonth) - 1);
				ps.setString(ii++, VchDate);
				
				ps.addBatch();
				
			}
			ps.executeBatch();
			
			UpdateToTable(TabName, Cid, SubjectID, Begin, End);
			UpdateToTable(Cmap, TabName, Cid, SubjectID, Begin, End, "CurrValue");
			UpdateToTable(Umap, TabName, Cid, SubjectID, Begin, End, "Quantity");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
			
			DbUtil.close(ps);
		}

	}

	private void UpdateToTable(String TabName, String Cid, String SubjectID,
			String Begin, String End) throws Exception {
		Statement st = null;
		Statement st1 = null;
		ResultSet rs = null;
		ASFuntion CHF = new ASFuntion();
		String sql = "";
		
		PreparedStatement ps = null;
		
		try {
			System.out.println("日记帐 UpdateToTable0:|"+CHF.getCurrentTime());
			System.out.println("＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝");
			String acc = Cid + End.substring(0, 4);
			AutoTokenService ats = new AutoTokenService(conn);
			String tokenid = CHF.showNull(ats.getTokenid(acc, SubjectID)).replaceAll("\\\\", "\\\\\\\\");
			String tokenid2 = CHF.showNull(ats.getTokenidLeaf(acc, SubjectID)).replaceAll("\\\\", "\\\\\\\\");

			st = conn.createStatement();
			st1 = conn.createStatement();

			String bBeginYear = Begin.substring(0, 4);
			String bEndYear = End.substring(0, 4);

			sql = "select distinct vchdate from c_subjectentry a where a.tokenid in("+ tokenid2+ ") and a.vchdate >='"+ Begin+ "' and a.vchdate <='" + End + "' order by vchdate";
			rs = st.executeQuery(sql);
			
			sql = "update `" + TabName + "` a join ( "
			+ " 	select sum(ifnull(remain,0)) remain from ( "
			+ " 		select (debitremain+creditremain) remain "
			+ " 		from c_account where tokenid ='" + tokenid+ "' and submonth=? " 
			+ " 		and subyearmonth=? " 
			+ " 		union all "
			+ " 		select sum(Dirction*OccurValue) "
			+ " 		from c_subjectentry " 
			+ " 		where tokenid in("+ tokenid2 + ")  " 
			+ " 		and VchDate>=? " 
			+ " 		and VchDate< ? " 
			+ " 	) a " 
			+ " ) b on 1=1 "
			+ " set dateremain=remain " 
			+ " where VchDate=? and vchmonth=0 ";
			
			ps = conn.prepareStatement(sql);
			
			while(rs.next()){
				String VchDate = rs.getString("vchdate");
				String byear = VchDate.substring(0, 4);
				String bmonth = VchDate.substring(5, 7);
				
				String VchDate1 = VchDate.substring(0, 8) + "01";
				
				ps.setString(1, bmonth);
				ps.setString(2, byear);
				ps.setString(3, VchDate1);
				ps.setString(4, VchDate);
				ps.setString(5, VchDate);
				
				ps.addBatch();
			}
			ps.executeBatch();

			sql = "update `"+ TabName+ "` a join `"+ TabName+ "` b  on a.vchmonth=0  and b.vchmonth=2 and a.vchdate=b.vchdate set b.dateRemain=ROUND((a.dateRemain+b.debit-b.credit),2)";
			// //org.util.Debug.prtOut("VUpdateToTable sql = " + sql);
			st1.addBatch(sql);

			sql = "update `"+ TabName+ "` a join c_account b on AccPackageID like concat('"+ Cid+ "','%') and SubYearMonth>="+ bBeginYear+ " and SubYearMonth <="+ bEndYear+ " and SubMonth=01 and subjectid='"+ SubjectID+ "' and SubYearMonth=substring(vchdate,1,4) and a.vchmonth=0  set  yearRemain=ROUND((debitremain+creditremain),2)";
			// //org.util.Debug.prtOut("VUpdateToTable sql = " + sql);
			st1.addBatch(sql);

			sql = "update `"+ TabName+ "` a join `"+ TabName+ "` b  on a.vchmonth=0  and b.vchmonth=3 and a.vchdate=b.vchdate set b.dateRemain=ROUND((a.yearRemain+b.debit-b.credit),2)";
			// //org.util.Debug.prtOut("VUpdateToTable sql = " + sql);
			st1.addBatch(sql);

			String vi = "@i_senc" + this.getRandom();
			st1.addBatch("set " + vi + ":=0.00");
			st1.executeBatch();
			sql = "UPDATE `" + TabName+ "` a set  dateremain= if(concat(dateremain)>''," + vi+ ":=dateremain,if(concat(" + vi + ")>''," + vi+ ":=round(" + vi + "+debit-credit,2)," + vi+ ":=dateremain)) ";
			// //org.util.Debug.prtOut("sql:" + sql);
			st1.addBatch(sql);

			st1.addBatch("set " + vi + ":=null");
			st1.executeBatch();
			st1.clearBatch();

			System.out.println("日记帐 UpdateToTable1:|"+CHF.getCurrentTime());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
			DbUtil.close(st1);
			DbUtil.close(ps);
		}

	}

	private void UpdateToTable(Map map, String TabName, String Cid,
			String SubjectID, String Begin, String End, String tValue)
			throws Exception {
		Statement st = null;
		Statement st1 = null;
		ResultSet rs = null;
		ASFuntion CHF = new ASFuntion();
		String sql = "";
		
		PreparedStatement ps = null;
		
		try {
			System.out.println("日记帐 UpdateToTable4:|"+CHF.getCurrentTime());
			System.out.println("＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝");
			
			String acc = Cid + End.substring(0, 4);
			AutoTokenService ats = new AutoTokenService(conn);
			String tokenid = CHF.showNull(ats.getTokenid(acc, SubjectID)).replaceAll("\\\\", "\\\\\\\\");
			String tokenid2 = CHF.showNull(ats.getTokenidLeaf(acc, SubjectID)).replaceAll("\\\\", "\\\\\\\\");

			String ss1 = "Currency";
			String ss2 = "CurrRate";
			String ss3 = "CurrValue";
			if ("Quantity".equals(tValue)) {
				ss1 = "UnitName";
				ss2 = "UnitPrice";
				ss3 = "Quantity";
			}

			st = conn.createStatement();
			st1 = conn.createStatement();

			String bBeginYear = Begin.substring(0, 4);
			String bEndYear = End.substring(0, 4);

			Set coll = map.keySet();
			for (Iterator iter = coll.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				String value = (String) map.get(key);

				sql = "select distinct vchdate from c_subjectentry a where a.tokenid in("+ tokenid2+ ") and a.vchdate >='"+ Begin+ "' and a.vchdate <='" + End + "' order by vchdate";
				rs = st.executeQuery(sql);
				
				sql = "update `" + TabName + "` a join ( "
				+ " 	select sum(ifnull(remain,0)) remain from ( "
				+ " 		select (debitremain+creditremain) remain "
				+ " 		from c_accountall where tokenid ='" + tokenid+ "' " 
				+ "			and submonth= ? "
				+ " 		and subyearmonth=? and DataName='"+ value + "'" 
				+ " 		union all "
				+ " 		select sum(Dirction*" + tValue + ") "
				+ " 		from c_subjectentry " + " 	where tokenid in("+ tokenid2 + ")  " 
				+ " 		and VchDate>=? "
				+ " 		and VchDate< ? " 
				+ "			and "+ ss1 + "='" + value + "'" 
				+ " 	) a "
				+ " ) b on 1=1 " 
				+ " set dRemain" + key+ "=remain " 
				+ " where VchDate=? and vchmonth=0 ";
				
				ps = conn.prepareStatement(sql);
				
				while (rs.next()) {
					String VchDate = rs.getString("vchdate");
					String byear = VchDate.substring(0, 4);
					String bmonth = VchDate.substring(5, 7);
					
					String VchDate1 = VchDate.substring(0, 8) + "01";
					
					ps.setString(1, bmonth);
					ps.setString(2, byear);
					ps.setString(3, VchDate1);
					ps.setString(4, VchDate);
					ps.setString(5, VchDate);
					
					ps.addBatch();
				}
				ps.executeBatch();
				DbUtil.close(ps);
				
				sql = "update `"+ TabName+ "` a join `"+ TabName+ "` b  on a.vchmonth=0  and b.vchmonth=2 and a.vchdate=b.vchdate set b.dRemain"+ key + "=ROUND((a.dRemain" + key + "+b.debit" + key+ "-b.credit" + key + "),2)";
				// //org.util.Debug.prtOut("VUpdateToTable sql = " + sql);
				st1.addBatch(sql);

				sql = "update `"+ TabName+ "` a join c_accountall b on AccPackageID like concat('"+ Cid+ "','%') and DataName='"+ value+ "' and SubYearMonth>="+ bBeginYear+ " and SubYearMonth <="+ bEndYear+ " and SubMonth=01 and subjectid='"+ SubjectID+ "' and SubYearMonth=substring(vchdate,1,4) and a.vchmonth=0  set  yRemain"+ key + "=ROUND((debitremain+creditremain),2)";
				// //org.util.Debug.prtOut("VUpdateToTable sql = " + sql);
				st1.addBatch(sql);

				sql = "update `"+ TabName+ "` a join `"+ TabName+ "` b  on a.vchmonth=0  and b.vchmonth=3 and a.vchdate=b.vchdate set b.dRemain"+ key + "=ROUND((a.yRemain" + key + "+b.debit" + key+ "-b.credit" + key + "),2)";
				// //org.util.Debug.prtOut("VUpdateToTable sql = " + sql);
				st1.addBatch(sql);

				String vi = "@i_senc" + this.getRandom();
				st1.addBatch("set " + vi + ":=0.00");
				st1.executeBatch();
				sql = "UPDATE `" + TabName + "` a set  dRemain" + key+ "= if(concat(dRemain" + key + ")>''," + vi+ ":=dRemain" + key + ",if(concat(" + vi + ")>''," + vi+ ":=round(" + vi + "+debit" + key + "-credit" + key+ ",2)," + vi + ":=dRemain" + key + ")) ";
				// ////org.util.Debug.prtOut("sql:" + sql);
				st1.addBatch(sql);

				st1.addBatch("set " + vi + ":=null");
				st1.executeBatch();
				st1.clearBatch();

			}
			
			//System.out.println("日记帐 UpdateToTable5:|"+CHF.getCurrentTime());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(st);
			DbUtil.close(ps);
		}
	}

	/**
	 * 批量打印
	 */

	public ArrayList getSubject(String T1, String EndYear, String EndDate,
			String bSubjectID, String eSubjectID, String SubjectType)
			throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList listResult = new ArrayList();
		try {
			ArrayList b1 = new ArrayList();

			int end = Integer.parseInt(EndYear) * 12
					+ Integer.parseInt(EndDate);
			String acc = T1 + EndYear;
			String blSubjectID = "";
			String elSubjectID = "";

			String sql = "select * from c_accpkgsubject where AccPackageID = ? and ? like concat(subjectid,'%') and level0=1 ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, acc);
			ps.setString(2, bSubjectID);
			rs = ps.executeQuery();
			if (rs.next()) {
				blSubjectID = rs.getString("SubjectID");
			}
			DbUtil.close(rs);

			ps.setString(2, eSubjectID);
			rs = ps.executeQuery();
			if (rs.next()) {
				elSubjectID = rs.getString("SubjectID");
			}
			DbUtil.close(rs);
			DbUtil.close(ps);

			sql = "select * from c_account where   subyearmonth * 12 + submonth = ? and subjectid>=? and subjectid<=? and level1=1 order by subjectid ";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, end);
			ps.setString(2, blSubjectID);
			ps.setString(3, elSubjectID);
			rs = ps.executeQuery();
			while (rs.next()) {
				// al.add(rs.getString("subjectid"));

				listResult.add(new SubjectInfo(rs.getString("subjectid"), rs
						.getString("AccName")));

				b1.add(rs.getString("subjectfullname2"));
			}
			DbUtil.close(rs);
			DbUtil.close(ps);

			if ("0".equals(SubjectType)) {
				return listResult; // 返回1级
			} else {
				listResult = new ArrayList();
			}

			sql = "select * from c_account where  subyearmonth * 12 + submonth = "+end+" and subjectid>='"+bSubjectID+"' and subjectid<='"+eSubjectID+"' and isleaf1=1 order by subjectid ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				listResult.add(new SubjectInfo(rs.getString("subjectid"),
						rs.getString("AccName")));
			}
			DbUtil.close(rs);
			
			return listResult; // 返回所有的末级

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}

	}

	public ArrayList getSubjectList(String T1, String EndYear, String EndDate,String Subjects, String SubjectType)throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList listResult = new ArrayList();
		try {
			ASFuntion CHF=new ASFuntion();
			String all = "'" + CHF.replaceStr(Subjects, ",", "','") + "'";
			String sql = "";
			int end = Integer.parseInt(EndYear) * 12 + Integer.parseInt(EndDate);
			String acc = T1 + EndYear;
			if ("0".equals(SubjectType)) {
				//1级
				sql = "SELECT a.* FROM c_account a,(" +
				"		SELECT * FROM c_account " +
				"		WHERE subyearmonth * 12 + submonth = ? " +
				"		AND subjectid IN (" + all + ") " +
				"	) b " +
				"	WHERE a.subyearmonth * 12 + a.submonth = ? " +
				"	AND a.level1 = 1 " +
				"	AND (b.subjectfullname1 = a.subjectfullname1 OR b.subjectfullname1 LIKE CONCAT(a.subjectfullname1,'/%'))" +
				"	order by a.subjectid";
				ps = conn.prepareStatement(sql);
				ps.setInt(1, end);
				ps.setInt(2, end);
				rs = ps.executeQuery();
				while (rs.next()) {
					listResult.add(new SubjectInfo(rs.getString("subjectid"), rs.getString("AccName")));
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
			}else{
				//末级
				sql = "SELECT 0 AS opt,'' AS assitemid,a.subjectid,a.accname,c.SubjectID AS LevelSubjectID ,c.accname as levelSubjectName " +
				"	FROM c_account a,(" +
				"		SELECT * FROM c_account " +
				"		WHERE subyearmonth * 12 + submonth = ? " +
				"		AND subjectid IN (" + all + ") " +
				"	) b ,c_account c " +
				"	WHERE a.subyearmonth * 12 + a.submonth = ? " +
				"	AND a.isleaf1 = 1 " +
				"	AND c.subyearmonth * 12 + c.submonth = ?  " +
				"	AND c.Level1 = 1 " +
				"	AND (a.subjectfullname1 = b.subjectfullname1 OR a.subjectfullname1 LIKE CONCAT(b.subjectfullname1,'/%'))" +
				"	AND (a.subjectfullname1 = c.subjectfullname1 OR a.subjectfullname1 LIKE CONCAT(c.subjectfullname1,'/%'))" +
				
				"	UNION " +
				
				"	SELECT 1 AS opt,a.assitemid,a.accid,a.assitemname,b.LevelSubjectID,b.levelSubjectName " + 
				"	FROM c_assitementryacc a,( " +
				"		SELECT 0 AS opt,'' AS assitemid,a.subjectid,a.accname,c.SubjectID AS LevelSubjectID,c.accname as levelSubjectName " +
				"		FROM c_account a,( " +
				"			SELECT * FROM c_account " +
				"			WHERE subyearmonth * 12 + submonth = ? " +
				"			AND subjectid IN (" + all + ") " + 
				"		) b ,c_account c " +
				"		WHERE a.subyearmonth * 12 + a.submonth = ? " +
				"		AND a.isleaf1 = 1 " +
				"		AND c.subyearmonth * 12 + c.submonth = ?  " +
				"		AND c.Level1 = 1 " +
				"		AND (a.subjectfullname1 = b.subjectfullname1 OR a.subjectfullname1 LIKE CONCAT(b.subjectfullname1,'/%')) " +
				"		AND (a.subjectfullname1 = c.subjectfullname1 OR a.subjectfullname1 LIKE CONCAT(c.subjectfullname1,'/%'))" +
				"	) b ,c_subjectassitem c      " +
				"	WHERE  a.subyearmonth * 12 + a.submonth = ? " +
				"	AND a.isleaf1 = 1 " +
				"	AND a.accid = b.subjectid " +
				"	AND c.accpackageid = ? " +
				"	AND c.ifequal = 0 " +
				"	AND a.accid = c.subjectid " +
				"	AND (a.AssTotalName1 = c.AssTotalName1 OR a.AssTotalName1 LIKE CONCAT(c.AssTotalName1,'/%')) " +
				
				"	order by subjectid,assitemid";
				int i = 1;
				ps = conn.prepareStatement(sql);
				ps.setInt(i++, end);
				ps.setInt(i++, end);
				ps.setInt(i++, end);
				ps.setInt(i++, end);
				ps.setInt(i++, end);
				ps.setInt(i++, end);
				ps.setInt(i++, end);
				ps.setString(i++, acc);
				rs = ps.executeQuery();
				while (rs.next()) {
					listResult.add(new SubjectInfo(
							rs.getString("opt"),
							rs.getString("assitemid"),
							rs.getString("subjectid"),
							rs.getString("AccName"),
							rs.getString("LevelSubjectID"),
							rs.getString("levelSubjectName")
						)
					);
				}
				DbUtil.close(rs);
				DbUtil.close(ps);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		return listResult;
	}
	
	public ArrayList getSubjectList(String T1, String EndYear, String EndDate,
			String bSubjectID, String eSubjectID, String SubjectType)
			throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList listResult = new ArrayList();
		try {
			
			int end = Integer.parseInt(EndYear) * 12 + Integer.parseInt(EndDate);
			String acc = T1 + EndYear;
			String blSubjectID = "",elSubjectID = "";
		
			String sql = "select * from c_accpkgsubject where AccPackageID = ? and ? like concat(subjectid,'%') and level0=1 ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, acc);
			ps.setString(2, bSubjectID);
			rs = ps.executeQuery();
			if (rs.next()) {
				blSubjectID = rs.getString("SubjectID");
			}
			DbUtil.close(rs);
		
			ps.setString(2, eSubjectID);
			rs = ps.executeQuery();
			if (rs.next()) {
				elSubjectID = rs.getString("SubjectID");
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			sql = "select * from c_account " +
			"	where   subyearmonth * 12 + submonth = ? " +
			"	and subjectid>=? " +
			"	and subjectid<=? " +
			"	and level1=1 " +
			"	order by subjectid ";
			ps = conn.prepareStatement(sql);
			ps.setInt(1, end);
			ps.setString(2, blSubjectID);
			ps.setString(3, elSubjectID);
			rs = ps.executeQuery();
			while (rs.next()) {
				listResult.add(new SubjectInfo(rs.getString("subjectid"), rs.getString("AccName")));
			}
			DbUtil.close(rs);
			DbUtil.close(ps);

			if ("0".equals(SubjectType)) {
				return listResult; // 返回1级
			} else {
				listResult = new ArrayList();
			}
			
			//末级（包括核算）
			sql = "select 0 AS opt,'' AS assitemid,subjectid,accname  from c_account " +
			"	where  subyearmonth * 12 + submonth = "+end+" " +
			"	and subjectid>='"+bSubjectID+"' " +
			"	and subjectid<='"+eSubjectID+"' " +
			"	and isleaf1=1 " +
			"	union " +
			"	SELECT 1 AS opt,a.assitemid,a.accid,a.assitemname  " +
			"	FROM c_assitementryacc a, c_assitem b  " +
			"	where  a.subyearmonth * 12 + a.submonth = "+end+" " +
			"	and a.accid>='"+bSubjectID+"' " +
			"	and a.accid<='"+eSubjectID+"' " +
			"	and a.isleaf1=1 " +
			"	AND a.accid = b.accid AND a.assitemid = b.assitemid " +
			"	order by subjectid,assitemid"; 
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				listResult.add(new SubjectInfo(
						rs.getString("opt"),
						rs.getString("assitemid"),
						rs.getString("subjectid"),
						rs.getString("AccName")
					)
				);
				
			}
			DbUtil.close(rs);
			
			return listResult;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	public int getTempTableCount(String TableName) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select count(*) from " + TableName;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			} else {
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	/**
	 * 获取科目
	 * @param accpackageId
	 * @return
	 * @throws Exception
	 */
	public String getSubjectJSON(String accpackageId,String addAssItem,String checked,String noSubject) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuffer sb = new StringBuffer();
		
		try {
			String sql = "",table = "c_account";
			if("1".equals(noSubject)){
				sql = " and accid is not null ";
			}else if("2".equals(noSubject)){
				table = "c_accountall";
				sql = " AND a.accsign = 2 AND a.isleaf1=1 ";
			}else{
				sql =  " and a.level1=1 ";
			}
			
			sql = "select subjectID,AccName,isLeaf1,subjectFullName1,level1,if(accid is null,isLeaf1,'0') isLeaf2 "
				+ " from "+table+" a "
				+ " left join ("
				+ "		select distinct accid from c_assitementryacc b "
				+ "		where 1=1 "
				+ "		and b.accpackageid =? "
				+ "		and b.level1 = 1 "
				+ "	) b "
				+ " on  a.accpackageid =? "
				+ " and  a.SubMonth='01' "
				//+ " and  a.level1=1 "
				+ " and a.subjectid = b.accid "
				+ " where a.accpackageid=? "
				+ " and a.SubMonth='01' " 
				+ sql 
				+ " order by a.SubjectID ";
			
			ps = conn.prepareStatement(sql);
			ps.setString(1, accpackageId);
			ps.setString(2, accpackageId);
			ps.setString(3, accpackageId);
			
			String subjectId;
			String subjectName;
			String isleaf;
			String subjectFullName;
			int level;
			String isleaf2;
			
			sb.append("[");
			
			rs = ps.executeQuery();
			
			while(rs.next()) {
				subjectId = rs.getString(1);
				subjectName = rs.getString(2);
				isleaf = rs.getString(3);
				subjectFullName = rs.getString(4);
				level = rs.getInt(5);
				isleaf2 = rs.getString(6);
				
				sb.append(" { ");
				
				sb.append("isSubject:'0',");//用于标志：0为科目，1为核算
				
				sb.append("cls:'folder',");
				
				//科目树，有核算的科目追加核算到科目下级
				if("1".equals(addAssItem)){	//含核算
					sb.append("leaf:").append("1".equals(isleaf2)).append(",");
				}else{	//不含核算
					sb.append("leaf:").append("1".equals(isleaf)).append(",");	
				}
				
				sb.append("id:'").append(subjectId).append("',") ;
				sb.append("subjectName:'").append(subjectName).append("',");
				sb.append("subjectFullName:'").append(subjectFullName).append("',");
				sb.append("level:").append(level).append(",");
				if(checked != null && !"".equals(checked)) {
					sb.append("checked:").append(checked).append(",");
				}
				sb.append("text:'").append(subjectId + " "  + subjectName).append("'");
				
				sb.append("}");
				
				if(!rs.isLast()) {
					sb.append(",");
				}
			}
			
			sb.append("]");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return sb.toString();
		
		
	}
	


	/**
	 * 找科目或核算的下级
	 * @param accpackageId	账套编号	
	 * @param parentSubjectFullName		科目或核算的全路径
	 * @param parentLevel	科目或核算的层次
	 * @param addAssItem	是否要显示核算树，0为不显示，1为显示
	 * @param isSubject		此节点是否科目，0为科目，1为核算
	 * @param accid		节点是核算时，对应的科目编号
	 * @return
	 * @throws Exception
	 */
	public String getSubjectJSON(String accpackageId,String parentSubjectFullName, int parentLevel,String addAssItem,String isSubject,String accid,String checked) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuffer sb = new StringBuffer();
		String sql = "";
		try {
			String subjectId;
			String subjectName;
			String isleaf;
			String subjectFullName;
			int tempLevel;
			String isleaf2;
			
			//System.out.println("addAssItem="+addAssItem+"|isSubject="+isSubject+"|accid="+accid);
			
			
			sb.append("[");
			
			if("0".equals(isSubject)){		//科目
				sql = "select subjectID,AccName,isLeaf1,subjectFullName1,level1,if(accid is null,isLeaf1,'0') isLeaf2 "
					+ " from c_account a "
					
					+ " left join ("
					+ "		select distinct accid from c_assitementryacc b "
					+ "		where 1=1 "
					+ "		and b.accpackageid =? "
					+ "		and b.level1 = 1 "
					+ "	) b "
					+ " on  a.accpackageid =? "
					+ " and  a.SubMonth='01' "
					+ " and a.subjectid = b.accid "
					
					+ " where AccPackageID = ? "
					+ " and SubjectFullName1 like ? "
					+ " and level1 = ? "
					+ " and SubMonth='01' "
					+ " order by SubjectID ";
			
				ps = conn.prepareStatement(sql);
				ps.setString(1, accpackageId);
				ps.setString(2, accpackageId);
				ps.setString(3, accpackageId);
				
				ps.setString(4, parentSubjectFullName + "/%");
				ps.setInt(5, (parentLevel+1));
				
				rs = ps.executeQuery();
				
				int rowCount = 0;
				while(rs.next()) {
					rowCount ++;
					subjectId = rs.getString(1);
					subjectName = rs.getString(2);
					isleaf = rs.getString(3);
					subjectFullName = rs.getString(4);
					tempLevel = rs.getInt(5);
					isleaf2 = rs.getString(6);
					sb.append(" { ");
					
					sb.append("isSubject:'0',");//用于标志：0为科目，1为核算	
					sb.append("accid:'',");
					
					sb.append("cls:'folder',");
					
					//科目树，有核算的科目追加核算到科目下级
					if("1".equals(addAssItem)){	//含核算
						sb.append("leaf:").append("1".equals(isleaf2)).append(",");
					}else{	//不含核算
						sb.append("leaf:").append("1".equals(isleaf)).append(",");	
					}
					
					sb.append("id:'").append(subjectId).append("',");
					sb.append("subjectName:'").append(subjectName).append("',");
					sb.append("subjectFullName:'").append(subjectFullName).append("',");
					sb.append("level:").append(tempLevel).append(",");
					if(checked != null && !"".equals(checked)) {
						sb.append("checked:").append(checked).append(",");
					}
					sb.append("text:'").append(subjectId + " "  + subjectName).append("'");
					
					sb.append("}");
					
					if(!rs.isLast()) {
						sb.append(",");
					}
				}
				
				DbUtil.close(rs);
				DbUtil.close(ps);
					
				if("1".equals(addAssItem)){	//含核算的科目树
					if(rowCount == 0){	//表示没有下级科目，要检查本科目有没有核算
						
						sql = "select assitemid,AssTotalName1 as AccName,isLeaf1,AssTotalName1, level1,accid  "
							+ "		from c_assitementryacc a,( "
							+ " 		select subjectid from c_account " 
							+ " 		where accpackageId=? "
							+ " 		and SubjectFullName1 = ? "
							+ " 		and submonth =1 "
							+ " 	) b  "
							+ " 	where accpackageId=? "
							+ " 	and submonth =1 "
							+ " 	and level1 = 1 "
							+ " 	and a.accid = b.subjectid ";
						ps = conn.prepareStatement(sql);
						ps.setString(1, accpackageId);
						ps.setString(2, parentSubjectFullName);
						ps.setString(3, accpackageId);
						rs = ps.executeQuery();
						while(rs.next()) {
							rowCount ++;
							subjectId = rs.getString(1);
							subjectName = rs.getString(2);
							isleaf = rs.getString(3);
							subjectFullName = rs.getString(4);
							tempLevel = rs.getInt(5);
							
							sb.append(" { ")
							.append("isSubject:'1',")	//用于标志：0为科目，1为核算	
							.append("accid:'").append(rs.getString("accid")).append("',")
							
							.append("cls:'folder',")
							.append("leaf:").append("1".equals(isleaf)).append(",")
							.append("id:'").append(subjectId).append("',")
							.append("subjectName:'").append(subjectName).append("',")
							.append("subjectFullName:'").append(subjectFullName).append("',")
							.append("level:").append(tempLevel).append(",")
							.append("text:'").append(subjectId + " "  + subjectName).append("'") ;
							if(checked != null && !"".equals(checked)) {
								sb.append(",checked:").append(checked);
							}
							sb.append("}");
							
							if(!rs.isLast()) {
								sb.append(",");
							}
						}
						DbUtil.close(rs);
						DbUtil.close(ps);
						
					}
				}
					
			}else{	//核算
				if("1".equals(addAssItem)){
					sql = "select assitemid,AssTotalName1 as AccName,isLeaf1,AssTotalName1, level1,accid  "
						+ "		from c_assitementryacc a  "
						+ " 	where accpackageId=? "
						+ " 	and submonth =1 "
						+ " 	and AssTotalName1 like ? "
						+ " 	and level1 = ? "
						+ " 	and accid = ? "
						+ "		order by assitemid ";
					ps = conn.prepareStatement(sql);
					ps.setString(1, accpackageId);
					ps.setString(2, parentSubjectFullName+ "/%");
					ps.setInt(3, (parentLevel+1));
					ps.setString(4, accid);
					
					rs = ps.executeQuery();
					while(rs.next()) {
						subjectId = rs.getString(1);
						subjectName = rs.getString(2);
						isleaf = rs.getString(3);
						subjectFullName = rs.getString(4);
						tempLevel = rs.getInt(5);
						
						sb.append(" { ")
						.append("isSubject:'1',")	//用于标志：0为科目，1为核算	
						.append("accid:'").append(rs.getString("accid")).append("',")
						
						.append("cls:'folder',")
						.append("leaf:").append("1".equals(isleaf)).append(",")
						.append("id:'").append(subjectId).append("',")
						.append("subjectName:'").append(subjectName).append("',")
						.append("subjectFullName:'").append(subjectFullName).append("',")
						.append("level:").append(tempLevel).append(",")
						.append("text:'").append(subjectId + " "  + subjectName).append("'") ;
						if(checked != null && !"".equals(checked)) {
							sb.append(",checked:").append(checked);
						}
						sb.append("}");
						
						if(!rs.isLast()) {
							sb.append(",");
						}
					}
				}
			}
			
			
			sb.append("]"); 
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		
		return sb.toString();
	}

	
	public Map getVoucher(String acc,String where) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Map map = new HashMap();
			
			String sql = "select *  from c_voucher  where 1=1  " + where;
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				String VchDate = rs.getString("VchDate");
				String TypeID = rs.getString("TypeID");
				String VoucherID = rs.getString("VoucherID");
				
				String FillUser = rs.getString("FillUser");
				String AuditUser = rs.getString("AuditUser");
				String KeepUser = rs.getString("KeepUser");
				String Director = rs.getString("Director");
				
				String AutoId = rs.getString("AutoId");
				String AccPackageID = rs.getString("AccPackageID");
				map.put("AccPackageID",	AccPackageID);
				map.put("AutoId", AutoId);
				
				map.put("VchDate", VchDate);
				map.put("TypeID", TypeID);
				map.put("VoucherID", VoucherID);
				
				map.put("Director", Director);
				map.put("FillUser", FillUser);
				map.put("AuditUser", AuditUser);
				map.put("KeepUser", KeepUser);
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			where = " and VoucherID = " + (String)map.get("AutoId");
			
			sql = "select if(property like '%2%',1,0) as  property from c_subjectentry  where  1=1  " +  where + " group by property ";
			//System.out.println(sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			String property = "0";
			int ii = 0;
			while(rs.next()){
				ii ++;
				property = rs.getString("property");
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			if(ii > 1){
				property = "0";
			}
			map.put("Property", property);
			
			sql = "select Currency from c_subjectentry  where Currency <> ''  " + where + " group by Currency ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				map.put("Currency", "1");	//有外币
			}else{
				map.put("Currency", "0");
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			sql = "select UnitName from c_subjectentry  where UnitName <> '' " + where + " group by UnitName ";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()){
				map.put("UnitName", "1");	//有数量
			}else{
				map.put("UnitName", "0");
			}
			DbUtil.close(rs);
			DbUtil.close(ps);
			
			
			
			
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
	}
	
	
	public void CreateTableSheet(String TabName) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "CREATE TABLE `"
					+ TabName
					+ "` ("
					+ " id int(10) NOT NULL auto_increment,"
					+ " autoid varchar(20) default NULL,"
					+ " voucherid varchar(20) default NULL,"
					+ " vchyear varchar(10) default NULL,"
					+ " vchmonth varchar(10) default NULL,"
					+ " vchdate varchar(20) default NULL,"
					+ " typeid varchar(20) default NULL,"
					+ " oldvoucherid varchar(50) default NULL,"
					+ " summary varchar(100) default NULL,"
					
					+ " rsubject varchar(500) default NULL," // 发生科目
					+ " subjects text default NULL," // 对方科目
					
					+ " rCurrency varchar(100) default NULL," // 币种
					+ " debitRate varchar(20) default NULL,"//汇率
					+ " debitCurr varchar(20) default NULL,"//原币借发生
					+ " creditRate varchar(20) default NULL,"//汇率
					+ " creditCurr varchar(20) default NULL,"//原币贷发生
					+ " currRemain varchar(20) default NULL,"//原币余额
					
					+ " rUnitName varchar(100) default NULL," //数量单位
					+ " debitPrice varchar(20) default NULL,"//单价
					+ " debitUnit varchar(20) default NULL,"//数量借发生
					+ " creditPrice varchar(20) default NULL,"//单价
					+ " creditUnit varchar(20) default NULL,"//数量贷发生
					+ " unitRemain varchar(20) default NULL,"//数量余额
					
//					+ " direction varchar(20) default NULL,"//方向
					+ " debit varchar(20) default NULL,"
					+ " credit varchar(20) default NULL,"
					+ " remain varchar(20) default NULL,"
					
					+ " PRIMARY KEY  (id)"
					+ ") ENGINE=MyISAM DEFAULT CHARSET=gbk";

			ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}

	}
	
	public void DataToTable(String TabName,String temp,int result,String opt) throws Exception {
		PreparedStatement ps = null;
		try {
//			autoid,voucherid,vchyear,vchmonth,vchdate,typeid,oldvoucherid,summary,
//			rsubject,subjects,
//			Currency,debitRate,debitCurr,creditRate,creditCurr,currRemain,
//			UnitName,debitPrice,debitUnit,creditPrice,creditUnit,unitRemain,
//			debit,credit,remain
			
			String sql = "insert into " + TabName + "( " +
			"autoid,voucherid,vchyear,vchmonth,vchdate,typeid,oldvoucherid,summary," +
			"rsubject,subjects," +
			"rCurrency,debitRate,debitCurr,creditRate,creditCurr,currRemain," +
			"rUnitName,debitPrice,debitUnit,creditPrice,creditUnit,unitRemain," +
			"debit,credit,remain	" +		
			") ";
			
			//opt 1为核算,其它为科目
			switch(result){
			case 0:		//本位币
				sql += " select  " +
				"	autoid,voucherid,vchyear,vchmonth,vchdate,typeid,oldvoucherid,summary," +
				"	rsubject,subjects," +
				"	'' as Currency,'' as debitRate,'' as debitCurr,'' as creditRate,'' as creditCurr,'' as currRemain," +
				"	'' as UnitName,'' as debitPrice,'' as debitUnit,'' as creditPrice,'' as creditUnit,'' as unitRemain," +
				"	debit,credit,dateRemain as remain	" +	
				"	from " + temp + " order by id";
				break;
			case 1:			//外币
				sql += " select  " +
				"	autoid,voucherid,vchyear,vchmonth,vchdate,typeid,oldvoucherid,summary," +
				"	rsubject,subjects," +
				"	rCurrency,debitrate1 as debitRate,debitCurr1 as debitCurr,creditrate1 as creditRate,creditCurr1 as creditCurr,dRemainCurr1 as currRemain," +
				"	'' as UnitName,'' as debitPrice,'' as debitUnit,'' as creditPrice,'' as creditUnit,'' as unitRemain," +
				"	debit,credit,dateRemain as remain	" +	
				"	from " + temp + " order by id";
				break;
			case 2:		//数量
				String str = "";
				if("1".equals(opt)){
					str ="	rUnitName,debitrate1 as debitPrice,debitCurr1 as debitUnit,creditrate1 as creditPrice,creditCurr1 as creditUnit,dRemainCurr1 as unitRemain," ;
				}else{
					str ="	rUnitName,debitPrice1 as debitPrice,debitUnit1 as debitUnit,creditPrice1 as creditPrice,creditUnit1 as creditUnit,dRemainUnit1 as unitRemain," ;
				}
				sql += " select  " +
				"	autoid,voucherid,vchyear,vchmonth,vchdate,typeid,oldvoucherid,summary," +
				"	rsubject,subjects," +
				"	'' as Currency,'' as debitRate,'' as debitCurr,'' as creditRate,'' as creditCurr,'' as currRemain," +
				str + 
				"	debit,credit,dateRemain as remain	" +	
				"	from " + temp + " order by id";
				break;
			case 3:		//外币与数量
				
				sql += " select  " +
				"	autoid,voucherid,vchyear,vchmonth,vchdate,typeid,oldvoucherid,summary," +
				"	rsubject,subjects," +
				"	rCurrency,debitrate1 as debitRate,debitCurr1 as debitCurr,creditrate1 as creditRate,creditCurr1 as creditCurr,dRemainCurr1 as currRemain," +
				"	rUnitName,debitPrice1 as debitPrice,debitUnit1 as debitUnit,creditPrice1 as creditPrice,creditUnit1 as creditUnit,dRemainUnit1 as unitRemain," +
				"	debit,credit,dateRemain as remain	" +	
				"	from " + temp + " order by id";
				break;
			}
			
			//System.out.println(sql);
			ps = conn.prepareStatement(sql);
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(ps);
		}
	}
	
	
	
	public static void main(String[] args) {
		
		String str = ",1001,1002,";
		ASFuntion CHF=new ASFuntion();
		//System.out.println("'"+CHF.replaceStr(str, ",", "','")+"'");
		
//		Connection conn = null;
//
//		try {
//			conn = new DBConnect().getConnect("100001");
//			String aa = new SubjectEntry(conn).getSubjectJSON("1000012002","应收账款",1);
//			
//			//System.out.println(aa);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			DbUtil.close(conn);
//		}
	}

	public boolean isSplitBool() {
		return splitBool;
	}

	public void setSplitBool(boolean splitBool) {
		this.splitBool = splitBool;
	}
}
