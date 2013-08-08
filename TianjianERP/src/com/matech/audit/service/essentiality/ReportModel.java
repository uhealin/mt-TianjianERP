package com.matech.audit.service.essentiality;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DecimalFormat;
import java.util.*;

import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.service.customer.CustomerService;
import com.matech.audit.service.rectify.RectifyService;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.sys.UTILSysProperty;

/*得到重要性水平的报表级别的信息*/
public class ReportModel {



	private int level1SubjectCount = 5;

	private double result[][] = new double[level1SubjectCount][2];//储存结果集

	private String iProjectEndMonth="12";

	DecimalFormat df = new DecimalFormat("0.00");

	DecimalFormat df2 = new DecimalFormat("###,##0.00");

	//分别保存了  余额  比率  重要性水平  自定义  选定
	private String[][] report = new String[6][7];

	private String choseEL;

	private String subjectSum;

	//得到负值重分类科目列表
	public List Classification(String acc,String projectID,int bYear,int eYear) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			List list = new ArrayList();
			conn = new DBConnect().getConnect(acc.substring(0, 6));
			
			String [] key = new String[]{"应收账款","应收款项","其他应收款","预收账款","预收款项","应付账款","应付款项","其他应付款","预付账款","预付款项"};
			
			RectifyService rectify = new RectifyService(conn);

			String ClassSubject = rectify.getClassSubject(acc, projectID, key);
			String NotSubject = rectify.getNotClassSubject(acc);

			String ClassAssitem = rectify.getClassAssitem(acc);
			
			String UserSubject = rectify.getUserSubject(acc, 0);
			String UserAssitem = rectify.getUserSubject(acc, 1);

			String sql = "";
			String s1 = "".equals(ClassSubject) ? " and 1=2 " : " and a.subjectid in ("+ClassSubject+") ";
			String s2 = "".equals(NotSubject) ? "  " : " and a.subjectid not IN ("+NotSubject+") ";
			String s3 = "".equals(ClassSubject) ? " and 1=2 " : " and a.accid in ("+ClassSubject+") ";
			
			String s9 = "".equals(ClassAssitem) ? " " : " and concat(a.accid,'`',a.assitemid)  in (" + ClassAssitem + ")  ";
			
			String s4 = " and a.AssTotalName1 not like '现金流量%' ";

			String s5 = "".equals(UserSubject) ? "" : " and a.subjectid not IN(" + UserSubject + ") ";
			String s6 = "".equals(UserAssitem) ? "" : " and concat(a.accid,'`',a.assitemid) not in (" + UserAssitem + ") ";

//			+ s1 + s2 + s5 +
//			+ s3 + s4 + s6 + s9
			
			String s7 = "".equals(UserSubject) ? " and 1=2 " : " and a.subjectid  IN(" + UserSubject + ") ";
			String s8 = "".equals(UserAssitem) ? " and 1=2 " : " and concat(a.accid,'`',a.assitemid)  in (" + UserAssitem + ") ";
			
			String strVdate = "1"; 
			
			String strHaving = " and direction*qmremain<0 ";
			String strHaving1 = " and direction*qmremain<>0 ";
			
			String strSelect = "SUM(a.direction*qmremain) ";
			String strRemain = "qmremain";
			String strOcc = "2";
			if("0".equals(strVdate)){
				strHaving = " and direction*qcremain<0 ";
				strHaving1 = " and direction*qcremain<>0 ";
				strSelect = "SUM(a.direction*qcremain) ";
				strOcc = "5";
				strRemain = "qcremain";
			}
			
			sql = "select 1 as opt, b.SubjectID,b.subjectfullname,if(direction=1,'借','贷') subDir,"+strSelect+" as rectifyocc," +
					"\n sum(a.direction*qcremain) qcremain,sum(debitocc) debitocc,sum(creditocc) creditocc," +
					"\n SUM(a.direction*qmremain) qmremain,if(SUM(occ1)=0,'',SUM(occ1)) totalocc,SUM(direction *"+strRemain+"+occ1) occ ,b.SubjectID as pid,'' as ppid " +
					"\n from (" +
					
					//科目
					" 	select a.*,ifnull(DebitTotalOcc"+strOcc+",0) as DebitTotalOcc"+strOcc+", ifnull(CreditTotalOcc"+strOcc+",0) as CreditTotalOcc"+strOcc+"," +
					"	direction * (ifnull(DebitTotalOcc"+strOcc+",0) - ifnull(CreditTotalOcc"+strOcc+",0)) as occ1," +
					"	case a.direction when 1 then ifnull(DebitTotalOcc"+strOcc+",0) when -1 then ifnull(CreditTotalOcc"+strOcc+",0) end occ " +
					
					" 	from (" +
					"\n		select a.SubjectID,'' as orderid,a.AccName," +
					"\n		sum(if(a.SubYearMonth * 12 + SubMonth = "+bYear+",(a.DebitRemain+a.CreditRemain),0))  qcremain," +
					"\n		sum(debitocc)  debitocc, " +
					"\n		sum(creditocc)  creditocc," +
					"\n		sum(if(a.SubYearMonth * 12 + SubMonth = "+eYear+",a.Balance,0)) qmremain ,direction2 as direction " +
					"\n		from c_account a " +
					"\n		where 1=1 " +
					"\n		and a.SubYearMonth * 12 + SubMonth >= "+bYear+" " +
					"\n		and a.SubYearMonth * 12 + SubMonth <= "+eYear+" " +
					
					"\n		and a.isleaf1 =1  " + s1 + s2 + s5 +
					"\n		group by a.SubjectID HAVING  ABS(qcremain) + abs(debitocc) + abs(creditocc) + abs(qmremain)>0 " + strHaving + 
					" 	) a left join z_accountrectify b on projectid='"+projectID+"' and (DebitTotalOcc"+strOcc+" <>0 OR CreditTotalOcc"+strOcc+" <>0)  and a.subjectid=b.subjectid\n" +

