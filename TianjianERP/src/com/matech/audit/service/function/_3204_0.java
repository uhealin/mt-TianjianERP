package com.matech.audit.service.function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.matech.audit.service.dataupload.DisposeTableService;
import com.matech.audit.service.dataupload.UploadItemService;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.util.ASFuntion;

/**
 * 
 * @author zyq
 * 
 * 提供按卡片分类进行分组统计的参数，此功能用于刷出分类明细表 支持： 提供输出列为: 固定资产名称:itemname 固定资产上年余额:remain
 * 固定资产本年增加:debitocc 固定资产本年减少:creditocc 固定资产年末未审数:balance 固定资产年末审定数:Validation
 * 累计折旧上年余额:Depreremain 累计折旧本年增加:DepreAdd 累计折旧本年减少:DepreMinus
 * 累计折旧年末未审数:DepreBalance 累计折旧年末审定数:DepreValidation 调整增加:debitsov 调整减少:creditsov
 * 
 * 取得时间:adddate 使用年限:period 固定资产原值:remain(initvalue) 残值率:remainvalue
 * 累计折旧期初余额:Depreremain(depreremain)、 减值准备期初余额:reservedremain 本期应提折旧:、
 * 本期已提折旧:hasdepre
 * 
 * 参数有： 类型：ctype，值为：原值, 累计折旧、减值准备或 折旧试算检查表，必填参数；
 * 科目名称：subjectName，指定科目名称，选填参数；如果没有填，就按照原值、累计折旧自动判断； 如果是原值，就找： 固定资产（标准科目名）；
 * 如果是累计折旧，就找：累计折旧（标准科目名）；
 * 核算名称：assitemName，指定核算名称，选填参数；只在没有固定资产模块，且相关科目上定义了固定资产辅助核算时才有效； 缺省值＝"固定资产"；
 * 固定资产增减：directtion，如果direction=1；刷固定资产增加大于0的，direction=2刷固定资产减少大于0的
 * 如:=取列公式覆盖(3204, "", "itemname","&ctype=原值")
 * 
 */

public class _3204_0 extends AbstractAreaFunction {
	
	String accpackageid = "";

	public ResultSet process(HttpSession session, HttpServletRequest request,
			HttpServletResponse response, Connection conn, Map args)
			throws Exception {

		accpackageid = (String) args.get("curAccPackageID");
		String curProjectid = (String) args.get("curProjectid");
		String ctype = (String) args.get("ctype");// 原值或累计折旧
		String subjectName = (String) args.get("subjectName");
		String assitemName = (String) args.get("assitemName");
		String direction = (String) args.get("direction");//固定资产增减
		String itemType = (String) args.get("itemType");//按固定资产类别刷对应类别下的明细
		String standType = (String) args.get("standType");//按固定资产标准类别汇总

		// if("".equals(assitemName) || assitemName == null) {
		// assitemName = "固定资产";
		// }
		String resultSql = "";
		String subjectid = "";
		String sql = "";
		Statement st = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		args.put("curPackageid", accpackageid);
		args.put("curProjectid", curProjectid);
		UploadItemService ui = new UploadItemService(conn, accpackageid);
		try {
			st = conn.createStatement();
			boolean bool = new DisposeTableService(conn)
			.checkTableExist("fa_" + accpackageid);
			String str = accpackageid.substring(6);
			if (bool)str = accpackageid;
			

			
			
			if ("折旧试算检查表".equals(ctype)) {// 刷折旧试算检查表
				if ("".equals(subjectName) || subjectName == null) {
					subjectName = "固定资产";
				}
				if (true) {
					if("".equals(itemType) || itemType==null) {
						itemType = "";
					} else {//刷指定类别的明细
						String typesql = "";
						String userType = "";
						int countnum = 0;
						typesql = " select usertype from fa_typecompare \n"
								+ " where standtype='"+itemType+"' and accpackageid="+accpackageid+"";
						ps = conn.prepareStatement(typesql);
						rs = ps.executeQuery();
						while(rs.next()) {
							countnum++;
							userType += " subjectfullname1 like '"+rs.getString(1)+"%' or";
						}
						if(countnum>0) {
							userType = userType.substring(0, userType.length()-2);
							itemType = " and ( "+userType+" )";
						} else {
							itemType = " and subjectfullname1 like '"+itemType+"/%' ";
						}
					}
					if(checkSubjectname(subjectName,accpackageid,conn)) {
						subjectName = "subjectfullname2 like '"
									+ subjectName
									+ "/%'";
					} else {
						subjectName = "subjectfullname2 like '"
									+ subjectName
									+ "/%' or subjectfullname2='"
									+ subjectName+"'";
					}
					resultSql = getSubjectSql(subjectName,itemType,standType);
				} else {
					if("".equals(itemType) || itemType==null) {
						itemType = "";
					} else {//刷指定类别的明细
						String typesql = "";
						String userType = "";
						int countnum = 0;
						typesql = " select usertype from fa_typecompare \n"
								+ " where standtype='"+itemType+"' and accpackageid="+accpackageid+"";
						ps = conn.prepareStatement(typesql);
						rs = ps.executeQuery();
						while(rs.next()) {
							countnum++;
							userType += " b.fullpathitemname like '"+rs.getString(1)+"/%' or";
						}
						if(countnum>0) {
							userType = userType.substring(0, userType.length()-2);
							itemType = " and ( "+userType+" )";
						} else {
							itemType = " and b.fullpathitemname like '"+itemType+"/%' ";
						}
					}
					
					// 根据条件构造取数Sql
//					用临时表来存放十二个月的
					this.tempTable = "tt_"+DELUnid.getCharUnid();
					String strSelectSql = "select distinct itemno  from fa_account where accpackageid = '"+accpackageid+"' and submonth=1 and isleaf=1";
					String sql1 = "create table " + tempTable + " " + strSelectSql;
					st.execute(sql1);
					try{
						st.execute("alter table " + tempTable + " add index itemno (itemno)");
					}catch(Exception e){
						System.out.println("sid太短了");
					}
					sql1="alter table "+this.tempTable+" add column value1 decimal (16,2) DEFAULT '0.00' NOT NULL , add column value2 decimal (16,2) DEFAULT '0.00' NOT NULL" +
							", add column value3 decimal (16,2) DEFAULT '0.00' NOT NULL, add column value4 decimal (16,2) DEFAULT '0.00' NOT NULL, add column value5 decimal (16,2) DEFAULT '0.00' NOT NULL " +
							", add column value6 decimal (16,2) DEFAULT '0.00' NOT NULL, add column value7 decimal (16,2) DEFAULT '0.00' NOT NULL, add column value8 decimal (16,2) DEFAULT '0.00' NOT NULL" +
							", add column value9 decimal (16,2) DEFAULT '0.00' NOT NULL, add column value10 decimal (16,2) DEFAULT '0.00' NOT NULL, add column value11 decimal (16,2) DEFAULT '0.00' NOT NULL" +
							" , add column value12 decimal (16,2) DEFAULT '0.00' NOT NULL ";
					st.execute(sql1);
					String alterSql = "";
					System.out.println("a1:" + new ASFuntion().getCurrentTime());
					for(int i=1;i<=12;i++) {
						alterSql = "update "+tempTable+" a,("
							+" select distinct itemno,(DepreAdd-DepreMinus) as myvalue from fa_account \n"
							+" where accpackageid="+accpackageid+" and submonth="+i+" and isleaf=1) b set a.value"+i+"=b.myvalue where a.itemno=b.itemno \n";
						st.execute(alterSql);
					}
					System.out.println("a2:" + new ASFuntion().getCurrentTime());
					if(!"".equals(direction) && direction!=null) {
						if("1".equals(direction)) {//固定资产增加大于0
							direction =  " and abs(c.initAdd) >0 ";
						} else if("2".equals(direction)) {//固定资产减少大于0
							direction =  " and abs(c.initMinus) >0 ";
						} else {
							direction = "";
						}
					} else {
						direction = "";
					}
					resultSql = getItemDetial(str,subjectName,tempTable,direction,itemType);
				}
			} else {// 刷原值或累计折旧

				if ("".equals(ctype) || ctype == null) {
					throw new Exception("ctype类型不能为空");
				} else if (!"原值".equals(ctype) && !"累计折旧".equals(ctype) && !"减值准备".equals(ctype)) {
					throw new Exception("ctype的值必须为原值、累计折旧或减值准备!");
				}
				if ("".equals(subjectName) || subjectName == null) {
					if ("原值".equals(ctype)) {
						subjectName = "固定资产";
					} else if ("累计折旧".equals(ctype)) {
						subjectName = "累计折旧";
					} else {
						subjectName = "减值准备";
					}
				}
				org.util.Debug.prtErr(resultSql);

				// 求出指定科目对应的科目编号
				sql = " select ifnull(group_concat(distinct subjectid SEPARATOR \"','\"),-1) from c_account \n"
						+ "	where (subjectfullname2 like '"
						+ subjectName
						+ "/%' or subjectfullname2='"
						+ subjectName
						+ "') \n"
						+ "	and accpackageid=? ";
				System.out.println("zyq:" + sql);
				ps = conn.prepareStatement(sql);
				ps.setString(1, accpackageid);
				rs = ps.executeQuery();
				if (rs.next()) {
					subjectid = "'" + rs.getString(1) + "'";
				}
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (true) {
					// 没有固定资产模块!
					if ("".equals(assitemName) || assitemName == null) {// 没有指定核算名称就取默认值：固定资产
						assitemName = "固定资产";
						if (ui.AssitemExist(assitemName, subjectName) > 0) {// 存在指定科目的核算
							// 刷指定科目的核算
							resultSql = getAssitemSql(assitemName, subjectid,standType);
						} else if (ui.SubjectExist(subjectName) > 0) {
							///////
							if(checkSubjectname(subjectName,accpackageid,conn)) {
								subjectName = "subjectfullname2 like '"
											+ subjectName
											+ "/%'";
							} else {
								subjectName = "subjectfullname2 like '"
											+ subjectName
											+ "/%' or subjectfullname2='"
											+ subjectName+"'";
							}
							resultSql = getSubjectSql(subjectName,"",standType);
						} else {
							throw new Exception("");
						}
					} else { 
						if (ui.AssitemExist(assitemName, subjectName) > 0) {// 存在指定科目的核算
							resultSql = getAssitemSql(assitemName, subjectid,standType);
						} else if (ui.AssitemExist(assitemName, "固定资产") > 0) {
							// 没有相关科目的核算，但是只有一个不叫做部门的核算，就刷核算余额表
							sql = " select ifnull(group_concat(distinct subjectid SEPARATOR \"','\"),-1) from c_account \n"
									+ "	where (subjectfullname2 like '固定资产/%' or subjectfullname2='固定资产') \n"
									+ "	and accpackageid=? ";
							ps = conn.prepareStatement(sql);
							ps.setString(1, accpackageid);
							rs = ps.executeQuery();
							if (rs.next()) {
								subjectid = "'" + rs.getString(1) + "'";
							}
							if (rs != null) {
								rs.close();
							}
							if (ps != null) {
								ps.close();
							}
							resultSql = getAssitemSql("固定资产", subjectid,standType);
						} else if (ui.SubjectExist(subjectName) > 0) {
							// 只刷科目
							//////
							if(checkSubjectname(subjectName,accpackageid,conn)) {
								subjectName = "subjectfullname2 like '"
											+ subjectName
											+ "/%'";
							} else {
								subjectName = "subjectfullname2 like '"
											+ subjectName
											+ "/%' or subjectfullname2='"
											+ subjectName+"'";
							}
							resultSql = getSubjectSql(subjectName,"",standType);
						} else {
							//throw new Exception("没有此账套[" + accpackageid
							//		+ "]的固定资产!");
						}
					}

				} else {// 按固定资产模块刷数
					resultSql = getItemSql(str,subjectName,standType);
				}
			}

			// 最终查询结果
			resultSql = this.setSqlArguments(resultSql, args);
			System.out.println("resultSql1=" + resultSql);
			st.executeQuery("set   charset   gbk;");
			rs = st.executeQuery(resultSql);
			System.out.println("a3:" + new ASFuntion().getCurrentTime());
			
			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(ps);
		}
	}

