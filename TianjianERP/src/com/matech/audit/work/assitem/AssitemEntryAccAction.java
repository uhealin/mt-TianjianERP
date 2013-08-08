package com.matech.audit.work.assitem;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.del.JRockey2Opp;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.pub.func.ASTextKey;
import com.matech.framework.listener.UserSession;
import com.matech.framework.multidb.MultiDbIF;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.sys.UTILSysProperty;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.service.print.PrintSetup;

public class AssitemEntryAccAction  extends MultiActionController{
	private final String _strSuccess = "/assitementryacc/List.jsp";
	private final String _strLedger = "/assitementryacc/LedgerList.jsp";
		
	
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn=null;
		HashMap mapResult = new HashMap();
		try {
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			MultiDbIF db = (MultiDbIF) UTILSysProperty.context.getBean("MultiDbAction");
						
			ASFuntion CHF=new ASFuntion();
			String T1 = CHF.showNull(request.getParameter("T1"));
			String acc = CHF.showNull(request.getParameter("AccPackageID"));
			if("".equals(acc)){
				acc = CHF.showNull(userSession.getCurChoiceAccPackageId());
				if("".equals(acc)){
					acc = CHF.showNull(userSession.getCurAccPackageId());
				}
			}else{
				userSession.setCurChoiceAccPackageId(acc);				
			}
			if(!"".equals(acc)){
				if("".equals(T1)){
					T1=acc.substring(0,6);
				}
			}
			String moneysql = CHF.showNull(request.getParameter("moneysql"));
			
			moneysql = moneysql.replaceAll(" &gt; ",">").replaceAll(" &lt; ","<");
			
			String html = CHF.showNull(request.getParameter("html"));	//用于标志要不要用setTdtoValue　1为要
			
			if(!"".equals(acc)){
				/**
				 * 没有狗不能看2005年后的帐套
				 */
				int accAllowYear = Integer.parseInt(UTILSysProperty.SysProperty.getProperty("accAllowYear")); 
				if (Integer.parseInt(acc.substring(6)) > accAllowYear && JRockey2Opp.getUserLic() <= 0) {
				    final String TRY_URL = "/AuditSystem/AS_SYSTEM/error_page.jsp?tip=999";
				    response.sendRedirect(TRY_URL);
				    return null;
				}
				
				DataGridProperty pp = new DataGridProperty(){
					public void onSearch (
		    				javax.servlet.http.HttpSession session,
		    				javax.servlet.http.HttpServletRequest request,
		    				javax.servlet.http.HttpServletResponse response) throws Exception{
						
						UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
						String acc = this.getRequestValue("AccPackageID");
						String T1 = this.getRequestValue("T1");
						
						String AssItemID1 = this.getRequestValue("AssItemID1");
						String SubjectID1 = this.getRequestValue("SubjectID1");
						
						String SubjectID2 = this.getRequestValue("SubjectID2");
						String AssItemID2 = this.getRequestValue("AssItemID2");
						
						String txtType = this.getRequestValue("txtType");
						String AssPro1 = this.getRequestValue("AssPro1");
						String AssPro2 = this.getRequestValue("AssPro2");
						
						String assitem =  this.getRequestValue("assitem");			//按披露显示核算
						System.out.println("按披露显示核算:"+assitem);
						
						//监控用的
	        			String moneysql=this.getRequestValue("moneysql")+"";
	        			moneysql=moneysql.replaceAll("％", "%");
	        			
						String StartMonth = this.getRequestValue("StartMonth");
						String endMonth = this.getRequestValue("endMonth");
						
						String AssItemName = this.getRequestValue("AssItemName");
						
						String yearType = this.getRequestValue("yearType");
						
						String allOpt = this.getRequestValue("allOpt");
						
						if("".equals(StartMonth)) StartMonth="01";
						if("".equals(endMonth)) endMonth="12";
						
						if("".equals(acc)){
							acc = CHF.showNull(userSession.getCurChoiceAccPackageId());
							if("".equals(acc)){
								acc = CHF.showNull(userSession.getCurAccPackageId());
							}
						}else{
							userSession.setCurChoiceAccPackageId(acc);								
						}
						if(!"".equals(acc)){
							if("".equals(T1)){
								T1=acc.substring(0,6);
							}
						}
						if(!"".equals(StartMonth)) userSession.setCurChoiceBeginMonth(StartMonth);
						if(!"".equals(acc)) userSession.setCurChoiceBeginYear(acc.substring(6));
						if(!"".equals(endMonth)) userSession.setCurChoiceEndMonth(endMonth);
						if(!"".equals(acc)) userSession.setCurChoiceEndYear(acc.substring(6));
						
						String strA = "";
						String sLev = "";
						String strW = " and abs(qcremain)+abs(debit)+abs(credit)+abs(qmremain)>0 ";
						if("1".equals(yearType)){
							strW = "";
						}
						
						
						if("1".equals(txtType)){
							SubjectID1 = SubjectID2;
							AssItemID1 = AssItemID2;
						}
						
						if(!"".equals(AssItemID1) && !"1".equals(allOpt) && "".equals(AssItemName)){
							strA = " and a.AssItemID ='"+AssItemID1+"' ";
						}else{
							strA = " and a.AssItemID  like '"+AssItemID1+"%' ";
						}
						if(!"".equals(AssPro1) && !"".equals(AssPro2)){
							strA = " and a.assitemid BETWEEN '"+AssPro1+"' and '"+AssPro2+"' ";
						}else if(!"".equals(AssPro1) && "".equals(AssPro2) ){
							strA =" and a.assitemid >= '"+AssPro1+"' ";
						}else if("".equals(AssPro1) && !"".equals(AssPro2) ){
							strA =" and a.assitemid <= '"+AssPro2+"' ";
						}
						
						if(!"".equals(SubjectID1)){
							strA +="and accid like concat('"+SubjectID1+"','%') ";
						}

						if(!"".equals(AssItemName)){
							strA +=" and AssItemName like '%"+AssItemName+"%' ";
						}
						
						
						sLev = "  and a.level1=1 ";
						if(!"".equals(AssPro1) || !"".equals(AssPro2) || !"".equals(AssItemName)||!"".equals(moneysql)){
							sLev = "";
						}
						this.setCancelPage(true);
						if("1".equals(allOpt)){
							sLev = " and a.isleaf1 = 1 ";
							this.setCancelPage(false);
						}
						
						//this.setBgColor("#C5D8FC");
						
						String sql1 = "", sql2 = "";

						if("1".equals(assitem)){
							sql1 = " left join c_subjectassitem b on b.AccPackageID = " + acc + " and a.accid = b.SubjectID and (a.asstotalname = b.AssTotalName1 or a.asstotalname like concat(b.AssTotalName1,'/%')) ";
							sql2 = " and b.AccPackageID is not null ";
						}
						
						this.setTrActionProperty(true);
						this.setTrAction(" AccPackageID='"+acc+"' DataName='${dname}' SubjectID='${accid}' AssItemID='${AssItemID}' isleaf='${isleaf}'  style='cursor:hand;' onDBLclick='goSort();' subMonth='${yearmonth}' ");				
						
						this.cleanColumn();

//						this.setCountsql("select '' _1,'' _2,'<font color=\\'blue\\'>>>合计<font>' _3,'' _0," +
//								" concat('<td style=\\'TEXT-ALIGN: center\\'>',if(sum(remain)>0,'借',if(sum(remain)=0,'平','贷')),'</td>') _4,abs(sum(remain)) _5,sum(debit) _6,sum(credit) _7," +
//								" concat('<td style=\\'TEXT-ALIGN: center\\'>',if(sum(occ)>0,'借',if(sum(occ)=0,'平','贷')),'</td>') _8,abs(sum(occ)) _9 " +
//								" from(select accid,AssItemID,sum(IF(SubMonth='${StartMonth}',DebitRemain+creditremain,0)) remain," +
//								" sum(DebitOcc) debit,sum(CreditOcc) credit,sum(IF(SubMonth='${endMonth}',Balance,0)) occ,direction " +
//								" from c_assitementryacc  where accpackageid = '${AccPackageID}' ${strA} and submonth>='${StartMonth}' and submonth <='${endMonth}' " +
//								" GROUP by accid,AssItemID ) a,(select DISTINCT accid, AssItemID,asstotalname,isleaf " +
//								" from c_assitem where accpackageid =  '${AccPackageID}' ${sLev} ) b where a.AssItemID = b.AssItemID and a.accid=b.accid ${strW}"
//						);
						
						String DataName = this.getRequestValue("DataName");
						if("".equals(DataName)) DataName = "0"; 
						
						if("0".equals(yearType) && ("".equals(DataName) || "本位币".equals(DataName) || "0".equals(DataName)) && "".equals(moneysql)){
							DataName=" and a.DataName='0'";
							this.setCountsql("select count(*) as datagrid_count,'' _10, '' _1,'' _2,'<font color=\\'blue\\'>>>合计<font>' _3,'' _0," +
									"\n concat('<td style=\\'TEXT-ALIGN: center\\'>',if(sum(qcremain)>0,'借',if(sum(qcremain)=0,'平','贷')),'</td>') _4,abs(sum(qcremain)) _5,sum(debit) _6,sum(credit) _7," +
									"\n concat('<td style=\\'TEXT-ALIGN: center\\'>',if(sum(qmremain)>0,'借',if(sum(qmremain)=0,'平','贷')),'</td>') _8,abs(sum(qmremain)) _9 " +
									"\n from(" +
									
									"\n select a.accid,a.AssItemID,AssTotalName1 as AssTotalName,sum(case when SubMonth='${StartMonth}' then DebitRemain+creditremain else 0 end ) qcremain," +
									"\n sum(DebitOcc) debit,sum(CreditOcc) credit," +
									"\n sum(case when SubMonth='${endMonth}' then Balance else 0 end ) qmremain,direction" +
									"\n from c_assitementryacc a " +
									
									"\n where a.accpackageid = '${AccPackageID}' ${strA} ${sLev} and submonth>='${StartMonth}' and submonth <='${endMonth}' " +
									"\n GROUP by a.accid,a.AssItemID " +
									"\n ) a " +
									sql1 + 
									"\n where 1=1 "+sql2 + " " +moneysql+" ${strW} ");
							
						}else{							
							this.setCountsql("");
							if("外币".equals(DataName)){
								DataName = " and a.AccSign=1 ";
							}else if("数量".equals(DataName)){
								DataName = " and a.AccSign=2 ";
							}else if("所有币种".equals(DataName)){
								DataName = " and a.AccSign <>2 ";
							}else if("币种与数量".equals(DataName)){
								DataName = "";
							}else{
								if("".equals(DataName) || "本位币".equals(DataName) || "0".equals(DataName)){
									DataName=" and a.DataName='0'";
								}else{
									DataName = " and a.DataName='"+DataName+"'";
								}
							}
						}
						
						
						this.setCustomerId(T1);						
						
						String sql = "";
						
						
						String ss = this.getRequestValue("Currency");
						if("0".equals(yearType)){
							this.addColumn("科目编号", "accid");
							this.addColumn("科目名称", "subjectfullname");
							this.addColumn("核算项目编号", "AssItemID");
							this.addColumn("核算项目名称", "asstotalname");
							this.addColumn("币种", "dataname");
							this.addColumn("期初方向", "QcWay","showCenter");
							this.addColumn("期初余额", "qcremain","showMoney");
							this.addColumn("本期借方发生额", "debit","showMoney");
							this.addColumn("本期贷方发生额", "credit","showMoney");
							this.addColumn("期末方向", "QmWay","showCenter");
							this.addColumn("期末余额", "qmremain","showMoney");
							
							sql = "select a.*,0 yearmonth from (" +
							"\n 	select accid,AssItemID,a.asstotalname1 asstotalname,b.subjectName,b.subjectfullname,case dataname when '0' then '"+ss+"' else dataname end dataname," +
							"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when sum(case when SubMonth='${StartMonth}' then a.DebitRemain+a.creditremain else 0 end )>0 then '借' when sum(case when SubMonth='${StartMonth}' then a.DebitRemain+a.creditremain else 0 end )<0 then '贷' else '平' end end QcWay," +												
							"\n 	case a.direction when 0 then abs(sum(case when SubMonth='${StartMonth}' then a.DebitRemain+a.creditremain else 0 end )) else sum(case when SubMonth='${StartMonth}' then a.DebitRemain+a.creditremain else 0 end ) *a.direction end qcremain," +
							"\n 	sum(DebitOcc) debit,sum(CreditOcc) credit," +
							"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when sum(case when SubMonth='${endMonth}' then Balance else 0 end )>0 then '借' when sum(case when SubMonth='${endMonth}' then Balance else 0 end )<0 then '贷' else '平' end end QmWay," +
							"\n 	case a.direction when 0 then abs(sum(case when SubMonth='${endMonth}' then Balance else 0 end )) else sum(case when SubMonth='${endMonth}' then Balance else 0 end ) *a.direction end qmremain," +
							"\n 	a.direction,a.isleaf1 isleaf ,a.dataname dname,'0' a " +
							"\n 	from c_assitementryacc  a left join c_accpkgsubject b on a.accid = b.subjectid and b.accpackageid = '${AccPackageID}'" +
							"\n 	where a.accpackageid = '${AccPackageID}' ${strA} ${sLev} and submonth>='${StartMonth}' and submonth <='${endMonth}' ${DataName} " +
							"\n 	GROUP by accid,AssItemID" +
							
							"\n 	union " +
							
							"\n 	select accid,AssItemID,a.asstotalname1,b.subjectName,b.subjectfullname," +
							"\n 	case a.accsign when 1 then concat('外币：',a.DataName) else concat('数量：',a.DataName) end dataname," +
							"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when sum(case when SubMonth='${StartMonth}' then a.DebitRemain+a.creditremain else 0 end )>0 then '借' when sum(case when SubMonth='${StartMonth}' then a.DebitRemain+a.creditremain else 0 end )<0 then '贷' else '平' end end QcWay," +												
							"\n 	case a.direction when 0 then abs(sum(case when SubMonth='${StartMonth}' then a.DebitRemain+a.creditremain else 0 end )) else sum(case when SubMonth='${StartMonth}' then a.DebitRemain+a.creditremain else 0 end ) *a.direction end qcremain," +
							"\n 	sum(DebitOcc) debit,sum(CreditOcc) credit," +
							"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when sum(case when SubMonth='${endMonth}' then Balance else 0 end )>0 then '借' when sum(case when SubMonth='${endMonth}' then Balance else 0 end )<0 then '贷' else '平' end end QmWay," +
							"\n 	case a.direction when 0 then abs(sum(case when SubMonth='${endMonth}' then Balance else 0 end )) else sum(case when SubMonth='${endMonth}' then Balance else 0 end ) *a.direction end qmremain," +
							"\n 	a.direction,a.isleaf1 isleaf ,a.dataname dname,'1' a " +
							"\n 	from c_assitementryaccall  a left join c_accpkgsubject b on a.accid = b.subjectid and b.accpackageid = '${AccPackageID}' " +
							"\n 	where a.accpackageid = '${AccPackageID}' ${strA} ${sLev} and submonth>='${StartMonth}' and submonth <='${endMonth}' ${DataName} " +
							"\n 	GROUP by accid,AssItemID,DataName" +
							
							"\n ) a " +
							sql1 +
							"\n	where 1=1 " + sql2 + " " + moneysql+" ${strW} ";
					
							this.setOrderBy_CH("a.accid,a.AssItemID ,a.dname,a");
							this.setDirection("asc,asc,asc");
							
						}else{
							this.addColumn("科目编号", "accid");
							this.addColumn("科目名称", "subjectfullname");
							this.addColumn("核算项目编号", "AssItemID");
							this.addColumn("核算项目名称", "asstotalname");
							this.addColumn("币种", "dataname");
							this.addColumn("月份", "yearmonth");
							this.addColumn("期初方向", "QcWay","showCenter");
							this.addColumn("年初余额", "ycremain","showMoney");
							this.addColumn("期初余额", "qcremain","showMoney");
							this.addColumn("本期借方发生额", "debit","showMoney");
							this.addColumn("本期贷方发生额", "credit","showMoney");
							this.addColumn("本年累计借方发生额", "DebitTotalOcc","showMoney");
							this.addColumn("本年累计贷方发生额", "CreditTotalOcc","showMoney");
							this.addColumn("期末方向", "QmWay","showCenter");
							this.addColumn("期末余额", "qmremain","showMoney");
							
							this.setOrderBy_CH("a.accid,a.AssItemID ,a.yearmonth,a.dname,a");
							this.setDirection("asc,asc,asc,asc");
							
							sql = "select a.* from (" +
							"\n 	select accid,AssItemID,a.asstotalname1 asstotalname,b.subjectName,b.subjectfullname," +
							"\n		case dataname when '0' then '"+ss+"' else dataname end dataname," +
							
							"\n		concat(subyearmonth,LPAD(SubMonth,2,'0')) yearmonth," +
							"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when a.DebitRemain+a.creditremain >0 then '借' when a.DebitRemain+a.creditremain <0 then '贷' else '平' end end QcWay," +												
							"\n 	case a.direction when 0 then abs((Balance - DebitTotalOcc + CreditTotalOcc) ) else ((Balance - DebitTotalOcc + CreditTotalOcc) ) *a.direction end ycremain," +
							"\n 	case a.direction when 0 then abs(a.DebitRemain+a.creditremain) else (a.DebitRemain+a.creditremain) *a.direction end qcremain," +
							"\n 	DebitOcc debit, CreditOcc credit,DebitTotalOcc,CreditTotalOcc," +
							"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when Balance >0 then '借' when Balance <0 then '贷' else '平' end end QmWay," +
							"\n 	case a.direction when 0 then abs(Balance) else Balance*a.direction end qmremain," +
							
							"\n 	a.direction,a.isleaf1 isleaf ,a.dataname dname,'0' a " +
							"\n 	from c_assitementryacc  a left join c_accpkgsubject b on a.accid=b.subjectid and b.accpackageid = '${AccPackageID}' " +
							"\n 	where a.accpackageid = '${AccPackageID}' ${strA} ${sLev} and submonth>='${StartMonth}' and submonth <='${endMonth}' ${DataName} " +
							
							"\n 	union " +
							
							"\n 	select accid,AssItemID,a.asstotalname1,b.subjectName,b.subjectfullname," +
							"\n 	case a.accsign when 1 then concat('外币：',a.DataName) else concat('数量：',a.DataName) end dataname," +
							
							"\n		concat(subyearmonth,LPAD(SubMonth,2,'0')) yearmonth," +
							"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when a.DebitRemain+a.creditremain >0 then '借' when a.DebitRemain+a.creditremain <0 then '贷' else '平' end end QcWay," +												
							"\n 	case a.direction when 0 then abs((Balance - DebitTotalOcc + CreditTotalOcc) ) else ((Balance - DebitTotalOcc + CreditTotalOcc) ) *a.direction end ycremain," +
							"\n 	case a.direction when 0 then abs(a.DebitRemain+a.creditremain) else (a.DebitRemain+a.creditremain) *a.direction end qcremain," +
							"\n 	DebitOcc debit, CreditOcc credit,DebitTotalOcc,CreditTotalOcc," +
							"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when Balance >0 then '借' when Balance <0 then '贷' else '平' end end QmWay," +
							"\n 	case a.direction when 0 then abs(Balance) else Balance*a.direction end qmremain," +

							"\n 	a.direction,a.isleaf1 isleaf ,a.dataname dname,'1' a " +
							"\n 	from c_assitementryaccall  a left join c_accpkgsubject b on a.accid = b.subjectid and b.accpackageid = '${AccPackageID}'" +
							"\n 	where a.accpackageid = '${AccPackageID}' ${strA} ${sLev} and submonth>='${StartMonth}' and submonth <='${endMonth}' ${DataName} " +
							
							"\n ) a " +
							sql1 +
							"\n	where 1=1 "+ sql2 + " " + moneysql+" ${strW} ";
							
						}
						this.setSQL(sql);
						
						this.setOrAddRequestValue("AccPackageID",acc);
						
						this.setOrAddRequestValue("endMonth", endMonth);
						this.setOrAddRequestValue("StartMonth", StartMonth);						
						this.setOrAddRequestValue("strA", strA);
						this.setOrAddRequestValue("sLev", sLev);
						this.setOrAddRequestValue("strW", strW);
						this.setOrAddRequestValue("moneysql", moneysql);
						this.setOrAddRequestValue("DataName", DataName);
					}
				};
				
				if("1".equals(html)){
					pp.setTdtoValue("<a href='St://${value}'>${tdvalue}</a>");
				}
				
				pp.setTrBgColor("isleaf", "0", "#C5D8FC", "0", "goSubSort(this);");
				pp.setCancelAjaxSynchronization(false);  //设为同步
				
				conn = new DBConnect().getConnect(T1);
				
				ASTextKey tkey = new ASTextKey(conn);
				String ss = tkey.getACurrRate(acc);
				String TName = tkey.TextCustomerName(acc.substring(0, 6));
				
				String AccAll = CHF.showNull(request.getParameter("AccAll"));
				String txtType = CHF.showNull(request.getParameter("txtType"));
				
				String AssItemID1 = CHF.showNull(request.getParameter("AssItemID1"));
				String SubjectID1 = CHF.showNull(request.getParameter("SubjectID1"));
				String AssItemID2 = CHF.showNull(request.getParameter("AssItemID2"));
				String SubjectID2 = CHF.showNull(request.getParameter("SubjectID2"));
				String AssPro1 = CHF.showNull(request.getParameter("AssPro1"));
				String AssPro2 = CHF.showNull(request.getParameter("AssPro2"));
				
			
				String StartMonth = CHF.showNull(request.getParameter("StartMonth"));
				String endMonth = CHF.showNull(request.getParameter("endMonth"));
				if("".equals(StartMonth)) StartMonth="01";
				if("".equals(endMonth)) endMonth="12";
				
				
				pp.setTableID("assitementryacc");
				pp.setCustomerId(T1);
				pp.setPageSize_CH(50);
				pp.setWhichFieldIsValue(1);
//				pp.setCancelOrderby(true);
				pp.setCancelPage(true);
				
//				String sql="select a.accid,a.AssItemID,b.asstotalname," +
//						" if(a.direction=1,'借',if(a.direction=-1,'贷',if(remain>0,'借',if(remain=0,'平','贷')))) as QcWay," +
//						" if(a.direction=0,abs(remain),remain*a.direction) qcremain, debit,credit," +
//						" if(a.direction=1,'借',if(a.direction=-1,'贷',if(occ>0,'借',if(occ=0,'平','贷')))) as QmWay," +
//						" if(a.direction=0,abs(occ),occ*a.direction) qmremain,a.direction,b.isleaf " +
//						" from(select accid,AssItemID,sum(IF(SubMonth='${StartMonth}',DebitRemain+creditremain,0)) remain," +
//						" sum(DebitOcc) debit,sum(CreditOcc) credit,sum(IF(SubMonth='${endMonth}',Balance,0)) occ,direction " +
//						" from c_assitementryacc  where accpackageid = '${AccPackageID}' ${strA} and submonth>='${StartMonth}' and submonth <='${endMonth}' " +
//						" GROUP by accid,AssItemID ) a,(select DISTINCT accid, AssItemID,asstotalname,isleaf " +
//						" from c_assitem where accpackageid =  '${AccPackageID}' ${sLev} ) b where a.AssItemID = b.AssItemID and a.accid=b.accid ${strW}";
				
				String sql = "select *,0 yearmonth from (" +
						"\n 	select accid,AssItemID,a.asstotalname1 asstotalname,b.subjectName,b.subjectfullname,case dataname when '0' then '"+ss+"' else dataname end dataname," +
						"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when sum(case when SubMonth='${StartMonth}' then a.DebitRemain+a.creditremain else 0 end )>0 then '借' when sum(case when SubMonth='${StartMonth}' then a.DebitRemain+a.creditremain else 0 end )<0 then '贷' else '平' end end QcWay," +												
						"\n 	case a.direction when 0 then abs(sum(case when SubMonth='${StartMonth}' then a.DebitRemain+a.creditremain else 0 end )) else sum(case when SubMonth='${StartMonth}' then a.DebitRemain+a.creditremain else 0 end ) *a.direction end qcremain," +
						"\n 	sum(DebitOcc) debit,sum(CreditOcc) credit," +
						"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when sum(case when SubMonth='${endMonth}' then Balance else 0 end )>0 then '借' when sum(case when SubMonth='${endMonth}' then Balance else 0 end )<0 then '贷' else '平' end end QmWay," +
						"\n 	case a.direction when 0 then abs(sum(case when SubMonth='${endMonth}' then Balance else 0 end )) else sum(case when SubMonth='${endMonth}' then Balance else 0 end ) *a.direction end qmremain," +
						"\n 	a.direction,a.isleaf1 isleaf ,a.dataname dname,'0' a " +
						"\n 	from c_assitementryacc  a left join c_accpkgsubject b on a.accid = b.subjectid and b.accpackageid = '${AccPackageID}' " +
						"\n 	where a.accpackageid = '${AccPackageID}' ${strA} ${sLev} and submonth>='${StartMonth}' and submonth <='${endMonth}' ${DataName} " +
						"\n 	GROUP by accid,AssItemID" +
						
						"\n 	union " +
						
						"\n 	select accid,AssItemID,a.asstotalname1,b.subjectName,b.subjectfullname," +
						"\n 	case a.accsign when 1 then concat('外币：',a.DataName) else concat('数量：',a.DataName) end dataname," +
						"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when sum(case when SubMonth='${StartMonth}' then a.DebitRemain+a.creditremain else 0 end )>0 then '借' when sum(case when SubMonth='${StartMonth}' then a.DebitRemain+a.creditremain else 0 end )<0 then '贷' else '平' end end QcWay," +												
						"\n 	case a.direction when 0 then abs(sum(case when SubMonth='${StartMonth}' then a.DebitRemain+a.creditremain else 0 end )) else sum(case when SubMonth='${StartMonth}' then a.DebitRemain+a.creditremain else 0 end ) *a.direction end qcremain," +
						"\n 	sum(DebitOcc) debit,sum(CreditOcc) credit," +
						"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when sum(case when SubMonth='${endMonth}' then Balance else 0 end )>0 then '借' when sum(case when SubMonth='${endMonth}' then Balance else 0 end )<0 then '贷' else '平' end end QmWay," +
						"\n 	case a.direction when 0 then abs(sum(case when SubMonth='${endMonth}' then Balance else 0 end )) else sum(case when SubMonth='${endMonth}' then Balance else 0 end ) *a.direction end qmremain," +
						"\n 	a.direction,a.isleaf1 isleaf ,a.dataname dname,'1' a " +
						"\n 	from c_assitementryaccall  a left join c_accpkgsubject b on a.accid = b.subjectid and b.accpackageid = '${AccPackageID}' " +
						"\n 	where a.accpackageid = '${AccPackageID}' ${strA} ${sLev} and submonth>='${StartMonth}' and submonth <='${endMonth}' ${DataName} " +
						"\n 	GROUP by accid,AssItemID,DataName" +
						
						"\n ) a where 1=1 ${strW} ";
				
				pp.setOrderBy_CH("dname,accid,AssItemID ,a");
				pp.setDirection("asc,asc,asc");
				
				
				pp.addSqlWhere("endMonth", "${endMonth}");
				pp.addSqlWhere("StartMonth", "${StartMonth}");
				pp.addSqlWhere("AccPackageID", "${AccPackageID}");
				pp.addSqlWhere("strA", "${strA}");
				pp.addSqlWhere("sLev", "${sLev}");
				pp.addSqlWhere("strW", "${strW}");
				pp.addSqlWhere("moneysql", "${moneysql}");
				
				pp.addSqlWhere("DataName", "${DataName}"); //外币
				
				
				pp.addInputValue("T1");
				pp.addInputValue("AccPackageID");
				
				pp.addInputValue("AssItemID1");
				pp.addInputValue("SubjectID1");
				
				pp.addInputValue("SubjectID2");
				pp.addInputValue("AssItemID2");
				
				pp.addInputValue("txtType");
				pp.addInputValue("AssPro1");
				pp.addInputValue("AssPro2");
				
				pp.addInputValue("StartMonth"); 
				pp.addInputValue("endMonth");
				
				pp.addInputValue("AssItemName");
				
				pp.addInputValue("yearType");
				pp.addInputValue("Currency");
				
				pp.addInputValue("allOpt");
				pp.addInputValue("moneysql");
				
				pp.addInputValue("assitem");
				
				//pp.setFixedHeader(true) ;
				pp.setSQL(sql);				
				request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
				
//				mapResult.put("isNotAssitem", new AssitemEntryAcc(conn).isNotAssitem(acc));
				
				mapResult.put("T1", T1);
				mapResult.put("AccPackageID", acc);
				
				mapResult.put("TName",TName);
				
				mapResult.put("Currency",ss);
				
				
				mapResult.put("AssItemID1", AssItemID1);
				mapResult.put("SubjectID1", SubjectID1);
				mapResult.put("AssItemID2", AssItemID2);
				mapResult.put("SubjectID2", SubjectID2);
				mapResult.put("moneysql", moneysql);//监控用的条件
				mapResult.put("AssPro1", AssPro1);
				mapResult.put("AssPro2", AssPro2);
				
				mapResult.put("StartMonth", StartMonth);
				mapResult.put("endMonth", endMonth);
				
				mapResult.put("AccAll", AccAll);
				if("".equals(txtType)){
					txtType = "1";
				}
				mapResult.put("txtType", txtType);
				
				mapResult.put("DataName",CHF.showNull(request.getParameter("DataName")));	
				
				mapResult.put("DataGrid", pp.getTableID());
			}else{
				mapResult.put("T1", "");
				mapResult.put("AccPackageID", "");
				mapResult.put("moneysql", moneysql);//监控用的条件
			}
			
			mapResult.put("html", html);
			
		} catch (Exception e) {
			Debug.print(Debug.iError, "核算余额表查询失败！", e);
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		return new ModelAndView(_strSuccess,mapResult);
	}
	
