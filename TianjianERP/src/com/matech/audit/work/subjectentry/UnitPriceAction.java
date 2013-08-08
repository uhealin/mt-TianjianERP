package com.matech.audit.work.subjectentry;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import com.matech.audit.service.project.ProjectService;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.datagrid.FormatType;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.sys.UTILSysProperty;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.service.print.PrintSetup;

public class UnitPriceAction extends MultiActionController{
	private final String _strSuccess = "/unitprice/List.jsp";
	private final String _strBList = "/subjectentry/BList.jsp";
	
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn=null;
		HashMap mapResult = new HashMap();
		try {
			ASFuntion CHF=new ASFuntion();
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			
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
			
			conn = new DBConnect().getConnect(T1);
			SubjectEntry subjectEntry = new SubjectEntry(conn);
			
			/**
			 * 没有狗不能看2005年后的帐套
			 */
			int accAllowYear = Integer.parseInt(UTILSysProperty.SysProperty.getProperty("accAllowYear")); 
			if (!"".equals(acc) && Integer.parseInt(acc.substring(6)) > accAllowYear && JRockey2Opp.getUserLic() <= 0) {
			    final String TRY_URL = "/AuditSystem/AS_SYSTEM/error_page.jsp?tip=999";
			    response.sendRedirect(TRY_URL);
			    return null;
			}
			
			
			if(!"".equals(acc) && subjectEntry.isNotUnit(acc)){
				
				
				ASTextKey tkey = new ASTextKey(conn);
				String TName = "";
				if(!"".equals(T1)) TName = tkey.TextCustomerName(T1);
				
				
				userSession.setCurChoiceBeginYear(acc.substring(6));
				userSession.setCurChoiceEndYear(acc.substring(6));
				
				String BeginDate = CHF.showNull(request.getParameter("BeginDate"));
				if("".equals(BeginDate)){
					BeginDate = CHF.showNull(userSession.getCurChoiceBeginMonth());
					if("".equals(BeginDate)){
						BeginDate = CHF.showNull(userSession.getCurProjectBeginMonth());
					}
				}else{
					userSession.setCurChoiceBeginMonth(BeginDate);
				}
				if("".equals(BeginDate)) BeginDate = "01";

				String EndDate = CHF.showNull(request.getParameter("EndDate"));
				if("".equals(EndDate)){
					EndDate = CHF.showNull(userSession.getCurChoiceEndMonth());
					if("".equals(EndDate)){
						EndDate = CHF.showNull(userSession.getCurProjectEndMonth());
					}
				}else{
					userSession.setCurChoiceEndMonth(EndDate);
				}
				if("".equals(EndDate)) EndDate = "12";
				
				String SubjectID = CHF.showNull(request.getParameter("SubjectID"));	
												
				if("".equals(subjectEntry.getSName(acc, SubjectID))){
					SubjectID = "";
				}
				
				String Subjects = subjectEntry.getSubjects(acc);
				
				mapResult.put("T1", T1);
				mapResult.put("AccPackageID", acc);
				mapResult.put("TName", TName);				
				mapResult.put("BeginDate", BeginDate);				
				mapResult.put("EndDate", EndDate);
				mapResult.put("SubjectID", SubjectID);
				mapResult.put("Subjects", Subjects);
				
			}else{
				mapResult.put("T1", "");
				mapResult.put("AccPackageID", "");
			}
		} catch (Exception e) {
			Debug.print(Debug.iError, "数量明细帐查询失败！", e);
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		return new ModelAndView(_strSuccess,mapResult);
	}
	
	
	public ModelAndView blist(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn=null;
		HashMap mapResult = new HashMap();
		try {
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			ASFuntion CHF=new ASFuntion();
			
			String user=CHF.showNull(userSession.getUserId());
			String projectid=CHF.showNull(userSession.getCurProjectId());
			if("".equals(projectid)) projectid = "-1";
			
			String T1 = CHF.showNull(request.getParameter("T1"));
			String TabName = CHF.showNull(request.getParameter("TabName"));
			
			String acc = CHF.showNull(request.getParameter("AccPackageID"));
			if("".equals(acc)){
				acc = CHF.showNull(userSession.getCurChoiceAccPackageId());
				if("".equals(acc)){
					acc = CHF.showNull(userSession.getCurAccPackageId());
				}
			}
			
			String BeginYear = CHF.showNull(request.getParameter("BeginYear"));
			if("".equals(BeginYear)){
				BeginYear = CHF.showNull(userSession.getCurChoiceBeginYear());
				if("".equals(BeginYear)){
					BeginYear = CHF.showNull(userSession.getCurProjectBeginYear());
				}
			}
			String EndYear = CHF.showNull(request.getParameter("EndYear"));
			if("".equals(EndYear)){
				EndYear = CHF.showNull(userSession.getCurChoiceEndYear());
				if("".equals(EndYear)){
					EndYear = CHF.showNull(userSession.getCurProjectEndYear());
				}
			}
			String BeginDate = CHF.showNull(request.getParameter("BeginDate"));
			if("".equals(BeginDate)){
				BeginDate = CHF.showNull(userSession.getCurChoiceBeginMonth());
				if("".equals(BeginDate)){
					BeginDate = CHF.showNull(userSession.getCurProjectBeginMonth());
				}
			}
			String EndDate = CHF.showNull(request.getParameter("EndDate"));
			if("".equals(EndDate)){
				EndDate = CHF.showNull(userSession.getCurChoiceEndMonth());
				if("".equals(EndDate)){
					EndDate = CHF.showNull(userSession.getCurProjectEndMonth());
				}
			}
			
			String SubjectID = CHF.showNull(request.getParameter("SubjectID"));
			conn = new DBConnect().getConnect(T1);
			SubjectEntry subjectEntry = new SubjectEntry(conn);
			
			Map map = subjectEntry.SubjectUnitName(acc,SubjectID);
			if(!subjectEntry.ExistsTable(TabName)){
				subjectEntry.CreateUnitTable(TabName,map);
				subjectEntry.DataToUnitTable(map,TabName,user,projectid,T1,SubjectID,BeginYear,BeginDate,EndYear,EndDate);
			}
			
			String sTable = "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tr><td>科目名称：<font color='blue'>"+subjectEntry.getSubFullName(acc,SubjectID)+"</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;期间：<font color='blue'>"+BeginYear+"年"+BeginDate+"月－"+EndYear+"年"+EndDate+"月</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;本位币名称：<font color='blue'>"+new ASTextKey(conn).getACurrRate(acc)+"</font></td></tr></table>";
			
			mapResult.put("SubjectID", SubjectID);
			mapResult.put("TabName", TabName);
			mapResult.put("T1", T1);
			mapResult.put("sTable", sTable);
			
			/**
			 * DataGrid
			 */
			if(!"".equals(SubjectID)){
				DataGridProperty pp = new DataGridProperty();
				pp.setTableID("UnitPrice");
				pp.setCustomerId(T1);
				pp.setPageSize_CH(50);
				pp.setWhichFieldIsValue(3);
				pp.setOrderBy_CH("id");
				pp.setDirection("asc");
				pp.setCancelOrderby(true);
				
				pp.setTrActionProperty(true);
				pp.setTrAction("  AccPackageID='"+acc+"' vchdate='${vchdate}' voucherid='${voucherid}' style='cursor:hand;' onDBLclick='goSort();'");
				
				String TableHead = "";
				String sql = "";
				
				TableHead = "年,月,凭证日期,字,号,摘要,";
				
//				sql = "select id,vchmonth vchmonth1,autoid,voucherid,p1,p2,IF(substring(vchdate,9)='00',vchyear,'') vchyear,IF(substring(vchdate,9)='00',vchmonth,'') vchmonth,IF(substring(vchdate,9)='00' or substring(vchdate,9)='97' or substring(vchdate,9)='98','',vchdate) vchdate,typeid,oldvoucherid,summary, ";
				sql = "select id,a.vchmonth vchmonth1,autoid,a.voucherid," +
				"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else y.vchid end p1," +
				"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else z.vchid end p2," +
				"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else t.vchid end p3," +
				"IF(substring(a.vchdate,9)='00',vchyear,'') vchyear,IF(substring(a.vchdate,9)='00',vchmonth,'') vchmonth," +
				"case substring(a.vchdate,9) when '00' then '' when '97' then '' when '98' then '' else a.vchdate end vchdate," +
				"a.typeid,a.oldvoucherid,a.summary, ";
				
				if(acc.equals(userSession.getCurAccPackageId()) && userSession.getCurProjectId()!=null){
					TableHead = "抽,疑,年,月,凭证日期,字,号,摘要,";
					pp.addColumn("抽","p1",FormatType.showTakeOut );
					pp.addColumn("疑","p2",FormatType.showDoubt );
					ProjectService projectService = new ProjectService(conn);
					if("税务审计".equals(projectService.getProjectById(projectid).getAuditPara())){
						pp.addColumn("税", "p3", "showTaskTax");
						TableHead = "抽,疑,税,年,月,凭证日期,字,号,摘要,";
					}
				}
				
				
				pp.addColumn("年", "vchyear","showCenter");
				pp.addColumn("月", "vchmonth","showCenter");
				pp.addColumn("凭证日期", "vchdate","showCenter");
				pp.addColumn("字", "typeid","showCenter");
				pp.addColumn("号", "oldvoucherid","showCenter");
				pp.addColumn("摘要", "summary");
//				pp.addColumn("摘要", "summary",null,null,"<td style=\"word-break: keep-all;\">${value}</td>");

				
				Set coll = map.keySet();
				int ii =1;
				
				String sqlHead1 = "";
				String sqlHead2 = "";
				String sqlHead3 = "";
				
				for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) map.get(key);
					sql += "if(substring(a.vchdate,9)='00','',debitPrice"+ii+") debitPrice"+ii+",if(substring(a.vchdate,9)='00','',debit"+key+") debit"+key+",";
					sqlHead1 += "单价,"+value+",";
					
					pp.addColumn("单价", "debitPrice"+ii,"showMoney");
					pp.addColumn(value, "debit"+key,"showMoney");
					ii ++;
				}
				sql += "if(substring(a.vchdate,9)='00','',debit) debit,";
				sqlHead1 += "借方本位币";
				TableHead += "借方{"+sqlHead1+"},";
				
				pp.addColumn("借方本位币", "debit","showMoney");
				
				ii=1;
				for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) map.get(key);
					sql += "if(substring(a.vchdate,9)='00','',creditPrice"+ii+") creditPrice"+ii+",if(substring(a.vchdate,9)='00','',credit"+key+") credit"+key+",";
					sqlHead2 += "单价,"+value+",";
					