	public String getItemSql(String str,String subjectName,String standType) {// 按固定资产模块刷数
		String resultSql = "";
		String mySubjectId =  " ( \n"
				 +" select distinct subjectid from c_account \n"
				 +" where subjectfullname2='"+subjectName+"' and accpackageid='${curPackageid}' \n"
				 +" ) ";// and itemtype != '' and itemtype is not null \n";
		 
		String myYear = accpackageid.substring(6);
		System.out.println("zyq:"+myYear);
		if(!"1".equals(standType)) {
		resultSql = "select a.itemno,a.itemname,a.initremain as remain,a.initremain as initvalue,a.Depreremain as Depreremain,b.initbalance as balance,b.DepreBalance as DepreBalance, \n"
				+ " c.initadd as debitocc,c.initminus as creditocc,c.DepreAdd as DepreAdd,c.DepreMinus as DepreMinus,ifnull(d.debitsov,0) as debitsov, \n"
				+ " ifnull(d.creditsov,0) as creditsov,(b.initbalance+ifnull(d.debitsov,0)-ifnull(d.creditsov,0)) as Validation,(b.ReservedBalance-ifnull(d.debitsov,0)+ifnull(d.creditsov,0)) as ReservedValidation, \n"
				+ " (b.DepreBalance-ifnull(d.debitsov,0)+ifnull(d.creditsov,0)) as DepreValidation,'' as adddate,'' as period,'' as remainvalue, "
				+ " a.reservedremain as reservedremain,b.ReservedBalance as ReservedBalance,c.ReservedAdd as ReservedAdd,c.ReservedMinus as ReservedMinus,'' as hasdepre,'' as ItemClass,'' as Quantity,'' as status,'' as DelDate,'' as AddMethod,'' as DelMethod,'' as Depramount,"
				+ " '' as value1,'' as value2,'' as value3,'' as value4,'' as value5,'' as value6,'' as value7,'' as value8,'' as value9,'' as value10,'' as value11,'' as value12,'' as DeprePercent,'' as ChangeMethod,'' as addr,'' as RemainPercent,'' as spec,'' as UnitName,d.debittotalocc1,d.debittotalocc2,d.debittotalocc4,d.debittotalocc5,d.credittotalocc1,d.credittotalocc2,d.credittotalocc4,d.credittotalocc5  \n"
				+ " from ( \n"
				+ " select itemno,itemname,initremain,Depreremain,ReservedRemain \n"
				+ " from (select * from fa_account where isleaf=0 and accpackageid='${curPackageid}') a,(select distinct parentitem from fa_account where isleaf = 1 and accpackageid='${curPackageid}' and submonth=1) b \n"
				+ " where a.itemname = b.parentitem and a.accpackageid='${curPackageid}' and a.submonth=1 \n"
				+ " group by itemno \n"
				+ " union \n"
				+ " select itemno,itemname,initremain,Depreremain,ReservedRemain \n"
				+ " from fa_account \n"
				+ " where accpackageid='${curPackageid}' and isleaf=1 and level1=1 \n"
				+ " group by itemno \n"
				+ " ) a \n"
				+ " left join \n"
				+ " ( \n"
				+ " select itemno,itemname,initbalance,DepreBalance,ReservedBalance \n"
				+ " from (select * from fa_account where isleaf=0 and accpackageid='${curPackageid}') a,(select distinct parentitem from fa_account where isleaf = 1 and accpackageid='${curPackageid}' and submonth=12) b \n"
				+ " where a.itemname = b.parentitem and a.accpackageid='${curPackageid}' and a.submonth=12 \n"
				+ " group by itemno \n"
				+ " union \n"
				+ " select itemno,itemname,initremain,DepreBalance,ReservedBalance \n"
				+ " from fa_account \n"
				+ " where accpackageid='${curPackageid}' and isleaf=1 and level1=1 \n"
				+ " group by itemno \n"
				+ " ) b  \n"
				+ " on a.itemno=b.itemno \n"
				+ " left join \n"
				+ " ( \n"
				+ " select itemno,sum(initadd) as initadd,sum(initminus) as initminus,sum(DepreAdd) as DepreAdd, sum(DepreMinus) as DepreMinus,sum(ReservedAdd) as ReservedAdd,sum(ReservedMinus) as ReservedMinus \n"
				+ " from (select * from fa_account where isleaf=0 and accpackageid='${curPackageid}') a,(select distinct parentitem from fa_account where isleaf = 1 and accpackageid='${curPackageid}' ) b \n"
				+ " where a.itemname = b.parentitem and a.accpackageid='${curPackageid}' \n"
				+ " group by itemno \n"
				+ " union \n"
				+ " select  itemno,sum(initadd) as initadd,sum(initminus) as initminus,sum(DepreAdd) as DepreAdd, sum(DepreMinus) as DepreMinus,sum(ReservedAdd) as ReservedAdd,sum(ReservedMinus) as ReservedMinus \n"
				+ " from fa_account where isleaf = 1 and accpackageid='${curPackageid}' and level1=1 \n"
				+ " group by itemno \n"
				+ " ) c \n"
				+ " on a.itemno=c.itemno \n"
				+ " left join \n"
				+ " (" 
				
				
//				+ " select a.itemtype as itemno,b.parentitem,sum(a.debitsov) as debitsov,sum(a.creditsov) as creditsov from (\n "
//				+ " select accpackageid,projectid,itemtype as itemno,sum(if(dirction='1',occurvalue,'0')) as debitsov,sum(if(dirction='-1',occurvalue,'0')) as creditsov,itemtype \n "
//				+ " from z_subjectentryrectify \n "
//				+ " where itemtype != '' and itemtype is not null and accpackageid='${curPackageid}' and projectid='${curProjectid}' and (property like '3%' or property like '4%' or property like '5%') \n "
//				+" and subjectid in \n"
//				 +" ( \n"
//				 +" select distinct subjectid from c_account \n"
//				 +" where subjectfullname2='"+subjectName+"' and accpackageid='${curPackageid}' \n"
//				 +" ) \n"
//				+ " group by itemtype "
				
				+ "select a.itemno as itemno,b.parentitem,a.debitsov as debitsov,a.creditsov as creditsov,a.debittotalocc1,a.debittotalocc2,a.debittotalocc4,a.debittotalocc5,a.credittotalocc1,a.credittotalocc2,a.credittotalocc4,a.credittotalocc5 from (\n "
				 + " select z.subjectid,a.itemno,ifnull(a.debittotalocc1,0) debittotalocc1,ifnull(b.debittotalocc2,0) debittotalocc2,ifnull(c.debittotalocc4,0) debittotalocc4,ifnull(d.debittotalocc5,0) debittotalocc5,ifnull(e.credittotalocc1,0) credittotalocc1,ifnull(f.credittotalocc2,0) credittotalocc2,ifnull(g.credittotalocc4,0) credittotalocc4,ifnull(h.credittotalocc5,0) credittotalocc5, \n"
					+ " (ifnull(a.debittotalocc1,0)+ifnull(b.debittotalocc2,0)+ifnull(c.debittotalocc4,0)+ifnull(d.debittotalocc5,0)) debitsov,(ifnull(e.credittotalocc1,0)+ifnull(f.credittotalocc2,0)+ifnull(g.credittotalocc4,0)+ifnull(h.credittotalocc5,0)) creditsov \n"
					+ "  from ( \n"
					+ "  select distinct subjectid from c_account  where subjectfullname2 like '"+subjectName+"/%' or subjectfullname2='"+subjectName+"' and accpackageid='1000022008' \n"
					+ "  ) z left join " +
							"( \n"
					+ " select subjectid,itemtype as itemno,ifnull(sum(occurvalue),0) debittotalocc1 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
					+ " and  dirction=1  and vchdate like '"+myYear+"%' and property like '3%' group by itemtype) a on z.subjectid=a.subjectid left join ( \n"//#借年末调整
					+ " select subjectid,ifnull(sum(occurvalue),0) debittotalocc2 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
					+ " and  dirction=1  and vchdate like '"+myYear+"%' and property like '4%' group by itemtype) b on z.subjectid=b.subjectid left join ( \n"//#借年末重分类
					+ " select subjectid,ifnull(sum(occurvalue),0) debittotalocc4 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
					+ " and  dirction=1  and vchdate < substring('${curPackageid}',7) and property like '3%' group by itemtype) c on z.subjectid=c.subjectid left join ( \n"//#借年初调整
					+ " select subjectid,ifnull(sum(occurvalue),0) debittotalocc5 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
					+ " and  dirction=1  and vchdate < substring('${curPackageid}',7) and property like '4%' group by itemtype) d on z.subjectid=d.subjectid left join ( \n"//#借年初重分类
					+ " select subjectid,ifnull(sum(occurvalue),0) credittotalocc1 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
					+ " and  dirction=-1  and vchdate like '"+myYear+"%' and property like '3%' group by itemtype) e on z.subjectid=e.subjectid left join ( \n"//#贷年末调整
					+ " select subjectid,ifnull(sum(occurvalue),0) credittotalocc2 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
					+ " and  dirction=-1  and vchdate like '"+myYear+"%' and property like '4%' group by itemtype) f on z.subjectid=f.subjectid left join ( \n"//#贷年末重分类
					+ " select subjectid,ifnull(sum(occurvalue),0) credittotalocc4 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
					+ " and  dirction=-1  and vchdate < substring('${curPackageid}',7) and property like '3%' group by itemtype) g on z.subjectid=g.subjectid left join ( \n"//#贷年初调整
					+ " select subjectid,ifnull(sum(occurvalue),0) credittotalocc5 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
					+ " and  dirction=-1  and vchdate < substring('${curPackageid}',7) and property like'4%' group by itemtype) h on z.subjectid=h.subjectid \n"//#贷年初重分类
				 
				 
				
				
				+ ") a left join (select * from fa_account where accpackageid='${curPackageid}' and submonth=1) b \n "
				+ " on a.itemno=b.itemno \n "
				+ " group by b.parentitem\n "
				+ " ) d \n "
				+ " on a.itemno=d.parentitem \n " + " order by a.itemno";
		} else {
	//标准类别汇总	
		resultSql = "select a.name as itemname,b.* from ( \n"
			+ " select name from k_dic where ctype='standtype' ) a left join ( "
			+ " select a.itemno,a.itemname,e.standtype,sum(a.initremain) as remain,sum(a.initremain) as initvalue,sum(a.Depreremain) as Depreremain,sum(b.initbalance) as balance,sum(b.DepreBalance) as DepreBalance, \n"
			+ " sum(c.initadd) as debitocc,sum(c.initminus) as creditocc,sum(c.DepreAdd) as DepreAdd,sum(c.DepreMinus) as DepreMinus,sum(ifnull(d.debitsov,0)) as debitsov, \n"
			+ " sum(ifnull(d.creditsov,0)) as creditsov,sum((b.initbalance+ifnull(d.debitsov,0)-ifnull(d.creditsov,0))) as Validation,sum((b.ReservedBalance-ifnull(d.debitsov,0)+ifnull(d.creditsov,0))) as ReservedValidation, \n"
			+ " sum((b.DepreBalance-ifnull(d.debitsov,0)+ifnull(d.creditsov,0))) as DepreValidation,'' as adddate,'' as period,'' as remainvalue, "
			+ " sum(a.reservedremain) as reservedremain,sum(b.ReservedBalance) as ReservedBalance,sum(c.ReservedAdd) as ReservedAdd,sum(c.ReservedMinus) as ReservedMinus,'' as hasdepre,'' as ItemClass,'' as Quantity,'' as status,'' as DelDate,'' as AddMethod,'' as DelMethod,'' as Depramount,"
			+ " '' as value1,'' as value2,'' as value3,'' as value4,'' as value5,'' as value6,'' as value7,'' as value8,'' as value9,'' as value10,'' as value11,'' as value12,'' as DeprePercent,'' as ChangeMethod,'' as addr,'' as RemainPercent,'' as spec,'' as UnitName,d.debittotalocc1,d.debittotalocc2,d.debittotalocc4,d.debittotalocc5,d.credittotalocc1,d.credittotalocc2,d.credittotalocc4,d.credittotalocc5  \n"
			+ " from ( \n"
			+ " select itemno,itemname,initremain,Depreremain,ReservedRemain \n"
			+ " from (select * from fa_account where isleaf=0 and accpackageid='${curPackageid}') a,(select distinct parentitem from fa_account where isleaf = 1 and accpackageid='${curPackageid}' and submonth=1) b \n"
			+ " where a.itemname = b.parentitem and a.accpackageid='${curPackageid}' and a.submonth=1 \n"
			+ " group by itemno \n"
			+ " union \n"
			+ " select itemno,itemname,initremain,Depreremain,ReservedRemain \n"
			+ " from fa_account \n"
			+ " where accpackageid='${curPackageid}' and isleaf=1 and level1=1 \n"
			+ " group by itemno \n"
			+ " ) a \n"
			+ " left join \n"
			+ " ( \n"
			+ " select itemno,itemname,initbalance,DepreBalance,ReservedBalance \n"
			+ " from (select * from fa_account where isleaf=0 and accpackageid='${curPackageid}') a,(select distinct parentitem from fa_account where isleaf = 1 and accpackageid='${curPackageid}' and submonth=12) b \n"
			+ " where a.itemname = b.parentitem and a.accpackageid='${curPackageid}' and a.submonth=12 \n"
			+ " group by itemno \n"
			+ " union \n"
			+ " select itemno,itemname,initremain,DepreBalance,ReservedBalance \n"
			+ " from fa_account \n"
			+ " where accpackageid='${curPackageid}' and isleaf=1 and level1=1 \n"
			+ " group by itemno \n"
			+ " ) b  \n"
			+ " on a.itemno=b.itemno \n"
			+ " left join \n"
			+ " ( \n"
			+ " select itemno,sum(initadd) as initadd,sum(initminus) as initminus,sum(DepreAdd) as DepreAdd, sum(DepreMinus) as DepreMinus,sum(ReservedAdd) as ReservedAdd,sum(ReservedMinus) as ReservedMinus \n"
			+ " from (select * from fa_account where isleaf=0 and accpackageid='${curPackageid}') a,(select distinct parentitem from fa_account where isleaf = 1 and accpackageid='${curPackageid}' ) b \n"
			+ " where a.itemname = b.parentitem and a.accpackageid='${curPackageid}' \n"
			+ " group by itemno \n"
			+ " union \n"
			+ " select  itemno,sum(initadd) as initadd,sum(initminus) as initminus,sum(DepreAdd) as DepreAdd, sum(DepreMinus) as DepreMinus,sum(ReservedAdd) as ReservedAdd,sum(ReservedMinus) as ReservedMinus \n"
			+ " from fa_account where isleaf = 1 and accpackageid='${curPackageid}' and level1=1 \n"
			+ " group by itemno \n"
			+ " ) c \n"
			+ " on a.itemno=c.itemno \n"
			+ " left join \n"
			+ " ( "
			
			
//			+ "select a.itemtype as itemno,b.parentitem,sum(a.debitsov) as debitsov,sum(a.creditsov) as creditsov from (\n "
//			+ " select accpackageid,projectid,itemtype as itemno,sum(if(dirction='1',occurvalue,'0')) as debitsov,sum(if(dirction='-1',occurvalue,'0')) as creditsov,itemtype \n "
//			+ " from z_subjectentryrectify \n "
//			+ " where itemtype != '' and itemtype is not null and accpackageid='${curPackageid}' and projectid='${curProjectid}' and (property like '3%' or property like '4%' or property like '5%') \n "
//			+" and subjectid in \n"
//			 +" ( \n"
//			 +" select distinct subjectid from c_account \n"
//			 +" where subjectfullname2='"+subjectName+"' and accpackageid='${curPackageid}' \n"
//			 +" ) \n"
//			 + " group by itemtype"
			 
			+ "select a.itemno as itemno,b.parentitem,a.debitsov as debitsov,a.creditsov as creditsov,a.debittotalocc1,a.debittotalocc2,a.debittotalocc4,a.debittotalocc5,a.credittotalocc1,a.credittotalocc2,a.credittotalocc4,a.credittotalocc5 from (\n "
			 + " select z.subjectid,a.itemno,ifnull(a.debittotalocc1,0) debittotalocc1,ifnull(b.debittotalocc2,0) debittotalocc2,ifnull(c.debittotalocc4,0) debittotalocc4,ifnull(d.debittotalocc5,0) debittotalocc5,ifnull(e.credittotalocc1,0) credittotalocc1,ifnull(f.credittotalocc2,0) credittotalocc2,ifnull(g.credittotalocc4,0) credittotalocc4,ifnull(h.credittotalocc5,0) credittotalocc5, \n"
				+ " (ifnull(a.debittotalocc1,0)+ifnull(b.debittotalocc2,0)+ifnull(c.debittotalocc4,0)+ifnull(d.debittotalocc5,0)) debitsov,(ifnull(e.credittotalocc1,0)+ifnull(f.credittotalocc2,0)+ifnull(g.credittotalocc4,0)+ifnull(h.credittotalocc5,0)) creditsov \n"
				+ "  from ( \n"
				+ "  select distinct subjectid from c_account  where subjectfullname2 like '"+subjectName+"/%' or subjectfullname2='"+subjectName+"' and accpackageid='1000022008' \n"
				+ "  ) z left join " +
						"( \n"
				+ " select subjectid,itemtype as itemno,ifnull(sum(occurvalue),0) debittotalocc1 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
				+ " and  dirction=1  and vchdate like '"+myYear+"%' and property like '3%' group by itemtype) a on z.subjectid=a.subjectid left join ( \n"//#借年末调整
				+ " select subjectid,ifnull(sum(occurvalue),0) debittotalocc2 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
				+ " and  dirction=1  and vchdate like '"+myYear+"%' and property like '4%' group by itemtype) b on z.subjectid=b.subjectid left join ( \n"//#借年末重分类
				+ " select subjectid,ifnull(sum(occurvalue),0) debittotalocc4 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
				+ " and  dirction=1  and vchdate < substring('${curPackageid}',7) and property like '3%' group by itemtype) c on z.subjectid=c.subjectid left join ( \n"//#借年初调整
				+ " select subjectid,ifnull(sum(occurvalue),0) debittotalocc5 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
				+ " and  dirction=1  and vchdate < substring('${curPackageid}',7) and property like '4%' group by itemtype) d on z.subjectid=d.subjectid left join ( \n"//#借年初重分类
				+ " select subjectid,ifnull(sum(occurvalue),0) credittotalocc1 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
				+ " and  dirction=-1  and vchdate like '"+myYear+"%' and property like '3%' group by itemtype) e on z.subjectid=e.subjectid left join ( \n"//#贷年末调整
				+ " select subjectid,ifnull(sum(occurvalue),0) credittotalocc2 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
				+ " and  dirction=-1  and vchdate like '"+myYear+"%' and property like '4%' group by itemtype) f on z.subjectid=f.subjectid left join ( \n"//#贷年末重分类
				+ " select subjectid,ifnull(sum(occurvalue),0) credittotalocc4 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
				+ " and  dirction=-1  and vchdate < substring('${curPackageid}',7) and property like '3%' group by itemtype) g on z.subjectid=g.subjectid left join ( \n"//#贷年初调整
				+ " select subjectid,ifnull(sum(occurvalue),0) credittotalocc5 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
				+ " and  dirction=-1  and vchdate < substring('${curPackageid}',7) and property like'4%' group by itemtype) h on z.subjectid=h.subjectid \n"//#贷年初重分类
			 
			 
			 
			 
			+ ") a left join (select * from fa_account where accpackageid='${curPackageid}' and submonth=1) b \n "
			+ " on a.itemno=b.itemno \n "
			+ " group by b.parentitem\n "
			+ " ) d \n "
			+ " on a.itemno=d.parentitem \n "
			+ " left join fa_typecompare e \n"
			+ " on a.itemname=e.usertype \n"
			+ " group by e.standtype ) b \n"
			+ " on a.name=b.standtype "
			+ " order by a.name";
		}
		return resultSql;
	}