					//核算
					"\n	union " +
					" 	select a.*,ifnull(DebitTotalOcc"+strOcc+",0), ifnull(CreditTotalOcc"+strOcc+",0)," +
					"	direction * (ifnull(DebitTotalOcc"+strOcc+",0) - ifnull(CreditTotalOcc"+strOcc+",0)) as occ1," +
					"	case a.direction when 1 then ifnull(DebitTotalOcc"+strOcc+",0) when -1 then ifnull(CreditTotalOcc"+strOcc+",0) end occ " +
					" 	from (" +
					"\n		select a.accid  as SubjectID ,a.assitemid as orderid,a.assitemname AS AccName," +
					"\n		sum(if(a.SubYearMonth * 12 + SubMonth = "+bYear+",(a.DebitRemain+a.CreditRemain),0))  qcremain,	" +
					"\n		sum(debitocc)  debitocc," +
					"\n		sum(creditocc)  creditocc,	" +
					"\n		sum(if(a.SubYearMonth * 12 + SubMonth = "+eYear+",a.Balance,0)) qmremain ,	direction2 as direction " +
					"\n		from c_assitementryacc a  " +
					"\n		where 1=1 " +
					"\n		and a.SubYearMonth * 12 + SubMonth >= "+bYear+" " +
					"\n		and a.SubYearMonth * 12 + SubMonth <= "+eYear+" " +

					"\n		and a.isleaf1=1 " + s3 + s4 + s6 + s9 + 
					"\n		group by a.accid ,a.assitemid HAVING  ABS(qcremain) + abs(debitocc) + abs(creditocc) + abs(qmremain)>0  " + strHaving + 
					" 	) a left join z_assitemaccrectify b on projectid='"+projectID+"' and (DebitTotalOcc"+strOcc+" <>0 OR CreditTotalOcc"+strOcc+" <>0) and a.subjectid=b.subjectid and b.AssItemID =a.orderid\n" +
			
					//多科目挂帐
					"\n	union " +
					"\n		select  if(oriaccid='',orisubjectid,oriaccid) oriaccid,if(oriaccid='','',orisubjectid) orisubjectid,orisubjectname," +
					"\n		sum(qcremain),sum(debitocc),sum(creditocc) ,sum(qmremain),oriparentdirection,sum(debittotalocc"+strOcc+"),sum(credittotalocc"+strOcc+"),oriparentdirection * (sum(debittotalocc"+strOcc+") - sum(credittotalocc"+strOcc+")) as occ1, sum(occ)" +
					"\n		from(" +
					"\n			select a.*, ifnull(debittotalocc"+strOcc+",0) as debittotalocc"+strOcc+", ifnull(credittotalocc"+strOcc+",0) as credittotalocc"+strOcc+",case oriparentdirection when 1 then ifnull(debittotalocc"+strOcc+",0) when -1 then ifnull(credittotalocc"+strOcc+",0)  end occ" +
					"\n			from (" +
					"\n				select a.accid,a.subjectid,a.oriparentsubjectid,a.oriparentdirection,a.oriaccid,a.orisubjectid,a.orisubjectname,a.orisubjectfullname,b.qcremain,b.debitocc,b.creditocc,b.qmremain" +
					"\n				from(" +
					"\n					select distinct a.accid,a.subjectid,a.SubjectName,a.subjectfullname,a.oriparentsubjectid,a.oriparentdirection,a.oriaccid,a.orisubjectid,a.orisubjectname,a.orisubjectfullname from  c_usersubject a where oriaccid ='' and accpackageid='"+acc+"' " +
					"\n				)a inner join (" +
					"\n					select a.SubjectID,'' as orderid,a.AccName," +
					"\n					sum(if(a.SubYearMonth * 12 + SubMonth = "+bYear+",(a.DebitRemain+a.CreditRemain),0))  qcremain," +
					"\n					sum(debitocc)  debitocc, " +
					"\n					sum(creditocc)  creditocc," +
					"\n					sum(if(a.SubYearMonth * 12 + SubMonth = "+eYear+",a.Balance,0)) qmremain ,direction2 as direction " +
					"\n					from c_account a " +
					"\n					where 1=1 " +
					"\n					and a.SubYearMonth * 12 + SubMonth >= "+bYear+" " +
					"\n					and a.SubYearMonth * 12 + SubMonth <= "+eYear+" " +
					"\n					and a.isleaf1 =1  " + s7 +
					"\n					group by a.SubjectID " +
//					"\n					HAVING  ABS(qcremain) + abs(debitocc) + abs(creditocc) + abs(qmremain)>0 " +
					"\n				) b on  a.orisubjectid=b.subjectid " +
					
					"\n				union" +
					
					"\n				select a.accid,a.subjectid,a.oriparentsubjectid,a.oriparentdirection,a.oriaccid,a.orisubjectid,a.orisubjectname,a.orisubjectfullname,b.qcremain,b.debitocc,b.creditocc,b.qmremain" +
					"\n				from(	" +
					"\n					select distinct a.accid,a.subjectid,a.SubjectName,a.subjectfullname,a.oriparentsubjectid,a.oriparentdirection,a.oriaccid,a.orisubjectid,a.orisubjectname,a.orisubjectfullname from  c_usersubject a where oriaccid >'' and accpackageid='"+acc+"' " +
					"\n				)a inner join (" +
					"\n					select a.accid  ,a.assitemid ,a.assitemname AS AccName," +
					"\n					sum(if(a.SubYearMonth * 12 + SubMonth = "+bYear+",(a.DebitRemain+a.CreditRemain),0))  qcremain,	" +
					"\n					sum(debitocc)  debitocc," +
					"\n					sum(creditocc)  creditocc,	" +
					"\n					sum(if(a.SubYearMonth * 12 + SubMonth = "+eYear+",a.Balance,0)) qmremain ,	direction2 as direction " +
					"\n					from c_assitementryacc a  " +
					"\n					where 1=1 " +
					"\n					and a.SubYearMonth * 12 + SubMonth >= "+bYear+" " +
					"\n					and a.SubYearMonth * 12 + SubMonth <= "+eYear+" " +
					"\n					and a.isleaf1=1 " + s8 +
					"\n					group by a.accid ,a.assitemid " +
//					"\n					HAVING  ABS(qcremain) + abs(debitocc) + abs(creditocc) + abs(qmremain)>0  " +
					"\n				) b on  a.oriaccid=b.accid and a.orisubjectid=b.assitemid" +  

