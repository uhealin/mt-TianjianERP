package com.matech.audit.work.assitem;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.del.JRockey2Opp;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.matech.audit.pub.datagrid.DataGrid;
import com.matech.audit.pub.datagrid.DataGridProperty;
import com.matech.audit.pub.db.DBConnect;
import com.matech.audit.pub.func.ASTextKey;
import com.matech.audit.service.assitem.AssItemService;
import com.matech.audit.service.project.ProjectService;
import com.matech.audit.work.subjectentry.SubjectEntry;
import com.matech.audit.work.subjectentry.SubjectInfo;
import com.matech.framework.listener.UserSession;
import com.matech.framework.pub.autocode.DELUnid;
import com.matech.framework.pub.datagrid.FormatType;
import com.matech.framework.pub.db.DbUtil;
import com.matech.framework.pub.sys.UTILSysProperty;
import com.matech.framework.pub.util.ASFuntion;
import com.matech.framework.pub.util.Debug;
import com.matech.framework.service.print.PrintSetup;

public class AssitemEntryAction  extends MultiActionController{
	private final String _strSuccess = "/assitementry/List.jsp";
	private final String _strBList = "/assitementry/BList.jsp";
	
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ModelAndView modelAndView = new ModelAndView(_strSuccess);
		Connection conn=null;
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
			
			modelAndView.addObject("T1", T1);
			modelAndView.addObject("AccPackageID", acc);
			/**
			 * 没有狗不能看2005年后的帐套
			 */
			int accAllowYear = Integer.parseInt(UTILSysProperty.SysProperty.getProperty("accAllowYear")); 
			if (!"".equals(acc) && Integer.parseInt(acc.substring(6)) > accAllowYear && JRockey2Opp.getUserLic() <= 0) {
			    final String TRY_URL = "/AuditSystem/AS_SYSTEM/error_page.jsp?tip=999";
			    response.sendRedirect(TRY_URL);
			    return null;
			}
			
			String html = CHF.showNull(request.getParameter("html"));	//用于标志要不要用setTdtoValue　1为要　
			
			if(!"".equals(acc) && new Assitem(conn).isNotAssitem(acc)){
				String AccAll = CHF.showNull(request.getParameter("AccAll"));
				String AssItemID1 = CHF.showNull(request.getParameter("AssItemID1"));
				if(!"".equals(AssItemID1)){
					AccAll = acc + AssItemID1;
				}
				
				String SubjectID1 = CHF.showNull(request.getParameter("SubjectID1"));
				String AssItemID2 = CHF.showNull(request.getParameter("AssItemID2"));
				String SubjectID2 = CHF.showNull(request.getParameter("SubjectID2"));
				String AssItemID = CHF.showNull(request.getParameter("AssItemID"));
				
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
				
				if(!"".equals(AssItemID) && "".equals(AssItemID1) ){
					AssItemID1 = new Assitem(conn).getPAss(SubjectID1,AssItemID,acc);
				}
				
				modelAndView.addObject("AccAll", AccAll);
				modelAndView.addObject("AssItemID1", AssItemID1);
				modelAndView.addObject("SubjectID1", SubjectID1);
				modelAndView.addObject("AssItemID2", AssItemID2);
				modelAndView.addObject("SubjectID2", SubjectID2);
				modelAndView.addObject("AssItemID", AssItemID);
				modelAndView.addObject("BeginDate", BeginDate);
				modelAndView.addObject("EndDate", EndDate);
				
				String tType = CHF.showNull(request.getParameter("tType"));
				if("".equals(tType)) tType = "0";
				modelAndView.addObject("tType", tType);
				
				modelAndView.addObject("opt", "1");
			}else{
				modelAndView.addObject("opt", "0");	
			}
			