	public String getSubjectSql(String subjectName,String itemType,String standType) {// 刷科目明细
		String resultSql = "";
		String mySubjectId = " (select distinct subjectid from c_account " 
							+" where "+subjectName+" and accpackageid='${curPackageid}') ";// and itemtype != '' and itemtype is not null";
		String myYear = accpackageid.substring(6);
		
		if(!"1".equals(standType)) {
		resultSql = "select a.accname1 as itemname,a.remain,a.remain as Depreremain,a.remain as initvalue,b.debitocc,b.debitocc as DepreAdd,b.debitocc as ReservedAdd,b.creditocc,b.creditocc as DepreMinus,b.creditocc as ReservedMinus,d.balance,d.direction2*d.balance as DepreBalance,d.direction2*d.balance as ReservedBalance,ifnull(e.debitsov,0) as debitsov,ifnull(e.creditsov,0) as creditsov, \n"
				+ " d.direction2*(d.balance+ifnull(e.debitsov,0)-ifnull(e.creditsov,0)) as Validation,d.direction2*(d.balance-ifnull(e.debitsov,0)+ifnull(e.creditsov,0)) as DepreValidation,direction2*(d.balance-ifnull(e.debitsov,0)+ifnull(e.creditsov,0)) as ReservedValidation,'' as adddate,'' as period,'' as remainvalue,a.remain as reservedremain,'' as hasdepre,'' as itemno, \n"
				+ " '' as ItemClass,'' as Quantity,'' as status,'' as DelDate,'' as AddMethod,'' as DelMethod,'' as Depramount, \n"
				+ " '' as value1,'' as value2,'' as value3,'' as value4,'' as value5,'' as value6,'' as value7,'' as value8,'' as value9,'' as value10,'' as value11,'' as value12,'' as DeprePercent,'' as ChangeMethod,'' as addr,'' as RemainPercent,'' as spec,'' as UnitName,e.debittotalocc1,e.debittotalocc2,e.debittotalocc4,e.debittotalocc5,e.credittotalocc1,e.credittotalocc2,e.credittotalocc4,e.credittotalocc5  \n"
				+ " from( \n"
				+ " select subjectid,accname,if(level1>=2,replace(subjectfullname1,substr(subjectfullname1,1,if(locate('/',subjectfullname1) = 0,length(subjectfullname1),locate('/',subjectfullname1))),''),subjectfullname1) as accname1,direction2 * (debitremain+creditremain) as remain \n"
				+ " from c_account \n"
				+ " where ("+subjectName+") and submonth=1 and isleaf1 = 1 \n"
				+ " and accpackageid='${curPackageid}' "+itemType
				+ " group by subjectid \n"
				+ " ) a \n"
				+ " left join \n"
				+ " ( \n"
				+ " select subjectid,accname,sum(if(direction2=1,debitocc,creditocc)) as debitocc,sum(if(direction2=-1,debitocc,creditocc)) as creditocc \n"
				+ " from c_account \n"
				+ " where ("+subjectName+") \n"
				+ " and accpackageid='${curPackageid}' and isleaf1 = 1  "+itemType
				+ " group by subjectid \n"
				+ " ) b \n"
				+ " on a.subjectid=b.subjectid \n"
				+ " left join \n"
				+ " ( \n"
				+ " select subjectid,accname,direction2 * balance as balance,direction2 \n"
				+ " from c_account \n"
				+ " where ("+subjectName+") and submonth=12 and isleaf1 = 1 \n"
				+ " and accpackageid='${curPackageid}' "+itemType
				+ " group by subjectid \n"
				+ " ) d \n"
				+ " on a.subjectid=d.subjectid \n"
				+ " left join \n"
				+ " ( \n"
//				+ " select subjectid,subjectname,(debittotalocc1+debittotalocc2+debittotalocc4+debittotalocc5) as debitsov,(credittotalocc1+credittotalocc2+credittotalocc4+credittotalocc5) as creditsov \n"
//				+ " from z_accountrectify \n"
//				+ " where accpackageid='${curPackageid}' and projectid='${curProjectid}' \n"
				
				+ " select z.subjectid,ifnull(a.debittotalocc1,0) debittotalocc1,ifnull(b.debittotalocc2,0) debittotalocc2,ifnull(c.debittotalocc4,0) debittotalocc4,ifnull(d.debittotalocc5,0) debittotalocc5,ifnull(e.credittotalocc1,0) credittotalocc1,ifnull(f.credittotalocc2,0) credittotalocc2,ifnull(g.credittotalocc4,0) credittotalocc4,ifnull(h.credittotalocc5,0) credittotalocc5, \n"
				+ " (ifnull(a.debittotalocc1,0)+ifnull(b.debittotalocc2,0)+ifnull(c.debittotalocc4,0)+ifnull(d.debittotalocc5,0)) debitsov,(ifnull(e.credittotalocc1,0)+ifnull(f.credittotalocc2,0)+ifnull(g.credittotalocc4,0)+ifnull(h.credittotalocc5,0)) creditsov \n"
				+ "  from ( \n"
				+ "  select distinct subjectid from c_account  where ("+subjectName+") and accpackageid='${curPackageid}' \n"
				+ "  ) z left join " +
						"( \n"
				+ " select subjectid,ifnull(sum(occurvalue),0) debittotalocc1 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
				+ " and  dirction=1  and vchdate like '"+myYear+"%' and property like '3%' group by subjectid) a on z.subjectid=a.subjectid left join ( \n"//#借年末调整
				+ " select subjectid,ifnull(sum(occurvalue),0) debittotalocc2 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
				+ " and  dirction=1  and vchdate like '"+myYear+"%' and property like '4%' group by subjectid) b on z.subjectid=b.subjectid left join ( \n"//#借年末重分类
				+ " select subjectid,ifnull(sum(occurvalue),0) debittotalocc4 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
				+ " and  dirction=1  and vchdate < substring('${curPackageid}',7) and property like '3%' group by subjectid) c on z.subjectid=c.subjectid left join ( \n"//#借年初调整
				+ " select subjectid,ifnull(sum(occurvalue),0) debittotalocc5 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
				+ " and  dirction=1  and vchdate < substring('${curPackageid}',7) and property like '4%' group by subjectid) d on z.subjectid=d.subjectid left join ( \n"//#借年初重分类
				+ " select subjectid,ifnull(sum(occurvalue),0) credittotalocc1 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
				+ " and  dirction=-1  and vchdate like '"+myYear+"%' and property like '3%' group by subjectid) e on z.subjectid=e.subjectid left join ( \n"//#贷年末调整
				+ " select subjectid,ifnull(sum(occurvalue),0) credittotalocc2 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
				+ " and  dirction=-1  and vchdate like '"+myYear+"%' and property like '4%' group by subjectid) f on z.subjectid=f.subjectid left join ( \n"//#贷年末重分类
				+ " select subjectid,ifnull(sum(occurvalue),0) credittotalocc4 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
				+ " and  dirction=-1  and vchdate < substring('${curPackageid}',7) and property like '3%' group by subjectid) g on z.subjectid=g.subjectid left join ( \n"//#贷年初调整
				+ " select subjectid,ifnull(sum(occurvalue),0) credittotalocc5 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
				+ " and  dirction=-1  and vchdate < substring('${curPackageid}',7) and property like'4%' group by subjectid) h on z.subjectid=h.subjectid \n"//#贷年初重分类
				+ " ) e \n"
				+ " on a.subjectid=e.subjectid \n"
				+ " order by a.subjectid ";
		} else {
//		标准类别汇总
		resultSql = "select a.name as itemname,b.* from ( \n"
			+ " select name from k_dic where ctype='standtype' ) a left join ( "
			+ " select a.accname1 as itemname,f.standtype,sum(a.remain) as remain,sum(a.remain) as Depreremain,sum(a.remain) as initvalue,sum(b.debitocc) as debitocc,sum(b.debitocc) as DepreAdd,sum(b.debitocc) as ReservedAdd,sum(b.creditocc) as creditocc,sum(b.creditocc) as DepreMinus,sum(b.creditocc) as ReservedMinus,sum(d.balance) as balance,sum(d.balance) as DepreBalance,sum(d.balance) as ReservedBalance,sum(ifnull(e.debitsov,0)) as debitsov,sum(ifnull(e.creditsov,0)) as creditsov, \n"
			+ " sum((d.balance+ifnull(e.debitsov,0)-ifnull(e.creditsov,0))) as Validation,sum((d.balance-ifnull(e.debitsov,0)+ifnull(e.creditsov,0))) as DepreValidation,sum((d.balance-ifnull(e.debitsov,0)+ifnull(e.creditsov,0))) as ReservedValidation,'' as adddate,'' as period,'' as remainvalue,sum(a.remain) as reservedremain,'' as hasdepre,'' as itemno, \n"
			+ " '' as ItemClass,'' as Quantity,'' as status,'' as DelDate,'' as AddMethod,'' as DelMethod,'' as Depramount, \n"
			+ " '' as value1,'' as value2,'' as value3,'' as value4,'' as value5,'' as value6,'' as value7,'' as value8,'' as value9,'' as value10,'' as value11,'' as value12,'' as DeprePercent,'' as ChangeMethod,'' as addr,'' as RemainPercent,'' as spec,'' as UnitName,e.debittotalocc1,e.debittotalocc2,e.debittotalocc4,e.debittotalocc5,e.credittotalocc1,e.credittotalocc2,e.credittotalocc4,e.credittotalocc5  \n"
			+ " from( \n"
			+ " select subjectfullname1,subjectid,accname,if(level1>=2,replace(subjectfullname1,substr(subjectfullname1,1,if(locate('/',subjectfullname1) = 0,length(subjectfullname1),locate('/',subjectfullname1))),''),subjectfullname1) as accname1,direction2 * (debitremain+creditremain) as remain \n"
			+ " from c_account \n"
			+ " where ("+subjectName+") and submonth=1 \n"
			+ " and accpackageid='${curPackageid}' "+itemType
			+ " group by subjectid \n"
			+ " ) a \n"
			+ " left join \n"
			+ " ( \n"
			+ " select subjectid,accname,sum(if(direction2=1,debitocc,creditocc)) as debitocc,sum(if(direction2=-1,debitocc,creditocc)) as creditocc \n"
			+ " from c_account \n"
			+ " where ("+subjectName+") \n"
			+ " and accpackageid='${curPackageid}' "+itemType
			+ " group by subjectid \n"
			+ " ) b \n"
			+ " on a.subjectid=b.subjectid \n"
			+ " left join \n"
			+ " ( \n"
			+ " select subjectid,accname,direction2 * balance as balance \n"
			+ " from c_account \n"
			+ " where ("+subjectName+") and submonth=12 \n"
			+ " and accpackageid='${curPackageid}' "+itemType
			+ " group by subjectid \n"
			+ " ) d \n"
			+ " on a.subjectid=d.subjectid \n"
			+ " left join \n"
			+ " ( \n"
//			+ " select subjectid,subjectname,(debittotalocc1+debittotalocc2+debittotalocc4+debittotalocc5) as debitsov,(credittotalocc1+credittotalocc2+credittotalocc4+credittotalocc5) as creditsov \n"
//			+ " from z_accountrectify \n"
//			+ " where accpackageid='${curPackageid}' and projectid='${curProjectid}' \n"
			
			+ " select z.subjectid,ifnull(a.debittotalocc1,0) debittotalocc1,ifnull(b.debittotalocc2,0) debittotalocc2,ifnull(c.debittotalocc4,0) debittotalocc4,ifnull(d.debittotalocc5,0) debittotalocc5,ifnull(e.credittotalocc1,0) credittotalocc1,ifnull(f.credittotalocc2,0) credittotalocc2,ifnull(g.credittotalocc4,0) credittotalocc4,ifnull(h.credittotalocc5,0) credittotalocc5, \n"
			+ " (ifnull(a.debittotalocc1,0)+ifnull(b.debittotalocc2,0)+ifnull(c.debittotalocc4,0)+ifnull(d.debittotalocc5,0)) debitsov,(ifnull(e.credittotalocc1,0)+ifnull(f.credittotalocc2,0)+ifnull(g.credittotalocc4,0)+ifnull(h.credittotalocc5,0)) creditsov \n"
			+ "  from ( \n"
			+ "  select distinct subjectid from c_account  where ("+subjectName+") and accpackageid='1000022008' \n"
			+ "  ) z left join " +
					"( \n"
			+ " select subjectid,ifnull(sum(occurvalue),0) debittotalocc1 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
			+ " and  dirction=1  and vchdate like '"+myYear+"%' and property like '3%' group by subjectid) a on z.subjectid=a.subjectid left join ( \n"//#借年末调整
			+ " select subjectid,ifnull(sum(occurvalue),0) debittotalocc2 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
			+ " and  dirction=1  and vchdate like '"+myYear+"%' and property like '4%' group by subjectid) b on z.subjectid=b.subjectid left join ( \n"//#借年末重分类
			+ " select subjectid,ifnull(sum(occurvalue),0) debittotalocc4 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
			+ " and  dirction=1  and vchdate < substring('${curPackageid}',7) and property like '3%' group by subjectid) c on z.subjectid=c.subjectid left join ( \n"//#借年初调整
			+ " select subjectid,ifnull(sum(occurvalue),0) debittotalocc5 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
			+ " and  dirction=1  and vchdate < substring('${curPackageid}',7) and property like '4%' group by subjectid) d on z.subjectid=d.subjectid left join ( \n"//#借年初重分类
			+ " select subjectid,ifnull(sum(occurvalue),0) credittotalocc1 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
			+ " and  dirction=-1  and vchdate like '"+myYear+"%' and property like '3%' group by subjectid) e on z.subjectid=e.subjectid left join ( \n"//#贷年末调整
			+ " select subjectid,ifnull(sum(occurvalue),0) credittotalocc2 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
			+ " and  dirction=-1  and vchdate like '"+myYear+"%' and property like '4%' group by subjectid) f on z.subjectid=f.subjectid left join ( \n"//#贷年末重分类
			+ " select subjectid,ifnull(sum(occurvalue),0) credittotalocc4 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
			+ " and  dirction=-1  and vchdate < substring('${curPackageid}',7) and property like '3%' group by subjectid) g on z.subjectid=g.subjectid left join ( \n"//#贷年初调整
			+ " select subjectid,ifnull(sum(occurvalue),0) credittotalocc5 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
			+ " and  dirction=-1  and vchdate < substring('${curPackageid}',7) and property like'4%' group by subjectid) h on z.subjectid=h.subjectid \n"//#贷年初重分类
			+ " ) e \n"
			
			+ " on a.subjectid=e.subjectid \n"
			+ " left join fa_typecompare f \n"
			+ " on a.subjectfullname1=f.usertype \n"
			+ " group by f.standtype ) b \n"
			+ " on a.name=b.standtype "
			+ " order by a.name";
		}
		return resultSql;
	}