					"\n			) a left join ( \n" +
					"\n				SELECT SubjectID,assitemid,debittotalocc"+strOcc+",credittotalocc"+strOcc+" from z_assitemaccrectify where  projectid='"+projectID+"' and (DebitTotalOcc"+strOcc+" <>0 OR CreditTotalOcc"+strOcc+" <>0)" +
					"\n				union " +
					"\n				select '' as accid,SubjectID,debittotalocc"+strOcc+",credittotalocc"+strOcc+"  from z_accountrectify where  projectid='"+projectID+"' and (DebitTotalOcc"+strOcc+" <>0 OR CreditTotalOcc"+strOcc+" <>0) and isleaf =1	" +
					"\n			) b on a.oriaccid=b.SubjectID and a.orisubjectid=b.assitemid order by accid,subjectid\n" +
					"\n		)t group by accid,subjectid,oriparentsubjectid,oriparentdirection having oriparentdirection* sum("+strRemain+")<0 \n" +

					
					"\n ) a " +
					"\n left join c_accpkgsubject b " +
					"\n on substring(a.subjectid,1,length(b.subjectid))=b.subjectid " +
					"\n and b.accpackageid='"+acc+"' " +
					"\n and level0=1 " +
					"\n group by b.SubjectID " ;
			
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			ResultSetMetaData RSMD = rs.getMetaData();
			while(rs.next()){
				Map map = new HashMap();
				for (int i = 1; i <= RSMD.getColumnCount(); i++) {
					map.put(RSMD.getColumnLabel(i).toLowerCase() , rs.getString(RSMD.getColumnLabel(i)));
				}
				list.add(map);
			}