					pp.addColumn("单价", "creditPrice"+ii,"showMoney");
					pp.addColumn(value, "credit"+key,"showMoney");
					ii ++;
				}
				sql += "if(substring(a.vchdate,9)='00','',credit) credit,IF(dateremain>0,'借',if(dateremain<0,'贷','平')) rec,";
				sqlHead2 += "贷方本位币";
				TableHead += "贷方{"+sqlHead2+"},方向,";
				
				pp.addColumn("贷方本位币", "credit","showMoney");
				pp.addColumn("方向", "rec","showCenter");

				ii=1;
				for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) map.get(key);
//					sql += "abs(dRemain"+key+") dRemain"+key+",";
					sql += "if(dateremain<0,(-1)*dRemain"+key+",dRemain"+key+") as dRemain"+key+",";
					sqlHead3 += value+",";
					
					pp.addColumn(value, "dRemain"+key,"showMoney");
					ii ++;
				}
				sql +="ABS(dateRemain) dateRemain  from (select * from `"+TabName+"` order by id ${LIMIT} ) a " +
				"left join (select distinct vchid from z_voucherspotcheck  where projectid='" +projectid + "' and createor='" + user +"'   ) y on a.voucherid=y.vchid  " +
				"left join (select distinct vchid from z_question  where projectid='" +projectid + "' and createor='" + user +"'   )  z on a.voucherid=z.vchid  " +
				"left join (select distinct vchid from z_taxcheck  where projectid='" +projectid + "' and createor='" + user +"'   )  t on a.autoid=t.vchid  ";

	
				sqlHead3 +="本位币余额";
				TableHead +="余额{"+sqlHead3+"}";
				
				pp.addColumn("本位币余额", "dateRemain","showMoney");
				
				pp.setTableHead(TableHead);
				
				pp.setLimitByOwnEnable(true);		
				pp.setCountsql("select count(*) as datagrid_count from `" + TabName + "`");
				pp.setEnableCountTr(false);
				pp.setFixedHeader(true) ;
				pp.setSQL(sql);
				request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
				
				mapResult.put("DataGrid", "UnitPrice");
				
			}
			
		} catch (Exception e) {
			Debug.print(Debug.iError, "科目明细账查询失败 2:叶子无外币有数量！", e);
            throw e;
		} finally {
			DbUtil.close(conn);
		} 
		return new ModelAndView(_strBList,mapResult);
	}

	public ModelAndView print(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn=null;
		HashMap mapResult = new HashMap();
		try {
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			ASFuntion CHF=new ASFuntion();
			String user=CHF.showNull(userSession.getUserId());
			String projectid=CHF.showNull(userSession.getCurProjectId());
			if("".equals(projectid)) projectid = "-1";
			
			String SubjectID = (String)CHF.showNull(request.getParameter("SubjectID"));
			String BeginYear =(String)CHF.showNull(request.getParameter("BeginYear"));
			String BeginDate = (String)CHF.showNull(request.getParameter("BeginDate"));
			String EndYear =(String)CHF.showNull(request.getParameter("EndYear"));
			String EndDate = (String)CHF.showNull(request.getParameter("EndDate"));
	
			String TabName = (String)CHF.showNull(request.getParameter("TabName"));
			
			String acc = (String) CHF.showNull(request.getParameter("AccPackageID"));
			
			conn = new DBConnect().getConnect(acc.substring(0, 6));
			SubjectEntry subjectEntry = new SubjectEntry(conn);
			
			Map Umap = subjectEntry.SubjectUnitName(acc,SubjectID);
			
			ASTextKey tkey = new ASTextKey(conn);
			String TName = "";
			String ss = "";
			String full = subjectEntry.getSubFullName(acc,SubjectID);
			if(!"".equals(acc.substring(0, 6))) {
				TName = tkey.TextCustomerName(acc.substring(0, 6));
				ss = tkey.getACurrRate(acc);
			}
			
			DataGridProperty pp = (DataGridProperty)request.getSession().getAttribute(DataGrid.sessionPre+"UnitPrice"); //科目明细账查询
			
			PrintSetup printSetup = new PrintSetup(conn);
			printSetup.setStrTitles(new String[]{TName+" 数量明细账查询"});
			
			List[] lists = new List[1];
			lists[0] = new ArrayList();  
			lists[0].add(0, "1");
			lists[0].add(1, "科目名称："+full +"    期间："+BeginYear+"年"+BeginDate+"月－"+EndYear+"年"+EndDate+"月     贷币单位："+ss);
			lists[0].add(2, "9");
			lists[0].add(3, null);
			
			printSetup.setPoms(lists);
			printSetup.setIColumnWidths(new int[]{7,3,14,10,10,23,18,17,18,17,18,18,3,17,18});
			printSetup.setCharColumn(new String[]{"1`2`3`4`5`6"});
			String sRows = "";
			String [][] CTName = new String[1][2];
			String [][] ColName = new String[1][2];
			
			CTName[0][1] = ",";
			ColName[0][1]= ",";
			
			CTName[0][0] = "年,月,凭证日期,字,号,摘要,";
			ColName[0][0] = "年,月,凭证日期,字,号,摘要,";
			
			String sql = "select vchyear,vchmonth,vchdate,typeid,oldvoucherid,summary,";
			
			Set coll = Umap.keySet();
			int ii =1;
			for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
				String key = (String) iter.next();
				String value = (String) Umap.get(key);
				sql += "debitPrice"+ii+",debit"+key+",";
				CTName[0][0] += "借方,借方,";
				ColName[0][0] += "单价,"+value+",";
				ii ++;
			}
			sql += "debit,";
			CTName[0][0] += "借方,";
			ColName[0][0] += "借方本位币,";
			
			ii=1;
			for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
				String key = (String) iter.next();
				String value = (String) Umap.get(key);
				sql += "creditPrice"+ii+",credit"+key+",";
				CTName[0][0] += "贷方,贷方,";
				ColName[0][0] += "单价,"+value+",";
				ii ++;
			}
			sql += "credit,rec,";
			CTName[0][0] += "贷方,方向,";
			ColName[0][0] += "贷方本位币,方向,";
			
			ii=1;
			for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
				String key = (String) iter.next();
				String value = (String) Umap.get(key);
				sql += "dRemain"+key+",";
				CTName[0][0] += "余额,";
				ColName[0][0] += value+",";
				ii ++;
			}
			sql += "dateRemain";
			CTName[0][0] += "余额";
			ColName[0][0] += "本位币余额";
			
			printSetup.setStrQuerySqls(new String[]{sql + " from ("+pp.getFinishSQLDeleteLimit()+") a where 1=1   order by id " });
			printSetup.setHeaders(CTName, ColName);
			
			sRows="$2:$5";
			
			String filename = printSetup.getExcelFile();			
			
			//vpage strPrintTitleRows
			mapResult.put("refresh","");
			
			mapResult.put("saveasfilename","数量明细帐");			
			mapResult.put("bVpage","false");
			mapResult.put("strPrintTitleRows",sRows);
			mapResult.put("filename", filename);
		
		} catch (Exception e) {
			Debug.print(Debug.iError, "科目明细账打印失败!", e);
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		return new ModelAndView("/Excel/tempdata/PrintandSave.jsp",mapResult);
	}
}