	public ModelAndView getaccstr(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn=null;
		try {
			ASFuntion CHF=new ASFuntion();
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			String acc = CHF.showNull(request.getParameter("AccPackageID"));
			if("".equals(acc)){
				acc = CHF.showNull(userSession.getCurChoiceAccPackageId());
				if("".equals(acc)){
					acc = CHF.showNull(userSession.getCurAccPackageId());
				}
			}
			String sid = CHF.showNull(request.getParameter("SubjectID"));
			String aid = CHF.showNull(request.getParameter("AssitemID"));
			String bdate = CHF.showNull(request.getParameter("BDate"));
			bdate = bdate.equals("")?"01":bdate; 
			String edate = CHF.showNull(request.getParameter("EDate"));
			edate = edate.equals("")?"12":edate;
			
			String DataName = CHF.showNull(request.getParameter("DataName"));
			
			String subMonth = CHF.showNull(request.getParameter("subMonth"));
			System.out.println("py 下级核算：" + subMonth);
			
			conn= new DBConnect().getConnect(acc.substring(0,6));
			
			response.setContentType("text/html;charset=utf-8");  //设置编码
			PrintWriter out = response.getWriter();
			String result = "";
			if("".equals(subMonth) || "0".equals(subMonth)){
				result = new Assitem(conn).getAssStr(acc, sid, aid, bdate, edate,DataName).toString();
			} else{
				result = new Assitem(conn).getAssStr(acc, sid, aid, bdate, edate,DataName,subMonth).toString();
			}
			 
			System.out.println(result);
			out.print(result);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		
		
		return null;
	}
	
	public ModelAndView ledgerlist(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn=null;
		HashMap mapResult = new HashMap();
		try {
			ASFuntion CHF=new ASFuntion();
			String acc = CHF.showNull(request.getParameter("AccPackageID"));
			String SubjectID = CHF.showNull(request.getParameter("SubjectID"));
			
			 
			String AssItemID = CHF.showNull(request.getParameter("AssItemID"));
			conn= new DBConnect().getConnect(acc.substring(0, 6));
			
			String TName = new ASTextKey(conn).TextCustomerName(acc.substring(0, 6));
			
			if(!"".equals(acc) && !"".equals(SubjectID) && !"".equals(AssItemID)){
				DataGridProperty pp = new DataGridProperty();
				pp.setTableID("ledgerassitementryacc");
				pp.setCustomerId(acc.substring(0, 6));
				pp.setPageSize_CH(50);
				pp.setWhichFieldIsValue(1);
				pp.setCancelOrderby(true);
				pp.setCancelPage(true);
						
				String html = CHF.showNull(request.getParameter("html"));	//用于标志要不要用setTdtoValue　1为要　
				if("1".equals(html)){
					pp.setTdtoValue("<a href='St://${value}'>${tdvalue}</a>");
				}
				
				String sql = "";
				pp.setTrActionProperty(true);
				pp.setTrAction(" AccPackageID='"+acc+"' SubjectID='"+SubjectID+"' AssItemID='"+AssItemID+"' BeginDate='${SubMonth}' EndDate='${SubMonth}' style='cursor:hand;' onDBLclick='goSort();'");

				pp.setPrintEnable(true);
				pp.setPrintTitle(TName + "   分类帐查询");
				pp.setPrintCharColumn("1`2`3`4");
				pp.setPrintColumnWidth("15,15,30,8");
				
				Assitem assitem = new Assitem(conn);
				
				Map Smap = assitem.AssitemCurrency(acc, SubjectID, AssItemID);//外币
				Map Umap = assitem.AssitemUnitName(acc, SubjectID, AssItemID);//数量
				
				int result = assitem.AssitemProperty(acc, SubjectID, AssItemID);
				String currency = CHF.showNull(request.getParameter("Currency"));
				if("1".equals(currency)) {
					result = 0;
				}
				
				switch(result){
				case 0:
					sql = "select AccID,AssItemID,AssItemName,LPAD(SubMonth,2,0) SubMonth,DebitOcc,CreditOcc,IF(direction = -1 ,'贷' ,'借') AS QcWay,direction * Balance AS Balance,1 a " +
					"	from c_assitementryacc " +
					"	where AssItemID='"+AssItemID+"' and AccPackageID='"+acc+"' and accid='"+SubjectID+"' " +
					"	union " +
					"	select accid,AssItemID,'>合计','',DebitTotalOcc,CreditTotalOcc,IF(direction = -1 ,'贷' ,'借') AS QcWay,direction * Balance AS Balance,2 a " +
					"	from c_assitementryacc " +
					"	where AssItemID='"+AssItemID+"' and AccPackageID='"+acc+"' and accid='"+SubjectID+"' AND submonth = 12 " +
					"	union " +
					"	select '','','>期初','','','',IF(direction = -1 ,'贷' ,'借') AS QcWay,direction * (DebitRemain+CreditRemain) AS Balance,0 a " +
					"	from c_assitementryacc " +
					"	where AssItemID='"+AssItemID+"' and AccPackageID='"+acc+"' and accid='"+SubjectID+"' AND submonth = 1 " +
					"	order by a,abs(SubMonth)";
					
					pp.addColumn("科目编号", "AccID");
					pp.addColumn("核算编号", "AssItemID");
					pp.addColumn("核算名称", "AssItemName");
					pp.addColumn("会计月份", "SubMonth","showCenter");
					pp.addColumn("借方发生金额", "DebitOcc","showMoney");
					pp.addColumn("贷方发生金额", "CreditOcc","showMoney");
					pp.addColumn("科目方向", "QcWay","showCenter");
					pp.addColumn("期末金额", "Balance","showMoney");	
					
					break;
					
				case 1:
					pp.addColumn("科目编号", "AccID");
					pp.addColumn("核算编号", "AssItemID");
					pp.addColumn("核算名称", "AssItemName");
					pp.addColumn("会计月份", "SubMonth","showCenter");
					
					Set Scoll = Smap.keySet();
					int ii =1;
					sql = "";

					String sql1 = "",sql2 = "",sql3 = "",sql4 = "",sql5 = "" ;
					String sql6 = "",sql7 = "",sql8 = "",sql9 = "",sql0 = "";
					String tN1 = "",tName = "";
					for (Iterator iter = Scoll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) Smap.get(key);

						sql1 += " '' Debit"+ii+",";	
						sql2 += " '' Credit"+ii+",";
						sql3 += " a"+ii+".direction * (a"+ii+".DebitRemain+a"+ii+".CreditRemain) Balance"+ii+",";

						sql6 += " a"+ii+".DebitOcc Debit"+ii+",";
						sql7 += " a"+ii+".CreditOcc Credit"+ii+",";
						
						sql8 += " a"+ii+".DebitTotalOcc Debit"+ii+",";
						sql9 += " a"+ii+".CreditTotalOcc Credit"+ii+",";

						sql0 += " a"+ii+".direction * (a"+ii+".Balance) Balance"+ii+",";

						sql4 += " c_assitementryaccall a"+ii+",";
						
						sql5 += " and a"+ii+".accid = '" + SubjectID + "' and a"+ii+".AssItemID='"+AssItemID+"' and a"+ii+".DataName ='"+value+"' ";
						sql5 += " and a.SubYearMonth = a"+ii+".SubYearMonth and a.SubMonth = a"+ii+".SubMonth and a.AccPackageID= a"+ii+".AccPackageID ";

						tN1 += value+",";
						ii ++;
					}
					ii = 1;
					for (Iterator iter = Scoll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) Smap.get(key);
						pp.addColumn(value, "Debit"+ii,"showMoney");
						ii ++;
					}
					pp.addColumn("借方本位币", "DebitOcc","showMoney");

					ii = 1;
					for (Iterator iter = Scoll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) Smap.get(key);
						pp.addColumn(value, "Credit"+ii,"showMoney");
						ii ++;
					}
					pp.addColumn("贷方本位币", "CreditOcc","showMoney");
					pp.addColumn("科目方向", "QcWay","showCenter");

					ii = 1;
					for (Iterator iter = Scoll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) Smap.get(key);
						pp.addColumn(value, "Balance"+ii,"showMoney");
						ii ++;
					}
					pp.addColumn("本位币余额", "Balance","showMoney");
					
					tName = "科目编号,核算编号,核算名称,会计月份,借方{"+tN1+"借方本位币},贷方{"+tN1+"贷方本位币},科目方向,余额{"+tN1+"本位币余额}";
					pp.setTableHead(tName);

					sql = "SELECT a.AccID,a.AssItemID,a.AssItemName, a.SubMonth ," + sql6 + " a.DebitOcc," + sql7 + " a.CreditOcc,IF(a.direction = -1 ,'贷' ,'借') AS QcWay," + sql0 + " a.direction * a.Balance AS Balance,1 a " +
						"	from " + sql4 + " c_assitementryacc a " +
						"	where 1=1 " +
						"	and a.AccPackageID = '" + acc + "' and a.accid = '" + SubjectID + "' and a.AssItemID='"+AssItemID+"' " + sql5 + 
						"	union " +
						"	SELECT a.accid,a.AssItemID,'>合计','' ," + sql8 + " a.DebitTotalOcc," + sql9 + " a.CreditTotalOcc,IF(a.direction = -1 ,'贷' ,'借') AS QcWay," + sql0 + " a.direction * a.Balance AS Balance,2 a " +
						"	from " + sql4 + " c_assitementryacc a " +
						"	where 1=1 " +
						"	and a.AccPackageID = '" + acc + "' and a.accid = '" + SubjectID + "' and a.AssItemID='"+AssItemID+"'  AND a.submonth = 12 " + sql5 + 
						"	union " +
						"	SELECT '','','>期初','' ," + sql1 + " ''," + sql2 + " '',IF(a.direction = -1 ,'贷' ,'借') AS QcWay," + sql3 + " a.direction * (a.DebitRemain+a.CreditRemain) AS Balance,0 a " +
						"	from " + sql4 + " c_assitementryacc a " +
						"	where 1=1 " +
						"	and a.AccPackageID = '" + acc + "' and a.accid = '" + SubjectID + "' and a.AssItemID='"+AssItemID+"'  AND a.submonth = 1 " + sql5 +
						"	order by a,abs(SubMonth)";
					
					break;
					
				case 2:
					pp.addColumn("科目编号", "AccID");
					pp.addColumn("核算编号", "AssItemID");
					pp.addColumn("核算名称", "AssItemName");
					pp.addColumn("会计月份", "SubMonth","showCenter");

					Set Ucoll = Umap.keySet();
					ii =1;
					sql = "";

					sql1 = "";sql2 = "";sql3 = "";sql4 = "";sql5 = "" ;
					sql6 = "";sql7 = "";sql8 = "";sql9 = "";sql0 = "";
					tN1 = "";tName = "";
					for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
				    	String key = (String) iter.next();
						String value = (String) Umap.get(key);

						sql1 += " '' Debit"+ii+",";	
						sql2 += " '' Credit"+ii+",";
						sql3 += " a"+ii+".direction * (a"+ii+".DebitRemain+a"+ii+".CreditRemain) Balance"+ii+",";

						sql6 += " a"+ii+".DebitOcc Debit"+ii+",";
						sql7 += " a"+ii+".CreditOcc Credit"+ii+",";
						
						sql8 += " a"+ii+".DebitTotalOcc Debit"+ii+",";
						sql9 += " a"+ii+".CreditTotalOcc Credit"+ii+",";

						sql0 += " a"+ii+".direction * (a"+ii+".Balance) Balance"+ii+",";

						sql4 += " c_assitementryaccall a"+ii+",";
						
						sql5 += " and a"+ii+".accid = '" + SubjectID + "' and a"+ii+".AssItemID='"+AssItemID+"' and a"+ii+".DataName ='"+value+"' ";
						sql5 += " and a.SubYearMonth = a"+ii+".SubYearMonth and a.SubMonth = a"+ii+".SubMonth and a.AccPackageID= a"+ii+".AccPackageID ";

						tN1 += value+",";
						ii ++;
					}
					ii = 1;
					for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
				    	String key = (String) iter.next();
						String value = (String) Umap.get(key);
						pp.addColumn(value, "Debit"+ii,"showMoney");
						ii ++;
					}
					pp.addColumn("借方本位币", "DebitOcc","showMoney");

					ii = 1;
					for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
				    	String key = (String) iter.next();
						String value = (String) Umap.get(key);
						pp.addColumn(value, "Credit"+ii,"showMoney");
						ii ++;
					}
					pp.addColumn("贷方本位币", "CreditOcc","showMoney");
					pp.addColumn("科目方向", "QcWay","showCenter");

					ii = 1;
					for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
				    	String key = (String) iter.next();
						String value = (String) Umap.get(key);
						pp.addColumn(value, "Balance"+ii,"showMoney");
						ii ++;
					}
					pp.addColumn("本位币余额", "Balance","showMoney");
					
					tName = "科目编号,核算编号,核算名称,会计月份,借方{"+tN1+"借方本位币},贷方{"+tN1+"贷方本位币},科目方向,余额{"+tN1+"本位币余额}";
					pp.setTableHead(tName);

					sql = "SELECT a.AccID,a.AssItemID,a.AssItemName, a.SubMonth ," + sql6 + " a.DebitOcc," + sql7 + " a.CreditOcc,IF(a.direction = -1 ,'贷' ,'借') AS QcWay," + sql0 + " a.direction * a.Balance AS Balance,1 a " +
					"	from " + sql4 + " c_assitementryacc a " +
					"	where 1=1 " +
					"	and a.AccPackageID = '" + acc + "' and a.accid = '" + SubjectID + "' and a.AssItemID='"+AssItemID+"' " + sql5 + 
					"	union " +
					"	SELECT a.accid,a.AssItemID,'>合计','' ," + sql8 + " a.DebitTotalOcc," + sql9 + " a.CreditTotalOcc,IF(a.direction = -1 ,'贷' ,'借') AS QcWay," + sql0 + " a.direction * a.Balance AS Balance,2 a " +
					"	from " + sql4 + " c_assitementryacc a " +
					"	where 1=1 " +
					"	and a.AccPackageID = '" + acc + "' and a.accid = '" + SubjectID + "' and a.AssItemID='"+AssItemID+"'  AND a.submonth = 12 " + sql5 + 
					"	union " +
					"	SELECT '','','>期初','' ," + sql1 + " ''," + sql2 + " '',IF(a.direction = -1 ,'贷' ,'借') AS QcWay," + sql3 + " a.direction * (a.DebitRemain+a.CreditRemain) AS Balance,0 a " +
					"	from " + sql4 + " c_assitementryacc a " +
					"	where 1=1 " +
					"	and a.AccPackageID = '" + acc + "' and a.accid = '" + SubjectID + "' and a.AssItemID='"+AssItemID+"'  AND a.submonth = 1 " + sql5 +
					"	order by a,abs(SubMonth)";
					
					break;
					
				case 3:
					
					pp.addColumn("科目编号", "AccID");
					pp.addColumn("核算编号", "AssItemID");
					pp.addColumn("核算名称", "AssItemName");
					pp.addColumn("会计月份", "SubMonth","showCenter");
					
					Scoll = Smap.keySet(); //外币
					Ucoll = Umap.keySet(); //数量
					
					ii =1;
					sql = "";

					sql1 = "";sql2 = "";sql3 = "";sql4 = "";sql5 = "" ;
					sql6 = "";sql7 = "";sql8 = "";sql9 = "";sql0 = "";
					tN1 = "";tName = "";
					
					for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
				    	String key = (String) iter.next();
						String value = (String) Umap.get(key);

						sql1 += " '' UDebit"+ii+",";	
						sql2 += " '' UCredit"+ii+",";
						sql3 += " b"+ii+".direction * (b"+ii+".DebitRemain+b"+ii+".CreditRemain) as UBalance"+ii+",";

						sql6 += " b"+ii+".DebitOcc UDebit"+ii+",";
						sql7 += " b"+ii+".CreditOcc UCredit"+ii+",";
						
						sql8 += " b"+ii+".DebitTotalOcc UDebit"+ii+",";
						sql9 += " b"+ii+".CreditTotalOcc UCredit"+ii+",";

						sql0 += " b"+ii+".direction * (b"+ii+".Balance) as UBalance"+ii+",";

						sql4 += " c_assitementryaccall b"+ii+",";
						sql5 += " and b"+ii+".accid = '" + SubjectID + "' and b"+ii+".AssItemID='"+AssItemID+"' and b"+ii+".DataName ='"+value+"' ";
						sql5 += " and a.SubYearMonth = b"+ii+".SubYearMonth and a.SubMonth = b"+ii+".SubMonth and a.AccPackageID= b"+ii+".AccPackageID ";

						tN1 += value+",";
						ii ++;
					}
					ii = 1;
					for (Iterator iter = Scoll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) Smap.get(key);

						sql1 += " '' Debit"+ii+",";	
						sql2 += " '' Credit"+ii+",";
						sql3 += " a"+ii+".direction * (a"+ii+".DebitRemain+a"+ii+".CreditRemain) Balance"+ii+",";

						sql6 += " a"+ii+".DebitOcc Debit"+ii+",";
						sql7 += " a"+ii+".CreditOcc Credit"+ii+",";
						
						sql8 += " a"+ii+".DebitTotalOcc Debit"+ii+",";
						sql9 += " a"+ii+".CreditTotalOcc Credit"+ii+",";

						sql0 += " a"+ii+".direction * (a"+ii+".Balance) Balance"+ii+",";

						sql4 += " c_assitementryaccall a"+ii+",";
						sql5 += " and a"+ii+".accid = '" + SubjectID + "' and a"+ii+".AssItemID='"+AssItemID+"' and a"+ii+".DataName ='"+value+"' ";
						sql5 += " and a.SubYearMonth = a"+ii+".SubYearMonth and a.SubMonth = a"+ii+".SubMonth and a.AccPackageID= a"+ii+".AccPackageID ";

						tN1 += value+",";
						ii ++;
					}
					ii = 1;
					for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) Umap.get(key);
						pp.addColumn(value, "UDebit"+ii,"showMoney");
						ii ++;
					}
					ii = 1;
					for (Iterator iter = Scoll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) Smap.get(key);
						pp.addColumn(value, "Debit"+ii,"showMoney");
						ii ++;
					}
					pp.addColumn("借方本位币", "DebitOcc","showMoney");

					ii = 1;
					for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) Umap.get(key);
						pp.addColumn(value, "UCredit"+ii,"showMoney");
						ii ++;
					}
					ii = 1;
					for (Iterator iter = Scoll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) Smap.get(key);
						pp.addColumn(value, "Credit"+ii,"showMoney");
						ii ++;
					}
					pp.addColumn("贷方本位币", "CreditOcc","showMoney");
					pp.addColumn("科目方向", "QcWay","showCenter");
					
					ii = 1;
					for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) Umap.get(key);
						pp.addColumn(value, "UBalance"+ii,"showMoney");
						ii ++;
					}
					ii = 1;
					for (Iterator iter = Scoll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) Smap.get(key);
						pp.addColumn(value, "Balance"+ii,"showMoney");
						ii ++;
					}
					pp.addColumn("本位币余额", "Balance","showMoney");
					
					tName = "科目编号,核算编号,核算名称,会计月份,借方{"+tN1+"借方本位币},贷方{"+tN1+"贷方本位币},科目方向,余额{"+tN1+"本位币余额}";
					pp.setTableHead(tName);
					
					sql = "SELECT a.AccID,a.AssItemID,a.AssItemName, a.SubMonth ," + sql6 + " a.DebitOcc," + sql7 + " a.CreditOcc,IF(a.direction = -1 ,'贷' ,'借') AS QcWay," + sql0 + " a.direction * a.Balance AS Balance,1 a " +
					"	from " + sql4 + " c_assitementryacc a " +
					"	where 1=1 " +
					"	and a.AccPackageID = '" + acc + "' and a.accid = '" + SubjectID + "' and a.AssItemID='"+AssItemID+"' " + sql5 + 
					"	union " +
					"	SELECT a.accid,a.AssItemID,'>合计','' ," + sql8 + " a.DebitTotalOcc," + sql9 + " a.CreditTotalOcc,IF(a.direction = -1 ,'贷' ,'借') AS QcWay," + sql0 + " a.direction * a.Balance AS Balance,2 a " +
					"	from " + sql4 + " c_assitementryacc a " +
					"	where 1=1 " +
					"	and a.AccPackageID = '" + acc + "' and a.accid = '" + SubjectID + "' and a.AssItemID='"+AssItemID+"'  AND a.submonth = 12 " + sql5 + 
					"	union " +
					"	SELECT '','','>期初','' ," + sql1 + " ''," + sql2 + " '',IF(a.direction = -1 ,'贷' ,'借') AS QcWay," + sql3 + " a.direction * (a.DebitRemain+a.CreditRemain) AS Balance,0 a " +
					"	from " + sql4 + " c_assitementryacc a " +
					"	where 1=1 " +
					"	and a.AccPackageID = '" + acc + "' and a.accid = '" + SubjectID + "' and a.AssItemID='"+AssItemID+"'  AND a.submonth = 1 " + sql5 +
					"	order by a,abs(SubMonth)";
					
					break;
				}
				
				
				pp.setSQL(sql);
				
				request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
				
				mapResult.put("TName",TName);
				
				String AssItem = new Assitem(conn).getPAss(SubjectID,AssItemID,acc);
				mapResult.put("AssItem",AssItem);
				
				mapResult.put("html",html);
			}
			
		} catch (Exception e) {
			Debug.print(Debug.iError, "科目余额表分类帐查询失败！", e);
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		return new ModelAndView(_strLedger,mapResult);
	}
	
	
	public ModelAndView ledgerprint(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn=null;
		HashMap mapResult = new HashMap();
		try {
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			
			conn= new DBConnect().getConnect(userSession.getCurChoiceAccPackageId().substring(0, 6));
			String TName = new ASTextKey(conn).TextCustomerName(userSession.getCurChoiceAccPackageId().substring(0, 6));
			
			String tid = request.getParameter("tid");
			
			DataGridProperty pp = (DataGridProperty)request.getSession().getAttribute(DataGrid.sessionPre+tid); //科目余额表分类帐查询
						
			PrintSetup printSetup = new PrintSetup(conn);	
			printSetup.setStrTitles(new String[]{TName+" 分类帐查询"});
			printSetup.setStrChineseTitles(new String[]{"科目编号`核算编号`核算名称`会计月份`借方发生金额`贷方发生金额`科目方向`期末金额"});
			printSetup.setStrQuerySqls(new String[]{"select AccID,AssItemID,AssItemName,SubMonth,DebitOcc,CreditOcc,QcWay,Balance from ("+pp.getFinishSQL()+") a"});			
			printSetup.setCharColumn(new String[]{"1`2`3`4"});
			printSetup.setIColumnWidths(new int[]{15,15,25,8,20,20,8,30});
			
			String filename = printSetup.getExcelFile();
			
			
			//vpage strPrintTitleRows
			mapResult.put("refresh","");
			
			mapResult.put("saveasfilename","分类帐查询");			
			mapResult.put("bVpage","false");
			mapResult.put("strPrintTitleRows","$2:$4");
			mapResult.put("filename", filename);
			
		} catch (Exception e) {
			Debug.print(Debug.iError, "科目余额表分类帐查询打印失败！", e);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return new ModelAndView("/Excel/tempdata/PrintandSave.jsp",mapResult);
	}
	
	public ModelAndView printSetup(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn=null;
		HashMap mapResult = new HashMap();
		try {
			ASFuntion CHF=new ASFuntion();
			
			String T1 = CHF.showNull(request.getParameter("T1"));
			String sYear = CHF.showNull(request.getParameter("sYear"));
			String eYear = CHF.showNull(request.getParameter("eYear"));
			String sMonth = CHF.showNull(request.getParameter("sMonth"));
			String eMonth = CHF.showNull(request.getParameter("eMonth"));
			String str = CHF.showNull(request.getParameter("str"));
			
			String yearType =  CHF.showNull(request.getParameter("yearType"));
			System.out.println("py　打印:"+yearType);
			
			String sAcc = T1+sYear;
			String eAcc = T1+eYear;

			conn= new DBConnect().getConnect(T1);
			
			String [] strU = str.split("\\|");
			String strW = "";
			if("0".equals(yearType)){
				String [] s = strU[1].split("`");
				strW = "and ((accid='"+s[0]+"' and assitemid='"+s[1]+"' and dname='"+s[2]+"') ";
				for(int i=2;i<strU.length;i++){
					String [] ss = strU[i].split("`");
					strW +=" or (accid='"+ss[0]+"' and  assitemid='"+ss[1]+"' and dname='"+ss[2]+"') ";
				}
				strW +=") ";
			} else {
				String [] s = strU[1].split("`");
				strW = "and ((accid='"+s[0]+"' and assitemid='"+s[1]+"' and dname='"+s[2]+"' and yearmonth='"+s[3]+"') ";
				for(int i=2;i<strU.length;i++){
					String [] ss = strU[i].split("`");
					strW +=" or (accid='"+ss[0]+"' and  assitemid='"+ss[1]+"' and dname='"+ss[2]+"' and yearmonth='"+ss[3]+"') ";
				}
				strW +=") ";
			}
//			String sql = "select a.accid,a.AssItemID,b.asstotalname,if(a.direction=1,'借',if(a.direction=-1,'贷',if(remain>0,'借',if(remain=0,'平','贷')))) as QcWay,		if(a.direction=0,abs(remain),remain*a.direction) qcremain, debit,credit,if(a.direction=1,'借',if(a.direction=-1,'贷',if(occ>0,'借',if(occ=0,'平','贷')))) as QmWay,	if(a.direction=0,abs(occ),occ*a.direction) qmremain from(select accid,AssItemID,sum(IF(SubYearMonth='"+sYear+"' && SubMonth='"+sMonth+"',DebitRemain+creditremain,0)) remain,sum(DebitOcc) debit,sum(CreditOcc) credit,sum(IF(SubYearMonth='"+eYear+"' && SubMonth='"+eMonth+"',Balance,0)) occ,direction from c_assitementryacc  where concat(accpackageid,LPAD(submonth,2,'0'))>= concat('"+sAcc+"','"+sMonth+"') and concat(accpackageid,LPAD(submonth,2,'0'))<= concat('"+eAcc+"','"+eMonth+"') GROUP by accid,AssItemID ) a,(select DISTINCT accid, AssItemID,asstotalname,isleaf from c_assitem where accpackageid =  '"+eAcc+"' "+strW+") b where a.AssItemID = b.AssItemID and a.accid=b.accid  and abs(remain)+abs(debit)+abs(credit)+abs(occ)>0  order by substring(a.AssItemID,1,1),a.accid,a.AssItemID ";
			
			int sDay = Integer.parseInt(sYear) * 12 + Integer.parseInt(sMonth);
			int eDay = Integer.parseInt(eYear) * 12 + Integer.parseInt(eMonth);
			
			ASTextKey tkey = new ASTextKey(conn);
			String ss = tkey.getACurrRate(eAcc);
			
			String sql = "";
			String Title = "";
			if(!"".equals(T1)) Title = new ASTextKey(conn).TextCustomerName(T1) + "  核算科目余额表";
			
			PrintSetup printSetup = new PrintSetup(conn);	
			printSetup.setStrTitles(new String[]{Title});
			
			if("0".equals(yearType)){

			sql = "select accid,subjectfullname,AssItemID,asstotalname,dataname,QcWay,qcremain,debit,credit,QmWay,qmremain " +
				" from (" +
				"\n 	select accid,AssItemID,a.asstotalname1 asstotalname,b.subjectName,b.subjectfullname,case dataname when '0' then '"+ss+"' else dataname end dataname," +
				"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when sum(case when SubYearMonth*12+submonth="+sDay+" then a.DebitRemain+a.creditremain else 0 end )>0 then '借' when sum(case when SubYearMonth*12+submonth="+sDay+" then a.DebitRemain+a.creditremain else 0 end )<0 then '贷' else '平' end end QcWay," +												
				"\n 	case a.direction when 0 then abs(sum(case when SubYearMonth*12+submonth="+sDay+" then a.DebitRemain+a.creditremain else 0 end )) else sum(case when SubYearMonth*12+submonth="+sDay+" then a.DebitRemain+a.creditremain else 0 end ) *a.direction end qcremain," +
				"\n 	sum(DebitOcc) debit,sum(CreditOcc) credit," +
				"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when sum(case when SubYearMonth*12+submonth="+eDay+" then Balance else 0 end )>0 then '借' when sum(case when SubYearMonth*12+submonth="+eDay+" then Balance else 0 end )<0 then '贷' else '平' end end QmWay," +
				"\n 	case a.direction when 0 then abs(sum(case when SubYearMonth*12+submonth="+eDay+" then Balance else 0 end )) else sum(case when SubYearMonth*12+submonth="+eDay+" then Balance else 0 end ) *a.direction end qmremain," +
				"\n 	a.direction,a.isleaf1 isleaf ,a.dataname dname,'0' a " +
				"\n 	from c_assitementryacc  a left join c_accpkgsubject b on a.accid = b.subjectid and b.accpackageid='"+eAcc+"' " +
				"\n 	where substring(a.accpackageid,1,6) = '"+T1+"'  and SubYearMonth*12+submonth>="+sDay+" and SubYearMonth*12+submonth <="+eDay+"  " +
				"\n 	GROUP by accid,AssItemID" +
				
				"\n 	union " +
				
				"\n 	select accid,AssItemID,a.asstotalname1,b.subjectName,b.subjectfullname," +
				"\n 	case a.accsign when 1 then concat('外币：',a.DataName) else concat('数量：',a.DataName) end dataname," +
				"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when sum(case when SubYearMonth*12+submonth="+sDay+" then a.DebitRemain+a.creditremain else 0 end )>0 then '借' when sum(case when SubYearMonth*12+submonth="+sDay+" then a.DebitRemain+a.creditremain else 0 end )<0 then '贷' else '平' end end QcWay," +												
				"\n 	case a.direction when 0 then abs(sum(case when SubYearMonth*12+submonth="+sDay+" then a.DebitRemain+a.creditremain else 0 end )) else sum(case when SubYearMonth*12+submonth="+sDay+" then a.DebitRemain+a.creditremain else 0 end ) *a.direction end qcremain," +
				"\n 	sum(DebitOcc) debit,sum(CreditOcc) credit," +
				"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when sum(case when SubYearMonth*12+submonth="+eDay+" then Balance else 0 end )>0 then '借' when sum(case when SubYearMonth*12+submonth="+eDay+" then Balance else 0 end )<0 then '贷' else '平' end end QmWay," +
				"\n 	case a.direction when 0 then abs(sum(case when SubYearMonth*12+submonth="+eDay+" then Balance else 0 end )) else sum(case when SubYearMonth*12+submonth="+eDay+" then Balance else 0 end ) *a.direction end qmremain," +
				"\n 	a.direction,a.isleaf1 isleaf ,a.dataname dname,'1' a " +
				"\n 	from c_assitementryaccall  a left join c_accpkgsubject b on a.accid = b.subjectid  and b.accpackageid='"+eAcc+"' " +
				"\n 	where substring(a.accpackageid,1,6) = '"+T1+"'  and SubYearMonth*12+submonth>="+sDay+" and SubYearMonth*12+submonth <="+eDay+"  " +
				"\n 	GROUP by accid,AssItemID,dataname" +
				
				"\n ) a where 1=1  and abs(qcremain)+abs(debit)+abs(credit)+abs(qmremain)>0  " + strW +
				"\n order by accid,AssItemID,dname,a";
				
				printSetup.setStrChineseTitles(new String[]{"科目编号`科目名称`核算项目编号`核算项目名称`币种`期初方向`期初余额`本期借方发生额`本期贷方发生额`期末方向`期末余额"});
				printSetup.setStrQuerySqls(new String[]{sql});
				printSetup.setCharColumn(new String[]{"1`2`3`4"});
				printSetup.setIColumnWidths(new int[]{10,20,8,20,9,6,20,20,20,6,20});//13,11,20,9,6,20,20,20,6,20
			
			} else{
				
				sql = "select accid,subjectfullname,AssItemID,asstotalname,dataname,yearmonth,QcWay,ycremain,qcremain,debit,credit,DebitTotalOcc,CreditTotalOcc,QmWay,qmremain from (" + 
				"\n 	select accid,AssItemID,a.asstotalname1 asstotalname,b.subjectName,b.subjectfullname," +
				"\n		case dataname when '0' then '"+ss+"' else dataname end dataname," +
				
				"\n		concat(subyearmonth,LPAD(SubMonth,2,'0')) yearmonth," +
				"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when a.DebitRemain+a.creditremain >0 then '借' when a.DebitRemain+a.creditremain <0 then '贷' else '平' end end QcWay," +												
				"\n 	case a.direction when 0 then abs((Balance - DebitTotalOcc + CreditTotalOcc) ) else ((Balance - DebitTotalOcc + CreditTotalOcc) ) *a.direction end ycremain," +
				"\n 	case a.direction when 0 then abs(a.DebitRemain+a.creditremain) else (a.DebitRemain+a.creditremain) *a.direction end qcremain," +
				"\n 	DebitOcc debit, CreditOcc credit,DebitTotalOcc,CreditTotalOcc," +
				"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when Balance >0 then '借' when Balance <0 then '贷' else '平' end end QmWay," +
				"\n 	case a.direction when 0 then abs(Balance) else Balance*a.direction end qmremain," +
				
				"\n 	a.direction,a.isleaf1 isleaf ,a.dataname dname,'0' a " +
				"\n 	from c_assitementryacc  a left join c_accpkgsubject b on a.accid = b.subjectid  and b.accpackageid='"+eAcc+"' " +
				"\n 	where substring(a.accpackageid,1,6) = '"+T1+"' and SubYearMonth*12+submonth>="+sDay+" and SubYearMonth*12+submonth <="+eDay+"    " +
				
				"\n 	union " +
				
				"\n 	select accid,AssItemID,a.asstotalname1,b.subjectName,b.subjectfullname," +
				"\n 	case a.accsign when 1 then concat('外币：',a.DataName) else concat('数量：',a.DataName) end dataname," +
				
				"\n		concat(subyearmonth,LPAD(SubMonth,2,'0')) yearmonth," +
				"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when a.DebitRemain+a.creditremain >0 then '借' when a.DebitRemain+a.creditremain <0 then '贷' else '平' end end QcWay," +												
				"\n 	case a.direction when 0 then abs((Balance - DebitTotalOcc + CreditTotalOcc) ) else ((Balance - DebitTotalOcc + CreditTotalOcc) ) *a.direction end ycremain," +
				"\n 	case a.direction when 0 then abs(a.DebitRemain+a.creditremain) else (a.DebitRemain+a.creditremain) *a.direction end qcremain," +
				"\n 	DebitOcc debit, CreditOcc credit,DebitTotalOcc,CreditTotalOcc," +
				"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when Balance >0 then '借' when Balance <0 then '贷' else '平' end end QmWay," +
				"\n 	case a.direction when 0 then abs(Balance) else Balance*a.direction end qmremain," +

				"\n 	a.direction,a.isleaf1 isleaf ,a.dataname dname,'1' a " +
				"\n 	from c_assitementryaccall  a left join c_accpkgsubject b on a.accid = b.subjectid and b.accpackageid='"+eAcc+"' " +
				"\n 	where substring(a.accpackageid,1,6) = '"+T1+"' and SubYearMonth*12+submonth>="+sDay+" and SubYearMonth*12+submonth <="+eDay+"  " +

				"\n ) a where 1=1 " + strW + " order by accid,yearmonth,AssItemID ,dname,a ";
				
				printSetup.setStrChineseTitles(new String[]{"科目编号`科目名称`核算项目编号`核算项目名称`币种`月份`期初方向`年初余额`期初余额`本期借方发生额`本期贷方发生额`本年累计借方发生额`本年累计贷方发生额`期末方向`期末余额"});
				printSetup.setStrQuerySqls(new String[]{sql});
				printSetup.setCharColumn(new String[]{"1`2`3`4`5"});
				printSetup.setIColumnWidths(new int[]{10,20,8,20,9,10,6,20,20,20,20,20,20,6,20});
			}
			
			String filename = printSetup.getExcelFile();
			
			PrintWriter out = response.getWriter();
			out.print(filename);
			out.close();
			
		} catch (Exception e) {
			Debug.print(Debug.iError, "核算科目余额表查询打印失败！", e);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}
	
	public ModelAndView printSetup1(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn=null;
		HashMap mapResult = new HashMap();
		try {
			ASFuntion CHF=new ASFuntion();
			
			String T1 = CHF.showNull(request.getParameter("T1"));
			String sYear = CHF.showNull(request.getParameter("sYear"));
			String eYear = CHF.showNull(request.getParameter("eYear"));
			String sMonth = CHF.showNull(request.getParameter("sMonth"));
			String eMonth = CHF.showNull(request.getParameter("eMonth"));
			String str = CHF.showNull(request.getParameter("str"));
			
			String yearType =  CHF.showNull(request.getParameter("yearType"));
			String allOpt =  CHF.showNull(request.getParameter("allOpt"));
			String DataName =  CHF.showNull(request.getParameter("DataName"));
			
			String AssItemID1 = CHF.showNull(request.getParameter("AssItemID1"));
			String SubjectID1 = CHF.showNull(request.getParameter("SubjectID1"));
			
			String SubjectID2 = CHF.showNull(request.getParameter("SubjectID2"));
			String AssItemID2 = CHF.showNull(request.getParameter("AssItemID2"));
			
			String AssPro1 = CHF.showNull(request.getParameter("AssPro1"));
			String AssPro2 = CHF.showNull(request.getParameter("AssPro2"));
			
			String AssItemName = CHF.showNull(request.getParameter("AssItemName"));
			
			String txtType =  CHF.showNull(request.getParameter("txtType"));
			
			String sAcc = T1+sYear;
			String eAcc = T1+eYear;

			conn= new DBConnect().getConnect(T1);
			
			if("1".equals(txtType)){
				SubjectID1 = SubjectID2;
				AssItemID1 = AssItemID2;
			}
			String strW = " and isleaf= 1 ";
			
			if(!"".equals(AssItemID1) && !"1".equals(allOpt)){
				strW += " and AssItemID ='"+AssItemID1+"' ";
			}else{
				strW += " and AssItemID  like '"+AssItemID1+"%' ";
			}
			
			if(!"".equals(AssPro1) && !"".equals(AssPro2)){
				strW += " and assitemid BETWEEN '"+AssPro1+"' and '"+AssPro2+"' ";
			}else if(!"".equals(AssPro1) && "".equals(AssPro2) ){
				strW +=" and assitemid >= '"+AssPro1+"' ";
			}else if("".equals(AssPro1) && !"".equals(AssPro2) ){
				strW +=" and assitemid <= '"+AssPro2+"' ";
			}
			
			if(!"".equals(SubjectID1)){
				strW +="and accid like concat('"+SubjectID1+"','%') ";
			}

			if(!"".equals(AssItemName)){
				strW +=" and AssItemName like '%"+AssItemName+"%' ";
			}
			
			if("".equals(DataName)) DataName = "0";
			if("0".equals(yearType) && ("".equals(DataName) || "本位币".equals(DataName) || "0".equals(DataName))){
				strW +=" and a.dname='0'";
				
			}else{							
				if("外币".equals(DataName)){
					strW += " and a.AccSign=1 ";
				}else if("数量".equals(DataName)){
					strW += " and a.AccSign=2 ";
				}else if("所有币种".equals(DataName)){
					strW += " and a.AccSign <>2 ";
				}else if("币种与数量".equals(DataName)){
					strW += "";
				}else{
					strW += " and a.dname='"+DataName+"'";
				}
			}
			
//			String sql = "select a.accid,a.AssItemID,b.asstotalname,if(a.direction=1,'借',if(a.direction=-1,'贷',if(remain>0,'借',if(remain=0,'平','贷')))) as QcWay,		if(a.direction=0,abs(remain),remain*a.direction) qcremain, debit,credit,if(a.direction=1,'借',if(a.direction=-1,'贷',if(occ>0,'借',if(occ=0,'平','贷')))) as QmWay,	if(a.direction=0,abs(occ),occ*a.direction) qmremain from(select accid,AssItemID,sum(IF(SubYearMonth='"+sYear+"' && SubMonth='"+sMonth+"',DebitRemain+creditremain,0)) remain,sum(DebitOcc) debit,sum(CreditOcc) credit,sum(IF(SubYearMonth='"+eYear+"' && SubMonth='"+eMonth+"',Balance,0)) occ,direction from c_assitementryacc  where concat(accpackageid,LPAD(submonth,2,'0'))>= concat('"+sAcc+"','"+sMonth+"') and concat(accpackageid,LPAD(submonth,2,'0'))<= concat('"+eAcc+"','"+eMonth+"') GROUP by accid,AssItemID ) a,(select DISTINCT accid, AssItemID,asstotalname,isleaf from c_assitem where accpackageid =  '"+eAcc+"' "+strW+") b where a.AssItemID = b.AssItemID and a.accid=b.accid  and abs(remain)+abs(debit)+abs(credit)+abs(occ)>0  order by substring(a.AssItemID,1,1),a.accid,a.AssItemID ";
			
			int sDay = Integer.parseInt(sYear) * 12 + Integer.parseInt(sMonth);
			int eDay = Integer.parseInt(eYear) * 12 + Integer.parseInt(eMonth);
			
			ASTextKey tkey = new ASTextKey(conn);
			String ss = tkey.getACurrRate(eAcc);
			
			String sql = "";
			String Title = "";
			if(!"".equals(T1)) Title = new ASTextKey(conn).TextCustomerName(T1) + "  核算科目余额表";
			
			PrintSetup printSetup = new PrintSetup(conn);	
			printSetup.setStrTitles(new String[]{Title});
			
			if("0".equals(yearType)){

				sql = "select accid,subjectfullname,AssItemID,asstotalname,dataname,QcWay,qcremain,debit,credit,QmWay,qmremain " +
				" from (" +
				"\n 	select accid,AssItemID,a.asstotalname1 asstotalname,b.subjectName,b.subjectfullname,case dataname when '0' then '"+ss+"' else dataname end dataname," +
				"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when sum(case when SubYearMonth*12+submonth="+sDay+" then a.DebitRemain+a.creditremain else 0 end )>0 then '借' when sum(case when SubYearMonth*12+submonth="+sDay+" then a.DebitRemain+a.creditremain else 0 end )<0 then '贷' else '平' end end QcWay," +												
				"\n 	case a.direction when 0 then abs(sum(case when SubYearMonth*12+submonth="+sDay+" then a.DebitRemain+a.creditremain else 0 end )) else sum(case when SubYearMonth*12+submonth="+sDay+" then a.DebitRemain+a.creditremain else 0 end ) *a.direction end qcremain," +
				"\n 	sum(DebitOcc) debit,sum(CreditOcc) credit," +
				"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when sum(case when SubYearMonth*12+submonth="+eDay+" then Balance else 0 end )>0 then '借' when sum(case when SubYearMonth*12+submonth="+eDay+" then Balance else 0 end )<0 then '贷' else '平' end end QmWay," +
				"\n 	case a.direction when 0 then abs(sum(case when SubYearMonth*12+submonth="+eDay+" then Balance else 0 end )) else sum(case when SubYearMonth*12+submonth="+eDay+" then Balance else 0 end ) *a.direction end qmremain," +
				"\n 	a.direction,a.isleaf1 isleaf ,a.dataname dname,AccSign,'0' a " +
				"\n 	from c_assitementryacc  a left join c_accpkgsubject b on a.accid=b.subjectid and b.accpackageid='"+eAcc+"' " +
				"\n 	where substring(a.accpackageid,1,6) = '"+T1+"'  and SubYearMonth*12+submonth>="+sDay+" and SubYearMonth*12+submonth <="+eDay+"  " +
				"\n 	GROUP by accid,AssItemID" +
				
				"\n 	union " +
				
				"\n 	select accid,AssItemID,a.asstotalname1,b.subjectName,b.subjectfullname," +
				"\n 	case a.accsign when 1 then concat('外币：',a.DataName) else concat('数量：',a.DataName) end dataname," +
				"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when sum(case when SubYearMonth*12+submonth="+sDay+" then a.DebitRemain+a.creditremain else 0 end )>0 then '借' when sum(case when SubYearMonth*12+submonth="+sDay+" then a.DebitRemain+a.creditremain else 0 end )<0 then '贷' else '平' end end QcWay," +												
				"\n 	case a.direction when 0 then abs(sum(case when SubYearMonth*12+submonth="+sDay+" then a.DebitRemain+a.creditremain else 0 end )) else sum(case when SubYearMonth*12+submonth="+sDay+" then a.DebitRemain+a.creditremain else 0 end ) *a.direction end qcremain," +
				"\n 	sum(DebitOcc) debit,sum(CreditOcc) credit," +
				"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when sum(case when SubYearMonth*12+submonth="+eDay+" then Balance else 0 end )>0 then '借' when sum(case when SubYearMonth*12+submonth="+eDay+" then Balance else 0 end )<0 then '贷' else '平' end end QmWay," +
				"\n 	case a.direction when 0 then abs(sum(case when SubYearMonth*12+submonth="+eDay+" then Balance else 0 end )) else sum(case when SubYearMonth*12+submonth="+eDay+" then Balance else 0 end ) *a.direction end qmremain," +
				"\n 	a.direction,a.isleaf1 isleaf ,a.dataname dname,AccSign,'1' a " +
				"\n 	from c_assitementryaccall  a left join c_accpkgsubject b on a.accid=b.subjectid and b.accpackageid='"+eAcc+"' " +
				"\n 	where substring(a.accpackageid,1,6) = '"+T1+"'  and SubYearMonth*12+submonth>="+sDay+" and SubYearMonth*12+submonth <="+eDay+"  " +
				"\n 	GROUP by accid,AssItemID" +
				
				"\n ) a where 1=1  and abs(qcremain)+abs(debit)+abs(credit)+abs(qmremain)>0  " + strW +
				"\n order by accid,AssItemID ,dname,a";
				
				printSetup.setStrChineseTitles(new String[]{"科目编号`科目名称`核算项目编号`核算项目名称`币种`期初方向`期初余额`本期借方发生额`本期贷方发生额`期末方向`期末余额"});
				printSetup.setStrQuerySqls(new String[]{sql});
				printSetup.setCharColumn(new String[]{"1`2`3`4"});
				printSetup.setIColumnWidths(new int[]{10,8,20,9,6,20,20,20,6,20});//13,11,20,9,6,20,20,20,6,20
			
			} else{
				
				sql = "select accid,subjectfullname,AssItemID,asstotalname,dataname,yearmonth,QcWay,ycremain,qcremain,debit,credit,DebitTotalOcc,CreditTotalOcc,QmWay,qmremain from (" + 
				"\n 	select accid,AssItemID,a.asstotalname1 asstotalname,b.subjectName,b.subjectfullname," +
				"\n		case dataname when '0' then '"+ss+"' else dataname end dataname," +
				
				"\n		concat(subyearmonth,LPAD(SubMonth,2,'0')) yearmonth," +
				"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when a.DebitRemain+a.creditremain >0 then '借' when a.DebitRemain+a.creditremain <0 then '贷' else '平' end end QcWay," +												
				"\n 	case a.direction when 0 then abs((Balance - DebitTotalOcc + CreditTotalOcc) ) else ((Balance - DebitTotalOcc + CreditTotalOcc) ) *a.direction end ycremain," +
				"\n 	case a.direction when 0 then abs(a.DebitRemain+a.creditremain) else (a.DebitRemain+a.creditremain) *a.direction end qcremain," +
				"\n 	DebitOcc debit, CreditOcc credit,DebitTotalOcc,CreditTotalOcc," +
				"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when Balance >0 then '借' when Balance <0 then '贷' else '平' end end QmWay," +
				"\n 	case a.direction when 0 then abs(Balance) else Balance*a.direction end qmremain," +
				
				"\n 	a.direction,a.isleaf1 isleaf ,a.dataname dname,AccSign,'0' a " +
				"\n 	from c_assitementryacc  a left join c_accpkgsubject b on a.accid=b.subjectid and b.accpackageid='"+eAcc+"' " +
				"\n 	where substring(a.accpackageid,1,6) = '"+T1+"' and SubYearMonth*12+submonth>="+sDay+" and SubYearMonth*12+submonth <="+eDay+"    " +
				
				"\n 	union " +
				
				"\n 	select accid,AssItemID,a.asstotalname1,b.subjectName,b.subjectfullname," +
				"\n 	case a.accsign when 1 then concat('外币：',a.DataName) else concat('数量：',a.DataName) end dataname," +
				
				"\n		concat(subyearmonth,LPAD(SubMonth,2,'0')) yearmonth," +
				"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when a.DebitRemain+a.creditremain >0 then '借' when a.DebitRemain+a.creditremain <0 then '贷' else '平' end end QcWay," +												
				"\n 	case a.direction when 0 then abs((Balance - DebitTotalOcc + CreditTotalOcc) ) else ((Balance - DebitTotalOcc + CreditTotalOcc) ) *a.direction end ycremain," +
				"\n 	case a.direction when 0 then abs(a.DebitRemain+a.creditremain) else (a.DebitRemain+a.creditremain) *a.direction end qcremain," +
				"\n 	DebitOcc debit, CreditOcc credit,DebitTotalOcc,CreditTotalOcc," +
				"\n 	case a.direction when 1 then '借' when -1 then '贷' else case when Balance >0 then '借' when Balance <0 then '贷' else '平' end end QmWay," +
				"\n 	case a.direction when 0 then abs(Balance) else Balance*a.direction end qmremain," +

				"\n 	a.direction,a.isleaf1 isleaf ,a.dataname dname,AccSign,'1' a " +
				"\n 	from c_assitementryaccall  a left join c_accpkgsubject b on a.accid=b.subjectid and b.accpackageid='"+eAcc+"' " +
				"\n 	where substring(a.accpackageid,1,6) = '"+T1+"' and SubYearMonth*12+submonth>="+sDay+" and SubYearMonth*12+submonth <="+eDay+"  " +

				"\n ) a where 1=1 " + strW + " order by accid,AssItemID,yearmonth ,dname,a ";
				
				printSetup.setStrChineseTitles(new String[]{"科目编号`科目名称`核算项目编号`核算项目名称`币种`月份`期初方向`年初余额`期初余额`本期借方发生额`本期贷方发生额`本年累计借方发生额`本年累计贷方发生额`期末方向`期末余额"});
				printSetup.setStrQuerySqls(new String[]{sql});
				printSetup.setCharColumn(new String[]{"1`2`3`4`5"});
				printSetup.setIColumnWidths(new int[]{10,8,20,9,10,6,20,20,20,20,20,20,6,20});
			}
			
			String filename = printSetup.getExcelFile();
			
			PrintWriter out = response.getWriter();
			out.print(filename);
			out.close();
			
		} catch (Exception e) {
			Debug.print(Debug.iError, "核算科目余额表查询打印失败！", e);
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}
	
	public ModelAndView print(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		HashMap mapResult = new HashMap();
		ASFuntion CHF=new ASFuntion();
		
		String filename = CHF.showNull(request.getParameter("filename"));
		
//		vpage strPrintTitleRows
		mapResult.put("refresh","");
		
		mapResult.put("saveasfilename","核算余额表查询");			
		mapResult.put("bVpage","true");
		mapResult.put("strPrintTitleRows","$2:$4");
		mapResult.put("filename", filename);
		
		return new ModelAndView("/Excel/tempdata/PrintandSave.jsp",mapResult);
	}
}