			return list;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
			DbUtil.close(conn);
		}
		return null;
	}
	
	
	public ReportModel(String apkgID, String projectID, String projectEndMonth) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		this.iProjectEndMonth=projectEndMonth;

		try {
			conn = new DBConnect().getConnect(apkgID.substring(0, 6));

			String sql = " select substring(a.subjectid,1,1) as subjectid, ";
			sql = sql + " sum(a.balance) as balance ";
			sql = sql + " from c_account a  ";
			sql = sql + " where a.accpackageid=?";
			sql = sql + " and a.submonth=" + iProjectEndMonth + " ";
			sql = sql + " and a.level1=1 ";
			sql = sql + " group by substring(subjectid,1,1) ";
			sql = sql + " order by subjectid";
			System.out.println(sql);
			ps = conn.prepareStatement(sql);
			ps.setString(1, apkgID);
			rs = ps.executeQuery();

			rs.last();
			int rows = rs.getRow();

			result = new double[rows][2];
			this.setLevel1SubjectCount(rows);

			rs.beforeFirst();

			for (int i = 0; rs.next(); i++) {
				result[i][0] = Double.parseDouble(rs.getString(1));
				result[i][1] = Double.parseDouble(rs.getString(2));
			}
			
			CustomerService customerService = new CustomerService(conn);
			String upSubSql = "a.subjectid like '1%' or a.subjectid like '2%' or a.subjectid like '3%'";
			String opt = "0";
			if(customerService.getCustomer(apkgID.substring(0, 6)).getVocationId().equals("59")){
				upSubSql = "a.subjectid like '1%' or a.subjectid like '2%' or a.subjectid like '3%' or a.subjectid like '4%'";	
				opt = "1";
			}
			
			//科目为4开头的合，如果是借方余额，就加到净资产里
			double FourSubject = 0.00;
			int tempI = result.length - 1;
			
			if (result[tempI][1] > 0.00) {
				FourSubject = result[tempI][1];
			}
			
			//索引为6
			//总资产 = 所有项目截止日期末余额大于零的资产类科目余额加总+ABS（所有项目截止日期末余额小于零的负债类科目余额加总）
			//净资产 = 总资产 - 总负债 = (资产 [正]+负债[负]) - (资产 [负]+负债[正]) = 资产 - 负债
			sql = "select " +
			"	sum(if(a.balance > 0,a.balance,0)) as abalance " +
			"	from c_account a" +
			"	where a.accpackageid=? " +
			"	and a.submonth=" + iProjectEndMonth + "  " +
			"	and a.level1=1  " +
			"	and substring(subjectid,1,1) in (1,2)  " ;
			ps = conn.prepareStatement(sql);
			ps.setString(1, apkgID);
			rs = ps.executeQuery();
			if(rs.next()){
				this.setReport(format(rs.getDouble("abalance")), 0, 5); //总资产
			}else{
				this.setReport(format(result[0][1]), 0, 5); //总资产	
			}
			this.setReport(format(result[0][1] + result[1][1] + FourSubject),1, 5); //净资产
			this.setReport(this.getYYSR(conn, apkgID), 2, 5);
			this.setReport(this.getJLR(conn, apkgID), 3, 5);
			this.setReport(this.getFY(conn, apkgID), 4, 5);
			this.setReport(this.getML(conn, apkgID), 5, 5);
			
			//x索引为0的表示科目1.
			this.setReport(format(result[0][1]), 0, 0); //总资产
			this.setReport(format(result[0][1] + result[1][1] + FourSubject),1, 0); //净资产

			//开发时暂时设置定的临时值
			this.setReport(this.getYYSR(conn, apkgID), 2, 0);
			//org.util.Debug.prtOut("\n\n\n\n\n\n\n\n"+this.getYYSR(conn,apkgID)+"\n\n\n\n\n\n\n\n\n\n\n");
			//org.util.Debug.prtOut("\n\n\n\n\n\n\n\n"+getReport(2,0)+"\n\n\n\n\n\n\n\n\n\n\n");
			this.setReport(this.getJLR(conn, apkgID), 3, 0);
			
			this.setReport(this.getFY(conn, apkgID), 4, 0);
			
			this.setReport(this.getML(conn, apkgID), 5, 0);

			/* 分别得到:比率 自定义  选定 +[自定义余额]   */
			//alter table `z_essentiality` add column `essentiality4` decimal (15,2);
			try {
				sql = "alter table z_essentiality add column essentiality4 decimal (15,2)";
				ps = conn.prepareStatement(sql);
				ps.execute();	
			} catch (Exception e) {
				System.out.println("z_essentiality -> essentiality4 已存在");
			}
			
			sql = " select essentiality1,essentiality2,essentiality3,ifnull(essentiality4,'') as essentiality4 ";
			sql = sql
					+ " from z_essentiality where accpackageid=? and subjectid = ? and projectid=? and property = 1";
			ps = conn.prepareStatement(sql);

			for (int i = 0; i < 6; i++) {
				ps.setString(1, apkgID);
				ps.setString(2, (opt.equals("1")?(i==3?i+2:i+1):i+1)+"");
				ps.setString(3, projectID);

				rs = ps.executeQuery();
				if (rs.next()) {
					this.setReport(rs.getString(1), i, 1);
					this.setReport(rs.getString(2), i, 3);
					this.setReport(rs.getString(3), i, 4);
					
					//负值重分类后余额
					if(!"".equals(rs.getString(4))){
						//不为空 
						this.setReport(format(rs.getDouble(4)), i, 6);
					}else{
						this.setReport(getReport(i, 0), i, 6);
					}
					
					if (getReport(i, 6) == null || getReport(i, 6).equals("")
							|| getReport(i, 6).equals("nothing")) {
						this.setReport("nothing", i, 2);
					} else {
						this.setReport(format(Double.parseDouble(getReport(i, 6)) * Double.parseDouble(getReport(i, 1)) * 0.01),i, 2);
					}
					
				} else {
					//为空判断。　
					this.setReport("1", i, 1);
					this.setReport("nothing", i, 3);
					this.setReport("0", i, 4);
					
					this.setReport(getReport(i, 0), i, 6);
					
					if (getReport(i, 6) == null || getReport(i, 6).equals("")
							|| getReport(i, 6).equals("nothing")) {
						this.setReport("nothing", i, 2);
					} else {
						this.setReport(format(Double.parseDouble(getReport(i, 6)) * Double.parseDouble(getReport(i, 1)) * 0.01),i, 2);
					}
					
				}
			}

			sql = " SELECT subjectid from `z_essentiality` where  accpackageid=? and projectid=? and essentiality3=1 ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, apkgID);
			ps.setString(2, projectID);
			rs = ps.executeQuery();
			if (rs.next()) {
				this.setChoseEL(rs.getString(1));
			} else {
				this.setChoseEL("0");
			}

			//不能设置重要性水平的科目
			String noSubject = "" + UTILSysProperty.SysProperty.get("不能设置重要性水平的科目");
			String[] noSubjects = noSubject.split(",");
			String noSubjectSql = "";
			for(int i=0; i<noSubjects.length; i++) {
				noSubjectSql += " and a.subjectfullname2 <> '" + noSubjects[i] + "' and a.subjectfullname2 not like '" + noSubjects[i] + "/%'\n";
			}
			
			//=========获取科目级别余额（发生额）的和
			sql = "select SUM(abs(case  when ("+upSubSql+") "
					+ " then balance else debittotalocc end )) as coin "
					+ " from c_account a,c_accpkgsubject b "
					+ " where a.accpackageid=? "
					+ " and b.accpackageid=? "
					+ " and submonth=" + iProjectEndMonth + " "
					+ " and b.level0=1 "
					+ " and a.subjectid=b.subjectid "
					+ " and a.accpackageid=b.accpackageid"
					+ noSubjectSql ;
			//org.util.Debug.prtOut("\n\n\n%%%"+sql+"%%%\n\n\n");
			ps = conn.prepareStatement(sql);
			ps.setString(1, apkgID);
			ps.setString(2, apkgID);
			rs = ps.executeQuery();
			if (rs.next()) {
				this.subjectSum = rs.getString(1);
			} else {
				this.subjectSum = "0";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null) //===================================切记关
					ps.close();
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	//取结果集的余额
	public double getResult(double args) {

		for (int i = 0; i < result.length; i++) {
			if (result[i][0] == (args)) {
				return result[i][1];
			}
		}
		return 0;
	}

	public String format(double args) {
		return df.format(args);
	}

	public static void main(String[] args) {
		//    char c=1;
		//    org.util.Debug.prtOut("="+c+"=");
		//    reportModel rm=new reportModel("1000072005");
		//    for(int i=0;i<5;i++){
		//      org.util.Debug.prtOut(rm.getResult(i+1));
		//    }
		String s = ",,,,,,,,,";
		String[] ss = s.split(",", 11);
		org.util.Debug.prtOut(ss[10] + ":::::::");
		org.util.Debug.prtOut(ss.length);
	}

	//获取 "营业收入（销售收入）"的值　，没有则返回null
	public String getYYSR(Connection c, String apkID) {

		String sql = " select creditTotalOcc from c_account a, \n";
		sql = sql + " ( \n";
		sql = sql + "     select '主营业务收入' as gs \n";
		sql = sql + "     union \n";
		sql = sql
				+ "     select replace(replace(replace('主营业务收入',b.key1,b.key2),c.key1,c.key2),d.key1,d.key2) \n";
		sql = sql + "     from k_key b,k_key c,k_key d \n";
		sql = sql + "     where '主营业务收入' like concat('%',b.key1,'%') \n";
		sql = sql + "     and '主营业务收入' like concat('%',c.key1,'%') \n";
		sql = sql + "     and '主营业务收入' like concat('%',d.key1,'%') \n";
		sql = sql + " ) c \n";
		sql = sql + " where a.accpackageid=?  \n";
		sql = sql + " and a.submonth=" + iProjectEndMonth + " \n";
		sql = sql + " and a.accName = c.gs \n";

		//org.util.Debug.prtOut("\n\n\n\n\n\n\n\n\n\n\n\n"+sql+"\n\n\n\n\n\n\n\n\n\n\n\n\n\n");

		Connection conn = c;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, apkID);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("creditTotalOcc");
			} else {
				return "nothing";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "nothing";
		}
		//这里不需要关闭conn，因为引用了本类的公共conn.

	}

	public String getJLRSql(String apkID) {
		String sql = "select sum(a.Occ*a.direction) \n";
		sql = sql + " from \n";
		sql = sql + "( \n";
		sql = sql + "select a.debittotalocc as occ,c.direction from \n";

		sql = sql + "  ( select * from  c_account\n";
		sql = sql + "  where accpackageid=" + apkID + " \n";
		sql = sql + "  and submonth=" + iProjectEndMonth + " \n";
		sql = sql + " ) a,  \n";
		sql = sql + " ( \n";
		sql = sql + "      select -1 as direction ,'主营业务成本' as gs   \n";
		sql = sql + "      union   \n";
		sql = sql
				+ "      select -1 as direction ,trim(replace(replace(replace('主营业务成本              ',b.key1,b.key2),c.key1,c.key2),d.key1,d.key2))   \n";
		sql = sql + "      from k_key b,k_key c,k_key d   \n";
		sql = sql + "      where '主营业务成本' like concat('%',b.key1,'%')   \n";
		sql = sql + "      and '主营业务成本' like concat('%',c.key1,'%')   \n";
		sql = sql + "      and '主营业务成本' like concat('%',d.key1,'%')   \n";
		sql = sql + "        \n";
		sql = sql + "      union  \n";
		sql = sql + "      select -1 as direction ,'主营业务税金及附加' as gs   \n";
		sql = sql + "      union   \n";
		sql = sql
				+ "      select -1 as direction ,trim(replace(replace(replace('主营业务税金及附加              ',b.key1,b.key2),c.key1,c.key2),d.key1,d.key2))   \n";
		sql = sql + "      from k_key b,k_key c,k_key d   \n";
		sql = sql + "      where '主营业务税金及附加' like concat('%',b.key1,'%')   \n";
		sql = sql + "      and '主营业务税金及附加' like concat('%',c.key1,'%')   \n";
		sql = sql + "      and '主营业务税金及附加' like concat('%',d.key1,'%')   \n";
		sql = sql + "        \n";
		sql = sql + "        \n";
		sql = sql + "      union  \n";
		sql = sql + "      select -1 as direction ,'其他业务支出' as gs   \n";
		sql = sql + "      union   \n";
		sql = sql
				+ "      select -1 as direction ,trim(replace(replace(replace('其他业务支出              ',b.key1,b.key2),c.key1,c.key2),d.key1,d.key2))   \n";
		sql = sql + "      from k_key b,k_key c,k_key d   \n";
		sql = sql + "      where '其他业务支出' like concat('%',b.key1,'%')   \n";
		sql = sql + "      and '其他业务支出' like concat('%',c.key1,'%')   \n";
		sql = sql + "      and '其他业务支出' like concat('%',d.key1,'%')   \n";
		sql = sql + "    \n";
		sql = sql + "      union  \n";
		sql = sql + "      select -1 as direction ,'营业费用' as gs   \n";
		sql = sql + "      union   \n";
		sql = sql
				+ "      select -1 as direction ,trim(replace(replace(replace('营业费用              ',b.key1,b.key2),c.key1,c.key2),d.key1,d.key2))   \n";
		sql = sql + "      from k_key b,k_key c,k_key d   \n";
		sql = sql + "      where '营业费用' like concat('%',b.key1,'%')   \n";
		sql = sql + "      and '营业费用' like concat('%',c.key1,'%')   \n";
		sql = sql + "      and '营业费用' like concat('%',d.key1,'%')   \n";
		sql = sql + "        \n";
		sql = sql + "      union  \n";
		sql = sql + "      select -1 as direction ,'管理费用' as gs   \n";
		sql = sql + "      union   \n";
		sql = sql
				+ "      select -1 as direction ,trim(replace(replace(replace('管理费用              ',b.key1,b.key2),c.key1,c.key2),d.key1,d.key2))   \n";
		sql = sql + "      from k_key b,k_key c,k_key d   \n";
		sql = sql + "      where '管理费用' like concat('%',b.key1,'%')   \n";
		sql = sql + "      and '管理费用' like concat('%',c.key1,'%')   \n";
		sql = sql + "      and '管理费用' like concat('%',d.key1,'%')   \n";
		sql = sql + "        \n";
		sql = sql + "      union  \n";
		sql = sql + "      select -1 as direction ,'财务费用' as gs   \n";
		sql = sql + "      union   \n";
		sql = sql
				+ "      select -1 as direction ,trim(replace(replace(replace('财务费用              ',b.key1,b.key2),c.key1,c.key2),d.key1,d.key2))   \n";
		sql = sql + "      from k_key b,k_key c,k_key d   \n";
		sql = sql + "      where '财务费用' like concat('%',b.key1,'%')   \n";
		sql = sql + "      and '财务费用' like concat('%',c.key1,'%')   \n";
		sql = sql + "      and '财务费用' like concat('%',d.key1,'%')   \n";
		sql = sql + "        \n";
		sql = sql + "      union  \n";
		sql = sql + "      select -1 as direction ,'营业外支出' as gs   \n";
		sql = sql + "      union   \n";
		sql = sql
				+ "      select -1 as direction ,trim(replace(replace(replace('营业外支出              ',b.key1,b.key2),c.key1,c.key2),d.key1,d.key2))   \n";
		sql = sql + "      from k_key b,k_key c,k_key d   \n";
		sql = sql + "      where '营业外支出' like concat('%',b.key1,'%')   \n";
		sql = sql + "      and '营业外支出' like concat('%',c.key1,'%')   \n";
		sql = sql + "      and '营业外支出' like concat('%',d.key1,'%')   \n";
		sql = sql + "        \n";
		sql = sql + "      union  \n";
		sql = sql + "      select -1 as direction ,'所得税' as gs   \n";
		sql = sql + "      union   \n";
		sql = sql
				+ "      select -1 as direction ,trim(replace(replace(replace('所得税              ',b.key1,b.key2),c.key1,c.key2),d.key1,d.key2))   \n";
		sql = sql + "      from k_key b,k_key c,k_key d   \n";
		sql = sql + "      where '所得税' like concat('%',b.key1,'%')   \n";
		sql = sql + "      and '所得税' like concat('%',c.key1,'%')   \n";
		sql = sql + "      and '所得税' like concat('%',d.key1,'%')   \n";

		sql = sql + "      union  \n";
		sql = sql + "      select -1 as direction ,'以前年度损益调整' as gs   \n";
		sql = sql + "      union   \n";
		sql = sql
				+ "      select -1 as direction ,trim(replace(replace(replace('以前年度损益调整              ',b.key1,b.key2),c.key1,c.key2),d.key1,d.key2))   \n";
		sql = sql + "      from k_key b,k_key c,k_key d   \n";
		sql = sql + "      where '以前年度损益调整' like concat('%',b.key1,'%')   \n";
		sql = sql + "      and '以前年度损益调整' like concat('%',c.key1,'%')   \n";
		sql = sql + "      and '以前年度损益调整' like concat('%',d.key1,'%')   \n";

		sql = sql + " ) c \n";
		sql = sql + " where  a.accName =c.gs \n";

		sql = sql + " union all \n";

		sql = sql + "  select a.credittotalocc as occ,c.direction from \n";

		sql = sql + "  ( select * from  c_account\n";
		sql = sql + "  where accpackageid=" + apkID + " \n";
		sql = sql + "  and submonth=" + iProjectEndMonth + " \n";
		sql = sql + " ) a,  \n";
		sql = sql + " ( \n";
		sql = sql + "      select 1 as direction ,'主营业务收入' as gs   \n";
		sql = sql + "      union   \n";
		sql = sql
				+ "      select 1 as direction ,trim(replace(replace(replace('主营业务收入              ',b.key1,b.key2),c.key1,c.key2),d.key1,d.key2))   \n";
		sql = sql + "      from k_key b,k_key c,k_key d   \n";
		sql = sql + "      where '主营业务收入' like concat('%',b.key1,'%')   \n";
		sql = sql + "      and '主营业务收入' like concat('%',c.key1,'%')   \n";
		sql = sql + "      and '主营业务收入' like concat('%',d.key1,'%')   \n";
		sql = sql + "        \n";
		sql = sql + "      union  \n";
		sql = sql + "      select +1 as direction ,'其他业务收入' as gs   \n";
		sql = sql + "      union   \n";
		sql = sql
				+ "      select +1 as direction ,trim(replace(replace(replace('其他业务收入              ',b.key1,b.key2),c.key1,c.key2),d.key1,d.key2))   \n";
		sql = sql + "      from k_key b,k_key c,k_key d   \n";
		sql = sql + "      where '其他业务收入' like concat('%',b.key1,'%')   \n";
		sql = sql + "      and '其他业务收入' like concat('%',c.key1,'%')   \n";
		sql = sql + "      and '其他业务收入' like concat('%',d.key1,'%')   \n";
		sql = sql + "    \n";
		sql = sql + "      union  \n";
		sql = sql + "      select 1 as direction ,'投资收益' as gs   \n";
		sql = sql + "      union   \n";
		sql = sql
				+ "      select 1 as direction ,trim(replace(replace(replace('投资收益              ',b.key1,b.key2),c.key1,c.key2),d.key1,d.key2))   \n";
		sql = sql + "      from k_key b,k_key c,k_key d   \n";
		sql = sql + "      where '投资收益' like concat('%',b.key1,'%')   \n";
		sql = sql + "      and '投资收益' like concat('%',c.key1,'%')   \n";
		sql = sql + "      and '投资收益' like concat('%',d.key1,'%')   \n";
		sql = sql + "        \n";
		sql = sql + "      union  \n";
		sql = sql + "      select 1 as direction ,'补贴收入' as gs   \n";
		sql = sql + "      union   \n";
		sql = sql
				+ "      select 1 as direction ,trim(replace(replace(replace('补贴收入              ',b.key1,b.key2),c.key1,c.key2),d.key1,d.key2))   \n";
		sql = sql + "      from k_key b,k_key c,k_key d   \n";
		sql = sql + "      where '补贴收入' like concat('%',b.key1,'%')   \n";
		sql = sql + "      and '补贴收入' like concat('%',c.key1,'%')   \n";
		sql = sql + "      and '补贴收入' like concat('%',d.key1,'%')   \n";
		sql = sql + "        \n";
		sql = sql + "      union  \n";
		sql = sql + "      select 1 as direction ,'营业外收入' as gs   \n";
		sql = sql + "      union   \n";
		sql = sql
				+ "      select 1 as direction ,trim(replace(replace(replace('营业外收入              ',b.key1,b.key2),c.key1,c.key2),d.key1,d.key2))   \n";
		sql = sql + "      from k_key b,k_key c,k_key d   \n";
		sql = sql + "      where '营业外收入' like concat('%',b.key1,'%')   \n";
		sql = sql + "      and '营业外收入' like concat('%',c.key1,'%')   \n";
		sql = sql + "      and '营业外收入' like concat('%',d.key1,'%')   \n";

		sql = sql + " ) c \n";
		sql = sql + " where  a.accName =c.gs \n";

		sql = sql + ") a \n";
		org.util.Debug.prtOut("\n\n\n:::" + sql + ":::\n\n\n");
		return sql;
	}

	//获取 "净利润 "的值　，没有则返回null
	//净利润＝主营业务收入-主营业务成本-主营业务税金及附加+其他业务利润-营业费用-管理费用- 财务费用+投资收益+补贴收入+营业外收入-营业外支出-所得税"
	public String getJLR(Connection c, String apkID) {
		//    String args="主营业务收入,主营业务成本,主营业务税金及附加,其他业务收入,其他业务支出,营业费用,管理费用,财务费用 ,投资收益,补贴收入,营业外收入,营业外支出,所得税";
		//           主营业务收入-主营业务成本-主营业务税金及附加+其他业务利润-            营业费用-管理费用- 财务费用+投资收益+补贴收入+营业外收入-营业外支出-所得税"
		//  利润总额＝主营业务收入 主营业务成本 主营业务税金及附加 其他业务利润－           经营费用 管理费用  财务费用 投资收益 营业外收入＋补贴收入＋以前年度损益调整－所得税
		//    String[] argsArray=args.split(",");
		double result = 0.00;
		double[] resultArgs = new double[13];

		Connection conn = c;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			ps = conn.prepareStatement(this.getJLRSql(apkID));
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			} else {
				return "nothing";
			}
		} catch (Exception e) {
			e.printStackTrace();
			//      return "nothing";
		}//这里不需要关闭conn，因为引用了本类的公共conn.

		{
			int i = 0;
			result = resultArgs[i++] - resultArgs[i++] - resultArgs[i++]
					+ resultArgs[i++] - resultArgs[i++] - resultArgs[i++]
					- resultArgs[i++] - resultArgs[i++] + resultArgs[i++]
					+ resultArgs[i++] + resultArgs[i++] - resultArgs[i++]
					- resultArgs[i++];
		}

		return format(result);
	}
	
	//获取 "费用总额"的值　，没有则返回null
	//费用总额 = 管理 + 销售 + 财务
	public String getFY(Connection c, String apkID) {
		double result = 0.00;

		Connection conn = c;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			String sql = "select sum(a.Occ*a.direction) \n";
			sql = sql + " from \n";
			sql = sql + "( \n";
			sql = sql + "select a.debittotalocc as occ,c.direction from \n";

			sql = sql + "  ( select * from  c_account\n";
			sql = sql + "  where accpackageid=" + apkID + " \n";
			sql = sql + "  and submonth=" + iProjectEndMonth + " \n";
			sql = sql + " ) a,  \n";
			sql = sql + " ( \n";
			sql = sql + "      select 1 as direction ,'营业费用' as gs   \n";
			sql = sql + "      union   \n";
			sql = sql + "      select 1 as direction ,trim(replace(replace(replace('营业费用              ',b.key1,b.key2),c.key1,c.key2),d.key1,d.key2))   \n";
			sql = sql + "      from k_key b,k_key c,k_key d   \n";
			sql = sql + "      where '营业费用' like concat('%',b.key1,'%')   \n";
			sql = sql + "      and '营业费用' like concat('%',c.key1,'%')   \n";
			sql = sql + "      and '营业费用' like concat('%',d.key1,'%')   \n";
			sql = sql + "        \n";
			sql = sql + "      union  \n";
			sql = sql + "      select 1 as direction ,'管理费用' as gs   \n";
			sql = sql + "      union   \n";
			sql = sql + "      select 1 as direction ,trim(replace(replace(replace('管理费用              ',b.key1,b.key2),c.key1,c.key2),d.key1,d.key2))   \n";
			sql = sql + "      from k_key b,k_key c,k_key d   \n";
			sql = sql + "      where '管理费用' like concat('%',b.key1,'%')   \n";
			sql = sql + "      and '管理费用' like concat('%',c.key1,'%')   \n";
			sql = sql + "      and '管理费用' like concat('%',d.key1,'%')   \n";
			sql = sql + "        \n";
			sql = sql + "      union  \n";
			sql = sql + "      select 1 as direction ,'财务费用' as gs   \n";
			sql = sql + "      union   \n";
			sql = sql + "      select 1 as direction ,trim(replace(replace(replace('财务费用              ',b.key1,b.key2),c.key1,c.key2),d.key1,d.key2))   \n";
			sql = sql + "      from k_key b,k_key c,k_key d   \n";
			sql = sql + "      where '财务费用' like concat('%',b.key1,'%')   \n";
			sql = sql + "      and '财务费用' like concat('%',c.key1,'%')   \n";
			sql = sql + "      and '财务费用' like concat('%',d.key1,'%')   \n";
			sql = sql + "        \n";
			sql = sql + " ) c \n";
			sql = sql + " where  a.accName =c.gs \n";
			sql = sql + ") a \n";

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			} else {
				return "nothing";
			}
		} catch (Exception e) {
			e.printStackTrace();
			//      return "nothing";
		}//这里不需要关闭conn，因为引用了本类的公共conn.
		return format(result);
	}
	
	