	public String getAssitemSql(String assitemName, String subjectid,String standType) {// 刷核算
		String resultSql = "";
		if(!"1".equals(standType)) {
		resultSql = " select a.assitemname as itemname,a.remain,a.remain as Depreremain,a.remain as initvalue,b.debitocc,b.debitocc as DepreAdd,b.debitocc as ReservedAdd,b.creditocc,b.creditocc as DepreMinus,b.creditocc as ReservedMinus,d.balance,d.balance as DepreBalancee,d.balance as ReservedBalance, "
				+ " ifnull(e.debitsov,0) as debitsov,ifnull(e.creditsov,0) as creditsov,(d.balance+ifnull(e.debitsov,0)-ifnull(e.creditsov,0)) as Validation, \n"
				+ " (d.balance-ifnull(e.debitsov,0)+ifnull(e.creditsov,0)) as DepreValidation,(d.balance-ifnull(e.debitsov,0)+ifnull(e.creditsov,0)) as ReservedValidation,'' as adddate,'' as period,'' as remainvalue,a.remain as reservedremain,'' as hasdepre,'' as itemno, \n"
				+ " '' as ItemClass,'' as Quantity,'' as status,'' as DelDate,'' as AddMethod,'' as DelMethod,'' as Depramount, \n"
				+ " '' as value1,'' as value2,'' as value3,'' as value4,'' as value5,'' as value6,'' as value7,'' as value8,'' as value9,'' as value10,'' as value11,'' as value12,'' as DeprePercent,'' as ChangeMethod,'' as addr,'' as RemainPercent,'' as spec,'' as UnitName,e.debittotalocc1,e.debittotalocc2,e.debittotalocc4,e.credittotalocc1,e.credittotalocc2,e.credittotalocc4  \n"
				+ " from( \n"
				+ " select accid,assitemid,asstotalname1 as assitemname,direction2 * (debitremain+creditremain) as remain \n"
				+ " from c_assitementryacc \n"
				+ " where asstotalname1 like '%"
				+ assitemName
				+ "%' and asstotalname1 not like '%部门%' and submonth=1 and isleaf1 = 1 \n"
				+ " and accpackageid='${curPackageid}' and accid in ( "
				+ subjectid
				+ " ) \n"
				+ " group by accid,assitemid \n"
				+ " ) a \n"
				+ " left join \n"
				+ " ( \n"
				+ " select accid,assitemid,assitemname,sum(if(direction2=1,debitocc,creditocc)) as debitocc,sum(if(direction2=-1,debitocc,creditocc)) as creditocc \n"
				+ " from c_assitementryacc \n"
				+ " where asstotalname1 like '%"
				+ assitemName
				+ "%' and asstotalname1 not like '%部门%' \n"
				+ " and accpackageid='${curPackageid}' and accid in ( "
				+ subjectid
				+ " ) \n"
				+ " group by accid,assitemid \n"
				+ " ) b \n"
				+ " on a.accid = b.accid and a.assitemid=b.assitemid \n"
				+ " left join \n"
				+ " ( \n"
				+ " select accid,assitemid,assitemname,direction2 * balance as balance \n"
				+ " from c_assitementryacc \n"
				+ " where asstotalname1 like '%"
				+ assitemName
				+ "%' and asstotalname1 not like '%部门%' and submonth=12 \n"
				+ " and accpackageid='${curPackageid}' and accid in ( "
				+ subjectid
				+ " ) \n"
				+ " group by accid,assitemid \n"
				+ " ) d \n"
				+ " on a.accid = d.accid and a.assitemid=d.assitemid \n"
				+ " left join  \n"
				+ " ( \n"
				+ " select SubjectID,assitemid,assitemname,(debittotalocc1+debittotalocc2+debittotalocc4+debittotalocc5) as debitsov,(credittotalocc1+credittotalocc2+credittotalocc4+credittotalocc5) as creditsov,debittotalocc1,debittotalocc2,debittotalocc4,debittotalocc5,credittotalocc1,credittotalocc2,credittotalocc4,credittotalocc5 \n"
				+ " from z_assitemaccrectify \n"
				+ " where accpackageid='${curPackageid}' and projectid='${curProjectid}' \n"
				+ " ) e	 \n"
				+ " on a.assitemid=e.assitemid and a.accid = e.SubjectID\n"
				+ " order by a.assitemname ";
		} else {
		//标准类别汇总
		resultSql = "select a.name as itemname,b.* from ( \n"
			+ " select name from k_dic where ctype='standtype' ) a left join ( "
			+ " select a.assitemname as itemname,f.standtype,sum(a.remain) as remain,sum(a.remain) as Depreremain,sum(a.remain) as initvalue,sum(b.debitocc) as debitocc,sum(b.debitocc) as DepreAdd,sum(b.debitocc) as ReservedAdd,sum(b.creditocc) as creditocc,sum(b.creditocc) as DepreMinus,sum(b.creditocc) as ReservedMinus,sum(d.balance) as balance,sum(d.balance) as DepreBalancee,sum(d.balance) as ReservedBalance, "
			+ " sum(ifnull(e.debitsov,0)) as debitsov,sum(ifnull(e.creditsov,0)) as creditsov,sum((d.balance+ifnull(e.debitsov,0)-ifnull(e.creditsov,0))) as Validation, \n"
			+ " sum((d.balance-ifnull(e.debitsov,0)+ifnull(e.creditsov,0))) as DepreValidation,sum((d.balance-ifnull(e.debitsov,0)+ifnull(e.creditsov,0))) as ReservedValidation,'' as adddate,'' as period,'' as remainvalue,sum(a.remain) as reservedremain,'' as hasdepre,'' as itemno, \n"
			+ " '' as ItemClass,'' as Quantity,'' as status,'' as DelDate,'' as AddMethod,'' as DelMethod,'' as Depramount, \n"
			+ " '' as value1,'' as value2,'' as value3,'' as value4,'' as value5,'' as value6,'' as value7,'' as value8,'' as value9,'' as value10,'' as value11,'' as value12,'' as DeprePercent,'' as ChangeMethod,'' as addr,'' as RemainPercent,'' as spec,'' as UnitName,e.debittotalocc1,e.debittotalocc2,e.debittotalocc4,e.credittotalocc1,e.credittotalocc2,e.credittotalocc4  \n"
			+ " from( \n"
			+ " select accid,assitemid,asstotalname1,asstotalname1 as assitemname,direction2 * (debitremain+creditremain) as remain \n"
			+ " from c_assitementryacc \n"
			+ " where asstotalname1 like '%"
			+ assitemName
			+ "%' and asstotalname1 not like '%部门%' and submonth=1 and isleaf1 = 1 \n"
			+ " and accpackageid='${curPackageid}' and accid in ( "
			+ subjectid
			+ " ) \n"
			+ " group by accid,assitemid \n"
			+ " ) a \n"
			+ " left join \n"
			+ " ( \n"
			+ " select accid,assitemid,assitemname,sum(if(direction2=1,debitocc,creditocc)) as debitocc,sum(if(direction2=-1,debitocc,creditocc)) as creditocc \n"
			+ " from c_assitementryacc \n"
			+ " where asstotalname1 like '%"
			+ assitemName
			+ "%' and asstotalname1 not like '%部门%' \n"
			+ " and accpackageid='${curPackageid}' and accid in ( "
			+ subjectid
			+ " ) \n"
			+ " group by accid,assitemid \n"
			+ " ) b \n"
			+ " on a.accid = b.accid and a.assitemid=b.assitemid \n"
			+ " left join \n"
			+ " ( \n"
			+ " select accid,assitemid,assitemname,direction2 * balance as balance \n"
			+ " from c_assitementryacc \n"
			+ " where asstotalname1 like '%"
			+ assitemName
			+ "%' and asstotalname1 not like '%部门%' and submonth=12 \n"
			+ " and accpackageid='${curPackageid}' and accid in ( "
			+ subjectid
			+ " ) \n"
			+ " group by accid,assitemid \n"
			+ " ) d \n"
			+ " on a.accid = d.accid and a.assitemid=d.assitemid \n"
			+ " left join  \n"
			+ " ( \n"
			+ " select SubjectID,assitemid,assitemname,(debittotalocc1+debittotalocc2+debittotalocc4+debittotalocc5) as debitsov,(credittotalocc1+credittotalocc2+credittotalocc4+credittotalocc5) as creditsov,debittotalocc1,debittotalocc2,debittotalocc4,debittotalocc5,credittotalocc1,credittotalocc2,credittotalocc4,credittotalocc5 \n"
			+ " from z_assitemaccrectify \n"
			+ " where accpackageid='${curPackageid}' and projectid='${curProjectid}' \n"
			+ " ) e	 \n"
			+ " on a.assitemid=e.assitemid and a.accid = e.SubjectID \n"
			+ " left join fa_typecompare f \n"
			+ " on a.acctotalname1 like concat('%/',f.usertype,'/%') \n"
			+ " group by f.standtype ) b \n"
			+ " on a.name=b.standtype "
			+ " order by a.name";
		}
		return resultSql;
	}