			modelAndView.addObject("html", html);
			
		} catch (Exception e) {
			Debug.print(Debug.iError, "核算项目明细账查询失败！", e);
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}

	public ModelAndView get(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		//AccPackageID="+acc+"&asstype="+ad+"&subjectid="+sd
		Connection conn=null;
		try {
			ASFuntion CHF=new ASFuntion();
			String acc = CHF.showNull(request.getParameter("AccPackageID"));
			String SubjectID = CHF.showNull(request.getParameter("SubjectID"));
			String AssItemID = CHF.showNull(request.getParameter("AssItemID"));
			
			String AssItem = CHF.showNull(request.getParameter("AssItem"));
			
			response.setContentType("text/html;charset=utf-8");  //设置编码
			PrintWriter out = response.getWriter();
			conn = new DBConnect().getConnect(acc.substring(0,6));
			Assitem assitem = new Assitem(conn);
			out.print(assitem.getAssProject(acc, assitem.getAssItemName(acc, AssItemID), SubjectID, AssItem));
			out.close();
		} catch (Exception e) {
			Debug.print(Debug.iError, "核算项目明细账查询失败！", e);
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}
	
	public ModelAndView centerpage(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn=null;
		try {
			ASFuntion CHF=new ASFuntion();
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			
			String html = CHF.showNull(request.getParameter("html"));	//用于标志要不要用setTdtoValue　1为要　
//			String user=CHF.showNull(userSession.getUserId());
//			String projectid=CHF.showNull(userSession.getCurProjectId());
						
			String acc = CHF.showNull(request.getParameter("AccPackageID"));
			if("".equals(acc)){
				acc = CHF.showNull(userSession.getCurChoiceAccPackageId());
				if("".equals(acc)){
					acc = CHF.showNull(userSession.getCurAccPackageId());
				}
			}else{
				userSession.setCurChoiceAccPackageId(acc);
			}
			conn = new DBConnect().getConnect(acc.substring(0, 6));	
			String BeginYear = CHF.showNull(request.getParameter("BeginYear"));
//			String BeginYear = CHF.showNull(acc.substring(6));			
			if("".equals(BeginYear)){
				BeginYear = CHF.showNull(userSession.getCurChoiceBeginYear());
				if("".equals(BeginYear)){
					BeginYear = CHF.showNull(userSession.getCurProjectBeginYear());
				}
			}else{
				userSession.setCurChoiceBeginYear(BeginYear);
			}
			
			String EndYear = CHF.showNull(request.getParameter("EndYear"));
//			String EndYear = CHF.showNull(acc.substring(6));
			if("".equals(EndYear)){
				EndYear = CHF.showNull(userSession.getCurChoiceEndYear());
				if("".equals(EndYear)){
					EndYear = CHF.showNull(userSession.getCurProjectEndYear());
				}
			}else{
				userSession.setCurChoiceEndYear(EndYear);
			}
			
			
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
			
			String TabName = CHF.showNull(request.getParameter("tab"));//tab
			
			String AssItemID = CHF.showNull(request.getParameter("AssItemID1"));
			String SubjectID = CHF.showNull(request.getParameter("SubjectID1"));

			if("".equals(AssItemID) && "".equals(SubjectID)){
				AssItemID = CHF.showNull(request.getParameter("AssItemID2"));
				SubjectID = CHF.showNull(request.getParameter("SubjectID2"));
			}
			
			String AssItem = CHF.showNull(request.getParameter("AssItemID"));
			if("".equals(AssItem)){
				AssItem = CHF.showNull(request.getParameter("AssItemID3"));	
			}
			if("".equals(AssItemID)){//从具体的核算拿到可算类型	
				AssItemID = new AssItemService(conn).getAssitemTypeById(AssItem,acc);			
			}
			request.setAttribute("AssItem", AssItem);

//			System.out.println("py0 AssItem:=" + (String)request.getAttribute("AssItem"));
			String string = AssItemID;
			if(!"".equals(AssItem)) string = AssItem;
			
			
			int result = new Assitem(conn).AssitemProperty(acc, SubjectID, string);
			
			/**
			 * 只显示本位币
			 */
			String currency = CHF.showNull(request.getParameter("currency"));
			if(!"".equals(currency)) {
				result = 0;
			}
			
			
			/*
			0:非叶子与无外币和无数量的叶子
			1:叶子有外币无数量
			2:叶子无外币有数量
			3:叶子有外币有数量
			*/
			
			switch(result){
			case 0://bonelist				
				request.getRequestDispatcher("assitementry.do?method=bonelist&AccPackageID="+acc+"&TabName="+TabName+"&AssItemID="+AssItemID+"&SubjectID="+SubjectID+"&AssItem="+AssItem+"&currency="+currency).forward(request, response);
				break;
			case 1://btwolist
			
				request.getRequestDispatcher("assitementry.do?method=btwolist&AccPackageID="+acc+"&TabName="+TabName+"&AssItemID="+AssItemID+"&SubjectID="+SubjectID+"&AssItem="+AssItem).forward(request, response);
				break;
			case 2://bthreelist
				request.getRequestDispatcher("assitementry.do?method=bthreelist&AccPackageID="+acc+"&TabName="+TabName+"&AssItemID="+AssItemID+"&SubjectID="+SubjectID+"&AssItem="+AssItem).forward(request, response);
				break;
			case 3://bfourlist
				request.getRequestDispatcher("assitementry.do?method=bfourlist&AccPackageID="+acc+"&TabName="+TabName+"&AssItemID="+AssItemID+"&SubjectID="+SubjectID+"&AssItem="+AssItem).forward(request, response);
				break;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return null;
	}
	
	public ModelAndView bonelist(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ModelAndView modelAndView = new ModelAndView(_strBList);
		Connection conn=null;
		try {
			ASFuntion CHF=new ASFuntion();
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			
			String html = CHF.showNull(request.getParameter("html"));	//用于标志要不要用setTdtoValue　1为要
			
			String user=CHF.showNull(userSession.getUserId());
			String projectid=CHF.showNull(userSession.getCurProjectId());
			if("".equals(projectid)) projectid = "-1";
			
			String TabName = CHF.showNull(request.getParameter("TabName"));
			String acc = CHF.showNull(request.getParameter("AccPackageID"));
			
			String AssItemID = CHF.showNull(request.getParameter("AssItemID"));
			String SubjectID = CHF.showNull(request.getParameter("SubjectID"));
			
			String AssItem = CHF.showNull((String)request.getAttribute("AssItem"));
			
			String currency = CHF.showNull(request.getParameter("currency"));
			
			String toSubjects = CHF.showNull(request.getParameter("toSubjects"));//显示对方科目
			String sqlwhere = "";
			if(!toSubjects.equals("1")){
				sqlwhere = " and  1=2 ";
			}
			String toLevel1 = CHF.showNull(request.getParameter("toLevel1"));//对方科目显示的级数
			String subjectnameSql = "";
			if(toLevel1.equals("1")){
				subjectnameSql = "if(locate('/',b.subjectfullname1)=0,b.subjectfullname1,substr(b.subjectfullname1,1,locate('/',b.subjectfullname1)-1))";
			}else{
//				subjectnameSql =  "if(b.subjectname1!=b.subjectfullname1 && concat(if(locate('/',b.subjectfullname1)=0,b.subjectfullname1,substr(b.subjectfullname1,1,locate('/',b.subjectfullname1)-1)),'/',b.subjectname1)!=b.subjectfullname1 \n"
//					 +" ,concat(if(locate('/',b.subjectfullname1)=0,b.subjectfullname1,substr(b.subjectfullname1,1,locate('/',b.subjectfullname1)-1)),'/../',b.subjectname1) \n"
//					 +" ,b.subjectfullname1) ";
				subjectnameSql =  " b.subjectfullname1 ";
			}
			
			if("".equals(AssItem)) AssItem = CHF.showNull(request.getParameter("AssItem"));
			
			String BeginYear = CHF.showNull(request.getParameter("BeginYear"));
			if("".equals(BeginYear)){
				BeginYear = CHF.showNull(userSession.getCurChoiceBeginYear());
				if("".equals(BeginYear)){
					BeginYear = CHF.showNull(userSession.getCurProjectBeginYear());
				}
			}
			if("".equals(BeginYear)){
				BeginYear = acc.substring(6);
			}
			
			String EndYear = CHF.showNull(request.getParameter("EndYear"));
			if("".equals(EndYear)){
				EndYear = CHF.showNull(userSession.getCurChoiceEndYear());
				if("".equals(EndYear)){
					EndYear = CHF.showNull(userSession.getCurProjectEndYear());
				}
			}
			if("".equals(EndYear)){
				EndYear = acc.substring(6);
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
			
			conn = new DBConnect().getConnect(acc.substring(0,6));
			Assitem assitem = new Assitem(conn);
			
			if(!"".equals(AssItem)){
				String full = assitem.getAssTotalName(acc,"".equals(AssItem)?AssItemID:AssItem);
				String sTable = "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tr><td>核算名称：<font color=\"blue\">"+full+"</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;年度：<font color=\"blue\">"+acc.substring(6)+"</font></td></tr></table>";
				modelAndView.addObject("TabName", TabName);			
				modelAndView.addObject("sTable", sTable);
				
				if(!assitem.ExistsTable(TabName)){
	
					assitem.CreateTable(TabName);
					if(!"".equals(AssItem)) AssItemID = AssItem;
					assitem.DataToTable(TabName, user, projectid, acc.substring(0,6), SubjectID, AssItemID, BeginYear, BeginDate, EndYear, EndDate);
					
					int result = assitem.LowAssItemProperty(acc,SubjectID,AssItemID);
					modelAndView.addObject("result", new Integer(result));
					modelAndView.addObject("SubjectID", SubjectID);
					modelAndView.addObject("AssItemID", AssItemID);
				}
				
				if(!"".equals(AssItemID) && !"".equals(SubjectID)){
					DataGridProperty pp = new DataGridProperty(){
						public void onSearch(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws Exception {
							String TabName = this.getRequestValue("TabName");

							String allOpt = this.getRequestValue("allOpt");
							String moneyChao = this.getRequestValue("moneyChao");
							
							if(moneyChao == null || "".equals(moneyChao.trim())){
								allOpt = "0";
							}
							
							String strWhere = "";
							String allType = this.getRequestValue("allType");
							if("".equals(allType)) allType = "1";
							if("1".equals(allType)){
								strWhere = " ABS(dateRemain) ";
							}else if("2".equals(allType)){
								strWhere = " ABS(debit) ";
							}else if("3".equals(allType)){
								strWhere = " ABS(credit) ";
							}
							
							String where = "";
							if("1".equals(allOpt)){
								where = " and substring(vchdate,9) not in ('00','97','98') " +
								" and " + strWhere + " >" + moneyChao ;
							}else if("2".equals(allOpt)){
								where = " and substring(vchdate,9) not in ('00','97','98') " +
								" and " + strWhere + " >" + moneyChao +
								" and id in (" +
								" 	select max(id) from `"+TabName+"` " +
								"	where 1=1 and substring(vchdate,9) not in ('00','97','98')" +
								"	group by vchdate" +
								" ) ";
							}else if("3".equals(allOpt)){
								where = " and substring(vchdate,9) not in ('00','97','98') " +
								" and " + strWhere + " >" + moneyChao +
								" and id in (" +
								" 	select max(id) from `"+TabName+"` " +
								"	where 1=1 and substring(vchdate,9) not in ('00','97','98')" +
								"	group by vchmonth" +
								" ) ";
							}


							this.setOrAddRequestValue("where",where);
						}
					};
					
					if("1".equals(html)){
						pp.setTdtoValue("<a href='St://${value}'>${tdvalue}</a>");	
					}
					
					pp.setTableID("AssItem"+assitem.getRandom());
					pp.setCustomerId(acc.substring(0,6));
					pp.setPageSize_CH(200);
					pp.setWhichFieldIsValue(4);
					
					pp.setOrderBy_CH("id");
					pp.setDirection("asc");
					pp.setCancelOrderby(true);
					
					String sql = "select id,a.vchmonth vchmonth1,a.voucherid,autoid," +
							"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else y.vchid end p1," +
							"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else z.vchid end p2," +
							"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else t.vchid end p3," +
							"IF(substring(a.vchdate,9)='00',vchmonth,'') vchmonth," +						
							"case substring(a.vchdate,9) when '00' then '' when '97' then '' when '98' then '' else a.vchdate end vchdate," +
							"a.typeid,a.oldvoucherid,a.assitemid,summary,if(substring(a.vchdate,9)='00','',debit)  debit,if(substring(a.vchdate,9)='00','',credit) credit,IF(dateremain>0,'借',if(dateremain<0,'贷','平')) rec,ABS(dateremain) dateremain,subjectnames " +
							"from (" +
							"	select a.*,group_concat(distinct "+subjectnameSql+") subjectnames   from (" +
							"		select *,IF(credit>0,1,-1) AS opt from `"+TabName+"` where 1=1 ${where} order by id ${LIMIT}" +
							"	) a left join c_subjectentry b " +
							"	on 1=1 " +
							"	AND SUBSTRING(b.VchDate,1,4)*12 + SUBSTRING(b.VchDate,6,2) >= " + (Integer.parseInt(BeginYear) * 12 + Integer.parseInt(BeginDate)) +  
							"	AND SUBSTRING(b.VchDate,1,4)*12 + SUBSTRING(b.VchDate,6,2) <= " + (Integer.parseInt(EndYear) * 12 + Integer.parseInt(EndDate)) + 
							"	AND a.voucherid=b.voucherid " +
							"	AND a.opt = IF(b.Dirction*b.occurvalue<0,-1,1) " +
							sqlwhere + 
							"	group by a.id  " +
							") a " +

							"left join (select distinct vchid from z_voucherspotcheck  where projectid='" +projectid + "' and createor='" + user +"'   ) y on a.voucherid=y.vchid " +
							"left join (select distinct vchid from z_question  where projectid='" +projectid + "' and createor='" + user +"'   )  z on a.voucherid=z.vchid  " +
							"left join (select distinct vchid from z_taxcheck  where projectid='" +projectid + "' and createor='" + user +"'   )  t on a.autoid=t.vchid " +
							"where 1=1 AND NOT (SUBSTRING(a.vchdate,9) = '00' AND vchmonth <> '01' AND id <> 1) ";					
					pp.setTrActionProperty(true);
					pp.setTrAction("  AccPackageID='"+acc+"' AssitemID='${assitemid}' VchDate='${vchdate}' TypeId='${typeid}' OldVoucherId='${oldvoucherid}' voucherid='${voucherid}' autoid='${autoid}' style='cursor:hand;' onDBLclick='goSort();'");
					String setColumnWidth = "";
					if(acc.equals(userSession.getCurAccPackageId()) && userSession.getCurProjectId()!=null && !"1".equals(html)){
						pp.addColumn("抽","p1",FormatType.showTakeOut );
						setColumnWidth = "4,";
//						pp.addColumn("疑","p2",FormatType.showDoubt );	
						ProjectService projectService = new ProjectService(conn);
						if("税务审计".equals(projectService.getProjectById(projectid).getAuditPara())){
							pp.addColumn("税", "p3", "showTaskTax");
						}
					}
	//				pp.addColumn("年", "vchyear","showCenter");
					setColumnWidth += "8,8,5,25,10";
//					pp.addColumn("月", "vchmonth","showCenter");
					pp.addColumn("凭证日期", "vchdate","showCenter");
					pp.addColumn("字", "typeid","showCenter");
					pp.addColumn("号", "oldvoucherid","showCenter");
					pp.addColumn("摘要", "summary");
					pp.addColumn("核算编号", "assitemid");
					if(toSubjects.equals("1")){
						pp.addColumn("对方科目", "subjectnames");
						setColumnWidth += ",15";
					}
					setColumnWidth += ",10,10,5,10";
					pp.setColumnWidth(setColumnWidth);
					
					
					pp.addColumn("借方发生额", "debit","showMoney");
					pp.addColumn("贷方发生额", "credit","showMoney");
					pp.addColumn("方向", "rec","showCenter");
					pp.addColumn("余　额", "dateremain","showMoney");
					
					
					pp.addSqlWhere("where", "${where}");
					pp.addInputValue("allOpt");
					pp.addInputValue("allType");
					pp.addInputValue("moneyChao");
					pp.addInputValue("TabName");
					
					pp.setLimitByOwnEnable(true);		
					pp.setCountsql("select count(*) as datagrid_count from `" + TabName + "` where 1=1 ${where} ");
					pp.setEnableCountTr(false);
					pp.setFixedHeader(true) ;
					pp.setSQL(sql);
					
					String TName = "";
					if(!"".equals(acc)) TName = new ASTextKey(conn).TextCustomerName(acc.substring(6));
					
					pp.setPrintEnable(true);
					pp.setPrintTitle(TName + "      科目明细账查询");
					pp.setPrintCharColumn("1`2`3`4`5") ;
					
					pp.setPrintPoms("核算名称："+full+"　　　　　　年度："+acc.substring(6));

					
					request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
					
					modelAndView.addObject("DataGrid", pp.getTableID());
					
					String Currency = "0";
					modelAndView.addObject("Currency", Currency);
					
				}
			}
			modelAndView.addObject("AssItem", AssItem);
			modelAndView.addObject("currency", currency);
			modelAndView.addObject("html", html);
			
		} catch (Exception e) {
			Debug.print(Debug.iError, "核算项目明细账查询失败！", e);
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
		
	}
	
	public ModelAndView btwolist(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ModelAndView modelAndView = new ModelAndView(_strBList);
		Connection conn=null;
		try {
			ASFuntion CHF=new ASFuntion();
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			
			String html = CHF.showNull(request.getParameter("html"));	//用于标志要不要用setTdtoValue　1为要
			
			String user=CHF.showNull(userSession.getUserId());
			String projectid=CHF.showNull(userSession.getCurProjectId());
			if("".equals(projectid)) projectid = "-1";
			
			String TabName = CHF.showNull(request.getParameter("TabName"));
			String acc = CHF.showNull(request.getParameter("AccPackageID"));
			
			String AssItemID = CHF.showNull(request.getParameter("AssItemID"));
			String SubjectID = CHF.showNull(request.getParameter("SubjectID"));
			
			String toSubjects = CHF.showNull(request.getParameter("toSubjects"));//显示对方科目
			String sqlwhere = "";
			if(!toSubjects.equals("1")){
				sqlwhere = " and  1=2 ";
			}
			String toLevel1 = CHF.showNull(request.getParameter("toLevel1"));//对方科目显示的级数
			String subjectnameSql = "";
			if(toLevel1.equals("1")){
				subjectnameSql = "if(locate('/',b.subjectfullname1)=0,b.subjectfullname1,substr(b.subjectfullname1,1,locate('/',b.subjectfullname1)-1))";
			}else{
//				subjectnameSql =  "if(b.subjectname1!=b.subjectfullname1 && concat(if(locate('/',b.subjectfullname1)=0,b.subjectfullname1,substr(b.subjectfullname1,1,locate('/',b.subjectfullname1)-1)),'/',b.subjectname1)!=b.subjectfullname1 \n"
//					 +" ,concat(if(locate('/',b.subjectfullname1)=0,b.subjectfullname1,substr(b.subjectfullname1,1,locate('/',b.subjectfullname1)-1)),'/../',b.subjectname1) \n"
//					 +" ,b.subjectfullname1) ";
				subjectnameSql =  " b.subjectfullname1 ";
			}
			
			String AssItem = CHF.showNull((String)request.getAttribute("AssItem"));
			if("".equals(AssItem)) AssItem = CHF.showNull(request.getParameter("AssItem"));
			
			String BeginYear = CHF.showNull(request.getParameter("BeginYear"));
			if("".equals(BeginYear)){
				BeginYear = CHF.showNull(userSession.getCurChoiceBeginYear());
				if("".equals(BeginYear)){
					BeginYear = CHF.showNull(userSession.getCurProjectBeginYear());
				}
			}
			if("".equals(BeginYear)){
				BeginYear = acc.substring(6);
			}
			
			String EndYear = CHF.showNull(request.getParameter("EndYear"));
			if("".equals(EndYear)){
				EndYear = CHF.showNull(userSession.getCurChoiceEndYear());
				if("".equals(EndYear)){
					EndYear = CHF.showNull(userSession.getCurProjectEndYear());
				}
			}
			if("".equals(EndYear)){
				EndYear = acc.substring(6);
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
			
			conn = new DBConnect().getConnect(acc.substring(0,6));
			Assitem assitem = new Assitem(conn);
			
			if(!"".equals(AssItem)){
				String full = assitem.getAssTotalName(acc,"".equals(AssItem)?AssItemID:AssItem);
				String sTable = "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tr><td>核算名称：<font color=\"blue\">"+full+"</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;年度：<font color=\"blue\">"+acc.substring(6)+"</font></td></tr></table>";
				modelAndView.addObject("TabName", TabName);			
				modelAndView.addObject("sTable", sTable);
				
				if(!"".equals(AssItem)) AssItemID = AssItem;
				
				Map map = assitem.AssitemCurrency(acc, SubjectID, AssItemID);
				if(!assitem.ExistsTable(TabName)){
					assitem.CreateTable(TabName,map);				
					assitem.DataToTable(map,TabName, user, projectid, acc.substring(0,6), SubjectID, AssItemID, BeginYear, BeginDate, EndYear, EndDate,1);
				}
				
				if(!"".equals(AssItemID) && !"".equals(SubjectID)){
					DataGridProperty pp = new DataGridProperty(){
						public void onSearch(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws Exception {
							String TabName = this.getRequestValue("TabName");

							String allOpt = this.getRequestValue("allOpt");
							String moneyChao = this.getRequestValue("moneyChao");
							
							if(moneyChao == null || "".equals(moneyChao.trim())){
								allOpt = "0";
							}
							
							String strWhere = "";
							String allType = this.getRequestValue("allType");
							if("".equals(allType)) allType = "1";
							if("1".equals(allType)){
								strWhere = " ABS(dateRemain) ";
							}else if("2".equals(allType)){
								strWhere = " ABS(debit) ";
							}else if("3".equals(allType)){
								strWhere = " ABS(credit) ";
							}
							
							String where = "";
							if("1".equals(allOpt)){
								where = " and substring(vchdate,9) not in ('00','97','98') " +
								" and " + strWhere + " >" + moneyChao ;
							}else if("2".equals(allOpt)){
								where = " and substring(vchdate,9) not in ('00','97','98') " +
								" and " + strWhere + " >" + moneyChao +
								" and id in (" +
								" 	select max(id) from `"+TabName+"` " +
								"	where 1=1 and substring(vchdate,9) not in ('00','97','98')" +
								"	group by vchdate" +
								" ) ";
							}else if("3".equals(allOpt)){
								where = " and substring(vchdate,9) not in ('00','97','98') " +
								" and " + strWhere + " >" + moneyChao +
								" and id in (" +
								" 	select max(id) from `"+TabName+"` " +
								"	where 1=1 and substring(vchdate,9) not in ('00','97','98')" +
								"	group by vchmonth" +
								" ) ";
							}


							this.setOrAddRequestValue("where",where);
						}
					};
					
					if("1".equals(html)){
						pp.setTdtoValue("<a href='St://${value}'>${tdvalue}</a>");	
					}
					
					pp.setTableID("AssItem"+assitem.getRandom());
					pp.setCustomerId(acc.substring(0,6));
					pp.setPageSize_CH(200);
					pp.setWhichFieldIsValue(3);
					
					pp.setOrderBy_CH("id");
					pp.setDirection("asc");
					pp.setCancelOrderby(true);
									
					
					pp.setTrActionProperty(true);
					pp.setTrAction(" voucherid='${voucherid}' AccPackageID='"+acc+"' AssitemID='${assitemid}' VchDate='${vchdate}' TypeId='${typeid}' OldVoucherId='${oldvoucherid}' autoid='${autoid}' style='cursor:hand;' onDBLclick='goSort();'");
					
					String TableHead = "",TableHead1 = "";
					String sql = "";
					
					TableHead = "凭证日期,字,号,摘要,核算编号,${subjectid},";
					
					sql = "select id,a.vchmonth vchmonth1,autoid,a.voucherid," +
						"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else y.vchid end p1," +
						"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else z.vchid end p2," +
						"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else t.vchid end p3," +
						"IF(substring(a.vchdate,9)='00',vchyear,'') vchyear,IF(substring(a.vchdate,9)='00',vchmonth,'') vchmonth," +
						"case substring(a.vchdate,9) when '00' then '' when '97' then '' when '98' then '' else a.vchdate end vchdate," +
						"a.typeid,a.oldvoucherid,a.assitemid,summary, ";
					String setColumnWidth = "";
					if(acc.equals(userSession.getCurAccPackageId()) && userSession.getCurProjectId()!=null && !"1".equals(html)){
//						TableHead = "抽,月,凭证日期,字,号,核算编号,${subjectid},摘要,";
						TableHead1 = "抽,";
						pp.addColumn("抽","p1",FormatType.showTakeOut );
//						pp.addColumn("疑","p2",FormatType.showDoubt );	
						setColumnWidth = "4,";
						ProjectService projectService = new ProjectService(conn);
						if("税务审计".equals(projectService.getProjectById(projectid).getAuditPara())){
							pp.addColumn("税", "p3", "showTaskTax");
//							TableHead = "抽,疑,税,月,凭证日期,字,号,核算编号,${subjectid},摘要,";
							TableHead1 = "抽,税,";
						}
					}
					
					setColumnWidth += "8,8,5,25,10";
	//				pp.addColumn("年", "vchyear","showCenter");
//					pp.addColumn("月", "vchmonth","showCenter");
					pp.addColumn("凭证日期", "vchdate","showCenter");
					pp.addColumn("字", "typeid","showCenter");
					pp.addColumn("号", "oldvoucherid","showCenter");
					pp.addColumn("摘要", "summary");
					pp.addColumn("核算编号", "assitemid");
					if(toSubjects.equals("1")){
						pp.addColumn("对方科目", "subjectnames");
						TableHead = TableHead.replaceAll("\\$\\{subjectid\\}", "对方科目");
						setColumnWidth += ",15";
					}else{
						TableHead = TableHead.replaceAll("\\$\\{subjectid\\},","");
					}
					pp.setColumnWidth(setColumnWidth);
					
				
					
					Set coll = map.keySet();
					int ii =1;
					
					String sqlHead1 = "";
					String sqlHead2 = "";
					String sqlHead3 = "";
					
					for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) map.get(key);
						sql += "if(substring(a.vchdate,9)='00','',debitrate"+ii+") debitrate"+ii+",if(substring(a.vchdate,9)='00','',debit"+key+") debit"+key+",";
						sqlHead1 += "汇率,"+value+",";
						
						pp.addColumn("汇率", "debitrate"+ii,"showMoney");
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
						sql += "if(substring(a.vchdate,9)='00','',creditrate"+ii+") creditrate"+ii+",if(substring(a.vchdate,9)='00','',credit"+key+") credit"+key+",";
						sqlHead2 += "汇率,"+value+",";
						
						pp.addColumn("汇率", "creditrate"+ii,"showMoney");
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
						sql += "abs(dRemain"+key+") dRemain"+key+",";
						sqlHead3 += value+",";
						
						pp.addColumn(value, "dRemain"+key,"showMoney");
						ii ++;
					}
					sql +="ABS(dateRemain) dateRemain,a.subjectnames  " +
					"from (" +
					"	select a.*,group_concat(distinct "+subjectnameSql+") subjectnames   from (" +
					"		select *,IF(credit>0,1,-1) AS opt from `"+TabName+"` where 1=1 ${where} order by id ${LIMIT}" +
					"	) a left join c_subjectentry b " +
					"	on 1=1 " +
					"	AND SUBSTRING(b.VchDate,1,4)*12 + SUBSTRING(b.VchDate,6,2) >= " + (Integer.parseInt(BeginYear) * 12 + Integer.parseInt(BeginDate)) +  
					"	AND SUBSTRING(b.VchDate,1,4)*12 + SUBSTRING(b.VchDate,6,2) <= " + (Integer.parseInt(EndYear) * 12 + Integer.parseInt(EndDate)) + 
					"	AND a.voucherid=b.voucherid " +
					"	AND a.opt = IF(b.Dirction*b.occurvalue<0,-1,1) " +
					sqlwhere + 
					"	group by a.id  " +
					") a " +

					"left join (select distinct vchid from z_voucherspotcheck  where projectid='" +projectid + "' and createor='" + user +"'   )  y on a.voucherid=y.vchid  " +
					"left join (select distinct vchid from z_question  where projectid='" +projectid + "' and createor='" + user +"'   )  z on a.voucherid=z.vchid  " +
					"left join (select distinct vchid from z_taxcheck  where projectid='" +projectid + "' and createor='" + user +"'   )  t on a.autoid=t.vchid " +
					"where 1=1 AND NOT (SUBSTRING(a.vchdate,9) = '00' AND vchmonth <> '01' AND id <> 1) ";
					
					sqlHead3 +="本位币余额";
					TableHead +="余额{"+sqlHead3+"}";
					
					pp.addColumn("本位币余额", "dateRemain","showMoney");
					
					pp.addSqlWhere("where", "${where}");
					pp.addInputValue("allOpt");
					pp.addInputValue("allType");
					pp.addInputValue("moneyChao");
					pp.addInputValue("TabName");
					
					pp.setTableHead(TableHead1 + TableHead);
					pp.setFixedHeader(true) ;
					pp.setLimitByOwnEnable(true);		
					pp.setCountsql("select count(*) as datagrid_count from `" + TabName + "` where 1=1 ${where} ");
					pp.setEnableCountTr(false);
					
					String TName = "";
					if(!"".equals(acc)) TName = new ASTextKey(conn).TextCustomerName(acc.substring(6));
					
					pp.setPrintEnable(true);
					pp.setPrintTitle(TName + "      科目明细账查询");
					pp.setPrintCharColumn("1`2`3`4`5") ;
					
					pp.setPrintPoms("核算名称："+full+"　　　　　　年度："+acc.substring(6));
					
					pp.setPrintTableHead(TableHead);
					
					pp.setSQL(sql);
					request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
									
					modelAndView.addObject("DataGrid", pp.getTableID());
					
				}
			}
		
			modelAndView.addObject("AssItem", AssItem);
			modelAndView.addObject("html", html);
		} catch (Exception e) {
			e.printStackTrace();
			 throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	public ModelAndView bthreelist(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ModelAndView modelAndView = new ModelAndView(_strBList);
		Connection conn=null;
		try {
			ASFuntion CHF=new ASFuntion();
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			
			String html = CHF.showNull(request.getParameter("html"));	//用于标志要不要用setTdtoValue　1为要
			
			String user=CHF.showNull(userSession.getUserId());
			String projectid=CHF.showNull(userSession.getCurProjectId());
			if("".equals(projectid)) projectid = "-1";
			
			String TabName = CHF.showNull(request.getParameter("TabName"));
			String acc = CHF.showNull(request.getParameter("AccPackageID"));
			
			String AssItemID = CHF.showNull(request.getParameter("AssItemID"));
			String SubjectID = CHF.showNull(request.getParameter("SubjectID"));
			
			String toSubjects = CHF.showNull(request.getParameter("toSubjects"));//显示对方科目
			String sqlwhere = "";
			if(!toSubjects.equals("1")){
				sqlwhere = " and  1=2 ";
			}
			String toLevel1 = CHF.showNull(request.getParameter("toLevel1"));//对方科目显示的级数
			String subjectnameSql = "";
			if(toLevel1.equals("1")){
				subjectnameSql = "if(locate('/',b.subjectfullname1)=0,b.subjectfullname1,substr(b.subjectfullname1,1,locate('/',b.subjectfullname1)-1))";
			}else{
//				subjectnameSql =  "if(b.subjectname1!=b.subjectfullname1 && concat(if(locate('/',b.subjectfullname1)=0,b.subjectfullname1,substr(b.subjectfullname1,1,locate('/',b.subjectfullname1)-1)),'/',b.subjectname1)!=b.subjectfullname1 \n"
//					 +" ,concat(if(locate('/',b.subjectfullname1)=0,b.subjectfullname1,substr(b.subjectfullname1,1,locate('/',b.subjectfullname1)-1)),'/../',b.subjectname1) \n"
//					 +" ,b.subjectfullname1) ";
				subjectnameSql =  " b.subjectfullname1 ";
			}
			
			String AssItem = CHF.showNull((String)request.getAttribute("AssItem"));
			if("".equals(AssItem)) AssItem = CHF.showNull(request.getParameter("AssItem"));
			
			String BeginYear = CHF.showNull(request.getParameter("BeginYear"));
			if("".equals(BeginYear)){
				BeginYear = CHF.showNull(userSession.getCurChoiceBeginYear());
				if("".equals(BeginYear)){
					BeginYear = CHF.showNull(userSession.getCurProjectBeginYear());
				}
			}
			if("".equals(BeginYear)){
				BeginYear = acc.substring(6);
			}
			
			String EndYear = CHF.showNull(request.getParameter("EndYear"));
			if("".equals(EndYear)){
				EndYear = CHF.showNull(userSession.getCurChoiceEndYear());
				if("".equals(EndYear)){
					EndYear = CHF.showNull(userSession.getCurProjectEndYear());
				}
			}
			if("".equals(EndYear)){
				EndYear = acc.substring(6);
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
			
			conn = new DBConnect().getConnect(acc.substring(0,6));
			Assitem assitem = new Assitem(conn);
			if(!"".equals(AssItem)){
				String full = assitem.getAssTotalName(acc,"".equals(AssItem)?AssItemID:AssItem);
				String sTable = "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tr><td>核算名称：<font color=\"blue\">"+full+"</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;年度：<font color=\"blue\">"+acc.substring(6)+"</font></td></tr></table>";
				modelAndView.addObject("TabName", TabName);			
				modelAndView.addObject("sTable", sTable);
				
				if(!"".equals(AssItem)) AssItemID = AssItem;
				
				Map map = assitem.AssitemUnitName(acc, SubjectID, AssItemID);
				if(!assitem.ExistsTable(TabName)){
					assitem.CreateTable(TabName,map);				
					assitem.DataToTable(map,TabName, user, projectid, acc.substring(0,6), SubjectID, AssItemID, BeginYear, BeginDate, EndYear, EndDate,2);
				}
				
				if(!"".equals(AssItemID) && !"".equals(SubjectID)){
					DataGridProperty pp = new DataGridProperty(){
						public void onSearch(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws Exception {
							String TabName = this.getRequestValue("TabName");

							String allOpt = this.getRequestValue("allOpt");
							String moneyChao = this.getRequestValue("moneyChao");
							
							if(moneyChao == null || "".equals(moneyChao.trim())){
								allOpt = "0";
							}
							
							String strWhere = "";
							String allType = this.getRequestValue("allType");
							if("".equals(allType)) allType = "1";
							if("1".equals(allType)){
								strWhere = " ABS(dateRemain) ";
							}else if("2".equals(allType)){
								strWhere = " ABS(debit) ";
							}else if("3".equals(allType)){
								strWhere = " ABS(credit) ";
							}
							
							String where = "";
							if("1".equals(allOpt)){
								where = " and substring(vchdate,9) not in ('00','97','98') " +
								" and " + strWhere + " >" + moneyChao ;
							}else if("2".equals(allOpt)){
								where = " and substring(vchdate,9) not in ('00','97','98') " +
								" and " + strWhere + " >" + moneyChao +
								" and id in (" +
								" 	select max(id) from `"+TabName+"` " +
								"	where 1=1 and substring(vchdate,9) not in ('00','97','98')" +
								"	group by vchdate" +
								" ) ";
							}else if("3".equals(allOpt)){
								where = " and substring(vchdate,9) not in ('00','97','98') " +
								" and " + strWhere + " >" + moneyChao +
								" and id in (" +
								" 	select max(id) from `"+TabName+"` " +
								"	where 1=1 and substring(vchdate,9) not in ('00','97','98')" +
								"	group by vchmonth" +
								" ) ";
							}


							this.setOrAddRequestValue("where",where);
						}
					};
					
					if("1".equals(html)){
						pp.setTdtoValue("<a href='St://${value}'>${tdvalue}</a>");	
					}
					
					pp.setTableID("AssItem"+assitem.getRandom());
					pp.setCustomerId(acc.substring(0,6));
					pp.setPageSize_CH(200);
					pp.setWhichFieldIsValue(3);
					
					pp.setOrderBy_CH("id");
					pp.setDirection("asc");
					pp.setCancelOrderby(true);
									
					
					pp.setTrActionProperty(true);
					pp.setTrAction(" voucherid='${voucherid}' AccPackageID='"+acc+"' AssitemID='${assitemid}' VchDate='${vchdate}' TypeId='${typeid}' OldVoucherId='${oldvoucherid}' autoid='${autoid}' style='cursor:hand;' onDBLclick='goSort();'");
					
					String TableHead = "",TableHead1 = "";
					String sql = "";
					
					TableHead = "凭证日期,字,号,摘要,核算编号,${subjectid},";
					
	//				sql = "select id,vchmonth vchmonth1,autoid,voucherid,p1,p2,IF(substring(vchdate,9)='00',vchyear,'') vchyear,IF(substring(vchdate,9)='00',vchmonth,'') vchmonth,IF(substring(vchdate,9)='00' or substring(vchdate,9)='97' or substring(vchdate,9)='98','',vchdate) vchdate,typeid,oldvoucherid,assitemid,summary, ";
					sql = "select id,a.vchmonth vchmonth1,autoid,a.voucherid," +
					"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else y.vchid end p1," +
					"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else z.vchid end p2," +
					"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else t.vchid end p3," +
					"IF(substring(a.vchdate,9)='00',vchyear,'') vchyear,IF(substring(a.vchdate,9)='00',vchmonth,'') vchmonth," +
					"case substring(a.vchdate,9) when '00' then '' when '97' then '' when '98' then '' else a.vchdate end vchdate," +
					"a.typeid,a.oldvoucherid,a.assitemid,summary, ";
					String setColumnWidth = "";
					if(acc.equals(userSession.getCurAccPackageId()) && userSession.getCurProjectId()!=null && !"1".equals(html)){
//						TableHead = "抽,月,凭证日期,字,号,核算编号,${subjectid},摘要,";
						TableHead1 = "抽,";
						pp.addColumn("抽","p1",FormatType.showTakeOut );
//						pp.addColumn("疑","p2",FormatType.showDoubt );	
						setColumnWidth = "4,";
						ProjectService projectService = new ProjectService(conn);
						if("税务审计".equals(projectService.getProjectById(projectid).getAuditPara())){
							pp.addColumn("税", "p3", "showTaskTax");
//							TableHead = "抽,疑,税,月,凭证日期,字,号,核算编号,${subjectid},摘要,";
							TableHead1 = "抽,税,";
						}
					}
					
					
					setColumnWidth += "8,8,5,25,10";
					
	//				pp.addColumn("年", "vchyear","showCenter");
//					pp.addColumn("月", "vchmonth","showCenter");
					pp.addColumn("凭证日期", "vchdate","showCenter");
					pp.addColumn("字", "typeid","showCenter");
					pp.addColumn("号", "oldvoucherid","showCenter");
					pp.addColumn("摘要", "summary");
					pp.addColumn("核算编号", "assitemid");
					if(toSubjects.equals("1")){
						pp.addColumn("对方科目", "subjectnames");
						TableHead = TableHead.replaceAll("\\$\\{subjectid\\}", "对方科目");
						setColumnWidth += ",15";
					}else{
						TableHead = TableHead.replaceAll("\\$\\{subjectid\\},","");
					}
					pp.setColumnWidth(setColumnWidth);
					
					Set coll = map.keySet();
					int ii =1;
					
					String sqlHead1 = "";
					String sqlHead2 = "";
					String sqlHead3 = "";
					
					for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) map.get(key);
						sql += "if(substring(a.vchdate,9)='00','',debitrate"+ii+") debitrate"+ii+",if(substring(a.vchdate,9)='00','',debit"+key+") debit"+key+",";
						sqlHead1 += "单价,"+value+",";
						
						pp.addColumn("单价", "debitrate"+ii,"showMoney");
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
						sql += "if(substring(a.vchdate,9)='00','',creditrate"+ii+") creditrate"+ii+",if(substring(a.vchdate,9)='00','',credit"+key+") credit"+key+",";
						sqlHead2 += "单价,"+value+",";
						
						pp.addColumn("单价", "creditrate"+ii,"showMoney");
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
						sql += "abs(dRemain"+key+") dRemain"+key+",";
						sqlHead3 += value+",";
						
						pp.addColumn(value, "dRemain"+key,"showMoney");
						ii ++;
					}
					sql +="ABS(dateRemain) dateRemain,a.subjectnames  " +
					"from (" +
					"	select a.*,group_concat(distinct "+subjectnameSql+") subjectnames   from (" +
					"		select *,IF(credit>0,1,-1) AS opt from `"+TabName+"` where 1=1 ${where} order by id ${LIMIT}" +
					"	) a left join c_subjectentry b " +
					"	on 1=1 " +
					"	AND SUBSTRING(b.VchDate,1,4)*12 + SUBSTRING(b.VchDate,6,2) >= " + (Integer.parseInt(BeginYear) * 12 + Integer.parseInt(BeginDate)) +  
					"	AND SUBSTRING(b.VchDate,1,4)*12 + SUBSTRING(b.VchDate,6,2) <= " + (Integer.parseInt(EndYear) * 12 + Integer.parseInt(EndDate)) + 
					"	AND a.voucherid=b.voucherid " +
					"	AND a.opt = IF(b.Dirction*b.occurvalue<0,-1,1) " +
					sqlwhere + 
					"	group by a.id  " +
					") a " +

					"left join (select distinct vchid from z_voucherspotcheck  where projectid='" +projectid + "' and createor='" + user +"'   ) y on a.voucherid=y.vchid " +
					"left join (select distinct vchid from z_question  where projectid='" +projectid + "' and createor='" + user +"'   )  z on a.voucherid=z.vchid  " +
					"left join (select distinct vchid from z_taxcheck  where projectid='" +projectid + "' and createor='" + user +"'   )  t on a.autoid=t.vchid " +
					"where 1=1 AND NOT (SUBSTRING(a.vchdate,9) = '00' AND vchmonth <> '01' AND id <> 1) ";
					
					sqlHead3 +="本位币余额";
					TableHead +="余额{"+sqlHead3+"}";
					
					pp.addColumn("本位币余额", "dateRemain","showMoney");
					
					pp.addSqlWhere("where", "${where}");
					pp.addInputValue("allOpt");
					pp.addInputValue("allType");
					pp.addInputValue("moneyChao");
					pp.addInputValue("TabName");
					
					pp.setTableHead(TableHead1 + TableHead);
					
					String TName = "";
					if(!"".equals(acc)) TName = new ASTextKey(conn).TextCustomerName(acc.substring(6));
					
					pp.setPrintEnable(true);
					pp.setPrintTitle(TName + "      科目明细账查询");
					pp.setPrintCharColumn("1`2`3`4`5") ;
					
					pp.setPrintPoms("核算名称："+full+"　　　　　　年度："+acc.substring(6));
					
					pp.setPrintTableHead(TableHead);
					
					pp.setLimitByOwnEnable(true);		
					pp.setCountsql("select count(*) as datagrid_count from `" + TabName + "` where 1=1 ${where} ");
					pp.setEnableCountTr(false);
					pp.setFixedHeader(true) ;
					pp.setSQL(sql);
					request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
									
					modelAndView.addObject("DataGrid", pp.getTableID());
					
				}
			}
			modelAndView.addObject("AssItem", AssItem);
			modelAndView.addObject("html", html);
		} catch (Exception e) {
			e.printStackTrace();
			 throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	
	public ModelAndView bfourlist(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ModelAndView modelAndView = new ModelAndView(_strBList);
		Connection conn=null;
		try {
			ASFuntion CHF=new ASFuntion();
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			
			String html = CHF.showNull(request.getParameter("html"));	//用于标志要不要用setTdtoValue　1为要
			
			String user=CHF.showNull(userSession.getUserId());
			String projectid=CHF.showNull(userSession.getCurProjectId());
			if("".equals(projectid)) projectid = "-1";
			
			String TabName = CHF.showNull(request.getParameter("TabName"));
			String acc = CHF.showNull(request.getParameter("AccPackageID"));
			
			String AssItemID = CHF.showNull(request.getParameter("AssItemID"));
			String SubjectID = CHF.showNull(request.getParameter("SubjectID"));
			
			String toSubjects = CHF.showNull(request.getParameter("toSubjects"));//显示对方科目
			String sqlwhere = "";
			if(!toSubjects.equals("1")){
				sqlwhere = " and  1=2 ";
			}
			String toLevel1 = CHF.showNull(request.getParameter("toLevel1"));//对方科目显示的级数
			String subjectnameSql = "";
			if(toLevel1.equals("1")){
				subjectnameSql = "if(locate('/',b.subjectfullname1)=0,b.subjectfullname1,substr(b.subjectfullname1,1,locate('/',b.subjectfullname1)-1))";
			}else{
//				subjectnameSql =  "if(b.subjectname1!=b.subjectfullname1 && concat(if(locate('/',b.subjectfullname1)=0,b.subjectfullname1,substr(b.subjectfullname1,1,locate('/',b.subjectfullname1)-1)),'/',b.subjectname1)!=b.subjectfullname1 \n"
//					 +" ,concat(if(locate('/',b.subjectfullname1)=0,b.subjectfullname1,substr(b.subjectfullname1,1,locate('/',b.subjectfullname1)-1)),'/../',b.subjectname1) \n"
//					 +" ,b.subjectfullname1) ";
				subjectnameSql =  " b.subjectfullname1 ";
			}
			
			String AssItem = CHF.showNull((String)request.getAttribute("AssItem"));
			if("".equals(AssItem)) AssItem = CHF.showNull(request.getParameter("AssItem"));
			
			String BeginYear = CHF.showNull(request.getParameter("BeginYear"));
			if("".equals(BeginYear)){
				BeginYear = CHF.showNull(userSession.getCurChoiceBeginYear());
				if("".equals(BeginYear)){
					BeginYear = CHF.showNull(userSession.getCurProjectBeginYear());
				}
			}
			if("".equals(BeginYear)){
				BeginYear = acc.substring(6);
			}
			
			String EndYear = CHF.showNull(request.getParameter("EndYear"));
			if("".equals(EndYear)){
				EndYear = CHF.showNull(userSession.getCurChoiceEndYear());
				if("".equals(EndYear)){
					EndYear = CHF.showNull(userSession.getCurProjectEndYear());
				}
			}
			if("".equals(EndYear)){
				EndYear = acc.substring(6);
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
			
			conn = new DBConnect().getConnect(acc.substring(0,6));
			Assitem assitem = new Assitem(conn);
			if(!"".equals(AssItem)){
				String full = assitem.getAssTotalName(acc,"".equals(AssItem)?AssItemID:AssItem);
				String sTable = "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tr><td>核算名称：<font color=\"blue\">"+full+"</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;年度：<font color=\"blue\">"+acc.substring(6)+"</font></td></tr></table>";
				modelAndView.addObject("TabName", TabName);			
				modelAndView.addObject("sTable", sTable);
				
				if(!"".equals(AssItem)) AssItemID = AssItem;
				
				Map Cmap = assitem.AssitemCurrency(acc, SubjectID, AssItemID);
				Map Umap = assitem.AssitemUnitName(acc, SubjectID, AssItemID);
				if(!assitem.ExistsTable(TabName)){
					assitem.CreateTable(TabName,Cmap,Umap);
					assitem.DataToTable(Cmap,Umap,TabName, user, projectid, acc.substring(0,6), SubjectID, AssItemID, BeginYear, BeginDate, EndYear, EndDate);				
				}
				
				/**
				 * DataGrid
				 */
				if(!"".equals(SubjectID)){
					DataGridProperty pp = new DataGridProperty(){
						public void onSearch(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws Exception {
							String TabName = this.getRequestValue("TabName");

							String allOpt = this.getRequestValue("allOpt");
							String moneyChao = this.getRequestValue("moneyChao");
							
							if(moneyChao == null || "".equals(moneyChao.trim())){
								allOpt = "0";
							}
							
							String strWhere = "";
							String allType = this.getRequestValue("allType");
							if("".equals(allType)) allType = "1";
							if("1".equals(allType)){
								strWhere = " ABS(dateRemain) ";
							}else if("2".equals(allType)){
								strWhere = " ABS(debit) ";
							}else if("3".equals(allType)){
								strWhere = " ABS(credit) ";
							}
							
							String where = "";
							if("1".equals(allOpt)){
								where = " and substring(vchdate,9) not in ('00','97','98') " +
								" and " + strWhere + " >" + moneyChao ;
							}else if("2".equals(allOpt)){
								where = " and substring(vchdate,9) not in ('00','97','98') " +
								" and " + strWhere + " >" + moneyChao +
								" and id in (" +
								" 	select max(id) from `"+TabName+"` " +
								"	where 1=1 and substring(vchdate,9) not in ('00','97','98')" +
								"	group by vchdate" +
								" ) ";
							}else if("3".equals(allOpt)){
								where = " and substring(vchdate,9) not in ('00','97','98') " +
								" and " + strWhere + " >" + moneyChao +
								" and id in (" +
								" 	select max(id) from `"+TabName+"` " +
								"	where 1=1 and substring(vchdate,9) not in ('00','97','98')" +
								"	group by vchmonth" +
								" ) ";
							}


							this.setOrAddRequestValue("where",where);
						}
					};
					
					if("1".equals(html)){
						pp.setTdtoValue("<a href='St://${value}'>${tdvalue}</a>");	
					}
					
					pp.setTableID("AssItem"+assitem.getRandom());
					pp.setCustomerId(acc.substring(0,6));
					pp.setPageSize_CH(200);
					pp.setWhichFieldIsValue(3);
					pp.setOrderBy_CH("id");
					pp.setDirection("asc");
					pp.setCancelOrderby(true);
					
					pp.setTrActionProperty(true);
					pp.setTrAction(" voucherid='${voucherid}' AccPackageID='"+acc+"' VchDate='${vchdate}' TypeId='${typeid}' OldVoucherId='${oldvoucherid}' style='cursor:hand;' onDBLclick='goSort();'");
					
					String TableHead = "",TableHead1 = "";
					String sql = "";
						
					TableHead = "凭证日期,字,号,摘要,核算编号,${subjectid},";
					
	//				sql = "select id,vchmonth vchmonth1,autoid,voucherid,p1,p2,IF(substring(vchdate,9)='00',vchyear,'') vchyear,IF(substring(vchdate,9)='00',vchmonth,'') vchmonth,IF(substring(vchdate,9)='00' or substring(vchdate,9)='97' or substring(vchdate,9)='98','',vchdate) vchdate,typeid,oldvoucherid,assitemid,summary, ";
					sql = "select id,a.vchmonth vchmonth1,autoid,a.voucherid," +
					"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else y.vchid end p1," +
					"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else z.vchid end p2," +
					"case substring(a.vchdate,9) when '00' then 0 when '97' then 0 when '98' then 0 else t.vchid end p3," +
					"IF(substring(a.vchdate,9)='00',vchyear,'') vchyear,IF(substring(a.vchdate,9)='00',vchmonth,'') vchmonth," +
					"case substring(a.vchdate,9) when '00' then '' when '97' then '' when '98' then '' else a.vchdate end vchdate," +
					"a.typeid,a.oldvoucherid,a.assitemid,summary, ";
					String setColumnWidth = "";
					if(acc.equals(userSession.getCurAccPackageId()) && userSession.getCurProjectId()!=null && !"1".equals(html)){
//						TableHead = "抽,月,凭证日期,字,号,核算编号,${subjectid},摘要,";
						TableHead1 = "抽,";
						pp.addColumn("抽","p1",FormatType.showTakeOut );
						setColumnWidth = "4,";
//						pp.addColumn("疑","p2",FormatType.showDoubt );	
						ProjectService projectService = new ProjectService(conn);
						if("税务审计".equals(projectService.getProjectById(projectid).getAuditPara())){
							pp.addColumn("税", "p3", "showTaskTax");
//							TableHead = "抽,疑,税,月,凭证日期,字,号,核算编号,${subjectid},摘要,";
							TableHead1 = "抽,税,";
						}
					}
					
					
					setColumnWidth += "8,8,5,25,10";
					
									
					
	//				pp.addColumn("年", "vchyear","showCenter");
//					pp.addColumn("月", "vchmonth","showCenter");
					pp.addColumn("凭证日期", "vchdate","showCenter");
					pp.addColumn("字", "typeid","showCenter");
					pp.addColumn("号", "oldvoucherid","showCenter");
					pp.addColumn("摘要", "summary");
					pp.addColumn("核算编号", "assitemid");
					
					if(toSubjects.equals("1")){
						pp.addColumn("对方科目", "subjectnames");
						TableHead = TableHead.replaceAll("\\$\\{subjectid\\}", "对方科目");
						setColumnWidth += ",15";
					}else{
						TableHead = TableHead.replaceAll("\\$\\{subjectid\\},","");
					}
					pp.setColumnWidth(setColumnWidth);
					
					Set Ucoll = Umap.keySet();
					Set Ccoll = Cmap.keySet();
					int ii =1;
					
					String sqlHead1 = "";
					String sqlHead2 = "";
					String sqlHead3 = "";
					
					for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) Umap.get(key);
						sql += "if(substring(a.vchdate,9)='00','',debitPrice"+ii+") debitPrice"+ii+",if(substring(a.vchdate,9)='00','',debit"+key+") debit"+key+",";
						sqlHead1 += "单价,"+value+",";
						
						pp.addColumn("单价", "debitPrice"+ii,"showMoney");
						pp.addColumn(value, "debit"+key,"showMoney");
						ii ++;
					}
					
					ii =1;
					for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) Cmap.get(key);
						sql += "if(substring(a.vchdate,9)='00','',debitrate"+ii+") debitrate"+ii+",if(substring(a.vchdate,9)='00','',debit"+key+") debit"+key+",";
						sqlHead1 += "汇率,"+value+",";
						
						pp.addColumn("汇率", "debitrate"+ii,"showMoney");
						pp.addColumn(value, "debit"+key,"showMoney");
						ii ++;
					}
					sql += "if(substring(a.vchdate,9)='00','',debit) debit,";
					sqlHead1 += "借方本位币";
					TableHead += "借方{"+sqlHead1+"},";
					
					pp.addColumn("借方本位币", "debit","showMoney");
					
					ii=1;
					for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) Umap.get(key);
						sql += "if(substring(a.vchdate,9)='00','',creditPrice"+ii+") creditPrice"+ii+",if(substring(a.vchdate,9)='00','',credit"+key+") credit"+key+",";
						sqlHead2 += "单价,"+value+",";
						
						pp.addColumn("单价", "creditPrice"+ii,"showMoney");
						pp.addColumn(value, "credit"+key,"showMoney");
						ii ++;
					}
					
					ii=1;
					for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) Cmap.get(key);
						sql += "if(substring(a.vchdate,9)='00','',creditrate"+ii+") creditrate"+ii+",if(substring(a.vchdate,9)='00','',credit"+key+") credit"+key+",";
						sqlHead2 += "汇率,"+value+",";
						
						pp.addColumn("汇率", "creditrate"+ii,"showMoney");
						pp.addColumn(value, "credit"+key,"showMoney");
						ii ++;
						
					}
					
					sql += "if(substring(a.vchdate,9)='00','',credit) credit,IF(dateremain>0,'借',if(dateremain<0,'贷','平')) rec,";
					sqlHead2 += "贷方本位币";
					TableHead += "贷方{"+sqlHead2+"},方向,";
					
					pp.addColumn("贷方本位币", "credit","showMoney");
					pp.addColumn("方向", "rec","showCenter");
					
					ii=1;
					for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) Umap.get(key);
						sql += "abs(dRemain"+key+") dRemain"+key+",";
						sqlHead3 += value+",";
						
						pp.addColumn(value, "dRemain"+key,"showMoney");
						ii ++;
					}
					
					ii=1;
					for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						String value = (String) Cmap.get(key);
						sql += "abs(dRemain"+key+") dRemain"+key+",";
						sqlHead3 += value+",";
						
						pp.addColumn(value, "dRemain"+key,"showMoney");
						ii ++;
					}
					
					sql +="ABS(dateRemain) dateRemain,a.subjectnames  " +
					"from (" +
					"	select a.*,group_concat(distinct "+subjectnameSql+") subjectnames   from (" +
					"		select *,IF(credit>0,1,-1) AS opt from `"+TabName+"` where 1=1 ${where} order by id ${LIMIT}" +
					"	) a left join c_subjectentry b " +
					"	on 1=1 " +
					"	AND SUBSTRING(b.VchDate,1,4)*12 + SUBSTRING(b.VchDate,6,2) >= " + (Integer.parseInt(BeginYear) * 12 + Integer.parseInt(BeginDate)) +  
					"	AND SUBSTRING(b.VchDate,1,4)*12 + SUBSTRING(b.VchDate,6,2) <= " + (Integer.parseInt(EndYear) * 12 + Integer.parseInt(EndDate)) + 
					"	AND a.voucherid=b.voucherid " +
					"	AND a.opt = IF(b.Dirction*b.occurvalue<0,-1,1) " +
					sqlwhere + 
					"	group by a.id  " +
					") a " +

					"left join (select distinct vchid from z_voucherspotcheck  where projectid='" +projectid + "' and createor='" + user +"'   ) y on a.voucherid=y.vchid " +
					"left join (select distinct vchid from z_question  where projectid='" +projectid + "' and createor='" + user +"'   )  z on a.voucherid=z.vchid  " +
					"left join z_taxcheck t on a.autoid=t.vchid and t.projectid='"+projectid+"' and t.createor='"+user+"'  " +
					"where 1=1 AND NOT (SUBSTRING(a.vchdate,9) = '00' AND vchmonth <> '01' AND id <> 1) ";
					
					sqlHead3 +="本位币余额";
					TableHead +="余额{"+sqlHead3+"}";
					
					pp.addColumn("本位币余额", "dateRemain","showMoney");
					
					pp.setTableHead(TableHead1 + TableHead);
					
					String TName = "";
					if(!"".equals(acc)) TName = new ASTextKey(conn).TextCustomerName(acc.substring(6));
					
					pp.setPrintEnable(true);
					pp.setPrintTitle(TName + "      科目明细账查询");
					pp.setPrintCharColumn("1`2`3`4`5") ;
					
					pp.setPrintPoms("核算名称："+full+"　　　　　　年度："+acc.substring(6));
					
					pp.setPrintTableHead(TableHead);
					
					pp.addSqlWhere("where", "${where}");
					pp.addInputValue("allOpt");
					pp.addInputValue("allType");
					pp.addInputValue("moneyChao");
					pp.addInputValue("TabName");
					
					pp.setLimitByOwnEnable(true);		
					pp.setCountsql("select count(*) as datagrid_count from `" + TabName + "` where 1=1 ${where} ");
					pp.setEnableCountTr(false);
					pp.setFixedHeader(true) ;
					pp.setSQL(sql);
					request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
					
					modelAndView.addObject("DataGrid", pp.getTableID());
					
					
				}
			}
			modelAndView.addObject("AssItem", AssItem);
			modelAndView.addObject("html", html);
		} catch (Exception e) {
			e.printStackTrace();
			 throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	public ModelAndView blist(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ModelAndView modelAndView = new ModelAndView(_strBList);
		Connection conn=null;
		try {
			ASFuntion CHF=new ASFuntion();
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			
			String html = CHF.showNull(request.getParameter("html"));	//用于标志要不要用setTdtoValue　1为要
			
			String user=CHF.showNull(userSession.getUserId());
			String projectid=CHF.showNull(userSession.getCurProjectId());
			if("".equals(projectid)) projectid = "-1";
			
			String TabName = CHF.showNull(request.getParameter("TabName"));
			String acc = CHF.showNull(request.getParameter("AccPackageID"));
			
			String AssItemID = CHF.showNull(request.getParameter("AssItemID"));
			String SubjectID = CHF.showNull(request.getParameter("SubjectID"));
			
			String AssItem = CHF.showNull(request.getParameter("AssItem"));
			String BeginDate = CHF.showNull(request.getParameter("BeginDate"));
			String EndDate = CHF.showNull(request.getParameter("EndDate"));
			
			conn = new DBConnect().getConnect(acc.substring(0,6));
			Assitem assitem = new Assitem(conn);
			
			String full = assitem.getAssTotalName(acc,"".equals(AssItem)?AssItemID:AssItem);
			String sTable = "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tr><td>核算名称：<font color=\"blue\">"+full+"</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;年度：<font color=\"blue\">"+acc.substring(6)+"</font></td></tr></table>";
			modelAndView.addObject("TabName", TabName);			
			modelAndView.addObject("sTable", sTable);
			
			if(!assitem.ExistsTable(TabName)){
				assitem.CreateTable(TabName);
				assitem.DataToTable(TabName,user,projectid,acc,AssItemID,SubjectID,AssItem,BeginDate,EndDate);
			}
			
			if(!"".equals(AssItemID) && !"".equals(SubjectID)){
				DataGridProperty pp = new DataGridProperty();
				
				if("1".equals(html)){
					pp.setTdtoValue("<a href='St://${value}'>${tdvalue}</a>");	
				}
				
				pp.setTableID("AssItem"+assitem.getRandom());
				pp.setCustomerId(acc.substring(0,6));
				pp.setPageSize_CH(200);
				pp.setWhichFieldIsValue(4);
				
				pp.setOrderBy_CH("id");
				pp.setDirection("asc");
				pp.setCancelOrderby(true);
				
				String sql = "select id,vchmonth vchmonth1,voucherid,autoid,p1,p2,IF(substring(a.vchdate,9)='00',vchmonth,'') vchmonth,IF(substring(a.vchdate,9)='00' or substring(a.vchdate,9)='97' or substring(a.vchdate,9)='98','',a.vchdate) vchdate,typeid,oldvoucherid,assitemid,summary,if(substring(a.vchdate,9)='00','',debit)  debit,if(substring(a.vchdate,9)='00','',credit) credit,IF(dateremain>0,'借',if(dateremain<0,'贷','平')) rec,ABS(dateremain) dateremain from `"+TabName+"` a ";
				
				pp.setTrActionProperty(true);
				pp.setTrAction(" voucherid='${voucherid}' AccPackageID='"+acc+"' AssitemID='${assitemid}' VchDate='${vchdate}' TypeId='${typeid}' OldVoucherId='${oldvoucherid}' autoid='${autoid}' style='cursor:hand;' onDBLclick='goSort();'");
				String setColumnWidth = "";
				if(acc.equals(userSession.getCurAccPackageId()) && userSession.getCurProjectId()!=null && !"1".equals(html)){
					pp.addColumn("抽","p1",FormatType.showTakeOut );
//					pp.addColumn("疑","p2",FormatType.showDoubt );		
				}
//				pp.addColumn("年", "vchyear","showCenter");
				pp.addColumn("月", "vchmonth","showCenter");
				pp.addColumn("凭证日期", "vchdate","showCenter");
				pp.addColumn("字", "typeid","showCenter");
				pp.addColumn("号", "oldvoucherid","showCenter");
				pp.addColumn("核算编号", "assitemid");
				pp.addColumn("摘要", "summary");
//				pp.addColumn("摘要", "summary",null,null,"<td style=\"word-break: keep-all;\">${value}</td>");
				pp.addColumn("借方发生额", "debit","showMoney");
				pp.addColumn("贷方发生额", "credit","showMoney");
				pp.addColumn("方向", "rec","showCenter");
				pp.addColumn("余　额", "dateremain","showMoney");
				
				pp.setSQL(sql);
				request.getSession().setAttribute(DataGrid.sessionPre+pp.getTableID(),pp);
				
				modelAndView.addObject("DataGrid", pp.getTableID());
				
			}			
			
			modelAndView.addObject("html", html);
		} catch (Exception e) {
			Debug.print(Debug.iError, "核算项目明细账查询失败！", e);
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		return modelAndView;
	}
	
	public ModelAndView btext(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		Connection conn=null;
		HashMap mapResult = new HashMap();
		try {
			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
			ASFuntion CHF=new ASFuntion();
			
			String user=CHF.showNull(userSession.getUserId());
			String projectid=CHF.showNull(userSession.getCurProjectId());
			if("".equals(projectid)) projectid = "-1";
			
			String TabName = CHF.showNull(request.getParameter("TabName"));
			String acc = CHF.showNull(request.getParameter("AccPackageID"));
			
			String AssItemID = CHF.showNull(request.getParameter("AssItemID"));
			String SubjectID = CHF.showNull(request.getParameter("SubjectID"));
			
			String AssItem = CHF.showNull(request.getParameter("AssItem"));
			String BeginDate = CHF.showNull(request.getParameter("BeginDate"));
			String EndDate = CHF.showNull(request.getParameter("EndDate"));

			String txtType = (String)CHF.showNull(request.getParameter("txtType"));
			String txtBegin =(String)CHF.showNull(request.getParameter("txtBegin"));
			String txtEnd = (String)CHF.showNull(request.getParameter("txtEnd"));
			
			String txtTable = (String)CHF.showNull(request.getParameter("txtTable"));
			
			String Currency = (String)CHF.showNull(request.getParameter("Currency"));
//			int result = subjectEntry.SubjectProperty(acc,SubjectID);
//			if("1".equals(Currency) && "".equals(sData)){
//				result = 0;
//			}
			
			String toSubjects = (String)CHF.showNull(request.getParameter("toSubjects"));
			
			String txtSQL1 = "";
			String txtSQL2 = "";
			txtType = "".equals(txtType)?"0":txtType; 
			switch(Integer.parseInt(txtType)){
			case 0:
				break;
			case 1:
			case 2:
				txtSQL1 = " limit "+txtBegin+","+txtEnd;
				break;
			case 3:
				txtSQL2 = " and vchmonth1>="+txtBegin+" and vchmonth1<="+txtEnd+" ";
				break;
			}
			
			conn = new DBConnect().getConnect(acc.substring(0,6));
			
			DataGridProperty pp = (DataGridProperty)request.getSession().getAttribute(DataGrid.sessionPre+txtTable);
			//pp.columns, pp.tableHead
			
			PrintSetup printSetup = new PrintSetup(conn);	
			printSetup.setStrTitles(new String[]{new ASTextKey(conn).TextCustomerName(acc.substring(0,6))+" 核算明细帐"});
			
			
//			printSetup.setStrChineseTitles(new String[]{"月`凭证日期`字`号`核算编号`摘要`借方发生额`贷方发生额`方向`余　额"});
//			printSetup.setStrQuerySqls(new String[]{"select vchmonth,vchdate,typeid,oldvoucherid,assitemid,summary,debit,credit,rec,dateremain from ("+pp.getFinishSQLDeleteLimit()+") a where 1=1 "+txtSQL2+" order by id "+txtSQL1 });
			printSetup.setCharColumn(new String[]{"1`2`3`4`5`6"});
//			printSetup.setIColumnWidths(new int[]{4,13,18,5,24,72,22,20,4,23});
		//	printSetup.setVertical(true);	 
		    
			
			String full = new Assitem(conn).getAssTotalName(acc,"".equals(AssItem)?AssItemID:AssItem);
			
			List[] lists = new List[1];
			lists[0] = new ArrayList();  
			lists[0].add(0, "1");
			lists[0].add(1, "核算名称："+full+"　　　　　　年度："+acc.substring(6));
			lists[0].add(2, "9");
			lists[0].add(3, null);
			
			printSetup.setPoms(lists);
			
			String sRows = "";
			
			String [][] CTName = new String[1][2];
			String [][] ColName = new String[1][2];
			
			String sql = "select vchmonth,vchdate,typeid,oldvoucherid,assitemid,";
		

			Assitem assitem = new Assitem(conn);
			
			if(!"".equals(AssItem)) AssItemID = AssItem;
			
			Map Cmap = assitem.AssitemCurrency(acc, SubjectID, AssItemID);
			Map Umap = assitem.AssitemUnitName(acc, SubjectID, AssItemID);
			
			int result = assitem.AssitemProperty(acc, SubjectID, AssItemID);
			
			if("1".equals(toSubjects) && result!=4){
				sql += "replace(subjectnames,'<br>',','),";
			}
			sql += "summary,";
			
			if("1".equals(Currency)){
				result = 0;
			}
			
			switch(result){
			case 0:
				if("1".equals(toSubjects)){
					printSetup.setStrChineseTitles(new String[]{"月`凭证日期`字`号`核算编号`对方科目`摘要`借方发生额`贷方发生额`方向`余　额`"});
					printSetup.setIColumnWidths(new int[]{4,13,18,5,24,30,72,22,20,4,23});
				}else{
					printSetup.setStrChineseTitles(new String[]{"月`凭证日期`字`号`核算编号`摘要`借方发生额`贷方发生额`方向`余　额"});
					printSetup.setIColumnWidths(new int[]{4,13,18,5,24,72,22,20,4,23});
				}
				printSetup.setStrQuerySqls(new String[]{sql + "debit,credit,rec,dateremain from ("+pp.getFinishSQLDeleteLimit()+") a where 1=1 "+txtSQL2+" order by id "+txtSQL1 });					
				sRows = "$2:$4";
				break;
			case 1:					
				CTName[0][1] = new String();
				CTName[0][1] = ",";
				ColName[0][1] = new String();
				ColName[0][1]= ",";
				CTName[0][0] = new String();
				ColName[0][0] = new String();
				
				CTName[0][0] = "月,凭证日期,字,号,核算编号,";
				ColName[0][0] = "月,凭证日期,字,号,核算编号,";
				
				if("1".equals(toSubjects)){
					CTName[0][0] += "对方科目,";
					ColName[0][0] += "对方科目,";
				}
				CTName[0][0] += "摘要,";
				ColName[0][0] += "摘要,";
				
				Set coll = Cmap.keySet();
				int ii =1;
				for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) Cmap.get(key);
					sql += "debitrate"+ii+",debit"+key+",";
					CTName[0][0] += "借方,借方,";
					ColName[0][0] += "汇率,"+value+",";
					ii ++;
				}
				sql += "debit,";
				CTName[0][0] += "借方,";
				ColName[0][0] += "借方本位币,";
				
				ii=1;
				for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) Cmap.get(key);
					sql += "creditrate"+ii+",credit"+key+",";
					CTName[0][0] += "贷方,贷方,";
					ColName[0][0] += "汇率,"+value+",";
					ii ++;
				}
				sql += "credit,rec,";
				CTName[0][0] += "贷方,方向,";
				ColName[0][0] += "贷方本位币,方向,";
				
				ii=1;
				for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) Cmap.get(key);
					sql += "dRemain"+key+",";
					CTName[0][0] += "余额,";
					ColName[0][0] += value+",";
					ii ++;
				}
				sql += "dateRemain";
				CTName[0][0] += "余额";
				ColName[0][0] += "本位币余额";
				
				printSetup.setStrQuerySqls(new String[]{sql + " from ("+pp.getFinishSQLDeleteLimit()+") a where 1=1 "+txtSQL2+"  order by id "+txtSQL1 });
				printSetup.setHeaders(CTName, ColName);
				
				sRows="$2:$5";
				break;
				
			case 2:
				CTName[0][1] = ",";
				ColName[0][1]= ",";
				
				CTName[0][0] = "月,凭证日期,字,号,核算编号,";
				ColName[0][0] = "月,凭证日期,字,号,核算编号,";
				
				if("1".equals(toSubjects)){
					CTName[0][0] += "对方科目,";
					ColName[0][0] += "对方科目,";
				}
				CTName[0][0] += "摘要,";
				ColName[0][0] += "摘要,";
				
				coll = Umap.keySet();
				ii =1;
				for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) Umap.get(key);
					sql += "debitrate"+ii+",debit"+key+",";
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
					sql += "creditrate"+ii+",credit"+key+",";
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
				
				printSetup.setStrQuerySqls(new String[]{sql + " from ("+pp.getFinishSQLDeleteLimit()+") a where 1=1 "+txtSQL2+"  order by id "+txtSQL1 });
				printSetup.setHeaders(CTName, ColName);
				
				sRows="$2:$5";
				break;
				
			case 3:
				CTName[0][1] = ",";
				ColName[0][1]= ",";
				
				CTName[0][0] = "月,凭证日期,字,号,核算编号,";
				ColName[0][0] = "月,凭证日期,字,号,核算编号,";
				if("1".equals(toSubjects)){
					CTName[0][0] += "对方科目,";
					ColName[0][0] += "对方科目,";
				}
				CTName[0][0] += "摘要,";
				ColName[0][0] += "摘要,";
				
				Set Ucoll = Umap.keySet();
				Set Ccoll = Cmap.keySet();
				
				ii =1;
				for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) Umap.get(key);
					sql += "debitPrice"+ii+",debit"+key+",";
					CTName[0][0] += "借方,借方,";
					ColName[0][0] += "单价,"+value+",";
					ii ++;						
				}
				ii =1;
				for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) Cmap.get(key);
					sql += "debitrate"+ii+",debit"+key+",";
					CTName[0][0] += "借方,借方,";
					ColName[0][0] += "汇率,"+value+",";
					ii ++;
				}
				sql += "debit,";
				CTName[0][0] += "借方,";
				ColName[0][0] += "借方本位币,";
				
				ii=1;
				for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) Umap.get(key);
					sql += "creditPrice"+ii+",credit"+key+",";
					CTName[0][0] += "贷方,贷方,";
					ColName[0][0] += "单价,"+value+",";
					ii ++;
				}
				ii=1;
				for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) Cmap.get(key);
					sql += "creditrate"+ii+",credit"+key+",";
					CTName[0][0] += "贷方,贷方,";
					ColName[0][0] += "汇率,"+value+",";
					ii ++;
				}
				sql += "credit,rec,";
				CTName[0][0] += "贷方,方向,";
				ColName[0][0] += "贷方本位币,方向,";
				
				ii=1;
				for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) Umap.get(key);
					sql += "dRemain"+key+",";
					CTName[0][0] += "余额,";
					ColName[0][0] += value+",";
					ii ++;
				}
				ii=1;
				for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					String value = (String) Cmap.get(key);
					sql += "dRemain"+key+",";
					CTName[0][0] += "余额,";
					ColName[0][0] += value+",";
					ii ++;
				}
				sql += "dateRemain";
				CTName[0][0] += "余额";
				ColName[0][0] += "本位币余额";
				
				printSetup.setStrQuerySqls(new String[]{sql + " from ("+pp.getFinishSQLDeleteLimit()+") a where 1=1 "+txtSQL2+"  order by id "+txtSQL1 });
				printSetup.setHeaders(CTName, ColName);
				
				sRows="$2:$5";
				break;	
			}
			

			String filename = printSetup.getExcelFile();
			
			
			//vpage strPrintTitleRows
			mapResult.put("refresh","");
			
			mapResult.put("saveasfilename","核算项目明细账");			
			mapResult.put("bVpage","false");
			mapResult.put("strPrintTitleRows",sRows);
			mapResult.put("filename", filename);	
			
		} catch (Exception e) {
			Debug.print(Debug.iError, "核算项目明细账打印失败！", e);
            throw e;
		} finally {
			DbUtil.close(conn);
		}
		return new ModelAndView("/Excel/tempdata/PrintandSave.jsp",mapResult);
	}
	
	/**
	 * 批量打印
	 */
	
	public ModelAndView create(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		ASFuntion CHF=new ASFuntion();
		HashMap mapResult = new HashMap();
		String T1 =(String)CHF.showNull(request.getParameter("T1"));
		String AccPackageID =(String)CHF.showNull(request.getParameter("AccPackageID"));
		String BeginYear =(String)CHF.showNull(request.getParameter("BeginYear"));
		String BeginDate = (String)CHF.showNull(request.getParameter("BeginDate"));
		String EndYear =(String)CHF.showNull(request.getParameter("EndYear"));
		String EndDate = (String)CHF.showNull(request.getParameter("EndDate"));
		
		if("".equals(AccPackageID.trim())){
			AccPackageID = T1 + EndYear;
		}
		String toSubjects = CHF.showNull(request.getParameter("toSubjects"));//显示对方科目
		String sqlwhere = "";
		if(!toSubjects.equals("1")){
			sqlwhere = " and  1=2 ";
		}
		String toLevel1 = CHF.showNull(request.getParameter("toLevel1"));//对方科目显示的级数
		String subjectnameSql = "";
		if(toLevel1.equals("1")){
			subjectnameSql = "if(locate('/',b.subjectfullname)=0,b.subjectfullname,substr(b.subjectfullname,1,locate('/',b.subjectfullname)-1))";
		}else{
			subjectnameSql = "b.subjectfullname";
		}

		mapResult.put("T1", T1);
		mapResult.put("AccPackageID", AccPackageID);
		mapResult.put("BeginDate", BeginDate);
		mapResult.put("EndDate", EndDate);
		mapResult.put("sqlwhere", sqlwhere);
		mapResult.put("subjectnameSql", subjectnameSql);

		return new ModelAndView("/assitementry/create.jsp",mapResult);
	}
	
	public ModelAndView createprintfilebyAssItemID(HttpServletRequest request, HttpServletResponse response)  throws Exception {
		String strResult = "";
		HashMap mapResult = new HashMap();
		Connection conn=null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ASFuntion CHF=new ASFuntion();
			
			String T1 =(String)CHF.showNull(request.getParameter("T1"));
			String AccPackageID =(String)CHF.showNull(request.getParameter("AccPackageID"));
			String BeginDate = (String)CHF.showNull(request.getParameter("BeginDate"));
			String EndYear = "";
			String EndDate = (String)CHF.showNull(request.getParameter("EndDate"));
			
			String SubjectType = (String)CHF.showNull(request.getParameter("SubjectType"));	//0为1级，1为末级
			String bAssItemID = (String)CHF.showNull(request.getParameter("bAssItemID"));
			String eAssItemID = (String)CHF.showNull(request.getParameter("eAssItemID"));
			String sqlwhere = (String)CHF.showNull(request.getParameter("sqlwhere"));
			String subjectnameSql = (String)CHF.showNull(request.getParameter("subjectnameSql"));
			String TName = "";
			if (TName == null || "".equals(TName)) {
				try{
					if(!"".equals(T1)) {
						conn = new DBConnect().getConnect(T1);
						ASTextKey tkey = new ASTextKey(conn);
						TName = tkey.TextCustomerName(T1);
					}
				}catch (Exception e) {
					e.printStackTrace();
					throw e;
				} finally {
					DbUtil.close(conn);
				}
			} 			
			if(!"".equals(AccPackageID)&&AccPackageID!="")
				EndYear = AccPackageID.substring(6,10);
			String BeginYear = EndYear;
			conn = new DBConnect().getConnect(T1);
			Assitem assitem = new Assitem(conn);
			
			ArrayList args = assitem.getAssItem(T1,EndYear, EndDate, bAssItemID, eAssItemID, SubjectType); 
			ArrayList filenameList = new ArrayList();
			String [] temptable = new String [1000];//暂时就这么大吧O(∩_∩)O
			int i = 0;
			for (Iterator aiter = args.iterator(); aiter.hasNext(); ) {
				try{
					AssitemInfo si = (AssitemInfo)aiter.next();
					String AssItemID =si.assitemid;		//核算ID
					
					String sqlSubject = "select distinct a.subjectid,SubjectFullName \n" 
									  +"from c_accpkgsubject a,( \n"
									  +"select distinct accid from c_assitementryacc \n"
									  +"where AccPackageID='"+AccPackageID+"' and AssItemID like concat('"+AssItemID+"','%')) b \n"
									  +"where a.AccPackageID='"+AccPackageID+"' \n"
									  +"and a.subjectid like b.accid \n"
									  +"ORDER by SubjectID ";
//					System.out.println("sk:查询核算对应科目编号的sql：\n"+sqlSubject);
					ps = conn.prepareStatement(sqlSubject);
					rs = ps.executeQuery();
					String filename ="";
					while (rs.next()) {
						Connection conn1=null;
						try{
							String SubjectID = rs.getString("subjectid");
							String avalue = SubjectID+"("+AssItemID+")";	//科目编号+核算编号
							temptable[i] = new String();
							temptable[i] = "tt_" + DELUnid.getCharUnid();
							conn1 = new DBConnect().getConnect(AccPackageID.substring(0,6));
							
							PrintSetup printSetup = new PrintSetup(conn1);
							printSetup.setStrSheetName(avalue);
							printSetup.setStrTitles(new String[]{new ASTextKey(conn1).TextCustomerName(AccPackageID.substring(0,6))+" 核算明细帐"});
							printSetup.setCharColumn(new String[]{"1`2`3`4`5`6"});
					 
							String full = new Assitem(conn1).getAssTotalName(AccPackageID,AssItemID);
							
							List[] lists = new List[1];
							lists[0] = new ArrayList();  
							lists[0].add(0, "1");
							lists[0].add(1, "核算名称："+full+"　　　　　　年度："+AccPackageID.substring(6));
							lists[0].add(2, "9");
							lists[0].add(3, null);
							
							printSetup.setPoms(lists);
							
							String [][] CTName = new String[1][2];
							String [][] ColName = new String[1][2];
							
							String sql = "select vchmonth,vchdate,typeid,oldvoucherid,assitemid,";
							if("".equals(sqlwhere)){
								sql +="subjectnames,";
							}
							sql += " summary,";
							
							Map Cmap = assitem.AssitemCurrency(AccPackageID, SubjectID, AssItemID);
							Map Umap = assitem.AssitemUnitName(AccPackageID, SubjectID, AssItemID);
							
							int result = assitem.AssitemProperty(AccPackageID, SubjectID, AssItemID);
							switch(result){
							case 0:
								assitem.CreateTable(temptable[i]);
								assitem.DataToTable(temptable[i],"", "", T1,SubjectID, AssItemID, BeginYear, BeginDate,EndYear, EndDate);
								
								if("".equals(sqlwhere)){
									printSetup.setStrChineseTitles(new String[]{"月`凭证日期`字`号`核算编号`对方科目`摘要`借方发生额`贷方发生额`方向`余　额"});
									printSetup.setIColumnWidths(new int[]{4,13,18,5,24,30,72,22,20,4,23});
								}else{
									printSetup.setStrChineseTitles(new String[]{"月`凭证日期`字`号`核算编号`摘要`借方发生额`贷方发生额`方向`余　额"});
									printSetup.setIColumnWidths(new int[]{4,13,18,5,24,72,22,20,4,23});
								}
								printSetup.setStrQuerySqls(new String[]{sql + "debit,credit,if(dateremain<0,'贷','平') rec,dateremain from (select *,group_concat(distinct "+subjectnameSql+") subjectnames   from (select * from `"+temptable[i]+"` order by id limit 0,10000) a left join (select * from c_accpkgsubject where  AccPackageID="+AccPackageID+" "+sqlwhere+" ) b  on 1=1 and a.subjects like concat('%,',b.subjectid,',%')  group by a.id  ) a "});					
								break;
							case 1:	
								assitem.CreateTable(temptable[i],Cmap);
								assitem.DataToTable(Cmap,temptable[i],"", "", T1,SubjectID, AssItemID, BeginYear, BeginDate,EndYear, EndDate,1);
								CTName[0][1] = new String();
								CTName[0][1] = ",";
								ColName[0][1] = new String();
								ColName[0][1]= ",";
								CTName[0][0] = new String();
								ColName[0][0] = new String();
								
								CTName[0][0] = "月,凭证日期,字,号,核算编号,";
								ColName[0][0] = "月,凭证日期,字,号,核算编号,";
								if("".equals(sqlwhere)){
									CTName[0][0] += "对方科目,";
									ColName[0][0] += "对方科目,";
								}
								CTName[0][0] += "摘要,";
								ColName[0][0] += "摘要,";
								
								Set coll = Cmap.keySet();
								int ii =1;
								for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) Cmap.get(key);
									sql += "debitrate"+ii+",debit"+key+",";
									CTName[0][0] += "借方,借方,";
									ColName[0][0] += "汇率,"+value+",";
									ii ++;
								}
								sql += "debit,";
								CTName[0][0] += "借方,";
								ColName[0][0] += "借方本位币,";
								
								ii=1;
								for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) Cmap.get(key);
									sql += "creditrate"+ii+",credit"+key+",";
									CTName[0][0] += "贷方,贷方,";
									ColName[0][0] += "汇率,"+value+",";
									ii ++;
								}
								sql += "credit,if(dateremain<0,'贷','平') rec,";
								CTName[0][0] += "贷方,方向,";
								ColName[0][0] += "贷方本位币,方向,";
								
								ii=1;
								for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) Cmap.get(key);
									sql += "dRemain"+key+",";
									CTName[0][0] += "余额,";
									ColName[0][0] += value+",";
									ii ++;
								}
								sql += "dateRemain";
								CTName[0][0] += "余额";
								ColName[0][0] += "本位币余额";