//	获取 "毛利"的值　，没有则返回null
	//毛利=（主营业务收入+其他业务收入）-（主营业务成本+其他业务成本）
	public String getML(Connection c, String apkID) {
		double result = 0.00;

		Connection conn = c;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			String sql = "select sum(a.Occ*a.direction) \n";
			sql = sql + " from \n";
			sql = sql + "( \n";
			sql = sql + "select a.debittotalocc as occ,c.direction from \n";

			sql = sql + "  ( select * from  c_account\n";
			sql = sql + "  where accpackageid=" + apkID + " \n";
			sql = sql + "  and submonth=" + iProjectEndMonth + " \n";
			sql = sql + " ) a,  \n";
			sql = sql + " ( \n";
			sql = sql + "      select -1 as direction ,'主营业务成本' as gs   \n";
			sql = sql + "      union   \n";
			sql = sql
					+ "      select -1 as direction ,trim(replace(replace(replace('主营业务成本              ',b.key1,b.key2),c.key1,c.key2),d.key1,d.key2))   \n";
			sql = sql + "      from k_key b,k_key c,k_key d   \n";
			sql = sql + "      where '主营业务成本' like concat('%',b.key1,'%')   \n";
			sql = sql + "      and '主营业务成本' like concat('%',c.key1,'%')   \n";
			sql = sql + "      and '主营业务成本' like concat('%',d.key1,'%')   \n";
			sql = sql + "        \n";
			sql = sql + "      union  \n";
			sql = sql + "      select -1 as direction ,'其他业务支出' as gs   \n";
			sql = sql + "      union   \n";
			sql = sql
					+ "      select -1 as direction ,trim(replace(replace(replace('其他业务支出              ',b.key1,b.key2),c.key1,c.key2),d.key1,d.key2))   \n";
			sql = sql + "      from k_key b,k_key c,k_key d   \n";
			sql = sql + "      where '其他业务支出' like concat('%',b.key1,'%')   \n";
			sql = sql + "      and '其他业务支出' like concat('%',c.key1,'%')   \n";
			sql = sql + "      and '其他业务支出' like concat('%',d.key1,'%')   \n";
			sql = sql + "    \n";
			sql = sql + " ) c \n";
			sql = sql + " where  a.accName =c.gs \n";

			sql = sql + " union all \n";

			sql = sql + "  select a.credittotalocc as occ,c.direction from \n";

			sql = sql + "  ( select * from  c_account\n";
			sql = sql + "  where accpackageid=" + apkID + " \n";
			sql = sql + "  and submonth=" + iProjectEndMonth + " \n";
			sql = sql + " ) a,  \n";
			sql = sql + " ( \n";
			sql = sql + "      select 1 as direction ,'主营业务收入' as gs   \n";
			sql = sql + "      union   \n";
			sql = sql
					+ "      select 1 as direction ,trim(replace(replace(replace('主营业务收入              ',b.key1,b.key2),c.key1,c.key2),d.key1,d.key2))   \n";
			sql = sql + "      from k_key b,k_key c,k_key d   \n";
			sql = sql + "      where '主营业务收入' like concat('%',b.key1,'%')   \n";
			sql = sql + "      and '主营业务收入' like concat('%',c.key1,'%')   \n";
			sql = sql + "      and '主营业务收入' like concat('%',d.key1,'%')   \n";
			sql = sql + "        \n";
			sql = sql + "      union  \n";
			sql = sql + "      select +1 as direction ,'其他业务收入' as gs   \n";
			sql = sql + "      union   \n";
			sql = sql
					+ "      select 1 as direction ,trim(replace(replace(replace('其他业务收入              ',b.key1,b.key2),c.key1,c.key2),d.key1,d.key2))   \n";
			sql = sql + "      from k_key b,k_key c,k_key d   \n";
			sql = sql + "      where '其他业务收入' like concat('%',b.key1,'%')   \n";
			sql = sql + "      and '其他业务收入' like concat('%',c.key1,'%')   \n";
			sql = sql + "      and '其他业务收入' like concat('%',d.key1,'%')   \n";
			sql = sql + "    \n";
			sql = sql + " ) c \n";
			sql = sql + " where  a.accName =c.gs \n";

			sql = sql + ") a \n";

			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			} else {
				return "nothing";
			}
		} catch (Exception e) {
			e.printStackTrace();
			//      return "nothing";
		}//这里不需要关闭conn，因为引用了本类的公共conn.
		return format(result);
	}

	private void setReport(String args, int x, int y) {
		this.report[x][y] = args;
	}

	private String getReport(int x, int y) {
		return report[x][y];
	}

	public String getReportResult(int x, int y) {
		if (report[x][y] == null) {
			return "";
		}
		if (report[x][y].equals("nothing")) {
			return "";
		}
		if (report[x][y].equals("")) {
			return "";
		}

		return df2.format(Double.parseDouble(report[x][y]));

	}

	private void setChoseEL(String choseEL) {
		this.choseEL = choseEL;
	}

	public String getChoseEL() {
		return choseEL;
	}

	public String getSubjectSum() {
		return subjectSum;
	}

	public int getLevel1SubjectCount() {
		return level1SubjectCount;
	}

	public void setLevel1SubjectCount(int level1SubjectCount) {
		this.level1SubjectCount = level1SubjectCount;
	}

}