	public String getItemDetial(String str,String subjectName,String tablename,String direction,String itemType) {// 刷折旧检查表的明细
		String resultSql = "";
		String mySubjectId = " ( \n"
				 +" select distinct subjectid from c_account \n"
				 +" where subjectfullname2='"+subjectName+"' and accpackageid='${curPackageid}' \n"
				 +" ) ";//and itemtype != '' and itemtype is not null \n";
		
		String myYear = accpackageid.substring(6);
		
		resultSql = " select a.itemno,a.itemname,a.adddate,a.period,a.initvalue as initvalue,b.initremain as remain,a.remainvalue,a.RemainPercent, \n"
			 +" b.depreremain as Depreremain,b.reservedremain,b.hasdepre,c.initAdd as debitocc,c.initMinus as creditocc, \n" 
			 +" e.initbalance as balance,(e.initbalance+ifnull(debitsov,0)-ifnull(creditsov,0)) as Validation,c.DepreAdd as DepreAdd,c.DepreMinus as DepreMinus,c.ReservedAdd as ReservedAdd,c.ReservedMinus as ReservedMinus,e.DepreBalance as DepreBalance,e.ReservedBalance as ReservedBalance, \n" 
			 +" (e.DepreBalance-ifnull(debitsov,0)+ifnull(creditsov,0)) as DepreValidation,(e.ReservedBalance-ifnull(debitsov,0)+ifnull(creditsov,0)) as ReservedValidation,d.debitsov as debitsov,d.creditsov as creditsov,a.ItemClass,a.Quantity,a.status,a.DelDate as DelDate, \n"
			 +" a.AddMethod as AddMethod,a.DelMethod as DelMethod,a.Depramount as Depramount,f.value1 as value1,f.value2 as value2,f.value3 as value3,f.value4 as value4,f.value5 as value5,f.value6 as value6, \n"
			 +"  f.value7 as value7,f.value8 as value8,f.value9 as value9,f.value10 as value10,f.value11 as value11,f.value12 as value12,a.DeprePercent as DeprePercent,g.ChangeMethod as ChangeMethod,a.addr as addr,b.fullpathitemname as fullpathitemname,a.spec as spec,a.UnitName as UnitName,d.debittotalocc1,d.debittotalocc2,d.debittotalocc4,d.debittotalocc5,d.credittotalocc1,d.credittotalocc2,d.credittotalocc4,d.credittotalocc5  \n"
			 +" from (  \n"
			 +" select distinct accpackageid,itemno,ItemClass,Quantity,status,itemname,AddDate,DelDate,AddMethod,DelMethod,Depramount,Period/12 period,InitValue,RemainValue,DeprePercent,addr,RemainPercent,spec,UnitName \n" 
			 +" from FA_"+str+" \n"
			 +" where accpackageid='${curPackageid}' \n" 
			 +" )a   \n"
			 +" left join ( \n" 
			 +" select accpackageid,itemno,itemname,initremain,Depreremain,ReservedRemain,(depreadd-depreminus) as hasdepre,fullpathitemname \n" 
			 +" from fa_account  \n"
			 +" where accpackageid='${curPackageid}' and submonth=1) b \n" 
			 +" on a.itemno=b.itemno  \n"
			 +" left join  \n"
			 +" ( \n"
			 +" select accpackageid,itemno,itemname,sum(initAdd) as initAdd,sum(initMinus) as initMinus,sum(DepreAdd) as DepreAdd,sum(DepreMinus) as DepreMinus,sum(ReservedAdd) as ReservedAdd,sum(ReservedMinus) as ReservedMinus \n"
			 +" from fa_account  \n"
			 +" where accpackageid='${curPackageid}' \n"
			 +" group by itemno \n"
			 +" ) c \n"
			 +" on a.itemno = c.itemno \n"
			 +" left join  \n"
			 +" ( \n"
			 +" select accpackageid,itemno,itemname,initbalance,DepreBalance,ReservedBalance \n" 
			 +" from fa_account  \n"
			 +" where accpackageid='${curPackageid}' and submonth=12 \n"
			 +" ) e  \n"
			 +" on a.itemno=e.itemno \n"
			 +" left join  \n"
			 +" (  \n"
			
//			 +" select accpackageid,projectid,sum(if(dirction='1',occurvalue,'0')) as debitsov,sum(if(dirction='-1',occurvalue,'0')) as creditsov,itemtype \n" 
//			 +" from z_subjectentryrectify  \n"
//			 +" where itemtype != '' and itemtype is not null and accpackageid='${curPackageid}' and projectid='${curProjectid}' and (property like '3%' or property like '4%' or property like '5%') \n" 
//			 +" and subjectid in \n"
//			 +" ( \n"
//			 +" select distinct subjectid from c_account \n"
//			 +" where subjectfullname2='"+subjectName+"' and accpackageid='${curPackageid}' \n"
//			 +" ) \n"
//			 +" group by itemtype  \n"
			 
			    + " select z.subjectid,a.itemtype,ifnull(a.debittotalocc1,0) debittotalocc1,ifnull(b.debittotalocc2,0) debittotalocc2,ifnull(c.debittotalocc4,0) debittotalocc4,ifnull(d.debittotalocc5,0) debittotalocc5,ifnull(e.credittotalocc1,0) credittotalocc1,ifnull(f.credittotalocc2,0) credittotalocc2,ifnull(g.credittotalocc4,0) credittotalocc4,ifnull(h.credittotalocc5,0) credittotalocc5, \n"
				+ " (ifnull(a.debittotalocc1,0)+ifnull(b.debittotalocc2,0)+ifnull(c.debittotalocc4,0)+ifnull(d.debittotalocc5,0)) debitsov,(ifnull(e.credittotalocc1,0)+ifnull(f.credittotalocc2,0)+ifnull(g.credittotalocc4,0)+ifnull(h.credittotalocc5,0)) creditsov \n"
				+ "  from ( \n"
				+ "  select distinct subjectid from c_account  where subjectfullname2 like '"+subjectName+"/%' or subjectfullname2='"+subjectName+"' and accpackageid='1000022008' \n"
				+ "  ) z left join " +
						"( \n"
				+ " select subjectid,itemtype,ifnull(sum(occurvalue),0) debittotalocc1 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
				+ " and  dirction=1  and vchdate like '"+myYear+"%' and property like '3%' group by itemtype) a on z.subjectid=a.subjectid left join ( \n"//#借年末调整
				+ " select subjectid,ifnull(sum(occurvalue),0) debittotalocc2 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
				+ " and  dirction=1  and vchdate like '"+myYear+"%' and property like '4%' group by itemtype) b on z.subjectid=b.subjectid left join ( \n"//#借年末重分类
				+ " select subjectid,ifnull(sum(occurvalue),0) debittotalocc4 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
				+ " and  dirction=1  and vchdate < substring('${curPackageid}',7) and property like '3%' group by itemtype) c on z.subjectid=c.subjectid left join ( \n"//#借年初调整
				+ " select subjectid,ifnull(sum(occurvalue),0) debittotalocc5 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
				+ " and  dirction=1  and vchdate < substring('${curPackageid}',7) and property like '4%' group by itemtype) d on z.subjectid=d.subjectid left join ( \n"//#借年初重分类
				+ " select subjectid,ifnull(sum(occurvalue),0) credittotalocc1 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
				+ " and  dirction=-1  and vchdate like '"+myYear+"%' and property like '3%' group by itemtype) e on z.subjectid=e.subjectid left join ( \n"//#贷年末调整
				+ " select subjectid,ifnull(sum(occurvalue),0) credittotalocc2 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
				+ " and  dirction=-1  and vchdate like '"+myYear+"%' and property like '4%' group by itemtype) f on z.subjectid=f.subjectid left join ( \n"//#贷年末重分类
				+ " select subjectid,ifnull(sum(occurvalue),0) credittotalocc4 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
				+ " and  dirction=-1  and vchdate < substring('${curPackageid}',7) and property like '3%' group by itemtype) g on z.subjectid=g.subjectid left join ( \n"//#贷年初调整
				+ " select subjectid,ifnull(sum(occurvalue),0) credittotalocc5 from z_subjectentryrectify where projectid='${curProjectid}' and accpackageid='${curPackageid}'  and subjectid in "+mySubjectId+" \n"
				+ " and  dirction=-1  and vchdate < substring('${curPackageid}',7) and property like'4%' group by itemtype) h on z.subjectid=h.subjectid \n"//#贷年初重分类
			 
			 
			 
			 +" ) d  \n"
			 +" on a.itemno=d.itemtype \n" 
			 +" left join "+tablename+" f \n"
			 +" on a.itemno=f.itemno \n"
			 +" left join \n"
			 +" (select distinct itemno,itemname,group_concat(distinct ChangeMethod) as ChangeMethod from fa_deprate \n"
			 +" where accpackageid='${curPackageid}' and 1=2 \n"
			 +" group by itemno) g \n"
			 +" on a.itemno=g.itemno \n"
			 +" where 1=1 "+itemType+" "+direction
			 +" order by a.itemno ";
		return resultSql;
	}
	/**
	 * 用于判断余额表中是否有固定资产明细，如果有，则只刷固定资产明细，不用刷固定资产总汇总，如果没有，则把
	 * 汇总的值刷出来,如果有明细，返回true,否则返回false
	 * @param subjectfullname
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean checkSubjectname(String subjectfullname,String accpackageid,Connection conn) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		String flag = "";
		String sql = "select count(1) from c_account where accpackageid='"+accpackageid+"' and  subjectfullname2 like '"+subjectfullname+"/%' and submonth=1  ";
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()) {
				
				if(Integer.parseInt(rs.getString(1))>0) {
					flag = "1";
				} else {
					flag = "0";
				}
			} else {
				flag = "0";
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		} finally {
			DbUtil.close(rs);
			DbUtil.close(ps);
		}
		if("1".equals(flag)) {
			return true;
		} else {
			return false;
		}
		
	}

}