//								System.out.println("sk:打印sql 1 \n"+sql + " from "+temptable[i]+" a where 1=1   order by id " );
								printSetup.setStrQuerySqls(new String[]{sql + "  from (select *,group_concat(distinct "+subjectnameSql+") subjectnames   from (select * from `"+temptable[i]+"` order by id limit 0,10000) a left join (select * from c_accpkgsubject where  AccPackageID="+AccPackageID+" "+sqlwhere+" ) b  on 1=1 and a.subjects like concat('%,',b.subjectid,',%')  group by a.id  ) a " });
								printSetup.setHeaders(CTName, ColName);

								break;
								
							case 2:
								assitem.CreateTable(temptable[i],Umap);				
								assitem.DataToTable(Umap,temptable[i],"", "", T1,SubjectID, AssItemID, BeginYear, BeginDate,EndYear, EndDate,2);
							
								CTName[0][1] = ",";
								ColName[0][1]= ",";
								
								CTName[0][0] = "月,凭证日期,字,号,核算编号,";
								ColName[0][0] = "月,凭证日期,字,号,核算编号,";
								if("".equals(sqlwhere)){
									CTName[0][0] += "对方科目,";
									ColName[0][0] += "对方科目,";
								}
								CTName[0][0] += "摘要,";
								ColName[0][0] += "摘要,";
								
								coll = Umap.keySet();
								ii =1;
								for (Iterator iter = coll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) Umap.get(key);
									sql += "debitrate"+ii+",debit"+key+",";
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
									sql += "creditrate"+ii+",credit"+key+",";
									CTName[0][0] += "贷方,贷方,";
									ColName[0][0] += "单价,"+value+",";
									ii ++;
								}
								sql += "credit,if(dateremain<0,'贷','平') rec,";
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
//								System.out.println("sk:打印sql 2 \n"+sql + " from "+temptable[i]+" a where 1=1   order by id " );
								printSetup.setStrQuerySqls(new String[]{sql + "  from (select *,group_concat(distinct "+subjectnameSql+") subjectnames   from (select * from `"+temptable[i]+"` order by id limit 0,10000) a left join (select * from c_accpkgsubject where  AccPackageID="+AccPackageID+" "+sqlwhere+" ) b  on 1=1 and a.subjects like concat('%,',b.subjectid,',%')  group by a.id  ) a " });
								printSetup.setHeaders(CTName, ColName);
								
								break;
								
							case 3:
								assitem.CreateTable(temptable[i],Cmap,Umap);
								assitem.DataToTable(Cmap,Umap,temptable[i],"", "", T1,SubjectID, AssItemID, BeginYear, BeginDate,EndYear, EndDate);				
								CTName[0][1] = ",";
								ColName[0][1]= ",";
								
								CTName[0][0] = "月,凭证日期,字,号,核算编号,";
								ColName[0][0] = "月,凭证日期,字,号,核算编号,";
								if("".equals(sqlwhere)){
									CTName[0][0] += "对方科目,";
									ColName[0][0] += "对方科目,";
								}
								CTName[0][0] += "摘要,";
								ColName[0][0] += "摘要,";
								
								Set Ucoll = Umap.keySet();
								Set Ccoll = Cmap.keySet();
								
								ii =1;
								for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) Umap.get(key);
									sql += "debitPrice"+ii+",debit"+key+",";
									CTName[0][0] += "借方,借方,";
									ColName[0][0] += "单价,"+value+",";
									ii ++;						
								}
								ii =1;
								for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) Cmap.get(key);
									sql += "debitrate"+ii+",debit"+key+",";
									CTName[0][0] += "借方,借方,";
									ColName[0][0] += "汇率,"+value+",";
									ii ++;
								}
								sql += "debit,";
								CTName[0][0] += "借方,";
								ColName[0][0] += "借方本位币,";
								
								ii=1;
								for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) Umap.get(key);
									sql += "creditPrice"+ii+",credit"+key+",";
									CTName[0][0] += "贷方,贷方,";
									ColName[0][0] += "单价,"+value+",";
									ii ++;
								}
								ii=1;
								for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) Cmap.get(key);
									sql += "creditrate"+ii+",credit"+key+",";
									CTName[0][0] += "贷方,贷方,";
									ColName[0][0] += "汇率,"+value+",";
									ii ++;
								}
								sql += "credit,if(dateremain<0,'贷','平') rec,";
								CTName[0][0] += "贷方,方向,";
								ColName[0][0] += "贷方本位币,方向,";
								
								ii=1;
								for (Iterator iter = Ucoll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) Umap.get(key);
									sql += "dRemain"+key+",";
									CTName[0][0] += "余额,";
									ColName[0][0] += value+",";
									ii ++;
								}
								ii=1;
								for (Iterator iter = Ccoll.iterator(); iter.hasNext(); ) {
									String key = (String) iter.next();
									String value = (String) Cmap.get(key);
									sql += "dRemain"+key+",";
									CTName[0][0] += "余额,";
									ColName[0][0] += value+",";
									ii ++;
								}
								sql += "dateRemain";
								CTName[0][0] += "余额";
								ColName[0][0] += "本位币余额";
//								System.out.println("sk:打印sql 3 \n"+sql + " from "+temptable[i]+" a where 1=1   order by id " );
								printSetup.setStrQuerySqls(new String[]{sql + "  from (select *,group_concat(distinct "+subjectnameSql+") subjectnames   from (select * from `"+temptable[i]+"` order by id limit 0,10000) a left join (select * from c_accpkgsubject where  AccPackageID="+AccPackageID+" "+sqlwhere+" ) b  on 1=1 and a.subjects like concat('%,',b.subjectid,',%')  group by a.id  ) a " });
								printSetup.setHeaders(CTName, ColName);
								
								break;
							}
							filename = printSetup.getExcelFile();
							filenameList.add(filename);
						}catch(Exception e){
							e.printStackTrace();
							throw e;
						}finally{
							DbUtil.close(conn1);
						}
						i ++;
					}
					
				}catch (Exception e) {
					e.printStackTrace();
					throw e;
				} finally {
					DbUtil.close(rs);
					DbUtil.close(ps);				
				}
				i ++;
			}
			for(int ii=0;ii<temptable.length;ii++){
				String TabName = temptable[ii];
				if(!"".equals(TabName)){
					assitem.DelTempTable(TabName);
				}
			}						
			mapResult.put("bVpage","false");
			mapResult.put("filenameList", filenameList);			
			strResult="/Excel/tempdata/PrintandSave.jsp";
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			DbUtil.close(conn);
		}
		return new ModelAndView(strResult, mapResult);
	}
	
	
}
